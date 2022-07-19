package com.th3hero.discordbattleshipbot.objects;

import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.th3hero.discordbattleshipbot.controllers.MessageController;

@Getter
@Builder
public class ClickRequest {
    private User user;
    private Message message;
    private MessageChannel channel;
    private MessageController.ClickEvent action;
    private String actionId;

    public static ClickRequest create(final ButtonClickEvent event) {
        // Get raw id from the button
        final String rawId = event.getComponentId();
        // Split the useable id and action apart
        final List<String> idSplits = Arrays.stream(rawId.substring(1).strip().split("-"))
            .collect(Collectors.toList());
        
        final String action = idSplits.get(1); // the action the button preforms
        final String actionId = idSplits.get(0); // the id to link a button to a previous action
        return request(event, action, actionId);
    }

    public static ClickRequest request(final ButtonClickEvent event, final String action, final String actionId) {
        return ClickRequest.builder()
            .user(event.getUser())
            .message(event.getMessage())
            .channel(event.getChannel())
            .action(MessageController.ClickEvent.value(action))
            .actionId(actionId)
            .build();
    }
}
