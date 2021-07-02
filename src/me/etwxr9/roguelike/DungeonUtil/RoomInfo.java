package me.etwxr9.roguelike.DungeonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomInfo {
    public RoomInfo() {
    }

    public RoomInfo(String dungeonId, String id, List<String> tags) {
        DungeonId = dungeonId;
        Id = id;
        Tags = tags;
        PlayerPosition = new int[3];
        SpecialPositions = new HashMap<int[], String>();
        Rooms = new ArrayList<int[]>();

    }

    public String DungeonId;
    public String Id;
    public List<String> Tags;
    public int[] PlayerPosition;
    public Map<int[], String> SpecialPositions;
    public List<int[]> Rooms;
}
