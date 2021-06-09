MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.require("MWF.widget.Common", null, false);
MWF.xDesktop.requireApp("Template","Explorer", null, false);
MWF.xDesktop.requireApp("query.Query", "lp."+o2.language, null, false);
MWF.xApplication.query.Query.ImporterRecord = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "importerId": "",
        "style": "default",
        "resizeNode": true,
        "viewPageNum" : 1
    },
    initialize: function (container, app, options) {
        this.setOptions( options );
        this.container = container;

        this.path = "../x_component_query_Query/$ImporterRecord/";
        this.cssPath = "../x_component_query_Query/$ImporterRecord/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.app = app;
        this.lp = MWF.xApplication.query.Query.LP;
    },
    load: function () {
        debugger;
        this.container.empty();

        this.actionbarAreaNode =  new Element("div.actionbarAreaNode", {"styles": this.css.actionbarAreaNode}).inject(this.container);

        this.viewContainerTop = Element("div",{
            "styles" : this.css.viewContainerTop
        }).inject(this.container);

        this.viewContainer = Element("div",{
            "styles" : this.css.viewContainer
        }).inject(this.container);


        //this.loadTopView();

        this.loadToolbar();
        this.loadView();

        if (this.options.resizeNode){
            this.setContentHeightFun = this.setContentHeight.bind(this);
            this.container.addEvent("resize", this.setContentHeightFun);
            // this.setContentHeightFun();
        }
    },
    setContentHeight: function(){
        var size = this.container.getSize();
        var h = size.y;
        if( this.actionbarAreaNode ){
            var exportSize = this.actionbarAreaNode.getComputedSize();
            h = h-exportSize.totalHeight;
        }
        var pageSize = this.view.pagingContainerBottom.getComputedSize();
        h = h-pageSize.totalHeight;
        this.view.viewWrapNode.setStyles({
            "height": ""+h+"px",
            "overflow": "auto"
        });
    },
    destroy : function(){
        if(this.resizeWindowFun)this.app.removeEvent("resize",this.resizeWindowFun);
        this.view.destroy();
    },
    loadToolbar: function(){
        MWF.require("MWF.widget.Toolbar", function(){
            this.toolbar = new MWF.widget.Toolbar(this.actionbarAreaNode, {"style": "simple"}, this); //this.exportAreaNode

            var doImportActionNode = new Element("div", {
                "id": "",
                "MWFnodetype": "MWFToolBarButton",
                "MWFButtonImage": this.path+""+this.options.style+"/icon/upload1.png",
                "title": this.lp.importData,
                "MWFButtonAction": "doImport",
                "MWFButtonText": this.lp.importData
            }).inject(this.actionbarAreaNode); //this.exportAreaNode

            var downLoadActionNode = new Element("div", {
                "id": "",
                "MWFnodetype": "MWFToolBarButton",
                "MWFButtonImage": this.path+""+this.options.style+"/icon/download1.png",
                "title": this.lp.downloadTemplate,
                "MWFButtonAction": "downloadTemplate",
                "MWFButtonText": this.lp.downloadTemplate
            }).inject(this.actionbarAreaNode); //this.exportAreaNode

            this.toolbar.load();
        }.bind(this));
    },
    loadView : function(){

        //this.resizeWindow();
        //this.resizeWindowFun = this.resizeWindow.bind(this)
        //this.app.addEvent("resize", this.resizeWindowFun );
       var  _self = this;
        this.view = new MWF.xApplication.query.Query.ImporterRecord.View( this.viewContainer, this.app, this, {
            templateUrl : this.path+this.options.style+"/listItem.json",
            pagingEnable : true,
            wrapView: true,
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
                },
                onPostLoad: function () {
                    _self.setContentHeight();
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
    doImport: function(){
        MWF.xDesktop.requireApp("query.Query", "Importer", function () {
            var importer = new MWF.xApplication.query.Query.Importer( this.container, {
                "id": this.options.importerId
            }, {
                "onAfterImport": function () {
                    if(this.view)this.view.reload();
                }.bind(this)
            }, this.app);
            importer.load();
        }.bind(this));
    },
    downloadTemplate: function(){
        MWF.xDesktop.requireApp("query.Query", "Importer", function () {
            var importer = new MWF.xApplication.query.Query.Importer(this.container, {
                "id": this.options.importerId
            }, {}, this.app);
            importer.downloadTemplate();
        }.bind(this));
    }
});

MWF.xApplication.query.Query.ImporterRecord.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data, index){
        return new MWF.xApplication.query.Query.ImporterRecord.ViewLine(this.viewNode, data, this.explorer, this, null,  index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.clearBody();
        if(!count)count=30;
        if(!pageNum)pageNum = 1;
        var filter = { "modelId": this.explorer.options.importerId };

        //filter.withTopSubject = false;
        o2.Actions.load("x_query_assemble_surface").ImportModelAction.recordListPaging( pageNum, count, filter, function(json){
            if( !json.data )json.data = [];
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        // this.actions.deleteSubject(documentData.id, function(json){
        //         //     this.reload();
        //         //     this.app.notice(this.explorer.lp.deleteDocumentOK, "success");
        //         // }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData,index ){
        var detail = new MWF.xApplication.query.Query.ImporterRecord.Detail(
                this.explorer.container,
                this.app,
                { recordId: documentData.id }
            )
        detail.load();
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

MWF.xApplication.query.Query.ImporterRecord.ViewLine = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    },
    open: function (e) {
        this.view._openDocument(this.data, this.index);
    },
    edit : function(){

    }
});

MWF.xApplication.query.Query.ImporterRecord.Detail = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "recordId" : ""
    },
    initialize: function( container, app, options ){
        this.setOptions( options );
        this.container = container;

        this.path = "../x_component_query_Query/$ImporterRecord/";
        this.cssPath = "../x_component_query_Query/$ImporterRecord/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.app = app;
        this.lp = MWF.xApplication.query.Query.LP;
    },
    load: function () {
        o2.Actions.load("x_query_assemble_surface").ImportModelAction.getRecord(this.options.recordId, function (json) {
            this.data = json.data;
            this.createNode();
            this.setBaseInfor();
            if( this.data.status === "部分成功" ){
                this.createTab();
            }
            this.openDlg();
        }.bind(this))
    },
    createNode: function(){
        this.node = new Element("div", { style : "padding:0px;"});
        this.inforNode = new Element("div", { style : "padding:0px;"}).inject(this.node);
        if( this.data.status === "部分成功" ){
            this.tabNode = new Element("div", { "styles": this.css.tabNode }).inject(this.node);
        }
        this.viewNode = new Element("div", { style : "padding:0px;"}).inject(this.node);
    },
    createTab: function(){
        [
            {"text": this.lp.importStatusList[0], "value": ""},
            {"text": this.lp.importStatusList[1], "value": "导入成功"},
            {"text": this.lp.importStatusList[2], "value": "导入失败"}
         ].each(function (obj, i) {
            var tabItemNode = new Element("div", {
                "styles": this.css.tabItemNode,
                "text": obj.text
            }).inject( this.tabNode );
            tabItemNode.addEvent("click", function () {
                if(this.currentTabItem)this.currentTabItem.setStyles(this.css.tabItemNode);
                this.currentTabItem = tabItemNode;
                tabItemNode.setStyles(this.css.currentTabItemNode);
                this.currentStatus = obj.value;
                this.view.reload();
            }.bind(this));
            if(i===0){
                this.currentTabItem = tabItemNode;
                tabItemNode.setStyles(this.css.currentTabItemNode);
            }
        }.bind(this))

    },
    openDlg: function () {
        var _self = this;
        var opt = {
            "style" : "user",
            "title": this.lp.importRecordDetail,
            "content": this.node,
            "offset": {"y": 0},
            "isMax": true,
            "width": 1000,
            "height": 750,
            "buttonList": [
                {
                    "type": "exportWithError",
                    "text": this.lp.exportErrorDataToExcel,
                    "action": function () { _self.exportWithImportDataToExcel(); }
                },
                {
                    "type": "cancel",
                    "text": this.lp.close,
                    "action": function () { this.dlg.close(); }.bind(this)
                }
            ],
            "onResizeCompleted": function () { this.setContentHeight(); }.bind(this),
            "onMax": function () { this.setContentHeight(); }.bind(this),
            "onRestore": function () { this.setContentHeight(); }.bind(this),
            "onPostShow": function () {
                // if( this.dlg.content.getScrollSize().y - this.dlg.content.getSize().y > 15 ){
                //     this.dlg.content.setStyles("padding-right","0px");
                // }
                this.content.setStyle("overflow","hidden");
                _self.loadView();
            },
            "onPostClose": function(){
                this.dlg = null;
            }.bind(this)
        };
        if( this.data.status === "导入成功" ){
            opt.buttonList.splice(0, 1);
        }
        this.dlg = o2.DL.open(opt);
    },
    setContentHeight: function(){
        var size = this.dlg.content.getSize();
        var h = size.y;
        if( this.inforNode ){
            var inforNodeSize = this.inforNode.getComputedSize();
            h = h-inforNodeSize.totalHeight;
        }
        if( this.tabNode ){
            var tabSize = this.tabNode.getComputedSize();
            h = h-tabSize.totalHeight;
        }
        this.view.viewWrapNode.setStyles({
            "height": ""+h+"px",
            "overflow": "auto"
        });
    },
    objectToString: function (obj, type) {
        if(!obj)return "";
        var arr = [];
        Object.each(obj,  function (value, key) {
            if( type === "style" ){
                arr.push( key + ":"+ value +";" )
            }else{
                arr.push( key + "='"+ value +"'" )
            }
        })
        return arr.join( " " )
    },
    setBaseInfor: function () {
        var objectToString = this.objectToString;

        var htmlArray = ["<table "+ objectToString( this.css.properties ) +" style='"+objectToString( this.css.tableStyles, "style" )+"'>"];

        var titleStyle = objectToString( this.css.titleStyles, "style" );

        var contentStyles = Object.clone( this.css.contentStyles );
        if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
        var contentStyle = objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

        htmlArray.push( "<tr>" );

        htmlArray.push( "<th style='"+titleStyle+" width:100px;'> "+ this.lp.importerName +"</th>" );
        htmlArray.push( "<td style='"+contentStyle+"'> "+ this.data.name +"</td>" );

        htmlArray.push( "<th style='"+titleStyle+"'> "+ this.lp.status +"</th>" );
        htmlArray.push( "<td style='"+contentStyle+"'> "+ this.data.status +"</td>" );

        htmlArray.push( "<th style='"+titleStyle+"'> "+ this.lp.importCount +"</th>" );
        htmlArray.push( "<td style='"+contentStyle+"'> "+ this.data.count +"</td>" );

        htmlArray.push( "</tr>" );

        htmlArray.push( "<tr>" );

        htmlArray.push( "<th style='"+titleStyle+"'> "+ this.lp.importTime +"</th>" );
        htmlArray.push( "<td style='"+contentStyle+"'> "+ this.data.createTime +"</td>" );

        htmlArray.push( "<th style='"+titleStyle+"'> "+ this.lp.updateTime +"</th>" );
        htmlArray.push( "<td style='"+contentStyle+"'> "+ this.data.updateTime +"</td>" );

        htmlArray.push( "<th style='"+titleStyle+"'> "+ this.lp.failCount +"</th>" );
        htmlArray.push( "<td style='"+contentStyle+"'> "+ (o2.typeOf(this.data.failCount)==="null"?"":this.data.failCount) +"</td>" );

        htmlArray.push( "</tr>" );

        htmlArray.push( "</table>" );

        this.inforNode.set("html", htmlArray.join(""));
    },
    loadView: function(){
        this.view = new MWF.xApplication.query.Query.ImporterRecord.DetailView( this.viewNode, this.app, this, {
            templateUrl : this.path+this.options.style+"/detailListItem.json",
            pagingEnable : true,
            wrapView: true,
            onPostCreateViewBody : function(){
                this.app.fireEvent("postCreateViewBody");
            }.bind(this),
            onPostReloadLoad: function () { this.setContentHeight() }.bind(this),
            pagingPar : {
                pagingBarUseWidget: true,
                position : [ "bottom" ],
                style : "blue_round",
                hasReturn : false,
                currentPage : this.options.viewPageNum,
                countPerPage : 15,
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
                },
                onPostLoad: function () { this.setContentHeight() }.bind(this)
            }
        } );
        this.view.pagingContainerBottom = new Element("div", {"styles":{"float":"left"}}).inject(this.dlg.button);
        this.view.load();
    }
});

MWF.xApplication.query.Query.ImporterRecord.DetailView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data, index){
        return new MWF.xApplication.query.Query.ImporterRecord.DetailViewLine(this.viewNode, data, this.explorer, this, null,  index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        debugger;
        this.clearBody();
        if(!count)count=30;
        if(!pageNum)pageNum = 1;
        var filter = {"recordId": this.explorer.options.recordId};
        if( this.explorer.currentStatus )filter.status = this.explorer.currentStatus;

        //filter.withTopSubject = false;
        o2.Actions.load("x_query_assemble_surface").ImportModelAction.recordItemListPaging( pageNum, count, filter, function(json){
            if( !json.data )json.data = [];
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        // this.actions.deleteSubject(documentData.id, function(json){
        //         //     this.reload();
        //         //     this.app.notice(this.explorer.lp.deleteDocumentOK, "success");
        //         // }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( d, e ){
        if (d.docType==="cms"){
            this.openCms(d, e)
        }else if (d.docType==="process"){
            this.openWorkAndCompleted(d, e)
        }
    },
    openCms: function(d, e){
        var options = {"documentId": d.docId};
        layout.desktop.openApplication(e, "cms.Document", options);
    },
    openWorkAndCompleted: function(d, e){
        MWF.Actions.get("x_processplatform_assemble_surface").listWorkByJob(this.data.bundle, function(json){
            var workCompletedCount = json.data.workCompletedList.length;
            var workCount = json.data.workList.length;
            var count = workCount+workCompletedCount;
            if (count===1){
                if (workCompletedCount) {
                    this.openWorkCompleted(json.data.workCompletedList[0].id, e);
                }else{
                    this.openWork(json.data.workList[0].id, e);
                }
            }else if (count>1){
                var worksAreaNode = this.createWorksArea();
                json.data.workCompletedList.each(function(work){
                    this.createWorkCompletedNode(work, worksAreaNode);
                }.bind(this));
                json.data.workList.each(function(work){
                    this.createWorkNode(work, worksAreaNode);
                }.bind(this));
                this.showWorksArea(worksAreaNode, e);
            }else{

            }
        }.bind(this));
    },
    createWorkNode: function(work, worksAreaNode){
        var worksAreaContentNode = worksAreaNode.getLast();
        var node = new Element("div", {"styles": this.css.workAreaNode}).inject(worksAreaContentNode);
        var actionNode = new Element("div", {"styles": this.css.workAreaActionNode, "text": this.view.lp.open}).inject(node);

        actionNode.store("workId", work.id);
        actionNode.addEvent("click", function(e){
            this.openWork(e.target.retrieve("workId"), e);
            this.mask.hide();
            worksAreaNode.destroy();
        }.bind(this));

        var areaNode = new Element("div", {"styles": this.css.workAreaLeftNode}).inject(node);

        var titleNode = new Element("div", {"styles": this.css.workAreaTitleNode, "text": work.title}).inject(areaNode);
        var contentNode = new Element("div", {"styles": this.css.workAreaContentNode}).inject(areaNode);
        new Element("div", {"styles": this.css.workAreaContentTitleNode, "text": this.view.lp.activity+": "}).inject(contentNode);
        new Element("div", {"styles": this.css.workAreaContentTextNode, "text": work.activityName}).inject(contentNode);

        var taskUsers = [];
        MWF.Actions.get("x_processplatform_assemble_surface").listTaskByWork(work.id, function(json){
            json.data.each(function(task){
                taskUsers.push(MWF.name.cn(task.person));
            }.bind(this));
            new Element("div", {"styles": this.css.workAreaContentTitleNode, "text": this.view.lp.taskPeople+": "}).inject(contentNode);
            new Element("div", {"styles": this.css.workAreaContentTextNode, "text": taskUsers.join(", ")}).inject(contentNode);
        }.bind(this));
    },
    createWorkCompletedNode: function(work, worksAreaNode){
        var worksAreaContentNode = worksAreaNode.getLast();

        var node = new Element("div", {"styles": this.css.workAreaNode}).inject(worksAreaContentNode);
        var actionNode = new Element("div", {"styles": this.css.workAreaActionNode, "text": this.view.lp.open}).inject(node);

        actionNode.store("workId", work.id);
        actionNode.addEvent("click", function(e){
            this.mask.hide();
            var id = e.target.retrieve("workId");
            worksAreaNode.destroy();
            this.openWorkCompleted(id, e);
        }.bind(this));

        var areaNode = new Element("div", {"styles": this.css.workAreaLeftNode}).inject(node);

        var titleNode = new Element("div", {"styles": this.css.workAreaTitleNode, "text": work.title}).inject(areaNode);
        var contentNode = new Element("div", {"styles": this.css.workAreaContentNode}).inject(areaNode);

        new Element("div", {"styles": this.css.workAreaContentTitleNode, "text": this.view.lp.activity+": "}).inject(contentNode);
        new Element("div", {"styles": this.css.workAreaContentTextNode, "text": this.view.lp.processCompleted}).inject(contentNode);
    },
    createWorksArea: function(){
        var worksAreaNode = new Element("div", {"styles": this.css.worksAreaNode});
        var worksAreaTitleNode = new Element("div", {"styles": this.css.worksAreaTitleNode}).inject(worksAreaNode);
        var worksAreaTitleCloseNode = new Element("div", {"styles": this.css.worksAreaTitleCloseNode}).inject(worksAreaTitleNode);
        worksAreaTitleCloseNode.addEvent("click", function(e){
            this.mask.hide();
            e.target.getParent().getParent().destroy();
        }.bind(this));
        var worksAreaContentNode = new Element("div", {"styles": this.css.worksAreaContentNode}).inject(worksAreaNode);

        return worksAreaNode;
    },
    showWorksArea: function(node, e){
        MWF.require("MWF.widget.Mask", null, false);
        this.mask = new MWF.widget.Mask({"style": "desktop", "loading": false});
        this.mask.loadNode(this.view.container);

        node.inject(this.view.node);
        this.setWorksAreaPosition(node, e.target);
    },
    setWorksAreaPosition: function(node, td){
        var p = td.getPosition(this.view.container);
        var containerS = this.view.container.getSize();
        var containerP = this.view.container.getPosition(this.view.container.getOffsetParent());
        var s = node.getSize();
        var offX = p.x+s.x-containerS.x;
        offX = (offX>0) ? offX+20 : 0;
        var offY = p.y+s.y-containerS.y;
        offY = (offY>0) ? offY+5 : 0;

        node.position({
            "relativeTo": td,
            "position": "lefttop",
            "edge": "lefttop",
            "offset": {
                "x": 0-offX,
                "y": 0-offY
            }
        });
    },
    openWork: function(id, e){
        var options = {"workId": id};
        layout.desktop.openApplication(e, "process.Work", options);
    },
    openWorkCompleted: function(id, e){
        var options = {"workCompletedId": id};
        layout.desktop.openApplication(e, "process.Work", options);
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

MWF.xApplication.query.Query.ImporterRecord.DetailViewLine = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    },
    open: function (e) {
        this.view._openDocument(this.data, e);
    },
    edit : function(){
    }
});
