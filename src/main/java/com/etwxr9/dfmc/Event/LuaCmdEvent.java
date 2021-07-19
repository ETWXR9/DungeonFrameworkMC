package com.etwxr9.dfmc.Event;

import com.etwxr9.dfmc.Dungeon.DungeonInfo;
import com.etwxr9.dfmc.Dungeon.RoomInfo;
import com.etwxr9.dfmc.Game.DungeonTour;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.luaj.vm2.LuaTable;

public class LuaCmdEvent extends Event {

    private Player player;
    private LuaTable args;

    public LuaTable getArgs() {
        return args;
    }

    public LuaCmdEvent(Player player, LuaTable args) {
        this.player = player;
        this.args = args;
    }

    public LuaCmdEvent(boolean isAsync, Player player, LuaTable args) {
        super(isAsync);
        this.player = player;
        this.args = args;
    }

    public Player getPlayer() {
        return player;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
