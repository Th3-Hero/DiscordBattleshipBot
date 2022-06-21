package com.th3hero.discordbattleshipbot.controllers;

import org.springframework.stereotype.Controller;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import com.th3hero.discordbattleshipbot.game.GameCreator;
import com.th3hero.discordbattleshipbot.objects.ClickRequest;
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
    public void onButtonClick(final ButtonClickEvent event) {
        try {
            final ClickRequest request = ClickRequest.create(event);
            if (request.getAction() == null) {
                return;
            }
            else {
                buttonHandler(request);
            }
        } catch (Exception e) {
            event.getChannel().sendMessage("Something went impossibly wrong").queue();
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

    public void buttonHandler(final ClickRequest request) {
        switch (request.getAction()) {
            case ACCEPT:
                GameCreator.updateGameRequest(request);
                break;
            case DECLINE:
                GameCreator.updateGameRequest(request);
                break;
            default:
        }
    }


    public enum ClickEvent {
        ACCEPT,
        DECLINE;

        public static ClickEvent value(final String command) {
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