package dev.cheezboi9.farmfix;

import dev.cheezboi9.farmfix.commands.ForcedCommand;
import dev.cheezboi9.farmfix.commands.HelpCommand;
import dev.cheezboi9.farmfix.commands.MobTrampleCommand;
import dev.cheezboi9.farmfix.commands.TrampleCommand;
import dev.cheezboi9.farmfix.eventhandlers.*;
import dev.cheezboi9.farmfix.managers.DatabaseManager;
import dev.cheezboi9.farmfix.managers.TrampleManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class FarmFix extends JavaPlugin {
  private static TrampleManager trampleManager;
  private static DatabaseManager databaseManager;
  @Override
  public void onEnable() {
    trampleManager = new TrampleManager();
    databaseManager = new DatabaseManager();
    registerCommands();
    registerEventListeners();
    getLogger().info("Loaded Plugin");
  }

  @Override
  public void onDisable() {
    // Save our config so it's not lost when we disable our plugin
    databaseManager.close();
    getLogger().info("Unloaded Plugin");
  }

  public void registerCommands() {
    Objects.requireNonNull(this.getCommand("ff")).setExecutor(new HelpCommand());
    Objects.requireNonNull(this.getCommand("trample")).setExecutor(new TrampleCommand());
    Objects.requireNonNull(this.getCommand("forced")).setExecutor(new ForcedCommand());
    Objects.requireNonNull(this.getCommand("mobtrample")).setExecutor(new MobTrampleCommand());
  }

  public void registerEventListeners() {
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new CropBreakEventHandler(), this);
    pluginManager.registerEvents(new PistonEventHandler(), this);
    pluginManager.registerEvents(new EntityTrampleEventHandler(), this);
    pluginManager.registerEvents(new FarmlandBreakHandler(), this);
    pluginManager.registerEvents(new LiquidBreakEventHandler(), this);
    pluginManager.registerEvents(new ExplosionHandler(), this);
  }

  public static TrampleManager getTrampleManager(){
    return trampleManager;
  }

  public static DatabaseManager getDatabaseManager(){
    return databaseManager;
  }
}
