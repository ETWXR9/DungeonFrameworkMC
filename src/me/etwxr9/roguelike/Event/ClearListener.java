package me.etwxr9.roguelike.Event;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.etwxr9.roguelike.Game.TourManager;

public class ClearListener implements Listener{
    @EventHandler
     public void onEnemyDeath(EntityDeathEvent event) {
        var p = event.getEntity().getKiller();
        if (p==null) {
            return;
        }
        var tour = TourManager.GetTour(p);
        if (tour == null) {
            return;
        }
        if (tour.EnemyList.contains((Entity)event.getEntity())) {
            tour.EnemyList.remove((Entity)event.getEntity());
        }
        if (tour.EnemyList.size() == 0) {
            tour.isClear =true;
        }
     }
}
