package dev.cheezboi9.farmfix.eventhandlers.croputils;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CropUtility {

  // a crop matrix that reads [crop type, primary drop, seed drop]
  public static enum CropInfo {
    WHEAT(Material.WHEAT, Material.WHEAT, Material.WHEAT_SEEDS),
    BEETROOTS(Material.BEETROOTS, Material.BEETROOT, Material.BEETROOT_SEEDS),
    COCOA(Material.COCOA, Material.COCOA_BEANS, Material.COCOA_BEANS),
    // Seedless crops
    CARROTS(Material.CARROTS, Material.CARROT, null),
    POTATOES(Material.POTATOES, Material.POTATO, null),
    NETHER_WART(Material.NETHER_WART, Material.NETHER_WART, null);

    private final Material cropType;
    private final Material cropDrop;
    private final Material seedDrop;

    // Constructor
    CropInfo(Material cropType, Material cropDrop, Material seedDrop) {
      this.cropType = cropType;
      this.cropDrop = cropDrop;
      this.seedDrop = seedDrop;
    }

    // Accessor Methods
    public Material getCropDrop() {
      return cropDrop;
    }

    public Material getSeedDrop() {
      return seedDrop;
    }

    // Search func returns null if crop type is not in CropInfo.
    public static CropInfo fromCropBlock(Material type) {
      for (CropInfo info : CropInfo.values()) {
        if (info.cropType == type) {
          return info;
        }
      }
      return null; // The crop block is not found
    }
  }

  public static void dropSeed(Block crop, CropInfo cropInfo) {
    Material seed = cropInfo.getSeedDrop();

    // Found this to be much cleaner than an if statement to check null case
    crop.getWorld().dropItemNaturally(
        crop.getLocation(),
        ItemStack.of(Objects.requireNonNullElseGet(seed, cropInfo::getCropDrop), 1));
    crop.getWorld().playSound(crop.getLocation(), Sound.ITEM_CROP_PLANT, 0.4f, 1.0f);
  }

}
