MWF.xDesktop.requireApp("process.ProcessManager", "DictionaryExplorer", null, false);
MWF.xApplication.process.ProcessManager.ProjectionExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.APPPM.LP.projection.create,
        "search": MWF.APPPM.LP.projection.search,
        "searchText": MWF.APPPM.LP.projection.searchText,
        "noElement": MWF.APPPM.LP.projection.noDictionaryNoticeText
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
                this.app.restActions.getScript(item.data.id, function(json){
                    json.data.elementType = "script";
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
    },
    pasteItem: function(data, i){
        if (i<data.length){
            var item = data[i];
            if (item.elementType==="script"){
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
        this.app.restActions.listScript(this.app.options.application.id, function(dJson){
            var i=1;
            var someItems = dJson.data.filter(function(d){ return d.id===data.id });
            if (someItems.length){
                var someItem = someItems[0];
                var lp = this.app.lp;
                var _self = this;

                var d1 = new Date().parse(data.lastUpdateTime);
                var d2 = new Date().parse(someItem.lastUpdateTime);
                var html = "<div>"+lp.copyConfirmInfor+"</div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='font-weight: bold; font-size:14px;'>"+lp.copySource+" "+someItem.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left'>"+someItem.lastUpdateTime+"</div>" +
                    "<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'>"+MWF.name.cn(someItem.lastUpdatePerson)+"</div>" +
                    "<div style='color: red; float: right;'>"+((d1>=d2) ? "": lp.copynew)+"</div></div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='clear: both;font-weight: bold; font-size:14px;'>"+lp.copyTarget+" "+data.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left;'>"+data.lastUpdateTime+"</div>" +
                    "<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'>"+MWF.name.cn(data.lastUpdatePerson)+"</div>" +
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
        data.name = someItem.name;
        data.alias = someItem.alias;
        data.isNewScript = false;
        data.application = someItem.application;
        data.applicationName = someItem.applicationName;

        this.app.restActions.saveScript(data, function(){
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
        data.isNewScript = true;
        data.application = id;
        data.applicationName = name;

        this.app.restActions.saveScript(data, function(){
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
                this.application = _self.app.options.application || _self.app.application;
                this.explorer = _self;
            }
        };
        this.app.desktop.openApplication(e, "process.ScriptDesigner", options);
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
                    "text": (this.options.noCreate) ? MWF.APPPM.LP.projection.noDictionaryNoCreateNoticeText : this.options.noElement
                }).inject(this.elementContentListNode);
                if (!this.options.noCreate){
                    noElementNode.addEvent("click", function(e){
                        this._createElement(e);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadItemDataList: function(callback){
        var id = "";
        if (this.app.application) id = this.app.application.id;
        if (this.app.options.application) id = this.app.options.application.id;
        this.actions.listProjection(id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.process.ProcessManager.ProjectionExplorer.Projection(this, item)
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.APPPM.LP.projection.create,
            "search": MWF.APPPM.LP.projection.search,
            "searchText": MWF.APPPM.LP.projection.searchText,
            "noElement": MWF.APPPM.LP.projection.noScriptNoticeText
        };
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteItem();
            }else{
                item.deleteItem(function(){
                //    this.reloadItems();
                //    this.hideDeleteAction();f
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.process.ProcessManager.ProjectionExplorer.Projection = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer.Dictionary,
    initialize: function(explorer, item){
        this.explorer = explorer;
        this.data= item;
        if (!this.data.name) this.data.name = "";
        // var id = item.sequence.substring(14, item.sequence.length);
        // this.explorer.actions.getProjection(id, function(json){
        //     this.data = json.data;
        //     this.data.name = "";
        // }.bind(this), null, false);

        //this.data = item;

        this.container = this.explorer.elementContentListNode;
        this.css = this.explorer.css;

        this.icon = this._getIcon();
    },
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
            "text": this.data.type,
            "title": this.data.type
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
    _customNodes: function(){
        // if (!this.data.validated){
        //     new Element("div", {"styles": this.explorer.css.itemErrorNode}).inject(this.node);
        //     this.node.setStyle("background-color", "#f9e8e8");
        // }
    },
	_open: function(e){
		var _self = this;
		var options = {
            "appId": "process.ProjectionDesigner"+_self.data.id,
			"onQueryLoad": function(){
				this.actions = _self.explorer.actions;
				this.category = _self;
				this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
                this.explorer = _self.explorer;
			}
		};
		this.explorer.app.desktop.openApplication(e, "process.ProjectionDesigner", options);
	},
    _getIcon: function(){
        //var x = (Math.random()*33).toInt();
        //return "process_icon_"+x+".png";
        return "projection.png";
    },
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/scriptIcon/lnk.png",
			"title": this.data.name,
			"par": "process.ProjectionDesigner#{\"id\": \""+this.data.id+"\", \"applicationId\": \""+this.explorer.app.options.application.id+"\"}"
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
    deleteItem: function(callback){
		this.explorer.app.restActions.deleteProjection(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	},
    saveItemAs: function(item){
        var id = item.id;
        var name = item.name;
        this.explorer.app.restActions.getScript(this.data.id, function(json){
            var data = json.data;
            var oldName = data.name;
            this.explorer.app.restActions.listScript(id, function(dJson){
                var i=1;
                while (dJson.data.some(function(d){ return d.name==data.name || d.alias==data.name })){
                    data.name = oldName+"_copy"+i;
                    data.alias = oldName+"_copy"+i;
                    i++;
                }
                data.id = "";
                data.isNewScript = true;
                data.application = id;
                data.applicationName = name;

                this.explorer.app.restActions.saveScript(data, function(){
                    if (id == this.explorer.app.options.application.id) this.explorer.reload();
                }.bind(this));

            }.bind(this));
        }.bind(this));
    }
});