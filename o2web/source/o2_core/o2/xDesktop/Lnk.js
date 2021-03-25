Element.implement({
	"makeLnk": function(options){
		return new MWF.xDesktop.LnkMaker(this, options);
	}
});

MWF.xDesktop.LnkMaker = new Class({
	Implements: [Options, Events],
	initialize: function(node, options){
		this.setOptions(options);
		
		this.node = node;
		this.desktop = layout.desktop;
		this.tmpApps = [];
		
		this.isReadyDrag = false;
		this.readyDragPosition = null;
		
		this.setEvent();
	},
	setEvent: function(){
		this.node.addEvents({
			"mousedown": function(e){
				if (!e.rightClick){
					this.isReadyDrag = true;
					this.readyDragPosition = {"x": e.page.x, "y": e.page.y};
					
					document.body.addEvent("mousemove", function(e){
						if (this.isReadyDrag){
							if (Math.abs(e.page.x-this.readyDragPosition.y)>6 || Math.abs(e.page.y-this.readyDragPosition.y)>6){
								this.isReadyDrag = false;
								this.readyDragPosition = null;
								this.node.removeEvents("mousemove");
								this.beginMoveToDesktop(e);
							}
						}
					}.bind(this));
				}
			}.bind(this),
			"mouseup": function(e){
				this.isReadyDrag = false;
				this.readyDragPosition = null;
				this.node.removeEvents("mousemove");
			}.bind(this)
		});
	},
	
	createMoveNode: function(){
		var moveNode = new Element("div", {
			"styles": this.desktop.css.lnkMoveNode
		}).inject(this.desktop.desktopNode);
		var size = this.node.getSize();
        moveNode.setStyles({
			"width": ""+size.x+"px",
            "height": ""+size.y+"px"
		});
		this.node.clone().inject(moveNode);
		moveNode.position({"relativeTo": this.node});
		
		return moveNode;
	},
	
	beginMoveToDesktop: function(e){

		var moveNode = this.createMoveNode();

		var desktop = this.desktop;
        //var quick
		var drag = new Drag.Move(moveNode, {
			"snap": 10,
			"stopPropagation": true,
			"droppables": desktop.lnkAreas,
			"onStart": function(){
				this.fireEvent("start");
				this.start();
				this.fireEvent("afterStart");
			}.bind(this),
			"onDrag": function(dragging,e){
				this.fireEvent("drag", dragging);
				this.drag(dragging, e);
				this.fireEvent("afterDrag", dragging);
			}.bind(this),
			"onEnter": function(dragging, inObj){
				this.fireEvent("enter", dragging, inObj);
				this.enter(dragging, inObj);
				this.fireEvent("afterEnter", dragging, inObj);
			}.bind(this),
			"onLeave": function(dragging, paper){
				this.fireEvent("leave", dragging, paper);
				this.leave(dragging);
				this.fireEvent("afterLeave", dragging, paper);
			}.bind(this),
			"onDrop": function(dragging, inObj){
				this.fireEvent("drop", dragging, inObj);
				this.drop(dragging, e);
				this.fireEvent("afterDrop", dragging, inObj);
			}.bind(this),
			"onCancel": function(dragging){
				this.fireEvent("cancel", dragging);
				this.cancel(dragging);
				this.fireEvent("afterCancel", dragging);
			}.bind(this),
			"onComplete": function(dragging, e){
				this.fireEvent("complete", dragging, e);
			//	this.complete();
				this.fireEvent("afterComplete", dragging, e);
			}.bind(this)
		});
		drag.start(e);
	},
	start: function(){
		this.tmpApps = [];
		Object.each(this.desktop.apps, function(app, id){
			if (!app.window.isHide){
				this.tmpApps.push(app);
				app.window._fadeOut();
			}
		}.bind(this));
	},
	enter: function(dragging, inObj){
		this.addLnkMark(dragging, inObj);
	},
	drag: function(dragging,e){
		this.moveLnkMark(dragging, e);
	},
	leave: function(dragging){
		this.removeLnkMark(dragging);
	},
	drop: function(dragging){
		this.addLnk(dragging);
		this.endMoveToDesktop(dragging);
	},
	cancel: function(dragging){
		this.endMoveToDesktop(dragging);
	},
	
	addLnkMark: function(node, inObj){
		if (!this.lnkMarkNode){
			this.lnkMarkNode = new Element("div", {
				"styles": this.desktop.css.dsektoplnkMarkNode
			}).inject(inObj);
		}
	},
	moveLnkMark: function(node,e){
		if (this.lnkMarkNode){
			var p = node.getPosition();
			var size = node.getSize();
			var area = this.lnkMarkNode.getParent();
			var lnks = area.getChildren();
			var position = false;
			for (var i=0; i<lnks.length; i++){
				if (lnks[i]!=this.lnkMarkNode && lnks[i]!=node){
					var lnkP = lnks[i].getPosition();
					var lnkSize = lnks[i].getSize();
					
					if (((lnkP.y+lnkSize.y) > (p.y+size.y/2)) && ((lnkP.x+lnkSize.x)>(p.x+size.x/2))){
//						var iconNode = lnks[i].getFirst("div");
//						
//						iconNode.setStyles({
//							"background-color": "#666"
//						});
					}else if ((lnkP.y+lnkSize.y)>(p.y+size.y)){
						this.lnkMarkNode.inject(lnks[i], "before");
						this.lnkMarkNode.setStyle("top", lnkP.y-10);
						position = true;
						break;
					}else{
//						var iconNode = lnks[i].getFirst("div");
//						iconNode.setStyles({
//							"background": "transparent"
//						});			
					}
                    //if ((lnkP.y< e.event.y) && (lnkP.y+lnkSize.y>e.event.y) && (lnkP.x<e.event.x) && (lnkP.x+lnkSize.x>e.event.x)){
                    //    debugger;
                    //    if (lnks[i].node != this.node){
                    //        alert("in")
                    //    }
                    //}
				}
			}
			if (!position){
				this.lnkMarkNode.inject(area);
				this.lnkMarkNode.setStyle("top", "auto");
				if (this.desktop.lnks.length) this.lnkMarkNode.setStyle("top", this.lnkMarkNode.getPosition().y-10);
			} 
		}
	},
	removeLnkMark: function(node){
		if (this.lnkMarkNode){
			this.lnkMarkNode.destroy();
			this.lnkMarkNode = null;
		} 
	},
	addLnk: function(node){

		if (this.lnkMarkNode){
			var par = this.options.par;
			var lnk = new MWF.xDesktop.Lnk(par.icon, par.title, par.par);
			
			var next = this.lnkMarkNode.getNext();
			if (next){
				var nextLnk = next.retrieve("lnk");
				var idx = this.desktop.lnks.indexOf(nextLnk);
				this.desktop.lnks.splice(idx, 0, lnk);
			}else{
				this.desktop.lnks.push(lnk);
			}

			lnk.inject(this.lnkMarkNode, "after");
			
			this.removeLnkMark();

			this.desktop.resizeLnk();
		}
	},
	
	endMoveToDesktop: function(dragging){
		this.tmpApps.each(function(app){
			app.window._fadeIn();
		});
		this.tmpApps = [];
		if (dragging) dragging.destroy();
	}
});

MWF.xDesktop.LnkMove = new Class({
	Extends: MWF.xDesktop.LnkMaker,
	
	setEvent: function(){
		this.isReadySwing = false;
		this.readySwingTime = "";
		this.node.addEvents({
			"mousedown": function(e){
				if (!e.rightClick){
					this.isReadyDrag = true;
					//this.isReadySwing = true;
					this.readyDragPosition = {"x": e.page.x, "y": e.page.y};
					this.readySwingTime = new Date().getTime();
					// this.swingTimer = window.setTimeout(function(){
					// 	this.beginNodeSwing(e);
					// }.bind(this), 1000);
					
					this.node.addEvent("mousemove", function(event){
						if (this.isReadyDrag){
							if (Math.abs(event.page.x-this.readyDragPosition.x)>10 || Math.abs(event.page.y-this.readyDragPosition.y)>10){
								this.clearEvent();
								this.beginMoveToDesktop(e);
							}
						}
					}.bind(this));
				}
			}.bind(this),
			"mouseup": function(e){this.clearEvent();}.bind(this)
		});
	},
	clearEvent: function(){
		this.isReadyDrag = false;
		this.readyDragPosition = null;
		this.isReadySwing = false;
		this.readySwingTime = "";
		this.node.removeEvents("mousemove");
		window.clearTimeout(this.swingTimer);
	},
	beginNodeSwing: function(e){
		if (this.isReadySwing){
			this.clearEvent();
			this.morph = new Fx.Morph(this.node, {duration: 100});
			var lnk = this.node.retrieve("lnk");
			lnk.isSwing = true;
		//	this.nodeSwing(1);
			
			this.nodeSwing(22);
			
		}
	},
	nodeSwing: function(o){
	//	var opacity = (o==1) ? 0.4 : 1;
		var margin = (o==22) ? 18 : 22;
		
//		this.morph.start({"opacity": opacity}).chain(function(){
//			this.nodeSwing(opacity);
//		}.bind(this));
		this.morph.start({"margin-left": margin}).chain(function(){
			this.nodeSwing(margin);
		}.bind(this));
		
	},
	stopSwing: function(){
		this.morph.cancel();
		var lnk = this.node.retrieve("lnk");
//		this.morph.start({"opacity": 1}).chain(function(){
//			lnk.isSwing = false;
//		}.bind(this));
		this.morph.start({"margin-left": 20}).chain(function(){
			lnk.isSwing = false;
		}.bind(this));
	},
	createMoveNode: function(){
		var moveNode = this.node.clone().inject(this.desktop.desktopNode);
		moveNode.position({"relativeTo": this.node});
		moveNode.setStyles(this.desktop.css.desktopLnkNode);
		return moveNode;
	},
	start: function(){
		this.node.setStyle("opacity", 0.5);
		this.node.setStyles(this.desktop.css.desktopLnkNode_current);
	},
	addLnk: function(node){
		if (this.lnkMarkNode){
			var nextLnkNode = this.lnkMarkNode.getNext();
			var prevLnkNode = this.lnkMarkNode.getPrevious();
			var nextLnk = (nextLnkNode) ? nextLnkNode.retrieve("lnk") : null;
			var prevLnk = (prevLnkNode) ? prevLnkNode.retrieve("lnk") : null;
			
			var lnk = this.node.retrieve("lnk");
			if (!nextLnk){
				this.desktop.lnks.erase(lnk);
				this.desktop.lnks.push(lnk);
			}else if (nextLnk==lnk || prevLnk==lnk){
		//		this.node.fade("in");
			}else{
				this.desktop.lnks.erase(lnk);
				var index = this.desktop.lnks.indexOf(nextLnk);
				if (index!=-1){
					this.desktop.lnks.splice(index, 0, lnk);
				}else{
					this.desktop.lnks.push(lnk);
				}
			}
		//	this.node.setStyle("opacity", 0);
			this.removeLnkMark();
			this.desktop.resizeLnk();
		//	this.node.fade("in");
		}
		this.node.fade("in");
	},
	endMoveToDesktop: function(dragging){
		this.node.setStyles(this.desktop.css.desktopLnkNode);
		if (dragging) dragging.destroy();
	}
});

MWF.xDesktop.Lnk = new Class({
	initialize: function(icon, title, par){
		this.desktop = layout.desktop;
		this.icon = icon;
		this.title = title;
		this.par = par;
		this.node = new Element("div", {
			"styles": this.desktop.css.desktopLnkNode,
			"title": this.title
		});
		this.node.store("lnk", this);
		this.iconNode = new Element("div", {
			"styles": this.desktop.css.desktopLnkIconNode
		}).inject(this.node);

		var img = new Element("img").inject(this.iconNode);
		var s = this.iconNode.getSize();
        if (!s.x) s.x = this.iconNode.getStyle("width").toFloat();
        if (!s.y) s.y = this.iconNode.getStyle("height").toFloat();
		var w = s.x-10;
        var h = s.y-10;
        img.setStyles({
			"margin": "5px",
			"max-width": ""+w+"px",
            "max-height": ""+h+"px"
		});

        if (this.icon.substr(0,3).toLowerCase()=="url"){
            img.set("src", this.icon.substring(4, this.icon.length-1));
            //this.iconNode.setStyle("background-image", this.icon);
        }else{
            img.set("src", this.icon);
            //this.iconNode.setStyle("background-image", "url("+this.icon+")");
        }
		//this.iconNode.setStyle("background-image", "url("+this.icon+")");
		
		this.titleNode = new Element("div", {
			"styles": this.desktop.css.desktopLnkTitleNode,
			"text": this.title
		}).inject(this.node);
		
		this.node.addEvents({
			"mouseover": function(e){this.node.setStyles(this.desktop.css.desktopLnkNode_current);}.bind(this),
			"mouseout": function(e){this.node.setStyles(this.desktop.css.desktopLnkNode);}.bind(this),
			"click": function(e){this.open(e);}.bind(this)
		});
		
		this.lnkMove = new MWF.xDesktop.LnkMove(this.node);
		this.loadMenu();
	},
	loadMenu: function(){
		this.menu = new MWF.widget.Menu(this.node, {"style": "lnk",
            "container": this.desktop.node
        });
		this.menu.load();
		this.menu.addMenuItem(o2.LP.desktop.deleteLink, "click", function(){
            this.deleteLnk();
        }.bind(this));
	},
    deleteLnk: function(){
        this.desktop.lnks.erase(this);
        this.node.destroy();
        MWF.release(this);
        delete this;
    },
	inject: function(node, where){
		this.node.inject(node, where);
	},
	open: function(e){

		if (!this.isSwing){
            var parList = this.par.split("#");
            var appName = parList[0];
            var statusStr = parList[1] || "";
            if (appName.toLowerCase()==="@url"){
                window.open(statusStr);
            }else{
                var status = JSON.decode(statusStr, false);

                var options = {};
                if (status){
                    if (status.appId){
                        options = {
                            "appId": status.appId,
                            "onQueryLoad": function(){
                                this.status = status;
                            }
                        };
                    }else{
                        options = {
                            "onQueryLoad": function(){
                                this.status = status;
                            }
                        };
                    }
                }
                this.desktop.openApplication(e, appName, options);
            }

		}else{
			this.stopSwingFun = function(){
				this.lnkMove.stopSwing();
				this.node.removeEvent("click", this.stopSwingFun);
			}.bind(this);
			this.node.addEvent("click", this.stopSwingFun);
			
		}
	}
});