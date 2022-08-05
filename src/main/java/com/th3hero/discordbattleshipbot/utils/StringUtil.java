package com.th3hero.discordbattleshipbot.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A collection of useful methods to manipulate strings.
 * Targeted mostly towards discord.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {

    public static String toBold(String string) {
        return "**" + string + "**";
    }

    public static String toItalic(String string) {
        return "*" + string + "*";
    }

    public static String toUnderline(String string) {
        return "_" + string + "_";
    }

}