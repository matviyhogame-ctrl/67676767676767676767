# BEER VISUALS 2.1.0

Fabric client mod for Minecraft 1.16.5.

## Included
- Six-column ClickGUI (Right Shift)
- HUD watermark, coordinates, FPS, health, armor, potion effects and enabled-module list
- Configurable RGB crosshair
- Fullbright with gamma restoration when disabled
- Armor durability percentage lines
- ClickPearl: right-click to throw an ender pearl from the hotbar and restore the previous selected slot
- AutoTotem: moves a totem from the inventory into the offhand when the offhand does not already contain one
- JSON settings persistence in `.minecraft/config/beer_visuals_settings.json`

## Build
Run `./mod/gradlew -p mod build` (Linux/macOS) or `mod\\gradlew.bat -p mod build` (Windows), with Java 17 and network access for Gradle dependencies.

Open the ClickGUI with Right Shift. Enable ClickPearl and/or AutoTotem in their categories.

Note: NoHurtCam, HitMarkers, custom swing animation and chat timestamps are still UI/config entries and are not implemented as complete runtime features in this build. Build compilation must be verified by GitHub Actions because this environment cannot download the Gradle distribution.
