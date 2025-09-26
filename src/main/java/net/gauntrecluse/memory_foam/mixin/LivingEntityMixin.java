package net.gauntrecluse.memory_foam.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.gauntrecluse.memory_foam.SleepingOperations;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    /**
     * Intends to systemically map the player to the kind of bed they're sleeping in. This will work for modded beds as
     * long as they extend {@link BedBlock} and call the {@link LivingEntity#startSleeping(BlockPos)} method to make the player sleep. <br>
     * Maybe should be replaced by injecting into ServerPlayer's method instead.
     */
    @WrapOperation(method = "startSleeping", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;setBedOccupied(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;Z)V"
    ))
    private void registerPlayerAndBed(BlockState instance, Level level, BlockPos blockPos, LivingEntity livingEntity, boolean b, Operation<Void> original) {
        original.call(instance, level, blockPos, livingEntity, b);
        if(livingEntity instanceof ServerPlayer player) {
            Block block = instance.getBlock();
            if(block instanceof BedBlock bedBlock) {
                SleepingOperations.registerPlayerAndBed(player, bedBlock);
            }
        }
    }
}