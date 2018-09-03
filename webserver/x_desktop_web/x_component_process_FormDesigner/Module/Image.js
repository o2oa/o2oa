MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Image = MWF.FCImage = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Image/image.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Image/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Image/"+this.options.style+"/css.wcss";

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
                var src = this.json.src.imageSrc;
				this.node.src = src;
                this.setPropertiesOrStyles("styles");
                //if (!this.json.styles.width || !this.json.styles.height){
                //    var tmpImg = new Element("img",{
                //        "src": src
                //    }).inject(this.form.node);
                //    var size = tmpImg.getSize();
                //    if (!this.json.styles.width){
                //        this.node.setStyles({"width": ""+size.x+"px"});
                //        this.json.styles.width = ""+size.x+"px";
                //    }
                //    if (!this.json.styles.height){
                //        this.node.setStyles({"height": ""+size.y+"px"});
                //        this.json.styles.height = ""+size.y+"px";
                //    }
                //    this.property.maplists["styles"].reload(this.json.styles);
                //    tmpImg.destroy();
                //}
			}
		}
	},
    _setNodeProperty: function(){
        if (typeOf(this.json.src)=="object"){
            var src = MWF.xDesktop.getImageSrc( this.json.src.imageId );
            this.node.set("src", src);
        }
	}

	
});
