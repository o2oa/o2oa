MWF.widget = MWF.widget || {};
MWF.require("MWF.widget.ScriptEditor", null, false);
MWF.require("MWF.widget.JavascriptEditor", null, false);
MWF.widget.ScriptArea = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "title": "ScriptArea",
        "style": "default",
        "helpStyle" : "default",
        "maxObj": document.body
    },
    initialize: function(node, options){
        this.setOptions(options);

        this.node = $(node);
        this.container = new Element("div");

        this.path = MWF.defaultPath+"/widget/$ScriptArea/";
        this.cssPath = MWF.defaultPath+"/widget/$ScriptArea/"+this.options.style+"/css.wcss";
        this._loadCss();
    },
    load: function(content){
        if (this.fireEvent("queryLoad")){
            this.container.set("styles", this.css.container);
            this.container.inject(this.node);

            this.createTitleNode();

            this.createContent(content);

            this.fireEvent("postLoad");
        }
    },
    createTitleNode: function(){
        this.titleNode = new Element("div", {
            "styles": this.css.titleNode,
            "events": {
                "dblclick": this.toggleSize.bind(this)
            }
        }).inject(this.container);

        this.titleActionNode = new Element("div", {
            "styles": this.css.titleActionNode,
            "events": {
                "click": this.toggleSize.bind(this)
            }
        }).inject(this.titleNode);
        this.referenceNode = new Element("div", {
            "styles": this.css.referenceNode
        }).inject(this.titleNode);

        this.titleTextNode = new Element("div", {
            "styles": this.css.titleTextNode,
            "text": this.options.title
        }).inject(this.titleNode);
    },
    toggleSize: function(e){
        var status = this.titleActionNode.retrieve("status", "max");
        if (status=="max"){
            this.maxSize();
        }else{
            this.returnSize();
        }
    },
    maxSize: function(){
        var obj = this.options.maxObj;
        var coordinates = obj.getCoordinates(obj.getOffsetParent());

        this.container.store("size", {"height": this.container.getStyle("height"), "width": this.container.getStyle("width")});

        this.jsEditor.showLineNumbers();
        this.jsEditor.max();

        this.container.setStyles({
            "position": "absolute",
            "top": coordinates.top,
            "left": coordinates.left,
            "width": coordinates.width,
            "height": coordinates.height-2,
            "z-index": 20001
        });
        this.resizeContentNodeSize();
        this.titleActionNode.setStyle("background", "url("+this.path+this.options.style+"/icon/return.png) center center no-repeat");
        this.titleActionNode.store("status", "return");
    },

    returnSize: function(){
        var size = this.container.retrieve("size");

        this.editor.setOption("lineNumbers", false);
        this.container.setStyles({
            "position": "static",
            "top": 0,
            "left": 0,
            "width": "auto",
            "height": size.height
        });

        this.resizeContentNodeSize();
        this.titleActionNode.setStyle("background", "url("+this.path+this.options.style+"/icon/max.png) center center no-repeat");
        this.titleActionNode.store("status", "max");
    },
    resizeContentNodeSize: function(){
        var titleSize = this.titleNode.getSize();
        var size = this.container.getSize();
        var h = this.container.getStyle("height").toInt();
        var th = this.titleNode.getStyle("height").toInt();
        var height = (size.y || h)-(titleSize.y || th)-2-6;
        this.contentNode.setStyle("height", ""+height+"px");
        if (this.editor) this.editor.resize();
    },
    toJson: function(){
        return (this.editor) ? {"code": this.editor.getValue(), "html": this.editor.getValue()} : this.contentCode;
    },
    createContent: function(content){
        this.contentNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.container);

        this.resizeContentNodeSize();

        var inforNode = new Element("div", {"styles": this.css.inforNode, "text": "点击此处，编写脚本代码"}).inject(this.contentNode);

        if (!content || !content.code){
            this.referenceNode.setStyle("background", "url("+MWF.defaultPath+"/widget/$ScriptArea/"+this.options.style+"/icon/reference_empty.png) center center no-repeat")
        }

        this.contentCode = content;
        var _self = this;
        inforNode.addEvent("click", function(){
            this.destroy();
            _self.loadEditor(content);
        });
    },
    loadEditor: function(content){
        var value=(content) ? content.code : "";
        value = (value) ? value : "";
        this.jsEditor = new MWF.widget.JavascriptEditor(this.contentNode,{
            "option": {
                "value": value,
                "lineNumbers": false
            },
            "onPostLoad": function(){
                this.editor = this.jsEditor.editor;
                this.editor.on("change", function() {
                    this.fireEvent("change");
                }.bind(this));
                debugger;
                this.editor.resize();
                this.fireEvent("postLoad");
            }.bind(this),
            "onSave": function(){
                this.fireEvent("change");
                this.fireEvent("save");
            }.bind(this)
        });
        this.jsEditor.load();
        this.createScriptReferenceMenu();


        this.jsEditor.addEvent("reference", function(editor, e, e1){
            if (!this.scriptReferenceMenu){
                this.createScriptReferenceMenu(this.showReferenceMenu.bind(this));
            }else{
                this.showReferenceMenu();
            }
        }.bind(this));
        this.referenceNode.setStyle("background", "url("+MWF.defaultPath+"/widget/$ScriptArea/"+this.options.style+"/icon/reference.png) center center no-repeat")
    },

    createScriptReferenceMenu: function(callback){
        MWF.require("MWF.widget.ScriptHelp", function(){
            this.scriptReferenceMenu = new MWF.widget.ScriptHelp(this.referenceNode, this.jsEditor.editor, {
                "style" : this.options.helpStyle,
                "event": "click",
                "onPostLoad": function(){
                    if (callback) callback();
                }.bind(this)
            });
            this.scriptReferenceMenu.getEditor = function(){return this.jsEditor.editor;}.bind(this)
        }.bind(this));
    },
    showReferenceMenu: function(){
        var pos = this.jsEditor.getCursorPixelPosition();
        var e = {"page": {}};
        e.page.x = pos.left;
        e.page.y = pos.top;
        this.scriptReferenceMenu.menu.showIm(e);
    },
    focus: function(){
        if (this.editor) this.editor.focus();
    }
});




