package com.th3hero.discordbattleshipbot.jpa.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    private Integer gameId;

    @NotNull
    @Column
    private String playerOne;

    @NotNull
    @Column
    private String playerTwo;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameBoard> gameBoards;

    /**
     * Current status of game
     * <ul>
     *  <li>CHALLENGE - Awaiting challenged user to accept game</li>
     *  <li>ACTIVE - Game has been started</li>
     *  <li>ENDED - Game is over</li>
     * </ul>
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column
    private GameStatus gameStatus = GameStatus.CHALLENGE;

    public static Game create(
        final String playerOne,
        final String playerTwo
    ) {
        return Game.builder()
            .playerOne(playerOne)
            .playerTwo(playerTwo)
            .build();
    }

    public enum GameStatus {
        CHALLENGE,
        ACTIVE,
        ENDED
    }
}
