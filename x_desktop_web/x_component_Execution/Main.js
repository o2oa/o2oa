MWF.xApplication.Execution = MWF.xApplication.Execution || {};
MWF.require("MWF.widget.Identity", null,false);
MWF.xDesktop.requireApp("Execution", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xApplication.Execution.options = {
	multitask: false,
	executable: true
}
MWF.xApplication.Execution.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Execution",
		"icon": "icon.png",
		"width": "1270",
		"height": "700",
		"isResize": false,
		"isMax": true,
		"title": MWF.xApplication.Execution.LP.title
	},
	onQueryClose:function(){
		this.logout();
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Execution.LP;
	},
	loadApplication: function(callback){
		this.user = layout.desktop.session.user.name;
		this.loginUser = layout.desktop.session.user.name;
		this.userGender = layout.desktop.session.user.genderType;
		this.department="";
		this.trueUserName = this.user
		this.restActions = new MWF.xApplication.Execution.Actions.RestActions();
		this.okrSystemAdmin = false;

		if(!this.workTabSwitch){
			var switchData = {};
			switchData.configCode = "INDEX_WORK_STATUSLIST";
			this.restActions.getProfileByCode(switchData,function(json){
				if(json.type == "success"){
					if(json.data && json.data.configValue){
						this.workTabSwitch = json.data.configValue;
					}
				}

				//this.reportAuditLeader
			}.bind(this),null,false);
		}
		if(!this.statSwitch){
			var switchData = {};
			switchData.configCode = "INDEX_STATISTIC_TYPE";
			this.restActions.getProfileByCode(switchData,function(json){
				if(json.type == "success"){
					if(json.data && json.data.configValue){
						this.statSwitch = json.data.configValue;
					}
				}

				//this.reportAuditLeader
			}.bind(this),null,false);
		}
		if(!this.companyAdmin){
			var switchData = {};
			switchData.configCode = "COMPANY_WORK_ADMIN";
			this.restActions.getProfileByCode(switchData,function(json){
				if(json.type == "success"){
					if(json.data && json.data.configValue){
						this.companyAdmin = json.data.configValue;
					}
				}

				//this.reportAuditLeader
			}.bind(this),null,false);
		}

		this.identites = this.getIdentityies();
		if( this.status && this.status.identity ){
			this.restActions.login( { "loginIdentity" : this.status.identity },function(json){
				if(json.data && json.data.okrSystemAdmin) this.okrSystemAdmin = json.data.okrSystemAdmin
				this._loadApplication( null, this.status.identity );
			}.bind(this), null, false);
		}else{
			if( this.identites.length > 1 ){
				var selector = new MWF.xApplication.Execution.IdenitySelector(this, this.restActions, this.identites, this.css, {
					closeText : this.lp.close,
					onPostSelectorClose : function(){ this.close() }.bind(this),
					onPostSelectorOk : function( identity ){
						this._loadApplication( null, identity);
					}.bind(this)
				});
				selector.load();
			}else if( this.identites.length = 1 ){
				this.restActions.login( { "loginIdentity" : this.identites[0] },function(json){
					if(json.data) this.okrSystemAdmin = json.data.okrSystemAdmin
					this._loadApplication( null, this.identites[0] );
				}.bind(this), null, false);
			}else{
				this.notice(this.app.lp.noIdentityNotice, "error");
				this.close();
			}
		}
	},
	_loadApplication: function(callback, identity ){
		this.identity = identity;
		this.getOrgAction(function(){
			this.orgAction.getDepartmentByIdentity(function(json){
				if(json.data && json.data.name){
					this.department = json.data.name
				}
			}.bind(this),null,this.identity,false)

			this.orgAction.getPersonByIdentity(function(json){
				if(json.data &&　json.data.name){
					this.trueUserName = json.data.name;
					this.user = json.data.name;
				}
			}.bind(this),null,this.identity,false)
		}.bind(this))

		this.createContainer();
		this.createTopBarVersion();
		this.createMiddleContent();

		if( this.status && this.status.navi1 == "work") {
			if (this.status.navi2 == "task") {
				this.openTask( this.status.navi3 , this.status.navi4 )
			} else if ( this.status.navi2 == "report") {
				this.openWorkReport( this.status.navi3 )
			} else {
				this.createLayout();
			}
		}else if( this.status && this.status.navi1 == "xmind") {
			this.openContent("xmind")
		}else if( this.status && this.status.navi1 == "setting"){
			this.openContent("setting")
		}else{
			this.createLayout();
		}

		this.addEvent("resize", function(){
			var size = this.middleContent.getSize();
			var y = size.y-300;
			var x = size.x-450;
			if(this.todoList)this.todoList.setStyles({"height":y+"px"});
			if(this.workConditionContentDiv)this.workConditionContentDiv.setStyles({"height":y+"px"});
			if(this.leftContent)this.leftContent.setStyles({"width":x+"px"});
		}.bind(this));
	},



//=======================================================================================================================================================================

	//*************************************************************************common-function BEGIN******************************************************************************
	logout: function(callback){
		var logoutData = {};
		this.restActions.logout( logoutData,function(json){

		}.bind(this), function(xhr,text,error){

		}.bind(this), false);
	},
	getIdentityies : function(){
		var identites = [];
		this.getOrgAction(function(){
			this.orgAction.listIdentityByPerson(function(json){
				json.data.each(function(d){
					identites.push( d.name );
				})
			}.bind(this), null, this.user,false)
		}.bind(this))
		this.restActions.listMyRelief(function(json){
			if( json.data ){
				json.data.each(function(d){
					identites.push( d.leaderIdentity );
				})
			}
		}.bind(this),null, false)
		return identites;
	},
	resizeContent : function(){
		var size = this.middleContent.getSize();
		var y = size.y-300;
		var x = size.x-450;
		if(this.todoList)this.todoList.setStyles({"height":y+"px"});
		if(this.workConditionContentDiv)this.workConditionContentDiv.setStyles({"height":y+"px"});
		if(this.leftContent)this.leftContent.setStyles({"width":x+"px"});
	},
	getOrgAction: function(callback){
		if (!this.orgAction){
			MWF.require("MWF.xAction.org.express.RestActions", function(){
				this.orgAction = new MWF.xAction.org.express.RestActions();
				if (callback) callback();
			}.bind(this),false);
		}else{
			if (callback) callback();
		}
	},
	changeIdentity: function(){
		this.identitySelector = new MWF.xApplication.Execution.IdenitySelector(this, this.restActions, this.identites, this.css, {
			closeText : this.lp.cancel,
			needLogout : true,
			onPostSelectorClose : function(){ this.identitySelector.close() }.bind(this),
			onPostSelectorOk : function( identity ){
				this.status = null;
				this._loadApplication( null, identity);
			}.bind(this)
		});
		this.identitySelector.load();
	},
	recordStatus: function(){
		var status = {
			identity : this.identity,
			navi1 : this.currentTopBarTab, //顶部的导航条
			navi2 : this.navi2	//当前打开的是任务还是汇报
		};
		if( status.navi1 == "work" ){
			if( status.navi2 == "task" ){
				status.navi3 = this.workTask?this.workTask.workNavi1:"";
				status.navi4 = this.workTask?this.workTask.workNavi2:"";
			}else if( status.navi2 == "report" ){
				status.navi3 = this.workReportList.workNavi1;
				status.navi4 = "";
			}else{
				status.navi3 = "";
				status.navi4 = "";
			}
		}
		return status;
	},
	openContent : function( str ){
		if( str == "xmind" ){
			this.currentTopBarTab = "xmind";
			this.loadTopBarTab(this.currentTopBarTab)
			this.middleContent.empty();
			MWF.xDesktop.requireApp("Execution", "MinderExplorer", null, false);
			var explorer = new MWF.xApplication.Execution.MinderExplorer(this.middleContent, this, this.restActions, {});
			explorer.load();
		}else if( str == "setting" ){
			this.currentTopBarTab = "setting";
			this.loadTopBarTab(this.currentTopBarTab)
			this.middleContent.empty();
			MWF.xDesktop.requireApp("Execution", "SettingExplorer", function(){
				var explorer = new MWF.xApplication.Execution.SettingExplorer(this.middleContent, this, this.restActions, {});
				explorer.load();
			}.bind(this), false);
		}else{
			this.currentTopBarTab = "work";
			this.loadTopBarTab(this.currentTopBarTab)
			this.middleContent.empty();
			this.createLayout();
		}
	},
	openTask: function(workNavi1, workNavi2){
		this.navi2 = "task";
		this.middleContent.empty();
		//MWF.xDesktop.requireApp("Execution", "WorkTask", function(){
		//	this.workTask = new MWF.xApplication.Execution.WorkTask(this.middleContent,this,this.restActions,{
		//		"workNavi1" : workNavi1 || "",
		//		"workNavi2" : workNavi2 || ""
		//	})
		//	this.workTask.load();
		//}.bind(this))
		MWF.xDesktop.requireApp("Execution", "WorkList", function(){
			this.workList = new MWF.xApplication.Execution.WorkList(this.middleContent,this,this.restActions,{
				"workNavi1" : workNavi1 || "",
				"workNavi2" : workNavi2 || ""
			})
			this.workList.load();
		}.bind(this))
	},
	openWorkReport: function(workNavi1){
		this.navi2 = "report";
		this.middleContent.empty();
		MWF.xDesktop.requireApp("Execution", "WorkReportList", function(){
			this.workReportList = new MWF.xApplication.Execution.WorkReportList(this.middleContent,this,this.restActions,{
				"workNavi1" : workNavi1 || ""
			})
			this.workReportList.load();
		}.bind(this))
	},
	openStat: function(){
		this.navi2 = "stat"
		this.middleContent.empty();

		MWF.xDesktop.requireApp("Execution", "WorkStat", function(){
			this.workStat = new MWF.xApplication.Execution.WorkStat(this.middleContent,this,this.restActions,{

			})
			this.workStat.load();
		}.bind(this))
	},
	//**********************************************************common-function END******************************************************************************


	//**********************************************************section-layout BEGIN******************************************************************************
		createContainer: function(){
			if( !this.container ){
				this.content.setStyle("overflow", "hidden");
				this.container = new Element("div", {
					"styles": this.css.container
				}).inject(this.content);
			}
		},
		createMiddleContent: function(){
			if( !this.middleContent ){
				this.middleContent = new Element("div.middleContent",{
					"styles": this.css.middleContent
				}).inject(this.container)
			}
		},
		createLayout: function(){
			this.navi2 = "main";
			if( this.middleContent )this.middleContent.empty();
			this.leftContent = new Element("div.leftContent", {"styles":this.css.leftContent}).inject(this.middleContent);

			this.leftTopContent = new Element("div",{"styles":this.css.leftTopContent}).inject(this.leftContent);
			this.clearDiv = new Element("div",{"styles":this.css.clearDiv}).inject(this.leftContent);
			//左上
			this.createCategoryIconsVersion();
			this.leftBottomContent = new Element("div",{"styles":this.css.leftBottomContent}).inject(this.leftContent);
			//左下
			this.createTodoContentVersion();

			this.rightContent = new Element("div.rightContent", {"styles": this.css.rightContent}).inject(this.middleContent);
			//右上
			//this.createContentDivVersion();
			this.createMyStatVersion();

			this.rightBottomContent = new Element("div.rightBottomContent",{"styles":this.css.rightBottomContent}).inject(this.rightContent);
			this.rightBottomContent.setStyle("display",""); //工作动态开关
			//右下
			this.createRightBottomVersion();

			this.resizeContent();
		},

	//*****************************top-layout BEGIN*************************************
		createTopBarVersion: function(){
			this.createTopBar();
		},
		createTopBar: function(){
			this.currentTopBarTab = "work";

			if( this.topBar ){
				this.topBar.empty();
			}else{
				this.topBar = new Element("div.topBar", {
					"styles": this.css.topBar
				}).inject(this.container);
			}

			this.topBarContent = new Element("div", {"styles": this.css.topBarContent}).inject(this.topBar);
			this.topBarTitleLi = new Element("li", {"styles": this.css.topBarTitleLi}).inject(this.topBarContent);
			this.topBarLog = new Element("img",{"styles": this.css.topBarLog,"src": this.path+"default/icon/okr.png"}).inject(this.topBarTitleLi);
			this.topBarTitleSpan = new Element("span",{	"styles": this.css.topBarTitleSpan,"text":MWF.xApplication.Execution.LP.topBar.title}).inject(this.topBarTitleLi);
			//top我的工作台
			this.topBarWorkLi = new Element("li.topBarWorkLi",{"styles": this.css.topBarCurrentLi}).inject(this.topBarContent)
			this.topBarWorkLi.addEvents({
				"mouseover":function(){
					if(this.currentTopBarTab!="work")this.topBarWorkLi.setStyles({"background-color":"#124c93"})
				}.bind(this),
				"mouseout":function(){
					if(this.currentTopBarTab!="work")this.topBarWorkLi.setStyles({"background-color":"#5c97e1"})
				}.bind(this),
				"click" : function(){
					this.openContent( "work" );
				}.bind(this)
			})
			this.topBarWorkImg = new Element("img",{"styles": this.css.topBarWorkImg,"src": this.path+"default/icon/Outline-104.png"}).inject(this.topBarWorkLi);
			this.topBarWorkSpan = new Element("span",{"styles": this.css.topBarWorkSpan,"text":MWF.xApplication.Execution.LP.topBar.work}).inject(this.topBarWorkLi);
			//top脑图展示
			this.topBarXmindLi = new Element("li.topBarXmindLi",{"styles": this.css.topBarLi}).inject(this.topBarContent);
			this.topBarXmindImg = new Element("img",{"styles": this.css.topBarXmindImg,"src": this.path+"default/icon/MindMapFilled-100.png"}).inject(this.topBarXmindLi);
			this.topBarXmindSpan = new Element("span",{"styles": this.css.topBarXmindSpan,"text":MWF.xApplication.Execution.LP.topBar.xmind}).inject(this.topBarXmindLi);
			this.topBarXmindLi.addEvents({
				"mouseover":function(){
					if(this.currentTopBarTab!="xmind")this.topBarXmindLi.setStyles({"background-color":"#124c93"})
				}.bind(this),
				"mouseout":function(){
					if(this.currentTopBarTab!="xmind")this.topBarXmindLi.setStyles({"background-color":"#5c97e1"})
				}.bind(this),
				"click" : function(){
					this.openContent( "xmind" );
				}.bind(this)
			});
			//top系统配置
			this.topBarSettingLi = new Element("li.topBarSettingLi",{"styles": this.css.topBarLi}).inject(this.topBarContent);
			this.topBarSettingImg = new Element("img",{"styles": this.css.topBarXmindImg,"src": this.path+"default/icon/Maintenance-96.png"}).inject(this.topBarSettingLi);
			this.topBarSettingSpan = new Element("span",{"styles": this.css.topBarXmindSpan,"text":MWF.xApplication.Execution.LP.topBar.setting}).inject(this.topBarSettingLi);
			this.topBarSettingLi.addEvents({
				"mouseover":function(){
					if(this.currentTopBarTab!="setting")this.topBarSettingLi.setStyles({"background-color":"#124c93"})
				}.bind(this),
				"mouseout":function(){
					if(this.currentTopBarTab!="setting")this.topBarSettingLi.setStyles({"background-color":"#5c97e1"})
				}.bind(this),
				"click" : function(){
					this.openContent( "setting" );
				}.bind(this)
			});
			if(!this.okrSystemAdmin) this.topBarSettingLi.setStyle("display","none")

			//top-right
			this.topBarRight = new Element("div.toBarRight",{"styles":this.css.topBarRight}).inject(this.topBar);
			//部门
			this.topBarRightDeptLi = new Element("li",{"styles":this.css.topBarRightDeptLi}).inject(this.topBarRight);
			this.topBarRightDeptImg = new Element("img",{"styles":this.css.topBarRightDeptImg,"src":this.path+"default/icon/Home-96.png"}).inject(this.topBarRightDeptLi);
			this.topBarRightDeptSpan = new Element("span",{"styles":this.css.topBarRightDeptSpan,"text":this.department}).inject(this.topBarRightDeptLi);
			//用户
			if(this.userGender=="f") this.userFace = this.path+"default/icon/UserFemale-104.png"
			else this.userFace = this.path+"default/icon/UserMale-104.png"
			this.topBarRightPersonLi = new Element("li",{"styles":this.css.topBarRightPersonLi}).inject(this.topBarRight);
			this.topBarRightPersonImg = new Element("img",{"styles":this.css.topBarRightPersonImg,"src":this.userFace}).inject(this.topBarRightPersonLi);
			this.topBarRightPersonSpan = new Element("span",{"styles":this.css.topBarRightPersonSpan,"text":this.trueUserName}).inject(this.topBarRightPersonLi);

			//***************change user**********************
			if( this.identites.length > 1 ){
				this.topBarRightChangeUserLi = new Element("li",{
					"styles":this.css.topBarRightOutLi
				}).inject(this.topBarRight).addEvents({
						"click":function(){
							this.changeIdentity();
						}.bind(this)
					});
				this.topBarRightChangeUserImg = new Element("img",{
					"styles":this.css.topBarRightOutImg,
					"src":this.path+"default/icon/ChangeUser.png"
				}).inject(this.topBarRightChangeUserLi);
				this.topBarRightChangeUserSpan = new Element("span",{
					"styles":this.css.topBarRightOutSpan,
					"text":MWF.xApplication.Execution.LP.changeUser
				}).inject(this.topBarRightChangeUserLi);
			}
			//***************change user**********************

			this.topBarRightLastLi = new Element("li",{"styles":this.css.topBarRightLastLi}).inject(this.topBarRight);
			this.topBarRightLastImg = new Element("img",{"styles":this.css.topBarRightLastImg,"src":this.path+"default/icon/BulletedListFilled-100.png"}).inject(this.topBarRightLastLi);
		},

	//*****************************top-layout END*************************************

	//*****************************left-top-layout BEGIN*************************************
		createCategoryIconsVersion: function(){
			this.createCategoryIcons();
		},
		createCategoryIcons: function(){
			this.categoryIconsUl = new Element("ul",{"styles": this.css.categoryIconsUl}).inject(this.leftTopContent);
			this.createCategoryNode(this.path+"default/icon/Checklist-100.png",MWF.xApplication.Execution.LP.categoryIcon.span1,"categoryIconsLi");
			this.createCategoryNode(this.path+"default/icon/ConferenceCall-104.png",MWF.xApplication.Execution.LP.categoryIcon.span2,"categoryIconsLi");
			//this.createCategoryNode(this.path+"default/icon/QuestionMark-104.png",MWF.xApplication.Execution.LP.categoryIcon.span3,"categoryIconsLi");
			//this.createCategoryNode(this.path+"default/icon/Collaboration-104.png",MWF.xApplication.Execution.LP.categoryIcon.span4,"categoryIconsLi");

			if(this.okrSystemAdmin || this.companyAdmin.indexOf(this.identity)>-1){
				this.createCategoryNode(this.path+"default/icon/AreaChart Filled-100.png",MWF.xApplication.Execution.LP.categoryIcon.span5,"categoryIconsLiRight");
			}

		},
	//*****************************left-top layout*************************************

	//*****************************left-bottom layout BEGIN*************************************
		createTodoContentVersion: function(){
			this.createTodoContent2()
		},
		createTodoContent2: function(){
			this.todoMenuDiv = new Element("div",{"styles": this.css.todoMenuDiv}).inject(this.leftBottomContent);
			this.todoMenuLeftDiv = new Element("div",{"styles":this.css.todoMenuLeftDiv}).inject(this.todoMenuDiv);
			this.todoMenuLeftImg = new Element("img",{"styles": this.css.todoMenuLeftImg,"src": this.path+"default/icon/Outline-104.png"}).inject(this.todoMenuLeftDiv);
			this.todoMenuLeftSpan = new Element("span",{"styles": this.css.todoMenuLeftSpan,"text":MWF.xApplication.Execution.LP.todoMenu.title}).inject(this.todoMenuLeftDiv);

			this.todoMenuRightDiv = new Element("div.todoMenuRightDiv",{"styles":this.css.todoMenuRightDiv}).inject(this.todoMenuDiv);
			//right category
			//this.todoMenuRightDiv.setStyle("display","none");
			//this.todoMenuRightLi = new Element("li",{"styles":this.css.todoMenuRightCurrentLi,"text":MWF.xApplication.Execution.LP.todoMenu.all}).inject(this.todoMenuRightDiv);
			//this.todoMenuRightLi = new Element("li",{"styles":this.css.todoMenuRightLi,"text":MWF.xApplication.Execution.LP.todoMenu.workTask}).inject(this.todoMenuRightDiv);
			//this.todoMenuRightLi = new Element("li",{"styles":this.css.todoMenuRightLi,"text":MWF.xApplication.Execution.LP.todoMenu.workReport}).inject(this.todoMenuRightDiv);
			//this.todoMenuRightLi = new Element("li",{"styles":this.css.todoMenuRightLiLast,"text":MWF.xApplication.Execution.LP.todoMenu.workProblem}).inject(this.todoMenuRightDiv);
			this.todoMenuDo = new Element("li.todoMenuDo",{"styles":this.css.todoMenuDo,"text":this.lp.main.todoMenuDo}).inject(this.todoMenuRightDiv);
			this.todoMenuDoCount = new Element("span.todoMenuDoCount",{"styles":this.css.todoMenuDoCount}).inject(this.todoMenuDo);
			if(this.workTabSwitch && this.workTabSwitch=="OPEN"){
				this.todoMenuWork = new Element("li.todoMenuWork",{"styles":this.css.todoMenuWork,"text":this.lp.main.todoMenuWork}).inject(this.todoMenuRightDiv);
				this.todoMenuWorkCount = new Element("span.todoMenuWorkCount",{"styles":this.css.todoMenuWorkCount}).inject(this.todoMenuWork);
			}

			this.todoList = new Element("div.todoList",{"styles": this.css.todoList}).inject(this.leftBottomContent);
			this.todoListDiv = new Element("div.todoListDiv",{"styles": this.css.todoListDiv}).inject(this.todoList);
			MWF.require("MWF.widget.ScrollBar", function () {
				new MWF.widget.ScrollBar(this.todoListDiv, {
					"indent": false,
					"style": "xApp_TaskList",
					"where": "before",
					"distance": 30,
					"friction": 4,
					"axis": {"x": false, "y": true},
					"onScroll": function (y) {
						var scrollSize = this.todoListDiv.getScrollSize();
						var clientSize = this.todoListDiv.getSize();
						var scrollHeight = scrollSize.y - clientSize.y;
						var view = this.todoView || this.workView;
						if (y + 200 > scrollHeight && view && view.loadElementList) {
							if (! view.isItemsLoaded) view.loadElementList();
						}
					}.bind(this)
				});
			}.bind(this));

			_selfToDo = this
			this.todoMenuRightDiv.getElements("li").addEvents({
				"click":function(e){
					_selfToDo.changeTodoTab(this)
				}
			})
			this.getTodoCount();
			this.createTodoList();
		},
		createTodoContent: function(){
			this.todoMenuDiv = new Element("div",{"styles": this.css.todoMenuDiv}).inject(this.leftBottomContent);
			this.todoMenuLeftDiv = new Element("div",{"styles":this.css.todoMenuLeftDiv}).inject(this.todoMenuDiv);
			this.todoMenuLeftImg = new Element("img",{"styles": this.css.todoMenuLeftImg,"src": this.path+"default/icon/Outline-104.png"}).inject(this.todoMenuLeftDiv);
			this.todoMenuLeftSpan = new Element("span",{"styles": this.css.todoMenuLeftSpan,"text":MWF.xApplication.Execution.LP.todoMenu.title}).inject(this.todoMenuLeftDiv);

			this.todoMenuRightDiv = new Element("div.todoMenuRightDiv",{"styles":this.css.todoMenuRightDiv}).inject(this.todoMenuDiv);
			//right category
			this.todoMenuRightDiv.setStyle("display","none");
			this.todoMenuRightLi = new Element("li",{"styles":this.css.todoMenuRightCurrentLi,"text":MWF.xApplication.Execution.LP.todoMenu.all}).inject(this.todoMenuRightDiv);
			this.todoMenuRightLi = new Element("li",{"styles":this.css.todoMenuRightLi,"text":MWF.xApplication.Execution.LP.todoMenu.workTask}).inject(this.todoMenuRightDiv);
			this.todoMenuRightLi = new Element("li",{"styles":this.css.todoMenuRightLi,"text":MWF.xApplication.Execution.LP.todoMenu.workReport}).inject(this.todoMenuRightDiv);
			this.todoMenuRightLi = new Element("li",{"styles":this.css.todoMenuRightLiLast,"text":MWF.xApplication.Execution.LP.todoMenu.workProblem}).inject(this.todoMenuRightDiv);

			this.todoList = new Element("div.todoList",{"styles": this.css.todoList}).inject(this.leftBottomContent);
			this.todoListDiv = new Element("div.todoListDiv",{"styles": this.css.todoListDiv}).inject(this.todoList);

			MWF.require("MWF.widget.ScrollBar", function () {
				new MWF.widget.ScrollBar(this.todoListDiv, {
					"indent": false,
					"style": "xApp_TaskList",
					"where": "before",
					"distance": 30,
					"friction": 4,
					"axis": {"x": false, "y": true},
					"onScroll": function (y) {
						var scrollSize = this.todoListDiv.getScrollSize();
						var clientSize = this.todoListDiv.getSize();
						var scrollHeight = scrollSize.y - clientSize.y;
						//var view = this.todoView;
						var view = this.todoView;

						if (y + 200 > scrollHeight && view && view.loadElementList) {
							if (! view.isItemsLoaded) view.loadElementList();
						}
					}.bind(this)
				});
			}.bind(this));

			this.createTodoList();
		},

	//*****************************left-bottom-layout END*************************************

	//****************************right-top BEGIN******************************
		createMyStatVersion: function(){
			this.rightTopContent = new Element("div.rightTopContent",{"styles":this.css.rightTopContent}).inject(this.rightContent);
			if(this.statSwitch && this.statSwitch == "PROMPTNESSRATE"){
				this.createMyStat();
			}

			//this.createContentDiv();
		},
		createMyStat: function(){

			var processCount = 0;
			var overtimeCount = 0;
			var completedCount = 0;
			var percentNum = "100%"
			this.restActions.getMyStat(function(json){
				if(json.data && json.data.responProcessingWorkCount) processCount = json.data.responProcessingWorkCount
				if(json.data && json.data.overtimeResponWorkCount) overtimeCount = json.data.overtimeResponWorkCount
				if(json.data && json.data.responCompletedWorkCount) completedCount = json.data.responCompletedWorkCount
				if(json.data && json.data.percent) percentNum = (parseInt(json.data.percent)*100)
			}.bind(this),null,false)


			this.rightTopTable = new Element("table.rightTopTable",{"styles":this.css.rightTopTable,"border":"0"}).inject(this.rightTopContent);

			var tr = new Element("tr").inject(this.rightTopTable);
			var td = new Element("td",{"colspan":"4"}).inject(tr);
			this.rightTopTdImg = new Element("img",{"styles":this.css.rightTopTdImg,"src":this.path+"default/icon/PieChart-104.png"}).inject(td);
			this.rightTopTdSpan = new Element("span",{"styles":this.css.rightTopTdSpan,"text":MWF.xApplication.Execution.LP.rightTop.title}).inject(td);

			tr = new Element("tr").inject(this.rightTopTable);
			rightTopNumTd = new Element("td",{"styles":this.css.rightTopNumTd}).inject(tr);
			this.rightTopFlowSpan = new Element("span",{"styles":this.css.rightTopFlowSpan,"text":processCount}).inject(rightTopNumTd);
			rightTopNumTd = new Element("td",{"styles":this.css.rightTopNumTd}).inject(tr);
			this.rightTopCompletedSpan = new Element("span",{"styles":this.css.rightTopCompletedSpan,"text":overtimeCount}).inject(rightTopNumTd);
			rightTopNumTd = new Element("td",{"styles":this.css.rightTopNumTd}).inject(tr);
			this.rightTopOverTimeSpan = new Element("span",{"styles":this.css.rightTopOverTimeSpan,"text":completedCount}).inject(rightTopNumTd);
			this.rightTopRateTd = new Element("td",{"width":"80","align":"right"}).inject(tr);
			this.rightTopRateSpan = new Element("span",{"styles":this.css.rightTopRateSpan,"text":percentNum}).inject(this.rightTopRateTd);
			tr = new Element("tr").inject(this.rightTopTable);
			rightTopTextTd = new Element("td",{"styles":this.css.rightTopTextTd,"width":"80","align":"center","text":MWF.xApplication.Execution.LP.rightTop.flow}).inject(tr);
			rightTopTextTd = new Element("td",{"styles":this.css.rightTopTextTd,"width":"80","align":"center","text":MWF.xApplication.Execution.LP.rightTop.overTime}).inject(tr);
			rightTopTextTd = new Element("td",{"styles":this.css.rightTopTextTd,"width":"80","align":"center","text":MWF.xApplication.Execution.LP.rightTop.completed}).inject(tr);
			rightTopTextTd = new Element("td",{"styles":this.css.rightTopTextTd,"width":"80","align":"right","text":MWF.xApplication.Execution.LP.rightTop.rate}).inject(tr);


		},
		createContentDiv: function(){
			//this.rightTopContent = new Element("div.createContentDiv",{"styles":this.css.rightTopContent}).inject(this.rightContent);
			this.rightTopTable = new Element("table.rightTopTable",{"styles":this.css.rightTopTable,"border":"0"}).inject(this.rightTopContent);
			//临时
			this.rightTopTable.setStyle("display","none");
			var tr = new Element("tr").inject(this.rightTopTable);
			var td = new Element("td",{"colspan":"4"}).inject(tr);
			this.rightTopTdImg = new Element("img",{"styles":this.css.rightTopTdImg,"src":this.path+"default/icon/PieChart-104.png"}).inject(td);
			this.rightTopTdSpan = new Element("span",{"styles":this.css.rightTopTdSpan,"text":MWF.xApplication.Execution.LP.rightTop.title}).inject(td);

			tr = new Element("tr").inject(this.rightTopTable);
			rightTopNumTd = new Element("td",{"styles":this.css.rightTopNumTd}).inject(tr);
			this.rightTopFlowSpan = new Element("span",{"styles":this.css.rightTopFlowSpan,"text":"11"}).inject(rightTopNumTd);
			rightTopNumTd = new Element("td",{"styles":this.css.rightTopNumTd}).inject(tr);
			this.rightTopCompletedSpan = new Element("span",{"styles":this.css.rightTopCompletedSpan,"text":"9"}).inject(rightTopNumTd);
			rightTopNumTd = new Element("td",{"styles":this.css.rightTopNumTd}).inject(tr);
			this.rightTopOverTimeSpan = new Element("span",{"styles":this.css.rightTopOverTimeSpan,"text":"16"}).inject(rightTopNumTd);
			this.rightTopRateTd = new Element("td",{"width":"80","align":"right"}).inject(tr);
			this.rightTopRateSpan = new Element("span",{"styles":this.css.rightTopRateSpan,"text":"71.75"}).inject(this.rightTopRateTd);
			tr = new Element("tr").inject(this.rightTopTable);
			rightTopTextTd = new Element("td",{"styles":this.css.rightTopTextTd,"width":"80","align":"center","text":MWF.xApplication.Execution.LP.rightTop.flow}).inject(tr);
			rightTopTextTd = new Element("td",{"styles":this.css.rightTopTextTd,"width":"80","align":"center","text":MWF.xApplication.Execution.LP.rightTop.overTime}).inject(tr);
			rightTopTextTd = new Element("td",{"styles":this.css.rightTopTextTd,"width":"80","align":"center","text":MWF.xApplication.Execution.LP.rightTop.completed}).inject(tr);
			rightTopTextTd = new Element("td",{"styles":this.css.rightTopTextTd,"width":"80","align":"right","text":MWF.xApplication.Execution.LP.rightTop.rate}).inject(tr);
		},
	//****************************right-top END******************************

	//****************************right-bottom BEGIN******************************
		createRightBottomVersion: function(){
			this.workConditionMenuDiv = new Element("div.createRightBottom",{"styles": this.css.workConditionMenuDiv}).inject(this.rightBottomContent);
			this.createRightBottom();
		},
		createRightBottom: function(){
			this.workConditionMenuTitleDiv = new Element("div.workConditionMenuTitleDiv",{"styles":this.css.workConditionMenuTitleDiv}).inject(this.workConditionMenuDiv);
			this.workConditionMenuTitleImg = new Element("img",{"styles":this.css.workConditionMenuTitleImg,"src":this.path+"default/icon/AppointmentReminders-96.png"}).inject(this.workConditionMenuTitleDiv);
			this.workConditionMenuTitleSpan = new Element("span",{"styles":this.css.workConditionMenuTitleSpan,"text": MWF.xApplication.Execution.LP.workConditionMenuTitle}).inject(this.workConditionMenuTitleDiv);

			this.workConditionMenuMoreDiv = new Element("div",{"styles":this.css.workConditionMenuMoreDiv}).inject(this.workConditionMenuDiv);
			this.workConditionMenuMoreLi = new Element("li",{"styles":this.css.workConditionMenuMoreLi}).inject(this.workConditionMenuMoreDiv);

			this.createWorkConditionContentDiv();
		},
	//****************************right-bottom END******************************

	//*************************************************************************section-layout END*******************************************************************************


//=======================================================================================================================================================================

	//*************************************************************************section-function BEGIN*******************************************************************************

	//*****************************top-function BEGIN*************************************
	loadTopBarTab: function(str){
		if(str=="work"){
			this.topBarWorkLi.setStyles({"background-color":"#124c93"});
			this.topBarXmindLi.setStyles({"background-color":"#5c97e1"});
			this.topBarSettingLi.setStyles({"background-color":"#5c97e1"});
		}else if(str == "xmind"){
			this.topBarWorkLi.setStyles({"background-color":"#5c97e1"});
			this.topBarXmindLi.setStyles({"background-color":"#124c93"});
			this.topBarSettingLi.setStyles({"background-color":"#5c97e1"});
		}else if(str == "setting"){
			this.topBarWorkLi.setStyles({"background-color":"#5c97e1"});
			this.topBarXmindLi.setStyles({"background-color":"#5c97e1"});
			this.topBarSettingLi.setStyles({"background-color":"#124c93"});
		}
	},
	//*****************************top-function END*************************************

	//*****************************left-top function BEGIN*************************************
	createCategoryNode: function(img,name,cla){
		var categoryIcons = new Element("li.categoryIcons",{
			"styles": this.css[cla]
		}).inject(this.categoryIconsUl);
		categoryIcons.addEvents({
			"mouseover":function(){
				this.setStyles({"background-color":"#124c93"})
			},
			"mouseout":function(){
				this.setStyles({"background-color":"#407ac1"})
			},
			"click":function(){
				if(name == MWF.xApplication.Execution.LP.categoryIcon.span1){
					this.openTask();
				}else if(name == MWF.xApplication.Execution.LP.categoryIcon.span2){
					this.openWorkReport();
				}else if(name == MWF.xApplication.Execution.LP.categoryIcon.span3){

				}else if(name == MWF.xApplication.Execution.LP.categoryIcon.span4){

				}else if(name == MWF.xApplication.Execution.LP.categoryIcon.span5){
					this.openStat();
				}
			}.bind(this)
		})
		var categoryIconsImg = new Element("img.categoryIconsImg",{
			"styles": this.css.categoryIconsImg,
			"src":img
		}).inject(categoryIcons);
		var categoryIconsSpan = new Element("span.categoryIconsSpan",{
			"styles": this.css.categoryIconsSpan,
			"text":name
		}).inject(categoryIcons);

	},
	//*****************************left-top function END*************************************

	//****************************left-bottom function BEGIN***********************
		getTodoCount:function(){
			this.restActions.getBaseWorkListMyDoNext("(0)", 10, null, function (json) {
				if(this.todoMenuWorkCount && json.count) this.todoMenuWorkCount.set("text","("+json.count+")")
			}.bind(this))

		},
		changeTodoTab: function(obj){
			if(obj.className == "todoMenuDo"){
				obj.setStyles({"background-color":"#0f72c2"});
				if(this.todoMenuWork)this.todoMenuWork.setStyles({"background-color":""});
				this.createTodoList();
			}
			if(obj.className == "todoMenuWork"){
				obj.setStyles({"background-color":"#0f72c2"});
				if(this.todoMenuDo)this.todoMenuDo.setStyles({"background-color":""});
				this.createWorkList();
			}
		},
		createTodoList: function(category){
			if( this.workView )delete this.workView;
			if( this.todoListDiv ){
				this.todoListDiv.empty();
				var filter = {}
				this.todoView =  new  MWF.xApplication.Execution.TodoView(this.todoListDiv, this, this, { templateUrl : this.path+"todoList.json",category:category,filterData:filter } )
				this.todoView.load();
			}
		},
		createWorkList : function(category){
			if( this.todoView )delete this.todoView;

			if(this.todoListDiv){
				this.todoListDiv.empty();
				var filter = {};

				this.workView =  new  MWF.xApplication.Execution.WorkView(this.todoListDiv, this, this, { templateUrl : this.path+"workList.json",category:category,filterData:filter } )
				this.workView.load();
			}
		},
	//****************************left-bottom function END***********************

	//****************************right-top function BEGIN***********************

	//****************************right-top function END***********************

	//****************************right-bottom function BEGIN***********************
		createWorkConditionContentDiv: function(){
			if(this.workConditionContentDiv) this.workConditionContentDiv.destroy();
			this.workConditionContentDiv = new Element("div",{"styles": this.css.workConditionContentDiv}).inject(this.rightBottomContent);
			this.workConditionListDiv = new Element("div.workConditionListDiv",{"styles":this.css.workConditionListDiv}).inject(this.workConditionContentDiv);

			MWF.require("MWF.widget.ScrollBar", function () {
				new MWF.widget.ScrollBar(this.workConditionListDiv, {
					"indent": false,
					"style": "xApp_TaskList",
					"where": "before",
					"distance": 30,
					"friction": 4,
					"axis": {"x": false, "y": true},
					"onScroll": function (y) {
						var scrollSize = this.workConditionListDiv.getScrollSize();
						var clientSize = this.workConditionListDiv.getSize();
						var scrollHeight = scrollSize.y - clientSize.y;
						var view = this.workConditionView;

						if (y + 200 > scrollHeight && view && view.loadElementList) {
							if (! view.isItemsLoaded) {view.loadElementList();}
						}
					}.bind(this)
				});
			}.bind(this));

			if(this.workConditionListDiv)this.workConditionListDiv.empty();
			this.workConditionView =  new  MWF.xApplication.Execution.WorkConditionView(this.workConditionListDiv, this, this, { templateUrl : this.path+"workConditionList.json" } )
			this.workConditionView.load();
		}

	//****************************right-bottom function END***********************

	//*************************************************************************section function END*******************************************************************************
});


//=======================================================================================================================================================================

//***********************************************************************Class****************************************************************************************
MWF.xApplication.Execution.IdenitySelector = new Class({
	Extends: MWF.xApplication.Template.Explorer.PopupForm,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"width": "500",
		"height": "300",
		"hasTop": true,
		"hasIcon": false,
		"hasBottom": true,
		"title": "",
		"draggable": false,
		"closeAction": false,
		"closeText" : "",
		"needLogout" : false,
		"isNew": true
	},
	initialize: function (app, actions, identities, css, options) {
		this.setOptions(options);
		this.app = app;
		this.actions = this.app.restActions;
		this.css = css;
		this.options.title = this.app.lp.idenitySelectTitle;
		this.identities = identities;
		this.actions = actions;
	},
	load: function () {
		this.create();
	},
	createTopNode: function () {
		if (!this.formTopNode) {
			this.formTopNode = new Element("div.formTopNode", {
				"styles": this.css.formTopNode
			}).inject(this.formNode);

			this.formTopIconNode = new Element("div", {
				"styles": this.css.formTopIconNode
			}).inject(this.formTopNode)

			this.formTopTextNode = new Element("div", {
				"styles": this.css.formTopTextNode,
				"text": this.options.title
			}).inject(this.formTopNode)

			if (this.options.closeAction) {
				this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
				this.formTopCloseActionNode.addEvent("click", function () {
					this.close()
				}.bind(this))
			}

			this.formTopContentNode = new Element("div", {
				"styles": this.css.formTopContentNode
			}).inject(this.formTopNode)

			this._createTopContent();

		}
	},
	_createTableContent: function () {
		var table = new Element("table",{"width":"100%",border:"0",cellpadding:"5",cellspacing:"0"}).inject(this.formTableArea);
		var tr = new Element("tr").inject(table);
		var td = new Element("td",{valign:"middle"}).inject(tr);
		this.identities.each(function(id){
			var node = new Element("div", {"styles": this.css.identitySelNode, "text":id}).inject(td);
			node.store("id",id);
			node.addEvents({
				"mouseover": function(ev){
					if ( this.selectedNode != ev.target ) ev.target.setStyles(this.css.identitySelNode_over);
				}.bind(this),
				"mouseout": function(ev){
					if ( this.selectedNode != ev.target ) ev.target.setStyles(this.css.identitySelNode_out);
				}.bind(this),
				"click": function(ev){
					this.selected( ev.target );
				}.bind(this),
				"dblclick": function(ev){
					this.selectedNode = ev.target;
					this.ok();
				}.bind(this)
			});

		}.bind(this))
	},
	selected: function( node ){
		if( this.selectedNode )this.selectedNode.setStyles( this.css.identitySelNode );
		this.selectedNode = node;
		node.setStyles(this.css.identitySelNode_selected)
	},
	_createBottomContent: function () {
		this.cancelActionNode = new Element("div.formCancelActionNode", {
			"styles": this.css.formCancelActionNode,
			"text": this.options.closeText
		}).inject(this.formBottomNode);

		this.cancelActionNode.addEvent("click", function (e) {
			this.cancel(e);
		}.bind(this));

		this.okActionNode = new Element("div.formOkActionNode", {
			"styles": this.css.formOkActionNode,
			"text": this.app.lp.comfirm
		}).inject(this.formBottomNode);

		this.okActionNode.addEvent("click", function (e) {
			this.ok(e);
		}.bind(this));
	},
	cancel: function(){
		//this.app.close();
		this.fireEvent("postSelectorClose");
	},
	ok: function (data, callback) {
		if( !this.selectedNode ){
			this.app.notice(this.app.lp.idenitySelecNotice,"error");
		}else{
			var loginData = {};
			loginData.loginIdentity = this.selectedNode.retrieve("id");
			if( this.options.needLogout ){
				this.actions.logout( {},function(json){
					if(json.type && json.type =="success"){
						this.actions.login( loginData,function(js){
							if(js.data && js.data.okrSystemAdmin){ this.app.okrSystemAdmin = js.data.okrSystemAdmin}
							this.fireEvent("postSelectorOk", loginData.loginIdentity );
							this.close();
						}.bind(this), function(xhr,text,error){

						}.bind(this), false);
					}
				}.bind(this), null, false);
			}else{
				this.actions.login( loginData,function(json){
					if(json.data && json.data.okrSystemAdmin){ this.app.okrSystemAdmin = json.data.okrSystemAdmin}
					//this.app._loadApplication( null, loginData.loginIdentity );
					this.fireEvent("postSelectorOk", loginData.loginIdentity );
					this.close();
				}.bind(this), null, false);
			}
		}
	}
});

MWF.xApplication.Execution.TodoView = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexView,
	_createDocument: function(data){
		return new MWF.xApplication.Execution.TodoDocument(this.viewNode, data, this.explorer, this);
	},

	_getCurrentPageData: function(callback, count){
		var category = this.options.category;

		if (!count)count = 20;
		var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
		var filter = this.options.filterData || {};

		this.actions.getTaskListNext(id, count, filter, function (json) {
			if(this.explorer.todoMenuDoCount && json.count) this.explorer.todoMenuDoCount.set("text","("+json.count+")")
			if (callback)callback(json);
		}.bind(this),function(xhr,error,text){}.bind(this))

	},
	_openDocument: function( documentData ){
		if(documentData.dynamicObjectType && documentData.dynamicObjectType == this.lp.todo.todoCategory.report){
			MWF.xDesktop.requireApp("Execution", "WorkReport", function(){
				var data = {
					workReportId : documentData.dynamicObjectId,
					workId : documentData.workId
				}

				this.workReport = new MWF.xApplication.Execution.WorkReport(this, this.actions,data,{
					"isEdited":false,
					onReloadView : function( data ){
						this.explorer.createTodoList();
						this.explorer.createWorkConditionContentDiv();
						this.explorer.getTodoCount();
						this.explorer.resizeContent();
					}.bind(this)
				} );
				this.workReport.load();
			}.bind(this));
		}else if(documentData.dynamicObjectType && documentData.dynamicObjectType == this.lp.todo.todoCategory.deploy){
			MWF.xDesktop.requireApp("Execution", "WorkDeploy", function(){
				//var isEditedBool = (this.tabLocation == "centerDrafter" || this.tabLocation == "baseDrafter") ? true : false

				this.workDeploy = new MWF.xApplication.Execution.WorkDeploy(this, this.actions,{"id":documentData.dynamicObjectId},{
					"isEdited":false,"centerWorkId":documentData.dynamicObjectId,
					onReloadView : function( data ){
						this.explorer.createTodoList();
						this.explorer.createWorkConditionContentDiv();
						this.explorer.getTodoCount();
						this.explorer.resizeContent();
					}.bind(this)
				} );
				this.workDeploy.load();

			}.bind(this))
		}else if(documentData.dynamicObjectType && documentData.dynamicObjectType == this.lp.todo.todoCategory.gather){
			MWF.xDesktop.requireApp("Execution", "WorkGather", function(){
				var data = {
					gatherId: documentData.id,
					title: documentData.dynamicObjectTitle
				}
				this.workGather = new MWF.xApplication.Execution.WorkGather(this, this.actions,data,{
					onReloadView : function( data ){
						this.explorer.createTodoList();
						this.explorer.createWorkConditionContentDiv();
						this.explorer.getTodoCount();
						this.explorer.resizeContent();
					}.bind(this)
				} );
				this.workGather.load();

			}.bind(this))
		}else if(documentData.dynamicObjectType && documentData.dynamicObjectType == this.lp.todo.todoCategory.readReport){
			MWF.xDesktop.requireApp("Execution", "WorkReport", function(){
				var data = {
					todoId : documentData.id,
					workReportId : documentData.dynamicObjectId,
					workId : documentData.workId
				}

				this.workReport = new MWF.xApplication.Execution.WorkReport(this, this.actions,data,{
					"isEdited":false,
					"isRead" : true,
					onReloadView : function( data ){
						this.explorer.createTodoList();
						this.explorer.createWorkConditionContentDiv();
						this.explorer.getTodoCount();
						this.explorer.resizeContent();
					}.bind(this)
				} );
				this.workReport.load();
			}.bind(this));
		}else if(documentData.dynamicObjectType && documentData.dynamicObjectType == this.lp.todo.todoCategory.reportDrafter){
			MWF.xDesktop.requireApp("Execution", "WorkReport", function(){
				var data = {
					todoId : documentData.id,
					workReportId : documentData.dynamicObjectId,
					workId : documentData.workId
				}

				this.workReport = new MWF.xApplication.Execution.WorkReport(this, this.actions,data,{
					"isEdited":false,
					onReloadView : function( data ){
						this.explorer.createTodoList();
						this.explorer.createWorkConditionContentDiv();
						this.explorer.getTodoCount();
						this.explorer.resizeContent();
					}.bind(this)
				} );
				this.workReport.load();
			}.bind(this));
		}
	}

})

MWF.xApplication.Execution.TodoDocument = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexDocument
})

MWF.xApplication.Execution.WorkView = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexView,
	_createDocument: function(data){
		return new MWF.xApplication.Execution.WorkDocument(this.viewNode, data, this.explorer, this);
	},

	_getCurrentPageData: function(callback, count){
		var category = this.options.category;

		if (!count)count = 20;
		var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
		var filter = this.options.filterData || {};

		this.actions.getBaseWorkListMyDoNext(id, count, filter, function (json) {
			if(this.explorer.todoMenuWorkCount && json.count) this.explorer.todoMenuWorkCount.set("text","("+json.count+")")
			if (callback)callback(json);
		}.bind(this))

	},
	_openDocument: function( documentData ){
		MWF.xDesktop.requireApp("Execution", "WorkDetail", function(){
			var workform = new MWF.xApplication.Execution.WorkDetail(this, this.app.restActions,documentData,{
				"isNew": false,
				"isEdited": false
			});

			workform.load();
		}.bind(this));
	}

})

MWF.xApplication.Execution.WorkDocument = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
	_postCreateDocumentNode: function( itemNode, itemData ){
		var obj = itemNode.getElement("div[class='barPercentDiv']");
		if(obj){
			if(itemData.overallProgress){
				var _width = parseFloat(itemData.overallProgress)*100;
				_width = _width.toFixed(2);
				var bgcolor = "#0099ff";
				if(itemData.isOverTime) bgcolor = "#f00";
				obj.setStyles({"width":_width+"%","background-color":bgcolor})
				itemNode.set("title",_width+"%")
			}
		}
	}
})


MWF.xApplication.Execution.WorkConditionView = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexView,
	_createDocument: function(data){
		return new MWF.xApplication.Execution.WorkConditionDocument(this.viewNode, data, this.explorer, this);
	},
	loadElementList: function (count) {
		if (!this.isItemsLoaded) {
			if (!this.isItemLoadding) {
				this.isItemLoadding = true;
				this._getCurrentPageData(function (json) {

					var length = json.count;

					if (length <= this.items.length) {
						this.isItemsLoaded = true;
					}
					json.data.each(function (data) {
						if (!this.documents[data.id]) {
							var item = this._createDocument(data);
							this.items.push(item);
							this.documents[data.id] = item;
						}
					}.bind(this));

					this.isItemLoadding = false;

					if (this.loadItemQueue > 0) {
						this.loadItemQueue--;
						this.loadElementList();
					}
				}.bind(this), count);
			} else {
				this.loadItemQueue++;
			}
		}
	},
	parseEmotion: function( d ){
		d.content = d.content.replace(/\[emotion=(.*?)\]/g, function( a,b ){
			var emotionPath = MWF.defaultPath+"/widget/$SimpleEditor/default/img/emotion/";
			return "<img imagename='"+b+"' style='cursor:pointer;border:0;padding:2px;' " +" class='MWF_editor_emotion' src='"+ emotionPath + b + ".gif" +"'>";
		}.bind(this));
	},
	_getCurrentPageData: function(callback, count){
		var category = this.options.category;

		if (!count)count = 15;
		var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
		var filter = this.options.filterData || {};

		this.actions.getWorkConditionListNext(id, count, filter, function (json) { //alert(JSON.stringify(json))
			json.data.each(function( d ){
				this.parseEmotion( d );
			}.bind(this))
			if (callback)callback(json);
		}.bind(this))

	}

})


MWF.xApplication.Execution.WorkConditionDocument = new Class({
	Extends: MWF.xApplication.Template.Explorer.ComplexDocument
})

//**************************Class*******************************************