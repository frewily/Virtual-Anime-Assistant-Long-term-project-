const { app, BrowserWindow, Tray, Menu, nativeImage } = require('electron');
const path = require('path');

const isDev = process.argv.includes('--dev');

let mainWindow;
let tray;

function createWindow() {
    mainWindow = new BrowserWindow({
        width: 400,
        height: 600,
        minWidth: 240,
        minHeight: 360,
        maxWidth: 800,
        maxHeight: 1200,
        transparent: true,
        frame: false,
        alwaysOnTop: true,
        skipTaskbar: true,
        resizable: true,
        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false,
            webSecurity: false
        }
    });

    mainWindow.loadFile(path.join(__dirname, 'renderer/index.html'));

    if (isDev) {
        mainWindow.webContents.openDevTools({ mode: 'detach' });
    }

    mainWindow.webContents.on('console-message', (event, level, message) => {
        if (isDev) {
            console.log('[Renderer]', message);
        }
    });

    mainWindow.on('closed', () => {
        mainWindow = null;
    });
}

function createTray() {
    try {
        const iconPath = path.join(__dirname, 'assets/icon.png');
        const fs = require('fs');
        let trayIcon;
        if (fs.existsSync(iconPath)) {
            trayIcon = nativeImage.createFromPath(iconPath);
        } else {
            trayIcon = createMinimalIcon();
        }
        trayIcon = trayIcon.resize({ width: 16, height: 16 });
        tray = new Tray(trayIcon);
        const contextMenu = Menu.buildFromTemplate([
            { label: '显示', click: () => { if (mainWindow) mainWindow.show(); } },
            { label: '隐藏', click: () => { if (mainWindow) mainWindow.hide(); } },
            { type: 'separator' },
            { label: '退出', click: () => app.quit() }
        ]);
        tray.setToolTip('桌面助手 - 小安');
        tray.setContextMenu(contextMenu);
        tray.on('click', () => {
            if (mainWindow) {
                mainWindow.isVisible() ? mainWindow.hide() : mainWindow.show();
            }
        });
    } catch (e) {
        console.warn('Tray icon error:', e.message);
    }
}

function createMinimalIcon() {
    const size = 32;
    const canvas = Buffer.alloc(size * size * 4);
    for (let y = 0; y < size; y++) {
        for (let x = 0; x < size; x++) {
            const idx = (y * size + x) * 4;
            const cx = size / 2, cy = size / 2;
            const dx = x - cx, dy = y - cy;
            const dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < size / 2 - 2 && dist > 3) {
                canvas[idx] = 255;
                canvas[idx + 1] = 130;
                canvas[idx + 2] = 210;
                canvas[idx + 3] = 255;
            } else if (dist <= 3) {
                canvas[idx] = 255;
                canvas[idx + 1] = 220;
                canvas[idx + 2] = 130;
                canvas[idx + 3] = 255;
            }
        }
    }
    return nativeImage.createFromBuffer(canvas, { width: size, height: size });
}

app.whenReady().then(() => {
    createWindow();
    createTray();
    console.log('小安桌面助手已启动');
    if (isDev) console.log('  DevTools: 已开启');
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

app.on('activate', () => {
    if (mainWindow === null) {
        createWindow();
    }
});
