o2.xDesktop = o2.xDesktop || {};
o2.xd = o2.xDesktop;
o2.xDesktop.requireApp = function(module, clazz, callback, async){
    o2.requireApp(module, clazz, callback, async)
};
o2.xApplication = o2.xApplication || {};

MWF.xDesktop.loadConfig = function(callback){
    o2.JSON.get("res/config/config.json", function(config) {
        layout.config = config;
        if (layout.config.app_protocol === "auto") {
            layout.config.app_protocol = window.location.protocol;
        }
        layout.config.systemName = layout.config.systemName || layout.config.footer;
        layout.config.systemTitle = layout.config.systemTitle || layout.config.title;
        if (callback) callback();
    });
};
MWF.xDesktop.getService = function(callback) {
    MWF.xDesktop.getServiceAddress(layout.config, function(service, center){
        layout.serviceAddressList = service;
        layout.centerServer = center;
        if (callback) callback();
    });
};
MWF.xDesktop.loadService = function(callback){
    MWF.xDesktop.loadConfig(function(){
        MWF.xDesktop.getService(callback);
    });
};

MWF.xDesktop.checkLogin = function(loginFun){
    layout.authentication = new MWF.xDesktop.Authentication({
        "onLogin": loginFun
    });
    layout.authentication.isAuthenticated(function(json){
        layout.session.user = json.data;
        if (loginFun) loginFun();
    }.bind(this), function(){
        layout.authentication.loadLogin(this.node);
    });
};

// MWF.xDesktop.openApplication = function(e, appNames, options, obj, inBrowser){
//     if (appNames.substring(0, 4)==="@url"){
//         var url = appNames.replace(/\@url\:/i, "");
//         var a = new Element("a", {"href": url, "target": "_blank"});
//         a.click();
//         a.destroy();
//         a = null;
//         return true;
//     }
//     var appPath = appNames.split(".");
//     var appName = appPath[appPath.length-1];
//
//     MWF.xDesktop.requireApp(appNames, function(appNamespace){
//         if (appNamespace.options.multitask){
//             if (options && options.appId){
//                 if (this.apps[options.appId]){
//                     this.apps[options.appId].setCurrent();
//                 }else {
//                     this.createNewApplication(e, appNamespace, appName, options, obj, inBrowser);
//                 }
//             }else{
//                 this.createNewApplication(e, appNamespace, appName, options, obj, inBrowser);
//             }
//         }else{
//             if (this.apps[appName]){
//                 this.apps[appName].setCurrent();
//             }else{
//                 this.createNewApplication(e, appNamespace, appName, options, obj, inBrowser);
//             }
//         }
//     }.bind(this));
// },
// requireApp: function(appNames, callback, clazzName){
//     var appPath = appNames.split(".");
//     var appName = appPath[appPath.length-1];
//     var appObject = "o2.xApplication."+appNames;
//     var className = clazzName || "Main";
//     var appClass = appObject+"."+className;
//     var appLp = appObject+".lp."+o2.language;
//     var baseObject = o2.xApplication;
//
//     appPath.each(function(path, i){
//         if (i<(appPath.length-1)){
//             baseObject[path] = baseObject[path] || {};
//         }else {
//             baseObject[path] = baseObject[path] || {"options": Object.clone(o2.xApplication.Common.options)};
//         }
//         baseObject = baseObject[path];
//     }.bind(this));
//     if (!baseObject.options) baseObject.options = Object.clone(o2.xApplication.Common.options);
//
//     o2.requireApp(appNames, "lp."+o2.language, {
//         "onRequestFailure": function(){
//             o2.requireApp(appNames, "lp.zh-cn", null, false);
//         }.bind(this),
//         "onSuccess": function(){}.bind(this)
//     }, false);
//
//     o2.requireApp(appNames, clazzName, function(){
//         if (callback) callback(baseObject);
//     });
// },
// createNewApplication: function(e, appNamespace, appName, options, obj, inBrowser){
//     if (options){
//         options.event = e;
//     }else{
//         options = {"event": e};
//     }
//
//     var app = new appNamespace["Main"](this, options);
//     app.desktop = this;
//     if (obj){
//         Object.each(obj, function(value, key){
//             app[key] = value;
//         });
//     }
//     if (!inBrowser){
//         app.taskitem = new o2.xDesktop.Layout.Taskitem(app, this);
//     }else{
//         app.inBrowser = true;
//     }
//
//     app.load(true);
//
//     var appId = appName;
//     if (options.appId){
//         appId = options.appId;
//     }else{
//         if (appNamespace.options.multitask) appId = appId+"-"+(new o2.widget.UUID());
//     }
//     app.appId = appId;
//
//     this.apps[appId] = app;
//     return app;
// },


MWF.xDesktop.getDefaultLayout = function(callback){
    MWF.UD.getPublicData("defaultLayout", function(json) {
        if (json) layout.defaultLayout = json;
        if (callback) callback();
    }.bind(this));
},
MWF.xDesktop.getUserLayout = function(callback){
    MWF.UD.getPublicData("forceLayout", function(json) {
        var forceStatus = null;
        if (json) forceStatus = json;
        MWF.UD.getDataJson("layout", function(json) {
            if (json) {
                layout.userLayout = json;
                if (forceStatus) layout.userLayout.apps = Object.merge(layout.userLayout.apps, forceStatus.apps);
                if (callback) callback();
            }else{
                MWF.UD.getPublicData("defaultLayout", function(json) {
                    if (json){
                        layout.userLayout = json;
                        if (forceStatus) layout.userLayout.apps = Object.merge(layout.userLayout.apps, forceStatus.apps);
                    }
                    if (callback) callback();
                }.bind(this));
            }
        }.bind(this));
    }.bind(this));
},

MWF.xDesktop.notice = function(type, where, content, target, offset, option){
    var noticeTarget = target || layout.desktop.desktopNode;

    var off = offset;
    if (!off){
        off = {
            x: 10,
            y: where.y.toString().toLowerCase()=="bottom" ? 10 : 10
        };
    }

    var options = {
        type: type,
        position: where,
        move: false,
        target: noticeTarget,
        offset: off,
        content: content
    };
    if( option && typeOf(option) === "object" ){
        options = Object.merge( options, option );
    }
    new mBox.Notice(options);
};
MWF.xDesktop.loadPortal =  function(portalId){
    layout.openApplication(null, "portal.Portal", {
        "portalId": portalId,
        "onAfterModulesLoad": function(){
            var layoutNode = $("layout");
            if (layoutNode) layoutNode.setStyles({
                "position": "absolute",
                "width": "100%",
                "z-index": 100,
                "top": "0px",
                "left": "0px"
            }).fade("out");
            var appContentNode = $("appContent");
            if (appContentNode) appContentNode.setStyles({
                "position": "absolute",
                "width": "100%",
                "top": "0px",
                "opacity": 0,
                "left": "0px"
            }).fade("in");
        }
    }, null, true);
};
MWF.name = {
    "cns": function(names){
        if( typeOf(names) !== "array" )return [];
        var n = [];
        names.each(function(v){
            n.push(this.cn(v));
        }.bind(this));
        return n;
    },
    "cn": function(name){
        var idx = name.indexOf("@");
        return (idx!==-1) ? name.substring(0, idx) : name;
    },
    "ou": function(name){
        var idx = name.indexOf("@");
        var lastIdx = name.lastIndexOf("@");
        if (idx===-1){
            return name;
        }else if (lastIdx===idx){
            return "";
        }else{
            return name.substring(idx+1, lastIdx);
        }
    },
    "flag": function(name){
        var lastIdx = name.lastIndexOf("@");
        if (lastIdx===-1){
            return "";
        }else{
            return name.substring(lastIdx+1, name.length);
        }
    },
    "type": function(){
        var lastIdx = name.lastIndexOf("@");
        if (lastIdx===-1){
            return "";
        }else{
            return name.substring(lastIdx+1, name.length);
        }
    }
};
MWF.xDesktop.confirm = function(type, e, title, text, width, height, ok, cancel, callback, mask, style){
    MWF.require("MWF.xDesktop.Dialog", function(){
        var container = layout.desktop.node || $(document.body);
        var size = container.getSize();
        var x = 0;
        var y = 0;

        if (typeOf(e)=="element"){
            var position = e.getPosition(container);
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

            if (e.target){
                var position = e.target.getPosition(container);
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
            "container": MWF.xDesktop.node,
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
                dlg.content.setStyle("background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAjCAYAAAAe2bNZAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAABsVJREFUeNqkWFtsFGUU/nZn2r21IqX3llp6AQmkWDVGAgIlGI0EeMAHffAFa998MCQaE8JDxZCgSHzQKIm3qPHFGC7GW0xqkIgIKhhEwFJaKSDQUtplu73s7vidmX/q32F2uw2TnOzMv2fO+f5z/8fci7yvWAZYRXo4CCwLAM1cq+HvXRYwQrrM/7rTwB+TwC/dwKG3uU75mVxCO7T7wExgKHiBATzJ2411wMoy3pSQ5gg6UiFpgpQgDZNukK6TLgBHuf7lAPD5q8DfXMpQl5U3mA4P4ztAO3+2tADLCQSV+VsR/5L+If0G/EqgH78EvKtwT1lqr0en6SfoLaCe1niB7nj+CQIuV+uZWYApV8RNPPAVcP/rQMtF4I03gbNcpjdvt5KxQXs4SKKflxBI54PAs20EElNvZTQJucjLFyUtpZwioJVurFtMD/4MXBXWDUqnL5jHHYt0PgQ8da/4UFMwThpTz0HF7wfEj0/kSKwVAwsZU5U1wKkTwOBBj7GD08xE17QSSJPanVCKlCSNkM5s2mT/JtV6epZ8InclsH4R9TjYRKWPZQixnch2POJsZNpOb5HOb9yIi5s3I5XJIHb2rL2LoBZL+fBZKhOZaS3LgPgh4HcnYZ34scFI+goQxsj8iA+QHipItrejrKwMiaVLMZJIIEpFAaUkH76AFrEVfLxEzzEej/0FXFOGc8CQ8bmFTOE6DciEUnCBCsapoLGxETU1NYhGo7i+YAHiSlFauWMmvqAGKOzcVzDlh2mdo2o/loCJkeEVRnldSMsGUdCrKaiqqkJxcTEikQgKCgpsRbJzk4oukm8iB1+CfEUKkLtZub/CZOsFvht0Qi1lrAfW0WwvN3gyI7J1K+7ZswfNzc0oLS1FKBRCMBiEaZoIh8OOovp6jI6NYXLLFjQ1NdlAxCKGYaCwsBAlJSWoJ08lwQZTKaSPHJmSL9YZZWZx438eZ8yLMwwWtWeYaqvv9oBJ8UWDyovWrUMgEPi/ZPPeBWT/rlhhx0h1dbUNRABPpSrvBVhixw4kd+26rRyMOq3jCl31kzya0vSiKgW91/DOnbZJ53V22iAsy5pSIopra2vtNflP3KIDcTcwuH074pQT8JEvelkMF4kjpBuY0n1Dbjj7XDcpSCCU+gCKxWK+77hABghkOAsQuUIOivmq3xrSm2qMLJZxrwEKlGJQ5QGUC8gVBSSYQ67hoCidAiPzSCCHZSxVlXopeHhiAk30v8RBtivFQO3etg1Du3fbbihQKe0L3MmqmGrYwaAMRuPKMl6aVCkeJ11jRvSuWYO+vj4kk0lf4bIu/wuf8MfV+5NZ5I87RhhVuAKmTGhsbHPCWSwiwoYoOMQ60tDQgPLycjvNfWOA6/J/Op3GefJzsMLcAwfs6PSz0JhTXAfcBDNlVCS0xaYHSEql3jCBRLSC5k3faV1XZZnwySWABmUqJKCo8oUOaNTZbL9SlzE4Niwh8lURLf/TyoQzAZFgdcmvDklhjKsKXKAqsF5rZEztAboOAz+KA4xHmeo0+tNFqky7VMkKfJ+nAnuV2rtn1pS0td32n16B67kpRjZuqQrs6pB5mW37s5OswoLNaOTUdRfQRjPWGhrqOF80aYVSTwXWgfQQSL8URiqa6wGkV+B+ZuAlTwUWF/VxyPoUeD/uTH5x4xhjiNapoHXWhj3l+ubhw0hTkbtz3SXdBNJHIJgFn+Vx0Tlg37eOi+RAkTTk+MDueY1WWc64qQ5oZpSXhpSiedrOz1HBBVWZZ8Pn0phzcjj9DfBBvz1r4aYkrz3PvEhZq9lIyfgY3RXwzrY3lKKytWtxhgp6fHaaL5+AoU8stulPvgB+UFZJuPOMPaF/D5wgoGq6q9XMosianER3FiD58iWcDNr/GvCegwtDbjeywShAGQ5Y3aYzZC00PELsDkxFmOGokosv6cy/XV8DHyr3XFfL1rSBnL/WNqKUcw3rQWWhD6A7oaSTPV1dwEecX07CmX1v6W3Re4iz5IAl5xqCiTIMW0zJ5DsAkXKOLxbHy/1iEQ3IiHdYmAbGdZccsBhDXXKcoMAyWqjCynJwywVCqjgbz2kJVokR5RoXyKRkctYTpQ5Iepica+Q4QesMU0GUoCozPjGS0QZ5t9uzJ51ioO6T9FVZc1XFiLgm5X6ROJjvJ5EOZ4iXwaeIs2Elz1WreExtlVFRJjQZjGQekTFAuq80PRazbp6JTtOyxy87FX9EkYCY8H6v6fDMNzNdagayQYXVZ5mIei7UmrHrnQlFSZXJY9qnECuXIjMPMJZ2lHIPj6aaGg0FNOD5CJHWjtl5f0n5T4ABAFHaXG6UVjGNAAAAAElFTkSuQmCC)");
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
MWF.xDesktop.getImageSrc = function( id ){
    if (layout.config.app_protocol=="auto"){
        layout.config.app_protocol = window.location.protocol;
    }

    var addressObj = layout.serviceAddressList["x_file_assemble_control"];
    if (addressObj){
        var address = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context;
    }else{
        var host = layout.config.center.host || window.location.hostname;
        var port = layout.config.center.port;
        var address = layout.config.app_protocol+"//"+host+(port=="80" ? "" : ":"+port)+"/x_program_center";
    }
    var url = "/jaxrs/file/"+id+"/download/stream";
    return address+url;
};
MWF.xDesktop.setImageSrc = function(){
    if( !event )return;
    var obj = event.srcElement ? event.srcElement : event.target;
    if( !obj )return;
    obj.onerror = null;
    var id = obj.get("data-id");
    if( id )obj.set("src" , MWF.xDesktop.getImageSrc(id) );
};
MWF.xDesktop.uploadImage = function( reference, referencetype, formData, file, success, failure ){
    this.action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_file_assemble_control");
    this.action.invoke({
        "name": "uploadImage",
        "parameter": {"reference" : reference, "referencetype": referencetype},
        "data": formData,
        "file": file,
        "success": success,
        "failure": failure
    });
};
MWF.xDesktop.uploadImageByScale = function( reference, referencetype, scale, formData, file, success, failure ){
    this.action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_file_assemble_control");
    this.action.invoke({
        "name": "uploadImageByScale",
        "parameter": {"reference" : reference, "referencetype": referencetype, "scale" : scale || 0},
        "data": formData,
        "file": file,
        "success": success,
        "failure": failure
    });
};
MWF.xDesktop.copyImage = function( reference, referencetype, attachmentId, scale, success, failure ){
    this.action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_file_assemble_control");
    this.action.invoke({
        "name": "copyImage",
        "parameter": {"reference" : reference, "referencetype": referencetype, "attachmentId" : attachmentId, "scale":scale || 0 },
        "success": success,
        "failure": failure
    });
};
MWF.xDesktop.getPortalFileUr = function(id, app){
    var root = "x_portal_assemble_surface";
    var url = MWF.Actions.getHost(root)+"/"+root+MWF.Actions.get(root).action.actions.readFile.uri;
    url = url.replace("{flag}", id);
    url = url.replace("{applicationFlag}", app);
    return url
};
MWF.xDesktop.getProcessFileUr = function(id, app){
    var root = "x_processplatform_assemble_surface";
    var url = MWF.Actions.getHost(root)+"/"+root+MWF.Actions.get(root).action.actions.readFile.uri;
    url = url.replace("{flag}", id);
    url = url.replace("{applicationFlag}", app);
    return url
};
MWF.xDesktop.getCMSFileUr = function(id, app){
    var root = "x_cms_assemble_control";
    var url = MWF.Actions.getHost(root)+"/"+root+MWF.Actions.get(root).action.actions.readFile.uri;
    url = url.replace("{flag}", id);
    url = url.replace("{applicationFlag}", app);
    return url
};

MWF.xDesktop.getServiceAddress = function(config, callback){
    var error = function(){
        //MWF.xDesktop.notice("error", {"x": "right", "y": "top"}, "")
        var loadingNode = $("browser_loadding");
        var contentNode = $("appContent");
        ((loadingNode) ? loadingNode.getFirst() : contentNode).empty();
        var html= "<div style='width: 800px; color: #ffffff; margin: 30px auto'>" +
            "<div style='height: 40px;'>" +
            "   <div style='height: 40px; width: 40px; float: left; background: url(/x_desktop/img/error.png)'></div>" +
            "   <div style='margin-left: 50px; font-size: 20px; line-height: 40px;'>"+MWF.LP.desktop.notice.errorConnectCenter1+"</div>" +
            "</div><div style='margin-left: 0px;'>";
        if (typeOf(config.center)==="array"){
            config.center.each(function(center){
                var h = (center.host) ? center.host : window.location.hostname;
                var p = (center.port) ? ":"+center.port : "";
                var url = "http://"+h+p+"/x_program_center/jaxrs/echo";
                html+="<br><a style='margin-left: 50px; color: #e0e8d1; line-height: 30px;' href='"+url+"' target='_blank'>"+url+"</a>"
            });
        }else{
            var h = (config.center.host) ? config.center.host : window.location.hostname;
            var p = (config.center.port) ? ":"+config.center.port : "";
            var url = "http://"+h+p+"/x_program_center/jaxrs/echo";
            html+="<br><a style='margin-left: 50px; color: #e0e8d1; line-height: 30px;'href='"+url+"' target='_blank'>"+url+"</a>"
        }
        html+="</div><br><div style='margin-left: 50px; font-size: 20px'>"+MWF.LP.desktop.notice.errorConnectCenter2+"</div></div>";

        ((loadingNode) ? loadingNode.getFirst() : contentNode).set("html", html);
        if (!loadingNode && contentNode){
            contentNode.setStyle("background-color", "#666666");
        }
    };
    if (typeOf(config.center)==="object"){
        MWF.xDesktop.getServiceAddressConfigObject(config.center, callback, error);
    }else if (typeOf(config.center)==="array"){
        var center = null;
        //var center = MWF.xDesktop.chooseCenter(config);
        if (center){
            MWF.xDesktop.getServiceAddressConfigObject(center, callback, function(){
                MWF.xDesktop.getServiceAddressConfigArray(config, callback, error);
            }.bind(this));
        }else{
            MWF.xDesktop.getServiceAddressConfigArray(config, callback, error);
        }
    }
};
MWF.xDesktop.chooseCenter = function(config){
    var host = window.location.host;
    var center = null;
    for (var i=0; i<config.center.length; i++){
        var ct = config.center[i];
        if (!ct.host || (ct.host.toString().toLowerCase()===host.toString().toLowerCase())){
            center = ct;
            break;
        }
    }
    return center;
};
MWF.xDesktop.getServiceAddressConfigArray = function(config, callback, error) {
    var requests = [];
    config.center.each(function(center){
        requests.push(
            MWF.xDesktop.getServiceAddressConfigObject(center, function(serviceAddressList, center){
                requests.each(function(res){
                    if (res) if (res.isRunning()){res.cancel();}
                });
                if (callback) callback(serviceAddressList, center);
            }.bind(this), function(){
                if (requests.length){
                    for (var i=0; i<requests.length; i++){
                        if (requests[i].isRunning()) return "";
                    }
                }
                if (error) error();
            }.bind(this))
        );
    }.bind(this));
};
MWF.xDesktop.getServiceAddressConfigObject = function(center, callback, error){
    var centerConfig = center;
    if (!centerConfig) centerConfig = layout.config.center;
    var host = centerConfig.host || window.location.hostname;
    var port = centerConfig.port;
    var uri = "";

    if (layout.config.app_protocol=="auto"){
        layout.config.app_protocol = window.location.protocol;
    }

    if (!port || port=="80"){
        uri = layout.config.app_protocol+"//"+host+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
    }else{
        uri = layout.config.app_protocol+"//"+host+":"+port+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
    }
    var currenthost = window.location.hostname;
    uri = uri.replace(/{source}/g, currenthost);
    //var uri = "http://"+layout.config.center+"/x_program_center/jaxrs/distribute/assemble";

    try{
        return MWF.restful("get", uri, null, {
            "onSuccess": function(json){
                //this.serviceAddressList = json.data;
                //this.centerServer = center;
                if (callback) callback(json.data, center);
            }.bind(this),
            "onRequestFailure": function(xhr){
                if (error) error(xhr);
            }.bind(this),
            "onError": function(xhr){
                if (error) error(xhr);
            }.bind(this)
        });
    }catch(e){
        if (error) error();
        return null;
    }
};
MWF.xDesktop.$globalEvents = {};
MWF.xDesktop.addEvent = function(name, type, fn){
    if (!MWF.xDesktop.$globalEvents[name]) MWF.xDesktop.$globalEvents[name] = {};
    if (!MWF.xDesktop.$globalEvents[name][type]) MWF.xDesktop.$globalEvents[name][type] = [];
    MWF.xDesktop.$globalEvents[name][type].push(fn);
};

MWF.xDesktop.addEvents = function(name, o){
    if (!MWF.xDesktop.$globalEvents[name]) MWF.xDesktop.$globalEvents[name] = {};
    Object.each(o, function(fn, type){
        MWF.xDesktop.addEvent(name, type, fn);
    }.bind(this));
};

MWF.xDesktop.removeEvent = function(name, type, fn){
    if (!MWF.xDesktop.$globalEvents[name]) return true;
    if (!MWF.xDesktop.$globalEvents[name][type]) return true;
    MWF.xDesktop.$globalEvents[name][type].erase(fn);
};
MWF.xDesktop.removeEvents = function(name, type){
    if (!MWF.xDesktop.$globalEvents[name]) return true;
    if (!MWF.xDesktop.$globalEvents[name][type]) return true;
    MWF.xDesktop.$globalEvents[name][type] = [];
};

MWF.org = {
    parseOrgData: function(data, flat){
        if (data.distinguishedName){
            var flag = data.distinguishedName.substr(data.distinguishedName.length-2, 2);
            switch (flag.toLowerCase()){
                case "@i":
                    return this.parseIdentityData(data, flat);
                    break;
                case "@p":
                    return this.parsePersonData(data, flat);
                    break;
                case "@u":
                    return this.parseUnitData(data, flat);
                    break;
                case "@g":
                    return this.parseGroupData(data, flat);
                    break;
                case "@r":
                    return this.parseRoleData(data, flat);
                    break;
                case "@a":
                    return this.parseAttributeData(data, flat);
                    break;
                default:
                    return data;
            }
        }else{
            return data;
        }
    },
    parseIdentityData: function(data, flat){
        var rData = {
            "id": data.id,
            "name": data.name,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            // "dn": data.distinguishedName,
            "person": data.person,
            "unit": data.unit,
            "unitName": data.unitName,
            // "unitLevel": data.unitLevel,
            "unitLevelName": data.unitLevelName
        };
        if( data.ignoreEmpower )rData.ignoreEmpower = true;
        if( data.ignoredEmpower )rData.ignoredEmpower = true;

        if( !flat || !data.personDn || !data.personEmployee || !data.personUnique ){
            var woPerson = data.woPerson;
            if (!data.woPerson){
                //MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
                //this.action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_control");
                //var uri = "/jaxrs/person/{flag}";
                //uri = uri.replace("{flag}", data.person);

                //this.action.invoke({"uri": uri, "success": function(json){
                //    woPerson = json.data;
                //}.bind(this), "async":false});
                MWF.Actions.get("x_organization_assemble_control").getPerson(data.person, function(json){
                    woPerson = json.data
                }, null, false);
            }
            rData.personName = woPerson.name;
            rData.personEmployee = woPerson.employee;
            rData.personUnique = woPerson.unique;
            rData.personDn = woPerson.distinguishedName;

            if (!flat){
                rData.woPerson = {
                    "id": woPerson.id,
                    "genderType": woPerson.genderType,
                    "name": woPerson.name,
                    "employee": woPerson.employee,
                    "unique": woPerson.unique,
                    "distinguishedName": woPerson.distinguishedName,
                    "dn": woPerson.distinguishedName,
                    "mail": woPerson.mail,
                    "weixin": woPerson.weixin,
                    "qq": woPerson.qq,
                    "mobile": woPerson.mobile,
                    "officePhone": woPerson.officePhone
                };
            }
        }
        return rData;
    },
    parsePersonData: function(data){
        return {
            "id": data.id,
            "genderType": data.genderType,
            "name": data.name,
            "employee": data.employee,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            "dn": data.distinguishedName,
            "mail": data.mail,
            "weixin": data.weixin,
            "qq": data.qq,
            "mobile": data.mobile,
            "officePhone": data.officePhone
        }
    },
    parseUnitData: function(data){
        return {
            "id": data.id,
            "name": data.name,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            "dn": data.distinguishedName,
            "typeList":data.typeList,
            "shortName": data.shortName,
            "level": data.level,
            "levelName": data.levelName
        }
    },
    parseGroupData: function(data){
        return {
            "id": data.id,
            "name": data.name,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            "dn": data.distinguishedName
        }
    },
    parseRoleData: function(data){
        return {
            "id": data.id,
            "name": data.name,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            "dn": data.distinguishedName
        }
    },
    parseAttributeData: function(){
        return {
            "id": data.id,
            "description": data.description,
            "name": data.name,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            "dn": data.distinguishedName,
            "person": data.person,
            "attributeList": Array.clone(data.attributeList)
        }
    }
};
