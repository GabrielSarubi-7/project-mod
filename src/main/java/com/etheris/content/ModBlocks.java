package com.etheris.content;

import com.etheris.Etheris;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Etheris.MOD_ID);
    private static BlockBehaviour.Properties machine() { return BlockBehaviour.Properties.of().strength(2.5f, 6f).requiresCorrectToolForDrops(); }
    public static final DeferredBlock<Block> ETHER_COLLECTOR = BLOCKS.registerSimpleBlock("ether_collector", machine());
    public static final DeferredBlock<Block> AETHER_FOCUS_BLOCK = BLOCKS.registerSimpleBlock("aether_focus_block", machine());
    public static final DeferredBlock<Block> RUNE_FORGE = BLOCKS.registerSimpleBlock("rune_forge", machine());
    public static final DeferredBlock<Block> AMBER_RESERVOIR = BLOCKS.registerSimpleBlock("amber_reservoir", machine());
    private ModBlocks() {}
}
