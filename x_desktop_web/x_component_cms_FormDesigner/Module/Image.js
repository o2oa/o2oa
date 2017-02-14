MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("cms.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.cms.FormDesigner.Module.Image = MWF.CMSFCImage = new Class({
	Extends: MWF.CMSFC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Image/image.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_cms_FormDesigner/Module/Image/";
		this.cssPath = "/x_component_cms_FormDesigner/Module/Image/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "image";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("img", {
			"MWFType": "image",
			"id": this.json.id,
			"src": this.json.src || this.path+this.options.style+"/icon/image1.png",
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	
	_getCopyNode: function(){
		if (!this.copyNode) this._createCopyNode();
		this.copyNode.setStyle("display", "inline-block");
		return this.copyNode;
	},
	
	_setEditStyle_custom: function(name){
		if (name=="src"){
			if (this.json.src){
				this.node.src = this.json.src;
				var tmpImg = new Element("img",{
					"src": this.json.src
				}).inject(this.form.node);
				var size = tmpImg.getSize();
				this.node.setStyles({
					"width": ""+size.x+"px",
					"height": ""+size.y+"px"
				});
				tmpImg.destroy();
			}
		}
	}

	
});
