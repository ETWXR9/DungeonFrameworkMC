package me.etwxr9.roguelike.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import me.etwxr9.roguelike.DungeonUtil.DungeonManager;
import me.etwxr9.roguelike.DungeonUtil.LuaLoader;
import me.etwxr9.roguelike.DungeonUtil.RoomInfo;
import me.etwxr9.roguelike.Event.EnterRoomEvent;
import me.etwxr9.roguelike.Event.LeaveRoomEvent;

public class TourManager implements Listener {
    public static List<DungeonTour> Tours = new ArrayList<DungeonTour>();

    public static DungeonTour NewTour(Player p) {
        var tour = new DungeonTour();
        Tours.add(tour);
        tour.player.add(p);
        // 加载LUAAPI
        tour.global.load("LuaAPI = luajava.bindClass([[me.etwxr9.roguelike.DungeonUtil.LuaAPI]])").call();

        // 加载全局lua逻辑
        LuaLoader.GameLuas.forEach((k, v) -> {
            tour.global.load(v).call();
            var gameLuaInstance = tour.global.get(k);
            tour.luaMap.put(k, gameLuaInstance);
            gameLuaInstance.get("init").call(CoerceJavaToLua.coerce(tour));
        });

        return tour;

    }

    public static DungeonTour GetTour(Player p) {
        for (DungeonTour t : Tours) {
            if (t.player.contains(p)) {
                return t;
            }
        }
        return null;
    }

    public static void EndTour(DungeonTour tour) {
        if (tour == null) {
            return;
        }
        tour.dynamicLuaMap.forEach((name, lua) -> {
            LuaListenerManager.UnRegisterEvent(tour, name);
        });
        LuaListenerManager.UnRegisterEvent(tour);
        TourManager.Tours.remove(tour);

    }

    public static void EnableDynamicLua(DungeonTour tour, String itemName) {
        var p = tour.GetFirstPlayer();
        try {
            // p.sendMessage("读取lua: " + LuaLoader.DynamicLuas.get(itemName));
            tour.global.load(LuaLoader.DynamicLuas.get(itemName)).call();
            var lua = tour.global.get(itemName);
            tour.dynamicLuaMap.put(itemName, lua);
            lua.get("init").call();
        } catch (Exception e) {
            p.sendMessage("读取物品 " + itemName + " lua出错！" + e.getMessage());
        }
    }

    public static void DisableDynamicLua(DungeonTour tour, String luaName) {

        var closeLua = tour.dynamicLuaMap.get(luaName);
        if (closeLua == LuaValue.NIL || closeLua == null) {
            // tour.GetFirstPlayer().sendMessage("卸载房间lua " + luaName);
            return;
        }
        if (closeLua.get("close") != LuaValue.NIL) {
            closeLua.get("close").call(closeLua);
        }
        LuaListenerManager.UnRegisterEvent(tour, luaName);
        tour.global.set(luaName, LuaValue.NIL);
        // tour.global.get("collectgarbage").call("collect");
        tour.dynamicLuaMap.remove(luaName);
        // tour.GetFirstPlayer().sendMessage("卸载成功 " + luaName);

    }

    // 房间防冲突 -1为房间人满
    public static int GetFreeRoom(RoomInfo ri) {
        var list = new ArrayList<int[]>();

        // 取得已经占用的房间
        Tours.forEach(t -> {
            if (t.room == null) {
                return;
            }
            if (t.room.equals(ri)) {
                list.add(t.GetRoomPosition());
            }
        });
        if (list.size() == 0) {
            return 0;
        }
        for (int i = 0; i < ri.Rooms.size(); i++) {
            for (int[] pos : list) {
                if (!Arrays.equals(pos, ri.Rooms.get(i))) {
                    return i;
                }
            }
        }
        return -1;

    }

    public static void EnterRoom(DungeonTour tour, RoomInfo ri) {

        var p = tour.GetFirstPlayer();
        var dungeon = DungeonManager.GetDungeonInfo(ri.DungeonId);
        // 选取Free房间
        int index = GetFreeRoom(ri);
        if (index == -1) {
            p.sendMessage("该房间所有副本都已被占用");
            return;
        }

        // 触发离开房间事件
        LeaveRoomEvent lre = new LeaveRoomEvent(tour, dungeon, ri, tour.roomIndex, p);
        Bukkit.getPluginManager().callEvent(lre);

        // 卸载房间lua
        if (tour.room != null) {
            DisableDynamicLua(tour, tour.room.Id);
        }

        var point = DungeonManager.GetPoint(dungeon, ri.Rooms.get(index), ri.PlayerPosition);
        var world = Bukkit.getWorld(dungeon.Id);
        tour.player.forEach(player -> {
            player.teleport(new Location(world, point[0] + 0.5, point[1] + 0.5, point[2] + 0.5));
        });
        tour.dungeon = dungeon;
        tour.room = ri;
        tour.roomIndex = index;

        var luaData = LuaLoader.RoomLuas.get(dungeon.Id).get(ri.Id);
        if (luaData != null) {
            var initLua = tour.global.load(luaData).call();
            initLua = tour.global.get(ri.Id);
            tour.dynamicLuaMap.put(ri.Id, initLua);

            var func = initLua.get("init");
            func.call(CoerceJavaToLua.coerce(tour));
        }

        EnterRoomEvent ere = new EnterRoomEvent(tour, dungeon, ri, index, p);
        Bukkit.getPluginManager().callEvent(ere);
    }

}
