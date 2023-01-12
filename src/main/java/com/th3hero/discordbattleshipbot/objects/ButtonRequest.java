package com.th3hero.discordbattleshipbot.objects;

import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.th3hero.discordbattleshipbot.utils.Utils;

@Getter
@Builder
public class ButtonRequest {
    private User user;
    private Message message;
    private TextChannel channel;
    private Guild server;
    private ClickEvent action;
    private int actionId;
    private ButtonClickEvent event;

    public static ButtonRequest create(final ButtonClickEvent event) {
        // Get raw id from the button
        final String rawId = event.getComponentId();
        // Split the useable id and action apart
        final List<String> idSplits = Arrays.stream(rawId.strip().split("-"))
            .collect(Collectors.toList());
        
        final String action = idSplits.get(1); // the action the button preforms
        final int actionId = Integer.parseInt(idSplits.get(0)); // the id to link the button to a previous action
        return request(event, action, actionId);
    }

    private static ButtonRequest request(final ButtonClickEvent event, final String action, final int actionId) {
        return ButtonRequest.builder()
            .event(event)
            .user(event.getUser())
            .message(event.getMessage())
            .channel(event.getTextChannel())
            .server(event.getGuild())
            .action(Utils.enumValue(ClickEvent.class, action))
            .actionId(actionId)
            .build();
    }

    public enum ClickEvent {
        ACCEPT,
        DECLINE,
        RANDOMIZE,
        READY,
        UN_READY,
        CLOSE_GAME;
    }
}