package beerlegit;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import java.util.ArrayList;
import java.util.List;

/** Six-column Beer Visuals interface for Minecraft 1.16.5. */
public class GuiScreen extends Screen {
    private static final int GAP = 6, TOP = 42, BOTTOM = 28;
    private Module selectedMod;
    private Setting dragging;
    private int dragX, dragW;
    private boolean showSettings;
    private final List<Hit> hits = new ArrayList<>();
    private static class Hit {
        String type; Category category; Module module; Setting setting;
        int x,y,w,h;
        Hit(String t,int x,int y,int w,int h){type=t;this.x=x;this.y=y;this.w=w;this.h=h;}
    }
    public GuiScreen(){ super(new LiteralText("BEER VISUALS")); }
    @Override public boolean isPauseScreen(){ return false; }
    @Override public void removed(){ BeerLegit.saveConfig(); }
    @Override public void init(){ hits.clear(); }
    @Override public boolean keyPressed(int key,int scan,int modifiers){
        if(key==org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT || key==256){ onClose(); return true; }
        return super.keyPressed(key,scan,modifiers);
    }
    private void hit(String type,int x,int y,int w,int h,Category c,Module m,Setting s){
        Hit a=new Hit(type,x,y,w,h);a.category=c;a.module=m;a.setting=s;hits.add(a);
    }
    private void panel(MatrixStack ms,int x,int y,int w,int h){ fill(ms,x,y,x+w,y+h,0xE9161420); fill(ms,x,y,x+w,y+2,0xFF8B5CF6); }
    @Override public void render(MatrixStack ms,int mouseX,int mouseY,float delta){
        fill(ms,0,0,width,height,0x99070A12); hits.clear();
        textRenderer.drawWithShadow(ms,"BEER VISUALS",18,14,0xFFD8B4FE);
        textRenderer.drawWithShadow(ms,"1.16.5  •  VISUAL CLIENT",112,14,0xFFAAA3B8);
        textRenderer.drawWithShadow(ms,"RIGHT SHIFT  ·  CLOSE",width-145,14,0xFFAAA3B8);
        int margin=14, colW=(width-margin*2-GAP*5)/6;
        Category[] cats=Category.values();
        for(int ci=0;ci<cats.length;ci++){
            Category cat=cats[ci]; int x=margin+ci*(colW+GAP), y=TOP, h=height-TOP-BOTTOM;
            panel(ms,x,y,colW,h);
            fill(ms,x+1,y+1,x+colW-1,y+27,0xFF242130);
            String title=cat.label; textRenderer.drawWithShadow(ms,title,x+8,y+10,0xFFE9D5FF);
            if(cat==Category.CONFIGS){
                textRenderer.drawWithShadow(ms,"PROFILE",x+8,y+43,0xFF9F94B5);
                fill(ms,x+6,y+59,x+colW-6,y+83,0xFF302344);
                textRenderer.drawWithShadow(ms,"Default",x+13,y+67,0xFFFFFFFF);
                hit("save",x+6,y+59,colW-12,24,cat,null,null);
                fill(ms,x+6,y+91,x+colW-6,y+115,0xFF302344);
                textRenderer.drawWithShadow(ms,"Save config",x+13,y+99,0xFFC4B5FD);
                hit("save",x+6,y+91,colW-12,24,cat,null,null);
                fill(ms,x+6,y+123,x+colW-6,y+147,0xFF302344);
                textRenderer.drawWithShadow(ms,"Reload config",x+13,y+131,0xFFC4B5FD);
                hit("reload",x+6,y+123,colW-12,24,cat,null,null);
                textRenderer.drawWithShadow(ms,"Saved to config/",x+8,y+170,0xFF8F879E);
                textRenderer.drawWithShadow(ms,"beer_visuals_settings",x+8,y+182,0xFF8F879E);
                continue;
            }
            int yy=y+34; boolean any=false;
            for(Module m:BeerLegit.MODULES){
                if(m.category!=cat) continue; any=true;
                if(yy+25>y+h-5) break;
                fill(ms,x+5,yy,x+colW-5,yy+23,m.enabled?0xFF302342:0xFF201E29);
                if(m.enabled) fill(ms,x+5,yy,x+7,yy+23,0xFFB58AFB);
                textRenderer.drawWithShadow(ms,m.name,x+10,yy+8, m.enabled?0xFFFFFFFF:0xFFB9B2C7);
                int sw=x+colW-24;
                fill(ms,sw,yy+7,sw+14,yy+16,m.enabled?0xFF9B72F5:0xFF514B5C);
                fill(ms,m.enabled?sw+8:sw+1,yy+8,m.enabled?sw+13:sw+6,yy+15,0xFFFFFFFF);
                hit("toggle",x+5,yy,colW-30,23,cat,m,null);
                hit("settings",x+colW-25,yy,20,23,cat,m,null);
                textRenderer.drawWithShadow(ms,"⚙",x+colW-21,yy+7,0xFFBFA5F5);
                yy+=27;
            }
            if(!any){
                textRenderer.drawWithShadow(ms,"Visual-only",x+8,y+49,0xFF8F879E);
                textRenderer.drawWithShadow(ms,"No modules",x+8,y+62,0xFF8F879E);
                if(cat==Category.COMBAT||cat==Category.MOVEMENT){
                    textRenderer.drawWithShadow(ms,"Combat automation",x+8,y+88,0xFF777080);
                    textRenderer.drawWithShadow(ms,"is intentionally",x+8,y+100,0xFF777080);
                    textRenderer.drawWithShadow(ms,"not included.",x+8,y+112,0xFF777080);
                }
            }
        }
        if(showSettings && selectedMod!=null) renderSettings(ms);
        textRenderer.drawWithShadow(ms,"LMB: toggle  •  gear: settings  •  changes save on close",18,height-17,0xFF9E96AE);
        super.render(ms,mouseX,mouseY,delta);
    }
    private void renderSettings(MatrixStack ms){
        int w=Math.min(260,width-30), h=Math.min(260,height-70), x=(width-w)/2,y=(height-h)/2;
        fill(ms,0,0,width,height,0x88000000); panel(ms,x,y,w,h);
        textRenderer.drawWithShadow(ms,selectedMod.name+" SETTINGS",x+12,y+12,0xFFE9D5FF);
        textRenderer.drawWithShadow(ms,"X",x+w-18,y+12,0xFFFFFFFF); hit("close",x+w-26,y+4,22,22,null,null,null);
        int sy=y+38;
        for(Setting s:selectedMod.settings){
            if(sy+29>y+h-8) break;
            textRenderer.drawWithShadow(ms,s.name,x+12,sy+3,0xFFE7E1F0);
            if(s.bool){
                int bx=x+w-35; fill(ms,bx,sy,bx+22,sy+12,s.on()?0xFF9867F5:0xFF514B5C);
                fill(ms,s.on()?bx+12:bx+2,sy+2,s.on()?bx+20:bx+10,sy+10,0xFFFFFFFF);
                hit("bool",x+8,sy-3,w-16,20,null,null,s);
            }else{
                String val=String.format(java.util.Locale.ROOT,"%.2f",s.value);
                textRenderer.drawWithShadow(ms,val,x+w-48,sy+3,0xFFC4B5FD);
                int bx=x+12,bw=w-24,by=sy+18; fill(ms,bx,by,bx+bw,by+4,0xFF494253);
                int fw=(int)(bw*(s.value-s.min)/(s.max-s.min)); fill(ms,bx,by,bx+fw,by+4,0xFFAD83FA);
                hit("slider",bx,sy+12,bw,14,null,null,s);
            }
            sy+=31;
        }
    }
    private void setSlider(Setting s,double mx,int x,int w){double f=Math.max(0,Math.min(1,(mx-x)/(double)w));s.value=Math.round((s.min+(s.max-s.min)*f)*100.0)/100.0;}
    @Override public boolean mouseClicked(double mx,double my,int button){
        if(button==1 && showSettings){showSettings=false;return true;}
        for(int hi=hits.size()-1;hi>=0;hi--){Hit h=hits.get(hi);if(mx<h.x||mx>=h.x+h.w||my<h.y||my>=h.y+h.h)continue;
            if(h.type.equals("toggle")){h.module.toggle();return true;}
            if(h.type.equals("settings")){selectedMod=h.module;showSettings=true;return true;}
            if(h.type.equals("close")){showSettings=false;return true;}
            if(h.type.equals("save")){BeerLegit.saveConfig();return true;}
            if(h.type.equals("reload")){BeerLegit.reloadConfig();return true;}
            if(h.type.equals("bool")){h.setting.value=h.setting.on()?0:1;return true;}
            if(h.type.equals("slider")){dragging=h.setting;dragX=h.x;dragW=h.w;setSlider(dragging,mx,dragX,dragW);return true;}
        }
        if(showSettings && button==0)return true;
        return super.mouseClicked(mx,my,button);
    }
    @Override public boolean mouseDragged(double mx,double my,int button,double dx,double dy){if(dragging!=null){setSlider(dragging,mx,dragX,dragW);return true;}return super.mouseDragged(mx,my,button,dx,dy);}
    @Override public boolean mouseReleased(double mx,double my,int button){dragging=null;return super.mouseReleased(mx,my,button);}
}
