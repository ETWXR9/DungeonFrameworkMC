package me.etwxr9.roguelike.dungeonutils;

import java.util.ArrayList;
import java.util.List;

public class DungeonInfo {
    public String World = "";
    public int[] Origin = new int[3];
    public int[] Size = new int[3];
    public int[] UnitSize = new int[3];
    public List<RoomInfo> Units = new ArrayList<RoomInfo>();

    // 返回指定ID的房间
    public RoomInfo GetRoom(String id) {
        RoomInfo ri = new RoomInfo();
        for (RoomInfo r : Units) {
            ri = r.Id.equals(id) ? r : null;
        }
        return ri;
    }
}
