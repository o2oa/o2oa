MWF.xApplication.portal.PageDesigner.Module.Image = MWF.PCImage = new Class({
	Extends: MWF.FCImage,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Image/image.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Image/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Image/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "image";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
    _setEditStyle_custom: function(name){
        if (name=="src"){
            if (this.json.src){
                var src = this.json.src.imageSrc;
                this.node.src = src;
                this.setPropertiesOrStyles("styles");
            }else{
                this.node.set("src", this.path +this.options.style+"/icon/image1.png");
            }
        }
        debugger;
        if (name=="srcfile"){
            var value = this.json.srcfile;
            if (value==="none"){
                this.json.srcfile = "";
                value = "";
            }
            if (value){
                if (typeOf(value)==="object"){
                    var url = MWF.xDesktop.getPortalFileUr(value.id, value.portal);
                    url = o2.filterUrl(url);
                    try{
                        this.node.set("src", url);
                    }catch(e){}
                }else{
                    var host = MWF.Actions.getHost("x_portal_assemble_surface");
                    var action = MWF.Actions.get("x_portal_assemble_surface");
                    var uri = action.action.actions.readFile.uri;
                    uri = uri.replace("{flag}", value);
                    uri = uri.replace("{applicationFlag}", this.form.json.application);
                    value = host+"/x_portal_assemble_surface"+uri;
                    value = o2.filterUrl(value);
                    try{
                        this.node.set("src", value);
                    }catch(e){}
                }
            }else{
                if (this.json.properties.src) {
                    this._setEditStyle_custom("properties");
                }else if (this.json.src){
                    this._setEditStyle_custom("src");
                }else{
                    this.node.set("src", this.path +this.options.style+"/icon/image1.png");
                }
            }

            // if (value==="none"){
            //     this.json.srcfile = "";
            //     value = "";
            // }
            // if (value){
            //     var host = MWF.Actions.getHost("x_portal_assemble_surface");
            //     var action = MWF.Actions.get("x_portal_assemble_surface");
            //     var uri = action.action.actions.readFile.uri;
            //     uri = uri.replace("{flag}", value);
            //     uri = uri.replace("{applicationFlag}", this.form.json.application);
            //     value = host+"/x_portal_assemble_surface"+uri;
            //
            //     try{
            //         this.node.set("src", value);
            //     }catch(e){}
            // }else{
            //     if (this.json.properties.src) {
            //         this._setEditStyle_custom("properties");
            //     }else if (this.json.src){
            //         this._setEditStyle_custom("src");
            //     }else{
            //         this.node.set("src", this.path +this.options.style+"/icon/image1.png");
            //     }
            // }
        }
        if (name=="properties"){
            this._setNodeProperty();
        }
    }
});
