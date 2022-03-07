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
      var options = {
          conversationId: this.options.conversationId,
          mode: this.options.mode || "default"
      }
      layout.openApplication(null, "IMV2", options);
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
        } else {
          this.app.notice(this.lp.msgNoBusinessId, "error");
        }
        //打开工作 o2.env.form.openJob
      }
      
    }
    
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
		o2.Actions.load("x_message_assemble_communicate").ImAction.create(conv, function (json) {
			var newConv = json.data;
      var options = {
        conversationId: newConv.id,
        mode: this.options.mode || "default"
      }
      if (this.app.refreshAll) this.app.refreshAll();
      layout.openApplication(null, "IMV2", options);
		}.bind(this), function (error) {
			console.log(error);
      if (this.app.refreshAll) this.app.refreshAll();
		}.bind(this))
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
