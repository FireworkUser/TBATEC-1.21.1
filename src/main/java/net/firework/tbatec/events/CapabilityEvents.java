package net.firework.tbatec.events;

import net.firework.tbatec.TBATEMod;
import net.firework.tbatec.data.PlayerRaceData;
import net.firework.tbatec.data.PlayerRaceDataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = TBATEMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CapabilityEvents {

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(
                PlayerRaceData.PLAYER_RACE_DATA,
                Player.class,
                (player, context) -> new PlayerRaceData()
        );
    }

    @EventBusSubscriber(modid = TBATEMod.MODID, bus = EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                event.addCapability(
                        ResourceLocation.fromNamespaceAndPath(TBATEMod.MODID, "player_race_data"),
                        new PlayerRaceDataProvider()
                );
            }
        }

        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
                event.getOriginal().getCapability(PlayerRaceData.PLAYER_RACE_DATA).ifPresent(oldStore -> {
                    event.getEntity().getCapability(PlayerRaceData.PLAYER_RACE_DATA).ifPresent(newStore -> {
                        newStore.setRace(oldStore.getRace());
                    });
                });
            }
        }
    }
}