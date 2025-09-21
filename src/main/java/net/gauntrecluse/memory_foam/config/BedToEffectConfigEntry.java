package net.gauntrecluse.memory_foam.config;

import org.jetbrains.annotations.NotNull;

/**
 * Custom data type used to have a custom and more flexible formatting for config purposes.
 * @param bedType a String that corresponds to a case agnostic version of the given bed's class.
 *                Implemented by working with {@link Class#getSimpleName()}
 * @param effectType a String that corresponds to the ResourceLocation declaration of a {@link net.minecraft.world.effect.MobEffect}.
 *                   Defaults to namespace {@code "minecraft:"} if none provided. Case-sensitive.
 * @param effectLength an integer that corresponds to the amount of seconds the effect should last for assuming server runs at 20 Ticks per second.
 * @param effectAmplifier an integer that corresponds to the number value you'd use in a {@code /effect give} command's amplifier field.
 */
public record BedToEffectConfigEntry(String bedType, String effectType, int effectLength, int effectAmplifier) {

    /**
     * Parses a string based on the formatting {@code "bedType|effectType|effectLength|effectAmplifier"}
     */
    public static BedToEffectConfigEntry fromString(String entry) throws IllegalArgumentException {
        String[] parts = entry.split("\\|");

        if(parts.length < 2 || parts.length > 4) { //We don't support less than two or more than 4 elements per entry.
            throw new IllegalArgumentException("Invalid BedtoEffectConfigEntry instance length in config " + entry);
        }

        String bedType = parts[0];
        String effectType = parts[1];
        int effectLength = (parts.length >= 3) ? Integer.parseInt(parts[2]) : 0; //Default effectLength and effectAmplifier to 0 if not present.
        int effectAmplifier = (parts.length == 4) ? Integer.parseInt(parts[3]) : 0;
        if(effectLength < 0) effectLength = 0;
        if(effectAmplifier < 0 || effectAmplifier > 255) effectAmplifier = 0;
        return new BedToEffectConfigEntry(bedType, effectType, effectLength, effectAmplifier);
    }

    @Override
    public @NotNull String toString() {
        return bedType + "|" + effectType + "|" + effectLength + "|" + effectAmplifier;
    }
}
