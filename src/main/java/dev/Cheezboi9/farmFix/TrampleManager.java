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
  private static final File CONFIG_FILE = new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("FarmFix")).getDataFolder(),
      "FarmFix-Trample.yml");
  private static final FileConfiguration CONFIG = YamlConfiguration.loadConfiguration(CONFIG_FILE);

  // Using ConcurrentHashMap instead of a HashMap because it doesn't lock the entire map on access and is threadsafe
  private static final ConcurrentHashMap<UUID, Boolean> playerTrample = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<UUID, Boolean> forcedTrample = new ConcurrentHashMap<>();

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
        playerTrample.put(uuid, trample);
        TrampleManager.forcedTrample.put(uuid, forcedTrample);

      } catch (IllegalArgumentException e) {
        logger.warning("UUID not found in " + CONFIG.getName());
      }
    }
  }

  public static boolean toggleTrample(UUID uuid, boolean forced) {
    boolean newTrample = !playerTrample.getOrDefault(uuid, false);
    playerTrample.put(uuid, newTrample);
    if (forced) {
      forcedTrample.put(uuid, true);
      CONFIG.set(uuid + ".forced", true);
    } else {
      forcedTrample.remove(uuid);
      CONFIG.set(uuid + ".forced", false);
    }
    CONFIG.set(uuid + ".trample", newTrample);
    saveConfig();
    return newTrample;
  }

  public static void forceTrample(UUID uuid, boolean state) {
    forcedTrample.put(uuid, state);
    CONFIG.set(uuid + ".forced", state);
    saveConfig();
  }

  public static boolean isForced(UUID uuid) {
    return forcedTrample.getOrDefault(uuid, false);
  }

  public static boolean canTrample(UUID uuid) {
    return playerTrample.getOrDefault(uuid, false);
  }

}
