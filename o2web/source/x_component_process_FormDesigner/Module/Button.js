MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Button = MWF.FCButton = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Button/button.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Button/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Button/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "button";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	},

	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "button",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
		var button = new Element("button", {
			"styles": this.css.buttonIcon,
			"text": this.json.name || this.json.id
		}).inject(this.moveNode);
	},
	_loadNodeStyles: function(){
		var button = this.node.getFirst("button");
		if (!button) button = this.node.getFirst("input");
        if (button)button.setStyles(this.css.buttonIcon);
	},
	
	unSelected: function(){
		this.node.setStyles({
			"border-top": "1px solid #999",
			"border-left": "1px solid #999",
			"border-right": "1px solid #333",
			"border-bottom": "1px solid #333"
		});
		if (this.actionArea) this.actionArea.setStyle("display", "none");
		this.form.currentSelectedModule = null;
		
		this.hideProperty();
	},
	unOver: function(){
		if (!this.form.moveModule) if (this.form.currentSelectedModule!=this) this.node.setStyles({
			"border-top": "1px solid #999",
			"border-left": "1px solid #999",
			"border-right": "1px solid #333",
			"border-bottom": "1px solid #333"
		});
	},
	_createCopyNode: function(){
		this.copyNode = new Element("div", {
			"styles": this.css.moduleNodeShow
		});
		this.copyNode.addEvent("selectstart", function(){
			return false;
		});
	},
	_getCopyNode: function(){
		if (!this.copyNode) this._createCopyNode();
		this.copyNode.setStyle("display", "inline-block");
		return this.copyNode;
	},
	
	_setEditStyle_custom: function(name){
		if (name=="name"){
			if (this.json.name){
				var button = this.node.getElement("button");
                if (!button) button = this.node.getFirst("input");
				if (button) button.set("text", this.json.name);
			}
		}
		if (name=="id"){
			if (!this.json.name){
				var button = this.node.getElement("button");
                if (!button) button = this.node.getFirst("input");
                if (button) button.set("text", this.json.id);
			}
		}
	},
	_preprocessingModuleData: function(){
		var button = this.node.getElement("button");
		button.clearStyles();
		debugger;
		button.setStyles(this.css.buttonStyles);

		//if (this.initialStyles) this.node.setStyles(this.initialStyles);
		this.json.recoveryStyles = Object.clone(this.json.styles);

		if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
			if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
				//需要运行时处理
			}else{
				button.setStyle(key, value);
				delete this.json.styles[key];
			}
		}.bind(this));
		this.json.preprocessing = "y";
	},
	_recoveryModuleData: function(){
		// var button = this.node.getElement("button");
		// button.clearStyles();
		// button.setStyles(this.css.buttonIcon);
		//
		if (this.json.recoveryStyles) this.json.styles = this.json.recoveryStyles;
		this.json.recoveryStyles = null;
	},
	setCustomStyles: function(){
		this._recoveryModuleData();
		//debugger;
		var border = this.node.getStyle("border");
		this.node.clearStyles();
		this.node.setStyles(this.css.moduleNode);

		if (this.initialStyles) this.node.setStyles(this.initialStyles);
		this.node.setStyle("border", border);

		var button = this.node.getElement("button");
		button.clearStyles();
		button.setStyles(this.css.buttonIcon);

		if (this.json.styles) Object.each(this.json.styles, function(value, key){
			if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
				var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
				var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
				if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
					value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
				}else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
					value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
				}
				if (value.indexOf("/x_portal_assemble_surface")!==-1){
					value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
				}else if (value.indexOf("x_portal_assemble_surface")!==-1){
					value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
				}
				value = o2.filterUrl(value);
			}

			var reg = /^border\w*/ig;
			if (!key.test(reg)){
				if (key){
					if (key.toString().toLowerCase()==="display"){
						if (value.toString().toLowerCase()==="none"){
							this.node.setStyle("opacity", 0.3);
						}else{
							this.node.setStyle("opacity", 1);
							this.node.setStyle(key, value);
						}
					}else{
						button.setStyle(key, value);
					}
				}
			}
			//this.node.setStyle(key, value);
		}.bind(this));

		// Object.each(this.json.styles, function(value, key){
		// 	var reg = /^border\w*/ig;
		// 	if (!key.test(reg)){
		// 		if (key){
		// 			if (key.toString().toLowerCase()==="display"){
		// 				if (value.toString().toLowerCase()==="none"){
		//                    this.node.setStyle("opacity", 0.3);
		// 				}else{
		//                    this.node.setStyle("opacity", 1);
		// 				}
		// 			}else{
		//                this.node.setStyle(key, value);
		// 			}
		// 		}
		// 	}
		// }.bind(this));
	},
});
