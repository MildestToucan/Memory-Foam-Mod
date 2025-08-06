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

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MemoryFoam.MODID)
public class MemoryFoam {

    public static final String MODID = "memory_foam";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MemoryFoam() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.addListener(this::playerWakeUpEventHandler);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup firing");
        SleepingOperations.bedToEffectRegistry.put(FancyBedBlock.class, MobEffects.NIGHT_VISION);
    }


    private void playerWakeUpEventHandler(PlayerWakeUpEvent event) {
        LOGGER.warn("PlayerWakeUpEvent fired and handler got it!");
        SleepingOperations.applyWakeUpEffects(event.getEntity());
    }
}
