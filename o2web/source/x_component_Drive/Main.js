MWF.xApplication.Drive.options.multitask = false;
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xApplication.Drive.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style1": "default",
		"style": "default",
		"name": "Drive",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"title": MWF.xApplication.Drive.LP.title
	},
	onQueryLoad: function(){

		this.lp = MWF.xApplication.Drive.LP;
		this.action = o2.Actions.load("x_pan_assemble_control");
		//o2.loadCss("https://at.alicdn.com/t/font_3332487_duh1y70onzl.css");
		if(layout.mobile){
			//$$("html").setStyle("font-size","13.3333333333vw");
			$$("body").setStyle("overflow","hidden");
		}
	},
	loadApplication: function(callback){
		this.currentNaviType = (this.status) ? (this.status.navi || "person") : "person";
		this.loadOfficeTool(function(){

			this.action.ConfigAction.getTopMenus().then(function(json){

				this.peronFileEnable = json.data.peronFileEnable;



				this.lp.personNavi = json.data.panMenuList[0];
				this.lp.publicNavi = json.data.panMenuList[1]

				this.action.ConfigAction.isFileManager().then(function (json){

					this.isManager = json.data.value;
					var url = this.path+this.options.style+"/view/view.html";

					this.action.ConfigAction.isZoneCreator().then(function (json){

						this.isZoneCreator = json.data.value;

						if(layout.mobile){
							url = this.path+this.options.style+"/view/view_m.html";



							this.content.loadHtml(url, {"bind": {"lp": this.lp,"data":{"isManager":this.isManager}}, "module": this}, function(){

								this.loadNavi(this.currentNaviType);
								if (callback) callback();
							}.bind(this));
						}else{

							this.content.loadHtml(url, {"bind": {"lp": this.lp,"data":{"isManager":this.isManager}}, "module": this}, function(){
								this.setLayout();

								if(!this.peronFileEnable){

									this.currentNaviType = "public";
									this.personNaviNode.hide();
								}


								this.loadNavi(this.currentNaviType);
								if (callback) callback();
							}.bind(this));

						}

					}.bind(this));
				}.bind(this));
			}.bind(this));
		}.bind(this));
	},
	loadOfficeTool : function (callback){
		this.isEnableOffice = false;
		this.action.ConfigAction.isEnableOfficePreview().then(function (json){

			if(json.data.value!==""){
				this.isEnableOffice = true;
			}
			this.officeTool = json.data.value;
			if (callback) callback();
		}.bind(this));
	},
	setLayout: function(){
		var items = this.content.getElements(".naviItem");
		items.addEvents({
			"mouseover": function(){this.addClass("naviItem_over")},
			"mouseout": function(){this.removeClass("naviItem_over")},
			"click": function(){}
		});
	},
	loadNavi : function (type,key){

		if(this.currentNavi) this.currentNavi.removeClass(layout.mobile?"m-navi-item-c":"naviItem_active");
		this.currentNavi = this[type+"NaviNode"];
		this.currentNavi.addClass(layout.mobile?"m-navi-item-c":"naviItem_active");
		this.contentAreaNode.empty();

		this.currentNaviType = type;

		var options = {
			"currentNaviType" : type
		};
		if(key){
			options.key = key;
		}
		var c = new MWF.xApplication.Drive[type.capitalize()+"Content"](this, options);
		c.load();
	},
	inputFilter: function(e){
		if (e.keyCode==13) this.doFilter();
	},
	doFilter: function(){
		var key = this.searchKeyNode.get("value");

		if(this.currentNaviType==="public"){
			this.loadNavi("public", key);
		}else {
			this.loadNavi("person", key);
		}


		this.searchKeyNode.set("value","");
	},
	recordStatus: function(){
		return {"navi": this.currentNaviType};
	}
});
MWF.xApplication.Drive.Content = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	initialize: function (app, options) {
		this.setOptions(options);
		this.app = app;
		this.content = app.contentAreaNode;
		this.path = this.app.path;
		this.lp = this.app.lp;
		this.util = new MWF.xApplication.Drive.Util();
		this.action = app.action;
		this.isManager = app.isManager;
		this.isZoneCreator = app.isZoneCreator;
		this.currentNaviType = this.options.currentNaviType;

		this.init();

	},
	init : function (){
		this.folderId = this.options.folderId;
		this.type = this.options.type;
		this.viewUrl = this.path+this.options.style+"/view/"+this.currentNaviType+"/"+(layout.mobile?"view_m":"view")+".html";
	},
	load: function(callback){
		this.content.loadHtml(this.viewUrl, {"bind": {"lp": this.lp,"data":{"isManager":this.isManager,"isZoneCreator":this.isZoneCreator}}, "module": this}, function(){
			this.setLayout();
			this._load();
			if (callback) callback();
		}.bind(this));
	},
	_load: function (){
		if(!layout.mobile){
			this.loadList("all");
		}

	},
	setLayout: function(){
		var items = this.content.getElements(".menuItem");
		items.addEvents({
			"mouseover": function(){this.addClass("menuItem_over")},
			"mouseout": function(){this.removeClass("menuItem_over")},
			"click": function(){}
		});
	},
	loadList: function(type,ev,data){

		if(!layout.mobile){
			if (this.currentMenu) this.setMenuItemStyleDefault(this.currentMenu);
			this.setMenuItemStyleCurrent(this[type+"MenuNode"]);
			this.currentMenu = this[type+"MenuNode"];
		}else{
			this.zoneListNode.hide();
			this.mainNode.show();
		}

		this._loadListContent(type);
	},
	_loadListContent: function(type){

		this.mainNode.empty();

		list = new MWF.xApplication.Drive[type.capitalize() +"List"](this.mainNode,this, {
			"onLoadData": function (){
				this.hideSkeleton();
			},
			"isEditor": true,
			"isAdmin" : true,
			"type" : type,
			"key" : this.options.key
		});
		this.currentList = list;



	},
	getFileData : function (id){
		return this.action.Attachment3Action.get(id).then(function(json){
			if(!json.data.zonePermissionList) return [];
			var dataList = json.data.zonePermissionList;
			dataList.each(function (d){
				if(d.role === "admin"){
					d.roleName = "管理员";
				}else if(d.role === "editor"){
					d.roleName = "编辑";
				}else if(d.role === "viewer"){
					d.roleName = "仅查看";
				}else if(d.role === "auditor"){
					d.roleName = "可授权";
				}else{
					d.roleName = "可查看/可下载";
				}
			})
			return json.data;
		});
	},
	getZoneData : function (id){
		return this.action.Folder3Action.get(id).then(function(json){
			if(!json.data.zonePermissionList) return [];
			var dataList = json.data.zonePermissionList;
			dataList.each(function (d){
				if(d.role === "admin"){
					d.roleName = "管理员";
				}else if(d.role === "editor"){
					d.roleName = "编辑";
				}else if(d.role === "viewer"){
					d.roleName = "仅查看";
				}else if(d.role === "auditor"){
					d.roleName = "可授权";
				}else{
					d.roleName = "可查看/可下载";
				}
			})
			return json.data;
		});
	},
	addPersonConfig : function (){
		var data = {

		};
		data.capacity = "0";

		var settingNode = new Element("div");

		var url = this.path+this.options.style+"/view/dlg/personSetting.html";
		settingNode.loadHtml(url, {"bind": {"lp": this.lp}, "module": this},function (){

			var settingForm = new MForm(settingNode, data, {
				isEdited: true,
				style : "attendance",
				itemTemplate: {

					capacity: {"text": "容量(单位M)，0表示无限大", "type": "text","style": {"width": "90%"}},
					person: {"text": "用户","orgType":["person"], "type": "org","style": {"width": "90%"}},
				},
				onPostLoad:function(){

				}.bind(this)
			},this,{});
			settingForm.load();


			var settingDlg = o2.DL.open({
				"title": "个人容量设置",
				"style": "user",
				"isResize": false,
				"content": settingNode,
				"container" : this.content,
				"maskNode": this.content,
				"minTop": 5,
				"width": "800",
				"height": "600",
				"buttonList": [
					{
						"type": "ok",
						"text": "保存",
						"action": function (d, e) {

							var result = settingForm.getResult(false, null, false, false, false);
							result.person = result.person[0];

							this.savePersonSetting(result).then(function (json){

								this.notice("保存成功","success");

								settingDlg.close();
							}.bind(this))

						}.bind(this)
					},
					{
						"type": "cancel",
						"text": "关闭",
						"action": function () {
							settingDlg.close();
							settingNode.destroy();
							//_self.content.unmask();
						}.bind(this)
					}
				],
				"onPostLoad": function () {

				}
			});

		}.bind(this));



	},
	savePersonSetting  : function (data){
		return this.action.ConfigAction.savePersonConfig(data).then(function(json){

			return json.data;
		});
	},
	setting : function (){
		this.getSettingData().then(function(data){

			data.capacity = data.capacity + "";
			data.fileTypeIncludes = data.fileTypeIncludes.join();
			data.fileTypeExcludes = data.fileTypeExcludes.join();

			var settingNode = new Element("div");

			var url = this.path+this.options.style+"/view/dlg/setting.html";
			settingNode.loadHtml(url, {"bind": {"lp": this.lp}, "module": this},function (){
//(true表示共享区子目录可设置查看权限|false表示共享区所有目录文件查看权限根据共享区的权限来，默认为true)
				var settingForm = new MForm(settingNode, data, {

					isEdited: true,
					style : "v10",
					mvcStyle: "v10",
					itemTemplate: {
						fileTypeIncludes: {"text": "只允许上传的文件后缀", "type": "oo-input","attr" : {"label-style": "min-width:15vw;"}},
						fileTypeExcludes: {"text": "不允许上传的文件后缀", "type": "oo-input","attr" : {"label-style": "min-width:15vw;"}},
						capacity: {"text": "个人默认容量(单位M)，0表示无限大", "type": "oo-input","attr" : {"label-style": "min-width:15vw;"}},
						recycleDays: {"text": "回收站数据保留天数", "type": "oo-input","attr" : {"label-style": "min-width:15vw;"}},
						officeHome: {"text": "libreoffice安装目录", "type": "oo-input","attr" : {"label-style": "min-width:15vw;"}},
						previewTools: {"text": "附件预览工具（需从应用市场安装对应应用）",
							"type": "oo-radiogroup","selectValue":"onlyoffice,wpsoffice,libreoffice,officeonline","attr" : {"label-style": "min-width:15vw;justify-content: flex-end;"}},
						viewDownLoadUrl: {"text": "onlyoffice附件查看下载地址", "type": "oo-input","attr" : {"label-style": "min-width:15vw;"}},
						officeOnlineUrl: {"text": "officeOnline服务器web访问地址", "type": "oo-input","attr" : {"label-style": "min-width:15vw;"}},
						portNumbers: {"text": "与libreoffice连接端口", "type": "oo-input","attr" : {"label-style": "min-width:15vw;"}},
						panMenuList: {"text": "网盘顶部菜单，默认：个人文件、企业文件", "type": "oo-input","attr" : {"label-style": "min-width:15vw;"}},
						zoneReadPermissionDown: {"text": "共享区目录是否允许设置查看权限", "type": "oo-radiogroup","selectValue":"true,false","selectText":"是,否","attr" : {"label-style": "min-width:15vw;justify-content: flex-end;"}},
						zoneAdminList: {"text": "共享区可创建权限列表(人员、组织或群组)","orgOptions":{"count":0},"orgType":["person","unit","group"], "type": "org",
							"attr" : {}},
						isOpenOfficeEdit: {"text": "打开Office时是否直接进入编辑状态", "type": "oo-radiogroup","selectText":"是,否","selectValue":"true,false","attr" : {"label-style": "min-width:15vw;justify-content: flex-end;"}},
						peronFileEnable: {"text": "是否启用个人文件", "type": "oo-radiogroup","selectText":"是,否","selectValue":"true,false","attr" : {"label-style": "min-width:15vw;justify-content: flex-end;"}},

					},
					onPostLoad:function(){

					}.bind(this)
				},this,{});
				settingForm.load();


				var settingDlg = o2.DL.open({
					"title": "系统设置",
					"style": "user",
					"isResize": false,
					"content": settingNode,
					"container" : this.app.content,
					"maskNode": this.app.content,
					"minTop": 5,
					"width": "800",
					"height": "600",
					"buttonList": [
						{
							"type": "ok",
							"text": "保存",
							"action": function (d, e) {

								var result = settingForm.getResult(false, null, false, false, false);

								if(result.fileTypeExcludes!=""){
									result.fileTypeExcludes = result.fileTypeExcludes.split(",");
								}else {
									result.fileTypeExcludes = [];
								}

								if(result.fileTypeIncludes!=""){
									result.fileTypeIncludes = result.fileTypeIncludes.split(",");
								}else{
									result.fileTypeIncludes = [];
								}
								if(result.panMenuList!=""){

									result.panMenuList = result.panMenuList.split(",");
								}else{
									result.panMenuList = [];
								}
								this.saveSettingData(result).then(function (json){

									this.app.notice("保存成功","success");

									settingDlg.close();
								}.bind(this))

							}.bind(this)
						},
						{
							"type": "cancel",
							"text": "关闭",
							"action": function () {
								settingDlg.close();
								settingNode.destroy();
								//_self.content.unmask();
							}.bind(this)
						}
					],
					"onPostLoad": function () {

					}
				});

			}.bind(this));






		}.bind(this));
	},
	getSettingData  : function (){
		return this.action.ConfigAction.getSystemConfig().then(function(json){

			return json.data;
		});
	},
	saveSettingData  : function (data){
		return this.action.ConfigAction.saveSystemConfig(data).then(function(json){

			return json.data;
		});
	},
	setZone3: function(e,data){
		//console.log(this.filterDlg);
		if (this.filterDlg) return;
		var node = e.target;

		var position = node.getPosition();
		var y = position.y-60;
		var x = position.x - 40;
		var fx = node.getParent(".menuItem").position.x;

		var filterContent = new Element("div");
		var url = this.path+this.options.style+"/view/dlg/favMore.html";
		filterContent.loadHtml(url, {"bind": {"lp": this.lp, "type": this.options.type, "data": data, filter: this.currentList.filterList}, "module": this})

		var _self = this;
		var closeFilterDlg = function(){
			_self.filterDlg.close();
		}
		this.filterDlg = o2.DL.open({
			"mask": false,
			"title": "",
			"style": "user",
			"isMove": false,
			"isResize": false,
			"isTitle": false,
			"content": filterContent,
			"container" : this.content,
			"maskNode": this.content,
			"top": y,
			"left": x,
			"fromTop": y,
			"fromLeft": fx,
			"width": 200,
			"height": 100,
			"duration": 100,
			// "onQueryClose": function(){
			// 	document.body.removeEvent("mousedown", closeFilterDlg);
			// },
			"onPostClose": function(){
				document.body.removeEvent("mousedown", closeFilterDlg);
				_self.filterDlg = null;
			}

		});
		this.filterDlg.node.addEvent("mousedown", function(e){
			e.stopPropagation();
		});
		document.body.addEvent("mousedown", closeFilterDlg);
	},

	setMenuItemStyleDefault: function(node){
		node.removeClass("mainColor_bg_opacity");
		node.getFirst().removeClass("mainColor_color");
		node.getLast().removeClass("mainColor_color");
	},
	setMenuItemStyleCurrent: function(node){
		node.addClass("mainColor_bg_opacity");
		node.getFirst().addClass("mainColor_color");
		node.getLast().addClass("mainColor_color");
	},
	showSkeleton: function(){
		if (this.skeletonNode) this.skeletonNode.inject(this.listContentNode);
	},
	hideSkeleton: function(){
		if (this.skeletonNode) this.skeletonNode.dispose();
	},
	recordStatus: function(){
		return {"navi": this.currentList.options.type};
	}
});
MWF.xApplication.Drive.PersonContent = new Class({
	Extends: MWF.xApplication.Drive.Content
});
MWF.xApplication.Drive.PublicContent = new Class({
	Extends: MWF.xApplication.Drive.Content,
	_load: function (){

		if(!layout.mobile){
			this.loadFavoriteList(function (){
				this.loadPublicMenuList();
			}.bind(this));
		}else{
			this.loadPublicData().then(function(data){

				var url = this.path+this.options.style+"/view/mobile/zoneMenu.html";
				this.publicMenuListNode.loadHtml(url, {"bind": {"lp": this.lp, "type": this.options.type, "data": data}, "module": this}, function(){

				}.bind(this));



			}.bind(this));
		}

	},
	loadList: function(type,id,ev,data){

		if(!layout.mobile){
			if (this.currentMenu) {
				this.setMenuItemStyleDefault(this.currentMenu);
				if(this.currentMenu.getElement(".menuItemAction")){
					this.currentMenu.getElement(".menuItemAction").hide();
				}

			}
			this.setMenuItemStyleCurrent(this[type + id+"MenuNode"]);
			this.currentMenu = this[type + id+"MenuNode"];

			if(this.currentMenu.getElement(".menuItemAction")){
				this.currentMenu.getElement(".menuItemAction").show();
			}

		}else{
			this.zoneListNode.hide();
			this.mainNode.show();
		}
		this._loadListContent(data.folder?data.folder:data.zoneId,data);

	},
	_loadListContent: function(id,data){

		this.zoneId = id;
		this.mainNode.empty();
		list = new MWF.xApplication.Drive["ZoneList"](this.mainNode,this, {
			"onLoadData": function (){
				this.hideSkeleton();
			},
			"folderId" : id,
			"zoneId" : id,
			"type" : "zone",
			"key" : this.options.key,
			"isAdmin": data.isAdmin,
			"isEditor": data.isEditor,
			"isCreator": data.isAdmin,
			"name" : data.name,
			"folderData" : {
				"isEditor" : data.isEditor,
				"isAdmin" : data.isAdmin

			}
		});
		this.currentList = list;
	},

	loadFavoriteList : function (callback){
		var _self = this;
		_self.favoriteListNode.empty();
		this.loadFavoriteData().then(function(data){
			_self.loadFavoriteItems(data,_self.favoriteListNode);
			if(callback) callback();
		});
	},
	loadFavoriteItems : function (data,node){

		var url = this.path+this.options.style+"/view/favMenu.html";
		node.loadHtml(url, {"bind": {"lp": this.lp, "type": this.options.type, "data": data}, "module": this}, function(){

			if(this.options.key){

				if(!this.currentList) this._loadListContent("",{});
			}else {
				if(data.length>0){
					if(!this.currentList) this.loadList("fav",data[0].id,null,data[0]);
				}
			}

		}.bind(this));
	},
	loadFavoriteData : function (){
		return this.action.FavoriteAction.list().then(function(json){
			console.log(json.data)
			return json.data;
		});
	},
	loadPublicMenuList : function (){

		var _self = this;
		_self.publicMenuListNode.empty();
		this.loadPublicData().then(function(data){

			_self.loadPublicItems(data,_self.publicMenuListNode);
		});
	},
	loadPublicItems : function (data,node){

		var url = this.path+this.options.style+"/view/zoneMenu.html";
		node.loadHtml(url, {"bind": {"lp": this.lp, "type": this.options.type, "data": data}, "module": this}, function(){

			if (!this.currentMenu) {


				if(this.options.key){
					if(!this.currentList) this._loadListContent("",{});
				}else {
					if(data.length>0){
						if(!this.currentList) this.loadList("zone",data[0].id,null,data[0]);
					}
				}

			}

		}.bind(this));
	},
	searchZone : function(e){
		var key = this.searchZoneNode.get("value");

		MWF.require("MWF.widget.PinYin", function(){

			var pinyin = key.toPY().toLowerCase();
			var firstPY = key.toPYFirst().toLowerCase();


			this.publicMenuListNode.getElements(".menuItem").each(function(menu){
				var menuItemText = menu.getElement(".menuItemText").get("text");

				var menuPinyin = menuItemText.toPY().toLowerCase();
				var menuFirstPY = menuItemText.toPYFirst().toLowerCase();

				if(menuItemText.indexOf(key)>-1 || menuPinyin.indexOf(pinyin) >  -1 || menuFirstPY.indexOf(firstPY) >  -1){
					menu.show();
				}else{
					menu.hide();
				}
			}.bind(this));

		}.bind(this));

	},
	loadPublicData : function (){
		return this.action.ZoneAction.list().then(function(json){
			console.log(json.data)
			return json.data;
		});
	},
	createZone : function (){
		var _self = this;
		var zoneNode = new Element("div");

		var url = this.path+this.options.style+"/view/dlg/newZone.html";
		zoneNode.loadHtml(url, {"bind": {"lp": this.lp}, "module": this},function(){
			var aclNode = zoneNode.getElement("textarea[name=acl]");

			aclNode.addEvent("click",function(){

				var opt = {
					"types": ["identity","unit","group"],
					"count": 0,
					"title": "添加成员",
					"values":aclNode.retrieve("aclName")?aclNode.retrieve("aclName"):[],
					"onComplete": function (items) {

						this.action.ConfigAction.isZonePermissionDown().then(function (json){

							var role;
							if(json.data.value){
								role = "reader";
							}else{
								role = "editor"
							}

							var zonePermissionList = [];
							var zonePermissionNameList = [];
							items.each(function(item){
								var json = {
									role : role
								};
								if(item.data.distinguishedName.indexOf("@I")>-1){
									json.name = item.data.woPerson.distinguishedName;
								}else{
									json.name = item.data.distinguishedName;
								}

								zonePermissionList.push(json);
								zonePermissionNameList.push(item.data.name);
							})
							aclNode.set("value",zonePermissionNameList.join());
							aclNode.store("acl",zonePermissionList);
							aclNode.store("aclName",zonePermissionNameList);
						}.bind(this));

					}.bind(this)
				};
				o2.xDesktop.requireApp("Selector", "package", function(){
					new o2.O2Selector(this.app.content, opt);
				}.bind(this), false);
			}.bind(this));
		}.bind(this));


		this.zoneDlg = o2.DL.open({
			"title": "创建共享区",
			"style": "user",
			"isResize": false,
			"content": zoneNode,
			"maskNode": this.app.content,
			"container" : this.app.content,
			"minTop": 5,
			"width": "550",
			"height": "450",
			"buttonList": [
				{
					"type": "ok",
					"text": "确定",
					"action": function (d, e) {
						var name = zoneNode.getElement("input[name=name]").get("value");
						var aclNode = zoneNode.getElement("textarea[name=acl]");
						var description = zoneNode.getElement("textarea[name=desc]").get("value");
						this.action.ZoneAction.create({
							"name" : name,
							"description" : description
						}).then(function(json){
							var zonePermissionList = aclNode.retrieve("acl");
							if(zonePermissionList && zonePermissionList.length>0){
								_self.action.ZoneAction.savePermission(json.data.id,{
									zonePermissionList : zonePermissionList
								}).then(function (json){
									_self.loadPublicMenuList.delay(500,_self);
									_self.zoneDlg.close();
									zoneNode.destroy();
								}.bind(this));
							}else{
								_self.loadPublicMenuList.delay(500,_self);
								_self.zoneDlg.close();
								zoneNode.destroy();
							}
						});

					}.bind(this)
				},
				{
					"type": "cancel",
					"text": "取消",
					"action": function () {
						this.zoneDlg.close();
						zoneNode.destroy();
						//_self.content.unmask();
					}.bind(this)
				}
			],
			"onPostLoad": function () {

			}
		});
	},
	setZone: function(e,data){
		//console.log(this.filterDlg);
		if (this.filterDlg) return;
		var node = e.target;

		var position = node.getPosition();
		// var y = position.y+10;
		// var x = position.x + 20;
		var y = position.y-60;
		var x = position.x - 40;
		var fx = position.x;

		var filterContent = new Element("div");
		var url = this.path+this.options.style+"/view/dlg/zoneMore.html";


		filterContent.loadHtml(url, {"bind": {"lp": this.lp, "type": this.options.type, "data": data, filter: this.currentList.filterList}, "module": this})

		var _self = this;
		var closeFilterDlg = function(){
			_self.filterDlg.close();
		}
		this.filterDlg = o2.DL.open({
			"mask": false,
			"title": "",
			"style": "user",
			"isMove": false,
			"isResize": false,
			"isTitle": false,
			"content": filterContent,
			"maskNode": this.content,
			"container" : this.content,
			"top": y,
			"left": x,
			"fromTop": y,
			"fromLeft": fx,
			"width": 200,
			"height": 180,
			"duration": 100,
			// "onQueryClose": function(){
			// 	document.body.removeEvent("mousedown", closeFilterDlg);
			// },
			"onPostClose": function(){
				document.body.removeEvent("mousedown", closeFilterDlg);
				_self.filterDlg = null;
			}

		});
		this.filterDlg.node.addEvent("mousedown", function(e){
			e.stopPropagation();
		});
		document.body.addEvent("mousedown", closeFilterDlg);
	},
	_reloadZoneAcl : function (zoneId){
		this.getZoneData(zoneId).then(function(data){
			this.zoneNode.empty();
			var url = this.path+this.options.style+"/view/dlg/zone.html";
			this.fixUnitList(data.zonePermissionList);
			this.zoneNode.loadHtml(url, {"bind": {"lp": this.lp,"data":data.zonePermissionList}, "module": this})
		}.bind(this));
	},
	deleteZone : function (ev,data){
		var _self = this;
		this.app.confirm("warn", ev.currentTarget, "确定删除该共享区", "删除后不能恢复，同时会删除对应共享区下的所有文件！", 350, 120, function () {
			_self.action.ZoneAction.delete(data.id).then(function (){
				_self.loadPublicMenuList.delay(500,_self);
			});
			this.close();
		}, function () {
			this.close();
		});
	},
	fixUnitList: function(zonePermissionList) {
		var unitList = zonePermissionList.filter(function(d) {
			return d.name.includes("@U");
		});

		var arr = unitList.map(function(d) {
			return d.name;
		});

		o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({ "unitList": arr }, function(json) {
			zonePermissionList.forEach(function(d) {
				json.data.forEach(function(dd) {
					if (d.name === dd.distinguishedName) {
						d.showName = dd.levelName;
					}
				});
			});
		}, null, false);
	},
	setZoneAcl : function (ev,data){

		this.filterDlg.close();
		var _self = this;

		this.getZoneData(data.id).then(function(data){

			var zoneNode = new Element("div");

			this.zoneNode = zoneNode;
			var url = this.path+this.options.style+"/view/dlg/zone.html";




			this.fixUnitList(data.zonePermissionList);




			zoneNode.loadHtml(url, {"bind": {"lp": this.lp,"data":data.zonePermissionList}, "module": this})

			this.zoneDlg = o2.DL.open({
				"title": "《" + data.name + "》权限设置",
				"style": "user",
				"isResize": false,
				"content": zoneNode,
				"maskNode": this.app.content,
				"container" : this.content,
				"minTop": 5,
				"width": "800",
				"height": "600",
				"buttonList": [
					{
						"type": "ok",
						"text": "添加",
						"styles" : {
							"border": "0px",
							"background-color": "#4A90E2",
							"height": "30px",
							"float" : "left",
							"border-radius": "20px",
							"min-width": "80px",
							"margin": "10px 10px 10px 30px",
							"color": "#ffffff"
						},

						"action": function (d, e) {

							var opt = {
								"types": ["identity","unit","group"],
								"count": 0,
								"title": "添加成员",
								"values":[],
								"onComplete": function (items) {
									var zonePermissionList = [];
									items.each(function(item){
										var json = {
											role : "reader"
										};
										if(item.data.distinguishedName.indexOf("@I")>-1){
											json.name = item.data.woPerson.distinguishedName;
										}else{
											json.name = item.data.distinguishedName;
										}
										var flag = true;
										data.zonePermissionList.each(function(d){
											if(d.name===json.name){
												flag= false;
											}
										})
										if(flag) zonePermissionList.push(json);

									})
									if(zonePermissionList.length>0){
										this.action.ZoneAction.savePermission(data.id,{
											zonePermissionList : zonePermissionList
										}).then(function (json){
											this._reloadZoneAcl(data.id);
										}.bind(this));
									}

								}.bind(this)
							};
							o2.xDesktop.requireApp("Selector", "package", function(){
								new o2.O2Selector(this.content, opt);
							}.bind(this), false);

						}.bind(this)
					},
					{
						"type": "ok",
						"text": "清空",
						"styles" : {
							"border": "0px",
							"background-color": "#4A90E2",
							"height": "30px",
							"float" : "left",
							"border-radius": "20px",
							"min-width": "80px",
							"margin": "10px 10px 10px 10px",
							"color": "#ffffff"
						},

						"action": function (d, e) {

							var removeZonePermissionList = [];
							data.zonePermissionList.each(function (d){
								if(d.name !== layout.user.distinguishedName){
									removeZonePermissionList.push(									{
										"role": d.role,
										"name": d.name
									});
								}
							})

							this.action.ZoneAction.savePermission(data.id,{
								zonePermissionList : [
									{
										"role": "admin",
										"name": layout.user.distinguishedName
									}
								]
							}).then(function (json){

								this.action.ZoneAction.deletePermission(data.id,{
									zonePermissionList : removeZonePermissionList
								}).then(function (json){
									this._reloadZoneAcl(data.id);

								}.bind(this));
							}.bind(this));

						}.bind(this)
					},
					{
						"type": "cancel",
						"text": "关闭",
						"action": function () {
							this.zoneDlg.close();
							zoneNode.destroy();
							//_self.content.unmask();
						}.bind(this)
					}
				],
				"onPostLoad": function () {

				}
			});



		}.bind(this));


	},

	setRoleAcl : function (id,ev,dataList){

		var data ;
		for(var i = 0 ; i < dataList.length;i++){
			if(dataList[i].id === id){
				data = dataList[i];
				break ;
			}
		}

		if (this.filterDlg) return;
		var node = ev.target;

		this.currentRoleNode  = node;

		var position = node.getPosition();
		var y = position.y-60;
		var x = position.x - 40;
		var fx = position.x;

		var filterContent = new Element("div");
		var url = this.path+this.options.style+"/view/dlg/roleAclMore.html";
		filterContent.loadHtml(url, {"bind": {"lp": this.lp, "type": this.options.type,"data":data,"read":true}, "module": this})

		var _self = this;
		var closeFilterDlg = function(){
			_self.filterDlg.close();
		}
		this.filterDlg = o2.DL.open({
			"mask": false,
			"title": "",
			"style": "user",
			"isMove": false,
			"isResize": false,
			"isTitle": false,
			"content": filterContent,
			"container" : this.app.content,
			"top": y,
			"left": x,
			"fromTop": y,
			"fromLeft": fx,
			"width": 200,
			"height": 250,
			"duration": 100,
			// "onQueryClose": function(){
			// 	document.body.removeEvent("mousedown", closeFilterDlg);
			// },
			"onPostClose": function(){
				document.body.removeEvent("mousedown", closeFilterDlg);
				_self.filterDlg = null;
			}

		});
		this.filterDlg.node.addEvent("mousedown", function(e){
			e.stopPropagation();
		});
		document.body.addEvent("mousedown", closeFilterDlg);


	},
	saveRoleAcl : function (type,ev,data){
		this.filterDlg.close();
		//this.currentRoleNode.set("text",type);

		this.action.ZoneAction.savePermission(data.zoneId,{
			zonePermissionList : [{
				"name" : data.name,
				"role" : type
			}]
		}).then(function (json){
			this._reloadZoneAcl(data.zoneId);
		}.bind(this));
	},
	removeRoleAcl : function(ev,data){
		this.filterDlg.close();
		this.currentRoleNode.getParent("tr").hide();
		this.action.ZoneAction.deletePermission(data.zoneId,{
			zonePermissionList : [{
				"name" : data.name,
				"role" : data.role
			}]
		}).then(function (json){
			console.log(json);
		});
	},
	addZoneFav : function (ev,data){
		this.action.FavoriteAction.create({
			"name" : data.name,
			"folder" : data.id
		}).then(function (json){
			this.loadFavoriteList();

			this.filterDlg.close();
		}.bind(this));

	},
	removeZoneFav  : function (ev,data){
		this.filterDlg.close();
		this.action.FavoriteAction.delete(data.id).then(function (json){
			this.loadFavoriteList();
			this.loadPublicMenuList();
			this.app.notice("删除成功","success");
		}.bind(this));

	},
	renameZone : function(ev,data){
		this.filterDlg.close();
		var _self = this;
		var zoneNode = new Element("div");

		var url = this.path+this.options.style+"/view/dlg/renameFav.html";
		zoneNode.loadHtml(url, {"bind": {"lp": this.lp,"data":data}, "module": this})

		this.zoneDlg = o2.DL.open({
			"title": "重命名",
			"style": "user",
			"isResize": false,
			"content": zoneNode,
			"maskNode": this.app.content,
			"container" : this.app.content,
			"minTop": 5,
			"width": "500",
			"height": "400",
			"buttonList": [
				{
					"type": "ok",
					"text": "确定",
					"action": function (d, e) {

						var name = zoneNode.getElement("input").get("value");

						this.action.FavoriteAction.update(data.id,{
							"name" : name
						}).then(function(json){
							_self.zoneDlg.close();
							zoneNode.destroy();
							_self.loadFavoriteList();
						});

					}.bind(this)
				},
				{
					"type": "cancel",
					"text": "取消",
					"action": function () {
						this.zoneDlg.close();
						zoneNode.destroy();
						//_self.content.unmask();
					}.bind(this)
				}
			],
			"onPostLoad": function () {

			}
		});
	},
	editZone : function (ev,data){
		this.filterDlg.close();
		var _self = this;
		var zoneNode = new Element("div");

		var url = this.path+this.options.style+"/view/dlg/editZone.html";
		zoneNode.loadHtml(url, {"bind": {"lp": this.lp,"data":data}, "module": this})

		this.zoneDlg = o2.DL.open({
			"title": "修改共享区",
			"style": "user",
			"isResize": false,
			"content": zoneNode,
			"maskNode": this.app.content,
			"container" : this.app.content,
			"minTop": 5,
			"width": "500",
			"height": "400",
			"buttonList": [
				{
					"type": "ok",
					"text": "确定",
					"action": function (d, e) {

						var name = zoneNode.getElement("input[name='name']").get("value");
						var capacity = zoneNode.getElement("input[name='capacity']").get("value");
						var description = zoneNode.getElement("textarea").get("value");

						this.action.ZoneAction.update(data.zoneId,{
							"name" : name,
							"capacity":capacity,
							"description" : description
						}).then(function(json){
							_self.zoneDlg.close();
							zoneNode.destroy();
							_self.loadPublicMenuList();
						});

					}.bind(this)
				},
				{
					"type": "cancel",
					"text": "取消",
					"action": function () {
						this.zoneDlg.close();
						zoneNode.destroy();
						//_self.content.unmask();
					}.bind(this)
				}
			],
			"onPostLoad": function () {

			}
		});
	}
});
MWF.xApplication.Drive.ManageContent = new Class({
	Extends: MWF.xApplication.Drive.Content,
	_load: function (){
		this.loadList("manageFile");
	},
});
MWF.xApplication.Drive.List = new Class({
	Implements: [Options, Events],
	options: {
		"type": "all",
		"defaultViewType" : "list",
		"folderId" : "-1",
		"orderBy" : "updateTime",
		"desc" : true
	},
	initialize: function (node,app, options) {
		this.setOptions(options);
		this.app = app;
		this.container = node;
		this.lp = this.app.lp;
		this.util = new MWF.xApplication.Drive.Util();
		this.action = app.action;
		this.type = this.options.type;

		this.orderBy = this.options.orderBy;
		this.desc = this.options.desc;

		var url = this.app.path+this.app.options.style+"/view/"+ this.app.currentNaviType + (layout.mobile?"/content_m.html":"/content.html");
		this.container.loadHtml(url, {"bind": {"lp": this.lp,"data":{"type":this.type}}, "module": this}, function(){
			this.content = this.listContentNode;
			this.dropFile();
			this.init();
			this.load();

		}.bind(this));

	},
	dropFile : function (){
		if (!this.isContentSetEvent){
			this.content.addEventListener("dragover", function( e ) {
				e.preventDefault();
				e.stopPropagation();
			}, false);
			this.content.addEventListener("drop", function( e ) {

				this.uploadAttachment(e.dataTransfer.files);
				event.preventDefault();
			}.bind(this), false);

			this.isContentSetEvent = true;
		}
	},
	uploadAttachment : function (files){

		if (this.app.currentNaviType === "manage") return;
		var folderId = this.folderId;
		if(folderId === "-1") folderId = "(0)";
		var isContinue = true;
		for (var i = 0; i < files.length; i++) {
			var file = files.item(i);
			this.action[this.type === "zone"?"Attachment3Action":"Attachment2Action"].checkFileUpload({
				"fileSize": file.size,
				"fileName": file.name
			}, function (json) {
				console.log(json)
			}.bind(this),  function (){
				isContinue = false;
			}.bind(this), false);
		}

		o2.Actions.get("x_pan_assemble_control").action.actions = {};
		var json = {
			"enctype": "formData",
			"method": "POST",
			"uri": "/jaxrs/attachment2/upload/folder/{folder}"
		}
		o2.Actions.get("x_pan_assemble_control").action.actions.upload = json;

		if(this.type === "zone"){
			json.uri = "/jaxrs/attachment3/upload/folder/{folder}"
		}

		if (files.length){

			var count = files.length;
			var current = 0;


			if (isContinue){
				for (var i = 0; i < files.length; i++) {
					var file = files.item(i);

					var formData = new FormData();
					// formData.append("fileName", file.name);
					formData.append('file', file);

					o2.Actions.get("x_pan_assemble_control").action.invoke({
						"name": "upload",
						"async": true,
						"data": formData,
						"file": file,
						"parameter": {
							"folder" : folderId
						},
						"success": function(json){
							current++;

							if(current===files.length){
								this.refresh();
							}
						}.bind(this)
					});
				}
			}
		}

	},
	showSkeleton: function(){


		if (this.skeletonNode) this.skeletonNode.inject(this.listContentNode);
	},
	hideSkeleton: function(){

		if (this.skeletonNode) this.skeletonNode.dispose();
	},
	loadListTitle : function (){
		this.listTitleNode.empty();
		this.listTitleNode.loadHtml(this.titleTempleteUrl, {"bind": {"lp": this.lp}, "module": this}, function(){

			var sortNode = this["sort_" + this.orderBy + "_Node"];
			if(sortNode && sortNode.getElement(".sort")){
				sortNode.getElement(".sort").setStyle("display","inline-block");
				if(!this.desc){
					sortNode.getElement(".icon-shangjiantou").addClass("mainColor_color");
				}else {
					sortNode.getElement(".icon-xiajiantou").addClass("mainColor_color");
				}
			}


		}.bind(this));
	},
	sortFile : function (orderBy,ev){

		this.desc = !this.desc;
		this.orderBy = orderBy;

		this.refresh();
	},
	selectAllFile : function (e){

		if (e.currentTarget.get("disabled").toString()!="true"){
			var itemNode = e.currentTarget.getParent(".listItem");
			var iconNode = e.currentTarget.getElement(".selectFlagIcon");

			if (itemNode){
				if (itemNode.hasClass("mainColor_bg_opacity")){
					itemNode.removeClass("mainColor_bg_opacity");
					iconNode.removeClass("o2icon-xuanzhong");
					iconNode.removeClass("selectFlagIcon_select");
					iconNode.removeClass("mainColor_color");


					this.listContentNode.getElements(this.toolbar.options.viewType === "list"?"tr":".listItem2").each(function (tr){
						tr.removeClass("mainColor_bg_opacity");
						var ss = tr.getElement(".selectFlagIcon");
						tr.getElement(".selectFlag").hide();
						ss.removeClass("o2icon-xuanzhong");
						ss.removeClass("selectFlagIcon_select");
						ss.removeClass("mainColor_color");

					})

					this.selectedList = [];

				}else{
					itemNode.addClass("mainColor_bg_opacity");
					iconNode.addClass("o2icon-xuanzhong");
					iconNode.addClass("selectFlagIcon_select");
					iconNode.addClass("mainColor_color");
					this.listContentNode.getElements(this.toolbar.options.viewType === "list"?"tr":".listItem2").each(function (tr){
						tr.getElement(".selectFlag").show();
						tr.addClass("mainColor_bg_opacity");
						var ss = tr.getElement(".selectFlagIcon");

						ss.addClass("o2icon-xuanzhong");
						ss.addClass("selectFlagIcon_select");
						ss.addClass("mainColor_color");

					})

					this.selectedList = this.dataList;
				}
			}
		}

		this._setToolBar();
	},
	loadItems: function(data){

		this.dataList = data;

		this.content.loadHtml(this.listTempleteUrl, {"bind": {"lp": this.lp, "type": this.options.type, "data": data}, "module": this}, function(){
			this.node = this.content.getFirst();

			this.bindThumb();


		}.bind(this));
	},
	init: function(){

		this.folderId = this.options.folderId;
		this.orderBy = "updateTime";
		this.desc = true;

		this.isTile = true;
		if(this.type === "all"){
			if(this.options.key){
				var keyContainer = new Element("div.ft_filterItem").inject(this.pathNode);
				new Element("div",{"class":"ft_filterItemTitle mainColor_color","text":"关键字："}).inject(keyContainer);
				new Element("div",{"class":"ft_filterItemName","text":this.options.key}).inject(keyContainer);
				var iconNode = new Element("icon",{"class":"o2icon-clear ft_filterItemDel"}).inject(keyContainer);

				iconNode.addEvent("click",function (ev){
					ev.target.getParent().hide();
					this.app.app.loadNavi("person");
				}.bind(this))
			}else {

				var rootPathNode = new Element("span",{"text":this.lp.allFile}).inject(this.pathNode);
				rootPathNode.addEvent("click",function(ev){

					this.app.currentList.folderId = this.options.folderId;
					this.app.currentList.refresh();
					ev.target.getAllNext().destroy();
					this.returnPathNode.destroy();
					this.returnPathNode = null;
				}.bind(this));
				this.rootPathNode = rootPathNode;
			}
		}else {
			this.pathNode.hide();
		}
	},
	_initTempate: function (){

		if(layout.mobile){
			this.titleTempleteUrl = this.app.path+this.app.options.style+"/view/mobile/list_title.html";
			this.listTempleteUrl = this.app.path+this.app.options.style+"/view/mobile/list.html";

		}else{
			this.titleTempleteUrl = this.app.path+this.app.options.style+"/view/"+this.app.currentNaviType+"/"+this.type+"/"+this.options.defaultViewType+"_title.html";
			this.listTempleteUrl = this.app.path+this.app.options.style+"/view/"+this.app.currentNaviType+"/"+this.type+"/" +this.options.defaultViewType + ".html";

		}

	},
	load: function(){

		debugger
		var _self = this;

		this._initToolBar();
		this._initTempate();
		if(!layout.mobile){
			this.loadListTitle();
		}


		this.loadToolBar(this.toolbarItems.unSelect);
		this.selectedList = [];
		this.loadData().then(function(data){
			_self.hide();
			_self.loadItems(data);
		});
	},
	_initToolBar : function (){

		if(this.type === "recycle"){
			this.toolbarItems = {
				"unSelect":[
					["clear"]
				],
				"selected":[
					["delete"],
					["restore"]
				],
				"mulSelect":[
					["delete"],
					["restore"]
				]
			}
		}else if (this.type === "zone"){

			if(this.options.isAdmin || this.options.isEditor ){

				this.toolbarItems = {
					"unSelect":[
						["upload","uploadFolder"],
						["createFolder"]
					],
					"selected":[
						["rename", "recycle"],
						["download", "move","saveZoneTo"],
						["setAcl"],
						["ai"]
					],
					"mulSelect":[
						["recycle"],
						["download", "move","saveZoneTo"]
					]
				}
			}else {
				this.toolbarItems = {
					"unSelect":[

					],
					"selected":[
						["download","saveZoneTo"],
						["ai"]
					],
					"mulSelect":[
						["download","saveZoneTo"]
					]
				}
			}

		}else if(this.type === "all"){
			this.toolbarItems = {
				"unSelect":[
					["upload","uploadFolder"],
					["createFolder"],
					["createOffice"]

				],
				"selected":[
					["rename", "recycle"],
					["download", "move"],
					["share","saveToZone"],
					["editOffice"],
					["ai"]
				],
				"mulSelect":[
					["recycle"],
					["download", "move"],
					["share","saveToZone"]
				]
			}
		}else {
			this.toolbarItems = {
				"unSelect":[
					["upload","uploadFolder"],
				],
				"selected":[
					["rename", "recycle"],
					["download", "move"],
					["share"],
					["ai"]
				],
				"mulSelect":[
					["recycle"],
					["download", "move"],
					["share"]
				]
			}
		}

	},
	loadToolBar : function (availableTool){
		this.toolBarNode.empty();
		this.toolbar = new MWF.xApplication.Drive.Toolbar(this.toolBarNode, this, {
			viewType : this.options.defaultViewType,
			type : this.type,
			availableTool : availableTool
		});
		this.toolbar.load();
	},
	refresh: function(){
		this.hide();
		this.load();
	},
	hide: function(){
		if (this.node) this.node.destroy();
	},
	loadData: function(){
		var _self = this;
		if(this.options.type === "all"){

			if(this.options.key){
				return this.action.Attachment2Action.listWithFilter(this.options.key).then(function(json){
					_self.fireEvent("loadData");
					return _self._fixData(json.data);
				});
			}else {
				var dataList = [];
				if(this.folderId === "-1"){
					return this.action.Folder2Action.listTop(_self.orderBy,_self.desc).then(function(json){
						dataList =  json.data;
						return _self.action.Attachment2Action.listTop(_self.orderBy,_self.desc).then(function(json){

							dataList.append(json.data);
							_self.fireEvent("loadData");
							return _self._fixData(dataList);
						});
					});
				}else {
					return this.action.Folder2Action.listWithFolder(_self.folderId,_self.orderBy,_self.desc).then(function(json){
						dataList =  json.data;
						return _self.action.Attachment2Action.listWithFolder(_self.folderId,_self.orderBy,_self.desc).then(function(json){

							dataList.append(json.data);
							_self.fireEvent("loadData");
							return _self._fixData(dataList);
						});
					});
				}
			}



		}
		else if(this.options.type === "recycle"){
			return this.action.RecycleAction.list().then(function(json){
				_self.fireEvent("loadData");
				return _self._fixData(json.data);
			});
		}
		else  if(this.options.type === "zone"){

			var dataList = [];
			return this.action.Folder3Action.listWithFolder(_self.folderId).then(function(json){
				dataList =  json.data;
				return _self.action.Attachment3Action.listWithFolder(_self.folderId).then(function(json){

					dataList.append(json.data);
					_self.fireEvent("loadData");
					return _self._fixData(dataList);
				});
			});

		}else if(this.options.type === "search"){
			return this.action.Attachment2Action.listWithFilter(this.searchKey).then(function(json){
				_self.fireEvent("loadData");
				return _self._fixData(json.data);
			});
		}else {

			return this.action.Attachment2Action.listFileTypePaging(1,1000,{"fileType":this.options.type}).then(function(json){
				_self.fireEvent("loadData");
				return _self._fixData(json.data);
			});
		}
	},
	_fixData : function (dataList){
		dataList.each(function (data){

			if(data.superior  || data.fileType === "folder"){
				data.extension = "folder";
				data.type = "folder";
			}

			if(this.app.currentNaviType === "person" || this.app.currentNaviType === "manage"){
				data.isAdmin = true;
				data.isEditor = true;
				data.downloadable = true;
			}

			//console.log("data.length:" + data.length)
			data.length = this.util.getFileSize(data.length);
			data.fileType = this.util.getFileExtension(data.extension);

		}.bind(this));
		return dataList;
	},

	overTaskItem: function(e){
		e.currentTarget.addClass("listItem_over");

		var iconNode = e.currentTarget.getElement(".selectFlagIcon");
		if (iconNode.hasClass("selectFlagIcon_select")){

		}else{
			e.currentTarget.getElement(".selectFlag").show();
		}
	},
	overTaskItem2: function(e){
		e.currentTarget.addClass("listItem_over");

		var iconNode = e.currentTarget.getElement(".selectFlagIcon");
		if (iconNode.hasClass("selectFlagIcon_select")){

		}else{
			e.currentTarget.getElement(".selectFlag").show();
		}
	},
	outTaskItem: function(e){
		e.currentTarget.removeClass("listItem_over");
		var iconNode = e.currentTarget.getElement(".selectFlagIcon");

		if (iconNode.hasClass("selectFlagIcon_select")){

		}else{
			e.currentTarget.getElement(".selectFlag").hide();
		}
	},
	outTaskItem2: function(e){
		e.currentTarget.removeClass("listItem_over");

		var iconNode = e.currentTarget.getElement(".selectFlagIcon");

		if (iconNode.hasClass("selectFlagIcon_select")){

		}else{
			e.currentTarget.getElement(".selectFlag").hide();
		}

	},
	open: function(id,e){

		var data ;
		for(var i = 0 ; i < this.dataList.length;i++){
			if(this.dataList[i].id === id){
				data = this.dataList[i];
				break ;
			}
		}
		if(data.type === "folder"){

			if(!this.returnPathNode){
				this.returnPathNode = new Element("span",{"text":"返回上一级","style":"margin-right:10px"}).inject(this.rootPathNode,"before");
				this.returnPathNode.addEvent("click",function (){
					this.pathNode.getLast().getPrevious().click();
				}.bind(this));
			}

			if(this.app.currentNaviType === "public"){
				this.options.isAdmin = data.isAdmin;
				this.options.isEditor = data.isEditor;
				this.options.folderData = {
					"isEditor" : data.isEditor,
					"isAdmin" : data.isAdmin

				}
			}

			this.folderId = data.id;
			this.refresh();
			var folderPathNode = new Element("span.pathicon",{"text":data.name}).inject(this.pathNode);
			folderPathNode.store("data",data);
			folderPathNode.addEvent("click",function(ev){
				var data = ev.target.retrieve("data");
				this.options.isAdmin = data.isAdmin;
				this.options.isEditor = data.isEditor;

				this.options.folderData = {
					"isEditor" : data.isEditor,
					"isAdmin" : data.isAdmin

				}

				if(this.app.currentNaviType === "person" ){
					this.options.isAdmin = true;
					this.options.isEditor = true;
				}


				this.folderId = data.id;
				this.refresh();
				ev.target.getAllNext().destroy();
			}.bind(this));


		}else{
			new MWF.xApplication.Drive.AttachmenPreview(data,this);
		}

	},
	getAttachmentUrl : function (att,callback){


		if(!this.type){
			var fileId ;
			var shareId;

			if(att.fileId){
				fileId = att.fileId;
				shareId = att.id;
			}else {
				fileId = att.id;
				shareId = this.shareId;
			}
			callback(o2.filterUrl(o2.Actions.getHost( "x_pan_assemble_control" ) + "/x_pan_assemble_control/jaxrs/share/download/share/"+shareId+"/file/" + fileId));

		}else {

			if(this.type === "manageFile"){
				callback(o2.filterUrl(o2.Actions.getHost( "x_pan_assemble_control" ) + "/x_pan_assemble_control/jaxrs/attachment3/" + att.id + "/download/stream"));

			}else {
				callback(o2.filterUrl(o2.Actions.getHost( "x_pan_assemble_control" ) + "/x_pan_assemble_control/jaxrs/"+(this.type==="zone"?"attachment3":"attachment2")+"/" + att.id + "/download/stream"));

			}

		}
	},
	getPreviewvAttachmentUrl : function (att,callback){

		callback(o2.Actions.getHost( "x_pan_assemble_control" ) + "/x_pan_assemble_control/jaxrs/"+(this.type==="zone"?"attachment3":"attachment2")+"/" + att.id + "/office/preview");
	},
	loadItemIcon: function(application, e){
		return
		this.app.loadItemIcon(application, e);
	},
	loadMore : function (e,data){
		this.wi = new Ant.Wiget();
		this.data = data;
		var itemActionUrl = this.path+"itemAction.json";
		this.moreActionNode = new Element("ul",{"class":"ant-dropdown-menu"});
		this.moreActionNode.set("style","min-width: 140px;");

		var json = [
			{
				"title": "下载",
				"action": "download",
				"show": "this.data.fileType!==\"folder\""
			},
			{
				"title": "GPG加密下载",
				"action": "gpgDownload",
				"show": "1>2"
			}
		]
		json.each(function(itemAction){
			this.createActionItemNode(itemAction);
		}.bind(this));
		this.documentDropNode = this.wi.dropdown(e,this.moreActionNode,"right");



	},
	createActionItemNode : function( actionItem ){
		var actionItemNode = new Element("li", {
			"class":"ant-dropdown-menu-item"
		});
		if( actionItem.title ){
			var textNode =  new Element("span", {
				"text": actionItem.title
			});
			if( actionItem.text )textNode.set("title", actionItem.text);
			textNode.inject(actionItemNode);
		}
		if(actionItem.show){
			var flag = eval(actionItem.show,this);
			if(!flag) return;
		}
		actionItemNode.inject(this.moreActionNode);
		actionItemNode.addEvent("click",function (){
			if( this[actionItem.action] ){
				this[actionItem.action]();
			}
		}.bind(this));
	},
	loadItemFlag: function(e, data){

	},
	selectFile: function(id,e, dataList){

		var data ;
		for(var i = 0 ; i < this.dataList.length;i++){
			if(this.dataList[i].id === id){
				data = this.dataList[i];
				break ;
			}
		}
		console.log("----------" + id)
		console.log(data)

		if (e.currentTarget.get("disabled").toString()!="true"){
			var itemNode = e.currentTarget.getParent(".listItem");
			var iconNode = e.currentTarget.getElement(".selectFlagIcon");

			if (itemNode){
				if (itemNode.hasClass("mainColor_bg_opacity")){
					itemNode.removeClass("mainColor_bg_opacity");
					iconNode.removeClass("o2icon-xuanzhong");
					iconNode.removeClass("selectFlagIcon_select");
					iconNode.removeClass("mainColor_color");
					this.unselectedFile(data);
				}else{
					itemNode.addClass("mainColor_bg_opacity");
					iconNode.addClass("o2icon-xuanzhong");
					iconNode.addClass("selectFlagIcon_select");
					iconNode.addClass("mainColor_color");
					this.selectedFile(data);
				}
			}
		}

		// this.app.app.detailContentNode.empty();

		this._setToolBar();

	},
	_setToolBar : function (){

		if(this.selectedList.length === 0 ){

			// var url = this.app.path+this.app.options.style+"/view/show_empty.html";
			// this.app.app.detailContentNode.loadHtml(url, {"bind": {"lp": this.lp,"data":this.selectedList[0]}, "module": this}, function(){
			//
			// }.bind(this));
			//
			//

			if(this.app.currentNaviType === "person"){
				this.options.isAdmin = true;
				this.options.isEditor = true;
			}else{
				this.options.isAdmin = this.options.folderData.isAdmin;
				this.options.isEditor = this.options.folderData.isEditor;
			}



			this.loadToolBar(this.toolbarItems.unSelect);

		} else if (this.selectedList.length === 1){

			this.options.isAdmin = this.selectedList[0].isAdmin;
			this.options.isEditor = this.selectedList[0].isEditor;

			if(this.app.currentNaviType === "person" || this.app.currentNaviType === "manage"){
				this.options.isAdmin = true;
				this.options.isEditor = true;
			}

			this._initToolBar();
			// var url = this.app.path+this.app.options.style+"/view/show.html";
			// this.app.app.detailContentNode.loadHtml(url, {"bind": {"lp": this.lp,"data":this.selectedList[0]}, "module": this}, function(){
			//
			// }.bind(this));

			this.loadToolBar(this.toolbarItems.selected);

		}else{

			// var url = this.app.path+this.app.options.style+"/view/show_muit.html";
			// this.app.app.detailContentNode.loadHtml(url, {"bind": {"lp": this.lp,"data":this.selectedList}, "module": this}, function(){
			//
			// }.bind(this));

			this.loadToolBar(this.toolbarItems.mulSelect);

		}
	},
	bindThumb: function(){

		this.dataList.each(function(d){

			if(d.fileType === "img"){
				var node = this.content.getElement("." + d.id);
				if(this.type!=="recycle" && this.type!=="myShare" && this.type!=="shareToMe"){
					this.action[this.type==="zone"?"Attachment3Action":"Attachment2Action"].getImageWidthHeightBase64(d.id,120,120,function (json){
						node.set("src","data:image/"+d.extension+';base64,'+ json.data.value)
					});
				}
			}
		}.bind(this))

	},

	selectFile2: function(id,e){

		var data ;
		for(var i = 0 ; i < this.dataList.length;i++){
			if(this.dataList[i].id === id){
				data = this.dataList[i];
				break ;
			}
		}

		if (e.currentTarget.get("disabled").toString()!="true"){
			var itemNode = e.currentTarget.getParent(".listItem2");
			var iconNode = e.currentTarget.getElement(".selectFlagIcon");

			if (itemNode){
				if (itemNode.hasClass("mainColor_bg_opacity")){
					itemNode.removeClass("mainColor_bg_opacity");
					iconNode.removeClass("o2icon-xuanzhong");
					iconNode.removeClass("selectFlagIcon_select");
					iconNode.removeClass("mainColor_color");
					this.unselectedFile(data);
				}else{
					itemNode.addClass("mainColor_bg_opacity");
					iconNode.addClass("o2icon-xuanzhong");
					iconNode.addClass("selectFlagIcon_select");
					iconNode.addClass("mainColor_color");
					this.selectedFile(data);
				}
			}
		}

		//this.app.app.detailContentNode.empty();

		if(this.selectedList.length === 0 ){

			// var url = this.app.path+this.app.options.style+"/view/show_empty.html";
			// this.app.app.detailContentNode.loadHtml(url, {"bind": {"lp": this.lp,"data":this.selectedList[0]}, "module": this}, function(){
			//
			// }.bind(this));

			if(this.type === "recycle"){
				this.loadToolBar([
					["clear"]
				]);
			}else {
				this.loadToolBar([
					["upload","uploadFolder"],
					["createFolder"]
				]);
			}
		} else if (this.selectedList.length === 1){
			//
			//
			// var url = this.app.path+this.app.options.style+"/view/show.html";
			// this.app.app.detailContentNode.loadHtml(url, {"bind": {"lp": this.lp,"data":this.selectedList[0]}, "module": this}, function(){
			//
			// }.bind(this));

			if(this.type === "recycle"){
				this.loadToolBar([
					["delete"],
					["restore"]
				]);
			}else{
				this.loadToolBar([
					["rename", "recycle"],
					["download", "move"],
					["share"]
				]);
			}

		}else{

			// var url = this.app.path+this.app.options.style+"/view/show_muit.html";
			// this.app.app.detailContentNode.loadHtml(url, {"bind": {"lp": this.lp,"data":this.selectedList}, "module": this}, function(){
			//
			// }.bind(this));

			if(this.type === "recycle"){
				this.loadToolBar([
					["delete"],
					["restore"]
				]);
			}else{
				this.loadToolBar([
					["recycle"],
					["download", "move"],
					["share"]
				]);
			}

		}

	},
	selectedFile: function(data){
		// console.log("=============")
		// console.log(data)
		//
		// delete data._;
		if (!this.selectedList) this.selectedList = [];
		var idx = this.selectedList.findIndex(function(t){
			return t.id == data.id;
		});
		if (idx===-1) this.selectedList.push(data);
	},
	unselectedFile: function(data){
		// delete data._;
		if (!this.selectedList) this.selectedList = [];
		var idx = this.selectedList.findIndex(function(t){
			return t.id == data.id;
		});
		if (idx!==-1) this.selectedList.splice(idx, 1);
	}
});
MWF.xApplication.Drive.AllList = new Class({
	Extends: MWF.xApplication.Drive.List
});
MWF.xApplication.Drive.ZoneList = new Class({
	Extends: MWF.xApplication.Drive.List,

	init: function(){

		this.folderId = this.options.folderId;

		this.isTile = true;
		this.orderBy = "updateTime";
		this.desc = true;

		if(this.options.key){
			var keyContainer = new Element("div.ft_filterItem").inject(this.pathNode);
			new Element("div",{"class":"ft_filterItemTitle mainColor_color","text":"关键字："}).inject(keyContainer);
			new Element("div",{"class":"ft_filterItemName","text":this.options.key}).inject(keyContainer);
			var iconNode = new Element("icon",{"class":"o2icon-clear ft_filterItemDel"}).inject(keyContainer);

			iconNode.addEvent("click",function (ev){
				ev.target.getParent().hide();
				this.app.app.loadNavi("public");
			}.bind(this))
		}else {

			var rootPathNode = new Element("span",{"text":this.options.name}).inject(this.pathNode);
			rootPathNode.addEvent("click",function(ev){
				this.folderId = this.options.zoneId;
				this.refresh();
				ev.target.getAllNext().destroy();
				this.returnPathNode.destroy();
				this.returnPathNode = null;
			}.bind(this));

			this.rootPathNode = rootPathNode;

		}
	},
	_initToolBar : function (){

		this.toolbarItems = {
			"unSelect":[
				["upload","uploadFolder"],
				["createFolder"],
				["createOffice"]
			],
			"selected":[
				["rename", "recycle"],
				["download"],
				["move"],
				["saveZoneTo"],
				["editOffice"],
				["setAcl"],
				["ai"]
			],
			"mulSelect":[
				["recycle"],
				["move"],
				["saveZoneTo"],
			]
		}
	},
	loadData: function(){
		var _self = this;
		if(this.options.key){
			return this.action.Attachment3Action.listWithFilter(this.options.key).then(function(json){
				_self.fireEvent("loadData");
				return _self._fixData(json.data);
			});
		}else {
			var dataList = [];
			if(this.folderId === "-1"){
				return this.action.Folder3Action.listTop(_self.orderBy,_self.desc).then(function(json){
					dataList =  json.data;
					return _self.action.Attachment3Action.listTop(_self.orderBy,_self.desc).then(function(json){

						dataList.append(json.data);
						_self.fireEvent("loadData");
						return _self._fixData(dataList);
					});
				});
			}else {
				return this.action.Folder3Action.listWithFolder(_self.folderId,_self.orderBy,_self.desc).then(function(json){
					dataList =  json.data;
					return _self.action.Attachment3Action.listWithFolder(_self.folderId,_self.orderBy,_self.desc).then(function(json){

						dataList.append(json.data);
						_self.fireEvent("loadData");
						return _self._fixData(dataList);
					});
				});
			}
		}
	},
});
MWF.xApplication.Drive.ImageList = new Class({
	Extends: MWF.xApplication.Drive.List,
	_initTempate: function (){
		if(layout.mobile){
			this.titleTempleteUrl = this.app.path+this.app.options.style+"/view/mobile/list_title.html";
			this.listTempleteUrl = this.app.path+this.app.options.style+"/view/mobile/list.html";
		}else{
			this.titleTempleteUrl = this.app.path+this.app.options.style+"/view/"+this.app.currentNaviType+"/all/"+this.options.defaultViewType+"_title.html";
			this.listTempleteUrl = this.app.path+this.app.options.style+"/view/"+this.app.currentNaviType+"/all/" +this.options.defaultViewType + ".html";
		}


	},
	loadData: function(){
		var _self = this;
		return this.action.Attachment2Action.listFileTypePaging(1,1000,{"fileType":this.options.type}).then(function(json){
			_self.fireEvent("loadData");
			return _self._fixData(json.data);
		});
	}
});
MWF.xApplication.Drive.OfficeList = new Class({
	Extends: MWF.xApplication.Drive.ImageList
});
MWF.xApplication.Drive.MovieList = new Class({
	Extends: MWF.xApplication.Drive.ImageList
});
MWF.xApplication.Drive.MusicList = new Class({
	Extends: MWF.xApplication.Drive.ImageList
});
MWF.xApplication.Drive.OtherList = new Class({
	Extends: MWF.xApplication.Drive.ImageList
});
MWF.xApplication.Drive.RecycleList = new Class({
	Extends: MWF.xApplication.Drive.List,
	init : function (){
		this.folderId = this.options.folderId;

		this.orderBy = "updateTime";
		this.desc = true;


		var rootPathNode = new Element("span",{"text":"回收站"}).inject(this.pathNode);

	},
	open : function (){

	},
	loadData: function(){
		var _self = this;
		return this.action.RecycleAction.list().then(function(json){
			_self.fireEvent("loadData");
			return _self._fixData(json.data);
		});
	}
});

MWF.xApplication.Drive.MyShareList = new Class({
	Extends: MWF.xApplication.Drive.List,
	init: function(){
		this.folderId = this.options.folderId;
		this.shareId = this.options.shareId;

		this.pathNode.empty();
		var rootPathNode = new Element("span",{"text":this.type==="myShare"?"我的分享":"分享给我"}).inject(this.pathNode);
		rootPathNode.addEvent("click",function(ev){
			this.app.currentList.folderId = "-1";
			this.app.currentList.refresh();
			ev.target.getAllNext().destroy();
		}.bind(this));


	},
	getAttachmentUrl : function (att,callback){
		var fileId ;
		var shareId;

		if(att.fileId){
			fileId = att.fileId;
			shareId = att.id;
		}else {
			fileId = att.id;
			shareId = this.shareId;
		}
		callback(o2.filterUrl(o2.Actions.getHost( "x_pan_assemble_control" ) + "/x_pan_assemble_control/jaxrs/share/download/share/"+shareId+"/file/" + fileId));
	},
	_initToolBar : function (){

		this.toolbarItems = {
			"unSelect":[

			],
			"selected":[
				["cancelShare"]
			],
			"mulSelect":[
				["cancelShare"]
			]
		}

	},
	bindThumb: function(){

		// this.dataList.each(function(d){
		//
		// 	if(d.fileType === "img"){
		// 		var node = this.content.getElement("." + d.id);
		// 		this.action["Attachment2Action"].getImageWidthHeightBase64(d.fileId,120,120,function (json){
		// 			node.set("src","data:image/"+d.extension+';base64,'+ json.data.value)
		// 		});
		// 	}
		// }.bind(this))

	},
	loadData : function (){
		var _self = this;

		var dataList = [];
		if(this.folderId === "-1"){
			return _self.action.ShareAction.listMyShare2("member","(0)").then(function(json){
				_self.fireEvent("loadData");
				return _self._fixData(json.data);
			});
		}else {
			return this.action.ShareAction.listFolderWithFolder(_self.shareId,_self.folderId).then(function(json){
				dataList =  json.data;
				return _self.action.ShareAction.listAttWithFolder(_self.shareId,_self.folderId).then(function(json){

					dataList.append(json.data);
					_self.fireEvent("loadData");
					return _self._fixData(dataList);
				});
			});
		}
	},
	_fixData : function (dataList){
		dataList.each(function (data){

			if(data.superior  || data.fileType === "folder"){
				data.extension = "folder";
				data.type = "folder";
			}
			data.length = this.util.getFileSize(data.length);
			data.fileType = this.util.getFileExtension(data.extension);

			var arr = [];
			if(data.shareUserList){
				data.shareUserList.append(data.shareOrgList);
				data.shareUserList.append(data.shareGroupList);

				data.shareUserList.each(function(d){
					arr.push(d.split("@")[0]);
				})
			}


			data.share = arr.join();

		}.bind(this));
		return dataList;
	},
	open: function(id,e){

		var data ;
		for(var i = 0 ; i < this.dataList.length;i++){
			if(this.dataList[i].id === id){
				data = this.dataList[i];
				break ;
			}
		}

		if(data.fileType === "folder"){

			if(data.fileId){
				this.folderId = data.fileId;
				this.shareId = data.id;
			}else {
				this.folderId = data.id;
			}

			this.refresh();

			var folderPathNode = new Element("span.pathicon",{"text":data.name}).inject(this.pathNode);

			folderPathNode.setStyle("background","")
			folderPathNode.addEvent("click",function(ev){

				var data = ev.target.retrieve("data");
				if(data.fileId){
					this.folderId = data.fileId;
					this.shareId = data.id;
				}else {
					this.folderId = data.id;
				}
				this.refresh();
				ev.target.getAllNext().destroy();
			}.bind(this));


		}else{
			new MWF.xApplication.Drive.AttachmenPreview(data,this);
		}

	}
});
MWF.xApplication.Drive.ShareToMeList = new Class({
	Extends: MWF.xApplication.Drive.MyShareList,
	_initToolBar : function (){

		this.toolbarItems = {
			"unSelect":[

			],
			"selected":[
				["shareDownload"],
				["saveTo"],
				["shareShield"]
			],
			"mulSelect":[
				["saveTo"],
				["shareShield"]
			]
		}
	},
	loadData : function (){

		var _self = this;

		var dataList = [];
		if(this.folderId === "-1"){
			return _self.action.ShareAction.listShareToMe2("(0)").then(function(json){
				_self.fireEvent("loadData");
				return _self._fixData(json.data);
			});
		}else {
			return this.action.ShareAction.listFolderWithFolder(_self.shareId,_self.folderId).then(function(json){
				dataList =  json.data;
				return _self.action.ShareAction.listAttWithFolder(_self.shareId,_self.folderId).then(function(json){

					dataList.append(json.data);
					_self.fireEvent("loadData");
					return _self._fixData(dataList);
				});
			});
		}


	},
	bindThumb: function(){

	},
	getAttachmentUrl : function (att,callback){
		var fileId ;
		var shareId;
		if(att.fileId){
			fileId = att.fileId;
			shareId = att.id;
		}else {
			fileId = att.id;
			shareId = this.shareId;
		}
		callback(o2.filterUrl(o2.Actions.getHost( "x_pan_assemble_control" ) + "/x_pan_assemble_control/jaxrs/share/download/share/"+shareId+"/file/" + fileId));
	},
});
MWF.xApplication.Drive.FolderList = new Class({
	Implements: [Options, Events],
	options: {
		"folderId" : "-1"
	},
	initialize: function (node,app,explorer,options) {
		this.setOptions(options);

		this.app = app;
		this.lp = app.lp;
		this.content = node;

		this.explorer = explorer;
		this.type = this.options.type;
		this.action = app.action;
	},
	init: function(){

		this.folderId = this.options.folderId;

		this.pathNode = new Element("div").inject(this.content);
		var rootPathNode = new Element("span",{"text":this.lp.allFile}).inject(this.pathNode);
		rootPathNode.addEvent("click",function(ev){
			this.explorer.folderList.folderId = this.options.folderId;
			this.explorer.folderList.refresh();
			ev.target.getAllNext().destroy();
		}.bind(this));
	},
	load: function(){

		var _self = this;
		this.loadData().then(function(data){
			_self.dataList = data;
			_self.hide();
			_self.loadItems(data);
		});
	},
	refresh: function(){
		this.hide();
		this.load();
	},
	hide: function(){
		if (this.node) this.node.destroy();
	},
	loadData: function(){

		var _self = this;

		if(this.folderId === "-1"){
			if(this.type==="zone"){
				return this.action.ZoneAction.list().then(function(json){
					return json.data;
				});
			}else {
				return this.action.Folder2Action.listTop().then(function(json){
					return json.data;
				});
			}

		}else {
			return this.action[this.type==="zone"?"Folder3Action":"Folder2Action"].listWithFolder(_self.folderId).then(function(json){
				return json.data;
			});
		}
	},
	loadItems: function(data){

		var url = this.app.path+this.app.options.style+"/view/folder/list.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp, "type": this.options.type, "data": data}, "module": this}, function(){
			this.node = this.content.getElement(".tableBody");

		}.bind(this));
	},
	overTaskItem: function(e){
		e.currentTarget.addClass("listItem_over");

	},
	outTaskItem: function(e){
		e.currentTarget.removeClass("listItem_over");

	},
	open: function(id,e){
		var data ;
		for(var i = 0 ; i < this.dataList.length;i++){
			if(this.dataList[i].id === id){
				data = this.dataList[i];
				break ;
			}
		}
		this.folderId = id;
		this.refresh();

		var folderPathNode = new Element("span.pathicon",{"text":data.name}).inject(this.pathNode);
		folderPathNode.store("data",data);
		folderPathNode.addEvent("click",function(ev){

			var data = ev.target.retrieve("data");
			this.folderId = data.id;
			this.refresh();
			ev.target.getAllNext().destroy();
		}.bind(this));
	},
});

MWF.xApplication.Drive.Toolbar = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"viewType" : "list",
		"type" : "all"
	},
	initialize : function( container, explorer, options ) {
		console.log("isEditor:" + explorer.options.isEditor)
		this.container = container;
		this.explorer = explorer;
		this.app = explorer.app.app;
		this.lp = explorer.app.lp;

		this.action = explorer.action;

		this.setOptions(options);

		this._initTools();
		this.type = this.options.type;

		this.availableTool = this.options.availableTool;

	},
	_initTools : function (){
		this.tools = {
			upload : {
				action : "upload",
				text : "上传",
				icon : "icon-upload",
				condition : "function(d){debugger;return this.explorer.options.isEditor;}"
			},
			ai : {
				action : "ai",
				text : "AI分析",
				icon : "icon-ai",
				condition : 'function(d){return !layout.mobile && (["jpeg","jpg", "png", "gif", "bmp", "webp", "doc", "docx", "wps", "ppt", "pptx", "pub", "vsd", "xls", "xlsx", "html", "htm", "txt", "md", "pdf", "ofd"].contains(d.extension))}'

			},
			uploadFolder : {
				action : "uploadFolder",
				text : "上传文件夹",
				icon : "icon-upload",
				condition : "function(d){return this.explorer.options.isEditor;}"
			},
			createFolder : {
				action : "createFolder",
				text : "新建文件夹",
				icon : "icon-newfolder",
				condition : "function(d){return this.explorer.options.isEditor;}"
			},
			createOffice : {
				action : "createOffice",
				text : "新建在线文档",
				icon : "icon-newfolder",
				condition : "function(d){return !layout.mobile && this.app.isEnableOffice && this.app.officeTool!=='libreoffice' && this.explorer.options.isEditor}"

			},
			rename : {
				action : "rename",
				text : "重命名",
				icon : "icon-rename",
				condition : "function(d){return !layout.mobile && this.explorer.options.isEditor;}"

			},
			download : {
				action : "download",
				text : "下载",
				icon : "icon-shareDownload",
				condition : "function(d){return  d.downloadable}"
			},
			shareDownload : {
				action : "shareDownload",
				text : "下载",
				icon : "icon-shareDownload"
			},
			saveTo : {
				action : "saveTo",
				text : "保存到网盘",
				icon : "icon-shareSave",
				condition : "function(d){return  d.downloadable}"
			},
			saveZoneTo : {
				action : "saveZoneTo",
				text : "保存到网盘",
				icon : "icon-shareSave",
				condition : "function(d){return  false;}"
			},
			saveToZone : {
				action : "saveToZone",
				text : "保存到工作区",
				icon : "icon-shareSave"
			},
			setAcl : {
				action : "setZoneAcl",
				text : "设置权限",
				icon : "icon-setting",
				condition : "function(d){return d.type=='folder' && !layout.mobile && (d.isAdmin  || d.isAuditor) }"
			},
			editOffice : {
				action : "editOffice",
				text : "编辑",
				icon : "icon-rename",
				condition : "function(d){return !layout.mobile && (['doc','docx','xls','xlsx','ppt','pptx'].contains(d.extension))&&this.app.isEnableOffice && this.app.officeTool!=='libreoffice' && this.explorer.options.isEditor;}"
			},
			shareShield : {
				action : "shareShield",
				text : "屏蔽",
				icon : "icon-shield"
			},
			move : {
				action : "move",
				text : "移动",
				icon : "icon-move",
				condition : "function(d){return !layout.mobile && (d.isAdmin  || d.isAuditor) }"
			},
			recycle : {
				action : "recycle",
				text : "删除",
				icon : "icon-delete",
				condition : "function(d){debugger;return (d.isAdmin ) }"
			},
			delete : {
				action : "delete",
				text : "彻底删除",
				icon : "icon-delete"
			},
			share : {
				action : "share",
				text : "分享",
				icon : "icon-share1"
			},
			cancelShare : {
				action : "cancelShare",
				text : "取消分享",
				icon : "icon-shareCancel"
			},
			restore : {
				action : "restore",
				text : "恢复",
				icon : "icon-restore"
			},
			clear : {
				action : "clear",
				text : "清空回收站",
				icon : "icon-clear"
			},
			editZone : {
				action : "editZone",
				text : "编辑",
				icon : "icon-rename"
			},
			setZoneAcl : {
				action : "setZoneAcl",
				text : "设置权限",
				icon : "icon-rename"
			},
			editZone : {
				action : "editZone",
				text : "编辑",
				icon : "icon-rename"
			},
			deleteZone : {
				action : "deleteZone",
				text : "删除",
				icon : "icon-delete"
			},
			resetZoneAcl : {
				action : "resetZoneAcl",
				text : "重置权限",
				icon : "icon-rename"
			},
			addCapacity : {
				action : "addCapacity",
				text : "添加",
				icon : "icon-upload"
			},
			editCapacity : {
				action : "editCapacity",
				text : "修改",
				icon : "icon-move"
			}
			,
			deleteCapacity : {
				action : "deleteCapacity",
				text : "删除",
				icon : "icon-delete"
			}
		}
	},
	getConditionResult: function (str) {
		var flag = true;

		if (str && str.substr(0, 8) == "function") { //"function".length
			eval("var fun = " + str);
			if(this.explorer.selectedList){
				var data = this.explorer.selectedList[0];
			}else {
				var data = {}
			}
			flag = fun.call(this, data);

		}
		return flag;
	},
	load : function(){

		this.node = new Element("div").inject( this.container );

		this.availableTool.each( function( group ){
			var toolgroupNode = new Element("div.toolgroupNode").inject( this.node );
			var length = group.length;
			group.each( function( t, i ){
				var className;
				if( length == 1 ){
					className = "toolItemNode_single";
				}else{
					if( i == 0 ){
						className = "toolItemNode_left";
					}else if( i + 1 == length ){
						className = "toolItemNode_right";
					}else{
						className = "toolItemNode_center";
					}
				}

				var tool = this.tools[t];

				console.log(tool.condition,tool)
				var flag = true;
				if( tool.condition){

					flag = this.getConditionResult(tool.condition);

				}
				if(flag){
					var toolNode = new Element( "div", {
						class : className,
						style : "cursor:pointer;height:30px;line-height:30px;padding-left:12px;padding-right:12px;background: var(--oo-color-main);font-size: 13px;color: #FFFFFF;font-weight: 400;",
						events : {
							click : function( ev ){ this[tool.action]( ev ) }.bind(this)
						}
					}).inject( toolgroupNode );

					var iconNode = new Element("icon",{"class":"o2Drive " + tool.icon,"style":"margin-right:6px"}).inject(toolNode);
					var textNode = new Element("span").inject(toolNode);
					textNode.set("text",tool.text);
				}

			}.bind(this))
		}.bind(this));

	},
	fixUnitList: function(zonePermissionList) {
		var unitList = zonePermissionList.filter(function(d) {
			return d.name.includes("@U");
		});

		var arr = unitList.map(function(d) {
			return d.name;
		});

		o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({ "unitList": arr }, function(json) {
			zonePermissionList.forEach(function(d) {
				json.data.forEach(function(dd) {
					if (d.name === dd.distinguishedName) {
						d.showName = dd.levelName;
					}
				});
			});
		}, null, false);
	},
	ai : function (){
		var data = this.explorer.selectedList[0];

		var options = {
			"attId": data.id,
			"attName" : data.name,
			"msg" : "总结分析下该文件",
			"jars" : "x_pan_assemble_control",
			"appId":  "AI" + data.id
		};
		layout.openApplication(null, "AI", options);
	},
	setZoneAcl : function (){

		var _self = this;
		var zoneNode = new Element("div");

		var data = this.explorer.selectedList[0];

		this.data = data;
		this.explorer.app[data.type=="folder"||data.isZone?"getZoneData":"getFileData"](data.id).then(function(data){

			var zoneNode = new Element("div");

			this.zoneNode = zoneNode;
			var url = this.app.path+this.app.options.style+"/view/dlg/zone.html";

			this.fixUnitList(data.zonePermissionList);

			zoneNode.loadHtml(url, {"bind": {"lp": this.lp,"data":data.zonePermissionList}, "module": this})

			this.zoneDlg = o2.DL.open({
				"title": "《" + data.name + "》权限设置",
				"style": "user",
				"isResize": false,
				"content": zoneNode,
				"maskNode": this.app.content,
				"container" : this.app.content,
				"minTop": 5,
				"width": "800",
				"height": "600",
				"buttonList": [
					{
						"type": "ok",
						"text": "添加",
						"styles" : {
							"border": "0px",
							"background-color": "#4A90E2",
							"height": "30px",
							"float" : "left",
							"border-radius": "20px",
							"min-width": "80px",
							"margin": "10px 10px 10px 30px",
							"color": "#ffffff"
						},

						"action": function (d, e) {

							var opt = {
								"types": ["identity","unit","group"],
								"count": 0,
								"title": "添加成员",
								"values":[],
								"onComplete": function (items) {
									this.action.ConfigAction.isZonePermissionDown().then(function (json){

										var role;
										if(json.data.value){
											role = "reader";
										}else{
											role = "editor"
										}

										var zonePermissionList = [];
										items.each(function(item){
											var json = {
												role : role
											};
											if(item.data.distinguishedName.indexOf("@I")>-1){
												json.name = item.data.woPerson.distinguishedName;
											}else{
												json.name = item.data.distinguishedName;
											}
											var flag = true;
											data.zonePermissionList.each(function(d){
												if(d.name===json.name){
													flag= false;
												}
											})
											if(flag) zonePermissionList.push(json);
										})
										if(zonePermissionList.length>0){
											this.action.ZoneAction.savePermission(data.id,{
												zonePermissionList : zonePermissionList
											}).then(function (json){
												this._reloadZoneAcl(data.id);
											}.bind(this));
										}
									}.bind(this));

								}.bind(this)
							};
							o2.xDesktop.requireApp("Selector", "package", function(){
								new o2.O2Selector(this.app.content, opt);
							}.bind(this), false);

						}.bind(this)
					},
					{
						"type": "ok",
						"text": "清空",
						"styles" : {
							"border": "0px",
							"background-color": "#4A90E2",
							"height": "30px",
							"float" : "left",
							"border-radius": "20px",
							"min-width": "80px",
							"margin": "10px 10px 10px 10px",
							"color": "#ffffff"
						},

						"action": function (d, e) {

							this.explorer.app[this.data.type=="folder"||data.isZone?"getZoneData":"getFileData"](data.id).then(function(data){


								var removeZonePermissionList = [];
								data.zonePermissionList.each(function (d){
									if(d.name !== layout.user.distinguishedName){
										removeZonePermissionList.push(									{
											"role": d.role,
											"name": d.name
										});
									}
								})
								this.action.ZoneAction.savePermission(data.id,{
									zonePermissionList : [
										{
											"role": "admin",
											"name": layout.user.distinguishedName
										}
									]
								}).then(function (json){

									this.action.ZoneAction.deletePermission(data.id,{
										zonePermissionList : removeZonePermissionList
									}).then(function (json){
										this._reloadZoneAcl(data.id);

									}.bind(this));
								}.bind(this));

							}.bind(this))


						}.bind(this)
					},
					{
						"type": "cancel",
						"text": "关闭",
						"action": function () {
							this.zoneDlg.close();
							zoneNode.destroy();
							//_self.content.unmask();
						}.bind(this)
					}
				],
				"onPostLoad": function () {

				}
			});
		}.bind(this));




	},
	_reloadZoneAcl : function (zoneId){
		this.explorer.app[this.data.type=="folder"||this.data.isZone?"getZoneData":"getFileData"](zoneId).then(function(data){
			this.zoneNode.empty();
			var url = this.app.path+this.app.options.style+"/view/dlg/zone.html";
			this.fixUnitList(data.zonePermissionList);
			this.zoneNode.loadHtml(url, {"bind": {"lp": this.lp,"data":data.zonePermissionList}, "module": this})
		}.bind(this));
	},
	setRoleAcl : function (id,ev,dataList){

		var data ;
		for(var i = 0 ; i < dataList.length;i++){
			if(dataList[i].id === id){
				data = dataList[i];
				break ;
			}
		}

		if(data.name === layout.user.distinguishedName){
			alert("不能修改自己的权限")
			return;
		}


		if (this.filterDlg) return;
		var node = ev.target;

		this.currentRoleNode  = node;

		var position = node.getPosition();
		var y = position.y-60;
		var x = position.x - 40;
		var fx = position.x;

		var filterContent = new Element("div");
		var url = this.app.path+this.app.options.style+"/view/dlg/roleAclMore.html";


		if(this.data.id === this.data.zoneId){
			filterContent.loadHtml(url, {"bind": {"lp": this.lp, "type": this.options.type,"data":data,"read" : true}, "module": this})

		}else {
			this.action.ConfigAction.isZonePermissionDown().then(function(json){
				filterContent.loadHtml(url, {"bind": {"lp": this.lp, "type": this.options.type,"data":data,"read" : json.data.value}, "module": this})
			}.bind(this))
		}


		var _self = this;
		var closeFilterDlg = function(){
			_self.filterDlg.close();
		}
		this.filterDlg = o2.DL.open({
			"mask": false,
			"title": "",
			"style": "user",
			"isMove": false,
			"isResize": false,
			"isTitle": false,
			"content": filterContent,
			"container" : this.app.content,
			"top": y,
			"left": x,
			"fromTop": y,
			"fromLeft": fx,
			"width": 200,
			"height": 250,
			"duration": 100,
			// "onQueryClose": function(){
			// 	document.body.removeEvent("mousedown", closeFilterDlg);
			// },
			"onPostClose": function(){
				document.body.removeEvent("mousedown", closeFilterDlg);
				_self.filterDlg = null;
			}

		});
		this.filterDlg.node.addEvent("mousedown", function(e){
			e.stopPropagation();
		});
		document.body.addEvent("mousedown", closeFilterDlg);


	},
	saveRoleAcl : function (type,ev,data){
		this.filterDlg.close();
		//this.currentRoleNode.set("text",type);

		this.action.ZoneAction.savePermission(data.zoneId,{
			zonePermissionList : [{
				"name" : data.name,
				"role" : type
			}]
		}).then(function (json){

			this._reloadZoneAcl(data.zoneId);
			console.log(json);
		}.bind(this));
	},
	removeRoleAcl : function(ev,data){
		this.filterDlg.close();

		this.action.ZoneAction.deletePermission(data.zoneId,{
			zonePermissionList : [{
				"name" : data.name,
				"role" : data.role
			}]
		}).then(function (json){
			this.currentRoleNode.getParent("tr").hide();
		}.bind(this));
	},
	editZone : function (){
		var _self = this;
		var zoneNode = new Element("div");

		var data = this.explorer.selectedList[0];

		var url = this.app.path+this.app.options.style+"/view/dlg/editZone.html";
		zoneNode.loadHtml(url, {"bind": {"lp": this.lp,"data":data}, "module": this})

		this.zoneDlg = o2.DL.open({
			"title": "修改共享区",
			"style": "user",
			"isResize": false,
			"content": zoneNode,
			"maskNode": this.app.content,
			"container" : this.app.content,
			"minTop": 5,
			"width": "500",
			"height": "400",
			"buttonList": [
				{
					"type": "ok",
					"text": "确定",
					"action": function (d, e) {

						var name = zoneNode.getElement("input").get("value");
						var description = zoneNode.getElement("textarea").get("value");

						this.action.ZoneAction.update(data.zoneId,{
							"name" : name,
							"description" : description
						}).then(function(json){
							_self.zoneDlg.close();
							zoneNode.destroy();
							_self.app.notice("修改成功");
							_self.explorer.refresh();
						});

					}.bind(this)
				},
				{
					"type": "cancel",
					"text": "取消",
					"action": function () {
						this.zoneDlg.close();
						zoneNode.destroy();
						//_self.content.unmask();
					}.bind(this)
				}
			],
			"onPostLoad": function () {

			}
		});
	},
	deleteZone : function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "删除确认", "是否删除选中的"+dataList.length+"个共享区？删除的后不能恢复。", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){
				_self.action.ZoneAction.delete( data.id , function(){
					count++;
					if( dataList.length == count ){
						_self.app.notice("成功删除"+count+"个共享区。");
						_self.explorer.refresh();
					}
				});
			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	resetZoneAcl : function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "重置确认", "是否重置选中的"+dataList.length+"个共享区？重置后不能恢复。", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){
				_self.action.ZoneAction.resetPermission( data.id , {},function(){
					count++;
					if( dataList.length == count ){
						_self.app.notice("重置"+count+"个共享区。");
						_self.explorer.refresh();
					}
				});
			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	move : function (){
		var _self = this;
		var zoneNode = new Element("div");

		this.folderList = new MWF.xApplication.Drive.FolderList(zoneNode,this.app,this, {
			folderId : this.explorer.type === "zone"?this.explorer.app.zoneId:"-1",
			type : this.explorer.type
		});
		this.folderList.init();
		this.folderList.load();

		this.zoneDlg = o2.DL.open({
			"title": "移动到",
			"style": "user",
			"isResize": false,
			"content": zoneNode,
			"maskNode": this.app.content,
			"container" : this.app.content,
			"minTop": 5,
			"width": "600",
			"height": "400",
			"buttonList": [
				{
					"type": "ok",
					"text": "确认",
					"action": function (d, e) {

						var dataList = this.explorer.selectedList;
						this.app.confirm("warn", e, "移动文件确认", "是否移动选中的"+dataList.length+"个文件？", 350, 120, function () {
							var count = 0;
							dataList.each( function(data){

								var folderId = _self.folderList.folderId==="-1"?"":_self.folderList.folderId;

								var action = data.type == "folder"?"Folder3Action":"Attachment3Action";

								if(_self.explorer.type === "zone"){
									_self.action[action].move( data.id ,{
										"folder" : folderId,
										"superior" : folderId,
										"name" : data.name
									} ,function(){
										count++;
										if( dataList.length == count ){
											_self.app.notice("成功移动"+count+"个文件。");
											_self.explorer.refresh();
											_self.zoneDlg.close();
											zoneNode.destroy();
										}
									});
								}else{

									_self.action[data.type == "folder"?"Folder2Action":"Attachment2Action"][data.type == "folder"?"put":"update"]( data.id ,{
										"folder" : folderId,
										"superior" : folderId,
										"name" : data.name
									} ,function(){
										count++;
										if( dataList.length == count ){
											_self.app.notice("成功移动"+count+"个文件。");
											_self.explorer.refresh();
											_self.zoneDlg.close();
											zoneNode.destroy();
										}
									});

								}






							}.bind(this));
							this.close();
						}, function () {
							this.close();
						});

					}.bind(this)
				},
				{
					"type": "cancel",
					"text": "取消",
					"action": function () {
						this.zoneDlg.close();
						zoneNode.destroy();
						//_self.content.unmask();
					}.bind(this)
				}
			],
			"onPostLoad": function () {

			}
		});
	},
	editCapacity : function (){
		var data = this.explorer.selectedList[0];
		var settingNode = new Element("div");

		var url = this.app.path+this.app.options.style+"/view/dlg/personSetting.html";
		settingNode.loadHtml(url, {"bind": {"lp": this.lp}, "module": this},function (){

			var settingForm = new MForm(settingNode, data, {
				isEdited: true,
				style : "attendance",
				itemTemplate: {

					capacity: {"text": "容量(单位M)，0表示无限大", "type": "text","style": {"width": "90%"}},
					person: {"text": "用户","isEdited":false,"orgType":["person"], "type": "org","style": {"width": "90%"}},
				},
				onPostLoad:function(){

				}.bind(this)
			},this.app,{});
			settingForm.load();


			var settingDlg = o2.DL.open({
				"title": "修改个人容量",
				"style": "user",
				"isResize": false,
				"content": settingNode,
				"maskNode": this.content,
				"container" : this.content,
				"minTop": 5,
				"width": "800",
				"height": "600",
				"buttonList": [
					{
						"type": "ok",
						"text": "保存",
						"action": function (d, e) {

							var result = settingForm.getResult(false, null, false, false, false);
							result.person = result.person[0];

							this.action.ConfigAction.savePersonConfig(result).then(function (json){

								this.app.notice("修改成功");
								this.explorer.refresh();
								settingDlg.close();
							}.bind(this))

						}.bind(this)
					},
					{
						"type": "cancel",
						"text": "关闭",
						"action": function () {
							settingDlg.close();
							settingNode.destroy();
							//_self.content.unmask();
						}.bind(this)
					}
				],
				"onPostLoad": function () {

				}
			});

		}.bind(this));



	},
	addCapacity : function (){
		var data = {

		};
		data.capacity = "0";

		var settingNode = new Element("div");

		var url = this.app.path+this.app.options.style+"/view/dlg/personSetting.html";
		settingNode.loadHtml(url, {"bind": {"lp": this.lp}, "module": this},function (){

			var settingForm = new MForm(settingNode, data, {
				isEdited: true,
				style : "attendance",
				itemTemplate: {

					capacity: {"text": "容量(单位M)，0表示无限大", "type": "text","style": {"width": "90%"}},
					person: {"text": "用户","orgType":["person"], "type": "org","style": {"width": "90%"}},
				},
				onPostLoad:function(){

				}.bind(this)
			},this.app,{});
			settingForm.load();


			var settingDlg = o2.DL.open({
				"title": "个人容量设置",
				"style": "user",
				"isResize": false,
				"content": settingNode,
				"maskNode": this.content,
				"container" : this.content,
				"minTop": 5,
				"width": "800",
				"height": "600",
				"buttonList": [
					{
						"type": "ok",
						"text": "保存",
						"action": function (d, e) {

							var result = settingForm.getResult(false, null, false, false, false);
							result.person = result.person[0];

							this.action.ConfigAction.savePersonConfig(result).then(function (json){

								this.app.notice("添加成功");
								this.explorer.refresh();
								settingDlg.close();
							}.bind(this))

						}.bind(this)
					},
					{
						"type": "cancel",
						"text": "关闭",
						"action": function () {
							settingDlg.close();
							settingNode.destroy();
							//_self.content.unmask();
						}.bind(this)
					}
				],
				"onPostLoad": function () {

				}
			});

		}.bind(this));



	},
	saveTo : function (){
		var _self = this;
		var zoneNode = new Element("div");

		this.folderList = new MWF.xApplication.Drive.FolderList(zoneNode,this.app,this, {
			folderId : this.explorer.type === "zone"?this.explorer.app.zoneId:"-1",
			type : this.explorer.type
		});
		this.folderList.init();
		this.folderList.load();

		this.zoneDlg = o2.DL.open({
			"title": "保存到",
			"style": "user",
			"isResize": false,
			"content": zoneNode,
			"maskNode": this.app.content,
			"container" : this.app.content,
			"minTop": 5,
			"width": "600",
			"height": "400",
			"buttonList": [
				{
					"type": "ok",
					"text": "确认",
					"action": function (d, e) {

						var dataList = this.explorer.selectedList;

						var count = 0;
						dataList.each( function(data){

							var folderId = _self.folderList.folderId==="-1"?"(0)":_self.folderList.folderId;

							if(this.explorer.shareId === data.id){
								var fileId = data.id;
								var shareId = this.explorer.shareId;
							}else {
								var fileId = data.fileId;
								var shareId = data.id
							}
							_self.action.ShareAction.saveToFolder(shareId,fileId,folderId,{
							} ,function(){
								count++;
								if( dataList.length == count ){
									_self.app.notice("保存成功"+count+"个文件。");
									_self.explorer.refresh();
									_self.zoneDlg.close();
									zoneNode.destroy();
								}
							});

						}.bind(this));

					}.bind(this)
				},
				{
					"type": "cancel",
					"text": "取消",
					"action": function () {
						this.zoneDlg.close();
						zoneNode.destroy();
						//_self.content.unmask();
					}.bind(this)
				}
			],
			"onPostLoad": function () {

			}
		});
	},
	editOffice : function (){
		var data = this.explorer.selectedList[0];


		if(this.app.officeTool === "officeonline"){
			this.editOfficeOnline(data);
		}else if(this.app.officeTool === "wpsoffice"){
			this.editWpsOffice(data);
		}else{
			this.editOnlyOffice(data);
		}


	},
	editWpsOffice : function(data){
		var options = {
			"documentId": data.id,
			"mode":"write",
			"jars" : "x_pan_assemble_control",
			"appId":  "WpsOfficeEditor" + data.id
		};
		layout.openApplication(null, "WpsOfficeEditor", options);
	},
	editOnlyOffice : function(data){
		var options = {
			"documentId": data.id,
			"mode":"edit",
			"jars" : "x_pan_assemble_control",
			"appId":  "OnlyOfficeEditor" + data.id
		};
		layout.openApplication(null, "OnlyOfficeEditor", options);
	},
	editOfficeOnline : function (data){
		var options = {
			"documentId": data.id,
			"mode":"write",
			"appId":  "OfficeOnlineEditor" + data.id
		};
		layout.openApplication(null, "OfficeOnlineEditor", options);
	},
	saveZoneTo : function (){
		var _self = this;
		var zoneNode = new Element("div");

		this.folderList = new MWF.xApplication.Drive.FolderList(zoneNode,this.app,this, {
			folderId : "-1",
			type : "all"
		});
		this.folderList.init();
		this.folderList.load();

		this.zoneDlg = o2.DL.open({
			"title": "保存到",
			"style": "user",
			"isResize": false,
			"content": zoneNode,
			"maskNode": this.app.content,
			"container" : this.app.content,
			"minTop": 5,
			"width": "600",
			"height": "400",
			"buttonList": [
				{
					"type": "ok",
					"text": "确认",
					"action": function (d, e) {

						var folderId = _self.folderList.folderId==="-1"?"(0)":_self.folderList.folderId;

						var dataList = this.explorer.selectedList;
						var attIdList = [];
						var folderIdList = [];


						dataList.each(function (d){
							if(d.type === "folder"){
								folderIdList.push(d.id);
							}else {
								attIdList.push(d.id);
							}
						})

						_self.action.Folder3Action.saveToPerson(folderId,{
							"attIdList" : attIdList,
							"folderIdList" : folderIdList
						} ,function(){
							_self.app.notice("保存成功");
							_self.explorer.refresh();
							_self.zoneDlg.close();
							zoneNode.destroy();
						});

					}.bind(this)
				},
				{
					"type": "cancel",
					"text": "取消",
					"action": function () {
						this.zoneDlg.close();
						zoneNode.destroy();
						//_self.content.unmask();
					}.bind(this)
				}
			],
			"onPostLoad": function () {

			}
		});
	},
	saveToZone : function (){
		var _self = this;
		var zoneNode = new Element("div");

		this.folderList = new MWF.xApplication.Drive.FolderList(zoneNode,this.app,this, {
			folderId : "-1",
			type : "zone"
		});
		this.folderList.init();
		this.folderList.load();

		this.zoneDlg = o2.DL.open({
			"title": "保存到",
			"style": "user",
			"isResize": false,
			"content": zoneNode,
			"maskNode": this.app.content,
			"container" : this.app.content,
			"minTop": 5,
			"width": "600",
			"height": "400",
			"buttonList": [
				{
					"type": "ok",
					"text": "确认",
					"action": function (d, e) {

						var folderId = _self.folderList.folderId==="-1"?"(0)":_self.folderList.folderId;

						var dataList = this.explorer.selectedList;
						var attIdList = [];
						var folderIdList = [];


						dataList.each(function (d){
							if(d.type === "folder"){
								folderIdList.push(d.id);
							}else {
								attIdList.push(d.id);
							}
						})

						_self.action.Folder2Action.saveToZone(folderId,{
							"attIdList" : attIdList,
							"folderIdList" : folderIdList
						} ,function(){
							_self.app.notice("保存成功");
							_self.explorer.refresh();
							_self.zoneDlg.close();
							zoneNode.destroy();
						});

					}.bind(this)
				},
				{
					"type": "cancel",
					"text": "取消",
					"action": function () {
						this.zoneDlg.close();
						zoneNode.destroy();
						//_self.content.unmask();
					}.bind(this)
				}
			],
			"onPostLoad": function () {

			}
		});
	},
	createFolder : function(){
		var form = new MWF.xApplication.Drive.FolderForm(this.explorer, {
		}, {}, {
			app: this.app
		});
		form.create()
	},
	createOffice : function(){
		var form = new MWF.xApplication.Drive.OfficeForm(this.explorer, {
		}, {}, {
			app: this.app
		});
		form.create()
	},
	cancelShare : function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "取消分享确认", "是否取消选中的"+dataList.length+"个文件的分享？", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){
				_self.action.ShareAction.delete( data.id , function(){
					count++;
					if( dataList.length == count ){
						_self.app.notice("取消分享成功");
						_self.explorer.refresh();
					}
				});
			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	rename : function(){

		var _self = this;
		if (this.explorer.selectedList && this.explorer.selectedList.length){
			var data = this.explorer.selectedList[0];
			var form = new MWF.xApplication.Drive.ReNameForm(this.explorer, data, {
			}, {
				app: this.app
			});
			form.edit()
		}else {
			this.app.notice("请先选择文件","error");
			return;
		}

	},
	recycle : function( e ){
		var _self = this;
		if (this.explorer.selectedList && this.explorer.selectedList.length){
			var dataList = this.explorer.selectedList;
			this.app.confirm("warn", e, "删除文件确认", "是否删除选中的"+dataList.length+"个文件？删除的文件会放到回收站。", 350, 120, function () {
				var count = 0;
				dataList.each( function(data){

					if(_self.explorer.type === "zone" || _self.explorer.type === "manageFile"){
						_self.action[data.type==="folder"?"Folder3Action":"Attachment3Action"].delete( data.id , function(){
							count++;
							if( dataList.length == count ){
								_self.app.notice("成功删除"+count+"个文件，您可以从回收站找到文件。");
								_self.explorer.refresh();
							}
						});
					}else{
						_self.action[data.type==="folder"?"Folder2Action":"Attachment2Action"].delete( data.id , function(){
							count++;
							if( dataList.length == count ){
								_self.app.notice("成功删除"+count+"个文件，您可以从回收站找到文件。");
								_self.explorer.refresh();
							}
						});
					}
				}.bind(this));
				this.close();
			}, function () {
				this.close();
			});
		}else {
			this.app.notice("请先选择文件","error");
			return;
		}
	},
	delete : function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "删除文件确认", "是否删除选中的"+dataList.length+"个文件？删除的文件不能恢复。", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){
				_self.action.RecycleAction.delete( data.id , function(){
					count++;
					if( dataList.length == count ){
						_self.app.notice("成功删除"+count+"个文件。");
						_self.explorer.refresh();
					}
				});
			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	deleteCapacity : function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "删除确认", "是否删除选中的"+dataList.length+"的配置", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){
				_self.action.ConfigAction.deletePersonConfig( data.id , function(){
					count++;
					if( dataList.length == count ){
						_self.app.notice("成功删除");
						_self.explorer.refresh();
					}
				});
			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	restore : function(e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "恢复文件确认", "是否恢复选中的"+dataList.length+"个文件？", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){
				_self.action.RecycleAction.resume( data.id ,{}, function(){
					count++;
					if( dataList.length == count ){
						_self.app.notice("成功恢复"+count+"个文件。");
						_self.explorer.refresh();
					}
				});
			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	clear : function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "清空回收站确认", "是否清空回收站？清空后文件不能恢复。", 350, 120, function () {
			_self.action.RecycleAction.empty(function(){
				_self.explorer.refresh();
			});
			this.close();
		}, function () {
			this.close();
		});
	},
	getListType : function(){
		return this.viewType || this.options.viewType
	},
	share : function(){


		if (this.explorer.selectedList && this.explorer.selectedList.length){
			var data = this.explorer.selectedList;
			var form = new MWF.xApplication.Drive.ShareForm(this.explorer, {}, {
			}, {
				app: this.app
			});
			form.checkedItemData = data;
			form.edit();
		}else {
			this.app.notice("请先选择文件","error");
			return;
		}



	},
	upload : function (){

		var folderId = this.explorer.folderId;
		if(folderId === "-1") folderId = "(0)";

		o2.Actions.get("x_pan_assemble_control").action.actions = {};
		var json = {
			"enctype": "formData",
			"method": "POST",
			"uri": "/jaxrs/attachment2/upload/folder/{folder}"
		}
		o2.Actions.get("x_pan_assemble_control").action.actions.upload = json;

		if(this.type === "zone"){
			json.uri = "/jaxrs/attachment3/upload/folder/{folder}"
		}


		var upload = new o2.widget.Upload(this.app.content, {
			"action": o2.Actions.get("x_pan_assemble_control").action,
			"method": "upload",
			"parameter": {
				"folder": folderId,
				"fileMd5": false
			},
			"onBeforeUpload" : function(files,obj){
				for (var i = 0; i < files.length; i++) {
					var file = files.item(i);
					this.action[this.type === "zone"?"Attachment3Action":"Attachment2Action"].checkFileUpload({
						"fileSize": file.size,
						"fileName": file.name
					}, function (json) {

					}.bind(this),  function (){
						obj.isContinue = false;
					}.bind(this), false);
				}

				if(!layout.desktop.inBrowser){
					layout.desktop.showMessage();
				}
			}.bind(this),
			"onCompleted": function(){
				this.explorer.refresh();
			}.bind(this)
		});
		upload.load();
	},
	uploadFolder : function (){

		var folderId = this.explorer.folderId;
		if(folderId === "-1") folderId = "(0)";

		o2.Actions.get("x_pan_assemble_control").action.actions = {};
		var json = {
			"enctype": "formData",
			"method": "POST",
			"uri": "/jaxrs/attachment2/upload/folder/{folder}"
		}
		o2.Actions.get("x_pan_assemble_control").action.actions.upload = json;

		if(this.type === "zone"){
			json.uri = "/jaxrs/attachment3/upload/folder/{folder}"
		}


		var upload = new o2.widget.Upload(this.app.content, {
			"action": o2.Actions.get("x_pan_assemble_control").action,
			"webkitdirectory" : true,
			"method": "upload",
			"parameter": {
				"folder": folderId,
				"fileMd5": false
			},
			"onBeforeUpload" : function(files,obj){
				for (var i = 0; i < files.length; i++) {
					var file = files.item(i);
					console.log(file)
					this.action[this.type === "zone"?"Attachment3Action":"Attachment2Action"].checkFileUpload({
						"fileSize": file.size,
						"fileName": file.name
					}, function (json) {

					}.bind(this),  function (){
						obj.isContinue = false;
					}.bind(this), false);

					// if(file.name.startsWith('.')){
					// 	obj.isContinue = false;
					// }
				}

				if(!layout.desktop.inBrowser){
					layout.desktop.showMessage();
				}
			}.bind(this),
			"onCompleted": function(){
				this.explorer.refresh();
			}.bind(this)
		});
		upload.load();
	},
	shareDownload : function (){
		var _self = this;
		var data = this.explorer.selectedList[0];

		var url = o2.Actions.getHost( "x_portal_assemble_surface" ) + "/x_pan_assemble_control/jaxrs/share/download/share/{shareId}/file/{fileId}";

		if(data.fileId){
			url = url.replace("{shareId}", data.id);
			url = url.replace("{fileId}", data.fileId);

		}else {
			url = url.replace("{shareId}", this.explorer.shareId);
			url = url.replace("{fileId}", data.id);
		}


		window.open(o2.filterUrl(url));


	},
	shareShield : function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "屏蔽分享确认", "屏蔽的分享文件无法恢复！是否屏蔽选中的"+dataList.length+"个文件？", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){
				_self.action.ShareAction.shield( data.id , function(){
					count++;
					if( dataList.length == count ){
						_self.app.notice("成功屏蔽"+count+"个分享文件");
						_self.explorer.refresh();
					}
				});
			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	download : function (){

		var _self = this;
		var dataList = this.explorer.selectedList;
		var attIds = [];
		var folderIds = [];

		dataList.each(function(data){

			if(data.type === "folder"){
				folderIds.push(data.id);
			}else{
				attIds.push(data.id);
			}
		}.bind(this));
		if(attIds.length ===1 && folderIds.length === 0){
			var url = o2.Actions.getHost( "x_pan_assemble_control" ) + "/x_pan_assemble_control/jaxrs/"+(_self.type==="zone"|| _self.type === "manageFile"?"attachment3":"attachment2")+"/{id}/download/stream";
			url = url.replace("{id}", attIds[0]);
			window.open(o2.filterUrl(url));
		}else if(attIds.length ===0 && folderIds.length === 1){

			var url = o2.Actions.getHost( "x_portal_assemble_surface" ) + "/x_pan_assemble_control/jaxrs/"+(_self.type==="zone"|| _self.type === "manageFile"?"folder3":"folder2")+"/{id}/download";
			url = url.replace("{id}", folderIds[0]);
			window.open(o2.filterUrl(url));

		}else{
			var url = o2.Actions.getHost( "x_portal_assemble_surface" ) + "/x_pan_assemble_control/jaxrs/"+(_self.type==="zone"|| _self.type === "manageFile"?"folder3":"folder2")+"/batch/download?";

			var attIdList = [];
			var folderIdList = [];
			if(attIds.length>0){
				attIds.each(function(attId){
					attIdList.push("&attIds="+attId);
				})
			}
			if(folderIds.length>0){
				folderIds.each(function(folderId){
					folderIdList.push("&folderIds="+folderId);
				})
			}
			url = url + attIdList.join("");
			url = url + folderIdList.join("");

			window.open(o2.filterUrl(url));
		}
	},
});

MWF.xApplication.Drive.PersonCapacityList = new Class({
	Extends: MWF.xApplication.Drive.List,
	init : function (){


		this.titleTempleteUrl = this.app.path+this.app.options.style+"/view/manage/personCapacity/list_title.html";
		this.listTempleteUrl = this.app.path+this.app.options.style+"/view/manage/personCapacity/" +this.options.defaultViewType + ".html";

		var rootPathNode = new Element("span",{"text":"个人容量"}).inject(this.pathNode);

	},
	_initToolBar : function (){

		this.toolbarItems = {
			"unSelect":[
				["addCapacity"]
			],
			"selected":[
				["editCapacity"],["deleteCapacity"]
			],
			"mulSelect":[
				["deleteCapacity"]
			]
		}

	},
	loadData : function (){
		var _self = this;
		return _self.action.ConfigAction.listWithFilter(1,200,{}).then(function(json){
			_self.fireEvent("loadData");
			return json.data;
		});
	},
	bindThumb : function (){

	}

});
MWF.xApplication.Drive.ManageFileList = new Class({
	Extends: MWF.xApplication.Drive.List,
	init : function (){

		this.titleTempleteUrl = this.app.path+this.app.options.style+"/view/"+this.app.currentNaviType+"/manageFile/list_title.html";
		this.listTempleteUrl = this.app.path+this.app.options.style+"/view/"+this.app.currentNaviType+"/manageFile/" +this.options.defaultViewType + ".html";
		var rootPathNode = new Element("span",{"text":"公共文件"}).inject(this.pathNode);

	},

	_initToolBar : function (){

		this.toolbarItems = {
			"unSelect":[

			],
			"selected":[
				["download"],
				["recycle"]
			],
			"mulSelect":[
				["download"],
				["recycle"]
			]
		}
	},

	loadData : function (){
		var _self = this;
		return _self.action.Attachment3Action.managerListWithFilter(1,200,{}).then(function(json){
			_self.fireEvent("loadData");
			return _self._fixData(json.data);
		});
	},
	bindThumb: function(){

		this.dataList.each(function(d){

			if(d.fileType === "img"){
				var node = this.content.getElement("." + d.id);
				this.action["Attachment3Action"].getImageWidthHeightBase64(d.id,120,120,function (json){
					node.set("src","data:image/"+d.extension+';base64,'+ json.data.value)
				});
			}
		}.bind(this))

	},

});
MWF.xApplication.Drive.ManageZoneList = new Class({
	Extends: MWF.xApplication.Drive.List,
	init : function (){


		this.titleTempleteUrl = this.app.path+this.app.options.style+"/view/manage/manageZone/list_title.html";
		this.listTempleteUrl = this.app.path+this.app.options.style+"/view/manage/manageZone/" +this.options.defaultViewType + ".html";

		var rootPathNode = new Element("span",{"text":"共享工作区"}).inject(this.pathNode);

	},

	_initToolBar : function (){

		this.toolbarItems = {
			"unSelect":[

			],
			"selected":[
				["editZone"],
				["setZoneAcl"],
				["deleteZone"],
				["resetZoneAcl"],
			],
			"mulSelect":[
				["deleteZone"]
			]
		}
	},
	loadData : function (){
		var _self = this;
		return _self.action.ZoneAction.managerList().then(function(json){
			_self.fireEvent("loadData");
			return _self._fixData(json.data);
		});
	},
	_fixData : function (dataList){
		dataList.each(function (data){

			data.usedCapacity = this.util.getFileSize(data.usedCapacity);

		}.bind(this));
		return dataList;
	},
	bindThumb : function (){

	}

});
MWF.xApplication.Drive.AttachmenPreview = new Class({
	Implements: [Options, Events],

	initialize : function(att,app ){
		this.att = att;
		this.app = app;

		this.action = this.app.action;
		this.load();
	},
	load:function(){

		var extension = this.att.extension;

		if(this.att.type === "movie"){
			this.previewVedio("movie");
		}
		if(this.att.type === "music"){
			this.previewVedio("music");
		}
		if(extension === "ofd"){
			//ofd预览暂时屏蔽ie，等兼容性改好了开启
			if(Browser.name!=="ie"){
				this.previewOfd();
			}
		}
		if(extension === "dwg"){
			if(Browser.name!=="ie"){
				this.previewCad();
			}
		}
		if(extension === "zip"){
			this.previewZip();
		}
		if(extension === "pdf"){
			this.previewPdf();
		}
		if(["doc","docx","xls","xlsx","ppt","pptx"].contains(extension)){

			this.action.ConfigAction.isEnableOfficePreview().then(function (json){
				if(json.data.value){

					this.isOpenOfficeEdit = json.data.isOpenOfficeEdit;

					if(this.att.fileId){

					}else{
						if(json.data.value === "onlyoffice"){
							this.previewOnlyOffice();
						}
						if(json.data.value === "wpsoffice"){
							this.previewWpsOffice();
						}
						if(json.data.value === "libreoffice"){
							this.previewOffice();
						}
						if(json.data.value === "officeonline"){
							this.previewOfficeOnline();
						}

					}

				}
			}.bind(this));

		}
		if(["png","jpg","bmp","jpeg","gif"].contains(extension)){
			this.previewImage();
		}
		if(extension === "js"){
			this.previewAce("javascript");
		}
		if(extension === "css"){
			this.previewAce("css");
		}
		if(extension === "java"){
			this.previewAce("java");
		}
		if(extension === "json"){
			this.previewAce("json");
		}
		if(extension === "xml"){
			this.previewAce("xml");
		}
		if(extension === "php"){
			this.previewAce("php");
		}
		if(["html","htm","xhtml"].contains(extension)){
			this.previewAce("html");
		}
		if(["log","md","txt"].contains(extension)){
			this.previewAce("text");
		}
	},
	previewVedio:function(type){

		var x = "960px";
		var y = "610px";
		if(type === "music"){
			x = "600px";
			y = "120px";
		}
		var dplayerNode = new Element("div",{"style":"height:100%;width:100%;"});
		this.app.getAttachmentUrl(this.att, function (url) {
			o2.loadCss("../x_component_Drive/lib/DPlayer.min.css",dplayerNode,function(){
				o2.load("/x_component_Drive/lib/DPlayer.min.js", function(){
					this.dPlayer = new DPlayer({
						container: dplayerNode,
						autoplay:true,
						video: {
							url: url
						},
					});
					var dlg = o2.DL.open({
						"title": this.att.name,
						"width": x,
						"height": y,
						"isMax" : type === "music"?false:true,
						"mask": true,
						"content": dplayerNode,
						"container": document.body,
						"positionNode": document.body,
						"onQueryClose": function () {
							dplayerNode.destroy();
						}.bind(this),
						"onPostShow": function () {
							dlg.reCenter();
						}.bind(this)
					});
				}.bind(this));
			}.bind(this));
		}.bind(this));

	},
	previewZip: function () {

		//zip压缩包预览
		var _self = this;
		var zipViewNode = new Element("div",{"text":"loadding..."});
		o2.load(["../o2_lib/jszip/jszip.min.js", "../o2_lib/jszip/jszip-utils.min.js"], function () {
			this.app.getAttachmentUrl(this.att, function (url) {
				o2.require("MWF.widget.Tree", function(){
					var dlg = o2.DL.open({
						"title": _self.att.name,
						"width": "660px",
						"height": "510px",
						"mask": true,
						"content": zipViewNode,
						"container": null,
						"positionNode": document.body,
						"onQueryClose": function () {
							zipViewNode.destroy();
						},
						"buttonList": [
							{
								"text": "关闭",
								"action": function () {
									dlg.close();
								}
							}
						],
						"onPostShow": function () {
							dlg.reCenter();
						},
						"onPostLoad" : function(){

						}
					});
				}.bind(this));
				zipViewNode.empty();
				JSZipUtils.getBinaryContent(url, function (err, data) {
					JSZip.loadAsync(data).then(function (zip) {
						var nodeList = [];
						zip.forEach(function (relativePath, zipEntry) {
							nodeList.push(zipEntry.name);
						});
						var tree = new MWF.widget.Tree(zipViewNode, {"style":"form"});
						var treeData = _pathToTree(nodeList);
						tree.load(treeData);


					});
				});

			}.bind(this));
		}.bind(this));
		function _pathToTree(pathList) {
			var pathJsonList = [];
			for (var i = 0; i < pathList.length; i++) {
				var chain = pathList[i].split("/");
				var currentNode = pathJsonList;
				for (var j = 0; j < chain.length; j++) {
					if (chain[j] === "") {
						break;
					}
					var wantedNode = chain[j];
					var lastNode = currentNode;
					for (var k = 0; k < currentNode.length; k++) {
						if (currentNode[k].name == wantedNode) {
							currentNode = currentNode[k].sub;
							break;
						}
					}
					if (lastNode == currentNode) {
						var obj = {
							key: pathList[i],
							name: wantedNode,
							title:wantedNode,
							text:wantedNode,
							sub: []
						};
						var newNode = (currentNode[k] = obj);
						if (wantedNode.indexOf(".") > -1) {
							obj.dir = false;
							obj.icon = "file.png";
							delete obj.sub;
						} else {
							obj.dir = true;
							obj.expand = false;
							currentNode = newNode.sub;
							//delete obj.sub;
						}
					} else {
						delete currentNode.sub;
					}
				}
			}
			var nodes = [];

			var folder = {
				"title" : _self.att.name,
				"text" : _self.att.name,
				"sub" : []
			};
			pathJsonList.each(function(path){
				folder.sub.push(path);
			})
			_sortPath(folder, nodes);
			return nodes;
		}
		function _sortPath(pathJsonList, nodes) {
			var folderList = [];
			pathJsonList.sub.each(function (file) {
				if (file.dir) {
					folderList.push(file);
				}
			});
			pathJsonList.sub.each(function (file) {
				if (!file.dir) {
					folderList.push(file);
				}
			});
			folderList.each(function (file) {
				var node = {
					text: file.name,
					title: file.name,
					expand : false
				};
				if (!file.dir) {
					node.icon = "file.png";
				}
				nodes.push(node);
				if(file.sub && file.sub.length>0){
					node.sub = [];
					_sortPath(file,node.sub);
				}

			})
		}
	},
	previewPdf : function(){
		this.app.getAttachmentUrl(this.att, function (url) {
			url = url.replace("/stream","");
			url = "../o2_lib/pdfjs/web/viewer.html?file=" + encodeURIComponent(url);

			const options = {
				"fileUrl": url,
				"fileName" : this.att.name,
				"appId": "PdfViewer" + this.att.id
			};
			layout.openApplication(null, "PdfViewer", options);


		}.bind(this));
	},
	previewCad : function (){
		this.app.getAttachmentUrl(this.att, function (url) {
			window.open("../cadviewer/index.html?file=" + url)
		});
	},
	previewOffice : function(){
		this.app.getPreviewvAttachmentUrl(this.att,  function (url) {
			window.open("../o2_lib/pdfjs/web/viewer.html?file=" + url);
		});
	},
	previewOnlyOffice : function (){

		var options = {
			"documentId": this.att.id,
			"mode": this.isOpenOfficeEdit?"edit":"view",
			"jars" : "x_pan_assemble_control",
			"appId":  "OnlyOfficeEditor" + this.att.id
		};
		layout.desktop.openApplication(null, "OnlyOfficeEditor", options);

	},

	previewWpsOffice : function (){

		var options = {
			"documentId": this.att.id,
			"mode":"view",
			"jars" : "x_pan_assemble_control",
			"appId":  "WpsOfficeEditor" + this.att.id
		};
		layout.desktop.openApplication(null, "WpsOfficeEditor", options);

	},
	previewOfficeOnline : function (){

		var options = {
			"documentId": this.att.id,
			"mode":"view",
			"appId":  "OfficeOnlineEditor" + this.att.id
		};
		layout.desktop.openApplication(null, "OfficeOnlineEditor", options);

	},
	previewOfd : function(){
		this.app.getAttachmentUrl(this.att,  function (url) {
			window.open("../o2_lib/ofdjs/index.html?file=" + url)
		});
	},
	previewImage : function(){
		var curImg ;
		var imgContainerNode = new Element("div").inject(document.body).hide();
		this.app.dataList.each(function(att){
			if(["png","jpg","bmp","jpeg","gif"].contains(att.extension)){
				this.app.getAttachmentUrl(att, function (url) {
					var imgNode = new Element("img",{"src":url,"alt":att.name}).inject(imgContainerNode);
					if(att.id === this.att.id){
						curImg = imgNode;
					}
				}.bind(this));
			}
		}.bind(this));
		o2.loadCss("../o2_lib/viewer/viewer.css", document.body,function(){
			o2.load("../o2_lib/viewer/viewer.js", function(){
				this.viewer = new Viewer(imgContainerNode,{
					navbar : true,
					toolbar : true,
					hidden : function(){
						imgContainerNode.destroy();
						this.viewer.destroy();
					}.bind(this)
				});

				//this.viewer.show(curImg);
				curImg.click();
			}.bind(this));
		}.bind(this));
		//
		//
		//
		//
		// this.app.getAttachmentUrl(this.att, function (url) {
		// 	var imgNode = new Element("img",{"src":url,"alt":this.att.name}).inject(document.body).hide();
		// 	o2.loadCss("../o2_lib/viewer/viewer.css", document.body,function(){
		// 		o2.load("../o2_lib/viewer/viewer.js", function(){
		// 			this.viewer = new Viewer(imgNode,{
		// 				navbar : false,
		// 				toolbar : false,
		// 				hidden : function(){
		// 					imgNode.destroy();
		// 					this.viewer.destroy();
		// 				}.bind(this)
		// 			});
		// 			this.viewer.show();
		// 		}.bind(this));
		// 	}.bind(this));
		// }.bind(this));
	},
	previewAce:function(type){

		this.app.getAttachmentUrl(this.att,  function (url) {
			o2.require("o2.widget.ace", null, false);
			var fileRequest = new Request({
				url: url,
				method: 'get',
				withCredentials: true,
				onSuccess: function(responseText){
					var editorNode = new Element("div",{"style":"padding:10px"});
					editorNode.set("text",responseText);

					o2.widget.ace.load(function(){
						o2.load("../o2_lib/ace/src-min-noconflict/ext-static_highlight.js", function(){
							var highlight = ace.require("ace/ext/static_highlight");
							highlight(editorNode, {mode: "ace/mode/"+ type , theme: "ace/theme/tomorrow", "fontSize": 30,"showLineNumbers":true});
						}.bind(this));

					}.bind(this));
					var dlg = o2.DL.open({
						"title": this.att.name,
						"width": "960px",
						"height": "610px",
						"mask": true,
						"content": editorNode,
						"container": null,
						"positionNode": document.body,
						"onQueryClose": function () {
							editorNode.destroy();
						}.bind(this),
						"buttonList": [
							{
								"text": "关闭",
								"action": function () {
									dlg.close();
								}.bind(this)
							}
						],
						"onPostShow": function () {
							dlg.reCenter();
						}.bind(this)
					});
				}.bind(this),
				onFailure: function(){
					console.log('text', 'Sorry, your request failed :(');
				}
			});
			fileRequest.send();
		}.bind(this));

	},
});
MWF.xAction.RestActions.Action["x_pan_assemble_control"] = new Class({
	Extends: MWF.xAction.RestActions.Action
});
MWF.xApplication.Drive.ReNameForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		"style": "attendanceV2",
		"width": 700,
		//"height": 300,
		"height": "200",
		"hasTop": true,
		"hasIcon": false,
		"draggable": true,
		"title" : "重命名",
		"id" : ""
	},
	_createTableContent: function () {

		var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
			"<tr><td styles='formTableTitle' lable='name' width='25%'></td>" +
			"    <td styles='formTableValue14' item='name' colspan='3'></td></tr>" +
			"</table>";
		this.formTableArea.set("html", html);

		this.form = new MForm(this.formTableArea, this.data || {}, {
			isEdited: true,
			style : "minder",
			hasColon : true,
			itemTemplate: {
				name: { text : "名称", notEmpty : true }
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
			"styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
			"text": "关闭"
		}).inject(this.formBottomNode);

		this.cancelActionNode.addEvent("click", function (e) {
			this.close(e);
		}.bind(this));

	},
	save: function(){


		var data = this.form.getResult(true,null,true,false,true);
		if( data ){
			if(data.fileType === "folder"){
				this.app.action[this.explorer.type !== "zone"?"Folder2Action":"Folder3Action"][this.explorer.type !== "zone"?"put":"updateName"](data.id,{
					name : data.name
				}).then(function (){
					this.app.notice("重命名成功");
					this.explorer.refresh();
					this.close();
				}.bind(this));
			}else {
				this.app.action[this.explorer.type !== "zone"?"Attachment2Action":"Attachment3Action"][this.explorer.type !== "zone"?"update":"updateName"](data.id,{
					name : data.name
				}).then(function (){
					this.app.notice("重命名成功");
					this.explorer.refresh();
					this.close();
				}.bind(this));
			}
		}
	}
});
MWF.xApplication.Drive.FolderForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		style : "attendanceV2",
		"width": layout.mobile?'100%':700,
		//"height": 300,
		"height": "200",
		"hasTop": true,
		"hasIcon": false,
		"draggable": true,
		"title" : "新建文件夹"
	},
	_createTableContent: function () {

		var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
			"<tr><td styles='formTableTitle' lable='name' width='25%'></td>" +
			"    <td styles='formTableValue14' item='name' colspan='3'></td></tr>" +
			"</table>";
		this.formTableArea.set("html", html);

		this.form = new MForm(this.formTableArea, this.data || {}, {
			isEdited: true,
			style : "minder",
			hasColon : true,
			itemTemplate: {
				name: { text : "名称", notEmpty : true }
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
			"styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
			"text": "关闭"
		}).inject(this.formBottomNode);

		this.cancelActionNode.addEvent("click", function (e) {
			this.close(e);
		}.bind(this));

	},
	save: function(){
		var data = this.form.getResult(true,null,true,false,true);
		if( data ){

			data.superior = this.explorer.folderId;
			if(this.explorer.folderId === "-1"){
				data.superior = "";
			}
			this.explorer.action[this.explorer.type==="all"?"Folder2Action":"Folder3Action"].create( data, function( json ){
				this.explorer.refresh();
				this.close();
			}.bind(this));
		}
	}
});
MWF.xApplication.Drive.OfficeForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		style : "attendanceV2",
		"width": 700,
		//"height": 300,
		"height": "200",
		"hasTop": true,
		"hasIcon": false,
		"draggable": true,
		"title" : "新建在线文档"
	},
	_createTableContent: function () {

		var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
			"<tr><td styles='formTableTitle' lable='name' width='25%'></td>" +
			"    <td styles='formTableValue14' item='name' width='50%'></td>" +
			"    <td styles='formTableValue14' item='type' width='25%'></td>" +
			"</tr>" +
			"</table>";
		this.formTableArea.set("html", html);

		this.form = new MForm(this.formTableArea, this.data || {}, {
			isEdited: true,
			style : "minder",
			hasColon : true,
			itemTemplate: {
				name: { text : "名称", notEmpty : true },
				type: {
					"text": "类型",
					"type": "select",
					"style": {"min-width": "100px"},
					"selectText": ["", "Word","Excel", "PPT"],
					"selectValue": ["", "docx","xlsx", "pptx"],
					"notEmpty" : true
				}
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
			"styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
			"text": "关闭"
		}).inject(this.formBottomNode);

		this.cancelActionNode.addEvent("click", function (e) {
			this.close(e);
		}.bind(this));

	},
	save: function(){
		var data = this.form.getResult(true,null,true,false,true);
		if( data ){
			data.folderId = this.explorer.folderId;
			if(this.explorer.folderId === "-1"){
				data.folderId = "(0)";
			}
			this.explorer.action[this.explorer.type !== "zone"?"Attachment2Action":"Attachment3Action"].createOfficeFile( data.folderId,data.name + "." + data.type, {},function( json ){
				this.explorer.refresh();
				this.close();
			}.bind(this));
		}
	}
});
MWF.xApplication.Drive.ShareForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		style : "attendanceV2",
		"width": 700,
		//"height": 300,
		"height": "400",
		"hasTop": true,
		"hasIcon": false,
		"draggable": true,
		"title" : "网盘分享"
	},
	_createTableContent: function () {

		var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
			"<tr><td styles='formTableTitle' lable='fileName' width='18%'></td>" +
			"    <td styles='formTableValue14' item='fileName'></td></tr>" +
			"<tr><td styles='formTableTitle' lable='shareTo' width='18%'></td>" +
			"    <td styles='formTableValue14' item='shareTo'></td></tr>" +
			"<tr><td styles='formTableTitle' width='18%'></td>" +
			"    <td styles='formTableValue14'>分享文件给其他人</td></tr>" +
			"</table>";
		this.formTableArea.set("html", html);

		this.form = new MForm(this.formTableArea, this.data || {}, {
			isEdited: true,
			style : "minder",
			hasColon : true,
			itemTemplate: {
				fileName : { text : "文件名称", type : "innerHTML", value : function(){
						var name = [];
						this.checkedItemData.each( function(d){
							name.push(d.name );
						});
						return name.join("<br>");
					}.bind(this)},
				shareTo: { type : "org", orgType:["person","unit","group"],text : "分享对象", notEmpty : true, count : 0, style : {
						"min-height" : "100px"
					} }
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
				this.share(e);
			}.bind(this));
		}

		this.cancelActionNode = new Element("button.inputCancelButton", {
			"styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
			"text": "关闭"
		}).inject(this.formBottomNode);

		this.cancelActionNode.addEvent("click", function (e) {
			this.close(e);
		}.bind(this));

	},
	share: function(){
		var data = this.form.getResult(true,null,true,false,true);
		if( data ){
			var json = {
				shareType : "member",
				shareUserList : [],
				shareOrgList : [],
				shareGroupList : []
			};
			data.shareTo.each( function( s ){
				var flag = s.substr(s.length-1, 1);
				switch (flag.toLowerCase()){
					case "p":
						json.shareUserList.push( s );
						break;
					case "u":
						json.shareOrgList.push( s );
						break;
					case "g":
						json.shareGroupList.push( s );
						break;
					default :
						break;
				}
			}.bind(this));
			var count = 0;

			this.checkedItemData.each( function(d){
				json.fileId = d.id;
				this.app.action.ShareAction.create( json, function(){
					count++;
					if( count ==  this.checkedItemData.length){
						this.app.notice( "分享成功！" );
						this.close();
					}
				}.bind(this));
			}.bind(this));
		}
	}
});
MWF.xApplication.Drive.Util = new Class({
	getFileSize : function( size ){
		if (!size)
			return "-";
		var num = 1024.00; //byte
		if (size < num)
			return size + "B";
		if (size < Math.pow(num, 2))
			return (size / num).toFixed(2) + "K"; //kb
		if (size < Math.pow(num, 3))
			return (size / Math.pow(num, 2)).toFixed(2) + "M"; //M
		if (size < Math.pow(num, 4))
			return (size / Math.pow(num, 3)).toFixed(2) + "G"; //G
		return (size / Math.pow(num, 4)).toFixed(2) + "T";
	},
	getFileExtension : function(extension){

		var extensionObj = {
			"excel":["xls","xlsx"],
			"exe":["exe"],
			"js":["js"],
			"folder":["folder"],
			"html":["html"],
			"css":["css"],
			"word":["doc","docx"],
			"file":["md","conf"],
			"img":["bmp", "gif", "png", "jpeg", "jpg", "jpe"],
			"ppt":["ppt","pptx"],
			"rar":["rar","7z","zip"],
			"music":["mp3", "wav", "wma"],
			"txt":["txt"],
			"pdf":["pdf"],
			"vedio":["avi", "mkv", "mov", "ogg", "mp4", "mpa", "mpe", "mpeg", "mpg", "rmvb", "wmv"]
		};
		for (var key in extensionObj){
			if (extensionObj[key].contains(extension)) {
				return key;
			}
		}
		return "other";
	}
});

o2.require("o2.widget.Upload", null, false);
o2.widget.Upload.implement({
	formData_CreateUploadArea: function(){
		if (!this.uploadFileAreaNode){
			this.uploadFileAreaNode = new Element("div");
			var html = null;
			if(this.options.webkitdirectory){
				html = "<input webkitdirectory name=\"file\" "+((this.options.multiple) ? "multiple": "")+" type=\"file\" accept=\"" +  this.options.accept  + "\"/>";

			}else {
				html = "<input  name=\"file\" "+((this.options.multiple) ? "multiple": "")+" type=\"file\" accept=\"" +  this.options.accept  + "\"/>";

			}
			this.uploadFileAreaNode.set("html", html);
			this.fileUploadNode = this.uploadFileAreaNode.getFirst();

			this.fileUploadNode.addEvent("change", this.formData_Upload.bind(this));
		}
	},
	formData_Upload: function(){
		var files = this.fileUploadNode.files;


		if (files.length){
			var count = files.length;
			var current = 0;

			this.isContinue = true;
			this.fireEvent("beforeUpload", [files, this]);

			var uploadBack = function(json){
				if (current == count) this.fireEvent("completed", [json]);
			}.bind(this);

			var uploadSingle = function(file){
				this.fireEvent("beforeUploadEntry", [file, this]);

				var formData = new FormData();
				Object.each(this.options.data, function(v, k){
					formData.append(k, v)
				});
				formData.append('file', file);


				if(file.webkitRelativePath){
					var path = file.webkitRelativePath;
					path = path.substring(0,path.lastIndexOf("/"));
					formData.append('subFolderPath', path);
				}
				this.action.invoke({
					"name": this.options.method,
					"async": true,
					"data": formData,
					"file": file,
					"parameter": this.options.parameter,
					"success": function(json){
						current++;
						this.fireEvent("every", [json, current, count, file]);
						uploadBack(json);
					}.bind(this),
					"failure": function (xhr){
						current++;
						this.fireEvent("failure", [xhr, current, count, file]);
						uploadBack();
					}.bind(this)
				});
			}.bind(this);

			if (this.isContinue){
				for (var i = 0; i < files.length; i++) {
					var file = files.item(i);
					uploadSingle(file);


				}
			}
			this.uploadFileAreaNode.destroy();
			this.uploadFileAreaNode = null;
		}
	},
})
