define(function(require, exports, module) {
    var theme = require('../core/theme');

    ['classic', 'classic-compact'].forEach(function(name) {
        var compact = name == 'classic-compact';

        /* jscs:disable maximumLineLength */
        theme.register(name, {
            'background': '#3A4144 url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAIAAAACDbGyAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDowQzg5QTQ0NDhENzgxMUUzOENGREE4QTg0RDgzRTZDNyIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDowQzg5QTQ0NThENzgxMUUzOENGREE4QTg0RDgzRTZDNyI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOkMwOEQ1NDRGOEQ3NzExRTM4Q0ZEQThBODREODNFNkM3IiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOkMwOEQ1NDUwOEQ3NzExRTM4Q0ZEQThBODREODNFNkM3Ii8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+e9P33AAAACVJREFUeNpisXJ0YUACTAyoAMr/+eM7EGGRZ4FQ7BycEAZAgAEAHbEGtkoQm/wAAAAASUVORK5CYII=") repeat',

            'root-color': '#430',
            'root-background': '#e9df98',
            'root-stroke': '#e9df98',
            'root-font-size': 24,
            'root-padding': compact ? [10, 25] : [15, 25],
            'root-margin': compact ? [15, 25] : [30, 100],
            'root-radius': 30,
            'root-space': 10,
            'root-shadow': 'rgba(0, 0, 0, .25)',

            'main-color': '#333',
            'main-background': '#a4c5c0',
            'main-stroke': '#a4c5c0',
            'main-font-size': 16,
            'main-padding': compact ? [5, 15] : [6, 20],
            'main-margin': compact ? [5, 10] : 20,
            'main-radius': 10,
            'main-space': 5,
            'main-shadow': 'rgba(0, 0, 0, .25)',

            'sub-color': 'white',
            'sub-background': 'transparent',
            'sub-stroke': 'none',
            'sub-font-size': 12,
            'sub-padding': [5, 10],
            'sub-margin': compact ? [5, 10] : [15, 20],
            'sub-tree-margin': 30,
            'sub-radius': 5,
            'sub-space': 5,

            'connect-color': 'white',
            'connect-width': 2,
            'main-connect-width': 3,
            'connect-radius': 5,

            'selected-background': 'rgb(254, 219, 0)',
            'selected-stroke': 'rgb(254, 219, 0)',
            'selected-color': 'black',

            'marquee-background': 'rgba(255,255,255,.3)',
            'marquee-stroke': 'white',

            'drop-hint-color': 'yellow',
            'sub-drop-hint-width': 2,
            'main-drop-hint-width': 4,
            'root-drop-hint-width': 4,

            'order-hint-area-color': 'rgba(0, 255, 0, .5)',
            'order-hint-path-color': '#0f0',
            'order-hint-path-width': 1,

            'text-selection-color': 'rgb(27,171,255)',
            'line-height':1.5
        });
    });
});