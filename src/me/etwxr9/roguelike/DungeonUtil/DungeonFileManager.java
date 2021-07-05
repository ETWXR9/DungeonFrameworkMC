package me.etwxr9.roguelike.DungeonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import me.etwxr9.roguelike.Main;

public class DungeonFileManager {
    // 返回所有地牢数据名
    public static List<String> AllDungeonFileName() {
        File dir = new File(Main.getInstance().getDataFolder().getAbsolutePath().toString() + "/Dungeon/");
        if (!dir.exists()) {
            return null;
        }
        var jsons = Arrays.asList(dir.list()).stream().filter(s -> s.endsWith(".json"))
                .map(s -> s = s.substring(0, s.length() - 5)).collect(Collectors.toList());
        return jsons;
    }

    // 读取地牢数据，如果文件不存在则反回""
    public static String ReadDungeonFile(String filename) throws IOException {
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Dungeon/" + filename + ".json");
        if (!Files.exists(path))
            return "";
        var data = Files.readString(path);
        return data;
    }

    // 写入地牢数据
    public static void WriteDungeonFile(String filename, String data) throws IOException {
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Dungeon/" + filename + ".json");
        if (Files.exists(path)) {
            Files.writeString(path, data);
        }
        Files.writeString(path, data, StandardOpenOption.WRITE);
    }

    // 创建地牢数据
    public static boolean CreateDungeonFile(String id, List<String> tags, int[] origin, int[] size, int[] unitSize)
            throws IOException {
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Dungeon/" + id + ".json");
        // 如果已经存在，返回false
        if (Files.exists(path)) {
            return false;
        }
        // 如果不存在目录，创建
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        var dungeonInfo = new DungeonInfo(id, tags, origin, size, unitSize);
        // 创建以worldid为名的json
        var data = Parsejson(dungeonInfo);
        Files.writeString(path, data, StandardOpenOption.CREATE);
        // 创建ROOM目录
        var roomDir = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Room/" + id + "/");
        if (!Files.exists(roomDir)) {
            Files.createDirectories(roomDir);
        }
        return true;
    }

    public static boolean DeleteDungeonFile(String id) {
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Dungeon/" + id + ".json");
        if (!Files.exists(path)) {
            return false;
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // var rooms = AllRoomFileName(id);
        // for (String r : rooms) {
        // if (!DeleteRoomFile(id, r)) {
        // return false;
        // }
        // }

        return deleteDir(new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/Room/" + id + "/"));

    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    // 将字符串转换为DungeonInfo
    public static DungeonInfo ParseDungeonInfo(String jsonData) {
        return JSON.parseObject(jsonData, DungeonInfo.class);
    }

    // 将DungeonInfo转换为字符串
    public static String Parsejson(DungeonInfo obj) {
        // 简化
        var a = new DungeonInfo(obj.Id, obj.Tags, obj.Origin, obj.Size, obj.RoomSize);
        var data = JSON.toJSONString(a);
        return data;
    }

    // 返回指定地牢所有房间名
    public static List<String> AllRoomFileName(String dungeonId) {
        File dir = new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/Room/" + dungeonId + "/");
        var jsons = Arrays.asList(dir.list());
        return jsons;
    }

    // 读取房间数据，如果文件不存在则反回""
    public static String ReadRoomFile(String dungeonId, String id) throws IOException {
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Room/" + dungeonId + "/" + id
                + "/" + id + ".json");
        if (!Files.exists(path))
            return "";
        var data = Files.readString(path);
        var luaPath = Main.getInstance().getDataFolder().getAbsolutePath() + "/Room/" + dungeonId + "/" + id + "/" + id
                + ".lua";
        LuaLoader.LoadRoomLua(Main.getInstance().global, luaPath, dungeonId, id);
        return data;
    }

    // 创建房间数据
    public static boolean CreateRoomFile(RoomInfo ri) throws IOException {
        var dungeonId = ri.DungeonId;
        var id = ri.Id;
        var tags = ri.Tags;
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Room/" + dungeonId + "/" + id
                + "/" + id + ".json");
        // 如果已经存在，返回false
        if (Files.exists(path)) {
            return false;
        }
        // 创建目录
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        var roomInfo = new RoomInfo(dungeonId, id, tags);
        // 创建以roomid为名的json
        var data = Parsejson(roomInfo);
        Files.writeString(path, data, StandardOpenOption.CREATE);
        return true;
    }

    // 写入房间数据
    public static void WriteRoomFile(String dungeonId, String id, String data) throws IOException {
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Room/" + dungeonId + "/" + id
                + "/" + id + ".json");
        if (Files.exists(path)) {
            Files.writeString(path, data);
        }
        Files.writeString(path, data, StandardOpenOption.WRITE);
    }

    public static void WriteRoomFile(RoomInfo ri) throws IOException {
        WriteRoomFile(ri.DungeonId, ri.Id, Parsejson(ri));
    }

    public static boolean DeleteRoomFile(String dungeonId, String id) {
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Room/" + dungeonId + "/" + id
                + "/" + id + ".json");
        if (!Files.exists(path)) {
            return false;
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // 将字符串转换为RoomInfo
    public static RoomInfo ParseRoomInfo(String jsonData) {
        return JSON.parseObject(jsonData, RoomInfo.class);
    }

    // 将roomInfo转换为字符串
    public static String Parsejson(RoomInfo obj) {
        // 简化
        // var a = new RoomInfo(obj.DungeonId, obj.Id, obj.Tags);
        var data = JSON.toJSONString(obj);
        return data;
    }

}
