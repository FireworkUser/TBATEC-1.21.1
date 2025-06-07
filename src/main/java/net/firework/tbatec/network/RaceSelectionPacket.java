package net.firework.tbatec.network;

import net.firework.tbatec.Race;
import net.firework.tbatec.data.PlayerRaceData;
import net.minecraft.network.FriendlyByteBuf;
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
            if (player != null) {
                PlayerRaceData raceData = PlayerRaceData.get(player);
                raceData.setRace(packet.race);
                raceData.setChanged();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}