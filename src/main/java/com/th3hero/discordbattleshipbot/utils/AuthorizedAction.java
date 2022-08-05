package com.th3hero.discordbattleshipbot.utils;

import com.th3hero.discordbattleshipbot.objects.ButtonRequest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Methods to check if a user is authorized to preform the action.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizedAction {

    /**
     * Checks if the user is supposed to be able to preform the action.
     * <p>If {@code false} informs user that they cannot preform the action.</p>
     * @param request -Button Request
     * @param playerId -Permitted User
     * @return If the {@code request} user is the permitted user for the action.
     */
    public static boolean permittedAction(ButtonRequest request, String playerId) {
        if (request.getUser().getId().equals(playerId)) {
            return true;
        } else {
            request.getEvent().reply("You cannot preform this action.")
            .setEphemeral(true).queue();
            return false;
        }
    }

    /**
     * Checks if the user is supposed to be able to preform the action.
     * <p>If {@code false} informs user that they cannot preform the action.</p>
     * @param request -Button Request
     * @param playerId -Permitted User
     * @param devMode -Skips this check entirely for development purposes
     * @return If the {@code request} user is the permitted user for the action.
     */
    public static boolean permittedAction(ButtonRequest request, String playerId, boolean devMode) {
        if (request.getUser().getId().equals(playerId) || devMode) {
            return true;
        } else {
            request.getEvent().reply("You cannot preform this action.")
            .setEphemeral(true).queue();
            return false;
        }
    }
}