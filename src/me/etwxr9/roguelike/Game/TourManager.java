package me.etwxr9.roguelike.Game;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.etwxr9.roguelike.DungeonUtil.DungeonManager;
import me.etwxr9.roguelike.DungeonUtil.RoomInfo;

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
        p.sendMessage(MessageFormat.format("准备传送至地牢世界：{0}， 房间Id：{1}， 序号：{2}", world.getName(), room.Id, index));
        p.sendMessage(MessageFormat.format("传送点 {0}， 房间Id：{1}， 序号：{2}", world.getName(), room.Id, index));
        if (p.teleport(new Location(world, point[0], point[1], point[2]))) {
            tour.dungeon = dungeon;
            tour.room = room;
            tour.roomPosition = room.Rooms.get(index);
            tour.isClear = false;
            // p.sendMessage("传送成功");
        } else {
            // p.sendMessage("传送失败");
        }
    }
}
