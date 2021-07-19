package com.etwxr9.dfmc.Lua;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.etwxr9.dfmc.Main;
import com.etwxr9.dfmc.Dungeon.DungeonInfo;
import com.etwxr9.dfmc.Dungeon.DungeonManager;

import org.luaj.vm2.LuaError;

public final class DungeonLuaManager {

    private DungeonLuaManager() {

    }

    public static DungeonLuaManager getInstance() {
        return InnerClass.ins;
    }

    private static class InnerClass {
        private static final DungeonLuaManager ins = new DungeonLuaManager();
    }

    public Map<String, String> DungeonPermanentLuas = new HashMap<String, String>();
    public Map<String, String> DungeonDynamicLuas = new HashMap<String, String>();
    // <dungeon<room,lua>>
    public Map<String, Map<String, String>> DungeonRoomLuas = new HashMap<String, Map<String, String>>();

    // 加载地牢动态lua
    public void LoadDungeonDynamicLuas() {
        File itemDir = new File(
                Main.getInstance().getDataFolder().getAbsolutePath().toString() + "/DungeonDynamicLua/");
        if (!itemDir.exists()) {
            itemDir.mkdirs();
        }
        var files = itemDir.listFiles();
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            if (file.isFile() && file.getName().endsWith(".lua")) {
                String gameLuaName = file.getName().substring(0, file.getName().length() - 4);
                String gameLua;
                try {
                    gameLua = Files.readString(file.toPath());
                } catch (LuaError | IOException e) {
                    Main.getInstance().getLogger().info("读取lua文件出错！" + e.getMessage());
                    continue;
                }
                DungeonDynamicLuas.put(gameLuaName, gameLua);
            }
        }

    }

    // 加载地牢永久lua
    public void LoadDungeonPermanentLuas() {
        File itemDir = new File(
                Main.getInstance().getDataFolder().getAbsolutePath().toString() + "/DungeonPermanentLua/");
        if (!itemDir.exists()) {
            itemDir.mkdirs();
        }
        var files = itemDir.listFiles();
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            if (file.isFile() && file.getName().endsWith(".lua")) {
                String gameLuaName = file.getName().substring(0, file.getName().length() - 4);
                String gameLua;
                try {
                    gameLua = Files.readString(file.toPath());
                    // Main.getInstance().getLogger().info("读取地牢永久lua文件" + gameLuaName);
                } catch (LuaError | IOException e) {
                    Main.getInstance().getLogger().info("读取lua文件出错！" + e.getMessage());
                    continue;
                }
                DungeonPermanentLuas.put(gameLuaName, gameLua);
            }
        }

    }

    // 加载地牢房间lua
    public void LoadDungeonRoomLuas(String path, String dungeonName, String roomName) {
        var file = new File(path);
        if (file.isFile() && file.getName().equals(roomName + ".lua")) {
            String roomLua = "";
            try {
                roomLua = Files.readString(file.toPath());
            } catch (LuaError | IOException e) {
                Main.getInstance().getLogger().info("读取lua文件出错！" + e.getMessage());
                return;
            }
            DungeonInfo di = DungeonManager.GetDungeonInfo(dungeonName);
            if (DungeonRoomLuas.get(di.Id) == null) {
                DungeonRoomLuas.put(di.Id, new HashMap<String, String>());
            }
            DungeonRoomLuas.get(di.Id).put(roomName, roomLua);
        }

    }
}
