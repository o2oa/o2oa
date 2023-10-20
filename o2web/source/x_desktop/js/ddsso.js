layout = window.layout || {};
var locate = window.location;
layout.protocol = locate.protocol;
var href = locate.href;
if (href.indexOf("debugger") !== -1) layout.debugger = true;
layout.desktop = layout;
layout.session = layout.session || {};
layout.mobile = true;

o2.addReady(function () {
    o2.load(["../o2_lib/mootools/plugin/mBox.Notice.js", "../o2_lib/mootools/plugin/mBox.Tooltip.js", "../o2_core/o2/widget/Common.js"], { "sequence": true }, function () {
        //MWF.defaultPath = "../x_desktop"+MWF.defaultPath;
        MWF.loadLP("zh-cn");
        MWF.require("MWF.xDesktop.Layout", function () {
            MWF.require("MWF.xDesktop.Authentication", null, false);
            MWF.require("MWF.xDesktop.Common", null, false);
            

            (function () {
                layout.load = function () {
                    var uri = href.toURI();
                    var redirect = uri.getData("redirect");
                    var processId = uri.getData("processId");
                    var applicationId = uri.getData("appId");
                    MWF.require("MWF.xDesktop.Actions.RestActions", function () {
                        var action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_authentication", "");
                        action.getActions = function (actionCallback) {
                            this.actions = {
                                "info": { "uri": "/jaxrs/dingding/info", "method": "POST" },
                                "auth": { "uri": "/jaxrs/dingding/code/{code}" }
                            };
                            if (actionCallback) actionCallback();
                        };
                        action.invoke({
                            "name": "info", "async": true, "data": { "url": href }, "success": function (json) {
                                var _config = json.data;
                                dd.config({
                                    agentId: _config.agentid,
                                    corpId: _config.corpId,
                                    timeStamp: _config.timeStamp,
                                    nonceStr: _config.nonceStr,
                                    signature: _config.signature,
                                    type:0, //0表示H5微应用的jsapi。
                                    jsApiList: ['runtime.info']
                                });
                                dd.error(function (err) {
                                    console.error('钉钉脚本错误 error: ' + JSON.stringify(err));
                                });
                                dd.ready(function() {
                                    dd.runtime.permission.requestAuthCode({
                                        corpId: _config.corpId,
                                        onSuccess: function (info) {
                                            action.invoke({
                                                "name": "auth", "async": true, "parameter": { "code": info.code },
                                                "success": function (json) {
                                                    //console.debug(json);
                                                    layout.session.user = json.data;
                                                    // var ua = navigator.userAgent.toLowerCase();
                                                    // console.log(ua);
                                                    // console.log(dd.pc);
                                                    // console.log(dd.android);
                                                    // console.log(dd.ios);
                                                    // 如果有参数 开始启动流程
                                                    if (processId && applicationId) {
                                                        //console.debug('获取到了流程信息 processId:'+processId+', applicationId:'+applicationId);
                                                        o2.Actions.load("x_processplatform_assemble_surface").ProcessAction.getWithProcessWithApplication(processId, applicationId, function (json) {
                                                            //console.debug(json);
                                                            if (json.data) {
                                                                MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function () {
                                                                    //console.debug('启动流程');
                                                                    var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(json.data, layout.app, {
                                                                        "workData": {},
                                                                        "identity": null,
                                                                        "latest": false,
                                                                        "onStarted": function (data, title, processName) {
                                                                            //console.debug('启动流程成功。。。。');
                                                                            var currentTask = [];
                                                                            data.each(function (work) {
                                                                                if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
                                                                            }.bind(this));
                                                                            var uri = "../x_desktop/appMobile.html?app=process.TaskCenter";
                                                                            if (redirect) {
                                                                                uri = redirect;
                                                                            }
                                                                            history.replaceState(null, "startProcess", uri);
                                                                            if (currentTask.length == 1) {
                                                                                var options = {"workId": currentTask[0], "appId": "process.Work"+currentTask[0]};
                                                                                //console.debug(options);
                                                                                layout.mobile = true;
                                                                                layout.openApplication(null, "process.Work", options);
                                                                            } else {
                                                                                //console.error('没有task');
                                                                                uri.toURI().go();
                                                                            }
                                                                        }.bind(this)
                                                                    });
                                                                    starter.load();
                                                                }.bind(this));
                                                            }
                                                        }.bind(this));
                                                    } else {
                                                        if (redirect) {
                                                            var uri = redirect;
                                                            if (dd.pc) { // 判断是否是PC端，目前测试这个参数可用
                                                                uri = redirect.replace("workmobilewithaction.html", "work.html");
                                                                uri = uri.replace("cmsdocMobile.html", "cmsdoc.html");
                                                            }
                                                            history.replaceState(null, "page", uri);
                                                            uri.toURI().go();
                                                        } else {
                                                            var uri = "../x_desktop/appMobile.html?app=process.TaskCenter";
                                                            if (dd.pc) { // 判断是否是PC端，目前测试这个参数可用
                                                                uri = "../x_desktop/app.html?app=process.TaskCenter"
                                                            }
                                                            history.replaceState(null, "page", uri);
                                                            uri.toURI().go();
                                                        }
                                                    }

                                                }.bind(this), "failure": function (xhr, text, error) {
                                                    layout.notice('钉钉单点认证失败！', 'error');
                                                    history.replaceState(null, "page", "../x_desktop/appMobile.html?app=process.TaskCenter");
                                                    "appMobile.html?app=process.TaskCenter".toURI().go();
                                                }.bind(this)
                                            });
                                        }.bind(this),
                                        onFail: function (err) {
                                            layout.notice('钉钉认证失败', 'error');
                                            console.error(err);
                                        }
                                    });
                                });


                            }.bind(this), "failure": function (xhr, text, error) {
                                layout.notice('请求失败！', 'error');
                                console.error(xhr);
                                console.error(error);
                             }.bind(this)
                        });
                    });
                };

                layout.isAuthentication = function () {
                    layout.authentication = new MWF.xDesktop.Authentication({
                        "onLogin": layout.load.bind(layout)
                    });

                    var returnValue = true;
                    this.authentication.isAuthenticated(function (json) {
                        this.user = json.data;
                        layout.session.user = json.data;
                    }.bind(this), function () {
                        this.authentication.loadLogin(this.node);
                        returnValue = false;
                    }.bind(this));
                    return returnValue;
                };

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
                    o2.tokenName = config.tokenName || "x-token";
                    layout.content = $(document.body);
                    layout.app = layout;
                    MWF.xDesktop.getServiceAddress(layout.config, function (service, center) {
                        layout.serviceAddressList = service;
                        layout.centerServer = center;
                        layout.load();
                    }.bind(this));
                   
                });

            })();

        });
    });
});
