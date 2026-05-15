#!/bin/bash
cd /Users/frewily/Interest-driven_project/Virtual-Anime-Assistant-Long-term-project-/desktop-app

killall Electron 2>/dev/null
sleep 1

case "${1:-run}" in
    run)
        npx electron . --no-sandbox --disable-gpu-sandbox 2>&1
        ;;
    dev)
        npx electron . --no-sandbox --disable-gpu-sandbox --dev 2>&1
        ;;
    build)
        ELECTRON_MIRROR="https://npmmirror.com/mirrors/electron/" npx electron-builder --dir
        echo ""
        echo "Build output: dist/mac-arm64/小安桌面助手.app"
        ;;
    dmg)
        ELECTRON_MIRROR="https://npmmirror.com/mirrors/electron/" npx electron-builder
        echo ""
        echo "DMG output: dist/小安桌面助手-*.dmg"
        ;;
    *)
        echo "Usage: ./start.sh [run|dev|build|dmg]"
        echo "  run   - 普通启动"
        echo "  dev   - 开发者模式 (开启 DevTools)"
        echo "  build - 构建应用 (.app)"
        echo "  dmg   - 构建 DMG 安装包"
        ;;
esac
read -p "Press Enter to close..."
