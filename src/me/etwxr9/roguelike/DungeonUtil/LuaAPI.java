package me.etwxr9.roguelike.DungeonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.parser.LuaParser;

import me.etwxr9.roguelike.Main;
import me.etwxr9.roguelike.Game.DungeonTour;
import me.etwxr9.roguelike.Game.GUIButton;
import me.etwxr9.roguelike.Game.LuaGUIManager;
import me.etwxr9.roguelike.Game.TourManager;

public class LuaAPI {
    public static void SendMessage(Player p, String text) {
        p.sendMessage(text);

    }

    public static DungeonTour GetTour(Player p) {
        return TourManager.GetTour(p);
    }

    public static DungeonInfo GetDungeon(String id) {
        return DungeonManager.GetDungeonInfo(id);
    }

    public static void EndTour(Player p) {
        // 是否已经存在游戏
        DungeonTour endTour = TourManager.GetTour(p);
        if (endTour == null) {
            // p.sendMessage("当前不存在游戏！");
            return;
        }
        TourManager.Tours.remove(endTour);
    }

    public static Inventory CreateGUI(Player p, int row) {
        var inv = Bukkit.createInventory(p, row * 9);
        LuaGUIManager.invMap.put(inv, new ArrayList<GUIButton>());
        return inv;
    }

    public static GUIButton AddButton(Player p, String name, LuaValue lore, String mat, boolean enchant, Inventory inv,
            int index, LuaFunction func, LuaValue self) {
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
        var btn = new GUIButton(itemStack, self, func);
        // p.sendMessage("AddButton3");
        LuaGUIManager.invMap.get(inv).add(btn);

        return btn;
    }

    public static InventoryView OpenGUI(Inventory inv) {
        return ((Player) inv.getHolder()).openInventory(inv);
    }

    public static RoomInfo EnterRoom(DungeonTour tour, String dungeon, String room) {
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

    public static LuaTable GetSpecialPos(DungeonTour tour) {
        var sp = tour.room.SpecialPositions;
        var a = new LuaTable();
        sp.forEach((k, v) -> {
            a.hashset(CoerceJavaToLua.coerce(k), CoerceJavaToLua.coerce(v));
        });
        return a;

    }

    public static Entity SpawnEntity(DungeonTour tour, int[] pos, String id) {
        var spawnPos = DungeonManager.GetPoint(tour.dungeon, tour.GetRoomPosition(), pos);
        var world = Main.getInstance().getServer().getWorld(tour.dungeon.Id);
        double x, y, z;
        x = spawnPos[0] + 0.5;
        y = spawnPos[1] + 0.5;
        z = spawnPos[2] + 0.5;
        var loc = new Location(world, x, y, z);
        var entity = world.spawnEntity(loc, EntityType.valueOf(id.toUpperCase()));
        return entity;
    }

    public static ItemStack NewItemStack(String mat, int amount) {
        ItemStack is = new ItemStack(Material.valueOf(mat.toUpperCase()), amount);
        return is;
    }

    public static void GivePlayerItem(Player p, ItemStack item) {
        p.getInventory().addItem(item);
    }

    public static void SetItemStringTag(ItemStack item, String key, String content) {
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

    public static String GetItemStringTag(ItemStack item, String key) {
        NamespacedKey nKey = new NamespacedKey(Main.getInstance(), key);
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        if (pdc.has(nKey, PersistentDataType.STRING)) {
            return pdc.get(nKey, PersistentDataType.STRING);
        } else {
            return null;
        }
    }

    public static ItemStack AddItemEnchantment(ItemStack item, String enchant, int level) {
        item.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchant.toLowerCase())), level);
        return item;
    }

    public static void EndTour(DungeonTour tour) {
        // 是否已经存在游戏
        if (tour == null) {
            return;
        }
        TourManager.Tours.remove(tour);
    }

}
