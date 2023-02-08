MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Relatedlink = MWF.FCRelatedlink = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Relatedlink/relatedlink.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Relatedlink/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Relatedlink/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "relatedlink";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	_resetModuleDomNode: function(){
		this.contentNode = this.node.getElement(".rlcontent");
		this.buttonNode = this.node.getElement(".rlbutton");
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "relatedlink",
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
		if( this.json.activeType === "click" || !this.json.activeType ){
			this.loadButton();
		}else{
			this.loadContent();
		}
	},
	loadContent: function(){
		this.contentNode = new Element("div.rlcontent", {
			"styles": this.css.contentNode
		}).inject(this.node);

		this.loadIcon();
	},
	loadIcon: function(){
		this.iconNode = new Element("div", {
			"styles": this.css.iconNode
		}).inject(this.contentNode);
		new Element("div", {
			"styles": this.css.iconNodeIcon
		}).inject(this.iconNode);
		new Element("div", {
			"styles": this.css.iconNodeText,
			"text": "Relatedlink"
		}).inject(this.iconNode);
	},
	loadButton: function(){
		this.buttonNode = new Element("div.rlbutton", {
			"styles": this.json.buttonStyles || {},
			"text": this.json.buttonText
		}).inject( this.node, "top" );
		// this.contentNode.setStyle("min-height", 100 - this.buttonNode.getSize().y +"px")
	},
	clearTemplateStyles: function(styles){
		if (styles){
			if (styles.buttonStyles) this.removeStyles(styles.buttonStyles, "buttonStyles");
			if (styles.tableStyles) this.removeStyles(styles.tableStyles, "tableStyles");
			if (styles.tableTitleCellStyles) this.removeStyles(styles.tableTitleCellStyles, "tableTitleCellStyles");
			if (styles.tableContentLineStyles) this.removeStyles(styles.tableContentLineStyles, "tableContentLineStyles");
			if (styles.tableContentLineStyles_over) this.removeStyles(styles.tableContentLineStyles_over, "tableContentLineStyles_over");
			if (styles.tableContentCellStyles) this.removeStyles(styles.tableContentCellStyles, "tableContentCellStyles");
			if (styles.tableTitleCellStyles) this.removeStyles(styles.tableTitleCellStyles, "tableTitleCellStyles");
		}
	},
	setTemplateStyles: function(styles){
		if (styles.buttonStyles) this.copyStyles(styles.buttonStyles, "buttonStyles");
		if (styles.tableStyles) this.copyStyles(styles.tableStyles, "tableStyles");
		if (styles.tableTitleCellStyles) this.copyStyles(styles.tableTitleCellStyles, "tableTitleCellStyles");
		if (styles.tableContentLineStyles) this.copyStyles(styles.tableContentLineStyles, "tableContentLineStyles");
		if (styles.tableContentLineStyles_over) this.copyStyles(styles.tableContentLineStyles_over, "tableContentLineStyles_over");
		if (styles.tableContentCellStyles) this.copyStyles(styles.tableContentCellStyles, "tableContentCellStyles");
		if (styles.tableTitleCellStyles) this.copyStyles(styles.tableTitleCellStyles, "tableTitleCellStyles");
	},
	setAllStyles: function(){
		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("buttonStyles");
		this.setPropertiesOrStyles("properties");
		this.reloadMaplist();
	},
	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			try{
				this.setCustomStyles();
			}catch(e){}
		}
		if (name=="buttonStyles"){
			try{
				if( this.buttonNode ){
					this.buttonNode.clearStyles();
					this.buttonNode.setStyles(this.json.buttonStyles||{});
				}
			}catch(e){}
		}
		if (name=="properties"){
			try{
				this.setCustomProperties();
			}catch(e){}
		}
	},
	_setEditStyle_custom: function(name, obj, oldValue){
		switch ( name ){
			case "activeType":

				switch (this.json.activeType) {
					case "click":
						if (!this.buttonNode) this.loadButton();
						if (this.contentNode) {
							this.contentNode.destroy();
							this.contentNode = null;
						}
						break;
					case "delay":
					case "immediately":
						if (!this.contentNode) this.loadContent();
						if (this.buttonNode) {
							this.buttonNode.destroy();
							this.buttonNode = null;
						}
						break;
				}

			case "buttonText":
				if (this.buttonNode) this.buttonNode.set("text", this.json.buttonText);

		}
	}

});
