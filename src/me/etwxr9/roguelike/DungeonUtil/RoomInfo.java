package me.etwxr9.roguelike.DungeonUtil;

import java.util.ArrayList;
import java.util.List;

public class RoomInfo {
    public String Id;
    public String Type;
    public String Clear;
    public int[] PlayerPosition;
    public List<int[]> EnemyPosition;
    public List<int[]> Rooms;

    public RoomInfo() {
        EnemyPosition = new ArrayList<int[]>();
        Rooms = new ArrayList<int[]>();
    }
}
