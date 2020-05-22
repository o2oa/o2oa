MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.cms.FormDesigner.Module.Readerfield = MWF.CMSFCReaderfield = new Class({
	Extends: MWF.FC$Element,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Readerfield/readerfield.html"
	},
	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_cms_FormDesigner/Module/Readerfield/";
		this.cssPath = "../x_component_cms_FormDesigner/Module/Readerfield/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "readerfield";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	clearTemplateStyles: function(styles){
		if (styles){
			if (styles.styles) this.removeStyles(styles.styles, "styles");
			if (styles.inputStyles) this.removeStyles(styles.inputStyles, "inputStyles");
			if (styles.properties) this.removeStyles(styles.properties, "properties");
		}
	},
	setTemplateStyles: function(styles){
		if (styles.styles) this.copyStyles(styles.styles, "styles");
		if (styles.inputStyles) this.copyStyles(styles.inputStyles, "inputStyles");
		if (styles.properties) this.copyStyles(styles.properties, "properties");
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "readerfield",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);

		var icon = new Element("div", {
			"styles": this.css.readerfieldIcon
		}).inject(this.moveNode);
		var text = new Element("div", {
			"styles": this.css.moduleText,
			"text": this.json.id
		}).inject(this.moveNode);
	},
	_loadNodeStyles: function(){
		var icon = this.node.getFirst("div");
		var text = this.node.getLast("div");
		icon.setStyles(this.css.readerfieldIcon);
		text.setStyles(this.css.moduleText);
	},
	_getCopyNode: function(){
		if (!this.copyNode) this._createCopyNode();
		this.copyNode.setStyle("display", "inline-block");
		return this.copyNode;
	},

	_setEditStyle_custom: function(name){
		if (name=="id"){
			this.node.getLast().set("text", this.json.id);
		}
	},
	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			if (this.parentContainer){
				if (this.parentContainer.moduleName == "datagrid$Data"){
					if (!this.json.styles.width) this.json.styles.width = "90%";
				}
			}
			try{
				this.setCustomStyles();
			}catch(e){}
			//this.setCustomStyles();
		}
		if (name=="inputStyles"){
			var text = this.node.getLast("div");
			text.clearStyles();
			text.setStyles(this.css.moduleText);

			Object.each(this.json.inputStyles, function(value, key){
				var reg = /^border\w*/ig;
				if (!key.test(reg)){
					text.setStyle(key, value);
				}
			}.bind(this));
		}
		if (name=="properties"){
			this.node.setProperties(this.json.properties);
		}
	}
});
