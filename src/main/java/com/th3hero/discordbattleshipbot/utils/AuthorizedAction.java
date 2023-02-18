package com.th3hero.discordbattleshipbot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.th3hero.discordbattleshipbot.objects.ButtonRequest;

import lombok.NoArgsConstructor;

/**
 * Methods to check if a user is authorized to preform the action.
 */
@Component
@NoArgsConstructor
public class AuthorizedAction {
    @Value("${app.devmode:false}")
    private boolean devMode;

    /**
     * Checks if the user is supposed to be able to preform the action.
     * <p>If {@code false} informs user that they cannot preform the action.</p>
     * @param request -Button Request
     * @param playerId -Permitted User
     * @return If the {@code request} user is the permitted user for the action.
     */
    public boolean permittedAction(ButtonRequest request, String playerId) {
        if (request.getUser().getId().equals(playerId) || devMode) {
            return true;
        } else {
            request.getEvent().reply("You cannot preform this action.")
                .setEphemeral(true).queue();
            return false;
        }
    }
}