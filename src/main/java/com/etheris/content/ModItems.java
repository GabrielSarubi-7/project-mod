package com.etheris.content;

import com.etheris.Etheris;
import com.etheris.item.AetherFocusItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Etheris.MOD_ID);
    public static final DeferredItem<com.etheris.item.AstraArmorItem> ASTRA_HELMET = astra("astra_helmet", net.minecraft.world.item.ArmorItem.Type.HELMET);
    public static final DeferredItem<com.etheris.item.AstraArmorItem> ASTRA_CHESTPLATE = astra("astra_chestplate", net.minecraft.world.item.ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<com.etheris.item.AstraArmorItem> ASTRA_LEGGINGS = astra("astra_leggings", net.minecraft.world.item.ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<com.etheris.item.AstraArmorItem> ASTRA_BOOTS = astra("astra_boots", net.minecraft.world.item.ArmorItem.Type.BOOTS);
    private static DeferredItem<com.etheris.item.AstraArmorItem> astra(String id, net.minecraft.world.item.ArmorItem.Type type) {
        return ITEMS.register(id, () -> new com.etheris.item.AstraArmorItem(type, new Item.Properties().durability(type.getDurability(30))));
    }
    public static final DeferredItem<net.minecraft.world.item.ArmorItem> ECLIPSE_HELMET = eclipse("eclipse_helmet", net.minecraft.world.item.ArmorItem.Type.HELMET);
    public static final DeferredItem<net.minecraft.world.item.ArmorItem> ECLIPSE_CHESTPLATE = eclipse("eclipse_chestplate", net.minecraft.world.item.ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<net.minecraft.world.item.ArmorItem> ECLIPSE_LEGGINGS = eclipse("eclipse_leggings", net.minecraft.world.item.ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<net.minecraft.world.item.ArmorItem> ECLIPSE_BOOTS = eclipse("eclipse_boots", net.minecraft.world.item.ArmorItem.Type.BOOTS);
    private static DeferredItem<net.minecraft.world.item.ArmorItem> eclipse(String id, net.minecraft.world.item.ArmorItem.Type type) {
        return ITEMS.register(id, () -> new net.minecraft.world.item.ArmorItem(EclipseArmor.MATERIAL, type, new Item.Properties().durability(type.getDurability(30))));
    }
    public static final DeferredItem<Item> ETHERIUM_FRAGMENT = ITEMS.registerSimpleItem("etherium_fragment", new Item.Properties());
    public static final DeferredItem<Item> AETHER_FOCUS = ITEMS.registerItem("aether_focus", AetherFocusItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> RUNE = ITEMS.registerSimpleItem("rune");
    public static final DeferredItem<Item> IRON_DUST = ITEMS.registerSimpleItem("iron_dust");
    public static final DeferredItem<Item> CRYSTAL_DUST = ITEMS.registerSimpleItem("crystal_dust");
    public static final DeferredItem<Item> STABLE_CRYSTAL = ITEMS.registerSimpleItem("stable_crystal");
    public static final DeferredItem<Item> VIGIA_CONTRACT = ITEMS.registerItem("vigia_contract", com.etheris.item.VigiaContractItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> CODEX = ITEMS.registerItem("codex", com.etheris.item.CodexItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> LINK_WAND = ITEMS.registerItem("link_wand", com.etheris.item.LinkWandItem::new, new Item.Properties().stacksTo(1));
    static {
        ITEMS.registerSimpleBlockItem(ModBlocks.ETHER_COLLECTOR);
        ITEMS.registerSimpleBlockItem(ModBlocks.AETHER_FOCUS_BLOCK);
        ITEMS.registerSimpleBlockItem(ModBlocks.RUNE_FORGE);
        ITEMS.registerSimpleBlockItem(ModBlocks.AMBER_RESERVOIR);
        ITEMS.registerSimpleBlockItem(ModBlocks.PRISMATIC_CRUSHER);
        ITEMS.registerSimpleBlockItem(ModBlocks.CRYSTAL_REFINERY);
        ITEMS.registerSimpleBlockItem(ModBlocks.DISTRIBUTION_NODE);
        ITEMS.registerSimpleBlockItem(ModBlocks.TRANSMUTATION_CHAMBER);
    }
    private ModItems() {}
}
