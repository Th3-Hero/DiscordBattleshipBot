package com.th3hero.discordbattleshipbot.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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