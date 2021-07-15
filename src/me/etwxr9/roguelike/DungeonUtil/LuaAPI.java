package me.etwxr9.roguelike.DungeonUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

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
import org.bukkit.inventory.InventoryView;
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

import me.etwxr9.roguelike.Main;
import me.etwxr9.roguelike.Game.DungeonTour;
import me.etwxr9.roguelike.Game.GUIButton;
import me.etwxr9.roguelike.Game.LuaGUIManager;
import me.etwxr9.roguelike.Game.LuaListenerManager;
import me.etwxr9.roguelike.Game.LuaRunnable;
import me.etwxr9.roguelike.Game.ScoreHelper;
import me.etwxr9.roguelike.Game.TourManager;

public class LuaAPI {

    /**
     * 加载动态lua，返回加载后的LuaTable
     * 
     * @param tour    DungeonTour
     * @param luaName 要加载的lua名称
     * @return 已加载的LuaTable
     */
    public static LuaValue LuaEnable(DungeonTour tour, String luaName) {
        TourManager.EnableDynamicLua(tour, luaName);
        return (LuaTable) tour.luaMap.get(luaName);
    }

    // 卸载动态lua
    public static void LuaDisable(DungeonTour tour, String lua) {
        TourManager.DisableDynamicLua(tour, lua);
    }

    public static void PlayerSendMessage(Player p, String text) {
        p.sendMessage(text);
    }

    public static DungeonTour GetTour(Player p) {
        return TourManager.GetTour(p);
    }

    public static DungeonInfo GetDungeon(String id) {
        return DungeonManager.GetDungeonInfo(id);
    }

    public static Inventory GUICreate(Player p, int row, String title) {
        var inv = Bukkit.createInventory(p, row * 9, title);
        LuaGUIManager.invMap.put(inv, new HashMap<Integer, GUIButton>());
        return inv;
    }

    /**
     * 向指定inv添加按钮，按钮在点击后取消点击事件，执行回调函数
     * 
     * @param p       玩家
     * @param name    物品名称
     * @param lore    lore(一个lua字符串组)
     * @param mat     物品材质（不分大小写）
     * @param enchant 是否有附魔光效
     * @param inv     GUI容器
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
        LuaGUIManager.invMap.get(inv).put(index, btn);

        return btn;
    }

    public static Inventory GUIOpen(Player player, Inventory inv) {
        var view = player.openInventory(inv);
        return view.getTopInventory();
    }

    public static void GUIDestory(Inventory inv) {
        LuaGUIManager.DestoryGUI(inv);
    }

    public static void GUIClose(Player player) {
        player.closeInventory();
    }

    public static RoomInfo TourEnterRoom(DungeonTour tour, String dungeon, String room) {
        var di = DungeonManager.GetDungeonInfo(dungeon);
        if (di == null) {
            tour.player.get(0).sendMessage("地牢" + dungeon + "不存在");
        }
        var ri = di.GetRoom(room);
        if (ri == null) {
            tour.player.get(0).sendMessage("房间" + room + "不存在");
        }

        TourManager.EnterRoom(tour, ri);

        return ri;
    }

    /**
     * 取得房间特殊点集<luatable{x,y,z},name>
     * 
     * @param tour
     * @return
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

    // 生成id对应的entityType
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

    public static ItemStack ItemNew(String mat, int amount) {
        ItemStack is = new ItemStack(Material.valueOf(mat.toUpperCase()), amount);
        return is;
    }

    // 该lore参数为lua字符串数组
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

    public static void PlayerGiveItem(Player p, ItemStack item) {
        p.getInventory().addItem(item);
    }

    // 设定物品PersistentDataHolder的给定key的string类型的值
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
     * 取得PersistentDataContainer中的字符串数据，如果item为null则返回null
     * 
     * @param item
     * @param key
     * @return
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

    public static ItemStack ItemAddEnchantment(ItemStack item, String enchant, int level) {
        item.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchant.toLowerCase())), level);
        return item;
    }

    public static ItemStack ItemSetName(ItemStack item, String name) {
        var meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

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
     * 为防止在事件中调用产生错误，会在下一帧执行。
     * 
     * @param tour
     */
    public static void TourEnd(DungeonTour tour) {
        TourManager.EndTour(tour);
    }

    // 用于全局lua注册事件，参数为：事件名,tour,回调的luaFunction
    public static void EventRegister(String name, DungeonTour tour, LuaFunction f) {
        try {
            LuaListenerManager.RegisterEvent(name, tour, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 用于动态lua注册事件，参数为：事件名,tour,动态lua名,回调的luaFunction
    public static void EventRegister(String name, DungeonTour tour, String dynamicLua, LuaFunction f) {
        try {
            LuaListenerManager.RegisterEvent(name, tour, dynamicLua, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void EventUnRegister(String name, DungeonTour tour, LuaFunction f) {
        try {
            LuaListenerManager.RegisterEvent(name, tour, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void EventUnRegister(String name, DungeonTour tour, String dynamicLua, LuaFunction f) {
        try {
            LuaListenerManager.UnRegisterEvent(name, tour, dynamicLua, f);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static LuaRunnable RunnableCreate(LuaFunction func) {
        return new LuaRunnable(JavaPlugin.getPlugin(Main.class), func);
    }

    public static ScoreHelper ScoreBoardCreate(Player player) {
        return ScoreHelper.createScore(player);
    }

    public static ScoreHelper ScoreBoardGet(Player player) {
        return ScoreHelper.getByPlayer(player);
    }

    public static void ScoreBoardRemove(Player player) {
        ScoreHelper.removeScore(player);
    }

    public static Particle ParticleGetType(String type) {
        return Particle.valueOf(type.toUpperCase());
    }

    public static void ParticleSpawn(Player player, String particle, double[] loc, int count) {
        var particleType = ParticleGetType(particle);
        var location = new Location(player.getWorld(), loc[0], loc[1], loc[2]);
        player.getWorld().spawnParticle(particleType, location, count);
    }

    public static void ParticleSpawn(Player player, String particle, double[] loc, int count, double ox, double oy,
            double oz, double extra) {
        var particleType = ParticleGetType(particle);
        var location = new Location(player.getWorld(), loc[0], loc[1], loc[2]);
        player.getWorld().spawnParticle(particleType, location, count, ox, oy, oz, extra);
    }

    public static void ParticleSpawn(Player player, String particle, double[] loc, int count, double ox, double oy,
            double oz, double extra, boolean force, int r, int g, int b, float size) {
        var particleType = ParticleGetType(particle);
        var location = new Location(player.getWorld(), loc[0], loc[1], loc[2]);
        DustOptions dustOptions = new DustOptions(Color.fromRGB(r, g, b), size);
        player.getWorld().spawnParticle(particleType, location, count, ox, oy, oz, extra, dustOptions, force);
    }

    public static DustOptions ParticleDustOptions(int r, int g, int b, float size) {
        return new DustOptions(Color.fromRGB(r, g, b), size);
    }

    public static Color ParticleColor(int r, int g, int b) {
        return Color.fromRGB(r, g, b);
    }

    public static double[] TourGetPosition(DungeonTour tour, double x, double y, double z) {
        return DungeonManager.GetPoint(tour.dungeon, tour.GetRoomPosition(), new double[] { x, y, z });
    }

    public static LuaTable TourGetPositionTable(DungeonTour tour, double x, double y, double z) {
        var table = new LuaTable();
        var pos = DungeonManager.GetPoint(tour.dungeon, tour.GetRoomPosition(), new double[] { x, y, z });
        for (int i = 0; i < pos.length; i++) {
            table.set(i + 1, CoerceJavaToLua.coerce(pos[i]));
        }
        return table;
    }

    // 将数组转换为表
    public static LuaTable ConvertArrayToTable(Object[] array) {
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

    public static Effect GetEffect(String name) {
        return Effect.valueOf(name.toUpperCase());
    }

    public static Color GetColor(int r, int g, int b) {
        return Color.fromRGB(r, g, b);
    }

    public static Material GetMaterial(String mat) {
        return Material.valueOf(mat.toUpperCase());
    }

    public static String GetClassName(Class<? extends Event> c) {
        return c.getCanonicalName();
    }

}
