package dev.Cheezboi9.farmFix;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TrampleCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    boolean isPlayer = sender instanceof Player;
    if (!isPlayer && args.length == 0) { // Executing this from console without specifying another player
      sender.sendMessage("You must be a player to toggle trample this way");
      return false;
    }

    // I'm assuming online mode servers, but I can change this to support offline servers
    OfflinePlayer offlinePlayer;
    if (isPlayer) {
      if (!(sender.hasPermission(FarmPerms.TRAMPLE))) {
        sender.sendMessage("You do not have permission to toggle your trample");
        return true;
      }
      offlinePlayer = (Player) sender;
    } else { // Executing this from Console
      offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
      if (!offlinePlayer.hasPlayedBefore()) {
        sender.sendMessage("The specified player doesn't exist. Please check for spelling");
        return true;
      }
    }
    UUID uuid = offlinePlayer.getUniqueId();

    if (args.length == 0) {
      if (TrampleManager.isForced(uuid) && !FarmPerms.isMod(sender)) {
        sender.sendMessage("Unable to toggle as your trample is set by a mod");
        return true;
      }
      boolean newState = TrampleManager.toggleTrample(uuid, false);
      sender.sendMessage("Trample is now: " + newState);
      return true;
    }

    // Handles /trample <player> [opt: forced]
    if (!FarmPerms.isMod(sender)) {
      sender.sendMessage("You do not have permissions to toggle other player's trample");
      return true;
    }

    boolean newState;
    if (args.length == 1) { // Handling /trample <player>
      newState = TrampleManager.toggleTrample(uuid, false);
    } else { // Finally handling the [forced] tag
      boolean forced = (args[1].equalsIgnoreCase("true") || args[1].equals("1"));
      newState = TrampleManager.toggleTrample(uuid, forced);
    }

    // Informing the user of changes
    sender.sendMessage(args[0] + "'s trample is now: " + newState +
        " and mod forced is: " + TrampleManager.isForced(uuid));
    return true;

  }
}