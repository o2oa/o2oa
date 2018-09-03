MWF.xDesktop.requireApp("process.ProcessManager", "DictionaryExplorer", null, false);
MWF.xApplication.portal.PortalManager.ScriptExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer,
	Implements: [Options, Events],
    options: {
        "style": "default",
        "tooltip": {
            "create": MWF.xApplication.portal.PortalManager.LP.script.create,
            "search": MWF.xApplication.portal.PortalManager.LP.script.search,
            "searchText": MWF.xApplication.portal.PortalManager.LP.script.searchText,
            "noElement": MWF.xApplication.portal.PortalManager.LP.script.noProcessNoticeText
        }
    },

    _createElement: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.app.restActions;
                this.application = _self.app.options.application || _self.app.application;
                this.explorer = _self;
            }
        };
        this.app.desktop.openApplication(e, "portal.ScriptDesigner", options);
    },
    _loadItemDataList: function(callback){
        var id = "";
        if (this.app.application) id = this.app.application.id;
        if (this.app.options.application) id = this.app.options.application.id;
        this.actions.listScript(id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.portal.PortalManager.ScriptExplorer.Script(this, item)
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.APPPM.LP.script.create,
            "search": MWF.APPPM.LP.script.search,
            "searchText": MWF.APPPM.LP.script.searchText,
            "noElement": MWF.APPPM.LP.script.noScriptNoticeText
        };
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteScript();
            }else{
                item.deleteScript(function(){
                //    this.reloadItems();
                //    this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.portal.PortalManager.ScriptExplorer.Script = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer.Dictionary,

    _customNodes: function(){
        if (!this.data.validated){
            new Element("div", {"styles": this.explorer.css.itemErrorNode}).inject(this.node);
            this.node.setStyle("background-color", "#f9e8e8");
        }
    },
	_open: function(e){
		var _self = this;
		var options = {
			"onQueryLoad": function(){
				this.actions = _self.explorer.actions;
				this.category = _self;
				this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
                this.explorer = _self.explorer
			}
		};
		this.explorer.app.desktop.openApplication(e, "portal.ScriptDesigner", options);
	},
    _getIcon: function(){
        //var x = (Math.random()*33).toInt();
        //return "process_icon_"+x+".png";
        return "script.png";
    },
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/scriptIcon/lnk.png",
			"title": this.data.name,
			"par": "portal.ScriptDesigner#{\"id\": \""+this.data.id+"\", \"applicationId\": \""+this.explorer.app.options.application.id+"\"}"
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
    deleteScript: function(callback){
		this.explorer.app.restActions.deleteScript(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});