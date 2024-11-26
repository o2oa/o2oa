MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Foxit = MWF.FCFoxit = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Foxit/foxit.html"
	},

	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_process_FormDesigner/Module/Foxit/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Foxit/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "foxit";
		this.moduleName = "foxit";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},

	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "foxit",
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
			"text": "Foxit"
		}).inject(this.iconNode);

		this.setIcon();
	},

	_loadNodeStyles: function(){
		this.iconNode = this.node.getElement("div").setStyles(this.css.iconNode);
		this.iconNode.getFirst("div").setStyles(this.css.iconNodeIcon);
		this.iconNode.getLast("div").setStyles(this.css.iconNodeText);

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
				"width": width-48
			});
		}
	},
	setIcon: function(){
		this.setIconNode("foxit", "Foxit", "#2b5797", 240);
	}
});
