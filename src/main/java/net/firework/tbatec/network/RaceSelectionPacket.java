package net.firework.tbatec.network;

import net.firework.tbatec.Race;
import net.firework.tbatec.TBATEMod;
import net.firework.tbatec.data.PlayerRaceData;
import net.firework.tbatec.abilities.RaceAbilitiesHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;

public record RaceSelectionPacket(Race race) implements CustomPacketPayload {
    public static final Type<RaceSelectionPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(TBATEMod.MODID, "race_selection")
    );

    public static final StreamCodec<FriendlyByteBuf, RaceSelectionPacket> STREAM_CODEC = StreamCodec.of(
            RaceSelectionPacket::encode,
            RaceSelectionPacket::decode
    );

    private static void encode(FriendlyByteBuf buf, RaceSelectionPacket packet) {
        buf.writeEnum(packet.race);
    }

    private static RaceSelectionPacket decode(FriendlyByteBuf buf) {
        return new RaceSelectionPacket(buf.readEnum(Race.class));
    }

    public static void handle(RaceSelectionPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player && packet.race != null) {
                PlayerRaceData raceData = PlayerRaceData.get(player);

                // Only allow setting race if not already set
                if (raceData.getRace() == null) {
                    raceData.setRace(packet.race);
                    raceData.setChanged();

                    // Send confirmation message
                    player.sendSystemMessage(Component.literal("You have chosen to be a " + packet.race.getDisplayName() + "!"));

                    // Apply race abilities
                    RaceAbilitiesHandler.applyRaceAbilitiesToPlayer(player, packet.race);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendToServer(Race race) {
        PacketDistributor.sendToServer(new RaceSelectionPacket(race));
    }
}