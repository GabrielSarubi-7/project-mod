package com.etheris.client;

import com.etheris.entity.VigiaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class VigiaRenderer extends MobRenderer<VigiaEntity, IronGolemModel<VigiaEntity>> {
    public VigiaRenderer(EntityRendererProvider.Context context) { super(context, new IronGolemModel<>(context.bakeLayer(ModelLayers.IRON_GOLEM)), 0.4f); }
    @Override public ResourceLocation getTextureLocation(VigiaEntity entity) { return ResourceLocation.withDefaultNamespace("textures/entity/iron_golem/iron_golem.png"); }
    @Override protected void scale(VigiaEntity entity, PoseStack pose, float partial) { pose.scale(0.6f, 0.6f, 0.6f); }
}
