MWF.require("MWF.widget.Calendar", null, false);
MWF.xApplication.Report.DayView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date": null
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "/x_component_Report/$DayView/";
        this.cssPath = "/x_component_Report/$DayView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        var d = this.options.date;
        if( d ){
            this.date =  typeOf( d ) == "string" ? new Date( d ) : d;
        }else{
            this.date = new Date();
        }
        this.load();
    },
    recordStatus : function(){
        return {
            date : (this.days.length > 0 ? this.days[0].date.clone() : this.date)
        };
    },
    load: function(){
        this.days = [];
        this.scrollNode = new Element("div.scrollNode", {
            "styles": this.app.inContainer ? this.css.scrollNode_inContainer : this.css.scrollNode
        }).inject(this.container);
        this.contentWarpNode = new Element("div", {
            "styles": this.css.contentWarpNode
        }).inject(this.scrollNode);

        this.contentContainerNode = new Element("div",{
            "styles" : this.css.contentContainerNode
        }).inject(this.contentWarpNode);
        this.node = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.contentContainerNode);

        this.leftNode = new Element("div",{
            "styles" : this.css.leftNode_disable
        }).inject(this.node);
        this.leftNode.addEvents( {
            "click" : function(){ if( this.pageNum != 1 )this.decrementDay() }.bind(this),
            "mouseover" : function(){ if( this.pageNum != 1 )this.leftNode.setStyles( this.css.leftNode_over ) }.bind(this),
            "mouseout" : function(){ if( this.pageNum != 1 )this.leftNode.setStyles( this.css.leftNode ) }.bind(this)
        });

        this.dayContainerNode = new Element("div", {
            "styles": this.css.dayContainerNode
        }).inject(this.node);

        this.rightNode = new Element("div",{
            "styles" : this.css.rightNode_disable
        }).inject(this.node);
        this.rightNode.addEvents( {
            "click" : function(){ if( this.pageNum < this.totalPage )this.incrementDay() }.bind(this),
            "mouseover" : function(){ if( this.pageNum < this.totalPage )this.rightNode.setStyles( this.css.rightNode_over ) }.bind(this),
            "mouseout" : function(){ if( this.pageNum < this.totalPage )this.rightNode.setStyles( this.css.rightNode ) }.bind(this)
        });

        //this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        //this.loadSideBar();

        this.resetNodeSize();
        this.resetNodeSizeFun = this.resetNodeSize.bind(this);
        this.app.addEvent("resize", this.resetNodeSizeFun );

        //this.dateNode = new Element("div", {"styles": this.css.dateNode}).inject(this.node);

    },
    resetNodeSize: function(){
        var size = this.container.getSize();
        var sizeY = this.app.inContainer ? 800 : size.y;

        var leftNodeSize = this.leftNode ? this.leftNode.getSize() : {x:0,y:0};
        var rightNodeSize = this.rightNode ? this.rightNode.getSize() : {x:0,y:0};
        var sideBarSize = this.app.sideBar ? this.app.sideBar.getSize() : {x:0,y:0};

        var availableX = size.x - leftNodeSize.x - rightNodeSize.x - sideBarSize.x ;

        this.dayNodeHeight = sizeY-110;

        var leftTop = ( this.dayNodeHeight - leftNodeSize.y ) / 2;
        var rightTop = ( this.dayNodeHeight - rightNodeSize.y ) / 2;
        this.leftNode.setStyle("margin-top", ""+leftTop+"px");
        this.rightNode.setStyle("margin-top", ""+rightTop+"px");

        var dayCount = (availableX/330).toInt();

        if( this.app.inContainer ){
            this.scrollNode.setStyle("min-height", ""+(sizeY-60)+"px");
        }else{
            this.scrollNode.setStyle("height", ""+(sizeY-60)+"px");
        }
        this.scrollNode.setStyle("margin-top", "60px");
        this.scrollNode.setStyle("margin-right", sideBarSize.x);

        if (this.contentWarpNode){
            var x = 330 * dayCount + leftNodeSize.x + rightNodeSize.x + sideBarSize.x;
            var m = (size.x - x)/2-10;
            this.contentWarpNode.setStyles({
                "width": ""+x+"px",
                "margin-left": ""+m+"px"
            });
        }
        if( this.dayCount != dayCount ){
            this.dayCount = dayCount;
            this.getPageNumberForDay( function(){
                if( !this.totalPage ){
                    this.showNoReportNode();
                }else{
                    this.adjustDay();
                }
            }.bind(this))
        }else{
            for(var i = 0; i<this.days.length; i++ ){
                this.days[i].resetHeight();
            }
        }
    },
    getPageNumberForDay : function( callback ){
        var d = this.date.format("%Y-%m-%d");
        this.app.restActions.getPageNumberForDay( d, this.dayCount, function( json ){
            this.pageNum = json.data.currentPage;
            this.totalPage = json.data.totalPage;
            //this.totalCount = 2;
            if( callback )callback();
        }.bind(this))
    },
    listDayForPage : function( callback ){
        this.app.restActions.listDayForPage( this.pageNum, this.dayCount, function( json ){
            this.data = {};
            json.data.each( function( d , i ){
                if( i == 0 ){
                    this.date = Date.parse(d.date );
                }
                this.data[d.date] = d.reports;
            }.bind(this));
            if(callback)callback();
        }.bind(this))
    },
    toDay: function(date){
        this.date = date;
        this.dayContainerNode.empty();
        this.leftNode.setStyles( this.css.leftNode_disable );
        this.rightNode.setStyles( this.css.rightNode_disable );
        this.days = [];
        this.getPageNumberForDay( function(){
            this.adjustDay();
        }.bind(this))
    },
    showNoReportNode : function(){
        if( this.noReportNode )this.noReportNode.destroy();
        this.noReportNode = new Element("div",{
            "styles" : this.css.noReportNode,
            "text" : this.app.lp.noReportDayView
        }).inject( this.contentContainerNode, "top" );
        this.setLeftRightNode();
    },
    adjustDay: function(){
        if( this.dayCount <= this.days.length ){
            for(var i = 0; i<this.days.length; i++ ){
                if( i < this.dayCount ){
                    this.days[i].resetHeight();
                }else{
                    if(this.days[i])this.days[i].destroy();
                }
            }
            this.days.splice( this.dayCount, (this.days.length - this.dayCount) );
            this.setLeftRightNode()
        }else{
            for(var i = 0; i<this.days.length; i++ ){
                this.days[i].resetHeight();
            }
            this.listDayForPage( function(){
                for( var key in this.data ){
                    var d = this.data[key];
                    var flag = true;
                    this.days.each( function( day ){
                        if( day.date.format("%Y-%m-%d") == key ){
                            flag = false;
                        }
                    }.bind(this));
                    if( flag ){
                        this.loadDay( Date.parse(key), d, this.days.length==0 )
                    }
                }
                this.setLeftRightNode()
            }.bind(this))
        }
    },
    setLeftRightNode: function(){
        if( this.pageNum == 1 || this.totalPage==0 ){
            this.leftNode.setStyles( this.css.leftNode_disable );
        }else{
            this.leftNode.setStyles( this.css.leftNode );
        }
        if( this.pageNum >= this.totalPage || this.totalPage==0 ){
            this.rightNode.setStyles( this.css.rightNode_disable );
        }else{
            this.rightNode.setStyles( this.css.rightNode );
        }
    },
    incrementDay : function(){
        if( this.pageNum >= this.totalPage )return;
        this.pageNum++;
        this.days.each( function( day ){
            day.destroy();
        }.bind(this));
        this.days = [];
        this.dayContainerNode.empty();
        this.leftNode.setStyles( this.css.leftNode_disable );
        this.rightNode.setStyles( this.css.rightNode_disable );
        this.adjustDay();
    },
    decrementDay : function( node ){
        if( this.pageNum == 1 )return;
        this.pageNum--;
        this.days.each( function( day ){
            day.destroy();
        }.bind(this));
        this.days = [];
        this.dayContainerNode.empty();
        this.leftNode.setStyles( this.css.leftNode_disable );
        this.rightNode.setStyles( this.css.rightNode_disable );
        this.adjustDay();
    },
    loadDay: function( date, data, setFirst ){
        var day = new MWF.xApplication.Report.DayView.Day(this, this.dayContainerNode, null, date, setFirst, data );
        this.days.push( day );
        //this.dayA.loadAction();
    },

    hide: function(){
        //this.app.removeEvent("resize", this.resetNodeSizeFun );
        var fx = new Fx.Morph(this.scrollNode, {
            "duration": "300",
            "transition": Fx.Transitions.Expo.easeOut
        });
        fx.start({
            "opacity": 0
        }).chain(function(){
            this.scrollNode.setStyle("display", "none");
        }.bind(this));

    },
    show: function(){
        //this.app.addEvent("resize", this.resetNodeSizeFun );
        this.scrollNode.setStyles(this.app.inContainer ? this.css.scrollNode_inContainer : this.css.scrollNode);
        this.scrollNode.setStyles({"display" : ""});
        var fx = new Fx.Morph(this.scrollNode, {
            "duration": "800",
            "transition": Fx.Transitions.Expo.easeOut
        });
        this.app.fireAppEvent("resize");
        fx.start({
            "opacity": 1,
            "left": "0px"
        }).chain(function(){
            this.scrollNode.setStyles({
                "position": "static",
                "width": "auto",
                "display" : ""
            });
        }.bind(this));
    },
    reload: function(){
        this.date = (this.days.length > 0 ? this.days[0].date.clone() : this.date);
        this.days.each( function(d){
            d.destroy();
        });
        this.dayContainerNode.empty();
        this.days = [];

        this.getPageNumberForDay( function(){
            if( !this.totalPage ){
                this.showNoReportNode();
            }else{
                this.adjustDay();
            }
        }.bind(this))
    },
    destroy : function(){
        this.days.each( function(d){
            d.destroy();
        });
        this.app.removeEvent("resize", this.resetNodeSizeFun );
        this.scrollNode.destroy();
    }

});

MWF.xApplication.Report.DayView.Day = new Class({
    Implements: [Events],
    initialize: function(view, node, position, date, isFirst, data){
        this.view = view;
        this.css = this.view.css;
        this.container = node;
        this.position = position || "bottom";
        this.app = this.view.app;
        this.date = (date) ? date.clone().clearTime() : (new Date()).clearTime();
        this.data = data;
        this.today = new Date().clearTime();
        this.isToday = (this.date.diff(this.today)==0);
        this.times = [];
        this.reports = [];
        this.isFirst = isFirst;
        this.load();
    },
    load : function(){
        this.node = new Element("div.dayNode", {"styles": this.css.dayNode}).inject(this.container , this.position);
        this.node.setStyle("min-height",""+this.view.dayNodeHeight+"px");
        this.node.addEvents( {
            mouseover : function(){
                this.node.setStyles( this.css.dayNode_over  );
            }.bind(this),
            mouseout : function(){
                this.node.setStyles( this.css.dayNode  );
            }.bind(this)
         });

        this.titleNode = new Element("div.titleNode", { "styles": this.css[ !this.isToday ? "dayTitleNode" : "dayTitleNode_today"] }).inject(this.node);


        if( this.isFirst ){
            className = !this.isToday ? "dayTitleTextNode_first" : "dayTitleTextNode_today_first";
        }else{
            className = !this.isToday ? "dayTitleTextNode" : "dayTitleTextNode_today";
        }
        this.titleTextNode = new Element("div.dayTitleTextNode", {
            "styles": this.css[ className ],
            "text" :  this.date.format("%Y年%m月%d日")
        }).inject(this.titleNode);
        if( this.isFirst ){
            this.calendar = new MWF.xApplication.Report.Calendar(this.titleTextNode, {
                "style":"meeting_blue",
                "target": this.node,
                "baseDate" : this.date,
                "onQueryComplate": function(e, dv, date){
                    var selectedDate = new Date.parse(dv);
                    this.view.toDay(selectedDate);
                }.bind(this)
            });
            this.calendar.app = this.app;
        }

        this.dayWeekNode = new Element("div.dayWeekNode", {
            "styles": this.css[ !this.isToday ? "dayWeekNode" : "dayWeekNode_today"],
            "text" : this.getWeek()
        }).inject(this.titleNode);

        this.dayContentNode = new Element("div.dayContentNode", {"styles": this.css.dayContentNode}).inject(this.node);

       this.loadReports();

    },
    resetHeight: function(){
        this.node.setStyle("min-height",""+this.view.dayNodeHeight+"px");
        if( this.noReportNode ){
            this.noReportNode.setStyle("min-height",""+(this.view.dayNodeHeight - 220)+"px");
            this.noReportNode.setStyle("line-height",""+(this.view.dayNodeHeight - 220)+"px");
        }
    },
    getWeek: function(){
        var week = this.app.lp.weeks.arr[this.date.getDay()];
        var title = "";
        var now = this.today;
        var d = now.diff(this.date);
        if (d==0){
            title = this.app.lp.today;
        }else{
            title = week;
        }
        return title;
    },
    setFrist: function(){
        if( this.isFirst )return;
        this.isFirst = true;
        className = !this.isToday ? "dayTitleTextNode_first" : "dayTitleTextNode_today_first";
        this.titleTextNode.setStyles( this.css[ className ] );
        this.calendar = new MWF.xApplication.Report.Calendar(this.titleTextNode, {
            "style":"meeting_blue",
            "target": this.node,
            "baseDate" : this.date,
            "onQueryComplate": function(e, dv, date){
                var selectedDate = new Date.parse(dv);
                this.view.toDay(selectedDate);
            }.bind(this)
        });
        this.calendar.app = this.app;
    },
    //disposeFrist : function(){
    //    if( !this.isFirst )return;
    //    this.isFirst = false;
    //    this.titleTextNode.removeEvent("click");
    //    this.titleTextNode.removeEvent("focus");
    //    var className = !this.isToday ? "dayTitleTextNode" : "dayTitleTextNode_today";
    //    this.titleTextNode.setStyles( this.css[ className ] );
    //    this.calendar.container.destroy();
    //    this.calendar = null;
    //},
    destroy: function(){
        if( this.calendar ){
            this.calendar.container.destroy();
        }
        this.reports.each( function(m){
            m.destroy();
        });
        this.reports = [];
        this.node.destroy();
    },
    loadReports: function(){
        this.data.each( function( d ){
            this.reports.push(new MWF.xApplication.Report.ReportArea(this.dayContentNode, this, d));
        }.bind(this));
        //
        //this.app.actions.listReportDay(y, m, d, function(json){
        //    var flag = true;
        //    if( !json.data || json.data.length == 0 ){
        //    }else{
        //        json.data.each(function(report, i){
        //            if (!report.myReject){
        //                flag = false;
        //                this.reports.push(new MWF.xApplication.Report.DayView.Report(this.dayContentNode, this, report));
        //            }
        //        }.bind(this));
        //    }
        //    if( flag ){
        //        this.noReportNode = new Element("div.noReportNode", {
        //            "styles": this.css.noReportNode,
        //            "text" :  this.app.lp.noReport
        //        }).inject(this.dayContentNode);
        //        this.noReportNode.setStyle("min-height",""+(this.view.dayNodeHeight - 220)+"px");
        //        this.noReportNode.setStyle("line-height",""+(this.view.dayNodeHeight - 220)+"px");
        //    }
        //}.bind(this));
    },
    reload : function(){
        this.view.reload();
    }
});

MWF.xApplication.Report.Calendar = new Class({
    Extends : MWF.widget.Calendar,
    _setDayDate: function(table, year, month){
        var baseDate = this.options.baseDate;
        if ((year!=undefined) && (month!=undefined)){
            baseDate = new Date();
            baseDate.setDate(1);
            baseDate.setFullYear(year);
            baseDate.setMonth(month);
        }
        this.loadDayData( baseDate, function(){
            this._setDayD( table, year, month )
        }.bind(this))
    },
    loadDayData : function(date, callback){
        var da = date.clone();
        da.decrement("month", 1);
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
    _setDayD: function(table, year, month){
        var dayTable = table || this.contentTable;
        var baseDate = this.options.baseDate;
        if ((year!=undefined) && (month!=undefined)){
            baseDate = new Date();
            baseDate.setDate(1);
            baseDate.setFullYear(year);
            baseDate.setMonth(month);
        }

        var tbody = dayTable.getElement("tbody");
        var tds = tbody.getElements("td");

        var firstDate = baseDate.clone();
        firstDate.setDate(1);
        var day = firstDate.getDay();

        var tmpDate = firstDate.clone();
        for (var i=day-1; i>=0; i--){
            var td = tds[i];
            tmpDate.increment("day", -1);
            td.set("text", tmpDate.getDate());
            td.addClass("gray_"+this.options.style);
            td.setStyles(this.css["gray_"+this.options.style]);
            td.store("dateValue", tmpDate.toString());
            if( this.data[ tmpDate.format("%Y-%m-%d") ] ){
                td.setStyles({
                    "position":"relative",
                    "cursor" : "pointer"
                });
                new Element("div",{
                    "position" : "absolute",
                    "top" : "2px",
                    "right" : "2px",
                    "width" : "2px",
                    "height" : "2px",
                    "background-color" : "#4990e2",
                    "border-radius" : "5px"
                }).inject( td )
            }else{
                tds[i].setStyles({
                    "cursor" : "default"
                });
            }
        }

        for (var i=day; i<tds.length; i++){
            tds[i].set("text", firstDate.getDate());
            if (firstDate.toString() == this.options.baseDate.toString()){
                tds[i].addClass("current_"+this.options.style);
                tds[i].setStyles(this.css["current_"+this.options.style]);

                tds[i].removeClass("gray_"+this.options.style);
                tds[i].setStyle("border", "1px solid #FFF");
            }else if (firstDate.getMonth()!=baseDate.getMonth()){
                tds[i].addClass("gray_"+this.options.style);
                tds[i].setStyles(this.css["gray_"+this.options.style]);
                tds[i].removeClass("current_"+this.options.style);
                tds[i].setStyle("border", "1px solid #FFF");
            }else{
                tds[i].setStyles(this.css["normal_"+this.options.style]);
                tds[i].removeClass("current_"+this.options.style);
                tds[i].removeClass("gray_"+this.options.style);
                tds[i].setStyle("border", "1px solid #FFF");
            }
            var tmp = firstDate.clone();
            if (tmp.clearTime().toString() == this.today.clearTime().toString()){
                //tds[i].addClass("today_"+this.options.style);
                tds[i].setStyles(this.css["today_"+this.options.style]);
                tds[i].setStyle("border", "0px solid #AAA");
            }
            tds[i].store("dateValue", firstDate.toString());

            if( this.data[ firstDate.format("%Y-%m-%d") ] ){
                var td = tds[i];
                td.setStyles({
                    "position":"relative",
                    "cursor" : "pointer"
                });
                new Element("div", { styles : {
                        "position" : "absolute",
                        "top" : "5px",
                        "right" : "5px",
                        "width" : "5px",
                        "height" : "5px",
                        "background-color" : "#4990e2",
                        "border-radius" : "5px"
                    }
                }).inject( td )
            }else{
                tds[i].setStyles({
                    "cursor" : "default"
                });
            }

            firstDate.increment("day", 1);
        }
    },
    _selectDate: function(dateStr){
        var date = new Date(dateStr);
        if( !this.data[ date.format("%Y-%m-%d") ] ){
            return;
        }
        var dv = date.format(this.options.format);
        if (this.options.isTime){
            this.changeViewToTime(date);
        }else{
            if (!this.options.beforeCurrent){
                var now = new Date();
                date.setHours(23,59,59);
                if (date.getTime()-now.getTime()<0){
                    alert("选择的日期必须大于当前日期!");
                    this.node.focus();
                    return false;
                }
            }
            if (this.fireEvent("queryComplate", [dv, date])){
                this.node.set("value", dv);
                this.hide();
                this.fireEvent("complate", [dv, date]);
            }
        }
    }
});



