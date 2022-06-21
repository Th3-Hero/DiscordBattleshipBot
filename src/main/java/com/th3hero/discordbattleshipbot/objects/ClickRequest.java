package com.th3hero.discordbattleshipbot.objects;

import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import com.th3hero.discordbattleshipbot.controllers.MessageController;

@Getter
@Builder
public class ClickRequest {
    private User user;
    private Message message;
    private MessageChannel channel;
    private MessageController.ClickEvent action;

    public static ClickRequest create(final ButtonClickEvent event) {
        return request(event);
    }

    public static ClickRequest request(final ButtonClickEvent event) {
        return ClickRequest.builder()
            .user(event.getUser())
            .message(event.getMessage())
            .channel(event.getChannel())
            .action(MessageController.ClickEvent.value(event.getComponentId()))
            .build();
    }
}
