package me.etwxr9.roguelike.Game;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.lib.jse.LuajavaLib;

import me.etwxr9.roguelike.Main;
import me.etwxr9.roguelike.DungeonUtil.DungeonManager;
import me.etwxr9.roguelike.DungeonUtil.LuaAPI;
import me.etwxr9.roguelike.DungeonUtil.LuaLoader;
import me.etwxr9.roguelike.DungeonUtil.RoomInfo;
import me.etwxr9.roguelike.Event.EnterRoomEvent;
import me.etwxr9.roguelike.Event.LeaveRoomEvent;
import me.etwxr9.roguelike.Event.RoomClearEvent;

public class TourManager implements Listener {
    public static Map<Player, DungeonTour> Tours = new HashMap<Player, DungeonTour>();

    public static DungeonTour NewTour(Player p) {
        var tour = new DungeonTour();
        tour.player.add(p);
        Tours.put(p, tour);
        // 加载LUAAPI
        tour.global.load("LuaAPI = luajava.bindClass([[me.etwxr9.roguelike.DungeonUtil.LuaAPI]])").call();
        // 加载全局lua逻辑
        LuaLoader.GameLuas.forEach((k, v) -> {
            tour.global.load(v).call();
            var gameLuaInstance = tour.global.get(k);
            tour.luaMap.put(k, gameLuaInstance);
            gameLuaInstance.get("init").call(gameLuaInstance, CoerceJavaToLua.coerce(tour));
        });

        return tour;

    }

    public static DungeonTour GetTour(Player p) {
        return Tours.get(p);

    }

    public static void EnableLua(DungeonTour tour, Player p, String itemName) {
        try {
            p.sendMessage("读取lua: " + LuaLoader.ItemLuas.get(itemName));
            tour.global.load(LuaLoader.ItemLuas.get(itemName)).call();
            var lua = tour.global.get(itemName);
            tour.luaMap.put(itemName, lua);
            lua.get("init").call(lua, CoerceJavaToLua.coerce(p));
        } catch (Exception e) {
            p.sendMessage("读取物品 " + itemName + " lua出错！" + e.getMessage());
        }
    }

    // 房间防冲突
    public static int GetFreeRoom(RoomInfo ri) {
        var list = new ArrayList<int[]>();

        // 取得已经占用的房间
        Tours.values().forEach(t -> {
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

    public static void EnterRoom(Player p, RoomInfo ri) {
        var tour = GetTour(p);
        if (tour == null) {
            p.sendMessage("游戏不存在！");
            return;
        }
        var dungeon = DungeonManager.GetDungeonInfo(ri.DungeonId);
        LeaveRoomEvent lre = new LeaveRoomEvent(tour, dungeon, ri, tour.roomIndex, p);
        Bukkit.getPluginManager().callEvent(lre);

        // 卸载房间lua
        var closeLua = tour.luaMap.get(ri.Id);
        if (closeLua != LuaValue.NIL && closeLua != null) {
            tour.luaMap.remove(ri.Id, closeLua);
            if (closeLua.get("close") != LuaValue.NIL) {
                closeLua.get("close").call(closeLua);
            }

        }

        // 选取Free房间
        int index = GetFreeRoom(ri);
        if (index == -1) {
            p.sendMessage("该房间所有副本都已被占用");
            return;
        }
        var point = DungeonManager.GetPoint(dungeon, ri.Rooms.get(index), ri.PlayerPosition);
        var world = Bukkit.getWorld(dungeon.Id);

        // p.sendMessage(MessageFormat.format("准备传送至地牢世界：{0}， 房间Id：{1}， 序号：{2}",
        // world.getName(), ri.Id, index));
        // p.sendMessage(MessageFormat.format("传送点 {0}， 房间Id：{1}， 序号：{2}",
        // world.getName(), ri.Id, index));
        if (p.teleport(new Location(world, point[0], point[1], point[2]))) {
            tour.dungeon = dungeon;
            tour.room = ri;
            tour.roomIndex = index;
            // tour.isClear = false;
            // p.sendMessage("传送成功");
        } else {
            p.sendMessage("传送失败");
            return;
        }

        // 加载房间lua
        var luaData = LuaLoader.RoomLuas.get(ri.Id);
        var initLua = tour.global.load(luaData).call();
        initLua = tour.global.get(ri.Id);
        tour.luaMap.put(ri.Id, initLua);

        var func = initLua.get("init");
        func.call(initLua, CoerceJavaToLua.coerce(tour), CoerceJavaToLua.coerce(p));

        EnterRoomEvent ere = new EnterRoomEvent(tour, dungeon, ri, index, p);
        Bukkit.getPluginManager().callEvent(ere);
    }

    // public static void ClearRoom(DungeonTour dt) {
    // RoomClearEvent e = new RoomClearEvent(dt, dt.dungeon, dt.room);
    // Bukkit.getPluginManager().callEvent(e);
    // e.getDungeonTour().EnemyList.forEach(enemy -> enemy.remove());
    // e.getDungeonTour().EnemyList.clear();
    // dt.isClear = true;
    // dt.player.sendMessage("当前房间已Clear");
    // }

    // @EventHandler
    // public void onEnterRoom(EnterRoomEvent e) {
    // // 生成敌人 房间锁住
    // var world = Main.getInstance().getServer().getWorld(e.getdDungeonInfo().Id);

    // // e.getRoomInfo().EnemyPosition.forEach(pos -> {
    // // var spawnPos = DungeonManager.GetPoint(e.getdDungeonInfo(),
    // // e.getRoomInfo().Rooms.get(e.getRoomIndex()),
    // // pos);
    // // e.getDungeonTour().EnemyList.add(
    // // world.spawnEntity(new Location(world, spawnPos[0], spawnPos[1],
    // spawnPos[2]),
    // // EntityType.ZOMBIE));
    // // });

    // // 随机抽取敌人组，循环遍历生成点进行逐个生成
    // var posIt = e.getRoomInfo().EnemyPosition.iterator();
    // Random r = new Random();
    // var enemyData = EnemyManager.Enemys
    // .get(EnemyManager.Enemys.keySet().toArray()[r.nextInt(EnemyManager.Enemys.size())]);

    // for (var id : enemyData.Enemys.keySet()) {
    // var num = enemyData.Enemys.get(id);
    // EntityType mob = EntityType.valueOf(id.toUpperCase());
    // for (int i = 0; i < num; i++) {
    // var spawnPos = DungeonManager.GetPoint(e.getdDungeonInfo(),
    // e.getRoomInfo().Rooms.get(e.getRoomIndex()),
    // posIt.next());
    // if (!posIt.hasNext()) {
    // posIt = e.getRoomInfo().EnemyPosition.iterator();
    // }
    // var newEnemy = world.spawnEntity(new Location(world, spawnPos[0],
    // spawnPos[1], spawnPos[2]), mob);
    // e.getDungeonTour().EnemyList.add(newEnemy);
    // }
    // }
    // e.getPlayer().sendMessage("生成怪物组 " + enemyData.Id);
    // if (e.getRoomInfo().Type.equals("normal") ||
    // e.getRoomInfo().Type.equals("boss")) {
    // e.getPlayer().sendMessage("此房间为战斗房间，锁住");
    // e.getDungeonTour().isClear = false;
    // } else {
    // e.getPlayer().sendMessage("此房间不锁");
    // e.getDungeonTour().isClear = true;
    // }

    // }

    // @EventHandler
    // public void onEnemyDeath(EntityDeathEvent event) {
    // var p = event.getEntity().getKiller();
    // if (p == null) {
    // return;
    // }
    // var tour = TourManager.GetTour(p);
    // if (tour == null) {
    // return;
    // }
    // if (tour.EnemyList.contains((Entity) event.getEntity())) {
    // p.sendMessage("敌人死亡事件：杀死一个房间内敌人");
    // tour.EnemyList.remove((Entity) event.getEntity());
    // }
    // if (tour.EnemyList.size() == 0) {
    // ClearRoom(tour);
    // }
    // }

    // @EventHandler
    // public void onRoomClear(RoomClearEvent e) {
    // if (e.getDungeonTour().row == e.getDungeonTour().DungeonLevel.size() - 1) {
    // TourManager.Tours.remove(e.getDungeonTour().player);
    // e.getDungeonTour().player.sendMessage("当前游戏结束！");
    // }
    // }

    // @EventHandler
    // public void onPlayerDamage(EntityDamageEvent e) {
    // if (e.getEntity().getType() != EntityType.PLAYER)
    // return;
    // e.setDamage(1);
    // }
}
