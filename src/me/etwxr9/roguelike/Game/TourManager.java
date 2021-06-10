package me.etwxr9.roguelike.Game;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.sk89q.worldedit.util.SideEffect.State;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.etwxr9.roguelike.Main;
import me.etwxr9.roguelike.DungeonUtil.DungeonManager;
import me.etwxr9.roguelike.DungeonUtil.RoomInfo;
import me.etwxr9.roguelike.Event.EnterRoomEvent;
import me.etwxr9.roguelike.Event.RoomClearEvent;

public class TourManager implements Listener {
    public static Map<Player, DungeonTour> Tours = new HashMap<Player, DungeonTour>();

    public static DungeonTour NewTour(Player p, String dungeon) {
        var di = DungeonManager.GetDungeonInfo(dungeon);
        if (di == null) {
            return null;
        }

        List<RoomInfo> normalList = new ArrayList<RoomInfo>();
        List<RoomInfo> shopList = new ArrayList<RoomInfo>();
        List<RoomInfo> bossList = new ArrayList<RoomInfo>();

        normalList = di.Units.stream().filter(u -> u.Type.equals("normal")).collect(Collectors.toList());
        shopList = di.Units.stream().filter(u -> u.Type.equals("shop")).collect(Collectors.toList());
        bossList = di.Units.stream().filter(u -> u.Type.equals("boss")).collect(Collectors.toList());
        Random rng = new Random();
        var tour = new DungeonTour();
        tour.player = p;
        tour.dungeon = di;
        for (int i = 0; i < 5; i++) {
            Map<Integer, RoomInfo> row = new HashMap<Integer, RoomInfo>();
            for (int j = 0; j < 9; j++) {
                // 测试商店概率为1/7
                if (rng.nextInt(7) >= 1) {
                    row.put(j, normalList.get(rng.nextInt(normalList.size())));
                } else {
                    row.put(j, shopList.get(rng.nextInt(shopList.size())));
                }
            }
            tour.DungeonLevel.put(i, row);
        }
        Map<Integer, RoomInfo> row = new HashMap<Integer, RoomInfo>();
        for (int i = 0; i < 9; i++) {
            row.put(i, bossList.get(rng.nextInt(bossList.size())));
        }
        tour.DungeonLevel.put(5, row);
        Tours.put(p, tour);
        return tour;

    }

    public static DungeonTour GetTour(Player p) {
        return Tours.get(p);

    }

    public static void EnterRoom(Player p, int row, int pos) {
        var tour = GetTour(p);
        if (tour == null) {
            return;
        }
        var dungeon = tour.dungeon;
        RoomInfo room = tour.DungeonLevel.get(row).get(pos);
        var rng = new Random();
        // 随机选取一个房间（暂不查重）
        var index = rng.nextInt(room.Rooms.size());
        var point = DungeonManager.GetPoint(dungeon, room.Rooms.get(index), room.PlayerPosition);
        var world = Bukkit.getWorld(dungeon.World);

        EnterRoomEvent e = new EnterRoomEvent(row, pos, tour, dungeon, room, index, p);
        Bukkit.getPluginManager().callEvent(e);

        p.sendMessage(MessageFormat.format("准备传送至地牢世界：{0}， 房间Id：{1}， 序号：{2}", world.getName(), room.Id, index));
        p.sendMessage(MessageFormat.format("传送点 {0}， 房间Id：{1}， 序号：{2}", world.getName(), room.Id, index));
        if (p.teleport(new Location(world, point[0], point[1], point[2]))) {
            tour.dungeon = dungeon;
            tour.room = room;
            tour.roomPosition = room.Rooms.get(index);
            tour.pos = pos;
            tour.row = row;
            // tour.isClear = false;
            // p.sendMessage("传送成功");
        } else {
            // p.sendMessage("传送失败");
        }
    }

    public static void ClearRoom(DungeonTour dt) {
        RoomClearEvent e = new RoomClearEvent(dt, dt.dungeon, dt.room);
        Bukkit.getPluginManager().callEvent(e);
        e.getDungeonTour().EnemyList.forEach(enemy -> enemy.remove());
        e.getDungeonTour().EnemyList.clear();
        dt.isClear = true;
        dt.player.sendMessage("当前房间已Clear");
    }

    @EventHandler
    public void onEnterRoom(EnterRoomEvent e) {
        // 生成敌人 房间锁住
        var world = Main.getInstance().getServer().getWorld(e.getdDungeonInfo().World);

        e.getRoomInfo().EnemyPosition.forEach(pos -> {
            var spawnPos = DungeonManager.GetPoint(e.getdDungeonInfo(), e.getRoomInfo().Rooms.get(e.getRoomIndex()),
                    pos);
            e.getDungeonTour().EnemyList.add(
                    world.spawnEntity(new Location(world, spawnPos[0], spawnPos[1], spawnPos[2]), EntityType.ZOMBIE));
        });

        if (e.getRoomInfo().Type.equals("normal") || e.getRoomInfo().Type.equals("boss")) {
            e.getPlayer().sendMessage("此房间为战斗房间，锁住");
            e.getDungeonTour().isClear = false;
        } else {
            e.getPlayer().sendMessage("此房间不锁");
            e.getDungeonTour().isClear = true;
        }

    }

    @EventHandler
    public void onEnemyDeath(EntityDeathEvent event) {
        var p = event.getEntity().getKiller();
        if (p == null) {
            return;
        }
        var tour = TourManager.GetTour(p);
        if (tour == null) {
            return;
        }
        if (tour.EnemyList.contains((Entity) event.getEntity())) {
            p.sendMessage("敌人死亡事件：杀死一个房间内敌人");
            tour.EnemyList.remove((Entity) event.getEntity());
        }
        if (tour.EnemyList.size() == 0) {
            ClearRoom(tour);
        }
    }

    @EventHandler
    public void onRoomClear(RoomClearEvent e) {
        if (e.getDungeonTour().row == e.getDungeonTour().DungeonLevel.size() - 1) {
            TourManager.Tours.remove(e.getDungeonTour().player);
            e.getDungeonTour().player.sendMessage("当前游戏结束！");
        }
    }
}
