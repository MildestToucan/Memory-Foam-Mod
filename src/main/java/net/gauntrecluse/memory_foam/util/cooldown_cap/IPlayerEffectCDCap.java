package net.gauntrecluse.memory_foam.util.cooldown_cap;

import net.gauntrecluse.memory_foam.SleepingOperations;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

/**
 * Capability that stores a {@code long} value in playerdata, intended to be set corresponding to the last time
 * {@link SleepingOperations#applyWakeUpEffects(ServerPlayer)} was used to successfully apply an effect to the player.
 */
@AutoRegisterCapability
public interface IPlayerEffectCDCap {
    long getLastTimeUsed();
    void setLastTimeUsed(long levelGameTime);
}
