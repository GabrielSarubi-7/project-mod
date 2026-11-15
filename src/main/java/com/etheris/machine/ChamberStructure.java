package com.etheris.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** Fixed 3x3x2 assembly. Never searches for blocks or loads a missing chunk. */
public final class ChamberStructure {
    private static final net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> CONDUCTORS = net.minecraft.tags.TagKey.create(
        net.minecraft.core.registries.Registries.BLOCK, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("etheris", "chamber_conductors"));
    public static boolean formed(Level level, BlockPos center) {
        if (level == null) return false;
        for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) {
            // The center below the controller is free for an output hopper.
            if (x == 0 && z == 0) continue;
            var base = center.offset(x, -1, z);
            if (!level.hasChunkAt(base) || !level.getBlockState(base).is(Blocks.POLISHED_DEEPSLATE)) return false;
            var frame = center.offset(x, 0, z);
            if (!level.hasChunkAt(frame)) return false;
            var state = level.getBlockState(frame);
            if (x != 0 && z != 0 ? !state.is(Blocks.AMETHYST_BLOCK) : !state.is(CONDUCTORS)) return false;
        }
        return true;
    }
    private ChamberStructure() {}
}
