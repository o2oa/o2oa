o2.require("MWF.widget.Dialog", null, false);
o2.xDesktop.Dialog = o2.DDL = new Class({
	Extends: o2.widget.Dialog,
    options : {
        maxHeightPercent : "98%"
    },
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
				if (!this.markNode_up) this.markNode_up = new Element("div", { styles: this.css.mark }).inject(topNode);
                this.markNode_up.set("styles", {
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
        if( (!this.options.buttonList || this.options.buttonList.length === 0) && ( !this.options.buttons ) ){
            this.button.setStyle("display","none");
            this.buttonDisable = true;
        }else{
            this.okButton = this.node.getElement(".MWF_dialod_ok_button");
            this.cancelButton = this.node.getElement(".MWF_dialod_cancel_button");
        }
        this.backAction = this.node.getElement(".MWF_dialod_Action_back");

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
        if (this.backAction)this.backAction.addEvent("click", this.close.bind(this))
	},
    getButton: function(){
        for (i in this.options.buttons){
            var button = new Element("input", {
                "type": "button",
                "value": i,
                "styles": this.css.button,
                "events": {
                    "click": this.options.buttons[i].bind(this)
                }
            }).inject(this.button);
        }
        if (this.options.buttonList){
            this.options.buttonList.each(function(bt){
                var styles = this.css.button;
                if( bt.type === "ok" && this.css.okButton )styles = this.css.okButton;
                if( bt.type === "cancel" && this.css.cancelButton )styles = this.css.cancelButton;
                if( bt.styles )styles = bt.styles;
                var button;
                if( bt.type === "ok" && this.okButton ){
                    button = this.okButton;
                    button.show();
                }else if( bt.type === "cancel" && this.cancelButton ){
                    button = this.cancelButton;
                    button.show();
                }else{
                    button = new Element("input", {
                        "type": "button"
                    }).inject(this.button);
                }
                button.set({
                    "value": bt.text,
                    "styles": styles,
                    "events": {
                        "click": function(e){bt.action.call(this, this, e)}.bind(this)
                    }
                })
            }.bind(this));
        }
    },
    setTitleEvent: function(){
        var content;
        if( this.options.isMove ){
            if (layout.app) content = layout.app.content;
            if (layout.desktop.currentApp) content = layout.desktop.currentApp.content;
            this.containerDrag = new Drag.Move(this.node, {
                "handle": this.title,
                "container": this.options.container || this.markNode || content,
                "snap": 5
            });
        }
    },
    getAction: function(){
        //未完成................................
        if (this.options.isClose){
            this.closeAction = new Element("div", {"styles": this.css.closeAction}).inject(this.titleAction);
            this.closeAction.addEvent("click", this.close.bind(this));
        }
        if (this.options.isMax){
            this.maxAction = new Element("div", {"styles": this.css.maxAction}).inject(this.titleAction);
            this.maxAction.addEvent("click", this.maxSize.bind(this));

            this.restoreAction = new Element("div", {"styles": this.css.restoreAction}).inject(this.titleAction);
            this.restoreAction.hide();
            this.restoreAction.addEvent("click", this.restoreSize.bind(this));

            if (this.title){
                this.title.addEvent("dblclick", function(){
                    this.switchMax();
                }.bind(this));
            }
        }
    },
    switchMax : function(){
        if( !this.isMax ){
            this.maxSize();
        }else{
            this.restoreSize();
        }
    },
    maxSize: function(){
        //if(!this.oldCoordinate)this.oldCoordinate = {
        //    height : this.options.height,
        //    width : this.options.width,
        //    top : this.options.top,
        //    left : this.options.left,
        //    fromTop : this.options.fromTop,
        //    fromLeft : this.options.fromLeft,
        //    contentHeight : this.options.contentHeight,
        //    contentWidth : this.options.contentWidth,
        //    maxHeightPercent : this.options.maxHeightPercent,
        //    maxHeight : this.options.maxHeight,
        //    maxWidth : this.options.maxWidth
        //};
        //if( !this.oldSize ){
        //    this.oldSize = {
        //        "width" : this.width,
        //        "height" : this.height
        //    }
        //}
        if( !this.oldNodeSize ){
            this.oldNodeSize = {
                "width" : this.nodeWidth,
                "height" : this.nodeHeight
            }
        }
        if( !this.oldContentSize ){
            this.oldContentSize = {
                "width" : this.contentWidth,
                "height" : this.contentHeight
            }
        }

        //this.options.top = 0;
        //this.options.left = 0;
        //this.options.fromTop = 0;
        //this.options.fromLeft = 0;
        //this.options.contentHeight = 0;
        //this.options.contentWidth = 0;
        //this.options.maxHeightPercent = null;
        //this.options.maxHeight = null;
        //this.options.maxWidth = null;

        //this.height = null;
        //this.width = null;

        var container = $(document.body);
        if (layout.desktop.currentApp){
            container = layout.desktop.currentApp.content;
        }else if (this.options.container){
            if (this.options.container.getSize().y<$(document.body).getSize().y){
                container = this.options.container;
            }
        }
        var containerSize = container.getSize();
        this.options.width = containerSize.x;
        this.options.height = containerSize.y;

        this.setContentSize( containerSize.y, containerSize.x );
        this.node.setStyles({
            width : containerSize.x + "px",
            height : containerSize.y + "px",
            top : "0px",
            left : "0px"
        });

        this.maxAction.setStyle("display","none");
        this.restoreAction.setStyle("display","");
        this.isMax = true;
        this.fireEvent("max");
    },
    restoreSize : function(){
        //if( this.oldCoordinate){
        //    this.options.height = this.oldCoordinate.height;
        //    this.options.width = this.oldCoordinate.width;
        //    this.options.top = this.oldCoordinate.top;
        //    this.options.left = this.oldCoordinate.left;
        //    this.options.fromTop = this.oldCoordinate.fromTop;
        //    this.options.fromLeft = this.oldCoordinate.fromLeft;
        //    this.options.contentHeight = this.oldCoordinate.contentHeight;
        //    this.options.contentWidth = this.oldCoordinate.contentWidth;
        //    this.options.maxHeightPercent = this.oldCoordinate.maxHeightPercent;
        //    this.options.maxHeight = this.oldCoordinate.maxHeight;
        //    this.options.maxWidth = this.oldCoordinate.maxWidth;
        //}
        //
        //if( this.oldSize ){
        //    this.width = this.oldSize.width;
        //    this.height = this.oldSize.height;
        //}

        //this.setContentSize( this.oldNodeSize.height, this.oldNodeSize.width );
        //this.node.setStyles( this.getNodeSize() );

        this.contentHeight = this.oldContentSize.height;
        this.contentWidth = this.oldContentSize.width;

        this.nodeHeight = this.oldNodeSize.height;
        this.nodeWidth = this.oldNodeSize.width;

        this.content.setStyles( this.oldContentSize );

        this.node.setStyles( this.oldNodeSize );

        this.reCenter();

        this.maxAction.setStyle("display","");
        this.restoreAction.setStyle("display","none");
        this.isMax = false;
        this.fireEvent("restore");
    },
    reCenter: function(){
        var size = this.node.getSize();

        if( this.options.positionWidth ){
            size.x = parseInt(this.options.positionWidth);
        }
        if( this.options.positionHeight ){
            size.y = parseInt(this.options.positionHeight);
        }

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
        if (p.y< ( this.options.minTop || 0 ) ) p.y = this.options.minTop || 0;
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
        var maxHeight = this.getMaxHeight();

        var offsetY = 0;
        var y = 0;
        //y = y + getOffsetY( this.title ) + this.title.getSize().y; //this.titleNode.getStyle("height").toInt();
        if( this.title )offsetY = offsetY + this.getOffsetY( this.title ) + this.title.getSize().y;
        if( this.bottom )offsetY = offsetY + this.getOffsetY( this.bottom ) + this.bottom.getSize().y;
        if( this.button && !this.buttonDisable )offsetY = offsetY + this.getOffsetY( this.button ) + this.button.getSize().y;
        if( this.content ){
            offsetY = offsetY + this.getOffsetY( this.content );
            y = offsetY + this.content.getSize().y;
        }else{
            y = offsetY;
        }


        if (  typeOf(maxHeight) === "number" && y > maxHeight) {
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
            this.contentHeight = maxHeight - offsetY;
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
            this.contentHeight = y - offsetY;
            if (this.content) {
                this.content.setStyles({
                    "height" : y - offsetY,
                    "overflow-y": "hidden"
                })
            }
        }
    },
    getMaxHeight : function(){
        var maxHeightPercent;
        if( this.options.maxHeightPercent ){
            maxHeightPercent = this.options.maxHeightPercent;
            if( typeOf(maxHeightPercent) === "string" && maxHeightPercent.substr(maxHeightPercent.length - 1, 1) === "%" ) {
                var containerHeight = ( this.options.positionNode || this.options.container || $(document.body)).getSize().y;
                maxHeightPercent = parseInt(containerHeight * parseInt(maxHeightPercent) / 100);
            }
        }

        var maxHeight;
        if( this.options.maxHeight && parseFloat( this.options.maxHeight ).toString() !== "NaN" ){
            maxHeight = parseFloat( this.options.maxHeight );
            if( typeOf(maxHeightPercent) === "number" ){
                maxHeight = Math.min( maxHeight, maxHeightPercent )
            }
        }else if( typeOf(maxHeightPercent) === "number" ){
            maxHeight = maxHeightPercent;
        }
        return maxHeight;
    },
    getNodeSize: function(){
        return {
            "height": this.nodeHeight+"px",
            "width": this.nodeWidth+"px"
        };
    },
    getContentSize: function(height, width){
        var nodeHeight, nodeWidth;
        if (!height){
            if (this.options.contentHeight){
                nodeHeight = height = this.options.contentHeight.toFloat();
                this.contentHeight = height;
            }else{
                height = this.options.height.toFloat();

                var maxHeight = this.getMaxHeight();
                if( typeOf(maxHeight) === "number" &&  maxHeight < height ){
                    height = maxHeight;
                }

                this.nodeHeight = height;
            }
        }else{
            this.nodeHeight = height;
        }
        if (!width){
            if (this.options.contentWidth){
                nodeWidth = width = this.options.contentWidth.toFloat();
                this.contentWidth = width;
            }else{
                width = this.options.width.toFloat();
                this.nodeWidth = width;
            }
        }else{
            this.nodeWidth = width;
        }

        var offsetHeight = 0;
        var offsetWidth = 0;
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
        if (this.button && !this.buttonDisable){
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
            width = width-offsetWidth;
        }


        if (nodeHeight) {
            this.nodeHeight = nodeHeight;
            this.options.height = nodeHeight;
            this.options.contentHeight = null;
            this.options.fromTop = this.options.fromTop.toFloat()-offsetHeight/2;
            this.options.top = this.options.top.toFloat()-offsetHeight/2;
            this.css.to.height = nodeHeight+"px";
            this.css.to.top = this.options.top+"px";
            this.css.from.top = this.options.fromTop+"px";
        }else{
            this.contentHeight = height;
        }

        if (nodeWidth){
            this.nodeWidth = nodeWidth;
            this.options.width = nodeWidth;
            this.options.contentWidth = null;
            this.options.fromLeft = this.options.fromLeft.toFloat()-offsetWidth/2;
            this.options.left = this.options.left.toFloat()-offsetWidth/2;
            this.css.to.width = nodeWidth+"px";
            this.css.to.left = this.options.left+"px";
            this.css.from.left = this.options.fromLeft+"px";
        }else{
            this.contentWidth = width;
        }

        if (!height || height<0){
            this.content.setStyles({"overflow": "hidden", "height": "auto", "width": ""+width+"px"});
            height = this.content.getSize().y;
            var h = height + h1 + ptop1 + pbottom1 + mtop1 + mbottom1;
            h = h + h2 + ptop2 + pbottom2 + mtop2 + mbottom2;
            h = h + h3 + ptop3 + pbottom3 + mtop3 + mbottom3;
            h = h + ptop4 + pbottom4 + mtop4 + mbottom4;
            this.css.to.height = h;
        }

//		var ptop5 = this.node.getStyle("padding-top").toFloat();
//		var pbottom5 = this.node.getStyle("padding-bottom").toFloat();
//		height = height - ptop5 - pbottom5;

        return {"height": height+"px", "width": width+"px"};
    },
    setContentHeight: function(height){
        var nodeHeight;
        if (!height){
            if (this.options.contentHeight){
                nodeHeight = height = this.options.contentHeight.toFloat();
                this.contentHeight = height;
            }else{
                height = this.options.height.toFloat();

                var maxHeight = this.getMaxHeight();
                if( typeOf(maxHeight) === "number" &&  maxHeight < height ){
                    height = maxHeight;
                }

                this.nodeHeight = height;
            }
        }else{
            this.nodeHeight = height;
        }

        var offsetHeight = 0;
        if (this.title){
            var h1 = this.title.getSize().y;
            //offsetHeight += h1 + this.getOffsetY(this.title);
            var ptop1 = this.title.getStyle("padding-top").toFloat();
            var pbottom1 = this.title.getStyle("padding-bottom").toFloat();
            var mtop1 = this.title.getStyle("margin-top").toFloat();
            var mbottom1 = this.title.getStyle("margin-bottom").toFloat();

            offsetHeight += h1 + ptop1 + pbottom1 + mtop1 + mbottom1;
        }
        if (this.bottom){
            var h2 = this.bottom.getSize().y;
            //offsetHeight += h2 + this.getOffsetY(this.bottom);
            var ptop2 = this.bottom.getStyle("padding-top").toFloat();
            var pbottom2 = this.bottom.getStyle("padding-bottom").toFloat();
            var mtop2 = this.bottom.getStyle("margin-top").toFloat();
            var mbottom2 = this.bottom.getStyle("margin-bottom").toFloat();

            offsetHeight += h2 + ptop2 + pbottom2 + mtop2 + mbottom2;
        }
        if (this.button && !this.buttonDisable){
            var h3 = this.button.getSize().y;
            //offsetHeight += h3 + this.getOffsetY(this.button);

            var ptop3 = this.button.getStyle("padding-top").toFloat();
            var pbottom3 = this.button.getStyle("padding-bottom").toFloat();
            var mtop3 = this.button.getStyle("margin-top").toFloat();
            var mbottom3 = this.button.getStyle("margin-bottom").toFloat();

            offsetHeight += h3 + ptop3 + pbottom3 + mtop3 + mbottom3;
        }

        //offsetHeight +=  this.getOffsetY(this.content);
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
            this.nodeHeight = nodeHeight;
            this.options.height = nodeHeight;
            this.options.contentHeight = null;
            this.options.fromTop = this.options.fromTop.toFloat()-offsetHeight/2;
            this.options.top = this.options.top.toFloat()-offsetHeight/2;
            this.css.to.height = nodeHeight+"px";
            this.css.to.top = this.options.top+"px";
            this.css.from.top = this.options.fromTop+"px";
        }else{
            this.contentHeight = height;
        }
        //if (nodeWidth){
        //    this.nodeWidth = nodeWidth;
        //    this.options.width = nodeWidth;
        //    this.options.contentWidth = null;
        //    this.options.fromLeft = this.options.fromLeft.toFloat()-offsetWidth/2;
        //    this.options.left = this.options.left.toFloat()-offsetWidth/2;
        //    this.css.to.width = nodeWidth+"px";
        //    this.css.to.left = this.options.left+"px";
        //    this.css.from.left = this.options.fromLeft+"px";
        //}else{
        //    this.contentWidth = width;
        //}

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
            this.nodeWidth = maxWidth;
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
            this.contentWidth = maxWidth - offsetX;
            if (this.content) {
                this.content.setStyles({
                    "width" : maxWidth - offsetX,
                    "overflow-x": "auto"
                })
            }
        }else{
            this.nodeWidth = x;
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
            this.contentWidth = x - offsetX;
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
                this.contentWidth = width;
            }else{
                width = this.options.width.toFloat();
                this.nodeWidth = width;
            }
        }else{
            this.nodeWidth = width;
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
            this.nodeWidth = nodeWidth;
            this.options.width = nodeWidth;
            this.options.contentWidth = null;
            this.css.to.left = this.options.left+"px";
            this.css.from.left = this.options.fromLeft+"px";
            this.css.to.width = nodeWidth+"px";
            this.options.fromLeft = this.options.fromLeft.toFloat()-offsetWidth/2;
            this.options.left = this.options.left.toFloat()-offsetWidth/2;
            this.node.setStyle("width", nodeWidth )
        }else{
            this.contentWidth = width;
            this.node.setStyle("width", x )
        }

        this.content.setStyles( {"width" : width} )

    },
    setContentSize: function(height, width){
        debugger;
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

        //如果是百分比
        if( "string" == typeOf(options.width)  && (1 < options.width.length && "%" == options.width.substr(options.width.length - 1, 1)) ){
            options.width = parseInt( container.getSize().x * parseInt(options.width, 10) / 100, 10);
        }

        if( "string" == typeOf(options.height)  && (1 < options.height.length && "%" == options.height.substr(options.height.length - 1, 1)) ){
            options.height = parseInt( container.getSize().y * parseInt(options.height, 10) / 100, 10);
        }

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