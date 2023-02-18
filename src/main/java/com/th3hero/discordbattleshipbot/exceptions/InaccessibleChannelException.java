package com.th3hero.discordbattleshipbot.exceptions;

public class InaccessibleChannelException extends RuntimeException {
    public InaccessibleChannelException(final String message) {
        super(message);
    }
}