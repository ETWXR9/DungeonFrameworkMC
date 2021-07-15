package me.etwxr9.roguelike.Command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaTable;

import me.etwxr9.roguelike.Event.LuaCmdEvent;
import me.etwxr9.roguelike.Game.TourManager;

public class CmdLuaCmd implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 1) {
            return false;
        }
        var p = (Player) sender;
        var tour = TourManager.GetTour(p);
        if (tour == null) {
            return true;
        }
        LuaTable table = new LuaTable();
        for (int i = 0; i < args.length - 1; i++) {
            table.set(i + 1, args[i + 1]);
        }
        LuaCmdEvent lce = new LuaCmdEvent(tour, tour.dungeon, tour.room, tour.roomIndex, tour.GetFirstPlayer(), table);
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
