MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Codeeditor = MWF.FCCodeeditor = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Codeeditor/codeeditor.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Codeeditor/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Codeeditor/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "codeeditor";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "codeeditor",
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
		if (name==="mode"){
			if (this.editor && this.editor.jsEditor){
				this.editor.jsEditor.setMode(this.json.mode);
			}
		}
		if (name==="lineNumber"){
			if (this.editor && this.editor.jsEditor){
				if (this.json.lineNumber){
					this.editor.jsEditor.showLineNumbers();
				}else{
					this.editor.jsEditor.hideLineNumbers();
				}
			}
		}
		if (name==="scriptEditor"){
			const forceType = (this.json.scriptEditor !== "monaco" && this.json.scriptEditor !== "ace") ? null : this.json.scriptEditor;
			if (this.editor){
				this.editor.options.forceType = forceType;
			}
			if (this.editor && this.editor.jsEditor){
				this.editor.jsEditor.options.forceType = forceType;
				this.editor.jsEditor.reload();
			}
		}

		if (name==="isReadonly"){
			if (this.editor){
				this.editor.setReadonly(this.json.isReadonly);
			}
		}
	},
	

	_initModule: function(){
		this.node.empty();

		this.loadCodeeditor();
		this._setNodeProperty();
        if (!this.form.isSubform) this._createIconAction() ;
		this._setNodeEvent();
	},

	loadCodeeditor(){
		if (this.editor || this.editorLoading) return;
		this.editorLoading = true;
		MWF.require("MWF.widget.ScriptArea", function(){
			this.editor = new MWF.widget.ScriptArea(this.node, {
				"title": this.json.title || MWF.xApplication.process.FormDesigner.LP.modules.codeedit,
				"isbind": false,
				"forceType": (this.json.scriptEditor !== "monaco" && this.json.scriptEditor !== "ace") ? null : this.json.scriptEditor,
				"maxPosition": "absolute",
				"mode": this.json.mode || "javascript",

				"onChange": function(){

				}.bind(this),
				"onSave": function(){

				}.bind(this),
				"style": this.json.style || "v10"
			});
			this.editor.load();

			this.form.designer.addEvent("queryClose", function(){
				console.log('app closed destroy ... ');
				debugger;
				if (this.editor) this.editor?.destroy?.()
			}.bind(this));
			this.editorLoading = false;
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
	},
	_preprocessingModuleData: function(){
		this.node.clearStyles();
		//if (this.initialStyles) this.node.setStyles(this.initialStyles);
		this.json.recoveryStyles = Object.clone(this.json.styles);

		if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
			if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
				//需要运行时处理
			}else{
				this.node.setStyle(key, value);
				delete this.json.styles[key];
			}
		}.bind(this));

		if (this.editor){
			this.editor.destroy();
			this.editor = null;
			this.node.empty();
		}
		this.json.preprocessing = "y";
	},
	_recoveryModuleData: function(){
		if (this.json.recoveryStyles) this.json.styles = this.json.recoveryStyles;
		this.json.recoveryStyles = null;
		this.loadCodeeditor();
	}
});
