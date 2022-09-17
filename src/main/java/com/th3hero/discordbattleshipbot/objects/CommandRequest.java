package com.th3hero.discordbattleshipbot.objects;

import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.th3hero.discordbattleshipbot.utils.Utils;

@Getter
@Builder
public class CommandRequest {
    private User requester;
    private List<User> mentionedUsers;
    private MessageChannel channel;
    private Guild server;
    private Command command;
    private boolean validToken;
    private List<String> arguments;

    public static CommandRequest create(final Message message) {
        final String messageRaw = message.getContentRaw().strip();
        if (messageRaw.isEmpty()) {
            return request(message, null, "", Collections.emptyList());
        }

        final String token = messageRaw.substring(0, 1);
        final List<String> arguments = Arrays.stream(messageRaw.substring(1).strip().split("\\s+"))
            .collect(Collectors.toList());
        final String command = arguments.remove(0).toLowerCase();

        return request(message, command, token, arguments);
    }

    public static CommandRequest request(final Message message, final String command, final String token, final List<String> arguments) {
        return CommandRequest.builder()
            .requester(message.getAuthor())
            .mentionedUsers(message.getMentionedUsers())
            .server(message.getGuild())
            .channel(message.getChannel())
            .command(Utils.enumValue(Command.class, command))
            .validToken(token.equals("$"))
            .arguments(arguments)
            .build();
    }

    public enum Command {
        HELP,
        PING,
        CHALLENGE,
        DELETE,
        APOCABLOOM,
        SHOOT;
    }
}
