MWF.APPDSMD = MWF.xApplication.query.StatementDesigner;
MWF.APPDSMD.options = {
	"multitask": true,
	"executable": false
};
MWF.xDesktop.requireApp("query.StatementDesigner", "Statement", null, false);

MWF.xApplication.query.StatementDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "query.StatementDesigner",
		"icon": "icon.png",
		"title": MWF.APPDSMD.LP.title,
		"appTitle": MWF.APPDSMD.LP.title,
		"id": "",
        "tooltip": {
            "unCategory": MWF.APPDSMD.LP.unCategory
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
            this.options.title = this.options.title + "-"+MWF.APPDSMD.LP.newStatement;
        }
        if (!this.actions) this.actions = MWF.Actions.get("x_query_assemble_designer");

        this.lp = MWF.xApplication.query.StatementDesigner.LP;

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
                this.openStatement(function(){
                    if (callback) callback();
                });
            }.bind(this));
        }else{
            this.openStatement(function(){
                if (callback) callback();
            });
        }

        if (!this.options.readMode) this.addKeyboardEvents();
    },
    createNode: function(){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.content);
    },
    addKeyboardEvents: function(){
        this.addEvent("keySave", function(e){
            this.keySave(e);
        }.bind(this));
    },
    keySave: function(e){
        if (this.shortcut) {
            this.view.save();
            e.preventDefault();
        }
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
    openStatement: function(callback){
        this.getApplication(function(){
            this.loadNodes();
            this.loadStatementListNodes();
            //	this.loadToolbar();
            this.loadContentNode();
            this.loadProperty();
            //	this.loadTools();
            this.resizeNode();
            this.addEvent("resize", this.resizeNode.bind(this));
            this.loadStatement(function(){
                if (callback) callback();
            });

            // this.setScrollBar(this.designerStatementArea, null, {
            //     "V": {"x": 0, "y": 0},
            //     "H": {"x": 0, "y": 0}
            // });
        }.bind(this));
    },
    loadNodes: function(){
        this.statementListNode = new Element("div", {
            "styles": this.css.statementListNode
        }).inject(this.node);
        this.designerNode = new Element("div", {
            "styles": this.css.designerNode
        }).inject(this.node);
        this.contentNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.node);
    },
    loadStatementListNodes: function(){
        this.statementListTitleNode = new Element("div", {
            "styles": this.css.statementListTitleNode,
            "text": this.lp.statement
        }).inject(this.statementListNode);

        this.statementListResizeNode = new Element("div", {"styles": this.css.statementListResizeNode}).inject(this.statementListNode);
        this.statementListAreaSccrollNode = new Element("div", {"styles": this.css.statementListAreaSccrollNode}).inject(this.statementListNode);
        this.statementListAreaNode = new Element("div", {"styles": this.css.statementListAreaNode}).inject(this.statementListAreaSccrollNode);

        this.loadStatementListResize();

        this.loadStatementList();
    },
    loadStatementListResize: function(){
        this.statementListResize = new Drag(this.statementListResizeNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.statementListAreaSccrollNode.getSize();
                el.store("initialWidth", size.x);
            }.bind(this),
            "onDrag": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var bodySize = this.content.getSize();
                var position = el.retrieve("position");
                var initialWidth = el.retrieve("initialWidth").toFloat();
                var dx = x.toFloat() - position.x.toFloat();

                var width = initialWidth+dx;
                if (width> bodySize.x/2) width =  bodySize.x/2;
                if (width<40) width = 40;
                this.contentNode.setStyle("margin-left", width+1);
                this.statementListNode.setStyle("width", width);
            }.bind(this)
        });
        this.statementListResizeNode.addEvents({
            "touchstart": function(e){
                el = e.target;
                var x = (Browser.name=="firefox") ? e.page.clientX : e.page.x;
                var y = (Browser.name=="firefox") ? e.page.clientY : e.page.y;
                el.store("position", {"x": x, "y": y});

                var size = this.statementListAreaSccrollNode.getSize();
                el.store("initialWidth", size.x);
            }.bind(this),
            "touchmove": function(e){
                el = e.target;

                var x = (Browser.name=="firefox") ? e.page.clientX : e.page.x;
                var bodySize = this.content.getSize();
                var position = el.retrieve("position");
                var initialWidth = el.retrieve("initialWidth").toFloat();
                var dx = x.toFloat() - position.x.toFloat();

                var width = initialWidth+dx;
                if (width> bodySize.x/2) width =  bodySize.x/2;
                if (width<40) width = 40;
                this.contentNode.setStyle("margin-left", width+1);
                this.statementListNode.setStyle("width", width);
            }.bind(this)
        });
    },
    loadStatementList: function(){
        this.actions.listStatement(this.application.id, function (json) {
            json.data.each(function(statement){
                this.createListStatementItem(statement);
            }.bind(this));
        }.bind(this), null, false);
    },
    //列示所有查询配置列表
    createListStatementItem: function(statement, isNew){
        var _self = this;
        var listStatementItem = new Element("div", {"styles": this.css.listStatementItem}).inject(this.statementListAreaNode, (isNew) ? "top": "bottom");
        var listStatementItemIcon = new Element("div", {"styles": this.css.listStatementItemIcon}).inject(listStatementItem);
        var listStatementItemText = new Element("div", {"styles": this.css.listStatementItemText, "text": (statement.name) ? statement.name+" ("+statement.alias+")" : this.lp.newStatement}).inject(listStatementItem);

        listStatementItem.store("statement", statement);
        listStatementItem.addEvents({
            "dblclick": function(e){_self.loadStatementByData(this, e);},
            "mouseover": function(){if (_self.currentListStatementItem!=this) this.setStyles(_self.css.listStatementItem_over);},
            "mouseout": function(){if (_self.currentListStatementItem!=this) this.setStyles(_self.css.listStatementItem);}
        });
    },
    //打开查询配置
    loadStatementByData: function(node, e){
        var statement = node.retrieve("statement");
        if (!statement.isNewStatement){
            var _self = this;
            var options = {
                "appId": "query.StatementDesigner"+statement.id,
                "onQueryLoad": function(){
                    this.actions = _self.actions;
                    this.category = _self;
                    this.options.id = statement.id;
                    this.application = _self.application;
                    this.explorer = _self.explorer;
                }
            };
            this.desktop.openApplication(e, "query.StatementDesigner", options);
        }
    },


    //loadContentNode-------------------------------------------
    loadContentNode: function(){
        this.contentToolbarNode = new Element("div", {
            "styles": this.css.contentToolbarNode
        }).inject(this.contentNode);
        if (!this.options.readMode) this.loadContentToolbar();

        this.editContentNode = new Element("div", {
            "styles": this.css.editContentNode
        }).inject(this.contentNode);
        this.loadEditContent();

        // this.loadEditContent(function(){
        //     //	if (this.designDcoument) this.designDcoument.body.setStyles(this.css.designBody);
        //     // if (this.designNode) this.designNode.setStyles(this.css.designNode);
        // }.bind(this));
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
                if (this.statement) if (this.statement.checkToolbars) this.statement.checkToolbars();
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
    loadEditContent: function(callback){
        this.designNode = new Element("div", {
            "styles": this.css.designNode
        }).inject(this.editContentNode);
    },

    //loadProperty--------------------------------------
    loadProperty: function(){
        this.designerTitleNode = new Element("div", {
            "styles": this.css.designerTitleNode,
            "text": this.lp.property
        }).inject(this.designerNode);

        this.designerResizeBar = new Element("div", {
            "styles": this.css.designerResizeBar
        }).inject(this.designerNode);
        this.loadDesignerResize();

        this.designerContentNode = new Element("div", {
            "styles": this.css.designerContentNode
        }).inject(this.designerNode);

        // this.designerStatementArea = new Element("div", {
        //     "styles": this.css.designerStatementArea
        // }).inject(this.designerContentNode);
        //
        // this.designerStatementPercent = 0.6;
        // this.designerContentResizeNode = new Element("div", {
        //     "styles": this.css.designerContentResizeNode
        // }).inject(this.designerContentNode);

        this.designerContentArea = new Element("div", {
            "styles": this.css.designerContentArea
        }).inject(this.designerContentNode);

        //this.loadDesignerStatementResize();
        //this.setPropertyContent();
        this.designerNode.addEvent("keydown", function(e){e.stopPropagation();});
    },
    loadDesignerResize: function(){
        this.designerResize = new Drag(this.designerResizeBar,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.designerNode.getSize();
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

                var nodeSize = this.node.getSize();
                var scale = width/nodeSize.x;
                var scale = scale*100;

                this.contentNode.setStyle("margin-right", scale+"%");
                this.designerNode.setStyle("width", scale+"%");
            }.bind(this)
        });
    },
    // loadDesignerStatementResize: function(){
    //     this.designerContentResize = new Drag(this.designerContentResizeNode, {
    //         "snap": 1,
    //         "onStart": function(el, e){
    //             var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
    //             var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
    //             el.store("position", {"x": x, "y": y});
    //
    //             var size = this.designerStatementArea.getSize();
    //             el.store("initialHeight", size.y);
    //         }.bind(this),
    //         "onDrag": function(el, e){
    //             var size = this.designerContentNode.getSize();
    //
    //             //			var x = e.event.x;
    //             var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
    //             var position = el.retrieve("position");
    //             var dy = y.toFloat()-position.y.toFloat();
    //
    //             var initialHeight = el.retrieve("initialHeight").toFloat();
    //             var height = initialHeight+dy;
    //             if (height<40) height = 40;
    //             if (height> size.y-40) height = size.y-40;
    //
    //             this.designerStatementPercent = height/size.y;
    //
    //             this.setDesignerStatementResize();
    //
    //         }.bind(this)
    //     });
    // },
    setDesignerStatementResize: function(){
        var size = this.designerContentNode.getSize();
        //var resizeNodeSize = this.designerContentResizeNode.getSize();
        //var height = size.y-resizeNodeSize.y;
        var height = size.y

        // var domHeight = this.designerStatementPercent*height;
        // var contentHeight = height-domHeight;

        //this.designerStatementArea.setStyle("height", ""+domHeight+"px");
        this.designerContentArea.setStyle("height", ""+height+"px");
    },

    //resizeNode------------------------------------------------
    resizeNode: function(){
        var nodeSize = this.node.getSize();
        this.contentNode.setStyle("height", ""+nodeSize.y+"px");
        this.designerNode.setStyle("height", ""+nodeSize.y+"px");

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


        titleSize = this.designerTitleNode.getSize();
        titleMarginTop = this.designerTitleNode.getStyle("margin-top").toFloat();
        titleMarginBottom = this.designerTitleNode.getStyle("margin-bottom").toFloat();
        titlePaddingTop = this.designerTitleNode.getStyle("padding-top").toFloat();
        titlePaddingBottom = this.designerTitleNode.getStyle("padding-bottom").toFloat();

        y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom;
        y = nodeSize.y-y;
        this.designerContentNode.setStyle("height", ""+y+"px");
        this.designerResizeBar.setStyle("height", ""+y+"px");

        this.setDesignerStatementResize();

        titleSize = this.statementListTitleNode.getSize();
        titleMarginTop = this.statementListTitleNode.getStyle("margin-top").toFloat();
        titleMarginBottom = this.statementListTitleNode.getStyle("margin-bottom").toFloat();
        titlePaddingTop = this.statementListTitleNode.getStyle("padding-top").toFloat();
        titlePaddingBottom = this.statementListTitleNode.getStyle("padding-bottom").toFloat();
        nodeMarginTop = this.statementListAreaSccrollNode.getStyle("margin-top").toFloat();

        nodeMarginBottom = this.statementListAreaSccrollNode.getStyle("margin-bottom").toFloat();

        y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom+nodeMarginTop+nodeMarginBottom;
        y = nodeSize.y-y;
        this.statementListAreaSccrollNode.setStyle("height", ""+y+"px");
        this.statementListResizeNode.setStyle("height", ""+y+"px");
    },

	
	//loadStatement------------------------------------------
    loadStatement: function(){
	    debugger;
		this.getStatementData(this.options.id, function(vdata){
            this.setTitle(this.options.appTitle + "-"+vdata.name);
            this.taskitem.setText(this.options.appTitle + "-"+vdata.name);
            this.options.appTitle = this.options.appTitle + "-"+vdata.name;
            this.statement = new MWF.xApplication.query.StatementDesigner.Statement(this, vdata);
			this.statement.load();
		}.bind(this));
	},
    getStatementData: function(id, callback){
        if (!this.options.id){
            this.loadNewStatementData(callback);
        }else{
            this.loadStatementData(id, callback);
        }
    },
    loadNewStatementData: function(callback){
        var url = "/x_component_query_StatementDesigner/$Statement/statement.json";
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                this.actions.getUUID(function(id){
                    obj.id=id;
                    obj.isNewStatement = true;
                    obj.application = this.application.id;
                    this.createListStatementItem(obj, true);
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
    loadStatementData: function(id, callback){
		this.actions.getStatement(id, function(json){
			if (json){
				var data = json.data;
                if (!this.application){
                    this.actions.getApplication(data.query, function(json){
                        this.application = {"name": json.data.name, "id": json.data.id};
                        if (callback) callback(data);
                    }.bind(this));
                }else{
                    if (callback) callback(data);
                }
			}
		}.bind(this));
	},





    saveStatement: function(){
        this.statement.save(function(){
            var name = this.statement.data.name;
            this.setTitle(MWF.APPDSMD.LP.title + "-"+name);
            this.options.desktopReload = true;
            this.options.id = this.statement.data.id;
        }.bind(this));
    },

    statementHelp: function(){
        var content = new Element("div", {"styles": {"margin": "20px"}});
        content.set("html", this.lp.tableHelp);
        o2.DL.open({
            "title": "table help",
            "content": content,
            "width": 500,
            "height": 300,
            "buttonList": [
                {
                    "text": "ok",
                    "action": function(){this.close();}
                }
            ]
        });
    },
    recordStatus: function(){
        //if (this.tab){
        var openViews = [];
        openViews.push(this.statement.data.id);
        var currentId = this.statement.data.id;
        return {
            "id": this.options.id,
            "application": this.application,
            "openViews": openViews,
            "currentId": currentId
        };
        //}
        //return {"id": this.options.id, "application": this.application};
    }

    // dictionaryExplode: function(){
    //     this.view.explode();
    // },
    // dictionaryImplode: function(){
    //     this.view.implode();
    // }
	//recordStatus: function(){
	//	return {"id": this.options.id};
	//},

});