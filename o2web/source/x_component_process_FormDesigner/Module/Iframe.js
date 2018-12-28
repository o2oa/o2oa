MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Iframe = MWF.FCIframe = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Iframe/iframe.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Iframe/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Iframe/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "iframe";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	load : function(json, node, parent){

		this.json = json;
		this.node= node;
		this.node.store("module", this);
		
		var iframe = this.node.getElement("iframe");
		if (iframe) iframe.destroy();
		
		this.node.setStyles(this.css.moduleNode);
		
		this._loadNodeStyles();
		
		this._initModule();
		this._loadTreeNode(parent);

        this.setCustomStyles();
		
		this.parentContainer = this.treeNode.parentNode.module;
        this._setEditStyle_custom("id");

		this.parseModules();
        this.json.moduleName = this.moduleName;
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "iframe",
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
		new Element("div", {
			"styles": this.css.iconNodeIcon
		}).inject(this.iconNode);
		new Element("div", {
			"styles": this.css.iconNodeText,
			"text": "iframe"
		}).inject(this.iconNode);

		this.iconNode.addEvent("click", function(){
			this.loadIframe();
		}.bind(this));
	},
	_loadNodeStyles: function(){
        this.iconNode = this.node.getElement("div");
		if (!this.iconNode) this.iconNode = new Element("div").inject(this.node, "top");
        this.iconNode.setStyles(this.css.iconNode);

		var icon = this.iconNode.getFirst("div");
        var text = this.iconNode.getLast("div");
        icon.setStyles(this.css.iconNodeIcon);
        text.setStyles(this.css.iconNodeText);

		this.iconNode.addEvent("click", function(){
			this.loadIframe();
		}.bind(this));
	},
	getIconPosition: function(){
		var p = this.node.getPosition(this.node.getOffsetParent());
		var size = this.node.getSize();
		var iconSize = this.iconNode.getSize();
		
		return {"x": p.x+size.x-iconSize.x-1, "y": p.y+1};
	},
	loadIframe: function(){
		if (this.iframe){
			this.closeSrc();
		}else{
			this.loadSrc();
		}
	},
	closeSrc: function(){
		this.iframe.destroy();
		this.iframe = null;
		this.iconNode.setStyles(this.css.iconNode);
		this.iconNode.getFirst().setStyles(this.css.iconNodeIcon);
	},
	loadSrc: function(){
        if (this.json.valueType!="script"){
		    if (this.json.src){
                var p = this.getIconPosition();
                this.iconNode.setStyles({
                    "float": "right",
                    "margin-top": "0px",
                    "position": "absolute",
                    "top": p.y,
                    "left": p.x-18
                });
                this.iconNode.getFirst().setStyles({
                    "background": "url("+this.path+this.options.style+"/icon/close.png) 5px center no-repeat"
                });

                this.iframe = new Element("iframe", {
                    "styles": this.css.iframe,
                    "border": "0",
                    "src": this.json.src
                });
                this.iframe.inject(this.node, "top");
            }
		}
	}
});
