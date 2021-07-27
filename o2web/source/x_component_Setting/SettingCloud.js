MWF.xDesktop.requireApp("Setting", "Document", null, false);
MWF.xApplication.Setting.CloudConnectDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        
        // 第一步 O2云连接检查
        this.firstStepTitleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.firstStepTitleName.set("text", this.lp.mobile_connect_first_cloud_check);
        // 查询是否已经连接登录到O2云
        var o2CloudConnected = false;
        this.actions.collectValidate(function(json){
            o2CloudConnected = json.data.value;
        }.bind(this), function(json){
            o2CloudConnected = false;
        }.bind(this), false);
        this.firstStepSubTitle = new Element("div", {"styles": this.explorer.css.explorerContentTitleSubNode}).inject(this.node);
        this.firstStepSubTitle.set("html", o2CloudConnected ? this.lp.mobile_connect_first_cloud_check_connected : this.lp.mobile_connect_first_cloud_check_disconnected);
        
        // 第二步 外网连接配置
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.mobile_connect_connectSetting);

         
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

        // 第三步 检查连接用的二维码
        this.thirdStepTitleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.thirdStepTitleName.set("text", this.lp.mobile_connect_third_mobile_connect_check);
        this.thirdStepSubTitle = new Element("div", {"styles": this.explorer.css.explorerContentTitleSubNode}).inject(this.node);
        this.thirdStepSubTitle.set("html", this.lp.mobile_connect_third_mobile_connect_check_info);
        // 生成二维码按钮
        this.thirdStepQrcodeNode = new Element("div", {"styles": this.css.explorerContentItemQrcodeNode}).inject(this.node);

        this.thirdStepQrcodeGenerateButton = new Element("div", {"styles": this.css.explorerContentItemButton, "html": this.lp.mobile_connect_third_mobile_connect_check_generate_button}).inject(this.thirdStepQrcodeNode);
        this.thirdStepQrcodeGenerateButton.addEvent("click", this.showQrcode.bind(this));

    },
    showQrcode: function() {
        o2.Actions.load("x_program_center").CollectAction.mobileCheckConnect(function(json){
            this.thirdStepQrcodeNode.empty();
            // 生成二维码
            new Element("img", {
                src: "data:image/png;base64," + json.data.qrcode
            }).inject(this.thirdStepQrcodeNode);

        }.bind(this));
    }
});