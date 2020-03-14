MWF.xApplication.Homepage.CalendarContent  = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent,
    Implements: [Options, Events],
    options: {
        "view": "calendarContent.html"
    },
    load: function(){
        this.tabs = {};
        this.dayFlags = [];
        this.container.loadHtml(this.viewPath, {"bind": {"lp": this.app.lp}, "module": this}, function(){
            this.initSize();
            this.loadCalender(function(){
                this.fireEvent("load");
            }.bind(this));

            // this.loadMyCalender(function(){
            //     this.fireEvent("load");
            // }.bind(this));

            // //是否需要定时自动刷新 @todo
            // this.startProcessAction.addEvent("click", this.startProcess.bind(this));

            //this.moreInforAction.addEvent("click", this.moreInfor.bind(this));
        }.bind(this));
    },
    openCalender: function(e){
        layout.openApplication(e, "Calendar");
    },
    setContentSize: function(){
        var total = this.container.getSize().y;
        var calenderHeight = this.calenderArea.getSize().y+this.calenderArea.getEdgeHeight();
        var titleHeight = this.calenderTitleNode.getSize().y+this.calenderTitleNode.getEdgeHeight();
        var bottomHeight = this.pageAreaNode.getSize().y+this.pageAreaNode.getEdgeHeight();
        var inforHeight = this.calenderInforArea.getSize().y+this.calenderInforArea.getEdgeHeight();
        var thisHeight = this.itemContentNode.getEdgeHeight();
        var contentHeight = total-titleHeight-bottomHeight-thisHeight-inforHeight-calenderHeight;
        this.itemContentNode.setStyle("height", ""+contentHeight+"px");
        this.contentHeight = contentHeight;
        //this.pageSize = (this.options.itemHeight/this.contentHeight).toInt();

        if (this.noItemNode){
            var m = (this.contentHeight- this.noItemNode.getSize().y)/2;
            this.noItemNode.setStyle("margin-top", ""+m+"px");
        }

        if (this.dayFlags && this.dayFlags.length){
            this.dayFlags.each(function(flag){
                var td = flag.retrieve("td");
                if (td) flag.position({
                    "relativeTo": td,
                    "position": 'topRight',
                    "edge": 'topRight'
                });
            });
        }
    },
    loadCalender: function(callback){
        o2.require("o2.widget.CalendarPage", function(){
            this.calender = new o2.widget.CalendarPage(this.calenderArea, {
                "style": "homepage",
                "onQueryComplate": function (d) {
                    this.loadMyCalender(d, callback);
                    //if (callback) callback();
                }.bind(this),
                "onChangeViewToDay": function(){
                    this.loadMonthCalender();
                }.bind(this),
                "onChangeViewToMonth": function(){
                    this.calenderFlagArea.empty();
                    this.dayFlags = [];
                }.bind(this),
                "onChangeViewToYear": function(){
                    this.calenderFlagArea.empty();
                    this.dayFlags = [];
                }.bind(this)
            });
            this.calender.show();
            this.calender._selectDate((new Date()).toString());

            var m = this.calender.currentNode.getStyle("margin-right").toInt();
            m = m +40;
            this.calender.currentNode.setStyle("margin-right", ""+m+"px");
            this.calender.todayNode = new Element("div.mainColor_color", {"styles": this.calender.css.todayNode, "text": this.app.lp.today}).inject(this.calender.currentNode, "before");
            this.calender.todayNode.addEvent("click", function(){
                this.calender.changeViewToDay();
                // this.calender.showDay();
                this.calender._selectDate((new Date()).toString());
                // this.loadMonthCalender();
            }.bind(this));

        }.bind(this));
    },
    loadMonthCalender: function(){
        this.calenderFlagArea.empty();
        this.dayFlags = [];
        var tds = this.calender.contentTable.getElements("td");
        var start = new Date(tds[0].retrieve("dateValue")).clearTime();
        var end = new Date(tds[tds.length-1].retrieve("dateValue")).clearTime();
        end.increment("day", 1);

        var d = {
            "startTime": start.format("db"),
            "endTime": end.format("db"),
            "createPerson": layout.user.distinguishedName
        };
        o2.Actions.load("x_calendar_assemble_control").Calendar_EventAction.listWithFilter(d, function(json){
            if (json.data){
                if (json.data.wholeDayEvents && json.data.wholeDayEvents.length){
                    json.data.wholeDayEvents.each(function(e){
                        var d = (new Date()).parse(e.startTime);
                        var i = start.diff(d);
                        this.setCalenderFlag(tds[i]);
                    }.bind(this));
                }
                if (json.data.inOneDayEvents && json.data.inOneDayEvents.length){
                    json.data.inOneDayEvents.each(function(e){
                        if (e.inOneDayEvents && e.inOneDayEvents.length){
                            var d = (new Date()).parse(e.eventDate);
                            var i = start.diff(d);
                            this.setCalenderFlag(tds[i]);
                        }
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    setCalenderFlag: function(td){
        // var t = td.get("text");
        //         // td.empty();
        //         // td.set("text", t);
        //td.set("background-color","#fdd9d9");
        var flag = new Element("div.o2_homepage_calender_item_flag").inject(this.calenderFlagArea);
        flag.position({
            "relativeTo": td,
            "position": 'topRight',
            "edge": 'topRight'
        });
        flag.store("td", td);
        this.dayFlags.push(flag);
    },

    loadMyCalender: function(d, callback){
        //this.loadFile(null, callback);
        if (!this.isLoading){
            if (!this.calenderContentTab){
                this.calenderContentTab = new MWF.xApplication.Homepage.CalendarContent.Calendar(this, this.calenderTab, d, {
                    "onLoad": function(){ if (callback) callback(); }
                });
            }else{
                this.calenderContentTab.reload(d);
            }
            this.currentTab = this.calenderContentTab;
        }
    },

    // loadFile: function(e, callback){
    //     if (!this.isLoading) {
    //         if (!this.fileContentTab){
    //             this.fileContentTab = new MWF.xApplication.Homepage.FileContent.File(this, this.meetingTab, {
    //                 "onLoad": function(){ if (callback) callback(); }
    //             });
    //         }else{
    //             this.fileContentTab.load();
    //         }
    //         this.currentTab = this.fileContentTab;
    //     }
    // }

});

MWF.xApplication.Homepage.CalendarContent.Calendar = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent.Task,
    Implements: [Options, Events],
    options: {
        "itemHeight": 80,
        "type": "meetingInvited",
        "month": 1
    },
    initialize: function(content, tab, date, options){
        this.setOptions(options);
        this.content = content;
        this.app = this.content.app;
        this.container = this.content.itemContentNode;
        this.tab = tab;
        this.date = (new Date()).parse(date).clearTime();
        this.load();
    },
    reload: function(date){
        if (!this.content.isLoading) {
            this.date = (new Date()).parse(date).clearTime();
            this.beginLoadContent();
            this.showTab();
            this.initItemCount(this.page);
            this.loadItemsRes();
        }
    },
    loadItemsRes: function(){
        var endDate = this.date.clone().increment("day", 1);
        var d = {
            "startTime": this.date.format("db"),
            "endTime": endDate.format("db"),
            "createPerson": layout.user.distinguishedName
        };
        o2.Actions.load("x_calendar_assemble_control").Calendar_EventAction.listWithFilterSample(d, function(json){
            if (json.data && json.data.length){
                this.loadItems(json.data);
                this.loadCalenderInfor(json.data.length);
            }else{
                this.emptyLoadContent();
                this.loadCalenderInfor(0);
            }
            this.fireEvent("load");
        }.bind(this));
    },
    loadCalenderInfor: function(count){
        var text = (count) ? this.app.lp.calenderInfor : this.app.lp.noCalenderInfor;
        text = text.replace("{name}", layout.user.name);
        var today = new Date();
        var dateStr = (this.date.diff(today)===0) ? this.app.lp.today : this.date.format(this.app.lp.dateFormat);
        text = text.replace("{date}", dateStr);
        text = text.replace("{count}", count);
        this.content.calenderInforArea.empty();
        this.content.calenderInforArea.set("html", text);
    },
    emptyLoadContent: function(){
        this.container.empty();
        this.container.removeClass("o2_homepage_area_content_loading").removeClass("icon_loading");
        this.content.pageAreaNode.empty();
        //this.itemContentNode.addClass("o2_homepage_task_area_content_empty").addClass("icon_notask");
        this.content.noItemNode = new Element("div.o2_homepage_calendar_area_content_empty_node", {"text": this.app.lp.noCalendar}).inject(this.container);
        var m = (this.content.contentHeight- this.content.noItemNode.getSize().y)/2;
        this.content.noItemNode.setStyle("margin-top", ""+m+"px");
        this.content.isLoading = false;
    },
    loadItems: function(data){
        data.each(function(d, i){
            this.loadItem(d, i);
        }.bind(this));
        this.endLoadContent();
    },
    loadItem: function(d, i){
        var row = this.loadItemRow(d, i);
        var _self = this;
        row.store("data", d);
        // row.addEvents({
        //     "mouseover": function(){
        //         this.addClass("mainColor_color").addClass("o2_homepage_task_item_row_over");
        //     },
        //     "mouseout": function(){
        //         this.removeClass("mainColor_color").removeClass("o2_homepage_task_item_row_over");
        //     }
        // });
        row.addEvent("click", function(e){
            layout.openApplication(e, "Calendar");
        });
    },

    getLightColor : function( deepColor ){
        var deep = ["#428ffc","#5bcc61","#f9bf24","#f75f59","#f180f7","#9072f1","#909090","#1462be"];
        var light = ["#cae2ff","#d0f1b0","#fef4bb","#fdd9d9","#f4c5f7","#d6ccf9","#e7e7e7","#cae2ff"];
        var index = deep.indexOf(deepColor);
        return index > -1 ? light[index] : light[0];
    },
    loadItemRow: function(d){
        var row = new Element("div.o2_homepage_calender_item_node").inject(this.container);
        row.setStyle("background-color", this.getLightColor(d.color));

        var locationNode = new Element("div.o2_homepage_calender_item_location", {"text": (d.locationName || ""), "title": (d.locationName || "")}).inject(row);
        var inforArea = new Element("div.o2_homepage_calender_item_infor").inject(row);

        var titleNode = new Element("div.o2_homepage_calender_item_title").inject(inforArea);
        var titleIconNode = new Element("div.o2_homepage_calender_item_title_icon").inject(titleNode);
        var titleTextNode = new Element("div.o2_homepage_calender_item_title_text", {"text": d.title, "title": d.title}).inject(titleNode);

        var timeNode = new Element("div.o2_homepage_calender_item_time").inject(inforArea);
        var timeStr = "";
        if (d.isAllDayEvent) {
            timeStr = this.app.lp.allDay;
        }else{
            var start = (new Date()).parse(d.startTime);
            var end = (new Date()).parse(d.endTime);
            if (start.diff(end)===0){
                timeStr = start.format("%Y-%m-%d %H:%M")+" - "+end.format("%H:%M");
            }else{
                timeStr = start.format("%Y-%m-%d %H:%M")+" - "+end.format("%Y-%m-%d %H:%M");
            }
        }
        timeNode.set("html", timeStr);


        return row;
    },
});
