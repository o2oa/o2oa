

/**
 * 开发版本的文件导入
 */
(function() {
    /* 可能的文件路径，已按照依赖关系排序 */
    var pathInfo = [
        /* 核心代码 */
        'src/core/kityminder.js',
        'src/core/utils.js',

        'src/core/command.js',
        'src/core/node.js',

        'src/core/options.js',
        'src/core/event.js',
        'src/core/status.js',
        'src/core/paper.js',
        'src/core/select.js',
        'src/core/module.js',
        'src/core/data.js',
        'src/core/readonly.js',
        'src/core/layout.js',
        'src/core/theme.js',
        
        'src/core/compatibility.js',
        'src/core/render.js',
        'src/core/connect.js',
        'src/core/template.js',
        'src/core/keymap.js',

        /* 布局 */
        'src/layout/mind.js',
        'src/layout/filetree.js',
        'src/layout/btree.js',
        'src/layout/fish-bone-master.js',
        'src/layout/fish-bone-slave.js',

        /* 连线 */
        'src/connect/bezier.js',
        'src/connect/poly.js',
        'src/connect/arc.js',
        'src/connect/under.js',
        'src/connect/l.js',
        'src/connect/fish-bone-master.js',

        /* 皮肤 */
        'src/theme/default.js',
        'src/theme/snow.js',
        'src/theme/fresh.js',
        'src/theme/fish.js',
        'src/theme/wire.js',

        /* 模板 */
        'src/template/default.js',
        'src/template/structure.js',
        'src/template/filetree.js',
        'src/template/right.js',
        'src/template/fish-bone.js',

        /* 模块 */
        'src/module/node.js',
        'src/module/text.js',
        'src/module/expand.js',
        'src/module/outline.js',
        'src/module/history.js',
        'src/module/progress.js',
        'src/module/priority.js',
        'src/module/image.js',
        'src/module/resource.js',
        'src/module/view.js',
        'src/module/dragtree.js',
        'src/module/keynav.js',
        'src/module/select.js',
        'src/module/history.js',
        'src/module/editor.js',
        'src/module/editor.keyboard.js',
        'src/module/editor.range.js',
        'src/module/editor.receiver.js',
        'src/module/editor.selection.js',
        'src/module/basestyle.js',
        'src/module/font.js',
        'src/module/zoom.js',
        'src/module/hyperlink.js',
        'src/module/arrange.js',
        'src/module/clipboard.js',
        'src/module/style.js'
    ];

    if (typeof(module) === 'object' && module.exports) {
        module.exports = pathInfo;
    } 

    else if (document) {
        while (pathInfo.length) {
            var path = pathInfo.shift();
            window.document.write('<script type="text/javascript" src="' + path + '"></script>');
        }
    }
})();