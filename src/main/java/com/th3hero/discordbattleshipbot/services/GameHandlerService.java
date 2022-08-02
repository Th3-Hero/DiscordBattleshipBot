package com.th3hero.discordbattleshipbot.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.jpa.entities.EnemyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.FriendlyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.jpa.entities.GameBoard;
import com.th3hero.discordbattleshipbot.jpa.entities.Player;
import com.th3hero.discordbattleshipbot.repositories.GameBoardRepository;
import com.th3hero.discordbattleshipbot.repositories.GameRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameHandlerService {
    private final GameRepository gameRepository;
    private final GameBoardRepository gameBoardRepository;

    public Game fetchGame(int gameId){
        if (gameRepository.existsById(gameId)) {
            return gameRepository.findById(gameId).get();
        }
        else {
            return null;
        }
    }

    public Game createGame(String player1, String player2){
        return gameRepository.save(Game.create(player1, player2));
    }

    public GameBoard createBoard(Game game, Player player){

        // GameBoard board = GameBoard.create(game, player);
        GameBoard board = GameBoard.builder()
            .player(player)
            .game(game)
            .build();

        // board.setGame(game);
        // board.setPlayer(player);
        board.setFriendlyCells(createFriendlyCells(board));
        board.setEnemyCells(createEnemyCells(board));
        log.info("Here1");

        return gameBoardRepository.save(board);
    }

    private List<FriendlyCell> createFriendlyCells(GameBoard gameBoard) {
        List<FriendlyCell> cells = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            FriendlyCell cell = FriendlyCell.builder()
                .gameBoard(gameBoard)
                .cellIndex(i)
                .build();
            cells.add(cell);
        }
        return cells;
    }

    private List<EnemyCell> createEnemyCells(GameBoard gameBoard) {
        List<EnemyCell> cells = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            EnemyCell cell = EnemyCell.builder()
                .gameBoard(gameBoard)
                .cellIndex(i)
                .build();
            cells.add(cell);
        }
        return cells;
    }
}