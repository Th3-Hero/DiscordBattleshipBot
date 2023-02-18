package com.th3hero.discordbattleshipbot.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.exceptions.InaccessibleChannelException;
import com.th3hero.discordbattleshipbot.exceptions.InaccessibleMemberException;
import com.th3hero.discordbattleshipbot.jpa.entities.EnemyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.FriendlyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.jpa.entities.GameBoard;
import com.th3hero.discordbattleshipbot.jpa.entities.Game.GameStatus;
import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.objects.ShotEvent;
import com.th3hero.discordbattleshipbot.utils.FindUtil;
import com.th3hero.discordbattleshipbot.utils.Utils;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

@Service
@Transactional
@RequiredArgsConstructor
public class FireService {
    private final GameHandlerService gameHandlerService;
    private final BoardDisplayService boardDisplayService;
    private final GameStateHandlerService gameStateHandlerService;
    @Value("${app.devmode:false}")
    private boolean devMode;

    public void fireHandling(CommandRequest request) {
        String requesterId = request.getRequester().getId();
        Game game = gameHandlerService.fetchGameByChannelId(request.getChannel().getId());
        String opponentId = findOpponentId(game, requesterId);
        if (!game.getGameStatus().equals(GameStatus.ACTIVE)) {
            request.getMessage().reply("Game is not currently active.").queue();
            return;
        }
        if (!game.getCurrentTurn().equals(requesterId) && !devMode) {
            request.getMessage().reply("It is NOT currently your turn").queue();
            return;
        }
        if (request.getArguments().isEmpty()) {
            request.getMessage().reply("Missing valid grid square").queue();
            return;
        }
        int cellIndex = coordinateToCellIndex(request.getArguments().get(0));
        if (cellIndex == -1) {
            request.getMessage().reply(request.getArguments().get(0) + " is not a valid grid square.").queue();
            return;
        }
        // TODO: better names maybe?
        List<EnemyCell> currentPlayerEnemyGrid = FindUtil.findGameboardByPlayerId(game, requesterId).getEnemyCells();
        List<FriendlyCell> opponentFriendlyGrid = FindUtil.findGameboardByPlayerId(game, opponentId)
            .getFriendlyCells();
        FriendlyCell.CellStatus cellStatus = opponentFriendlyGrid.get(cellIndex).getCellStatus();

        switch (cellStatus) {
            case HIT, MISS -> {
                request.getMessage().reply(request.getArguments().get(0) + " has already been guessed.").queue();
                return;
            }
            case EMPTY -> {
                currentPlayerEnemyGrid.get(cellIndex).setCellStatus(EnemyCell.CellStatus.MISS);
                opponentFriendlyGrid.get(cellIndex).setCellStatus(FriendlyCell.CellStatus.MISS);
            }
            case SHIP -> {
                currentPlayerEnemyGrid.get(cellIndex).setCellStatus(EnemyCell.CellStatus.HIT);
                opponentFriendlyGrid.get(cellIndex).setCellStatus(FriendlyCell.CellStatus.HIT);
            }
        }

        Member memberById = request.getServer().getMemberById(opponentId);
        if (memberById == null) {
            throw new InaccessibleMemberException("Failed to retrieve member");
        }
        ShotEvent hitEvent = ShotEvent.createEvent(
            request.getServer(),
            request.getRequester(),
            memberById.getUser(), game, 
            opponentFriendlyGrid.get(cellIndex).getShipType(), 
            request.getArguments().get(0)
        );

        if (hitEvent.getShipType() != null) {
            gameStateHandlerService.sunkStateHandler(hitEvent);
        }
        game.setCurrentTurn(opponentId);
        displayUpdateBoards(request.getServer(), game);
    }

    private void displayUpdateBoards(Guild server, Game game) {
        for (GameBoard board : game.getGameBoards()) {
            TextChannel textChannelById = server.getTextChannelById(board.getChannelId());
            if (textChannelById == null) {
                throw new InaccessibleChannelException("Failed to retrieve TextChannel with given id");
            }
            textChannelById.sendMessageEmbeds(boardDisplayService.displayBoard(board)).queue();
        }
    }

    private int coordinateToCellIndex(String coordinates) {
        Matcher matcher = Pattern.compile("^([a-jA-J])(\\d)$")
            .matcher(coordinates);
        if (!matcher.find()) {
            return -1;
        }

        Rows enumValue = Utils.enumValue(Rows.class, matcher.group(1));
        if (enumValue == null) {
            return -1;
        }
        int letter = enumValue.ordinal();
        int number = Integer.parseInt(matcher.group(2));

        return (letter * Utils.V_ROW_INCREMENT) + number;
    }

    private String findOpponentId(Game game, String currentPlayerId) {
        return game.getPlayerOne().equals(currentPlayerId) ? game.getPlayerTwo() : game.getPlayerOne();
    }

    private enum Rows {
        A, B, C, D, E, F, G, H, I, J;
    }
}