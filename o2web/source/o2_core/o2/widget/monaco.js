o2.widget = o2.widget || {};
o2.widget.monaco = {
    "callbackList": [],
    "load": function(callback){
        if (!window.monaco){
            this.callbackList.push(callback);
            if (!this.isLoadding){
                this.isLoadding = true;
                o2.load("monaco", {"sequence": true}, function(){
                    require.config({ paths: { "vs": "../o2_lib/vs" }});
                    require(["vs/editor/editor.main"], function() {
                        this.isLoadding = false;
                        while (this.callbackList.length){
                            this.callbackList.shift()();
                        }
                        //define.amd = false;
                        //if (callback) callback();
                    }.bind(this));
                }.bind(this));
            }
        }else{
            if (callback) callback();
        }
    }
};