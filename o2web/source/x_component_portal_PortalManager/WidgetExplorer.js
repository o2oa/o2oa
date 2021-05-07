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
                this.app.restActions.getWidget(item.data.id, function(json){
                    json.data.elementType = "widget";
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
            if (item.elementType==="widget"){
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
        this.app.restActions.listWidget(this.app.options.application.id, function(dJson){
            var i=1;
            var someItems = dJson.data.filter(function(d){ return d.id===data.id });
            if (someItems.length){
                var someItem = someItems[0];
                var lp = this.app.lp;
                var _self = this;

                var d1 = new Date().parse(data.lastUpdateTime || data.updateTime);
                var d2 = new Date().parse(someItem.lastUpdateTime || someItem.updateTime);
                var html = "<div>"+lp.copyConfirmInfor+"</div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='font-weight: bold; font-size:14px;'>"+lp.copySource+" "+someItem.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left'>"+(someItem.lastUpdateTime || someItem.updateTime)+"</div>" +
                    "<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'>"+MWF.name.cn(someItem.lastUpdatePerson || "")+"</div>" +
                    "<div style='color: red; float: right;'>"+((d1>=d2) ? "": lp.copynew)+"</div></div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='clear: both;font-weight: bold; font-size:14px;'>"+lp.copyTarget+" "+data.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left;'>"+(data.lastUpdateTime || data.updateTime)+"</div>" +
                    "<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'>"+MWF.name.cn(data.lastUpdatePerson || "")+"</div>" +
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
    saveItemAsUpdate: function(someItem, form, success, failure){
        var item = this.app.options.application;

        var pcdata = JSON.decode(MWF.decodeJsonString(form.data));
        var mobiledata = JSON.decode(MWF.decodeJsonString(form.mobileData));

        pcdata.id = someItem.id;
        pcdata.isNewPage = false;
        pcdata.json.id = someItem.id;
        pcdata.json.application = item.id;
        pcdata.json.applicationName = item.name;
        pcdata.json.name = someItem.name;
        pcdata.json.alias = someItem.alias;
        mobiledata.json.id = someItem.id;
        mobiledata.json.application = item.id;
        mobiledata.applicationName = item.name;
        mobiledata.json.name = someItem.name;
        mobiledata.json.alias = someItem.alias;

        this.app.restActions.saveWidget(pcdata, mobiledata, null, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
    },
    saveItemAsNew: function(formsJson, form, success, failure){
        var item = this.app.options.application;
        var id = item.id;
        var name = item.name;

        var pcdata = JSON.decode(MWF.decodeJsonString(form.data));
        var mobiledata = JSON.decode(MWF.decodeJsonString(form.mobileData));

        var oldName = pcdata.json.name;

        var i=1;
        while (formsJson.data.some(function(d){ return d.name==pcdata.json.name })){
            pcdata.json.name = oldName+"_copy"+i;
            mobiledata.json.name = oldName+"_copy"+i;
            i++;
        }
        pcdata.id = "";
        pcdata.isNewPage = true;
        pcdata.json.id = "";
        pcdata.json.application = id;
        pcdata.json.applicationName = name;
        pcdata.json.alias = "";

        mobiledata.json.id = "";
        mobiledata.json.application = id;
        mobiledata.applicationName = name;
        mobiledata.json.alias = "";

        this.app.restActions.saveWidget(pcdata, mobiledata, null, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
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
            "appId": "portal.WidgetDesigner"+_self.data.id,
            "id": _self.data.id,
            "application": _self.explorer.app.options.application.id,
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
