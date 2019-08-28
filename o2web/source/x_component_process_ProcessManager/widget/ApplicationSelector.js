MWF.xDesktop.requireApp("process.ApplicationExplorer", "",null,false);
MWF.xApplication.process.ProcessManager.widget = MWF.xApplication.process.ProcessManager.widget || {};
MWF.xApplication.process.ProcessManager.widget.ApplicationSelector = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "multi": true,
        "maskNode": $(document.body)
    },
    initialize: function(app, options){
        this.setOptions(options);
        //this.node = $(node);
        this.app = app;
        this.applications = [];
        this.path = "/x_component_process_ProcessManager/widget/$ApplicationSelector/";
        this.cssPath = "/x_component_process_ProcessManager/widget/$ApplicationSelector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.selectedItems = [];
    },
    load: function(callback){
        this.callback = callback || null;
        this.loadWindow();
        this.loadContent();
    },

    loadWindow: function(){
        this.options.maskNode.mask({
            "destroyOnHide": true,
            "style": this.css.maskNode
        });
        this.windowNode = new Element("div", {"styles": this.css.containerNode});
        this.titleNode = new Element("div", {"styles": this.css.titleNode,}).inject(this.windowNode);

        this.titleActionNode = new Element("div", {"styles": this.css.titleActionNode}).inject(this.titleNode);
        this.titleTextNode = new Element("div", {"styles": this.css.titleTextNode,"text": "Select Application"}).inject(this.titleNode);
        this.titleActionNode.addEvent("click", function(){
            this.close();
        }.bind(this));

        this.windowContentScrollNode = new Element("div", {"styles": this.css.windowContentScrollNode}).inject(this.windowNode);
        this.windowContentNode = new Element("div", {"styles": this.css.windowContentNode}).inject(this.windowContentScrollNode);

        MWF.require("MWF.widget.DragScroll", function(){
            new MWF.widget.DragScroll(this.windowContentScrollNode);
        }.bind(this));
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.windowContentScrollNode);
        }.bind(this));

        this.actionNode = new Element("div", {"styles": this.css.actionNode}).inject(this.windowNode);
        this.loadAction();

        this.windowNode.inject(this.options.maskNode);
        this.windowNode.position({
            relativeTo: this.options.maskNode,
            position: "center",
            edge: "center"
        });

        var size = this.options.maskNode.getSize();
        var nodeSize = this.windowNode.getSize();
        this.windowNode.makeDraggable({
            "handle": this.titleNode,
            "limit": {
                "x": [0, size.x-nodeSize.x],
                "y": [0, size.y-nodeSize.y]
            }
        });
    },

    loadAction: function(){
        this.cancelActionNode = new Element("button", {
            "styles": this.css.cancelActionNode,
            "text": "取 消"
        }).inject(this.actionNode);

        this.okActionNode = new Element("button", {
            "styles": this.css.okActionNode,
            "text": "确　定"
        }).inject(this.actionNode);

        this.okActionNode.addEvent("click", function(){
            if (this.callback) this.callback(this.selectedItems);
            this.close();
        }.bind(this));
        this.cancelActionNode.addEvent("click", function(){this.close();}.bind(this));
    },

    loadContent: function(){
        //MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
            this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
            //if (!this.restActions) this.restActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();

            this.restActions.listApplication("", function(json){
                json.data.each(function(app){
                    var application = new MWF.xApplication.process.ProcessManager.widget.ApplicationSelector.Application(this.app, this, app);
                    application.load();
                    this.applications.push(application);
                    //this.windowContentNode
                    //var check = new Element("input", {"type": "checkbox", "value": app.id}).inject(content);
                    //var span = new Element("span", {"text": app.name}).inject(content);
                }.bind(this));
            }.bind(this));

        //}.bind(this));
    },
    //selected: function(script){
    //    this.scriptData = script.data;
    //    this.createScriptNode(script);
    //
    //    this.fireEvent("selected", [script.data]);
    //    //this.node.set("text", script.data.name);
    //    this.close();
    //},
    //createScriptNode: function(){
    //    this.node.empty();
    //
    //    var _self = this;
    //    this.scriptNode = new Element("div", {
    //        "styles": {"cursor": "pointer","color": "#0000FF"},
    //        "text": this.scriptData.name
    //    }).inject(this.node);
    //    this.scriptNode.addEvent("click", function(e){this.openScript(e);}.bind(this));
    //},
    //
    //openScript: function(e){
    //    var id = this.scriptData.id;
    //    var _self = this;
    //    var options = {
    //        "onQueryLoad": function(){
    //            this.actions = _self.app.actions;
    //            this.options.id = id;
    //            this.application = _self.app.application;
    //        }
    //    };
    //    this.app.desktop.openApplication(e, "process.ScriptDesigner", options);
    //},
    clearSelected: function(){
        this.applications.each(function(app){
            if (app.selected) app.unSelectedItem();
        }.bind(this));
    },
    close: function(){
        //if (this.callback) this.callback();
        this.selectedItems = [];
        this.windowNode.destroy();
        this.options.maskNode.unmask();
    }
});

MWF.xApplication.process.ProcessManager.widget.ApplicationSelector.Application = new Class({
    Extends: MWF.xApplication.process.ApplicationExplorer.Application,
    Implements: [Options, Events],
    initialize: function(app, selector, data, options){
        this.setOptions(options);
        this.app = app;
        this.selector = selector;
        this.container = selector.windowContentNode;
        this.css = selector.css;
        this.data = data;
    },

    load: function(){
        this.node = new Element("div", {
            "styles": this.css.applicationItemNode
        });

        //this.loadTopNode();

        this.loadIconNode();

        this.loadSelectIcon();
        //this.loadExportAction();

        this.loadTitleNode();

        this.loadNewNode();

        //this.loadInforNode();
        //this.loadProcessNode();
        //this.loadFormNode();

        //    this.loadDateNode();

        this.node.inject(this.container, this.options.where);
    },
    openApplication: function(){
        if (!this.selected){
            if (!this.selector.options.multi) this.selector.clearSelected();
            this.selectedItem();
        }else{
            //var bgcolor = this.topNode.retrieve("bgcolor");
            //this.topNode.setStyle("background-color", bgcolor);
            //this.selectIconNode.setStyles(this.css.applicationItemSelectIcon);
            //this.node.setStyles(this.css.applicationItemNode);
            //this.topNode.setStyles(this.css.applicationItemTopNode);
            //this.selector.selectedItems.erase(this.data);
            //this.selected = false;
            this.unSelectedItem();
        }
    },
    selectedItem: function(){
        this.selectIconNode.setStyles(this.css.applicationItemSelectIcon_selected);
        this.topNode.store("bgcolor", this.topNode.getStyle("background-color"));
        this.node.setStyles(this.css.applicationItemNode_select);
        this.topNode.setStyles(this.css.applicationItemTopNode_select);
        this.selector.selectedItems.push(this.data);
        this.selected = true;
    },
    unSelectedItem: function(){
        var bgcolor = this.topNode.retrieve("bgcolor");
        this.topNode.setStyle("background-color", bgcolor);
        this.selectIconNode.setStyles(this.css.applicationItemSelectIcon);
        this.node.setStyles(this.css.applicationItemNode);
        this.topNode.setStyles(this.css.applicationItemTopNode);
        this.selector.selectedItems.erase(this.data);
        this.selected = false;
    },
    loadSelectIcon: function(){
        //this.topNode.setStyle("background-color", "#666");
        this.selectIconNode = new Element("div", {
            "styles": this.css.applicationItemSelectIcon
        }).inject(this.topNode);

        //this.topNode.addEvents({
        //    "mouseover": function(){if (!this.readyDelete) this.delAdctionNode.fade("in"); }.bind(this),
        //    "mouseout": function(){if (!this.readyDelete) this.delAdctionNode.fade("out"); }.bind(this)
        //});
        this.selectIconNode.addEvent("click", function(e){
            this.checkDeleteApplication(e);
            e.stopPropagation();
        }.bind(this));
    },

});