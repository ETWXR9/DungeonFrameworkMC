package me.etwxr9.roguelike;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.plugin.java.JavaPlugin;

import me.etwxr9.roguelike.DungeonUtil.DungeonManager;
import me.etwxr9.roguelike.Game.DungeonGUI;
import me.etwxr9.roguelike.Game.TourManager;
import me.etwxr9.roguelike.Command.BaseCmd;
import me.etwxr9.roguelike.Command.BaseTabCompleter;
import me.etwxr9.roguelike.Command.CmdCopyRoom;
import me.etwxr9.roguelike.Command.CmdCreateDungeon;
import me.etwxr9.roguelike.Command.CmdDeleteRoom;
import me.etwxr9.roguelike.Command.CmdDeleteWorld;
import me.etwxr9.roguelike.Command.CmdDungeonInfo;
import me.etwxr9.roguelike.Command.CmdEnterDungeon;
import me.etwxr9.roguelike.Command.CmdGameTest;
import me.etwxr9.roguelike.Command.CmdNewRoom;
import me.etwxr9.roguelike.Command.CmdNewWorld;
import me.etwxr9.roguelike.Command.CmdRoomInfo;
import me.etwxr9.roguelike.Command.CmdSetDefaultWorld;
import me.etwxr9.roguelike.Command.CmdSetRoomInfo;
import me.etwxr9.roguelike.Command.CmdSpawnEnemy;
import me.etwxr9.roguelike.Command.CmdUpdateRoom;
import me.etwxr9.roguelike.Command.CommandHandler;

public class Main extends JavaPlugin {

    // 获取单例
    private static Main i;

    public static Main getInstance() {
        return i;
    }

    public CommandHandler cmdHandler;

    @Override
    public void onEnable() {
        i = this;

        // 管理配置文件
        saveDefaultConfig();

        // 加载DungeonInfo
        DungeonManager.LoadDungeons();
        getLogger().info("读取地牢数据!");
        // 注册指令
        cmdHandler = new CommandHandler();
        cmdHandler.register("rl", new BaseCmd());
        cmdHandler.register("createDungeon", new CmdCreateDungeon());
        cmdHandler.register("newWorld", new CmdNewWorld());
        cmdHandler.register("setDefaultWorld", new CmdSetDefaultWorld());
        cmdHandler.register("deleteWorld", new CmdDeleteWorld());
        cmdHandler.register("enterDungeon", new CmdEnterDungeon());
        cmdHandler.register("dungeonInfo", new CmdDungeonInfo());
        cmdHandler.register("roomInfo", new CmdRoomInfo());
        cmdHandler.register("deleteRoom", new CmdDeleteRoom());
        cmdHandler.register("setRoomInfo", new CmdSetRoomInfo());
        // cmdHandler.register("roomInfo", new CmdRoomInfo());
        cmdHandler.register("newRoom", new CmdNewRoom());
        cmdHandler.register("copyRoom", new CmdCopyRoom());
        cmdHandler.register("updateRoom", new CmdUpdateRoom());
        cmdHandler.register("spawnEnemy", new CmdSpawnEnemy());
        cmdHandler.register("gameTest", new CmdGameTest());
        this.getCommand("rl").setExecutor(cmdHandler);
        this.getCommand("rl").setTabCompleter(new BaseTabCompleter());

        // 注册事件
        getServer().getPluginManager().registerEvents(new DungeonGUI(), this);
        getServer().getPluginManager().registerEvents(new TourManager(), this);

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
