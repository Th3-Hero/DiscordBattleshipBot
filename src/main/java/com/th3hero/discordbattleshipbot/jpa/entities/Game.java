package com.th3hero.discordbattleshipbot.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "game")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Game implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_game_id_generator")
    @SequenceGenerator(name = "seq_game_id_generator", sequenceName = "seq_game_id", allocationSize = 1)
    @Column(name = "game_id")
    private String gameId;

    @NotNull
    @Column
    private String player1;

    @NotNull
    @Column
    private String player2;

    @Column
    private Boolean gameActive;

    public static Game create(
        final String player1,
        final String player2,
        final Boolean gameActive
    ) {
        return Game.builder()
            .player1(player1)
            .player2(player2)
            .gameActive(gameActive)
            .build();
    }
}