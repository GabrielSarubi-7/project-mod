package com.etheris.client;

import com.etheris.item.VazioArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class VazioArmorRenderer extends GeoArmorRenderer<VazioArmorItem> {
    public VazioArmorRenderer() {
        super(new VazioArmorModel());
        addRenderLayer(new VazioGlowLayer(this));
    }
}
