MWF.xApplication.Execution = MWF.xApplication.Execution || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.Execution.WorkList = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "workNavi1" : "",
        "workNavi2" : ""
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp.workList;
        this.path = "/x_component_Execution/$WorkList/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
        this.actionProcess = MWF.Actions.get("x_processplatform_assemble_surface");
    },
    loadCss: function () {
        this.cssPath = "/x_component_Execution/$WorkList/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        //alert(this.options.workNavi1);alert(this.options.workNavi2)
        if(!this.appointSwitch){
            var switchData = {};
            switchData.configCode = "WORK_AUTHORIZE";
            this.actions.getProfileByCode(switchData,function(json){
                if(json.type == "success"){
                    if(json.data && json.data.configValue){
                        this.appointSwitch = json.data.configValue;
                    }
                }
            }.bind(this),null,false);
        }

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
        if( this.naviDiv)this.naviDiv.setStyles({"height":(size.y-60)+"px"});
        if(this.naviContentDiv)this.naviContentDiv.setStyles({"height":(size.y-180)+"px"});
        if(this.contentDiv)this.contentDiv.setStyles({"height":(size.y-60)+"px"});
        if(this.rightContentDiv)this.rightContentDiv.setStyles({
            "height":(size.y-40-140)+"px",
            "width":(size.x-this.naviDiv.getSize().x-6)+"px"
        });
    },
    createNaviContent: function(){
        this.naviDiv = new Element("div.naviDiv",{
            "styles":this.css.naviDiv
        }).inject(this.middleContent);

        this.naviTitleDiv = new Element("div.naviTitleDiv",{
            "styles":this.css.naviTitleDiv,
            "text":this.lp.navi.title
        }).inject(this.naviDiv);
        this.naviContentDiv = new Element("div.naviContentDiv",{"styles":this.css.naviContentDiv}).inject(this.naviDiv);
        this.naviBottomDiv = new Element("div.naviBottomDiv",{"styles":this.css.naviBottomDiv}).inject(this.naviDiv);

        var naviContentLi = new Element("li.naviContentLi",{"styles":this.css.naviContentLi}).inject(this.naviContentDiv)
            .addEvents({
                "click":function(){
                    this.app.openWorkReport()
                }.bind(this)
            });
        var naviContentImg = new Element("img.naviContentImg",{
            "styles":this.css.naviContentImg,
            "src":this.path+"default/icon/Prototype-100.png"
        }).inject(naviContentLi);
        var naviContentSpan = new Element("span.naviContentSpan",{
            "styles":this.css.naviContentSpan,
            "text":this.lp.navi.items.workReport
        }).inject(naviContentLi)

    },
    createContentDiv: function(){
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.middleContent);
        this.createCategoryItemDiv();

        this.clickWorkListNavi( this.options.workNavi1 || "base", this.options.workNavi2 || "" );
    },
    createCategoryItemDiv: function(){
        this.rightCategoryDiv = new Element("div.rightCategoryDiv",{"styles":this.css.rightCategoryDiv}).inject(this.contentDiv);

        var isCreate = false;
        this.actions.createCenterWorkAuthorization(function(json){
            if(json.data && json.data.value){
                isCreate = json.data.value;
            }
        }.bind(this),null,false);

        if(isCreate){
            this.rightCategoryNewDiv = new Element("div.rightCategoryNewDiv",{
                "styles":this.css.rightCategoryNewDiv,
                "text":this.lp.createWork
            }).inject(this.rightCategoryDiv)
                .addEvents({
                "click":function(){
                    MWF.xDesktop.requireApp("Execution", "WorkDeploy", function(){
                        this.explorer = new MWF.xApplication.Execution.WorkDeploy(this, this.actions,{},{
                            "isEdited":true,
                            "isNew":true,
                            "onReloadView" : function( data ){
                                this.createRightContentDiv(this.workNavi1,this.workNavi2)
                            }.bind(this)
                        });
                        this.explorer.load();
                    }.bind(this))

                }.bind(this)
                });
        }


        this.rightCategoryItemDiv = new Element("div.rightCategoryItemDiv",{"styles":this.css.rightCategoryItemDiv}).inject(this.rightCategoryDiv);

        this.centerWorkLi = new Element("li.centerWorkLi",{
            "styles":this.css.rightCategoryItemCurrentLi,
            "text": this.lp.workItems.centerWork.title
        }).inject(this.rightCategoryItemDiv)
            .addEvents({
                "click":function(){
                    //alert("中心工作点击");
                    this.clickWorkListNavi("center")
                }.bind(this)
            });
        this.baseWorkLi = new Element("li.baseWorkLi",{
            "styles":this.css.rightCategoryItemLi,
            "text": this.lp.workItems.baseWork.title
        }).inject(this.rightCategoryItemDiv)
            .addEvents({
                "click":function(){
                    //alert("具体工作点击");
                    this.clickWorkListNavi("base");

                }.bind(this)
            });

        this.rightSearchDiv = new Element("div.rightSearchDiv",{"styles":this.css.rightSearchDiv}).inject(this.contentDiv);
    },
    clickWorkListNavi : function( workNavi1, workNavi2 ){
        this.workNavi1 = workNavi1 || "base";

        if(this.rightSearchDiv)this.rightSearchDiv.empty();
        if(this.rightContentDiv)this.rightContentDiv.destroy();
        if( workNavi1 == "base" ){
            if(this.centerWorkLi)this.centerWorkLi.setStyles({"border-bottom":""});
            if(this.baseWorkLi)this.baseWorkLi.setStyles({"border-bottom":"2px solid #124c93"});
            this.createBaseWorkSearchDiv();
            this.createRightContentDiv( "base" , workNavi2 || "" );
        }else{
            if(this.centerWorkLi)this.centerWorkLi.setStyles({"border-bottom":"2px solid #124c93"});
            if(this.baseWorkLi)this.baseWorkLi.setStyles({"border-bottom":""});
            this.createCenterWorkSearchDiv();
            this.createRightContentDiv("center", workNavi2 || "" );
        }

    },


    createCenterWorkSearchDiv: function(){
        this.rightDrafterTabLi = new Element("li.rightDrafterTabLi", {
            "styles": this.css.rightDrafterTabLi,
            "text" : MWF.xApplication.Execution.LP.workTask.centerWorkDrafter
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickCenterWorkTaskNavi("drafter")
                }.bind(this)
            });
        this.rightDeployTabLi = new Element("li.rightDeployTabLi", {
            "styles": this.css.rightDeployTabLi,
            "text" :MWF.xApplication.Execution.LP.workTask.centerWorkDeploy
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickCenterWorkTaskNavi("deploy")
                }.bind(this)
            });
        this.rightArchiveTabLi = new Element("li.rightArchiveTabLi", {
            "styles": this.css.rightArchiveTabLi,
            "text" :MWF.xApplication.Execution.LP.workTask.centerWorkArchive
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickCenterWorkTaskNavi("archive")
                }.bind(this)
            });

        rightSearchBarSpan = new Element("span.rightSearchBarSpan",{
            "styles":this.css.rightSearchBarSpan
        }).inject(this.rightSearchDiv);
        this.rightSearchBarInput = new Element("input.rightSearchBarInput",{
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
    clickCenterWorkTaskNavi : function( str ){
        if(this.rightSearchBarInput) this.rightSearchBarInput.set("value","");
        this.workNavi1 = "center";
        this.workNavi2 = str || "deploy";
        if( str == "drafter" ){
            this.rightContentDiv.empty();
            if(this.rightDrafterTabLi)this.rightDrafterTabLi.setStyles({"border-bottom":"2px solid #124c93"});
            if(this.rightDeployTabLi)this.rightDeployTabLi.setStyles({"border-bottom":""});
            if(this.rightArchiveTabLi)this.rightArchiveTabLi.setStyles({"border-bottom":""});
            this.loadCenterWorkList("drafter");
        }else if(str=="archive"){
            this.rightContentDiv.empty();
            if(this.rightArchiveTabLi)this.rightArchiveTabLi.setStyles({"border-bottom":"2px solid #124c93"});
            if(this.rightDeployTabLi)this.rightDeployTabLi.setStyles({"border-bottom":""});
            if(this.rightDrafterTabLi)this.rightDrafterTabLi.setStyles({"border-bottom":""});
            this.loadCenterWorkList("archive");
        }else{
            this.rightContentDiv.empty();
            if(this.rightDrafterTabLi)this.rightDrafterTabLi.setStyles({"border-bottom":""});
            if(this.rightArchiveTabLi)this.rightArchiveTabLi.setStyles({"border-bottom":""});
            if(this.rightDeployTabLi)this.rightDeployTabLi.setStyles({"border-bottom":"2px solid #124c93"});
            this.loadCenterWorkList("deploy");
        }
    },

    createBaseWorkSearchDiv:function(){
        //全部工作
        this.baseAllTabLi = new Element("li.baseAllTabLi", {
            "styles": this.css.baseTabLi,
            "text" : this.lp.baseWorkCategory.all
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myAll")
                }.bind(this)
            });

        //草稿
        this.baseDrafterTabLi = new Element("li.baseDrafterTabLi", {
            "styles": this.css.baseTabLi,
            "text" : this.lp.baseWorkCategory.myDrafter
        }).inject(this.rightSearchDiv)
        .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myDrafter")
                }.bind(this)
            });
        //我部署的
        this.baseDeployTabLi = new Element("li.baseDeployTabLi", {
            "styles": this.css.baseTabLi,
            "text" : this.lp.baseWorkCategory.myDeploy
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myDeploy");
                }.bind(this)
            });

        //我负责的
        this.baseDoTabLi = new Element("li.baseDoTabLi", {
            "styles": this.css.baseTabLi,
            "text" : this.lp.baseWorkCategory.myDo
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myDo");
                }.bind(this)
            });
        //我协助的
        this.baseAssistTabLi = new Element("li.baseAssistTabLi", {
            "styles": this.css.baseTabLi,
            "text" : this.lp.baseWorkCategory.myAssist
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myAssist");
                }.bind(this)
            });
        //我阅知的
        this.baseReadTabLi = new Element("li.baseReadTabLi", {
            "styles": this.css.baseTabLi,
            "text" : this.lp.baseWorkCategory.myRead
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myRead");
                }.bind(this)
            });

        if(this.appointSwitch && this.appointSwitch.toUpperCase() == "OPEN"){
            //我委派的
            this.baseAppointTabLi = new Element("li.baseAppointTabLi", {
                "styles": this.css.baseTabLi,
                "text" : this.lp.baseWorkCategory.myAppoint
            }).inject(this.rightSearchDiv)
                .addEvents({
                    "click":function(){
                        this.clickBaseWorkTaskNavi("myAppoint");
                    }.bind(this)
                })

        }

        //已归档
        this.baseArchiveTabLi = new Element("li.baseArchiveTabLi", {
            "styles": this.css.baseTabLi,
            "text" : this.lp.baseWorkCategory.myArchive
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myArchive");
                }.bind(this)
            });



        rightSearchBarSpan = new Element("span.rightSearchBarSpan",{
            "styles":this.css.rightSearchBarSpan
        }).inject(this.rightSearchDiv);


        this.rightSearchBarInput = new Element("input.input",{
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
            "text" : this.lp.searchButton.search
        }).inject(rightSearchBarSpan)
            .addEvents({
                "click":function(){
                    this.searchAction();
                }.bind(this)
            })

    },
    clickBaseWorkTaskNavi : function( str ){
        if(this.rightSearchBarInput) this.rightSearchBarInput.set("value","");
        this.workNavi1 = "base";
        this.workNavi2 = str || "myDo";
        if( str == "myDrafter" ) {
            this.changeBaseWork(this.baseDrafterTabLi);
            this.loadBaseWorkList("myDrafter");
        }else if(str =="myDeploy"){
                this.changeBaseWork(this.baseDeployTabLi);
                this.loadBaseWorkList("myDeploy");
        }else if( str == "myDo" ){
            this.changeBaseWork(this.baseDoTabLi);
            this.loadBaseWorkList("myDo");
        }else if( str == "myAssist" ){
            this.changeBaseWork(this.baseAssistTabLi);
            this.loadBaseWorkList("myAssist");
        }else if( str == "myRead" ){
            this.changeBaseWork(this.baseReadTabLi);
            this.loadBaseWorkList("myRead");
        }else if( str == "myAppoint") {
            this.changeBaseWork(this.baseAppointTabLi);
            this.loadBaseWorkList("myAppoint");
        }else if( str == "myArchive"){
            this.changeBaseWork(this.baseArchiveTabLi);
            this.loadBaseWorkList("myArchive");
        }else if(str == "myAll"){
            this.changeBaseWork(this.baseAllTabLi);
            this.loadBaseWorkList("myAll");
        }else{
            this.changeBaseWork(this.baseDoTabLi);
            this.loadBaseWorkList("myDo");
        }
    },
    reloadRightContentDiv : function(){
        if(this.rightContentDiv)this.rightContentDiv.destroy();
        this.rightContentDiv = new Element("div.rightContentDiv",{
            "styles":this.css.rightContentDiv
        }).inject(this.contentDiv);
        this.rightContentDiv.setStyles({
            "width":(this.app.middleContent.getSize().x-this.naviDiv.getSize().x-6)+"px"
        });
    },
    createRightContentDiv: function(workNavi1 , workNavi2 ){
        //alert(this.app.middleContent.getSize()-40-140+"px")
        if(this.rightContentDiv)this.rightContentDiv.destroy();
        this.rightContentDiv = new Element("div.rightContentDiv",{
            "styles":this.css.rightContentDiv
        }).inject(this.contentDiv);

        if(workNavi1=="base"){
            this.clickBaseWorkTaskNavi( workNavi2 );
        }else{
            this.clickCenterWorkTaskNavi( workNavi2 );
        }


    },
    createTableContent:function(json,colsJson){

    },
    loadCenterWorkList: function (str,filter) {
        if( this.baseView )delete this.baseView;
        //if(this.rightContentDiv) this.rightContentDiv.empty();
        this.reloadRightContentDiv();
        this.rightContentDiv.setStyles({"height":this.app.middleContent.getSize().y-40-140+"px"});

        //if(this.scrollBar && this.scrollBar.scrollVAreaNode){
        //    this.scrollBar.scrollVAreaNode.destroy()
        //}


        //MWF.require("MWF.widget.ScrollBar", function () {
        //    this.scrollBar =  new MWF.widget.ScrollBar(this.rightContentDiv, {
        //        "indent": false,
        //        "style": "default",
        //        "where": "before",
        //        "distance": 100,
        //        "friction": 4,
        //        "axis": {"x": false, "y": true},
        //        "onScroll": function (y) {
        //            var scrollSize = this.rightContentDiv.getScrollSize();
        //            var clientSize = this.rightContentDiv.getSize();
        //            var scrollHeight = scrollSize.y - clientSize.y;
        //            var view = this.baseView || this.centerView;
        //            if (y + 200 > scrollHeight && view && view.loadElementList) {
        //                if (! view.isItemsLoaded) view.loadElementList()
        //            }
        //        }.bind(this)
        //    });
        //}.bind(this),false);

        var templateUrl = this.path+"centerWorkAll.json";

        this.centerView =  new  MWF.xApplication.Execution.WorkList.CenterWorkView(this.rightContentDiv, this.app, {explorer:this,lp : this.app.lp.centerWorkView, css : this.css, actions : this.actions }, { templateUrl : templateUrl,category:str,filterData:filter } );
        this.centerView.load();
        this.app.setScrollBar(this.rightContentDiv,this.centerView);

    },
    loadBaseWorkList: function (str,filter) {
        this.workNavi2 = str || "deploy";
        if( this.centerView )delete this.centerView;
        this.reloadRightContentDiv();

        this.rightContentDiv.setStyles({"height":this.app.middleContent.getSize().y-40-140+"px"});
        var templateUrl= this.path+"baseWorkAll.json";

        if(this.scrollBar && this.scrollBar.scrollVAreaNode){
            this.scrollBar.scrollVAreaNode.destroy()
        }
        //this.app.setScrollBar(this.rightContentDiv,this.baseView);
        //MWF.require("MWF.widget.ScrollBar", function () {
        //    if(this.scrollBar) delete this.scrollBar
        //    this.scrollBar =  new MWF.widget.ScrollBar(this.rightContentDiv, {
        //        "indent": false,
        //        "style": "xApp_TaskList",
        //        "where": "before",
        //        "distance": 100,
        //        "friction": 4,
        //        "axis": {"x": false, "y": true},
        //        "onScroll": function (y) {
        //            var scrollSize = this.rightContentDiv.getScrollSize();
        //            var clientSize = this.rightContentDiv.getSize();
        //            var scrollHeight = scrollSize.y - clientSize.y;
        //            var view = this.baseView || this.centerView;
        //            if (y+200 > scrollHeight && view && view.loadElementList) {
        //                if (! view.isItemsLoaded) view.loadElementList();
        //            }
        //        }.bind(this)
        //    });
        //
        //}.bind(this),false);

        this.baseView =  new  MWF.xApplication.Execution.WorkList.BaseWorkView(this.rightContentDiv, this.app, {lp : this.app.lp.baseWorkView, css : this.css, actions : this.actions }, { templateUrl : templateUrl,category:str,filterData:filter} );
        //this.baseView.options.templateUrl =
        this.baseView.load();
        this.app.setScrollBar(this.rightContentDiv,this.baseView);
    },
    //启动流程
    createProcessDocument:function(processId,data){

        this.app.createShade();
        //this.actions.startWork( function( json ){
        this.actionProcess.startWork( function( json ){
            //this.app.destroyShade();

            this.afterStartProcess( json.data );

            this.actions.bindCheck(data.data.baseWorkId,json.data[0].work,function(json){
                this.app.destroyShade();
                this.clickBaseWorkTaskNavi(this.workNavi2 || "");
            }.bind(this));

        }.bind(this), null, processId, data)
    },
    afterStartProcess:function(data){
        //提交给后台服务流程已启动


        var options = {"workId": data[0].work};
        this.app.desktop.openApplication(null, "process.Work", options);

        //this.actions.bindCheck()
    },

    //切换具体工作tab页
    changeBaseWork: function(obj){
        var liObj = this.rightSearchDiv.getElements("li");
        liObj.setStyle("border-bottom","");
        obj.setStyle("border-bottom","2px solid #124c93");
    },
    searchAction : function(){
        var filterData = {};
        filterData.workTitle = this.rightSearchBarInput.get("value");

        if(this.workNavi1 == "base"){
            this.loadBaseWorkList(this.workNavi2,filterData)
        }else if(this.workNavi1 == "center"){
            this.loadCenterWorkList(this.workNavi2,filterData)
        }
    },
    showErrorMessage:function(xhr,text,error){
        var errorText = error;
        if (xhr) errorMessage = xhr.responseText;
        if(errorMessage!=""){
            var e = JSON.parse(errorMessage);
            if(e.message){
                this.app.notice( e.message,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }else{
            this.app.notice(errorText,"error")
        }

    }

});


MWF.xApplication.Execution.WorkList.WorkForm = new Class({
    Extends: MPopupForm,

    createTopNode: function(){
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);
            this.formTopImg = new Element("img.formTopImg",{
                "styles":this.css.formTopImg,
                "src":"/x_component_Execution/$Main/default/icon/Document-104.png"
            }).inject(this.formTopNode);
            this.formTopSpan = new Element("span.formTopSpan",{
                "styles": this.css.formTopSpan,
                "text":this.options.title
            }).inject(this.formTopNode);


            if( this.options.closeAction ){
                this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }
        }
    },
    _createBottomContent: function(){
        var html = "<span style='color:#f59353'>"+MWF.xApplication.Execution.LP.workTask.popUp.createNewTask.title+"</span>："+MWF.xApplication.Execution.LP.workTask.popUp.createNewTask.explain;
        html += "<br><span style='color:#f59353'>"+MWF.xApplication.Execution.LP.workTask.popUp.createAddTask.title+"</span>："+MWF.xApplication.Execution.LP.workTask.popUp.createAddTask.explain;
        this.createExplainDiv = new Element("div.createExplainDiv",{
            "styles":this.css.createExplainDiv,
            "html":html
        }).inject(this.formBottomNode);
    }




});



MWF.xApplication.Execution.WorkList.CenterWorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,


    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkList.CenterWorkDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        var category = this.options.category;

        if (!count)count = 15;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        if(id=="(0)")this.app.createShade();
        //alert("this.items.length="+this.items.length)

        //alert("id="+id)
        var filter = this.options.filterData || {};
        filter.maxCharacterNumber = "-1";

        if(category=="deploy"){
            this.tabLocation = "centerDeploy";
            this.actions.getCenterWorkDeployListNext(id, count, filter, function (json) {
                if (callback)callback(json);
                this.app.destroyShade();
            }.bind(this))
        }else if(category=="drafter"){
            this.tabLocation = "centerDrafter";
            this.actions.getCenterWorkDrafterListNext(id, count, filter, function (json) {
                if (callback)callback(json);
                this.app.destroyShade();
            }.bind(this))
        }else if(category=="archive"){
            this.tabLocation = "centerArchive";
            this.actions.getCenterWorkArchiveListNext(id,count,filter,function(json){
                if(callback)callback(json);
                this.app.destroyShade();
            }.bind(this))
        }else{
            this.tabLocation = "centerDeploy";
            this.actions.getCenterWorkDeployListNext(id, count, filter, function (json) {
                if (callback)callback(json);
                this.app.destroyShade();
            }.bind(this),null,false)
        }
        //this.app.workList.tabLocation = this.tabLocation;

    },
    _removeDocument: function(documentData, all){
        this.actions.deleteCenterWork(documentData.id, function(){
            if(this.tabLocation == "centerDrafter"){
                this.app.workList.loadCenterWorkList("drafter")
            }else if(this.tabLocation == "centerDeploy"){
                this.app.workList.loadCenterWorkList("deploy")
            }

            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        MWF.xDesktop.requireApp("Execution", "WorkDeploy", function(){
            this.workDeploy = new MWF.xApplication.Execution.WorkDeploy(this, this.actions,{"id":documentData.id},{
                "isEdited":false,
                "centerWorkId":documentData.id,
                "onReloadView":function(json){
                    if(json && json.action && json.action == "reload"){
                        this.explorer.explorer.createRightContentDiv(this.explorer.explorer.workNavi1,this.explorer.explorer.workNavi2)
                    }
                }.bind(this)
            } );
            this.workDeploy.load();

        }.bind(this))
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

MWF.xApplication.Execution.WorkList.CenterWorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    viewActionReturn:function(d) {
        var ret = false;
        if(d.operation && d.operation.length==1){
            ret = true;
        }
        //if (d.operation && d.operation.indexOf("VIEW")>-1)ret = true;
        return ret;
    },
    editActionReturn:function(d) {
        var ret = false;
        if (d.operation && d.operation.indexOf("EDIT")>-1)ret = true;
        return ret;
    },
    deleteActionReturn:function(d) {
        var ret = false;
        if (d.operation && d.operation.indexOf("DELETE")>-1)ret = true;
        return ret;
    },

    action_view:function(){
        MWF.xDesktop.requireApp("Execution", "WorkDeploy", function(){
            this.workDeploy = new MWF.xApplication.Execution.WorkDeploy(this.view, this.view.app.restActions,{"id":this.data.id},{
                "isEdited":false,
                "centerWorkId":this.data.id,
                "onReloadView":function(json){
                    if(json && json.action && json.action == "reload"){
                        this.explorer.explorer.clickCenterWorkTaskNavi("drafter")
                    }
                }.bind(this)
            });
            this.workDeploy.load();

        }.bind(this))
    },
    action_edit:function(){
        MWF.xDesktop.requireApp("Execution", "WorkDeploy", function(){
            this.workDeploy = new MWF.xApplication.Execution.WorkDeploy(this.view, this.view.app.restActions,{"id":this.data.id},{
                "isEdited":true,"centerWorkId":this.data.id
            } );
            this.workDeploy.load();

        }.bind(this))
    },
    action_delete:function(e){
        var _self = this;
        _self.view.app.confirm("warn",e,_self.view.app.lp.workList.submitWarn.warnTitle,_self.view.app.lp.workList.submitWarn.warnContent.delete,300,120,function(){
            _self.actions.deleteCenterWork(_self.data.id, function(json){
                if(json.type && json.type=="success"){
                    this.app.notice(_self.view.app.lp.workList.prompt.deleteCenterWork, "success");
                    _self.app.workList.loadCenterWorkList(this.app.workList.workNavi2)
                }
            }.bind(_self),function(xhr,text,error){
                _self.explorer.explorer.showErrorMessage(xhr,text,error)
            }.bind(_self));

            this.close()


        },function(){
            this.close();
        })

    },
    _postCreateDocumentNode: function( itemNode, itemData ){

        if(itemNode.getElements("span[icon='showIcon']")){
            var iconObj = itemNode.getElements("span[icon='showIcon']");
            var icons = itemData.workProcessIdentity;
            var path = this.app.workList.path+"default/icon/";
            var styles = "margin-left:5px";
            for(var i=0;i<icons.length;i++){
                if(icons[i]=="AUTHORIZE"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"authorize.png' style='"+styles+"' >")
                }else if(icons[i]=="TACKBACK"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"authorize.png' style='"+styles+"' >")
                }else if(icons[i]=="AUTHORIZECANCEL"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"authorize.png' style='"+styles+"' >")
                }else if(icons[i]=="RESPONSIBILITY"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"responsibility.png' style='"+styles+"' >")
                }else if(icons[i]=="COOPERATE"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"cooperate.png' style='"+styles+"' >")
                }else if(icons[i]=="READ"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"read.png' style='"+styles+"' >")
                }else if(icons[i]=="DEPLOY"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"deploy.png' style='"+styles+"' >")
                }else if(icons[i]=="VIEW"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"view.png' style='"+styles+"' >")
                }

            }
        }
    }
});



MWF.xApplication.Execution.WorkList.BaseWorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkList.BaseWorkDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        var category = this.category = this.options.category;

        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        filter.maxCharacterNumber = "-1";

        if(category=="myDrafter"){   //我的草稿
            this.tabLocation = "baseDrafter";
            this.actions.getBaseWorkListMyDrafterNext(id, count, filter, function (json) {
                if (callback)callback(json);
                this.app.destroyShade();
            }.bind(this))
        }else if(category=="myDeploy"){  //我部署的
            this.tabLocation = "baseDeploy";
            this.actions.getBaseWorkListMyDeployNext(id, count, filter, function (json) {
                if (callback)callback(json);
                this.app.destroyShade();
            }.bind(this))
        }else if(category=="myDo"){ //我负责的
            this.tabLocation = "baseDo";
            this.actions.getBaseWorkListMyDoNext(id, count, filter, function (json) {
                if (callback)callback(json);
                this.app.destroyShade();
            }.bind(this))
        }else if(category=="myAssist"){ //我协助的
            this.tabLocation = "baseAssist";
            this.actions.getBaseWorkListMyAssistNext(id, count, filter, function (json) {
                if (callback)callback(json);
                this.app.destroyShade();
            }.bind(this))
        }else if(category=="myRead"){ //我阅知的
            this.tabLocation = "baseRead";
            this.actions.getBaseWorkListMyReadNext(id, count, filter, function (json) {
                if (callback)callback(json);
                this.app.destroyShade();
            }.bind(this))
        }else if(category=="myAppoint"){   //我委托的
            this.tabLocation = "baseAppoint";
            this.actions.getBaseWorkListMyAppointNext(id, count,filter,function(json){
                if (callback)callback(json);
                this.app.destroyShade();
            }.bind(this))
        }else if(category=="myArchive"){
            this.tabLocation = "baseArchive";
            this.actions.getBaseWorkListMyArchiveNext(id, count,filter,function(json){
                if (callback)callback(json);
                this.app.destroyShade();
            }.bind(this))
        }else if(category=="myAll"){
            this.tabLocation = "baseAll";
            this.actions.getBaseWorkListAllNext(id, count,filter,function(json){
                if (callback)callback(json);
                this.app.destroyShade();
            }.bind(this))
        }

        this.app.workList.tabLocation = this.tabLocation;

    },
    _removeDocument: function(documentData){
        this.actions.deleteBaseWork(documentData.id, function(json){
            if(json.type && json.type=="success"){
                if(this.tabLocation == "baseDrafter"){
                    this.app.workList.loadBaseWorkList("myDrafter")
                }else if(this.tabLocation == "baseDeploy"){
                    this.app.workList.loadBaseWorkList("myDeploy")
                }

                this.app.notice(this.app.lp.deleteDocumentOK, "success");
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
        if( documentData.workProcessStatus == this.lp.workProcessStatus.drafter ){
            MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
                var workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,documentData,{
                    "isNew": false,
                    "isEdited": false,
                    "tabLocation":this.category
                });

                workform.load();
            }.bind(this));
        }else{
            MWF.xDesktop.requireApp("Execution", "WorkDetail", function(){
                var workform = new MWF.xApplication.Execution.WorkDetail(this, this.app.restActions,documentData,{
                    "isNew": false,
                    "isEdited": false,
                    "tabLocation":this.category,
                    //"container":this.app.content
                });
                //workform.container = this.app.content;
                workform.load();
            }.bind(this));
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

MWF.xApplication.Execution.WorkList.BaseWorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    viewActionReturn:function(d) {
        var ret = false;
        if(d.operation && d.operation.length==1){
            ret = true;
        }
        //if (d.operation && d.operation.indexOf("VIEW")>-1)ret = true;
        return ret;
    },
    editActionReturn:function(d) {
        var ret = false;
        if (d.operation && d.operation.indexOf("EDIT")>-1)ret = true;
        return ret;
    },
    splitActionReturn:function(d) {
        var ret = false;
        if (d.operation && d.operation.indexOf("SPLIT")>-1)ret = true;
        return ret;
    },
    authorizeActionReturn:function(d) {
        var ret = false;
        if (d.operation && d.operation.indexOf("AUTHORIZE")>-1)ret = true;
        return ret;
    },
    tackBackActionReturn:function(d) {
        var ret = false;
        if (d.operation && d.operation.indexOf("TACKBACK")>-1)ret = true;
        return ret;
    },
    reportActionReturn:function(d) {
        var ret = false;
        if (d.operation && d.operation.indexOf("REPORT")>-1)ret = true;
        return ret;
    },
    deleteActionReturn:function(d) {
        var ret = false;
        if (d.operation && d.operation.indexOf("DELETE")>-1)ret = true;
        return ret;
    },
    archiveActionReturn:function(d){
        var ret = false;
        if (d.operation && d.operation.indexOf("ARCHIVE")>-1)ret = true;
        return ret;
    },
    checkActionReturn:function(d){
        var ret = false;
        if (d.operation && d.operation.indexOf("APPRAISE")>-1)ret = true;
        return ret;
        //return true
    },
    progressActionReturn:function(d){
        var ret = false;
        if (d.operation && d.operation.indexOf("PROGRESS")>-1)ret = true;
        return ret;
    },
    action_view:function(){
        if( this.data.workProcessStatus == this.lp.workProcessStatus.drafter ){
            MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
                var workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,this.data,{
                    "isNew": false,
                    "isEdited": false,
                    "tabLocation":this.category
                });
                workform.load();
            }.bind(this));
        }else{
            MWF.xDesktop.requireApp("Execution", "WorkDetail", function(){
                var workform = new MWF.xApplication.Execution.WorkDetail(this, this.app.restActions,this.data,{
                    "isNew": false,
                    "isEdited": false,
                    "tabLocation":this.category
                });
                workform.load();
            }.bind(this));
        }
    },

    action_edit:function(){
        MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
            var workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,this.data,{
                "isNew": false,
                "isEdited": true,
                "tabLocation":this.view.category,
                "actionStatus":"save"
            });
            workform.load();
        }.bind(this));
    },
    action_split:function(){
        MWF.xDesktop.requireApp("Execution", "WorkForm", function(){

            var workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,{"centerWorkTitle":this.data.centerTitle},{
                "isNew": true,
                "isEdited": false,
                "parentWorkId":this.data.id,
                "actionStatus":"deploy"

            });

            workform.load();
        }.bind(this));
    },
    action_authorize:function(){
        var data = {
            workId : this.data.id
        };
        var appointForm =  new MWF.xApplication.Execution.WorkList.Appoint(this.view.app,this.view.app.restActions,data,this.view.css,{
            "ieEdited": true,
            "onReloadView" : function(){
                //this.view.app.workList.createRightContentDiv("base","myAppoint");
                this.view.app.workList.clickBaseWorkTaskNavi("myAppoint")
            }.bind(this)
        });
        appointForm.load();
    },
    action_tackBack:function(e){
        var _self = this;
        _self.view.app.confirm("warn",e,_self.view.app.lp.workList.submitWarn.warnTitle,_self.view.app.lp.workList.submitWarn.warnContent.tackBack,300,120,function(){
            var data = {
                workId : _self.data.id
            };

            _self.actions.unAppointBaseWork(data,function(){
                this.app.notice(_self.view.app.lp.workList.prompt.tackbackBaseWork,"success");
            }.bind(_self),function(xhr,text,error){
                var errorText = error;
                if (xhr) errorMessage = xhr.responseText;
                var e = JSON.parse(errorMessage);
                if(e.message){
                    this.app.notice( e.message,"error");
                }else{
                    this.app.notice( errorText,"error");
                }
            }.bind(_self),false);

            _self.view.app.workList.clickBaseWorkTaskNavi("myAppoint");
            this.close()

        },function(){
            this.close();
        })
    },
    action_report:function(){
        MWF.xDesktop.requireApp("Execution", "WorkReport", function(){
            var data = {
                workId : this.data.id
            };
            var workReport = new MWF.xApplication.Execution.WorkReport(this, this.app.restActions,data,{
                "isNew": false,
                "isEdited": false,
                "tabLocation":this.view.category,
                "from":"drafter"
            });
            workReport.load();
        }.bind(this));
    },
    action_delete:function(e){
        var _self = this;
        _self.view.app.confirm("warn",e,_self.view.app.lp.workList.submitWarn.warnTitle,_self.view.app.lp.workList.submitWarn.warnContent.delete,300,120,function(){
            _self.actions.deleteBaseWork(_self.data.id, function(json){
                if(json.type && json.type=="success"){
                    this.app.notice(_self.view.app.lp.workList.prompt.deleteBaseWork, "success");
                    _self.app.workList.clickBaseWorkTaskNavi(_self.app.workList.workNavi2)
                }
            }.bind(_self),function(xhr,text,error){
                _self.app.WorkList.showErrorMessage(xhr,text,error)
            }.bind(_self));

            this.close()


        },function(){
            this.close();
        })

    },
    action_archive:function(e){
        var _self = this;
        _self.view.app.confirm("warn",e,_self.view.app.lp.workList.submitWarn.warnTitle,_self.view.app.lp.workList.submitWarn.warnContent.archive,300,120,function(){
            _self.actions.archiveBaseWork(_self.data.id, function(json){
                if(json.type && json.type=="success"){
                    this.app.notice(_self.view.app.lp.workList.prompt.archiveBaseWork, "success");
                    _self.app.workList.clickBaseWorkTaskNavi(_self.app.workList.workNavi2)
                }
            }.bind(_self),function(xhr,text,error){
                _self.app.WorkList.showErrorMessage(xhr,text,error)
            }.bind(_self));

            this.close()


        },function(){
            this.close();
        })
    },
    action_check:function(e){
        var _self = this;
        //var processId = "8a4eeb9d-05a6-4e78-8782-32bc4d260c6b";

        this.actions.getProfileByCode({configCode:"APPRAISE_WORKFLOW_ID"},function(json){
            if(json.type == "success"){
                if(json.data && json.data.configValue){
                    var processId = json.data.configValue;
                    var switchData = {};
                    switchData.configCode = "REPORT_SUPERVISOR";
                    this.actions.getProfileByCode(switchData,function(json){
                        if(json.type == "success"){
                            if(json.data && json.data.configValue){
                                this.companyAdmin = json.data.configValue;

                                var dept = "";
                                if(_self.data.responsibilityUnitName){
                                    dept = _self.data.responsibilityUnitName.split("@")[0]
                                }
                                var data = {
                                    "title":"关于"+dept+"《"+_self.data.title+"》的考核",
                                    //"identity": _self.view.app.identity,
                                    data:{
                                        "createPerson":this.companyAdmin,
                                        //"createPerson":this.data.responsibilityIdentity,
                                        "baseWorkTitle":_self.data.title,
                                        "baseWorkId":_self.data.id
                                    }
                                };

                                _self.view.app.confirm("warn",e,_self.view.app.lp.workList.submitWarn.warnTitle,_self.view.app.lp.workList.submitWarn.warnContent.startProcess,300,120,function(){
                                    _self.view.app.workList.createProcessDocument(processId,data);
                                    this.close()


                                },function(){
                                    this.close();
                                });
                            }
                        }
                    }.bind(this));
                }
            }
        }.bind(this));

    },
    action_progress:function(e){

        var data = {
            title : this.data.title,
            workId : this.data.id,
            isCompleted : this.data.isCompleted,
            overallProgress : this.data.overallProgress
        };
        var progressForm =  new MWF.xApplication.Execution.WorkList.Progress(this.view.app,this.view.app.restActions,data,this.view.css,{
            "ieEdited": true,
            "onReloadView" : function( data ){
                //this.view.app.workList.createRightContentDiv("base","myAppoint");
                this.view.app.workList.clickBaseWorkTaskNavi(this.app.workList.workNavi2 || "")
            }.bind(this)
        });
        progressForm.load();


    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if(itemNode.getElements("div[name='appointDiv']")){
            if(itemData.okrWorkAuthorizeRecord){
                itemNode.getElements("div[name='appointDiv']").setStyle("display","")
            }
        }
        if(itemNode.getElements("div[item='workDetail']")){
            itemNode.getElements("div[item='workDetail']").set("title",itemData.workDetail)
        }
        if(itemNode.getElements("div[item='progressAction']")){
            itemNode.getElements("div[item='progressAction']").set("title",itemData.progressAction)
        }
        if(itemNode.getElements("span[icon='showIcon']")){
            var iconObj = itemNode.getElements("span[icon='showIcon']");
            var icons = itemData.workProcessIdentity;
            var path = this.app.workList.path+"default/icon/";
            var styles = "margin-left:5px";
            for(i=0;i<icons.length;i++){
                if(icons[i]=="AUTHORIZE"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"authorize.png' style='"+styles+"' >")
                }else if(icons[i]=="TACKBACK"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"authorize.png' style='"+styles+"' >")
                }else if(icons[i]=="AUTHORIZECANCEL"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"authorize.png' style='"+styles+"' >")
                }else if(icons[i]=="RESPONSIBILITY"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"responsibility.png' style='"+styles+"' >")
                }else if(icons[i]=="COOPERATE"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"cooperate.png' style='"+styles+"' >")
                }else if(icons[i]=="READ"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"read.png' style='"+styles+"' >")
                }else if(icons[i]=="DEPLOY"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"deploy.png' style='"+styles+"' >")
                }else if(icons[i]=="VIEW"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"view.png' style='"+styles+"' >")
                }else if(icons[i]=="COMPLETED"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"completed.png' style='"+styles+"' >")
                }else if(icons[i]=="ARCHIVE"){
                    iconObj.set("html",iconObj.get("html")+"<img src='"+path+"completed.png' style='"+styles+"' >")
                }

            }
        }
    }

});



MWF.xApplication.Execution.WorkList.Appoint = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "500",
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": false,
        "closeAction": true,
        "closeText" : "",
        "needLogout" : false,
        "isNew": true
    },
    initialize: function (app, actions, data, css, options) {
        this.setOptions(options);
        this.app = app;
        this.actions = this.app.restActions;
        this.css = css;

        //this.options.title = this.app.lp.idenitySelectTitle;
        //
        //this.identities = identities;
        this.data = data || {};
        this.actions = actions;
    },
    load: function () {
        this.create();
    },

    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopIconNode = new Element("div.formTopIconNode", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div.formTopTextNode", {
                "styles": this.css.formTopTextNode,
                "text": this.app.lp.workTask.appoint.appointTitle
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            this.formTopContentNode = new Element("div.formTopContentNode", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode);

            //this._createTopContent();

        }

    },
    _createTableContent: function () {
        var table = new Element("table",{"width":"100%",border:"0",cellpadding:"5",cellspacing:"0"}).inject(this.formTableArea);
        table.setStyles({"margin-top":"40px"});
        var tr = new Element("tr").inject(table);
        var td = new Element("td",{
            text : this.app.lp.workTask.appoint.appointFor,
            valign:"middle",
            width:"20%"
        }).inject(tr);
        td = new Element("td",{width:"80%"}).inject(tr);

        this.appointPerson = new MDomItem( td, {
            "name" : "appointPerson", "type":"org","orgType":"identity","notEmpty":true,
            "style":{"width":"90%","height":"25px","border":"1px solid #666"}
        }, true, this.app );
        this.appointPerson.load();

        tr = new Element("tr").inject(table);
        td = new Element("td",{
            "text" : this.app.lp.workTask.appoint.appointOpinion,
            valign:"middle"
        }).inject(tr);


        td = new Element("td").inject(tr);
        this.appointOpinion = new Element("textarea").inject(td);
        this.appointOpinion.setStyles({"width":"90%","height":"50px"})

    },

    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.app.lp.workTask.appoint.appointCancel
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function () {
            this.close();
        }.bind(this));

        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css.formOkActionNode,
            "text": this.app.lp.workTask.appoint.appointOK
        }).inject(this.formBottomNode);

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));
    },
    ok:function(){

        if(this.appointPerson.getValue(",")==""){
            this.app.notice(this.app.lp.workTask.appoint.personEmpty,"error");
            return false;
        }
        if(this.appointOpinion.get("value")==""){
            this.app.notice(this.app.lp.workTask.appoint.opinionEmpty,"error");
            return false;
        }
        var submitData = {
            workId : this.data.workId,
            undertakerIdentity : this.appointPerson.getValue(","),
            authorizeOpinion : this.appointOpinion.get("value")
        };
        this.actions.appointBaseWork(submitData,function(json){
            this.close();
            this.fireEvent("reloadView");
        }.bind(this),function(xhr,text,error){
            var errorText = error;
            if (xhr) errorMessage = xhr.responseText;
            var e = JSON.parse(errorMessage);
            if(e.message){
                this.app.notice( e.message,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }.bind(this),false)
    },
    selectPerson: function( item, type,count ) {
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        this.fireEvent("querySelect", this);
        var value = item.get("value").split(this.valSeparator);
        var options = {
            "type": type,
            "title": this.app.lp.workTask.appoint.appointTitle,
            "count": count,
            "values": value || [],
            "onComplete": function (items) {
                var arr = [];
                items.each(function (item) {
                    arr.push(item.data.distinguishedName);
                }.bind(this));
                item.set("value", arr.join(","));
            }.bind(this)
        };

        var selector = new MWF.O2Selector(this.app.content, options);
    }

});


MWF.xApplication.Execution.WorkList.Progress = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "400",
        "height": "200",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": true,
        "closeAction": true,
        "closeText" : "",
        "needLogout" : false,
        "isNew": true
    },
    initialize: function (app, actions, data, css, options) {
        this.setOptions(options);
        this.app = app;
        this.actions = this.app.restActions;
        this.css = css;

        //this.options.title = this.app.lp.idenitySelectTitle;
        //
        //this.identities = identities;
        this.data = data || {};
        this.actions = actions;
    },
    load: function () {
        this.create();
    },
    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopIconNode = new Element("div.formTopIconNode", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div.formTopTextNode", {
                "styles": this.css.formTopTextNode,
                "text": this.app.lp.workList.progress.progressTitle
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            this.formTopContentNode = new Element("div.formTopContentNode", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode);

            //this._createTopContent();

        }

    },
    _createTableContent: function () {
        this.formTableArea.setStyles({"margin-top":"40px"});
        var table = new Element("table",{"width":"100%",border:"0",cellpadding:"5",cellspacing:"0"}).inject(this.formTableArea);
        table.setStyles({"margin-top":"10px"});
        tr = new Element("tr").inject(table);
        tr = new Element("tr").inject(table);
        td = new Element("td",{
            "text" : this.app.lp.workList.progress.completedPercent,
            align:"center",
            "width":"40%"
        }).inject(tr);

        var td = new Element("td").inject(tr);

        this.completePercentSelect = new Element("select.completePercentSelect").inject(td);
        var completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"0%","value":"0"}).inject(this.completePercentSelect);
        completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"10%","value":"10"}).inject(this.completePercentSelect);
        completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"20%","value":"20"}).inject(this.completePercentSelect);
        completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"30%","value":"30"}).inject(this.completePercentSelect);
        completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"40%","value":"40"}).inject(this.completePercentSelect);
        completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"50%","value":"50"}).inject(this.completePercentSelect);
        completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"60%","value":"60"}).inject(this.completePercentSelect);
        completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"70%","value":"70"}).inject(this.completePercentSelect);
        completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"80%","value":"80"}).inject(this.completePercentSelect);
        completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"90%","value":"90"}).inject(this.completePercentSelect);
        completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"100%","value":"100"}).inject(this.completePercentSelect);

        this.completePercentSelect.set("value",parseInt(this.data.overallProgress))
    },

    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.app.lp.workTask.appoint.appointCancel
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close();
        }.bind(this));

        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css.formOkActionNode,
            "text": this.app.lp.workTask.appoint.appointOK
        }).inject(this.formBottomNode);

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));
    },
    ok:function(){
        var percent = this.completePercentSelect.get("value");
        this.actions.progressBaseWork(this.data.workId,parseInt(percent),
            function(json){
                this.close();
                this.fireEvent("reloadView");
            }.bind(this),
            function(xhr,text,error){
                this.app.showErrorMessage(xhr,text,error)
            }.bind(this)
        )
    }

});