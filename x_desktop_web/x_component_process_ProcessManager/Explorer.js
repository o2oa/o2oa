MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.ProcessManager = MWF.xApplication.process.ProcessManager || {};

MWF.xDesktop.requireApp("process.ProcessManager", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("process.ProcessManager", "package", null, false);
MWF.xApplication.process.ProcessManager.Explorer = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"tooltip": {
            "create": MWF.APPPM.LP.process.create,
            "search": MWF.APPPM.LP.process.search,
            "searchText": MWF.APPPM.LP.process.searchText,
            "noElement": MWF.APPPM.LP.process.noProcessNoticeText
		}
	},
	
	initialize: function(node, actions, options){
		this.setOptions(options);
		this.setTooltip();
		
		this.path = "/x_component_process_ProcessManager/$Explorer/";
		this.cssPath = "/x_component_process_ProcessManager/$Explorer/"+this.options.style+"/css.wcss";

		this._loadCss();
		
		this.actions = actions;
		this.node = $(node);
		this.initData();
	},
	setTooltip: function(tooltip){
		if (tooltip) this.options.tooltip = Object.merge(this.options.tooltip, tooltip);
	},
	initData: function(){
		//this.categoryLoadFirst = true;
		//this.isLoaddingCategory = false;
		//this.categoryLoaded = false;
		//this.categorys = [];
		//this.dragItem = false;
		//this.dragCategory = false;
		//this.currentCategory = null;
		//this.loadCategoryQueue = 0;

        this.deleteMarkItems = [];
	},
    reload: function(){
        this.node.empty();
        this.load();
    },
    load: function(){
        this.loadToolbar();
        this.loadContentNode();

        this.setNodeScroll();
        this.loadElementList();
    },
    loadToolbar: function(){
        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode});
        this.createCreateElementNode();
        this.createIconElementNode();

        this.createTitleElementNode();
        this.createSearchElementNode();
        this.toolbarNode.inject(this.node);
    },
    createIconElementNode: function(){
        this.iconElementNode = new Element("div", {
            "styles": this.css.iconElementNode
        }).inject(this.toolbarNode);

        if (this.app.options.application){
            if (this.app.options.application.icon){
                this.iconElementNode.setStyle("background-image", "url(data:image/png;base64,"+this.app.options.application.icon+")");
            }else{
                this.iconElementNode.setStyle("background-image", "url("+"/x_component_process_ApplicationExplorer/$Main/default/icon/application.png)");
            }
        }
    },
    createCreateElementNode: function(){
        this.createElementNode = new Element("div", {
            "styles": this.css.createElementNode,
            "title": this.options.tooltip.create
        }).inject(this.toolbarNode);
        this.createElementNode.addEvent("click", function(e){
            this._createElement(e);
        }.bind(this));
    },
    createTitleElementNode: function() {
        this.titleElementNode = new Element("div", {
            "styles": this.css.titleElementNode,
            "text": this.app.options.application.name
        }).inject(this.toolbarNode);
    },
    createSearchElementNode: function(){
        this.searchElementNode = new Element("div", {
            "styles": this.css.searchElementNode
        }).inject(this.toolbarNode);

        //@todo
        return false;

        this.searchElementButtonNode = new Element("div", {"styles": this.css.searchElementButtonNode,"title": this.options.tooltip.search}).inject(this.searchElementNode);

        this.searchElementInputAreaNode = new Element("div", {
            "styles": this.css.searchElementInputAreaNode
        }).inject(this.searchElementNode);

        this.searchElementInputBoxNode = new Element("div", {
            "styles": this.css.searchElementInputBoxNode
        }).inject(this.searchElementInputAreaNode);

        this.searchElementInputNode = new Element("input", {
            "type": "text",
            "value": this.options.tooltip.searchText,
            "styles": this.css.searchElementInputNode,
            "x-webkit-speech": "1"
        }).inject(this.searchElementInputBoxNode);
        var _self = this;
        this.searchElementInputNode.addEvents({
            "focus": function(){
                if (this.value==_self.options.tooltip.searchText) this.set("value", "");
            },
            "blur": function(){if (!this.value) this.set("value", _self.options.tooltip.searchText);},
            "keydown": function(e){
                if (e.code==13){
                    this.searchElement();
                    e.preventDefault();
                }
            }.bind(this),
            "selectstart": function(e){
                e.preventDefault();
            }
        });
        this.searchElementButtonNode.addEvent("click", function(){this.searchElement();}.bind(this));
    },
    searchElement: function(){
        //-----------------------------------------
        //-----------------------------------------
        //-----search category---------------------
        //-----------------------------------------
        //-----------------------------------------
        alert("search Element");
    },

    loadContentNode: function(){
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.elementContentListNode = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);

        this.setContentSize();
        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));
    },
    setContentSize: function(){
        var toolbarSize = this.toolbarNode.getSize();
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y-toolbarSize.y-pt-pb;
        this.elementContentNode.setStyle("height", ""+height+"px");

        var count = (nodeSize.x/282).toInt();
        var x = count*282;
        var m = (nodeSize.x-x)/2-10;

        this.elementContentListNode.setStyles({
            "width": ""+x+"px",
            "margin-left": "" + m + "px"
        });
    },
    setNodeScroll: function(){
        MWF.require("MWF.widget.DragScroll", function(){
            new MWF.widget.DragScroll(this.elementContentNode);
        }.bind(this));
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.elementContentNode, {"indent": false});
        }.bind(this));
    },

    loadElementList: function(){
        this._loadItemDataList(function(json){
            if (json.data.length){
                json.data.each(function(item){
                    var itemObj = this._getItemObject(item);
                    itemObj.load()
                }.bind(this));
            }else{
                var noElementNode = new Element("div", {
                    "styles": this.css.noElementNode,
                    "text": this.options.tooltip.noElement
                }).inject(this.elementContentListNode);
                noElementNode.addEvent("click", function(e){
                    this._createElement(e);
                }.bind(this));
            }
        }.bind(this));
    },

    showDeleteAction: function(){
        if (!this.deleteItemsAction){
            this.deleteItemsAction = new Element("div", {
                "styles": this.css.deleteItemsAction,
                "text": this.app.lp.deleteItems
            }).inject(this.node);
            this.deleteItemsAction.fade("in");
            this.deleteItemsAction.position({
                relativeTo: this.elementContentListNode,
                position: 'centerTop',
                edge: 'centerTop'
            });
            this.deleteItemsAction.addEvent("click", function(){
                var _self = this;
                this.app.confirm("warn", this.deleteItemsAction, MWF.APPPM.LP.deleteElementTitle, MWF.APPPM.LP.deleteElement, 300, 120, function(){
                    _self.deleteItems();
                    this.close();
                }, function(){
                    this.close();
                });
            }.bind(this));
        }
    },
    hideDeleteAction: function(){
        if (this.deleteItemsAction) this.deleteItemsAction.destroy();
        delete this.deleteItemsAction;
    },

    _createElement: function(e){

    },
    _loadItemDataList: function(callback){
        this.app.restActions.listProcess(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return MWF.xApplication.process.ProcessManager.Explorer.Item(this, item)
    }
});

MWF.xApplication.process.ProcessManager.Explorer.Item = new Class({
    initialize: function(explorer, item){
        this.explorer = explorer;
        this.data = item;
        this.container = this.explorer.elementContentListNode;
        this.css = this.explorer.css;

        this.icon = this._getIcon();
    },

    load: function(){
        this.createNode();
        this.createIconNode();
        this.createDeleteNode();
        this.createTextNodes();
        this._isNew();
    },
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.css.itemNode,
            "events": {
                "mouseover": function(){this.deleteActionNode.fade("in");}.bind(this),
                "mouseout": function(){this.deleteActionNode.fade("out");}.bind(this)
            }
        }).inject(this.container);
    },
    createIconNode: function(){
        if (this.data.icon) this.icon = this.data.icon.substr(this.data.icon.lastIndexOf("/")+1, this.data.icon.length);
        //if (this.data.name.icon) this.icon = this.data.name.icon;
        var iconUrl = this.explorer.path+""+this.explorer.options.style+"/processIcon/"+this.icon;

        var itemIconNode = new Element("div", {
            "styles": this.css.itemIconNode
        }).inject(this.node);
        itemIconNode.setStyle("background", "url("+iconUrl+") center center no-repeat");

        itemIconNode.makeLnk({
            "par": this._getLnkPar()
        });
    },
    createDeleteNode: function(){
        this.deleteActionNode = new Element("div", {
            "styles": this.css.deleteActionNode
        }).inject(this.node);
        this.deleteActionNode.addEvent("click", function(e){
            this.deleteItem(e);
        }.bind(this));
    },
    createTextNodes: function(){
        new Element("div", {
            "styles": this.css.itemTextTitleNode,
            "text": this.data.name,
            "title": this.data.name,
            "events": {
                "click": function(e){this._open(e);}.bind(this)
            }
        }).inject(this.node);

        new Element("div", {
            "styles": this.css.itemTextDescriptionNode,
            "text": this.data.description || "",
            "title": this.data.description || ""
        }).inject(this.node);

        new Element("div", {
            "styles": this.css.itemTextDateNode,
            "text": (this.data.updateTime || "")
        }).inject(this.node);
    },

    deleteItem: function(){
        if (!this.deleteMode){
            this.deleteMode = true;
            this.node.setStyle("background-color", "#ffb7b7");
            this.deleteActionNode.setStyle("background-image", "url("+"/x_component_process_ProcessManager/$Explorer/default/processIcon/deleteProcess_red1.png)");
            this.node.removeEvents("mouseover");
            this.node.removeEvents("mouseout");
            this.explorer.deleteMarkItems.push(this);
        }else{
            this.deleteMode = false;
            this.node.setStyle("background", "#FFF");
            this.deleteActionNode.setStyle("background-image", "url("+"/x_component_process_ProcessManager/$Explorer/default/processIcon/deleteProcess.png)");
            this.node.addEvents({
                "mouseover": function(){this.deleteActionNode.fade("in");}.bind(this),
                "mouseout": function(){this.deleteActionNode.fade("out");}.bind(this)
            });
            this.explorer.deleteMarkItems.erase(this);
        }
        if (this.explorer.deleteMarkItems.length){
            this.explorer.showDeleteAction();
        }else{
            this.explorer.hideDeleteAction();
        }
    },
    deleteItems: function(){},
    _open: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
            }
        };
        this.explorer.app.desktop.openApplication(e, "process.ProcessDesigner", options);
    },
    _getIcon: function(){
        var x = (Math.random()*49).toInt();
        return "process_icon_"+x+".png";
    },
    _getLnkPar: function(){
        return {
            "icon": this.explorer.path+this.explorer.options.style+"/processIcon/lnk.png",
            "title": this.data.name,
            "par": "ProcessDesigner#{\"id\": \""+this.data.id+"\"}"
        };
    },
    _isNew: function(){
        if (this.data.updateTime){
            var createDate = Date.parse(this.data.updateTime);
            var currentDate = new Date();
            if (createDate.diff(currentDate, "hour")<12) {
                this.newNode = new Element("div", {
                    "styles": this.css.itemNewNode
                }).inject(this.node);
            }
        }
    }
});
