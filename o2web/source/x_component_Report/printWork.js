layout = window.layout || {};
var locate = window.location;
layout.protocol = locate.protocol;
var href = locate.href;
if (href.indexOf("debugger")!=-1) layout.debugger = true;
layout.desktop = layout;
layout.session = layout.session || {};
COMMON.DOM.addReady(function(){
    COMMON.AjaxModule.load("/x_desktop/res/framework/mootools/plugin/mBox.Notice.js", null, false);
    COMMON.AjaxModule.load("/x_desktop/res/framework/mootools/plugin/mBox.Tooltip.js", null, false);

    COMMON.setContentPath("/x_desktop");
    COMMON.AjaxModule.load("mwf", function(){
        MWF.defaultPath = "/x_desktop"+MWF.defaultPath;
        MWF.loadLP("zh-cn");

        MWF.require("MWF.widget.Mask", null, false);
        layout.mask = new MWF.widget.Mask({"style": "desktop"});
        layout.mask.load();

        MWF.require("MWF.xDesktop.Layout", function(){
            MWF.require("MWF.xDesktop.Authentication", null, false);

            (function(){
                layout.load = function(){
                    if (this.isAuthentication()){
                        //var preview = window.frameElement.retrieve("preview");
                        //layout.desktop = window.frameElement.ownerDocument.window.layout.desktop;
                        //

                        this.node = $("layout");
                        this.content = $(document.body);
                        this.path = "/x_component_process_Work/$Main/";
                        this.cssPath = "/x_component_process_Work/$Main/default/css.wcss";
                        this._loadCss();

                        MWF.require("MWF.xDesktop.MessageMobile", function(){
                            layout.message = new MWF.xDesktop.MessageMobile();
                            layout.message.load();
                        }.bind(this));

                        //MWF.xDesktop.requireApp("process.Work", "Actions.RestActions", null, false);
                        this.action = MWF.Actions.get("x_processplatform_assemble_surface");
                        //new MWF.xApplication.process.Work.Actions.RestActions();
                        //MWF.xDesktop.requireApp("process.Work", "lp."+MWF.language, null, false);
                        MWF.xApplication.process = MWF.xApplication.process || {};
                        MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
                        MWF.xDesktop.requireApp("process.Work", "lp."+MWF.language, {
                            "onRequestFailure": function(){
                                MWF.xDesktop.requireApp("process.Work", "lp.zh-cn", null, false);
                            }.bind(this),
                            "onSuccess": function(){}.bind(this)
                        }, false);

                        //this.lp = MWF.xApplication.process.Work.LP;

                        this.options = this.getIds();
                        this.loadWork(this.options);

                        //MWF.require("MWF.xApplication.process.Xform.Form", function(){
                        //    layout.appForm = new MWF.APPForm($("layout"), preview.data);
                        //    layout.appForm.load();
                        //});
                    }
                };
                layout.close = function(){
                    $(document.body).addEvent("click", function(){window.close();});
                };
                layout._loadCss = function(){
                    var key = encodeURIComponent(this.cssPath);
                    if (MWF.widget.css[key]){
                        this.css = MWF.widget.css[key];
                    }else{
                        var r = new Request.JSON({
                            url: this.cssPath,
                            secure: false,
                            async: false,
                            method: "get",
                            noCache: false,
                            onSuccess: function(responseJSON, responseText){
                                this.css = responseJSON;
                                MWF.widget.css[key] = responseJSON;
                            }.bind(this),
                            onError: function(text, error){
                                alert(error + text);
                            }
                        });
                        r.send();
                    }
                };

                layout.getIds = function(){

                    var href = window.location.href;
                    var qStr = href.substr(href.indexOf("?")+1, href.length);
                    var qDatas = qStr.split("&");
                    var obj = {};
                    qDatas.each(function(d){
                        var q = d.split("=");
                        obj[q[0].toLowerCase()] = q[1];
                    });

                    return obj;
                };

                layout.loadWork = function(options){
                    var id = options.workid || options.workcompletedid;
                    var application = options.app;
                    var form = options.form;
                    var method = (options.workid) ? "getJobByWorkAssignForm" : "getJobByWorkCompletedAssignForm";
                    if (method && id){
                        this.action[method](function(json){
                            if (this.mask) this.mask.hide();
                            this.parseData(json.data);
                            this.openWork();
                        }.bind(this), function(){
                            this.errorWork();
                        }, id, form);
                    }

                    //if (options.workid){
                    //    var method = "getJobByWorkAssignForm";
                    //    if (method && id){
                    //        this.action[method](function(json){
                    //            if (this.mask) this.mask.hide();
                    //            this.parseData(json.data);
                    //            this.openWork();
                    //        }.bind(this), function(){
                    //            this.errorWork();
                    //        }, id, application, form);
                    //    }
                    //}else if (options.workcompletedid){
                    //    id = options.workcompletedid;
                    //    this.action.getJobByWorkCompleted(function(json){
                    //        this.action.getForm(form, function(formJson){
                    //            json.data.form = formJson.data;
                    //            if (this.mask) this.mask.hide();
                    //            this.parseData(json.data);
                    //            this.openWork();
                    //        }.bind(this));
                    //    }.bind(this), function(){
                    //        this.errorWork();
                    //    }, id);
                    //}

                };

                layout.errorWork = function(){
                    if (this.mask) this.mask.hide();
                    this.node.set("text", "openError");
                };
                layout.getCurrentTaskData = function(data){
                    if (data.currentTaskIndex && data.currentTaskIndex != -1){
                        this.options.taskid = this.taskList[data.currentTaskIndex].id;
                        return this.taskList[data.currentTaskIndex];
                    }
                    if (this.taskList){
                        if (this.taskList.length==1){
                            this.options.taskid = this.taskList[0].id;
                            return this.taskList[0];
                        }
                    }

                    return null;
                };
                layout.parseData = function(data){
                 //   this.setTitle(this.options.title+"-"+data.work.title);

                    this.activity = data.activity;
                    this.data = data.data;
                    this.taskList = data.taskList;
                    this.currentTask = this.getCurrentTaskData(data);
                    this.taskList = data.taskList;
                    this.work = data.work;
                    this.workCompleted = data.workCompleted;
                    this.workLogList = data.workLogList;
                    this.attachmentList = data.attachmentList;
                    this.inheritedAttachmentList = data.inheritedAttachmentList;
                    this.control = data.control;
                    this.form = JSON.decode(MWF.decodeJsonString(data.form.data));
                };
                layout.openWork = function(){
                    MWF.xDesktop.requireApp("process.Xform", "Form", function(){
                        this.options.readonly = true;
                        this.appForm = new MWF.APPForm(this.node, this.form, {"readonly": this.options.readonly});
                        this.appForm.businessData = {
                            "data": this.data,
                            "taskList": this.taskList,
                            "work": this.work,
                            "workCompleted": this.workCompleted,
                            "control": this.control,
                            "activity": this.activity,
                            "task": this.currentTask,
                            "workLogList": this.workLogList,
                            "attachmentList": this.attachmentList,
                            "inheritedAttachmentList": this.inheritedAttachmentList,
                            "status": {
                                "readonly": (this.options.readonly) ? true : false
                            }
                        };
                        this.appForm.workAction = this.action;
                        this.appForm.app = this;
                        this.appForm.load();
                    }.bind(this))
                };

                layout.isAuthentication = function(){
                    layout.authentication = new MWF.xDesktop.Authentication({
                        "onLogin": layout.load.bind(layout)
                    });

                    var returnValue = true;
                    this.authentication.isAuthenticated(function(json){
                        this.user = json.data;
                    }.bind(this), function(){
                        this.authentication.loadLogin(this.node);
                        returnValue = false;
                    }.bind(this));
                    return returnValue;
                };
                //layout.getServiceAddress = function(callback){
                //    var host = layout.config.center.host || window.location.hostname;
                //    var port = layout.config.center.port;
                //    var uri = "";
                //    if (!port || port=="80"){
                //        uri = "http://"+host+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
                //    }else{
                //        uri = "http://"+host+":"+port+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
                //    }
                //    var currenthost = window.location.hostname;
                //    uri = uri.replace(/{source}/g, currenthost);
                //    //var uri = "http://"+layout.config.center+"/x_program_center/jaxrs/distribute/assemble";
                //    MWF.restful("get", uri, null, function(json){
                //        this.serviceAddressList = json.data;
                //        if (callback) callback();
                //    }.bind(this));
                //};
                //layout.getServiceAddress = function(callback){
                //    if (typeOf(layout.config.center)=="object"){
                //        this.getServiceAddressConfigObject(callback);
                //    }else if (typeOf(layout.config.center)=="array"){
                //        this.getServiceAddressConfigArray(callback);
                //    }
                //
                //};
                //layout.getServiceAddressConfigArray = function(callback) {
                //    var requests = [];
                //    layout.config.center.each(function(center){
                //        requests.push(
                //            this.getServiceAddressConfigObject(function(){
                //                requests.each(function(res){
                //                    if (res.isRunning()){res.cancel();}
                //                });
                //                if (callback) callback();
                //            }.bind(this), center)
                //        );
                //    }.bind(this));
                //};
                //layout.getServiceAddressConfigObject = function(callback, center){
                //    var centerConfig = center;
                //    if (!centerConfig) centerConfig = layout.config.center;
                //    var host = centerConfig.host || window.location.hostname;
                //    var port = centerConfig.port;
                //    var uri = "";
                //    if (!port || port=="80"){
                //        uri = "http://"+host+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
                //    }else{
                //        uri = "http://"+host+":"+port+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
                //    }
                //    var currenthost = window.location.hostname;
                //    uri = uri.replace(/{source}/g, currenthost);
                //    //var uri = "http://"+layout.config.center+"/x_program_center/jaxrs/distribute/assemble";
                //    return MWF.restful("get", uri, null, function(json){
                //        this.serviceAddressList = json.data;
                //        this.centerServer = center;
                //        if (callback) callback();
                //    }.bind(this));
                //};
                layout.confirm = function(type, e, title, text, width, height, ok, cancel, callback, mask, style){
                    MWF.require("MWF.xDesktop.Dialog", function(){
                        var size = this.content.getSize();
                        var x = 0;
                        var y = 0;

                        if (typeOf(e)=="element"){
                            var position = e.getPosition(this.content);
                            x = position.x;
                            y = position.y;
                        }else{
                            if (Browser.name=="firefox"){
                                x = parseFloat(e.event.clientX);
                                y = parseFloat(e.event.clientY);
                            }else{
                                x = parseFloat(e.event.x);
                                y = parseFloat(e.event.y);
                            }


                            //    if (!x || !y){
                            if (e.target){
                                var position = e.target.getPosition(this.content);
                                x = position.x;
                                y = position.y;
                            }
                            //    }
                        }

                        if (x+parseFloat(width)>size.x){
                            x = x-parseFloat(width);
                        }
                        if (x<0) x = 0;
                        if (y+parseFloat(height)>size.y){
                            y = y-parseFloat(height);
                        }
                        if (y<0) y = 0;

                        var ctext = "";
                        var chtml = "";
                        if (typeOf(text).toLowerCase()=="object"){
                            ctext = text.text;
                            chtml = text.html;
                        }else{
                            ctext = text;
                        }
                        var dlg = new MWF.xDesktop.Dialog({
                            "title": title,
                            "style": style || "flat",
                            "top": y,
                            "left": x-20,
                            "fromTop":y,
                            "fromLeft": x-20,
                            "width": width,
                            "height": height,
                            "text": ctext,
                            "html": chtml,
                            "container": this.content,
                            "maskNode": mask,
                            "buttonList": [
                                {
                                    "text": MWF.LP.process.button.ok,
                                    "action": ok
                                },
                                {
                                    "text": MWF.LP.process.button.cancel,
                                    "action": cancel
                                }
                            ]
                        });

                        switch (type.toLowerCase()){
                            case "success":
                                dlg.content.setStyle("background-image", "url(data:image/png;base64,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)");
                                break;
                            case "error":
                                dlg.content.setStyle("background-image", "url(data:image/png;base64,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)");
                                break;
                            case "info":
                                dlg.content.setStyle("background-image", "url(data:image/png;base64,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)");
                                break;
                            case "warn":
                                dlg.content.setStyle("background-image", "url(data:image/png;base64,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)");
                                break;
                            default:
                            //dlg.content.setStyle("background-image", "");
                        }
                        dlg.show();
                        if (callback) callback(dlg);
                    }.bind(this));
                };
                layout.notice = function(content, type, target, where, offset){
                    if (!where) where = {"x": "right", "y": "top"};
                    if (!target) target = this.content;
                    if (!type) type = "ok";
                    var noticeTarget = target || $(document.body);
                    var off = offset;
                    if (!off){
                        off = {
                            x: 10,
                            y: where.y.toString().toLowerCase()=="bottom" ? 10 : 10
                        };
                    }

                    new mBox.Notice({
                        type: type,
                        position: where,
                        move: false,
                        target: noticeTarget,
                        delayClose: (type=="error") ? 10000 : 5000,
                        offset: off,
                        content: content
                    });
                };

                layout.addEvent = function(){};

                MWF.getJSON("/x_desktop/res/config/config.json", function(config){
                    layout.config = config;


                    MWF.xDesktop.getServiceAddress(layout.config, function(service, center){
                        layout.serviceAddressList = service;
                        layout.centerServer = center;
                        layout.load();
                    }.bind(this));
                    //layout.getServiceAddress(function(){
                    //    layout.load();
                    //});
                });

            })();

        });
    });
});