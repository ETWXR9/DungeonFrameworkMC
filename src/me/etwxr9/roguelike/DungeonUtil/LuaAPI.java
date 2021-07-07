package me.etwxr9.roguelike.DungeonUtil;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    // 加载动态lua，返回加载后的luaTable
    public static LuaValue LuaEnable(DungeonTour tour, String lua) {
        TourManager.EnableDynamicLua(tour, lua);
        return (LuaTable) tour.luaMap.get(lua);
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

    public static Inventory GUICreate(Player p, int row) {
        var inv = Bukkit.createInventory(p, row * 9);
        LuaGUIManager.invMap.put(inv, new ArrayList<GUIButton>());
        return inv;
    }

    // 向指定inv添加按钮，参数为：玩家，物品名称，lore（一个lua字符串数组），物品材质（不分大小写），是否有附魔特效，GUI容器，slot位置，点击回调
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
        LuaGUIManager.invMap.get(inv).add(btn);

        return btn;
    }

    public static InventoryView GUIOpen(Inventory inv) {
        return ((Player) inv.getHolder()).openInventory(inv);
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

    public static ItemStack ItemNew(String mat, int amount) {
        ItemStack is = new ItemStack(Material.valueOf(mat.toUpperCase()), amount);
        return is;
    }

    public static void PlayerGiveItem(Player p, ItemStack item) {
        p.getInventory().addItem(item);
    }

    public static void ItemSetStringTag(ItemStack item, String key, String content) {
        NamespacedKey nKey = new NamespacedKey(Main.getInstance(), key);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        if (pdc.has(nKey, PersistentDataType.STRING)) {
            var str = pdc.get(nKey, PersistentDataType.STRING);
            if (!str.equals(key)) {
                pdc.set(nKey, PersistentDataType.STRING, key);
            }
        } else {
            pdc.set(nKey, PersistentDataType.STRING, content);
        }
        item.setItemMeta(itemMeta);
    }

    public static String ItemGetStringTag(ItemStack item, String key) {
        NamespacedKey nKey = new NamespacedKey(Main.getInstance(), key);
        ItemMeta itemMeta = item.getItemMeta();
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
            double oz, double extra, boolean force) {
        var particleType = ParticleGetType(particle);
        var location = new Location(player.getWorld(), loc[0], loc[1], loc[2]);
        player.getWorld().spawnParticle(particleType, location, count, ox, oy, oz, extra, force);
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

    // 将数组转换为表
    public static LuaTable ConvertArray(Object[] array) {
        var table = new LuaTable();
        for (int i = 0; i < array.length; i++) {
            table.add(CoerceJavaToLua.coerce(array[i]));
        }
        return table;
    }

}
