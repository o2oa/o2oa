MWF.xDesktop.requireApp("process.ProcessManager", "Explorer", null, false);
MWF.xApplication.query.QueryManager.StatExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer,
	Implements: [Options, Events],
    options: {
        "style": "default",
        "tooltip": {
            "create": MWF.xApplication.query.QueryManager.LP.stat.create,
            "search": MWF.xApplication.query.QueryManager.LP.stat.search,
            "searchText": MWF.xApplication.query.QueryManager.LP.stat.searchText,
            "noElement": MWF.xApplication.query.QueryManager.LP.stat.noStatNoticeText
        }
    },

    _createElement: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.app.restActions;
                this.application = _self.app.options.application;
                this.explorer = _self;
            }
        };
        this.app.desktop.openApplication(e, "query.StatDesigner", options);
    },

    _loadItemDataList: function(callback){
        this.app.restActions.listStat(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.query.QueryManager.StatExplorer.Stat(this, item);
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteStat();
            }else{
                item.deleteStat(function(){
                    //this.reloadItems();
                    //this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.query.QueryManager.StatExplorer.Stat= new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer.Item,
	
	_open: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
            }
        };
        this.explorer.app.desktop.openApplication(e, "query.StatDesigner", options);
	},
	_getIcon: function(){
		var x = (Math.random()*49).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/processIcon/lnk.png",
			"title": this.data.name,
			"par": "query.StatDesigner#{\"id\": \""+this.data.id+"\"}"
		};
	},
    deleteStat: function(callback){
		this.explorer.actions.deleteStat(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});
