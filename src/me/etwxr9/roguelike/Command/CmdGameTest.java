package me.etwxr9.roguelike.Command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.DungeonUtil.DungeonManager;
import me.etwxr9.roguelike.Game.DungeonGUI;
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
                var newtour = TourManager.NewTour(p, DungeonManager.GetDIList().get(0).World);
                p.openInventory(DungeonGUI.GetUI(newtour));
                break;
            case "end":
                // 是否已经存在游戏
                if (TourManager.GetTour(p) == null) {
                    p.sendMessage("当前不存在游戏！");
                    break;
                }
                TourManager.Tours.remove(p);
                p.sendMessage("当前游戏结束！");
                break;
            case "clear":
                if (TourManager.GetTour(p) == null) {
                    p.sendMessage("无当前关卡");
                    return true;
                }
                TourManager.GetTour(p).isClear = true;
                p.sendMessage("关卡已clear");
                break;
            case "open":
                if (TourManager.GetTour(p) == null) {
                    p.sendMessage("无当前关卡");
                    return true;
                }
                var tour = TourManager.GetTour(p);

                if (tour == null) {
                    return true;
                }
                if (!tour.isClear) {
                    p.sendMessage("房间未CLEAR!");
                    return true;
                }
                p.openInventory(DungeonGUI.GetUI(TourManager.GetTour(p)));
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        if (args.length != 2) {
            return null;
        }
        return Arrays.asList("start", "open", "clear");

    }

}
