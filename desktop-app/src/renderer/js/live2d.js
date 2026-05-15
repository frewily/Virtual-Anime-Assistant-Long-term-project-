console.log('[Live2D] Step 1: Loading PixiJS...');
const PIXI = require('pixi.js');
console.log('[Live2D] Step 2: PixiJS loaded, version:', PIXI.VERSION);

console.log('[Live2D] Step 3: Live2DCubismCore:', typeof window.Live2DCubismCore !== 'undefined');

console.log('[Live2D] Step 4: Loading pixi-live2d-display...');
const { Live2DModel } = require('pixi-live2d-display/cubism4');
console.log('[Live2D] Step 5: pixi-live2d-display loaded');

const { applyAction } = require('./actions');
const { showBubble } = require('./chat');

window.PIXI = PIXI;

let app, model, currentExpression = 'f01';
let isInitialized = false;
const SCALE_MIN = 0.5;
const SCALE_MAX = 1.2;
const SCALE_STEP = 0.05;
let scaleMultiplier = 1.0;
let scaleRafId = null;

function getBaseScale(container) {
    return (container.clientHeight / model.height) * 0.85;
}

function applyScale() {
    if (scaleRafId) cancelAnimationFrame(scaleRafId);
    scaleRafId = requestAnimationFrame(() => {
        if (!model) return;
        const container = document.getElementById('app');
        const scale = getBaseScale(container) * scaleMultiplier;
        model.scale.set(scale);
        model.x = container.clientWidth / 2;
        model.y = container.clientHeight * 0.55;
    });
}

console.log('[Live2D] Step 5: Live2DCubismCore exists:', typeof window.Live2DCubismCore !== 'undefined');

function initCanvas() {
    console.log('[Live2D] Step 6: initCanvas started');
    const container = document.getElementById('app');
    const loadingEl = container.querySelector('.loading');

    app = new PIXI.Application({
        view: document.getElementById('live2d-canvas'),
        width: container.clientWidth,
        height: container.clientHeight,
        backgroundAlpha: 0,
        antialias: true,
        resolution: window.devicePixelRatio || 1,
        autoDensity: true
    });
    console.log('[Live2D] Step 7: PIXI.Application created');

    app.stage.sortableChildren = true;

    const modelPath = '../../assets/models/hiyori/Hiyori.model3.json';
    console.log('[Live2D] Step 8: Loading model from:', modelPath);

    Live2DModel.from(modelPath)
        .then(async (loadedModel) => {
            console.log('[Live2D] Step 9: Model loaded successfully!');
            model = loadedModel;

            model.anchor.set(0.5, 0.5);
            model.interactive = true;
            model.cursor = 'pointer';
            applyScale();

            model.on('hit', (hitAreas) => {
                if (hitAreas && hitAreas.length > 0) {
                    const areaName = hitAreas[0];
                    if (areaName === 'Head') {
                        playMotion('tap_head');
                        showBubble('不要摸头啦~', 3000);
                    } else {
                        playMotion('tap_body');
                        showBubble('有什么事吗，主人？', 3000);
                    }
                }
            });

            app.stage.addChild(model);
            console.log('[Live2D] Step 10: Model added to stage');

            if (loadingEl) loadingEl.remove();
            isInitialized = true;
            console.log('[Live2D] DONE: Live2D model fully loaded');
        })
        .catch((err) => {
            console.error('[Live2D] ERROR:', err.message);
            console.error('[Live2D] Stack:', err.stack);
            if (loadingEl) {
                loadingEl.textContent = '模型加载失败: ' + (err.message || '未知错误');
                loadingEl.style.color = 'rgba(255,100,100,0.8)';
            }
        });

    window.addEventListener('resize', () => {
        if (app) {
            app.renderer.resize(container.clientWidth, container.clientHeight);
            applyScale();
        }
    });

    const canvas = document.getElementById('live2d-canvas');
    let lastWheelTime = 0;
    canvas.addEventListener('wheel', (e) => {
        if (e.ctrlKey || e.metaKey) {
            e.preventDefault();
            const now = Date.now();
            if (now - lastWheelTime < 100) return;
            lastWheelTime = now;
            scaleMultiplier += e.deltaY < 0 ? SCALE_STEP : -SCALE_STEP;
            scaleMultiplier = Math.max(SCALE_MIN, Math.min(SCALE_MAX, scaleMultiplier));
            applyScale();
            showBubble(Math.round(scaleMultiplier * 100) + '%', 1500);
        }
    }, { passive: false });

    window.addEventListener('keydown', (e) => {
        if (e.target.tagName === 'INPUT') return;
        if (e.key === '=' || e.key === '+') {
            e.preventDefault();
            scaleMultiplier = Math.min(SCALE_MAX, scaleMultiplier + SCALE_STEP);
            applyScale();
            showBubble(Math.round(scaleMultiplier * 100) + '%', 1500);
        } else if (e.key === '-') {
            e.preventDefault();
            scaleMultiplier = Math.max(SCALE_MIN, scaleMultiplier - SCALE_STEP);
            applyScale();
            showBubble(Math.round(scaleMultiplier * 100) + '%', 1500);
        } else if (e.key === '0') {
            e.preventDefault();
            scaleMultiplier = 1.0;
            applyScale();
            showBubble('100%', 1500);
        }
    });
}

function setExpression(expId) {
    if (!model || !isInitialized) return;
    model.expression(expId).then((success) => {
        if (success) {
            currentExpression = expId;
        }
    }).catch(() => { });
}

function playMotion(motionName) {
    if (!model || !isInitialized) return;
    try {
        model.motion(motionName);
    } catch (e) {
        console.warn('playMotion failed:', e.message);
    }
}

function getModel() {
    return model;
}

function getApp() {
    return app;
}

function setScaleMultiplier(val) {
    scaleMultiplier = Math.max(SCALE_MIN, Math.min(SCALE_MAX, val));
    applyScale();
    localStorage.setItem('assistant_scale', Math.round(scaleMultiplier * 100));
}

module.exports = { initCanvas, setExpression, playMotion, getModel, getApp, setScaleMultiplier };