package com.th3hero.discordbattleshipbot.services;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.jpa.entities.Player;
import com.th3hero.discordbattleshipbot.repositories.PlayerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerHandlerService {
    private final PlayerRepository playerRepository;
    
    public Player fetchPlayer(String playerId){
        if (playerRepository.existsById(playerId)) {
            return playerRepository.findById(playerId).get();
        }
        else {
            return playerRepository.save(Player.create(playerId, 0, 0, 0, 0, 0, 0));
        }
    }
}