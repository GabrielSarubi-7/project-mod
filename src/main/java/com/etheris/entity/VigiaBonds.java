package com.etheris.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** World-wide ownership ledger: unloading or changing dimension never frees a bond. */
public final class VigiaBonds extends SavedData {
    private final Map<UUID, UUID> bonds = new HashMap<>();
    public static VigiaBonds get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(new Factory<>(VigiaBonds::new, VigiaBonds::load), "etheris_vigia_bonds");
    }
    public UUID bound(UUID owner) { return bonds.get(owner); }
    public boolean claim(UUID owner, UUID entity) {
        if (bonds.containsKey(owner)) return false;
        bonds.put(owner, entity); setDirty(); return true;
    }
    public void release(UUID owner, UUID entity) {
        if (bonds.remove(owner, entity)) setDirty();
    }
    public static VigiaBonds load(CompoundTag tag, HolderLookup.Provider registries) {
        var result = new VigiaBonds();
        for (var entry : tag.getList("bonds", 10)) {
            var bond = (CompoundTag)entry;
            if (bond.hasUUID("owner") && bond.hasUUID("entity")) result.bonds.put(bond.getUUID("owner"), bond.getUUID("entity"));
        }
        return result;
    }
    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        var list = new ListTag();
        bonds.forEach((owner, entity) -> { var bond = new CompoundTag(); bond.putUUID("owner", owner); bond.putUUID("entity", entity); list.add(bond); });
        tag.put("bonds", list); return tag;
    }
}
