package com.etheris.content;

import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Preview equipment: intentionally obtained explicitly, not inserted into survival progression. */
public final class EclipseArmor {
    public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "etheris");
    public static final net.neoforged.neoforge.registries.DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("eclipse", () -> new ArmorMaterial(
        Map.of(ArmorItem.Type.HELMET,3, ArmorItem.Type.CHESTPLATE,8, ArmorItem.Type.LEGGINGS,6, ArmorItem.Type.BOOTS,3, ArmorItem.Type.BODY,8),
        15, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(ModItems.STABLE_CRYSTAL.get()),
        List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("etheris", "eclipse"))), 2, 0));
    private EclipseArmor() {}
}
