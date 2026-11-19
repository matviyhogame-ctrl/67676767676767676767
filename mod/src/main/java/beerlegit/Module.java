package beerlegit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public final String name;
    public final Category category;
    public boolean enabled;
    public boolean expanded;
    public final List<Setting> settings = new ArrayList<>();

    public Module(String name, Category category) { this.name = name; this.category = category; }

    protected Setting num(String n, double def, double min, double max) {
        Setting s = new Setting(n, def, min, max); settings.add(s); return s;
    }
    protected Setting bool(String n, boolean def) {
        Setting s = new Setting(n, def); settings.add(s); return s;
    }

    public void toggle() {
        enabled = !enabled;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (enabled) onEnable(mc); else onDisable(mc);
    }

    public void onEnable(MinecraftClient mc) {}
    public void onDisable(MinecraftClient mc) {}
    public void onTick(MinecraftClient mc) {}
    public void onRender(MatrixStack ms, Camera cam, float tickDelta) {}
}
