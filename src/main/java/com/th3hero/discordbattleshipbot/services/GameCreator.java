package com.th3hero.discordbattleshipbot.services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.Color;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.objects.ClickRequest;
import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.services.PlayerHandlerService;
import com.th3hero.discordbattleshipbot.utils.JdaFactory;
import com.th3hero.discordbattleshipbot.utils.StringUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameCreator {
    private final PlayerHandlerService playerHandlerService;
    private final GameHandlerService gameHandlerService;
    private final JdaFactory jdaFactory;

    public void gameRequest(CommandRequest request) {
        try {
            final JDA jda = jdaFactory.jdaLookupClient().awaitReady();

            // Get users from request
            User user1 = request.getRequester();
            List<User> mentionedUser = request.getMentionedUser();
            User user2 = mentionedUser.get(0);

            // Get ids of each user involved in the game
            String player1 = playerHandlerService.fetchPlayer(user1.getId()).getPlayerId();
            String player2 = playerHandlerService.fetchPlayer(user2.getId()).getPlayerId();
            // Create game and fetch gameId
            Game game = gameHandlerService.createGame(player1, player2);
            String gameId = game.getGameId();

            // Fetch player names from playerId stored in game
            String player1Name = jda.getUserById(game.getPlayer1()).getName();
            String player2Name = jda.getUserById(game.getPlayer2()).getName();




            request.getChannel().sendMessageEmbeds(challengeRequestBuilder(player1Name, player2Name, gameId)).setActionRow(
                Button.success(gameId + "-ACCEPT", "Accept"),
                Button.danger(gameId + "-DECLINE", "Decline")
            ).queue();
            jda.shutdown();
        } catch (LoginException | InterruptedException e) {
            request.getChannel().sendMessage("Unexpected lookup error").queue();
        }
    }

    public void updateGameRequest(ClickRequest request) {
        try {
            final JDA jda = jdaFactory.jdaLookupClient().awaitReady();

            Game game = gameHandlerService.fetchGame(request.getActionId());
            String interactionUserId = request.getUser().getId(); // ID of the user who clicked the button
            
            // Only player2 should be able to accept or decline the challenge
            if (interactionUserId != game.getPlayer2()) {
                final String interactionUserName = jda.getUserById(interactionUserId).getName();
                request.getChannel()
                .sendMessage(interactionUserName + " you must be the person challenged to accept or delcine.").queue();
                jda.shutdown();
                return;
            }

            final String gameId = game.getGameId();
            String player1Name = jda.getUserById(game.getPlayer1()).getName();
            String player2Name = jda.getUserById(game.getPlayer2()).getName();
            
            switch (request.getAction()) {
                case ACCEPT:
                    MessageEmbed acceptedEmbed = acceptGameEmbed(player1Name, player2Name, gameId);
                    request.getMessage().editMessageEmbeds(acceptedEmbed) // Update Embed
                            .setActionRows() // Strip Buttons
                            .queue();
                    break;
                case DECLINE:
                    MessageEmbed declinedEmbed = declineGameEmbed(player1Name, player2Name, gameId);
                        request.getMessage().editMessageEmbeds(declinedEmbed) // Update Embed
                            .setActionRows() // Strip Buttons
                            .queue();
                    break;
                default:
            }

            jda.shutdown();
        } catch (Exception e) {
            request.getChannel().sendMessage("Unexpected lookup error").queue();
        }
    }

    public static MessageEmbed acceptGameEmbed(String player1, String player2, String gameId) {
        return new EmbedBuilder()
        .setColor(new Color(0, 255, 0))
        .setTitle("Battleship")
        .addField(
            player1 + " vs " + player2,
            "PUT STATS REEEEE",
            false)
        .addField(
            "",
            "GameID #" + gameId,
            false
        )
        .build();
    }

    public static MessageEmbed declineGameEmbed(String player1, String player2, String gameId) {
        return new EmbedBuilder()
        .setColor(new Color(255, 8, 0))
        .setTitle("Battleship")
        .addField(
            player1 + " vs " + player2,
            StringUtil.toBold(player2) + " declined the match.",
            false
        )
        .addField(
            "",
            "GameID #" + gameId,
            false
        )
        .build();
    }

    public static MessageEmbed challengeRequestBuilder(String player1, String player2, String gameId) {
        return new EmbedBuilder()
        .setColor(new Color(3, 123, 252))
        .setTitle("Battleship")
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
            "GameID #" + gameId,
            false
        )
        .build();
    }
}