var MWFCalendar = MWF.xApplication.Calendar  = MWF.xApplication.Calendar || {};
MWF.require("MWF.xDesktop.UserData", null, false);
MWF.require("MWF.xAction.org.express.RestActions", null,false);
//MWF.xDesktop.requireApp("Calendar", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Calendar", "Common", null, false);
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xApplication.Calendar.options.multitask = false;
MWFCalendar.LeftNaviWidth = 250;
MWF.xApplication.Calendar.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "Calendar",
        "icon": "icon.png",
        "width": "1000",
        "height": "600",
        "isResize": true,
        "isMax": true,
        "eventId" : "",
        "title": MWF.xApplication.Calendar.LP.title
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.Calendar.LP;
        this.menuMode="show";
        this.isManager = MWF.AC.isAdministrator();
        this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;
        if (!this.actions) this.actions = MWF.Actions.get("x_calendar_assemble_control"); //new MWF.xApplication.Calendar.Actions.RestActions();
        //if (!this.personActions) this.personActions = MWF.Actions.get("x_organization_assemble_express");
    },
    loadApplication: function(callback) {
        this.canlendarData = null;
        MWF.UD.getDataJson("calendarConfig", function(json){
            this.calendarConfig = json || {};

            MWF.UD.getPublicData("calendarConfig", function(json){
                var jsonData = json || {};
                if (jsonData.process){
                    this.calendarConfig.process = jsonData.process;
                }else{
                    this.calendarConfig.process = null;
                }
                if( jsonData.weekBegin ){
                    this.calendarConfig.weekBegin = jsonData.weekBegin;
                }
                for( var key in jsonData ){
                    if( key != "process" && key != "weekBegin" ){
                        this.calendarConfig[ key ] = jsonData[key];
                    }
                }
                if( !this.calendarConfig.disableViewList ) this.calendarConfig.disableViewList = [];

                this.createNode();
                if (!this.options.isRefresh) {
                    this.maxSize(function () {
                        this.listCalendar( function(){
                            this.loadLayout();
                        }.bind(this));
                    }.bind(this));
                } else {
                    this.listCalendar( function(){
                        this.loadLayout();
                    }.bind(this))
                }
                if (callback) callback();

            }.bind(this));
        }.bind(this));
    },
    listCalendar : function( callback ){
        if( this.canlendarData ){
            if(callback)callback( this.canlendarData )
        }else{
            this.actions.listMyCalendar( function( json ){
                if( ( json.data.myCalendars || [] ).length == 0 ){
                    this.createDefaultCalendar(function(){
                        if(callback)callback( json.data )
                    });
                }else{
                    this.canlendarData = json.data;
                    this.calendarDataList = json.data.myCalendars;
                    this.currentCalendarData = json.data.myCalendars[0];
                    if(callback)callback( json.data )
                }
            }.bind(this))
        }
    },
    getSelectedCalendarId : function(){
        if( this.leftNavi ){
            return this.leftNavi.getSelectedCalendarId();
        }else{
            return null;
        }

    },
    createDefaultCalendar : function( callback ){
      // this.actions.saveCalendar({
      //     name : "我的日历",
      //     type : "person",
      //     color : "",
      //     description : "",
      //     source : "PERSON",
      //     isPublic : false//,
      //     //manageablePersonList : [this.userName]
      // }, function(){
          this.actions.listMyCalendar( function( json ){
              if( ( json.data.myCalendars || [] ).length == 0 ){
              }else{
                  this.canlendarData = json.data;
                  this.calendarDataList = json.data.myCalendars;
                  this.currentCalendarData = json.data.myCalendars[0];
                  if(callback)callback()
              }
          }.bind(this))
      // }.bind(this))
    },
    createNode: function(){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.content);

        this.naviContainerNode = new Element("div.naviContainerNode", {
            "styles": this.css.naviContainerNode
        }).inject(this.node);
        this.leftTitleNode = new Element("div.leftTitleNode", {
            "styles": this.css.leftTitleNode
        }).inject(this.naviContainerNode);

        this.rightContentNode = new Element("div", {
            "styles":this.css.rightContentNode
        }).inject(this.node);
    },
    loadLayout: function(){
        debugger;
        if( this.status && this.status.action ) {
            this.defaultAction = this.status.action;
        }else if( this.options.defaultAction ){
                this.defaultAction = this.options.defaultAction;
        }else if (this.calendarConfig.defaultView){
            this.defaultAction = this.calendarConfig.defaultView;
        }else{
            this.defaultAction = "toMonth";
        }
        if( this.calendarConfig.disableViewList.contains( this.defaultAction ) ){
            this.defaultAction = "";
        }

        this.loadNaviTitleNode();
        this.loadLeftNavi();

        this.topMenu = new Element("div", {"styles": this.css.topMenu}).inject(this.rightContentNode);
        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.rightContentNode);

        this.loadTopMenus();
        //this.loadSideBar();


        this.resizeNodes();
        this.resizeNodesFun = this.resizeNodes.bind(this);
        this.addEvent("resize", this.resizeNodesFun )


    },
    loadNaviTitleNode: function(){
        this.titleContentNode = new Element("div.titleContentNode", {
            "styles": this.css.titleContentNode
        }).inject(this.leftTitleNode);

        this.newCalendarNode = new Element("div", {
            styles : this.css.newCalendarNode,
            text : this.lp.createNewCalendar,
            events : {
                mouseover : function( ev ){ ev.target.setStyles( this.css.newCalendarNode_over ); }.bind(this),
                mouseout : function( ev ){ ev.target.setStyles( this.css.newCalendarNode ); }.bind(this),
                click : function(){ this.addCalendar() }.bind(this)
            }
        }).inject( this.titleContentNode );

        //var iconAreaNode = this.iconAreaNode = new Element("div",{
        //    "styles" : this.css.titleIconAreaNode
        //}).inject(this.titleContentNode);
        //
        //var iconNode = this.iconNode = new Element("img",{
        //    "styles" : this.css.titleIconNode,
        //    "src" : "../x_component_Calendar/$Main/appicon.png"
        //}).inject(iconAreaNode);
        //
        //this.titleTextNode = new Element("div.titleTextNode", {
        //    "styles": this.css.titleTextNode,
        //    "text": this.lp.title
        //}).inject(this.titleContentNode);

        //this.titleDescriptionNode =  new Element("div.titleDescriptionNode", {
        //    "styles": this.css.titleDescriptionNode
        //}).inject(this.titleContentNode);
    },
    loadLeftNavi : function(){
        this.naviNode = new Element("div.naviNode", {
            "styles": this.css.naviNode
        }).inject(this.naviContainerNode);

        this.leftNavi = new MWF.xApplication.Calendar.Navi(this, this.naviNode, {});
    },
    loadTopMenus_middle : function(){

    },
    loadTopMenus_right: function(){
        this.topMenuRight = new Element("div", {"styles": this.css.topMenuRight }).inject(this.topMenu);

        this.createTopMenu_right(this.lp.addEvent, "icon_newapply", "addCalendarEvent");
        //this.createTopMenu_right("新建日历", "icon_newapply", "addCalendar");

        //var refreshNode = this.createTopMenu_right(this.lp.refresh, "refresh", "refresh");
        //refreshNode.setStyle("float", "right");

        var configNode = this.createTopMenu_right(this.lp.setting, "icon_shezhi", "config");
        configNode.setStyle("float", "right");
    },
    createTopMenu_right : function(text, icon, action){
        var actionNode = new Element("div", {"styles": this.css.topMenuNode_right, "title" : text}).inject(this.topMenuRight);
        var actionIconNode = new Element("div", {"styles": this.css.topMenuIconNode}).inject(actionNode);
        var actionTextNode = new Element("div",{styles: this.css.topMenuTextNode, "text":text}).inject(actionNode);
        actionIconNode.setStyle("background", "url(../x_component_Calendar/$Main/default/icon/"+icon+".png) no-repeat center center");
        actionNode.store("icon",icon);
        actionNode.store("iconNode",actionIconNode);

        var _self = this;
        actionNode.addEvents({
            "mouseover": function(){
                this.node.setStyles(_self.css.topMenuNode_over);
                this.node.retrieve("iconNode").setStyle( "background","url(../x_component_Calendar/$Main/default/icon/"+this.node.retrieve("icon")+"_click.png) no-repeat center center" );
            }.bind( { node : actionNode } ),
            "mouseout": function(){
                this.node.setStyles(_self.css.topMenuNode_right);
                this.node.retrieve("iconNode").setStyle( "background","url(../x_component_Calendar/$Main/default/icon/"+this.node.retrieve("icon")+".png) no-repeat center center" );
            }.bind({ node:actionNode }),
            "click": function(){
                this.node.setStyles(_self.css.topMenuNode_down);
                this.node.retrieve("iconNode").setStyle( "background","url(../x_component_Calendar/$Main/default/icon/"+this.node.retrieve("icon")+"_click.png) no-repeat center center" );
                if (_self[action]) _self[action].apply(_self);
            }.bind({ node : actionNode })
        });
        return actionNode;
    },
    loadTopMenus: function(){
        //this.createTopMenu(this.lp.myCalendar, "icon_huiyi", "toMyCalendar");
        this.createTopMenu(this.lp.month, "icon_yue", "toMonth");
        this.createTopMenu(this.lp.week, "icon_zhou", "toWeek");
        this.createTopMenu(this.lp.day, "icon_ri", "toDay");
        this.createTopMenu(this.lp.list, "icon_liebiao", "toList");

        this.loadTopMenus_middle();
        this.loadTopMenus_right();
    },
    isViewAvailable : function( action ){
        return  !this.calendarConfig.disableViewList.contains( action );
    },
    createTopMenu: function(text, icon, action){
        if( this.calendarConfig.disableViewList.contains( action ) )return;

        if( this.calendarConfig[ action + "ViewName" ] ){
            text = this.calendarConfig[ action + "ViewName" ];
        }

        var actionNode = new Element("div", {"styles": this.css.topMenuNode}).inject(this.topMenu);
        var actionIconNode = new Element("div", {"styles": this.css.topMenuIconNode}).inject(actionNode);
        actionIconNode.setStyle("background", "url(../x_component_Calendar/$Main/default/icon/"+icon+".png) no-repeat center center");
        var actionTextNode = new Element("div", {"styles": this.css.topMenuTextNode, "text": text}).inject(actionNode);
        actionNode.store("icon",icon);
        actionNode.store("iconNode",actionIconNode);
        actionNode.store("action",action);

        var _self = this;
        actionNode.addEvents({
            "mouseover": function(){
                if( this.node != _self.currentTopMenuNode ){
                    this.node.setStyles(_self.css.topMenuNode_over);
                    this.node.retrieve("iconNode").setStyle( "background","url(../x_component_Calendar/$Main/default/icon/"+this.node.retrieve("icon")+"_click.png) no-repeat center center" );
                }
            }.bind( { node : actionNode } ),
            "mouseout": function(){
                if(this.node != _self.currentTopMenuNode){
                    this.node.setStyles(_self.css.topMenuNode);
                    this.node.retrieve("iconNode").setStyle( "background","url(../x_component_Calendar/$Main/default/icon/"+this.node.retrieve("icon")+".png) no-repeat center center" );
                }
            }.bind({ node:actionNode }),
            //"mousedown": function(){this.setStyles(_self.css.topMenuNode_down);},
            //"mouseup": function(){this.setStyles(_self.css.topMenuNode_over);},
            "click": function(){
                if( this.node != _self.currentTopMenuNode ){
                    this.node.setStyles( _self.css.topMenuNode_down );
                    this.node.retrieve("iconNode").setStyle( "background","url(../x_component_Calendar/$Main/default/icon/"+this.node.retrieve("icon")+"_click.png) no-repeat center center" );
                }
                if( _self.currentTopMenuNode && this.node != _self.currentTopMenuNode){
                    _self.currentTopMenuNode.setStyles( _self.css.topMenuNode );
                    _self.currentTopMenuNode.retrieve("iconNode").setStyle( "background","url(../x_component_Calendar/$Main/default/icon/"+_self.currentTopMenuNode.retrieve("icon")+".png) no-repeat center center" );
                }
                _self.currentTopMenuNode = this.node;
                if (_self[action]) _self[action].apply(_self);
            }.bind({ node : actionNode })
        });
        if( this.defaultAction ){
            if( this.defaultAction == action ){
                actionNode.click();
            }
        }else if( !this.loaded ){
            actionNode.click();
            this.loaded = true;
        }
        this.resizeNodes();
        return actionNode;
    },

    hideCurrentView: function(){
        if (this.currentView){
            this.currentView.hide();
            this.currentView = null;
        }
    },
    toList: function(){
        if(this.contentNode)this.contentNode.setStyle("background", "#EEE");
        if( this.currentView ){
            this.currentView.destroy();
            this.currentView = null;
        }
        this.listView = null;
        this.getListView(function(){
            this.listView.show();
            this.currentView = this.listView;
            if( this.options.eventId ){
                this.openEvent( this.options.eventId );
                this.options.eventId = "";
            }
        }.bind(this));
    },
    getListView: function(callback){
        if (!this.listView){
            MWF.xDesktop.requireApp("Calendar", "ListView", function(){
                var options;
                if( this.status && this.status.options ){
                    options = this.status.options
                }else if( this.currentDate  ){
                    //options = { date : this.currentDate };
                }
                this.listView = new MWF.xApplication.Calendar.ListView(this.contentNode, this, options);
                if( options && this.status)this.status.options = null;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    toMonth: function(){
        if(this.contentNode)this.contentNode.setStyle("background", "#EEE");
        if( this.currentView ){
            this.currentView.destroy();
            this.currentView = null;
        }
        this.monthView = null;
        this.getMonthView(function(){
            this.monthView.show();
            this.currentView = this.monthView;
            debugger;
            if( this.options.eventId ){
                this.openEvent( this.options.eventId );
                this.options.eventId = "";
            }
        }.bind(this));
    },
    getMonthView: function(callback){
        if (!this.monthView){
            MWF.xDesktop.requireApp("Calendar", "MonthView", function(){
                var options;
                if( this.status && this.status.options ){
                    options = this.status.options
                }else if( this.currentDate  ){
                    //options = { date : this.currentDate.format("%Y-%m-%d") };
                }
                this.monthView = new MWF.xApplication.Calendar.MonthView(this.contentNode, this, options);
                if( options && this.status)this.status.options = null;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    toWeek: function(){
        if(this.contentNode)this.contentNode.setStyle("background", "#EEE");
        if( this.currentView ){
            this.currentView.destroy();
            this.currentView = null;
        }
        this.weekView = null;
        this.getWeekView(function(){
            this.weekView.show();
            this.currentView = this.weekView;
            if( this.options.eventId ){
                this.openEvent( this.options.eventId );
                this.options.eventId = "";
            }
        }.bind(this));
    },
    getWeekView: function(callback){
        if (!this.weekView){
            MWF.xDesktop.requireApp("Calendar", "WeekView", function(){
                var options;
                if( this.status && this.status.options ){
                    options = this.status.options
                }else if( this.currentDate  ){
                    //options = { date : this.currentDate.format("%Y-%m-%d") };
                }
                this.weekView = new MWF.xApplication.Calendar.WeekView(this.contentNode, this, options);
                if( options && this.status)this.status.options = null;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    toDay: function(d){
        if(this.contentNode)this.contentNode.setStyle("background", "#EEE");
        if( this.currentView ){
            this.currentView.destroy();
            this.currentView = null;
        }
        this.dayView = null;
        this.getDayView(function(){
            this.dayView.show();
            this.currentView = this.dayView;
            if( this.options.eventId ){
                this.openEvent( this.options.eventId );
                this.options.eventId = "";
            }
        }.bind(this), d);
    },
    getDayView: function(callback, d){
        if (!this.dayView){
            MWF.xDesktop.requireApp("Calendar", "DayView", function(){
                var options;
                if( this.status && this.status.options ){
                    options = this.status.options
                }else if( d ){
                    options = {"date": d};
                }else if( this.currentDate ){
                    //options = { date : this.currentDate.format("%Y-%m-%d") };
                }
                this.dayView = new MWF.xApplication.Calendar.DayView(this.contentNode, this, options);
                if(this.status && this.status)this.status.options = null;
                if (callback) callback();
            }.bind(this));
        }else{
            this.dayView.toDay(d);
            if (callback) callback();
        }
    },
    addCalendarEvent: function(date, hour, minute, calendarId){
        MWF.UD.getPublicData("calendarConfig", function(json){
            var process = (json) ? json.process : null;
            if (process){
                this.loadCalendarProcess(process);
            }else{
                //new MWF.xApplication.Calendar.Creator(this, date, hour, room);
                var form = new MWF.xApplication.Calendar.EventForm(this,{}, {
                    date : date,
                    hour : hour,
                    minute : minute,
                    defaultCalendarId : calendarId
                }, {app:this});
                form.view = this;
                form.create();
                //this.hideMenu();
            }
        }.bind(this));
    },
    addCalendar: function(){
        var form = new MWF.xApplication.Calendar.CalendarForm(this,{}, {
        }, {app:this});
        form.view = this.leftNavi;
        form.create();
    },
    editCalendar: function( data ){
        var form = new MWF.xApplication.Calendar.CalendarForm(this, data, {
        }, {app:this});
        form.view = this.leftNavi;
        this.isEventEditable(data) ? form.edit() : form.open();
    },
    openCalendar: function( data ){
        var form = new MWF.xApplication.Calendar.CalendarForm(this,data, {
        }, {app:this});
        form.view = this.leftNavi;
        form.open();
    },
    loadCalendarProcess: function(id){
        this.getProcess(id, function(process){
            MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function(){
                var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(process, this, {
                    "onStarted": function(data, title, processName){
                        this.afterStartProcess(data, title, processName);
                    }.bind(this)
                });
                starter.load();
            }.bind(this));
        }.bind(this));
    },
    afterStartProcess: function(data, title, processName){
        var workInfors = [];
        var currentTask = [];

        data.each(function(work){
            if (work.currentTaskIndex !== -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
            workInfors.push(this.getStartWorkInforObj(work));
        }.bind(this));

        if (currentTask.length===1){
            var options = {"workId": currentTask[0], "appId": currentTask[0]};
            this.desktop.openApplication(null, "process.Work", options);

            this.createStartWorkResault(workInfors, title, processName, false);
        }else{
            this.createStartWorkResault(workInfors, title, processName, true);
        }
    },
    createStartWorkResault: function(workInfors, title, processName, isopen){
        var content = "";
        workInfors.each(function(infor){
            var users = [];
            infor.users.each(function(uname){
                users.push(MWF.name.cn(uname));
            });

            content += "<div><b>"+this.lp.nextActivity+"<font style=\"color: #ea621f\">"+infor.activity+"</font>, "+this.lp.nextUser+"<font style=\"color: #ea621f\">"+users.join(", ")+"</font></b>";
            if (infor.currentTask && isopen){
                content += "&nbsp;&nbsp;&nbsp;&nbsp;<span value=\""+infor.currentTask+"\">"+this.lp.deal+"</span></div>";
            }else{
                content += "</div>";
            }
        }.bind(this));

        var msg = {
            "subject": this.lp.processStarted,
            "content": "<div>"+this.lp.processStartedMessage+"“["+processName+"]"+title+"”</div>"+content
        };
        var tooltip = layout.desktop.message.addTooltip(msg);
        var item = layout.desktop.message.addMessage(msg);

        this.setStartWorkResaultAction(tooltip);
        this.setStartWorkResaultAction(item);
    },
    getStartWorkInforObj: function(work){
        var users = [];
        var currentTask = "";
        work.taskList.each(function(task, idx){
            users.push(task.person+"("+task.department + ")");
            if (work.currentTaskIndex===idx) currentTask = task.id;
        }.bind(this));
        return {"activity": work.fromActivityName, "users": users, "currentTask": currentTask};
    },
    setStartWorkResaultAction: function(item){
        var node = item.node.getElements("span");
        node.setStyles(this.app.css.dealStartedWorkAction);
        var _self = this;
        node.addEvent("click", function(e){
            var options = {"taskId": this.get("value"), "appId": this.get("value")};
            _self.desktop.openApplication(e, "process.Work", options);
        });
    },
    getProcess: function(id, callback){
        // MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
        //     var action = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
        //     action.getProcess(id, function(json){
                 if (callback) callback(id);
        //     }.bind(this));
        // }.bind(this));
    },

    config: function(){
        new MWF.xApplication.Calendar.Config(this, this.calendarConfig);
        //this.hideMenu();
    },
    recordStatus: function(){
        //alert( JSON.stringify(
        //    {
        //        action : this.currentTopMenuNode ? this.currentTopMenuNode.retrieve("action") : "toMyCalendar",
        //        options : this.currentView.recordStatus ? this.currentView.recordStatus() : null
        //    }
        //) )
        return {
            action : this.currentTopMenuNode ? this.currentTopMenuNode.retrieve("action") : "toMyCalendar",
            options : this.currentView.recordStatus ? this.currentView.recordStatus() : null
        };
    },
    reloadView : function(){
        if( this.currentView ){
            this.currentView.reload();
        }
    },
    reload: function( ){
        this.refresh()
    },
    loadSideBar : function(){
        //this.sideBar = new MWF.xApplication.Calendar.SideBar(this.node, this);
        //this.sideBar.show();
    },
    resizeNodes : function(){
        this.showLeftNavi = true;
        if( this.inContainer ){
            var size = this.container.getSize();
        }else{
            var size = this.node.getSize();
        }
        this.naviContainerNode.setStyle("height", ""+size.y+"px");
        if( this.showLeftNavi ){
            this.rightContentNode.setStyle("width",size.x - MWFCalendar.LeftNaviWidth );
            //this.topMenu.setStyle("width",size.x - leftNaviSize.x);
            //this.contentNode.setStyle("width",size.x - leftNaviSize.x);
        }
        if( this.currentView ){
            this.currentView.resetNodeSize();
        }
        if( this.leftNavi )this.leftNavi.resizeNode();
    },
    openEvent : function (id) {
        debugger;
        this.actions.getEvent( id, function (json) {
            var form = new MWF.xApplication.Calendar.EventForm(this, json.data, {
                isFull : true
            }, {app:this});
            form.view = this.currentView;
            form.open();
        }.bind(this))
    },
    isEventEditable: function (data) {
        if( MWF.AC.isAdministrator() )return true;
        if( (data.manageablePersonList || []).contains( layout.desktop.session.user.distinguishedName ) )return true;
        if( data.createPerson === layout.desktop.session.user.distinguishedName )return true;
        return false;
    }
});

MWF.xApplication.Calendar.Navi = new Class({
    Implements: [Options, Events],
    options : {

    },
    initialize: function(app, node, options){
        this.setOptions(options);
        this.app = app;
        this.lp = this.app.lp;
        this.node = $(node);
        this.css = this.app.css;
        this.load();
    },
    load : function(){
        this.naviContainer = new Element("div", {
            styles : {
                overflow : "hidden"
            }
        }).inject(this.node);
        this.naviNode = new Element("div").inject(this.naviContainer);

        this.myCalendarNaviItem = [];
        this.unitCalendarNaviItem = [];
        this.followCalendarNaviItem = [];

        this.app.listCalendar( function( data ){
            this.myCalendars = data.myCalendars;
            this.unitCalendars = data.unitCalendars;
            this.followCalendars = data.followCalendars;
            this.app.canlendarData = null;
            this.loadNode();
        }.bind(this));

        o2.require("MWF.widget.ScrollBar", function(){
            this.scrollBar = new MWF.widget.ScrollBar(this.naviContainer, {
                "indent": false,
                "style": "default",
                "where": "before",
                "distance": 60,
                "friction": 4,
                "axis": {"x": false, "y": true},
                "onScroll": function (y) {

                }.bind(this)
            });
        }.bind(this));
    },
    loadNode: function(){
        this.loadMyCalendar();
        this.loadUnitCalendar();
        this.loadFollowCalendar();
        this.loadMoreCalendarNode();
        //this.resizeNode();
    },
    reload : function(){
        this.node.empty();
        this.load();
    },
    loadMoreCalendarNode : function(){
        //this.newCalendarNode = new Element("div", {
        //    styles : this.css.newCalendarNode,
        //    text : "创建新日历",
        //    events : {
        //        mouseover : function( ev ){ ev.target.setStyles( this.css.newCalendarNode_over ); }.bind(this),
        //        mouseout : function( ev ){ ev.target.setStyles( this.css.newCalendarNode ); }.bind(this),
        //        click : function(){ this.app.addCalendar() }.bind(this)
        //    }
        //}).inject( this.node );
        this.seeMore = new Element("div.seeMore", {
            styles : this.css.seeMoreNode,
            "text" : this.lp.calendarMarket,
            "events" : {
                mouseover : function(ev){ ev.target.setStyles( this.css.seeMoreNode_over ) }.bind(this),
                mouseout : function(ev){ ev.target.setStyles( this.css.seeMoreNode ) }.bind(this),
                click : function(ev){
                    var form = new MWF.xApplication.Calendar.CalendarMarket(this.app,{}, {
                    }, {app:this.app});
                    form.view = this.leftNavi;
                    form.create();
                }.bind(this)
            }
        }).inject( this.node );
    },
    selectSingleCalendar : function( id ){
        this.myCalendarNaviItem.each( function( item ){
            if( item.isSelected && item.data.id !== id ){
                item.cancelSelect()
            }
            if( !item.isSelected && item.data.id == id ){
                item.select()
            }
        });
        this.unitCalendarNaviItem.each( function( item ){
            if( item.isSelected && item.data.id !== id ){
                item.cancelSelect()
            }
            if( !item.isSelected && item.data.id == id ){
                item.select()
            }
        });
        this.followCalendarNaviItem.each( function( item ){
            if( item.isSelected && item.data.id !== id ){
                item.cancelSelect()
            }
            if( !item.isSelected && item.data.id == id ){
                item.select()
            }
        });
        this.app.reloadView();
    },
    getSelectedCalendarId : function(){
        var ids = [];
        this.myCalendarNaviItem.each( function( navi ){
            if( navi.isSelected )ids.push( navi.data.id );
        });
        this.unitCalendarNaviItem.each( function( navi ){
            if( navi.isSelected )ids.push( navi.data.id );
        });
        this.followCalendarNaviItem.each( function( navi ){
            if( navi.isSelected )ids.push( navi.data.id );
        });
        return ids;
    },
    loadMyCalendar : function(){
        var listNode = this.createCategoryNode(this.lp.myCalendar);

        this.myCalendars.each( function( d ){
            this.myCalendarNaviItem.push( new MWF.xApplication.Calendar.NaviItem( this, listNode, d, {
                isSelected : true
            } ) );
        }.bind(this))
    },
    loadUnitCalendar : function(){
        var listNode = this.createCategoryNode(this.lp.unitCalendar);

        this.unitCalendars.each( function( d ){
            this.unitCalendarNaviItem.push( new MWF.xApplication.Calendar.NaviItem( this, listNode, d, {
                isSelected : true
            } ));
        }.bind(this))
    },
    loadFollowCalendar : function(){
        var listNode = this.createCategoryNode(this.lp.myFollowCalendar);

        this.followCalendars.each( function( d ){
            this.followCalendarNaviItem.push( new MWF.xApplication.Calendar.NaviItem( this, listNode, d, {
                isSelected : true
            } ));
        }.bind(this));
        //var seeMore = new Element("div", {
        //    styles : this.css.seeMoreNode,
        //    "text" : "查看日历广场",
        //    "events" : {
        //        mouseover : function(ev){ ev.target.setStyles( this.css.seeMoreNode_over ) }.bind(this),
        //        mouseout : function(ev){ ev.target.setStyles( this.css.seeMoreNode ) }.bind(this),
        //        click : function(ev){
        //            var form = new MWF.xApplication.Calendar.CalendarMarket(this.app,{}, {
        //            }, {app:this.app});
        //            form.view = this.leftNavi;
        //            form.create();
        //        }.bind(this)
        //    }
        //}).inject( this.naviNode );
    },
    createCategoryNode : function( text ){
        var _self = this;

        var categoryNaviNode = new Element("div.categoryNaviNode", {
            "styles": this.css.categoryNaviNode
        }).inject(this.naviNode);

        var expendNode = new Element("div.categoryExpendNode", {
            styles : this.css.categoryExpendNode
        }).inject(categoryNaviNode);

        categoryNaviNode.addEvent( "click" , function(ev){
            var target = this.categoryNaviNode;
            if( target.retrieve("isExpended") ){
                target.store("isExpended" , false);
                target.retrieve("expendNode").setStyles( _self.css.categoryCollapseNode );
                target.retrieve("listNode").setStyle("display","none")
            }else{
                target.store("isExpended" , true);
                target.retrieve("expendNode").setStyles( _self.css.categoryExpendNode );
                target.retrieve("listNode").setStyle("display","")
            }
        }.bind( { categoryNaviNode : categoryNaviNode } ));

        var textNode = new Element("div.categoryNaviTextNode",{
            "styles": this.css.categoryNaviTextNode,
            "text": text //this.defaultRevealData.id == "defaultList" ? this.data.name : this.defaultRevealData.showName
        }).inject( categoryNaviNode);

        var listNode = new Element("div.viewNaviListNode",{
            "styles" : this.css.viewNaviListNode
        }).inject(this.naviNode);

        categoryNaviNode.store("isExpended" , true);
        categoryNaviNode.store("expendNode" , expendNode);
        categoryNaviNode.store("textNode" , textNode);
        categoryNaviNode.store("listNode" , listNode);

        return listNode;
    },
    resizeNode : function(){
        if( this.app.inContainer ){
            var size = this.app.container.getSize();
        }else{
            var size = this.app.node.getSize();
        }
        //var titleSize = this.app.leftTitleNode ? this.app.leftTitleNode.getSize() : {x:0,y:0};
        this.node.setStyle("height",size.y - 80 );
        this.naviContainer.setStyle("height",size.y - 122 );
    }
});

MWF.xApplication.Calendar.NaviItem = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "index" : 0,
        "isSelected" : true
    },
    initialize: function ( navi, container, data, options) {
        this.setOptions(options);
        this.navi = navi;
        this.app = navi.app;
        this.data = data;
        this.container = $(container);
        this.css = this.app.css;
        this.load();
    },
    load: function(){
        this.isSelected = this.options.isSelected;

        var _self = this;
        this.node = new Element("div.naviItemNode", {
            "styles": this.css.naviItemNode
        }).inject(this.container);


        this.node.addEvents({
            "mouseover": function(){
                this.setStyles(_self.css.naviItemNode_over);
                _self.actionNode.fade("in");
            },
            "mouseout": function(){
                this.setStyles( _self.css.naviItemNode );
                _self.actionNode.fade("out");
            },
            "click": function (el) {
                if( _self.isSelected ){
                    _self.cancelSelect( true );
                }else{
                    _self.select( true );
                }
            }
        });

        this.actionNode = new Element("div.naviItemActionNode",{
            styles : this.css.naviItemActionNode,
            events : {
                click : function( ev ){
                    ev.stopPropagation();
                }.bind(this)
            }
        }).inject( this.node );

        this.colorNode = new Element("div", {
            styles : this.css.naviColorNode
        }).inject( this.node );
        this.colorNode.setStyle("border-color", this.data.color);

        this.textNode = new Element("div.naviItemTextNode", {
            styles : this.css.naviItemTextNode,
            "text" : this.data.name,
            "title" : this.data.name
        }).inject( this.node );

        if( this.isSelected ){
            this.select()
        }

        this.loadActionsMenu();
    },
    select : function( reload ){
        //this.node.setStyles( this.css.naviItemNode_selected );

        this.isSelected = true;

        this.colorNode.setStyle("background-color", this.data.color);
        this.colorNode.setStyles(this.css.naviColorNode_selected);

        if(reload)this.app.reloadView();
        //this.loadView();
    },
    cancelSelect : function(reload){
        this.isSelected = false;
        this.colorNode.setStyle("background-color", "transparent");
        this.colorNode.setStyles(this.css.naviColorNode);
        this.colorNode.setStyle("border-color", this.data.color);

        if(reload)this.app.reloadView();
    },
    loadActionsMenu : function(){
        this.menu = new MWF.xApplication.Calendar.CalendarMenu(this.actionNode, {} , this.app, {}, this.app.node);
        this.menu.calendarData = this.data;
        this.menu.load();
    }
    //loadView : function( searchKey ){
    //    this.app.openView( this, this.category.data, this.data, searchKey || "", this );
    //}
});

MWF.xDesktop.requireApp("Template", "MSelector", null, false);
MWF.xApplication.Calendar.CalendarMenu = new Class({
    Extends: MSelector,
    options : {
        "style": "arrow",
        "width": "150px",
        "height": "36px",
        "defaultOptionLp" : MWF.xApplication.Calendar.LP.font,
        "textField" : "name",
        "valueField" : "val",
        "event" : "mouseenter",
        "isSetSelectedValue" : false,
        "isChangeOptionStyle" : false,
        "emptyOptionEnable" : false,
        "containerIsTarget" : true,
        "tooltipsOptions" : {
            axis : "x",
            hasArrow : true
        }
    },
    _selectItem : function( itemNode, itemData, ev ){
        if( this[itemData.val] ){
            this[itemData.val](ev);
        }
    },
    showThis : function(){
        this.app.leftNavi.selectSingleCalendar( this.calendarData.id );
    },
    createEvent : function(){
        this.app.addCalendarEvent(null,null,null, this.calendarData.id);
    },
    openCalendar : function(){
        this.app.actions.getCalendar( this.calendarData.id, function( json ){
            this.app.openCalendar( json.data );
        }.bind(this))
    },
    editCalendar : function(){
        this.app.actions.getCalendar( this.calendarData.id, function( json ){
            this.app.editCalendar( json.data );
        }.bind(this))
    },
    deleteCalendar : function( e ){
        var lp = MWF.xApplication.Calendar.LP;
        var _self = this;
        _self.app.confirm("warn", e,  lp.deleteCalendarTitle, lp.deleteCalendarContent.replace("{name}", _self.calendarData.name), 300, 120, function(){
            _self.app.actions.deleteCalendar( _self.calendarData.id, function( json ){
                _self.app.notice( lp.deleteSuccess );
                _self.app.leftNavi.reload();
            }.bind(this));
            this.close();
        }, function(){
            this.close();
        }, null);

    },
    _loadData : function( callback ){
        var lp =  MWF.xApplication.Calendar.LP;
        var actionList = [{
            name: lp.onlyViewCurrent,
            val: 'showThis'
        }];
        if( this.calendarData.publishable || this.calendarData.manageable){
            actionList.push( {
                name: lp.addEvent,
                val: 'createEvent'
            });
        }
        if( this.calendarData.manageable ){
            actionList.push( {
                name: lp.editCalendar,
                val: 'editCalendar'
            }, {
                name: lp.deleteCalendar,
                val: 'deleteCalendar'
            });
        }else{
            actionList.push( {
                name: lp.viewCalendar,
                val: 'openCalendar'
            });
        }

        if(callback)callback( actionList );
    },
    _postCreateItem: function( itemNode, data ){
        itemNode.setStyles( {
            "cursor" : "pointer",
            "font-size" : "14px",
            "min-height" : "36px",
            "line-height" : "36px"
        } );
    }
});

MWF.xApplication.Calendar.CalendarMarket = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "meeting",
        "width": "1100",
        "height": "80%",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "closeAction": true,
        "title" : MWF.xApplication.Calendar.LP.calendarMarket
    },
    _createTableContent : function(){
        var lp = MWF.xApplication.Calendar.LP;
        var _self = this;

        this.formTableContainer.setStyles({
            "width" : "auto",
            "padding-top" : "20px",
            "padding-left" : "20px"
        });

        this.formTableArea.setStyle("overflow","hidden");



        this.app.actions.listPublicCalendar( function( json ){
            if( !json.data  || json.data.length == 0 ){
                this.noCalendarNode = new Element("div",{
                    "styles" : this.css.noCalendarNode,
                    "text" : lp.noPublicCalendar
                }).inject( this.formTableArea );
            }else{
                ( json.data || [] ).each( function(d){
                    var node = new Element("div", { styles : this.css.marketNode }).inject( this.formTableArea );
                    node.setStyle("border-left","5px solid "+ d.color);

                    var itemNode = new Element("div", { styles : this.css.marketItemNode }).inject( node );

                    var topNode = new Element("div", { styles : this.css.marketItemTopNode }).inject( itemNode );

                    var titleNode = new Element("div", {
                        styles : this.css.marketItemTitleNode,
                        text : d.name
                    }).inject( topNode );
                    titleNode.setStyle("color", d.color);

                    var middleNode = new Element("div", { styles : this.css.marketItemMiddleNode }).inject( itemNode );


                    new Element("div", {
                        styles : this.css.marketItemTopLable,
                        text : lp.create + "："
                    }).inject( middleNode );

                    new Element("div", {
                        styles : this.css.marketItemTopInfor,
                        text : d.createor.split("@")[0] + "，"
                    }).inject( middleNode );

                    new Element("div", {
                        styles : this.css.marketItemTopLable,
                        text : lp.time + "："
                    }).inject( middleNode );

                    new Element("div", {
                        styles : this.css.marketItemTopInfor,
                        text : d.createTime.split(" ")[0]
                    }).inject( middleNode );


                    var middleNode = new Element("div", { styles : this.css.marketItemMiddleNode }).inject( itemNode );
                    new Element("div", {
                        styles : this.css.marketItemTopLable,
                        text : lp.type + "："
                    }).inject( middleNode );

                    new Element("div", {
                        styles : this.css.marketItemTopInfor,
                        text : d.type == "PERSON" ? lp.personal : lp.unit2
                    }).inject( middleNode );

                    if( d.type == "UNIT" ){
                        new Element("div", {
                            styles : this.css.marketItemTopLable,
                            text : lp.belongTo + "："
                        }).inject( middleNode );

                        new Element("div", {
                            styles : this.css.marketItemTopInfor,
                            text : d.target.split("@")[0],
                            title : d.target.split("@")[0]
                        }).inject( middleNode );
                    }

                    var middleNode = new Element("div", { styles : this.css.marketItemMiddleNode }).inject( itemNode );
                    new Element("div", {
                        styles : this.css.marketItemTopLable,
                        text : lp.description + "："
                    }).inject( middleNode );

                    new Element("div", {
                        styles : this.css.marketItemDescriptiontInfor,
                        text : d.description || "",
                        title : d.description || ""
                    }).inject( middleNode );

                    var followedAction, followAction;
                    var followedAction = new Element("div",{
                        styles : this.css.marketItemFollowedAction,
                        text : lp.followed,
                        title : lp.clickToCancelFollow,
                        events : {
                            click : function(){
                                _self.app.actions.followCalendarCancel(d.id, function(){
                                    _self.app.notice( lp.cancelFollowSuccess );
                                    _self.needReload = true;
                                    this.followedAction.setStyle("display","none");
                                    this.followAction.setStyle("display","");
                                }.bind({ followedAction : followedAction, followAction: followAction }))
                            }.bind(this)
                        }
                    }).inject(node);
                    if( !d.followed )followedAction.setStyle("display","none");

                    var followAction = new Element("div",{
                        styles : this.css.marketItemFollowAction,
                        text : lp.follow ,
                        events : {
                            click: function () {
                                _self.app.actions.followCalendar(d.id, function () {
                                    _self.app.notice( lp.followSuccess );
                                    _self.needReload = true;
                                    this.followedAction.setStyle("display","");
                                    this.followAction.setStyle("display","none");
                                }.bind({ followedAction : followedAction, followAction: followAction }))
                            }.bind(this)
                        }
                    }).inject(node);
                    if( d.followed )followAction.setStyle("display","none");

                    //var typeNode = new Element("div", {
                    //    styles : this.css.marketTypleLabel,
                    //    text : d.type == "PERSON" ? "个人日历" : "组织日历"
                    //}).inject( topNode );
                    //typeNode.setStyle( "background-color" , MWFCalendar.ColorOptions.getLightColor(d.color ) );
                }.bind(this))
            }

        }.bind(this))

    },
    _close : function(){
        if( this.needReload )this.app.reload();
    }
});


MWF.xApplication.Calendar.Config = new Class({
    Implements: [Events],
    initialize: function(app){
        this.app = app;
        this.css = this.app.css;
        this.lp = this.app.lp;
        this.configData = this.app.calendarConfig || {};
        this.process = null;

        MWF.UD.getPublicData("calendarConfig", function(json){
            var jsonData = json || {};
            //if (jsonData.process){
            //    this.configData.process = jsonData.process;
            //}else{
            //    this.configData.process = null;
            //}
            if( jsonData.weekBegin ){
                this.configData.weekBegin = jsonData.weekBegin;
            }
            //if( jsonData.mobileCreateEnable ){
            //    this.configData.mobileCreateEnable = jsonData.mobileCreateEnable;
            //}

            for( var key in jsonData ){
                if( key != "process" && key != "weekBegin"  && key !="mobileCreateEnable"){
                    this.configData[ key ] = jsonData[key];
                }
            }
            this.load();
        }.bind(this));
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.configNode}).inject(this.app.node);
        this.contentNode = new Element("div", {"styles": this.css.configContentNode}).inject(this.node);

        var statusStyle = "overflow: hidden;margin-top:6px;margin-left:10px;";
        var statusIconStyle = "float: left; width: 16px; height: 16px; border-radius: 100px; ";
        var statusIconStyle2 = "float: left; width: 14px; height: 14px; border-radius: 100px; ";
        var statusTextStyle = "margin-left:35px; line-height:16px; height:16px;font-size:14px;color:#666;";

        var viewStyle = "font-size:14px;color:#666;heigh:16px;margin-top:6px;margin-left:10px;";
        var inputTextStyle = "float:right; width:120px; border:1px solid #ccc";

        var d = this.configData;
        var configLp = this.lp.config;

        var html =

            //"<div class='configTitle'>"+this.lp.config.calendarStatus+"</div>" +
            //
            //"<div style='"+ statusStyle +"'>"+
            //"   <div style='"+ statusIconStyle + "background-color:#4990E2'></div>" +
            //"   <div style='"+  statusTextStyle +"'>"+this.lp.config.wait+"</div></div>" +
            //"</div>"+
            //
            //"<div style='"+ statusStyle +"'>"+
            //"   <div style='"+ statusIconStyle + "background-color:#66CC7F'></div>" +
            //"   <div style='"+  statusTextStyle +"'>"+this.lp.config.progress+"</div></div>" +
            //"</div>"+
            //
            //"<div style='"+ statusStyle +"'>"+
            //"   <div style='"+ statusIconStyle + "background-color:#F6A623'></div>" +
            //"   <div style='"+  statusTextStyle +"'>"+this.lp.config.invite+"</div></div>" +
            //"</div>"+
            //
            //"<div style='"+ statusStyle +"'>"+
            //"   <div style='"+ statusIconStyle + "background-color:#ccc'></div>" +
            //"   <div style='"+  statusTextStyle +"'>"+this.lp.config.completed+"</div></div>" +
            //"</div>"+
            //
            //"<div style='"+ statusStyle +"'>"+
            //"   <div style='"+ statusIconStyle2 + "border:2px solid #FF7F7F;'></div>" +
            //"   <div style='"+  statusTextStyle +"'>"+this.lp.config.conflict+"</div></div>" +
            //"</div>"+
            //
            //"<div class='line'></div>" +

            "<div class='configTitle'>"+this.lp.config["default"]+"</div>" +
            "<div>";


        if( !d.disableViewList.contains( "toMonth" )  ){
            html +=
                "   <div style='"+ viewStyle +"'>" +
                "<input type='radio' name='configSelectDefaultView' "+((d.defaultView=="toMonth") ? "checked" : "")+" value='toMonth'>"+ ( d.toMonthViewName || this.lp.month )+
                "   </div>";
        }

        if( !d.disableViewList.contains( "toWeek" )  ){
            html +=
                "   <div style='"+ viewStyle +"'>" +
                "<input type='radio' name='configSelectDefaultView' "+((d.defaultView=="toWeek") ? "checked" : "")+" value='toWeek'>"+( d.toWeekViewName || this.lp.week )+
                "   </div>";
        }

        if( !d.disableViewList.contains( "toDay" )  ){
            html +=
                "   <div style='"+ viewStyle +"'>" +
                "<input type='radio' name='configSelectDefaultView' "+((d.defaultView=="toDay") ? "checked" : "")+" value='toDay'>"+(d.toDayViewName || this.lp.day)+
                "   </div>";
        }

        if( !d.disableViewList.contains( "toList" )  ){
            html +=
                "   <div style='"+ viewStyle +"'>" +
                "<input type='radio' name='configSelectDefaultView' "+((d.defaultView=="toList") ? "checked" : "")+" value='toList'>"+(d.toListViewName || this.lp.list)+
                "   </div>";
        }



        html +="</div>";

        if( MWF.AC.isAdministrator() ){
            d.disableViewList = d.disableViewList || [];
            html += "<div class='line'></div>"+

                "<div class='configTitle'>"+this.lp.config.viewSetting +"</div>" +
                "<div>"+
                "   <div style='"+ viewStyle +"'>" +
                "<input type='checkbox' name='configAvailableView' "+( !d.disableViewList.contains( "toMonth" ) ? "checked" : "")+" value='toMonth'>"+this.lp.month+
                "<input type='text' name='toMonthViewName' value='" + (d.toMonthViewName || "") + "' style='"+ inputTextStyle +"' placeholder='"+ this.lp.config.viewCustomName +"' >"+
                "   </div>" +
                "   <div style='"+ viewStyle +"'>" +
                "<input type='checkbox' name='configAvailableView' "+( !d.disableViewList.contains( "toWeek" ) ? "checked" : "")+" value='toWeek'>"+this.lp.week+
                "<input type='text' name='toWeekViewName' value='" + (d.toWeekViewName || "") + "' style='"+ inputTextStyle +"' placeholder='"+ this.lp.config.viewCustomName +"' >"+
                "   </div>" +
                "   <div style='"+ viewStyle +"'>" +
                "<input type='checkbox' name='configAvailableView' "+( !d.disableViewList.contains( "toDay" ) ? "checked" : "")+" value='toDay'>"+this.lp.day+
                "<input type='text' name='toDayViewName' value='" + (d.toDayViewName || "") + "' style='"+ inputTextStyle +"' placeholder='"+ this.lp.config.viewCustomName +"' >"+
                "   </div>" +
                "   <div style='"+ viewStyle +"'>" +
                "<input type='checkbox' name='configAvailableView' "+( !d.disableViewList.contains( "toList" ) ? "checked" : "")+" value='toList'>"+this.lp.list+
                "<input type='text' name='toListViewName' value='" + (d.toListViewName || "") + "' style='"+ inputTextStyle +"' placeholder='"+ this.lp.config.viewCustomName +"' >"+
                "   </div>" +

                "</div>" +

                "<div class='line'></div>"+

                "<div class='configTitle'>"+this.lp.config.weekBegin +"</div>" +
                "<div><select name='configSelectWeekBeign'>"+
                "<option value='0' "+((d.weekBegin=="0") ? "selected" : "")+">"+this.lp.weeks.Sun+"</option>"+
                "<option value='1' "+((d.weekBegin=="1") ? "selected" : "")+">"+this.lp.weeks.Mon+"</option>"+
                    //"<option value='2'"+((d.weekBegin=="2") ? "selected" : "")+">"+this.lp.weeks.Tues+"</option>"+
                    //"<option value='3'"+((d.weekBegin=="3") ? "selected" : "")+">"+this.lp.weeks.Wed+"</option>"+
                    //"<option value='4'"+((d.weekBegin=="4") ? "selected" : "")+">"+this.lp.weeks.Thur+"</option>"+
                    //"<option value='5'"+((d.weekBegin=="5") ? "selected" : "")+">"+this.lp.weeks.Fri+"</option>"+
                    //"<option value='6'"+((d.weekBegin=="6") ? "selected" : "")+">"+this.lp.weeks.Sat+"</option>"+
                "</select></div>"; //+

                //"<div class='line'></div>"+
                //"<div class='configTitle'>"+this.lp.config.applyProcess+"</div>" +
                //"<div item='processArea'></div>" +
                //
                //
                //"<div class='configTitle'>"+this.lp.config.mobileCreateEnable +"</div>" +
                //"<div item='mobileCreateEnable'></div>";

        }

        this.contentNode.set("html", html);
        this.contentNode.getElements("div.line").setStyles(this.css.configContentLine);
        this.contentNode.getElements("div.configTitle").setStyles(this.css.configTitleDiv);

        if( MWF.AC.isAdministrator() ){
            //this.processNode = this.contentNode.getElement("[item='processArea']");
            //this.processNode.setStyles(this.css.configProcessNode);
            //this.createApplicationSelect();
            //
            //this.mobileCreateEnableNode = this.contentNode.getElement("[item='mobileCreateEnable']");
            //this.mobileCreateEnable = new MDomItem(this.mobileCreateEnableNode, {
            //    name : "mobileCreateEnable", type : "select", selectValue : ["true","false"], selectText : this.lp.config.mobileCreateEnableOptions,
            //    value : d.mobileCreateEnable || "true"
            //} , null, this.app );
            //this.mobileCreateEnable.load();

        }

        this.actionNode = new Element("div", {"styles": this.css.configActionNode}).inject(this.node);
        this.cancelNode = new Element("div", {"styles": this.css.configActionCancelNode, "text": this.app.lp.cancel}).inject(this.actionNode);
        this.saveNode = new Element("div", {"styles": this.css.configActionSaveNode, "text": this.app.lp.save}).inject(this.actionNode);

        this.cancelNode.addEvent("click", this.hide.bind(this));
        this.saveNode.addEvent("click", this.save.bind(this));

        this.node.addEvent("mousedown", function(e){e.stopPropagation();}.bind(this));

        this.setSize();

        this.show();
    },
    setSize : function(){
        var sizeY = this.node.getSize().y;
        if( this.app.inContainer ){
            var y = this.app.container.getSize().y;
        }else{
            var y = this.app.content.getSize().y;
        }
        if( sizeY > y-50 ){
            this.node.setStyle("height", y-50 );
        }

    },
    createApplicationSelect: function(){
        if (this.configData.process){
            MWF.require("MWF.widget.O2Identity", function(){
                var p = new MWF.widget.O2Process(this.configData.process, this.processNode);
            }.bind(this));
        }
        this.processNode.addEvent("click", function(){
            MWF.xDesktop.requireApp("Selector", "package", function(){

                var options = {
                    "type": "Process",
                    "values": [this.process || this.configData.process],
                    "count": 1,
                    "onComplete": function (items) {
                        this.processNode.empty();
                        this.process = null;
                        this.configData.process = null;
                        if (items.length){
                            MWF.require("MWF.widget.O2Identity", function(){
                                var p = new MWF.widget.O2Process(items[0].data, this.processNode);
                                this.process = {
                                    "name": items[0].data.name,
                                    "id": items[0].data.id,
                                    "application": items[0].data.application,
                                    "applicationName": items[0].data.applicationName,
                                    "alias": items[0].data.alias
                                };
                                this.configData.process = this.process;
                            }.bind(this));
                        }

                    }.bind(this)
                };
                var selector = new MWF.O2Selector(this.app.content, options);

            }.bind(this));
        }.bind(this));

    },


    save: function(){
        //var hideMenu = "auto";
        var defaultView = "toMyCalendar";
        var process = null;
        //var node = this.contentNode.getFirst("div");
        //var hideMenuNode = node.getElement("input");
        //if (hideMenuNode) if (!hideMenuNode.checked) hideMenu = "static";

        //node = node.getNext();
        var viewNodes = this.contentNode.getElements("input[name='configSelectDefaultView']");
        for (var i=0; i<viewNodes.length; i++){
            if (viewNodes[i].checked){
                defaultView = viewNodes[i].get("value");
                break;
            }
        }

        MWF.UD.putData("calendarConfig", {
            //"hideMenu": hideMenu,
            "defaultView": defaultView
        }, null, false);

        if( MWF.AC.isAdministrator() ){
            //if (this.processSelect){
                //process = this.processSelect.options[this.processSelect.selectedIndex].get("value");
                //MWF.UD.putPublicData("calendarConfig", {"process": process});
            //}

            var disableViewList = [];
            var viewAvailableNodes = this.contentNode.getElements("input[name='configAvailableView']");
            for (var i=0; i<viewAvailableNodes.length; i++){
                if ( !viewAvailableNodes[i].checked){
                    disableViewList.push( viewAvailableNodes[i].get("value") ) ;
                }
            }

            var weekBeginSelect = this.contentNode.getElement("select[name='configSelectWeekBeign']");
            var weekBeginValue = "0";
            if( weekBeginSelect ){
                weekBeginValue = weekBeginSelect.options[weekBeginSelect.selectedIndex].get("value");
            }


            MWF.UD.putPublicData("calendarConfig", {
                //"process": this.process || this.configData.process,
                "weekBegin" : weekBeginValue,
                //"mobileCreateEnable" : this.mobileCreateEnable.getValue(),
                "disableViewList" : disableViewList,
                "toMonthViewName" : this.contentNode.getElement("input[name='toMonthViewName']").get("value"),
                "toWeekViewName" : this.contentNode.getElement("input[name='toWeekViewName']").get("value"),
                "toDayViewName" : this.contentNode.getElement("input[name='toDayViewName']").get("value"),
                "toListViewName" : this.contentNode.getElement("input[name='toListViewName']").get("value")
            }, null, false);
        }

        this.app.notice( this.app.lp.config_saveSuccess, "success" );

        this.hide();
    },
    show: function(){
        this.node.setStyles(this.css.configNode);
        var fx = new Fx.Morph(this.node, {
            "duration": "500",
            "transition": Fx.Transitions.Expo.easeOut
        });
        fx.start({
            "opacity": 1
        }).chain(function(){
            this.hideFun = this.hide.bind(this);
            this.app.node.addEvent("mousedown", this.hideFun);
        }.bind(this));
    },
    hide: function(){
        this.node.destroy();
        this.app.node.removeEvent("mousedown", this.hideFun);
        MWF.release(this);
    }

});