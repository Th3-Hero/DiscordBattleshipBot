package com.th3hero.discordbattleshipbot.controllers;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Controller;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SlashController extends ListenerAdapter {

    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        // text
        
    }

}