MWF.xApplication.Minder = MWF.xApplication.Minder || {};
MWF.xApplication.Minder.Actions = MWF.xApplication.Minder.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);

MWF.xApplication.Minder.Actions.RestActions2 = new Class({
	Extends : MWF.xDesktop.Actions.RestActions,
	invoke: function(option){
		this.getActions(function(){
			//name, parameter, data, async, success, failure, withCredentials, urlEncode
			var action = this.actions[option.name];
			var method = action.method || "GET";
			var uri = action.uri;
			if (option.parameter){
				Object.each(option.parameter, function(value, key){
					var reg = new RegExp("{"+key+"}", "g");
					if (option.urlEncode===false){
						uri = uri.replace(reg, value);
					}else{
						uri = uri.replace(reg, encodeURIComponent(value));
					}
				});
			}
			if( !this.address )this.getAddress();
			uri = this.address+uri;

			var async = (option.async===false) ? false : true;

			var progress = (option.progress===false) ? false : true; //控制是否显示进度条

			var callback = new MWF.xDesktop.Actions.RestActions.Callback(option.success, option.failure);
			if (action.enctype && (action.enctype.toLowerCase()=="formdata")){
				this.invokeFormData(method, uri, option.data, option.file, callback, async, progress);
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
	invokeFormData: function(method, uri, data, file, callback, async, progress){
		var xhr = new COMMON.Browser.Request();
		data.append('fileName', file.name);
		if (xhr.upload && progress){
			this.invokeFormDataWithProgress(xhr, method, uri, data, file, callback, async);
		}else{
			this.invokeFormDataWithoutProgress(xhr, method, uri, data, file, callback, async);
		}
	},
	invokeFormDataWithoutProgress: function(xhr, method, uri, data, file, callback, async){
		var messageItem = null;
		var currentDate = new Date();

		xhr.addEventListener("readystatechange", function(e){
			if (xhr.readyState == 4){
				//this.transferComplete(e, xhr, messageItem, currentDate, file)
				this.xhrStateChange(e, xhr, messageItem, callback);
			}
		}.bind(this), false);

		xhr.open(method, uri, true);
		xhr.withCredentials = true;

		//messageItem = this.addFormDataMessage(file, true);
		xhr.send(data);
		//this.setMessageText(messageItem, MWF.LP.desktop.action.sendStart);
	}
});

MWF.xAction.RestActions.Action["x_mind_assemble_control"] = new Class({
	Extends: MWF.xAction.RestActions.Action,
	initialize: function(root, actions){
		this.action = new MWF.xApplication.Minder.Actions.RestActions2("/xAction/services/"+root+".json", root, "");
		this.action.actions = actions;

		Object.each(this.action.actions, function(service, key){
			if (service.uri) if (!this[key]) this.createMethod(service, key);
		}.bind(this));
	},
	uploadMindIcon: function(mindId, size, success, failure, formData, file, progress){
		this.action.invoke({"name": "uploadMindIcon", "parameter": {"mindId": mindId, "size" : size},"data": formData,"file": file,"success": success,"failure": failure, "progress" : progress});
	}
});