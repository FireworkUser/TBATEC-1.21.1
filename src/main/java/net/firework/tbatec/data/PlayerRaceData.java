package net.firework.tbatec.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.firework.tbatec.Race;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.firework.tbatec.TBATEMod;

import java.util.function.Supplier;

public class PlayerRaceData {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TBATEMod.MODID);

    private static final Codec<PlayerRaceData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("race", "").forGetter(data ->
                            data.race != null ? data.race.name() : ""
                    )
            ).apply(instance, PlayerRaceData::new)
    );

    public static final Supplier<AttachmentType<PlayerRaceData>> PLAYER_RACE_DATA =
            ATTACHMENT_TYPES.register("player_race_data", () ->
                    AttachmentType.builder(() -> new PlayerRaceData())
                            .serialize(CODEC)
                            .build()
            );

    private Race race;
    private boolean hasChanged = false;

    public PlayerRaceData() {
        this.race = null;
    }

    private PlayerRaceData(String raceName) {
        if (!raceName.isEmpty()) {
            try {
                this.race = Race.valueOf(raceName);
            } catch (IllegalArgumentException e) {
                this.race = null;
            }
        }
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
        this.hasChanged = true;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public void setChanged() {
        this.hasChanged = true;
    }

    public static PlayerRaceData get(Player player) {
        return player.getData(PLAYER_RACE_DATA);
    }
}