package com.etheris.world;
import com.etheris.content.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class RuinFeature extends Feature<NoneFeatureConfiguration> {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, "etheris");
    static { FEATURES.register("astral_ruin", RuinFeature::new); }
    public RuinFeature() { super(NoneFeatureConfiguration.CODEC); }
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var level = context.level(); var origin = context.origin();
        for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) {
            var floor = origin.offset(x, -1, z);
            if (!level.getBlockState(floor).isSolidRender(level, floor) || !level.isEmptyBlock(floor.above())) return false;
        }
        for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++)
            level.setBlock(origin.offset(x, -1, z), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
        for (int x : new int[]{-3, 3}) for (int z : new int[]{-3, 3}) {
            int height = 2 + context.random().nextInt(3);
            for (int y = 0; y < height; y++) level.setBlock(origin.offset(x, y, z), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
            level.setBlock(origin.offset(x, height, z), Blocks.AMETHYST_BLOCK.defaultBlockState(), 2);
        }
        level.setBlock(origin, ModBlocks.AETHER_FOCUS_BLOCK.get().defaultBlockState(), 2);
        var chestPos = origin.offset(0, 0, 2);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 2);
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest)
            chest.setLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("etheris", "chests/astral_ruin")), context.random().nextLong());
        return true;
    }
}
