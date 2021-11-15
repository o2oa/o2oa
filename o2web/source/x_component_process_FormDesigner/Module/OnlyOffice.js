MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.OnlyOffice = MWF.FCOnlyOffice = new Class({
    Extends: MWF.FC$Element,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "propertyPath": "../x_component_process_FormDesigner/Module/OnlyOffice/onlyoffice.html"
    },

    initialize: function(form, options){
        this.setOptions(options);

        this.path = "../x_component_process_FormDesigner/Module/OnlyOffice/";
        this.cssPath = "../x_component_process_FormDesigner/Module/OnlyOffice/"+this.options.style+"/css.wcss";

        this._loadCss();
        this.moduleType = "element";
        this.moduleName = "onlyoffice";

        this.form = form;
        this.container = null;
        this.containerNode = null;
    },

    _createMoveNode: function(){
        // var flag = false;
        // this.form.moduleList.each(function (module){
        //     if(module.moduleName === "onlyoffice"){
        //         flag = true;
        //     }
        // }.bind(this));
        // if(flag){
        //     this.form.designer.notice( "表单中只允许有一个OnlyOffice控件", "error", this.form.container );
        //     return false;
        // }

        this.moveNode = new Element("div", {
            "MWFType": "onlyoffice",
            "id": this.json.id,
            "styles": this.css.moduleNodeMove,
            "events": {
                "selectstart": function(){
                    return false;
                }
            }
        }).inject(this.form.container);
    },
    _createNode: function(){
        this.node = this.moveNode.clone(true, true);
        this.node.setStyles(this.css.moduleNode);
        this.node.set("id", this.json.id);
        this.node.addEvent("selectstart", function(){
            return false;
        });

        this.iconNode = new Element("div", {
            "styles": this.css.iconNode
        }).inject(this.node);
        var icon = new Element("div", {
            "styles": this.css.iconNodeIcon
        }).inject(this.iconNode);
        var text = new Element("div", {
            "styles": this.css.iconNodeText,
            "text": "OnlyOffice"
        }).inject(this.iconNode);

        this.setIcon();
    },

    _loadNodeStyles: function(){
        this.iconNode = this.node.getElement("div").setStyles(this.css.iconNode);
        this.iconNode.getFirst("div").setStyles(this.css.iconNodeIcon);
        this.iconNode.getLast("div").setStyles(this.css.iconNodeText);
        this.setIcon();
    },

    setIconNode: function(img, txt, color, width){
        if (this.iconNode){
            this.iconNode.setStyle("width", width);
            var icon = this.iconNode.getFirst();
            var text = this.iconNode.getLast();
            icon.setStyle("background-image", "url("+this.path+this.options.style+"/icon/"+img+".png)");
            text.set("text", txt);
            text.setStyles({
                "color": color,
                "width": width-34
            });
        }
    },
    setIcon: function(){
        if (this.json.officeType=="docx"){
            this.setIconNode("docx", "Word", "#2b5797", 90);
        }
        if (this.json.officeType=="xlsx"){
            this.setIconNode("xlsx", "Excel", "#1e7145", 86);
        }
        if (this.json.officeType=="pptx"){
            this.setIconNode("pptx", "PowerPoint", "#d04525", 130);
        }
        if (this.json.officeType=="other"){
            this.setIconNode("office", "Office", "#f36523", 96);
        }
    },
    _setEditStyle_custom: function(name){
        if (name=="officeType"){
            this.setIcon();
        }
    },
    _resetModuleDomNode: function(){
        this.setNodeContainer();
    },
    setNodeContainer:function(){

        this.node.empty();
        this.iconNode = new Element("div", {
            "styles": this.css.iconNode
        }).inject(this.node);
        var icon = new Element("div", {
            "styles": this.css.iconNodeIcon
        }).inject(this.iconNode);
        var text = new Element("div", {
            "styles": this.css.iconNodeText,
            "text": "OnlyOffice"
        }).inject(this.iconNode);

        this.setIcon();
        if(!layout.serviceAddressList["x_onlyofficefile_assemble_control"]){
            this.tipNode = new Element("div").inject(this.node);
            this.tipNode.set("html","<h3><font color=red>" + MWF.APPFD.LP.onlyoffice.nosetup + "</font></h3>");
        }
        this.setIcon();

    }
});
