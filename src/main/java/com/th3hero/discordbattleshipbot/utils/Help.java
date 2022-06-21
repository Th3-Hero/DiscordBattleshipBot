package com.th3hero.discordbattleshipbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;

public class Help {
    public static void displayHelpMessage(MessageChannel channel) {
        channel.sendMessageEmbeds(helpMessage()).queue();
    }

    public static MessageEmbed helpMessage() {
        return new EmbedBuilder()
        .setColor(new Color(58, 235, 52))
        .setTitle("Economy Bot Help")
        .addField(
            "$Help",
            "Used to Display this message.",
            false
        )
        .addField(
            "$Ping",
            "Used to ping the bot to make sure it's working",
            false
        )
        .addField(
            "$Challenge",
            "Challenge a member to a game of battleship.",
            false
        )
        .build();
    }
}