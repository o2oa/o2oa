MWF.require("MWF.xApplication.Organization.GroupExplorer", null, false);
MWF.require("MWF.xApplication.Organization.OrgExplorer", null, false);
MWF.xApplication.Organization.PersonExplorer = new Class({
	Extends: MWF.xApplication.Organization.GroupExplorer,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	
	initialize: function(node, actions, options){
		this.setOptions(options);
		
		this.path = "/x_component_Organization/$PersonExplorer/";
		this.cssPath = "/x_component_Organization/$PersonExplorer/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.actions = actions;
		this.node = $(node);
		
		this.loaddingElement = false;
		this.groups = [];
		this.isElementLoaded = false;
		this.loadElementQueue = 0;
		
		this.deleteGroups = [];
	},

	loadElements: function(addToNext){
		if (!this.isElementLoaded){
			if (!this.loaddingElement){
				this.loaddingElement = true;
				this.actions.listPersonNext(this.getLastLoadedGroupId(), this.getPageNodeCount(), function(json){
					if (json.data.length){
						this.loadChartContent(json.data);
						this.loaddingElement = false;
						
						if (json.data.length<count){
							this.isElementLoaded = true;
							this.app.notice(this.app.lp.personLoaded, "ok", this.chartScrollNode, {"x": "center", "y": "bottom"});
						}else{
							if (this.loadElementQueue>0){
								this.loadElementQueue--;
								this.loadElements();
							}
						}
					}else{
						if (!this.groups.length){
							this.setNoGroupNoticeArea();
						}else{
							this.app.notice(this.app.lp.personLoaded, "ok", this.chartScrollNode, {"x": "center", "y": "bottom"});
						}
						this.isElementLoaded = true;
						this.loaddingElement = false;
					}
					
				}.bind(this));
			}else{
				if (addToNext) this.loadElementQueue++;
			}
		}
	},
	loadChartContent: function(data){
		data.each(function(itemData){
			var item = new MWF.xApplication.Organization.PersonExplorer.Person(itemData, this);
			this.groups.push(item);
			item.load();
		}.bind(this));
	},
	
	loadToolbar: function(){
		this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode}).inject(this.chartAreaNode);
		this.addTopGroupNode = new Element("div", {"styles": this.css.addTopGroupNode}).inject(this.toolbarNode);
		this.addTopGroupNode.addEvent("click", function(){
			this.addTopGroup();
		}.bind(this));
		this.createSearchNode();
	},
	
	searchOrg: function(){

		var key = this.searchInputNode.get("value");
		if (key){
			if (key!=this.app.lp.searchText){
				var isSearchGroup = true;
				if (this.currentItem) isSearchGroup = this.currentItem.unSelected();
				if (isSearchGroup){
					this.actions.listPersonByKey(function(json){
						if (this.currentItem) this.currentItem.unSelected();
						this.clear();
						json.data.each(function(itemData){
							var item = new MWF.xApplication.Organization.PersonExplorer.Person(itemData, this);
							item.load();
						}.bind(this));
					}.bind(this), null, key);
				}else{
					this.app.notice(this.app.lp.groupSave, "error", this.propertyContentNode);
				}
			}else{
				if (this.currentItem) isSearchGroup = this.currentItem.unSelected();
				if (isSearchGroup){
					this.clear();
					this.loadElements();
				}else{
					this.app.notice(this.app.lp.groupSave, "error", this.propertyContentNode);
				}
			}
		}else{
			if (this.currentItem) isSearchGroup = this.currentItem.unSelected();
			if (isSearchGroup){
				this.clear();
				this.loadElements();
			}else{
				this.app.notice(this.app.lp.groupSave, "error", this.propertyContentNode);
			}
		}
	},
	addTopGroup: function(){
		var isNewGroup = true;
		if (this.currentItem) isNewGroup = this.currentItem.unSelected();
		if (isNewGroup){
			var newGroupData = {
				"employee": "",
				"password": "",
				"display": "",
				"qq": "",
				"mail": "",
				"weixin": "",
				"weibo": "",
				"mobile": "",
				"name": ""
			};
			var item = new MWF.xApplication.Organization.PersonExplorer.Person(newGroupData, this);
			item.load();
			item.selected();
			item.editBaseInfor();
			
			(new Fx.Scroll(this.chartScrollNode)).toElement(item.node);
		}else{
			this.app.notice(this.app.lp.groupSave, "error", this.propertyContentNode);
		}
	},
	deleteSelectedGroups: function(e){
		var _self = this;
		this.app.confirm("infor", e, this.app.lp.deleteGroupsTitle, this.app.lp.deleteGroupsConfirm, 300, 120, function(){
			var deleted = [];
			var doCount = 0;

			_self.deleteGroups.each(function(group){
				group["delete"](function(){
					deleted.push(group);
					doCount++;
					if (_self.deleteGroups.length==doCount){
						_self.deleteGroups = _self.deleteGroups.filter(function(item, index){
							return !deleted.contains(item);
						});
						_self.checkDeleteGroups();
					}
				}, function(){
					doCount++;
					if (_self.deleteGroups.length==doCount){
						_self.deleteGroups = _self.deleteGroups.filter(function(item, index){
							return !deleted.contains(item);
						});
						_self.checkDeleteGroups();
					}
				});
			});
			this.close();
		}, function(){
			this.close();
		});
	}
	
});

MWF.xApplication.Organization.PersonExplorer.Person = new Class({
	Extends: MWF.xApplication.Organization.GroupExplorer.Group,
	
	initialize: function(data, explorer){
		this.data = data;
		
		this.explorer = explorer;
		this.chartNode = this.explorer.chartNode;
		this.initStyle();
		
		this.selectedAttributes = [];

		this.isEdit = false;
		this.deleteSelected = false;
	},
	
	
	showItemProperty: function(){
		this.explorer.propertyTitleNode.set("text", this.data.name);
		this.showItemPropertyBase();
		this.showItemPropertyAttribute();
		this.showItemPropertyIdentity();
	},
	
	showItemPropertyBase: function(){
		this.propertyBaseNode = new Element("div", {
			"styles": this.style.propertyInforNode
		}).inject(this.explorer.propertyContentNode);
		
		this.baseActionNode = new Element("div", {
			"styles": this.style.propertyInforActionNode
		}).inject(this.propertyBaseNode);
		this.propertyBaseTextNode = new Element("div", {
			"styles": this.style.propertyInforTextNode,
			"text": this.explorer.app.lp.personBaseText
		}).inject(this.propertyBaseNode);
		
		this.createEditBaseNode();
		
		this.propertyBaseContentNode = new Element("div", {
			"styles": this.style.propertyInforContentNode
		}).inject(this.propertyBaseNode);
		
		var html = "<table cellspacing='0' cellpadding='0' border='0' width='95%' align='center'>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personName+"</td><td id='formPersonName'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personEmployee+"</td><td id='formPersonEmployee'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personDisplay+"</td><td id='formPersonDisplay'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personMobile+"</td><td id='formPersonMobile'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personMail+"</td><td id='formPersonMail'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personQQ+"</td><td id='formPersonQQ'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personWeixin+"</td><td id='formPersonWeixin'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personWeibo+"</td><td id='formPersonWeibo'></td></tr>";
		html += "</table>";
		this.propertyBaseContentNode.set("html", html);
		this.propertyBaseContentNode.getElements("td.formTitle").setStyles(this.style.propertyBaseContentTdTitle);
		
		this.personNameInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonName"), this.data.name, this.explorer.css.formInput);
		this.personEmployeeInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonEmployee"), this.data.employee, this.explorer.css.formInput);
		this.personDisplayInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonDisplay"), this.data.display, this.explorer.css.formInput);
		this.personMobileInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonMobile"), this.data.mobile, this.explorer.css.formInput);
		this.personMailInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonMail"), this.data.mail, this.explorer.css.formInput);
		this.personQQInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonQQ"), this.data.qq, this.explorer.css.formInput);
		this.personWeixinInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonWeixin"), this.data.weixin, this.explorer.css.formInput);
		this.personWeiboInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonWeibo"), this.data.weibo, this.explorer.css.formInput);
	},
	editMode: function(){
		this.personNameInput.editMode();
		this.personEmployeeInput.editMode();
		this.personDisplayInput.editMode();
		this.personMobileInput.editMode();
		this.personMailInput.editMode();
		this.personQQInput.editMode();
		this.personWeixinInput.editMode();
		this.personWeiboInput.editMode();
		this.isEdit = true;
	},
	readMode: function(){
		this.personNameInput.readMode();
		this.personEmployeeInput.readMode();
		this.personDisplayInput.readMode();
		this.personMobileInput.readMode();
		this.personMailInput.readMode();
		this.personQQInput.readMode();
		this.personWeixinInput.readMode();
		this.personWeiboInput.readMode();
		this.isEdit = false;
	},
	
	saveBaseInfor: function(){
		if (!this.personNameInput.input.get("value") || !this.personEmployeeInput.input.get("value")){
			this.explorer.app.notice(this.explorer.app.lp.inputPersonInfor, "error", this.explorer.propertyContentNode);
			return false;
		}
		if (this.personDisplayInput.input.get("value")) this.data.display = this.personNameInput.input.get("value");
		this.propertyBaseNode.mask({
			"style": {
				"opacity": 0.7,
				"background-color": "#999"
			}
		});
		this.save(function(){
			this.baseActionNode.empty();
			this.cancelBaseNode = null;
			this.saveBaseNode = null;
			this.createEditBaseNode();
			
			this.readMode();
			
			this.propertyBaseNode.unmask();
		}.bind(this), function(xhr, text, error){
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			this.explorer.app.notice("request json error: "+errorText, "error");
			this.propertyBaseNode.unmask();
		}.bind(this));
	},
	
	save: function(callback, cancel){
		this.data.name = this.personNameInput.input.get("value");
		this.data.employee = this.personEmployeeInput.input.get("value");
		this.data.display = this.personDisplayInput.input.get("value");
		this.data.mobile = this.personMobileInput.input.get("value");
		this.data.mail = this.personMailInput.input.get("value");
		this.data.qq = this.personQQInput.input.get("value");
		this.data.weixin = this.personWeixinInput.input.get("value");
		this.data.weibo = this.personWeiboInput.input.get("value");

		this.explorer.actions.savePerson(this.data, function(json){

			this.textNode.set("text", this.data.name);
			this.data.id = json.data.id;

			this.personNameInput.save();
			this.personEmployeeInput.save();
			this.personDisplayInput.save();
			this.personMobileInput.save();
			this.personMailInput.save();
			this.personQQInput.save();
			this.personWeixinInput.save();
			this.personWeiboInput.save();
			
			if (callback) callback();
		}.bind(this), function(xhr, text, error){
			if (cancel) cancel(xhr, text, error);
		}.bind(this));
	},
	
	showItemPropertyAttribute: function(){
		this.propertyAttributeNode = new Element("div", {
			"styles": this.style.propertyInforNode
		}).inject(this.explorer.propertyContentNode);
		
		this.attributeActionNode = new Element("div", {
			"styles": this.style.propertyInforActionNode
		}).inject(this.propertyAttributeNode);
		this.propertyAttributeTextNode = new Element("div", {
			"styles": this.style.propertyInforTextNode,
			"text": this.explorer.app.lp.personAttributeText
		}).inject(this.propertyAttributeNode);
	//	this.createEditBaseNode();
		
		this.propertyAttributeContentNode = new Element("div", {
			"styles": this.style.propertyInforContentNode
		}).inject(this.propertyAttributeNode);

		this.createDeleteAttributeNode();
		this.createAddAttributeNode();
		
		this.listAttribute();
	},
	createAddAttributeNode: function(){
		this.addAttributeNode = new Element("button", {
			"styles": this.style.addDutyNode,
			"text": this.explorer.app.lp.add,
			"events": {"click": this.addAttribute.bind(this)}
		}).inject(this.attributeActionNode);
	},
	createDeleteAttributeNode: function(){
		this.deleteAttributeNode = new Element("button", {
			"styles": this.style.deleteDutyNode_desable,
			"text": this.explorer.app.lp["delete"],
			"disable": true
		}).inject(this.attributeActionNode);
	},
	addAttribute: function(){
		var data = this.getNewAttributeData();
		new MWF.xApplication.Organization.PersonAttribute(this.propertyAttributeContentNode.getElement("table").getFirst(), data, this, this.explorer.css.map);
	},
	getNewAttributeData: function(){
		return {
			"person": this.data.id,
			"name": "",
			"attributeList":[]
		};
	},
	checkDeleteAttributeAction: function(){

		if (this.selectedAttributes.length){
			if (this.deleteAttributeNode.get("disable")){
				this.deleteAttributeNode.set({
					"styles": this.style.deleteDutyNode
				});
				this.deleteAttributeNode.removeProperty("disable");
				this.deleteAttributeNode.addEvent("click", function(e){this.deleteAttribute(e);}.bind(this));
			}
		}else{
			if (!this.deleteAttributeNode.get("disable")){
				this.deleteAttributeNode.set({
					"styles": this.style.deleteDutyNode_desable,
					"disable": true
				});
				this.deleteAttributeNode.removeEvents("click");
			}
		}
	},
	deleteAttribute: function(e){
		var _self = this;
		this.explorer.app.confirm("infor", e, this.explorer.app.lp.deleteAttributeTitle, this.explorer.app.lp.deleteAttribute, 300, 120, function(){
			this.close();
			_self.selectedAttributes.each(function(attribute){
				attribute.remove();
			});
			delete _self.selectedAttributes;
			_self.selectedAttribute = [];
			_self.checkDeleteAttributeAction();
		}, function(){this.close();});
	},
	listAttribute: function(){
		var html = "<table cellspacing='0' cellpadding='5' border='0' width='95%' align='center' style='line-height:normal'>";
		html += "<tr><th style='width:20px'></th>";
		html += "<th style='width: 30%; border-right: 1px solid #FFF'>"+this.explorer.app.lp.attributeName+"</th>";
		html += "<th>"+this.explorer.app.lp.attributeValue+"</th><th style='width:20px'></th></tr>";
		html += "</table>";
		this.propertyAttributeContentNode.set("html", html);
		this.propertyAttributeContentNode.getElements("th").setStyles(this.style.propertyDutyContentTdTitle);
		
		if (this.data.id){
			this.explorer.actions.listPersonAttribute(function(json){
				json.data.each(function(item){
					new MWF.xApplication.Organization.PersonAttribute(this.propertyAttributeContentNode.getElement("table").getFirst(), item, this, this.explorer.css.map);
				}.bind(this));
			}.bind(this), null, this.data.id);
		}
	},
	
	showItemPropertyIdentity:function(){
		this.propertyIdentityNode = new Element("div", {
			"styles": this.style.propertyInforNode
		}).inject(this.explorer.propertyContentNode);
		
		this.identityActionNode = new Element("div", {
			"styles": this.style.propertyInforActionNode
		}).inject(this.propertyIdentityNode);
		this.propertyIdentityTextNode = new Element("div", {
			"styles": this.style.propertyInforTextNode,
			"text": this.explorer.app.lp.personIdentityText
		}).inject(this.propertyIdentityNode);
	//	this.createEditBaseNode();
		
		this.propertyIdentityContentNode = new Element("div", {
			"styles": this.style.propertyInforContentNode
		}).inject(this.propertyIdentityNode);
		
		this.listIdentity();
	},
	listIdentity: function(){
		if (this.data.id){
			this.explorer.actions.listIdentityByPerson(function(json){
				json.data.each(function(item){
					new MWF.xApplication.Organization.PersonExplorer.Identity(this.propertyIdentityContentNode, item, this, this.style);
				}.bind(this));
			}.bind(this), null, this.data.id);
		}
	},
	
	destroy: function(){
		this.explorer.currentItem = null;
		this.clearItemProperty();
		this.node.destroy();
		delete this;
	},
	"delete": function(success, failure){
		this.explorer.actions.deleteRole(this.data.id, function(){
			this.destroy();
			if (success) success();
		}.bind(this), function(xhr, text, error){
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
			
			if (failure) failure();
		});
	}
});

MWF.xApplication.Organization.PersonAttribute = new Class({
	Extends: MWF.xApplication.Organization.CompanyAttribute,
	saveValue: function(value){
		var oldValue = this.data.attributeList;
		this.data.attributeList = value.split("/,\s*/");
		this.item.explorer.actions.savePersonAttribute(this.data, function(json){
			this.data.id = json.data.id;
			this.valueNode.empty();
			this.valueInput = null;
			this.valueNode.set("text", this.data.attributeList.join(","));
		}.bind(this), function(xhr, text, error){
			this.data.attributeList = oldValue;
			this.valueInput.focus();
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			this.item.explorer.app.notice("request json error: "+errorText, "error");
		}.bind(this));
	},
	save: function(name){
		var oldName = this.data.name;
		this.data.name = name;
		this.item.explorer.actions.savePersonAttribute(this.data, function(json){
			this.data.id = json.data.id;
			this.nameNode.empty();
			this.input = null;
			this.nameNode.set("text", this.data.name);
		}.bind(this), function(xhr, text, error){
			this.data.name = oldName;
			this.input.focus();
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			this.item.explorer.app.notice("request json error: "+errorText, "error");
		}.bind(this));
	},
	remove: function(){
		this.item.explorer.actions.deletePersonAttribute(this.data.id, function(){
			this.node.destroy();
			delete this;
		}.bind(this));
	}
});

MWF.xApplication.Organization.PersonExplorer.Identity = new Class({
	initialize: function(container, data, item, style){
		this.container = $(container);
		this.data = data;		
		this.style = style;
		this.item = item;
		this.load();
	},
	load: function(){

		this.node = new Element("div", {
			"styles": this.style.identityNode
		}).inject(this.container);
		
		var nameNode = new Element("div", {
			"styles": this.style.identityInforNameNode
		}).inject(this.node);
		var picNode = new Element("div", {
			"styles": this.style.identityInforPicNode,
			"html": "<img width='50' height='50' border='0' src='"+"/x_component_Organization/$PersonExplorer/default/icon/head.png'></img>"
		}).inject(nameNode);
		var nameTextNode = new Element("div", {
			"styles": this.style.identityInforNameTextNode,
			"text": this.data.name
		}).inject(nameNode);
		
		var departmentNode = new Element("div", {"styles": this.style.identityDepartmentNode}).inject(this.node);
		var departmentTitleNode = new Element("div", {
			"styles": this.style.identityTitleNode,
			"text": this.item.explorer.app.lp.department
		}).inject(departmentNode);
		this.departmentTextNode = new Element("div", {"styles": this.style.identityTextNode}).inject(departmentNode);
		
		var companyNode = new Element("div", {"styles": this.style.identityCompanyNode}).inject(this.node);
		var companyTitleNode = new Element("div", {
			"styles": this.style.identityTitleNode,
			"text": this.item.explorer.app.lp.company
		}).inject(companyNode);
		this.companyTextNode = new Element("div", {"styles": this.style.identityTextNode}).inject(companyNode);
		
		var dutyNode = new Element("div", {"styles": this.style.identityDutyNode}).inject(this.node);
		var dutyTitleNode = new Element("div", {
			"styles": this.style.identityTitleNode,
			"text": this.item.explorer.app.lp.duty
		}).inject(dutyNode);
		this.dutyTextNode = new Element("div", {"styles": this.style.identityTextNode}).inject(dutyNode);
		
		
		this.item.explorer.actions.getDepartment(function(json){
			this.department = json.data;
			this.departmentTextNode.set({"text": this.department.name, "title": this.department.name});
			
			this.item.explorer.actions.getCompany(function(json){
				this.company = json.data;
				this.companyTextNode.set({"text": this.company.name, "text": this.company.name});
			}.bind(this), null, this.department.company);
			
		}.bind(this), null, this.data.department);
		
		
		this.item.explorer.actions.listCompanyDutyByIdentity(function(json){
			json.data.each(function(duty){
				var text = this.dutyTextNode.get("text");
				if (text){
					text = text+", "+duty.name;
				}else{
					text = duty.name;
				}
				this.dutyTextNode.set({"text": text, "title": text});
			}.bind(this));
		}.bind(this), null, this.data.id);
		
		this.item.explorer.actions.listDepartmentDutyByIdentity(function(json){
			json.data.each(function(duty){
				var text = this.dutyTextNode.get("text");
				if (text){
					text = text+", "+duty.name;
				}else{
					text = duty.name;
				}
				this.dutyTextNode.set({"text": text, "title": text});
			}.bind(this));
		}.bind(this), null, this.data.id);
	}
});

