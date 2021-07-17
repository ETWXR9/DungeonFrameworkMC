package com.etwxr9.dfmc.Lua;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etwxr9.dfmc.Main;
import com.etwxr9.dfmc.Game.DungeonTour;
import com.etwxr9.dfmc.Utility.EventNames;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

public final class DungeonLuaListenerManager {

    private DungeonLuaListenerManager() {
    }

    public static DungeonLuaListenerManager getInstance() {
        return InnerClass.ins;
    }

    private static class InnerClass {
        private static final DungeonLuaListenerManager ins = new DungeonLuaListenerManager();
    }

    public Map<Class<?>, Map<DungeonTour, List<LuaFunction>>> dungeonLuaEventHandlers = new HashMap<Class<?>, Map<DungeonTour, List<LuaFunction>>>();

    public Map<Class<?>, Map<DungeonTour, Map<String, List<LuaFunction>>>> dungeonDynamicLuaEventHandlers = new HashMap<Class<?>, Map<DungeonTour, Map<String, List<LuaFunction>>>>();

    public void RegisterEvent(String ename, DungeonTour tour, LuaFunction handler) throws ClassNotFoundException {
        // ename = ManageEventName(ename);
        // Bukkit.getLogger().info("注册事件" + ename);
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!dungeonLuaEventHandlers.containsKey(ec)) {
            dungeonLuaEventHandlers.put(ec, new HashMap<DungeonTour, List<LuaFunction>>());
        }
        if (!dungeonLuaEventHandlers.get(ec).containsKey(tour)) {

            dungeonLuaEventHandlers.get(ec).put(tour, new ArrayList<LuaFunction>());
        }
        if (!dungeonLuaEventHandlers.get(ec).get(tour).contains(handler)) {
            if (handler == null || handler == LuaValue.NIL) {
                throw new NullPointerException("订阅方法为空");
            }
            // Main.getInstance().getLogger().info("订阅方法 " + handler.classnamestub());
            dungeonLuaEventHandlers.get(ec).get(tour).add(handler);
        }

    }

    public void RegisterDynamicEvent(String ename, DungeonTour tour, String dynamicLua, LuaFunction handler)
            throws ClassNotFoundException {
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!dungeonDynamicLuaEventHandlers.containsKey(ec)) {
            dungeonDynamicLuaEventHandlers.put(ec, new HashMap<DungeonTour, Map<String, List<LuaFunction>>>());
        }
        if (!dungeonDynamicLuaEventHandlers.get(ec).containsKey(tour)) {
            dungeonDynamicLuaEventHandlers.get(ec).put(tour, new HashMap<String, List<LuaFunction>>());
        }
        if (!dungeonDynamicLuaEventHandlers.get(ec).get(tour).containsKey(dynamicLua)) {
            dungeonDynamicLuaEventHandlers.get(ec).get(tour).put(dynamicLua, new ArrayList<LuaFunction>());
        }
        if (!dungeonDynamicLuaEventHandlers.get(ec).get(tour).get(dynamicLua).contains(handler)) {
            dungeonDynamicLuaEventHandlers.get(ec).get(tour).get(dynamicLua).add(handler);
        }

    }

    public void UnRegisterEvent(String ename, DungeonTour tour, LuaFunction handler) throws ClassNotFoundException {
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!dungeonLuaEventHandlers.containsKey(ec)) {
            return;
        }
        if (!dungeonLuaEventHandlers.get(ec).containsKey(tour)) {
            return;
        }
        if (!dungeonLuaEventHandlers.get(ec).get(tour).contains(handler)) {
            return;
        }
        dungeonLuaEventHandlers.get(ec).get(tour).remove(handler);
    }

    public void UnRegisterDynamicEvent(String ename, DungeonTour tour, String dynamicLua, LuaFunction handler)
            throws ClassNotFoundException {
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!dungeonDynamicLuaEventHandlers.containsKey(ec)) {
            return;
        }
        if (!dungeonDynamicLuaEventHandlers.get(ec).containsKey(tour)) {
            return;
        }
        if (!dungeonDynamicLuaEventHandlers.get(ec).get(tour).containsKey(dynamicLua)) {
            return;
        }
        if (!dungeonDynamicLuaEventHandlers.get(ec).get(tour).get(dynamicLua).contains(handler)) {
            return;
        }
        dungeonDynamicLuaEventHandlers.get(ec).get(tour).get(dynamicLua).remove(handler);
    }

    public void UnRegisterTourEvent(DungeonTour tour) {
        dungeonLuaEventHandlers.forEach((ec, v) -> {
            if (!v.containsKey(tour)) {
                return;
            }
            v.get(tour).clear();
        });
    }

    public void UnRegisterDynamaicAllEvent(DungeonTour tour, String dynamicLua) {
        dungeonDynamicLuaEventHandlers.forEach((ec, v) -> {
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
