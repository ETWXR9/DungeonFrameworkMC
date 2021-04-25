package me.etwxr9.Roguelike.Command;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.Roguelike.DungeonUtil.DungeonManager;

//设置当前房间的配置，敌人位置有set/unset/clear三种
public class CmdSetRoomInfo implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length < 2) {
            return false;
        }
        var p = (Player) sender;
        var dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            p.sendMessage("无当前地牢");
            return true;
        }
        if (dm.currentRoom == null) {
            p.sendMessage("无当前房间");
            return true;
        }

        var loc = p.getLocation();
        var roomOrigin = DungeonManager.GetPoint(dm.currentDungeon, dm.currentPosition, new int[]{0,0,0});
        var pos = new int[] { loc.getBlockX()-roomOrigin[0], loc.getBlockY()-roomOrigin[1], loc.getBlockZ()-roomOrigin[2] };
        switch (args[1]) {
        case "enemyPosition":
            if (args.length != 3) {
                return false;
            }
            switch (args[2]) {
            case "set":

                if (loc.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {

                    if (dm.currentRoom.EnemyPosition.contains(pos)) {
                        p.sendMessage("该位置已经存在");
                    } else {
                        dm.currentRoom.EnemyPosition.add(pos);
                        p.sendMessage(MessageFormat.format("增加敌人生成位置为：{0},{1},{2}", pos[0], pos[1], pos[2]));
                    }
                }
                break;
            case "unset": {
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
                    if (dm.currentRoom.EnemyPosition.contains(pos)) {
                        dm.currentRoom.EnemyPosition.remove(pos);
                        p.sendMessage("已移除");
                    }
                }
                break;
            }
            case "clear": {
                dm.currentRoom.EnemyPosition.clear();
                break;
            }
            default:
                break;
            }
            break;
        case "playerPostion":
            if (loc.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {

                if (dm.currentRoom.PlayerPosition.equals(pos)) {
                    p.sendMessage("该位置已经存在");
                } else {
                    dm.currentRoom.PlayerPosition = pos;
                    p.sendMessage(MessageFormat.format("该位置已经设置：{0},{1},{2}", pos[0], pos[1], pos[2]));
                }
            }
            break;
        case "clear":
            if (args.length != 3) {
                return false;
            }
            dm.currentRoom.Clear = args[2];
            break;
        case "id":// 这里需要做下重复检查
            if (args.length != 3) {
                return false;
            }
            dm.currentRoom.Id = args[2];
            break;
        case "type":
            if (args.length != 3) {
                return false;
            }
            dm.currentRoom.Type = args[2];
            break;
        default:
            break;
        }
        dm.SaveDungeon();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            var configs = Arrays.asList("enemyPosition", "playerPostion", "clear", "id", "type");
            return configs;
        } else if (args.length == 3) {
            List<String> items;
            switch (args[1]) {
            case "enemyPosition":
                items = Arrays.asList("set", "unset", "clear");
                return items;
            default:
                break;
            }
        }

        return null;

    }

}
