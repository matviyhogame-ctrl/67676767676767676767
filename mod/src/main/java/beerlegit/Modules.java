package beerlegit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import java.util.List;

/** Beer Visuals modules. */
public final class Modules {
    private Modules() {}
    public static void init(List<Module> list) {
        list.add(new Module("HUD", Category.RENDER) {{
            bool("Watermark", true); bool("Info Panel", true); bool("Coordinates", true);
            bool("FPS", true); bool("Armor Status", true); bool("Potion Effects", true);
            bool("Enabled Modules", true); bool("Armor Durability", true);
        }});
        list.add(new Module("Crosshair", Category.RENDER) {{
            num("Size", 6, 2, 16); num("Gap", 3, 0, 10); num("Thickness", 1, 1, 4);
            num("Red", 190, 0, 255); num("Green", 150, 0, 255); num("Blue", 255, 0, 255);
            bool("Outline", true);
        }});
        list.add(new Module("HitMarkers", Category.RENDER) {{
            bool("Sound", true); bool("Particles", true); num("Size", 8, 4, 20); num("Duration", 12, 4, 30);
        }});
        list.add(new Module("Fullbright", Category.RENDER) {
            private double oldGamma = 1.0;
            @Override public void onEnable(MinecraftClient mc) { if (mc.options != null) { oldGamma = mc.options.gamma; mc.options.gamma = 16.0; } }
            @Override public void onDisable(MinecraftClient mc) { if (mc.options != null) mc.options.gamma = oldGamma; }
            @Override public void onTick(MinecraftClient mc) { if (mc.options != null && mc.options.gamma < 15.0) mc.options.gamma = 16.0; }
        });
        list.add(new Module("NoHurtCam", Category.RENDER));
        list.add(new Module("Armor Durability", Category.PLAYER) {{ bool("Show Percent", true); bool("Low Durability Warning", true); num("Warning At", 20, 5, 80); }});
        list.add(new Module("Animations", Category.RENDER) {{ bool("Swing Animation", true); num("Swing Speed", 1.0, 0.5, 2.0); }});
        list.add(new Module("Chat Timestamp", Category.MISC) {{ bool("24-hour Clock", true); }});

    }
}
