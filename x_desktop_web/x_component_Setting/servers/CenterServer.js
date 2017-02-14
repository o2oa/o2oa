MWF.xDesktop.requireApp("Setting", "servers.DataServers", null, false);
MWF.xApplication.Setting.servers = MWF.xApplication.Setting.servers || {};
MWF.xApplication.Setting.servers.CenterServer = new Class({
    Extends: MWF.xApplication.Setting.servers.DataServer.Document,
    Implements: [Events],
    initialize: function(explorer){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.container = this.explorer.centerServerContent;
        this.css = this.explorer.app.css;
        this.load();
    },
    load: function(){
        this.app.actions.getCenterServer(function(json){
            this.json = json.data;
            this.node = new Element("div", {"styles": this.css.centerServerDocumentNode}).inject(this.container);
            this.createForm();
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

    createBaseInfo: function(){
        this.inforAreaNode = new Element("div", {"styles": this.css.applicationServerDocumentInforAreaNode}).inject(this.node);
        this.inforNode = new Element("div", {"styles": this.css.dataServerDocumentInforNode}).inject(this.inforAreaNode);
        var html = "<table cellSpacing='8px' width='90%' align='center'>" +
            "<tr><td width='160px'>host</td><td><input value='"+(this.json.host || "")+"'/></td></tr>" +
            "<tr><td>port</td><td><input value='"+(this.json.port || "")+"'/></td></tr>" +
            "<tr><td>cipher</td><td><input value='"+(this.json.cipher || "")+"'/></td></tr>" +
            "<tr><td>proxyHost</td><td><input value='"+(this.json.proxyHost || "")+"'/></td></tr>" +
            "<tr><td>proxyPort</td><td><input value='"+(this.json.proxyPort || "")+"'/></td></tr>" +
            "</table>";

        this.inforNode.set("html", html);
        var tds = this.inforNode.getElements("td");
        var inputs = this.inforNode.getElements("input");

        tds.setStyles(this.css.applicationServerDocumentTdNode);
        inputs.setStyles(this.css.applicationServerDocumentInputNode);
    },
    saveDocument: function(){
        var inputs = this.inforNode.getElements("input");
        this.json.host = inputs[0].get("value");
        this.json.port = inputs[1].get("value");
        this.json.cipher = inputs[2].get("value");
        this.json.proxyHost = inputs[3].get("value");
        this.json.proxyPort = inputs[4].get("value");

        this.app.actions.updateCenterServer(this.json, function(){
            this.app.notice(this.app.lp.centerSaveInfor, "success");
        }.bind(this));
    },
    destroy: function(){
        if (this.node) this.node.destroy();
        MWF.release(this);
    },
});