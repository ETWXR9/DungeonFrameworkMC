package com.etwxr9.dfmc.Command;

import java.util.Arrays;
import java.util.List;

import com.etwxr9.dfmc.Dungeon.DungeonManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//一个参数：房间数量
public class CmdCopyRoom implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length != 2) {
            return false;
        }
        Player p = (Player) sender;
        int count;
        try {
            count = Integer.parseInt(args[1]);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        DungeonManager dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            p.sendMessage("请用enterdungeon进入一个地牢");
            return true;
        }
        dm.CloneRoom(p, count);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2) {
            return null;
        }
        return Arrays.asList("<count>");
    }

}
