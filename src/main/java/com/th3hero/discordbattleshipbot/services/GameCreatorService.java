package com.th3hero.discordbattleshipbot.services;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.EnumSet;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.enums.ChannelPermissions;
import com.th3hero.discordbattleshipbot.exceptions.InaccessibleMemberException;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.jpa.entities.GameBoard;
import com.th3hero.discordbattleshipbot.jpa.entities.Player;
import com.th3hero.discordbattleshipbot.jpa.entities.Game.GameStatus;
import com.th3hero.discordbattleshipbot.objects.ButtonRequest;
import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.repositories.GameRepository;
import com.th3hero.discordbattleshipbot.utils.AuthorizedAction;
import com.th3hero.discordbattleshipbot.utils.EmbedBuilderFactory;
import com.th3hero.discordbattleshipbot.utils.Utils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GameCreatorService {
    private final PlayerHandlerService playerHandlerService;
    private final GameHandlerService gameHandlerService;
    private final GameRepository gameRepository;
    private final ShipPlacementService shipPlacementService;
    private final AuthorizedAction authorizedAction;

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

        request.getChannel().sendMessageEmbeds(EmbedBuilderFactory.challengeRequestBuilder(Utils.playerNames(request.getServer(), game), gameId)).setActionRow(
            Button.success(gameId + "-ACCEPT", "Accept"),
            Button.danger(gameId + "-DECLINE", "Decline")
        ).queue();
    }

    /**
     * Accept the challenge, update embed and setup the game.
     * @param request
     */
    public void acceptGame(ButtonRequest request) {
        Game game = gameHandlerService.fetchGameById(request.getActionId());

        // Only player2 can accept
        if (!authorizedAction.permittedAction(request, game.getPlayerTwo())) {
            return;
        }

        Guild server = request.getServer();
        String gameId = game.getGameId().toString();

        // Update challenge embed
        MessageEmbed acceptedEmbed = EmbedBuilderFactory.acceptGameEmbed(Utils.playerNames(server, game), gameId);
        request.getEvent().editMessageEmbeds(acceptedEmbed)
            .setActionRows() // Strip Buttons
            .queue();

        startGame(game, server, server.getGuildChannelById(ChannelType.TEXT, request.getChannel().getId()));
    }

    /**
     * Decline challenge, update embed and clean game from DB.
     * @param request
     */
    public void declineGame(ButtonRequest request) {
        Game game = gameHandlerService.fetchGameById(request.getActionId());

        // Only player2 can decline
        if (!authorizedAction.permittedAction(request, game.getPlayerTwo())) {
            return;
        }

        Guild server = request.getServer();
        String gameId = game.getGameId().toString();

        // Update challenge embed
        MessageEmbed declinedEmbed = EmbedBuilderFactory.declineGameEmbed(Utils.playerNames(server, game), gameId);
        request.getEvent().editMessageEmbeds(declinedEmbed)
            .setActionRows() // Strip Buttons
            .queue();

        // Delete game from DB, it's no longer needed
        gameRepository.delete(game);
    }

    public void startGame(Game game, Guild server, GuildChannel channel) {
        game.setGameStatus(GameStatus.WAITING_START); // Update game status
        game.setCurrentTurn(game.getPlayerTwo());
        Player playerOne = playerHandlerService.fetchPlayer(game.getPlayerOne());
        Player playerTwo = playerHandlerService.fetchPlayer(game.getPlayerTwo());

        // Create and populate game boards
        List<GameBoard> gameBoards = game.getGameBoards();

        // playerOne setup
        Member memberOneById = server.getMemberById(game.getPlayerOne());
        if (memberOneById == null) {
            throw new InaccessibleMemberException("Failed to retrieve Member");
        }
        User userOne = memberOneById.getUser();
        String playerOneName = memberOneById.getEffectiveName();
        server.createTextChannel(playerOneName + "-Battleship-" + game.getGameId()) // Create channel for playerOne
        .addMemberPermissionOverride(userOne.getIdLong(), ChannelPermissions.allow(), ChannelPermissions.deny())
        .addPermissionOverride(server.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
        .setParent(channel.getParent())
        .queue(success -> {
            // Use callback to finish setting board then save
            GameBoard boardOne = gameHandlerService.createBoard(game, playerOne, success.getId());
            List<MessageEmbed> boardOneEmbed = shipPlacementService.shipPlacementCreation(server, game, boardOne);
            gameBoards.add(boardOne);
            gameRepository.save(game);
            success.sendMessageEmbeds(boardOneEmbed)
                .setActionRow(
                    Button.secondary(game.getGameId() + "-RANDOMIZE", "🎲 Randomize 🎲"),
                    Button.success(game.getGameId() + "-READY", "Ready Up")
                )
                .queue();
        });

        // playerTwo setup
        Member memberTwoById = server.getMemberById(game.getPlayerTwo());
        if (memberTwoById == null) {
            throw new InaccessibleMemberException("Failed to retrieve Member");
        }
        User userTwo = memberTwoById.getUser();
        String playerTwoName = memberTwoById.getEffectiveName();
        server.createTextChannel(playerTwoName + "-Battleship-" + game.getGameId()) // Create channel for playerTwo
        .addMemberPermissionOverride(userTwo.getIdLong(), ChannelPermissions.allow(), ChannelPermissions.deny())
        .addPermissionOverride(server.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
        .setParent(channel.getParent())
        .queue(success -> {
            // Use callback to finish setting board then save
            GameBoard boardTwo = gameHandlerService.createBoard(game, playerTwo, success.getId());
            List<MessageEmbed> boardTwoEmbed = shipPlacementService.shipPlacementCreation(server, game, boardTwo);
            gameBoards.add(boardTwo);
            gameRepository.save(game);
            success.sendMessageEmbeds(boardTwoEmbed)
                .setActionRow(
                    Button.secondary(game.getGameId() + "-RANDOMIZE", "🎲 Randomize 🎲"),
                    Button.success(game.getGameId() + "-READY", "Ready Up")
                )
                .queue();
        });
    }

}