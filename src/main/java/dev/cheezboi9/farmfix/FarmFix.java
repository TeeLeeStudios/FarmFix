package dev.cheezboi9.farmfix;

import dev.cheezboi9.farmfix.commands.ForcedCommand;
import dev.cheezboi9.farmfix.commands.HelpCommand;
import dev.cheezboi9.farmfix.commands.TrampleCommand;
import dev.cheezboi9.farmfix.eventhandlers.CropBreakEventHandler;
import dev.cheezboi9.farmfix.eventhandlers.EntityTrampleEventHandler;
import dev.cheezboi9.farmfix.eventhandlers.PistonEventHandler;
import dev.cheezboi9.farmfix.managers.TrampleManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class FarmFix extends JavaPlugin {
  private static TrampleManager trampleManager;
  @Override
  public void onEnable() {
    registerCommands();
    registerEventListeners();
    getLogger().info("Loaded Plugin");
    trampleManager = new TrampleManager();
    trampleManager.loadConfig();
  }

  @Override
  public void onDisable() {
    getLogger().info("Unloaded Plugin");
    // Save our config so it's not lost when we disable our plugin
    trampleManager.saveConfig();
  }

  public void registerCommands() {
    Objects.requireNonNull(this.getCommand("ff")).setExecutor(new HelpCommand());
    Objects.requireNonNull(this.getCommand("trample")).setExecutor(new TrampleCommand());
    Objects.requireNonNull(this.getCommand("forced")).setExecutor(new ForcedCommand());
  }

  public void registerEventListeners() {
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new CropBreakEventHandler(), this);
    pluginManager.registerEvents(new PistonEventHandler(), this);
    pluginManager.registerEvents(new EntityTrampleEventHandler(), this);
  }

  public static TrampleManager getTrampleManager(){
    return trampleManager;
  }
}
