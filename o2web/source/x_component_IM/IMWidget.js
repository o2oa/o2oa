MWF.xDesktop.requireApp("IM", "Actions.RestActions", null, false);
MWF.require("MWF.xDesktop.UserData", null, false);
MWF.xApplication.IM = MWF.xApplication.IM || {};
MWF.xApplication.IM.IMWidget = new Class({
    Extends: MWF.xApplication.Common.Widget,
    Implements: [Options, Events],
	options: {
        "style": "default",
        "appName": "IM",
        "name": "IMWidget"
	},
    initialize: function(desktop, options){
        this.setOptions(options);
        this.desktop = desktop;
    },
    loadContent: function(callback){
        this.ssoCount = 0;
        this.widget.node.setStyle("display", "none");
        this.userAction = new MWF.xApplication.IM.Actions.RestActions();
        this.users = {};
        this.unShowMessage = {};
        ////this.setTimeout();
        //this.action = new MWF.xApplication.VsettanMail.Actions.RestActions();
        //if (this.desktop.session.user.name!="xadmin") this.getUnreadCount();
        //this.doUnreadMessages();
        this.widget.close()
    },
    doUnreadMessages: function(){
        this.unShowMessage = MWF.UD.getDataJson("unreadChat") || {};
        layout.desktop.addEvent("unload", function(){
            MWF.UD.putData("unreadChat", this.unShowMessage);
        }.bind(this));
    },
    getOwner: function(callback){
        if (this.owner){
            if (callback) callback();
        }else{
            this.userAction.getPerson(function(json){
                this.owner = json.data;
                if (callback) callback();
            }.bind(this), null, layout.desktop.session.user.id);
        }
    },
    getPerson: function(id, callback){
        if (this.users[id]){
            if (callback) callback();
        }else{
            this.userAction.getPerson(function(json){
                this.users[id] = json.data;
                if (callback) callback();
            }.bind(this), null, id);
        }
    },
    getDialogue: function(chat, key, data, callback){
        var dialogue = chat.dialogues[key];
        if (!dialogue){
            this.getPerson(data.from, function(){
                dialogue = chat.addDialogueBack(this.owner, [this.users[data.from]]);
                if (callback) callback(dialogue);
            }.bind(this));
        }else{
            if (callback) callback(dialogue);
        }
    },
    receiveChatMessage: function(data){
        this.getOwner(function(){
            var chat = layout.desktop.apps["Chat"];
            //var im = layout.desktop.apps["IM"];
            if (chat){
                if (layout.desktop.currentApp==chat){
                    var key = data.from+layout.desktop.session.user.distinguishedName;
                    this.getDialogue(chat, key, data, function(dialogue){
                        if (chat.current != dialogue){
                            this.receiveMessageRecod(data);
                            dialogue.addUnreadMessage(data);
                        }else{
                            dialogue.showMessage(data, data.from);
                            layout.desktop.playMessageAudio();
                        }
                    }.bind(this));
                }else{
                    var key = data.from+layout.desktop.session.user.distinguishedName;
                    this.getDialogue(chat, key, data, function(dialogue){
                        if (chat.current != dialogue){
                            this.receiveMessageRecod(data);
                            dialogue.addUnreadMessage(data);
                        }else{
                            dialogue.showMessage(data, data.from);
                            layout.desktop.playMessageAudio();
                        }
                    }.bind(this));
                    this.sendTooltipMessage(data);
                    layout.desktop.playMessageAudio();
                }
            }else{
                this.receiveMessageRecod(data);
            }
        }.bind(this));
    },

    receiveMessageRecod: function(data){
        var key = data.from+layout.desktop.session.user.distinguishedName;
        if (!this.unShowMessage[key]) this.unShowMessage[key] = [];
        this.unShowMessage[key].push(data);
        //var userItem = this.users["online"+data.fromPerson];
        //if (userItem){
        //    userItem.addUnreadMessage(data);
        //}else{
        //    this.users["online"+data.fromPerson] = new MWF.xDesktop.UserPanel.User(this.userListOnlineAreaNode, this, data.fromPerson);
        //    userItem = this.users["online"+data.fromPerson];
        //    userItem.addUnreadMessage(data);
        //}
        this.setUnread();
        this.sendTooltipMessage(data);
        layout.desktop.playMessageAudio();
    },

    sendTooltipMessage: function(data){
        var content = "<div style=\"height: 20px; line-height: 20px\">"+data.text+"</div></div>"
        msg = {
            "subject": data.from+" "+MWF.LP.desktop.say+": ",
            "content": content
        };
        var tooltipItem = layout.desktop.message.addTooltip(msg);
        tooltipItem.contentNode.addEvent("click", function(e){
            this.openChat(e, data);
        }.bind(this));
    },
    openChat: function(e, data){
        this.getPerson(data.from, function(){
            var fromData = this.users[data.from];
            var chat = layout.desktop.apps["Chat"];
            if (chat){
                var key = data.from+layout.desktop.session.user.name;
                var dialogue = chat.dialogues[key];
                if (!dialogue) dialogue = chat.addDialogue(this.owner, [fromData]);
                //this.unreadDatas.each(function(data){
                //    dialogue.showMessage(data, data.fromPerson);
                //});
                dialogue.setCurrent();
                //this.clearUnread();
            }
            var _self = this;
            layout.desktop.openApplication(e, "Chat", {
                "onPostLoad": function(){
                    dialogue = this.addDialogue(_self.owner, [fromData]);
                    //_self.unreadDatas.each(function(data){
                    //    dialogue.showMessage(data, data.fromPerson);
                    //});
                    //_self.clearUnread();
                }
            });
        }.bind(this));
    },
    setUnread: function(){
        var im = layout.desktop.apps["IM"];
        var count = 0;
        Object.each(this.unShowMessage, function(v, k){
            count += v.length;
            if (im) if (v.length) im.checkUnread(v[0].from);
        }.bind(this));

        if (count>0){
            if (!this.unreadNode){
                this.unreadNode = new Element("div", {"styles": layout.desktop.css.messageUnreadCountNode}).inject(layout.desktop.top.userChatNode);
            }
            this.unreadNode.set("text", count);
        }else{
            if (this.unreadNode){
                this.unreadNode.destroy();
                this.unreadNode = null;
                delete this.unreadNode;
            }
        }
    }

});