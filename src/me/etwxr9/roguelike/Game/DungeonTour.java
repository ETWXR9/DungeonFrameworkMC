package me.etwxr9.roguelike.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.DungeonUtil.DungeonInfo;
import me.etwxr9.roguelike.DungeonUtil.RoomInfo;

public class DungeonTour {
    // 一个map储存一行9个房间
    // 一个map储存7行，为一个地牢层
    public Map<Integer, Map<Integer, RoomInfo>> DungeonLevel = new HashMap<Integer, Map<Integer, RoomInfo>>();
    public Player player;

    public DungeonInfo dungeon;
    public RoomInfo room;
    public int[] roomPosition;
    public int row;
    public int pos;
    public List<Entity> EnemyList = new ArrayList<Entity>();
    public boolean isClear;

}
// 一个dungeontour管理类
// 创建tour逻辑
// 需要一个取得所有房间的方法，以及从中筛选特定type/id的房间集合。
// 需要一个将房间加入行的方法，并且管理不同类型房间出现频率。