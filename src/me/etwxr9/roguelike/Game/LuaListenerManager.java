package me.etwxr9.roguelike.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import java.util.ArrayList;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import me.etwxr9.roguelike.Main;
import me.etwxr9.roguelike.Event.EnterRoomEvent;
import me.etwxr9.roguelike.Event.LeaveRoomEvent;

public class LuaListenerManager implements Listener {
    // Hashmap<事件名,<道具Id>>
    public static Map<String, List<String>> luaFuncMap = Map.ofEntries(
            entry("onEntityDamageByEntity", new ArrayList<String>()), entry("onSpawnEnemy", new ArrayList<String>()),
            entry("onEnterRoom", new ArrayList<String>()), entry("onLeaveRoom", new ArrayList<String>()),
            entry("onEntityDeath", new ArrayList<String>()));

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) {
        Main.getInstance().getServer().broadcastMessage("发生EntityDamageByEntity事件，实体为 " + e.getDamager().getType());
        if (e.getDamager().getType() == EntityType.PLAYER) {
            var p = (Player) e.getDamager();
            var tour = TourManager.GetTour(p);
            if (tour == null) {
                return;
            }
            luaFuncMap.get("onEntityDamageByEntity").forEach((luaName) -> {
                LuaValue itemLua = tour.luaMap.get(luaName);
                if (itemLua == null) {
                    return;
                }
                itemLua.get("onEntityDamageByEntity").call(itemLua, CoerceJavaToLua.coerce(e));
                p.sendMessage("执行lua逻辑 " + luaName);
            });

        }

    }

    @EventHandler
    public void onLeaveRoom(LeaveRoomEvent e) {
        var tour = e.getDungeonTour();
        if (tour == null) {
            return;
        }
        luaFuncMap.get("onLeaveRoom").forEach((luaName) -> {
            LuaValue itemLua = tour.luaMap.get(luaName);
            if (itemLua == null) {
                return;
            }
            itemLua.get("onLeaveRoom").call(itemLua, CoerceJavaToLua.coerce(e));
        });

    }

    public void onEnterRoom(EnterRoomEvent e) {
        var tour = e.getDungeonTour();
        if (tour == null) {
            return;
        }
        luaFuncMap.get("onEnterRoom").forEach((luaName) -> {
            LuaValue itemLua = tour.luaMap.get(luaName);
            if (itemLua == null) {
                return;
            }
            itemLua.get("onEnterRoom").call(itemLua, CoerceJavaToLua.coerce(e));
        });

    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        for (DungeonTour tour : TourManager.Tours.values()) {
            luaFuncMap.get("onEntityDeath").forEach((luaName) -> {
                LuaValue itemLua = tour.luaMap.get(luaName);
                if (itemLua == null) {
                    return;
                }
                itemLua.get("onEntityDeath").call(itemLua, CoerceJavaToLua.coerce(e));
            });
        }

    }

}
