MWF.require("MWF.widget.Dialog", null, false);
MWF.xDesktop.Window = new Class({
	Extends: MWF.widget.Dialog,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"title": "window",
		"width": "800",
		"height": "600",
		"top": "10",
		"left": "10",
		"fromTop": "0",
		"fromLeft": "0",
		"mark": false,

		"html": "",
		"text": "",
		"url": "",
		"content": null,

		"isMax": true,
		"isClose": true,
		"isResize": true,
		"isMove": true,
		
		"buttons": null,
		"buttonList": null
	},
	initialize: function(app, options){

		var position = layout.desktop.desktopNode.getPosition();
	//	var size = layout.desktop.desktopNode.getSize();
		
		this.options.top = parseFloat(this.options.top)+position.y;
		this.options.fromTop = parseFloat(this.options.fromTop)+position.y;
		
		this.app = app;
		this.parent(options);
		this.isHide = false;
		this.isMax = false;
	},
    // _loadCss: function(){
     //    if (this.app.windowCss){
	// 		this.css = this.app.windowCss;
     //    }else{
     //        var key = encodeURIComponent(this.cssPath);
     //        if (!reload && MWF.widget.css[key]){
     //            this.css = MWF.widget.css[key];
     //        }else{
     //            this.cssPath = (this.cssPath.indexOf("?")!=-1) ? this.cssPath+"&v="+COMMON.version : this.cssPath+"?v="+COMMON.version;
     //            var r = new Request.JSON({
     //                url: this.cssPath,
     //                secure: false,
     //                async: false,
     //                method: "get",
     //                noCache: false,
     //                onSuccess: function(responseJSON, responseText){
     //                    this.css = responseJSON;
     //                    MWF.widget.css[key] = responseJSON;
     //                }.bind(this),
     //                onError: function(text, error){
     //                    alert(error + text);
     //                }
     //            });
     //        }
     //    }
	// },
	changeStyle: function(){
        var obj = this.getNodeStyleStatus();
        this.cssPath = MWF.defaultPath+"/widget/$Dialog/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.getContentUrl();
        var request = new Request.HTML({
            url: this.contentUrl,
            method: "GET",
            async: false,
            onSuccess: function(responseTree){
                var node = responseTree[0];
                var title = node.getElement(".MWF_dialod_title");
                var titleCenter = node.getElement(".MWF_dialod_title_center");
                var titleRefresh = node.getElement(".MWF_dialod_title_refresh");
                var titleText = node.getElement(".MWF_dialod_title_text");
                var titleAction = node.getElement(".MWF_dialod_title_action");
                var under = node.getElement(".MWF_dialod_under");
                var content = node.getElement(".MWF_dialod_content");
                var bottom = node.getElement(".MWF_dialod_bottom");
                var resizeNode = node.getElement(".MWF_dialod_bottom_resize");
                var button = node.getElement(".MWF_dialod_button");

                if (this.title && title){
                	this.title.clearStyles();
                	this.title.set("style", title.get("style"));
				}
                if (this.titleCenter && titleCenter){
                    this.titleCenter.clearStyles();
                    this.titleCenter.set("style", titleCenter.get("style"));
                }
                if (this.titleRefresh && titleRefresh){
                    this.titleRefresh.clearStyles();
                    this.titleRefresh.set("style", titleRefresh.get("style"));
                }
                if (this.titleText && titleText){
                    this.titleText.clearStyles();
                    this.titleText.set("style", titleText.get("style"));
                }
                if (this.titleAction && titleAction){
                    this.titleAction.clearStyles();
                    this.titleAction.set("style", titleAction.get("style"));
                }
                if (this.under && under){
                    this.under.clearStyles();
                    this.under.set("style", under.get("style"));
                }
                if (this.content && content){
                    this.content.clearStyles();
                    this.content.set("style", content.get("style"));
                }
                if (this.bottom && bottom){
                    this.bottom.clearStyles();
                    this.bottom.set("style", bottom.get("style"));
                }
                if (this.resizeNode && resizeNode){
                    this.resizeNode.clearStyles();
                    this.resizeNode.set("style", resizeNode.get("style"));
                }
                if (this.button && button){
                    this.button.clearStyles();
                    this.button.set("style", button.get("style"));
                }


                this.setNodeStyleStatus(obj);

                this.node.setStyles(this.css.to);
                this.spacer.setStyles(this.css.spacerTo);

                if (this.closeActionNode) this.closeActionNode.setStyles(this.css.closeActionNode);
                if (this.maxActionNode) this.maxActionNode.setStyles(this.css.maxActionNode);
                if (this.minActionNode) this.minActionNode.setStyles(this.css.minActionNode);
                if (this.restoreActionNode) this.restoreActionNode.setStyles(this.css.restoreActionNode);

                if (this.app == this.app.desktop.currentApp){
                    this.node.setStyles(this.css.current);
                    this.spacer.setStyles(this.css.spacerCurrent);
                }else{
                    this.node.setStyles(this.css.uncurrent);
                    this.spacer.setStyles(this.css.spacerUncurrent);
                }

                this.setContentSize();


            }.bind(this)
        });
        request.send();



	},
	setNodeStyleStatus: function(obj){
		this.css.to.height = obj.nodeTo.height;
		this.css.to.width = obj.nodeTo.width;
		this.css.to.top = obj.nodeTo.top;
		this.css.to.left = obj.nodeTo.left;
		this.css.to["z-index"] = obj.nodeTo["z-index"];
		
		this.css.from.top = obj.nodeFrom.top;
		this.css.from.left = obj.nodeFrom.left;
		this.css.from["z-index"] = obj.nodeFrom["z-index"];
		
		this.css.spacerTo.height = obj.spacerTo.height;
		this.css.spacerTo.width = obj.spacerTo.width;
		this.css.spacerTo.top = obj.spacerTo.top;
		this.css.spacerTo.left = obj.spacerTo.left;
		this.css.spacerTo["z-index"] = obj.spacerTo["z-index"];
		
		this.css.spacerFrom.top = obj.spacerFrom.top;
		this.css.spacerFrom.left = obj.spacerFrom.left;
		this.css.spacerFrom["z-index"] = obj.spacerFrom["z-index"];
	},
	
	getNodeStyleStatus: function(){
		return {
			"nodeTo": {
				"height": this.css.to.height,
				"width": this.css.to.width,
				"top": this.css.to.top,
				"left": this.css.to.left,
				"z-index": this.css.to["z-index"]
			},
			"nodeFrom": {
				"top": this.css.from.top,
				"left": this.css.from.left,
				"z-index": this.css.from["z-index"]
			},
			"spacerTo": {
				"height": this.css.spacerTo.height,
				"width": this.css.spacerTo.width,
				"top": this.css.spacerTo.top,
				"left": this.css.spacerTo.left,
				"z-index": this.css.spacerTo["z-index"]
			},
			"spacerFrom": {
				"top": this.css.spacerFrom.top,
				"left": this.css.spacerFrom.left,
				"z-index": this.css.spacerFrom["z-index"]
			}
		};
	},
	reStyle: function(options){
		if (options) this.setOptions(options);
		
		var index = null;
		if (MWF.xDesktop.zIndexPool) index = MWF.xDesktop.zIndexPool.applyZindex();

		this.css.to.height = this.options.height+"px";
		this.css.to.width = this.options.width+"px";
		this.css.to.top = this.options.top+"px";
		this.css.to.left = this.options.left+"px";
	//	this.css.to.top = this.options.top+"px";
		this.css.from.top = this.options.fromTop+"px";
		this.css.from.left = this.options.fromLeft+"px";
		if (index){
			this.css.from["z-index"] = index+1;
			this.css.to["z-index"] = index+1;
		}
		if (this.node) this.node.set("styles", this.css.from);
		
		this.css.spacerTo.height = this.options.height+"px";
		this.css.spacerTo.width = this.options.width+"px";
		this.css.spacerTo.top = this.options.top+"px";
		this.css.spacerTo.left = this.options.left+"px";
	//	this.css.spacerTo.top = this.options.top+"px";
		this.css.spacerFrom.top = this.options.fromTop+"px";
		this.css.spacerFrom.left = this.options.fromLeft+"px";
		if (index){
			this.css.spacerFrom["z-index"] = index;
			this.css.spacerTo["z-index"] = index;
		}
		if (this.spacer) this.spacer.set("styles", this.css.spacerFrom);
	},
	
	setCurrent: function(){
		if (MWF.xDesktop.zIndexPool){
			var index = this.node.getStyle("z-index").toFloat()+1;
			if (index < MWF.xDesktop.zIndexPool.zIndex){
				var newIndex = MWF.xDesktop.zIndexPool.applyZindex();
				
				this.css.spacerTo["z-index"] = newIndex;
				this.css.spacerFrom["z-index"] = newIndex;
				
//				if(this.spacerMorph){
//					if (this.spacerMorph.isRunning()){
//						this.spacerMorph.chain(function(){
//							if (this.spacer) this.spacer.setStyle("z-index", newIndex);
//						}.bind(this));
//					}else{
						if (this.spacer) this.spacer.setStyle("z-index", newIndex);
//					}
//				}

				this.css.to["z-index"] = newIndex+1;
				this.css.from["z-index"] = newIndex+1;
				
//				if(this.morph){
//					if (this.morph.isRunning()){
//						this.morph.chain(function(){
//							this.node.setStyle("z-index", newIndex+1);
//						}.bind(this));
//					}else{
						this.node.setStyle("z-index", newIndex+1);
//					}
//				}
			}
		}
		this.node.setStyles(this.css.current);
		if (this.spacer) this.spacer.setStyles(this.css.spacerCurrent);
	},
	setUncurrent: function(){
		this.node.setStyles(this.css.uncurrent);
		if (this.spacer) this.spacer.setStyles(this.css.spacerUncurrent);
	},
	setTitleEvent: function(){
		this.node.addEvent("mousedown", function(){
			this.app.setCurrent();
		}.bind(this));

        this.containerDrag = new Drag(this.title, {
            "handle": this.title,
            "onStart": function(el, e){
                this.containerDrag.stop();
                var div = this.node.clone(false).inject($(document.body));
                var size = this.node.getSize();
                div.setStyles({
                    "opacity": 0.7,
                    "border": "2px dashed #999",
                    "z-index": this.node.getStyle("z-index"),
                    "width": size.x,
                    "height": size.y,
                    "background-color": "#CCC",
                    "position": "absolute"
                });
                div.position({
                    relativeTo: this.node,
                    position: 'upperLeft',
                    edge: 'upperLeft'
                });

                var drag = new Drag.Move(div, {
                    "onStart": function(){
                        //this.node.setStyle("display", "none");
                        this.node.fade("out");
                        this.spacer.fade("out");
                    }.bind(this),
                    "onDrop": function(dragging, inObj, e){
                        
                        this.node.fade("in");
                        this.spacer.fade(this.css.spacerTo.opacity);
                        this.node.position({
                            relativeTo: dragging,
                            position: 'upperLeft',
                            edge: 'upperLeft'
                        });
                        var p = this.node.getPosition();
                        this.spacer.setStyles({
                            "top": p.y,
                            "left": p.x
                        });
                        this.css.spacerTo.left = p.x;
                        this.css.spacerTo.top = p.y;
                        this.css.to.left = p.x;
                        this.css.to.top = p.y;
                        dragging.destroy();
                        this.fireEvent("moveDrop", [e]);
                    }.bind(this),
                    "onCancel": function(dragging){
                        dragging.destroy();
                        drag = null;
                    }
                });
                drag.start(e);
            }.bind(this)
        });



        //this.title.addEvent("mousedown", function(e){
        //}.bind(this));

        //this.containerDrag = new Drag.Move(this.node, {
        //    "limit": {"x": [null, null], "y": [0, null]},
        //    "handle": this.title,
        //    "onDrag": function(){
        //        var p = this.node.getPosition();
        //        if (this.spacer){
        //            this.spacer.setStyles({
        //                "top": p.y,
        //                "left": p.x
        //            });
        //            this.css.spacerTo.left = p.x;
        //            this.css.spacerTo.top = p.y;
        //        };
        //        this.css.to.left = p.x;
        //        this.css.to.top = p.y;
        //    }.bind(this)
        //});
		
		if (this.options.isResize){
			this.node.makeResizable({
				"handle": this.resizeNode || this.bottom,
				"limit": {x:[200, null], y:[200, null]},
				"onDrag": function(){
					var size = this.node.getComputedSize();
					
					if (this.spacer){
						this.spacer.setStyles({
							"width": size.width,
							"height": size.height
						});
						this.css.spacerTo.width = size.width;
						this.css.spacerTo.height = size.height;
					}
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
	},
	getContentUrl: function(){
		if (layout.desktop.windowCss){
            this.contentUrl = MWF.defaultPath+"/xDesktop/$Window/desktop_default/dialog.html";
		}else{
            this.contentUrl = MWF.defaultPath+"/xDesktop/$Window/"+this.options.style+"/dialog.html";
		}

	},
	
	_loadCss: function(){
        if (this.app.desktop.windowCss){
            this.css = this.app.desktop.windowCss;
        }else{
            this.path = MWF.defaultPath+"/xDesktop/$Window/";
            this.cssPath = MWF.defaultPath+"/xDesktop/$Window/"+this.options.style+"/css.wcss";

            var r = new Request.JSON({
                url: this.cssPath,
                secure: false,
                async: false,
                method: "get",
                noCache: true,
                onSuccess: function(responseJSON, responseText){
                    this.css = responseJSON;
                }.bind(this),
                onError: function(text, error){
                    alert(text);
                }
            });
            r.send();
        }
	},
	getSpacer: function(){
		if (!this.spacer){
			this.spacer = new Element("div", {
				styles: this.css.spacerFrom
			}).inject(this.options.container || $(document.body));
		}
	},
    showNoAnimation: function(max, hide){
        if (max){
            this.showMaxIm();
        }else if (hide){
            this.showIm();
            this.hideIm();
        }else{
            this.showIm();
        }
    },
    showMaxIm: function(callback){
        this.querySetMaxStyle();
        this.getSpacer();
        this.spacer.setStyle("display", "block");
        this.node.setStyle("display", "block");

        var size = this.getMaxSize();

        var spacerTo = Object.clone(this.css.spacerTo);
        var to = Object.clone(this.css.to);
        var contentTo = {};

        spacerTo.top = size.position.y;
        spacerTo.left = size.position.x;
        spacerTo.width = size.spacer.x;
        spacerTo.height = size.spacer.y;

        to.top = size.position.y;
        to.left = size.position.x;
        to.width = size.node.x;
        to.height = size.node.y;

        contentTo = size.contentSize;

        if (this.fireEvent("queryShow")){
            this.fireEvent("queryMax");
            this.node.setStyles(to);
            this.spacer.setStyles(spacerTo);
            this.content.setStyles(contentTo);

            if (this.titleText) this.getTitle();
            this.isMax = true;
            this.isHide = false;
            if (this.containerDrag) this.containerDrag.detach();

            this.postSetMaxStyle();

            if (callback) callback();
            this.fireEvent("postShow");
            this.fireEvent("postMax");
            if (this.maxActionNode) this.maxActionNode.setStyles(this.css.restoreActionNode);
        }
    },
    showIm: function(){
        if (this.options.mark) this._markShow();
        this.getSpacer();
        this.spacer.setStyle("display", "block");
        this.node.setStyle("display", "block");
        var contentSize = this.getContentSize(this.css.to.height.toFloat(), this.css.to.width.toFloat());

        if (this.fireEvent("queryShow")){

            this.spacer.setStyles(this.css.spacerTo);
            if (this.app.desktop.currentApp!=this.app) this.spacer.setStyles(this.css.spacerUncurrent);
            this.content.setStyles(contentSize);
            this.node.setStyles(this.css.to);

            if (this.titleText) this.getTitle();
            this.isHide = false;
            if (this.containerDrag) this.containerDrag.attach();
            this.fireEvent("postShow");
        }
    },
	show: function(){
		if (this.options.mark) this._markShow();
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: 100, link: "chain"});
		}
		this.morph.setOptions({duration: 50});

		this.getSpacer();
		if (!this.spacerMorph){
			this.spacerMorph = new Fx.Morph(this.spacer, {duration: 100, link: "chain"});
		}
		this.spacerMorph.setOptions({duration: 50});
		
		if (!this.contentMorph){
			this.contentMorph = new Fx.Morph(this.content, {duration: 50, link: "chain"});
		}
		this.contentMorph.setOptions({duration: 50});
		
		this.spacer.setStyle("display", "block");
		this.node.setStyle("display", "block");
		
		var contentSize = this.getContentSize(this.css.to.height.toFloat(), this.css.to.width.toFloat());

		if (this.fireEvent("queryShow")){
			var nodeFinish = false;
			var spacerFinish = false;
			var contentFinish = false;
			
			this.spacerMorph.start(this.css.spacerTo).chain(function(){
				if (this.app.desktop.currentApp!=this.app) this.spacer.setStyles(this.css.spacerUncurrent);
				spacerFinish = true;
				firePost();
			}.bind(this));

			this.contentMorph.start(contentSize).chain(function(){
				contentFinish = true;
				firePost();
			}.bind(this));

			this.morph.start(this.css.to).chain(function(){
				nodeFinish = true;
				firePost();
			}.bind(this));
			
			var firePost = function(){
				if (nodeFinish && spacerFinish && contentFinish){
					if (this.titleText) this.getTitle();
					this.isHide = false;
					if (this.containerDrag) this.containerDrag.attach();
					this.fireEvent("postShow");

//					if (this.isMax){
//						this.isMax = false;
//						this.app.maxSize();
//					}
				} 
			}.bind(this);
		}
	},
	restore: function(callback){
		this.querySetRestoreStyle();
		if (this.options.mark) this._markShow();
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: 100, link: "chain"});
		}
		this.morph.setOptions({duration: 50});
		this.getSpacer();
		if (!this.spacerMorph){
			this.spacerMorph = new Fx.Morph(this.spacer, {duration: 100, link: "chain"});
		}
		this.spacerMorph.setOptions({duration: 50});
		
		if (!this.contentMorph){
			this.contentMorph = new Fx.Morph(this.content, {duration: 50, link: "chain"});
		}
		this.contentMorph.setOptions({duration: 50});
		this.spacer.setStyle("display", "block");
		this.node.setStyle("display", "block");
		
		var contentSize = this.getContentSize(this.css.to.height.toFloat(), this.css.to.width.toFloat());
		
		if (this.fireEvent("queryRestore")){
			var nodeFinish = false;
			var spacerFinish = false;
			var contentFinish = false;
			
			this.spacerMorph.start(this.css.spacerTo).chain(function(){
				if (this.app.desktop.currentApp!=this.app) this.spacer.setStyles(this.css.spacerUncurrent);
				spacerFinish = true;
				firePost();
			}.bind(this));
			this.contentMorph.start(contentSize).chain(function(){
				contentFinish = true;
				firePost();
			}.bind(this));

			this.morph.start(this.css.to).chain(function(){
				if (this.app.desktop.currentApp!=this.app) this.node.setStyles(this.css.uncurrent);
				nodeFinish = true;
				firePost();
			}.bind(this));
			
			var firePost = function(){
				if (nodeFinish && spacerFinish && contentFinish){
					if (this.titleText) this.getTitle();
					this.isHide = false;
					this.isMax = false;
					if (this.containerDrag) this.containerDrag.attach();
                    this.postSetRestoreStyle();
					if (callback) callback();
					this.fireEvent("resize");
					this.fireEvent("postRestore");
				}
			}.bind(this);
			if (this.maxActionNode) this.maxActionNode.setStyles(this.css.maxActionNode);
		}
	},

    querySetRestoreStyle: function(){
        if (this.css.windowTitleRestore) this.title.setStyles(this.css.windowTitleRestore);
        if (this.css.titleRefresh) this.titleRefresh.setStyles(this.css.titleRefresh);
        if (this.css.windowTitleTextRestore) this.titleText.setStyles(this.css.windowTitleTextRestore);
        if (this.css.windowTitleActionRestore) this.titleAction.setStyles(this.css.windowTitleActionRestore);

        if (this.closeActionNode){
            if (this.css.closeActionNode) this.closeActionNode.setStyles(this.css.closeActionNode);
        }
        if (this.maxActionNode){
            if (this.css.maxActionNode) this.maxActionNode.setStyles(this.css.maxActionNode);
        }
        if (this.minActionNode){
            if (this.css.minActionNode) this.minActionNode.setStyles(this.css.minActionNode);
        }
    },
    postSetRestoreStyle: function(){
        if (this.css.windowNodeRestore) this.node.setStyles(this.css.windowNodeRestore);
    },
	restoreIm: function(callback){
        this.querySetRestoreStyle();
		this.getSpacer();
	
		this.spacer.setStyle("display", "block");
		this.node.setStyle("display", "block");
		var contentSize = this.getContentSize(this.css.to.height.toFloat(), this.css.to.width.toFloat());
		
		if (this.fireEvent("queryRestore")){
			this.node.setStyles(this.css.to);
			this.spacer.setStyles(this.css.spacerTo);
			this.content.setStyles(contentSize);
			
			if (this.titleText) this.getTitle();
			this.isHide = false;
			this.isMax = false;
			if (this.containerDrag) this.containerDrag.attach();
			this.postSetRestoreStyle();
			if (callback) callback();
			this.fireEvent("resize");
			this.fireEvent("postRestore");

			if (this.maxActionNode) this.maxActionNode.setStyles(this.css.maxActionNode);
		}
	},
    hideIm: function(x, y, callback) {
        this.getSpacer();
        if (x){
            this.css.from.left = x;
            this.css.spacerFrom.left = x;
        }
        if (y){
            this.css.from.top = y;
            this.css.spacerFrom.top = y;
        }

        if (this.fireEvent("queryHide")){
            if (this.titleText) this.titleText.set("text", "");
            if (this.button) this.button.set("html", "");

            this.spacer.setStyles(this.css.spacerFrom);

            this.node.setStyles(this.css.from);

            this._markHide();
            this.isHide = true;
            this.node.setStyle("display", "none");
            this.spacer.setStyle("display", "none");
            if (callback) callback();
            this.fireEvent("postHide");
        }
    },
	hide: function(x, y, callback) {
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: 100, link: "chain"});
		}
		this.morph.setOptions({duration: 100});
		
		this.getSpacer();
		if (!this.spacerMorph){
			this.spacerMorph = new Fx.Morph(this.spacer, {duration: 100, link: "chain"});
		}
		this.spacerMorph.setOptions({duration: 100});
		
		if (x){
			this.css.from.left = x;
			this.css.spacerFrom.left = x;
		} 
		if (y){
			this.css.from.top = y;
			this.css.spacerFrom.top = y;
		} 
		
		if (this.fireEvent("queryHide")){
			if (this.titleText) this.titleText.set("text", "");
			if (this.button) this.button.set("html", "");
					
			var nodeFinish = false;
			var spacerFinish = false;
			
			this.spacerMorph.start(this.css.spacerFrom).chain(function(){
				spacerFinish = true;
				firePost();
			}.bind(this));

			this.morph.start(this.css.from).chain(function(){
				nodeFinish = true;
				firePost();
			}.bind(this));
			
			var firePost = function(){
				if (nodeFinish && spacerFinish){
					this._markHide();
					this.isHide = true;
					this.node.setStyle("display", "none");
					this.spacer.setStyle("display", "none");
					if (callback) callback();
					this.fireEvent("postHide");
				}
			}.bind(this);
		}
	},
	_hideIm: function(){
		this.node.setStyle("display", "none");
		this.spacer.setStyle("display", "none");
	},
	_showIm: function(){
		this.node.setStyle("display", "block");
		this.spacer.setStyle("display", "block");
	},
	_fadeOut: function(){
		this.node.fade("out");
		this.spacer.fade("out");
	},
	_fadeIn: function(){
		this.node.fade("in");
		this.spacer.fade("in");
	},
	
	close: function(callback){
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: 100, link: "chain"});
		}
		this.morph.setOptions({duration: 100});
		this.getSpacer();
		if (!this.spacerMorph){
			this.spacerMorph = new Fx.Morph(this.spacer, {duration: 100, link: "chain"});
		}
		this.spacerMorph.setOptions({duration: 100});
		
		if (this.fireEvent("queryClose")){
			//this.spacerMorph.start(this.css.spacerFrom).chain(function(){
			//	this.spacer.destroy();
			//	this.spacer = null;
			//}.bind(this));
            //
			//this.morph.start(this.css.from).chain(function(){
			//	this._markHide();
			//	this.node.destroy();
			//	this.node = null;
			//	if (callback) callback();
			//	this.fireEvent("postClose");
			//}.bind(this));

            this.spacer.destroy();
            this.spacer = null;

            this._markHide();
            this.node.destroy();
            this.node = null;
            if (callback) callback();
            this.fireEvent("postClose");
			o2.release(this);
		}
	},
	getAction: function(){
		this.titleAction.setStyles(this.css.titleAction);
		var _self = this;
		this.closeActionNode = new Element("div", {
			"styles": this.css.closeActionNode,
			"events": {
				"mouseover": function(e){this.setStyles(_self.css.titleActionOver);e.stopPropagation();},
				"mouseout": function(e){this.setStyles(_self.css.titleActionOut);e.stopPropagation();},
				"mousedown": function(e){this.setStyles(_self.css.titleActionDown);e.stopPropagation();},
				"mouseup": function(e){this.setStyles(_self.css.titleActionOver);e.stopPropagation();},
				"click": function(e){
					this.app.close();
                    e.stopPropagation();
				}.bind(this)
			}
		}).inject(this.titleAction);
		
		if (this.options.isMax!=false){
			this.maxActionNode = new Element("div", {
				"styles": this.css.maxActionNode,
				"events": {
					"mouseover": function(){this.setStyles(_self.css.titleActionOver);},
					"mouseout": function(){this.setStyles(_self.css.titleActionOut);},
					"mousedown": function(){this.setStyles(_self.css.titleActionDown);},
					"mouseup": function(){this.setStyles(_self.css.titleActionOver);},
					"click": function(){
						this.app.maxOrRestoreSize();
					}.bind(this)
				}
			}).inject(this.titleAction);
		}
		if (this.options.isResize){
            this.title.addEvent("dblclick", this.app.maxOrRestoreSize.bind(this.app));
		}
		
		this.minActionNode = new Element("div", {
			"styles": this.css.minActionNode,
			"events": {
				"mouseover": function(){this.setStyles(_self.css.titleActionOver);},
				"mouseout": function(){this.setStyles(_self.css.titleActionOut);},
				"mousedown": function(){this.setStyles(_self.css.titleActionDown);},
				"mouseup": function(){this.setStyles(_self.css.titleActionOver);},
				"click": function(){
					this.app.minSize();
				}.bind(this)
			}
		}).inject(this.titleAction);

        if (this.options.isRefresh!=false){
            this.titleRefresh.addEvents({
                "mouseover": function(){this.setStyles(_self.css.titleActionOver);},
                "mouseout": function(){this.setStyles(_self.css.titleActionOut);},
                "mousedown": function(){this.setStyles(_self.css.titleActionDown);},
                "mouseup": function(){this.setStyles(_self.css.titleActionOver);},
                "click": function(e){
                    this.app.refresh();
                }.bind(this),
                "dblclick": function(e){
                    e.stopPropagation();
                }.bind(this)
            });
        }
	},
	maxOrRestoreSize: function(callback){
		if (this.isMax){
			this.restoreSize(callback);
		}else{
			this.maxSize(callback);
		}
	},
	restoreSize: function(callback){
		this.restore(callback);
	},
	maxSize: function(callback){
        this.querySetMaxStyle();

		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: 50, link: "chain"});
		}
		this.morph.setOptions({duration: 50});
		
		this.getSpacer();
		if (!this.spacerMorph){
			this.spacerMorph = new Fx.Morph(this.spacer, {duration: 50, link: "chain"});
		}
		this.spacerMorph.setOptions({duration: 50});
		
		if (!this.contentMorph){
			this.contentMorph = new Fx.Morph(this.content, {duration: 50, link: "chain"});
		}
		this.contentMorph.setOptions({duration: 50});
		
		this.spacer.setStyle("display", "block");
		this.node.setStyle("display", "block");
		
		var size = this.getMaxSize();
		
		var spacerTo = Object.clone(this.css.spacerTo);
		var to = Object.clone(this.css.to);
		var contentTo = {};
		
		spacerTo.top = size.position.y;
		spacerTo.left = size.position.x;
		spacerTo.width = size.spacer.x;
		spacerTo.height = size.spacer.y;
		
		to.top = size.position.y;
		to.left = size.position.x;
		to.width = size.node.x;
		to.height = size.node.y;

		contentTo = size.contentSize;

		if (this.fireEvent("queryMax")){
			var nodeFinish = false;
			var spacerFinish = false;
			var contentFinish = false;
			
			this.spacerMorph.start(spacerTo).chain(function(){
				if (this.app.desktop.currentApp!=this.app) this.spacer.setStyles(this.css.spacerUncurrent);
				spacerFinish = true;
				firePost();
			}.bind(this));
			this.contentMorph.start(contentTo).chain(function(){
				contentFinish = true;
				firePost();
			}.bind(this));

			this.morph.start(to).chain(function(){
				if (this.app.desktop.currentApp!=this.app) this.node.setStyles(this.css.uncurrent);
				nodeFinish = true;
				firePost();
			}.bind(this));
			
			var firePost = function(){
				if (nodeFinish && spacerFinish && contentFinish){
					if (this.titleText) this.getTitle();
					this.isMax = true;
					this.isHide = false;
					if (this.containerDrag) this.containerDrag.detach();

                    this.postSetMaxStyle();

					if (callback) callback();
					this.fireEvent("resize");
					this.fireEvent("postMax");
				}
			}.bind(this);
			
			if (this.maxActionNode) this.maxActionNode.setStyles(this.css.restoreActionNode);
		}
	},
    querySetMaxStyle: function(){
	    if (this.css.windowTitleMax) this.title.setStyles(this.css.windowTitleMax);
        if (this.css.windowTitleRefreshMax) this.titleRefresh.setStyles(this.css.windowTitleRefreshMax);
        if (this.css.windowTitleTextMax) this.titleText.setStyles(this.css.windowTitleTextMax);
        if (this.css.windowTitleActionMax) this.titleAction.setStyles(this.css.windowTitleActionMax);

        if (this.closeActionNode){
            if (this.css.windowActionNodeMax) this.closeActionNode.setStyles(this.css.windowActionNodeMax);
        }
        if (this.maxActionNode){
            if (this.css.windowActionNodeMax) this.maxActionNode.setStyles(this.css.windowActionNodeMax);
        }
        if (this.minActionNode){
            if (this.css.windowActionNodeMax) this.minActionNode.setStyles(this.css.windowActionNodeMax);
        }
	},
    postSetMaxStyle: function(){
        if (this.css.windowNodeMax) this.node.setStyles(this.css.windowNodeMax);
    },
	maxSizeIm: function(callback){
        this.querySetMaxStyle();

		this.getSpacer();
		this.spacer.setStyle("display", "block");
		this.node.setStyle("display", "block");
		
		var size = this.getMaxSize();
		
		var spacerTo = Object.clone(this.css.spacerTo);
		var to = Object.clone(this.css.to);
		var contentTo = {};
		
		spacerTo.top = size.position.y;
		spacerTo.left = size.position.x;
		spacerTo.width = size.spacer.x;
		spacerTo.height = size.spacer.y;
		
		to.top = size.position.y;
		to.left = size.position.x;
		to.width = size.node.x;
		to.height = size.node.y;
		
		contentTo = size.contentSize;

		if (this.fireEvent("queryMax")){
			this.node.setStyles(to);
			this.spacer.setStyles(spacerTo);
			this.content.setStyles(contentTo);
			
			if (this.titleText) this.getTitle();
			this.isMax = true;
			this.isHide = false;
			if (this.containerDrag) this.containerDrag.detach();

            this.postSetMaxStyle();

			if (callback) callback();
			this.fireEvent("resize");
			this.fireEvent("postMax");

			if (this.maxActionNode) this.maxActionNode.setStyles(this.css.restoreActionNode);
		}
	},
	getMaxSize: function(){
		var size = layout.desktop.desktopNode.getSize();
		var position = layout.desktop.desktopNode.getPosition();
		
		var pt = this.spacer.getStyle("padding-top").toFloat();
		var pb = this.spacer.getStyle("padding-bottom").toFloat();
		var pl = this.spacer.getStyle("padding-left").toFloat();
		var pr = this.spacer.getStyle("padding-right").toFloat();
		spacerHeight = size.y-pt-pb;
		spacerWidth = size.x-pl-pr;
		
		pt = this.node.getStyle("padding-top").toFloat();
		pb = this.node.getStyle("padding-bottom").toFloat();
		pl = this.node.getStyle("padding-left").toFloat();
		pr = this.node.getStyle("padding-right").toFloat();
		nodeHeight = size.y-pt-pb;
		nodeWidth = size.x-pl-pr;
		
		var contentSize = this.getContentSize(nodeHeight, nodeWidth);
		
		return {"node": {"x": nodeWidth, "y": nodeHeight}, "spacer": {"x": spacerWidth, "y": spacerHeight}, "position": {"x": position.x, "y": position.y}, "contentSize": contentSize};
	}
});
MWF.xDesktop.WindowTransparent = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function(app, options){
        this.setOptions(options);
        var position = layout.desktop.desktopNode.getPosition();
        this.app = app;

        this.fireEvent("queryLoad");
        this.isMax = false;
        this.isHide = false;
        this.css = {
            "to": {"width": "0", "height": "0", "left": "0", "top": "0"}
        };

        var index = null;
        if (MWF.xDesktop.zIndexPool) index = MWF.xDesktop.zIndexPool.applyZindex();
        this.node = new Element("div", {
            "styles":{
                "width": "0px",
                "height": "0px",
                "top": "0px",
                "left": "0px",
                "position": "absolute",
                "z-index": (index)? index : 100
            }
        }).inject(this.options.container || $(document.body));
        this.content = new Element("div", {
            "styles": {"width": "100%", "height": "100%"}
        }).inject(this.node);
        this.fireEvent("postLoad");
    },
    show: function(){
        this.fireEvent("queryShow");
        this.node.setStyle("display", "block");
        this.isHide = false;
        this.fireEvent("postShow");
    },
    hide: function(){
        this.fireEvent("queryHide");
        this.node.setStyle("display", "none");
        this.isHide = true;
        this.fireEvent("postHide");
    },
    restore: function(callback) {
        this.fireEvent("queryRestore");
        this.node.setStyle("display", "block");
        this.isHide = false;
        this.fireEvent("postRestore");
        if (callback) callback();
    },
    restoreSize: function(callback){
        this.restore(callback);
    },
    close: function(callback){
        this.fireEvent("queryClose");
        this.node.destroy();
        this.node = null;
        this.content = null;
        delete this;
        if (callback) callback();
        this.fireEvent("postClose");
    },
    maxOrRestoreSize: function(callback){
        if (this.isMax){
            this.restoreSize(callback);
        }else{
            this.maxSize(callback);
        }
    },
    maxSize: function(callback){
        this.fireEvent("queryMax");
        this.node.setStyle("display", "block");
        this.isHide = false;
        this.fireEvent("resize");
        this.fireEvent("postMax");
        if (callback) callback();
    },
    restoreIm: function(){
        this.restore();
    },
    maxSizeIm: function(){
        this.maxSize();
    },
    setCurrent: function(){
        if (MWF.xDesktop.zIndexPool){
            var index = this.node.getStyle("z-index").toFloat()+1;
            if (index < MWF.xDesktop.zIndexPool.zIndex){
                var newIndex = MWF.xDesktop.zIndexPool.applyZindex();
                this.node.setStyle("z-index", newIndex);
            }
        }
    },
    _fadeOut: function(){
        this.node.fade("out");
        if (this.spacer) this.spacer.fade("out");
    },
    _fadeIn: function(){
        this.node.fade("in");
        if (this.spacer) this.spacer.fade("in");
    },
    setUncurrent: function(){},
    reStyle: function(){},
    changeStyle: function(){}
});