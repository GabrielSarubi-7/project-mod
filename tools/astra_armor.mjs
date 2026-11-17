// Astra: the Celestial Oracle. Authored cuboid geometry, textures and animation keys.
// Bedrock/GeckoLib coordinates: feet at y=0, face towards -Z, head pivot at y=24.
import fs from 'node:fs';
import path from 'node:path';
import {fileURLToPath} from 'node:url';
import {deflateSync} from 'node:zlib';
import {createHash} from 'node:crypto';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'..');
const assets=path.join(root,'src/main/resources/assets/etheris');
const write=(p,data)=>{fs.mkdirSync(path.dirname(p),{recursive:true});fs.writeFileSync(p,data);};
const json=(p,data)=>write(p,JSON.stringify(data,null,2)+'\n');
const uuid=s=>{const h=createHash('sha256').update('astra:'+s).digest('hex');return `${h.slice(0,8)}-${h.slice(8,12)}-4${h.slice(13,16)}-a${h.slice(17,20)}-${h.slice(20,32)}`;};
function crc(b){let c=0xffffffff;for(const v of b){c^=v;for(let i=0;i<8;i++)c=(c>>>1)^((c&1)?0xedb88320:0);}return(c^0xffffffff)>>>0;}
function png(w,h,p){const chunk=(t,d)=>{const a=Buffer.from(t),n=Buffer.alloc(4),c=Buffer.alloc(4);n.writeUInt32BE(d.length);c.writeUInt32BE(crc(Buffer.concat([a,d])));return Buffer.concat([n,a,d,c]);};const ih=Buffer.alloc(13);ih.writeUInt32BE(w);ih.writeUInt32BE(h,4);ih[8]=8;ih[9]=6;const scan=Buffer.alloc(h*(w*4+1));for(let y=0;y<h;y++)p.copy(scan,y*(w*4+1)+1,y*w*4,(y+1)*w*4);return Buffer.concat([Buffer.from([137,80,78,71,13,10,26,10]),chunk('IHDR',ih),chunk('IDAT',deflateSync(scan)),chunk('IEND',Buffer.alloc(0))]);}
const colors=['#E8DFD5','#D5A354','#29234E','#111329','#FFF0B8','#B4A4C4','#AA8FE0','#866037'];
const atlas=Buffer.alloc(512*64*4),glow=Buffer.alloc(atlas.length);
for(let y=0;y<64;y++)for(let x=0;x<512;x++){
  const mat=Math.floor(x/64),u=x%64,base=[1,3,5].map(i=>parseInt(colors[mat].slice(i,i+2),16));
  let d=mat===0?(y%16===0?-10:u%16===0?-5:0):mat===1?(y%8===0?27:y%8===7?-21:0):mat===2?((u*7+y*13)%97===0?12:0):0;
  let rgba=[...base.map(v=>Math.max(0,Math.min(255,v+d))),255];
  if(mat===5){
    const r=Math.hypot((u-31.5)/31.5,(y-31.5)/31.5);
    const a=r>=1?0:Math.round(135*Math.exp(-r*r*4.5)*(1-r));
    rgba=a?[218,194,255,a]:[0,0,0,0];
  }
  atlas.set(rgba,(y*512+x)*4);
  if(mat===4||mat===5||mat===6)glow.set(rgba,(y*512+x)*4);
}
const texture=png(512,64,atlas);write(path.join(assets,'textures/armor/astra.png'),texture);write(path.join(assets,'textures/armor/astra_glowmask.png'),png(512,64,glow));
const bones=[];
function bone(name,parent,pivot,rotation){const b={name,...parent?{parent}:{},pivot,...rotation?{rotation}:{},cubes:[]};bones.push(b);return b;}
function box(b,name,p,size,mat=0,rotation=null,pivot=null){const c={origin:p,size,uv:[mat*64,0],...rotation?{rotation,pivot:pivot||p.map((v,i)=>v+size[i]/2)}:{}};b.cubes.push({...c,_name:name});return c;}
function centered(b,name,c,size,mat,rot){return box(b,name,c.map((v,i)=>v-size[i]/2),size,mat,rot,c);}
function ring(b,c,r,plane='xz',n=16,thick=.42,mat=1){
  for(let i=0;i<n;i++){
    const a=i*2*Math.PI/n,deg=i*360/n,len=2*r*Math.tan(Math.PI/n)+.03;
    const p=plane==='xz'?[c[0]+r*Math.cos(a),c[1],c[2]+r*Math.sin(a)]:[c[0]+r*Math.cos(a),c[1]+r*Math.sin(a),c[2]];
    centered(b,'Segmento orbital '+i,p,[len,thick,thick],mat,plane==='xz'?[0,-deg-90,0]:[0,0,deg+90]);
  }
}
function star(b,c,r,mat=4,facing=-1){centered(b,'Estrela vertical',c,[.42,r*2,.35],mat);centered(b,'Estrela horizontal',c,[r*1.5,.42,.36],mat);centered(b,'Lapidacao da estrela',c,[r*.62,r*.62,.4],1,[0,0,45]);centered(b,'Luz central',[c[0],c[1],c[2]+facing*.23],[.38,.38,.2],4,[0,0,45]);centered(b,'Aura estelar',[c[0],c[1],c[2]+facing*.48],[r*4,r*4,.02],5);}
const head=bone('armorHead',null,[0,24,0]);
box(head,'Fundo do capuz',[-4.8,23.6,3.7],[9.6,9.8,1.2]);box(head,'Coroa de porcelana',[-4.8,32,-4.5],[9.6,1.4,8.2]);
for(const s of [-1,1]){
  box(head,'Lateral do capuz',[s<0?-4.9:3.7,24.2,-4.6],[1.2,8.2,8.4]);
  box(head,'Borda dourada do capuz',[s<0?-4.9:4.15,24,-5],[.75,7.6,.65],1);
  centered(head,'Dobra inclinada',[s*3.6,24.4,-2.4],[2.5,1.4,5],0,[0,0,-s*22.5]);
  centered(head,'Dobra da testa',[s*3,31.8,-4.7],[3.5,.8,.8],1,[0,0,s*22.5]);
}
box(head,'Sombra da mascara',[-3.7,24.4,-4.9],[7.4,7.2,.6],3);
box(head,'Mascara lisa de porcelana',[-2.9,25.2,-5.22],[5.8,6.2,.6],0);
centered(head,'Queixo lapidado',[0,25.3,-5.25],[4.5,1.2,.65],0);
centered(head,'Insignia da testa',[0,32.2,-5.12],[1.25,2.6,.65],1,[0,0,45]);
const halo=bone('halo_orbit','armorHead',[0,31,0],[0,0,-8]);ring(halo,[0,31,0],10,'xz',24,.55);
for(let i=0;i<8;i++){const a=i*Math.PI/4;centered(halo,'Engaste do halo',[Math.cos(a)*10,31,Math.sin(a)*10],[.7,.9,.7],i%2?0:6);}
star(halo,[10,31,0],1.2);
const meridian=bone('halo_meridian','armorHead',[0,32,1.4],[0,0,12]);ring(meridian,[0,32,1.4],6.7,'xy',16,.28);star(meridian,[0,39,1.4],1.8);
for(const s of [-1,1]){
  const tal=bone(s<0?'talisman_right':'talisman_left','armorHead',[s*9.5,30,-1]);
  for(let k=0;k<3;k++)centered(tal,'Elo dourado',[s*9.5,29.1-k*.7,-1],[.3,.9,.3],1,k%2?[0,45,0]:null);
  box(tal,'Carta violeta',[s*9.5-1,22.6,-1.25],[2,4.5,.45],2);
  for(const x of [-1.15,1])box(tal,'Moldura vertical',[s*9.5+x,22.4,-1.4],[.2,4.9,.7],1);
  for(const y of [22.4,27.1])box(tal,'Moldura horizontal',[s*9.5-1.15,y,-1.4],[2.35,.2,.7],1);
  star(tal,[s*9.5,24.8,-1.55],.9);centered(tal,'Gota do talisma',[s*9.5,21.9,-1],[.65,.9,.5],1,[0,0,45]);
}
const body=bone('armorBody',null,[0,24,0]);
box(body,'Tunica violeta',[-4.4,11.8,-2.65],[8.8,12.2,5.3],2);
box(body,'Gola de marfim',[-4.7,22.8,-2.9],[9.4,1.1,5.8],0);
for(const s of [-1,1]){box(body,'Estola clara',[s<0?-4.45:2.55,12.3,-3.1],[1.9,10.8,.7]);box(body,'Friso da estola',[s<0?-2.75:2.35,12.4,-3.55],[.4,10.7,.4],1);}
const crest=bone('chest_astrolabe','armorBody',[0,20.5,-3.7]);ring(crest,[0,20.5,-3.7],2.6,'xy',16,.4);ring(crest,[0,20.5,-3.7],1.7,'xy',12,.22);star(crest,[0,20.5,-4],2.2);
box(body,'Cinto cerimonial',[-4.6,12,-3],[9.2,1,6],1);centered(body,'Fecho lunar',[0,12.4,-3.4],[1.4,1.4,.5],0,[0,0,45]);
// Thin cloth sections bend at their own seams instead of moving as solid hip plates.
for(const s of [-1,1]){
  const side=s<0?'right':'left';let parent='armorBody';
  for(let tier=0;tier<3;tier++){
    const y=13-tier*3.65,x=s*4.9,name=tier===0?'robe_'+side:'robe_'+side+'_'+tier;
    const skirt=bone(name,parent,[x,y,0]);parent=name;
    centered(skirt,'Tecido lateral de marfim',[x,y-1.87,0],[.55,3.85,5.6],0);
    for(const z of [-2.75,2.6])centered(skirt,'Costura lateral',[x,y-1.87,z],[.62,3.85,.18],1);
    if(tier===2)centered(skirt,'Bainha de ouro',[x,y-3.65,0],[.62,.28,5.65],1);
  }
}
for(const [name,z] of [['front',-3.4],['back',3.4]]){
  const panels=[];let parent='armorBody';
  for(let tier=0;tier<3;tier++){
    const id=tier===0?'stole_'+name:'stole_'+name+'_'+tier,y=12.5-tier*3.65;
    const p=bone(id,parent,[0,y,z]);parent=id;panels.push(p);
    box(p,'Tecido astronomico',[-2.5,y-3.8,z],[5,3.85,.22],2);
    for(const x of [-2.6,2.3])box(p,'Bordado de ouro',[x,y-3.8,z-.04],[.3,3.85,.3],1);
  }
  const panel=panels[2];
  centered(panel,'Ponta da estola',[0,2.6,z+.2],[3.5,3.5,.5],2,[0,0,45]);
  for(const s of [-1,1])centered(panel,'Borda da ponta',[s*1.3,1.3,z+.2],[3.6,.3,.65],1,[0,0,s*45]);
  for(const [i,y] of [10.5,8.5,6,3.5].entries()){
    const segment=panels[Math.min(2,Math.floor((12.5-y)/3.65))];
    const r=[.35,.55,.75,.4][i];centered(segment,'Fase lunar',[0,y,z+(name==='front'?-.25:.4)],[r*1.7,r*1.7,.14],1,[0,0,45]);
    if(i===1)centered(segment,'Sombra crescente',[.25,y+.1,z+(name==='front'?-.35:.5)],[.6,.7,.15],2,[0,0,45]);
  }star(panel,[0,1.5,z+(name==='front'?-.45:.85)],.8,4,name==='front'?-1:1);
}
// Three neighboring strips, each with four joints: a draping violet cloak with a gold hem.
const cape=bone('cape_root','armorBody',[0,23,4.8]);
for(const s of [-1,1])centered(body,'Fecho da capa',[s*4,22.8,3.5],[1.3,1.3,.5],1,[0,0,45]);
for(const [column,x] of [['right',-3.8],['center',0],['left',3.8]]){
  let parent=cape.name;
  for(let tier=0;tier<4;tier++){
    const y=23-tier*4.85,name=`cape_${column}_${tier}`,p=bone(name,parent,[x,y,4.8]);parent=name;
    // Small overlapping seams cover the gap between neighboring articulated strips.
    box(p,'Veludo violeta da capa',[x-2.05,y-4.95,column==='center'?4.84:4.8],[4.1,5.05,.24],2);
    // A narrow ivory lining and an antique-gold outer border keep the Astra palette readable.
    if(column!=='center'){
      const edge=x+(column==='left'?1.55:-1.88);
      box(p,'Forro de marfim',[edge,y-4.95,4.75],[.35,5.05,.34],0);
      box(p,'Borda dourada da capa',[edge,y-4.95,5.10],[.18,5.05,.13],1);
    }
    if(tier===3)box(p,'Bainha da capa',[x-2.05,y-4.9,column==='center'?4.78:4.74],[4.1,.3,.4],1);
    if(column==='center'&&tier===1)star(p,[0,y-2.6,5.35],1.25,4,1);
    if(column==='center'&&tier===2)for(const dy of [1.1,3.1])centered(p,'Constelacao bordada',[0,y-dy,5.15],[.55,.55,.12],1,[0,0,45]);
  }
}
for(const [part,s] of [['armorRightArm',-1],['armorLeftArm',1]]){
  const arm=bone(part,null,[s*5,22,0]);const cx=s*6;
  centered(arm,'Manga cerimonial',[cx,17.3,0],[4.6,9.7,4.8],0);
  centered(arm,'Faixa violeta',[cx,19.3,0],[4.75,1.8,4.95],2);
  centered(arm,'Punho dourado',[cx,12.8,0],[4.95,.85,5.1],1);
  centered(arm,'Luva escura',[cx,11.9,0],[4.2,1,4.2],3);
  centered(arm,'Ombreira de marfim',[cx,22.4,0],[5.2,1.7,5.6],0,[0,0,-s*12]);
  centered(arm,'Ombreira dourada',[cx,21.6,-2.65],[5.1,.55,.5],1,[0,0,-s*12]);
  centered(arm,'Selo da manga',[cx,16.4,-2.6],[1.1,1.1,.4],1,[0,0,45]);
  const cuff=bone(s<0?'sleeve_right':'sleeve_left',part,[cx+s*1.9,15.5,1.8]);
  box(cuff,'Ponta flexivel da manga',[cx+s*1.9-.4,10.5,1.7],[.8,5.1,2.3],2);
  box(cuff,'Bordado da manga',[cx+s*1.9-.44,10.5,1.65],[.88,.25,2.4],1);
}
for(const [leg,boot,s] of [['armorRightLeg','armorRightBoot',-1],['armorLeftLeg','armorLeftBoot',1]]){
  const l=bone(leg,null,[s*2,12,0]);centered(l,'Calca violeta',[s*2,8.8,0],[4.2,7,4.4],2);centered(l,'Joelheira clara',[s*2,5.6,-2.3],[3.7,1.5,.7],0);centered(l,'Friso do joelho',[s*2,5,-2.6],[3.9,.3,.4],1);
  const b=bone(boot,null,[s*2,12,0]);centered(b,'Bota de marfim',[s*2,2.8,-.2],[4.5,5.6,5.2],0);centered(b,'Sola de ouro',[s*2,.4,-.5],[4.7,.8,5.9],1);centered(b,'Caneleira violeta',[s*2,3.5,-2.9],[1.7,3,.4],2);centered(b,'Engaste do tornozelo',[s*2,1.7,-3],[1,1,.5],1,[0,0,45]);
}
const keys=(list)=>Object.fromEntries(list.map(([t,v])=>[t.toFixed(2),v]));
const animation={loop:true,animation_length:12,bones:{
  halo_orbit:{rotation:keys([[0,[0,0,0]],[6,[0,180,0]],[12,[0,360,0]]])},
  halo_meridian:{rotation:keys([[0,[-10,-12,-5]],[3,[10,0,5]],[6,[-10,12,-5]],[9,[10,0,5]],[12,[-10,-12,-5]]])},
  talisman_left:{rotation:keys([[0,[7,0,-8]],[3,[-7,0,8]],[6,[7,0,-8]],[9,[-7,0,8]],[12,[7,0,-8]]])},
  talisman_right:{rotation:keys([[0,[-7,0,8]],[3,[7,0,-8]],[6,[-7,0,8]],[9,[7,0,-8]],[12,[-7,0,8]]])},
  robe_left:{rotation:keys([[0,[0,0,-1]],[3,[1,0,1]],[6,[0,0,-1]],[9,[1,0,1]],[12,[0,0,-1]]])},
  robe_right:{rotation:keys([[0,[0,0,1]],[3,[-1,0,-1]],[6,[0,0,1]],[9,[-1,0,-1]],[12,[0,0,1]]])},
  stole_back:{rotation:keys([[0,[2,0,0]],[3,[-2,0,0]],[6,[2,0,0]],[9,[-2,0,0]],[12,[2,0,0]]])}
}};
// Sample sine curves rather than reversing abruptly at the extreme poses.
for(const [name,phase] of [['talisman_left',0],['talisman_right',Math.PI]]){
  animation.bones[name].rotation=keys(Array.from({length:49},(_,i)=>{const t=i/4,s=Math.sin(t*Math.PI/3+phase);return [t,[7*s,2*Math.cos(t*Math.PI/3+phase),8*s]];}));
}
animation.bones.chest_astrolabe={scale:keys(Array.from({length:25},(_,i)=>{const t=i/2,s=1+.045*Math.sin(t*Math.PI/2);return [t,[s,s,s]];}))};
animation.bones.stole_front={rotation:keys([[0,[2,0,0]],[3,[-2,0,0]],[6,[2,0,0]],[9,[-2,0,0]],[12,[2,0,0]]])};
// Editor preview only; gameplay replaces cloth tracks with damped inertial joints.
for(const b of bones.filter(b=>/^(cape_|robe_|stole_|sleeve_)/.test(b.name))){
  const offset=bones.indexOf(b)*.4;
  animation.bones[b.name]={rotation:keys(Array.from({length:49},(_,i)=>{
    const t=i/4,phase=t*Math.PI/3+offset;return[t,[2.5*Math.sin(phase),0,1.5*Math.cos(phase)]];
  }))};
}
// Explicit face UVs keep sub-pixel ornaments valid in Blockbench and GeckoLib.
const faceUV=c=>Object.fromEntries(['north','south','east','west','up','down'].map(side=>{
  if(c.uv[0]===320)return [side,{uv:[320,0],uv_size:['north','south'].includes(side)?[64,64]:[1,1]}];
  const [w,h,d]=c.size,fw=['east','west'].includes(side)?d:w,fh=['up','down'].includes(side)?d:h;
  return [side,{uv:[c.uv[0]+8,8],uv_size:[Math.max(1,fw*2),Math.max(1,fh*2)]}];
}));
const clean=bones.map(b=>({...b,cubes:b.cubes.map(({_name,...c})=>({...c,uv:faceUV(c)}))}));
json(path.join(assets,'geo/armor/astra.geo.json'),{format_version:'1.12.0','minecraft:geometry':[{description:{identifier:'geometry.astra_oracle',texture_width:512,texture_height:64,visible_bounds_width:4,visible_bounds_height:5,visible_bounds_offset:[0,1.7,0]},bones:clean}]});
json(path.join(assets,'animations/armor/astra.animation.json'),{format_version:'1.8.0',animations:{'animation.astra.orbit':animation}});
const groups=bones.map(b=>({uuid:uuid(b.name),name:b.name,origin:b.pivot,rotation:b.rotation||[0,0,0],export:true,isOpen:false}));
const elements=bones.flatMap(b=>b.cubes.map((c,i)=>({uuid:uuid(b.name+':'+i),name:c._name,type:'cube',from:c.origin,to:c.origin.map((v,k)=>v+c.size[k]),origin:c.pivot||b.pivot,rotation:c.rotation||[0,0,0],box_uv:false,autouv:0,faces:Object.fromEntries(Object.entries(faceUV(c)).map(([side,f])=>[side,{uv:[...f.uv,f.uv[0]+f.uv_size[0],f.uv[1]+f.uv_size[1]],texture:0}]))})));
const outliner=b=>({uuid:uuid(b.name),isOpen:false,children:[...b.cubes.map((c,i)=>uuid(b.name+':'+i)),...bones.filter(child=>child.parent===b.name).map(outliner)]});
const animators=Object.fromEntries(Object.entries(animation.bones).map(([name,channels])=>[uuid(name),{
  name,type:'bone',keyframes:Object.entries(channels).flatMap(([channel,frames])=>
    Object.entries(frames).map(([time,val])=>({uuid:uuid(name+channel+time),channel,time:Number(time),interpolation:'linear',data_points:[{x:String(val[0]),y:String(val[1]),z:String(val[2])}]})))
}]));
json(path.join(root,'art/blockbench/astra_oracle.bbmodel'),{meta:{format_version:'5.0',model_format:'bedrock',box_uv:false},name:'Astra - O Oraculo Celestial',model_identifier:'astra_oracle',resolution:{width:512,height:64},elements,groups,outliner:bones.filter(b=>!b.parent).map(outliner),textures:[{uuid:uuid('texture'),id:'0',name:'astra.png',width:512,height:64,uv_width:512,uv_height:64,internal:true,mode:'bitmap',source:'data:image/png;base64,'+texture.toString('base64')}],animations:[{uuid:uuid('animation'),name:'animation.astra.orbit',loop:'loop',length:12,animators}]});
// Readable original 16px inventory silhouettes for the four actual armor slots.
const icons={helmet:[' G       G ',' GGGGGGGGG ','  GIIIIG   ',' GIKKKKIG  ',' GIKIIKIG  ',' GIKIIKIG  ','  GIIIIG   '],chestplate:[' II     II ','IGIIGGGIIGI',' IIIIGIIII ','  IIGLGII  ','  IIGPGII  ','  IIGPGII  ','  IIGPGII  ','  IIGPGII  ','  GGGGGGG  '],leggings:[' GGGGGGGG  ',' PPPPPPPP  ',' PPPIIPPP  ',' PPP  PPP  ',' PPP  PPP  ',' III  III  ',' GGG  GGG  '],boots:[' III   III ',' IPI   IPI ',' IPI   IPI ',' IIII  IIII',' GGGG  GGGG']};
for(const [part,rows] of Object.entries(icons)){const p=Buffer.alloc(16*16*4),lookup={I:0,G:1,P:2,K:3,L:4};rows.forEach((row,y)=>[...row].forEach((ch,x)=>{if(lookup[ch]!==undefined){const rgb=[1,3,5].map(i=>parseInt(colors[lookup[ch]].slice(i,i+2),16));p.set([...rgb,255],((y+3)*16+x+2)*4);}}));write(path.join(assets,`textures/item/astra_${part}.png`),png(16,16,p));json(path.join(assets,`models/item/astra_${part}.json`),{parent:'minecraft:item/generated',textures:{layer0:`etheris:item/astra_${part}`}});}
console.log(`Astra: ${elements.length} cuboids, ${bones.length} bones, 12s loop, emissive mask, 4 item icons, editable Blockbench project.`);
