MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.TinyMCEEditor = MWF.FCTinyMCEEditor = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/TinyMCEEditor/TinyMCEEditor.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/TinyMCEEditor/";
		this.cssPath = "../x_component_process_FormDesigner/Module/TinyMCEEditor/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "tinymceeditor";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "tinymceeditor",
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
	},
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
	},
	loadResource: function ( callback ) {
		o2.load([
			"../o2_lib/tinymce/tinymce_5.9.2/tinymce.min.js",
			"../o2_lib/tinymce/tinymce_5.9.2/o2config.js"
		], function () {
			var config = o2.TinyMCEConfig( this.form.json.mode === "Mobile" );
			callback( config );
		}.bind(this))
	},
	//ckeditor
	loadTinyMCEEditor: function(config){
		this.loadResource( function( defaultConfig ){
			var editorConfig = Object.merge(defaultConfig, config || {});

			var id = this.form.json.id +"_"+this.json.id + "_" + this.form.options.mode;
			editorConfig.selector = '#'+id;
			var editorDiv = new Element("div", {"id": id}).inject(this.node);
			editorConfig.readonly = true;

			editorConfig.init_instance_callback = function(editor) {
				this.form.designer.addEvent("queryClose", function () {
					editor.destroy();
				});
				this.editor = editor;
				if( this.json.templateCode ){
					this.editor.setContent(this.json.templateCode);
				}
			}.bind(this);

			tinymce.init(editorConfig);
		}.bind(this));
	},
	destroy: function(){
		this.distroyEditor();
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
	distroyEditor: function(){
		if (this.editor) this.editor.destroy();
		this.editor = null;
	}
});

