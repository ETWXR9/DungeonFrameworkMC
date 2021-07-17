package com.etwxr9.dfmc.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.etwxr9.dfmc.Dungeon.DungeonInfo;
import com.etwxr9.dfmc.Dungeon.RoomInfo;

import org.bukkit.entity.Player;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class DungeonTour {
    public List<Player> player;

    public DungeonInfo dungeon;

    public DungeonTour() {
        this.player = new ArrayList<Player>();
        this.luaMap = new LinkedHashMap<String, LuaValue>();
        this.dynamicLuaMap = new HashMap<String, LuaValue>();
        this.global = JsePlatform.standardGlobals();
    }

    public RoomInfo room;
    public int roomIndex;
    public Map<String, LuaValue> luaMap;
    public Map<String, LuaValue> dynamicLuaMap;
    public Globals global;

    public int[] GetRoomPosition() {
        return room.Rooms.get(roomIndex);
    }

    public Player GetFirstPlayer() {
        return player.get(0);
    }

}