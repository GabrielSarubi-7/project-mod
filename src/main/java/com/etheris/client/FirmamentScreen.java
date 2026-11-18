package com.etheris.client;

import com.etheris.gameplay.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Vector-drawn celestial instrument: crisp at every GUI scale, no borrowed interface textures. */
public final class FirmamentScreen extends AbstractContainerScreen<FirmamentMenu> {
    private int preview = -1;
    private static final int GOLD = 0xFFD9BC7B, INK = 0xFF0B0D19, LILAC = 0xFFB8AAEC;
    private static final String[] SCHOOL = {"ASTRA", "ASTRA", "VITA", "MATERIA", "FLUXO", "FULGOR", "VITA", "MATERIA"};
    private static final String[] EFFECT = {"Um corte de luz contra um monstro.", "Enxergue onde a noite esconde o caminho.", "Restaure seis pontos de vida.", "Pedregulho na outra mão vira pedra.", "Avance com velocidade e salto ampliados.", "Perfure a distância com luz fulgente.", "Faça uma plantação crescer um estágio.", "Ferro bruto na outra mão vira lingote."};
    private static final int[] COST = {8,15,20,15,10,18,12,25};
    public FirmamentScreen(FirmamentMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); imageWidth=360; imageHeight=230; }
    private int nodeX(int i) { return leftPos+100+(int)(65*Math.cos(-Math.PI/2+i*Math.PI/4)); }
    private int nodeY(int i) { return topPos+120+(int)(65*Math.sin(-Math.PI/2+i*Math.PI/4)); }
    private void line(GuiGraphics g, int x, int y, int xx, int yy, int color) {
        int steps=Math.max(Math.abs(xx-x),Math.abs(yy-y));
        for(int i=0;i<=steps;i++) { int px=x+(xx-x)*i/Math.max(1,steps), py=y+(yy-y)*i/Math.max(1,steps); g.fill(px,py,px+1,py+1,color); }
    }
    private void ring(GuiGraphics g,int x,int y,int radius,int color,double rotation) {
        for(int i=0;i<96;i++) { double a=rotation+i*Math.PI/48,b=rotation+(i+1)*Math.PI/48; line(g,x+(int)(radius*Math.cos(a)),y+(int)(radius*Math.sin(a)),x+(int)(radius*Math.cos(b)),y+(int)(radius*Math.sin(b)),color); }
    }
    @Override protected void renderBg(GuiGraphics g,float partial,int mx,int my) {
        int x=leftPos,y=topPos, chosen=preview<0?menu.selected():preview;
        double time=(minecraft.level==null?0:minecraft.level.getGameTime()+partial)*0.006;
        g.fillGradient(x,y,x+360,y+230,0xFA131426,0xFA080B13);
        g.fill(x,y,x+360,y+1,GOLD); g.fill(x,y+229,x+360,y+230,0xFF685B40);
        for(int i=0;i<54;i++) { int sx=x+8+(i*83)%344,sy=y+8+(i*47)%210; g.fill(sx,sy,sx+1,sy+1,(i%3==0?0x887F72BA:0x444F526C)); }
        g.drawString(font,"E T H E R I S",x+18,y+14,GOLD,false);
        g.drawString(font,"F I R M A M E N T O",x+18,y+29,0xFF9291B0,false);
        line(g,x+193,y+51,x+193,y+195,0xFF393449);
        ring(g,x+100,y+120,79,0xFF40374D,time); ring(g,x+100,y+120,73,0xFF6A5960,-time);
        for(int i=0;i<8;i++) {
            int nx=nodeX(i),ny=nodeY(i),jx=nodeX((i+3)%8),jy=nodeY((i+3)%8);
            line(g,nx,ny,jx,jy,0xFF302C48);
        }
        ring(g,x+100,y+120,24,0xFF665675,time);
        line(g,x+100,y+101,x+100,y+139,GOLD); line(g,x+81,y+120,x+119,y+120,GOLD);
        for(int i=0;i<8;i++) {
            int nx=nodeX(i),ny=nodeY(i); boolean known=i<=menu.unlocked(), focus=i==chosen;
            if(focus) { ring(g,nx,ny,13,GOLD,time); g.fill(nx-10,ny-10,nx+11,ny+11,0xFF302741); }
            int c=known?(focus?GOLD:LILAC):0xFF555366;
            line(g,nx,ny-7,nx+7,ny,c);line(g,nx+7,ny,nx,ny+7,c);line(g,nx,ny+7,nx-7,ny,c);line(g,nx-7,ny,nx,ny-7,c);
            g.drawCenteredString(font,known?Integer.toString(i+1):"·",nx,ny-4,c);
        }
        g.drawString(font,SCHOOL[chosen]+"  /  "+(chosen<=menu.unlocked()?"DESCOBERTO":"VELADO"),x+209,y+57,LILAC,false);
        g.drawWordWrap(font,Component.literal(Spells.NAMES[chosen]),x+209,y+78,135,GOLD);
        g.drawWordWrap(font,Component.literal(EFFECT[chosen]),x+209,y+111,132,0xFFC1BFD0);
        g.drawString(font,COST[chosen]+" ÉTER  /  1,5 s",x+209,y+153,0xFF9C98B4,false);
        boolean known=chosen<=menu.unlocked();
        g.fill(x+208,y+174,x+344,y+196,known?0xFF302A40:0xFF181923);
        g.drawCenteredString(font,known?(menu.selected()==chosen?"SINTONIZADA":"SINTONIZAR"):"AINDA NÃO DESCOBERTA",x+276,y+181,known?GOLD:0xFF777386);
        g.fill(x+18,y+211,x+168,y+214,0xFF282639);g.fill(x+18,y+211,x+18+menu.energy()*150/100,y+214,LILAC);
        g.drawString(font,"ÉTER  "+menu.energy()+" / 100",x+18,y+199,0xFFB1ACC5,false);
        g.drawString(font,"ESC  voltar e conjurar",x+209,y+211,0xFF8B869F,false);
    }
    @Override protected void renderLabels(GuiGraphics g,int x,int y) {}
    @Override public boolean mouseClicked(double x,double y,int button) {
        if(button==0) {
            for(int i=0;i<8;i++) if(Math.abs(x-nodeX(i))<=12&&Math.abs(y-nodeY(i))<=12) { preview=i; return true; }
            if(x>=leftPos+208&&x<=leftPos+344&&y>=topPos+174&&y<=topPos+196) {
                int selected=preview<0?menu.selected():preview;
                if(selected<=menu.unlocked()&&minecraft.gameMode!=null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId,selected);
                return true;
            }
        }
        return super.mouseClicked(x,y,button);
    }
}
