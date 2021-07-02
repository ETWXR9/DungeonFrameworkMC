package me.etwxr9.roguelike.Command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.Game.DungeonTour;
// import me.etwxr9.roguelike.Game.DungeonGUI;
import me.etwxr9.roguelike.Game.TourManager;

public class CmdGameTest implements CommandInterface {

    // 三种参数，start启动地牢，clear清理房间，open开启UI
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // TODO Auto-generated method stub
        if (args.length != 2) {
            return false;
        }
        Player p = (Player) sender;

        switch (args[1]) {
            case "start":
                // 是否已经存在游戏
                if (TourManager.GetTour(p) != null) {
                    p.sendMessage("当前已经存在游戏！");
                    break;
                }
                TourManager.NewTour(p);
                break;
            case "end":
                // 是否已经存在游戏
                DungeonTour endTour = TourManager.GetTour(p);
                if (endTour == null) {
                    p.sendMessage("当前不存在游戏！");
                    break;
                }
                TourManager.Tours.remove(endTour);
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
