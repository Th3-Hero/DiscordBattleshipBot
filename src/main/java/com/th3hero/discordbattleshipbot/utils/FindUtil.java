package com.th3hero.discordbattleshipbot.utils;

import java.util.Collection;
import java.util.function.Predicate;

import javax.persistence.EntityNotFoundException;

import com.th3hero.discordbattleshipbot.jpa.entities.EnemyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.FriendlyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.jpa.entities.GameBoard;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Useful methods for finding an object by field
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindUtil {
    private static <T> T findByField(Collection<T> collection, Predicate<T> filter) {
        return collection.stream().filter(filter).findFirst().orElse(null);
    }

    /**
     * Searches a list of friendly cells by field {@code cellIndex}
     * @param cellList
     * @param index
     * @return <pre><code>FriendlyCell</code></pre>
     */
    public static FriendlyCell findFriendlyCellByIndex(Collection<FriendlyCell> cellList, Integer index) {
        return findByField(cellList, friendlyCell -> index.equals(friendlyCell.getCellIndex()));
    }

    /**
     * Searches a list of enemy cells by field {@code cellIndex}
     * @param cellList
     * @param index
     * @return <pre><code>EnemyCell</code></pre>
     */
    public static EnemyCell findEnemyCellByIndex(Collection<EnemyCell> cellList, Integer index) {
        return findByField(cellList, enemyCell -> index.equals(enemyCell.getCellIndex()));
    }

    public static GameBoard findGameboardByPlayerId(Game game, String playerId) {
        return game.getGameBoards().stream()
            .filter(board -> board.getPlayer().getPlayerId().equals(playerId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Gameboard could not be found"));
    }
}
