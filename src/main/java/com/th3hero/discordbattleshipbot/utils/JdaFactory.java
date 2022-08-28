package com.th3hero.discordbattleshipbot.utils;


import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

@Component
public class JdaFactory {
    @Value("${app.discord.token}")
    private String token;

    /** 
     * Builds a new basic JDA client for lookups
     * 
     * @return
     *      New JDA client
     * @throws General
     *      If discord fails to auth given token
     */
    public JDA jdaLookupClient() throws GeneralSecurityException {
        return JDABuilder.createDefault(token).build();
    }
}