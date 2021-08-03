MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xApplication.Attendance.HolidayExplorer = new Class({
    Extends: MWF.xApplication.Attendance.Explorer,
    Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "../x_component_Attendance/$HolidayExplorer/";
        this.cssPath = "../x_component_Attendance/$HolidayExplorer/"+this.options.style+"/css.wcss";
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
                // holidy.makeUpClassDay = holidy.makeUpClassDay.join(",");
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


MWF.xApplication.Attendance.HolidayExplorer.Holiday = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.PopupForm,
    options : {
        "width": 500,
        "height": 600,
        "hasTop" : true,
        "hasBottom" : true,
        "title" : MWF.xApplication.Attendance.LP.holiday.setHoliday,
        "draggable" : true,
        "closeAction" : true,
    },
    _loadFormCss: function(cssPath){
        var css;
        var key = encodeURIComponent(cssPath);
        if (o2.widget.css[key]){
            css = o2.widget.css[key];
        }else{
            var r = new Request.JSON({
                url: o2.filterUrl(cssPath),
                secure: false,
                async: false,
                method: "get",
                noCache: false,
                onSuccess: function(responseJSON, responseText){
                    css = responseJSON;
                    o2.widget.css[key] = responseJSON;
                }.bind(this),
                onError: function(text, error){
                    alert(error + text);
                }
            });
            r.send();
        }
        return css;
    },
    _createTableContent: function(){
        var _self = this;

        var lp = this.app.lp.holiday;

        var css = this._loadFormCss("../x_component_Template/$MForm/attendance/css.wcss");

        debugger;

        var inputStyle = "";
        for( var key in css.inputText ){
            inputStyle += key+":"+ css.inputText[key] + ";"
        }

        var inputTimeStyle = "";
        for( var key in css.inputTime ){
            inputTimeStyle += key+":"+ css.inputTime[key] + ";"
        }

        var makeupClassArea = "";
        if( this.data && this.data.makeUpClassDay ){
            this.data.makeUpClassDay.each( function ( d, idx ) {
                makeupClassArea += "<input type='text' class='makeUpClassDay' " +
                    "style='" + inputTimeStyle +"'" +
                    " value='" + (  d || "" ) + "'/>";
                if( idx > 0 )makeupClassArea += "<div class='removeMakeUpClassDay' style='color: #354f67;padding-bottom: 5px;cursor: pointer;'>"+this.app.lp.delete+"</div>";
            }.bind(this))
        }else{
            makeupClassArea += "<input type='text' class='makeUpClassDay' " +
            "style='" + inputTimeStyle +"'" +
            " value='" + (  "" ) + "'/>";
        }

        var html = "<table width='100%' height='200' border='0' cellPadding='3' cellSpacing='0'>" +
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
            "<td valign='top' style='height: 30px; line-height: 30px;  text-align: left'>"+lp.makeUpClassDay+":</td>" +
            "<td style='; text-align: right;' id='makeUpClassDayTd'>" +
            (!this.isNew && !this.isEdited  ? "" :makeupClassArea )+
            (!this.isNew && !this.isEdited  ? "" : "<div id='addMakeupClass' style='color: #354f67;cursor: pointer;'>"+lp.addMakeUpClassDay+"</div>" )+
            "</td>" +
            "</tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        //this.configYear = this.formNode.getElement("#configYear");
        this.yearArea = this.formNode.getElement("#yearArea");
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
        }, true, this.app, css );
        this.configYear.load();

        this.configName = this.formNode.getElement("#configName");
        this.startDate = this.formNode.getElement("#startDate");
        this.endDate = this.formNode.getElement("#endDate");
        this.makeUpClassDay = this.formNode.getElements(".makeUpClassDay");
        this.removeMakeUpClassDay = this.formNode.getElements(".removeMakeUpClassDay");
        this.addMakeupClass = this.formNode.getElement("#addMakeupClass");
        this.makeUpClassDayTd = this.formNode.getElement("#makeUpClassDayTd");


        this.startDate.addEvent("click",function(){
            _self.selectCalendar(this);
        });
        this.endDate.addEvent("click",function(){
            _self.selectCalendar(this);
        });
        this.makeUpClassDay.addEvent("click",function(){
            _self.selectCalendar(this);
        });
        this.removeMakeUpClassDay.addEvent("click", function () {
            var input = this.getPrevious();
            if(input && input.get('tag') === "input")input.destroy();
            this.destroy();
        });
        this.addMakeupClass.addEvent("click",function(){
            var input = new Element("input",{
                class : "makeUpClassDay",
                style : inputTimeStyle,
                value : "",
                events : {
                    click : function () {
                        _self.selectCalendar(this);
                    }
                }
            }).inject(this, "before");

            var div = new Element("div",{
                "class" : "removeMakeUpClassDay",
                style : "color: #354f67;padding-bottom: 5px;cursor: pointer;",
                text : this.app.lp.delete,
                events : {
                    click : function () {
                        var input = this.getPrevious();
                        if(input && input.get('tag') === "input")input.destroy();
                        this.destroy();
                    }
                }
            }).inject(this, "before")
        });

        // this.cancelActionNode = new Element("div", {
        //     "styles": this.css.createCancelActionNode,
        //     "text": this.app.lp.cancel
        // }).inject(this.formNode);
        // this.createOkActionNode = new Element("div", {
        //     "styles": this.css.createOkActionNode,
        //     "text": this.app.lp.ok
        // }).inject(this.formNode);
        //
        // this.cancelActionNode.addEvent("click", function(e){
        //     this.cancelCreate(e);
        // }.bind(this));
        // this.createOkActionNode.addEvent("click", function(e){
        //     this.okCreate(e);
        // }.bind(this));
    },
    ok: function(e){

        var data = {
            "id" : (this.data && this.data.id) ? this.data.id : this.app.restActions.getUUID(),
            "configYear": this.configYear.get("value"),
            "configName": this.configName.get("value"),
            "startDate": this.startDate.get("value"),
            "endDate": this.endDate.get("value")
            // "makeUpClassDay": this.makeUpClassDay.get("value")
        };
        if (data.configYear && data.configName && data.startDate && data.endDate ){
            var endDate = new Date( data.endDate );
            var startDate = new Date( data.startDate  );
            if( startDate > endDate ){
                this.app.notice( this.app.lp.holiday.beginGreateThanEndNotice ,"error");
                return;
            }

            var flag = true;
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

                this.formNode.getElements(".makeUpClassDay").each( function(el){
                    if(el.get("value")){
                        this.app.restActions.saveHoliday({
                            "configName" : data.configName,
                            "configYear" : data.configYear,
                            "configDate": this.dateFormat( new Date(el.get("value")),"yyyy-MM-dd"),
                            "configType": "Workday"
                        }, function(json){
                            if( json.type == "ERROR" ){error=json.message}
                        },function(json){
                            flag = false;
                        }.bind(this),false);
                    }
                }.bind(this))

                if(error==""){
                    if( this.formMaskNode )this.formMaskNode.destroy();
                    if( this.formAreaNode )this.formAreaNode.destroy();
                    if (this.explorer && this.explorer.view)this.explorer.view.reload();
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

