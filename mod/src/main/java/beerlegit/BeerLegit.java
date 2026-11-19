package beerlegit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Beer Visuals — pure visual client for Minecraft 1.16.5 Fabric.
 * No cheats. Only cosmetics, HUD and quality-of-life visuals.
 */
public class BeerLegit implements ClientModInitializer {
    public static final List<Module> MODULES = new ArrayList<>();
    public static boolean wasDown = false;

    public static Module get(String name) {
        for (Module m : MODULES) if (m.name.equals(name)) return m;
        return null;
    }

    @Override
    public void onInitializeClient() {
        Modules.init(MODULES);

        Module hud = get("HUD");
        if (hud != null) hud.enabled = true;

        loadConfig();

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.getWindow() != null) {
                boolean down = InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT);
                if (down && !wasDown && mc.currentScreen == null && mc.player != null) {
                    mc.openScreen(new GuiScreen());
                }
                wasDown = down;
            }
            if (mc.player == null || mc.world == null) return;
            for (Module m : MODULES) if (m.enabled) m.onTick(mc);
        });

        HudRenderCallback.EVENT.register(Hud::render);
    }

    private static Path cfg(String file) {
        return FabricLoader.getInstance().getConfigDir().resolve(file);
    }

    public static void saveConfig() {
        try {
            JsonObject o = new JsonObject();
            for (Module m : MODULES) {
                o.addProperty(m.name + ".enabled", m.enabled);
                for (Setting s : m.settings) {
                    o.addProperty(m.name + "." + s.name, s.value);
                }
            }
            Files.write(cfg("beer_visuals_settings.json"), o.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            System.err.println("[BeerVisuals] save error: " + e);
        }
    }

    public static void reloadConfig() {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            Path f = cfg("beer_visuals_settings.json");
            if (Files.exists(f)) {
                JsonObject o = new JsonParser().parse(new String(Files.readAllBytes(f), "UTF-8")).getAsJsonObject();
                for (Module m : MODULES) {
                    String ek = m.name + ".enabled";
                    if (o.has(ek)) m.enabled = o.get(ek).getAsBoolean();
                    for (Setting s : m.settings) {
                        String k = m.name + "." + s.name;
                        if (o.has(k)) s.value = o.get(k).getAsDouble();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[BeerVisuals] config error: " + e);
        }
    }
}
