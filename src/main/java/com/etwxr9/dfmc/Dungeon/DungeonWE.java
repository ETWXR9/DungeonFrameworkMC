package com.etwxr9.dfmc.Dungeon;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;

import java.text.MessageFormat;
import java.util.Arrays;

import com.boydti.fawe.object.RunnableVal;
import com.boydti.fawe.util.EditSessionBuilder;
import com.boydti.fawe.util.TaskManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DungeonWE {
    // 复制房间（count为-1时为更新房间）
    public static void CloneRoom(DungeonManager dm, Player p, int count) {

        if (dm.currentDungeon == null) {
            return;
        }
        var ri = dm.currentRoom;
        if (ri == null) {
            return;
        }
        var begin = DungeonManager.GetPoint(dm.currentDungeon, ri.Rooms.get(0), new int[] { 0, 0, 0 });
        var end = DungeonManager.GetPoint(dm.currentDungeon, ri.Rooms.get(0), dm.currentDungeon.RoomSize);

        p.sendMessage(count == -1 ? "准备更新 " + (ri.Rooms.size() - 1) + " 个房间" : "准备复制 " + count + " 个房间");
        // USE WEAPI
        var vbegin = BlockVector3.at(begin[0], begin[1], begin[2]);
        var vend = BlockVector3.at(end[0] - 1, end[1] - 1, end[2] - 1);
        CuboidRegion region = new CuboidRegion(vbegin, vend);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        try (EditSession editSession = new EditSessionBuilder(BukkitAdapter.adapt(p.getWorld())).build()) {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
                    region.getMinimumPoint());
            try {
                forwardExtentCopy.setCopyingEntities(false);
                Operations.complete(forwardExtentCopy);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }

        }
        p.sendMessage("房间数据进入剪贴版，开始执行");

        TaskManager.IMP.async(new Runnable() {
            @Override
            public void run() {
                if (count == -1) {
                    for (int i = 1; i < ri.Rooms.size(); i++) {
                        var des = DungeonManager.GetPoint(dm.currentDungeon, ri.Rooms.get(i), new int[] { 0, 0, 0 });
                        p.sendMessage(MessageFormat.format("更新房间{0}", Arrays.toString(des)));

                        try (EditSession editSession = new EditSessionBuilder(BukkitAdapter.adapt(p.getWorld()))
                                .build()) {
                            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                                    .to(BlockVector3.at(des[0], des[1], des[2])).build();
                            try {
                                Operations.complete(operation);
                            } catch (WorldEditException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("更新完毕");
                        }
                    }
                    p.sendMessage("全部更新完毕");
                } else {
                    for (int i = 0; i < count; i++) {
                        var emptyRoom = DungeonManager.GetEmptyRoom(dm.currentDungeon);
                        var des = DungeonManager.GetPoint(dm.currentDungeon, emptyRoom, new int[] { 0, 0, 0 });
                        p.sendMessage(MessageFormat.format("向{0}复制房间", Arrays.toString(des)));
                        try (EditSession editSession = new EditSessionBuilder(BukkitAdapter.adapt(p.getWorld()))
                                .build()) {
                            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                                    .to(BlockVector3.at(des[0], des[1], des[2])).build();
                            try {
                                Operations.complete(operation);
                            } catch (WorldEditException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("复制完毕");
                            dm.currentRoom.Rooms.add(emptyRoom);
                            dm.currentDungeon.EmptyRoomList.remove(emptyRoom);
                        }
                    }
                    TaskManager.IMP.sync(new RunnableVal<Integer>() {
                        @Override
                        public void run(Integer a) {
                            dm.SaveDungeon();
                            p.sendMessage("全部复制完毕");
                        }
                    });

                }
                TaskManager.IMP.sync(new RunnableVal<Integer>() {
                    @Override
                    public void run(Integer a) {
                        Bukkit.getWorld(dm.currentDungeon.Id).save();
                        p.sendMessage("世界已保存");
                    }
                });

            }
        });

    }

}
