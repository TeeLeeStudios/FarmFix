# FarmFix
Paper/Spigot plugin for Minecraft 1.21.1 that changes how crops are interacted with.

## Drop Behaviour
 
WOODEN_HOE, STONE_HOE -> 1 Crop

IRON_HOE -> 2 Crops

DIAMOND_HOE, NETHERITE_HOE -> 3 Crops


Seed drops are 1 seed + 50% chance to drop 1 additional one.

Disables Piston interaction with FARMLAND, and any CROPS

## Fortune Behaviour

Base crop drops as seen above + level of fortune on the hoe

## Other Enchantment Behaviour

Unchanged from Vanilla

## Permissions
### Uses FarmPerms.java to manage permission behaviour

groups.farmfix.mod - Can be changed to work with groups, acts as 'farmfix.mod'

farmfix.mod - Gives the ability to toggle other players trample/force set other players trample

farmfix.break - Allows breaking of crops/farmland (You cannot break crops with a hoe in hand)

farmfix.trample - Allows self trample toggling behaviour

farmfix.harvest - Allows harvesting with hoes.

## Commands
Help command for this plugin is /ff

Players can toggle their own trample using /trample

Mods can toggle other user's trample using /trample [playername] [opt: forced]

The [opt: forced] flag accepts "true" and "1" as true, all other values as false
