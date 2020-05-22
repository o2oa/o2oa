

layout.addReady(function(){
    //重写 base_simple.js中的方法 这里是单点登录企业微信
    layout.openLogin = function () {
        console.log("开始login。。。。。。。。。。。。。");
        var uri = locate.href.toURI();
        var redirect = uri.getData("redirect");
        var processId = uri.getData("processId");
        var applicationId = uri.getData("appId");
        MWF.require("MWF.xDesktop.Actions.RestActions", function () {
            console.log("执行单点。。。。。。。。。。");
            var action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_authentication", "");
            action.getActions = function (actionCallback) {
                this.actions = { "sso": { "uri": "/jaxrs/qiyeweixin/code/{code}", "method": "GET" } };
                if (actionCallback) actionCallback();
            };
            action.invoke({
                "name": "sso", "async": true, "parameter": { "code": uri.getData("code") }, "success": function (json) {
                    console.log("单点成功。");
                    console.log(json);
                    //基础数据。。。。
                    layout.session.user = json.data;
                    layout.content = $(document.body);
                    layout.app = layout;

                    //开始启动
                    layout.startProcess(applicationId, processId, redirect);
                }.bind(this), "failure": function (xhr, text, error) {
                    var n = document.getElementById("loaddingArea");
                    if (n) { n.destroy(); }
                    document.id("layout").set("html", "<div>企业微信单点异常！</div>")
                }.bind(this)
            });
        });
    };

    layout.startProcess = function (appId, pId, redirect) {
        console.log("开始startProcess。。。。。。。。");
        console.log(appId);
        console.log(pId);
        MWF.Actions.get("x_processplatform_assemble_surface").getProcessByName(pId, appId, function (json) {

            if (json.data) {
                MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function () {
                    var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(json.data, layout.app, {
                        "workData": {},
                        "identity": null,
                        "latest": false,
                        "onStarted": function (data, title, processName) {
                            console.log("进入 onStarted。。。。");
                            debugger;
                            if (data.work){
                                layout.startProcessDraft(data, title, processName, redirect);
                            }else{
                                layout.startProcessInstance(data, title, processName, redirect);
                            }
                            
                        }.bind(this)

                    });
                    var mask = document.getElementById("loaddingArea");
                    if (mask) mask.destroy();
                    starter.load();
                }.bind(this));
            }
        }.bind(this));
    };

    layout.startProcessDraft = function(data, title, processName, redirect){
        console.log("草稿模式。。。。");
        console.log(data);
        o2.require("o2.widget.UUID", function () {
            var work = data.work;
            var options = {"draft": work, "appId": "process.Work"+(new o2.widget.UUID).toString(), "desktopReload": false};
            //先修改当前url为配置的门户地址
            if (redirect) {
                history.replaceState(null, "startProcess", redirect);

            } else {
                history.replaceState(null, "startProcess", "../x_desktop/appMobile.html?app=process.TaskCenter");
                
            }
            debugger;
            // layout.openApplication(null, "process.Work", options);
            layout.openWorkIn(options);
        });
        
    };
    layout.startProcessInstance = function(data, title, processName, redirect){
        console.log("实例模式。。。。");
        console.log(data);
        var currentTask = [];
        data.each(function (work) {
            if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
        }.bind(this));

        if (currentTask.length == 1) {
            var options = { "workId": currentTask[0], "appId": currentTask[0] };

            //先修改当前url为配置的门户地址
            if (redirect) {
                history.replaceState(null, "startProcess", redirect);

            } else {
                history.replaceState(null, "startProcess", "../x_desktop/appMobile.html?app=process.TaskCenter");
            }

            // layout.openApplication(null, "process.Work", options);
            layout.openWorkIn(options);

        } else { }
    };
    layout.openWorkIn =  function(options){
        o2.requireApp("Common", "", function() {
            var uri = new URI(window.location.href);
            var redirectlink = uri.getData("redirectlink");
            if (!redirectlink) {
                redirectlink = encodeURIComponent(locate.pathname + locate.search);
            } else {
                redirectlink = encodeURIComponent(redirectlink);
            }
            var appName="process.Work", m_status=null;
            options.redirectlink = redirectlink; 
            layout.app = null;//创建工作界面
            layout.openApplication(null, appName, options, m_status);
        }, true);
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

    (function(layout){
        console.log("开始执行。。。。。。。。");
        var uri = locate.href.toURI();
        var redirect = uri.getData("redirect");
        var processId = uri.getData("processId");
        var applicationId = uri.getData("appId");
        console.log(uri);
        layout.content = $(document.body);
        layout.app = layout;
        layout.startProcess(applicationId, processId, redirect);
     })(layout);
});





