MWF.xDesktop.requireApp("cms.ColumnManager", "DictionaryExplorer", null, false);
MWF.xApplication.cms.ColumnManager.ScriptExplorer = new Class({
	Extends: MWF.xApplication.cms.ColumnManager.DictionaryExplorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.CMSCM.LP.dictionary.create,
        "search": MWF.CMSCM.LP.dictionary.search,
        "searchText": MWF.CMSCM.LP.dictionary.searchText,
        "noElement": MWF.CMSCM.LP.dictionary.noDictionaryNoticeText
    },

    _createElement: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.app.restActions;
                //_self.app.options.application = _self.app.options.application || _self.app.options.column;
                this.application = _self.app.options.application || _self.app.application || _self.app.options.column || _self.app.column;
                this.column = this.application;
                this.explorer = _self;
            },
            "onPostSave" : function(){
                _self.reload();
            }
        };
        this.app.desktop.openApplication(e, "cms.ScriptDesigner", options);
    },
    _loadItemDataList: function(callback){
        var id = "";
        if (this.app.application) id = this.app.application.id;
        if (this.app.options.application) id = this.app.options.application.id;
        this.actions.listScript(id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.cms.ColumnManager.ScriptExplorer.Script(this, item)
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.CMSCM.LP.script.create,
            "search": MWF.CMSCM.LP.script.search,
            "searchText": MWF.CMSCM.LP.script.searchText,
            "noElement": MWF.CMSCM.LP.script.noScriptNoticeText
        };
    },
    deleteItems: function(){
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteScript();
            }else{
                item.deleteScript(function(){
                //    this.reloadItems();
                    this.hideDeleteAction();
                    this.reload();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.cms.ColumnManager.ScriptExplorer.Script = new Class({
	Extends: MWF.xApplication.cms.ColumnManager.DictionaryExplorer.Dictionary,

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
                this.application = _self.explorer.app.options.application || _self.explorer.app.options.column;
                this.column = _self.explorer.app.options.application || _self.explorer.app.options.column;
                this.explorer = _self.explorer
			}
		};
		this.explorer.app.desktop.openApplication(e, "cms.ScriptDesigner", options);
	},
	_getIcon: function(){
		var x = (Math.random()*33).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/scriptIcon/lnk.png",
			"title": this.data.name,
			"par": "cms.ScriptDesigner#{\"id\": \""+this.data.id+"\", \"applicationId\": \""+ (this.explorer.app.options.application.id ||this.explorer.app.options.column.id)+"\"}"
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