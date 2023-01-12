package com.th3hero.discordbattleshipbot.utils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.th3hero.discordbattleshipbot.objects.Placement.Ship;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Contains methods for creating various embeds the bot uses.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EmbedBuilderFactory {
    private static String embedTitle = "Battleship";
    private static String gameEmbed = "GameID #";
    private static Color green = new Color(0, 255, 0);
    private static Color red = new Color(255, 8, 0);
    private static Color blue = new Color(3, 123, 252);


    /**
     * Generates embed for a game that has been accepted.
     * @param playerOneName -PlayerOne's effective name
     * @param playerTwoName -PlayerTwo's effective name
     * @param gameId -Game ID
     * @return <pre><code>MessageEmbed</code></pre>
     */
    public static MessageEmbed acceptGameEmbed(List<String> names, String gameId) {
        return new EmbedBuilder()
        .setColor(green)
        .setTitle(embedTitle)
        .addField(
            StringUtil.escapeSpecialCharacters(names.get(0)) + " vs " + StringUtil.escapeSpecialCharacters(names.get(1)),
            "PUT STATS REEEEE",
            false)
        .setFooter(gameEmbed + gameId)
        .build();
    }

    /**
     * Generates embed for a game that has been declined.
     * @param playerOneName -PlayerOne's effective name
     * @param playerTwoName -PlayerTwo's effective name
     * @param gameId -Game ID
     * @return <pre><code>MessageEmbed</code></pre>
     */
    public static MessageEmbed declineGameEmbed(List<String> names, String gameId) {
        return new EmbedBuilder()
        .setColor(red)
        .setTitle(embedTitle)
        .addField(
            StringUtil.escapeSpecialCharacters(names.get(0)) + " vs " + StringUtil.escapeSpecialCharacters(names.get(1)),
            StringUtil.toBold(StringUtil.escapeSpecialCharacters(names.get(1))) + " declined the match.",
            false
        )
        .setFooter(gameEmbed + gameId)
        .build();
    }

    /**
     * Generates embed for a new challenge.
     * @param playerOneName -PlayerOne's effective name
     * @param playerTwoName -PlayerTwo's effective name
     * @param gameId -Game ID
     * @return <pre><code>MessageEmbed</code></pre>
     */
    public static MessageEmbed challengeRequestBuilder(List<String> names, String gameId) {
        return new EmbedBuilder()
        .setColor(blue)
        .setTitle(embedTitle)
        .addField(
            StringUtil.escapeSpecialCharacters(names.get(0)) + " vs " + StringUtil.escapeSpecialCharacters(names.get(1)),
            "Stats",
            false)
        .addField(
            "",
            StringUtil.toBold(StringUtil.escapeSpecialCharacters(names.get(1))) + " do you choose to accept?",
            false
        )
        .setFooter(gameEmbed + gameId)
        .build();
    }

    public static List<MessageEmbed> boardDisplay(String gridOne, String gridTwo) {
        List<MessageEmbed> embeds = new ArrayList<>();
        embeds.add(
            new EmbedBuilder()
            .setColor(red)
            .addField(
                "Enemy Board",
                gridTwo,
                false
            )
            .build()
        );

        embeds.add(
            new EmbedBuilder()
            .setColor(green)
            .addField(
                "Your Board",
                gridOne,
                false
            )
            .build()
        );

        return embeds;
    }

    public static MessageEmbed boardHeader(List<String> names) {
        return new EmbedBuilder()
        .setTitle(names.get(0) + " vs " + names.get(1))
        .setDescription("description")
        .setColor(blue)
        .build();
    }

    public static MessageEmbed shipSunkEmbed(String playerWhoSunkName, String playerWhoGotSunkName, Ship ship, String position) {
        return new EmbedBuilder()
        .setColor(blue)
        .setTitle(ship + " Sunk!")
        .setDescription(playerWhoSunkName + " sunk " + playerWhoGotSunkName + "'s " + ship.name().toLowerCase() + " at " + position)
        .build();
    }

    // TODO: Add stats about the match
    public static MessageEmbed gameOver(String winner) {
        return new EmbedBuilder()
        .setColor(blue)
        .setTitle("Game Over")
        .addField(
            winner + " is the winner!!!",
            "Some Text",
            false
        )
        .build();
    }

    public static MessageEmbed gameStart(boolean isTurn) {
        String description;
        if (isTurn) {
            description = "It is your turn";
        }
        else {
            description = "It is the opponents turn";
        }
        return new EmbedBuilder()
        .setColor(blue)
        .setTitle("Game has started!")
        .setDescription(description)
        .build();
    }
}