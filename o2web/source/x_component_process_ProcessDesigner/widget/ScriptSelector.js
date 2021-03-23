MWF.xApplication.process.ProcessDesigner.widget = MWF.xApplication.process.ProcessDesigner.widget || {};
MWF.xApplication.process.ProcessDesigner.widget.ScriptSelector = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "maskNode": $(document.body)
    },
    initialize: function(node, script, app, options){
        this.setOptions(options);
        this.node = $(node);
        this.app = app;

        this.script = script;
        this.path = "../x_component_process_ProcessDesigner/widget/$ScriptSelector/";
        this.cssPath = "../x_component_process_ProcessDesigner/widget/$ScriptSelector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.selNode = new Element("div", {"styles": this.css.selScriptNode}).inject(this.node, "before");
        this.delNode = new Element("div", {"styles": this.css.delScriptNode}).inject(this.node, "before");
        //this.selNode = new Element("div", {"styles": this.css.selScriptNode, "text": "选择"}).inject(this.node, "before");

        this.node.setStyles(this.css.contentNode);
        //this.node.set("text", script||"");

        this.loadValue(script);
        //this.createEditor();

        //this.addNode.addEvent("click", function(e){
        //    this.addScript();
        //}.bind(this));

        this.selNode.addEvent("click", function(e){
            this.editScript();
        }.bind(this));

        this.delNode.addEvent("click", function(e){
            this.delScript();
        }.bind(this));
    },
    loadValue: function(script){
        if (script && script.toString()){
            this.app.actions.getScriptByName(script, this.app.application.id, function(json){
                this.scriptData = json.data;
                this.createScriptNode(script);
            }.bind(this));
        }
    },

    //createEditor: function(){
    //    this.editorNode = new Element("div", {"styles": this.css.editorNode}).inject(this.node, "after");
    //    MWF.require("MWF.widget.JavascriptEditor", function(){
    //        this.editor = new MWF.widget.JavascriptEditor(this.editorNode);
    //        this.editor.load();
    //    }.bind(this));
    //},

    delScript: function(){
        this.fireEvent("delete");
    },
    editScript: function(){
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
        this.titleTextNode = new Element("div", {"styles": this.css.titleTextNode,"text": "Select Script"}).inject(this.titleNode);
        this.titleActionNode.addEvent("click", function(){
            this.close();
        }.bind(this));

        this.windowContentNode = new Element("div", {"styles": this.css.windowContentNode}).inject(this.windowNode);

        //this.actionNode = new Element("div", {"styles": this.css.actionNode}).inject(this.windowNode);
        //this.loadAction();

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
        this.okActionNode = new Element("button", {
            "styles": this.css.okActionNode,
            "text": this.app.lp.selectorButton.ok
        }).inject(this.actionNode);
        this.cancelActionNode = new Element("button", {
            "styles": this.css.cancelActionNode,
            "text": this.app.lp.selectorButton.cancel
        }).inject(this.actionNode);
        this.okActionNode.addEvent("click", function(){
            this.fireEvent("complete", [this.selectedItems]);
            this.close();
        }.bind(this));
        this.cancelActionNode.addEvent("click", function(){this.close();}.bind(this));
    },

    loadContent: function(){
        //MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
            if (!this.restActions) this.restActions = MWF.Actions.get("x_organization_assemble_control");
            //if (!this.restActions) this.restActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
            this.scriptConfigurator = new MWF.xApplication.process.ProcessDesigner.widget.ScriptSelector.ScriptExplorer(this.windowContentNode, this.app.actions);
            this.scriptConfigurator.app = this.app;
            this.scriptConfigurator.window = this;
            this.scriptConfigurator.load();
        //}.bind(this));

        //this.scriptListNode = new Element("div", {"styles": this.css.scriptListNode}).inject(this.windowContentNode);
        //this.scriptContentNode = new Element("div", {"styles": this.css.scriptContentNode}).inject(this.windowContentNode);
    },
    selected: function(script){
        this.scriptData = script.data;
        this.createScriptNode(script);

        this.fireEvent("selected", [script.data]);
        //this.node.set("text", script.data.name);
        this.close();
    },
    createScriptNode: function(){
        this.node.empty();

        var _self = this;
        this.scriptNode = new Element("div", {
            "styles": {"cursor": "pointer","color": "#0000FF"},
            "text": this.scriptData.name
        }).inject(this.node);
        this.scriptNode.addEvent("click", function(e){this.openScript(e);}.bind(this));
    },

    openScript: function(e){
        var id = this.scriptData.id;
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.app.actions;
                this.options.id = id;
                this.application = _self.app.application;
            }
        };
        this.app.desktop.openApplication(e, "process.ScriptDesigner", options);
    },
    close: function(){
        this.windowNode.destroy();
        this.options.maskNode.unmask();
    },
});

MWF.xDesktop.requireApp("process.ProcessManager", "ScriptExplorer", null, false);
MWF.xApplication.process.ProcessDesigner.widget.ScriptSelector.ScriptExplorer = new Class({
    Extends: MWF.xApplication.process.ProcessManager.ScriptExplorer,
    loadToolbar: function(){
        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode});
        this.toolbarNode.setStyle("height", "40px");
        this.createCreateElementNode();
        this.createElementNode.setStyles({"height": "40px", "width": "40px"});
        this.toolbarNode.inject(this.node);
    },

    loadContentNode: function(){
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.elementContentListNode = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);

        this.setContentSize();
        //this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));
    },
    _getItemObject: function(item){
        return new MWF.xApplication.process.ProcessDesigner.widget.ScriptSelector.ScriptExplorer.Script(this, item)
    },
});
MWF.xApplication.process.ProcessDesigner.widget.ScriptSelector.ScriptExplorer.Script = new Class({
    Extends: MWF.xApplication.process.ProcessManager.ScriptExplorer.Script,
    _open: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.application = _self.explorer.app.application;
                this.explorer = _self.explorer
            }
        };
        this.explorer.app.desktop.openApplication(e, "process.ScriptDesigner", options);
    },
    _getLnkPar: function(){
        return {
            "icon": this.explorer.path+this.explorer.options.style+"/scriptIcon/lnk.png",
            "title": this.data.name,
            "par": "process.ScriptDesigner#{\"id\": \""+this.data.id+"\", \"applicationId\": \""+this.explorer.app.application.id+"\"}"
        };
    },
    _customNodes: function(){
        if (!this.data.validated){
            new Element("div", {"styles": this.explorer.css.itemErrorNode}).inject(this.node);
            this.node.setStyle("background-color", "#f9e8e8");
        }
        this.node.setStyle("cursor", "pointer");
        this.nodeColor = this.node.getStyle("background-color");
        var _self = this;
        this.node.removeEvents("mouseover");
        this.node.removeEvents("mouseout");
        this.node.addEvents({
            "mouseover": function(){this.setStyle("background-color", "#dcdcdc");},
            "mouseout": function(){this.setStyle("background-color", _self.nodeColor);},
            "click": function(){this.selected();}.bind(this)
        });


        var inforNode = new Element("div", {"styles": {
            "font-size": "14px"
        }, "text": this.explorer.app.lp.selectScript+this.data.name+" ("+this.data.alias+") "});
        new mBox.Tooltip({
            theme: 'BlackGradient',
            content: inforNode,
            offset: {x: 0, y:0},
            setStyles: {content: {padding: 10, lineHeight: 20}},
            attach: this.node,
            transition: 'flyin'
        });
    },
    selected: function(){
        this.explorer.window.selected(this);
    }

});