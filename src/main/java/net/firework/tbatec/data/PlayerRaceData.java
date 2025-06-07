package net.firework.tbatec.data;

import net.firework.tbatec.Race;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerRaceData implements INBTSerializable<CompoundTag> {
    public static final Capability<PlayerRaceData> PLAYER_RACE_DATA =
            CapabilityManager.get(new CapabilityToken<PlayerRaceData>() {});

    private Race race;
    private boolean hasChanged = false;

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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (race != null) {
            nbt.putString("race", race.name());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("race")) {
            this.race = Race.valueOf(nbt.getString("race"));
        }
    }

    public static PlayerRaceData get(Player player) {
        return player.getCapability(PLAYER_RACE_DATA).orElse(new PlayerRaceData());
    }
}