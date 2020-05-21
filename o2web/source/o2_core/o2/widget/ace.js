o2.widget = o2.widget || {};
o2.widget.ace = {
    //"ace": COMMON.contentPath+"/res/framework/ace/src-min/ace.js",
    //"tools": COMMON.contentPath+"/res/framework/ace/src-min/ext-language_tools.js",
    "callbackList": [],
    "load": function(callback){
        if (!window.ace){
            this.callbackList.push(callback);
            if (!this.isLoadding) {
                this.isLoadding = true;
                o2.load("ace", {"sequence": true}, function(){
                    this.isLoadding = false;
                    while (this.callbackList.length){
                        this.callbackList.shift()();
                    }
                }.bind(this));
            }

        }else{
            if (callback) callback();
        }
    }
};