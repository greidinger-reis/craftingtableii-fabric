package net.johnpgr.craftingtableiifabric.blocks

import com.google.common.collect.Lists
import net.johnpgr.craftingtableiifabric.blocks.entities.CraftingTableIIBlockEntity
import net.johnpgr.craftingtableiifabric.screens.handlers.CraftingTableIIScreenHandler
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CraftingTableIIBlock(settings: Settings) : BlockWithEntity(settings) {

    override fun createBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity {
        return CraftingTableIIBlockEntity(pos, state)
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (!world.isClient) {
            player.openHandledScreen(
                state.createScreenHandlerFactory(
                    world,
                    pos
                )
            )
            return ActionResult.CONSUME
        }
        val recipeMatcher = RecipeMatcher()
        val blockEntity =
            world.getBlockEntity(pos) as CraftingTableIIBlockEntity
        val clientPlayerEntity = player as ClientPlayerEntity

        sendCraftableItemList(world, clientPlayerEntity, blockEntity, recipeMatcher)

        return ActionResult.SUCCESS
    }

    private fun sendCraftableItemList(
        world: World,
        player: ClientPlayerEntity,
        blockEntity: CraftingTableIIBlockEntity,
        recipeMatcher: RecipeMatcher
    ) {
        player.inventory.populateRecipeFinder(recipeMatcher)
        val list =
            player.recipeBook.getResultsForGroup(RecipeBookGroup.CRAFTING_SEARCH)
        val list2 = Lists.newArrayList(list)

        list2.forEach {
            it.computeCraftables(
                recipeMatcher,
                9,
                9,
                player.recipeBook
            )
        }
        list2.removeIf { !it.isInitialized || !it.hasFittingRecipes() || !it.hasCraftableRecipes() }

        val items = list2.flatMap { result ->
            result.getResults(true).map { it.getOutput(world.registryManager) }
        }

        blockEntity.setStacks(items)
    }

    override fun hasComparatorOutput(state: BlockState): Boolean {
        return true
    }

    override fun getComparatorOutput(
        state: BlockState,
        world: World,
        pos: BlockPos
    ): Int {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos))
    }
}
