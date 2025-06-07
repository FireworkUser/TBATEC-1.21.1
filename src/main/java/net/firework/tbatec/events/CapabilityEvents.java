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

@EventBusSubscriber(modid = TBATEMod.MODID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(TBATEMod.MODID, "player_race_data"),
                    new PlayerRaceDataProvider());
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerRaceData.class);
    }
}