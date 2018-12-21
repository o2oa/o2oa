MWF.xApplication.Report = MWF.xApplication.Report || {};
MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("Report", "Access", null, false);
//MWF.xDesktop.requireApp("Report", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Report", "Common", null, false);
MWF.xDesktop.requireApp("Report", "Setting", null, false);

MWF.xApplication.Report.options = {
    multitask: false,
    executable: true
};
MWF.xApplication.Report.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "Report",
        "view" : "all",
        "icon": "icon.png",
        "width": "1220",
        "height": "700",
        "isResize": true,
        "isMax": true,
        "sideBarEnable" : true,
        "title": MWF.xApplication.Report.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.Report.LP;
    },
    loadApplication: function (callback) {
        MWF.UD.getDataJson("reportConfig", function (json) {
            this.reportConfig = json || {};
            if (!this.options.isRefresh) {
                this.maxSize(function () {
                    this.loadLayout();
                }.bind(this));
            } else {
                this.loadLayout();
            }
            if (callback) callback();
        }.bind(this));
    },
    loadLayout : function(){
        this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;
        this.restActions =  MWF.Actions.get("x_report_assemble_control"); //new MWF.xApplication.Report.Actions.RestActions();
        this.strategyActions =  MWF.Actions.get("x_strategydeploy_assemble_control");

        this.common = new MWF.xApplication.Report.Common(this);

        this.path = "/x_component_Report/$Main/" + this.options.style + "/";

        //this.restActions.getIdentity( this.userName, function( json ){
        //    this.identityList = json.data.identityList;
        //    this.identityCNList = [];
        //    this.identityList.each( function( id ){
        //        this.identityCNList.push(id.split("@")[0])
        //    }.bind(this));
        //    this.identityCNList.unique();
        //
        //}.bind(this))
        this.createNode();
        this.loadApplicationContent();
    },
    loadController: function (callback) {
        this.isAdmin = this.common.isAdmin();
        if( this.isAdmin ){
            this.exportAllFlag = true;
        }else{
            this.exportAllFlag = this.common.hasExportAllUnitPermission();
        }
        this.unitWithExport = [];
        if( !this.exportAllFlag ){
            this.common.getUnitWithExportPermission( function( unit ){
                this.unitWithExport = unit;
                if (callback)callback();
            }.bind(this))
        }else{
            //this.isAdmin = MWF.AC.isAdministrator();
            if (callback)callback();
        }

    },
    isShowExport : function(){
        if( this.isAdmin )return true;
        if( this.exportAllFlag )return true;
        if( this.unitWithExport && this.unitWithExport.length > 0 )return true;
        return false;
    },
    createNode: function(){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div.reportNode", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden", "background-color":"#eee"}
        }).inject(this.content);
    },
    loadApplicationContent: function () {
        this.loadController(function () {
            this.loadApplicationLayout();
        }.bind(this))
    },
    loadApplicationLayout: function () {
        if( this.status && this.status.action ){
            this.defaultAction = this.status.action;
        }else if (this.reportConfig.defaultView){
            this.defaultAction = this.reportConfig.defaultView;
        }else if( this.options.view == "todo" ) {
            this.defaultAction = "toTodo";
        }else{
            this.defaultAction = "toList";
        }

        if( (this.common.isAdmin() || this.isShowExport()) && this.options.view == "all" ){
            this.topMenu = new Element("div", {"styles": this.css.topMenu}).inject(this.node);
        }
        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);

        if( this.options.view == "todo" ){
            this.toTodo();
        }else if( (this.common.isAdmin() || this.isShowExport()) && this.options.view == "all" ){
            this.loadTopMenus();
        }else{
            this.toList();
        }

        if( this.options.sideBarEnable ){
            //this.loadSideBar();
        }

    },
    loadTopMenus_right: function(){
        this.topMenuRight = new Element("div", {"styles": this.css.topMenuRight }).inject(this.topMenu);

        //this.createTopMenu_right(this.lp.addReport, "icon_newapply", "addReport");

        var configNode = this.createTopMenu_right(this.lp.setting, "icon_shezhi", "config");
        configNode.setStyle("float", "right");
    },
    createTopMenu_right : function(text, icon, action){
        var actionNode = new Element("div", {"styles": this.css.topMenuNode_right, "title" : text}).inject(this.topMenuRight);
        var actionIconNode = new Element("div", {"styles": this.css.topMenuIconNode}).inject(actionNode);
        actionIconNode.setStyle("background", "url(/x_component_Report/$Main/default/icon/"+icon+".png) no-repeat center center");
        actionNode.store("icon",icon);
        actionNode.store("iconNode",actionIconNode);

        var _self = this;
        actionNode.addEvents({
            "mouseover": function(){
                this.node.retrieve("iconNode").setStyle( "background","url(/x_component_Report/$Main/default/icon/"+this.node.retrieve("icon")+"_click.png) no-repeat center center" );
            }.bind( { node : actionNode } ),
            "mouseout": function(){
                this.node.retrieve("iconNode").setStyle( "background","url(/x_component_Report/$Main/default/icon/"+this.node.retrieve("icon")+".png) no-repeat center center" );
            }.bind({ node:actionNode }),
            "click": function(){
                this.node.retrieve("iconNode").setStyle( "background","url(/x_component_Report/$Main/default/icon/"+this.node.retrieve("icon")+"_click.png) no-repeat center center" );
                if (_self[action]) _self[action].apply(_self);
            }.bind({ node : actionNode })
        });
        return actionNode;
    },
    loadTopMenus: function(){
        this.createTopMenu(this.lp.list, "icon_liebiao", "toList");
        //this.createTopMenu(this.lp.month, "icon_yue", "toMonth");
        //this.createTopMenu(this.lp.day, "icon_ri", "toDay");
        //this.createTopMenu(this.lp.mind, "icon_naotu", "toKeyWork");
        if( this.isShowExport() ){
            this.createTopMenu( "导出", "icon_export", "toStatistics", true);
            this.createTopMenu( "部门五项重点工作统览", "icon_tongji", "toSummarization", true);
        }
        if( this.common.isAdmin() ){
            this.createTopMenu( this.lp.startRecord, "icon_liebiao", "toStartRecord");
            this.loadTopMenus_right();
        }

    },
    createTopMenu: function(text, icon, action, isPop){
        var actionNode = new Element("div", {"styles": this.css.topMenuNode}).inject(this.topMenu);
        var actionIconNode = new Element("div", {"styles": this.css.topMenuIconNode}).inject(actionNode);
        actionIconNode.setStyle("background", "url(/x_component_Report/$Main/default/icon/"+icon+".png) no-repeat center center");
        var actionTextNode = new Element("div", {"styles": this.css.topMenuTextNode, "text": text}).inject(actionNode);
        actionNode.store("icon",icon);
        actionNode.store("iconNode",actionIconNode);
        actionNode.store("action",action);

        var _self = this;
        actionNode.addEvents({
            "mouseover": function(){
                if( this.node != _self.currentTopMenuNode ){
                    this.node.setStyles(_self.css.topMenuNode_over);
                    this.node.retrieve("iconNode").setStyle( "background","url(/x_component_Report/$Main/default/icon/"+this.node.retrieve("icon")+"_click.png) no-repeat center center" );
                }
            }.bind( { node : actionNode } ),
            "mouseout": function(){
                if(this.node != _self.currentTopMenuNode){
                    this.node.setStyles(_self.css.topMenuNode);
                    this.node.retrieve("iconNode").setStyle( "background","url(/x_component_Report/$Main/default/icon/"+this.node.retrieve("icon")+".png) no-repeat center center" );
                }
            }.bind({ node:actionNode }),
            "click": function(){
                if( !isPop ){
                    if( this.node != _self.currentTopMenuNode ){
                        this.node.setStyles( _self.css.topMenuNode_down );
                        this.node.retrieve("iconNode").setStyle( "background","url(/x_component_Report/$Main/default/icon/"+this.node.retrieve("icon")+"_click.png) no-repeat center center" );
                    }
                    if( _self.currentTopMenuNode && this.node != _self.currentTopMenuNode){
                        _self.currentTopMenuNode.setStyles( _self.css.topMenuNode );
                        _self.currentTopMenuNode.retrieve("iconNode").setStyle( "background","url(/x_component_Report/$Main/default/icon/"+_self.currentTopMenuNode.retrieve("icon")+".png) no-repeat center center" );
                    }
                    _self.currentTopMenuNode = this.node;
                }
                if (_self[action]) _self[action].apply(_self);
            }.bind({ node : actionNode })
        });
        if( this.defaultAction == action ){
            actionNode.click();
        }
        return actionNode;
    },
    showMenu: function(){
        if (this.menuMode!="show") {
            this.topMenu.set("tween", {duration: 100, transition: "bounce:out"});
            this.topMenu.tween("top", "-50px", "0px");
            this.menuMode = "show";

        }
    },
    hideCurrentView: function(){
        if (this.currentView){
            this.currentView.hide();
            this.currentView = null;
        }
    },
    toMyReport: function(){
        this.contentNode.setStyles(this.css.contentNode);
        if( this.currentView ){
            this.currentView.destroy();
            this.currentView = null;
        }
        this.myReportView = null;
        this.getMyReportView(function(){
            this.myReportView.show();
            this.currentView = this.myReportView;
        }.bind(this));
    },
    getMyReportView: function(callback){
        if (!this.myReportView){
            MWF.xDesktop.requireApp("Report", "ReportView", function(){
                var options;
                if( this.status && this.status.options ){
                    options = this.status.options
                }
                this.myReportView = new MWF.xApplication.Report.ReportView(this.contentNode, this, options);
                if( options )this.status.options = null;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    toTodo: function(){
        if( this.currentView ){
            this.currentView.destroy();
            this.currentView = null;
        }
        this.todoView = null;
        this.getTodoView(function(){
            this.todoView.show();
            this.currentView = this.todoView;
        }.bind(this));
    },
    getTodoView: function(callback){
        if (!this.todoView){
            MWF.xDesktop.requireApp("Report", "ListView", function(){
                var options;
                if( this.status && this.status.options ){
                    options = this.status.options
                }
                options = options || {};
                options.isTodo = true;
                this.todoView = new MWF.xApplication.Report.ListView(this.contentNode, this, options);
                if( this.status && this.status.options )this.status.options = null;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    toList: function(){
        //this.contentNode.setStyle("background", "#f7f7f7");
        if( this.currentView ){
            this.currentView.destroy();
            this.currentView = null;
        }
        this.listView = null;
        this.getListView(function(){
            this.listView.show();
            this.currentView = this.listView;
        }.bind(this));
    },
    getListView: function(callback){
        if (!this.listView){
            MWF.xDesktop.requireApp("Report", "ListView", function(){
                var options;
                if( this.status && this.status.options ){
                    options = this.status.options
                }
                this.listView = new MWF.xApplication.Report.ListView(this.contentNode, this, options);
                if( options )this.status.options = null;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    toMonth: function(){
        //this.contentNode.setStyle("background", "#f7f7f7");
        if( this.currentView ){
            this.currentView.destroy();
            this.currentView = null;
        }
        this.monthView = null;
        this.getMonthView(function(){
            this.monthView.show();
            this.currentView = this.monthView;
        }.bind(this));
    },
    getMonthView: function(callback){
        if (!this.monthView){
            MWF.xDesktop.requireApp("Report", "MonthView", function(){
                var options;
                if( this.status && this.status.options ){
                    options = this.status.options
                }
                this.monthView = new MWF.xApplication.Report.MonthView(this.contentNode, this, options);
                if( options )this.status.options = null;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    toDay: function(d){
        //this.contentNode.setStyle("background", "#f7f7f7");
        if( this.currentView ){
            this.currentView.destroy();
            this.currentView = null;
        }
        this.dayView = null;
        this.getDayView(function(){
            this.dayView.show();
            this.currentView = this.dayView;
        }.bind(this), d);
    },
    getDayView: function(callback, d){
        if (!this.dayView){
            MWF.xDesktop.requireApp("Report", "DayView", function(){
                var options;
                if( this.status && this.status.options ){
                    options = this.status.options
                }
                this.dayView = new MWF.xApplication.Report.DayView(this.contentNode, this, options || {"date": d});
                if(this.status)this.status.options = null;
                if (callback) callback();
            }.bind(this));
        }else{
            this.dayView.toDay(d);
            if (callback) callback();
        }
    },

    toKeyWork : function(d){
        //this.contentNode.setStyle("background", "#f7f7f7");
        if( this.currentView ){
            this.currentView.destroy();
            this.currentView = null;
        }
        this.mindView = null;
        this.getKeyWorkView(function(){
            this.mindView.load();
            this.currentView = this.mindView;
        }.bind(this), d);
    },
    getKeyWorkView: function(callback, d){
        if (!this.mindView){
            MWF.xDesktop.requireApp("Report", "MindView", function(){
                var options;
                if( this.status && this.status.options ){
                    options = this.status.options
                }
                this.mindView = new MWF.xApplication.Report.MindView(this.contentNode, this, this.restActions, options || {"date": d});
                if(this.status)this.status.options = null;
                if (callback) callback();
            }.bind(this));
        }else{
            this.mindView.load(d);
            if (callback) callback();
        }
    },

    toStatistics : function(){
      var form = new MWF.xApplication.Report.StatisticsForm( this, {}, {}, { app : this } );
        form.create();
    },

    toSummarization : function(){
        var form = new MWF.xApplication.Report.SummarizationForm( this, {}, {}, { app : this } );
        form.create();
    },

    //toStatistics : function(d){
    //    if( this.currentView ){
    //        this.currentView.destroy();
    //        this.currentView = null;
    //    }
    //    this.statisticsView = null;
    //    this.getStatisticsView(function(){
    //        this.statisticsView.load();
    //        this.currentView = this.statisticsView;
    //    }.bind(this), d);
    //},
    //getStatisticsView: function(callback, d){
    //    if (!this.statisticsView){
    //        MWF.xDesktop.requireApp("Report", "StatisticsView", function(){
    //            var options;
    //            if( this.status && this.status.options ){
    //                options = this.status.options
    //            }
    //            this.statisticsView = new MWF.xApplication.Report.StatisticsView(this.contentNode, this, this.restActions, options || {"date": d});
    //            if(this.status)this.status.options = null;
    //            if (callback) callback();
    //        }.bind(this));
    //    }else{
    //        this.statisticsView.load(d);
    //        if (callback) callback();
    //    }
    //},

    toStartRecord: function(){
        //this.contentNode.setStyle("background", "#f7f7f7");
        if( this.currentView ){
            this.currentView.destroy();
            this.currentView = null;
        }
        this.startRecordView = null;
        this.getStartRecordView(function(){
            this.startRecordView.show();
            this.currentView = this.startRecordView;
        }.bind(this));
    },
    getStartRecordView: function(callback){
        if (!this.startRecordView){
            MWF.xDesktop.requireApp("Report", "StartRecordView", function(){
                var options;
                if( this.status && this.status.options ){
                    options = this.status.options
                }
                this.startRecordView = new MWF.xApplication.Report.StartRecordView(this.contentNode, this, options);
                if( options )this.status.options = null;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    config: function(){
        //new MWF.xApplication.Report.Config(this, this.reportConfig);
        var setting = new MWF.xApplication.Report.SettingForm(this, null, {
            height : this.common.isAdmin() ? "600" : "300",
            width : this.common.isAdmin() ? "1000" : "700"
        }, {
            app : this
        });
        setting.edit();
    },
    recordStatus: function(){
        return {
            action : this.currentTopMenuNode ? this.currentTopMenuNode.retrieve("action") : "toList",
            options : (this.currentView && this.currentView.recordStatus) ? this.currentView.recordStatus() : null
        };
    },
    reload: function( ){
        this.refresh()
    },
    loadSideBar : function(){
        this.sideBar = new MWF.xApplication.Report.SideBar(this.node, this);
        this.sideBar.show();
    },
    addReport : function(){
        //var data = { id : "94949494948" }; //this.data;
        //var appId = "ReportDocument"+data.id;
        //if (this.desktop.apps[appId]){
        //    this.desktop.apps[appId].setCurrent();
        //}else {
        //    this.desktop.openApplication(null, "ReportDocument", {
        //        "id" : data.id,
        //        "isEdited" : false,
        //        "isNew" : false
        //    });
        //}

        //var form = new MWF.xApplication.Report.CustomWorkForm( this, { "reportId" : this.options.id }, {}, {
        //    app : this
        //} );
        //form.create();
        //MWF.xDesktop.requireApp("Template", "Test", null, false);
        //var test = new MWF.xApplication.Template.Test(this, { "reportId" : this.options.id }, {}, {
        //        app : this
        //});
        //test.open();
    }
});

var getDateDiff = function (publishTime) {
    if(!publishTime)return "";
    var dateTimeStamp = Date.parse(publishTime.replace(/-/gi, "/"));
    var minute = 1000 * 60;
    var hour = minute * 60;
    var day = hour * 24;
    var halfamonth = day * 15;
    var month = day * 30;
    var year = month * 12;
    var now = new Date().getTime();
    var diffValue = now - dateTimeStamp;
    if (diffValue < 0) {
        //若日期不符则弹出窗口告之
        //alert("结束日期不能小于开始日期！");
    }
    var yesterday = new Date().decrement('day', 1);
    var beforYesterday = new Date().decrement('day', 2);
    var yearC = diffValue / year;
    var monthC = diffValue / month;
    var weekC = diffValue / (7 * day);
    var dayC = diffValue / day;
    var hourC = diffValue / hour;
    var minC = diffValue / minute;
    if (yesterday.getFullYear() == dateTimeStamp.getFullYear() && yesterday.getMonth() == dateTimeStamp.getMonth() && yesterday.getDate() == dateTimeStamp.getDate()) {
        result = "昨天 " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (beforYesterday.getFullYear() == dateTimeStamp.getFullYear() && beforYesterday.getMonth() == dateTimeStamp.getMonth() && beforYesterday.getDate() == dateTimeStamp.getDate()) {
        result = "前天 " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (yearC > 1) {
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (monthC >= 1) {
        //result= parseInt(monthC) + "个月前";
        // s.getFullYear()+"年";
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (weekC >= 1) {
        result = parseInt(weekC) + "周前";
    } else if (dayC >= 1) {
        result = parseInt(dayC) + "天前";
    } else if (hourC >= 1) {
        result = parseInt(hourC) + "小时前";
    } else if (minC >= 1) {
        result = parseInt(minC) + "分钟前";
    } else
        result = "刚刚发表";
    return result;
};


