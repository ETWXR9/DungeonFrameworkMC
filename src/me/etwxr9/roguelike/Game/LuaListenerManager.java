package me.etwxr9.roguelike.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.Listener;
import org.luaj.vm2.LuaFunction;

import me.etwxr9.roguelike.Main;
import me.etwxr9.roguelike.DungeonUtil.EventNames;

public class LuaListenerManager implements Listener {
    // Hashmap<事件名,<道具Id>>
    // public static Map<String, List<String>> luaFuncMap = new HashMap<String,
    // List<String>>();
    // static {
    // var funcs = LuaListenerManager.class.getMethods();
    // for (Method f : funcs) {
    // var name = f.getName();
    // if (!name.startsWith("on")) {
    // continue;
    // }
    // Main.getInstance().getServer().getLogger().info("lua事件读取 " + name);
    // luaFuncMap.put(name, new ArrayList<String>());
    // }
    // }

    //
    public static Map<Class<?>, Map<DungeonTour, List<LuaFunction>>> luaEventHandlers = new HashMap<Class<?>, Map<DungeonTour, List<LuaFunction>>>();

    public static Map<Class<?>, Map<DungeonTour, Map<String, List<LuaFunction>>>> luaEventDynamicHandlers = new HashMap<Class<?>, Map<DungeonTour, Map<String, List<LuaFunction>>>>();

    public static void RegisterEvent(String ename, DungeonTour tour, LuaFunction handler)
            throws ClassNotFoundException {
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!luaEventHandlers.containsKey(ec)) {
            luaEventHandlers.put(ec, new HashMap<DungeonTour, List<LuaFunction>>());
        }
        if (!luaEventHandlers.get(ec).containsKey(tour)) {
            luaEventHandlers.get(ec).put(tour, new ArrayList<LuaFunction>());
        }
        if (!luaEventHandlers.get(ec).get(tour).contains(handler)) {
            luaEventHandlers.get(ec).get(tour).add(handler);
        }

    }

    public static void RegisterEvent(String ename, DungeonTour tour, String dynamicLua, LuaFunction handler)
            throws ClassNotFoundException {
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!luaEventDynamicHandlers.containsKey(ec)) {
            luaEventDynamicHandlers.put(ec, new HashMap<DungeonTour, Map<String, List<LuaFunction>>>());
        }
        if (!luaEventDynamicHandlers.get(ec).containsKey(tour)) {
            luaEventDynamicHandlers.get(ec).put(tour, new HashMap<String, List<LuaFunction>>());
        }
        if (!luaEventDynamicHandlers.get(ec).get(tour).containsKey(dynamicLua)) {
            luaEventDynamicHandlers.get(ec).get(tour).put(dynamicLua, new ArrayList<LuaFunction>());
        }
        if (!luaEventDynamicHandlers.get(ec).get(tour).get(dynamicLua).contains(handler)) {
            luaEventDynamicHandlers.get(ec).get(tour).get(dynamicLua).add(handler);
        }

    }

    public static void UnRegisterEvent(String ename, DungeonTour tour, LuaFunction handler)
            throws ClassNotFoundException {
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!luaEventHandlers.containsKey(ec)) {
            return;
        }
        if (!luaEventHandlers.get(ec).containsKey(tour)) {
            return;
        }
        if (!luaEventHandlers.get(ec).get(tour).contains(handler)) {
            return;
        }
        luaEventHandlers.get(ec).get(tour).remove(handler);
    }

    public static void UnRegisterEvent(String ename, DungeonTour tour, String dynamicLua, LuaFunction handler)
            throws ClassNotFoundException {
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!luaEventDynamicHandlers.containsKey(ec)) {
            return;
        }
        if (!luaEventDynamicHandlers.get(ec).containsKey(tour)) {
            return;
        }
        if (!luaEventDynamicHandlers.get(ec).get(tour).containsKey(dynamicLua)) {
            return;
        }
        if (!luaEventDynamicHandlers.get(ec).get(tour).get(dynamicLua).contains(handler)) {
            return;
        }
        luaEventDynamicHandlers.get(ec).get(tour).get(dynamicLua).remove(handler);
    }

    public static void UnRegisterEvent(DungeonTour tour) {
        luaEventHandlers.forEach((ec, v) -> {
            if (!v.containsKey(tour)) {
                return;
            }
            v.get(tour).clear();
        });
    }

    public static void UnRegisterEvent(DungeonTour tour, String dynamicLua) {
        luaEventDynamicHandlers.forEach((ec, v) -> {
            if (!v.containsKey(tour)) {
                return;
            }
            if (!v.get(tour).containsKey(dynamicLua)) {
                return;
            }
            v.get(tour).get(dynamicLua).clear();
        });
    }

}
