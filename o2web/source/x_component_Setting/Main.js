MWF.xDesktop.requireApp("Setting", "Document", null, false);
MWF.xDesktop.requireApp("Setting", "SettingBase", null, false);
MWF.xDesktop.requireApp("Setting", "SettingMobile", null, false);
MWF.xDesktop.requireApp("Setting", "SettingCloud", null, false);
MWF.xDesktop.requireApp("Setting", "SettingLoginUI", null, false);
MWF.xDesktop.requireApp("Setting", "SettingIndexUI", null, false);
MWF.xDesktop.requireApp("Setting", "SettingModuleUI", null, false);
MWF.xDesktop.requireApp("Setting", "SettingModuleService", null, false);
MWF.xDesktop.requireApp("Setting", "SettingModuleResource", null, false);

MWF.xApplication.Setting.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "Setting",
        "icon": "icon.png",
        "width": "1020",
        "height": "660",
        "title": MWF.xApplication.Setting.LP.title
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.Setting.LP;
        this.actions = MWF.Actions.get("x_program_center");
        //this.actions = new MWF.xApplication.Setting.Actions.RestActions();
    },
    loadApplication: function(callback){
        debugger;

        this.baseAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);
        //this.uiAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);
        this.mobileAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);
        this.cloudAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);

        this.disposeAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);

        // this.serverAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);
        // this.applicationAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);
        // this.resourceAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);
        // this.mobileAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);


        MWF.require("MWF.widget.Tab", function(){
            this.tab = new MWF.widget.Tab(this.content, {"style": "administrator"});
            this.tab.load();

            this.basePage = this.tab.addTab(this.baseAreaNode, this.lp.tab_base, false);
            // this.uiPage = this.tab.addTab(this.uiAreaNode, this.lp.tab_ui, false);
            this.cloudPage = this.tab.addTab(this.cloudAreaNode, this.lp.tab_cloud, false);
            this.mobilePage = this.tab.addTab(this.mobileAreaNode, this.lp.tab_mobile, false);
            this.disposePage = this.tab.addTab(this.disposeAreaNode, this.lp.tab_dispose, false);

            this.basePage.addEvent("postShow", function(){
                if (!this.baseExplorer) this.baseExplorer = new MWF.xApplication.Setting.BaseExplorer(this, this.baseAreaNode);
            }.bind(this));

            // this.uiPage.addEvent("postShow", function(){
            //     if (!this.uiExplorer) this.uiExplorer = new MWF.xApplication.Setting.UIExplorer(this, this.uiAreaNode);
            // }.bind(this));

            this.mobilePage.addEvent("postShow", function(){
                if (!this.mobileExplorer) this.mobileExplorer = new MWF.xApplication.Setting.MobileExplorer(this, this.mobileAreaNode);
            }.bind(this));

            this.cloudPage.addEvent("postShow", function(){
                if (!this.cloudExplorer) this.cloudExplorer = new MWF.xApplication.Setting.CloudExplorer(this, this.cloudAreaNode);
            }.bind(this));


            this.disposePage.addEvent("postShow", function(){
                if (!this.disposeExplorer) this.disposeExplorer = new MWF.xApplication.Setting.DisposeExplorer(this, this.disposeAreaNode);
            }.bind(this));


            // this.serverPage = this.tab.addTab(this.serverAreaNode, this.lp.tab_Server, false);
            // this.applicationPage = this.tab.addTab(this.applicationAreaNode, this.lp.tab_Application, false);
            // this.resourcePage = this.tab.addTab(this.resourceAreaNode, this.lp.tab_Resource, false);
            // this.mobilePage = this.tab.addTab(this.mobileAreaNode, this.lp.tab_Mobile, false);

            // this.serverPage.addEvent("postShow", function(){
            //     if (!this.serversExplorer) this.serversExplorer = new MWF.xApplication.Setting.ServersExplorer(this);
            // }.bind(this));
            //
            // this.applicationPage.addEvent("postShow", function(){
            //     if (!this.applicationExplorer) this.applicationExplorer = new MWF.xApplication.Setting.ApplicationsExplorer(this);
            // }.bind(this));
            //
            // this.resourcePage.addEvent("postShow", function(){
            //     if (!this.resourceExplorer) this.resourceExplorer = new MWF.xApplication.Setting.ResourceExplorer(this);
            // }.bind(this));
            //
            // this.mobilePage.addEvent("postShow", function(){
            //     if (!this.mobileExplorer) this.mobileExplorer = new MWF.xApplication.Setting.MobileExplorer(this);
            // }.bind(this));

            this.basePage.showIm();
            //this.loadServers();

            this.setContentHeight();
            this.addEvent("resize", function(){this.setContentHeight();}.bind(this));
        }.bind(this));

        //MWF.xDesktop.requireApp("Setting", "ApplicationServers", function(){
        //    this.applicationServerList = new MWF.xApplication.Setting.ApplicationServers(this);
        //}.bind(this));

        //this.loadApplicationServers();
    },


    loadTitle: function(){
        this.titleBar = new Element("div", {"styles": this.css.titleBar}).inject(this.content);
        this.taskTitleTextNode = new Element("div", {"styles": this.css.titleTextNode,"text": this.lp.title}).inject(this.titleBar);
    },
    setContentHeight: function(node){
        var size = this.content.getSize();
        //var titleSize = this.titleBar.getSize();
        var tabSize = this.tab.tabNodeContainer.getSize();
        //var height = size.y-tabSize.y-titleSize.y;
        var height = size.y-tabSize.y;

        this.tab.pages.each(function(page){
            page.contentNodeArea.setStyles({"height": ""+height+"px", "overflow": "auto"})
        });

        //this.appDeploymentContent.setStyle("height", height);
    }

});

MWF.xApplication.Setting.BaseExplorer = new Class({
    Implements: [Events],
    initialize: function(app, content){
        this.app = app;
        this.lp = this.app.lp;
        this.container = content;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.naviItems = [];
        this.collectData = null;
        this.personData = null;
        this.tokenData = null;
        this.portalData = null;
        this.loadDataBack = null;
        this.publicData = null;
        this.load();
    },
    getData: function(){
        var checkData = function(){
            if (this.collectData && this.personData &&  this.tokenData && this.portalData && this.publicData){
                if (this.loadDataBack){
                    var fun = this.loadDataBack;
                    this.loadDataBack = null;
                    fun();
                }
            }
        }.bind(this);
        this.actions.getCollect(function(json){
            this.collectData = json.data;
            checkData();
        }.bind(this));
        this.actions.getPerson(function(json){
            this.personData = json.data;
            checkData();
        }.bind(this));
        this.actions.getToken(function(json){
            this.tokenData = json.data;
            checkData();
        }.bind(this));
        this.actions.getPortal(function(json){
            this.portalData = json.data;
            checkData();
        }.bind(this));
        //o2.UD.deletePublicData("faceKeys");
        o2.UD.getPublicData("faceKeys", function(json){
            if (json){
                if (json["api-key"]){
                    json.api_key = json["api-key"];
                    delete json["api-key"]
                }
                if (json["api_secret"]){
                    json.api_secret = json["api_secret"];
                    delete json["api_secret"]
                }
            }
            this.publicData = json || {"api": {"api_key":"", "api_secret":""}};
            checkData();
        }.bind(this));
    },
    load: function(){
        if (MWF.AC.isAdministrator()) this.getData();
        this.naviAreaNode = new Element("div", {"styles": this.css.explorerNaviAreaNode}).inject(this.container);
        this.naviNode = new Element("div", {"styles": this.css.explorerNaviNode}).inject(this.naviAreaNode);
        this.contentAreaNode = new Element("div", {"styles": this.css.explorerContentAreaNode}).inject(this.container);
        this.loadNavi();
    },
    loadNavi: function(){
        var json = this.getNaviJson();
        json.each(function(navi){
            this.naviItems.push(this.createNaviItem(navi));
        }.bind(this));
        this.naviItems[0].click();
    },
    createNaviItem: function(navi){
        var naviItemNode = new Element("div", {"styles": this.css.naviItemNode}).inject(this.naviNode);
        //var naviItemIconNode = new Element("div", {"styles": this.css.naviItemIconNode}).inject(naviItemNode);
        naviItemNode.setStyle("background-image", "url(../x_component_Setting/$Main/default/icon/"+navi.icon+".png)");
        //var naviItemTextNode = new Element("div", {"styles": this.css.naviItemTextNode}).inject(naviItemNode);
        naviItemNode.set("text", navi.text);
        naviItemNode.store("navi", navi);

        var _self = this;
        naviItemNode.addEvent("click", function(){
            _self.setNavi(this);
        });
        return naviItemNode;
    },
    setNavi: function(item){
        var navi = item.retrieve("navi");
        this.naviItems.each(function(node){
            var itemNavi = node.retrieve("navi");
            var content = node.retrieve("content", null);

            if (content) content.destroy();
            node.eliminate("content");
            node.setStyles(this.css.naviItemNode);
            node.setStyle("background-image", "url(../x_component_Setting/$Main/default/icon/"+itemNavi.icon+".png)");
        }.bind(this));

        item.setStyles(this.css.naviItemNode_current);
        item.setStyle("background-image", "url(../x_component_Setting/$Main/default/icon/"+navi.icon+"_current.png)");
        if (this[navi.action]) this[navi.action](item);
    },
    getNaviJson: function(){
        return [
            {
                "text": this.app.lp.tab_name,
                "icon": "name",
                "action": "loadSystemNameSetting"
            },
            {
                "text": this.app.lp.tab_user,
                "icon": "user",
                "action": "loadSystemPersonSetting"
            },
            {
                "text": this.app.lp.tab_login,
                "icon": "login",
                "action": "loadSystemLoginSetting"
            },
            {
                "text": this.app.lp.tab_sso,
                "icon": "sso",
                "action": "loadSystemSSOSetting"
            },
            {
                "text": this.app.lp.tab_config,
                "icon": "config",
                "action": "loadSystemConfigSetting"
            }
        ];
    },
    loadSystemConfigSetting:function(item){
        if (MWF.AC.isAdministrator()) {
            var appId = "ConfigDesigner";
            if (layout.desktop.apps["ConfigDesigner"]){
                layout.desktop.apps[appId].setCurrent();
                return;
            }
            layout.openApplication(null, "ConfigDesigner",{
                "appId": appId
            });
        }
    },
    loadSystemNameSetting: function(item){
        if (MWF.AC.isAdministrator()) {
            if (this.collectData && this.personData &&  this.tokenData){
                this.baseNameSetting = new MWF.xApplication.Setting.BaseNameDocument(this, this.contentAreaNode);
                item.store("content", this.baseNameSetting);
            }else{
                this.loadDataBack = function(){this.loadSystemNameSetting(item)}.bind(this);
            }
        }

    },
    loadSystemPersonSetting: function(item){
        if (MWF.AC.isAdministrator()) if (this.collectData && this.personData &&  this.tokenData){
            this.basePersonSetting = new MWF.xApplication.Setting.BasePersonDocument(this, this.contentAreaNode);
            item.store("content", this.basePersonSetting);
        }else{
            this.loadDataBack = function(){this.loadSystemPersonSetting(item)}.bind(this);
        }
    },
    loadSystemLoginSetting: function(item){
        if (MWF.AC.isAdministrator()) if (this.collectData && this.personData &&  this.tokenData){
            this.baseLoginSetting = new MWF.xApplication.Setting.BaseLoginDocument(this, this.contentAreaNode);
            item.store("content", this.baseLoginSetting);
        }else{
            this.loadDataBack = function(){this.loadSystemLoginSetting(item)}.bind(this);
        }
    },
    loadSystemSSOSetting: function(item){
        if (MWF.AC.isAdministrator()) if (this.collectData && this.personData &&  this.tokenData){
            this.baseLoginSetting = new MWF.xApplication.Setting.BaseSSODocument(this, this.contentAreaNode);
            item.store("content", this.baseLoginSetting);
        }else{
            this.loadDataBack = function(){this.loadSystemSSOSetting(item)}.bind(this);
        }
    }

});
MWF.xApplication.Setting.MobileExplorer = new Class({
    Extends: MWF.xApplication.Setting.BaseExplorer,
    initialize: function(app, content){
        this.app = app;
        this.lp = this.app.lp;
        this.container = content;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.naviItems = [];
        this.collectData = null;
        this.personData = null;
        this.tokenData = null;
        this.loadDataBack = null;
        this.load();
    },

    getNaviJson: function(){
        return [
            {
                "text": this.app.lp.tab_mobile_module,
                "icon": "module",
                "action": "loadMobileModuleSetting"
            },
            {
                "text": this.app.lp.tab_mobile_style,
                "icon": "style",
                "action": "loadMobileStyleSetting"
            },
            {
                "text": this.app.lp.tab_mobile_mpweixin_menu,
                "icon": "style",
                "action": "loadMPWeixinMenuSetting"
            },
            {
                "text": this.app.lp.tab_mobile_app_pack,
                "icon": "service",
                "action": "loadAppPackSetting"
            }
        ];
    },
    getData: function(){
        var checkData = function(){
            if (this.proxyData && this.nativeData && this.imagesData){
                if (this.loadDataBack){
                    var fun = this.loadDataBack;
                    this.loadDataBack = null;
                    fun();
                }
            }
        }.bind(this);
        this.actions.getProxy(function(json){
            this.proxyData = json.data;
            checkData();
        }.bind(this));
        this.actions.mobile_currentStyle(function(json){
            this.nativeData = {"indexType": json.data.indexType, "indexPortal": json.data.indexPortal, "simpleMode": json.data.simpleMode, "nativeAppList": Array.clone(json.data.nativeAppList)};
            this.imagesData = {"images": Array.clone(json.data.images)};
            //this.indexData = {"indexType": json.data.indexType, "indexId": json.data.indexId};
            this.portalData = {"portalList": Array.clone(json.data.portalList)};

            delete json.data;
            json = null;
            checkData();
        }.bind(this));
        //微信菜单数据
        o2.Actions.load("x_program_center").MPWeixinAction.menuWeixinList(function (json) {
            if (json.data && json.data.button) {
                this.mpweixinListData = json.data.button;
            } else {
                this.mpweixinListData = [];
            }
        }.bind(this));

    },

    // loadMobileConnectSetting: function(item){
    //     if (MWF.AC.isAdministrator()) if (this.proxyData && this.nativeData && this.imagesData){
    //         this.mobileConnectSetting = new MWF.xApplication.Setting.MobileConnectDocument(this, this.contentAreaNode);
    //         item.store("content", this.mobileConnectSetting);
    //     }else{
    //         this.loadDataBack = function(){this.loadMobileConnectSetting(item)}.bind(this);
    //     }
    // },

    loadMobileModuleSetting: function(item){
        if (MWF.AC.isAdministrator()) if (this.proxyData && this.nativeData && this.imagesData){
            this.mobileModuleSetting = new MWF.xApplication.Setting.MobileModuleDocument(this, this.contentAreaNode);
            item.store("content", this.mobileModuleSetting);
        }else{
            this.loadDataBack = function(){this.loadMobileModuleSetting(item)}.bind(this);
        }
    },

    loadMobileStyleSetting: function(item){
        if (MWF.AC.isAdministrator()) if (this.proxyData && this.nativeData && this.imagesData){
            this.mobileStyleSetting = new MWF.xApplication.Setting.MobileStyleDocument(this, this.contentAreaNode);
            item.store("content", this.mobileStyleSetting);
        }else{
            this.loadDataBack = function(){this.loadMobileStyleSetting(item)}.bind(this);
        }
    },
    loadMPWeixinMenuSetting: function(item) {
        if (MWF.AC.isAdministrator()) if (this.proxyData && this.nativeData && this.imagesData){
            this.mpweixinMenuSetting = new MWF.xApplication.Setting.MPWeixinMenuSettingDocument(this, this.contentAreaNode);
            item.store("content", this.mpweixinMenuSetting);
        }else{
            this.loadDataBack = function(){this.loadMPWeixinMenuSetting(item)}.bind(this);
        }
    },
    loadAppPackSetting: function(item) {
        if (MWF.AC.isAdministrator()) {
            this.mobileAppPackOnline = new MWF.xApplication.Setting.AppPackOnlineDocument(this, this.contentAreaNode);
            item.store("content", this.mobileAppPackOnline);
        }
    }
});
MWF.xApplication.Setting.CloudExplorer = new Class({
    Extends: MWF.xApplication.Setting.BaseExplorer,
    initialize: function(app, content){
        this.app = app;
        this.lp = this.app.lp;
        this.container = content;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.naviItems = [];
        this.collectData = null;
        this.personData = null;
        this.tokenData = null;
        this.loadDataBack = null;
        this.load();
    },

    getNaviJson: function(){
        return [
            {
                "text": this.app.lp.tab_cloud_connect,
                "icon": "connect",
                "action": "loadCloudConnectSetting"
            }
        ];
    },
    getData: function(){
        var checkData = function(){
            if (this.proxyData && this.nativeData && this.imagesData){
                if (this.loadDataBack){
                    var fun = this.loadDataBack;
                    this.loadDataBack = null;
                    fun();
                }
            }
        }.bind(this);
        this.actions.getProxy(function(json){
            this.proxyData = json.data;
            checkData();
        }.bind(this));
        this.actions.mobile_currentStyle(function(json){
            this.nativeData = {"indexType": json.data.indexType, "indexPortal": json.data.indexPortal, "nativeAppList": Array.clone(json.data.nativeAppList)};
            this.imagesData = {"images": Array.clone(json.data.images)};
            //this.indexData = {"indexType": json.data.indexType, "indexId": json.data.indexId};
            this.portalData = {"portalList": Array.clone(json.data.portalList)};

            delete json.data;
            json = null;
            checkData();
        }.bind(this));

    },
    loadCloudConnectSetting: function(item){
        if (MWF.AC.isAdministrator()) if (this.proxyData && this.nativeData && this.imagesData){
            this.mobileConnectSetting = new MWF.xApplication.Setting.CloudConnectDocument(this, this.contentAreaNode);
            item.store("content", this.mobileConnectSetting);
        }else{
            this.loadDataBack = function(){this.loadCloudConnectSetting(item)}.bind(this);
        }
    },

});
MWF.xApplication.Setting.UIExplorer = new Class({
    Extends: MWF.xApplication.Setting.BaseExplorer,
    initialize: function(app, content){
        this.app = app;
        this.lp = this.app.lp;
        this.container = content;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.naviItems = [];
        this.load();
    },

    getNaviJson: function(){
        return [
            {
                "text": this.app.lp.tab_ui_index,
                "icon": "index",
                "action": "loadUIIndexSetting"
            },
            {
                "text": this.app.lp.tab_ui_login,
                "icon": "login",
                "action": "loadUILoginSetting"
            }
        ];
    },
    getData: function(){

    },

    loadUILoginSetting: function(item){
        if (MWF.AC.isAdministrator()){
            this.uiLoginSetting = new MWF.xApplication.Setting.UILoginDocument(this, this.contentAreaNode);
            item.store("content", this.uiLoginSetting);
        }
    },

    loadUIIndexSetting: function(item){
        if (MWF.AC.isAdministrator()) {
            this.uiIndexSetting = new MWF.xApplication.Setting.UIIndexDocument(this, this.contentAreaNode);
            item.store("content", this.uiIndexSetting);
        }
    }
});
MWF.xApplication.Setting.DisposeExplorer = new Class({
    Extends: MWF.xApplication.Setting.BaseExplorer,
    initialize: function(app, content){
        this.app = app;
        this.lp = this.app.lp;
        this.container = content;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.naviItems = [];
        this.load();
    },

    getNaviJson: function(){
        return [
            {
                "text": this.app.lp.tab_ui_module,
                "icon": "module",
                "action": "loadUIModuleSetting"
            },
            {
                "text": this.app.lp.tab_ui_resource,
                "icon": "resource",
                "action": "loadResourceModuleSetting"
            },
            {
                "text": this.app.lp.tab_ui_service,
                "icon": "service",
                "action": "loadServiceModuleSetting"
            }
        ];
    },
    getData: function(){

    },
    loadUIModuleSetting: function(item){
        if (MWF.AC.isAdministrator()) {
            this.uiModuleSetting = new MWF.xApplication.Setting.UIModuleDocument(this, this.contentAreaNode);
            item.store("content", this.uiModuleSetting);
        }
    },
    loadServiceModuleSetting: function(item){
        if (MWF.AC.isAdministrator()) {
            this.serviceModuleSetting = new MWF.xApplication.Setting.ServiceModuleDocument(this, this.contentAreaNode);
            item.store("content", this.serviceModuleSetting);
        }
    },
    loadResourceModuleSetting: function(item){
        if (MWF.AC.isAdministrator()) {
            this.resourceModuleSetting = new MWF.xApplication.Setting.ResourceModuleDocument(this, this.contentAreaNode);
            item.store("content", this.resourceModuleSetting);
        }
    }
});







