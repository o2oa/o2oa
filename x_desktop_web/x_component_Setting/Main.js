//MWF.xDesktop.requireApp("Deployment", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Setting", "Actions.RestActions", null, false);
MWF.xApplication.Setting.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Setting",
		"icon": "icon.png",
		"width": "1280",
		"height": "660",
		"title": MWF.xApplication.Setting.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Setting.LP;
        this.actions = new MWF.xApplication.Setting.Actions.RestActions();
	},
	loadApplication: function(callback){

        this.serverAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);
        this.applicationAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);
        this.resourceAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);
        this.mobileAreaNode = new Element("div", {"styles": this.css.tabAreaNode}).inject(this.content);


        MWF.require("MWF.widget.Tab", function(){
            this.tab = new MWF.widget.Tab(this.content, {"style": "administrator"});
            this.tab.load();

            this.serverPage = this.tab.addTab(this.serverAreaNode, this.lp.tab_Server, false);
            this.applicationPage = this.tab.addTab(this.applicationAreaNode, this.lp.tab_Application, false);
            this.resourcePage = this.tab.addTab(this.resourceAreaNode, this.lp.tab_Resource, false);
            this.mobilePage = this.tab.addTab(this.mobileAreaNode, this.lp.tab_Mobile, false);

            this.serverPage.addEvent("postShow", function(){
                if (!this.serversExplorer) this.serversExplorer = new MWF.xApplication.Setting.ServersExplorer(this);
            }.bind(this));

            this.applicationPage.addEvent("postShow", function(){
                if (!this.applicationExplorer) this.applicationExplorer = new MWF.xApplication.Setting.ApplicationsExplorer(this);
            }.bind(this));

            this.resourcePage.addEvent("postShow", function(){
                if (!this.resourceExplorer) this.resourceExplorer = new MWF.xApplication.Setting.ResourceExplorer(this);
            }.bind(this));

            this.mobilePage.addEvent("postShow", function(){
                if (!this.mobileExplorer) this.mobileExplorer = new MWF.xApplication.Setting.MobileExplorer(this);
            }.bind(this));

            this.serverPage.showIm();
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

MWF.xApplication.Setting.ServersExplorer = new Class({
    Implements: [Events],
    initialize: function(app){
        this.app = app;
        this.container = this.app.serverAreaNode;
        this.css = this.app.css;
        this.naviItems = [];
        this.load();
    },
    load: function(){
        this.naviAreaNode = new Element("div", {"styles": this.css.serverNaviAreaNode}).inject(this.container);
        this.naviNode = new Element("div", {"styles": this.css.serverNaviNode}).inject(this.naviAreaNode);
        this.contentAreaNode = new Element("div", {"styles": this.css.serverContentAreaNode}).inject(this.container);
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
        naviItemNode.setStyle("background-image", "url(/x_component_Setting/$Main/default/icon/"+navi.icon+".png)")
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
            node.setStyle("background-image", "url(/x_component_Setting/$Main/default/icon/"+itemNavi.icon+".png)");
        }.bind(this));

        item.setStyles(this.css.naviItemNode_current);
        item.setStyle("background-image", "url(/x_component_Setting/$Main/default/icon/"+navi.icon+"_current.png)");
        if (this[navi.action]) this[navi.action](item);
    },
    loadApplicationServers: function(item){
        this.applicationServerContent = new Element("div", {"styles": this.css.applicationServerContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "servers.ApplicationServers", function(){
            this.applicationServerList = new MWF.xApplication.Setting.servers.ApplicationServers(this);
            item.store("content", this.applicationServerList);
        }.bind(this));
    },
    loadDataServers: function(item){
        this.dataServerContent = new Element("div", {"styles": this.css.applicationServerContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "servers.DataServers", function(){
            this.dataServerList = new MWF.xApplication.Setting.servers.DataServers(this);
            item.store("content", this.dataServerList);
        }.bind(this));
    },
    loadStorageServers: function(item){
        this.storageServerContent = new Element("div", {"styles": this.css.applicationServerContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "servers.StorageServers", function(){
            this.storageServerList = new MWF.xApplication.Setting.servers.StorageServers(this);
            item.store("content", this.storageServerList);
        }.bind(this));
    },
    loadWebServers: function(item){
        this.webServerContent = new Element("div", {"styles": this.css.applicationServerContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "servers.WebServers", function(){
            this.webServerList = new MWF.xApplication.Setting.servers.WebServers(this);
            item.store("content", this.webServerList);
        }.bind(this));
    },
    loadCenterServer: function(item){

        this.centerServerContent = new Element("div", {"styles": this.css.applicationServerContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "servers.CenterServer", function(){
            this.centerServerList = new MWF.xApplication.Setting.servers.CenterServer(this);
            item.store("content", this.centerServerList);
        }.bind(this));
    },

    getNaviJson: function(){
        return [
            {
                "text": this.app.lp.tab_centerServer,
                "icon": "center",
                "action": "loadCenterServer"
            },
            {
                "text": this.app.lp.tab_ApplicationServer,
                "icon": "application",
                "action": "loadApplicationServers"
            },
            {
                "text": this.app.lp.tab_WebServer,
                "icon": "web",
                "action": "loadWebServers"
            },
            {
                "text": this.app.lp.tab_DataServer,
                "icon": "data",
                "action": "loadDataServers"
            },
            {
                "text": this.app.lp.tab_StorageServer,
                "icon": "storage",
                "action": "loadStorageServers"
            }
        ];
    }
});

MWF.xApplication.Setting.ApplicationsExplorer = new Class({
    Extends: MWF.xApplication.Setting.ServersExplorer,
    initialize: function(app){
        this.app = app;
        this.container = this.app.applicationAreaNode;
        this.css = this.app.css;
        this.naviItems = [];
        this.load();
    },

    getNaviJson: function(){
        return [
            {
                "text": "Applications",
                "icon": "app",
                "action": "loadApplications"
            },
            {
                "text": "Datas",
                "icon": "data",
                "action": "loadDatas"
            },
            //{
            //    "text": "DataMappings",
            //    "icon": "data",
            //    "action": "loadDataMappings"
            //},
            {
                "text": "Storages",
                "icon": "storage",
                "action": "loadStorages"
            }
            //{
            //    "text": "StorageMappings",
            //    "icon": "storage",
            //    "action": "loadStorageMappings"
            //}
        ];
    },
    loadApplications: function(item){
        this.applicationsContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "applications.Applications", function(){
            this.applicationList = new MWF.xApplication.Setting.applications.Applications(this);
            item.store("content", this.applicationList);
        }.bind(this));
    },
    loadDatas: function(item){
        this.datasContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "applications.Datas", function(){
            this.dataList = new MWF.xApplication.Setting.applications.Datas(this);
            item.store("content", this.dataList);
        }.bind(this));
    },
    loadStorages: function(item){
        this.storagesContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "applications.Storages", function(){
            this.storageList = new MWF.xApplication.Setting.applications.Storages(this);
            item.store("content", this.storageList);
        }.bind(this));
    },
});

MWF.xApplication.Setting.ResourceExplorer = new Class({
    Extends: MWF.xApplication.Setting.ServersExplorer,
    initialize: function(app){
        this.app = app;
        this.container = this.app.resourceAreaNode;
        this.css = this.app.css;
        this.naviItems = [];
        this.load();
    },

    getNaviJson: function(){
        return [
            {
                "text": "Administrator",
                "icon": "admin",
                "action": "loadAdministrator"
            },
            {
                "text": "Collect",
                "icon": "collect",
                "action": "loadCollect"
            },
            {
                "text": "OpenMeeting",
                "icon": "openmeeting",
                "action": "loadOpenMeeting"
            },
            {
                "text": "Password",
                "icon": "password",
                "action": "loadPassword"
            },
            {
                "text": "Person",
                "icon": "person",
                "action": "loadPerson"
            },
            {
                "text": "SSO",
                "icon": "sso",
                "action": "loadSSO"
            },
            {
                "text": "WorkTime",
                "icon": "time",
                "action": "loadWorkTime"
            }
        ];
    },

    loadAdministrator: function(item){
        this.adminContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "resource.Administrator", function(){
            this.adminConfig = new MWF.xApplication.Setting.resource.Administrator(this);
            item.store("content", this.adminConfig);
        }.bind(this));
    },
    loadCollect: function(item){
        this.collectContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "resource.Collect", function(){
            this.collectConfig = new MWF.xApplication.Setting.resource.Collect(this);
            item.store("content", this.collectConfig);
        }.bind(this));
    },
    loadOpenMeeting: function(item){
        this.openmeetingContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "resource.Openmeeting", function(){
            this.openmeetingConfig = new MWF.xApplication.Setting.resource.Openmeeting(this);
            item.store("content", this.openmeetingConfig);
        }.bind(this));
    },
    loadPassword: function(item){
        this.passwordContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "resource.Password", function(){
            this.passwordConfig = new MWF.xApplication.Setting.resource.Password(this);
            item.store("content", this.passwordConfig);
        }.bind(this));
    },
    loadPerson: function(item){
        this.personContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "resource.Person", function(){
            this.personConfig = new MWF.xApplication.Setting.resource.Person(this);
            item.store("content", this.personConfig);
        }.bind(this));
    },
    loadSSO: function(item){
        this.ssoContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "resource.SSO", function(){
            this.ssoConfig = new MWF.xApplication.Setting.resource.SSO(this);
            item.store("content", this.ssoConfig);
        }.bind(this));
    },
    loadWorkTime: function(item){
        this.worktimeContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "resource.Worktime", function(){
            this.worktimeConfig = new MWF.xApplication.Setting.resource.Worktime(this);
            item.store("content", this.worktimeConfig);
        }.bind(this));
    },
});

MWF.xApplication.Setting.MobileExplorer = new Class({
    Extends: MWF.xApplication.Setting.ServersExplorer,
    initialize: function(app){
        this.app = app;
        this.container = this.app.mobileAreaNode;
        this.css = this.app.css;
        this.naviItems = [];
        this.load();
    },

    getNaviJson: function(){
        return [
            {
                "text": this.app.lp.mobileSetting1,
                "icon": "online",
                "action": "checkOnlineSetting"
            },
            {
                "text": this.app.lp.mobileSetting2,
                "icon": "account",
                "action": "accountSetting"
            },
            {
                "text": this.app.lp.mobileSetting3,
                "icon": "application",
                "action": "serverSetting"
            }
        ];
    },
    checkOnlineSetting: function(item){
        this.checkOnlineContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "mobile.CheckOnline", function(){
            this.checkOnline = new MWF.xApplication.Setting.mobile.CheckOnline(this);
            item.store("content", this.checkOnline);
        }.bind(this));
    },
    accountSetting: function(item){
        this.accountContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "mobile.Account", function(){
            this.account = new MWF.xApplication.Setting.mobile.Account(this);
            item.store("content", this.account);
        }.bind(this));
    },
    serverSetting: function(item){
        this.serverSettingContent = new Element("div", {"styles": this.css.applicationsContent}).inject(this.contentAreaNode);
        MWF.xDesktop.requireApp("Setting", "mobile.ServerSetting", function(){
            this.server = new MWF.xApplication.Setting.mobile.ServerSetting(this);
            item.store("content", this.server);
        }.bind(this));
    },
});

