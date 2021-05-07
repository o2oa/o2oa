MWF.xDesktop.requireApp("query.QueryManager", "StatExplorer", null, false);
MWF.xApplication.query.QueryManager.StatementExplorer = new Class({
	Extends: MWF.xApplication.query.QueryManager.StatExplorer,
	Implements: [Options, Events],
    options: {
        "style": "default",
        "tooltip": {
            "create": MWF.xApplication.query.QueryManager.LP.statement.create,
            "search": MWF.xApplication.query.QueryManager.LP.statement.search,
            "searchText": MWF.xApplication.query.QueryManager.LP.statement.searchText,
            "noElement": MWF.xApplication.query.QueryManager.LP.statement.noStatNoticeText
        }
    },
    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "../x_component_query_QueryManager/$Explorer/";
        this.cssPath = "../x_component_query_QueryManager/$Explorer/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.actions = actions;
        this.node = $(node);
        this.initData();
    },

    openFindDesigner: function(){
        this.app.options.application.moduleType = "query";
        var options = {
            "filter": {
                "moduleList": ["query"],
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
                this.app.restActions.getStatement(item.data.id, function(json){
                    json.data.elementType = "statement";
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
            if (item.elementType==="statement"){
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
        this.app.restActions.listStatement(this.app.options.application.id, function(dJson){
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
    saveItemAsUpdate: function(someItem, data, success, failure){
        var item = this.app.options.application;
        var id = item.id;
        var name = item.name;

        data.id = someItem.id;
        data.isNewStatement = false;
        data.application = id;
        data.applicationName = name;

        this.app.restActions.saveStatement(data, function(){
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
        data.isNewStatement = true;
        data.application = id;
        data.applicationName = name;

        this.app.restActions.saveStatement(data, function(){
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
        this.app.desktop.openApplication(e, "query.StatementDesigner", options);
    },

    _loadItemDataList: function(callback){
        this.app.restActions.listStatement(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.query.QueryManager.StatementExplorer.Statement(this, item);
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteStatement();
            }else{
                item.deleteStatement(function(){
                    //this.reloadItems();
                    //this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.query.QueryManager.StatementExplorer.Statement= new Class({
	Extends: MWF.xApplication.query.QueryManager.StatExplorer.Stat,
	
	_open: function(e){
        var _self = this;
        var options = {
            "appId": "query.StatementDesigner"+_self.data.id,
            "id": _self.data.id,
            "application":_self.explorer.app.options.application.id,
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
            }
        };
        this.explorer.app.desktop.openApplication(e, "query.StatementDesigner", options);
	},
	_getIcon: function(){
		var x = (Math.random()*49).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/statementIcon/lnk.png",
			"title": this.data.name,
			"par": "query.StatementDesigner#{\"id\": \""+this.data.id+"\", \"applicationId\": \""+this.data.query+"\"}"
		};
	},
    deleteStatement: function(callback){
		this.explorer.actions.deleteStatement(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	},
    saveas: function(){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var selector = new MWF.O2Selector(this.explorer.app.content, {
                "title": this.explorer.app.lp.copyto,
                "type": "Query",
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
        this.explorer.app.restActions.getStatement(this.data.id, function(json){
            var data = json.data;
            var dataJson = (data.data) ? JSON.decode(data.data): "";
            data.data = dataJson;
            data.data.id = "";
            var oldName = data.name;
            this.explorer.app.restActions.listStatement(id, function(dJson){
                var i=1;
                while (dJson.data.some(function(d){ return d.name==data.name || d.alias==data.name })){
                    data.name = oldName+"_copy"+i;
                    data.alias = oldName+"_copy"+i;
                    i++;
                }
                data.id = "";
                data.isNewStatement = true;
                data.application = id;
                data.applicationName = name;

                this.explorer.app.restActions.saveStatement(data, function(){
                    if (id == this.explorer.app.options.application.id) this.explorer.reload();
                }.bind(this));

            }.bind(this));
        }.bind(this));
    }
});
