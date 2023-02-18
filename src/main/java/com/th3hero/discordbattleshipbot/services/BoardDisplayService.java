package com.th3hero.discordbattleshipbot.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.jpa.entities.GameBoard;
import com.th3hero.discordbattleshipbot.utils.EmbedBuilderFactory;
import com.th3hero.discordbattleshipbot.utils.Utils;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Service
@RequiredArgsConstructor
public class BoardDisplayService {

    public List<MessageEmbed> displayBoard(GameBoard board) {
        return cellsToUnicodeGrid(board);
    }

    public List<MessageEmbed> displayStartingBoard(Guild server, GameBoard board) {
        List<MessageEmbed> embeds = cellsToUnicodeGrid(board);
        embeds.add(0, EmbedBuilderFactory.boardHeader(Utils.playerNames(server, board.getGame())));

        return embeds;
    }

    private List<MessageEmbed> cellsToUnicodeGrid(GameBoard board) {
        List<String> columnList = List.of("🇦", "🇧", "🇨", "🇩", "🇪", "🇫", "🇬", "🇭", "🇮", "🇯");
        List<String> rowsList = List.of("🟦", "0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣");
        StringBuilder columns = new StringBuilder();
        rowsList.forEach(columns::append);
        StringBuilder friendlyCellGrid = new StringBuilder();
        StringBuilder enemyCellGrid = new StringBuilder();

        friendlyCellGrid.append(columns);
        enemyCellGrid.append(columns);
        for (int i = 0; i < Utils.MAX_INCLUSIVE_CELLS; i++) {
            if (i % Utils.V_ROW_INCREMENT == 0) {
                friendlyCellGrid.append("\n");
                enemyCellGrid.append("\n");
                friendlyCellGrid.append(columnList.get(i/Utils.V_ROW_INCREMENT));
                enemyCellGrid.append(columnList.get(i/Utils.V_ROW_INCREMENT));
            }

            switch (board.getFriendlyCells().get(i).getCellStatus()) {
                case EMPTY -> friendlyCellGrid.append("🟦");
                case SHIP -> friendlyCellGrid.append("⬛");
                case HIT -> friendlyCellGrid.append("❌");
                case MISS -> friendlyCellGrid.append("❕");
            }

            switch (board.getEnemyCells().get(i).getCellStatus()) {
                case EMPTY -> enemyCellGrid.append("🟦");
                case HIT -> enemyCellGrid.append("❌");
                case MISS -> enemyCellGrid.append("❕");
            }
        }

        return EmbedBuilderFactory.boardDisplay(friendlyCellGrid.toString(), enemyCellGrid.toString());
    }
}