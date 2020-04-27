o2.widget = o2.widget || {};
o2.widget.monaco = {
    //"ace": COMMON.contentPath+"/res/framework/ace/src-min/ace.js",
    //"tools": COMMON.contentPath+"/res/framework/ace/src-min/ext-language_tools.js",
    "load": function(callback){
        if (!window.monaco){
            o2.load("monaco", {"sequence": true}, function(){
                require.config({ paths: { "vs": "/o2_lib/vs" }});
                require(["vs/editor/editor.main"], function() {
                    if (callback) callback();
                    // var editor = monaco.editor.create(document.getElementById('container'), {
                    //     value: [
                    //         'function x() {',
                    //         '\tconsole.log("Hello world!");',
                    //         '}'
                    //     ].join('\n'),
                    //     language: 'javascript'
                    // });
                });
            }.bind(this));
        }else{
            if (callback) callback();
        }
    }
};