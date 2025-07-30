package dev.cheezboi9.farmfix.commands;

import dev.cheezboi9.farmfix.FarmFix;
import dev.cheezboi9.farmfix.FarmPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ForcedCommand implements CommandExecutor {


  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {

    if (args.length < 2) {
      sender.sendMessage("Missing arguments. Use /ff to get usage");
      return true;
    }

    boolean isPlayer = sender instanceof Player;

    if (isPlayer && FarmPerms.notMod((Player)sender)) {
      sender.sendMessage("You do not have permission to force trample");
      return true;
    }

    // Handling /forced <player>
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0].trim());
    if (!offlinePlayer.hasPlayedBefore()) {
      sender.sendMessage("Unable to find player");
      return true;
    }

    // Handling the state value
    boolean toForce = args[1].equalsIgnoreCase("true") || args[1].equals("1");

    FarmFix.getTrampleManager().forceTrample(offlinePlayer.getUniqueId(), toForce);

    sender.sendMessage(args[0] + "'s forced trample state is now: " + toForce);

    return true;
  }
}
