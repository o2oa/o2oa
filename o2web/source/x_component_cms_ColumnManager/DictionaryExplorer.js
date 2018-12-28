MWF.xDesktop.requireApp("cms.ColumnManager", "Explorer", null, false);
MWF.xApplication.cms.ColumnManager.DictionaryExplorer = new Class({
	Extends: MWF.xApplication.cms.ColumnManager.Explorer,
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
        return new MWF.xApplication.cms.ColumnManager.DictionaryExplorer.Dictionary(this, item, {index : index})
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
    },

    keyCopy: function(e){
        if (this.selectMarkItems.length){
            var items = [];
            var i = 0;

            var checkItems = function(e){
                if (i>=this.selectMarkItems.length){
                    if (items.length){
                        var str = JSON.encode(items);
                        if (e){
                            e.clipboardData.setData('text/plain', str);
                        }else {
                            window.clipboardData.setData("Text", str);
                        }
                        this.app.notice(this.app.lp.copyed, "success");
                    }
                }
            }.bind(this);

            this.selectMarkItems.each(function(item){
                this.app.restActions.getDictionary(item.data.id, function(json){
                    json.data.elementType = "dictionary";
                    items.push(json.data);
                    i++;
                    checkItems(e);
                }.bind(this), null, false)
            }.bind(this));
        }
    },
    keyPaste: function(e){
        var dataStr = "";
        if (e){
            dataStr = e.clipboardData.getData('text/plain');
        }else{
            dataStr = window.clipboardData.getData("Text");
        }
        var data = JSON.decode(dataStr);

        this.pasteItem(data, 0);

        // data.each(function(item){
        //     if (item.elementType==="dictionary"){
        //         this.saveItemAs(this.app.options.application, item);
        //     }
        // }.bind(this));
    },
    pasteItem: function(data, i){
        if (i<data.length){
            var item = data[i];
            if (item.elementType==="dictionary"){
                this.saveItemAs(item, function(){
                    i++;
                    this.pasteItem(data, i);
                }.bind(this), function(){
                    i++;
                    this.pasteItem(data, i);
                }.bind(this), function(){
                    this.reload();
                }.bind(this));
            }else{
                i++;
                this.pasteItem(data, i);
            }
        }else{
            this.reload();
        }
    },
    saveItemAs: function(data, success, failure, cancel){
        this.app.restActions.listDictionary(this.app.options.application.id, function(dJson){
            dJson.data = dJson.data || [];
            var i=1;
            var someItems = dJson.data.filter(function(d){ return d.id===data.id });
            if (someItems.length){
                var someItem = someItems[0];
                var lp = this.app.lp;

                var _self = this;
                var d1 = new Date().parse(data.updateTime);
                var d2 = new Date().parse(someItem.updateTime);
                var html = "<div>"+lp.copyConfirmInfor+"</div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='font-weight: bold; font-size:14px;'>"+lp.copySource+" "+someItem.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left'>"+someItem.updateTime+"</div>" +
                    "<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'></div>" +
                    "<div style='color: red; float: right;'>"+((d1>=d2) ? "": lp.copynew)+"</div></div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='clear: both;font-weight: bold; font-size:14px;'>"+lp.copyTarget+" "+data.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left;'>"+data.updateTime+"</div>" +
                    "<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'></div>" +
                    "<div style='color: red; float: right;'>"+((d1<=d2) ? "": lp.copynew)+"</div></div>";
//                html += "<>"
                this.app.dlg("inofr", null, this.app.lp.copyConfirmTitle, {"html": html}, 500, 290, [
                    {
                        "text": lp.copyConfirm_overwrite,
                        "action": function(){_self.saveItemAsUpdate(someItem, data, success, failure);this.close();}
                    },
                    {
                        "text": lp.copyConfirm_new,
                        "action": function(){_self.saveItemAsNew(dJson, data, success, failure);this.close();}
                    },
                    {
                        "text": lp.copyConfirm_skip,
                        "action": function(){/*nothing*/ this.close(); if (success) success();}
                    },
                    {
                        "text": lp.copyConfirm_cancel,
                        "action": function(){this.close(); if (cancel) cancel();}
                    }
                ]);
            }else{
                this.saveItemAsNew(dJson, data, success, failure)
            }
        }.bind(this), function(){if (failure) failure();}.bind(this));
    },
    saveItemAsUpdate: function(someItem, data, success, failure){
        data.id = someItem.id;
        data.application = someItem.appId || someItem.application ;
        data.applicationName = someItem.appName || someItem.applicationName;

        data.appId = data.application;
        data.appName = data.applicationName;

        data.name = someItem.name;
        data.alias = someItem.alias;

        this.app.restActions.saveDictionary(data, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
    },
    saveItemAsNew: function(dJson, data, success, failure){
        var item = this.app.options.application;
        var id = item.id;
        var name = item.name;
        var oldName = data.name;

        var i=1;
        while (dJson.data.some(function(d){ return d.name==data.name || d.alias==data.name })){
            data.name = oldName+"_copy"+i;
            data.alias = oldName+"_copy"+i;
            i++;
        }
        data.id = "";
        data.application = id;
        data.applicationName = name;

        data.appId = id;
        data.appName = name;

        delete data.createTime;
        delete data.updateTime;
        delete data.elementType;

        this.app.restActions.saveDictionary(data, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
    }
});

MWF.xApplication.cms.ColumnManager.DictionaryExplorer.Dictionary = new Class({
	Extends: MWF.xApplication.cms.ColumnManager.Explorer.Item,

    //load: function(){
    //    if( this.options.index % 2 == 0 ){
    //        this.itemNodeCss = this.explorer.css.itemNode_even
    //    }else{
    //        this.itemNodeCss = this.explorer.css.itemNode
    //    }
    //    this.node = new Element("div", {
    //        "styles": this.itemNodeCss,
    //        "events": {
    //            "click": function(e){this._open(e);e.stopPropagation();}.bind(this),
    //            "mouseover": function(){
    //                if( !this.isSelected )this.node.setStyles( this.explorer.css.itemNode_over )
    //            }.bind(this),
    //            "mouseout": function(){
    //                if( !this.isSelected )this.node.setStyles( this.itemNodeCss )
    //            }.bind(this)
    //        }
    //    }).inject(this.container,this.options.where);
    //
    //
    //    if (this.data.icon) this.icon = this.data.icon;
    //    var iconUrl = this.explorer.path+""+this.explorer.options.style+"/processIcon/"+this.icon;
    //
    //    var itemIconNode = new Element("div", {
    //        "styles": this.explorer.css.itemIconNode
    //    }).inject(this.node);
    //    itemIconNode.setStyle("background", "url("+iconUrl+") center center no-repeat");
    //    //new Element("img", {
    //    //    "src": iconUrl, "border": "0"
    //    //}).inject(itemIconNode);
    //
    //    itemIconNode.makeLnk({
    //        "par": this._getLnkPar()
    //    });
    //
    //    itemIconNode.addEvent("click", function(e){
    //        this.toggleSelected();
    //        e.stopPropagation();
    //    }.bind(this));
    //
    //    this.actionsArea = new Element("div.actionsArea",{
    //        styles : this.explorer.css.actionsArea
    //    }).inject(this.node);
    //    if (!this.explorer.options.noDelete){
    //        this.deleteActionNode = new Element("div.deleteAction", {
    //            "styles": this.explorer.css.deleteAction
    //        }).inject(this.actionsArea);
    //        this.deleteActionNode.addEvent("click", function(e){
    //            this.deleteItem(e);
    //            e.stopPropagation();
    //        }.bind(this));
    //        this.deleteActionNode.addEvents({
    //            "mouseover" : function(ev){
    //                this.deleteActionNode.setStyles( this.explorer.css.deleteAction_over )
    //            }.bind(this),
    //            "mouseout" : function(ev){
    //                this.deleteActionNode.setStyles( this.explorer.css.deleteAction )
    //            }.bind(this)
    //        })
    //    }
    //
    //
    //    var inforNode = new Element("div.itemInforNode", {
    //        "styles": this.explorer.css.itemInforNode
    //    }).inject(this.node);
    //    var inforBaseNode = new Element("div.itemInforBaseNode", {
    //        "styles": this.explorer.css.itemInforBaseNode
    //    }).inject(inforNode);
    //
    //    new Element("div.itemTextTitleNode", {
    //        "styles": this.explorer.css.itemTextTitleNode,
    //        "text": this.data.name,
    //        "title": this.data.name
    //    }).inject(inforBaseNode);
    //
    //    new Element("div.itemTextAliasNode", {
    //        "styles": this.explorer.css.itemTextAliasNode,
    //        "text": this.data.alias,
    //        "title": this.data.alias
    //    }).inject(inforBaseNode);
    //    new Element("div.itemTextDateNode", {
    //        "styles": this.explorer.css.itemTextDateNode,
    //        "text": (this.data.updateTime || "")
    //    }).inject(inforBaseNode);
    //
    //    new Element("div.itemTextDescriptionNode", {
    //        "styles": this.explorer.css.itemTextDescriptionNode,
    //        "text": this.data.description || "",
    //        "title": this.data.description || ""
    //    }).inject(inforBaseNode);
    //
    //
    //},
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
		this.explorer.app.restActions.removeDictionary(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	},
    saveItemAs: function(item){
        var id = item.id;
        var name = item.name || item.appName;
        this.explorer.app.restActions.getDictionary(this.data.id, function(json){
            var data = json.data;
            var oldName = data.name;
            this.explorer.app.restActions.listDictionary(id, function(dJson){
                dJson.data = dJson.data || [];
                var i=1;
                while (dJson.data.some(function(d){ return d.name==data.name || d.alias==data.name })){
                    data.name = oldName+"_copy"+i;
                    data.alias = oldName+"_copy"+i;
                    i++;
                }
                data.id = "";
                data.appId = id;
                data.appName = name;
                data.application = id;
                data.applicationName = name;

                delete data.createTime;
                delete data.updateTime;

                this.explorer.app.restActions.saveDictionary(data, function(){
                    if (id == this.explorer.app.options.application.id) this.explorer.reload();
                }.bind(this));

            }.bind(this));
        }.bind(this));
    }
});
