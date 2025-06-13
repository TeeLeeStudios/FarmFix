package dev.Cheezboi9.farmFix;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class FarmFix extends JavaPlugin {

  @Override
  public void onEnable() {
    registerCommands();
    registerEventListeners();
    getLogger().info("Loaded Plugin");
  }

  @Override
  public void onDisable() {
    getLogger().info("Unloaded Plugin");
  }

  public void registerCommands() {
    Objects.requireNonNull(this.getCommand("ff")).setExecutor(new HelpCommand());
    Objects.requireNonNull(this.getCommand("trample")).setExecutor(new TrampleCommand());
  }

  public void registerEventListeners() {
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new CropBreakEvent(), this);
  }
}
