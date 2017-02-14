MWF.xDesktop.requireApp("cms.ColumnManager", "Explorer", null, false);
MWF.xApplication.cms.ColumnManager.ViewExplorer = new Class({
    Extends: MWF.xApplication.cms.ColumnManager.Explorer,
    Implements: [Options, Events],
    options: {
        "style" : "default",
        "create": MWF.CMSCM.LP.view.create,
        "search": MWF.CMSCM.LP.view.search,
        "searchText": MWF.CMSCM.LP.view.searchText,
        "noElement": MWF.CMSCM.LP.view.noViewNoticeText
    },

    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "/x_component_cms_ColumnManager/$ViewExplorer/";
        this.cssPath = "/x_component_cms_ColumnManager/$ViewExplorer/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.actions = actions;
        this.node = $(node);
        this.initData();
    },
    setContentSize: function(){
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : { x :0 , y :0 };
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y-toolbarSize.y-pt-pb;
        this.elementContentNode.setStyle("height", ""+height+"px");

        if (this.options.noCreate) this.createElementNode.destroy();
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
    //_createElement: function(e){
    //    var _self = this;
    //    var options = {
    //        "onQueryLoad": function(){
    //            this.actions = _self.app.restActions;
    //            this.application = _self.app.options.column;
    //            this.column = _self.app.options.column;
    //        }
    //    };
    //    this.app.desktop.openApplication(e, "cms.ViewDesigner", options);
    //},
    _createElement: function(e){
        var _self = this;
        var createView = function(e, form){
            layout.desktop.getFormDesignerStyle(function(){
                var options = {
                    "style": layout.desktop.formDesignerStyle,
                    "onQueryLoad": function(){
                        this.actions = _self.app.restActions;
                        this.column = _self.app.options.column;
                        this.application = _self.app.options.column;
                        this.relativeForm = form;
                    },
                    "onPostSave" : function(){
                        _self.reload();
                    }
                };
                layout.desktop.openApplication(e, "cms.ViewDesigner", options);
            }.bind(this));

        }

        var selectFormMaskNode = new Element("div", {"styles": this.css.selectFormMaskNode}).inject(this.app.content);
        var selectFormAreaNode = new Element("div", {"styles": this.css.selectFormTemplateAreaNode}).inject(this.app.content);
        selectFormAreaNode.fade("in");

        var selectFormTitleNode = new Element("div",{"styles":this.css.selectFormTitleNode,"text":this.app.lp.view.selectRelativeForm}).inject(selectFormAreaNode);

        var selectFormScrollNode = new Element("div", {"styles": this.css.selectFormScrollNode}).inject(selectFormAreaNode);
        var selectFormContentNode = new Element("div", {"styles": this.css.selectFormContentNode}).inject(selectFormScrollNode);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(selectFormScrollNode, {"indent": false});
        }.bind(this));

        var _self = this;
        this.app.restActions.listForm(this.app.options.column.id, function(json){
            json.data.each(function(form){

                var formNode = new Element("div", {
                    "styles": this.css.formNode
                }).inject(selectFormContentNode);

                var x = "process_icon_" + (Math.random()*33).toInt() + ".png";
                var iconUrl = this.path+this.options.style+"/processIcon/"+x;

                var formIconNode = new Element("div", {
                    "styles": this.css.formIconNode
                }).inject(formNode);
                formIconNode.setStyle("background", "url("+iconUrl+") center center no-repeat");

                new Element("div", {
                    "styles": this.css.formTitleNode,
                    "text": form.name
                }).inject(formNode);

                new Element("div", {
                    "styles": this.css.formDescriptionNode,
                    "text": form.description || "",
                    "title": form.description || ""
                }).inject(formNode);

                new Element("div", {
                    "styles": this.css.formDateNode,
                    "text": (form.updateTime || "")
                }).inject(formNode);

                formNode.store("form", {"name":form.name, "id":form.id});

                formNode.addEvents({
                    "mouseover": function(){this.setStyles(_self.css.formNode_over)},
                    "mouseout": function(){this.setStyles(_self.css.formNode)},
                    "mousedown": function(){this.setStyles(_self.css.formNode_down)},
                    "mouseup": function(){this.setStyles(_self.css.formNode_over)},
                    "click": function(e){
                        createView(e, this.retrieve("form"));
                        selectFormAreaNode.destroy();
                        selectFormMaskNode.destroy();
                    }
                });

            }.bind(this))

        }.bind(this));

        selectFormMaskNode.addEvent("click", function(){
            selectFormAreaNode.destroy();
            selectFormMaskNode.destroy();
        });

        var size = this.app.content.getSize();
        var y = (size.y - 262)/2;
        var x = (size.x - 828)/2;
        if (y<0) y=0;
        if (x<0) x=0;
        selectFormAreaNode.setStyles({
            "top": ""+y+"px",
            "left": ""+x+"px"
        });

    },
    _loadItemDataList: function(callback){
        this.actions.listView(this.app.options.column.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.cms.ColumnManager.ViewExplorer.View(this, item)
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.CMSCM.LP.view.create,
            "search": MWF.CMSCM.LP.view.search,
            "searchText": MWF.CMSCM.LP.view.searchText,
            "noElement": MWF.CMSCM.LP.view.noViewNoticeText
        };
    },
    loadElementList: function(){
        this._loadItemDataList(function(json){
            if (json.data.length){
                json.data.each(function(item){
                    var itemObj = this._getItemObject(item);
                    itemObj.load();
                    this.itemObject[ item.id ] = itemObj;
                    this.itemArray.push( itemObj );
                }.bind(this));
            }else{
                var noElementNode = new Element("div", {
                    "styles": this.css.noElementNode,
                    "text": (this.options.noCreate) ? MWF.CMSCM.LP.view.noViewNoCreateNoticeText : this.options.tooltip.noElement
                }).inject(this.elementContentListNode);
                if (!this.options.noCreate){
                    noElementNode.addEvent("click", function(e){
                        this._createElement(e);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    deleteItems: function(){
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteView();
            }else{
                item.deleteView(function(){
                    //    this.reloadItems();
                    this.hideDeleteAction();
                    this.reload();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.cms.ColumnManager.ViewExplorer.View = new Class({
    Extends: MWF.xApplication.cms.ColumnManager.Explorer.Item,

    load: function(){
        this.node = new Element("div", {
            "styles": this.explorer.css.itemNode,
            "events": {
                "mouseover": function(){if (this.deleteActionNode) this.deleteActionNode.fade("in");}.bind(this),
                "mouseout": function(){if (this.deleteActionNode) this.deleteActionNode.fade("out");}.bind(this)
            }
        }).inject(this.container);

        if (this.data.name.icon) this.icon = this.data.name.icon;
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

        if (!this.explorer.options.noDelete){
            this.deleteActionNode = new Element("div", {
                "styles": this.explorer.css.deleteActionNode
            }).inject(this.node);
            this.deleteActionNode.addEvent("click", function(e){
                this.deleteItem(e);
            }.bind(this));
        }

        var inforNode = new Element("div", {
            "styles": this.explorer.css.itemInforNode
        }).inject(this.node);
        var inforBaseNode = new Element("div", {
            "styles": this.explorer.css.itemInforBaseNode
        }).inject(inforNode);

        new Element("div", {
            "styles": this.explorer.css.itemTextTitleNode,
            "text": this.data.name,
            "title": this.data.name,
            "events": {
                "click": function(e){this._open(e);e.stopPropagation();}.bind(this)
            }
        }).inject(inforBaseNode);

        new Element("div", {
            "styles": this.explorer.css.itemTextAliasNode,
            "text": this.data.alias,
            "title": this.data.alias
        }).inject(inforBaseNode);
        new Element("div", {
            "styles": this.explorer.css.itemTextDateNode,
            "text": (this.data.updateTime || "")
        }).inject(inforBaseNode);

        new Element("div", {
            "styles": this.explorer.css.itemTextDescriptionNode,
            "text": this.data.description || "",
            "title": this.data.description || ""
        }).inject(inforNode);

        this._customNodes();

        this._isNew();
    },
    _customNodes: function(){},

    _open: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.column = _self.explorer.app.options.column;
                this.application = _self.explorer.app.options.column;
                this.options.noModifyName = _self.explorer.options.noModifyName;
                this.options.readMode = _self.explorer.options.readMode,
                this.options.formId = _self.data.formId;
            }
        };
        this.explorer.app.desktop.openApplication(e, "cms.ViewDesigner", options);
    },
    _getIcon: function(){
        var x = (Math.random()*33).toInt();
        return "process_icon_"+x+".png";
    },
    _getLnkPar: function(){
        return {
            "icon": this.explorer.path+this.explorer.options.style+"/viewIcon/lnk.png",
            "title": this.data.name,
            "par": "cms.ViewDesigner#{\"id\": \""+this.data.id+"\"}"
        };
    },
//	deleteItem: function(e){
//		var _self = this;
//		this.explorer.app.confirm("info", e, this.explorer.app.lp.form.deleteFormTitle, this.explorer.app.lp.form.deleteForm, 320, 110, function(){
//			_self.deleteForm();
//			this.close();
//		},function(){
//			this.close();
//		});
//	},
    deleteView: function(callback){
        this.explorer.app.restActions.deleteView(this.data.id, function(){
            this.node.destroy();
            if (callback) callback();
        }.bind(this));
    }
});
