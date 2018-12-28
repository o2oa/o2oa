MWF.xDesktop.requireApp("ScriptEditor", "statement.Package", null, false);
MWF.xDesktop.requireApp("ScriptEditor", "block.Package", null, false);
MWF.xDesktop.requireApp("ScriptEditor", "ScriptArea", null, false);

MWF.xApplication.ScriptEditor.Editor = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    _loadPath: function(){
        this.path = "/x_component_ScriptEditor/$Editor/";
        this.cssPath = "/x_component_ScriptEditor/$Editor/"+this.options.style+"/css.wcss";
    },
    initialize: function(node, app, options){
        this.setOptions(options);
        this._loadPath();
        this._loadCss();

        this.node = $(node);
        this.app  = app;

        this.moduleTypes = [];
        this.currentModuleType = null;
    },
    load: function(){
        this.moduleAreaNode = new Element("div", {"styles": this.css.moduleAreaNode}).inject(this.node);
        this.resizeAreaNode = new Element("div", {"styles": this.css.resizeAreaNode}).inject(this.node);
        this.scriptAreaNode = new Element("div", {"styles": this.css.scriptAreaNode}).inject(this.node);

        this.scriptArea = new MWF.xApplication.ScriptEditor.ScriptArea(this);

        this.moduleTypeAreaNode = new Element("div", {"styles": this.css.moduleTypeAreaNode}).inject(this.moduleAreaNode);
        this.moduleBlockAreaNode = new Element("div", {"styles": this.css.moduleBlockAreaNode}).inject(this.moduleAreaNode);

        // this.setAreaSizeFun = this.setAreaSize.bind(this);
        // this.app.addEvent("resize", this.setAreaSizeFun);
        // this.setAreaSize();

        this.getModule(function(){
            Object.each(this.json, function(v, k){
                v.name = k;
                this.createModuleType(v);
            }.bind(this));
        }.bind(this));
    },
    // setAreaSize: function(){
    //     var size = this.moduleAreaNode.getSize();
    //     var moduleTypeSize = this.moduleTypeAreaNode.getSize();
    //     debugger;
    //     var margin = this.moduleTypeAreaNode.getStyles("margin-top", "margin-bottom");
    //     var y = size.y-moduleTypeSize.y-margin["margin-top"].toInt()-margin["margin-bottom"].toInt();
    //     this.moduleBlockAreaNode.setStyle("height", ""+y+"px");
    // },
    getModule: function(callback){
        MWF.getJSON("/x_component_ScriptEditor/$Editor/block.json", function(json){
            this.json = json;
            if (callback) callback();
        }.bind(this));
    },

    createModuleType: function(data){
        this.moduleTypes.push(new MWF.xApplication.ScriptEditor.Editor.ModuleType(data, this));
    }

});


MWF.xApplication.ScriptEditor.Editor.ModuleType = new Class({
    initialize: function(data, editor){
        this.data = data;
        this.editor = editor;
        this.typeAreaNode = this.editor.moduleTypeAreaNode;
        this.blockAreaNode = this.editor.moduleBlockAreaNode;
        this.css = this.editor.css;
        this.blocks = [];
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.moduleTypeNode}).inject(this.typeAreaNode);
        this.colorNode = new Element("div", {"styles": this.css.moduleTypeColorNode}).inject(this.node);
        this.iconNode = new Element("div", {"styles": this.css.moduleTypeIconNode}).inject(this.colorNode);
        this.textNode = new Element("div", {"styles": this.css.moduleTypeTextNode}).inject(this.node);

        this.colorNode.setStyle("background-color", this.data.color);
        this.iconNode.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/"+this.data.icon+")");

        this.textNode.set("text", this.data.text);

        this.node.addEvent("click", function(){
            this.selected();
        }.bind(this));
    },

    selected: function(){
        if (this.editor.currentModuleType) this.editor.currentModuleType.unselected();
        this.node.setStyle("background", this.data.color);
        this.textNode.setStyle("color", "#ffffff");
        this.editor.currentModuleType = this;
        this.showBlockArea();
    },

    unselected: function(){
        if (this.editor.currentModuleType.data.name===this.data.name){
            this.node.setStyle("background", "transparent");
            this.textNode.setStyles(this.css.moduleTypeTextNode);
            this.editor.currentModuleType = null;
            this.hiddenBlockArea();
        }
    },
    showBlockArea: function(){
        if (!this.blocksNode) this.createBlocksNode();
        this.blocksNode.setStyle("display", "block");
    },
    createBlocksNode: function(){
        this.blocksNode = new Element("div", {"styles": this.css.moduleBlocksNode}).inject(this.blockAreaNode);
        if (this.data.blocks && this.data.blocks.length){
            this.data.blocks.each(function(block){
                this.createBlock(block);
            }.bind(this));
        }else if (this.data.blockLink){
            MWF.getJSON("/x_component_ScriptEditor/$Editor/"+this.data.blockLink, function(json){
                this.data.blocks = json;
                this.data.blocks.each(function(block){
                    this.createBlock(block);
                }.bind(this));
            }.bind(this));
        }
    },
    createBlock: function(block){
        //this.blocks.push(new MWF.xApplication.ScriptEditor.Editor.Block(block, this));
        if (block.class){
            //MWF.xDesktop.requireApp("ScriptEditor", "block."+block.class, function(){
                var classPath = ("block."+block.class).split(".");
                var clazz = MWF.xApplication.ScriptEditor;
                classPath.each(function(p){ if (clazz){clazz = clazz[p];} }.bind(this));
                if (!clazz) clazz = this.createClazz(block);
                if (clazz) this.blocks.push(new clazz(block, this));
            //}.bind(this));
        }else if (block.type === "category"){
            var lineNode = new Element("div", {"styles": this.css.moduleBlocksSplitLineNode}).inject(this.blocksNode);
            var leftNode = new Element("div", {"styles": this.css.moduleBlocksSplitLineLeftNode}).inject(lineNode);
            var textNode = new Element("div", {"styles": this.css.moduleBlocksSplitLineTextNode}).inject(lineNode);
            var rightNode = new Element("div", {"styles": this.css.moduleBlocksSplitLineRightNode}).inject(lineNode);
            textNode.set("text", block.name);
        }
    },
    createClazz: function(block){
        var clazz = MWF.xApplication.ScriptEditor;
        var classPath = ("block."+block.class).split(".");
        classPath.each(function(p, i){
            if (i===(classPath.length-1)){
                clazz[p] = new Class({
                    Extends: MWF.xApplication.ScriptEditor.block.$Block[block.extend]
                });
                clazz = clazz[p];
            }else{
                clazz = clazz[p];
                if (!clazz) clazz = {};
            }
        }.bind(this));
        return clazz;
    },
    hiddenBlockArea: function(){
        if (this.blocksNode) this.blocksNode.setStyle("display", "none");
    }
});
MWF.SES.zIndexPool = {
    zIndex: 200,
    apply: function(){
        var i = this.zIndex;
        this.zIndex = this.zIndex+1;
        return i;
    }
};