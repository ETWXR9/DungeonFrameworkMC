package me.etwxr9.roguelike.Game;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LuaRunnable extends BukkitRunnable {

    public LuaRunnable(JavaPlugin plugin, LuaFunction luaFunction) {
        this.plugin = plugin;
        this.luaFunction = luaFunction;
    }

    private final JavaPlugin plugin;
    private final LuaFunction luaFunction;

    @Override
    public void run() {
        luaFunction.call(CoerceJavaToLua.coerce(this));
    }

    public synchronized BukkitTask runTask() throws IllegalArgumentException, IllegalStateException {
        return super.runTask(plugin);
    }

    public synchronized BukkitTask runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException {
        return super.runTaskAsynchronously(plugin);
    }

    public synchronized BukkitTask runTaskLater(long delay) throws IllegalArgumentException, IllegalStateException {
        return super.runTaskLater(plugin, delay);
    }

    public synchronized BukkitTask runTaskLaterAsynchronously(long delay)
            throws IllegalArgumentException, IllegalStateException {
        return super.runTaskLaterAsynchronously(plugin, delay);
    }

    public synchronized BukkitTask runTaskTimer(long delay, long period)
            throws IllegalArgumentException, IllegalStateException {
        return super.runTaskTimer(plugin, delay, period);
    }

    public synchronized BukkitTask runTaskTimerAsynchronously(long delay, long period)
            throws IllegalArgumentException, IllegalStateException {
        return super.runTaskTimerAsynchronously(plugin, delay, period);
    }

}