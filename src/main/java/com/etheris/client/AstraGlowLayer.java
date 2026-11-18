package com.etheris.client;

import com.etheris.item.AstraArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/** Pulses only the emissive mask, preserving the lighting of porcelain and cloth. */
public final class AstraGlowLayer extends AutoGlowingGeoLayer<AstraArmorItem> {
    private final AstraArmorRenderer armor;
    public AstraGlowLayer(AstraArmorRenderer armor) { super(armor); this.armor = armor; }

    @Override public void render(PoseStack pose, AstraArmorItem item, BakedGeoModel model, RenderType type,
            MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
        if (buffer == null) return;
        var entity = armor.getCurrentEntity();
        if (entity == null || entity.isInvisible()) return;
        var glowType = getRenderType(item, buffers);
        if (glowType == null) return;
        float pulse = .82f + .18f * Mth.sin((entity.tickCount + partialTick) * Mth.TWO_PI / 80f);
        int color = ((int)(pulse * 255) << 24) | 0xFFFFFF;
        armor.reRender(model, pose, buffers, item, glowType, buffers.getBuffer(glowType), partialTick,
                LightTexture.FULL_BRIGHT, overlay, color);
    }
}
