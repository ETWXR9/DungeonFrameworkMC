package me.etwxr9.roguelike.DungeonUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.ArrowBodyCountChangeEvent;
import org.bukkit.event.entity.BatToggleSleepEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityEnterBlockEvent;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.PigZombieAngerEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.entity.StriderTemperatureChangeEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import me.etwxr9.roguelike.Event.EnterRoomEvent;
import me.etwxr9.roguelike.Event.LeaveRoomEvent;
import me.etwxr9.roguelike.Event.LuaCmdEvent;

public class EventNames {
    public static List<Class<? extends Event>> Events = Arrays.asList(PlayerAnimationEvent.class,
            PlayerBedEnterEvent.class, PlayerBedLeaveEvent.class, PlayerChangedMainHandEvent.class,
            PlayerChangedWorldEvent.class, PlayerChannelEvent.class, PlayerCommandPreprocessEvent.class,
            PlayerCommandSendEvent.class, PlayerDropItemEvent.class, PlayerEditBookEvent.class,
            PlayerEggThrowEvent.class, PlayerExpChangeEvent.class, PlayerFishEvent.class,
            PlayerGameModeChangeEvent.class, PlayerHarvestBlockEvent.class, PlayerInteractEntityEvent.class,
            PlayerInteractEvent.class, PlayerItemBreakEvent.class, PlayerItemConsumeEvent.class,
            PlayerItemDamageEvent.class, PlayerItemHeldEvent.class, PlayerItemMendEvent.class, PlayerJoinEvent.class,
            PlayerKickEvent.class, PlayerLevelChangeEvent.class, PlayerLocaleChangeEvent.class, PlayerLoginEvent.class,
            PlayerMoveEvent.class, PlayerQuitEvent.class, PlayerRecipeDiscoverEvent.class,
            PlayerResourcePackStatusEvent.class, PlayerRespawnEvent.class, PlayerRiptideEvent.class,
            PlayerShearEntityEvent.class, PlayerSpawnLocationEvent.class, PlayerStatisticIncrementEvent.class,
            PlayerSwapHandItemsEvent.class, PlayerTakeLecternBookEvent.class, PlayerToggleFlightEvent.class,
            PlayerToggleSneakEvent.class, PlayerToggleSprintEvent.class, PlayerVelocityEvent.class,
            AreaEffectCloudApplyEvent.class, ArrowBodyCountChangeEvent.class, BatToggleSleepEvent.class,
            CreeperPowerEvent.class, EnderDragonChangePhaseEvent.class, EntityAirChangeEvent.class,
            EntityBreedEvent.class, EntityChangeBlockEvent.class, EntityCombustEvent.class, EntityDeathEvent.class,
            PlayerDeathEvent.class, EntityDismountEvent.class, EntityDropItemEvent.class, EntityEnterBlockEvent.class,
            EntityEnterLoveModeEvent.class, EntityExplodeEvent.class, EntityInteractEvent.class, EntityMountEvent.class,
            EntityPickupItemEvent.class, EntityPortalEnterEvent.class, EntityPoseChangeEvent.class,
            EntityPotionEffectEvent.class, EntityRegainHealthEvent.class, EntityResurrectEvent.class,
            EntityShootBowEvent.class, EntitySpawnEvent.class, EntitySpellCastEvent.class, EntityTameEvent.class,
            EntityTargetEvent.class, EntityTeleportEvent.class, EntityToggleGlideEvent.class,
            EntityToggleSwimEvent.class, EntityTransformEvent.class, EntityUnleashEvent.class,
            ExplosionPrimeEvent.class, FireworkExplodeEvent.class, FoodLevelChangeEvent.class, HorseJumpEvent.class,
            ItemDespawnEvent.class, ItemMergeEvent.class, PigZombieAngerEvent.class, ProjectileHitEvent.class,
            SheepDyeWoolEvent.class, SheepRegrowWoolEvent.class, SlimeSplitEvent.class,
            StriderTemperatureChangeEvent.class, VillagerAcquireTradeEvent.class, VillagerCareerChangeEvent.class,
            VillagerReplenishTradeEvent.class, EnchantItemEvent.class, InventoryCloseEvent.class,
            InventoryClickEvent.class, InventoryDragEvent.class, TradeSelectEvent.class, InventoryOpenEvent.class,
            PrepareAnvilEvent.class, PrepareItemCraftEvent.class, PrepareItemEnchantEvent.class,
            PrepareSmithingEvent.class, VehicleCreateEvent.class, VehicleDamageEvent.class, VehicleDestroyEvent.class,
            VehicleEnterEvent.class, VehicleExitEvent.class, VehicleMoveEvent.class, VehicleUpdateEvent.class,
            EntityDamageEvent.class, EntityDamageByEntityEvent.class, EntityDamageByBlockEvent.class,

            // 插件自定义事件
            EnterRoomEvent.class, LeaveRoomEvent.class, LuaCmdEvent.class);
    public static Map<String, String> EventClassNames = new HashMap<String, String>();
}
