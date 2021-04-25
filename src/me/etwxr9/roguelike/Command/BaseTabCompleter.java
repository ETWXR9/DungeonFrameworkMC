package me.etwxr9.Roguelike.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import me.etwxr9.Roguelike.Main;

public class BaseTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return null;
        if (args.length == 1) {
            final List<String> completions = new ArrayList<>();
            // copy matches of first argument from list (ex: if first arg is 'm' will return
            // just 'minecraft')
            StringUtil.copyPartialMatches(args[0], Main.getInstance().cmdHandler.commandList(), completions);
            // sort the list
            Collections.sort(completions);
            completions.remove("rl");
            return completions;
        } else {
            if (Main.getInstance().cmdHandler.getExecutor(args[0]) == null)return null;
            //当有两个以上参数时，使用subcommand提供的onTabComplete
            return Main.getInstance().cmdHandler.getExecutor(args[0]).onTabComplete(sender, command, alias, args);
        }

    }
}
