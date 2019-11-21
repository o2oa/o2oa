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
        this.width = this.options.width;
        this.height = this.options.height;

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
	},
    reCenter: function(){
        var size = this.node.getSize();
        var container = $(document.body);

        if( this.options.positionNode && this.options.positionNode.getSize().y<$(document.body).getSize().y ){
            container = this.options.positionNode;
        }else if (layout.desktop.currentApp){
            container = layout.desktop.currentApp.content;
        }else{
            if (this.options.container){
                if (this.options.container.getSize().y<$(document.body).getSize().y){
                    container = this.options.container;
                }
            }
        }

        var p = o2.getCenter(size, container, container);
        if (p.y<0) p.y = 0;
        this.options.top = p.y;
        this.options.left = p.x;
        this.css.to.top = this.options.top+"px";
        this.css.to.left = this.options.left+"px";
        this.node.setStyles({
            "top": this.css.to.top,
            "left": this.css.to.left
        });
    },
    getOffsetY : function(node){
        return (node.getStyle("margin-top").toInt() || 0 ) +
            (node.getStyle("margin-bottom").toInt() || 0 ) +
            (node.getStyle("padding-top").toInt() || 0 ) +
            (node.getStyle("padding-bottom").toInt() || 0 )+
            (node.getStyle("border-top-width").toInt() || 0 ) +
            (node.getStyle("border-bottom-width").toInt() || 0 );
    },
    getOffsetX : function(node){
        return (node.getStyle("margin-left").toInt() || 0 ) +
            (node.getStyle("margin-right").toInt() || 0 ) +
            (node.getStyle("padding-left").toInt() || 0 ) +
            (node.getStyle("padding-right").toInt() || 0 )+
            (node.getStyle("border-left-width").toInt() || 0 ) +
            (node.getStyle("border-right-width").toInt() || 0 );
    },
    setContentHeightAuto : function(){
        var maxHeight = this.options.maxHeight || "98%";
        if( typeOf(maxHeight) === "string" && maxHeight.substr(maxHeight.length - 1, 1) === "%" ) {
            var containerHeight = ( this.options.positionNode || this.options.container || $(document.body)).getSize().y;
            maxHeight = parseInt(containerHeight * parseInt(maxHeight) / 100);
        }

        var offsetY = 0;
        var y = 0;
        //y = y + getOffsetY( this.title ) + this.title.getSize().y; //this.titleNode.getStyle("height").toInt();
        if( this.title )offsetY = offsetY + this.getOffsetY( this.title ) + this.title.getSize().y;
        if( this.bottom )offsetY = offsetY + this.getOffsetY( this.bottom ) + this.bottom.getSize().y;
        if( this.button )offsetY = offsetY + this.getOffsetY( this.button ) + this.button.getSize().y;
        if( this.content ){
            offsetY = offsetY + this.getOffsetY( this.content );
            y = offsetY + this.content.getSize().y;
        }else{
            y = offsetY;
        }


        if ( y > maxHeight) {
            this.options.height = maxHeight;
            this.options.contentHeight = null;
            this.options.fromTop = this.options.fromTop.toFloat() - offsetY / 2;
            this.options.top = this.options.top.toFloat() - offsetY / 2;
            this.css.to.height = maxHeight + "px";
            this.css.to.top = this.options.top + "px";
            this.css.from.top = this.options.fromTop + "px";
            this.node.setStyles({
                "height": maxHeight
            });
            if (this.content) {
                this.content.setStyles({
                    "height" : maxHeight - offsetY,
                    "overflow-y": "auto"
                })
            }
        }else{
            this.options.height = y;
            this.options.contentHeight = null;
            this.options.fromTop = this.options.fromTop.toFloat() - offsetY / 2;
            this.options.top = this.options.top.toFloat() - offsetY / 2;
            this.css.to.height = y + "px";
            this.css.to.top = this.options.top + "px";
            this.css.from.top = this.options.fromTop + "px";
            this.node.setStyles({
                "height": y
            });
            if (this.content) {
                this.content.setStyles({
                    "height" : y - offsetY,
                    "overflow-y": "hidden"
                })
            }
        }
    },
    setContentHeight: function(height){
        var nodeHeight;
        if (!height){
            if (this.options.contentHeight){
                nodeHeight = height = this.options.contentHeight.toFloat();
            }else{
                height = this.options.height.toFloat();
            }
        }

        var offsetHeight = 0;
        if (this.title){
            var h1 = this.title.getSize().y;
            var ptop1 = this.title.getStyle("padding-top").toFloat();
            var pbottom1 = this.title.getStyle("padding-bottom").toFloat();
            var mtop1 = this.title.getStyle("margin-top").toFloat();
            var mbottom1 = this.title.getStyle("margin-bottom").toFloat();
            offsetHeight += h1 + ptop1 + pbottom1 + mtop1 + mbottom1;
        }
        if (this.bottom){
            var h2 = this.bottom.getSize().y;
            var ptop2 = this.bottom.getStyle("padding-top").toFloat();
            var pbottom2 = this.bottom.getStyle("padding-bottom").toFloat();
            var mtop2 = this.bottom.getStyle("margin-top").toFloat();
            var mbottom2 = this.bottom.getStyle("margin-bottom").toFloat();

            offsetHeight += h2 + ptop2 + pbottom2 + mtop2 + mbottom2;
        }
        if (this.button){
            var h3 = this.button.getSize().y;
            var ptop3 = this.button.getStyle("padding-top").toFloat();
            var pbottom3 = this.button.getStyle("padding-bottom").toFloat();
            var mtop3 = this.button.getStyle("margin-top").toFloat();
            var mbottom3 = this.button.getStyle("margin-bottom").toFloat();

            offsetHeight += h3 + ptop3 + pbottom3 + mtop3 + mbottom3;
        }

        var ptop4 = this.content.getStyle("padding-top").toFloat();
        var pbottom4 = this.content.getStyle("padding-bottom").toFloat();
        var mtop4 = this.content.getStyle("margin-top").toFloat();
        var mbottom4 = this.content.getStyle("margin-bottom").toFloat();
        offsetHeight += ptop4 + pbottom4 + mtop4 + mbottom4;

        if (nodeHeight){
            nodeHeight = nodeHeight + offsetHeight+2;
        }else {
            height = height - offsetHeight;
        }

        if (nodeHeight) {
            this.options.height = nodeHeight;
            this.options.contentHeight = null;
            this.options.fromTop = this.options.fromTop.toFloat()-offsetHeight/2;
            this.options.top = this.options.top.toFloat()-offsetHeight/2;
            this.css.to.height = nodeHeight+"px";
            this.css.to.top = this.options.top+"px";
            this.css.from.top = this.options.fromTop+"px";
        }
        if (nodeWidth){
            this.options.width = nodeWidth;
            this.options.contentWidth = null;
            this.options.fromLeft = this.options.fromLeft.toFloat()-offsetWidth/2;
            this.options.left = this.options.left.toFloat()-offsetWidth/2;
            this.css.to.width = nodeWidth+"px";
            this.css.to.left = this.options.left+"px";
            this.css.from.left = this.options.fromLeft+"px";
        }

        if (!height || height<0){
            this.content.setStyles({"overflow": "hidden", "height": "auto"});
            height = this.content.getSize().y;
            var h = height + h1 + ptop1 + pbottom1 + mtop1 + mbottom1;
            h = h + h2 + ptop2 + pbottom2 + mtop2 + mbottom2;
            h = h + h3 + ptop3 + pbottom3 + mtop3 + mbottom3;
            h = h + ptop4 + pbottom4 + mtop4 + mbottom4;
            this.css.to.height = h;
        }else{
            this.content.setStyles( {"height" : height} )
        }

    },
    setContentWidthAuto : function(){
        var maxWidth = this.options.maxWidth || "100%";
        if( typeOf(maxWidth) === "string" && maxWidth.substr(maxWidth.length - 1, 1) === "%" ) {
            var containerWidth = ( this.options.positionNode || this.options.container || $(document.body)).getSize().x;
            maxWidth = parseInt(containerWidth * parseInt(maxWidth) / 100);
        }

        var offsetX = 0;
        var x = 0;
        if( this.content ){
            offsetX = offsetX + this.getOffsetX( this.content );
            x = offsetX + this.content.getSize().x;
        }else{
            x = offsetX;
        }


        if ( x > maxWidth) {
            this.options.width = maxWidth;
            this.options.contentWidth = null;
            this.options.fromLeft = this.options.fromLeft.toFloat() - offsetX / 2;
            this.options.left = this.options.left.toFloat() - offsetX / 2;
            this.css.to.width = maxWidth + "px";
            this.css.to.left = this.options.left + "px";
            this.css.from.left = this.options.fromLeft + "px";
            this.node.setStyles({
                "width": maxWidth
            });
            if (this.content) {
                this.content.setStyles({
                    "width" : maxWidth - offsetX,
                    "overflow-x": "auto"
                })
            }
        }else{
            this.options.width = x;
            this.options.contentHeight = null;
            this.options.fromLeft = this.options.fromLeft.toFloat() - offsetX / 2;
            this.options.left = this.options.left.toFloat() - offsetX / 2;
            this.css.to.width = x + "px";
            this.css.to.left = this.options.left + "px";
            this.css.from.left = this.options.fromLeft + "px";
            this.node.setStyles({
                "width": x
            });
            if (this.content) {
                this.content.setStyles({
                    "width" : x - offsetX,
                    "overflow-x": "hidden"
                })
            }
        }
    },
    setContentWidth: function(width){
        var nodeWidth;
        if (!width){
            if (this.options.contentWidth){
                nodeWidth = width = this.options.contentWidth.toFloat();
            }else{
                width = this.options.width.toFloat();
            }
        }


        var offsetWidth = 0;

        //if (this.content.getParent().getStyle("overflow-x")!="hidden" ) height = height-18;

        var pleft = this.content.getStyle("padding-left").toFloat();
        var pright = this.content.getStyle("padding-right").toFloat();
        var mleft = this.content.getStyle("margin-left").toFloat();
        var mright = this.content.getStyle("margin-right").toFloat();
        offsetWidth = pleft+pright+mleft+mright;
        //width = width-pleft-pright-mleft-mright;
        //if (this.content.getParent().getStyle("overflow-y")!="hidden" ) width = width-18;
        if (nodeWidth){
            nodeWidth = nodeWidth+offsetWidth;
        }else{
            var x = width;
            width = width-offsetWidth;
        }

        if (nodeWidth){
            this.options.width = nodeWidth;
            this.options.contentWidth = null;
            this.css.to.left = this.options.left+"px";
            this.css.from.left = this.options.fromLeft+"px";
            this.css.to.width = nodeWidth+"px";
            this.options.fromLeft = this.options.fromLeft.toFloat()-offsetWidth/2;
            this.options.left = this.options.left.toFloat()-offsetWidth/2;
            this.node.setStyle("width", nodeWidth )
        }else{
            this.node.setStyle("width", x )
        }

        this.content.setStyles( {"width" : width} )

    },
    setContentSize: function(height, width){
        //this.content.setStyle("height", this.getContentSize(height));
        // if (!this.options.height && !height){
        //    this.content.setStyle("height", "auto");
        //    this.content.setStyle("overflow", "hidden");
        //    this.content.setStyle("width", "auto");
        // }else{
        var y = height;
        if (!y){
            if (this.options.contentHeight){
                y = this.options.contentHeight;
            }else{
                y = this.height;
            }
        }

        var x = width;
        if (!x){
            if (this.options.contentWidth){
                x = this.options.contentWidth;
            }else{
                x = this.width;
            }
        }
        if( y === "auto" || x === "auto" ){
            if( y === "auto" ){
                this.setContentHeightAuto();
            }else{
                this.setContentHeight( height );
            }
            if( x === "auto" ){
                this.setContentWidthAuto();
            }else{
                this.setContentWidth( width );
            }
        }else{
            this.content.setStyles(this.getContentSize(height, width));
            this.content.setStyle("width", "auto");
        }
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

    if( options.width !== "auto" && options.height !== "auto" ){
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
    }else{
        if(options.top===undefined )options.top = 0;
        if(options.left===undefined)options.left = 0;
        if(options.fromTop===undefined)options.fromTop = 0;
        if(options.fromLeft===undefined)options.fromLeft = 0;
    }


    options.mark = !(options.mask===false);

    var dlg = new o2.DDL(options);
    if( options.width === "auto" || options.height === "auto" ){
        dlg.reCenter();
    }
    dlg.show();
    return dlg;
};