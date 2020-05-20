o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.widget.ScrollBar = new Class({
	Implements : [ Options, Events],
	Extends: o2.widget.Common,
	options : {
		"distance" : 50,
		"style": "default",
		"isShow": false,
		"where": "before",
		"indent": true,
		"offset": {
			"V": {
				"x": 0,
				"y": 0
			},
			"H": {
				"x": 0,
				"y": 0
			}
		}
	},

	scrollEvent: function(node){
		var scrollTop = node.scrollTop.toFloat();
		var scrollLeft = node.scrollLeft.toFloat();
		this.fireEvent("scroll", [scrollTop, scrollLeft]);
	},
	initialize : function(node, options) {
		if (COMMON.Browser.Platform.isMobile){
			node.setStyle("overflow", "auto");
			node.addEvent("scroll", function(){
				this.scrollEvent(node);
			}.bind(this));
			// node.addEvent("touchstart", function(e){ this.checkScroll()}.bind(this));
			// node.addEvent("touchmove", function(e){
			//    e.preventDefault();
			//    // this.fireEvent("scroll", [1]);
			//    // e.preventDefault();
			//    //
			//    // var scrollTop = node.scrollTop.toFloat();
			//    // var scrollLeft = node.scrollLeft.toFloat();
			//    // //this.fireEvent("scroll", [scrollTop, scrollLeft]);
			//    e.stopPropagation();
			// }.bind(this));
			// node.addEvent("touchend", function(e){
			//    this.scrollEvent(node);
			// }.bind(this));
			return true;
		}

		this.node = $(node);
		this.setOptions(options);

		this.path = o2.session.path+"/widget/$ScrollBar/";
		this.cssPath = o2.session.path+"/widget/$ScrollBar/"+this.options.style+"/css.wcss";
		this._loadCss();

		// document.body.onresize = function(e){
		// 	this.checkScroll();
		// }.bind(this);
		// this.node.onresize = function(e){
		// 	this.checkScroll();
		// }.bind(this);

		this.checkScrollFun = function(e){
			this.checkScroll();
		}.bind(this);
		this.checkScrollStopFun = function(e){
			this.checkScroll();
			e.stopPropagation();
		}.bind(this);

		if(layout.desktop && layout.desktop.addEvent)layout.desktop.addEvent("resize", this.checkScrollFun);
		//layout.desktop.addEvent("onresize", this.checkScrollFun);
		this.node.addEvent("mouseover", this.checkScrollStopFun);
		this.node.addEvent("mouseout", this.checkScrollStopFun);

//		this.node.addEvent("click", function(e){
//			this.checkScrollShow(e);
//		}.bind(this));

		this.checkScrollShow();
		this.checkScrollShowFun = function(e){
			this.checkScrollShow(e);
		}.bind(this);
		document.body.addEvent("mousemove", this.checkScrollShowFun);

//		this.node.getChildren().each(function(node){
//			node.onresize = function(e){
//				this.checkScroll();
//			}.bind(this);
//		}.bind(this));

		//	this.node.onpropertychange = function(e){
		//		this.checkScroll();
		//	}.bind(this);

	},

	checkScrollShow: function(e){
		if (!this.node.isPointIn) return false;
		//if (this.node.isPointIn(e.event.clientX, e.event.clientY, this.scrollVWidth)){
		if (this.scrollVAreaNode){
			var opacity = this.scrollVAreaNode.getStyle("opacity");
			if (opacity==0){
				if (!this.scrollAreaOverLock && !this.scrollAreaOutLock){
					this.scrollAreaOverLock = true;
					var margin = this.node.getStyle("margin-right").toFloat();
					if (this.options.indent){
						var marginFx = new Fx.Tween(this.node, {property: "margin-right", duration: "100"});
						marginFx.start(margin+this.scrollVWidth).chain(function(){
							this.scrollVAreaNode.setStyle("display", "block");

							var scrollFx = new Fx.Tween(this.scrollVAreaNode, {property: "opacity", duration: "100"});
							scrollFx.start(0,1).chain(function(){
								this.scrollAreaOverLock = false;
							}.bind(this));

							//this.scrollVAreaNode.fade("in");

						}.bind(this));
					}else{
						this.scrollVAreaNode.setStyle("display", "block");

						var scrollFx = new Fx.Tween(this.scrollVAreaNode, {property: "opacity", duration: "100"});
						scrollFx.start(0,1).chain(function(){
							this.scrollAreaOverLock = false;
						}.bind(this));
					}
				}
			}
		}
		// }else{
		//     if (this.scrollVAreaNode){
		//         var opacity = this.scrollVAreaNode.getStyle("opacity");
		//         //		if (!this.options.isShow){
		//         if (opacity==1){
		//             if (!this.scrollAreaOutLock && !this.scrollAreaOverLock){
		//                 if (!this.showScrollBar){
		//                     this.scrollAreaOutLock = true;
		//                     var scrollFx = new Fx.Tween(this.scrollVAreaNode, {property: "opacity", duration: "100"});
		//                     scrollFx.start(0).chain(function(){
		//                         var margin = this.node.getStyle("margin-right").toFloat();
		//                         this.scrollVAreaNode.setStyle("display", "none");
		//                         if (this.options.indent){
		//                             var marginFx = new Fx.Tween(this.node, {property: "margin-right", duration: "100"});
		//                             marginFx.start(margin-this.scrollVWidth).chain(function(){
		//                                 this.scrollAreaOutLock = false;
		//                             }.bind(this));
		//                         }else{
		//                             this.scrollAreaOutLock = false;
		//                         }
		//                     }.bind(this));
		//                 }
		//             }
		//         }
		//         //		}
		//     }
		// }
	},

	// checkScrollShow: function(e){
	//    if (!this.node.isPointIn) return false;
	// 	if (this.node.isPointIn(e.event.clientX, e.event.clientY, this.scrollVWidth)){
	// 		if (this.scrollVAreaNode){
	// 			var opacity = this.scrollVAreaNode.getStyle("opacity");
	// 			if (opacity==0){
	// 				if (!this.scrollAreaOverLock && !this.scrollAreaOutLock){
	// 					this.scrollAreaOverLock = true;
	// 					var margin = this.node.getStyle("margin-right").toFloat();
	// 					if (this.options.indent){
	// 						var marginFx = new Fx.Tween(this.node, {property: "margin-right", duration: "100"});
	// 						marginFx.start(margin+this.scrollVWidth).chain(function(){
	// 							this.scrollVAreaNode.setStyle("display", "block");
	//
	// 							var scrollFx = new Fx.Tween(this.scrollVAreaNode, {property: "opacity", duration: "100"});
	// 							scrollFx.start(0,1).chain(function(){
	// 								this.scrollAreaOverLock = false;
	// 							}.bind(this));
	//
	// 							//this.scrollVAreaNode.fade("in");
	//
	// 						}.bind(this));
	// 					}else{
	// 						this.scrollVAreaNode.setStyle("display", "block");
	//
	// 						var scrollFx = new Fx.Tween(this.scrollVAreaNode, {property: "opacity", duration: "100"});
	// 						scrollFx.start(0,1).chain(function(){
	// 							this.scrollAreaOverLock = false;
	// 						}.bind(this));
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}else{
	// 		if (this.scrollVAreaNode){
	// 			var opacity = this.scrollVAreaNode.getStyle("opacity");
	// 	//		if (!this.options.isShow){
	// 				if (opacity==1){
	// 					if (!this.scrollAreaOutLock && !this.scrollAreaOverLock){
	// 						if (!this.showScrollBar){
	// 							this.scrollAreaOutLock = true;
	// 							var scrollFx = new Fx.Tween(this.scrollVAreaNode, {property: "opacity", duration: "100"});
	// 							scrollFx.start(0).chain(function(){
	// 								var margin = this.node.getStyle("margin-right").toFloat();
	// 								this.scrollVAreaNode.setStyle("display", "none");
	// 								if (this.options.indent){
	// 									var marginFx = new Fx.Tween(this.node, {property: "margin-right", duration: "100"});
	// 									marginFx.start(margin-this.scrollVWidth).chain(function(){
	// 										this.scrollAreaOutLock = false;
	// 									}.bind(this));
	// 								}else{
	// 									this.scrollAreaOutLock = false;
	// 								}
	// 							}.bind(this));
	// 						}
	// 					}
	// 				}
	// 	//		}
	// 		}
	// 	}
	// },

	setScrollNodePosition: function(){
		this.node.scrollTo(0,0);
		if (this.scrollVNode){
//			var x = (this.clientSize.x.toFloat()) - (this.scrollVNodeSize.x.toFloat()) + this.options.offset.V.x;
//			var y = 0-(this.scrollSize.y.toFloat()) + this.options.offset.V.y;
//			this.scrollVNode.setStyle("margin-left", ""+x+"px");
			this.scrollVNode.setStyle("margin-top", "0px");

//			this.scrollVNode.setStyle("margin-left", "100px");
//			this.scrollVNode.setStyle("margin-top", ""+y+"px");

			this.setScrollVNodeMoveLimit();
		}
		if (this.scrollHNode){
//			var x = (this.nodePosition.x.toFloat());
//			//var y = (this.nodePosition.y.toFloat())+(this.clientSize.y.toFloat())-(this.scrollVNodeSize.y.toFloat());
//			var y = (this.nodePosition.y.toFloat())+(this.clientSize.y.toFloat());
//			this.scrollHNode.setStyle("top", ""+y+"px");
//			this.scrollHNode.setStyle("left", ""+x+"px");
			var y = this.scrollHNode.getSize().y;
			y = 0-y + this.options.offset.H.y;
			var x = 0 + this.options.offset.H.x;
			this.scrollHNode.setStyle("margin-left", ""+x+"px");
			this.scrollHNode.setStyle("margin-top", ""+y+"px");
		}
	},
	setScrollVNodeMove: function(){
		this.scrollVAreaNode.addEvent("click", function(e){e.stopPropagation();});
		this.scrollVNodeMove = new Drag(this.scrollVNode,{
			"onStart": function(el, e){
				this.fireEvent("scrollStart");
				var x = e.event.clientX;
				var y = e.event.clientY;
				el.store("position", {"x": x, "y": y});
				el.store("margin", el.getStyle("margin-top"));
				this.showScrollBar = true;
			}.bind(this),
			"onComplete": function(e){
				this.showScrollBar = false;
				this.fireEvent("scrollComplete");
			}.bind(this),
			"onDrag": function(el, e){
				var p = el.retrieve("position");
				var margin = el.retrieve("margin").toFloat();
				//	var dx = (e.event.clientX.toFloat()) - (p.x.toFloat());
				var dy = (e.event.clientY.toFloat()) - (p.y.toFloat());

				var dmargin = margin+dy;

				var scrollSize = this.node.getScrollSize();
				var clientSize = this.node.getSize();
				var scrollVNodeSize = this.scrollVNode.getSize();
				//var marginTop = this.node.getStyle("margin-top").toFloat();

				var maxY = (clientSize.y.toFloat())-(scrollVNodeSize.y.toFloat());
				var minY = 0;
				if (dmargin<minY) dmargin = minY;
				if (dmargin>maxY) dmargin = maxY;

				this.scrollVNode.setStyle("margin-top", ""+dmargin+"px");
				this.scroll(null, dmargin);

				//	var vPosition = this.scrollVNode.getPosition();
				//	this.scroll(null, (vPosition.y.toFloat())-(this.nodePosition.y.toFloat()));
			}.bind(this)
		});
	},
	setScrollVNodeMoveLimit: function(){
		//var x = (this.nodePosition.x.toFloat())+(this.clientSize.x.toFloat())-(this.scrollVNodeSize.x.toFloat());
		var x = (this.nodePosition.x.toFloat())+(this.clientSize.x.toFloat());
		var y = (this.nodePosition.y.toFloat());
		var maxY = y+(this.clientSize.y.toFloat()) - (this.scrollVNodeSize.y.toFloat());

		this.scrollVNodeMove.detach();
		this.scrollVNodeMove.setOptions({"limit": {"x": [x, x], "y": [y, maxY]}});
		this.scrollVNodeMove.attach();
	},
	scroll: function(nodeDelta, scrollDelta){
		var scrollSize = this.node.getScrollSize();
		var clientSize = this.node.getSize();
		var scrollVNodeSize = this.scrollVNode.getSize();
		//	var marginTop = this.node.getStyle("margin-top").toFloat();

		var scrollHeight = scrollSize.y-clientSize.y;
		var maxY = (clientSize.y.toFloat())-(scrollVNodeSize.y.toFloat());
		var minY = 0;

		if (nodeDelta){
			var scroll = this.node.getScroll();
			var scrollTo = (scroll.y.toFloat())+(nodeDelta.toFloat());
			if (scrollTo<0) scrollTo = 0;
			if (scrollTo>scrollHeight) scrollTo = scrollHeight;

			//this.node.scrollTo(0, scrollTo);
			this.fireEvent("scroll", [scrollTo]);
			this.node.tweenScroll(scrollTo, 1);

			//this.node.tween("margin-top", -100);


			var y = (scrollTo/(scrollHeight.toFloat()))*(maxY.toFloat());

			if (y<minY) y = minY;
			if (y>maxY) y = maxY;

			// this.scrollVNode.set("tween", {"duration": "1", "transition": Fx.Transitions.Expo.easeOut});
			// this.scrollVNode.tween("margin-top", ""+y+"px");
			this.scrollVNode.setStyle("margin-top", ""+y+"px");
		}
		if (scrollDelta){
			if (scrollDelta>maxY) scrollDelta = maxY;
			var y = (scrollDelta/maxY)*scrollHeight;

			var scroll = this.node.getScroll();
			//this.node.scrollTo(0, y);
			this.node.tweenScroll(y, 1);
			this.fireEvent("scroll", [y]);
		}

	},
	checkScroll: function(){
		var scrollSize = this.node.getScrollSize();
		var clientSize = this.node.getSize();
		var nodePosition = this.node.getPosition(this.node.getOffsetParent());

		if (!this.mousewheel) this.mousewheel = function(e){
			var delta = 1-e.event.wheelDelta;

			var step = ((this.options.distance.toFloat())/100)*(clientSize.y.toFloat());

			delta = (delta/(clientSize.y.toFloat()))*step;
			this.scroll(delta, null);
			e.stopPropagation();
		}.bind(this);
		if (!this.domMousewheel) this.domMousewheel = function(e){
			var delta = e.detail;

			var step = ((this.options.distance.toFloat())/100)*(clientSize.y.toFloat());

			delta = (delta/6)*step;
			this.scroll(delta, null);
			e.stopPropagation();
		}.bind(this);

		if (!this.touchmove)  this.touchmove = function(e){
			var delta = e.event.detail;

			var step = ((this.options.distance.toFloat())/100)*(clientSize.y.toFloat());

			delta = (delta/(clientSize.y.toFloat()))*step;
			this.scroll(delta, null);
			e.preventDefault();
			e.stopPropagation();
		}.bind(this);

		if (scrollSize.y>clientSize.y){
			if (!this.scrollVNode){
//				this.scrollVAreaNode = new Element("div", {
//					"styles": this.css.scrollVAreaNode
//				}).inject(this.node, "before");
				this.scrollVAreaNode = new Element("div", {
					"styles": this.css.scrollVAreaNode
				}).inject(this.node, this.options.where);

				//if (this.scrollVAreaNode.getStyle("position")=="absolute"){
				//    this.scrollVAreaNode.position({
				//        relativeTo: this.node,
				//        position: "topright",
				//        edge: "topleft"
				//    });
				//}


				this.scrollVNode = new Element("div", {
					"styles": this.css.scrollVNode
				}).inject(this.scrollVAreaNode);
				var margin = this.node.getStyle("margin-right").toFloat();
				var marginTop = this.node.getStyle("margin-top").toFloat();
				var marginBottom = this.node.getStyle("margin-bottom").toFloat();
				var scrollVNodeSize = this.scrollVAreaNode.getSize();

				//	this.node.tween("margin-right", margin+scrollVNodeSize.x);
				if (this.options.indent) this.scrollVAreaNode.setStyle("margin-right", margin);
				this.scrollVAreaNode.setStyle("margin-top", marginTop);

				this.scrollVAreaNode.setStyle("display", "none");

				this.scrollVWidth = this.scrollVAreaNode.getStyle("width").toFloat();

				this.setScrollVNodeMove();

				if (!this.isAddEvent){
					this.node.addEvent("mousewheel", this.mousewheel);
					this.node.addEvent("touchmove", this.touchmove);
					if (Browser.name=="firefox"){
						this.node.addEventListener("DOMMouseScroll", this.domMousewheel, false);
					}
					this.isAddEvent = true
				}

				// this.node DOMMouseScroll
			}
			if (this.scrollVAreaNode.getStyle("position")=="absolute"){
				this.scrollVAreaNode.position({
					relativeTo: this.node,
					position: "topright",
					edge: "topleft"
				});
			}
			this.scrollVAreaNode.setStyle("height", clientSize.y);
			//	this.setScrollNodePosition();
		}else{
			if (this.scrollVNode){

				if (!this.scrollAreaOutLock && !this.scrollAreaOverLock){
					if (!this.showScrollBar){
						this.scrollAreaOutLock = true;
						var scrollFx = new Fx.Tween(this.scrollVAreaNode, {property: "opacity", duration: "100"});
						scrollFx.start(0).chain(function(){
							var margin = this.node.getStyle("margin-right").toFloat();
							this.scrollVAreaNode.setStyle("display", "none");
							//var marginFx = new Fx.Tween(this.node, {property: "margin-right", duration: "100"});
							if (this.options.indent){
								var marginFx = new Fx.Tween(this.node, {property: "margin-right", duration: "100"});
								marginFx.start(margin-this.scrollVWidth).chain(function(){
									this.scrollAreaOutLock = false;

									this.scrollVAreaNode.destroy();
									this.scrollVNode = null;
									this.scrollVAreaNode = null;
								}.bind(this));
							}else{
								this.scrollAreaOutLock = false;
								this.scrollVAreaNode.destroy();
								this.scrollVNode = null;
								this.scrollVAreaNode = null;
							}

						}.bind(this));
					};
				};
//				var scrollVNodeSize = this.scrollVAreaNode.getSize();
//				var margin = this.node.getStyle("margin-right").toFloat();
//
//				this.scrollVAreaNode.destroy();
//				this.scrollVNode = null;
//				this.scrollVAreaNode = null;

				//		this.node.tween("margin-right", margin-scrollVNodeSize.x);
			}
			//this.node.removeEvent("mousewheel", this.mousewheel);
			this.node.removeEvent("touchmove", this.touchmove);
			if (Browser.name=="firefox"){
				this.node.removeEventListener("DOMMouseScroll", this.domMousewheel, false);
			}
		}
//		if (scrollSize.x>scrollSize.x){
//			alert("ddd");
//			var scrollNode = node.retrieve("scrollbarH");
//			if (!scrollNode){
//				var scrollNode = new Element("div", {
//					"styles": css
//				}).inject(node, "after");
//				alerrt("ddd");
//				var scrollNodeMove = new Drag.Move(scrollNode);
//				node.store("scrollbarH", scrollNode);
//			}
//		}

	},
	destroy: function(){
		if (this.checkScrollFun) layout.desktop.removeEvent("resize", this.checkScrollFun);
		//if (this.checkScrollFun) layout.desktop.removeEvent("onresize", this.checkScrollFun);
		if (this.checkScrollStopFun) this.node.removeEvent("mouseover", this.checkScrollStopFun);
		if (this.checkScrollStopFun) this.node.removeEvent("mouseout", this.checkScrollStopFun);
		if (this.checkScrollShowFun) document.body.removeEvent("mousemove", this.checkScrollShowFun);
		if (this.mousewheel) this.node.removeEvent("mousewheel", this.mousewheel);
		if (this.touchmove) this.node.removeEvent("touchmove", this.touchmove);
		if (Browser.name=="firefox"){
			if (this.domMousewheel) this.node.removeEventListener("DOMMouseScroll", this.domMousewheel, false);
		}
		if (this.scrollVAreaNode) this.scrollVAreaNode.destroy();
		if (this.scrollVNode) this.scrollVNode.destroy();
		o2.release(this);
	}
});
