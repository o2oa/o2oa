
/**
 * 微信公众号单点登录
 * 目前这个页面有两个功能 一个是绑定账号 一个是单点登录
 */
layout = window.layout || {};
var locate = window.location;
layout.protocol = locate.protocol;
var href = locate.href;
if (href.indexOf("debugger") != -1) layout.debugger = true;
layout.desktop = layout;
layout.session = layout.session || {};


o2.xDesktop = o2.xDesktop || {};

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

    o2.load(["../o2_lib/mootools/plugin/mBox.Notice.js", "../o2_lib/mootools/plugin/mBox.Tooltip.js"], { "sequence": true }, function () {
        MWF.loadLP("zh-cn");
        MWF.require("MWF.xDesktop.Layout", function () {
            MWF.require("MWF.xDesktop.Authentication", null, false);

            (function () {

                layout.load = function () {
                    var uri = href.toURI();
                    var redirect = uri.getData("redirect"); //登录成功后跳转地址
                    var code = uri.getData("code"); //微信code
                    console.log("code：" + code)
                    var type = uri.getData("type"); // bind 是绑定
                    console.log("type：" + type)
                    if (type && type === "bind") {
                        layout._authForm();
                    } else { // code 登录
                        layout.showLoading();
                        o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.loginWithCode(code, function (json) {
                            console.log(json);
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
                            layout.notice('单点登录失败，请先绑定用户', 'error');
                        });
                    }

                };


                /**
                 * 认证表单
                 */
                layout._authForm = function () {
                    //加载资源
                    var css = "../x_desktop/template/_mpwxssoLogin.css";
                    var html = "../x_desktop/template/_mpwxssoLogin.html";
                    var myBody = $(document.body);
                    myBody.loadAll({ "css": [css], "html": [html] }, { "bind": layout, "module": layout }, function () {
                        // 密码登录模式
                        layout.loginPwdModeNode.addEvents({
                            "click": function (e) {
                                e.target.addClass("mode-active");
                                layout.loginCodeModeNode.removeClass("mode-active");
                                layout.loginCodeBoxNode.setStyles({ "display": "none" });
                                layout.loginInputPasswordNode.setStyles({ "display": "" });
                                layout.loginMode = "password";
                            }
                        });
                        // 验证码登录模式
                        layout.loginCodeModeNode.addEvents({
                            "click": function (e) {
                                e.target.addClass("mode-active");
                                layout.loginPwdModeNode.removeClass("mode-active");
                                layout.loginCodeBoxNode.setStyles({ "display": "" });
                                layout.loginInputPasswordNode.setStyles({ "display": "none" });
                                layout.loginMode = "code";
                            }
                        });
                        layout.getCodeBtnNode.addEvents({
                            "click": function (e) {
                                layout._getCodeForLogin();
                            }
                        });
                        //登录
                        layout.loginBtnNode.addEvents({
                            "click": function (e) {
                                var username = layout.loginInputUsernameNode.get('value');
                                if (!username || username === '') {
                                    layout.notice('请先输入用户名或手机号码！', "error")
                                    return
                                }
                                if (layout.loginMode && layout.loginMode === 'code') { // 验证码登录
                                    var code = layout.loginInputCodeNode.get('value');
                                    if (!code || code === '') {
                                        layout.notice('请先输入验证码！', "error")
                                        return
                                    }
                                    var data = {
                                        credential: username,
                                        codeAnswer: code
                                    }
                                    layout.showLoading();
                                    o2.Actions.load("x_organization_assemble_authentication").AuthenticationAction.codeLogin(data, function (json) {
                                        console.log('登录成功。。。。。。。');
                                        layout.session.user = json.data;
                                        //绑定用户
                                        layout._bindWeixin2User();
                                    }, function (err) {
                                        layout.hideLoading();
                                        console.log(err)
                                        layout.notice('登录失败！', "error")
                                    });
                                } else { //密码登录
                                    var password = layout.loginInputPasswordNode.get('value');
                                    if (!password || password === '') {
                                        layout.notice('请先输入密码！', "error")
                                        return
                                    }
                                    var data = {
                                        credential: username,
                                        password: password
                                    }
                                    layout.showLoading();
                                    o2.Actions.load("x_organization_assemble_authentication").AuthenticationAction.login(data, function (json) {
                                        layout.session.user = json.data;
                                        //删除登录窗口
                                        layout.loginBoxNode.destroy();
                                        //绑定用户
                                        layout._bindWeixin2User();
                                    }, function (err) {
                                        layout.hideLoading();
                                        console.log(err)
                                        layout.notice('登录失败！', "error")
                                    });
                                }

                            }
                        })
                    }.bind(layout));
                };
                //绑定微信openid到用户信息
                layout._bindWeixin2User = function () {
                    var uri = href.toURI();
                    var code = uri.getData("code"); //微信code
                    if (code) {
                        o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.bindWithCode(code, function (json) {
                            layout.hideLoading();
                            //绑定成功
                            var box = new Element("div", { "style": "text-align: center;" }).inject($(document.body));
                            new Element("h2", { "text": "绑定成功！" }).inject(box);
                        }, function (err) {
                            layout.hideLoading();
                            console.log(err)
                            layout.notice('绑定账号失败', 'error');
                        })
                    } else {
                        layout.hideLoading();
                        layout.notice('没有传入微信code无法绑定', 'error');
                    }
                };
                // 获取验证码
                layout._getCodeForLogin = function () {
                    var username = layout.loginInputUsernameNode.get('value');
                    if (!username || username === '') {
                        layout.notice('请先输入用户名或手机号码！', "error")
                        return
                    }
                    layout.countDownTime = 60;
                    // 修改按钮样式
                    layout.getCodeBtnNode.removeClass("code-btn");
                    layout.getCodeBtnNode.addClass("code-btn-sending");
                    layout.countDownTime--;
                    layout.getCodeBtnNode.set('text', layout.countDownTime + '秒后可重新获取');
                    layout.countDownFun = setInterval(() => {
                        layout._countDownSendCode();
                    }, 1000)
                    o2.Actions.load("x_organization_assemble_authentication").AuthenticationAction.code(username, function (json) {
                        layout.notice('验证码已经发送，请注意查收！', 'success');
                    }, function (err) {
                        console.log(err);
                        layout.notice('发送验证码失败！', 'error');
                    });

                };
                // countdown 定时器
                layout._countDownSendCode = function () {
                    if (layout.countDownTime === 0) {
                        layout.getCodeBtnNode.removeClass("code-btn-sending");
                        layout.getCodeBtnNode.addClass("code-btn");
                        layout.getCodeBtnNode.set('text', '获取验证码');
                        layout.countDownTime = 60
                        if (layout.countDownFun) {
                            clearInterval(layout.countDownFun)
                            layout.countDownFun = null
                        }
                    } else {
                        layout.countDownTime--
                        layout.getCodeBtnNode.set('text', layout.countDownTime + '秒后可重新获取');
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

                // 通知
                layout.notice = function (content, type, target, where, offset) {
                    if (!where) where = { "x": "right", "y": "top" };
                    if (!target) target = this.content;
                    if (!type) type = "ok";
                    var noticeTarget = target || $(document.body);
                    var off = offset;
                    if (!off) {
                        off = {
                            x: 10,
                            y: where.y.toString().toLowerCase() == "bottom" ? 10 : 10
                        };
                    }
                    new mBox.Notice({
                        type: type,
                        position: where,
                        move: false,
                        target: noticeTarget,
                        delayClose: (type == "error") ? 10000 : 5000,
                        offset: off,
                        content: content
                    });
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
                    o2.require(modules, {
                        "onSuccess": function () {
                            commonLoaded = true;
                            if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { layout.load(); });
                        },
                        "onEvery": function () {
                            _loadProgressBar();
                        }
                    });
                });

                o2.getJSON("../x_desktop/res/config/config.json", function (config) {
                    _loadProgressBar();
                    layout.config = config;
                    configLoaded = true
                    if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { layout.load(); });
                });

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

                // MWF.getJSON("res/config/config.json", function (config) {
                //     layout.config = config;
                //     MWF.xDesktop.getServiceAddress(layout.config, function (service, center) {
                //         layout.serviceAddressList = service;
                //         layout.centerServer = center;
                //         layout.load();
                //     }.bind(this));
                // });

                // o2.getJSON("../x_desktop/res/config/config.json", function (config) {
                //     _loadProgressBar();
                //     layout.config = config;
                //     configLoaded = true
                //     if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { _load(); });
                // });

            })();

        });
    });
});