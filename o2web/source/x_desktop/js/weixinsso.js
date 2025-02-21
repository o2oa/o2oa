layout = window.layout || {};
var locate = window.location;
layout.protocol = locate.protocol;
var href = locate.href;
if (href.indexOf("debugger") != -1) layout.debugger = true;
layout.desktop = layout;
layout.session = layout.session || {};
o2.addReady(function () {

        MWF.loadLP("zh-cn");

        MWF.require("MWF.xDesktop.Layout", function () {
            MWF.require("MWF.xDesktop.Authentication", null, false);
            MWF.require("MWF.xDesktop.Common", null, false);

            (function () {
                layout.load = function () {
                    var uri = href.toURI();
                    var mode = uri.getData("mode");
                    var code = uri.getData("code");
                    var redirect = uri.getData("redirect");
                    console.log("code = "+ code)
                    console.log("redirect = "+ redirect)
                    MWF.require("MWF.xDesktop.Actions.RestActions", function () {
                        var action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_authentication", "");
                        action.getActions = function (actionCallback) {
                            this.actions = { "sso": { "uri": "/jaxrs/qiyeweixin/code/{code}", "method": "GET" },
                                            "userdetail" : {"uri": "/jaxrs/qiyeweixin/update/person/detail/{code}", "method": "GET" } };
                            if (actionCallback) actionCallback();
                        };
                        if (mode && mode === "snsapi_privateinfo") { // 获取用户详细信息，更新到Person
                            console.log("获取用户详细信息，更新到Person")
                            action.invoke({
                                "name": "userdetail", "async": true, "parameter": { "code":  code}, "success":  function(json) {
                                // 完成认证 关闭页面
                                if (redirect) {
                                    history.replaceState(null, "page", redirect);
                                    redirect.toURI().go();
                                } else {
                                    console.error("没有引入企业微信js！！！");
                                    document.id("layout").set("html", "<div>更新成功，可以关闭页面了！</div>");
                                }
                            }.bind(this), "failure": function (xhr, text, error) {
                                    console.log("企业微信单点异常 11111")
                                    document.id("layout").set("html", "<div>企业微信单点异常！</div>")
                                }.bind(this)
                            });
                        } else { // 默认的单点登录
                            console.log("默认的单点登录")
                            action.invoke({
                                "name": "sso", "async": true, "parameter": { "code":  code}, "success": function (json) {
                                    console.log("认证成功")
                                    if (redirect) {
                                        console.log("---------------------准备跳转 redirect 地址")
                                        history.replaceState(null, "page", redirect);
                                        redirect.toURI().go();
                                    } else {
                                        history.replaceState(null, "page", "../x_desktop/appMobile.html?app=process.TaskCenter");
                                        "appMobile.html?app=process.TaskCenter".toURI().go();
                                    }
                                }.bind(this), "failure": function (xhr, text, error) {
                                    console.log("企业微信单点异常 222222")
                                    document.id("layout").set("html", "<div>企业微信单点异常！</div>")
                                }.bind(this)
                            });
                        }
                    });
                    
                };


                MWF.getJSON("res/config/config.json", function (config) {
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
                    MWF.xDesktop.getServiceAddress(layout.config, function (service, center) {
                        layout.serviceAddressList = service;
                        layout.centerServer = center;
                        layout.load();
                    }.bind(this));

                });

            })();

        });
});
