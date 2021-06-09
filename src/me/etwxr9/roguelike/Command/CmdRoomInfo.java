package me.etwxr9.roguelike.Command;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.DungeonUtil.DungeonManager;

//查看当前地牢当前房间信息
public class CmdRoomInfo implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        var dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            p.sendMessage("请进入一个房间");
            return true;
        }
        if (dm.currentPosition == null) {
            p.sendMessage("请进入一个房间");
            return true;
        }
        var di = dm.currentDungeon;
        var ri = dm.currentRoom;
        
        p.sendMessage(MessageFormat.format("查看房间信息：所属地牢：{0}， Id：{1}, 序号：{2}", di.World,ri.Id,ri.Rooms.indexOf(dm.currentPosition)));
        p.sendMessage(MessageFormat.format("单元大小：{0}",Arrays.toString(di.UnitSize)));
        p.sendMessage(MessageFormat.format("房间副本数量：{0}", ri.Rooms.size()));
        p.sendMessage(MessageFormat.format("房间类型：{0}", ri.Type));
        p.sendMessage(MessageFormat.format("玩家传送点：{0}",Arrays.toString(ri.PlayerPosition)));
        p.sendMessage(MessageFormat.format("敌人生成点数量：{0}", ri.EnemyPosition.size()));
        ri.EnemyPosition.forEach(pos->{
            p.sendMessage(MessageFormat.format("敌人生成点坐标：{0}",Arrays.toString(pos)));
        });
        p.sendMessage(MessageFormat.format("房间通过条件：{0}", ri.Clear));
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