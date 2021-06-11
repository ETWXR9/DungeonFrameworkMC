package me.etwxr9.roguelike.Game;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import me.etwxr9.roguelike.Main;
import me.etwxr9.roguelike.DungeonUtil.EnemyJsonParser;

public class EnemyManager {
    public static Map<String, EnemyData> Enemys = new HashMap<String, EnemyData>();

    public static void LoadEnemyData() {
        File dir = new File(Main.getInstance().getDataFolder().getAbsolutePath().toString() + "/EnemyData/");
        var jsons = Arrays.asList(dir.list()).stream().filter(s -> s.endsWith(".json"))
                .map(s -> s = s.substring(0, s.length() - 5)).collect(Collectors.toList());
        jsons.forEach(file -> {
            var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/EnemyData/" + file + ".json");
            if (Files.exists(path)) {
                try {
                    var data = Files.readString(path);
                    EnemyData enemy = EnemyJsonParser.LoadEnemyData(data);
                    Enemys.put(enemy.Id, enemy);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });
    }
}
