MWF.xApplication.cms = MWF.xApplication.cms || {};
//MWF.xApplication.cms.Column = MWF.xApplication.cms.Column || {};
MWF.xDesktop.requireApp("cms.Column", "Actions.RestActions", null, false);
MWF.xApplication.cms.Column.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "cms.Column",
        "icon": "icon.png",
        "width": "1000",
        "height": "600",
        "isResize": false,
        "isMax": true,
        "title": MWF.xApplication.cms.Column.LP.title,
        "tooltip": {
            "description": MWF.xApplication.cms.Column.LP.description,
            "column": {
                "title": MWF.xApplication.cms.Column.LP.column.title,
                "create": MWF.xApplication.cms.Column.LP.column.create,
                "nameLabel": MWF.xApplication.cms.Column.LP.column.nameLabel,
                "aliasLabel": MWF.xApplication.cms.Column.LP.column.aliasLabel,
                "descriptionLabel": MWF.xApplication.cms.Column.LP.column.descriptionLabel,
                "sortLabel": MWF.xApplication.cms.Column.LP.column.sortLabel,
                "iconLabel": MWF.xApplication.cms.Column.LP.column.iconLabel,
                "cancel": MWF.xApplication.cms.Column.LP.column.cancel,
                "ok": MWF.xApplication.cms.Column.LP.column.ok,
                "inputName": MWF.xApplication.cms.Column.LP.column.inputName,
                "create_cancel_title": MWF.xApplication.cms.Column.LP.column.create_cancel_title,
                "create_cancel": MWF.xApplication.cms.Column.LP.column.create_cancel,
                "noDescription": MWF.xApplication.cms.Column.LP.column.noDescription,
                "delete": MWF.xApplication.cms.Column.LP.column.delete,
                "edit": MWF.xApplication.cms.Column.LP.column.edit,
                "delete_confirm_content": MWF.xApplication.cms.Column.LP.column.delete_confirm_content,
                "delete_confirm_title": MWF.xApplication.cms.Column.LP.column.delete_confirm_title,
                "createColumnSuccess": MWF.xApplication.cms.Column.LP.column.createColumnSuccess,
                "updateColumnSuccess": MWF.xApplication.cms.Column.LP.column.updateColumnSuccess
            },
            "category": {
                "title": MWF.xApplication.cms.Column.LP.category.title,
                "create": MWF.xApplication.cms.Column.LP.category.create,
                "nameLabel": MWF.xApplication.cms.Column.LP.category.nameLabel,
                "aliasLabel": MWF.xApplication.cms.Column.LP.category.aliasLabel,
                "descriptionLabel": MWF.xApplication.cms.Column.LP.category.descriptionLabel,
                "sortLabel": MWF.xApplication.cms.Column.LP.category.sortLabel,
                "iconLabel": MWF.xApplication.cms.Column.LP.category.iconLabel,
                "columnLabel": MWF.xApplication.cms.Column.LP.category.columnLabel,
                "cancel": MWF.xApplication.cms.Column.LP.category.cancel,
                "ok": MWF.xApplication.cms.Column.LP.category.ok,
                "inputName": MWF.xApplication.cms.Column.LP.category.inputName,
                "create_cancel_title": MWF.xApplication.cms.Column.LP.category.create_cancel_title,
                "create_cancel": MWF.xApplication.cms.Column.LP.category.create_cancel,
                "noDescription": MWF.xApplication.cms.Column.LP.category.noDescription,
                "edit": MWF.xApplication.cms.Column.LP.category.edit
            }
        }
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.cms.Column.LP;
        this.defaultColumnIcon = "/x_component_cms_Column/$Main/" + this.options.style + "/icon/column.png";
        this.defaultCategoryIcon = "/x_component_cms_Column/$Main/" + this.options.style + "/icon/category2.png";
    },
    loadApplication: function (callback) {
        this.isAdmin = ( MWF.AC.isProcessPlatformCreator() || MWF.AC.isAdministrator() );
        if (!this.restActions) this.restActions = new MWF.xApplication.cms.Column.Actions.RestActions();
        this.columns = [];
        this.categorys = [];
        this.deleteElements = [];
        this.createNode();
        this.loadApplicationContent();
        if (callback) callback();
    },
    loadApplicationContent: function () {
        //this.loadToolbar();
        this.loadColumnArea();
        //this.loadCategoryArea();
    },
    createNode: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.content);
    },
    loadToolbar: function () {
        this.toolbarAreaNode = new Element("div", {
            "styles": this.css.toolbarAreaNode,
            "text": this.options.tooltip.description
        }).inject(this.node);
        //this.createCreateAction();
        //this.createSearchAction();
    },
    createCreateAction: function () {
        //if (MWF.AC.isProcessPlatformCreator()){
        this.createCategoryNode = new Element("div", {
            "styles": this.css.createCategoryNode,
            "title": this.options.tooltip.category.create
        }).inject(this.toolbarAreaNode);
        this.createCategoryNode.addEvent("click", function () {
            this.createCategory();
        }.bind(this));
        //}
    },
    loadColumnArea: function () {
        this.columnAreaNode = new Element("div", {
            "styles": this.css.columnAreaNode
        }).inject(this.node);

        this.columnToolbarAreaNode = new Element("div", {
            "styles": this.css.columnToolbarAreaNode
        }).inject(this.columnAreaNode);

        if (MWF.AC.isProcessPlatformCreator()) {
            if (MWF.AC.isAdministrator()) {
                this.createColumnNode = new Element("button", {
                    "styles": this.css.createColumnNode,
                    "text": this.options.tooltip.column.create
                }).inject(this.columnToolbarAreaNode);
                this.createColumnNode.addEvent("click", function () {
                    this.createColumn();
                }.bind(this));
            }
        }

        this.columnToolbarTextNode = new Element("div", {
            "styles": this.css.columnToolbarTextNode,
            "text": this.options.tooltip.column.title
        }).inject(this.columnToolbarAreaNode);


        this.setColumnAreaSize();
        this.addEvent("resize", this.setColumnAreaSize);

        this.loadColumnContentArea();

        this.setColumnContentSize();
    },
    setColumnAreaSize: function () {
        var nodeSize = this.node.getSize();
        var toolbarSize = this.columnToolbarAreaNode.getSize();
        var y = nodeSize.y - toolbarSize.y;

        this.columnAreaNode.setStyle("height", "" + y + "px");

        if (this.columnContentAreaNode) {
            var count = (nodeSize.x / 282).toInt();
            var x = 282 * count;
            var m = (nodeSize.x - x) / 2 - 10;
            this.columnContentAreaNode.setStyles({
                //"width": ""+x+"px",
                "margin-left": "" + m + "px"
            });
        }
    },
    setColumnContentSize: function () {
        var nodeSize = this.node.getSize();
        if (this.columnContentAreaNode) {
            var count = (nodeSize.x / 282).toInt();
            var x = 282 * count;
            var m = (nodeSize.x - x) / 2 - 10;
            this.columnContentAreaNode.setStyles({
                //"width": ""+x+"px",
                "margin-left": "" + m + "px"
            });
        }
    },
    loadColumnContentArea: function () {

        this.columnContentAreaNode = new Element("div", {
            "styles": this.css.columnContentAreaNode
        }).inject(this.columnAreaNode);

        this.loadController(function () {
            this.createColumnNodes()
        }.bind(this))

        //MWF.require("MWF.widget.DragScroll", function(){
        //	new MWF.widget.DragScroll(this.columnContentAreaNode);
        //}.bind(this));
        MWF.require("MWF.widget.ScrollBar", function () {
            new MWF.widget.ScrollBar(this.columnContentAreaNode);
        }.bind(this));
    },
    loadController: function (callback) {
        this.availableApp = [];
        this.restActions.listControllerByPerson(layout.desktop.session.user.name, function (json) {
            if (json && json.data && json.data.length) {
                json.data.each(function (d) {
                    //if( d.objectType == "APPINFO"){
                    this.availableApp.push(d.objectId)
                    //}
                }.bind(this))
            }
            if (callback)callback();
        }.bind(this), null, true)
    },
    hasPermision: function (appId) {
        return this.isAdmin || this.availableApp.contains(appId);
    },
    createColumnNodes: function () {
        this.restActions.listColumn(function (json) {
            var emptyColumn = null;
            if (json && json.data && json.data.length) {
                var tmpArr = json.data;
                tmpArr.sort(function(a , b ){
                    return parseFloat( a.appInfoSeq ) - parseFloat(b.appInfoSeq);
                })
                json.data = tmpArr;
                json.data.each(function (column) {
                    if (this.hasPermision(column.id)) {
                        var column = new MWF.xApplication.cms.Column.Column(this, column);
                        column.load();
                        this.columns.push(column);
                    }
                }.bind(this));
            }

            if (this.columns.length == 0) {
                this.noElementNode = new Element("div", {
                    "styles": this.css.noElementNode,
                    "text": this.lp.column.noElement
                }).inject(this.columnContentAreaNode);
            }
        }.bind(this));
    },
    createColumn: function (text, alias, memo, icon, creator) {
        var column = new MWF.xApplication.cms.Column.Column(this);
        column.createColumn(this.node);
    },
    /*
     createLoadding: function(){
     this.loaddingNode = new Element("div", {
     "styles": this.css.noApplicationNode,
     "text": this.options.tooltip.loadding
     }).inject(this.applicationContentNode);
     },
     removeLoadding: function(){
     if (this.loaddingNode) this.loaddingNode.destroy();
     },
     */
});


MWF.xApplication.cms.Column.Column = new Class({
    Implements: [Options, Events],
    options: {
        "where": "bottom"
    },

    initialize: function (app, data, options) {
        this.setOptions(options);
        this.app = app;
        this.container = this.app.columnContentAreaNode;
        this.data = data;
        this.isNew = false;
    },
    load: function () {

        this.data.name = this.data.appName;
        var columnName = this.data.appName;
        var alias = this.data.appAlias;
        var memo = this.data.description;
        var order = this.data.appInfoSeq;
        var creator = this.data.creatorUid;
        var createTime = this.data.createTime;
        //var icon = this.data.appIcon;
        //if( !icon || icon == "")icon = this.app.defaultColumnIcon;

        var itemNode = this.node = new Element("div.columnItem", {
            "styles": this.app.css.columnItemNode
        }).inject(this.container, this.options.where);

        itemNode.store("columnName", columnName);
        //itemNode.setStyle("background-color", this.options.bgColor[(Math.random()*10).toInt()]);

        var iconNode = this.iconNode = new Element("div", {
            "styles": this.app.css.columnItemIconNode
        }).inject(itemNode);
        //iconNode.setStyles({
        //	"background-image" : "url("+icon+")"
        //});
        if (this.data.appIcon) {
            this.iconNode.setStyle("background-image", "url(data:image/png;base64," + this.data.appIcon + ")");
        } else {
            this.iconNode.setStyle("background-image", "url(" + this.app.defaultColumnIcon + ")")
        }

        var textNode = new Element("div", {
            "styles": this.app.css.columnItemTextNode
        }).inject(itemNode)

        var titleNode = new Element("div", {
            "styles": this.app.css.columnItemTitleNode,
            "text": columnName,
            "title": (alias) ? columnName + " (" + alias + ") " : columnName
        }).inject(textNode)

        var description = ( memo && memo != "") ? memo : this.app.options.tooltip.column.noDescription;
        var descriptionNode = new Element("div", {
            "styles": this.app.css.columnItemDescriptionNode,
            "text": description,
            "title": description
        }).inject(textNode)

        var _self = this;
        itemNode.addEvents({
            "mouseover": function () {
                if (!_self.selected) this.setStyles(_self.app.css.columnItemNode_over);
            },
            "mouseout": function () {
                if (!_self.selected) this.setStyles(_self.app.css.columnItemNode);
            },
            "click": function (e) {
                _self.clickColumnNode(_self, this, e)
            }
        });

        if (MWF.AC.isProcessPlatformCreator()) {
            if ((creator == layout.desktop.session.user.name) || MWF.AC.isAdministrator()) {
                this.delAdctionNode = new Element("div.delNode", {
                    "styles": this.app.css.columnItemDelActionNode,
                    "title": this.app.options.tooltip.column.delete
                }).inject(itemNode);

                itemNode.addEvents({
                    "mouseover": function () {
                        this.delAdctionNode.fade("in");
                    }.bind(this),
                    "mouseout": function () {
                        this.delAdctionNode.fade("out");
                    }.bind(this)
                });
                this.delAdctionNode.addEvent("click", function (e) {
                    this.deleteColumn(e);
                    e.stopPropagation();
                }.bind(this));
            }
        }

        if (MWF.AC.isProcessPlatformCreator()) {
            if ((creator == layout.desktop.session.user.name) || MWF.AC.isAdministrator()) {
                this.editAdctionNode = new Element("div.editNode", {
                    "styles": this.app.css.columnItemEditActionNode,
                    "title": this.app.options.tooltip.column.edit
                }).inject(itemNode);

                itemNode.addEvents({
                    "mouseover": function () {
                        this.editAdctionNode.fade("in");
                    }.bind(this),
                    "mouseout": function () {
                        this.editAdctionNode.fade("out");
                    }.bind(this)
                });
                this.editAdctionNode.addEvent("click", function (e) {
                    this.edit(e);
                    e.stopPropagation();
                }.bind(this));
            }
        }
    },
    clickColumnNode: function (_self, el, e) {
        /*
         _self.app.columns.each(function( column ){
         if( column.selected ){
         column.itemNode.setStyles( _self.app.css.columnItemNode );
         }
         })
         this.selected = true;
         el.setStyles( _self.app.css.columnItemNode_select );
         */
        var appId = "cms.ColumnManager" + this.data.id;
        if (this.app.desktop.apps[appId]) {
            this.app.desktop.apps[appId].setCurrent();
        } else {
            this.app.desktop.openApplication(e, "cms.ColumnManager", {
                "column": this.data,
                "appId": appId,
                "onQueryLoad": function () {
                    this.status = {"navi": 0};
                }
            });
        }
    },
    checkDeleteColumn: function () {
        if (this.deleteElements.length) {
            if (!this.deleteElementsNode) {
                this.deleteElementsNode = new Element("div", {
                    "styles": this.app.css.deleteElementsNode,
                    "text": this.app.lp.column.deleteElements
                }).inject(this.node);
                this.deleteElementsNode.position({
                    relativeTo: this.container,
                    position: "centerTop",
                    edge: "centerbottom"
                });
                this.deleteElementsNode.addEvent("click", function (e) {
                    this.delete();
                }.bind(this));
            }
        } else {
            if (this.deleteElementsNode) {
                this.deleteElementsNode.destroy();
                this.deleteElementsNode = null;
                delete this.deleteElementsNode;
            }
        }
    },
    deleteColumn: function (e) {
        var _self = this;
        this.app.confirm("warn", e, this.app.options.tooltip.column.delete_confirm_title,
            this.app.options.tooltip.column.delete_confirm_content, "320px", "100px", function () {
                _self._deleteElement();
                this.close();
            }, function () {
                this.close();
            }
        )
    },
    _deleteElement: function (id, success, failure) {
        this.app.restActions.removeColumn(this.data.id, function () {
            this.destroy();
            if (success) success();
        }.bind(this), function (xhr, text, error) {
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            if (failure) failure(errorText);
        }.bind(this));
    },
    destroy: function () {
        this.node.destroy();
        MWF.release(this);
        delete this;
    },
    edit: function () {
        this.isNew = false;
        this.createContainer = this.app.node;
        this.createColumnCreateMarkNode();
        this.createColumnCreateAreaNode();
        this.createColumnCreateNode();

        this.columnCreateAreaNode.inject(this.columnCreateMarkNode, "after");
        this.columnCreateAreaNode.fade("in");
        $("createColumnName").focus();

        this.setColumnCreateNodeSize();
        this.setColumnCreateNodeSizeFun = this.setColumnCreateNodeSize.bind(this);
        this.addEvent("resize", this.setColumnCreateNodeSizeFun);
    },
    createColumn: function (container) {
        this.isNew = true;
        this.createContainer = container;
        this.createColumnCreateMarkNode();
        this.createColumnCreateAreaNode();
        this.createColumnCreateNode();

        this.columnCreateAreaNode.inject(this.columnCreateMarkNode, "after");
        this.columnCreateAreaNode.fade("in");
        $("createColumnName").focus();

        this.setColumnCreateNodeSize();
        this.setColumnCreateNodeSizeFun = this.setColumnCreateNodeSize.bind(this);
        this.addEvent("resize", this.setColumnCreateNodeSizeFun);
    },
    createColumnCreateMarkNode: function () {
        this.columnCreateMarkNode = new Element("div", {
            "styles": this.app.css.columnCreateMarkNode,
            "events": {
                "mouseover": function (e) {
                    e.stopPropagation();
                },
                "mouseout": function (e) {
                    e.stopPropagation();
                }
            }
        }).inject(this.createContainer, "after");
    },
    createColumnCreateAreaNode: function () {
        this.columnCreateAreaNode = new Element("div", {
            "styles": this.app.css.columnCreateAreaNode
        });
    },
    createColumnCreateNode: function () {

        if (!this.isNew) {
            var columnName = this.data.appName;
            var alias = this.data.appAlias;
            var memo = this.data.description;
            var order = this.data.appInfoSeq;
            var creator = this.data.creatorUid;
            var createTime = this.data.createTime;
            //var icon = this.data.appIcon;
            //if( !icon || icon == "")icon = this.app.defaultColumnIcon;
        } else {
            var columnName = "";
            var alias = "";
            var memo = "";
            var order = "";
            var creator = "";
            var icon = "";
            var createTime = "";
        }

        this.columnCreateNode = new Element("div", {
            "styles": this.app.css.columnCreateNode
        }).inject(this.columnCreateAreaNode);

        this.columnCreateNewNode = new Element("div", {
            "styles": ( this.isNew ? this.app.css.columnCreateNewNode : this.app.css.columnCreateEditNode )
        }).inject(this.columnCreateNode);

        this.columnCreateFormNode = new Element("div", {
            "styles": this.app.css.columnCreateFormNode
        }).inject(this.columnCreateNode);

        var html = "<table width=\"100%\" height=\"90%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">" +
            "<tr><td style=\"height: 30px; line-height: 30px; text-align: left; min-width: 80px; width:25%\">" +
            this.app.options.tooltip.column.nameLabel + ":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createColumnName\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\" value=\"" + columnName + "\"/></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px; text-align: left\">" + this.app.options.tooltip.column.aliasLabel + ":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createColumnAlias\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\" value=\"" + alias + "\"/></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">" + this.app.options.tooltip.column.descriptionLabel + ":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createColumnDescription\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\" value=\"" + memo + "\"/></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">" + this.app.options.tooltip.column.sortLabel + ":</td>" +
            "<td style=\"; text-align: right;\"><input type=\"text\" id=\"createColumnSort\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\" value=\"" + order + "\"/></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">" + this.app.options.tooltip.column.iconLabel + ":</td>" +
            "<td style=\"; text-align: right;\"><div id='formIconPreview'></div><div id='formChangeIconAction'></div></td></tr>" +
                //"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.options.tooltip.column.iconLabel+":</td>" +
                //"<td style=\"; text-align: right;\"><div " +
                //"style=\"height:72px; width:72px;background:url(/x_component_cms_Column/$Main/default/icon/column.png) center center no-repeat \"></div></td></tr>" +
                //"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.options.tooltip.iconLabel+":</td>" +
                //"<td style=\"; text-align: right;\"><input type=\"text\" id=\"createColumnType\" " +
                //"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
                //"height: 26px;\"/></td></tr>" +
            "</table>";
        this.columnCreateFormNode.set("html", html);

        this.columnCancelActionNode = new Element("div", {
            "styles": this.app.css.columnCreateCancelActionNode,
            "text": this.app.options.tooltip.column.cancel
        }).inject(this.columnCreateFormNode);
        this.columnCreateOkActionNode = new Element("div", {
            "styles": this.app.css.columnCreateOkActionNode,
            "text": this.app.options.tooltip.column.ok
        }).inject(this.columnCreateFormNode);

        this.columnCancelActionNode.addEvent("click", function (e) {
            this.cancelCreateColumn(e);
        }.bind(this));
        this.columnCreateOkActionNode.addEvent("click", function (e) {
            this.okCreateColumn(e);
        }.bind(this));

        this.iconPreviewNode = this.columnCreateFormNode.getElement("div#formIconPreview");
        this.iconActionNode = this.columnCreateFormNode.getElement("div#formChangeIconAction");
        this.iconPreviewNode.setStyles({
            "height": "72px",
            "width": "72px",
            "float": "left"
        });
        if (!this.isNew && this.data.appIcon) {
            this.iconPreviewNode.setStyle("background", "url(data:image/png;base64," + this.data.appIcon + ") center center no-repeat");
        } else {
            this.iconPreviewNode.setStyle("background", "url(" + "/x_component_cms_Column/$Main/default/icon/column.png) center center no-repeat")
        }
        var changeIconAction = new Element("div", {
            "styles": {
                "margin-left": "20px",
                "float": "left",
                "background-color": "#FFF",
                "padding": "4px 14px",
                "border": "1px solid #999",
                "border-radius": "3px",
                "margin-top": "10px",
                "font-size": "14px",
                "color": "#666",
                "cursor": "pointer"
            },
            "text": "更改图标"
        }).inject(this.iconActionNode);
        changeIconAction.addEvent("click", function () {
            this.changeIcon();
        }.bind(this));
    },

    setColumnCreateNodeSize: function () {
        var size = this.createContainer.getSize();
        var allSize = this.app.content.getSize();
        this.columnCreateMarkNode.setStyles({
            "width": "" + allSize.x + "px",
            "height": "" + allSize.y + "px"
        });
        this.columnCreateAreaNode.setStyles({
            "width": "" + size.x + "px",
            "height": "" + size.y + "px"
        });
        var hY = size.y * 0.8;
        var mY = size.y * 0.2 / 2;
        this.columnCreateNode.setStyles({
            "height": "" + hY + "px",
            "margin-top": "" + mY + "px"
        });

        var iconSize = this.columnCreateNewNode.getSize();
        var formHeight = hY * 0.7;
        if (formHeight > 250) formHeight = 250;
        var formMargin = hY * 0.3 / 2 - iconSize.y;
        this.columnCreateFormNode.setStyles({
            "height": "" + formHeight + "px",
            "margin-top": "" + formMargin + "px"
        });
    },
    cancelCreateColumn: function (e) {
        if (this.isNew) {
            this.cancelNewColumn(e)
        } else {
            this.cancelEditColumn(e)
        }
    },
    cancelNewColumn: function (e) {
        var _self = this;
        if ($("createColumnName").get("value") || $("createColumnAlias").get("value") || $("createColumnDescription").get("value")) {
            this.app.confirm("warn", e, this.app.options.tooltip.column.create_cancel_title,
                this.app.options.tooltip.column.create_cancel, "320px", "100px", function () {
                    _self.columnCreateMarkNode.destroy();
                    _self.columnCreateAreaNode.destroy();
                    this.close();
                }, function () {
                    this.close();
                });
        } else {
            this.columnCreateMarkNode.destroy();
            this.columnCreateAreaNode.destroy();
        }
    },
    cancelEditColumn: function (e) {
        this.columnCreateMarkNode.destroy();
        this.columnCreateAreaNode.destroy();
    },
    okCreateColumn: function (e) {
        var data = {
            "id": (this.data && this.data.id) ? this.data.id : this.app.restActions.getUUID(),
            "isNewColumn": this.isNew,
            "appName": $("createColumnName").get("value"),
            "appAlias": $("createColumnAlias").get("value"),
            "description": $("createColumnDescription").get("value"),
            "appInfoSeq": $("createColumnSort").get("value")
        };
        if (data.appName) {

            var callback = function () {
                this.app.restActions.getColumn(data, function (json) {

                    //保存当前用户为管理员
                    if (this.isNew) {
                        var controllerData = {
                            "objectType": "APPINFO",
                            "objectId": data.id,
                            "adminUid": layout.desktop.session.user.name,
                            "adminName": layout.desktop.session.user.name,
                            "adminLevel": "ADMIN"
                        }
                        this.app.restActions.addController(controllerData);
                    }

                    if (this.app.noElementNode)this.app.noElementNode.destroy();

                    var column = new MWF.xApplication.cms.Column.Column(this.app, json.data, {"where": "top"});
                    column.load();
                    this.app.columns.push(column);
                }.bind(this));
            }.bind(this)

            this.app.notice(this.isNew ? this.app.options.tooltip.column.createColumnSuccess : this.app.options.tooltip.column.updateColumnSuccess, "success");
            this.app.restActions.saveColumn(data, function (json) {
                if (json.type == "error") {
                    this.app.notice(json.userMessage, "error");
                } else {
                    this.columnCreateMarkNode.destroy();
                    this.columnCreateAreaNode.destroy();
                    if (!this.isNew)this.node.destroy();
                    if (this.formData) {
                        this.saveIcon(data.id, callback);
                    } else {
                        callback();
                    }
                }
                //    this.app.processConfig();
            }.bind(this), function( errorObj ){
                var error = JSON.parse( errorObj.responseText );
                this.app.notice( error.message || json.userMessage, "error" );
            }.bind(this));
        } else {
            $("createColumnName").setStyle("border-color", "red");
            $("createColumnName").focus();
            this.app.notice(this.app.options.tooltip.column.inputName, "error");
        }
    },
    changeIcon: function () {
        if (!this.uploadFileAreaNode) {
            this.uploadFileAreaNode = new Element("div");
            var html = "<input name=\"file\" type=\"file\"/>";
            this.uploadFileAreaNode.set("html", html);

            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", function () {

                var files = fileNode.files;
                if (files.length) {
                    for (var i = 0; i < files.length; i++) {
                        var file = files.item(i);
                        if (!file.type.match('image.*'))continue;

                        this.file = file;
                        this.formData = new FormData();
                        this.formData.append('file', this.file);

                        if (!window.FileReader) continue;
                        var reader = new FileReader();
                        reader.onload = (function (theFile) {
                            return function (e) {
                                this.iconPreviewNode.setStyle("background", "");
                                this.iconPreviewNode.empty();
                                new Element("img", {
                                    "styles": {
                                        "height": "72px",
                                        "width": "72px"
                                    },
                                    "src": e.target.result
                                }).inject(this.iconPreviewNode);
                            }.bind(this);
                        }.bind(this))(file);
                        reader.readAsDataURL(file);
                    }
                }

            }.bind(this));
        }
        var fileNode = this.uploadFileAreaNode.getFirst();
        fileNode.click();
    },
    saveIcon: function (id, callback) {
        this.app.restActions.updataColumnIcon(id, function () {
            if (callback)callback();
            //this.app.restActions.getColumnIcon(this.data.id, function(json){
            //	if (json.data){
            //		this.data = json.data;
            //		if (this.data.icon){
            //			this.iconPreviewNode.setStyle("background", "url(data:image/png;base64,"+this.data.icon+") center center no-repeat");
            //		}else{
            //			this.iconPreviewNode.setStyle("background", "url("+"/x_component_cms_Column/$Main/default/icon/category2.png) center center no-repeat")
            //		}
            //	}
            //}.bind(this), false)
        }.bind(this), null, this.formData, this.file);
    }

})
