MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.AssociatedDocument = MWF.FCAssociatedDocument = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/AssociatedDocument/associateddocument.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/AssociatedDocument/";
		this.cssPath = "../x_component_process_FormDesigner/Module/AssociatedDocument/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "associatedDocument";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	_resetModuleDomNode: function(){
		// this.contentNode = this.node.getElement(".MWFADContent");
		// this.buttonContainer = this.node.getElement(".MWFADButtonContainer");
		// this.buttonArea = this.node.getElement(".MWFADButtonArea");
		// this.buttonNode = this.node.getElement(".MWFADBbutton");
		debugger;
		this.node.empty();
		this.loadContent();
		this.loadButton();
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "associatedDocument",
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
		this.loadContent();
		this.loadButton();
	},
	loadContent: function(){
		this.contentNode = new Element("div.MWFADContent", {
			"styles": this.json.documentListNodeStyles
		}).inject(this.node);

		this.contentArea = new Element("div", {
			"styles": this.css.contentArea
		}).inject(this.contentNode);

		this.loadIcon();
	},
	loadIcon: function(){
		this.iconNode = new Element("div", {
			"styles": this.css.iconNode
		}).inject(this.contentArea);
		new Element("div", {
			"styles": this.css.iconNodeIcon
		}).inject(this.iconNode);
		new Element("div", {
			"styles": this.css.iconNodeText,
			"text": "associatedDocument"
		}).inject(this.iconNode);
	},
	loadButton: function(){
		this.buttonContainer = new Element("div.MWFADButtonContainer", {
			styles : { "width" : "100%", "overflow" : "hidden", "text-align":"center" }
		});
		this.buttonArea = new Element("div.MWFADButtonArea").inject( this.buttonContainer );
		this.buttonNode = new Element("button.MWFADBbutton", {
			"styles": this.json.buttonStyles || {},
			"text": this.json.buttonText
		}).inject( this.buttonArea );
		this.setButtonPosition();
	},
	clearTemplateStyles: function(styles){
		if (styles){
			if (styles.styles) this.removeStyles(styles.styles, "styles");
			if (styles.buttonStyles) this.removeStyles(styles.buttonStyles, "buttonStyles");
		}
	},
	setTemplateStyles: function(styles){
		if (styles.styles) this.copyStyles(styles.styles, "styles");
		if (styles.buttonStyles) this.copyStyles(styles.buttonStyles, "buttonStyles");
	},
	setAllStyles: function(){
		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("properties");
		this.setPropertiesOrStyles("buttonStyles");
		this.reloadMaplist();
	},
	setPropertiesOrStyles: function(name){
		debugger;
		switch (name) {
			case "styles":
				try{
					this.setCustomStyles();
				}catch(e){}
				break;
			case "properties":
				try{
					this.setCustomProperties();
				}catch(e){}
				break;
			case "buttonStyles":
				try{
					if( this.buttonNode ){
						this.buttonNode.clearStyles();
						this.buttonNode.setStyles(this.json.buttonStyles||{});
					}
				}catch(e){}
				break;
			case "documentListNodeStyles":
				this.contentNode.setStyles( this.json.contentStyle );
		}
	},
	_setEditStyle_custom: function(name, obj, oldValue){
		switch ( name ){
			case "buttonText":
				this.buttonNode.set("text", this.json.buttonText);
				break;
			case "buttonPosition":
				this.setButtonPosition();
				break;
		}


	},
	_checkView: function(callback, name, oldValue, newValue){
		debugger;
		if( name !== "queryView" )return;
		if( !oldValue ){
			oldValue = [];
		}else{
			oldValue = typeOf(oldValue) === "array" ? oldValue : [oldValue];
		}
		if( !newValue ){
			newValue = [];
		}else{
			newValue = typeOf(newValue) === "array" ? newValue : [newValue];
		}
		if( !this.json.viewFilterScriptList )this.json.viewFilterScriptList = [];
		var list = [];
		newValue.each(function (n) {
			var vf = this.json.viewFilterScriptList.filter(function (viewFilter) {
				return n.id === viewFilter.id;
			}.bind(this));
			if( vf.length ){
				list.push( vf[0] );
			}else{
				list.push({
					id: n.id,
					title: n.name + MWF.xApplication.process.FormDesigner.LP.propertyTemplate.filterCond,
					script: {"code": "", "html": ""}
				})
			}
		}.bind(this));

		this.json.viewFilterScriptList.each(function (vf) {
			var ns = newValue.filter(function (n) {
				return n.id === vf.id;
			});
			if( !ns.length ){
				var id1 = "scriptArea_"+vf.id;
				if( this.property[ id1 ] ){
					this.property[ id1 ].destroy();
					this.property[ id1 ] = null;
				}
			}
		}.bind(this))

		this.json.viewFilterScriptList = list;
		this.property.loadScriptListArea();
	},
	setButtonPosition: function () {
		var position = ["leftTop","centerTop","rightTop"].contains( this.json.buttonPosition ) ? "top" : "bottom";
		this.buttonContainer.inject(this.node, position);
		switch ( this.json.buttonPosition ) {
			case "leftTop":
			case "leftBottom":
				this.buttonArea.setStyles({ "float" : "left", "margin" : "0px" });
				break;
			case "rightTop":
			case "rightBottom":
				this.buttonArea.setStyles({ "float" : "right", "margin" : "0px" });
				break;
			default:
				this.buttonArea.setStyles({ "float" : "none", "margin" : "0px auto" });
				break;
		}
	}

});
