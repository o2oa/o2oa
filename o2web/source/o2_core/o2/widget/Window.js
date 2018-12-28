o2.widget = o2.widget || {};
o2.require("o2.widget.Dialog", null, false);
o2.widget.Window = new Class({
	Extends: o2.widget.Dialog,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"title": "window",
		"width": "600",
		"height": "500",
		"top": "0",
		"left": "0",
		"fromTop": "0",
		"fromLeft": "0",
		"mark": false,

		"html": "",
		"text": "",
		"url": "",
		"content": null,

		"isMax": true,
		"isClose": true,
		"isResize": true,
		"isMove": true,
		
		"buttons": null,
		"buttonList": null
	},
	initialize: function(options){
		var position = layout.desktop.desktopNode.getPosition();
		var size = layout.desktop.desktopNode.getSize();
		
		this.options.top = parseFloat(this.options.top)+position.y;
		this.options.fromTop = parseFloat(this.options.fromTop)+position.y;
		
		this.parent(options);
	},
	
	getDialogNode: function(){
		this.node.set("styles", this.css.from);
		this.node.inject($(document.body));

		this.title = this.node.getElement(".MWF_dialod_title");
		this.titleCenter = this.node.getElement(".MWF_dialod_title_center");
		this.titleText = this.node.getElement(".MWF_dialod_title_text");
		this.titleAction = this.node.getElement(".MWF_dialod_title_action");
		this.content = this.node.getElement(".MWF_dialod_content");
		this.bottom = this.node.getElement(".MWF_dialod_bottom");
		this.resizeNode = this.node.getElement(".MWF_dialod_bottom_resize");
		this.button = this.node.getElement(".MWF_dialod_button");

		if (this.title){
			this.title.addEvent("mousedown", function(){
				this.containerDrag = new Drag.Move(this.node);
			}.bind(this));
			this.title.addEvent("mouseup", function(){
				this.node.removeEvents("mousedown");
				this.title.addEvent("mousedown", function(){
					this.containerDrag = new Drag.Move(this.node);
				}.bind(this));
			}.bind(this));
		}

	//	if (this.titleText) this.getTitle();
		if (this.content) this.getContent();
		if (this.titleAction) this.getAction();
		if (this.resizeNode) this.setResizeNode();
	//	if (this.button) this.getButton();

		if (this.content) this.setContentSize();
	}
});