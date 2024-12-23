o2.widget = o2.widget || {};
o2.widget.monaco = {
    "callbackList": [],
    "load": function(callback){
        if (!window.monaco){
            this.callbackList.push(callback);
            if (!this.isLoadding){
                this.isLoadding = true;
                o2.load("monaco", {"sequence": true}, function(){
                    var currentScriptPath = window.location.href;
                    var baseUrl = currentScriptPath.substring(0, currentScriptPath.lastIndexOf('/') + 1) + '../o2_lib/'; // 计算 baseUrl
                    require.config({
                        baseUrl: baseUrl,
                        paths: { "vs": "vs/min/vs" }
                    });
                    require(["vs/editor/editor.main"], function() {
                        this.isLoadding = false;
                        while (this.callbackList.length){
                            this.callbackList.shift()();
                        }
                    }.bind(this));

                }.bind(this));
            }
        }else{
            if (callback) callback();
        }
    }
};
