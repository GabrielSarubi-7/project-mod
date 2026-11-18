import fs from 'node:fs';
import path from 'node:path';
import assert from 'node:assert/strict';
import {fileURLToPath} from 'node:url';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'..');
const assets=path.join(root,'src/main/resources/assets/etheris');
const folder=path.join(root,'art/blockbench');
const read=f=>JSON.parse(fs.readFileSync(f,'utf8'));
let count=0;
for(const entry of read(path.join(folder,'catalogue.json'))){
  const game=read(path.join(assets,`models/${entry.kind}/${entry.id}.json`));
  const bb=read(path.join(folder,entry.project));
  assert.equal(game.elements.length,entry.cubes,entry.id);
  assert.equal(bb.elements.length,entry.cubes,entry.id);
  const ids=[...bb.elements,...bb.groups,...bb.textures].map(e=>e.uuid);
  assert.equal(new Set(ids).size,ids.length,'Duplicate UUID: '+entry.id);
  assert.equal(bb.outliner.flatMap(g=>g.children).length,entry.cubes);
  for(const tex of bb.textures) {
    const embedded=Buffer.from(tex.source.split(',')[1],'base64');
    const disk=fs.readFileSync(path.join(assets,`textures/block/firmament/${tex.name}`));
    assert.ok(embedded.equals(disk),'Embedded texture differs: '+tex.name);
    assert.equal(disk.readUInt32BE(16),32);assert.equal(disk.readUInt32BE(20),32);
  }
  for(let i=0;i<game.elements.length;i++){
    const e=game.elements[i],b=bb.elements[i];
    assert.deepEqual(e.from,b.from);assert.deepEqual(e.to,b.to);
    for(let k=0;k<3;k++){assert.ok(e.from[k]<e.to[k],'Flat/reversed cube: '+entry.id);assert.ok(e.from[k]>=-16&&e.to[k]<=32,'Minecraft bounds: '+entry.id);}
    if(e.rotation){assert.ok(['x','y','z'].includes(e.rotation.axis));assert.ok([-45,-22.5,0,22.5,45].includes(e.rotation.angle),'1.21.1 rotation limit');assert.deepEqual(e.rotation.origin,b.origin);}
    for(const [side,face] of Object.entries(e.faces)){
      assert.ok(face.uv.every(v=>Number.isFinite(v)&&v>=0&&v<=16));
      assert.deepEqual(face.uv.map(v=>v*2),b.faces[side].uv);
      const ref=game.textures[face.texture.slice(1)];assert.ok(ref?.startsWith('etheris:'),'External or missing texture');
      const file=path.join(assets,'textures',ref.slice('etheris:'.length)+'.png');assert.ok(fs.existsSync(file),'Missing texture '+file);
      assert.equal(bb.textures[b.faces[side].texture].name,ref.split('/').at(-1)+'.png');
    }
  }
  if(entry.kind==='block')assert.equal(read(path.join(assets,`models/item/${entry.id}.json`)).parent,`etheris:block/${entry.id}`);
  count+=game.elements.length;
}
console.log(`PASS: 11 models / ${count} cuboids; Java 1.21.1 bounds/rotations, UV parity, embedded textures, UUIDs and item parents.`);
