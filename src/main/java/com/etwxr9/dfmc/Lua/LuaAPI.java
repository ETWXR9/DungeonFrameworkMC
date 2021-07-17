package com.etwxr9.dfmc.Lua;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import com.etwxr9.dfmc.Main;
import com.etwxr9.dfmc.Dungeon.DungeonInfo;
import com.etwxr9.dfmc.Dungeon.DungeonManager;
import com.etwxr9.dfmc.Dungeon.RoomInfo;
import com.etwxr9.dfmc.Game.DungeonTour;
import com.etwxr9.dfmc.Game.TourManager;
import com.etwxr9.dfmc.Lua.LuaGUI.GUIButton;
import com.etwxr9.dfmc.Lua.LuaGUI.LuaGUIManager;
import com.etwxr9.dfmc.Utility.ScoreHelper;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LuaAPI {

    /**
     * 加载全局动态lua，返回加载后的LuaTable
     * 
     * @param luaName 要加载的全局动态lua名称
     * @return 已加载的LuaTable
     */
    public static LuaValue GlobalDynamicLuaEnable(String luaName) {
        GlobalLuaManager.getInstance().EnableDynamicLua(luaName);
        return (LuaTable) GlobalLuaManager.getInstance().LoadedGlobalDynamicLuas.get(luaName);
    }

    /**
     * 卸载全局动态lua
     * 
     * @param lua 要卸载的全局动态lua名称
     */
    public static void GlobalDynamicLuaDisable(String lua) {
        GlobalLuaManager.getInstance().DisableDynamicLua(lua);
    }

    /**
     * 加载地牢动态lua，返回加载后的LuaTable
     * 
     * @param tour    DungeonTour
     * @param luaName 要加载的动态lua名称
     * @return 已加载的LuaTable
     */
    public static LuaValue DungeonDynamicLuaEnable(DungeonTour tour, String luaName) {
        TourManager.getInstance().EnableDynamicLua(tour, luaName);
        return (LuaTable) tour.luaMap.get(luaName);
    }

    /**
     * 卸载地牢动态lua
     * 
     * @param tour DungeonTour
     * @param lua  要卸载的地牢动态lua名称
     */
    public static void DungeonDynamicLuaDisable(DungeonTour tour, String lua) {
        TourManager.getInstance().DisableDynamicLua(tour, lua);
    }

    /**
     * 向指定玩家发送消息，同Player.SendMessage
     * 
     * @param p    Player对象
     * @param text
     */
    public static void PlayerSendMessage(Player p, String text) {
        p.sendMessage(text);
    }

    /**
     * 根据玩家对象取得DungeonTour
     * 
     * @param p Player对象
     * @return
     */
    public static DungeonTour GetTour(Player p) {
        return TourManager.getInstance().GetTour(p);
    }

    /**
     * 根据id取得DungeonInfo
     * 
     * @param id
     * @return
     */
    public static DungeonInfo GetDungeon(String id) {
        return DungeonManager.GetDungeonInfo(id);
    }

    /**
     * 创建GUI
     * 
     * @param p     创建UI的玩家
     * @param row   行数（小于6）
     * @param title 标题
     * @return
     */
    public static Inventory GUICreate(Player p, int row, String title) {
        var inv = Bukkit.createInventory(p, row * 9, title);
        LuaGUIManager.getInstance().invMap.put(inv, new HashMap<Integer, GUIButton>());
        return inv;
    }

    /**
     * 向指定GUI添加按钮，按钮在点击后执行回调函数
     * 
     * @param p       玩家
     * @param name    按钮名称
     * @param lore    lore(一个lua字符串组)
     * @param mat     物品材质（不分大小写）
     * @param enchant 是否有附魔光效
     * @param inv     GUI对象
     * @param index   slot位置
     * @param func    回调函数
     * @return
     */
    public static GUIButton GUIAddButton(Player p, String name, LuaValue lore, String mat, boolean enchant,
            Inventory inv, int index, LuaFunction func) {
        var itemStack = new ItemStack(Material.getMaterial(mat.toUpperCase()), 1);
        var meta = itemStack.getItemMeta();

        meta.setDisplayName(name);
        // p.sendMessage("AddButton1");
        var loreList = new ArrayList<String>();
        for (int i = 0; i < lore.length(); i++) {
            loreList.add(lore.get(i + 1).toString());
        }
        meta.setLore(loreList);
        // p.sendMessage("AddButton2");
        if (enchant) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.MENDING, 10, true);
        }

        itemStack.setItemMeta(meta);
        inv.setItem(index, itemStack);
        var btn = new GUIButton(itemStack, func);
        // p.sendMessage("AddButton3");
        LuaGUIManager.getInstance().invMap.get(inv).put(index, btn);

        return btn;
    }

    /**
     * 打开GUI
     * 
     * @param player Player
     * @param inv    GUI
     * @return
     */
    public static Inventory GUIOpen(Player player, Inventory inv) {
        var view = player.openInventory(inv);
        return view.getTopInventory();
    }

    /**
     * 销毁GUI
     * 
     * @param inv
     */
    public static void GUIDestory(Inventory inv) {
        LuaGUIManager.getInstance().DestoryGUI(inv);
    }

    /**
     * 关闭GUI
     * 
     * @param player
     */
    public static void GUIClose(Player player) {
        player.closeInventory();
    }

    /**
     * 进入房间（DungeonTour内的所有玩家全部传送）
     * 
     * @param tour    DungeonTour
     * @param dungeon DungeonInfo的Id
     * @param room    RoomInfo的Id
     * @return 进入房间的RoomInfo对象
     */
    public static RoomInfo TourEnterRoom(DungeonTour tour, String dungeon, String room) {
        var di = DungeonManager.GetDungeonInfo(dungeon);
        if (di == null) {
            tour.player.get(0).sendMessage("地牢" + dungeon + "不存在");
        }
        var ri = di.GetRoom(room);
        if (ri == null) {
            tour.player.get(0).sendMessage("房间" + room + "不存在");
        }

        TourManager.getInstance().EnterRoom(tour, ri);

        return ri;
    }

    /**
     * 取得房间特殊点集（相对坐标）
     * 
     * @param tour
     * @return LuaTabel<LuaTable{x,y,z},PositionName>
     */
    public static LuaTable TourGetSpecialPos(DungeonTour tour) {
        var sp = tour.room.SpecialPositions;
        var a = new LuaTable();
        sp.forEach((k, v) -> {
            var table = new LuaTable();
            for (var i = 0; i < k.length; i++) {
                var num = k[i];
                table.set(i + 1, CoerceJavaToLua.coerce(num));
            }
            a.hashset(table, CoerceJavaToLua.coerce(v));
        });
        return a;

    }

    /**
     * 生成id对应的Entity
     * 
     * @param tour
     * @param x    相对坐标
     * @param y    相对坐标
     * @param z    相对坐标
     * @param id   EntityType对应的Id（不分大小写）
     * @return 生成的Entity
     */
    public static Entity EntitySpawn(DungeonTour tour, double x, double y, double z, String id) {
        double[] pos = new double[] { x, y, z };
        var spawnPos = DungeonManager.GetPoint(tour.dungeon, tour.GetRoomPosition(), pos);
        var world = Main.getInstance().getServer().getWorld(tour.dungeon.Id);
        x = spawnPos[0] + 0.5;
        y = spawnPos[1] + 0.5;
        z = spawnPos[2] + 0.5;
        var loc = new Location(world, x, y, z);
        var entity = world.spawnEntity(loc, EntityType.valueOf(id.toUpperCase()));
        return entity;
    }

    /**
     * 对实体的PersistentDataContainer设置String类型的标签
     * 
     * @param entity  要设置标签的Entity
     * @param key     String类型的标签名
     * @param content String类型的标签值
     */
    public static void EntitySetStringTag(Entity entity, String key, String content) {
        NamespacedKey nKey = new NamespacedKey(Main.getInstance(), key);
        if (entity == null) {
            return;
        }
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (pdc.has(nKey, PersistentDataType.STRING)) {
            var str = pdc.get(nKey, PersistentDataType.STRING);
            if (!str.equals(content)) {
                pdc.set(nKey, PersistentDataType.STRING, content);
            }
        } else {
            pdc.set(nKey, PersistentDataType.STRING, content);
        }
    }

    /**
     * 对实体的PersistentDataContainer取得String类型的标签
     * 
     * @param entity 要取得标签的Entity
     * @param key    String类型的标签名
     * @return String类型的标签值
     */
    public static String EntityGetStringTag(Entity entity, String key) {
        NamespacedKey nKey = new NamespacedKey(Main.getInstance(), key);
        if (entity == null) {
            return null;
        }

        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (pdc.has(nKey, PersistentDataType.STRING)) {
            return pdc.get(nKey, PersistentDataType.STRING);
        } else {
            return null;
        }
    }

    /**
     * 创建一个ItemStack
     * 
     * @param mat    Material名称，不分大小写
     * @param amount 数量
     * @return ItemStakc
     */
    public static ItemStack ItemNew(String mat, int amount) {
        ItemStack is = new ItemStack(Material.valueOf(mat.toUpperCase()), amount);
        return is;
    }

    /**
     * 设置物品的Lore
     * 
     * @param item ItemStack
     * @param lore lua字符串数组
     * @return
     */
    public static ItemStack ItemSetLore(ItemStack item, LuaTable lore) {
        var meta = item.getItemMeta();
        var loreList = new ArrayList<String>();
        for (int i = 0; i < lore.length(); i++) {
            loreList.add(lore.get(i + 1).toString());
        }
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * 给予玩家ItemStack
     * 
     * @param p    Player
     * @param item ItemStack
     */
    public static void PlayerGiveItem(Player p, ItemStack item) {
        p.getInventory().addItem(item);
    }

    /**
     * 对物品的PersistentDataContainer设置String类型的标签
     * 
     * @param item    要取得标签的ItemStack
     * @param key     String类型的标签名
     * @param content String类型的标签值
     */
    public static void ItemSetStringTag(ItemStack item, String key, String content) {
        NamespacedKey nKey = new NamespacedKey(Main.getInstance(), key);
        if (item == null) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        if (pdc.has(nKey, PersistentDataType.STRING)) {
            var str = pdc.get(nKey, PersistentDataType.STRING);
            if (!str.equals(content)) {
                pdc.set(nKey, PersistentDataType.STRING, content);
            }
        } else {
            pdc.set(nKey, PersistentDataType.STRING, content);
        }
        item.setItemMeta(itemMeta);
    }

    /**
     * 对物品的PersistentDataContainer取得String类型的标签
     * 
     * @param item 要取得标签的ItemStack
     * @param key  String类型的标签名
     * @return String类型的标签值
     */
    public static String ItemGetStringTag(ItemStack item, String key) {
        NamespacedKey nKey = new NamespacedKey(Main.getInstance(), key);
        if (item == null) {
            return null;
        }
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        if (pdc.has(nKey, PersistentDataType.STRING)) {
            return pdc.get(nKey, PersistentDataType.STRING);
        } else {
            return null;
        }
    }

    /**
     * 为物品添加附魔
     * 
     * @param item    ItemStack
     * @param enchant Enchantment对应的String，不分大小写
     * @param level   int等级
     * @return
     */
    public static ItemStack ItemAddEnchantment(ItemStack item, String enchant, int level) {
        item.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchant.toLowerCase())), level);
        return item;
    }

    /**
     * 为物品设置名称
     * 
     * @param item
     * @param name
     * @return
     */
    public static ItemStack ItemSetName(ItemStack item, String name) {
        var meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * 取得药水的PotionMeta
     * 
     * @param item
     * @return PotionMeta
     */
    public static PotionMeta ItemGetPotionMeta(ItemStack item) {
        if (item == null) {
            return null;
        }
        if (item.getType() != Material.POTION) {
            return null;
        }
        return (PotionMeta) item.getItemMeta();
    }

    /**
     * 结束DungeonTour，调用所有lua脚本的close并注销事件。为防止在事件中调用产生错误，会在下一帧执行。
     * 
     * @param tour
     */
    public static void TourEnd(DungeonTour tour) {
        TourManager.getInstance().EndTour(tour);
    }

    /**
     * 全局永久lua注册事件
     * 
     * @param name Event类名
     * @param f    回调的LuaFunction
     */
    public static void EventGlobalRegister(String name, LuaFunction f) {
        try {
            GlobalLuaListenerManager.getInstance().RegisterEvent(name, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 全局动态lua注册事件
     * 
     * @param name       Event类名
     * @param dynamicLua 动态Lua名称
     * @param f          LuaFunction回调
     * 
     */
    public static void EventDynamicRegister(String name, String dynamicLua, LuaFunction f) {
        try {
            GlobalLuaListenerManager.getInstance().RegisterDynamicEvent(name, dynamicLua, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 全局永久Lua注销事件
     * 
     * @param name 事件名
     * @param f    注册的LuaFunction回调
     */
    public static void EventGlobalUnRegister(String name, LuaFunction f) {
        try {
            GlobalLuaListenerManager.getInstance().RegisterEvent(name, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 全局动态Lua注销事件
     * 
     * @param name
     * @param dynamicLua
     * @param f
     */
    public static void EventGlobalDynamicUnregister(String name, String dynamicLua, LuaFunction f) {
        try {
            GlobalLuaListenerManager.getInstance().UnRegisterDynamicEvent(name, dynamicLua, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 地牢永久lua注册事件，会在DungeonTour结束时自动注销
     * 
     * @param name Event类名
     * @param tour DungeonTour
     * @param f    回调的LuaFunction
     */
    public static void EventRegister(String name, DungeonTour tour, LuaFunction f) {
        try {
            DungeonLuaListenerManager.getInstance().RegisterEvent(name, tour, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 地牢动态lua注册事件
     * 
     * @param name       Event类名
     * @param tour       DungeonTour
     * @param f          LuaFunction回调
     * @param dynamicLua 动态Lua名称
     */
    public static void EventDynamicRegister(String name, DungeonTour tour, String dynamicLua, LuaFunction f) {
        try {
            DungeonLuaListenerManager.getInstance().RegisterDynamicEvent(name, tour, dynamicLua, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 地牢永久Lua注销事件
     * 
     * @param name 事件名
     * @param tour DungeonTour
     * @param f    注册的LuaFunction回调
     */
    public static void EventUnRegister(String name, DungeonTour tour, LuaFunction f) {
        try {
            DungeonLuaListenerManager.getInstance().RegisterEvent(name, tour, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 地牢动态Lua注销事件
     * 
     * @param name
     * @param tour
     * @param dynamicLua
     * @param f
     */
    public static void EventDynamicUnRegister(String name, DungeonTour tour, String dynamicLua, LuaFunction f) {
        try {
            DungeonLuaListenerManager.getInstance().UnRegisterDynamicEvent(name, tour, dynamicLua, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建一个Runnable
     * 
     * @param func Runnable所执行的LuaFunction
     * @return 创建的Runnable
     */
    public static LuaRunnable RunnableCreate(LuaFunction func) {
        return new LuaRunnable(JavaPlugin.getPlugin(Main.class), func);
    }

    /**
     * 对特定玩家创建一个计分板
     * 
     * @param player Player
     * @return 一个ScoreHelper对象
     */
    public static ScoreHelper ScoreBoardCreate(Player player) {
        return ScoreHelper.createScore(player);
    }

    /**
     * 取得玩家的ScoreHelper对象
     * 
     * @param player
     * @return ScoreHelper
     */
    public static ScoreHelper ScoreBoardGet(Player player) {
        return ScoreHelper.getByPlayer(player);
    }

    /**
     * 移除玩家的ScoreHelper对象
     * 
     * @param player
     */
    public static void ScoreBoardRemove(Player player) {
        ScoreHelper.removeScore(player);
    }

    /**
     * 取得一个Particle枚举
     * 
     * @param type Particle对应的字符串（不分大小写）
     * @return Particle枚举
     */
    public static Particle ParticleGetType(String type) {
        return Particle.valueOf(type.toUpperCase());
    }

    /**
     * 生成粒子
     * 
     * @param player   Player对象，用于确定所在世界
     * @param particle Particle对应的字符串（不分大小写）
     * @param loc      double[3] 位置
     * @param count    粒子数量
     */
    public static void ParticleSpawn(Player player, String particle, double[] loc, int count) {
        var particleType = ParticleGetType(particle);
        var location = new Location(player.getWorld(), loc[0], loc[1], loc[2]);
        player.getWorld().spawnParticle(particleType, location, count);
    }

    /**
     * 生成粒子
     * 
     * @param player   Player对象，用于确定所在世界
     * @param particle Particle对应的字符串（不分大小写）
     * @param loc      double[3] 位置
     * @param count    粒子数量
     * @param ox       offsetX
     * @param oy       offsetY
     * @param oz       offsetZ
     * @param extra
     */
    public static void ParticleSpawn(Player player, String particle, double[] loc, int count, double ox, double oy,
            double oz, double extra) {
        var particleType = ParticleGetType(particle);
        var location = new Location(player.getWorld(), loc[0], loc[1], loc[2]);
        player.getWorld().spawnParticle(particleType, location, count, ox, oy, oz, extra);
    }

    /**
     * 生成粒子
     * 
     * @param player   Player对象，用于确定所在世界
     * @param particle Particle对应的字符串（不分大小写）
     * @param loc      double[3] 位置
     * @param count    粒子数量
     * @param ox       offsetX
     * @param oy       offsetY
     * @param oz       offsetZ
     * @param extra
     * @param force
     * @param r
     * @param g
     * @param b
     * @param size
     */
    public static void ParticleSpawn(Player player, String particle, double[] loc, int count, double ox, double oy,
            double oz, double extra, boolean force, int r, int g, int b, float size) {
        var particleType = ParticleGetType(particle);
        var location = new Location(player.getWorld(), loc[0], loc[1], loc[2]);
        DustOptions dustOptions = new DustOptions(Color.fromRGB(r, g, b), size);
        player.getWorld().spawnParticle(particleType, location, count, ox, oy, oz, extra, dustOptions, force);
    }

    /**
     * 创建DustOptions粒子设置
     * 
     * @param r
     * @param g
     * @param b
     * @param size
     * @return
     */
    public static DustOptions ParticleDustOptions(int r, int g, int b, float size) {
        return new DustOptions(Color.fromRGB(r, g, b), size);
    }

    /**
     * 根据相对坐标取得绝对坐标，返回double数组
     * 
     * @param tour DungeonTour
     * @param x    doubleX
     * @param y    doubleY
     * @param z    doubleZ
     * @return double[3]
     */
    public static double[] TourGetPosition(DungeonTour tour, double x, double y, double z) {
        return DungeonManager.GetPoint(tour.dungeon, tour.GetRoomPosition(), new double[] { x, y, z });
    }

    /**
     * 根据相对坐标取得绝对坐标，返回装有坐标的LuaTable
     * 
     * @param tour DungeonTour
     * @param x    doubleX
     * @param y    doubleY
     * @param z    doubleZ
     * @return LuaTable{x,y,z}
     */
    public static LuaTable TourGetPositionTable(DungeonTour tour, double x, double y, double z) {
        var table = new LuaTable();
        var pos = DungeonManager.GetPoint(tour.dungeon, tour.GetRoomPosition(), new double[] { x, y, z });
        for (int i = 0; i < pos.length; i++) {
            table.set(i + 1, CoerceJavaToLua.coerce(pos[i]));
        }
        return table;
    }

    /**
     * 将数组转换为LuaTable，方便操作
     * 
     * @param array java数组
     * @return LuaTable
     */
    public static LuaTable ConvertArrayToTable(Object[] array) {
        if (array == null) {
            return null;
        }
        var table = new LuaTable();
        for (int i = 0; i < array.length; i++) {
            table.set(i + 1, CoerceJavaToLua.coerce(array[i]));
        }
        return table;
    }

    /**
     * 将luatable转换为java数组，用于为java函数传递可变参数
     * 
     * @param table
     * @param toClass 要转换的类，一般使用getClass()取得
     * @return Object[] 如果table的内容不一致，将返回null
     */
    public static Object[] ConvertTableToArray(LuaTable table, Class<? extends Event> toClass) {
        var array = Array.newInstance(toClass, table.length());
        for (int i = 0; i < table.length(); i++) {
            try {
                var v = table.get(i + 1).touserdata(toClass);
                Array.set(array, i, v);
            } catch (ClassCastException e) {
                e.printStackTrace();
                return null;
            }
        }
        return (Object[]) array;
    }

    /**
     * 取得org.bukkit.Sound枚举
     * 
     * @param name
     * @return
     */
    public static Sound GetSound(String name) {
        return Sound.valueOf(name.toUpperCase());
    }

    /**
     * 取得Effect枚举
     * 
     * @param name
     * @return
     */
    public static Effect GetEffect(String name) {
        return Effect.valueOf(name.toUpperCase());
    }

    /**
     * 取得Color枚举
     * 
     * @param r
     * @param g
     * @param b
     * @return
     */
    public static Color GetColor(int r, int g, int b) {
        return Color.fromRGB(r, g, b);
    }

    /**
     * 取得Material枚举
     * 
     * @param mat
     * @return
     */
    public static Material GetMaterial(String mat) {
        return Material.valueOf(mat.toUpperCase());
    }

}
