MWF.xApplication.ThreeMember = MWF.xApplication.ThreeMember || {};
MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Access", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xApplication.ThreeMember.PasswordView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "title": MWF.xApplication.ThreeMember.LP.title
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_ThreeMember/$PasswordView/";
        this.cssPath = "../x_component_ThreeMember/$PasswordView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.content = $(node);
        this.lp = MWF.xApplication.ThreeMember.LP;
        this.load();
    },
    reload: function () {
        this.clear();
        this.load();
    },
    load: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": this.css.node
        }).inject(this.content);

        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);

        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.contentContainerNode);

        this.contentScrollNode = new Element("div.contentScrollNode", {
            "styles": this.css.contentScrollNode
        }).inject(this.middleNode);

        this.contentNode = new Element("div.contentNode", {
            "styles": this.css.contentNode
        }).inject(this.contentScrollNode);

         this.bottomNode = new Element("div.bottomNode", {
             "styles": this.css.bottomNode
         }).inject(this.contentContainerNode);
         this.loadActions();

        this.loadForm();

        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);
        this.setContentSize();

    },
    getOffsetY: function (node) {
        return (node.getStyle("margin-top").toInt() || 0) +
            (node.getStyle("margin-bottom").toInt() || 0) +
            (node.getStyle("padding-top").toInt() || 0) +
            (node.getStyle("padding-bottom").toInt() || 0) +
            (node.getStyle("border-top-width").toInt() || 0) +
            (node.getStyle("border-bottom-width").toInt() || 0);
    },
    setContentSize: function () {
        var nodeSize = this.content.getSize();
        var h = nodeSize.y - this.getOffsetY(this.content);

        var topY = this.topContainerNode ? (this.getOffsetY(this.topContainerNode) + this.topContainerNode.getSize().y) : 0;
        h = h - topY;

        var bottomY = this.bottomNode ? (this.getOffsetY(this.bottomNode) + this.bottomNode.getSize().y) : 0;
        h = h - bottomY;

        h = h - this.getOffsetY(this.contentScrollNode);

        this.contentScrollNode.setStyles({
            "height": "" + h + "px",
            "overflow": "auto"
        });
    },
    clear: function () {
        if (this.setContentSizeFun) this.removeEvent("resize", this.setContentSizeFun);
        if( this.form )this.form.destroy();
        if (this.contentContainerNode) {
            this.contentContainerNode.destroy();
        }
        this.node.destroy();
    },
    loadActions: function(){
        this.loadReadModeAction();
    },
    loadReadModeAction: function(){
        this.bottomNode.empty();
        this.saveAction = null;
        this.cancelAction = null;
        this.editAction = new Element("div", {
            "styles" : this.css.inputEditButton,
            "text": this.lp.edit,
            "events":{
                "click": function () {
                    this.form.changeMode(true);
                    this.loadEditModeAction();
                }.bind(this)
            }
        }).inject( this.bottomNode );
    },
    loadEditModeAction: function(){
        this.bottomNode.empty();
        this.editAction = null;
        this.saveAction = new Element("div", {
            "styles" : this.css.inputOkButton,
            "text": this.lp.save,
            "events":{
                "click": function () {
                    var result = this.form.getResult(true, null, true, true);
                    if(!result)return;
                    if(Object.keys(result).length > 0){
                        this.saveForm(result);
                    }else{
                        this.form.changeMode(true);
                        this.loadReadModeAction();
                    }
                }.bind(this)
            }
        }).inject( this.bottomNode );
        this.cancelAction = new Element("div", {
            "styles" : this.css.inputCancelButton,
            "text": this.lp.cancel,
            "events":{
                "click": function () {
                    this.form.changeMode();
                    this.loadReadModeAction();
                }.bind(this)
            }
        }).inject( this.bottomNode );
    },
    saveForm: function(data){
        var actions = [];
        if( data.adminPassword ){
            actions.push(
                o2.Actions.load("x_program_center").ConfigAction.setToken({
                    password: data.adminPassword
                })
            );
            delete data.adminPassword;
        }
        if( data.systemManagerPassword || data.securityManagerPassword || data.auditManagerPassword ) {
            var d = {};
            if (data.systemManagerPassword) {
                d.systemManagerPassword = data.systemManagerPassword;
                delete data.systemManagerPassword;
            }
            if (data.securityManagerPassword){
                d.securityManagerPassword = data.securityManagerPassword;
                delete data.securityManagerPassword;
            }
            if( data.auditManagerPassword ){
                d.auditManagerPassword = data.auditManagerPassword;
                delete data.auditManagerPassword;
            }
            actions.push(
                o2.Actions.load("x_program_center").ConfigAction.setTernaryManagement(d)
            );
        }
        if( Object.keys(data).length > 0 ){
            actions.push(
                o2.Actions.load("x_program_center").ConfigAction.setPerson(data)
            );
        }
        if( actions.length > 0 ){
            Promise.all(actions).then(function () {
                this.app.notice( this.lp.saveSuccess );
                this.form.changeMode();
                this.loadReadModeAction();
            }.bind(this)).catch(function (json) {
                if (json.type === "error") {
                    this.app.notice(json.message, "error");
                }else{
                    this.app.notice( this.lp.saveFailure );
                }
            }.bind(this))
        }
    },
    loadForm: function(){
        this.loadData( function (data) {
            this._loadForm(data)
        }.bind(this))
    },
    loadData: function( callback ){
        var personAction = o2.Actions.load("x_program_center").ConfigAction.getPerson();
        var TMAction = o2.Actions.load("x_program_center").ConfigAction.getTernaryManagement();
        var tokenAction = o2.Actions.load("x_program_center").ConfigAction.getToken();
        Promise.all([personAction, TMAction, tokenAction]).then(function (args) {
            this.personData = args[0].data;
            this.TMData = args[1].data;
            this.tokenData = args[2].data;
            var data = Object.clone(this.personData);
            data.adminPassword = this.tokenData.password;
            data.systemManagerPassword = "";
            data.securityManagerPassword = "";
            data.auditManagerPassword = "";
            // data.systemManagerPassword = this.TMData.systemManagerPassword;
            // data.securityManagerPassword = this.TMData.securityManagerPassword;
            // data.auditManagerPassword = this.TMData.auditManagerPassword;
            callback( data );
        }.bind(this))
    },
    _loadForm: function( data ){
        var lp = this.lp.passwordConfig;
        this.contentNode.set("html", this.getHtml());
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.contentNode, data, {
                isEdited: false,
                style : "setting",
                hasColon : true,
                itemTemplate: {
                    password: { "text": lp.password, tType : "text", notEmpty: true, attr: {"autocomplete": "chrome-off"} },
                    passwordPeriod: { "text": lp.passwordPeriod, tType : "number", attr: {"autocomplete": "off"} },
                    adminPassword: { "text": lp.adminPassword, type : "password", notEmpty: true, attr: {"autocomplete": "chrome-off"} },
                    passwordRegex: { "text": lp.passwordRegex, tType : "text", notEmpty: true, attr: {"autocomplete": "off"} },
                    passwordRegexHint: { "text": lp.passwordRegexHint, tType : "text", notEmpty: true, attr: {"autocomplete": "off"} },
                    failureCount: { "text": lp.failureCount, tType : "number", attr: {"autocomplete": "off"} },
                    failureInterval: { "text": lp.failureInterval, tType : "number", attr: {"autocomplete": "off"} },
                    systemManagerPassword: { "text": lp.systemManagerPassword, type : "password", attr: {"autocomplete": "chrome-off"} },
                    securityManagerPassword: { "text": lp.securityManagerPassword, type : "password", attr: {"autocomplete": "chrome-off"} },
                    auditManagerPassword: { "text": lp.auditManagerPassword, type : "password", attr: {"autocomplete": "chrome-off"} }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    getHtml : function(){
        var lp = this.lp.passwordConfig;
        return  "<div styles='formTitle'>"+lp.title+"</div>"+
        "<table width='90%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +

            "<tr><td styles='formTableTitle'>"+lp.password+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.passwordNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='password'></td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.passwordPeriod+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.passwordPeriodNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='passwordPeriod'></td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.passwordRegex+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.passwordRegexNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='passwordRegex'></td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.passwordRegexHint+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.passwordRegexHintNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='passwordRegexHint'></td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.failureCount+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.failureCountNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='failureCount'></td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.failureInterval+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.failureIntervalNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='failureInterval'></td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.adminPassword+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.adminPasswordNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='adminPassword'></td></tr>" +

            "</table>"+
            "<table width='90%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle'>"+lp.systemManagerPassword+"</td>" +
            "   <td styles='formTableTitle'>"+lp.securityManagerPassword+"</td>" +
            "   <td styles='formTableTitle'>"+lp.auditManagerPassword+"</td>" +
            "</tr>" +
            "<tr>" +
            "   <td styles='formTableNote'>"+lp.systemManagerPasswordNote+"</td>" +
            "   <td styles='formTableNote'>"+lp.securityManagerPasswordNote+"</td>" +
            "   <td styles='formTableNote'>"+lp.auditManagerPasswordNote+"</td>" +
            "</tr>" +
            "<tr>" +
            "   <td styles='formTableValue' item='systemManagerPassword'></td>" +
            "   <td styles='formTableValue' item='securityManagerPassword'></td>" +
            "   <td styles='formTableValue' item='auditManagerPassword'></td>" +
            "</tr>" +
            "</table>"

    },
    recordStatus: function () {
        var status = this.navi.currentStatus || {};
        status.explorer = "passwordview";
        return status;
    }
});
