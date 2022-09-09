MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Image", null, false);
MWF.xApplication.cms.FormDesigner.Module.Image = MWF.CMSFCImage = new Class({
	Extends: MWF.FCImage,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Image/image.html"
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
		if (name=="srcfile"){
			debugger;
			var value = this.json.srcfile;
			if (value==="none"){
				this.json.srcfile = "";
				value = "";
			}
			if (value){
				if (typeOf(value)==="object"){
					var url;
					if( value.application ){ //兼容之前的版本，都是从流程中获取的
						url = MWF.xDesktop.getProcessFileUr(value.id, value.application);
					}else{
						url = MWF.xDesktop.getCMSFileUr(value.id, value.appId);
					}
					try{
						this.node.set("src", url);
					}catch(e){}
				}else{
					var host = MWF.Actions.getHost("x_processplatform_assemble_surface");
					var action = MWF.Actions.get("x_processplatform_assemble_surface");
					var uri = action.action.actions.readFile.uri;
					uri = uri.replace("{flag}", value);
					uri = uri.replace("{applicationFlag}", this.form.json.application);
					value = host+"/x_processplatform_assemble_surface"+uri;

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
		}
		if (name=="properties"){
			this._setNodeProperty();
		}
	}
});
