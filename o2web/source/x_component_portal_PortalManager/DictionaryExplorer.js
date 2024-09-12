MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.ProcessManager = MWF.xApplication.process.ProcessManager || {};
MWF.xDesktop.requireApp("process.ProcessManager", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("process.ProcessManager", "DictionaryExplorer", null, false);
MWF.xApplication.portal.PortalManager.DictionaryExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.xApplication.portal.PortalManager.LP.dictionary.create,
        "search": MWF.xApplication.portal.PortalManager.LP.dictionary.search,
        "searchText": MWF.xApplication.portal.PortalManager.LP.dictionary.searchText,
        "noElement": MWF.xApplication.portal.PortalManager.LP.dictionary.noDictionaryNoticeText,
        "categoryEnable": false,
        "itemStyle": "line",
        "name": 'portal.DictionaryExplorer'
    },
    openFindDesigner: function(){
        this.app.options.application.moduleType = "portal";
        var options = {
            "filter": {
                "moduleList": ["portal"],
                "appList": [this.app.options.application]
            }
        };
        layout.openApplication(null, "FindDesigner", options);
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
        var application = _self.app.options.application || _self.app.application;
        var options = {
            "application":{
                "name": application.name,
                "id": application.id
            },
            "onQueryLoad": function(){
                this.actions = _self.app.restActions;
                this.application = application;
                this.explorer = _self;
            }
        };
        this.app.desktop.openApplication(e, "portal.DictionaryDesigner", options);
    },
    _loadItemDataList: function(callback){
        var id = "";
        if (this.app.application) id = this.app.application.id;
        if (this.app.options.application) id = this.app.options.application.id;
        this.actions.listDictionary(id,callback, function () {

        }.bind(this));
    },
    _getItemObject: function(item){
        return new MWF.xApplication.portal.PortalManager.DictionaryExplorer.Dictionary(this, item)
    },
    // setTooltip: function(){
    //     this.options.tooltip = {
    //         "create": MWF.APPPM.LP.dictionary.create,
    //         "search": MWF.APPPM.LP.dictionary.search,
    //         "searchText": MWF.APPPM.LP.dictionary.searchText,
    //         "noElement": MWF.APPPM.LP.dictionary.noDictionaryNoticeText
    //     };
    // },
    // loadElementList: function(){
    //     this._loadItemDataList(function(json){
    //         if (json.data.length){
    //             json.data.each(function(item){
    //                 var itemObj = this._getItemObject(item);
    //                 itemObj.load()
    //             }.bind(this));
    //         }else{
    //             var noElementNode = new Element("div.noElementNode", {
    //                 "styles": this.css.noElementNode,
    //                 "text": (this.options.noCreate) ? MWF.APPPM.LP.dictionary.noDictionaryNoCreateNoticeText : this.options.noElement
    //             }).inject(this.elementContentListNode);
    //             if (!this.options.noCreate){
    //                 noElementNode.addEvent("click", function(e){
    //                     this._createElement(e);
    //                 }.bind(this));
    //             }
    //         }
    //         if( !this.isSetContentSize ){
    //             this.setContentSize();
    //             this.isSetContentSize = true;
    //         }
    //     }.bind(this));
    // },
    // deleteItems: function(){
    //     this.hideDeleteAction();
    //     while (this.deleteMarkItems.length){
    //         var item = this.deleteMarkItems.shift();
    //         if (this.deleteMarkItems.length){
    //             item.deleteDictionary();
    //         }else{
    //             item.deleteDictionary(function(){
    //             //    this.reloadItems();
    //             //    this.hideDeleteAction();
    //             }.bind(this));
    //         }
    //     }
    // }
});

MWF.xApplication.portal.PortalManager.DictionaryExplorer.Dictionary = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer.Dictionary,

	_open: function(e){

		var _self = this;
		var options = {
            "appId": "portal.DictionaryDesigner"+_self.data.id,
            "id": _self.data.id,
            // "application": _self.explorer.app.options.application.id,
            "application":{
                "name": _self.explorer.app.options.application.name,
                "id": _self.explorer.app.options.application.id
            },
            "noModifyName": _self.explorer.options.noModifyName,
            "readMode": _self.explorer.options.readMode,
			"onQueryLoad": function(){
				// this.actions = _self.explorer.actions;
				this.category = _self;
				this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
                this.options.noModifyName = _self.explorer.options.noModifyName;
                this.options.readMode = _self.explorer.options.readMode;
                this.explorer = _self.explorer;
			}
		};
		this.explorer.app.desktop.openApplication(e, "portal.DictionaryDesigner", options);
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/dictionaryIcon/lnk.png",
			"title": this.data.name,
            "par": "portal.DictionaryDesigner#{\"id\": \""+this.data.id+"\", \"applicationId\": \""+this.explorer.app.options.application.id+"\"}"
		};
	},
	deleteDictionary: function(callback){
		this.explorer.app.restActions.deleteDictionary(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	},

    saveas: function(){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var selector = new MWF.O2Selector(this.explorer.app.content, {
                "title": this.explorer.app.lp.copyto,
                "type": "Portal",
                "values": [this.explorer.app.options.application],
                "onComplete": function(items){
                    items.each(function(item){
                        this.saveItemAs(item.data);
                    }.bind(this));
                }.bind(this),
            });
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
