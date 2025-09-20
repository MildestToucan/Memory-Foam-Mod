package net.gauntrecluse.memory_foam;

import com.mojang.logging.LogUtils;
import net.gauntrecluse.memory_foam.config.BedToEffectConfigEntry;
import net.gauntrecluse.memory_foam.config.MemoryFoamConfig;
import net.gauntrecluse.memory_foam.util.cooldown_cap.CDCapProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.List;

@Mod(MemoryFoam.MODID)
public class MemoryFoam {

    public static final String MODID = "memory_foam";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MemoryFoam() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup); //? TODO: remove if common setup is never actually used.

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, MemoryFoam::onAttachCapabilitiesEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerCloneEvent);

        modEventBus.addListener(this::onModConfigEvent);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, MemoryFoamConfig.SERVER_CONFIG_SPEC, "memoryfoamconfig.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    public void onModConfigEvent(ModConfigEvent event) {
        List<BedToEffectConfigEntry> entries = MemoryFoamConfig.getParsedEntries();
        SleepingOperations.bedToEffectsRegistry.clear();
        for(BedToEffectConfigEntry current : entries) {
            SleepingOperations.addToBedEffectRegistry(current.bedType().toLowerCase(),
                    current.effectType(),
                    current.effectLength(),
                    current.effectAmplifier()
            );
        }
        SleepingOperations.effectCooldown = MemoryFoamConfig.parseCooldownTime() * 20;
    }

    private static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if(!(event.getObject() instanceof ServerPlayer player)) return;

        event.addCapability(
                ResourceLocation.fromNamespaceAndPath(MODID, "cooldown_capability"),
                new CDCapProvider(player)
        );
    }

    public void onPlayerCloneEvent(PlayerEvent.Clone event) {
        if(!event.isWasDeath()) return;

        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(CDCapProvider.CD_CAP_CAPABILITY).ifPresent(
                oldCap -> event.getEntity().getCapability(CDCapProvider.CD_CAP_CAPABILITY).ifPresent(
                        newCap -> newCap.setLastTimeUsed(oldCap.getLastTimeUsed())
                )
        );
    }
}