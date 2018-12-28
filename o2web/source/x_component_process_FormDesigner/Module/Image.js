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
		if (name="properties"){
			this._setNodeProperty();
		}
	},
    _setNodeProperty: function(){
        if (this.form.moduleList.indexOf(this)==-1) this.form.moduleList.push(this);
        if (this.form.moduleNodeList.indexOf(this.node)==-1) this.form.moduleNodeList.push(this.node);
        if (this.form.moduleElementNodeList.indexOf(this.node)==-1) this.form.moduleElementNodeList.push(this.node);
        this.node.store("module", this);

        if (typeOf(this.json.src)=="object"){
            var src = MWF.xDesktop.getImageSrc( this.json.src.imageId );
            this.node.set("src", src);
        }
		if (this.json.properties && this.json.properties["src"]){
            var value = this.json.properties["src"];
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
            }
            this.node.set("src", value);
		}

	}

	
});
