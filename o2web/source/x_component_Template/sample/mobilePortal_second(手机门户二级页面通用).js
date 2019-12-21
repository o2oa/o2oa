//手机二级门户通用JS
//
//queryLoad
//window.Current_Portal_Config = {
//    "name": "考勤管理",
//    "process_app": [  //流程应用id, 必填，可以是数组或者 字符串。
//        "d96ca69a-446d-4d12-929f-f985aa0a9c85", //请假应用id
//        "1d5f9c01-11e6-4e68-bb79-72d12f27ebee" //出差应用id
//    ],
//    "process_id" : [  //流程id，非必填，可以不存在或为空，如果不同门户对应不同流程id，在这里进行设置。需要注意的是，如果该值一旦存在，那么本门户下所有对应的流程id都需要列式
//        "362a2b16-5047-4aaa-b1c4-19bb46e7e448",  //请假流程id
//        "4fb993c2-eca7-45d8-a023-d34e9c811291" //出差流程id
//    ]
//}
//var container = this.target.node.getParent("#appContent");
//if( container ){
//    var scrollNode = container.getFirst();
//    scrollNode.setStyle("overflow","visible");
//    scrollNode.setStyle("height","100%");
//}
//this.target.node.setStyle("height",document.body.getSize().y);
//this.include({"application":"common","name":"common_secondly"});
//
//由于上面例子的 process_app 的 process_id 都已列式出，其实等同于
//window.Current_Portal_Config = {
//    "name": "考勤管理",
//    "process_app": [  //流程应用id, 必填，可以是数组或者 字符串。
//        "d96ca69a-446d-4d12-929f-f985aa0a9c85", //请假应用id
//        "1d5f9c01-11e6-4e68-bb79-72d12f27ebee" //出差应用id
//    ]
//}

//window.Current_Portal_Config = {
//    "name": "用车申请",
//    "process_app": "9bed0d41-a4e9-40bc-9e0e-21a33c03475c"
//}


//
//var container = this.target.node.getParent("#appContent");
//if( container ){
//    var scrollNode = container.getFirst();
//    scrollNode.setStyle("overflow","visible");
//    scrollNode.setStyle("height","100%");
//}
//this.target.node.setStyle("height",document.body.getSize().y);
//
//this.include({"application":"common","name":"common_secondly"});


//var uri = new URI(window.location.href);
//var appid = uri.getData("appid");
//var processId = uri.getData("processId");
//this.include("common_secondly", function () {
//    this.loadSelectProcessLayout(appid,processId);
//}.bind(this))

//var uri = new URI(window.location.href);
//var appid = uri.getData("appid");
//var processId = uri.getData("processId");
//this.include("common_secondly", function () {
//    this.loadSelectIdentityLayout(appid,processId);
//}.bind(this))

////我发起的
//if( Current_Portal_Config.process_id ){
//    var processId = Current_Portal_Config.process_id;
//    var list = typeOf( processId ) == "array" ? processId : [processId];
//    return {"processList":list,"creatorPersonList":[( layout.desktop.session.user || layout.user ).distinguishedName]}
//}else{
//    var appId = Current_Portal_Config.process_app;
//    var list = typeOf( appId ) == "array" ? appId : [appId];
//    return {"applicationList":list,"creatorPersonList":[( layout.desktop.session.user || layout.user ).distinguishedName]}
//
//}
//
////待办
//if( Current_Portal_Config.process_id ){
//    var processId = Current_Portal_Config.process_id;
//    var list = typeOf( processId ) == "array" ? processId : [processId];
//    return {"processList":list}
//}else{
//    var appId = Current_Portal_Config.process_app;
//    var list = typeOf( appId ) == "array" ? appId : [appId];
//    return {"applicationList":list}
//}


this.define("openWork", function (data) {
    var options = {
        "onQueryClose": function () {
            var source = this.page.get("Source_todo");
            source.reload();
        }.bind(this)

    };
    if (data.completed) {
        options.workCompletedId = data.workCompleted
    } else {
        options.workId = data.work
    }
    layout.desktop.openApplication(this.event, "process.Work", options);
    this.event.stopPropagation();
});


this.define("startWork", function () {
    //var alias = this.page.currentPageName;
    var applactionId = Current_Portal_Config["process_app"];
    var processId = Current_Portal_Config["process_id"];
    var starter = new MobileProcessStarter( this.page.app , this);
    starter.load( applactionId, processId );
});

this.define("loadSelectProcessLayout", function ( applactionId , processId ) {
    document.title = "选择流程";
    if( applactionId.indexOf(",") > 0 ){
        applactionId = applactionId.split(",");
    }
    if( processId && processId.indexOf(",") > 0  ){
        processId = processId.split(",");
    }
    var starter = new MobileProcessStarter( this.page.app, this );
    starter.loadSelectProcessLayout( applactionId, processId );
});

this.define("loadSelectIdentityLayout", function (applactionId, processId) {
    document.title = "选择身份";
    var starter = new MobileProcessStarter( this.page.app, this );
    starter.loadSelectIdentityLayout( applactionId, processId );
});




var MobileProcessStarter = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function ( app, environment, options) {
        this.setOptions(options);

        this.getLp();

        this.app = app;
        this.environment = environment;
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
        this.workAction = MWF.Actions.get("x_processplatform_assemble_surface");
        this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;
    },
    load: function ( applactionId , processId) {
        this.applactionId = applactionId;
        this.processId = processId;
        this.checkProcess(applactionId , processId);
    },
    checkProcess : function(applactionId, processId){
        this.getProcessByAppId(applactionId, function (processList) {
            if( processId ){
                processId = typeOf( processId ) == "string" ? [processId] : processId;
                this.processList = [];
                for( var i=0; i<processList.length; i++ ){
                    if( processId.contains( processList[i].id ) ){
                        this.processList.push( processList[i] );
                    }
                }
            }else{
                this.processList = processList;
            }
            if (this.processList.length == 0) {
                this.app.notice("您没有权限发起流程","error");
            } else if( this.processList.length == 1 ){
                this.process = this.processList[0];
                this.checkIdentity();
            } else {
                this.gotoSelectProcessPage();
            }
        }.bind(this));
    },
    checkIdentity : function(){
        this.orgAction.getPerson(function (json) {
            this.identityList = json.data.woIdentityList || [];
            if (this.identityList.length == 0) {
                this.app.notice("无法获取您的身份","error");
            }else if (this.identityList.length == 1) {
                this.identity = this.identityList[0];
                this.startImmedintely();
            } else{
                this.gotoSelectIdentityPage();
            }
        }.bind(this), null, this.userName)
    },
    gotoSelectProcessPage : function(){
        var option = {
            'portalId': '77ef21c3-14a2-4e0b-9449-4c9936aed40f',
            'pageId': '82a5192c-6244-4d52-a8c8-6497d33c6d7c'
        };
        var uri = new URI(window.location.href);
        var redirectlink = uri.getData("redirectlink");
        if( !redirectlink ){
            var locate = window.location;
            var redirectlink = encodeURIComponent(locate.pathname + locate.search);
        }else{
            redirectlink = encodeURIComponent(redirectlink);
        }
        var href = "/x_desktop/appMobile.html?app=portal.Portal&appid=" + this.applactionId;
        if( this.processId ){
            href = href + "&processId=" + this.processId
        }
        window.location = href + "&option=" + JSON.stringify(option) + "&redirectlink=" + redirectlink;
    },
    gotoSelectIdentityPage : function(){
        var option = {
            'portalId': '77ef21c3-14a2-4e0b-9449-4c9936aed40f',
            'pageId': 'f3644ba3-ecef-4863-ae61-002cf38468e1'
        };
        var uri = new URI(window.location.href);
        var redirectlink = uri.getData("redirectlink");
        if( !redirectlink ){
            var locate = window.location;
            var redirectlink = encodeURIComponent(locate.pathname + locate.search);
        }else{
            redirectlink = encodeURIComponent(redirectlink);
        }
        window.location = "/x_desktop/appMobile.html?app=portal.Portal&appid=" + this.process.application + "&processId=" + this.process.id + "&option=" + JSON.stringify(option) + "&redirectlink=" + redirectlink;
    },
    loadSelectProcessLayout : function( applactionId, processId ){
        this.applactionId = applactionId;
        this.getProcessByAppId(applactionId, function (processList) {
            if( processId ){
                processId = typeOf( processId ) == "string" ? [processId] : processId;
                this.processList = [];
                for( var i=0; i<processList.length; i++ ){
                    if( processId.contains( processList[i].id ) ){
                        this.processList.push( processList[i] );
                    }
                }
            }else{
                this.processList = processList;
            }

            this.getCss();
            this.createMarkNode();
            this.createAreaNode();
            this.createSelectProcessNode();

            this.areaNode.inject(this.markNode, "after");
            this.areaNode.fade("in");

            this.setStartNodeSize();

        }.bind(this));
    },
    loadSelectIdentityLayout : function(applactionId, processId){
        this.applactionId = applactionId;
        this.processId = processId;
        this.getProcessByAppId(applactionId, function (processList) {
            this.processList = processList;
            for( var i=0 ; i<processList.length; i++ ){
                var process = processList[i];
                if( process.id == processId ){
                    this.orgAction.getPerson(function (json) {
                        this.identityList = json.data.woIdentityList || [];
                        this.process = process;
                        this.getCss();
                        this.createMarkNode();
                        this.createAreaNode();
                        this.createSelectIdentityNode();

                        this.areaNode.inject(this.markNode, "after");
                        this.areaNode.fade("in");

                        this.setStartNodeSize();
                    }.bind(this), null, this.userName);
                    return;
                }
            }
        }.bind(this));
    },
    getProcessByAppId : function (applactionId, callback) {
        var list = applactionId;
        if( typeOf( list ) == "string"  )list = [list];
        var processList = [];
        for( var i=0 ; i<list.length; i++ ){
            var action = new this.environment.Action("x_processplatform_assemble_surface", {
                "lookup": {"uri": "/jaxrs/process/list/application/" + list[i], "method": "GET"}
            });
            action.invoke({
                "name": "lookup", "parameter": {}, "data": null, "success": function (json) {
                    processList = processList.concat( json.data );
                }.bind(this), async: false
            });
        }
        if (callback)callback(processList);

    },
    createMarkNode: function () {
        this.markNode = new Element("div#mark", {
            "styles": this.css.markNode,
            "events": {
                "mouseover": function (e) {
                    e.stopPropagation();
                },
                "mouseout": function (e) {
                    e.stopPropagation();
                }
            }
        }).inject(this.app.content);
    },
    createAreaNode: function () {
        this.areaNode = new Element("div#area", {
            "styles": this.css.areaNode
        });
    },
    createSelectProcessNode : function(){
        this.createNode = new Element("div", {
            "styles": this.css.createNode
        }).inject(this.areaNode);
        this.formNode = new Element("div", {
            "styles": this.css.formNode
        }).inject(this.createNode);

        var html = "<table width=\"100%\" height=\"90%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">" +
                // "<tr><td colSpan=\"2\" style=\"height: 60px; color: #333; line-height: 60px; text-align: center; font-size: 18px; background-color:#fff;\">" +
                // this.lp.selectStartIdentity+"</td></tr>" +
            "<tr><td colSpan=\"2\" id=\"form_startProcess\"></td></tr>" +
            "</table>";
        this.formNode.set("html", html);

        this.processArea = this.formNode.getElementById("form_startProcess");

        var _self = this;
        this.processList.each(function (item) {
            //if (item.woUnit){
            var p = new MobileProcessStarter.Process(this.processArea, item, this, this.css);
            p.node.store("process", p);
            p.node.addEvents({
                "click": function () {
                    var p = this.retrieve("process");
                    if (p) {
                        _self.process = p.data;
                        _self.checkIdentity();
                    }
                }
            });
        }.bind(this));
    },
    createSelectIdentityNode: function () {
        this.createNode = new Element("div", {
            "styles": this.css.createNode
        }).inject(this.areaNode);
        // this.createNewNode = new Element("div", {
        //     "styles": this.css.createNewNode
        // }).inject(this.createNode);

        // this.createCloseNode = new Element("div", {
        //     "styles": this.css.createCloseNode
        // }).inject(this.createNode);
        // this.createCloseNode.addEvent("click", function(e){
        //     this.cancelStartProcess(e);
        // }.bind(this));

        this.formNode = new Element("div", {
            "styles": this.css.formNode
        }).inject(this.createNode);

        var html = "<table width=\"100%\" height=\"90%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">" +
                // "<tr><td colSpan=\"2\" style=\"height: 60px; color: #333; line-height: 60px; text-align: center; font-size: 18px; background-color:#fff;\">" +
                // this.lp.selectStartIdentity+"</td></tr>" +
            "<tr><td colSpan=\"2\" id=\"form_startIdentity\"></td></tr>" +
            "</table>";
        this.formNode.set("html", html);

        this.identityArea = this.formNode.getElementById("form_startIdentity");

        var _self = this;
        this.identityList.each(function (item) {
            //if (item.woUnit){
            var id = new MobileProcessStarter.Identity(this.identityArea, item, this, this.css);
            id.node.store("identity", id);
            id.node.addEvents({
                // "mouseover": function(){
                //     this.setStyles(_self.css.identityNode_over);
                //     this.getFirst().getLast().setStyles(_self.css.identityInforNameTextNode_over);
                //     this.getFirst().getNext().getFirst().setStyles(_self.css.identityTitleNode_over);
                //     this.getFirst().getNext().getNext().getFirst().setStyles(_self.css.identityTitleNode_over);
                // },
                // "mouseout": function(){
                //     this.setStyles(_self.css.identityNode);
                //     this.getFirst().getLast().setStyles(_self.css.identityInforNameTextNode);
                //     this.getFirst().getNext().getFirst().setStyles(_self.css.identityTitleNode);
                //     this.getFirst().getNext().getNext().getFirst().setStyles(_self.css.identityTitleNode);
                // },
                "click": function () {
                    var identity = this.retrieve("identity");
                    if (identity) {
                        _self.okStartProcess(identity.data.distinguishedName);
                    }
                }
            });
            //}
        }.bind(this));

        //}.bind(this));

    },
    setStartNodeSize: function () {
        var size = this.app.content.getSize();
        var allSize = this.app.content.getSize();
        this.markNode.setStyles({
            "width": "" + allSize.x + "px",
            "height": "" + allSize.y + "px"
        });
        this.areaNode.setStyles({
            "width": "" + size.x + "px",
            "height": "" + size.y + "px"
        });
        var hY = size.y * 0.7;
        var mY = size.y * 0.3 / 2;
        // this.createNode.setStyles({
        //     "height": ""+hY+"px",
        //     "margin-top": ""+mY+"px"
        // });
        // var count = this.identityList.length;
        // if (count>2) count=2;
        // var w = count*294;
        // this.formNode.setStyles({
        //     "width": ""+w+"px"
        // });
        // w = w + 60;
        // this.createNode.setStyles({
        //     "width": ""+w+"px"
        // });
    },
    cancelStartProcess: function (e) {
        this.markNode.destroy();
        this.areaNode.destroy();
    },
    startImmedintely : function(){
        var data = {
            "title": this.process.name + "-" + "未命名",
            "identity": this.identity.distinguishedName
        };

        this.workAction.startWork(function (json) {

            this.afterStartProcess(json.data, data.title, this.process.name);

        }.bind(this), null, this.process.id, data);
    },
    okStartProcess: function (identity) {
        var data = {
            "title": this.process.name + "-" + this.lp.unnamed,
            //"identity": this.identityArea.get("value")
            "identity": identity
        };

        if (!data.identity) {
            this.departmentSelArea.setStyle("border-color", "red");
            this.app.notice(this.lp.selectStartId, "error");
        } else {
            this.mask = new MWF.widget.Mask({"style": "desktop"});
            this.mask.loadNode(this.areaNode);
            this.workAction.startWork(function (json) {
                this.mask.hide();

                this.markNode.destroy();
                this.areaNode.destroy();

                this.afterStartProcess(json.data, data.title, this.process.name);

                //this.fireEvent("started", [json.data, data.title, this.process.name]);

                // this.app.refreshAll();
                // this.app.notice(this.lp.processStarted, "success");
                //    this.app.processConfig();
            }.bind(this), null, this.process.id, data);
        }
    },
    afterStartProcess: function (data, title, processName) {
        // this.recordProcessData();
        var workInfors = [];
        var currentTask = [];

        data.each(function (work) {
            if (work.currentTaskIndex !== -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
            workInfors.push(this.getStartWorkInforObj(work));
        }.bind(this));

        if (currentTask.length === 1) {
            var options = {"workId": currentTask[0], "appId": currentTask[0]};
            layout.desktop.openApplication(null, "process.Work", options);
        }
    },
    getStartWorkInforObj: function (work) {
        var users = [];
        var currentTask = "";
        work.taskList.each(function (task, idx) {
            users.push(task.person + "(" + task.department + ")");
            if (work.currentTaskIndex === idx) currentTask = task.id;
        }.bind(this));
        return {"activity": work.fromActivityName, "users": users, "currentTask": currentTask};
    },
    recordProcessData: function () {
        MWF.UD.getDataJson("taskCenter_startTop", function (json) {
            if (!json || !json.length) json = [];
            var recordProcess = null;
            this.process.lastStartTime = new Date();
            var earlyProcessIdx = 0;
            var flag = true;
            for (var i = 0; i < json.length; i++) {
                var process = json[i];
                if (process.id === this.process.id) recordProcess = process;
                if (flag) {
                    if (!process.lastStartTime) {
                        earlyProcessIdx = i;
                        flag = false;
                    } else {
                        if (new Date(process.lastStartTime) < new Date(json[earlyProcessIdx].lastStartTime)) {
                            earlyProcessIdx = i;
                        }
                    }
                }
            }
            if (recordProcess) {
                recordProcess.lastStartTime = new Date();
                recordProcess.count = (recordProcess.count || 0) + 1;
                recordProcess.applicationName = this.applicationData.name;
            } else {
                if (json.length < 10) {
                    this.process.count = 1;
                    this.process.applicationName = this.applicationData.name;
                    json.push(this.process);
                } else {
                    json.splice(earlyProcessIdx, 1);
                    this.process.count = 1;
                    this.process.applicationName = this.applicationData.name;
                    json.push(this.process);
                }
            }
            MWF.UD.putData("taskCenter_startTop", json);
        }.bind(this));
    },
    getLp: function () {
        this.lp = {
            "name": "姓名",
            "unit": "部门",
            "start": "启动流程",
            "department": "部门",
            "company": "公司",
            "duty": "职务",
            "identity": "身份",
            "date": "时间",
            "subject": "文件标题",
            "process": "流程",

            "cancel": "取消",
            "ok": "确定",

            "startProcess_cancel_title": "取消启动流程确认",
            "startProcess_cancel": "您确定要取消启动流程吗？",
            "inputProcessSubject": "请输入文件标题",
            "selectStartId": "请选择启动部门，以确定启动者身份",

            "processStarted": "流程已启动",
            "unnamed": "无标题",
            "selectStartIdentity": "请选择您的身份"
        };
    },
    getCss: function () {
        this.css = {
            "markNode": {
                "opacity": 1,
                "position": "absolute",
                "background-color": "#f5f5f5",
                "top": "0px",
                "left": "0px"
            },
            "areaNode": {
                "position": "absolute",
                "opacity": 0,
                "top": "0px"
            },
            "createNode": {
                "background-color": "#f5f5f5",
                "width": "100%",
                //"max-width": "1000px",
                // "box-shadow": "0px 0px 20px #999",
                "overflow": "auto"
            },
            "createNewNode": {
                // "width": "47px",
                // "height": "47px",
                // "float": "left",
                // "background": "url("+"/x_component_process_TaskCenter/$ProcessStarter/default/new.png) no-repeat"
            },
            "createCloseNode": {
                "width": "47px",
                "height": "47px",
                "float": "right",
                "cursor": "pointer",
                "background": "url(" + "/x_component_process_TaskCenter/$ProcessStarter/default/close.png) center center no-repeat"
            },
            "formNode": {
                "border-radius": "8px",
                "border": "0px solid #666",
                "width": "100%",
                "margin": "0px auto",
                "font-size": "16px",
                "color": "#666",
                "overflow": "hidden",
                "font-family": "Microsoft YaHei"
            },
            "actionNode": {
                "width": "280px",
                "margin": "auto",
                "overflow": "hidden"
            },
            "startOkActionNode": {
                "height": "30px",
                "width": "85px",
                "cursor": "pointer",
                "float": "right",
                "line-height": "30px",
                "padding-left": "65px",
                "font-size": "16px",
                "font-family": "Microsoft YaHei",
                "border-radius": "3px",
                "border": "1px solid #354f67",
                "color": "#FFF",
                "margin-right": "20px",
                "margin-top": "20px",
                "margin-bottom": "20px",
                "box-shadow": "0px 0px 0px #666",
                "background": "url(" + "/x_component_process_TaskCenter/$ProcessStarter/default/editOk_bg.png) no-repeat"
            },
            "cancelActionNode": {
                "height": "30px",
                "width": "60px",
                "cursor": "pointer",
                "float": "right",
                "line-height": "30px",
                "padding-left": "40px",
                "font-size": "16px",
                "font-family": "Microsoft YaHei",
                "border-radius": "3px",
                "color": "#FFF",
                "margin-top": "20px",
                "margin-bottom": "20px",
                "box-shadow": "0px 0px 0px #666",
                "border": "1px solid #999",
                "background": "url(" + "/x_component_process_TaskCenter/$ProcessStarter/default/editCancel_bg.png) no-repeat"
            },
            "departSelNode": {
                "padding": "5px",
                "margin-right": "10px",
                "float": "left",
                "background-color": "#DDD",
                "border-radius": "3px",
                "color": "#000",
                "cursor": "pointer"
            },
            "departSelNode_over": {
                "background-color": "#fecfb7"
            },
            "departSelNode_out": {
                "background-color": "#EEE"
            },
            "departSelNode_selected": {
                "background-color": "#ea621f",
                "color": "#FFF"
            },

            "identityNode": {
                "overflow": "hidden",
                "width": "80%",
                //"height": "120px", 
                "border": "1px solid #eee",
                "border-radius": "8px",
                "background-color": "#FFF",
                "cursor": "pointer",
                "font-size": "14px",
                "padding": "10px",
                "margin": "20px auto",
                "box-shadow": "0px 0px 10px #ddd"
            },
            "identityNode_over": {
                "border": "1px solid #da7429"
                // "box-shadow": "0px 0px 20px #999",
            },

            "identityInforNameNode": {
                "height": "50px",
                "margin-bottom": "5px"
            },
            "identityInforPicNode": {
                "height": "50px",
                "width": "50px",
                "border-radius": "25px",
                "overflow": "hidden",
                "float": "left"
            },
            "identityInforNameTextNode": {
                "height": "40px",
                "line-height": "40px",
                "overflow": "hidden",
                "float": "left",
                "margin-left": "10px",
                "margin-right": "30px",
                "width": "150px",
                "color": "#666",
                "font-size": "14px",
                "font-weight": "bold",
                "text-align": "center"
            },
            "identityInforNameTextNode_over": {
                "color": "#da7429"
            },
            "identityDepartmentNode": {
                "height": "26px",
                "line-height": "26px",
                "overflow": "hidden"
            },
            "identityCompanyNode": {
                "height": "26px",
                "line-height": "26px",
                "overflow": "hidden"
            },
            "identityDutyNode": {
                "height": "22px",
                "line-height": "22px",
                "overflow": "hidden"
            },
            "identityTitleNode": {
                "color": "#666",
                "width": "50px",
                "padding-left": "10px",
                "float": "left"
            },
            "identityTitleNode_over": {
                "color": "#da7429"
            },

            "identityTextNode": {
                "color": "#333",
                "margin-left": "50px",
                "text-align": "left"
            },

            "processNode": {
                "overflow": "hidden",
                "width": "80%",
                //"height": "120px",
                "border": "1px solid #eee",
                "border-radius": "8px",
                "background-color": "#FFF",
                "cursor": "pointer",
                "font-size": "14px",
                "padding": "10px",
                "margin": "30px auto",
                "box-shadow": "0px 0px 10px #ddd"
            },
            "processItemNode": {
                "height": "50px",
                "line-height": "50px",
                "overflow": "hidden"
            },
            "processTitleNode": {
                "color": "#666",
                "width": "50px",
                "padding-left": "10px",
                "float": "left"
            },

            "processTextNode": {
                "color": "#333",
                "margin-left": "50px",
                "text-align": "left"
            }
        }
    }
});


MobileProcessStarter.Process = new Class({
    initialize: function (container, data, starter, style) {
        this.container = $(container);
        this.data = data;
        this.starter = starter;
        this.action = this.starter.orgAction;
        this.lp = starter.lp;
        this.style = style;
        //this.item = item;
        this.load();
    },
    load: function () {
        this.node = new Element("div", {
            "styles": this.style.processNode
        }).inject(this.container);

        var nameNode = new Element("div", {"styles": this.style.processItemNode}).inject(this.node);
        var nameTitleNode = new Element("div", {
            "styles": this.style.processTitleNode,
            "text": "流程"
        }).inject(nameNode);
        nameTitleNode.setStyle("font-size","16px");
        this.textNode = new Element("div", {
            "styles": this.style.processTextNode,
            "text": this.data.name
        }).inject(nameNode);
        this.textNode.setStyle("font-size","16px");
    }
});

MobileProcessStarter.Identity = new Class({
    initialize: function (container, data, starter, style) {
        this.container = $(container);
        this.data = data;
        this.starter = starter;
        this.action = this.starter.orgAction;
        this.lp = starter.lp;
        this.style = style;
        //this.item = item;
        this.load();
    },
    load: function () {
        this.node = new Element("div", {
            "styles": this.style.identityNode
        }).inject(this.container);

        // var nameNode = new Element("div", {
        //     "styles": this.style.identityInforNameNode
        // }).inject(this.node);

        // var url = this.action.getPersonIcon(this.starter.app.desktop.session.user.id);
        // var img = "<img width='50' height='50' border='0' src='"+url+"'></img>";

        // var picNode = new Element("div", {
        //     "styles": this.style.identityInforPicNode,
        //     "html": img
        // }).inject(nameNode);

        // var nameTextNode = new Element("div", {
        //     "styles": this.style.identityInforNameTextNode,
        //     "text": this.data.name
        // }).inject(nameNode);

        var nameNode = new Element("div", {"styles": this.style.identityDutyNode}).inject(this.node);
        var nameTitleNode = new Element("div", {
            "styles": this.style.identityTitleNode,
            "text": this.lp.name
        }).inject(nameNode);
        this.unitTextNode = new Element("div", {
            "styles": this.style.identityTextNode,
            "text": this.data.name
        }).inject(nameNode);

        var unitNode = new Element("div", {"styles": this.style.identityDepartmentNode}).inject(this.node);
        var unitTitleNode = new Element("div", {
            "styles": this.style.identityTitleNode,
            "text": this.lp.unit
        }).inject(unitNode);
        this.unitTextNode = new Element("div", {"styles": this.style.identityTextNode}).inject(unitNode);
        if (this.data.woUnit) this.unitTextNode.set({
            "text": this.data.woUnit.levelName,
            "title": this.data.woUnit.levelName
        });

        // var companyNode = new Element("div", {"styles": this.style.identityCompanyNode}).inject(this.node);
        // var companyTitleNode = new Element("div", {
        //     "styles": this.style.identityTitleNode,
        //     "text": this.item.explorer.app.lp.company
        // }).inject(companyNode);
        // this.companyTextNode = new Element("div", {"styles": this.style.identityTextNode}).inject(companyNode);

        var dutyNode = new Element("div", {"styles": this.style.identityDutyNode}).inject(this.node);
        var dutyTitleNode = new Element("div", {
            "styles": this.style.identityTitleNode,
            "text": this.lp.duty
        }).inject(dutyNode);
        this.dutyTextNode = new Element("div", {"styles": this.style.identityTextNode}).inject(dutyNode);
        var dutyTextList = [];
        var dutyTitleList = [];
        this.data.woUnitDutyList.each(function (duty) {
            dutyTextList.push(duty.name);
            if (duty.woUnit) dutyTitleList.push(duty.name + "(" + duty.woUnit.levelName + ")");
        }.bind(this));
        this.dutyTextNode.set({"text": dutyTextList.join(", "), "title": dutyTitleList.join(", ")});

    }
});