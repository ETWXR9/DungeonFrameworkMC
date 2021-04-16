package me.etwxr9.roguelike.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.dungeonutils.DungeonManager;

//查看指定名称的地牢信息，或者指定Id的房间信息
public class CmdDungeonInfo implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 2) {
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

        else if (args.length == 3) {
            var di = DungeonManager.GetDungeonInfo(args[1]);
            Player p = (Player) sender;
            if (di == null) {
                p.sendMessage(String.format("不存在地牢：{0}", args[1]));
                return true;
            }
            var ri = di.GetRoom(args[2]);
            if (ri == null) {
                p.sendMessage(String.format("不存在房间：{0}", args[2]));
                return true;
            }
            p.sendMessage(String.format("查看房间信息：{0}", ri.Id));
            p.sendMessage(String.format("房间类型：{0}", ri.Type));
            p.sendMessage(String.format("玩家传送点：{0},{1},{2}", ri.PlayerPosition[0], ri.PlayerPosition[1], ri.PlayerPosition[2]));
            p.sendMessage(String.format("敌人生成点数量：{0}", ri.EnemyPosition.size()));
            ri.EnemyPosition.forEach(pos->p.sendMessage(String.format("敌人生成点坐标：{0},{1},{2}", pos[0], pos[1], pos[2])));
            p.sendMessage(String.format("房间通过条件：{0}", ri.Clear));
            p.sendMessage(String.format("房间副本数量：{0}", ri.Rooms.size()));
            return true;
        }
        return false;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        if (args.length == 2){
            var names = new ArrayList<String>();
            DungeonManager.GetDIList().forEach(d -> names.add(d.World));
            return names;
        }else if (args.length == 3) {
            var di = DungeonManager.GetDungeonInfo(args[1]);
            if (di == null) {
                return null;
            }
            var names = new ArrayList<String>();
            di.Units.forEach(r -> names.add(r.Id));
            return names;
        }
        return null;

    }

}
