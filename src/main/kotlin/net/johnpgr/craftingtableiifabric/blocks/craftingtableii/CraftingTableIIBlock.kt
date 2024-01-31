package net.johnpgr.craftingtableiifabric.blocks.craftingtableii

import com.google.common.collect.Lists
import net.johnpgr.craftingtableiifabric.utils.BlockScreenHandlerFactory
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

class CraftingTableIIBlock(settings: Settings) : BlockWithEntity(settings) {

    override fun appendProperties(stateManager: StateManager.Builder<Block?, BlockState?>) {
        stateManager.add(Properties.HORIZONTAL_FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return defaultState.with(
            Properties.HORIZONTAL_FACING,
            ctx.horizontalPlayerFacing
        )
    }

    override fun rotate(
        state: BlockState,
        rotation: BlockRotation
    ): BlockState {
        return state.with(
            Properties.HORIZONTAL_FACING,
            rotation.rotate(state[Properties.HORIZONTAL_FACING])
        )
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState? {
        return state.rotate(mirror.getRotation(state[Properties.HORIZONTAL_FACING]))
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun hasSidedTransparency(state: BlockState?): Boolean {
        return false
    }

    //TODO: Calculate correct collision shape
    override fun getCollisionShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return super.getCollisionShape(state, world, pos, context)
    }

    override fun createBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity {
        return CraftingTableIIBlockEntity(this, pos, state)
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        player.openHandledScreen(
            BlockScreenHandlerFactory(
                this,
                pos
            )
        )
        return ActionResult.SUCCESS
    }

    private fun getCraftableItemsList(
        player: ClientPlayerEntity,
        registryManager: DynamicRegistryManager,
    ): List<ItemStack> {
        val recipeMatcher = RecipeMatcher()
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
            result.getResults(true).map { it.getOutput(registryManager) }
        }

        return items
    }

    override fun hasComparatorOutput(state: BlockState): Boolean {
        return false
    }
}
