package com.etheris.content;

import com.etheris.Etheris;
import com.etheris.item.AetherFocusItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Etheris.MOD_ID);
    public static final DeferredItem<Item> ETHERIUM_FRAGMENT = ITEMS.registerSimpleItem("etherium_fragment", new Item.Properties());
    public static final DeferredItem<Item> AETHER_FOCUS = ITEMS.registerItem("aether_focus", AetherFocusItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> RUNE = ITEMS.registerSimpleItem("rune");
    public static final DeferredItem<Item> CODEX = ITEMS.registerItem("codex", com.etheris.item.CodexItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> LINK_WAND = ITEMS.registerItem("link_wand", com.etheris.item.LinkWandItem::new, new Item.Properties().stacksTo(1));
    static {
        ITEMS.registerSimpleBlockItem(ModBlocks.ETHER_COLLECTOR);
        ITEMS.registerSimpleBlockItem(ModBlocks.AETHER_FOCUS_BLOCK);
        ITEMS.registerSimpleBlockItem(ModBlocks.RUNE_FORGE);
        ITEMS.registerSimpleBlockItem(ModBlocks.AMBER_RESERVOIR);
    }
    private ModItems() {}
}
