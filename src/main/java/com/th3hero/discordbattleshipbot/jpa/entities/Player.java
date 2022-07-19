package com.th3hero.discordbattleshipbot.jpa.entities;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "player")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Player implements Serializable {
    @Id
    @NotNull
    @Column(name = "player_id")
    private String playerId;

    // Player Stats
    @NotNull
    @Column
    private int wins;
    @NotNull
    @Column
    private int losses;
    @NotNull
    @Column
    private int hits;
    @NotNull
    @Column
    private int misses;
    @NotNull
    @Column
    private int shipsSunk;
    @NotNull
    @Column
    private int shipsLost;

    public static Player create(
        final String playerId,
        final int wins,
        final int losses,
        final int hits,
        final int misses,
        final int shipsSunk,
        final int shipsLost
    ) {
        return Player.builder()
            .playerId(playerId)
            .wins(wins)
            .losses(losses)
            .hits(hits)
            .misses(misses)
            .shipsSunk(shipsSunk)
            .shipsLost(shipsLost)
            .build();
    }
}