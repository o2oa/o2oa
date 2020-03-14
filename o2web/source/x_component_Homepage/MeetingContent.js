MWF.xApplication.Homepage.MeetingContent  = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent,
    Implements: [Options, Events],
    options: {
        "view": "meetingContent.html"
    },
    load: function(){
        this.tabs = {};
        this.container.loadHtml(this.viewPath, {"bind": {"lp": this.app.lp}, "module": this}, function(){
            this.initSize();
            this.loadMyMeeting(function(){
                this.fireEvent("load");
            }.bind(this));

            // //是否需要定时自动刷新 @todo
            // this.startProcessAction.addEvent("click", this.startProcess.bind(this));

            //this.moreInforAction.addEvent("click", this.moreInfor.bind(this));
        }.bind(this));
    },
    openMeeting: function(e){
        layout.openApplication(e, "Meeting");
    },
    setContentSize: function(){
        var total = this.container.getSize().y;
        var titleHeight = this.taskTitleNode.getSize().y+this.taskTitleNode.getEdgeHeight();
        var bottomHeight = this.pageAreaNode.getSize().y+this.pageAreaNode.getEdgeHeight();
        var thisHeight = this.itemContentNode.getEdgeHeight();
        var contentHeight = total-titleHeight-bottomHeight-thisHeight;
        this.itemContentNode.setStyle("height", ""+contentHeight+"px");
        this.contentHeight = contentHeight;
        //this.pageSize = (this.options.itemHeight/this.contentHeight).toInt();

        if (this.noItemNode){
            var m = (this.contentHeight- this.noItemNode.getSize().y)/2;
            this.noItemNode.setStyle("margin-top", ""+m+"px");
        }
    },

    loadMyMeeting: function(callback){
        o2.Actions.load("x_meeting_assemble_control").MeetingAction.lisInvitedWait(function(json){
            if (json.data && json.data.length){
                this.loadMeetingInvited(null, callback, this.data);
            }else{
                this.loadMeeting(null, callback);
            }
        }.bind(this));
    },
    loadMeetingInvited: function(e, callback, data){
        if (!this.isLoading) {
            if (!this.invitedContentTab){
                this.invitedContentTab = new MWF.xApplication.Homepage.MeetingContent.MeetingInvited(this, this.invitedTab, data, {
                    "onLoad": function(){ if (callback) callback(); }
                });
            }else{
                this.invitedContentTab.load();
            }
            this.currentTab = this.invitedContentTab;
        }
    },
    loadMeeting: function(e, callback){
        if (!this.isLoading) {
            if (!this.meetingContentTab){
                this.meetingContentTab = new MWF.xApplication.Homepage.MeetingContent.Meeting(this, this.meetingTab, null, {
                    "onLoad": function(){ if (callback) callback(); }
                });
            }else{
                this.meetingContentTab.load();
            }
            this.currentTab = this.meetingContentTab;
        }
    }

});

MWF.xApplication.Homepage.MeetingContent.MeetingInvited = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent.Task,
    Implements: [Options, Events],
    options: {
        "itemHeight": 80,
        "type": "meetingInvited"
    },
    initialize: function(content, tab, data, options){
        this.setOptions(options);
        this.content = content;
        this.app = this.content.app;
        this.container = this.content.itemContentNode;
        this.tab = tab;
        this.data = data;
        this.load();
    },
    loadItemsRes: function(){
        if (this.data){
            this.loadItems(this.data);
            this.data = null;
            this.fireEvent("load");
        }else{
            o2.Actions.load("x_meeting_assemble_control").MeetingAction.lisInvitedWait(function(json){
                if (json.data && json.data.length){
                    this.loadItems(json.data);
                }else{
                    this.emptyLoadContent();
                }
                this.fireEvent("load");
            }.bind(this));
        }
    },
    emptyLoadContent: function(){
        this.container.empty();
        this.container.removeClass("o2_homepage_area_content_loading").removeClass("icon_loading");
        this.content.pageAreaNode.empty();
        //this.itemContentNode.addClass("o2_homepage_task_area_content_empty").addClass("icon_notask");
        this.content.noItemNode = new Element("div.o2_homepage_meeting_area_content_empty_node", {"text": this.app.lp.noMeeting}).inject(this.container);
        var m = (this.content.contentHeight- this.content.noItemNode.getSize().y)/2;
        this.content.noItemNode.setStyle("margin-top", ""+m+"px");
        this.content.isLoading = false;
    },

    loadItems: function(data){
        for (var i=0; i<Math.min(data.length, this.pageSize); i++){
            var d = data[i];
            this.loadItem(d, i);
        }
        this.endLoadContent();
    },
    loadItemRow: function(d){
        var row = new Element("div.o2_homepage_meeting_item_node").inject(this.container);

        var actionArea = new Element("div.o2_homepage_meeting_item_action").inject(row);
        var inforArea = new Element("div.o2_homepage_meeting_item_infor").inject(row);

        var titleNode = new Element("div.o2_homepage_meeting_item_title", {"text": d.subject, "title": d.subject}).inject(inforArea);

        var timeNode = new Element("div.o2_homepage_meeting_item_time").inject(inforArea);
        var start = (new Date()).parse(d.startTime);
        var completed = (new Date()).parse(d.completedTime);
        var startStr = start.format("%Y-%m-%d %H:%M");
        var completedStr = start.format("%H:%M");
        timeNode.set("html", this.app.lp.meetingTime+": <span style='color: #999999'>"+startStr+" - "+completedStr+"<span>");

        var locationNode = new Element("div.o2_homepage_meeting_item_location").inject(inforArea);
        locationNode.set("html", this.app.lp.meetingLocation+": <span style='color: #999999'>"+d.woRoom.name+"<span>");

        if (!d.myAccept && !d.myReject){    //等待接受
            var acceptNode = new Element("div.o2_homepage_meeting_item_action_accept", {"text": this.app.lp.accept}).inject(actionArea);
            var rejectNode = new Element("div.o2_homepage_meeting_item_action_reject", {"text": this.app.lp.reject}).inject(actionArea);

            acceptNode.store("invited", d);
            rejectNode.store("invited", d);
            acceptNode.addEvent("click", function(e){
                var d = e.target.retrieve("invited");
                this.acceptInvitedConfirm(d, acceptNode, e);
            }.bind(this));
            rejectNode.addEvent("click", function(e){
                var d = e.target.retrieve("invited");
                this.rejectInvitedConfirm(d, rejectNode, e);
            }.bind(this));

        }else if (d.myAccept){              //已经参加
            new Element("div.o2_homepage_meeting_item_action_accepted", {"text": this.app.lp.accepted}).inject(actionArea);
        }else if (d.myReject){              //拒绝参加
            new Element("div.o2_homepage_meeting_item_action_rejected", {"text": this.app.lp.rejected}).inject(actionArea);
        }
        return row;
    },
    acceptInvitedConfirm: function(d, node, e){
        var text = this.app.lp.acceptConfirm;
        text = text.replace("{name}", d.subject);
        var _self = this;
        this.app.confirm("warn", e, this.app.lp.acceptConfirmTitle, text, 340, 100, function(){
            _self.acceptInvited(d, node);
            this.close();
        }, function(){this.close()})
    },
    acceptInvited: function(d, node){
        o2.Actions.load("x_meeting_assemble_control").MeetingAction.accpet(d.id, function(json){
            var actionArea = node.getParent()
            actionArea.empty();
            new Element("div.o2_homepage_meeting_item_action_accepted", {"text": this.app.lp.accepted}).inject(actionArea);
        }.bind(this));
    },
    rejectInvitedConfirm: function(d, node, e){
        var text = this.app.lp.rejectConfirm;
        text = text.replace("{name}", d.subject);
        var _self = this;
        this.app.confirm("warn", e, this.app.lp.rejectConfirmTitle, text, 340, 100, function(){
            _self.rejectInvited(d, node);
            this.close();
        }, function(){this.close()})
    },
    rejectInvited: function(d, node){
        o2.Actions.load("x_meeting_assemble_control").MeetingAction.reject(d.id, function(json){
            var actionArea = node.getParent()
            actionArea.empty();
            new Element("div.o2_homepage_meeting_item_action_rejected", {"text": this.app.lp.rejected}).inject(actionArea);
        }.bind(this));
    },
    loadItem: function(d, i){
        var row = this.loadItemRow(d, i);

        var _self = this;
        row.store("data", d);
        row.addEvents({
            "mouseover": function(){
                this.addClass("mainColor_color").addClass("o2_homepage_task_item_row_over");
            },
            "mouseout": function(){
                this.removeClass("mainColor_color").removeClass("o2_homepage_task_item_row_over");
            }
        });
        row.getLast().addEvent("click", function(e){
            layout.openApplication(e, "Meeting");
        });
    },

    open: function(e, d){
        layout.openApplication(e, "Meeting");
    }

});
MWF.xApplication.Homepage.MeetingContent.Meeting = new Class({
    Extends: MWF.xApplication.Homepage.MeetingContent.MeetingInvited,
    Implements: [Options, Events],
    options: {
        "itemHeight": 80,
        "type": "meeting",
        "month": 1
    },
    loadItemsRes: function(){
        o2.Actions.load("x_meeting_assemble_control").MeetingAction.listComingMonth(this.options.month, function(json){
            if (json.data && json.data.length){
                this.loadItems(json.data);
            }else{
                this.emptyLoadContent();
            }
            this.fireEvent("load");
        }.bind(this));
    },
    loadItemRow: function(d){
        var row = new Element("div.o2_homepage_meeting_item_node").inject(this.container);

        var actionArea = new Element("div.o2_homepage_meeting_item_action").inject(row);
        var inforArea = new Element("div.o2_homepage_meeting_item_infor").inject(row);

        var titleNode = new Element("div.o2_homepage_meeting_item_title", {"text": d.subject, "title": d.subject}).inject(inforArea);

        var timeNode = new Element("div.o2_homepage_meeting_item_time").inject(inforArea);
        var start = (new Date()).parse(d.startTime);
        var completed = (new Date()).parse(d.completedTime);
        var startStr = start.format("%Y-%m-%d %H:%M");
        var completedStr = start.format("%H:%M");
        timeNode.set("html", this.app.lp.meetingTime+": <span style='color: #999999'>"+startStr+" - "+completedStr+"<span>");

        var locationNode = new Element("div.o2_homepage_meeting_item_location").inject(inforArea);
        locationNode.set("html", this.app.lp.meetingLocation+": <span style='color: #999999'>"+d.woRoom.name+"<span>");


        return row;
    },
});
