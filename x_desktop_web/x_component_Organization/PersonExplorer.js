MWF.xDesktop.requireApp("Organization", "GroupExplorer", null, false);
MWF.xDesktop.requireApp("Organization", "OrgExplorer", null, false);
MWF.xApplication.Organization.PersonExplorer = new Class({
	Extends: MWF.xApplication.Organization.GroupExplorer,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
    _loadPath: function(){
        this.path = "/x_component_Organization/$PersonExplorer/";
        this.cssPath = "/x_component_Organization/$PersonExplorer/"+this.options.style+"/css.wcss";
    },
    _loadLp: function(){
        this.options.lp = {
            "elementLoaded": this.app.lp.personLoaded,
            "search": this.app.lp.search,
            "searchText": this.app.lp.searchText,
            "elementSave": this.app.lp.personSave,
            "deleteElements": this.app.lp.deletePersons,

            "deleteElementsTitle": this.app.lp.deletePersonsTitle,
            "deleteElementsConfirm": this.app.lp.deletePersonsConfirm,

            "elementBaseText": this.app.lp.roleBaseText,
            "elementName": this.app.lp.roleName,

            "edit": this.app.lp.edit,
            "cancel": this.app.lp.cancel,
            "save": this.app.lp.save,
            "add": this.app.lp.add
        }
    },
    _listElementNext: function(lastid, count, callback){
        this.actions.listPersonNext(lastid, count, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this));
    },
    _newElement: function(data, explorer){
        return new MWF.xApplication.Organization.PersonExplorer.Person(data, explorer, this.isEditor);
    },
    _listElementByKey: function(callback, failure, key){
        this.actions.listPersonByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getAddElementData: function(){
        return {
            "employee": "",
            "password": "",
            "display": "",
            "qq": "",
            "mail": "",
            "weixin": "",
            "weibo": "",
            "mobile": "",
            "name": "",
            "controllerList": []
        };
    },
    loadToolbar: function(){
        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode}).inject(this.chartAreaNode);
        if (MWF.AC.isCompanyCreator() || MWF.AC.isPersonManager()) {
            //          "yes": function(){
            this.isEditor = true;
            this.addTopElementNode = new Element("div", {"styles": this.css.addTopGroupNode}).inject(this.toolbarNode);
            this.addTopElementNode.addEvent("click", function () {
                this.addTopElement();
            }.bind(this));
            //           }.bind(this)
        }
        this.createSearchNode();
    }
});

MWF.xApplication.Organization.PersonExplorer.Person = new Class({
	Extends: MWF.xApplication.Organization.GroupExplorer.Group,
	
	initialize: function(data, explorer, isEditor){
		this.data = data;
		
		this.explorer = explorer;
		this.chartNode = this.explorer.chartNode;
		this.initStyle();
		
		this.selectedAttributes = [];

		this.isEdit = false;
        this.isEditor = isEditor;
		this.deleteSelected = false;
	},
    load: function(){
        this.node = new Element("div", {"styles": this.style.node}).inject(this.chartNode);
        this.contentNode = new Element("div", {"styles": this.style.contentNode}).inject(this.node);
        this.childNode = new Element("div", {"styles": this.style.childNode}).inject(this.node);

        this.flagNode = new Element("div", {"styles": this.style.flagNode}).inject(this.contentNode);
        this.iconNode = new Element("div", {"styles": this.style.iconNode}).inject(this.contentNode);

        if (this.data.icon){
            this.iconNode.setStyle("background-image", "");
            var img = new Element("img", {
                "styles": {
                    "margin-top": "6px",
                    "margin-left": "13px",
                    "width": "24px",
                    "height": "24px",
                    "border": "0"
                },
                "src": "data:image/png;base64,"+this.data.icon
            }).inject(this.iconNode);
        }else{
            if (this.data.genderType=="f"){
                this.iconNode.setStyle("background", "url("+"/x_component_Organization/$PersonExplorer/default/icon/female24.png) center center no-repeat");
            }else{
                this.iconNode.setStyle("background", "url("+"/x_component_Organization/$PersonExplorer/default/icon/man24.png) center center no-repeat");
            }

        }

        this.actionNode = new Element("div", {"styles": this.style.actionNode}).inject(this.contentNode);

        this.textNode = new Element("div", {"styles": this.style.textNode}).inject(this.contentNode);
        this.textNode.set({
            "text": this.data.name
        });

        this.setNewItem();

        this.node.inject(this.chartNode);

        this.addActions();
        this.setEvent();
    },
    addActions: function(){
        if (this.isEditor){
            if (MWF.AC.isPersonEditor({"list": this.data.controllerList})){
                this.deleteNode = new Element("div", {"styles": this.style.actionDeleteNode}).inject(this.actionNode);
//		this.addNode = new Element("div", {"styles": this.style.actionAddNode}).inject(this.actionNode);
                this.deleteNode.addEvent("click", function(e){
                    if (!this.deleteSelected){
                        this.deleteNode.setStyles(this.style.actionDeleteNode_selected);
                        this.contentNode.setStyles(this.style.contentNode_selected);

                        this.explorer.deleteElements.push(this);
                        this.deleteSelected = true;

                        this.explorer.checkDeleteElements();
                    }else{
                        this.deleteNode.setStyles(this.style.actionDeleteNode);
                        this.contentNode.setStyles(this.style.contentNode);

                        this.explorer.deleteElements.erase(this);
                        this.deleteSelected = false;
                        this.explorer.checkDeleteElements();
                    }
                    e.stopPropagation();
                }.bind(this));
            }
        }
    },
	
	showItemProperty: function(){
		this.explorer.propertyTitleNode.set("text", this.data.name);
		this.showItemPropertyBase();
        if (MWF.AC.isPersonEditor({"list": this.data.controllerList})) this.showItemPropertyAttribute();
		this.showItemPropertyIdentity();

        this.showItemcontrollerList();
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
        html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personImage+"</td><td id='formPersonImage'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personName+"</td><td id='formPersonName'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personUnique+"</td><td id='formPersonUnique'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personGender+"</td><td id='formPersonGender'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personEmployee+"</td><td id='formPersonEmployee'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personDisplay+"</td><td id='formPersonDisplay'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personMobile+"</td><td id='formPersonMobile'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personMail+"</td><td id='formPersonMail'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personQQ+"</td><td id='formPersonQQ'></td></tr>";
		html += "<tr><td class='formTitle'>"+this.explorer.app.lp.personWeixin+"</td><td id='formPersonWeixin'></td></tr>";
		html += "<tr><td class='formTitle' style='display:none'>"+this.explorer.app.lp.personWeibo+"</td><td id='formPersonWeibo' style='display:none'></td></tr>";
		html += "</table>";
		this.propertyBaseContentNode.set("html", html);
		this.propertyBaseContentNode.getElements("td.formTitle").setStyles(this.style.propertyBaseContentTdTitle);

        this.personImageNode = this.propertyBaseContentNode.getElement("#formPersonImage");
        this.personImageAreaNode = new Element("div", {"styles": this.style.personImageAreaNode}).inject(this.personImageNode);

        if (this.data.icon){
            this.personImageAreaNode.setStyle("background", "url(data:image/png;base64,"+this.data.icon+") center center no-repeat");
        }else{
            if (this.data.genderType=="f"){
                this.personImageAreaNode.setStyle("background", "url("+"/x_component_Organization/$PersonExplorer/default/icon/female.png) center center no-repeat");
            }else{
                this.personImageAreaNode.setStyle("background", "url("+"/x_component_Organization/$PersonExplorer/default/icon/man.png) center center no-repeat");
            }
        }
        if (MWF.AC.isPersonEditor({"list": this.data.controllerList})){
            this.personImageActionNode = new Element("div", {"styles": this.style.personImageActionNode, "text": this.explorer.app.lp.uploadImage}).inject(this.personImageNode);
            this.personImageActionNode.addEvent("click", function(){
                this.changePersonIcon();
            }.bind(this));
        }


        this.personGenderNode = this.propertyBaseContentNode.getElement("#formPersonGender");
        this.personGenderNode.setStyle("padding-left", "20px");
        if (this.data.genderType){
            var text = "";
            switch (this.data.genderType) {
                case "m":
                    text = this.explorer.app.lp.man;
                    break;
                case "f":
                    text = this.explorer.app.lp.female;
                    break;
                default:
                    text = this.explorer.app.lp.other;
            }
            this.personGenderNode.set("text", text);
        }

		
		this.personNameInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonName"), this.data.name, this.explorer.css.formInput);
		this.personEmployeeInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonEmployee"), this.data.employee, this.explorer.css.formInput, "none");
        this.personUniqueInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonUnique"), this.data.unique, this.explorer.css.formInput, "none");
        //this.personGenderInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonGender"), this.data.gender, this.explorer.css.formInput);
		this.personDisplayInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonDisplay"), this.data.display, this.explorer.css.formInput);
		this.personMobileInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonMobile"), this.data.mobile, this.explorer.css.formInput);
		this.personMailInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonMail"), this.data.mail, this.explorer.css.formInput);
		this.personQQInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonQQ"), this.data.qq, this.explorer.css.formInput);
		this.personWeixinInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonWeixin"), this.data.weixin, this.explorer.css.formInput);
		this.personWeiboInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formPersonWeibo"), this.data.weibo, this.explorer.css.formInput);
	},
    createEditBaseNode: function(){
        if (MWF.AC.isPersonEditor({"list": this.data.controllerList})){
            this.editBaseNode = new Element("button", {
                "styles": this.style.editBaseNode,
                "text": this.explorer.options.lp.edit,
                "events": {"click": this.editBaseInfor.bind(this)}
            }).inject(this.baseActionNode);
            //this.editBaseNode = new Element("button", {
            //    "styles": this.style.editBaseNode,
            //    "text": this.explorer.options.lp.edit,
            //    "events": {"click": this.editBaseInfor.bind(this)}
            //}).inject(this.baseActionNode);
        }
    },
    changePersonIcon: function(){
        if (!this.uploadFileAreaNode){
            this.uploadFileAreaNode = new Element("div");
            var html = "<input name=\"file\" type=\"file\"/>";
            this.uploadFileAreaNode.set("html", html);

            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", function(){

                var files = fileNode.files;
                if (files.length){
                    for (var i = 0; i < files.length; i++) {
                        var file = files.item(i);

                        var formData = new FormData();
                        formData.append('file', file);
                        //formData.append('name', file.name);
                        //formData.append('folder', folderId);

                        this.explorer.actions.changePersonIcon(this.data.id ,function(){
                            this.explorer.actions.getPerson(function(json){
                                if (json.data){
                                    this.data = json.data;
                                    if (this.data.icon){
                                        this.personImageAreaNode.setStyle("background", "url(data:image/png;base64,"+this.data.icon+") center center no-repeat");
                                        this.iconNode.setStyle("background-image", "");
                                        this.iconNode.empty();
                                        var img = new Element("img", {
                                            "styles": {
                                                "margin-top": "6px",
                                                "margin-left": "13px",
                                                "width": "24px",
                                                "height": "24px",
                                                "border": "0"
                                            },
                                            "src": "data:image/png;base64,"+this.data.icon
                                        }).inject(this.iconNode);
                                    }
                                }
                            }.bind(this), null, this.data.id, false)
                        }.bind(this), null, formData, file);
                    }
                }

            }.bind(this));
        }
        var fileNode = this.uploadFileAreaNode.getFirst();
        fileNode.click();
    },

	editMode: function(){
		this.personNameInput.editMode();
		this.personEmployeeInput.editMode();
        this.personUniqueInput.editMode();
		this.personDisplayInput.editMode();
		this.personMobileInput.editMode();
		this.personMailInput.editMode();
		this.personQQInput.editMode();
		this.personWeixinInput.editMode();
		this.personWeiboInput.editMode();

        var html = "<input name=\"personGenderRadioNode\" value=\"m\" type=\"radio\" "+((this.data.genderType=="m") ? "checked" : "")+"/>"+this.explorer.app.lp.man;
        html += "<input name=\"personGenderRadioNode\" value=\"f\" type=\"radio\" "+((this.data.genderType=="f") ? "checked" : "")+"/>"+this.explorer.app.lp.female;
        html += "<input name=\"personGenderRadioNode\" value=\"o\" type=\"radio\" "+((this.data.genderType=="d") ? "checked" : "")+"/>"+this.explorer.app.lp.other;
        this.personGenderNode.set("html", html);

		this.isEdit = true;
	},
	readMode: function(){
		this.personNameInput.readMode();
		this.personEmployeeInput.readMode();
        this.personUniqueInput.readMode();
		this.personDisplayInput.readMode();
		this.personMobileInput.readMode();
		this.personMailInput.readMode();
		this.personQQInput.readMode();
		this.personWeixinInput.readMode();
		this.personWeiboInput.readMode();

        this.personGenderNode.empty();
        if (this.data.genderType){
            var text = "";
            switch (this.data.genderType) {
                case "m":
                    text = this.explorer.app.lp.man;
                    break;
                case "f":
                    text = this.explorer.app.lp.female;
                    break;
                default:
                    text = this.explorer.app.lp.other;
            }
            this.personGenderNode.set("text", text);
        }

		this.isEdit = false;
	},
	
	saveBaseInfor: function(){
        var radios = this.personGenderNode.getElements("input");
        var gender = "";
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                gender = radios[i].value;
                break;
            }
        }

		if (!this.personNameInput.input.get("value") || !this.personUniqueInput.input.get("value") || !this.personEmployeeInput.input.get("value") || !gender){
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
        this.data.unique = this.personUniqueInput.input.get("value");
		this.data.display = this.personDisplayInput.input.get("value");
		this.data.mobile = this.personMobileInput.input.get("value");
		this.data.mail = this.personMailInput.input.get("value");
		this.data.qq = this.personQQInput.input.get("value");
		this.data.weixin = this.personWeixinInput.input.get("value");
		this.data.weibo = this.personWeiboInput.input.get("value")

        var radios = this.personGenderNode.getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                this.data.genderType = radios[i].value;
                break;
            }
        }

		this.explorer.actions.savePerson(this.data, function(json){

			this.textNode.set("text", this.data.name);
			this.data.id = json.data.id;

			this.personNameInput.save();
			this.personEmployeeInput.save();
            this.personUniqueInput.save();
			this.personDisplayInput.save();
			this.personMobileInput.save();
			this.personMailInput.save();
			this.personQQInput.save();
			this.personWeixinInput.save();
			this.personWeiboInput.save();

            if (this.data.icon){
                this.iconNode.setStyle("background-image", "");
                var img = new Element("img", {
                    "styles": {
                        "margin-top": "6px",
                        "margin-left": "13px",
                        "width": "24px",
                        "height": "24px",
                        "border": "0"
                    },
                    "src": "data:image/png;base64,"+this.data.icon
                }).inject(this.iconNode);
                this.personImageAreaNode.setStyle("background", "url(data:image/png;base64,"+this.data.icon+") center center no-repeat");
            }else{
                if (this.data.genderType=="f"){
                    this.iconNode.setStyle("background", "url("+"/x_component_Organization/$PersonExplorer/default/icon/female24.png) center center no-repeat");
                    this.personImageAreaNode.setStyle("background", "url("+"/x_component_Organization/$PersonExplorer/default/icon/female.png) center center no-repeat");
                }else{
                    this.iconNode.setStyle("background", "url("+"/x_component_Organization/$PersonExplorer/default/icon/man24.png) center center no-repeat");
                    this.personImageAreaNode.setStyle("background", "url("+"/x_component_Organization/$PersonExplorer/default/icon/man.png) center center no-repeat");
                }

            }
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

        if (MWF.AC.isPersonEditor({"list": this.data.controllerList})) {
            this.createDeleteAttributeNode();
            this.createAddAttributeNode();
        }
		
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
        MWF.AC.isCompanyEditor({
            "yes": function(){
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
            }.bind(this)
        });


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
    showItemcontrollerList: function(){
        this.controllerListNode = new Element("div", {
            "styles": this.style.propertyInforNode
        }).inject(this.explorer.propertyContentNode);

        this.controllerListActionNode = new Element("div", {
            "styles": this.style.propertyInforActionNode
        }).inject(this.controllerListNode);

        if (MWF.AC.isPersonEditor({"list": this.data.controllerList})){
            this.addAttributeNode = new Element("button", {
                "styles": this.style.addDutyNode,
                "text": this.explorer.app.lp.manager,
                "events": {"click": this.changeControllerList.bind(this)}
            }).inject(this.controllerListActionNode);
        }

        this.controllerListTextNode = new Element("div", {
            "styles": this.style.propertyInforTextNode,
            "text": this.explorer.app.lp.controllerListText
        }).inject(this.controllerListNode);

        this.controllerListContentNode = new Element("div", {
            "styles": this.style.propertyInforContentNode
        }).inject(this.controllerListNode);

        this.listController();
    },

    listController: function(){
        this.controllerListContentNode.empty();
        MWF.require("MWF.widget.Identity", function(){
            this.data.controllerList.each(function(id){
                this.explorer.actions.getPerson(function(json){
                    new MWF.widget.Person(json.data, this.controllerListContentNode, this.explorer)
                }.bind(this), null, id)
            }.bind(this));
        }.bind(this));
    },

    changeControllerList: function(){
        var selector = new MWF.OrgSelector(this.explorer.app.content, {
            "type": "person",
            "values": this.data.controllerList || [],
            "onComplete": function(items){
                var ids = [];
                items.each(function(item){ids.push(item.data.id);});
                this.data.controllerList = ids;
                this.explorer.actions.savePerson(this.data);
                this.listController();
            }.bind(this)
        });
    },
	
	destroy: function(){
		this.explorer.currentItem = null;
		this.clearItemProperty();
		this.node.destroy();
		delete this;
	},
	"delete": function(success, failure){
		this.explorer.actions.deletePerson(this.data.id, function(){
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

        var img = ""
        if (this.item.data.icon){
            img = "<img width='50' height='50' border='0' src='data:image/png;base64,"+this.item.data.icon+"'></img>"
        }else{
            if (this.item.data.genderType=="f"){
                img = "<img width='50' height='50' border='0' src='"+"/x_component_Organization/$PersonExplorer/default/icon/female.png'></img>";
            }else{
                img = "<img width='50' height='50' border='0' src='"+"/x_component_Organization/$PersonExplorer/default/icon/man.png'></img>";
            }
        }

		var picNode = new Element("div", {
			"styles": this.style.identityInforPicNode,
			"html": img
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
				this.companyTextNode.set({"text": this.company.name, "title": this.company.name});
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

