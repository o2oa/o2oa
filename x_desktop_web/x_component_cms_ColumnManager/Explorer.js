MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.ColumnManager = MWF.xApplication.cms.ColumnManager || {};

MWF.xDesktop.requireApp("cms.ColumnManager", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("cms.ColumnManager", "package", null, false);

MWF.xApplication.cms.ColumnManager.Explorer = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"tooltip": {
            "create": MWF.CMSCM.LP.category.create,
            "search": MWF.CMSCM.LP.category.search,
            "searchText": MWF.CMSCM.LP.category.searchText,
            "noElement": MWF.CMSCM.LP.category.noCategoryNoticeText
		}
	},
	
	initialize: function(node, actions, options){
		this.setOptions(options);
		this.setTooltip();
		this.path = "/x_component_cms_ColumnManager/$Explorer/";
		this.cssPath = "/x_component_cms_ColumnManager/$Explorer/"+this.options.style+"/css.wcss";

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
        this.itemArray = [];
        this.itemObject = {};
        this.deleteMarkItems = [];
	},
    reload: function(){
        this.initData();
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

        if (this.app.options.column){
            var icon = this.app.options.column.icon || this.app.options.column.appIcon;
            if (icon){
                this.iconElementNode.setStyle("background-image", "url(data:image/png;base64,"+icon+")");
            }else{
                this.iconElementNode.setStyle("background-image", "url("+"/x_component_cms_Column/$Main/default/icon/column.png)");
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
            "text": this.app.options.column.name || this.app.options.column.appName
        }).inject(this.toolbarNode);
    },
    createSearchElementNode: function(){
        this.searchElementNode = new Element("div", {
            "styles": this.css.searchElementNode
        }).inject(this.toolbarNode);

        //@todo
        return false

        this.searchElementButtonNode = new Element("div", {
            "styles": this.css.searchElementButtonNode,
            "title": this.options.tooltip.search
        }).inject(this.searchElementNode);

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
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : { x : 0 , y : 0 };
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
                    this.itemObject[ item.id ] = itemObj;
                    this.itemArray.push( itemObj );
                }.bind(this));
            }else{
                var noElementNode = this.noElementNode = new Element("div", {
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
                this.app.confirm("warn", this.deleteItemsAction, MWF.CMSCM.LP.deleteElementTitle, MWF.CMSCM.LP.deleteElement, 300, 120, function(){
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
        this.app.restActions.listCategory(this.app.options.column.id,callback);
    },
    _getItemObject: function(item){
        return MWF.xApplication.cms.ColumnManager.Explorer.Item(this, item)
    }
});

MWF.xApplication.cms.ColumnManager.Explorer.Item = new Class({

    Implements: [Options],
    options: {
        "where": "bottom"
    },

    initialize: function(explorer, item, options ){
        this.setOptions(options);
        this.explorer = explorer;
        this.data = item;
        this.container = this.explorer.elementContentListNode;

        this.icon = this._getIcon();
    },

    load: function(){
        this.node = new Element("div", {
            "styles": this.explorer.css.itemNode,
            "events": {
                "mouseover": function(){this.deleteActionNode.fade("in");}.bind(this),
                "mouseout": function(){this.deleteActionNode.fade("out");}.bind(this)
            }
        }).inject(this.container,this.options.where);

        if (this.data.icon) this.icon = this.data.icon;
        var iconUrl = this.explorer.path+""+this.explorer.options.style+"/processIcon/"+this.icon;

        var itemIconNode = new Element("div", {
            "styles": this.explorer.css.itemIconNode
        }).inject(this.node);
        itemIconNode.setStyle("background", "url("+iconUrl+") center center no-repeat");
        //new Element("img", {
        //    "src": iconUrl, "border": "0"
        //}).inject(itemIconNode);

        itemIconNode.makeLnk({
            "par": this._getLnkPar()
        });

        this.deleteActionNode = new Element("div", {
            "styles": this.explorer.css.deleteActionNode
        }).inject(this.node);
        this.deleteActionNode.addEvent("click", function(e){
            this.deleteItem(e);
        }.bind(this));

        new Element("div", {
            "styles": this.explorer.css.itemTextTitleNode,
            "text": this.data.name,
            "title": this.data.name,
            "events": {
                "click": function(e){this._open(e);}.bind(this)
            }
        }).inject(this.node);

        new Element("div", {
            "styles": this.explorer.css.itemTextDescriptionNode,
            "text": this.data.description || "",
            "title": this.data.description || ""
        }).inject(this.node);

        new Element("div", {
            "styles": this.explorer.css.itemTextDateNode,
            "text": (this.data.updateTime || "")
        }).inject(this.node);

        this._isNew();
    },

    deleteItem: function(){
        if (!this.deleteMode){
            this.deleteMode = true;
            this.node.setStyle("background-color", "#ffb7b7");
            this.deleteActionNode.setStyle("background-image", "url("+"/x_component_cms_ColumnManager/$Explorer/default/processIcon/deleteProcess_red1.png)");
            this.node.removeEvents("mouseover");
            this.node.removeEvents("mouseout");
            this.explorer.deleteMarkItems.push(this);
        }else{
            this.deleteMode = false;
            this.node.setStyle("background", "#FFF");
            this.deleteActionNode.setStyle("background-image", "url("+"/x_component_cms_ColumnManager/$Explorer/default/processIcon/deleteProcess.png)");
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
                this.column = _self.explorer.app.options.column;
                this.application = _self.explorer.app.options.column;
            }
        };
        this.explorer.app.desktop.openApplication(e, "cms.ProcessDesigner", options);
    },
    _getIcon: function(){
        var x = (Math.random()*33).toInt();
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
                    "styles": this.explorer.css.itemNewNode
                }).inject(this.node);
            }
        }
    }
});
