MWF.xApplication.portal.ScriptDesigner.options = {
	"multitask": true,
	"executable": false
};
//MWF.xDesktop.requireApp("portal.PortalManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("portal.ScriptDesigner", "Script", null, false);
MWF.require("MWF.xDesktop.UserData", null, false);
MWF.xApplication.portal.ScriptDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "portal.ScriptDesigner",
		"icon": "icon.png",
		"title": MWF.xApplication.portal.ScriptDesigner.LP.title,
		"appTitle": MWF.xApplication.portal.ScriptDesigner.LP.title,
		"id": "",
		"actions": null,
		"category": null,
		"portalData": null
	},
	onQueryLoad: function(){
		if (this.status){
            this.options.application = this.status.applicationId;
            this.application = this.status.application;
			this.options.id = this.status.id;
		}
		if (!this.options.id){
			this.options.desktopReload = false;
			this.options.title = this.options.title + "-"+MWF.xApplication.portal.ScriptDesigner.LP.newScript;
		}

        this.actions = MWF.Actions.get("x_portal_assemble_designer");
		//this.actions = new MWF.xApplication.portal.PortalManager.Actions.RestActions();
		
		this.lp = MWF.xApplication.portal.ScriptDesigner.LP;

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
				this.openScript();
			}.bind(this));
		}else{
			this.openScript();
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
	openScript: function(){
        this.getApplication(function(){
            this.loadNodes();
            this.loadScriptListNodes();
            this.loadContentNode(function(){

                this.loadProperty();
                //	this.loadTools();
                this.resizeNode();
                this.addEvent("resize", this.resizeNode.bind(this));
                this.loadScript();

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
        this.scriptListNode = new Element("div", {
            "styles": this.css.scriptListNode
        }).inject(this.node);

		this.propertyNode = new Element("div", {
			"styles": this.css.propertyNode
		}).inject(this.node);

		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.node);
	},
    //loadScriptList-------------------------------
    loadScriptListNodes: function(){
        this.scriptListTitleNode = new Element("div", {
            "styles": this.css.scriptListTitleNode,
            "text": MWF.xApplication.portal.ScriptDesigner.LP.scriptLibrary
        }).inject(this.scriptListNode);

        this.scriptListResizeNode = new Element("div", {"styles": this.css.scriptListResizeNode}).inject(this.scriptListNode);
        this.scriptListAreaSccrollNode = new Element("div", {"styles": this.css.scriptListAreaSccrollNode}).inject(this.scriptListNode);
        this.scriptListAreaNode = new Element("div", {"styles": this.css.scriptListAreaNode}).inject(this.scriptListAreaSccrollNode);

        this.loadScriptListResize();

        this.loadScriptList();
    },

    loadScriptListResize: function(){
//		var size = this.propertyNode.getSize();
//		var position = this.propertyResizeBar.getPosition();
        this.scriptListResize = new Drag(this.scriptListResizeNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.scriptListAreaSccrollNode.getSize();
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
                this.scriptListNode.setStyle("width", width);
            }.bind(this)
        });
    },

    loadScriptList: function() {
        this.actions.listScript(this.application.id, function (json) {
            json.data.each(function(script){
                this.createListScriptItem(script);
            }.bind(this));
        }.bind(this), null, false);
    },
    createListScriptItem: function(script, isNew){
        var _self = this;
        var listScriptItem = new Element("div", {"styles": this.css.listScriptItem}).inject(this.scriptListAreaNode, (isNew) ? "top": "bottom");
        var listScriptItemIcon = new Element("div", {"styles": this.css.listScriptItemIcon}).inject(listScriptItem);
        var listScriptItemText = new Element("div", {"styles": this.css.listScriptItemText, "text": (script.name) ? script.name+" ("+script.alias+")" : this.lp.newScript}).inject(listScriptItem);

        listScriptItem.store("script", script);
        listScriptItem.addEvents({
            "dblclick": function(e){_self.loadScriptByData(this, e);},
            "mouseover": function(){if (_self.currentListScriptItem!=this) this.setStyles(_self.css.listScriptItem_over);},
            "mouseout": function(){if (_self.currentListScriptItem!=this) this.setStyles(_self.css.listScriptItem);}
        });

        this.listScriptItemMove(listScriptItem);

    },
    createScriptListCopy: function(node){
        var copyNode = node.clone().inject(this.node);
        copyNode.position({
            "relativeTo": node,
            "position": "upperLeft",
            "edge": "upperLeft"
        });
        var size = copyNode.getSize();
        copyNode.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px",
            "z-index": 50001,
        });
        return copyNode;
    },
    listDragEnter: function(dragging, inObj){
        var markNode = inObj.retrieve("markNode");
        if (!markNode){
            var size = inObj.getSize();
            markNode = new Element("div", {"styles": this.css.dragListItemMark}).inject(this.node);
            markNode.setStyles({
                "width": ""+size.x+"px",
                "height": ""+size.y+"px",
                "position": "absolute",
                "background-color": "#666",
                "z-index": 50000,
                "opacity": 0.3
             //   "border": "2px solid #ffba00"
            });
            markNode.position({
                "relativeTo": inObj,
                "position": "upperLeft",
                "edge": "upperLeft"
            });
            var y = markNode.getStyle("top").toFloat()-1;
            var x = markNode.getStyle("left").toFloat()-2;
            markNode.setStyles({
                "left": ""+x+"px",
                "top": ""+y+"px",
            });
            inObj.store("markNode", markNode);
        }
    },
    listDragLeave: function(dragging, inObj){
        var markNode = inObj.retrieve("markNode");
        if (markNode) markNode.destroy();
        inObj.eliminate("markNode");
    },
    listScriptItemMove: function(node){
        var iconNode = node.getFirst();
        iconNode.addEvent("mousedown", function(e){
            var script = node.retrieve("script");
            if (script.id!=this.scriptTab.showPage.script.data.id){
                var copyNode = this.createScriptListCopy(node);

                var droppables = [this.designNode, this.propertyDomArea];
                var listItemDrag = new Drag.Move(copyNode, {
                    "droppables": droppables,
                    "onEnter": function(dragging, inObj){
                        this.listDragEnter(dragging, inObj);
                    }.bind(this),
                    "onLeave": function(dragging, inObj){
                        this.listDragLeave(dragging, inObj);
                    }.bind(this),
                    "onDrag": function(e){
                        //nothing
                    }.bind(this),
                    "onDrop": function(dragging, inObj){
                        if (inObj){
                            this.addIncludeScript(script);
                            this.listDragLeave(dragging, inObj);
                            copyNode.destroy();
                        }else{
                            copyNode.destroy();
                        }
                    }.bind(this),
                    "onCancel": function(dragging){
                        copyNode.destroy();
                    }.bind(this)
                });
                listItemDrag.start(e);
            }
        }.bind(this));
    },
    addIncludeScript: function(script){
        var currentScript = this.scriptTab.showPage.script;
        if (currentScript.data.dependScriptList.indexOf(script.name)==-1){
            currentScript.data.dependScriptList.push(script.name);
            this.addIncludeToList(script.name);
        }
    },
    addIncludeToList: function(name){
        this.actions.getScriptByName(name, this.application.id, function(json){
            var script = json.data;
            var includeScriptItem = new Element("div", {"styles": this.css.includeScriptItem}).inject(this.propertyIncludeListArea);
            var includeScriptItemAction = new Element("div", {"styles": this.css.includeScriptItemAction}).inject(includeScriptItem);
            var includeScriptItemText = new Element("div", {"styles": this.css.includeScriptItemText}).inject(includeScriptItem);
            includeScriptItemText.set("text", script.name+" ("+script.alias+")");
            includeScriptItem.store("script", script);

            var _self = this;
            includeScriptItemAction.addEvent("click", function(){
                var node = this.getParent();
                var script = node.retrieve("script");
                if (script){
                    _self.scriptTab.showPage.script.data.dependScriptList.erase(script.name);
                }
                node.destroy();
            });
        }.bind(this), function(){
            this.scriptTab.showPage.script.data.dependScriptList.erase(name);
        }.bind(this));
    },


    loadScriptByData: function(node, e){
        var script = node.retrieve("script");

        var openNew = true;
        for (var i = 0; i<this.scriptTab.pages.length; i++){
            if (script.id==this.scriptTab.pages[i].script.data.id){
                this.scriptTab.pages[i].showTabIm();
                openNew = false;
                break;
            }
        }
        if (openNew){
            this.loadScriptData(script.id, function(data){
                var script = new MWF.xApplication.portal.ScriptDesigner.Script(this, data);
                script.load();
            }.bind(this), true);
        }
    },
	
	//loadContentNode------------------------------
    loadContentNode: function(toolbarCallback, contentCallback){
		this.contentToolbarNode = new Element("div#contentToolbarNode", {
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
                var _self = this;
                //this.styleSelectNode = toolbarNode.getElement("select");
                //this.styleSelectNode.addEvent("change", function(){
                //    _self.changeEditorStyle(this);
                //});
                this.styleSelectNode = toolbarNode.getElement("select[MWFnodetype='theme']");
                this.styleSelectNode.addEvent("change", function(){
                    _self.changeEditorStyle(this);
                });

                this.fontsizeSelectNode = toolbarNode.getElement("select[MWFnodetype='fontSize']");
                this.fontsizeSelectNode.addEvent("change", function(){
                    _self.changeFontSize(this);
                });

				if (callback) callback();
			}.bind(this));
		}.bind(this));
	},
    changeFontSize: function(node){
        var idx = node.selectedIndex;
        var value = node.options[idx].value;
        //var editorData = null;
        this.scriptTab.pages.each(function(page){
            //if (!editorData) editorData = page.invoke.editor.editorData;
            var editor = page.script.editor.editor;
            if (editor) editor.setFontSize(value);
        }.bind(this));
        //if (!editorData) editorData = MWF.editorData;
        //editorData.javainvokeEditor.theme = value;
        if (!MWF.editorData){
            MWF.editorData = {
                "javascriptEditor": {
                    "theme": "tomorrow",
                    "fontSize" : "12px"
                }
            };
        }
        MWF.editorData.javascriptEditor["fontSize"] = value;

        MWF.UD.putData("editor", MWF.editorData);

    },
    changeEditorStyle: function(node){
        var idx = node.selectedIndex;
        var value = node.options[idx].value;
        //var editorData = null;
        this.scriptTab.pages.each(function(page){
            //if (!editorData) editorData = page.script.editor.editorData;
            var editor = page.script.editor.editor;
            if (editor) editor.setTheme("ace/theme/"+value);
        }.bind(this));
        //if (!editorData) editorData = MWF.editorData;
        //editorData.javascriptEditor.theme = value;
        if (!MWF.editorData){
            MWF.editorData = {
                "javascriptEditor": {
                    "theme": "tomorrow",
                    "fontSize" : "12px"
                }
            };
        }
        MWF.editorData.javascriptEditor.theme = value;

        MWF.UD.putData("editor", MWF.editorData);

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
				this.notice("request portalToolbars error: "+xhr.responseText, "error");
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
            this.scriptTab.pages.each(function(page){
                page.script.setAreaNodeSize();
            });
            this.isMax = true;
        }else{
            this.isMax = false;
            this.designNode.inject(this.editContentNode);
            this.designNode.setStyles(this.css.designNode);
            this.designNode.setStyles({
                "position": "static"
            });
            this.resizeNode();
            this.scriptTab.pages.each(function(page){
                page.script.setAreaNodeSize();
            });
        }

    },
    loadEditContent: function(callback){
        this.designNode = new Element("div", {
            "styles": this.css.designNode
        }).inject(this.editContentNode);

        MWF.require("MWF.widget.Tab", function(){
            this.scriptTab = new MWF.widget.Tab(this.designNode, {"style": "script"});
            this.scriptTab.load();
        }.bind(this), false);


        //MWF.require("MWF.widget.ScrollBar", function(){
            //    new MWF.widget.ScrollBar(this.designNode, {"distance": 100});
            //}.bind(this));
	},
	
	//loadProperty------------------------
	loadProperty: function(){
		this.propertyTitleNode = new Element("div", {
			"styles": this.css.propertyTitleNode,
			"text": MWF.xApplication.portal.ScriptDesigner.LP.property
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

        this.setPropertyContent();
        this.setIncludeNode();
	},
    setIncludeNode: function(){
        this.includeTitleNode = new Element("div", {"styles": this.css.includeTitleNode}).inject(this.propertyDomArea);
        this.includeTitleActionNode = new Element("div", {"styles": this.css.includeTitleActionNode}).inject(this.includeTitleNode);
        this.includeTitleTextNode = new Element("div", {"styles": this.css.includeTitleTextNode, "text": this.lp.include}).inject(this.includeTitleNode);
        this.includeTitleActionNode.addEvent("click", function(){
            this.addInclude();
        }.bind(this));

        this.propertyIncludeListArea = new Element("div", {
            "styles": {"overflow": "hidden"}
        }).inject(this.propertyDomArea);
    },
    addInclude: function(){



    },
    setPropertyContent: function(){
        var node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.id+":"}).inject(this.propertyContentArea);
        this.propertyIdNode = new Element("div", {"styles": this.css.propertyTextNode, "text": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.name+":"}).inject(this.propertyContentArea);
        this.propertyNameNode = new Element("input", {"styles": this.css.propertyInputNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.alias+":"}).inject(this.propertyContentArea);
        this.propertyAliasNode = new Element("input", {"styles": this.css.propertyInputNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.description+":"}).inject(this.propertyContentArea);
        this.propertyDescriptionNode = new Element("textarea", {"styles": this.css.propertyInputAreaNode, "value": ""}).inject(this.propertyContentArea);
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

            this.setPropertyContentResize();

            titleSize = this.scriptListTitleNode.getSize();
            titleMarginTop = this.scriptListTitleNode.getStyle("margin-top").toFloat();
            titleMarginBottom = this.scriptListTitleNode.getStyle("margin-bottom").toFloat();
            titlePaddingTop = this.scriptListTitleNode.getStyle("padding-top").toFloat();
            titlePaddingBottom = this.scriptListTitleNode.getStyle("padding-bottom").toFloat();
            nodeMarginTop = this.scriptListAreaSccrollNode.getStyle("margin-top").toFloat();
            nodeMarginBottom = this.scriptListAreaSccrollNode.getStyle("margin-bottom").toFloat();

            y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom+nodeMarginTop+nodeMarginBottom;
            y = nodeSize.y-y;
            this.scriptListAreaSccrollNode.setStyle("height", ""+y+"px");
            this.scriptListResizeNode.setStyle("height", ""+y+"px");
        }
	},
	
	//loadForm------------------------------------------
    loadScript: function(){
        //this.scriptTab.addTab(node, title);
		this.getScriptData(this.options.id, function(data){
			this.script = new MWF.xApplication.portal.ScriptDesigner.Script(this, data);
			this.script.load();

            if (this.status){
                if (this.status.openScripts){
                    this.status.openScripts.each(function(id){
                        this.loadScriptData(id, function(data){
                            var showTab = true;
                            if (this.status.currentId){
                                if (this.status.currentId!=data.id) showTab = false;
                            }
                            var script = new MWF.xApplication.portal.ScriptDesigner.Script(this, data, {"showTab": showTab});
                            script.load();
                        }.bind(this), true);
                    }.bind(this));
                }
            };
            if (!this.scriptHelpMenu){
                MWF.require("MWF.widget.ScriptHelp", function(){
                    this.scriptHelpMenu = new MWF.widget.ScriptHelp($("MWFScriptAutoCode"), this.script.editor);
                    this.scriptHelpMenu.getEditor = function(){
                        if (this.scriptTab.showPage) return this.scriptTab.showPage.script.editor.editor;
                        return null;
                    }.bind(this)
                }.bind(this));
            }
		}.bind(this));
	},

    getScriptData: function(id, callback){
		if (!id){
			this.loadNewScriptData(callback);
		}else{
			this.loadScriptData(id, callback);
		}
	},

    loadNewScriptData: function(callback){
        this.actions.getUUID(function(id){
            var data = {
                "name": "",
                "id": id,
                "application": this.application.id,
                "alias": "",
                "description": "",
                "language": "javascript",
                "dependScriptList": [],
                "isNewScript": true,
                "text": ""
            }
            this.createListScriptItem(data, true);
            if (callback) callback(data);
        }.bind(this))
	},
    loadScriptData: function(id, callback, notSetTile){
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

    saveScript: function(){
        if (this.scriptTab.showPage){
            var script = this.scriptTab.showPage.script;
            script.save(function(){
                if (script==this.script){
                    var name = script.data.name;
                    this.setTitle(MWF.xApplication.portal.ScriptDesigner.LP.title + "-"+name);
                    this.options.desktopReload = true;
                    this.options.id = script.data.id;
                }
            }.bind(this));
        }
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
