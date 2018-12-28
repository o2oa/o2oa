MWF.xApplication.Execution = MWF.xApplication.Execution || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.Execution.WorkReportList = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "workNavi1" : ""
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp.WorkReportList;
        this.path = "/x_component_Execution/$WorkReportList/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_Execution/$WorkReportList/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        this.tabLocation = "";
        this.middleContent = this.app.middleContent;
        this.middleContent.setStyles({"margin-top":"0px","border":"0px solid #f00"});
        this.createNaviContent();
        this.createContentDiv();

        this.resizeWindow();
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));
    },
    resizeWindow: function(){
        var size = this.app.middleContent.getSize();
        this.naviDiv.setStyles({"height":(size.y-60)+"px"});
        this.naviContentDiv.setStyles({"height":(size.y-180)+"px"});
        this.contentDiv.setStyles({"height":(size.y-60)+"px"});
        this.rightContentDiv.setStyles({"height":(size.y-40-100)+"px"});
    },
    createNaviContent: function(){
        this.naviDiv = new Element("div.naviDiv",{
            "styles":this.css.naviDiv
        }).inject(this.middleContent);

        this.naviTitleDiv = new Element("div.naviTitleDiv",{
            "styles":this.css.naviTitleDiv,
            "text":this.lp.title
        }).inject(this.naviDiv);
        this.naviContentDiv = new Element("div.naviContentDiv",{"styles":this.css.naviContentDiv}).inject(this.naviDiv);
        this.naviBottomDiv = new Element("div.naviBottomDiv",{"styles":this.css.naviBottomDiv}).inject(this.naviDiv);


        var naviContentLi = new Element("li.naviContentLi",{"styles":this.css.naviContentLi}).inject(this.naviContentDiv)
            .addEvents({
                "click":function(){
                    this.app.openTask()
                }.bind(this)
            });
        var naviContentImg = new Element("img.naviContentImg",{
            "styles":this.css.naviContentImg,
            "src":"/x_component_Execution/$WorkReportList/default/icon/Prototype-100.png"
        }).inject(naviContentLi);
        var naviContentSpan = new Element("span.naviContentSpan",{
            "styles":this.css.naviContentSpan,
            "text":this.lp.naviItem.workDeploy
        }).inject(naviContentLi);

        //var naviContentLi = new Element("li.naviContentLi",{"styles":this.css.naviContentLi}).inject(this.naviContentDiv);
        //var naviContentImg = new Element("img.naviContentImg",{
        //    "styles":this.css.naviContentImg,
        //    "src":"/x_component_Execution/$WorkReportList/default/icon/Conference-100.png"
        //}).inject(naviContentLi);
        //var naviContentSpan = new Element("span.naviContentSpan",{
        //    "styles":this.css.naviContentSpan,
        //    "text":this.lp.naviItem.workConsult
        //}).inject(naviContentLi)
        //
        //var naviContentLi = new Element("li.naviContentLi",{"styles":this.css.naviContentLi}).inject(this.naviContentDiv);
        //var naviContentImg = new Element("img.naviContentImg",{
        //    "styles":this.css.naviContentImg,
        //    "src":"/x_component_Execution/$WorkReportList/default/icon/Department-100.png"
        //}).inject(naviContentLi);
        //var naviContentSpan = new Element("span.naviContentSpan",{
        //    "styles":this.css.naviContentSpan,
        //    "text":this.lp.naviItem.workStat
        //}).inject(naviContentLi)

    },
    createContentDiv: function(){
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.middleContent);
        this.createCategoryItemDiv();

        this.createSearchDiv();

        this.createRightContentDiv();
    },
    createCategoryItemDiv: function(){
        this.rightCategoryDiv = new Element("div.rightCategoryDiv",{"styles":this.css.rightCategoryDiv}).inject(this.contentDiv);

        this.rightCategoryItemDiv = new Element("div.rightCategoryItemDiv",{"styles":this.css.rightCategoryItemDiv}).inject(this.rightCategoryDiv);

        this.drafterLi = new Element("li.drafterLi",{
            "styles":this.css.rightCategoryItemLi,
            "text": this.lp.drafterTab
        }).inject(this.rightCategoryItemDiv)
            .addEvents({
                "click":function(){
                    //alert("草稿箱点击");
                    //this.rightContentDiv.destroy();
                    //this.createCenterWorkSearchDiv();
                    this.createView( "drafter" );
                }.bind(this)
            });
        this.todoLi = new Element("li.todoLi",{
            "styles":this.css.rightCategoryItemCurrentLi,
            "text": this.lp.todoTab
        }).inject(this.rightCategoryItemDiv)
            .addEvents({
                "click":function(){
                    //alert("待处理点击");
                    //this.rightContentDiv.destroy();
                    this.createView( "todo" );
                    //this.createBaseWorkSearchDiv();
                    //this.createRightContentDiv("base");
                }.bind(this)
            });
        this.doneLi = new Element("li.doneLi",{
            "styles":this.css.rightCategoryItemLi,
            "text": this.lp.doneTab
        }).inject(this.rightCategoryItemDiv)
            .addEvents({
                "click":function(){
                    //alert("已处理点击");
                    //this.rightContentDiv.destroy();
                    //this.createBaseWorkSearchDiv();
                    this.createView( "done" );
                }.bind(this)
            });
        this.archiveLi = new Element("li.archiveLi",{
            "styles":this.css.rightCategoryItemLi,
            "text": this.lp.archiveTab
        }).inject(this.rightCategoryItemDiv)
            .addEvents({
                "click":function(){
                    //alert("已处理点击");
                    //this.rightContentDiv.destroy();
                    //this.createBaseWorkSearchDiv();
                    this.createView( "archive" );
                }.bind(this)
            })

    },
    createView : function( str ){
        this.workNavi1 = str || "todo";
        if( str == "done" ){
            this.drafterLi.setStyles({"border-bottom":""});
            this.todoLi.setStyles({"border-bottom":""});
            this.doneLi.setStyles({"border-bottom":"2px solid #124c93"});
            this.archiveLi.setStyles({"border-bottom":""});
            this.createDoneView();
        }else if(str == "drafter"){
            this.drafterLi.setStyles({"border-bottom":"2px solid #124c93"});
            this.todoLi.setStyles({"border-bottom":""});
            this.doneLi.setStyles({"border-bottom":""});
            this.archiveLi.setStyles({"border-bottom":""});
            this.createDrafterView();
        }else if(str=="archive"){
            this.drafterLi.setStyles({"border-bottom":""});
            this.todoLi.setStyles({"border-bottom":""});
            this.doneLi.setStyles({"border-bottom":""});
            this.archiveLi.setStyles({"border-bottom":"2px solid #124c93"});
            this.createArchiveView();
        }else{
            this.drafterLi.setStyles({"border-bottom":""});
            this.todoLi.setStyles({"border-bottom":"2px solid #124c93"});
            this.doneLi.setStyles({"border-bottom":""});
            this.archiveLi.setStyles({"border-bottom":""});
            this.createTodoView();
        }
    },
    createSearchDiv:function(){
        this.rightSearchDiv = new Element("div.rightSearchDiv",{"styles":this.css.rightSearchDiv}).inject(this.contentDiv);

        rightSearchBarSpan = new Element("span.rightSearchBarSpan",{
            "styles":this.css.rightSearchBarSpan
        }).inject(this.rightSearchDiv);
        this.rightSearchBarInput = new Element("input",{
            "styles":this.css.rightSearchBarInput,
            "type":"text"
        }).inject(rightSearchBarSpan)
            .addEvents({
                "keyup": function(e){
                    if(e.code == 13){
                        this.searchAction();
                    }
                }.bind(this)
            });
        this.rightSearchBarSearch = new Element("div.rightSearchBarSearch",{
            "styles":this.css.rightSearchBarSearch,
            "text" : MWF.xApplication.Execution.LP.workTask.search
        }).inject(rightSearchBarSpan)
            .addEvents({
                "click":function(){
                    this.searchAction();
                }.bind(this)
            })
    },
    searchAction: function(){
        var filterData = {};
        filterData.title = this.rightSearchBarInput.get("value");
        if(this.drafterView) this.createDrafterView(filterData);
        if(this.todoView) this.createTodoView(filterData);
        if(this.doneView) this.createDoneView(filterData)
    },


    createRightContentDiv: function(){
        if(this.rightContentDiv)this.rightContentDiv.destroy();
        this.rightContentDiv = new Element("div.rightContentDiv",{
            "styles":this.css.rightContentDiv
        }).inject(this.contentDiv);


        //MWF.require("MWF.widget.ScrollBar", function () {
        //    new MWF.widget.ScrollBar(this.rightContentDiv, {
        //        "indent": false,
        //        "style": "xApp_TaskList",
        //        "where": "before",
        //        "distance": 30,
        //        "friction": 4,
        //        "axis": {"x": false, "y": true},
        //        "onScroll": function (y) {
        //            var scrollSize = this.rightContentDiv.getScrollSize();
        //            var clientSize = this.rightContentDiv.getSize();
        //            var scrollHeight = scrollSize.y - clientSize.y;
        //            var view = this.baseView || this.centerView;
        //            if (y + 200 > scrollHeight && view && view.loadElementList) {
        //                if (! view.isItemsLoaded) view.loadElementList();
        //            }
        //        }.bind(this)
        //    });
        //}.bind(this));
        this.createView( this.options.workNavi1 );

    },
    createTodoView: function(filterData){
        if( this.drafterView )delete this.drafterView;
        if( this.todoView )delete this.todoView;
        if( this.doneView )delete this.doneView;
        if( this.archiveView) delete this.archiveView;
        //if(this.rightContentDiv) this.rightContentDiv.empty();
        this.reloadRightContentDiv();
        this.rightContentDiv.setStyles({"height":this.app.middleContent.getSize().y-40-100+"px"});


        if(this.scrollBar && this.scrollBar.scrollVAreaNode){
            this.scrollBar.scrollVAreaNode.destroy()
        }
        MWF.require("MWF.widget.ScrollBar", function () {
            //if(this.scrollBar) this.scrollBar.destroy()
            this.scrollBar =  new MWF.widget.ScrollBar(this.rightContentDiv, {
                "indent": false,
                "style": "xApp_TaskList",
                "where": "before",
                "distance": 30,
                "friction": 4,
                "axis": {"x": false, "y": true},
                "onScroll": function (y) {
                    var scrollSize = this.rightContentDiv.getScrollSize();
                    var clientSize = this.rightContentDiv.getSize();
                    var scrollHeight = scrollSize.y - clientSize.y;
                    var view = this.todoView;
                    if (y + 200 > scrollHeight && view && view.loadElementList) {
                        if (! view.isItemsLoaded) view.loadElementList();
                    }
                }.bind(this)
            });
        }.bind(this),false);



        this.todoView =  new  MWF.xApplication.Execution.WorkReportList.todoView(this.rightContentDiv, this.app, this, { templateUrl : this.path+"listItem_todo.json",filterData: filterData },{lp: this.lp.view} );
        this.todoView.load();
    },
    createDrafterView: function(filterData){
        if( this.drafterView )delete this.drafterView;
        if( this.todoView )delete this.todoView;
        if( this.doneView )delete this.doneView;
        if( this.archiveView) delete this.archiveView;
        //if(this.rightContentDiv) this.rightContentDiv.empty();
        this.reloadRightContentDiv();
        this.rightContentDiv.setStyles({"height":this.app.middleContent.getSize().y-40-100+"px"});


        if(this.scrollBar && this.scrollBar.scrollVAreaNode){
            this.scrollBar.scrollVAreaNode.destroy()
        }
        MWF.require("MWF.widget.ScrollBar", function () {
            //if(this.scrollBar) this.scrollBar.destroy()
            this.scrollBar =  new MWF.widget.ScrollBar(this.rightContentDiv, {
                "indent": false,
                "style": "xApp_TaskList",
                "where": "before",
                "distance": 30,
                "friction": 4,
                "axis": {"x": false, "y": true},
                "onScroll": function (y) {
                    var scrollSize = this.rightContentDiv.getScrollSize();
                    var clientSize = this.rightContentDiv.getSize();
                    var scrollHeight = scrollSize.y - clientSize.y;
                    var view = this.drafterView;
                    if (y + 200 > scrollHeight && view && view.loadElementList) {
                        if (! view.isItemsLoaded) view.loadElementList();
                    }
                }.bind(this)
            });
        }.bind(this),false);




        this.drafterView =  new  MWF.xApplication.Execution.WorkReportList.DrafterView(this.rightContentDiv, this.app, this, {
            templateUrl : this.path+"listItem_drafter.json",
            filterData: filterData
        },{lp: this.lp.view} );
        this.drafterView.load();
    },
    reloadRightContentDiv : function(){
        if(this.rightContentDiv)this.rightContentDiv.destroy();
        this.rightContentDiv = new Element("div.rightContentDiv",{
            "styles":this.css.rightContentDiv
        }).inject(this.contentDiv);
    },
    createDoneView: function(filterData){
        if( this.drafterView )delete this.drafterView;
        if( this.todoView )delete this.todoView;
        if( this.doneView )delete this.doneView;
        if( this.archiveView) delete this.archiveView;
        //if(this.rightContentDiv) this.rightContentDiv.empty();
        this.reloadRightContentDiv();
        this.rightContentDiv.setStyles({"height":this.app.middleContent.getSize().y-40-100+"px"});


        if(this.scrollBar && this.scrollBar.scrollVAreaNode){
            this.scrollBar.scrollVAreaNode.destroy()
        }
        MWF.require("MWF.widget.ScrollBar", function () {
            //if(this.scrollBar) this.scrollBar.destroy()
            this.scrollBar =  new MWF.widget.ScrollBar(this.rightContentDiv, {
                "indent": false,
                "style": "xApp_TaskList",
                "where": "before",
                "distance": 30,
                "friction": 4,
                "axis": {"x": false, "y": true},
                "onScroll": function (y) {
                    var scrollSize = this.rightContentDiv.getScrollSize();
                    var clientSize = this.rightContentDiv.getSize();
                    var scrollHeight = scrollSize.y - clientSize.y;
                    if (y + 200 > scrollHeight && this.doneView && this.doneView.loadElementList) {
                        if (! this.doneView.isItemsLoaded) this.doneView.loadElementList();
                    }
                }.bind(this)
            });
        }.bind(this),false);

        this.doneView =  new  MWF.xApplication.Execution.WorkReportList.doneView(this.rightContentDiv, this.app, this, { templateUrl : this.path+"listItem_done.json",filterData: filterData },{lp: this.lp.view} )
        this.doneView.load();
    },
    createArchiveView: function(filterData){
        if( this.drafterView )delete this.drafterView;
        if( this.todoView )delete this.todoView;
        if( this.doneView )delete this.doneView;
        if( this.archiveView) delete this.archiveView;
        //if(this.rightContentDiv) this.rightContentDiv.empty();
        this.reloadRightContentDiv();
        this.rightContentDiv.setStyles({"height":this.app.middleContent.getSize().y-40-100+"px"});


        if(this.scrollBar && this.scrollBar.scrollVAreaNode){
            this.scrollBar.scrollVAreaNode.destroy()
        }
        MWF.require("MWF.widget.ScrollBar", function () {
            //if(this.scrollBar) this.scrollBar.destroy()
            this.scrollBar =  new MWF.widget.ScrollBar(this.rightContentDiv, {
                "indent": false,
                "style": "xApp_TaskList",
                "where": "before",
                "distance": 30,
                "friction": 4,
                "axis": {"x": false, "y": true},
                "onScroll": function (y) {
                    var scrollSize = this.rightContentDiv.getScrollSize();
                    var clientSize = this.rightContentDiv.getSize();
                    var scrollHeight = scrollSize.y - clientSize.y;
                    if (y + 200 > scrollHeight && this.archiveView && this.archiveView.loadElementList) {
                        if (! this.archiveView.isItemsLoaded) this.archiveView.loadElementList();
                    }
                }.bind(this)
            });
        }.bind(this),false);

        this.archiveView =  new  MWF.xApplication.Execution.WorkReportList.archiveView(this.rightContentDiv, this.app, this, { templateUrl : this.path+"listItem_archive.json",filterData: filterData },{lp: this.lp.view} );
        this.archiveView.load();
    }
});



MWF.xApplication.Execution.WorkReportList.DrafterView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkReportList.DrafterDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};

        this.actions.getWorkReportDrafterNext(id, count, filter,function(json){
            if (callback)callback(json);
            this.app.destroyShade();
        }.bind(this))

    },
    _removeDocument: function(documentData, all){
        this.actions.deleteWortReport(documentData.id, function(json){
            if(json.type && json.type == "success"){
                this.explorer.createDrafterView();
                this.app.notice(this.explorer.lp.prompt.deleteWortReport, "success");
            }

        }.bind(this),function(xhr,text,error){
            var errorText = error;
            if (xhr) errorMessage = xhr.responseText;
            var e = JSON.parse(errorMessage);
            if(e.message){
                this.app.notice( e.message,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        MWF.xDesktop.requireApp("Execution", "WorkReport", function(){

            var data = {
                workReportId : documentData.id,
                workId : documentData.workId
            };

            this.workReport = new MWF.xApplication.Execution.WorkReport(this, this.actions,data,{
                "isEdited":false,
                onReloadView : function( data ){
                    this.explorer.createDrafterView();
                }.bind(this)
            } );
            this.workReport.load();
        }.bind(this));

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

MWF.xApplication.Execution.WorkReportList.DrafterDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    action_open:function(){



    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if(itemNode.getElements("div[item='title']")){
            itemNode.getElements("div[item='title']").set("title",itemData.title)
        }
    },
    removeCenterWork : function(itemData){

        return false;
    }

});

MWF.xApplication.Execution.WorkReportList.todoView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkReportList.todoDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){

        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        this.actions.getWorkReportTodoNext(id, count, filter,function(json){
            if (callback)callback(json);
            this.app.destroyShade();
        }.bind(this))

    },
    _removeDocument: function(documentData, all){
        this.actions.deleteCenterWork(documentData.id, function(json){
            if(this.tabLocation == "centerDrafter"){
                this.app.workTask.loadCenterWorkList("drafter")
            }else if(this.tabLocation == "centerDeploy"){
                this.app.workTask.loadCenterWorkList("deploy")
            }

            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){


        MWF.xDesktop.requireApp("Execution", "WorkReport", function(){

            var data = {
                workReportId : documentData.workReportId,
                workId : documentData.workId
            };

            this.workReport = new MWF.xApplication.Execution.WorkReport(this, this.actions,data,{
                "isEdited":false,
                onReloadView : function( data ){
                    this.explorer.createTodoView();
                }.bind(this)
            } );
            this.workReport.load();
        }.bind(this));


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

MWF.xApplication.Execution.WorkReportList.todoDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    action_open:function(){


    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if(itemNode.getElements("div[item='title']")){
            itemNode.getElements("div[item='title']").set("title",itemData.title)
        }
    },
    removeCenterWork : function(itemData){

        return false;
    }

});

MWF.xApplication.Execution.WorkReportList.doneView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkReportList.doneDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){

        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        this.actions.getWorkReportDoneNext(id, count, filter,function(json){
            if (callback)callback(json);
            this.app.destroyShade();
        }.bind(this))

    },
    _removeDocument: function(documentData, all){
        this.actions.deleteCenterWork(documentData.id, function(json){
            if(this.tabLocation == "centerDrafter"){
                this.app.workTask.loadCenterWorkList("drafter")
            }else if(this.tabLocation == "centerDeploy"){
                this.app.workTask.loadCenterWorkList("deploy")
            }

            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        MWF.xDesktop.requireApp("Execution", "WorkReport", function(){

            var data = {
                workReportId : documentData.workReportId,
                workId : documentData.workId
            };

            this.workReport = new MWF.xApplication.Execution.WorkReport(this, this.actions,data,{
                "isEdited":false,
                onReloadView : function( data ){
                    this.explorer.createDoneView();
                }.bind(this)
            } );
            this.workReport.load();
        }.bind(this));


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

MWF.xApplication.Execution.WorkReportList.doneDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    action_open:function(){

        MWF.xDesktop.requireApp("Execution", "WorkDeploy", function(){
            var isEditedBool = (this.view.tabLocation == "centerDrafter" || this.view.tabLocation == "baseDrafter") ? true : false;

            this.workDeploy = new MWF.xApplication.Execution.WorkDeploy(this.view, this.view.app.restActions,{"id":this.data.id},{"isEdited":isEditedBool,"centerWorkId":this.data.id} );
            this.workDeploy.load();

        }.bind(this))


    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if(itemNode.getElements("div[item='title']")){
            itemNode.getElements("div[item='title']").set("title",itemData.title)
        }
    },
    removeCenterWork : function(itemData){

        return false;
    }

});


MWF.xApplication.Execution.WorkReportList.archiveView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkReportList.archiveDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){

        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        this.actions.getWorkReportArchiveNext(id, count, filter,function(json){
            if (callback)callback(json);
            this.app.destroyShade();
        }.bind(this))

    },
    _removeDocument: function(documentData, all){
        this.actions.deleteCenterWork(documentData.id, function(json){
            if(this.tabLocation == "centerDrafter"){
                this.app.workTask.loadCenterWorkList("drafter")
            }else if(this.tabLocation == "centerDeploy"){
                this.app.workTask.loadCenterWorkList("deploy")
            }

            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        MWF.xDesktop.requireApp("Execution", "WorkReport", function(){

            var data = {
                workReportId : documentData.workReportId,
                workId : documentData.workId
            };

            this.workReport = new MWF.xApplication.Execution.WorkReport(this, this.actions,data,{
                "isEdited":false,
                onReloadView : function( data ){
                    this.explorer.createDoneView();
                }.bind(this)
            } );
            this.workReport.load();
        }.bind(this));


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

MWF.xApplication.Execution.WorkReportList.archiveDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    action_open:function(){

        MWF.xDesktop.requireApp("Execution", "WorkDeploy", function(){
            var isEditedBool = (this.view.tabLocation == "centerDrafter" || this.view.tabLocation == "baseDrafter") ? true : false

            this.workDeploy = new MWF.xApplication.Execution.WorkDeploy(this.view, this.view.app.restActions,{"id":this.data.id},{"isEdited":isEditedBool,"centerWorkId":this.data.id} );
            this.workDeploy.load();

        }.bind(this))


    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if(itemNode.getElements("div[item='title']")){
            itemNode.getElements("div[item='title']").set("title",itemData.title)
        }
    },
    removeCenterWork : function(itemData){

        return false;
    }

});
