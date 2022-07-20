package com.th3hero.discordbattleshipbot.enums;

import java.util.EnumSet;

import net.dv8tion.jda.api.Permission;

public class ChannelPermissions {

    public static EnumSet<Permission> allow() {
        return EnumSet.of(
            Permission.VIEW_CHANNEL,
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_HISTORY,
            Permission.USE_SLASH_COMMANDS
        );
    }

    public static EnumSet<Permission> deny() {
        return EnumSet.of(
            Permission.MANAGE_CHANNEL
        );
    }
    
}