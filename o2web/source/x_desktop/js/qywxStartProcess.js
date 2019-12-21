layout = window.layout || {};
var locate = window.location;
layout.protocol = locate.protocol;
var href = locate.href;
if (href.indexOf("debugger") != -1) layout.debugger = true;
layout.desktop = layout;
layout.session = layout.session || {};
o2.addReady(function () {
    o2.load(["../o2_lib/mootools/plugin/mBox.Notice.js", "../o2_lib/mootools/plugin/mBox.Tooltip.js"], { "sequence": true }, function () {
        MWF.loadLP("zh-cn");
        MWF.require("MWF.xDesktop.Layout", function () {
            MWF.require("MWF.xDesktop.Authentication", null, false);
            (function () {
                layout.load = function () {
                    var uri = href.toURI();
                    var redirect = uri.getData("redirect");
                    var processId = uri.getData("processId");
                    var applicationId = uri.getData("appId");

                    MWF.require("MWF.xDesktop.Actions.RestActions", function () {
                        var action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_authentication", "");
                        action.getActions = function (actionCallback) {
                            this.actions = { "sso": { "uri": "/jaxrs/qiyeweixin/code/{code}", "method": "GET" } };
                            if (actionCallback) actionCallback();
                        };
                        action.invoke({
                            "name": "sso", "async": true, "parameter": { "code": uri.getData("code") }, "success": function (json) {
                                //基础数据。。。。
                                layout.session.user = json.data;
                                layout.content = $(document.body);
                                layout.app = layout;

                                //开始启动
                                layout.startProcess(applicationId, processId, redirect);
                            }.bind(this), "failure": function (xhr, text, error) {
                                var mask = document.getElementById("loaddingArea");
                                if (mask) mask.destroy();
                                document.id("layout").set("html", "<div>企业微信单点异常！</div>")
                            }.bind(this)
                        });
                    });
                };

                layout.startProcess = function (appId, pId, redirect) {

                    MWF.Actions.get("x_processplatform_assemble_surface").getProcessByName(pId, appId, function (json) {

                        if (json.data) {
                            MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function () {
                                var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(json.data, layout.app, {
                                    "workData": {},
                                    "identity": null,
                                    "latest": false,
                                    "onStarted": function (data, title, processName) {

                                        debugger;
                                        var currentTask = [];
                                        data.each(function (work) {
                                            if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
                                        }.bind(this));

                                        if (currentTask.length == 1) {
                                            var options = { "workId": currentTask[0], "appId": currentTask[0] };
                                            //先修改当前url为配置的门户地址
                                            if (redirect) {
                                                history.replaceState(null, "startProcess", redirect);
                                                var workUrl = "workmobilewithaction.html?workid=" + options.workId + ((layout.debugger) ? "&debugger" : "") + "&redirectlink=" + redirect;
                                                workUrl.toURI().go();
                                            } else {
                                                history.replaceState(null, "startProcess", "/x_desktop/appMobile.html?app=process.TaskCenter");
                                                var workUrl = "workmobilewithaction.html?workid=" + options.workId + ((layout.debugger) ? "&debugger" : "") + "&redirectlink=appMobile.html%3Fapp%3Dprocess.TaskCenter";
                                                workUrl.toURI().go();
                                            }
                                        } else { }

                                        if (callback) callback(data);
                                    }.bind(this)
                                });
                                var mask = document.getElementById("loaddingArea");
                                if (mask) mask.destroy();
                                starter.load();
                            }.bind(this));
                        }
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
                    layout.config = config;
                    MWF.xDesktop.getServiceAddress(layout.config, function (service, center) {
                        layout.serviceAddressList = service;
                        layout.centerServer = center;
                        layout.load();
                    }.bind(this));
                });

                window.addEventListener('popstate', function (event) {
                    debugger
                    var ua = navigator.userAgent.toLowerCase()
                    var isiOS = /(iPhone|iPad|iPod|iOS)/i.test(ua)
                    if (isiOS) {
                        console.log("is IOs");
                        window.location.reload();
                    }
                }.bind(this));
            })();




        });
    });
});