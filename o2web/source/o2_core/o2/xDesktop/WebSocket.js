MWF.xDesktop = MWF.xDesktop || {};
MWF.xApplication = MWF.xApplication || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);

MWF.xDesktop.WebSocket = new Class({
    Implements: [Options, Events],
    options: {},
    initialize: function(options){
        var addressObj = layout.serviceAddressList["x_message_assemble_communicate"];
        var uri = new URI(window.location.href);
        var scheme = uri.get("scheme");
        var wsScheme = (scheme.toString().toLowerCase()==="https") ? "wss" : "ws";
        this.ws = wsScheme+"://"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context+"/ws/collaboration";

        this.reConnect = true;
        this.checking = false;
        this.heartTimeout = 30000;
        this.checkingTimeout = 10000;
        this.heartMsg = "heartbeat";

        // var addressObj = layout.desktop.serviceAddressList["x_collaboration_assemble_websocket"];
        // this.ws = "ws://"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context+"/ws/collaboration";
        //var ws = "ws://hbxa01.bf.ctc.com/x_collaboration_assemble_websocket/ws/collaboration";

        //使用轮询方式处理消息.....
        // this.webSocket = {
        //     "readyState":"1",
        //     "close": function(){},
        //     "open": function(){}
        // };
        // window.setInterval(function(){
        //     o2.Actions.get("")
        // }, 10000);

        ///*暂时不启用WebSocket了------------
        //this.ws = this.ws+"?x-token="+encodeURIComponent(Cookie.read("x-token"))+"&authorization="+encodeURIComponent(Cookie.read("x-token"));

        if (layout.config.webSocketEnable){
            this.ws = this.ws+"?x-token="+encodeURIComponent(Cookie.read("x-token"));

            try{
                this.webSocket = new WebSocket(this.ws);

                //this.webSocket = new WebSocket(this.ws);
                this.webSocket.onopen = function (e){this.onOpen(e);}.bind(this);
                this.webSocket.onclose = function (e){this.onClose(e);}.bind(this);
                this.webSocket.onmessage = function (e){this.onMessage(e);}.bind(this);
                this.webSocket.onerror = function (e){this.onError(e);}.bind(this);
                //---------------------------------*/\
            }catch(e){
                //WebSocket.close();
                //this.webSocket = new WebSocket(this.ws);
                console.log("Unable to connect to the websocket server, will retry in "+(this.heartTimeout/1000)+" seconds");
                if (this.webSocket){
                    this.close();
                    //this.webSocket = new WebSocket(this.ws);
                }
            }
            this.heartbeat();
        }

    },
    onOpen: function(e){
        console.log("websocket is open, You can receive system messages");
        //MWF.xDesktop.notice("success", {"x": "right", "y": "top"}, "websocket is open ...");
    },
    onClose: function(e){
        console.log("websocket is closed. ");
        if (this.reConnect) this.initialize();
        //MWF.xDesktop.notice("success", {"x": "right", "y": "top"}, "websocket is closed ...");
    },
    onMessage: function(e){
        if (e.data){
            try{
                if (e.data===this.heartMsg){
                    this.heartbeat();
                    //console.log("get heartbeat...");
                    return true;
                }
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
                            case "task_create":
                            case "task_urge":
                            case "task_expire":
                            case "task_press":
                                this.receiveTaskMessage(data);
                                break;
                            case "read":
                            case "read_create":
                                this.receiveReadMessage(data);
                                break;
                            case "review":
                                this.receiveReviewMessage(data);
                                break;
                            case "fileEditor":
                            case "attachment_editor":
                            case "attachment_editorCancel":
                            case "attachment_editorModify":
                                this.receiveFileEditorMessage(data);
                                break;
                            case "fileShare":
                            case "attachment_share":
                            case "attachment_shareCancel":
                                this.receiveFileShareMessage(data);
                                break;
                            case "meetingInvite":
                            case "meeting_invite":
                                this.receiveMeetingInviteMessage(data);
                                break;
                            case "meetingCancel":
                            case "meeting_cancel":
                                this.receiveMeetingCancelMessage(data);
                                break;
                            case "meetingAccept":
                            case "meeting_accept":
                                this.receiveMeetingAcceptMessage(data);
                                break;
                            case "meetingReject":
                            case "meeting_reject":
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
                            case "calendar_alarm":
                                this.receiveAttendanceAppealRejectMessage(data);
                                break;
                            case "custom_create":
                                this.receiveCustomMessage(data);
                                break;
                            default:
                        }
                }
            }catch(e){}
        }
    },
    onError: function(e){
        console.log("websocket is error ...");
        //MWF.xDesktop.notice("success", {"x": "right", "y": "top"}, "websocket is error ...");
    },
    retry: function(){
        if (this.webSocket){
            this.close();
        }
        this.initialize();
    },
    close: function(){
        this.reConnect = false;
        if (this.webSocket) this.webSocket.close();
        //WebSocket.close();
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
    heartbeat: function(){
        if (this.serverCheck) window.clearTimeout(this.serverCheck);
        if (this.heartbeatCheck) window.clearTimeout(this.heartbeatCheck);
        this.heartbeatCheck = window.setTimeout(function(){
            this.sendHeartbeat(this.heartMsg);
        }.bind(this), this.heartTimeout);
    },
    sendHeartbeat: function(msg){
        if (!this.webSocket || this.webSocket.readyState != 1) {
            this.retry();
        }
        try{
            //console.log("send heartbeat ...");
            this.webSocket.send(msg);
            this.serverCheck = window.setTimeout(function(){
                this.retry();
            }.bind(this), this.checkingTimeout);
        }catch(e){
            this.retry();
            //this.initialize();
        }
    },

    receiveChatMessage: function(data){
        if (layout.desktop.widgets["IMIMWidget"]) layout.desktop.widgets["IMIMWidget"].receiveChatMessage(data);
        //if (layout.desktop.top.userPanel) layout.desktop.top.userPanel.receiveChatMessage(data);
    },
    openWork: function(id, e){
        o2.Actions.get("x_processplatform_assemble_surface").getWorkInfor(id, function(){
            var options = {"workId": id, "appId": "process.Work"+id};
            layout.desktop.openApplication(e, "process.Work", options);
        }.bind(this), function(){
            layout.desktop.openApplication(e, "process.TaskCenter", null, {
                "status": {
                    "navi": "task"
                }
            });
        }.bind(this));
    },
    receiveTaskMessage: function(data){
        debugger;
        var task = data.body;
        //var content = MWF.LP.desktop.messsage.receiveTask+"《"+task.title+"》, "+MWF.LP.desktop.messsage.activity+": <font style='color: #ea621f'>"+(task.activityName || "")+"</font>";
        var content = data.title;
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
            this.openWork(task.work,e);
        }.bind(this));
        messageItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.addUnread(-1);
            layout.desktop.message.hide();
            this.openWork(task.work,e);
        }.bind(this));
    },
    receiveReadMessage: function(data){
        var read = data.body;
        //var content = MWF.LP.desktop.messsage.receiveRead+"《"+read.title+"》. ";
        var content = data.title;
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
            this.openWork(read.work,e);
        }.bind(this));

        messageItem.contentNode.addEvent("click", function(e){
            layout.desktop.message.addUnread(-1);
            layout.desktop.message.hide();
            this.openWork(read.work,e);
        }.bind(this));
    },
    receiveCustomMessage: function(data){
        var content = "<font style='color: #333; font-weight: bold'>"+MWF.LP.desktop.messsage.customMessage+"</font>"+data.body;
        var msg = {
            "subject": MWF.LP.desktop.messsage.customMessageTitle,
            "content": content
        };
        var messageItem = layout.desktop.message.addMessage(msg);
        var tooltipItem = layout.desktop.message.addTooltip(msg);
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
    getMeeting: function(data, callback){
        //this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_meeting_assemble_control", "x_component_Meeting");
        //var action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_meeting_assemble_control", "x_component_Meeting");
        if( data.body && typeOf( data.body ) === "object" ){
            var data = data.body;
            MWF.Actions.get("x_meeting_assemble_control").getRoom(data.room, function(roomJson){
                data.roomName = roomJson.data.name;
                MWF.Actions.get("x_meeting_assemble_control").getBuilding(roomJson.data.building, function(buildingJson){
                    data.buildingName = buildingJson.data.name;
                    if (callback) callback(data);
                }.bind(this));
            }.bind(this));
        }else{
            MWF.Actions.get("x_meeting_assemble_control").getMeeting(data.metting, function(json){
                var data = json.data;
                MWF.Actions.get("x_meeting_assemble_control").getRoom(data.room, function(roomJson){
                    data.roomName = roomJson.data.name;
                    MWF.Actions.get("x_meeting_assemble_control").getBuilding(roomJson.data.building, function(buildingJson){
                        data.buildingName = buildingJson.data.name;
                        if (callback) callback(data);
                    }.bind(this));
                }.bind(this));
            }.bind(this));
        }
    },
    receiveMeetingInviteMessage: function(data){
        debugger;
        this.getMeeting(data, function(meeting){
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
        debugger;
        this.getMeeting(data, function(meeting){
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
        debugger;
        this.getMeeting(data, function(meeting){
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
        debugger;
        this.getMeeting(data, function(meeting){
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
