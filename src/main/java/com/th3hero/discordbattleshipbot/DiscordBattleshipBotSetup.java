package com.th3hero.discordbattleshipbot;

import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.th3hero.discordbattleshipbot.controllers.MessageController;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

@Configuration
@EnableJpaRepositories
public class DiscordBattleshipBotSetup {
    @Value("${app.discord.token}")
    private String token;

    @Bean
    public JDA discordClient(MessageController controller) throws GeneralSecurityException {
        return JDABuilder.createDefault(token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .setChunkingFilter(ChunkingFilter.ALL)
            .addEventListeners(controller)
            .setActivity(Activity.playing("Battleship"))
            .build();
    }
}