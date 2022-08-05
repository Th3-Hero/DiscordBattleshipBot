package com.th3hero.discordbattleshipbot.services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.Color;
import java.util.EnumSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.enums.ChannelPermissions;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.jpa.entities.GameBoard;
import com.th3hero.discordbattleshipbot.jpa.entities.Player;
import com.th3hero.discordbattleshipbot.jpa.entities.Game.GameStatus;
import com.th3hero.discordbattleshipbot.objects.ButtonRequest;
import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.repositories.GameRepository;
import com.th3hero.discordbattleshipbot.utils.AuthorizedAction;
import com.th3hero.discordbattleshipbot.utils.StringUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameCreatorService {
    private final PlayerHandlerService playerHandlerService;
    private final GameHandlerService gameHandlerService;
    private final GameRepository gameRepository;

    /**
     * Creates a game(DB) and a embed(interactive) in discord
     * @param request
     */
    public void gameRequest(CommandRequest request) {

        // Get users from request
        User user1 = request.getRequester();
        List<User> mentionedUser = request.getMentionedUsers();
        User user2 = mentionedUser.get(0);

        // Get ids of each user involved in the game
        String playerOneId = playerHandlerService.fetchPlayer(user1.getId()).getPlayerId();
        String playerTwoId = playerHandlerService.fetchPlayer(user2.getId()).getPlayerId();

        // Create game and fetch gameId
        Game game = gameHandlerService.createGame(playerOneId, playerTwoId);
        String gameId = game.getGameId().toString();

        // Fetch effective player names from playerId stored in game
        Guild server = request.getServer();
        String playerOneName = server.getMember(user1).getEffectiveName();
        String playerTwoName = server.getMember(user2).getEffectiveName();

        request.getChannel().sendMessageEmbeds(challengeRequestBuilder(playerOneName, playerTwoName, gameId)).setActionRow(
            Button.success(gameId + "-ACCEPT", "Accept"),
            Button.danger(gameId + "-DECLINE", "Decline")
        ).queue();
    }

    /**
     * Accept the challenge, update embed and setup the game.
     * @param request
     */
    public void acceptGame(ButtonRequest request) {
        Game game = gameHandlerService.fetchGame(request.getActionId());

        // Only player2 can accept
        // ! DISABLE DEVMODE WHEN DONE TESTING
        if (!AuthorizedAction.permittedAction(request, game.getPlayerTwo(), false)) {
            return;
        }

        Guild server = request.getServer();
        // Grab needed info
        String gameId = game.getGameId().toString();
        String playerOneName = server.getMemberById(game.getPlayerOne()).getEffectiveName();
        String playerTwoName = server.getMemberById(game.getPlayerTwo()).getEffectiveName();

        // Update challenge embed
        MessageEmbed acceptedEmbed = acceptGameEmbed(playerOneName, playerTwoName, gameId);
            request.getMessage().editMessageEmbeds(acceptedEmbed)
                .setActionRows() // Strip Buttons
                .queue();

        startGame(game, server, server.getGuildChannelById(ChannelType.TEXT, request.getChannel().getId()));
    }

    /**
     * Decline challenge, update embed and clean game from DB.
     * @param request
     */
    public void declineGame(ButtonRequest request) {
        Game game = gameHandlerService.fetchGame(request.getActionId());

        // Only player2 can decline
        // ! DISABLE DEVMODE WHEN DONE TESTING
        if (!AuthorizedAction.permittedAction(request, game.getPlayerTwo(), false)) {
            return;
        }

        Guild server = request.getServer();
        String gameId = game.getGameId().toString();
        String playerOneName = server.getMemberById(game.getPlayerOne()).getEffectiveName();
        String playerTwoName = server.getMemberById(game.getPlayerTwo()).getEffectiveName();

        // Update challenge embed
        MessageEmbed declinedEmbed = declineGameEmbed(playerOneName, playerTwoName, gameId);
            request.getMessage().editMessageEmbeds(declinedEmbed)
            .setActionRows() // Strip Buttons
            .queue();
        
        // Delete game from DB, it's no longer needed
        gameRepository.delete(game);
    }

    public void startGame(Game game, Guild server, GuildChannel channel) {
        game.setGameStatus(GameStatus.ACTIVE); // Update game status
        Player playerOne = playerHandlerService.fetchPlayer(game.getPlayerOne());
        Player playerTwo = playerHandlerService.fetchPlayer(game.getPlayerTwo());

        // Create and populate game boards
        List<GameBoard> gameBoards = game.getGameBoards();
        GameBoard boardOne = gameHandlerService.createBoard(game, playerOne);
        GameBoard boardTwo = gameHandlerService.createBoard(game, playerTwo);

        // playerOne setup
        User user1 = server.getMemberById(game.getPlayerOne()).getUser();
        String playerOneName = server.getMemberById(game.getPlayerOne()).getEffectiveName();
        server.createTextChannel(playerOneName + "-Battleship-" + game.getGameId()) // Create channel for playerOne
        .addMemberPermissionOverride(user1.getIdLong(), ChannelPermissions.allow(), ChannelPermissions.deny())
        .addPermissionOverride(server.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
        .setParent(channel.getParent())
        .queue(success -> {
            // Use callback to finish setting board then save
            boardOne.setChannelId(success.getId());
            gameBoards.add(boardOne);
            game.setGameBoards(gameBoards);
            gameRepository.save(game);
        });

        // playerTwo setup
        User user2 = server.getMemberById(game.getPlayerTwo()).getUser();
        String playerTwoName = server.getMemberById(game.getPlayerTwo()).getEffectiveName();
        server.createTextChannel(playerTwoName + "-Battleship-" + game.getGameId()) // Create channel for playerTwo
        .addMemberPermissionOverride(user2.getIdLong(), ChannelPermissions.allow(), ChannelPermissions.deny())
        .addPermissionOverride(server.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
        .setParent(channel.getParent())
        .queue(success -> {
            // Use callback to finish setting board then save
            boardTwo.setChannelId(success.getId());
            gameBoards.add(boardTwo);
            game.setGameBoards(gameBoards);
            gameRepository.save(game);
        });
    }

    
    static String embedTitle = "Battleship";
    static String gameEmbed = "GameID #";
    /**
     * Generates embed for a game that has been accepted.
     * @param playerOneName -PlayerOne's effective name
     * @param playerTwoName -PlayerTwo's effective name
     * @param gameId -Game ID
     * @return <pre><code>MessageEmbed</code></pre>
     */
    public static MessageEmbed acceptGameEmbed(String playerOneName, String playerTwoName, String gameId) {
        return new EmbedBuilder()
        .setColor(new Color(0, 255, 0))
        .setTitle(embedTitle)
        .addField(
            playerOneName + " vs " + playerTwoName,
            "PUT STATS REEEEE",
            false)
        .addField(
            "",
            gameEmbed + StringUtil.toBold(gameId),
            false
        )
        .build();
    }

    /**
     * Generates embed for a game that has been declined.
     * @param playerOneName -PlayerOne's effective name
     * @param playerTwoName -PlayerTwo's effective name
     * @param gameId -Game ID
     * @return <pre><code>MessageEmbed</code></pre>
     */
    public static MessageEmbed declineGameEmbed(String playerOneName, String playerTwoName, String gameId) {
        return new EmbedBuilder()
        .setColor(new Color(255, 8, 0))
        .setTitle(embedTitle)
        .addField(
            playerOneName + " vs " + playerTwoName,
            StringUtil.toBold(playerTwoName) + " declined the match.",
            false
        )
        .addField(
            "",
            gameEmbed + StringUtil.toBold(gameId),
            false
        )
        .build();
    }

    /**
     * Generates embed for a new challenge.
     * @param playerOneName -PlayerOne's effective name
     * @param playerTwoName -PlayerTwo's effective name
     * @param gameId -Game ID
     * @return <pre><code>MessageEmbed</code></pre>
     */
    public static MessageEmbed challengeRequestBuilder(String playerOneName, String playerTwoName, String gameId) {
        return new EmbedBuilder()
        .setColor(new Color(3, 123, 252))
        .setTitle(embedTitle)
        .addField(
            playerOneName + " vs " + playerTwoName,
            "Stats",
            false)
        .addField(
            "",
            StringUtil.toBold(playerTwoName) + " do you choose to accept?",
            false
        )
        .addField(
            "",
            gameEmbed + StringUtil.toBold(gameId),
            false
        )
        .build();
    }
}