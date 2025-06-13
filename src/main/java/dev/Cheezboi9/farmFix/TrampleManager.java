package dev.Cheezboi9.farmFix;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class TrampleManager {

  // Appends FarmFix to our logs which is easier to debug later
  static Logger logger = Logger.getLogger("FarmFix");

  // Get the Configuration File
  private static final File CONFIG_FILE = new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("FarmFix")).getDataFolder(), "FarmFix-Trample.yml");
  private static final FileConfiguration CONFIG = YamlConfiguration.loadConfiguration(CONFIG_FILE);

  // Using ConcurrentHashMap instead of a HashMap because it doesn't lock the entire map on access and is threadsafe
  private static final ConcurrentHashMap<UUID, Boolean> PLAYER_TRAMPLE = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<UUID, Boolean> FORCED_TRAMPLE = new ConcurrentHashMap<>();

  static { // Called on load to memory
    loadConfig();
  }

  private static void saveConfig() {
    try { // Try Catch for most file based operations to prevent crashing
      CONFIG.save(CONFIG_FILE);
    } catch (IOException e) {
      logger.severe("Unable to save FarmFix-Trample.yml : " + e.getMessage());
    }
  }

  private static void loadConfig() {
    // Early return if no file
    if (!CONFIG_FILE.exists()) {
      return;
    }

    for (String key : CONFIG.getKeys(false)) {
      try {
        UUID uuid = UUID.fromString(key);
        boolean trample = CONFIG.getBoolean(key + ".trample"); // Default false
        boolean forcedTrample = CONFIG.getBoolean(key + ".forced"); // Also default false
        PLAYER_TRAMPLE.put(uuid, trample);
        FORCED_TRAMPLE.put(uuid, forcedTrample);

      } catch (IllegalArgumentException e) {
        logger.warning("UUID not found in " + CONFIG.getName());
      }
    }
  }

  public static boolean toggleTrample(UUID uuid, boolean forced) {
    boolean newTrample = !PLAYER_TRAMPLE.getOrDefault(uuid, false);
    PLAYER_TRAMPLE.put(uuid, newTrample);
    if (forced) {
      FORCED_TRAMPLE.put(uuid, true);
      CONFIG.set(uuid + ".forced", true);
    } else {
      FORCED_TRAMPLE.remove(uuid);
      CONFIG.set(uuid + ".forced", false);
    }
    CONFIG.set(uuid + ".trample", newTrample);
    saveConfig();
    return newTrample;
  }

  public static boolean isForced(UUID uuid) {
    return FORCED_TRAMPLE.getOrDefault(uuid, false);
  }

  public static boolean canTrample(UUID uuid) {
    return PLAYER_TRAMPLE.getOrDefault(uuid, false);
  }

}
