package com.th3hero.discordbattleshipbot.exceptions;

public class DiscordNullReturnException extends RuntimeException {
    public DiscordNullReturnException(final String message) {
        super(message);
    }
}