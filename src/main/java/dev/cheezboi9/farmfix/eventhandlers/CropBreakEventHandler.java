package dev.cheezboi9.farmfix.eventhandlers;

import dev.cheezboi9.farmfix.FarmFix;
import dev.cheezboi9.farmfix.FarmPerms;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;

public class CropBreakEventHandler implements Listener {

  private final float RARE_EVENT_CHANCE = 0.05f; // Chance to immediately grow a crop after harvest
  private final float EXTRA_SEED_CHANCE = 0.5f;


  /**
   * Handles crop breaking (left click) behavior
   * @param breakEvent event data from breaking any block by a player
   */
  @EventHandler (ignoreCancelled = true)
  public void onCropBreak(BlockBreakEvent breakEvent) {
    Block block = breakEvent.getBlock();
    BlockData data = block.getBlockData();
    Player player = breakEvent.getPlayer();

    // Farmland is handled by DropEventHandler
    if (block.getType() == Material.FARMLAND) {
      return;
    }

    if (data instanceof Ageable) {
      CropUtility.CropInfo cropInfo = CropUtility.CropInfo.fromCropBlock(block.getType());
      if (cropInfo == null) { // Crop doesn't exist so default to vanilla behaviour
        return;
      }
      // Disable default drop behaviour
      breakEvent.setDropItems(false);
      // Don't drop seeds if in creative, otherwise drop 1 seed
      if (player.getGameMode() != GameMode.CREATIVE) {
        CropUtility.dropSeed(block, cropInfo);
      }
    }
  }

  /**
   * Handles harvest behaviour
   */
  @EventHandler (ignoreCancelled = true)
  public void onCropHarvest(PlayerInteractEvent interactEvent) {
    Block block = interactEvent.getClickedBlock();
    // Early return for non-crops or non-farmlands. Surprisingly, BlockData: Ageable does not apply to copper blocks
    if (block == null || (!(block.getBlockData() instanceof Ageable) && block.getType() != Material.FARMLAND)) {
      return;
    }

    // Determine if it's left/right click or a trample and handle event
    switch (interactEvent.getAction()) {
      case Action.PHYSICAL -> handleTrample(interactEvent, block); // Always involves a block
      case Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_BLOCK -> useHoe(interactEvent, block);
      // Falls through so we can handle other events should we choose to
    }
  }

  // The bread and butter of the entire plugin. Handles all crop interaction with hoes and bone meal
  private void useHoe(PlayerInteractEvent event, Block crop) {
    Player player = event.getPlayer();
    ItemStack heldItem = player.getInventory().getItemInMainHand();

    // Early returns so that the crop block is actually a crop, and we're using a hoe
    if (!(crop.getBlockData() instanceof Ageable ageable)) {
      return;
    }
    // Bone meal behaviour should not be affected
    if (heldItem.getType() == Material.BONE_MEAL) {
      return;
    }

    // Right-clicking without a hoe does nothing while left-clicking will trigger onCropBreak()
    if (!isHoe(heldItem)) {
        return;
    }

    // Disable default harvest behaviour
    event.setCancelled(true);

    // Must have permission to harvest
    if (!FarmPerms.canHarvest(player)) {
      return;
    }
    // or if crop is not mature yet
    if (ageable.getAge() < ageable.getMaximumAge()) {
      return;
    }

    List<ItemStack> cropDrops = getCropDrops(heldItem, crop.getType());
    damageTool(player, heldItem);
    dropItems(cropDrops, crop);

    // Rare event that instantly grows a crop after harvest (double drops pretty much)
    boolean rareEvent = triggerRareEvent(crop.getWorld(), crop.getLocation());

    // Set max age if rare event triggers, otherwise reset it
    int age = rareEvent ? ageable.getMaximumAge() : 0;
    ageable.setAge(age);
    crop.setBlockData(ageable);
  }

  // Checks to see if we can trigger the rare event, and give visual/audio feedback if we have.
  private boolean triggerRareEvent(World world, Location location) {
    boolean rareEventTriggered = Math.random() < RARE_EVENT_CHANCE;
    if (rareEventTriggered) {
      world.spawnParticle(Particle.HAPPY_VILLAGER, location.add(0.5, 1, 0.5), 5);
      world.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
    return rareEventTriggered;
  }

  private void dropItems(List<ItemStack> drops, Block crop) {
    drops.forEach(drop -> crop.getWorld().dropItemNaturally(crop.getLocation(), drop));
    crop.getWorld().playSound(crop.getLocation(), Sound.ITEM_CROP_PLANT, 0.4f, 1.0f);
  }

  private void handleTrample(PlayerInteractEvent event, Block crop) {
    Player player = event.getPlayer();
    if (crop.getType() == Material.FARMLAND) {
      if (!FarmFix.getTrampleManager().canTrample(player.getUniqueId())) {
        event.setCancelled(true);
      }
    }
  }

  private boolean isHoe(ItemStack heldItem) {
    return heldItem.getType().name().endsWith("_HOE");
  }

  /**
   * Applies damage to a tool and accounts for vanilla-like and unbreakable behaviour.
   *
   * @param player Current player
   * @param tool   Held tool
   */
  private void damageTool(Player player, ItemStack tool) {
    // Early returns for creative mode, unbreakable tool, or no meta to take damage
    ItemMeta meta = tool.getItemMeta();
    if (player.getGameMode() == GameMode.CREATIVE || meta != null && meta.isUnbreakable()) {
      return;
    }
    if (meta == null) {
      return;
    }

    if (meta instanceof Damageable damageable) { // The spigot-safe way
      // Handle Unbreaking the vanilla way
      int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.UNBREAKING);
      float damageChance = (1f / (unbreakingLevel + 1)); // https://minecraft.fandom.com/wiki/Unbreaking
      boolean takeDamage = (Math.random() < damageChance);

      if (takeDamage) {
        int currentDamage = damageable.getDamage();
        damageable.setDamage(currentDamage + 1);
        tool.setItemMeta(meta);
        tryVanillaBreak(tool, damageable, player);
      }
    }

    // tool.damage(1, player); // This is Paper extended api... it would handle unbreaking too :(
  }

  // Breaks tool if max durability is reached
  private void tryVanillaBreak(ItemStack tool, Damageable damageable, Player player) {
    if (damageable.getDamage() >= tool.getType().getMaxDurability()) {
      player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
      player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
    }
  }

  // Handles calculations for how many crops to drop based on crop type
  private List<ItemStack> getCropDrops(ItemStack hoe, Material cropType) {
    // Early return for unsupported crops/blocks
    CropUtility.CropInfo cropDropInfo = CropUtility.CropInfo.fromCropBlock(cropType);
    if (cropDropInfo == null) {
      return List.of();
    }

    // Default behavior without fortune
    Material hoeType = hoe.getType();
    int baseDropAmount = switch (hoeType) {
      case WOODEN_HOE, STONE_HOE -> 1;
      case IRON_HOE, GOLDEN_HOE -> 2;
      case DIAMOND_HOE, NETHERITE_HOE -> 3;
      default -> 0;
    };

    // Fortune behavior in this plugin is [amount to drop] + [fortune level]
    int fortuneLevel = hoe.getEnchantmentLevel(Enchantment.FORTUNE);
    int extraSeed = Math.random() < EXTRA_SEED_CHANCE ? 1 : 0; // Used to add seeds, rather than compare so is converted to int

    Material seed = cropDropInfo.getSeedDrop();
    Material crop = cropDropInfo.getCropDrop();

    List<ItemStack> toDrop = new ArrayList<>();
    int totalCropDrops = baseDropAmount + fortuneLevel; // Kept this in for a sanity check
    toDrop.add(new ItemStack(crop, totalCropDrops));
    if (seed != null) { // Intended behaviour as we don't want to drop extra crops if they don't have seeds
      toDrop.add(new ItemStack(seed, 1 + extraSeed));
    }

    return toDrop;
  }

}
