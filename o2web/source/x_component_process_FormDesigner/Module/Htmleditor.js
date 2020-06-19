MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Htmleditor = MWF.FCHtmleditor = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Htmleditor/htmleditor.html"
	},
	
	initialize: function(form, options){
		debugger;
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Htmleditor/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Htmleditor/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "htmleditor";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "htmleditor",
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
		this.node.addEvent("selectstart", function(e){
			e.preventDefault();
		});
//		this.loadCkeditor();
	},
	
	_setEditStyle_custom: function(name){
		if (name=="editorProperties"){
			if (this.editor){
				Object.each(this.json.editorProperties, function(value, key){
					if (value=="true") this.json.editorProperties[key] = true;
					if (value=="false") this.json.editorProperties[key] = false;
				}.bind(this));
				this.distroyCkeditor();

                var config = Object.clone(this.json.editorProperties);
                if (this.json.config){
                    if (this.json.config.code){
                        var obj = MWF.Macro.exec(this.json.config.code, this);
                        Object.each(obj, function(v, k){
                            config[k] = v;
                        });
                    }
                }

				this.loadCkeditor(config);
			}
		}

        if (name=="templateCode"){
            if (this.editor) this.editor.setData(this.json.templateCode);
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

		this.loadCkeditor(config);
		this._setNodeProperty();
        if (!this.form.isSubform) this._createIconAction() ;
		this._setNodeEvent();
	},
	//ckeditor
	loadCkeditor: function(config){
		COMMON.AjaxModule.load("ckeditor", function(){
			CKEDITOR.disableAutoInline = true;
			var editorDiv = new Element("div").inject(this.node);
            if (this.json.templateCode) editorDiv.set("html", this.json.templateCode);

			var height = this.node.getSize().y;
			var editorConfig = config || {};
            if (this.form.options.mode=="Mobile"){
            //    if (!editorConfig.toolbar && !editorConfig.toolbarGroups){
                    editorConfig.toolbar = [
                        //{ name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
                        //{ name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
                        //{ name: 'links' },
                        //{ name: 'insert' },
                        //{ name: 'forms' },
                        //{ name: 'tools' },
                        //{ name: 'document',    groups: [ 'mode', 'document', 'doctools' ] },
                        //{ name: 'others' },
                        //'/',
                        { name: 'paragraph',   items: [ 'Bold', 'Italic', "-" , 'TextColor', "BGColor", 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', "-", 'Undo', 'Redo' ] },
                        { name: 'basicstyles', items: [ 'Styles', 'FontSize']}
                        //{ name: 'colors' },
                        //{ name: 'about' }
                    ];
            //    }
            }


          //  CKEDITOR.basePath = COMMON.contentPath+"/res/framework/htmleditor/ckeditor/";
			this.editor = CKEDITOR.replace(editorDiv, editorConfig);

			this.editor.on("dataReady", function(){
				this.editor.setReadOnly(true);
			}, this);
		}.bind(this));
	},
	destroy: function(){
		this.distroyCkeditor();
		this.form.moduleList.erase(this);
		this.form.moduleNodeList.erase(this.node);
		this.form.moduleElementNodeList.erase(this.node);

		if (this.form.scriptDesigner){
			this.form.scriptDesigner.removeModule(this.json);
		}

		if (this.property) this.property.destroy();
		this.node.destroy();
		this.actionArea.destroy();

		delete this.form.json.moduleList[this.json.id];
		this.json = null;
		delete this.json;

		this.treeNode.destroy();
		o2.release(this);
	},
	distroyCkeditor: function(){
		if (this.editor) this.editor.destroy();
		this.editor = null;
	}
});
