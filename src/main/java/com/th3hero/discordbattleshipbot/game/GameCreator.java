package com.th3hero.discordbattleshipbot.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.Color;
import java.util.List;

import com.th3hero.discordbattleshipbot.objects.CommandRequest;

public class GameCreator {
    public static String toBold(String string) {
        return "**" + string + "**";
    }

    public static void gameRequest(CommandRequest request) {
        String player1 = request.getRequester().getName();
        List<User> user2 = request.getMentionedUserName();
        String player2 = user2.toString().substring(3, user2.toString().length() - 21);
        request.getChannel().sendMessageEmbeds(challengeRequestBuilder(player1, player2)).setActionRow(
            Button.success("ACCEPT", "Accept"),
            Button.danger("DECLINE", "Decline")
        ).queue();
    }

    public static void updateGameRequest(ButtonClickEvent event, String action) {

        String player2 = event.getUser().getName();
        if (action == "ACCEPT") {
            MessageEmbed acceptedEmbed = acceptGameEmbed("Player1", player2);
            event.getMessage().editMessageEmbeds(acceptedEmbed) // Update Embed
                     .setActionRows() // Strip Buttons
                     .queue();
        } 
        else {
            MessageEmbed declinedEmbed = declineGameEmbed("Player1", event.getUser().getName());
                event.editMessageEmbeds(declinedEmbed) // Update Embed
                     .setActionRows() // Strip Buttons
                     .queue();
        }
    }

    public static MessageEmbed acceptGameEmbed(String player1, String player2) {
        return new EmbedBuilder()
        .setColor(new Color(0, 255, 0))
        .setTitle("Battleship")
        .addField(
            player1 + " vs " + player2,
            "PUT STATS REEEEE",
            false)
        .build();
    }

    public static MessageEmbed declineGameEmbed(String player1, String player2) {
        return new EmbedBuilder()
        .setColor(new Color(255, 8, 0))
        .setTitle("Battleship")
        .addField(
            player1 + " vs " + player2,
            toBold(player2) + " declined the match.",
            false
        )
        .build();
    }

    public static MessageEmbed challengeRequestBuilder(String player1, String player2) {
        return new EmbedBuilder()
        .setColor(new Color(3, 123, 252))
        .setTitle("Battleship")
        .addField(
            player1 + " vs " + player2,
            "Stats",
            false)
        .addField(
            "",
            toBold(player2) + " do you choose to accept?",
            false
        )
        .build();
    }
}