const API_BASE = 'http://localhost:8080/api';

async function getStatus() {
    const response = await fetch(`${API_BASE}/status`);
    return response.json();
}

async function getCurrentActivity() {
    const response = await fetch(`${API_BASE}/activity/current`);
    return response.json();
}

async function sendChatMessage(message) {
    const response = await fetch(`${API_BASE}/message`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(message)
    });
    return response.json();
}

module.exports = {
    getStatus,
    getCurrentActivity,
    sendChatMessage
};
