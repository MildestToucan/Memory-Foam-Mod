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

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @WrapOperation(method = "startSleeping", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;setBedOccupied(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;Z)V"
    ))
    private void foo(BlockState instance, Level level, BlockPos blockPos, LivingEntity livingEntity, boolean b, Operation<Void> original) {
        original.call(instance, level, blockPos, livingEntity, b);
        if(livingEntity instanceof ServerPlayer player) {
            Block block = instance.getBlock();
            if(block instanceof BedBlock bedBlock) {
                SleepingOperations.registerPlayerAndBed(player, bedBlock);
            }
        }
    }
}
//    @Inject(method = "startSleepInBed", at = @At(value = "HEAD")) //? Target ServerPlayer instead?
//    private void registerPlayersBed(BlockPos pBedPos, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {
//        if(!this.level().isClientSide()) {
//            Block block = this.level().getBlockState(pBedPos).getBlock();
//            SleepingOperations.registerPlayerAndBed((Player)(Object)this, (BedBlock)block);
//        }
//    }
