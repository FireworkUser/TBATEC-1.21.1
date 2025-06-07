package net.firework.tbatec.client;

import net.firework.tbatec.client.gui.RaceSelectionScreen;
import net.firework.tbatec.data.PlayerRaceData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = "tbatec", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            // Check if player has selected a race
            PlayerRaceData raceData = PlayerRaceData.get(player);
            if (raceData.getRace() == null) {
                // Show race selection screen
                Minecraft.getInstance().setScreen(new RaceSelectionScreen());
            }
        }
    }
}