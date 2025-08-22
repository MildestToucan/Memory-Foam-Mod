package net.gauntrecluse.memory_foam.mixin;


import net.gauntrecluse.memory_foam.SleepingOperations;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//! Currently not registered in the mixins config file. Remove if successfully replaced by PlayerMixin instead.
@Deprecated
@Mixin(value = BedBlock.class)
public abstract class BedBlockMixin extends HorizontalDirectionalBlock implements EntityBlock {
    protected BedBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    //! FIXME: This does not work when a modded BedBlock child class overrides use without calling the super.
    //* TODO: test the replacement ServerPlayerMixin.
    @Inject(method = "use", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;startSleepInBed(Lnet/minecraft/core/BlockPos;)Lcom/mojang/datafixers/util/Either;"
            )
    )
    private void registerPlayersBed(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> cir) {
        SleepingOperations.registerPlayerAndBed(pPlayer, (BedBlock)(Object)this);
    }
}
