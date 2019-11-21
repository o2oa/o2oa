MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Attachment = MWF.FCAttachment = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Attachment/attachment.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Attachment/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Attachment/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "attachment";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	setTemplateStyles: function(styles){
		this.json.style = styles.style || "default";
	},
	clearTemplateStyles: function(styles){
		this.json.style = "default";
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "itmleditor",
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
		if (name=="size"){
            if (this.json[name]=="min"){
                this.attachmentController.changeControllerSizeToMin();
            }else{
                this.attachmentController.changeControllerSizeToMax();
            }
		}else if(name=="toolbarGroupHidden"){
			this.attachmentController.resetToolbarGroupHidden( this.json[name] );
		}else if( name=="availableListStyles" ){
			this.attachmentController.resetToolbarAvailableListStyle( this.json[name] );
		}
	},

	_initModule: function(){
		this.node.empty();
		this.loadAttachmentController(this.json.editorProperties);
		this._setNodeProperty();
        if (!this.form.isSubform) this._createIconAction();
		this._setNodeEvent();
	},
    loadAttachmentController: function(){
        MWF.require("MWF.widget.AttachmentController", function(){
            this.attachmentController = new MWF.widget.ATTER(this.node, this, {
				"readonly": true,
				"size": this.json.size,
				"toolbarGroupHidden" : this.json.toolbarGroupHidden || [],
				"availableListStyles" : this.json.availableListStyles || ["list","seq","icon","preview"]
            });
            this.attachmentController.load();
        }.bind(this));
    }
});
