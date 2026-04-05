const PIXI = require('pixi.js');
const { Live2DModel } = require('pixi-live2d-display');

let app;
let model;

async function initLive2D() {
    app = new PIXI.Application({
        view: document.getElementById('live2d-container'),
        transparent: true,
        autoStart: true,
        width: 400,
        height: 600
    });

    try {
        model = await Live2DModel.from('assets/models/hiyori/hiyori.model3.json');
        
        app.stage.addChild(model);
        
        model.scale.set(0.3);
        model.anchor.set(0.5, 0.5);
        model.x = 200;
        model.y = 400;
        
        model.on('hit', (hitAreas) => {
            if (hitAreas.includes('head')) {
                model.motion('tap_head');
            } else if (hitAreas.includes('body')) {
                model.motion('tap_body');
            }
        });
        
        console.log('Live2D model loaded successfully');
    } catch (error) {
        console.error('Failed to load Live2D model:', error);
    }
}

function setExpression(expressionName) {
    if (model) {
        model.expression(expressionName);
    }
}

function playMotion(motionName) {
    if (model) {
        model.motion(motionName);
    }
}

function getModel() {
    return model;
}

window.addEventListener('DOMContentLoaded', initLive2D);

module.exports = {
    setExpression,
    playMotion,
    getModel
};
