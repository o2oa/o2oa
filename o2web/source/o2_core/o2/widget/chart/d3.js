o2.widget = o2.widget || {};
o2.widget.chart = o2.widget.chart || {};
o2.widget.chart.d3 = {
    "d3": COMMON.contentPath+"/res/framework/d3/d3.js",
    "load": function(callback){
        if (!window.d3){
            var jsLoaded = false;
            var cssLoaded = false;
            COMMON.AjaxModule.loadDom("d3", function(){
                //COMMON.AjaxModule.loadDom(this.tools, function(){
                if (callback) callback();
                //}.bind(this))
            }.bind(this));
        }else{
            if (callback) callback();
        }
    }
};