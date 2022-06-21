package com.th3hero.discordbattleshipbot.controllers;

import org.springframework.stereotype.Controller;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import com.th3hero.discordbattleshipbot.game.GameCreator;
import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.utils.*;


@Controller
public class MessageController extends ListenerAdapter {

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { // Ignore any message sent by a bot
            return;
        }

        try {
            final CommandRequest request = CommandRequest.create(event.getMessage());
            if (!request.isValidToken() || request.getCommand() == null) {
                return;
            }
            else {
                commandHandler(request);
            }


        } catch (Exception e) {
            event.getChannel().sendMessage("The command is invalid or an error has occurred").queue();
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        switch (event.getComponentId()) {
            case "ACCEPT":
                GameCreator.updateGameRequest(event, "ACCEPT");
                break;
            case "DECLINE":
                GameCreator.updateGameRequest(event, "DECLINE");
                break;
            default:
        }
    }

    public void commandHandler(final CommandRequest request) {
        switch (request.getCommand()) {
            case PING:
                Ping.pingBot(request.getChannel());
                break;
            case HELP:
                Help.displayHelpMessage(request.getChannel());
                break;
            case CHALLENGE:
                GameCreator.gameRequest(request);
                break;
        
            default:
        }
    }

    public enum Command {
        HELP,
        PING,
        CHALLENGE;


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
}