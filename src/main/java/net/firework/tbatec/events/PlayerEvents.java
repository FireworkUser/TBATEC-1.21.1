package net.firework.tbatec.events;

import net.firework.tbatec.TBATEMod;
import net.firework.tbatec.data.PlayerRaceData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = TBATEMod.MODID)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            PlayerRaceData oldData = PlayerRaceData.get(event.getOriginal());
            PlayerRaceData newData = PlayerRaceData.get(event.getEntity());

            if (oldData.getRace() != null) {
                newData.setRace(oldData.getRace());
            }
        }
    }
}