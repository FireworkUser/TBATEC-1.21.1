package net.firework.tbatec;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;

@Mod(TBATEMod.MODID)
public class TBATEMod {
    public static final String MODID = "tbatec";
    public static final String VERSION = "1.0.0";

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public TBATEMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new ClientEventHandler());

        modContainer.registerConfig(ModConfig.Type.COMMON, RaceConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Register packets
        int messageNumber = 0;
        PACKET_HANDLER.messageBuilder(RaceSelectionPacket.class, messageNumber++)
                .decoder(RaceSelectionPacket::decode)
                .encoder(RaceSelectionPacket::encode)
                .consumerMainThread(RaceSelectionPacket::handle)
                .add();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Client-side initialization
    }
}