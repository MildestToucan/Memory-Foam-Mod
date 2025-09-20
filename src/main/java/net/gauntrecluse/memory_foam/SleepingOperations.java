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
import org.slf4j.Logger;

import java.util.*; //? TODO: probably replace with single class imports later.

/**
 * Class containing all operations and data relating to the mod's changes to sleeping,
 * for the sake of being subjectively tidy.
 */
public class SleepingOperations {
    private static final Logger LOGGER = MemoryFoam.LOGGER;


    public static Map<UUID, BedBlock> playerBedRegistry = new HashMap<>();

    public static SetMultimap<String, MobEffectInstance> bedToEffectsRegistry = HashMultimap.create();

    public static int effectCooldown;

    //We register the player's UUID because we have no interest in the other values at that point.
    public static void registerPlayerAndBed(Player player, BedBlock bedBlock) {
        playerBedRegistry.put(player.getUUID(), bedBlock);
    }


    /**
     * Shorthand method for getting a mob effect via ForgeRegistries and {@link ResourceLocation#tryParse(String)}
     */
    public static MobEffect getEffectFromResourceLoc(String effectResourceLoc) {
        return ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(effectResourceLoc));
    }
    /**
     * Maps the {@code bedType} parameter parsed from config to a constructed {@link MobEffectInstance} in {@code bedToEffectRegistry}. <br>
     * Parameters should be supplied by the config. Refer to it for instructions on parameter formatting.
     */
    public static void addToBedEffectRegistry(String bedType, String effectType, int effectLength, int effectAmplifier) {
        LOGGER.warn("addToBedEffectRegistry called with parameters: {}, {}, {}, {}", bedType, effectType, effectLength, effectAmplifier);
        MobEffect typeAsMobEffect = getEffectFromResourceLoc(effectType);
        MobEffectInstance effectInstance = new MobEffectInstance(typeAsMobEffect, (effectLength * 20), effectAmplifier);
        bedToEffectsRegistry.put(bedType, effectInstance);
    }

    /**
     * Applies effects upon waking up based on config values and the bed type the player slept in.
     */
    public static void applyWakeupEffects(ServerPlayer player) {
        LOGGER.info("applyWakeupEffects called.");
        long gameTime = player.level().getGameTime();

        LazyOptional<IPlayerEffectCDCap> lazyOptional = player.getCapability(CDCapProvider.CD_CAP_CAPABILITY);
        if(lazyOptional.isPresent()) {
            IPlayerEffectCDCap capInstance = lazyOptional.orElse(null); //isPresent works as a null check.

            long lastTimeSet = capInstance.getLastTimeUsed();



            if(gameTime - lastTimeSet < effectCooldown) {
                LOGGER.warn("The effect is still on cooldown, cancelling applyWakeupEffects.");
                LOGGER.warn("Remaining cooldown time in ticks {}", (effectCooldown - (gameTime - lastTimeSet)));
                return;
            }
            LOGGER.warn("Setting new cooldown.");
            capInstance.setLastTimeUsed(gameTime);
        } else {
            LOGGER.error("lazyOptional's cap instance is null or invalidated. Disregarding cooldown operations.");
        }
        LOGGER.info("Starting post cooldown wakeup operations.");
        LOGGER.warn("bedToEffectsRegistry: {}", bedToEffectsRegistry.keys());

        //We give the option to map effects to all beds.
        //At that point, we also do not need to know if the player is registered to that type of bed.
        if(bedToEffectsRegistry.containsKey("all")) {
            LOGGER.info("bed-effect config entry with all keyword detected. Applying effects associated to all beds.");
            Set<MobEffectInstance> allBedsEffects = new HashSet<>(bedToEffectsRegistry.get("all"));
            for(MobEffectInstance current : allBedsEffects) {
                player.addEffect(new MobEffectInstance(current));
            }
        }
        UUID playerId = player.getUUID();

        if(!playerBedRegistry.containsKey(playerId)) {
            LOGGER.warn("Can't find UUID of player {} in playerBedRegistry!", player.getName());
            LOGGER.warn("Cancelling applyWakeUpEffects call");
            return;
        }

        //! relying on getSimpleName might be brittle. Thorough testing is needed.
        String bedTypeSleptIn = playerBedRegistry.get(playerId).getClass().getSimpleName().toLowerCase();
        LOGGER.info("bed type slept in of the player identified as {}", bedTypeSleptIn);


        if(!bedToEffectsRegistry.containsKey(bedTypeSleptIn)) {
            LOGGER.warn("bedTypeSleptIn {} not found in bed-effects registry.", bedTypeSleptIn);
            LOGGER.warn("Cancelling applyWakeUpEffects call");
            return;
        }

        Set<MobEffectInstance> effectSet = new HashSet<>(bedToEffectsRegistry.get(bedTypeSleptIn));

        for(MobEffectInstance current : effectSet) {
            LOGGER.info("Applying applicable effects to player.");
            player.addEffect(new MobEffectInstance(current));
        }
        LOGGER.warn("end of applyWakeupEffects call with no early return.");
    }
}