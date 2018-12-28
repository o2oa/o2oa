MWF.xDesktop.requireApp("process.ProcessManager", "package", null, false);
MWF.xApplication.process.ProcessManager.RestActions = new Class({
	initialize: function(actions){
		this.actions = actions;
		this.designAddress = "";
	},
	
	listApplicationAddress: function(success, failure){
		var url = this.actions.listAddress;
		url = this.actions.slotHost+url;
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
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
		
		this.designAddress = "http://xa01.zoneland.net:9080/x_processplatform_core_designer";
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
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
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
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(function(ids){
			id = ids.data[0].id;
		}, null);
		MWF.getJSON(url, callback, false);
		return id;
	},
	
	_listFormCategory: function(success, failure, sign){
		var url = this.designAddress+this.actions.listFormCategory;
		url = url.replace(/{id}/g, sign.lastId ? sign.lastId : "(0)");
		url = url.replace(/{count}/g, sign.count ? sign.count : "20");
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listFormCategory: function(lastId, count, success, failure){
		this.request(success, failure, "listFormCategory", {"lastId": lastId, "count": count});
	},
	
	
	_listProcessCategory: function(success, failure, sign){
		var url = this.designAddress+this.actions.listProcessCategory;
		url = url.replace(/{id}/g, sign.lastId ? sign.lastId : "(0)");
		url = url.replace(/{count}/g, sign.count ? sign.count : "20");
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listProcessCategory: function(lastId, count, success, failure){
		this.request(success, failure, "listProcessCategory", {"lastId": lastId, "count": count});
	},

	_listForm: function(success, failure, arg){
		var url = this.designAddress+this.actions.listForm;
		url = url.replace(/{cid}/g, arg.category);
		url = url.replace(/{id}/g, arg.last ? arg.last : "(0)");
		url = url.replace(/{count}/g, arg.count ? arg.count : "20");
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listForm: function(category, last, count, success, failure){
		this.request(success, failure, "listForm", {"category": category, "last": last, "count": count});
	},
	
	_listProcess: function(success, failure, arg){
		var url = this.designAddress+this.actions.listProcess;
		url = url.replace(/{cid}/g, arg.category);
		url = url.replace(/{id}/g, arg.last ? arg.last : "(0)");
		url = url.replace(/{count}/g, arg.count ? arg.count : "20");

		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listProcess: function(category, last, count, success, failure){
		this.request(success, failure, "listProcess", {"category": category, "last": last, "count": count});
	},
	
	
	_getForm: function(success, failure, form){
		var url = this.designAddress+this.actions.getForm;
		url = url.replace(/{id}/g, form);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
		
	},
	getForm: function(form, success, failure){
		this.request(success, failure, "getForm", form);
	},
	
	_getProcess: function(success, failure, process){
		var url = this.designAddress+this.actions.getProcess;
		url = url.replace(/{id}/g, process);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
		
	},
	getProcess: function(process, success, failure){
		this.request(success, failure, "getProcess", process);
	},
	
	_getProcessCategory: function(success, failure, id){
		var url = this.designAddress+this.actions.getProcessCategory;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	getProcessCategory: function(id, success, failure){
		this.request(success, failure, "getProcessCategory", id);
	},
	
	_getFormCategory: function(success, failure, id){
		var url = this.designAddress+this.actions.getFormCategory;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	getFormCategory: function(id, success, failure){
		this.request(success, failure, "getFormCategory", id);
	},
	
	saveProcessCategory: function(categoryData, success, failure){
		if (this.designAddress){
			if (categoryData.id){
				this.updateProcessCategory(categoryData, success, failure);
			}else{
				this.addProcessCategory(categoryData, success, failure);
			}
		}else{
			this.getDesignAddress(function(data){
				if (categoryData.id){
					this.updateProcessCategory(categoryData, success, failure);
				}else{
					this.addProcessCategory(categoryData, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updateProcessCategory: function(categoryData, success, failure){
		var address = this.designAddress+this.actions.updataProcessCategory;
		address = address.replace(/{id}/g, categoryData.id);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(categoryData), callback);
	},
	addProcessCategory: function(categoryData, success, failure){
		this.getId(1, function(json){
			categoryData.id = json.data[0].id;
			var address = this.designAddress+this.actions.addProcessCategory;
			var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
			MWF.restful("POST", address, JSON.encode(categoryData), callback);
		}.bind(this));
	},
	
	saveProcess: function(processData, success, failure){
		if (this.designAddress){
			if (!processData.isNewProcess){
				this.updateProcess(processData, success, failure);
			}else{
				this.addProcess(processData, success, failure);
			}
		}else{
			this.getDesignAddress(function(data){
				if (!processData.isNewProcess){
					this.updateProcess(processData, success, failure);
				}else{
					this.addProcess(processData, success, failure);
				}
			}.bind(this), failure);
		}
	},
	addProcess: function(processData, success, failure){
		var address = this.designAddress+this.actions.addProcess;
		address = address.replace(/{id}/g, processData.categoryId);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.restful("POST", address, JSON.encode(processData), callback);
	},
	updateProcess: function(processData, success, failure){
		var address = this.designAddress+this.actions.updataProcess;
		address = address.replace(/{id}/g, processData.id);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(processData), callback);
	},
	_deleteProcess: function(success, failure, id){
		var url = this.designAddress+this.actions.removeProcess;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.restful("DELETE", url, "", callback);
	},
	deleteProcess: function(id, success, failure){
		this.request(success, failure, "deleteProcess", id);
	},
	
	saveFormCategory: function(categoryData, success, failure){
		if (this.designAddress){
			if (categoryData.id){
				this.updateFormCategory(categoryData, success, failure);
			}else{
				this.addFormCategory(categoryData, success, failure);
			}
		}else{
			this.getDesignAddress(function(data){
				if (categoryData.id){
					this.updateFormCategory(categoryData, success, failure);
				}else{
					this.addFormCategory(categoryData, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updateFormCategory: function(categoryData, success, failure){
		var address = this.designAddress+this.actions.updataFormCategory;
		address = address.replace(/{id}/g, categoryData.id);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(categoryData), callback);
	},
	addFormCategory: function(categoryData, success, failure){
		this.getId(1, function(json){
			categoryData.id = json.data[0].id;
			var address = this.designAddress+this.actions.addFormCategory;
			var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
			MWF.restful("POST", address, JSON.encode(categoryData), callback);
		}.bind(this));
	},
	
	saveForm: function(formData, success, failure){

		if (this.designAddress){
			if (!formData.isNewForm){
				this.updateForm(formData, success, failure);
			}else{
				this.addForm(formData, success, failure);
			}
		}else{
			this.getDesignAddress(function(data){
				if (!formData.isNewForm){
					this.updateForm(formData, success, failure);
				}else{
					this.addForm(formData, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updateForm: function(formData, success, failure){
		var address = this.designAddress+this.actions.updataForm;
		address = address.replace(/{id}/g, formData.json.id);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		var data = MWF.encodeJsonString(JSON.encode(formData));
		var json = "{\"id\":\""+formData.json.id+"\", \"data\":\""+data+"\", \"name\":\""+formData.json.name+"\", \"alias\":\""+formData.json.name+"\", \"description\":\""+formData.json.description+"\", \"formCategory\": \""+formData.json.formCategory+"\"}";
		MWF.restful("PUT", address, json, callback);
	},
	addForm: function(formData, success, failure){
		var address = this.designAddress+this.actions.addForm;
		address = address.replace(/{id}/g, formData.json.categoryId);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		var data = MWF.encodeJsonString(JSON.encode(formData));
		alert(data)
		var json = "{\"id\":\""+formData.json.id+"\", \"data\": \""+data+"\", \"name\":\""+formData.json.name+"\", \"alias\":\""+formData.json.name+"\", \"description\":\""+formData.json.description+"\", \"formCategory\": \""+formData.json.formCategory+"\"}";
		MWF.restful("POST", address, json, callback);
	},
	_deleteForm: function(success, failure, id){
		var url = this.designAddress+this.actions.removeForm;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.process.ProcessManager.RestActions.Callback(success, failure);
		MWF.restful("DELETE", url, "", callback);
	},
	deleteForm: function(id, success, failure){
		this.request(success, failure, "deleteForm", id);
	}
	
});

MWF.xApplication.process.ProcessManager.RestActions.Callback = new Class({
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
