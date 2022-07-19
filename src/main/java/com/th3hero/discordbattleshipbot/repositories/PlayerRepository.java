package com.th3hero.discordbattleshipbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.th3hero.discordbattleshipbot.jpa.entities.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {

    
}