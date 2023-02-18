package com.th3hero.discordbattleshipbot.controllers;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Controller;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import com.th3hero.discordbattleshipbot.services.GameCreatorService;
import com.th3hero.discordbattleshipbot.services.GameStateHandlerService;
import com.th3hero.discordbattleshipbot.services.ShipPlacementService;
import com.th3hero.discordbattleshipbot.objects.ButtonRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ButtonController extends ListenerAdapter {
    private final GameCreatorService gameCreator;
    private final ShipPlacementService shipPlacementService;
    private final GameStateHandlerService gameStateHandlerService;

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
            event.getChannel().sendMessage("How did you mess up clicking a button? Cause you definitely broke something...").queue();
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