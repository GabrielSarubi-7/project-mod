// Original Etheris cuboid models. Node 20+, no external dependencies.
// Generates the game assets and self-contained, editable Blockbench 5 projects.
import fs from 'node:fs';
import path from 'node:path';
import {fileURLToPath} from 'node:url';
import {deflateSync} from 'node:zlib';
import {createHash} from 'node:crypto';
const root=path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const assets=path.join(root,'src/main/resources/assets/etheris');
const source=path.join(root,'art/blockbench');
const write=(file,data)=>{fs.mkdirSync(path.dirname(file),{recursive:true});fs.writeFileSync(file,data);};
const json=(file,value)=>write(file,JSON.stringify(value,null,2)+'\n');
function uuid(key) { const h=createHash('sha256').update(key).digest('hex'); return `${h.slice(0,8)}-${h.slice(8,12)}-4${h.slice(13,16)}-a${h.slice(17,20)}-${h.slice(20,32)}`; }
function crc32(b) { let c=0xffffffff; for(const v of b){c^=v;for(let i=0;i<8;i++)c=(c>>>1)^((c&1)?0xedb88320:0);}return (c^0xffffffff)>>>0; }
function png(width,height,pixels) {
  const chunk=(type,data)=>{const t=Buffer.from(type),len=Buffer.alloc(4),crc=Buffer.alloc(4);len.writeUInt32BE(data.length);crc.writeUInt32BE(crc32(Buffer.concat([t,data])));return Buffer.concat([len,t,data,crc]);};
  const header=Buffer.alloc(13);header.writeUInt32BE(width);header.writeUInt32BE(height,4);header[8]=8;header[9]=6;
  const scan=Buffer.alloc(height*(width*4+1));for(let y=0;y<height;y++)pixels.copy(scan,y*(width*4+1)+1,y*width*4,(y+1)*width*4);
  return Buffer.concat([Buffer.from([137,80,78,71,13,10,26,10]),chunk('IHDR',header),chunk('IDAT',deflateSync(scan)),chunk('IEND',Buffer.alloc(0))]);
}
const materials={
  obsidian:['#171D31','#1E263B','#111627'], gold:['#BC9654','#E1C58A','#806035'],
  silver:['#9EAFC4','#D1E2E8','#5A6F8C'], lunar:['#779ED7','#D7EEFF','#41547C'],
  violet:['#7362AC','#C3A9F2','#3C315D'], amber:['#B2692E','#F2C66C','#65372A'],
  rune:['#192138','#C5AF79','#101627'], cavity:['#090E1C','#151C30','#060912'],
  leather:['#26334B','#3C4C6A','#141D30'], pages:['#C2BBA4','#E0D9BF','#968C76']
};
const textures={};
for(const [name,colors] of Object.entries(materials)) {
  const rgb=colors.map(c=>[1,3,5].map(i=>parseInt(c.slice(i,i+2),16)));
  const pixels=Buffer.alloc(32*32*4);
  for(let y=0;y<32;y++)for(let x=0;x<32;x++) {
    let color=rgb[0];const hash=(x*13+y*29+x*y*3)%61;
    if(hash<4)color=rgb[1];else if(hash===13)color=rgb[2];
    if(name==='gold'||name==='silver'){color=y<3?rgb[1]:y>27?rgb[2]:rgb[0];if(x%16===0)color=rgb[1];}
    if(['lunar','violet','amber'].includes(name)){color=(x+y<27||x-y>15)?rgb[1]:(x+y>47?rgb[2]:rgb[0]);if(x===13||y===9&&x>5&&x<22)color=rgb[1];}
    if(name==='rune'){color=rgb[0];if((Math.abs(x-16)+Math.abs(y-16)===9)||(x===16&&y>=3&&y<=28)||(y===16&&(x===4||x===28)))color=rgb[1];}
    if(name==='pages')color=y%4===0?rgb[2]:rgb[0];
    if(name==='obsidian'&&(x===0||y===0))color=rgb[1];
    pixels.set([...color,255],(y*32+x)*4);
  }
  textures[name]=png(32,32,pixels);write(path.join(assets,`textures/block/firmament/${name}.png`),textures[name]);
}
const sides=['north','south','east','west','up','down'];
const models=[];
class Model {
  constructor(id,title,kind='block'){this.id=id;this.title=title;this.kind=kind;this.cubes=[];models.push(this);}
  box(name,from,to,mat='obsidian',rotation=null,group='Estrutura',faceMaterials={}) {
    const cube={name,from,to,mat,rotation,group,faceMaterials};this.cubes.push(cube);return this;
  }
  center(name,c,size,mat,rotation=null,group='Detalhes'){return this.box(name,c.map((v,i)=>v-size[i]/2),c.map((v,i)=>v+size[i]/2),mat,rotation,group);}
  base(){this.box('Sapata de obsidiana',[1,0,1],[15,1.5,15]);this.box('Filete de ouro',[1.5,1.5,1.5],[14.5,2,14.5],'gold');this.box('Plataforma chanfrada',[2,2,2],[14,3,14]);return this;}
  crystal(c,size=3,mat='lunar',group='Nucleo') {this.center('Gema lapidada',c,[size,size,size],mat,{origin:c,axis:'z',angle:45},group);return this;}
  ring(name,c,r,plane='xy',mat='gold',thick=.65) {
    // Eight short beams create an open octagonal ring, never an opaque disk.
    for(let i=0;i<8;i++){
      const a=i*Math.PI/4,offset=[Math.cos(a)*r,Math.sin(a)*r];
      const p=plane==='xy'?[c[0]+offset[0],c[1]+offset[1],c[2]]:[c[0]+offset[0],c[1],c[2]+offset[1]];
      let angle=(i*45+90)%180;if(angle>90)angle-=180;
      const length=2*r*Math.tan(Math.PI/8)+.12;
      let size=plane==='xy'?[length,thick,thick]:[length,thick,thick];
      if(angle===90){size=plane==='xy'?[thick,length,thick]:[thick,thick,length];angle=0;}
      this.center(`${name} ${i+1}`,p,size,mat,angle?{origin:p,axis:plane==='xy'?'z':'y',angle:plane==='xy'?angle:-angle}:null,'Aneis');
    }return this;
  }
  seal(c,size=2,mat='rune'){return this.center('Selo de principio',c,[size,size,.2],mat,null,'Runas');}
}
let m=new Model('ether_collector','Coletor | Astrolabio do Firmamento');m.base();
m.box('Pedestal',[5.5,3,5.5],[10.5,6,10.5]);m.ring('Orbita celeste',[8,9.5,8],5.4);m.ring('Equador',[8,9.5,8],3.8,'xz','silver',.4);m.crystal([8,9.5,8],2.8);
for(const x of [3,12]){m.box('Garra de captacao',[x,3,7],[x+1,6.5,9]);m.box('Terminal lunar',[x,6.5,7],[x+1,7.2,9],'lunar');}m.seal([8,4.5,5.35],2);

m=new Model('aether_focus_block','Foco Etéreo | Altar da Convergencia');m.base();
m.box('Coluna de sintonia',[5,3,5],[11,7,11]);m.ring('Circulo de sintonia',[8,7.6,8],5.2,'xz');
m.center('Tabua de constelacoes',[8,9,8],[7,.7,6],'rune',{origin:[8,9,8],axis:'x',angle:22.5},'Altar');m.crystal([8,13,8],2.5,'violet');
for(const x of [2.5,12.5]){m.box('Pilar do altar',[x,3,10.5],[x+1,11,12]);m.box('Coroa do altar',[x-.2,11,10.3],[x+1.2,11.6,12.2],'gold');}

m=new Model('rune_forge','Forja | Altar da Brasa Violeta');m.base();
m.box('Corpo da forja',[3,3,3],[13,9,13]);m.box('Boca escura',[5,3.8,2.8],[11,7.8,3.1],'cavity');m.box('Brasa astral',[5.8,4.1,2.6],[10.2,5.5,2.85],'violet',null,'Fornalha');
for(const x of [4.4,7.5,10.6])m.box('Grade dourada',[x,3.8,2.3],[x+.45,7.8,2.65],'gold',null,'Fornalha');
m.box('Mesa de gravacao',[2,9,4],[14,10,12],'gold');m.box('Bigorna obsidiana',[3,10,5],[13,12,11]);m.box('Superficie rúnica',[3.5,12,5.5],[12.5,12.3,10.5],'rune');
for(const x of [3,12]){m.box('Espora do altar',[x,12,8],[x+1,14.5,10]);m.box('Ponta dourada',[x,14.5,8],[x+1,15,10],'gold');}

m=new Model('amber_reservoir','Reservatorio | Relicario de Ambar');m.base();
m.box('Ambar encerrado',[4.5,3,4.5],[11.5,12.5,11.5],'amber',null,'Reservatorio');m.box('Faixa central',[4.2,6,4.2],[11.8,6.6,11.8],'gold');
for(const x of [3,11.5])for(const z of [3,11.5]){m.box('Costela do relicario',[x,3,z],[x+1.5,13,z+1.5]);m.box('Capitel dourado',[x-.2,12.5,z-.2],[x+1.7,13.2,z+1.7],'gold');}
m.box('Tampa do relicario',[3.5,13,3.5],[12.5,14,12.5]);m.box('Coroa do selo',[5.5,14,5.5],[10.5,14.6,10.5],'gold');m.crystal([8,15,8],1.2,'amber');m.seal([8,9,4.2],2.5);

m=new Model('prismatic_crusher','Triturador | Mandibulas do Prisma');m.base();
m.box('Cuba de moagem',[3,3,3],[13,7,13]);m.box('Cavidade de moagem',[4,7,4],[12,7.2,12],'cavity');
for(const x of [4.5,9.5]){m.box('Eixo do triturador',[x,7,4],[x+2,9.5,12],'silver',null,'Moagem');for(const z of [4.8,7.7,10.6])m.center('Dente prismatico',[x+1,9.5,z],[1.6,1.6,1.5],'violet',{origin:[x+1,9.5,z],axis:'z',angle:45},'Moagem');}
for(const x of [1.8,12.2]){m.box('Muralha lateral',[x,3,3],[x+2,12.5,13]);m.box('Faixa estrutural',[x-.15,10.8,2.8],[x+2.15,11.5,13.2],'gold');}
m.box('Saida de po',[5,3.5,2.7],[11,5.7,3],'cavity');m.seal([8,6.3,2.6],1.2);m.box('Travessa posterior',[3,11,11.5],[13,12,13],'gold');

m=new Model('crystal_refinery','Refinaria | Destilaria da Lua');m.base();
for(const [x,h,mat] of [[5,13,'violet'],[11,10,'lunar']]){
  m.box('Pe de ampola',[x-2,3,5],[x+2,4,11],'gold');m.box('Ampola lapidada',[x-1.5,4,6],[x+1.5,h,10],mat,null,'Ampolas');
  m.box('Lacre de ampola',[x-1.8,h,5.7],[x+1.8,h+.6,10.3],'gold');m.box('Cabeca de ampola',[x-1,h+.6,7],[x+1,h+1.5,9]);
  for(const z of [5.7,10])m.box('Suporte de ampola',[x-.3,4,z],[x+.3,h,z+.3],'silver');
}m.box('Ponte de destilacao',[5,12.5,7.3],[11.5,13.1,8.7],'silver');m.box('Duto descendente',[10.8,10.6,7.3],[11.5,13.1,8.7],'gold');m.seal([8,2.1,1.3],1.8);

m=new Model('distribution_node','No | Bussola dos Quatro Enlaces');
m.box('Base cruciforme X',[0,0,5],[16,2,11]);m.box('Base cruciforme Z',[5,0,0],[11,2,16]);m.box('Nucleo de distribuicao',[4,2,4],[12,5,12]);
m.ring('Rosa dos ventos',[8,5.8,8],5.7,'xz');m.crystal([8,7.5,8],2.8,'lunar');
for(const [x,z] of [[8,1.5],[8,14.5],[1.5,8],[14.5,8]]){m.center('Porta de enlace',[x,3,z],[2.8,2.5,2.8],'gold',null,'Portas');m.center('Contato lunar',[x,4.5,z],[1.8,.5,1.8],'lunar',null,'Portas');}

m=new Model('transmutation_chamber','Camara | Santuario do Eclipse');m.base();
m.box('Altar interior',[4,3,4],[12,4,12],'rune');m.crystal([8,8.3,8],3.3,'violet');m.ring('Orbita de contencao',[8,8.3,8],3.4,'xz','silver',.4);
for(const x of [2.2,12.2])for(const z of [2.2,12.2]){
  m.box('Contraforte',[x,3,z],[x+1.6,13,z+1.6]);m.box('Nervura dourada',[x+.55,4,z-.15],[x+1.05,12,z+.05],'gold');m.box('Capitel',[x-.25,12.5,z-.25],[x+1.85,13.2,z+1.85],'gold');
}
for(const z of [2.5,12.5]){m.box('Arco horizontal',[3,13,z],[13,14,z+1]);m.box('Coroamento',[4.5,14,z],[11.5,14.7,z+1],'gold');}
m.box('Arco oeste',[2.5,13,3],[3.5,14,13]);m.box('Arco leste',[12.5,13,3],[13.5,14,13]);m.seal([8,13.5,2.3],2);m.crystal([8,15,8],1.2,'lunar');

m=new Model('aether_focus','Foco | Cetro do Eclipse','item');
m.box('Empunhadura',[7.25,0,7.25],[8.75,8,8.75],'leather',null,'Cabo');
for(const y of [1,3,5,7.5])m.box('Anel do cabo',[7,y,7],[9,y+.4,9],'gold',null,'Cabo');
m.ring('Coroa aberta',[8,11.5,8],3.3,'xy','gold',.65);m.crystal([8,11.5,8],2.3,'lunar');
for(const x of [4.4,10.6]){m.box('Ponta do cetro',[x,11.5,7.6],[x+1,15.5,8.4]);m.box('Lacre lunar',[x,15.5,7.6],[x+1,16,8.4],'lunar');}
m.crystal([8,.4,8],1,'violet');

m=new Model('link_wand','Enlace | Diapasao Astral','item');
m.box('Empunhadura',[7.3,0,7.3],[8.7,7.5,8.7],'leather',null,'Cabo');m.box('Cinta do cabo',[7,6,7],[9,7,9],'gold');m.box('Ponte do diapasao',[4.5,7.5,7],[11.5,8.5,9],'gold');
for(const x of [4.5,10.5]){m.box('Haste de ressonancia',[x,8.5,7.25],[x+1,15,8.75],'silver');m.box('Lacre de ressonancia',[x-.2,13,7],[x+1.2,13.5,9],'gold');}
m.crystal([8,10,8],1.3,'violet');m.box('Gema de enlace',[7.6,14.2,7.6],[8.4,15.2,8.4],'lunar');

m=new Model('codex','Codice | Livro das Orbitas','item');
m.box('Capa inferior',[3,2,5.5],[13,14,6.2],'leather',null,'Livro');m.box('Paginas',[3.5,2.5,6.2],[12.5,13.5,8.8],'pages',null,'Livro');m.box('Capa superior',[3,2,8.8],[13,14,9.5],'leather',null,'Livro');m.box('Lombada',[2.8,2,5.5],[3.7,14,9.5],'obsidian',null,'Livro');
for(const y of [2.4,13])m.box('Friso da capa',[3.5,y,9.5],[12.5,y+.4,9.75],'gold');
for(const x of [3.6,11.5])for(const y of [3,12])m.box('Cantoneira',[x,y,9.5],[x+.9,y+.9,9.8],'gold');
m.ring('Selo orbital',[8,8,9.8],2.8,'xy','gold',.4);m.crystal([8,8,9.9],1.6,'lunar');m.box('Fecho',[11.5,7,5.3],[13.3,9,9.8],'gold');m.box('Fita violeta',[9,0.5,7],[10,3,7.2],'violet');

const displayBlock={gui:{rotation:[30,225,0],translation:[0,0,0],scale:[.625,.625,.625]},ground:{translation:[0,3,0],scale:[.25,.25,.25]},fixed:{rotation:[0,0,0],scale:[.5,.5,.5]},thirdperson_righthand:{rotation:[75,45,0],translation:[0,2.5,0],scale:[.375,.375,.375]}};
const displayItem={gui:{rotation:[15,-35,-15],translation:[0,0,0],scale:[.9,.9,.9]},ground:{translation:[0,2,0],scale:[.45,.45,.45]},fixed:{rotation:[0,180,0],scale:[.8,.8,.8]},thirdperson_righthand:{rotation:[0,-90,15],translation:[0,1,0],scale:[.8,.8,.8]},thirdperson_lefthand:{rotation:[0,90,-15],translation:[0,1,0],scale:[.8,.8,.8]},firstperson_righthand:{rotation:[0,-90,15],translation:[1,2,0],scale:[.8,.8,.8]},firstperson_lefthand:{rotation:[0,90,-15],translation:[1,2,0],scale:[.8,.8,.8]}};
function faces(c,bb=false,texNames=[]){
  const size=c.to.map((v,i)=>v-c.from[i]);
  return Object.fromEntries(sides.map(side=>{const w=['east','west'].includes(side)?size[2]:size[0],h=['up','down'].includes(side)?size[2]:size[1],mat=c.faceMaterials[side]||c.mat;
    const uv=(['rune','lunar','violet','amber'].includes(mat)?[0,0,16,16]:[0,0,Math.min(16,w),Math.min(16,h)]).map(v=>bb?v*2:v);
    return [side,{uv,texture:bb?texNames.indexOf(mat):'#'+mat}];}));
}
function bbProject(model,format='java_block'){
  const texNames=[...new Set(model.cubes.flatMap(c=>[c.mat,...Object.values(c.faceMaterials)]))];
  const elements=model.cubes.map((c,i)=>({name:c.name,type:'cube',uuid:uuid(model.id+':cube:'+i),from:c.from,to:c.to,origin:c.rotation?.origin||[8,8,8],rotation:['x','y','z'].map(axis=>c.rotation?.axis===axis?c.rotation.angle:0),box_uv:false,autouv:0,color:i%8,faces:faces(c,true,texNames)}));
  const groups=[...new Set(model.cubes.map(c=>c.group))].map(name=>({name,uuid:uuid(model.id+':group:'+name),origin:[8,8,8],rotation:[0,0,0],export:true,isOpen:false}));
  return {meta:{format_version:'5.0',model_format:format,box_uv:false},name:model.title,model_identifier:model.id,parent:model.kind==='block'?'block/block':'item/handheld',ambientocclusion:true,front_gui_light:model.kind==='item',resolution:{width:32,height:32},elements,groups,outliner:groups.map(g=>({uuid:g.uuid,isOpen:false,children:elements.filter((e,i)=>model.cubes[i].group===g.name).map(e=>e.uuid)})),textures:texNames.map((name,i)=>({uuid:uuid('texture:'+name),id:String(i),name:name+'.png',namespace:'etheris',folder:'block/firmament',width:32,height:32,uv_width:32,uv_height:32,mode:'bitmap',internal:true,saved:true,source:'data:image/png;base64,'+textures[name].toString('base64')})),display:model.kind==='block'?displayBlock:displayItem};
}
for(const model of models){
  const used=[...new Set(model.cubes.flatMap(c=>[c.mat,...Object.values(c.faceMaterials)]))];
  const game={parent:model.kind==='block'?'minecraft:block/block':'minecraft:item/handheld',ambientocclusion:true,textures:{particle:'etheris:block/firmament/obsidian',...Object.fromEntries(used.map(t=>[t,`etheris:block/firmament/${t}`]))},elements:model.cubes.map(c=>({from:c.from,to:c.to,...c.rotation?{rotation:{...c.rotation,rescale:false}}:{},faces:faces(c)})),display:model.kind==='block'?displayBlock:displayItem};
  json(path.join(assets,`models/${model.kind}/${model.id}.json`),game);
  json(path.join(source,model.id+'.bbmodel'),bbProject(model));
}
// An editor-only contact sheet; never register its oversized geometry in Minecraft.
const gallery={id:'etheris_collection',title:'Etheris | Oficina do Firmamento',kind:'block',cubes:[]};
models.forEach((model,i)=>{const off=[(i%4)*23,0,Math.floor(i/4)*25];for(const c of model.cubes)gallery.cubes.push({...c,group:model.title,from:c.from.map((v,k)=>v+off[k]),to:c.to.map((v,k)=>v+off[k]),rotation:c.rotation?{...c.rotation,origin:c.rotation.origin.map((v,k)=>v+off[k])}:null});});
json(path.join(source,'00_colecao_firmamento.bbmodel'),bbProject(gallery,'free'));
json(path.join(source,'catalogue.json'),models.map(m=>({id:m.id,title:m.title,kind:m.kind,cubes:m.cubes.length,project:m.id+'.bbmodel'})));
console.log(`Generated ${models.length} game models, ${models.length+1} Blockbench projects and ${Object.keys(textures).length} original textures.`);
