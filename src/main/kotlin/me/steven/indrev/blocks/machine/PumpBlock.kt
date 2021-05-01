package me.steven.indrev.blocks.machine

import alexiil.mc.lib.attributes.AttributeList
import alexiil.mc.lib.attributes.fluid.FluidAttributes
import me.steven.indrev.api.machines.Tier
import me.steven.indrev.blockentities.farms.PumpBlockEntity
import me.steven.indrev.config.IRConfig
import me.steven.indrev.registry.MachineRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class PumpBlock(registry: MachineRegistry, settings: Settings) : HorizontalFacingMachineBlock(
    registry,
    settings,
    Tier.MK1,
    IRConfig.machines.pump,
    null) {

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape = when (state?.get(HORIZONTAL_FACING)) {
        Direction.NORTH -> SHAPE_NORTH
        Direction.SOUTH -> SHAPE_SOUTH
        Direction.WEST -> SHAPE_WEST
        Direction.EAST -> SHAPE_EAST
        else -> VoxelShapes.fullCube()
    }

    override fun addAllAttributes(world: World?, pos: BlockPos?, blockState: BlockState?, to: AttributeList<*>) {
        val blockEntity = world?.getBlockEntity(pos) as? PumpBlockEntity ?: return
        val fluidComponent = blockEntity.fluidComponent ?: return
        val dir = to.searchDirection
        val facing = blockState!![HORIZONTAL_FACING]
        if ((facing == dir?.opposite && to.attribute == FluidAttributes.EXTRACTABLE) || (facing == dir&&to.attribute == FluidAttributes.GROUPED_INV))
            to.offer(fluidComponent)
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return this.defaultState.with(HORIZONTAL_FACING, ctx?.playerFacing)
    }

    companion object {
        private val SHAPE_NORTH = createCuboidShape(2.5, 0.0, 0.0, 14.5, 16.0, 14.5)
        private val SHAPE_SOUTH = createCuboidShape(2.5, 0.0, 2.5, 14.5, 16.0, 16.0)
        private val SHAPE_WEST = createCuboidShape(0.0, 0.0, 2.5, 14.5, 16.0, 14.5)
        private val SHAPE_EAST = createCuboidShape(2.5, 0.0, 2.5, 16.0, 16.0, 14.5)
    }
}