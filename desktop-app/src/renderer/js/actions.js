let live2dModule = null;

function getLive2d() {
    if (!live2dModule) {
        live2dModule = require('./live2d');
    }
    return live2dModule;
}

const expressionMap = {
    'idle': 'f01',
    'happy': 'f01',
    'curious': 'f02',
    'sleepy': 'f03',
    'worried': 'f04',
    'surprised': 'f05',
    'angry': 'f06'
};

const motionMap = {
    'idle': 'idle',
    'wave': 'tap_body',
    'shake': 'shake',
    'point': 'tap_head',
    'yawn': 'pinch_in',
    'tap_body': 'tap_body',
    'tap_head': 'tap_head',
    'tilt': 'tilt'
};

function applyExpression(name) {
    const mapped = expressionMap[name] || name;
    getLive2d().setExpression(mapped);
}

function applyMotion(name) {
    const mapped = motionMap[name] || name;
    getLive2d().playMotion(mapped);
}

function applyAction(expression, motion) {
    if (expression) applyExpression(expression);
    if (motion) applyMotion(motion);
}

module.exports = { applyExpression, applyMotion, applyAction };