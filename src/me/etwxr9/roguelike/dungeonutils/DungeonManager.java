package me.etwxr9.roguelike.dungeonutils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
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
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.Main;
import me.etwxr9.roguelike.VoidChunkGenerator;

//每个该对象和一个玩家绑定。管理所有DungeonInfo。
public class DungeonManager {

    public DungeonInfo currentDungeon;
    public RoomInfo currentRoom;
    public int[] currentPosition;
    public Player player;

    private static List<DungeonManager> dmList = new ArrayList<DungeonManager>();
    private static List<DungeonInfo> diList = new ArrayList<DungeonInfo>();

    // 使用JsonIO的函数加载所有json文件并解析为DungeonInfo，加入diList，（可能功能：填充空房间列表），加载世界
    public static void LoadDungeons() {
        diList = new ArrayList<DungeonInfo>();

        var names = JsonIO.AllDungeonFileName();
        Main.getInstance().getLogger().info("读取地牢数据! names.length=" + names.size());
        names.forEach(n -> {
            try {
                Main.getInstance().getLogger().info("读取地牢数据 " + n);
                diList.add(JsonIO.ParseDungeonInfo(JsonIO.ReadFile(n)));
            } catch (Exception e) {
                Main.getInstance().getLogger().info("读取全部地牢数据出错！ " + e.getMessage());
            }
        });
        diList.forEach(d -> {
            WorldCreator wc = new WorldCreator(d.World);
            wc.generateStructures(false);

            // 这个VoidChunkGenerator重写了generateChunkData方法，只会生成空区块。
            wc.generator(new VoidChunkGenerator());
            World newWorld = wc.createWorld();
            newWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            newWorld.setGameRule(GameRule.DO_MOB_LOOT, false);
            newWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            newWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        });
    }

    // 新建DungeonManager
    public static DungeonManager NewDungeonManager(Player p, DungeonInfo di, RoomInfo cr) {
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
    public static void FillDefaultRoom(Player p, int[] origin, int[] size) {
        for (int z = 0; z < size[2]; z++) {
            for (int y = 0; y < size[1]; y++) {
                for (int x = 0; x < size[0]; x++) {
                    int blockX = origin[0] + x;
                    int blockY = origin[1] + y;
                    int blockZ = origin[2] + z;
                    if (x * y * z == 0 || x == size[0] - 1 || y == size[1] - 1 || z == size[2] - 1) {
                        p.getWorld().getBlockAt(blockX, blockY, blockZ).setType(Material.STONE);
                    }
                }
            }
        }
    }

    // 传送玩家
    public static void TeleportPlayerToRoom(DungeonManager dm, DungeonInfo dungeon, RoomInfo room) {
        TeleportPlayerToRoom(dm, dungeon, room, 0);
    }

    // 传送玩家
    public static void TeleportPlayerToRoom(DungeonManager dm, DungeonInfo dungeon, RoomInfo room, int index) {
        if (index >= room.Rooms.size()) {
            return;
        }
        var p = dm.player;
        var point = GetPoint(dungeon, room.Rooms.get(index), room.PlayerPosition);
        var world = Bukkit.getWorld(dungeon.World);
        p.sendMessage(MessageFormat.format("准备传送至地牢世界：{0}， 房间Id：{1}， 序号：{2}", world.getName(), room.Id, index));
        if (p.teleport(new Location(world, point[0], point[1], point[2]))) {
            dm.currentDungeon = dungeon;
            dm.currentRoom = room;
            dm.currentPosition = room.Rooms.get(index);
            p.sendMessage("传送成功");
        } else {
            p.sendMessage("传送失败");
        }
    }

    // 新建房间并传送玩家
    public static RoomInfo NewDefaultRoom(Player p, DungeonInfo dungeon) {
        var point = GetEmptyRoom(dungeon);
        RoomInfo newRoom = new RoomInfo();
        newRoom.Id = "default";
        newRoom.Rooms.add(point);
        newRoom.PlayerPosition = new int[] { 1, 1, 1 };
        dungeon.EmptyRoomList.remove(point);
        dungeon.Units.add(newRoom);
        if (!SaveDungeon(dungeon))
            p.sendMessage("保存地牢 " + dungeon.World + ".json 文件时出错！");

        var roomSize = dungeon.UnitSize;
        var origin = new int[] { point[0] * roomSize[0], point[1] * roomSize[1], point[2] * roomSize[2] };
        FillDefaultRoom(p, origin, roomSize);
        var x = origin[0] + newRoom.PlayerPosition[0];
        var y = origin[1] + newRoom.PlayerPosition[1];
        var z = origin[2] + newRoom.PlayerPosition[2];
        // 为该玩家设定DM
        DungeonManager.NewDungeonManager(p, dungeon, newRoom);
        p.teleport(new Location(p.getWorld(), x, y, z));
        return newRoom;
    }

    // 复制房间（count为-1时为更新房间）
    public static void CloneRoom(Player p, int count) {
        var dm = GetDMbyPlayer(p);
        if (dm.currentDungeon == null) {
            return;
        }
        var ri = dm.currentRoom;
        if (ri == null) {
            return;
        }
        var begin = GetPoint(dm.currentDungeon, ri.Rooms.get(0), new int[] { 0, 0, 0 });
        var end = GetPoint(dm.currentDungeon, ri.Rooms.get(0), dm.currentDungeon.UnitSize);

        p.sendMessage(count == -1 ? "准备更新 " + (ri.Rooms.size() - 1) + " 个房间" : "准备复制 " + count + " 个房间");
        // USE WEAPI
        var vbegin = BlockVector3.at(begin[0], begin[1], begin[2]);
        var vend = BlockVector3.at(end[0] - 1, end[1] - 1, end[2] - 1);
        CuboidRegion region = new CuboidRegion(vbegin, vend);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(p.getWorld()))) {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
                    region.getMinimumPoint());
            // configure here
            try {
                Operations.complete(forwardExtentCopy);
            } catch (WorldEditException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        p.sendMessage("房间数据进入剪贴版，开始执行");

        if (count == -1) {
            for (int i = 1; i < ri.Rooms.size(); i++) {
                var des = GetPoint(dm.currentDungeon, ri.Rooms.get(i), new int[] { 0, 0, 0 });
                p.sendMessage(MessageFormat.format("更新房间{0}", Arrays.toString(des)));
                try (EditSession editSession = WorldEdit.getInstance()
                        .newEditSession(BukkitAdapter.adapt(p.getWorld()))) {
                    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                            .to(BlockVector3.at(des[0], des[1], des[2]))
                            // configure here
                            .build();
                    try {
                        Operations.complete(operation);
                    } catch (WorldEditException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    p.sendMessage("更新完毕");
                }
            }
            p.sendMessage("全部更新完毕");
        } else {
            for (int i = 0; i < count; i++) {
                var emptyRoom = DungeonManager.GetEmptyRoom(dm.currentDungeon);
                var des = GetPoint(dm.currentDungeon, emptyRoom, new int[] { 0, 0, 0 });
                p.sendMessage(MessageFormat.format("向{0}复制房间", Arrays.toString(des)));
                try (EditSession editSession = WorldEdit.getInstance()
                        .newEditSession(BukkitAdapter.adapt(p.getWorld()))) {
                    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                            .to(BlockVector3.at(des[0], des[1], des[2]))
                            // configure here
                            .build();
                    try {
                        Operations.complete(operation);
                    } catch (WorldEditException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    p.sendMessage("复制完毕");
                    dm.currentRoom.Rooms.add(emptyRoom);
                    dm.currentDungeon.EmptyRoomList.remove(emptyRoom);
                }
            }
            dm.SaveDungeon();
            p.sendMessage("全部复制完毕");
        }

        Bukkit.getWorld(dm.currentDungeon.World).save();
        p.sendMessage("世界已保存");

    }

    private static int[] GetEmptyRoom(DungeonInfo di) {

        if (di.EmptyRoomList.size() == 0) {
            return null;
        }
        return di.EmptyRoomList.get(0);
    }

    // 返回对应DungeonInfo，没有则返回null
    public static DungeonInfo GetDungeonInfo(String worldName) {
        Main.getInstance().getLogger().info("准备遍历DI查找" + worldName);
        for (DungeonInfo d : diList) {
            Main.getInstance().getLogger().info("遍历DI中：" + d.World);
            if (worldName.equals(d.World)) {
                Main.getInstance().getLogger().info("遍历DI" + d.World + " 匹配");
                return d;
            }
        }
        return null;
    }

    // 返回指定地牢坐标中的最靠前空房间（null房间），没有空房间返回null
    public static int[] FirstEmptyRoom(DungeonInfo d) {
        List<int[]> rooms = new ArrayList<int[]>();
        d.Units.stream().filter(r -> r != null).forEach(r -> rooms.addAll(r.Rooms));
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
        var x = di.Origin[0] + di.UnitSize[0] * roomPos[0] + roomPoint[0];
        var y = di.Origin[1] + di.UnitSize[1] * roomPos[1] + roomPoint[1];
        var z = di.Origin[2] + di.UnitSize[2] * roomPos[2] + roomPoint[2];
        return new int[] { x, y, z };
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
            JsonIO.WriteFile(di.World, JsonIO.Parsejson(di));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    // 保存
    public boolean SaveDungeon() {
        try {
            JsonIO.WriteFile(currentDungeon.World, JsonIO.Parsejson(currentDungeon));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
