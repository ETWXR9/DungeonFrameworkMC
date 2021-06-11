package me.etwxr9.roguelike.DungeonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import me.etwxr9.roguelike.Main;

public class DungeonJsonParser {
    // 返回所有地牢数据名
    public static List<String> AllDungeonFileName() {
        File dir = new File(Main.getInstance().getDataFolder().getAbsolutePath().toString() + "/");
        var jsons = Arrays.asList(dir.list()).stream().filter(s -> s.endsWith(".json"))
                .map(s -> s = s.substring(0, s.length() - 5)).collect(Collectors.toList());
        return jsons;
    }

    // 读取地牢数据，如果文件不存在则反回""
    public static String ReadFile(String filename) throws IOException {
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/" + filename + ".json");
        if (!Files.exists(path))
            return "";
        var data = Files.readString(path);
        return data;
    }

    // 写入地牢数据
    public static void WriteFile(String filename, String data) throws IOException {
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/" + filename + ".json");
        if (Files.exists(path)) {
            Files.writeString(path, data);
        }
        Files.writeString(path, data, StandardOpenOption.WRITE);
    }

    // 创建地牢数据
    public static boolean CreateFile(String worldId, int[] origin, int[] size, int[] unitSize) throws IOException {
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/" + worldId + ".json");
        // 如果已经存在，返回false
        if (Files.exists(path)) {
            return false;
        }
        var dungeonInfo = new DungeonInfo();
        dungeonInfo.World = worldId;
        dungeonInfo.Origin = origin;
        dungeonInfo.Size = size;
        dungeonInfo.UnitSize = unitSize;
        dungeonInfo.Units = new ArrayList<RoomInfo>(size[0] * size[1] * size[2]);
        dungeonInfo.initEmptyRoomList();
        // 创建以worldid为名的json
        var data = Parsejson(dungeonInfo);
        Files.writeString(path, data, StandardOpenOption.CREATE);
        return true;
    }

    // 将字符串转换为DungeonInfo
    public static DungeonInfo ParseDungeonInfo(String jsonData) {
        return JSON.parseObject(jsonData, DungeonInfo.class);
    }

    // 将DungeonInfo转换为字符串
    public static String Parsejson(DungeonInfo obj) {
        return JSON.toJSONString(obj);
    }
}
