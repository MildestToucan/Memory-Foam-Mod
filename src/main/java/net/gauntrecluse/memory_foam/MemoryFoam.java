package net.gauntrecluse.memory_foam;

import com.mojang.logging.LogUtils;
import earth.terrarium.handcrafted.common.blocks.FancyBedBlock;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Main mod file for Memory Foam, in this branch specifically: <br>
 * Modloader: Forge 47.4.0 <br>
 * Minecraft version: 1.20.1 <br>
 * Note: Will likely forget to update these numbers accurately. See {@code build.gradle} for internal versions. <br>
 * If the versions are off from what they're supposed to be, open an issue.
 * @version 0.0.1-POC
 * @author GauntRecluse
 */
@Mod(MemoryFoam.MODID)
public class MemoryFoam {

    public static final String MODID = "memory_foam";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MemoryFoam() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus(); //TODO: Figure out whether it matters that .get() is deprecated for removal.

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.addListener(this::playerWakeUpEventHandler);
        LOGGER.warn("Memory Foam is only a proof of concept at the moment, here be dragons and lack of features!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Registering bed-effect associations...");
        SleepingOperations.bedToEffectRegistry.put(FancyBedBlock.class, MobEffects.REGENERATION);
    }


    private void playerWakeUpEventHandler(PlayerWakeUpEvent event) {
        LOGGER.warn("PlayerWakeUpEvent fired and handler got it!");
        SleepingOperations.applyWakeUpEffects(event.getEntity());
    }
}
