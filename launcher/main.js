const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('path');
const fs = require('fs');
const { Client, Authenticator } = require('minecraft-launcher-core');

const MC = '1.16.5';
const LOADER = '0.14.9';
const FABRIC_ID = `fabric-loader-${LOADER}-${MC}`;
let win;

function createWindow() {
  win = new BrowserWindow({
    width: 960,
    height: 640,
    resizable: true,
    minWidth: 820,
    minHeight: 560,
    backgroundColor: '#0b0614',
    autoHideMenuBar: true,
    title: 'BEER VISUALS',
    webPreferences: { preload: path.join(__dirname, 'preload.js') }
  });
  win.loadFile('index.html');
}
app.whenReady().then(createWindow);
app.on('window-all-closed', () => app.quit());

const log = (m) => win && win.webContents.send('log', String(m));
const gameRoot = () => path.join(app.getPath('appData'), '.beervisuals');

function bundledMod() {
  const p = app.isPackaged
    ? path.join(process.resourcesPath, 'mods', 'beer_visuals.jar')
    : path.join(__dirname, 'resources', 'mods', 'beer_visuals.jar');
  return fs.existsSync(p) ? p : null;
}

async function download(url, dest) {
  const r = await fetch(url);
  if (!r.ok) throw new Error(`HTTP ${r.status} для ${url}`);
  fs.writeFileSync(dest, Buffer.from(await r.arrayBuffer()));
}

async function ensureFabric(root) {
  const dir = path.join(root, 'versions', FABRIC_ID);
  const json = path.join(dir, `${FABRIC_ID}.json`);
  if (fs.existsSync(json)) return;
  fs.mkdirSync(dir, { recursive: true });
  log('Завантажую Fabric loader...');
  await download(`https://meta.fabricmc.net/v2/versions/loader/${MC}/${LOADER}/profile/json`, json);
}

async function ensureFabricApi(modsDir) {
  if (fs.readdirSync(modsDir).some(f => f.toLowerCase().startsWith('fabric-api'))) return;
  log('Завантажую Fabric API (Modrinth)...');
  const q = `https://api.modrinth.com/v2/project/fabric-api/version?game_versions=${encodeURIComponent('["1.16.5"]')}&loaders=${encodeURIComponent('["fabric"]')}`;
  const r = await fetch(q);
  const versions = await r.json();
  if (!versions.length) throw new Error('Fabric API для 1.16.5 не знайдено');
  const file = versions[0].files.find(f => f.primary) || versions[0].files[0];
  await download(file.url, path.join(modsDir, file.filename));
}

let msAuth = null;

ipcMain.handle('login', async () => {
  try {
    const { Auth } = require('msmc');
    const authManager = new Auth('select_account');
    const xbox = await authManager.launch('electron');
    const token = await xbox.getMinecraft();
    msAuth = token.mclc();
    log('Вхід через Microsoft: ' + msAuth.name);
    return { ok: true, name: msAuth.name };
  } catch (e) {
    log('Помилка входу: ' + e.message);
    return { ok: false, error: e.message };
  }
});

ipcMain.handle('launch', async (_e, { nick, ram, modules, mode }) => {
  try {
    const root = gameRoot();
    const modsDir = path.join(root, 'mods');
    fs.mkdirSync(modsDir, { recursive: true });

    await ensureFabric(root);
    await ensureFabricApi(modsDir);

    const target = path.join(modsDir, 'beer_visuals.jar');
    const src = bundledMod();
    if (src) {
      fs.copyFileSync(src, target);
    } else {
      log('Увага: beer_visuals.jar не знайдено в resources/mods — мод не буде підключено');
    }

    const cfgDir = path.join(root, 'config');
    fs.mkdirSync(cfgDir, { recursive: true });
    fs.writeFileSync(path.join(cfgDir, 'beer_visuals_launcher.json'), JSON.stringify(modules || {}, null, 2));

    const launcher = new Client();
    launcher.on('debug', log);
    launcher.on('data', log);
    launcher.on('progress', p => log(`Завантаження ${p.type}: ${p.task}/${p.total}`));
    launcher.on('close', c => log(`Гру закрито (код ${c})`));

    log('Запуск...');
    await launcher.launch({
      authorization: mode === 'ms' ? msAuth : Authenticator.getAuth(nick || 'Player'),
      root,
      version: { number: MC, type: 'release', custom: FABRIC_ID },
      memory: { max: `${ram || 4}G`, min: '1G' }
    });
    return { ok: true };
  } catch (e) {
    log('Помилка: ' + e.message);
    return { ok: false, error: e.message };
  }
});
