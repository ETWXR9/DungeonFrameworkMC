package me.etwxr9.roguelike.DungeonUtil;

import com.alibaba.fastjson.JSON;

import me.etwxr9.roguelike.Game.EnemyData;

public class EnemyJsonParser {
    public static EnemyData LoadEnemyData(String jsonData) {
        return JSON.parseObject(jsonData, EnemyData.class);
    }

    public static String Parsejson(EnemyData obj) {
        return JSON.toJSONString(obj);
    }
}
