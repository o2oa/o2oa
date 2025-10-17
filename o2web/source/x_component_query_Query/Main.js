MWF.xApplication.query.Query.options.multitask = true;
MWF.xApplication.query.Query.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "query.Query",
		"icon": "icon.png",
		"width": "1200",
		"height": "700",
		"title": MWF.xApplication.query.Query.LP.title,
        "isControl": false,
        "taskObject": null,
        "parameters": "",
        "readonly": false
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.query.Query.LP;
        if (this.status){
            this.options.id = this.status.id;
            this.options.viewId = this.status.viewId;
            this.options.statId = this.status.statId;
            this.options.statementId = this.status.statementId;
            this.options.importerId = this.status.importerId;
        }
	},
    loadApplication: function(callback){
        this.content.setStyle("background-color", "#F0F0F0");
        this.node = new Element("div", {"styles": this.css.content}).inject(this.content);
        this.formNode = new Element("div", {"styles": {"min-height": "100%", "font-size": "14px"}}).inject(this.node);
        this.action = MWF.Actions.get("x_query_assemble_surface");
        // if (!this.options.isRefresh){
        //     this.maxSize(function(){
        //         this.loadQuery(this.options.parameters);
        //     }.bind(this));
        // }else{
             this.loadQuery(this.options.parameters);
        // }
        if (callback) callback();
    },

    loadQuery: function(par){
        this.action.getQuery(this.options.id, function(json){
            this.query = json.data;
            this.setTitle(this.query.name);

            if (this.query.icon){
                if (this.taskitem){
                    this.taskitem.iconNode.setStyles({
                        "background-image": "url(data:image/png;base64,"+this.query.icon+")",
                        "background-size": "24px 24px"
                    });
                }
            }

            var lp = this.lp;
            var iData = this.query.data ? JSON.parse(this.query.data) : "";
            this.interfaceData = Object.merge({
                viewShow: "true",
                viewNumber: 1,
                viewName: lp.view,
                viewIcon: "../x_component_query_Query/$Main/"+this.options.style+"/icon/view_new.png",
                statShow: "true",
                statNumber: 2,
                statName: lp.stat,
                statIcon: "../x_component_query_Query/$Main/"+this.options.style+"/icon/stat_new.png",
                statementShow: "true",
                statementNumber: 3,
                statementName: lp.statement,
                statementIcon: "../x_component_query_Query/$Main/"+this.options.style+"/icon/statement_new.png",
                importerShow: "true",
                importerNumber: 4,
                importerName: lp.importer,
                importerIcon: "../x_component_query_Query/$Main/"+this.options.style+"/icon/importer_new.png",
            }, iData || {} );

            this.createLayout();
            this.createNavi();

            this.addEvent("resize", function(){
                if (this.currentItem){
                    if (this.currentItem.viewer && this.currentItem.viewer.setContentHeight){
                        this.currentItem.viewer.setContentHeight();
                    }
                }
            }.bind(this));
        }.bind(this));
    },
    createLayout: function(){
        if (!layout.mobile){
            this.createLayoutPC();
        }else{
            this.createLayoutMobile();
        }
    },
    createLayoutPC: function(){
        this.naviNode = new Element("div", {"styles": this.css.naviNode}).inject(this.content);
        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.content);
        this.naviTitleNode = new Element("div", {"styles": this.css.naviTitleNode}).inject(this.naviNode);
        this.naviContentNode = new Element("div", {"styles": this.css.naviContentNode}).inject(this.naviNode);

        this.createNavSearchNode();

        var lp = this.lp;

        var data = this.interfaceData;

        var object = [];
        this.naviArray = object;
        if( data.viewShow !== "false" && data.viewShow !== false ) {
            this.naviViewTitleNode = new Element("div", {"styles": this.css.naviCategoryNode });
            // this.naviViewTitleNode = new Element("div", {"styles": this.css.naviViewTitleNode, "text": data.viewName});
            this.naviViewContentNode = new Element("div", {"styles": this.css.naviViewContentNode});
            object.push({
                "type": "view",
                "index": data.viewNumber,
                "titleNode": this.naviViewTitleNode,
                "contentNode": this.naviViewContentNode
            });
        }

        if( data.statShow !== "false" && data.statShow !== false ) {
            this.naviStatTitleNode = new Element("div", {"styles": this.css.naviCategoryNode });
            // this.naviStatTitleNode = new Element("div", {"styles": this.css.naviStatTitleNode, "text": data.statName});
            this.naviStatContentNode = new Element("div", {"styles": this.css.naviStatContentNode});
            object.push({
                "type": "stat",
                "index": data.statNumber,
                "titleNode": this.naviStatTitleNode,
                "contentNode": this.naviStatContentNode
            });
        }

        if( data.statementShow !== "false" && data.statementShow !== false ) {
            this.naviStatementTitleNode = new Element("div", {"styles": this.css.naviCategoryNode });
            // this.naviStatementTitleNode = new Element("div", {"styles": this.css.naviStatementTitleNode, "text": data.statementName});
            this.naviStatementContentNode = new Element("div", {"styles": this.css.naviStatementContentNode});
            object.push({
                "type": "statement",
                "index": data.statementNumber,
                "titleNode": this.naviStatementTitleNode,
                "contentNode": this.naviStatementContentNode
            });
        }

        if( data.importerShow !== "false" && data.importerShow !== false ) {
            this.naviImporterTitleNode = new Element("div", {"styles": this.css.naviCategoryNode });
            // this.naviImporterTitleNode = new Element("div", {
            //     "styles": this.css.naviImporterTitleNode,
            //     "text": data.importerName
            // });
            this.naviImporterContentNode = new Element("div", {"styles": this.css.naviImporterContentNode});
            object.push({
                "type": "importer",
                "index": data.importerNumber,
                "titleNode": this.naviImporterTitleNode,
                "contentNode": this.naviImporterContentNode
            });
        }
        object.sort(function(a, b){
            return a.index - b.index
        });
        object.each(function(a){
            a.titleNode.inject(this.naviContentNode);
            var actionNode = new Element("div", {"styles": this.css.naviExpandNode }).inject(a.titleNode);
            var textNode = new Element("div", {"styles": this.css.naviTitleTextNode, "text": data[a.type+"Name"] }).inject(a.titleNode);
            textNode.setStyle("background-image", "url('"+ data[a.type+"Icon"] +"')");
            a.titleNode.addEvents({
                "mouseover": function () {
                    a.titleNode.setStyles(this.css.naviCategoryNode_over)
                }.bind(this),
                "mouseout": function () {
                    a.titleNode.setStyles(this.css.naviCategoryNode)
                }.bind(this),
                "click": function () {
                    if( actionNode.retrieve("collapse") ){
                        actionNode.setStyles(this.css.naviExpandNode);
                        actionNode.store("collapse",false);
                        a.contentNode.show();
                    }else{
                        actionNode.setStyles(this.css.naviCollapseNode);
                        actionNode.store("collapse",true);
                        a.contentNode.hide();
                    }
                }.bind(this)
            });

            a.contentNode.inject(this.naviContentNode);
        }.bind(this))

        this.setContentHeightFun = this.setContentHeight.bind(this);
        this.addEvent("resize", this.setContentHeightFun);
        this.setContentHeightFun();

        this.naviIconTitleNode = new Element("div", {"styles": this.css.naviIconTitleNode}).inject(this.naviTitleNode);
        if (this.query.icon){
            this.naviIconTitleNode.setStyles({
                "background-image": "url(data:image/png;base64,"+this.query.icon+")",
            });
        }

        this.naviRightTitleNode = new Element("div", {"styles": this.css.naviRightTitleNode}).inject(this.naviTitleNode);
        this.naviTextTitleNode = new Element("div", {"styles": this.css.naviTextTitleNode, "text": this.query.name}).inject(this.naviRightTitleNode);
        this.naviDescriptionTitleNode = new Element("div", {"styles": this.css.naviDescriptionTitleNode, "text": this.query.description || this.lp.noDescription}).inject(this.naviRightTitleNode);
    },
    createLayoutMobile: function(){},

    setContentHeight: function(){
        var size = this.content.getSize();
        var titleSize = this.naviTitleNode.getSize();
        var y = size.y-titleSize.y;
        this.naviContentNode.setStyle("height", ""+y+"px");
    },
    createNavi: function(){
	    debugger;
	    var viewLoaded,statLoaded, statementLoaded,importerLoaded;
	    var callback = function () {
	        if( this.viewItemSelected || this.statItemSelected || this.statementItemSelected || this.importerItemSelected )return;
            if(viewLoaded && statLoaded && statementLoaded && importerLoaded){
                for( var i=0; i<this.naviArray.length; i++ ){
                    var items = this[this.naviArray[i].type+'Items'];
                    if( items && items.length ){
                        items[0].selected();
                        return;
                    }
                }
            }
        }.bind(this);

        this.createViewNavi(function () {
            viewLoaded = true;
            callback();
        }.bind(this));

        this.createStatNavi(function () {
            statLoaded = true;
            callback();
        }.bind(this));

        this.createStatementNavi(function () {
            statementLoaded = true;
            callback();
        }.bind(this));

        this.createImporterNavi(function () {
            importerLoaded = true;
            callback();
        }.bind(this));
    },
    createNavSearchNode: function (){
        this.searchNode = new Element("div.searchNode", {
            "styles": this.css.navSearch
        }).inject(this.naviContentNode);

        this.searchInput = new Element("input.searchInput", {
            "styles": this.css.navSearchInput,
            "placeholder": this.lp.searchNavPlaceholder,
            "title": this.lp.searchNavTitle
        }).inject(this.searchNode);

        this.searchButton = new Element("i.ooicon-search", {
            "styles": this.css.navSearchButton
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
                    this.searchNav();
                }
            }.bind(this)
        });

        this.searchButton.addEvent("click", function (e) {
            this.searchNav();
        }.bind(this));
    },
    searchNav: function(){
        var key = this.searchInput.get("value");

        var check = (item)=>{
            var isShow = !key ? true : item.node.get('text').contains(key);
            isShow ? item.node.show() : item.node.hide();
            return isShow;
        };

        var matches;

        matches= this.viewItems.filter((item)=>{ return check(item); });
        matches.length > 0 ? this.naviViewTitleNode.show() : this.naviViewTitleNode.hide();

        matches= this.statItems.filter((item)=>{ return check(item); });
        matches.length > 0 ? this.naviStatTitleNode.show() : this.naviStatTitleNode.hide();

        matches= this.statementItems.filter((item)=>{ return check(item); });
        matches.length > 0 ? this.naviStatementTitleNode.show() : this.naviStatementTitleNode.hide();

        matches= this.importerItems.filter((item)=>{ return check(item); });
        matches.length > 0 ? this.naviImporterTitleNode.show() : this.naviImporterTitleNode.hide();
    },
    createViewNavi: function( callback ){
        var data = this.interfaceData;
        this.viewItems = [];
        this.viewItemSelected = false;
        if( data.viewShow !== "flase" && data.viewShow !== false ) {
            this.action.listView(this.options.id, function (json) {
                if (json.data) {
                    if(json.data.length === 0){
                        this.naviViewTitleNode.hide();
                        this.naviViewContentNode.hide();
                    }
                    (json.data || []).sort(function(a, b){
                        return (a.orderNumber || 999999999) - (b.orderNumber || 999999999 );
                    });
                    json.data.each(function (view) {
                        if (view.display) {
                            var item = this.createViewNaviItem(view);
                            this.viewItems.push(item);
                            if (view.id === this.options.viewId) {
                                item.selected();
                                this.viewItemSelected = true;
                            }
                        }
                    }.bind(this));
                }
                callback();
            }.bind(this));
        }else{
            callback();
        }
    },
    createStatNavi: function(callback){
        var data = this.interfaceData;
        this.statItems = [];
        this.statItemSelected = false;
        if( data.statShow !== "flase" && data.statShow !== false ) {
            MWF.Actions.get("x_query_assemble_surface").listStat(this.options.id, function (json) {
                //this.action.listStat(this.options.id, function(json){
                if (json.data) {
                    if(json.data.length === 0){
                        this.naviStatTitleNode.hide();
                        this.naviStatContentNode.hide();
                    }
                    (json.data || []).sort(function(a, b){
                        return (a.orderNumber || 999999999) - (b.orderNumber || 999999999 );
                    });
                    json.data.each(function (stat) {
                        if (stat.display !== false ) {
                            var item = this.createStatNaviItem(stat);
                            this.statItems.push(item);
                            if (stat.id === this.options.statId) {
                                item.selected();
                                this.statItemSelected = true;
                            }
                        }
                    }.bind(this));
                }
                callback();
            }.bind(this));
        }else{
            callback();
        }
    },
    createStatementNavi: function(callback){
        var data = this.interfaceData;
        this.statementItems = [];
        this.statementItemSelected = false;
        if( data.statementShow !== "flase" && data.statementShow !== false ) {
            MWF.Actions.load("x_query_assemble_surface").StatementAction.listWithQuery(this.options.id, {
                "justSelect": true,
                "hasView": true
            }, function (json) {
                //this.action.listStat(this.options.id, function(json){
                if (json.data) {
                    if(json.data.length === 0){
                        this.naviStatementTitleNode.hide();
                        this.naviStatementContentNode.hide();
                    }
                    (json.data || []).sort(function(a, b){
                        return (a.orderNumber || 999999999) - (b.orderNumber || 999999999 );
                    });
                    json.data.each(function (statement) {
                        if (statement.display !== false && statement.viewEnable !== false ) {
                            var item = this.createStatementNaviItem(statement);
                            this.statementItems.push(item);
                            if (statement.id === this.options.statementId) {
                                item.selected();
                                this.statementItemSelected = true;
                            }
                        }
                    }.bind(this));
                }
                callback();
            }.bind(this));
        }else{
            callback();
        }
    },
    createImporterNavi: function(callback){
        var data = this.interfaceData;
        this.importerItems = [];
        this.importerItemSelected = false;
        if( data.importerShow !== "flase" && data.importerShow !== false ) {
            MWF.Actions.load("x_query_assemble_surface").ImportModelAction.listWithQuery(this.options.id, function (json) {
                //this.action.listStat(this.options.id, function(json){
                if (json.data) {
                    if(json.data.length === 0){
                        this.naviImporterTitleNode.hide();
                        this.naviImporterContentNode.hide();
                    }
                    (json.data || []).sort(function(a, b){
                        return (a.orderNumber || 999999999) - (b.orderNumber || 999999999 );
                    });
                    json.data.each(function (importer) {
                        if (importer.display !== false ) {
                            var item = this.createImporterNaviItem(importer);
                            this.importerItems.push(item);
                            if (importer.id === this.options.importerId) {
                                item.selected();
                                this.importerItemSelected = true;
                            }
                        }
                    }.bind(this));
                }
                callback();
            }.bind(this));
        }else{
            callback();
        }
    },
    createViewNaviItem: function(view){
        var item = new MWF.xApplication.query.Query.ViewItem(view, this);
        return item;
    },
    createStatNaviItem: function(stat){
        var item = new MWF.xApplication.query.Query.StatItem(stat, this);
        return item;
    },
    createStatementNaviItem: function(statement){
        var item = new MWF.xApplication.query.Query.StatementItem(statement, this);
        return item;
    },
    createImporterNaviItem: function(importer){
        var item = new MWF.xApplication.query.Query.ImporterItem(importer, this);
        return item;
    },


    recordStatus: function(){
        return {"id": this.options.id};
    }

});

MWF.xApplication.query.Query.ViewItem = new Class({
    initialize: function(view, app){
        this.view = view;
        this.app = app;
        this.css = this.app.css;
        this.lp = this.app.lp;
        this.isSelected = false;
        this.content = this.getContentNode();
        this.viewContent = this.getViewContentNode();
        this.load();
    },
    getContentNode: function(){
        return this.app.naviViewContentNode;
    },
    getViewContentNode: function(){
        return this.app.contentNode;
    },
    load: function(){
        this.node = new Element("div", {
            "styles": this.css.naviContentItemNode,
            "text": this.view.name,
            "title": this.view.name
        }).inject(this.content);

        this.node.addEvents({
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css.naviContentItemNode_over); }.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css.naviContentItemNode); }.bind(this),
            "click": function(){this.selected();}.bind(this)
        });
    },
    selected: function(){
        if (this.app.currentItem) this.app.currentItem.unselected();
        this.node.setStyles(this.css.naviContentItemNode_selected);
        this.app.currentItem = this;
        this.isSelected = true;
        this.loadView();
    },
    unselected: function(){
        this.node.setStyles(this.css.naviContentItemNode);
        this.app.currentItem = null;
        this.isSelected = false;
    },
    loadView: function(){
        MWF.xDesktop.requireApp("query.Query", "Viewer",function(){
            this.viewContent.empty();
            var data = JSON.parse(this.view.data);
            this.viewer = new MWF.QViewer(this.viewContent, {
                "application": this.view.query,
                "viewName": this.view.name,
                "isExpand": data.isExpand
            }, {
                "export": true,
                "onLoadLayout": function () {
                    this.viewAreaNode.setStyles({
                        "padding-left": "10px",
                        "padding-right": "10px"
                    });
                    if( this.viewJson && this.viewJson.customFilterList && this.viewJson.customFilterList.length ) {
                    }else{
                        this.viewAreaNode.setStyles({"padding-top": "10px"})
                    }
                }
            }, this.app);
        }.bind(this));
    }

});


MWF.xApplication.query.Query.StatItem = new Class({
    Extends: MWF.xApplication.query.Query.ViewItem,
    getContentNode: function(){
        return this.app.naviStatContentNode;
    },
    loadView: function(){
        MWF.xDesktop.requireApp("query.Query", "Statistician",function(){
            this.viewContent.empty();
            this.viewer = new MWF.QStatistician(this.app, this.viewContent, {
                "application": this.view.query,
                "statName": this.view.name,
                "isTable": true,
                "isChart": true,
                "isLegend": true
            }, {
                "onLoadLayout": function () {
                    this.node.setStyles({
                        "padding-left": "10px",
                        "padding-right": "10px",
                        "padding-top": "10px",
                        "padding-bottom": "10px"
                    });
                },
                "onLoaded": function () {
                    this._setContentHeight();
                }
            });
        }.bind(this));
    }
});

MWF.xApplication.query.Query.StatementItem = new Class({
    Extends: MWF.xApplication.query.Query.ViewItem,
    getContentNode: function(){
        return this.app.naviStatementContentNode;
    },
    loadView: function(){
        MWF.xDesktop.requireApp("query.Query", "Statement",function(){
            this.viewContent.empty();
            this.viewer = new MWF.QStatement( this.viewContent, {
                "application": this.view.query,
                "statementName": this.view.name,
                "statementId" : this.view.id
            },{
                "onLoadLayout": function () {
                    this.viewAreaNode.setStyles({
                        "padding-left": "10px",
                        "padding-right": "10px"
                    });
                    if( this.viewJson && this.viewJson.customFilterList && this.viewJson.customFilterList.length ) {
                    }else{
                        this.viewAreaNode.setStyles({"padding-top": "10px"})
                    }
                }
            }, this.app);
        }.bind(this));
    }
});


MWF.xApplication.query.Query.ImporterItem = new Class({
    Extends: MWF.xApplication.query.Query.ViewItem,
    getContentNode: function(){
        return this.app.naviImporterContentNode;
    },
    loadView: function(){
        MWF.xDesktop.requireApp("query.Query", "ImporterRecord", function(){
            this.viewContent.empty();
            this.viewer = new MWF.xApplication.query.Query.ImporterRecord( this.viewContent, this.app, {
                "application": this.view.query,
                "importerName": this.view.name,
                "importerId" : this.view.id,
                "onLoadLayout": function () {
                    this.viewContainer.setStyles({
                        "padding-left": "10px",
                        "padding-right": "10px",
                        "padding-top": "10px"
                    });
                }
            });
            this.viewer.load()
        }.bind(this));
    }
});