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
    public static final Supplier<BlockEntityType<MachineEntity>> TYPE = TYPES.register("machine", () -> BlockEntityType.Builder.of(MachineEntity::new, ModBlocks.ETHER_COLLECTOR.get(), ModBlocks.AETHER_FOCUS_BLOCK.get(), ModBlocks.RUNE_FORGE.get(), ModBlocks.AMBER_RESERVOIR.get(), ModBlocks.PRISMATIC_CRUSHER.get(), ModBlocks.CRYSTAL_REFINERY.get(), ModBlocks.DISTRIBUTION_NODE.get(), ModBlocks.TRANSMUTATION_CHAMBER.get()).build(null));
    private int energy;
    private boolean transmutationCompleted;
    public boolean transmutationCompleted() { return transmutationCompleted; }
    public void grantDiscovery(net.minecraft.world.entity.player.Player player) {
        if (isChamber() && transmutationCompleted && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            var advancement = serverPlayer.server.getAdvancements().get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("etheris", "prova_transmutacao"));
            if (advancement != null) serverPlayer.getAdvancements().award(advancement, "operated");
        }
    }
    private long lastStructureCheck = Long.MIN_VALUE;
    private boolean structureReady;
    public boolean isChamber() { return getBlockState().is(ModBlocks.TRANSMUTATION_CHAMBER); }
    private boolean structureReady(boolean refresh) {
        if (!isChamber()) return true;
        if (level == null) return false;
        long now = level.getGameTime();
        if (refresh || lastStructureCheck == Long.MIN_VALUE || now - lastStructureCheck >= 20) {
            structureReady = ChamberStructure.formed(level, worldPosition);
            lastStructureCheck = now;
        }
        return structureReady;
    }
    private boolean paused;
    public boolean paused() { return paused; }
    public boolean automationBlocked() { return paused || (level != null && level.hasNeighborSignal(worldPosition)); }
    public void setPaused(boolean value) { paused = value; setChanged(); wake(); }
    public void clearLinks() { link = null; branches.clear(); nextBranch = 0; setChanged(); }
    public int status() {
        if (paused) return 1;
        if (level != null && level.hasNeighborSignal(worldPosition)) return 2;
        if (!structureReady(false)) return 7;
        if (getBlockState().is(ModBlocks.ETHER_COLLECTOR) && level != null && !level.canSeeSky(worldPosition.above())) return 3;
        var recipe = MachineProcessing.find(getBlockState(), inventory.getStackInSlot(0));
        if (recipe == null) return 0;
        if (inventory.getStackInSlot(0).getCount() < recipe.inputCount()) return 8;
        if (energy < recipe.cost()) return 4;
        return outputFits(recipe) ? 6 : 5;
    }
    private BlockPos link;
    private final java.util.List<BlockPos> branches = new java.util.ArrayList<>();
    private int nextBranch;
    public final net.neoforged.neoforge.items.ItemStackHandler inventory = new net.neoforged.neoforge.items.ItemStackHandler(2) {
        @Override public boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) { return slot == 0 && MachineProcessing.find(getBlockState(), stack) != null; }
        @Override public net.minecraft.world.item.ItemStack insertItem(int slot, net.minecraft.world.item.ItemStack stack, boolean simulate) { return slot == 0 ? super.insertItem(slot, stack, simulate) : stack; }
        @Override public net.minecraft.world.item.ItemStack extractItem(int slot, int amount, boolean simulate) { return slot == 1 ? super.extractItem(slot, amount, simulate) : net.minecraft.world.item.ItemStack.EMPTY; }
        @Override protected void onContentsChanged(int slot) { setChanged(); wake(); }
    };
    public void wake() {
        if (level != null && !level.isClientSide && !isRemoved() && !automationBlocked()
            && (getBlockState().is(ModBlocks.ETHER_COLLECTOR) || link != null
                || (isChamber() && !inventory.getStackInSlot(0).isEmpty()) || canProcess()))
            level.scheduleTick(worldPosition, getBlockState().getBlock(), 100);
    }
    @Override public void onLoad() { super.onLoad(); wake(); }
    public boolean canProcess() {
        if (automationBlocked()) return false;
        var recipe = MachineProcessing.find(getBlockState(), inventory.getStackInSlot(0));
        if (recipe == null || inventory.getStackInSlot(0).getCount() < recipe.inputCount() || energy < recipe.cost() || !structureReady(true)) return false;
        return outputFits(recipe);
    }
    private boolean outputFits(MachineProcessing.Operation recipe) {
        var output = inventory.getStackInSlot(1);
        return output.isEmpty() || (net.minecraft.world.item.ItemStack.isSameItemSameComponents(output, recipe.output()) && output.getCount() + recipe.output().getCount() <= output.getMaxStackSize());
    }
    public boolean processOne() {
        if (!canProcess()) return false;
        var recipe = MachineProcessing.find(getBlockState(), inventory.getStackInSlot(0));
        consume(recipe.cost());
        var input = inventory.getStackInSlot(0).copy(); input.shrink(recipe.inputCount());
        var output = inventory.getStackInSlot(1).copy();
        if (output.isEmpty()) output = recipe.output().copy(); else output.grow(recipe.output().getCount());
        inventory.setStackInSlot(0, input); inventory.setStackInSlot(1, output);
        if (isChamber()) { transmutationCompleted = true; setChanged(); }
        return true;
    }
    public MachineEntity(BlockPos pos, BlockState state) { super(TYPE.get(), pos, state); }
    public int energy() { return energy; }
    public BlockPos link() { return link; }
    public boolean linkTo(BlockPos pos) {
        if (pos.equals(worldPosition) || worldPosition.distSqr(pos) > 64) return false;
        if (!getBlockState().is(ModBlocks.DISTRIBUTION_NODE)) { link = pos.immutable(); setChanged(); wake(); return true; }
        if (pos.equals(link)) { link = branches.isEmpty() ? null : branches.removeFirst(); }
        else if (branches.contains(pos)) branches.remove(pos);
        else if (link == null) link = pos.immutable();
        else if (branches.size() < 3) branches.add(pos.immutable());
        else return false;
        setChanged(); wake(); return true;
    }
    public void distribute() {
        if (level == null || level.isClientSide || link == null || automationBlocked()) return;
        var targets = new java.util.ArrayList<BlockPos>(); targets.add(link); targets.addAll(branches);
        int budget = 8;
        for (int i = 0; i < targets.size() && budget > 0; i++) {
            var target = targets.get(Math.floorMod(nextBranch + i, targets.size()));
            if (worldPosition.distSqr(target) <= 64 && level.hasChunkAt(target) && level.getBlockEntity(target) instanceof MachineEntity machine) budget -= transferTo(machine, budget);
        }
        nextBranch = (nextBranch + 1) % targets.size(); setChanged();
    }
    public int transferTo(MachineEntity target, int limit) {
        if (target == this || limit <= 0) return 0;
        int moved = target.insert(Math.min(energy, limit));
        consume(moved);
        return moved;
    }
    public int capacity() { return getBlockState().is(ModBlocks.AMBER_RESERVOIR) ? 1000 : 200; }
    public int insert(int amount) { int accepted = Math.min(Math.max(0, amount), capacity() - energy); energy += accepted; if (accepted > 0) { setChanged(); wake(); } return accepted; }
    public boolean consume(int amount) { if (amount < 0 || energy < amount) return false; energy -= amount; setChanged(); return true; }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("ether", energy);
        tag.putBoolean("paused", paused);
        tag.putBoolean("transmutationCompleted", transmutationCompleted);
        tag.put("inventory", inventory.serializeNBT(registries));
        if (link != null) tag.putLong("link", link.asLong());
        tag.putLongArray("branches", branches.stream().mapToLong(BlockPos::asLong).toArray());
        tag.putInt("nextBranch", nextBranch);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy = Math.clamp(tag.getInt("ether"), 0, capacity());
        paused = tag.getBoolean("paused");
        transmutationCompleted = tag.getBoolean("transmutationCompleted");
        link = tag.contains("link") ? BlockPos.of(tag.getLong("link")) : null;
        if (link != null && (link.equals(worldPosition) || worldPosition.distSqr(link) > 64)) link = null;
        if (tag.contains("inventory")) inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        branches.clear();
        if (getBlockState().is(ModBlocks.DISTRIBUTION_NODE)) {
            for (long packed : tag.getLongArray("branches")) {
                var pos = BlockPos.of(packed);
                if (branches.size() < 3 && !pos.equals(link) && !pos.equals(worldPosition)
                    && worldPosition.distSqr(pos) <= 64 && !branches.contains(pos)) branches.add(pos);
            }
        }
        nextBranch = Math.floorMod(tag.getInt("nextBranch"), branches.size() + 1);
    }
}
