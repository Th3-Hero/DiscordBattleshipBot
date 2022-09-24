package com.th3hero.discordbattleshipbot.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.th3hero.discordbattleshipbot.objects.CommandRequest;
import com.th3hero.discordbattleshipbot.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Service
@AllArgsConstructor
public class FireService {

    public void fireHandling(CommandRequest request) {
        int cellIndex = coordinateToCellIndex(request.getArguments().get(0));
        if (cellIndex == -1) {
            request.getMessage().reply(request.getArguments().get(0) + " is not a valid gridsquare").queue();
            return;
        }
        
        // stuff
    }

    private int coordinateToCellIndex(String coordinates) {
        Matcher matcher = Pattern.compile("^([a-jA-J])(\\d)$")
            .matcher(coordinates);
        if (!matcher.find()) {
            return -1;
        }

        int letter = Utils.enumValue(Letters.class, matcher.group(1)).getRow();
        int number = Integer.parseInt(matcher.group(2));

        return (letter * 10) + number;
    }

    @Getter
    @RequiredArgsConstructor
    private enum Letters {
        A(0),
        B(1),
        C(2),
        D(3),
        E(4),
        F(5),
        G(6),
        H(7),
        I(8),
        J(9);

        private final int row;
    }
}