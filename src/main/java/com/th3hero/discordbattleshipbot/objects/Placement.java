package com.th3hero.discordbattleshipbot.objects;

import java.security.SecureRandom;

import com.th3hero.discordbattleshipbot.utils.Utils;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class Placement {
    private Integer cellIndex;
    private Direction direction;
    private Ship shipType;
    private int shipSize;

    public static Placement create(Integer cellIndex, Ship ship, Direction direction) {
        return location(cellIndex, ship, direction);
    }

    public static Placement createRandom(Ship ship) {
        Integer cellIndex = new SecureRandom().nextInt(0, 99 + 1);
        return location(cellIndex, ship, Utils.randomEnum(Direction.class));
    }

    public static Placement location(Integer index, Ship ship, Direction direction) {
        return Placement.builder()
            .cellIndex(index)
            .direction(direction)
            .shipType(ship)
            .shipSize(ship.getShipSize())
            .build();
    }

    @Getter
    @RequiredArgsConstructor
    public enum Ship {
        CARRIER(5),
        BATTLESHIP(4),
        CRUISER(3),
        SUBMARINE(3),
        DESTROYER(2);

        private final int shipSize;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Direction {
        HORIZONTAL(1),
        VERTICAL(10);

        private final int value;
    }
}