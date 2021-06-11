package me.etwxr9.roguelike.Command;

import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.DungeonUtil.EnemyJsonParser;
import me.etwxr9.roguelike.Game.EnemyData;
import me.etwxr9.roguelike.Game.EnemyManager;

public class CmdEnemyLoadTest implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // TODO Auto-generated method stub
        Player p = (Player) sender;
        if (args.length != 2)
            return false;
        if (args[1].toString().equals("test")) {
            p.sendMessage("读取敌人数据共 " + EnemyManager.Enemys.size() + " 个");
            EnemyManager.Enemys.values().forEach(e -> {
                p.sendMessage("敌人组 " + e.Id);
            });
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

}
