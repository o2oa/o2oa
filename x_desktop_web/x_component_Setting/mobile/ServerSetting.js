MWF.xDesktop.requireApp("Setting", "servers.DataServers", null, false);
MWF.xApplication.Setting.mobile = MWF.xApplication.Setting.mobile || {};
MWF.xApplication.Setting.mobile.ServerSetting = new Class({
    Extends: MWF.xApplication.Setting.servers.DataServer.Document,
    Implements: [Events],
    initialize: function(explorer){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.content = this.explorer.serverSettingContent;
        this.page = this.app.mobilePage;
        this.json = null;
        this.webServers = [];
        this.applicationServers = [];
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.centerServerDocumentNode}).inject(this.content);
        this.createActions();
        this.centerServerNode = new Element("div", {"styles": {"overflow": "hidden", "border-bottom": "0px solid #999"}}).inject(this.node);
        this.webServerNode = new Element("div", {"styles": {"overflow": "hidden", "border-bottom": "0px solid #999"}}).inject(this.node);
        this.applicationServerNode = new Element("div", {"styles": {"overflow": "hidden", "border-bottom": "0px solid #999"}}).inject(this.node);

        this.centerServerTitleNode = new Element("div", {"styles": this.css.mobileServerTitleNode, "text": "Center Server"}).inject(this.centerServerNode);
        this.webServerTitleNode = new Element("div", {"styles": this.css.mobileServerTitleNode, "text": "Web Server"}).inject(this.webServerNode);
        this.applicationServerTitleNode = new Element("div", {"styles": this.css.mobileServerTitleNode, "text": "Applicaton Server"}).inject(this.applicationServerNode);

        this.centerServerListNode = new Element("div", {"styles": this.css.mobileCenterServerListNode}).inject(this.centerServerNode);
        this.webServerListNode = new Element("div", {"styles": this.css.mobileServerListNode}).inject(this.webServerNode);
        this.applicationServerListNode = new Element("div", {"styles": this.css.mobileServerListNode}).inject(this.applicationServerNode);

        this.app.actions.getCenterServer(function(json){
            this.json = json.data;
            this.createCenterForm();
        }.bind(this));

        this.actions.listWebServer(function(json){
            json.data.each(function(serverJson){
                this.webServers.push(new MWF.xApplication.Setting.mobile.ServerSetting.WebServer(this, serverJson));
            }.bind(this));
        }.bind(this));

        this.actions.listApplicationServer(function(json){
            json.data.each(function(serverJson){
                this.applicationServers.push(new MWF.xApplication.Setting.mobile.ServerSetting.ApplicationServer(this, serverJson));
            }.bind(this));
        }.bind(this));
    },
    createActions: function(){
        this.actionNode = new Element("div", {"styles": this.css.applicationServerDocumentActionNode}).inject(this.node);
        this.saveAction = new Element("div", {"styles": this.css.applicationServerDocumentSaveNode}).inject(this.actionNode);
        //this.closeAction = new Element("div", {"styles": this.css.applicationServerDocumentCloseNode}).inject(this.actionNode);

        this.saveAction.addEvents({
            "mouseover": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode_over);}.bind(this),
            "mouseout": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode);}.bind(this),
            "mousedown": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode_down);}.bind(this),
            "mouseup": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode_over);}.bind(this),
            "click": function(e){this.saveDocument();}.bind(this)
        });
    },
    createCenterForm: function(){
        this.inforAreaNode = new Element("div", {"styles": this.css.applicationServerDocumentInforAreaNode}).inject(this.centerServerListNode);
        this.inforNode = new Element("div", {"styles": this.css.dataServerDocumentInforNode}).inject(this.inforAreaNode);
        var html = "<table cellSpacing='8px' width='90%' align='center'>" +
            "<tr><td width='160px'>proxyHost</td><td><input value='"+(this.json.proxyHost || "")+"'/></td></tr>" +
            "<tr><td>proxyPort</td><td><input value='"+(this.json.proxyPort || "")+"'/></td></tr>" +
            "</table>";

        this.inforNode.set("html", html);
        var tds = this.inforNode.getElements("td");
        var inputs = this.inforNode.getElements("input");

        tds.setStyles(this.css.applicationServerDocumentTdNode);
        inputs.setStyles(this.css.applicationServerDocumentInputNode);
    },
    checkSave: function(){
        var count = this.webServers.length+this.applicationServers.length+1;
        if (this.savedServers>=count){
            if (!this.errorServer.length){
                this.app.notice(this.app.lp.mobileServerSaveInfor, "success");
            }else{
                var errorText = "";
                this.errorServer.each(function(server){
                    errorText = errorText+"; \n"+server.server.json.name+": "+server.message;
                }.bind(this));
                var text = this.app.lp.mobileServerSaveErrorInfor.replace(/{error}/, errorText);
                this.app.notice(text, "error");
            }
        }
    },
    saveDocument: function(){
        this.savedServers = 0;
        this.errorServer = [];
        this.webServers.each(function(ser){
            ser.saveDocument(function(){
                this.savedServers++;
                this.checkSave();
            }.bind(this), function(server, message){
                this.savedServers++;
                this.errorServer.push({"server": server, "message": message});
                this.checkSave();
            }.bind(this));
        }.bind(this));


        this.applicationServers.each(function(ser){
            ser.saveDocument(function(){
                this.savedServers++;
                this.checkSave();
            }.bind(this), function(server, message){
                this.savedServers++;
                this.errorServer.push({"server": server, "message": message});
                this.checkSave();
            }.bind(this));
        }.bind(this));


        var inputs = this.inforNode.getElements("input");
        this.json.proxyHost = inputs[0].get("value");
        this.json.proxyPort = inputs[1].get("value");

        this.app.actions.updateCenterServer(this.json, function(){
            this.savedServers++;
            this.checkSave();
        }.bind(this), function(xhr){
            var json = JSON.decode(xhr.responseText);
            this.savedServers++;
            this.errorServer.push({"server": {"json": this.json}, "message": json.message});
            this.checkSave();
        }.bind(this));
    },
    destroy: function(){
        this.webServers.each(function(ser){
            ser.destroy();
        }.bind(this));
        this.applicationServers.each(function(ser){
            ser.destroy();
        }.bind(this));

        if (this.node) this.node.destroy();
        MWF.release(this);
    },
});
MWF.xApplication.Setting.mobile.ServerSetting.WebServer = new Class({
    initialize: function(setting, json){
        this.setting = setting;
        this.app = this.setting.app;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.json = json;
        this.content = this.setting.webServerListNode;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.mobileServerNode}).inject(this.content);
        this.createForm();
    },
    createForm: function(){
        this.inforAreaNode = new Element("div", {"styles": this.css.applicationServerDocumentInforAreaNode}).inject(this.node);
        this.inforNode = new Element("div", {"styles": this.css.dataServerDocumentInforNode}).inject(this.inforAreaNode);
        var html = "<table cellSpacing='8px' width='90%' align='center'>" +
            "<tr><td width='160px'>name</td><td>"+(this.json.name || "")+"</td></tr>" +
            "<tr><td width='160px'>proxyHost</td><td><input value='"+(this.json.proxyHost || "")+"'/></td></tr>" +
            "<tr><td>proxyPort</td><td><input value='"+(this.json.proxyPort || "")+"'/></td></tr>" +
            "</table>";

        this.inforNode.set("html", html);
        var tds = this.inforNode.getElements("td");
        var inputs = this.inforNode.getElements("input");

        tds.setStyles(this.css.applicationServerDocumentTdNode);
        inputs.setStyles(this.css.applicationServerDocumentInputNode);
    },
    destroy: function(){
        if (this.node) this.node.destroy();
        MWF.release(this);
    },

    saveDocument: function(success, error){
        var inputs = this.inforNode.getElements("input");
        this.json.proxyHost = inputs[0].get("value");
        this.json.proxyPort = inputs[1].get("value");

        this.app.actions.updateWebServer(this.json.name, this.json, function(){
            if (success) success();
        }.bind(this), function(xhr){
            var json = JSON.decode(xhr.responseText);
            if (error) error.apply(this, [this, json.message]);
        }.bind(this));
    },
});
MWF.xApplication.Setting.mobile.ServerSetting.ApplicationServer = new Class({
    Extends: MWF.xApplication.Setting.mobile.ServerSetting.WebServer,
    initialize: function(setting, json){
        this.setting = setting;
        this.app = this.setting.app;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.json = json;
        this.content = this.setting.applicationServerListNode;
        this.load();
    },
    saveDocument: function(success, error){
        var inputs = this.inforNode.getElements("input");
        this.json.proxyHost = inputs[0].get("value");
        this.json.proxyPort = inputs[1].get("value");

        this.app.actions.updateAppServer(this.json.name, this.json, function(){
            if (success) success();
        }.bind(this), function(xhr){
            var json = JSON.decode(xhr.responseText);
            if (error) error.apply(this, [this, json.message]);
        }.bind(this));
    },
});