package me.etwxr9.roguelike.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.dungeonutils.DungeonManager;

//一个参数：房间数量
public class CmdCopyRoom implements CommandInterface{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // TODO Auto-generated method stub
        if (args.length!=2) {
            return false;
        }
        Player p = (Player)sender;
        int count;
        try {
            count = Integer.parseInt(args[1]); 
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
            return false;
        }
        if (DungeonManager.GetDMbyPlayer(p) == null) {
            p.sendMessage("请用enterdungeon进入一个地牢");
            return true;
        }
        DungeonManager.CloneRoom(p, count);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length!=2) {
            return null;
        }
        return Arrays.asList("<count>");
    }
    
}
