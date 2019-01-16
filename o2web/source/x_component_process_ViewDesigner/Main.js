MWF.xApplication.process.ViewDesigner =MWF.xApplication.process.ViewDesigner || {};
MWF.APPVD = MWF.xApplication.process.ViewDesigner;
MWF.APPVD.options = {
	"multitask": true,
	"executable": false
};
//MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("process.ViewDesigner", "View", null, false);
MWF.xApplication.process.ViewDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "process.ViewDesigner",
		"icon": "icon.png",
		"title": MWF.APPVD.LP.title,
		"appTitle": MWF.APPVD.LP.title,
		"id": "",
        "tooltip": {
            "unCategory": MWF.APPVD.LP.unCategory
        },
		"actions": null,
		"category": null,
		"processData": null
	},
	onQueryLoad: function(){
        this.shortcut = true;
        if (this.status){
            this.options.application = this.status.applicationId;
            this.application = this.status.application;
            this.options.id = this.status.id;
        }

		if (!this.options.id){
			this.options.desktopReload = false;
			this.options.title = this.options.title + "-"+MWF.APPVD.LP.newView;
		}

        if (!this.actions) this.actions = MWF.Actions.get("x_processplatform_assemble_designer");
		//if (!this.actions) this.actions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
		
		this.lp = MWF.xApplication.process.ViewDesigner.LP;

        this.addEvent("queryClose", function(e){
            if (this.explorer){
                this.explorer.reload();
            }
        }.bind(this));
        this.addEvent("postLoadWindowMax", function(e){
            this.loadWindowOk = true;
            if (this.loadApplicationOk && this.loadWindowOk){
                //if (this.tab.showPage) {
                //    var view = this.tab.showPage.view;
                //    if (view) {
                        this.view.setViewWidth();
                //    }
                //}
            }
        }.bind(this));
        this.addEvent("postLoadApplication", function(e){
            this.loadApplicationOk = true;
            if (this.loadApplicationOk && this.loadWindowOk){
                //if (this.tab.showPage) {
                //    var view = this.tab.showPage.view;
                //    if (view) {
                        this.view.setViewWidth();
                //    }
                //}
            }
        }.bind(this));
	},
	
	loadApplication: function(callback){
		this.createNode();
		if (!this.options.isRefresh){
			this.maxSize(function(){
				this.openView(function(){
                    if (callback) callback();
                });
			}.bind(this));
		}else{
			this.openView(function(){
                if (callback) callback();
            });
		}

        if (!this.options.readMode) this.addKeyboardEvents();
	},
    addKeyboardEvents: function(){
        this.addEvent("copy", function(){
            this.copyModule();
        }.bind(this));
        this.addEvent("paste", function(){
            this.pasteModule();
        }.bind(this));
        this.addEvent("cut", function(){
            this.cutModule();
        }.bind(this));
        this.addEvent("keySave", function(e){
            this.keySave(e);
        }.bind(this));
        this.addEvent("keyDelete", function(e){
            this.keyDelete(e);
        }.bind(this));
    },
    keySave: function(e){
        if (this.shortcut) {
            //if (this.tab.showPage) {
            //    var view = this.tab.showPage.view;
            //    if (view) {
                    this.view.save();
                    e.preventDefault();
            //    }
            //}
        }
    },
    keyDelete: function(){
        if (this.shortcut) {
            //if (this.tab.showPage) {
                //var view = this.tab.showPage.view;
                //if (view) {
                    if (this.view.currentSelectedModule) {
                        var item = this.view.currentSelectedModule;
                        item["delete"]();
                    }
                //}
            //}
        }
    },

    copyModule: function(){
        if (this.shortcut) {
            //if (this.tab.showPage) {
                //var view = this.tab.showPage.view;
                //if (view) {
                    if (this.view.currentSelectedModule) {
                        var item = this.view.currentSelectedModule;
                        MWF.clipboard.data = {
                            "type": "view",
                            "data": item.json
                        };
                    }
                //}
            //}
        }
    },
    cutModule: function(){
        if (this.shortcut) {
            //if (this.tab.showPage) {
                //var view = this.tab.showPage.view;
                //if (view) {
                    if (this.view.currentSelectedModule) {
                        this.copyModule();
                        var item = this.view.currentSelectedModule;
                        item.destroy();
                    }
                //}
            //}
        }
    },
    pasteModule: function(){
        if (this.shortcut) {
            if (MWF.clipboard.data) {
                if (MWF.clipboard.data.type == "view") {
                    //if (this.tab.showPage) {
                        //var view = this.tab.showPage.view;
                        //if (view) {
                            if (this.view.currentSelectedModule) {
                                var item = this.view.currentSelectedModule;
                                var data = MWF.clipboard.data.data;

                                item.addColumn(null, data);
                            }
                        //}
                    //}
                }
            }
        }
    },

	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
	},
    getApplication:function(callback){
        if (!this.application){
            this.actions.getApplication(this.options.application, function(json){
                this.application = {"name": json.data.name, "id": json.data.id};
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
	openView: function(callback){
        this.getApplication(function(){
            this.initOptions();
            this.loadNodes();
            this.loadViewListNodes();
            //	this.loadToolbar();
            this.loadContentNode();
            this.loadProperty();
            //	this.loadTools();
            this.resizeNode();
            this.addEvent("resize", this.resizeNode.bind(this));
            this.loadView(function(){
                if (callback) callback();
            });

            this.setScrollBar(this.propertyDomArea, null, {
                "V": {"x": 0, "y": 0},
                "H": {"x": 0, "y": 0}
            });
        }.bind(this));
	},
	initOptions: function(){
		//this.toolsData = null;
		//this.toolbarMode = "all";
		//this.tools = [];
		//this.toolbarDecrease = 0;
		//
		//this.designNode = null;
		//this.form = null;
	},
	loadNodes: function(){
        this.viewListNode = new Element("div", {
            "styles": this.css.viewListNode
        }).inject(this.node);
		this.propertyNode = new Element("div", {
			"styles": this.css.propertyNode
		}).inject(this.node);
		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.node);
	},

    //loadViewListNodes-------------------------------
    loadViewListNodes: function(){
        this.viewListTitleNode = new Element("div", {
            "styles": this.css.viewListTitleNode,
            "text": MWF.APPVD.LP.view
        }).inject(this.viewListNode);

        this.viewListResizeNode = new Element("div", {"styles": this.css.viewListResizeNode}).inject(this.viewListNode);
        this.viewListAreaSccrollNode = new Element("div", {"styles": this.css.viewListAreaSccrollNode}).inject(this.viewListNode);
        this.viewListAreaNode = new Element("div", {"styles": this.css.viewListAreaNode}).inject(this.viewListAreaSccrollNode);

        this.loadViewListResize();

        this.loadViewList();
    },

    loadViewListResize: function(){
        this.viewListResize = new Drag(this.viewListResizeNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.viewListAreaSccrollNode.getSize();
                el.store("initialWidth", size.x);
            }.bind(this),
            "onDrag": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
//				var y = e.event.y;
                var bodySize = this.content.getSize();
                var position = el.retrieve("position");
                var initialWidth = el.retrieve("initialWidth").toFloat();
                var dx = x.toFloat() - position.x.toFloat();

                var width = initialWidth+dx;
                if (width> bodySize.x/2) width =  bodySize.x/2;
                if (width<40) width = 40;
                this.contentNode.setStyle("margin-left", width+1);
                this.viewListNode.setStyle("width", width);
                //this.tab.pages.each(function(page){
                    this.view.setViewWidth();
                //});
            }.bind(this)
        });
        this.viewListResizeNode.addEvents({
            "touchstart": function(e){
                el = e.target;
                var x = (Browser.name=="firefox") ? e.page.clientX : e.page.x;
                var y = (Browser.name=="firefox") ? e.page.clientY : e.page.y;
                el.store("position", {"x": x, "y": y});

                var size = this.viewListAreaSccrollNode.getSize();
                el.store("initialWidth", size.x);
            }.bind(this),
            "touchmove": function(e){
                //Object.each(e, function(v, k){
                //    alert(k+": "+ v);
                //});
                el = e.target;

                var x = (Browser.name=="firefox") ? e.page.clientX : e.page.x;
//				var y = e.event.y;
                var bodySize = this.content.getSize();
                var position = el.retrieve("position");
                var initialWidth = el.retrieve("initialWidth").toFloat();
                var dx = x.toFloat() - position.x.toFloat();

                var width = initialWidth+dx;
                if (width> bodySize.x/2) width =  bodySize.x/2;
                if (width<40) width = 40;
                this.contentNode.setStyle("margin-left", width+1);
                this.viewListNode.setStyle("width", width);
                //this.tab.pages.each(function(page){
                    this.view.setViewWidth();
                //});
            }.bind(this)
        });

    },

    loadViewList: function(){
        this.actions.listView(this.application.id, function (json) {
            json.data.each(function(view){
                this.createListViewItem(view);
            }.bind(this));
        }.bind(this), null, false);
    },

    //列示所有视图列表
    createListViewItem: function(view, isNew){
        var _self = this;
        var listViewItem = new Element("div", {"styles": this.css.listViewItem}).inject(this.viewListAreaNode, (isNew) ? "top": "bottom");
        var listViewItemIcon = new Element("div", {"styles": this.css.listViewItemIcon}).inject(listViewItem);
        var listViewItemText = new Element("div", {"styles": this.css.listViewItemText, "text": (view.name) ? view.name+" ("+view.alias+")" : this.lp.newView}).inject(listViewItem);

        listViewItem.store("view", view);
        listViewItem.addEvents({
            "dblclick": function(e){_self.loadViewByData(this, e);},
            "mouseover": function(){if (_self.currentListViewItem!=this) this.setStyles(_self.css.listViewItem_over);},
            "mouseout": function(){if (_self.currentListViewItem!=this) this.setStyles(_self.css.listViewItem);}
        });
    },
    //打开视图
    loadViewByData: function(node, e){
        var view = node.retrieve("view");

        var openNew = true;
        //for (var i = 0; i<this.tab.pages.length; i++){
        //    if (view.id==this.tab.pages[i].view.data.id){
        //        this.tab.pages[i].showTabIm();
        //        openNew = false;
        //        break;
        //    }
        //}
        if (openNew){
            //this.loadViewData(view.id, function(vdata){
            //    var view = new MWF.xApplication.process.ViewDesigner.View(this, vdata);
            //    view.load();
            //}.bind(this));

            var _self = this;
            var options = {
                "onQueryLoad": function(){
                    this.actions = _self.actions;
                    this.category = _self;
                    this.options.id = view.id;
                    this.application = _self.application;
                    this.explorer = _self.explorer;
                }
            };
            this.desktop.openApplication(e, "process.ViewDesigner", options);
        }
    },


	//loadContentNode------------------------------
    loadContentNode: function(){
		this.contentToolbarNode = new Element("div#contentToolbarNode", {
			"styles": this.css.contentToolbarNode
		}).inject(this.contentNode);
		if (!this.options.readMode) this.loadContentToolbar();
		
		this.editContentNode = new Element("div", {
			"styles": this.css.editContentNode
		}).inject(this.contentNode);

		this.loadEditContent(function(){
		//	if (this.designDcoument) this.designDcoument.body.setStyles(this.css.designBody);
			if (this.designNode) this.designNode.setStyles(this.css.designNode);
		}.bind(this));
	},
    loadContentToolbar: function(callback){
		this.getFormToolbarHTML(function(toolbarNode){
			var spans = toolbarNode.getElements("span");
			spans.each(function(item, idx){
				var img = item.get("MWFButtonImage");
				if (img){
					item.set("MWFButtonImage", this.path+""+this.options.style+"/toolbar/"+img);
				}
			}.bind(this));

			$(toolbarNode).inject(this.contentToolbarNode);
			MWF.require("MWF.widget.Toolbar", function(){
				this.toolbar = new MWF.widget.Toolbar(toolbarNode, {"style": "ProcessCategory"}, this);
				this.toolbar.load();
				if (callback) callback();
			}.bind(this));
		}.bind(this));
	},
	getFormToolbarHTML: function(callback){
		var toolbarUrl = this.path+this.options.style+"/toolbars.html";
		var r = new Request.HTML({
			url: toolbarUrl,
			method: "get",
			onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
				var toolbarNode = responseTree[0];
				if (callback) callback(toolbarNode);
			}.bind(this),
			onFailure: function(xhr){
				this.notice("request processToolbars error: "+xhr.responseText, "error");
			}.bind(this)
		});
		r.send();
	},
    maxOrReturnEditor: function(){
        if (!this.isMax){
            this.designNode.inject(this.node);
            this.designNode.setStyles({
                "position": "absolute",
                "width": "100%",
                "height": "100%",
                "top": "0px",
                "margin": "0px",
                "left": "0px"
            });
            //this.tab.pages.each(function(page){
                this.view.setAreaNodeSize();
            //});
            this.isMax = true;
        }else{
            this.isMax = false;
            this.designNode.inject(this.editContentNode);
            this.designNode.setStyles(this.css.designNode);
            this.designNode.setStyles({
                "position": "static"
            });
            this.resizeNode();
            //this.tab.pages.each(function(page){
                this.view.setAreaNodeSize();
            //});
        }

    },
    loadEditContent: function(callback){
        this.designNode = new Element("div", {
            "styles": this.css.designNode
        }).inject(this.editContentNode);

        //MWF.require("MWF.widget.Tab", function(){
        //    this.tab = new MWF.widget.Tab(this.designNode, {"style": "dictionary"});
        //    this.tab.load();
        //}.bind(this), false);

        //    MWF.require("MWF.widget.ScrollBar", function(){
        //        new MWF.widget.ScrollBar(this.designNode, {"distance": 100});
        //    }.bind(this));
	},
	
	//loadProperty------------------------
	loadProperty: function(){
		this.propertyTitleNode = new Element("div", {
            "styles": this.css.propertyTitleNode,
            "text": MWF.APPVD.LP.property
        }).inject(this.propertyNode);

        this.propertyResizeBar = new Element("div", {
            "styles": this.css.propertyResizeBar
        }).inject(this.propertyNode);
        this.loadPropertyResize();

        this.propertyContentNode = new Element("div", {
            "styles": this.css.propertyContentNode
        }).inject(this.propertyNode);

        this.propertyDomArea = new Element("div", {
			"styles": this.css.propertyDomArea
		}).inject(this.propertyContentNode);
		
		this.propertyDomPercent = 0.3;
		this.propertyContentResizeNode = new Element("div", {
			"styles": this.css.propertyContentResizeNode
		}).inject(this.propertyContentNode);
		
		this.propertyContentArea = new Element("div", {
			"styles": this.css.propertyContentArea
		}).inject(this.propertyContentNode);
		
		this.loadPropertyContentResize();

        //this.setPropertyContent();
        this.propertyNode.addEvent("keydown", function(e){e.stopPropagation();});
	},

    //setPropertyContent: function(){
    //    this.dictionaryPropertyNode = new Element("div", {"styles": this.css.dictionaryPropertyNode});
    //    this.jsonDomNode = new Element("div", {"styles": this.css.jsonDomNode});
    //    this.jsonTextNode = new Element("div", {"styles": this.css.jsonTextNode});
    //    this.jsonTextAreaNode = new Element("textarea", {"styles": this.css.jsonTextAreaNode}).inject(this.jsonTextNode);
    //
    //    MWF.require("MWF.widget.Tab", function(){
    //        var tab = new MWF.widget.Tab(this.propertyContentArea, {"style": "moduleList"});
    //        tab.load();
    //        tab.addTab(this.dictionaryPropertyNode, this.lp.property, false);
    //        tab.addTab(this.jsonDomNode, "JSON", false);
    //        tab.addTab(this.jsonTextNode, "TEXT", false);
    //        tab.pages[0].showTab();
    //    }.bind(this));
    //
    //    var node = new Element("div", {"styles": this.css.propertyTitleNode, "text": this.lp.id+":"}).inject(this.dictionaryPropertyNode);
    //    this.propertyIdNode = new Element("div", {"styles": this.css.propertyTextNode}).inject(this.dictionaryPropertyNode);
    //
    //    node = new Element("div", {"styles": this.css.propertyTitleNode, "text": this.lp.name+":"}).inject(this.dictionaryPropertyNode);
    //    this.propertyNameNode = new Element("input", {"styles": this.css.propertyInputNode}).inject(this.dictionaryPropertyNode);
    //    if (this.options.noModifyName || this.options.readMode){
    //        this.propertyNameNode.set("readonly", true);
    //        this.propertyNameNode.addEvent("keydown", function(){
    //            this.notice(this.lp.notice.noModifyName, "error");
    //        }.bind(this));
    //    }
    //
    //    node = new Element("div", {"styles": this.css.propertyTitleNode, "text": this.lp.alias+":"}).inject(this.dictionaryPropertyNode);
    //    this.propertyAliasNode = new Element("input", {"styles": this.css.propertyInputNode}).inject(this.dictionaryPropertyNode);
    //    if (this.options.noModifyName || this.options.readMode){
    //        this.propertyAliasNode.set("readonly", true);
    //        this.propertyAliasNode.addEvent("keydown", function(){
    //            this.notice(this.lp.notice.noModifyName, "error");
    //        }.bind(this));
    //    }
    //
    //    node = new Element("div", {"styles": this.css.propertyTitleNode, "text": this.lp.description+":"}).inject(this.dictionaryPropertyNode);
    //    this.propertyDescriptionNode = new Element("textarea", {"styles": this.css.propertyInputAreaNode}).inject(this.dictionaryPropertyNode);
    //    if (this.options.noModifyName || this.options.readMode){
    //        this.propertyDescriptionNode.set("readonly", true);
    //    }
    //},

	loadPropertyResize: function(){
//		var size = this.propertyNode.getSize();
//		var position = this.propertyResizeBar.getPosition();
		this.propertyResize = new Drag(this.propertyResizeBar,{
			"snap": 1,
			"onStart": function(el, e){
				var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
				var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
				el.store("position", {"x": x, "y": y});
				
				var size = this.propertyNode.getSize();
				el.store("initialWidth", size.x);
			}.bind(this),
			"onDrag": function(el, e){
				var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
//				var y = e.event.y;
				var bodySize = this.content.getSize();
				var position = el.retrieve("position");
				var initialWidth = el.retrieve("initialWidth").toFloat();
				var dx = position.x.toFloat()-x.toFloat();
				
				var width = initialWidth+dx;
				if (width> bodySize.x/2) width =  bodySize.x/2;
				if (width<40) width = 40;
				this.contentNode.setStyle("margin-right", width+1);
				this.propertyNode.setStyle("width", width);
                //this.tab.pages.each(function(page){
                    this.view.setViewWidth();
                //});
			}.bind(this)
		});
	},
	loadPropertyContentResize: function(){
		this.propertyContentResize = new Drag(this.propertyContentResizeNode, {
			"snap": 1,
			"onStart": function(el, e){
				var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
				var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
				el.store("position", {"x": x, "y": y});
				
				var size = this.propertyDomArea.getSize();
				el.store("initialHeight", size.y);
			}.bind(this),
			"onDrag": function(el, e){
				var size = this.propertyContentNode.getSize();
				
	//			var x = e.event.x;
				var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
				var position = el.retrieve("position");
				var dy = y.toFloat()-position.y.toFloat();

				var initialHeight = el.retrieve("initialHeight").toFloat();
				var height = initialHeight+dy;
				if (height<40) height = 40;
				if (height> size.y-40) height = size.y-40;
				
				this.propertyDomPercent = height/size.y;
				
				this.setPropertyContentResize();
				
			}.bind(this)
		});
	},
	setPropertyContentResize: function(){
		var size = this.propertyContentNode.getSize();
		var resizeNodeSize = this.propertyContentResizeNode.getSize();
		var height = size.y-resizeNodeSize.y;
		
		var domHeight = this.propertyDomPercent*height;
		var contentHeight = height-domHeight;
		
		this.propertyDomArea.setStyle("height", ""+domHeight+"px");
		this.propertyContentArea.setStyle("height", ""+contentHeight+"px");
		
		if (this.view){
			if (this.view.currentSelectedModule){
				if (this.view.currentSelectedModule.property){
					var tab = this.view.currentSelectedModule.property.propertyTab;
					if (tab){
						var tabTitleSize = tab.tabNodeContainer.getSize();
						
						tab.pages.each(function(page){
							var topMargin = page.contentNodeArea.getStyle("margin-top").toFloat();
							var bottomMargin = page.contentNodeArea.getStyle("margin-bottom").toFloat();
							
							var tabContentNodeAreaHeight = contentHeight - topMargin - bottomMargin - tabTitleSize.y.toFloat()-15;
							page.contentNodeArea.setStyle("height", tabContentNodeAreaHeight);
						}.bind(this));
						
					}
				}
			}
		}
	},
	

	
	//resizeNode------------------------------------------------
	resizeNode: function(){
		var nodeSize = this.node.getSize();
		this.contentNode.setStyle("height", ""+nodeSize.y+"px");
		this.propertyNode.setStyle("height", ""+nodeSize.y+"px");
		
		var contentToolbarMarginTop = this.contentToolbarNode.getStyle("margin-top").toFloat();
		var contentToolbarMarginBottom = this.contentToolbarNode.getStyle("margin-bottom").toFloat();
		var allContentToolberSize = this.contentToolbarNode.getComputedSize();
		var y = nodeSize.y - allContentToolberSize.totalHeight - contentToolbarMarginTop - contentToolbarMarginBottom;
		this.editContentNode.setStyle("height", ""+y+"px");
		
		if (this.designNode){
			var designMarginTop = this.designNode.getStyle("margin-top").toFloat();
			var designMarginBottom = this.designNode.getStyle("margin-bottom").toFloat();
			y = nodeSize.y - allContentToolberSize.totalHeight - contentToolbarMarginTop - contentToolbarMarginBottom - designMarginTop - designMarginBottom;
			this.designNode.setStyle("height", ""+y+"px");
		}
		
		
		titleSize = this.propertyTitleNode.getSize();
		titleMarginTop = this.propertyTitleNode.getStyle("margin-top").toFloat();
		titleMarginBottom = this.propertyTitleNode.getStyle("margin-bottom").toFloat();
		titlePaddingTop = this.propertyTitleNode.getStyle("padding-top").toFloat();
		titlePaddingBottom = this.propertyTitleNode.getStyle("padding-bottom").toFloat();
		
		y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom;
		y = nodeSize.y-y;
		this.propertyContentNode.setStyle("height", ""+y+"px");
		this.propertyResizeBar.setStyle("height", ""+y+"px");
		
		this.setPropertyContentResize();

        titleSize = this.viewListTitleNode.getSize();
        titleMarginTop = this.viewListTitleNode.getStyle("margin-top").toFloat();
        titleMarginBottom = this.viewListTitleNode.getStyle("margin-bottom").toFloat();
        titlePaddingTop = this.viewListTitleNode.getStyle("padding-top").toFloat();
        titlePaddingBottom = this.viewListTitleNode.getStyle("padding-bottom").toFloat();
        nodeMarginTop = this.viewListAreaSccrollNode.getStyle("margin-top").toFloat();
        nodeMarginBottom = this.viewListAreaSccrollNode.getStyle("margin-bottom").toFloat();

        y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom+nodeMarginTop+nodeMarginBottom;
        y = nodeSize.y-y;
        this.viewListAreaSccrollNode.setStyle("height", ""+y+"px");
        this.viewListResizeNode.setStyle("height", ""+y+"px");
	},



	
	//loadView------------------------------------------
    loadView: function(callback){
		this.getViewData(this.options.id, function(vdata){
            this.setTitle(this.options.appTitle + "-"+vdata.name);
            this.taskitem.setText(this.options.appTitle + "-"+vdata.name);
            this.options.appTitle = this.options.appTitle + "-"+vdata.name;

            //if (this.options.readMode){
            //    this.view = new MWF.xApplication.process.DictionaryDesigner.DictionaryReader(this, ddata);
            //}else{
                this.view = new MWF.xApplication.process.ViewDesigner.View(this, vdata);
            //}

			this.view.load();
            if (callback) callback();
            //if (this.status){
            //    if (this.status.openViews){
            //        this.status.openViews.each(function(id){
            //            this.loadViewData(id, function(data){
            //                var showTab = true;
            //                if (this.status.currentId){
            //                    if (this.status.currentId!=data.id) showTab = false;
            //                }
            //                //if (this.options.readMode){
            //                //    var view = new MWF.xApplication.process.DictionaryDesigner.DictionaryReader(this, data, {"showTab": showTab});
            //                //}else{
            //                    var view = new MWF.xApplication.process.DictionaryDesigner.View(this, data, {"showTab": showTab});
            //                //}
            //
            //                view.load();
            //            }.bind(this), true);
            //        }.bind(this));
            //    }
            //}
		}.bind(this));
	},
    getViewData: function(id, callback){
		if (!this.options.id){
			this.loadNewViewData(callback);
		}else{
			this.loadViewData(id, callback);
		}
	},
	loadNewViewData: function(callback){
        var url = "/x_component_process_ViewDesigner/$View/view.json";
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                this.actions.getUUID(function(id){
                    obj.id=id;
                    obj.isNewView = true;
                    obj.application = this.application.id;
                    this.createListViewItem(obj, true);
                    if (callback) callback(obj);
                }.bind(this));
            }.bind(this),
            "onerror": function(text){
                this.notice(text, "error");
            }.bind(this),
            "onRequestFailure": function(xhr){
                this.notice(xhr.responseText, "error");
            }.bind(this)
        });
	},
    loadViewData: function(id, callback){
		this.actions.getView(id, function(json){
			if (json){
				var data = json.data;
                var dataJson = JSON.decode(data.data);
                data.data = dataJson;

                if (!this.application){
                    this.actions.getApplication(data.application, function(json){
                        this.application = {"name": json.data.name, "id": json.data.id};
                        if (callback) callback(data);
                    }.bind(this));
                }else{
                    if (callback) callback(data);
                }
			}
		}.bind(this));
	},

    saveView: function(){
        //if (this.tab.showPage){
            //var view = this.tab.showPage.view;
            this.view.save(function(){
                //if (view==this.view){
                    var name = this.view.data.name;
                    this.setTitle(MWF.APPVD.LP.title + "-"+name);
                    this.options.desktopReload = true;
                    this.options.id = this.view.data.id;
                //}
            }.bind(this));
        //}
	},
    saveDictionaryAs: function(){
        this.view.saveAs();
	},
    dictionaryExplode: function(){
        this.view.explode();
    },
    dictionaryImplode: function(){
        this.view.implode();
    },
	//recordStatus: function(){
	//	return {"id": this.options.id};
	//},
    recordStatus: function(){
        //if (this.tab){
            var openViews = [];
            openViews.push(this.view.data.id);
            var currentId = this.view.data.id;
            return {
                "id": this.options.id,
                "application": this.application,
                "openViews": openViews,
                "currentId": currentId
            };
        //}
        //return {"id": this.options.id, "application": this.application};
    }
});
