package net.firework.tbatec.client;

import net.firework.tbatec.TBATEMod;
import net.firework.tbatec.client.gui.RaceSelectionScreen;
import net.firework.tbatec.data.PlayerRaceData;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    private static boolean shouldShowRaceSelection = false;
    private static int tickDelay = 0;

    @SubscribeEvent
    public void onClientPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        // Schedule race selection screen to open after a delay
        shouldShowRaceSelection = true;
        tickDelay = 20; // 1 second delay
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && shouldShowRaceSelection && tickDelay > 0) {
            tickDelay--;
            if (tickDelay == 0) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null && mc.screen == null) {
                    PlayerRaceData raceData = PlayerRaceData.get(mc.player);
                    if (raceData.getRace() == null) {
                        mc.setScreen(new RaceSelectionScreen());
                    }
                    shouldShowRaceSelection = false;
                }
            }
        }
    }
}