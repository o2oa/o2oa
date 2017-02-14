MWF.xDesktop.requireApp("Setting", "servers.ApplicationServers", null, false);
MWF.xApplication.Setting.servers = MWF.xApplication.Setting.servers || {};
MWF.xApplication.Setting.servers.DataServers = new Class({
    Extends: MWF.xApplication.Setting.servers.ApplicationServers,
    Implements: [Options, Events],

    initialize: function(explorer){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.servers = [];
        this.content = this.explorer.dataServerContent;
        this.page = this.app.serverPage;
        this.currentDoc = null;
        this.load();
    },
    load: function(){
        this.actions.listDataServer(function(json){
            json.data.each(function(serverJson){
                this.servers.push(new MWF.xApplication.Setting.servers.DataServer(this, serverJson));
            }.bind(this));

            this.createAddAction();
            this.setServerAreaWidth();
            this.setServerAreaWidthFun = this.setServerAreaWidth.bind(this);
            this.addEvent("resize", this.setServerAreaWidthFun);
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
                this.list.servers.push(new MWF.xApplication.Setting.servers.DataServer(this.list, this.json));
            }
        }
        this.currentDoc = new MWF.xApplication.Setting.servers.DataServer.Document(server);
    },
    setServerAreaWidth: function(){
        var count = this.servers.length;
        var width = this.explorer.contentAreaNode.getSize().x;
        var nodeWidth = this.servers[0].node.getSize().x;
        var x1 = this.servers[0].node.getStyle("margin-left").toInt();
        var x2 = this.servers[0].node.getStyle("margin-right").toInt();
        nodeWidth = nodeWidth+x1+x2;

        var n = (width/nodeWidth).toInt();
        var w = nodeWidth*n;
        this.content.setStyle("width", ""+w+"px");
    },
    destroy: function(){
        if (this.setServerAreaWidthFun) this.app.removeEvent("resize", this.setServerAreaWidthFun);
        this.servers.each(function(server){
            server.destroy();
        }.bind(this));
        if (this.currentDoc) this.currentDoc.destroy();
        this.content.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.Setting.servers.DataServer = new Class({
    Extends: MWF.xApplication.Setting.servers.ApplicationServer,
    Implements: [Events],
    initialize: function(list, json){
        this.list = list;
        this.explorer = this.list.explorer;
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
        this.adminNode.set("text", (this.json.databaseType || ""));
        this.messageNode.set("text", (this.json.message || ""));
    },
    open: function(){
        new MWF.xApplication.Setting.servers.DataServer.Document(this);
    },
    reload: function(){
        this.app.actions.getDataServer(this.json.name, function(json){
            this.name = this.json.name;
            this.json = json.data;
            this.nameNode.set("text", this.json.name);
            this.hostNode.set("text", this.json.host+" : "+this.json.port);
            this.adminNode.set("text", this.json.databaseType);
            this.messageNode.set("text", this.json.message);
        }.bind(this));
    }
});
MWF.xApplication.Setting.servers.DataServer.Document = new Class({
    Extends: MWF.xApplication.Setting.servers.ApplicationServer.Document,
    Implements: [Events],

    setDocumentSize: function(){
        var size = this.list.page.contentNodeArea.getSize();
        var actionSize = this.actionNode.getSize();
        var h = size.y-actionSize.y;
        this.inforNode.setStyle("height", ""+h+"px");
        //this.applicationAreaNode.setStyle("height", ""+h+"px");
        //this.inforNode.setStyle("min-height", ""+h+"px");
        this.node.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px",
        });
    },
    createForm: function(){
        this.createActions();
        this.createBaseInfo();
    },
    createActions: function(){
        this.actionNode = new Element("div", {"styles": this.css.applicationServerDocumentActionNode}).inject(this.node);
        this.saveAction = new Element("div", {"styles": this.css.applicationServerDocumentSaveNode}).inject(this.actionNode);
        if (this.server.name) this.deleteAction = new Element("div", {"styles": this.css.applicationServerDocumentDeleteNode}).inject(this.actionNode);
        this.closeAction = new Element("div", {"styles": this.css.applicationServerDocumentCloseNode}).inject(this.actionNode);

        this.saveAction.addEvents({
            "mouseover": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode_over);}.bind(this),
            "mouseout": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode);}.bind(this),
            "mousedown": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode_down);}.bind(this),
            "mouseup": function(){this.saveAction.setStyles(this.css.applicationServerDocumentSaveNode_over);}.bind(this),
            "click": function(e){this.saveDocument();}.bind(this)
        });

        if (this.deleteAction){
            this.deleteAction.addEvents({
                "mouseover": function(){this.deleteAction.setStyles(this.css.applicationServerDocumentDeleteNode_over);}.bind(this),
                "mouseout": function(){this.deleteAction.setStyles(this.css.applicationServerDocumentDeleteNode);}.bind(this),
                "mousedown": function(){this.deleteAction.setStyles(this.css.applicationServerDocumentDeleteNode_down);}.bind(this),
                "mouseup": function(){this.deleteAction.setStyles(this.css.applicationServerDocumentDeleteNode_over);}.bind(this),
                "click": function(e){this.deleteDocument(e);}.bind(this)
            });
        }

        this.closeAction.addEvents({
            "mouseover": function(){this.closeAction.setStyles(this.css.applicationServerDocumentCloseNode_over);}.bind(this),
            "mouseout": function(){this.closeAction.setStyles(this.css.applicationServerDocumentCloseNode);}.bind(this),
            "mousedown": function(){this.closeAction.setStyles(this.css.applicationServerDocumentCloseNode_down);}.bind(this),
            "mouseup": function(){this.closeAction.setStyles(this.css.applicationServerDocumentCloseNode_over);}.bind(this),
            "click": function(e){this.closeDocument();}.bind(this)
        });
    },
    createBaseInfo: function(){
        this.inforAreaNode = new Element("div", {"styles": this.css.applicationServerDocumentInforAreaNode}).inject(this.node);
        this.inforNode = new Element("div", {"styles": this.css.dataServerDocumentInforNode}).inject(this.inforAreaNode);
        var html = "<table cellSpacing='8px' width='90%' align='center'>" +
            "<tr><td width='160px'>name</td><td><input value='"+(this.json.name || "")+"'/></td></tr>" +
            "<tr><td>description</td><td><input value='"+(this.json.description || "")+"'/></td></tr>" +
            "<tr><td>order</td><td><input value='"+(this.json.order || "0")+"'/></td></tr>" +
            "<tr><td>databaseType</td><td><select>" +
            "<option value='postgreSQL' "+((this.json.databaseType=="postgreSQL") ? "selected" : "")+">postgreSQL</option>" +
            "<option value='mysql' "+((this.json.databaseType=="mysql") ? "selected" : "")+">mysql</option>" +
            "<option value='db2' "+((this.json.databaseType=="db2") ? "selected" : "")+">db2</option>" +
            "<option value='oracle' "+((this.json.databaseType=="oracle") ? "selected" : "")+">oracle</option>" +
            "</select></td></tr>" +
            "<tr><td>host</td><td><input value='"+(this.json.host || "")+"'/></td></tr>" +
            "<tr><td>port</td><td><input value='"+(this.json.port || "")+"'/></td></tr>" +
            "<tr><td>database</td><td><input value='"+(this.json.database || "")+"'/></td></tr>" +
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
        this.json.description = inputs[1].get("value");
        this.json.order = inputs[2].get("value");
        this.json.host = inputs[3].get("value");
        this.json.port = inputs[4].get("value");
        this.json.database = inputs[5].get("value");
        this.json.username = inputs[6].get("value");
        this.json.password = inputs[7].get("value");

        var select = this.inforNode.getElement("select");
        this.json.databaseType = select.options[select.selectedIndex].value;

        if (this.server.name){
            this.app.actions.updateAppServer(this.server.name, this.json, function(){
                this.closeDocument();
                this.server.reload();
            }.bind(this));
        }else{
            this.app.actions.addDataServer(this.json, function(){
                this.closeDocument();
                this.server.reload();
            }.bind(this));
        }
    },
    deleteDocument: function(e){
        var _self = this;
        this.app.confirm("warn", e, this.app.lp.deleteDataServer_title, this.app.lp.deleteDataServer, "350", "120", function(){
            _self.app.actions.removeDataServer(_self.server.name, function(){
                this.closeDocument();
                this.server.destroy();
            }.bind(_self));
            this.close();
        }, function(){
            this.close();
        });
    }
});



