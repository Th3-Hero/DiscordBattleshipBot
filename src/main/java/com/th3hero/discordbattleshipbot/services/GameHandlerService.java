package com.th3hero.discordbattleshipbot.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.exceptions.InaccessibleChannelException;
import com.th3hero.discordbattleshipbot.jpa.entities.EnemyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.FriendlyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.jpa.entities.GameBoard;
import com.th3hero.discordbattleshipbot.jpa.entities.Player;
import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.repositories.GameRepository;
import com.th3hero.discordbattleshipbot.utils.FindUtil;
import com.th3hero.discordbattleshipbot.utils.Utils;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

@Service
@RequiredArgsConstructor
public class GameHandlerService {
    private final GameRepository gameRepository;
    // TODO: redo javdoc for these

    /**
     * Attempts to fetch {@code Game}, if non exist returns {@code null}
     * <p>Since playerIDs are needed to create {@code Game}, {@code fetchGame} is not guaranteed to return {@code Game}</p>
     * @param gameId
     * @return {@code Game} or {@code null}
     * @see <pre><code>GameHandlerService.createGame</code></pre>
     */
    public Game fetchGameById(int gameId){
        return gameRepository.findById(gameId)
            .orElse(null);
    }

    /**
     * Attempts to fetch {@code Game} by using {@code channelId} from one of the {@code GameBoard}s
     * @param channelId
     * @return {@code Game} or {@code null}
     */
    public Game fetchGameByChannelId(String channelId) {
        return gameRepository.findGameByGameBoardWithChannelId(channelId)
            .orElse(null);
    }

    /**
     * Creates a new game
     * @param playerOneId -ID of playerOne
     * @param playerTwoId -ID of playerTwo
     * @return Never null {@code Game}
     */
    public Game createGame(String playerOneId, String playerTwoId){
        return gameRepository.save(Game.create(playerOneId, playerTwoId));
    }

    /**
     * Creates a {@code GameBoard} instance
     * <h2>Important:</h2>
     * The board returned is NOT assigned to {@code Game} or saved to the DB
     * @param game
     * @param player
     * @return {@code GameBoard}
     */
    public GameBoard createBoard(Game game, Player player, String channelId){

        GameBoard board = GameBoard.create(game, player, channelId);

        board.setFriendlyCells(createFriendlyCells(board));
        board.setEnemyCells(createEnemyCells(board));

        return board;
    }

    /**
     * Deletes {@code Game} and all children from the Database.
     * <h3>This action can NOT be reversed!</h3>
     * @param game to be deleted
     */
    public void deleteGame(Game game){
        gameRepository.delete(game);
    }

    /**
     * Deletes {@code Game} and all children from the Database.
     * Deletes channels associated with the {@code GameBoard}s
     * <h3>This action can NOT be reversed!</h3>
     * @param request
     */
    public void deleteGame(CommandRequest request){
        Guild server = request.getServer();
        Game game = fetchGameByChannelId(request.getChannel().getId());

        if (game == null) {
            return;
        }
        List<GameBoard> boards = game.getGameBoards();

        for (GameBoard gameBoard : boards) {
            TextChannel textChannelById = server.getTextChannelById(gameBoard.getChannelId());
            if (textChannelById == null) {
                throw new InaccessibleChannelException("Failed to retrieve TextChannel");
            }
            textChannelById.delete().queue();
        }
        gameRepository.delete(game);
    }

    public void deleteGame(String channelId, Guild server){
        Game game = fetchGameByChannelId(channelId);

        if (game == null) {
            return;
        }
        List<GameBoard> boards = game.getGameBoards();

        for (GameBoard gameBoard : boards) {
            TextChannel textChannelById = server.getTextChannelById(gameBoard.getChannelId());
            if (textChannelById == null) {
                throw new InaccessibleChannelException("Failed to retrieve TextChannel");
            }
            textChannelById.delete().queue();
        }
        gameRepository.delete(game);
    }

    // private <T> List<T> createCells(GameBoard gameBoard, Class<T> cellType) {
    //     List<T> cells = new ArrayList<>();
    //     for (int i = 0; i < 100; i++) {
    //         Cel cell = cellType.builder()
    //             .gameBoard(gameBoard)
    //             .cellIndex(i)
    //             .build();
    //         cells.add(cell);
    //     }
    //     return cells;
    // }

    // TODO: can these be more generic?
    /**
     * Helper to populate friendly grid on {@code GameBoard}
     * @param gameBoard
     * @return {@code List<FriendlyCell>}
     */
    private List<FriendlyCell> createFriendlyCells(GameBoard gameBoard) {
        List<FriendlyCell> cells = new ArrayList<>();
        for (int i = 0; i < Utils.MAX_INCLUSIVE_CELLS; i++) {
            FriendlyCell cell = FriendlyCell.builder()
                .gameBoard(gameBoard)
                .cellIndex(i)
                .build();
            cells.add(cell);
        }
        return cells;
    }

    /**
     * Helper to populate enemy grid on {@code GameBoard}
     * @param gameBoard
     * @return {@code List<EnemyCell>}
     */
    private List<EnemyCell> createEnemyCells(GameBoard gameBoard) {
        List<EnemyCell> cells = new ArrayList<>();
        for (int i = 0; i < Utils.MAX_INCLUSIVE_CELLS; i++) {
            EnemyCell cell = EnemyCell.builder()
                .gameBoard(gameBoard)
                .cellIndex(i)
                .build();
            cells.add(cell);
        }
        return cells;
    }
}