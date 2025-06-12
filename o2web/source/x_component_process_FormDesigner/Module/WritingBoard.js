MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.WritingBoard = MWF.FCWritingBoard = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/WritingBoard/writingBoard.html"
	},

	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_process_FormDesigner/Module/WritingBoard/";
		this.cssPath = "../x_component_process_FormDesigner/Module/WritingBoard/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "writingBoard";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	_createNode: function(){
		this.node = this.moveNode.clone(true, true);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("id", this.json.id);
		this.node.addEvent("selectstart", function(){
			return false;
		})
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "writingBoard",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
		var actionNode = new Element("div", {
			"styles": this.css.actionNode,
			"text": this.json.name || MWF.xApplication.process.FormDesigner.LP.handWriting
		}).inject(this.moveNode);
		var imageAreaNode = new Element("div", {
			"styles": this.css.imageNode
		}).inject(this.moveNode)
	},
	setAllStyles: function(){
		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("actionStyles");
		this.setPropertiesOrStyles("imageStyles");
		this.setPropertiesOrStyles("properties");
		this.reloadMaplist();
	},
	_initModule: function(){
		if (!this.json.isSaved) this.setStyleTemplate();

		this._resetModuleDomNode();

		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("actionStyles");
		this.setPropertiesOrStyles("imageStyles");
		this.setPropertiesOrStyles("properties");

		this._setNodeProperty();
		if (!this.form.isSubform) this._createIconAction();
		this._setNodeEvent();
		this.json.isSaved = true;
	},
	clearTemplateStyles: function(styles){
		if (styles){
			if (styles.styles) this.removeStyles(styles.styles, "styles");
			if (styles.actionStyles) this.removeStyles(styles.actionStyles, "actionStyles");
			if (styles.imageStyles) this.removeStyles(styles.imageStyles, "imageStyles");
			if (styles.properties) this.removeStyles(styles.properties, "properties");
		}
	},
	setTemplateStyles: function(styles){
		if (styles.styles) this.copyStyles(styles.styles, "styles");
		if (styles.actionStyles) this.copyStyles(styles.actionStyles, "actionStyles");
		if (styles.imageStyles) this.copyStyles(styles.imageStyles, "imageStyles");
		if (styles.properties) this.copyStyles(styles.properties, "properties");
	},

	setPropertiesOrStyles: function(name){
		if (name=="styles")this.setCustomStyles();
		if (name=="actionStyles"){
			this._recoveryModuleData();
			var actionNode = this.node.getFirst();
			if(actionNode){
				actionNode.clearStyles();
				this.parseStyles(actionNode, this.json.actionStyles, true);
			}

		}
		if (name=="imageStyles"){
			this._recoveryModuleData();
			var imageNode = this.node.getLast();
			if(imageNode){
				imageNode.clearStyles();
				this.parseStyles(imageNode, this.json.imageStyles, true);
			}

		}
		if (name=="properties"){
			try{
				this.setCustomProperties();
			}catch(e){}
			this.node.setProperties(this.json.properties);
		}
	},

	_setEditStyle_custom: function(name, obj, oldValue){
		if( ["styles","actionStyles","imageStyles","properties"].contains(name) ){
			this.setPropertiesOrStyles(name)
		}
	},

	setCustomStyles: function(){
		var border = this.node.getStyle("border");
		this._recoveryModuleData();
		this.node.clearStyles();
		this.node.setStyles(this.css.moduleNode);
		if (this.initialStyles) this.node.setStyles(this.initialStyles);
		this.node.setStyle("border", border);
		this.parseStyles(this.node, this.json.styles);
	},
	parseStyles: function (node, style, ignoreBorder) {
		if (style) Object.each(style, function(value, key){
			if ((value.toString().indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
				var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
				var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
				if (value.toString().indexOf("/x_processplatform_assemble_surface")!==-1){
					value = value.toString().replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
				}else if (value.toString().indexOf("x_processplatform_assemble_surface")!==-1){
					value = value.toString().replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
				}
				if (value.toString().indexOf("/x_portal_assemble_surface")!==-1){
					value = value.toString().replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
				}else if (value.toString().indexOf("x_portal_assemble_surface")!==-1){
					value = value.toString().replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
				}
				value = o2.filterUrl(value);
			}

			var reg = /^border\w*/ig;
			if (ignoreBorder || !key.test(reg)){
				if (key){
					if (key.toString().toLowerCase()==="display"){
						if (value.toString().toLowerCase()==="none"){
							node.setStyle("opacity", 0.3);
						}else{
							node.setStyle("opacity", 1);
							node.setStyle(key, value);
						}
					}else{
						node.setStyle(key, value);
					}
				}
			}
		}.bind(this));
	}


});
