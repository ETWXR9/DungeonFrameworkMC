package me.etwxr9.roguelike.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.etwxr9.roguelike.DungeonUtil.DungeonManager;
import me.etwxr9.roguelike.Main;
import me.etwxr9.roguelike.VoidChunkGenerator;
import me.etwxr9.roguelike.DungeonUtil.DungeonFileManager;

public class CmdCreateDungeon implements CommandInterface {

    // /rl createdungeon <id> <dungeon x> <y> <z> <room x> <y> <z> <tags...>
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length < 8)
            return false;

        Player p = (Player) sender;
        // 判断参数格式
        String id;
        var dungeonSize = new int[3];
        var roomSize = new int[3];
        var tags = new ArrayList<String>();
        try {
            id = args[1];
            dungeonSize[0] = Integer.parseInt(args[2]);
            dungeonSize[1] = Integer.parseInt(args[3]);
            dungeonSize[2] = Integer.parseInt(args[4]);
            roomSize[0] = Integer.parseInt(args[5]);
            roomSize[1] = Integer.parseInt(args[6]);
            roomSize[2] = Integer.parseInt(args[7]);
            if (args.length > 8) {
                for (int i = 8; i < roomSize.length; i++) {
                    tags.add(args[i]);
                }
            }
        } catch (Exception e) {
            p.sendMessage("参数格式错误");
            return false;
            // TODO: handle exception
        }
        // 检查size超限
        if (dungeonSize[1] * roomSize[1] > 250) {
            p.sendMessage("最大高度超过250");
            return true;
        }

        // 生成新世界
        // 检查是否存在同名世界
        if (Main.getInstance().getServer().getWorlds().stream().anyMatch(w -> w.getName() == args[1])) {
            p.sendMessage("同名世界已经存在！");
            return true;
        }
        // 生成世界
        WorldCreator wc = new WorldCreator(args[1]);
        wc.generateStructures(false);

        // 这个VoidChunkGenerator重写了generateChunkData方法，只会生成空区块。
        wc.generator(new VoidChunkGenerator());
        World newWorld = wc.createWorld();
        newWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        newWorld.setGameRule(GameRule.DO_MOB_LOOT, false);
        newWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        newWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        newWorld.setGameRule(GameRule.MOB_GRIEFING, false);
        newWorld.setDifficulty(Difficulty.HARD);

        // 创建配置
        try {
            if (DungeonFileManager.CreateDungeonFile(id, tags, new int[] { 0, 0, 0 }, dungeonSize, roomSize)) {
                p.sendMessage("创建成功!");
            } else {
                p.sendMessage("创建失败！文件已存在。");
                return true;
            }
        } catch (Exception e) {
            p.sendMessage("写入文件出错： " + e.getMessage());
            e.printStackTrace();
            return true;
        }

        p.sendMessage("开始读取刚创建的文件！");
        DungeonManager.LoadDungeon(id);

        // 在0,0,0创建一个新房间，创建相应的ROOM配置文件
        // 传送玩家,设定当前房间
        DungeonManager.NewRoom(p, DungeonManager.GetDungeonInfo(id), "default");

        return true;
    }

    // /rl createdungeon <id> <dungeon x> <y> <z> <room x> <y> <z> <tags...>
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // TODO Auto-generated method stub
        switch (args.length) {
            case 0:
                return null;
            case 1:
                return null;
            case 2:
                return Arrays.asList("<id>");
            case 3:
                return Arrays.asList("<dungeon x>");
            case 4:
                return Arrays.asList("<dungeon y>");
            case 5:
                return Arrays.asList("<dungeon z>");
            case 6:
                return Arrays.asList("<room x>");
            case 7:
                return Arrays.asList("<room y>");
            case 8:
                return Arrays.asList("<room z>");
            default:
                return Arrays.asList("<tags...>");
        }
    }

}
