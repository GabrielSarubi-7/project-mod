package com.etheris.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;

/** Bakes authored polygon surfaces into Gecko's normal bone, visibility and emissive pipeline. */
public final class ArmorMeshLoader {
    private ArmorMeshLoader() {}

    public static Map<String, List<GeoCube>> read(Reader reader) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        if (root.get("format_version").getAsInt() != 1) throw new IllegalArgumentException("Unsupported armor mesh version");
        JsonArray size = root.getAsJsonArray("texture_size");
        float width = size.get(0).getAsFloat(), height = size.get(1).getAsFloat();
        if (!(width > 0 && height > 0)) throw new IllegalArgumentException("Invalid mesh atlas size");
        Map<String, List<GeoCube>> result = new LinkedHashMap<>();
        int count = 0;
        for (var boneElement : root.getAsJsonArray("bones")) {
            JsonObject bone = boneElement.getAsJsonObject();
            String name = bone.get("name").getAsString();
            List<GeoCube> pieces = new ArrayList<>();
            if (result.put(name, pieces) != null) throw new IllegalArgumentException("Duplicate mesh bone: " + name);
            for (var element : bone.getAsJsonArray("meshes")) {
                JsonObject mesh = element.getAsJsonObject();
                JsonArray points = mesh.getAsJsonArray("vertices");
                List<GeoQuad> quads = new ArrayList<>();
                for (var faceElement : mesh.getAsJsonArray("faces")) {
                    JsonObject face = faceElement.getAsJsonObject();
                    JsonArray indices = face.getAsJsonArray("vertices"), uvs = face.getAsJsonArray("uv");
                    int n = indices.size();
                    if ((n != 3 && n != 4) || uvs.size() != n || ++count > 16000)
                        throw new IllegalArgumentException("Invalid or oversized armor mesh face");
                    GeoVertex[] vertices = new GeoVertex[4];
                    // Blockbench X is mirrored by Gecko. Reverse winding too, retaining UV pairing.
                    for (int i = 0; i < 4; i++) {
                        int j = n - 1 - Math.min(i, n - 1);
                        Vector3f p = vector(points.get(indices.get(j).getAsInt()).getAsJsonArray());
                        p.mul(-1f / 16, 1f / 16, 1f / 16);
                        JsonArray uv = uvs.get(j).getAsJsonArray();
                        float u = uv.get(0).getAsFloat(), v = uv.get(1).getAsFloat();
                        if (!Float.isFinite(u) || !Float.isFinite(v) || u < 0 || v < 0 || u > width || v > height)
                            throw new IllegalArgumentException("Mesh UV outside atlas");
                        vertices[i] = new GeoVertex(p, u / width, v / height);
                    }
                    Vector3f normal = vector(face.getAsJsonArray("normal")).mul(-1, 1, 1);
                    if (normal.lengthSquared() < .9f || normal.lengthSquared() > 1.1f)
                        throw new IllegalArgumentException("Invalid mesh face normal");
                    normal.normalize();
                    // Direction is unused by GeoRenderer; geometry and normal describe arbitrary faces.
                    quads.add(new GeoQuad(vertices, normal, Direction.NORTH));
                }
                // Coordinates are already baked in model space. Nonzero size avoids flat-cube normal correction.
                pieces.add(new GeoCube(quads.toArray(GeoQuad[]::new), Vec3.ZERO, Vec3.ZERO, new Vec3(1, 1, 1), 0, false));
            }
        }
        return result;
    }

    public static void attach(BakedGeoModel model, Map<String, List<GeoCube>> data) {
        Map<GeoBone, List<GeoCube>> pending = new LinkedHashMap<>();
        data.forEach((name, cubes) -> pending.put(model.getBone(name)
                .orElseThrow(() -> new IllegalArgumentException("Unknown mesh bone: " + name)), cubes));
        // Resolve every target before changing the shared cached model. Repeated attachment is idempotent.
        pending.forEach((bone, cubes) -> { bone.getCubes().clear(); bone.getCubes().addAll(cubes); });
    }

    private static Vector3f vector(JsonArray data) {
        if (data.size() != 3) throw new IllegalArgumentException("Expected three vector components");
        Vector3f v = new Vector3f(data.get(0).getAsFloat(), data.get(1).getAsFloat(), data.get(2).getAsFloat());
        if (!v.isFinite()) throw new IllegalArgumentException("Nonfinite mesh vector");
        return v;
    }
}
