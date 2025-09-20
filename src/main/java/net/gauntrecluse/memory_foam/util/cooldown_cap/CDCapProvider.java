package net.gauntrecluse.memory_foam.util.cooldown_cap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CDCapProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<IPlayerEffectCDCap> CD_CAP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});


    private final IPlayerEffectCDCap instance;
    private final LazyOptional<IPlayerEffectCDCap> lazyOptional;


    public CDCapProvider(ServerPlayer player) {
        this.instance = new CooldownCapImpl();
        this.lazyOptional = LazyOptional.of(() -> this.instance);
    }



    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CD_CAP_CAPABILITY) {
            return LazyOptional.of(() -> instance).cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("last_time_used", instance.getLastTimeUsed());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.setLastTimeUsed(nbt.getLong("last_time_used"));
    }


}