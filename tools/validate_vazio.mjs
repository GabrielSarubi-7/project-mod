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
let cubes=0;
for(const b of bones){
  const chain=new Set([b.name]);let parent=b.parent;
  while(parent){assert.ok(byName.has(parent));assert.ok(!chain.has(parent),'Cyclic bone graph');chain.add(parent);parent=byName.get(parent).parent;}
  for(const c of b.cubes){
    assert.ok(c.origin.every(Number.isFinite));assert.ok(c.size.every(n=>Number.isFinite(n)&&n>0));
    for(const f of Object.values(c.uv))assert.ok(f.uv.every(n=>n>=0)&&f.uv_size.every(v=>v>=1)&&f.uv[0]+f.uv_size[0]<=1024&&f.uv[1]+f.uv_size[1]<=64);
    cubes++;
  }
}
assert.equal(cubes,project.elements.length);
assert.ok(cubes<500,'Keep the wearable geometry budget bounded');
assert.equal(byName.get('cape_root').parent,'armorBody');
for(const column of ['right','center','left'])for(let i=0;i<4;i++){
  const b=byName.get(`cape_${column}_${i}`);
  assert.equal(b.parent,i===0?'cape_root':`cape_${column}_${i-1}`);
  assert.ok(b.cubes.length>1,'Torn cape requires real segmented geometry');
  assert.ok(b.cubes.every(c=>c.size[2]<.4),'Cloth should be thin');
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
console.log(`PASS Vazio: ${cubes} cuboids, ${bones.length} bones, 8 equipment roots, cape/cloth hierarchy, UVs, animations, localized items and selective emission.`);
