package dev.Cheezboi9.farmFix;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

  private static final ConcurrentHashMap<UUID, Long> breakCooldown = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<UUID, Long> harvestCooldown = new ConcurrentHashMap<>();
  private static final long TICKS_TO_MS = 50L; // (Convert ticks to ms) 50L = 1000ms / 20 ticks per second
  private static final long BREAK_COOLDOWN_IN_MS = 10L * TICKS_TO_MS; // 0.5 seconds or 500mms
  private static final long HARVEST_COOLDOWN_IN_MS = 2L * TICKS_TO_MS; // 0.1 seconds or 100ms

  /**
   * Compares current time and cached cooldown time.
   */
  private static boolean isOnCooldown(UUID uuid, ConcurrentHashMap<UUID, Long> cooldownMap, long cooldownInMS) {
    long now = System.currentTimeMillis();
    long cooldownStarted = cooldownMap.getOrDefault(uuid, 0L);
    return (now - cooldownStarted) < cooldownInMS;
  }

  /**
   * Cancels event and returns true if on cooldown.
   * Returns false without cancelling event if not on cooldown.
   * @param event the event to try and cancel
   */
  public static boolean tryCancel(BlockBreakEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();

    if (isOnCooldown(uuid, breakCooldown, BREAK_COOLDOWN_IN_MS)) {
      breakCooldown.put(uuid, System.currentTimeMillis());
      event.setCancelled(true);
      return true;
    }

    return false;
  }

  // Overload
  /**
   * Cancels event and returns true if on cooldown.
   * Returns false without cancelling event if not on cooldown.
   * @param event the event to try and cancel
   */
  public static boolean tryCancel(PlayerInteractEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();

    if (isOnCooldown(uuid, harvestCooldown, HARVEST_COOLDOWN_IN_MS)) {
      harvestCooldown.put(uuid, System.currentTimeMillis());
      event.setCancelled(true);
      return true;
    }

    return false;
  }

  /**
   * Cancel the event and set cooldown
   * @param event the event to cancel
   */
  public static void cancelAction(PlayerInteractEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    harvestCooldown.put(uuid, System.currentTimeMillis());
    event.setCancelled(true);
  }

  // Overload
  /**
   * Cancel the event and set cooldown
   * @param event the event to cancel
   */
  public static void cancelAction(BlockBreakEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    breakCooldown.put(uuid, System.currentTimeMillis());
    event.setCancelled(true);
  }
}
