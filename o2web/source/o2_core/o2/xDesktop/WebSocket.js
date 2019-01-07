MWF.xDesktop = MWF.xDesktop || {};
MWF.xApplication = MWF.xApplication || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);

MWF.xDesktop.WebSocket = new Class({
    Implements: [Options, Events],
    options: {},
    initialize: function(options){
        var addressObj = layout.desktop.serviceAddressList["x_collaboration_assemble_websocket"];
        this.ws = "ws://"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context+"/ws/collaboration";
        //var ws = "ws://hbxa01.bf.ctc.com/x_collaboration_assemble_websocket/ws/collaboration";

        this.ws = this.ws+"?x-token="+encodeURIComponent(Cookie.read("x-token"))+"&authorization="+encodeURIComponent(Cookie.read("x-token"));

        this.webSocket = new WebSocket(this.ws);
        this.webSocket.onopen = function (e){this.onOpen(e);}.bind(this);
        this.webSocket.onclose = function (e){this.onClose(e);}.bind(this);
        this.webSocket.onmessage = function (e){this.onMessage(e);}.bind(this);
        this.webSocket.onerror = function (e){this.onError(e);}.bind(this);
    },
    onOpen: function(e){
       //MWF.xDesktop.notice("success", {"x": "right", "y": "top"}, "websocket is open ...");
    },
    onClose: function(e){
       //MWF.xDesktop.notice("success", {"x": "right", "y": "top"}, "websocket is closed ...");
    },
    onMessage: function(e){
        if (e.data){
            try{
                var data = JSON.decode(e.data);
                switch (data.category){
                    case "dialog":
                        switch (data.type){
                            case "text":
                                this.receiveChatMessage(data);
                                break;
                            default:
                        }
                        break;
                    default:
                        switch (data.type){
                            case "task":
                                this.receiveTaskMessage(data);
                                break;
                            case "read":
                                this.receiveReadMessage(data);
                                break;
                            case "review":
                                this.receiveReviewMessage(data);
                                break;
                            case "fileEditor":
                                this.receiveFileEditorMessage(data);
                                break;
                            case "fileShare":
                                this.receiveFileShareMessage(data);
                                break;
                            case "meetingInvite":
                                this.receiveMeetingInviteMessage(data);
                                break;
                            case "meetingCancel":
                                this.receiveMeetingCancelMessage(data);
                                break;
                            case "meetingAccept":
                                this.receiveMeetingAcceptMessage(data);
                                break;
                            case "meetingReject":
                                this.receiveMeetingRejectMessage(data);
                                break;
                            case "attendanceAppealInvite":
                                this.receiveAttendanceAppealInviteMessage(data);
                                break;
                            case "attendanceAppealAccept":
                                this.receiveAttendanceAppealAcceptMessage(data);
                                break;
                            case "attendanceAppealReject":
                                this.receiveAttendanceAppealRejectMessage(data);
                                break;
                            default:
                        }
                }
            }catch(e){}
        }
    },
    onError: function(e){

    },
    close: function(){
        this.webSocket.close();
    },
    send: function(msg){
        if (!this.webSocket || this.webSocket.readyState != 1) {
            this.initialize();
        }
        try{
            this.webSocket.send(JSON.encode(msg));
        }catch(e){
            this.initialize();
            this.webSocket.send(JSON.encode(msg));
        }

    },

    receiveChatMessage: function(data){
        if (layout.desktop.widgets["IMIMWidget"]) layout.desktop.widgets["IMIMWidget"].receiveChatMessage(data);
        //if (layout.desktop.top.userPanel) layout.desktop.top.userPanel.receiveChatMessage(data);
    },
    receiveTaskMessage: function(data){
        //data.task
        var action = new MWF.xDesktop.Actions.RestActions("/res/mwf4/package/xDesktop/Actions/action.json", "x_processplatform_assemble_surface", "x_desktop");
        action.invoke({
            "name": "getTask",
            "parameter": {"id": data.task},
            "success": function(json){
                var task = json.data;
                var content = MWF.LP.desktop.messsage.receiveTask+"《"+task.title+"》, "+MWF.LP.desktop.messsage.activity+": <font style='color: #ea621f'>"+(task.activityName || "")+"</font>";
                content += "<br/><font style='color: #333; font-weight: bold'>"+MWF.LP.desktop.messsage.appliction+": </font><font style='color: #ea621f'>"+task.applicationName+"</font>;  "+
                "<font style='color: #333; font-weight: bold'>"+MWF.LP.desktop.messsage.process+": </font><font style='color: #ea621f'>"+task.processName+"</font>";
                var msg = {
                    "subject": MWF.LP.desktop.messsage.taskMessage,
                    "content": content
                };
                var messageItem = layout.desktop.message.addMessage(msg);
                var tooltipItem = layout.desktop.message.addTooltip(msg);
                tooltipItem.contentNode.addEvent("click", function(e){
                    layout.desktop.message.hide();
                    layout.desktop.openApplication(e, "process.TaskCenter", null, {
                        "status": {
                            "navi": "task"
                        }
                    });
                });

                messageItem.contentNode.addEvent("click", function(e){
                    layout.desktop.message.addUnread(-1);
                    layout.desktop.message.hide();
                    layout.desktop.openApplication(e, "process.TaskCenter", null, {
                        "status": {
                            "navi": "task"
                        }
                    });
                });
            }.bind(this),
            failure: function(){}
        });
    },
    receiveReadMessage: function(data){
        var action = new MWF.xDesktop.Actions.RestActions("/res/mwf4/package/xDesktop/Actions/action.json", "x_processplatform_assemble_surface", "x_desktop");
        action.invoke({
            "name": "getRead",
            "parameter": {"id": data.read},
            "success": function(json){
                var read = json.data;
                var content = MWF.LP.desktop.messsage.receiveRead+"《"+read.title+"》. ";
                content += "<br/><font style='color: #333; font-weight: bold'>"+MWF.LP.desktop.messsage.appliction+": </font><font style='color: #ea621f'>"+read.applicationName+"</font>;  "+
                "<font style='color: #333; font-weight: bold'>"+MWF.LP.desktop.messsage.process+": </font><font style='color: #ea621f'>"+read.processName+"</font>";
                var msg = {
                    "subject": MWF.LP.desktop.messsage.readMessage,
                    "content": content
                };
                var messageItem = layout.desktop.message.addMessage(msg);
                var tooltipItem = layout.desktop.message.addTooltip(msg);
                tooltipItem.contentNode.addEvent("click", function(e){
                    layout.desktop.message.hide();
                    layout.desktop.openApplication(e, "process.TaskCenter", null, {
                        "status": {
                            "navi": "read"
                        }
                    });
                });

                messageItem.contentNode.addEvent("click", function(e){
                    layout.desktop.message.addUnread(-1);
                    layout.desktop.message.hide();
                    layout.desktop.openApplication(e, "process.TaskCenter", null, {
                        "status": {
                            "navi": "read"
                        }
                    });
                });
            }.bind(this),
            failure: function(){}
        });


    },
    receiveReviewMessage: function(data){
        var content = MWF.LP.desktop.messsage.receiveReview+"《"+data.title+"》. ";
        content += "<br/><font style='color: #333; font-weight: bold'>"+MWF.LP.desktop.messsage.appliction+": </font><font style='color: #ea621f'>"+data.applicationName+"</font>;  "+
        "<font style='color: #333; font-weight: bold'>"+MWF.LP.desktop.messsage.process+": </font><font style='color: #ea621f'>"+data.processName+"</font>";
        var msg = {
            "subject": MWF.LP.desktop.messsage.reviewMessage,
            "content": content
        };
        var messageItem = layout.desktop.message.addMessage(msg);
        var tooltipItem = layout.desktop.message.addTooltip(msg);
        tooltipItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "process.TaskCenter", null, {
                "status": {
                    "navi": "review"
                }
            });
        });

        messageItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.addUnread(-1);
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "process.TaskCenter", null, {
                "status": {
                    "navi": "review"
                }
            });
        });
    },

    receiveFileEditorMessage: function(data){
        var content = "<font style='color: #ea621f; font-weight: bold'>"+data.person+"</font> "+MWF.LP.desktop.messsage.receiveFileEditor+"“"+data.name+"”. ";
        var msg = {
            "subject": MWF.LP.desktop.messsage.fileEditorMessage,
            "content": content
        };
        var messageItem = layout.desktop.message.addMessage(msg);
        var tooltipItem = layout.desktop.message.addTooltip(msg);
        tooltipItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "File", null, {
                "status": {
                    "tab": "editor",
                    "node": data.person
                }
            });
        });

        messageItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.addUnread(-1);
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "File", null, {
                "status": {
                    "tab": "editor",
                    "node": data.person
                }
            });
        });
    },

    receiveFileShareMessage: function(data){
        var content = "<font style='color: #ea621f; font-weight: bold'>"+data.person+"</font> "+MWF.LP.desktop.messsage.receiveFileShare+"“"+data.name+"”. ";
        var msg = {
            "subject": MWF.LP.desktop.messsage.fileShareMessage,
            "content": content
        };
        var messageItem = layout.desktop.message.addMessage(msg);
        var tooltipItem = layout.desktop.message.addTooltip(msg);
        tooltipItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "File", null, {
                "status": {
                    "tab": "share",
                    "node": data.person
                }
            });
        });

        messageItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.addUnread(-1);
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "File", null, {
                "status": {
                    "tab": "share",
                    "node": data.person
                }
            });
        });
    },
    getMeeting: function(id, callback){
        //this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_meeting_assemble_control", "x_component_Meeting");
        //var action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_meeting_assemble_control", "x_component_Meeting");

        MWF.Actions.get("x_meeting_assemble_control").getMeeting(id, function(json){
            var data = json.data;
            MWF.Actions.get("x_meeting_assemble_control").getRoom(data.room, function(roomJson){
                data.roomName = roomJson.data.name;
                MWF.Actions.get("x_meeting_assemble_control").getBuilding(roomJson.data.building, function(buildingJson){
                    data.buildingName = buildingJson.data.name;
                    if (callback) callback(data);
                }.bind(this));
            }.bind(this));
        }.bind(this));

    },
    receiveMeetingInviteMessage: function(data){
        this.getMeeting(data.meeting, function(meeting){
            var content = MWF.LP.desktop.messsage.meetingInvite;
            content = content.replace(/{person}/g, MWF.name.cn(meeting.applicant));
            var date = Date.parse(meeting.startTime).format("%Y-%m-%d- %H:%M");
            content = content.replace(/{date}/g, date);
            content = content.replace(/{subject}/g, meeting.subject);
            content = content.replace(/{addr}/g, meeting.roomName+"("+meeting.buildingName+")");

            var msg = {
                "subject": MWF.LP.desktop.messsage.meetingInviteMessage,
                "content": content
            };
            var messageItem = layout.desktop.message.addMessage(msg);
            var tooltipItem = layout.desktop.message.addTooltip(msg);
            tooltipItem.contentNode.addEvent("click", function(e){
                layout.desktop.message.hide();
                layout.desktop.openApplication(e, "Meeting", null);
            });

            messageItem.contentNode.addEvent("click", function(e){
                layout.desktop.message.addUnread(-1);
                layout.desktop.message.hide();
                layout.desktop.openApplication(e, "Meeting", null);
            });
        }.bind(this));
    },
    receiveMeetingCancelMessage: function(data){
        this.getMeeting(data.meeting, function(meeting){
            var content = MWF.LP.desktop.messsage.meetingCancel;
            content = content.replace(/{person}/g, MWF.name.cn(meeting.applicant));
            var date = Date.parse(meeting.startTime).format("%Y-%m-%d- %H:%M");
            content = content.replace(/{date}/g, date);
            content = content.replace(/{subject}/g, meeting.subject);
            content = content.replace(/{addr}/g, meeting.roomName+"("+meeting.buildingName+")");

            var msg = {
                "subject": MWF.LP.desktop.messsage.meetingCancelMessage,
                "content": content
            };
            var messageItem = layout.desktop.message.addMessage(msg);
            var tooltipItem = layout.desktop.message.addTooltip(msg);
            tooltipItem.contentNode.addEvent("click", function(e){
                layout.desktop.message.hide();
                layout.desktop.openApplication(e, "Meeting", null);
            });

            messageItem.contentNode.addEvent("click", function(e){
                layout.desktop.message.addUnread(-1);
                layout.desktop.message.hide();
                layout.desktop.openApplication(e, "Meeting", null);
            });
        }.bind(this));
    },
    receiveMeetingAcceptMessage: function(data){
        this.getMeeting(data.meeting, function(meeting){
            var content = MWF.LP.desktop.messsage.meetingAccept;
            //content = content.replace(/{person}/g, MWF.name.cn(meeting.applicant));
            content = content.replace(/{person}/g, MWF.name.cn(data.person));
            var date = Date.parse(meeting.startTime).format("%Y-%m-%d- %H:%M");
            content = content.replace(/{date}/g, date);
            content = content.replace(/{subject}/g, meeting.subject);
            content = content.replace(/{addr}/g, meeting.roomName+"("+meeting.buildingName+")");

            var msg = {
                "subject": MWF.LP.desktop.messsage.meetingAcceptMessage,
                "content": content
            };
            var messageItem = layout.desktop.message.addMessage(msg);
            var tooltipItem = layout.desktop.message.addTooltip(msg);
            tooltipItem.contentNode.addEvent("click", function(e){
                layout.desktop.message.hide();
                layout.desktop.openApplication(e, "Meeting", null);
            });

            messageItem.contentNode.addEvent("click", function(e){
                layout.desktop.message.addUnread(-1);
                layout.desktop.message.hide();
                layout.desktop.openApplication(e, "Meeting", null);
            });
        }.bind(this));
    },
    receiveMeetingRejectMessage: function(data){
        this.getMeeting(data.meeting, function(meeting){
            var content = MWF.LP.desktop.messsage.meetingReject;
            //content = content.replace(/{person}/g, MWF.name.cn(meeting.applicant));
            content = content.replace(/{person}/g, MWF.name.cn(data.person));
            var date = Date.parse(meeting.startTime).format("%Y-%m-%d- %H:%M");
            content = content.replace(/{date}/g, date);
            content = content.replace(/{subject}/g, meeting.subject);
            content = content.replace(/{addr}/g, meeting.roomName+"("+meeting.buildingName+")");

            var msg = {
                "subject": MWF.LP.desktop.messsage.meetingRejectMessage,
                "content": content
            };
            var messageItem = layout.desktop.message.addMessage(msg);
            var tooltipItem = layout.desktop.message.addTooltip(msg);
            tooltipItem.contentNode.addEvent("click", function(e){
                layout.desktop.message.hide();
                layout.desktop.openApplication(e, "Meeting", null);
            });

            messageItem.contentNode.addEvent("click", function(e){
                layout.desktop.message.addUnread(-1);
                layout.desktop.message.hide();
                layout.desktop.openApplication(e, "Meeting", null);
            });
        }.bind(this));
    },
    receiveAttendanceAppealInviteMessage : function(data){
        var content = MWF.LP.desktop.messsage.attendanceAppealInvite;
        content = content.replace(/{subject}/g, data.subject);

        var msg = {
            "subject": MWF.LP.desktop.messsage.attendanceAppealInviteMessage,
            "content": content
        };
        var messageItem = layout.desktop.message.addMessage(msg);
        var tooltipItem = layout.desktop.message.addTooltip(msg);
        tooltipItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "Attendance", {"curNaviId":"13"});
        });

        messageItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.addUnread(-1);
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "Attendance", {"curNaviId":"13"});
        });
    },
    receiveAttendanceAppealAcceptMessage : function(data){
        var content = MWF.LP.desktop.messsage.attendanceAppealAccept;
        content = content.replace(/{subject}/g, data.subject);

        var msg = {
            "subject": MWF.LP.desktop.messsage.attendanceAppealAcceptMessage,
            "content": content
        };
        var messageItem = layout.desktop.message.addMessage(msg);
        var tooltipItem = layout.desktop.message.addTooltip(msg);
        tooltipItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "Attendance", {"curNaviId":"12"});
        });

        messageItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.addUnread(-1);
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "Attendance", {"curNaviId":"12"});
        });
    },
    receiveAttendanceAppealRejectMessage : function(data){
        var content = MWF.LP.desktop.messsage.attendanceAppealReject;
        content = content.replace(/{subject}/g, data.subject);

        var msg = {
            "subject": MWF.LP.desktop.messsage.attendanceAppealRejectMessage,
            "content": content
        };
        var messageItem = layout.desktop.message.addMessage(msg);
        var tooltipItem = layout.desktop.message.addTooltip(msg);
        tooltipItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "Attendance", {"curNaviId":"12"});
        });

        messageItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.addUnread(-1);
            layout.desktop.message.hide();
            layout.desktop.openApplication(e, "Attendance", {"curNaviId":"12"});
        });
    }
});