
/**
 * 微信公众号单点登录
 * 目前这个页面有两个功能 一个是绑定账号 一个是单点登录
 */
layout = window.layout || {};
var locate = window.location;
layout.protocol = locate.protocol;
layout.inBrowser = true;
var href = locate.href;
if (href.indexOf("debugger") != -1) layout.debugger = true;
layout.desktop = layout;
layout.session = layout.session || {};


o2.xDesktop = o2.xDesktop || {};
o2.xDesktop.requireApp = function (module, clazz, callback, async) {
    o2.requireApp(module, clazz, callback, async);
};

o2.addReady(function () {
    // loading 
    var loadingNode = $("loaddingArea");
    var loadeds = 0;
    var loadCount = 16;
    var size = document.body.getSize();
    var _closeLoadingNode = function () {
        if (loadingNode) {
            loadingNode.destroy();
            loadingNode = null;
        }
    };
    var _loadProgressBar = function (complete) {
        if (loadingNode) {
            if (complete) {
                loadingNode.setStyles({ "width": "" + size.x + "px" });
                //loadingNode.set('morph', {duration: 100}).morph({"width": ""+size.x+"px"});
                window.setTimeout(_closeLoadingNode, 500);
            } else {
                loadeds++;
                var p = (loadeds / loadCount) * size.x;
                loadingNode.setStyles({ "width": "" + p + "px" });
                //loadingNode.set('morph', {duration: 100}).morph({"width": ""+p+"px"});
                if (loadeds >= loadCount) window.setTimeout(_closeLoadingNode, 500);
            }
        }
    };

    layout.showLoading = function () {
        layout.loadingGif = new Element("div", { "sytle": "position:fixed;left:0;right:0;top:0;text-align: center;margin: 60px;" }).inject($(document.body));
        new Element("img", { "src": "../x_desktop/img/loading2.gif", "style": "width: 96px; height: 96px;" }).inject(layout.loadingGif);
    };

    layout.hideLoading = function () {
        if (layout.loadingGif) {
            layout.loadingGif.destroy()
            layout.loadingGif = null
        }
    };

     // 打开登录界面
     layout.openLogin = function () {
        layout.authentication = new o2.xDesktop.Authentication({
            "style": "flat",
            "onLogin": _load.bind(layout)
        });
        layout.authentication.loadLogin(document.body);
    };
    //绑定微信openid到用户信息
    layout._bindWeixin2User = function () {
        var uri = href.toURI();
        var code = uri.getData("code"); //微信code
        if (code) {
            layout.showLoading();
            o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.bindWithCode(code, function (json) {
                layout.hideLoading();
                //绑定成功
                var box = new Element("div", { "style": "text-align: center;" }).inject($("appContent"));
                new Element("h2", { "text": "绑定成功！" }).inject(box);
            }, function (err) {
                layout.hideLoading();
                console.log(err);
                // layout.notice('绑定账号失败', 'error');
            })
        } else {
            layout.hideLoading();
            console.log('没有传入微信code无法绑定');
            // layout.notice('没有传入微信code无法绑定', 'error');
        }
    };
 
     var _getDistribute = function (callback) {
         if (layout.config.app_protocol === "auto") {
             layout.config.app_protocol = window.location.protocol;
         }
         o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
             layout.serviceAddressList = service;
             layout.centerServer = center;
             _loadProgressBar();
             if (callback) callback();
         }.bind(this));
     };
 
     
     var _load = function() {
        _loadProgressBar(true);
        var uri = href.toURI();
        var redirect = uri.getData("redirect"); //登录成功后跳转地址
        var code = uri.getData("code"); //微信code
        console.log("code：" + code)
        var type = uri.getData("type"); // bind 是绑定
        console.log("type：" + type)
        if (type && type === "bind") { // 绑定要登录
            //先判断用户是否登录
            o2.Actions.get("x_organization_assemble_authentication").getAuthentication(function (json) {
                //已经登录
                layout.session.user = json.data;
                layout._bindWeixin2User();
            }.bind(this), function (json) {
                console.debug("需要登录", json);
                layout.openLogin();
            });
        } else { //code 单点登录
            layout.showLoading();
            o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.loginWithCode(code, function (json) {
                layout.hideLoading();
                layout.session.user = json.data;
                if (redirect) {
                    history.replaceState(null, "page", redirect);
                    redirect.toURI().go();
                } else {
                    history.replaceState(null, "page", "../x_desktop/appMobile.html?app=process.TaskCenter");
                    "appMobile.html?app=process.TaskCenter".toURI().go();
                }
            }, function (err) {
                layout.hideLoading();
                console.log(err)
                // layout.notice('单点登录失败，请先绑定用户', 'error');
            });
        }
     };


     //异步载入必要模块
     layout.config = null;
     var configLoaded = false;
     var lpLoaded = false;
     var commonLoaded = false;
     var lp = o2.session.path + "/lp/" + o2.language + ".js";
     o2.load(lp, function () {
         _loadProgressBar();
         lpLoaded = true;
 
         var modules = ["o2.xDesktop.$all"];
        // var modules = ['o2.widget.Common',
        //         'o2.widget.Dialog',
        //         'o2.widget.UUID',
        //         'o2.xDesktop.Common',
        //         'o2.xDesktop.Actions.RestActions',
        //         'o2.xAction.RestActions',
        //         'o2.xDesktop.Access',
        //         'o2.xDesktop.Dialog',
        //         'o2.xDesktop.Menu',
        //         'o2.xDesktop.UserData',
        //         'o2.xDesktop.Authentication',
        //         'o2.xDesktop.Dialog',
        //         'o2.xDesktop.Window'];
         o2.require(modules, {
             "onSuccess": function () {
                 commonLoaded = true;
                 if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { _load(); });
             },
             "onEvery": function () {
                 _loadProgressBar();
             }
         });
     });

     // 请求config.json 然后再开始业务
     o2.getJSON("../x_desktop/res/config/config.json", function (config) {
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
        configLoaded = true;
        if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { _load(); });
    });
     



    // o2.load(["../o2_lib/mootools/plugin/mBox.Notice.js", "../o2_lib/mootools/plugin/mBox.Tooltip.js"], { "sequence": true }, function () {
    //     MWF.loadLP("zh-cn");
    //     MWF.require("MWF.xDesktop.Layout", function () {
    //         MWF.require("MWF.xDesktop.Authentication", null, false);

    //         (function () {

    //             layout.load = function () {
    //                 var uri = href.toURI();
    //                 var redirect = uri.getData("redirect"); //登录成功后跳转地址
    //                 var code = uri.getData("code"); //微信code
    //                 console.log("code：" + code)
    //                 var type = uri.getData("type"); // bind 是绑定
    //                 console.log("type：" + type)
    //                 if (type && type === "bind") {
    //                     layout._authForm();
    //                 } else { // code 登录
    //                     layout.showLoading();
    //                     o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.loginWithCode(code, function (json) {
    //                         console.log(json);
    //                         layout.hideLoading();
    //                         layout.session.user = json.data;
    //                         if (redirect) {
    //                             history.replaceState(null, "page", redirect);
    //                             redirect.toURI().go();
    //                         } else {
    //                             history.replaceState(null, "page", "../x_desktop/appMobile.html?app=process.TaskCenter");
    //                             "appMobile.html?app=process.TaskCenter".toURI().go();
    //                         }
    //                     }, function (err) {
    //                         layout.hideLoading();
    //                         console.log(err)
    //                         layout.notice('单点登录失败，请先绑定用户', 'error');
    //                     });
    //                 }

    //             };


    //             /**
    //              * 认证表单
    //              */
    //             layout._authForm = function () {
    //                 //加载资源
    //                 var css = "../x_desktop/template/_mpwxssoLogin.css";
    //                 var html = "../x_desktop/template/_mpwxssoLogin.html";
    //                 var myBody = $(document.body);
    //                 myBody.loadAll({ "css": [css], "html": [html] }, { "bind": layout, "module": layout }, function () {
    //                     // 密码登录模式
    //                     layout.loginPwdModeNode.addEvents({
    //                         "click": function (e) {
    //                             e.target.addClass("mode-active");
    //                             layout.loginCodeModeNode.removeClass("mode-active");
    //                             layout.loginCodeBoxNode.setStyles({ "display": "none" });
    //                             layout.loginInputPasswordNode.setStyles({ "display": "" });
    //                             layout.loginMode = "password";
    //                         }
    //                     });
    //                     // 验证码登录模式
    //                     layout.loginCodeModeNode.addEvents({
    //                         "click": function (e) {
    //                             e.target.addClass("mode-active");
    //                             layout.loginPwdModeNode.removeClass("mode-active");
    //                             layout.loginCodeBoxNode.setStyles({ "display": "" });
    //                             layout.loginInputPasswordNode.setStyles({ "display": "none" });
    //                             layout.loginMode = "code";
    //                         }
    //                     });
    //                     layout.getCodeBtnNode.addEvents({
    //                         "click": function (e) {
    //                             layout._getCodeForLogin();
    //                         }
    //                     });
    //                     //登录
    //                     layout.loginBtnNode.addEvents({
    //                         "click": function (e) {
    //                             var username = layout.loginInputUsernameNode.get('value');
    //                             if (!username || username === '') {
    //                                 layout.notice('请先输入用户名或手机号码！', "error")
    //                                 return
    //                             }
    //                             if (layout.loginMode && layout.loginMode === 'code') { // 验证码登录
    //                                 var code = layout.loginInputCodeNode.get('value');
    //                                 if (!code || code === '') {
    //                                     layout.notice('请先输入验证码！', "error")
    //                                     return
    //                                 }
    //                                 var data = {
    //                                     credential: username,
    //                                     codeAnswer: code
    //                                 }
    //                                 layout.showLoading();
    //                                 o2.Actions.load("x_organization_assemble_authentication").AuthenticationAction.codeLogin(data, function (json) {
    //                                     console.log('登录成功。。。。。。。');
    //                                     layout.session.user = json.data;
    //                                     //绑定用户
    //                                     layout._bindWeixin2User();
    //                                 }, function (err) {
    //                                     layout.hideLoading();
    //                                     console.log(err)
    //                                     layout.notice('登录失败！', "error")
    //                                 });
    //                             } else { //密码登录
    //                                 var password = layout.loginInputPasswordNode.get('value');
    //                                 if (!password || password === '') {
    //                                     layout.notice('请先输入密码！', "error")
    //                                     return
    //                                 }
    //                                 var data = {
    //                                     credential: username,
    //                                     password: password
    //                                 }
    //                                 layout.showLoading();
    //                                 o2.Actions.load("x_organization_assemble_authentication").AuthenticationAction.login(data, function (json) {
    //                                     layout.session.user = json.data;
    //                                     //删除登录窗口
    //                                     layout.loginBoxNode.destroy();
    //                                     //绑定用户
    //                                     layout._bindWeixin2User();
    //                                 }, function (err) {
    //                                     layout.hideLoading();
    //                                     console.log(err)
    //                                     layout.notice('登录失败！', "error")
    //                                 });
    //                             }

    //                         }
    //                     })
    //                 }.bind(layout));
    //             };
    //             //绑定微信openid到用户信息
    //             layout._bindWeixin2User = function () {
    //                 var uri = href.toURI();
    //                 var code = uri.getData("code"); //微信code
    //                 if (code) {
    //                     o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.bindWithCode(code, function (json) {
    //                         layout.hideLoading();
    //                         //绑定成功
    //                         var box = new Element("div", { "style": "text-align: center;" }).inject($(document.body));
    //                         new Element("h2", { "text": "绑定成功！" }).inject(box);
    //                     }, function (err) {
    //                         layout.hideLoading();
    //                         console.log(err)
    //                         layout.notice('绑定账号失败', 'error');
    //                     })
    //                 } else {
    //                     layout.hideLoading();
    //                     layout.notice('没有传入微信code无法绑定', 'error');
    //                 }
    //             };
    //             // 获取验证码
    //             layout._getCodeForLogin = function () {
    //                 var username = layout.loginInputUsernameNode.get('value');
    //                 if (!username || username === '') {
    //                     layout.notice('请先输入用户名或手机号码！', "error")
    //                     return
    //                 }
    //                 layout.countDownTime = 60;
    //                 // 修改按钮样式
    //                 layout.getCodeBtnNode.removeClass("code-btn");
    //                 layout.getCodeBtnNode.addClass("code-btn-sending");
    //                 layout.countDownTime--;
    //                 layout.getCodeBtnNode.set('text', layout.countDownTime + '秒后可重新获取');
    //                 layout.countDownFun = setInterval(() => {
    //                     layout._countDownSendCode();
    //                 }, 1000)
    //                 o2.Actions.load("x_organization_assemble_authentication").AuthenticationAction.code(username, function (json) {
    //                     layout.notice('验证码已经发送，请注意查收！', 'success');
    //                 }, function (err) {
    //                     console.log(err);
    //                     layout.notice('发送验证码失败！', 'error');
    //                 });

    //             };
    //             // countdown 定时器
    //             layout._countDownSendCode = function () {
    //                 if (layout.countDownTime === 0) {
    //                     layout.getCodeBtnNode.removeClass("code-btn-sending");
    //                     layout.getCodeBtnNode.addClass("code-btn");
    //                     layout.getCodeBtnNode.set('text', '获取验证码');
    //                     layout.countDownTime = 60
    //                     if (layout.countDownFun) {
    //                         clearInterval(layout.countDownFun)
    //                         layout.countDownFun = null
    //                     }
    //                 } else {
    //                     layout.countDownTime--
    //                     layout.getCodeBtnNode.set('text', layout.countDownTime + '秒后可重新获取');
    //                 }
    //             };

    //             layout.showLoading = function () {
    //                 layout.loadingGif = new Element("div", { "sytle": "position:fixed;left:0;right:0;top:0;text-align: center;margin: 60px;" }).inject($(document.body));
    //                 new Element("img", { "src": "../x_desktop/img/loading2.gif", "style": "width: 96px; height: 96px;" }).inject(layout.loadingGif);
    //             };

    //             layout.hideLoading = function () {
    //                 if (layout.loadingGif) {
    //                     layout.loadingGif.destroy()
    //                     layout.loadingGif = null
    //                 }
    //             };

    //             // 通知
    //             layout.notice = function (content, type, target, where, offset) {
    //                 if (!where) where = { "x": "right", "y": "top" };
    //                 if (!target) target = this.content;
    //                 if (!type) type = "ok";
    //                 var noticeTarget = target || $(document.body);
    //                 var off = offset;
    //                 if (!off) {
    //                     off = {
    //                         x: 10,
    //                         y: where.y.toString().toLowerCase() == "bottom" ? 10 : 10
    //                     };
    //                 }
    //                 new mBox.Notice({
    //                     type: type,
    //                     position: where,
    //                     move: false,
    //                     target: noticeTarget,
    //                     delayClose: (type == "error") ? 10000 : 5000,
    //                     offset: off,
    //                     content: content
    //                 });
    //             };

    //             //异步载入必要模块
    //             layout.config = null;
    //             var configLoaded = false;
    //             var lpLoaded = false;
    //             var commonLoaded = false;
    //             var lp = o2.session.path + "/lp/" + o2.language + ".js";
    //             o2.load(lp, function () {
    //                 _loadProgressBar();
    //                 lpLoaded = true;
    //                 var modules = ["o2.xDesktop.$all"];
    //                 o2.require(modules, {
    //                     "onSuccess": function () {
    //                         commonLoaded = true;
    //                         if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { layout.load(); });
    //                     },
    //                     "onEvery": function () {
    //                         _loadProgressBar();
    //                     }
    //                 });
    //             });

    //             o2.getJSON("../x_desktop/res/config/config.json", function (config) {
    //                 _loadProgressBar();
    //                 if (config.proxyCenterEnable){
    //                     if (o2.typeOf(config.center)==="array"){
    //                         config.center.forEach(function(c){
    //                             c.port = window.location.port || 80;
    //                         });
    //                     }else{
    //                         config.port = window.location.port || 80;
    //                     }
    //                 }
    //                 layout.config = config;
    //                 o2.tokenName = config.tokenName || "x-token";
    //                 configLoaded = true
    //                 if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { layout.load(); });
    //             });

    //             var _getDistribute = function (callback) {
    //                 if (layout.config.app_protocol === "auto") {
    //                     layout.config.app_protocol = window.location.protocol;
    //                 }
    //                 o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
    //                     layout.serviceAddressList = service;
    //                     layout.centerServer = center;
    //                     _loadProgressBar();
    //                     if (callback) callback();
    //                 }.bind(this));
    //             };

    //             // MWF.getJSON("res/config/config.json", function (config) {
    //             //     layout.config = config;
    //             //     MWF.xDesktop.getServiceAddress(layout.config, function (service, center) {
    //             //         layout.serviceAddressList = service;
    //             //         layout.centerServer = center;
    //             //         layout.load();
    //             //     }.bind(this));
    //             // });

    //             // o2.getJSON("../x_desktop/res/config/config.json", function (config) {
    //             //     _loadProgressBar();
    //             //     layout.config = config;
    //             //     configLoaded = true
    //             //     if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { _load(); });
    //             // });

    //         })();

    //     });
    // });
});



(function (layout) {
    layout.readys = [];
    layout.addReady = function () {
        for (var i = 0; i < arguments.length; i++) {
            if (o2.typeOf(arguments[i]) === "function") layout.readys.push(arguments[i]);
        }
    };
    var _requireApp = function (appNames, callback, clazzName) {
        var appPath = appNames.split(".");
        var baseObject = o2.xApplication;
        appPath.each(function (path, i) {
            if (i < (appPath.length - 1)) {
                baseObject[path] = baseObject[path] || {};
            } else {
                baseObject[path] = baseObject[path] || { "options": Object.clone(o2.xApplication.Common.options) };
            }
            baseObject = baseObject[path];
        }.bind(this));
        if (!baseObject.options) baseObject.options = Object.clone(o2.xApplication.Common.options);

        var _lpLoaded = false;
        o2.xDesktop.requireApp(appNames, "lp." + o2.language, {
            "failure": function () {
                o2.xDesktop.requireApp(appNames, "lp.zh-cn", null, false);
            }.bind(this)
        }, false);

        o2.xDesktop.requireApp(appNames, clazzName, function () {
            if (callback) callback(baseObject);
        });
    };
    var _createNewApplication = function (e, appNamespace, appName, options, statusObj) {

        var app = new appNamespace["Main"](this, options);
        app.desktop = layout;
        app.inBrowser = true;
        app.status = statusObj;

        app.load(true);

        var appId = appName;
        if (options.appId) {
            appId = options.appId;
        } else {
            if (appNamespace.options.multitask) appId = appId + "-" + (new o2.widget.UUID());
        }
        app.appId = appId;
        layout.app = app;
        layout.desktop.currentApp = app;

        var mask = document.getElementById("appContentMask");
        if (mask) mask.destroy();
    };

    var _openWorkAndroid = function (options) {
        if (window.o2android && window.o2android.postMessage) {
            var body = {
                type: "openO2Work",
                data: {
                    title : options.title || ""
                }
            };
            if (options.workId) {
                body.data.workId = options.workId;
            } else if (options.workCompletedId) {
                body.data.workCompletedId = options.workCompletedId;
            }
            window.o2android.postMessage(JSON.stringify(body));
            return true;
        }
        if (window.o2android && window.o2android.openO2Work) {
            if (options.workId) {
                window.o2android.openO2Work(options.workId, "", options.title || "");
            } else if (options.workCompletedId) {
                window.o2android.openO2Work("", options.workCompletedId, options.title || "");
            }
            return true;
        }
        return false;
    };
    var _openWorkIOS = function (options) {
        if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Work) {
            if (options.workId) {
                window.webkit.messageHandlers.openO2Work.postMessage({
                    "work": options.workId,
                    "workCompleted": "",
                    "title": options.title || ""
                });
            } else if (options.workCompletedId) {
                window.webkit.messageHandlers.openO2Work.postMessage({
                    "work": "",
                    "workCompleted": options.workCompletedId,
                    "title": options.title || ""
                });
            }
            return true;
        }
        return false;
    };
    var _openWorkHTML = function (options) {
        var uri = new URI(window.location.href);
        var redirectlink = uri.getData("redirectlink");
        if (!redirectlink) {
            redirectlink = encodeURIComponent(locate.pathname + locate.search);
        } else {
            redirectlink = encodeURIComponent(redirectlink);
        }
        if (options.workId) {
            window.location = o2.filterUrl("../x_desktop/workmobilewithaction.html?workid=" + options.workId + ((layout.debugger) ? "&debugger" : "") + "&redirectlink=" + redirectlink);
        } else if (options.workCompletedId) {
            window.location = o2.filterUrl("../x_desktop/workmobilewithaction.html?workcompletedid=" + options.workCompletedId + ((layout.debugger) ? "&debugger" : "") + "&redirectlink=" + redirectlink);
        }else if (options.draft){
            window.location = o2.filterUrl("../x_desktop/workmobilewithaction.html?draft=" + JSON.stringify(options.draft) + ((layout.debugger) ? "&debugger" : "") + "&redirectlink=" + redirectlink);
        }else {
            console.log("open work 错误，缺少参数！");
        }
    };
    var _openWork = function (options) {
        if (!_openWorkAndroid(options)) if (!_openWorkIOS(options)) _openWorkHTML(options);
    };
    var _openDocument = function (appNames, options, statusObj) {
        var title = typeOf(options) === "object" ? (options.docTitle || options.title) : "";
        title = title || "";
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        if (window.o2android && window.o2android.postMessage) {
            var body = {
                type: "openO2CmsDocument",
                data: {
                    docId : options.documentId,
                    title: title,
                    options: options
                }
            };
            window.o2android.postMessage(JSON.stringify(body));
        } else if (window.o2android && window.o2android.openO2CmsDocumentV2) {
            window.o2android.openO2CmsDocumentV2(options.documentId, title, JSON.stringify(options));
        } else if (window.o2android && window.o2android.openO2CmsDocument) {
            window.o2android.openO2CmsDocument(options.documentId, title);
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsDocument) {
            window.webkit.messageHandlers.openO2CmsDocument.postMessage({ "docId": options.documentId, "docTitle": title, "options": JSON.stringify(options) });
        } else {
            window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
        }
    };
    var _openCms = function (appNames, options, statusObj) {
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        if (window.o2android && window.o2android.postMessage) {
            var body = {
                type: "openO2CmsApplication",
                data: {
                    appId : options.columnId,
                    title: options.title || ""
                }
            };
            window.o2android.postMessage(JSON.stringify(body));
        } else if (window.o2android && window.o2android.openO2CmsApplication) {
            window.o2android.openO2CmsApplication(options.columnId, options.title || "");
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsApplication) {
            window.webkit.messageHandlers.openO2CmsApplication.postMessage(options.columnId);
        } else {
            window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
        }
    };
    var _openMeeting = function (appNames, options, statusObj) {
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        if (window.o2android && window.o2android.postMessage) {
            var body = {
                type: "openO2Meeting",
                data: {}
            };
            window.o2android.postMessage(JSON.stringify(body));
        } else if (window.o2android && window.o2android.openO2Meeting) {
            window.o2android.openO2Meeting("");
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Meeting) {
            window.webkit.messageHandlers.openO2Meeting.postMessage("");
        } else {
            window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
        }
    };

    var _openCalendar = function (appNames, options, statusObj) {
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        if (window.o2android && window.o2android.postMessage) {
            var body = {
                type: "openO2Calendar",
                data: {}
            };
            window.o2android.postMessage(JSON.stringify(body));
        } else if (window.o2android && window.o2android.openO2Calendar) {
            window.o2android.openO2Calendar("");
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Calendar) {
            window.webkit.messageHandlers.openO2Calendar.postMessage("");
        } else {
            window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
        }
    };
    var _openTaskCenter = function (appNames, options, statusObj) {
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        var tab = ((options && options.navi) ? options.navi : "task").toLowerCase();
        if (tab === "done") tab = "taskCompleted";
        if (tab === "readed") tab = "readCompleted";

        if (window.o2android && window.o2android.postMessage) {
            var body = {
                type: "openO2WorkSpace",
                data: { type: tab }
            };
            window.o2android.postMessage(JSON.stringify(body));
        } else if (window.o2android && window.o2android.openO2WorkSpace) {
            window.o2android.openO2WorkSpace(tab);
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2WorkSpace) {
            window.webkit.messageHandlers.openO2WorkSpace.postMessage(tab);
        } else {
            window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
        }
    };

    var _openApplicationMobile = function (appNames, options, statusObj) {
        switch (appNames) {
            case "process.Work":
                _openWork(options);
                break;
            case "cms.Document":
                _openDocument(appNames, options, statusObj);
                break;
            case "cms.Module":
                _openCms(appNames, options, statusObj);
                break;
            case "Meeting":
                _openMeeting(appNames, options, statusObj);
                break;
            case "Calendar":
                _openCalendar(appNames, options, statusObj);
                break;
            case "process.TaskCenter":
                _openTaskCenter(appNames, options, statusObj);
                break;
            default:
                var optionsStr, statusStr;
                if( options || statusObj){
                    optionsStr = encodeURIComponent((options) ? JSON.encode(options) : "")
                    statusStr = encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "")
                }else{
                    var uri = new URI(window.location.href);
                    optionsStr = uri.getData("option");
                    statusStr = uri.getData("status");
                }
                window.location = o2.filterUrl("../x_desktop/appMobile.html?app=" + appNames + "&option=" + (optionsStr || "") + "&status=" + (statusStr || "") + ((layout.debugger) ? "&debugger" : ""));
        }
    };

    layout.openApplication = function (e, appNames, options, statusObj) {
        if (layout.app) {
            if (layout.mobile) {
                _openApplicationMobile(appNames, options, statusObj);
            } else {
                var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");


                if (layout.app.$openWithSelf) {
                    return window.location = o2.filterUrl("../x_desktop/app.html?" + par + ((layout.debugger) ? "&debugger" : ""));
                } else {
                    return window.open(o2.filterUrl("../x_desktop/app.html?" + par + ((layout.debugger) ? "&debugger" : "")), par);
                }
            }
        } else {
            var appPath = appNames.split(".");
            var appName = appPath[appPath.length - 1];

            _requireApp(appNames, function (appNamespace) {
                if (appNamespace.loading && appNamespace.loading.then){
                    appNamespace.loading.then(function(){
                        _createNewApplication(e, appNamespace, appName, options, statusObj);
                    });
                }else{
                    _createNewApplication(e, appNamespace, appName, options, statusObj);
                }

            }.bind(this));
        }
    };

    layout.refreshApp = function (app) {
        var status = app.recordStatus();

        var uri = new URI(window.location.href);
        var appNames = uri.getData("app");
        var optionsStr = uri.getData("option");
        var statusStr = uri.getData("status");
        if (status) statusStr = JSON.encode(status);

        var port = uri.get("port");
        window.location = uri.get("scheme") + "://" + uri.get("host") + ((port) ? ":" + port : "") + uri.get("directory ") + "?app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent(statusStr) + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "") + ((layout.debugger) ? "&debugger" : "");
    };

    layout.load = function (appNames, options, statusObj) {
        // layout.message = new o2.xDesktop.MessageMobile();
        // layout.message.load();

        layout.apps = [];
        layout.node = $("layout");
        var appName = appNames, m_status = statusObj, option = options;

        var topWindow = window.opener;
        if (topWindow) {
            try {
                if (!appName) appName = topWindow.layout.desktop.openBrowserApp;
                if (!m_status) m_status = topWindow.layout.desktop.openBrowserStatus;
                if (!option) option = topWindow.layout.desktop.openBrowserOption;
            } catch (e) { }
        }
        layout.openApplication(null, appName, option || {}, m_status);
    }

})(layout);