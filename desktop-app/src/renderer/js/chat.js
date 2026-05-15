let bubbleTextEl = null;
const bubbleEl = createBubble();
const historyEl = createHistory();

function createBubble() {
    const el = document.createElement('div');
    el.id = 'speech-bubble';
    el.style.cssText = `
        position: absolute;
        top: 8%;
        left: 50%;
        transform: translateX(-50%);
        background: rgba(255,255,255,0.92);
        color: #333;
        padding: 10px 16px;
        border-radius: 14px;
        font-size: 13px;
        width: 320px;
        max-height: 200px;
        overflow-y: auto;
        text-align: left;
        pointer-events: none;
        opacity: 0;
        transition: opacity 0.3s ease;
        box-shadow: 0 2px 12px rgba(0,0,0,0.15);
        z-index: 10;
        line-height: 1.6;
        word-break: break-word;
    `;
    el.addEventListener('wheel', (e) => e.stopPropagation());

    const textSpan = document.createElement('span');
    textSpan.style.display = 'block';
    textSpan.style.paddingRight = '8px';
    el.appendChild(textSpan);
    bubbleTextEl = textSpan;

    const arrow = document.createElement('div');
    arrow.style.cssText = `
        position: absolute;
        bottom: -8px;
        left: 50%;
        transform: translateX(-50%);
        width: 0;
        height: 0;
        border-left: 8px solid transparent;
        border-right: 8px solid transparent;
        border-top: 8px solid rgba(255,255,255,0.92);
    `;
    el.appendChild(arrow);

    document.body.appendChild(el);
    return el;
}

let bubbleTimer = null;

function showBubble(text, duration) {
    if (bubbleTimer) clearTimeout(bubbleTimer);
    bubbleTextEl.textContent = text;
    bubbleEl.style.opacity = '1';

    const autoHide = duration || Math.max(4000, text.length * 100);
    bubbleTimer = setTimeout(() => {
        bubbleEl.style.opacity = '0';
    }, autoHide);
}

function hideBubble() {
    if (bubbleTimer) clearTimeout(bubbleTimer);
    bubbleEl.style.opacity = '0';
}

function createHistory() {
    const el = document.getElementById('chat-history');
    if (el) return el;
    return null;
}

function addToHistory(role, text) {
    if (!historyEl) return;
    const msg = document.createElement('div');
    msg.className = 'chat-msg chat-msg-' + role;
    msg.textContent = text;
    historyEl.appendChild(msg);
    historyEl.scrollTop = historyEl.scrollHeight;
}

module.exports = { showBubble, hideBubble, addToHistory };