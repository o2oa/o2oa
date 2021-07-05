MWF.xDesktop.requireApp("Setting", "Document", null, false);
MWF.xApplication.Setting.MobileConnectDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function () {
        this.node = new Element("div", { "styles": { "overflow": "hidden", "padding-bottom": "80px" } }).inject(this.contentAreaNode);
        this.titleName = new Element("div", { "styles": this.explorer.css.explorerContentTitleNode }).inject(this.node);
        this.titleName.set("text", this.lp.mobile_connectSetting);

        var o2CloudConnected = false;
        this.actions.collectConnected(function (json) {
            o2CloudConnected = json.data.value;
        }.bind(this), function (json) {
            o2CloudConnected = false;
        }.bind(this), false);

        this.mobileO2CloudConnectInput = new MWF.xApplication.Setting.Document.Button(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_connectO2Cloud, "infor": this.mobile_connectO2Cloud_infor, "action": this.lp.mobile_connectO2Cloud_action },
            //"data": {"key": "proxyData", "valueKey": "ssos", "notEmpty": false },
            "value": o2CloudConnected ? this.lp.mobile_connectO2Cloud_success : this.lp.mobile_connectO2Cloud_error,
            "itemTitle": o2CloudConnected ? this.lp.mobile_connectO2Cloud_success : this.lp.mobile_connectO2Cloud_error,
            "icon": o2CloudConnected ? "cloud.png" : "cloud_error.png",
            "action": function (e) {
                layout.desktop.openApplication(e, "Collect");
            }.bind(this)
        });

        this.mobileHttpProtocolInput = new MWF.xApplication.Setting.Document.Select(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_httpProtocol, "infor": this.lp.mobile_httpProtocol_infor },
            "data": { "key": "proxyData", "valueKey": "httpProtocol" },
            "value": this.explorer.proxyData.httpProtocol,
            "options": [{ "value": "http", "text": "http" }, { "value": "https", "text": "https" }]
        });

        this.mobileCenterInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_center, "infor": this.lp.mobile_center_infor, "editAction": this.lp.mobile_center_action },
            "data": { "key": "proxyData", "valueKey": "center", "notEmpty": false },
            "value": this.explorer.proxyData.center,
            "itemTitle": "{proxyHost}:{proxyPort}",
            "icon": "center.png"
        });

        this.mobileWebInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_web, "infor": this.lp.mobile_web_infor, "editAction": this.lp.mobile_web_action },
            "data": { "key": "proxyData", "valueKey": "web", "notEmpty": false },
            "value": this.explorer.proxyData.web,
            "itemTitle": "{proxyHost}:{proxyPort}",
            "icon": "webserver.png"
        });

        this.mobileApplicationInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_application, "infor": this.lp.mobile_application_infor, "editAction": this.lp.mobile_application_action },
            "data": { "key": "proxyData", "valueKey": "applicationList", "notEmpty": false },
            "value": this.explorer.proxyData.applicationList,
            "itemTitle": "{proxyHost}:{proxyPort}",
            "readonly": ["node"],
            "icon": "server.png"
        });
    }
});

MWF.xApplication.Setting.MobileModuleDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function () {
        this.node = new Element("div", { "styles": { "overflow": "hidden", "padding-bottom": "80px" } }).inject(this.contentAreaNode);
        this.titleName = new Element("div", { "styles": this.explorer.css.explorerContentTitleNode }).inject(this.node);
        this.titleName.set("text", this.lp.mobile_moduleSetting);

        var options = [{ "value": "", "text": this.lp.mobile_index_defalue }];
        if (this.explorer.portalData.portalList && this.explorer.portalData.portalList.length) {
            this.explorer.portalData.portalList.each(function (portal) {
                options.push({
                    "value": portal.id,
                    "text": portal.name
                });
            }.bind(this));
        }

        this.mobileIndexInput = new MWF.xApplication.Setting.Document.Select(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_index, "infor": this.lp.mobile_index_infor },
            "data": { "key": "nativeData", "valueKey": "indexPortal" },
            "value": this.explorer.nativeData.indexPortal,
            "options": options
        });

        //移动端简易模式
        var simpleModeTitle = this.lp.mobile_module_simple_mode;
        var simpleModeInfor = this.lp.mobile_module_simple_mode_infor;
        new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
            "lp": { "title": simpleModeTitle, "infor": simpleModeInfor },
            "data": { "key": "nativeData", "valueKey": "simpleMode", "notEmpty": false },
            "value": this.explorer.nativeData.simpleMode
        });

        this.explorer.nativeData.nativeAppList.each(function (app, i) {
            var title = this.lp.mobile_module.replace("{name}", app.name);
            var infor = this.lp.mobile_module_infor.replace("{name}", app.name);
            new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
                "lp": { "title": title, "infor": infor },
                "data": { "key": "nativeData", "valueKey": "nativeAppList." + i + ".enable", "notEmpty": false },
                "value": app.enable
            });
        }.bind(this));
    }
});

// app在线打包功能
MWF.xApplication.Setting.AppPackOnlineDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function () {
        var cssurl = "../x_component_Setting/$Main/default/apppack/index.css";
        var htmlurl = "../x_component_Setting/$Main/default/apppack/index.html";
        this.contentAreaNode.loadAll({ "css": [cssurl], "html": [htmlurl] }, { "bind": { "lp": this.lp, "data": {} }, "module": this }, function () {
            this.node = this.apppackBoxNode;
            this.checkConnectAppPackServer();
        }.bind(this));
    },
    // 检查app打包服务器连接情况，包括是否已经登录O2云
    checkConnectAppPackServer: function () {
        o2.Actions.load("x_program_center").AppPackAction.connect(function(json){
            if (json && json.data) {
                var data = json.data
                if (data.status === 1001) { // 成功 获取token
                    this.token = data.token;
                    this.packServerUrl = data.packServerUrl;
                    this.apppackErrorMsgNode.setStyles({
                        "display": "none"
                    });
                    this.loadAppPackInfo();
                } else if (data.status === 1) { // o2云未连接 o2云未启用
                    this.showErrorMsg(this.lp.mobile_apppack_message_o2cloud_not_enable);
                    // this.app.notice(this.lp.mobile_apppack_message_o2cloud_not_enable, "error", this.contentAreaNode);
                } else if (data.status === 2) { // o2云未登录
                    // this.app.notice(this.lp.mobile_apppack_message_o2cloud_not_login, "error", this.contentAreaNode);
                    this.showErrorMsg(this.lp.mobile_apppack_message_o2cloud_not_login);
                } else if (data.status === 3) { // 打包服务器未认证通过
                    // this.app.notice(this.lp.mobile_apppack_message_apppack_server_login_fail, "error", this.contentAreaNode);
                    this.showErrorMsg(this.lp.mobile_apppack_message_apppack_server_login_fail);
                }
            } else {
                // this.app.notice(this.lp.mobile_apppack_message_check_connect_fail, "error", this.contentAreaNode);
                this.showErrorMsg(this.lp.mobile_apppack_message_check_connect_fail);
            }
        }.bind(this));
    },
    // 加载最近一次打包信息 
    loadAppPackInfo: function () {
        this.showLoading();
        o2.Actions.load("x_program_center").AppPackAction.packInfo(this.token, function(json){
            this.hiddenLoading();
            if (json && json.type === "success") {
                this.packInfo = json.data;
                this.showPackInfoDetail();
            } else {
                console.log("查询打包信息失败。。。")
                this.loadConfigProxy();
            }
        }.bind(this), function(err){
            console.log("错误拉，没有找到打包信息！");
            console.log(err);
            this.hiddenLoading();
            this.loadConfigProxy();
        }.bind(this));
    },
    showLoading: function() {
        if (this.packLoadingNode) {
            this.hiddenLoading();
        }
        this.packLoadingNode = new Element("div", { "class": "pack-loading" }).inject(this.apppackBoxNode);
        new Element("div", {"text": "Loading......"}).inject(this.packLoadingNode);
    },
    hiddenLoading: function() {
        if (this.packLoadingNode) {
            this.packLoadingNode.destroy();
            this.packLoadingNode = null;
        }
    },
    // 显示错误信息
    showErrorMsg: function(msg) {
        this.apppackErrorMsgNode.set("text", msg);
        this.apppackErrorMsgNode.setStyles({
            "display": ""
        });
    },
    // 显示提交表单
    showForm: function() {
        this.apppackShowBodyNode.setStyles({
            "display": "none"
        });
        this.apppackFormBodyNode.setStyles({
            "display": ""
        });
        this.apppackBtnNode.addEvents({
            "click": function(e) {
                this.submitPack();
            }.bind(this)
        })
    },
    // 显示打包详情
    showPackInfoDetail: function () {
        if (this.packInfo) {
            this.apppackAppNameShowNode.set("text", this.packInfo.appName);
            this.apppackProtocolShowNode.set("text", this.packInfo.o2ServerProtocol);
            this.apppackHostShowNode.set("text", this.packInfo.o2ServerHost);
            this.apppackPortShowNode.set("text", this.packInfo.o2ServerPort);
            this.apppackContextShowNode.set("text", this.packInfo.o2ServerContext);
            this.apppackLogoShowImgNode.set("src", this.packServerUrl + this.packInfo.appLogoPath + "?token=" + this.token);
            var status = ""
            if (this.packInfo.packStatus === "0") {
                status = this.lp.mobile_apppack_status_order_inline
                this.apppackStatusRefreshNode.setStyles({
                    "display": ""
                });
                this.apppackStatusRefreshNode.addEvents({
                    "click": function(e) {
                        this.loadAppPackInfo();
                    }.bind(this)
                })
                this.apppackReInputBtnNode.setStyles({
                    "display": "none"
                });
                this.apppackRePackBtnNode.setStyles({"display": "none"})
            } else if (this.packInfo.packStatus === "1") {
                status =  this.lp.mobile_apppack_status_packing
                this.apppackStatusRefreshNode.setStyles({
                    "display": ""
                });
                this.apppackStatusRefreshNode.addEvents({
                    "click": function(e) {
                        this.loadAppPackInfo();
                    }.bind(this)
                })
                this.apppackReInputBtnNode.setStyles({
                    "display": "none"
                });
                this.apppackRePackBtnNode.setStyles({"display": "none"})
            } else if (this.packInfo.packStatus === "2") {
                status = this.lp.mobile_apppack_status_pack_end
                this.apppackStatusRefreshNode.setStyles({
                    "display": "none"
                });
                this.apppackReInputBtnNode.setStyles({
                    "display": ""
                });
                this.apppackReInputBtnNode.addEvents({
                    "click": function(e) {
                        this.reInput();
                    }.bind(this)
                })
                this.apppackRePackBtnNode.setStyles({"display": ""})
                this.apppackRePackBtnNode.addEvents({
                    "click": function(e) {
                        this.reSubmitPack();
                    }.bind(this)
                })
            }
            this.apppackStatusShowNode.set("text", status);
            
            if (this.packInfo.apkPath) {
                this.apppackDownloadLinkNode.set("href", this.packServerUrl + this.packInfo.apkPath + "?token=" + this.token);
                this.apppackDownloadShowNode.setStyles({
                    "display": ""
                });
            } else {
                this.apppackDownloadShowNode.setStyles({
                    "display": "none"
                });
            }
            this.apppackShowBodyNode.setStyles({
                "display": ""
            });
            this.apppackFormBodyNode.setStyles({
                "display": "none"
            });
            
        }
    },
    // 重新输入 提交表单
    reInput: function () {
        // 填充内容
        if (this.packInfo) {
            this.apppackProtocolInputNode.set("value", this.packInfo.o2ServerProtocol);
            this.apppackHostInputNode.set("value", this.packInfo.o2ServerHost);
            this.apppackPortInputNode.set("value", this.packInfo.o2ServerPort);
            this.apppackContextInputNode.set("value", this.packInfo.o2ServerContext);
            this.apppackAppNameInputNode.set("value", this.packInfo.appName);
        }
        this.showForm();
    },
    // 获取本地配置的proxy地址
    loadConfigProxy: function () {
        o2.Actions.load("x_program_center").ConfigAction.getProxy(function(json){
            this.hiddenLoading();
            // 填充
            if (json && json.data) {
                var data = json.data
                if (data.httpProtocol) {
                    this.apppackProtocolInputNode.set("value", data.httpProtocol);
                } else {
                    this.apppackProtocolInputNode.set("value", "http");
                }
                if (data.center) {
                    if (data.center.proxyHost) {
                        this.apppackHostInputNode.set("value", data.center.proxyHost);
                    }
                    if (data.center.proxyPort) {
                        this.apppackPortInputNode.set("value", data.center.proxyPort);
                    } else {
                        this.apppackPortInputNode.set("value", "20030");
                    }
                } else {
                    this.apppackPortInputNode.set("value", "20030");
                }
            }
            this.apppackContextInputNode.set("value", "/x_program_center");
            this.showForm();
        }.bind(this))
    },
    // 直接用原有资料重新打包
    reSubmitPack: function() {
        this.confirm(this.lp.alert, this.lp.mobile_apppack_message_alert_submit, function() {
            this.showLoading();
            o2.Actions.load("x_program_center").AppPackAction.androidPackReStart(this.token, function (json) {
                console.log(json)
                this.hiddenLoading();
                if (json.data && json.data.value) {
                    // 提交成功 获取最新打包对象信息
                    this.loadAppPackInfo();
                } else {
                    this.app.notice(json.message, "error", this.contentAreaNode);
                }
            }.bind(this), function (error) {
                this.hiddenLoading();
                console.log(error);
                this.app.notice(error, "error", this.contentAreaNode);
            }.bind(this));
        }.bind(this));
    },
    // 提交打包
    submitPack: function () {
       
        var appName = this.apppackAppNameInputNode.get("value");
        if (!appName || appName === "") {
            this.app.notice(this.lp.mobile_apppack_message_appname_not_empty, "error", this.contentAreaNode);
            return;
        }
        if (appName.length > 6) {
            this.app.notice(this.lp.mobile_apppack_message_appname_len_max_6, "error", this.contentAreaNode);
            return;
        }
        var files = this.apppackLogoInputNode.files;
        if (files.length) {
            var file = files.item(0);
            var fileExt = file.name.substring(file.name.lastIndexOf("."));
            if (fileExt.toLowerCase() !== ".png") {
                this.app.notice(this.lp.mobile_apppack_message_app_logo_need_png, "error", this.contentAreaNode);
                return;
            }
        } else {
            this.app.notice(this.lp.mobile_apppack_message_app_logo_not_empty, "error", this.contentAreaNode);
            return;
        }
        var protocol = this.apppackProtocolInputNode.get("value");
        if (!protocol || protocol === "") {
            this.app.notice(this.lp.mobile_apppack_message_portocol_not_empty, "error", this.contentAreaNode);
            return;
        }
        if (protocol !== "http" && protocol !== "https") {
            this.app.notice(this.lp.mobile_apppack_message_portocol_http_https, "error", this.contentAreaNode);
            return;
        }
        var host = this.apppackHostInputNode.get("value");
        if (!host || host === "") {
            this.app.notice(this.lp.mobile_apppack_message_host_not_empty, "error", this.contentAreaNode);
            return;
        }
        var port = this.apppackPortInputNode.get("value");
        if (!port || port === "") {
            this.app.notice(this.lp.mobile_apppack_message_port_not_empty, "error", this.contentAreaNode);
            return;
        }
        var context = this.apppackContextInputNode.get("value");
        if (!context || context === "") {
            this.app.notice(this.lp.mobile_apppack_message_context_not_empty, "error", this.contentAreaNode);
            return;
        }
        this.confirm(this.lp.alert, this.lp.mobile_apppack_message_alert_submit, function() {
            this.showLoading();
            var formData = new FormData();
            formData.append('file', file);
            formData.append('fileName', file.name);
            formData.append('appName', appName);
            formData.append('o2ServerProtocol', protocol);
            formData.append('o2ServerHost', host);
            formData.append('o2ServerPort', port);
            formData.append('o2ServerContext', context);
            formData.append('token', this.token);
            
            o2.Actions.load("x_program_center").AppPackAction.androidPackStart(formData, "{}",function (json) {
                console.log(json)
                this.hiddenLoading();
                if (json.data && json.data.value) {
                    // 提交成功 获取最新打包对象信息
                    this.loadAppPackInfo();
                } else {
                    this.app.notice(json.message, "error", this.contentAreaNode);
                }
            }.bind(this), function (error) {
                this.hiddenLoading();
                console.log(error);
                this.app.notice(error, "error", this.contentAreaNode);
            }.bind(this));
        }.bind(this));
        
    },
    //确认
    confirm: function (title, text, okCallback) {
        var width = 600;
        var height = 110;
        var size = this.app.content.getSize();
        var x = (size.x - width) / 2;
        var y = (size.y - height) / 2;
        MWF.require("MWF.xDesktop.Dialog", function () {
            var dlg = new MWF.xDesktop.Dialog({
                "title": title,
                "style": "settingStyle",
                "top": y,
                "left": x,
                "width": width,
                "height": height,
                "text": text,
                "maskNode": this.app.content,
                "container": this.app.content,
                "buttonList": [
                    {
                        "text": this.lp.ok,
                        "action": function () {
                            if (okCallback) { okCallback(); }
                            this.close();
                        }
                    },
                    {
                        "text": this.lp.cancel,
                        "action": function () { this.close(); }
                    }
                ]
            });
            dlg.show();
        }.bind(this));
    }
});


// 微信菜单
MWF.xApplication.Setting.MPWeixinMenuSettingDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function () {
        var cssurl = "../x_component_Setting/$Main/default/mpweixin/mpweixin.css";
        var htmlurl = "../x_component_Setting/$Main/default/mpweixin/menu.html";
        this.contentAreaNode.loadAll({ "css": [cssurl], "html": [htmlurl] }, { "bind": { "lp": this.lp, "data": {} }, "module": this }, function () {
            this.node = this.mpwxMenuNode;
            this.initMenuData();
            this.loadMenu();
            this.loadPublishBtn();
        }.bind(this));
    },
    initMenuData: function() {
         //处理数据
        // var list = this.explorer.mpweixinListData;
        if (!this.menuList) {
            this.menuList = this.explorer.mpweixinListData || [];
        }
        //去除 subscribe
        var subscribe = null;
        for (let index = 0; index < this.menuList.length; index++) {
            const element = this.menuList[index];
            if (element.type && element.type === "subscribe") {
                subscribe = element;
            }
        }
        if (subscribe) {
            this.menuList.erase(subscribe);
        }
    },
    //发布菜单 点击事件
    loadPublishBtn: function() {
        this.mpwxPublishBtnNode.addEvents({
            "click": function(){
                if (this.menuList && this.menuList.length > 0) {
                    this.confirm(this.lp.alert, this.lp.mobile_mpweixin_menu_msg_publish_2_wxmp, function () {
                        o2.Actions.load("x_program_center").MPWeixinAction.menuCreate2Weixin(function (json) {
                            this.app.notice(this.lp.mobile_mpweixin_menu_msg_publish_success, "success", this.contentAreaNode);
                        }.bind(this));
                    }.bind(this));
                }
            }.bind(this)
        });
    },
    loadMenu: function () {
        this.mpwxMenuListNode.empty();
        // 生成菜单
        if (this.menuList && this.menuList.length > 0) {
            if (!this.showSubId) {
                this.showSubId = this.menuList[0].id;//初始化 showSubId 表示哪个子菜单是显示的
            }
            if (!this.currentMenuId) {
                this.currentMenuId = this.menuList[0].id;//初始化 currentMenuId 表示当前选中的菜单ID
            }
            for (let index = 0; index < this.menuList.length; index++) {
                const element = this.menuList[index];
                this.firstLevelMenu(element);
            }
        }
        this.firstLevelAddButton();
    },
    //第一层级的新建按钮
    firstLevelAddButton: function () {
        var addLi = new Element("li", { "class": "js_addMenuBox pre_menu_item grid_item no_extra" }).inject(this.mpwxMenuListNode);
        new Element("a", { "class": "pre_menu_link js_addL1Btn", "href": "javascript:void(0);" }).inject(addLi);
        new Element("i", { "class": "icon14_menu_add" }).inject(addLi);
        //点击事件
        addLi.addEvents({
            "click": function () {
                this.createNewFirstMenuMock();
            }.bind(this)
        });
    },
    // 第一层级 菜单 UI
    // <li class="jsMenu pre_menu_item grid_item jslevel1 ui-sortable ui-sortable-disabled size1of3  current selected" id="menu_0">
    //     <a href="javascript:void(0);" class="pre_menu_link" draggable="false">
    //     <i class="icon_menu_dot js_icon_menu_dot dn" style="display: none;"></i>
    //     <i class="icon20_common sort_gray"></i>
    //     <span class="js_l1Title">菜单名称</span>
    // </a>
    // <div class="sub_pre_menu_box js_l2TitleBox" >
    //     <ul class="sub_pre_menu_list">
    //     <li id="1618290787255_subMenu_menu_0_0" class="jslevel2">
    //     <a href="javascript:void(0);" class="jsSubView" draggable="false">
    //         <span class="sub_pre_menu_inner js_sub_pre_menu_inner">
    //             <i class="icon20_common sort_gray"></i>
    //             <span class="js_l2Title">子菜单名称单名称</span>
    //         </span>
    //     </a>
    // </li>
    //     <li id="1618290787255_subMenu_menu_0_0" class="jslevel2 current"><a href="javascript:void(0);" class="jsSubView" draggable="false"><span class="sub_pre_menu_inner js_sub_pre_menu_inner"><i class="icon20_common sort_gray"></i><span class="js_l2Title">子菜单名称</span></span></a></li>
    //     <li class="js_addMenuBox">
    //     <a href="javascript:void(0);" class="jsSubView js_addL2Btn" title="最多添加5个子菜单" draggable="false">
    //     <span class="sub_pre_menu_inner js_sub_pre_menu_inner">
    //     <i class="icon14_menu_add"></i></span></a>
    // </li>
    //     </ul>
    //     <i class="arrow arrow_out"></i>
    //     <i class="arrow arrow_in"></i>
    // </div>
    // </li>
    firstLevelMenu: function (menu) {
        var sub_button = menu.sub_button;
        var menuClass = "jsMenu pre_menu_item grid_item jslevel1 ui-sortable ui-sortable-disabled size1of3";
        if (this.currentMenuId && this.currentMenuId === menu.id) {
            menuClass = "jsMenu pre_menu_item grid_item jslevel1 ui-sortable ui-sortable-disabled size1of3 current";
        }
        var menuLi = new Element("li", { "class": menuClass }).inject(this.mpwxMenuListNode);
        var menuA = new Element("a", { "class": "pre_menu_link", "href": "javascript:void(0);" }).inject(menuLi);
        new Element("span", { "class": "js_l1Title", "text": menu.name }).inject(menuA);
        menuLi.addEvents({
            "click": function () {
                this.clickMenu(menu.id, menu);
            }.bind(this)
        });
        //子菜单
        var subDiv = new Element("div", { "class": "sub_pre_menu_box js_l2TitleBox" }).inject(menuLi);
        var subUl = new Element("ul", { "class": "sub_pre_menu_list" }).inject(subDiv);
        if (!sub_button || sub_button.length < 5) {
            this.secondLevelAddButton(menu, subUl); //新增按钮放上面
        }
        if (sub_button && sub_button.length > 0) {
            // 子菜单是倒叙的
            sub_button.sort(function (a, b) {
                var x = a.order.toLowerCase();
                var y = b.order.toLowerCase();
                if (x < y) { return 1; }
                if (x > y) { return -1; }
                return 0;
            })
            for (let index = 0; index < sub_button.length; index++) {
                const element = sub_button[index];
                this.secondLevelMenu(element, subUl, menu);
            }
        }
        new Element("i", { "class": "arrow arrow_out" }).inject(subDiv);
        new Element("i", { "class": "arrow arrow_in" }).inject(subDiv);
        if (this.showSubId && this.showSubId === menu.id) {
            subDiv.setStyles({
                "display": ""
            });
        } else {
            subDiv.setStyles({
                "display": "none"
            });
        }

    },
    // 子菜单 新建按钮
    secondLevelAddButton: function (parentMenu, parentNode) {
        var addLi = new Element("li", { "class": "js_addMenuBox" }).inject(parentNode);
        var addA = new Element("a", { "class": "jsSubView js_addL2Btn", "href": "javascript:void(0);" }).inject(addLi);
        var span = new Element("span", { "class": "sub_pre_menu_inner js_sub_pre_menu_inner" }).inject(addA);
        new Element("i", { "class": "icon14_menu_add" }).inject(span);
        addLi.addEvents({
            "click": function (ev) {
                console.log("点击了新增，属于 " + parentMenu.name);
                this.createNewSubMenuMock(parentMenu);
                ev.stopPropagation()
            }.bind(this)
        });
    },
    // 子菜单 UI
    secondLevelMenu: function (menu, parentNode, parentMenu) {
        var menuClass = "jslevel2";
        if (this.currentMenuId && this.currentMenuId === menu.id) {
            menuClass = "jslevel2 current";
        }
        var menuLi = new Element("li", { "class": menuClass }).inject(parentNode);
        var menuA = new Element("a", { "class": "jsSubView", "href": "javascript:void(0);" }).inject(menuLi);
        var firstSapn = new Element("span", { "class": "sub_pre_menu_inner js_sub_pre_menu_inner" }).inject(menuA);
        new Element("i", { "class": "icon20_common sort_gray" }).inject(firstSapn);
        new Element("span", { "class": "js_l2Title", "text": menu.name }).inject(firstSapn);
        menuLi.addEvents({
            "click": function (ev) {
                this.clickMenu(parentMenu.id, menu);
                ev.stopPropagation();
            }.bind(this)
        });
    },
    //右边区域 装载
    menuRightBoxCreate: function (menu) {
        this.mpwxMenuFormHeaderNode.empty();
        // 头部
        new Element("h4", { "class": "global_info", "text": menu.name }).inject(this.mpwxMenuFormHeaderNode);
        var deleteNode = new Element("div", { "class": "global_extra" }).inject(this.mpwxMenuFormHeaderNode);
        var deleteA = new Element("a", { "href": "javascript:void(0);", "text": this.lp.mobile_mpweixin_menu_deleteBtnName_label }).inject(deleteNode);
        deleteA.addEvents({
            "click": function (ev) {
                this.deleteAction(menu);
                
                ev.stopPropagation();
            }.bind(this)
        })

        // 表单页面
        new MWF.xApplication.Setting.MPWeixinMenuSettingDocument.MenuForm(this, this.mpwxMenuFormNode, menu);
    },
    //点击菜单执行
    clickMenu: function (showSubId, menu) {
        this.showSubId = showSubId; //showSubId 表示哪个子菜单是显示的
        this.currentMenuId = menu.id;//currentMenuId 表示当前选中的菜单ID
        this.menuRightBoxCreate(menu);
        this.loadMenu();
    },
    //创建子菜单
    createNewSubMenuMock: function (parent) {
        let sub = parent.sub_button;
        //最多可创建5个
        if (sub && sub.length >= 5 ) {
            this.app.notice(this.lp.mobile_mpweixin_menu_msg_sub_max_len, "error", this.contentAreaNode);
            return;
        }
        if (parent.id.startsWith('mock_')) {
            this.app.notice(this.lp.mobile_mpweixin_menu_msg_parent_no_save, "error", this.contentAreaNode);
            return;
        }
        // 排序号生成 
        let order = "000001";
        if (sub && sub.length > 0) {
            let bigestOrder = sub[0].order;//第一条 因为上面已经排序过了 所以第一条是最大的
            const intOrder = parseInt(bigestOrder);
            if (!isNaN(intOrder)) {
                let o = intOrder + 1;
                let os = '' + o;
                for (let len = os.length; len < 6; len = os.length) {
                    os = "0" + os;
                }
                order = os;
            }
        }
        // id mock_开头 正式保存的时候知道是新增的 可以清除 后台生成id
        let body = {
            "id": "mock_" + o2.uuid(),
            "name": this.lp.mobile_mpweixin_menu_default_new_name,
            "parentId": parent.id,
            "type": "click", //默认文字消息
            "order": order
        };
        if (sub) {
            parent.sub_button.push(body);
        } else {
            parent.sub_button = [body];
        }
        for (let index = 0; index < this.menuList.length; index++) {
            const element = this.menuList[index];
            if (element.id === parent.id) {
                this.menuList[index] = parent;
            }
        }
        this.clickMenu(parent.id, body);
    },
    // 创建第一层菜单
    createNewFirstMenuMock: function() {
        //最多可创建3个
        if (this.menuList && this.menuList.length >=3 ) {
            this.app.notice(this.lp.mobile_mpweixin_menu_msg_first_max_len, "error", this.contentAreaNode);
            return;
        }
        // 排序号生成 
        let order = "000001";
        if (this.menuList && this.menuList.length > 0) {
            let bigestOrder = this.menuList[this.menuList.length-1].order;//最后一条是最大的号码
            console.log("最大的order " + bigestOrder);
            const intOrder = parseInt(bigestOrder);
            if (!isNaN(intOrder)) {
                let o = intOrder + 1;
                let os = '' + o;
                for (let len = os.length; len < 6; len = os.length) {
                    os = "0" + os;
                }
                order = os;
            }
            console.log("最大的order " + intOrder);
        }
        // id mock_开头 正式保存的时候知道是新增的 可以清除 后台生成id
        let body = {
            "id": "mock_" + o2.uuid(),
            "name": this.lp.mobile_mpweixin_menu_default_new_name,
            "type": "click", //默认文字消息
            "order": order
        };
        if (this.menuList) {
            this.menuList.push(body);
        } else {
            this.menuList = [body];
        }
        this.clickMenu(body.id, body);
    },
    // 更新list中的数据对象
    setMenuData(menu) {
        if (this.menuList && this.menuList.length > 0) {
            for (let index = 0; index < this.menuList.length; index++) {
                const element = this.menuList[index];
                if (element.id === menu.id) {
                    this.menuList[index] = menu;
                    break;
                }
                var flag = false;
                if (element.sub_button && element.sub_button.length > 0) {
                    for (let i = 0; i < element.sub_button.length; i++) {
                        const child = element.sub_button[i];
                        if (child.id === menu.id) {
                            flag = true;
                            element.sub_button[i] = menu;
                        }
                    }
                }
                if (flag) {
                    this.menuList[index] = element;
                    break;
                }
            }
            this.loadMenu();
        }
    },
    // 删除数据
    deleteAction: function(menu) {
        if (menu.id) {
            if (menu.id.startsWith("mock_")) {
                console.log("没有保存到数据库的数据 直接删除就行。");
                this.removeDataUI(menu);
            }else {
                this.confirm(this.lp.alert, this.lp.mobile_mpweixin_menu_delete_alert_msg, function () {
                    o2.Actions.load("x_program_center").MPWeixinAction.menuDelete(menu.id,function (json) {
                        //刷新数据
                        this.removeDataUI(menu);
                        this.app.notice(this.lp.mobile_mpweixin_menu_delete_success, "success", this.contentAreaNode);
                    }.bind(this));
                }.bind(this));
            }
        }
    },
    // 删除数据后更新ui
    removeDataUI: function(menu) {
        this.mpwxMenuFormHeaderNode.empty();
        this.mpwxMenuFormNode.empty();
        this.showSubId = null;
        this.currentMenuId = null;
        if (this.menuList && this.menuList.length > 0) {
            for (let index = 0; index < this.menuList.length; index++) {
                const element = this.menuList[index];
                if (element.id === menu.id) {
                    this.menuList.erase(menu);
                    console.log("删除成功。。。。。上级")
                    break;
                }
                var flag = false;
                if (element.sub_button && element.sub_button.length > 0) {
                    for (let i = 0; i < element.sub_button.length; i++) {
                        const child = element.sub_button[i];
                        if (child.id === menu.id) {
                            flag = true;
                            element.sub_button.erase(menu);
                            console.log("删除成功。。。。子级。")
                        }
                    }
                }
                if (flag) {
                    this.menuList[index] = element;
                    break;
                }
            }
            this.loadMenu();
        }         
    },
    //确认
    confirm: function (title, text, okCallback) {
        var width = 600;
        var height = 110;
        var size = this.app.content.getSize();
        var x = (size.x - width) / 2;
        var y = (size.y - height) / 2;
        MWF.require("MWF.xDesktop.Dialog", function () {
            var dlg = new MWF.xDesktop.Dialog({
                "title": title,
                "style": "settingStyle",
                "top": y,
                "left": x,
                "width": width,
                "height": height,
                "text": text,
                "maskNode": this.app.content,
                "container": this.app.content,
                "buttonList": [
                    {
                        "text": this.lp.ok,
                        "action": function () {
                            if (okCallback) { okCallback(); }
                            this.close();
                        }
                    },
                    {
                        "text": this.lp.cancel,
                        "action": function () { this.close(); }
                    }
                ]
            });
            dlg.show();
        }.bind(this));
    }
});

//菜单表单功能
MWF.xApplication.Setting.MPWeixinMenuSettingDocument.MenuForm = new Class({
    Implements: [Options, Events],
    initialize: function (menuApp, contentAreaNode, menu, options) {
        this.setOptions(options);
        this.menuApp = menuApp;
        this.explorer = menuApp.explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.contentAreaNode = contentAreaNode;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.menu = menu;
        this.load();
    },
    load: function () {
        this.contentAreaNode.empty();
        this.nameInputCreate();
        this.orderInputCreate();
        if (this.menu.sub_button && this.menu.sub_button.length > 0) { //父菜单 如果存在子菜单就不需要编辑类型
            this.haSubMenus = true;
        } else {
            this.haSubMenus = false;
            this.typeRadioCreate();
        }
        this.saveButtonCreate();
    },
    //菜单名称
    nameInputCreate: function () {
        this.nameInputBoxNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.contentAreaNode);
        var labelNode = new Element("label", { "class": "frm_label" }).inject(this.nameInputBoxNode);
        new Element("strong", { "class": "title js_menuTitle", "text": this.lp.mobile_mpweixin_menu_form_name_label }).inject(labelNode);
        var inputNode = new Element("div", { "class": "frm_controls" }).inject(this.nameInputBoxNode);
        var spanNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputNode);
        this.nameInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.name }).inject(spanNode);
        //事件？
        this.nameInputTipsNode = new Element("p", { "class": "frm_tips js_titleNolTips", "text": this.lp.mobile_mpweixin_menu_form_name_tips }).inject(inputNode);
        this.nameInputErrorTipsNode = new Element("p", { "class": "frm_msg fail js_titleEorTips dn", "text": this.lp.mobile_mpweixin_menu_form_name_error }).inject(inputNode);
    },
    //菜单排序号
    orderInputCreate: function () {
        this.orderInputBoxNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.contentAreaNode);
        var labelNode = new Element("label", { "class": "frm_label" }).inject(this.orderInputBoxNode);
        new Element("strong", { "class": "title js_menuTitle", "text": this.lp.mobile_mpweixin_menu_form_order_label }).inject(labelNode);
        var inputNode = new Element("div", { "class": "frm_controls" }).inject(this.orderInputBoxNode);
        var spanNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputNode);
        this.orderInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.order }).inject(spanNode);
        //事件？
        this.orderInputTipsNode = new Element("p", { "class": "frm_tips js_titleNolTips", "text": this.lp.mobile_mpweixin_menu_form_order_tips }).inject(inputNode);
        this.orderInputErrorTipsNode = new Element("p", { "class": "frm_msg fail js_titleEorTips dn", "text": this.lp.mobile_mpweixin_menu_form_order_error }).inject(inputNode);
    },
    //菜单类型 
    typeRadioCreate: function () {
        this.typeRadioBoxNode = new Element("div", { "class": "frm_control_group" }).inject(this.contentAreaNode);
        var labelNode = new Element("label", { "class": "frm_label" }).inject(this.typeRadioBoxNode);
        new Element("strong", { "class": "title js_menuContent", "text": this.lp.mobile_mpweixin_menu_form_radio_label }).inject(labelNode);
        var groupNode = new Element("div", { "class": "frm_controls frm_vertical_pt" }).inject(this.typeRadioBoxNode);
        //消息类型
        this.msgTypeRadioBoxNode = new Element("label", { "class": "frm_radio_label js_radio_sendMsg" }).inject(groupNode);
        new Element("i", { "class": "icon_radio" }).inject(this.msgTypeRadioBoxNode);
        new Element("span", { "class": "lbl_content", "text": this.lp.mobile_mpweixin_menu_form_radio_type_msg }).inject(this.msgTypeRadioBoxNode);
        new Element("input", { "class": "frm_radio", "type": "radio" }).inject(this.msgTypeRadioBoxNode);
        this.msgTypeRadioBoxNode.addEvents({
            "click": function () {
                this.changeMenuType("click")
            }.bind(this)
        });
        //网页类型
        this.urlTypeRadioBoxNode = new Element("label", { "class": "frm_radio_label js_radio_sendMsg" }).inject(groupNode);
        new Element("i", { "class": "icon_radio" }).inject(this.urlTypeRadioBoxNode);
        new Element("span", { "class": "lbl_content", "text": this.lp.mobile_mpweixin_menu_form_radio_type_url }).inject(this.urlTypeRadioBoxNode);
        new Element("input", { "class": "frm_radio", "type": "radio" }).inject(this.urlTypeRadioBoxNode);
        this.urlTypeRadioBoxNode.addEvents({
            "click": function () {
                this.changeMenuType("view")
            }.bind(this)
        });
        //小程序
        this.miniprogramTypeRadioBoxNode = new Element("label", { "class": "frm_radio_label js_radio_sendMsg" }).inject(groupNode);
        new Element("i", { "class": "icon_radio" }).inject(this.miniprogramTypeRadioBoxNode);
        new Element("span", { "class": "lbl_content", "text": this.lp.mobile_mpweixin_menu_form_radio_type_miniprogram }).inject(this.miniprogramTypeRadioBoxNode);
        new Element("input", { "class": "frm_radio", "type": "radio" }).inject(this.miniprogramTypeRadioBoxNode);
        this.miniprogramTypeRadioBoxNode.addEvents({
            "click": function () {
                this.changeMenuType("miniprogram")
            }.bind(this)
        });
        // 3个类型的具体内容
        this.typeContentCreate();

        this.changeMenuType(this.menu.type)
    },
    //保存按钮
    saveButtonCreate: function () {
        //<span id="pubBt" class="btn btn_input btn_primary"><button>保存并发布</button></span>
        var buttonDiv = new Element("div", { "class": "btn-box" }).inject(this.contentAreaNode);
        this.submitButtonNode = new Element("span", { "class": "btn btn_input btn_primary" }).inject(buttonDiv);
        new Element("button", { "text": this.lp.save }).inject(this.submitButtonNode);
        this.submitButtonNode.addEvents({
            "click": function () {
                this.submitAction();
            }.bind(this)
        })
    },
    typeContentCreate: function () {
        var outContentNode = new Element("div", { "class": "menu_content_container" }).inject(this.contentAreaNode);
        this.msgContentCreate(outContentNode);
        this.urlContentCreate(outContentNode);
        this.miniprogramContentCreate(outContentNode);
    },
    //文字消息
    msgContentCreate: function (outContentNode) {
        this.msgTypeContentNode = new Element("div", { "class": "menu_content dn" }).inject(outContentNode);
        new Element("p", { "class": "menu_content_tips tips_global js_url_tips", "text": this.lp.mobile_mpweixin_menu_form_type_msg_tips }).inject(this.msgTypeContentNode);
        var inputOutNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.msgTypeContentNode);
        new Element("label", { "class": "frm_label", "text": this.lp.mobile_mpweixin_menu_form_type_msg_label }).inject(inputOutNode);
        var inputNode = new Element("div", { "class": "frm_controls" }).inject(inputOutNode);
        var spanNode = new Element("span", { "class": "frm_textarea_box with_counter counter_in append" }).inject(inputNode);
        this.msgTypeContentInputNode = new Element("textarea", { "class": "frm_textarea js_menu_name", "value": this.menu.content }).inject(spanNode);
    },
    // 网页链接
    urlContentCreate: function (outContentNode) {
        this.urlTypeContentNode = new Element("div", { "class": "menu_content dn" }).inject(outContentNode);
        new Element("p", { "class": "menu_content_tips tips_global js_url_tips", "text": this.lp.mobile_mpweixin_menu_form_type_url_tips }).inject(this.urlTypeContentNode);
        var inputOutNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.urlTypeContentNode);
        new Element("label", { "class": "frm_label", "text": this.lp.mobile_mpweixin_menu_form_type_url_label }).inject(inputOutNode);
        var inputNode = new Element("div", { "class": "frm_controls" }).inject(inputOutNode);
        var spanNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputNode);
        this.urlTypeContentInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.url }).inject(spanNode);
    },
    // 小程序
    miniprogramContentCreate: function (outContentNode) {
        this.miniprogramTypeContentNode = new Element("div", { "class": "menu_content dn" }).inject(outContentNode);
        new Element("p", { "class": "menu_content_tips tips_global js_url_tips", "text": this.lp.mobile_mpweixin_menu_form_type_miniprogram_tips }).inject(this.miniprogramTypeContentNode);
        // 小程序id
        var inputOutNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.miniprogramTypeContentNode);
        new Element("label", { "class": "frm_label", "text": this.lp.mobile_mpweixin_menu_form_type_miniprogram_appid_label }).inject(inputOutNode);
        var inputNode = new Element("div", { "class": "frm_controls" }).inject(inputOutNode);
        var spanNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputNode);
        this.miniprogramTypeAppIdContentInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.appid , "placeholder": this.lp.mobile_mpweixin_menu_form_type_miniprogram_appid_placeholder }).inject(spanNode);

        // 小程序路径
        var inputPathOutNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.miniprogramTypeContentNode);
        new Element("label", { "class": "frm_label", "text": this.lp.mobile_mpweixin_menu_form_type_miniprogram_path_label }).inject(inputPathOutNode);
        var inputPathNode = new Element("div", { "class": "frm_controls" }).inject(inputPathOutNode);
        var spanPathNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputPathNode);
        this.miniprogramTypePathContentInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.pagepath , "placeholder": this.lp.mobile_mpweixin_menu_form_type_miniprogram_path_placeholder }).inject(spanPathNode);
        // 备用网页
         var inputUrlOutNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.miniprogramTypeContentNode);
         new Element("label", { "class": "frm_label", "text": this.lp.mobile_mpweixin_menu_form_type_miniprogram_url_label }).inject(inputUrlOutNode);
         var inputUrlNode = new Element("div", { "class": "frm_controls" }).inject(inputUrlOutNode);
         var spanUrlNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputUrlNode);
         this.miniprogramTypeUrlContentInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.url , "placeholder": this.lp.mobile_mpweixin_menu_form_type_miniprogram_url_placeholder }).inject(spanUrlNode);
    },
    changeMenuType: function (type) {
        if (type) {
            this.menu.type = type;
            if (type === "view") {
                this.msgTypeRadioBoxNode.removeClass("selected");
                this.msgTypeContentNode.addClass("dn");
                this.urlTypeRadioBoxNode.addClass("selected");
                this.urlTypeContentNode.removeClass("dn");
                this.miniprogramTypeRadioBoxNode.removeClass("selected");
                this.miniprogramTypeContentNode.addClass("dn");
            } else if (type === "miniprogram") {
                this.msgTypeRadioBoxNode.removeClass("selected");
                this.msgTypeContentNode.addClass("dn");
                this.urlTypeRadioBoxNode.removeClass("selected");
                this.urlTypeContentNode.addClass("dn");
                this.miniprogramTypeRadioBoxNode.addClass("selected");
                this.miniprogramTypeContentNode.removeClass("dn");
            } else {
                this.msgTypeRadioBoxNode.addClass("selected");
                this.msgTypeContentNode.removeClass("dn");
                this.urlTypeRadioBoxNode.removeClass("selected");
                this.urlTypeContentNode.addClass("dn");
                this.miniprogramTypeRadioBoxNode.removeClass("selected");
                this.miniprogramTypeContentNode.addClass("dn");
            }
        } else {
            this.msgTypeRadioBoxNode.addClass("selected");
            this.msgTypeContentNode.removeClass("dn");
            this.urlTypeRadioBoxNode.removeClass("selected");
            this.urlTypeContentNode.addClass("dn");
            this.miniprogramTypeRadioBoxNode.removeClass("selected");
            this.miniprogramTypeContentNode.addClass("dn");
        }
    },
    //提交菜单数据
    submitAction: function() {
        var name = this.nameInputNode.get("value");
        if (!name || name === "") {
            this.app.notice(this.lp.mobile_mpweixin_menu_form_name_error_empty, "error", this.menuApp.contentAreaNode);
            return;
        }
        if (name.length > 4) {
            this.app.notice(this.lp.mobile_mpweixin_menu_form_name_error_max_len, "error", this.menuApp.contentAreaNode);
            return;
        }
        var order = this.orderInputNode.get("value");
        if (!order || order === "") {
            this.app.notice(this.lp.mobile_mpweixin_menu_form_order_error_empty, "error", this.menuApp.contentAreaNode);
            return;
        }
        //数字
        var reg = /^[\d]+$/;
        var s = reg.test(order);
        if (s === false) {
            this.app.notice(this.lp.mobile_mpweixin_menu_form_order_error_not_number, "error", this.menuApp.contentAreaNode);
            return;
        }
        if (order.length > 6) {
            this.app.notice(this.lp.mobile_mpweixin_menu_form_order_error_max_len, "error", this.menuApp.contentAreaNode);
            return;
        }
        this.menu.name = name;
        this.menu.order = order;
        //父菜单 只需要name和order
        if (this.haSubMenus && this.haSubMenus === true) {
            console.log("存在子菜单菜单");
        }else {
            var type = this.menu.type;
            if (type === "view") { //网页
                var url = this.urlTypeContentInputNode.get("value");
                if (!url || url === "") {
                    this.app.notice(this.lp.mobile_mpweixin_menu_form_type_url_error_empty, "error", this.menuApp.contentAreaNode);
                    return;
                }
                this.menu.url = url;
                //新建的菜单删除id
                if (this.menu.id.startsWith('mock_')) {
                    delete this.menu.id; //新增要删除id
                }
            } else if (type === "miniprogram") { //小程序
                var appid = this.miniprogramTypeAppIdContentInputNode.get("value");
                if (!appid || appid === "") {
                    this.app.notice(this.lp.mobile_mpweixin_menu_form_type_miniprogram_appid_error_empty, "error", this.menuApp.contentAreaNode);
                    return;
                }
                var path = this.miniprogramTypePathContentInputNode.get("value");
                if (!path || path === "") {
                    this.app.notice(this.lp.mobile_mpweixin_menu_form_type_miniprogram_path_error_empty, "error", this.menuApp.contentAreaNode);
                    return;
                }
                var url = this.miniprogramTypeUrlContentInputNode.get("value");
                if (!url || url === "") {
                    this.app.notice(this.lp.mobile_mpweixin_menu_form_type_miniprogram_url_error_empty, "error", this.menuApp.contentAreaNode);
                    return;
                }
                this.menu.appid = appid;
                this.menu.pagepath = path;
                this.menu.url = url;
                //新建的菜单删除id
                if (this.menu.id.startsWith('mock_')) {
                    delete this.menu.id; //新增要删除id
                }
            } else { //消息
                var content = this.msgTypeContentInputNode.get("value");
                if (!content || content === "") {
                    this.app.notice(this.lp.mobile_mpweixin_menu_form_type_msg_error_empty, "error", this.menuApp.contentAreaNode);
                    return;
                }
                this.menu.content = content;
                //新建的菜单 click类型需要设置key 
                if (this.menu.id.startsWith('mock_')) {
                    this.menu.key = this.menu.id;
                    delete this.menu.id; //新增要删除id
                }
            }
        }
        //写入数据 远程写入 还有 上级数组中
        if (!this.menu.id) { //新增
            o2.Actions.load("x_program_center").MPWeixinAction.menuAdd(this.menu,function (json) {
                this.menu.id = json.data.id;//更新id
                this.menuApp.setMenuData(this.menu);
                this.app.notice(this.lp.mobile_mpweixin_menu_save_success, "success", this.menuApp.contentAreaNode);
            }.bind(this));
        }else { // 更新
            o2.Actions.load("x_program_center").MPWeixinAction.menuUpdate(this.menu.id, this.menu,function (json) {
                this.menuApp.setMenuData(this.menu);
                this.app.notice(this.lp.mobile_mpweixin_menu_save_success, "success", this.menuApp.contentAreaNode);
            }.bind(this));
        }
    },



});

MWF.xApplication.Setting.MobileStyleDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function () {
        this.node = new Element("div", { "styles": { "overflow": "hidden", "padding-bottom": "80px" } }).inject(this.contentAreaNode);
        this.titleName = new Element("div", { "styles": this.explorer.css.explorerContentTitleNode }).inject(this.node);
        this.titleName.set("text", this.lp.mobile_styleSetting);

        this.explorer.imagesData.images.each(function (img, i) {
            var imgName = this.lp.mobile_style_imgs[img.name];
            var title = this.lp.mobile_style.replace("{name}", imgName);
            var infor = this.lp.mobile_style_infor.replace("{name}", imgName);

            new MWF.xApplication.Setting.Document.Image(this.explorer, this.node, {
                "lp": { "title": title, "infor": "" },
                "data": { "key": "imagesData", "valueKey": "applicationList", "notEmpty": false },
                "value": img,
                "itemTitle": infor,
                "iconData": img.value
            });
        }.bind(this));
    }
});