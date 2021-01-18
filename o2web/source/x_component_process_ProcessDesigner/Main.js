MWF.APPPD = MWF.xApplication.process.ProcessDesigner;
MWF.APPPD.options = {
    "multitask": true,
    "executable": false
};
//MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", null, false);
MWF.require("MWF.widget.MWFRaphael", null, false);
MWF.xApplication.process.ProcessDesigner.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "template": "process.json",
        "name": "process.ProcessDesigner",
        "icon": "icon.png",
        "title": MWF.APPPD.LP.title,
        "appTitle": MWF.APPPD.LP.title,
        "id": "",
        "tooltip": {
            "unCategory": MWF.APPPD.LP.unCategory
        },
        "actions": null,
        "category": null,
        "processData": null
    },
    onQueryLoad: function(){
        this.shortcut = true;
        if (this.status){
            this.options.id = this.status.id;
        }
        if (!this.options.id){
            this.options.desktopReload = false;
            this.options.title = this.options.title + "-"+MWF.APPPD.LP.newProcess;
        }
        this.actions = MWF.Actions.get("x_processplatform_assemble_designer");
        //this.actions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();

        this.lp = MWF.xApplication.process.ProcessDesigner.LP;
//		this.processData = this.options.processData;		
    },
    loadApplication: function(callback){
        this.gadgets = [];
        this.gadgetMode="all";
        this.gadgetDecrease = 0;

        this.createNode();
        if (!this.options.isRefresh){
            this.maxSize(function(){
                this.openProcess();
            }.bind(this));
        }else{
            this.openProcess();
        }
        this.addKeyboardEvents();
        if (callback) callback();
    },
    addKeyboardEvents: function(){
        this.addEvent("copy", function(){
            this.copyModule();
        }.bind(this));
        this.addEvent("paste", function(){
            this.pasteModule();
        }.bind(this));
        this.addEvent("keySave", function(e){
            this.keySave(e);
        }.bind(this));
        this.addEvent("keyDelete", function(e){
            this.keyDelete(e);
        }.bind(this));
        //this.addEvent("cut", function(){
        //    this.cutModule();
        //}.bind(this));
    },
    keySave: function(e){
        if (this.shortcut) {
            if (this.process) this.saveProcess();
            e.preventDefault();
        }
    },
    keyDelete: function(e){
        if (this.shortcut) {
            if (this.process) {
                if (this.process.selectedActivitys.length) {

                } else if (this.process.currentSelected) {
                    var p = this.content.getPosition();
                    if (this.process.currentSelected.type) {
                        var a = this.process.currentSelected;
                        var e = {"event": {"x": a.center.x + p.x, "y": a.center.y + p.y}};
                        this.process.deleteActivity(e, this.process.currentSelected);
                    } else {
                        var r = this.process.currentSelected;
                        var e = {"event": {"x": r.beginPoint.x + p.x, "y": r.beginPoint.y + p.y}};
                        this.process.deleteRoute(e, this.process.currentSelected);
                    }
                }
            }
        }
    },
    copyModule: function(){
        if (this.shortcut) {
            if (this.process) {
                //       if (this.process.isFocus){
                //if (!this.paperInNode.contains(document.activeElement)) return false;
                if (this.process.selectedActivitys.length) {
                    var activitys = [];
                    var routes = [];
                    this.process.selectedActivitys.each(function (activity) {
                        //if (activity.data.type.toLowerCase()!=="begin"){
                            activitys.push(Object.clone(activity.data));

                            activity.routes.each(function (route) {
                                if (route.toActivity) {
                                    //if (this.process.selectedActivitys.indexOf(route.toActivity) != -1){
                                    routes.push(Object.clone(route.data));
                                    //}else{
                                    //    activity.routes = null;
                                    //}
                                } else {
                                    routes.push(Object.clone(route.data));
                                }
                            }.bind(this));
                        //}

                        //activity.routes = activity.routes.clean();
                    }.bind(this));
                    MWF.clipboard.data = {
                        "type": "process",
                        "data": {
                            "activitys": activitys,
                            "routes": routes
                        }
                    };
                } else if (this.process.currentSelected) {
                    if (this.process.currentSelected.type) {
                        //if (this.process.currentSelected.data.type.toLowerCase()!=="begin"){
                            var data = Object.clone(this.process.currentSelected.data);
                            MWF.clipboard.data = {
                                "type": "process",
                                "data": {
                                    "activitys": [data],
                                    "routes": []
                                }
                            };
                        //}
                    } else {
                        MWF.clipboard.data = null;
                    }
                }
                //           }
            }
        }
    },
    pasteModuleError: function(bakData){
        this.notice(this.lp.notice.processCopyError, "error");
        this.process.reload(bakData);
        MWF.clipboard.data = null;
    },
    pasteModule: function(){
        if (this.process){
            //        if (this.process.isFocus){
            //if (!this.paperInNode.contains(document.activeElement)) return false;

            if (MWF.clipboard.data){
                if (MWF.clipboard.data.type=="process"){
                    var bakData = Object.clone(this.process.process);

                    this.process.unSelectedAll();
                    var activitys = MWF.clipboard.data.data.activitys;
                    var routes = MWF.clipboard.data.data.routes;
                    var checkUUIDs;
                    this.actions.getId(activitys.length+routes.length, function(ids) {
                        try {
                            checkUUIDs = ids.data;
                            var idReplace = {};
                            var processId = this.process.process.id;

                            activitys.each(function(d){
                                var id = checkUUIDs.pop().id;
                                idReplace[d.id] = id;
                                d.id = id;
                                d.process = processId;
                            });

                            routes.each(function(d){
                                var id = checkUUIDs.pop().id;
                                idReplace[d.id] = id;
                                d.id = id;
                                d.process = processId;
                                d.activity = idReplace[d.activity];
                            });

                            activitys.each(function(d){
                                if (d.route){
                                    d.route = idReplace[d.route];
                                }
                                if (d.routeList){
                                    d.routeList.each(function(r, i){
                                        d.routeList[i] = idReplace[r]
                                    });
                                }
                            });

                            var loadRoutes = function(){
                                try{
                                    routes.each(function(item){
                                        this.process.process.routeList.push(Object.clone(item));
                                        this.process.routes[item.id] = new MWF.APPPD.Route(item, this.process);
                                        this.process.routeDatas[item.id] = item;
                                        //	this.routes[item.id].load();
                                    }.bind(this));

                                    activitys.each(function(d){
                                        //this.process[d.type+"s"][d.id].loadRoutes();
                                        (this.process[d.type] || this.process[d.type+"s"][d.id]).loadRoutes();
                                        if (activitys.length>1){
                                            (this.process[d.type] || this.process[d.type+"s"][d.id]).selectedMulti();
                                        }else{
                                            this.process[d.type+"s"][d.id].selected();
                                        }


                                    }.bind(this));

                                    routes.each(function(item){
                                        var route = this.process.routes[item.id];
                                        if (!route.loaded) route.load();
                                    }.bind(this));

                                    MWF.clipboard.data = null;
                                }catch(e){
                                    this.pasteModuleError(bakData);
                                }
                            };

                            var loadedCount = 0;
                            activitys.each(function(activity){
                                //if (activity.type.toLowerCase()!=="begin"){
                                var data = Object.clone(activity);
                                var type = data.type;
                                if (type.toLowerCase()=="begin"){
                                    if (!this.process.begin){
                                        this.process.process.begin = data;
                                        this.process.loadBegin(function(){
                                            loadedCount++;
                                            if (loadedCount==activitys.length) loadRoutes.apply(this);
                                        }.bind(this));
                                    }else{
                                        //loadedCount++;
                                        activitys.erase(activity);
                                        if (loadedCount==activitys.length) loadRoutes.apply(this);
                                    }
                                }else{
                                    if (!this.process.process[type+"List"]) this.process.process[type+"List"] = [];
                                    this.process.process[type+"List"].push(data);
                                    var c = type.capitalize();
                                    // if (type==="begin") {
                                    //     if (!this.process.begin){
                                    //         this.process.loadActivity(c, data, this.process.begin, function(){
                                    //             loadedCount++;
                                    //             if (loadedCount==activitys.length) loadRoutes.apply(this);
                                    //         }.bind(this));
                                    //     }
                                    // }else{
                                    this.process.loadActivity(c, data, this.process[type+"s"], function(){
                                        loadedCount++;
                                        if (loadedCount==activitys.length) loadRoutes.apply(this);
                                    }.bind(this));
                                    // }

                                }
                                // }

                            }.bind(this));
                        }catch(e){
                            this.pasteModuleError(bakData)
                        }

                    }.bind(this));

                }
            }
            //         }
        }
    },


    createNode: function(){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.content);
    },
    openProcess: function(){
        this.loadNodes();
        this.loadGadgets();
        this.loadToolbar(function(){
            this.resizePaper();
            this.addEvent("resize", function(){this.resizePaper();}.bind(this));

            this.getProcessData(function(){
                this.loadPaper();
            }.bind(this));
        }.bind(this));
        this.resizeNode();
        this.addEvent("resize", this.resizeNode.bind(this));
    },
    resizeNode: function(){
        var nodeSize = this.node.getSize();

        var titleSize = this.gadgetTitleNode.getSize();
        var titleMarginTop = this.gadgetTitleNode.getStyle("margin-top").toFloat();
        var titleMarginBottom = this.gadgetTitleNode.getStyle("margin-bottom").toFloat();
        var titlePaddingTop = this.gadgetTitleNode.getStyle("padding-top").toFloat();
        var titlePaddingBottom = this.gadgetTitleNode.getStyle("padding-bottom").toFloat();

        y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom;
        y = nodeSize.y-y;
        this.gadgetContentNode.setStyle("height", ""+y+"px");
    },
    getProcessData: function(callback){
        if (!this.options.id){
            this.loadNewProcessData(callback);
        }else{
            this.loadProcessData(callback);
        }
    },
    loadNewProcessData: function(callback){

        //MWF.getJSON(this.path+"process.json", {
        var url = "../x_component_process_ProcessDesigner/$Process/template/"+this.options.template;
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                obj.id="";
                obj.isNewProcess = true;
                this.processData = obj;
                if (callback) callback();
            }.bind(this),
            "onerror": function(text){
                this.notice(text, "error");
            }.bind(this),
            "onRequestFailure": function(xhr){
                this.notice(xhr.responseText, "error");
            }.bind(this)
        });
    },
    loadProcessData: function(callback){
        this.actions.getProcess(this.options.id, function(process){
            if (process){
                this.processData = process.data;
                this.processData.isNewProcess = false;

                this.setTitle(this.options.appTitle + "-"+this.processData.name);

                if (!this.application){
                    this.actions.getApplication(process.data.application, function(json){
                        this.application = {"name": json.data.name, "id": json.data.id};
                        if (callback) callback();
                    }.bind(this));
                }else{
                    if (callback) callback();
                }

            }
        }.bind(this));
    },

    loadGadgets: function(){
        this.gadgetTitleNode = new Element("div", {
            "styles": this.css.gadgetTitleNode,
            "text": MWF.APPPD.LP.tools
        }).inject(this.gadgetAreaNode);

        this.gadgetTitleActionNode = new Element("div", {
            "styles": this.css.gadgetTitleActionNode,
            "events": {"click": function(e){this.switchGadgetAreaMode();}.bind(this)}
        }).inject(this.gadgetAreaNode);

        this.gadgetContentNode = new Element("div", {
            "styles": this.css.gadgetContentNode,
            "events": {"selectstart": function(e){e.preventDefault(); e.stopPropagation();}}
        }).inject(this.gadgetAreaNode);

        MWF.getJSON(this.path+"gadget.json", function(json){
            Object.each(json, function(value, key){
                this.createGadgetNode(value, key);
            }.bind(this));
        }.bind(this));

        this.setScrollBar(this.gadgetContentNode, null, {
            "V": {"x": 0, "y": 0},
            "H": {"x": 0, "y": 0}
        });
    },
    switchGadgetAreaMode: function(){
        if (this.gadgetMode=="all"){
            var size = this.gadgetAreaNode.getSize();
            this.gadgetDecrease = (size.x.toFloat())-60;

            this.gadgets.each(function(node){
                node.getLast().setStyle("display", "none");
            });
            this.gadgetTitleNode.set("text", "");

            this.gadgetAreaNode.setStyle("width", "60px");

            var rightMargin = this.rightContentNode.getStyle("margin-left").toFloat();
            rightMargin = rightMargin - this.gadgetDecrease;

            this.rightContentNode.setStyle("margin-left", ""+rightMargin+"px");

            this.gadgetTitleActionNode.setStyles(this.css.gadgetTitleActionNodeRight);

            this.gadgetMode="simple";
        }else{
            sizeX = 60 + this.gadgetDecrease;
            var rightMargin = this.rightContentNode.getStyle("margin-left").toFloat();
            rightMargin = rightMargin + this.gadgetDecrease;

            this.gadgetAreaNode.setStyle("width", ""+sizeX+"px");
            this.rightContentNode.setStyle("margin-left", ""+rightMargin+"px");

            this.gadgets.each(function(node){
                node.getLast().setStyle("display", "block");
            });

            this.gadgetTitleNode.set("text", MWF.APPPD.LP.tools);

            this.gadgetTitleActionNode.setStyles(this.css.gadgetTitleActionNode);
            this.gadgetMode="all";
        }
    },
    createGadgetNode: function(data, key){
        var _self = this;
        var gadgetNode = new Element("div", {
            "styles": this.css.gadgetToolNode,
            "title": data.text,
            "events": {
                "mouseover": function(e){
                    try {
                        this.setStyles(_self.css.gadgetToolNodeOver);
                    }catch(e){
                        this.setStyles(_self.css.gadgetToolNodeOverCSS2);
                    };
                },
                "mouseout": function(e){
                    try {
                        this.setStyles(_self.css.gadgetToolNode);
                    }catch(e){};
                },
                "mousedown": function(e){
                    try {
                        this.setStyles(_self.css.gadgetToolNodeDown);
                    }catch(e){
                        this.setStyles(_self.css.gadgetToolNodeDownCSS2);
                    };
                },
                "mouseup": function(e){
                    try {
                        this.setStyles(_self.css.gadgetToolNodeUp);
                    }catch(e){
                        this.setStyles(_self.css.gadgetToolNodeUpCSS2);
                    };
                }
            }
        }).inject(this.gadgetContentNode);

        gadgetNode.store("gadgetClass", data.className);
        gadgetNode.store("gadgetType", key);
        gadgetNode.store("gadgetIcon", data.icon);

        var iconNode = new Element("div", {
            "styles": this.css.gadgetToolIconNode
        }).inject(gadgetNode);
        iconNode.setStyle("background-image", "url("+this.path+this.options.style+"/gadget/"+data.icon+")");

        var textNode = new Element("div", {
            "styles": this.css.gadgetToolTextNode,
            "text": data.text
        });
        textNode.inject(gadgetNode);

//				var designer = this;
        gadgetNode.addEvent("mousedown", function(e){
            var className = this.retrieve("gadgetClass");
            var gadgetType = this.retrieve("gadgetType");
            var gadgetIcon = this.retrieve("gadgetIcon");
            _self.gadgetCreateActivity(this, gadgetType, gadgetIcon, className, e);
        });

        this.gadgets.push(gadgetNode);
    },
    gadgetCreateActivity: function(node, gadgetType, gadgetIcon, className, e){
        var activity = null;
        //    if (activity){
        var moveNode = new Element("div", {
            "styles": {
                "width": "30px",
                "height": "30px",
                "opacity": "1",
                "background": "url("+this.path+this.options.style+"/gadget/"+gadgetIcon+") top left no-repeat",
                "postion": "absolute"
            }
        }).inject(this.paperNode);
        var moveNodeSize = moveNode.getSize();
        //moveNode.position({"relativeTo": node, "position": "upperLeft", "edge": "upperLeft"});
        var x = e.page.x;
        var y = e.page.y;
        moveNode.positionTo(x, y);

        var droppables = [this.paperNode];

        var nodeDrag = new Drag.Move(moveNode, {
            "droppables": droppables,
            "onEnter": function(dragging, inObj){
                moveNode.setStyles({
                    "opacity":"1",
                    "background": "url("+this.path+this.options.style+"/gadget/"+gadgetIcon+") top left no-repeat"
                });
                //moveNode.setStyle("opacity","0");
                //activity = this.process.createActivity(gadgetType, className);
                //if (activity){
                //    activity.selected();
                //    activity.activityMoveStart();
                //
                //    moveNode.dragMove = true;
                //
                //    var p = moveNode.getPosition(this.paperNode);
                //    var dx = p.x - activity.point.x;
                //    var dy = p.y - activity.point.y;
                //    activity.activityMove(dx, dy, 0, 0, e);
                //
                //    moveNode.dx = activity.point.x;
                //    moveNode.dy = activity.point.y;
                //    activity.activityMoveStart();
                //}else{
                //    moveNode.setStyles({
                //        "opacity":"1",
                //        "background": "url("+this.path+this.options.style+"/gadget/stop.png) top left no-repeat",
                //    });
                //}
            }.bind(this),
            "onLeave": function(dragging, inObj){
                moveNode.setStyles({
                    "opacity":"1",
                    "background": "url("+this.path+this.options.style+"/gadget/stop.png) top left no-repeat"
                });
                if (activity) activity.destroy();
                moveNode.dragMove = false;
            }.bind(this),
            "onDrag": function(e){
                if (moveNode.dragMove){
                    var p = moveNode.getPosition(this.paperNode);
                    var dx = p.x-moveNode.dx;
                    var dy = p.y-moveNode.dy;
                    activity.activityMove(dx, dy, 0, 0, e);
                }
            }.bind(this),
            "onDrop": function(dragging, inObj){
                //if (moveNode.dragMove){
                //    activity.activityMoveEnd();
                //}
            }.bind(this),
            "onComplete": function(e){
                var p = moveNode.getPosition(this.paperNode);
                //var dx = p.x - activity.point.x;
                //var dy = p.y - activity.point.y;

                activity = this.process.createActivity(gadgetType, className, {"x": p.x, "y": p.y});
                if (activity) activity.selected();
                //      activity.activityMoveStart();

                //moveNode.dragMove = true;


                //          activity.activityMove(dx, dy, 0, 0, e);

                //moveNode.dx = activity.point.x;
                //moveNode.dy = activity.point.y;
                //activity.activityMoveStart();
                //       activity.activityMoveEnd();

                moveNode.destroy();
            }.bind(this),
            "onCancel": function(dragging){
                if (activity) activity.destroy();
                moveNode.destroy();
            }.bind(this)
        });
        nodeDrag.start(e);
        //    }
    },
    loadNodes: function(){
        this.gadgetAreaNode = new Element("div", {
            "styles": this.css.gadgetAreaNode
        }).inject(this.node);

        this.rightContentNode = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);


        this.toolbarNode = new Element("div", {
            "styles": this.css.toolbarNode
        }).inject(this.rightContentNode);

        this.paperAreaNode = new Element("div", {
            "styles": this.css.paperAreaNode
        }).inject(this.rightContentNode);
        this.paperNode = new Element("div", {
            "styles": this.css.paperNode
        }).inject(this.paperAreaNode);
    },
    loadToolbar: function(callback){
        this.getProcessToolbarHTML(function(toolbarNode){
            var spans = toolbarNode.getElements("span");
            spans.each(function(item, idx){
                var img = item.get("MWFButtonImage");
                if (img){
                    item.set("MWFButtonImage", this.path+""+this.options.style+"/toolbarIcon/"+img);
                }
            }.bind(this));

            $(toolbarNode).inject(this.toolbarNode);
            MWF.require("MWF.widget.Toolbar", function(){
                this.processToolbar = new MWF.widget.Toolbar(toolbarNode, {"style": "Process"}, this);
                this.processToolbar.load();
                if (callback) callback();
            }.bind(this));

            var select = toolbarNode.getElement("select");
            if (select){
                select.addEvent("change", function(){
                    this.process.setStyle(select.options[select.selectedIndex].value);
                }.bind(this));
            }

            this.processEditionNode = toolbarNode.getElement(".processEdition");
            this.processEditionInforNode = toolbarNode.getElement(".processEditionInfor");
        }.bind(this));
    },
    getProcessToolbarHTML: function(callback){
        var toolbarUrl = this.path+"processToolbars.html";
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
    resizePaper: function(){
        var nodeSize = this.node.getSize();
        var toolbarSize = this.processToolbar.node.getSize();
        var y = nodeSize.y - toolbarSize.y;

        var marginTop = this.paperNode.getStyle("margin-top").toFloat();
        var marginBottom = this.paperNode.getStyle("margin-bottom").toFloat();
        y = y - marginTop - marginBottom;

        //this.paperAreaNode.setStyle("height", ""+y+"px");
        this.paperNode.setStyle("height", ""+y+"px");
        if (this.paper) this.paper.setSize("100%", "100%");

        if (this.process){
            if (this.process.panel){
                this.process.panel.modulePanel.container.position({
                    relativeTo: this.paperNode,
                    position: 'upperRight',
                    edge: 'upperRight'
                });

                var size = this.process.panel.modulePanel.container.getSize();
                var pSize = this.paperNode.getSize();
                if (pSize.y<size.y){
                    var height = (this.paperNode.getSize().y.toFloat())-6;
                    this.process.panel.modulePanel.container.setStyle("height", ""+height+"px");
                    this.process.panel.modulePanel.content.setStyle("height", this.process.panel.modulePanel.getContentHeight());
                    this.process.panel.setPanelSize();
                }
                if (pSize.x<size.x){
                    this.process.panel.modulePanel.container.setStyle("width", ""+pSize.x+"px");
                }
                if (this.process.panel.propertyPanel){
                    this.process.panel.propertyPanel.container.position({
                        relativeTo: this.paperNode,
                        position: 'bottomRight',
                        edge: 'bottomRight'
                    });

                    var size = this.process.panel.propertyPanel.container.getSize();
                    var pSize = this.paperNode.getSize();
                    if (pSize.y<size.y){
                        var height = (this.paperNode.getSize().y.toFloat())-6;
                        this.process.panel.propertyPanel.container.setStyle("height", ""+height+"px");
                        this.process.panel.propertyPanel.content.setStyle("height", this.process.panel.propertyPanel.getContentHeight());
                        this.process.panel.setPropertyPanelSize();
                    }
                    if (pSize.x<size.x){
                        this.process.panel.propertyPanel.container.setStyle("width", ""+pSize.x+"px");
                    }
                }
            }
        }
    },
    loadPaper: function(){
        MWFRaphael.load(function(){
            this.paperInNode = new Element("div", {"styles": this.css.paperInNode}).inject(this.paperNode);
            this.paper = Raphael(this.paperInNode, "100%", "99%");
            this.paper.container = this.paperNode;

            MWF.xDesktop.requireApp("process.ProcessDesigner", "Process", function(){
                this.process = new MWF.APPPD.Process(this.paper, this.processData, this, {
                    "style":"flat",
                    "onPostLoad": function(){
                        this.fireEvent("postProcessLoad");
                    }.bind(this)
                });
                this.process.load();
            }.bind(this));
        }.bind(this));
    },
    setToolBardisabled: function(type){
        switch (type){
            case "default":

                break;
            case "createRoute":

                break;
            case "decision":

                break;
            default:

        }
    },
    recordStatus: function(){
        debugger;
        return {"id": this.options.id};
    },
    saveProcess: function(){
        if (!this.process.process.name){
            this.notice(this.lp.notice.no_name, "error");
            return false;
        }

        this.process.save(function(){
            var name = this.process.process.name;
            this.setTitle(this.options.appTitle + "-"+name);

            this.options.desktopReload = true;
            this.options.id = this.process.process.id;
        }.bind(this));

    },
    saveNewProcess: function(b, e){
        //this.process.saveNew(e);
    },
    createManualActivity: function(){
        this.process.createManualActivity();
    },
    createConditionActivity: function(){
        this.process.createConditionActivity();
    },
    createAutoActivity: function(){
        this.process.createAutoActivity();
    },
    createSplitActivity: function(){
        this.process.createSplitActivity();
    },
    createMergeActivity: function(){
        this.process.createMergeActivity();
    },
    createEmbedActivity: function(){
        this.process.createEmbedActivity();
    },
    createInvokesActivity: function(){
        this.process.createInvokesActivity();
    },
    createBeginActivity: function(){
        this.process.createBeginActivity();
    },
    createEndActivity: function(){
        this.process.createEndActivity();
    },

    createRoute: function(){
        this.process.createRoute();
    },

    processExplode: function(){
        this.process.explode();
    },
    saveNewEdition: function(el, e){
        this.process.saveNewEdition(e);
    },
    listEdition: function(el, e){
        this.process.listEdition(e);
    },

    onPostClose: function() {
        if (this.process) {
            this.process.activitys.each(function (activity) {
                activity.routes.each(function (route) {
                    MWF.release(route);
                });
                MWF.release(activity);
            });
            MWF.release(this.process);
        }
    }
});
