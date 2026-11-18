package com.etheris.animation;

import com.etheris.client.ArmorMeshLoader;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;

/** Exercises the actual runtime polygon baker, including winding and resource reattachment. */
public final class ArmorMeshCheck {
    private static void require(boolean value, String message) { if (!value) throw new AssertionError(message); }
    private static InputStreamReader resource(String path) {
        var in=ArmorMeshCheck.class.getResourceAsStream(path);
        if(in==null)throw new AssertionError("Missing packaged resource: "+path);
        return new InputStreamReader(in,StandardCharsets.UTF_8);
    }
    private static BakedGeoModel skeleton() throws Exception {
        Map<String,GeoBone> bones=new LinkedHashMap<>(); List<GeoBone> roots=new ArrayList<>();
        try(var reader=resource("/assets/etheris/geo/armor/vazio.geo.json")) {
            var data=JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("minecraft:geometry")
                    .get(0).getAsJsonObject().getAsJsonArray("bones");
            for(var item:data) {
                var b=item.getAsJsonObject(); String name=b.get("name").getAsString();
                var parent=b.has("parent")?bones.get(b.get("parent").getAsString()):null;
                var bone=new GeoBone(parent,name,false,0d,false,false); bones.put(name,bone);
                if(parent==null)roots.add(bone);else parent.getChildBones().add(bone);
            }
        }
        return new BakedGeoModel(roots,null);
    }
    public static void main(String[] args) throws Exception {
        Map<String,List<GeoCube>> data;
        try(var reader=resource("/assets/etheris/armor_mesh/vazio.mesh.json")){data=ArmorMeshLoader.read(reader);}
        int count=0;
        for(var cubes:data.values())for(var cube:cubes)for(var face:cube.quads()){
            var v=face.vertices(); require(v.length==4,"Minecraft's vertex buffer requires quads");
            Vector3f a=new Vector3f(v[1].position()).sub(v[0].position());
            Vector3f b=new Vector3f(v[2].position()).sub(v[0].position());
            Vector3f normal=a.cross(b).normalize();
            require(normal.isFinite()&&normal.dot(face.normal())>.97f,"Mirrored winding must agree with outward lighting normal");
            for(var vertex:v)require(vertex.texU()>=0&&vertex.texU()<=1&&vertex.texV()>=0&&vertex.texV()<=1,"Normalized UV bounds");
            count++;
        }
        require(count>5000&&count<9000,"Unexpected polygon budget");
        BakedGeoModel model=skeleton(); require(model.topLevelBones().size()==8,"Eight armor slot roots");
        ArmorMeshLoader.attach(model,data);
        int head=model.getBone("armorHead").orElseThrow().getCubes().size();require(head>0,"Head mesh missing");
        ArmorMeshLoader.attach(model,data);
        require(model.getBone("armorHead").orElseThrow().getCubes().size()==head,"Repeated attachment duplicated surfaces");
        var invalid=new LinkedHashMap<>(data);invalid.put("missing_bone",List.of());
        try { ArmorMeshLoader.attach(model,invalid);throw new AssertionError("Unknown bone accepted"); }
        catch(IllegalArgumentException expected){require(model.getBone("armorHead").orElseThrow().getCubes().size()==head,"Failed load changed existing model");}
        BakedGeoModel reloaded=skeleton();ArmorMeshLoader.attach(reloaded,data);
        require(reloaded.getBone("armorHead").orElseThrow().getCubes().size()==head,"Reload lost geometry");
        System.out.println("PASS armor polygon baker: "+count+" faces; reflected winding/normals/UVs, 8 roots, idempotent attach, atomic failure and reload.");
    }
}
