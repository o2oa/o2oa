tinymce.PluginManager.add('o2wordlimit', function(editor) {
    var pluginName='字数限制';
    var global$1 = tinymce.util.Tools.resolve('tinymce.util.Tools');
    var global$2 = tinymce.util.Tools.resolve('tinymce.util.Delay');
    var ax_wordlimit_type = editor.getParam('ax_wordlimit_type', 'letter' );
    var ax_wordlimit_num = editor.getParam('ax_wordlimit_num', false );
    var ax_wordlimit_delay = editor.getParam('ax_wordlimit_delay', 500 );
    var ax_wordlimit_callback = editor.getParam('ax_wordlimit_callback', function(){} );
    var ax_wordlimit_event = editor.getParam('ax_wordlimit_event', 'SetContent Undo Redo Keyup' );
    var onsign=1;
    //统计方法1：计算总字符数
    var sumLetter = function(){
        var html = editor.getContent();
        var re1 = new RegExp("<.+?>","g");
        var txt = html.replace(re1,'');
        txt = txt.replace(/\n/g,'');
        txt = txt.replace(/&nbsp;/g,' ');
        var num=txt.length;
        return {txt:txt,num:num}
    }
    var onAct = function(){
        if(onsign){
            onsign=0;
            //此处预留更多统计方法
            switch(ax_wordlimit_type){
                case 'letter':
                default:
                    var res = sumLetter();
            }
            if( res.num > ax_wordlimit_num ){
                ax_wordlimit_callback(editor, res.txt, ax_wordlimit_num);
            }
            setTimeout(function(){onsign=1}, ax_wordlimit_delay);
        }
        
    }
    var setup = function(){
        if( ax_wordlimit_num>0 ){
            global$2.setEditorTimeout(editor, function(){
                var doth = editor.on(ax_wordlimit_event, onAct);
            }, 300);
        }
    };

    setup();

    return {
        getMetadata: function () {
            return  {
                name: pluginName
                // url: "http://tinymce.ax-z.cn/more-plugins/ax_wordlimit.php",
            };
        }
    };
});
