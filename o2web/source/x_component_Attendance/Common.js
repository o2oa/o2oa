MWF.xApplication.Attendance = MWF.xApplication.Attendance || {};

MWF.xDesktop.requireApp("Attendance", "lp."+MWF.language, null, false);

//MWF.xApplication.Attendance.Org = new Class({
//    initialize: function ( actions ){
//        this.actions = actions
//    },
//    getUnitByPerson : function( name, callback, fail, async ){
//        this.actions.listUnitByPerson( function( json ){
//            if( callback )callback(json.data);
//        }, null, async )
//    },
//    getTopUnitByPerson : function( name, callback ){
//        this.actions.listTopUnitByPerson( function( json ){
//            if( callback )callback(json.data);
//        }, null, name )
//    }
//})

MWF.xApplication.Attendance.Calendar = new Class({
    Implements: [Options, Events],
    options : {
        date : null
    },
    initialize: function (node, explorer, data, options) {
        this.setOptions(options);
        this.app = explorer.app;
        this.explorer = explorer;
        this.actions = this.app.actions;
        this.node = node;
        this.css = explorer.css;
        //this.data = data;
        this.holiday = data.holiday;
        this.eventData = data.eventData;
        this.detail = data.detail;
        this.statusColor = explorer.statusColor;
    },
    reload: function () {
        this.node.empty();
        this.load();
    },
    load : function(){
        this.loadResource(function(){
            this.loadCalendar();
        }.bind(this))
    },
    loadResource: function ( callback ) {
        var baseUrls = [
            "/x_component_Attendance/$Common/fullcalendar/lib/moment.js",
            "/x_component_Attendance/$Common/fullcalendar/lib/jquery.js"
        ];
        var fullcalendarUrl = "/x_component_Attendance/$Common/fullcalendar/fullcalendar.js";
        var langUrl =  "/x_component_Attendance/$Common/fullcalendar/lang/zh-cn.js";
        COMMON.AjaxModule.loadCss("/x_component_Attendance/$Common/fullcalendar/fullcalendar.css",function(){
            COMMON.AjaxModule.load(baseUrls, function(){
                jQuery.noConflict();
                COMMON.AjaxModule.load(fullcalendarUrl, function(){
                    COMMON.AjaxModule.load(langUrl, function(){
                        if(callback)callback();
                    }.bind(this))
                }.bind(this));
            }.bind(this))
        }.bind(this))
    },
    loadCalendar : function(){
        var _self = this;
        jQuery(this.node).fullCalendar({
            header: {
                left: '', //'prev,next today',
                center: '', //'title',
                right: '' //'holidayButton'
            },
            contentHeight: 350,
            defaultDate: this.options.date.format( this.app.lp.dateFormatDay ) ,
            //editable: true,
            eventColor : "#fff",
            eventTextColor : "#fff",
            rendingNumberCellFun : function( date, formatedDate, dateStr ){
                if( this.holiday && this.holiday.holidays && this.holiday.holidays.contains( formatedDate )){
                    return "<span style='float:right;padding-right:5px;color:red'>休</span>"
                }
                if( this.holiday && this.holiday.workdays && this.holiday.workdays.contains( formatedDate ) ){
                    return "<span style='float:right;padding-right:5px;'>班</span>"
                }
            }.bind(this),
            eventMouseover : function(event, jsEvent, view){
                jsEvent.target.title = event.text;
            },
            events: this.eventData
        });
    }
});

MWF.xApplication.Attendance.Echarts = new Class({
    Implements: [Options, Events],
    options :{
        "type" : "pie",
        "date" : null
    },
    initialize: function (node, explorer, data,  options) {
        this.setOptions(options);
        this.app = explorer.app;
        this.lp = this.app.lp;
        this.actions = this.app.actions;
        this.node = node;
        this.css = explorer.css;
        this.data = data;
        this.statusColor = explorer.statusColor;
    },
    load : function(){
        if( !this.options.date )this.options.date = new Date();
        this.year = this.options.date.getFullYear();
        var month = this.options.date.getMonth()+1;
        this.month = month.toString().length == 2 ? month : "0"+month;
        this.loadResource(function(){
            if( this.options.type == "pie"){
                this.loadPieChart();
            }else{
                this.loadLineChart();
            }
        }.bind(this))
    },
    loadResource: function ( callback ) {
        var baseUrls = [
            "/x_component_Attendance/$Common/echarts/echarts.common.js"
        ];
        var themeUrl = "/x_component_Attendance/$Common/echarts/theme/shine.js";
        COMMON.AjaxModule.load(baseUrls, function(){
            COMMON.AjaxModule.load(themeUrl, function(){
                if(callback)callback();
            }.bind(this))
        }.bind(this));
    },
    loadPieChart: function (  ) {
        this.chart = echarts.init(this.node, 'shine');

        this.chart.setOption({
            title: {
                text: '考勤汇总'
                //subtext: '纯属虚构'
            },
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b}: {c} ({d}%)"
            },
            //legend: {
            //    orient: 'vertical',
            //    x: 'left',
            //    data:['直接访问','邮件营销','联盟广告','视频广告','搜索引擎']
            //},
            series: [
                {
                    name:'考勤状态',
                    type:'pie',
                    radius: ['55%', '70%'],
                    avoidLabelOverlap: false,
                    label: {
                        normal: {
                            show: false,
                            position: 'center'
                        },
                        emphasis: {
                            show: true,
                            textStyle: {
                                fontSize: '30',
                                fontWeight: 'bold'
                            }
                        }
                    },
                    labelLine: {
                        normal: {
                            show: false
                        }
                    },
                    data: this.getPieData()
                }
            ]
        });
    },
    loadLineChart: function () {
        var _self = this;
        var crossMonth = (this.options.cycleStart.getMonth() != this.options.cycleEnd.getMonth());
        var x = this.getDateByMonth();
        var data = this.analyseLineData();
        var option = {
            title: {
                text: '上下班走势图'
            },
            grid : {
                left : 50,
                right : 40
             },
            tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                    var vs, p1, p2, v;
                    if ( !params[0].value || params[0].value == "-") {
                        p1 = "";
                    }else{
                        vs =  params[0].value.toString().split(".");
                        if( vs.length == 1 ){
                            v = vs[0]+":00";
                        }else{
                           v = vs[1].length == 1 ? vs[0]+":"+vs[1]+"0" : vs.join(":")
                        }
                        p1 =  params[0].seriesName + "  " + v;
                    }

                    if ( !params[1].value || params[1].value == "-") {
                        p2 = "";
                    }else{
                        vs =  params[1].value.toString().split(".");
                        if( vs.length == 1 ){
                            v = vs[0]+":00";
                        }else{
                            v = vs[1].length == 1 ? vs[0]+":"+vs[1]+"0" : vs.join(":")
                        }
                        p2 =  params[1].seriesName + "  " + v;
                    }

                    if( crossMonth ){
                        return params[0].name  + '<br />'+ p1 + '<br />' + p2;
                    }else{
                        return _self.year +"-" + _self.month  +"-" + params[0].name  + '<br />'+ p1 + '<br />' + p2;
                    }
                }
            },
            legend: {
                data: ['上班时间', '下班时间']
            }
            //toolbox: {
            //    show: true,
            //    feature: {
            //        dataZoom: {},
            //        //dataView: {readOnly: false},
            //        magicType: {type: ['line', 'bar']},
            //        restore: {},
            //        saveAsImage: {}
            //    }
            //}
        };
        option.yAxis = {
            type: 'value',
            min :  (data.yOffTime.length == 0 && data.yOnTime.length == 0)  ? "5.00" : "dataMin" ,
            max : (data.yOffTime.length == 0 && data.yOnTime.length == 0)  ? "23.00" : "dataMax",
            axisLabel: {
                formatter: function (val) {
                    vs =  val.toString().split(".");
                    if( vs.length == 1 ){
                        return vs[0]+":00";
                    }else{
                        return vs[1].length == 1 ? vs[0]+":"+vs[1]+"0" : vs.join(":")
                    }
                }
            }
        };
        option.xAxis = [
            {
                type: 'category',
                //min : "dataMin" ,
                //max : "dataMax",
                boundaryGap : false,
                data : x,
                axisLabel : {
                    //rotate : crossMonth ? 15 : 0
                }
            }
        ];
        option.series= [
            {
                name:'上班时间',
                type:'line',
                data : data.yOnTime.map(function (str) {
                    return (!str || isNaN(parseFloat(str.replace(':', '.')))) ? "-" : parseFloat(str.replace(':', '.'))
                })
            },
            {
                name:'下班时间',
                type:'line',
                data:data.yOffTime.map(function (str) {
                    return (!str || isNaN(parseFloat(str.replace(':', '.')))) ? "-" : parseFloat(str.replace(':', '.'))
                })
            }
        ];
        this.chart = echarts.init(this.node, 'shine');
        this.chart.setOption(option);
    },
    getPieData : function(){
        var val = [];
        for( var n in this.data){
            val.push({
                value : this.data[n],
                name : this.lp[n],
                itemStyle :{
                    normal : {
                        color : this.statusColor[n]
                    }
                }
            })
        }
        return val;
        //[
        //    {value:335, name:'直接访问'},
        //    {value:310, name:'邮件营销'},
        //    {value:234, name:'联盟广告'},
        //    {value:135, name:'视频广告'},
        //    {value:1548, name:'搜索引擎'}
        //]
    },
    getDateByMonth : function(){
        var days = [];
        if( this.options.cycleStart.getMonth() == this.options.cycleEnd.getMonth() ){
            var year = this.year || new Date().getFullYear();
            var month = this.options.date.getMonth() || new Date().getMonth();
            var date = new Date(year, month, 1);
            while (date.getMonth() === month) {
                days.push( date.getDate() );
                date.setDate(date.getDate() + 1);
            }
        }else{
            var start = this.options.cycleStart.clone();
            var end = this.options.cycleEnd.clone();
            while (start <= end) {
                days.push( start.format("%Y-%m-%d") );
                start.setDate(start.getDate() + 1);
            }
        }
        this.allDates = days;
        return days;
    },
    analyseLineData: function(){
        var days = this.getDateByMonth();
        var dataObj = {};
        this.data.each( function( d ,i ){
            dataObj[d.recordDateString] = d;
        });

        var yOnTime = [];
        var yOffTime = [];
        if( this.options.cycleStart.getMonth() == this.options.cycleEnd.getMonth() ){
            var year = this.year || new Date().getFullYear().toString();
            var month = this.options.date.format( "%m" );
            days.each(function( d ,i ){
                var key = year + "-" + month  + "-" + ( d > 9 ? d : ("0"+d)  );
                var val = dataObj[ key ];
                if( val ){
                    yOnTime.push(  val.onDutyTime != "" ? val.onDutyTime : "-"  );
                    yOffTime.push( val.offDutyTime  != "" ? val.offDutyTime : "-"   )
                }else{
                    yOnTime.push(  "-"  );
                    yOffTime.push( "-"   )
                }
            })
        }else{
            days.each(function( d ,i ){
                var key = d;
                var val = dataObj[ key ];
                if( val ){
                    yOnTime.push(  val.onDutyTime != "" ? val.onDutyTime : "-"  );
                    yOffTime.push( val.offDutyTime  != "" ? val.offDutyTime : "-"   )
                }else{
                    yOnTime.push(  "-"  );
                    yOffTime.push( "-"   )
                }
            })
        }

        return {
            yOnTime : yOnTime,
            yOffTime : yOffTime
        }
    },

    loadUnitPieChart : function() {
        //if( !this.options.date )this.options.date = new Date();
        //this.year = this.options.date.getFullYear()
        //var month = this.options.date.getMonth()+1
        //this.month = month.toString().length == 2 ? month : "0"+month;
        this.loadResource(function(){
            this._loadUnitPieChart();
        }.bind(this))
    },
    _loadUnitPieChart: function (  ) {
        var data = this.getUnitPieData();
        this.chart = echarts.init(this.node, 'shine');
        this.chart.setOption({
            title: {
                text: '考勤汇总'
                //subtext: '纯属虚构'
            },
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b}: {c} ({d}%)"
            },
            //legend: {
            //    orient: 'vertical',
            //    x: 'right',
            //    data: data.name
            //},
            series: [
                {
                    name:'考勤状态',
                    type:'pie',
                    radius: ['55%', '70%'],
                    avoidLabelOverlap: false,
                    label: {
                        normal: {
                            show: false,
                            position: 'center'
                        },
                        emphasis: {
                            show: true,
                            textStyle: {
                                fontSize: '30',
                                fontWeight: 'bold'
                            }
                        }
                    },
                    labelLine: {
                        normal: {
                            show: false
                        }
                    },
                    data: data.data
                }
            ]
        });
    },
    getUnitPieData : function(){
        var data = {
            name : [],
            data : []
        };
        for( var n in this.data){
            data.name.push( this.lp[n] );
            data.data.push({
                value : this.data[n],
                name : this.lp[n],
                itemStyle :{
                    normal : {
                        color : this.statusColor[n]
                    }
                }
            })
        }
        return data;
        //[
        //    {value:335, name:'直接访问'},
        //    {value:310, name:'邮件营销'},
        //    {value:234, name:'联盟广告'},
        //    {value:135, name:'视频广告'},
        //    {value:1548, name:'搜索引擎'}
        //]
    },
    loadUnitBarChart : function(){
        this.loadResource(function(){
            this._loadUnitBarChart();
        }.bind(this))
    },
    _loadUnitBarChart : function(){

        var data = this.getUnitBarData();
        var option = {
            title: {
                text: '考勤趋势'
                //subtext: '纯属虚构'
            },
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            //legend: {
            //    data:['直接访问','邮件营销','联盟广告','视频广告','搜索引擎','百度','谷歌','必应','其他']
            //},
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis : [
                {
                    type : 'category',
                    data : data.name
                }
            ],
            yAxis : [
                {
                    type : 'value'
                }
            ],
            series : data.data
        };
        this.chart = echarts.init(this.node, 'shine');
        this.chart.setOption( option )
    },
    getUnitBarData : function(){
        var obj = {};
        this.data.each(function( d ){
            for( var n in d.data ){
                if( !obj[n] ){
                    obj[n] = {
                        month : [],
                        name : this.lp[n],
                        type : "bar",
                        itemStyle :{
                        normal : { color : this.statusColor[n] } },
                        data :[]
                    }
                }
                obj[n].data.push( d.data[n] );
                obj[n].month.push( d.year+"年"+ d.month+"月" );
            }
        }.bind(this));

        var data = {
            data : []
        };
        for( var o in obj ){
            if( !data.name )data.name = obj[o].month;
            data.data.push( obj[o] )
        }
        return data;
    }
});


MWF.xApplication.Attendance.MonthSelector = new Class({
    Implements: [Events],
    initialize: function(date, explorer){
        this.explorer = explorer;
        this.css = this.explorer.css;
        this.app = this.explorer.app;
        this.date = date;
        this.year = this.date.get("year");
        this.load();
    },
    load: function(){
        this.monthSelectNode = new Element("div", {"styles": this.css.calendarMonthSelectNode}).inject(this.explorer.node);
        this.monthSelectNode.position({
            relativeTo: this.explorer.titleTextNode,
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
            "cellSpacing": "5",
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
            td.set("text", ""+m+this.app.lp.month);

            td.setStyle("background-color", "#FFF");
            if ((this.year == thisY) && (idx == thisM)){
                td.setStyle("background-color", "#EEE");
            }
            if ((this.year == todayY) && (idx == todayM)){
                td.setStyle("background-color", "#CCC");
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

        this.monthSelectBottomNode.addEvent("click", function(){
            this.todayMonth();
        }.bind(this));
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
        this.explorer.changeMonthTo(d);
        this.hide();
    },
    selectedMonth: function(td){
        var m = td.retrieve("month");
        var d = Date.parse(this.year+"/"+m+"/1");
        this.explorer.changeMonthTo(d);
        this.hide();
    },

    show: function(){
        this.date = this.explorer.date;
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
        //delete this.explorer.days[this.key];
        //
        //this.node.empty();
        //MWF.release(this);
    }

});
