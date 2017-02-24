MWF.xDesktop.requireApp("cms.ColumnManager", "Explorer", null, false);
MWF.xApplication.cms.ColumnManager.QueryViewExplorer = new Class({
	Extends: MWF.xApplication.cms.ColumnManager.Explorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.CMSCM.LP.queryView.create,
        "search": MWF.CMSCM.LP.queryView.search,
        "searchText": MWF.CMSCM.LP.queryView.searchText,
        "noElement": MWF.CMSCM.LP.queryView.noViewNoticeText
    },

    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.CMSCM.LP.queryView.create,
            "search": MWF.CMSCM.LP.queryView.search,
            "searchText": MWF.CMSCM.LP.queryView.searchText,
            "noElement": MWF.CMSCM.LP.queryView.noViewNoticeText
        };
    },
    _createElement: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.app.restActions;
                this.application = _self.app.options.application;
                this.explorer = _self;
            },
            "onPostSave" : function(){
                _self.reload();
            }
        };
        this.app.desktop.openApplication(e, "cms.QueryViewDesigner", options);
    },


    _loadItemDataList: function(callback){
        this.app.restActions.listQueryView(this.app.options.application.id,callback);
    },
    _getItemObject: function(item, index){
        return new MWF.xApplication.cms.ColumnManager.QueryViewExplorer.View(this, item, {index : index})
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
                    this.reload();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.cms.ColumnManager.QueryViewExplorer.View = new Class({
	Extends: MWF.xApplication.cms.ColumnManager.Explorer.Item,
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
        this.explorer.app.desktop.openApplication(e, "cms.QueryViewDesigner", options);


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
		var x = (Math.random()*33).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/queryViewIcon/lnk.png",
			"title": this.data.name,
            "par": "cms.QueryViewDesigner#{\"id\": \""+this.data.id+"\", \"application\": "+JSON.stringify( this.explorer.app.options.application )+"}"
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
		this.explorer.app.restActions.deleteQueryView(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});
