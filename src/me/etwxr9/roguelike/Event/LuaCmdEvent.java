package me.etwxr9.roguelike.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.luaj.vm2.LuaTable;

import me.etwxr9.roguelike.DungeonUtil.DungeonInfo;
import me.etwxr9.roguelike.DungeonUtil.RoomInfo;
import me.etwxr9.roguelike.Game.DungeonTour;

public class LuaCmdEvent extends Event {
    public LuaCmdEvent(DungeonTour tour, DungeonInfo di, RoomInfo ri, int roomIndex, Player player, LuaTable args) {
        this.dt = tour;
        this.di = di;
        this.ri = ri;
        this.roomIndex = roomIndex;
        this.player = player;
        this.args = args;
    }

    private DungeonTour dt;
    private DungeonInfo di;
    private RoomInfo ri;
    private int roomIndex;
    private Player player;
    private LuaTable args;

    public LuaTable getArgs() {
        return args;
    }

    public DungeonTour getDungeonTour() {
        return dt;
    }

    public DungeonInfo getDungeonInfo() {
        return di;
    }

    public RoomInfo getRoomInfo() {
        return ri;
    }

    public int getRoomIndex() {
        return roomIndex;
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
