/**
 * @fileOverview
 *
 * 提供脑图选项支持
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define(function(require, exports, module) {
    var kity = require('./kity');
    var utils = require('./utils');
    var Minder = require('./minder');

    Minder.registerInitHook(function(options) {
        this._defaultOptions = {};
    });

    kity.extendClass(Minder, {
        setDefaultOptions: function(options) {
            utils.extend(this._defaultOptions, options);
            return this;
        },
        getOption: function(key) {
            if (key) {
                return key in this._options ? this._options[key] : this._defaultOptions[key];
            } else {
                return utils.extend({}, this._defaultOptions, this._options);
            }
        },
        setOption: function(key, value) {
            this._options[key] = value;
        }
    });
});