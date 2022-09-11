package com.th3hero.discordbattleshipbot.objects;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * TODO: Document
 */
@Getter
@Builder
public class Placement {
    private Integer cellIndex;
    private int shipSize;
    private Direction direction;

    public static Placement create(Integer cellIndex, int shipSize, Direction direction) {
        return location(cellIndex, shipSize, direction);
    }

    public static Placement createRandom(int shipSize) {
        Random random = new SecureRandom();
        Integer cellIndex = random.nextInt(0, 99 + 1);
        List<Direction> directionValues = new ArrayList<>(Arrays.asList(Direction.values()));
        Direction direction = directionValues.get(random.nextInt(directionValues.size()));

        return location(cellIndex, shipSize, direction);
    }

    public static Placement location(Integer index, int shipSize, Direction direction) {
        return Placement.builder()
            .cellIndex(index)
            .shipSize(shipSize)
            .direction(direction)
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
        // LEFT(-1),
        HORIZONTAL(1),
        // UP(-10),
        VERTICAL(10);

        private final int value;
    }
}