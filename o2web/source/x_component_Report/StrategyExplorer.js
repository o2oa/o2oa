MWF.xDesktop.requireApp("Report", "Attachment", null, false);
MWF.xDesktop.requireApp("Template", "MSelector", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);

MWF.xApplication.Report.StrategyExplorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "id" : "",
        "type" : "app"  //flow print
     },

    initialize: function( app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Report/$StrategyExplorer/";
        this.cssPath = "/x_component_Report/$StrategyExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.lp = this.app.lp;

        this.actions = actions;
    },
    reload: function(){
        this.node.empty();
        this.loadLayout();
    },
    destroy : function(){
        if( this.options.type == "app" ){
            this.app.removeEvent("resize", this.resetNodeSizeFun );
        }
        this.node.empty();
        delete  this;
    },
    load: function( callback ){
        if( this.reportContainer )this.node = $( this.reportContainer );
        //summaryContainer planContainer threeworkContainer
        this.loadLayout( callback );
        if( this.options.type == "app" ){
            this.resetNodeSizeFun = this.resetNodeSize.bind(this);
            this.app.addEvent("resize", this.resetNodeSizeFun );
        }
    },
    loadLayout : function(callback){
        this.createNode();
        this.actions.getReport( this.options.id, function( json ){
            this.data = json.data;

            if(callback)callback( this.data );

            this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;
            this.isEdited = false;

            this.getStatus( function(){
                this.app.setTitle( this.getTitle() );
                //this.createTopNode();
                this.loadContentNode();
                if( this.options.type == "app" ){
                    this.resetNodeSize();
                }
            }.bind(this))

        }.bind(this));
    },
    getTitle : function(){
        var current = this.data.year + "年" + parseInt( this.data.month ) + "月工作总结";

        var nextMonth = new Date( this.data.year, parseInt( this.data.month ) - 1, 1 ).increment("month", 1);
        var next = nextMonth.getFullYear() + "年" + (nextMonth.getMonth() + 1) + "月工作计划";

        return this.data.targetUnit.split("@")[0] + current + "和" + next;
    },
    isToReadLeader : function( callback ){
        MWF.Actions.get("x_organization_assemble_express").getUnitWithIdentityAndLevelValue( {"identity":this.data.targetIdentity,"level":"1"}, function( json ){
            var topUnit = json.data.unit;
            if( topUnit ){
                MWF.Actions.get("x_organization_assemble_express").getDutyValue({"name":"董事长","unit":topUnit}, function( js ){
                    var flag = false;
                    var leaders = js.data.identityList || [];
                    ( layout.desktop.session.user || layout.user ).identityList.each( function( id ){
                        if( leaders.contains( id.distinguishedName ) ){
                            flag = true;
                        }
                    });
                    if( callback )callback( flag );
                }.bind(this))
            }else{
                if( callback )callback( false );
            }
        }.bind(this));

    },
    isToRead : function(){
        var flag = false;
        (this.workApp.readList || []).each( function( read ){
            if( this.userName == read.person ){
                flag = true;
                this.readData = read;
            }
        }.bind(this) );
        return flag;
        //return this.workApp.control.allowReadProcessing || false;
    },
    getStatus : function( callback ){
        this.isEdited = this.workApp.control.allowProcessing || this.workApp.control.allowSave;
        this.activityName = this.workApp.activity ? this.workApp.activity.name : "";
        this.activityAlias = this.workApp.activity ? this.workApp.activity.alias : "";

        if( this.data.reportStatus == "月度汇报员分派填写人" || this.data.activityName == "拟稿"  || this.activityName == "月度汇报员分派填写人" || this.activityAlias == "deployment" ){
            this.status = "deployment";
            //if( ( this.app.common.isAdmin() || this.userName == this.data.targetPerson )  ){
            //    this.isEdited = true;
            //}
            if( callback )callback();
        }else if( this.activityName == "汇报人" || this.activityAlias == "write"  ) {
            this.status = "write";
            //this.app.common.getIdentity( "", function( identityList ){
            //    var flag = false;
            //    identityList.each( function( id ){
            //        if( this.data.workreportPersonList.contains( id ) ){
            //            flag = true;
            //        }
            //    }.bind(this));
            //    if (( this.app.common.isAdmin() || this.userName == this.data.targetPerson || flag )) {
            //        this.isEdited = true;
            //    }
            //    if( callback )callback();
            //}.bind(this))
            if( callback )callback();
        }else if( this.activityName == "月度汇报员汇总" || this.activityAlias == "confirm" ) {
            this.status = "confirm";
            //if (( this.app.common.isAdmin() || this.userName == this.data.targetPerson )) {
            //    this.isEdited = true;
            //}
            if (callback)callback();
        }else if(this.activityName == "战略负责人审核" || this.activityAlias == "audit"){
            this.status = "audit";
            //if (( this.app.common.isAdmin() || this.userName == this.data.targetPerson )) {
            //    this.isEdited = true;
            //}
            if (callback)callback();
        }else if(this.activityName == "公司领导阅" ){
            this.status = "summary";
            //if (( this.app.common.isAdmin() || this.userName == this.data.targetPerson )) {
            //    this.isEdited = true;
            //}
            if (callback)callback();
        }else{
            this.status = "summary";
            //this.data.reportStatus == "审核中"
            //this.data.reportStatus == "已完成"
            //this.isEdited = false;
            if( callback )callback();
        }
    },
    createNode: function(){
        this.container = new Element("div.container", {
            "styles": this.css.container
        }).inject(this.node);

        this.contentContainer = new Element("div.contentContainerNode", {
            "styles": this.css[ this.options.type=="app" ? "contentContainer" : "contentContainer_flow"]
        }).inject(this.container);
    },
    createTopNode: function(){
        var topNode = this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.contentContainer);
        if(this.isPrinted)topNode.setStyle("width","auto");

        var topTitleMiddleNode = new Element("div.topTitleMiddleNode", {
            "styles": this.css.topTitleMiddleNode
        }).inject(topNode);

        var topItemTitleNode = new Element("div.topItemTitleNode", {
            "styles": this.css.topItemTitleNode,
            "text": this.lp.title
        }).inject(topTitleMiddleNode);
        topItemTitleNode.addEvent("click", function(){
            var appId = "Report";
            if (this.app.desktop.apps[appId]){
                this.app.desktop.apps[appId].setCurrent();
            }else {
                this.app.desktop.openApplication(null, "Report", {});
            }
        }.bind(this));

        var topItemSepNode = new Element("div.topItemSepNode", {
            "styles": this.css.topItemSepNode,
            "text" : ">"
        }).inject(topTitleMiddleNode);

        var topItemTitleNode = new Element("div.topItemTitleNode", {
            "styles": this.css.topItemTitleLastNode,
            "text":  this.data.title
        }).inject(topTitleMiddleNode);
    },
    loadContentNode: function(){

        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.contentContainer);
        if(this.isPrinted)this.middleNode.setStyle("width","auto");

        this.inforNode = new Element("div",{
            "styles": this.css.inforNode
        }).inject( this.middleNode );

        //this.selectNode = new Element("div",{
        //    "styles": this.css.selectNode
        //}).inject( this.middleNode );

        this.mainContentNode = new Element("div.mainContentNode", {
            "styles": this.css.mainContentNode
        }).inject(this.middleNode);

        this.loadInforContent();

        if( this.options.type == "print" ){
            MWF.xDesktop.requireApp("Report", "StrategyExplorerPrint", null, false);
            this.print = new MWF.xApplication.Report.StrategyExplorer.Print(this.mainContentNode, this, this.data, {
                isEdited : false,
                isKeyworkEdited : false,
                status : this.status
            });
            this.print.load();
        }else if( this.status == "deployment" ){
            MWF.xDesktop.requireApp("Report", "StrategyExplorerDeploy", null, false);
            this.deployment = new MWF.xApplication.Report.StrategyExplorer.Deployment(this.mainContentNode, this, this.data, {
                isEdited : this.isEdited,
                isKeyworkEdited : false //this.data.workPlanModifyable
            });
            this.deployment.load();
        }else if( this.status == "summary" ){
            var loadSummary = function( isToRead, isLeader ){
                MWF.xDesktop.requireApp("Report", "StrategyExplorerSummary", null, false);
                this.summary = new MWF.xApplication.Report.StrategyExplorer.Summary(this.mainContentNode, this, this.data, {
                    isToRead : isToRead,
                    isToReadLeader : isLeader,
                    isEdited : this.isEdited,
                    status : this.status
                });
                this.summary.load();
            }.bind(this);
            if( this.isToRead() ){
                this.isToReadLeader( function( isLeader ){
                    loadSummary( true, isLeader );
                })
            }else{
                loadSummary( false, false );
            }
        }else{
            MWF.xDesktop.requireApp("Report", "StrategyExplorerWrite", null, false);
            this.write = new MWF.xApplication.Report.StrategyExplorer.Write(this.mainContentNode, this, this.data, {
                isEdited : this.isEdited,
                status : this.status
            });
            this.write.load();
        }
    },
    verifyProcess : function( status ){
        if( status == "confirm" || status == "audit" ){
            return this.write.verifyProcess( status )
        }
        return true;
    },
    loadInforContent: function(){
        if( this.status == "summary" || this.options.type == "print" ){
            var html = "<table width='96%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' >" +
                "   <td styles='formTableTitleP10' lable='unitManager' style='width:90px;'></td>" +
                "    <td styles='formTableValueP10' item='unitManager' style='width:100px;'></td>"+
                "   <td styles='formTableTitleP10' lable='activityName2' style='width:50px;'></td>" +
                "    <td styles='formTableValueP10' item='activityName2' style='width:100px;'></td>"+
                "</tr>";
            html +="</table>";
            this.inforNode.set("html", html);
            MWF.xDesktop.requireApp("Template", "MForm", function () {
                var form = new MForm(this.inforNode, this.data, {
                    usesNewVersion : true,
                    isEdited: false,
                    style : "report",
                    hasColon : true,
                    itemTemplate: {
                        createDateString: { text : this.lp.reportDate, type : "innertext"},
                        unitManager: { text : "部主管", type : "org", orgType : "person", defaultValue : this.data.unitManager },
                        activityName2: { text : this.lp.activityName, type : "innertext", defaultValue : ( this.data.reportStatus || this.data.activityName ) }
                    }
                }, this);
                form.load();
            }.bind(this), true);
        }else{
            var html = "<table width='96%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' >" +
                "   <td styles='formTableTitleP10' lable='targetPerson' style='width:90px;'></td>" +
                "    <td styles='formTableValueP10' item='targetPerson' style='width:100px;'></td>"+
                "   <td styles='formTableTitleP10' lable='activityName2' style='width:50px;'></td>" +
                "    <td styles='formTableValueP10' item='activityName2' style='width:100px;'></td>"+
                "</tr>";
            if( this.status != "deployment" || this.options.type == "print" ){
                html +=  "<tr>"+
                    "   <td styles='formTableTitleP10' lable='currentPersonName' style='width:70px;'></td>" +
                    "    <td styles='formTableValueP10' item='currentPersonName' colspan='3'></td>"+
                    "</tr>"
            }
            html +="</table>";
            this.inforNode.set("html", html);
            MWF.xDesktop.requireApp("Template", "MForm", function () {
                var form = new MForm(this.inforNode, this.data, {
                    usesNewVersion : true,
                    isEdited: false,
                    style : "report",
                    hasColon : true,
                    itemTemplate: {
                        createDateString: { text : this.lp.reportDate, type : "innertext"},
                        targetPerson: { text : "月度汇报员", type : "org", orgType : "person"},
                        currentPersonName: { text : "汇报人", type : "org", orgType : "person", defaultValue : this.data.workreportPersonList },
                        reportObjType: { text : this.lp.reportObjType, type : "select", selectValue : ["","PERSON","UNIT"], selectText : ["","个人汇报","组织汇报"] },
                        activityName2: { text : this.lp.activityName, type : "innertext", defaultValue : ( this.data.reportStatus || this.data.activityName ) }
                    }
                }, this);
                form.load();
            }.bind(this), true);
        }

    },
    resetNodeSize: function () {
        var topSize = this.topNode ? this.topNode.getSize() : {"x": 0, "y": 0};
        var nodeSize = this.node.getSize();
        var pt = this.contentContainer.getStyle("padding-top").toFloat();
        var pb = this.contentContainer.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y  - pt - pb; //- topSize.y
        this.contentContainer.setStyle("height", "" + height + "px");
    },
    perview : function(){
        var url = this.getPerviewUrl();
        var previewer = new MWF.xApplication.Report.StrategyExplorer.Previewer(this, {
          url : url
        }, {}, { app: this.app });
        previewer.open();
    },
    getPerviewUrl : function(){
        var businessData = this.workApp.appForm.businessData;
        var application = (businessData.work) ? businessData.work.application : businessData.workCompleted.application;
        var appForm = this.workApp.appForm;

        var form = appForm.json.id;
        if (appForm.json.printForm) form = appForm.json.printForm;

        if ( businessData.workCompleted){
            var application = businessData.workCompleted.application;
            return window.location.protocol + "//" + window.location.host +"/x_component_Report/$Common/printWork.html?workCompletedId="+ businessData.workCompleted.id+"&app="+application+"&form="+form+"&debugger";
        }else{
            var application = businessData.work.application;
            return window.location.protocol + "//" + window.location.host +"/x_component_Report/$Common/printWork.html?workid="+ businessData.work.id+"&app="+application+"&form="+form+"&debugger";
        }
    }
});

MWF.xApplication.Report.StrategyExplorer.Previewer = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "report",
        "width": "95%",
        "height": "95%",
        "minWidth" : 300,
        "minHeight" : 220,

        "hasTop": true,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasIcon": false,
        "hasScroll" : false,
        "hasBottom": true,
        "hasMask" : true,
        "closeByClickMask" : true,

        "title": "预览",
        "draggable": false,
        "resizeable" : false,
        "maxAction" : true,
        "closeAction": true,

        "relativeToApp" : true,
        "sizeRelateTo" : "app" //desktop
    },
    _createTableContent: function () {
        this.formTableContainer.setStyles({
            "width":"99%",
            "height" : "99%"
        });
        this.formTableArea.setStyle( "text-align","center" );
        this.iframe = new Element("iframe", {
            src : this.data.url,
            frameborder : 0,
            height : "100%",
            width : "100%",
            scrolling : "auto",
            seamless : "seamless"
        }).inject( this.formTableArea )

    },
    _setNodesSize : function(width, height, formContentHeight, formTableHeight ){
        this.iframe.set("width", width - 100 );
        this.iframe.set("height", formTableHeight - 5 );
    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": this.lp.save
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": this.lp.close
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    }

});

