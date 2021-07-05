package me.etwxr9.roguelike.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LuaGUIManager implements Listener {
    public static Map<Inventory, ArrayList<GUIButton>> invMap = new HashMap<Inventory, ArrayList<GUIButton>>();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        var buttons = invMap.get(e.getInventory());
        if (buttons == null) {
            // e.getWhoClicked().sendMessage("没找到btn");
            return;
        }
        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        e.setCancelled(true);
        GUIButton button = null;
        for (GUIButton btn : buttons) {
            // e.getWhoClicked().sendMessage("比较item");

            if (item.equals(btn.itemStack)) {
                button = btn;
                // e.getWhoClicked().sendMessage("相等");
            }
        }

        if (button == null)
            return;
        button.func.call(CoerceJavaToLua.coerce((Player) e.getWhoClicked()));
        e.getInventory().clear();
        e.getWhoClicked().closeInventory();
        invMap.remove(e.getInventory());
    }

}
