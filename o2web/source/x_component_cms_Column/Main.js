MWF.xApplication.cms = MWF.xApplication.cms || {};
//MWF.xApplication.cms.Column = MWF.xApplication.cms.Column || {};
//MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.cms.Column.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "cms.Column",
        "icon": "icon.png",
        "width": "1000",
        "height": "600",
        "isResize": true,
        "isMax": true,
        "title": MWF.xApplication.cms.Column.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.cms.Column.LP;
        this.defaultColumnIcon = "../x_component_cms_Column/$Main/" + this.options.style + "/icon/column.png";
        this.defaultCategoryIcon = "../x_component_cms_Column/$Main/" + this.options.style + "/icon/category2.png";
    },
    loadApplication: function (callback) {
        this.isAdmin = MWF.AC.isCMSManager();
        if (!this.restActions) this.restActions = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
        this.columns = [];
        this.categorys = [];
        this.deleteElements = [];
        this.createNode();
        this.loadApplicationContent();
        if (callback) callback();
    },
    createNode: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": this.css.container
        }).inject(this.content);
        this.node.loadCss("../x_component_cms_Column/$Main/"+ this.options.style +"/style.css");
    },
    reload: function(appType, callback){
        // if( appType ){
            this.reloadTop(appType, callback);
        // }else{
        //     this.reloadTop();
            // this.columnContentAreaNode.empty();
            // this.createColumnNodes(callback);
        // }
    },
    loadApplicationContent: function () {
        //this.columnAreaNode = new Element("div", {
        //    "styles": this.css.columnAreaNode
        //}).inject(this.node);

        this.loadTopNode();


        //this.setColumnAreaSize();
        //this.addEvent("resize", this.setColumnAreaSize);

        this.loadColumnContentArea();

        this.setCurrentAppType( "all", this.columnAllTypeNode );

        //this.setColumnContentSize();
        this.setContentSize();

        this.addEvent("resize", function(){
            this.setContentSize();
        }.bind(this));
    },
    reloadTop : function( appType, callback, noRefreshContent ){
        this.columnToolbarAreaNode.empty();
        if( !appType )appType = this.currentAppType;
        this.currentAppType = null;
        this.currentAppTypeNode = null;
        this.loadTopNode( appType, callback, noRefreshContent );
    },
    loadTopNode: function( appType, callback, noRefreshContent ){
        if( !this.columnToolbarAreaNode ){
            this.columnToolbarAreaNode = new Element("div.columnToolbarAreaNode", {
                "styles": this.css.columnToolbarAreaNode
            }).inject(this.node);
        }

        this.columnAllTypeNode =  new Element("div.columnTop_All",{
            "styles" : this.css.columnTop_All,
            "text" : this.lp.allApp
        }).inject( this.columnToolbarAreaNode );
        this.columnAllTypeNode.addEvents({
            "mouseover" : function(){
                if( this.currentAppTypeNode !== this.columnAllTypeNode )this.columnAllTypeNode.setStyles( this.css.columnTop_All_over );
            }.bind(this),
            "mouseout" : function(){
                if( this.currentAppTypeNode !== this.columnAllTypeNode )this.columnAllTypeNode.setStyles( this.css.columnTop_All );
            }.bind(this),
            "click": function () {
                this.setCurrentAppType( "all", this.columnAllTypeNode );
            }.bind(this)
        });
        if( appType && appType === "all" ){
            this.setCurrentAppType( "all", this.columnAllTypeNode, callback, noRefreshContent );
        }


        if (MWF.AC.isCMSCreator()) {
            this.createColumnNode = new Element("div.createColumnNode", {
                "styles": this.css.createColumnNode,
                "text": this.lp.column.create
            }).addClass("o2_cms_column_createColumnNode").inject(this.columnToolbarAreaNode);
            this.createColumnNode.addEvents({
                "mouseover" : function(){
                    this.createColumnNode.setStyles( this.css.createColumnNode_over );
                }.bind(this),
                "mouseout" : function(){
                    this.createColumnNode.setStyles( this.css.createColumnNode );
                }.bind(this),
                "click": function () {
                    this.createColumn();
                }.bind(this)
            });

            this.searchNode = new Element("div.searchNode", {
                "styles": this.css.columnTop_search
            }).inject(this.columnToolbarAreaNode);

            this.searchInput = new Element("input.searchInput", {
                "styles": this.css.columnTop_searchInput,
                "placeholder": this.lp.searchAppPlacholder,
                "title": this.lp.searchAppTitle
            }).inject(this.searchNode);

            this.searchButton = new Element("i.o2icon-search", {
                "styles": this.css.columnTop_searchButton
            }).inject(this.searchNode);

            this.searchInput.addEvents({
                focus: function(){
                    this.searchNode.addClass("mainColor_border");
                    this.searchButton.addClass("mainColor_color");
                }.bind(this),
                blur: function () {
                    this.searchNode.removeClass("mainColor_border");
                    this.searchButton.removeClass("mainColor_color");
                }.bind(this),
                keydown: function (e) {
                    if( (e.keyCode || e.code) === 13 ){
                        this.searchApp();
                    }
                }.bind(this)
            });

            this.searchButton.addEvent("click", function (e) {
                this.searchApp();
            }.bind(this));

            this.findNode = new Element("div.createColumnNode", {
                "styles": this.css.findNode,
                "text": this.lp.column.findDesigner
            }).inject(this.columnToolbarAreaNode);
            this.findNode.addEvent("click", function(){
                var options = {
                    "filter": {
                        "moduleList": ["cms"]
                    }
                };
                layout.openApplication(null, "FindDesigner", options);
            }.bind(this));
        }

        this.columnTypeListContaienr = new Element("div.columnTop_category", {
            "styles": this.css.columnTop_category
        }).inject(this.columnToolbarAreaNode);

        this.loadAppType( appType, callback, noRefreshContent );


        //this.columnToolbarTextNode = new Element("div.columnToolbarTextNode", {
        //    "styles": this.css.columnToolbarTextNode,
        //    "text": this.lp.column.title
        //}).inject(this.columnToolbarAreaNode);
    },
    searchApp: function(){
        var key = this.searchInput && this.searchInput.get("value");
        this.columns.each(function (app) {
            if( !key || app.data.id.contains(key) || app.data.appName.contains(key) || app.data.appAlias.contains(key)){
                app.node.show();
            }else{
                app.node.hide();
            }
        }.bind(this));
    },
    loadAppType : function( appType, callback, noRefreshContent ){
        var _self = this;
        this.restActions.listAllAppTypeByManager( function( json ){
            (json.data || []).each( function( typeObject ){
                var cNode = new Element( "div.columnTop_category", {
                    "styles" : this.css.columnTop_categoryItem,
                    "text" : typeObject.appType + "(" + typeObject.count + ")",
                    "events" : {
                        "mouseover" : function( ev ){
                            if( this.currentAppTypeNode !== ev.target )ev.target.setStyles( this.css.columnTop_categoryItem_over );
                        }.bind(this),
                        "mouseout" : function( ev ){
                            if( this.currentAppTypeNode !== ev.target )ev.target.setStyles( this.css.columnTop_categoryItem );
                        }.bind(this),
                        "click": function ( ev ) {
                            _self.setCurrentAppType( this, ev.target );
                        }.bind( typeObject.appType )
                    }
                }).inject( this.columnTypeListContaienr )
                if( appType && appType === typeObject.appType ){
                    _self.setCurrentAppType( this, cNode, callback, noRefreshContent );
                }
            }.bind(this))
            if (this.columnTypeListContaienr.getScrollSize().y>this.columnTypeListContaienr.getSize().y) this.createTypeExpandButton();
        }.bind(this))
    },
    createTypeExpandButton : function(){
        this.columnTypeExpandNode =  new Element("div.columnTop_categoryExpandButton",{
            "styles" : this.css.columnTop_categoryExpandButton
        }).inject( this.columnTypeListContaienr, "before" );
        this.columnTypeExpandNode.addEvent("click", this.expandOrCollapseCategory.bind(this));
    },
    expandOrCollapseCategory : function(e){
        if (!this.categoryMorph) this.categoryMorph = new Fx.Morph(this.columnTypeListContaienr, {"duration": 100});
        if( !this.expand ){
            this.columnTypeListContaienr.setStyles( this.css.columnTop_category_more );
            this.categoryMorph.start({"height": ""+this.columnTypeListContaienr.getScrollSize().y+"px"});

            this.expandOrCollapseCategoryFun = this.expandOrCollapseCategory.bind(this);
            this.content.addEvent("click", this.expandOrCollapseCategoryFun);
            this.expand = true;
        }else{
            this.columnTypeListContaienr.setStyles( this.css.columnTop_category );
            this.categoryMorph.start({"height": ""+this.columnToolbarAreaNode.getSize().y+"px"});
            if (this.expandOrCollapseCategoryFun) this.content.removeEvent("click", this.expandOrCollapseCategoryFun);
            this.expand = false;
        }
        e.stopPropagation();
    },
    setCurrentAppType : function( appType, target, callback, noRefreshContent ){
        if( this.currentAppType ){
            if( this.currentAppType === "all" ){
                this.currentAppTypeNode.setStyles( this.css.columnTop_All );
                this.currentAppTypeNode.removeClass("o2_cms_column_all_current");
            }else{
                this.currentAppTypeNode.setStyles( this.css.columnTop_categoryItem );
                this.currentAppTypeNode.removeClass("o2_cms_column_categoryItem_current");
            }
        }
        if( appType === "all" ){
            target.setStyles( this.css.columnTop_All_current );
            target.addClass("o2_cms_column_all_current");
        }else{
            target.setStyles( this.css.columnTop_categoryItem_current );
            target.addClass("o2_cms_column_categoryItem_current");
        }
        this.currentAppType = appType;
        this.currentAppTypeNode = target;

        if(!noRefreshContent){
            this.createColumnNodes( callback );
        }
    },
    setContentSize: function(){
        var nodeSize = this.node.getSize();
        var titlebarSize = this.columnToolbarAreaNode ? this.columnToolbarAreaNode.getSize() : {"x":0,"y":0};

        this.scrollNode.setStyle("height", ""+(nodeSize.y-titlebarSize.y)+"px");

        if (this.contentWarpNode){
            var count = (nodeSize.x/287).toInt();
            var x = 287 * count;
            var m = (nodeSize.x-x)/2-10;
            this.contentWarpNode.setStyles({
                "width": ""+x+"px",
                "margin-left": ""+m+"px"
            });
            //this.titleBar.setStyles({
            //    "margin-left": ""+(m+10)+"px",
            //    "margin-right": ""+(m+10)+"px"
            //})
        }

        if( this.columnTypeListContaienr ){
            if ( this.columnTypeListContaienr.getScrollSize().y> Math.round(this.columnTypeListContaienr.getSize().y)) {
                if( !this.columnTypeExpandNode ){
                    this.createTypeExpandButton();
                }else{
                    this.columnTypeExpandNode.setStyle("display","")
                }
            }else{
                if(this.columnTypeExpandNode)this.columnTypeExpandNode.setStyle("display","none");
            }
        }
    },
    //setColumnAreaSize: function () {
    //    var nodeSize = this.node.getSize();
    //    var toolbarSize = this.columnToolbarAreaNode.getSize();
    //    var y = nodeSize.y - toolbarSize.y;
    //
    //    this.columnAreaNode.setStyle("height", "" + y + "px");
    //
    //    if (this.columnContentAreaNode) {
    //        var count = (nodeSize.x / 282).toInt();
    //        var x = 282 * count;
    //        var m = (nodeSize.x - x) / 2 - 10;
    //        this.columnContentAreaNode.setStyles({
    //            //"width": ""+x+"px",
    //            "margin-left": "" + m + "px"
    //        });
    //    }
    //},
    //setColumnContentSize: function () {
    //    var nodeSize = this.node.getSize();
    //    if (this.columnContentAreaNode) {
    //        var count = (nodeSize.x / 282).toInt();
    //        var x = 282 * count;
    //        var m = (nodeSize.x - x) / 2 - 10;
    //        this.columnContentAreaNode.setStyles({
    //            //"width": ""+x+"px",
    //            "margin-left": "" + m + "px"
    //        });
    //    }
    //},
    loadColumnContentArea: function () {

        this.scrollNode = new Element("div", {
            "styles": this.css.scrollNode
        }).inject(this.node);
        this.contentWarpNode = new Element("div", {
            "styles": this.css.contentWarpNode
        }).inject(this.scrollNode);

        this.contentContainerNode = new Element("div",{
            "styles" : this.css.contentContainerNode
        }).inject(this.contentWarpNode);

        this.columnContentAreaNode = new Element("div", {
            "styles": this.css.columnContentAreaNode
        }).inject(this.contentContainerNode);

        //this.loadController(function () {

        //}.bind(this));

        //MWF.require("MWF.widget.DragScroll", function(){
        //	new MWF.widget.DragScroll(this.columnContentAreaNode);
        //}.bind(this));
        //MWF.require("MWF.widget.ScrollBar", function () {
        //    new MWF.widget.ScrollBar(this.columnContentAreaNode);
        //}.bind(this));
    },
    //loadController: function (callback) {
        //this.availableApp = [];
        //this.restActions.listAppByManager( function( json ){
        //    (json.data||[]).each(function (d) {
        //        this.availableApp.push(d.id);
        //    }.bind(this));
        //    if (callback)callback();
        //}.bind(this), null, true );
        //this.restActions.listControllerByPerson(layout.desktop.session.user.distinguishedName, function (json) {
        //    if (json && json.data && json.data.length) {
        //        json.data.each(function (d) {
        //            this.availableApp.push(d.objectId);
        //        }.bind(this))
        //    }
        //    if (callback)callback();
        //}.bind(this), null, true)
    //},
    //hasPermision: function (appId) {
    //    return this.isAdmin || this.availableApp.contains(appId);
    //},
    createColumnNodes: function ( callback ) {
        this.columnContentAreaNode.empty();
        if( this.currentAppType === "all" ){
            this.restActions.listAppByManager(function (json){
                this._createColumnNodes( json );
                if(callback)callback();
            }.bind(this));
        }else{
            this.restActions.listWhatICanManageWithAppType(this.currentAppType, function (json){
                this._createColumnNodes( json );
                if(callback)callback();
            }.bind(this))
        }

    },
    _createColumnNodes : function( json ){
        var emptyColumn = null;
        if (json && json.data && json.data.length) {
            var tmpArr = json.data;
            tmpArr.sort(function(a , b ){
                return parseFloat( a.appInfoSeq ) - parseFloat(b.appInfoSeq);
            });
            json.data = tmpArr;
            json.data.each(function (column, index) {
                ///if (this.hasPermision(column.id)) {
                this.index = index;
                var column = new MWF.xApplication.cms.Column.Column(this, column, {index : index});
                column.load();
                this.columns.push(column);
                //}
            }.bind(this));
        }

        if (this.columns.length == 0) {
            this.noElementNode = new Element("div", {
                "styles": this.css.noElementNode,
                "text": this.lp.column.noElement
            }).inject(this.columnContentAreaNode);
        }
    },
    createColumn: function () {
        //var column = new MWF.xApplication.cms.Column.Column(this, null, { index: ++this.index });
        //column.createColumn(this.node);
        var form = new MWF.xApplication.cms.Column.PopupForm(this, {}, {
            title : this.lp.column.create
        }, {
            app : this,
            container :  this.content,
            lp : this.lp.column,
            css : {},
            actions : this.restActions
        });
        form.create();
    }
    /*
     createLoadding: function(){
     this.loaddingNode = new Element("div", {
     "styles": this.css.noApplicationNode,
     "text": this.lp.loadding
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
        "where": "bottom",
        "index" : 1
    },

    initialize: function (app, data, options) {
        this.setOptions(options);
        this.app = app;
        this.container = this.app.columnContentAreaNode;
        this.data = data;
        this.isNew = false;
        this.lp = this.app.lp.column;
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

        var topNode = new Element("div", {
            "styles": this.app.css.columnItemTopNode
        }).inject(itemNode);
        if( this.data.iconColor ){
            topNode.setStyle("background-color" , "rgba("+ this.data.iconColor +",1)" )
        }

        var titleNode = new Element("div", {
            "styles": this.app.css.columnItemTitleNode,
            "text": columnName,
            "title": (alias) ? columnName + " (" + alias + ") " : columnName
        }).inject(topNode);
        this.titleNode = titleNode;

        var iconAreaNode = new Element("div",{
            "styles": this.app.css.columnItemIconAreaNode
        }).inject(itemNode);
        if( this.data.iconColor ){
            iconAreaNode.setStyle("border-color" , "rgba("+ this.data.iconColor +",1)" )
        }

        var iconNode = this.iconNode = new Element("div", {
            "styles": this.app.css.columnItemIconNode
        }).inject(itemNode);
        //iconNode.setStyles({
        //	"background-image" : "url("+icon+")"
        //});
        if (this.data.appIcon) {
            this.iconNode.setStyle("background-image", "url(data:image/png;base64," + this.data.appIcon + ")");
        } else {
            this.iconNode.setStyle("background-image", "url(" + this.app.defaultColumnIcon + ")");
        }
        this.iconNode.makeLnk({
            "par": this._getLnkPar()
        });

        var middleNode = new Element("div", {
            "styles": this.app.css.columnItemMiddleNode
        }).inject(itemNode);



        var description = ( memo && memo != "") ? memo : this.lp.noDescription;
        var descriptionNode = new Element("div", {
            "styles": this.app.css.columnItemDescriptionNode,
            "text": description,
            "title": description
        }).inject(middleNode);

        var _self = this;
        itemNode.addEvents({
            "mouseover": function () {
                if (!_self.selected){
                    this.setStyles(_self.app.css.columnItemNode_over);
                    this.addClass("o2_cms_column_columnNode_over");
                }
            },
            "mouseout": function () {
                if (!_self.selected){
                    this.setStyles(_self.app.css.columnItemNode);
                    this.removeClass("o2_cms_column_columnNode_over");
                }
            },
            "click": function (e) {
                _self.clickColumnNode(_self, this, e)
            }
        });

        var bottomNode = new Element("div", {
            "styles": this.app.css.columnItemBottomNode
        }).inject(itemNode);

        var bottomTitleNode = new Element("div", {
            "styles": this.app.css.columnItemCategoryTitleNode,
            "text" : this.lp.category
        }).inject(bottomNode);
        var bottomContentNode_category = new Element("div", {
            "styles": this.app.css.columnItemCategoryContentNode
        }).inject(bottomNode);
        this.app.restActions.listCategory( this.data.id, function ( json ) {
            var data = json.data || [];
            data.each( function( category ){
                var bottomItemNode = new Element("div",{
                    styles : this.app.css.columnItemBottomItemNode,
                    text : category.name
                }).inject(bottomContentNode_category);
                bottomItemNode.addEvents( {
                    "click": function( ev ){
                        this.obj.clickColumnNode(this.obj, ev.target, ev, this.data.id);
                        ev.stopPropagation();
                    }.bind({ obj : this, data : category }),
                    "mouseover" : function(){
                        this.node.setStyles( this.obj.app.css.columnItemBottomItemNode_over );
                    }.bind({ obj : this, node : bottomItemNode }),
                    "mouseout" : function(){
                        this.node.setStyles( this.obj.app.css.columnItemBottomItemNode );
                    }.bind({ obj : this, node : bottomItemNode })
                })
            }.bind(this))
        }.bind(this) );

        var bottomTitleNode = new Element("div", {
            "styles": this.app.css.columnItemFormTitleNode,
            "text" : this.lp.form
        }).inject(bottomNode);
        var bottomContentNode_form = new Element("div", {
            "styles": this.app.css.columnItemFormContentNode
        }).inject(bottomNode);
        this.app.restActions.listForm( this.data.id, function ( json ) {
            var data = json.data || [];
            data.each( function( form ){
                var bottomItemNode = new Element("div",{
                    styles : this.app.css.columnItemBottomItemNode,
                    text : form.name
                }).inject(bottomContentNode_form);
                bottomItemNode.addEvents( {
                    "click": function( ev ){
                        this.obj.openForm( this.data );
                        ev.stopPropagation();
                    }.bind({ obj : this, data : form }),
                    "mouseover" : function(){
                        this.node.setStyles( this.obj.app.css.columnItemBottomItemNode_over );
                    }.bind({ obj : this, node : bottomItemNode }),
                    "mouseout" : function(){
                        this.node.setStyles( this.obj.app.css.columnItemBottomItemNode );
                    }.bind({ obj : this, node : bottomItemNode })
                })
            }.bind(this))
        }.bind(this) );


        if ((creator == layout.desktop.session.user.distinguishedName) || MWF.AC.isCMSManager()) {
            this.delAdctionNode = new Element("div.delNode", {
                "styles": this.app.css.columnItemDelActionNode,
                "title": this.lp["delete"]
            }).inject(itemNode);

            itemNode.addEvents({
                "mouseover": function () {
                    this.delAdctionNode.setStyle("display","");
                }.bind(this),
                "mouseout": function () {
                    this.delAdctionNode.setStyle("display","none");
                }.bind(this)
            });
            this.delAdctionNode.addEvent("click", function (e) {
                this.deleteColumn(e);
                e.stopPropagation();
            }.bind(this));
        }

        if ((creator == layout.desktop.session.user.distinguishedName) || MWF.AC.isCMSManager()) {
            this.editAdctionNode = new Element("div.editNode", {
                "styles": this.app.css.columnItemEditActionNode,
                "title": this.lp.edit
            }).inject(itemNode);

            itemNode.addEvents({
                "mouseover": function () {
                    this.editAdctionNode.setStyle("display","");
                }.bind(this),
                "mouseout": function () {
                    this.editAdctionNode.setStyle("display","none");
                }.bind(this)
            });
            this.editAdctionNode.addEvent("click", function (e) {
                this.edit(e);
                e.stopPropagation();
            }.bind(this));
        }

        //if ((creator == layout.desktop.session.user.distinguishedName) || MWF.AC.isCMSManager()) {
        //    this.exportAdctionNode = new Element("div.exportNode", {
        //        "styles": this.app.css.columnItemExportActionNode,
        //        "title": this.lp.export
        //    }).inject(itemNode);
        //
        //    itemNode.addEvents({
        //        "mouseover": function () {
        //            this.exportAdctionNode.setStyle("display","");
        //        }.bind(this),
        //        "mouseout": function () {
        //            this.exportAdctionNode.setStyle("display","none");
        //        }.bind(this)
        //    });
        //    this.exportAdctionNode.addEvent("click", function (e) {
        //        this.export(e);
        //        e.stopPropagation();
        //    }.bind(this));
        //}
    },
    _getLnkPar: function(){
        var lnkIcon = this.app.defaultColumnIcon;
        if (this.data.icon) lnkIcon = "data:image/png;base64," + this.data.appIcon;
        var appId = "cms.ColumnManager"+this.data.id;
        return {
            "icon": lnkIcon,
            "title": this.data.appName,
            "par": "cms.ColumnManager#{\"column\": \""+this.data.id+"\", \"appId\": \""+appId+"\"}"
        };
    },
    export: function(){
        //var applicationjson = {
        //    "application": {},
        //    "processList": [],
        //    "formList": [],
        //    "dictionaryList": [],
        //    "scriptList": []
        //};
        //this.app.restActions.getApplication(this.data.name, function(json){
        //
        //}
        MWF.xDesktop.requireApp("cms.Column", "Exporter", function(){
            (new MWF.xApplication.cms.Column.Exporter(this.app, this.data)).load();
        }.bind(this));
    },
    edit : function(){
        var form = new MWF.xApplication.cms.Column.PopupForm(this.app, this.data, {
            title : this.lp.edit
        }, {
            app : this.app,
            container :  this.app.content,
            lp : this.lp,
            css : {},
            actions : this.app.restActions
        });
        form.edit();
    },
    openForm: function( form ){
        layout.desktop.getFormDesignerStyle(function(){
            var _self = this;
            var options = {
                "style": layout.desktop.formDesignerStyle,
                "onQueryLoad": function(){
                    //this.actions = _self.explorer.actions;
                    this.category = _self;
                    this.options.id = form.id;
                    this.column = _self.data;
                    this.application = _self.data;
                }
            };
            this.app.desktop.openApplication(null, "cms.FormDesigner", options);
        }.bind(this));
    },
    clickColumnNode: function (_self, el, e, currentCategoryId) {
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
            var app = this.app.desktop.apps[appId];
            if( app && app.setCurrent )app.setCurrent();
            if( currentCategoryId ){
                if( app && app.setCategory ){
                    app.setCategory( currentCategoryId );
                }
            }
        } else {
            this.app.desktop.openApplication(e, "cms.ColumnManager", {
                "currentCategoryId" : currentCategoryId,
                "column": {"id": this.data.id, "appName": this.data.appName, "appIcon": this.data.appIcon},
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
                    "text": this.lp.deleteElements
                }).inject(this.node);
                this.deleteElementsNode.position({
                    relativeTo: this.container,
                    position: "centerTop",
                    edge: "centerbottom"
                });
                this.deleteElementsNode.addEvent("click", function (e) {
                    this["delete"]();
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
        this.app.confirm("warn", e, this.lp.delete_confirm_title,
            this.lp.delete_confirm_content, 320, 100, function () {
                _self._deleteElement();
                this.close();
            }, function( ) {
                this.close();
            }
        )
    },
    _deleteElement: function (id, success, failure) {
        this.app.restActions.removeColumn( id || this.data.id, function () {
            this.app.reloadTop(null, null, true);
            this.destroy();
            if (success) success();
        }.bind(this), function( error ){
            var errorObj = JSON.parse( error.responseText );
            this.app.notice(errorObj.message , "error");
            if(failure)failure();
        }.bind(this));
    },
    destroy: function () {
        this.node.destroy();
        MWF.release(this);
        delete this;
    }

});

MWF.xApplication.cms.Column.PopupForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "blue",
        "width": "650",
        "height": "630",
        "hasTop": true,
        "hasIcon": false,
        "hasTopContent" : true,
        "hasBottom": true,
        //"title": MWF.xApplication.cms.Index.LP.createDocument,
        "draggable": true,
        "closeAction": true
    },
    _createTableContent: function () {
        MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
        if (this.isNew) {
            var columnName = "";
            var alias = "";
            var memo = "";
            var order = "";
            var creator = "";
            var icon = "";
            var createTime = "";
            var type = "";
            var editform = "";
            var readform = "";
        } else {
            var columnName = this.data.appName;
            var alias = this.data.appAlias || "";
            var memo = this.data.description;
            var order = this.data.appInfoSeq;
            var creator = this.data.creatorUid;
            var createTime = this.data.createTime;
            var type = this.data.appType || "";
            var editform = this.data.defaultEditForm || "";
            var readform = this.data.defaultReadForm || "";
        }

        var inputStyle = "width: 96%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; height: 26px";
        var titleStyle = "font-size:16px; height: 40px; line-height: 40px;  text-align: left";
        var contentStyle = "text-align: left;"
        var clearStyle = "position:absolute;cursor:pointer;width:26px;height:26px;right:10px;top:1px;background:url(../x_component_Template/$MPopupForm/report/icon/icon_off.png) center center no-repeat"
        var inputFormStyle = "width: calc( 96% - 20px);  border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; height: 26px; padding-right:20px;";
        var html = "<table width='100%' height='90%' border='0' cellPadding='0' cellSpacing='0'>" +
            "<tr>" +
            "   <td style='font-size:16px; height: 40px; line-height: 40px; text-align: left; min-width: 80px; width:26%'>" + this.lp.nameLabel + "：</td>" +
            "   <td style='"+contentStyle+"'>" +
            "       <input type='text' id='createColumnName' style='"+inputStyle+"' value='" + columnName + "'/>" +
            "   </td>" +
            "</tr>" +
            "<tr>" +
            "   <td style='"+titleStyle+"'>" + this.lp.aliasLabel + "：</td>" +
            "   <td style='"+contentStyle+"'>" +
            "       <input type='text' id='createColumnAlias' style='"+inputStyle+"' value='" + alias + "'/>" +
            "   </td>" +
            "</tr>" +
            "<tr>" +
            "   <td style='"+titleStyle+"'>" + this.lp.descriptionLabel + "：</td>" +
            "   <td style='"+contentStyle+"'>" +
            "       <input type='text' id='createColumnDescription' style='"+inputStyle+"' value='" + memo + "'/>" +
            "   </td>" +
            "</tr>" +
            "<tr>" +
            "   <td style='"+titleStyle+"'>" + this.lp.sortLabel + "：</td>" +
            "   <td style='"+contentStyle+"'>" +
            "       <input type='text' id='createColumnSort' style='"+inputStyle+"' value='" + order + "'/>" +
            "   </td>" +
            "</tr>" +
            "<tr>" +
            "   <td style='"+titleStyle+"'>" + this.lp.typeLabel + "：</td>" +
            "   <td style='"+contentStyle+"'>" +
            "       <input type='text' id='createColumnType' style='"+inputStyle+"' value='" + type + "'/>" +
            "   </td>" +
            "</tr>" +
            "<tr>" +
            "   <td style='"+titleStyle+"'>" + this.lp.editform + "：</td>" +
            "   <td style='"+contentStyle+"'>" +
            ( this.isNew ?
             "       <div style='position: relative;'><input type='text' readonly id='formEditform' style='"+inputFormStyle+"' value='" + editform + "'/><div id='formClearEditform' style='"+clearStyle+"'></div></div>" :
             "       <select id='formEditform' style='"+inputStyle+"'></select>"
            ) +
            "       <div style='text-align: left;padding-left: 2.5%;font-size: 14px;color:#999;margin-bottom: 5px;'>"+this.lp.editformNote +"</div>"+
            "   </td>" +
            "</tr>" +
            "<tr>" +
            "   <td style='"+titleStyle+"'>" + this.lp.readform + "：</td>" +
            "   <td style='"+contentStyle+"'>" +
                ( this.isNew ?
            "       <div style='position: relative;'><input type='text' readonly id='formReadform' style='"+inputFormStyle+"' value='" + readform + "'/><div id='formClearReadform' style='"+clearStyle+"'></div></div>" :
            "       <select id='formReadform' style='"+inputStyle+"'></select>"
                ) +
            "       <div style='text-align: left;padding-left: 2.5%;font-size: 14px;color:#999;margin-bottom: 5px;'>"+this.lp.readformNote +"</div>"+
            "   </td>" +
            "</tr>" +
            "<tr>" +
            "   <td style='"+titleStyle+"'>" + this.lp.iconLabel + "：</td>" +
            "   <td style='"+contentStyle+"'>" +
            "       <div id='formIconPreview'></div>" +
            "       <div id='formChangeIconAction'></div>" +
            "   </td>" +
            "</tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        this.setContent();
        this.setIconContent();
        this.setDefaultFormContent();
    },
    _setCustom: function(){
        this.formTableContainer.setStyles({
            "padding-top" : "15px",
            "width" : "470px"
        });

        this.formBottomNode.setStyles({
            "padding-right" : "170px",
            "padding-bottom" : "50px"
        });

    },
    setContent: function(){
        this.nameInput = this.formTableArea.getElementById("createColumnName");
        this.aliasInput = this.formTableArea.getElementById("createColumnAlias");
        this.descriptionInput = this.formTableArea.getElementById("createColumnDescription");
        this.sortInput = this.formTableArea.getElementById("createColumnSort");
        this.typeInput = this.formTableArea.getElementById("createColumnType");
    },
    setIconContent: function(){
        this.iconPreviewNode = this.formTableArea.getElement("div#formIconPreview");
        this.iconActionNode = this.formTableArea.getElement("div#formChangeIconAction");
        this.iconPreviewNode.setStyles({
            "margin-left" : "20px",
            "margin-top" : "10px",
            "height": "72px",
            "width": "72px",
            "float": "left"
        });
        if (!this.isNew && this.data.appIcon) {
            this.iconPreviewNode.setStyle("background", "url(data:image/png;base64," + this.data.appIcon + ") center center no-repeat");
        } else {
            this.iconPreviewNode.setStyle("background", "url(" + "../x_component_cms_Column/$Main/default/icon/column.png) center center no-repeat")
        }
        var changeIconAction = new Element("div", {
            "styles": {
                "margin-left": "20px",
                "float": "left",
                "background-color": "#FFF",
                "padding": "4px 14px",
                "border": "1px solid #999",
                "border-radius": "3px",
                "margin-top": "25px",
                "font-size": "14px",
                "color": "#666",
                "cursor": "pointer"
            },
            "text": this.lp.changeIcon
        }).inject(this.iconActionNode);
        changeIconAction.addEvent("click", function () {
            this.changeIcon();
        }.bind(this));
    },
    setDefaultFormContent: function(){
        this.formEditformInput = this.formTableArea.getElement("#formEditform");
        if( this.isNew ){
            this.formEditformInput.addEvent("click", function(){
               this.selectDefaultForm(function (object) {
                    this.editformTemplate = object;
                    this.formEditformInput.set("value", object.title);
               }.bind(this))
            }.bind(this))
        }else{
            this.createFormSelect(this.formEditformInput, "edit")
        }
        this.formReadformInput = this.formTableArea.getElement("#formReadform");
        if( this.isNew ) {
            this.formReadformInput.addEvent("click", function () {
                this.selectDefaultForm(function (object) {
                    this.readformTemplate = object;
                    this.formReadformInput.set("value", object.title);
                }.bind(this))
            }.bind(this))
        }else{
            this.createFormSelect(this.formReadformInput, "read");
        }
        if(this.isNew){
            this.listFormTemplate(function () {
                this.formTemplateList.each(function(form){
                    if(form.defaultEditForm){
                        this.editformTemplate = {"template": form.name, "title": form.title, "type":"default"};
                        this.formEditformInput.set("value", form.title);
                    }
                    if(form.defaultReadForm){
                        this.readformTemplate = {"template": form.name, "title": form.title, "type":"default"};
                        this.formReadformInput.set("value", form.title);
                    }
                }.bind(this))
            }.bind(this))
            this.formTableArea.getElement("#formClearEditform").addEvent("click", function(){
                this.editformTemplate = null;
                this.formEditformInput.set("value", "");
            }.bind(this))
            this.formTableArea.getElement("#formClearReadform").addEvent("click", function(){
                this.readformTemplate = null;
                this.formReadformInput.set("value", "");
            }.bind(this))
        }
    },
    listFormTemplate: function(callback){
        if (this.formTemplateList){
            if (callback) callback();
        }else{
            if( !MWF.xApplication.cms.ColumnManager || !MWF.xApplication.cms.ColumnManager.LP ){
                MWF.requireApp("cms.ColumnManager", "lp."+o2.language, null, false);
            }
            var url = "../x_component_cms_FormDesigner/Module/Form/template/templates.json";
            MWF.getJSON(url, function(json){
                this.formTemplateList = json;
                if (callback) callback();
            }.bind(this));
        }
    },
    createFormSelect: function(selectNode, type){
        this.listForm(function () {
            new Element("option", {
                "text": "",
                "value": "",
            }).inject(selectNode)
            this.formList.each(function (form) {
                var selected = false;
                if( type === "edit" ){
                    if( this.data.defaultEditForm === form.id )selected = true;
                }else{
                    if( this.data.defaultReadForm === form.id )selected = true;
                }
                new Element("option", {
                    "text": form.name,
                    "value": form.id,
                    "selected": selected
                }).inject(selectNode)
            }.bind(this))
        }.bind(this))
        this.addEvent("queryClose", function () {
            selectNode.destroy();
        }.bind(this))
    },
    getFormName: function(id, callback){
        this.listForm(function(){
            for( var i=0; i<this.formList.length; i++ ){
                if( this.formList[i].id === id )callback(this.formList[i].appName);
            }
            callback("")
        }.bind(this))
    },
    listForm: function( callback ){
        if( this.formList ){
            callback( this.formList );
            return;
        }
        this.app.restActions.listForm(this.data.id, function(json){
            this.formList = json.data;
            callback( this.formList );
        }.bind(this));
    },
    selectDefaultForm: function(callback){
        var _self = this;
        MWF.requireApp("cms.ColumnManager", "widget.CMSFormTemplateSelector", null, false);
        new MWF.xApplication.cms.ColumnManager.CMSFormTemplateSelector(this.app, {
            onSelectDefaultForm: function (template, title) {
                if(callback)callback({"template": template, "title": title, "type":"default"})
            },
            onSelectForm: function (template, title) {
                if(callback)callback({"template": template, "title": title})
            }
        }).load();
    },

    cancel: function (e) {
        this.fireEvent("queryCancel");
        if (this.isNew) {
            this.cancelNewColumn(e)
        } else {
            this.close();
        }
        this.fireEvent("postCancel");
    },
    cancelNewColumn: function (e) {
        var _self = this;
        if (this.nameInput.get("value") || this.descriptionInput.get("value")) {
            this.app.confirm("warn", e, this.lp.create_cancel_title,
                this.lp.create_cancel, 320, 100, function () {
                    _self.close();
                    this.close();
                }, function () {
                    this.close();
                });
        } else {
            _self.close();
        }
    },
    ok: function (e) {
        this.fireEvent("queryOk");
        var data = {
            "id": (this.data && this.data.id) ? this.data.id : this.app.restActions.getUUID(),
            "isNewColumn": this.isNew,
            "appName": this.nameInput.get("value"),
            "appAlias": this.aliasInput.get("value"),
            "alias": this.aliasInput.get("value"),
            "description": this.descriptionInput.get("value"),
            "appInfoSeq": this.sortInput.get("value"),
            "appType" : this.typeInput.get("value")
        };
        if( !this.isNew ){
            this.formEditformInput.getElements("option").each(function (option) {
                if(option.selected)data.defaultEditForm = option.value;
            })
            this.formReadformInput.getElements("option").each(function (option) {
                if(option.selected)data.defaultReadForm = option.value;
            })
        }
        if( this.data && this.data.appIcon )data.appIcon = this.data.appIcon;
        if (!data.appName) {
            this.app.notice( this.lp.inputName );
            return;
        }else{

            var callback = function ( id ) {
                this.app.restActions.getColumn( id, function (json) {
                    //保存当前用户为管理员
                    if (this.isNew) {
                        // var data = {
                        //     personList : [ layout.desktop.session.user.distinguishedName || "xadmin" ],
                        //     unitList : [],
                        //     groupList : []
                        // };
                        // this.app.restActions.saveAppInfoManager(json.data.id, data, function (js) {
                        // }.bind(this), null, false);
                    }

                    if (this.app.noElementNode)this.app.noElementNode.destroy();

                    if( this.formMaskNode )this.formMaskNode.destroy();
                    this.formAreaNode.destroy();
                    if( this.app )this.app.notice(this.isNew ? this.lp.createColumnSuccess : this.lp.updateColumnSuccess, "success");
                    if( this.isNew ){
                        var column = new MWF.xApplication.cms.Column.Column(this.app, json.data, {"where": "top"});
                        column.load();
                        this.app.columns.push(column);
                        this.app.reloadTop(null, null, true);
                    }else{
                        this.app.reload();
                    }

                    this.fireEvent("postOk");
                }.bind(this));
            }.bind(this);

            this.app.restActions.saveColumn(data, function (json) {
                if(!data)data.id = json.data.id;
                if (json.type == "error") {
                    this.app.notice(json.message, "error");
                } else {
                    this.saveForm(data, function () {
                        if (this.formData) {
                            this.saveIcon(json.data.id, callback);
                        } else {
                            callback( json.data.id );
                        }
                    }.bind(this))
                }
                //    this.app.processConfig();
            }.bind(this), function( errorObj ){
                var error = JSON.parse( errorObj.responseText );
                this.app.notice( error.message || json.userMessage, "error" );
            }.bind(this));
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
            this.formData = null;
            if (callback)callback( id );
        }.bind(this), null, this.formData, this.file);
    },
    saveForm: function ( columnData, callback ) {
        var editDone, readDone;
        var editFormId, readFormId;
        var saveColumn = function () {
            if( editFormId || readFormId ){
                columnData.defaultEditForm = editFormId;
                columnData.defaultReadForm = readFormId;
                this.app.restActions.saveColumn(columnData, function (json) {
                    callback()
                }.bind(this), function( errorObj ){
                    var error = JSON.parse( errorObj.responseText );
                    this.app.notice( error.message || json.userMessage, "error" );
                }.bind(this));
            }else{
                callback()
            }
        }.bind(this)
        var saveEditDone = function (formId) {
            editFormId = formId;
            editDone = true;
            if( editDone && readDone )saveColumn(editFormId, readFormId);
        }.bind(this)

        var saveReadDone = function (formId) {
            readFormId = formId;
            readDone = true;
            if( editDone && readDone )saveColumn(editFormId, readFormId);
        }.bind(this)

        if( this.editformTemplate ){
            if( this.editformTemplate.type === "default" ){
                this.saveNewFormData( columnData, this.editformTemplate.template, saveEditDone )
            }else{
                this.saveNewFormDataFormTemplate( columnData, this.editformTemplate.template, saveEditDone )
            }
        }else{
            saveEditDone();
        }
        if( this.readformTemplate ){
            if( this.readformTemplate.type === "default" ){
                this.saveNewFormData( columnData, this.readformTemplate.template, saveReadDone )
            }else{
                this.saveNewFormDataFormTemplate( columnData, this.readformTemplate.template, saveReadDone )
            }
        }else{
            saveReadDone();
        }
    },
    saveNewFormData: function(columnData, templateId, callback){
        var url = "../x_component_cms_FormDesigner/Module/Form/template/"+templateId;
        //MWF.getJSON("../x_component_process_FormDesigner/Module/Form/template.json", {
        MWF.getJSON(url, {
            "onSuccess": function(obj){
                if (obj) this.saveFormAsNew(columnData, obj, callback)
            }.bind(this),
            "onerror": function(text){
                this.notice(text, "error");
            }.bind(this),
            "onRequestFailure": function(xhr){
                this.notice(xhr.responseText, "error");
            }.bind(this)
        });
    },
    saveNewFormDataFormTemplate: function(columnData, templateId, callback){
        this.actions.getFormTemplate(templateId, function(form){
            if (form) this.saveFormAsNew(columnData, form, callback)
        }.bind(this));
    },
    saveFormAsNew: function(columnData, form, success, failure) {
        debugger;

        var id = columnData.id;
        var name = columnData.appName;

        var pcdata, mobiledata;
        if (form.data) {
            if (form.data.data) pcdata = JSON.decode(MWF.decodeJsonString(form.data.data));
            if (form.data.mobileData) mobiledata = JSON.decode(MWF.decodeJsonString(form.data.mobileData));
        } else {
            pcdata = form.pcData;
            mobiledata = form.mobileData;
        }

        if (pcdata){
            pcdata.id = "";
            pcdata.isNewForm = true;
            pcdata.json.id = "";
            pcdata.json.application = id;
            pcdata.json.applicationName = name;
            pcdata.json.appId = id;
            pcdata.json.appName = name;
            // pcdata.json.alias = "";
            pcdata.json.name = pcdata.json.name.replace("模板", "")
        }

        if(mobiledata){
            mobiledata.json.id = "";
            mobiledata.json.application = id;
            mobiledata.json.applicationName = name;
            mobiledata.applicationName = name;
            mobiledata.json.appId = id;
            mobiledata.json.appName = name;
            // mobiledata.json.alias = "";
            mobiledata.json.name = pcdata.json.name
        }

        this.app.restActions.saveForm(pcdata, mobiledata, form.fieldList, function(json){
            if (success) success(json.data.id);
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
    }

});
