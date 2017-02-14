MWF.xDesktop.requireApp("cms.Explorer", "Explorer", null, false);
MWF.xApplication.cms.Explorer.DraftExplorer = new Class({
	Extends: MWF.xApplication.cms.Explorer.Explorer,
	Implements: [Options, Events],
    options: {
        "style": "default",
        "status": "draft",
        "tooltip": {
        }
    },
    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();
        this.path = "/x_component_cms_Explorer/$DraftExplorer/";
        this.cssPath = "/x_component_cms_Explorer/$DraftExplorer/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.actions = actions;
        this.node = $(node);
        this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    load: function(){

        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode});
        this.toolbarNode.inject(this.node);

        this.loadToolbar();

        this.filterConditionNode = new Element("div", {
            "styles": this.css.filterConditionNode
        }).inject(this.node );

        this.loadContentNode();

        this.setNodeScroll();

        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.node);

        this.loadElementList();
    },

    _createItem: function(data){
        return new MWF.xApplication.cms.Explorer.DraftExplorer.Document(data, this);
    },

    _getCurrentPageData: function(callback, count){
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        if (this.filter && this.filter.filter ){
            this.actions.listDraftNext(id, count || this.pageCount, this.filter.getFilterResult(), function(json){
                if (callback) callback(json);
            });
        }else{
            this.actions.listDraftNext(id, count || this.pageCount, {}, function(json){
                if (callback) callback(json);
            });
        }
    },
    createDocument: function( el ){
        if (!this.startDocumentAreaNode){
            this.createStartDocumentArea();
        }
        this.startDocumentAreaNode.fade("0.9");
    },
    closeStartDocumentArea: function(){
        //if (this.startDocumentAreaNode) this.startDocumentTween.start("left", "0px", "-400px");
        if (this.startDocumentAreaNode) this.startDocumentAreaNode.fade("out");
    },

    createStartDocumentArea: function(){
        this.createStartDocumentAreaNode();
        this.createStartDocumentCloseNode();
        this.createStartDocumentScrollNode();

        this.listColumns();

        this.setResizeStartDocumentAreaHeight();
        this.app.addEvent("resize", this.setResizeStartDocumentAreaHeight.bind(this));

    },
    createStartDocumentAreaNode: function(){
        this.startDocumentAreaNode = new Element("div", {"styles": this.css.startDocumentAreaNode}).inject(this.app.content);
        this.startDocumentAreaNode.addEvent("click", function(e){
            this.closeStartDocumentArea();
        }.bind(this));
    },
    createStartDocumentCloseNode: function(){
        this.startDocumentTopNode = new Element("div", {"styles": this.css.startDocumentTopNode}).inject(this.startDocumentAreaNode);
        this.startDocumentCloseNode = new Element("div", {"styles": this.css.startDocumentCloseNode}).inject(this.startDocumentTopNode);
        this.startDocumentCloseNode.addEvent("click", function(e){
            this.closeStartDocumentArea();
        }.bind(this));
    },
    createStartDocumentScrollNode: function(){
        this.startDocumentScrollNode = new Element("div", {"styles": this.css.startDocumentScrollNode}).inject(this.startDocumentAreaNode);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.startDocumentScrollNode, {
                "style":"xApp_taskcenter", "where": "after", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));
        this.startDocumentContentNode = new Element("div", {"styles": this.css.startDocumentContentNode}).inject(this.startDocumentScrollNode);
    },
    listColumns: function(){
        this.getAction(function(){
            this.action.listColumn(function(json){
                json.data.each(function(column){
                    if(!column.name)column.name = column.appName;
                    new MWF.xApplication.cms.Explorer.DraftExplorer.Column(column, this.app, this, this.startDocumentContentNode);
                }.bind(this));
            }.bind(this));
        }.bind(this));
    },
    getAction: function(callback){
        if (!this.action){
            MWF.xDesktop.requireApp("cms.Explorer", "Actions.RestActions", function(){
                this.action = new MWF.xApplication.cms.Explorer.Actions.RestActions();
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    setResizeStartDocumentAreaHeight: function(){
        var size = this.app.content.getSize();
        if (this.startDocumentAreaNode){
            var topSize = this.startDocumentCloseNode.getSize();
            var y = size.y-topSize.y-80;
            var x = size.x - 110;
            var areay = size.y-60;
            var areax = size.x-90;
            this.startDocumentScrollNode.setStyle("height", ""+y+"px");
            this.startDocumentScrollNode.setStyle("width", ""+x+"px");
            this.startDocumentAreaNode.setStyle("height", ""+areay+"px");
            this.startDocumentAreaNode.setStyle("width", ""+areax+"px");
        }
    }
});

MWF.xApplication.cms.Explorer.DraftExplorer.Document = new Class({
    Extends: MWF.xApplication.cms.Explorer.Explorer.Document,

    setActions: function(){
        this.openNode = new Element("div", {"styles": this.css.actionOpenNode, "title": this.explorer.app.lp.open}).inject(this.actionAreaNode);
        this.deleteNode = new Element("div", {"styles": this.css.actionDeleteNode, "title": this.explorer.app.lp.delete}).inject(this.actionAreaNode);
    }

})


MWF.xApplication.cms.Explorer.DraftExplorer.Column = new Class({

    initialize: function(data, app, explorer, container){
        this.bgColors = ["#30afdc", "#e9573e", "#8dc153", "#9d4a9c", "#ab8465", "#959801", "#434343", "#ffb400", "#9e7698", "#00a489"];
        this.data = data;
        this.app = app;
        this.explorer = explorer;
        this.container = container;
        this.css = this.explorer.css;

        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.columnNode}).inject(this.container);

        this.topNode = new Element("div", {"styles": this.css.columnTopNode}).inject(this.node);
        //   this.topNode.setStyle("background-color", this.bgColors[(Math.random()*10).toInt()]);

        this.iconNode = new Element("div", {"styles": this.css.columnIconNode}).inject(this.topNode);
        if (this.data.appIcon){
            this.iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.appIcon+")");
        }else{
            this.iconNode.setStyle("background-image", "url("+"/x_component_cms_Column/$Main/"+this.app.options.style+"/icon/column.png)")
        }

        this.textNode = new Element("div", {"styles": this.css.columnTextNode}).inject(this.topNode);
        this.textNode.set("text", this.data.name);

        this.childNode = new Element("div", {"styles": this.css.columnChildNode}).inject(this.node);
        this.loadChild();
    },
    loadChild: function(){
        this.explorer.action.listCategory(this.data.id,function(json){
            if (json.data.length){
                json.data.each(function(category){
                    new MWF.xApplication.cms.Explorer.DraftExplorer.Category(category, this, this.childNode);
                }.bind(this));
            }else{
                this.node.setStyle("display", "none");
            }
        }.bind(this), null, this.data.id)

    }
});

MWF.xApplication.cms.Explorer.DraftExplorer.Category = new Class({
    initialize: function(data, column, container){
        this.data = data;
        this.column = column;
        this.app = this.column.app;
        this.explorer = this.column.explorer;
        this.container = container;
        this.css = this.explorer.css;

        this.load();
    },
    load: function(){
        this.node = new Element("div.categoryItem", {"styles": this.css.startCategoryNode}).inject(this.container);

        this.iconNode = new Element("div", {"styles": this.css.categoryIconNode}).inject(this.node);
        this.textNode = new Element("div", {"styles": this.css.categoryTextNode}).inject(this.node);

        this.textNode.set({
            "text": this.data.name,
            "title": this.data.name+"-"+this.data.description
        });
        var _self = this;
        this.node.addEvents({
            "mouseover": function(e){this.node.setStyles(this.css.startCategoryNode_over);}.bind(this),
            "mouseout": function(e){this.node.setStyles(this.css.startCategoryNode_out);}.bind(this),
            "click": function(e){
                this.startCategory(e);
            }.bind(this)
        });
    },
    startCategory: function(e){
        this.explorer.closeStartDocumentArea();
        MWF.xDesktop.requireApp("cms.Explorer", "Starter", function(){
            var starter = new MWF.xApplication.cms.Explorer.Starter(this.column.data, this.data, this.app, {
                "onStarted": function(data, title, categoryName){
                    this.afterStart(data, title, categoryName);
                }.bind(this)
            });
            starter.load();
        }.bind(this));
    },
    afterStart : function(data, title, categoryName){
        var options = {"documentId": data.id};
        this.app.desktop.openApplication(null, "cms.Document", options);
    }

});

