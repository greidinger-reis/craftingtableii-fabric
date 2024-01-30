## Crafting Table II
This is an attempt at porting the Crafting Table II mod from [perky](https://github.com/perky/CraftingTableII) to modern Minecraft versions using Fabric.

This is my first minecraft mod, so this is a learning experience for me.

The usage of Kotlin here is purely from my own preference and disliking of Java's verboseness. It could be removed later if the additional dependency of kotlin is too much.

The first version I'm targeting is 1.20.1

### Requirements

- [x] The block's inventory render to screen
- [ ] The block's inventory has a dynamic size and grows as the recipe list grows
- [ ] The block's inventory has functional a scroll wheel 
- [x] Have access to the current client's recipe book craftable recipe list
- [ ] Send the recipe list to the ScreenHandler and display them in the inventory GUI
- [ ] Add the item's description overlay on hover
- [ ] Craft the item and subtract the required items from the player's inventory on slot click

### Current Status

Im currently able to access the player's recipe book and get the list of craftable recipes.
However I'm not being able to send the list to the ScreenHandler and display them in the inventory GUI.
Add readme

<video src="./assets/ct2_wip.mp4" controls></video>
