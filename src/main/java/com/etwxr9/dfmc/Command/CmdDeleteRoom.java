package com.etwxr9.dfmc.Command;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.etwxr9.dfmc.Dungeon.DungeonFileManager;
import com.etwxr9.dfmc.Dungeon.DungeonManager;
import com.etwxr9.dfmc.Dungeon.RoomInfo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//根据第二个参数删除当前地牢内指定房间，根据第三个参数删除指定数量的房间，若位置全空则删除房间配置（参数为all则直接删除配置）
public class CmdDeleteRoom implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        if (args.length != 2)
            return false;
        DungeonManager dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            p.sendMessage("无当前地牢");
            return true;
        }
        if (dm.currentDungeon.Rooms.size() == 0) {
            p.sendMessage("当前地牢无房间");
            return true;
        }
        // 根据参数取得相应的ri
        RoomInfo ri = dm.currentRoom;
        if (ri == null) {
            p.sendMessage("指定房间不存在");
            return true;
        }
        // 根据参数取得删除数量
        if (!args[1].equals("all")) {
            int count;
            try {
                count = Integer.parseInt(args[1]);
            } catch (Exception e) {
                return false;
            }
            // 判断是否全空
            if (count < ri.Rooms.size()) {
                var max = ri.Rooms.size() - 1;
                for (int i = 0; i < count; i++) {
                    dm.currentDungeon.EmptyRoomList.add(dm.currentRoom.Rooms.get(max - i));
                    ri.Rooms.remove(max - i);
                    dm.TeleportPlayerToRoom(dm.currentDungeon, dm.currentRoom);
                    p.sendMessage("执行删除操作，自动回到0号房间！");
                }
            } else {// 全空，删除房间设置
                if (dm.currentDungeon.Rooms.size() == 1) {
                    p.sendMessage("最后一个房间，不可删除");
                    return true;
                }
                ri.Rooms.forEach(pos -> dm.currentDungeon.EmptyRoomList.add(pos));
                dm.currentDungeon.Rooms.remove(ri);
                DungeonFileManager.DeleteRoomFile(ri.DungeonId, ri.Id);
                p.sendMessage("该房间已经删除，请使用enterdungeon重新进入一个房间！");
            }
        } else {// all，删除房间设置
            if (dm.currentDungeon.Rooms.size() == 1) {
                p.sendMessage("最后一个房间，不可删除");
                return true;
            }
            ri.Rooms.forEach(pos -> dm.currentDungeon.EmptyRoomList.add(pos));
            dm.currentDungeon.Rooms.remove(ri);
            DungeonFileManager.DeleteRoomFile(ri.DungeonId, ri.Id);
            p.sendMessage("该房间已经删除，请使用enterdungeon重新进入一个房间！");
        }
        // 排序emptyroom
        dm.currentDungeon.EmptyRoomList.sort(new Comparator<int[]>() {

            @Override
            public int compare(int[] o1, int[] o2) {
                for (int i = 2; i > -1; i--) {
                    if (o1[i] != o2[i]) {
                        return Integer.compare(o1[i], o2[i]);
                    }
                }
                return 0;

            }
        });
        DungeonManager.SaveDungeon(dm.currentDungeon);
        p.sendMessage("删除成功");
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2)
            return null;
        Player p = (Player) sender;
        DungeonManager dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            return null;
        }
        if (dm.currentRoom == null) {
            return null;
        }
        RoomInfo ri = dm.currentRoom;
        if (dm.currentDungeon.Rooms.size() == 0) {
            return null;
        }
        if (ri == null) {
            return null;
        }

        return Arrays.asList(Integer.toString(ri.Rooms.size()));
    }

}
