package net.gauntrecluse.memory_foam.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.gauntrecluse.memory_foam.SleepingOperations;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Debug(export = false)
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements WorldGenLevel {
    protected ServerLevelMixin(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }


    @WrapOperation(method = "wakeUpAllPlayers",
            at = @At(value = "INVOKE",
            target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private void addWakeupOps(List<ServerPlayer> instance, Consumer consumer, Operation<Void> original) {
        original.call(instance, consumer);
        instance.forEach((player) -> {
            SleepingOperations.applyWakeupEffects(player);
            SleepingOperations.playerBedRegistry.remove(player.getUUID());
        });
    }
}


//! Crashes in prod due to something to do with not being able to find the lambda to target.
//* To avoid lambda related troubleshooting, this was replaced with wrapping the forEach instead of the Consumer lambda.
//    @WrapOperation(method = "lambda$wakeUpAllPlayers$7", at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/server/level/ServerPlayer;stopSleepInBed(ZZ)V"))
//    private static void addWakeupOps(ServerPlayer instance, boolean pWakeImmediately, boolean pUpdateLevelForSleepingPlayers, Operation<Void> original) {
//        original.call(instance, pWakeImmediately, pUpdateLevelForSleepingPlayers);
//        SleepingOperations.applyWakeupEffects(instance);
//        SleepingOperations.playerBedRegistry.remove(instance.getUUID());
//    }