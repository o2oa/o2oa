MWF.xDesktop.Actions = MWF.xDesktop.Actions || {};
MWF.xDesktop.Actions.RestActions = new Class({
	initialize: function(actionPath, serviceName){
		this.actionPath = actionPath;
		this.serviceName = serviceName;
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
		//this.designAddress = "http://localhost:9080/x_processplatform_front_designer";
		if (success) success.apply();
	},
	getActions: function(callback){
        if (!this.actions){
            MWF.getJSON(MWF.defaultPath+this.actionPath, function(json){
                this.actions = json;
                if (callback) callback();
            }.bind(this), true, true, false);
        }else{
            if (callback) callback();
        }
    },
	invoke: function(option){
        this.getActions(function(){
            //name, parameter, data, async, success, failure, withCredentials
            var action = this.actions[option.name];
            var method = action.method || "GET";
            var uri = action.uri;
            if (option.parameter){
                Object.each(option.parameter, function(value, key){
                    var reg = new RegExp("{"+key+"}", "g");
                    uri = uri.replace(reg, encodeURIComponent(value));
                });
            }
            uri = this.address+uri;

            var async = (option.async===false) ? false : true;

            var callback = new MWF.xDesktop.Actions.RestActions.Callback(option.success, option.failure);
            if (action.enctype && (action.enctype.toLowerCase()=="formdata")){
                this.invokeFormData(method, uri, option.data, option.file, callback, async);
            }else{
                var data = (option.data) ? JSON.encode(option.data) : "";
                var credentials = true;
                if (option.withCredentials===false){
                    credentials = false;
                }
                MWF.restful(method, uri, data, callback, async, credentials);
            }
        }.bind(this));
	},
	formDataUpdateProgress: function(){
		
	},
	invokeFormData: function(method, uri, data, file, callback, async){
		var xhr = new COMMON.Browser.Request();		
		xhr.open(method, uri, true);
		xhr.withCredentials = true;
		var messageItem = null;
        var tooltipItem = null;
		var totalSize = file.size;
		var currentDate = new Date();
//		var prevDate = currentDate;
        var dataId = "";

		var transferStart = function(e){
			
		}.bind(this);

        var finish = false;
		var updateProgress = function(e){
			if (e.lengthComputable){
                if (!finish){
                    var returnSize = xhr.responseText;
                    //if (!dataId) dataId = returnSize.substr(0, returnSize.indexOf("#"));
                    if (xhr.responseText.indexOf("$")!=-1){
                        returnSize = returnSize.substr(0, returnSize.lastIndexOf("$"));
                        returnSize = returnSize.substr(returnSize.lastIndexOf("#")+1, returnSize.length);
                        finish = true;
                    }else{
                        returnSize = returnSize.substr(returnSize.lastIndexOf("#")+1, returnSize.length);
                    }
                    if (!returnSize) return false;

                    returnSize = returnSize*32*1024;

                    var percent = 100*(returnSize/totalSize);
                    //this.messageItem.subjectNode.set("text", percent);
                    if (messageItem.contentNode){
                        var progressNode = messageItem.contentNode.getFirst("div").getFirst("div");
                        var progressPercentNode = progressNode.getFirst("div");
                        var progressInforNode = messageItem.contentNode.getFirst("div").getLast("div");
                        progressPercentNode.setStyle("width", ""+percent+"%");
                    }


                    var sendDate = new Date();
                    var ms = sendDate.getTime()-currentDate.getTime();
                    //var msSpan = sendDate.getTime() - prevDate.getTime();
                    //prevDate = sendDate;
                    //if (msSpan>300){
                    var speed = (returnSize)/ms;
                    var u = "K/S";
                    if (speed>1024){
                        u = "M/S";
                        speed = speed/1024;
                    }
                    if (speed>1024){
                        u = "G/S";
                        speed = speed/1024;
                    }
                    speed = speed.round(2)

                    progressInforNode.set("text", MWF.LP.desktop.action.sendStart+": "+speed+u);
                    //}
                }

			}
		}.bind(this);
		
		var transferFailed = function(e){
			
		}.bind(this);
		var transferCanceled = function(e){
			
		}.bind(this);
		var transferComplete = function(e){
            if (messageItem.contentNode) {
                var progressNode = messageItem.contentNode.getFirst("div").getFirst("div");
                var progressPercentNode = progressNode.getFirst("div");
                var progressInforNode = messageItem.contentNode.getFirst("div").getLast("div");
            }
			
			var sendDate = new Date();
			var ms = sendDate.getTime()-currentDate.getTime();
			var speed = (totalSize)/ms;
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

            if (messageItem.contentNode) {
                progressPercentNode.setStyle("width", "100%");
                progressInforNode.set("text", MWF.LP.desktop.action.uploadComplete+ "  "+MWF.LP.desktop.action.speed+": "+speed+u+"  "+MWF.LP.desktop.action.time+": "+timeStr);
            }
            var msg = {
                "subject": MWF.LP.desktop.action.uploadTitle,
                "content": MWF.LP.desktop.action.uploadComplete+" : "+file.name
            };
            layout.desktop.message.addTooltip(msg);
		}.bind(this);
		var onreadystatechange= function(e){
			if (xhr.readyState == 2){
				var progressNode = messageItem.contentNode.getFirst("div").getFirst("div");
				var progressPercentNode = progressNode.getFirst("div");
				var progressInforNode = messageItem.contentNode.getFirst("div").getLast("div");
				
				progressInforNode.set("text", MWF.LP.desktop.action.sendStart);
			}
			
			if (xhr.readyState != 4) return;
            var returnStr = xhr.responseText;
            returnStr = returnStr.substr(returnStr.lastIndexOf("$")+1, returnStr.length);
            returnStr = returnStr.substr(returnStr.lastIndexOf("#")+1, returnStr.length);
            var idx = returnStr.indexOf("*");
            if (idx!=-1){
                returnStr = returnStr.substr(0, idx);
            }
            dataId = returnStr;

			var status = xhr.status;
            alert(status+"--"+xhr.readyState);

			status = (status == 1223) ? 204 : status;
			
			if ((status >= 200 && status < 300)){
				MWF.runCallback(callback, "success", [{
					"type": "success",
                    "id": dataId
				}, xhr.responseText]);
			}else{
				MWF.runCallback(callback, "failure", [xhr]);
			}
		};
		if (xhr.upload){
            xhr.addEventListener("progress", updateProgress, false);
            xhr.addEventListener("load", transferComplete, false);
            xhr.addEventListener("loadstart", transferStart, false);
            xhr.addEventListener("error", transferFailed, false);
            xhr.addEventListener("abort", transferCanceled, false);
        }else{

        }

		xhr.addEventListener("readystatechange", onreadystatechange, false);

        var o = this.addFormDataMessage(file);
		messageItem = o.messageItem;
        tooltipItem = o.tooltipItem;
		
	//	xhr.onreadystatechange = onreadystatechange;
		
		xhr.send(data);
	},
	addFormDataMessage: function(file){
		var contentHTML = "<div style=\"overflow: hidden\"><div style=\"height: 10px; border:1px solid #666;\">" +
				"<div style=\"height: 10px; background-color: #acdab9; width: 0px;\"></div></div>" +
				"<div style=\"height: 20px; line-height: 20px\">"+MWF.LP.desktop.action.sendReady+"</div></div>" ;
		var msg = {
			"subject": MWF.LP.desktop.action.uploadTitle,
			"content": MWF.LP.desktop.action.uploadTitle+" : "+file.name+"<br/>"+contentHTML
		};
        var messageItem = layout.desktop.message.addMessage(msg);

        msg = {
            "subject": MWF.LP.desktop.action.uploadTitle,
            "content": MWF.LP.desktop.action.uploadTitle+" : "+file.name
        };
        var tooltipItem = layout.desktop.message.addTooltip(msg);
        return {"tooltipItem": tooltipItem, "messageItem": messageItem};
	},
	getAuthentication: function(success, failure){
		this.invoke({
			"name": "authentication",
			"async": true,
			"success": function(json, responseText){
				if (json.data){
					if (success) success(json);
				}else{
					if (failure) failure(null, responseText, json.message);
				}
			},
			"failure": failure
		});
	},
	login: function(data, success, failure){
		this.invoke({
			"name": "login",
			"async": true,
			"data": data,
			"success": function(json, responseText){
				if (json.data.authentication){
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
				   if (this.success) this.success(responseJSON);
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

			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
		//	throw "request error: "+errorText;
		}
	}
});
