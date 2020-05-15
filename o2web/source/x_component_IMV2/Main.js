MWF.require("MWF.widget.UUID", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.IMV2.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "IMV2",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"width": "1024",
		"height": "768",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.IMV2.LP.title,
		"conversationId":""
	},
	onQueryLoad: function () {
		this.lp = MWF.xApplication.IMV2.LP;
		this.app = this;
		this.conversationNodeItemList = [];
		this.conversationId = this.options.conversationId || "";
		this.messageList = [];
	},
	onQueryClose: function(){
		console.log("关闭聊天窗口。。。。");
		this.closeListening()
	},
	loadApplication: function (callback) {
		var url = this.path + this.options.style + "/im.html";
		this.content.loadHtml(url, { "bind": { "lp": this.lp, "data": {} }, "module": this }, function () {
			//设置content
			this.app.content = this.o2ImMainNode;
			//启动监听
			this.startListening();
			//获取会话列表
			this.conversationNodeItemList = [];
			o2.Actions.load("x_message_assemble_communicate").ImAction.myConversationList(function (json) {
				if (json.data && json.data instanceof Array) {
					this.loadConversationList(json.data);
				}
			}.bind(this));

		}.bind(this));
	},
	startListening:function(){
		this.messageNumber = layout.desktop.message.items.length;
		//查询ws消息 如果增加
		if (this.listener) {
			clearInterval(this.listener);
		}
		this.listener = setInterval(function(){
			var newNumber = layout.desktop.message.items.length;
			//判断是否有新的ws消息
			if(newNumber > this.messageNumber) {
				//查询会话数据
				this._checkConversationMessage();
				//查询聊天数据
				this._checkNewMessage();
				this.messageNumber = newNumber;
			}
		}.bind(this), 1000);
	},
	closeListening: function() {
		if (this.listener) {
			clearInterval(this.listener);
		}
	},
	//加载会话列表
	loadConversationList: function (list) {
		for (var i = 0; i < list.length; i++) {
			var chat = list[i];
			var itemNode = this._createConvItemNode(chat);
			this.conversationNodeItemList.push(itemNode);
			if(this.conversationId && this.conversationId == chat.id) {
				this.tapConv(chat);
			}
		}
		console.log("结束");
	},
	//分页获取会话的消息列表数据
	loadMsgListByConvId: function (page, size, convId) {
		var data = { "conversationId": convId };
		o2.Actions.load("x_message_assemble_communicate").ImAction.msgListByPaging(page, size, data, function (json) {
			var list = json.data;
			
			for (var i = 0; i < list.length; i++) {
				this.messageList.push(list[i]);
				this._buildMsgNode(list[i], true);
			}
			console.log("聊天信息添加结束！");
		}.bind(this), function (error) {
			console.log(error);
		}.bind(this), false);
	},
	//点击会话
	tapConv: function (conv) {
		this._setCheckNode(conv);
		var url = this.path + this.options.style + "/chat.html";
		var data = { "convName": conv.title };
		this.conversationId = conv.id;
		this.chatNode.empty();
		this.chatNode.loadHtml(url, { "bind": data, "module": this }, function () {
			//获取聊天信息
			this.messageList = [];
			this.loadMsgListByConvId(1, 20, conv.id);
			console.log("开始滚动！！！");
			var scrollFx = new Fx.Scroll(this.chatContentNode);
			scrollFx.toBottom();
		}.bind(this));
	},
	//点击发送消息
	sendMsg: function () {
		var text = this.chatBottomAreaTextareaNode.value;
		console.log(text);
		if (text) {
			this.chatBottomAreaTextareaNode.value = "";
			this._newAndSendTextMsg(text);
		} else {
			console.log("没有消息内容！");
		}
	},
	//点击创建单聊按钮
	tapCreateSingleConv: function () {
		console.log("click tapCreateSingleConv................");
		var form = new MWF.xApplication.IMV2.SingleForm(this, {}, {}, { app: this.app });
		form.create()
	},
	/**
	 * 	创建会话
	 * @param {*} persons 人员列表
	 * @param {*} cType 会话类型 "single" "group"
	 */
	newConversation: function (persons, cType) {
		var conv = {
			type: cType,
			personList: persons,
		};
		var _self = this;
		o2.Actions.load("x_message_assemble_communicate").ImAction.create(conv, function (json) {
			var newConv = json.data;
			var isOld = false;
			for (var i = 0; i < _self.conversationNodeItemList.length; i++) {
				var c = _self.conversationNodeItemList[i];
				if (newConv.id  == c.data.id) {
					isOld = true;
					_self.tapConv(c);
				}
			}
			if(!isOld) {
				var itemNode = _self._createConvItemNode(newConv);
				_self.conversationNodeItemList.push(itemNode);
				_self.tapConv(newConv);
			}
			console.log("创建会话 结束。。。。。");
		}.bind(this), function (error) {
			console.log(error);
		}.bind(this))
	},
	//创建会话ItemNode
	_createConvItemNode: function (conv) {
		return new MWF.xApplication.IMV2.ConversationItem(conv, this);
	},
	//会话ItemNode 点击背景色
	_setCheckNode: function (conv) {
		for (var i = 0; i < this.conversationNodeItemList.length; i++) {
			var item = this.conversationNodeItemList[i];
			if (item.data.id == conv.id) {
				item.addCheckClass();
			} else {
				item.removeCheckClass();
			}
		}
	},
	//创建文本消息 并发送
	_newAndSendTextMsg: function (text) {
		var distinguishedName = layout.session.user.distinguishedName;
		var time = this._currentTime();
		var body = { "body": text };
		var bodyJson = JSON.stringify(body);
		var uuid = (new MWF.widget.UUID).toString();
		var textMessage = {
			"id": uuid,
			"conversationId": this.conversationId,
			"body": bodyJson,
			"createPerson": distinguishedName,
			"createTime": time,
			"sendStatus": 1
		};
		o2.Actions.load("x_message_assemble_communicate").ImAction.msgCreate(textMessage,
			function (json) {
				//data = json.data;
				console.log("消息发送成功！");
			}.bind(this),
			function (error) {
				console.log(error);
			}.bind(this));
		this.messageList.push(textMessage);
		this._buildSender(body, distinguishedName, false);
		this._refreshConvMessage(textMessage);
	},
	//刷新会话Item里面的最后消息内容
	_refreshConvMessage: function(msg) {
		for (var i = 0; i < this.conversationNodeItemList.length; i++) {
			var node = this.conversationNodeItemList[i];
			if (node.data.id == this.conversationId) {
				node.refreshLastMsg(msg);
			}
		}
	},
	//检查会话列表是否有更新
	_checkConversationMessage: function() {
		o2.Actions.load("x_message_assemble_communicate").ImAction.myConversationList(function (json) {
			if (json.data && json.data instanceof Array) {
				var newConList = json.data;
				console.log(newConList);
				for (var j = 0; j < newConList.length; j++) {
					var nCv = newConList[j];
					var isNew = true;
					for (var i = 0; i < this.conversationNodeItemList.length; i++) {
						var cv = this.conversationNodeItemList[i];
						if (cv.data.id == nCv.id) {
							isNew = false;
							//刷新
							cv.refreshLastMsg(nCv.lastMessage);
						}
					}
					//新会话 创建
					if(isNew) {
						var itemNode = this._createConvItemNode(nCv);
						this.conversationNodeItemList.push(itemNode);
					}
				}
				//this.loadConversationList(json.data);
			}
		}.bind(this));
	},
	//检查是否有新消息
	_checkNewMessage: function() {
		if (this.conversationId && this.conversationId != "") {//是否有会话窗口
			var data = { "conversationId": this.conversationId };
			o2.Actions.load("x_message_assemble_communicate").ImAction.msgListByPaging(1, 10, data, function (json) {
				var list = json.data;
				if(list && list.length > 0) {
					var msg = list[0];
					//检查聊天框是否有变化
					if (this.conversationId == msg.conversationId) {
						for (var i = 0; i < list.length; i++) {
							var isnew = true;
							var m = list[i];
							for (var j = 0; j < this.messageList.length; j++) {
								if(this.messageList[j].id == m.id) {
									isnew = false;
								}
							}
							if(isnew) {
								this.messageList.push(m);
								this._buildMsgNode(m, false);
								// this._refreshConvMessage(m);
							}
						}
					}
				}
				
			}.bind(this), function (error) {
				console.log(error);
			}.bind(this), false);
		}
	},
	//创建消息html节点
	_buildMsgNode: function (msg, isTop) {
		var createPerson = msg.createPerson;
		var jsonbody = msg.body;
		var body = JSON.parse(jsonbody);//todo 目前只有一种text类型
		var distinguishedName = layout.session.user.distinguishedName;
		if (createPerson != distinguishedName) {
			this._buildReceiver(body, createPerson, isTop);
		} else {
			this._buildSender(body, createPerson, isTop);
		}
	},
	/**
	 * 消息发送体
	 * @param  msgBody 消息体
	 * @param createPerson 消息人员
	 * @param isTop 是否放在顶部
	 */
	_buildSender: function (msgBody, createPerson, isTop) {
		var receiverBodyNode = new Element("div", { "class": "chat-sender" }).inject(this.chatContentNode, isTop ? "top" : "bottom");
		var avatarNode = new Element("div").inject(receiverBodyNode);
		var avatarUrl = this._getIcon(createPerson);
		var name = createPerson;
		if (createPerson.indexOf("@") != -1) {
			name = name.substring(0, createPerson.indexOf("@"));
		}
		var avatarImg = new Element("img", { "src": avatarUrl }).inject(avatarNode);
		var nameNode = new Element("div", { "text": name }).inject(receiverBodyNode);
		var lastNode = new Element("div").inject(receiverBodyNode);
		var lastFirstNode = new Element("div", { "class": "chat-left_triangle" }).inject(lastNode);
		//text
		var lastSecNode = new Element("span", { "text": msgBody.body }).inject(lastNode);
		if(!isTop) {
			var scrollFx = new Fx.Scroll(this.chatContentNode);
			scrollFx.toBottom();
		}
	},
	/**
	 * 消息接收体
	 * @param  msgBody 
	 * @param createPerson 消息人员
	 * @param isTop 是否放在顶部
	 */
	_buildReceiver: function (msgBody, createPerson, isTop) {
		var receiverBodyNode = new Element("div", { "class": "chat-receiver" }).inject(this.chatContentNode, isTop ? "top" : "bottom");
		var avatarNode = new Element("div").inject(receiverBodyNode);
		var avatarUrl = this._getIcon(createPerson);
		var name = createPerson;
		if (createPerson.indexOf("@") != -1) {
			name = name.substring(0, createPerson.indexOf("@"));
		}
		var avatarImg = new Element("img", { "src": avatarUrl }).inject(avatarNode);
		var nameNode = new Element("div", { "text": name }).inject(receiverBodyNode);
		var lastNode = new Element("div").inject(receiverBodyNode);
		var lastFirstNode = new Element("div", { "class": "chat-right_triangle" }).inject(lastNode);
		//text
		var lastSecNode = new Element("span", { "text": msgBody.body }).inject(lastNode);
		if(!isTop) {
			var scrollFx = new Fx.Scroll(this.chatContentNode);
			scrollFx.toBottom();
		}
	},
	//用户头像
	_getIcon: function (id) {
		var orgAction = MWF.Actions.get("x_organization_assemble_control")
		var url = (id) ? orgAction.getPersonIcon(id) : "/x_component_IMV2/$Main/default/icons/group.png";
		return url + "?" + (new Date().getTime());
	},
	//输出特殊的时间格式
	_friendlyTime: function (date) {
		var day = date.getDate();
		var monthIndex = date.getMonth();
		var year = date.getFullYear();
		var time = date.getTime();
		var today = new Date();
		var todayDay = today.getDate();
		var todayMonthIndex = today.getMonth();
		var todayYear = today.getFullYear();
		var todayTime = today.getTime();

		var retTime = "";
		//同一天
		if (day === todayDay && monthIndex === todayMonthIndex && year === todayYear) {
			var hour = 0;
			if (todayTime > time) {
				hour = parseInt((todayTime - time) / 3600000);
				if (hour == 0) {
					retTime = Math.max(parseInt((todayTime - time) / 60000), 1) + "分钟前"
				} else {
					retTime = hour + "小时前"
				}

			}
			return retTime;
		}
		var dates = parseInt(time / 86400000);
		var todaydates = parseInt(todayTime / 86400000);
		if (todaydates > dates) {
			var days = (todaydates - dates);
			if (days == 1) {
				retTime = "昨天";
			} else if (days == 2) {
				retTime = "前天 ";
			} else if (days > 2 && days < 31) {
				retTime = days + "天前";
			} else if (days >= 31 && days <= 2 * 31) {
				retTime = "一个月前";
			} else if (days > 2 * 31 && days <= 3 * 31) {
				retTime = "2个月前";
			} else if (days > 3 * 31 && days <= 4 * 31) {
				retTime = "3个月前";
			} else {
				retTime = this._formatDate(date);
			}
		}

		return retTime;

	},
	//yyyy-MM-dd
	_formatDate: function (date) {
		var month = date.getMonth() + 1;
		var day = date.getDate();
		month = (month.toString().length == 1) ? ("0" + month) : month;
		day = (day.toString().length == 1) ? ("0" + day) : day;
		return date.getFullYear() + '-' + month + '-' + day;
	},
	//当前时间 yyyy-MM-dd HH:mm:ss
	_currentTime: function () {
		var today = new Date();
		var year = today.getFullYear(); //得到年份
		var month = today.getMonth();//得到月份
		var date = today.getDate();//得到日期
		var hour = today.getHours();//得到小时
		var minu = today.getMinutes();//得到分钟
		var sec = today.getSeconds();//得到秒
		month = month + 1;
		if (month < 10) month = "0" + month;
		if (date < 10) date = "0" + date;
		if (hour < 10) hour = "0" + hour;
		if (minu < 10) minu = "0" + minu;
		if (sec < 10) sec = "0" + sec;
		return year + "-" + month + "-" + date + " " + hour + ":" + minu + ":" + sec;
	}


});

//会话对象
MWF.xApplication.IMV2.ConversationItem = new Class({
	initialize: function (data, main) {
		this.data = data;
		this.main = main;
		this.container = this.main.chatItemListNode;

		this.load();
	},
	load: function () {
		var avatarDefault = this.main._getIcon();
		var convData = {
			"id": this.data.id,
			"avatarUrl": avatarDefault,
			"title": this.data.title,
			"time": "",
			"lastMessage": ""
		};
		var distinguishedName = layout.session.user.distinguishedName;
		if (this.data.type && this.data.type === "single") {
			var chatPerson = "";
			if (this.data.personList && this.data.personList instanceof Array) {
				for (var j = 0; j < this.data.personList.length; j++) {
					var person = this.data.personList[j];
					if (person !== distinguishedName) {
						chatPerson = person;
					}
				}
			}
			convData.avatarUrl = this.main._getIcon(chatPerson);
			var name = chatPerson;
			if (chatPerson.indexOf("@") != -1) {
				name = name.substring(0, chatPerson.indexOf("@"));
			}
			convData.title = name;
		}
		if (this.data.lastMessage) {
			//todo 其它消息类型
			var mBody = JSON.parse(this.data.lastMessage.body);
			convData.lastMessage = mBody.body;
			if (this.data.lastMessage.createTime) {
				var time = this.main._friendlyTime(o2.common.toDate(this.data.lastMessage.createTime));
				convData.time = time;
			}
		}
		this.node = new Element("div", { "class": "item" }).inject(this.container);
		this.nodeBaseItem = new Element("div", { "class": "base" }).inject(this.node);
		var avatarNode = new Element("div", { "class": "avatar" }).inject(this.nodeBaseItem);
		new Element("img", { "src": convData.avatarUrl, "class": "img" }).inject(avatarNode);
		var bodyNode = new Element("div", { "class": "body" }).inject(this.nodeBaseItem);
		var bodyUpNode = new Element("div", { "class": "body_up" }).inject(bodyNode);
		new Element("div", { "class": "body_title", "text": convData.title }).inject(bodyUpNode);
		this.messageTimeNode = new Element("div", { "class": "body_time", "text": convData.time }).inject(bodyUpNode);
		this.lastMessageNode = new Element("div", { "class": "body_down", "text": convData.lastMessage }).inject(bodyNode);
		var _self = this;
		this.node.addEvents({
			"click": function () {
				_self.main.tapConv(_self.data);
			}
		});
	},
	/**
	 * {
			"id": uuid,
			"conversationId": this.conversationId,
			"body": bodyJson,
			"createPerson": distinguishedName,
			"createTime": time,
			"sendStatus": 1
		};
	 * 刷新会话列表的最后消息内容 
	 * @param {*} lastMessage 
	 */
	refreshLastMsg: function(lastMessage) {
		//目前是text 类型的消息
		var jsonbody = lastMessage.body;
		var body = JSON.parse(jsonbody);//todo 目前只有一种text类型
		if(this.lastMessageNode) {
			this.lastMessageNode.set('text', body.body);
		}
		var time = this.main._friendlyTime(o2.common.toDate(lastMessage.createTime));
		if(this.messageTimeNode) {
			this.messageTimeNode.set("text", time);
		}
	},
	addCheckClass: function () {
		if (this.nodeBaseItem) {
			if (!this.nodeBaseItem.hasClass("check")) {
				this.nodeBaseItem.addClass("check");
			}
		}
	},
	removeCheckClass: function () {
		if (this.nodeBaseItem) {
			if (this.nodeBaseItem.hasClass("check")) {
				this.nodeBaseItem.removeClass("check");
			}
		}
	}

});

//弹出窗 表单 单聊创建的form
MWF.xApplication.IMV2.SingleForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		"style": "minder",
		"width": 700,
		//"height": 300,
		"height": "200",
		"hasTop": true,
		"hasIcon": false,
		"draggable": true,
		"title": "创建单聊"
	},
	_createTableContent: function () {
		var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
			"<tr><td styles='formTableTitle' lable='person' width='25%'></td>" +
			"    <td styles='formTableValue14' item='person' colspan='3'></td></tr>" +
			"</table>";
		this.formTableArea.set("html", html);
		var me = layout.session.user.distinguishedName;
		var exclude = [];
		if (me) {
			exclude = [me];
		}
		this.form = new MForm(this.formTableArea, this.data || {}, {
			isEdited: true,
			style: "minder",
			hasColon: true,
			itemTemplate: {
				person: { text: "选择人员", type: "org", orgType: "person", notEmpty: true, exclude: exclude },
			}
		}, this.app);
		this.form.load();

	},
	_createBottomContent: function () {
		if (this.isNew || this.isEdited) {
			this.okActionNode = new Element("button.inputOkButton", {
				"styles": this.css.inputOkButton,
				"text": "确定"
			}).inject(this.formBottomNode);
			this.okActionNode.addEvent("click", function (e) {
				this.save(e);
			}.bind(this));
		}
		this.cancelActionNode = new Element("button.inputCancelButton", {
			"styles": (this.isEdited || this.isNew || this.getEditPermission()) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
			"text": "关闭"
		}).inject(this.formBottomNode);
		this.cancelActionNode.addEvent("click", function (e) {
			this.close(e);
		}.bind(this));

	},
	save: function () {
		var data = this.form.getResult(true, null, true, false, true);
		if (data) {
			console.log(data);
			this.app.newConversation(data.person, "single");
			this.close();
		}
	}
});