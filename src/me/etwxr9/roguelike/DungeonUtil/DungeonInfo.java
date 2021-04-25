package me.etwxr9.Roguelike.DungeonUtil;

import java.util.ArrayList;
import java.util.List;

import me.etwxr9.Roguelike.Main;

public class DungeonInfo {
    public String World = "";
    public int[] Origin = new int[3];
    public int[] Size = new int[3];
    public int[] UnitSize = new int[3];
    public List<RoomInfo> Units = new ArrayList<RoomInfo>();
    public List<int[]> EmptyRoomList = new ArrayList<int[]>();

    // 构造函数填充EmptyRoomList
    public void initEmptyRoomList() {
        for (int z = 0; z < Size[2]; z++) {
            for (int y = 0; y < Size[1]; y++) {
                for (int x = 0; x < Size[0]; x++) {
                    var pos = new int[] { x, y, z };
                    EmptyRoomList.add(pos);
                }
            }
        }
        Main.getInstance().getServer().broadcastMessage("dungeoninfo构造完毕， " + EmptyRoomList.size());
    }

    // 返回指定ID的房间
    public RoomInfo GetRoom(String id) {
        for (RoomInfo r : Units) {
            if (r.Id.equals(id)) {
                return r;
            }
        }
        return null;
    }
}
