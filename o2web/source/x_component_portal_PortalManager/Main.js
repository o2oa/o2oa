MWF.xDesktop.requireApp("portal.PortalManager", "package", null, false);
//MWF.xDesktop.requireApp("portal.PortalManager", "Actions.RestActions", null, false);
MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.require("MWF.widget.Identity", null,false);
MWF.xDesktop.requireApp("process.ProcessManager", "", null, false);
MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.PortalManager.Main = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Main,
	Implements: [Options, Events],

	options: {
        "application": null,
		"style": "default",
		"name": "portal.PortalManager",
		"icon": "icon.png",
		"width": "1100",
		"height": "700",
		"title": MWF.xApplication.portal.PortalManager.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.portal.PortalManager.LP;
		this.currentContentNode = null;
        this.restActions = MWF.Actions.get("x_portal_assemble_designer");
        //new MWF.xApplication.portal.PortalManager.Actions.RestActions();
	},

    keyCopyItems: function(e){
        var app = layout.desktop.currentApp || layout.desktop.app;
        if (app && app.appId===this.appId){
            if (this.pageConfigurator){
                this.pageConfigurator.keyCopy(e);
            }
            if (this.scriptConfigurator){
                this.scriptConfigurator.keyCopy(e);
            }
            if (this.dictionaryConfigurator){
                this.dictionaryConfigurator.keyCopy(e);
            }
            if (this.widgetConfigurator){
                this.widgetConfigurator.keyCopy(e);
            }
        }
    },
    keyPasteItems: function(e){
        var app = layout.desktop.currentApp || layout.desktop.app;
        if (app && app.appId===this.appId) {
            if (this.pageConfigurator){
                this.pageConfigurator.keyPaste(e);
            }
            if (this.scriptConfigurator){
                this.scriptConfigurator.keyPaste(e);
            }
            if (this.dictionaryConfigurator){
                this.dictionaryConfigurator.keyPaste(e);
            }
            if (this.widgetConfigurator){
                this.widgetConfigurator.keyPaste(e);
            }
        }
    },

    loadStartMenu: function(callback){
        this.startMenuNode = new Element("div", {
            "styles": this.css.startMenuNode
        }).inject(this.node);

        this.menu = new MWF.xApplication.portal.PortalManager.Menu(this, this.startMenuNode, {
            "onPostLoad": function(){
                if (this.status){
                    if (this.status.navi!=null){
                        this.menu.doAction(this.menu.startNavis[this.status.navi]);
                    }else{
                        this.menu.doAction(this.menu.startNavis[0]);
                    }
                }else{
                    this.menu.doAction(this.menu.startNavis[0]);
                }
            }.bind(this)
        });
        this.addEvent("resize", function(){
            if (this.menu) this.menu.onResize();
        }.bind(this));
    },
    clearContent: function(){
        if (this.pageConfiguratorContent){
            if (this.pageConfigurator) delete this.pageConfigurator;
            this.pageConfiguratorContent.destroy();
            this.pageConfiguratorContent = null;
        }
        if (this.menuConfiguratorContent){
            if (this.menuConfigurator) delete this.menuConfigurator;
            this.menuConfiguratorContent.destroy();
            this.menuConfiguratorContent = null;
        }
        if (this.propertyConfiguratorContent){
            if (this.property) delete this.property;
            this.propertyConfiguratorContent.destroy();
            this.propertyConfiguratorContent = null;
        }
        if (this.widgetConfiguratorContent){
            if (this.widgetConfigurator) delete this.widgetConfigurator;
            this.widgetConfiguratorContent.destroy();
            this.widgetConfiguratorContent = null;
        }
        if (this.scriptConfiguratorContent){
            if (this.scriptConfigurator) delete this.scriptConfigurator;
            this.scriptConfiguratorContent.destroy();
            this.scriptConfiguratorContent = null;
        }
        if (this.dictionaryConfiguratorContent){
            if (this.dictionaryConfigurator) delete this.dictionaryConfigurator;
            this.dictionaryConfiguratorContent.destroy();
            this.dictionaryConfiguratorContent = null;
        }
        if (this.fileConfiguratorContent){
            if (this.fileConfigurator) delete this.fileConfigurator;
            this.fileConfiguratorContent.destroy();
            this.fileConfiguratorContent = null;
        }
    },

    applicationProperty: function(){
        this.clearContent();
        this.propertyConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.property = new MWF.xApplication.portal.PortalManager.ApplicationProperty(this, this.propertyConfiguratorContent);
        this.property.load();
    },

    pageConfig: function(){
        this.clearContent();
        this.pageConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadPageConfig();
    },
    loadPageConfig: function(){
        MWF.xDesktop.requireApp("portal.PortalManager", "PageExplorer", function(){
            this.pageConfigurator = new MWF.xApplication.portal.PortalManager.PageExplorer(this.pageConfiguratorContent, this.restActions);
            this.pageConfigurator.app = this;
            this.pageConfigurator.load();
        }.bind(this));
    },

    menuConfig: function(){
        this.clearContent();
        this.menuConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadMenuConfig();
    },
    loadMenuConfig: function(){
        MWF.xDesktop.requireApp("portal.PortalManager", "MenuExplorer", function(){
            //MWF.xDesktop.requireApp("portal.PortalManager", "Actions.RestActions", function(){
                //if (!this.restActions) this.restActions = new MWF.xApplication.portal.PortalManager.Actions.RestActions();
            if (!this.restActions) this.restActions = MWF.Actions.get("x_portal_assemble_designer");
            this.menuConfigurator = new MWF.xApplication.portal.PortalManager.MenuExplorer(this.menuConfiguratorContent, this.restActions);
            this.menuConfigurator.app = this;
            this.menuConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    widgetConfig: function(){
        this.clearContent();
        this.widgetConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadWidgetConfig();
    },
    loadWidgetConfig: function(){
        MWF.xDesktop.requireApp("portal.PortalManager", "WidgetExplorer", function(){
            this.widgetConfigurator = new MWF.xApplication.portal.PortalManager.WidgetExplorer(this.widgetConfiguratorContent, this.restActions);
            this.widgetConfigurator.app = this;
            this.widgetConfigurator.load();
        }.bind(this));
    },
    scriptConfig: function(){
        this.clearContent();
        this.scriptConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadScriptConfig();
    },
    loadScriptConfig: function(){
        MWF.xDesktop.requireApp("portal.PortalManager", "ScriptExplorer", function(){
            //MWF.xDesktop.requireApp("portal.PortalManager", "Actions.RestActions", function(){
                //if (!this.restActions) this.restActions = new MWF.xApplication.portal.PortalManager.Actions.RestActions();
            if (!this.restActions) this.restActions = MWF.Actions.get("x_portal_assemble_designer");
                this.scriptConfigurator = new MWF.xApplication.portal.PortalManager.ScriptExplorer(this.scriptConfiguratorContent, this.restActions);
                this.scriptConfigurator.app = this;
                this.scriptConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    dataConfig: function(){
        this.clearContent();
        this.dictionaryConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadDictionaryConfig();
    },
    loadDictionaryConfig: function(){
        MWF.xDesktop.requireApp("portal.PortalManager", "DictionaryExplorer", function(){
            //MWF.xDesktop.requireApp("portal.PortalManager", "Actions.RestActions", function(){
            //if (!this.restActions) this.restActions = new MWF.xApplication.portal.PortalManager.Actions.RestActions();
            if (!this.restActions) this.restActions = MWF.Actions.get("x_portal_assemble_designer");
            this.dictionaryConfigurator = new MWF.xApplication.portal.PortalManager.DictionaryExplorer(this.dictionaryConfiguratorContent, this.restActions);
            this.dictionaryConfigurator.app = this;
            this.dictionaryConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    fileConfig: function(){
        this.clearContent();
        this.fileConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadFileConfig();
    },
    loadFileConfig: function(){
        MWF.xDesktop.requireApp("portal.PortalManager", "FileExplorer", function(){
            //MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
            this.restActions = MWF.Actions.get("x_portal_assemble_designer");
            this.fileConfigurator = new MWF.xApplication.portal.PortalManager.FileExplorer(this.fileConfiguratorContent, this.restActions);
            this.fileConfigurator.app = this;
            this.fileConfigurator.load();
            //}.bind(this));
        }.bind(this));
    }
});

MWF.xApplication.portal.PortalManager.Menu = new Class({
    Extends: MWF.xApplication.process.ProcessManager.Menu,
    Implements: [Options, Events]
});

MWF.xApplication.portal.PortalManager.ApplicationProperty = new Class({
    Extends: MWF.xApplication.process.ProcessManager.ApplicationProperty,
    load: function(){
        this.app.restActions.getApplication(this.app.options.application.id, function(json){
            this.data = json.data;
            this.propertyTitleBar = new Element("div", {
                "styles": this.app.css.propertyTitleBar,
                "text": this.data.name
            }).inject(this.node);

            this.contentNode =  new Element("div", {
                "styles": this.app.css.propertyContentNode
            }).inject(this.node);
            this.contentAreaNode =  new Element("div", {
                "styles": this.app.css.propertyContentAreaNode
            }).inject(this.contentNode);

            this.setContentHeight();
            this.setContentHeightFun = this.setContentHeight.bind(this);
            this.app.addEvent("resize", this.setContentHeightFun);
            MWF.require("MWF.widget.ScrollBar", function(){
                new MWF.widget.ScrollBar(this.contentNode, {"indent": false});
            }.bind(this));

            this.baseActionAreaNode = new Element("div", {
                "styles": this.app.css.baseActionAreaNode
            }).inject(this.contentAreaNode);

            this.baseActionNode = new Element("div", {
                "styles": this.app.css.propertyInforActionNode
            }).inject(this.baseActionAreaNode);
            this.baseTextNode = new Element("div", {
                "styles": this.app.css.baseTextNode,
                "text": this.app.lp.application.property
            }).inject(this.baseActionAreaNode);

            this.createEditBaseNode();

            this.createPropertyContentNode();

            this.createIconContentNode();
            this.createAvailableNode();
            this.createControllerListNode();

            this.createCornerMarkNode();


            // this.createMaintainerNode();
        }.bind(this));
    },
    createCornerMarkNode: function(){
        var lp = this.app.lp.application;
        this.cornerMarkTitleNode = new Element("div", {
            "styles": this.app.css.availableTitleNode,
            "text": lp.cornerMark
        }).inject(this.contentAreaNode);

        this.cornerMarkContentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);


        new Element("div", {
            styles: this.app.css.noteNode,
            text: lp.cornerMarkNote
        }).inject( this.cornerMarkContentNode );


        this.cornerMarkItemsContentNode = new Element("div", {"styles": this.app.css.availableItemsContentNode}).inject(this.cornerMarkContentNode);

        this.cornerMarkLeftNode = new Element("div", {"styles": {
            "width": "calc( 100% - 260px )",
             "float": "left"
        }}).inject(this.cornerMarkItemsContentNode);

        MWF.require("MWF.widget.ScriptArea", null, false);
        this.cornerMarkScriptArea = new MWF.widget.ScriptArea(this.cornerMarkLeftNode, {
            "type": "service",
            "api": "../api/index.html#module-print",
            "title": lp.cornerMarkScript,
            //"isload" : true,
            "isbind" : false,
            // "forceType": "ace",
            "maxObj": this.app.content,
            "onChange": function(){
                this.data.cornerMarkScriptText = this.cornerMarkScriptArea.toJson().code;
                this.app.restActions.saveApplication(this.data, function(json){}.bind(this));
            }.bind(this)
            // "onSave": function(){
            //     this.data.cornerMarkScriptText = this.cornerMarkScriptArea.toJson().code;
            //     this.app.restActions.saveApplication(this.data, function(json){}.bind(this));
            // }.bind(this),
            // "style": "formula"
        });
        this.app.addEvent("resize", function () {
            if(this.cornerMarkScriptArea.jsEditor)this.cornerMarkScriptArea.jsEditor.resize();
        }.bind(this))

        debugger;

        var host = window.location.origin;
        if( !host )host = window.location.protocol + "//" + window.location.host;
        var defaultText = "/********************\n" +
            "this.currentPerson; //当前用户对象，如：{\"distinguishedName\":\"张三@zhangsan@P\",\"unique\":\"zhangsan\",\"name\":\"张三\"}\n" +
            "this.org; //组织快速访问方法\n" +
            "return 10; //返回结果\n" +
            "API Document: "+ host +"/api\n" +
            "示例：角标显示当前人的待办总数\n" +
            "var json = this.Actions.load(\"x_processplatform_assemble_surface\").TaskAction.manageListFilterPaging(1, 1, {\"credentialList\": [this.currentPerson.distinguishedName]})\n" +
            "return json.count;\n"+
            "********************/";
        var v = this.data.cornerMarkScriptText || defaultText;
        this.cornerMarkScriptArea.load({code: v});

        this.cornerMarkRightNode = new Element("div", {"styles": {
                "width": "260px",
                "margin-left": "calc( 100% - 260px )"
            }}).inject(this.cornerMarkItemsContentNode);

        this.selectScriptArea = new Element("div", {
            styles: this.app.css.selectScriptArea
        }).inject( this.cornerMarkRightNode );
        this.selectScriptNode = new Element("div", {
            text: this.data.cornerMarkScript ? "" : lp.selectScriptNote,
            styles: {color: "#aaa"}
        }).inject(this.selectScriptArea);


        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function() {
            new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(this.selectScriptNode, this.app, {
                "title" : lp.selectScript,
                "type": "script",
                "selectorOptions":{
                    "appType" : ["portal"],
                    "applications": [this.app.options.application.id]
                },
                "count" : 1,
                "names": this.data.cornerMarkScript ? [ {
                    appId: this.app.options.application.id,
                    name : this.data.cornerMarkScript,
                    alias: "",
                    applicationName: this.data.name,
                    appType: "portal"
                } ] : [],
                "onChange": function ( arr ) {
                    if( arr && arr.length ){
                        this.data.cornerMarkScript = arr[0].data.name;
                    }else{
                        this.data.cornerMarkScript = "";
                    }
                    this.app.restActions.saveApplication(this.data, function(json){}.bind(this));
                }.bind(this)
            });
        }.bind(this));


        this.cornerMarkActionAreaNode = new Element("div", {"styles": this.app.css.cornerMarkActionAreaNode }).inject(this.cornerMarkRightNode);

        var executeAction = new Element("div", {
            "styles": this.app.css.cornerMarkButton,
            "text": lp.run
        }).inject(this.cornerMarkActionAreaNode);
        executeAction.addEvent("click", function(){
            o2.Actions.load("x_portal_assemble_surface").PortalAction.getCornerMark(this.data.id, function (json) {
                var result = "";
                try{
                    result = JSON.stringify(json, null, 4);
                }catch (e) {}
                this.cornerMarkResultNode.set("text",result);
            }.bind(this), function (xhr) {
                var result;
                try{
                    result = JSON.stringify(xhr.responseText, null, 4);
                }catch (e) {
                    result = xhr.responseText;
                }
                this.cornerMarkResultNode.set("text",result);
            }.bind(this))
        }.bind(this));

        var logAction = new Element("div", {
            "styles": this.app.css.cornerMarkButton,
            "text": lp.openLogView
        }).inject(this.cornerMarkActionAreaNode);
        logAction.addEvent("click", function(){
            layout.openApplication(null, "LogViewer");
        }.bind(this));

        new Element("div", {
            styles: this.app.css.cornerMarkExecuteNoteNode,
            text: lp.runResult
        }).inject( this.cornerMarkRightNode );

        this.cornerMarkResultNode = new Element("div", {
            style: "white-space: pre; font-size: 12px; word-break: break-all; word-wrap: break-word; height: auto; overflow:auto; margin-left:10px;"
        }).inject( this.cornerMarkRightNode );
    },
    createPropertyContentNode: function(){
        this.propertyContentNode = new Element("div", {"styles": {
            "overflow": "hidden",
            "-webkit-user-select": "text",
            "-moz-user-select": "text"
        }}).inject(this.contentAreaNode);

        var html = "<table cellspacing='0' cellpadding='0' border='0' width='95%' align='center' style='margin-top: 20px'>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.name+"</td><td id='formApplicationName'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.alias+"</td><td id='formApplicationAlias'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.description+"</td><td id='formApplicationDescription'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.type+"</td><td id='formApplicationType'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.firstPage+"</td><td id='formApplicationFirstPage'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.pcClient+"</td><td id='formApplicationPcClient'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.mobileClient+"</td><td id='formApplicationMobileClient'></td></tr>";
        // html += "<tr><td class='formTitle'>"+this.app.lp.application.anonymousAccess+"</td><td id='formApplicationAnonymousAccess'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.id+"</td><td id='formApplicationId'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.url+"</td><td id='formApplicationUrl'></td></tr>";
        //     html += "<tr><td class='formTitle'>"+this.app.lp.application.icon+"</td><td id='formApplicationIcon'></td></tr>";
        html += "</table>";
        this.propertyContentNode.set("html", html);
        this.propertyContentNode.getElements("td.formTitle").setStyles(this.app.css.propertyBaseContentTdTitle);

        this.nameInput = new MWF.xApplication.portal.PortalManager.Input(this.propertyContentNode.getElement("#formApplicationName"), this.data.name, this.app.css.formInput);
        this.aliasInput = new MWF.xApplication.portal.PortalManager.Input(this.propertyContentNode.getElement("#formApplicationAlias"), this.data.alias, this.app.css.formInput);
        this.descriptionInput = new MWF.xApplication.portal.PortalManager.Input(this.propertyContentNode.getElement("#formApplicationDescription"), this.data.description, this.app.css.formInput);
        this.typeInput = new MWF.xApplication.portal.PortalManager.Input(this.propertyContentNode.getElement("#formApplicationType"), this.data.portalCategory, this.app.css.formInput);
        this.firstPageInput = new MWF.xApplication.portal.PortalManager.Select(this.propertyContentNode.getElement("#formApplicationFirstPage"), this.data.firstPage, this.app.css.formInput, function(){
            var pages = {};
            this.app.restActions.listPage(this.app.options.application.id, function(json){
                json.data.each(function(page) {
                    pages[page.id] = page.name;
                }.bind(this));
            }.bind(this), null, false);
            return pages;
        }.bind(this));

        this.pcClientInput = new MWF.xApplication.portal.PortalManager.Radio(this.propertyContentNode.getElement("#formApplicationPcClient"), (this.data.pcClient!=undefined) ? this.data.pcClient.toString() : "true", this.app.css.formInput, function(){
            return {
                "true": this.app.lp.application.true,
                "false": this.app.lp.application.false
            };
        }.bind(this));
        this.mobileClientInput = new MWF.xApplication.portal.PortalManager.Radio(this.propertyContentNode.getElement("#formApplicationMobileClient"), (this.data.mobileClient!=undefined) ? this.data.mobileClient.toString():"true", this.app.css.formInput, function(){
            return {
                "true": this.app.lp.application.true,
                "false": this.app.lp.application.false
            };
        }.bind(this));

        // this.anonymousAccessInput = new MWF.xApplication.portal.PortalManager.Radio(
        //     this.propertyContentNode.getElement("#formApplicationAnonymousAccess"),
        //     (this.data.anonymousAccess!=undefined) ? this.data.anonymousAccess.toString():"false",
        //     this.app.css.formInput,
        //     function(){
        //         return {
        //             "true": this.app.lp.application.true,
        //             "false": this.app.lp.application.false
        //         };
        //     }.bind(this))
        // ;

        this.idInput = new MWF.xApplication.portal.PortalManager.Input(this.propertyContentNode.getElement("#formApplicationId"), this.data.id, this.app.css.formInput);

        var host = window.location.host;
        //var port = window.location.port;
        var par = '?id='+this.data.id;
        //var url = "http://"+host+(((!port || port==80) ? "" : ":"+port))+"../x_desktop/portal.html"+par;
        var url = "http://"+host+"/x_desktop/portal.html"+par;
        this.urlInput = new MWF.xApplication.portal.PortalManager.Input(this.propertyContentNode.getElement("#formApplicationUrl"), url, this.app.css.formInput);
    },
    editMode: function(){
        this.nameInput.editMode();
        this.aliasInput.editMode();
        this.descriptionInput.editMode();
        this.typeInput.editMode();
        this.firstPageInput.editMode();
        this.pcClientInput.editMode();
        this.mobileClientInput.editMode();
        // this.anonymousAccessInput.editMode();
        this.isEdit = true;
    },
    readMode: function(){
        this.nameInput.readMode();
        this.aliasInput.readMode();
        this.descriptionInput.readMode();
        this.typeInput.readMode();
        this.firstPageInput.readMode();
        this.pcClientInput.readMode();
        this.mobileClientInput.readMode();
        // this.anonymousAccessInput.readMode();
        this.isEdit = false;
    },
    save: function(callback, cancel) {
        this.data.name = this.nameInput.input.get("value");
        this.data.alias = this.aliasInput.input.get("value");
        this.data.description = this.descriptionInput.input.get("value");
        this.data.portalCategory = this.typeInput.input.get("value") || "";
        this.data.firstPage = this.firstPageInput.input.get("value");
        this.data.pcClient = this.pcClientInput.getValue()==="true";
        this.data.mobileClient = this.mobileClientInput.getValue()==="true";
        // this.data.anonymousAccess = this.anonymousAccessInput.getValue()==="true";

        this.app.restActions.saveApplication(this.data, function (json) {
            this.propertyTitleBar.set("text", this.data.name);
            this.data.id = json.data.id;
            this.nameInput.save();
            this.aliasInput.save();
            this.descriptionInput.save();
            this.typeInput.save();
            this.firstPageInput.save();
            this.pcClientInput.save();
            this.mobileClientInput.save();
            // this.anonymousAccessInput.save();

            if (callback) callback();
        }.bind(this), function (xhr, text, error) {
            if (cancel) cancel(xhr, text, error);
        }.bind(this));
    }
});

MWF.xApplication.portal.PortalManager.Input = new Class({
    Extends: MWF.xApplication.process.ProcessManager.Input,
    Implements: [Events]
});
MWF.xApplication.portal.PortalManager.Select = new Class({
    Extends: MWF.xApplication.portal.PortalManager.Input,
    Implements: [Events],
    initialize: function(node, value, style, select){
        this.node = $(node);
        this.value = (value) ? value: "";
        this.style = style;
        this.select = select;
        this.selectList = null;
        this.load();
    },
    getSelectList: function(){
        if (this.select){
            return this.select();
        }
        return [];
    },
    getText: function(value){
        if (value){
            if (this.selectList){
                return this.selectList[value] || "";
            }
        }
        return "";
    },
    load: function(){
        this.selectList = this.getSelectList();
        this.content = new Element("div", {
            "styles": this.style.content,
            "text": this.getText(this.value)
        }).inject(this.node);
    },
    editMode: function(){
        this.content.empty();
        this.input = new Element("select",{
            //"styles": this.style.input,
            //"value": this.value
        }).inject(this.content);

        Object.each(this.selectList, function(v, k){
            new Element("option", {
                "value": k,
                "text": v,
                "selected": (this.value==k)
            }).inject(this.input);
        }.bind(this));

        //this.input.addEvents({
        //    //"focus": function(){
        //    //    this.input.setStyles(this.style.input_focus);
        //    //}.bind(this),
        //    //"blur": function(){
        //    //    this.input.setStyles(this.style.input);
        //    //}.bind(this),
        //    //"change": function(){
        //    //    this.input.setStyles(this.style.input);
        //    //}.bind(this)
        //});
    },
    readMode: function(){
        this.content.empty();
        this.input = null;
        this.content.set("text", this.getText(this.value));
    },
    save: function(){
        if (this.input && this.input.options && this.input.options.length) {
            this.value = this.input.options[this.input.selectedIndex].get("value");
        }else{
            this.value = "";
        }
        return this.value;
    }
});
MWF.xApplication.portal.PortalManager.Radio = new Class({
    Extends: MWF.xApplication.portal.PortalManager.Select,

    editMode: function(){
        this.content.empty();
        var name = Math.random();
        Object.each(this.selectList, function(v, k){
            var input = new Element("input", {
                "type": "radio",
                "name": "rd"+name,
                "value": k,
                "checked": (this.value===k)
            }).inject(this.content);
            input.appendHTML(v, "after");
        }.bind(this));
    },
    getValue: function(){
        var radios = this.content.getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                this.value = radios[i].value;
                break;
            }
        }
        return this.value;
    },
    save: function(){
        var radios = this.content.getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                this.value = radios[i].value;
                break;
            }
        }
        return this.value;
    }
});