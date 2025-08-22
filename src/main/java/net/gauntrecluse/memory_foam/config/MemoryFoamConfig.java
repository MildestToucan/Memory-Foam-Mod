package net.gauntrecluse.memory_foam.config;

import net.gauntrecluse.memory_foam.MemoryFoam;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class MemoryFoamConfig {

    public static final ForgeConfigSpec SERVER_CONFIG_SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENTRIES;

    static
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        ENTRIES = builder
                .comment("Config version: 1") //TODO: after release, update this with every change to the config.
                .comment("List of bed to effect entries, following the format: \"bedType|effectType|effectLength|effectAmplifier\"")
                .comment("Such that:\nbedType is the bed block's class name (e.g. FancyBedBlock, not case-sensitive),\n effectType is the ResourceLocation of the effect (e.g. minecraft:absorption or modid:modeffect, case-sensitive) and\neffectLength is in seconds.\neffectAmplifier works the same as in a command.")
                .comment("example config: bed_to_effect_registry[\"BedBlock|minecraft:absorption|10|3\"]\nthe example would give absorption for ten seconds with an amplifier of three after waking up from a vanilla bed.\nDifferent entries can be put in separate strings separated by a comma. You may have more than one entry for the same bed block.")
                .comment("Separate entries should still be put in the same brackets, but in separate strings, aka separate quotation marks. If either the effectLength or the effectAmplifier field is empty, the mod will default it to 0")
                .worldRestart()
                .defineListAllowEmpty("bed_to_effect_registry",
                        List.of(""),
                        o -> o instanceof String
                );
        SERVER_CONFIG_SPEC = builder.build();
    }


    public static List<BedToEffectConfigEntry> getParsedEntries() {
        List<BedToEffectConfigEntry> result = new ArrayList<>();
        for(String raw : ENTRIES.get()) {
            try {
                result.add(BedToEffectConfigEntry.fromString(raw));
            } catch(IllegalArgumentException e) {
                MemoryFoam.LOGGER.error("Invalid config entry! {} ({})", raw, e.getMessage());
            }
        }
        return result;
    }
}
