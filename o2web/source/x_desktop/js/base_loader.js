if (!layout.isReady) {
    o2.addReady(function () {
        //兼容方法
        if (window.Element) {
            Element.implement({
                "makeLnk": function (options) {
                }
            });
        }
        layout.desktop.addEvent = function (type, e, d) {
            window.addEvent(type, e, d);
        };
        layout.desktop.addEvents = function (e) {
            window.addEvents(e);
        };

        var loadingNode = (window.$) ? $("loaddingArea") : null;
        var loadeds = 0;
        var loadCount = 4;
        var size = (window.document && document.body) ? document.body.getSize() : null;
        var _closeLoadingNode = function () {
            if (loadingNode) {
                loadingNode.destroy();
                loadingNode = null;
            }
        };
        var _loadProgressBar = function (complete) {
            if (loadingNode) {
                if (complete) {
                    loadingNode.setStyles({"width": "" + size.x + "px"});
                    //loadingNode.set('morph', {duration: 100}).morph({"width": ""+size.x+"px"});
                    window.setTimeout(_closeLoadingNode, 500);
                } else {
                    loadeds++;
                    var p = (loadeds / loadCount) * size.x;
                    loadingNode.setStyles({"width": "" + p + "px"});
                    //loadingNode.set('morph', {duration: 100}).morph({"width": ""+p+"px"});
                    if (loadeds >= loadCount) window.setTimeout(_closeLoadingNode, 500);
                }
            }
        };

        var _setLayoutService = function (service, center) {
            layout.serviceAddressList = service;
            layout.centerServer = center;
            layout.desktop.serviceAddressList = service;
            layout.desktop.centerServer = center;
        };
        var _getDistribute = function (callback) {

            if (layout.config.app_protocol === "auto") {
                layout.config.app_protocol = window.location.protocol;
            }

            if (layout.config.configMapping && (layout.config.configMapping[window.location.host] || layout.config.configMapping[window.location.hostname])) {
                var mapping = layout.config.configMapping[window.location.host] || layout.config.configMapping[window.location.hostname];
                if (mapping.servers) {
                    layout.serviceAddressList = mapping.servers;
                    layout.desktop.serviceAddressList = mapping.servers;
                    if (mapping.center) center = (o2.typeOf(mapping.center) === "array") ? mapping.center[0] : mapping.center;
                    layout.centerServer = center;
                    layout.desktop.centerServer = center;
                    if (callback) callback();
                } else {
                    if (mapping.center) layout.config.center = (o2.typeOf(mapping.center) === "array") ? mapping.center : [mapping.center];
                    o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
                        _setLayoutService(service, center);
                        _loadProgressBar();
                        if (callback) callback();
                    }.bind(this));
                }
            } else {
                o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
                    _setLayoutService(service, center);
                    _loadProgressBar();
                    if (callback) callback();
                }.bind(this));
            }

        };

        var _load = function () {
            var _loadApp = function (json) {
                //用户已经登录
                if (json) {
                    layout.user = json.data;
                    layout.session = layout.session || {};
                    layout.session.user = json.data;
                    layout.session.token = json.data.token;
                    layout.desktop.session = layout.session;
                }

                _loadProgressBar(true);
                layout.isReady = true;
                while (layout.readys && layout.readys.length) {
                    layout.readys.shift().apply(window);
                }
            };

            // 是否ip
            var _isIp = function (ip) {
                var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
                return reg.test(ip);
            };


            //修改支持x-token
            var uri = new URI(window.location.href);
            var options = uri.get("data");
            if (options[o2.tokenName]) {
                // 删除
                // Cookie.dispose(o2.tokenName);
                // // 写入
                // var host = window.location.hostname; // 域名
                // var domain = null;
                // if (_isIp(host)) {
                //     domain = host;
                // } else {
                //     if (host.indexOf(".") > 0) {
                //         domain = host.substring(host.indexOf(".")); // 上级域名 如 .o2oa.net
                //     }
                // }
                // if (domain) {
                //     Cookie.write(o2.tokenName, options[o2.tokenName], {domain: domain, path: "/"});
                // } else {
                //     Cookie.write(o2.tokenName, options[o2.tokenName]);
                // }
                if (window.layout) {
                    if (!layout.session) layout.session = {};
                    layout.session.token = options[o2.tokenName];
                }
                if (layout.config && layout.config.sessionStorageEnable && window.sessionStorage) window.sessionStorage.setItem("o2LayoutSessionToken", options[o2.tokenName]);
            }

            layout.sessionPromise = new Promise(function (resolve, reject) {
                o2.Actions.get("x_organization_assemble_authentication").getAuthentication(function (json) {
                    if (json.data.language && (json.data.language !== o2.languageName)) {
                        o2.language = json.data.language.toLowerCase();
                        o2.languageName = json.data.language;
                        var lp = "../x_desktop/js/base_lp_" + o2.language + ((o2.session.isDebugger) ? "" : ".min") + ".js?v="+o2.version.v;;
                        o2.load(lp, {"reload": true}, function () {
                            if (resolve) resolve(json.data);
                        });
                    } else {
                        if (resolve) resolve(json.data);
                    }
                }.bind(this), function (xhr, text, error) {
                    if (reject) reject({"xhr": xhr, "text": text, "error": error});
                }.bind(this));
            });

            layout.sessionPromise.then(function (data) {
                //已经登录
                layout.user = data;
                layout.session = layout.session || {};
                layout.session.user = data;
                layout.session.token = data.token;
                layout.desktop.session = layout.session;
                //_loadApp();
            }, function () {
                //允许匿名访问
                if (layout.anonymous) {
                    var data = {name: "anonymous", roleList: []};
                    layout.user = data;
                    layout.session = layout.session || {};
                    layout.session.user = data;
                    layout.session.token = data.token;
                    layout.desktop.session = layout.session;
                    //_loadApp();
                } else {
                    _loadProgressBar(true);
                    if (layout.yqwx) {
                        layout.openLoginQywx();
                    } else {
                        layout.openLogin();
                    }
                }
            });
            _loadApp();

            layout.openLogin = function () {
                layout.desktop.type = "app";
                layout.app = null;
                var content = $("appContent") || $("layout");
                if (content) content.empty();
                layout.authentication = new o2.xDesktop.Authentication({
                    "style": "flat",
                    "onLogin": _load.bind(layout)
                });
                layout.authentication.loadLogin(document.body);
                var loadingNode = $("browser_loading");
                if (loadingNode) loadingNode.fade("out");
            };

            layout.openLoginQywx = function () {
                //console.log("开始login。。。。。。。。。。。。。");
                var uri = locate.href.toURI();

                //console.log("执行单点。。。。。。。。。。");
                var action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_authentication", "");
                action.getActions = function (actionCallback) {
                    this.actions = {"sso": {"uri": "/jaxrs/qiyeweixin/code/{code}", "method": "GET"}};
                    if (actionCallback) actionCallback();
                };
                action.invoke({
                    "name": "sso",
                    "async": true,
                    "parameter": {"code": uri.getData("code")},
                    "success": function (json) {
                        //console.log("单点成功。");
                        //console.log(json);
                        //基础数据。。。。
                        layout.session.user = json.data;
                        //
                        _load();

                    }.bind(this),
                    "failure": function (xhr, text, error) {
                        var n = document.getElementById("loaddingArea");
                        if (n) {
                            n.destroy();
                        }
                        document.id("layout").set("html", "<div>企业微信单点异常！</div>");
                    }.bind(this)
                });
            };
        };

        //异步载入必要模块
        layout.config = null;
        var configLoaded = false;
        var lpLoaded = false;
        var commonLoaded = false;
        //var lp = o2.session.path + "/lp/" + o2.language + ".js";
        // var lp = "../x_desktop/js/base_lp_" + o2.language + ((o2.session.isDebugger) ? "" : ".min") + ".js?v="+o2.version.v;;

        if (o2.session.isDebugger && (o2.session.isMobile || layout.mobile)) o2.load("../o2_lib/eruda/eruda.js");
        var loadAllModules = function(error){
            _loadProgressBar();
            lpLoaded = true;

            var modules = ["o2.xDesktop.$all"];
            o2.require(modules, {
                "onSuccess": function () {
                    if (o2.xDesktop.getServiceAddress){
                        commonLoaded = true;
                        if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () {
                            _load();
                        });
                    }else{
                        if (error) error();
                    }
                },
                "onFailure": function(){ if (error) error();},
                "onEvery": function () {
                    _loadProgressBar();
                }
            });
        }
        var loadO2Modules = function(){
            _loadProgressBar();
            lpLoaded = true;

            var o2modules = ['o2.widget.Common',
                'o2.widget.Dialog',
                'o2.widget.UUID',
                'o2.xDesktop.Common',
                'o2.xDesktop.Actions.RestActions',
                'o2.xAction.RestActions',
                'o2.xDesktop.Access',
                'o2.xDesktop.Dialog',
                'o2.xDesktop.Menu',
                'o2.xDesktop.UserData',
                'o2.xDesktop.Authentication',
                'o2.xDesktop.Dialog',
                'o2.xDesktop.Window'];
            o2.require(o2modules, {
                "onSuccess": function () {
                    var appmodules = [['Template', 'MPopupForm'], ['Common', 'Main']];
                    o2.requireApp(appmodules, "", {
                        "onSuccess": function(){
                            if (o2.xDesktop.getServiceAddress){
                                commonLoaded = true;
                                if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () {
                                    _load();
                                });
                            }
                        },
                        "onEvery": function () {
                            _loadProgressBar();
                        }
                    });
                },
                "onEvery": function () {
                    _loadProgressBar();
                }
            });
        }


        var loadModuls = function () {
            loadAllModules(loadO2Modules);
        };


        o2.getJSON("../x_desktop/res/config/config.json", function (config) {
            var supportedLanguages = Object.keys(config.supportedLanguages);

            if (supportedLanguages.indexOf(o2.language) === -1){
                o2.language = o2.language.substring(0, o2.language.indexOf('-'));
            }
            if (supportedLanguages.indexOf(o2.language) === -1) o2.language = "zh-cn";

            if (!o2.LP) {
                var lp = "../x_desktop/js/base_lp_" + o2.language + ((o2.session.isDebugger) ? "" : ".min") + ".js?v="+o2.version.v;
                o2.load(lp, function(m){
                    if (!m.length){
                        var lp = "../o2_core/o2/lp/" + o2.language + ((o2.session.isDebugger) ? "" : ".min") + ".js?v="+o2.version.v;
                        o2.load(lp,loadModuls);
                    }else{
                        loadModuls();
                    }
                });
            } else {
                loadModuls();
            }

            _loadProgressBar();
            if (config.proxyCenterEnable){
                if (o2.typeOf(config.center)==="array"){
                    config.center.forEach(function(c){
                        c.port = window.location.port || 80;
                    });
                }else{
                    config.port = window.location.port || 80;
                }
            }
            layout.config = config;
            o2.tokenName = config.tokenName || "x-token";

            if( !layout.mobile )document.title = layout.config.systemTitle || layout.config.title;

            configLoaded = true;
            if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () {
                _load();
            });
        });
    });
}
