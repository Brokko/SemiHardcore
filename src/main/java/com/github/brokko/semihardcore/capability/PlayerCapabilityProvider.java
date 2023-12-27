package com.github.brokko.semihardcore.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<PlayerData> PLAYER_DATA = CapabilityManager.get(new CapabilityToken<>() { });
    private final LazyOptional<PlayerData> optional = LazyOptional.of(this::createPlayerData);

    private PlayerData playerData;

    public PlayerCapabilityProvider() {

    }

    private PlayerData createPlayerData() {
        if(playerData == null)
            playerData = new PlayerData();

        return playerData;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_DATA)
            return optional.cast();

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createPlayerData().saveNBTData(tag); // FEHLER
        tag.putBoolean("sync", true);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerData().loadNBTData(nbt);
    }
}
