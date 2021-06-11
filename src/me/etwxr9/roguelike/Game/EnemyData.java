package me.etwxr9.roguelike.Game;

import java.util.Map;

public class EnemyData {
    public EnemyData(String id, int level, Map<String, Integer> enemys) {
        Id = id;
        Level = level;
        Enemys = enemys;
    }

    public EnemyData() {
    }

    public String Id;
    public int Level;
    public Map<String, Integer> Enemys;
}
