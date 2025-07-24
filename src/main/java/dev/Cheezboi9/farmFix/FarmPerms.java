package dev.Cheezboi9.farmFix;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FarmPerms {
  public static final String BREAK = "farmfix.break";
  public static final String TRAMPLE = "farmfix.trample";
  public static final String HARVEST = "farmfix.harvest";
  public static final String MOD = "farmfix.mod"; // Easily changeable to used mod groups on servers


  public static boolean notMod(Player player) {
    return !player.hasPermission(MOD);
  }

  public static boolean isMod(CommandSender sender) {
    return sender.hasPermission(MOD);
  }

  public static boolean canHarvest(Player player){
    return player.hasPermission(HARVEST) || (player.hasPermission(MOD));
  }

  public static boolean canBreak(Player player){
    return player.hasPermission(BREAK) || (player.hasPermission(MOD));
  }

  public static boolean canTrample(Player player){
    return player.hasPermission(TRAMPLE) || (player.hasPermission(MOD));
  }
}
