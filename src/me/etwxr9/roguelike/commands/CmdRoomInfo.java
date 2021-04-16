package me.etwxr9.roguelike.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.dungeonutils.DungeonManager;

//查看当前地牢中指定Id的房间信息
public class CmdRoomInfo implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length != 2)
            return false;
        var di = DungeonManager.GetDungeonInfo(args[1]);
        Player p = (Player) sender;
        if (di == null) {
            p.sendMessage(String.format("不存在地牢：{0}", di.World));
            return true;
        }
        p.sendMessage(String.format("查看地牢信息：{0}", di.World));
        p.sendMessage(String.format("原点坐标：{0},{1},{2}", di.Origin[0], di.Origin[1], di.Origin[2]));
        p.sendMessage(String.format("地牢大小：{0},{1},{2}", di.Size[0], di.Size[1], di.Size[2]));
        p.sendMessage(String.format("单元大小：{0},{1},{2}", di.UnitSize[0], di.UnitSize[1], di.UnitSize[2]));
        p.sendMessage(String.format("房间数量：{0}", di.Units.size()));
        di.Units.forEach(
                d -> p.sendMessage(String.format("   房间名：{0}，房间类型：{1}，房间数量：{2}", d.Id, d.Type, d.Rooms.size())));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        if (args.length != 2)
            return null;
        var names = new ArrayList<String>();
        DungeonManager.GetDIList().forEach(d -> names.add(d.World));
        return names;
    }

}