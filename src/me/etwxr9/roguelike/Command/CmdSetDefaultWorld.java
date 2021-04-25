package me.etwxr9.Roguelike.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.etwxr9.Roguelike.Main;

public class CmdSetDefaultWorld implements CommandInterface{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length != 2)return false;

        Player p = (Player) sender;
        FileConfiguration config = Main.getInstance().getConfig();
        String worldName = args[1];

        if (config.getString("DefaultWorld")==null) {
            config.addDefault("DefaultWorld", worldName);
            config.options().copyDefaults(true);
        }else{
            config.set("DefaultWorld", worldName);
        }
        Main.getInstance().saveConfig();
        p.sendMessage("已经将 "+worldName+" 设置为默认世界！");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2)return null;
        var worldNameList = new ArrayList<String>();
        Main.getInstance().getServer().getWorlds().forEach(w->worldNameList.add(w.getName()));
        return worldNameList;
    }
    
}
