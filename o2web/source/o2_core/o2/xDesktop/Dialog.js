MWF.require("MWF.widget.Dialog", null, false);
MWF.xDesktop.Dialog = new Class({
	Extends: MWF.widget.Dialog,
//	_markShow: function(){
//
//		if (this.options.mark){
//			if (!this.markNode){
//				
//				this.markNode = new Element("div", {
//					styles: this.css.mark
//				}).inject($(document.body));
//				
//			}
////			if (this.options.markNode){
////				var size = this.options.markNode.getComputedSize();
////				var position = this.options.markNode.getPosition();
////				alert(size.totalHeight);
////				this.markNode.set("styles", {
////					"height": size.totalHeight+"px",
////					"width": size.totalWidth+"px",
////					"top": position.y,
////					"height": position.x
////				});
////
////			}else{
//				var size = MWF.getMarkSize();
//				this.markNode.set("styles", {
//					"height": size.y,
//					"width": size.x,
//					"top": "0xp",
//					"height": "0px"
//				});
////			}
//			
//			this.markNode.setStyle("display", "block");
//		}
//	},
	_markShow: function(){

		if (this.options.mark){
			if (!this.markNode){
				var size = MWF.getMarkSize(this.options.maskNode);
				var topNode = this.options.container || $(document.body);
				this.markNode = new Element("iframe", {
					styles: this.css.mark
				}).inject(topNode);
				this.markNode.set("styles", {
					"height": size.y,
					"width": size.x
				});
			}
			this.markNode.setStyle("display", "block");
		}
	},
	getDialogNode: function(){
		this.node.set("styles", this.css.from);
		var topNode = this.options.container || $(document.body);
		this.node.inject(topNode);
//		this.node.addEvent("selectstart", function(e){
//		//	e.preventDefault();
//		});

		this.title = this.node.getElement(".MWF_dialod_title");
		this.titleCenter = this.node.getElement(".MWF_dialod_title_center");
		this.titleText = this.node.getElement(".MWF_dialod_title_text");
		this.titleAction = this.node.getElement(".MWF_dialod_title_action");
		this.content = this.node.getElement(".MWF_dialod_content");
		this.bottom = this.node.getElement(".MWF_dialod_bottom");
		this.resizeNode = this.node.getElement(".MWF_dialod_bottom_resize");
		this.button = this.node.getElement(".MWF_dialod_button");

		if (this.title) this.setTitleEvent(); 
	//	if (this.titleText) this.getTitle();
		if (this.content) this.getContent();
		if (this.titleAction) this.getAction();
		if (this.resizeNode) this.setResizeNode();
	//	if (this.button) this.getButton();

		if (this.content) this.setContentSize();
	}
});
