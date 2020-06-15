MWF.xApplication.process.ProcessDesigner.widget = MWF.xApplication.process.ProcessDesigner.widget || {};
MWF.xApplication.process.ProcessDesigner.widget.ScriptText = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "maskNode": $(document.body),
        "height": null,
        "maxObj": null
    },
    initialize: function(node, code, app, options){
        this.setOptions(options);
        this.node = $(node);
        this.app = app;
        this.code = code;

        this.path = "../x_component_process_ProcessDesigner/widget/$ScriptText/";
        this.cssPath = "../x_component_process_ProcessDesigner/widget/$ScriptText/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.createEditor();
    },

    createEditor: function(){
        this.areaNode = new Element("div", {"styles": this.css.areaNode}).inject(this.node);
        if (this.options.height) this.areaNode.setStyle("height", ""+this.options.height+"px");

        this.titleNode = new Element("div", {"styles": this.css.titleNode}).inject(this.areaNode);

        this.referenceNode = new Element("div", {"styles": this.css.actionReferenceNode}).inject(this.titleNode);
        if (!this.code) this.referenceNode.setStyle("background", "url("+"../x_component_process_ProcessDesigner/widget/$ScriptText/"+this.options.style+"/icon/reference_empty.png) no-repeat center center")
        this.maxNode = new Element("div", {"styles": this.css.actionMaxNode}).inject(this.titleNode);

        this.returnNode = new Element("div", {"styles": this.css.actionReturnNode}).inject(this.titleNode);
        this.editorNode = new Element("div", {"styles": this.css.editorNode}).inject(this.areaNode);
        if (this.options.height){
            var height = this.options.height.toInt()-20;
            this.editorNode.setStyle("height", ""+height+"px");
        }

        this.inforNode = new Element("div", {"styles": this.css.inforNode, "text": this.app.lp.intoScript}).inject(this.editorNode);
        var _self = this;
        this.inforNode.addEvent("click", function(){
            //this.destroy();
            //_self.inforNode = null;
            _self.loadEditor();
        });
    },

    loadEditor: function(callback){
        if (this.inforNode){
            this.inforNode.destroy();
            this.inforNode = null;
        }
        MWF.require("MWF.widget.JavascriptEditor", function(){
            this.editor = new MWF.widget.JavascriptEditor(this.editorNode, {
                "runtime": "server",
                "option": {"value": this.code},
                "onSave": function(){
                    var value = this.editor.getValue();
                    this.fireEvent("change", [value]);
                    this.app.saveProcess();
                }.bind(this)
            });
            this.editor.load(function(){

                this.editor.addEditorEvent("blur", function(){
                    var value = this.editor.getValue();
                    this.fireEvent("change", [value]);
                }.bind(this));

                // this.editor.editor.on("blur", function(){
                //     var value = this.editor.editor.getValue();
                //     this.fireEvent("change", [value]);
                // }.bind(this));

                // this.createScriptReferenceMenu();
                //
                // this.editor.addEvent("reference", function(editor, e, e1){
                //     if (!this.scriptReferenceMenu){
                //         this.createScriptReferenceMenu(this.showReferenceMenu.bind(this));
                //     }else{
                //         this.showReferenceMenu();
                //     }
                // }.bind(this));

                if (callback) callback();

            }.bind(this));
        }.bind(this));
        this.maxNode.addEvent("click", function(){this.maxSize();}.bind(this));
        this.returnNode.addEvent("click", function(){this.returnSize();}.bind(this))
    },
    createScriptReferenceMenu: function(callback){
        MWF.require("MWF.widget.ScriptHelp", function(){
            this.scriptReferenceMenu = new MWF.widget.ScriptHelp(this.referenceNode, this.editor.editor, {
                "code": "code_background.json",
                "event": "click",
                "onPostLoad": function(){
                    if (callback) callback();
                }.bind(this)
            });
            this.scriptReferenceMenu.getEditor = function(){return this.editor.editor;}.bind(this)
        }.bind(this));
    },
    showReferenceMenu: function(){
        var pos = this.editor.getCursorPixelPosition();
        var e = {"page": {}};
        e.page.x = pos.left;
        e.page.y = pos.top;
        this.scriptReferenceMenu.menu.showIm(e);
    },
    maxSize: function() {
        if (!this.options.maxObj){
            this.areaNode.setStyles({
                "width": "100%",
                "height": "100%",
                "position": "absolute",
                "z-index": "50001",
                "top": "0px",
                "left": "0px"
            });
            this.areaNode.inject(this.app.content);
        }else{
            var size = this.options.maxObj.getSize();
            this.areaNode.inject(this.options.maxObj);
            this.areaNode.setStyles({
                "width": ""+size.x+"px",
                "height": ""+size.y+"px",
                "position": "absolute",
                "z-index": "50001"
            });
            this.areaNode.position({
                "relativeTo": this.options.maxObj,
                "position": "upperLeft",
                "edge": "upperLeft"
            });
        }


        this.resizeEditor();
        this.resizeEditorFun = this.resizeEditor.bind(this);
        this.app.addEvent("resize", this.resizeEditorFun);

        this.maxNode.setStyle("display", "none");
        this.returnNode.setStyle("display", "block");
    },
    resizeEditor: function(){
        var size = this.areaNode.getSize();
        var y = size.y-20;
        this.editorNode.setStyle("height", ""+y+"px");
        if (this.editor) this.editor.resize();
    },

    returnSize: function(){
        this.areaNode.setStyles(this.css.areaNode);
        this.areaNode.inject(this.node);
        if (this.options.height) this.areaNode.setStyle("height", ""+this.options.height+"px");

        this.resizeEditor();

        this.app.removeEvent("resize", this.resizeEditorFun);
        this.maxNode.setStyle("display", "block");
        this.returnNode.setStyle("display", "none");
    }

    //getValue()


});
