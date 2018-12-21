//MWF.xDesktop.requireApp("Deployment", "Actions.RestActions", null, false);
MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
//MWF.xDesktop.requireApp("Deployment", "Actions.RestActions", null, false);
MWF.require("MWF.widget.O2Identity", null,false);
MWF.xApplication.Deployment.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Deployment",
		"icon": "icon.png",
		"width": "1000",
		"height": "660",
		"title": MWF.xApplication.Deployment.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Deployment.LP;

        this.actions = MWF.Actions.get("x_component_assemble_control");
        //this.actions = new MWF.xApplication.Deployment.Actions.RestActions();
        var personActions = new MWF.xAction.org.express.RestActions();
        this.explorer = {"actions": personActions,"app": {"lp": this.lp}};
	},
	loadApplication: function(callback){
        this.components = [];
        this.loadTitle();
		this.appDeploymentContent = new Element("div", {"styles": this.css.appDeploymentContent}).inject(this.content);
        this.componentsContent = new Element("div", {"styles": this.css.componentsContent}).inject(this.appDeploymentContent);

		MWF.require("MWF.widget.Tab", function(){
			this.tab = new MWF.widget.Tab(this.content, {"style": "processlayout"});
			this.tab.load();
			this.appPage = this.tab.addTab(this.appDeploymentContent, "已部署组件", false);
			this.appPage.showIm();

			this.setContentHeight();
			this.addEvent("resize", function(){this.setContentHeight();}.bind(this));
		}.bind(this));

		this.loadApplicationContent();
	},

    loadTitle: function(){
        this.titleBar = new Element("div", {"styles": this.css.titleBar}).inject(this.content);
        this.taskTitleTextNode = new Element("div", {"styles": this.css.titleTextNode,"text": this.lp.title}).inject(this.titleBar);
    },

	loadApplicationContent: function(){
		this.loadApps(function(){
            if (MWF.AC.isAdministrator()) this.loadNewApp();
        }.bind(this));
	},
    getComponentCatalogue: function(callback){
        var url = MWF.defaultPath+"/xDesktop/$Layout/components.json";
        MWF.getJSON(url, function(json){
            if (callback) callback(json);
        }.bind(this));
    },

    loadApps: function(callback){
        this.getComponentCatalogue(function(json){
            json.each(function(value, key){
                //this.createComponentItem(value, key);
                this.components.push(new MWF.xApplication.Deployment.Component(value, this));
            }.bind(this));

            this.actions.listComponent(function(json){
                json.data.each(function(value, key){
                    this.components.push(new MWF.xApplication.Deployment.UserComponent(value, this));
                }.bind(this));

                if (callback) callback();
            }.bind(this));
        }.bind(this));
    },

	loadNewApp: function(){
        var node = new Element("div", {"styles": this.css.componentItemNode}).inject(this.componentsContent);
        node.setStyles({
            "background-color": "#FFF"
        });

        var contentNode = new Element("div", {"styles": this.css.contentNode}).inject(node);
        var titleNode = new Element("div", {"styles": this.css.titleNode}).inject(contentNode);
        //contentNode.setStyles({"height": "30px"});

        var iconNode = new Element("div", {"styles": this.css.addIconNode}).inject(node);
        iconNode.addEvents({
            "mouseover": function(){iconNode.setStyles(this.css.addIconNode_over); titleNode.setStyle("color", "#3498db");}.bind(this),
            "mouseout": function(){iconNode.setStyles(this.css.addIconNode); titleNode.setStyle("color", "#999");}.bind(this),
            "click": function(e){
                this.createNewDeploy(e);
            }.bind(this)
        });
        var actionAreaNode = new Element("div", {"styles": this.css.actionAreaNode}).inject(node);

        titleNode.set("text", this.lp.add);
        titleNode.setStyle("color", "#999");
	},
    createNewDeploy: function(){
        new MWF.xApplication.Deployment.Deploy(this);
    },

	deployApp: function(){
		var inputs = this.appContentNode.getElements("input");
		var nameInput = inputs[0];
		var tileInput = inputs[1];
		var fileInput = inputs[2];
		var name = nameInput.get("value");
		var title = tileInput.get("value");

		if (!name || !title){
			this.notice("请输入应用名称和应用标题", "error", this.appContentNode);
		}else if (!fileInput.files.length){
			this.notice("请上传文件的ZIP包", "error", this.appContentNode);
		}else{
			var formData = new FormData();
			formData.append('file', fileInput.files[0]);
			formData.append('name', name);
			formData.append('title', title);
			formData.append('path', "/res/mwf4/package/xApplication");

			var xhr = new COMMON.Browser.Request();
			xhr.open("POST", "jaxrs/application", false);

			var onreadystatechange= function(){
				if (xhr.readyState != 4) return;

				var status = xhr.status;
				status = (status == 1223) ? 204 : status;

				if ((status >= 200 && status < 300)) {
					this.notice("部署成功", "success", this.appContentNode);
					this.appListNode.empty();
					this.loadApps();
				};
			}.bind(this);
			xhr.onreadystatechange = onreadystatechange;

			xhr.send(formData);

		}
	},

	setContentHeight: function(node){
		var size = this.content.getSize();
        var titleSize = this.titleBar.getSize();
		var tabSize = this.tab.tabNodeContainer.getSize();
		var height = size.y-tabSize.y-titleSize.y;

        this.tab.pages.each(function(page){
            page.contentNodeArea.setStyles({"height": ""+height+"px", "overflow": "auto"})
        });

		//this.appDeploymentContent.setStyle("height", height);
	}
});
MWF.xApplication.Deployment.Component = new Class({
    initialize: function(value, deployment, inset){
        this.data = value;
        this.deployment = deployment;
        this.css = this.deployment.css;
        this.content = this.deployment.componentsContent;

        this.load(inset);
    },
    reload: function(data){
        this.data = data;
        this.node.empty();
        this.load();
    },
    load: function(inset){
        if (!this.node){
            this.node = new Element("div", {"styles": this.css.componentItemNode});
            if (inset){
                var tmpNode = this.content.getLast("div");
                this.node.inject(tmpNode, "before");
            }else{
                this.node.inject(this.content);
            }
        }

        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.titleNode = new Element("div", {"styles": this.css.titleNode}).inject(this.contentNode);
        this.nameNode = new Element("div", {"styles": this.css.nameNode}).inject(this.contentNode);

        this.iconNode = new Element("div", {"styles": this.css.iconNode}).inject(this.node);

        this.actionAreaNode = new Element("div", {"styles": this.css.actionAreaNode}).inject(this.node);

        var icon = "/x_component_"+this.data.path.replace(/\./g, "_")+"/$Main/"+this.data.iconPath;
        this.iconNode.setStyle("background-image", "url("+icon+")");

        this.titleNode.set("text", this.data.title);
        this.nameNode.set("text", this.data.name);

        this.addAction();
        this.loadSystemFlag();
    },
    addAction: function(){
        if (this.data.visible){
            var user = this.deployment.desktop.session.user;
            var currentNames = [user.name, user.distinguishedName, user.id, user.unique];
            if (user.roleList) currentNames = currentNames.concat(user.roleList);
            if (user.groupList) currentNames = currentNames.concat(user.groupList);

            var isAllow = true;
            if (this.data.allowList) isAllow = (this.data.allowList.length) ? (this.data.allowList.isIntersect(currentNames)) : true;
            var isDeny = false;
            if (this.data.denyList) isDeny = (this.data.denyList.length) ? (this.data.denyList.isIntersect(currentNames)) : false;
            if ((!isDeny && isAllow) || MWF.AC.isAdministrator()){
                this.openAction = new Element("div", {"styles": this.css.actionNode, "text": this.deployment.lp.open}).inject(this.actionAreaNode);
                this.openAction.addEvents({
                    "mouseover": function(){this.openAction.setStyles(this.css.actionNode_over);}.bind(this),
                    "mouseout": function(){this.openAction.setStyles(this.css.actionNode);}.bind(this),
                    "click": function(e){
                        this.deployment.desktop.openApplication(e, this.data.path);
                    }.bind(this)
                });
            }
        }else{

        }
    },
    loadSystemFlag: function(){
        //this.flagNode = new Element("div", {"styles": this.css.flagNode}).inject(this.node);
    }
});
MWF.xApplication.Deployment.UserComponent = new Class({
    Extends: MWF.xApplication.Deployment.Component,

    createOpenAction: function(style){
        this.openAction = new Element("div", {"styles": this.css[style], "text": this.deployment.lp.open}).inject(this.actionAreaNode);
        this.openAction.addEvents({
            "mouseover": function(){this.openAction.setStyles(this.css.actionNode_over);}.bind(this),
            "mouseout": function(){this.openAction.setStyles(this.css[style]);}.bind(this),
            "click": function(e){
                this.deployment.desktop.openApplication(e, this.data.path);
            }.bind(this)
        });
    },
    createEditAction: function(style){
        this.editAction = new Element("div", {"styles": this.css[style], "text": this.deployment.lp.edit}).inject(this.actionAreaNode);
        this.editAction.addEvents({
            "mouseover": function(){this.editAction.setStyles(this.css.actionNode_over);}.bind(this),
            "mouseout": function(){this.editAction.setStyles(this.css[style]);}.bind(this),
            "click": function(e){
                this.editComponent();
            }.bind(this)
        });
    },
    createRemoveAction: function(style){
        this.removeAction = new Element("div", {"styles": this.css[style], "text": this.deployment.lp.remove}).inject(this.actionAreaNode);
        this.removeAction.addEvents({
            "mouseover": function(){this.removeAction.setStyles(this.css.actionNode_over);}.bind(this),
            "mouseout": function(){this.removeAction.setStyles(this.css[style]);}.bind(this),
            "click": function(e){
                var _self = this;
                var text = this.deployment.lp.removeComponent.replace(/{name}/, this.data.title)
                this.deployment.confirm("warn", e, this.deployment.lp.removeComponentTitle, text, 300, 130, function(){
                    _self.removeComponent();
                    this.close();
                }, function(){
                    this.close();
                }, null, this.deployment.content);

            }.bind(this)
        });
    },

    addAction: function(){
        this.node.setStyles(this.css.userComponentItemNode);
        var user = this.deployment.desktop.session.user;
        var currentNames = [user.name, user.distinguishedName, user.id, user.unique];
        if (user.roleList) currentNames = currentNames.concat(user.roleList);
        if (user.groupList) currentNames = currentNames.concat(user.groupList);

        var isAdministrator = this.checkAdministrator();
        if (isAdministrator && this.data.visible){
            this.createOpenAction("action2Node");
            this.createEditAction("action2Node");
            this.createRemoveAction("action3Node");
        }else if (!isAdministrator && this.data.visible){


            var isAllow = (this.data.allowList.length) ? (this.data.allowList.isIntersect(currentNames)) : true;
            var isDeny = (this.data.denyList.length) ? (this.data.denyList.isIntersect(currentNames)) : false;
            if ((!isDeny && isAllow) || MWF.AC.isAdministrator()){
                this.createOpenAction("actionNode");
            }
        }else if (isAdministrator && !this.data.visible){
            this.createEditAction("action4Node");
            this.createRemoveAction("action5Node");
        }
    },
    checkAdministrator: function(){
        if (MWF.AC.isAdministrator()) return true;
        var user = this.deployment.desktop.session.user;
        var currentNames = [user.name, user.distinguishedName, user.id, user.unique];
        if (user.roleList) currentNames = currentNames.concat(user.roleList);
        if (user.groupList) currentNames = currentNames.concat(user.groupList);

        if (this.data.controllerList.isIntersect(currentNames)) return true;
        return false;
    },
    loadSystemFlag: function(){},
    editComponent: function(){
        new MWF.xApplication.Deployment.DeployEdit(this.data, this, this.deployment);
    },
    removeComponent: function(){
        this.deployment.actions.removeComponent(this.data.id, function(){
            this.deployment.notice(this.deployment.lp.removeComponentOk, "success");
            this.deployment.components.erase(this);
            this.node.destroy();
            MWF.release(this);
        }.bind(this));
    }

});

MWF.xApplication.Deployment.Deploy = new Class({
    initialize: function(deployment){
        this.deployment = deployment;
        this.css = this.deployment.css;
        this.tab = this.deployment.tab;
        this.lp = this.deployment.lp;

        this.load(this.lp.add);
    },

    createLine: function(title){
        var lineNode = new Element("div", {"styles": this.css.deployLineNode}).inject(this.content);
        var titleNode = new Element("div", {"styles": this.css.deployTitleNode, "text": title}).inject(lineNode);
        var valueNode = new Element("div", {"styles": this.css.deployvalueNode}).inject(lineNode);
        return new Element("input", {"styles": this.css.deployInputNode, "type": "text"}).inject(valueNode);
    },
    createLineSelect: function(title, defaultValue){
        var lineNode = new Element("div", {"styles": this.css.deployLineNode}).inject(this.content);
        var titleNode = new Element("div", {"styles": this.css.deployTitleNode, "text": title}).inject(lineNode);
        var valueNode = new Element("div", {"styles": this.css.deployvalueNode}).inject(lineNode);
        var selectNode = new Element("select").inject(valueNode);
        new Element("option", {"text": this.lp.yes, "value":"yes"}).inject(selectNode);
        new Element("option", {"text": this.lp.no, "value":"no", "checked": ((defaultValue=="no") ? true : false)}).inject(selectNode);
        return selectNode;
    },

    load: function(title){
        this.node = new Element("div", {"styles": this.css.newDeployNode});

        this.content = new Element("div", {"styles": this.css.deployContentNode}).inject(this.node);

        this.nameInputNode = this.createLine(this.lp.name);
        this.titleInputNode = this.createLine(this.lp.componentTitle);
        this.pathInputNode = this.createLine(this.lp.path);
        this.visibleInputNode = this.createLineSelect(this.lp.isVisible);

        this.widgetNameInputNode = this.createLine(this.lp.widgetName);
        this.widgetTitleInputNode = this.createLine(this.lp.widgetTitle);
        this.widgetStartInputNode = this.createLineSelect(this.lp.widgetStart, "no");
        this.widgetVisibleInputNode = this.createLineSelect(this.lp.widgetVisible);

        this.allowList = new MWF.xApplication.Deployment.Deploy.Select(this.deployment, this.content, this.lp.allowList);
        this.denyList = new MWF.xApplication.Deployment.Deploy.Select(this.deployment, this.content, this.lp.denyList);
        this.controllerList = new MWF.xApplication.Deployment.Deploy.Select(this.deployment, this.content, this.lp.controllerList);

        this.okAction = new Element("div", {"styles": this.css.deployOkAction, "text": this.lp.add}).inject(this.content);
        this.okAction.addEvent("click", function(){
            var data = this.getComponentData();
            if ((!data.name) || (!data.title) || (!data.path)){
                this.deployment.notice(this.lp.noInputInfor, "error");
                return false;
            }else{
                this.deployment.actions.createComponent(data, function(){
                    this.deployment.notice(this.lp.deploySuccess, "success");
                    this.page.closeTab();
                    this.deployment.appPage.showTabIm();

                    this.deployment.components.push(new MWF.xApplication.Deployment.UserComponent(data, this.deployment, true));
                }.bind(this));
            }
        }.bind(this));

        this.page = this.tab.addTab(this.node, title, true);
        this.page.showTabIm();
    },
    getComponentData: function(){
        var visible = this.visibleInputNode.options[this.visibleInputNode.selectedIndex].value;
        var widgetStart = this.widgetStartInputNode.options[this.widgetStartInputNode.selectedIndex].value;
        var widgetVisible = this.widgetVisibleInputNode.options[this.widgetVisibleInputNode.selectedIndex].value;
        var data = {
            "name": this.nameInputNode.get("value"),
            "title": this.titleInputNode.get("value"),
            "path": this.pathInputNode.get("value"),
            "visible": (visible=="yes") ? true : false,
            "iconPath": "appicon.png",
            "widgetName": this.widgetNameInputNode.get("value"),
            "widgetTitle": this.widgetTitleInputNode.get("value"),
            "widgetIconPath": "widgeticon.png",
            "widgetStart": (widgetStart=="yes") ? true : false,
            "widgetVisible": (widgetVisible=="yes") ? true : false,

            "allowList": this.allowList.list,
            "denyList": this.denyList.list,
            "controllerList": this.controllerList.list
        };
        return data;
    }

});
MWF.xApplication.Deployment.DeployEdit = new Class({
    Extends: MWF.xApplication.Deployment.Deploy,
    initialize: function(data, component, deployment){
        this.deployment = deployment;
        this.component = component;
        this.css = this.deployment.css;
        this.tab = this.deployment.tab;
        this.lp = this.deployment.lp;
        this.data = data;

        this.load(this.lp.modify);
        this.setValues();
    },
    setValues: function(){
        this.nameInputNode.set("value", this.data.name);
        this.titleInputNode.set("value", this.data.title);
        this.pathInputNode.set("value", this.data.path);

        if (this.data.visible){
            this.visibleInputNode.getFirst("option").set("checked", true);
        }else{
            this.visibleInputNode.getLast("option").set("checked", true);
        }

        this.widgetNameInputNode.set("value", this.data.widgetName);
        this.widgetTitleInputNode.set("value", this.data.widgetTitle);

        this.allowList.setList(this.data.allowList);
        this.denyList.setList(this.data.denyList);
        this.controllerList.setList(this.data.controllerList);

        this.okAction.set("text", this.lp.modify);
        this.okAction.removeEvents("click");

        this.okAction.addEvent("click", function(){
            var data = this.getComponentData();
            if ((!data.name) || (!data.title) || (!data.path)){
                this.deployment.notice(this.lp.noInputInfor, "error");
                return false;
            }else{
                this.deployment.actions.updateComponent(this.data.id, data, function(){
                    this.deployment.notice(this.lp.modifySuccess, "success");
                    this.page.closeTab();
                    this.deployment.appPage.showTabIm();
                    data.id = this.data.id;
                    this.component.reload(data);
                }.bind(this));
            }
        }.bind(this));
    }
});


MWF.xApplication.Deployment.Deploy.Select = new Class({
    initialize: function(deployment, content, title){
        this.deployment = deployment;
        this.css = this.deployment.css;

        this.list = [];

        var lineNode = new Element("div", {"styles": this.css.deployLineNode}).inject(content);
        lineNode.setStyle("height", "40px");
        var titleNode = new Element("div", {"styles": this.css.deployTitleNode, "text": title}).inject(lineNode);
        var valueNode = new Element("div", {"styles": this.css.deployvalueNode}).inject(lineNode);
        this.listNode = new Element("div", {"styles": {"float": "left"}}).inject(valueNode);
        var actionNode = new Element("div", {"styles": this.css.actionNode, "text": this.deployment.lp.selPerson}).inject(valueNode);
        actionNode.setStyles({"margin-top": "10px", "float": "left"});

        actionNode.addEvent("click", function(){
            var options = {
                "type": "person",
                "values": this.list,
                "count": 0,
                "onComplete": function(items){
                    this.list = [];
                    items.each(function(item){
                        this.list.push(item.data.distinguishedName);
                    }.bind(this));
                    this.listNode.empty();
                    this.list.each(function(personName){
                        if (personName) new MWF.widget.O2Person({"name": personName}, this.listNode, {"style": "application"});
                    }.bind(this));
                }.bind(this)
            };
            var selector = new MWF.O2Selector(this.deployment.content, options);
        }.bind(this));
    },
    setList: function(data){
        this.list = data;
        this.list.each(function(personName){
            if (personName) new MWF.widget.O2Person({"name": personName}, this.listNode, {"style": "application"});
        }.bind(this));
    }
});