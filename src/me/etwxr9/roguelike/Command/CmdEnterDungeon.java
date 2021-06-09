package me.etwxr9.roguelike.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.DungeonUtil.DungeonManager;

//玩家进入指定地牢，默认进入序号为0的房间
public class CmdEnterDungeon implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        var di = DungeonManager.GetDungeonInfo(args[1]);
        var dm = DungeonManager.GetDMbyPlayer(p);

        if (di == null) {
            p.sendMessage("地牢 " + args[1] + " 不存在");
        }
        p.sendMessage("准备进入地牢" + args[1] + " " + di);
        if (dm == null) {
            dm = DungeonManager.NewDungeonManager(p, di, di.Units.get(0), di.Units.get(0).Rooms.get(0));
        }
        if (args.length == 2) {
            DungeonManager.TeleportPlayerToRoom(dm, dm.currentDungeon, dm.currentRoom);
        } else if (args.length == 3) {
            var ri = di.GetRoom(args[2]);
            if (ri == null) {
                p.sendMessage("指定房间 " + args[2] + " 不存在");
                return true;
            }
            DungeonManager.TeleportPlayerToRoom(dm, di, ri);
        } else if (args.length == 4) {
            var ri = di.GetRoom(args[2]);
            if (ri == null) {
                p.sendMessage("指定房间 " + args[2] + " 不存在");
                return true;
            }
            int index;
            try {
                index = Integer.parseInt(args[3]);
            } catch (Exception e) {
                return false;
            }
            if (ri.Rooms.size() <= index) {
                p.sendMessage("该房间只有 " + ri.Rooms.size() + " 个副本");
                return true;
            }
            DungeonManager.TeleportPlayerToRoom(dm, di, ri, index);
            return true;
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
        } else if (args.length == 4) {
            var _di = DungeonManager.GetDungeonInfo(args[1]);
            if (_di != null) {
                var ri = _di.GetRoom(args[2]);
                // Main.getInstance().getLogger().info("enterdungeon参数四:"+ri.Id);
                if (ri != null) {
                    return Arrays.asList(Integer.toString(ri.Rooms.size()));
                }
            }

        }
        return null;
    }

}
