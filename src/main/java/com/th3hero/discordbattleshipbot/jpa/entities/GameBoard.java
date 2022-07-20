package com.th3hero.discordbattleshipbot.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
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
@Table(name = "game_board")
@IdClass(GameBoardKey.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GameBoard implements Serializable {
    @Id
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "game_id", referencedColumnName = "game_id")
    private Game game;

    @Id
    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "player_id")
    private Player player;

    @NotNull
    @OneToOne(mappedBy = "gameBoard")
    @Column
    private FriendlyGrid friendlyGrid;

    @NotNull
    @OneToOne(mappedBy = "gameBoard")
    @Column
    private EnemyGrid enemyGrid;

}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class GameBoardKey implements Serializable {
    private Game game;
    private Player player;
}