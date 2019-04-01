MWF.xDesktop.requireApp("Setting", "Document", null, false);
MWF.xApplication.Setting.MobileConnectDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.mobile_connectSetting);

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

MWF.xApplication.Setting.MobileModuleDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.mobile_moduleSetting);

        var options = [{"value": "", "text": this.lp.mobile_index_defalue}];
        if (this.explorer.portalData.portalList && this.explorer.portalData.portalList.length){
            this.explorer.portalData.portalList.each(function(portal){
                options.push({
                    "value": portal.id,
                    "text": portal.name
                });
            }.bind(this));
        }

        this.mobileIndexInput = new MWF.xApplication.Setting.Document.Select(this.explorer, this.node, {
            "lp": {"title": this.lp.mobile_index, "infor": this.lp.mobile_index_infor},
            "data": {"key": "nativeData", "valueKey": "indexPortal"},
            "value": this.explorer.nativeData.indexPortal,
            "options": options
        });


        this.explorer.nativeData.nativeAppList.each(function(app, i){
            var title = this.lp.mobile_module.replace("{name}", app.name);
            var infor = this.lp.mobile_module_infor.replace("{name}", app.name);
            new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
                "lp": {"title": title, "infor": infor},
                "data": {"key": "nativeData", "valueKey": "nativeAppList."+i+".enable", "notEmpty": false },
                "value": app.enable
            });
        }.bind(this));
    }
});

MWF.xApplication.Setting.MobileStyleDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.mobile_styleSetting);

        this.explorer.imagesData.images.each(function(img, i){
            var imgName = this.lp.mobile_style_imgs[img.name];
            var title = this.lp.mobile_style.replace("{name}", imgName);
            var infor = this.lp.mobile_style_infor.replace("{name}", imgName);

            new MWF.xApplication.Setting.Document.Image(this.explorer, this.node, {
                "lp": {"title": title, "infor": ""},
                "data": {"key": "imagesData", "valueKey": "applicationList", "notEmpty": false},
                "value": img,
                "itemTitle": infor,
                "iconData": img.value
            });
        }.bind(this));
    }
});