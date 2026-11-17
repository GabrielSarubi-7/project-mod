package com.etheris.content;

import com.etheris.Etheris;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Etheris.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ETHERIS_TAB = TABS.register("etheris", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.etheris"))
        .icon(() -> new ItemStack(ModItems.AETHER_FOCUS.get()))
        .displayItems((parameters, output) -> {
            output.accept(ModItems.ETHERIUM_FRAGMENT);
            output.accept(ModItems.AETHER_FOCUS);
            output.accept(ModItems.RUNE);
            output.accept(ModItems.IRON_DUST);
            output.accept(ModItems.CRYSTAL_DUST);
            output.accept(ModItems.STABLE_CRYSTAL);
            output.accept(ModItems.ASTRA_HELMET); output.accept(ModItems.ASTRA_CHESTPLATE);
            output.accept(ModItems.ASTRA_LEGGINGS); output.accept(ModItems.ASTRA_BOOTS);
            output.accept(ModItems.ECLIPSE_HELMET); output.accept(ModItems.ECLIPSE_CHESTPLATE);
            output.accept(ModItems.ECLIPSE_LEGGINGS); output.accept(ModItems.ECLIPSE_BOOTS);
            output.accept(ModItems.VIGIA_CONTRACT);
            output.accept(ModBlocks.PRISMATIC_CRUSHER);
            output.accept(ModBlocks.CRYSTAL_REFINERY);
            output.accept(ModBlocks.DISTRIBUTION_NODE);
            output.accept(ModBlocks.TRANSMUTATION_CHAMBER);
            output.accept(ModItems.CODEX);
            output.accept(ModItems.LINK_WAND);
            output.accept(ModBlocks.ETHER_COLLECTOR);
            output.accept(ModBlocks.AETHER_FOCUS_BLOCK);
            output.accept(ModBlocks.RUNE_FORGE);
            output.accept(ModBlocks.AMBER_RESERVOIR);
        }).build());
    private ModCreativeTabs() {}
}
