package dev.Cheezboi9.farmFix;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.List;

public class PistonEventHandler implements Listener {

  @EventHandler
  public void onPistonExtend(BlockPistonExtendEvent pistonExtendEvent) {
    cancelIllegalAction(pistonExtendEvent, pistonExtendEvent.getBlocks());
  }

  @EventHandler
  public void onPistonRetract(BlockPistonRetractEvent pistonRetractEvent) {
    cancelIllegalAction(pistonRetractEvent, pistonRetractEvent.getBlocks());
  }

  /**
   * Cancels piston extention/retraction if an illegal block is detected
   * @param pistonEvent The Piston event to cancel if illegal block is affected
   * @param blocksAffected The list of blocks the piston would affect
   */
  private void cancelIllegalAction(BlockPistonEvent pistonEvent, List<Block> blocksAffected) {
    for (Block block : blocksAffected) {
      if (block.getType() == Material.FARMLAND || block.getBlockData() instanceof Ageable) {
        pistonEvent.setCancelled(true);
        return;
      }
    }
  }

}
