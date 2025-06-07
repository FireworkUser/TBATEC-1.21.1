package net.firework.tbatec;

import net.firework.tbatec.network.RaceSelectionPacket;
import net.firework.tbatec.data.PlayerRaceData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.firework.tbatec.events.PlayerEvents;
import net.firework.tbatec.abilities.RaceAbilitiesHandler;

@Mod(TBATEMod.MODID)
public class TBATEMod {
    public static final String MODID = "tbatec";
    public static final String VERSION = "1.0.0";

    public TBATEMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register data attachments
        PlayerRaceData.ATTACHMENT_TYPES.register(modEventBus);

        // Register common setup
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);

        // Register game event bus handlers
        NeoForge.EVENT_BUS.register(PlayerEvents.class);
        NeoForge.EVENT_BUS.register(RaceAbilitiesHandler.class);

        // Only register client setup on client side
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::clientSetup);
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, RaceConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                RaceSelectionPacket.TYPE,
                RaceSelectionPacket.STREAM_CODEC,
                RaceSelectionPacket::handle
        );
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Register client event handler
        NeoForge.EVENT_BUS.register(new net.firework.tbatec.client.ClientEventHandler());
    }
}