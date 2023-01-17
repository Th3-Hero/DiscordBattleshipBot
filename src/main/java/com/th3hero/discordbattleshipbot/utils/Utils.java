package com.th3hero.discordbattleshipbot.utils;

import org.jetbrains.annotations.NotNull;

import com.th3hero.discordbattleshipbot.exceptions.DiscordNullReturnException;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    /**
     * Find effective player names for both players in a game.
     * @param server (Guild)
     * @param game
     * @return List of effective player names (names may be null)
     */
    public static List<String> playerNames(final Guild server, final Game game) {
        Member memberOneById = server.getMemberById(game.getPlayerOne());
        Member memberTwoById = server.getMemberById(game.getPlayerTwo());
        if (memberOneById == null || memberTwoById == null) {
            throw new DiscordNullReturnException("Failed to retrived Member when attemping to create list of player names");
        }
        return Arrays.asList(
            memberOneById.getEffectiveName(),
            memberTwoById.getEffectiveName()
        );
    }

    /**
     * Null safe, case-insensitive {@code valueOf} call. If the value is null, empty or invalid, then the provided default value is used.
     *
     * @param enumType
     *      The class type of the enum to search
     * @param value
     *      The value to find the enum value for
     * @return
     *      The found enum or null
     */
    public static <T extends Enum<T>> T enumValue(@NotNull final Class<T> enumType, final String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            return Enum.valueOf(enumType, value.toUpperCase());
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    public static <T extends Enum<T>> T randomEnum(final Class<T> enumClass) {
        final T[] values = enumClass.getEnumConstants();
        return values[new SecureRandom().nextInt(values.length)];
    }
}