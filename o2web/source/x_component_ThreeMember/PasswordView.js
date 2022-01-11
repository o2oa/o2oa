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
        this.clearContent();
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

        h = h - this.getOffsetY(this.contentScrollNode);

        this.contentScrollNode.setStyles({
            "height": "" + h + "px",
            "overflow": "auto"
        });
    },
    clearContent: function () {
        if (this.setContentSizeFun) this.removeEvent("resize", this.setContentSizeFun);
        if( this.form )this.form.destroy();
        if (this.contentContainerNode) {
            this.contentContainerNode.destroy();
        }
        this.node.destroy();
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
            // data.systemManagerPassword = this.TMData.systemManagerPassword;
            // data.securityManagerPassword = this.TMData.securityManagerPassword;
            // data.auditManagerPassword = this.TMData.auditManagerPassword;
            callback( data );
        }.bind(this))
    },
    _loadForm: function( data ){
        this.contentNode.set("html", this.getHtml());
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.contentNode, data, {
                isEdited: false,
                style : "setting",
                hasColon : true,
                itemTemplate: {
                    password: { tType : "text" },
                    passwordPeriod: { tType : "text" },
                    adminPassword: { tType : "text" },
                    passwordRegex: { tType : "text" },
                    passwordRegexHint: { tType : "text" },
                    failureCount: { tType : "number" },
                    failureInterval: { tType : "number" },
                    systemManagerPassword: { tType : "text" },
                    securityManagerPassword: { tType : "text" },
                    auditManagerPassword: { tType : "text" },
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    getHtml : function(){
        var lp = this.lp.passwordConfig;
        return  "<div styles='formTitle'>"+lp.title+"</div>"+
        "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitle'>"+lp.password+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.passwordNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='password'></td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.passwordPeriod+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.passwordPeriodNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='passwordPeriod'></td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.adminPassword+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.adminPasswordNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='adminPassword'></td></tr>" +

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
            "</table>"+
            "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle'>"+lp.systemManagerPassword+"</td>" +
            "   <td styles='formTableTitle'>"+lp.securityManagerPassword+"</td>" +
            "   <td styles='formTableTitle'>"+lp.auditManagerPassword+"</td>" +
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
