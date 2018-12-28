MWF.xApplication.Chat.options.multitask = false;
MWF.xApplication.Chat.options.executable = false;
MWF.xDesktop.requireApp("IM", "Actions.RestActions", null, false);
MWF.xApplication.Chat.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Chat",
		"icon": "icon.png",
		"width": "800",
		"height": "500",
		"title": MWF.xApplication.Chat.LP.title,
        "id": "",
        "owner": "",
        "desktopReload": false
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Chat.LP;
        this.userAction = MWF.Actions.get("x_organization_assemble_control");
        this.socketAction = MWF.Actions.get("x_collaboration_assemble_websocket");
        //if (!this.userAction) this.userAction = new MWF.xApplication.IM.Actions.RestActions();
	},
	loadApplication: function(callback){
        this.dialogues = {};

        this.node = new Element("div", {"styles": this.css.content}).inject(this.content);
        this.dialogueTabAreaNode = new Element("div", {"styles": this.css.dialogueTabAreaNode}).inject(this.node);
        this.chatContentAreaNode = new Element("div", {"styles": this.css.chatContentAreaNode}).inject(this.node);

        this.chatTitleNode =  new Element("div", {"styles": this.css.chatTitleNode}).inject(this.chatContentAreaNode);
        this.chatTitleAddMemberAction =  new Element("div", {"styles": this.css.chatTitleAddMemberAction}).inject(this.chatTitleNode);
        //this.chatTitleAddMemberActionButton =  new Element("div", {"styles": this.css.chatTitleAddMemberActionButton, "text": this.lp.add}).inject(this.chatTitleAddMemberAction);
        //this.chatTitleDelMemberActionButton =  new Element("div", {"styles": this.css.chatTitleAddMemberActionButton, "text": this.lp.del}).inject(this.chatTitleAddMemberAction);

        this.chatTitleMemberNode =  new Element("div", {"styles": this.css.chatTitleMemberNode}).inject(this.chatTitleNode);

        this.chatAreaNode = new Element("div", {"styles": this.css.chatAreaNode}).inject(this.chatContentAreaNode);

        this.chatInputAreaNode = new Element("div", {"styles": this.css.chatInputAreaNode}).inject(this.chatContentAreaNode);
        this.chatInputNode = new Element("div", {"styles": this.css.chatInputNode}).inject(this.chatInputAreaNode);
        this.chatTextAreaNode = new Element("textarea", {"styles": this.css.chatTextAreaNode}).inject(this.chatInputNode);

        this.chatInputActionNode = new Element("div", {"styles": this.css.chatInputActionNode}).inject(this.chatInputAreaNode);
        this.chatSendActionNode = new Element("div", {"styles": this.css.chatSendActionNode, "text": this.lp.send}).inject(this.chatInputActionNode);

        this.setChatAreaHeight();
        this.addEvent("resize", this.setChatAreaHeight);

        this.setEvent();
    },
    setEvent: function(){
        this.chatTextAreaNode.addEvent("keydown", function(e){
            if (e.control && (e.code==13)){
                this.sendMessage();
                this.chatTextAreaNode.focus();
            }
        }.bind(this));

        this.chatSendActionNode.addEvent("click", this.sendMessage.bind(this));

        //this.chatTitleAddMemberActionButton.addEvent("click", this.addFriend.bind(this));
        //this.chatTitleDelMemberActionButton.addEvent("click", this.delFriend.bind(this));

    },
    //addFriend: function(){
    //    if (this.current){
    //        var panel = this.desktop.top.userPanel;
    //        var chatData = panel.chatData;
    //        if (!chatData) chatData = {};
    //        if (!chatData.onlineList) chatData.onlineList = [];
    //
    //        var user = this.current.members[0];
    //        if (chatData.onlineList.indexOf(user.name)==-1){
    //            if (layout.desktop.session.user.name!=user.name) panel.users["online"+user.name] = new MWF.xDesktop.UserPanel.User(panel.userListOnlineAreaNode, panel, user.name);
    //            chatData.onlineList.push(user.name);
    //            panel.chatData = chatData;
    //            MWF.UD.putData("chat", chatData);
    //            this.current.setCurrent();
    //        }
    //    }
    //},
    //delFriend: function(){
    //    if (this.current){
    //        var panel = this.desktop.top.userPanel;
    //        var chatData = panel.chatData;
    //        if (!chatData) chatData = {};
    //        if (!chatData.onlineList) chatData.onlineList = [];
    //
    //        var user = this.current.members[0];
    //        if (chatData.onlineList.indexOf(user.name)!=-1){
    //            var userItem = panel.users["online"+user.name];
    //            if (userItem) userItem.destroy();
    //
    //            chatData.onlineList.erase(user.name);
    //            panel.chatData = chatData;
    //            MWF.UD.putData("chat", chatData);
    //
    //            this.current.setCurrent();
    //        }
    //    }
    //},
    //addToCathList: function(){
    //    if (this.current){
    //        var panel = this.desktop.top.userPanel;
    //        var chatData = panel.chatData;
    //        if (!chatData) chatData = {};
    //        if (!chatData.chatList) chatData.chatList = [];
    //
    //        var user = this.current.members[0];
    //        if (chatData.chatList.indexOf(user.name)==-1){
    //            if (layout.desktop.session.user.name!=user.name) panel.users["chat"+user.name] = new MWF.xDesktop.UserPanel.User(panel.userListChatAreaNode, panel, user.name);
    //            chatData.chatList.unshift(user.name);
    //            panel.chatData = chatData;
    //            MWF.UD.putData("chat", chatData);
    //            this.current.setCurrent();
    //        }
    //    }
    //},
    sendMessage: function(){
        if (this.current){
            var text = this.chatTextAreaNode.get("value");
            if (text){
                //message = {
                //    "messageType": "chat",
                //    "personList": this.current.toNames,
                //    "text": text
                //}
                message = {
                    "text": text,
                    "type": "text",
                    "from": this.current.owner.distinguishedName,
                    "person": this.current.toNames[0],
                    "category": "dialog"
                };
                this.desktop.socket.send(message);
                this.current.showMessage(message);
            }
            this.chatTextAreaNode.set("value", "");
            //this.addToCathList();
        }
    },

    setChatAreaHeight: function(){
        var size = this.chatContentAreaNode.getSize();
        var titleSize = this.chatTitleNode.getSize();
        var inputSize = this.chatInputAreaNode.getSize();
        var y = size.y - titleSize.y - inputSize.y;

        this.chatAreaNode.setStyle("height", ""+y+"px");
    },

    addDialogueBack: function(owner, members){
        var dialogue = new MWF.xApplication.Chat.Dialogue(owner, members, this);
        var key1 = owner.distinguishedName+members[0].distinguishedName;
        var key2 = members[0].distinguishedName+owner.distinguishedName;
        this.dialogues[key1] = dialogue;
        this.dialogues[key2] = dialogue;
        return dialogue;
    },
    addDialogue: function(owner, members){
        var dialogue = new MWF.xApplication.Chat.Dialogue(owner, members, this);
        var key1 = owner.distinguishedName+members[0].distinguishedName;
        var key2 = members[0].distinguishedName+owner.distinguishedName;
        this.dialogues[key1] = dialogue;
        this.dialogues[key2] = dialogue;
        dialogue.setCurrent();
        return dialogue;
    }

});

MWF.xApplication.Chat.Dialogue = new Class({
    initialize: function(owner, members, chat){
        this.chat = chat;
        this.owner = owner;
        this.members = members;
        this.css = this.chat.css;
        this.unreadCount = 0;
        this.messageDate = null;
        this.load();
    },
    load: function(){
        this.createTab();
        this.createContent();

    },
    createContent: function(){
        this.chatContentScrollNode = new Element("div", {"styles": this.css.chatContentScrollNode}).inject(this.chat.chatAreaNode);
        this.chatContentNode = new Element("div", {"styles": this.css.chatContentNode}).inject(this.chatContentScrollNode);

        this.scroll = new Fx.Scroll(this.chatContentScrollNode);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.chatContentScrollNode, {
                "style":"xDesktop_Message", "where": "before", "indent": false, "distance": 50, "friction": 6,	"axis": {"x": false, "y": true}
            });
        }.bind(this));
    },
    createTab: function(){
        this.tabNode = new Element("div", {"styles": this.css.chatTabNode}).inject(this.chat.dialogueTabAreaNode);
        var icon = this.members[0].icon;
        if (this.members.length>1){
            icon = this.chat.path+this.chat.options.style+"/group.png";
        }else{
            icon = this.chat.userAction.getPersonIcon(this.members[0].id);
            // if (!icon){
            //     if (this.members[0].genderType=="f"){
            //         icon = "/x_component_Chat/$Main/"+this.chat.options.style+"/female.png";
            //     }else{
            //         icon = "/x_component_Chat/$Main/"+this.chat.options.style+"/man.png";
            //     }
            // }else{
            //     icon = "data:image/png;base64,"+icon;
            // }
        }

        //if (this.members[0].onlineStatus==="offline"){
        //    icon = MWF.grayscale(icon);
        //}
        this.tabIconNode = new Element("div", {"styles": this.css.tabIconNode}).inject(this.tabNode);
        this.tabIconImgNode = new Element("img", {"styles": this.css.tabIconImgNode}).inject(this.tabIconNode);
        this.tabIconImgNode.set("src", icon);

        this.tabCloseAction = new Element("div", {"styles": this.css.tabCloseAction}).inject(this.tabNode);

        this.tabTextNode = new Element("div", {"styles": this.css.tabTextNode}).inject(this.tabNode);
        var names = [];
        var texts = [];
        this.members.each(function(m){
            names.push(m.distinguishedName);
            texts.push(m.name)
        });
        this.toNames = names;
        this.title = texts.join(",");
        this.tabTextNode.set("text", this.title);

        this.tabNode.addEvents({
            "mouseover": function(){
                if (this.chat.current != this) this.tabNode.setStyles(this.css.chatTabNode_over);
                this.tabCloseAction.setStyle("display", "block");
            }.bind(this),
            "mouseout": function(){
                if (this.chat.current != this) this.tabNode.setStyles(this.css.chatTabNode);
                this.tabCloseAction.setStyle("display", "none");
            }.bind(this),
            "click": function(){this.setCurrent();}.bind(this)
        });
        this.tabCloseAction.addEvents({
            //"mouseover": function(){
            //    this.tabCloseAction.setStyles(this.css.tabCloseAction_over);
            //}.bind(this),
            //"mouseout": function(){
            //    this.tabCloseAction.setStyles(this.css.tabCloseAction);
            //}.bind(this),
            "click": function(e){
                this.close();
                e.stopPropagation();
            }.bind(this)
        });
    },
    close: function(){

    },
    setCurrent: function(){
        if (this.chat.current) this.chat.current.setUncurrent();
        this.tabNode.setStyles(this.css.chatTabNode_current);
        this.chatContentScrollNode.setStyle("display", "block");

        this.chat.chatTitleMemberNode.set("text", this.title);
        this.chat.setTitle(this.title);

        //this.chat.chatTitleAddMemberActionButton.setStyle("display", "block");
        //this.chat.chatTitleDelMemberActionButton.setStyle("display", "none");
        //if (this.chat.desktop.top.userPanel.chatData){
        //    if (this.chat.desktop.top.userPanel.chatData.onlineList){
        //        if (this.chat.desktop.top.userPanel.chatData.onlineList.indexOf(this.members[0].name)!=-1){
        //            this.chat.chatTitleAddMemberActionButton.setStyle("display", "none");
        //            this.chat.chatTitleDelMemberActionButton.setStyle("display", "block");
        //        }
        //    }
        //}


        this.chat.current = this;
        this.checkUnread();

        this.clearUnread();
    },
    checkUnread: function(){
        if (layout.desktop.widgets["IMIMWidget"]){
            var unShowMessage = layout.desktop.widgets["IMIMWidget"].unShowMessage;
            var key = this.members[0].distinguishedName+this.owner.distinguishedName;
            if (unShowMessage[key]){
                if (unShowMessage[key].length){
                    unShowMessage[key].each(function(msg){
                        this.showMessage(msg, msg.from);
                    }.bind(this));
                }
                delete unShowMessage[key];
                layout.desktop.widgets["IMIMWidget"].setUnread();
                if (layout.desktop.apps["IM"]) layout.desktop.apps["IM"].checkUnread(this.members[0].distinguishedName);
            }
        }
    },

    setUncurrent: function(){
        this.chatContentScrollNode.setStyle("display", "none");
        this.tabNode.setStyles(this.css.chatTabNode);
        this.chat.current = null;
    },
    showMessage: function(msg, from){
        var messageDate = new Date();
        if (!this.messageDate || ((messageDate.getTime()-this.messageDate.getTime()) > 120000)){
            var timeText = messageDate.format("%Y-%m-%d %H:%M");
            var timeNode = new Element("div", {"styles": this.css.messageTimeNode, "text": timeText}).inject(this.chatContentNode);
            this.messageDate = messageDate;
        }

        var messageNode = new Element("div", {"styles": this.css.messageNode}).inject(this.chatContentNode);

        var icon = "";
        var iconcss = "";
        var textcss = "";

        if (from){
            for (var i=0; i<this.members.length; i++){
                if (this.members[i].distinguishedName===from) break;
            }
            if (this.members[i]){
                icon = this.getIcon(this.members[i]);
                iconCss = this.css.messageIconGetNode;
                textAreaCss = this.css.messageTextAreaGetNode;
                textCss = this.css.messageTextGetNode;
            }
        }else{
            icon = this.getIcon(this.owner);
            iconCss = this.css.messageIconSendNode;
            textAreaCss = this.css.messageTextAreaSendNode;
            textCss = this.css.messageTextSendNode;
        }
        var iconNode = new Element("div", {"styles": iconCss}).inject(messageNode);
        var iconImgNode = new Element("img", {"styles": this.css.messageIconImgNode, "src": icon}).inject(iconNode);

        var textAreaNode = new Element("div", {"styles": textAreaCss}).inject(messageNode);

        var text = msg.text.replace(/[\n+]/g, "<br/>");
        var textNode = new Element("div", {"styles": textCss, "html": text}).inject(textAreaNode);

        this.scroll.toElement(messageNode);
        //this.chatContentNode
    },
    getIcon: function(data){
        return this.chat.userAction.getPersonIcon(data.id);

        // var icon = "";
        // if (data.icon){
        //     icon = "data:image/png;base64,"+data.icon;
        // }else{
        //     if (data.genderType=="f"){
        //         icon = "/x_component_Chat/$Main/"+this.chat.options.style+"/female.png";
        //     }else{
        //         icon = "/x_component_Chat/$Main/"+this.chat.options.style+"/man.png";
        //     }
        // }
        // return icon;
    },
    addUnreadMessage: function(data){
        if (!this.unreadNode) this.unreadNode = new Element("div", {"styles": this.css.userListUnreadNode, "text": "0"}).inject(this.tabNode, "bottom");
        var i = this.unreadNode.get("text").toInt()+1;
        this.unreadNode.set("text", i);
        //this.node.inject(this.container, "top");
    },
    clearUnread: function(){
        if (this.unreadNode){
            this.unreadNode.destroy();
            this.unreadNode = null;
        }
    }

});