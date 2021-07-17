package me.etwxr9.roguelike.Command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.Game.DungeonTour;
// import me.etwxr9.roguelike.Game.DungeonGUI;
import me.etwxr9.roguelike.Game.TourManager;

public class CmdGameTest implements CommandInterface {

    // 三种参数，start启动地牢，clear清理房间，open开启UI
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p;
        if (sender instanceof BlockCommandSender) {
            if (args.length != 3) {
                return false;
            }

            var result = Bukkit.selectEntities(sender, args[2]);
            var playerArray = result.stream().filter(e -> e.getType() == EntityType.PLAYER).toArray();
            if (playerArray.length < 1) {
                return true;
            }

            p = (Player) playerArray[0];
        } else if (sender instanceof Player) {
            if (args.length != 2) {
                return false;
            }
            p = (Player) sender;
        } else {
            return false;
        }
        switch (args[1]) {
            case "start":
                // 是否已经存在游戏
                if (TourManager.GetTour(p) != null) {
                    TourManager.Tours.forEach(t -> {
                        p.sendMessage("存在游戏" + t.player.get(0).getName());
                    });
                    p.sendMessage("当前已经存在游戏！");
                    break;
                }
                try {
                    p.sendMessage("启动游戏");
                    TourManager.NewTour(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case "end":
                // 是否已经存在游戏
                DungeonTour endTour = TourManager.GetTour(p);
                if (endTour == null) {
                    p.sendMessage("当前不存在游戏！");
                    break;
                }
                TourManager.EndTour(endTour);
                p.sendMessage("当前游戏结束！");
                break;
            case "gameInfo":
                if (TourManager.GetTour(p) == null) {
                    p.sendMessage("无当前关卡");
                    return true;
                }
                var tourInfo = TourManager.GetTour(p);
                p.sendMessage("游戏信息");
                p.sendMessage(" 当前房间 " + tourInfo.room.Id);
                p.sendMessage("游戏信息打印完毕");
                // p.sendMessage("当前Row " + tourInfo.row);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2) {
            return null;
        }
        return Arrays.asList("start", "gameInfo", "end");

    }

}
