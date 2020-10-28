MWF.SRVAD = MWF.xApplication.service.AgentDesigner = MWF.xApplication.service.AgentDesigner || {};
MWF.SRVAD.options = {
	"multitask": true,
	"executable": false
};
//MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("service.AgentDesigner", "Agent", null, false);
MWF.require("MWF.xDesktop.UserData", null, false);
MWF.xApplication.service.AgentDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "service.AgentDesigner",
		"icon": "icon.png",
		"title": MWF.SRVAD.LP.title,
		"appTitle": MWF.SRVAD.LP.title,
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
			this.options.title = this.options.title + "-"+MWF.SRVAD.LP.newAgent;
		}

        this.actions = MWF.Actions.get("x_program_center");
		//this.actions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
		
		this.lp = MWF.xApplication.service.AgentDesigner.LP;

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
				this.openAgent();
			}.bind(this));
		}else{
			this.openAgent();
		}
		if (callback) callback();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
	},
	openAgent: function(){
        this.loadNodes();
        this.loadAgentListNodes();
        this.loadContentNode(function(){

            this.loadProperty();
            //	this.loadTools();
            this.resizeNode();
            this.addEvent("resize", this.resizeNode.bind(this));
            this.loadAgent();

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
        this.agentListNode = new Element("div", {
            "styles": this.css.agentListNode
        }).inject(this.node);

		this.propertyNode = new Element("div", {
			"styles": this.css.propertyNode
		}).inject(this.node);

		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.node);
	},
    //loadAgentList-------------------------------
    loadAgentListNodes: function(){
        this.agentListTitleNode = new Element("div", {
            "styles": this.css.agentListTitleNode,
            "text": MWF.SRVAD.LP.agentLibrary
        }).inject(this.agentListNode);

        this.agentListResizeNode = new Element("div", {"styles": this.css.agentListResizeNode}).inject(this.agentListNode);
        this.agentListAreaSccrollNode = new Element("div.agentListAreaSccrollNode", {"styles": this.css.agentListAreaSccrollNode}).inject(this.agentListNode);
        this.agentListAreaNode = new Element("div", {"styles": this.css.agentListAreaNode}).inject(this.agentListAreaSccrollNode);

        this.loadAgentListResize();

        this.loadAgentList();
    },

    loadAgentListResize: function(){
//		var size = this.propertyNode.getSize();
//		var position = this.propertyResizeBar.getPosition();
        this.agentListResize = new Drag(this.agentListResizeNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.agentListAreaSccrollNode.getSize();
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
                this.agentListNode.setStyle("width", width);
            }.bind(this)
        });
    },

    loadAgentList: function() {
        this.actions.listAgent(function (json) {
            json.data.each(function(agent){
                this.createListAgentItem(agent);
            }.bind(this));
        }.bind(this), null, false);
    },
    createListAgentItem: function(agent, isNew){
        var _self = this;
        var listAgentItem = new Element("div", {"styles": this.css.listAgentItem}).inject(this.agentListAreaNode, (isNew) ? "top": "bottom");
        var listAgentItemIcon = new Element("div", {"styles": this.css.listAgentItemIcon}).inject(listAgentItem);
        var listAgentItemText = new Element("div", {"styles": this.css.listAgentItemText, "text": (agent.name) ? agent.name+" ("+agent.alias+")" : this.lp.newAgent}).inject(listAgentItem);

        listAgentItem.store("agent", agent);
        listAgentItem.addEvents({
            "dblclick": function(e){_self.loadAgentByData(this, e);},
            "mouseover": function(){if (_self.currentListAgentItem!=this) this.setStyles(_self.css.listAgentItem_over);},
            "mouseout": function(){if (_self.currentListAgentItem!=this) this.setStyles(_self.css.listAgentItem);}
        });

        this.listAgentItemMove(listAgentItem);

    },
    createAgentListCopy: function(node){
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
    listAgentItemMove: function(node){
        var iconNode = node.getFirst();
        iconNode.addEvent("mousedown", function(e){
            var agent = node.retrieve("agent");
            if (agent.id!=this.agentTab.showPage.agent.data.id){
                var copyNode = this.createAgentListCopy(node);

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
                            //this.addIncludeAgent(agent);
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
    addIncludeAgent: function(agent){
        var currentAgent = this.agentTab.showPage.agent;
        if (currentAgent.data.dependAgentList.indexOf(agent.name)==-1){
            currentAgent.data.dependAgentList.push(agent.name);
            this.addIncludeToList(agent.name);
        }
    },
    addIncludeToList: function(name){
        this.actions.getAgentByName(name, function(json){
            var agent = json.data;
            var includeAgentItem = new Element("div", {"styles": this.css.includeAgentItem}).inject(this.propertyIncludeListArea);
            var includeAgentItemAction = new Element("div", {"styles": this.css.includeAgentItemAction}).inject(includeAgentItem);
            var includeAgentItemText = new Element("div", {"styles": this.css.includeAgentItemText}).inject(includeAgentItem);
            includeAgentItemText.set("text", agent.name+" ("+agent.alias+")");
            includeAgentItem.store("agent", agent);

            var _self = this;
            includeAgentItemAction.addEvent("click", function(){
                var node = this.getParent();
                var agent = node.retrieve("agent");
                if (agent){
                    _self.agentTab.showPage.agent.data.dependAgentList.erase(agent.name);
                }
                node.destroy();
            });
        }.bind(this), function(){
            this.agentTab.showPage.agent.data.dependAgentList.erase(name);
        }.bind(this));
    },


    loadAgentByData: function(node, e){
        var agent = node.retrieve("agent");

        var openNew = true;
        for (var i = 0; i<this.agentTab.pages.length; i++){
            if (agent.id==this.agentTab.pages[i].agent.data.id){
                this.agentTab.pages[i].showTabIm();
                openNew = false;
                break;
            }
        }
        if (openNew){
            this.loadAgentData(agent.id, function(data){
                var agent = new MWF.xApplication.service.AgentDesigner.Agent(this, data);
                agent.load();
            }.bind(this), true);
        }
        //var _self = this;
        //var options = {
        //    "onQueryLoad": function(){
        //        this.actions = _self.actions;
        //        this.options.id = agent.id;
        //        this.application = _self.application;
        //    }
        //};
        //this.desktop.openApplication(e, "service.AgentDesigner", options);
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

        this.agentTab.pages.each(function(page){
            var editor = page.agent.editor;
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
        this.agentTab.pages.each(function(page){
            //if (!editorData) editorData = page.invoke.editor.editorData;
            var editor = page.agent.editor;
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
        this.agentTab.pages.each(function(page){
            //if (!editorData) editorData = page.script.editor.editorData;
            var editor = page.agent.editor;
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
            this.agentTab.pages.each(function(page){
                page.agent.setAreaNodeSize();
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
            this.agentTab.pages.each(function(page){
                page.agent.setAreaNodeSize();
            });
        }

    },
    loadEditContent: function(callback){
        this.designNode = new Element("div", {
            "styles": this.css.designNode
        }).inject(this.editContentNode);

        MWF.require("MWF.widget.Tab", function(){
            this.agentTab = new MWF.widget.Tab(this.designNode, {"style": "script"});
            this.agentTab.load();
        }.bind(this), false);


        //MWF.require("MWF.widget.ScrollBar", function(){
            //    new MWF.widget.ScrollBar(this.designNode, {"distance": 100});
            //}.bind(this));
	},
	
	//loadProperty------------------------
	loadProperty: function(){
		this.propertyTitleNode = new Element("div", {
			"styles": this.css.propertyTitleNode,
			"text": MWF.SRVAD.LP.property
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

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.cron+":"}).inject(this.propertyContentArea);
        this.propertyCronNode = new Element("input", {"styles": this.css.propertyInputNode, "value": ""}).inject(this.propertyContentArea);
        this.loadCronTooltip();

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.lastStartTime+":"}).inject(this.propertyContentArea);
        this.propertyLastStartTimeNode = new Element("div", {"styles": this.css.propertyTextNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.lastEndTime+":"}).inject(this.propertyContentArea);
        this.propertyLastEndTimeNode = new Element("div", {"styles": this.css.propertyTextNode, "value": ""}).inject(this.propertyContentArea);

        //node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.appointmentTime+":"}).inject(this.propertyContentArea);
        //this.propertyAppointmentTimeNode = new Element("div", {"styles": this.css.propertyTextNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.description+":"}).inject(this.propertyContentArea);
        this.propertyDescriptionNode = new Element("textarea", {"styles": this.css.propertyInputAreaNode, "value": ""}).inject(this.propertyContentArea);

        node = new Element("div", {"styles": this.css.propertyItemTitleNode, "text": this.lp.isEnable+":"}).inject(this.propertyContentArea);
        var div = new Element("div", {"styles": this.css.propertyTextNode, "text": ""}).inject(this.propertyContentArea);
        this.propertyEnableNode = new Element("div", { styles : {float:"left", color : "red"}, "text": ""}).inject(div);
        this.propertyEnableButton = new Element("input", { type : "button", styles : this.css.propertyButton, "value":  this.lp.enable }).inject(div);
        this.propertyEnableButton.addEvent("click", function(){
            var id = this.propertyEnableButton.retrieve("id");
            if( id )this.actions.enableAgent( id , function(){
                this.refresh();
            }.bind(this));
        }.bind(this));
        this.propertyDisableButton = new Element("input", { type : "button", styles : this.css.propertyButton, "value": this.lp.disable }).inject(div);
        this.propertyDisableButton.addEvent("click", function(){
            var id = this.propertyDisableButton.retrieve("id");
            if( id )this.actions.disableAgent( id , function(){
                this.refresh();
            }.bind(this));
        }.bind(this));
    },
    loadCronTooltip : function(){
        MWF.xDesktop.requireApp("Template", "widget.CronPicker", null, false);
        this.cronPicker = new MWF.xApplication.Template.widget.CronPicker( this.content, this.propertyCronNode, this, {}, {
            style : "design",
            position : { //node 固定的位置
                x : "right",
                y : "auto"
            },
            onSelect : function( value ){
                this.propertyCronNode.set("value", value );
                this.cronValue = value;
            }.bind(this),
            onQueryLoad : function(){
                if( this.cronValue ){
                    if( !this.cronPicker.node  ){
                        this.cronPicker.options.value = this.cronValue;
                    }else{
                        this.cronPicker.setCronValue( this.cronValue );
                    }
                }
            }.bind(this)
        } );
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

            titleSize = this.agentListTitleNode.getSize();
            titleMarginTop = this.agentListTitleNode.getStyle("margin-top").toFloat();
            titleMarginBottom = this.agentListTitleNode.getStyle("margin-bottom").toFloat();
            titlePaddingTop = this.agentListTitleNode.getStyle("padding-top").toFloat();
            titlePaddingBottom = this.agentListTitleNode.getStyle("padding-bottom").toFloat();
            nodeMarginTop = this.agentListAreaSccrollNode.getStyle("margin-top").toFloat();
            nodeMarginBottom = this.agentListAreaSccrollNode.getStyle("margin-bottom").toFloat();

            y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom+nodeMarginTop+nodeMarginBottom;
            y = nodeSize.y-y;
            this.agentListAreaSccrollNode.setStyle("height", ""+y+"px");
            this.agentListResizeNode.setStyle("height", ""+y+"px");
        }
	},
	
	//loadForm------------------------------------------
    loadAgent: function(){
        //this.agentTab.addTab(node, title);
		this.getAgentData(this.options.id, function(data){
			this.agent = new MWF.xApplication.service.AgentDesigner.Agent(this, data);
			this.agent.load();

            if (this.status){
                if (this.status.openAgents){
                    this.status.openAgents.each(function(id){
                        this.loadAgentData(id, function(data){
                            var showTab = true;
                            if (this.status.currentId){
                                if (this.status.currentId!=data.id) showTab = false;
                            }
                            var agent = new MWF.xApplication.service.AgentDesigner.Agent(this, data, {"showTab": showTab});
                            agent.load();
                        }.bind(this), true);
                    }.bind(this));
                }
            };
            //if (!this.agentHelpMenu){
            //    MWF.require("MWF.widget.ScriptHelp", function(){
            //        this.agentHelpMenu = new MWF.widget.ScriptHelp($("MWFScriptAutoCode"), this.agent.editor);
            //        this.agentHelpMenu.getEditor = function(){
            //            if (this.agentTab.showPage) return this.agentTab.showPage.agent.editor.editor;
            //            return null;
            //        }.bind(this)
            //    }.bind(this));
            //}
		}.bind(this));
	},

    getAgentData: function(id, callback){
		if (!id){
			this.loadNewAgentData(callback);
		}else{
			this.loadAgentData(id, callback);
		}
	},

    loadNewAgentData: function(callback){
        MWF.Actions.get("x_cms_assemble_control").getUUID(function(id){
            var data = {
                "name": "",
                "id": id,
                "alias": "",
                "description": "",
                //"language": "javascript",
                //"dependAgentList": [],
                "isNewAgent": true,
                "text": "",
                "enable" : true,
                "cron" : "",
                "lastStartTime" : "",
                "lastEndTime" : "",
                "appointmentTime" : ""
            };
            this.createListAgentItem(data, true);
            if (callback) callback(data);
        }.bind(this))
	},
    loadAgentData: function(id, callback, notSetTile){
		this.actions.getAgent(id, function(json){
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

    saveAgent: function(){
        if (this.agentTab.showPage){
            var agent = this.agentTab.showPage.agent;
            agent.save(function(){
                if (agent==this.agent){
                    var name = agent.data.name;
                    this.setTitle(MWF.SRVAD.LP.title + "-"+name);
                    this.options.desktopReload = true;
                    this.options.id = agent.data.id;
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
        if (this.agentTab){
            var openAgents = [];
            this.agentTab.pages.each(function(page){
                if (page.agent.data.id!=this.options.id) openAgents.push(page.agent.data.id);
            }.bind(this));
            var currentId = this.agentTab.showPage.agent.data.id;
            var status = {
                "id": this.options.id,
                "openAgents": openAgents,
                "currentId": currentId
            };
            return status;
        }
		return {"id": this.options.id};
	}
});
