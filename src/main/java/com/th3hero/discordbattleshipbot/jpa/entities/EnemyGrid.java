package com.th3hero.discordbattleshipbot.jpa.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
@Table(name = "enemy_grid")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class EnemyGrid implements Serializable {
    @Id
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "game_id", referencedColumnName = "game_id")
    private Game game;

    @Id
    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "player_id")
    private Player player;

    @OneToMany
    private List<EnemyCell> enemyCells;
}
