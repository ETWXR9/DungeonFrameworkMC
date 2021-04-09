package me.etwxr9.roguelike.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.Main;
import me.etwxr9.roguelike.VoidChunkGenerator;

public class CmdNewWorld implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //检查参数个数和类型
        if (args.length != 2)return false;
        if (args[1] == "") return false;
        Player p = (Player)sender;
        //检查是否存在同名世界
        if (Main.getInstance().getServer().getWorlds().stream().anyMatch(w->w.getName()==args[1])) {
            p.sendMessage("同名世界已经存在！");
            return true;
        }
        //生成世界并传送
        WorldCreator wc = new WorldCreator(args[1]);
        wc.generateStructures(false);

        //这个VoidChunkGenerator重写了generateChunkData方法，只会生成空区块。
        wc.generator(new VoidChunkGenerator());
        World newWorld = wc.createWorld();

        //传送玩家
        Player player = (Player) sender;
        player.sendMessage("生成完毕，正在传送");
        // Location loc = newWorld.getSpawnLocation();
        Location loc1 = new Location(newWorld,0,64,0);
        player.teleport(loc1);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length !=2) return null;
        return Arrays.asList("<worldName>");
    }
    
}
