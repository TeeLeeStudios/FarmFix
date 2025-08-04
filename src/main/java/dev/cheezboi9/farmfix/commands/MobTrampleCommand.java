package dev.cheezboi9.farmfix.commands;

import dev.cheezboi9.farmfix.FarmFix;
import dev.cheezboi9.farmfix.FarmPerms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;

public class MobTrampleCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                           @NotNull String s, @NotNull String[] args) {

    if (args.length == 0) {
      commandSender.sendMessage("Please specify mob trample state as [true] / [1] or [false] / [0]");
      return true;
    }

    if (FarmPerms.isMod(commandSender)) {
      boolean mobsCanTrample = args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("1");
      FarmFix.getTrampleManager().setMobTrample(mobsCanTrample);
    }
    else {
      commandSender.sendMessage("You do not have permission to set this!");
    }
    return true;
  }
}
