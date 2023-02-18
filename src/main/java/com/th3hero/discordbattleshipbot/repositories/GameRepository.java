package com.th3hero.discordbattleshipbot.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.th3hero.discordbattleshipbot.jpa.entities.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    @Query("select g from Game g join g.gameBoards b where g.gameId = b.game.gameId and b.channelId = :channelId")
    Optional<Game> findGameByGameBoardWithChannelId(@Param("channelId") String channelId);
}