MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("cms.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.cms.FormDesigner.Module.Attachment = MWF.CMSFCAttachment = new Class({
	Extends: MWF.CMSFC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Attachment/attachment.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_cms_FormDesigner/Module/Attachment/";
		this.cssPath = "/x_component_cms_FormDesigner/Module/Attachment/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "attachment";

		this.form = form;
		this.container = null;
		this.containerNode = null;
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
		};
	},

	_initModule: function(){
		this.node.empty();
		this.loadAttachmentController(this.json.editorProperties);
		this._setNodeProperty();
		this._createIconAction();
		this._setNodeEvent();
	},
    loadAttachmentController: function(){
        MWF.require("MWF.widget.AttachmentController", function(){
            this.attachmentController = new MWF.widget.ATTER(this.node, this, {"style":"cms","readonly": true, "size": this.json.size});
            this.attachmentController.load();
        }.bind(this));
    }
});
