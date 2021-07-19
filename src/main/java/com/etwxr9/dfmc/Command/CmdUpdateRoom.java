package com.etwxr9.dfmc.Command;

import java.util.List;

import com.etwxr9.dfmc.Dungeon.DungeonManager;
import com.etwxr9.dfmc.Dungeon.DungeonWE;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdUpdateRoom implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        DungeonManager dm = DungeonManager.GetDMbyPlayer(p);
        // 检查是否有WE
        if (!Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit").isEnabled()) {
            p.sendMessage("§c§l未检测到FastAsyncWorldEdit插件，无法执行复制操作");
            return true;
        }
        if (dm == null) {
            p.sendMessage("请用enterdungeon进入一个地牢");
            return true;
        }
        if (dm.currentRoom == null) {
            p.sendMessage("请用enterdungeon进入一个地牢");
            return true;
        }
        DungeonWE.CloneRoom(dm, p, -1);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
