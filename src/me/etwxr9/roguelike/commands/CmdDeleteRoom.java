package me.etwxr9.roguelike.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.dungeonutils.DungeonInfo;
import me.etwxr9.roguelike.dungeonutils.DungeonManager;
import me.etwxr9.roguelike.dungeonutils.JsonIO;
import me.etwxr9.roguelike.dungeonutils.RoomInfo;

//根据第二个参数删除当前地牢内指定房间，根据第三个参数删除指定数量的房间，若位置全空则删除房间配置（参数为all则直接删除配置）
public class CmdDeleteRoom implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // TODO Auto-generated method stub
        Player p = (Player) sender;
        if (args.length != 3)
            return false;
        DungeonManager dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            p.sendMessage("无当前地牢");
            return true;
        }
        if (dm.currentDungeon.Units.size() == 0) {
            p.sendMessage("当前地牢无房间");
            return true;
        }
        // 根据参数取得相应的ri
        RoomInfo ri = dm.currentDungeon.GetRoom(args[1]);
        // 根据参数取得删除数量
        if (!args[2].equals("all")) {
            int count;
            try {
                count = Integer.parseInt(args[2]);
            } catch (Exception e) {
                return false;
            }
            // 判断是否全空
            if (count < ri.Rooms.size()) {
                for (int i = 0; i < ri.Rooms.size(); i++) {
                    if (i >= count)
                        dm.currentDungeon.Units.remove(i);
                }
            } else {
                dm.currentDungeon.Units.remove(ri);
            }
        } else {
            dm.currentDungeon.Units.remove(ri);
        }
        DungeonManager.SaveDungeon(dm.currentDungeon);
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

}
