MWF.xDesktop.requireApp("Setting", "Document", null, false);
MWF.xApplication.Setting.BaseNameDocument = new Class({
    Extends: MWF.xApplication.Setting.Document
});
MWF.xApplication.Setting.BasePersonDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.base_personSetting);

        this.baseUserPasswordInput = new MWF.xApplication.Setting.Document.Input(this.explorer, this.node, {
            "lp": {"title": this.lp.base_UserPassword, "infor": this.lp.base_UserPassword_infor, "action": this.lp.base_UserPassword_action},
            "data": {"key": "personData", "valueKey": "password", "notEmpty": true, "infor": this.lp.base_UserPassword_empty },
            "value": this.explorer.personData.password
        });
        this.basePasswordPeriodInput = new MWF.xApplication.Setting.Document.Input(this.explorer, this.node, {
            "lp": {"title": this.lp.base_passwordPeriod, "infor": this.lp.base_passwordPeriod_infor, "action": this.lp.base_passwordPeriod_action},
            "data": {"key": "personData", "valueKey": "passwordPeriod", "notEmpty": false},
            "value": this.explorer.personData.passwordPeriod
        });
        this.baseAdminPasswordInput = new MWF.xApplication.Setting.Document.Input(this.explorer, this.node, {
            "lp": {"title": this.lp.base_adminPassword, "infor": this.lp.base_adminPassword_infor, "action": this.lp.base_adminPassword_action, "confirm": this.lp.base_adminPassword_confirm},
            "data": {"key": "tokenData", "valueKey": "password", "notEmpty": false},
            "value": this.explorer.tokenData.password,
            "show": "********"
        });
    }
});

MWF.xApplication.Setting.BaseLoginDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.base_loginSetting);

        this.baseCaptchaLoginInput = new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
            "lp": {"title": this.lp.base_captchaLogin, "infor": this.lp.base_captchaLogin_infor, "action": this.lp.base_captchaLogin_action},
            "data": {"key": "personData", "valueKey": "captchaLogin", "notEmpty": false },
            "value": this.explorer.personData.captchaLogin
        });

        this.baseCodeLoginInput = new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
            "lp": {"title": this.lp.base_codeLogin, "infor": this.lp.base_codeLogin_infor, "action": this.lp.base_codeLogin_action},
            "data": {"key": "personData", "valueKey": "codeLogin", "notEmpty": false },
            "value": this.explorer.personData.codeLogin
        });
        this.baseBindLoginInput = new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
            "lp": {"title": this.lp.base_bindLogin, "infor": this.lp.base_bindLogin_infor, "action": this.lp.base_bindLogin_action},
            "data": {"key": "personData", "valueKey": "bindLogin", "notEmpty": false },
            "value": this.explorer.personData.bindLogin
        });
        this.baseFaceLoginInput = new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
            "lp": {"title": this.lp.base_faceLogin, "infor": this.lp.base_faceLogin_infor, "action": this.lp.base_faceLogin_action},
            "data": {"key": "personData", "valueKey": "faceLogin", "notEmpty": false },
            "value": this.explorer.personData.faceLogin
        });

        var apiKey = {
            "api_key": "",
            "api_secret": ""
        };
        this.baseFaceLoginApiInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": {"title": this.lp.base_faceApi, "infor": this.lp.base_faceApi_infor, "action": this.lp.base_faceApi_action},
            "data": {"key": "publicData", "valueKey": "api", "notEmpty": false},
            "value": this.explorer.publicData.api,
            "itemTitle": "API Key: {api_key}",
            "addItem": {"api_key": "", "api_secret": ""},
            "icon": "sso.png"
        });

        // this.baseFaceLoginApikeyInput = new MWF.xApplication.Setting.Document.Input(this.explorer, this.node, {
        //     "lp": {"title": this.lp.base_adminPassword, "infor": this.lp.base_adminPassword_infor, "action": this.lp.base_adminPassword_action, "confirm": this.lp.base_adminPassword_confirm},
        //     "data": {"key": "tokenData", "valueKey": "password", "notEmpty": false},
        //     "value": this.explorer.tokenData.password,
        //     "show": "********"
        // });
        // this.baseFaceLogiApiSecretInput = new MWF.xApplication.Setting.Document.Input(this.explorer, this.node, {
        //     "lp": {"title": this.lp.base_adminPassword, "infor": this.lp.base_adminPassword_infor, "action": this.lp.base_adminPassword_action, "confirm": this.lp.base_adminPassword_confirm},
        //     "data": {"key": "tokenData", "valueKey": "password", "notEmpty": false},
        //     "value": this.explorer.tokenData.password,
        //     "show": "********"
        // });

        this.baseRegisterInput = new MWF.xApplication.Setting.Document.Select(this.explorer, this.node, {
            "lp": {"title": this.lp.base_register, "infor": this.lp.base_register_infor, "action": this.lp.base_register_action},
            "data": {"key": "personData", "valueKey": "register", "notEmpty": false },
            "value": this.explorer.personData.register,
            "options": [{"value": "disable", "text": this.lp.register_disable}, {"value": "captcha", "text": this.lp.register_captcha}, {"value": "code", "text": this.lp.register_code}]
        });

        this.baseLoginPortalTypeInput = new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
            "lp": {"title": this.lp.base_portalLogin, "infor": this.lp.base_portalLogin_infor, "action": this.lp.base_portalLogin_action},
            "data": {"key": "personData", "valueKey": "loginPage.enable", "notEmpty": false },
            "value": this.explorer.personData.loginPage.enable
        });

        var getOptions = function(){
            var options = [{"value": "", "text": this.lp.mobile_index_defalue}];
            MWF.Actions.get("x_portal_assemble_surface").listApplication(function(json){
                json.data.each(function(d){
                    var o = {"value": d.id, "text": d.name+((d.alias) ? "("+d.alias+")" : "")};
                    options.push(o);
                });
            }.bind(this), null, false);
            return options;
        }.bind(this);

        this.baseLoginPortalInput = new MWF.xApplication.Setting.Document.Select(this.explorer, this.node, {
            "lp": {"title": this.lp.base_loginPortalId, "infor": this.lp.base_loginPortalId_infor},
            "data": {"key": "personData", "valueKey": "loginPage.portal"},
            "value": this.explorer.personData.loginPage.portal,
            "options": getOptions
        });

        this.baseIndexPortalTypeInput = new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
            "lp": {"title": this.lp.base_portalIndex, "infor": this.lp.base_portalIndex_infor, "action": this.lp.base_portalIndex_action},
            "data": {"key": "portalData", "valueKey": "indexPage.enable", "notEmpty": false },
            "value": this.explorer.portalData.indexPage.enable
        });
        debugger;
        this.baseIndexPortalInput = new MWF.xApplication.Setting.Document.Select(this.explorer, this.node, {
            "lp": {"title": this.lp.base_indexPortalId, "infor": this.lp.base_indexPortalId_infor},
            "data": {"key": "portalData", "valueKey": "indexPage.portal"},
            "value": this.explorer.portalData.indexPage.portal,
            "options": getOptions
        });
    }
});

MWF.xApplication.Setting.BaseSSODocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        this.titleName = new Element("div", {"styles": this.explorer.css.explorerContentTitleNode}).inject(this.node);
        this.titleName.set("text", this.lp.base_ssoSetting);

        this.baseSSOInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": {"title": this.lp.base_ssos, "infor": this.lp.base_sso_infor, "action": this.lp.base_sso_action, "editAction": this.lp.base_sso_editAction},
            "data": {"key": "tokenData", "valueKey": "ssos", "notEmpty": false },
            "value": this.explorer.tokenData.ssos,
            "itemTitle": "{client}",
            "addItem": {"enable": true, "client": "", "key": ""},
            "icon": "sso.png"
        });
        this.baseOauthsInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": {"title": this.lp.base_oauths, "infor": this.lp.base_oauths_infor, "action": this.lp.base_oauths_action, "editAction": this.lp.base_oauths_editAction},
            "data": {"key": "tokenData", "valueKey": "oauths", "notEmpty": false },
            "value": this.explorer.tokenData.oauths,
            "itemTitle": "{clientId}",
            "addItem": {"enable": true, "clientId": "","clientSecret": "", "mapping": {}},
            "icon": "sso.png"
        });

        this.baseOauthsServerInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": {"title": this.lp.base_oauths_server, "infor": this.lp.base_oauths_infor_server, "action": this.lp.base_oauths_action, "editAction": this.lp.base_oauths_editAction},
            "data": {"key": "tokenData", "valueKey": "oauthClients", "notEmpty": false },
            "value": this.explorer.tokenData.oauthClients,
            "itemTitle": "{name}",
            "addItem": {
                "enable": false,
                "name": "",
                "icon": "",
                "clientId": "",
                "clientSecret": "",
                "authAddress": "",
                "authParameter": "client_id={$client_id}&client_secret={$client_secret}",
                "authMethod": "GET",
                "tokenAddress": "",
                "tokenParameter": "code={$code}&grant_type=authorization_code&client_id={$client_id}",
                "tokenMethod": "POST",
                "tokenType": "json",
                "infoAddress": "",
                "infoParameter": "access_token={$access_token}&client_id={$client_id}",
                "infoMethod": "GET",
                "infoType": "json",
                "infoCredentialField": "username"
            },
            "icon": "sso.png"
        });


        if (!this.explorer.tokenData.qyweixin) this.explorer.tokenData.qyweixin = {
            "enable": false,
            "syncCron": "",
            "forceSyncCron": "",
            "apiAddress": "",
            "corpId": "",
            "corpSecret": "",
            "agentId": "",
            "token": "",
            "encodingAesKey": "",
            "messageEnable": false
        };
        this.baseQyweixinInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": {"title": this.lp.base_qyweixin, "infor": this.lp.base_qyweixin_infor, "action": this.lp.base_qyweixin_action},
            "data": {"key": "tokenData", "valueKey": "qiyeweixin", "notEmpty": false},
            "value": this.explorer.tokenData.qiyeweixin,
            "itemTitle": "corpId: {corpId}",
            "addItem": {"corpId": "", "corpSecret": ""},
            "icon": "weixin.png"
        });

        if (!this.explorer.tokenData.dingding) this.explorer.tokenData.dingding = {
            "enable": false,
            "corpId": "",
            "agentId": "",
            "appKey": "",
            "appSecret": "",
            "syncCron": "",
            "forceSyncCron": "",
            "oapiAddress": "",
            "messageEnable": true
        };
        this.baseDingdingInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": {"title": this.lp.base_dingding, "infor": this.lp.base_dingding_infor, "action": this.lp.base_dingding_action},
            "data": {"key": "tokenData", "valueKey": "dingding", "notEmpty": false},
            "value": this.explorer.tokenData.dingding,
            "itemTitle": "agentId: {agentId}",
            "addItem": {"corpId": "", "corpSecret": "", "agentId": ""},
            "icon": "dingding.png"
        });
    }
});
