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
        this.content.setStyle("background-color", "#ffffff");
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
            this.createLayout();
            this.createNavi();

            this.addEvent("resize", function(){
                if (this.currentItem){
                    if (this.currentItem.viewer){
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
        this.naviViewTitleNode = new Element("div", {"styles": this.css.naviViewTitleNode, "text": this.lp.view}).inject(this.naviContentNode);
        this.naviViewContentNode = new Element("div", {"styles": this.css.naviViewContentNode}).inject(this.naviContentNode);
        this.naviStatTitleNode = new Element("div", {"styles": this.css.naviStatTitleNode, "text": this.lp.stat}).inject(this.naviContentNode);
        this.naviStatContentNode = new Element("div", {"styles": this.css.naviStatContentNode}).inject(this.naviContentNode);

        this.naviStatementTitleNode = new Element("div", {"styles": this.css.naviStatementTitleNode, "text": this.lp.statement}).inject(this.naviContentNode);
        this.naviStatementContentNode = new Element("div", {"styles": this.css.naviStatementContentNode}).inject(this.naviContentNode);

        this.naviImporterTitleNode = new Element("div", {"styles": this.css.naviImporterTitleNode, "text": this.lp.importer}).inject(this.naviContentNode);
        this.naviImporterContentNode = new Element("div", {"styles": this.css.naviImporterContentNode}).inject(this.naviContentNode);

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
        this.action.listView(this.options.id, function(json){
            if (json.data){
                json.data.each(function(view){
                    if(view.display) {
                        var item = this.createViewNaviItem(view);
                        if( view.id === this.options.viewId ){
                            item.selected()
                        }
                    }
                }.bind(this));
            }
        }.bind(this));
        MWF.Actions.get("x_query_assemble_surface").listStat(this.options.id, function(json){
        //this.action.listStat(this.options.id, function(json){
            if (json.data){
                json.data.each(function(stat){
                    var item = this.createStatNaviItem(stat);
                    if( stat.id === this.options.statId ){
                        item.selected()
                    }
                }.bind(this));
            }
        }.bind(this));
        MWF.Actions.load("x_query_assemble_surface").StatementAction.listWithQuery(this.options.id, {
            "justSelect" : true,
            "hasView" : true
        }, function(json){
            //this.action.listStat(this.options.id, function(json){
            if (json.data){
                json.data.each(function(statement){
                    debugger;
                    var item = this.createStatementNaviItem(statement);
                    if( statement.id === this.options.statementId ){
                        item.selected()
                    }
                }.bind(this));
            }
        }.bind(this));

        MWF.Actions.load("x_query_assemble_surface").ImportModelAction.listWithQuery(this.options.id, function(json){
            //this.action.listStat(this.options.id, function(json){
            if (json.data){
                json.data.each(function(importer){
                    debugger;
                    var item = this.createImporterNaviItem(importer);
                    if( importer.id === this.options.importerId ){
                        item.selected()
                    }
                }.bind(this));
            }
        }.bind(this));

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
            "styles": this.css.naviViewContentItemNode,
            "text": this.view.name,
            "title": this.view.name
        }).inject(this.content);

        this.node.addEvents({
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css.naviViewContentItemNode_over); }.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css.naviViewContentItemNode); }.bind(this),
            "click": function(){this.selected();}.bind(this)
        });
    },
    selected: function(){
        if (this.app.currentItem) this.app.currentItem.unselected();
        this.node.setStyles(this.css.naviViewContentItemNode_selected);
        this.app.currentItem = this;
        this.isSelected = true;
        this.loadView();
    },
    unselected: function(){
        this.node.setStyles(this.css.naviViewContentItemNode);
        this.app.currentItem = null;
        this.isSelected = false;
    },
    loadView: function(){
        debugger;
        MWF.xDesktop.requireApp("query.Query", "Viewer",function(){
            this.viewContent.empty();
            var data = JSON.parse(this.view.data);
            this.viewer = new MWF.QViewer(this.viewContent, {
                "application": this.view.query,
                "viewName": this.view.name,
                "isExpand": data.isExpand
            }, {"export": true}, this.app);
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
            debugger;
            this.viewer = new MWF.QStatement( this.viewContent, {
                "application": this.view.query,
                "statementName": this.view.name,
                "statementId" : this.view.id
            },{}, this.app);
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
            debugger;
            this.viewContent.empty();
            this.viewer = new MWF.xApplication.query.Query.ImporterRecord( this.viewContent, this.app, {
                "application": this.view.query,
                "importerName": this.view.name,
                "importerId" : this.view.id
            });
            this.viewer.load()
        }.bind(this));
    }
});