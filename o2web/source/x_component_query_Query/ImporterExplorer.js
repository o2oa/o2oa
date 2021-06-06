MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.require("MWF.widget.Common", null, false);
MWF.xDesktop.requireApp("Template","Explorer", null, false);
MWF.xDesktop.requireApp("query.Query", "lp."+o2.language, null, false);
MWF.xApplication.query.Query.ImporterExplorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "importId": "",
        "style": "default",
        "viewPageNum" : 1
    },
    initialize: function (container, app, options) {
        this.setOptions( options );
        this.container = container;

        this.path = "../x_component_query_Query/$ImporterExplorer/";
        this.cssPath = "../x_component_query_Query/$ImporterExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.app = app;
        this.lp = MWF.xApplication.query.Query.LP;
    },
    load: function () {
        debugger;
        this.container.empty();

        this.loadToolbar();

        this.viewContainerTop = Element("div",{
            "styles" : this.css.viewContainerTop
        }).inject(this.container);

        this.viewContainer = Element("div",{
            "styles" : this.css.viewContainer
        }).inject(this.container);


        //this.loadTopView();
        this.loadView();
    },
    destroy : function(){
        if(this.resizeWindowFun)this.app.removeEvent("resize",this.resizeWindowFun);
        this.view.destroy();
    },
    loadToolbar: function(){
        var toolbar = new Element("div",{
            styles : this.css.toolbar
        }).inject(this.container);
        this.toolbarTop = toolbar;

        var createActionNode = new Element("div",{
            styles : this.css.toolbarActionNode,
            text: this.lp.createSubject
        }).inject(toolbar);
        createActionNode.addEvents(
            {
                "mouseover": function () {
                    this.node.setStyles(this.obj.css.toolbarActionNode_over);
                }.bind({obj: this, node: createActionNode}),
                "mouseout": function () {
                    this.node.setStyles(this.obj.css.toolbarActionNode);
                }.bind({obj: this, node: createActionNode}),
                "click": function () {
                    if( this.app.access.isAnonymousDynamic() ){
                        this.app.openLoginForm(
                            function(){ this.createSubject(); }.bind(this)
                        );
                    }else{
                        this.createSubject();
                    }
                }.bind(this)
            }
        )
    },
    loadView : function(){

        //this.resizeWindow();
        //this.resizeWindowFun = this.resizeWindow.bind(this)
        //this.app.addEvent("resize", this.resizeWindowFun );

        this.view = new MWF.xApplication.query.Query.ImporterView( this.viewContainer, this.app, this, {
            templateUrl : this.path+this.options.style+"/listItem.json",
            pagingEnable : true,
            onPostCreateViewBody : function(){
                this.app.fireEvent("postCreateViewBody");
            }.bind(this),
            pagingPar : {
                pagingBarUseWidget: true,
                position : [ "bottom" ],
                style : "blue_round",
                hasReturn : false,
                currentPage : this.options.viewPageNum,
                countPerPage : 30,
                visiblePages : 9,
                hasNextPage : true,
                hasPrevPage : true,
                hasTruningBar : true,
                hasJumper : true,
                returnText : "",
                hiddenWithDisable: false,
                text: {
                    prePage: "",
                    nextPage: "",
                    firstPage: "第一页",
                    lastPage: "最后一页"
                }
            }
        } );
        this.view.load();
    },
    reloadView : function() {
        this.viewContainer.setStyle("display","");
        this.viewContainerTop.setStyle("display","");

        //this.loadTopView();
        this.loadView();
    },
    resizeWindow: function(){
        var size = this.app.content.getSize();
        this.viewContainer.setStyles({"height":(size.y-121)+"px"});
    },
    createSubject: function(){
        var _self = this;
        var appId = "ForumDocument"+this.app.sectionData.id;
        if (_self.app.desktop.apps[appId]){
            _self.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ForumDocument", {
                "sectionId": this.app.sectionData.id,
                "appId": appId,
                "isNew" : true,
                "isEdited" : true,
                "onPostPublish" : function(){
                    //this.view.reload();
                }.bind(this)
            });
        }
    }
});

MWF.xApplication.query.Query.ImporterView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data, index){
        return new MWF.xApplication.query.Query.ImporterDocument(this.viewNode, data, this.explorer, this, null,  index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.clearBody();
        if(!count)count=30;
        if(!pageNum)pageNum = 1;
        var filter = { "modelId": this.explorer.options.importId };

        var json = {
            count: 5,
            size: 5,
            data: [
                {
                    "name": "导入测试",
                    "status": "导入成功",
                    "count": "200",
                    "createTime": "2020-03-10 09:11:12"
                },
                {
                    "name": "导入测试1",
                    "status": "导入失败",
                    "count": "110",
                    "createTime": "2020-03-10 09:11:10"
                },
                {
                    "name": "导入测试2",
                    "status": "导入成功",
                    "count": "200",
                    "createTime": "2020-03-10 09:11:12"
                },
                {
                    "name": "导入测试3",
                    "status": "导入失败",
                    "count": "110",
                    "createTime": "2020-03-10 09:11:10"
                },
                {
                    "name": "导入测试4",
                    "status": "导入成功",
                    "count": "200",
                    "createTime": "2020-03-10 09:11:12"
                },
                {
                    "name": "导入测试5",
                    "status": "导入失败",
                    "count": "110",
                    "createTime": "2020-03-10 09:11:10"
                }
            ]
        };
        if( callback )callback(json);
        return;

        //filter.withTopSubject = false;
        this.actions.listImportModuleRecord( pageNum, count, filter, function(json){
            if( !json.data )json.data = [];
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteSubject(documentData.id, function(json){
            this.reload();
            this.app.notice(this.explorer.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData,index ){
        var appId = "ForumDocument"+documentData.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ForumDocument", {
                "sectionId" : documentData.sectionId,
                "id" : documentData.id,
                "appId": appId,
                "isEdited" : false,
                "isNew" : false,
                "index" : index
            });
        }
    },
    _queryCreateViewNode: function(){
    },
    _postCreateViewNode: function( viewNode ){
    },
    _queryCreateViewHead:function(){
    },
    _postCreateViewHead: function( headNode ){
    }

});

MWF.xApplication.query.Query.ImporterDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    },
    open: function (e) {
        this.view._openDocument(this.data, this.index);
    },
    edit : function(){
        var appId = "ForumDocument"+this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ForumDocument", {
                "sectionId" : this.data.sectionId,
                "id" : this.data.id,
                "appId": appId,
                "isEdited" : true,
                "isNew" : false,
                "index" : this.index
            });
        }
    }
});