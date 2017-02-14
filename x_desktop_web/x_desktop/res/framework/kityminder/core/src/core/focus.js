define(function(require, exports, module) {
    var kity = require('./kity');
    var Minder = require('./minder');

    Minder.registerInitHook(function() {
        this.on('beforemousedown', function(e) {
            this.focus();
            e.preventDefault();
        });
        this.on('paperrender', function() {
            this.focus();
        });
    });

    kity.extendClass(Minder, {
        focus: function() {
            if (!this.isFocused()) {
                var renderTarget = this._renderTarget;
                renderTarget.classList.add('focus');
                this.renderNodeBatch(this.getSelectedNodes());
            }
            this.fire('focus');
            return this;
        },

        blur: function() {
            if (this.isFocused()) {
                var renderTarget = this._renderTarget;
                renderTarget.classList.remove('focus');
                this.renderNodeBatch(this.getSelectedNodes());
            }
            this.fire('blur');
            return this;
        },

        isFocused: function() {
            var renderTarget = this._renderTarget;
            return renderTarget && renderTarget.classList.contains('focus');
        }
    });
});