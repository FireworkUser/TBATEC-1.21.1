package net.firework.tbatec.abilities;

import net.firework.tbatec.Race;
import net.firework.tbatec.RaceConfig;
import net.firework.tbatec.TBATEMod;
import net.firework.tbatec.data.PlayerRaceData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.UUID;

@EventBusSubscriber(modid = TBATEMod.MODID)
public class RaceAbilitiesHandler {

    private static final UUID ELF_SPEED_UUID = UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635");
    private static final UUID HUMAN_HEALTH_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID DWARF_MINING_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!RaceConfig.ENABLE_RACE_ABILITIES.get()) return;

        Player player = event.getEntity();
        player.getCapability(PlayerRaceData.PLAYER_RACE_DATA).ifPresent(raceData -> {
            Race race = raceData.getRace();
            if (race != null) {
                applyRaceAbilities(player, race);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        removeAllRaceModifiers(player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!RaceConfig.ENABLE_RACE_ABILITIES.get()) return;

        Player player = event.getEntity();
        player.getCapability(PlayerRaceData.PLAYER_RACE_DATA).ifPresent(raceData -> {
            Race race = raceData.getRace();
            if (race != null) {
                applyRaceAbilities(player, race);
            }
        });
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!RaceConfig.ENABLE_RACE_ABILITIES.get()) return;
        if (!(event.getPlayer() instanceof Player player)) return;

        player.getCapability(PlayerRaceData.PLAYER_RACE_DATA).ifPresent(raceData -> {
            Race race = raceData.getRace();
            if (race == Race.DWARF) {
                // Apply mining speed bonus
                event.setCanceled(false); // Ensure event isn't canceled

                // Dwarf mining bonus - chance for extra drops
                if (player.getRandom().nextFloat() < 0.15f) { // 15% chance
                    ItemStack mainHand = player.getMainHandItem();
                    if (mainHand.getItem() == Items.IRON_PICKAXE ||
                            mainHand.getItem() == Items.DIAMOND_PICKAXE ||
                            mainHand.getItem() == Items.NETHERITE_PICKAXE) {
                        // Add bonus experience
                        event.setExpToDrop(event.getExpToDrop() + 1);
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!RaceConfig.ENABLE_RACE_ABILITIES.get()) return;

        Player player = event.getEntity();
        player.getCapability(PlayerRaceData.PLAYER_RACE_DATA).ifPresent(raceData -> {
            if (raceData.getRace() == Race.DWARF) {
                event.setNewSpeed(event.getOriginalSpeed() * RaceConfig.DWARF_MINING_SPEED_MULTIPLIER.get().floatValue());
            }
        });
    }

    private static void applyRaceAbilities(Player player, Race race) {
        // Remove any existing modifiers first
        removeAllRaceModifiers(player);

        switch (race) {
            case ELF:
                // Apply speed bonus
                AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedAttribute != null && speedAttribute.getModifier(ELF_SPEED_UUID) == null) {
                    speedAttribute.addPermanentModifier(
                            new AttributeModifier(ELF_SPEED_UUID, "Elf Speed Bonus",
                                    RaceConfig.ELF_SPEED_MULTIPLIER.get() - 1.0,
                                    AttributeModifier.Operation.MULTIPLY_TOTAL)
                    );
                }
                break;

            case DWARF:
                // Mining speed is handled in the break speed event
                break;

            case HUMAN:
                // Apply health bonus
                AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttribute != null && healthAttribute.getModifier(HUMAN_HEALTH_UUID) == null) {
                    healthAttribute.addPermanentModifier(
                            new AttributeModifier(HUMAN_HEALTH_UUID, "Human Health Bonus",
                                    RaceConfig.HUMAN_HEALTH_BONUS.get(),
                                    AttributeModifier.Operation.ADDITION)
                    );
                    player.setHealth(player.getHealth()); // Update current health
                }
                break;
        }
    }

    private static void removeAllRaceModifiers(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(ELF_SPEED_UUID);
        }

        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.removeModifier(HUMAN_HEALTH_UUID);
        }
    }
}