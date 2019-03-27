o2.require("MWF.widget.Dialog", null, false);
o2.xDesktop.Dialog = o2.DDL = new Class({
	Extends: o2.widget.Dialog,
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

        if (!this.options.isTitle) {
            this.title.destroy();
            this.title = null;
            this.titleCenter = null;
            this.titleRefresh = null;
            this.titleText = null;
            this.titleAction = null;
        }

		if (this.title) this.setTitleEvent(); 
	//	if (this.titleText) this.getTitle();
		if (this.content) this.getContent();
		if (this.titleAction) this.getAction();
		if (this.resizeNode) this.setResizeNode();
	//	if (this.button) this.getButton();

		if (this.content) this.setContentSize();
	}
});
o2.DL.open = function(options){
    if (!options) options = {};
    if (!options.style) options.style = "user";
    //if (!options.transition) options.transition = Fx.Transitions.Back.easeOut;
    if (!options.duration) options.duration = 200;
    if (options.isClose!==false) options.isClose = true;

    var size;
    if (!options.width && !options.contentWidth){
        if (options.content){
            options.content.show();
            size = options.content.getComputedSize();
            options.contentWidth = size.totalWidth.toFloat();
        }
    }
    if (!options.height && !options.contentHeight){
        if (options.content){
            if (!size){
                options.content.show();
                size = options.content.getComputedSize();
            }
            options.contentHeight = size.totalHeight.toFloat()+2;
        }
    }
    if (!options.width && !options.contentWidth) options.width = 300;
    if (!options.height && !options.contentHeight) options.height = 150;

    if (!options.container && layout){
        if (layout.desktop.currentApp){
            options.container = layout.desktop.currentApp.content;
        }
    }
    var container = (options.positionNode || options.container || $(document.body));

    if ((options.top===undefined ) && (options.left===undefined)){
        var p = o2.getCenter({"x":(options.width || options.contentWidth), "y": (options.height || options.contentHeight+120)}, container, container);
        options.top = (p.y<0) ? 0 : p.y;
        options.left  = (p.x<0) ? 0 : p.x;
    }
    if ((options.fromTop===undefined ) && (options.fromLeft===undefined)){
        var p = o2.getCenter({"x":(options.width || options.contentWidth)*0, "y": (options.height || options.contentHeight+120)*0}, container, container);
        options.fromTop = (p.y<0) ? 0 : p.y;
        options.fromLeft  = (p.x<0) ? 0 : p.x;
    }
    if (options.offset){
        if (options.offset.y){
            options.top = options.top+options.offset.y.toInt();
            options.fromTop = options.fromTop+options.offset.y.toInt();
        }
        if (options.offset.x){
            options.left = options.left+options.offset.x.toInt();
            options.fromLeft = options.fromLeft+options.offset.x.toInt();
        }
    }
    if (options.top<0) options.top = 0;
    if (options.left<0) options.left = 0;
    if (options.fromTop<0) options.fromTop = 0;
    if (options.fromLeft<0) options.fromLeft = 0;

    options.mark = !(options.mask===false);

    var dlg = new o2.DDL(options);
    dlg.show();
    return dlg;
};