MWF.xDesktop.requireApp("query.QueryManager", "StatExplorer", null, false);
MWF.xApplication.query.QueryManager.TableExplorer = new Class({
	Extends: MWF.xApplication.query.QueryManager.StatExplorer,
	Implements: [Options, Events],
    options: {
        "style": "default",
        "tooltip": {
            "create": MWF.xApplication.query.QueryManager.LP.table.create,
            "search": MWF.xApplication.query.QueryManager.LP.table.search,
            "searchText": MWF.xApplication.query.QueryManager.LP.table.searchText,
            "noElement": MWF.xApplication.query.QueryManager.LP.table.noStatNoticeText
        }
    },
    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "/x_component_query_QueryManager/$Explorer/";
        this.cssPath = "/x_component_query_QueryManager/$Explorer/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.actions = actions;
        this.node = $(node);
        this.initData();
    },
    saveItemAsUpdate: function(someItem, data, success, failure){
        var item = this.app.options.application;
        var id = item.id;
        var name = item.name;

        //var dataJson = (data.data) ? JSON.decode(data.data): "";
        var draftDataJson = (data.draftData) ? JSON.decode(data.draftData): "";
        data.status = "draft";
        data.draftData = draftDataJson;
        data.data.id = someItem.id;
        data.id = someItem.id;
        data.isNewTable = false;
        data.application = id;
        data.applicationName = name;

        this.app.restActions.saveTable(data, function(){
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
        var dataJson = (data.data) ? JSON.decode(data.data): "";
        data.data = dataJson;
        data.data.id = "";
        data.id = "";
        data.isNewTable = true;
        data.application = id;
        data.applicationName = name;

        this.app.restActions.saveTable(data, function(){
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
        this.app.desktop.openApplication(e, "query.TableDesigner", options);
    },

    _loadItemDataList: function(callback){
        this.app.restActions.listTable(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.query.QueryManager.TableExplorer.Table(this, item);
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteTable();
            }else{
                item.deleteTable(function(){
                    //this.reloadItems();
                    //this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.query.QueryManager.TableExplorer.Table= new Class({
	Extends: MWF.xApplication.query.QueryManager.StatExplorer.Stat,
	
	_open: function(e){
        var _self = this;
        var options = {
            "appId": "query.TableDesigner"+_self.data.id,
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
            }
        };
        this.explorer.app.desktop.openApplication(e, "query.TableDesigner", options);
	},
	_getIcon: function(){
		var x = (Math.random()*49).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/tableIcon/lnk.png",
			"title": this.data.name,
			"par": "query.TableDesigner#{\"appId\": \"query.TableDesigner"+this.data.id+"\", \"id\": \""+this.data.id+"\", \"applicationId\": \""+this.data.query+"\"}"
		};
	},
    deleteTable: function(callback){
		this.explorer.actions.deleteTable(this.data.id, function(){
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
        this.explorer.app.restActions.getTable(this.data.id, function(json){
            var data = json.data;
            var dataJson = (data.data) ? JSON.decode(data.data): "";
            data.data = dataJson;
            data.data.id = "";
            var oldName = data.name;
            this.explorer.app.restActions.listTable(id, function(dJson){
                var i=1;
                while (dJson.data.some(function(d){ return d.name==data.name || d.alias==data.name })){
                    data.name = oldName+"_copy"+i;
                    data.alias = oldName+"_copy"+i;
                    i++;
                }
                data.id = "";
                data.isNewTable = true;
                data.application = id;
                data.applicationName = name;

                this.explorer.app.restActions.saveTable(data, function(){
                    if (id == this.explorer.app.options.application.id) this.explorer.reload();
                }.bind(this));

            }.bind(this));
        }.bind(this));
    }
});
