/**
 * @fileOverview
 *
 * KityMinder 类，暴露在 window 上的唯一变量
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define(function(require, exports, module) {
    var kity = require('./kity');
    var utils = require('./utils');

    var _initHooks = [];

    var Minder = kity.createClass('Minder', {
        constructor: function(options) {
            this._options = utils.extend({}, options);

            var initHooks = _initHooks.slice();

            var initHook;
            while (initHooks.length) {
                initHook = initHooks.shift();
                if (typeof(initHook) == 'function') {
                    initHook.call(this, this._options);
                }
            }

            this.fire('finishInitHook');
        }
    });

    Minder.version = '1.4.33';

    Minder.registerInitHook = function(hook) {
        _initHooks.push(hook);
    };

    module.exports = Minder;
});
