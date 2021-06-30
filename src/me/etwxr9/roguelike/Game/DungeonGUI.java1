package me.etwxr9.roguelike.Game;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class DungeonGUI implements Listener {

    public static List<Inventory> UIList = new ArrayList<Inventory>();

    public static Inventory GetUI(DungeonTour tour) {
        var inv = Bukkit.createInventory(null, 54, "选择下一个房间");
        // 取得每一行RI并添加进去
        int rowCount = tour.DungeonLevel.size() - tour.row;
        // 判断总数是否大于6，循环填入
        rowCount = Integer.min(6, rowCount);
        for (int i = tour.row, currentRow = 5; i < tour.row + rowCount; i++, currentRow--) {
            for (int currentPos = 0; currentPos < 9; currentPos++) {
                ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
                var name = "default";
                switch (tour.DungeonLevel.get(i).get(currentPos).Type) {
                    case "normal":

                        item.setType(Material.STONE_SWORD);
                        name = "normal";
                        break;
                    case "shop":
                        item.setType(Material.EMERALD);
                        name = "shop";
                        break;
                    case "boss":

                        item.setType(Material.ZOMBIE_HEAD);
                        name = "boss";
                        break;
                    default:
                        item.setType(Material.BLACK_STAINED_GLASS_PANE);
                        item.getItemMeta().setDisplayName("defalut");
                        break;
                }
                var newMeta = item.getItemMeta();
                newMeta.setDisplayName(name);
                newMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(newMeta);
                // 判断是否可移动
                if ((currentPos == tour.pos && i == tour.row) || (currentPos == tour.pos && i == tour.row + 1)
                        || (currentPos == tour.pos + 1 && i == tour.row + 1)
                        || (currentPos == tour.pos - 1 && i == tour.row + 1)) {
                    item.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
                }

                inv.setItem(currentRow * 9 + currentPos, item);
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
            // 绑定诅咒代表可前往的房间
            if (clickedItem == null || !clickedItem.containsEnchantment(Enchantment.BINDING_CURSE)
                    || clickedItem.getType() == Material.AIR) {
                p.sendMessage("点击了错误的位置");
                p.closeInventory();
                return;
            }

            var slot = e.getRawSlot();
            int row = 5 - (slot / 9);
            int pos = slot % 9;
            p.sendMessage(MessageFormat.format("点击slot{0}， row{1}， pos{2}", slot, row, pos));
            // 需要判断是否已经处在房间中
            TourManager.EnterRoom(p, row + TourManager.GetTour(p).row, pos);
            p.closeInventory();
        }
    }
}
