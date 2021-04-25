MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.widget.JsonTemplate", null, false);

MWF.xDesktop.requireApp("query.ViewDesigner", "Property", null, false);
MWF.xApplication.query.StatementDesigner.Property = MWF.SDProperty = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.Property,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    show: function () {
        if (!this.propertyContent) {
            this.getHtmlString(function () {
                if (this.htmlString) {
                    this.htmlString = o2.bindJson(this.htmlString, {"lp": MWF.xApplication.query.StatementDesigner.LP.propertyTemplate});
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

                    this.loadColumnExportEditor();

                    this.loadJSONArea();

                    this.loadEventsEditor();
                    this.loadViewStylesArea();
                    this.loadPagingStylesArea();
                    this.loadActionStylesArea();
                    this.loadActionArea();
                    this.loadStylesList();
                    this.loadMaplist();
                    this.loadDataPathSelect();
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
    loadActionArea: function () {
        debugger;
        MWF.xApplication.process = MWF.xApplication.process || {};
        MWF.APPFD = MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
        MWF.xDesktop.requireApp("process.FormDesigner", "lp." + o2.language, null, false);

        var multiActionArea = this.propertyContent.getElements(".MWFMultiActionArea");
        multiActionArea.each(function(node){
            debugger;
            var name = node.get("name");
            var actionContent = this.data[name];
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function(){
                var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
                    "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "systemToolsAddress": "../x_component_query_StatementDesigner/$Statement/toolbars.json",
                    "isSystemTool" : false,
                    "noEditShow": true,
                    "noReadShow": true,
                    "onChange": function(){
                        this.data[name] = actionEditor.data;
                        this.changeData(name);
                    }.bind(this)
                });
                actionEditor.load(actionContent);
            }.bind(this));
        }.bind(this));

        var actionAreas = this.propertyContent.getElements(".MWFActionArea");
        actionAreas.each(function (node) {
            var name = node.get("name");
            var actionContent = this.data[name];
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function () {

                // debugger;
                // var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, {
                //     "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                //     "noCreate": true,
                //     "noDelete": true,
                //     "noCode": true,
                //     "onChange": function(){
                //         this.data[name] = actionEditor.data;
                //     }.bind(this)
                // });
                // actionEditor.load(this.module.defaultToolBarsData);

                var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
                    "maxObj": this.propertyNode.parentElement.parentElement,
                    "noEditShow": true,
                    "noReadShow": true,
                    "onChange": function () {
                        this.data[name] = actionEditor.data;
                        this.changeData(name);
                    }.bind(this)
                });
                actionEditor.load(actionContent);
            }.bind(this));

        }.bind(this));

        var actionAreas = this.propertyContent.getElements(".MWFDefaultActionArea");
        actionAreas.each(function (node) {
            var name = node.get("name");
            var actionContent = this.data[name] || this.module.defaultToolBarsData;
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function () {

                var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
                    "maxObj": this.propertyNode.parentElement.parentElement,
                    "isSystemTool": true,
                    "systemToolsAddress": "../x_component_query_StatementDesigner/$Statement/toolbars.json",
                    "noCreate": true,
                    "noDelete": false,
                    "noCode": true,
                    "noReadShow": true,
                    "noEditShow": true,
                    "onChange": function () {
                        this.data[name] = actionEditor.data;
                        this.changeData(name);
                    }.bind(this)
                });
                actionEditor.load(actionContent);

                // var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, {
                //     "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                //     "onChange": function(){
                //         this.data[name] = actionEditor.data;
                //     }.bind(this)
                // });
                // actionEditor.load(actionContent);
            }.bind(this));

        }.bind(this));

    }
});