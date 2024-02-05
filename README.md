## Crafting Table II
This is an attempt at porting the Crafting Table II mod from [perky](https://github.com/perky/CraftingTableII) to modern Minecraft versions using Fabric.

This is my first minecraft mod, so this is a learning experience for me.

The usage of Kotlin here is purely from my own preference and disliking of Java's verboseness. It could be removed later if the additional dependency of kotlin is too much.

The first version I'm targeting is 1.20.1

### Requirements

- [x] The block's inventory render to screen
- [ ] The block entity has the original model, animation and sound
- [x] The block's inventory has functional a scrollbar
- [ ] The scrollbar function moves the recipe list up and down
- [x] Have access to the current client's recipe book craftable recipe list
- [x] Send the recipe list to the ScreenHandler and display them in the inventory GUI
- [x] Update the recipe list when the player's inventory change
- [ ] Recipe item name & description and ingredient list on hover
- [x] Figure out how to handle items with multiple recipes (the craft is going to pick the first one)
- [x] Craft the item and subtract the required items from the player's inventory on slot click