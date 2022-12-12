MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.ImageClipper = MWF.FCImageClipper = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/ImageClipper/imageclipper.html"
	},

	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_process_FormDesigner/Module/ImageClipper/";
		this.cssPath = "../x_component_process_FormDesigner/Module/ImageClipper/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "imageclipper";

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
		});
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
		//var imageNode = new Element("img", {
		//	"src": this.json.src || this.path+this.options.style+"/icon/image1.png",
		//	"styles": this.css.imageNode
		//}).inject(this.moveNode);
		var button = new Element("button", {
			"styles": this.css.buttonIcon,
			"text": this.json.name || this.json.id
		}).inject(this.moveNode);
	},
	_loadNodeStyles: function(){
		var button = this.node.getFirst("button");
		button.setStyles(this.css.buttonIcon);
		button.setStyles(this.json.buttonStyles);
	},


	setAllStyles: function(){
		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("buttonStyles");
		this.setPropertiesOrStyles("imageStyles");
		this.setPropertiesOrStyles("properties");
		this.reloadMaplist();
	},
	_initModule: function(){
		if (!this.json.isSaved) this.setStyleTemplate();

		this._resetModuleDomNode();

		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("buttonStyles");
		this.setPropertiesOrStyles("imageStyles");
		this.setPropertiesOrStyles("properties");

		this._setNodeProperty();
		if (!this.form.isSubform) this._createIconAction();
		this._setNodeEvent();
		this.json.isSaved = true;
	},

	unSelected: function(){
		this.node.setStyles({
			"border": "1px dashed #999"
		});
		if (this.actionArea) this.actionArea.setStyle("display", "none");
		this.form.currentSelectedModule = null;

		this.hideProperty();
	},
	unOver: function(){
		if (!this.form.moveModule) if (this.form.currentSelectedModule!=this) this.node.setStyles({
			"border": "1px dashed #999"
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

	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			try{
				this.setCustomStyles();
			}catch(e){}
		}
		if (name=="properties"){
			try{
				this.setCustomProperties();
			}catch(e){}
		}
		if (name=="buttonStyles"){
			if (this.json.buttonStyles){
				var button = this.node.getElement("button");
				button.clearStyles();
				button.setStyles(this.css.buttonIcon);
				button.setStyles(this.json.buttonStyles);
			}
		}
	},

	_setEditStyle_custom: function(name){
		if (name=="name"){
			if (this.json.name){
				var button = this.node.getElement("button");
				button.set("text", this.json.name);
			}
		}
		if (name=="id"){
			if (!this.json.name){
				var button = this.node.getElement("button");
				button.set("text", this.json.id);
			}
		}
	},

	getData: function(){
		return this.attachmentController.getAttachmentNames();
	}

	//_setEditStyle_custom: function(name){
	//	if (name=="name"){
	//		if (this.json.name){
	//			var img = this.node.getElement("img");
	//			//button.set("text", this.json.name);
	//			img.setStyles( this.css.imageNode );
	//		}
	//	}
	//	if (name=="id"){
	//		if (!this.json.name){
	//			var img = this.node.getElement("img");
	//			//button.set("text", this.json.name);
	//			img.setStyles( this.css.imageNode );
	//		}
	//	}
	//}

	
});
