package dev.cheezboi9.farmfix.eventhandlers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class ExplosionHandler implements Listener {

  // Handles blocks exploding like end crystals
  @EventHandler (ignoreCancelled = true)
  public void handleBlockExplosions(BlockExplodeEvent event) {
    List<Block> blocks = event.blockList();

    for (Block block : blocks) {
      if (CropUtility.notCrop(block)) {
        continue;
      }
      block.setType(Material.AIR);
    }
  }

  // Handles creepers and tnt
  @EventHandler (ignoreCancelled = true)
  public void handleEntityExplosions(EntityExplodeEvent event) {
    List<Block> blocks = event.blockList();

    for (Block block : blocks) {
      if (CropUtility.notCrop(block)) {
        continue;
      }
      block.setType(Material.AIR);
    }
  }
}
