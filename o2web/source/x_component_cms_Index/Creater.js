MWF.require("MWF.widget.Mask", null, false);
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Index = MWF.xApplication.cms.Index || {};

MWF.xApplication.cms.Index.Creater = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function(app, columnData, view, options){
        this.setOptions(options);
        this.path = "../x_component_cms_Index/$Creater/";
        this.cssPath = "../x_component_cms_Index/$Creater/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.view = view;
        this.columnData = columnData;

        MWF.xDesktop.requireApp("cms.Index", "$Creater."+MWF.language, null, false);
        this.lp = MWF.xApplication.cms.Index.Creater.lp;
    },
    load: function(){
        if (!this.startDocumentAreaNode){
            this.createStartDocumentArea();
        }
        this.startDocumentAreaNode.fade("0.95");
    },
    closeStartDocumentArea: function(){
        //if (this.startDocumentAreaNode) this.startDocumentTween.start("left", "0px", "-400px");
        if (this.startDocumentAreaNode) this.startDocumentAreaNode.fade("out");
    },

    createStartDocumentArea: function(){
        this.createStartDocumentAreaNode();
        this.createStartDocumentCloseNode();
        this.createStartDocumentScrollNode();
        if( this.columnData ){
            if(!this.columnData.name)this.columnData.name = this.columnData.appName;
            new MWF.xApplication.cms.Index.Creater.Column(this.columnData, this.app, this, this.startDocumentContentNode, true );
        }else{
            this.listColumns();
        }

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
            this.action.listColumnByPublish(function(json){
                json.data.each(function(column){
                    if(!column.name)column.name = column.appName;
                    new MWF.xApplication.cms.Index.Creater.Column(column, this.app, this, this.startDocumentContentNode);
                }.bind(this));
            }.bind(this));
        }.bind(this));
    },
    getAction: function(callback){
        if (!this.action){
            //MWF.xDesktop.requireApp("cms.Index", "Actions.RestActions", function(){
                this.action = MWF.Actions.get("x_cms_assemble_control");  //new MWF.xApplication.cms.Index.Actions.RestActions();
                if (callback) callback();
            //}.bind(this));
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

MWF.xApplication.cms.Index.Creater.Column = new Class({

    initialize: function(data, app, creater, container, needGetCategorys ){
        this.bgColors = ["#30afdc", "#e9573e", "#8dc153", "#9d4a9c", "#ab8465", "#959801", "#434343", "#ffb400", "#9e7698", "#00a489"];
        this.data = data;
        this.app = app;
        this.creater = creater;
        this.container = container;
        this.css = this.creater.css;
        this.needGetCategorys = needGetCategorys;

        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.columnNode}).inject(this.container);

        this.topNode = new Element("div", {"styles": this.css.columnTopNode}).inject(this.node);
        //   this.topNode.setStyle("background-color", this.bgColors[(Math.random()*10).toInt()]);

        //this.iconNode = new Element("div", {"styles": this.css.columnIconNode}).inject(this.topNode);
        //if (this.data.appIcon){
        //    this.iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.appIcon+")");
        //}else{
        //    this.iconNode.setStyle("background-image", "url("+this.creater.path+this.creater.options.style+"/column.png)")
        //}
        var iconAreaNode = this.iconAreaNode = new Element("div",{
            "styles" : this.css.columnIconAreaNode
        }).inject(this.topNode);
        var iconNode = this.iconNode = new Element("img",{
            "styles" : this.css.columnIconNode
        }).inject(iconAreaNode);
        if (this.data.appIcon){
            this.iconNode.set("src", "data:image/png;base64,"+this.data.appIcon+"");
        }else{
            this.iconNode.set("src", this.creater.path+this.creater.options.style+"/column.png")
        }

        this.textNode = new Element("div", {"styles": this.css.columnTextNode}).inject(this.topNode);
        this.textNode.set("text", this.data.name);

        this.childNode = new Element("div", {"styles": this.css.columnChildNode}).inject(this.node);
        this.loadChild();
    },
    loadChild: function(){
        if( this.needGetCategorys ){
            this.creater.getAction(function(){
                this.creater.action.listCategoryByPublisher(this.data.id,function(json){
                    if (json.data.length){
                        json.data.each(function(category){
                            new MWF.xApplication.cms.Index.Creater.Category(category, this, this.childNode);
                        }.bind(this));
                    }else{
                        this.node.setStyle("display", "none");
                    }
                }.bind(this), null, this.data.id)
            }.bind(this))
        }else{
            if( this.data.wrapOutCategoryList && this.data.wrapOutCategoryList.length ){
                this.data.wrapOutCategoryList.each(function(category){
                    new MWF.xApplication.cms.Index.Creater.Category(category, this, this.childNode);
                }.bind(this));
            }else{
                this.node.setStyle("display", "none");
            }
        }
    }
});

MWF.xApplication.cms.Index.Creater.Category = new Class({
    initialize: function(data, column, container){
        this.data = data;
        this.column = column;
        this.app = this.column.app;
        this.creater = this.column.creater;
        this.container = container;
        this.css = this.creater.css;

        this.load();
    },
    load: function(){
        if( !this.data.name )this.data.name = this.data.categoryName;
        this.node = new Element("div.categoryItem", {"styles": this.css.startCategoryNode}).inject(this.container);

        this.iconNode = new Element("div", {"styles": this.css.categoryIconNode}).inject(this.node);
        this.textNode = new Element("div", {"styles": this.css.categoryTextNode}).inject(this.node);

        this.textNode.set({
            "text": this.data.categoryName
        });
        var _self = this;
        this.node.addEvents({
            "mouseover": function(e){this.node.setStyles(this.css.startCategoryNode_over);}.bind(this),
            "mouseout": function(e){this.node.setStyles(this.css.startCategoryNode_out);}.bind(this),
            "click": function(e){
                this.createDocument(e);
            }.bind(this)
        });
    },

    //createDocument: function( e ){
    //    var fielter = {
    //        "categoryIdList": [this.data.id ],
    //        "creatorList": [layout.desktop.session.user.name]
    //    };
    //    this.creater.getAction(function(){
    //        this.creater.action.listDraftNext("(0)", 1, fielter, function(json){
    //            if( json.data.length > 0 ){
    //                this._openDocument(json.data[0].id);
    //            }else{
    //                this._createDocument();
    //            }
    //        }.bind(this));
    //    }.bind(this))
    //},
    createDocument : function(){
        this.creater.closeStartDocumentArea();
        if( !this.data.formId || this.data.formId=="" ){
            this.app.notice(this.creater.lp.noFormSelected, "error");
            return;
        }
        MWF.xDesktop.requireApp("cms.Index", "Starter", function(){
            var starter = new MWF.xApplication.cms.Index.Starter(this.column.data, this.data, this.app, {
                //"onStarted": function(data, title, categoryName){
                //    this.afterStart(data, title, categoryName);
                //}.bind(this)
                onPostPublish : function(){
                    if(this.creater.view )this.creater.view.reload();
                }.bind(this)
            });
            starter.load();
        }.bind(this));
    }
    //_openDocument: function(id,el){
    //    var _self = this;
    //
    //    var appId = "cms.Document"+id;
    //    if (_self.app.desktop.apps[appId]){
    //        _self.app.desktop.apps[appId].setCurrent();
    //    }else {
    //        var options = {
    //            "readonly" :false,
    //            "documentId": id,
    //            "appId": appId,
    //            "postPublish" : function(){
    //                if(_self.creater.view )_self.creater.view.reload();
    //            }
    //        };
    //        this.app.desktop.openApplication(el, "cms.Document", options);
    //    }
    //},
    //afterStart : function(data, title, categoryName){
    //    var _self = this;
    //    var appId = "cms.Document"+data.id;
    //    if (_self.app.desktop.apps[appId]){
    //        _self.app.desktop.apps[appId].setCurrent();
    //    }else {
    //        var options = {
    //            "readonly" :false,
    //            "documentId": data.id,
    //            "appId": appId,
    //            "postPublish" : function(){
    //                if(_self.creater.view )_self.creater.view.reload();
    //            }
    //        };
    //        this.app.desktop.openApplication(null, "cms.Document", options);
    //    }
    //}

});