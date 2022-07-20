package com.th3hero.discordbattleshipbot.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
@Table(name = "enemy_cell")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class EnemyCell implements Serializable{
    @Id
    @OneToOne
    private EnemyGrid enemyGrid;

    @Id
    private Integer index;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column
    private CellStatus cell = CellStatus.EMPTY;

    public enum CellStatus {
        EMPTY,
        HIT,
        MISS
    }
}

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
class EnemyCellKey implements Serializable {
    private EnemyGrid enemyGrid;
    private Integer index;
}