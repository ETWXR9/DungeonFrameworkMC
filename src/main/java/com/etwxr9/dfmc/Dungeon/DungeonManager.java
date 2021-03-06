package com.etwxr9.dfmc.Dungeon;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.boydti.fawe.object.RunnableVal;
import com.boydti.fawe.util.EditSessionBuilder;
import com.boydti.fawe.util.TaskManager;
import com.etwxr9.dfmc.Main;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

//每个该对象和一个玩家绑定。管理所有DungeonInfo。
public class DungeonManager {

    public DungeonInfo currentDungeon;
    public RoomInfo currentRoom;
    public int[] currentPosition;
    public Player player;

    private static List<DungeonManager> dmList = new ArrayList<DungeonManager>();
    private static List<DungeonInfo> diList = new ArrayList<DungeonInfo>();

    // 取得空房间坐标
    public static int[] GetEmptyRoom(DungeonInfo di) {

        if (di.EmptyRoomList.size() == 0) {
            return null;
        }
        return di.EmptyRoomList.get(0);
    }

    // 保存
    public boolean SaveDungeon() {
        try {
            DungeonFileManager.WriteDungeonFile(currentDungeon.Id, DungeonFileManager.Parsejson(currentDungeon));
            var rs = currentDungeon.Rooms;
            for (RoomInfo ri : rs) {
                DungeonFileManager.WriteRoomFile(ri);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    // 使用JsonIO的函数加载所有json文件并解析为DungeonInfo，加入diList，（可能功能：填充空房间列表），加载世界
    public static void LoadDungeons() {
        diList = new ArrayList<DungeonInfo>();
        var names = DungeonFileManager.AllDungeonFileName();
        if (names == null) {
            return;
        }
        Main.getInstance().getLogger().info("读取地牢数据数量" + names.size());
        names.forEach(n -> {
            try {
                Main.getInstance().getLogger().info("读取地牢数据 " + n);
                LoadDungeon(n);
            } catch (Exception e) {
                Main.getInstance().getLogger().info("读取地牢数据出错！ ");
                e.printStackTrace();
            }
        });
    }

    public static void LoadDungeon(String id) {
        // 读取地牢json、房间json、合并
        DungeonInfo di;
        try {
            di = DungeonFileManager.ParseDungeonInfo(DungeonFileManager.ReadDungeonFile(id));
            diList.add(di);
            var rooms = DungeonFileManager.AllRoomFileName(id);
            di.initEmptyRoomList();
            // Main.getInstance().getLogger().info("读取所有房间，数量为" + rooms.size());
            rooms.forEach(r -> {
                try {
                    var room = DungeonFileManager.ParseRoomInfo(DungeonFileManager.ReadRoomFile(id, r));
                    di.Rooms.add(room);
                    room.Rooms.forEach(pos -> {
                        // Main.getInstance().getLogger().info("读取房间位置为" + pos[0] + "," + pos[1] + "," +
                        // pos[2]);
                        if (di.EmptyRoomList.removeIf(p -> Arrays.equals(p, pos))) {
                            pos.equals(pos);
                        }
                    });
                } catch (IOException e) {
                    Main.getInstance().getLogger().info("读取房间数据出错！ " + e.getMessage());
                    return;
                }

            });

        } catch (IOException e) {
            Main.getInstance().getLogger().info("读取地牢数据出错！ " + e.getMessage());
            return;
        }

        WorldCreator wc = new WorldCreator(id);
        wc.generateStructures(false);
        // 这个VoidChunkGenerator重写了generateChunkData方法，只会生成空区块。
        wc.generator(new VoidChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                return createChunkData(world);
            }
        });
        World newWorld = wc.createWorld();
        newWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        newWorld.setGameRule(GameRule.DO_MOB_LOOT, false);
        newWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        newWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        newWorld.setGameRule(GameRule.MOB_GRIEFING, false);
        newWorld.setDifficulty(Difficulty.HARD);

    }

    // 新建DungeonManager
    public static DungeonManager NewDungeonManager(Player p, DungeonInfo di, RoomInfo cr, int[] roomPos) {
        var dm = GetDMbyPlayer(p);
        if (dm != null) {
            dm.currentDungeon = di;
            dm.currentRoom = cr;
            return dm;
        }

        dm = new DungeonManager();
        dm.player = p;
        dm.currentDungeon = di;
        dm.currentRoom = cr;
        dm.currentPosition = roomPos;
        dmList.add(dm);
        return dm;
    }

    // 返回对应玩家的DM
    public static DungeonManager GetDMbyPlayer(Player p) {
        var dm = dmList.stream().filter(d -> d.player.getName() == p.getName()).toArray();
        if (dm.length > 0) {
            return (DungeonManager) dm[0];
        } else
            return null;
    }

    // 给出指定原点坐标和房间大小，填充房间四壁为石头。
    public static void FillDefaultRoom(World w, int[] origin, int[] size) {
        for (int z = 0; z < size[2]; z++) {
            for (int y = 0; y < size[1]; y++) {
                for (int x = 0; x < size[0]; x++) {
                    int blockX = origin[0] + x;
                    int blockY = origin[1] + y;
                    int blockZ = origin[2] + z;
                    if (x * y * z == 0 || x == size[0] - 1 || y == size[1] - 1 || z == size[2] - 1) {
                        w.getBlockAt(blockX, blockY, blockZ).setType(Material.STONE);
                    } else {
                        w.getBlockAt(blockX, blockY, blockZ).setType(Material.AIR);
                    }
                }
            }
        }
    }

    // 传送玩家
    public void TeleportPlayerToRoom(DungeonInfo dungeon, RoomInfo room) {
        TeleportPlayerToRoom(dungeon, room, 0);
    }

    // 传送玩家
    public void TeleportPlayerToRoom(DungeonInfo dungeon, RoomInfo room, int index) {
        if (index >= room.Rooms.size()) {
            return;
        }
        var p = player;
        var point = GetPoint(dungeon, room.Rooms.get(index), room.PlayerPosition);
        var world = Bukkit.getWorld(dungeon.Id);
        p.sendMessage(MessageFormat.format("准备传送至地牢世界：{0}， 房间Id：{1}， 序号：{2}", world.getName(), room.Id, index));

        p.sendMessage(MessageFormat.format("y的地牢原点：{0}，房间规模 {1}， 房间位置{2}，出生点{3}", dungeon.Origin[1],
                dungeon.RoomSize[1], room.Rooms.get(index)[1], room.PlayerPosition[1]));
        if (p.teleport(new Location(world, point[0] + 0.5, point[1] + 0.5, point[2] + 0.5))) {
            p.sendMessage(MessageFormat.format("准备传送至：{0}， {1}， {2}", point[0], point[1], point[2]));
            currentDungeon = dungeon;
            currentRoom = room;
            currentPosition = room.Rooms.get(index);
            p.sendMessage("传送成功");
        } else {
            p.sendMessage("传送失败");
        }
    }

    // 返回对应DungeonInfo，没有则返回null
    public static DungeonInfo GetDungeonInfo(String worldName) {
        // Main.getInstance().getLogger().info("准备遍历DI查找" + worldName);
        for (DungeonInfo d : diList) {
            // Main.getInstance().getLogger().info("遍历DI中：" + d.Id);
            if (worldName.equals(d.Id)) {
                // Main.getInstance().getLogger().info("遍历DI" + d.Id + " 匹配");
                return d;
            }
        }
        Main.getInstance().getLogger().info("未能查找到地牢" + worldName);
        return null;
    }

    // 新建房间并传送玩家
    public static RoomInfo NewRoom(Player p, DungeonInfo dungeon, String id) {
        var point = GetEmptyRoom(dungeon);
        // p.sendMessage("创建默认房间，GetEmptyRoom为 " + point[0] + "," + point[1] + "," +
        // point[2]);
        RoomInfo newRoom = new RoomInfo(dungeon.Id, id, new ArrayList<String>());
        newRoom.Rooms.add(point);
        newRoom.PlayerPosition = new int[] { 1, 1, 1 };
        try {
            DungeonFileManager.CreateRoomFile(newRoom);
        } catch (IOException e) {
            p.sendMessage("创建 " + dungeon.Id + ".json 文件时出错！");
            e.printStackTrace();
            return null;
        }
        dungeon.EmptyRoomList.remove(point);
        dungeon.Rooms.add(newRoom);
        if (!SaveDungeon(dungeon))
            p.sendMessage("保存地牢 " + dungeon.Id + ".json 文件时出错！");

        var roomSize = dungeon.RoomSize;
        var origin = new int[] { point[0] * roomSize[0], point[1] * roomSize[1], point[2] * roomSize[2] };
        FillDefaultRoom(Main.getInstance().getServer().getWorld(dungeon.Id), origin, roomSize);
        // 为该玩家设定DM
        var dm = DungeonManager.NewDungeonManager(p, dungeon, newRoom, point);
        dm.TeleportPlayerToRoom(dungeon, newRoom);
        // p.teleport(new Location(p.getWorld(), x, y, z));
        return newRoom;
    }

    // 返回指定地牢坐标中的最靠前空房间（null房间），没有空房间返回null
    public static int[] FirstEmptyRoom(DungeonInfo d) {
        List<int[]> rooms = new ArrayList<int[]>();
        d.Rooms.stream().filter(r -> r != null).forEach(r -> rooms.addAll(r.Rooms));
        for (int z = 0; z < d.Size[2]; z++) {
            for (int y = 0; y < d.Size[1]; y++) {
                for (int x = 0; x < d.Size[0]; x++) {
                    var point = new int[] { x, y, z };
                    if (rooms.indexOf(point) == -1) {
                        return point;
                    }
                }
            }
        }
        return null;
    }

    // 返回指定房间内点的坐标
    public static int[] GetPoint(DungeonInfo di, int[] roomPos, int[] roomPoint) {
        var x = di.Origin[0] + di.RoomSize[0] * roomPos[0] + roomPoint[0];
        var y = di.Origin[1] + di.RoomSize[1] * roomPos[1] + roomPoint[1];
        var z = di.Origin[2] + di.RoomSize[2] * roomPos[2] + roomPoint[2];

        return new int[] { x, y, z };
    }

    public static double[] GetPoint(DungeonInfo di, int[] roomPos, double[] roomPoint) {
        var x = di.Origin[0] + di.RoomSize[0] * roomPos[0] + roomPoint[0];
        var y = di.Origin[1] + di.RoomSize[1] * roomPos[1] + roomPoint[1];
        var z = di.Origin[2] + di.RoomSize[2] * roomPos[2] + roomPoint[2];

        return new double[] { x, y, z };
    }

    // 返回dmList的副本
    public static List<DungeonManager> GetDMList() {
        return new ArrayList<DungeonManager>(dmList);
    }

    // 返回dungeonList的副本
    public static List<DungeonInfo> GetDIList() {
        return new ArrayList<DungeonInfo>(diList);
    }

    // 保存
    public static boolean SaveDungeon(DungeonInfo di) {
        try {
            DungeonFileManager.WriteDungeonFile(di.Id, DungeonFileManager.Parsejson(di));
            for (RoomInfo ri : di.Rooms) {
                DungeonFileManager.WriteRoomFile(ri);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
