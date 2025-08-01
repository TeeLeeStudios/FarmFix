package dev.cheezboi9.farmfix.eventhandlers;

import dev.cheezboi9.farmfix.FarmFix;
import dev.cheezboi9.farmfix.managers.TrampleManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityTrampleEventHandler implements Listener {

  TrampleManager trampleManager = FarmFix.getTrampleManager();

  // Prevents trampling of farmland by mobs
  @EventHandler(ignoreCancelled = true)
  public void HandleEntityEvent(EntityChangeBlockEvent event) {

    Block farmland = event.getBlock();
    if (farmland.getType() == Material.FARMLAND) {
      event.setCancelled(true);
      Block crop = farmland.getWorld().getBlockAt(farmland.getLocation().add(0,1,0));
      if (crop.getType() == Material.AIR) {
        return;
      }
      // Only allow if mobs are allowed to trample
      if (!trampleManager.canMobTrample()) {
        return;
      }
      CropUtility.CropInfo cropInfo = CropUtility.CropInfo.fromCropBlock(crop.getType());
      if (cropInfo == null) {
        return;
      }

      CropUtility.dropSeed(crop, cropInfo);
      crop.setType(Material.AIR);
      farmland.setType(Material.DIRT);

    }
  }

}
