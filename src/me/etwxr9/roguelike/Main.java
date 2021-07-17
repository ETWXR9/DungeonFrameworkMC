package me.etwxr9.roguelike;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

// import me.etwxr9.roguelike.DungeonUtil.LuaLoader;
// import me.etwxr9.roguelike.Game.DungeonGUI;
// import me.etwxr9.roguelike.Game.EnemyData;
// import me.etwxr9.roguelike.Game.EnemyManager;
// import me.etwxr9.roguelike.Game.ItemData;
// import me.etwxr9.roguelike.Game.LuaListenerManager;
// import me.etwxr9.roguelike.Game.TourManager;
import me.etwxr9.roguelike.Command.BaseCmd;
import me.etwxr9.roguelike.Command.BaseTabCompleter;
import me.etwxr9.roguelike.Command.CmdCopyRoom;
import me.etwxr9.roguelike.Command.CmdCreateDungeon;
import me.etwxr9.roguelike.Command.CmdDeleteRoom;
import me.etwxr9.roguelike.Command.CmdDeleteWorld;
import me.etwxr9.roguelike.Command.CmdDungeonInfo;
import me.etwxr9.roguelike.Command.CmdEnterDungeon;
import me.etwxr9.roguelike.Command.CmdGameTest;
import me.etwxr9.roguelike.Command.CmdLuaCmd;
// import me.etwxr9.roguelike.Command.CmdGameTest;
// import me.etwxr9.roguelike.Command.CmdLuaTest;
import me.etwxr9.roguelike.Command.CmdNewRoom;
import me.etwxr9.roguelike.Command.CmdRoomInfo;
import me.etwxr9.roguelike.Command.CmdSetDefaultWorld;
import me.etwxr9.roguelike.Command.CmdSetRoomInfo;
import me.etwxr9.roguelike.Command.CmdUpdateRoom;
import me.etwxr9.roguelike.Command.CommandHandler;
import me.etwxr9.roguelike.DungeonUtil.DungeonManager;
import me.etwxr9.roguelike.DungeonUtil.EventNames;
import me.etwxr9.roguelike.DungeonUtil.LuaLoader;
import me.etwxr9.roguelike.Game.LuaGUIManager;
import me.etwxr9.roguelike.Game.LuaListenerManager;
import me.etwxr9.roguelike.Game.TourManager;

public class Main extends JavaPlugin {

    // 获取单例
    private static Main i;

    public static Main getInstance() {
        return i;
    }

    public CommandHandler cmdHandler;
    public Globals global;
    public ParticleNativeAPI api;

    @Override
    public void onEnable() {
        i = this;
        // getLogger().info(System.getProperty("java.class.path"));
        // 管理配置文件
        saveDefaultConfig();

        // lua
        global = JsePlatform.standardGlobals();
        // 加载lua
        LuaLoader.LoadGameLua(global);
        LuaLoader.LoadItem(global);
        getLogger().info("读取lua数据!");
        // 加载DungeonInfo
        DungeonManager.LoadDungeons();
        getLogger().info("读取地牢数据!");
        // 注册指令
        cmdHandler = new CommandHandler();
        cmdHandler.register("dfmc", new BaseCmd());
        cmdHandler.register("defaultWorldSet", new CmdSetDefaultWorld());
        cmdHandler.register("createDungeon", new CmdCreateDungeon());
        cmdHandler.register("deleteWorld", new CmdDeleteWorld());
        cmdHandler.register("deleteRoom", new CmdDeleteRoom());
        cmdHandler.register("enterDungeon", new CmdEnterDungeon());
        cmdHandler.register("dungeonInfo", new CmdDungeonInfo());
        cmdHandler.register("roomInfo", new CmdRoomInfo());
        cmdHandler.register("setRoomInfo", new CmdSetRoomInfo());
        cmdHandler.register("newRoom", new CmdNewRoom());
        cmdHandler.register("copyRoom", new CmdCopyRoom());
        cmdHandler.register("updateRoom", new CmdUpdateRoom());
        cmdHandler.register("luaCmd", new CmdLuaCmd());
        cmdHandler.register("gameTest", new CmdGameTest());
        cmdHandler.registerCB("gameTest", new CmdGameTest());
        this.getCommand("dfmc").setExecutor(cmdHandler);
        this.getCommand("dfmc").setTabCompleter(new BaseTabCompleter());

        // 注册事件
        EventNames.Events.forEach(clazz -> {
            EventNames.EventClassNames.put(clazz.getSimpleName(), clazz.getCanonicalName());
            try {
                getServer().getPluginManager().registerEvent(clazz, new Listener() {
                }, EventPriority.NORMAL, new EventExecutor() {
                    @Override
                    public void execute(Listener listener, Event event) throws EventException {
                        if (event == null) {
                            return;
                        }
                        if (!event.getClass().equals(clazz)) {
                            return;
                        }

                        var handlers = LuaListenerManager.luaEventHandlers.get(event.getClass());
                        if (handlers != null) {
                            // 执行handlers
                            handlers.forEach((tour, funcs) -> {

                                funcs.forEach(f -> {
                                    f.call(CoerceJavaToLua.coerce(event));
                                });
                            });
                        }
                        var dynamicHandlers = LuaListenerManager.luaEventDynamicHandlers.get(event.getClass());
                        if (dynamicHandlers != null) {
                            dynamicHandlers.forEach((tour, luas) -> {
                                luas.forEach((lua, funcs) -> {
                                    funcs.forEach(f -> {
                                        f.call(CoerceJavaToLua.coerce(event));
                                    });
                                });

                            });
                        }

                    }
                }, this, false);
            } catch (Exception e) {
                getLogger().info("事件 " + clazz + " 注册出错! " + e.getMessage());
            }
        });
        getServer().getPluginManager().registerEvents(new TourManager(), this);
        getServer().getPluginManager().registerEvents(new LuaListenerManager(), this);
        getServer().getPluginManager().registerEvents(new LuaGUIManager(), this);
        // 如果没有配置目录，创建。
        if (!Files.exists(Paths.get(getDataFolder() + "/"))) {
            try {
                Files.createDirectory(Paths.get(getDataFolder() + "/"));
            } catch (Exception e) {
                getLogger().info("插件rl创建配置目录出错");
            }
        }

    }

    @Override
    public void onDisable() {
    }

}