package com.etheris.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import com.etheris.content.ModItems;

public final class EclipseArmorExtensions implements IClientItemExtensions {
    private final EclipseArmorModel outer=new EclipseArmorModel(false), inner=new EclipseArmorModel(true);
    @Override public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity,ItemStack stack,EquipmentSlot slot,HumanoidModel<?> original) {
        return slot==EquipmentSlot.LEGS?inner:outer;
    }
    public static void register(RegisterClientExtensionsEvent event) {
        event.registerItem(new EclipseArmorExtensions(),ModItems.ECLIPSE_HELMET.get(),ModItems.ECLIPSE_CHESTPLATE.get(),ModItems.ECLIPSE_LEGGINGS.get(),ModItems.ECLIPSE_BOOTS.get());
    }
}
