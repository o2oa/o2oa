MWF.xDesktop.requireApp("query.ViewDesigner", "", null, false);
MWF.APPDIPD = MWF.xApplication.query.ImporterDesigner;
MWF.APPDIPD.options = {
    "multitask": true,
    "executable": false
};
MWF.xDesktop.requireApp("query.ImporterDesigner", "Importer", null, false);

MWF.xApplication.query.ImporterDesigner.Main = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "query.ImporterDesigner",
        "icon": "icon.png",
        "title": MWF.APPDIPD.LP.title,
        "appTitle": MWF.APPDIPD.LP.title,
        "id": "",
        "tooltip": {
            "unCategory": MWF.APPDIPD.LP.unCategory
        },
        "actions": null,
        "category": null
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
            this.options.title = this.options.title + "-"+MWF.APPDIPD.LP.newImporter;
        }
        if (!this.actions) this.actions = MWF.Actions.get("x_query_assemble_designer");

        this.lp = MWF.xApplication.query.ImporterDesigner.LP;

        this.addEvent("queryClose", function(e){
            if (this.explorer){
                this.explorer.reload();
            }
        }.bind(this));
    },

    loadViewListNodes: function(){
        this.viewListTitleNode = new Element("div", {
            "styles": this.css.viewListTitleNode,
            "text": MWF.APPDIPD.LP.importer
        }).inject(this.viewListNode);

        this.viewListResizeNode = new Element("div", {"styles": this.css.viewListResizeNode}).inject(this.viewListNode);
        this.viewListAreaSccrollNode = new Element("div", {"styles": this.css.viewListAreaSccrollNode}).inject(this.viewListNode);
        this.viewListAreaNode = new Element("div", {"styles": this.css.viewListAreaNode}).inject(this.viewListAreaSccrollNode);

        this.loadViewListResize();

        this.loadViewList();
    },
    loadViewList: function(){
        debugger;
        this.actions.listImportModel(this.application.id, function (json) {
            json.data.each(function(importer){
                this.createListViewItem(importer);
            }.bind(this));
        }.bind(this), null, false);
    },

    //列示所有数据表列表
    createListViewItem: function(importer, isNew){
        debugger;
        var _self = this;
        var listImporterItem = new Element("div", {"styles": this.css.listViewItem}).inject(this.viewListAreaNode, (isNew) ? "top": "bottom");
        var listImporterItemIcon = new Element("div", {"styles": this.css.listViewItemIcon}).inject(listImporterItem);
        var listImporterItemText = new Element("div", {"styles": this.css.listViewItemText, "text": (importer.name) ? importer.name+" ("+importer.alias+")" : this.lp.newImporter}).inject(listImporterItem);

        listImporterItem.store("importer", importer);
        listImporterItem.addEvents({
            "dblclick": function(e){_self.loadImporterByData(this, e);},
            "mouseover": function(){if (_self.currentListViewItem!=this) this.setStyles(_self.css.listViewItem_over);},
            "mouseout": function(){if (_self.currentListViewItem!=this) this.setStyles(_self.css.listViewItem);}
        });
    },
    //打开数据表
    loadImporterByData: function(node, e){
        var importer = node.retrieve("importer");
        if (!importer.isNewImportModel){
            var openNew = true;
            if (openNew){
                var _self = this;
                var options = {
                    "appId": "query.ImporterDesigner"+importer.id,
                    "onQueryLoad": function(){
                        this.actions = _self.actions;
                        this.category = _self;
                        this.options.id = importer.id;
                        this.application = _self.application;
                        this.explorer = _self.explorer;
                    }
                };
                this.desktop.openApplication(e, "query.ImporterDesigner", options);
            }
        }
    },

    //loadView------------------------------------------
    loadView: function(){
        this.getViewData(this.options.id, function(vdata){
            this.setTitle(this.options.appTitle + "-"+vdata.name);
            this.taskitem.setText(this.options.appTitle + "-"+vdata.name);
            this.options.appTitle = this.options.appTitle + "-"+vdata.name;
            this.importer = new MWF.xApplication.query.ImporterDesigner.Importer(this, vdata);
            this.view = this.importer;
            this.importer.load();
        }.bind(this));
    },

    loadNewViewData: function(callback){
        var url = "../x_component_query_ImporterDesigner/$Importer/importer.json";
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                this.actions.getUUID(function(id){
                    obj.id=id;
                    obj.isNewImportModel = true;
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
        this.actions.getImportModel(id, function(json){
            if (json){
                var data = json.data;
                // data.draftData = JSON.decode(data.draftData);

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
            this.setTitle(MWF.APPDIPD.LP.title + "-"+name);
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
    }

});

MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.query.ImporterDesigner.Importer.NewNameForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "design",
        "width": 700,
        //"height": 300,
        "height": "260",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : MWF.xApplication.query.ImporterDesigner.LP.newImporter
    },
    _createTableContent: function () {

        var html = "<table width='80%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin: 20px auto 0px auto; '>" +
            "<tr><td styles='formTableTitle' lable='selectQuery' width='25%'></td>" +
            "    <td styles='formTableValue' item='selectQuery' colspan='3' width='75%'></td></tr>" +
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
                    selectQuery : { text: MWF.xApplication.query.ImporterDesigner.LP.application , type : "org",  orgType : "Query", defaultValue :  this.data.queryName, orgWidgetOptions : {
                            "canRemove" : false
                        }},
                    name: {text: MWF.xApplication.query.ImporterDesigner.LP.name, notEmpty: true}
                }
            }, this.app);
            this.form.load();
        }.bind(this),null, true)

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