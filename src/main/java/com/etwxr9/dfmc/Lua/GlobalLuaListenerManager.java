package com.etwxr9.dfmc.Lua;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etwxr9.dfmc.Main;
import com.etwxr9.dfmc.Utility.EventNames;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

public class GlobalLuaListenerManager {

    private GlobalLuaListenerManager() {
    }

    public static GlobalLuaListenerManager getInstance() {
        return InnerClass.ins;
    }

    private static class InnerClass {
        private static final GlobalLuaListenerManager ins = new GlobalLuaListenerManager();
    }

    public Map<Class<?>, List<LuaFunction>> luaEventHandlers = new HashMap<Class<?>, List<LuaFunction>>();

    public Map<Class<?>, Map<String, List<LuaFunction>>> dynamicLuaEventHandlers = new HashMap<Class<?>, Map<String, List<LuaFunction>>>();

    public void RegisterEvent(String ename, LuaFunction handler) throws ClassNotFoundException {
        // ename = ManageEventName(ename);
        // Bukkit.getLogger().info("注册事件" + ename);
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!luaEventHandlers.containsKey(ec)) {
            luaEventHandlers.put(ec, new ArrayList<LuaFunction>());
        }
        if (!luaEventHandlers.get(ec).contains(handler)) {
            if (handler == null || handler == LuaValue.NIL) {
                throw new NullPointerException("订阅方法为空");
            }
            // Main.getInstance().getLogger().info("订阅方法 " + handler.classnamestub());
            luaEventHandlers.get(ec).add(handler);
        }

    }

    public void RegisterDynamicEvent(String ename, String dynamicLua, LuaFunction handler)
            throws ClassNotFoundException {
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!dynamicLuaEventHandlers.containsKey(ec)) {
            dynamicLuaEventHandlers.put(ec, new HashMap<String, List<LuaFunction>>());
        }
        if (!dynamicLuaEventHandlers.get(ec).containsKey(dynamicLua)) {
            dynamicLuaEventHandlers.get(ec).put(dynamicLua, new ArrayList<LuaFunction>());
        }
        if (!dynamicLuaEventHandlers.get(ec).get(dynamicLua).contains(handler)) {
            dynamicLuaEventHandlers.get(ec).get(dynamicLua).add(handler);
        }
    }

    public void UnRegisterEvent(String ename, LuaFunction handler) throws ClassNotFoundException {
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!luaEventHandlers.containsKey(ec)) {
            return;
        }
        if (!luaEventHandlers.get(ec).contains(handler)) {
            return;
        }
        luaEventHandlers.get(ec).remove(handler);
    }

    public void UnRegisterDynamicEvent(String ename, String dynamicLua, LuaFunction handler)
            throws ClassNotFoundException {
        var ec = Class.forName(EventNames.EventClassNames.get(ename), false, Main.class.getClassLoader());
        if (!dynamicLuaEventHandlers.containsKey(ec)) {
            return;
        }
        if (!dynamicLuaEventHandlers.get(ec).containsKey(dynamicLua)) {
            return;
        }
        if (!dynamicLuaEventHandlers.get(ec).get(dynamicLua).contains(handler)) {
            return;
        }
        dynamicLuaEventHandlers.get(ec).get(dynamicLua).remove(handler);
    }

    public void UnRegisterDynamaicAllEvent(String dynamicLua) {
        dynamicLuaEventHandlers.forEach((ec, v) -> {
            if (!v.containsKey(dynamicLua)) {
                return;
            }
            v.get(dynamicLua).clear();
        });
    }

}
