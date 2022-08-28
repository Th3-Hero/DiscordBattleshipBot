package com.th3hero.discordbattleshipbot.enums;

/**
 * Interaction Enums for discord text commands and buttons
 */
public class Interactions {

    /**
     * Text Commands
     */
    public enum Command {
        HELP,
        PING,
        CHALLENGE,
        DELETE,
        APOCABLOOM;


        /**
         * Null safe, case-insensitive {@code valueOf} call that returns null rather than an error if no result is found.
         *
         * @param command
         *      The value to find an enum for. May be null
         * @return
         *      The resulting enum, or null if not found
         */
        public static Command value(final String command) {
            if (command == null) {
                return null;
            }

            try {
                return valueOf(command.toUpperCase());
            } catch (final IllegalArgumentException e) {
                return null;
            }
        }
    }

    /**
     * Button Actions
     */
    public enum ClickEvent {
        ACCEPT,
        DECLINE;

        /**
         * Null safe, case-insensitive {@code valueOf} call that returns null rather than an error if no result is found.
         *
         * @param command
         *      The value to find an enum for. May be null
         * @return
         *      The resulting enum, or null if not found
         */
        public static ClickEvent value(final String command) {
            if (command == null) {
                return null;
            }

            try {
                return valueOf(command.toUpperCase());
            } catch (final IllegalArgumentException e) {
                return null;
            }
        }
    }
}