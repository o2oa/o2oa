o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.widget.Notices = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
        "style": "default",
		"type": "default",  // the type of the notice (defaults to 'default'), possible types are: 'ok', 'error', 'info', 'notice'
        "target": null,
        "delayClose": 1000,
        "offset": {x: 10, y: 10},
        "content": ""
	},
	initialize: function(options){
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
				"width": this.options.width,
				"height": this.options.height
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
			
			this.fireEvent("postLoad");
		}
	},
	show: function(){
		if (!this.showHideMorph){
			this.showHideMorph = new Fx.Morph(this.container, {
				"duration": this.options.duration,
				"transition": this.options.transition
			});
		}
		
		this.showHideMorph.start(this.css.containerShow);
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
		return contentHeight;
	},
	setMove: function(){
		//this.titleNode.setStyle("cursor", "move");
		this.panelMove = new Drag.Move(this.container, {
			"container": this.options.target,
			"handle": this.titleNode,
			"onComplete": function(){
				this.options.top = this.container.getStyle("top").toFloat();
				this.options.left = this.container.getStyle("left").toFloat();
				this.fireEvent("completeMove");
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
		this.returnTopSize = this.titleNode.getSize();
		this.returnBottomSize = this.bottomNode.getSize();
		this.returnContainerSize = this.container.getSize();
		
		if (!this.containerTween){
			this.containerTween = new Fx.Tween(this.container, {
				"duration": this.options.duration,
				"transition": this.options.transition
			});
		}
		if (!this.bottomTween){
			this.bottomTween = new Fx.Tween(this.bottomNode, {
				"duration": this.options.duration,
				"transition": this.options.transition
			});
		}
		if (!this.contentTween){
			this.contentTween = new Fx.Tween(this.content, {
				"duration": this.options.duration,
				"transition": this.options.transition
			});
		}
		this.containerTween.options.transition = this.options.transition;
		this.bottomTween.options.transition = this.options.transition;
		this.contentTween.options.transition = this.options.transition;
		
		this.contentTween.start("height", 0);
		this.bottomTween.start("height", 0);
		this.containerTween.start("height", this.returnTopSize.y).chain(function(){
			this.bottomNode.setStyle("display", "none");
			this.content.setStyle("display", "none");
		}.bind(this));
	},
	expand: function(){
		if (!this.containerTween){
			this.containerTween = new Fx.Tween(this.container, {
				"duration": this.options.duration,
				"transition": this.options.transition
			});
		}
		if (!this.bottomTween){
			this.bottomTween = new Fx.Tween(this.bottomNode, {
				"duration": this.options.duration,
				"transition": this.options.transition
			});
		}
		if (!this.contentTween){
			this.contentTween = new Fx.Tween(this.content, {
				"duration": this.options.duration,
				"transition": this.options.transition
			});
		}
		this.containerTween.options.transition = this.options.transitionOut;
		this.bottomTween.options.transition = this.options.transitionOut;
		this.contentTween.options.transition = this.options.transitionOut;
		
		this.bottomNode.setStyle("display", "block");
		this.content.setStyle("display", "block");
		
		this.containerTween.start("height", (this.returnContainerSize.y.toFloat())-(this.container.getStyle("padding-top").toFloat())-(this.container.getStyle("padding-bottom").toFloat())-2);
		this.contentTween.start("height", (this.options.height.toFloat())-(this.returnTopSize.y.toFloat())-(this.returnBottomSize.y.toFloat()));
		this.bottomTween.start("height", this.returnBottomSize.y);
	},
	setExpand: function(){
		this.container.setStyle("height", (this.returnContainerSize.y.toFloat())-(this.container.getStyle("padding-top").toFloat())-(this.container.getStyle("padding-bottom").toFloat())-2);
		this.content.setStyle("height", (this.options.height.toFloat())-(this.returnTopSize.y.toFloat())-(this.returnBottomSize.y.toFloat()));
		this.bottomNode.setStyle("height", this.returnBottomSize.y);
		
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