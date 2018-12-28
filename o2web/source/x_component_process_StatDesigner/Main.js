MWF.xDesktop.requireApp("process.ViewDesigner", "", null, false);
MWF.APPSTD = MWF.xApplication.process.StatDesigner;
MWF.APPSTD.options = {
	"multitask": true,
	"executable": false
};
//MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("process.StatDesigner", "Stat", null, false);

MWF.xApplication.process.StatDesigner.Main = new Class({
	Extends: MWF.xApplication.process.ViewDesigner.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "process.StatDesigner",
		"icon": "icon.png",
		"title": MWF.APPSTD.LP.title,
		"appTitle": MWF.APPSTD.LP.title,
		"id": "",
        "tooltip": {
            "unCategory": MWF.APPSTD.LP.unCategory
        },
		"actions": null,
		"category": null,
		"processData": null
	},

    onQueryLoad: function(){
        this.shortcut = true;
        if (this.status){
            this.options.application = this.status.applicationId;
            this.application = this.status.application;
            this.options.id = this.status.id;
        }

        if (!this.options.id){
            this.options.desktopReload = false;
            this.options.title = this.options.title + "-"+MWF.APPSTD.LP.newStat;
        }
        if (!this.actions) this.actions = MWF.Actions.get("x_processplatform_assemble_designer");
        //if (!this.actions) this.actions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();

        this.lp = MWF.xApplication.process.StatDesigner.LP;

        this.addEvent("queryClose", function(e){
            if (this.explorer){
                this.explorer.reload();
            }
        }.bind(this));
        this.addEvent("postLoadWindowMax", function(e){
            this.loadWindowOk = true;
            if (this.loadApplicationOk && this.loadWindowOk) this.view.setViewWidth();
        }.bind(this));
        this.addEvent("postLoadApplication", function(e){
            this.loadApplicationOk = true;
            if (this.loadApplicationOk && this.loadWindowOk) this.view.setViewWidth();
        }.bind(this));
    },

    loadViewList: function(){
        this.actions.listStat(this.application.id, function (json) {
            json.data.each(function(view){
                this.createListViewItem(view);
            }.bind(this));
        }.bind(this), null, false);
    },

    //列示所有视图列表
    createListViewItem: function(view, isNew){
        var _self = this;
        var listViewItem = new Element("div", {"styles": this.css.listViewItem}).inject(this.viewListAreaNode, (isNew) ? "top": "bottom");
        var listViewItemIcon = new Element("div", {"styles": this.css.listViewItemIcon}).inject(listViewItem);
        var listViewItemText = new Element("div", {"styles": this.css.listViewItemText, "text": (view.name) ? view.name+" ("+view.alias+")" : this.lp.newStat}).inject(listViewItem);

        listViewItem.store("view", view);
        listViewItem.addEvents({
            "dblclick": function(e){_self.loadViewByData(this, e);},
            "mouseover": function(){if (_self.currentListViewItem!=this) this.setStyles(_self.css.listViewItem_over);},
            "mouseout": function(){if (_self.currentListViewItem!=this) this.setStyles(_self.css.listViewItem);}
        });
    },
    //打开视图
    loadViewByData: function(node, e){
        var view = node.retrieve("view");
        if (openNew){
            var _self = this;
            var options = {
                "onQueryLoad": function(){
                    this.actions = _self.actions;
                    this.category = _self;
                    this.options.id = view.id;
                    this.application = _self.application;
                    this.explorer = _self.explorer;
                }
            };
            this.desktop.openApplication(e, "process.StatDesigner", options);
        }
    },
	
	//loadView------------------------------------------
    loadView: function(){
		this.getViewData(this.options.id, function(vdata){
            this.setTitle(this.options.appTitle + "-"+vdata.name);
            this.taskitem.setText(this.options.appTitle + "-"+vdata.name);
            this.options.appTitle = this.options.appTitle + "-"+vdata.name;
            this.view = new MWF.xApplication.process.StatDesigner.Stat(this, vdata);
			this.view.load();
		}.bind(this));
	},

	loadNewViewData: function(callback){
        var url = "/x_component_process_StatDesigner/$Stat/stat.json";
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                this.actions.getUUID(function(id){
                    obj.id=id;
                    obj.isNewView = true;
                    obj.application = this.application.id;
                    this.createListViewItem(obj, true);
                    if (callback) callback(obj);
                }.bind(this));
            }.bind(this),
            "onerror": function(text){
                this.notice(text, "error");
            }.bind(this),
            "onRequestFailure": function(xhr){
                this.notice(xhr.responseText, "error");
            }.bind(this)
        });
	},
    loadViewData: function(id, callback){
		this.actions.getStat(id, function(json){
			if (json){
				var data = json.data;
                var dataJson = JSON.decode(data.data);
                data.data = dataJson;

                if (!this.application){
                    this.actions.getApplication(data.application, function(json){
                        this.application = {"name": json.data.name, "id": json.data.id};
                        if (callback) callback(data);
                    }.bind(this));
                }else{
                    if (callback) callback(data);
                }
			}
		}.bind(this));
	},

    saveView: function(){
        this.view.save(function(){
            var name = this.view.data.name;
            this.setTitle(MWF.APPSTD.LP.title + "-"+name);
            this.options.desktopReload = true;
            this.options.id = this.view.data.id;
        }.bind(this));
    },
    saveDictionaryAs: function(){
        this.view.saveAs();
	},
    dictionaryExplode: function(){
        this.view.explode();
    },
    dictionaryImplode: function(){
        this.view.implode();
    }
	//recordStatus: function(){
	//	return {"id": this.options.id};
	//},

});
