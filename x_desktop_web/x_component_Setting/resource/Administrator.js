MWF.xDesktop.requireApp("Setting", "servers.DataServers", null, false);
MWF.xApplication.Setting.resource = MWF.xApplication.Setting.resource || {};
MWF.xApplication.Setting.resource.Administrator = new Class({
    Extends: MWF.xApplication.Setting.servers.DataServer.Document,
    Implements: [Events],
    initialize: function(explorer){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.container = this.explorer.adminContent;
        this.css = this.explorer.app.css;
        this.load();
    },
    load: function(){
        this.app.actions.getResAdministrator(function(json){
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
            "<tr><td width='120px'>id</td><td>"+(this.json.id || "")+"</td></tr>" +
            "<tr><td>name</td><td>"+(this.json.name || "")+"</td></tr>" +
            "<tr><td>password</td><td><input value='"+(this.json.password || "")+"'/></td></tr>" +
            "<tr><td>employee</td><td><input value='"+(this.json.employee || "")+"'/></td></tr>" +
            "<tr><td>display</td><td><input value='"+(this.json.display || "")+"'/></td></tr>" +
            "<tr><td>mobile</td><td><input value='"+(this.json.mobile || "")+"'/></td></tr>" +
            "<tr><td>mail</td><td><input value='"+(this.json.mail || "")+"'/></td></tr>" +
            "<tr><td>weixin</td><td><input value='"+(this.json.qq || "")+"'/></td></tr>" +
            "<tr><td>qq</td><td><input value='"+(this.json.cipher || "")+"'/></td></tr>" +
            "<tr><td>weibo</td><td><input value='"+(this.json.weibo || "")+"'/></td></tr>" +
            "<tr><td>roleList</td><td><textarea>"+this.json.roleList.join(", ")+"</textarea></td></tr>" +
            "<tr><td>icon</td><td><textarea>"+(this.json.icon || "")+"</textarea></td></tr>" +
            "</table>";


        this.inforNode.set("html", html);
        var tds = this.inforNode.getElements("td");
        var inputs = this.inforNode.getElements("input");
        var textareas = this.inforNode.getElements("textarea");

        tds.setStyles(this.css.applicationServerDocumentTdNode);
        inputs.setStyles(this.css.applicationServerDocumentInputNode);
        textareas.setStyles(this.css.applicationServerDocumentTextareasNode);
    },
    saveDocument: function(){
        var inputs = this.inforNode.getElements("input");
        var textareas = this.inforNode.getElements("textarea");

        this.json.password = inputs[0].get("value");
        this.json.employee = inputs[1].get("value");
        this.json.display = inputs[2].get("value");
        this.json.mobile = inputs[3].get("value");
        this.json.mail = inputs[4].get("value");
        this.json.weixin = inputs[5].get("value");
        this.json.qq = inputs[6].get("value");
        this.json.weibo = inputs[7].get("value");
        this.json.roleList = textareas[0].get("value").split(/,\s*/g);
        this.json.icon = textareas[1].get("value");

        this.app.actions.updateResAdministrator(this.json, function(){
            this.app.notice(this.app.lp.centerSaveInfor, "success");
        }.bind(this));
    },
    destroy: function(){
        if (this.node) this.node.destroy();
        MWF.release(this);
    },
});