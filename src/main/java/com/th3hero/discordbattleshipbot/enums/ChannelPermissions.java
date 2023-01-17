package com.th3hero.discordbattleshipbot.enums;

import java.util.EnumSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.Permission;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelPermissions {

    public static Set<Permission> allow() {
        return EnumSet.of(
            Permission.VIEW_CHANNEL,
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_HISTORY,
            Permission.USE_SLASH_COMMANDS
        );
    }

    public static Set<Permission> deny() {
        return EnumSet.of(
            Permission.MANAGE_CHANNEL
        );
    }
    
}