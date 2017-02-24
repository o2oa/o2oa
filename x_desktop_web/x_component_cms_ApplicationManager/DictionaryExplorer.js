MWF.xDesktop.requireApp("cms.ApplicationManager", "Explorer", null, false);
MWF.xApplication.cms.ApplicationManager.DictionaryExplorer = new Class({
	Extends: MWF.xApplication.cms.ApplicationManager.Explorer,
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
                this.application = _self.app.options.column;
                this.column = _self.app.options.column;
            },
            "onPostSave" : function(){
                _self.reload();
            }
        };
        this.app.desktop.openApplication(e, "cms.DictionaryDesigner", options);
    },
    _loadItemDataList: function(callback){
        this.actions.listDictionary(this.app.options.column.id,callback);
    },
    _getItemObject: function(item, index){
        return new MWF.xApplication.cms.ApplicationManager.DictionaryExplorer.Dictionary(this, item, {index : index})
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.CMSCM.LP.dictionary.create,
            "search": MWF.CMSCM.LP.dictionary.search,
            "searchText": MWF.CMSCM.LP.dictionary.searchText,
            "noElement": MWF.CMSCM.LP.dictionary.noDictionaryNoticeText
        };
    },
    loadElementList: function(callback){
        this._loadItemDataList(function(json){
            if (json.data.length){
                json.data.each(function(item){
                    var itemObj = this._getItemObject(item, this.itemArray.length + 1);
                    itemObj.load();
                    this.itemObject[ item.id ] = itemObj;
                    this.itemArray.push( itemObj );
                }.bind(this));
                if( callback )callback();
            }else{
                var noElementNode = new Element("div", {
                    "styles": this.css.noElementNode,
                    "text": (this.options.noCreate) ? MWF.CMSCM.LP.dictionary.noDictionaryNoCreateNoticeText : this.options.tooltip.noElement
                }).inject(this.elementContentListNode);
                if (!this.options.noCreate){
                    noElementNode.addEvent("click", function(e){
                        this._createElement(e);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    deleteItems: function(){
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteDictionary();
            }else{
                item.deleteDictionary(function(){
                //    this.reloadItems();
                    this.hideDeleteAction();
                    this.reload();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.cms.ApplicationManager.DictionaryExplorer.Dictionary = new Class({
	Extends: MWF.xApplication.cms.ApplicationManager.Explorer.Item,

    _customNodes: function(){},

	_open: function(e){
		var _self = this;
		var options = {
			"onQueryLoad": function(){
				this.actions = _self.explorer.actions;
				this.category = _self;
				this.options.id = _self.data.id;
                this.column = _self.explorer.app.options.column;
                this.application = _self.explorer.app.options.column;
                this.options.noModifyName = _self.explorer.options.noModifyName;
                this.options.readMode = _self.explorer.options.readMode
			}
		};
		this.explorer.app.desktop.openApplication(e, "cms.DictionaryDesigner", options);
	},
	_getIcon: function(){
		var x = (Math.random()*33).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		var par = {
			"icon": this.explorer.path+this.explorer.options.style+"/dictionaryIcon/lnk.png",
			"title": this.data.name,
			"par": "cms.DictionaryDesigner#{\"id\": \""+this.data.id +"\", \"application\" : "+ JSON.stringify(this.explorer.app.options.column) +"}"
		};
        return par
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
	deleteDictionary: function(callback){
		this.explorer.app.restActions.deleteDictionary(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});
