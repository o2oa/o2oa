MWF.xDesktop.requireApp("query.ViewDesigner", "", null, false);
MWF.APPDSTD = MWF.xApplication.query.StatDesigner;
MWF.APPDSTD.options = {
	"multitask": true,
	"executable": false
};
MWF.xDesktop.requireApp("query.StatDesigner", "Stat", null, false);

MWF.xApplication.query.StatDesigner.Main = new Class({
	Extends: MWF.xApplication.query.ViewDesigner.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "query.StatDesigner",
		"icon": "icon.png",
		"title": MWF.APPDSTD.LP.title,
		"appTitle": MWF.APPDSTD.LP.title,
		"id": "",
        "tooltip": {
            "unCategory": MWF.APPDSTD.LP.unCategory
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
            this.options.title = this.options.title + "-"+MWF.APPDSTD.LP.newStat;
        }
        if (!this.actions) this.actions = MWF.Actions.get("x_query_assemble_designer");

        this.lp = MWF.xApplication.query.StatDesigner.LP;

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

    loadViewListNodes: function(){
        this.viewListTitleNode = new Element("div", {
            "styles": this.css.viewListTitleNode,
            "text": MWF.APPDSTD.LP.stat
        }).inject(this.viewListNode);

        this.viewListResizeNode = new Element("div", {"styles": this.css.viewListResizeNode}).inject(this.viewListNode);
        this.viewListAreaSccrollNode = new Element("div", {"styles": this.css.viewListAreaSccrollNode}).inject(this.viewListNode);
        this.viewListAreaNode = new Element("div", {"styles": this.css.viewListAreaNode}).inject(this.viewListAreaSccrollNode);

        this.loadViewListResize();

        this.loadViewList();
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
        if (!view.isNewView){
            var openNew = true;
            if (openNew){
                var _self = this;
                var options = {
                    "appId": "query.StatDesigner"+view.id,
                    "onQueryLoad": function(){
                        this.actions = _self.actions;
                        this.category = _self;
                        this.options.id = view.id;
                        this.application = _self.application;
                        this.explorer = _self.explorer;
                    }
                };
                this.desktop.openApplication(e, "query.StatDesigner", options);
            }
        }
    },
	
	//loadView------------------------------------------
    loadView: function(){
		this.getViewData(this.options.id, function(vdata){
            this.setTitle(this.options.appTitle + "-"+vdata.name);
            this.taskitem.setText(this.options.appTitle + "-"+vdata.name);
            this.options.appTitle = this.options.appTitle + "-"+vdata.name;
            this.view = new MWF.xApplication.query.StatDesigner.Stat(this, vdata);
			this.view.load();
		}.bind(this));
	},

	loadNewViewData: function(callback){
        var url = "../x_component_query_StatDesigner/$Stat/stat.json";
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
                data.data = JSON.decode(data.data);

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
            this.setTitle(MWF.APPDSTD.LP.title + "-"+name);
            this.options.desktopReload = true;
            this.options.id = this.view.data.id;
        }.bind(this));
    },
    saveViewAs: function(){
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


MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.query.StatDesigner.Stat.NewNameForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "design",
        "width": 700,
        //"height": 300,
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : MWF.xApplication.query.StatDesigner.LP.copyStat
    },
    _createTableContent: function () {

        var html = "<table width='80%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin: 20px auto 0px auto; '>" +
            "<tr><td styles='formTableTitle' lable='selectQuery' width='25%'></td>" +
            "    <td styles='formTableValue' item='selectQuery' colspan='3' width='75%'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='view'></td>" +
            "    <td styles='formTableValue' item='view' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='name'></td>" +
            "    <td styles='formTableValue' item='name' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data || {}, {
                isEdited: true,
                style: "cms",
                hasColon: true,
                itemTemplate: {
                    selectQuery : { text: MWF.xApplication.query.StatDesigner.LP.application , type : "org",  orgType : "Query", defaultValue :  this.data.queryName,
                        orgWidgetOptions : {"canRemove" : false },
                        event : {
                            change : function(){ this.form.getItem("view").resetItemOptions( this.getViewIdList(), this.getViewNameList() ) }.bind(this)
                        }
                    },
                    view : {
                        text: MWF.xApplication.query.StatDesigner.LP.view , type : "select",
                        selectValue : function(){ return this.getViewIdList(); }.bind(this),
                        selectText : function(){ return this.getViewNameList(); }.bind(this)
                    },
                    name: {text: MWF.xApplication.query.StatDesigner.LP.name, notEmpty: true}
                }
            }, this.app);
            this.form.load();
        }.bind(this),null, true)

    },
    getViewIdList : function(){
        return this.getViews().idList;
    },
    getViewNameList : function(){
        return this.getViews().nameList;
    },
    getViews : function(){
        var id;
        var selectQuery = this.form.getItem("selectQuery").orgObject;
        if( selectQuery && selectQuery.length > 0 ){
            var queryData = selectQuery[0].data;
            id = queryData.id;
        }else{
            id = this.data.query;
            //data.query 和 data.queryName 还是传进来的值
        }
        var idList = [];
        var nameList = [];
        MWF.Actions.get("x_query_assemble_designer").listView( id, function(json){
            json.data.each( function(d){
                idList.push(d.id );
                nameList.push(d.name );
            })
        }.bind(this), null, false);
        return {
            idList : idList,
            nameList : nameList
        }
    },
    ok: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            var selectQuery = this.form.getItem("selectQuery").orgObject;
            if( selectQuery && selectQuery.length > 0 ){
                var queryData = selectQuery[0].data;
                data.query = queryData.id;
                data.queryName = queryData.name;
            }else{
                //data.query 和 data.queryName 还是传进来的值
            }
            this.fireEvent("save", [data , function(){
                this.close();
            }.bind(this)])
        }
    }
});
