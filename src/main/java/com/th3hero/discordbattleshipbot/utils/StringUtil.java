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

    /**
     * Escapes special characters used in discord markdown
     * @param string
     * @return discord markdown safe string
     */
    public static String escapeSpecialCharacters(String string) {
        return string
            .replace("_", "\\_") // Italics or Underline
            .replace("*", "\\*") // Bold or Bold Italics
            .replace("~", "\\~") // Strikethrough
            .replace("|", "\\|") // Spoiler
            .replace(">", "\\>") // Quote
            .replace("`", "\\`"); // Code Block
    }
}