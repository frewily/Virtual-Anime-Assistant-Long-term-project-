#!/bin/bash
set -e

BOT_DIR="$(cd "$(dirname "$0")" && pwd)"
COMPOSE_FILE="$BOT_DIR/docker-compose.yml"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo "=============================="
echo -e "  ${CYAN}小安 QQ Bot 启动器${NC}"
echo "  Protocol: NapCatQQ (OneBot v11)"
echo "  Image:    ghcr.io/napneko/napcatappimagebuild"
echo "=============================="
echo ""

if ! command -v docker &>/dev/null; then
    echo -e "${RED}[错误]${NC} 未找到 Docker，请先安装 Docker Desktop"
    echo "  brew install --cask docker"
    exit 1
fi

if ! docker info &>/dev/null; then
    echo -e "${RED}[错误]${NC} Docker 未运行，请先启动 Docker Desktop"
    exit 1
fi

mkdir -p "$BOT_DIR/data/QQ" "$BOT_DIR/data/napcat"

case "${1:-start}" in
    start)
        echo -e "${YELLOW}[1/3]${NC} 拉取最新镜像..."
        COMPOSE_FILE="$BOT_DIR/docker-compose.yml"
        
        if ! docker compose -f "$COMPOSE_FILE" pull 2>/dev/null; then
            echo -e "${YELLOW}[提示]${NC} GHCR 拉取失败，尝试 Docker Hub..."
            docker compose -f "$BOT_DIR/docker-compose.dh.yml" pull 2>/dev/null && {
                COMPOSE_FILE="$BOT_DIR/docker-compose.dh.yml"
            } || {
                echo -e "${YELLOW}[提示]${NC} 都失败了，使用已有本地镜像启动..."
            }
        fi

        echo -e "${YELLOW}[2/3]${NC} 启动容器..."
        docker compose -f "$COMPOSE_FILE" up -d

        echo -e "${YELLOW}[3/3]${NC} 等待 QQ 客户端初始化..."
        sleep 5

        echo ""
        echo -e "${GREEN}✔ NapCatQQ 已启动${NC}"
        echo ""
        echo -e "┌─────────────────────────────────────────────┐"
        echo -e "│  ${CYAN}WebUI 地址:${NC}  http://localhost:6099/webui       │"
        echo -e "│  ${CYAN}登录令牌:${NC}    napcat                           │"
        echo -e "│                                             │"
        echo -e "│  ${CYAN}配置步骤:${NC}                                  │"
        echo -e "│  1. 浏览器打开 WebUI                         │"
        echo -e "│  2. 进入「网络配置」→ 添加反向 WebSocket      │"
        echo -e "│  3. 地址填写:                                │"
        echo -e "│     ${YELLOW}ws://host.docker.internal:8080/ws/qq${NC}       │"
        echo -e "│  4. 回到 WebUI 首页扫码登录 QQ               │"
        echo -e "└─────────────────────────────────────────────┘"
        echo ""
        echo -e "  查看日志: ${CYAN}docker logs -f qq-bot-napcat${NC}"
        ;;
    stop)
        echo "停止容器..."
        docker compose -f "$COMPOSE_FILE" stop
        echo -e "${GREEN}✔ 已停止${NC}"
        ;;
    restart)
        echo "重启容器..."
        docker compose -f "$COMPOSE_FILE" restart
        echo -e "${GREEN}✔ 已重启${NC}"
        ;;
    logs)
        docker logs -f qq-bot-napcat
        ;;
    status)
        if docker ps --format '{{.Names}}' | grep -q 'qq-bot-napcat'; then
            echo -e "${GREEN}✔ NapCatQQ 运行中${NC}"
            docker ps --filter "name=qq-bot-napcat" --format "  ID: {{.ID}}  Status: {{.Status}}"
        else
            echo -e "${RED}✘ NapCatQQ 未运行${NC}"
        fi
        ;;
    clean)
        echo -e "${RED}此操作将删除容器及所有持久化数据（需重新扫码登录）${NC}"
        read -p "确认？(yes/no): " confirm
        if [ "$confirm" = "yes" ]; then
            docker compose -f "$COMPOSE_FILE" down -v
            rm -rf "$BOT_DIR/data"
            echo -e "${GREEN}✔ 已清理${NC}"
        else
            echo "已取消"
        fi
        ;;
    update)
        echo "更新镜像并重启..."
        docker compose -f "$COMPOSE_FILE" pull
        docker compose -f "$COMPOSE_FILE" down
        docker compose -f "$COMPOSE_FILE" up -d
        echo -e "${GREEN}✔ 已更新${NC}"
        ;;
    *)
        echo "用法: ./start.sh [start|stop|restart|logs|status|clean|update]"
        echo ""
        echo "  start    - 启动 NapCatQQ（默认）"
        echo "  stop     - 停止容器"
        echo "  restart  - 重启容器"
        echo "  logs     - 查看实时日志"
        echo "  status   - 查看运行状态"
        echo "  clean    - 彻底清理（删除数据，需重新扫码）"
        echo "  update   - 更新镜像并重启"
        ;;
esac
