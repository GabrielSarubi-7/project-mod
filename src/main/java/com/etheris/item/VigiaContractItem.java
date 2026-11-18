package com.etheris.item;

import com.etheris.entity.ModEntities;
import com.etheris.entity.VigiaBonds;
import com.etheris.gameplay.EtherData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public final class VigiaContractItem extends Item {
    public VigiaContractItem(Properties properties) { super(properties); }
    @Override public void appendHoverText(net.minecraft.world.item.ItemStack stack, TooltipContext context, java.util.List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.etheris.vigia_contract"));
        tooltip.add(Component.translatable("tooltip.etheris.vigia_release"));
    }
    @Override public net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> use(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player user, net.minecraft.world.InteractionHand hand) {
        var stack = user.getItemInHand(hand);
        if (!user.isShiftKeyDown() || user.isSpectator()) return net.minecraft.world.InteractionResultHolder.pass(stack);
        if (user instanceof ServerPlayer player) {
            var bonds = VigiaBonds.get(player.serverLevel()); var bound = bonds.bound(player.getUUID());
            if (bound != null) bonds.release(player.getUUID(), bound);
            player.displayClientMessage(Component.translatable("message.etheris.vigia.released"), false);
        }
        return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    private InteractionResult fail(ServerPlayer player, String key) { player.displayClientMessage(Component.translatable("message.etheris.vigia." + key), false); return InteractionResult.FAIL; }
    @Override public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) return InteractionResult.SUCCESS;
        if (!(context.getPlayer() instanceof ServerPlayer player) || player.isSpectator()) return InteractionResult.FAIL;
        var level = player.serverLevel(); var floor = context.getClickedPos(); var pos = floor.above();
        if (context.getClickedFace() != Direction.UP || !level.mayInteract(player, floor) || !level.mayInteract(player, pos)
            || !player.mayUseItemAt(pos, Direction.UP, context.getItemInHand()) || !level.getBlockState(floor).isFaceSturdy(level, floor, Direction.UP)) return fail(player, "space");
        var proof = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath("etheris", "prova_transmutacao"));
        if (!player.isCreative() && (proof == null || !player.getAdvancements().getOrStartProgress(proof).isDone())) return fail(player, "proof");
        var bonds = VigiaBonds.get(level);
        if (bonds.bound(player.getUUID()) != null) return fail(player, "limit");
        if (!player.isCreative() && EtherData.get(player) < 60) return fail(player, "energy");
        var vigia = ModEntities.VIGIA.get().create(level);
        if (vigia == null) return InteractionResult.FAIL;
        vigia.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot(), 0);
        var box = vigia.getBoundingBox();
        if (!level.hasChunksAt(BlockPos.containing(box.minX, box.minY, box.minZ), BlockPos.containing(box.maxX, box.maxY, box.maxZ))
            || !level.noCollision(vigia) || level.containsAnyLiquid(box)) return fail(player, "space");
        vigia.bind(player.getUUID(), pos);
        if (!bonds.claim(player.getUUID(), vigia.getUUID())) return fail(player, "limit");
        if (!level.addFreshEntity(vigia)) { bonds.release(player.getUUID(), vigia.getUUID()); return InteractionResult.FAIL; }
        if (!player.isCreative()) { EtherData.spend(player, 60); context.getItemInHand().shrink(1); }
        player.displayClientMessage(Component.translatable("message.etheris.vigia.created"), false);
        return InteractionResult.SUCCESS;
    }
}
