import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
import {fileURLToPath} from 'node:url';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'..');
const assets=path.join(root,'src/main/resources/assets/etheris');
const read=p=>JSON.parse(fs.readFileSync(p,'utf8'));
const geometry=read(path.join(assets,'geo/armor/astra.geo.json'))['minecraft:geometry'][0];
const project=read(path.join(root,'art/blockbench/astra_oracle.bbmodel'));
const bones=geometry.bones,byName=new Map(bones.map(b=>[b.name,b]));
const roots=['armorHead','armorBody','armorRightArm','armorLeftArm','armorRightLeg','armorLeftLeg','armorRightBoot','armorLeftBoot'];
assert.equal(byName.size,bones.length,'Duplicate bone names');
assert.deepEqual(bones.filter(b=>!b.parent).map(b=>b.name).sort(),[...roots].sort(),'Armor slot roots');
let cubes=0;
for(const b of bones){
  const chain=new Set([b.name]);let parent=b.parent;
  while(parent){assert.ok(byName.has(parent),'Missing parent');assert.ok(!chain.has(parent),'Cyclic bone graph');chain.add(parent);parent=byName.get(parent).parent;}
  for(const c of b.cubes){
    assert.ok(c.size.every(n=>Number.isFinite(n)&&n>0));
    for(const uv of Object.values(c.uv)){assert.ok(uv.uv_size.every(v=>v>=1));assert.ok(uv.uv[0]+uv.uv_size[0]<=512&&uv.uv[1]+uv.uv_size[1]<=64,'UV outside atlas');}
    cubes++;
  }
}
assert.equal(cubes,project.elements.length);
const texture=fs.readFileSync(path.join(assets,'textures/armor/astra.png'));
assert.ok(texture.equals(Buffer.from(project.textures[0].source.split(',')[1],'base64')),'Embedded atlas must match game');
const animation=read(path.join(assets,'animations/armor/astra.animation.json')).animations['animation.astra.orbit'];
assert.equal(byName.get('cape_root').parent,'armorBody','Cape belongs to chest slot');
for(const column of ['right','center','left'])for(let i=0;i<4;i++){
  const name=`cape_${column}_${i}`,b=byName.get(name);
  assert.equal(b.parent,i===0?'cape_root':`cape_${column}_${i-1}`,'Cape hinge chain');
  assert.ok(animation.bones[name],'Cape editor preview track');
  const cloth=b.cubes[0];assert.ok(cloth.size[2]<.4,'Cape cloth should be thin');
  assert.ok(cloth.size[1]>4.85,'Overlapping vertical seams');
  assert.ok(cloth.size[0]>3.8,'Overlapping horizontal seams');
}
for(const name of ['robe_right','robe_left','stole_front','stole_back'])for(let i=0;i<3;i++){
  const b=byName.get(i===0?name:name+'_'+i);
  assert.equal(b.parent,i===0?'armorBody':i===1?name:name+'_'+(i-1),'Robe hinge chain');
  assert.ok(Math.min(...b.cubes[0].size)<.7,'Cloth must not become an armor plate');
}
for(const [name,channels] of Object.entries(animation.bones)){
  assert.ok(byName.has(name),'Animation has no bone: '+name);
  for(const frames of Object.values(channels)){assert.ok(Object.keys(frames).every(t=>Number(t)>=0&&Number(t)<=animation.animation_length));}
}
const glow=fs.readFileSync(path.join(assets,'textures/armor/astra_glowmask.png'));
assert.equal(glow.readUInt32BE(16),512);assert.equal(glow.readUInt32BE(20),64);
for(const part of ['helmet','chestplate','leggings','boots']){
  assert.equal(read(path.join(assets,`models/item/astra_${part}.json`)).textures.layer0,`etheris:item/astra_${part}`);
  assert.ok(fs.existsSync(path.join(assets,`textures/item/astra_${part}.png`)));
}
console.log(`PASS Astra: ${cubes} cubes, ${bones.length} bones, 8 isolated armor roots, UV bounds, animation targets, embedded atlas and inventory assets.`);
