MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xApplication.Attendance.HolidayExplorer = new Class({
    Extends: MWF.xApplication.Attendance.Explorer,
    Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Attendance/$HolidayExplorer/";
        this.cssPath = "/x_component_Attendance/$HolidayExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    loadView : function(){
        this.view = new MWF.xApplication.Attendance.HolidayExplorer.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        this.view.load();
        this.setContentSize();
    },
    createDocument: function(){
        if(this.view)this.view._createDocument();
    }
});

MWF.xApplication.Attendance.HolidayExplorer.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.HolidayExplorer.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        this.actions.listHolidayAll(function(json){
            if (callback) callback(json);
        });
    },
    _removeDocument: function(document, isNotice, callback){
        this.actions.listHolidayByYearAndName(document.configYear,document.configName, function( json ){
            json.data.each(function(d){
                this.actions.deleteHoliday(d.id, function(json){
                }.bind(this), null, false);
            }.bind(this));
            if(!isNotice)this.app.notice(this.app.lp.deleteDocumentOK, "success");
            if(callback)callback();
            this.explorer.reload();
        }.bind(this))
    },
    _createDocument: function(){
        var holiday = new MWF.xApplication.Attendance.HolidayExplorer.Holiday(this.explorer);
        holiday.create();
    },
    _openDocument: function( documentData ){
        this.actions.getHoliday( documentData.id, function( json ) {
            var data = json.data;
            var holidy = {
                "configYear" : data.configYear,
                "configName" : data.configName,
                "makeUpClassDay" : []
            };
            this.actions.listHolidayByYearAndName(data.configYear,data.configName, function( json ){
                var startDate, endDate;

                json.data.each(function( d ){
                    if( d.configType == "Workday" ){
                        holidy.makeUpClassDay.push( d.configDate )
                    }else{
                        if( !startDate ){
                            startDate = d.configDate;
                        }else if( new Date(startDate) > new Date(d.configDate) ){
                            startDate = d.configDate;
                        }
                        if( !endDate ){
                            endDate = d.configDate;
                        }else if( new Date(endDate) < new Date(d.configDate) ){
                            endDate = d.configDate;
                        }
                    }
                }.bind(this));
                holidy.makeUpClassDay = holidy.makeUpClassDay.join(",");
                holidy.startDate = startDate;
                holidy.endDate = endDate;
                var h = new MWF.xApplication.Attendance.HolidayExplorer.Holiday(this.explorer,holidy);
                h.edit();
            }.bind(this))
        }.bind(this))
    }

});

MWF.xApplication.Attendance.HolidayExplorer.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});
//
//MWF.xApplication.Attendance.ScheduleExplorer.Schedule = new Class({
//    Extends: MWF.xApplication.Attendance.Explorer.PopupForm,
//    options : {
//        "width": 500,
//        "height": 400,
//        "hasTop" : true,
//        "hasBottom" : true,
//        "title" : "",
//        "draggable" : true,
//        "closeAction" : true
//    },
//    _createTableContent: function(){
//        var lp = this.app.lp.holiday;
//
//        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
//            "<tr><td colspan='2' styles='formTableHead'>"+lp.setHoliday+"</td></tr>" +
//            "<tr><td styles='formTabelTitle' lable='unitName'></td>"+
//            "    <td styles='formTableValue' item='unitName'></td></tr>" +
//            "<tr><td styles='formTabelTitle' lable='onDutyTime'></td>"+
//            "    <td styles='formTableValue' item='onDutyTime'></td></tr>" +
//            "<tr><td styles='formTabelTitle' lable='offDutyTime'></td>"+
//            "    <td styles='formTableValue' item='offDutyTime'></td></tr>" +
//            "<tr><td styles='formTabelTitle' lable='lateStartTime'></td>"+
//            "    <td styles='formTableValue' item='lateStartTime'></td></tr>" +
//            "<tr><td styles='formTabelTitle' lable='leaveEarlyStartTime'></td>"+
//            "    <td styles='formTableValue' item='leaveEarlyStartTime'></td></tr>" +
//            "<tr><td styles='formTabelTitle' lable='absenceStartTime'></td>"+
//            "    <td styles='formTableValue' item='absenceStartTime'></td></tr>" +
//            "</table>"
//        this.formTableArea.set("html",html);
//
//        MWF.xDesktop.requireApp("Template", "MForm", function(){
//            this.form = new MForm( this.formTableArea, this.data, {
//                isEdited : this.isEdited || this.isNew,
//                itemTemplate : {
//                    configYear : {
//                        "text" : lp.year, notEmpty : true,
//                        "type" : "select",
//                        "defaultValue" : new Date().getFullYear() ,
//                        "selectValue" : function(){
//                            var years = [];
//                            var year = new Date().getFullYear()+5;
//                            for(var i=0; i<11; i++ ){
//                                years.push( year-- );
//                            }
//                            return years;
//                        }
//                    },
//                    configName : { text: lp.name, notEmpty : true },
//                    startDate : { text: lp.startDate,  tType : "date", notEmpty : true },
//                    endDate : { text: lp.endDate, tType : "date", notEmpty : true },
//                    makeUpClassDay : {  text:lp.makeUpClassDay, tType : "date" }
//                }
//            }, this.app);
//            this.form.load();
//        }.bind(this), true);
//    },
//    _ok: function( data, callback ){
//            var endDate = new Date( data.endDate );
//            var startDate = new Date( data.startDate  );
//            if( startDate > endDate ){
//                this.app.notice("开始日期不能大于结束日期","error");
//                return;
//            }
//
//            var save = function(){
//                var error = "";
//                this.getDateByRange(startDate,endDate).each(function( date ){
//                    this.app.restActions.saveHoliday({
//                            "configName" : data.configName,
//                            "configYear" : data.configYear,
//                            "configDate": date,
//                            "configType": "Holiday"
//                        }, function(json){
//                            if( json.type == "ERROR" ){error=json.message}
//                        }.bind(this),
//                        function(json){
//                            flag = false;
//                        }.bind(this),false);
//                }.bind(this))
//
//                if(data.makeUpClassDay!=""){
//                    data.makeUpClassDay.split(",").each(function( date ){
//                        this.app.restActions.saveHoliday({
//                            "configName" : data.configName,
//                            "configYear" : data.configYear,
//                            "configDate": this.dateFormat( new Date(date),"yyyy-MM-dd"),
//                            "configType": "Workday"
//                        }, function(json){
//                            if( json.type == "ERROR" ){error=json.message}
//                        },function(json){
//                            flag = false;
//                        }.bind(this),false);
//                    }.bind(this))
//                }
//                if(error==""){
//                    this.createMarkNode.destroy();
//                    this.createAreaNode.destroy();
//                    if(this.explorer.view)this.explorer.view.reload();
//                    this.app.notice( this.isNew ? this.app.lp.createSuccess : this.app.lp.updateSuccess  , "success");
//                }else{
//                    this.app.notice( error  , "error");
//                }
//            }.bind(this);
//            if(!this.isNew){
//                this.explorer.view._removeDocument(data, false, save )
//            }else{
//                save();
//            }
//    }
//});


MWF.xApplication.Attendance.HolidayExplorer.Holiday = new Class({
    Extends: MWF.widget.Common,
    options: {
        "width": "500",
        "height": "400"
    },
    initialize: function( explorer, data ){
        this.explorer = explorer;
        this.app = explorer.app;
        this.data = data || {};
        this.css = this.explorer.css;

        this.load();
    },
    load : function(){

    },
    open: function(e){
        this.isNew = false;
        this.isEdited = false;
        this._open();
    },
    create: function(){
        this.isNew = true;
        this._open();
    },
    edit: function(){
        this.isEdited = true;
        this._open();
    },
    _open : function(){
        this.createMarkNode = new Element("div", {
            "styles": this.css.createMarkNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.app.content, "after");

        this.createAreaNode = new Element("div", {
            "styles": this.css.createAreaNode
        });

        this.createNode();

        this.createAreaNode.inject(this.createMarkNode, "after");
        this.createAreaNode.fade("in");

        this.setCreateNodeSize();
        this.setCreateNodeSizeFun = this.setCreateNodeSize.bind(this);
        this.addEvent("resize", this.setCreateNodeSizeFun);
    },
    createNode: function(){
        var _self = this;
        this.createNode = new Element("div", {
            "styles": this.css.createNode
        }).inject(this.createAreaNode);


        this.createIconNode = new Element("div", {
            "styles": this.isNew ? this.css.createNewNode : this.css.createIconNode
        }).inject(this.createNode);


        this.createFormNode = new Element("div", {
            "styles": this.css.createFormNode
        }).inject(this.createNode);

        var lp = this.app.lp.holiday;

        var inputStyle = "width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC;height: 26px;";
        var inputTimeStyle = "width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC;height: 26px;"+
            "background : url(/x_component_Attendance/$HolidayExplorer/default/icon/calendar.png) 98% center no-repeat";

        var html = "<table width='100%' height='200' border='0' cellPadding='0' cellSpacing='0'>" +
            "<tr>"+
            "<td colspan='2' style='height: 50px; line-height: 50px; text-align: center; min-width: 80px; font-size:18px;font-weight: bold;'>" + lp.setHoliday + "</td>" +
            "</tr>" +
            "<tr>"+
            "<td style='height: 30px; line-height: 30px; text-align: left; min-width: 80px; width:25%'>" + lp.year + ":</td>" +
            "<td style='; text-align: left;' id='yearArea'>"+
            //(!this.isNew && !this.isEdited  ? "" :
            //    ("<input type='text' id='configYear' " + "style='" + inputStyle +"'" + " value='" + ( this.data && this.data.configYear ? this.data.configYear : "") + "'/>")) +
            "</td>"+
            "</tr>" +
            "<tr>"+
            "<td style='height: 30px; line-height: 30px; text-align: left'>"+lp.name+":</td>" +
            "<td style='; text-align: right;'>"+
            (!this.isNew && !this.isEdited  ? "" :
                ("<input type='text' id='configName' " + "style='" + inputStyle +"'" + " value='" + ( this.data && this.data.configName ? this.data.configName : "") + "'/>")) +
            "</td>" +
            "</tr>" +
            "<tr>" +
            "<td style='height: 30px; line-height: 30px;  text-align: left'>"+lp.startDate+":</td>" +
            "<td style='; text-align: right;'>" +
            (!this.isNew && !this.isEdited  ? "" :
                ("<input type='text' id='startDate' " + "style='" + inputTimeStyle +"'" + " value='" + ( this.data && this.data.startDate ? this.data.startDate : "") + "'/>")) +
            "</td>" +
            "</tr>" +
            "<tr>" +
            "<td style='height: 30px; line-height: 30px;  text-align: left'>"+lp.endDate+":</td>" +
            "<td style='; text-align: right;'>" +
            (!this.isNew && !this.isEdited  ? "" :
                ("<input type='text' id='endDate' " + "style='" + inputTimeStyle +"'" + " value='" + ( this.data && this.data.endDate ? this.data.endDate : "") + "'/>")) +
            "</td>" +
            "</tr>" +
            "<tr>" +
            "<td style='height: 30px; line-height: 30px;  text-align: left'>"+lp.makeUpClassDay+":</td>" +
            "<td style='; text-align: right;'>" +
            (!this.isNew && !this.isEdited  ? "" :
                ("<input type='text' id='makeUpClassDay' " + "style='" + inputTimeStyle +"'" + " value='" + ( this.data && this.data.makeUpClassDay ? this.data.makeUpClassDay : "") + "'/>")) +
            "</td>" +
            "</tr>" +
            "</table>";
        this.createFormNode.set("html", html);

        //this.configYear = this.createFormNode.getElement("#configYear");
        this.yearArea = this.createFormNode.getElement("#yearArea");
        this.configYear = new MDomItem( this.yearArea, {
            "name" : "configYear",
            "type" : "select",
            "value" : this.data.configYear || new Date().getFullYear(),
            "selectValue" : function(){
                var years = [];
                var year = new Date().getFullYear()+5;
                for(var i=0; i<11; i++ ){
                    years.push( year-- );
                }
                return years;
            }
        }, true, this.app );
        this.configYear.load();

        this.configName = this.createFormNode.getElement("#configName");
        this.startDate = this.createFormNode.getElement("#startDate");
        this.endDate = this.createFormNode.getElement("#endDate");
        this.makeUpClassDay = this.createFormNode.getElement("#makeUpClassDay");

        this.startDate.addEvent("click",function(){
            _self.selectCalendar(this);
        });
        this.endDate.addEvent("click",function(){
            _self.selectCalendar(this);
        });
        this.makeUpClassDay.addEvent("click",function(){
            _self.selectCalendar(this);
        });

        this.cancelActionNode = new Element("div", {
            "styles": this.css.createCancelActionNode,
            "text": this.app.lp.cancel
        }).inject(this.createFormNode);
        this.createOkActionNode = new Element("div", {
            "styles": this.css.createOkActionNode,
            "text": this.app.lp.ok
        }).inject(this.createFormNode);

        this.cancelActionNode.addEvent("click", function(e){
            this.cancelCreate(e);
        }.bind(this));
        this.createOkActionNode.addEvent("click", function(e){
            this.okCreate(e);
        }.bind(this));
    },
    setCreateNodeSize: function (width, height, top, left) {
        if (!width)width = this.options && this.options.width ? this.options.width : "50%";
        if (!height)height = this.options && this.options.height ? this.options.height : "50%";
        if (!top) top = this.options && this.options.top ? this.options.top : 0;
        if (!left) left = this.options && this.options.left ? this.options.left : 0;

        var allSize = this.app.content.getSize();
        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
        300 > width && (width = 300);
        220 > height && (height = 220);
        top = top || parseInt((limitHeight - height) / 2, 10);
        left = left || parseInt((limitWidth - width) / 2, 10);

        this.createAreaNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px",
            "top": "" + top + "px",
            "left": "" + left + "px"
        });

        this.createNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px"
        });

        var iconSize = this.createIconNode ? this.createIconNode.getSize() : {x: 0, y: 0};
        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};

        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y;
        //var formMargin = formHeight -iconSize.y;
        this.createFormNode.setStyles({
            "height": "" + contentHeight + "px"
        });
    },
    //setCreateNodeSize: function(){
    //    var size = this.app.node.getSize();
    //    var allSize = this.app.content.getSize();
    //
    //    this.createAreaNode.setStyles({
    //        "width": ""+size.x+"px",
    //        "height": ""+size.y+"px"
    //    });
    //    var hY = size.y*0.8;
    //    var mY = size.y*0.2/2;
    //    this.createNode.setStyles({
    //        "height": ""+hY+"px",
    //        "margin-top": ""+mY+"px"
    //    });
    //
    //    var iconSize = this.createIconNode.getSize();
    //    var formHeight = hY*0.7;
    //    if (formHeight>250) formHeight = 250;
    //    var formMargin = hY*0.3/2-iconSize.y;
    //    this.createFormNode.setStyles({
    //        "height": ""+formHeight+"px",
    //        "margin-top": ""+formMargin+"px"
    //    });
    //},
    cancelCreate: function(e){
        var _self = this;
        if ( this.isNew &&  this.configName.get("value") ){
            this.app.confirm("warn", e,
                this.app.lp.create_cancel_title,
                this.app.lp.create_cancel, "320px", "100px",
                function(){
                    _self.createMarkNode.destroy();
                    _self.createAreaNode.destroy();
                    this.close();
                },function(){
                    this.close();
                }
            );
        }else{
            this.createMarkNode.destroy();
            this.createAreaNode.destroy();
            delete _self;
        }
    },
    okCreate: function(e){

        var data = {
            "id" : (this.data && this.data.id) ? this.data.id : this.app.restActions.getUUID(),
            "configYear": this.configYear.get("value"),
            "configName": this.configName.get("value"),
            "startDate": this.startDate.get("value"),
            "endDate": this.endDate.get("value"),
            "makeUpClassDay": this.makeUpClassDay.get("value")
        };
        if (data.configYear && data.configName && data.startDate && data.endDate ){
            var endDate = new Date( data.endDate );
            var startDate = new Date( data.startDate  );
            if( startDate > endDate ){
                this.app.notice("开始日期不能大于结束日期","error");
                return;
            }

            var save = function(){
                var error = "";
                this.getDateByRange(startDate,endDate).each(function( date ){
                    this.app.restActions.saveHoliday({
                        "configName" : data.configName,
                        "configYear" : data.configYear,
                        "configDate": date,
                        "configType": "Holiday"
                    }, function(json){
                        if( json.type == "ERROR" ){error=json.message}
                    }.bind(this),
                        function(json){
                        flag = false;
                    }.bind(this),false);
                }.bind(this));

                if(data.makeUpClassDay!=""){
                    data.makeUpClassDay.split(",").each(function( date ){
                        this.app.restActions.saveHoliday({
                            "configName" : data.configName,
                            "configYear" : data.configYear,
                            "configDate": this.dateFormat( new Date(date),"yyyy-MM-dd"),
                            "configType": "Workday"
                        }, function(json){
                            if( json.type == "ERROR" ){error=json.message}
                        },function(json){
                            flag = false;
                        }.bind(this),false);
                    }.bind(this))
                }
                if(error==""){
                    this.createMarkNode.destroy();
                    this.createAreaNode.destroy();
                    if(this.explorer.view)this.explorer.view.reload();
                    this.app.notice( this.isNew ? this.app.lp.createSuccess : this.app.lp.updateSuccess  , "success");
                }else{
                    this.app.notice( error  , "error");
                }
            }.bind(this);
            if(!this.isNew){
                this.explorer.view._removeDocument(data, false, save )
            }else{
                save();
            }
        }else{
            this.configName.setStyle("border-color", "red");
            this.configName.focus();
            this.app.notice( this.app.lp.holiday.inputValid, "error");
        }
    },
    selectCalendar : function( calendarNode ){
        MWF.require("MWF.widget.Calendar", function(){
            var calendar = new MWF.widget.Calendar( calendarNode, {
                "style": "xform",
                "isTime": false,
                "target": this.app.content
            });
            calendar.show();
        }.bind(this));
    },
    getDateByRange : function(startDate, endDate){
        var days = [];
        while (startDate <= endDate) {
            days.push( this.dateFormat(startDate,"yyyy-MM-dd") );
            startDate.setDate(startDate.getDate() + 1);
        }
        return days;
    },
    dateFormat : function(date, fmt){
        var o = {
            "M+" : date.getMonth()+1,                 //月份
            "d+" : date.getDate(),                    //日
            "h+" : date.getHours(),                   //小时
            "m+" : date.getMinutes(),                 //分
            "s+" : date.getSeconds(),                 //秒
            "q+" : Math.floor((date.getMonth()+3)/3), //季度
            "S"  : date.getMilliseconds()             //毫秒
        };
        if(/(y+)/.test(fmt))
            fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));
        for(var k in o)
            if(new RegExp("("+ k +")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        return fmt;
    }
});

