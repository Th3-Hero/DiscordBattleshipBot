package com.th3hero.discordbattleshipbot.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.jpa.entities.EnemyCell;
import com.th3hero.discordbattleshipbot.jpa.entities.FriendlyCell;
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
        List<String> columnList = Arrays.asList("🇦", "🇧", "🇨", "🇩","🇪","🇫","🇬","🇭","🇮","🇯");
        List<String> rowsList = Arrays.asList("🟦", "0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣");
        StringBuilder columns = new StringBuilder();
        rowsList.forEach(columns::append);
        List<FriendlyCell> friendlyCellList = board.getFriendlyCells();
        List<EnemyCell> enemyCellList = board.getEnemyCells();
        StringBuilder friendlyCellGrid = new StringBuilder();
        StringBuilder enemyCellGrid = new StringBuilder();

        friendlyCellGrid.append(columns);
        enemyCellGrid.append(columns);
        for (int i = 0; i < 100; i++) {
            if (i % 10 == 0) {
                friendlyCellGrid.append("\n");
                enemyCellGrid.append("\n");
                friendlyCellGrid.append(columnList.get(i/10));
                enemyCellGrid.append(columnList.get(i/10));
            }

            switch (friendlyCellList.get(i).getCellStatus()) {
                case EMPTY -> friendlyCellGrid.append("🟦");
                case SHIP -> friendlyCellGrid.append("⬛");
                case HIT -> friendlyCellGrid.append("❌");
                case MISS -> friendlyCellGrid.append("❕");
            }

            switch (enemyCellList.get(i).getCellStatus()) {
                case EMPTY -> enemyCellGrid.append("🟦");
                case HIT -> enemyCellGrid.append("❌");
                case MISS -> enemyCellGrid.append("❕");
            }
        }

        return EmbedBuilderFactory.boardDisplay(friendlyCellGrid.toString(), enemyCellGrid.toString());
    }
}