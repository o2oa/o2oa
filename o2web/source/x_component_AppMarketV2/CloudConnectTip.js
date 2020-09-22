MWF.xApplication.AppMarketV2.CloudConnectTip = new Class({
    Implements: [Options, Events],
    options: {
        "view": "cloudConnectTip.html"
    },
    initialize: function(app, container, options){
        this.setOptions(options);
        this.app = app;
        this.container = container;
        this.viewPath = this.app.path+this.app.options.style+"/"+this.options.view;
        debugger;
        this.load();
    },
    load: function(){
        debugger;
        this.container.loadHtml(this.viewPath, {"bind": {"lp": this.app.lp}, "module": this}, function(){
        }.bind(this));
    }
});