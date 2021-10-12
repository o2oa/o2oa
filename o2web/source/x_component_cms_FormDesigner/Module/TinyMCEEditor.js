MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.TinyMCEEditor", null, false);
MWF.xApplication.cms.FormDesigner.Module.TinyMCEEditor = MWF.CMSFCTinyMCEEditor = new Class({
	Extends: MWF.FCTinyMCEEditor,
	Implements : [MWF.CMSFCMI],
	_setEditStyle_custom: function(name){
		if (name=="editorProperties"){
			if (this.editor){
				Object.each(this.json.editorProperties, function(value, key){
					if (value=="true") this.json.editorProperties[key] = true;
					if (value=="false") this.json.editorProperties[key] = false;
				}.bind(this));
				this.distroyEditor();

                var config = Object.clone(this.json.editorProperties);
                if (this.json.config){
                    if (this.json.config.code){
                        var obj = MWF.Macro.exec(this.json.config.code, this);
                        Object.each(obj, function(v, k){
                            config[k] = v;
                        });
                    }
                }

				this.loadTinyMCEEditor(config);
			}
		}

        if (name=="templateCode"){
            if (this.editor) {
				this.editor.setContent(this.json.templateCode);
			}
        }
	},
	_initModule: function(){
		this.node.empty();

        var config = Object.clone(this.json.editorProperties);
        if (this.json.config){
            if (this.json.config.code){
                var obj = MWF.Macro.exec(this.json.config.code, this);
                Object.each(obj, function(v, k){
                    config[k] = v;
                });
            }
        }

		this.loadTinyMCEEditor(config);
		this._setNodeProperty();
        if (!this.form.isSubform) this._createIconAction() ;
		this._setNodeEvent();
	}
});
