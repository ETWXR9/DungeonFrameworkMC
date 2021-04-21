package me.etwxr9.roguelike.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.dungeonutils.DungeonManager;

//玩家进入指定地牢，默认进入序号为0的房间
public class CmdEnterDungeon implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        var di = DungeonManager.GetDungeonInfo(args[1]);
        var dm = DungeonManager.GetDMbyPlayer(p);

        p.sendMessage("准备进入地牢" + args[1] + " " + di);
        if (di != null) {
            if (dm != null) {
                dm.currentDungeon = di;
                p.sendMessage("di.units.size=" + di.Units.size());
                dm.currentRoom = di.Units.get(0);
            } else {
                dm = DungeonManager.NewDungeonManager(p, di, di.Units.get(0));
            }
            if (args.length == 2) {
                DungeonManager.TeleportPlayerToRoom(p, dm.currentDungeon, dm.currentRoom);
            } else if (args.length == 3) {
                var ri = di.GetRoom(args[2]);
                if (ri == null) {
                    p.sendMessage("指定房间 "+args[2]+" 不存在");
                    return true;
                }
                dm.currentRoom = ri;
                DungeonManager.TeleportPlayerToRoom(p, dm.currentDungeon, dm.currentRoom);
            }
        } else {
            p.sendMessage("指定地牢不存在");
            return true;

        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            var dis = DungeonManager.GetDIList();
            // sender.sendMessage("tab enterdungeon dis is null " + (dis == null));
            // sender.sendMessage("tab enterdungeon " + dis.size());
            // sender.sendMessage("tab enterdungeon " + dis.get(0));
            // sender.sendMessage("tab enterdungeon " + dis.get(0).World);
            var names = new ArrayList<String>();
            dis.forEach(d -> names.add(d.World));
            return names;
        } else if (args.length == 3) {
            var di = DungeonManager.GetDungeonInfo(args[1]);
            if (di != null) {
                var names = new ArrayList<String>();
                di.Units.forEach(d -> names.add(d.Id));
                return names;
            }
        }
        return null;
    }

}
