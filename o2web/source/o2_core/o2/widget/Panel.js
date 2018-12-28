o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.widget.Panel = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"title": "Panel",
		"style": "default",
		"width": 260,
		"height": 300,
		"top": 0,
		"left": 0,
		"minTop": 0,
		"minLeft": 0,
		
		"isMove": true,
		"isClose": true,
		"isMax": true,
		"isExpand": true,
		"isResize": true,
		"limitMove": true,
		
		"transition": Fx.Transitions.Back.easeIn,
		"transitionOut": Fx.Transitions.Back.easeOut,
		"duration": 100,
		
		"target": null
	},
	initialize: function(node, options){
		this.setOptions(options);
		
		this.node = $(node);
		this.container = new Element("div");
		
		this.path = o2.session.path+"/widget/$Panel/";
		this.cssPath = o2.session.path+"/widget/$Panel/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.setStatus();
		
		o2.widget.Panel.panels.push(this);
	},
	load: function(){
		if (this.fireEvent("queryLoad")){
			this.container.set("styles", this.css.container);
			this.container.set("styles", {
				"width": ""+this.options.width.toFloat()+"px",
				"height": ""+this.options.height.toFloat()+"px"
			});
			
			this.createTitleNode();
			
			this.content = new Element("div", {
				"styles": this.css.panelContent
			}).inject(this.container);
			
			this.createBottomNode();
			
			if (!this.options.target) this.options.target = $(document.body);
			this.container.inject(this.options.target);
			
			this.content.setStyle("height", this.getContentHeight());
			
			this.node.inject(this.content);
			this.setPositions();
			
			if (this.options.isMove) this.setMove();
			if (this.options.isResize) this.setResize();
			
			this.show();
		}
	},
	show: function(){
		if (!this.showHideMorph){
			this.showHideMorph = new Fx.Morph(this.container, {
				"duration": this.options.duration,
				"transition": this.options.transition
			});
		}
		
		this.showHideMorph.start(this.css.containerShow).chain(function(){
            this.fireEvent("postLoad");
		}.bind(this));
	},
	getContentHeight: function(top, bottom, container){
		var topSize = top;
		if (!topSize) topSize = this.titleNode.getSize();
		
		var bottomSize = bottom;
		if (!bottomSize) bottomSize = this.bottomNode.getSize();
		
		var containerSize = container;
		if (!containerSize) containerSize = this.container.getSize();
		
		paddingTop = this.container.getStyle("padding-top");
		paddingBottom = this.container.getStyle("padding-bottom");
		paddingLeft = this.container.getStyle("padding-left");
		paddingRight = this.container.getStyle("padding-right");
		this.options.height = (containerSize.y.toFloat())-(paddingTop.toFloat())-(paddingBottom.toFloat());
		this.options.width = (containerSize.x.toFloat())-(paddingLeft.toFloat())-(paddingRight.toFloat());
	
	//	var contentHeight = (containerSize.y.toFloat())-(paddingTop.toFloat())-(paddingBottom.toFloat())-(topSize.y.toFloat())-(bottomSize.y.toFloat());
		var contentHeight = (this.options.height.toFloat())-(topSize.y.toFloat())-(bottomSize.y.toFloat());
		if (contentHeight<10){
			contentHeight = 10;
		//	this.options.height = contentHeight+(topSize.y.toFloat())+(bottomSize.y.toFloat());
		}
		return contentHeight-2;
	},
	setMove: function(){
		//this.titleNode.setStyle("cursor", "move");
		this.panelMove = new Drag.Move(this.container, {
			"container": (this.options.limitMove) ? this.options.target : null,
			"handle": this.titleNode,
			"onDrag": function(el, e){
                this.fireEvent("drag", [el, e]);
			}.bind(this),
			"onComplete": function(el, e){
				this.options.top = this.container.getStyle("top").toFloat();
				this.options.left = this.container.getStyle("left").toFloat();
				this.fireEvent("completeMove", [el, e]);
			}.bind(this)
		});
	},
	setResize: function(){
		this.container.makeResizable({
			"handle": this.resizeAction,
			"onDrag": function(){
				this.content.setStyle("height", this.getContentHeight());
				this.fireEvent("resize");
		    }.bind(this),
		    "limit": {x:[120,null], y:[120,null]}
		});
	},
	setPositions: function(){
		if (this.options.status.active){
			var p = this.options.target.getPosition();
			var top = (p.y.toFloat()) + (this.options.top.toFloat());
			var left = (p.x.toFloat()) + (this.options.left.toFloat());
			
			var pNode = this.options.target.getOffsetParent();
			if (pNode){
				var pNodePosition = pNode.getPosition();
				top = top - pNodePosition.y;
				left = left - pNodePosition.x;
			};
			
			this.container.setStyles({
				"position": "absolute",
				"top": top,
				"left": left
			});
			
//			this.container.setPosition({"x": left, "y": top});
//			alert(top)
//			this.container.position({
//			    relativeTo: this.options.target,
//			    position: 'upperRight',
//			    edge: 'upperRight'
//			});
		}
	},
	setStatus: function(){
		this.options.status = {};
		this.options.status.active = true;
		this.options.status.expand = true;
		this.options.status.max = false;
	},
	
	createTitleNode: function(){
		this.titleNode = new Element("div", {
			"styles": this.css.panelTitleNode
		}).inject(this.container);
		
		this.actionNode = new Element("div", {
			"styles": this.css.panelActionNode
		}).inject(this.titleNode);
		
		this.textNode = new Element("div", {
			"styles": this.css.panelTextNode,
			"text": this.options.title
		}).inject(this.titleNode);
		
		
		if (this.options.isClose) this.createCloseAction();
		if (this.options.isMax) this.createMaxAction();
		if (this.options.isExpand) this.createExpandAction();
		
		this.titleNode.addEvent("click", function(){
			var maxIndex = this.container.getStyle("z-index").toInt();
			var idx = maxIndex;
			o2.widget.Panel.panels.each(function(panel){
				var index = panel.container.getStyle("z-index");
				if (maxIndex.toFloat()<index.toFloat()){
					maxIndex = index;
				}
			}.bind(this));
			maxIndex = (maxIndex.toFloat())+1;
			if (idx!=maxIndex){
				this.container.setStyle("z-index", maxIndex);
			}
		}.bind(this));
		
		
	},
	createCloseAction: function(){
		this.closeAction = new Element("div", {
			"styles": this.css.closeAction
		}).inject(this.actionNode);
		this.closeAction.addEvent("click", function(){
			this.closePanel();
		}.bind(this));
	},
	closePanel: function(){
		this.fireEvent("queryClose");
		if (!this.showHideMorph){
			this.showHideMorph = new Fx.Morph(this.container, {
				"duration": this.options.duration,
				"transition": this.options.transition
			});
		}
		
		this.showHideMorph.start(this.css.container).chain(function(){
			o2.widget.Panel.panels.erase(this);
			this.container.destroy();
			this.fireEvent("postClose");
			o2.release(this);
		}.bind(this));
	},
	destroy: function(){
		this.container.destroy();
	},
	createMaxAction: function(){
		this.maxAction = new Element("div", {
			"styles": this.css.maxAction
		}).inject(this.actionNode);
		this.maxAction.addEvent("click", function(){
			this.maxReturnPanel();
		}.bind(this));
	},
	maxReturnPanel: function(){
		if (this.options.status.max){
			this.returnPanel();
			var img = this.maxAction.getStyle("background-image");
			img = img.replace(/return.gif/g, "max.gif");;
			this.maxAction.setStyle("background-image", img);
			
			this.options.status.max = false;
		}else{
			this.maxPanel();
			var img = this.maxAction.getStyle("background-image");
			img = img.replace(/max.gif/g, "return.gif");;
			this.maxAction.setStyle("background-image", img);
			
			this.options.status.max = true;
		}
		this.fireEvent("resize");
	},
	returnPanel: function(){
		if (!this.options.status.expand) this.setExpand();
		
		this.container.setStyles({
			"position": "absolute",
			"top": this.options.top,
			"left": this.options.left,
			"width": this.returnMaxContainerSize.x,
			"height": this.returnMaxContainerSize.y
		});
		this.content.setStyle("height", this.getContentHeight());
		
		this.panelMove.attach();
	//	this.resizeAction.setStyle("display", "block");
	},
	maxPanel: function(){
		if (!this.options.status.expand) this.setExpand();
		
		this.returnMaxContainerSize = this.container.getSize();
		
		var size = $(document.body).getSize();
		this.container.setStyles({
			"position": "absolute",
			"top": 2,
			"left": 2,
			"width": size.x-6,
			"height": size.y
		});
		
		this.panelMove.detach();
	//	this.resizeAction.setStyle("display", "none");
		
		this.content.setStyle("height", this.getContentHeight());
	},
	createExpandAction: function(){
		this.explandAction = new Element("div", {
			"styles": this.css.explandAction
		}).inject(this.actionNode);
		this.explandAction.addEvent("click", function(){
			this.explandPanel();
		}.bind(this));
	},
	explandPanel: function(){
		if (this.options.status.expand){
			this.collapse();
			var img = this.explandAction.getStyle("background-image");
			img = img.replace(/up.gif/g, "down.gif");;
			this.explandAction.setStyle("background-image", img);
			this.options.status.expand = false;
		}else{
			this.expand();
			var img = this.explandAction.getStyle("background-image");
			img = img.replace(/down.gif/g, "up.gif");;
			this.explandAction.setStyle("background-image", img);
			this.options.status.expand = true;
		}
	},
	
	collapse: function(){
		this.returnTopSize = this.titleNode.getComputedSize();
		this.returnBottomSize = this.bottomNode.getComputedSize();
		this.returnContainerSize = this.container.getComputedSize();
        this.returnContentSize = this.content.getComputedSize();
        this.returnContainerPosition = this.container.getPosition(this.options.target);

        if (!this.containerTween){
            this.containerTween = new Fx.Morph(this.container, {
                "duration": this.options.duration,
                "transition": this.options.transition
            });
        }
        if (!this.bottomTween){
            this.bottomTween = new Fx.Morph(this.bottomNode, {
                "duration": this.options.duration,
                "transition": this.options.transition
            });
        }
        if (!this.contentTween){
            this.contentTween = new Fx.Morph(this.content, {
                "duration": this.options.duration,
                "transition": this.options.transition
            });
        }
        this.containerTween.options.transition = this.options.transition;
        this.bottomTween.options.transition = this.options.transition;
        this.contentTween.options.transition = this.options.transition;

        this.contentTween.start({"height": 0});
        this.bottomTween.start({"height": 0});
        this.containerTween.start({
			"height": this.returnTopSize.height,
			"width": "120px",
			"top": (this.options.minTop) || this.returnContainerPosition.y,
            "left": (this.options.minLeft) || this.returnContainerPosition.x
        }).chain(function(){
            this.bottomNode.setStyle("display", "none");
            this.content.setStyle("display", "none");
        }.bind(this));

		// if (!this.containerTween){
		// 	this.containerTween = new Fx.Tween(this.container, {
		// 		"duration": this.options.duration,
		// 		"transition": this.options.transition
		// 	});
		// }
		// if (!this.bottomTween){
		// 	this.bottomTween = new Fx.Tween(this.bottomNode, {
		// 		"duration": this.options.duration,
		// 		"transition": this.options.transition
		// 	});
		// }
		// if (!this.contentTween){
		// 	this.contentTween = new Fx.Tween(this.content, {
		// 		"duration": this.options.duration,
		// 		"transition": this.options.transition
		// 	});
		// }
		// this.containerTween.options.transition = this.options.transition;
		// this.bottomTween.options.transition = this.options.transition;
		// this.contentTween.options.transition = this.options.transition;
		//
		// this.contentTween.start("height", 0);
		// this.bottomTween.start("height", 0);
		// this.containerTween.start("height", this.returnTopSize.y).chain(function(){
		// 	this.bottomNode.setStyle("display", "none");
		// 	this.content.setStyle("display", "none");
		// }.bind(this));
	},
	expand: function(){
        if (!this.containerTween){
            this.containerTween = new Fx.Morph(this.container, {
                "duration": this.options.duration,
                "transition": this.options.transition
            });
        }
        if (!this.bottomTween){
            this.bottomTween = new Fx.Morph(this.bottomNode, {
                "duration": this.options.duration,
                "transition": this.options.transition
            });
        }
        if (!this.contentTween){
            this.contentTween = new Fx.Morph(this.content, {
                "duration": this.options.duration,
                "transition": this.options.transition
            });
        }
        this.containerTween.options.transition = this.options.transitionOut;
        this.bottomTween.options.transition = this.options.transitionOut;
        this.contentTween.options.transition = this.options.transitionOut;

        this.bottomNode.setStyle("display", "block");
        this.content.setStyle("display", "block");

        //var height = (this.returnContainerSize.y.toFloat())-(this.container.getStyle("padding-top").toFloat())-(this.container.getStyle("padding-bottom").toFloat())-2;
        this.containerTween.start({
			"height": this.returnContainerSize.height,
			"width": this.returnContainerSize.width,
			"left": this.returnContainerPosition.x,
			"top": this.returnContainerPosition.y
        });
        //this.contentTween.start("height", (this.options.height.toFloat())-(this.returnTopSize.y.toFloat())-(this.returnBottomSize.y.toFloat()));
        this.contentTween.start({"height": this.returnContentSize.height, "width": "auto"});
        //this.bottomTween.start("height", this.returnBottomSize.y);
        this.bottomTween.start({"height": this.returnBottomSize.height, "width": "auto"});

		// if (!this.containerTween){
		// 	this.containerTween = new Fx.Tween(this.container, {
		// 		"duration": this.options.duration,
		// 		"transition": this.options.transition
		// 	});
		// }
		// if (!this.bottomTween){
		// 	this.bottomTween = new Fx.Tween(this.bottomNode, {
		// 		"duration": this.options.duration,
		// 		"transition": this.options.transition
		// 	});
		// }
		// if (!this.contentTween){
		// 	this.contentTween = new Fx.Tween(this.content, {
		// 		"duration": this.options.duration,
		// 		"transition": this.options.transition
		// 	});
		// }
		// this.containerTween.options.transition = this.options.transitionOut;
		// this.bottomTween.options.transition = this.options.transitionOut;
		// this.contentTween.options.transition = this.options.transitionOut;
		//
		// this.bottomNode.setStyle("display", "block");
		// this.content.setStyle("display", "block");
		//
		// this.containerTween.start("height", (this.returnContainerSize.y.toFloat())-(this.container.getStyle("padding-top").toFloat())-(this.container.getStyle("padding-bottom").toFloat())-2);
		// this.contentTween.start("height", (this.options.height.toFloat())-(this.returnTopSize.y.toFloat())-(this.returnBottomSize.y.toFloat()));
		// this.bottomTween.start("height", this.returnBottomSize.y);
	},
	setExpand: function(){
		//this.container.setStyle("height", (this.returnContainerSize.y.toFloat())-(this.container.getStyle("padding-top").toFloat())-(this.container.getStyle("padding-bottom").toFloat())-2);
        this.container.setStyle("height", ""+this.returnContainerSize.height+"px");
        this.container.setStyle("width", ""+this.returnContainerSize.width+"px");
		//this.content.setStyle("height", (this.options.height.toFloat())-(this.returnTopSize.y.toFloat())-(this.returnBottomSize.y.toFloat()));
        this.content.setStyle("height", ""+this.returnContentSize.height+"px");
        this.content.setStyle("width", "auto");
		//this.bottomNode.setStyle("height", this.returnBottomSize.y);
        this.bottomNode.setStyle("height", ""+this.returnBottomSize.height+"px");
        this.bottomNode.setStyle("width", "auto");
		
		this.bottomNode.setStyle("display", "block");
		this.content.setStyle("display", "block");
		
		var img = this.explandAction.getStyle("background-image");
		img = img.replace(/down.gif/g, "up.gif");;
		this.explandAction.setStyle("background-image", img);
		this.options.status.expand = true;
	},
	
	createBottomNode: function(){
		this.bottomNode = new Element("div", {
			"styles": this.css.bottomNode
		}).inject(this.container);
		if (this.options.isResize) this.createResizeAction();
	},
	createResizeAction: function(){
		this.resizeAction = new Element("div", {
			"styles": this.css.resizeAction
		}).inject(this.bottomNode);
	}
});
o2.widget.Panel.panels = [];