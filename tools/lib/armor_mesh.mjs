// Authoring primitives for bone-bound polygon armor. Coordinates match Blockbench.
import {deflateSync} from 'node:zlib';
export const add=(a,b)=>a.map((v,i)=>v+b[i]),sub=(a,b)=>a.map((v,i)=>v-b[i]);
export const mul=(a,s)=>a.map(v=>v*s),dot=(a,b)=>a.reduce((s,v,i)=>s+v*b[i],0);
export const cross=(a,b)=>[a[1]*b[2]-a[2]*b[1],a[2]*b[0]-a[0]*b[2],a[0]*b[1]-a[1]*b[0]];
export const norm=a=>mul(a,1/(Math.hypot(...a)||1));
export const lerp=(a,b,t)=>a.map((v,i)=>v+(b[i]-v)*t);
export function rotate(v,deg=[0,0,0]){let[x,y,z]=v;for(let i=0;i<3;i++){const a=deg[i]*Math.PI/180,c=Math.cos(a),s=Math.sin(a);if(i===0)[y,z]=[c*y-s*z,s*y+c*z];if(i===1)[x,z]=[c*x+s*z,-s*x+c*z];if(i===2)[x,y]=[c*x-s*y,s*x+c*y];}return[x,y,z];}
export function png(w,h,p){
  const crc=b=>{let c=0xffffffff;for(const v of b){c^=v;for(let i=0;i<8;i++)c=(c>>>1)^((c&1)?0xedb88320:0);}return(c^0xffffffff)>>>0;};
  const chunk=(t,d)=>{const a=Buffer.from(t),n=Buffer.alloc(4),c=Buffer.alloc(4);n.writeUInt32BE(d.length);c.writeUInt32BE(crc(Buffer.concat([a,d])));return Buffer.concat([n,a,d,c]);};
  const ih=Buffer.alloc(13);ih.writeUInt32BE(w);ih.writeUInt32BE(h,4);ih[8]=8;ih[9]=6;
  const scan=Buffer.alloc(h*(w*4+1));for(let y=0;y<h;y++)p.copy(scan,y*(w*4+1)+1,y*w*4,(y+1)*w*4);
  return Buffer.concat([Buffer.from([137,80,78,71,13,10,26,10]),chunk('IHDR',ih),chunk('IDAT',deflateSync(scan)),chunk('IEND',Buffer.alloc(0))]);
}
export function mesh(b,name){const m={name,vertices:[],faces:[]};b.meshes.push(m);return m;}
export function face(m,points,mat=0,expected=null,uv=null){
  let normal=norm(cross(sub(points[1],points[0]),sub(points[2],points[0])));
  if(Math.hypot(...normal)<.5)return;
  uv=uv||points.map((p,i)=>[mat*64+8+([0,0,48,48][i]||0),8+([48,0,0,48][i]||0)]);
  if(expected&&dot(normal,expected)<0){points=[...points].reverse();uv=[...uv].reverse();normal=mul(normal,-1);}
  const ids=points.map(p=>{m.vertices.push(p.map(v=>+v.toFixed(5)));return m.vertices.length-1;});
  m.faces.push({vertices:ids,uv,normal:normal.map(v=>+v.toFixed(6))});
}
export function rect(w,h,r=.3){return[[-w/2+r,-h/2],[w/2-r,-h/2],[w/2,-h/2+r],[w/2,h/2-r],[w/2-r,h/2],[-w/2+r,h/2],[-w/2,h/2-r],[-w/2,-h/2+r]];}
// Convex plate, actual front/rear chamfers and a thin side wall; not stacked boxes.
export function plate(b,name,outline,z,depth,mat=0,edge=11,inset=.1){
  const m=mesh(b,name),n=outline.length,center=outline.reduce((a,p)=>add(a,p),[0,0]).map(v=>v/n);
  const rad=Math.min(...outline.map(p=>Math.hypot(...sub(p,center))));
  const inner=outline.map(p=>lerp(p,center,Math.min(.45,inset/rad)));
  const front=inner.map(p=>[...p,z]),shoulder=outline.map(p=>[...p,z+Math.min(depth*.45,inset)]),rear=outline.map(p=>[...p,z+depth]);
  for(let i=0;i<n;i++){
    const j=(i+1)%n;
    face(m,[[...center,z],front[i],front[j]],mat,[0,0,-1],[[mat*64+32,32],[mat*64+8+(inner[i][0]-center[0])/rad*18,32-(inner[i][1]-center[1])/rad*18],[mat*64+8+(inner[j][0]-center[0])/rad*18,32-(inner[j][1]-center[1])/rad*18]].map(([u,v])=>[Math.max(mat*64+1,Math.min(mat*64+62,u)),Math.max(1,Math.min(62,v))]));
    face(m,[front[i],shoulder[i],shoulder[j],front[j]],edge,[0,0,-1]);
    face(m,[shoulder[i],rear[i],rear[j],shoulder[j]],mat,[outline[i][0]-center[0],outline[i][1]-center[1],0]);
    face(m,[[...center,z+depth],rear[j],rear[i]],mat,[0,0,1]);
  }
  return m;
}
export function transform(m,pivot,rotation){m.vertices=m.vertices.map(p=>add(rotate(sub(p,pivot),rotation),pivot));m.faces.forEach(f=>f.normal=rotate(f.normal,rotation));return m;}
export function bevel(b,name,c,size,r=.25,mat=0,edge=11,rotation=[0,0,0]){
  const outline=rect(size[0],size[1],Math.min(r,size[0]*.2,size[1]*.2)).map(p=>[p[0]+c[0],p[1]+c[1]]);
  return transform(plate(b,name,outline,c[2]-size[2]/2,size[2],mat,edge,Math.min(r,size[2]*.4)),c,rotation);
}
export function tube(b,name,path,radii,mat=1,sides=6){
  const m=mesh(b,name),rings=[];
  for(let i=0;i<path.length;i++){
    const tangent=norm(sub(path[Math.min(i+1,path.length-1)],path[Math.max(0,i-1)]));
    const u=norm(cross(tangent,Math.abs(tangent[2])<.9?[0,0,1]:[0,1,0])),v=cross(tangent,u);
    const radius=Array.isArray(radii)?radii[i]:radii;
    rings.push(Array.from({length:sides},(_,j)=>add(path[i],add(mul(u,Math.cos(j*2*Math.PI/sides)*radius),mul(v,Math.sin(j*2*Math.PI/sides)*radius)))));
  }
  for(let i=0;i<path.length-1;i++)for(let j=0;j<sides;j++){
    const k=(j+1)%sides,p=[rings[i][j],rings[i+1][j],rings[i+1][k],rings[i][k]],mid=lerp(path[i],path[i+1],.5);
    const color=typeof mat==='function'?mat(i,j):mat;
    face(m,p,color,sub(p[0],mid));
  }
  for(const i of [0,path.length-1])for(let j=0;j<sides;j++)face(m,[path[i],rings[i][j],rings[i][(j+1)%sides]],typeof mat==='function'?mat(i,j):mat,sub(path[i],path[i===0?1:i-1]));
  return m;
}
export function torus(b,name,c,r,t,mat=1,n=48,sides=6,skip=()=>false){
  const m=mesh(b,name),at=(i,j)=>{const a=i*2*Math.PI/n,q=j*2*Math.PI/sides;return[c[0]+(r+t*Math.cos(q))*Math.cos(a),c[1]+(r+t*Math.cos(q))*Math.sin(a),c[2]+t*Math.sin(q)];};
  for(let i=0;i<n;i++)if(!skip(i))for(let j=0;j<sides;j++){
    const p=[at(i,j),at(i+1,j),at(i+1,j+1),at(i,j+1)],a=(i+.5)*2*Math.PI/n,q=(j+.5)*2*Math.PI/sides;
    face(m,p,typeof mat==='function'?mat(i,j):mat,[Math.cos(a)*Math.cos(q),Math.sin(a)*Math.cos(q),Math.sin(q)]);
  }
  return m;
}
export function jewel(b,name,c,w,h,d,mat=6,tilt=0){
  const m=tube(b,name,[[c[0],c[1]-h*.5,c[2]],[c[0],c[1]-h*.18,c[2]],[c[0],c[1]+h*.14,c[2]],[c[0],c[1]+h*.5,c[2]]],[.005,w*.5,w*.32,.003],(i,j)=>j===1||j===2?13:mat,5);
  m.vertices=m.vertices.map(p=>[p[0],p[1],c[2]+(p[2]-c[2])*d/w]);
  // Recompute normals after the nonuniform crystal depth scaling.
  m.faces.forEach(f=>{const p=f.vertices.map(i=>m.vertices[i]);f.normal=norm(cross(sub(p[1],p[0]),sub(p[2],p[0])));});
  return transform(m,c,[0,0,tilt]);
}
export function aura(b,c,r,mat=5){const m=mesh(b,'Halo suave');const p=[[c[0]-r,c[1]-r,c[2]],[c[0]-r,c[1]+r,c[2]],[c[0]+r,c[1]+r,c[2]],[c[0]+r,c[1]-r,c[2]]];face(m,p,mat,null,[[320,64],[320,0],[384,0],[384,64]]);face(m,[...p].reverse(),mat,null,[[384,64],[384,0],[320,0],[320,64]]);return m;}
export function star(b,c,r,back=false){
  const p=[[0,r],[.12,.2],[r*.6,0],[.12,-.14],[0,-r],[-.12,-.14],[-r*.6,0],[-.12,.2]];
  // Concave star uses separate triangles rather than a convex polygon fan.
  const m=mesh(b,'Fenda estelar');
  for(let i=0;i<p.length;i++)face(m,[c,add(c,[...p[i],0]),add(c,[...p[(i+1)%p.length],0])],i%2?4:13,[0,0,back?1:-1]);
  aura(b,[c[0],c[1],c[2]+(back?.06:-.06)],r*1.25);
}
