MWF.xDesktop.requireApp("process.ProcessManager", "Explorer", null, false);
MWF.xApplication.process.ProcessManager.DictionaryExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.APPPM.LP.dictionary.create,
        "search": MWF.APPPM.LP.dictionary.search,
        "searchText": MWF.APPPM.LP.dictionary.searchText,
        "noElement": MWF.APPPM.LP.dictionary.noDictionaryNoticeText
    },

    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "/x_component_process_ProcessManager/$DictionaryExplorer/";
        this.cssPath = "/x_component_process_ProcessManager/$DictionaryExplorer/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.actions = actions;
        this.node = $(node);
        this.initData();
    },
    setContentSize: function(){
        var toolbarSize = this.toolbarNode.getSize();
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y-toolbarSize.y-pt-pb;
        this.elementContentNode.setStyle("height", ""+height+"px");

        if (this.options.noCreate) this.createElementNode.destroy();
    },
    showDeleteAction: function(){
        if (!this.deleteItemsAction){
            this.deleteItemsAction = new Element("div", {
                "styles": this.css.deleteItemsAction,
                "text": this.app.lp.deleteItems
            }).inject(this.node);
            this.deleteItemsAction.fade("in");
            this.deleteItemsAction.position({
                relativeTo: this.elementContentListNode,
                position: 'centerTop',
                edge: 'centerTop',
                "offset": {"y": this.elementContentNode.getScroll().y}
            });
            this.deleteItemsAction.addEvent("click", function(){
                var _self = this;
                this.app.confirm("warn", this.deleteItemsAction, MWF.APPPM.LP.deleteElementTitle, MWF.APPPM.LP.deleteElement, 300, 120, function(){
                    _self.deleteItems();
                    this.close();
                }, function(){
                    this.close();
                });
            }.bind(this));
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
            if (e) e.preventDefault();
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
        data.application = someItem.application;
        data.applicationName = someItem.applicationName;
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

        this.app.restActions.saveDictionary(data, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
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
        this.app.desktop.openApplication(e, "process.DictionaryDesigner", options);
    },
    _loadItemDataList: function(callback){
        this.actions.listDictionary(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.process.ProcessManager.DictionaryExplorer.Dictionary(this, item)
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.APPPM.LP.dictionary.create,
            "search": MWF.APPPM.LP.dictionary.search,
            "searchText": MWF.APPPM.LP.dictionary.searchText,
            "noElement": MWF.APPPM.LP.dictionary.noDictionaryNoticeText
        };
    },
    loadElementList: function(){
        this._loadItemDataList(function(json){
            if (json.data.length){
                json.data.each(function(item){
                    var itemObj = this._getItemObject(item);
                    itemObj.load()
                }.bind(this));
            }else{
                var noElementNode = new Element("div.noElementNode", {
                    "styles": this.css.noElementNode,
                    "text": (this.options.noCreate) ? MWF.APPPM.LP.dictionary.noDictionaryNoCreateNoticeText : this.options.noElement
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
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteDictionary();
            }else{
                item.deleteDictionary(function(){
                //    this.reloadItems();
                //    this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.process.ProcessManager.DictionaryExplorer.Dictionary = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer.Item,

    load: function(){
        this.node = new Element("div", {
            "styles": this.explorer.css.itemNode,
            "events": {
                "mouseover": function(){
                    if (this.deleteActionNode) this.deleteActionNode.fade("in");
                    if (this.saveasActionNode) this.saveasActionNode.fade("in");
                }.bind(this),
                "mouseout": function(){
                    if (this.deleteActionNode) this.deleteActionNode.fade("out");
                    if (this.saveasActionNode) this.saveasActionNode.fade("out");
                }.bind(this)
            }
        }).inject(this.container);

        if (this.data.name.icon) this.icon = this.data.name.icon;
        var iconUrl = this.explorer.path+""+this.explorer.options.style+"/processIcon/"+this.icon;

        var itemIconNode = new Element("div", {
            "styles": this.explorer.css.itemIconNode
        }).inject(this.node);
        itemIconNode.setStyle("background", "url("+iconUrl+") center center no-repeat");
        //new Element("img", {
        //    "src": iconUrl, "border": "0"
        //}).inject(itemIconNode);

        itemIconNode.addEvent("click", function(e){
            this.toggleSelected();
            e.stopPropagation();
        }.bind(this));

        itemIconNode.makeLnk({
            "par": this._getLnkPar()
        });

        if (!this.explorer.options.noDelete){
            this._createActions();
        }

        var inforNode = new Element("div", {
            "styles": this.explorer.css.itemInforNode
        }).inject(this.node);
        var inforBaseNode = new Element("div", {
            "styles": this.explorer.css.itemInforBaseNode
        }).inject(inforNode);

        new Element("div", {
            "styles": this.explorer.css.itemTextTitleNode,
            "text": this.data.name,
            "title": this.data.name,
            "events": {
                "click": function(e){this._open(e);e.stopPropagation();}.bind(this)
            }
        }).inject(inforBaseNode);

        new Element("div", {
            "styles": this.explorer.css.itemTextAliasNode,
            "text": this.data.alias,
            "title": this.data.alias
        }).inject(inforBaseNode);
        new Element("div", {
            "styles": this.explorer.css.itemTextDateNode,
            "text": (this.data.updateTime || "")
        }).inject(inforBaseNode);

        new Element("div", {
            "styles": this.explorer.css.itemTextDescriptionNode,
            "text": this.data.description || "",
            "title": this.data.description || ""
        }).inject(inforNode);

        this._customNodes();

        this._isNew();
    },
    _createActions: function(){
        this.deleteActionNode = new Element("div", {
            "styles": this.explorer.css.deleteActionNode
        }).inject(this.node);
        this.deleteActionNode.addEvent("click", function(e){
            this.deleteItem(e);
        }.bind(this));

        this.saveasActionNode = new Element("div", {
            "styles": this.css.saveasActionNode,
            "title": this.explorer.app.lp.copy
        }).inject(this.node);
        this.saveasActionNode.addEvent("click", function(e){
            this.saveas(e);
        }.bind(this));
    },
    _customNodes: function(){},

	_open: function(e){

		var _self = this;
		var options = {
            "appId": "process.DictionaryDesigner"+_self.data.id,
			"onQueryLoad": function(){
				this.actions = _self.explorer.actions;
				this.category = _self;
				this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
                this.options.noModifyName = _self.explorer.options.noModifyName;
                this.options.readMode = _self.explorer.options.readMode;
                this.explorer = _self.explorer;
			}
		};
		this.explorer.app.desktop.openApplication(e, "process.DictionaryDesigner", options);
	},
	_getIcon: function(){
		//var x = (Math.random()*33).toInt();
		//return "process_icon_"+x+".png";
        return "dictionary.png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/dictionaryIcon/lnk.png",
			"title": this.data.name,
            "par": "process.DictionaryDesigner#{\"id\": \""+this.data.id+"\", \"applicationId\": \""+this.explorer.app.options.application.id+"\"}"
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
	deleteDictionary: function(callback){
		this.explorer.app.restActions.deleteDictionary(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	},

    saveItemAs: function(item){
        var id = item.id;
        var name = item.name;
        this.explorer.app.restActions.getDictionary(this.data.id, function(json){
            var data = json.data;
            var oldName = data.name;
            this.explorer.app.restActions.listDictionary(id, function(dJson){
                var i=1;
                while (dJson.data.some(function(d){ return d.name==data.name || d.alias==data.name })){
                    data.name = oldName+"_copy"+i;
                    data.alias = oldName+"_copy"+i;
                    i++;
                }
                data.id = "";
                data.application = id;
                data.applicationName = name;
                this.explorer.app.restActions.saveDictionary(data, function(){
                    if (id == this.explorer.app.options.application.id) this.explorer.reload();
                }.bind(this));

            }.bind(this));
        }.bind(this));
    }
});
