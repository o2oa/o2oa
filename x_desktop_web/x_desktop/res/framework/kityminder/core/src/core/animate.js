/**
 * @fileOverview
 *
 * 动画控制
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define(function(require, exports, module) {
    var Minder = require('./minder');

    var animateDefaultOptions = {
        enableAnimation: true,
        layoutAnimationDuration: 300,
        viewAnimationDuration: 100,
        zoomAnimationDuration: 300
    };
    var resoredAnimationOptions = {};

    Minder.registerInitHook(function() {
        this.setDefaultOptions(animateDefaultOptions);
        if (!this.getOption('enableAnimation')) {
            this.disableAnimation();
        }
    });

    Minder.prototype.enableAnimation = function() {
        for (var name in animateDefaultOptions) {
            if (animateDefaultOptions.hasOwnProperty(name)) {
                this.setOption(resoredAnimationOptions[name]);
            }
        }
    };

    Minder.prototype.disableAnimation = function() {
        for (var name in animateDefaultOptions) {
            if (animateDefaultOptions.hasOwnProperty(name)) {
                resoredAnimationOptions[name] = this.getOption(name);
                this.setOption(name, 0);
            }
        }
    };
});