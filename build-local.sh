#!/usr/bin/env bash
set -e

echo "==> Building Fabric mod..."
cd mod
if [ -f gradlew ]; then
  ./gradlew build --no-daemon
else
  gradle build --no-daemon
fi
cd ..

JAR=$(ls mod/build/libs/beer_visuals-*.jar 2>/dev/null | head -1)
if [ -z "$JAR" ]; then
  echo "ERROR: jar not found after build"
  exit 1
fi

echo "==> Copying $JAR -> launcher/resources/mods/beer_visuals.jar"
mkdir -p launcher/resources/mods
cp "$JAR" launcher/resources/mods/beer_visuals.jar

echo "==> Building Electron portable..."
cd launcher
npm install
npm run dist

echo ""
echo "Done!"
echo "  Mod:      $JAR"
echo "  Launcher: launcher/dist/BeerVisuals.exe"
