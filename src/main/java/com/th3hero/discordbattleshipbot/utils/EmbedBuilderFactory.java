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
public final class EmbedBuilderFactory {
    private static final String EMBED_TITLE = "Battleship";
    private static final String GAME_EMBED = "GameID #";
    private static final Color GREEN = new Color(0, 255, 0);
    private static final Color RED = new Color(255, 8, 0);
    private static final Color BLUE = new Color(3, 123, 252);

    /**
     * Accept Game Embed
     * @param names List of player names involved in the game
     * @param gameId
     * @return
     */
    public static MessageEmbed acceptGameEmbed(List<String> names, String gameId) {
        return new EmbedBuilder()
            .setColor(GREEN)
            .setTitle(EMBED_TITLE)
            .addField(
                StringUtil.escapeSpecialCharacters(names.get(0)) + " vs " + StringUtil.escapeSpecialCharacters(names.get(1)),
                "PUT STATS REEEEE",
                false)
            .setFooter(GAME_EMBED + gameId)
            .build();
    }

    /**
     * Accept Game Embed
     * @param names List of player names involved in the game
     * @param gameId
     * @return
     */
    public static MessageEmbed declineGameEmbed(List<String> names, String gameId) {
        return new EmbedBuilder()
            .setColor(RED)
            .setTitle(EMBED_TITLE)
            .addField(
                StringUtil.escapeSpecialCharacters(names.get(0)) + " vs " + StringUtil.escapeSpecialCharacters(names.get(1)),
                StringUtil.toBold(StringUtil.escapeSpecialCharacters(names.get(1))) + " declined the match.",
                false
            )
            .setFooter(GAME_EMBED + gameId)
            .build();
    }

    /**
     * Embed for when a player creates a challenge
     * @param names List with challenger and challenged player
     * @param gameId
     * @return
     */
    public static MessageEmbed challengeRequestBuilder(List<String> names, String gameId) {
        return new EmbedBuilder()
            .setColor(BLUE)
            .setTitle(EMBED_TITLE)
            .addField(
                StringUtil.escapeSpecialCharacters(names.get(0)) + " vs " + StringUtil.escapeSpecialCharacters(names.get(1)),
                "Stats",
                false)
            .addField(
                "",
                StringUtil.toBold(StringUtil.escapeSpecialCharacters(names.get(1))) + " do you choose to accept?",
                false
            )
            .setFooter(GAME_EMBED + gameId)
            .build();
    }

    /**
     * Embed to display a players board with it's grids
     * @param gridOne Friendly Grid
     * @param gridTwo Enemy Grid
     * @return
     */
    public static List<MessageEmbed> boardDisplay(String gridOne, String gridTwo) {
        List<MessageEmbed> embeds = new ArrayList<>();
        embeds.add(
            new EmbedBuilder()
                .setColor(RED)
                .addField(
                    "Enemy Board",
                    gridTwo,
                    false
                )
                .build()
        );

        embeds.add(
            new EmbedBuilder()
                .setColor(GREEN)
                .addField(
                    "Your Board",
                    gridOne,
                    false
                )
                .build()
        );

        return embeds;
    }

    /**
     * Header for a new game
     * @param names
     * @return
     */
    public static MessageEmbed boardHeader(List<String> names) {
        return new EmbedBuilder()
            .setTitle(names.get(0) + " vs " + names.get(1))
            .setDescription("description")
            .setColor(BLUE)
            .build();
    }

    /**
     * Display who sunk who's ship and at what position on the board
     * @param playerWhoSunkName
     * @param playerWhoGotSunkName
     * @param ship
     * @param position
     * @return
     */
    public static MessageEmbed shipSunkEmbed(String playerWhoSunkName, String playerWhoGotSunkName, Ship ship, String position) {
        return new EmbedBuilder()
            .setColor(BLUE)
            .setTitle(ship + " Sunk!")
            .setDescription(playerWhoSunkName + " sunk " + playerWhoGotSunkName + "'s " + ship.name().toLowerCase() + " at " + position)
            .build();
    }

    // TODO: Add stats about the match
    public static MessageEmbed gameOver(String winner) {
        return new EmbedBuilder()
            .setColor(BLUE)
            .setTitle("Game Over")
            .addField(
                "%s is the winner!!!".formatted(winner),
                "Some Text",
                false
            )
            .build();
    }

    public static MessageEmbed gameStart(boolean isTurn) {
        String description = isTurn ? "It is your turn" : "It is the opponents turn";
        return new EmbedBuilder()
            .setColor(BLUE)
            .setTitle("Game has started!")
            .setDescription(description)
            .build();
    }
}