o2.widget = o2.widget || {};
o2.widget.ace = {
    //"ace": COMMON.contentPath+"/res/framework/ace/src-min/ace.js",
    //"tools": COMMON.contentPath+"/res/framework/ace/src-min/ext-language_tools.js",
    "load": function(callback){
        if (!window.ace){
            var jsLoaded = false;
            var cssLoaded = false;
            o2.load("ace", {"sequence": true}, function(){
                //COMMON.AjaxModule.loadDom("ace-tools", function(){
                    if (callback) callback();
                //}.bind(this))
            }.bind(this));
        }else{
            if (callback) callback();
        }
    }
};