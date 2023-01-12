package com.th3hero.discordbattleshipbot.objects;

import com.th3hero.discordbattleshipbot.jpa.entities.Game;
import com.th3hero.discordbattleshipbot.objects.Placement.Ship;

import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

@Getter
@Builder
public class HitEvent {
    private Guild server;
    private User currentPlayer;
    private User opponent;
    private Game game;
    private Ship shipHit;
    private String hitSquare;


    public static HitEvent createEvent(
        final Guild server, 
        final User currentPlayer, 
        final User opponent, 
        final Game game, 
        final Ship shipHit, 
        final String hitSquare
        ) {
            return HitEvent.builder()
            .server(server)
            .currentPlayer(currentPlayer)
            .opponent(opponent)
            .game(game)
            .shipHit(shipHit)
            .hitSquare(hitSquare)
            .build();
    }
}