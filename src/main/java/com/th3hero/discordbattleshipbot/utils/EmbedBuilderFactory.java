package com.th3hero.discordbattleshipbot.utils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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
    public static MessageEmbed acceptGameEmbed(String playerOneName, String playerTwoName, String gameId) {
        return new EmbedBuilder()
        .setColor(green)
        .setTitle(embedTitle)
        .addField(
            StringUtil.escapeSpecialCharacters(playerOneName) + " vs " + StringUtil.escapeSpecialCharacters(playerTwoName),
            "PUT STATS REEEEE",
            false)
        .addField(
            "",
            gameEmbed + StringUtil.toBold(gameId),
            false
        )
        .build();
    }

    /**
     * Generates embed for a game that has been declined.
     * @param playerOneName -PlayerOne's effective name
     * @param playerTwoName -PlayerTwo's effective name
     * @param gameId -Game ID
     * @return <pre><code>MessageEmbed</code></pre>
     */
    public static MessageEmbed declineGameEmbed(String playerOneName, String playerTwoName, String gameId) {
        return new EmbedBuilder()
        .setColor(red)
        .setTitle(embedTitle)
        .addField(
            StringUtil.escapeSpecialCharacters(playerOneName) + " vs " + StringUtil.escapeSpecialCharacters(playerTwoName),
            StringUtil.toBold(StringUtil.escapeSpecialCharacters(playerTwoName)) + " declined the match.",
            false
        )
        .addField(
            "",
            gameEmbed + StringUtil.toBold(gameId),
            false
        )
        .build();
    }

    /**
     * Generates embed for a new challenge.
     * @param playerOneName -PlayerOne's effective name
     * @param playerTwoName -PlayerTwo's effective name
     * @param gameId -Game ID
     * @return <pre><code>MessageEmbed</code></pre>
     */
    public static MessageEmbed challengeRequestBuilder(String playerOneName, String playerTwoName, String gameId) {
        return new EmbedBuilder()
        .setColor(blue)
        .setTitle(embedTitle)
        .addField(
            StringUtil.escapeSpecialCharacters(playerOneName) + " vs " + StringUtil.escapeSpecialCharacters(playerTwoName),
            "Stats",
            false)
        .addField(
            "",
            StringUtil.toBold(StringUtil.escapeSpecialCharacters(playerTwoName)) + " do you choose to accept?",
            false
        )
        .addField(
            "",
            gameEmbed + StringUtil.toBold(gameId),
            false
        )
        .build();
    }

    public static List<MessageEmbed> boardDisplay(String gridOne, String gridTwo) {
        List<MessageEmbed> boards = new ArrayList<>();
        boards.add(
            new EmbedBuilder()
            .setColor(green)
            .setTitle("Battleship")
            .addField(
                "Your Board",
                gridOne,
                false
            )
            .build()
        );

        boards.add(
            new EmbedBuilder()
            .setColor(red)
            .addField(
                "Enemy Board",
                gridTwo,
                false
            )
            .build()
        );

        return boards;
    }

    public static MessageEmbed apocaBloom(){
        return new EmbedBuilder()
        .setColor(blue)
        .setTitle("Apoca Bloom", "https://deeprockgalactic.fandom.com/wiki/Apoca_Bloom")
        .setImage("https://static.wikia.nocookie.net/deeprockgalactic_gamepedia_en/images/a/a0/Apoca_bloom_icon.png/revision/latest/scale-to-width-down/250?cb=20180519134446")
        .addField(
            "", 
            "Apoca Bloom are a secondary mission objective. They can appear in any gamemode. To be gathered, dwarves must be close enough to pick them up with the [USE] key ('E' by default). 15 must be collected to complete the secondary objective.", 
            false
        )
        .build();
    }
}