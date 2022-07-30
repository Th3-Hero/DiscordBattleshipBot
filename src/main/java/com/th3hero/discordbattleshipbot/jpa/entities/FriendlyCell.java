package com.th3hero.discordbattleshipbot.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "friendly_cell")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendlyCell implements Serializable{
    @Id
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "game_id", referencedColumnName = "game_id")
    private Game game;

    @Id
    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "player_id")
    private Player player;

    @Id
    @ManyToOne
    @JoinColumn(name = "cell_index", referencedColumnName = "cell_index")
    private Integer cellIndex;


    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column
    private CellStatus cell = CellStatus.EMPTY;

    public enum CellStatus {
        EMPTY,
        SHIP,
        SHIPHIT,
        MISS
    }
}

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
class FriendlyCellKey implements Serializable {
    private FriendlyGrid friendlyGrid;
    private Integer index;
}