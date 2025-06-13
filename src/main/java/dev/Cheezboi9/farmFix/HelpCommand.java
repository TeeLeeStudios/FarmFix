package dev.Cheezboi9.farmFix;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements CommandExecutor {

  private final String[] HELP_COMMANDS = {"help", "-h"};

  /* SUB_COMMANDS is a paired array [command usage, permission]
     and uses a 1 dimensional array for less overhead.
     I considered using a map but realistically, I'm not going to be adding more commands
  */
  private final String[] SUB_COMMANDS = {
      "/ff trample [opt: player] [opt: forced] - Toggles ability to trample crops", FarmPerms.TRAMPLE,
  };

  /**
   * Handles commands '/ff help' and '/ff -h' to display help context menu
   */
  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                           @NotNull String s, @NotNull String[] args) {
    // Case-insensitive command argument check
    if (args.length == 0 || isHelpCommand(args[0])) {
      sendHelpContext(commandSender);
      return true;
    }
    return false;
  }

  // Extracted the following functions for readability
  private boolean isHelpCommand(String arg) {
    for (String command : HELP_COMMANDS) {
      if (arg.equalsIgnoreCase(command)) {
        return true;
      }
    }
    return false;
  }

  private void sendHelpContext(CommandSender sender) {
    // Message formatting
    String formatPrefix = "<b><green>[FarmFix] </green></b><yellow>";
    String formatSuffix = "</yellow>";
    String headerText = formatPrefix + "-== Available Help Options ==-" + formatSuffix;
    MiniMessage mm = MiniMessage.miniMessage();
    sender.sendMessage(mm.deserialize(headerText));

    // First, iterate by 2 to check if they have the correct permission, and send the command usage (i-1) if they do
    for (int i = 1; i < SUB_COMMANDS.length; i += 2) {
      if (sender.hasPermission(SUB_COMMANDS[i]) || FarmPerms.isMod(sender)) {
        sender.sendMessage(mm.deserialize(formatPrefix + SUB_COMMANDS[i - 1] + formatSuffix));
      }
    }
  }

}
