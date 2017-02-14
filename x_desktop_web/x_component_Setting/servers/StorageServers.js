MWF.xDesktop.requireApp("Setting", "servers.DataServers", null, false);
MWF.xApplication.Setting.servers = MWF.xApplication.Setting.servers || {};
MWF.xApplication.Setting.servers.StorageServers = new Class({
    Extends: MWF.xApplication.Setting.servers.DataServers,
    Implements: [Options, Events],

    initialize: function(explorer){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.servers = [];
        this.content = this.explorer.storageServerContent;
        this.page = this.app.serverPage;
        this.load();
    },
    load: function(){
        this.actions.listStorageServer(function(json){
            json.data.each(function(serverJson){
                this.servers.push(new MWF.xApplication.Setting.servers.StorageServer(this, serverJson));
            }.bind(this));

            this.createAddAction();
            this.setServerAreaWidthFun = this.setServerAreaWidth.bind(this);
            this.addEvent("resize", this.setServerAreaWidthFun);
            this.addEvent("resize", function(){this.setStorageServerAreaWidth();}.bind(this));
        }.bind(this));
    },
    createServer: function(){
        var server = {
            "list": this,
            "app": this.app,
            "json": {
                "contextList": [],
                "planList": []
            },
            "node": this.createServerAction,
            "name": "",
            "reload": function(){
                this.list.servers.push(new MWF.xApplication.Setting.servers.StorageServer(this.list, this.json));
            }
        }
        var doc = new MWF.xApplication.Setting.servers.StorageServer.Document(server);
    }
});
MWF.xApplication.Setting.servers.StorageServer = new Class({
    Extends: MWF.xApplication.Setting.servers.DataServer,
    Implements: [Events],
    initialize: function(list, json){
        this.list = list
        this.app = this.list.app;
        this.json = json;
        this.container = this.list.content;
        this.css = this.app.css;
        this.name = this.json.name;
        this.load();
    },
    setServerText: function(){
        this.nameNode.set("text", (this.json.name || ""));
        this.hostNode.set("text", (this.json.host || "")+" : "+(this.json.port || ""));
        this.adminNode.set("text", (this.json.username || ""));
        this.messageNode.set("text", (this.json.message || ""));
    },
    open: function(){
        new MWF.xApplication.Setting.servers.StorageServer.Document(this);
    },
    reload: function(){
        this.app.actions.getStorageServer(this.json.name, function(json){
            this.name = this.json.name;
            this.json = json.data;
            this.nameNode.set("text", this.json.name);
            this.hostNode.set("text", this.json.host+" : "+this.json.port);
            this.adminNode.set("text", this.json.username);
            this.messageNode.set("text", this.json.message);
        }.bind(this));
    }
});
MWF.xApplication.Setting.servers.StorageServer.Document = new Class({
    Extends: MWF.xApplication.Setting.servers.DataServer.Document,
    Implements: [Events],

    createBaseInfo: function(){
        this.inforAreaNode = new Element("div", {"styles": this.css.applicationServerDocumentInforAreaNode}).inject(this.node);
        this.inforNode = new Element("div", {"styles": this.css.dataServerDocumentInforNode}).inject(this.inforAreaNode);
        var html = "<table cellSpacing='8px' width='90%' align='center'>" +
            "<tr><td width='160px'>name</td><td><input value='"+(this.json.name || "")+"'/></td></tr>" +
            "<tr><td>order</td><td><input value='"+(this.json.order || "0")+"'/></td></tr>" +
            "<tr><td>storageServiceType</td><td><select>" +
            "<option value='ftp' "+((this.json.storageServiceType=="ftp") ? "selected" : "")+">ftp</option>" +
            "</select></td></tr>" +
            "<tr><td>host</td><td><input value='"+(this.json.host || "")+"'/></td></tr>" +
            "<tr><td>port</td><td><input value='"+(this.json.port || "")+"'/></td></tr>" +
            "<tr><td>path</td><td><input value='"+(this.json.path || "")+"'/></td></tr>" +
            "<tr><td>username</td><td><input value='"+(this.json.username || "")+"'/></td></tr>" +
            "<tr><td>password</td><td><input type='password' value='"+(this.json.password || "")+"'/></td></tr>" +
            "</table>";

        this.inforNode.set("html", html);
        var tds = this.inforNode.getElements("td");
        var inputs = this.inforNode.getElements("input");

        tds.setStyles(this.css.applicationServerDocumentTdNode);
        inputs.setStyles(this.css.applicationServerDocumentInputNode);
    },
    saveDocument: function(){
        var inputs = this.inforNode.getElements("input");
        this.json.name = inputs[0].get("value");
        this.json.order = inputs[1].get("value");
        this.json.host = inputs[2].get("value");
        this.json.port = inputs[3].get("value");
        this.json.path = inputs[4].get("value");
        this.json.username = inputs[5].get("value");
        this.json.password = inputs[6].get("value");

        var select = this.inforNode.getElement("select");
        this.json.storageServiceType = select.options[select.selectedIndex].value;

        if (this.server.name){
            this.app.actions.updateStorageServer(this.server.name, this.json, function(){
                this.closeDocument();
                this.server.reload();
            }.bind(this));
        }else{
            this.app.actions.addStorageServer(this.json, function(){
                this.closeDocument();
                this.server.reload();
            }.bind(this));
        }
    },
    deleteDocument: function(e){
        var _self = this;
        this.app.confirm("warn", e, this.app.lp.deleteStorageServer_title, this.app.lp.deleteStorageServer, "350", "120", function(){
            _self.app.actions.removeStorageServer(_self.server.name, function(){
                this.closeDocument();
                this.server.destroy();
            }.bind(_self));
            this.close();
        }, function(){
            this.close();
        });
    }
});