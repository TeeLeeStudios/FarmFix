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

    if (args.length == 0) {
      // Early return for console trying to use trample incorrectly
      if (!isPlayer) {
        sender.sendMessage("You must be a player to toggle trample this way");
        return true;
      }
      Player player = (Player) sender;
      // Early return if no perms to toggle trample
      if (!FarmPerms.canTrample(player)) {
        sender.sendMessage("You do not have permission to toggle your trample");
        return true;
      }
      // Early return if we don't have perms or forced toggle
      if (TrampleManager.isForced(player.getUniqueId()) && FarmPerms.notMod(player)) {
        sender.sendMessage("Unable to toggle as your trample is set by a mod");
        return true;
      }

      boolean newState = !TrampleManager.canTrample(player.getUniqueId());
      TrampleManager.toggleTrample(player.getUniqueId(), newState);
      sender.sendMessage("Trample is now: " + newState);
      return true;
    }

    // Only allows mods to use /trample <player>
    if (!FarmPerms.isMod(sender)) {
      sender.sendMessage("You do not have permissions to toggle other player's trample");
      return true;
    }

    // Handling /trample <player>
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0].trim());
    if (!offlinePlayer.hasPlayedBefore()) {
      sender.sendMessage("Unable to find player");
      return true;
    }

    boolean trampleState;
    UUID uuid = offlinePlayer.getUniqueId();

    if (args.length >= 2) {
      trampleState = args[1].equalsIgnoreCase("true") || args[1].equals("1");
    } else {
      trampleState = false;
    }

    TrampleManager.toggleTrample(uuid, trampleState);

    // Informing the user of changes
    sender.sendMessage(args[0] + "'s trample is now: " + trampleState);
    return true;
  }
}