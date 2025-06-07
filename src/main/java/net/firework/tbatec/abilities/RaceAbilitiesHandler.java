package net.firework.tbatec.abilities;

import net.firework.tbatec.Race;
import net.firework.tbatec.RaceConfig;
import net.firework.tbatec.data.PlayerRaceData;
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

@EventBusSubscriber(modid = "tbatec")
public class RaceAbilitiesHandler {

    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635");
    private static final UUID MINING_MODIFIER_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

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

                    // Add bonus experience
                    event.setExpToDrop(event.getExpToDrop() + 1);
                }
            }
        }
    }

    private static void applyRaceAbilities(Player player, Race race) {
        switch (race) {
            case ELF:
                // Apply speed bonus
                player.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(
                        new AttributeModifier(SPEED_MODIFIER_UUID, "Elf Speed Bonus",
                                RaceConfig.ELF_SPEED_MULTIPLIER.get() - 1.0,
                                AttributeModifier.Operation.MULTIPLY_TOTAL)
                );
                break;

            case DWARF:
                // Apply mining speed bonus (handled in block break event)
                break;

            case HUMAN:
                // Apply health bonus
                player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(
                        new AttributeModifier(UUID.randomUUID(), "Human Health Bonus",
                                RaceConfig.HUMAN_HEALTH_BONUS.get(),
                                AttributeModifier.Operation.ADDITION)
                );
                break;
        }
    }
}