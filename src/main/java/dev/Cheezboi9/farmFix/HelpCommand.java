package dev.Cheezboi9.farmFix;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements CommandExecutor {

  private static final MiniMessage MM = MiniMessage.miniMessage();

  // I'm using an array rather than a list for the purposes of this test, but I would use a list of records here
  /**
   * SUB_COMMANDS is a paired array [command usage, permission]
   * and uses a 1 dimensional array for less overhead.
   * I considered using a map but realistically, I'm not going to be adding more commands
   */
  private final String[] SUB_COMMANDS = {
      "/trample <opt: player> <opt: state [true,1,false,0]> - Toggles ability to trample crops for other players or sets it if given a state", FarmPerms.MOD,
      "/trample - Toggles ability to trample crops", FarmPerms.TRAMPLE,
      "/forced <player> <trampleState> - Sets player trample state to be <forcedState>", FarmPerms.MOD,
  };

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                           @NotNull String s, @NotNull String[] args) {
    sendHelpContext(commandSender);
    return true;
  }

  private void sendHelpContext(CommandSender sender) {
    // Message formatting
    String formatPrefix = "<b><green>[FarmFix] </green></b><yellow>";
    String formatSuffix = "</yellow>";
    String headerText = formatPrefix + "-== Available Help Options ==-" + formatSuffix;
    sender.sendMessage(MM.deserialize(headerText));

    // Only sends usage of commands the user has access to
    for (int i = 0; i < SUB_COMMANDS.length; i += 2) {
      String usage = SUB_COMMANDS[i];
      String permission = SUB_COMMANDS[i + 1];

      if (sender.hasPermission(permission) || FarmPerms.isMod(sender)) {
        sender.sendMessage(MM.deserialize(formatPrefix + usage + formatSuffix));
      }
    }
  }

}
