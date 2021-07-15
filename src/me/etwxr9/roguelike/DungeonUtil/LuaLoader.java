package me.etwxr9.roguelike.DungeonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;

import me.etwxr9.roguelike.Main;

public class LuaLoader {
    // <name,lua>>
    public static Map<String, String> GameLuas = new HashMap<String, String>();
    public static Map<String, String> DynamicLuas = new HashMap<String, String>();
    // <dungeon<room,lua>>
    public static Map<String, Map<String, String>> RoomLuas = new HashMap<String, Map<String, String>>();

    public static void LoadItem(Globals globals) {
        // 加载道具
        File itemDir = new File(Main.getInstance().getDataFolder().getAbsolutePath().toString() + "/ItemData/");
        var dirs = itemDir.listFiles();
        for (var i = 0; i < dirs.length; i++) {
            var dir = dirs[i];
            if (!dir.isDirectory())
                continue;
            var files = dir.listFiles();
            for (var j = 0; j < files.length; j++) {
                var file = files[j];
                String itemName = dir.getName();
                // if (file.isFile() && file.getName().equals(itemName + ".json")) {
                // // 读道具配置
                // try {
                // var data = Files.readString(file.toPath());
                // // var item = JSON.parseObject(data, ItemData.class);
                // // getLogger().info("读取道具名 " + item.Id);
                // } catch (IOException e) {
                // e.printStackTrace();
                // }
                // }
                if (file.isFile() && file.getName().equals(itemName + ".lua")) {
                    // 读道具lua
                    String itemLua;
                    try {
                        itemLua = Files.readString(file.toPath());
                        globals.load(itemLua).call();
                    } catch (LuaError | IOException e) {
                        Main.getInstance().getLogger().info("读取lua文件出错！" + e.getMessage());
                        continue;
                    }

                    DynamicLuas.put(itemName, itemLua);
                    // // 读道具事件订阅
                    // var itemTable = globals.get(itemName);
                    // LuaValue key = LuaValue.NIL;
                    // while (true) {
                    // Varargs n = itemTable.next(key);
                    // if ((key = n.arg1()).isnil())
                    // break;
                    // LuaValue v = n.arg(2);
                    // if (v.typename().equals("function")) {
                    // List<String> itemList = LuaListenerManager.luaFuncMap.get(key.toString());
                    // if (itemList != null)
                    // itemList.add(itemName);
                    // }
                    // }
                }
            }

        }
    }

    public static void LoadGameLua(Globals globals) {
        File itemDir = new File(Main.getInstance().getDataFolder().getAbsolutePath().toString() + "/Game/");
        var files = itemDir.listFiles();
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            if (file.isFile() && file.getName().endsWith(".lua")) {
                // 读Game lua
                String gameLuaName = file.getName().substring(0, file.getName().length() - 4);
                String gameLua;
                try {
                    gameLua = Files.readString(file.toPath());
                    globals.load(gameLua).call();
                } catch (LuaError | IOException e) {
                    Main.getInstance().getLogger().info("读取lua文件出错！" + e.getMessage());
                    continue;
                }
                GameLuas.put(gameLuaName, gameLua);
                // 读事件订阅
                // var itemTable = globals.get(gameLuaName);
                // LuaValue key = LuaValue.NIL;
                // while (true) {
                // Varargs n = itemTable.next(key);
                // if ((key = n.arg1()).isnil())
                // break;
                // // LuaValue v = n.arg(2);
                // // if (v.typename().equals("function")) {
                // // List<String> gameList = LuaListenerManager.luaFuncMap.get(key.toString());
                // // if (gameList != null)
                // // gameList.add(gameLuaName);
                // // }
                // }
            }
        }

    }

    public static void LoadRoomLua(Globals globals, String path, String dungeonName, String roomName) {
        var file = new File(path);
        if (file.isFile() && file.getName().equals(roomName + ".lua")) {
            // 读lua
            String roomLua = "";
            try {
                roomLua = Files.readString(file.toPath());
                globals.load(roomLua).call();
            } catch (LuaError | IOException e) {
                Main.getInstance().getLogger().info("读取lua文件出错！" + e.getMessage());
                return;
            }
            DungeonInfo di = DungeonManager.GetDungeonInfo(dungeonName);
            if (RoomLuas.get(di.Id) == null) {
                RoomLuas.put(di.Id, new HashMap<String, String>());
            }
            RoomLuas.get(di.Id).put(roomName, roomLua);
            // RoomLuas.put(name, roomLua);
            // // 读道具事件订阅
            // var luaTable = globals.get(name);
            // LuaValue key = LuaValue.NIL;
            // while (true) {
            // Varargs n = luaTable.next(key);
            // if ((key = n.arg1()).isnil())
            // break;
            // LuaValue v = n.arg(2);
            // if (v.typename().equals("function")) {
            // List<String> luaList = LuaListenerManager.luaFuncMap.get(key.toString());
            // if (luaList != null)
            // luaList.add(name);
            // }
            // }
        }

    }
}
