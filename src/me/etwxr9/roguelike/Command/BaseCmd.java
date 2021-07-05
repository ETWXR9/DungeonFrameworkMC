package me.etwxr9.roguelike.Command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BaseCmd implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // Because in our CommandHandler we are already checking if the sender's
        // instance is a player, we don't have to do it here.
        Player p = (Player) sender;

        p.sendMessage("Example!");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
