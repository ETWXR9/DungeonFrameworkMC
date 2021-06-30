package me.etwxr9.roguelike.Command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.DungeonUtil.DungeonManager;

public class CmdUpdateRoom implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        DungeonManager dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            p.sendMessage("请用enterdungeon进入一个地牢");
            return true;
        }
        if (dm.currentRoom == null) {
            p.sendMessage("请用enterdungeon进入一个地牢");
            return true;
        }
        DungeonManager.CloneRoom(p, -1);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

}
