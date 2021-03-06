package com.etwxr9.dfmc.Command;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.etwxr9.dfmc.Main;
import com.etwxr9.dfmc.Dungeon.DungeonManager;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

//查看当前地牢当前房间信息
public class CmdRoomInfo implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        var dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            p.sendMessage("请进入一个房间");
            return true;
        }
        if (dm.currentPosition == null) {
            p.sendMessage("请进入一个房间");
            return true;
        }
        var di = dm.currentDungeon;
        var ri = dm.currentRoom;

        p.sendMessage(MessageFormat.format("§b查看房间信息：所属地牢：{0}， Id：{1}, 序号：{2}", di.Id, ri.Id,
                ri.Rooms.indexOf(dm.currentPosition)));
        p.sendMessage(MessageFormat.format("单元大小：{0}", Arrays.toString(di.RoomSize)));
        p.sendMessage(MessageFormat.format("房间副本数量：{0}", ri.Rooms.size()));
        p.sendMessage("房间Tag：");
        ri.Tags.forEach(t -> {
            p.sendMessage("    " + t);
        });
        p.sendMessage("特殊点：");
        ri.SpecialPositions.forEach((k, v) -> {
            p.sendMessage("    " + Arrays.toString(k) + ":" + v);
        });
        p.sendMessage(MessageFormat.format("玩家传送点：{0}", Arrays.toString(ri.PlayerPosition)));
        p.sendMessage("§b房间信息打印完毕");
        new BukkitRunnable() {
            private int count = 20;

            @Override
            public void run() {
                count--;

                ri.SpecialPositions.forEach((k, v) -> {
                    var pos = DungeonManager.GetPoint(di, dm.currentPosition, k);
                    p.getWorld().spawnParticle(Particle.REDSTONE,
                            new Location(p.getWorld(), pos[0] + 0.5, pos[1] + 0.5, pos[2] + 0.5), 10, 0.1, 0.1, 0.1,
                            new DustOptions(Color.AQUA, 1));
                });
                var pos = DungeonManager.GetPoint(di, dm.currentPosition, ri.PlayerPosition);
                p.getWorld().spawnParticle(Particle.REDSTONE,
                        new Location(p.getWorld(), pos[0] + 0.5, pos[1] + 0.5, pos[2] + 0.5), 10, 0.1, 0.1, 0.1,
                        new DustOptions(Color.RED, 1));
                if (count == 0) {
                    cancel();
                }
            }

        }.runTaskTimer(Main.getPlugin(Main.class), 0, 10);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2)
            return null;
        var names = new ArrayList<String>();
        DungeonManager.GetDIList().forEach(d -> names.add(d.Id));
        return names;
    }

}