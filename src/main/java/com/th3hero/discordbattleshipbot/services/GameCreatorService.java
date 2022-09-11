package com.th3hero.discordbattleshipbot.services;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.enums.ChannelPermissions;
import com.th3hero.discordbattleshipbot.jpa.entities.EnemyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.FriendlyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.jpa.entities.GameBoard;
import com.th3hero.discordbattleshipbot.jpa.entities.Player;
import com.th3hero.discordbattleshipbot.jpa.entities.Game.GameStatus;
import com.th3hero.discordbattleshipbot.objects.ButtonRequest;
import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.repositories.GameRepository;
import com.th3hero.discordbattleshipbot.utils.AuthorizedAction;
import com.th3hero.discordbattleshipbot.utils.EmbedBuilderFactory;
import com.th3hero.discordbattleshipbot.utils.FindUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
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

        request.getChannel().sendMessageEmbeds(EmbedBuilderFactory.challengeRequestBuilder(playerOneName, playerTwoName, gameId)).setActionRow(
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
        if (!AuthorizedAction.permittedAction(request, game.getPlayerTwo(), true)) {
            return;
        }

        Guild server = request.getServer();
        // Grab needed info
        String gameId = game.getGameId().toString();
        String playerOneName = server.getMemberById(game.getPlayerOne()).getEffectiveName();
        String playerTwoName = server.getMemberById(game.getPlayerTwo()).getEffectiveName();

        // Update challenge embed
        MessageEmbed acceptedEmbed = EmbedBuilderFactory.acceptGameEmbed(playerOneName, playerTwoName, gameId);
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
        if (!AuthorizedAction.permittedAction(request, game.getPlayerTwo(), true)) {
            return;
        }

        Guild server = request.getServer();
        String gameId = game.getGameId().toString();
        String playerOneName = server.getMemberById(game.getPlayerOne()).getEffectiveName();
        String playerTwoName = server.getMemberById(game.getPlayerTwo()).getEffectiveName();

        // Update challenge embed
        MessageEmbed declinedEmbed = EmbedBuilderFactory.declineGameEmbed(playerOneName, playerTwoName, gameId);
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
            displayCellsToUnicodeGrid(server, success.getId(), boardOne);
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
            displayCellsToUnicodeGrid(server, success.getId(), boardTwo);
        });

    }


    public void displayCellsToUnicodeGrid(Guild server, String channelId, GameBoard board) {
        List<String> columnList = Arrays.asList("🇦", "🇧", "🇨", "🇩","🇪","🇫","🇬","🇭","🇮","🇯");
        List<String> rowsList = Arrays.asList("🟦", "0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣");
        StringBuilder columns = new StringBuilder();
        rowsList.forEach(columns::append);
        List<FriendlyCell> friendlyCellList = board.getFriendlyCells();
        List<EnemyCell> enemyCellList = board.getEnemyCells();
        StringBuilder friendlyCellGrid = new StringBuilder();
        StringBuilder enemyCellGrid = new StringBuilder();

        friendlyCellGrid.append(columns);
        enemyCellGrid.append(columns);
        for (int i = 0; i < 100; i++) {
            if (i % 10 == 0) {
                friendlyCellGrid.append("\n");
                enemyCellGrid.append("\n");
                friendlyCellGrid.append(columnList.get(i/10));
                enemyCellGrid.append(columnList.get(i/10));
            }

            switch (friendlyCellList.get(i).getCellStatus()) {
                case EMPTY -> friendlyCellGrid.append("🟦");
                case SHIP -> friendlyCellGrid.append("⬛");
                case HIT -> friendlyCellGrid.append("❌");
                case MISS -> friendlyCellGrid.append("❕");
            }

            switch (enemyCellList.get(i).getCellStatus()) {
                case EMPTY -> enemyCellGrid.append("🟦");
                case HIT -> enemyCellGrid.append("❌");
                case MISS -> enemyCellGrid.append("❕");
            }
        }

        List<MessageEmbed> boardsEmbed = EmbedBuilderFactory.boardDisplay(friendlyCellGrid.toString(), enemyCellGrid.toString());
        server.getTextChannelById(channelId)
            .sendMessageEmbeds(boardsEmbed.get(0), boardsEmbed.get(1))
            .queue();
    }
}