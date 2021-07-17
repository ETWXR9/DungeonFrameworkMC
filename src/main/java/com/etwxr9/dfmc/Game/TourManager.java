package com.etwxr9.dfmc.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.etwxr9.dfmc.Main;
import com.etwxr9.dfmc.Dungeon.DungeonManager;
import com.etwxr9.dfmc.Dungeon.RoomInfo;
import com.etwxr9.dfmc.Event.EnterRoomEvent;
import com.etwxr9.dfmc.Event.LeaveRoomEvent;
import com.etwxr9.dfmc.Lua.DungeonLuaListenerManager;
import com.etwxr9.dfmc.Lua.DungeonLuaManager;
import com.etwxr9.dfmc.Lua.LuaAPI;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class TourManager implements Listener {

    private TourManager() {
    }

    public static TourManager getInstance() {
        return InnerClass.ins;
    }

    private static class InnerClass {
        private static final TourManager ins = new TourManager();
    }

    public List<DungeonTour> Tours = new ArrayList<DungeonTour>();

    public DungeonTour NewTour(Player p, String[] tourConfig) {
        var tour = new DungeonTour();
        Tours.add(tour);
        tour.player.add(p);
        LuaTable luaTourConfig = LuaAPI.ConvertArrayToTable(tourConfig);
        // 加载LuaAPI到环境中
        tour.global.load("LuaAPI = luajava.bindClass([[" + LuaAPI.class.getCanonicalName() + "]])").call();

        try {
            // 加载Game目录下的Lua文件
            DungeonLuaManager.getInstance().DungeonPermanentLuas.forEach((k, v) -> {
                tour.global.load(v).call();
                var gameLuaInstance = tour.global.get(k);
                tour.luaMap.put(k, gameLuaInstance);
            });
            // 对lua内的initOrder变量进行排序，以确定加载顺序
            var sortList = new LinkedList<Entry<String, LuaValue>>(tour.luaMap.entrySet());
            Collections.sort(sortList, new Comparator<Entry<String, LuaValue>>() {
                @Override
                public int compare(Entry<String, LuaValue> e1, Entry<String, LuaValue> e2) {
                    var o1 = e1.getValue();
                    var o2 = e2.getValue();
                    // p.sendMessage("比较lua:" + o1 + "," + o2);
                    if (o1 == LuaValue.NIL || o2 == LuaValue.NIL) {
                        return o1 == LuaValue.NIL ? 1 : -1;
                    }

                    var o1number = o1.get("initOrder");
                    var o2number = o2.get("initOrder");
                    if (o1number != LuaValue.NIL && o2number != LuaValue.NIL) {
                        return o1number.toint() - o2number.toint();
                    } else if (o1number == LuaValue.NIL && o2number == LuaValue.NIL) {
                        return 0;
                    } else {
                        return o1number == LuaValue.NIL ? 1 : -1;
                    }
                }
            });
            tour.luaMap.clear();
            for (Entry<String, LuaValue> entry : sortList) {
                tour.luaMap.put(entry.getKey(), entry.getValue());
            }
            // 对排序后的lua，依次执行init(tour,config)
            tour.luaMap.forEach((k, v) -> {
                if (v.get("init") == LuaValue.NIL) {
                    Main.getInstance().getLogger().log(Level.SEVERE, "lua脚本" + k + " 未找到init函数！");
                }
                // Main.getInstance().getLogger().info("执行lua脚本" + k);

                v.get("init").call(CoerceJavaToLua.coerce(tour), luaTourConfig);

            });
        } catch (Exception e) {
            // 如果出错，注销事件，移除Tour，不执行close函数，防止空指针
            e.printStackTrace();
            new BukkitRunnable() {
                @Override
                public void run() {
                    tour.dynamicLuaMap.forEach((name, lua) -> {
                        DungeonLuaListenerManager.getInstance().UnRegisterDynamaicAllEvent(tour, name);
                    });
                    DungeonLuaListenerManager.getInstance().UnRegisterTourEvent(tour);
                }
            }.runTaskLater(Main.getPlugin(Main.class), 1);
            Tours.remove(tour);
        }
        return tour;
    }

    public DungeonTour GetTour(Player p) {
        for (DungeonTour t : Tours) {
            if (t.player.contains(p)) {
                return t;
            }
        }
        return null;
    }

    public void EndTour(DungeonTour tour) {
        if (tour == null) {
            return;
        }
        tour.dynamicLuaMap.forEach((k, v) -> {
            if (v.get("close") != LuaValue.NIL) {
                v.get("close").call();
            }
        });
        tour.luaMap.forEach((k, v) -> {
            if (v.get("close") != LuaValue.NIL) {
                v.get("close").call();
            }
        });
        new BukkitRunnable() {
            @Override
            public void run() {
                tour.dynamicLuaMap.forEach((name, lua) -> {
                    DungeonLuaListenerManager.getInstance().UnRegisterDynamaicAllEvent(tour, name);
                });
                DungeonLuaListenerManager.getInstance().UnRegisterTourEvent(tour);
            }
        }.runTaskLater(Main.getPlugin(Main.class), 1);

        Tours.remove(tour);
    }

    public void EnableDynamicLua(DungeonTour tour, String itemName) {
        var p = tour.GetFirstPlayer();
        try {
            // p.sendMessage("读取lua: " + LuaLoader.DynamicLuas.get(itemName));
            tour.global.load(DungeonLuaManager.getInstance().DungeonDynamicLuas.get(itemName)).call();
            var lua = tour.global.get(itemName);
            tour.dynamicLuaMap.put(itemName, lua);
            lua.get("init").call();
        } catch (Exception e) {
            p.sendMessage("读取物品 " + itemName + " lua出错！" + e.getMessage());
        }
    }

    public void DisableDynamicLua(DungeonTour tour, String luaName) {

        var closeLua = tour.dynamicLuaMap.get(luaName);
        if (closeLua == LuaValue.NIL || closeLua == null) {
            // tour.GetFirstPlayer().sendMessage("卸载房间lua " + luaName);
            return;
        }
        if (closeLua.get("close") != LuaValue.NIL) {
            closeLua.get("close").call(closeLua);
        }
        DungeonLuaListenerManager.getInstance().UnRegisterDynamaicAllEvent(tour, luaName);
        tour.global.set(luaName, LuaValue.NIL);
        // tour.global.get("collectgarbage").call("collect");
        tour.dynamicLuaMap.remove(luaName);
        // tour.GetFirstPlayer().sendMessage("卸载成功 " + luaName);

    }

    // 房间防冲突 -1为房间人满
    public int GetFreeRoom(RoomInfo ri) {
        var list = new ArrayList<Integer>();

        // 取得已经占用的房间
        Tours.forEach(t -> {
            if (t.room == null) {
                return;
            }
            if (t.room.equals(ri)) {
                Bukkit.getLogger()
                        .info("玩家" + t.GetFirstPlayer().getName() + "在进入房间" + ri.Id + "时，已经占用房间" + t.roomIndex);
                list.add(t.roomIndex);
            }
        });
        if (list.size() == 0) {
            return 0;
        }
        for (int i = 0; i < ri.Rooms.size(); i++) {
            if (!list.contains(i)) {
                return i;
            }
            // for (int[] pos : list) {
            // if (!Arrays.equals(pos, ri.Rooms.get(i))) {
            // return i;
            // }
            // }
        }
        return -1;

    }

    public void EnterRoom(DungeonTour tour, RoomInfo ri) {

        var p = tour.GetFirstPlayer();
        var dungeon = DungeonManager.GetDungeonInfo(ri.DungeonId);
        // 选取Free房间
        int index = GetFreeRoom(ri);
        if (index == -1) {
            p.sendMessage("该房间所有副本都已被占用");
            return;
        }
        Bukkit.getLogger().info("玩家" + p.getName() + "进入房间" + ri.Id + "且坐标为" + index);
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
        var luaData = DungeonLuaManager.getInstance().DungeonRoomLuas.get(dungeon.Id).get(ri.Id);
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
