package net.firework.tbatec;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RaceConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // Race abilities configuration
    public static final ModConfigSpec.BooleanValue ENABLE_RACE_ABILITIES;
    public static final ModConfigSpec.DoubleValue ELF_SPEED_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue DWARF_MINING_SPEED_MULTIPLIER;
    public static final ModConfigSpec.IntValue HUMAN_HEALTH_BONUS;

    static {
        BUILDER.push("Race Abilities");

        ENABLE_RACE_ABILITIES = BUILDER
                .comment("Enable special abilities for races")
                .define("enableRaceAbilities", true);

        ELF_SPEED_MULTIPLIER = BUILDER
                .comment("Speed multiplier for elves")
                .defineInRange("elfSpeedMultiplier", 1.2, 1.0, 2.0);

        DWARF_MINING_SPEED_MULTIPLIER = BUILDER
                .comment("Mining speed multiplier for dwarves")
                .defineInRange("dwarfMiningSpeedMultiplier", 1.5, 1.0, 3.0);

        HUMAN_HEALTH_BONUS = BUILDER
                .comment("Additional health points for humans")
                .defineInRange("humanHealthBonus", 4, 0, 20);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}