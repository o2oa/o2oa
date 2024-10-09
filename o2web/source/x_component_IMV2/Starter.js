MWF.require("MWF.widget.UUID", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.IMV2 = MWF.xApplication.IMV2 || {};
MWF.require("MWF.widget.Mask", null, false);
MWF.xDesktop.requireApp("IMV2", "lp."+o2.language, null, false);

MWF.xApplication.IMV2.Starter = new Class({
  Extends: MWF.widget.Common,
  Implements: [Options, Events],
  options: {
    "style": "default",
    "businessType" : "none", // 业务类型 :none, process, cms  。启动聊天 是否带业务类型
    "businessId": null, // 业务ID 新创建为空
    "includePersonList": [], // 在规定范围内的人员进行选择, 如果为空，则不限制
    "conversationId": null, // 传入的会话id, 新创建为空
		"mode": "default" // 展现模式：default onlyChat 。 onlyChat的模式需要传入conversationId 会打开这个会话的聊天窗口并隐藏左边的会话列表
  },
  initialize: function(data, app, options){
    console.log("init   IMV2.Starter " );
    this.setOptions(options);
    this.path = "../x_component_IMV2/$Main/";
    MWF.xDesktop.requireApp("IMV2", "lp."+o2.language, null, false);
    this.lp = MWF.xApplication.IMV2.LP;
    this.data = data;
    this.app = app;
  },
  load: function(){
    console.log("init   IMV2.load    " );
    debugger;
    // 打开聊天会话
    if (this.options.conversationId && this.options.conversationId !== "") {
      this.openConversationWindow(this.options.conversationId);
    } else {
      // 创建聊天
      var me = layout.session.user.distinguishedName;
      var exclude = [];
      if (me) {
        exclude = [me];
      }
      // 默认创建聊天
      if ( !(this.options.businessType) || this.options.businessType === "none" || !(this.options.businessId) || this.options.businessId === ""  ) {
        var form = new MWF.xApplication.IMV2.Starter.CreateConversationForm(this);
        form.create()
      } else if (this.options.businessType === "process") {
        if (this.options.businessId) {
          //TODO 如果已经存在是否考虑可创建多个，那就需要提示创建新的还是打开老的
          console.log("根据流程的job id查询，会话是否存在，如果存在，则打开，如果不存在，则创建。jobId: " + this.options.businessId);
          this.findConversationByBusinessId();
        } else {
          this.app.notice(this.lp.msgNoBusinessId, "error");
        }
        //打开工作 o2.env.form.openJob
      } else {
        this.app.notice(this.lp.msgNotSupport, "error");
      }
      
    }
    
  },
   // 打开会话聊天窗口
   openConversationWindow: function(conversationId) {
    var options = {
      conversationId: conversationId,
      mode: this.options.mode || "default"
    }
    layout.openApplication(null, "IMV2", options);
  },
  // 创建会话
  newConversation: function(personList) {
    console.log("newConversation", personList);
    var cType = "single"; // cType 会话类型 "single" "group"
    if (personList.length > 1) {
      cType = "group"
    }
    var conv = {
			type: cType,
			personList: personList,
		};
    if (this.options.businessId) {
      conv.businessId = this.options.businessId;
      conv.businessType = this.options.businessType;
    }
		o2.Actions.load("x_message_assemble_communicate").ImAction.create(conv, function (json) {
			var newConv = json.data;
      if (this.app.refreshAll) this.app.refreshAll();
      this.openConversationWindow(newConv.id);
		}.bind(this), function (error) {
			console.log(error);
      this.app.notice(error, "error");
      if (this.app.refreshAll) this.app.refreshAll();
		}.bind(this))
  },
  // 根据流程的job id查询，会话是否存在
  findConversationByBusinessId: function() {
    o2.Actions.load("x_message_assemble_communicate").ImAction.conversationFindByBusinessId(this.options.businessId, function(json){
      if (json.data && json.data.length > 0) {
        this.showChooseConversationDialog(json.data);
      } else {
        if (this.options.businessType === "process") {
          this.getProcessReviewByJobId();
        }
      }
    }.bind(this), function (error) {
      console.log(error);
      if (this.options.businessType === "process") {
        this.getProcessReviewByJobId();
      }
    }.bind(this));
  },
  // 有存在的会话 展现可以点击打开会话聊天
  showChooseConversationDialog: function(conversationList) {
    if (conversationList && conversationList.length > 0) {
      var cssPath = this.path + "default/style.css";
      var chooseConversationHtmlPath = this.path + "default/chooseConversation.html";
      this.app.content.loadAll({
        "css": [cssPath],
        "html": chooseConversationHtmlPath,
      }, { "bind": { "lp": this.lp, "data": {} }, "module": this }, function(){
        // 会话列表
        for (let index = 0; index < conversationList.length; index++) {
          const conv = conversationList[index];
          // <div class="conversation-item">群聊1111 | <span style="color: #4A90E2;">打开</span></div>
          var conversationItem = new Element("div", {"class": "conversation-item"}).inject(this.conversationChooseListNode);
          new Element("span", {"text": "【"+conv.title+"】"}).inject(conversationItem);
          new Element("span", {"text": "打开", "style": "color: #4A90E2;"}).inject(conversationItem);
          // conversationItem.set("text", conv.title + " | <span style=\"color: #4A90E2;\">打开</span>");
          conversationItem.store("conversation", conv);
          conversationItem.addEvents({
            "click": function(e){
              debugger;
              var myConv = null;
              if (e.target.get("tag") === "span") {
                myConv = e.target.parentNode.retrieve("conversation");
              } else {
                myConv = e.target.retrieve("conversation");
              }
              if (myConv) {
                this.openConversationWindow(myConv.id);
                this.closeChooseConversationDialog();
              }
              
            }.bind(this)
          });
        }
        // 关闭按钮
        this.conversationChooseCloseNode.addEvents({
          "click": function(e){
            this.closeChooseConversationDialog();
          }.bind(this)
        });
        // 新建聊天按钮
        this.conversationCreateNewNode.addEvents({
          "click": function(e){
            this.closeChooseConversationDialog();
            if (this.options.businessType === "process") {
              this.getProcessReviewByJobId();
            }
          }.bind(this)
        })
      }.bind(this));
    }
  },
  // 关闭会话选择窗口
  closeChooseConversationDialog: function() {
    if (this.conversationChooseDialogNode) {
      this.conversationChooseDialogNode.destroy();
      this.conversationChooseDialogNode = null;
    }
    if (this.app.refreshAll) this.app.refreshAll();
  },
  // 显示创建会话的表单，上面列示出人员列表，可以选择人员
  showCreateConversationWithPersonsDialog: function(personList) {
    if (personList && personList.length > 0) {
      var cssPath = this.path + "default/style.css";
      var choosePersonHtmlPath = this.path + "default/choosePerson.html";
      this.app.content.loadAll({
        "css": [cssPath],
        "html": choosePersonHtmlPath,
      }, { "bind": { "lp": this.lp, "data": {} }, "module": this }, function(){
          //载入完成后的回调
          // 人员列表
         for (let index = 0; index < personList.length; index++) {
            const person = personList[index];
            // <div class="person-tag person-tag-active">了国栋</div>
            var personTag = new Element("div", {"class": "person-tag person-tag-active"}).inject(this.personListNode);
            var name = person;
            if (person.indexOf("@") != -1) {
              name = name.substring(0, person.indexOf("@"));
            }
            personTag.set("text", name);
            personTag.store("person", person);
            personTag.addEvents({
              "click": function(e){
                if (e.target.get("class") == "person-tag person-tag-active") {
                  e.target.set("class", "person-tag");
                } else {
                  e.target.set("class", "person-tag person-tag-active");
                }
              }
            });
         }
         // 创建会话按钮
         this.personChooseCreateNode.addEvents({
           "click": function(e){
             var personList = []
              this.personListNode.getChildren().each(function(tag){
                if (tag.get("class") == "person-tag person-tag-active") {
                  personList.push(tag.retrieve("person"));
                }
              });
              if (personList.length > 0) {
                this.newConversation(personList);
                this.closeChoosePersonDialog();
              } else {
                this.app.notice(this.lp.msgNeedChoosePerson, "error");
              }
           }.bind(this)
         });
         // 关闭按钮
         this.personChooseCloseBtnNode.addEvents({
           "click": function(e){
              this.closeChoosePersonDialog();
           }.bind(this)
         })
      }.bind(this));

    } else {
      this.app.notice(this.lp.msgNoBusinessPerson, "error");
    }
  },
  // 关闭人员选择
  closeChoosePersonDialog: function(){
    if (this.personChooseDialogNode) {
      this.personChooseDialogNode.destroy();
      this.personChooseDialogNode = null;
    }
    if (this.app.refreshAll) this.app.refreshAll();
  },

  // 根据jobid查询review，获取所有相关的人员
  getProcessReviewByJobId: function() {
    o2.Actions.load("x_processplatform_assemble_surface").ReviewAction.listWithJob(this.options.businessId, function(json){
      if (json.data && json.data.length > 0) {
        var personList = [];
        json.data.each(function(review){
          if (review.person && review.person !== layout.session.user.distinguishedName) {
            personList.push(review.person);
          }
        });
        this.showCreateConversationWithPersonsDialog(personList);
      } else {
        this.showCreateConversationWithPersonsDialog([]);
      }
    }.bind(this), function(error){
      console.log(error);
      this.showCreateConversationWithPersonsDialog([]);
    }.bind(this));
  }


});



//创建聊天 弹出窗表单
MWF.xApplication.IMV2.Starter.CreateConversationForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		"style": "minder",
		"width": 700,
		"height": "200",
		"hasTop": true,
		"hasIcon": false,
		"draggable": true,
		"title": MWF.xApplication.IMV2.LP.createConversation,
		"includePersonList": [], // 在规定范围内的人员进行选择, 如果为空，则不限制
		"personSelected": []
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
				person: { 
          text: MWF.xApplication.IMV2.LP.selectPerson, 
          type: "org", 
          orgType: "person", 
          notEmpty: true, 
          exclude: exclude, 
          count: 0, // 可选人数 0是不限制人数
          value: this.options["personSelected"] },
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
			this.explorer.newConversation(data.person);
			this.close();
		}
	}
});




/**
 * 分享消息、转发消息
 */
MWF.xApplication.IMV2.ShareToConversation = new Class({
  Extends: MWF.widget.Common,
  Implements: [Options, Events],
  options: {
    "style": "default",
    "businessType" : "none", // 业务类型 :none, process, cms  。启动聊天 是否带业务类型
    "businessId": null, // 业务ID 新创建为空
    "includePersonList": [], // 在规定范围内的人员进行选择, 如果为空，则不限制
    "conversationId": null, // 传入的会话id, 新创建为空
		"mode": "default" // 展现模式：default onlyChat 。 onlyChat的模式需要传入conversationId 会打开这个会话的聊天窗口并隐藏左边的会话列表
  },
  initialize: function(data, app, options){
    console.log("init   IMV2.Starter " );
    this.setOptions(options);
    this.path = "../x_component_IMV2/$Main/";
    MWF.xDesktop.requireApp("IMV2", "lp."+o2.language, null, false);
    this.lp = MWF.xApplication.IMV2.LP;
    this.data = data; // msgBody 消息体
    this.app = app;
    
  },
  load: function(){
    console.log("init   IMV2.ShareToConversation    " );
    debugger;
    if (this.data && this.data.msgBody) {
      console.log(this.data.msgBody );
      this.openConversationListDialog();
    } else {
      this.app.notice(this.lp.msgShareNoBody, "error");
    }
  },
  // 打开窗口
  openConversationListDialog: function() {
    var cssPath = this.path + "default/style.css";
      var choosePersonHtmlPath = this.path + "default/shareToConversation.html";
      this.app.content.loadAll({
        "css": [cssPath],
        "html": choosePersonHtmlPath,
      }, { "bind": { "lp": this.lp, "data": {} }, "module": this }, function() {
        this.conversationChooseCloseBtnNode.addEvents({
          "click": function(){this.closeConversationListDialog()}.bind(this)
        });
        this.shareSearchInputNode.addEvent("keyup", function (e){
            if (e.code === 13) {
                e.stopPropagation();
                var searchInputVal = this.shareSearchInputNode.get("value");
                console.log('搜索内容: '+searchInputVal)
                this.searchConversationAndPerson(searchInputVal);
            }
        }.bind(this));
        this.loadConversationList();
      }.bind(this));
  },
  // 关闭窗口
  closeConversationListDialog: function() {
    if (this.conversationChooseDialogNode) {
      this.conversationChooseDialogNode.destroy();
      this.conversationChooseDialogNode = null;
    }
    if (this.app.refreshAll) this.app.refreshAll();
  },
  // 加载会话列表
  loadConversationList: function() {
    o2.Actions.load("x_message_assemble_communicate").ImAction.myConversationList(function (json) {
			if (json.data && json.data instanceof Array) {
                this.conversationList = json.data; // 会话数据
                this.conversationNodeItemList = [];
        for (var i = 0; i < this.conversationList.length; i++) {
          var conversation = this.conversationList[i];
          var itemNode = this._createConvItemNode(conversation);
          this.conversationNodeItemList.push(itemNode);
        }
			}
		}.bind(this));
  },
    // 搜索会话
    searchConversationAndPerson: function(value) {
        this.conversationListNode.empty();
        this.searchPersonListNode.empty();
        this.searchPersonTitleNode.classList.remove("block");
        this.searchPersonTitleNode.classList.add("none");
        this.conversationNodeItemList = [];
        this.personNodeItemList = [];
        var searchConvList = [];
        if (value) {
            // 搜索会话
            searchConvList = this.conversationList.filter((c) => c.title.indexOf(value) > -1 );
            //搜索人员
            this.searchLoadPerson(value);
        } else {
            // 还原会话列表
            searchConvList = this.conversationList;
        }
        // 会话列表创建
        for (var i = 0; i < searchConvList.length; i++) {
            var conversation = searchConvList[i];
            var itemNode = this._createConvItemNode(conversation);
            this.conversationNodeItemList.push(itemNode);
        }

    },
    searchLoadPerson: function (value) {
        var body = {"key": value};
        this.searchPersonList = [];
        o2.Actions.load("x_organization_assemble_control").PersonAction.listFilterPaging(1, 20, body, function (json) {
                if (json.data && json.data instanceof Array) {
                    this.searchPersonList = json.data || [];
                    this.personNodeItemList = [];
                    this.searchPersonTitleNode.classList.remove("none");
                    this.searchPersonTitleNode.classList.add("block");
                    for (var i = 0; i < this.searchPersonList.length; i++) {
                        var person = this.searchPersonList[i];
                        var personItemNode = this._createPersonItemNode(person);
                        this.personNodeItemList.push(personItemNode);
                    }
                }
        }.bind(this));
    },
    _createPersonItemNode: function (person) {
        var avatarDefault = this._getIcon(person.distinguishedName);
        var itemNode = new Element("div", { "class": "item" }).inject(this.searchPersonListNode);
        var nodeBaseItem = new Element("div", { "class": "base" }).inject(itemNode);
        var avatarNode = new Element("div", { "class": "avatar" }).inject(nodeBaseItem);
        new Element("img", { "src": avatarDefault, "class": "img" }).inject(avatarNode);
        new Element("div", { "class": "body" , "text": person.name }).inject(nodeBaseItem);
        itemNode.store("person", person);
        itemNode.addEvents({
            "click": function() {
                this.clickPersonItem(person)
            }.bind(this)
        })
        return itemNode;
    },
    clickPersonItem: function (person) {
        console.log('点击人员', person);
        MWF.require("MWF.widget.Mask", function () {
                this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
                this.mask.loadNode(this.app.content);
                var conv = {
                    type: "single",
                    personList: [person.distinguishedName],
                };
                o2.Actions.load("x_message_assemble_communicate").ImAction.create(conv, function (json) {
                    var newConv = json.data;
                    this.clickConversationItem(newConv);
                    if (this.mask) { this.mask.hide(); this.mask = null; }
                }.bind(this), function (error) {
                    console.error(error);
                    if (this.mask) { this.mask.hide(); this.mask = null; }
                }.bind(this));
        }.bind(this));

    },
  	//用户头像
	_getIcon: function (id) {
		var orgAction = MWF.Actions.get("x_organization_assemble_control")
		var url = (id) ? orgAction.getPersonIcon(id) : "../x_component_IMV2/$Main/default/icons/group.png";
		return url + "?" + (new Date().getTime());
	},
  // 创建会话Node
  _createConvItemNode: function(conversation) {
    var avatarDefault = this._getIcon();
		var convData = {
			"id": conversation.id,
			"avatarUrl": avatarDefault,
			"title": conversation.title,
		};
		var distinguishedName = layout.session.user.distinguishedName;
		if (conversation.type && conversation.type === "single") {
			var chatPerson = "";
			if (conversation.personList && conversation.personList instanceof Array) {
				for (var j = 0; j < conversation.personList.length; j++) {
					var person = conversation.personList[j];
					if (person !== distinguishedName) {
						chatPerson = person;
					}
				}
			}
			convData.avatarUrl = this._getIcon(chatPerson);
			var name = chatPerson;
			if (chatPerson.indexOf("@") != -1) {
				name = name.substring(0, chatPerson.indexOf("@"));
			}
			convData.title = name;
		}
    var itemNode = new Element("div", { "class": "item" }).inject(this.conversationListNode);
    var nodeBaseItem = new Element("div", { "class": "base" }).inject(itemNode);
		var avatarNode = new Element("div", { "class": "avatar" }).inject(nodeBaseItem);
		new Element("img", { "src": convData.avatarUrl, "class": "img" }).inject(avatarNode);
		new Element("div", { "class": "body" , "text": convData.title }).inject(nodeBaseItem);
    itemNode.store("conversation", conversation);
    itemNode.addEvents({
      "click": function() {
        this.clickConversationItem(conversation)
      }.bind(this)
    })
    return itemNode;
  },
  // 点击会话
  clickConversationItem: function(conversation) {
    console.log(conversation);
    if (this.data.callback ) { // 选择器
        this.data.callback(conversation)
    } else {
        var distinguishedName = layout.session.user.distinguishedName;
        var time = this._currentTime();
        var bodyJson = JSON.stringify(this.data.msgBody);
        var uuid = new MWF.widget.UUID().createTrueUUID();
        var textMessage = {
            "id": uuid,
            "conversationId": conversation.id,
            "body": bodyJson,
            "createPerson": distinguishedName,
            "createTime": time,
            "sendStatus": 1
        };
        o2.Actions.load("x_message_assemble_communicate").ImAction.msgCreate(textMessage,
            function (json) {
                var options = {
                    conversationId: conversation.id,
                    mode: this.options.mode || "default"
                }
                layout.openApplication(null, "IMV2", options);
            }.bind(this),
            function (error) {
                console.log(error);
                this.app.notice(this.lp.msgShareError, "error");
            }.bind(this));
    }

    this.closeConversationListDialog();
  },
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