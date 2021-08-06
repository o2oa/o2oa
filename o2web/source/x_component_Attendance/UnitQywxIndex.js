MWF.xApplication.Attendance = MWF.xApplication.Attendance || {};

MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.xDesktop.requireApp("Attendance", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Attendance", "Common", null, false);
MWF.xDesktop.requireApp("Attendance", "UnitIndex", null, false);

MWF.xApplication.Attendance.UnitQywxIndex = new Class({
    Extends: MWF.xApplication.Attendance.UnitIndex,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    loadData : function( callback, unit, year, month, async ){
        this.statusColor = {
            "resultNormal" : "#4A90E2", //绿色，正常
            "leaveEarlyTimes":"#AC71E3", //蓝色，早退
            "lateTimes":"#F5A623", //黄色，迟到
            "notSignedCount":"#ee807f", //粉红色,未签到
            "outsideDutyTimes" : "#4FB2E3",//外出签到次数
            "absenteeismTimes" : "#8B572A"//矿工
        };
        if( !unit  )unit = this.unit;
        if( !year )year = this.year;
        if( !month )month = this.month;
        if( this.data[ unit + year + month ] ) {
            if(callback)callback();
        }else{
            var action = o2.Actions.load("x_attendance_assemble_control");
            action.QywxAttendanceStatisticAction.unitMonth(unit, year, month, function(json){
                var d = json.data || {};
                var data = this.data[ unit + year + month ] = {};
                var totals = data.totalData = {
                    resultNormal: d.resultNormal || 0,
                    lateTimes: d.lateTimes || 0,
                    leaveEarlyTimes: d.leaveEarlyTimes || 0,
                    absenteeismTimes: d.absenteeismTimes || 0,
                    outsideDutyTimes: d.outsideDutyTimes || 0,
                    notSignedCount: d.notSignedCount || 0
                }

                var total = 0;
                for( var n in totals  ){
                    total += totals[n];
                }
                data.rateData = {
                    resultNormal : (!totals.resultNormal || !total) ? 0 : ((totals.resultNormal/total * 100).toFixed(2) + "%"),
                    lateTimes : (!totals.lateTimes || !total) ? 0 : ((totals.lateTimes/total * 100).toFixed(2)  + "%"),
                    leaveEarlyTimes : (!totals.leaveEarlyTimes || !total) ? 0 : ((totals.leaveEarlyTimes/total * 100).toFixed(2)  + "%"),
                    absenteeismTimes : (!totals.absenteeismTimes || !total) ? 0 : ((totals.absenteeismTimes/total * 100).toFixed(2)  + "%"),
                    outsideDutyTimes : (!totals.outsideDutyTimes || !total) ? 0 : ((totals.outsideDutyTimes/total * 100).toFixed(2) + "%"),
                    notSignedCount : (!totals.notSignedCount || !total) ? 0 : ((totals.notSignedCount/total* 100).toFixed(2)  + "%")
                }
                if(callback)callback();

            }.bind(this), null, async);
        }
    },
    loadDetail : function(){
        this.detailArea.empty();
        this.detailNode = new Element("div",{
            "styles" : this.css.detailNode
        }).inject(this.detailArea);

        this.detailTitleNode = new Element("div",{
            "styles" : this.css.detailTitleNode,
            "text" : this.lp.attendanceStatisic
        }).inject(this.detailNode)

        var table = new Element("table", {
            "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.table, "class" : "editTable"
        }).inject( this.detailNode );

        var tr = new Element("tr", { "styles" : this.css.listHeadNode }).inject(table);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.name  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.onDutyTimes  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.offDutyTimes   }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.resultNormal  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.lateTimes  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.outsideDutyTimes  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.leaveEarlyTimes  }).inject(tr);

        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.absenteeismTimes  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.notSignedCount  }).inject(tr);
        var action = o2.Actions.load("x_attendance_assemble_control");
        action.QywxAttendanceStatisticAction.personMonthWithUnit(this.unit, this.year, this.month, function(json){
            var data = json.data || [];
            data.sort( function(a, b){
               return b.workDayCount - a.workDayCount;
            });
            data.each(function( d ){
                var tr = new Element("tr").inject(table);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.o2User.split("@")[0] }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.onDutyTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.offDutyTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.resultNormal }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.lateTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.outsideDutyTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.leaveEarlyTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.absenteeismTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.notSignedCount }).inject(tr);
            }.bind(this))

        }.bind(this));
        
    }
});
