MWF.xDesktop.requireApp("process.ProcessManager", "ViewExplorer", null, false);
MWF.xApplication.process.ProcessManager.StatExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.ViewExplorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.APPPM.LP.stat.create,
        "search": MWF.APPPM.LP.stat.search,
        "searchText": MWF.APPPM.LP.stat.searchText,
        "noElement": MWF.APPPM.LP.stat.noNoticeText
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
        this.app.desktop.openApplication(e, "process.StatDesigner", options);
    },


    _loadItemDataList: function(callback){
        this.app.restActions.listStat(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.process.ProcessManager.StatExplorer.Stat(this, item)
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.APPPM.LP.stat.create,
            "search": MWF.APPPM.LP.stat.search,
            "searchText": MWF.APPPM.LP.stat.searchText,
            "noElement": MWF.APPPM.LP.stat.noNoticeText
        };
    },
    deleteItems: function(){
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteStat();
            }else{
                item.deleteStat(function(){
                //    this.reloadItems();
                    this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.process.ProcessManager.StatExplorer.Stat = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer.Item,
	_open: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
                this.explorer = _self.explorer;
            }
        };
        this.explorer.app.desktop.openApplication(e, "process.StatDesigner", options);
	},
	_getIcon: function(){
        return "stat.png"
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/statIcon/lnk.png",
			"title": this.data.name,
            "par": "process.StatDesigner#{\"appId\": \"query.StatDesigner"+this.data.id+"\", \"id\": \""+this.data.id+"\", \"applicationId\": \""+this.explorer.app.options.application.id+"\"}"
		};
	},
//	deleteItem: function(e){
//		var _self = this;
//		this.explorer.app.confirm("info", e, this.explorer.app.lp.form.deleteFormTitle, this.explorer.app.lp.form.deleteForm, 320, 110, function(){
//			_self.deleteForm();
//			this.close();
//		},function(){
//			this.close();
//		});
//	},
    deleteStat: function(callback){
		this.explorer.app.restActions.deleteStat(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});
