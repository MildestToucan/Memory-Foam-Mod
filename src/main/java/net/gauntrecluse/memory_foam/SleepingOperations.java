package net.gauntrecluse.memory_foam;

import earth.terrarium.handcrafted.common.blocks.FancyBedBlock;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BedBlock;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SleepingOperations {

    private static final Logger LOGGER = MemoryFoam.LOGGER;

    public static Map<UUID, BedBlock> playerBedRegistry = new HashMap<>();

    public static Map<Class<? extends BedBlock>, MobEffect> bedToEffectRegistry = new HashMap<>();

    public static void registerPlayerAndBed(Player player, BedBlock bedBlock) {
        playerBedRegistry.putIfAbsent(player.getUUID(), bedBlock);
        LOGGER.info("Added player and bed block to registry!");
    }


    public static void applyWakeUpEffects(Player player) {

    }

}
