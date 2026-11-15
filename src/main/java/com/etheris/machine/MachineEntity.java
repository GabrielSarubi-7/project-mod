package com.etheris.machine;
import com.etheris.content.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;
public final class MachineEntity extends BlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "etheris");
    public static final Supplier<BlockEntityType<MachineEntity>> TYPE = TYPES.register("machine", () -> BlockEntityType.Builder.of(MachineEntity::new, ModBlocks.ETHER_COLLECTOR.get(), ModBlocks.AETHER_FOCUS_BLOCK.get(), ModBlocks.RUNE_FORGE.get(), ModBlocks.AMBER_RESERVOIR.get()).build(null));
    private int energy;
    private BlockPos link;
    public MachineEntity(BlockPos pos, BlockState state) { super(TYPE.get(), pos, state); }
    public int energy() { return energy; }
    public BlockPos link() { return link; }
    public void linkTo(BlockPos pos) { link = pos.immutable(); setChanged(); }
    public int transferTo(MachineEntity target, int limit) {
        if (target == this || limit <= 0) return 0;
        int moved = target.insert(Math.min(energy, limit));
        consume(moved);
        return moved;
    }
    public int capacity() { return getBlockState().is(ModBlocks.AMBER_RESERVOIR) ? 1000 : 200; }
    public int insert(int amount) { int accepted = Math.min(Math.max(0, amount), capacity() - energy); energy += accepted; if (accepted > 0) setChanged(); return accepted; }
    public boolean consume(int amount) { if (amount < 0 || energy < amount) return false; energy -= amount; setChanged(); return true; }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.saveAdditional(tag, registries); tag.putInt("ether", energy); if (link != null) tag.putLong("link", link.asLong()); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.loadAdditional(tag, registries); energy = Math.clamp(tag.getInt("ether"), 0, capacity()); link = tag.contains("link") ? BlockPos.of(tag.getLong("link")) : null; }
}
