package com.th3hero.discordbattleshipbot.utils;

import net.dv8tion.jda.api.entities.MessageChannel;

public class Ping {
    public static void pingBot(MessageChannel channel) {
        long time = System.currentTimeMillis();
            channel.sendMessage("Ping... Wait no Pong!") /* => RestAction<Message> */
                   .queue(response /* => Message */ -> {
                       response.editMessageFormat("Ping! \r\n ...Wait no Pong!: `%d ms`", System.currentTimeMillis() - time).queue();
                   });
    }
}