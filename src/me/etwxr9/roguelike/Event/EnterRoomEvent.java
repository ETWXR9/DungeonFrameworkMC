package me.etwxr9.roguelike.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.etwxr9.roguelike.DungeonUtil.DungeonInfo;
import me.etwxr9.roguelike.DungeonUtil.RoomInfo;
import me.etwxr9.roguelike.Game.DungeonTour;
import me.etwxr9.roguelike.Game.TourManager;

public class EnterRoomEvent extends Event {

    public EnterRoomEvent(int row, int pos, DungeonTour tour, DungeonInfo di, RoomInfo ri, int roomIndex,
            Player player) {
        this.row = row;
        this.pos = pos;
        this.dt = tour;
        this.di = di;
        this.ri = ri;
        this.roomIndex = roomIndex;
        this.player = player;
    }

    private int row;
    private int pos;
    private DungeonTour dt;
    private DungeonInfo di;
    private RoomInfo ri;
    private int roomIndex;
    private Player player;

    public int getRow() {
        return row;
    }

    public int getPos() {
        return pos;
    }

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
