package dev.cheezboi9.farmfix.managers;

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
  private final File CONFIG_FILE = new File(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("FarmFix")).getDataFolder(),
      "FarmFix-Trample.yml");
  private final FileConfiguration CONFIG = YamlConfiguration.loadConfiguration(CONFIG_FILE);

  // Using ConcurrentHashMap instead of a HashMap because it doesn't lock the entire map on access and is threadsafe
  private final ConcurrentHashMap<UUID, Boolean> playerTrample = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<UUID, Boolean> forcedTrample = new ConcurrentHashMap<>();

  private boolean mobTrample = false;

  public void saveConfig() {
    try { // Try Catch for most file based operations to prevent crashing
      CONFIG.save(CONFIG_FILE);
    } catch (IOException e) {
      logger.severe("Unable to save FarmFix-Trample.yml : " + e.getMessage());
    }
  }

  public void loadConfig() {
    // Early return if no file
    if (!CONFIG_FILE.exists()) {
      return;
    }

    for (String key : CONFIG.getKeys(false)) {
      try {
        UUID uuid = UUID.fromString(key);
        boolean trample = CONFIG.getBoolean(key + ".trample"); // Default false
        boolean isForcedTrample = CONFIG.getBoolean(key + ".forced"); // Also default false
        playerTrample.put(uuid, trample);
        forcedTrample.put(uuid, isForcedTrample);

      } catch (IllegalArgumentException e) {
        logger.warning("UUID not found in " + CONFIG.getName());
      }
    }
  }
  // TODO: Do not save default values
  public void toggleTrample(UUID uuid, boolean state) {
    playerTrample.put(uuid, state);
    CONFIG.set(uuid + ".trample", state);
    saveConfig();
  }
  // TODO: Do not save default values
  public void forceTrample(UUID uuid, boolean state) {
    forcedTrample.put(uuid, state);
    CONFIG.set(uuid + ".forced", state);
    saveConfig();
  }


  public boolean isForced(UUID uuid) {
    return forcedTrample.getOrDefault(uuid, false);
  }

  public boolean canTrample(UUID uuid) {
    return playerTrample.getOrDefault(uuid, false);
  }

  public void setMobTrample (boolean state) {
    this.mobTrample = state;
  }

  public boolean canMobTrample() {
    return this.mobTrample;
  }

}
