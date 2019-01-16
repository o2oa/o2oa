MWF.xDesktop.requireApp("process.ProcessManager", "Explorer", null, false);
MWF.xApplication.portal.PortalManager.WidgetExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer,
	Implements: [Options, Events],

    options: {
        "style": "default",
        "tooltip": {
            "create": MWF.xApplication.portal.PortalManager.LP.widget.create,
            "search": MWF.xApplication.portal.PortalManager.LP.widget.search,
            "searchText": MWF.xApplication.portal.PortalManager.LP.widget.searchText,
            "noElement": MWF.xApplication.portal.PortalManager.LP.widget.noNoticeText
        }
    },

    _createElement: function(e){
        var _self = this;
        var options = {
            "style": layout.desktop.pageDesignerStyle || "default",
            //"templateId": "page.json",
            "onQueryLoad": function(){
                this.actions = _self.app.restActions;
                this.application = _self.app.options.application;
            }
        };
        this.app.desktop.openApplication(e, "portal.WidgetDesigner", options);

    },
    _loadItemDataList: function(callback){
        this.app.restActions.listWidget(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.portal.PortalManager.WidgetExplorer.Widget(this, item)
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteWidget();
            }else{
                item.deleteWidget(function(){
                    //    this.reloadItems();
                    //this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.portal.PortalManager.WidgetExplorer.Widget= new Class({
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
        this.explorer.app.desktop.openApplication(e, "portal.WidgetDesigner", options);
	},
	_getIcon: function(){
		var x = (Math.random()*49).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/processIcon/lnk.png",
			"title": this.data.name,
			"par": "portal.WidgetDesigner#{\"id\": \""+this.data.id+"\"}"
		};
	},
//	deleteItem: function(e){
//		var _self = this;
//		this.explorer.app.confirm("info", e, this.explorer.app.lp.process.deleteProcessTitle, this.explorer.app.lp.process.deleteProcess, 320, 110, function(){
//			_self.deleteProcess();
//			this.close();
//		},function(){
//			this.close();
//		});
//	},
    deleteWidget: function(callback){
		this.explorer.actions.deleteWidget(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});
