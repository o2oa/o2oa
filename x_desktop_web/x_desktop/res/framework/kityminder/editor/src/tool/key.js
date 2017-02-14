define(function(require, exports, module) {
    var keymap = require('./keymap');

    var CTRL_MASK = 0x1000;
    var ALT_MASK = 0x2000;
    var SHIFT_MASK = 0x4000;

    function hash(unknown) {
        if (typeof(unknown) == 'string') {
            return hashKeyExpression(unknown);
        }
        return hashKeyEvent(unknown);
    }
    function is(a, b) {
        return a && b && hash(a) == hash(b);
    }
    exports.hash = hash;
    exports.is = is;


    function hashKeyEvent(keyEvent) {
        var hashCode = 0;
        if (keyEvent.ctrlKey || keyEvent.metaKey) {
            hashCode |= CTRL_MASK;
        }
        if (keyEvent.altKey) {
            hashCode |= ALT_MASK;
        }
        if (keyEvent.shiftKey) {
            hashCode |= SHIFT_MASK;
        }
        // Shift, Control, Alt KeyCode ignored.
        if ([16, 17, 18, 91].indexOf(keyEvent.keyCode) === -1) {
            /**
             * 解决浏览器输入法状态下对keyDown的keyCode判断不准确的问题,使用keyIdentifier,
             * 可以解决chrome和safari下的各种问题,其他浏览器依旧有问题,然而那并不影响我们对特
             * 需判断的按键进行判断(比如Space在safari输入法态下就是229,其他的就不是)
             * @editor Naixor
             * @Date 2015-12-2
             */
            if (keyEvent.keyCode === 229 && keyEvent.keyIdentifier) {
                return hashCode |= parseInt(keyEvent.keyIdentifier.substr(2), 16);
            }
            hashCode |= keyEvent.keyCode;
        }
        return hashCode;
    }

    function hashKeyExpression(keyExpression) {
        var hashCode = 0;
        keyExpression.toLowerCase().split(/\s*\+\s*/).forEach(function(name) {
            switch(name) {
                case 'ctrl':
                case 'cmd':
                    hashCode |= CTRL_MASK;
                    break;
                case 'alt':
                    hashCode |= ALT_MASK;
                    break;
                case 'shift':
                    hashCode |= SHIFT_MASK;
                    break;
                default:
                    hashCode |= keymap[name];
            }
        });
        return hashCode;
    }
});
