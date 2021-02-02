MWF.xDesktop.requireApp("query.ViewDesigner", "", null, false);
MWF.APPDTBD = MWF.xApplication.query.TableDesigner;
MWF.APPDTBD.options = {
    "multitask": true,
    "executable": false
};
MWF.xDesktop.requireApp("query.TableDesigner", "Table", null, false);

MWF.xApplication.query.TableDesigner.Main = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "query.TableDesigner",
        "icon": "icon.png",
        "title": MWF.APPDTBD.LP.title,
        "appTitle": MWF.APPDTBD.LP.title,
        "id": "",
        "tooltip": {
            "unCategory": MWF.APPDTBD.LP.unCategory
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
            this.options.title = this.options.title + "-"+MWF.APPDTBD.LP.newTable;
        }
        if (!this.actions) this.actions = MWF.Actions.get("x_query_assemble_designer");

        this.lp = MWF.xApplication.query.TableDesigner.LP;

        this.addEvent("queryClose", function(e){
            if (this.explorer){
                this.explorer.reload();
            }
        }.bind(this));
    },

    loadViewListNodes: function(){
        this.viewListTitleNode = new Element("div", {
            "styles": this.css.viewListTitleNode,
            "text": MWF.APPDTBD.LP.table
        }).inject(this.viewListNode);

        this.viewListResizeNode = new Element("div", {"styles": this.css.viewListResizeNode}).inject(this.viewListNode);
        this.viewListAreaSccrollNode = new Element("div", {"styles": this.css.viewListAreaSccrollNode}).inject(this.viewListNode);
        this.viewListAreaNode = new Element("div", {"styles": this.css.viewListAreaNode}).inject(this.viewListAreaSccrollNode);

        this.loadViewListResize();

        this.loadViewList();
    },
    loadViewList: function(){
        debugger;
        this.actions.listTable(this.application.id, function (json) {
            json.data.each(function(table){
                this.createListViewItem(table);
            }.bind(this));
        }.bind(this), null, false);
    },

    //列示所有数据表列表
    createListViewItem: function(table, isNew){
        var _self = this;
        var listTableItem = new Element("div", {"styles": this.css.listViewItem}).inject(this.viewListAreaNode, (isNew) ? "top": "bottom");
        var listTableItemIcon = new Element("div", {"styles": this.css.listViewItemIcon}).inject(listTableItem);
        var listTableItemText = new Element("div", {"styles": this.css.listViewItemText, "text": (table.name) ? table.name+" ("+table.alias+")" : this.lp.newTable}).inject(listTableItem);

        listTableItem.store("table", table);
        listTableItem.addEvents({
            "dblclick": function(e){_self.loadTableByData(this, e);},
            "mouseover": function(){if (_self.currentListViewItem!=this) this.setStyles(_self.css.listViewItem_over);},
            "mouseout": function(){if (_self.currentListViewItem!=this) this.setStyles(_self.css.listViewItem);}
        });
    },
    //打开数据表
    loadTableByData: function(node, e){
        var table = node.retrieve("table");
        if (!table.isNewTable){
            var openNew = true;
            if (openNew){
                var _self = this;
                var options = {
                    "appId": "query.TableDesigner"+table.id,
                    "onQueryLoad": function(){
                        this.actions = _self.actions;
                        this.category = _self;
                        this.options.id = table.id;
                        this.application = _self.application;
                        this.explorer = _self.explorer;
                    }
                };
                this.desktop.openApplication(e, "query.TableDesigner", options);
            }
        }
    },

    //loadView------------------------------------------
    loadView: function(){
        this.getViewData(this.options.id, function(vdata){
            this.setTitle(this.options.appTitle + "-"+vdata.name);
            this.taskitem.setText(this.options.appTitle + "-"+vdata.name);
            this.options.appTitle = this.options.appTitle + "-"+vdata.name;
            this.table = new MWF.xApplication.query.TableDesigner.Table(this, vdata);
            this.table.load();
        }.bind(this));
    },

    loadNewViewData: function(callback){
        var url = "../x_component_query_TableDesigner/$Table/table.json";
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                this.actions.getUUID(function(id){
                    obj.id=id;
                    obj.isNewTable = true;
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
        this.actions.getTable(id, function(json){
            if (json){
                var data = json.data;
                data.draftData = JSON.decode(data.draftData);

                if (!this.application){
                    this.actions.getApplication(data.query, function(json){
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
            this.setTitle(MWF.APPDTBD.LP.title + "-"+name);
            this.options.desktopReload = true;
            this.options.id = this.view.data.id;
        }.bind(this));
    },
    statusBuild: function(){
        this.view.statusBuild();
    },
    statusDraft: function(){
        this.view.statusDraft();
    },
    buildAllView: function(){
        this.view.buildAllView();
    },
    tableExplode: function(){
        this.view.tableExplode();
    },
    tableImplode: function(){
        this.view.tableImplode();
    },
    tableExcelExplode: function(){
        this.view.tableExcelExplode();
    },
    tableExcelImplode: function(){
        this.view.tableExcelImplode();
    },
    tableClear: function(){
        this.view.tableClear();
    },
    tableHelp: function(){
        var content = new Element("div", {"styles": {"margin": "20px"}});
        content.set("html", this.lp.tableHelp);
        o2.DL.open({
            "title": "table help",
            "content": content,
            "width": 500,
            "height": 300,
            "buttonList": [
                {
                    "text": "ok",
                    "action": function(){this.close();}
                }
            ]
        });
    }
    // dictionaryExplode: function(){
    //     this.view.explode();
    // },
    // dictionaryImplode: function(){
    //     this.view.implode();
    // }
    //recordStatus: function(){
    //	return {"id": this.options.id};
    //},

});