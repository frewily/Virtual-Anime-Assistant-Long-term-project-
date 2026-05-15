const BACKEND_URL = 'http://localhost:8080/api';

const defaultHeaders = { 'Content-Type': 'application/json' };

async function getStatus() {
    try {
        const response = await fetch(`${BACKEND_URL}/status`);
        return await response.json();
    } catch (e) {
        console.error('getStatus failed:', e);
        return null;
    }
}

async function getCurrentActivity() {
    try {
        const response = await fetch(`${BACKEND_URL}/report/activity/current`);
        return await response.json();
    } catch (e) {
        console.error('getCurrentActivity failed:', e);
        return null;
    }
}

async function sendChatMessage(content, source) {
    try {
        const response = await fetch(`${BACKEND_URL}/message`, {
            method: 'POST',
            headers: defaultHeaders,
            body: JSON.stringify({ content, source: source || 'desktop', senderId: 'local_user' })
        });
        return await response.json();
    } catch (e) {
        console.error('sendChatMessage failed:', e);
        return null;
    }
}

async function triggerScenario(scenarioId) {
    try {
        const response = await fetch(`${BACKEND_URL}/message/trigger`, {
            method: 'POST',
            headers: defaultHeaders,
            body: JSON.stringify({ scenarioId })
        });
        return await response.json();
    } catch (e) {
        console.error('triggerScenario failed:', e);
        return null;
    }
}

async function playAudio(audioId) {
    try {
        const response = await fetch(`${BACKEND_URL}/tts/audio/${audioId}`);
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        const audio = new Audio(url);
        audio.play();
        audio.onended = () => URL.revokeObjectURL(url);
    } catch (e) {
        console.error('playAudio failed:', e);
    }
}

module.exports = { getStatus, getCurrentActivity, sendChatMessage, triggerScenario, playAudio };