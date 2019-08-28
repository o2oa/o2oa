MWF.APPPJD = MWF.xApplication.process.ProjectionDesigner;
MWF.APPPJD.options = {
	"multitask": true,
	"executable": false
};
MWF.xDesktop.requireApp("process.ProjectionDesigner", "Projection", null, false);
MWF.xApplication.process.ProjectionDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "process.ProjectionDesigner",
		"icon": "icon.png",
		"title": MWF.APPPJD.LP.title,
		"appTitle": MWF.APPPJD.LP.title,
		"id": "",
		"actions": null,
		"category": null,
		"processData": null
	},
	onQueryLoad: function(){
		if (this.status){
            this.options.application = this.status.applicationId;
            this.application = this.status.application;
			this.options.id = this.status.id;
		}
		if (!this.options.id){
			this.options.desktopReload = false;
			this.options.title = this.options.title + "-"+MWF.APPPJD.LP.newProjection;
		}
        this.actions = MWF.Actions.get("x_processplatform_assemble_designer");
		this.lp = MWF.xApplication.process.ProjectionDesigner.LP;
        this.addEvent("queryClose", function(e){
            if (this.explorer){
                this.explorer.reload();
            }
        }.bind(this));
	},
	
	loadApplication: function(callback){
		this.createNode();
		if (!this.options.isRefresh){
			this.maxSize(function(){
				this.openProjection();
			}.bind(this));
		}else{
			this.openProjection();
		}
		if (callback) callback();
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
    openProjection: function(){
        this.getApplication(function(){
            this.loadNodes();
            this.loadProjectionListNodes();
            this.loadContentNode(function(){

                this.loadProperty();
                //	this.loadTools();
                this.resizeNode();
                this.addEvent("resize", this.resizeNode.bind(this));
                this.loadProjection();

                if (this.toolbarContentNode){
                    this.setScrollBar(this.toolbarContentNode, null, {
                        "V": {"x": 0, "y": 0},
                        "H": {"x": 0, "y": 0}
                    });
                    this.setScrollBar(this.propertyDomArea, null, {
                        "V": {"x": 0, "y": 0},
                        "H": {"x": 0, "y": 0}
                    });
                }

            }.bind(this));

        }.bind(this));
	},
	loadNodes: function(){
        this.projectionListNode = new Element("div", {
            "styles": this.css.projectionListNode
        }).inject(this.node);

		this.propertyNode = new Element("div", {
			"styles": this.css.propertyNode
		}).inject(this.node);

		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.node);
	},
    //loadScriptList-------------------------------
    loadProjectionListNodes: function(){
        this.projectionListTitleNode = new Element("div", {
            "styles": this.css.projectionListTitleNode,
            "text": MWF.APPPJD.LP.projectionLibrary
        }).inject(this.projectionListNode);

        this.projectionListResizeNode = new Element("div", {"styles": this.css.projectionListResizeNode}).inject(this.projectionListNode);
        this.projectionListAreaSccrollNode = new Element("div", {"styles": this.css.projectionListAreaSccrollNode}).inject(this.projectionListNode);
        this.projectionListAreaNode = new Element("div", {"styles": this.css.projectionListAreaNode}).inject(this.projectionListAreaSccrollNode);

        this.loadProjectionListResize();

        this.loadProjectionList();
    },

    loadProjectionListResize: function(){
//		var size = this.propertyNode.getSize();
//		var position = this.propertyResizeBar.getPosition();
        this.projectionListResize = new Drag(this.projectionListResizeNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.projectionListAreaSccrollNode.getSize();
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
                this.projectionListNode.setStyle("width", width);
            }.bind(this)
        });
    },

    loadProjectionList: function() {
        this.actions.listProjection(this.application.id, function (json) {
            json.data.each(function(projection){
                this.createListProjectionItem(projection);
            }.bind(this));
        }.bind(this), null, false);
    },
    createListProjectionItem: function(projection, isNew){
        var _self = this;
        var listProjectionItem = new Element("div", {"styles": this.css.listProjectionItem}).inject(this.projectionListAreaNode, (isNew) ? "top": "bottom");
        var listProjectionItemIcon = new Element("div", {"styles": this.css.listProjectionItemIcon}).inject(listProjectionItem);
        var listProjectionItemText = new Element("div", {"styles": this.css.listProjectionItemText, "text": (projection.name) ? projection.name+" ("+projection.alias+")" : this.lp.newProjection}).inject(listProjectionItem);

        listProjectionItem.store("projection", projection);
        listProjectionItem.addEvents({
            "dblclick": function(e){_self.loadProjectionByData(this, e);},
            "mouseover": function(){if (_self.currentListProjectionItem!=this) this.setStyles(_self.css.listProjectionItem_over);},
            "mouseout": function(){if (_self.currentListProjectionItem!=this) this.setStyles(_self.css.listProjectionItem);}
        });
    },

    loadScriptByData: function(node, e){
        var projection = node.retrieve("projection");

        var openNew = true;
        for (var i = 0; i<this.projectionTab.pages.length; i++){
            if (projection.id==this.projectionTab.pages[i].projection.data.id){
                this.projectionTab.pages[i].showTabIm();
                openNew = false;
                break;
            }
        }
        if (openNew){
            this.loadProjectionData(projection.id, function(data){
                var projection = new MWF.xApplication.process.ProjectionDesigner.Projection(this, data);
                projection.load();
            }.bind(this), true);
        }
    },
	
	//loadContentNode------------------------------
    loadContentNode: function(toolbarCallback, contentCallback){
		this.contentToolbarNode = new Element("div", {
			"styles": this.css.contentToolbarNode
		}).inject(this.contentNode);
		this.loadContentToolbar(toolbarCallback);
		
		this.editContentNode = new Element("div", {
			"styles": this.css.editContentNode
		}).inject(this.contentNode);

		this.loadEditContent(function(){
		//	if (this.designDcoument) this.designDcoument.body.setStyles(this.css.designBody);
			if (this.designNode) this.designNode.setStyles(this.css.designNode);
            if (contentCallback) contentCallback();
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
				this.notice("request projectionToolbars error: "+xhr.responseText, "error");
			}.bind(this)
		});
		r.send();
	},

    loadEditContent: function(callback){
        this.designNode = new Element("div", {
            "styles": this.css.designNode
        }).inject(this.editContentNode);

        MWF.require("MWF.widget.Tab", function(){
            this.projectionTab = new MWF.widget.Tab(this.designNode, {"style": "script"});
            this.projectionTab.load();
        }.bind(this), false);
	},
	
	//loadProperty------------------------
	loadProperty: function(){
		this.propertyTitleNode = new Element("div", {
			"styles": this.css.propertyTitleNode,
			"text": MWF.APPPJD.LP.property
		}).inject(this.propertyNode);
		
		this.propertyResizeBar = new Element("div", {
			"styles": this.css.propertyResizeBar
		}).inject(this.propertyNode);
		this.loadPropertyResize();
		
		this.propertyContentNode = new Element("div", {
			"styles": this.css.propertyContentNode
		}).inject(this.propertyNode);

		this.propertyContentArea = new Element("div", {
			"styles": this.css.propertyContentArea
		}).inject(this.propertyContentNode);

        this.setPropertyContent();
	},

    setPropertyContent: function(){
        var node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.id+":"}).inject(this.propertyContentArea);
        this.propertyIdNode = new Element("div", {"styles": this.css.propertyTextNode, "text": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.name+":"}).inject(this.propertyContentArea);
        this.propertyNameNode = new Element("input", {"styles": this.css.propertyInputNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.description+":"}).inject(this.propertyContentArea);
        this.propertyDescriptionNode = new Element("textarea", {"styles": this.css.propertyInputAreaNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.enable+":"}).inject(this.propertyContentArea);
        this.propertyEnableNode = new Element("select", {
            "styles": this.css.propertyInputNode,
            "html": "<option value='true'>是</option><option value='false' selected>否</option>"
        }).inject(this.propertyContentArea);
    },
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
			}.bind(this)
		});
	},
	
	//resizeNode------------------------------------------------
	resizeNode: function(){
        if (!this.isMax){
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

            //this.setPropertyContentResize();

            titleSize = this.projectionListTitleNode.getSize();
            titleMarginTop = this.projectionListTitleNode.getStyle("margin-top").toFloat();
            titleMarginBottom = this.projectionListTitleNode.getStyle("margin-bottom").toFloat();
            titlePaddingTop = this.projectionListTitleNode.getStyle("padding-top").toFloat();
            titlePaddingBottom = this.projectionListTitleNode.getStyle("padding-bottom").toFloat();
            nodeMarginTop = this.projectionListAreaSccrollNode.getStyle("margin-top").toFloat();
            nodeMarginBottom = this.projectionListAreaSccrollNode.getStyle("margin-bottom").toFloat();

            y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom+nodeMarginTop+nodeMarginBottom;
            y = nodeSize.y-y;
            this.projectionListAreaSccrollNode.setStyle("height", ""+y+"px");
            this.projectionListResizeNode.setStyle("height", ""+y+"px");
        }
	},
	
	//loadProjection------------------------------------------
    loadProjection: function(){
		this.getProjectionData(this.options.id, function(data){

			this.projection = new MWF.xApplication.process.ProjectionDesigner.Projection(this, data);
			this.projection.load();

            if (this.status){
                if (this.status.openProjections){
                    this.status.openProjections.each(function(id){
                        this.loadProjectionData(id, function(data){
                            var showTab = true;
                            if (this.status.currentId){
                                if (this.status.currentId!==data.id) showTab = false;
                            }
                            var projection = new MWF.xApplication.process.ProjectionDesigner.Projection(this, data, {"showTab": showTab});
                            projection.load();
                        }.bind(this), true);
                    }.bind(this));
                }
            }
		}.bind(this));
	},

    getProjectionData: function(id, callback){
		if (!id){
			this.loadNewProjectionData(callback);
		}else{
			this.loadProjectionData(id, callback);
		}
	},

    loadNewProjectionData: function(callback){
        this.actions.getUUID(function(id){
            var data = {
                "name": "",
                "id": id,
                "application": this.application.id,
                "process": "",
                "enable": false,
                "description": "",
                "type": "",
                "data": [],
                "isNewProjection": true,
            };
            this.createListProjectionItem(data, true);
            if (callback) callback(data);
        }.bind(this))
	},

    loadProjectionData: function(id, callback, notSetTile){
		this.actions.getScript(id, function(json){
			if (json){
				var data = json.data;

                if (!notSetTile){
                    this.setTitle(this.options.appTitle + "-"+data.name);
                    this.taskitem.setText(this.options.appTitle + "-"+data.name);
                    this.options.appTitle = this.options.appTitle + "-"+data.name;
                }

                if (!this.application){
                    this.actions.getApplication(this.data.application, function(json){
                        this.application = {"name": json.data.name, "id": json.data.id};
                        if (callback) callback(data);
                    }.bind(this));
                }else{
                    if (callback) callback(data);
                }
			}
		}.bind(this));
	},

    saveProjection: function(){
        if (this.scriptTab.showPage){
            var script = this.scriptTab.showPage.script;
            script.save(function(){
                if (script==this.script){
                    var name = script.data.name;
                    this.setTitle(MWF.APPPJD.LP.title + "-"+name);
                    this.options.desktopReload = true;
                    this.options.id = script.data.id;
                }
            }.bind(this));
        }
	},
    execProjection: function(){

    },

    saveDictionaryAs: function(){
        this.dictionary.saveAs();
	},
    dictionaryExplode: function(){
        this.dictionary.explode();
    },
    dictionaryImplode: function(){
        this.dictionary.implode();
    },
	recordStatus: function(){
        if (this.scriptTab){
            var openScripts = [];
            this.scriptTab.pages.each(function(page){
                if (page.script.data.id!=this.options.id) openScripts.push(page.script.data.id);
            }.bind(this));
            var currentId = this.scriptTab.showPage.script.data.id;
            var status = {
                "id": this.options.id,
                "application": this.application,
                "openScripts": openScripts,
                "currentId": currentId
            };
            return status;
        }
		return {"id": this.options.id, "application": this.application};
	}
});
