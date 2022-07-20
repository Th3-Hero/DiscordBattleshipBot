package com.th3hero.discordbattleshipbot.services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.Color;
import java.util.EnumSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.enums.ChannelPermissions;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.jpa.entities.Game.GameStatus;
import com.th3hero.discordbattleshipbot.objects.ClickRequest;
import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.utils.StringUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameCreator {
    private final PlayerHandlerService playerHandlerService;
    private final GameHandlerService gameHandlerService;

    public void gameRequest(CommandRequest request) {

        // Get users from request
        User user1 = request.getRequester();
        List<User> mentionedUser = request.getMentionedUser();
        User user2 = mentionedUser.get(0);
        
        // Get ids of each user involved in the game
        String player1 = playerHandlerService.fetchPlayer(user1.getId()).getPlayerId();
        String player2 = playerHandlerService.fetchPlayer(user2.getId()).getPlayerId();
        
        // Create game and fetch gameId
        Game game = gameHandlerService.createGame(player1, player2);
        String gameId = game.getGameId().toString();
        
        // Fetch player names from playerId stored in game
        Guild server = request.getServer();
        String player1Name = server.getMember(user1).getEffectiveName();
        String player2Name = server.getMember(user2).getEffectiveName();


        request.getChannel().sendMessageEmbeds(challengeRequestBuilder(player1Name, player2Name, gameId)).setActionRow(
            Button.success(gameId + "-ACCEPT", "Accept"),
            Button.danger(gameId + "-DECLINE", "Decline")
        ).queue();

    }

    public void updateGameRequest(ClickRequest request) {

        Game game = gameHandlerService.fetchGame(request.getActionId());
        String interactionUserId = request.getUser().getId(); // ID of the user who clicked the button
        Guild server = request.getServer();

        // Only player2 should be able to accept or decline the challenge
        if (!interactionUserId.equals(game.getPlayerTwo())) {
            final String interactionUserName = server.getMemberById(interactionUserId).getEffectiveName();
            request.getEvent().reply(interactionUserName + " you must be the person challenged to accept or decline.").queue();

            return;
        }
        final String gameId = game.getGameId().toString();
        String player1Name = server.getMemberById(game.getPlayerOne()).getEffectiveName();
        String player2Name = server.getMemberById(game.getPlayerTwo()).getEffectiveName();
        switch (request.getAction()) {
            case ACCEPT:
                MessageEmbed acceptedEmbed = acceptGameEmbed(player1Name, player2Name, gameId);
                request.getMessage().editMessageEmbeds(acceptedEmbed) // Update Embed
                        .setActionRows() // Strip Buttons
                        .queue();
                game.setGameStatus(GameStatus.ACTIVE);
                startGame(game, server, server.getGuildChannelById(ChannelType.TEXT, request.getChannel().getId()));
                break;
            case DECLINE:
                MessageEmbed declinedEmbed = declineGameEmbed(player1Name, player2Name, gameId);
                    request.getMessage().editMessageEmbeds(declinedEmbed) // Update Embed
                        .setActionRows() // Strip Buttons
                        .queue();
                break;
            default:
        }
        
    }

    public void startGame(Game game, Guild server, GuildChannel channel) {
        User user1 = server.getMemberById(game.getPlayerOne()).getUser();
        User user2 = server.getMemberById(game.getPlayerTwo()).getUser();
        String player1Name = server.getMemberById(game.getPlayerOne()).getEffectiveName();
        String player2Name = server.getMemberById(game.getPlayerTwo()).getEffectiveName();
        server.createTextChannel(player1Name + "-Battleship-" + game.getGameId())
            .addMemberPermissionOverride(user1.getIdLong(), ChannelPermissions.allow(), ChannelPermissions.deny())
            .addPermissionOverride(server.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
            .setParent(channel.getParent())
            .queue();
        server.createTextChannel(player2Name + "-Battleship-" + game.getGameId())
            .addMemberPermissionOverride(user2.getIdLong(), ChannelPermissions.allow(), ChannelPermissions.deny())
            .addPermissionOverride(server.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
            .setParent(channel.getParent())
            .queue();
    }

    static String embedTitle = "Battleship";
    static String gameEmbed = "GameID #";
    public static MessageEmbed acceptGameEmbed(String player1, String player2, String gameId) {
        return new EmbedBuilder()
        .setColor(new Color(0, 255, 0))
        .setTitle(embedTitle)
        .addField(
            player1 + " vs " + player2,
            "PUT STATS REEEEE",
            false)
        .addField(
            "",
            gameEmbed + StringUtil.toBold(gameId),
            false
        )
        .build();
    }

    public static MessageEmbed declineGameEmbed(String player1, String player2, String gameId) {
        return new EmbedBuilder()
        .setColor(new Color(255, 8, 0))
        .setTitle(embedTitle)
        .addField(
            player1 + " vs " + player2,
            StringUtil.toBold(player2) + " declined the match.",
            false
        )
        .addField(
            "",
            gameEmbed + StringUtil.toBold(gameId),
            false
        )
        .build();
    }

    public static MessageEmbed challengeRequestBuilder(String player1, String player2, String gameId) {
        return new EmbedBuilder()
        .setColor(new Color(3, 123, 252))
        .setTitle(embedTitle)
        .addField(
            player1 + " vs " + player2,
            "Stats",
            false)
        .addField(
            "",
            StringUtil.toBold(player2) + " do you choose to accept?",
            false
        )
        .addField(
            "",
            gameEmbed + StringUtil.toBold(gameId),
            false
        )
        .build();
    }
}