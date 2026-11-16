package com.etheris.client;

import com.etheris.entity.VigiaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class VigiaRenderer extends MobRenderer<VigiaEntity, IronGolemModel<VigiaEntity>> {
    public VigiaRenderer(EntityRendererProvider.Context context) { super(context, new IronGolemModel<>(VigiaModel.layer().bakeRoot()), 0.4f); }
    @Override public ResourceLocation getTextureLocation(VigiaEntity entity) { return ResourceLocation.fromNamespaceAndPath("etheris", "textures/entity/vigia.png"); }
    @Override protected void scale(VigiaEntity entity, PoseStack pose, float partial) { pose.scale(0.6f, 0.6f, 0.6f); }
}
