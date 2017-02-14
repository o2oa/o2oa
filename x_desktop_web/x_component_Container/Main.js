MWF.xApplication.Container.options = {
    "multitask": true,
    "executable": false
};
MWF.xApplication.Container.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Container",
		"icon": "icon.png",
		"width": "1000",
		"height": "800",
		"isResize": true,
		"isMax": true,
		"title": ""
	},
	onQueryLoad: function(){
        if (this.status){
            this.clazzModule = this.status.clazzModule;
            this.clazzPath = this.status.clazzPath || "Main";
            this.clazz = this.status.clazz;
            this.options = this.status.options;
            this.parameter = this.status.parameter;
        }
	},
	loadApplication: function(callback){
        if (this.clazz && this.clazzModule && this.clazzPath){
            MWF.xDesktop.requireApp(this.clazzModule, this.clazzPath, function(){
                var obj = MWF;
                var paths = this.clazz.split(".");
                paths.each(function(p, i){
                    if (i>0) obj = obj[p];
                }.bind(this));

                if (obj){
                    this.app = new obj(this.content, null, this.options);
                    this.app.load();
                }
            }.bind(this));
        }
	}
});
