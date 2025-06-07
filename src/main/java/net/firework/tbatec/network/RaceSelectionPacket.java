package net.firework.tbatec.network;

import net.firework.tbatec.Race;
import net.firework.tbatec.TBATEMod;
import net.firework.tbatec.data.PlayerRaceData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RaceSelectionPacket {
    private final Race race;

    public RaceSelectionPacket(Race race) {
        this.race = race;
    }

    public static void encode(RaceSelectionPacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.race);
    }

    public static RaceSelectionPacket decode(FriendlyByteBuf buf) {
        return new RaceSelectionPacket(buf.readEnum(Race.class));
    }

    public static void handle(RaceSelectionPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && packet.race != null) {
                player.getCapability(PlayerRaceData.PLAYER_RACE_DATA).ifPresent(raceData -> {
                    // Only allow setting race if not already set
                    if (raceData.getRace() == null) {
                        raceData.setRace(packet.race);
                        raceData.setChanged();

                        // Send confirmation message
                        player.sendSystemMessage(Component.literal("You have chosen to be a " + packet.race.getDisplayName() + "!"));

                        // Apply race abilities immediately
                        net.firework.tbatec.abilities.RaceAbilitiesHandler.onPlayerLogin(
                                new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent(player)
                        );
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}