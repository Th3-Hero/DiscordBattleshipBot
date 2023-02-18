package com.th3hero.discordbattleshipbot.objects;

import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.objects.Placement.Ship;

import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

@Getter
@Builder
public class ShotEvent {
    private Guild server;
    private User currentPlayer;
    private User opponent;
    private Game game;
    private Ship shipType;
    private String hitSquare;


    public static ShotEvent createEvent(
        final Guild server,
        final User currentPlayer,
        final User opponent,
        final Game game,
        final Ship shipType,
        final String hitSquare
    ) {
        return ShotEvent.builder()
            .server(server)
            .currentPlayer(currentPlayer)
            .opponent(opponent)
            .game(game)
            .shipType(shipType)
            .hitSquare(hitSquare)
            .build();
    }
}