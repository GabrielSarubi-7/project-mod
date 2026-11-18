// Vazio sculpted revision: polygon plates and folded cloth, attached to the existing Gecko rig.
import fs from 'node:fs';
import path from 'node:path';
import {fileURLToPath} from 'node:url';
import {createHash} from 'node:crypto';
import {png,mesh,face,plate,rect,bevel,tube,torus,jewel,aura,star,add,sub,norm,cross,lerp} from './lib/armor_mesh.mjs';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'..'),assets=path.join(root,'src/main/resources/assets/etheris');
const write=(p,d)=>{fs.mkdirSync(path.dirname(p),{recursive:true});fs.writeFileSync(p,d);};
const json=(p,d)=>write(p,JSON.stringify(d,null,2)+'\n');
const uuid=s=>{const h=createHash('sha256').update('vazio-sculpt:'+s).digest('hex');return`${h.slice(0,8)}-${h.slice(8,12)}-4${h.slice(13,16)}-a${h.slice(17,20)}-${h.slice(20,32)}`;};
const colors=['#292331','#AD99B5','#1B1528','#05020A','#F0D6FF','#BD78EF','#A94CE8','#351C50','#63327B','#8C48A9','#DAC7E6','#67516F','#886794','#C897F6','#17121E','#362641'];
const atlas=Buffer.alloc(1024*64*4),glow=Buffer.alloc(atlas.length);
for(let y=0;y<64;y++)for(let x=0;x<1024;x++){
  const mat=Math.floor(x/64),u=x%64,base=[1,3,5].map(i=>parseInt(colors[mat].slice(i,i+2),16));
  const grain=(((u*31+y*17)%19)/19-.5)*3;
  let d=[1,10,11,12].includes(mat)?8*Math.sin(u/63*Math.PI)+10*(1-y/63)+grain:[0,2,14,15].includes(mat)?2*Math.sin(u*.25+y*.15)+grain*.4:0;
  let rgba=[...base.map(v=>Math.max(0,Math.min(255,Math.round(v+d)))),255];
  if(mat===3)rgba=[5,2,10,255];
  if(mat===7){const t=(y/63)**2.3,k=.85+.15*Math.cos(u/63*Math.PI*6);rgba=[...[27+82*t,18+25*t,41+101*t].map(v=>Math.round(v*k+grain*.5)),255];}
  if(mat===5){const r=Math.hypot((u-31.5)/31.5,(y-31.5)/31.5),a=r>=1?0:Math.round(125*Math.exp(-r*r*5)*(1-r));rgba=a?[202,122,255,a]:[0,0,0,0];}
  atlas.set(rgba,(y*1024+x)*4);if([4,5,6,13].includes(mat))glow.set(rgba,(y*1024+x)*4);
}
const texture=png(1024,64,atlas);write(path.join(assets,'textures/armor/vazio.png'),texture);write(path.join(assets,'textures/armor/vazio_glowmask.png'),png(1024,64,glow));
const bones=[];
const bone=(name,parent,pivot)=>{const b={name,...parent?{parent}:{},pivot,cubes:[],meshes:[]};bones.push(b);return b;};
const profile=(b,name,outline,z,d,mat=0,edge=11,r=.12)=>plate(b,name,outline,z,d,mat,edge,r);
const mirror=(p,s)=>p.map(([x,y])=>[s*x,y]);

const head=bone('armorHead',null,[0,24,0]);
// Eight-sided shell with a rounded top rim: no full-width rectangular side slabs.
const shell=mesh(head,'Casco envolvente octogonal');
const shellOutline=[[-3.7,-4.7],[3.7,-4.7],[4.85,-3.55],[4.95,2.95],[3.7,4.5],[-3.7,4.5],[-4.95,2.95],[-4.85,-3.55]];
const shellRings=[[24.0,1],[24.45,1],[31.9,1],[32.55,.975]].map(([y,k])=>shellOutline.map(([x,z])=>[x*k,y,z*k]));
for(let row=0;row<3;row++)for(let i=0;i<8;i++){const j=(i+1)%8;face(shell,[shellRings[row][i],shellRings[row+1][i],shellRings[row+1][j],shellRings[row][j]],row===2?11:0,[shellOutline[i][0],0,shellOutline[i][1]]);}
for(let i=0;i<8;i++){face(shell,[[0,32.55,0],shellRings[3][i],shellRings[3][(i+1)%8]],0,[0,1,0]);face(shell,[[0,24,0],shellRings[0][i],shellRings[0][(i+1)%8]],14,[0,-1,0]);}
for(const y of [27.2,30.4])tube(head,'Filete posterior',[[4.96,y,.6],[4.98,y,2.92],[3.72,y,4.55],[0,y,4.6],[-3.72,y,4.55],[-4.98,y,2.92],[-4.96,y,.6]],.055,12,4);
profile(head,'Cavidade da mascara',[[-3.55,30.7],[-2.7,31.45],[0,31.25],[2.7,31.45],[3.55,30.7],[3.4,26.0],[2.25,24.65],[-2.25,24.65],[-3.4,26.0]],-4.73,.12,3,14,.08);
for(const s of [-1,1]){
  profile(head,'Sobrancelha esculpida',mirror([[.25,31.7],[1.9,32.05],[3.9,31.55],[4.2,30.65],[3.2,30.85],[1.7,31.2]],s),-4.96,.45,1,10,.12);
  tube(head,'Filete da viseira',[[s*3.85,30.6,-4.91],[s*3.65,28,-4.95],[s*3.5,26,-4.94],[s*2.4,24.55,-4.76],[s*.75,24.2,-4.6]],[.18,.16,.13,.11,.04],11,6);
  profile(head,'Guarda temporal',mirror([[3.7,31.35],[4.6,30.9],[4.8,27.25],[4.0,25.7],[3.6,26.3]],s),-3.85,.36,11,1,.14);
  // Curved, tapered horn sections replace the rectangular antenna silhouette.
  const offset=s<0?0:-.7;
  tube(head,'Coroa em lamina curva',[[s*4.0,31.15,.15],[s*4.15,32.9,.1],[s*5.15,34.4,-.15],[s*4.95,36+offset,-.28],[s*5.6,37.1+offset,-.35]],[.68,.57,.45,.25,.018],(i,j)=>j===0?10:j===1?1:j===2&&i>1?6:11,6);
  tube(head,'Espora da coroa',[[s*4.65,33.0,-.1],[s*5.7,33.6,-.05],[s*6.05,35.15,-.08]],[.29,.25,.01],(i,j)=>j===1?13:12,5);
  jewel(head,'Cristal temporal',[s*4.8,29.25,-1.1],.52,2.15,.42,6,-s*12);
  for(let i=0;i<3;i++)profile(head,'Gravura na fonte',mirror([[3.91,29.8-i*.7],[4.25,30.0-i*.7],[4.24,29.86-i*.7],[3.9,29.67-i*.7]],s),-3.98,.03,12,12,.015);
}
profile(head,'Cume em ponta',[[0,33.15],[.95,31.7],[.35,30.9],[0,30.55],[-.35,30.9],[-.95,31.7]],-5.12,.43,1,10,.12);
jewel(head,'Entalhe do cume',[0,31.7,-5.29],.25,1.35,.13,13,0);
const visage=bone('void_face','armorHead',[0,28.4,-4.98]);star(visage,[0,28.4,-5.0],1.45);
for(const[name,c,w,h,a]of [['crown_shard_right',[-5.83,38.18,-.4],.65,1.85,-25],['crown_shard_left',[5.5,37.7,-.35],.53,1.5,27],['crown_shard_rear',[-.7,34.5,3.8],.36,1.05,-18]]){
  const b=bone(name,'armorHead',c);jewel(b,'Fragmento suspenso',c,w,h,w*.55,6,a);
}

const body=bone('armorBody',null,[0,24,0]);
bevel(body,'Base da couraca',[0,18,.05],[8.75,12.2,5.45],.46,0,14);
for(const s of [-1,1]){
  profile(body,'Peitoral curvo',mirror([[.45,22.8],[3.8,23.65],[4.35,21.0],[3.6,18.25],[1.35,17.7],[.7,19.2]],s),-3.0,.48,0,11,.23);
  tube(body,'Gola de prata',[[s*.55,22.8,-3.3],[s*2.6,23.7,-3.2],[s*4.0,23.4,-2.8]],[.12,.22,.04],1,6);
  for(let i=0;i<3;i++){
    const y=18.2-i*1.55;
    profile(body,'Lamela abdominal',mirror([[.8,y+.7],[3.75,y+1.1],[3.8,y-.05],[1.3,y-.8],[.6,y-.4]],s),-3.12-i*.025,.25,0,11,.1);
    tube(body,'Aresta da lamela',[[s*1.2,y-.6,-3.22-i*.025],[s*2.6,y-.1,-3.3-i*.025],[s*3.6,y+.25,-3.29-i*.025]],[.035,.06,.025],12,4);
  }
  profile(body,'Cinto afilado',mirror([[.25,12.7],[3.8,13.8],[4.4,13.4],[3.7,12.9],[1.0,11.9]],s),-3.38,.42,1,10,.1);
}
jewel(body,'Fecho da cintura',[0,12.65,-3.65],.85,1.55,.3,13,0);
const core=bone('void_core','armorBody',[0,20.25,-3.8]);
const disk=Array.from({length:48},(_,i)=>[1.86*Math.cos(i*Math.PI/24),20.25+1.86*Math.sin(i*Math.PI/24)]);
profile(core,'Disco de ausencia',disk,-4.02,.15,3,3,.015);
torus(core,'Engaste continuo',[0,20.25,-4.08],2.02,.23,(i,j)=>j===4?10:j===3?1:11,48,6);
torus(core,'Borda de luz',[0,20.25,-4.28],1.88,.075,6,48,4);
const iris=bone('void_iris','void_core',[0,20.25,-4.33]);
torus(iris,'Iris interrompida',[0,20.25,-4.33],1.75,.035,4,48,4,i=>i%16>10);
for(let i=0;i<8;i++){
  const a=i*Math.PI/4,dx=Math.cos(a),dy=Math.sin(a),r=i%2===0?2.7:2.42;
  const p=[[2.15,-.25],[r,.0],[2.15,.25],[1.99,0]].map(([u,v])=>[u*dx-v*dy,20.25+u*dy+v*dx]);
  profile(core,'Petala do eclipse',p,-4.20,.20,i%2?11:1,10,.07);
  if(i%2===0)jewel(core,'Gema do aro',[dx*2.17,20.25+dy*2.17,-4.38],.22,.43,.13,13,-i*45);
}
for(let i=0;i<24;i++){
  const a=i*Math.PI/12,r=2.02,c=[r*Math.cos(a),20.25+r*Math.sin(a),-4.34];
  tube(core,'Runa radial',[[c[0]-.07*Math.cos(a),c[1]-.07*Math.sin(a),c[2]],[c[0]+.07*Math.cos(a),c[1]+.07*Math.sin(a),c[2]]],.024,i%3?12:13,4);
}

function clothGrid(b,name,surface,rows,cols,uvFn,skip=()=>false,outward=[0,0,-1]){
  const m=mesh(b,name);
  for(let y=0;y<rows;y++)for(let x=0;x<cols;x++){
    if(skip(x,y))continue;
    const ij=[[x/cols,y/rows],[(x+1)/cols,y/rows],[(x+1)/cols,(y+1)/rows],[x/cols,(y+1)/rows]];
    const p=ij.map(([u,v])=>surface(u,v)),uv=ij.map(([u,v])=>uvFn(u,v));
    // Subdivide warped quads into real triangles so normals and silhouettes agree in game/editor.
    for(const ids of [[0,1,2],[0,2,3]]){
      const points=ids.map(i=>p[i]),coords=ids.map(i=>uv[i]);
      face(m,points,7,outward,coords);
      face(m,points.map(p=>p.map((v,i)=>v-outward[i]*.065)),7,outward.map(v=>-v),coords);
    }
  }
  return m;
}
for(const s of [-1,1]){
  let parent='armorBody';const side=s<0?'right':'left';
  for(let i=0;i<3;i++){
    const y=12.7-i*3.5,id=i?'robe_'+side+'_'+i:'robe_'+side,b=bone(id,parent,[s*4.7,y,0]);parent=id;
    clothGrid(b,'Saia lateral pregueada',(u,v)=>[s*(4.55+i*.10+.13*Math.cos(u*Math.PI*4)+.06*v),y-v*3.58,-2.75+u*5.5],3,5,(u,v)=>[448+u*63,10+(i+v)*15],()=>false,[s,0,0]);
    const x=s*(4.68+i*.09);
    profile(b,'Escama da saia',mirror([[4.4+i*.09,y-.2],[5.15+i*.09,y-.6],[5.0+i*.09,y-2.9],[4.62+i*.09,y-3.35],[4.32+i*.09,y-1.6]],s),-2.83,.22,i===0?11:0,1,.10);
    tube(b,'Fio da saia',[[x,y-.6,-3.0],[x+s*.24,y-1.85,-3.02],[x+s*.02,y-3.15,-3.0]],[.045,.035,.012],i===2?13:12,4);
  }
}
let parent='armorBody';
for(let i=0;i<3;i++){
  const y=12.25-i*3.45,z=-3.6-i*.03,id=i?'stole_front_'+i:'stole_front',b=bone(id,parent,[0,y,z]);parent=id;
  const surface=(u,v)=>{const w=1.65-.15*(i+v);return[(u*2-1)*w,y-v*3.49-(i===2?v**4*(1-Math.abs(u*2-1))*1.25:0),z+.09*Math.cos(u*Math.PI*4)];};
  clothGrid(b,'Tabardo afilado',surface,4,6,(u,v)=>[448+u*63,10+(i+v)*17]);
  for(const u of [0,1])tube(b,'Bordado do tabardo',[surface(u,0),surface(u,.5),surface(u,1)].map(p=>[p[0],p[1],p[2]-.06]),[.045,.035,.018],i===2?13:12,4);
  if(i===1)star(b,[0,y-2.2,z-.2],.65);
}

const cape=bone('cape_root','armorBody',[0,23,4.5]);
const capePoint=(column,u,tier,v)=>{
  const col=column==='right'?-1:column==='left'?1:0,depth=(tier+v)/4;
  const x=(col*3.65+(u-.5)*3.84)*(.84+.16*depth);
  let y=23-(tier+v)*4.65;
  if(tier===3){
    const hem=column==='right'?[.12,.65,1.6,.4,.2,.85,.22]:[.35,1.25,.2,.15,.8,.4,.08];
    const index=Math.min(5,Math.floor(u*6)),blend=u*6-index;
    y-=v**4*(column==='center'?(1.8*(1-Math.abs(u*2-1))):hem[index]*(1-blend)+hem[index+1]*blend);
  }
  const z=4.72-.035*x*x+.13*Math.cos(x*2.8)+depth*.3+(col===0?.025:0);
  return[x,y,z];
};
for(const column of ['right','center','left']){
  let parent=cape.name;const x=column==='right'?-3.65:column==='left'?3.65:0;
  for(let tier=0;tier<4;tier++){
    const b=bone(`cape_${column}_${tier}`,parent,[x,23-tier*4.65,4.5]);parent=b.name;
    clothGrid(b,'Manto com pregas continuas',(u,v)=>capePoint(column,u,tier,v),5,6,(u,v)=>[448+u*63,(tier+v)*15.7],(cx,cy)=>tier===3&&((column==='right'&&cx===1&&cy===3)||(column==='left'&&cx===4&&cy===2)),[0,0,1]);
    if(column!=='center'){
      const u=column==='right'?0:1;
      tube(b,'Debrum do manto',[0,.25,.5,.75,1].map(v=>{const p=capePoint(column,u,tier,v);return[p[0],p[1],p[2]+.055];}),tier===3?.035:.025,tier===3?13:12,4);
    }
    if(tier===3){
      for(let i=0;i<6;i++){
        const a=capePoint(column,i/6,tier,1),bpt=capePoint(column,(i+1)/6,tier,1);
        tube(b,'Bainha em fio de luz',[a,bpt].map(p=>[p[0],p[1],p[2]+.045]),.035,i%2?13:6,4);
      }
    }
  }
}
const back=bone('back_eclipse','cape_center_0',[0,20.5,5.02]);
torus(back,'Eclipse bordado',[0,20.4,5.02],1.28,.045,12,40,4);
star(back,[0,19.05,5.07],.8,true);
for(const s of [-1,1])tube(back,'Filigrana astral',[[s*.5,21.7,5.04],[s*1.8,22.05,4.99],[s*3.0,21.5,4.85]],[.035,.045,.01],12,4);

for(const[part,s]of [['armorRightArm',-1],['armorLeftArm',1]]){
  const arm=bone(part,null,[s*5,22,0]),cx=s*6;
  bevel(arm,'Base braquial',[cx,17.2,0],[4.3,10.0,4.5],.28,0,14);
  // A curved shell with tapered layered scales, rather than rectangular shoulder frames.
  profile(arm,'Ombreira abaulada',mirror([[4.15,23.2],[5.0,24.15],[7.2,24.0],[8.8,22.8],[8.5,20.7],[7.7,19.8],[5.3,20.8]],s),-2.8,5.6,0,11,.3);
  tube(arm,'Borda da ombreira',[[s*4.6,23.35,-2.95],[s*5.3,23.95,-2.97],[s*7.1,23.83,-2.97],[s*8.5,22.7,-2.95],[s*8.3,21.3,-2.96]],[.11,.16,.17,.12,.035],1,6);
  for(let i=0;i<2;i++)profile(arm,'Escama do ombro',mirror([[6.15+i*.25,21.5-i*1.15],[8.15+i*.15,21.7-i*1.15],[8.75,19.85-i*1.15],[7.35,20.0-i*1.15],[6.0,20.7-i*1.15]],s),-2.96-i*.06,.22,i?11:0,1,.12);
  star(arm,[s*6.65,22.2,-3.18],.58);
  bevel(arm,'Manopla facetada',[cx,12.75,-.08],[4.5,3.5,4.65],.33,0,11);
  profile(arm,'Escudo do antebraco',mirror([[4.3,15.8],[6.0,16.25],[7.65,15.6],[7.2,12.2],[6.0,10.9],[4.7,12.25]],s),-2.65,.38,11,1,.18);
  tube(arm,'Sulco da manopla',[[cx,15.2,-2.94],[cx,13.65,-3.0],[cx,11.7,-2.93]],[.045,.065,.01],12,4);
  const fin=bone('sleeve_'+(s<0?'right':'left'),part,[s*8.1,15.1,1]);
  profile(fin,'Lamina suspensa',mirror([[8.15,15.5],[8.65,14.6],[8.75,12.0],[8.1,10.8],[7.8,12.6]],s),.0,1.3,11,1,.13);
  jewel(fin,'Gume de cristal',[s*8.35,11.7,-.12],.32,1.65,.24,6,-s*8);
}
for(const[leg,boot,s]of [['armorRightLeg','armorRightBoot',-1],['armorLeftLeg','armorLeftBoot',1]]){
  const l=bone(leg,null,[s*2,12,0]);
  bevel(l,'Perneira ajustada',[s*2.07,8.6,0],[4.14,7.3,4.5],.12,0,14);
  profile(l,'Joelho em escudo',mirror([[.35,6.65],[1.95,7.35],[3.65,6.65],[3.5,5.7],[2,4.85],[.5,5.65]],s),-2.5,.45,11,1,.16);
  const b=bone(boot,null,[s*2,12,0]);
  bevel(b,'Greva esculpida',[s*2.1,2.75,-.3],[4.2,5.5,5.0],.16,0,11);
  bevel(b,'Peito do pe',[s*2.15,.95,-.95],[4.3,1.85,6.0],.25,11,1);
  profile(b,'Ponta da greva',mirror([[.35,4.8],[2,5.2],[3.65,4.8],[3.6,2.1],[2,.9],[.4,2.15]],s),-2.98,.30,0,1,.12);
  jewel(b,'Gema da greva',[s*2,4.5,-3.15],.35,.9,.16,13);
}

const keys=list=>Object.fromEntries(list.map(([t,v])=>[t.toFixed(2),v]));
const sample=fn=>keys(Array.from({length:49},(_,i)=>[i/4,fn(i/4)]));
const animation={loop:true,animation_length:12,bones:{
  void_iris:{rotation:keys([[0,[0,0,0]],[6,[0,0,180]],[12,[0,0,360]]])},
  void_core:{scale:sample(t=>{const s=1+.022*Math.sin(t*Math.PI/2);return[s,s,s];})},
  void_face:{scale:sample(t=>{const s=1+.05*Math.sin(t*Math.PI/3);return[s,s,1];})}
}};
for(const[i,name]of ['crown_shard_right','crown_shard_left','crown_shard_rear'].entries())animation.bones[name]={position:sample(t=>[.08*Math.sin(t*Math.PI/3+i),.25*Math.sin(t*Math.PI/3+i),.06*Math.cos(t*Math.PI/3+i)]),rotation:sample(t=>[3*Math.sin(t*Math.PI/3+i),4*Math.cos(t*Math.PI/3+i),2*Math.sin(t*Math.PI/3+i)])};
for(const b of bones.filter(b=>/^(cape_|robe_|stole_|sleeve_)/.test(b.name)))animation.bones[b.name]={rotation:sample(t=>[1.5*Math.sin(t*Math.PI/3+bones.indexOf(b)*.35),0,.8*Math.cos(t*Math.PI/3+bones.indexOf(b)*.35)])};
// Weld coincident positions within each authored piece for useful mesh editing.
for(const b of bones)for(const m of b.meshes){const map=new Map(),positions=[],ids=m.vertices.map(p=>{const key=p.map(v=>v.toFixed(5)).join(',');if(!map.has(key)){map.set(key,positions.length);positions.push(p);}return map.get(key);});m.faces.forEach(f=>f.vertices=f.vertices.map(i=>ids[i]));m.vertices=positions;}
const skeleton=bones.map(({meshes,...b})=>b);
json(path.join(assets,'geo/armor/vazio.geo.json'),{format_version:'1.12.0','minecraft:geometry':[{description:{identifier:'geometry.vazio_regalia',texture_width:1024,texture_height:64,visible_bounds_width:4,visible_bounds_height:5,visible_bounds_offset:[0,1.7,0]},bones:skeleton}]});
json(path.join(assets,'armor_mesh/vazio.mesh.json'),{format_version:1,texture_size:[1024,64],bones:bones.map(({name,meshes})=>({name,meshes}))});
json(path.join(assets,'animations/armor/vazio.animation.json'),{format_version:'1.8.0',animations:{'animation.vazio.eclipse':animation}});
const elements=bones.flatMap(b=>b.meshes.map((m,i)=>({uuid:uuid(b.name+':'+i),type:'mesh',name:m.name,origin:[0,0,0],rotation:[0,0,0],vertices:Object.fromEntries(m.vertices.map((p,i)=>['v'+i,p])),faces:Object.fromEntries(m.faces.map((f,i)=>['f'+i,{vertices:f.vertices.map(v=>'v'+v),uv:Object.fromEntries(f.vertices.map((v,j)=>['v'+v,f.uv[j]])),texture:0}]))})));
const groups=bones.map(b=>({uuid:uuid(b.name),name:b.name,origin:b.pivot,rotation:[0,0,0],export:true,isOpen:false}));
const outliner=b=>({uuid:uuid(b.name),isOpen:false,children:[...b.meshes.map((m,i)=>uuid(b.name+':'+i)),...bones.filter(child=>child.parent===b.name).map(outliner)]});
const animators=Object.fromEntries(Object.entries(animation.bones).map(([name,channels])=>[uuid(name),{name,type:'bone',keyframes:Object.entries(channels).flatMap(([channel,frames])=>Object.entries(frames).map(([t,val])=>({uuid:uuid(name+channel+t),channel,time:Number(t),interpolation:'linear',data_points:[{x:String(val[0]),y:String(val[1]),z:String(val[2])}]})))}]));
json(path.join(root,'art/blockbench/vazio_regalia.bbmodel'),{meta:{format_version:'5.0',model_format:'free',box_uv:false},name:'Vazio - Coroa da Ausencia - Esculpida',model_identifier:'vazio_regalia',resolution:{width:1024,height:64},elements,groups,outliner:bones.filter(b=>!b.parent).map(outliner),textures:[{uuid:uuid('texture'),id:'0',name:'vazio.png',width:1024,height:64,uv_width:1024,uv_height:64,internal:true,mode:'bitmap',source:'data:image/png;base64,'+texture.toString('base64')}],animations:[{uuid:uuid('animation'),name:'animation.vazio.eclipse',loop:'loop',length:12,animators}]});
const icons={helmet:[' P       P ',' SP     SP ',' SS SSS SS ','  SSSSSSS  ','  SKKLKKS  ','  SKLLLKS  ','  SKKLKKS  ','   SKKKS   ','    SSS    '],chestplate:[' SSS   SSS ','SSKSSSSSKSS',' KKKSL S K ','  KSLKLSK  ','  KKSL SK  ','  SKKKKKS  ','   SKKS    ','   PPKPP   ','    PPP    '],leggings:[' SSSSSSSSS ',' SKKKKKKKS ','  KKK KKK  ','  KKK KKK  ','  PKK KKP  ','  SSS SSS  ','  PPP PPP  '],boots:['  SS   SS  ',' SKKS SKKS ',' SKKS SKKS ',' SKKS SKKS ',' SSSS SSSS ',' SSSS SSSS ']};
for(const[part,rows]of Object.entries(icons)){const p=Buffer.alloc(16*16*4),lookup={K:0,S:1,P:6,L:4};rows.forEach((row,y)=>[...row].forEach((ch,x)=>{if(lookup[ch]!==undefined)p.set([...([1,3,5].map(i=>parseInt(colors[lookup[ch]].slice(i,i+2),16))),255],((y+3)*16+x+2)*4);}));write(path.join(assets,`textures/item/vazio_${part}.png`),png(16,16,p));json(path.join(assets,`models/item/vazio_${part}.json`),{parent:'minecraft:item/generated',textures:{layer0:`etheris:item/vazio_${part}`}});}
console.log(`Vazio sculpted: ${elements.length} pieces, ${bones.length} bones, ${bones.flatMap(b=>b.meshes).reduce((n,m)=>n+m.faces.length,0)} polygon faces; same Gecko rig, real beveled/tapered surfaces.`);
