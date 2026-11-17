// Offline geometric inspection, not a Minecraft screenshot. No app/window automation.
// Usage: node tools/preview_geo.mjs vazio output.png
import fs from 'node:fs';
import path from 'node:path';
import {fileURLToPath} from 'node:url';
import {inflateSync,deflateSync} from 'node:zlib';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'..');
const name=process.argv[2]||'vazio',out=process.argv[3]||path.join(root,'build',`${name}-geometry.png`);
const assets=path.join(root,'src/main/resources/assets/etheris');
const model=JSON.parse(fs.readFileSync(path.join(assets,`geo/armor/${name}.geo.json`),'utf8'))['minecraft:geometry'][0];
function decode(p){const b=fs.readFileSync(p),w=b.readUInt32BE(16),h=b.readUInt32BE(20),chunks=[];for(let i=8;i<b.length;){const n=b.readUInt32BE(i);if(b.toString('ascii',i+4,i+8)==='IDAT')chunks.push(b.subarray(i+8,i+8+n));i+=n+12;}const raw=inflateSync(Buffer.concat(chunks)),pixels=Buffer.alloc(w*h*4);for(let y=0;y<h;y++){if(raw[y*(w*4+1)]!==0)throw Error('Expected authoring PNG filter zero');raw.copy(pixels,y*w*4,y*(w*4+1)+1,(y+1)*(w*4+1));}return{w,h,pixels};}
const tex=decode(path.join(assets,`textures/armor/${name}.png`)),glow=decode(path.join(assets,`textures/armor/${name}_glowmask.png`));
const add=(a,b)=>a.map((v,i)=>v+b[i]),sub=(a,b)=>a.map((v,i)=>v-b[i]),dot=(a,b)=>a.reduce((s,v,i)=>s+v*b[i],0),cross=(a,b)=>[a[1]*b[2]-a[2]*b[1],a[2]*b[0]-a[0]*b[2],a[0]*b[1]-a[1]*b[0]],norm=a=>a.map(v=>v/Math.hypot(...a));
function rotate(v,deg){let[x,y,z]=v;for(let i=0;i<3;i++){const a=(deg?.[i]||0)*Math.PI/180,c=Math.cos(a),s=Math.sin(a);if(i===0)[y,z]=[c*y-s*z,s*y+c*z];if(i===1)[x,z]=[c*x+s*z,-s*x+c*z];if(i===2)[x,y]=[c*x-s*y,s*x+c*y];}return[x,y,z];}
const boneMap=new Map(model.bones.map(b=>[b.name,b]));
const faces={north:[0,3,2,1],south:[5,6,7,4],west:[4,7,3,0],east:[1,2,6,5],up:[3,7,6,2],down:[4,0,1,5]};
const quads=[];
for(const b of model.bones)for(const c of b.cubes){
  const[x,y,z]=c.origin,[w,h,d]=c.size;
  let vertices=[[x,y,z],[x+w,y,z],[x+w,y+h,z],[x,y+h,z],[x,y,z+d],[x+w,y,z+d],[x+w,y+h,z+d],[x,y+h,z+d]];
  if(c.rotation)vertices=vertices.map(v=>add(rotate(sub(v,c.pivot),c.rotation),c.pivot));
  for(let bone=b;bone;bone=boneMap.get(bone.parent))if(bone.rotation)vertices=vertices.map(v=>add(rotate(sub(v,bone.pivot),bone.rotation),bone.pivot));
  for(const[side,indices]of Object.entries(faces)){
    const uv=c.uv[side];if(!uv)continue;const[u,v]=uv.uv,[uw,vh]=uv.uv_size;
    const points=indices.map(i=>vertices[i]),normal=norm(cross(sub(points[1],points[0]),sub(points[2],points[0])));
    quads.push({points,normal,uv:[[u,v+vh],[u,v],[u+uw,v],[u+uw,v+vh]]});
  }
}
const W=1400,H=980,pixels=Buffer.alloc(W*H*4),depth=new Float64Array(W*H).fill(Infinity);
for(let y=0;y<H;y++)for(let x=0;x<W;x++){
  const r=Math.min(1,Math.hypot((x-W/2)/(W*.8),(y-H*.45)/(H*.8))),p=(y*W+x)*4;
  pixels.set([Math.round(28-14*r),Math.round(23-12*r),Math.round(42-21*r),255],p);
}
const light=norm([-.4,.9,-.65]);
function draw(camera,center,scale){
  const target=[0,19,0],forward=norm(sub(target,camera)),right=norm(cross(forward,[0,1,0])),up=cross(right,forward),triangles=[];
  const project=v=>[center[0]+dot(sub(v,target),right)*scale,center[1]-dot(sub(v,target),up)*scale,dot(sub(v,camera),forward)];
  for(const q of quads){
    if(dot(q.normal,forward)>=0)continue;
    const points=q.points.map(project),shade=.48+.52*Math.max(0,dot(q.normal,light));
    const material=Math.floor(q.uv[0][0]/64),transparent=material===5;
    for(const ids of [[0,1,2],[0,2,3]])triangles.push({p:ids.map(i=>points[i]),uv:ids.map(i=>q.uv[i]),shade,transparent});
  }
  triangles.sort((a,b)=>Number(a.transparent)-Number(b.transparent)||(b.p.reduce((s,p)=>s+p[2],0)-a.p.reduce((s,p)=>s+p[2],0)));
  for(const t of triangles){
    const[a,b,c]=t.p,minx=Math.max(0,Math.floor(Math.min(a[0],b[0],c[0]))),maxx=Math.min(W-1,Math.ceil(Math.max(a[0],b[0],c[0])));
    const miny=Math.max(0,Math.floor(Math.min(a[1],b[1],c[1]))),maxy=Math.min(H-1,Math.ceil(Math.max(a[1],b[1],c[1])));
    const den=(b[1]-c[1])*(a[0]-c[0])+(c[0]-b[0])*(a[1]-c[1]);if(Math.abs(den)<1e-7)continue;
    for(let y=miny;y<=maxy;y++)for(let x=minx;x<=maxx;x++){
      const l=((b[1]-c[1])*(x+.5-c[0])+(c[0]-b[0])*(y+.5-c[1]))/den;
      const m=((c[1]-a[1])*(x+.5-c[0])+(a[0]-c[0])*(y+.5-c[1]))/den,n=1-l-m;
      if(l<0||m<0||n<0)continue;const z=l*a[2]+m*b[2]+n*c[2],at=y*W+x;if(z>=depth[at])continue;
      const u=Math.max(0,Math.min(tex.w-1,Math.floor(l*t.uv[0][0]+m*t.uv[1][0]+n*t.uv[2][0]))),v=Math.max(0,Math.min(tex.h-1,Math.floor(l*t.uv[0][1]+m*t.uv[1][1]+n*t.uv[2][1])));
      const ti=(v*tex.w+u)*4,alpha=tex.pixels[ti+3]/255;if(!alpha)continue;
      const illumination=glow.pixels[ti+3]>0?1:t.shade;
      for(let ch=0;ch<3;ch++)pixels[at*4+ch]=Math.round(tex.pixels[ti+ch]*illumination*alpha+pixels[at*4+ch]*(1-alpha));
      if(!t.transparent)depth[at]=z;
    }
  }
}
draw([37,29,-90],[410,510],22);draw([-33,29,90],[1070,510],19.5);
function crc(b){let c=0xffffffff;for(const v of b){c^=v;for(let i=0;i<8;i++)c=(c>>>1)^((c&1)?0xedb88320:0);}return(c^0xffffffff)>>>0;}
const chunk=(t,d)=>{const a=Buffer.from(t),n=Buffer.alloc(4),c=Buffer.alloc(4);n.writeUInt32BE(d.length);c.writeUInt32BE(crc(Buffer.concat([a,d])));return Buffer.concat([n,a,d,c]);};
const ih=Buffer.alloc(13);ih.writeUInt32BE(W);ih.writeUInt32BE(H,4);ih[8]=8;ih[9]=6;
const scan=Buffer.alloc(H*(W*4+1));for(let y=0;y<H;y++)pixels.copy(scan,y*(W*4+1)+1,y*W*4,(y+1)*W*4);
fs.mkdirSync(path.dirname(out),{recursive:true});fs.writeFileSync(out,Buffer.concat([Buffer.from([137,80,78,71,13,10,26,10]),chunk('IHDR',ih),chunk('IDAT',deflateSync(scan)),chunk('IEND',Buffer.alloc(0))]));
console.log(`Rendered ${name} geometry front/back to ${out}; lighting is an offline approximation.`);
