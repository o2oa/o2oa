o2.widget = o2.widget || {};
o2.widget.ProcessIconToolbar = o2.PITB = new Class({
	css: {
		toolbarNodeHide: {
//			borderLeftWidth: 1,
//			borderLeftStyle: "solid",
//			borderLeftColor: "#ddd",
//			
//			borderTopWidth: 1,
//			borderTopStyle: "solid",
//			borderTopColor: "#ddd",
//			
//			borderRightWidth: 1,
//			borderRightStyle: "solid",
//			borderRightColor: "#bbb",
//			
//			borderBottomWidth: 1,
//			borderBottomStyle: "solid",
//			borderBottomColor: "#bbb",
			
			width: 16,
			paddingTop: 4,
			paddingLeft: 2,
			paddingRight: 2,
			paddingBottom: 0,
			//backgroundColor: "#fff",
			//backgroundColor: "e8edf6",
			//opacity: 0,
			top: 0,
			left: 0,
			position: "relative",
			display: "none"
		},
		toolbarNodeShow: {
			borderWidth: 1,
			borderStyle: "solid",
			borderColor: "#ccc",
			width: 16,
			paddingTop: 4,
			paddingLeft: 2,
			paddingRight: 2,
			paddingBottom: 0,
			backgroundColor: "#fff",
			//backgroundColor: "e8edf6",
			//opacity: 1,
			top: 0,
			left: 0,
			position: "relative",
			display: "block"
		},
		img: {
			cursor: "pointer",
			marginBottom: 4,
			borderWidth: 0,
			width: 16,
			height: 16
		}
	},
	node: null,
	targetNode: null,
	initialize: function(targetNode){
		this.targetNode = targetNode;
		this.node = new Element("div", {
			styles: this.css.toolbarNodeHide
		}).inject(this.targetNode);
		
		var itb = this;
		this.targetNode.addEvents({
			mouseover: function(obj){
				itb._mouseOver.apply(itb, [this, obj]);
			},
			mousemove: function(obj){
				itb._mouseOver.apply(itb, [this, obj]);
			},
			mouseout: function(obj){
				itb._mouseOut.apply(itb, [this, obj]);
			}
		});
		
//		this.node.addEvent("mousemove", function(obj){
//			itb._mouseOver.apply(itb, [this, obj]);
//		});
//		this.node.addEvent("mouseover", function(obj){
//			itb._mouseOver.apply(itb, [this, obj]);
//		});
//		var Children = this.targetNode.getChildren()
////		Children.addEvent("mouseout", function(obj){
////			//itb._mouseOut.apply(itb, [this, obj]);
////			obj.preventDefault();
////		});
//		Children.addEvent("mouseover", function(obj){
//			//itb._mouseOver.apply(itb, [this, obj]);
//			//obj.preventDefault();
//		});
		
		this.targetNode.store("tools", this);
	},
	
	addTool: function(img, title, fun, obj){
		var itb = this;
		this.css.img.background = "url("+img+") no-repeat center center";
		var imgNode = new Element("div",{
			title: title,
			styles: this.css.img
		}).inject(this.node);
		imgNode.addEvents({
			click: function(event){
				if (fun){
					fun.apply(obj, [itb, this, event]);
				}
			}
//			mouseover: function(obj){
//				itb._mouseOver.apply(itb, [this, obj]);
//			},
//			mousemove: function(obj){
//				itb._mouseOver.apply(itb, [this, obj]);
//			}
//			mouseout: function(obj){
//				itb._mouseOut.apply(itb, [this, obj]);
//			}
		});
	},
	
	_mouseOver: function(el, obj){
		this.show();
	},
	
	_mouseOut: function(el, obj){
	//	if (!this.targetNode.contains(window.event.toElement)){
			this.hide();
	//	};
	},
	
	_setNodePosition: function(){
		var objs = this.node.getAllPrevious();
		var y = 0;
		for (var i=0; i<objs.length; i++){
			y += objs[i].getSize().y;
		}
		var targetNodeSize = this.targetNode.getSize();
		var top = 0-y;
		var left = targetNodeSize.x - 16;
		this.node.setStyles({
			top: top,
			left: left
		});
		this.css.toolbarNodeHide.top = top;
		this.css.toolbarNodeHide.left = left;
		this.css.toolbarNodeShow.top = top;
		this.css.toolbarNodeShow.left = left;
	},
	
	show: function(){
		this._setNodePosition();
		if (!this.morph){
			this.morph = new Fx.Morph(this.node);
		}
		window.status = "show";
		//this.morph.start(this.css.toolbarNodeShow);
		this.node.setStyle("display", "block");
	},
	
	hide: function(){
		this._setNodePosition();
		if (!this.morph){
			this.morph = new Fx.Morph(this.node);
		}
		window.status = "hide";
		//this.node.setStyle("display", "none");
		//this.morph.start(this.css.toolbarNodeHide);
		this.node.setStyle("display", "none");
	}

});