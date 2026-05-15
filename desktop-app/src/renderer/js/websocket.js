const { applyAction } = require('./actions');
const { showBubble, addToHistory } = require('./chat');
const { playAudio } = require('./api');

const WS_URL = 'ws://localhost:8080/ws/avatar';
let ws = null;
let reconnectTimer = null;
let reconnectAttempts = 0;
const MAX_RECONNECT_DELAY = 30000;

function connect() {
    try {
        ws = new WebSocket(WS_URL);

        ws.onopen = () => {
            console.log('WebSocket connected');
            reconnectAttempts = 0;
            updateStatusDot('connected');
        };

        ws.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                handleMessage(data);
            } catch (e) {
                console.error('Failed to parse WS message:', e);
            }
        };

        ws.onclose = () => {
            console.log('WebSocket disconnected');
            updateStatusDot('disconnected');
            scheduleReconnect();
        };

        ws.onerror = (err) => {
            console.error('WebSocket error:', err);
        };
    } catch (e) {
        console.error('WebSocket connect failed:', e);
        scheduleReconnect();
    }
}

function handleMessage(data) {
    switch (data.type) {
        case 'speak':
            handleSpeak(data);
            break;
        case 'action':
            handleAction(data);
            break;
        case 'status':
            handleStatus(data);
            break;
        default:
            console.log('Unknown message type:', data.type);
    }
}

function handleSpeak(data) {
    if (data.text) {
        showBubble(data.text);
        addToHistory('assistant', data.text);
    }
    if (data.expression || data.motion) {
        applyAction(data.expression, data.motion);
    }
    if (data.audioUrl) {
        const audioId = data.audioUrl.split('/').pop();
        if (audioId) {
            updateStatusDot('speaking');
            playAudio(audioId).finally(() => {
                updateStatusDot('connected');
            });
        }
    }
}

function handleAction(data) {
    if (data.expression || data.motion) {
        applyAction(data.expression, data.motion);
    }
}

function handleStatus(data) {
    console.log('Status update:', data);
}

function scheduleReconnect() {
    if (reconnectTimer) return;
    const delay = Math.min(1000 * Math.pow(2, reconnectAttempts), MAX_RECONNECT_DELAY);
    reconnectAttempts++;
    console.log(`Reconnecting in ${delay}ms (attempt ${reconnectAttempts})`);
    reconnectTimer = setTimeout(() => {
        reconnectTimer = null;
        connect();
    }, delay);
}

function updateStatusDot(state) {
    const dot = document.getElementById('ws-dot');
    if (!dot) return;
    dot.className = 'status-dot';
    if (state === 'disconnected') dot.classList.add('disconnected');
    if (state === 'speaking') dot.classList.add('speaking');
}

function send(data) {
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify(data));
    }
}

module.exports = { connect, send };