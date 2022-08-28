package com.th3hero.discordbattleshipbot.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.th3hero.discordbattleshipbot.jpa.entities.Player;
import com.th3hero.discordbattleshipbot.repositories.PlayerRepository;

/**
 * PlayerHandlerServiceTest
 */
@ExtendWith(MockitoExtension.class)
class PlayerHandlerServiceTest {
    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerHandlerService playerHandlerService;

    @Test
    void fetchPlayer_existingPlayer() {
        String playerId = "1234";
        Player realPlayer = Player.create(playerId);
        when(playerRepository.existsById(playerId))
            .thenReturn(true);
        when(playerRepository.findById(playerId))
            .thenReturn(Optional.of(realPlayer));

        Player player = playerHandlerService.fetchPlayer(playerId);
        Assertions.assertThat(player).isEqualTo(realPlayer);
    }

    @Test
    void fetchPlayer_missingPlayer() {
        String playerId = "1234";
        when(playerRepository.existsById(playerId))
            .thenReturn(false);
        when(playerRepository.save(any(Player.class)))
            .thenAnswer(AdditionalAnswers.returnsFirstArg());

        Player player = playerHandlerService.fetchPlayer(playerId);
        Assertions.assertThat(player.getPlayerId()).isEqualTo(playerId);
        verify(playerRepository,never()).findById(any());
    }
}