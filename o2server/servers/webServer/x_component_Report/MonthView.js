MWF.xApplication.Report.MonthView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date" : ""
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "/x_component_Report/$MonthView/";
        this.cssPath = "/x_component_Report/$MonthView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.load();
    },
    load: function(){
        this.node = new Element("div.node", {"styles": this.css.node}).inject(this.container);
        //this.loadSideBar();
        this.resetNodeSize();
        this.app.addEvent("resize", this.resetNodeSize.bind(this));
        this.loadCalendar();
    },

    resetNodeSize: function(){
        var size = this.container.getSize();
        var y = size.y-60;
        if( this.app.inContainer ){
            this.node.setStyle("height", "100%");
        }else{
            this.node.setStyle("height", ""+y+"px");
        }
        this.node.setStyle("margin-top", "60px");


        var sideBarSize = this.app.sideBar ?  this.app.sideBar.getSize() : { x : 0, y:0 };
        this.node.setStyle("width", ""+(size.x - sideBarSize.x)+"px");
        this.node.setStyle("margin-right", ""+sideBarSize.x+"px");


    },

    loadCalendar: function(){
        var date = "";
        if( this.options.date ){
            date = Date.parse( this.options.date )
        }
        this.calendar = new MWF.xApplication.Report.MonthView.Calendar(this, date  );

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
        if (this.calendar) this.calendar.reLoadCalendar();
    },
    recordStatus : function(){
        var date = "";
        if (this.calendar) date = this.calendar.date;
        return {
            date : date.toString()
        };
    },
    destroy: function(){
        if (this.calendar){
            this.calendar.destroy();
        }
        this.node.destroy();
        //MWF.release( this );
    }

});

MWF.xApplication.Report.MonthView.Calendar = new Class({
    Implements: [Events],
    initialize: function(view, date){
        this.view = view;
        this.css = this.view.css;
        this.container = this.view.node;
        this.app = this.view.app;
        this.date = date || new Date();
        this.today = new Date();
        this.days = {};
        this.load();
    },
    load: function(){
        this.titleNode = new Element("div", {"styles": this.css.calendarTitleNode}).inject(this.container);


        this.scrollNode = new Element("div.scrollNode", {
            "styles": this.app.inContainer ? this.css.scrollNode_inContainer : this.css.scrollNode
        }).inject(this.container);
        this.contentWarpNode = new Element("div", {
            "styles": this.css.contentWarpNode
        }).inject(this.scrollNode);

        this.contentContainerNode = new Element("div",{
            "styles" : this.css.contentContainerNode
        }).inject(this.contentWarpNode);
        this.bodyNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.contentContainerNode);

        //this.bodyNode = new Element("div", {"styles": this.css.calendarBodyNode}).inject(this.container);

        this.setTitleNode();
        this.setBodyNode();

        this.resetBodySize();
        this.app.addEvent("resize", this.resetBodySize.bind(this));

    },
    resetBodySize: function(){
        var size = this.container.getSize();
        var sizeY = this.app.inContainer ? 1060 : size.y;

        var titleSize = this.titleNode.getSize();
        var y = sizeY-titleSize.y;
        //this.bodyNode.setStyle("height", ""+y+"px");

        //var size = this.container.getSize();

        if( this.app.inContainer ){
            this.contentWarpNode.setStyle("height", ""+y+"px");
        }else{
            this.scrollNode.setStyle("height", ""+y+"px");
        }
        //this.scrollNode.setStyle("margin-top", "60px");

        if (this.contentWarpNode){
            this.contentWarpNode.setStyles({
                "width": (size.x - 40) +"px"
            });
        }

        //var tdy = (y-30)/6;
        //tdy = tdy-34;
        //var tds = this.calendarTable.getElements("td");
        //tds.each(function(td){
        //    var yy = tdy;
        //    var node = td.getLast("div");
        //    if (node.childNodes.length>=4){
        //        if (yy<92) yy = 69;
        //    }
        //    node.setStyle("height", ""+yy+"px");
        //}.bind(this));


    },
    setTitleNode: function(){
        this.prevMonthNode =  new Element("div", {"styles": this.css.calendarPrevMonthNode}).inject(this.titleNode);

        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode = new Element("div", {"styles": this.css.calendarTitleTextNode, "text": text}).inject(this.titleNode);

        this.nextMonthNode =  new Element("div", {"styles": this.css.calendarNextMonthNode}).inject(this.titleNode);

        this.prevMonthNode.addEvents({
            "mouseover": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode_over);}.bind(this),
            "mouseout": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode);}.bind(this),
            "mousedown": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode_down);}.bind(this),
            "mouseup": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode_over);}.bind(this),
            "click": function(){this.changeMonthPrev();}.bind(this)
        });
        this.nextMonthNode.addEvents({
            "mouseover": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode_over);}.bind(this),
            "mouseout": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode);}.bind(this),
            "mousedown": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode_down);}.bind(this),
            "mouseup": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode_over);}.bind(this),
            "click": function(){this.changeMonthNext();}.bind(this)
        });
        this.titleTextNode.addEvents({
            "mouseover": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_over);}.bind(this),
            "mouseout": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode);}.bind(this),
            "mousedown": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_down);}.bind(this),
            "mouseup": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_over);}.bind(this),
            "click": function(){this.changeMonthSelect();}.bind(this)
        });

        this.filterNode = new Element("div", {
            styles : this.css.filterNode
        }).inject( this.titleNode );
        //this.loadFilter();


        this.loadStatusArea()
    },
    loadStatusArea : function(){
        var lp = this.app.lp.config;
        var area = new Element("div", {
            "styles" : this.css.statusArea
        }).inject( this.titleNode );

        var html =
            "<div class = 'statusStyle'>"+
            "   <div class='statusIconStyle' style='background-color:"+ lp.waitColor +"'></div>" +
            "   <div class = 'statusTextStyle'>"+lp.wait+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div class='statusIconStyle' style='background-color:"+ lp.auditColor +"'></div>" +
            "   <div class = 'statusTextStyle'>"+lp.audit+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div class='statusIconStyle' style='background-color:"+ lp.progressColor +"'></div>" +
            "   <div class = 'statusTextStyle'>"+lp.progress+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div  class='statusIconStyle' style='background-color:"+ lp.completedColor +"'></div>" +
            "   <div class = 'statusTextStyle'>"+lp.completed+"</div></div>" +
            "</div>";

        area.set("html", html);
        area.getElements("div.statusStyle").setStyles( this.css.statusStyle );
        area.getElements("div.statusIconStyle").setStyles( this.css.statusIconStyle );
        area.getElements("div.statusIconStyle2").setStyles( this.css.statusIconStyle2 );
        area.getElements("div.statusTextStyle").setStyles( this.css.statusTextStyle );
    },
    loadFilter: function(){
        this.filterData = {};
        if( this.filter ){
            this.filter.destroy();
        }
        this.filter = new MWF.xApplication.Report.ReportFileter( this.filterNode, this.app, {
            items :  ["reportType","title","targetList","activityList","currentPersonList","reportStatus","reportObjType"],
            defaultResult : {},
            onSearch : function( condition ){
                this.filterData = condition;
                this.reLoadCalendar();
            }.bind(this)
        });
    },
    changeMonthPrev: function(){
        this.date.decrement("month", 1);
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reLoadCalendar();
    },
    changeMonthNext: function(){
        this.date.increment("month", 1);
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reLoadCalendar();
    },
    changeMonthSelect: function(){
        if (!this.monthSelector) this.createMonthSelector();
        this.monthSelector.show();
    },
    createMonthSelector: function(){
        this.monthSelector = new MWF.xApplication.Report.MonthView.Calendar.MonthSelector(this.date, this);
    },
    changeMonthTo: function(d){
        this.date = d;
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reLoadCalendar();
    },

    setBodyNode: function(){
        var html = "<tr><th>"+this.app.lp.weeks.Mon+"</th><th>"+this.app.lp.weeks.Tues+"</th><th>"+this.app.lp.weeks.Wed+"</th>" +
            "<th>"+this.app.lp.weeks.Thur+"</th><th>"+this.app.lp.weeks.Fri+"</th><th>"+this.app.lp.weeks.Sat+"</th><th>"+this.app.lp.weeks.Sun+"</th></tr>";
        html += "<tr><td valign='top'></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        this.calendarTable = new Element("table", {
            "styles": this.css.calendarTable,
            "height": "100%",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0",
            "html": html
        }).inject(this.bodyNode);

        this.calendarTableTitleTr = this.calendarTable.getElement("tr");
        this.calendarTableTitleTr.setStyles(this.css.calendarTableTitleTr);
        var ths = this.calendarTableTitleTr.getElements("th");
        ths.setStyles(this.css.calendarTableTh);

        //var tds = this.calendarTable.getElements("td");
        //tds.setStyles(this.css.calendarTableCell);

        this.loadCalendar();
    },
    reLoadCalendar: function(){
        Object.each(this.days, function(day){
            day.destroy();
        }.bind(this));

        this.loadCalendar();
    },

    loadCalendar: function(){
        var date = this.date.clone();
        date.set("date", 1);
        var week = date.getDay();
        var decrementDay = ((week-1)<0) ? 6 : week-1;
        date.decrement("day", decrementDay);

        this.loadData( date, function(){
            var tds = this.calendarTable.getElements("td");
            tds.each(function(td){
                this.loadDay(td, date, this.data[date.format("%Y-%m-%d")]);
                date.increment();
            }.bind(this));
        }.bind(this))
    },
    loadDay: function(td, date, data){
        var type = "thisMonth";
        var m = date.get("month");
        var y = date.get("year");
        var d = date.get("date");
        var mm = this.date.get("month");
        var yy = this.date.get("year");
        var mmm = this.today.get("month");
        var yyy = this.today.get("year");
        var ddd = this.today.get("date");

        if ((m==mmm) && (y==yyy) && (d==ddd)){
            type = "today";
        }else if ((m==mm) && (y==yy)){
            type = "thisMonth";
        }else{
            type = "otherMonth";
        }

        var key = date.format(this.app.lp.dateFormat);
        this.days[key] = new MWF.xApplication.Report.MonthView.Calendar.Day(td, date, this, type, data);
    },
    loadData : function(date, callback){
        var da = date.clone();
        this.data = {};
        var flag = 0;
        for( var i=0; i<3; i++ ){
            var m = this.app.common.addZero( (da.get("month")+1).toString(), 2);
            var y = da.get("year");
            this.app.restActions.listDayByYearMonth(y, m, function(json){
                flag++;
                json.data.each( function(d){
                    this.data[d.date] = d.reports;
                }.bind(this));
                if(callback && flag==3)callback();
            }.bind(this), null, false );
            da.increment("month", 1);
        }

    },
    reload : function(){
        this.view.reload();
    },
    destroy: function(){
        Object.each(this.days, function(day){
            day.destroy();
        }.bind(this));
        this.container.empty();
    }

});

MWF.xApplication.Report.MonthView.Calendar.Day = new Class({
    Implements: [Events],
    initialize: function(td, date, calendar, type, data){
        this.container = td;
        this.calendar = calendar;
        this.view = this.calendar.view;
        this.css = this.calendar.css;
        this.app = this.calendar.app;
        this.date = date.clone();
        this.data = data || [];
        this.key = this.date.format(this.app.lp.dateFormat);
        this.type = type; //today, otherMonth, thisMonth
        this.reports = [];
        this.load();
    },
    load: function(){
        this.color = "#666";
        if(  this.type == "thisMonth" ){
        }else if( this.type == "otherMonth" ){
            this.color = "#ccc";
        }
        this.day = this.date.getDate();
        this.month = this.date.getMonth();
        this.year = this.date.getYear();

        this.node = new Element("div", {
            "styles" : this.css["calendarTableCell_"+this.type]
        }).inject( this.container );

        this.titleNode = new Element("div", {"styles": this.css["dayTitle_"+this.type]}).inject(this.node);
        this.titleDayNode = new Element("div", {"styles": this.css["dayTitleDay_"+this.type], "text": this.day}).inject(this.titleNode);

        this.contentNode = new Element("div", {"styles": this.css.dayContentNode}).inject(this.node);

        this.loadReports();

    },
    loadReports: function(){
        var y = this.date.getFullYear();
        var m = this.date.getMonth()+1;
        var d = this.date.getDate();
        var reportCount = 0;
        this.firstStatus = "";
        this.lastStatus = "";
        //var filterData = this.calendar.filterData;
        //filterData.year = y;
        //filterData.month = m;
        //filterData.date = this.date.clone().format("%Y-%m-%d");


        var lp = this.app.lp.config;

        var length = this.data.length;
        this.data.each(function(report, i){
            reportCount++;
            if (reportCount==3){
                //this.contentNode.setStyle("height", "100px");
            }
            if( reportCount == 1 ){
                if( report.reportStatus == "审核中" &&  this.app.userName == report.currentPersonName ){
                    this.firstStatus = "需要我审核"
                }else{
                    this.firstStatus = report.reportStatus;
                }
            }
            if( reportCount == length ){
                if( report.reportStatus == "审核中" && this.app.userName == report.currentPersonName ){
                    this.lastStatus = "需要我审核"
                }else{
                    this.lastStatus = report.reportStatus;
                }
            }
            //if (reportCount<4)
            this.reports.push(new MWF.xApplication.Report.MonthView.Calendar.Day.Report(this, report, reportCount));
        }.bind(this));

        if (reportCount==0){
            var node = new Element("div", {
                "styles": {
                    "line-height": "40px",
                    "font-size": "14px",
                    "text-align" : "center",
                    "color" : this.color,
                    "padding": "0px 10px"
                }
            }).inject(this.contentNode);
            node.set("text", this.app.lp.noReport);
        }else{
            this.titleNode.setStyle("cursor","pointer");
            this.titleInforNode = new Element("div", {"styles": this.css["dayTitleInfor_"+this.type]}).inject(this.titleNode);
            this.titleInforNode.addEvent("click", function(e){
                this.app.toDay(this.date);
                e.stopPropagation();
            }.bind(this));
            this.titleInforNode.set("text", ""+reportCount+this.app.lp.countReports+"");
            if (reportCount>3){
                this.node.addEvents( {
                    "mouseenter" : function(){
                        this.expend();
                    }.bind(this),
                    "mouseleave" : function(){
                        this.collapseReady = true;
                        this.collapse();
                    }.bind(this)
                } )
            }else{
                this.titleInforNode.setStyle("color", this.type == "otherMonth" ? "#ccc" : "#999");
            }
        }

        if(this.firstStatus){
            switch (this.firstStatus){
                case "汇报者填写":
                    this.titleNode.setStyles({ "border-left": "6px solid "+ lp.waitColor });
                    break;
                case "审核中":
                    this.titleNode.setStyles({ "border-left": "6px solid "+ lp.progressColor });
                    break;
                case "需要我审核":
                    this.titleNode.setStyles({ "border-left": "6px solid "+ lp.auditColor });
                    break;
                case "已完成":
                    this.titleNode.setStyles({ "border-left": "6px solid "+ lp.completedColor });
                    break;
            }
        }

        if( this.lastStatus ){
            var heigth=0;
            if( reportCount >= 3 ){
                heigth = 10;
            }else{
                heigth = 100 - reportCount*30;
            }
            var bottomEmptyNode = new Element("div", {
                styles : {
                    "height" : ""+heigth+"px"
                }
            }).inject( this.node );
            switch (this.lastStatus){
                case "汇报者填写":
                    bottomEmptyNode.setStyles({ "border-left": "6px solid " + lp.waitColor });
                    break;
                case "审核中":
                    bottomEmptyNode.setStyles({ "border-left": "6px solid "+ lp.progressColor });
                    break;
                case "需要我审核":
                    bottomEmptyNode.setStyles({ "border-left": "6px solid "+ lp.auditColor });
                    break;
                case "已完成":
                    bottomEmptyNode.setStyles({ "border-left": "6px solid " + lp.completedColor });
                    break;
            }
        }

    },
    expend : function(){
        this.oSize = this.node.getSize();
        this.container.setStyles({
            "position" : "relative"
        });
        this.tempNode = new Element("div",{
            styles : {
                width : (this.node.getSize().x ) + "px",
                height : "1px",
                margin : "7px"
            }
        }).inject(this.container);
        this.node.setStyles({
            "height" : this.node.getScrollSize().y + "px",
            "width" : (this.node.getSize().x ) + "px",
            "position" : "absolute",
            "top" : "0px",
            "left" : "0px",
            "box-shadow": "0 0 8px 0 rgba(0,0,0,0.25)"
        });
        var nodeCoordinate = this.node.getCoordinates();
        var contentNode = this.calendar.contentWarpNode;
        var contentCoordinate = contentNode.getCoordinates();
        if( nodeCoordinate.bottom > contentCoordinate.bottom ){
            this.contentHeight = contentCoordinate.height;
            contentNode.setStyle("height", (  nodeCoordinate.bottom - contentCoordinate.top  )+"px"  );
        }
        this.isCollapse = false;
    },
    collapse : function(){
        if( !this.collapseDisable && this.collapseReady){
            this.container.setStyles({
                "position" : "static"
            });
            if( this.tempNode )this.tempNode.destroy();
            this.node.setStyles({
                "height" : "140px",
                "width" : "auto",
                "position" : "static",
                "box-shadow": "none"
            });
            if( this.contentHeight ){
                var contentNode = this.calendar.contentWarpNode;
                contentNode .setStyle("height", ( this.contentHeight )+"px"  );
                this.contentHeight = null;
            }
            this.isCollapse = true;
        }
    },
    destroy: function(){
        this.reports.each(function(report){
            report.destroy();
        }.bind(this));
        this.reports = [];
        this.titleNode.destroy();
        this.titleNode = null;
        this.titleDayNode = null;
        this.titleInforNode = null;

        delete this.calendar.days[this.key];

        this.container.empty();
        MWF.release(this);
    },
    reload: function(){
        this.view.reload();
    }
});
MWF.xApplication.Report.MonthView.Calendar.Day.Report = new Class({
    initialize: function(day, data, index){
        this.day = day;
        this.css = this.day.css;
        this.view = this.day.view;
        this.app = this.day.app;
        this.container = this.day.contentNode;
        this.data = data;
        this.index = index;
        this.load();
    },
    load: function(){
        var d = this.data;
        this.nodeStyles = (this.day.type == "today") ? this.css.reportNode_today : this.css.reportNode;
        this.node = new Element("div", {
            "styles": this.nodeStyles
            //"title" : d.title
        }).inject(this.container);
        //this.iconNode = new Element("div", {"styles": this.css.reportIconNode}).inject(this.node);
        //this.timeNode = new Element("div", {"styles": this.css.reportTimeNode}).inject(this.node);
        this.textNode = new Element("div", {"styles": this.css.reportTextNode}).inject(this.node);
        //var timeStr = Date.parse(this.data.dateString).format("%H:%M");
        //this.timeNode.set("text", timeStr);
        var text = d[  d.reportObjType == "UNIT" ? "targetUnit" : "targetPerson"].split("@")[0] +
            "(" + this.app.lp[d.reportObjType] + this.app.lp[d.reportType] +")";

        this.textNode.set("text", text);
        //this.node.set("title", this.data.subject);
        //
        //if (this.data.myWaitAccept){
        //    this.iconNode.setStyle("background", "url(/x_component_Report/$MonthView/"+this.app.options.style+"/icon/invite.png) no-repeat center center");
        //}


        var lp = this.app.lp.config;
        var status;
        if( d.reportStatus == "审核中" && this.app.userName == d.currentPersonName ){
            status = "需要我审核"
        }else{
            status = d.reportStatus;
        }
        switch (status){
            case "汇报者填写":
                this.node.setStyles({ "border-left": "6px solid "+ lp.waitColor });
                break;
            case "审核中":
                this.node.setStyles({ "border-left": "6px solid "+ lp.progressColor });
                break;
            case "需要我审核":
                this.node.setStyles({ "border-left": "6px solid "+ lp.auditColor });
                break;
            case "已完成":
                this.node.setStyles({ "border-left": "6px solid "+ lp.completedColor });
                break;
        }

        this.node.addEvents({
            mouseenter : function(){
                this.day.collapseReady = false;
                this.node.setStyles( this.css.reportNode_over );
            }.bind(this),
            mouseleave : function(){
                this.node.setStyles( this.nodeStyles );
            }.bind(this),
            "click": function(){this.openReport();}.bind(this)
        });
        this.loadTooltip();
    },
    loadTooltip : function(){
        this.tooltip = new MWF.xApplication.Report.ReportTooltip(this.app.content, this.node, this.app, this.data, {
            axis : "x",
            hiddenDelay : 300,
            displayDelay : 300,
            onShow : function(){
                this.day.collapseDisable = true;
            }.bind(this),
            onQueryCreate : function(){
                this.day.collapseDisable = true;
            }.bind(this),
            onHide : function(){
                this.day.collapseDisable = false;
                this.day.collapse();
            }.bind(this)
        });
    },
    openReport: function(){
        this.app.common.openReport(this.data, this);
    },
    destroy: function(){
        if(this.tooltip)this.tooltip.destroy();
       this.node.destroy();
       MWF.release(this);
    },
    reload: function(){
        this.view.reload();
    }
});


MWF.xApplication.Report.MonthView.Calendar.MonthSelector = new Class({
    Implements: [Events],
    initialize: function(date, calendar){
        this.calendar = calendar;
        this.css = this.calendar.css;
        this.app = this.calendar.app;
        this.date = date;
        this.year = this.date.get("year");
        this.load();
    },
    load: function(){
        this.monthSelectNode = new Element("div", {"styles": this.css.calendarMonthSelectNode}).inject(this.calendar.container);
        this.monthSelectNode.position({
            relativeTo: this.calendar.titleTextNode,
            position: 'bottomCenter',
            edge: 'upperCenter'
        });
        this.monthSelectNode.addEvent("mousedown", function(e){e.stopPropagation();});

        this.monthSelectTitleNode = new Element("div", {"styles": this.css.calendarMonthSelectTitleNode}).inject(this.monthSelectNode);
        this.monthSelectPrevYearNode = new Element("div", {"styles": this.css.calendarMonthSelectTitlePrevYearNode}).inject(this.monthSelectTitleNode);
        this.monthSelectNextYearNode = new Element("div", {"styles": this.css.calendarMonthSelectTitleNextYearNode}).inject(this.monthSelectTitleNode);
        this.monthSelectTextNode = new Element("div", {"styles": this.css.calendarMonthSelectTitleTextNode}).inject(this.monthSelectTitleNode);
        this.monthSelectTextNode.set("text", this.year);

        var html = "<tr><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td></tr>";
        this.monthSelectTable = new Element("table", {
            "styles": {"margin-top": "10px"},
            "height": "200px",
            "width": "90%",
            "align": "center",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0", //5
            "html": html
        }).inject(this.monthSelectNode);

        //this.loadMonth();

        this.monthSelectBottomNode = new Element("div", {"styles": this.css.calendarMonthSelectBottomNode, "text": this.app.lp.today}).inject(this.monthSelectNode);

        this.setEvent();
    },
    loadMonth: function(){
        this.monthSelectTextNode.set("text", this.year);
        var d = new Date();
        var todayY = d.get("year");
        var todayM = d.get("month");

        var thisY = this.date.get("year");
        var thisM = this.date.get("month");

        var _self = this;
        var tds = this.monthSelectTable.getElements("td");
        tds.each(function(td, idx){
            td.empty();
            td.removeEvents("mouseover");
            td.removeEvents("mouseout");
            td.removeEvents("mousedown");
            td.removeEvents("mouseup");
            td.removeEvents("click");

            var m = idx+1;
            td.store("month", m);
            td.setStyles(this.css.calendarMonthSelectTdNode);

            td.setStyle("background-color", "#FFF");
            if ((this.year == todayY) && (idx == todayM)){
                new Element("div", {
                    styles : _self.css.calendarMonthSelectTodayNode,
                    text : ""+m+this.app.lp.month
                }).inject( td );
            }else if ((this.year == thisY) && (idx == thisM)){
                //td.setStyle("background-color", "#EEE");
                new Element("div", {
                    styles : _self.css.calendarMonthSelectCurrentNode,
                    text : ""+m+this.app.lp.month
                }).inject( td );
            }else{
                td.set("text", ""+m+this.app.lp.month);
            }

            td.addEvents({
                "mouseover": function(){this.setStyles(_self.css.calendarMonthSelectTdNode_over);},
                "mouseout": function(){this.setStyles(_self.css.calendarMonthSelectTdNode);},
                "mousedown": function(){this.setStyles(_self.css.calendarMonthSelectTdNode_down);},
                "mouseup": function(){this.setStyles(_self.css.calendarMonthSelectTdNode_over);},
                "click": function(){
                    _self.selectedMonth(this);
                }
            });
        }.bind(this));
    },
    setEvent: function(){
        this.monthSelectPrevYearNode.addEvent("click", function(){
            this.prevYear();
        }.bind(this));

        this.monthSelectNextYearNode.addEvent("click", function(){
            this.nextYear();
        }.bind(this));

        this.monthSelectBottomNode.addEvents({
            "mouuseover" : function(){ this.monthSelectBottomNode.setStyles( this.css.calendarMonthSelectBottomNode_over ); }.bind(this),
            "mouuseout" : function(){ this.monthSelectBottomNode.setStyles( this.css.calendarMonthSelectBottomNode ); }.bind(this),
            "click" : function(){ this.todayMonth(); }.bind(this)
        });
    },
    prevYear: function(){
        this.year--;
        if (this.year<1900) this.year=1900;
        this.monthSelectTextNode.set("text", this.year);
        this.loadMonth();
    },
    nextYear: function(){
        this.year++;
        //if (this.year<1900) this.year=1900;
        this.monthSelectTextNode.set("text", this.year);
        this.loadMonth();
    },
    todayMonth: function(){
        var d = new Date();
        this.calendar.changeMonthTo(d);
        this.hide();
    },
    selectedMonth: function(td){
        var m = td.retrieve("month");
        var d = Date.parse(this.year+"/"+m+"/1");
        this.calendar.changeMonthTo(d);
        this.hide();
    },

    show: function(){
        this.date = this.calendar.date;
        this.year = this.date.get("year");
        this.loadMonth();
        this.monthSelectNode.setStyle("display", "block");
        this.hideFun = this.hide.bind(this);
        document.body.addEvent("mousedown", this.hideFun);
    },
    hide: function(){
        this.monthSelectNode.setStyle("display", "none");
        document.body.removeEvent("mousedown", this.hideFun);
    },

    destroy: function(){
        //this.titleNode.destroy();
        //this.titleNode = null;
        //this.titleDayNode = null;
        //this.titleInforNode = null;
        //
        //delete this.calendar.days[this.key];
        //
        //this.node.empty();
        //MWF.release(this);
    }

});