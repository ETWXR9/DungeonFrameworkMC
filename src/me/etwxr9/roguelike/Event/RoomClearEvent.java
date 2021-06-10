package me.etwxr9.roguelike.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.etwxr9.roguelike.DungeonUtil.DungeonInfo;
import me.etwxr9.roguelike.DungeonUtil.RoomInfo;
import me.etwxr9.roguelike.Game.DungeonTour;

public class RoomClearEvent extends Event {

    public RoomClearEvent(DungeonTour dt, DungeonInfo di, RoomInfo ri) {
        this.dt = dt;
        this.di = di;
        this.ri = ri;
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

    private DungeonTour dt;
    private DungeonInfo di;
    private RoomInfo ri;

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
