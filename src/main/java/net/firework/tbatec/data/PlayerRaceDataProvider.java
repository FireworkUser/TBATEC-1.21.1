package net.firework.tbatec.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilitySerializable;
import net.neoforged.neoforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerRaceDataProvider implements ICapabilitySerializable<CompoundTag> {
    private final PlayerRaceData raceData = new PlayerRaceData();
    private final LazyOptional<PlayerRaceData> optional = LazyOptional.of(() -> raceData);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return PlayerRaceData.PLAYER_RACE_DATA.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return raceData.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        raceData.deserializeNBT(nbt);
    }
}