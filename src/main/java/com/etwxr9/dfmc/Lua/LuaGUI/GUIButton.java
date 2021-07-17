package com.etwxr9.dfmc.Lua.LuaGUI;

import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;

public class GUIButton {
    public GUIButton(ItemStack itemStack, LuaValue func) {
        this.itemStack = itemStack;
        this.func = func;
    }

    public GUIButton() {
    }

    public ItemStack itemStack;
    public LuaValue func;
}
