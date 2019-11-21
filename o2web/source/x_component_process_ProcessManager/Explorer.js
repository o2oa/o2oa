MWF.xApplication.process = MWF.xApplication.process || {};
MWF.APPPM = MWF.xApplication.process.ProcessManager = MWF.xApplication.process.ProcessManager || {};

MWF.xDesktop.requireApp("process.ProcessManager", "lp."+MWF.language, null, false);
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
        this.categoryList = [];
        this.deleteMarkItems = [];
        this.selectMarkItems = [];
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
        this.createCategoryElementNode();
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

        //this.searchElementButtonNode = new Element("div", {"styles": this.css.searchElementButtonNode,"title": this.options.tooltip.search}).inject(this.searchElementNode);
        //
        //this.searchElementInputAreaNode = new Element("div", {
        //    "styles": this.css.searchElementInputAreaNode
        //}).inject(this.searchElementNode);
        //
        //this.searchElementInputBoxNode = new Element("div", {
        //    "styles": this.css.searchElementInputBoxNode
        //}).inject(this.searchElementInputAreaNode);
        //
        //this.searchElementInputNode = new Element("input", {
        //    "type": "text",
        //    "value": this.options.tooltip.searchText,
        //    "styles": this.css.searchElementInputNode,
        //    "x-webkit-speech": "1"
        //}).inject(this.searchElementInputBoxNode);
        //var _self = this;
        //this.searchElementInputNode.addEvents({
        //    "focus": function(){
        //        if (this.value==_self.options.tooltip.searchText) this.set("value", "");
        //    },
        //    "blur": function(){if (!this.value) this.set("value", _self.options.tooltip.searchText);},
        //    "keydown": function(e){
        //        if (e.code==13){
        //            this.searchElement();
        //            e.preventDefault();
        //        }
        //    }.bind(this),
        //    "selectstart": function(e){
        //        e.preventDefault();
        //    }
        //});
        //this.searchElementButtonNode.addEvent("click", function(){this.searchElement();}.bind(this));
    },
    createCategoryElementNode: function(){
        this.categoryElementNode = new Element("div", {
            "styles": this.css.categoryElementNode
        }).inject(this.node);
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

        this.elementContentNode.addEvent("click", function(){
            while (this.selectMarkItems.length){
                this.selectMarkItems[0].unSelected();
            }
        }.bind(this));

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
                    if (this.categoryList.indexOf(item.category) === -1) if (item.category) this.categoryList.push(item.category);
                    if (!this.elementCategory || (item.category === this.elementCategory)){
                        var itemObj = this._getItemObject(item);
                        itemObj.load();
                    }

                }.bind(this));
            }else{
                var noElementNode = new Element("div.noElementNode", {
                    "styles": this.css.noElementNode,
                    "text": this.options.tooltip.noElement
                }).inject(this.elementContentListNode);
                noElementNode.addEvent("click", function(e){
                    this._createElement(e);
                }.bind(this));
            }
            this.loadCategoryList();
        }.bind(this));
    },
    loadCategoryList: function(){
        this.categoryElementNode.empty();
        var node = new Element("div", {"styles": this.css.categoryElementItemAllNode, "text": MWF.xApplication.process.ProcessManager.LP.all}).inject(this.categoryElementNode);
        if (!this.elementCategory) node.setStyles(this.css.categoryElementItemAllNode_current);
        this.categoryList.each(function(category){
            node = new Element("div", {"styles": this.css.categoryElementItemNode, "text": category}).inject(this.categoryElementNode);
            if (this.elementCategory===category){
                node.setStyles(this.css.categoryElementItemNode_current);
            }
        }.bind(this));
        var categoryItems = this.categoryElementNode.getChildren();
        categoryItems.addEvent("click", function(e){
            var text = e.target.get("text");
            this.elementCategory = (text===MWF.xApplication.process.ProcessManager.LP.all) ? "" : text;
            // categoryItems.setStyles(this.css.categoryElementItemNode);
            // this.categoryElementNode.getFirst().setStyles(this.css.categoryElementItemAllNode);
            // e.target.setStyles((this.elementCategory) ? this.css.categoryElementItemNode_current : this.css.categoryElementItemAllNode_current);
            this.reload();
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
                edge: 'centerTop',
                "offset": {"y": this.elementContentNode.getScroll().y}
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
    },
    destroy: function(){
        this.node.destroy();
        o2.release(this);
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
        //this.createDeleteNode();
        this.createActionNode();
        this.createTextNodes();
        this._isNew();
    },
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.css.itemNode,
            "events": {
                "mouseover": function(){
                    this.deleteActionNode.fade("in");
                    if (this.saveasActionNode) this.saveasActionNode.fade("in");
                }.bind(this),
                "mouseout": function(){
                    this.deleteActionNode.fade("out");
                    if (this.saveasActionNode) this.saveasActionNode.fade("out");
                }.bind(this)
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

        itemIconNode.addEvent("click", function(e){
            this.toggleSelected();
            e.stopPropagation();
        }.bind(this));

        itemIconNode.makeLnk({
            "par": this._getLnkPar()
        });
    },
    toggleSelected: function(){
        if (this.isSelected){
            this.unSelected();
        }else{
            this.selected();
        }
    },
    checkShowCopyInfor: function(){
        if (this.explorer.selectMarkItems.length===1){
            this.explorer.app.notice(this.explorer.app.lp.copyInfor, "infor");
        }
    },
    selected: function(){
        if (this.deleteMode) this.deleteItem();
        this.isSelected = true;
        this.node.setStyles(this.css.itemNode_selected);
        this.explorer.selectMarkItems.push(this);

        this.checkShowCopyInfor();
    },
    unSelected: function(){
        this.isSelected = false;
        this.node.setStyles(this.css.itemNode);
        this.explorer.selectMarkItems.erase(this);
    },
    createActionNode: function(){
        this.deleteActionNode = new Element("div", {
            "styles": this.css.deleteActionNode
        }).inject(this.node);
        this.deleteActionNode.addEvent("click", function(e){
            this.deleteItem(e);
        }.bind(this));

        this.saveasActionNode = new Element("div", {
            "styles": this.css.saveasActionNode,
            "title": this.explorer.app.lp.copy
        }).inject(this.node);
        this.saveasActionNode.addEvent("click", function(e){
            this.saveas(e);
        }.bind(this));
    },
    // createDeleteNode: function(){
    //     this.deleteActionNode = new Element("div", {
    //         "styles": this.css.deleteActionNode
    //     }).inject(this.node);
    //     this.deleteActionNode.addEvent("click", function(e){
    //         this.deleteItem(e);
    //     }.bind(this));
    // },
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
    saveas: function(){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var selector = new MWF.O2Selector(this.explorer.app.content, {
                "title": this.explorer.app.lp.copyto,
                "type": "Application",
                "values": [this.explorer.app.options.application],
                "onComplete": function(items){
                    items.each(function(item){
                        this.saveItemAs(item.data);
                    }.bind(this));
                }.bind(this),
            });
        }.bind(this));
    },
    // saveItemAs: function(item){
    //
    // },
    deleteItem: function(){
        if (this.isSelected) this.unSelected();
        if (!this.deleteMode){
            this.deleteMode = true;
            this.node.setStyle("background-color", "#ffb7b7");
            this.deleteActionNode.setStyle("background-image", "url("+"/x_component_process_ProcessManager/$Explorer/default/processIcon/deleteProcess_red1.png)");
            this.node.removeEvents("mouseover");
            this.node.removeEvents("mouseout");
            if (this.saveasActionNode) this.saveasActionNode.fade("out");

            this.explorer.deleteMarkItems.push(this);
        }else{
            this.deleteMode = false;
            this.node.setStyle("background", "#FFF");
            this.deleteActionNode.setStyle("background-image", "url("+"/x_component_process_ProcessManager/$Explorer/default/processIcon/deleteProcess.png)");
            if (this.saveasActionNode) this.saveasActionNode.fade("in");
            this.node.addEvents({
                "mouseover": function(){
                    this.deleteActionNode.fade("in");
                    if (this.saveasActionNode) this.saveasActionNode.fade("in");
                }.bind(this),
                "mouseout": function(){
                    this.deleteActionNode.fade("out");
                    if (this.saveasActionNode) this.saveasActionNode.fade("out");
                }.bind(this)
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
                this.newNode.addEvent("click", function(e){
                    this.toggleSelected();
                    e.stopPropagation();
                }.bind(this));
            }
        }
    }
});
