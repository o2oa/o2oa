define(function(require, exports, module) {
    var kity = require('./kity');
    var utils = require('./utils');
    var Minder = require('./minder');

    function listen(element, type, handler) {
        type.split(' ').forEach(function(name) {
            element.addEventListener(name, handler, false);
        });
    }

    Minder.registerInitHook(function(option) {
        this.setDefaultOptions({
            enableKeyReceiver: true
        });
        if (this.getOption('enableKeyReceiver')) {
            this.on('paperrender', function() {
                this._initKeyReceiver();
            });
        }
    });

    kity.extendClass(Minder, {
        _initKeyReceiver: function() {

            if (this._keyReceiver) return;

            var receiver = this._keyReceiver = document.createElement('input');
            receiver.classList.add('km-receiver');

            var renderTarget = this._renderTarget;
            renderTarget.appendChild(receiver);

            var minder = this;

            listen(receiver, 'keydown keyup keypress copy paste blur focus input', function(e) {
                switch (e.type) {
                    case 'blur':
                        minder.blur();
                        break;
                    case 'focus':
                        minder.focus();
                        break;
                    case 'input':
                        receiver.value = null;
                        break;
                }
                minder._firePharse(e);
                e.preventDefault();
            });

            this.on('focus', function() {
                receiver.select();
                receiver.focus();
            });
            this.on('blur', function() {
                receiver.blur();
            });

            if (this.isFocused()) {
                receiver.select();
                receiver.focus();
            }
        }
    });
});