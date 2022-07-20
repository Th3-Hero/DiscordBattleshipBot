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
    @Builder.Default
    @Column
    private Integer wins = 0;
    @NotNull
    @Builder.Default
    @Column
    private Integer losses = 0;
    @NotNull
    @Builder.Default
    @Column
    private Integer hits = 0;
    @NotNull
    @Builder.Default
    @Column
    private Integer misses = 0;
    @NotNull
    @Builder.Default
    @Column
    private Integer shipsSunk = 0;
    @NotNull
    @Builder.Default
    @Column
    private Integer shipsLost = 0;

    public static Player create(
        final String playerId
    ) {
        return Player.builder()
            .playerId(playerId)
            .build();
    }
}