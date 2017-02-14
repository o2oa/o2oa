MWF.xDesktop.requireApp("Setting", "servers.DataServers", null, false);
MWF.xApplication.Setting.resource = MWF.xApplication.Setting.resource || {};
MWF.xApplication.Setting.resource.Worktime = new Class({
    Extends: MWF.xApplication.Setting.servers.DataServer.Document,
    Implements: [Events],
    initialize: function(explorer){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.container = this.explorer.worktimeContent;
        this.css = this.explorer.app.css;
        this.load();
    },
    load: function(){
        this.app.actions.getResWorktime(function(json){
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
            "<tr><td width='120px'>amStart</td><td><input value='"+(this.json.amStart || "")+"'/></td></tr>" +
            "<tr><td>amEnd</td><td><input value='"+(this.json.amEnd || "")+"'/></td></tr>" +
            "<tr><td>pmStart</td><td><input value='"+(this.json.pmStart || "")+"'/></td></tr>" +
            "<tr><td>pmEnd</td><td><input value='"+(this.json.pmEnd || "")+"'/></td></tr>" +
            "<tr><td>weekends</td><td><textarea>"+this.json.weekends.join(", ")+"</textarea></td></tr>" +
            "<tr><td>holidays</td><td><textarea>"+this.json.holidays.join(", ")+"</textarea></td></tr>" +
            "<tr><td>workdays</td><td><textarea>"+this.json.workdays.join(", ")+"</textarea></td></tr>" +
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

        this.json.amStart = inputs[0].get("value");
        this.json.amEnd = inputs[1].get("value");
        this.json.pmStart = inputs[2].get("value");
        this.json.pmEnd = inputs[3].get("value");

        this.json.weekends = textareas[0].get("value").split(/,\s*/g);
        this.json.holidays = textareas[1].get("value").split(/,\s*/g);
        this.json.workdays = textareas[2].get("value").split(/,\s*/g);

        this.app.actions.updateResWorktime(this.json, function(){
            this.app.notice(this.app.lp.centerSaveInfor, "success");
        }.bind(this));
    },
    destroy: function(){
        if (this.node) this.node.destroy();
        MWF.release(this);
    },
});