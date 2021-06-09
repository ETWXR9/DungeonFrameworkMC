package me.etwxr9.roguelike.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import me.etwxr9.roguelike.DungeonUtil.DungeonManager;

public class CmdSpawnEnemy implements CommandInterface{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // TODO Auto-generated method stub
        if (args.length != 2) {
            return false;
        }
        EntityType mob;
        Player  p = (Player)sender;
        try {
            mob = EntityType.valueOf(args[1]);
        } catch (Exception e) {
            p.sendMessage(args[1]+" 不存在");
            return false;
        }
        var dm = DungeonManager.GetDMbyPlayer(p);
        if (dm.currentRoom == null) {
            p.sendMessage("请用enterdungeon进入房间");
            return true;
        }
        dm.currentRoom.EnemyPosition.forEach(pos->{
            var locpos = DungeonManager.GetPoint(dm.currentDungeon, dm.currentPosition, pos);
            p.getWorld().spawnEntity(new Location(p.getWorld(), locpos[0], locpos[1], locpos[2]), mob);
            p.sendMessage("生成怪物"+mob.name()+" 于坐标"+locpos[0]+","+locpos[1]+","+ locpos[2]);
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2) {
            return null;
        }
        var names = new ArrayList<String>();
        for (EntityType et : EntityType.values()){
            names.add(et.name());
        }
        return names;
    }
    
}
