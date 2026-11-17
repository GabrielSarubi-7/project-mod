package com.etheris.client;

import com.etheris.item.AstraArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class AstraArmorRenderer extends GeoArmorRenderer<AstraArmorItem> {
    public AstraArmorRenderer() {
        super(new AstraArmorModel());
        addRenderLayer(new AstraGlowLayer(this));
    }
}
