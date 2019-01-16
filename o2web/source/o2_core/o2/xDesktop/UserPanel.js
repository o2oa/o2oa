MWF.xDesktop = MWF.xDesktop || {};
MWF.xDesktop.requireApp("Selector", "Actions.RestActions", null, false);

MWF.xDesktop.UserPanel = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function(container, options){
        this.setOptions(options);
        this.container = container;
        this.path = MWF.defaultPath+"/xDesktop/$UserPanel/";
        this.cssPath = MWF.defaultPath+"/xDesktop/$UserPanel/"+this.options.style+"/css.wcss";
        this.users = {};
        this.isShow = false;
        this._loadCss();
    },
    changStyle: function(style){
        this.setOptions({"style": style});
        this.cssPath = MWF.defaultPath+"/xDesktop/$UserPanel/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.node.setStyles(this.css.panelNode);
        this.titleNode.setStyles(this.css.titleNode);
        this.userInforNode.setStyles(this.css.userInforNode);
        this.userSearchNode.setStyles(this.css.userSearchNode);
        this.userListNode.setStyles(this.css.userListNode);
        this.userMenuNode.setStyles(this.css.userMenuNode);
        this.show();
    },
    load: function(){
        this.node = new Element("div#userpanel", {"styles": this.css.panelNode});
        this.titleNode = new Element("div", {"styles": this.css.titleNode}).inject(this.node);
        this.userInforNode = new Element("div", {"styles": this.css.userInforNode}).inject(this.node);
        this.userSearchNode = new Element("div", {"styles": this.css.userSearchNode}).inject(this.node);
        this.userListNode = new Element("div", {"styles": this.css.userListNode}).inject(this.node);
        this.userMenuNode = new Element("div", {"styles": this.css.userMenuNode}).inject(this.node);

        //this.node.addEvent("click", function(){
        //    this.show();
        //}.bind(this));

        this.createTitle();
        this.createUserInfor();
        this.createSearch();
        this.createUserList();

        this.createBottomMenu();

        window.setInterval(function(){
            if (this.isShow) this.checkOnline();
        }.bind(this), 120000);
    },
    checkOnline: function(){
        Object.each(this.users, function(user){
            if (!this.onlineAction) this.onlineAction = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_collaboration_assemble_websocket");
            this.onlineAction.invoke({"name": "personOnline", "parameter": {"person": user.name}, "success": function(json){
                if (user.data.status != json.data.status){
                    var icon = (user.data.icon) ? "data:image/png;base64,"+user.data.icon : "";
                    if (!icon){
                        if (user.data.genderType=="f"){
                            icon = MWF.defaultPath+"/xDesktop/$UserPanel/default/icon/female.png";
                        }else{
                            icon = MWF.defaultPath+"/xDesktop/$UserPanel/default/icon/man.png";
                        }
                    }
                    if (json.data.status!="offline"){
                        user.userIconNode.set("src", icon);
                        user.node.inject(user.onlineContainer);
                    }else{
                        user.userIconNode.set("src", MWF.grayscale(icon));
                        user.node.inject(user.offlineContainer);
                    }
                    user.data.status = json.data.status
                }

            }.bind(this)});
        }.bind(this));
    },
    createBottomMenu: function(){
    //    this.userConfigNode = new Element("div", {"styles": this.css.userConfigNode}).inject(this.userMenuNode);
    //    this.userLogoutNode = new Element("div", {"styles": this.css.userLogoutNode}).inject(this.userMenuNode);
    },
    createTitle: function(){
        this.closeAction = new Element("div", {"styles": this.css.closeActionNode}).inject(this.titleNode);
        this.closeAction.addEvent("click", function(e){
            this.hide();
            e.stopPropagation();
        }.bind(this));
    },
    createUserInfor: function(){
        this.userIconAreaNode = new Element("div", {"styles": this.css.userIconAreaNode}).inject(this.userInforNode);
        this.userIconNode = new Element("img", {"styles": this.css.userIconNode}).inject(this.userIconAreaNode);
        this.userTextInforNode = new Element("div", {"styles": this.css.userTextInforNode}).inject(this.userInforNode);

        this.userNameInforNode = new Element("div", {"styles": this.css.userNameInforNode}).inject(this.userTextInforNode);
        this.userNameTextInforNode = new Element("div", {"styles": this.css.userNameTextInforNode}).inject(this.userNameInforNode);
        this.userDutyTextInforNode = new Element("div", {"styles": this.css.userDutyTextInforNode}).inject(this.userNameInforNode);

        this.userSignInforNode = new Element("div", {"styles": this.css.userSignInforNode}).inject(this.userTextInforNode);

        if (!this.userAction) this.userAction = new MWF.xApplication.Selector.Actions.RestActions();
        this.userAction.getPersonComplex(function(json){
            if (!json.data) json.data = {"display": layout.desktop.session.user.name, "name": layout.desktop.session.user.name, "identityList": []};
            this.owner = json.data;
            if (json.data.icon){
                this.userIconNode.set("src", "data:image/png;base64,"+json.data.icon+"");
            }else{
                if (json.data.genderType=="f"){
                    this.userIconNode.set("src", ""+MWF.defaultPath+"/xDesktop/$UserPanel/default/icon/female.png");
                }else{
                    this.userIconNode.set("src", ""+MWF.defaultPath+"/xDesktop/$UserPanel/default/icon/man.png");
                }
            }

            this.userNameTextInforNode.set("text", json.data.display);
            var departments = [];
            json.data.identityList.each(function(id){
                departments.push(id.department);
            }.bind(this));

            var dutys = [];
            //json.data.companyDutyList.each(function(duty){
            //    dutys.push(duty.name);
            //}.bind(this));
            //json.data.departmentDutyList.each(function(duty){
            //    dutys.push(duty.name);
            //}.bind(this));
            var text = (dutys.length) ?  departments.join(", ")+ "["+ dutys.join(", ")+"]" : departments.join(", ");
            this.userDutyTextInforNode.set("text", text);

            this.userSignInforNode.set("text", json.data.signature || MWF.LP.desktop.nosign);

        }.bind(this), null, layout.desktop.session.user.name);
    },
    createSearch: function(){
        this.searchInput = new Element("input", {
            "styles": this.css.searchInput,
            "type": "text",
            "value": MWF.LP.desktop.searchUser
        }).inject(this.userSearchNode);

        this.searchInput.addEvents({
            "focus": function(){ if (this.searchInput.get("value")==MWF.LP.desktop.searchUser) this.searchInput.set("value", "");}.bind(this),
            "blur": function(){ if (!this.searchInput.get("value")) this.searchInput.set("value", MWF.LP.desktop.searchUser);}.bind(this),
            "keydown": function(e){ if (e.code==13) this.doSearch(); }.bind(this)
        });
    },
    doSearch: function(){
        var key = this.searchInput.get("value");
        if (key){
            this.userListSearchTab.click();
            if (!this.userAction) this.userAction = new MWF.xApplication.Selector.Actions.RestActions();
            this.userAction.listPersonByKey(function(json){
//                debugger;
                this.userListSearchAreaNode.getFirst().empty();
                this.userListSearchAreaNode.getLast().empty();
                json.data.each(function(user){
                    if (layout.desktop.session.user.name!=user) new MWF.xDesktop.UserPanel.User(this.userListSearchAreaNode, this, user.name);
                }.bind(this));
            }.bind(this), null, key);
        }
    },
    createUserList: function(){
        this.userListTitleNode = new Element("div", {"styles": this.css.userListTitleNode}).inject(this.userListNode);
        this.userListChatTab = new Element("div", {"styles": this.css.userListChatTab}).inject(this.userListTitleNode);
        this.userListOnlineTab = new Element("div", {"styles": this.css.userListOnlineTab}).inject(this.userListTitleNode);
        this.userListSearchTab = new Element("div", {"styles": this.css.userListSearchTab}).inject(this.userListTitleNode);
    //    this.userListGroupTab = new Element("div", {"styles": this.css.userListChartTab}).inject(this.userListTitleNode);

        this.userListChatTab.addEvents({
            "mouseover": function(){if (this.currentList != this.userListChatTab) this.userListChatTab.setStyles(this.css.userListChatTab_over);}.bind(this),
            "mouseout": function(){ if (this.currentList != this.userListChatTab) this.userListChatTab.setStyles(this.css.userListChatTab); }.bind(this),
            "click": function(){this.loadChatList();}.bind(this)
        });
        this.userListOnlineTab.addEvents({
            "mouseover": function(){ if (this.currentList != this.userListOnlineTab) this.userListOnlineTab.setStyles(this.css.userListOnlineTab_over); }.bind(this),
            "mouseout": function(){ if (this.currentList != this.userListOnlineTab) this.userListOnlineTab.setStyles(this.css.userListOnlineTab); }.bind(this),
            "click": function(){this.loadOnlineList();}.bind(this)
        });
        this.userListSearchTab.addEvents({
            "mouseover": function(){ if (this.currentList != this.userListSearchTab) this.userListSearchTab.setStyles(this.css.userListSearchTab_over); }.bind(this),
            "mouseout": function(){ if (this.currentList != this.userListSearchTab) this.userListSearchTab.setStyles(this.css.userListSearchTab); }.bind(this),
            "click": function(){this.loadSearchList();}.bind(this)
        });

        this.currentList = this.userListOnlineTab;
        //MWF.UD.getData("chat", function(json) {
        //    if (json.data) {
        //        chartList = JSON.decode(json.data);
        //        if (!chartList.users){
        //            this.currentList = this.userListOnlineTab;
        //        }else if (!chartList.users.length) this.currentList = this.userListOnlineTab;
        //    }else{
        //        this.currentList = this.userListOnlineTab;
        //    }
        //}.bind(this));

        this.userListScrollNode = new Element("div", {"styles": this.css.userListScrollNode}).inject(this.userListNode);

        this.userListChatAreaNode = new Element("div", {"styles": this.css.userListAreaNode}).inject(this.userListScrollNode);
        this.userListChatAreaOnlineNode = new Element("div", {"styles": this.css.userListAreaNode}).inject(this.userListChatAreaNode);
        this.userListChatAreaOfflineNode = new Element("div", {"styles": this.css.userListAreaNode}).inject(this.userListChatAreaNode);

        this.userListOnlineAreaNode = new Element("div", {"styles": this.css.userListAreaNode}).inject(this.userListScrollNode);
        this.userListOnlineAreaOnlineNode = new Element("div", {"styles": this.css.userListAreaNode}).inject(this.userListOnlineAreaNode);
        this.userListOnlineAreaOfflineNode = new Element("div", {"styles": this.css.userListAreaNode}).inject(this.userListOnlineAreaNode);

        this.userListSearchAreaNode = new Element("div", {"styles": this.css.userListAreaNode}).inject(this.userListScrollNode);
        this.userListSearchAreaOnlineNode = new Element("div", {"styles": this.css.userListAreaNode}).inject(this.userListSearchAreaNode);
        this.userListSearchAreaOfflineNode = new Element("div", {"styles": this.css.userListAreaNode}).inject(this.userListSearchAreaNode);

        this.loadChatOnlineList();

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.userListScrollNode, {
                "style":"xDesktop_Message", "where": "before", "indent": false, "distance": 50, "friction": 6,	"axis": {"x": false, "y": true}
            });
        }.bind(this));
    },
    loadChatOnlineList: function(){
        MWF.UD.getData("chat", function(json) {
            if (json.data) {
                this.chatData = JSON.decode(json.data);
                chatList = this.chatData.chatList;
                onlineList = this.chatData.onlineList;

                if (chatList){
                    if (chatList.length){
                        chatList.each(function(user){
                            if (layout.desktop.session.user.name!=user) this.users["chat"+user] = new MWF.xDesktop.UserPanel.User(this.userListChatAreaNode, this, user);
                        }.bind(this));
                    }else{
            //            this.setNoChatListText();
                    }
                }else{
            //        this.setNoChatListText();
                }
                if (onlineList){
                    if (onlineList.length){
                        onlineList.each(function(user){
                            if (layout.desktop.session.user.name!=user) this.users["online"+user] = new MWF.xDesktop.UserPanel.User(this.userListOnlineAreaNode, this, user);
                        }.bind(this));
                    }else{
            //            this.setNoOnlineListText();
                    }
                }else{
            //        this.setNoOnlineListText();
                }
            }

            if (!layout.desktop.socket) layout.desktop.openWebSocket();

        }.bind(this));
    },
    loadChatList: function(){
    //    this.userListChatAreaNode.empty();
        this.userListChatAreaNode.setStyle("display", "block");
        this.userListOnlineAreaNode.setStyle("display", "none");
        this.userListSearchAreaNode.setStyle("display", "none");

        this.userListChatTab.setStyles(this.css.userListChatTab_current);
        this.userListOnlineTab.setStyles(this.css.userListOnlineTab);
        this.userListSearchTab.setStyles(this.css.userListSearchTab);
        this.currentList = this.userListChatTab;
    },
    loadOnlineList: function(){
    //    this.userListOnlineAreaNode.empty();
        this.userListOnlineAreaNode.setStyle("display", "block");
        this.userListChatAreaNode.setStyle("display", "none");
        this.userListSearchAreaNode.setStyle("display", "none");

        this.userListOnlineTab.setStyles(this.css.userListOnlineTab_current);
        this.userListChatTab.setStyles(this.css.userListChatTab);
        this.userListSearchTab.setStyles(this.css.userListSearchTab);
        this.currentList = this.userListOnlineTab;
        //if (!this.onlineAction) this.onlineAction = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_collaboration_assemble_websocket");
        //this.onlineAction.invoke({"name": "listOnline",	"success": function(json){
        //    debugger;
        //    if (json.data){
        //        json.data.each(function(user){
        //            if (layout.desktop.session.user.name!=user) this.users["online"+user] = new MWF.xDesktop.UserPanel.User(this.userListOnlineAreaNode, this, user);
        //        }.bind(this));
        //    }
        //}.bind(this)});
    },
    loadSearchList: function(){
        this.userListOnlineAreaNode.setStyle("display", "none");
        this.userListChatAreaNode.setStyle("display", "none");
        this.userListSearchAreaNode.setStyle("display", "block");

        this.userListOnlineTab.setStyles(this.css.userListOnlineTab);
        this.userListChatTab.setStyles(this.css.userListChatTab);
        this.userListSearchTab.setStyles(this.css.userListSearchTab_current);
        this.currentList = this.userListSearchTab;
    },

    listOnlineUser: function(){

        //MWF.xDesktop.Actions.RestActions
    },

    hide: function(){
        this.node.setStyle("display", "none");
        this.isShow = false;
    },
    show: function(){
        this.node.inject($(document.body));
        var zidx = MWF.xDesktop.zIndexPool.applyZindex();
        this.node.setStyles({
            "display": "block",
            "z-index": zidx
        });
        this.setPosition();
        this.currentList.click();
        this.isShow = true;

        if (layout.desktop.currentApp) layout.desktop.currentApp.setUncurrent();
    },
    setPosition: function(){
        var size = this.container.getSize();

        var position = this.container.getPosition(this.container.getOffsetParent());
        var y = size.y-24;
        var top = position.y+10;
        this.node.setStyle("height", ""+y+"px");
        this.node.setStyle("top", ""+top+"px");

        var titleSize = this.titleNode.getSize();
        var userInforSize = this.userInforNode.getSize();
        var userSearchSize = this.userSearchNode.getSize();
        var userMenuSize = this.userMenuNode.getSize();

        var height = y-(titleSize.y+userInforSize.y+userSearchSize.y+userMenuSize.y);
        this.userListNode.setStyle("height", ""+height+"px");

        var userListTitleSize = this.userListTitleNode.getSize();
        height = height-userListTitleSize.y;
        this.userListScrollNode.setStyle("height", ""+height+"px");
    },
    receiveChatMessage: function(data){
        var chat = this.desktop.apps["Chat"];
        if (chat){
            var key = data.fromPerson+layout.desktop.session.user.name;
            var dialogue = chat.dialogues[key];
            if (dialogue){
                dialogue.showMessage(data, data.fromPerson);
                if (chat.current != dialogue){
                    layout.desktop.playMessageAudio();
                    dialogue.addUnreadMessage(data);
                }

                if (layout.desktop.currentApp!=chat){
                    var userItem = this.users["online"+data.fromPerson];
                    this.sendTooltipMessage(data, userItem)
                    layout.desktop.playMessageAudio();
                }
                //window.focus();
            }else{
                this.receiveMessageRecodePanel(data);
            }
        }else{
            this.receiveMessageRecodePanel(data);
        }
    },
    receiveMessageRecodePanel: function(data){
        var userItem = this.users["online"+data.fromPerson];
        if (userItem){
            userItem.addUnreadMessage(data);
        }else{
            this.users["online"+data.fromPerson] = new MWF.xDesktop.UserPanel.User(this.userListOnlineAreaNode, this, data.fromPerson);
            userItem = this.users["online"+data.fromPerson];
            userItem.addUnreadMessage(data);
        }
        this.sendTooltipMessage(data, userItem);
        layout.desktop.playMessageAudio();
    },
    sendTooltipMessage: function(data, userItem){
        var content = "<div style=\"height: 20px; line-height: 20px\">"+data.text+"</div></div>"
        msg = {
            "subject": data.fromPerson+" "+MWF.LP.desktop.say+": ",
            "content": content
        };
        var tooltipItem = layout.desktop.message.addTooltip(msg);
        tooltipItem.contentNode.addEvent("click", function(){
            userItem.openChat();
        });
    }


});

MWF.xDesktop.UserPanel.User = new Class({
    initialize: function(container, panel, name){
        this.panel = panel;
        this.name = name;
        this.data = null;
        this.container = container;
        this.onlineContainer = container.getFirst();
        this.offlineContainer = container.getLast();
        this.css = this.panel.css;
        this.checkOnlineTime = new Date();
        this.unreadDatas = [];
        this.load();
    },
    createNode: function(){
        this.node = new Element("div", {"styles": this.css.chatUserNode}).inject(this.onlineContainer);

        this.userIconAreaNode = new Element("div", {"styles": this.css.userListIconAreaNode}).inject(this.node);
        this.userIconNode = new Element("img", {"styles": this.css.userListIconNode}).inject(this.userIconAreaNode);
        this.userTextInforNode = new Element("div", {"styles": this.css.userListTextInforNode}).inject(this.node);

        this.userNameInforNode = new Element("div", {"styles": this.css.userListNameInforNode}).inject(this.userTextInforNode);
        this.userNameTextInforNode = new Element("div", {"styles": this.css.userListNameTextInforNode}).inject(this.userNameInforNode);
        this.userListGenderFlagNode = new Element("div", {"styles": this.css.userListGenderFlagNode}).inject(this.userNameInforNode);

        this.userDutyTextInforNode = new Element("div", {"styles": this.css.userListDutyTextInforNode}).inject(this.userNameInforNode);

        this.userSignInforNode = new Element("div", {"styles": this.css.userListSignInforNode}).inject(this.userTextInforNode);
    },

    checkOnline: function(){
        if (this.data.status=="offline"){
            this.userIconNode.set("src", MWF.grayscale(this.userIconNode.get("src")));
            this.node.inject(this.offlineContainer);
        }
    },

    loadPerson: function(){
        this.panel.userAction.getPersonComplex(function(json){
            this.data = json.data;
            if (json.data.icon){
                this.userIconNode.set("src", "data:image/png;base64,"+json.data.icon+"");
            }else{
                if (json.data.genderType=="f"){
                    this.userIconNode.set("src", ""+MWF.defaultPath+"/xDesktop/$UserPanel/default/icon/female.png");
                }else{
                    this.userIconNode.set("src", ""+MWF.defaultPath+"/xDesktop/$UserPanel/default/icon/man.png");
                }
            }

            if (json.data.status=="offline"){
                this.userIconNode.set("src", MWF.grayscale(this.userIconNode.get("src")));
            }

            this.userNameTextInforNode.set("text", json.data.display);
            if (json.data.genderType=="f"){
                this.userListGenderFlagNode.setStyle("background-image", "url("+MWF.defaultPath+"/xDesktop/$UserPanel/default/icon/female_flag.png)");
            }else{
                this.userListGenderFlagNode.setStyle("background-image", "url("+MWF.defaultPath+"/xDesktop/$UserPanel/default/icon/male_flag.png)");
            }

            var departments = [];
            json.data.identityList.each(function(id){
                departments.push(id.department);
            }.bind(this));

            var dutys = [];
            //json.data.companyDutyList.each(function(duty){
            //    dutys.push(duty.name);
            //}.bind(this));
            //json.data.departmentDutyList.each(function(duty){
            //    dutys.push(duty.name);
            //}.bind(this));
            var text = (dutys.length) ?  departments.join(", ")+ "["+ dutys.join(", ")+"]" : departments.join(", ");
            this.userDutyTextInforNode.set("text", text);

            this.userSignInforNode.set("text", json.data.signature || MWF.LP.desktop.nosign);

            this.checkOnline();
            this.createPersonInforNode();
        }.bind(this), null, this.name);
    },
    createPersonInforNode: function(){
        var dutys = [];
        this.data.companyDutyList.each(function(duty){
            dutys.push(duty.name);
        }.bind(this));
        this.data.departmentDutyList.each(function(duty){
            dutys.push(duty.name);
        }.bind(this));

        this.personInforNode =  new Element("div", {"styles": this.css.personInforNode});
        var html = "<table width=\"100%\" cellpadding=\"3px\" border=\"0\">";
        html += "<tr><td>"+MWF.LP.desktop.person.personEmployee+"</td><td>"+this.data.employee+"</td></tr>";
        html += "<tr><td>"+MWF.LP.desktop.person.personMobile+"</td><td>"+this.data.mobile+"</td></tr>";
        html += "<tr><td>"+MWF.LP.desktop.person.personMail+"</td><td>"+this.data.mail+"</td></tr>"
        html += "<tr><td>"+MWF.LP.desktop.person.personQQ+"</td><td>"+this.data.qq+"</td></tr>";
        html += "<tr><td>"+MWF.LP.desktop.person.personWeixin+"</td><td>"+this.data.weixin+"</td></tr>";
        if (dutys.length) html += "<tr><td>"+MWF.LP.desktop.person.duty+"</td><td>"+dutys.join(", ")+"</td></tr>";
        html += "</table>";
        this.personInforNode.set("html", html);
    },
    load: function(){
        this.createNode();
        this.loadPerson();

        this.setEvent();
    },
    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){ this.node.setStyles(this.css.chatUserNode_over); }.bind(this),
            "mouseout": function(){ this.node.setStyles(this.css.chatUserNode); }.bind(this),
            "dblclick": function(e){this.openChat(e);}.bind(this),
            "click": function(e){this.openChat(e);}.bind(this)
        });
        this.userIconAreaNode.addEvents({
            "mouseover": function(){ this.showPersonInfor(); }.bind(this),
            "mouseout": function(){ this.hidePersonInfor(); }.bind(this)
        });
    },

    showPersonInfor: function(){
        this.personInforNode.inject(this.container);
        this.personInforNode.setStyle("display", "block");
        var size = this.personInforNode.getSize();
        var position = this.node.getPosition(this.node.getOffsetParent());
        var panelSize = this.panel.node.getSize();

        var top = position.y;
        var right = panelSize.x+5;

        this.personInforNode.setStyles({
            "top": ""+top+"px",
            "right": ""+right+"px"
        });
    },
    hidePersonInfor: function(){
        this.personInforNode.setStyle("display", "none");
    },
    destroy: function(){
        this.node.destroy();
        delete this.panel.users["online"+this.name];
        MWF.release(this);
        delete this;
    },
    openChat: function(e){
        var chat = layout.desktop.apps["Chat"];
        if (chat){
            var key = this.name+layout.desktop.session.user.name;
            var dialogue = chat.dialogues[key];
            if (!dialogue) dialogue = chat.addDialogue(this.panel.owner, [this.data]);
            this.unreadDatas.each(function(data){
                dialogue.showMessage(data, data.fromPerson);
            });
            dialogue.setCurrent();
            this.clearUnread();
        }

        var _self = this;
        layout.desktop.openApplication(e, "Chat", {
            "onPostLoad": function(){
                dialogue = this.addDialogue(_self.panel.owner, [_self.data]);
                _self.unreadDatas.each(function(data){
                    dialogue.showMessage(data, data.fromPerson);
                });
                _self.clearUnread();
            }
        });
    },

    addUnreadMessage: function(data){
        if (!this.unreadNode) this.createUnreadnode();
        this.unreadDatas.push(data);
        if (this.unreadDatas.length>100) this.unreadDatas.shift();

        this.unreadNode.set("text", this.unreadDatas.length);
        this.node.inject(this.container, "top");
    },
    createUnreadnode: function(){
        this.unreadNode = new Element("div", {"styles": this.css.userListUnreadNode}).inject(this.node, "bottom");
    },
    clearUnread: function(){
        if (this.unreadNode){
            this.unreadDatas = null;
            this.unreadDatas = [];
            this.unreadNode.destroy();
            this.unreadNode = null;
        }
    }



});