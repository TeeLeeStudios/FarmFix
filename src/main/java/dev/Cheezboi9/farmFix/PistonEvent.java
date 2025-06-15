package dev.Cheezboi9.farmFix;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.ArrayList;
import java.util.List;

public class PistonEvent implements Listener {

  /**
   * Cancels piston extent/retract actions if it would break a crop/farmland
   * @param pistonEvent Piston event to handle
   */
  @EventHandler
  public void onPistonChange(BlockPistonEvent pistonEvent) {
    List<Block> affectedBlocks = switch (pistonEvent) {
      case BlockPistonExtendEvent extendEvent -> new ArrayList<>(extendEvent.getBlocks());
      case BlockPistonRetractEvent retractEvent -> new ArrayList<>(retractEvent.getBlocks());
      default -> null;
    };
    // Early return for no blocks affected
    if (affectedBlocks == null) {
      return;
    }

    // Cancels event if a crop or farmland would be affected
    for (Block affectedBlock : affectedBlocks) {
      Material type = affectedBlock.getType();
      if (type == Material.FARMLAND || affectedBlock.getBlockData() instanceof Ageable) {
        pistonEvent.setCancelled(true);
        return;
      }
    }
  }
}
