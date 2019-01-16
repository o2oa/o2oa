//MWF.xDesktop.requireApp("Deployment", "Actions.RestActions", null, false);
//MWF.xDesktop.requireApp("Servers", "Actions.RestActions", null, false);
MWF.xApplication.Servers.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Setting",
		"icon": "icon.png",
		"width": "1200",
		"height": "660",
		"title": MWF.xApplication.Servers.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Servers.LP;
        this.actions = new MWF.xApplication.Servers.Actions.RestActions();
	},
	loadApplication: function(callback){
        this.loadTitle();

        this.applicationServerContent = new Element("div", {"styles": this.css.applicationServerContent}).inject(this.content);
        this.applicationServerContentArea = new Element("div", {"styles": this.css.applicationServerContentArea}).inject(this.applicationServerContent);

        this.dataServerContent = new Element("div", {"styles": this.css.applicationServerContent}).inject(this.content);
        this.dataServerContentArea = new Element("div", {"styles": this.css.applicationServerContentArea}).inject(this.dataServerContent);

        this.storageServerContent = new Element("div", {"styles": this.css.applicationServerContent}).inject(this.content);
        this.storageServerContentArea = new Element("div", {"styles": this.css.applicationServerContentArea}).inject(this.storageServerContent);

        this.webServerContent = new Element("div", {"styles": this.css.applicationServerContent}).inject(this.content);
        this.webServerContentArea = new Element("div", {"styles": this.css.applicationServerContentArea}).inject(this.webServerContent);

        //this.componentsContent = new Element("div", {"styles": this.css.componentsContent}).inject(this.appDeploymentContent);

        MWF.require("MWF.widget.Tab", function(){
            this.tab = new MWF.widget.Tab(this.content, {"style": "processlayout"});
            this.tab.load();
            this.appPage = this.tab.addTab(this.applicationServerContent, this.lp.tab_ApplicationServer, false);

            this.dataPage = this.tab.addTab(this.dataServerContent, this.lp.tab_DataServer, false);
            this.dataPage.addEvent("postShow", function(){
                if (!this.dataServerList){
                    MWF.xDesktop.requireApp("Servers", "DataServers", function(){
                        this.dataServerList = new MWF.xApplication.Servers.DataServers(this);
                    }.bind(this));
                }
            }.bind(this));

            this.storagePage = this.tab.addTab(this.storageServerContent, this.lp.tab_StorageServer, false);
            this.storagePage.addEvent("postShow", function(){
                if (!this.storageServerList){
                    MWF.xDesktop.requireApp("Servers", "StorageServers", function(){
                        this.storageServerList = new MWF.xApplication.Servers.StorageServers(this);
                    }.bind(this));
                }
            }.bind(this));

            this.webPage = this.tab.addTab(this.webServerContent, this.lp.tab_WebServer, false);
            this.webPage.addEvent("postShow", function(){
                if (!this.webServerList){
                    MWF.xDesktop.requireApp("Servers", "WebServers", function(){
                        this.webServerList = new MWF.xApplication.Servers.WebServers(this);
                    }.bind(this));
                }
            }.bind(this));

            this.appPage.showIm();
            this.setContentHeight();
            this.addEvent("resize", function(){this.setContentHeight();}.bind(this));
        }.bind(this));

        MWF.xDesktop.requireApp("Servers", "ApplicationServers", function(){
            this.applicationServerList = new MWF.xApplication.Servers.ApplicationServers(this);
        }.bind(this));

        //this.loadApplicationServers();
	},
    loadTitle: function(){
        this.titleBar = new Element("div", {"styles": this.css.titleBar}).inject(this.content);
        this.taskTitleTextNode = new Element("div", {"styles": this.css.titleTextNode,"text": this.lp.title}).inject(this.titleBar);
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