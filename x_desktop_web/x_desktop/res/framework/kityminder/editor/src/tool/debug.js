/**
 * @fileOverview
 *
 * 支持各种调试后门
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define(function(require, exports, module) {
    var format = require('./format');

    function noop() {}

    function stringHash(str) {
        var hash = 0;
        for (var i = 0; i < str.length; i++) {
            hash += str.charCodeAt(i);
        }
        return hash;
    }

    /* global console */
    function Debug(flag) {
        var debugMode = this.flaged = window.location.search.indexOf(flag) != -1;

        if (debugMode) {
            var h = stringHash(flag) % 360;

            var flagStyle = format(
                'background: hsl({0}, 50%, 80%); ' +
                'color: hsl({0}, 100%, 30%); ' +
                'padding: 2px 3px; ' +
                'margin: 1px 3px 0 0;' +
                'border-radius: 2px;', h);

            var textStyle = 'background: none; color: black;';
            this.log = function() {
                var output = format.apply(null, arguments);
                console.log(format('%c{0}%c{1}', flag, output), flagStyle, textStyle);
            };
        } else {
            this.log = noop;
        }
    }

    return module.exports = Debug;
});