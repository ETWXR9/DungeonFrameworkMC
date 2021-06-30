package me.etwxr9.roguelike.DungeonUtil;

import java.util.ArrayList;
import java.util.List;

import me.etwxr9.roguelike.Main;

public class DungeonInfo {
    public DungeonInfo(String id, List<String> tags, int[] origin, int[] size, int[] unitSize) {
        Id = id;
        Tags = tags;
        Origin = origin;
        Size = size;
        UnitSize = unitSize;
        Units = new ArrayList<RoomInfo>();
        EmptyRoomList = new ArrayList<int[]>();
    }

    public DungeonInfo() {
    }

    public String Id = "";
    public List<String> Tags;
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
