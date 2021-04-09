package me.etwxr9.roguelike.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.dungeonutils.DungeonManager;

//玩家进入指定地牢，默认进入序号为0的房间
public class CmdEnterDungeon implements CommandInterface{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(args.length!=2)return false;
        Player p = (Player)sender;
        var di = DungeonManager.GetDungeonInfo(args[1]);
        var dm = DungeonManager.GetDMbyPlayer(p);
        if (di!=null) {
            if (dm!=null) {
                dm.currentDungeon = di;
                dm.currentRoom = di.Units.get(0);
            }else{
                dm = DungeonManager.NewDungeonManager(p, di, di.Units.get(0));
            }
            DungeonManager.TeleportPlayerToRoom(p, dm.currentDungeon, dm.currentRoom);
        }else{
            p.sendMessage("指定地牢不存在");
            return true;
        }


        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2) return null;
        var dis = DungeonManager.GetDIList();
        var names = new ArrayList<String>();
        dis.forEach(d->names.add(d.World));
        return names;
    }
    
}
