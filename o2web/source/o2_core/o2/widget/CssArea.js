o2.widget = o2.widget || {};
o2.require("o2.widget.ScriptArea", null, false);
//o2.require("o2.widget.CssEditor", null, false);

o2.widget.CssArea = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.ScriptArea,
    options: {
        "title": "CssArea",
        "style": "default",
        "helpStyle" : "default",
        "maxObj": document.body,
        "isload": false,
        "key": "code"
    },
    initialize: function(node, options){
        this.setOptions(options);

        this.node = $(node);
        this.container = new Element("div");

        this.path = o2.session.path+"/widget/$CssArea/";
        this.cssPath = o2.session.path+"/widget/$CssArea/"+this.options.style+"/css.wcss";
        this._loadCss();
    },
    toJson: function(){
        return (this.editor) ? {"code": this.editor.getValue(), "html": this.editor.getValue()} : this.contentCode;
    },
    createContent: function(content){
        this.contentNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.container);

        this.resizeContentNodeSize();

        this.contentCode = content;

        if (!content || !content.code){
            this.referenceNode.setStyle("background", "url("+o2.session.path+"/widget/$ScriptArea/"+this.options.style+"/icon/reference_empty.png) center center no-repeat")
        }

        if (this.options.isload){
            this.loadEditor(content);
        }else{
            var inforNode = new Element("div", {"styles": this.css.inforNode, "text": o2.LP.widget.clickToEditCss }).inject(this.contentNode);
            var _self = this;
            inforNode.addEvent("click", function(){
                this.destroy();
                _self.loadEditor(content);
            });
        }
    },
    loadEditor: function(content){
        var value=(content) ? content.code : "";
        value = (value) ? value : "";
        this.jsEditor = new o2.widget.JavascriptEditor(this.contentNode,{
            "option": {
                "mode": "css",
                "value": value,
                "lineNumbers": false
            },
            "onPostLoad": function(){
                this.editor = this.jsEditor.editor;
                this.editor.id = "2";

                this.jsEditor.addEditorEvent("change", function() {
                    this.fireEvent("change");
                }.bind(this));
                this.jsEditor.addEditorEvent("blur", function() {
                    this.fireEvent("blur");
                }.bind(this));

                // this.editor.on("change", function() {
                //     this.fireEvent("change");
                // }.bind(this));
                // this.editor.on("blur", function() {
                //     this.fireEvent("blur");
                // }.bind(this));

                this.jsEditor.resize();
                this.fireEvent("postLoad");
            }.bind(this),
            "onSave": function(){
                this.fireEvent("change");
                this.fireEvent("save");
            }.bind(this)
        });
        this.jsEditor.load();
        //this.bind(content);

        this.createScriptReferenceMenu();

        this.jsEditor.addEvent("reference", function(editor, e, e1){
            if (!this.scriptReferenceMenu){
                this.createScriptReferenceMenu(this.showReferenceMenu.bind(this));
            }else{
                this.showReferenceMenu();
            }
        }.bind(this));
        this.referenceNode.setStyle("background", "url("+o2.session.path+"/widget/$ScriptArea/"+this.options.style+"/icon/reference.png) center center no-repeat")
    }
});




