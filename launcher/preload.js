const { contextBridge, ipcRenderer } = require('electron');
contextBridge.exposeInMainWorld('api', {
  launch: (opts) => ipcRenderer.invoke('launch', opts),
  login: () => ipcRenderer.invoke('login'),
  onLog: (cb) => ipcRenderer.on('log', (_e, m) => cb(m))
});
