package me.etwxr9.roguelike.Command;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.DungeonUtil.DungeonManager;

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
        var roomOrigin = DungeonManager.GetPoint(dm.currentDungeon, dm.currentPosition, new int[] { 0, 0, 0 });
        var pos = new int[] { loc.getBlockX() - roomOrigin[0], loc.getBlockY() - roomOrigin[1],
                loc.getBlockZ() - roomOrigin[2] };
        switch (args[1]) {
            case "playerPosition":
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
                    switch (args[2]) {
                        case "set":
                            // 判断玩家是否在房间内
                            if ((pos[0] >= dm.currentDungeon.UnitSize[0] || pos[0] < 0)
                                    || (pos[1] >= dm.currentDungeon.UnitSize[1] || pos[1] < 0)
                                    || (pos[2] >= dm.currentDungeon.UnitSize[2] || pos[2] < 0)) {
                                p.sendMessage("当前位置超出房间范围");
                                break;
                            }
                            if (dm.currentRoom.PlayerPosition.equals(pos)) {
                                p.sendMessage("该位置已经存在");
                            } else {
                                dm.currentRoom.PlayerPosition = pos;
                                p.sendMessage(MessageFormat.format("该位置已经设置：{0},{1},{2}", pos[0], pos[1], pos[2]));
                            }
                            break;

                        case "clear":
                            dm.currentRoom.PlayerPosition = new int[] { 0, 0, 0 };
                            p.sendMessage("已将当前房间出生点重置");
                            break;
                    }
                }
                break;
            case "id":// 这里需要做下重复检查
                if (args.length != 3) {
                    return false;
                }
                dm.currentRoom.Id = args[2];
                break;
            case "tags":// 这里需要做下重复检查
                if (args.length != 4) {
                    return false;
                }
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
                    switch (args[2]) {
                        case "set":
                            if (dm.currentRoom.Tags.contains(args[3])) {
                                p.sendMessage("该Tag已经存在");
                            } else {
                                dm.currentRoom.Tags.add(args[3]);
                                p.sendMessage(MessageFormat.format("设置了Tag：{0}", args[3]));
                            }
                            break;

                        case "clear":
                            dm.currentRoom.SpecialPositions.clear();
                            p.sendMessage("已将当前房间所有Tags清除");
                            break;
                        case "unset":
                            if (!dm.currentRoom.Tags.contains(args[3])) {
                                p.sendMessage("该Tag不存在");
                            } else {
                                dm.currentRoom.Tags.remove(args[3]);
                                p.sendMessage(MessageFormat.format("清除了Tag：{0}", args[3]));
                            }
                            break;
                    }
                }
                break;
            case "specialPosition":
                if (args.length < 3) {
                    return false;
                }
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
                    switch (args[2]) {
                        case "set":
                            if (args.length != 4) {
                                p.sendMessage("当前参数数量" + args.length + "请打出该点的名称");
                                return false;
                            }
                            if (args[3].equals("")) {
                                p.sendMessage("args[3] = " + args[3] + "请打出该点的名称");
                                return false;
                            }
                            // 判断玩家是否在房间内
                            if ((pos[0] >= dm.currentDungeon.UnitSize[0] || pos[0] < 0)
                                    || (pos[1] >= dm.currentDungeon.UnitSize[1] || pos[1] < 0)
                                    || (pos[2] >= dm.currentDungeon.UnitSize[2] || pos[2] < 0)) {
                                p.sendMessage("当前位置超出房间范围");
                                break;
                            }
                            if (dm.currentRoom.SpecialPositions.keySet().stream()
                                    .anyMatch(sp -> Arrays.equals(sp, pos))) {
                                p.sendMessage("该位置已经存在");
                            } else {
                                dm.currentRoom.SpecialPositions.put(pos, args[3]);
                                p.sendMessage(MessageFormat.format("该位置已经设置：{0},{1},{2}:{3}", pos[0], pos[1], pos[2],
                                        args[3]));
                            }
                            break;

                        case "clear":
                            dm.currentRoom.SpecialPositions.clear();
                            p.sendMessage("已将当前房间所有特殊点重置");
                            break;
                        case "unset":
                            var a = dm.currentRoom.SpecialPositions.keySet().stream()
                                    .filter(sp -> Arrays.equals(sp, pos)).findFirst();
                            if (a.isPresent()) {
                                dm.currentRoom.SpecialPositions.remove(a.get());
                            }
                            break;
                    }
                }
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
            var configs = Arrays.asList("playerPostion", "tags", "id", "specialPosition");
            return configs;
        } else if (args.length == 3) {
            List<String> items;
            switch (args[1]) {
                case "playerPosition":
                    items = Arrays.asList("set", "unset", "clear");
                    return items;
                case "specialPosition":
                    items = Arrays.asList("set", "unset", "clear");
                    return items;
                case "tags":
                    items = Arrays.asList("set", "unset", "clear");
                    return items;
                default:
                    break;
            }
            if (args.length == 4 && args[1].equals("specialPosition")) {
                items = Arrays.asList("<posId>");
                return items;
            }
        }

        return null;

    }

}
