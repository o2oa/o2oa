/**
 * @fileOverview
 *
 * 默认导出（全部模块）
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */


define(function(require, exports, module) {
    var kityminder = {
        version: require('./core/minder').version
    };

    // 核心导出，大写的部分导出类，小写的部分简单 require 一下
    // 这里顺序是有讲究的，调整前先弄清楚依赖关系。
    require('./core/utils');
    kityminder.Minder = require('./core/minder');
    kityminder.Command = require('./core/command');
    kityminder.Node = require('./core/node');
    require('./core/option');
    require('./core/animate');
    kityminder.Event = require('./core/event');
    kityminder.data = require('./core/data');
    require('./core/compatibility');
    kityminder.KeyMap = require('./core/keymap');
    require('./core/shortcut');
    require('./core/status');
    require('./core/paper');
    require('./core/select');
    require('./core/focus');
    require('./core/keyreceiver');
    kityminder.Module = require('./core/module');
    require('./core/readonly');
    kityminder.Render = require('./core/render');
    kityminder.Connect = require('./core/connect');
    kityminder.Layout = require('./core/layout');
    kityminder.Theme = require('./core/theme');
    kityminder.Template = require('./core/template');
    kityminder.Promise = require('./core/promise');
    require('./core/_boxv');
    require('./core/patch');

    // 模块依赖
    require('./module/arrange');
    require('./module/basestyle');
    require('./module/clipboard');
    require('./module/dragtree');
    require('./module/expand');
    require('./module/font');
    require('./module/hyperlink');
    require('./module/image');
    require('./module/keynav');
    require('./module/layout');
    require('./module/node');
    require('./module/note');
    require('./module/outline');
    require('./module/priority');
    require('./module/progress');
    require('./module/resource');
    require('./module/select');
    require('./module/style');
    require('./module/text');
    require('./module/view');
    require('./module/zoom');

    require('./protocol/json');
    require('./protocol/text');
    require('./protocol/markdown');
    require('./protocol/svg');
    require('./protocol/png');

    require('./layout/mind');
    require('./layout/btree');
    require('./layout/filetree');
    require('./layout/fish-bone-master');
    require('./layout/fish-bone-slave');
    require('./layout/tianpan');

    require('./theme/default');
    require('./theme/snow');
    require('./theme/fresh');
    require('./theme/fish');
    require('./theme/snow');
    require('./theme/wire');
    require('./theme/tianpan');

    require('./connect/arc');
    require('./connect/arc_tp');
    require('./connect/bezier');
    require('./connect/fish-bone-master');
    require('./connect/l');
    require('./connect/poly');
    require('./connect/under');

    require('./template/default');
    require('./template/structure');
    require('./template/filetree');
    require('./template/right');
    require('./template/fish-bone');
    require('./template/tianpan');

    module.exports = kityminder;
});