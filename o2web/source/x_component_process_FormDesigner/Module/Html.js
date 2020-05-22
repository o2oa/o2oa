MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Html = MWF.FCHtml = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Html/html.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Html/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Html/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "html";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "html",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"text": ""
		}).inject(this.form.container);
		
		this.textarea = new Element("textarea", {
			"styles":{
				"background": "transparent",
				"border": "0px",
				"width": "100%",
				"overflow": "hidden",
				"cursor": "pointer",
				"webkit-user-select": "text",
				"moz-user-select": "text",
				"font-size": "12px",
				"color": "#193ee1",
				"height": "18px",
				"line-height": "18px"
			}
		}).inject(this.moveNode);
	},
	_loadNodeStyles: function(){
		this.textarea = this.node.getFirst("textarea");
		this.textarea.setStyles({
			"background": "transparent",
			"border": "0px",
			"width": "100%",
			"overflow": "hidden",
			"cursor": "pointer",
			"webkit-user-select": "text",
			"moz-user-select": "text",
			"font-size": "12px",
			"color": "#193ee1",
			"height": "18px",
			"line-height": "18px"
		});
        this.textarea.set("value", this.json.text);
	},
	_setNodeProperty: function(){
		this.textarea.set("value", this.json.text);
		if (this.property){
			var editNode = this.property.propertyNode.getElement(".MWF_editHtmlText");
			if (editNode) editNode.set("value", this.textarea.get("value"));
		}
		this._setTextareaHeight();
	},
	_createNode: function(){
		this.node = this.moveNode.clone(true, true);
		this.node.setStyles(this.css.moduleNode);
		this.textarea = this.node.getFirst("textarea");
		this.textarea.set("value", this.json.text);
	},
	_setTextareaHeight: function(){

		this.textarea.setStyle("height", "18px");
		var scroll = this.textarea.getScrollSize();
		var size = this.textarea.getSize();
		
		if (scroll.y>size.y){
			this.textarea.setStyle("height", ""+scroll.y+"px");
		}
	},
	_setOtherNodeEvent: function(){

		this.textarea.focus();
		
		this.textarea.addEvents({
			"keydown": function(e){
				this._setTextareaHeight();
				e.stopPropagation();
			}.bind(this),
			"keyup": function(e){
				if (e.code==8 || e.code==46 || (e.control && e.code==88)){
					this._setTextareaHeight();
				}
				if (this.property){
					var editNode = this.property.propertyNode.getElement(".MWF_editHtmlText");
					if (editNode) editNode.set("value", this.textarea.get("value"));
				}
				// var editNode = $("editHtmlText");
				// if (editNode) editNode.set("value", this.textarea.get("value"));
			}.bind(this),
			"change": function(){
				this.json.text = this.textarea.get("value");
			}.bind(this),
			"blur": function(){
				this.json.text = this.textarea.get("value");
			}.bind(this)
		});
	},
	_setEditStyle_custom: function(name){
		if (name=="text"){
			this.textarea.set("value", this.json.text);
			this._setTextareaHeight();
		}
	}
});
