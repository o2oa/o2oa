MWF.xApplication.MinderEditor = MWF.xApplication.MinderEditor || {};

if ((!('innerText' in document.createElement('a'))) && ('getSelection' in window)) {
    HTMLElement.prototype.__defineGetter__('innerText', function() {
        var selection = window.getSelection(),
            ranges    = [],
            str, i;

        // Save existing selections.
        for (i = 0; i < selection.rangeCount; i++) {
            ranges[i] = selection.getRangeAt(i);
        }

        // Deselect everything.
        selection.removeAllRanges();

        // Select `el` and all child nodes.
        // 'this' is the element .innerText got called on
        selection.selectAllChildren(this);

        // Get the string representation of the selected nodes.
        str = selection.toString();

        // Deselect everything. Again.
        selection.removeAllRanges();

        // Restore all formerly existing selections.
        for (i = 0; i < ranges.length; i++) {
            selection.addRange(ranges[i]);
        }

        // Oh look, this is what we wanted.
        // String representation of the element, close to as rendered.
        return str;
    });
    HTMLElement.prototype.__defineSetter__('innerText', function(text) {
        /**
         * @Desc: 解决FireFox节点内容删除后text为null，出现报错的问题
         * @Editor: Naixor
         * @Date: 2015.9.16
         */
        this.innerHTML = (text || '').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/\n/g, '<br>');
    });
}

(function(){
    var keymap = MWF.xApplication.MinderEditor.KeyMap = {

        'Shift': 16,
        'Control': 17,
        'Alt': 18,
        'CapsLock': 20,

        'BackSpace': 8,
        'Tab': 9,
        'Enter': 13,
        'Esc': 27,
        'Space': 32,

        'PageUp': 33,
        'PageDown': 34,
        'End': 35,
        'Home': 36,

        'Insert': 45,

        'Left': 37,
        'Up': 38,
        'Right': 39,
        'Down': 40,

        'Direction': {
            37: 1,
            38: 1,
            39: 1,
            40: 1
        },

        'Del': 46,

        'NumLock': 144,

        'Cmd': 91,
        'CmdFF': 224,
        'F1': 112,
        'F2': 113,
        'F3': 114,
        'F4': 115,
        'F5': 116,
        'F6': 117,
        'F7': 118,
        'F8': 119,
        'F9': 120,
        'F10': 121,
        'F11': 122,
        'F12': 123,

        '`': 192,
        '=': 187,
        '-': 189,

        '/': 191,
        '.': 190
    };

// 小写适配
    for (var key in keymap) {
        if (keymap.hasOwnProperty(key)) {
            keymap[key.toLowerCase()] = keymap[key];
        }
    }
    var aKeyCode = 65;
    var aCharCode = 'a'.charCodeAt(0);

// letters
    'abcdefghijklmnopqrstuvwxyz'.split('').forEach(function(letter) {
        keymap[letter] = aKeyCode + (letter.charCodeAt(0) - aCharCode);
    });

// numbers
    var n = 9;
    do {
        keymap[n.toString()] = n + 48;
    } while (--n);
}());

MWF.xApplication.MinderEditor.Key = function(){

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
    this.hash = hash;
    this.is = is;


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
                    hashCode |= MWF.xApplication.MinderEditor.KeyMap[name];
            }
        });
        return hashCode;
    }
};

MWF.xApplication.MinderEditor.Format = function(template, args) {
    if (typeof(args) != 'object') {
        args = [].slice.call(arguments, 1);
    }
    return String(template).replace(/\{(\w+)\}/ig, function(match, $key) {
        return args[$key] || $key;
    });
};

MWF.xApplication.MinderEditor.Debug = function(flag) {

    function noop() {}

    function stringHash(str) {
        var hash = 0;
        for (var i = 0; i < str.length; i++) {
            hash += str.charCodeAt(i);
        }
        return hash;
    }

    var debugMode = this.flaged = window.location.search.indexOf(flag) != -1;

    if (debugMode) {
        var h = stringHash(flag) % 360;

        var flagStyle = MWF.xApplication.MinderEditor.Format(
            'background: hsl({0}, 50%, 80%); ' +
            'color: hsl({0}, 100%, 30%); ' +
            'padding: 2px 3px; ' +
            'margin: 1px 3px 0 0;' +
            'border-radius: 2px;', h);

        var textStyle = 'background: none; color: black;';
        this.log = function() {
            var output = MWF.xApplication.MinderEditor.Format.apply(null, arguments);
            console.log(MWF.xApplication.MinderEditor.Format('%c{0}%c{1}', flag, output), flagStyle, textStyle);
        };
    } else {
        this.log = noop;
    }
};

MWF.xApplication.MinderEditor.JsonDiff = function( tree1, tree2 ){
    /*!
     * https://github.com/Starcounter-Jack/Fast-JSON-Patch
     * json-patch-duplex.js 0.5.0
     * (c) 2013 Joachim Wester
     * MIT license
     */

    var _objectKeys = (function () {
        if (Object.keys)
            return Object.keys;

        return function (o) {
            var keys = [];
            for (var i in o) {
                if (o.hasOwnProperty(i)) {
                    keys.push(i);
                }
            }
            return keys;
        };
    })();
    function escapePathComponent(str) {
        if (str.indexOf('/') === -1 && str.indexOf('~') === -1)
            return str;
        return str.replace(/~/g, '~0').replace(/\//g, '~1');
    }
    function deepClone(obj) {
        if (typeof obj === "object") {
            return JSON.parse(JSON.stringify(obj));
        } else {
            return obj;
        }
    }

    // Dirty check if obj is different from mirror, generate patches and update mirror
    function _generate(mirror, obj, patches, path) {
        var newKeys = _objectKeys(obj);
        var oldKeys = _objectKeys(mirror);
        var changed = false;
        var deleted = false;

        for (var t = oldKeys.length - 1; t >= 0; t--) {
            var key = oldKeys[t];
            var oldVal = mirror[key];
            if (obj.hasOwnProperty(key)) {
                var newVal = obj[key];
                if (typeof oldVal == "object" && oldVal != null && typeof newVal == "object" && newVal != null) {
                    _generate(oldVal, newVal, patches, path + "/" + escapePathComponent(key));
                } else {
                    if (oldVal != newVal) {
                        changed = true;
                        patches.push({ op: "replace", path: path + "/" + escapePathComponent(key), value: deepClone(newVal) });
                    }
                }
            } else {
                patches.push({ op: "remove", path: path + "/" + escapePathComponent(key) });
                deleted = true; // property has been deleted
            }
        }

        if (!deleted && newKeys.length == oldKeys.length) {
            return;
        }

        for (var t = 0; t < newKeys.length; t++) {
            var key = newKeys[t];
            if (!mirror.hasOwnProperty(key)) {
                patches.push({ op: "add", path: path + "/" + escapePathComponent(key), value: deepClone(obj[key]) });
            }
        }
    }

    //function compare(tree1, tree2) {
    //	var patches = [];
    //	_generate(tree1, tree2, patches, '');
    //	return patches;
    //}

    var patches = [];
    _generate(tree1, tree2, patches, '');
    return patches;
};
