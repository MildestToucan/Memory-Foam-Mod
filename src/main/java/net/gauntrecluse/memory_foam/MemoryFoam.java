package net.gauntrecluse.memory_foam;

import com.mojang.logging.LogUtils;
import net.gauntrecluse.memory_foam.config.BedToEffectConfigEntry;
import net.gauntrecluse.memory_foam.config.MemoryFoamConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.List;

/**
 * Main mod file for Memory Foam, version specs: <br>
 * Modloader: Forge 47.4.0 <br>
 * Minecraft version: 1.20.1 <br>
 * Note: Will likely forget to update these numbers accurately. See {@code build.gradle} for internal versions. <br>
 * If the versions are off from what they're supposed to be, open an issue.
 * @version 0.1.0+1.20.1FORGE
 * @author GauntRecluse
 */
@Mod(MemoryFoam.MODID)
public class MemoryFoam {

    public static final String MODID = "memory_foam";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MemoryFoam() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus(); //#get being deprecated does not matter for 1.20.1

        modEventBus.addListener(this::commonSetup); //TODO: maybe unnecessary

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, MemoryFoamConfig.SERVER_CONFIG_SPEC, "memoryfoamconfig.toml");
        modEventBus.addListener(this::onModConfigEvent);
        LOGGER.warn("Memory Foam is only a proof of concept at the moment, here be dragons and lack of features!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void onModConfigEvent(final ModConfigEvent event) {
        List<BedToEffectConfigEntry> entries = MemoryFoamConfig.getParsedEntries();
        SleepingOperations.bedToEffectsRegistry.clear(); //Clear the registry before reconstructing it.
        for(BedToEffectConfigEntry current : entries) {
            SleepingOperations.addToBedEffectRegistry(current.bedType().toLowerCase(), current.effectType(), current.effectLength(), current.effectAmplifier());
        }
    }
}