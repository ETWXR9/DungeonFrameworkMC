package com.etwxr9.dfmc.Dungeon;

import java.util.ArrayList;
import java.util.List;

public class DungeonInfo {
    public DungeonInfo(String id, List<String> tags, int[] origin, int[] size, int[] unitSize) {
        Id = id;
        Tags = tags;
        Origin = origin;
        Size = size;
        RoomSize = unitSize;
        Rooms = new ArrayList<RoomInfo>();
        EmptyRoomList = new ArrayList<int[]>();
    }

    public DungeonInfo() {
    }

    public String Id = "";
    public List<String> Tags;
    public int[] Origin = new int[3];
    public int[] Size = new int[3];
    public int[] RoomSize = new int[3];
    public List<RoomInfo> Rooms = new ArrayList<RoomInfo>();
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
        for (RoomInfo r : Rooms) {
            if (r.Id.equals(id)) {
                return r;
            }
        }
        return null;
    }

}
