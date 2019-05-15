MWF.xApplication.Execution = MWF.xApplication.Execution || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.Execution.WorkTask = new Class({
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
        this.lp = app.lp;
        this.path = "/x_component_Execution/$WorkTask/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_Execution/$WorkTask/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        if(!this.reportSwitch){
            var switchData = {};
            switchData.configCode = "REPORT_USERCREATE";
            this.actions.getProfileByCode(switchData,function(json){
                if(json.type == "success"){
                    if(json.data && json.data.configValue){
                        this.reportSwitch = json.data.configValue;
                    }
                }

                //this.reportAuditLeader
            }.bind(this),null,false);
        }
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
        if(!this.splitSwitch){
            var switchData = {};
            switchData.configCode = "WORK_DISMANTLING";
            this.actions.getProfileByCode(switchData,function(json){
                if(json.type == "success"){
                    if(json.data && json.data.configValue){
                        this.splitSwitch = json.data.configValue;
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
        this.naviDiv.setStyles({"height":(size.y-60)+"px"});
        this.naviContentDiv.setStyles({"height":(size.y-180)+"px"});
        this.contentDiv.setStyles({"height":(size.y-60)+"px"});
        this.rightContentDiv.setStyles({"height":(size.y-40-140)+"px"});
    },
    createNaviContent: function(){
        this.naviDiv = new Element("div.naviDiv",{
            "styles":this.css.naviDiv
        }).inject(this.middleContent);

        this.naviTitleDiv = new Element("div.naviTitleDiv",{
            "styles":this.css.naviTitleDiv,
            "text":MWF.xApplication.Execution.LP.workTask.naviTitle
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
            "src":"/x_component_Execution/$WorkReportList/default/icon/Prototype-100.png"
        }).inject(naviContentLi);
        var naviContentSpan = new Element("span.naviContentSpan",{
            "styles":this.css.naviContentSpan,
            "text":this.lp.workTask.naviItem.workReport
        }).inject(naviContentLi)

        //var naviContentLi = new Element("li.naviContentLi",{"styles":this.css.naviContentLi}).inject(this.naviContentDiv);
        //var naviContentImg = new Element("img.naviContentImg",{
        //    "styles":this.css.naviContentImg,
        //    "src":"/x_component_Execution/$WorkReportList/default/icon/Conference-100.png"
        //}).inject(naviContentLi);
        //var naviContentSpan = new Element("span.naviContentSpan",{
        //    "styles":this.css.naviContentSpan,
        //    "text":this.lp.workTask.naviItem.workConsult
        //}).inject(naviContentLi)
        //
        //var naviContentLi = new Element("li.naviContentLi",{"styles":this.css.naviContentLi}).inject(this.naviContentDiv);
        //var naviContentImg = new Element("img.naviContentImg",{
        //    "styles":this.css.naviContentImg,
        //    "src":"/x_component_Execution/$WorkReportList/default/icon/Department-100.png"
        //}).inject(naviContentLi);
        //var naviContentSpan = new Element("span.naviContentSpan",{
        //    "styles":this.css.naviContentSpan,
        //    "text":this.lp.workTask.naviItem.workStat
        //}).inject(naviContentLi)

    },
    createContentDiv: function(){
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.middleContent);
        this.createCategoryItemDiv();

        this.clickWorkTaskNavi( this.options.workNavi1 || "base", this.options.workNavi2 || "" );

        //if( this.options.workNavi1 == "base" ) {
        //    this.createBaseWorkSearchDiv();
        //}else{
        //    this.createCenterWorkSearchDiv();
        //}
        //this.createRightContentDiv( this.options.workNavi1 || "" , this.options.workNavi2 || "" );
    },
    createCategoryItemDiv: function(){
        this.rightCategoryDiv = new Element("div.rightCategoryDiv",{"styles":this.css.rightCategoryDiv}).inject(this.contentDiv);

        //新建权限
        var isCreate = false;
        var auditData = {};
        auditData.configCode = "COMPANY_WORK_ADMIN";
        this.actions.getProfileByCode(auditData,function(json){
            if(json.data){
                if(json.data.configValue == "" || json.data.configValue.indexOf(this.app.identity)>-1){
                    isCreate = true;
                }
            }
        }.bind(this),null,false);
        if(isCreate){

            this.rightCategoryNewDiv = new Element("div.rightCategoryNewDiv",{
                "styles":this.css.rightCategoryNewDiv,
                "text":MWF.xApplication.Execution.LP.workTask.newTask
            }).inject(this.rightCategoryDiv)
                .addEvents({
                "click":function(){

                    MWF.xDesktop.requireApp("Execution", "WorkDeploy", function(){

                        this.explorer = new MWF.xApplication.Execution.WorkDeploy(this, this.actions,{},{
                            "isEdited":true,
                            "onReloadView" : function( data ){
                                this.centerWorkLi.click()
                                //this.createRightContentDiv();
                            }.bind(this)
                        });
                        this.explorer.load();
                    }.bind(this))

                }.bind(this)
                });
        }





        this.rightCategoryItemDiv = new Element("div.rightCategoryItemDiv",{"styles":this.css.rightCategoryItemDiv}).inject(this.rightCategoryDiv);
        var categoryJson;
        var categoryJsonUrl = this.path+"categoryNavi.json";

        this.centerWorkLi = new Element("li.centerWorkLi",{
            "styles":this.css.rightCategoryItemCurrentLi,
            "text": MWF.xApplication.Execution.LP.workTask.centerWork
        }).inject(this.rightCategoryItemDiv)
            .addEvents({
                "click":function(){
                    //alert("中心工作点击");
                    this.clickWorkTaskNavi("center")
                }.bind(this)
            });
        this.baseWorkLi = new Element("li.baseWorkLi",{
            "styles":this.css.rightCategoryItemLi,
            "text": MWF.xApplication.Execution.LP.workTask.baseWork
        }).inject(this.rightCategoryItemDiv)
            .addEvents({
                "click":function(){
                    //alert("具体工作点击");
                    this.clickWorkTaskNavi("base");

                }.bind(this)
            });

        this.rightSearchDiv = new Element("div.rightSearchDiv",{"styles":this.css.rightSearchDiv}).inject(this.contentDiv);
    },
    clickWorkTaskNavi : function( workNavi1, workNavi2 ){
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
    clickCenterWorkTaskNavi : function( str ){
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
        //草稿
        this.baseDrafterTabLi = new Element("li.baseDrafterTabLi", {
            "styles": this.css.baseTabLi,
            "text" : MWF.xApplication.Execution.LP.workTask.baseWorkCategory.myDrafter
        }).inject(this.rightSearchDiv)
        .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myDrafter")
                }.bind(this)
            });
        //我部署的
        this.baseDeployTabLi = new Element("li.baseDeployTabLi", {
            "styles": this.css.baseTabLi,
            "text" : MWF.xApplication.Execution.LP.workTask.baseWorkCategory.myDeploy
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myDeploy");
                }.bind(this)
            });

        //我负责的
        this.baseDoTabLi = new Element("li.baseDoTabLi", {
            "styles": this.css.baseTabLi,
            "text" : MWF.xApplication.Execution.LP.workTask.baseWorkCategory.myDo
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myDo");
                }.bind(this)
            });
        //我协助的
        this.baseAssistTabLi = new Element("li.baseAssistTabLi", {
            "styles": this.css.baseTabLi,
            "text" : MWF.xApplication.Execution.LP.workTask.baseWorkCategory.myAssist
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myAssist");
                }.bind(this)
            });
        //我阅知的
        this.baseReadTabLi = new Element("li.baseReadTabLi", {
            "styles": this.css.baseTabLi,
            "text" : MWF.xApplication.Execution.LP.workTask.baseWorkCategory.myRead
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
                "text" : MWF.xApplication.Execution.LP.workTask.baseWorkCategory.myAppoint
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
            "text" : MWF.xApplication.Execution.LP.workTask.baseWorkCategory.myArchive
        }).inject(this.rightSearchDiv)
            .addEvents({
                "click":function(){
                    this.clickBaseWorkTaskNavi("myArchive");
                }.bind(this)
            });

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
    clickBaseWorkTaskNavi : function( str ){
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
    },
    createRightContentDiv: function(workNavi1 , workNavi2 ){
        //alert(this.app.middleContent.getSize()-40-140+"px")
        if(this.rightContentDiv)this.rightContentDiv.destroy();
        this.rightContentDiv = new Element("div.rightContentDiv",{
            "styles":this.css.rightContentDiv
        }).inject(this.contentDiv);




        if(workNavi1=="base"){
            //this.changeBaseWork(this.baseDeployTabLi);
            //this.loadBaseWorkList("myDeploy");
            this.clickBaseWorkTaskNavi( workNavi2 );
        }else{
            //this.loadCenterWorkList("deploy");
            this.clickCenterWorkTaskNavi( workNavi2 );
        }


    },
    createTableContent:function(json,colsJson){
        var trHtml = "<tr>";
            trHtml+="<td>fffff</td>";
        trHtml += "</tr>";
        this.taskTable.set("html",this.taskTable.get("html")+trHtml)
    },
    loadCenterWorkList: function (str,filter) {
        if( this.baseView )delete this.baseView;
        //if(this.rightContentDiv) this.rightContentDiv.empty();
        this.reloadRightContentDiv();
        this.rightContentDiv.setStyles({"height":this.app.middleContent.getSize().y-40-140+"px"});

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
                    var view = this.baseView || this.centerView;
                    if (y + 200 > scrollHeight && view && view.loadElementList) {
                        if (! view.isItemsLoaded) view.loadElementList();
                    }
                }.bind(this)
            });
        }.bind(this),false);

        templateUrl = this.path+"listItem.json";
        if(str == "drafter"){
            templateUrl = this.path+"listItemDrafter.json";
        }else if(str == "archive"){
            templateUrl = this.path+"listItemArchive.json";
        }

        this.centerView =  new  MWF.xApplication.Execution.WorkTask.CenterWorkView(this.rightContentDiv, this.app, {explorer:this,lp : this.lp.centerWorkView, css : this.css, actions : this.actions }, { templateUrl : templateUrl,category:str,filterData:filter } );
        this.centerView.load();

    },
    loadBaseWorkList: function (str,filter) {

        if( this.centerView )delete this.centerView;
        //if(this.rightContentDiv) this.rightContentDiv.empty()
        this.reloadRightContentDiv();

        this.rightContentDiv.setStyles({"height":this.app.middleContent.getSize().y-40-140+"px"});
        templateUrl= this.path+"listItemBase.json";
        if(str=="myDrafter"){
            templateUrl= this.path+"listItemBaseDrafter.json";
        }else if(str=="myDeploy"){
            templateUrl= this.path+"listItemBaseDeploy.json";
        }else if(str=="myDo"){
            templateUrl= this.path+"listItemBaseDo.json";
        }else if(str=="myAppoint"){
            templateUrl= this.path+"listItemBaseAppoint.json";
        }else if( str =="myArchive"){
            templateUrl= this.path+"listItemBaseArchive.json";
        }

        if(this.scrollBar && this.scrollBar.scrollVAreaNode){
            this.scrollBar.scrollVAreaNode.destroy()
        }
        MWF.require("MWF.widget.ScrollBar", function () {
            if(this.scrollBar) delete this.scrollBar;
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
                    var view = this.baseView || this.centerView;
                    if (y + 200 > scrollHeight && view && view.loadElementList) {
                        if (! view.isItemsLoaded) view.loadElementList();
                    }
                }.bind(this)
            });

        }.bind(this),false);

        this.baseView =  new  MWF.xApplication.Execution.WorkTask.BaseWorkView(this.rightContentDiv, this.app, {lp : this.lp.baseWorkView, css : this.css, actions : this.actions }, { templateUrl : templateUrl,category:str,filterData:filter} );
        //this.baseView.options.templateUrl =
        this.baseView.load();
    },

    //切换具体工作tab页
    changeBaseWork: function(obj){
        var liObj = this.rightSearchDiv.getElements("li");
        liObj.setStyle("border-bottom","");
        obj.setStyle("border-bottom","2px solid #124c93");
    },
    searchAction : function(){
        //if(this.rightSearchBarInput && this.rightSearchBarInput.get("value")==""){
        //    this.app.notice(this.lp.searchEmpty, "error");
        //    this.rightSearchBarInput.focus();
        //    return false;
        //}
        var filterData = {};
        filterData.workTitle = this.rightSearchBarInput.get("value");

        if(this.tabLocation == "centerDrafter"){
            this.app.workTask.loadCenterWorkList("drafter");
            this.loadCenterWorkList("drafter",filterData);
        }else if(this.tabLocation == "centerDeploy"){
            this.loadCenterWorkList("deploy",filterData);
        }else if(this.tabLocation == "centerArchive"){
            this.loadCenterWorkList("archive",filterData)
        }


        if(this.tabLocation == "baseDrafter"){
            this.loadBaseWorkList("myDrafter",filterData)
        }else if(this.tabLocation == "baseDeploy"){
            this.loadBaseWorkList("myDeploy",filterData)
        }else if(this.tabLocation == "baseDo"){
            this.loadBaseWorkList("myDo",filterData)
        }else if(this.tabLocation == "baseAssist"){
            this.loadBaseWorkList("myAssist",filterData)
        }else if(this.tabLocation == "baseRead"){
            this.loadBaseWorkList("myRead",filterData)
        }else if(this.tabLocation == "baseAppoint"){
            this.loadBaseWorkList("myAppoint",filterData)
        }else if(this.tabLocation == "baseArchive"){
            this.loadBaseWorkList("myArchive",filterData)
        }
    }

});


MWF.xApplication.Execution.WorkTask.WorkForm = new Class({
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
    _createTableContent: function(){

        this.createNewTaskDiv = new Element("div.createNewTaskDiv",{
            "styles":this.css.createNewTaskDiv,
            "text":MWF.xApplication.Execution.LP.workTask.popUp.createNewTask.title
        }).inject(this.formTableArea)
            .addEvents({
                "click":function(){
                    this.close();

                    MWF.xDesktop.requireApp("Execution", "CreateMainTask", function(){
                        //this.clearContent();
                        //this.explorerContent = new Element("div", {
                        //	"styles": this.css.rightContentNode
                        //}).inject(this.node);
                        this.explorer = new MWF.xApplication.Execution.CreateMainTask(this, this.explorer.app.restActions,{},{} );
                        this.explorer.load();
                    }.bind(this))
                }.bind(this)
            });

        this.createAddTaskDiv = new Element("div.createAddTaskDiv",{
            "styles":this.css.createAddTaskDiv,
            "text":MWF.xApplication.Execution.LP.workTask.popUp.createAddTask.title
        }).inject(this.formTableArea)
            .addEvents({
                "click":function(){

                }
            });
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



MWF.xApplication.Execution.WorkTask.CenterWorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkTask.CenterWorkDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        var category = this.options.category;

        if (!count)count = 15;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.options.filterData || {};

        if(category=="deploy"){
            this.tabLocation = "centerDeploy";
            this.actions.getCenterWorkDeployListNext(id, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }else if(category=="drafter"){
            this.tabLocation = "centerDrafter";
            this.actions.getCenterWorkDrafterListNext(id, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
            //this.actions.getCenterWorkListNext(id, count, filter, function (json) {
            //    if (callback)callback(json);
            //}.bind(this))
        }else if(category=="archive"){
            this.tabLocation = "centerArchive";
            this.actions.getCenterWorkArchiveListNext(id,count,filter,function(json){
                if(callback)callback(json)
            }.bind(this))
        }else{
            this.tabLocation = "centerDeploy";
            this.actions.getCenterWorkDeployListNext(id, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }
        this.app.workTask.tabLocation = this.tabLocation;

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


        MWF.xDesktop.requireApp("Execution", "WorkDeploy", function(){
            var isEditedBool = (this.tabLocation == "centerDrafter" || this.tabLocation == "baseDrafter") ? true : false

            this.workDeploy = new MWF.xApplication.Execution.WorkDeploy(this, this.actions,{"id":documentData.id},{
                "isEdited":isEditedBool,"centerWorkId":documentData.id,
                onReloadView : function( data ){
                    tab = "deploy";
                    if(data && data.tab && data.tab == "drafter") tab = "drafter";
                    this.explorer.explorer.createRightContentDiv("center",tab);
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

})

MWF.xApplication.Execution.WorkTask.CenterWorkDocument = new Class({
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

    },
    removeCenterWork : function(itemData){
        //如果是拟稿人有删除部署的中心工作的权限
        if(itemData.processStatus && itemData.processStatus == this.view.app.lp.workTask.drafter){
            return true;
        }else{
            return false;
        }
    }
    //open: function(){
    //    alert("open")
    //}
});



MWF.xApplication.Execution.WorkTask.BaseWorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkTask.BaseWorkDocument(this.viewNode, data, this.explorer, this);
    },


    _getCurrentPageData: function(callback, count){
        var category = this.category = this.options.category;

        if (!count)count = 15;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.options.filterData || {};


        if(category=="myDrafter"){   //我的草稿
            this.tabLocation = "baseDrafter";
            this.actions.getBaseWorkListMyDrafterNext(id, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }else if(category=="myDeploy"){  //我部署的
            this.tabLocation = "baseDeploy";
            this.actions.getBaseWorkListMyDeployNext(id, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }else if(category=="myDo"){ //我负责的
            this.tabLocation = "baseDo";
            this.actions.getBaseWorkListMyDoNext(id, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }else if(category=="myAssist"){ //我协助的
            this.tabLocation = "baseAssist";
            this.actions.getBaseWorkListMyAssistNext(id, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }else if(category=="myRead"){ //我阅知的
            this.tabLocation = "baseRead";
            this.actions.getBaseWorkListMyReadNext(id, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }else if(category=="myAppoint"){   //我委托的
            this.tabLocation = "baseAppoint";
            this.actions.getBaseWorkListMyAppointNext(id, count,filter,function(json){
                if (callback)callback(json)
            }.bind(this))
        }else if(category=="myArchive"){
            this.tabLocation = "baseArchive"
            this.actions.getBaseWorkListMyArchiveNext(id, count,filter,function(json){
                if (callback)callback(json)
            }.bind(this))
        }

        this.app.workTask.tabLocation = this.tabLocation;


    },
    _removeDocument: function(documentData, all){
        this.actions.deleteBaseWork(documentData.id, function(json){
            if(json.type && json.type=="success"){
                if(this.tabLocation == "baseDrafter"){
                    this.app.workTask.loadBaseWorkList("myDrafter")
                }else if(this.tabLocation == "baseDeploy"){
                    this.app.workTask.loadBaseWorkList("myDeploy")
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
        if( this.tabLocation == "baseDrafter" ){

            MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
                var isEditedBool = (this.tabLocation == "centerDrafter" || this.tabLocation == "baseDrafter") ? true : false

                var workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,documentData,{
                    "isNew": false,
                    "isEdited": isEditedBool,
                    "tabLocation":this.category
                });

                workform.load();
            }.bind(this));
        }else{
            MWF.xDesktop.requireApp("Execution", "WorkDetail", function(){
                var workform = new MWF.xApplication.Execution.WorkDetail(this, this.app.restActions,documentData,{
                    "isNew": false,
                    "isEdited": false,
                    "tabLocation":this.category
                });

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

})

MWF.xApplication.Execution.WorkTask.BaseWorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    action_edit:function(){
        //this.workForm = new MWF.xApplication.Execution.WorkForm(this, this.actions, this.data, {
        //    "isNew": true,
        //    "isEdited": false
        //})
        //this.workForm.load();

        MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
            var isEditedBool = (this.view.tabLocation == "centerDrafter" || this.view.tabLocation == "baseDrafter") ? true : false

            var workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,this.data,{
                "isNew": false,
                "isEdited": isEditedBool,
                "tabLocation":this.view.category,
            });

            workform.load();
        }.bind(this));

    },
    appointActionReturn : function(d){
        if(this.view.app.workTask.appointSwitch && this.view.app.workTask.appointSwitch.toUpperCase() == "OPEN"){
            return true;
        }
        return false
    },
    reportActionReturn : function(d){
        if(this.view.app.workTask.reportSwitch && this.view.app.workTask.reportSwitch == "OPEN"){
            return true;
        }
        return false;
    },
    splitActionReturn: function(d){
        if(this.view.app.workTask.splitSwitch && this.view.app.workTask.splitSwitch == "OPEN"){
            return true;
        }
        return false;
    },
    editActionReturn : function(d){
        if(d.workProcessStatus == this.lp.statusDraft){
            return true;
        }
        return false;
    },
    action_report: function(){
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
    action_split:function(){

        MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
            var data = {
                title : this.data.title,
                centerId : this.data.centerId,
                //centerTitle: this.data.centerTitle,
                parentWorkId : this.data.id,
                //parentWorkTitle : this.data.title,
                workType : this.data.workType,
                workLevel : this.data.workLevel,
                completeDateLimitStr : this.data.completeDateLimitStr,
                completeDateLimit : this.data.completeDateLimit,
                reportCycle: this.data.reportCycle,
                reportDayInCycle: this.data.reportDayInCycle
            };
            if(this.data.id){
                this.actions.getBaseWorkDetails(this.data.id, function (json) {
                    data.workSplitAndDescription = json.data.workDetail
                    data.specificActionInitiatives = json.data.progressAction
                    data.cityCompanyDuty = json.data.dutyDescription
                    data.milestoneMark = json.data.landmarkDescription
                    data.importantMatters = json.data.majorIssuesDescription
                }.bind(this),null,false)
            }

            var workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,data,{
                "isNew": false,
                "isEdited": true,
                "tabLocation":this.view.category

            });

            workform.load();
        }.bind(this));
    },
    action_appoint:function(){

        data = {
            workId : this.data.id
        };
        var appointForm =  new MWF.xApplication.Execution.WorkTask.Appoint(this.view.app,this.view.app.restActions,data,this.view.css,{
            "ieEdited": true,
            "onReloadView" : function( data ){
                this.view.app.workTask.createRightContentDiv("base","myAppoint");
            }.bind(this)
        });
        appointForm.load();


    },
    action_unAppoint:function(e){
        var _self = this;
        _self.view.app.confirm("warn",e,_self.view.app.lp.workTask.submitWarn.warnTitle,_self.view.app.lp.workTask.submitWarn.warnContent,300,120,function(){
            data = {
                workId : _self.data.id
            };

            _self.actions.unAppointBaseWork(data,function(json){
                this.app.notice(_self.view.app.lp.workTask.prompt.tackbackBaseWork,"success");
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

            _self.view.app.workTask.createRightContentDiv("base","myAppoint");
            this.close(0)

        },function(){
            this.close();
        })

    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if(itemNode.getElements("div[name='appointDiv']")){
            if(itemData.okrWorkAuthorizeRecord){
                itemNode.getElements("div[name='appointDiv']").setStyle("display","")
            }
        }
        if(itemNode.getElements("div[styles='documentSubject']")){
            itemNode.getElements("div[styles='documentSubject']").set("title",itemData.shortWorkDetail)
        }
    }

    //open: function(){
    //    alert("open")
    //}
})



MWF.xApplication.Execution.WorkTask.Appoint = new Class({
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
            }).inject(this.formTopNode)

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
        this.appointPerson = new Element("input",{
            "readonly": true
        }).inject(td);
        this.appointPerson.setStyles({"width":"90%","height":"20px"});
        this.appointPerson.addEvents({
            "click":function(){
                this.selectPerson(this.appointPerson,"identity",1)
            }.bind(this)
        });
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

        if(this.appointPerson.get("value")==""){
            this.app.notice(this.app.lp.workTask.appoint.personEmpty,"error");
            return false;
        }
        if(this.appointOpinion.get("value")==""){
            this.app.notice(this.app.lp.workTask.appoint.opinionEmpty,"error");
            return false;
        }
        var submitData = {
            workId : this.data.workId,
            undertakerIdentity : this.appointPerson.get("value"),
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
        MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
        this.fireEvent("querySelect", this);
        var value = item.get("value").split(this.valSeparator);
        var options = {
            "type": type,
            "title": this.app.lp.workTask.appoint.appointTitle,
            "count": count,
            "names": value || [],
            "onComplete": function (items) {
                var arr = [];
                items.each(function (item) {
                    arr.push(item.data.name);
                }.bind(this));
                item.set("value", arr.join(","));
            }.bind(this)
        };

        var selector = new MWF.OrgSelector(this.app.content, options);
    }

});
