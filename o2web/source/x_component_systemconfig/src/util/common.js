if (window.NodeList && !NodeList.prototype.forEach) {
    NodeList.prototype.forEach = Array.prototype.forEach;
}
function _getRectPoint(rect){
    return {
        leftTop: {x: rect.x, y: rect.y},
        rightTop: {x: rect.x+rect.width, y: rect.y},
        leftBottom: {x: rect.x, y: rect.y+rect.height},
        rightBottom: {x: rect.x+rect.width, y: rect.y+rect.height},
    }
}
function _pointInRect(p, ps){
    return (p.x>=ps.leftTop.x && p.x<=ps.rightTop.x && p.y>=ps.leftTop.y && p.y<=ps.leftBottom.y)
}

function isOverlap(recta, rectb){
    const pa = _getRectPoint(recta);
    const pb = _getRectPoint(rectb);

    for (const k of ['leftTop', 'rightTop', 'leftBottom', 'rightBottom']){
        if (_pointInRect(pa[k], pb)) return true;
    }
    for (const k of ['leftTop', 'rightTop', 'leftBottom', 'rightBottom']){
        if (_pointInRect(pb[k], pa)) return true;
    }
    return false;
}
export {isOverlap};
