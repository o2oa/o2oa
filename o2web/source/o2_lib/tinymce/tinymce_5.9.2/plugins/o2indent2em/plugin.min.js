tinymce.PluginManager.add('o2indent2em', function(editor, url) {
    var pluginName='首行缩进';
    var global$1 = tinymce.util.Tools.resolve('tinymce.util.Tools');
    var indent2em_val = editor.getParam('indent2em_val', '2em');
    var doAct = function () {
        var dom = editor.dom;
        var blocks = editor.selection.getSelectedBlocks();
        var act = '';
        global$1.each(blocks, function (block) {
            if(act==''){
                act = dom.getStyle(block,'text-indent')==indent2em_val ? 'remove' : 'add';
            }
            if( act=='add' ){
                dom.setStyle(block, 'text-indent', indent2em_val);
            }else{
                var style=dom.getAttrib(block,'style');
                var reg = new RegExp('text-indent:[\\s]*' + indent2em_val + ';', 'ig');
                style = style.replace(reg, '');
                dom.setAttrib(block,'style',style);
            }

        });
    };

    editor.ui.registry.getAll().icons.indent2em || editor.ui.registry.addIcon('indent2em','<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="24" height="24"><path d="M170.666667 563.2v-102.4H887.466667v102.4zM170.666667 836.266667v-102.4H887.466667v102.4zM512 290.133333v-102.4H887.466667v102.4zM238.933333 341.333333V136.533333l204.8 102.4z" fill="#2c2c2c" p-id="5210"></path></svg>');

    var stateSelectorAdapter = function (editor, selector) {
      return function (buttonApi) {
        return editor.selection.selectorChangedWithUnbind(selector.join(','), buttonApi.setActive).unbind;
      };
    };
    
    editor.ui.registry.addToggleButton('o2indent2em', {
        icon: 'indent2em',
        tooltip: pluginName,
        onAction: function () {
            doAct();
        },
        onSetup: stateSelectorAdapter(editor, [
          '*[style*="text-indent"]',
          '*[data-mce-style*="text-indent"]',
        ])
    });

    editor.ui.registry.addMenuItem('o2indent2em', {
        text: pluginName,
        onAction: function() {
            doAct();
        }
    });

    editor.addCommand('indent2em', doAct  );

    return {
        getMetadata: function () {
            return  {
                name: pluginName,
                url: "http://tinymce.ax-z.cn/more-plugins/indent2em.php",
            };
        }
    };
});
