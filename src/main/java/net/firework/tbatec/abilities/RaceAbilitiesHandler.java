package net.firework.tbatec.abilities;

import net.firework.tbatec.Race;
import net.firework.tbatec.RaceConfig;
import net.firework.tbatec.TBATEMod;
import net.firework.tbatec.data.PlayerRaceData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = TBATEMod.MODID)
public class RaceAbilitiesHandler {

    private static final ResourceLocation ELF_SPEED_ID = ResourceLocation.fromNamespaceAndPath(TBATEMod.MODID, "elf_speed");
    private static final ResourceLocation HUMAN_HEALTH_ID = ResourceLocation.fromNamespaceAndPath(TBATEMod.MODID, "human_health");

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!RaceConfig.ENABLE_RACE_ABILITIES.get()) return;

        Player player = event.getEntity();
        PlayerRaceData raceData = PlayerRaceData.get(player);
        Race race = raceData.getRace();
        if (race != null) {
            applyRaceAbilities(player, race);
        }
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
        PlayerRaceData raceData = PlayerRaceData.get(player);
        Race race = raceData.getRace();
        if (race != null) {
            applyRaceAbilities(player, race);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!RaceConfig.ENABLE_RACE_ABILITIES.get()) return;
        if (!(event.getPlayer() instanceof Player player)) return;

        PlayerRaceData raceData = PlayerRaceData.get(player);
        Race race = raceData.getRace();
        if (race == Race.DWARF) {
            // Dwarf mining bonus - chance for extra drops
            if (player.getRandom().nextFloat() < 0.15f) { // 15% chance
                ItemStack mainHand = player.getMainHandItem();
                if (mainHand.getItem() == Items.IRON_PICKAXE ||
                        mainHand.getItem() == Items.DIAMOND_PICKAXE ||
                        mainHand.getItem() == Items.NETHERITE_PICKAXE) {
                    // Drop extra experience orbs
                    if (player.level() instanceof ServerLevel serverLevel) {
                        BlockState blockState = event.getState();
                        blockState.getBlock().popExperience(serverLevel, event.getPos(), 1);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!RaceConfig.ENABLE_RACE_ABILITIES.get()) return;

        Player player = event.getEntity();
        PlayerRaceData raceData = PlayerRaceData.get(player);
        if (raceData.getRace() == Race.DWARF) {
            event.setNewSpeed(event.getOriginalSpeed() * RaceConfig.DWARF_MINING_SPEED_MULTIPLIER.get().floatValue());
        }
    }

    // Public method for packet handler to use
    public static void applyRaceAbilitiesToPlayer(Player player, Race race) {
        if (RaceConfig.ENABLE_RACE_ABILITIES.get()) {
            applyRaceAbilities(player, race);
        }
    }

    private static void applyRaceAbilities(Player player, Race race) {
        // Remove any existing modifiers first
        removeAllRaceModifiers(player);

        switch (race) {
            case ELF:
                // Apply speed bonus
                AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedAttribute != null && speedAttribute.getModifier(ELF_SPEED_ID) == null) {
                    speedAttribute.addPermanentModifier(
                            new AttributeModifier(ELF_SPEED_ID,
                                    RaceConfig.ELF_SPEED_MULTIPLIER.get() - 1.0,
                                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    );
                }
                break;

            case DWARF:
                // Mining speed is handled in the break speed event
                break;

            case HUMAN:
                // Apply health bonus
                AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttribute != null && healthAttribute.getModifier(HUMAN_HEALTH_ID) == null) {
                    healthAttribute.addPermanentModifier(
                            new AttributeModifier(HUMAN_HEALTH_ID,
                                    RaceConfig.HUMAN_HEALTH_BONUS.get(),
                                    AttributeModifier.Operation.ADD_VALUE)
                    );
                    player.setHealth(player.getHealth()); // Update current health
                }
                break;
        }
    }

    private static void removeAllRaceModifiers(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(ELF_SPEED_ID);
        }

        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute != null) {
            healthAttribute.removeModifier(HUMAN_HEALTH_ID);
        }
    }
}