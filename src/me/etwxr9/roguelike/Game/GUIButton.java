package me.etwxr9.roguelike.Game;

import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;

public class GUIButton {
    public GUIButton(ItemStack itemStack, LuaValue self, LuaValue func) {
        this.itemStack = itemStack;
        this.self = self;
        this.func = func;
    }

    public GUIButton() {
    }

    public ItemStack itemStack;
    public LuaValue self;
    public LuaValue func;

}
