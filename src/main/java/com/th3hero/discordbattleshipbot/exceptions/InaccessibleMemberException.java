package com.th3hero.discordbattleshipbot.exceptions;

public class InaccessibleMemberException extends RuntimeException {
    public InaccessibleMemberException(final String message) {
        super(message);
    }
}