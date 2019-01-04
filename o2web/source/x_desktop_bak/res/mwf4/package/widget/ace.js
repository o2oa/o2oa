MWF.widget = MWF.widget || {};
MWF.widget.ace = {
    //"ace": COMMON.contentPath+"/res/framework/ace/src-min/ace.js",
    //"tools": COMMON.contentPath+"/res/framework/ace/src-min/ext-language_tools.js",
    "load": function(callback){
        if (!window.ace){
            var jsLoaded = false;
            var cssLoaded = false;
            COMMON.AjaxModule.loadDom("ace", function(){
                COMMON.AjaxModule.loadDom("ace-tools", function(){
                    if (callback) callback();
                }.bind(this))
            }.bind(this));
        }else{
            if (callback) callback();
        }
    }
};