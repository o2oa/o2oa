MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.widget.JsonTemplate", null, false);

MWF.xDesktop.requireApp("query.ViewDesigner", "Property", null, false);
MWF.xApplication.query.StatementStatDesigner.Property = MWF.SSDProperty = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.Property,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (module, propertyNode, designer, options) {
        this.setOptions(options);
        this.module = module;
        this.view = module.stat || module.view;
        this.data = module.json;
        debugger;
        this.data.vid = this.view.json.id;
        this.data.vtype = this.view.json.type;
        this.data.pid = this.view.json.id + this.data.id;
        this.htmlPath = this.options.path;

        if (this.module) {
            this.data.mid = this.module.json.id;
        }

        this.maplists = {};

        this.designer = designer;

        this.propertyNode = propertyNode;
    },
    show: function () {
        if (!this.propertyContent) {
            this.getHtmlString(function () {
                if (this.htmlString) {
                    this.htmlString = o2.bindJson(this.htmlString, {"lp": MWF.xApplication.query.StatementStatDesigner.LP.propertyTemplate});
                    this.JsonTemplate = new MWF.widget.JsonTemplate(this.data, this.htmlString);
                    this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.propertyNode);
                    //var htmlStr = this.JsonTemplate.load();
                    this.propertyContent.set("html", this.JsonTemplate.load());

                    this.setEditNodeEvent();
                    this.setEditNodeStyles(this.propertyContent);
                    this.loadPropertyTab();
                    this.loadPersonInput();
                    this.loadPersonSelectInput();
                    this.loadViewFilter();
                    this.loadScriptArea();

                    // this.loadColumnExportEditor();

                    this.loadJSONArea();

                    this.loadEventsEditor();


                    this.loadStylesList();
                    this.loadMaplist();
                    this.loadDataPathSelect();

                    this.loadArrayList();
                }
            }.bind(this));
        } else {
            this.propertyContent.setStyle("display", "block");
        }
    },
    loadDataPathSelect : function(){
        var nodes = this.propertyContent.getElements(".MWFDataPathSelect");
        nodes.each( function (select) {
            select.empty();
            var option = new Element("option", {"text": "none", "value" : ""}).inject(select);
            this.module.getColumnDataPath().each(function(model){
                var option = new Element("option", {
                    "text": model,
                    "value": model,
                    "selected": (this.data[name]==model)
                }).inject(select);
            }.bind(this));
        }.bind(this))
    },
    loadViewFilter: function () {
        var nodes = this.propertyContent.getElements(".MWFViewFilter");
        var parameterData = this.view.data.data.parameterList;
        var customData = this.view.data.data.customFilterList;
        nodes.each(function (node) {
            MWF.xDesktop.requireApp("query.StatementDesigner", "widget.ViewFilter", function () {
                var _slef = this;
                this.viewFilter = new MWF.xApplication.query.StatementDesigner.widget.ViewFilter(node, this.view.designer, {
                    "parameterData": parameterData,
                    "customData": customData
                }, {
                    "onChange": function (ids) {
                        var data = this.getData();
                        _slef.changeJsonDate(["data", "parameterList"], data.parameterData);
                        _slef.changeJsonDate(["data", "customFilterList"], data.customData);
                    }
                });
            }.bind(this));
        }.bind(this));
    },
    loadArrayList: function(){
        var arrays = this.propertyContent.getElements(".MWFArraylist");
        arrays.each(function(node){
            var title = node.get("title");
            var name = node.get("name");

            var names = name.split(".");
            var arr = this.data;
            for (var idx = 0; idx<names.length; idx++){
                if (!arr[names[idx]]){
                    arr = null;
                    break;
                }else{
                    arr = arr[names[idx]];
                }
            }
            //var arr = this.data[name];
            if (!arr) arr = [];
            MWF.require("MWF.widget.Arraylist", function(){
                var arraylist = new MWF.widget.Arraylist(node, {
                    "title": title,
                    "onChange": function(){
                        this.setValue(name, arraylist.toArray(), node);

                        //this.data[name] = arraylist.toArray();
                    }.bind(this)
                });
                arraylist.load(arr);
            }.bind(this));
            node.addEvent("keydown", function(e){e.stopPropagation();});
        }.bind(this));
    },
    // loadActionArea: function () {
    //     MWF.xApplication.process = MWF.xApplication.process || {};
    //     MWF.APPFD = MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
    //     MWF.xDesktop.requireApp("process.FormDesigner", "lp." + o2.language, null, false);
    //
    //     var multiActionArea = this.propertyContent.getElements(".MWFMultiActionArea");
    //     multiActionArea.each(function(node){
    //         debugger;
    //         var name = node.get("name");
    //         var actionContent = this.data[name];
    //         MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function(){
    //             var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
    //                 "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
    //                 "systemToolsAddress": "../x_component_query_StatementDesigner/$Statement/toolbars.json",
    //                 "isSystemTool" : true,
    //                 "noEditShow": true,
    //                 "noReadShow": true,
    //                 "onChange": function(){
    //                     this.data[name] = actionEditor.data;
    //                     this.changeData(name);
    //                 }.bind(this)
    //             });
    //             actionEditor.load(actionContent);
    //         }.bind(this));
    //     }.bind(this));
    //
    //     var actionAreas = this.propertyContent.getElements(".MWFActionArea");
    //     actionAreas.each(function (node) {
    //         var name = node.get("name");
    //         var actionContent = this.data[name];
    //         MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function () {
    //
    //             // debugger;
    //             // var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, {
    //             //     "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
    //             //     "noCreate": true,
    //             //     "noDelete": true,
    //             //     "noCode": true,
    //             //     "onChange": function(){
    //             //         this.data[name] = actionEditor.data;
    //             //     }.bind(this)
    //             // });
    //             // actionEditor.load(this.module.defaultToolBarsData);
    //
    //             var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
    //                 "maxObj": this.propertyNode.parentElement.parentElement,
    //                 "noEditShow": true,
    //                 "noReadShow": true,
    //                 "onChange": function () {
    //                     this.data[name] = actionEditor.data;
    //                     this.changeData(name);
    //                 }.bind(this)
    //             });
    //             actionEditor.load(actionContent);
    //         }.bind(this));
    //
    //     }.bind(this));
    //
    //     var actionAreas = this.propertyContent.getElements(".MWFDefaultActionArea");
    //     actionAreas.each(function (node) {
    //         var name = node.get("name");
    //         var actionContent = this.data[name] || this.module.defaultToolBarsData;
    //         MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function () {
    //
    //             var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
    //                 "maxObj": this.propertyNode.parentElement.parentElement,
    //                 "isSystemTool": true,
    //                 "systemToolsAddress": "../x_component_query_StatementDesigner/$Statement/toolbars.json",
    //                 "noCreate": true,
    //                 "noDelete": false,
    //                 "noCode": true,
    //                 "noReadShow": true,
    //                 "noEditShow": true,
    //                 "onChange": function () {
    //                     this.data[name] = actionEditor.data;
    //                     this.changeData(name);
    //                 }.bind(this)
    //             });
    //             actionEditor.load(actionContent);
    //
    //             // var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, {
    //             //     "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
    //             //     "onChange": function(){
    //             //         this.data[name] = actionEditor.data;
    //             //     }.bind(this)
    //             // });
    //             // actionEditor.load(actionContent);
    //         }.bind(this));
    //
    //     }.bind(this));
    //
    // }
});