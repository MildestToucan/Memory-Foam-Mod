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


    /*
     Arguably this should be replaced by detecting when players are waking up after a "deep" sleep. Meaning,
     detecting when the player's been sleeping long enough that, if enough players are sleeping to allow a skip,
     the server would typically wake up all players. This would allow to have our effects apply not just when enough
     players go to sleep to skip the night.
    */
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