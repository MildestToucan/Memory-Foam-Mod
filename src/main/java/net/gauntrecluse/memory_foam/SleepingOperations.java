package net.gauntrecluse.memory_foam;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import net.gauntrecluse.memory_foam.util.cooldown_cap.CDCapProvider;
import net.gauntrecluse.memory_foam.util.cooldown_cap.IPlayerEffectCDCap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BedBlock;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Class containing all operations and data relating to the mod's changes to sleeping,
 * for the sake of being subjectively tidy.
 */
public class SleepingOperations {


    public static Map<UUID, BedBlock> playerBedRegistry = new HashMap<>();

    public static SetMultimap<String, MobEffectInstance> bedToEffectsRegistry = HashMultimap.create();

    public static int effectCooldown;

    //We register the player's UUID because we have no interest in the other values at that point.
    public static void registerPlayerAndBed(Player player, BedBlock bedBlock) {
        playerBedRegistry.put(player.getUUID(), bedBlock);
    }


    public static void deregisterPlayerAndBed(UUID id) {
        playerBedRegistry.remove(id);
    }



    /**
     * Maps the {@code bedType} parameter parsed from config to a constructed {@link MobEffectInstance} in {@code bedToEffectRegistry}. <br>
     * Parameters should be supplied by the config. Refer to it for instructions on parameter formatting.
     */
    public static void addToBedEffectRegistry(String bedType, String effectType, int effectLength, int effectAmplifier, boolean hideParticles) {
        MobEffect typeAsMobEffect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(effectType));
        MobEffectInstance effectInstance = new MobEffectInstance(typeAsMobEffect, effectLength * 20, effectAmplifier, false, !hideParticles);
        bedToEffectsRegistry.put(bedType, effectInstance);
    }


    public static void applyWakeUpEffects(ServerPlayer player) {
        long gameTime = player.serverLevel().getGameTime();
        LazyOptional<IPlayerEffectCDCap> lazyOptional = player.getCapability(CDCapProvider.CD_CAP_CAPABILITY);
        if(lazyOptional.isPresent()) {
            IPlayerEffectCDCap capInstance = lazyOptional.orElse(null);

            long lastTimeUsed = capInstance.getLastTimeUsed();

            if(lastTimeUsed != 0 && gameTime - lastTimeUsed < effectCooldown) {
                return;
            }

            Set<MobEffectInstance> effects = new HashSet<>();

            if(bedToEffectsRegistry.containsKey("all")) {
                effects.addAll(bedToEffectsRegistry.get("all"));
            }

            UUID playerID = player.getUUID();

            if(playerBedRegistry.containsKey(playerID)) {
                String bedSleptIn = playerBedRegistry.get(playerID).getClass().getSimpleName().toLowerCase();

                if(bedToEffectsRegistry.containsKey(bedSleptIn)) {
                    effects.addAll(bedToEffectsRegistry.get(bedSleptIn));
                }
            }

            if(effects.isEmpty()) {
                return;
            }

            for(MobEffectInstance current : effects) {
                player.addEffect(new MobEffectInstance(current));
            }
            capInstance.setLastTimeUsed(gameTime);
        }
    }
}