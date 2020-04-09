MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.require("MWF.widget.AttachmentController",null,false);

var MWFCalendar = MWF.xApplication.Calendar = MWF.xApplication.Calendar || {};

MWFCalendar.ColorOptions = {
    deep : ["#428ffc","#5bcc61","#f9bf24","#f75f59","#f180f7","#9072f1","#909090","#1462be"],
    light : ["#cae2ff","#d0f1b0","#fef4bb","#fdd9d9","#f4c5f7","#d6ccf9","#e7e7e7","#cae2ff"],
    getLightColor : function( deepColor ){
        var index = this.deep.indexOf(deepColor);
        return index > -1 ? this.light[index] : this.light[0];
    },
    getDeepColor : function( lightColor ){
        var index = this.light.indexOf(lightColor);
        return index > -1 ? this.deep[index] : this.deep[0];
    }
};

MWFCalendar.EventForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "meeting",
        "width": "800",
        "height": "475",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "closeAction": true,
        "isFull" : false,
        "startTime" : null,
        "endTime" : null,
        "isWholeday" : false,
        "defaultCalendarId" : ""
    },
    open: function (e) {
        if( this.options.isFull ){
            this.options.width = "800";
            this.options.height = "630";
        }
        this.fireEvent("queryOpen");
        this.isNew = false;
        this.isEdited = false;
        this._open();
        this.fireEvent("postOpen");
    },
    create: function () {
        if( this.options.isFull ){
            this.options.width = "1100";
            this.options.height = "630";
        }
        this.fireEvent("queryCreate");
        this.isNew = true;
        this._open();
        this.fireEvent("postCreate");
    },
    edit: function () {
        if( this.options.isFull ){
            this.options.width = "1100";
            this.options.height = "630";
        }
        this.fireEvent("queryEdit");
        this.isEdited = true;
        this._open();
        this.fireEvent("postEdit");
    },
    _createTableContent : function(){
        this.getEventData( function(){
            this.loadEventContent()
        }.bind(this))
    },
    loadEventContent : function(){
        var path = "/o2_lib/rrule/";
        COMMON.AjaxModule.load(path+"rrule.js", function () {
            this.app.actions.listMyCalendar( function( json ){
                this.calendarIds = [];
                this.calendarNames = [];
                this.calendarList = [];
                (json.data.myCalendars || []).each( function(c){
                    if(c.publishable || c.manageable )this.calendarList.push(c)
                }.bind(this));
                ( json.data.unitCalendars || [] ).each( function(c){
                    if(c.publishable || c.manageable )this.calendarList.push(c)
                }.bind(this));
                this.calendarList.each( function( d ){
                    this.calendarIds.push(d.id);
                    this.calendarNames.push(d.name);
                }.bind(this));


                if( this.isEdited || this.isNew ){
                    this._createTableContent_Edit();
                }else{
                    this._createTableContent_Read();
                }
            }.bind(this))
        }.bind(this));
    },
    _createTableContent_Read : function(){
        this.formTopTextNode.set( "text", this.lp.readEvent );
        this.formTableContainer.setStyle("width","86%");

        var data = this.data;

        var beginD = Date.parse(data.startTime);
        var endD = Date.parse(data.endTime);
        var begin = beginD.format(this.lp.dateFormatAll) + "（" + this.lp.weeks.arr[beginD.get("day")] + "）";
        var end = endD.format(this.lp.dateFormatAll) + "（" + this.lp.weeks.arr[endD.get("day")] + "）";

        if( data.recurrenceRule  ){
            this.oldRecurrenceRule = data.recurrenceRule;
            this.rRule = RRule.fromString(data.recurrenceRule).origOptions;
        }else{
            this.rRule = {};
        }
        var text = ["不","每天","每周","每月（当日）","每年（当日）"];
        var value = ["NONE",RRule["DAILY"],RRule["WEEKLY"],RRule["MONTHLY"],RRule["YEARLY"] ];
        var repeat;
        if( this.rRule.freq ){
            repeat = text[ value.indexOf( this.rRule.freq ) ];
            if( this.rRule.byweekday ){
                var repeatWeeks = this.rRule.byweekday.toString().split(",");
                var repeatWeekTextList = [];
                var weekArr = this.lp.weeks.arr;
                var rruleArr = this.lp.weeks.rruleArr;
                repeatWeeks.each( function(r){
                    repeatWeekTextList.push( weekArr[ rruleArr.indexOf( r ) ] );
                });
                repeat = "每"+ repeatWeekTextList.join("、")+"重复";
            }else{
                repeat = repeat+"重复";
            }
            if( this.rRule.until && repeat!="不重复" ){
                repeat += "  结束日期："+this.rRule.until.format("%Y-%m-%d");
            }
        }else{
            repeat = "不重复"
        }

        var remind;
        if( data.valarmTime_config ){ //天、时、分、秒
            var valarmTime_configList = data.valarmTime_config.split(",");
            valarmTime_configList.each( function( v, i ){
                var unit;
                if( i == 0 ){
                    unit = "天"
                }else if( i == 1 ){
                    unit = "小时";
                }else if( i==2 ){
                    unit = "分钟"
                }else{
                    unit = "秒"
                }
                if( v && v!="0" ){
                    remind = "提前"+ Math.abs(v)+unit+"提醒";
                }
            }.bind(this))
        }

        var calendarName =  this.calendarNames[ this.calendarIds.indexOf( data.calendarId ) ];

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='table-layout:fixed;'>" +
            //"<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +
        "<tr><td styles='formTableTitle' width='40'>日历：</td>" +
        "    <td styles='formTableValue' width='400'>"+calendarName+"</td>" +
        "</tr>" +
        "<tr><td styles='formTableTitle'>标题：</td>" +
        "    <td styles='formTableValue'><div styles='colorItem'></div>"+ data.title +"</td></tr>" +
        "<tr><td styles='formTableTitle'>开始：</td>" +
        "    <td styles='formTableValue'>"+ begin +"</td>" +
        "</tr>" +
        "<tr><td styles='formTableTitle'>结束：</td>" +
        "    <td styles='formTableValue'>"+end+"</td>" +
        "</tr>"+
        "<tr><td styles='formTableTitle'>地点：</td>" +
        "    <td styles='formTableValue'>"+( data.locationName || "" )+"</td>" +
        "</tr>";
        if( remind ){
            html += "<tr><td styles='formTableTitle'>提醒：</td>" +
                "    <td styles='formTableValue'>"+remind+"</td>" +
                "</tr>";
        }
        if( repeat && repeat!="不" && repeat!="不重复" ){
            "<tr><td styles='formTableTitle'>重复：</td>" +
            "    <td styles='formTableValue'>"+ repeat +"</td>" +
            "</tr>";
        }
        if( data.comment ){
            html += "<tr><td styles='formTableTitle'>内容：</td>" +
                "    <td styles='formTableValue'>"+data.comment+"</td>" +
                "</tr>";
        }
        this.formTableArea.set("html", html );
        this.formTableArea.getElements("[styles='formTableTitle']").setStyles({
            "color" : "#333",
            "font-size": "16px",
            "padding": "0px 20px 0px 0px",
            "height" : "35px",
            "line-height" : "35px",
            "text-align": "left"
        });
        this.formTableArea.getElements("[styles='formTableValue']").setStyles({
            "text-align": "left",
            "font-size": "16px"
        });
        var colorItem = this.formTableArea.getElement("[styles='colorItem']");
        var div = new Element("div", {
            styles : this.css.colorNode
        }).inject( colorItem );
        div.setStyle("background-color",this.data.color);
    },
    _createTableContent_Edit: function () {
        this.oldCoordinate = null;
        var editEnable = this.editEnable = ( this.isEdited || this.isNew );
        this.userName = layout.desktop.session.user.distinguishedName;
        this.userId = layout.desktop.session.user.id;

        if( this.options.isFull ){
            this.formTableContainer.setStyles({
                "width" : "auto",
                "padding-left" : "40px"
            });
        }else{
            this.formTableContainer.setStyle("width","80%");
        }

        if( this.isNew ){
            this.formTopTextNode.set( "text", this.lp.addEvent );
        }else if( this.isEdited ){
            this.formTopTextNode.set( "text", this.lp.editEvent );
            this.options.height = "590";
        }

        var startTime, endTime, defaultStartDate, defaultStartTime, defaultEndDate, defaultEndTime;
        if( this.options.startTime && this.options.endTime ){
            startTime= this.date = typeOf( this.options.startTime )=="string" ? Date.parse( this.options.startTime ) : this.options.startTime;
            endTime= typeOf( this.options.endTime )=="string" ? Date.parse( this.options.endTime ) : this.options.endTime;
            defaultStartDate = startTime.format("%Y-%m-%d");
            defaultStartTime = startTime.format("%H:%M");
            defaultEndDate = endTime.format("%Y-%m-%d");
            defaultEndTime = endTime.format("%H:%M");
        }else{
            startTime = this.date = new Date().increment("hour",1);
            endTime = startTime.clone().increment("hour",1);
            defaultStartDate = startTime.format("%Y-%m-%d");
            defaultStartTime = startTime.format("%H") + ":00";
            defaultEndDate = endTime.format("%Y-%m-%d");
            defaultEndTime = endTime.format("%H") + ":00";
        }

        var data = this.data;

        if( this.options.isWholeday && this.isNew ){
            data.isAllDayEvent = true;
        }

        if( data.startTime ){
            var beignDate = Date.parse( data.startTime  );
            data.startDateInput = beignDate.format("%Y-%m-%d");
            data.startTimeInput = this.getString( beignDate.getHours() ) + ":" + this.getString( beignDate.getMinutes() );
        }
        if( data.endTime ){
            var endDate = Date.parse( data.endTime  );
            data.endDateInput = endDate.format("%Y-%m-%d");
            data.endTimeInput = this.getString( endDate.getHours() ) + ":" + this.getString( endDate.getMinutes() );
        }

        //data.rRuleString = "FREQ=WEEKLY;DTSTART=20180523T090000Z;UNTIL=20180523T160000Z;BYDAY=WE,TH,FR";
        if( data.recurrenceRule  ){
            this.oldRecurrenceRule = data.recurrenceRule;
            this.rRule = RRule.fromString(data.recurrenceRule).origOptions;
        }else{
            this.rRule = {};
        }
        data.repeat = this.rRule.freq || "";

        if( this.rRule.until ){
            data.repeatUntilAvailable = "AVAILABLE";
            data.repeatUntilDate = this.rRule.until.format("%Y-%m-%d");
        }
        if( this.rRule.byweekday ){
            data.repeatWeeks = this.rRule.byweekday.toString().split(",");
        }

        if( data.valarmTime_config ){ //天、时、分、秒
            var valarmTime_configList = data.valarmTime_config.split(",");
            valarmTime_configList.each( function( v, i ){
                var unit;
                if( i == 0 ){
                    unit = "d"
                }else if( i == 1 ){
                    unit = "h";
                }else if( i==2 ){
                    unit = "m"
                }else{
                    unit = "s"
                }
                if( v && v!="0" ){
                    data.remind = v+"_"+unit;
                }
            }.bind(this))
        }

        this.formTableArea.set("html", this.getHtml());

        this.colorItem = this.formTableArea.getElement("[item='color']");


        //this.attachmentTr = this.formTableArea.getElement("[item='attachmentTr']");
        //this.attachmentArea = this.formTableArea.getElement("[item='attachment']");

        if( !this.data.color ){
            if( this.options.defaultCalendarId ){
                this.data.color = this.getColorByCalendarId( this.options.defaultCalendarId );
            }else{
                this.data.color = this.calendarList[0].color;
            }
        }

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, data, {
                isEdited: this.isEdited || this.isNew,
                style : "meeting",
                hasColon : true,
                itemTemplate: {
                    calendarId : { text : this.lp.calendar, defaultValue : this.options.defaultCalendarId, type : "select", selectValue : this.calendarIds, selectText : this.calendarNames, event : {
                        change : function( item ){
                            this.setColorByCalendarId( item.getValue() )
                        }.bind(this)
                    }},
                    startDateInput: { text : this.lp.beginTime ,tType: "date", defaultValue: defaultStartDate , notEmpty : true },
                    startTimeInput: { tType: "time",
                        defaultValue: defaultStartTime, className : ( (this.isNew || this.isEdited ) ?  "inputTimeUnformatWidth" : "" ),
                        disable : data.isAllDayEvent
                    },
                    endDateInput: { text : this.lp.endTime, tType: "date",  defaultValue: defaultEndDate, notEmpty : true  },
                    endTimeInput: { tType: "time",
                        defaultValue: defaultEndTime, className : ( (this.isNew || this.isEdited ) ?  "inputTimeUnformatWidth" : "" ),
                        disable : data.isAllDayEvent
                    },
                    remind : { text : this.lp.remind, type : "select", selectText : ["不提醒","开始时","提前5分钟","提前10分钟","提前15分钟","提前30分钟","提前1小时","提前2小时"],
                        selectValue : ["", "-5_s","-5_m","-10_m","-15_m","-30_m","-1_h","-2_h"] },
                    isAllDayEvent : { type : "checkbox", selectValue : ["true"], selectText : ["全天"], event : {
                        change : function(item ){
                            var itemStart = item.form.getItem("startTimeInput");
                            var itemEnd = item.form.getItem("endTimeInput");
                            if( item.getValue() == "true" && !itemStart.options.disable && !itemEnd.options.disable ){
                                itemStart.getElements().setStyle("display","none");
                                itemEnd.getElements().setStyle("display","none")
                            }else{
                                if( itemStart.options.disable )itemStart.enable();
                                if( itemEnd.options.disable )itemEnd.enable();
                                itemStart.getElements().setStyle("display","");
                                itemEnd.getElements().setStyle("display","")
                            }
                        }.bind(this)
                    } },
                    title: { text : this.lp.eventSubject, notEmpty : true },
                    description: {type: "textarea"},
                    locationName : { text : "地点" },
                    repeat : { text : this.lp.repeat, type : "select", defaultValue : "NONE",
                        selectText : ["不重复","每天","每周","每月（当日）","每年（当日）"],
                        selectValue : ["NONE",RRule["DAILY"],RRule["WEEKLY"],RRule["MONTHLY"],RRule["YEARLY"] ], event : {
                        change : function(item){
                            var val = item.getValue();
                            var area = this.formTableArea.getElement("[item='repeatUntilArea']");
                            area.setStyle("display", val == "NONE" ? "none" : "");
                            if( val == RRule["WEEKLY"] ){
                                this.showWeek()
                            }else{
                                area = this.formTableArea.getElement("[item='repeatWeekArea']");
                                area.setStyle("display","none");
                            }
                        }.bind(this)
                    }},
                    repeatUntilAvailable : { text : this.lp.repeatUntilAvailable, type : "radio",
                        selectText : ["永不", "结束日期"],
                        selectValue : ["NONE", "AVAILABLE"],
                        defaultValue : "NONE"
                    },
                    repeatUntilDate : { tType : "date", event : {
                        click : function(){ this.form.getItem("repeatUntilAvailable").setValue("AVAILABLE") }.bind(this)
                    }},
                    comment : { text : this.lp.content, type : "rtf", RTFConfig : {
                        skin : "bootstrapck",
                        "resize_enabled": false,
                        toolbar : [
                            { name: 'document', items : [ 'Preview' ] },
                            //{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
                            { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','-','RemoveFormat' ] },
                            //{ name: 'paragraph', items : [ 'JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ] },
                            { name: 'list', items : [ 'NumberedList','BulletedList'] },
                            //{ name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
                            //{ name: 'colors', items : [ 'TextColor','BGColor' ] },
                            { name: 'links', items : [ 'Link','Unlink' ] }
                            //{ name: 'insert', items : [ 'Image' ] },
                            //{ name: 'tools', items : [ 'Maximize','-','About' ] }
                        ]
                    }
                }}
            }, this.app);
            this.form.load();

            if( this.data.repeat == RRule["WEEKLY"] ){
                this.showWeek()
            }

            this.loadColor();

            //if( this.data.id )
            //    this.loadAttachment();
        }.bind(this), true);
    },
    getRRuleString : function( data ){
        if( !data )data = this.form.getResult(false,null,false,false,false);
        if( !data.repeat || data.repeat == "NONE"  )return "";
        //var startDate;
        //if( data.isAllDayEvent == "true" ){
        //    startDate = Date.parse( data.startDateInput )
        //}else{
        //    startDate = Date.parse( data.startDateInput + " " + data.startTimeInput )
        //}
        var options = {
            freq : data.repeat //RRule[data.repeat],
            //dtstart :  startDate
        };
        if( data.repeatUntilAvailable != "NONE" && data.repeatUntilDate!="" ){
            options.until = Date.parse( data.repeatUntilDate )
        }
        if( data.repeat == RRule["WEEKLY"] ){
            options.byweekday = [];
            this.getSelectWeek().each( function( w ){
                options.byweekday.push( RRule[w] )
            })
        }
        var rule = new RRule( options );
        return rule.toString();
    },
    getHtml : function(){
        var boxStyle = (this.isEdited || this.isNew) ? "border:1px solid #ccc; border-radius: 4px;overflow: hidden;padding:8px;" : "";
      if( this.options.isFull ){
          var html =  "<div style='overflow: hidden;'>" +
              "<div item='baseInforContainer' style='float: left; width : 500px;'>" +
              "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='table-layout:fixed;'>" +
                  //"<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +
              "<tr><td styles='formTableTitle' width='80' lable='calendarId'></td>" +
              "    <td styles='formTableValue' item='calendarId' colspan='2' width='400'></td>" +
              "</tr>" +
              "<tr><td styles='formTableTitle' lable='title'></td>" +
              "    <td styles='formTableValue' item='title'  colspan='2'></td></tr>" +
              "<tr><td styles='formTableTitle' lable='startDateInput'></td>" +
              "    <td styles='formTableValue' item='startDateInput' width='" + ( this.editEnable ? "260" : "100" ) + "'></td>" +
              "    <td styles='formTableValue' item='startTimeInput'></td>" +
              "</tr>" +
              "<tr><td styles='formTableTitle' lable='endDateInput'></td>" +
              "    <td styles='formTableValue' item='endDateInput'></td>" +
              "    <td styles='formTableValue' item='endTimeInput'></td>" +
              "</tr>"+
              "<tr><td styles='formTableTitle'></td>" +
              "    <td styles='formTableValue' item='isAllDayEvent' colspan='2'></td>" +
              "</tr>"+
              "<tr><td styles='formTableTitle' lable='locationName'></td>" +
              "    <td styles='formTableValue' item='locationName' colspan='2'></td>" +
              "</tr>" +
              "<tr><td styles='formTableTitle' lable='remind'></td>" +
              "    <td styles='formTableValue' item='remind' colspan='2'></td>" +
              "</tr>" +
              "<tr><td styles='formTableTitle' lable='repeat'></td>" +
              "    <td styles='formTableValue' item='repeat' colspan='2'></td>" +
              "</tr>" +
              "<tr item='repeatWeekArea' style='display:"+ ( this.data.repeat == RRule["WEEKLY"] ? "" : "none") +";'><td styles='formTableTitle'></td>" +
              "    <td styles='formTableValue' item='repeatWeek' colspan='2' style='overflow:hidden;'></td>" +
              "</tr>" +
              "<tr item='repeatUntilArea' style='display:"+ ( (!this.data.repeat || this.data.repeat == "") ? "none" : "") +";'><td styles='formTableTitle'></td>" +
              "    <td styles='formTableValue' colspan='2' style='overflow: hidden;line-height:34px;'>" +
              "        <div lable='repeatUntilAvailable' style='float: left;'></div>"+
              "        <div item='repeatUntilAvailable'  style='float: left;'></div>"+
              "        <div item='repeatUntilDate'  style='float: left;width:170px;'></div>"+
              "    </td>" +
              "</tr>" +
              "<tr><td styles='formTableTitle'>"+this.lp.color+"：</td>" +
              "    <td styles='formTableValue' item='color' colspan='2' style='overflow: hidden;'></td>" +
              "</tr>" +
              "</table>"+
              "</div>" +
              "<div style='float: left; width : 500px;' item='commentContainer'>" +
              "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
                  //"<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +
                  "<tr><td styles='formTableTitle' ></td>" +
                  "    <td styles='formTableValue' lable='comment' colspan='2'></td>" +
                  "</tr>" +
              "<tr><td styles='formTableTitle' ></td>" +
              "    <td styles='formTableValue' item='comment' colspan='2'></td>" +
              "</tr>" +
                  //"<tr><td styles='formTableTitle'>"+this.lp.eventDescription+":</td>" +
                  //"    <td styles='formTableValue' item='description'  colspan='2'></td></tr>" +
              "<tr item='attachmentTr'><td styles='formTableTitle'></td>" +
              "    <td styles='formTableValue' item='attachment'></td></tr>" +
              "</table>"+
              "</div>" +
              "</div>";
          return html;
      }else{
          return  "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
                  //"<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +
              "<tr><td styles='formTableTitle' lable='calendarId'></td>" +
              "    <td styles='formTableValue' item='calendarId'  colspan='2'></td></tr>" +
              "<tr><td styles='formTableTitle' lable='title'></td>" +
              "    <td styles='formTableValue' item='title'  colspan='2'></td></tr>" +
              "<tr><td styles='formTableTitle' width='100' lable='startDateInput'></td>" +
              "    <td styles='formTableValue' item='startDateInput' width='300'></td>" +
              "    <td styles='formTableValue' item='startTimeInput'></td>" +
              "</tr>" +
              "<tr><td styles='formTableTitle' lable='endDateInput'></td>" +
              "    <td styles='formTableValue' item='endDateInput'></td>" +
              "    <td styles='formTableValue' item='endTimeInput'></td>" +
              "</tr>" +
              "<tr><td styles='formTableTitle'></td>" +
              "    <td styles='formTableValue' item='isAllDayEvent' colspan='2'></td>" +
              "</tr>" +
              "<tr><td styles='formTableTitle' lable='remind'></td>" +
              "    <td styles='formTableValue' item='remind' colspan='2'></td>" +
              "</tr>" +
              "</table>";
      }
    },
    _setNodesSize : function(width, height, formContentHeight, formTableHeight ){
        if( this.options.isFull ){
            var baseInforContainer = this.formAreaNode.getElement("[item='baseInforContainer']");
            var commentContainer = this.formAreaNode.getElement("[item='commentContainer']");
            if(baseInforContainer)baseInforContainer.setStyle("width", (width-100) / 2);
            if(commentContainer)commentContainer.setStyle("width", (width-100) / 2)
        }
    },
    getColorByCalendarId : function( calendarId ){
        var color;
        this.calendarList.each( function( d ){
            if( d.id == calendarId ){
                color = d.color;
            }
        }.bind(this));
        return color;
    },
    setColorByCalendarId : function( calendarId ){
        var color = this.getColorByCalendarId( calendarId );
        if( color ){
            this.data.color = color;
            this.setColor(color);
        }
    },
    setColor : function( color ){
        (this.colorOptions || []).each( function( div ){
            if( div.retrieve("color") == color ){
                div.click();
            }
        })
    },
    loadColor : function(){
        if( this.isEdited || this.isNew ){
            this.loadColor_Edited();
        }else{
            this.loadColor_Read();
        }
    },
    loadColor_Read : function(){
        if( !this.colorItem )return;
        var div = new Element("div", {
            styles : this.css.colorNode
        }).inject( this.colorItem );
        div.setStyle("background-color",this.data.color);
        div.setStyles( this.css.colorNode_current );
    },
    loadColor_Edited : function(){
        if( !this.colorItem )return;
        this.colorOptions = [];
        if( !this.data.color ){
            if( this.options.defaultCalendarId ){
                this.getColorByCalendarId( this.options.defaultCalendarId );
            }else{
                this.data.color = this.calendarList[0].color;
            }
        }
        MWFCalendar.ColorOptions.deep.each( function( color , i){
            var div = new Element("div", {
                styles : this.css.colorNode,
                events : {
                    click : function(ev){
                        if(this.currentColorNode)this.currentColorNode.setStyles( this.css.colorNode );
                        ev.target.setStyles( this.css.colorNode_current );
                        this.currentColorNode = ev.target;
                    }.bind(this)
                }
            }).inject( this.colorItem );
            div.setStyle("background-color",color);
            div.store("color",color);
            if( this.data.color ){
                if( this.data.color == color ){
                    div.setStyles( this.css.colorNode_current );
                    this.currentColorNode = div;
                }
            }else if(i==0){
                div.setStyles( this.css.colorNode_current );
                this.currentColorNode = div;
            }
            this.colorOptions.push( div )
        }.bind(this))
    },
    showWeek : function(){
        var area = this.formTableArea.getElement("[item='repeatWeekArea']");
        if( !area )return;
        area.setStyle("display","");
        if( this.isWeekSelectCreated )return;
        this.weekItems = [];
        var container = this.formTableArea.getElement("[item='repeatWeek']");
        var weekDayStyle = this.css.weekDayStyle = {
            "border" : "1px solid #ccc",
            "border-radius" : "3px",
            "height" : "20px",
            "line-heigh" : "20px",
            "background" : "#f7f7f7",
            "color" : "#666",
            "padding" : "0px 9px",
            "margin-right" : "9px",
            "float" : "left",
            "font-size" : "12px",
            "cursor" : "pointer"
        };
        var weekDayOnStyle = this.css.weekDayOnStyle = Object.merge( Object.clone(weekDayStyle), {
            "border" : "1px solid #3c75b7",
            "background" : "#3c75b7",
            "color" : "#fff"
        });
        var repeatWeeks = this.data.repeatWeeks || [ this.lp.weeks.rruleArr[ this.date.getDay() ] ];
        for( var i=0; i<7; i++ ){
            var rruleWeekString = this.lp.weeks.rruleArr[i];
            var isCurrentDay = repeatWeeks.contains( rruleWeekString );
            var div = new Element("div",{
                styles : isCurrentDay ? weekDayOnStyle : weekDayStyle,
                text : this.lp.weeks.arr[i],
                events : {
                    click : function(ev){
                        this.triggerWeek( ev.target )
                    }.bind(this)
                }
            }).inject(container);
            if( isCurrentDay ){
                if( this.date.getDay() == i ){
                    this.currrentWeekDayItem = div;
                }
                div.store("isOn", true);
            }
            div.store( "weekDay", rruleWeekString );
            this.weekItems.push(div);

        }
        this.isWeekSelectCreated = true;
    },
    triggerWeek : function(item){
        if(item.retrieve("isOn")){
            item.store("isOn", false);
            item.setStyles( this.css.weekDayStyle )
        }else{
            item.store("isOn", true);
            item.setStyles( this.css.weekDayOnStyle )
        }
        if( this.getSelectWeek().length==0 ){
            this.triggerWeek( this.currrentWeekDayItem );
        }
    },
    getSelectWeek : function(){
        if( !this.weekItems )return [];
        var weekDay = [];
        this.weekItems.each(function(item){
            if( item.retrieve("isOn") ){
                weekDay.push( item.retrieve("weekDay"));
            }
        });
        return weekDay;
    },
    _createBottomContent : function(){
        var html = "<div style='width:724px;margin:0px auto;'><table width='724' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableValue' width='80'></td>" +
            "    <td styles='formTableValue' style='padding-top: 15px;'>"+
            "       <div item='saveAction' style='float:left;display:"+ ( (this.isEdited || this.isNew) ? "" : "none") +";'></div>"+
            "       <div item='editAction' style='float:left;display:"+ ( (this.isEdited || this.isNew) ? "none" : "") +";'></div>"+
            "       <div item='removeAction' style='float:left;display:"+ ( this.isEdited ? "" : "none") +";'></div>"+
            "       <div item='cancelAction' style='"+( (this.isEdited || this.isNew ) ? "float:left;" : "float:left")+"'></div>"+
            "       <div item='moreInfor' style='float: right;margin-top:5px;'></div>"+
            "   </td></tr>" +
            "</table></div>";
        this.formBottomNode.set("html", html);
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            var form = new MForm(this.formBottomNode, {}, {
                isEdited: this.isEdited || this.isNew,
                style : "meeting",
                hasColon : true,
                itemTemplate: {
                    moreInfor : {
                        type : "a", value : "更多编辑", event : {
                            click : function(){ this.openMoreInfor() }.bind(this)
                        }, disable : this.options.isFull
                    },
                    saveAction : { type : "button", className : "inputOkButton", value : this.lp.save, event : {
                        click : function(){ this.save();}.bind(this)
                    } },
                    removeAction : { type : "button", className : "inputCancelButton", value : this.lp.cancelEvent , event : {
                        click : function( item, ev ){ this.cancelEvent(ev); }.bind(this)
                    } },
                    editAction : { type : "button", className : "inputOkButton", value : this.lp.editEvent , event : {
                        click : function(){ this.editEvent(); }.bind(this)
                    } },
                    cancelAction : { type : "button", className : "inputCancelButton", value : this.lp.close , event : {
                        click : function(){ this.close(); }.bind(this)
                    } }
                }
            }, this.app);
            form.load();
        }.bind(this), true);
    },
    openMoreInfor : function(){
        this.options.isFull = true;
        this.options.width = "1100";
        this.options.height = "620";
        this.isWeekSelectCreated = false;
        this.reload( true );
    },
    getString : function( str ){
        var s = "00" + str;
        return s.substr(s.length - 2, 2 );
    },
    getEventData : function( callback ){
        if( this.data && this.data.id ){
            this.app.actions.getEvent( this.data.id, function( json ){
                this.data = json.data;
                if(callback)callback();
            }.bind(this))
        }else{
            if(callback)callback();
        }
    },
    editEvent : function(){

        this.isWeekSelectCreated = false;
        this.formTopNode = null;
        if(this.setFormNodeSizeFun && this.app && this.app.removeEvent ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();

        this.edit();
    },
    reset: function(){
        this.formTableArea.empty();
        this._createTableContent();
    },

    loadAttachment: function(){
        if(!this.attachmentTr)return;
        this.attachmentTr.setStyle("display","");
        this.attachmentNode = new Element("div", {"styles": this.css.createEventAttachmentNode}).inject(this.attachmentArea);
        var attachmentContentNode = new Element("div", {"styles": this.css.createEventAttachmentContentNode}).inject(this.attachmentNode);
        MWF.require("MWF.widget.AttachmentController", function(){
            this.attachmentController = new MWF.widget.AttachmentController(attachmentContentNode, this, {
                "size": "min",
                "isSizeChange": false,
                "isReplace": false,
                "isUpload": this.isNew || this.isEdited,
                "isDelete": this.isNew || this.isEdited,
                "isDownload": true,
                "readonly": !this.isNew && !this.isEdited
            });
            this.attachmentController.load();
            if( this.data.attachmentList ){
                this.data.attachmentList.each(function (att) {
                    att.person = att.lastUpdatePerson.split("@")[0];
                    var at = this.attachmentController.addAttachment(att);
                }.bind(this));
            }
        }.bind(this));
    },
    uploadAttachment: function(e, node){
        if (!this.uploadFileAreaNode){
            this.createUploadFileNode();
        }
        this.fileUploadNode.click();
    },
    createUploadFileNode: function(){
        this.uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" multiple/>";
        this.uploadFileAreaNode.set("html", html);

        this.fileUploadNode = this.uploadFileAreaNode.getFirst();
        this.fileUploadNode.addEvent("change", function(){

            var files = this.fileUploadNode.files;
            if (files.length){

                for (var i = 0; i < files.length; i++) {
                    var file = files.item(i);

                    var formData = new FormData();
                    formData.append('file', file);
                    //formData.append('folder', folderId);

                    this.app.actions.addAttachment(function(o, text){
                        if (o.id){
                            this.app.actions.getAttachment(o.id, function(json){
                                if (json.data) this.attachmentController.addAttachment(json.data);
                                this.attachmentController.checkActions();
                            }.bind(this))
                        }
                        this.attachmentController.checkActions();
                    }.bind(this), null, formData, this.data.id, file);
                }

            }
        }.bind(this));
    },

    deleteAttachments: function(e, node, attachments){
        var names = [];
        attachments.each(function(attachment){
            names.push(attachment.data.name);
        }.bind(this));

        var _self = this;
        var confirm = ( this.app && this.app.confirm ) ? this.app.confirm : MWF.xDesktop.confirm;
        confirm("warn", e, this.lp.deleteAttachmentTitle, this.lp.deleteAttachment+"( "+names.join(", ")+" )", 300, 120, function(){
            while (attachments.length){
                attachment = attachments.shift();
                _self.deleteAttachment(attachment);
            }
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    deleteAttachment: function(attachment){
        this.app.actions.deleteFile(attachment.data.id, function(josn){
            this.attachmentController.removeAttachment(attachment);
            this.attachmentController.checkActions();
        }.bind(this));
    },
    downloadAttachment: function(e, node, attachments){
        attachments.each(function(att){
            this.app.actions.getFileDownload(att.data.id);
        }.bind(this));
    },
    openAttachment: function(e, node, attachments){
        attachments.each(function(att){
            this.app.actions.getFile(att.data.id);
        }.bind(this));
    },
    getAttachmentUrl: function(attachment, callback){
        this.app.actions.getFileUrl(attachment.data.id, callback);
    },

    cancelEvent: function(e){
        var _self = this;
        var data = this.data;
        var postDelete = function(){
            this.view.reload();
            this.close();
        }.bind(this);
        if( this.oldRecurrenceRule ) { //如果是原来是重复的
            this.openDeleteOptionForm( function( saveOptions ){
                if( saveOptions == "single" ){
                    this.app.actions.deleteSingleEvent(data.id, function(json){ postDelete(json); }.bind(this));
                }else if( saveOptions == "after" ){
                    this.app.actions.deleteAfterEvent(data.id, function(json){ postDelete(json); }.bind(this));
                }else if( saveOptions == "all" ){
                    this.app.actions.deleteAllEventsWithRepeatMaster(data.id, function(json){ postDelete(json); }.bind(this));
                }
            }.bind(this))
        }else if( data.id ){ //编辑
            var text = this.app.lp.cancel_confirm.replace(/{name}/g, this.data.title);
            var confirm = MWF.xDesktop.confirm; //( this.app && this.app.confirm ) ? this.app.confirm : MWF.xDesktop.confirm;
            confirm("infor", e, this.app.lp.cancel_confirm_title, text, 380, 200, function(){
                _self._cancelEvent();
                this.close();
            }, function(){
                this.close();
            });
        }
    },
    _cancelEvent: function(){
        var view = this.view;
        this.app.actions.deleteSingleEvent(this.data.id, function(){
            view.reload();
            this.close();
        }.bind(this))
    },
    openDeleteOptionForm : function( callback ){
        this.deleteOptionsForm = new MWFCalendar.DeleteOptionDialog( this, {}, {
            onPostOk : function( saveOptions ){
                if(callback){
                    callback(saveOptions)
                }
            }.bind(this)
        }, {});
        this.deleteOptionsForm.edit();
    },
    save: function(){
        this._save(function(){
            if( this.app && this.app.notice ){
                this.app.notice(this.lp.event_saveSuccess, "success");
            }else{
                MWF.xDesktop.notice("ok", {"x": "right", "y": "top"}, this.lp.event_saveSuccess, $(document.body))
            }
            this.close();
            //if (!this.attachmentNode){
            //    this.loadAttachment();
            //}
        }.bind(this));
    },
    _save: function(callback){
        var data = this.getSaveData();
        if( !data )return;
        var errorText = "";
        if (!data.title) errorText +=this.lp.event_input_subject_error;
        if( data.startTime ){
            if ( (Date.parse(data.startTime) - Date.parse(data.endTime)) > 0) errorText +=this.lp.event_input_time_error;
            //if (now.diff(this.data.startTime, "minute")<0) errorText +=this.lp.event_input_date_error;
            //
            //delete this.data.startTimeDate;
            //delete this.data.completedTimeDate;
        }

        if (errorText){
            if( this.app && this.app.notice ){
                this.app.notice(this.lp.event_input_error+errorText, "error");
            }else{
                MWF.xDesktop.notice("error", {"x": "right", "y": "top"}, this.lp.event_input_error+errorText, $(document.body));
            }
            return false;
        }

        var postSave = function( json ){
            this.data.id = json.data.id;
            this.oldRecurrenceRule = data.recurrenceRule;
            this.waitReload = true;
            if (callback) callback();
        }.bind(this);

        this.data = data;
        if( this.oldRecurrenceRule ) { //如果是原来是重复的
            this.openSaveOptionForm( function( saveOptions ){
                if( saveOptions == "single" ){
                    this.app.actions.updateSingleEvent(data.id, this.data, function(json){ postSave(json); }.bind(this));
                }else if( saveOptions == "after" ){
                    this.app.actions.updateAfterEvent(data.id, this.data, function(json){ postSave(json); }.bind(this));
                }else if( saveOptions == "all" ){
                    this.app.actions.updateAllEventsWithRepeatMaster(data.id, this.data, function(json){ postSave(json); }.bind(this));
                }
            }.bind(this))
        }else if( data.id ){ //编辑
            this.app.actions.updateSingleEvent(data.id, this.data, function(json){ postSave(json); }.bind(this));
        }else{ //新增
            this.app.actions.addEvent(this.data, function(json){ postSave(json); }.bind(this));
        }
    },
    openSaveOptionForm : function( callback ){
        this.saveOptionsForm = new MWFCalendar.SaveOptionDialog( this, {}, {
            onPostOk : function( saveOptions ){
                if(callback){
                    callback(saveOptions)
                }
            }.bind(this)
        }, {});
        this.saveOptionsForm.edit();
    },
    getSaveData: function(){
        var data =  this.form.getResult(true,"",true,false,true);
        if( !data )return null;
        if( data ){
            data.isAllDayEvent = typeOf( data.isAllDayEvent ) == "array" ? data.isAllDayEvent.join("") : data.isAllDayEvent;
            if( data.isAllDayEvent == "true"){
                data.startTime = this.data.startDateInput + " " + "00:00:00";
                data.endTime = this.data.endDateInput + " " + "23:59:59";
            }else{
                data.startTime = this.data.startDateInput + " " + this.data.startTimeInput + ":00";
                data.endTime = this.data.endDateInput + " " + this.data.endTimeInput + ":00";
            }
            data.recurrenceRule = this.getRRuleString( data );
            if( data.remind ){
                var valarmTime_config = [0,0,0,0];
                var r = data.remind.split("_");
                var n = parseInt( r[0] );
                if( r[1] == "d" ){
                    valarmTime_config[0] = n;
                }else if( r[1] == "h" ){
                    valarmTime_config[1] = n;
                }else if( r[1] == "m" ){
                    valarmTime_config[2] = n;
                }else if( r[1] == "s" ){
                    valarmTime_config[3] = n;
                }
                data.valarmTime_config = valarmTime_config.join(",");
            }
            if( !data.calendarId ){
                data.calendarId = this.app.currentCalendarData.id;
            }
            if( this.currentColorNode ){
                data.color = this.currentColorNode.retrieve("color");
            }else if( data.calendarId ){
                data.color = this.getColorByCalendarId(data.calendarId);
            }else{
                data.color = MWFCalendar.ColorOptions.deep[0];
            }
            delete  data.range;
        }
        return data;
    },

    close: function (e) {
        this.fireEvent("queryClose");
        this._close();
        //if( this.form ){
        //    this.form.destroy();
        //}
        if(this.setFormNodeSizeFun && this.app && this.app.removeEvent ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();
        this.fireEvent("postClose");
        if( this.waitReload )this.view.reload();
        delete this;
    }
});

MWFCalendar.CalendarForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "meeting",
        "width": "800",
        "height": "500",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "resizeable" : true,
        "closeAction": true,
        "resultSeparator" : null
    },
    _createTableContent: function () {
        var data = this.data;
        var editEnable = this.editEnable = ( !this.isEdited && !this.isNew && this.data.manageable );
        this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;
        this.userId = ( layout.desktop.session.user || layout.user ).id;
        if( data.type == "UNIT" ){
            this.options.height = "650"
        }
        if( this.isNew ){
            this.formTopTextNode.set( "text", "新建日历" );
        }else if( this.isEdited ){
            this.formTopTextNode.set( "text", "编辑日历" );
        }else{
            this.formTopTextNode.set( "text", "日历" );
        }

        this.formTableArea.set("html", this.getHtml());

        this.formTableContainer.setStyle("width","80%");

        this.colorItem = this.formTableArea.getElement("[item='color']");


        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, data, {
                isEdited: this.isEdited || this.isNew,
                style : "meeting",
                hasColon : true,
                itemTemplate: {
                    name: { text : "日历名称", notEmpty : true },
                    description: {text : "备注", type: "textarea"},
                    type : { text : "类型", type : "select", isEdited : this.isNew,
                        selectValue : ["PERSON", "UNIT"],
                        selectText : ["个人日历", "组织日历"],
                        defaultValue : "PERSON",
                        event : {
                            change : function(item){
                                this.changeType( item.getValue() )
                            }.bind(this)
                        }
                    },
                    target : { text : "所属组织", type : "org", orgType : "unit", validRule : { empty : function( value, item){
                        if( item.form.getItem("type").getValue() == "UNIT" && value == ""){ return false }else{ return true };
                    }}, validMessage : { empty : "所属组织不能为空"} },
                    isPublic : { text : "是否公开", type : "select", selectValue : ["true","false"], selectText : ["是","否"], defaultValue : "false" },
                    status : { text : "是否启用", type : "radio", selectValue : ["OPEN","CLOSE"], selectText : ["是","否"], defaultValue : "OPEN" },
                    manageablePersonList : { text : "管理者", type : "org", orgType : "person", count : 0},
                    viewerList : { text : "可见范围", type : "org", orgType : ["person","unit","group"], count : 0, value : function(){
                        return ( data.viewablePersonList || [] ).combine( data.viewableUnitList || [] ).combine( data.viewableGroupList || [] )
                    }.bind(this)},
                    publisherList : { text : "可新建范围", type : "org", orgType : ["person","unit","group"], count : 0, value : function(){
                        return ( data.publishablePersonList || [] ).combine( data.publishableUnitList || [] ).combine( data.publishableGroupList || [] )
                    }.bind(this)}
                }
            }, this.app);
            this.form.load();

            this.loadColor();
            //this.loadPermission();

        }.bind(this), true);
    },
    getHtml : function(){
        var boxStyle = (this.isEdited || this.isNew) ? "border:1px solid #ccc; border-radius: 4px;overflow: hidden;padding:8px;" : "";
        var targetStyle = this.data.type != "UNIT" ? "style='display:none'" : "";
        var permissionStyle = this.data.type != "UNIT" ? "style='display:none'" : "";
        return  "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='table-layout:fixed;'>" +
                "<tr><td styles='formTableTitle' width='80' lable='name'></td>" +
                "    <td styles='formTableValue' item='name' width='400' colspan='3'></td></tr>" +
                "<tr><td styles='formTableTitle'>"+this.lp.color+"：</td>" +
                "    <td styles='formTableValue' item='color' style='overflow: hidden;' colspan='3'></td></tr>" +
                "<tr><td styles='formTableTitle' lable='type' width='80'></td>" +
                "    <td styles='formTableValue' item='type'></td>" +
                "   <td styles='formTableTitleRight' lable='isPublic'  width='50'></td>" +
                "    <td styles='formTableValue' item='isPublic'></td></tr>" +
                "<tr><td styles='formTableTitle' lable='description'></td>" +
                "    <td styles='formTableValue' item='description' colspan='3'></td></tr>" +
                "<tr "+targetStyle+"><td styles='formTableTitle' lable='target'></td>" +
                "    <td styles='formTableValue' item='target' colspan='3'></td></tr>" +
                "<tr "+permissionStyle+"><td styles='formTableTitle' lable='manageablePersonList'></td>" +
                "    <td styles='formTableValue' item='manageablePersonList' colspan='3'></td></tr>" +
                "<tr "+permissionStyle+"><td styles='formTableTitle' lable='viewerList'></td>" +
                "    <td styles='formTableValue' item='viewerList' colspan='3'></td></tr>" +
                "<tr "+permissionStyle+"><td styles='formTableTitle' lable='publisherList'></td>" +
                "    <td styles='formTableValue' item='publisherList' colspan='3'></td></tr>" +
                "</tr>" +
                "<tr><td styles='formTableTitle' lable='status'></td>" +
                "   <td styles='formTableValue' item='status' colspan='3'></td></tr>" +
                "</table>"
    },
    changeType : function( type ){
        var changeItemName = ["target","manageablePersonList","viewerList","publisherList"];
        changeItemName.each( function(name){
            this.formTableArea.getElement("[item='"+name+"']").getParent().setStyle("display",type == "UNIT" ? "" : "none");
        }.bind(this));
        this.options.height = type == "UNIT" ? "650" : "500";
        this.setFormNodeSize();
    },
    loadColor : function(){
        if( this.isEdited || this.isNew ){
            this.loadColor_Edited();
        }else{
            this.loadColor_Read();
        }
    },
    loadColor_Read : function(){
        if( !this.colorItem )return;
        var div = new Element("div", {
            styles : this.css.colorNode
        }).inject( this.colorItem );
        div.setStyle("background-color",this.data.color);
        div.setStyles( this.css.colorNode_current );
    },
    loadColor_Edited : function(){
        if( !this.colorItem )return;
        this.colorOptions = [];
        MWFCalendar.ColorOptions.deep.each( function( color , i){
            var div = new Element("div", {
                styles : this.css.colorNode,
                events : {
                    click : function(ev){
                        if(this.currentColorNode)this.currentColorNode.setStyles( this.css.colorNode );
                        ev.target.setStyles( this.css.colorNode_current );
                        this.currentColorNode = ev.target;
                    }.bind(this)
                }
            }).inject( this.colorItem );
            div.setStyle("background-color",color);
            div.store("color",color);
            if( this.data.color ){
                if( this.data.color == color ){
                    div.setStyles( this.css.colorNode_current );
                    this.currentColorNode = div;
                }
            }else if(i==0){
                div.setStyles( this.css.colorNode_current );
                this.currentColorNode = div;
            }
            this.colorOptions.push( div )
        }.bind(this))
    },
    _createBottomContent : function(){
        var html = "<div style='width:724px;margin:0px auto;'><table width='724' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableValue' width='80'></td>" +
            "    <td styles='formTableValue' style='padding-top: 15px;'>"+
            "       <div item='saveAction' style='float:left;display:"+ ( (this.isEdited || this.isNew) ? "" : "none") +";'></div>"+
            "       <div item='editAction' style='float:left;display:"+ ( this.editEnable ? "" : "none") +";'></div>"+
            "       <div item='removeAction' style='float:left;display:"+ ( this.isEdited ? "" : "none") +";'></div>"+
            "       <div item='cancelAction' style='"+( (this.isEdited || this.isNew || this.editEnable) ? "float:left;" : "float:right;margin-right:15px;")+"'></div>"+
            "   </td></tr>" +
            "</table></div>";
        this.formBottomNode.set("html", html);
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            var form = new MForm(this.formBottomNode, {}, {
                isEdited: this.isEdited || this.isNew,
                style : "meeting",
                hasColon : true,
                itemTemplate: {
                    saveAction : { type : "button", className : "inputOkButton", value : this.lp.save, event : {
                        click : function(){ this.ok();}.bind(this)
                    } },
                    removeAction : { type : "button", className : "inputCancelButton", value : this.lp.deleteCalendar , event : {
                        click : function( item, ev ){ this.deleteCalendar(ev); }.bind(this)
                    } },
                    editAction : { type : "button", className : "inputOkButton", value : this.lp.editCalendar , event : {
                        click : function(){ this.editCalendar(); }.bind(this)
                    } },
                    cancelAction : { type : "button", className : "inputCancelButton", value : this.lp.close , event : {
                        click : function(){ this.close(); }.bind(this)
                    } }
                }
            }, this.app);
            form.load();
        }.bind(this), true);
    },
    deleteCalendar : function(){

    },
    editCalendar : function(){
        this.formTopNode = null;
        if(this.setFormNodeSizeFun && this.app && this.app.removeEvent ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();

        this.edit();
    },
    reset: function(){
        this.formTableArea.empty();
        this._createTableContent();
    },

    _ok : function( data, callback ){
        if( data.type == "UNIT" ){
            var viewerItem = this.form.getItem("viewerList").dom;
            data.viewablePersonList = viewerItem.getValueByType("person");
            data.viewableUnitList = viewerItem.getValueByType("unit");
            data.viewableGroupList = viewerItem.getValueByType("group");

            var publisherItem = this.form.getItem("publisherList").dom;
            data.publishablePersonList = publisherItem.getValueByType("person");
            data.publishableUnitList = publisherItem.getValueByType("unit");
            data.publishableGroupList = publisherItem.getValueByType("group");
            data.target = data.target.join("");
        }else{
            data.target = this.userName;
            ["manageablePersonList","viewablePersonList","publishablePersonList"].each( function( name ){
                data[name] = [ this.userName ];
            }.bind(this));
            ["viewableUnitList","viewableGroupList","publishableUnitList","publishableGroupList"].each( function( name ){
                data[name] = [];
            });
        }
        if( this.currentColorNode ){
            data.color = this.currentColorNode.retrieve("color");
        }else{
            data.color = MWFCalendar.ColorOptions.deep[0];
        }
        delete data.viewerList;
        delete data.publisherList;
        this.app.actions.saveCalendar(data, function(json){
            if( this.view )this.view.reload();
            callback(json);
        }.bind(this));
    },

    close: function (e) {
        this.fireEvent("queryClose");
        this._close();
        //if( this.form ){
        //    this.form.destroy();
        //}
        if(this.setFormNodeSizeFun && this.app && this.app.removeEvent){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();
        this.fireEvent("postClose");
        if( this.waitReload )this.view.reload();
        delete this;
    }
});

MWFCalendar.SaveOptionDialog = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "meeting",
        "width": "470",
        "height": "325",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        //"maxAction" : true,
        "closeAction": true,
        "title" : "修改重复日程"
    },
    _createTableContent : function(){

        this.formTableContainer.setStyles({
            "width" : "auto",
            "padding-top" : "20px",
            "padding-left" : "40px"
        });

        this.lp = { ok : "确定修改", cancel : "取消" };
        var html = "<table width='80%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
                //"<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +
            "<tr><td styles='formTableTitle' lable='saveOption'></td>" +
            "<tr><td styles='formTableValue' item='saveOption'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, {empName: "xadmin"}, {
                style : "meeting",
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    saveOption: {
                        defaultValue : "single",
                        text: "请选择您要修改日程的类型",
                        type: "radio",
                        selectText: ["只修改当前日程", "修改当前日程和之后的此重复日程","修改所有此重复日程"],
                        selectValue: ["single", "after", "all"]
                    }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    ok: function (e) {
        this.fireEvent("queryOk");
        var data = this.form.getResult(true, this.options.resultSeparator, true, false, true);
        if (data) {
            if( this.formMaskNode )this.formMaskNode.destroy();
            this.formAreaNode.destroy();
            this.fireEvent("postOk", data.saveOption );
        }
    }
});

MWFCalendar.DeleteOptionDialog = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "meeting",
        "width": "470",
        "height": "325",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        //"maxAction" : true,
        "closeAction": true,
        "title" : "删除重复日程"
    },
    _createTableContent : function(){
        this.formTableContainer.setStyles({
            "width" : "auto",
            "padding-top" : "20px",
            "padding-left" : "40px"
        });

        this.lp = { ok : "确定删除", cancel : "取消" };
        var html = "<table width='80%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
                //"<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +
            "<tr><td styles='formTableTitle' lable='saveOption'></td>" +
            "<tr><td styles='formTableValue' item='saveOption'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, {empName: "xadmin"}, {
                isEdited: this.isEdited || this.isNew,
                style : "meeting",
                itemTemplate: {
                    saveOption: {
                        defaultValue : "single",
                        text: "请选择您要删除日程的类型",
                        type: "radio",
                        selectText: ["只删除当前日程", "删除当前日程和之后的此重复日程","删除所有此重复日程"],
                        selectValue: ["single", "after", "all"]
                    }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    ok: function (e) {
        this.fireEvent("queryOk");
        var data = this.form.getResult(true, this.options.resultSeparator, true, false, true);
        if (data) {
            if( this.formMaskNode )this.formMaskNode.destroy();
            this.formAreaNode.destroy();
            this.fireEvent("postOk", data.saveOption );
        }
    }
});

MWFCalendar.EventTooltip = new Class({
    Extends: MTooltips,
    options : {
        displayDelay : 300
    },
    _loadCustom : function( callback ){
        this.loadAttachment();
        this.loadButton();
        if(callback)callback();
    },
    _getHtml : function(){
        var data = this.data;
        var titleStyle = "font-size:14px;color:#333";
        var valueStyle = "font-size:14px;color:#666;padding-right:10px";

        var beginD = Date.parse(this.data.startTime);
        var endD = Date.parse(this.data.endTime);
        var begin = beginD.format(this.lp.dateFormatAll) + "（" + this.lp.weeks.arr[beginD.get("day")] + "）";
        var end = endD.format(this.lp.dateFormatAll) + "（" + this.lp.weeks.arr[endD.get("day")] + "）";

        var html =
            "<div style='font-size: 16px;color:#333;padding:10px 10px 10px 20px;'>"+ data.title +"</div>"+
            "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
            "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
            "<tr><td style='"+titleStyle+";' width='40'>开始:</td>" +
            "    <td style='"+valueStyle+"'>" + begin + "</td></tr>" +
            "<tr><td style='"+titleStyle+"'>结束:</td>" +
            "    <td style='"+valueStyle+ "'>"+ end +"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+this.lp.locationName+":</td>" +
            "    <td style='"+valueStyle+ "'>"+ (this.data.locationName||"") +"</td></tr>" +
            //( this.options.isHideAttachment ? "" :
            //"<tr><td style='"+titleStyle+"'>"+this.lp.eventAttachment+":</td>" +
            //"    <td style='"+valueStyle+"' item='attachment'></td></tr>"+
            //)+
            "<tr><td style='"+titleStyle+"'></td>" +
            "    <td style='"+valueStyle+ "' item='seeMore'></td></tr>"+
        "</table>";
        return html;
    },
    destroy: function(){
        if( this.node ){
            this.node.destroy();
            this.node = null;
        }
    },
    loadButton : function(){
        var area = this.node.getElement("[item='seeMore']");
        new Element("div",{
            styles : {
                "background-color" : "#3c75b7",
                "height" : "28px",
                "line-height" : "28px",
                "border-radius" : "5px",
                "width" : "80px",
                "text-align" : "center",
                "font-size" : "12px",
                "cursor" : "pointer",
                "color" : "#fff"
            },
            events : { click : function() {
                var form = new MWFCalendar.EventForm(this, this.data, {
                    isFull : true
                }, {app:this.app});
                form.view = this.view;
                form.edit();
                this.hide();
            }.bind(this)},
            text : "查看更多"
        }).inject(area);
    },
    loadAttachment: function(){
        if( this.options.isHideAttachment )return;
        if( typeOf(this.data.attachmentList)=="array" && this.data.attachmentList[0] ){
            var area = this.node.getElement("[item='attachment']");
            this.attachmentNode = new Element("div"
                //{"styles": this.css.createCalendarAttachmentNode}
            ).inject(area);
            var attachmentContentNode = new Element("div", {
                //"styles": this.css.createCalendarAttachmentContentNode
            }).inject(this.attachmentNode);

                this.attachmentController = new MWFCalendar.EventTooltip.AttachmentController(attachmentContentNode, this, {
                    "size": "min",
                    "isSizeChange": false,
                    "isReplace": false,
                    "isUpload": false,
                    "isDelete": false,
                    "isDownload": true,
                    "readonly": true
                });
                this.attachmentController.load();
                this.data.attachmentList.each(function (att) {
                    att.person = att.lastUpdatePerson.split("@")[0];
                    var at = this.attachmentController.addAttachment(att);
                }.bind(this));
        }
    },
    downloadAttachment: function(e, node, attachments){
        attachments.each(function(att){
            this.app.actions.getFileDownload(att.data.id);
        }.bind(this));
    },
    openAttachment: function(e, node, attachments){
        attachments.each(function(att){
            this.app.actions.getFile(att.data.id);
        }.bind(this));
    },
    getAttachmentUrl: function(attachment, callback){
        this.app.actions.getFileUrl(attachment.data.id, callback);
    }
});

MWFCalendar.EventTooltip.AttachmentController = new Class({
    Extends : MWF.widget.AttachmentController,
    loadMin: function(){
        if (!this.node) this.node = new Element("div", {"styles": this.css.container_min});

        if (!this.minActionAreaNode){
            //this.minActionAreaNode = new Element("div", {"styles": this.css.minActionAreaNode}).inject(this.node);
            this.minContent = new Element("div", {"styles": this.css.minContentNode}).inject(this.node);
            this.minContent.setStyles({
                "margin-right" : "0px"
            });
            //this.min_downloadAction = this.createAction(this.minActionAreaNode, "download", MWF.LP.widget.download, function(e, node){
            //    this.downloadAttachment(e, node);
            //}.bind(this));

            this.node.inject(this.container);

            //if (this.options.readonly) this.setReadonly();
            this.checkActions();

            this.setEvent();
        }else{
            //this.minActionAreaNode.setStyle("display", "block");
            this.minContent.setStyle("display", "block");
            this.minContent.empty();
        }
        var atts = [];
        while (this.attachments.length){
            var att = this.attachments.shift();
            atts.push(new MWFCalendar.EventTooltip.AttachmentMin(att.data, this));
        }
        this.attachments = atts;
    },
    addAttachment: function(data){
        if (this.options.size=="min"){
            this.attachments.push(new MWFCalendar.EventTooltip.AttachmentMin(data, this));
        }else{
            this.attachments.push(new MWF.widget.AttachmentController.Attachment(data, this));
        }
    }

});

MWFCalendar.EventTooltip.AttachmentMin = new Class({
    Extends : MWF.widget.AttachmentController.AttachmentMin,
    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css["minAttachmentNode_list_over"])}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css["minAttachmentNode_list"])}.bind(this),
            "mousedown": function(e){this.selected(e);}.bind(this),
            "click": function(e){this.downloadAttachment(e);}.bind(this)
        });
    },
    downloadAttachment: function(e){
        if (this.controller.module) this.controller.module.downloadAttachment(e, null, [this]);
    }
});
