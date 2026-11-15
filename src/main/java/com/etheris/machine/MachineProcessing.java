package com.etheris.machine;

import com.etheris.content.ModBlocks;
import com.etheris.content.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

/** Small deterministic recipe catalogue; only successful completion commits input and energy. */
public final class MachineProcessing {
    public record Operation(ItemStack output, int cost, int inputCount) {
        public Operation(ItemStack output, int cost) { this(output, cost, 1); }
    }
    public static Operation find(BlockState state, ItemStack input) {
        if (input.isEmpty()) return null;
        if (state.is(ModBlocks.ETHER_COLLECTOR) && input.is(Items.AMETHYST_SHARD)) return new Operation(new ItemStack(ModItems.ETHERIUM_FRAGMENT.get()), 20);
        if (state.is(ModBlocks.RUNE_FORGE) && input.is(Items.COPPER_INGOT)) return new Operation(new ItemStack(ModItems.RUNE.get()), 20);
        if (state.is(ModBlocks.PRISMATIC_CRUSHER)) {
            if (input.is(Items.RAW_IRON)) return new Operation(new ItemStack(ModItems.IRON_DUST.get(), 2), 30);
            if (input.is(Items.AMETHYST_SHARD)) return new Operation(new ItemStack(ModItems.CRYSTAL_DUST.get(), 2), 20);
        }
        if (state.is(ModBlocks.CRYSTAL_REFINERY)) {
            if (input.is(ModItems.IRON_DUST)) return new Operation(new ItemStack(Items.IRON_INGOT), 20);
            if (input.is(ModItems.CRYSTAL_DUST)) return new Operation(new ItemStack(ModItems.STABLE_CRYSTAL.get()), 40);
        }
        if (state.is(ModBlocks.TRANSMUTATION_CHAMBER)) {
            if (input.is(ModItems.STABLE_CRYSTAL)) return new Operation(new ItemStack(Items.ENDER_PEARL), 160, 4);
            if (input.is(Items.QUARTZ)) return new Operation(new ItemStack(Items.AMETHYST_SHARD), 80, 8);
        }
        return null;
    }
    private MachineProcessing() {}
}
