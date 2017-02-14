/**
 * @fileOverview
 *
 * 脑图示例运行时
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define(function(require, exports, module) {
    var Minder = require('../minder');

    function MinderRuntime() {

        // 不使用 kityminder 的按键处理，由 ReceiverRuntime 统一处理
        var minder = new Minder({
            enableKeyReceiver: false,
            enableAnimation: true
        });

        // 渲染，初始化
        minder.renderTo(this.selector);
        minder.setTheme(null);
        minder.select(minder.getRoot(), true);
        minder.execCommand('text', '中心主题');

        // 导出给其它 Runtime 使用
        this.minder = minder;
    }

    return module.exports = MinderRuntime;
});