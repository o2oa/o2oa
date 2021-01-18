
function o2TestLoader() {
    //o2.base = "base/";
    layout.debugger = true;
    o2.session.isDebugger = true;
    //兼容方法
    if (window.Element){
        Element.implement({
            "makeLnk": function (options) { }
        });
    }
    layout.desktop.addEvent = function (type, e, d) {
        window.addEvent(type, e, d);
    };
    layout.desktop.addEvents = function (e) {
        window.addEvents(e);
    };

    // var loadingNode = (window.$) ? $("loaddingArea") : null;
    var loadeds = 0;
    var loadCount = 4;
    var size = (window.document && document.body) ? document.body.getSize() : null;

    var _setLayoutService = function(service, center){
        layout.serviceAddressList = service;
        layout.centerServer = center;
        layout.desktop.serviceAddressList = service;
        layout.desktop.centerServer = center;
    };
    var _getDistribute = function (callback) {

        if (layout.config.app_protocol === "auto") {
            layout.config.app_protocol = window.location.protocol;
        }

        if (layout.config.configMapping && (layout.config.configMapping[window.location.host] || layout.config.configMapping[window.location.hostname])){
            var mapping = layout.config.configMapping[window.location.host] || layout.config.configMapping[window.location.hostname];
            if (mapping.servers){
                layout.serviceAddressList = mapping.servers;
                layout.desktop.serviceAddressList = mapping.servers;
                if (mapping.center) center = (o2.typeOf(mapping.center)==="array") ? mapping.center[0] : mapping.center;
                layout.centerServer = center;
                layout.desktop.centerServer = center;
                if (callback) callback();
            }else{
                if (mapping.center) layout.config.center = (o2.typeOf(mapping.center)==="array") ? mapping.center : [mapping.center];
                o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
                    _setLayoutService(service, center);
                    if (callback) callback();
                }.bind(this));
            }
        }else{
            o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
                _setLayoutService(service, center);
                if (callback) callback();
            }.bind(this));
        }

    };

    var _load = function () {
        var _loadApp = function (json) {
            //用户已经登录
            if (json){
                layout.user = json.data;
                layout.session = layout.session || {};
                layout.session.user = json.data;
                layout.session.token = json.data.token;
                layout.desktop.session = layout.session;
            }

            while (layout.readys && layout.readys.length) {
                layout.readys.shift().apply(window);
            }
        };

        layout.sessionPromise = new Promise(function(resolve, reject){
            o2.Actions.get("x_organization_assemble_authentication").getAuthentication(function (json) {
                if (resolve) resolve(json.data);
            }.bind(this), function (xhr, text, error) {
                if (reject) reject({"xhr": xhr, "text": text, "error": error});
            }.bind(this));
        });

        layout.sessionPromise.then(function(data){
            //已经登录
            layout.user = data;
            layout.session = layout.session || {};
            layout.session.user = data;
            layout.session.token = data.token;
            layout.desktop.session = layout.session;
            _loadApp();
        }, function(){
            //允许匿名访问
            if (layout.anonymous) {
                var data = { name: "anonymous", roleList: [] };
                layout.user = data;
                layout.session = layout.session || {};
                layout.session.user = data;
                layout.session.token = data.token;
                layout.desktop.session = layout.session;
                _loadApp();
            } else {
                if (o2Unit.username){
                    o2.Actions.load("x_organization_assemble_authentication").AuthenticationAction.login({"credential":o2Unit.username,"password":o2Unit.password}, function (json) {
                        _loadApp(json);
                    }.bind(this), function (xhr, text, error) {
                        layout.openLogin();
                    }.bind(this));
                }else{
                    layout.openLogin();
                }
            }
        });
        //_loadApp();

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
    };

    //异步载入必要模块
    var configLoaded = false;
    var lpLoaded = false;
    var commonLoaded = false;
    var appLoaded = false;
    var lp = o2.session.path + "/lp/" + o2.language + ".js";

    if (o2.session.isDebugger && (o2.session.isMobile || layout.mobile)) o2.load("../o2_lib/eruda/eruda.js");

    var loadModuls = function () {
        lpLoaded = true;

        var modules = [
            'o2.widget.Common',
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
            'o2.xDesktop.Window'
        ];
        o2.require(modules, {
            "onSuccess": function () {
                commonLoaded = true;
                if (configLoaded && commonLoaded && lpLoaded && appLoaded) _getDistribute(function () { _load(); });
            }
        });
        var apps = [
            ["Template", "MPopupForm"],
            ["Common", ""]
        ];
        o2.requireApp(apps, null, function(){
            appLoaded = true;
            if (configLoaded && commonLoaded && lpLoaded && appLoaded) _getDistribute(function () { _load(); });
        });
    }

    if (!o2.LP){
        o2.load(lp, loadModuls);
    }else{
        loadModuls();
    }

    //o2.getJSON("../x_desktop/res/config/config.json", function (config) {
        configLoaded = true;
        if (configLoaded && commonLoaded && lpLoaded && appLoaded) _getDistribute(function () { _load(); });
    //});
};

o2Unit = {
    //options: {
    //  "username": "",
    //  "password": "",
    //  "anonymous": false (default) or true,
    //  "mode": "spa"(default) or "mpa"
    // }
    init: function(options, fn){
        this.username = options.username;
        this.password = options.password;
        layout.anonymous = (options.anonymous===true);
        this.mode = options.mode || "spa";
        o2.addReady(o2TestLoader);
        layout.addReady(function(){
            this["load_"+this.mode](function(){
                if (fn) fn.apply(window);
            });
        }.bind(this));
        //if (fn) layout.addReady(fn);
    },
    load_spa: function(fn){
        var html = "<div id=\"layout_main\" style=\"overflow: hidden; height: 100%; background-position-x: center; background-size: cover;\">\n" +
            "        <div id=\"layout_top_shim\"></div>\n" +
            "        <div id=\"layout_top\"></div>\n" +
            "        <div id=\"layout_desktop\">\n" +
            "            <div id=\"desktop_content\"></div>\n" +
            "            <div id=\"desktop_navi\"></div>\n" +
            "        </div>\n" +
            "    </div>"
        document.body.appendHTML(html);
        layout.inBrowser = false;
        debugger;
        layout.desktop.type = "layout";
        layout.viewMode = "Default";
        var _load = function(){
            layout.userLayout = {};
            layout.userLayout.apps = {};
            MWF.require("MWF.xDesktop.Default", function(){
                layout.desktop = new MWF.xDesktop.Default("layout_main", {});
                layout.desktop.load(function(){
                    if (fn) fn();
                });
                if (!layout.desktop.openApplication) layout.desktop.openApplication = layout.openApplication;
                if (!layout.desktop.refreshApp) layout.desktop.refreshApp = layout.refreshApp;
            });
        };
        if (layout.session && layout.session.user){
            _load();
        }else{
            if (layout.sessionPromise){
                layout.sessionPromise.then(function(){
                    _load();
                },function(){});
            }
        }
    },
    load_mpa: function(fn){
        var html = "<div id=\"appContent\" style=\"overflow: hidden; height:100%; background-color:#EEE\"></div>";
        document.body.appendHTML(html);
        if (fn) fn();
    },
    openApplication: function(fn, appNames, options){
        var option = options || {};
        option.onPostLoadApplication = function(){
            this.runPostLoadApplication = true;
            if (fn) fn();
        };
        option.postLoad = function(){
            if (!this.runPostLoadApplication) if (fn) fn();
        }
        var args = Array.from(arguments);
        args.shift();
        args.unshift(null);
        if (!options) args.push(option);
        layout.openApplication.apply(layout, args);
    }
};
