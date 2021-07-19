package com.etwxr9.dfmc;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.etwxr9.dfmc.Command.BaseCmd;
import com.etwxr9.dfmc.Command.BaseTabCompleter;
import com.etwxr9.dfmc.Command.CmdCopyRoom;
import com.etwxr9.dfmc.Command.CmdCreateDungeon;
import com.etwxr9.dfmc.Command.CmdDeleteRoom;
import com.etwxr9.dfmc.Command.CmdDeleteWorld;
import com.etwxr9.dfmc.Command.CmdDungeonInfo;
import com.etwxr9.dfmc.Command.CmdEnterDungeon;
import com.etwxr9.dfmc.Command.CmdLuaCmd;
import com.etwxr9.dfmc.Command.CmdNewRoom;
import com.etwxr9.dfmc.Command.CmdRoomInfo;
import com.etwxr9.dfmc.Command.CmdSetDefaultWorld;
import com.etwxr9.dfmc.Command.CmdSetRoomInfo;
import com.etwxr9.dfmc.Command.CmdTour;
import com.etwxr9.dfmc.Command.CmdUpdateRoom;
import com.etwxr9.dfmc.Command.CommandHandler;
import com.etwxr9.dfmc.Dungeon.DungeonManager;
import com.etwxr9.dfmc.Game.TourManager;
import com.etwxr9.dfmc.Lua.DungeonLuaManager;
import com.etwxr9.dfmc.Lua.GlobalLuaListenerManager;
import com.etwxr9.dfmc.Lua.GlobalLuaManager;
import com.etwxr9.dfmc.Lua.LuaAPI;
import com.etwxr9.dfmc.Lua.DungeonLuaListenerManager;
import com.etwxr9.dfmc.Lua.LuaGUI.LuaGUIManager;
import com.etwxr9.dfmc.Utility.EventNames;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaMethod;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class Main extends JavaPlugin {

    // 获取单例
    private static Main i;

    public static Main getInstance() {
        return i;
    }

    public CommandHandler cmdHandler;
    public DungeonLuaManager dungeonLuaManager;
    public GlobalLuaManager globalLuaManager;
    public DungeonLuaListenerManager luaListenerManager = DungeonLuaListenerManager.getInstance();
    public GlobalLuaListenerManager globalLuaListenerManager = GlobalLuaListenerManager.getInstance();

    @Override
    public void onEnable() {
        i = this;
        // getLogger().info(System.getProperty("java.class.path"));
        // 管理配置文件
        saveDefaultConfig();

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
                        // 调用全局永久lua
                        var globalLuaEventHandlers = globalLuaListenerManager.luaEventHandlers.get(event.getClass());
                        if (globalLuaEventHandlers != null) {
                            // 执行handlers
                            globalLuaEventHandlers.forEach((f) -> {
                                f.call(CoerceJavaToLua.coerce(event));
                            });
                        }
                        // 调用全局动态lua
                        var dynamicLuaEventHandlers = globalLuaListenerManager.dynamicLuaEventHandlers
                                .get(event.getClass());
                        if (dynamicLuaEventHandlers != null) {
                            // 执行handlers
                            dynamicLuaEventHandlers.forEach((lua, funcs) -> {
                                funcs.forEach(f -> {
                                    f.call(CoerceJavaToLua.coerce(event));
                                });

                            });
                        }
                        // 调用地牢永久lua
                        var dungeonLuaEventHandlers = luaListenerManager.dungeonLuaEventHandlers.get(event.getClass());
                        if (dungeonLuaEventHandlers != null) {
                            // 执行handlers
                            dungeonLuaEventHandlers.forEach((tour, funcs) -> {
                                funcs.forEach(f -> {
                                    f.call(CoerceJavaToLua.coerce(event));
                                });
                            });
                        }
                        // 调用地牢动态lua
                        var dungeonDynamicLuaEventHandlers = luaListenerManager.dungeonDynamicLuaEventHandlers
                                .get(event.getClass());
                        if (dungeonDynamicLuaEventHandlers != null) {
                            dungeonDynamicLuaEventHandlers.forEach((tour, luas) -> {
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

        // 加载全局lua
        globalLuaManager = GlobalLuaManager.getInstance();
        globalLuaManager.LoadGlobalPermanentLuas();
        globalLuaManager.LoadGlobalDynamicLuas();

        // 加载地牢lua
        dungeonLuaManager = DungeonLuaManager.getInstance();
        dungeonLuaManager.LoadDungeonPermanentLuas();
        dungeonLuaManager.LoadDungeonDynamicLuas();

        getLogger().info("读取lua数据完毕!");
        // 加载DungeonInfo
        DungeonManager.LoadDungeons();
        getLogger().info("读取地牢数据完毕!");
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
        cmdHandler.register("tour", new CmdTour());
        cmdHandler.registerCB("tour", new CmdTour());
        this.getCommand("dfmc").setExecutor(cmdHandler);
        this.getCommand("dfmc").setTabCompleter(new BaseTabCompleter());

        getServer().getPluginManager().registerEvents(TourManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(LuaGUIManager.getInstance(), this);
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

    // 用于导出lua补全用的LuaAPI
    public static void PrintLuaAPI() {
        // 导出LuaAPI.lua

        JavaProjectBuilder builder = new JavaProjectBuilder();
        try {
            builder.addSource(new FileReader(
                    "D:\\GameS\\paper plugin\\dfmc\\dfmc\\src\\main\\java\\com\\etwxr9\\dfmc\\Lua\\LuaAPI.java"));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        var luaapifuncs = builder.getClassByName(LuaAPI.class.getName()).getMethods();
        var fileContent = new StringBuilder();
        fileContent.append("LuaAPI = {}");
        fileContent.append("\r\n");
        for (JavaMethod method : luaapifuncs) {
            // 首先读取注释
            // Access the JavaDoc comment
            String comment = method.getComment();
            if (comment != "" && comment != null) {
                fileContent.append("---" + comment);
                fileContent.append("\r\n");
            }
            // 读取返回注释
            DocletTag returns = method.getTagByName("return");
            if (returns != null) {

                if (returns.getValue() != "") {
                    fileContent.append("---@return " + method.getReturnType().getValue() + " " + returns.getValue());
                    fileContent.append("\r\n");
                }
            }
            // 读取参数注释
            var params = method.getTagsByName("param");
            var paramsTypes = method.getParameterTypes();
            for (int i = 0; i < params.size(); i++) {

                var param = params.get(i);

                if (param != null) {
                    if (param.getValue() != "") {
                        var spacePos = param.getValue().indexOf(" ");
                        String pName = "";
                        String pComment = "";
                        if (spacePos == -1) {
                            pName = param.getValue();
                        } else {
                            pName = param.getValue().substring(0, spacePos);
                            pComment = param.getValue().substring(spacePos);
                        }

                        fileContent.append("---@param " + pName + " " + paramsTypes.get(i).getValue() + " " + pComment);
                        fileContent.append("\r\n");
                    }
                }
            }
            // 构建函数主体
            var args = method.getParameters();
            fileContent.append("function LuaAPI:" + method.getName() + "(");
            for (int i = 0; i < args.size(); i++) {
                var arg = args.get(i);
                fileContent.append(arg.getName());
                if (i < args.size() - 1) {
                    fileContent.append(",");
                }
            }
            fileContent.append(")");
            fileContent.append("\r\n");
            fileContent.append("end");
            fileContent.append("\r\n");
            fileContent.append("\r\n");
        }
        System.out.println(fileContent.toString());
    }
}