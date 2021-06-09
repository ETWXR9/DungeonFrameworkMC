package me.etwxr9.roguelike.Game;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DungeonGUI implements Listener {

    public static List<Inventory> UIList = new ArrayList<Inventory>();

    public static Inventory GetUI(DungeonTour tour) {
        var inv = Bukkit.createInventory(null, 54, "选择下一个房间");
        // 取得每一行RI并添加进去
        int rowCount = tour.DungeonLevel.size() - tour.row;
        // 判断总数是否大于6，循环填入
        rowCount = Integer.min(6, rowCount);
        for (int i = tour.row, j = 5; i < tour.row + rowCount; i++, j--) {
            for (int z = 0; z < 9; z++) {
                ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
                var name = "default";
                switch (tour.DungeonLevel.get(i).get(z).Type) {
                    case "normal":

                        item.setType(Material.BLACK_STAINED_GLASS_PANE);
                        name = "normal";
                        break;
                    case "shop":
                        item.setType(Material.RED_STAINED_GLASS_PANE);
                        name = "shop";
                        break;
                    case "boss":

                        item.setType(Material.GREEN_STAINED_GLASS_PANE);
                        name = "boss";
                        break;
                    default:
                        item.setType(Material.BLACK_STAINED_GLASS_PANE);
                        item.getItemMeta().setDisplayName("defalut");
                        break;
                }
                var newMeta = item.getItemMeta();
                newMeta.setDisplayName(name);
                item.setItemMeta(newMeta);
                inv.setItem(j * 9 + z, item);
            }
        }
        UIList.add(inv);
        return inv;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (UIList.contains(e.getInventory())) {
            // 用过就扔
            UIList.remove(e.getInventory());

            e.setCancelled(true);
            final Player p = (Player) e.getWhoClicked();
            // 计算点击位置，传送对应房间
            var clickedItem = e.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                p.closeInventory();
                return;
            }

            var slot = e.getRawSlot();
            int row = 5 - (slot / 9);
            int pos = slot % 9;
            p.sendMessage(MessageFormat.format("点击slot{0}， row{1}， pos{2}", slot, row, pos));
            // 需要判断是否已经处在房间中
            TourManager.EnterRoom(p, row, pos);
            p.closeInventory();
        }
    }
}
