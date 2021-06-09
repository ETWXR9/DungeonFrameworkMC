package me.etwxr9.roguelike.Command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandInterface {
    //指令执行逻辑
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args);
    //补全逻辑（从第三个参数开始补全）
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);
}
