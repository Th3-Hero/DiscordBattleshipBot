package com.th3hero.discordbattleshipbot.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.exceptions.DiscordNullReturnException;
import com.th3hero.discordbattleshipbot.jpa.entities.EnemyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.FriendlyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.jpa.entities.GameBoard;
import com.th3hero.discordbattleshipbot.jpa.entities.Game.GameStatus;
import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.objects.HitEvent;
import com.th3hero.discordbattleshipbot.repositories.GameRepository;
import com.th3hero.discordbattleshipbot.utils.FindUtil;
import com.th3hero.discordbattleshipbot.utils.Utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

@Service
@Transactional
@RequiredArgsConstructor
public class FireService {
    private final GameHandlerService gameHandlerService;
    private final GameRepository gameRepository;
    private final BoardDisplayService boardDisplayService;
    private final GameStateHandlerService gameStateHandlerService;
    @Value("${app.devmode:false}")
    private boolean devMode;

    public void fireHandling(CommandRequest request) {
        String requesterId = request.getRequester().getId();
        Game game = gameHandlerService.fetchGameByChannelId(request.getChannel().getId());
        if (!game.getGameStatus().equals(GameStatus.ACTIVE)) {
            request.getMessage().reply("Game is not currently active.").queue();
            return;
        }
        if (!game.getCurrentTurn().equals(requesterId) && !devMode) {
            request.getMessage().reply("It is NOT currently your turn").queue();
            return;
        }
        if (request.getArguments().isEmpty()) {
            request.getMessage().reply("Missing valid gridsquare").queue();
            return;
        }
        int cellIndex = coordinateToCellIndex(request.getArguments().get(0));
        if (cellIndex == -1) {
            request.getMessage().reply(request.getArguments().get(0) + " is not a valid gridsquare.").queue();
            return;
        }

        List<EnemyCell> currentPlayerEnemyGrid = FindUtil.findGameboardByPlayerId(game, requesterId).getEnemyCells();
        List<FriendlyCell> opponentFriendlyGrid = FindUtil.findGameboardByPlayerId(game, findOpponentId(game, requesterId))
            .getFriendlyCells();
        FriendlyCell.CellStatus fullCellStatus = opponentFriendlyGrid.get(cellIndex).getCellStatus();

        if (fullCellStatus.equals(FriendlyCell.CellStatus.HIT) || fullCellStatus.equals(FriendlyCell.CellStatus.MISS)) {
            request.getMessage().reply(request.getArguments().get(0) + " has already been guessed.").queue();
            return;
        }
        if (fullCellStatus.equals(FriendlyCell.CellStatus.EMPTY)) {
            currentPlayerEnemyGrid.get(cellIndex).setCellStatus(EnemyCell.CellStatus.MISS);
            opponentFriendlyGrid.get(cellIndex).setCellStatus(FriendlyCell.CellStatus.MISS);
        }
        if (fullCellStatus.equals(FriendlyCell.CellStatus.SHIP)) {
            currentPlayerEnemyGrid.get(cellIndex).setCellStatus(EnemyCell.CellStatus.HIT);
            opponentFriendlyGrid.get(cellIndex).setCellStatus(FriendlyCell.CellStatus.HIT);
        }

        Guild server = request.getServer();
        Member memberById = server.getMemberById(findOpponentId(game, requesterId));
        if (memberById == null) {
            throw new DiscordNullReturnException("Failed to retrieve member");
        }
        User opponent = memberById.getUser();
        HitEvent hitEvent = HitEvent.createEvent(
            request.getServer(),
            request.getRequester(),
            opponent, game, 
            opponentFriendlyGrid.get(cellIndex).getShipType(), 
            request.getArguments().get(0)
        );

        if (hitEvent.getShipHit() != null) {
            gameStateHandlerService.sunkStateHandler(hitEvent);
        }

        game.setCurrentTurn(findOpponentId(game, requesterId));
        game = gameRepository.save(game);
        displayUpdateBoards(request.getServer(), game);
    }

    private void displayUpdateBoards(Guild server, Game game) {
        for (GameBoard board : game.getGameBoards()) {
            List<MessageEmbed> embeds = boardDisplayService.displayBoard(board);
            TextChannel textChannelById = server.getTextChannelById(board.getChannelId());
            if (textChannelById == null) {
                throw new DiscordNullReturnException("Failed to retrieve TextChannel with given id");
            }
            textChannelById.sendMessageEmbeds(embeds).queue();
        }
    }

    private int coordinateToCellIndex(String coordinates) {
        Matcher matcher = Pattern.compile("^([a-jA-J])(\\d)$")
            .matcher(coordinates);
        if (!matcher.find()) {
            return -1;
        }

        Letters enumValue = Utils.enumValue(Letters.class, matcher.group(1));
        if (enumValue == null) {
            return -1;
        }
        int letter = enumValue.ordinal();
        int number = Integer.parseInt(matcher.group(2));

        return (letter * 10) + number;
    }

    private String findOpponentId(Game game, String currentPlayerId) {
        if (game.getPlayerOne().equals(currentPlayerId)) {
            return game.getPlayerTwo();
        } else {
            return game.getPlayerOne();
        }
    }


    private enum Letters {
        A, B, C, D, E, F, G, H, I, J;
    }
}