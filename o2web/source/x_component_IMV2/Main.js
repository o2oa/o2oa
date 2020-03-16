MWF.xApplication.IMV2.options.multitask = true;
MWF.xApplication.IMV2.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "IMV2",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"width": "1200",
		"height": "700",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.IMV2.LP.title
	},
	onQueryLoad: function () {
		this.lp = MWF.xApplication.IMV2.LP;
		this.conversationList = [];
	},
	loadApplication: function (callback) {
		var url = this.path + this.options.style + "/im.html";
		this.content.loadHtml(url, { "bind": { "lp": this.lp, "data": {} }, "module": this }, function () {
			//获取会话列表
			o2.Actions.load("x_message_assemble_communicate").ImAction.myConversationList(function (json) {
				if (json.data && json.data instanceof Array) {
					this.conversationList = json.data;
					this.loadConversationList(json.data);
				}
			}.bind(this));

		}.bind(this));
	},
	//加载会话列表
	loadConversationList: function (list) {
		for (var i = 0; i < list.length; i++) {
			var chat = list[i];
			var url = this.path + this.options.style + "/conversationItem.html";
			var avatarDefault = this._getIcon();
			var data = {
				"id": chat.id,
				"avatarUrl": avatarDefault,
				"title": chat.title,
				"time": "",
				"lastMessage": ""
			};
			var distinguishedName = layout.session.user.distinguishedName;
			if (chat.type && chat.type === "single") {
				var chatPerson = "";
				if (chat.personList && chat.personList instanceof Array) {
					for (var i = 0; i < chat.personList.length; i++) {
						var person = chat.personList[i];
						if (person !== distinguishedName) {
							chatPerson = person;
						}
					}
				}
				data.avatarUrl = this._getIcon(chatPerson);
				var name = chatPerson;
				if (chatPerson.indexOf("@") != -1) {
					name = name.substring(0, chatPerson.indexOf("@"));
				}
				data.title = name;
			}
			if (chat.lastMessage) {
				//todo 其它消息类型
				var mBody = JSON.parse(chat.lastMessage.body);
				data.lastMessage = mBody.body;
				if (chat.lastMessage.createTime) {
					var time = this._friendlyTime(o2.common.toDate(chat.lastMessage.createTime));
					data.time = time;
				}
			}
			this.chatItemListNode.loadHtml(url, { "bind": data, "module": this }, function (html) {
				//bind event

				console.log(html);
			}.bind(this));
		}
		console.log("结束");
	},
	//点击
	tapConv: function (e) {
		console.log("clickConversationvvvvvv");
		console.log(e);
		var url = this.path + this.options.style + "/chat.html";
		this.chatNode.loadHtml(url, { "bind": {}, "module": this }, function () {
		}.bind(this));
	},



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
	}


});
