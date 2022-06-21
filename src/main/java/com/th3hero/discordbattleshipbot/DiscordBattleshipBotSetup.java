package com.th3hero.discordbattleshipbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.th3hero.discordbattleshipbot.controllers.MessageController;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

@Configuration
public class DiscordBattleshipBotSetup {
    @Value("${app.discord.token}")
    private String token;
    
    @Bean
    public JDA discordClient(MessageController controller) throws Exception {
        return JDABuilder.createDefault(token)
            .setActivity(Activity.playing("Battleship"))
            .addEventListeners(controller)
            .build();
    }
}