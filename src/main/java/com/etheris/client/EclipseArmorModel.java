package com.etheris.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

/** Authored geometry, with palette UVs: dark plates, gilded edges and lunar inlays. */
public final class EclipseArmorModel extends HumanoidModel<LivingEntity> {
    private static CubeListBuilder box(int palette,float x,float y,float z,float w,float h,float d) {
        return CubeListBuilder.create().texOffs(palette*64,0).addBox(x,y,z,w,h,d);
    }
    private static void add(PartDefinition parent,String name,int color,float x,float y,float z,float w,float h,float d) {
        parent.addOrReplaceChild(name,box(color,x,y,z,w,h,d),PartPose.ZERO);
    }
    public EclipseArmorModel(boolean leggings) { super(layer(leggings).bakeRoot()); }
    private static LayerDefinition layer(boolean leggings) {
        var mesh=HumanoidModel.createMesh(CubeDeformation.NONE,0); var root=mesh.getRoot();
        // Empty vanilla parts retain all pose pivots while removing the vanilla silhouette.
        var head=root.addOrReplaceChild("head",CubeListBuilder.create(),PartPose.ZERO);
        root.addOrReplaceChild("hat",CubeListBuilder.create(),PartPose.ZERO);
        var body=root.addOrReplaceChild("body",CubeListBuilder.create(),PartPose.ZERO);
        var ra=root.addOrReplaceChild("right_arm",CubeListBuilder.create(),PartPose.offset(-5,2,0));
        var la=root.addOrReplaceChild("left_arm",CubeListBuilder.create(),PartPose.offset(5,2,0));
        var rl=root.addOrReplaceChild("right_leg",CubeListBuilder.create(),PartPose.offset(-1.9f,12,0));
        var ll=root.addOrReplaceChild("left_leg",CubeListBuilder.create(),PartPose.offset(1.9f,12,0));
        if(leggings) {
            add(body,"girdle",1,-4.6f,9,-2.7f,9.2f,2,5.4f);
            add(rl,"thigh",0,-2.4f,0,-2.4f,4.8f,7,4.8f); add(ll,"thigh",0,-2.4f,0,-2.4f,4.8f,7,4.8f);
            for(var leg:new PartDefinition[]{rl,ll}) { add(leg,"knee",1,-2.5f,5,-2.7f,5,2,1); add(leg,"moon",3,-0.5f,5.4f,-2.85f,1,1.2f,0.3f); }
        } else {
            add(head,"helm",0,-4.5f,-8.6f,-4.5f,9,9,9);
            add(head,"visor",2,-3.8f,-5.5f,-4.7f,7.6f,2.5f,0.4f);
            add(head,"eye_left",3,-3.2f,-4.6f,-4.95f,2.4f,0.4f,0.3f);
            add(head,"eye_right",3,0.8f,-4.6f,-4.95f,2.4f,0.4f,0.3f);
            add(head,"brow",1,-4.8f,-6.2f,-4.8f,9.6f,0.7f,0.7f);
            add(head,"crest",3,-0.6f,-9.8f,-4.7f,1.2f,3.4f,0.7f);
            for(int sign:new int[]{-1,1}) {
                var crown=head.addOrReplaceChild("crown_"+sign,box(1,-0.55f,-7,0,1.1f,7,1),PartPose.offsetAndRotation(sign*4,-7,-2,0,0,sign*0.28f));
                add(crown,"tip",3,-0.35f,-8,0.1f,0.7f,1,0.7f);
            }
            add(body,"cuirass",0,-4.6f,0,-2.8f,9.2f,10,5.6f);
            add(body,"collar",1,-4.8f,-1,-3,9.6f,1.2f,6);
            add(body,"sternum",1,-0.8f,1,-3.1f,1.6f,8,0.6f);
            var jewel=body.addOrReplaceChild("eclipse",box(3,-1.2f,-1.2f,-0.35f,2.4f,2.4f,0.7f),PartPose.offsetAndRotation(0,3,-3.4f,0,0,0.7854f));
            add(jewel,"core",2,-0.6f,-0.6f,-0.5f,1.2f,1.2f,0.3f);
            add(ra,"gauntlet",0,-3.6f,2,-2.6f,5.2f,8,5.2f); add(la,"gauntlet",0,-1.6f,2,-2.6f,5.2f,8,5.2f);
            add(ra,"pauldron",1,-5,-2.5f,-3.4f,7,3.5f,6.8f); add(ra,"plate",0,-5.3f,-3,-3.1f,6.8f,1.5f,6.2f);
            add(la,"pauldron",1,-2,-2,-3.2f,6.2f,2.5f,6.4f); add(la,"plate",0,-1.8f,-2.6f,-2.9f,5.8f,1,5.8f);
            add(ra,"cuff",1,-3.8f,8,-2.8f,5.6f,1,5.6f); add(la,"cuff",1,-1.8f,8,-2.8f,5.6f,1,5.6f);
            for(int i=0;i<3;i++) {
                var mantle=body.addOrReplaceChild("mantle_"+i,box(0,-1.4f,0,0,2.8f,15-i,0.8f),PartPose.offsetAndRotation((i-1)*3,5,3.2f,0.12f,0,(i-1)*-0.08f));
                add(mantle,"hem",1,-1.4f,13-i,0,2.8f,0.7f,1); add(mantle,"inlay",i==1?3:1,-0.2f,1,0.8f,0.4f,10,0.2f);
            }
            for(var leg:new PartDefinition[]{rl,ll}) {
                add(leg,"greave",0,-2.5f,6,-2.6f,5,6,5.2f); add(leg,"sole",1,-2.6f,11,-3.3f,5.2f,1.3f,6);
                add(leg,"shin",1,-0.5f,7,-2.9f,1,4,0.5f);
            }
        }
        return LayerDefinition.create(mesh,256,32);
    }
}
