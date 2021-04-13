package me.etwxr9.roguelike.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.dungeonutils.DungeonInfo;
import me.etwxr9.roguelike.dungeonutils.DungeonManager;
import me.etwxr9.roguelike.dungeonutils.JsonIO;

public class CmdCreateDungeon implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 1)
            return false;

        Player p = (Player) sender;
        var worldName = p.getWorld().getName();
        try {
            if (JsonIO.CreateFile(worldName, new int[] { 0, 0, 0 }, new int[] { 20, 10, 20 },
                    new int[] { 50, 20, 50 })) {
                p.sendMessage("创建成功!");
            } else {
                p.sendMessage("创建失败！文件已存在。");
                return true;
            }

        } catch (Exception e) {
            p.sendMessage("写入文件出错： " + e.getMessage());
            return true;
        }


        p.sendMessage("开始读取刚创建的文件！");
        DungeonInfo newDungeon = new DungeonInfo();
        try {
            var data = JsonIO.ReadFile(worldName);
            // p.sendMessage("读取JSON内容为：" + data);
            newDungeon = JsonIO.ParseDungeonInfo(data);
            p.sendMessage("读取世界ID为：" + newDungeon.World);
            p.sendMessage("读取世界Room数量为：" + newDungeon.Units.size());
        } catch (Exception e) {
            p.sendMessage("读取文件出错： " + e.getMessage());
            return true;
        }
        //在0,0,0创建一个新房间，并传送玩家,设定当前房间
        var room = DungeonManager.NewDefaultRoom(p, newDungeon, new int[]{0,0,0});
        //这里应当重加载一遍DM中的diList，然后使用DM提供的函数加载di
        DungeonManager.LoadDungeons();
        //为该玩家设定DM
        DungeonManager.NewDungeonManager(p, newDungeon, room);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

}
