package com.th3hero.discordbattleshipbot.controllers;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Controller;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import com.th3hero.discordbattleshipbot.services.FireService;
import com.th3hero.discordbattleshipbot.services.GameCreatorService;
import com.th3hero.discordbattleshipbot.services.GameHandlerService;
import com.th3hero.discordbattleshipbot.services.GameStateHandlerService;
import com.th3hero.discordbattleshipbot.services.ShipPlacementService;
import com.th3hero.discordbattleshipbot.objects.ButtonRequest;
import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.utils.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController extends ListenerAdapter {
    private final GameCreatorService gameCreator;
    private final GameHandlerService gameHandlerService;
    private final ShipPlacementService shipPlacementService;
    private final FireService fireService;
    private final GameStateHandlerService gameStateHandlerService;

    @Override
    public void onMessageReceived(@Nonnull final MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { // Ignore any message sent by a bot
            return;
        }

        try {
            final CommandRequest request = CommandRequest.create(event.getMessage());
            if (!request.isValidToken() || request.getCommand() == null) {
                return;
            }
            commandHandler(request);

        } catch (Exception e) {
            log.error("onMessageReceived", e);
            event.getChannel().sendMessage("If you're seeing this Hero is bad at coding. Which we already knew!").queue();
        }
    }

    @Override
    public void onButtonClick(@Nonnull final ButtonClickEvent event) {
        try {
            final ButtonRequest request = ButtonRequest.create(event);
            if (request.getAction() == null) {
                return;
            }
            buttonHandler(request);

        } catch (Exception e) {
            log.error("onButtonClick", e);
            event.getChannel().sendMessage("Something went impossibly wrong... well I guess it was possible").queue();
        }
    }

    /**
     * Directs all text commands
     * @param request
     */
    public void commandHandler(final CommandRequest request) {
        switch (request.getCommand()) {
            case PING -> Ping.pingBot(request.getChannel());
            case HELP -> Help.displayHelpMessage(request.getChannel());
            case CHALLENGE -> gameCreator.gameRequest(request);
            case DELETE -> gameHandlerService.deleteGame(request);
            case FIRE -> fireService.fireHandling(request);
        }
    }

    /**
     * Directs all button events
     * @param request
     */
    public void buttonHandler(final ButtonRequest request) {
        switch (request.getAction()) {
            case ACCEPT -> gameCreator.acceptGame(request);
            case DECLINE -> gameCreator.declineGame(request);
            case RANDOMIZE -> shipPlacementService.shipPlacementRandomizeExisting(request);
            case READY -> gameStateHandlerService.readyStateHandler(request);
            case UN_READY -> gameStateHandlerService.readyStateHandler(request);
            case CLOSE_GAME -> gameStateHandlerService.closeGame(request);
        }
    }

}