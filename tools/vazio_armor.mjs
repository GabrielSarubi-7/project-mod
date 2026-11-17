// Vazio: a fractured crown, a lightless eclipse and a dissolving articulated mantle.
// Bedrock/GeckoLib coordinates: feet y=0, head y=24, front -Z. Original cuboid art.
import fs from 'node:fs';
import path from 'node:path';
import {fileURLToPath} from 'node:url';
import {deflateSync} from 'node:zlib';
import {createHash} from 'node:crypto';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)),'..');
const assets=path.join(root,'src/main/resources/assets/etheris');
const write=(p,data)=>{fs.mkdirSync(path.dirname(p),{recursive:true});fs.writeFileSync(p,data);};
const json=(p,data)=>write(p,JSON.stringify(data,null,2)+'\n');
const uuid=s=>{const h=createHash('sha256').update('vazio:'+s).digest('hex');return `${h.slice(0,8)}-${h.slice(8,12)}-4${h.slice(13,16)}-a${h.slice(17,20)}-${h.slice(20,32)}`;};
function crc(b){let c=0xffffffff;for(const v of b){c^=v;for(let i=0;i<8;i++)c=(c>>>1)^((c&1)?0xedb88320:0);}return(c^0xffffffff)>>>0;}
function png(w,h,p){const chunk=(t,d)=>{const a=Buffer.from(t),n=Buffer.alloc(4),c=Buffer.alloc(4);n.writeUInt32BE(d.length);c.writeUInt32BE(crc(Buffer.concat([a,d])));return Buffer.concat([n,a,d,c]);};const ih=Buffer.alloc(13);ih.writeUInt32BE(w);ih.writeUInt32BE(h,4);ih[8]=8;ih[9]=6;const scan=Buffer.alloc(h*(w*4+1));for(let y=0;y<h;y++)p.copy(scan,y*(w*4+1)+1,y*w*4,(y+1)*w*4);return Buffer.concat([Buffer.from([137,80,78,71,13,10,26,10]),chunk('IHDR',ih),chunk('IDAT',deflateSync(scan)),chunk('IEND',Buffer.alloc(0))]);}
// Stone/metal is softly shaded; luminous crystals occupy only the selected tiles.
const colors=['#211B2D','#A590AD','#190E25','#05020A','#F0CFFF','#BD78EF','#A94CF1','#30113F','#54206F','#8531B6','#D9BDDF','#4B3858','#724E8B','#C388EF','#10091A','#2B173C'];
const atlas=Buffer.alloc(1024*64*4),glow=Buffer.alloc(atlas.length);
for(let y=0;y<64;y++)for(let x=0;x<1024;x++){
  const mat=Math.floor(x/64),u=x%64,rgb=[1,3,5].map(i=>parseInt(colors[mat].slice(i,i+2),16));
  const d=mat===1||mat===11?(y%12===0?14:y%12===11?-13:0):[0,2,7,8,9,14,15].includes(mat)?((u*11+y*7)%89===0?5:(y%16===0?-3:0)):0;
  let rgba=[...rgb.map(v=>Math.max(0,Math.min(255,v+d))),255];
  if(mat===5){const r=Math.hypot((u-31.5)/31.5,(y-31.5)/31.5);const a=r>=1?0:Math.round(130*Math.exp(-r*r*5)*(1-r));rgba=a?[202,122,255,a]:[0,0,0,0];}
  atlas.set(rgba,(y*1024+x)*4);
  if([4,5,6,13].includes(mat))glow.set(rgba,(y*1024+x)*4);
}
const texture=png(1024,64,atlas);
write(path.join(assets,'textures/armor/vazio.png'),texture);
write(path.join(assets,'textures/armor/vazio_glowmask.png'),png(1024,64,glow));
const bones=[];
function bone(name,parent,pivot,rotation){const b={name,...parent?{parent}:{},pivot,...rotation?{rotation}:{},cubes:[]};bones.push(b);return b;}
function box(b,name,p,size,mat=0,rotation=null,pivot=null){const c={origin:p,size,uv:[mat*64,0],...rotation?{rotation,pivot:pivot||p.map((v,i)=>v+size[i]/2)}:{}};b.cubes.push({...c,_name:name});}
function centered(b,name,c,size,mat=0,rot=null){box(b,name,c.map((v,i)=>v-size[i]/2),size,mat,rot,c);}
function ring(b,c,r,thick,mat,n=20,skip=[]){for(let i=0;i<n;i++){if(skip.includes(i))continue;const a=i*2*Math.PI/n;centered(b,'Arco de eclipse',[c[0]+r*Math.cos(a),c[1]+r*Math.sin(a),c[2]],[2*r*Math.tan(Math.PI/n)+.015,thick,.24],mat,[0,0,i*360/n+90]);}}
function star(b,c,r,facing=-1){
  centered(b,'Fenda vertical',c,[.15,r*2,.12],6);centered(b,'Fenda horizontal',c,[r*1.25,.14,.13],6);
  centered(b,'Centro da fenda',[c[0],c[1],c[2]+facing*.08],[r*.45,r*.45,.13],4,[0,0,45]);
  centered(b,'Luz da fenda',[c[0],c[1],c[2]+facing*.16],[.09,r*1.6,.08],4);
  centered(b,'Aura violeta',[c[0],c[1],c[2]+facing*.27],[r*3.1,r*3.1,.02],5);
}
function shard(b,c,size,mat=6,angle=25){centered(b,'Lasca lapidada',c,size,mat,[0,0,angle]);centered(b,'Faceta clara',[c[0]-.06,c[1]+size[1]*.1,c[2]-.12],[size[0]*.24,size[1]*.82,size[2]*.72],13,[0,0,angle]);}
const head=bone('armorHead',null,[0,24,0]);
box(head,'Casco posterior',[-4.6,24,3.5],[9.2,8.6,1.2],0);
box(head,'Topo escuro',[-4.6,31.8,-4.3],[9.2,.8,8.7],0);
box(head,'Vazio sem rosto',[-3.65,24.5,-4.92],[7.3,7.5,.32],3);
for(const s of [-1,1]){
  box(head,'Placa temporal',[s<0?-4.65:3.65,24,-4.4],[1,8.3,8.5],0);
  centered(head,'Borda da face',[s*3.9,27.9,-4.91],[.68,6.2,.5],1,[0,0,-s*7]);
  centered(head,'Arco da sobrancelha',[s*2.45,31.1,-4.98],[3.3,1.15,.72],1,[0,0,-s*23]);
  centered(head,'Queixeira fraturada',[s*2.9,24.7,-4.96],[2.6,.75,.7],11,[0,0,s*24]);
  centered(head,'Faixa posterior',[s*4.8,30.1,0],[.42,.7,7.3],11);
  centered(head,'Base da coroa',[s*4.25,33.1,-.7],[1.2,3.6,1.35],1,[0,0,s*13]);
  centered(head,'Dente da coroa',[s*4.8,35.8,-.7],[.8,2.6,1.0],s<0?6:1,[0,0,-s*24]);
  centered(head,'Ramo quebrado da coroa',[s*5.55,34.45,-.7],[1.45,.65,.95],11,[0,0,-s*23]);
  centered(head,'Aresta do ramo',[s*5.8,34.85,-1.08],[.42,1.65,.25],6,[0,0,-s*20]);
  centered(head,'Faceta da coroa',[s*4.75,35.9,-1.26],[.22,2.1,.13],13,[0,0,-s*24]);
  centered(head,'Cristal lateral',[s*5.0,30,-1.3],[.65,2.9,1.35],6,[0,0,s*14]);
}
centered(head,'Cume frontal',[0,32.2,-5],[1.65,2.25,.8],1,[0,0,45]);
centered(head,'Ponta frontal',[0,33.4,-5],[.7,1.55,.7],10,[0,0,45]);
const face=bone('void_face','armorHead',[0,28.4,-5.13]);star(face,[0,28.4,-5.13],1.7);
for(const [name,c,size,angle] of [
  ['crown_shard_right',[-5.75,38.1,-.6],[.85,2.25,.65],-26],
  ['crown_shard_left',[4.9,38.25,-.7],[.72,1.5,.65],28],
  ['crown_shard_rear',[-.7,35.6,3.2],[.55,1.15,.7],-18]
]){const b=bone(name,'armorHead',c);shard(b,c,size,6,angle);}

const body=bone('armorBody',null,[0,24,0]);
box(body,'Couraca de obsidiana',[-4.4,11.9,-2.7],[8.8,12.1,5.4],0);
for(const s of [-1,1]){
  centered(body,'Gola angular',[s*2.65,23.4,-2.95],[3.7,1.0,.65],1,[0,0,s*22]);
  for(let i=0;i<3;i++){
    centered(body,'Placa costal',[s*2.55,20.7-i*2.8,-2.98],[3.5,1.85,.65],i===1?14:0,[0,0,-s*13]);
    centered(body,'Friso costal',[s*3.5,20.3-i*2.8,-3.35],[.6,1.75,.28],11,[0,0,-s*13]);
  }
  centered(body,'Cinto em chevron',[s*2.45,12.7,-3.13],[4.4,1.25,.72],1,[0,0,-s*22]);
  centered(body,'Protecao lateral do cinto',[s*4.25,12.5,.2],[.6,1.2,5.5],11);
}
centered(body,'Selo da cintura',[0,12.3,-3.65],[1.45,1.45,.55],6,[0,0,45]);
const core=bone('void_core','armorBody',[0,20,-3.8]);
// A stepped opaque disk keeps the center genuinely black, not a glowing purple gemstone.
for(let i=-5;i<=5;i++){const y=i*.35,w=2*Math.sqrt(Math.max(0,1.9*1.9-y*y));centered(core,'Disco sem luz',[0,20+y,-3.8],[w,.37,.25],3);}
ring(core,[0,20,-3.98],2.12,.53,11);ring(core,[0,20,-4.15],2.02,.24,6);
for(const [x,y,angle] of [[-2.85,20,0],[2.85,20,0],[0,22.8,90],[0,17.2,90]]){
  centered(core,'Raio de metal',[x,y,-3.92],[1.7,.85,.55],1,[0,0,angle+45]);
  centered(core,'Aresta violeta',[x,y,-4.24],[1.15,.2,.12],6,[0,0,angle]);
}
const iris=bone('void_iris','void_core',[0,20,-4.24]);ring(iris,[0,20,-4.24],1.78,.10,4,24,[0,1,8,9,16,17]);
// A faint corona uses an annulus, leaving the central black disk untouched.
for(let i=0;i<8;i++){const a=i*Math.PI/4;centered(core,'Corona de eclipse',[Math.cos(a)*2.15,20+Math.sin(a)*2.15,-4.4],[1.6,1.6,.02],5);}

for(const s of [-1,1]){
  const side=s<0?'right':'left';let parent='armorBody';
  for(let i=0;i<3;i++){
    const y=12.7-i*3.5,x=s*(4.75+i*.035),id=i?'robe_'+side+'_'+i:'robe_'+side;
    const p=bone(id,parent,[x,y,0]);parent=id;
    centered(p,'Tecido lateral escuro',[x,y-1.75,.2],[.42,3.7,5.7],i===2?7:2);
    centered(p,'Lamina externa',[x+s*.18,y-1.1,.1],[.43,2.3,5.3],i===0?1:11,[0,0,-s*10]);
    centered(p,'Borda violeta frontal',[x,y-2.1,-2.7],[.5,2.6,.26],i===2?6:12);
    if(i===2)shard(p,[x,y-3.4,-2.4],[.5,1.4,.4],6,-s*18);
  }
}
let parent='armorBody';
for(let i=0;i<3;i++){
  const y=12.4-i*3.45,z=-3.5-i*.04,id=i?'stole_front_'+i:'stole_front';const p=bone(id,parent,[0,y,z]);parent=id;
  box(p,'Tabardo estreito',[-1.7,y-3.65,z],[3.4,3.75,.25],i===2?8:2);
  for(const s of [-1,1])box(p,'Borda do tabardo',[s<0?-1.78:1.55,y-3.65,z-.13],[.23,3.75,.22],i===2?6:11);
  if(i===1)star(p,[0,y-2.5,z-.3],.85);
  if(i===2){centered(p,'Cauda em ponta',[0,y-3.15,z-.04],[2.25,2.25,.25],9,[0,0,45]);for(const s of [-1,1])centered(p,'Ponta luminosa',[s*.82,y-3.85,z-.22],[2.45,.2,.23],6,[0,0,s*45]);}
}

const cape=bone('cape_root','armorBody',[0,23,4.5]);
for(const s of [-1,1])centered(body,'Grampo do manto',[s*3.7,23.1,3.8],[1.3,.7,1.2],11,[0,0,s*10]);
for(const [column,x] of [['right',-3.65],['center',0],['left',3.65]]){
  let parent=cape.name;
  for(let tier=0;tier<4;tier++){
    const y=23-tier*4.65,id=`cape_${column}_${tier}`;
    const p=bone(id,parent,[x,y,4.5]);parent=id;
    let pattern=['1111','1111','1111','1111'];
    if(tier===1&&column==='right')pattern=['1111','1111','1011','1011'];
    if(tier===2&&column==='left')pattern=['1111','1110','1011','1011'];
    if(tier===3)pattern=column==='center'?['1111','1111','0110','0110']:column==='right'?['1111','0111','0011','0001']:['1111','1110','1100','1000'];
    // Merge horizontal cells so deliberate holes are geometry, not transparent skin exposure.
    for(let row=0;row<4;row++){
      const mat=tier<2?2:tier===2?(row<2?7:8):(row<2?8:row===2?9:6);
      for(let col=0;col<4;){if(pattern[row][col]==='0'){col++;continue;}let end=col;while(end<4&&pattern[row][end]==='1')end++;
        const z=4.5+(column==='center'?.04:0)+tier*.048+row*.012;
        box(p,'Fragmento tecido do manto',[x-1.92+col*.96,y-(row+1)*1.17,z],[(end-col)*.96,1.27,.22],mat);col=end;
      }
    }
    if(tier===0&&column!=='center')box(p,'Borda do ombro',[x+(column==='left'?1.60:-1.92),y-4.6,4.78],[.27,4.7,.17],11);
    if(tier===3){const edge=column==='right'?1.2:column==='left'?-1.2:0;centered(p,'Brasa da bainha',[x+edge,y-4.35,4.98],[column==='center'?1.2:.6,.4,.13],6);}
  }
}
// The back insignia is attached to the top cloth, not to the torso behind it.
const back=bone('back_eclipse','cape_center_0',[0,20.5,4.85]);
ring(back,[0,20.5,4.93],1.28,.2,12,16);star(back,[0,18.7,5.08],.8,1);

for(const [part,s] of [['armorRightArm',-1],['armorLeftArm',1]]){
  const arm=bone(part,null,[s*5,22,0]),cx=s*6;
  centered(arm,'Braquial escuro',[cx,17.2,0],[4.35,10,4.5],0);
  centered(arm,'Topo da ombreira',[s*6.5,22.8,0],[6.0,1.2,6.0],1,[0,0,-s*10]);
  centered(arm,'Vazio da ombreira',[s*7.1,20.5,-.15],[4.5,3.5,5.0],3,[0,0,-s*9]);
  centered(arm,'Moldura externa',[s*8.8,20.5,-2.8],[.7,3.6,.65],1,[0,0,s*16]);
  centered(arm,'Lamina inferior',[s*7.4,18.8,-2.8],[4.4,.85,.7],1,[0,0,s*16]);
  star(arm,[s*7.25,20.4,-2.85],.72);
  centered(arm,'Bracelete superior',[cx,15.1,0],[4.7,.8,4.85],11);
  centered(arm,'Manopla',[cx,12.7,-.15],[4.7,3.3,4.8],0);
  centered(arm,'Borda da manopla',[cx,13.7,-2.6],[4.9,.75,.65],1,[0,0,-s*8]);
  centered(arm,'Placa da mao',[cx,11.6,-2.7],[3.7,1.8,.6],1);
  const fin=bone('sleeve_'+(s<0?'right':'left'),part,[s*8.1,15.1,1]);
  centered(fin,'Lamina flutuante',[s*8.25,13.2,1.2],[.65,4.3,2.5],11,[0,0,-s*10]);
  shard(fin,[s*8.5,11.4,.1],[.55,1.35,.5],6,-s*20);
}
for(const [leg,boot,s] of [['armorRightLeg','armorRightBoot',-1],['armorLeftLeg','armorLeftBoot',1]]){
  const l=bone(leg,null,[s*2,12,0]);
  centered(l,'Perneira de obsidiana',[s*2.02,8.5,0],[4.04,7,4.5],0);
  centered(l,'Placa de joelho',[s*2,6.25,-2.5],[3.6,1.4,.65],11,[0,0,s*9]);
  centered(l,'Friso do joelho',[s*2,5.5,-2.8],[3.9,.35,.3],1);
  const b=bone(boot,null,[s*2,12,0]);
  centered(b,'Greva escura',[s*2.07,2.8,-.3],[4.10,5.5,5.1],0);
  centered(b,'Peito da bota',[s*2.07,1.0,-1],[4.14,1.7,6.1],11);
  centered(b,'Biqueira de prata',[s*2.07,1,-3.85],[4.14,1.8,.7],1);
  for(const dx of [-1.55,1.55])centered(b,'Arco da greva',[s*2+dx,3.5,-2.9],[.55,3,.6],1,[0,0,-dx*4]);
  centered(b,'Cristal da greva',[s*2,4.7,-2.92],[.55,.8,.26],6,[0,0,45]);
}

const keys=list=>Object.fromEntries(list.map(([t,v])=>[t.toFixed(2),v]));
const sample=fn=>keys(Array.from({length:49},(_,i)=>[i/4,fn(i/4)]));
const animation={loop:true,animation_length:12,bones:{
  void_iris:{rotation:keys([[0,[0,0,0]],[6,[0,0,180]],[12,[0,0,360]]])},
  void_core:{scale:sample(t=>{const s=1+.025*Math.sin(t*Math.PI/2);return[s,s,s];})},
  void_face:{scale:sample(t=>{const s=1+.06*Math.sin(t*Math.PI/3);return[s,s,1];})}
}};
for(const [i,name] of ['crown_shard_right','crown_shard_left','crown_shard_rear'].entries())animation.bones[name]={
  position:sample(t=>[.10*Math.sin(t*Math.PI/3+i),.32*Math.sin(t*Math.PI/3+i),.09*Math.cos(t*Math.PI/3+i)]),
  rotation:sample(t=>[3*Math.sin(t*Math.PI/3+i),5*Math.cos(t*Math.PI/3+i),3*Math.sin(t*Math.PI/3+i)])
};
for(const b of bones.filter(b=>/^(cape_|robe_|stole_|sleeve_)/.test(b.name))) {
  const phase=bones.indexOf(b)*.35;
  animation.bones[b.name]={rotation:sample(t=>[2*Math.sin(t*Math.PI/3+phase),0,Math.cos(t*Math.PI/3+phase)])};
}
const faceUV=c=>Object.fromEntries(['north','south','east','west','up','down'].map(side=>{
  if(c.uv[0]===320)return[side,{uv:[320,0],uv_size:['north','south'].includes(side)?[64,64]:[1,1]}];
  const[w,h,d]=c.size,fw=['east','west'].includes(side)?d:w,fh=['up','down'].includes(side)?d:h;
  return[side,{uv:[c.uv[0]+8,8],uv_size:[Math.max(1,fw*2),Math.max(1,fh*2)]}];
}));
const clean=bones.map(b=>({...b,cubes:b.cubes.map(({_name,...c})=>({...c,uv:faceUV(c)}))}));
json(path.join(assets,'geo/armor/vazio.geo.json'),{format_version:'1.12.0','minecraft:geometry':[{description:{identifier:'geometry.vazio_regalia',texture_width:1024,texture_height:64,visible_bounds_width:4,visible_bounds_height:5,visible_bounds_offset:[0,1.7,0]},bones:clean}]});
json(path.join(assets,'animations/armor/vazio.animation.json'),{format_version:'1.8.0',animations:{'animation.vazio.eclipse':animation}});
const groups=bones.map(b=>({uuid:uuid(b.name),name:b.name,origin:b.pivot,rotation:b.rotation||[0,0,0],export:true,isOpen:false}));
const elements=bones.flatMap(b=>b.cubes.map((c,i)=>({uuid:uuid(b.name+':'+i),name:c._name,type:'cube',from:c.origin,to:c.origin.map((v,k)=>v+c.size[k]),origin:c.pivot||b.pivot,rotation:c.rotation||[0,0,0],box_uv:false,autouv:0,faces:Object.fromEntries(Object.entries(faceUV(c)).map(([side,f])=>[side,{uv:[...f.uv,f.uv[0]+f.uv_size[0],f.uv[1]+f.uv_size[1]],texture:0}]))})));
const outliner=b=>({uuid:uuid(b.name),isOpen:false,children:[...b.cubes.map((c,i)=>uuid(b.name+':'+i)),...bones.filter(child=>child.parent===b.name).map(outliner)]});
const animators=Object.fromEntries(Object.entries(animation.bones).map(([name,channels])=>[uuid(name),{name,type:'bone',keyframes:Object.entries(channels).flatMap(([channel,frames])=>Object.entries(frames).map(([time,val])=>({uuid:uuid(name+channel+time),channel,time:Number(time),interpolation:'linear',data_points:[{x:String(val[0]),y:String(val[1]),z:String(val[2])}]})))}]));
json(path.join(root,'art/blockbench/vazio_regalia.bbmodel'),{meta:{format_version:'5.0',model_format:'bedrock',box_uv:false},name:'Vazio - Coroa da Ausencia',model_identifier:'vazio_regalia',resolution:{width:1024,height:64},elements,groups,outliner:bones.filter(b=>!b.parent).map(outliner),textures:[{uuid:uuid('texture'),id:'0',name:'vazio.png',width:1024,height:64,uv_width:1024,uv_height:64,internal:true,mode:'bitmap',source:'data:image/png;base64,'+texture.toString('base64')}],animations:[{uuid:uuid('animation'),name:'animation.vazio.eclipse',loop:'loop',length:12,animators}]});
const icons={helmet:[' P       P ',' SP     SP ',' SS SSS SS ','  SSSSSSS  ','  SKKLKKS  ','  SKLLLKS  ','  SKKLKKS  ','   SKKKS   ','    SSS    '],chestplate:[' SSS   SSS ','SSKSSSSSKSS',' KKKSL S K ','  KSLKLSK  ','  KKSL SK  ','  SKKKKKS  ','  KSKKKSK  ','   PPKPP   ','    PPP    '],leggings:[' SSSSSSSSS ',' SKKKKKKKS ','  KKK KKK  ','  KKK KKK  ','  PKK KKP  ','  SSS SSS  ','  PPP PPP  '],boots:['  SS   SS  ',' SKKS SKKS ',' SKKS SKKS ',' SKKS SKKS ',' SSSS SSSS ',' SSSS SSSS ']};
for(const[part,rows]of Object.entries(icons)){
  const p=Buffer.alloc(16*16*4),lookup={K:0,S:1,P:6,L:4};
  rows.forEach((row,y)=>[...row].forEach((ch,x)=>{if(lookup[ch]!==undefined)p.set([...([1,3,5].map(i=>parseInt(colors[lookup[ch]].slice(i,i+2),16))),255],((y+3)*16+x+2)*4);}));
  write(path.join(assets,`textures/item/vazio_${part}.png`),png(16,16,p));
  json(path.join(assets,`models/item/vazio_${part}.json`),{parent:'minecraft:item/generated',textures:{layer0:`etheris:item/vazio_${part}`}});
}
console.log(`Vazio: ${elements.length} cuboids / ${bones.length} bones, torn 12-panel cape, animated crown/core, emissive atlas and editable Blockbench project.`);
