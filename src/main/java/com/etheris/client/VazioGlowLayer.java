package com.etheris.client;

import com.etheris.item.VazioArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/** Only fissures and crystals emit light; the eclipse's opaque center stays black. */
public final class VazioGlowLayer extends AutoGlowingGeoLayer<VazioArmorItem> {
    private final VazioArmorRenderer armor;
    public VazioGlowLayer(VazioArmorRenderer armor) { super(armor); this.armor = armor; }

    @Override public void render(PoseStack pose, VazioArmorItem item, BakedGeoModel model, RenderType type,
            MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
        var entity = armor.getCurrentEntity();
        if (buffer == null || entity == null || entity.isInvisible()) return;
        var glowType = getRenderType(item, buffers);
        if (glowType == null) return;
        float pulse = .88f + .12f * Mth.sin((entity.tickCount + partialTick) * Mth.TWO_PI / 100f);
        int color = ((int)(pulse * 255) << 24) | 0xFFFFFF;
        armor.reRender(model, pose, buffers, item, glowType, buffers.getBuffer(glowType), partialTick,
                LightTexture.FULL_BRIGHT, overlay, color);
    }
}
