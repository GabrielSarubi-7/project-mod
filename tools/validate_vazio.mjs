import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
import {inflateSync} from 'node:zlib';
import {fileURLToPath} from 'node:url';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'..');
const assets=path.join(root,'src/main/resources/assets/etheris');
const read=p=>JSON.parse(fs.readFileSync(p,'utf8'));
const geometry=read(path.join(assets,'geo/armor/vazio.geo.json'))['minecraft:geometry'][0];
const project=read(path.join(root,'art/blockbench/vazio_regalia.bbmodel'));
const bones=geometry.bones,byName=new Map(bones.map(b=>[b.name,b]));
const roots=['armorHead','armorBody','armorRightArm','armorLeftArm','armorRightLeg','armorLeftLeg','armorRightBoot','armorLeftBoot'];
assert.equal(byName.size,bones.length);
assert.deepEqual(bones.filter(b=>!b.parent).map(b=>b.name).sort(),[...roots].sort());
const sculpt=read(path.join(assets,'armor_mesh/vazio.mesh.json'));
const meshes=new Map(sculpt.bones.map(b=>[b.name,b.meshes]));
assert.deepEqual([...meshes.keys()].sort(),[...byName.keys()].sort());
let faces=0,pieces=0,triangles=0;
for(const b of bones){
  const chain=new Set([b.name]);let parent=b.parent;
  while(parent){assert.ok(byName.has(parent));assert.ok(!chain.has(parent),'Cyclic bone graph');chain.add(parent);parent=byName.get(parent).parent;}
  assert.equal(b.cubes.length,0,'Gecko skeleton must not double-render old cuboids');
  for(const m of meshes.get(b.name)){
    for(const p of m.vertices)assert.ok(p.length===3&&p.every(Number.isFinite));
    for(const f of m.faces){
      assert.ok([3,4].includes(f.vertices.length));
      assert.ok(f.vertices.every(i=>Number.isInteger(i)&&i>=0&&i<m.vertices.length));
      assert.equal(f.vertices.length,f.uv.length);
      assert.ok(f.uv.every(([u,v])=>u>=0&&u<=1024&&v>=0&&v<=64));
      assert.ok(Math.abs(Math.hypot(...f.normal)-1)<.002);
      faces++;triangles+=f.vertices.length-2;
    }
    pieces++;
  }
}
assert.equal(pieces,project.elements.length);
assert.ok(faces<9000&&triangles<14000,'Keep wearable polygon cost bounded');
assert.ok(project.elements.every(e=>e.type==='mesh'),'Editable sources must contain the actual polygons');
assert.equal(project.meta.model_format,'free');
assert.equal(byName.get('cape_root').parent,'armorBody');
for(const column of ['right','center','left'])for(let i=0;i<4;i++){
  const b=byName.get(`cape_${column}_${i}`);
  assert.equal(b.parent,i===0?'cape_root':`cape_${column}_${i-1}`);
  assert.ok(meshes.get(b.name).some(m=>m.faces.length>30),'Cape needs real folded surfaces');
}
for(const name of ['robe_right','robe_left','stole_front'])for(let i=0;i<3;i++){
  const b=byName.get(i===0?name:name+'_'+i);
  assert.equal(b.parent,i===0?'armorBody':i===1?name:name+'_'+(i-1));
}
for(const name of ['crown_shard_right','crown_shard_left','crown_shard_rear','void_face'])assert.equal(byName.get(name).parent,'armorHead');
assert.equal(byName.get('void_core').parent,'armorBody');
assert.equal(byName.get('void_iris').parent,'void_core');
assert.equal(byName.get('back_eclipse').parent,'cape_center_0');
const texture=fs.readFileSync(path.join(assets,'textures/armor/vazio.png'));
assert.ok(texture.equals(Buffer.from(project.textures[0].source.split(',')[1],'base64')));
const animation=read(path.join(assets,'animations/armor/vazio.animation.json')).animations['animation.vazio.eclipse'];
for(const[name,channels]of Object.entries(animation.bones)){
  assert.ok(byName.has(name),'Unknown animation bone '+name);
  for(const frames of Object.values(channels))for(const[t,v]of Object.entries(frames)){
    assert.ok(Number(t)>=0&&Number(t)<=12);assert.ok(v.every(Number.isFinite));
  }
}
// Decode this generator's filter-0 PNG to catch a fully glowing or missing mask.
const mask=fs.readFileSync(path.join(assets,'textures/armor/vazio_glowmask.png'));
assert.equal(mask.readUInt32BE(16),1024);assert.equal(mask.readUInt32BE(20),64);
const chunks=[];for(let p=8;p<mask.length;){const n=mask.readUInt32BE(p),type=mask.toString('ascii',p+4,p+8);if(type==='IDAT')chunks.push(mask.subarray(p+8,p+8+n));p+=12+n;}
const pixels=inflateSync(Buffer.concat(chunks)),stride=1024*4+1;
for(let mat=0;mat<16;mat++){
  const alpha=pixels[32*stride+1+(mat*64+32)*4+3];
  assert.equal(alpha>0,[4,5,6,13].includes(mat),'Wrong emissive material '+mat);
}
for(const part of ['helmet','chestplate','leggings','boots']){
  assert.equal(read(path.join(assets,`models/item/vazio_${part}.json`)).textures.layer0,`etheris:item/vazio_${part}`);
  assert.ok(fs.existsSync(path.join(assets,`textures/item/vazio_${part}.png`)));
}
for(const lang of ['pt_br','en_us']){
  const messages=read(path.join(assets,`lang/${lang}.json`));
  for(const part of ['helmet','chestplate','leggings','boots'])assert.ok(messages[`item.etheris.vazio_${part}`]);
  assert.ok(messages['message.etheris.vazio_preview']);
}
console.log(`PASS Vazio: ${pieces} sculpted pieces, ${faces} faces (${triangles} triangles), ${bones.length} bones; slot/cloth hierarchy, UVs, animations, mesh source, localized items and selective emission.`);
