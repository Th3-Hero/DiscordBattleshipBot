package com.th3hero.discordbattleshipbot.services;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.repositories.GameRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameHandlerService {
    private final GameRepository gameRepository;

    public Game fetchGame(String gameId){
        if (gameRepository.existsById(gameId)) {
            return gameRepository.findById(gameId).get();
        }
        else {
            return null;
        }
    }

    public Game createGame(String player1, String player2){
        return gameRepository.save(Game.create(player1, player2, null));
    }
}