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
    private ModItems() {}
}
