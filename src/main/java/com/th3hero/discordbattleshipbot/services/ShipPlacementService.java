package com.th3hero.discordbattleshipbot.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.jpa.entities.FriendlyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.jpa.entities.GameBoard;
import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.objects.Placement;
import com.th3hero.discordbattleshipbot.objects.Placement.Direction;
import com.th3hero.discordbattleshipbot.objects.Placement.Ship;
import com.th3hero.discordbattleshipbot.repositories.GameRepository;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;


@Service
@AllArgsConstructor
@Transactional
public class ShipPlacementService {
    private GameHandlerService gameHandlerService;
    private GameRepository gameRepository;
    private GameCreatorService gameCreatorService;

    public void shipPlacementHandeler(CommandRequest request) {
        Guild server = request.getServer();
        Game game = gameHandlerService.fetchGameByChannel(request.getChannel().getId());
        List<GameBoard> boards = game.getGameBoards();
        GameBoard gameBoard = boards.stream().filter(board -> board.getChannelId().equals(request.getChannel().getId())).findFirst().orElse(null);
        if (gameBoard == null) {
            return;
        }

        List<Ship> ships = new ArrayList<>(Arrays.asList(Ship.values()));

        for (Ship ship : ships) {
            boolean placed = false;
            while (!placed) {
                Placement placement = Placement.createRandom(ship.getShipSize());
                placed = placeShip(gameBoard, placement);
            }

        }

        gameRepository.save(game);
        gameCreatorService.displayCellsToUnicodeGrid(server, request.getChannel().getId(), gameBoard);
        
    }

    private boolean placeShip(GameBoard gameBoard, Placement placement) {
        List<FriendlyCell> cellList = gameBoard.getFriendlyCells();
        int shipSize = placement.getShipSize();
        Direction shipDirection = placement.getDirection();
        int shipFrontIndex = placement.getCellIndex();

        if (placement.getDirection().equals(Placement.Direction.HORIZONTAL)) {
            double shipFront = shipFrontIndex + 1.0;
            double shipEnd = (shipFrontIndex + shipSize) + 1.0;
            if (!sameRow(shipFront, shipEnd)) {
                return false;
            }
        }


        List<FriendlyCell> validPlacement = new ArrayList<>();
        for (int i = 0; i < shipSize; i++) {
            int cellIndex = shipFrontIndex + (shipDirection.getValue() * i);
            // Check if cell is within bounds
            if (cellIndex > 99 || cellIndex < 0) {
                return false;
            }

            // if horizontal
            if (shipDirection == Direction.HORIZONTAL) {
                // if front cell of ship
                if (i == 0
                    // check if the cell in front of ship is in bounds(false if cellIndex is 0, cellIndex -1 doesn't exist)
                    && cellIndex - 1 >= 0
                    // Prevent check if it will wrap to next row
                    && sameRow(cellIndex, cellIndex - 1.0)
                    // check if that cell in front of ship is another ship
                    && cellList.get(cellIndex - 1).getCellStatus().equals(FriendlyCell.CellStatus.SHIP)) {
                    return false;
                }
                // if back cell of ship
                if (i == (shipSize - 1)
                    // check if cell behind ship is in bounds(will trigger if cellIndex is 99, cellIndex 100 doesn't exist)
                    && (cellIndex + 1) <= 99
                    // Prevent check if it will wrap to next row
                    && sameRow(cellIndex, cellIndex + 1.0)
                    // check if cell behind ship is another ship
                    && cellList.get(cellIndex + 1).getCellStatus().equals(FriendlyCell.CellStatus.SHIP)) {
                    return false;
                }

                // Below cell is inbounds and blocked by another ship
                if ((cellIndex + 10 <= 99) && cellList.get(cellIndex + 10).getCellStatus().equals(FriendlyCell.CellStatus.SHIP)) {
                    return false;
                }
                // Above cell is inbounds and blocked by another ship
                if ((cellIndex - 10 >= 0) && cellList.get(cellIndex - 10).getCellStatus().equals(FriendlyCell.CellStatus.SHIP)) {
                    return false;
                }
            }
            // if vertical
            if (shipDirection == Direction.VERTICAL) {
                // if front cell of ship
                if (i == 0
                    // check if the cell in front of ship is in bounds
                    && (cellIndex - 10) >= 0
                    // check if that cell in front of ship is another ship
                    && cellList.get(cellIndex - 10).getCellStatus().equals(FriendlyCell.CellStatus.SHIP)) {
                    return false;
                }
                // if back cell of ship
                if (i == (shipSize - 1)
                    // check if cell behind ship is in bounds
                    && (cellIndex + 10) <= 99
                    // check if cell behind ship is another ship
                    && cellList.get(cellIndex + 10).getCellStatus().equals(FriendlyCell.CellStatus.SHIP)) {
                    return false;
                }

                // Left Side blocked
                if ((cellIndex - 1) >= 0 
                    // Prevent check if it will wrap to next row
                    && sameRow(cellIndex, cellIndex - 1.0)
                    && cellList.get(cellIndex - 1).getCellStatus().equals(FriendlyCell.CellStatus.SHIP)) {
                    return false;
                }
                // Right Side blocked
                if ((cellIndex + 1) <= 99 
                    // Prevent check if it will wrap to next row
                    && sameRow(cellIndex, cellIndex + 1.0)
                    && cellList.get(cellIndex + 1).getCellStatus().equals(FriendlyCell.CellStatus.SHIP)) {
                    return false;
                }
            }

            FriendlyCell cell = cellList.get(cellIndex);
            if (cell.getCellStatus().equals(FriendlyCell.CellStatus.SHIP)) {
                return false;
            }
            validPlacement.add(cell);
        }
        // Only actually place the ship if all checks pass
        validPlacement.forEach(cell -> cell.setCellStatus(FriendlyCell.CellStatus.SHIP));
        return true;

    }

    /**
     * Check if two cells are on the same row
     * @param indexOne index of first cell
     * @param indexTwo index of second cell
     * @return <pre><code>boolean</code></pre>
     */
    private boolean sameRow(double indexOne, double indexTwo) {
        double firstRow = Math.ceil(indexOne / 10.0);
        double secondRow = Math.ceil(indexTwo / 10.0);
        return firstRow == secondRow;
    }
}