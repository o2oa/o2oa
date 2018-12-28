MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.require("MWF.xDesktop.UserData", null, false);

MWF.xApplication.Execution.IdenitySelector = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "500",
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": false,
        "closeAction": false,
        "closeText" : "",
        "needLogout" : false,
        "isNew": true
    },
    initialize: function (app, actions, identities, css, options) {
        this.setOptions(options);
        this.app = app;
        this.actions = this.app.restActions;
        this.css = css;
        this.options.title = this.app.lp.idenitySelectTitle;
        this.identities = identities;
        this.actions = actions;
        this.loadCss();
    },
    load: function () {
        this.create();
    },
    loadCss: function () {
        this.cssPath = "/x_component_Execution/$Main/" + this.options.style + "/css.wcss";
        this._loadCss();
    },

    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopIconNode = new Element("div", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.options.title
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close();
                }.bind(this));
            }

            this.formTopContentNode = new Element("div", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode);

            this._createTopContent();

        }
    },
    _createTableContent: function () {
        var table = new Element("table",{"width":"100%",border:"0",cellpadding:"5",cellspacing:"0"}).inject(this.formTableArea);
        var tr = new Element("tr").inject(table);
        var td = new Element("td",{valign:"middle"}).inject(tr);
        this.identities.each(function(id,i){
            var name = id.name||id.leaderIdentity.split("@")[0];
            var unit = id.unitName || id["leaderUnitName"].split("@")[0];
            var node = new Element("div", {"styles": this.css["identitySelNode"], "text":name+"("+unit+")"}).inject(td);
            //var node = new Element("div", {"styles": this.css.identitySelNode, "text":id.split("@")[0]}).inject(td);
            node.set("identity",i);
            node.store("id",id);
            node.store("distinguishedName",id.distinguishedName||id.leaderIdentity);
            node.addEvents({
                "mouseover": function(ev){
                    if ( this.selectedNode != ev.target ) ev.target.setStyles(this.css["identitySelNode_over"]);
                }.bind(this),
                "mouseout": function(ev){
                    if ( this.selectedNode != ev.target ) ev.target.setStyles(this.css["identitySelNode_out"]);
                }.bind(this),
                "click": function(ev){
                    this.selected( ev.target );
                }.bind(this),
                "dblclick": function(ev){
                    this.selectedNode = ev.target;
                    this.ok();
                }.bind(this)
            });

        }.bind(this))
    },
    selected: function( node ){
        if( this.selectedNode )this.selectedNode.setStyles( this.css["identitySelNode"]);
        this.selectedNode = node;
        node.setStyles(this.css["identitySelNode_selected"])
    },
    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css["formCancelActionNode"],
            "text": this.options.closeText
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css["formOkActionNode"],
            "text": this.app.lp.comfirm
        }).inject(this.formBottomNode);

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));
    },
    cancel: function(){
        //this.app.close();
        this.fireEvent("postSelectorClose");
    },
    ok: function () {
        if( !this.selectedNode ){
            this.app.notice(this.app.lp.idenitySelecNotice,"error");
        }else{
            var loginData = {};

            loginData.loginIdentity = this.selectedNode.retrieve("distinguishedName");
            if( this.options.needLogout ){
                this.actions.logout( {},function(json){
                    if(json.type && json.type =="success"){
                        this.actions.login( loginData,function(js){
                            MWF.UD.putData("okr_identity_login",{identity:loginData.loginIdentity});

                            if(js.data && js.data.okrManager){ this.app.okrManager = js.data.okrManager}
                            this.fireEvent("postSelectorOk", loginData.loginIdentity );
                            this.close();
                        }.bind(this), function(xhr,text,error){

                        }.bind(this), false);
                    }
                }.bind(this), null, false);
            }else{
                this.actions.login( loginData,function(json){
                    MWF.UD.putData("okr_identity_login",{identity:loginData.loginIdentity});

                    if(json.data && json.data.okrManager){ this.app.okrManager = json.data.okrManager}
                    this.fireEvent("postSelectorOk", loginData.loginIdentity );
                    this.close();
                }.bind(this), null, false);
            }
        }
    }
});