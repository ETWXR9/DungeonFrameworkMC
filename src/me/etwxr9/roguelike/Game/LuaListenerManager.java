package me.etwxr9.roguelike.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import java.io.Console;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import me.etwxr9.roguelike.Main;
import me.etwxr9.roguelike.Event.EnterRoomEvent;
import me.etwxr9.roguelike.Event.LeaveRoomEvent;

public class LuaListenerManager implements Listener {
    // Hashmap<事件名,<道具Id>>
    public static Map<String, List<String>> luaFuncMap = new HashMap<String, List<String>>();
    static {
        var funcs = LuaListenerManager.class.getMethods();
        for (Method f : funcs) {
            var name = f.getName();
            if (!name.startsWith("on")) {
                continue;
            }
            Main.getInstance().getServer().getLogger().info("lua事件读取 " + name);
            luaFuncMap.put(name, new ArrayList<String>());
        }
    }

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) {
        // Main.getInstance().getServer().broadcastMessage("发生EntityDamageByEntity事件，实体为
        // " + e.getDamager().getType());
        if (e.getDamager().getType() == EntityType.PLAYER) {
            var p = (Player) e.getDamager();
            var tour = TourManager.GetTour(p);
            if (tour == null) {
                return;
            }
            CallLuaFunc("onEntityDamagedByEntity", tour, e);

        }

    }

    @EventHandler
    public void onLeaveRoom(LeaveRoomEvent e) {
        var tour = e.getDungeonTour();
        if (tour == null) {
            return;
        }
        CallLuaFunc("onLeaveRoom", tour, e);
    }

    @EventHandler
    public void onEnterRoom(EnterRoomEvent e) {
        var tour = e.getDungeonTour();
        if (tour == null) {
            return;
        }
        CallLuaFunc("onEnterRoom", tour, e);

    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        for (DungeonTour tour : TourManager.Tours) {
            CallLuaFunc("onEntityDeath", tour, e);
        }

    }

    // 该事件用于道具触发逻辑：包含：左右键 主手物品 副手物品
    @EventHandler
    public void onPlayerLeftClickAir(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR) {
            var tour = TourManager.GetTour(e.getPlayer());
            if (tour == null) {
                return;
            }
            CallLuaFunc("onPlayerLeftClickAir", tour, e);
        }
    }

    private void CallLuaFunc(String name, DungeonTour tour, Event e) {
        luaFuncMap.get(name).forEach((luaName) -> {
            LuaValue itemLua = tour.luaMap.get(luaName);
            if (itemLua == null) {
                return;
            }
            itemLua.get(name).call(itemLua, CoerceJavaToLua.coerce(e));
        });
    }
}
