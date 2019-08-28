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
	},
	
	_setEditStyle_custom: function(name){
	},

	_initModule: function(){
		this.node.empty();

		var pageNode = new Element("div.doc_layout_page", {"styles": this.css.doc_page}).inject(this.node);
		var pageContentNode = new Element("div.doc_layout_page_content", {"styles": this.css.doc_layout_page_content}).inject(pageNode);

		var html = '<div class="doc_block doc_layout_redHeader">文件红头</div>' +
			"<div class=\"doc_block doc_layout_fileno\">[文号]</div>" +
			"<div color=\"#ff0000\" class=\"doc_block doc_layout_redline\"></div>" +
			"<div class=\"doc_block doc_layout_subject\">[文件标题]</div>" +
			"<div class=\"doc_block doc_layout_mainSend\">[主送单位：]</div>"+
			"<div class=\"doc_block doc_layout_filetext\">　　[正文内容]</div>";
		pageContentNode.set("html", html);

		pageContentNode.getElement(".doc_layout_redHeader").setStyles(this.css.doc_layout_redHeader);
		pageContentNode.getElement(".doc_layout_fileno").setStyles(this.css.doc_layout_fileno);
		pageContentNode.getElement(".doc_layout_redline").setStyles(this.css.doc_layout_redline);
		pageContentNode.getElement(".doc_layout_subject").setStyles(this.css.doc_layout_subject);
		pageContentNode.getElement(".doc_layout_mainSend").setStyles(this.css.doc_layout_mainSend);
		pageContentNode.getElement(".doc_layout_filetext").setStyles(this.css.doc_layout_filetext);

		this._setNodeProperty();
        if (!this.form.isSubform) this._createIconAction() ;
		this._setNodeEvent();
	}
});
