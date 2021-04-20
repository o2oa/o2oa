MWF.SRVID = MWF.xApplication.service.InvokeDesigner = MWF.xApplication.service.InvokeDesigner || {};
MWF.SRVID.options = {
	"multitask": true,
	"executable": false
};
//MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("service.InvokeDesigner", "Invoke", null, false);
MWF.require("MWF.xDesktop.UserData", null, false);
MWF.xApplication.service.InvokeDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "service.InvokeDesigner",
		"icon": "icon.png",
		"title": MWF.SRVID.LP.title,
		"appTitle": MWF.SRVID.LP.title,
		"id": "",
		"actions": null,
		"category": null,
		"serviceData": null
	},
	onQueryLoad: function(){
		if (this.status){
			this.options.id = this.status.id;
		}
		if (!this.options.id){
			this.options.desktopReload = false;
			this.options.title = this.options.title + "-"+MWF.SRVID.LP.newInvoke;
		}

        this.actions = MWF.Actions.get("x_program_center");
		//this.actions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
		
		this.lp = MWF.xApplication.service.InvokeDesigner.LP;

        this.addEvent("queryClose", function(e){
            if (this.explorer){
                this.explorer.reload();
            }
        }.bind(this));
//		this.processData = this.options.processData;
	},
	
	loadApplication: function(callback){
		this.createNode();
		if (!this.options.isRefresh){
			this.maxSize(function(){
				this.openInvoke();
			}.bind(this));
		}else{
			this.openInvoke();
		}
		if (callback) callback();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
	},
	openInvoke: function(){
        this.loadNodes();
        this.loadInvokeListNodes();
        this.loadContentNode(function(){

            this.loadProperty();
            //	this.loadTools();
            this.resizeNode();
            this.addEvent("resize", this.resizeNode.bind(this));
            this.loadInvoke();

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
	},
	loadNodes: function(){
        this.invokeListNode = new Element("div", {
            "styles": this.css.invokeListNode
        }).inject(this.node);

		this.propertyNode = new Element("div", {
			"styles": this.css.propertyNode
		}).inject(this.node);

		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.node);
	},
    //loadInvokeList-------------------------------
    loadInvokeListNodes: function(){
        this.invokeListTitleNode = new Element("div", {
            "styles": this.css.invokeListTitleNode,
            "text": MWF.SRVID.LP.invokeLibrary
        }).inject(this.invokeListNode);

        this.invokeListResizeNode = new Element("div", {"styles": this.css.invokeListResizeNode}).inject(this.invokeListNode);
        this.invokeListAreaSccrollNode = new Element("div.invokeListAreaSccrollNode", {"styles": this.css.invokeListAreaSccrollNode}).inject(this.invokeListNode);
        this.invokeListAreaNode = new Element("div", {"styles": this.css.invokeListAreaNode}).inject(this.invokeListAreaSccrollNode);

        this.loadInvokeListResize();

        this.loadInvokeList();
    },

    loadInvokeListResize: function(){
//		var size = this.propertyNode.getSize();
//		var position = this.propertyResizeBar.getPosition();
        this.invokeListResize = new Drag(this.invokeListResizeNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.invokeListAreaSccrollNode.getSize();
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
                this.invokeListNode.setStyle("width", width);
            }.bind(this)
        });
    },

    loadInvokeList: function() {
        this.actions.listInvoke(function (json) {
            json.data.each(function(invoke){
                this.createListInvokeItem(invoke);
            }.bind(this));
        }.bind(this), null, false);
    },
    createListInvokeItem: function(invoke, isNew){
        var _self = this;
        var listInvokeItem = new Element("div", {"styles": this.css.listInvokeItem}).inject(this.invokeListAreaNode, (isNew) ? "top": "bottom");
        var listInvokeItemIcon = new Element("div", {"styles": this.css.listInvokeItemIcon}).inject(listInvokeItem);
        var listInvokeItemText = new Element("div", {"styles": this.css.listInvokeItemText, "text": (invoke.name) ? invoke.name+" ("+invoke.alias+")" : this.lp.newInvoke}).inject(listInvokeItem);

        listInvokeItem.store("invoke", invoke);
        listInvokeItem.addEvents({
            "dblclick": function(e){_self.loadInvokeByData(this, e);},
            "mouseover": function(){if (_self.currentListInvokeItem!=this) this.setStyles(_self.css.listInvokeItem_over);},
            "mouseout": function(){if (_self.currentListInvokeItem!=this) this.setStyles(_self.css.listInvokeItem);}
        });

        this.listInvokeItemMove(listInvokeItem);

    },
    createInvokeListCopy: function(node){
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
            "z-index": 50001
        });
        return copyNode;
    },
    listDrinvokeer: function(dragging, inObj){
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
                "top": ""+y+"px"
            });
            inObj.store("markNode", markNode);
        }
    },
    listDragLeave: function(dragging, inObj){
        var markNode = inObj.retrieve("markNode");
        if (markNode) markNode.destroy();
        inObj.eliminate("markNode");
    },
    listInvokeItemMove: function(node){
        var iconNode = node.getFirst();
        iconNode.addEvent("mousedown", function(e){
            var invoke = node.retrieve("invoke");
            if (invoke.id!=this.invokeTab.showPage.invoke.data.id){
                var copyNode = this.createInvokeListCopy(node);

                var droppables = [this.designNode, this.propertyDomArea];
                var listItemDrag = new Drag.Move(copyNode, {
                    "droppables": droppables,
                    "onEnter": function(dragging, inObj){
                        this.listDrinvokeer(dragging, inObj);
                    }.bind(this),
                    "onLeave": function(dragging, inObj){
                        this.listDragLeave(dragging, inObj);
                    }.bind(this),
                    "onDrag": function(e){
                        //nothing
                    }.bind(this),
                    "onDrop": function(dragging, inObj){
                        if (inObj){
                            //this.addIncludeInvoke(invoke);
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
    addIncludeInvoke: function(invoke){
        var currentInvoke = this.invokeTab.showPage.invoke;
        if (currentInvoke.data.dependInvokeList.indexOf(invoke.name)==-1){
            currentInvoke.data.dependInvokeList.push(invoke.name);
            this.addIncludeToList(invoke.name);
        }
    },
    addIncludeToList: function(name){
        this.actions.getInvokeByName(name, function(json){
            var invoke = json.data;
            var includeInvokeItem = new Element("div", {"styles": this.css.includeInvokeItem}).inject(this.propertyIncludeListArea);
            var includeInvokeItemAction = new Element("div", {"styles": this.css.includeInvokeItemAction}).inject(includeInvokeItem);
            var includeInvokeItemText = new Element("div", {"styles": this.css.includeInvokeItemText}).inject(includeInvokeItem);
            includeInvokeItemText.set("text", invoke.name+" ("+invoke.alias+")");
            includeInvokeItem.store("invoke", invoke);

            var _self = this;
            includeInvokeItemAction.addEvent("click", function(){
                var node = this.getParent();
                var invoke = node.retrieve("invoke");
                if (invoke){
                    _self.invokeTab.showPage.invoke.data.dependInvokeList.erase(invoke.name);
                }
                node.destroy();
            });
        }.bind(this), function(){
            this.invokeTab.showPage.invoke.data.dependInvokeList.erase(name);
        }.bind(this));
    },


    loadInvokeByData: function(node, e){
        var invoke = node.retrieve("invoke");

        var openNew = true;
        for (var i = 0; i<this.invokeTab.pages.length; i++){
            if (invoke.id==this.invokeTab.pages[i].invoke.data.id){
                this.invokeTab.pages[i].showTabIm();
                openNew = false;
                break;
            }
        }
        if (openNew){
            this.loadInvokeData(invoke.id, function(data){
                var invoke = new MWF.xApplication.service.InvokeDesigner.Invoke(this, data);
                invoke.load();
            }.bind(this), true);
        }
        //var _self = this;
        //var options = {
        //    "onQueryLoad": function(){
        //        this.actions = _self.actions;
        //        this.options.id = invoke.id;
        //        this.application = _self.application;
        //    }
        //};
        //this.desktop.openApplication(e, "service.InvokeDesigner", options);
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
                this.styleSelectNode = toolbarNode.getElement("select[MWFnodetype='theme']");
                this.styleSelectNode.addEvent("change", function(){
                    _self.changeEditorStyle(this);
                });

                this.fontsizeSelectNode = toolbarNode.getElement("select[MWFnodetype='fontSize']");
                this.fontsizeSelectNode.addEvent("change", function(){
                    _self.changeFontSize(this);
                });

                this.editorSelectNode = toolbarNode.getElement("select[MWFnodetype='editor']");
                this.editorSelectNode.addEvent("change", function(){
                    _self.changeEditor(this);
                });

                this.monacoStyleSelectNode = toolbarNode.getElement("select[MWFnodetype='monaco-theme']");
                this.monacoStyleSelectNode.addEvent("change", function(){
                    _self.changeEditorStyle(this);
                });

				if (callback) callback();
			}.bind(this));
		}.bind(this));
	},

    changeEditor: function(node){
        var idx = node.selectedIndex;
        var value = node.options[idx].value;

        if (!MWF.editorData){
            MWF.editorData = {
                "javascriptEditor": {
                    "monaco_theme": "vs",
                    "theme": "tomorrow",
                    "fontSize" : "12px"
                }
            };
        }
        MWF.editorData.javascriptEditor["editor"] = value;
        MWF.UD.putData("editor", MWF.editorData);

        this.invokeTab.pages.each(function(page){
            var editor = page.invoke.editor;
            if (editor) editor.changeEditor(value);
        }.bind(this));

        if (value=="ace"){
            this.monacoStyleSelectNode.hide();
            this.styleSelectNode.show();
        }else{
            this.monacoStyleSelectNode.show();
            this.styleSelectNode.hide();
        }

    },
    changeFontSize: function(node){
        var idx = node.selectedIndex;
        var value = node.options[idx].value;
        //var editorData = null;
        this.invokeTab.pages.each(function(page){
            //if (!editorData) editorData = page.invoke.editor.editorData;
            var editor = page.invoke.editor;
            if (editor) editor.setFontSize(value);
        }.bind(this));
        //if (!editorData) editorData = MWF.editorData;
        //editorData.javainvokeEditor.theme = value;
        if (!MWF.editorData){
            MWF.editorData = {
                "javascriptEditor": {
                    "monaco_theme": "vs",
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
        this.invokeTab.pages.each(function(page){
            //if (!editorData) editorData = page.script.editor.editorData;
            var editor = page.invoke.editor;
            if (editor) editor.setTheme(value);
        }.bind(this));
        //if (!editorData) editorData = MWF.editorData;
        //editorData.javascriptEditor.theme = value;
        if (!MWF.editorData){
            MWF.editorData = {
                "javascriptEditor": {
                    "monaco_theme": "vs",
                    "theme": "tomorrow",
                    "fontSize" : "12px"
                }
            };
        }

        if (MWF.editorData.javascriptEditor.editor === "monaco"){
            MWF.editorData.javascriptEditor.monaco_theme = value;
        }else{
            MWF.editorData.javascriptEditor.theme = value;
        }

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
            this.invokeTab.pages.each(function(page){
                page.invoke.setAreaNodeSize();
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
            this.invokeTab.pages.each(function(page){
                page.invoke.setAreaNodeSize();
            });
        }

    },
    loadEditContent: function(callback){
        this.designNode = new Element("div", {
            "styles": this.css.designNode
        }).inject(this.editContentNode);

        MWF.require("MWF.widget.Tab", function(){
            this.invokeTab = new MWF.widget.Tab(this.designNode, {"style": "script"});
            this.invokeTab.load();
        }.bind(this), false);


        //MWF.require("MWF.widget.ScrollBar", function(){
            //    new MWF.widget.ScrollBar(this.designNode, {"distance": 100});
            //}.bind(this));
	},
	
	//loadProperty------------------------
	loadProperty: function(){
		this.propertyTitleNode = new Element("div", {
			"styles": this.css.propertyTitleNode,
			"text": MWF.SRVID.LP.property
		}).inject(this.propertyNode);
		
		this.propertyResizeBar = new Element("div", {
			"styles": this.css.propertyResizeBar
		}).inject(this.propertyNode);
		this.loadPropertyResize();
		
		this.propertyContentNode = new Element("div", {
			"styles": this.css.propertyContentNode
		}).inject(this.propertyNode);
		
		//this.propertyDomArea = new Element("div", {
		//	"styles": this.css.propertyDomArea
		//}).inject(this.propertyContentNode);
		
		//this.propertyDomPercent = 0.3;
		//this.propertyContentResizeNode = new Element("div", {
		//	"styles": this.css.propertyContentResizeNode
		//}).inject(this.propertyContentNode);
		
		this.propertyContentArea = new Element("div", {
			"styles": this.css.propertyContentArea
		}).inject(this.propertyContentNode);
		
		//this.loadPropertyContentResize();

        this.setPropertyContent();
        //this.setIncludeNode();
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

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.remoteAddrRegex+":"}).inject(this.propertyContentArea);
        this.propertyRemoteAddrRegexNode = new Element("input", {"styles": this.css.propertyInputNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.lastStartTime+":"}).inject(this.propertyContentArea);
        this.propertyLastStartTimeNode = new Element("div", {"styles": this.css.propertyTextNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.lastEndTime+":"}).inject(this.propertyContentArea);
        this.propertyLastEndTimeNode = new Element("div", {"styles": this.css.propertyTextNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.description+":"}).inject(this.propertyContentArea);
        this.propertyDescriptionNode = new Element("textarea", {"styles": this.css.propertyInputAreaNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.enableToken+":"}).inject(this.propertyContentArea);
        this.propertyEnableTokenNode = new Element("select", {"styles": this.css.propertySelectNode }).inject(this.propertyContentArea);
        new Element("option" , {  "value" : "true", "text" : this.lp.true  }).inject(this.propertyEnableTokenNode);
        new Element("option" , {  "value" : "false", "text" : this.lp.false  }).inject(this.propertyEnableTokenNode);
        new Element("div", {"styles": this.css.propertyTextNode, "text": this.lp.enableTokenInfo}).setStyles({
            "word-break":"break-all",
            "height" : "auto",
            "line-height": "18px",
            "margin-top": "10px",
            "color": "#999999"
        }).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.isEnable+":"}).inject(this.propertyContentArea);
        this.propertyEnableNode = new Element("select", {"styles": this.css.propertySelectNode }).inject(this.propertyContentArea);
        new Element("option" , {  "value" : "true", "text" : this.lp.true  }).inject(this.propertyEnableNode);
        new Element("option" , {  "value" : "false", "text" : this.lp.false  }).inject(this.propertyEnableNode);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.invokeUri+":"}).inject(this.propertyContentArea);
        this.propertyInvokeUriNode = new Element("div", {"styles": this.css.propertyTextNode, "text": ""}).inject(this.propertyContentArea);
        this.propertyInvokeUriNode.setStyles({
            "word-break":"break-all",
            "height" : "auto"
        });

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.invokeMethod+":"}).inject(this.propertyContentArea);
        this.propertyInvokeMethodNode = new Element("div", {"styles": this.css.propertyTextNode, "text": "POST"}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.invokeHead+":"}).inject(this.propertyContentArea);
        this.propertyInvokeHeadTextNode = new Element("div", {"styles": this.css.propertyTextNode, "text": "Content-Type:application/json; charset=utf-8"}).inject(this.propertyContentArea);

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
		//var resizeNodeSize = this.propertyContentResizeNode.getSize();
		//var height = size.y-resizeNodeSize.y;
		
		//var domHeight = this.propertyDomPercent*height;
		//var contentHeight = height-domHeight;
		
		//this.propertyDomArea.setStyle("height", ""+domHeight+"px");
		//this.propertyContentArea.setStyle("height", ""+contentHeight+"px");
        this.propertyContentArea.setStyle("height", ""+size.y+"px");
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

            titleSize = this.invokeListTitleNode.getSize();
            titleMarginTop = this.invokeListTitleNode.getStyle("margin-top").toFloat();
            titleMarginBottom = this.invokeListTitleNode.getStyle("margin-bottom").toFloat();
            titlePaddingTop = this.invokeListTitleNode.getStyle("padding-top").toFloat();
            titlePaddingBottom = this.invokeListTitleNode.getStyle("padding-bottom").toFloat();
            nodeMarginTop = this.invokeListAreaSccrollNode.getStyle("margin-top").toFloat();
            nodeMarginBottom = this.invokeListAreaSccrollNode.getStyle("margin-bottom").toFloat();

            y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom+nodeMarginTop+nodeMarginBottom;
            y = nodeSize.y-y;
            this.invokeListAreaSccrollNode.setStyle("height", ""+y+"px");
            this.invokeListResizeNode.setStyle("height", ""+y+"px");
        }
	},
	
	//loadForm------------------------------------------
    loadInvoke: function(){
        //this.invokeTab.addTab(node, title);
		this.getInvokeData(this.options.id, function(data){
			this.invoke = new MWF.xApplication.service.InvokeDesigner.Invoke(this, data);
			this.invoke.load();

            if (this.status){
                if (this.status.openInvokes){
                    this.status.openInvokes.each(function(id){
                        this.loadInvokeData(id, function(data){
                            var showTab = true;
                            if (this.status.currentId){
                                if (this.status.currentId!=data.id) showTab = false;
                            }
                            var invoke = new MWF.xApplication.service.InvokeDesigner.Invoke(this, data, {"showTab": showTab});
                            invoke.load();
                        }.bind(this), true);
                    }.bind(this));
                }
            }
            //if (!this.invokeHelpMenu){
            //    MWF.require("MWF.widget.ScriptHelp", function(){
            //        this.invokeHelpMenu = new MWF.widget.ScriptHelp($("MWFScriptAutoCode"), this.invoke.editor);
            //        this.invokeHelpMenu.getEditor = function(){
            //            if (this.invokeTab.showPage) return this.invokeTab.showPage.invoke.editor.editor;
            //            return null;
            //        }.bind(this)
            //    }.bind(this));
            //}
		}.bind(this));
	},

    getInvokeData: function(id, callback){
		if (!id){
			this.loadNewInvokeData(callback);
		}else{
			this.loadInvokeData(id, callback);
		}
	},

    loadNewInvokeData: function(callback){
        MWF.Actions.get("x_cms_assemble_control").getUUID(function(id){
            var data = {
                "name": "",
                "id": id,
                "alias": "",
                "description": "",
                //"language": "javascript",
                //"dependInvokeList": [],
                "isNewInvoke": true,
                "text": "",
                "enableToken" : true,
                "enable" : true,
                "remoteAddrRegex" : "",
                "lastStartTime" : "",
                "lastEndTime" : ""
            };
            this.createListInvokeItem(data, true);
            if (callback) callback(data);
        }.bind(this))
	},
    loadInvokeData: function(id, callback, notSetTile){
		this.actions.getInvoke(id, function(json){
			if (json){
				var data = json.data;

                if (!notSetTile){
                    this.setTitle(this.options.appTitle + "-"+data.name);
                    this.taskitem.setText(this.options.appTitle + "-"+data.name);
                    this.options.appTitle = this.options.appTitle + "-"+data.name;
                }

                if (callback) callback(data);
			}
		}.bind(this));
	},

    saveInvoke: function(){
        if (this.invokeTab.showPage){
            var invoke = this.invokeTab.showPage.invoke;
            invoke.save(function(){
                if (invoke==this.invoke){
                    var name = invoke.data.name;
                    this.setTitle(MWF.SRVID.LP.title + "-"+name);
                    this.options.desktopReload = true;
                    this.options.id = invoke.data.id;
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
        if (this.invokeTab){
            var openInvokes = [];
            this.invokeTab.pages.each(function(page){
                if (page.invoke.data.id!=this.options.id) openInvokes.push(page.invoke.data.id);
            }.bind(this));
            var currentId = this.invokeTab.showPage.invoke.data.id;
            var status = {
                "id": this.options.id,
                "openInvokes": openInvokes,
                "currentId": currentId
            };
            return status;
        }
		return {"id": this.options.id};
	}
});
