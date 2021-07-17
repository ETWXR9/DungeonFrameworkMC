package com.etwxr9.dfmc.Event;

import com.etwxr9.dfmc.Dungeon.DungeonInfo;
import com.etwxr9.dfmc.Dungeon.RoomInfo;
import com.etwxr9.dfmc.Game.DungeonTour;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EnterRoomEvent extends Event {

    public EnterRoomEvent(DungeonTour tour, DungeonInfo di, RoomInfo ri, int roomIndex, Player player) {
        this.dt = tour;
        this.di = di;
        this.ri = ri;
        this.roomIndex = roomIndex;
        this.player = player;
    }

    private DungeonTour dt;
    private DungeonInfo di;
    private RoomInfo ri;
    private int roomIndex;
    private Player player;

    public DungeonTour getDungeonTour() {
        return dt;
    }

    public DungeonInfo getdDungeonInfo() {
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
