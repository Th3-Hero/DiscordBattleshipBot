package com.th3hero.discordbattleshipbot.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.exceptions.DiscordNullReturnException;
import com.th3hero.discordbattleshipbot.jpa.entities.FriendlyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.jpa.entities.GameBoard;
import com.th3hero.discordbattleshipbot.jpa.entities.Game.GameStatus;
import com.th3hero.discordbattleshipbot.objects.ButtonRequest;
import com.th3hero.discordbattleshipbot.objects.HitEvent;
import com.th3hero.discordbattleshipbot.objects.Placement.Ship;
import com.th3hero.discordbattleshipbot.utils.EmbedBuilderFactory;
import com.th3hero.discordbattleshipbot.utils.FindUtil;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;

@Service
@Transactional
@RequiredArgsConstructor
public class GameStateHandlerService {
    private final GameHandlerService gameHandlerService;

    public void sunkStateHandler(HitEvent hitEvent) {

        List<FriendlyCell> opponentFriendlyGrid = FindUtil.findGameboardByPlayerId(hitEvent.getGame(), hitEvent.getOpponent().getId()).getFriendlyCells();
        Ship currentShip = hitEvent.getShipHit();
        if (isSunk(opponentFriendlyGrid, currentShip)) {
            shipSunk(hitEvent);
        }
        if (allSunk(opponentFriendlyGrid)) {
            endGame(hitEvent);
        }
    }

    public void readyStateHandler(ButtonRequest request) {
        Game game = gameHandlerService.fetchGameByChannelId(request.getChannel().getId());
        int gameId = game.getGameId();

        // If action is ready
        if (request.getAction().equals(ButtonRequest.ClickEvent.READY)) {
            GameBoard board = FindUtil.findGameboardByPlayerId(game, request.getUser().getId());
            // ready up the player
            board.setPlayerReady(true);


            // check if both players are ready
            boolean bothPlayersReady = game.getGameBoards().stream().allMatch(status -> status.getPlayerReady().equals(true));
            if (bothPlayersReady) {
                // if both players are ready start the game
                request.getEvent().editMessageEmbeds(request.getMessage().getEmbeds())
                    .setActionRows().queue();
                startGame(request, game);
            }
            else {
                request.getEvent().editMessageEmbeds(request.getMessage().getEmbeds())
                    .setActionRow(Button.danger(gameId + "-UN_READY", "Unready")).queue();
            }
        }
        // if action is un_ready
        if (request.getAction().equals(ButtonRequest.ClickEvent.UN_READY)) {
            GameBoard board = FindUtil.findGameboardByPlayerId(game, request.getUser().getId());
            // unready the player
            board.setPlayerReady(false);
            request.getEvent().editMessageEmbeds(request.getMessage().getEmbeds())
                .setActionRow(
                    Button.secondary(game.getGameId() + "-RANDOMIZE", "🎲 Randomize 🎲"),
                    Button.success(game.getGameId() + "-READY", "Ready Up")
                ).queue();
        }
    }

    private void startGame(ButtonRequest request, Game game) {
        Guild server = request.getServer();
        for (GameBoard board : game.getGameBoards()) {
            TextChannel textChannelById = server.getTextChannelById(board.getChannelId());
            if (textChannelById == null) {
                throw new DiscordNullReturnException("Failed to retrieve TextChannel when attemping to send starting embeds");
            }
            textChannelById.sendMessageEmbeds(
                EmbedBuilderFactory.gameStart(board.getPlayer().getPlayerId().equals(game.getCurrentTurn()))
                ).queue();
            }
        game.setGameStatus(GameStatus.ACTIVE);
    }

    public void closeGame(ButtonRequest request) {
        gameHandlerService.deleteGame(request.getChannel().getId(), request.getServer());
    }

    private void shipSunk(HitEvent hitEvent) {
        Guild server = hitEvent.getServer();
        Member memberWhoSunkById = server.getMemberById(hitEvent.getCurrentPlayer().getId());
        if (memberWhoSunkById == null) {
            throw new DiscordNullReturnException("Failed to retrieve Member who got sunk");
        }
        String playerWhoSunkName = memberWhoSunkById.getEffectiveName();
        Member memberWhoGotSunkById = server.getMemberById(hitEvent.getOpponent().getId());
        if (memberWhoGotSunkById == null) {
            throw new DiscordNullReturnException("Failed to retrieve Member who sunk opponent");
        }
        String playerWhoGotSunkName = memberWhoGotSunkById.getEffectiveName();
        String cellHit = hitEvent.getHitSquare();
        MessageEmbed embed = EmbedBuilderFactory.shipSunkEmbed(playerWhoSunkName, playerWhoGotSunkName, hitEvent.getShipHit(), cellHit);
        for (GameBoard board : hitEvent.getGame().getGameBoards()) {
            TextChannel textChannelById = server.getTextChannelById(board.getChannelId());
            if (textChannelById == null) {
                throw new DiscordNullReturnException("Failed to retrieve TextChannel when attempting to send sunk embeds");
            }
            textChannelById.sendMessageEmbeds(embed).queue();
        }
    }

    private void endGame(HitEvent hitEvent) {
        Game game = hitEvent.getGame();
        game.setGameStatus(GameStatus.ENDED);
        game.setCurrentTurn(null);

        Guild server = hitEvent.getServer();
        Member member = server.getMember(hitEvent.getCurrentPlayer());
        if (member == null) {
            throw new DiscordNullReturnException("Failed to retrieve Member");
        }
        String winnerName = member.getEffectiveName();
        MessageEmbed embed = EmbedBuilderFactory.gameOver(winnerName);
        for (GameBoard board: hitEvent.getGame().getGameBoards()) {
            TextChannel textChannelById = server.getTextChannelById(board.getChannelId());
            if (textChannelById == null) {
                throw new DiscordNullReturnException("Failed to retrieve TextChannel when attemping to send winner embed");
            }
            textChannelById.sendMessageEmbeds(embed)
                .setActionRow(
                    Button.danger(game.getGameId() + "-CLOSE_GAME", "❌ Close Game ❌")
                )
                .queue();
        }
    }

    private boolean isSunk(List<FriendlyCell> grid, Ship ship) {
        List<FriendlyCell> shipCells = grid.stream().filter(cell -> ship.equals(cell.getShipType())).toList();
        // Check if ship of that type sunk
        if (Boolean.TRUE.equals(shipCells.get(0).getSunk())) {
            // Return true if it is
            return true;
        }
        boolean allHit = shipCells.stream().allMatch(cell -> cell.getCellStatus().equals(FriendlyCell.CellStatus.HIT));
        // If not, check if all cells are HIT
        if (allHit) {
            // If all cells are HIT set status to sunk and return true
            shipCells.forEach(cell -> cell.setSunk(true));
            return true;
        }
        // If all cells are not HIT return false
        return false;
    }

    private boolean allSunk(List<FriendlyCell> grid) {
        List<FriendlyCell> ships = grid.stream().filter(cell -> cell.getShipType() != null).toList();
        return ships.stream().allMatch(cell -> cell.getSunk());
    }
}