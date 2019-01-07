MWF.xDesktop.Actions = MWF.xDesktop.Actions || {};
MWF.xDesktop.Actions.RestActions = new Class({
    Implements: [Events],
	initialize: function(actionPath, serviceName, root){
		this.actionPath = actionPath;
		this.serviceName = serviceName;
        this.root = root;
		this.getAddress();
	},
	
	listApplicationAddress: function(success, failure){
		var url = this.actions.listAddress;
		url = this.actions.slotHost+url;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	
	getAddress: function(success, failure){
//		var name = "x_processplatform_core_designer";
//		var url = this.actions.getAddress.replace(/{id}/g, name);
//		url = this.actions.slotHost+url;
//		var callback = new MWF.process.RestActions.Callback(success, failure, function(data){
//			this.designAddress = data.data.url;
//		}.bind(this));
		
//		MWF.getJSON(url, callback);
		
		//this.address = "http://xa02.zoneland.net:8080/"+this.serviceName;
        var addressObj = layout.desktop.serviceAddressList[this.serviceName];

        if (addressObj){
            this.address = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context;
        }else{
            var host = layout.desktop.centerServer.host || window.location.hostname;
            var port = layout.desktop.centerServer.port;
            this.address = layout.config.app_protocol+"//"+host+(port=="80" ? "" : ":"+port)+"/x_program_center";
        }

        //this.address = "http://hbxa01.bf.ctc.com/"+this.serviceName;
		//this.designAddress = "http://localhost:9080/x_processplatform_front_designer";
		if (success) success.apply();
        return this.address;
	},
	getActions: function(callback){

        if (!this.actions){
            var url = (this.root) ? "/"+this.root+this.actionPath : MWF.defaultPath+this.actionPath;
            MWF.getJSON(url, function(json){
                this.actions = json;
                if (callback) callback();
            }.bind(this), false, false, false);
        }else{
            if (callback) callback();
        }
    },
    invokeUri: function(option){
        var uri = this.address+option.uri;
        var async = (option.async===false) ? false : true;
        var method = option.method || "GET";
        var callback = new MWF.xDesktop.Actions.RestActions.Callback(option.success, option.failure);

        var data = (option.data) ? JSON.encode(option.data) : "";
        var credentials = true;
        if (option.withCredentials===false){
            credentials = false;
        }
        MWF.restful(method, uri, data, callback, async, credentials);
    },
	invoke: function(option){
        var res = null;
        this.getActions(function(){
            //name, parameter, data, async, success, failure, withCredentials, urlEncode
            var action = this.actions[option.name];
            var method = action.method || "GET";
            var uri = action.uri;
            var progress = action.progress;
            if (option.parameter){
                Object.each(option.parameter, function(value, key){
                    var reg = new RegExp("{"+key+"}", "g");
                    if (option.urlEncode===false){
                        uri = uri.replace(reg, value);
                    }else{
                        uri = uri.replace(reg, encodeURI(value));
                    }
                });
            }
            uri = this.address+uri;

            var async = (option.async===false) ? false : true;

            var callback = new MWF.xDesktop.Actions.RestActions.Callback(option.success, option.failure);
            if (action.enctype && (action.enctype.toLowerCase()=="formdata")){
                res = this.invokeFormData(method, uri, option.data, option.file, callback, async, progress);
            }else{
                var data = (option.data) ? JSON.encode(option.data) : "";
                var credentials = true;
                if (option.withCredentials===false){
                    credentials = false;
                }
                res = MWF.restful(method, uri, data, callback, async, credentials);
            }
        }.bind(this));
        return res
	},
	formDataUpdateProgress: function(){
		
	},

    invokeFormDataWithProgress: function(xhr, method, uri, data, file, callback, async, progress){
        var messageItem = null;
        var currentDate = new Date();

        xhr.upload.addEventListener("progress", function(e){this.updateProgress(e, xhr, messageItem, currentDate);}.bind(this), false);
        xhr.upload.addEventListener("load", function(e){this.transferComplete(e, xhr, messageItem, currentDate, file);}.bind(this), false);
        xhr.upload.addEventListener("loadstart", function(e){this.transferStart(e, xhr, messageItem);}.bind(this), false);
        xhr.upload.addEventListener("error", function(e){this.transferFailed(e, xhr, messageItem);}.bind(this), false);
        xhr.upload.addEventListener("abort", function(e){this.transferCanceled(e, xhr, messageItem);}.bind(this), false);
        xhr.upload.addEventListener("timeout", function(e){this.transferCanceled(e, xhr, messageItem);}.bind(this), false);

        xhr.addEventListener("readystatechange", function(e){this.xhrStateChange(e, xhr, messageItem, callback);}.bind(this), false);

        xhr.open(method, uri, async!==false);
        xhr.withCredentials = true;

        if (file.name) messageItem = this.addFormDataMessage(file, false, xhr, progress);
        xhr.send(data);
    },
    setMessageText: function(messageItem, text){
        if (messageItem){
            var progressNode = messageItem.contentNode.getFirst("div").getFirst("div");
            var progressPercentNode = progressNode.getFirst("div");
            var progressInforNode = messageItem.contentNode.getFirst("div").getLast("div");
            progressInforNode.set("text", text);
            messageItem.dateNode.set("text", (new Date()).format("db"));
        }

    },
    setMessageTitle: function(messageItem, text){
        if (messageItem) messageItem.subjectNode.set("text", text);
    },
    clearMessageProgress: function(messageItem){
        if (messageItem) {
            var progressNode = messageItem.contentNode.getFirst("div").getFirst("div");
            progressNode.destroy();
        }
    },


    transferStart: function(e, xhr, messageItem){
        if (messageItem) {
            this.setMessageText(messageItem, MWF.LP.desktop.action.sendStart);
            messageItem.status = "progress";
        }
        this.fireEvent("loadstart");
    },
    transferFailed: function(e, xhr, messageItem){
        if (messageItem) {
            this.setMessageText(messageItem, MWF.LP.desktop.action.sendError);
            this.setMessageTitle(messageItem, MWF.LP.desktop.action.sendError);
            this.clearMessageProgress(messageItem);
            messageItem.status = "failed";
        }
        this.fireEvent("error");
    },
    transferCanceled: function(e, xhr, messageItem){
        if (messageItem) {
            this.setMessageText(messageItem, MWF.LP.desktop.action.sendAbort);
            this.setMessageTitle(messageItem, MWF.LP.desktop.action.sendAbort);
            this.clearMessageProgress(messageItem);
            messageItem.status = "cancel";
        }
        this.fireEvent("abort");
    },
    transferComplete: function(e, xhr, messageItem, currentDate, file){

        var sendDate = new Date();
        var ms = sendDate.getTime()-currentDate.getTime();
        var speed = (file.size)/ms;
        var u = "K/S";
        if (speed>1024){
            u = "M/S";
            speed = speed/1024;
        }
        if (speed>1024){
            u = "G/S";
            speed = speed/1024;
        }
        speed = speed.round(2);

        var timeStr = "";
        if (ms>3600000){
            var h = ms/3600000;
            var m_s = ms % 3600000;
            var m = m_s / 60000;
            var s_s = m_s % 60000;
            var s = s_s/1000;
            timeStr = ""+h.toInt()+MWF.LP.desktop.action.hour+m.toInt()+MWF.LP.desktop.action.minute+s.toInt()+MWF.LP.desktop.action.second;
        }else if (ms>60000){
            var m = ms / 60000;
            var s_s = ms % 60000;
            var s = s_s/1000;
            timeStr = ""+m.toInt()+MWF.LP.desktop.action.minute+s.toInt()+MWF.LP.desktop.action.second;
        }else{
            var s = ms/1000;
            timeStr = ""+s.toInt()+MWF.LP.desktop.action.second;
        }
        if (messageItem) {
            this.setMessageText(messageItem, MWF.LP.desktop.action.uploadComplete + "  " + MWF.LP.desktop.action.speed + ": " + speed + u + "  " + MWF.LP.desktop.action.time + ": " + timeStr, MWF.LP.desktop.action.uploadComplete);
            this.setMessageTitle(messageItem, MWF.LP.desktop.action.uploadComplete);
            this.clearMessageProgress(messageItem);

            messageItem.status = "completed";
        }
        //var msg = {
        //    "subject": MWF.LP.desktop.action.uploadComplete,
        //    "content": MWF.LP.desktop.action.uploadComplete+" : "+file.name
        //};
        //layout.desktop.message.addTooltip(msg);
        this.fireEvent("load");
    },
    updateProgress: function(e, xhr, messageItem, currentDate){
        var percent = 100*(e.loaded/e.total);

        var sendDate = new Date();
        var ms = sendDate.getTime()-currentDate.getTime();
        var speed = (e.loaded)/ms;
        var u = "K/S";
        if (speed>1024){
            u = "M/S";
            speed = speed/1024;
        }
        if (speed>1024){
            u = "G/S";
            speed = speed/1024;
        }
        speed = speed.round(2);

        if (messageItem) {
            if (messageItem.contentNode) {
                var progressNode = messageItem.contentNode.getFirst("div").getFirst("div");
                var progressPercentNode = progressNode.getFirst("div");
                var progressInforNode = messageItem.contentNode.getFirst("div").getLast("div");
                progressPercentNode.setStyle("width", "" + percent + "%");
                progressInforNode.set("text", MWF.LP.desktop.action.sendStart + ": " + speed + u);
            }
        }
        this.fireEvent("progress");
    },
    xhrStateChange: function(e, xhr, messageItem, callback){
        if (xhr.readyState != 4) return;

        var status = xhr.status;
        status = (status == 1223) ? 204 : status;

        if ((status >= 200 && status < 300)){
            var json = JSON.decode(xhr.responseText);
            if (json){
                switch(json.type) {
                    case "success":
                        var dataId = "";
                        var t = typeOf(json.data);
                        if (t=="array"){
                            dataId = json.data[0].id;
                        }
                        if (t=="object"){
                            dataId = json.data.id;
                        }

                        MWF.runCallback(callback, "success", [{
                            "type": "success",
                            "id": dataId,
                            "data": json.data
                        }, xhr.responseText]);
                        break;
                    case "warn":
                        MWF.xDesktop.notice("info", {x: "right", y:"top"}, json.errorMessage.join("\n"));

                        var dataId = "";
                        var t = typeOf(json.data);
                        if (t=="array"){
                            dataId = json.data[0].id;
                        }
                        if (t=="object"){
                            dataId = json.data.id;
                        }
                        MWF.runCallback(callback, "success", [{
                            "type": "success",
                            "id": dataId
                        }, xhr.responseText]);
                        break;
                    case "error":
                        MWF.runCallback(callback, "failure", [xhr]);
                        break;
                }
            }else{
                MWF.runCallback(callback, "failure", [xhr]);
            }
        }else{
            MWF.runCallback(callback, "failure", [xhr]);
        }
    },

    invokeFormDataWithoutProgress: function(xhr, method, uri, data, file, callback, async, progress){
        var messageItem = null;
        var currentDate = new Date();

        xhr.addEventListener("readystatechange", function(e){
            if (xhr.readyState == 4){
                this.transferComplete(e, xhr, messageItem, currentDate, file);
                this.xhrStateChange(e, xhr, messageItem, callback);
            }
        }.bind(this), false);

        xhr.open(method, uri, true);
        xhr.withCredentials = true;

        messageItem = this.addFormDataMessage(file, true, xhr, progress);
        xhr.send(data);
        this.setMessageText(messageItem, MWF.LP.desktop.action.sendStart);
    },
    invokeFormDataWithForm: function(xhr, method, uri, data, file, callback, async){
        MWF.O2UploadCallback = callback;
        MWF.O2UploadCallbackFun = function(){
            if (MWF.O2UploadCallback) MWF.O2UploadCallback();
        };
        var div = data.items[0].value.el.getParent();
        div.set("styles", {
            "width": "500px",
            "height": "300px",
            "background-color": "#999999",
            "position": "absolute",
            "top": "100px",
            "left": "100px",
            "z-index": "30000",
            "display": "block"
        }).inject(document.body);

        var formNode = new Element("form", {
            "method": method,
            "action": (uri.indexOf("?")!=-1) ? uri+"&callback=MWF.O2UploadCallbackFun" : uri+"?callback=MWF.O2UploadCallbackFun",
            "enctype": "multipart/form-data",
            "target": "o2_upload_iframe"
        }).inject(div);
        var iframe = new Element("iframe", {
            "name": "o2_upload_iframe"
        }).inject(div);
        data.items.each(function(item){
            if (typeOf(item.value)=="string"){
                new Element("input", {
                    "name": item.name,
                    "value": item.value
                }).inject(formNode);
            }else{
                item.value.el.inject(formNode);
                item.value.el.set("name", item.name);
            }
        }.bind(this));
        var submitNode = new Element("input", {
            "type": "submit"
        }).inject(formNode);
     //   formNode.submit();
    },

	invokeFormData: function(method, uri, data, file, callback, async, progress){
		var xhr = new COMMON.Browser.Request();
        data.append('fileName', file.name);
        if (data.type==="o2_formdata"){
            this.invokeFormDataWithForm(xhr, method, uri, data, file, callback, async);
        }else{
            if (xhr.upload){
                this.invokeFormDataWithProgress(xhr, method, uri, data, file, callback, async, progress);
            }else{
                this.invokeFormDataWithoutProgress(xhr, method, uri, data, file, callback, async, progress);
            }
        }
        return xhr;
	},
	addFormDataMessage: function(file, noProgress, xhr, showMsg){
        var contentHTML = "";
        if (noProgress){
            contentHTML = "<div style=\"height: 20px; line-height: 20px\">"+MWF.LP.desktop.action.sendReady+"</div></div>" ;
        }else{
            contentHTML = "<div style=\"overflow: hidden\"><div style=\"height: 3px; border:1px solid #999; margin: 3px 0px\">" +
            "<div style=\"height: 3px; background-color: #acdab9; width: 0px;\"></div></div>" +
            "<div style=\"height: 20px; line-height: 20px\">"+MWF.LP.desktop.action.sendReady+"</div></div>" ;
        }
		var msg = {
			"subject": MWF.LP.desktop.action.uploadTitle,
			//"content": MWF.LP.desktop.action.uploadTitle+" : "+file.name+"<br/>"+contentHTML
            "content": ( file.name ? (file.name+"<br/>") : "" )+contentHTML
		};
        if (layout.desktop.message){
            var messageItem = layout.desktop.message.addMessage(msg);

            //var _self = this;
            messageItem.close = function(callback, e){
                if (this.status=="progress"){
                    flag = false;
                    var text = MWF.LP.desktop.action.cancelUpload.replace(/{name}/g, (file.name||""));
                    MWF.xDesktop.confirm("wram", e, MWF.LP.desktop.action.cancelUploadTitle, text, "400", "140", function(){
                        xhr.abort();
                        //xhr.upload.timeout = 1;
                        this.close();
                        //messageItem.closeItem();
                    }, function(){
                        this.close()
                    });
                    //MWF.LP.desktop.action.sendStart
                }else{
                    messageItem.closeItem(callback, e);
                }
            };
        }

        //messageItem.addEvent("close", function(flag, e){
        //    debugger;
        //    if (this.status=="progress"){
        //        flag = false;
        //        var text = MWF.LP.desktop.action.cancelUpload.replace(/{name}/g, file.name);
        //        MWF.xDesktop.confirm("wram", e, MWF.LP.desktop.action.cancelUploadTitle, text, "300", "120", function(){
        //            xhr.abort();
        //            this.close();
        //            //messageItem.closeItem();
        //        }, function(){
        //            this.close()
        //        });
        //        //MWF.LP.desktop.action.sendStart
        //    }
        //});

        if (showMsg){
            window.setTimeout(function(){
                if (layout.desktop.message) if (!layout.desktop.message.isShow) layout.desktop.message.show();
            }.bind(this), 300);
        }

        //msg = {
        //    "subject": MWF.LP.desktop.action.uploadTitle,
        //    "content": MWF.LP.desktop.action.uploadTitle+" : "+file.name
        //};
        //var tooltipItem = layout.desktop.message.addTooltip(msg);
        return messageItem;
	},
	getAuthentication: function(success, failure){
		this.invoke({
			"name": "authentication",
			"async": true,
			"success": function(json, responseText){
				if (json.data.tokenType!="anonymous"){
					if (success) success(json);
				}else{
					if (failure) failure(null, responseText, json.message);
				}
			},
			"failure": failure
		});
	},
	login: function(data, success, failure){
        var name = "login";
    //    if (data.credential.toLowerCase()=="xadmin") name = "loginAdmin";
		this.invoke({
			"name": name,
			"async": true,
			"data": data,
			"success": function(json, responseText){
				//if (json.data.authentication){
                if (json.data.tokenType!="anonymous"){
					if (success) success(json);
				}else{
					if (failure) failure(null, responseText, json.message);
				}
			},
			"failure": failure
		});
	},
	logout: function(success, failure){
		this.invoke({
			"name": "logout",
			"async": false,
			"success": success,
			"failure": failure
		});
	}
	
});

MWF.xDesktop.Actions.RestActions.Callback = new Class({
	initialize: function(success, failure, appendSuccess, appendFailure){
		this.success = success;
		this.failure = failure;
		this.appendSuccess = appendSuccess;
		this.appendFailure = appendFailure;
	},
	
	onSuccess: function(responseJSON, responseText){
		if (responseJSON){
			switch(responseJSON.type) {
			   case "success":
				   if (this.appendSuccess) this.appendSuccess(responseJSON);
				   if (this.success) this.success(responseJSON, responseText);
			       break;
			   case "warn":
				   MWF.xDesktop.notice("info", {x: "right", y:"top"}, responseJSON.errorMessage.join("\n"));
				   
				   if (this.appendSuccess) this.appendSuccess(responseJSON);
				   if (this.success) this.success(responseJSON);
			       break;
			   case "error":
				   this.doError(null, responseText, responseJSON.message);
				   break;
			}
		}else{
			this.doError(null, responseText, "");
		}
	},
	onRequestFailure: function(xhr){
		this.doError(xhr, "", "");
	},
	onFailure: function(xhr){
		this.doError(xhr, "", "");
	},
	onError: function(text, error){
		this.doError(null, text, error);
	},
	doError: function(xhr, text, error){
		if (this.appendFailure) this.appendFailure(xhr, text, error);
		if (this.failure) this.failure(xhr, text, error);
		if (!this.failure && !this.appendFailure){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
		//	throw "request error: "+errorText;
		}
	}
});
