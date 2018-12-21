MWF.xDesktop.requireApp("Report", "Attachment", null, false);
MWF.xApplication.Report.Explorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "id" : "",
        "type" : "app"  //flow
     },

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Report/$Explorer/";
        this.cssPath = "/x_component_Report/$Explorer/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.lp = this.app.lp;

        this.actions = actions;
        this.node = $(node);
    },
    reload: function(){
        this.node.empty();
        this.loadLayout();
    },
    destroy : function(){
        if( this.options.type != "flow" ){
            this.app.removeEvent("resize", this.resetNodeSizeFun );
        }
        this.node.empty();
        delete  this;
    },
    load: function(){
        this.loadLayout();
        if( this.options.type != "flow" ){
            this.resetNodeSizeFun = this.resetNodeSize.bind(this);
            this.app.addEvent("resize", this.resetNodeSizeFun );
        }
    },
    loadLayout : function(){
        this.createNode();
        this.actions.getReport( this.options.id, function( json ){
            this.data = json.data;
            this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;
            this.isEdited = false;
            if( this.data.reportStatus == "汇报者填写" && ( this.app.common.isAdmin() || this.userName == this.data.targetPerson ) ){
                this.isEdited = true;
            }
            this.app.setTitle( this.data.title );
            this.createTopNode();
            this.loadContentNode();
            if( this.options.type != "flow" ){
                this.resetNodeSize();
            }


        }.bind(this));
    },
    createNode: function(){
        this.container = new Element("div.container", {
            "styles": this.css.container
        }).inject(this.node);

        this.contentContainer = new Element("div.contentContainerNode", {
            "styles": this.css[ this.options.type=="flow" ? "contentContainer_flow": "contentContainer" ]
        }).inject(this.container);
    },
    createTopNode: function(){
        var topNode = this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.contentContainer);

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

        this.inforNode = new Element("div",{
            "styles": this.css.inforNode
        }).inject( this.middleNode );

        this.planNode = new Element("div.listContainer", {
            styles : this.css.listContainer
        }).inject( this.middleNode );

        this.workNode = new Element("div.listContainer", {
            styles : this.css.listContainer
        }).inject( this.middleNode );

        this.planNodeNext = new Element("div.listContainer", {
            styles : this.css.listContainer
        }).inject( this.middleNode );

        this.loadInforContent();

        this.loadPlanTop( this.planNode, true, false );
        this.loadPlanView( this.planNode );

        this.loadWorkTop();
        this.loadWorkView();
        this.loadWorkAction();

        this.loadPlanTop( this.planNodeNext, true ,true );
        this.loadPlanViewNext( this.planNodeNext );
        this.loadPlanAction( this.planNodeNext );
    },
    loadInforContent: function(){
        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' >" +
            "<tr><td styles='formTableTitleP10' lable='createDateString'></td>" +
            "    <td styles='formTableValueP10' item='createDateString'></td>" +
            "   <td styles='formTableTitleP10' lable='targetPerson'></td>" +
            "    <td styles='formTableValueP10' item='targetPerson'></td>"+
            "   <td styles='formTableTitleP10' lable='activityName'></td>" +
            "    <td styles='formTableValueP10' item='activityName'></td>"+
            "   <td styles='formTableTitleP10' lable='currentPersonName'></td>" +
            "    <td styles='formTableValueP10' item='currentPersonName'></td>"+
            "   <td styles='formTableTitleP10' lable='reportObjType'></td>" +
            "    <td styles='formTableValueP10' item='reportObjType'></td>"+
            "</tr>" +
            //"<tr><td styles='formTableTitle' lable='title'></td>" +
            //"    <td styles='formTableValue' item='title'></td>" +
            //"   <td styles='formTableTitle' lable='workId'></td>" +
            //"    <td styles='formTableValue14' item='title'></td>"+
            //"</tr>" +
            "</table>";
        this.inforNode.set("html", html);
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            var form = new MForm(this.inforNode, this.data, {
                usesNewVersion : true,
                isEdited: false,
                style : "report",
                hasColon : true,
                itemTemplate: {
                    createDateString: { text : this.lp.reportDate, type : "innertext"},
                    targetPerson: { text : this.lp.targetPerson, type : "org", orgType : "person"},
                    reportObjType: { text : this.lp.reportObjType, type : "select", selectValue : ["","PERSON","UNIT"], selectText : ["","个人汇报","组织汇报"] },
                    activityName: { text : this.lp.activityName, type : "innertext"},
                    currentPersonName: { text : this.lp.currentPersonName, type : "org", orgType : "person"}
                }
            }, this);
            form.load();
        }.bind(this), true);
    },
    loadPlanTop: function( container, addEnable, isNextPlan ){
        var planTop = new Element("div.listTop", {
            "styles" : this.css.listTop,
            "text" : isNextPlan ? "下月工作计划" : "本月工作计划"
        }).inject( container );

        if( addEnable  && this.isEdited ){
            var listTopAction = new Element("div.listTopAction", {
                "styles" : this.css.listTopAction,
                "text" : "增加"
            }).inject( planTop );
            listTopAction.addEvents( {
                "mouseover" : function(){
                    this.node.setStyles( this._self.css.listTopAction_over )
                }.bind({_self : this , node : listTopAction }),
                "mouseout" : function(){
                    this.node.setStyles( this._self.css.listTopAction )
                }.bind({_self : this , node : listTopAction }),
                "click" : function(){
                    this._self.app.common.addPlan(this._self.data,  isNextPlan ? this._self.planViewNext : this._self.planView, isNextPlan );
                }.bind({_self : this })
            } )
        }
    },
    loadPlanView : function( container ){
        this.planViewNode = new Element("div").inject( container );
        this.planView = new MWF.xApplication.Report.Explorer.PlanView(this.planViewNode, this.app, this, {
            "style": "default",
            "templateUrl": "listItemPlan.json"
        });
        this.planView.load();
    },
    loadPlanViewNext : function( container ){
        this.planViewNextNode = new Element("div").inject( container );
        this.planViewNext = new MWF.xApplication.Report.Explorer.PlanViewNext(this.planViewNextNode, this.app, this, {
            "style": "default",
            "templateUrl": "listItemPlan.json"
        });
        this.planViewNext.load();
    },
    loadPlanAction: function(container){
        if(this.isEdited){
            this.addPlanNode = new Element("div.listAddAction", {
                styles : this.css.listAddActionContainer,
                events : {
                    "mouseover" : function(){
                        this.addPlanNode.setStyles( this.css.listAddActionContainer_over );
                        this.addPlanAction.setStyles( this.css.listAddAction_over )
                    }.bind(this),
                    "mouseout" : function(){
                        this.addPlanNode.setStyles( this.css.listAddActionContainer );
                        this.addPlanAction.setStyles( this.css.listAddAction )
                    }.bind(this),
                    "click" : function(){
                        this._self.app.common.addPlan(this._self.data, this._self.planViewNext, true);
                    }.bind({_self : this })
                }
            }).inject( container );

            this.addPlanAction = new Element("div.listAddAction", {
                styles : this.css.listAddAction,
                text : "增加计划"
            }).inject( this.addPlanNode );
        }

    },
    loadWorkTop: function( addEnable ){
        var workTop = new Element("div.listTop", {
            "styles" : this.css.listTop,
            "text" : "本月完成情况"
        }).inject( this.workNode );

        if( this.isEdited ){
            var listTopAction = new Element("div.listTopAction", {
                "styles" : this.css.listTopAction,
                "text" : "增加"
            }).inject( workTop );
            listTopAction.addEvents( {
                "mouseover" : function(){
                    this.node.setStyles( this._self.css.listTopAction_over )
                }.bind({_self : this , node : listTopAction }),
                "mouseout" : function(){
                    this.node.setStyles( this._self.css.listTopAction )
                }.bind({_self : this , node : listTopAction }),
                "click" : function(){
                    this._self.app.common.addWork( this._self.data, this._self.workView );
                }.bind({_self : this })
            } )
        }

    },
    loadWorkView : function(){
        this.workViewNode = new Element("div").inject(this.workNode);
        this.workView = new MWF.xApplication.Report.Explorer.WorkView(this.workViewNode, this.app, this, {
            "style": "default",
            "templateUrl": "listItemWork.json"
        });
        this.workView.load();
    },
    loadWorkAction : function(){
        if( this.isEdited ){
            this.addWorkNode = new Element("div.listAddAction", {
                styles : this.css.listAddActionContainer,
                events : {
                    "mouseover" : function(){
                        this.addWorkNode.setStyles( this.css.listAddActionContainer_over );
                        this.addWorkAction.setStyles( this.css.listAddAction_over )
                    }.bind(this),
                    "mouseout" : function(){
                        this.addWorkNode.setStyles( this.css.listAddActionContainer );
                        this.addWorkAction.setStyles( this.css.listAddAction )
                    }.bind(this),
                    "click" : function(){
                        this._self.app.common.addWork( this._self.data, this._self.workView );
                    }.bind({_self : this })
                }
            }).inject( this.workNode );
            this.addWorkAction = new Element("div.listAddAction", {
                styles : this.css.listAddAction,
                text : "增加工作"
            }).inject( this.addWorkNode );
        }
    },
    resetNodeSize: function () {
        var topSize = this.topNode ? this.topNode.getSize() : {"x": 0, "y": 0};
        var nodeSize = this.node.getSize();
        var pt = this.contentContainer.getStyle("padding-top").toFloat();
        var pb = this.contentContainer.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y  - pt - pb; //- topSize.y
        this.contentContainer.setStyle("height", "" + height + "px");
    }
});



MWF.xApplication.Report.Explorer.WorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.Report.Explorer.WorkDocument(this.viewNode, data, this.explorer, this, null, index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.actions.listWork( this.explorer.data.id, function( json ){
            if( callback )callback( json );
        })
    },
    _removeDocument: function (documentData, all) {
        this.app.common.deleteWork( documentData, e, function(){
            this.reload();
        }.bind(this))
    },
    _create: function () {
    },
    _openDocument: function (documentData) {
        this.app.common.openWork( documentData, this.explorer.data, this, this.explorer.isEdited );
    },
    _queryCreateViewNode: function () {
    },
    _postCreateViewNode: function (viewNode) {
    },
    _queryCreateViewHead: function () {
    },
    _postCreateViewHead: function (headNode) {
    }

});

MWF.xApplication.Report.Explorer.WorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {
    },
    open: function(  ){
        this.view._openDocument( this.data )
    },
    edit: function(node, ev){
        this.app.common.editWork( this.data, this.explorer.data, this.view );
        ev.stopPropagation();
    },
    delete : function(node, ev){
        this.app.common.deleteWork( this.data, ev, function(){
            this.view.reload();
        }.bind(this));
        ev.stopPropagation();
    }
});


MWF.xApplication.Report.Explorer.PlanView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.Report.Explorer.PlanDocument(this.viewNode, data, this.explorer, this, null, index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.actions.listPlan( this.explorer.data.id, function( json ){
            if( callback )callback( json );
        })
    },
    _create: function () {
    },
    _openDocument: function (documentData) {
        this.app.common.openPlan( documentData, this.explorer.data, this, false, this.explorer.isEdited );
    },
    _queryCreateViewNode: function () {
    },
    _postCreateViewNode: function (viewNode) {
    },
    _queryCreateViewHead: function () {
    },
    _postCreateViewHead: function (headNode) {
    }

});

MWF.xApplication.Report.Explorer.PlanDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {
    },
    open: function(  ){
        this.view._openDocument( this.data )
    },
    edit: function( node , ev ){
        this.app.common.editPlan( this.data, this.explorer.data, this.view, false );
        ev.stopPropagation();
    },
    delete : function(node, ev){
        this.app.common.deletePlan( this.data, ev, function (){
            this.view.reload();
        }.bind(this));
        ev.stopPropagation();
    }
});

MWF.xApplication.Report.Explorer.PlanViewNext = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.Report.Explorer.PlanDocumentNext(this.viewNode, data, this.explorer, this, null, index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.actions.listPlanNext( this.explorer.data.id, function( json ){
            if( callback )callback( json );
        })
    },
    _create: function () {
    },
    _openDocument: function (documentData) {
        this.app.common.openPlan( documentData, this.explorer.data, this.view, true );
    },
    _queryCreateViewNode: function () {
    },
    _postCreateViewNode: function (viewNode) {
    },
    _queryCreateViewHead: function () {
    },
    _postCreateViewHead: function (headNode) {
    }
});

MWF.xApplication.Report.Explorer.PlanDocumentNext = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {
    },
    open: function(  ){
        this.view._openDocument( this.data )
    },
    edit: function( node , ev ){
        this.app.common.editPlan( this.data, this.explorer.data, this.view, true );
        ev.stopPropagation();
    },
    delete : function(node, ev){
        this.app.common.deletePlanNext( this.data, ev, function () {
            this.view.reload();
        }.bind(this));
        ev.stopPropagation();
    }
});
