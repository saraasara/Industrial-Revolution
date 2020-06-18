package me.steven.indrev.utils

import me.steven.indrev.blockentities.MachineBlockEntity
import me.steven.indrev.mixin.AccessorEnergyHandler
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import team.reborn.energy.Energy
import team.reborn.energy.EnergyHolder
import team.reborn.energy.EnergySide

@Suppress("CAST_NEVER_SUCCEEDS")
object EnergyMovement {
    fun spreadNeighbors(sourceBlockEntity: BlockEntity, pos: BlockPos, vararg directions: Direction) {
        val world = sourceBlockEntity.world
        if (sourceBlockEntity !is EnergyHolder) return
        val sourceHandler = Energy.of(sourceBlockEntity)
        val targets = directions
            .map { direction ->
                if (sourceBlockEntity is MachineBlockEntity && sourceBlockEntity.lastInputFrom == direction) return@map null
                else if (sourceBlockEntity.getMaxOutput(EnergySide.fromMinecraft(direction)) > 0) {
                    val targetPos = pos.offset(direction)
                    val target = world?.getBlockEntity(targetPos)
                    if (target != null && Energy.valid(target)) {
                        val targetHandler = Energy.of(target).side(direction.opposite)
                        if (targetHandler.energy < targetHandler.maxStored)
                            return@map targetHandler
                    }
                }
                null
            }
            .filterNotNull()
        val sum = targets.sumByDouble { targetHandler ->
            (targetHandler.maxStored - targetHandler.energy).coerceAtMost(targetHandler.maxInput)
        }
        targets.forEach { targetHandler ->
            val accessor = targetHandler as AccessorEnergyHandler
            val direction = accessor.side
            val target = accessor.holder
            sourceHandler.side(direction)
            val targetMaxInput = targetHandler.maxInput
            val amount = (targetMaxInput / sum) * sourceHandler.maxOutput
            if (amount > 0) {
                if (target is MachineBlockEntity)
                    target.lastInputFrom = Direction.byName(direction.toString())
                sourceHandler.into(targetHandler).move(amount)
            }
        }
    }
}