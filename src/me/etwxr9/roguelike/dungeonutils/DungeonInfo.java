package me.etwxr9.roguelike.dungeonutils;

import java.util.List;

public class DungeonInfo {
    public String World = "";
    public int[] Origin = new int[3];
    public int[] Size = new int[3];
    public int[] UnitSize = new int[3];
    public List<RoomInfo> Units;
}
