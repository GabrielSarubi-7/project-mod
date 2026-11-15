package com.etheris.item;
import com.etheris.machine.MachineEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
public final class LinkWandItem extends Item {
    public LinkWandItem(Properties properties) { super(properties); }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel(); var player = context.getPlayer(); var pos = context.getClickedPos();
        if (player == null || !(level.getBlockEntity(pos) instanceof MachineEntity)) return InteractionResult.PASS;
        if (!level.mayInteract(player, pos)) return InteractionResult.FAIL;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        var data = player.getPersistentData(); String dimension = level.dimension().location().toString();
        if (player.isShiftKeyDown() || !data.contains("etheris_link_start")) {
            data.putLong("etheris_link_start", pos.asLong()); data.putString("etheris_link_dimension", dimension);
            player.displayClientMessage(Component.literal("Origem marcada. Use em uma máquina a até 8 blocos para conectar."), true);
        } else {
            var start = BlockPos.of(data.getLong("etheris_link_start"));
            if (dimension.equals(data.getString("etheris_link_dimension")) && !start.equals(pos) && start.distSqr(pos) <= 64 && level.hasChunkAt(start) && level.mayInteract(player, start) && level.getBlockEntity(start) instanceof MachineEntity source) {
                source.linkTo(pos); level.scheduleTick(start, source.getBlockState().getBlock(), 100);
                player.displayClientMessage(Component.literal("Enlace criado: origem → destino. Máximo 8 Éter a cada 5 segundos."), true);
            } else player.displayClientMessage(Component.literal("Enlace inválido: mesma dimensão, máquinas distintas, até 8 blocos."), true);
            data.remove("etheris_link_start"); data.remove("etheris_link_dimension");
        }
        return InteractionResult.SUCCESS;
    }
}
