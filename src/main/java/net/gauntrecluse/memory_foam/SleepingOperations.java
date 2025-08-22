package net.gauntrecluse.memory_foam;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BedBlock;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Class containing all operations and data relating to the mod's changes to sleeping, for the sake of being tidy. <br>
 * Logger and usages are commented out for releases, but still there for debugging in development.
 */
public class SleepingOperations {


    public static Map<UUID, BedBlock> playerBedRegistry = new HashMap<>();

    public static SetMultimap<String, MobEffectInstance> bedToEffectsRegistry = HashMultimap.create();

    public static void registerPlayerAndBed(Player player, BedBlock bedBlock) {
        playerBedRegistry.put(player.getUUID(), bedBlock);
    }


    /**
     * @param effectResourceLoc The name of the effect under the ResourceLocation, including the modid if using a mod. Such that
     *                          {@code modid:modeffect}. Defaults to {@code minecraft:effect} if no valid namespace is provided.
     *                          For example, {@code absorption} would be defaulted as {@code minecraft:absorption}
     * @return the {@code MobEffect} object based on the input-based ResourceLocation.
     * @see ResourceLocation#tryParse(String)
     */
    public static MobEffect getEffectFromName(String effectResourceLoc) {
        ResourceLocation key = ResourceLocation.tryParse(effectResourceLoc);
        return ForgeRegistries.MOB_EFFECTS.getValue(key);
    }
    /**
     * Uses {@link #getEffectFromName(String)} and parameters to create a {@code MobEffectInstance} to put into the
     * {@code bedToEffectsRegistry}.
     * @param bedType The type of Bed based on the bed's class name in source code. E.g. just put "FancyBedBlock" for class {@code FancyBedBlock}.
     * @param effectType See {@link #getEffectFromName(String)} for instructions.
     * @param effectLength Length of the effect in SECONDS, assuming a tick speed of 20. Defaults to 0.
     * @param effectAmplifier How much to amplify the effects by. Defaults to 0.
     */
    public static void addToBedEffectRegistry(String bedType, String effectType, int effectLength, int effectAmplifier) {
        MobEffect typeAsMobEffect = getEffectFromName(effectType);
        MobEffectInstance effectInstance = new MobEffectInstance(typeAsMobEffect, (effectLength * 20), effectAmplifier);
        bedToEffectsRegistry.put(bedType.toLowerCase(), effectInstance);
    }


    public static void applyWakeupEffects(Player player) {
        UUID playerId = player.getUUID();

        if(!playerBedRegistry.containsKey(playerId)) {
            return;
        }
        String bedTypeSleptIn = playerBedRegistry.get(playerId).getClass().getSimpleName().toLowerCase();
        if(!bedToEffectsRegistry.containsKey(bedTypeSleptIn)) {
            return;
        }

        final Set<MobEffectInstance> effectSet = bedToEffectsRegistry.get(bedTypeSleptIn);

        for(MobEffectInstance current : effectSet) {
            MobEffectInstance copy = new MobEffectInstance(current);
            player.addEffect(copy);
        }
    }
}