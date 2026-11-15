package com.etheris.machine;
import com.etheris.content.ModBlocks;
import com.etheris.content.ModItems;
import com.etheris.gameplay.EtherData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
public final class MachineBlock extends Block implements EntityBlock {
    public MachineBlock(Properties properties) { super(properties); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new MachineEntity(pos, state); }
    @Override protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving) {
        super.onPlace(state, level, pos, old, moving);
        if (state.is(ModBlocks.ETHER_COLLECTOR)) level.scheduleTick(pos, this, 100);
    }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof MachineEntity machine) {
            if (state.is(ModBlocks.ETHER_COLLECTOR) && level.canSeeSky(pos.above())) machine.insert(level.isNight() ? 4 : 2);
            machine.distribute();
            machine.processOne();
            machine.wake();
        }
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (stack.is(ModItems.LINK_WAND)) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        if (!level.mayInteract(player, pos)) return ItemInteractionResult.FAIL;
        if (!(level.getBlockEntity(pos) instanceof MachineEntity machine)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (state.is(ModBlocks.ETHER_COLLECTOR) && stack.is(Items.AMETHYST_SHARD) && machine.consume(20)) {
            stack.shrink(1); give(player, new ItemStack(ModItems.ETHERIUM_FRAGMENT.get()));
        } else if (!state.is(ModBlocks.ETHER_COLLECTOR) && stack.is(ModItems.ETHERIUM_FRAGMENT) && machine.capacity() - machine.energy() >= 20) {
            stack.shrink(1); machine.insert(20);
        } else if (state.is(ModBlocks.RUNE_FORGE) && stack.is(Items.COPPER_INGOT) && machine.consume(20)) {
            stack.shrink(1); give(player, new ItemStack(ModItems.RUNE.get()));
        } else if (state.is(ModBlocks.AMBER_RESERVOIR) && stack.is(Items.AMETHYST_SHARD) && machine.consume(20)) {
            stack.shrink(1); give(player, new ItemStack(ModItems.ETHERIUM_FRAGMENT.get()));
        } else if (state.is(ModBlocks.AETHER_FOCUS_BLOCK) && (stack.is(ModItems.RUNE) || stack.is(ModItems.STABLE_CRYSTAL))) {
            int unlocked = player.getPersistentData().getInt("etheris_unlocked");
            boolean valid = unlocked < 4 ? stack.is(ModItems.RUNE) : stack.is(ModItems.STABLE_CRYSTAL);
            if (!valid) player.displayClientMessage(Component.literal(unlocked < 4 ? "Esta descoberta requer uma Runa de Princípio." : "Sintonização: refine Pó de Cristal e use um Cristal Estável para avançar."), false);
            if (unlocked < 7 && valid && machine.consume(40)) { stack.shrink(1); player.getPersistentData().putInt("etheris_unlocked", unlocked + 1); player.displayClientMessage(Component.literal("Aprendido: " + com.etheris.gameplay.Spells.NAMES[unlocked + 1] + ". Agache e use o foco para selecionar."), false); }
        } else if (state.is(ModBlocks.AETHER_FOCUS_BLOCK) && stack.is(ModItems.AETHER_FOCUS)) {
            int amount = Math.min(20, EtherData.max() - EtherData.get(player));
            if (amount > 0 && machine.consume(amount)) EtherData.add(player, amount);
        }
        if ((state.is(ModBlocks.PRISMATIC_CRUSHER) || state.is(ModBlocks.CRYSTAL_REFINERY)) && !stack.isEmpty()) {
            var remainder = machine.inventory.insertItem(0, stack.copy(), false);
            stack.setCount(remainder.getCount());
        }
        player.displayClientMessage(Component.literal("Éter: " + machine.energy() + "/" + machine.capacity() + " | Entrada: " + machine.inventory.getStackInSlot(0).getCount() + " | Saída: " + machine.inventory.getStackInSlot(1).getCount()), true);
        return ItemInteractionResult.SUCCESS;
    }
    private static void give(Player player, ItemStack result) { if (!player.getInventory().add(result)) player.drop(result, false); }
    @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.mayInteract(player, pos) && level.getBlockEntity(pos) instanceof MachineEntity machine) {
            var output = machine.inventory.extractItem(1, 64, false);
            if (!output.isEmpty()) give(player, output);
            player.displayClientMessage(Component.literal("Éter " + machine.energy() + "/" + machine.capacity() + " | Use funil para inserir reagentes e retirar produtos."), true);
        }
        return net.minecraft.world.InteractionResult.SUCCESS;
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof MachineEntity machine && !level.isClientSide)
            for (int slot = 0; slot < 2; slot++) net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), machine.inventory.getStackInSlot(slot).copy());
        super.onRemove(state, level, pos, next, moving);
    }
}
