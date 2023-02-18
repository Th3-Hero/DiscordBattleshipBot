package com.th3hero.discordbattleshipbot.services;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.jpa.entities.Player;
import com.th3hero.discordbattleshipbot.repositories.PlayerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerHandlerService {
    private final PlayerRepository playerRepository;

    /**
     * Attempts to fetch {@code Player}, if non is found one will be created.
     * @param playerId -Id of the player(User)
     * @return Never Null {@code Player}
     */
    public Player fetchPlayer(String playerId){
        return playerRepository.findById(playerId)
            .orElse(playerRepository.save(Player.create(playerId)));
    }
}