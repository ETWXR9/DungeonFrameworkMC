package com.etwxr9.dfmc.Lua;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.etwxr9.dfmc.Main;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public final class GlobalLuaManager {
    private GlobalLuaManager() {
    }

    public static GlobalLuaManager getInstance() {
        return InnerClass.ins;
    }

    private static class InnerClass {
        private static final GlobalLuaManager ins = new GlobalLuaManager();
    }

    public Globals global = JsePlatform.standardGlobals();
    public Map<String, String> GlobalDynamicLuas = new HashMap<String, String>();
    public Map<String, LuaValue> LoadedGlobalDynamicLuas = new HashMap<String, LuaValue>();

    // 加载全局动态lua
    public void LoadGlobalDynamicLuas() {
        File globalDynamicLuaDir = new File(
                Main.getInstance().getDataFolder().getAbsolutePath().toString() + "/GlobalDynamicLua/");
        if (!globalDynamicLuaDir.exists()) {
            globalDynamicLuaDir.mkdirs();
        }
        var files = globalDynamicLuaDir.listFiles();
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            var fileName = file.getName();
            if (file.isFile() && fileName.endsWith(".lua")) {
                var luaName = fileName.substring(0, fileName.length() - 4);
                String luaString;
                try {
                    luaString = Files.readString(file.toPath());
                } catch (LuaError | IOException e) {
                    Main.getInstance().getLogger().info("读取lua文件出错！" + e.getMessage());
                    continue;
                }
                GlobalDynamicLuas.put(luaName, luaString);
            }
        }

    }

    // 加载全局永久lua
    public void LoadGlobalPermanentLuas() {
        global.load("LuaAPI = luajava.bindClass([[" + LuaAPI.class.getCanonicalName() + "]])").call();
        File globalLuaDir = new File(
                Main.getInstance().getDataFolder().getAbsolutePath().toString() + "/GlobalPermanentLua/");
        if (!globalLuaDir.exists()) {
            globalLuaDir.mkdirs();
        }
        var files = globalLuaDir.listFiles();
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            var fileName = file.getName();
            if (file.isFile() && fileName.endsWith(".lua")) {
                String luaString;
                try {
                    luaString = Files.readString(file.toPath());
                    global.load(luaString).call();
                    String luaName = fileName.substring(0, fileName.length() - 4);
                    var loadedLua = global.get(luaName);
                    if (loadedLua.get("init") != LuaValue.NIL) {
                        loadedLua.get("init").call(CoerceJavaToLua.coerce(Main.getInstance()));
                    }
                } catch (LuaError | IOException e) {
                    Main.getInstance().getLogger().info("读取lua文件出错！" + e.getMessage());
                    continue;
                }
            }
        }

    }

    public void EnableDynamicLua(String luaName) {
        try {
            global.load(DungeonLuaManager.getInstance().DungeonDynamicLuas.get(luaName)).call();
            var lua = global.get(luaName);
            LoadedGlobalDynamicLuas.put(luaName, lua);
            lua.get("init").call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DisableDynamicLua(String luaName) {
        var closeLua = LoadedGlobalDynamicLuas.get(luaName);
        if (closeLua == LuaValue.NIL || closeLua == null) {
            return;
        }
        if (closeLua.get("close") != LuaValue.NIL) {
            closeLua.get("close").call(closeLua);
        }
        GlobalLuaListenerManager.getInstance().UnRegisterDynamaicAllEvent(luaName);
        global.set(luaName, LuaValue.NIL);
        // tour.global.get("collectgarbage").call("collect");
        LoadedGlobalDynamicLuas.remove(luaName);
        // tour.GetFirstPlayer().sendMessage("卸载成功 " + luaName);

    }
}
