package net.gauntrecluse.memory_foam;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BedBlock;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class containing all operations and data relating to the mod's changes to sleeping. <br>
 * Logger and usages are commented out for releases, but still there for debugging in development.
 */
public class SleepingOperations {

//    private static final Logger LOGGER = MemoryFoam.LOGGER;

    public static Map<UUID, BedBlock> playerBedRegistry = new HashMap<>();

    public static Map<Class<? extends BedBlock>, MobEffect> bedToEffectRegistry = new HashMap<>();

    public static void registerPlayerAndBed(Player player, BedBlock bedBlock) {
        playerBedRegistry.putIfAbsent(player.getUUID(), bedBlock);
//        LOGGER.info("Added player and bed block to registry!");
    }


    public static void applyWakeUpEffects(Player player) {
        UUID playerId = player.getUUID();
        if(playerBedRegistry.containsKey(playerId)) {
//            LOGGER.info("Found {} in playerBedRegistry", playerId);
//            LOGGER.info("Should correspond to player {}", player.getName());
            Class<? extends BedBlock> bedType = playerBedRegistry.get(playerId).getClass();
            if(bedToEffectRegistry.containsKey(bedType)) {
//                LOGGER.info("Found {} in bedToEffectRegistry, trying to apply effects!", bedType);
                MobEffectInstance effectInstance = new MobEffectInstance(bedToEffectRegistry.get(bedType), 300);
                player.addEffect(effectInstance);
//                LOGGER.info("Tried to add effect {} to {}", bedToEffectRegistry.get(bedType), player.getName());
            }
        }
        playerBedRegistry.remove(playerId);
    }

}