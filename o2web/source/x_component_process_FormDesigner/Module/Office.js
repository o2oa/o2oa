MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Office = MWF.FCOffice = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Office/office.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Office/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Office/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "office";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "office",
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
			"text": "Office"
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
		if (this.json.officeType=="word"){
			this.setIconNode("word", "Word", "#2b5797", 90);
		}
		if (this.json.officeType=="excel"){
			this.setIconNode("excel", "Excel", "#1e7145", 86);
		}
		if (this.json.officeType=="ppt"){
			this.setIconNode("ppt", "PowerPoint", "#d04525", 130);
		}
		if (this.json.officeType=="other"){
			this.setIconNode("office", "Office", "#f36523", 96);
		}
	},
	_setEditStyle_custom: function(name){
		if (name=="officeType"){
			this.setIcon();
		}
	}
});
