MWF.xApplication.Common.Actions = MWF.xApplication.Common.Actions || {};
MWF.xApplication.Common.Actions.RestActions = new Class({
	initialize: function(){

		this.designAddress = "";	
		MWF.getJSON("../x_component_Common/Actions/properties.jsp", function(json){
			this.actions = json;
		}.bind(this), false);
	},
	
	listApplicationAddress: function(success, failure){
		var url = this.actions.listAddress;
		url = this.actions.slotHost+url;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	
	getDesignAddress: function(success, failure){
//		var name = "x_processplatform_core_designer";
//		var url = this.actions.getAddress.replace(/{id}/g, name);
//		url = this.actions.slotHost+url;
//		var callback = new MWF.process.RestActions.Callback(success, failure, function(data){
//			this.designAddress = data.data.url;
//		}.bind(this));
		
//		MWF.getJSON(url, callback);
		
		this.designAddress = "http://xa01.zoneland.net:9080/x_processplatform_assemble_designer";
		//this.designAddress = "http://localhost:9080/x_processplatform_front_designer";
		if (success) success.apply();
	},
	
	request: function(success, failure, type, arg){
		if (this.designAddress){
			this["_"+type](success, failure, arg);
		}else{
			this.getDesignAddress(function(data){
				this["_"+type](success, failure, arg);
			}.bind(this), failure);
		}
	},
	
	_getId: function(success, failure, count){
		var url = this.designAddress+this.actions.getId;
		url = url.replace(/{count}/g, count);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	
	getId: function(count, success, failure){
		this.request(success, failure, "getId", count);
	},
	getUUID: function(){
		if (!this.designAddress) this.getDesignAddress();
		var url = this.designAddress+this.actions.getId;
		url = url.replace(/{count}/g, "1");
		var id = "";
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(function(ids){
			id = ids.data[0].id;
		}, null);
		MWF.getJSON(url, callback, false);
		return id;
	}
});

MWF.xApplication.Common.Actions.RestActions.Callback = new Class({
	initialize: function(success, failure, appendSuccess, appendFailure){
		this.success = success;
		this.failure = failure;
		this.appendSuccess = appendSuccess;
		this.appendFailure = appendFailure;
	},
	
	onSuccess: function(responseJSON, responseText){

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
