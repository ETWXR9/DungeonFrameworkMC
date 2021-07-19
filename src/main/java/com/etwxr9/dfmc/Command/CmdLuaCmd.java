package com.etwxr9.dfmc.Command;

import java.util.Arrays;
import java.util.List;

import com.etwxr9.dfmc.Event.LuaCmdEvent;
import com.etwxr9.dfmc.Game.TourManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaTable;

public class CmdLuaCmd implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 1) {
            return false;
        }
        var p = (Player) sender;
        LuaTable table = new LuaTable();
        for (int i = 0; i < args.length - 1; i++) {
            table.set(i + 1, args[i + 1]);
        }
        LuaCmdEvent lce = new LuaCmdEvent(p, table);
        Bukkit.getPluginManager().callEvent(lce);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2) {
            return null;
        }
        return Arrays.asList("<roomid>");
    }

}
