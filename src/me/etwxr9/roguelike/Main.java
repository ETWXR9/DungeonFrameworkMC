package me.etwxr9.roguelike;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.plugin.java.JavaPlugin;

import me.etwxr9.roguelike.commands.BaseCmd;
import me.etwxr9.roguelike.commands.BaseTabCompleter;
import me.etwxr9.roguelike.commands.CmdCopyRoom;
import me.etwxr9.roguelike.commands.CmdCreateDungeon;
import me.etwxr9.roguelike.commands.CmdDeleteRoom;
import me.etwxr9.roguelike.commands.CmdDeleteWorld;
import me.etwxr9.roguelike.commands.CmdDungeonInfo;
import me.etwxr9.roguelike.commands.CmdEnterDungeon;
import me.etwxr9.roguelike.commands.CmdNewRoom;
import me.etwxr9.roguelike.commands.CmdNewWorld;
import me.etwxr9.roguelike.commands.CmdRoomInfo;
import me.etwxr9.roguelike.commands.CmdSetDefaultWorld;
import me.etwxr9.roguelike.commands.CmdSetRoomInfo;
import me.etwxr9.roguelike.commands.CommandHandler;
import me.etwxr9.roguelike.dungeonutils.DungeonManager;

public class Main extends JavaPlugin{

    //获取单例
    private static Main i;
    public static Main getInstance(){
        return i;
    }

    public CommandHandler cmdHandler;

    @Override
    public void onEnable(){
        i = this;
        
        //管理配置文件
        saveDefaultConfig();

        //加载DungeonInfo
        DungeonManager.LoadDungeons();
        getLogger().info("读取地牢数据!");
        //注册指令
        cmdHandler = new CommandHandler();
        cmdHandler.register("rl", new BaseCmd());
        cmdHandler.register("createDungeon", new CmdCreateDungeon());
        cmdHandler.register("newWorld", new CmdNewWorld());
        cmdHandler.register("setDefaultWorld", new CmdSetDefaultWorld());
        cmdHandler.register("deleteWorld", new CmdDeleteWorld());
        cmdHandler.register("enterDungeon", new CmdEnterDungeon());
        cmdHandler.register("dungeonInfo", new CmdDungeonInfo());
        cmdHandler.register("deleteRoom", new CmdDeleteRoom());
        cmdHandler.register("setRoomInfo", new CmdSetRoomInfo());
        cmdHandler.register("roomInfo", new CmdRoomInfo());
        cmdHandler.register("newRoom", new CmdNewRoom());
        cmdHandler.register("copyRoom", new CmdCopyRoom());
        this.getCommand("rl").setExecutor(cmdHandler);
        this.getCommand("rl").setTabCompleter(new BaseTabCompleter());


        //如果没有配置目录，创建。
        if (!Files.exists(Paths.get(getDataFolder()+"/"))) {
            try {
                Files.createDirectory(Paths.get(getDataFolder()+"/"));
            } catch (Exception e) {
                getLogger().info("插件rl创建配置目录出错");
            }
        }

    }
    @Override
    public void onDisable(){}

}
