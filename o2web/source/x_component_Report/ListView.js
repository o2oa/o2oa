MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xApplication.Report.ListView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date": null,
        "isTodo" : false,
        "action" : ""
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "/x_component_Report/$ListView/";
        this.cssPath = "/x_component_Report/$ListView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.date = this.options.date || new Date();
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        //this.leftNode = new Element("div", {"styles": this.css.leftNode}).inject(this.node);
        this.contentAreaNode  = new Element("div.contentAreaNode", {"styles": this.css.contentAreaNode}).inject(this.node);
        this.contentNode  = new Element("div.contentNode", {"styles": this.css.contentNode}).inject(this.contentAreaNode);
        this.filterNode = new Element("div.filterNode", {"styles": this.css.filterNode}).inject(this.contentNode);
        this.viewNode = new Element("div.viewNode", {"styles": this.css.viewNode}).inject(this.contentNode);

        //this.loadSideBar();

        this.resetNodeSizeFun = this.resetNodeSize.bind(this);
        this.app.addEvent("resize", this.resetNodeSizeFun );

        if( this.options.isTodo ){
            this.toTodo();
        }else{
            this.toApplyAll();
        }

        this.resetNodeSize();

    },
    resetNodeSize: function(){
        //var size = this.container.getSize();
        //if (this.app.reportConfig.hideMenu=="static"){
        //    var y = size.y-120;
        //    this.node.setStyle("height", ""+y+"px");
        //    this.node.setStyle("margin-top", "60px");
        //}else{
        //    var y = size.y-20;
        //    this.node.setStyle("height", ""+y+"px");
        //}

        var size = this.container.getSize();
        var y = size.y-120;
        //this.node.setStyle("margin-top", "60px");
        //this.node.setStyle("height", ""+y+"px");
        if( !this.app.inContainer )this.viewNode.setStyle("height", ""+y+"px");
        //this.leftNode.setStyle("height", ""+size.y-60+"px");

        var sideBar = this.app.sideBar ? this.app.sideBar.getSize() : { x : 0, y : 0 };
        this.contentAreaNode.setStyle("margin-right",sideBar.x+"px");
    },
    loadLeftNavi: function(){
        var menuNode = new Element("div.menuNode", {"styles": this.css.menuNode, "text": this.app.lp.listNavi.myApply}).inject(this.leftNode);
        this.loadNaviItem(this.app.lp.listNavi.wait, "toApplyWait");
        this.loadNaviItem(this.app.lp.listNavi.processing, "toApplyProcessing");
        this.loadNaviItem(this.app.lp.listNavi.completed, "toApplyCompleted");
        this.loadNaviItem(this.app.lp.listNavi.allStatus, "toApplyAll");

        var menuNode = new Element("div.menuNode", {"styles": this.css.menuNode, "text": this.app.lp.listNavi.myReport}).inject(this.leftNode);
        //this.loadNaviItem(this.app.lp.listNavi.wait, "toAuditWait");
        this.loadNaviItem(this.app.lp.listNavi.waitProcessing, "toAuditProcessing");
        this.loadNaviItem(this.app.lp.listNavi.allStatus, "toAuditAll");
        //this.loadNaviItem(this.app.lp.listNavi.reject, "toReportReject");

        if( this.app.common.isAdmin() ){
            var menuNode = new Element("div.menuNode", {"styles": this.css.menuNode, "text": this.app.lp.listNavi.manage}).inject(this.leftNode);
            //this.loadNaviItem(this.app.lp.listNavi.wait, "toAuditWait");
            this.loadNaviItem(this.app.lp.listNavi.all, "toAll");
        }

        //var menuNode = new Element("div", {"styles": this.css.menuNode, "text": this.app.lp.listNavi.room}).inject(this.leftNode);
    },
    loadNaviItem: function(text, action){
        var itemNode = new Element("div", {"styles": this.css.menuItemNode, "text": text}).inject(this.leftNode);
        var _self = this;
        itemNode.addEvents({
            "mouseover": function(){if (_self.currentNavi != this) this.setStyles(_self.css.menuItemNode_over);},
            "mouseout": function(){if (_self.currentNavi != this) this.setStyles(_self.css.menuItemNode);},
            "click": function(){
                if (_self.currentNavi) _self.currentNavi.setStyles(_self.css.menuItemNode);
                _self.currentNavi = this;
                this.setStyles(_self.css.menuItemNode_current);
                if (_self[action]) _self[action]();
            }
        });
        itemNode.store("action",action);
        if( this.options.action == action){
            itemNode.click();
        }else if( action == "toApplyWait"){
            itemNode.click();
        }
    },
    loadFilter: function(options, callback){
        var opt = {};
        if( options ){
            opt = Object.merge( options, { onSearch : function( condition ){
                if(callback)callback( condition );
            }});
        }
        //if( this.filter ){
        //    this.filter.reload( opt );
        //}else{
        //    this.filter = new MWF.xApplication.Report.ReportFileter( this.filterNode, this.app, opt );
        //}
        if( this.filter ){
            this.filter.destroy();
        }
        this.filter = new MWF.xApplication.Report.ReportFileter( this.filterNode, this.app, opt );
    },
    toApplyWait: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Report.ListView.ApplyWait( this.viewNode, this.app, this, {
            templateUrl : this.path + this.options.style+"/listItem.json"
        });
        this.currentView.load();
    },
    toApplyProcessing: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Report.ListView.ApplyProcessing( this.viewNode, this.app, this, {
            templateUrl : this.path + this.options.style+"/listItem_apply_processing.json"
        });
        this.currentView.load();
    },
    toApplyCompleted: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Report.ListView.ApplyCompleted( this.viewNode, this.app, this, {
            templateUrl : this.path + this.options.style+"/listItem.json"
        });
        this.currentView.load();
    },
    toTodo: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Report.ListView.Todo( this.viewNode, this.app, this, {
            templateUrl : this.path + this.options.style+"/listItem_all.json"
        });
        this.currentView.load();
    },
    toApplyAll: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Report.ListView.ApplyAll( this.viewNode, this.app, this, {
            templateUrl : this.path + this.options.style+"/listItem_all.json"
        });
        this.currentView.load();
    },
    //toAuditWait: function(){
    //    if (this.currentView) this.currentView.destroy();
    //    this.currentView = new MWF.xApplication.Report.ListView.AuditWait(this);
    //},
    toAuditProcessing: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Report.ListView.AuditProcessing( this.viewNode, this.app, this, {
            templateUrl : this.path + this.options.style+"/listItem_all.json"
        });
        this.currentView.load();
    },
    toAuditAll: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Report.ListView.AuditAll( this.viewNode, this.app, this, {
            templateUrl : this.path + this.options.style+"/listItem_all.json"
        });
        this.currentView.load();
    },

    toAll: function(){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Report.ListView.All( this.viewNode, this.app, this, {
            templateUrl : this.path + this.options.style+"/listItem_all.json"
        });
        this.currentView.load();
    },



    hide: function(){
        var fx = new Fx.Morph(this.node, {
            "duration": "300",
            "transition": Fx.Transitions.Expo.easeOut
        });
        fx.start({
            "opacity": 0
        }).chain(function(){
            this.node.setStyle("display", "none");
        }.bind(this));

    },
    show: function(){
        this.node.setStyles(this.css.node);
        var fx = new Fx.Morph(this.node, {
            "duration": "800",
            "transition": Fx.Transitions.Expo.easeOut
        });
        this.app.fireAppEvent("resize");
        fx.start({
            "opacity": 1,
            "left": "0px"
        }).chain(function(){
            this.node.setStyles({
                "position": "static",
                "width": "auto"
            });
        }.bind(this));
    },
    reload: function(){
        this.app.reload();
    },
    recordStatus : function(){
        var action = "";
        if( this.currentNavi )action = this.currentNavi.retrieve("action");
        return {
            action : action
        };
    },
    destroy : function(){
        if( this.currentView ){
            this.currentView.destroy()
        }
        this.app.removeEvent("resize", this.resetNodeSizeFun );
        this.node.destroy();
    }

});


MWF.xApplication.Report.ListView.ApplyWait = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    options : {
        "scrollEnable" : true,
        "scrollType" : "window"
    },
    _createDocument: function(data, index){
        return new MWF.xApplication.Report.ListView.Document(this.viewNode, data, this.explorer, this, null,  index);
    },
    _getCurrentPageData: function(callback, count){
        if(!count)count=30;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        var filter = this.filterData || this.forceFilterCondition;
        if( this.sortType && this.sortField ){
            filter.orderField = this.sortField;
            filter.orderType = this.sortType;
        }
        this.actions.listReportNextWithFilter(id, count, filter, function(json){
            if( !json.data )json.data = [];
            if (callback)callback(json);
        });
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteSubject(documentData.id, function(json){
            this.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){},
    _openDocument: function( documentData,index ){
        //var appId = "ReportDocument"+documentData.id;
        //if (this.app.desktop.apps[appId]){
        //    this.app.desktop.apps[appId].setCurrent();
        //}else {
        //    this.app.desktop.openApplication(null, "ReportDocument", {
        //        "id" : documentData.id,
        //        "isEdited" : false,
        //        "isNew" : false
        //    });
        //}
        this.app.common.openReport(documentData, this);
    },
    _queryCreateViewNode: function(){
        this.forceFilterCondition = {
            targetList : [this.app.userName],
            reportStatus : "汇报者填写"
        };
    },
    _postCreateViewNode: function( viewNode ){
        if( !this.filterLoaded ) {
            this.explorer.loadFilter({
                items: ["reportType", "title", "year", "month", "reportDate", "reportObjType"],
                defaultResult: this.forceFilterCondition
            }, function (filterData) {
                this.filterData = filterData;
                this.reload();
            }.bind(this));
            this.filterLoaded = true;
        }
    },
    _queryCreateViewHead:function(){},
    _postCreateViewHead: function( headNode ){},
    destroyScroll: function(){
        if( this.scrollContainerFun ){
            var scrollNode = this.app.scrollNode ? this.app.scrollNode : this.container;
            scrollNode.removeEvent("scroll", this.scrollContainerFun );
            this.scrollContainerFun = null;
        }
    },
    setScroll: function(){
        var scrollNode = this.app.scrollNode ? this.app.scrollNode : this.container;
        scrollNode.setStyle("overflow","auto");
        this.scrollContainerFun = function(){
            if( !this.options.pagingEnable ){
                var scrollSize = scrollNode.getScrollSize();
                var clientSize = scrollNode.getSize();
                var scrollHeight = scrollSize.y - clientSize.y;
                if (scrollNode.scrollTop + 150 > scrollHeight ) {
                    if (! this.isItemsLoaded) this.loadElementList();
                }
            }
        }.bind(this);
        scrollNode.addEvent("scroll", this.scrollContainerFun )
    }
});

MWF.xApplication.Report.ListView.ApplyProcessing = new Class({
    Extends: MWF.xApplication.Report.ListView.ApplyWait,
    _getCurrentPageData: function(callback, count){
        if(!count)count=30;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        var filter = this.filterData || this.forceFilterCondition;
        if( this.sortType && this.sortField ){
            filter.orderField = this.sortField;
            filter.orderType = this.sortType;
        }
        this.actions.listReportNextWithFilter(id, count, filter, function(json){
            if( !json.data )json.data = [];
            if (callback)callback(json);
        });
    },
    _queryCreateViewNode: function(){
        this.forceFilterCondition = {
            targetList : [this.app.userName],
            reportStatus : "审核中"
        };
    },
    _postCreateViewNode: function( viewNode ){
        if( !this.filterLoaded ) {
            this.explorer.loadFilter({
                items: ["reportType", "title", "year", "month", "reportDate", "reportObjType", "activityList", "currentPersonList"],
                defaultResult: this.forceFilterCondition
            }, function (filterData) {
                this.filterData = filterData;
                this.reload();
            }.bind(this));
            this.filterLoaded = true;
        }
    }
});
MWF.xApplication.Report.ListView.ApplyCompleted = new Class({
    Extends: MWF.xApplication.Report.ListView.ApplyWait,
    _getCurrentPageData: function(callback, count){
        if(!count)count=30;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        var filter = this.filterData || this.forceFilterCondition;
        if( this.sortType && this.sortField ){
            filter.orderField = this.sortField;
            filter.orderType = this.sortType;
        }
        this.actions.listReportNextWithFilter(id, count, filter, function(json){
            if( !json.data )json.data = [];
            if (callback)callback(json);
        });
    },
    _queryCreateViewNode: function(){
        this.forceFilterCondition = {
            targetList : [this.app.userName],
            reportStatus : "已完成"
        };
    },
    _postCreateViewNode: function( viewNode ){
        if( !this.filterLoaded ){
            this.explorer.loadFilter({
                items : ["reportType","title","year","month","reportDate","reportObjType"],
                defaultResult : this.forceFilterCondition
            },function( filterData ){
                this.filterData = filterData;
                this.reload();
            }.bind(this));
            this.filterLoaded = true;
        }
    }
});

MWF.xApplication.Report.ListView.ApplyAll = new Class({
    Extends: MWF.xApplication.Report.ListView.ApplyWait,
    _getCurrentPageData: function(callback, count){
        if(!count)count=30;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        var filter = this.filterData || this.forceFilterCondition;
        filter = Object.merge( {
            "title":"",
            "reportType":"",
            "year":"",
            "month":"",
            "week":"",
            "workIds":[],
            "reportDate":"",
            "createDate":"","targetList":[],
            "unitList":[],"activityList":[],"currentPersonList":[],
            "orderField":"","reportObjType":"","reportStatus":"",
            "orderType":"","permission":""}, filter );
        if( this.sortType && this.sortField ){
            filter.orderField = this.sortField;
            filter.orderType = this.sortType;
        }else if(this.sortTypeDefault && this.sortFieldDefault){
            filter.orderField = this.sortFieldDefault;
            filter.orderType = this.sortTypeDefault;
        }
        if( !filter.reportStatus ){
            filter.reportStatus="";
        }
        filter.permission = "";

        this.actions.listReportNextWithFilter(id, count, filter, function(json){
            if( !json.data )json.data = [];
            if (callback)callback(json);
        });
    },
    _queryCreateViewNode: function(){
        this.forceFilterCondition = {
            //targetList : [this.app.userName]
        };
    },
    _postCreateViewNode: function( viewNode ){
        if( !this.filterLoaded ){
            this.explorer.loadFilter({
                items : ["reportType","title","year","month","reportDate","reportObjType","reportStatus", "activityList"],
                defaultResult : this.forceFilterCondition
            },function( filterData ){
                this.filterData = filterData;
                this.reload();
            }.bind(this));
            this.filterLoaded = true;
        }
    }
});

MWF.xApplication.Report.ListView.Todo = new Class({
    Extends: MWF.xApplication.Report.ListView.ApplyWait,
    _getCurrentPageData: function(callback, count){
        if(!count)count=30;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.filterData || this.forceFilterCondition;
        filter = Object.merge( {
            "title":"",
            "reportType":"",
            "year":"",
            "month":"",
            "week":"",
            "workIds":[],
            "reportDate":"",
            "createDate":"","targetList":[],
            "unitList":[],"activityList":[],"currentPersonList":[],
            "orderField":"","reportObjType":"","reportStatus":"",
            "orderType":"","permission":""}, filter );
        if( this.sortType && this.sortField ){
            filter.orderField = this.sortField;
            filter.orderType = this.sortType;
        }else if(this.sortTypeDefault && this.sortFieldDefault){
            filter.orderField = this.sortFieldDefault;
            filter.orderType = this.sortTypeDefault;
        }
        if( !filter.reportStatus ){
            //filter.reportStatus="";
        }
        filter.permission = "作者";
        this.actions.listReportNextWithFilter(id, count, filter, function(json){
            if( !json.data )json.data = [];
            if (callback)callback(json);
        });
    },
    _queryCreateViewNode: function(){
        this.forceFilterCondition = {
            //targetList : [this.app.userName]
        };
    },
    _postCreateViewNode: function( viewNode ){
        //this.explorer.filterNode.setStyle("height","10px");
        if( !this.filterLoaded ){
            this.explorer.loadFilter({
                items : ["reportType","title","year","month","reportDate","reportObjType","reportStatus", "activityList"],
                defaultResult : this.forceFilterCondition
            },function( filterData ){
                this.filterData = filterData;
                this.reload();
            }.bind(this));
            this.filterLoaded = true;
        }
    }
});

MWF.xApplication.Report.ListView.AuditWait = new Class({
    Extends: MWF.xApplication.Report.ListView.ApplyWait,
    _getCurrentPageData: function(callback, count){
        if(!count)count=30;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        var filter = this.filterData || this.forceFilterCondition;
        if( this.sortType && this.sortField ){
            filter.orderField = this.sortField;
            filter.orderType = this.sortType;
        }
        this.actions.listReportNextWithFilter(id, count, filter, function(json){
            if( !json.data )json.data = [];
            if (callback)callback(json);
        });
    },
    _queryCreateViewNode: function(){
        this.forceFilterCondition = {
            currentPersonList : [this.app.userName],
            reportStatus : "审核中"
        };
    },
    _postCreateViewNode: function( viewNode ){
        if( !this.filterLoaded ) {
            this.explorer.loadFilter({
                items: ["reportType", "title", "year", "month", "reportDate", "targetList", "activityList", "reportObjType"],
                defaultResult: this.forceFilterCondition
            }, function (filterData) {
                this.filterData = filterData;
                this.reload();
            }.bind(this));
            this.filterLoaded = true;
        }
    }
});
MWF.xApplication.Report.ListView.AuditProcessing = new Class({
    Extends: MWF.xApplication.Report.ListView.ApplyWait,
    _getCurrentPageData: function(callback, count){
        if(!count)count=30;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        var filter = this.filterData || this.forceFilterCondition;
        if( this.sortType && this.sortField ){
            filter.orderField = this.sortField;
            filter.orderType = this.sortType;
        }
        if( !filter.reportStatus ){
            filter.reportStatus="";
        }
        this.actions.listReportNextWithFilter(id, count, filter, function(json){
            if( !json.data )json.data = [];
            if (callback)callback(json);
        });
    },
    _queryCreateViewNode: function(){
        this.forceFilterCondition = {
            currentPersonList : [this.app.userName],
            reportStatus : "审核中"
        };
    },
    _postCreateViewNode: function( viewNode ){
        if( !this.filterLoaded ){
            this.explorer.loadFilter({
                items : ["reportType","title","year","month","reportDate","targetList","activityList","reportObjType"],
                defaultResult : this.forceFilterCondition
            },function( filterData ){
                this.filterData = filterData;
                this.reload();
            }.bind(this));
            this.filterLoaded = true;
        }
    }
});
MWF.xApplication.Report.ListView.AuditAll = new Class({
    Extends: MWF.xApplication.Report.ListView.ApplyWait,
    _getCurrentPageData: function(callback, count){
        if(!count)count=30;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        var filter = this.filterData || this.forceFilterCondition;
        if( this.sortType && this.sortField ){
            filter.orderField = this.sortField;
            filter.orderType = this.sortType;
        }
        if( !filter.reportStatus ){
            //filter.reportStatus="";
        }
        this.actions.listMyAuditNextWithFilter(id, count, filter, function(json){
            if( !json.data )json.data = [];
            if (callback)callback(json);
        });
    },
    _queryCreateViewNode: function(){
        this.forceFilterCondition = {
            //currentPersonList : [this.app.userName],
            //reportStatus : "已完成"
        };
    },
    _postCreateViewNode: function( viewNode ){
        if( !this.filterLoaded ){
            this.explorer.loadFilter({
                items : ["reportType","title","year","month","reportDate","targetList","activityList","reportStatus","reportObjType","currentPersonList"],
                defaultResult : this.forceFilterCondition
            },function( filterData ){
                this.filterData = filterData;
                this.reload();
            }.bind(this));
            this.filterLoaded = true;
        }
    }
});
MWF.xApplication.Report.ListView.All = new Class({
    Extends: MWF.xApplication.Report.ListView.ApplyWait,
    _getCurrentPageData: function(callback, count){
        if(!count)count=30;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        var filter = this.filterData || this.forceFilterCondition;
        var flag = false;
        if( !filter.reportStatus ){
            filter.reportStatus="";
        }
        //filter.permission = "";
        //filter = {"title":"","reportType":"","year":"","month":"","week":"","workIds":[],"reportDate":"","createDate":"","targetList":[],"unitList":[],"activityList":[],"currentPersonList":[],"orderField":"","reportObjType":"","reportStatus":"","orderType":"","permission":""}

        if( this.sortType && this.sortField ){
            filter.orderField = this.sortField;
            filter.orderType = this.sortType;
        }
        this.actions.listReportNextWithFilter(id, count, filter, function(json){
            if( !json.data )json.data = [];
            if (callback)callback(json);
        });
    },
    _queryCreateViewNode: function(){
        this.forceFilterCondition = {
        };
    },
    _postCreateViewNode: function( viewNode ){
        if( !this.filterLoaded ){
            this.explorer.loadFilter({
                items :  ["reportType","title","year","month","reportDate","targetList","activityList","currentPersonList","reportStatus","reportObjType"],
                defaultResult : this.forceFilterCondition
            },function( filterData ){
                this.filterData = filterData;
                this.reload();
            }.bind(this));
            this.filterLoaded = true;
        }
    }
});

MWF.xApplication.Report.ListView.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if(this.index % 2 == 1){
            itemNode.setStyle("background-color","#f0f0f0");
        }
    },
    open: function (e) {
        this.view._openDocument(this.data, this.index);
    },
    edit : function(){
        var appId = "ReportDocument"+this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ReportDocument", {
                "id" : this.data.id,
                "isEdited" : true,
                "isNew" : false
            });
        }
    },
    remove : function(){

    },
    mouseoverDocument : function(){
        this.node.setStyle("background-color","#fff7eb");
    },
    mouseoutDocument : function(){
        if(this.index % 2 == 1){
            this.node.setStyle("background-color","#f0f0f0");
        }else{
            this.node.setStyle("background-color","#fff");
        }
    }
});