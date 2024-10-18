MWF.xDesktop.requireApp("process.ProcessManager", "DictionaryExplorer", null, false);
MWF.xApplication.service.ServiceManager.ScriptExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer,
	Implements: [Options, Events],
    options: {
        "style": "default",
        "tooltip": {
            "create": MWF.xApplication.service.ServiceManager.LP.script.create,
            "search": MWF.xApplication.service.ServiceManager.LP.script.search,
            "searchText": MWF.xApplication.service.ServiceManager.LP.script.searchText,
            "noElement": MWF.xApplication.service.ServiceManager.LP.script.noScriptNoticeText
        },
        "categoryEnable": false,
        "itemStyle": "line",
        "name": "service.ScriptExplorer",
    },
    openFindDesigner: function(){
        var options = {
            "filter": {
                "moduleList": ["service"]
            }
        };
        layout.openApplication(null, "FindDesigner", options);
    },
    createCreateElementNode: function(){
        if( MWF.AC.isServiceManager() ) {
            this.createElementNode = new Element("div", {
                "styles": this.css.createElementNode,
                "title": this.options.tooltip.create
            }).inject(this.toolbarNode);
            this.createElementNode.addEvent("click", function (e) {
                this._createElement(e);
            }.bind(this));
        }
    },
    createTitleElementNode: function() {
        this.titleElementNode = new Element("div", {
            "styles": this.css.titleElementNode,
            "text": MWF.xApplication.service.ServiceManager.LP.scriptConfig
        }).inject(this.toolbarNode);
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
        this.app.desktop.openApplication(e, "service.ScriptDesigner", options);
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.xApplication.service.ServiceManager.LP.script.create,
            "search": MWF.xApplication.service.ServiceManager.LP.script.search,
            "searchText": MWF.xApplication.service.ServiceManager.LP.script.searchText,
            "noElement": MWF.xApplication.service.ServiceManager.LP.script.noScriptNoticeText
        };
    },
    loadElementList: function(){
        this.itemList = [];
        if( MWF.AC.isServiceManager() ){
            this._loadItemDataList(function(json){
                if (json.data.length){
                    this.checkSort(json.data);
                    json.data.each(function(item){
                        var itemObj = this._getItemObject(item);
                        itemObj.load();
                        this.checkShow(itemObj);
                        this.itemList.push(itemObj);
                    }.bind(this));
                }else{
                    var noElementNode = new Element("div.noElementNode", {
                        "styles": this.css.noElementNode,
                        "text": this.options.tooltip.noElement
                    }).inject(this.elementContentListNode);
                    noElementNode.addEvent("click", function(e){
                        this._createElement(e);
                    }.bind(this));
                }
                this.loadTopNode();
                if( !this.isSetContentSize ){
                    this.setContentSize();
                    this.isSetContentSize = true;
                }
            }.bind(this));
        }else{
            var noElementNode = new Element("div.noElementNode", {
                "styles": this.css.noElementNode,
                "text": MWF.xApplication.service.ServiceManager.LP.script.noPermission
            }).inject(this.elementContentListNode);
        }

    },
    _loadItemDataList: function(callback){
        this.app.restActions.listScript(callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.service.ServiceManager.ScriptExplorer.Script(this, item)
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
                    //this.hideDeleteAction();
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
        this.app.restActions.listScript(function(dJson){
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

        this.app.restActions.updateScript(data, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
    },
    saveItemAsNew: function(dJson, data, success, failure){

        var oldName = data.name;

        var i=1;
        while (dJson.data.some(function(d){ return d.name==data.name || d.alias==data.name })){
            data.name = oldName+"_copy"+i;
            data.alias = oldName+"_copy"+i;
            i++;
        }
        data.id = "";
        data.isNewScript = true;

        this.app.restActions.addScript(data, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
    }
});

MWF.xApplication.service.ServiceManager.ScriptExplorer.Script = new Class({
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
            "appId": "service.ScriptDesigner"+_self.data.id,
            "id": _self.data.id,
			"onQueryLoad": function(){
				this.actions = _self.explorer.actions;
				this.category = _self;
				this.options.id = _self.data.id;
			}
		};
		this.explorer.app.desktop.openApplication(e, "service.ScriptDesigner", options);
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
			"par": "service.ScriptDesigner#{\"id\": \""+this.data.id+"\"}"
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
		this.explorer.app.restActions.removeScript(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});
