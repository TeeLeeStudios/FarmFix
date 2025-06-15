package dev.Cheezboi9.farmFix;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FarmPerms {
  public static final String BREAK = "farmfix.break";
  public static final String TRAMPLE = "farmfix.trample";
  public static final String HARVEST = "farmfix.harvest";
  public static final String MOD = "farmfix.mod"; // Easily changeable to used mod groups on servers


  public static boolean isMod(Player player) {
    return player.hasPermission(MOD) || player.hasPermission("groups." + MOD) || player.isOp();
  }

  public static boolean isMod(CommandSender sender) {
    return sender.hasPermission(MOD) || sender.hasPermission("groups." + MOD) || sender.isOp();
  }
}
