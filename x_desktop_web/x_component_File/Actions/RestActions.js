MWF.xApplication.File.Actions = MWF.xApplication.File.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.File.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_file_assemble_control", "x_component_File");
	},
	listTopFolder: function(success, failure, async){
		this.action.invoke({"name": "listFolderTop","async": async,	"success": success,	"failure": failure});
	},
	listShare: function(success, failure, async){
		this.action.invoke({"name": "listShare","async": async,"success": success,"failure": failure});
	},
	listShareAttachment: function(success, failure, id, async){
		this.action.invoke({"name": "listShareAttachment","async": async,"parameter": {"person": id},"success": success,"failure": failure});
	},
    listEditor: function(success, failure, async){
        this.action.invoke({"name": "listEditor","async": async,"success": success,"failure": failure});
    },
    listEditorAttachment: function(success, failure, id, async){
        this.action.invoke({"name": "listEditorAttachment","async": async,"parameter": {"person": id},"success": success,"failure": failure});
    },

	
	listFolder: function(success, failure, id, async){
		this.action.invoke({"name": "listFolder","async": async,"parameter": {"id": id},"success": success,"failure": failure});
	},
	listAttachmentTop: function(success, failure, async){
		this.action.invoke({"name": "listAttachmentTop","async": async,"success": success,"failure": failure});
	},
	listAttachment: function(success, failure, id, async){
		this.action.invoke({"name": "listAttachment","async": async,"parameter": {"id": id},"success": success,"failure": failure});
	},
    listComplex: function(success, failure, id, async){
        this.action.invoke({"name": "listComplex","async": async,"parameter": {"id": id},"success": success,"failure": failure});
    },
	
	addAttachment: function(success, failure, formData, folder, file){
		this.action.invoke({"name": "addAttachment","data": formData, "parameter": {"folder": folder},"file": file,"success": success,"failure": failure});
	},
    updateAttachmentData: function(success, failure, formData, id, file){
        this.action.invoke({"name": "updateAttachmentData","data": formData,"parameter": {"id": id},"file": file,"success": success,"failure": failure});
    },

    getFolder: function(success, failure, id, async){
        this.action.invoke({"name": "getFolder","async": async,"parameter": {"id": id},"success": success,"failure": failure});
    },
	getAttachment: function(id){
		var url= this.designAddress+this.fileActions.getAttachmentData.uri;
		url = url.replace(/{id}/g, id);
		window.open(url);
	},
	
	saveFolder: function(data, success, failure){
		if (data.id){
			this.updateFolder(data, success, failure);
		}else{
			this.addFolder(data, success, failure);
		}
	},
	updateFolder: function(data, success, failure){
		this.action.invoke({"name": "updateFolder","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addFolder: function(data, success, failure){
		this.action.invoke({"name": "addFolder","data": data,"success": success,"failure": failure});
	},
	
	
	updateAttachment: function(data, success, failure){
		this.action.invoke({"name": "updateAttachment","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	
	deleteFolder: function(id, success, failure){
		this.action.invoke({
			"name": "removeFolder",
			"parameter": {"id": id},
			"success": success,
			"failure": failure
		});
	},
	deleteFile: function(id, success, failure){
		this.action.invoke({
			"name": "removeAttachment",
			"parameter": {"id": id},
			"success": success,
			"failure": failure
		});
	},
    getFileUrl: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    },
    getFileDownloadUrl: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    },

	getBase64Code: function(success, failure, id, width, height, async){
		width = width || 0;
		height = height ||0;
		this.action.invoke({"name": "getBase64Code","async": async,"parameter": {"id": id, "height" : height, "width" : width},"success": success,"failure": failure});
	}
});