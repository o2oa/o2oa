MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Documenteditor = MWF.FCDocumenteditor = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Documenteditor/documenteditor.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Documenteditor/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Documenteditor/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "documenteditor";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "documenteditor",
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
			//CKEDITOR.disableAutoInline = true;
			var toolbaeDiv = new Element("div").inject(this.node);
			var editorDiv = new Element("div").inject(this.node);
            if (this.json.templateCode) editorDiv.set("html", this.json.templateCode);

			var height = this.node.getSize().y;
			var editorConfig = config || {
				"bodyClass": "document-editor"
			};
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

			editorConfig.toolbarGroups = [
				{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
				{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
				{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
				{ name: 'forms', groups: [ 'forms' ] },
				{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
				{ name: 'insert', groups: [ 'insert' ] },
				{ name: 'tools', groups: [ 'tools' ] },
				'/',
				{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
				{ name: 'links', groups: [ 'links' ] },
				'/',
				{ name: 'styles', groups: [ 'styles' ] },
				{ name: 'colors', groups: [ 'colors' ] },
				{ name: 'others', groups: [ 'others' ] },
				{ name: 'about', groups: [ 'about' ] }
			];
			editorConfig.removeButtons = 'Source,Templates,Scayt,Form,Bold,Italic,Underline,Strike,Subscript,Superscript,CopyFormatting,RemoveFormat,Indent,Outdent,Blockquote,CreateDiv,BidiLtr,BidiRtl,Language,Link,Unlink,Anchor,Flash,HorizontalRule,Smiley,SpecialChar,Iframe,PageBreak,Styles,Format,Font,FontSize,TextColor,BGColor,About,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField';


			editorConfig.bodyClass = "document-editor";
			editorConfig.contentsCss = [ '/x_desktop/mystyles.css' ];
			editorConfig.extraPlugins = 'ecnet';
			editorConfig.height = "230";
			this.editor = CKEDITOR.replace(editorDiv, editorConfig);

			this.editor.on("dataReady", function(){
				this.editor.setReadOnly(true);
			}, this);
		}.bind(this));
	},
	distroyCkeditor: function(){
		if (this.editor) this.editor.destroy();
		this.editor = null;
	}
});
