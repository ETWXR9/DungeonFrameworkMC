package me.etwxr9.roguelike.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.dungeonutils.DungeonManager;

public class CmdNewRoom implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // TODO Auto-generated method stub
        if (args.length != 1) {
            return false;
        }
        var p = (Player) sender;
        var dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            p.sendMessage("请使用enterdungeon进入一个地牢");
            return true;
        }
        if (dm.currentDungeon == null || dm.currentRoom == null) {
            p.sendMessage("请使用enterdungeon进入一个地牢");
            return true;
        }
        var room =DungeonManager.NewDefaultRoom(p, dm.currentDungeon);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

}
