package com.th3hero.discordbattleshipbot.jpa.entities;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

import com.th3hero.discordbattleshipbot.objects.Placement.Ship;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "enemy_cell")
@IdClass(EnemyCellKey.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class EnemyCell implements Serializable {
    @Id
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
        @JoinColumn(name = "game_id", referencedColumnName = "game_id"),
        @JoinColumn(name = "player_id", referencedColumnName = "player_id")
    })
    private GameBoard gameBoard;

    @Id
    @Column
    private Integer cellIndex;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column
    private CellStatus cellStatus = CellStatus.EMPTY;

    @Enumerated(EnumType.STRING)
    @Column
    private Ship shipType;

    public enum CellStatus {
        EMPTY,
        HIT,
        MISS
    }
}

@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
class EnemyCellKey implements Serializable {
    private GameBoard gameBoard;
    private Integer cellIndex;
}