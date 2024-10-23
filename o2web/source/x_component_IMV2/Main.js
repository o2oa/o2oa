MWF.require("MWF.widget.UUID", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.IMV2 = MWF.xApplication.IMV2 || {};
MWF.xApplication.IMV2.options.multitask = false; //多窗口
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
		"conversationId": "", // 传入的当前会话id
		"mode": "default" // 展现模式：default onlyChat 。 onlyChat的模式需要传入conversationId 会打开这个会话的聊天窗口并隐藏左边的会话列表
	},
	onQueryLoad: function () {
		this.lp = MWF.xApplication.IMV2.LP;
		this.app = this;
		this.conversationNodeItemList = [];
		this.messageList = [];
		this.emojiV2TypeList = ["smile", "animals", "food", "activities", "travel", "objects","symbol"];
		this.emojiV2Object = {}; // 新版字符表情对象 对象里面按照上面 emojiV2TypeList里面的名称进行分类
		this.emojiList = [];
		//添加87个表情
		for (var i = 1; i < 88; i++) {
			var emoji = {
				"key": i > 9 ? "[" + i + "]" : "[0" + i + "]",
				"path": i > 9 ? "/x_component_IMV2/$Main/emotions/im_emotion_" + i + ".png" : "/x_component_IMV2/$Main/emotions/im_emotion_0" + i + ".png",
			};
			this.emojiList.push(emoji);
		}
		
		if (!this.status) {
			this.conversationId = this.options.conversationId || "";
			this.mode = this.options.mode || "default";
		} else {
			this.conversationId = this.status.conversationId || "";
			this.mode = this.status.mode || "default";
		}
	},
	// 加载新版本的 emoji 字符表情
	_loadNewEmoji: function () {
		const emojiJsonPath = this.path + this.options.style + "/emoji.json";
		// 使用 fetch 获取 JSON 数据
		fetch(emojiJsonPath)
			.then(response => {
				if (!response.ok) {
					console.error('网络响应错误, emoji.json读取错误');
				}
				return response.json(); // 解析为 JSON
			})
			.then(data => {
				 this.emojiV2Object = data;
			})
			.catch(error => {
				console.error('请求失败:', error);
			});
	},
	// 刷新的时候缓存数据
	recordStatus: function(){
		return {"conversationId": this.conversationId, "mode": this.mode};
	},
	onQueryClose: function () {
		// this.closeListening();
	},
	// 获取组件名称
	loadComponentName: function () {
		o2.Actions.load("x_component_assemble_control").ComponentAction.get("IMV2", function (json) {
			var imComponent = json.data;
			if (imComponent && imComponent.title) {
				this.setTitle(imComponent.title);
			}
		}.bind(this), function (err) {
			console.error(err);
		})
	},
	// 加载应用
	loadApplication: function (callback) {
		// 判断xadmin 打开聊天功能
		if (layout.session.user && layout.session.user.name == "xadmin") {
			console.log("xadmin can not open IMV2");
			this.app.notice(this.lp.messageXadminNotSupport, "error");
			return;
		}
		this._loadNewEmoji(); // 加载 emoji json 对象
		// 先加载配置文件 放入imConfig对象
		MWF.xDesktop.loadConfig(function () {
			this.imConfig = layout.config.imConfig || {}
			var url = this.path + this.options.style + "/im.html";
			this.content.loadHtml(url, { "bind": { "lp": this.lp, "data": {} }, "module": this }, function () {
				//设置content
				this.app.content = this.o2ImMainNode;
				// 给websocket 添加撤回消息回调函数
				if (layout.desktop && layout.desktop.socket && layout.desktop.socket.addImListener) {
					layout.desktop.socket.addImListener("im_revoke", this.revokeMsgCallback.bind(this));
					layout.desktop.socket.addImListener("im_create", this.createNewMsgCallback.bind(this));
					layout.desktop.socket.addImListener("im_conversation", this.conversationMsgCallback.bind(this));
				}
				//启动监听
				// this.startListening();
				// 处理窗口模式
				if (this.mode === "onlyChat" && this.conversationId != "") {
					this.o2ConversationListNode.setStyle("display", "none");
					this.chatContainerNode.setStyle("margin-left", "2px");
				} else {
					this.o2ConversationListNode.setStyle("display", "flex");
					this.chatContainerNode.setStyle("margin-left", "259px");
				}

				//获取会话列表
				this.conversationNodeItemList = [];
				o2.Actions.load("x_message_assemble_communicate").ImAction.myConversationList(function (json) {
					if (json.data && json.data instanceof Array) {
						this.loadConversationList(json.data);
					}
				}.bind(this));
				// 管理员可见设置按钮
				if (MWF.AC.isAdministrator()) {
					this.o2ImAdminSettingNode.removeAttribute("style");
				} else {
					this.o2ImAdminSettingNode.setStyle("display", "none");
				}
			}.bind(this));
		}.bind(this));
		
		this.loadComponentName();
	},
	openCollectionListPage: function () {
		if (this.chatNodeBox) {
			this.chatNodeBox.openMyCollection();
		}
	},
	// 撤回消息回调
	revokeMsgCallback: function(msg) {
		if (this.chatNodeBox) {
			this.chatNodeBox._checkRevokeMsg(msg);
		}
	},
	// websocket过来的新消息回调
	createNewMsgCallback: function(msg) {
		console.log('=======> msg ', msg)
		this.reciveNewMessage();
	},
	// 接收新的消息 会话列表更新 或者 聊天窗口更新
	reciveNewMessage: function () {
		//查询会话数据
		this._checkConversationMessage();
		//查询聊天数据
		if (this.chatNodeBox) {
			this.chatNodeBox._checkNewMessage();
		}
	},
	// 收到会话变更或删除消息
	conversationMsgCallback: function(conv) {
		console.debug("会话消息处理", conv);
		if (conv && conv.id) {
			//  查询会话
			o2.Actions.load("x_message_assemble_communicate").ImAction.conversation(conv.id, function (json) {
				if (json && json.data) {
					var newConv = json.data;
					var personList = newConv.personList || [];
					var distinguishedName = layout.session.user.distinguishedName;
					if (personList.indexOf(distinguishedName) > -1) { // 成员存在 更新会话
						for (var i = 0; i < this.conversationNodeItemList.length; i++) {
							var cv = this.conversationNodeItemList[i];
							if (cv.data.id == conv.id) {
								cv.refreshData(conv);
							}
						}
					} else { // 被踢出了 删除会话
						this._deleteConversation(conv)
					}
				}
				
			}.bind(this), function(err){
				console.error(err);
				// 出错 可能是会话删除了
				this._deleteConversation(conv);
				return true;
			}.bind(this));
		}
	},
	// 删除会话
	_deleteConversation(conv) {
		for (var i = 0; i < this.conversationNodeItemList.length; i++) {
			var item = this.conversationNodeItemList[i];
			if (item.data.id === conv.id) {
				item.node.destroy();
				this.conversationNodeItemList.splice(i, 1);
				break;
			}
		}
		// 当前聊天窗口 关闭
		if (this.conversationId && this.conversationId === conv.id) {
			this.chatContainerNode.empty();
			this.conversationId = null;
		}
	},
	//加载会话列表
	loadConversationList: function (list) {
		for (var i = 0; i < list.length; i++) {
			var chat = list[i];
			var itemNode = this._createConvItemNode(chat);
			this.conversationNodeItemList.push(itemNode);
			if (this.conversationId && this.conversationId == chat.id) {
				this.tapConv(chat);
			}
		}
		// 初始情况 打开第一个
		if(!this.conversationId && this.conversationNodeItemList.length > 0) {
			this.tapConv(this.conversationNodeItemList[0].data);
		}
	},
	
	// 点击设置按钮
	tapOpenSettings: function() {
		this.openSettingsDialog();
	},
	// 打开IM配置文件
	openSettingsDialog: function () {
		var settingNode = new Element("div", {"style":"padding:10px;background-color:#fff;"});

		var lineNode = new Element("div", {"style":"height:24px;line-height: 24px;"}).inject(settingNode);
		var isClearEnableNode = new Element("input", {"type":"checkbox",  "name": "clearEnable"}).inject(lineNode);
		isClearEnableNode.checked = this.imConfig.enableClearMsg || false
		new Element("span", { "text": this.lp.settingsClearMsg}).inject(lineNode);

		var line2Node = new Element("div", {"style":"height:24px;line-height: 24px;margin-top: 10px;"}).inject(settingNode);
		var isRevokeEnableNode = new Element("input", {"type":"checkbox", "name": "revokeEnable"}).inject(line2Node);
		isRevokeEnableNode.checked = this.imConfig.enableRevokeMsg || false;
		new Element("span", { "text": this.lp.settingsRevokeMsg}).inject(line2Node);

		var line3Node = new Element("div", {"style":"height:24px;line-height: 24px;margin-top: 10px;"}).inject(settingNode);
		var revokeOutMinuteNode = new Element("input", {"type":"number", "value": this.imConfig.revokeOutMinute ?? 2, "name": "revokeEnable"}).inject(line3Node);
		new Element("span", { "text": this.lp.settingsRevokeOutMinuteMsg}).inject(line3Node);

		var line4Node = new Element("div", {"style":"height:24px;line-height: 24px;margin-top: 10px;"}).inject(settingNode);
		var conversationCheckInvokeNode = new Element("input", {"type":"text", "value": this.imConfig.conversationCheckInvoke ?? "", "name": "revokeEnable"}).inject(line4Node);
		new Element("span", { "text": this.lp.settingsConversationCheckInvokeMsg}).inject(line4Node);

		var line5Node = new Element("div", {"style":"height:24px;line-height: 24px;margin-top: 10px;"}).inject(settingNode);
		var enableOnlyOfficePreviewNode = new Element("input", {"type":"checkbox",  "name": "enableOnlyOfficePreview"}).inject(line5Node);
		enableOnlyOfficePreviewNode.checked = this.imConfig.enableOnlyOfficePreview || false
		new Element("span", { "text": this.lp.settingsEnableOnlyOfficePreviewMsg}).inject(line5Node);


		var dlg = o2.DL.open({
				"title": this.lp.setting,
				"mask": true,
				"width": '500',
				"height": "310",
				"content": settingNode,
				"onQueryClose": function () {
					settingNode.destroy();
				}.bind(this),
				"buttonList": [
					{
						"type": "ok",
						"text": this.lp.ok,
						"action": function () { 
							this.imConfig.enableClearMsg = isClearEnableNode.checked;
							this.imConfig.enableRevokeMsg = isRevokeEnableNode.checked;
							this.imConfig.enableOnlyOfficePreview = enableOnlyOfficePreviewNode.checked;
							this.imConfig.revokeOutMinute = revokeOutMinuteNode.get("value") ?? 2;
							if (this.imConfig.revokeOutMinute <= 0 ) {
								this.imConfig.revokeOutMinute = 2;
							}
							this.imConfig.conversationCheckInvoke = (conversationCheckInvokeNode.get("value") ?? "").trim();
							console.debug(this.imConfig)
							this.postIMConfig(this.imConfig);
							// 保存配置文件
							dlg.close(); 
						}.bind(this)
					},
					{
							"type": "cancel",
							"text": this.lp.close,
							"action": function () { dlg.close(); }
					}
				],
				"onPostShow": function () {
						dlg.reCenter();
				}.bind(this),
				"onPostClose": function(){
					dlg = null;
				}.bind(this)
		});
	},
	// 保存IM配置文件
	postIMConfig: function (imConfig) {
		o2.Actions.load("x_message_assemble_communicate").ImAction.config(imConfig, function (json) {
			this.refresh();//重新加载整个IM应用
		}.bind(this), function (error) {
			console.error(error);
			this.app.notice(error, "error", this.app.content);
		}.bind(this));
	},
	//点击会话
	tapConv: function (conv) {
		this._setCheckNode(conv);
		this.conversationId = conv.id;
		// new ChatNodeBox
		this.chatNodeBox = new MWF.xApplication.IMV2.ChatNodeBox(conv, this);
	},
	tapChoosePerson: function() {
		MWF.requireApp("Selector","package", function(){
			new MWF.O2Selector(document.body,  {
				"type": 'identity',
				"count": 0,
				"title": '通讯录',
				"firstLevelSelectable": true,
				"resultType": "person",
				"onPostLoadContent": function () {
					this.titleTextNode.set("text", '通讯录')
				},
				"onComplete": function(items) {
					console.log(items)
					if (items && items.length > 0) {
						let personList = items.map(i => i.data.distinguishedName)
						const me = layout.session.user.distinguishedName;
						personList = personList.filter(p => p !== me)
						if (personList.length === 0 ) {
							this.app.notice(this.lp.msgNeedChoosePerson, "error");
						} else {
							this.newConversation(personList, personList.length === 1 ? "single" : "group")
						}
					}
				}.bind(this)
			})
		}.bind(this));
	},
	//点击创建单聊按钮
	tapCreateSingleConv: function () {
		// var form = new MWF.xApplication.IMV2.SingleForm(this, {}, {}, { app: this.app });
		// form.create()
		var form = new MWF.xApplication.IMV2.CreateConversationForm(this, {}, { "title": this.lp.createSingle, "personCount": 1 }, { app: this.app });
		form.create()
	},
	//点击创建群聊按钮
	tapCreateGroupConv: function () {
		var form = new MWF.xApplication.IMV2.CreateConversationForm(this, {}, { "title": this.lp.createGroup, "personCount": 0, "personSelected": [] }, { app: this.app });
		form.create()
	},
	//更新群名
	updateConversationTitle: function (title, convId) {
		var conv = {
			id: convId,
			title: title,
		};
		var _self = this;
		o2.Actions.load("x_message_assemble_communicate").ImAction.update(conv, function (json) {
			var newConv = json.data;
			//点击会话 刷新聊天界面
			// _self.tapConv(newConv);
			// //刷新会话列表的title
			// for (var i = 0; i < this.conversationNodeItemList.length; i++) {
			// 	var cv = this.conversationNodeItemList[i];
			// 	if (cv.data.id == convId) {
			// 		//刷新
			// 		cv.refreshConvTitle(title);
			// 	}
			// }
			// 列表上的数据也要刷新
			_self.reciveNewMessage();

		}.bind(this), function (error) {
			console.error(error);
		}.bind(this))
	},
	//更新群成员
	updateConversationMembers: function (members, convId) {
		var conv = {
			id: convId,
			personList: members,
		};
		var _self = this;
		o2.Actions.load("x_message_assemble_communicate").ImAction.update(conv, function (json) {
			var newConv = json.data;
			//_self.tapConv(newConv);
			// 列表上的数据也要刷新
			_self.reciveNewMessage();
		}.bind(this), function (error) {
			console.error(error);
		}.bind(this))
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
		MWF.require("MWF.widget.Mask", function () {
			this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
			this.mask.loadNode(this.app.content);
			o2.Actions.load("x_message_assemble_communicate").ImAction.create(conv, function (json) {
				var newConv = json.data;
				var isOld = false;
				for (var i = 0; i < this.conversationNodeItemList.length; i++) {
					var c = this.conversationNodeItemList[i];
					if (newConv.id == c.data.id) {
						isOld = true;
						this.tapConv(c.data);
						break;
					}
				}
				if (!isOld) {
					newConv.isNew = true; // 新建的 放在列表的前面
					var itemNode = this._createConvItemNode(newConv);
					this.conversationNodeItemList.unshift(itemNode);
					this.tapConv(newConv);
				}
				if (this.mask) { this.mask.hide(); this.mask = null; }
			}.bind(this), function (error) {
				console.error(error);
				if (this.mask) { this.mask.hide(); this.mask = null; }
			}.bind(this));
		}.bind(this));

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
	
	//刷新会话Item里面的最后消息内容
	_refreshConvMessage: function (msg) {
		var isIn = false;
		for (var i = 0; i < this.conversationNodeItemList.length; i++) {
			var node = this.conversationNodeItemList[i];
			if (node.data.id === msg.conversationId) {
				node.refreshLastMsg(msg);
				isIn = true;
			}
		}
		if (!isIn) {
			this.reciveNewMessage();
		}
	},
	//检查会话列表是否有更新
	_checkConversationMessage: function () {
		o2.Actions.load("x_message_assemble_communicate").ImAction.myConversationList(function (json) {
			if (json.data && json.data instanceof Array) {
				var newConList = json.data;
				for (var j = 0; j < newConList.length; j++) {
					var nCv = newConList[j];
					var isNew = true;
					for (var i = 0; i < this.conversationNodeItemList.length; i++) {
						var cv = this.conversationNodeItemList[i];
						if (cv.data.id == nCv.id) {
							isNew = false;
							//刷新
							cv.refreshLastMsg(nCv.lastMessage);
							cv.refreshData(nCv);
							// if (this.conversationId === nCv.id) {
							// 	this.tapConv(nCv);
							// }
						}
					}
					//新会话 创建
					if (isNew) {
						nCv.isNew = true; // 新建的 放在列表的前面
						var itemNode = this._createConvItemNode(nCv);
						this.conversationNodeItemList.unshift(itemNode);
					}
				}
				//this.loadConversationList(json.data);
			}
		}.bind(this));
	},

	//用户头像
	_getIcon: function (id) {
		var orgAction = MWF.Actions.get("x_organization_assemble_control")
		var url = (id) ? orgAction.getPersonIcon(id) : "../x_component_IMV2/$Main/default/icons/group.png";
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
					retTime = Math.max(parseInt((todayTime - time) / 60000), 1) + this.lp.minutesBefore
				} else {
					retTime = hour + this.lp.hoursBefore
				}

			}
			return retTime;
		}
		var dates = parseInt(time / 86400000);
		var todaydates = parseInt(todayTime / 86400000);
		if (todaydates > dates) {
			var days = (todaydates - dates);
			if (days == 1) {
				retTime = this.lp.yesterday;
			} else if (days == 2) {
				retTime = this.lp.beforeYesterday;
			} else if (days > 2 && days < 31) {
				retTime = days + this.lp.daysBefore;
			} else if (days >= 31 && days <= 2 * 31) {
				retTime = this.lp.monthAgo;
			} else if (days > 2 * 31 && days <= 3 * 31) {
				retTime = this.lp.towMonthAgo;
			} else if (days > 3 * 31 && days <= 4 * 31) {
				retTime = this.lp.threeMonthAgo;
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

// 聊天窗口
MWF.xApplication.IMV2.ChatNodeBox = new Class({
	initialize: function (data, main) {
		this.data = data;
		this.main = main;
		this.app = main.app;
		this.container = this.main.chatContainerNode;
		this.lp = this.main.lp;
		this.path = this.main.path;
		this.options = this.main.options;
		this.pageSize = 20;
		this.page = 1;
		this.isLoading = false; // 正在加载
		this.hasMoreMsgData = false; // 是否还有更多的消息 翻页
		this.selectMode = false; // 选择模式
		this.selectMsgList = []; // 选择的列表
		this.quoteMessage = null; // 引用消息
		this.load();
	},
	htmlSymbols: function() {
		return  {
			'"': '&quot;',
			'\'': '&#039;',
			'>': '&gt;',
			'¡': '&iexcl;',
			'£': '&pound;',
			'¥': '&yen;',
			'§': '&sect;',
			'©': '&copy;',
			'«': '&laquo;',
			'®': '&reg;',
			'¯': '&macr;',
			'±': '&plusmn;',
			'³': '&sup3;',
			'µ': '&micro;',
			'·': '&middot;',
			'¹': '&sup1;',
			'»': '&raquo;',
			'½': '&frac12;',
			'¿': '&iquest;',
			'Á': '&Aacute;',
			'Ã': '&Atilde;',
			'Å': '&Aring;',
			'Ç': '&Ccedil;',
			'É': '&Eacute;',
			'Ë': '&Euml;',
			'Í': '&Iacute;',
			'Ï': '&Iuml;',
			'Ñ': '&Ntilde;',
			'Ó': '&Oacute;',
			'Õ': '&Otilde;',
			'×': '&times;',
			'Ù': '&Ugrave;',
			'Û': '&Ucirc;',
			'Ý': '&Yacute;',
			'ß': '&szlig;',
			'á': '&aacute;',
			'ã': '&atilde;',
			'å': '&aring;',
			'ç': '&ccedil;',
			'é': '&eacute;',
			'ë': '&euml;',
			'í': '&iacute;',
			'ï': '&iuml;',
			'ñ': '&ntilde;',
			'ó': '&oacute;',
			'õ': '&otilde;',
			'÷': '&divide;',
			'ù': '&ugrave;',
			'û': '&ucirc;',
			'ý': '&yacute;',
			'ÿ': '&yuml;',
			'œ': '&oelig;',
			'š': '&scaron;',
			'ƒ': '&fnof;',
			'˜': '&tilde;',
			'Β': '&Beta;',
			'Δ': '&Delta;',
			'Ζ': '&Zeta;',
			'Θ': '&Theta;',
			'Κ': '&Kappa;',
			'Μ': '&Mu;',
			'Ξ': '&Xi;',
			'Π': '&Pi;',
			'Σ': '&Sigma;',
			'Υ': '&Upsilon;',
			'Χ': '&Chi;',
			'Ω': '&Omega;',
			'β': '&beta;',
			'δ': '&delta;',
			'ζ': '&zeta;',
			'θ': '&theta;',
			'κ': '&kappa;',
			'μ': '&mu;',
			'ξ': '&xi;',
			'π': '&pi;',
			'ς': '&sigmaf;',
			'τ': '&tau;',
			'φ': '&phi;',
			'ψ': '&psi;',
			'ϑ': '&thetasym;',
			'ϖ': '&piv;',
			'–': '&ndash;',
			'‘': '&lsquo;',
			'‚': '&sbquo;',
			'”': '&rdquo;',
			'†': '&dagger;',
			'•': '&bull;',
			'‰': '&permil;',
			'″': '&Prime;',
			'›': '&rsaquo;',
			'⁄': '&frasl;',
			'ℑ': '&image;',
			'ℜ': '&real;',
			'ℵ': '&alefsym;',
			'↑': '&uarr;',
			'↓': '&darr;',
			'↵': '&crarr;',
			'⇑': '&uArr;',
			'⇓': '&dArr;',
			'∀': '&forall;',
			'∃': '&exist;',
			'∇': '&nabla;',
			'∉': '&notin;',
			'∏': '&prod;',
			'−': '&minus;',
			'√': '&radic;',
			'∞': '&infin;',
			'∧': '&and;',
			'∩': '&cap;',
			'∫': '&int;',
			'∼': '&sim;',
			'≈': '&asymp;',
			'≡': '&equiv;',
			'≥': '&ge;',
			'⊃': '&sup;',
			'⊆': '&sube;',
			'⊕': '&oplus;',
			'⊥': '&perp;',
			'⌈': '&lceil;',
			'⌊': '&lfloor;',
			'⟨': '&lang;',
			'◊': '&loz;',
			'♣': '&clubs;',
			'♦': '&diams;',
			'&': '&amp;',
			'<': '&lt;',
			' ': '&nbsp;',
			'¢': '&cent;',
			'¤': '&curren;',
			'¦': '&brvbar;',
			'¨': '&uml;',
			'ª': '&ordf;',
			'¬': '&not;',
			'°': '&deg;',
			'²': '&sup2;',
			'´': '&acute;',
			'¶': '&para;',
			'¸': '&cedil;',
			'º': '&ordm;',
			'¼': '&frac14;',
			'¾': '&frac34;',
			'À': '&Agrave;',
			'Â': '&Acirc;',
			'Ä': '&Auml;',
			'Æ': '&AElig;',
			'È': '&Egrave;',
			'Ê': '&Ecirc;',
			'Ì': '&Igrave;',
			'Î': '&Icirc;',
			'Ð': '&ETH;',
			'Ò': '&Ograve;',
			'Ô': '&Ocirc;',
			'Ö': '&Ouml;',
			'Ø': '&Oslash;',
			'Ú': '&Uacute;',
			'Ü': '&Uuml;',
			'Þ': '&THORN;',
			'à': '&agrave;',
			'â': '&acirc;',
			'ä': '&auml;',
			'æ': '&aelig;',
			'è': '&egrave;',
			'ê': '&ecirc;',
			'ì': '&igrave;',
			'î': '&icirc;',
			'ð': '&eth;',
			'ò': '&ograve;',
			'ô': '&ocirc;',
			'ö': '&ouml;',
			'ø': '&oslash;',
			'ú': '&uacute;',
			'ü': '&uuml;',
			'þ': '&thorn;',
			'Œ': '&OElig;',
			'Š': '&Scaron;',
			'Ÿ': '&Yuml;',
			'ˆ': '&circ;',
			'Α': '&Alpha;',
			'Γ': '&Gamma;',
			'Ε': '&Epsilon;',
			'Η': '&Eta;',
			'Ι': '&Iota;',
			'Λ': '&Lambda;',
			'Ν': '&Nu;',
			'Ο': '&Omicron;',
			'Ρ': '&Rho;',
			'Τ': '&Tau;',
			'Φ': '&Phi;',
			'Ψ': '&Psi;',
			'α': '&alpha;',
			'γ': '&gamma;',
			'ε': '&epsilon;',
			'η': '&eta;',
			'ι': '&iota;',
			'λ': '&lambda;',
			'ν': '&nu;',
			'ο': '&omicron;',
			'ρ': '&rho;',
			'σ': '&sigma;',
			'υ': '&upsilon;',
			'χ': '&chi;',
			'ω': '&omega;',
			'ϒ': '&upsih;',
			'—': '&mdash;',
			'’': '&rsquo;',
			'“': '&ldquo;',
			'„': '&bdquo;',
			'‡': '&Dagger;',
			'…': '&hellip;',
			'′': '&prime;',
			'‹': '&lsaquo;',
			'‾': '&oline;',
			'€': '&euro;',
			'℘': '&weierp;',
			'™': '&trade;',
			'←': '&larr;',
			'→': '&rarr;',
			'↔': '&harr;',
			'⇐': '&lArr;',
			'⇒': '&rArr;',
			'⇔': '&hArr;',
			'∂': '&part;',
			'∅': '&empty;',
			'∈': '&isin;',
			'∋': '&ni;',
			'∑': '&sum;',
			'∗': '&lowast;',
			'∝': '&prop;',
			'∠': '&ang;',
			'∨': '&or;',
			'∪': '&cup;',
			'∴': '&there4;',
			'≅': '&cong;',
			'≠': '&ne;',
			'≤': '&le;',
			'⊂': '&sub;',
			'⊄': '&nsub;',
			'⊇': '&supe;',
			'⊗': '&otimes;',
			'⋅': '&sdot;',
			'⌉': '&rceil;',
			'⌋': '&rfloor;',
			'⟩': '&rang;',
			'♠': '&spades;',
			'♥': '&hearts;'
		};
	},
	contentEscapeBackToSymbol: function(text) {
		if (!text || text === "") {
			return "";
		}
		var newText = text+"";
		for (const [key, value] of Object.entries(this.htmlSymbols())) {
			if (newText.includes(value)) {
				newText = newText.replaceAll(value, key);
			}
		}
		return newText;
	},
	// 创建聊天窗口
	load: function() {
			var url = this.path + this.options.style + "/chat.html";
			this.conversationId = this.data.id;
			this.container.empty();
			if (this.emojiBoxNode) {
				this.emojiBoxNode.destroy();
				this.emojiBoxNode = null;
			}
			this.container.loadHtml(url, { "bind": { "convName": this.data.title, "lp": this.lp }, "module": this }, function () {
				var me = layout.session.user.distinguishedName;
				if (this.data.type === "group") {
					this.chatTitleMoreBtnNode.setStyle("display", "block");
					this.chatTitleMoreBtnNode.addEvents({
						"click": function (e) {
							var display = this.chatTitleMoreMenuNode.getStyle("display");
							if (display === "none") {
								this.chatTitleMoreMenuNode.setStyle("display", "block");
								this.chatTitleMoreMenuItem4Node.setStyle("display", "block");
								if (me === this.data.adminPerson) { // 群主有操作权限
									this.chatTitleMoreMenuItem1Node.setStyle("display", "block");
									this.chatTitleMoreMenuItem2Node.setStyle("display", "block");
									if (this.main.imConfig.enableClearMsg) {
										this.chatTitleMoreMenuItem3Node.setStyle("display", "block");
									} else {
										this.chatTitleMoreMenuItem3Node.setStyle("display", "none");
									}
								} else {
									this.chatTitleMoreMenuItem1Node.setStyle("display", "none");
									this.chatTitleMoreMenuItem2Node.setStyle("display", "none");
									this.chatTitleMoreMenuItem3Node.setStyle("display", "none");
								}
							} else {
								this.chatTitleMoreMenuNode.setStyle("display", "none");
							}
						}.bind(this)
					});
				} else if (this.data.type !== "group") {
					if (this.main.imConfig.enableClearMsg) {
						this.chatTitleMoreBtnNode.setStyle("display", "block");
						this.chatTitleMoreBtnNode.addEvents({
							"click": function (e) {
								var display = this.chatTitleMoreMenuNode.getStyle("display");
								if (display === "none") {
									this.chatTitleMoreMenuNode.setStyle("display", "block");
									this.chatTitleMoreMenuItem4Node.setStyle("display", "none");
									this.chatTitleMoreMenuItem1Node.setStyle("display", "none");
									this.chatTitleMoreMenuItem2Node.setStyle("display", "none");
									this.chatTitleMoreMenuItem3Node.setStyle("display", "block");
								} else {
									this.chatTitleMoreMenuNode.setStyle("display", "none");
								}
							}.bind(this)
						});
					} else {
						this.chatTitleMoreBtnNode.setStyle("display", "none");
					}
				}
				//获取聊天信息
				this.page = 1;
				this.loadMsgListByPage();
				var scrollFx = new Fx.Scroll(this.chatContentNode);
				scrollFx.toBottom();
				this.addChatEventListener()
				// 显示业务图标
				this.loadBusinessIcon();
			}.bind(this));

	},
	// 内部一些节点添加事件
	addChatEventListener: function () {
		// 消息输入框绑定回车事件
		this.chatBottomAreaTextareaNode.addEvents({
			"keyup": function (e) {
				// debugger;
				if (e.code === 13) {
					if (e.control === true) {
						var text = this.chatBottomAreaTextareaNode.value;
						this.chatBottomAreaTextareaNode.value = text + "\n";
					} else {
						this.sendMsg();
					}
					e.stopPropagation();
				}
			}.bind(this)
		});
		// 消息列表上绑定滚动事件
		this.chatContentNode.addEvents({
			"scroll": function(e) {
				//滑到顶部时触发下次数据加载
				if (this.chatContentNode.scrollTop == 0) {
					if (this.hasMoreMsgData) { // 有更多数据
						// 间隔1秒 防止频繁
						setTimeout(() => {
							//将scrollTop置为10以便下次滑到顶部
							this.chatContentNode.scrollTop = 10;
							//加载数据
							this.loadMoreMsgList();
						}, 1000);
					}
				}
			}.bind(this)
		});
		// 绑定拖拽事件，拖拽上传文件 发送文件消息
		// 阻止默认行为（防止文件打开）
		["dragenter", "dragover", "dragleave", "drop"].forEach(eventName => {
			this.chatNode.addEventListener(eventName, (e)=> {
				e.preventDefault();
				e.stopPropagation();
			});
		});
		// 添加拖入/离开时的样式变化
		["dragenter", "dragover"].forEach(eventName => {
			this.chatNode.addEventListener(eventName, (e) => this.dragEnterOverEvent(e));
		});
		["dragleave", "drop"].forEach(eventName => {
			this.chatNode.addEventListener(eventName, (e) => this.dragLeaveEvent(e));
		});
		// 拖入文件发送消息
		this.chatNode.addEventListener("drop", (e) => this.dragDropFileSendMsg(e))
		// 从剪贴板 复制文件 发送消息
		this.chatNode.addEventListener("paste", (e) => this.pasteFileSendMsg(e))
	},
	// 如果有业务数据 头部展现应用图标 可以点击打开
	loadBusinessIcon: function() {
		if (this.data.businessId && this.data.businessBody) {
			if (this.data.businessType && this.data.businessType === "process") {
				var work = JSON.parse(this.data.businessBody);
				var applicationId = work.application;
				this.chatTitleBusinessBtnNode.setStyles({"background-image": "url(../x_component_process_ApplicationExplorer/$Main/default/icon/application.png)", "display":"block"});
				this.chatTitleBusinessBtnNode.store("work", work);
				this.chatTitleBusinessBtnNode.addEvents({
					"click": function(e) {
						this.loadProcessWork(e.target.retrieve("work"));
						e.preventDefault();
					}.bind(this),
					"mouseover": function() {
						if (this.businessTipsNode) {
							this.businessTipsNode.setStyle("display", "block");
						} else {
							this.businessTipsNode = new Element("div", {
								"style": "position: absolute;right: 0;top: 30px;width: 200px;border: 1px solid #dedede;border-radius: 5px;padding: 8px;background: #ffffff;color: #666;text-align: left;overflow:hidden;white-space: nowrap;text-overflow: ellipsis;"
							}).inject(this.chatTitleBusinessBtnNode);
							var work = this.chatTitleBusinessBtnNode.retrieve("work")
							this.businessTipsNode.set("text", this.lp.chooseBusinessWorkTitle + "【"+work.processName+"】"+work.title);
							this.businessTipsNode.setStyle("display", "block");
						}
					}.bind(this),
					"mouseout": function() {
						if (this.businessTipsNode) {this.businessTipsNode.setStyle("display", "none");}
					}.bind(this)
			});
				o2.Actions.load("x_processplatform_assemble_surface").ApplicationAction.getIcon(applicationId, function(json) {
					if (json.data && json.data.icon) {
						this.chatTitleBusinessBtnNode.setStyles({"background-image": "url(data:image/png;base64," + json.data.icon + ")", "display":"block"});
					}
				}.bind(this));
			}
		}
	},
	// 获取工作对象
	loadProcessWork(work) {
		if (work && work.job) {
			o2.Actions.load("x_processplatform_assemble_surface").JobAction.findWorkWorkCompleted(work.job, function(json){
				if (json.data ) {
					var workList = [];
					if (json.data.workList && json.data.workList.length > 0) {
						workList = json.data.workList
					}
					var workCompletedList = [];
					if (json.data.workCompletedList && json.data.workCompletedList.length > 0) {
						workCompletedList = json.data.workCompletedList
					}
					this.showProcessWorkDialog(workList, workCompletedList);
				}
			}.bind(this), function(error){
				console.error(error);
			}.bind(this));
		}
	},
	// 打开关联工作
	showProcessWorkDialog: function(workList, workCompletedList) {
		if (workList.length > 0 || workCompletedList.length > 0) {
			var url = this.path + this.options.style + "/chooseBusinessWork.html";
			this.container.loadHtml(url, { "bind": { "lp": this.lp }, "module": this }, function(){
				// 工作展现
				if (workList.length > 0) {
					for (let index = 0; index < workList.length; index++) {
						const work = workList[index];
						var workItemNode = new Element("div", {"class":"business-work-item"}).inject(this.businessWorkListNode);
						var workProcessNameNode = new Element("div", {"style":"flex: 1;"}).inject(workItemNode);
						var title = work.title
						if (title === "") {
							title = this.lp.noTitle
						}
						workProcessNameNode.set("text", "【"+work.processName+"】" + title);
						var openBtnNode = new Element("div", {"class":"business-work-item-btn"}).inject(workItemNode);
						openBtnNode.store("work", work);
						openBtnNode.set("text", this.lp.open);
						openBtnNode.addEvents({
							"click": function(e) {
								var thisWork = e.target.retrieve("work");
								if (thisWork) {
									// var opotions = {
									// 	"workId": thisWork.id,
									// }
									// layout.openApplication(null, "process.Work", opotions);
									o2.api.form.openWork(thisWork.id, "", thisWork.title || "" );
								}
								this.closeProcessWorkDialog();
								e.preventDefault();
							}.bind(this)
						})
					}
				}
				if (workCompletedList.length > 0) {
					for (let index = 0; index < workCompletedList.length; index++) {
						const workCompleted = workCompletedList[index];
						var workItemNode = new Element("div", {"class":"business-work-item"}).inject(this.businessWorkListNode);
						var workProcessNameNode = new Element("div", {"style":"flex: 1;"}).inject(workItemNode);
						var title = workCompleted.title
						if (title === "") {
							title = this.lp.noTitle
						}
						workProcessNameNode.set("text", "【"+workCompleted.processName+"】" + title);
						var openBtnNode = new Element("div", {"class":"business-work-item-btn"}).inject(workItemNode);
						openBtnNode.store("work", workCompleted);
						openBtnNode.set("text", this.lp.open);
						openBtnNode.addEvents({
							"click": function(e) {
								var thisWork = e.target.retrieve("work");
								if (thisWork) {
									// var opotions = {
									// 	"workCompletedId": thisWork.id,
									// }
									// layout.openApplication(null, "process.Work", opotions);
									o2.api.form.openWork(thisWork.id, "", thisWork.title || "" );
								}
								this.closeProcessWorkDialog();
								e.preventDefault();
							}.bind(this)
						})
					}
				}
			

				// 关闭
				this.businessWorkChooseCloseBtnNode.addEvents({
					"click": function(e) {
						this.closeProcessWorkDialog();
						e.preventDefault();
					}.bind(this)
				})
			}.bind(this));
		}
	},
	// 
	closeProcessWorkDialog: function() {
		if (this.businessWorkChooseDialogNode) {
			this.businessWorkChooseDialogNode.destroy();
			this.businessWorkChooseDialogNode = null;
		}
	},

	//检查是否有新消息
	_checkNewMessage: function () {
		if (this.conversationId && this.conversationId !== "") {//是否有会话窗口
			var data = { "conversationId": this.conversationId };
			o2.Actions.load("x_message_assemble_communicate").ImAction.msgListByPaging(1, 10, data, function (json) {
				var list = json.data;
				if (list && list.length > 0) {
					for (var i = 0; i < list.length; i++) {
						var isnew = true;
						var m = list[i];
						for (var j = 0; j < this.messageList.length; j++) {
							if (this.messageList[j].id === m.id) {
								isnew = false;
							}
						}
						if (isnew) {
							this.messageList.push(m);
							this._buildMsgNode(m, false);
							// this._refreshConvMessage(m);
						}
					}
				}
			}.bind(this), function (error) {
				console.error(error);
			}.bind(this), false);
		}
	},
	// 撤回消息
	_checkRevokeMsg: function(msg) {
		if (this.conversationId && this.conversationId !== "") {//是否有会话窗口
			if (msg.conversationId && msg.conversationId === this.conversationId) {
				// 删除数据
				this.messageList.splice(this.messageList.findIndex(e => e.id === msg.id), 1);
				this._removeMsgNode(msg);
			}
		}
	},
	// 加载更多
	loadMoreMsgList: function() {
		this.page += 1;
		this.loadMsgListByPage();
	},

	//分页获取会话的消息列表数据
	loadMsgListByPage: function () {
		if (this.isLoading) {
			console.log("正在加载中。。。。。。");
			return ;
		}
		var data = { "conversationId": this.conversationId };
		this.isLoading = true;
		if (this.page === 1) {
			this.messageList = [];
		}
		o2.Actions.load("x_message_assemble_communicate").ImAction.msgListByPaging(this.page, this.pageSize, data, function (json) {
			var list = json.data;
			var size = 0;
			if (list && list.length > 0) {
				size = list.length;
					for (var i = 0; i < list.length; i++) {
						if (this.page == 1) {
							this.messageList.push(list[i]);
						} else {
							this.messageList.unshift(list[i]);
						}
						this._buildMsgNode(list[i], true);
					} 
			}
			this.isLoading = false;
			if (size < this.pageSize) { // 没有更多数据了
				this.noMoreDataNode = new Element("div", {"class": "chat-no-more-data"}).inject(this.chatContentNode, "top");
				this.noMoreDataNode.set("text", this.lp.msgLoadNoMoreData);
				this.hasMoreMsgData = false;
			} else {
				if (this.noMoreDataNode) {
					this.noMoreDataNode.destroy();
					this.noMoreDataNode = null;
				}
				this.hasMoreMsgData = true;
			}
		}.bind(this), function (error) {
			console.error(error);
			this.isLoading = false;
		}.bind(this), false);
	},

	// 群信息
	tapConvInfo: function() {
		this.chatTitleMoreMenuNode.setStyle("display", "none");
		var convObj = null;
		for (var i = 0; i < this.main.conversationNodeItemList.length; i++) {
			var c = this.main.conversationNodeItemList[i];
			if (this.conversationId == c.data.id) {
				convObj = c.data;
			}
		}
		if (convObj) {
			var infoContainerNode = new Element("div", {"style":"padding:10px;background-color:#fff;overflow:auto;"});
			new Element("div", {"style":"font-size: 16px;line-height: 32px;margin: 10px 0 0;", "text": this.lp.groupName}).inject(infoContainerNode);
			new Element("div", {"style":"font-size: 14px;line-height: 26px;margin: 10px 20px 0;", "text": convObj.title}).inject(infoContainerNode);
			new Element("div", {"style":"font-size: 16px;line-height: 32px;margin: 10px 0 0;", "text": this.lp.groupMemberAdmin}).inject(infoContainerNode);
			var adminPerson = convObj.adminPerson || "";
			var adminName = adminPerson;
			if (adminPerson.indexOf("@") != -1) {
				adminName = adminPerson.substring(0, adminPerson.indexOf("@"));
			}
			new Element("div", {"style":"font-size: 14px;line-height: 26px;margin: 10px 20px 0;", "text": adminName}).inject(infoContainerNode);
			new Element("div", {"style":"font-size: 16px;line-height: 32px;margin: 10px 0 0;", "text": this.lp.groupMember}).inject(infoContainerNode);
			var memberListContainer = new Element("div", {"style":"margin: 10px 20px;display:flex; flex-wrap:wrap;"}).inject(infoContainerNode);
			var personList = convObj.personList || [];
			for (let index = 0; index < personList.length; index++) {
				const person = personList[index];
				var memberDiv = new Element("div", {"style":"display:flex; flex-direction: column;padding: 10px;align-items: center;"}).inject(memberListContainer);
				var avatarUrl = this.main._getIcon(person);
				new Element("img", { "src": avatarUrl, "style": "width:40px;height:40px;" }).inject(memberDiv);
				var name = person;
				if (person.indexOf("@") != -1) {
					name = name.substring(0, person.indexOf("@"));
				}
				new Element("div", { "text": name, "style": "margin-top:10px;" }).inject(memberDiv);
			}
			var dlg = o2.DL.open({
				"title": this.lp.openGroupInfo,
				"mask": true,
				"width": "500",
				"height": "430",
				"content": infoContainerNode,
				"onQueryClose": function () {
					infoContainerNode.destroy();
				}.bind(this),
				"buttonList": [
					{
						"type": "ok",
						"text": this.lp.ok,
						"action": function () { 
							dlg.close(); 
						}.bind(this)
					}
				],
				"onPostShow": function () {
						dlg.reCenter();
				}.bind(this),
				"onPostClose": function(){
					dlg = null;
				}.bind(this)
			});
		}
	},
	//修改群名
	tapUpdateConvTitle: function () {
		this.chatTitleMoreMenuNode.setStyle("display", "none");
		var title = "";
		for (var i = 0; i < this.main.conversationNodeItemList.length; i++) {
			var c = this.main.conversationNodeItemList[i];
			if (this.conversationId == c.data.id) {
				title = c.data.title;
			}
		}
		var form = new MWF.xApplication.IMV2.UpdateConvTitleForm(this.main, {}, {"defaultValue": title}, { app: this.main.app });
		form.create();
	},
	//修改群成员
	tapUpdateConvMembers: function () {
		this.chatTitleMoreMenuNode.setStyle("display", "none");
		var members = [];
		for (var i = 0; i < this.main.conversationNodeItemList.length; i++) {
			var c = this.main.conversationNodeItemList[i];
			if (this.conversationId == c.data.id) {
				members = c.data.personList;
			}
		}
		var form = new MWF.xApplication.IMV2.CreateConversationForm(this.main, {}, { "title": this.lp.modifyMember, "personCount": 0, "personSelected": members, "isUpdateMember": true }, { app: this.main.app });
		form.create()
	},
	// 点击菜单 删除会话
	tapDeleteConversation: function(e) {
		var _self = this;
		var con = null;
		for (var i = 0; i < this.main.conversationNodeItemList.length; i++) {
			var c = this.main.conversationNodeItemList[i];
			if (this.conversationId == c.data.id) {
				con = c.data;
				break;
			}
		}
		if (con) {
			var msg = this.lp.messageDeleteSingleConversationAlert;
			if (con.type === "single") {
				msg = this.lp.messageDeleteSingleConversationAlert;
			} else {
				msg = this.lp.messageDeleteGroupConversationAlert;
			}
			MWF.xDesktop.confirm("info", this.chatTitleNode, this.lp.alert, msg, 400, 150, function() {
				if (con.type === "single") {
					_self.deleteSingleConversation();
				} else {
					_self.deleteGroupConversation();
				}
				this.close();
			}, function(){
				this.close();
			}, null, null, "o2");
		} else {
			console.error('没有找到会话对象。。。。。');
		}
	},
	// 删除群聊
	deleteGroupConversation: function() {
		o2.Actions.load("x_message_assemble_communicate").ImAction.deleteGroupConversation(this.conversationId, function (json) {
			this.main.refresh();
		}.bind(this), function (error) {
			console.error(error);
			this.app.notice(error, "error", this.app.content);
		}.bind(this));
		
	},
	deleteSingleConversation: function() {
		o2.Actions.load("x_message_assemble_communicate").ImAction.deleteSingleConversation(this.conversationId, function (json) {
			this.main.refresh();
		}.bind(this), function (error) {
			console.error(error);
			this.app.notice(error, "error", this.app.content);
		}.bind(this));
	},
	_reclickConv: function() {
		for (var i = 0; i < this.main.conversationNodeItemList.length; i++) {
			var c = this.main.conversationNodeItemList[i];
			if (this.conversationId == c.data.id) {
				this.main.tapConv(c.data);
			}
		}
	},
	//创建图片或文件消息
	_newImageOrFileMsgAndSend: function (type, fileId, fileName, fileExt) {
		var distinguishedName = layout.session.user.distinguishedName;
		var time = this._currentTime();
		var body = {
			"body": type === "image" ? this.lp.msgTypeImage : this.lp.file,
			"type": type,
			"fileId": fileId,
			"fileExtension": fileExt,
			"fileName": fileName
		};
		var bodyJson = JSON.stringify(body);
		var uuid = new MWF.widget.UUID().createTrueUUID();
		var message = {
			"id": uuid,
			"conversationId": this.conversationId,
			"body": bodyJson,
			"createPerson": distinguishedName,
			"createTime": time,
			"sendStatus": 1
		};
		o2.Actions.load("x_message_assemble_communicate").ImAction.msgCreate(message,
			function (json) {
				console.log(this.lp.sendSuccess);
			}.bind(this),
			function (error) {
				console.error(error);
			}.bind(this));
		this.messageList.push(message);
		this._buildReceiver(body, distinguishedName, false, message);
		this.main._refreshConvMessage(message);
	},
	//创建文本消息 并发送
	_newAndSendTextMsg: function (text, type) {
		var distinguishedName = layout.session.user.distinguishedName;
		var time = this._currentTime();
		var body = { "body": text, "type": type };
		var bodyJson = JSON.stringify(body);
		var uuid = new MWF.widget.UUID().createTrueUUID();
		var textMessage = {
			"id": uuid,
			"conversationId": this.conversationId,
			"body": bodyJson,
			"createPerson": distinguishedName,
			"createTime": time,
			"sendStatus": 1
		};
		if (this.quoteMessage && this.quoteMessage.id) {
			textMessage.quoteMessageId = this.quoteMessage.id;
			textMessage.quoteMessage = this.quoteMessage;
		}
		o2.Actions.load("x_message_assemble_communicate").ImAction.msgCreate(textMessage,
			function (json) {
				console.log(this.lp.sendSuccess);
			}.bind(this),
			function (error) {
				console.error(error);
			}.bind(this));
		this.messageList.push(textMessage);
		this._buildReceiver(body, distinguishedName, false, textMessage);
		this.main._refreshConvMessage(textMessage);
		this.deleteQuoteMessage();
	},
	// 创建引用消息的 Node 节点
	_newQuoteMessageElement: function (msg, parentNode) {
		var name = msg.createPerson;
		if (msg.createPerson.indexOf("@") !== -1) {
			name = name.substring(0, msg.createPerson.indexOf("@"));
		}
		name += ": ";
		var msgBody = JSON.parse(msg.body);
		if (msgBody.type !== "emoji" && msgBody.type !== "image") {
			name += this.contentEscapeBackToSymbol(msgBody.body)
			if (msgBody.type === "file") {
				name += " " + msgBody.fileName;
			}
		}
		let quoteMessageNode = new Element("div", {"class": "quote-message-box"}).inject(parentNode);
		new Element("div", {"text": name , "class": "quote-message-desc" }).inject(quoteMessageNode)
		if (msgBody.type === "emoji") {
			var img = "";
			for (var i = 0; i < this.main.emojiList.length; i++) {
				if (msgBody.body === this.main.emojiList[i].key) {
					img = this.main.emojiList[i].path;
				}
			}
			new Element("img", { "src": img, "class": "quote-message-emoji" }).inject(quoteMessageNode);
		} else if (msgBody.type === "image") {
			var url = this._getFileUrlWithWH(msgBody.fileId, 48, 48);
			if (msgBody.fileExtension && msgBody.fileExtension.toLowerCase() === "webp") {
				url = this._getFileDownloadUrl(msgBody.fileId);
			}
			new Element("img", { "src": url, "class": "quote-message-image" }).inject(quoteMessageNode);
		}
		return quoteMessageNode;
	},
	// 添加引用消息
	addQuoteMessage: function (msg) {
		if (this.quoteMessage) {
			this.deleteQuoteMessage();
		}
		this.quoteMessage = msg;
		if (!this.quoteMessage) {
			console.error('引用消息为空！！！！');
			return;
		}

		let node = this._newQuoteMessageElement(msg, this.chatBottomAreaQuoteMessageNode);

		// var name = msg.createPerson;
		// if (msg.createPerson.indexOf("@") !== -1) {
		// 	name = name.substring(0, msg.createPerson.indexOf("@"));
		// }
		// name += ": ";
		// var msgBody = JSON.parse(msg.body);
		// if (msgBody.type !== "emoji") {
		// 	name += this.contentEscapeBackToSymbol(msgBody.body)
		// }
		// let quoteMessageNode = new Element("div", {"class": "quote-message-box"}).inject(this.chatBottomAreaQuoteMessageNode);
		// new Element("div", {"text": name , "class": "quote-message-desc" }).inject(quoteMessageNode)
		// if (msgBody.type === "emoji") {
		// 	var img = "";
		// 	for (var i = 0; i < this.main.emojiList.length; i++) {
		// 		if (msgBody.body === this.main.emojiList[i].key) {
		// 			img = this.main.emojiList[i].path;
		// 		}
		// 	}
		// 	new Element("img", { "src": img, "class": "quote-message-emoji" }).inject(quoteMessageNode);
		// }
		let closeNode = new Element("div", {"class": "quote-message-close", "title": "关闭"}).inject(node)
		closeNode.addEvent("click", function (){
			this.deleteQuoteMessage()
		}.bind(this))
	},
	deleteQuoteMessage: function () {
		this.quoteMessage = null;
		for (const child of this.chatBottomAreaQuoteMessageNode.children) {
			 child.remove()
		}
	},
	//点击发送消息
	sendMsg: function () {
		var text = this.chatBottomAreaTextareaNode.value;
		if (text) {
			this.chatBottomAreaTextareaNode.value = "";
			this._newAndSendTextMsg(text, "text");
		} else {
			console.log(this.lp.noMessage);
			this.app.notice(this.lp.noMessage, "error", this.app.content);
		}
	},
	forwardMsgList: function (msgList) {
		// 先选择会话
		MWF.xDesktop.requireApp("IMV2", "Starter", function () {
			var share = new MWF.xApplication.IMV2.ShareToConversation({
				msgBody: {},
				callback: function (conversation) {
					console.debug("选择了会话 " + conversation.title)
					this._forwardMsgList(conversation, msgList)
				}.bind(this)
			}, this.app);
			share.load();
		}.bind(this));
	},
	_forwardMsgList: function (conversation, msgList) {
		var time = this._currentTime();
		for (let i = 0; i < msgList.length; i++) {
			let msg = msgList[i];
			msg.id = new MWF.widget.UUID().createTrueUUID();
			msg.conversationId = conversation.id;
			msg.createTime = time;
			o2.Actions.load("x_message_assemble_communicate").ImAction.msgCreate(msg,
				function (json) {
					console.log(this.lp.sendSuccess);
				}.bind(this),
				function (error) {
					console.error(error);
				}.bind(this));
			if (conversation.id === this.conversationId) {
				this.messageList.push(msg);
				this._buildReceiver(JSON.parse(msg.body), msg.createPerson, false, msg);
			}
			this.main._refreshConvMessage(msg);
		}
	},
	// 逐条转发
	forwardOneByOne: function () {
		if (this.selectMsgList.length < 1) {
			this.app.notice(this.lp.msgNeedSelectMessage, "error", this.app.content);
			return;
		}
		this.forwardMsgList(this.selectMsgList)
		this.cancelSelectMode()
	},
	// 合并转发
	forwardMerge: function () {
		if (this.selectMsgList.length < 1) {
			this.app.notice(this.lp.msgNeedSelectMessage, "error", this.app.content);
			return;
		}
		let list = this.selectMsgList.slice();
		// 倒序
		list.sort(function (a, b) {
			return new Date(b.createTime) - new Date(a.createTime);
		})
		var descList = [];
		if (list.length > 4) {
			descList = list.slice(0, 4);
		} else {
			descList = list.slice();
		}
		var desc = '';
		for (var i = 0; i < descList.length; i++) {
			var msg = descList[i];
			var name = msg.createPerson;
			if (msg.createPerson.indexOf("@") != -1) {
				name = name.substring(0, msg.createPerson.indexOf("@"));
			}
			var body = JSON.parse(msg.body)
			var content = body.body;
			if (body.type === "text") {
				content = this.contentEscapeBackToSymbol(body.body)
			} else if (body.type === "emoji") {
				content = this.lp.msgTypeEmoji;
			}
			desc += name + ": " + content + "\n"
		}
		var title = "群聊的聊天记录"
		if (this.data.type === "single") {
			title = this.data.personList.map((p) => {
				var name = p;
				if (p.indexOf("@") != -1) {
					name = p.substring(0, p.indexOf("@"));
				}
				return name;
			}).join(",") + "的聊天记录"
		}

		var distinguishedName = layout.session.user.distinguishedName;

		var body = {
			"body": this.lp.msgTypeHistory,
			"type": "messageHistory",
			"messageHistoryTitle":title,
			"messageHistoryDesc":desc,
			"messageHistoryIds": list.map((e)=> e.id)
		};
		var bodyJson = JSON.stringify(body);
		var message = {
			"id": "",
			"conversationId": this.conversationId,
			"body": bodyJson,
			"createPerson": distinguishedName,
			"createTime": "",
			"sendStatus": 1
		};
 		this.forwardMsgList([message])
		this.cancelSelectMode()
	},
	// 收藏 选中的消息
	collectionMsgs: function () {
		if (this.selectMsgList.length < 1) {
			this.app.notice(this.lp.msgNeedSelectMessage, "error", this.app.content);
			return;
		}
		let list = this.selectMsgList.slice();
		// 顺序
		list.sort(function (a, b) {
			return new Date(a.createTime) - new Date(b.createTime);
		})
		this.collectionMsgList(list);
		this.cancelSelectMode()
	},
	collectionMsgList: function (msgList) {
		var body = {
			msgIdList: msgList.map((e) => e.id)
		}
		o2.Actions.load("x_message_assemble_communicate").ImAction.msgCollectionSave(body,
			function (json) {
				this.app.notice(this.lp.msgCollectionSuccess, "success", this.app.content);
			}.bind(this),
			function (error) {
				console.error(error);
			}.bind(this));
	},
	// 选择模式
	openSelectMode: function(msg) {
		this.selectMode = true;
		this.selectMsgList = [];
		const list = this.chatContentNode.querySelectorAll(".chat-msg-checkbox")
		list.forEach(item => {
			item.classList.remove("none")
			item.classList.add("block")
		})
		this.chatBottomAreaNode.classList.remove("block")
		this.chatBottomAreaNode.classList.add("none")
		this.chatBottomSelectModeAreaNode.classList.remove("none")
		this.chatBottomSelectModeAreaNode.classList.add("block")
		this._selectOrUnSelectMsg(msg)
	},
	// 取消选择模式
	cancelSelectMode: function () {
		this.selectMode = false;
		this.selectMsgList = [];
		const list = this.chatContentNode.querySelectorAll(".chat-msg-checkbox")
		debugger
		list.forEach(item => {
			item.classList.remove("block")
			item.classList.add("none")
		})
		this.chatBottomSelectModeAreaNode.classList.remove("block")
		this.chatBottomSelectModeAreaNode.classList.add("none")
		this.chatBottomAreaNode.classList.remove("none")
		this.chatBottomAreaNode.classList.add("block")
		this._selectOrUnSelectMsg()
	},
	dragEnterOverEvent: function (e) {
		this.chatNode.classList.add("drag-area");
	},
	dragLeaveEvent: function (e) {
		this.chatNode.classList.remove("drag-area");
	},
	// 拖拽发送文件消息
	dragDropFileSendMsg: function (e) {
		console.log('拖拽了文件', e)
		if (e && e.dataTransfer && e.dataTransfer.files) {
			const files = e.dataTransfer.files
			console.log('拖拽了文件', files);
			[...files].forEach((file)=> {
				if (file.type && file.type !== '') {
					this.sendFileMsg(file)
				}
			});
		}
	},
	// 从剪贴板 复制 文件上传并发送消息
	pasteFileSendMsg: function (e) {
		// 获取粘贴的内容
		const items = e.clipboardData.items;
		// 遍历剪贴板中的所有项目
		for (let i = 0; i < items.length; i++) {
			const item = items[i];
			// 判断是否为文件类型
			if (item.kind === 'file') {
				const file = item.getAsFile();
				if (file) {
					console.log('粘贴的文件:', file);
					this.sendFileMsg(file)
				}
			} else if (item.type.indexOf('image') > -1) {
				// 处理图片类型，可以通过 getAsFile 获取 Blob 对象
				const file = item.getAsFile();
				if (file) {
					console.log('粘贴的图片:', file);
					this.sendFileMsg(file)
				}
			}
		}
	},
	// 点击发送文件消息
	showChooseFile: function () {
		if (!this.uploadFileAreaNode) {
			this.createUploadFileNode();
		}
		this.fileUploadNode.click();
	},
	// 检测浏览器是否支持WebP
	_canUseWebP: function() {
		var elem = document.createElement('canvas');
		if (elem.getContext && elem.getContext('2d')) {
			return elem.toDataURL('image/webp').indexOf('data:image/webp') === 0;
		}
		return false;
	},

	//创建文件选择框
	createUploadFileNode: function () {
		this.uploadFileAreaNode = new Element("div");
		var html = "<input name=\"file\" type=\"file\" multiple/>";
		this.uploadFileAreaNode.set("html", html);
		this.fileUploadNode = this.uploadFileAreaNode.getFirst();
		this.fileUploadNode.addEvent("change", function () {
			var files = this.fileUploadNode.files;
			if (files.length) {
				var file = files.item(0);
				this.sendFileMsg(file)
			}
		}.bind(this));
	},
	sendFileMsg: function (file) {
		var formData = new FormData();
		formData.append('file', file);
		formData.append('fileName', file.name);
		var fileExt = file.name.substring(file.name.lastIndexOf("."));
		// 图片消息
		var type = "file"
		if (fileExt.toLowerCase() === ".webp" && this._canUseWebP()) {
			type = "image"
		} else if (fileExt.toLowerCase() === ".bmp" || fileExt.toLowerCase() === ".jpeg"
			|| fileExt.toLowerCase() === ".png" || fileExt.toLowerCase() === ".jpg") {
			type = "image"
		} else { // 文件消息
			type = "file"
		}
		//上传文件
		o2.Actions.load("x_message_assemble_communicate").ImAction.uploadFile(this.conversationId, type, formData, "{}", function (json) {
			if (json.data) {
				var fileId = json.data.id
				var fileExtension = json.data.fileExtension
				var fileName = json.data.fileName
				this._newImageOrFileMsgAndSend(type, fileId, fileName, fileExtension)
			}
		}.bind(this), function (error) {
			console.error(error);
		}.bind(this))
	},
	//点击表情按钮
	showEmojiBox: function (){
		if (!this.emojiBoxNode) {
			this.emojiBoxNode = new Element("div", { "class": "chat-emoji-box" }).inject(this.container);
			var _self = this;
			for (var i = 0; i < this.main.emojiList.length; i++) {
				var emoji = this.main.emojiList[i];
				var emojiNode = new Element("img", { "src": emoji.path, "class": "chat-emoji-img" }).inject(this.emojiBoxNode);
				emojiNode.addEvents({
					"mousedown": function (ev) {
						_self.sendEmojiMsg(this.emoji);
						_self.hideEmojiBox();
					}.bind({ emoji: emoji })
				});
			}
		}
		this.emojiBoxNode.setStyle("display", "block");
		this.hideFun = this.hideEmojiBox.bind(this);
		document.body.addEvent("mousedown", this.hideFun);
	},
	hideEmojiBox: function () {
		//关闭emojiBoxNode
		this.emojiBoxNode.setStyle("display", "none");
		document.body.removeEvent("mousedown", this.hideFun);
	},
	showEmojiV2: function () {
		if (!this.isLoadEmojiV2) {
			for (let i = 0; i < this.main.emojiV2TypeList.length; i++) {
				let type = this.main.emojiV2TypeList[i];
				let typeNode = new Element("div", {"class": "im-chat-emoji-item"}).inject(this.emojiTypeListContainerNode);
				let className = "btn";
				if (i === 0) {
					className += " active"
				}
				let btnNode = new Element("div", {"class": className}).inject(typeNode);
				new Element("img", {"src": "../x_component_IMV2/$Main/default/icons/emoji_type_" + type + ".png"}).inject(btnNode);
				typeNode.store("type", type);
				typeNode.addEvent("click", function (e){
					this._clickEmojiV2TypeBtn(e)
				}.bind(this))
			}
			this._renderEmojiV2List(this.main.emojiV2Object[this.main.emojiV2TypeList[0]]);
			this.isLoadEmojiV2 = true;
		}
		this.emojiMaskNode.classList.remove('none');
	},
	_renderEmojiV2List: function (list) {
		this.emojiListContainerNode.empty()
		this.currentEmojiV2List = list;
		for (let i = 0; i < this.currentEmojiV2List.length; i++) {
			let emoji = this.currentEmojiV2List[i];
			let emojiNode = new Element("div", {"class": "im-chat-emoji-item"}).inject(this.emojiListContainerNode);
			emojiNode.set("text", emoji)
			emojiNode.store("emoji", emoji)
			emojiNode.addEvent("click", function (e){
				this._clickEmojiV2Item(e)
			}.bind(this))
		}
	},
	// 点击表情类型
	_clickEmojiV2TypeBtn: function (e) {
		let target =  e.event.currentTarget;
		let type = target.retrieve("type");
		console.debug('点击了 表情类型 ' + type);
		this._renderEmojiV2List(this.main.emojiV2Object[type]);
		let list = this.emojiTypeListContainerNode.children;
		for (let i = 0; i < list.length; i++) {
			let child = list[i];
			child.firstChild.classList.remove('active');
		}
		target.firstChild.classList.add('active')
	},
	// 点击表情
	_clickEmojiV2Item: function (e) {
		let emoji = e.target.retrieve("emoji");
		let text = this.chatBottomAreaTextareaNode.value;
		this.chatBottomAreaTextareaNode.value = text + emoji;
		// this.closeEmojiMaskV2()
	},
	closeEmojiMaskV2: function () {
		this.emojiMaskNode.classList.add('none')
	},
	clickStopCloseEmojiMaskV2: function (e) {
		e.stopPropagation()
	},
	//发送表情消息
	sendEmojiMsg: function (emoji) {
		this._newAndSendTextMsg(emoji.key, "emoji");
	},
	_selectOrUnSelectMsg: function (msg) {
		if (msg) {
			if (this.selectMsgList.findIndex( m => m.id === msg.id) > -1) {
				this.selectMsgList.splice(this.selectMsgList.findIndex( m => m.id === msg.id), 1);
			} else {
				this.selectMsgList.push(msg);
			}
		}
		var checkList = this.chatContentNode.querySelectorAll(".check-box-select-item")
		checkList.forEach(item => {
			var checkMsg = item.retrieve("msg")
			if (this.selectMsgList.findIndex( m => m.id === checkMsg.id) > -1) {
				item.checked = true
			} else {
				item.checked = false
			}
		})
	},
	// 点击消息 包含 选择 和 打开 消息
	_clickMsgItem: function (e, isQuoteMsg) {
		e.stopPropagation();
		console.debug('点击消息，isQuoteMsg ' + isQuoteMsg);
		var msg = e.event.currentTarget.retrieve("msg");
		if (!msg || !msg.body) {
			console.error('错误的 target！！！')
			return;
		}
		if (this.selectMode) {
			if (isQuoteMsg) {
				return;
			}
			this._selectOrUnSelectMsg(msg)
		} else {
			this.openMsgItem(msg)
		}
	},
	// 打开消息
	openMsgItem: function (msg) {
		var msgBody = JSON.parse(msg.body);
		if (msgBody.type === "image") {
			window.open(this._getFileDownloadUrl(msgBody.fileId));
		} else if (msgBody.type === "process") {
			o2.api.form.openWork(msgBody.work, "", title || "" );
		} else  if (msgBody.type === "messageHistory") {
			console.debug('聊天记录点击')
			this._openMessageHistory(msg)
		} else if (msgBody.type === "file") {
			// 有安装 onlyOffice
			if (layout.serviceAddressList["x_onlyofficefile_assemble_control"]
				&& this.main.imConfig.enableOnlyOfficePreview  && msgBody.fileExtension
				&& (msgBody.fileExtension.toLowerCase() === "docx" || msgBody.fileExtension.toLowerCase() === "doc"
				|| msgBody.fileExtension.toLowerCase() === "xls" || msgBody.fileExtension.toLowerCase() === "xlsx"
				|| msgBody.fileExtension.toLowerCase() === "ppt" || msgBody.fileExtension.toLowerCase() === "pptx"
				|| msgBody.fileExtension.toLowerCase() === "pdf" || msgBody.fileExtension.toLowerCase() === "csv"
				|| msgBody.fileExtension.toLowerCase() === "txt")) {
				var onlyOfficeUrl =  "../o2_lib/onlyoffice/index.html?fileName=" +msgBody.fileName+ "&file=" + this._getFileDownloadUrl(msgBody.fileId);
				window.open(onlyOfficeUrl);
				return;
			} else if (msgBody.fileExtension && (msgBody.fileExtension.toLowerCase() === "mp4" || msgBody.fileExtension.toLowerCase() === "avi" || msgBody.fileExtension.toLowerCase() === "ogg")) {
				console.log('视频文件无需下载！')
				return;
			}
			window.open(this._getFileDownloadUrl(msgBody.fileId));
		} else if (msgBody.type === "location") {
			var url = this._getBaiduMapUrl(msgBody.latitude, msgBody.longitude, msgBody.address, msgBody.addressDetail);
			window.open(url);
		}
	},
	// 打开收藏的消息
	openMyCollection: function () {
		let id = 'myCollectionFlag';// 这个作为一个标识
		let msg = {
			id: id
		}
		if (!this.messageHistoryMap) {
			this.messageHistoryMap = new Map();
		}
		// 遮罩层
		if (!this.messageHistoryNode) {
			this.messageHistoryNode = new Element("div", {"class": "chat-msg-list-container"}).inject(this.chatNode);
		}
		if (this.messageHistoryMap.has(msg.id)) {
			this.closeMessageHistory(msg)
		}
		// collectionMode 标识收藏
		const el = new MWF.xApplication.IMV2.ChatMessageList({title: this.lp.msgCollectionTitle, msg: msg, collectionMode: true}, this);
		this.messageHistoryMap.set(msg.id, el)
	},
	// 打开一个聊天记录
	_openMessageHistory: function (msg) {
		if (!this.messageHistoryMap) {
			this.messageHistoryMap = new Map();
		}
		// 遮罩层
		if (!this.messageHistoryNode) {
			this.messageHistoryNode = new Element("div", {"class": "chat-msg-list-container"}).inject(this.chatNode);
		}
		if (this.messageHistoryMap.has(msg.id)) {
			this.closeMessageHistory(msg)
		}
		const el = new MWF.xApplication.IMV2.ChatMessageList({title: this.lp.msgHistory, msg: msg}, this);
		this.messageHistoryMap.set(msg.id, el)
	},
	// 关闭某一个聊天记录
	closeMessageHistory: function (msg) {
		if (this.messageHistoryMap.has(msg.id)) {
			this.messageHistoryMap.get(msg.id).deleteSelfNode()
			this.messageHistoryMap.delete(msg.id) // 删除
		}
		// 关闭遮罩层
		if (this.messageHistoryMap.size < 1 && this.messageHistoryNode) {
			this.messageHistoryNode.destroy()
			this.messageHistoryNode = null;
		}
	},
	// 撤回、删除 消息
	_removeMsgNode: function(msg) {
		var itemNode = this.chatContentNode.getElement("#"+msg.id);
		if (itemNode) {
			var beforeNode = itemNode.getPrevious();
			itemNode.destroy();
			if (beforeNode) {
				beforeNode.destroy();
			}
		}
	},
	//创建消息html节点
	_buildMsgNode: function (msg, isTop) {
		var createPerson = msg.createPerson;
		var jsonbody = msg.body;
		var body = JSON.parse(jsonbody);
		var distinguishedName = layout.session.user.distinguishedName;
		if (createPerson != distinguishedName) {
			this._buildSender(body, createPerson, isTop, msg);
		} else {
			this._buildReceiver(body, createPerson, isTop, msg);
		}
	},
	/**
	 * 消息接收对象  
	 * 这里的方法名错了两者互换了无需理会
	 * @param  msgBody 消息体
	 * @param createPerson 消息人员
	 * @param isTop 是否放在顶部
	 * @param msg 消息对象
	 */
	 _buildSender: function (msgBody, createPerson, isTop, msg) {
		if (!isTop) { 
			// 添加消息时间
			this._buildMsgTime(isTop, msg);
		}
		var msgItemNode = new Element("div", {"class": "chat-msg"}).inject(this.chatContentNode, isTop ? "top" : "bottom");

		var checkBoxClass = "chat-msg-checkbox none"
		if (this.selectMode) {
			checkBoxClass = "chat-msg-checkbox block"
		}
		var msgItemCheckBoxNode = new Element("div", {"class": checkBoxClass}).inject(msgItemNode);

		var msgItemCheckBoxInputNode =  new Element("input", {"type": "checkbox", "class": "check-box-select-item"}).inject(msgItemCheckBoxNode);
		msgItemCheckBoxInputNode.store("msg", msg)
		msgItemCheckBoxInputNode.addEvents({
			"click": function(e) {
				this._clickMsgItem(e);
			}.bind(this)
		})

		var receiverBodyNode = new Element("div", { "class": "chat-sender", "id": msg.id}).inject(msgItemNode);
		this._addContextMenuEvent(receiverBodyNode, msg);
		var avatarNode = new Element("div", {"class": "chat-sender-avatar"}).inject(receiverBodyNode);
		var avatarUrl = this.main._getIcon(createPerson);
		var name = createPerson;
		if (createPerson.indexOf("@") != -1) {
			name = name.substring(0, createPerson.indexOf("@"));
		}
		var avatarImg = new Element("img", { "src": avatarUrl }).inject(avatarNode);
		var nameNode = new Element("div", { "text": name , "class": "chat-sender-name"}).inject(receiverBodyNode);
		var lastNodeClass = "chat-sender-box"
		if (msgBody.type === "process" || msgBody.type === "cms") {
			lastNodeClass = "chat-sender-card-box"
		}
		var lastNode = new Element("div", {"class": lastNodeClass}).inject(receiverBodyNode);
		lastNode.store("msg", msg);
		lastNode.addEvents({
			"click": function(e) {
				this._clickMsgItem(e);
			}.bind(this)
		})
		var lastFirstNode = new Element("div", { "class": "chat-left_triangle" }).inject(lastNode);
		//text
		if (msgBody.type === "emoji") { // 表情
			var img = "";
			for (var i = 0; i < this.main.emojiList.length; i++) {
				if (msgBody.body === this.main.emojiList[i].key) {
					img = this.main.emojiList[i].path;
				}
			}
			new Element("img", { "src": img, "class": "chat-content-emoji" }).inject(lastNode);
		} else if (msgBody.type === "image") {//image
			var imgBox = new Element("div", { "class": "img-chat" }).inject(lastNode);
			var url = this._getFileUrlWithWH(msgBody.fileId, 144, 192);
			if (msgBody.fileExtension && msgBody.fileExtension.toLowerCase() === "webp") {
				url = this._getFileDownloadUrl(msgBody.fileId);
			}
			new Element("img", { "src": url }).inject(imgBox);
		} else if (msgBody.type === "audio") {
			var url = this._getFileDownloadUrl(msgBody.fileId);
			new Element("audio", { "src": url, "controls": "controls", "preload": "preload" }).inject(lastNode);
		} else if (msgBody.type === "location") {
			var mapBox = new Element("span", {"style": "display: flex;gap: 5px;align-items: center;"}).inject(lastNode);
			new Element("img", { "src": "../x_component_IMV2/$Main/default/icons/location.png", "width": 24, "height": 24 }).inject(mapBox);
			new Element("span", {   "text": msgBody.address }).inject(mapBox);
		} else if (msgBody.type === "file") { //文件
			// 视频文件 mp4 avi ogg
			if (msgBody.fileExtension
				&& (msgBody.fileExtension.toLowerCase() === "mp4" || msgBody.fileExtension.toLowerCase() === "avi" || msgBody.fileExtension.toLowerCase() === "ogg")) {
				// var videoType = "video/" + msgBody.fileExtension.toLowerCase();
				new Element("video", {"class": "chat-content-video", "src": this._getFileDownloadUrl(msgBody.fileId), "controls": "controls", "preload": "preload"}).inject(lastNode);
			} else {
				var mapBox = new Element("span", {"style": "display: flex;gap: 5px;align-items: center;"}).inject(lastNode);
				var fileIcon = this._getFileIcon(msgBody.fileExtension);
				new Element("img", { "src": "../x_component_IMV2/$Main/file_icons/" + fileIcon, "width": 48, "height": 48 }).inject(mapBox);
				new Element("span", {"text": msgBody.fileName }).inject(mapBox);
			}
		} else if (msgBody.type === "process") {
			var cardNode = new Element("div", {"class": "chat-card"}).inject(lastNode);
			// 流程名称
			new Element("div", {"class": "chat-card-type", "text": "【"+msgBody.processName+"】"}).inject(cardNode);
			// 工作标题
			var title = msgBody.title;
			if (title == null || title === "") {
				title = "【"+msgBody.processName+"】- " + this.lp.noTitle;
			}
			new Element("div", {"class": "chat-card-body", "text":title}).inject(cardNode);
			var cardFooter = new Element("div", {"class": "chat-card-bottom"}).inject(cardNode);
			var appIconNode = new Element("img", {"class": "chat-card-bottom-icon"}).inject(cardFooter);
			this._loadProcessApplicationIcon(msgBody.application, function(appIcon) {
				if (appIcon && appIcon.icon) {
					appIconNode.set("src", "data:image/png;base64," + appIcon.icon);
				} else {
					console.log('没有找到应用图标');
					appIconNode.set("src", "../x_component_process_ApplicationExplorer/$Main/default/icon/application.png");
				}
			})
			new Element("div", { "class": "chat-card-bottom-name", "text": msgBody.applicationName }).inject(cardFooter);
		} else if (msgBody.type === "cms") {
		
		} else if (msgBody.type === "messageHistory") { // 聊天记录
			var cardNode = new Element("div", {"class": "chat-card"}).inject(lastNode);
			// title
			new Element("div", {"class": "chat-card-type", "text": msgBody.messageHistoryTitle}).inject(cardNode);
			// desc
			new Element("div", {"class": "chat-card-body", "text": msgBody.messageHistoryDesc }).inject(cardNode);
			var cardFooter = new Element("div", {"class": "chat-card-bottom"}).inject(cardNode);
			new Element("div", { "class": "chat-card-bottom-name", "text": this.lp.msgHistory }).inject(cardFooter);

		} else {//text
			new Element("span", { "text": this.contentEscapeBackToSymbol(msgBody.body) }).inject(lastNode);
		}

		// 引用消息
		if (msg.quoteMessage) {
			let quoteMessage = msg.quoteMessage;
			let node = this._newQuoteMessageElement(quoteMessage, receiverBodyNode);
			node.classList.add("chat-sender-quote-msg");
			node.store("msg", quoteMessage);
			node.addEvents({
				"click": function(e) {
					this._clickMsgItem(e, true);
				}.bind(this)
			});
		}

		if (isTop) {
			// 添加消息时间
			this._buildMsgTime(isTop, msg);
		}
		if (!isTop) {
			var scrollFx = new Fx.Scroll(this.chatContentNode);
			scrollFx.toBottom();
		}
	},
	/**
	 * 消息发送对象
	 * 这里的方法名错了两者互换了无需理会
	 * @param  msgBody 
	 * @param createPerson 消息人员
	 * @param isTop 是否放在顶部
	 * @param msg 消息对象
	 */
	_buildReceiver: function (msgBody, createPerson, isTop, msg) {
		if (!isTop) { 
			// 添加消息时间
			this._buildMsgTime(isTop, msg);
		}
		var msgItemNode = new Element("div", {"class": "chat-msg"}).inject(this.chatContentNode, isTop ? "top" : "bottom");

		var msgItemCheckBoxNode = new Element("div", {"class": "chat-msg-checkbox none"}).inject(msgItemNode);
		var msgItemCheckBoxInputNode =  new Element("input", {"type": "checkbox", "class": "check-box-select-item"}).inject(msgItemCheckBoxNode);
		msgItemCheckBoxInputNode.store("msg", msg)
		msgItemCheckBoxInputNode.addEvents({
			"click": function(e) {
				this._clickMsgItem(e);
			}.bind(this)
		})

		var receiverBodyNode = new Element("div", { "class": "chat-receiver", "id": msg.id}).inject(msgItemNode);
		this._addContextMenuEvent(receiverBodyNode, msg);
	
		var avatarNode = new Element("div", {"class": "chat-receiver-avatar"}).inject(receiverBodyNode);
		var avatarUrl = this.main._getIcon(createPerson);
		var name = createPerson;
		if (createPerson.indexOf("@") != -1) {
			name = name.substring(0, createPerson.indexOf("@"));
		}
		var avatarImg = new Element("img", { "src": avatarUrl }).inject(avatarNode);
		var nameNode = new Element("div", { "text": name , "class": "chat-receiver-name"}).inject(receiverBodyNode);
		var lastNodeClass = "chat-receiver-box"
		if (msgBody.type === "process" || msgBody.type === "cms") {
			lastNodeClass = "chat-receiver-card-box"
		}
		var lastNode = new Element("div", {"class": lastNodeClass}).inject(receiverBodyNode);
		lastNode.store("msg", msg);
		lastNode.addEvent("click", function(e) {
			this._clickMsgItem(e);
		}.bind(this))

		var lastFirstNode = new Element("div", { "class": "chat-right_triangle" }).inject(lastNode);

		if (msgBody.type === "emoji") { // 表情
			var img = "";
			for (var i = 0; i < this.main.emojiList.length; i++) {
				if (msgBody.body === this.main.emojiList[i].key) {
					img = this.main.emojiList[i].path;
				}
			}
			new Element("img", { "src": img, "class": "chat-content-emoji" }).inject(lastNode);
		} else if (msgBody.type === "image") {//image
			var imgBox = new Element("div", { "class": "img-chat" }).inject(lastNode);
			var url = this._getFileUrlWithWH(msgBody.fileId, 144, 192);
			if (msgBody.fileExtension && msgBody.fileExtension.toLowerCase() === "webp") {
				url = this._getFileDownloadUrl(msgBody.fileId);
			}
			new Element("img", { "src": url }).inject(imgBox);
		} else if (msgBody.type === "audio") {
			var url = this._getFileDownloadUrl(msgBody.fileId);
			new Element("audio", { "src": url, "controls": "controls", "preload": "preload" }).inject(lastNode);
		}  else if (msgBody.type === "location") {
			var mapBox = new Element("span", {"style": "display: flex;gap: 5px;align-items: center;"}).inject(lastNode);
			new Element("img", { "src": "../x_component_IMV2/$Main/default/icons/location.png", "width": 24, "height": 24 }).inject(mapBox);
			new Element("span", {   "text": msgBody.address }).inject(mapBox);
		} else if (msgBody.type === "file") { //文件
			// 视频文件 mp4 avi ogg
			if (msgBody.fileExtension
				&& (msgBody.fileExtension.toLowerCase() === "mp4" || msgBody.fileExtension.toLowerCase() === "avi" || msgBody.fileExtension.toLowerCase() === "ogg")) {
				//var videoType = "video/" + msgBody.fileExtension.toLowerCase();
				new Element("video", {"class": "chat-content-video","src": this._getFileDownloadUrl(msgBody.fileId), "controls": "controls", "preload": "preload"}).inject(lastNode);
			} else {
				var mapBox = new Element("span", {"style": "display: flex;gap: 5px;align-items: center;"}).inject(lastNode);
				var fileIcon = this._getFileIcon(msgBody.fileExtension);
				new Element("img", { "src": "../x_component_IMV2/$Main/file_icons/" + fileIcon, "width": 48, "height": 48 }).inject(mapBox);
				new Element("span", {"text": msgBody.fileName }).inject(mapBox);
			}
		} else if (msgBody.type === "process") {
			var cardNode = new Element("div", {"class": "chat-card"}).inject(lastNode);
			// 流程名称
			new Element("div", {"class": "chat-card-type", "text": "【"+msgBody.processName+"】"}).inject(cardNode);
			// 工作标题
			var title = msgBody.title;
			if (title == null || title === "") {
				title = "【"+msgBody.processName+"】- " + this.lp.noTitle;
			}
			new Element("div", {"class": "chat-card-body", "text":title}).inject(cardNode);
			var cardFooter = new Element("div", {"class": "chat-card-bottom"}).inject(cardNode);
			var appIconNode = new Element("img", {"class": "chat-card-bottom-icon"}).inject(cardFooter);
			this._loadProcessApplicationIcon(msgBody.application, function(appIcon) {
				if (appIcon && appIcon.icon) {
					appIconNode.set("src", "data:image/png;base64," + appIcon.icon);
				} else {
					console.log('没有找到应用图标');
					appIconNode.set("src", "../x_component_process_ApplicationExplorer/$Main/default/icon/application.png");
				}
			})
			new Element("div", { "class": "chat-card-bottom-name", "text": msgBody.applicationName }).inject(cardFooter);

		} else if (msgBody.type == "cms") {
		
		} else if (msgBody.type == "messageHistory") { // 聊天记录
			var cardNode = new Element("div", {"class": "chat-card"}).inject(lastNode);
			// title
			new Element("div", {"class": "chat-card-type", "text": msgBody.messageHistoryTitle}).inject(cardNode);
			// desc
			new Element("div", {"class": "chat-card-body", "text": msgBody.messageHistoryDesc }).inject(cardNode);
			var cardFooter = new Element("div", {"class": "chat-card-bottom"}).inject(cardNode);
			new Element("div", { "class": "chat-card-bottom-name", "text": this.lp.msgHistory }).inject(cardFooter);
		} else {//text
			new Element("span", { "text": this.contentEscapeBackToSymbol(msgBody.body) }).inject(lastNode);
		}

		// 引用消息
		if (msg.quoteMessage) {
			let quoteMessage = msg.quoteMessage;
			let node = this._newQuoteMessageElement(quoteMessage, receiverBodyNode);
			node.classList.add("chat-receiver-quote-msg");
			node.store("msg", quoteMessage);
			node.addEvents({
				"click": function(e) {
					this._clickMsgItem(e, true);
				}.bind(this)
			});
		}

		if (isTop) {
			// 添加消息时间
			this._buildMsgTime(isTop, msg);
		}
		if (!isTop) {
			var scrollFx = new Fx.Scroll(this.chatContentNode);
			scrollFx.toBottom();
		}
	},
	// 获取流程应用图标
	_loadProcessApplicationIcon: function(appId, callback) {
		if (!this.processApplications) {
			this.processApplications = [];
		}
		if (this.processApplications[appId]) {
			if (callback) callback(this.processApplications[appId]);
		} else {
			o2.Actions.load("x_processplatform_assemble_surface").ApplicationAction.	getIcon(appId, function (json) {
				if(json && json.data) {
					this.processApplications[appId] = json.data;
					if (callback) callback(json.data);
				} else {
					if (callback) callback();
				}
			}.bind(this), function (error) {
				console.error(error);
				if (callback) callback();
			}.bind(this))
		}
	},

	// 消息体上是否显示消息时间
	_buildMsgTime: function(isTop, msg) {
		var timeNode = new Element("div", { "class": "chat-msg-time"}).inject(this.chatContentNode, isTop ? "top" : "bottom");
		timeNode.set("text", this._msgShowTime(o2.common.toDate(msg.createTime)))
	},

	// 消息时间
	_msgShowTime: function (date) {
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
			var hour =  date.getHours() > 9 ? ""+date.getHours() : "0" + date.getHours();
			var minute =  date.getMinutes() > 9 ? ""+date.getMinutes() : "0" + date.getMinutes();
			retTime = hour + ":" +minute;
			return retTime;
		}
		var dates = parseInt(time / 86400000);
		var todaydates = parseInt(todayTime / 86400000);
		if (todaydates > dates) {
			var days = (todaydates - dates);
			if (days == 1) {
				var hour =  date.getHours() > 9 ? ""+date.getHours() : "0" + date.getHours();
				var minute =  date.getMinutes() > 9 ? ""+date.getMinutes() : "0" + date.getMinutes();
				retTime = this.lp.yesterday + " " +  hour + ":" +minute;
			} else if (days == 2) {
				var hour =  date.getHours() > 9 ? ""+date.getHours() : "0" + date.getHours();
				var minute =  date.getMinutes() > 9 ? ""+date.getMinutes() : "0" + date.getMinutes();
				retTime = this.lp.beforeYesterday + " " +  hour + ":" +minute;
			}else {
				var month = date.getMonth() + 1;
				var day = date.getDate();
				month = (month.toString().length == 1) ? ("0" + month) : month;
				day = (day.toString().length == 1) ? ("0" + day) : day;
				var hour =  date.getHours() > 9 ? ""+date.getHours() : "0" + date.getHours();
				var minute =  date.getMinutes() > 9 ? ""+date.getMinutes() : "0" + date.getMinutes();
				retTime = month + '-' + day + " " +  hour + ":" +minute;
			}
		}

		return retTime;

	},
	// 绑定右键事件
	_addContextMenuEvent: function(receiverBodyNode, msg) {
		receiverBodyNode.store("msg", msg);
		receiverBodyNode.addEvent("contextmenu", function(e) {
				//取消默认的浏览器自带右键 很重要！！
				e.preventDefault();
				if (this.selectMode) return; // 选择模式不需要右键菜单
				var menuleft=e.client.x+'px';
    			var menutop=e.client.y+'px';
				var m = receiverBodyNode.retrieve("msg");
				this._createMsgContextMenu(m, menuleft, menutop);
			}.bind(this)
		);
	},
	// 打开 消息体上 右键菜单
	_createMsgContextMenu: function(msg, menuleft, menutop) {
		var createPerson = msg.createPerson;
		var distinguishedName = layout.session.user.distinguishedName;
		var list = []; // 菜单列表
		
		if (this.main.imConfig.enableRevokeMsg) { // 是否启用撤回消息
			var revokeMinute = this.main.imConfig.revokeOutMinute ?? 2;
			if (revokeMinute <= 0) {
				revokeMinute = 2;
			}
			var createTime = o2.common.toDate(msg.createTime);
			if ( revokeMinute > 0 && (new Date().getTime() - createTime.getTime()) <  revokeMinute * 60 * 1000)  {
				if (createPerson !== distinguishedName) {
					// 判断是否群主
					var isGroupAdmin = false;
					for (var i = 0; i < this.main.conversationNodeItemList.length; i++) {
						var c = this.main.conversationNodeItemList[i];
						if (this.conversationId === c.data.id) {
							if (c.data.type === "group" && distinguishedName === c.data.adminPerson) {
								isGroupAdmin = true;
							}
						}
					}
					if (isGroupAdmin) {
						list.push({"id":"revokeMemberMsg", "text": this.lp.msgMenuItemRevokeMemberMsg});
					}
				} else {
					list.push({"id":"revokeMsg", "text": this.lp.msgMenuItemRevokeMsg});
				}
			}
		}
		// 转发
		list.push({"id":"forward", "text": this.lp.msgMenuItemForwardMsg});
		// 收藏
		list.push({"id":"collection", "text": this.lp.msgMenuItemCollectionMsg});
		// 选择
		list.push({"id":"select", "text": this.lp.msgMenuItemSelectMsg});
		// 引用
		list.push({"id":"quote", "text": this.lp.msgMenuItemQuoteMsg});

		if (this.menuNode) {
			this.menuNode.destroy();
			this.menuNode = null;
		}
		if (list.length > 0) {
			// 生成菜单
			this.menuNode = new Element("ul", {"class": "chat-menulist", "styles": { "position": "fixed", "z-index": "9999", "top": menutop, "left": menuleft } }).inject(this.container);
			for (let index = 0; index < list.length; index++) {
				const element = list[index];
				let menuItemNode = new Element("li", {"text": element.text}).inject(this.menuNode);
				menuItemNode.store('menuItemData', element);
				menuItemNode.store('menuItemMsgData', msg);
				menuItemNode.addEvents({
					"click": function(e) {
						let menuItemData = e.target.retrieve('menuItemData'); // 菜单项数据
						console.debug('点击菜单。。。。。。。。' + menuItemData.text)
						let menuItemMsgData = e.target.retrieve('menuItemMsgData'); // 消息数据
						this._clickMsgContextMenuItem(menuItemData, menuItemMsgData);
						e.preventDefault();
					}.bind(this)
				});
			}
			// 添加关闭菜单事件
			this.closeMsgContextMenuFun = function(e) {
				if (this.menuNode) {
					this.menuNode.destroy();
					this.menuNode = null;
				}
				e.preventDefault();
				if( this.closeMsgContextMenuFun )this.main.app.content.removeEvent( "click", this.closeMsgContextMenuFun );
			}.bind(this);
	
			this.main.app.content.addEvents({
				"click": this.closeMsgContextMenuFun
			});
		}
	},
	// 点击 右键菜单项
	_clickMsgContextMenuItem: function(menuItemData, msg) {
		debugger
		// 关闭菜单
		if (this.menuNode) {
			this.menuNode.destroy();
			this.menuNode = null;
		}
		// 根据菜单不同处理不同内容
		// 撤回
		if (menuItemData.id === "revokeMemberMsg" || menuItemData.id === "revokeMsg") {
			this._revokeMsg(msg);
		}
		if (menuItemData.id === "forward") {
			this.forwardMsgList([msg])
		}
		if (menuItemData.id === "collection") {
			this.collectionMsgList([msg])
		}
		if (menuItemData.id === "select") {
			if (this.selectMode) {
				this.cancelSelectMode()
			} else {
				this.openSelectMode(msg)
			}
		}
		if (menuItemData.id === "quote") {
			this.addQuoteMessage(msg)
		}
	},
	// 撤回消息
	_revokeMsg: function(msg) {
		o2.Actions.load("x_message_assemble_communicate").ImAction.msgRevoke(msg.id, function(json) {
			console.debug("撤回消息：", json);
			// 删除消息
			$(msg.id).destroy();
		}.bind(this));
	},
	//图片 根据大小 url
	_getFileUrlWithWH: function (id, width, height) {
		var action = MWF.Actions.get("x_message_assemble_communicate").action;
		var url = action.getAddress() + action.actions.imgFileDownloadWithWH.uri;
		url = url.replace("{id}", encodeURIComponent(id));
		url = url.replace("{width}", encodeURIComponent(width));
		url = url.replace("{height}", encodeURIComponent(height));
		return url;
	},
	//file 下载的url
	_getFileDownloadUrl: function (id) {
		var action = MWF.Actions.get("x_message_assemble_communicate").action;
		var url = action.getAddress() + action.actions.imgFileDownload.uri;
		url = url.replace("{id}", encodeURIComponent(id));
		return url;
	},
	//百度地图打开地址
	_getBaiduMapUrl: function (lat, longt, address, content) {
		var url = "https://api.map.baidu.com/marker?location=" + lat + "," + longt + "&title=" + address + "&content=" + content + "&output=html&src=net.o2oa.map";
		return url;
	},
	// 文件类型icon图
	_getFileIcon: function (ext) {
		if (ext) {
			if (ext === "jpg" || ext === "jpeg") {
				return "icon_file_jpeg.png";
			} else if (ext === "gif") {
				return "icon_file_gif.png";
			} else if (ext === "png") {
				return "icon_file_png.png";
			} else if (ext === "tiff") {
				return "icon_file_tiff.png";
			} else if (ext === "bmp" || ext === "webp") {
				return "icon_file_img.png";
			} else if (ext === "ogg" || ext === "mp3" || ext === "wav" || ext === "wma") {
				return "icon_file_mp3.png";
			} else if (ext === "mp4") {
				return "icon_file_mp4.png";
			} else if (ext === "avi") {
				return "icon_file_avi.png";
			} else if (ext === "mov" || ext === "rm" || ext === "mkv") {
				return "icon_file_rm.png";
			} else if (ext === "doc" || ext === "docx") {
				return "icon_file_word.png";
			} else if (ext === "xls" || ext === "xlsx") {
				return "icon_file_excel.png";
			} else if (ext === "ppt" || ext === "pptx") {
				return "icon_file_ppt.png";
			} else if (ext === "html") {
				return "icon_file_html.png";
			} else if (ext === "pdf") {
				return "icon_file_pdf.png";
			} else if (ext === "txt" || ext === "json") {
				return "icon_file_txt.png";
			} else if (ext === "zip") {
				return "icon_file_zip.png";
			} else if (ext === "rar") {
				return "icon_file_rar.png";
			} else if (ext === "7z") {
				return "icon_file_arch.png";
			} else if (ext === "ai") {
				return "icon_file_ai.png";
			} else if (ext === "att") {
				return "icon_file_att.png";
			} else if (ext === "au") {
				return "icon_file_au.png";
			} else if (ext === "cad") {
				return "icon_file_cad.png";
			} else if (ext === "cdr") {
				return "icon_file_cdr.png";
			} else if (ext === "eps") {
				return "icon_file_eps.png";
			} else if (ext === "exe") {
				return "icon_file_exe.png";
			} else if (ext === "iso") {
				return "icon_file_iso.png";
			} else if (ext === "link") {
				return "icon_file_link.png";
			} else if (ext === "swf") {
				return "icon_file_flash.png";
			} else if (ext === "psd") {
				return "icon_file_psd.png";
			} else if (ext === "tmp") {
				return "icon_file_tmp.png";
			} else {
				return "icon_file_unkown.png";
			}
		} else {
			return "icon_file_unkown.png";
		}
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

// 会话对象
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
			"lastMessage": "",
			"lastMessageType": "text"
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
			if (mBody.type) {
				convData.lastMessageType = mBody.type;
				if (mBody.type === "process") {
					var title = mBody.title;
					if (title == null || title == "") {
						title = "【" + mBody.processName + "】- " + this.lp.noTitle;
					}
					convData.lastMessage = title;
				} else if (mBody.type === "cms") {
					convData.lastMessage = mBody.title || "";
				}
			}

		}
		this.node = new Element("div", { "class": "item" }).inject(this.container, this.data.isNew ? 'top' : '');
		this.nodeBaseItem = new Element("div", { "class": "base" }).inject(this.node);
		var avatarNode = new Element("div", { "class": "avatar" }).inject(this.nodeBaseItem);
		new Element("img", { "src": convData.avatarUrl, "class": "img" }).inject(avatarNode);
		var bodyNode = new Element("div", { "class": "body" }).inject(this.nodeBaseItem);
		var bodyUpNode = new Element("div", { "class": "body_up" }).inject(bodyNode);
		this.titleNode = new Element("div", { "class": "body_title", "text": convData.title }).inject(bodyUpNode);
		this.messageTimeNode = new Element("div", { "class": "body_time", "text": convData.time }).inject(bodyUpNode);
		if (convData.lastMessageType == "emoji") {
			this.lastMessageNode = new Element("div", { "class": "body_down" }).inject(bodyNode);
			var imgPath = "";
			for (var i = 0; i < this.main.emojiList.length; i++) {
				var emoji = this.main.emojiList[i];
				if (emoji.key == convData.lastMessage) {
					imgPath = emoji.path;
				}
			}
			new Element("img", { "src": imgPath, "style": "width: 16px;height: 16px;" }).inject(this.lastMessageNode);
		} else {
			this.lastMessageNode = new Element("div", { "class": "body_down", "text": convData.lastMessage }).inject(bodyNode);
		}

		var _self = this;
		this.node.addEvents({
			"click": function () {
				_self.main.tapConv(_self.data);
			}
		});
	},
	/**
	 *
	 * 刷新会话列表的最后消息内容 
	 * @param {*} lastMessage 
	 */
	refreshLastMsg: function (lastMessage) {
		if (lastMessage) {
			//目前是text 类型的消息
			var jsonbody = lastMessage.body;
			var body = JSON.parse(jsonbody);

			if (this.lastMessageNode) {
				if (body.type == "emoji") { //表情 消息
					var imgPath = "";
					for (var i = 0; i < this.main.emojiList.length; i++) {
						var emoji = this.main.emojiList[i];
						if (emoji.key == body.body) {
							imgPath = emoji.path;
						}
					}
					this.lastMessageNode.empty();
					new Element("img", { "src": imgPath, "style": "width: 16px;height: 16px;" }).inject(this.lastMessageNode);
				} else { //文本消息
					this.lastMessageNode.empty();
					this.lastMessageNode.set('text', body.body);
				}
			}
			var time = this.main._friendlyTime(o2.common.toDate(lastMessage.createTime));
			if (this.messageTimeNode) {
				this.messageTimeNode.set("text", time);
			}
		}
	},
	// 更新聊天窗口上的标题 修改标题的时候使用 @Disuse 使用refreshData
	refreshConvTitle: function (title) {
		this.titleNode.set("text", title);
	},
	// 更新会话数据
	refreshData: function (data) {
		this.data = data;
		// 更新聊天窗口上的标题 修改标题的时候使用
		this.titleNode.set("text", data.title);
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
		"title": MWF.xApplication.IMV2.LP.createSingle
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
				person: { text: MWF.xApplication.IMV2.LP.selectPerson, type: "org", orgType: "person", count: 0, notEmpty: true, exclude: exclude },
			}
		}, this.app);
		this.form.load();

	},
	_createBottomContent: function () {
		if (this.isNew || this.isEdited) {
			this.okActionNode = new Element("button.inputOkButton", {
				"styles": this.css.inputOkButton,
				"text": MWF.xApplication.IMV2.LP.ok
			}).inject(this.formBottomNode);
			this.okActionNode.addEvent("click", function (e) {
				this.save(e);
			}.bind(this));
		}
		this.cancelActionNode = new Element("button.inputCancelButton", {
			"styles": (this.isEdited || this.isNew || this.getEditPermission()) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
			"text": MWF.xApplication.IMV2.LP.close
		}).inject(this.formBottomNode);
		this.cancelActionNode.addEvent("click", function (e) {
			this.close(e);
		}.bind(this));

	},
	save: function () {
		var data = this.form.getResult(true, null, true, false, true);
		if (data) {
			this.app.newConversation(data.person, "single");
			this.close();
		}
	}
});


//创建聊天 弹出窗表单
MWF.xApplication.IMV2.CreateConversationForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		"style": "minder",
		"width": 700,
		"height": "200",
		"hasTop": true,
		"hasIcon": false,
		"draggable": true,
		"title": MWF.xApplication.IMV2.LP.createSingle,
		"personCount": 1, //1 是单选  0 是多选,
		"personSelected": [],
		"isUpdateMember": false
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
				person: { text: MWF.xApplication.IMV2.LP.selectPerson, type: "org", orgType: "person", count: this.options["personCount"], notEmpty: true, exclude: exclude, value: this.options["personSelected"] },
			}
		}, this.app);
		this.form.load();

	},
	_createBottomContent: function () {
		if (this.isNew || this.isEdited) {
			this.okActionNode = new Element("button.inputOkButton", {
				"styles": this.css.inputOkButton,
				"text": MWF.xApplication.IMV2.LP.ok
			}).inject(this.formBottomNode);
			this.okActionNode.addEvent("click", function (e) {
				this.save(e);
			}.bind(this));
		}
		this.cancelActionNode = new Element("button.inputCancelButton", {
			"styles": (this.isEdited || this.isNew || this.getEditPermission()) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
			"text": MWF.xApplication.IMV2.LP.close
		}).inject(this.formBottomNode);
		this.cancelActionNode.addEvent("click", function (e) {
			this.close(e);
		}.bind(this));

	},
	save: function () {
		var data = this.form.getResult(true, null, true, false, true);
		if (data) {
			if (this.options["isUpdateMember"] === true) {
				this.app.updateConversationMembers(data.person, this.app.conversationId);
			} else {
				this.app.newConversation(data.person, this.options["personCount"] === 1 ? "single" : "group");
			}

			this.close();
		}
	}
});



//修改群名
MWF.xApplication.IMV2.UpdateConvTitleForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		"style": "minder",
		"width": 500,
		"height": "200",
		"hasTop": true,
		"hasIcon": false,
		"draggable": true,
		"defaultValue": "", // 默认值
		"title": MWF.xApplication.IMV2.LP.modifyGroupName
	},
	_createTableContent: function () {
		var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
			"<tr><td styles='formTableTitle' lable='title' width='25%'></td>" +
			"    <td styles='formTableValue14' item='title' colspan='3'></td></tr>" +
			"</table>";
		this.formTableArea.set("html", html);

		this.form = new MForm(this.formTableArea, this.data || {}, {
			isEdited: true,
			style: "minder",
			hasColon: true,
			itemTemplate: {
				title: { text: MWF.xApplication.IMV2.LP.groupName, type: "text", notEmpty: true, value:  this.options["defaultValue"] },
			}
		}, this.app);
		this.form.load();

	},
	_createBottomContent: function () {
		if (this.isNew || this.isEdited) {
			this.okActionNode = new Element("button.inputOkButton", {
				"styles": this.css.inputOkButton,
				"text": MWF.xApplication.IMV2.LP.ok
			}).inject(this.formBottomNode);
			this.okActionNode.addEvent("click", function (e) {
				this.save(e);
			}.bind(this));
		}
		this.cancelActionNode = new Element("button.inputCancelButton", {
			"styles": (this.isEdited || this.isNew || this.getEditPermission()) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
			"text": MWF.xApplication.IMV2.LP.close
		}).inject(this.formBottomNode);
		this.cancelActionNode.addEvent("click", function (e) {
			this.close(e);
		}.bind(this));
	},
	save: function () {
		var data = this.form.getResult(true, null, true, false, true);
		if (data) {
			this.app.updateConversationTitle(data.title, this.app.conversationId);
			this.close();
		}
	}
});


// 消息列表
MWF.xApplication.IMV2.ChatMessageList = new Class({
	initialize: function (data, main) {
		this.data = data;
		this.main = main;
		this.app = main.app;
		this.container = main.messageHistoryNode; // 有专门的容器
		this.lp = main.lp;
		this.path = main.path;
		this.options = main.options;
		this.msg = data.msg; // 消息对象 里面的 id 作为标识
		this.page = 1;
		this.collectionMode = !!this.data.collectionMode; // 是否是收藏模式
		this.selectMode = false; // 选择模式
		this.hasMoreCollection = false; // 是否有更多收藏
		this.isLoadingCollection = false; // 是否正在加载
		this.selectMsgList = [];
		this.collectionList = [];
		this.load();
	},
	load: function () {
		var url = this.path + this.options.style + "/messageList.html";
		this.container.loadHtml(url, { "bind": { "thisTitle": this.data.title ?? this.lp.msgHistory, "lp": this.lp }, "module": this }, function () {
			console.debug("加载完成");
			this._layout();
			if (this.collectionMode) {
				this._addScrollListener();
				this.page = 1;
				this.loadMsgCollectionList();
			} else {
				this.loadMsgList();
			}
		}.bind(this));
	},
	// 删除当前节点 给上层调用的
	deleteSelfNode: function () {
		this.messageListBoxNode.destroy()
	},
	// 关闭 调用了上层的方法，为了关闭遮罩层
	close: function () {
		 this.main.closeMessageHistory(this.msg);
	},
	_layout: function() {
		if (this.collectionMode) {
			this.messageListBoxNode.style.height = '90%';
			this.messageListBoxNode.style['max-width'] =  '700px';
			this.messageListToolNode.classList.remove('none');
			this.messageListToolNode.classList.add('block');

		}
		const rect = this.messageListBoxNode.parentElement.getBoundingClientRect()
		this.parentElWidth = rect.width
		this.parentElHeight = rect.height
		const selfRect = this.messageListBoxNode.getBoundingClientRect()
		this.selfElWidth = selfRect.width
		this.selfElHeight = selfRect.height
		const left = (rect.width - selfRect.width) / 2
		const top = (rect.height - selfRect.height) / 2
		this.messageListBoxNode.style.position = 'absolute';
		this.messageListBoxNode.style.left = left + 'px';
		this.messageListBoxNode.style.top = top + 'px';

		console.debug("加载_layout完成")
		this._draggable()
	},
	_draggable: function() {
		this.messageListBoxHeaderNode.addEventListener('mousedown', (e) => {
			this.offsetX = e.clientX - this.messageListBoxNode.offsetLeft
			this.offsetY = e.clientY - this.messageListBoxNode.offsetTop
			this.isDragging = true
		})
		this.messageListBoxHeaderNode.addEventListener('mousemove', (e) => {
			if (this.isDragging) {
				let left = e.clientX - this.offsetX
				if (left < 0) {
					left = 0
				}
				if (left > this.parentElWidth - this.selfElWidth) {
					left = this.parentElWidth - this.selfElWidth
				}
				let top = e.clientY - this.offsetY
				if (top < 0) {
					top = 0
				}
				if (top > this.parentElHeight - this.selfElHeight) {
					top = this.parentElHeight - this.selfElHeight
				}
				this.messageListBoxNode.style.left = left + 'px'
				this.messageListBoxNode.style.top = top + 'px'
			}
		})
		this.messageListBoxHeaderNode.addEventListener('mouseup', () => {
			this.isDragging = false
		})
		console.debug("加载 _draggable 完成")
	},
	// 删除选中的收藏
	deleteSelectedCollection: function () {
		if (this.selectMsgList.length < 1) {
			this.app.notice(this.lp.msgNeedSelectMessage, "error", this.app.content);
			return;
		}
		let deleteIdList = [];
		for (let i = 0; i < this.collectionList.length; i++) {
			const collection = this.collectionList[i];
			if ( this.selectMsgList.findIndex( m => m.id === collection.message.id) > -1) {
				deleteIdList.push(collection.id)
			}
		}
		if (deleteIdList.length < 1) {
			return;
		}
		o2.Actions.load("x_message_assemble_communicate").ImAction.msgCollectionRemove({msgIdList: deleteIdList}, function (json) {
			 console.log('删除成功！');
			 this.page = 1;
			 this.cancelSelectMode();
			 this.loadMsgCollectionList();
		}.bind(this), function (error) {
			console.error(error);
		}.bind(this));
	},
	// 选择模式
	openSelectMode: function() {
		this.selectMode = true;
		this.messageListSelectBtnNode.classList.remove('block');
		this.messageListSelectBtnNode.classList.add('none');
		this.messageListCancelBtnNode.classList.remove('none');
		this.messageListCancelBtnNode.classList.add('block');
		this.messageListDeleteCollectionBtnNode.classList.remove('none');
		this.messageListDeleteCollectionBtnNode.classList.add('block');
		this.selectMsgList = [];
		const list = this.messageListNode.querySelectorAll(".chat-msg-checkbox")
		list.forEach(item => {
			item.classList.remove("none")
			item.classList.add("block")
		})
	},
	// 取消选择模式
	cancelSelectMode: function () {
		this.selectMode = false;
		this.messageListSelectBtnNode.classList.remove('none');
		this.messageListSelectBtnNode.classList.add('block');
		this.messageListCancelBtnNode.classList.remove('block');
		this.messageListCancelBtnNode.classList.add('none');
		this.messageListDeleteCollectionBtnNode.classList.remove('block');
		this.messageListDeleteCollectionBtnNode.classList.add('none');
		this.selectMsgList = [];
		const list = this.messageListNode.querySelectorAll(".chat-msg-checkbox")
		list.forEach(item => {
			item.classList.remove("block")
			item.classList.add("none")
		})
		this._selectOrUnSelectMsg()
	},
	_selectOrUnSelectMsg: function (msg) {
		if (msg) {
			if (this.selectMsgList.findIndex( m => m.id === msg.id) > -1) {
				this.selectMsgList.splice(this.selectMsgList.findIndex( m => m.id === msg.id), 1);
			} else {
				this.selectMsgList.push(msg);
			}
		}
		var checkList = this.messageListNode.querySelectorAll(".check-box-select-item")
		checkList.forEach(item => {
			var checkMsg = item.retrieve("msg")
			if (this.selectMsgList.findIndex( m => m.id === checkMsg.id) > -1) {
				item.checked = true
			} else {
				item.checked = false
			}
		})
	},

	_addScrollListener: function () {
		console.debug('_addScrollListener ', this.messageListContainerNode);
		this.messageListContainerNode.addEvents({
			"scroll": function () {
				// 检查是否滚动到底部
				if (this.messageListContainerNode.scrollTop + this.messageListContainerNode.clientHeight >= this.messageListContainerNode.scrollHeight) {
					this.loadMoreMsgCollectionList();  // 加载更多内容
				}
			}.bind(this)
		});
	},
	// 加载更多
	loadMoreMsgCollectionList: function () {
		if (!this.hasMoreCollection) return;
		this.page += 1;
		this.loadMsgCollectionList()
	},
	// 分页查询收藏列表
	loadMsgCollectionList: function () {
		if (this.isLoadingCollection) return;
		if (this.page === 1) {
			while (this.messageListNode.firstChild) {
				this.messageListNode.removeChild(
					this.messageListNode.firstChild
				)
			}
			this.collectionList = []
		}
		this.isLoadingCollection = true;
		o2.Actions.load("x_message_assemble_communicate").ImAction.collectionListByPaging(''+this.page, '20', {}, function (json) {
			let list = json.data;
			if (list && list.length > 0) {
				for (let i = 0; i < list.length; i++) {
					const msg = list[i];
					this._renderMsgItem(msg.message);
					this.collectionList.push(msg); // 存储收藏列表
				}
			}
			this.hasMoreCollection = (list && list.length === 20);
			this.isLoadingCollection = false;
		}.bind(this), function (error) {
			console.error(error);
			this.hasMoreCollection = false;
			this.isLoadingCollection = false;
		}.bind(this));
	},
	loadMsgList: function (){
		var msgBody = JSON.parse(this.msg.body)
		var messageHistoryIds = msgBody.messageHistoryIds;
		if (messageHistoryIds && messageHistoryIds.length > 0) {
			o2.Actions.load("x_message_assemble_communicate").ImAction.msgListObject({msgIdList: messageHistoryIds }, function (json) {
				var list = json.data;
				if (list && list.length > 0) {
					for (let i = 0; i < list.length; i++) {
						const msg = list[i];
						this._renderMsgItem(msg)
					}
				}
			}.bind(this), function (error) {
				console.error(error);
			}.bind(this));
		}
	},
	clickMsgItem(e, quoteMessage) {
		e.stopPropagation();
		var msg = e.event.currentTarget.retrieve("msg");
		if (!msg || !msg.body) {
			console.error('错误的 target！！！');
			return;
		}

		if (this.selectMode) {
			if (quoteMessage) {
				return;
			}
			this._selectOrUnSelectMsg(msg)
		} else {
			this.main.openMsgItem(msg);
		}
	},
	_renderMsgItem: function (msg) {
		var msgBody = JSON.parse(msg.body)
		var msgItemNode = new Element("div", {"class": "chat-msg"}).inject(this.messageListNode);
		msgItemNode.store("msg", msg);
		msgItemNode.addEvents({
			"click": function(e) {
				this.clickMsgItem(e);
			}.bind(this)
		})
		/// checkbox
		var checkBoxClass = "chat-msg-checkbox none"
		if (this.selectMode) {
			checkBoxClass = "chat-msg-checkbox block"
		}
		var msgItemCheckBoxNode = new Element("div", {"class": checkBoxClass}).inject(msgItemNode);
		var msgItemCheckBoxInputNode =  new Element("input", {"type": "checkbox", "class": "check-box-select-item"}).inject(msgItemCheckBoxNode);
		msgItemCheckBoxInputNode.store("msg", msg)
		/// 消息体
		var receiverBodyNode = new Element("div", { "class": "chat-sender", "id": msg.id}).inject(msgItemNode);
		/// 消息时间
		var timeNode = new Element("div", { "class": "chat-msg-time", "style": "width: 48px;"}).inject(msgItemNode);
		timeNode.set("text", this.main._msgShowTime(o2.common.toDate(msg.createTime)))

		var avatarNode = new Element("div", {"class": "chat-sender-avatar"}).inject(receiverBodyNode);
		var avatarUrl = this.main.main._getIcon(msg.createPerson);
		var name = msg.createPerson;
		if (msg.createPerson.indexOf("@") > -1) {
			name = name.substring(0, msg.createPerson.indexOf("@"));
		}
		new Element("img", { "src": avatarUrl }).inject(avatarNode);
		new Element("div", { "text": name , "class": "chat-sender-name"}).inject(receiverBodyNode);
		var lastNodeClass = "chat-sender-box"
		if (msgBody.type === "process" || msgBody.type === "cms") {
			lastNodeClass = "chat-sender-card-box"
		}
		var lastNode = new Element("div", {"class": lastNodeClass}).inject(receiverBodyNode);
		var lastFirstNode = new Element("div", { "class": "chat-left_triangle" }).inject(lastNode);
		//text
		if (msgBody.type === "emoji") { // 表情
			var img = "";
			for (var i = 0; i < this.main.main.emojiList.length; i++) {
				if (msgBody.body === this.main.main.emojiList[i].key) {
					img = this.main.main.emojiList[i].path;
				}
			}
			new Element("img", { "src": img, "class": "chat-content-emoji" }).inject(lastNode);
		} else if (msgBody.type === "image") {//image
			var imgBox = new Element("div", { "class": "img-chat" }).inject(lastNode);
			var url = this.main._getFileUrlWithWH(msgBody.fileId, 144, 192);
			if (msgBody.fileExtension && msgBody.fileExtension.toLowerCase() === "webp") {
				url = this.main._getFileDownloadUrl(msgBody.fileId);
			}
			new Element("img", { "src": url }).inject(imgBox);
		} else if (msgBody.type === "audio") {
			var url = this.main._getFileDownloadUrl(msgBody.fileId);
			new Element("audio", { "src": url, "controls": "controls", "preload": "preload" }).inject(lastNode);
		} else if (msgBody.type === "location") {
			var mapBox = new Element("span", {"style": "display: flex;gap: 5px;align-items: center;"}).inject(lastNode);
			new Element("img", { "src": "../x_component_IMV2/$Main/default/icons/location.png", "width": 24, "height": 24 }).inject(mapBox);
			new Element("span", {   "text": msgBody.address }).inject(mapBox);
		} else if (msgBody.type === "file") { //文件
			// 视频文件 mp4 avi ogg
			if (msgBody.fileExtension
				&& (msgBody.fileExtension.toLowerCase() === "mp4" || msgBody.fileExtension.toLowerCase() === "avi" || msgBody.fileExtension.toLowerCase() === "ogg")) {
				// var videoType = "video/" + msgBody.fileExtension.toLowerCase();
				new Element("video", {"class": "chat-content-video", "src":  this.main._getFileDownloadUrl(msgBody.fileId), "controls": "controls", "preload": "preload"}).inject(lastNode);
			} else {
				var mapBox = new Element("span", {"style": "display: flex;gap: 5px;align-items: center;"}).inject(lastNode);
				var fileIcon = this.main._getFileIcon(msgBody.fileExtension);
				new Element("img", { "src": "../x_component_IMV2/$Main/file_icons/" + fileIcon, "width": 48, "height": 48 }).inject(mapBox);
				new Element("span", {"text": msgBody.fileName }).inject(mapBox);
			}
		} else if (msgBody.type === "process") {
			var cardNode = new Element("div", {"class": "chat-card"}).inject(lastNode);
			// 流程名称
			new Element("div", {"class": "chat-card-type", "text": "【"+msgBody.processName+"】"}).inject(cardNode);
			// 工作标题
			var title = msgBody.title;
			if (title == null || title === "") {
				title = "【"+msgBody.processName+"】- " + this.lp.noTitle;
			}
			new Element("div", {"class": "chat-card-body", "text":title}).inject(cardNode);
			var cardFooter = new Element("div", {"class": "chat-card-bottom"}).inject(cardNode);
			var appIconNode = new Element("img", {"class": "chat-card-bottom-icon"}).inject(cardFooter);
			this.main._loadProcessApplicationIcon(msgBody.application, function(appIcon) {
				if (appIcon && appIcon.icon) {
					appIconNode.set("src", "data:image/png;base64," + appIcon.icon);
				} else {
					console.log('没有找到应用图标');
					appIconNode.set("src", "../x_component_process_ApplicationExplorer/$Main/default/icon/application.png");
				}
			})
			new Element("div", { "class": "chat-card-bottom-name", "text": msgBody.applicationName }).inject(cardFooter);
		} else if (msgBody.type === "cms") {

		} else if (msgBody.type === "messageHistory") { // 聊天记录
			var cardNode = new Element("div", {"class": "chat-card"}).inject(lastNode);
			// title
			new Element("div", {"class": "chat-card-type", "text": msgBody.messageHistoryTitle}).inject(cardNode);
			// desc
			new Element("div", {"class": "chat-card-body", "text": msgBody.messageHistoryDesc }).inject(cardNode);
			var cardFooter = new Element("div", {"class": "chat-card-bottom"}).inject(cardNode);
			new Element("div", { "class": "chat-card-bottom-name", "text": this.lp.msgHistory }).inject(cardFooter);
		} else {//text
			new Element("span", { "text": this.main.contentEscapeBackToSymbol(msgBody.body) }).inject(lastNode);
		}
		// 引用消息
		if (msg.quoteMessage) {
			let quoteMessage = msg.quoteMessage;
			let node = this.main._newQuoteMessageElement(quoteMessage, receiverBodyNode);
			node.classList.add("chat-sender-quote-msg");
			node.store("msg", quoteMessage);
			node.addEvents({
				"click": function(e) {
					this.clickMsgItem(e, true);
				}.bind(this)
			});
		}
	}
});