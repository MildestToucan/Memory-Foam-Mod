package net.gauntrecluse.memory_foam.config;

import net.gauntrecluse.memory_foam.MemoryFoam;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class MemoryFoamConfig {

    public static final ForgeConfigSpec SERVER_CONFIG_SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENTRIES;
    public static final ForgeConfigSpec.ConfigValue<Integer> EFFECT_COOLDOWN;

    static
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        ENTRIES = builder
                .comment("List of bed to effect entries, following the format: \"bedType|effectType|effectLength|effectAmplifier\"")
                .comment("Such that:\nbedType is the bed block's class name (e.g. FancyBedBlock, not case-sensitive),\neffectType is the ResourceLocation of the effect (e.g. minecraft:absorption or modid:modeffect, case-sensitive) and\neffectLength is in seconds.\neffectAmplifier works the same as in a command.")
                .comment("example config: bed_to_effect_registry[\"BedBlock|minecraft:absorption|10|3\"]\nthe example would give absorption for ten seconds with an amplifier of three after waking up from a vanilla bed.\nDifferent entries can be put in separate strings separated by a comma. You may have more than one entry for the same bed block.")
                .comment("Using the word \"all\" in the bedType field will make the following effect apply regardless of the kind of bed the player slept in.")
                .comment("Separate entries should still be put in the same brackets, but in separate strings, aka separate quotation marks. If either the effectLength or the effectAmplifier field is empty, the mod will default it to 0")
                .defineListAllowEmpty("bed_to_effect_registry",
                        List.of(""),
                        o -> o instanceof String
                );

        EFFECT_COOLDOWN = builder
                .comment("Cooldown for the bed effects, meant to prevent spam usage. Value is in seconds.")
                .comment("Will treat any negative value or excessively high value as 0")
                .comment("Default is 300 seconds, AKA 5 minutes")
                .define("effect_cooldown", 300);

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

    public static int parseCooldownTime() {
        if(EFFECT_COOLDOWN.get() < 0) return 0;
        return EFFECT_COOLDOWN.get();
    }
}
