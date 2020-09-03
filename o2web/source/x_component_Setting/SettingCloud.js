MWF.xDesktop.requireApp("Setting", "Document", null, false);
MWF.xApplication.Setting.CloudConnectDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.cloud_connectSetting);

        var o2CloudConnected = false;
        this.actions.collectConnected(function(json){
            o2CloudConnected = json.data.value;
        }.bind(this), function(json){
            o2CloudConnected = false;
        }.bind(this), false);

        this.mobileO2CloudConnectInput = new MWF.xApplication.Setting.Document.Button(this.explorer, this.node, {
            "lp": {"title": this.lp.mobile_connectO2Cloud, "infor": this.mobile_connectO2Cloud_infor, "action": this.lp.mobile_connectO2Cloud_action},
            //"data": {"key": "proxyData", "valueKey": "ssos", "notEmpty": false },
            "value": o2CloudConnected ? this.lp.mobile_connectO2Cloud_success : this.lp.mobile_connectO2Cloud_error,
            "itemTitle": o2CloudConnected ? this.lp.mobile_connectO2Cloud_success : this.lp.mobile_connectO2Cloud_error,
            "icon": o2CloudConnected ? "cloud.png" : "cloud_error.png",
            "action": function(e){
                layout.desktop.openApplication(e, "Collect");
            }.bind(this)
        });

        this.mobileHttpProtocolInput = new MWF.xApplication.Setting.Document.Select(this.explorer, this.node, {
            "lp": {"title": this.lp.mobile_httpProtocol, "infor": this.lp.mobile_httpProtocol_infor},
            "data": {"key": "proxyData", "valueKey": "httpProtocol"},
            "value": this.explorer.proxyData.httpProtocol,
            "options": [{"value": "http", "text": "http"}, {"value": "https", "text": "https"}]
        });

        this.mobileCenterInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": {"title": this.lp.mobile_center, "infor": this.lp.mobile_center_infor, "editAction": this.lp.mobile_center_action},
            "data": {"key": "proxyData", "valueKey": "center", "notEmpty": false},
            "value": this.explorer.proxyData.center,
            "itemTitle": "{proxyHost}:{proxyPort}",
            "icon": "center.png"
        });

        this.mobileWebInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": {"title": this.lp.mobile_web, "infor": this.lp.mobile_web_infor, "editAction": this.lp.mobile_web_action},
            "data": {"key": "proxyData", "valueKey": "web", "notEmpty": false},
            "value": this.explorer.proxyData.web,
            "itemTitle": "{proxyHost}:{proxyPort}",
            "icon": "webserver.png"
        });

        this.mobileApplicationInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": {"title": this.lp.mobile_application, "infor": this.lp.mobile_application_infor, "editAction": this.lp.mobile_application_action},
            "data": {"key": "proxyData", "valueKey": "applicationList", "notEmpty": false},
            "value": this.explorer.proxyData.applicationList,
            "itemTitle": "{proxyHost}:{proxyPort}",
            "readonly": ["node"],
            "icon": "server.png"
        });
    }
});