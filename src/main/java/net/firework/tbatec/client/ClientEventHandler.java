package net.firework.tbatec.client;

import net.firework.tbatec.client.gui.RaceSelectionScreen;
import net.firework.tbatec.data.PlayerRaceData;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    private static boolean hasCheckedForRace = false;

    @SubscribeEvent
    public void onClientPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        // Reset the flag when logging in
        hasCheckedForRace = false;
    }

    @SubscribeEvent
    public void onScreenOpen(ScreenEvent.Opening event) {
        if (!hasCheckedForRace && event.getScreen() == null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                hasCheckedForRace = true;

                // Schedule the race check for next tick
                NeoForge.EVENT_BUS.register(new Object() {
                    @SubscribeEvent
                    public void onRenderTick(net.neoforged.neoforge.client.event.RenderFrameEvent.Pre event) {
                        NeoForge.EVENT_BUS.unregister(this);

                        if (mc.player != null && mc.screen == null) {
                            PlayerRaceData raceData = PlayerRaceData.get(mc.player);
                            if (raceData.getRace() == null) {
                                mc.setScreen(new RaceSelectionScreen());
                            }
                        }
                    }
                });
            }
        }
    }
}