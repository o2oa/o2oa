MWF.xApplication.Meeting.MeetingView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "/x_component_Meeting/$MeetingView/";
        this.cssPath = "/x_component_Meeting/$MeetingView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.days = [];
        this.load();
    },
    load: function(){
        this.date = new Date();
        this.node = new Element("div#meetingNode", {"styles": this.css.node}).inject(this.container);
        this.topNode = new Element("div", {"styles": this.css.topNode}).inject(this.node);
        this.todayNode = new Element("div", {"styles": this.css.todayNode}).inject(this.topNode);
        this.currentNode = new Element("div", {"styles": this.css.currentNode}).inject(this.topNode);

        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.bottomNode = new Element("div", {"styles": this.css.bottomNode}).inject(this.node);
        this.dayContentNode = new Element("div", {"styles": this.css.dayContentNode}).inject(this.bottomNode);

        this.setContentNodeHeight();
        this.app.addEvent("resize", this.setContentNodeHeight.bind(this));

        this.loadContent();

        this.node.addEvent("mousewheel", this.scrollBottomNode.bind(this));
    },

    scrollBottomNode: function(e){
        var delta = 1-e.event.wheelDelta;

        var scrollSize = this.bottomNode.getScrollSize();
        var scroll = this.bottomNode.getScroll();

        var size = this.bottomNode.getSize();

        var step = 0.6*(size.x.toFloat());
        delta = (delta/120)*step;

        var to = scroll.x+delta;
        if (to>scrollSize.x) to = scrollSize.x;
        if (to<0) to = 0;


        if (!this.bottomScroll) this.bottomScroll = new Fx.Scroll(this.bottomNode);
        this.bottomScroll.start(to, 0);
    },

    setContentNodeHeight: function(){
        var size = this.container.getSize();
        if (this.app.meetingConfig.hideMenu=="static"){
            var y = size.y-110;
            this.node.setStyle("height", ""+y+"px");
            this.node.setStyle("margin-top", "60px");
        }

        var size = this.node.getSize();
        var topSize = this.topNode.getSize();
        var bottomSize = this.bottomNode.getSize();
        var y = size.y - topSize.y - bottomSize.y-10;

        this.contentNode.setStyle("height", ""+y+"px");
    },

    loadContent: function(){
        var d = this.date.format(this.app.lp.dateFormatMonthDay);
        var w = this.app.lp.weeks.arr[this.date.getDay()];

        this.todayNode.set("text", d+","+w);

        this.app.actions.listMeetingDays(7, function(json){
            this.currentNode.set("text", this.app.lp.noMeetingWeek);
            for (i=0; i<json.data.length; i++){
                var data = json.data[i];
                if (data.status == "wait" && !data.myWaitConfirm && !data.myWaitAccept && !data.myReject){

                    var timeStr = "";
                    var now = new Date();
                    var mDate = Date.parse(data.startTime);
                    var d = now.diff(mDate);
                    if (d==0){
                        timeStr = Date.parse(data.startTime).format("%H:%M");
                    }else if (d==1){
                        timeStr = this.app.lp.tomorrow+Date.parse(data.startTime).format("%H:%M");
                    }else if (d==2){
                        timeStr = this.app.lp.afterTomorrow+Date.parse(data.startTime).format("%H:%M");
                    }else{
                        var m = now.diff(mDate, "month");
                        if (m==0){
                            var w = this.app.lp.weeks.arr[Date.parse(data.startTime).getDay()];
                            timeStr = Date.parse(data.startTime).format(this.app.lp.dateFormatDayOnly)+"("+w+")"+Date.parse(data.startTime).format("%H:%M");
                        }else if (m==1){
                            timeStr = this.app.lp.nextMonth+Date.parse(data.startTime).format(this.app.lp.dateFormatDayOnly);
                        }else{
                            timeStr = Date.parse(data.startTime).format(this.app.lp.dateFormatMonthOnly);
                        }
                    }
                    var text = this.app.lp.newlyMeeting.replace(/{time}/g, timeStr);
                    text = text.replace(/{name}/g, data.subject);


                    this.app.actions.getRoom(data.room, function(roomJson){
                        this.app.actions.getBuilding(roomJson.data.building, function(buildingJson){
                            var addr = "";
                            if (roomJson.data.roomNumber){
                                addr = roomJson.data.name+" ("+buildingJson.data.name+" #"+roomJson.data.roomNumber+") ";
                            }else{
                                addr = roomJson.data.name+" ("+buildingJson.data.name+") ";
                            }
                            text = text.replace(/{room}/g, addr);
                            this.currentNode.set("text", text);
                        }.bind(this));
                    }.bind(this));
                    break;
                }
            }
        }.bind(this));

        this.loadDays();
    },
    loadDays: function(){
        var date = this.date.clone();
        for (var i=1; i<=30; i++){
            this.days.push(new MWF.xApplication.Meeting.MeetingView.Day(this, date));
            date.increment();
        }
        //this.checkDayContentWidth();
    },
    //checkDayContentWidth: function(){
    //
    //
    //},

    hide: function(){
        var fx = new Fx.Morph(this.node, {
            "duration": "300",
            "transition": Fx.Transitions.Expo.easeOut
        });
        if (this.currentDocument) this.currentDocument.closeDocument();
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
        debugger;
        this.days.each(function(day){
            day.destroy();
        }.bind(this));
        //this.node.destroy();
        this.days = [];
        this.loadContent();
    }

});

MWF.xApplication.Meeting.MeetingView.Day = new Class({
    Implements: [Events],
    initialize: function(view, date){
        this.view = view
        this.css = this.view.css;
        this.container = this.view.dayContentNode;
        this.app = this.view.app;
        this.date = date.clone();
        this.meetings = [];
        this.load();
    },
    load: function(){
        var dateStr = "";
        if (this.date.diff((new Date()))==0){
            dateStr = this.app.lp.today;
        }else{
            var d = this.date.format(this.app.lp.dateFormatDay);
            var w = this.app.lp.weeks.arr[this.date.getDay()];
            dateStr = d+", "+w;
        }

        this.node = new Element("div", {"styles": this.css.dayNode}).inject(this.container);
        this.dateNode = new Element("div", {"styles": this.css.dayDateNode, "text": dateStr}).inject(this.node);
        this.dayBodyNode = new Element("div", {"styles": this.css.dayBodyNode}).inject(this.node);

        this.dateNode.addEvent("click", function(){
            //this.app.addMeeting(this.date);
            this.app.toDay(this.date);
        }.bind(this));
        this.loadMeetings();
    },
    loadMeetings: function(){
        var y = this.date.getFullYear();
        var m = this.date.getMonth()+1;
        var d = this.date.getDate();
        var meetingCount = 0;
        var taskCount = 0;
        var meetings = [];
        this.app.actions.listMeetingDay(y, m, d, function(json){
            json.data.each(function(meeting){
                if (!meeting.myReject){
                    if (meeting.myWaitConfirm || meeting.myWaitAccept){
                        taskCount++;
                        this.meetings.push(new MWF.xApplication.Meeting.MeetingView.Day.Task(this, meeting));
                    }else{
                        meetingCount++;
                        meetings.unshift(meeting);
                    }
                }
            }.bind(this));
            meetings.each(function(meeting){
                this.meetings.push(new MWF.xApplication.Meeting.MeetingView.Day.Meeting(this, meeting));
            }.bind(this));


            if (meetingCount>0){
                var size = this.container.getSize();
                var addWidth = (meetingCount*350);
                var w = size.x+addWidth;
                this.container.setStyle("width", ""+w+"px");
            }

            if (taskCount==0){
                var node = new Element("div", {
                    "styles": {
                        "line-height": "60px",
                        "font-size": "18px",
                        "color": "#DDD",
                        "padding": "0px 20px"
                    }
                }).inject(this.dayBodyNode);
                if (meetingCount==0){
                    node.set("text", this.app.lp.noMeeting);
                }else{
                    node.set("text", this.app.lp.noTask);
                }
            }
        }.bind(this));
    },
    destroy: function(){
        this.meetings.each(function(meeting){
            meeting.destroy();
        }.bind(this));
        this.node.destroy();
        MWF.release(this);
    }
});

MWF.xApplication.Meeting.MeetingView.Day.Meeting = new Class({
    initialize: function(day, data){
        this.day = day;
        this.data = data;
        this.view = this.day.view
        this.css = this.view.css;
        this.app = this.view.app;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.dayMeetingNode}).inject(this.day.node, "after");
        this.titleNode = new Element("div", {"styles": this.css.dayMeetingTitleNode}).inject(this.node);
        this.timeNode = new Element("div", {"styles": this.css.dayMeetingTimeNode}).inject(this.node);

        this.descriptionNode = new Element("div", {"styles": this.css.dayMeetingDescriptionNode}).inject(this.node);

        var timeStr = Date.parse(this.data.startTime).format("%H:%M")+" - "+Date.parse(this.data.completedTime).format("%H:%M");
        this.timeNode.set("text", timeStr);

        this.titleNode.set("text", this.data.subject);

        this.descriptionNode.set("text", this.data.description);

        this.node.addEvent("click", this.openDocument.bind(this));

        switch (this.data.status){
            case "wait":
                //nothing
                break;
            case "processing":
                this.node.setStyles({
                    "border-left": "10px solid #18da14",
                    "background-color": "#deffdd"
                });
                break
            case "completed":
                //add attachment
                this.node.setStyles({
                    "border-left": "10px solid #333",
                    "background-color": "#ccc"
                });
                break;
        }
    },
    openDocument: function(){
        if (!this.document){
            debugger;
            if (this.view.currentDocument) this.view.currentDocument.closeDocument();
            this.document = new MWF.xApplication.Meeting.MeetingView.Document(this);
            this.view.currentDocument = this.document;
        }
    },
    destroy: function(){
        if (this.document) this.document.closeDocument();
        this.node.destroy();
        MWF.release(this);
    }
});

MWF.xApplication.Meeting.MeetingView.Day.Task = new Class({
    initialize: function(day, data){
        this.day = day;
        this.data = data;
        this.view = this.day.view
        this.css = this.view.css;
        this.container = this.day.dayBodyNode;
        this.app = this.view.app;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.dayTaskNode}).inject(this.container);
        this.iconNode = new Element("div", {"styles": this.css.dayTaskIconNode}).inject(this.node);
        this.timeNode = new Element("div", {"styles": this.css.dayTaskTimeNode}).inject(this.node);
        this.titleNode = new Element("div", {"styles": this.css.dayTaskTitleNode}).inject(this.node);

        var timeStr = Date.parse(this.data.startTime).format("%H:%M")+" - "+Date.parse(this.data.completedTime).format("%H:%M");
        this.timeNode.set("text", timeStr);

        var type;
        if (this.data.myWaitConfirm) type = this.app.lp.myWaitConfirm;
        if (this.data.myWaitAccept) type = this.app.lp.myWaitAccept

        this.titleNode.set("text", "["+type+"]"+this.data.subject);

        this.node.addEvent("click", this.openDocument.bind(this));
    },
    openDocument: function(){
        if (!this.document){
            debugger;
            if (this.view.currentDocument) this.view.currentDocument.closeDocument();
            this.document = new MWF.xApplication.Meeting.MeetingView.Document(this);
            this.view.currentDocument = this.document;
        }
    },
    destroy: function(){
        if (this.document) this.document.closeDocument();
        this.node.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.Meeting.MeetingView.Document = new Class({
    initialize: function(item){
        this.item = item;
        this.data = this.item.data;
        this.view = this.item.view
        this.css = this.item.css;
        this.container = this.view.contentNode;
        this.app = this.view.app;
        this.isEdit = (this.data.applicant == this.app.desktop.session.user.name);
        this.load();
    },
    load: function(){
        this.createNode();
        this.show();
        this.loadContent();
    },
    show: function(){
        var o = this.getNodeCoordinates();

        var fx = new Fx.Morph(this.node, {
            "duration": "500",
            "transition": Fx.Transitions.Expo.easeOut
        });
        fx.start({
            "opacity": 1,
            "width": ""+ o.width+"px",
            "height": ""+ o.height+"px",
            "left": ""+ o.left+"px",
            "top": ""+ o.top+"px",
        }).chain(function(){
            this.setNodeSizeFun = this.setNodeSize.bind(this);
            this.view.app.addEvent("resize", this.setNodeSizeFun);
        }.bind(this));
    },
    setNodeSize: function(){
        var o = this.getNodeCoordinates();
        this.node.setStyles({
            "width": ""+ o.width+"px",
            "height": ""+ o.height+"px",
            "left": ""+ o.left+"px",
            "top": ""+ o.top+"px"
        });
    },
    getNodeCoordinates: function(){
        var size = this.container.getSize();
        var w = size.x*0.7;
        if (w<800) w = 800;
        var h = size.y*0.8;
        if (h<300) h = 300;
        var position = this.container.getPosition(this.container.getOffsetParent());
        var l = size.x/2-w/2;
        if (l<0) l=0;
        l = position.x+l;
        var t = size.y/2-h/2;
        if (t<0) t=0;
        t = position.y+t;

        return {
            "width": w,
            "height": h,
            "left": l,
            "top": t
        }
    },
    createNode: function(){
        var size = this.item.node.getSize();
        this.node = new Element("div", {
            "styles": {
                "width": ""+size.x+"px",
                "height": ""+size.y+"px",
                "background-color": "#FFF",
                "opacity": 0,
                "z-index": 100,
                "border": "1px solid #999",
                "position": "absolute"
            }
        }).inject(this.container);

        this.node.position({
            relativeTo: this.item.node,
            position: 'topLeft',
            edge: 'topLeft'
        });
    },
    loadContent: function(){
        this.infoNode = new Element("div", {"styles": this.css.documentContentInfoNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.documentContentNode}).inject(this.node);

        this.loadInfos();
        this.loadBody();
    },
    loadBody: function(){
        this.loadActions();
        this.loadSubject();
        this.loadDescription();
        this.loadAttachment();

        this.setDescriptionNodeSize();
        this.setDescriptionNodeSizeFun = this.setDescriptionNodeSize.bind(this);
        this.app.addEvent("resize", this.setDescriptionNodeSizeFun);

        if (this.isEdit){
            this.editDocument();
        }
    },
    editDocument: function(){
        switch (this.data.status){
            case "wait":
                //edit subject, description, attachment, add person
                this.addSaveAction();

                this.subjectNode.empty();
                this.subjectInput = new Element("input", {"styles": this.css.documentContentSubjectInputNode, "type": "text", "value": this.data.subject}).inject(this.subjectNode);

                this.descriptionNode.empty();
                this.descriptionInput = new Element("textarea", {"styles": this.css.documentContentDescriptionInputNode, "value": this.data.description}).inject(this.descriptionNode);

                if (this.attachmentController){
                    this.attachmentController.setOptions({"readonly": false});
                    this.attachmentController.checkActions();
                }else{
                    this.isEditAttachment = true;
                }
                this.loadAddInvite();
                break;
            case "processing":
                //nothing
                break
            case "completed":
                //add attachment
                if (this.attachmentController){
                    this.attachmentController.attachments.each(function(att){
                        att.isDelete = false;
                    }.bind(this));
                    this.attachmentController.setOptions({"readonly": false});
                    this.attachmentController.checkActions();
                }else{
                    this.isEditCompletedAttachment = true;
                }
                break;
        }
    },

    addSaveAction: function(){
        this.saveNode = new Element("div", {"styles": this.css.documentTopSaveNode}).inject(this.closeNode, "after");
        this.saveNode.addEvents({
            "mouseover": function(){this.saveNode.setStyles(this.css.documentTopSaveNode_over);}.bind(this),
            "mouseout": function(){this.saveNode.setStyles(this.css.documentTopSaveNode);}.bind(this),
            "mousedown": function(){this.saveNode.setStyles(this.css.documentTopSaveNode_down);}.bind(this),
            "mouseup": function(){this.saveNode.setStyles(this.css.documentTopSaveNode_over);}.bind(this),
            "click": function(e){this.saveDocument();}.bind(this)
        });
    },
    saveDocument: function(noNotice, notClose){
        if (this.subjectInput && this.descriptionInput){
            var subject = this.subjectInput.get("value");
            var description = this.descriptionInput.get("value");
            if (!subject){
                if (!noNotice) this.app.notice(this.app.lp.meeting_input_subject_error, "error", this.node, {"x": "right", "y": "top"});
                return false;
            }

            this.data.subject = subject;
            this.data.description = description;
            this.app.actions.saveMeeting(this.data, function(){
                if (!noNotice) this.app.notice(this.app.lp.meeting_saveSuccess, "success", this.node, {"x": "right", "y": "top"});
                if (!notClose){
                    var view = this.view;
                    this.closeDocument(function(){
                        view.reload();
                    }.bind(this));
                }else{
                    this.view.reload();
                }
            }.bind(this));
        }
    },
    setDescriptionNodeSize: function(){
        var o = this.getNodeCoordinates();

        var topSize = this.topNode.getSize();
        var subjectSize = this.subjectNode.getSize();
        var attachmentSize = this.attachmentNode.getSize();
        var y = o.height-topSize.y-subjectSize.y-attachmentSize.y-4;

        this.descriptionNode.setStyle("height", ""+y+"px");
    },
    loadActions: function(){
        this.topNode = new Element("div", {"styles": this.css.documentTopNode}).inject(this.contentNode);
        this.closeNode = new Element("div", {"styles": this.css.documentTopCloseNode}).inject(this.topNode);
        this.closeNode.addEvents({
            "mouseover": function(){this.closeNode.setStyles(this.css.documentTopCloseNode_over);}.bind(this),
            "mouseout": function(){this.closeNode.setStyles(this.css.documentTopCloseNode);}.bind(this),
            "mousedown": function(){this.closeNode.setStyles(this.css.documentTopCloseNode_down);}.bind(this),
            "mouseup": function(){this.closeNode.setStyles(this.css.documentTopCloseNode_over);}.bind(this),
            "click": function(e){this.closeDocument();}.bind(this)
        });
    },
    closeDocument: function(callback){
        //this.saveDocument(true, true);

        if (this.setDescriptionNodeSizeFun) this.app.removeEvent("resize", this.setDescriptionNodeSizeFun);
        if (this.setNodeSizeFun) this.app.removeEvent("resize", this.setNodeSizeFun);

        var size = this.item.node.getSize();
        var position = this.item.node.getPosition(this.item.node.getOffsetParent());

        var fx = new Fx.Morph(this.node, {
            "duration": "500",
            "transition": Fx.Transitions.Expo.easeOut
        });
        this.node.empty();
        this.view.currentDocument = null;
        fx.start({
            "opacity": 0,
            "width": ""+ size.x+"px",
            "height": ""+ size.y+"px",
            "left": ""+ position.x+"px",
            "top": ""+ position.y+"px",
        }).chain(function(){
            this.destroy();
            if (callback) callback();
        }.bind(this));
    },
    destroy: function(){
        if (this.setDescriptionNodeSizeFun) this.app.removeEvent("resize", this.setDescriptionNodeSizeFun);
        if (this.setNodeSizeFun) this.app.removeEvent("resize", this.setNodeSizeFun);
        this.node.destroy();
        this.item.document = null;
        MWF.release(this);
        delete this;
    },

    loadSubject: function(){
        this.subjectNode = new Element("div", {"styles": this.css.documentContentSubjectNode}).inject(this.topNode);
        this.subjectNode.set("text", this.data.subject);
    },
    loadDescription: function(){
        this.descriptionNode = new Element("div", {"styles": this.css.documentContentDescriptionNode}).inject(this.contentNode);
        this.descriptionNode.set("text", this.data.description);

    },
    loadAttachment: function(){
        this.attachmentNode = new Element("div", {"styles": this.css.documentContentAttachmentnNode}).inject(this.contentNode);
        MWF.require("MWF.widget.AttachmentController", function(){

            var option = {"size": "min", "isSizeChange": false, "isReplace": false, "readonly": true};
            if (this.isEditAttachment || this.isEditCompletedAttachment){
                option = {"size": "min", "isSizeChange": false, "isReplace": false, "readonly": false};
            }

            this.attachmentController = new MWF.widget.AttachmentController(this.attachmentNode, this, option);
            this.attachmentController.load();
            this.data.attachmentList.each(function (att) {
                var att = this.attachmentController.addAttachment(att);
                if (this.isEditCompletedAttachment){
                    att.isDelete = false;
                }
            }.bind(this));
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
                        debugger;
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
        this.app.confirm("warn", e, this.lp.deleteAttachmentTitle, this.lp.deleteAttachment+"( "+names.join(", ")+" )", 300, 120, function(){
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

    loadInfos: function(){
        //var title = this.app.lp.meetingApply.replace(/{person}/g, this.data.applicant);
        var title = this.app.lp.meetingApply.replace(/{person}/g, "<div></div>");
        if (this.data.applicant == this.app.desktop.session.user.name){
            title = this.app.lp.myMeetingApply;
        }

        if (this.data.status!="completed" && !this.data.myReject){
            if (this.data.myWaitConfirm){
                title = title+"<br/>"+this.app.lp.waitConfirm;
            }else if (this.data.myWaitAccept){
                title = title+"<br/>"+this.app.lp.waitAccept;
            }else if (this.data.applicant == this.app.desktop.session.user.name){
                title = this.app.lp.myMeetingApply+"<br/>"+this.app.lp.attend;
            }else if (this.data.acceptPersonList.indexOf(this.app.desktop.session.user.name)!=-1){
                title = title+"<br/>"+this.app.lp.isAccept;
            }
        }

        this.infoTitleNode = new Element("div", {"styles": this.css.documentInfoTitleNode}).inject(this.infoNode);
        var infoTitleTextNode = new Element("div", {"styles": this.css.documentInfoTitleTextNode, "html": title}).inject(this.infoTitleNode);
        var personNode = infoTitleTextNode.getElement("div");
        if (personNode){
            var explorer = {
                "actions": this.app.personActions,
                "app": {
                    "lp": this.app.lp
                }
            }
            MWF.require("MWF.widget.Identity", function(){
                var person = new MWF.widget.Person({"name": this.data.applicant}, personNode, explorer, false, null, {"style": "meetingApply"});;
            }.bind(this));

        }


        if (this.data.myWaitConfirm) this.createConfirmActions();
        if (this.data.myWaitAccept) this.createAcceptActions();
        if (this.data.status=="wait" && this.isEdit) this.createCancelActions();

        this.loadDate();
        this.loadTime();
        //this.loadEndTime();
        this.loadRoom();
        this.loadInvite();
    },


    createConfirmActions: function(){
        var infoTitleActionNode = new Element("div", {"styles": this.css.documentInfoTitleActionNode}).inject(this.infoTitleNode);
        this.disagreeAction =  new Element("div", {"styles": this.css.documentInfoActionNode}).inject(infoTitleActionNode);
        this.disagreeActionIcon =  new Element("div", {"styles": this.css.documentInfoActionIconRejectNode}).inject(this.disagreeAction);
        this.disagreeActionText =  new Element("div", {"styles": this.css.documentInfoActionTextNode, "text": this.app.lp.reject}).inject(this.disagreeAction);

        this.agreeAction =  new Element("div", {"styles": this.css.documentInfoActionNode}).inject(infoTitleActionNode);
        this.agreeActionIcon =  new Element("div", {"styles": this.css.documentInfoActionIconAcceptNode}).inject(this.agreeAction);
        this.agreeActionText =  new Element("div", {"styles": this.css.documentInfoActionTextNode, "text": this.app.lp.accept}).inject(this.agreeAction);

        this.disagreeAction.addEvents({
            "mouseover": function(){
                this.disagreeAction.setStyles(this.css.documentInfoActionNode_over);
                this.disagreeActionIcon.setStyles(this.css.documentInfoActionIconRejectNode);
            }.bind(this),
            "mouseout": function(){
                this.disagreeAction.setStyles(this.css.documentInfoActionNode);
                this.disagreeActionIcon.setStyles(this.css.documentInfoActionIconRejectNode);
            }.bind(this),
            "mousedown": function(){
                this.disagreeAction.setStyles(this.css.documentInfoActionNode_down);
                this.disagreeActionIcon.setStyles(this.css.documentInfoActionIconRejectNode_down);
            }.bind(this),
            "mouseup": function(){
                this.disagreeAction.setStyles(this.css.documentInfoActionNode_over);
                this.disagreeActionIcon.setStyles(this.css.documentInfoActionIconRejectNode);
            }.bind(this),
            "click": function(e){this.disagree(e);}.bind(this)
        });
        this.agreeAction.addEvents({
            "mouseover": function(){
                this.agreeAction.setStyles(this.css.documentInfoActionNode_over);
                this.agreeActionIcon.setStyles(this.css.documentInfoActionIconAcceptNode);
            }.bind(this),
            "mouseout": function(){
                this.agreeAction.setStyles(this.css.documentInfoActionNode);
                this.agreeActionIcon.setStyles(this.css.documentInfoActionIconAcceptNode);
            }.bind(this),
            "mousedown": function(){
                this.agreeAction.setStyles(this.css.documentInfoActionNode_down);
                this.agreeActionIcon.setStyles(this.css.documentInfoActionIconAcceptNode_down);
            }.bind(this),
            "mouseup": function(){
                this.agreeAction.setStyles(this.css.documentInfoActionNode_over);
                this.agreeActionIcon.setStyles(this.css.documentInfoActionIconAcceptNode);
            }.bind(this),
            "click": function(e){this.agree(e);}.bind(this)
        });
    },
    createAcceptActions: function(){
        var infoTitleActionNode = new Element("div", {"styles": this.css.documentInfoTitleActionNode}).inject(this.infoTitleNode);
        this.rejectAction =  new Element("div", {"styles": this.css.documentInfoActionNode}).inject(infoTitleActionNode);
        this.rejectActionIcon =  new Element("div", {"styles": this.css.documentInfoActionIconRejectNode}).inject(this.rejectAction);
        this.rejectActionText =  new Element("div", {"styles": this.css.documentInfoActionTextNode, "text": this.app.lp.reject}).inject(this.rejectAction);

        this.acceptAction =  new Element("div", {"styles": this.css.documentInfoActionNode}).inject(infoTitleActionNode);
        this.acceptActionIcon =  new Element("div", {"styles": this.css.documentInfoActionIconAcceptNode}).inject(this.acceptAction);
        this.acceptActionText =  new Element("div", {"styles": this.css.documentInfoActionTextNode, "text": this.app.lp.accept}).inject(this.acceptAction);

        this.rejectAction.addEvents({
            "mouseover": function(){
                this.rejectAction.setStyles(this.css.documentInfoActionNode_over);
                this.rejectActionIcon.setStyles(this.css.documentInfoActionIconRejectNode);
            }.bind(this),
            "mouseout": function(){
                this.rejectAction.setStyles(this.css.documentInfoActionNode);
                this.rejectActionIcon.setStyles(this.css.documentInfoActionIconRejectNode);
            }.bind(this),
            "mousedown": function(){
                this.rejectAction.setStyles(this.css.documentInfoActionNode_down);
                this.rejectActionIcon.setStyles(this.css.documentInfoActionIconRejectNode_down);
            }.bind(this),
            "mouseup": function(){
                this.rejectAction.setStyles(this.css.documentInfoActionNode_over);
                this.rejectActionIcon.setStyles(this.css.documentInfoActionIconRejectNode);
            }.bind(this),
            "click": function(e){this.reject(e);}.bind(this)
        });
        this.acceptAction.addEvents({
            "mouseover": function(){
                this.acceptAction.setStyles(this.css.documentInfoActionNode_over);
                this.acceptActionIcon.setStyles(this.css.documentInfoActionIconAcceptNode);
            }.bind(this),
            "mouseout": function(){
                this.acceptAction.setStyles(this.css.documentInfoActionNode);
                this.acceptActionIcon.setStyles(this.css.documentInfoActionIconAcceptNode);
            }.bind(this),
            "mousedown": function(){
                this.acceptAction.setStyles(this.css.documentInfoActionNode_down);
                this.acceptActionIcon.setStyles(this.css.documentInfoActionIconAcceptNode_down);
            }.bind(this),
            "mouseup": function(){
                this.acceptAction.setStyles(this.css.documentInfoActionNode_over);
                this.acceptActionIcon.setStyles(this.css.documentInfoActionIconAcceptNode);
            }.bind(this),
            "click": function(e){this.accept(e);}.bind(this)
        });
    },
    createCancelActions: function(){
        var infoTitleActionNode = new Element("div", {"styles": this.css.documentInfoTitleActionNode}).inject(this.infoTitleNode);

        this.cancelAction =  new Element("div", {"styles": this.css.documentInfoActionNode}).inject(infoTitleActionNode);
        this.cancelActionIcon =  new Element("div", {"styles": this.css.documentInfoActionIconAcceptNode}).inject(this.cancelAction);
        this.cancelActionText =  new Element("div", {"styles": this.css.documentInfoActionTextNode, "text": this.app.lp.cancelMeeting}).inject(this.cancelAction);
        this.cancelAction.setStyle("width", "80px");

        this.cancelAction.addEvents({
            "mouseover": function(){
                this.cancelAction.setStyles(this.css.documentInfoActionNode_over);
                this.cancelActionIcon.setStyles(this.css.documentInfoActionIconRejectNode);
            }.bind(this),
            "mouseout": function(){
                this.cancelAction.setStyles(this.css.documentInfoActionNode);
                this.cancelAction.setStyle("width", "80px");
                this.cancelActionIcon.setStyles(this.css.documentInfoActionIconRejectNode);
            }.bind(this),
            "mousedown": function(){
                this.cancelAction.setStyles(this.css.documentInfoActionNode_down);
                this.cancelActionIcon.setStyles(this.css.documentInfoActionIconRejectNode_down);
            }.bind(this),
            "mouseup": function(){
                this.cancelAction.setStyles(this.css.documentInfoActionNode_over);
                this.cancelActionIcon.setStyles(this.css.documentInfoActionIconRejectNode);
            }.bind(this),
            "click": function(e){this.cancel(e);}.bind(this)
        });
    },
    cancel: function(e){
        var _self = this;
        var text = this.app.lp.cancel_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.cancel_confirm_title, text, 380, 150, function(){
            _self.cancelMeeting();
            this.close();
        }, function(){
            this.close();
        });
    },
    cancelMeeting: function(){
        this.app.actions.deleteMeeting(this.data.id, function(){
            var view = this.view;
            this.closeDocument(function(){
                view.reload();
            }.bind(this));
        }.bind(this))
    },


    reject: function(e){
        var _self = this;
        var text = this.app.lp.reject_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.reject_confirm_title, text, 300, 120, function(){
            _self.rejectMeeting();
            this.close();
        }, function(){
            this.close();
        });
    },
    rejectMeeting: function(){
        this.app.actions.rejectMeeting(this.data.id, function(){
            var view = this.view;
            this.closeDocument(function(){
                view.reload();
            }.bind(this));
        }.bind(this))
    },

    accept: function(e){
        var _self = this;
        var text = this.app.lp.accept_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.accept_confirm_title, text, 300, 120, function(){
            _self.acceptMeeting();
            this.close();
        }, function(){
            this.close();
        });
    },
    acceptMeeting: function(){
        this.app.actions.acceptMeeting(this.data.id, function(){
            var view = this.view;
            this.closeDocument(function(){
                view.reload();
            }.bind(this));
        }.bind(this))
    },

    disagree: function(e){
        var _self = this;
        var text = this.app.lp.disagree_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.disagree_confirm_title, text, 300, 120, function(){
            _self.disagreeMeeting();
            this.close();
        }, function(){
            this.close();
        });
    },
    disagreeMeeting: function(){
        this.app.actions.denyMeeting(this.data.id, function(){
            var view = this.view;
            this.closeDocument(function(){
                view.reload();
            }.bind(this));
        }.bind(this))
    },

    agree: function(e){
        var _self = this;
        var text = this.app.lp.agree_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.agree_confirm_title, text, 300, 120, function(){
            _self.agreeMeeting();
            this.close();
        }, function(){
            this.close();
        });
    },
    agreeMeeting: function(){
        this.app.actions.allowMeeting(this.data.id, function(){
            var view = this.view;
            this.closeDocument(function(){
                view.reload();
            }.bind(this));
        }.bind(this))
    },

    loadDate: function(){
        var lineNode = new Element("div", {"styles": this.css.documentInfoLineNode}).inject(this.infoNode);
        var texNode = new Element("div", {"styles": this.css.documentInfoTextNode, "text": this.app.lp.beginDate}).inject(lineNode);
        var bodyNode = new Element("div", {"styles": this.css.documentInfoBodyNode}).inject(lineNode);
        var date = Date.parse(this.data.startTime).format(this.app.lp.dateFormatDay);
        bodyNode.set("text", date);
    },
    loadTime: function(){
        var lineNode = new Element("div", {"styles": this.css.documentInfoLineNode}).inject(this.infoNode);
        var texNode = new Element("div", {"styles": this.css.documentInfoTextNode, "text": this.app.lp.time}).inject(lineNode)

        var bodyNode = new Element("div", {"styles": this.css.documentInfoBody1Node}).inject(lineNode);
        var date = Date.parse(this.data.startTime).format("%H:%M");
        bodyNode.set("text", date);

        tmpNode = new Element("div", {"styles": this.css.documentInfoBody2Node, "text": "-"}).inject(lineNode);

        bodyNode = new Element("div", {"styles": this.css.documentInfoBody1Node}).inject(lineNode);
        date = Date.parse(this.data.completedTime).format("%H:%M");
        bodyNode.set("text", date);

        var m = Date.parse(this.data.startTime).diff(Date.parse(this.data.completedTime), "minute");
        var h = (m/60).toInt();
        var m = m%60;
        var rangeText = "";
        if (m==0){
            rangeText = this.app.lp.timeRangeHour.replace(/{n}/g, h);
        }else{
            rangeText = this.app.lp.timeRangeMinute.replace(/{h}/g, h).replace(/{m}/g, m);
        }
        bodyNode = new Element("div", {"styles": this.css.documentInfoBody3Node, "text": rangeText}).inject(lineNode);
    },
    loadRoom: function(){
        var lineNode = new Element("div", {"styles": this.css.documentInfoLineNode}).inject(this.infoNode);
        var texNode = new Element("div", {"styles": this.css.documentInfoTextNode, "text": this.app.lp.selectRoom}).inject(lineNode);
        var bodyNode = new Element("div", {"styles": this.css.documentInfoBodyNode}).inject(lineNode);

        this.app.actions.getRoom(this.data.room, function(roomJson){
            this.app.actions.getBuilding(roomJson.data.building, function(buildingJson){
                var text = "";
                if (roomJson.data.roomNumber){
                    text = roomJson.data.name+" ("+buildingJson.data.name+" "+roomJson.data.floor+this.app.lp.floor+" #"+roomJson.data.roomNumber+") ";
                }else{
                    text = roomJson.data.name+" ("+buildingJson.data.name+" "+roomJson.data.floor+this.app.lp.floor+") ";
                }
                bodyNode.set("text", text);
            }.bind(this));
        }.bind(this));
    },
    loadInvite: function(){
        this.inviteLineNode = new Element("div", {"styles": this.css.documentInfoLineNode}).inject(this.infoNode);
        var texNode = new Element("div", {"styles": this.css.documentInfoTextNode, "text": this.app.lp.invitePerson1}).inject(this.inviteLineNode);
        this.inviteBodyNode = new Element("div", {"styles": this.css.documentInfoBodyNode}).inject(this.inviteLineNode);

        var explorer = {
            "actions": this.app.personActions,
            "app": {
                "lp": this.app.lp
            }
        }
        MWF.require("MWF.widget.Identity", function(){
            this.data.invitePersonList.each(function(personName){
                debugger;
                var person = new MWF.widget.Person({"name": personName}, this.inviteBodyNode, explorer, false, null, {
                    "style": "room",
                    "onLoadedInfor": function(){

                        if (this.data.acceptPersonList.indexOf(personName)!=-1){
                            var acceptNode = new Element("div", {"styles": this.css.documentInfoAcceptIconNode}).inject(person.node, "top");
                            new Element("div", {
                                "styles": this.css.documentInfoAcceptTextNode,
                                "text": this.app.lp.accepted
                            }).inject(person.inforNode);
                        }
                        if (this.data.rejectPersonList.indexOf(personName)!=-1){
                            var rejectNode = new Element("div", {"styles": this.css.documentInfoRejectIconNode}).inject(person.node, "top");
                            new Element("div", {
                                "styles": this.css.documentInfoRejectTextNode,
                                "text": this.app.lp.rejected
                            }).inject(person.inforNode);
                        }
                    }.bind(this)
                });
            }.bind(this));
        }.bind(this));
    },
    loadAddInvite: function(){
        this.addInviteNode = new Element("div", {"styles": this.css.documentAddInviteActionNode}).inject(this.inviteLineNode);
        this.addInviteIconNode = new Element("div", {"styles": this.css.documentAddInviteActionIconNode}).inject(this.addInviteNode);
        this.addInviteTextNode = new Element("div", {"styles": this.css.documentAddInviteActionTextNode, "text": this.app.lp.addInvitePerson1}).inject(this.addInviteNode);

        this.addInviteNode.addEvents({
            "mouseover": function(){
                this.addInviteNode.setStyles(this.css.documentAddInviteActionNode_over);
                this.addInviteIconNode.setStyles(this.css.documentAddInviteActionIconNode);
            }.bind(this),
            "mouseout": function(){
                this.addInviteNode.setStyles(this.css.documentAddInviteActionNode);
                this.addInviteIconNode.setStyles(this.css.documentAddInviteActionIconNode);
            }.bind(this),
            "mousedown": function(){
                this.addInviteNode.setStyles(this.css.documentAddInviteActionNode_down);
                this.addInviteIconNode.setStyles(this.css.documentAddInviteActionIconNode_down);
            }.bind(this),
            "mouseup": function(){
                this.addInviteNode.setStyles(this.css.documentAddInviteActionNode_over);
                this.addInviteIconNode.setStyles(this.css.documentAddInviteActionIconNode);
            }.bind(this)
        });
        var explorer = {
            "actions": this.app.personActions,
            "app": {
                "lp": this.app.lp
            }
        }

        MWF.xDesktop.requireApp("Organization", "Selector.package", function(){
            this.addInviteNode.addEvents({
                "click": function(){
                    var options = {
                        "type": "person",
                        "names": [],
                        "count": 0,
                        "onComplete": function(items){
                            MWF.require("MWF.widget.Identity", function(){
                                var invitePersonList = [];
                                items.each(function(item){
                                    var _self = this;
                                    var person = new MWF.widget.Person(item.data, this.inviteBodyNode, explorer, false, null, {"style": "room"});
                                    invitePersonList.push(item.data.name);
                                }.bind(this));

                                this.app.actions.addMeetingInvite({"invitePersonList": invitePersonList, "id": this.data.id}, function(json){
                                    this.app.actions.getMeeting(json.data.id, function(meeting){
                                        this.data.invitePersonList = meeting.data.invitePersonList;
                                        this.app.notice(this.app.lp.addedInvitePerson1, "success", this.node, {"x": "left", "y": "top"});
                                    }.bind(this));
                                }.bind(this));

                            }.bind(this));
                        }.bind(this)
                    };
                    var selector = new MWF.OrgSelector(this.app.content, options);
                }.bind(this)
            });
        }.bind(this));
    }

});