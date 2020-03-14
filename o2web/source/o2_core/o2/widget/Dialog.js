o2.widget = o2.widget || {};
o2.widget.Dialog = o2.DL = new Class({
	Implements: [Options, Events],
    Extends: o2.widget.Common,
	options: {
		"style": "default",
		"title": "dialog",
		"width": "300",
		"height": "150",
		"contentWidth": null,
		"contentHeight": null,
		"top": "0",
		"left": "0",
		"fromTop": "0",
		"fromLeft": "0",
		"mark": true,

		"html": "",
		"text": "",
		"url": "",
		"content": null,

		"isMax": false,
		"isClose": false,
		"isResize": true,
		"isMove": true,
        "isTitle": true,
		
		"buttons": null,
		"buttonList": null,
        "maskNode" : null,

		"transition": null,
		"duration": 200,

        "container": null
	},
	initialize: function(options){
		this.setOptions(options);

		this.path = o2.session.path+"/widget/$Dialog/";
		this.cssPath = o2.session.path+"/widget/$Dialog/"+this.options.style+"/css.wcss";

		this._loadCss();
		
		this.reStyle();
//		this.css.to.height = this.options.height;
//		this.css.to.width = this.options.width;
//		this.css.to.top = this.options.top;
//		this.css.to.left = this.options.left;
//		this.css.to.top = this.options.top;
//		this.css.from.top = this.options.fromTop;
//		this.css.from.left = this.options.fromLeft;
		
		this.fireEvent("queryLoad");

		this.getContentUrl();
		var request = new Request.HTML({
			url: this.contentUrl,
			method: "GET",
			async: false,
			onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
				this.node = responseTree[0];
				this.getDialogNode();
				this.fireEvent("postLoad");
			}.bind(this),
			onFailure: function(xhr){
				alert(xhr);
			}
		});
		request.send();
	},
	getContentUrl: function(){
		this.contentUrl = o2.session.path+"/widget/$Dialog/"+this.options.style+"/dialog.html";
	},
	reStyle: function(options){
		if (options) this.setOptions(options);
		this.css.to.height = this.options.height+"px";
		this.css.to.width = this.options.width+"px";
		this.css.to.top = this.options.top+"px";
		this.css.to.left = this.options.left+"px";
		//this.css.to.top = this.options.top+"px";
		this.css.from.top = this.options.fromTop+"px";
		this.css.from.left = this.options.fromLeft+"px";

		if (this.node) this.node.set("styles", this.css.from);
	},

    getParentSelect: function(node){
        var select = "";
        var pnode = node.getParent();
        while (!select && pnode){
            select = pnode.getStyle("-webkit-user-select");
            var pnode = pnode.getParent();
        }
        return select;
    },
	getDialogNode: function(){
		this.node.set("styles", this.css.from);
		this.node.inject(this.options.container || $(document.body));
		this.node.addEvent("selectstart", function(e){
			var select = e.target.getStyle("-webkit-user-select");
            if (!select) select = this.getParentSelect(e.target);
			if (!select){
				select = "none";
			}else{
				select = select.toString().toLowerCase();
			}
			var tag = e.target.tagName.toString().toLowerCase();
			if (select!="text" && select!="auto" && ["input", "textarea"].indexOf(tag)==-1) e.preventDefault();
			
        }.bind(this));

		this.title = this.node.getElement(".MWF_dialod_title");
		this.titleCenter = this.node.getElement(".MWF_dialod_title_center");
        this.titleRefresh = this.node.getElement(".MWF_dialod_title_refresh");
		this.titleText = this.node.getElement(".MWF_dialod_title_text");
		this.titleAction = this.node.getElement(".MWF_dialod_title_action");
        this.under = this.node.getElement(".MWF_dialod_under");
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

		if (this.title) this.title.setStyles(this.css.MWF_dialod_title);
        if (this.titleCenter) this.titleCenter.setStyles(this.css.MWF_dialod_title_center);
        if (this.titleRefresh) this.titleRefresh.setStyles(this.css.MWF_dialod_title_refresh);
        if (this.titleText) this.titleText.setStyles(this.css.MWF_dialod_title_text);
        if (this.titleAction) this.titleAction.setStyles(this.css.MWF_dialod_title_action);
        if (this.under) this.under.setStyles(this.css.MWF_dialod_under);
        if (this.content) this.content.setStyles(this.css.MWF_dialod_content);
        if (this.bottom) this.bottom.setStyles(this.css.MWF_dialod_bottom);
        if (this.resizeNode) this.resizeNode.setStyles(this.css.MWF_dialod_bottom_resize);
        if (this.button) this.button.setStyles(this.css.MWF_dialod_button);


		if (this.title) this.setTitleEvent();
        if (this.titleRefresh) this.setTitleRefreshNode();
	//	if (this.titleText) this.getTitle();
		if (this.content) this.getContent();
		if (this.titleAction) this.getAction();
		if (this.resizeNode) this.setResizeNode();
	//	if (this.button) this.getButton();

		if (this.content) this.setContentSize();
	},
    setTitleRefreshNode: function(){
        this.titleRefresh.setStyles(this.css.titleRefresh);
        this.titleRefresh.set("title", o2.LP.widget.refresh);
    },
	setTitleEvent: function(){
		var content;
		if (layout.app) content = layout.app.content;
		if (layout.desktop.currentApp) content = layout.desktop.currentApp.content;
		this.containerDrag = new Drag.Move(this.node, {
			"handle": this.title,
			"container": this.options.container || this.markNode || content,
			"snap": 5
		});

		// this.title.addEvent("mousedown", function(e){
        //     var content;
        //     if (layout.app) content = layout.app.content;
        //     if (layout.desktop.currentApp) content = layout.desktop.currentApp.content;
		// 	this.containerDrag = new Drag.Move(this.node, {
        //         "container": content
        //     });
		// 	this.containerDrag.start();
		// }.bind(this));
		// this.title.addEvent("mouseup", function(){
		// 	this.node.removeEvents("mousedown");
		// 	this.title.addEvent("mousedown", function(){
		// 		var content;
		// 		if (layout.app) content = layout.app.content;
		// 		if (layout.desktop.currentApp) content = layout.desktop.currentApp.content;
        //         this.containerDrag = new Drag.Move(this.node, {
        //             "container": content
        //         });
		// 		this.containerDrag.start();
		// 	}.bind(this));
		// }.bind(this));
	},
	setResizeNode: function(){
		//未实现................................
        if (!this.options.isResize){
            if (this.resizeNode) this.resizeNode.hide();
        }else{
            if (this.resizeNode){
                this.node.makeResizable({
                    "handle": this.resizeNode || this.bottom,
                    "limit": {x:[200, null], y:[150, null]},
                    "onDrag": function(){
                        var size = this.node.getComputedSize();
                        // this.css.to.width = size.totalWidth;
                        // this.css.to.height = size.totalHeight;
                        this.css.to.width = size.width;
                        this.css.to.height = size.height;

                        this.setContentSize(size.height, size.width);

                        this.fireEvent("resize");
                    }.bind(this),
                    "onComplete": function(){
                        this.fireEvent("resizeCompleted");
                    }.bind(this)
                });
			}
		}
	},
	getAction: function(){
		//未完成................................
		if (this.options.isClose){
			this.closeAction = new Element("div", {"styles": this.css.closeAction}).inject(this.titleAction);
            this.closeAction.addEvent("click", this.close.bind(this));
		}
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
				var button = new Element("input", {
					"type": "button",
					"value": bt.text,
					"styles": styles,
					"events": {
						"click": function(e){bt.action.call(this, this, e)}.bind(this)
					}
				}).inject(this.button);
			}.bind(this));
		}
	},
	getContentSize: function(height, width){
        var nodeHeight, nodeWidth;
		if (!height){
			if (this.options.contentHeight){
                nodeHeight = height = this.options.contentHeight.toFloat();
			}else{
                height = this.options.height.toFloat();
			}
		}
        if (!width){
            if (this.options.contentWidth){
                nodeWidth = width = this.options.contentWidth.toFloat();
            }else{
                width = this.options.width.toFloat();
            }
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
	setContentSize: function(height, width){
		//this.content.setStyle("height", this.getContentSize(height));
		// if (!this.options.height && !height){
         //    this.content.setStyle("height", "auto");
         //    this.content.setStyle("overflow", "hidden");
         //    this.content.setStyle("width", "auto");
		// }else{
            this.content.setStyles(this.getContentSize(height, width));
            this.content.setStyle("width", "auto");
		//}
	},
	reCenter: function(){
		var size = this.node.getSize();
		var container = $(document.body);
		if (layout.desktop.currentApp){
			container = layout.desktop.currentApp.content;
		}else{
			if (this.options.container){
				if (this.options.container.getSize().y<$(document.body).getSize().y){
					container = this.options.container;
				}
			}
		}

		// if (this.options.container){
		// 	if (this.options.container.getSize().y<$(document.body).getSize().y){
		// 		container = this.options.container;
		// 	}
		// }

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

	getTitle: function(){
		this.titleText.set("text", this.options.title);
	},
	getContent: function(){
		this.content.setStyles(this.css.content);
		if (this.options.content){
			this.options.content.inject(this.content);
		}else if (this.options.url){
			this.content.set("load", {"method": "get", "async": false});
			$(this.content).load(this.options.url);
/*
			var request = new Request.HTML({
				url: this.options.url,
				method: "GET",
				onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
					alert(responseHTML);
					this.content.set("html", responseHTML);
				}.bind(this),
				onFailure: function(xhr){
					alert("回退出现错误："+xhr.status+" "+xhr.statusText);
					window.close();
				}
			});*/
		}else if (this.options.html){
			this.content.set("html", this.options.html);
		}else if (this.options.text){
			this.content.set("text", this.options.text);
		}
//		this.content.addEvent("selectstart", function(e){
//			e.preventDefault();
//		});
	},
	show: function(){
		if (this.options.mark) this._markShow();
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: this.options.duration, "transition": this.options.transition});
		}
		if (this.fireEvent("queryShow")){
			this.node.setStyle("display", "block");

            // this.node.setStyles(t);
            // if (this.titleText) this.getTitle();
            // if (this.button) this.getButton();
            // //	this.content.setStyle("display", "block");
            // //this.fireEvent("postShow");

			this.morph.start(this.css.to).chain(function(){
				if (this.titleText) this.getTitle();
				if (this.button) this.getButton();
			//	this.content.setStyle("display", "block");
				this.fireEvent("postShow");
			}.bind(this));
		}
	},
	hide: function() {
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: this.options.duration, "transition": this.options.transition});
		}
		if (this.fireEvent("queryHide")){
			if (this.titleText) this.titleText.set("text", "");
			if (this.button) this.button.set("html", "");
			
			this.morph.start(this.css.from).chain(function(){
				this._markHide();
				this.node.setStyle("display", "none");
				this.fireEvent("postHide");
			}.bind(this));
		}
	},
	close: function(){
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: this.options.duration, "transition": this.options.transition});
		}
		
		if (this.fireEvent("queryClose")){
			this.morph.start(this.css.from).chain(function(){
				this._markHide();
				this.node.destroy();
				this.node = null;
				this.fireEvent("postClose");
			}.bind(this));
		}
	},
	_markShow: function(){

		if (this.options.mark){
			if (!this.markNode){
				var size = o2.getMarkSize(this.options.maskNode);
				this.markNode = new Element("div", {
					styles: this.css.mark
				}).inject(this.options.container || $(document.body));
				this.markNode.set("styles", {
					"height": size.y,
					"width": size.x
				});
			}
			this.markNode.setStyle("display", "block");
		}
	},
	
	_markHide: function(){
		if (this.markNode){
			this.markNode.setStyle("display", "none");
			this.markNode.destroy();
			this.markNode = null;
		}
		if (this.markNode_up){
			this.markNode_up.setStyle("display", "none");
			this.markNode_up.destroy();
			this.markNode_up = null;
		}
	}
});
