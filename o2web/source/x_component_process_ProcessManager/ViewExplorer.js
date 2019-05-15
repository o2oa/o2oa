MWF.xDesktop.requireApp("process.ProcessManager", "Explorer", null, false);
MWF.xApplication.process.ProcessManager.ViewExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.APPPM.LP.view.create,
        "search": MWF.APPPM.LP.view.search,
        "searchText": MWF.APPPM.LP.view.searchText,
        "noElement": MWF.APPPM.LP.view.noNoticeText
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
        this.app.desktop.openApplication(e, "process.ViewDesigner", options);
    },


    _loadItemDataList: function(callback){
        this.app.restActions.listView(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.process.ProcessManager.ViewExplorer.View(this, item)
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.APPPM.LP.view.create,
            "search": MWF.APPPM.LP.view.search,
            "searchText": MWF.APPPM.LP.view.searchText,
            "noElement": MWF.APPPM.LP.view.noNoticeText
        };
    },
    deleteItems: function(){
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteView();
            }else{
                item.deleteView(function(){
                //    this.reloadItems();
                    this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.process.ProcessManager.ViewExplorer.View = new Class({
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
        this.explorer.app.desktop.openApplication(e, "process.ViewDesigner", options);


        //layout.desktop.getFormDesignerStyle(function(){
        //    var _self = this;
        //    var options = {
        //        "style": layout.desktop.formDesignerStyle,
        //        "onQueryLoad": function(){
        //            this.actions = _self.explorer.actions;
        //            this.category = _self;
        //            this.options.id = _self.data.id;
        //            this.application = _self.explorer.app.options.application;
        //        }
        //    };
        //    this.explorer.app.desktop.openApplication(e, "process.FormDesigner", options);
        //}.bind(this));
	},
	_getIcon: function(){
		//var x = (Math.random()*33).toInt();
		//return "process_icon_"+x+".png";
        return "view.png"
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/viewIcon/lnk.png",
			"title": this.data.name,
            "par": "process.ViewDesigner#{\"appId\": \"query.ViewDesigner"+this.data.id+"\", \"id\": \""+this.data.id+"\", \"applicationId\": \""+this.explorer.app.options.application.id+"\"}"
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
    deleteView: function(callback){
		this.explorer.app.restActions.deleteView(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});
