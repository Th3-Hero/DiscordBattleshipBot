package com.th3hero.discordbattleshipbot.controller;

import org.springframework.stereotype.Controller;

// import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import com.th3hero.discordbattleshipbot.utils.*;

@Controller
public class ListenController extends ListenerAdapter {

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { // Ignore any message sent by a bot
            return;
        }

        if (!event.getMessage().getContentRaw().startsWith("$")) {
            messageSentTracking(event);
            return;
        }
        commandHandler(event);
    }

    public void commandHandler(MessageReceivedEvent event) {
        // Message msg = event.getMessage();
        switch (event.getMessage().getContentRaw()) {
            case "$Ping":
                Ping.pingBot(event.getChannel());
                break;
            case "$Help":
                Help.displayHelpMessage(event.getChannel());
                break;
        
            default:
        }
    }

    public void messageSentTracking(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
            channel.sendMessage("Not a command")
            .queue();
        
    }
}