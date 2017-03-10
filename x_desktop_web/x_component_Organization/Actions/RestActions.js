MWF.xApplication.Organization = MWF.xApplication.Organization || {};
MWF.xApplication.Organization.Actions = MWF.xApplication.Organization.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.Organization.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_control", "x_component_Organization");
        this.appAction = new MWF.xDesktop.Actions.RestActions("/Actions/appAction.json", "x_processplatform_assemble_surface", "x_component_Organization");
        this.designerAction = new MWF.xDesktop.Actions.RestActions("/Actions/appAction.json", "x_processplatform_assemble_designer", "x_component_Organization");
		this.cmsAction = new MWF.xDesktop.Actions.RestActions("/Actions/appAction.json", "x_cms_assemble_control", "x_component_Organization");
	},
	listTopCompany: function(success, failure, async){
		this.action.invoke({"name": "listTopCompany","async": async,	"success": success,	"failure": failure});
	},
	listSubCompany: function(success, failure, id, async){
		this.action.invoke({"name": "listSubCompanyDirect","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
    listCompanyByKey: function(success, failure, key, async){
        this.action.invoke({"name": "listCompanyByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },

	listDepartment: function(success, failure, id, async){
		this.action.invoke({"name": "listDepartmentDirect","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	listSubDepartment: function(success, failure, id, async){
		this.action.invoke({"name": "listSubDepartmentDirect","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
    //listSubDepartment: function(success, failure, id, async){
    //    this.action.invoke({"name": "listSubDepartmentDirect","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    //},
    listDepartmentByKey: function(success, failure, key, async){
        this.action.invoke({"name": "listDepartmentByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },

    listSubComplexDirect: function(success, failure, id, async){
        this.action.invoke({"name": "listSubComplexDirect","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

	listCompanyDuty: function(success, failure, id, async){
		this.action.invoke({"name": "listCompanyDuty","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	listDepartmentDuty: function(success, failure, id, async){
		this.action.invoke({"name": "listDepartmentDuty","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},

    listDepartmentDutyName: function(success, failure, async){
        this.action.invoke({"name": "listDepartmentDutyName","async": async, "success": success, "failure": failure});
    },
    listCompanyDutyName: function(success, failure, async){
        this.action.invoke({"name": "listCompanyDutyName","async": async, "success": success, "failure": failure});
    },

	listCompanyDutyByIdentity: function(success, failure, id, async){
		this.action.invoke({"name": "listCompanyDutyByIdentity","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	listDepartmentDutyByIdentity: function(success, failure, id, async){
		this.action.invoke({"name": "listDepartmentDutyByIdentity","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	listCompanyAttribute: function(success, failure, id, async){
		this.action.invoke({"name": "listCompanyAttribute","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	listDepartmentAttribute: function(success, failure, id, async){
		this.action.invoke({"name": "listDepartmentAttribute","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	listIdentity: function(success, failure, id, async){
		this.action.invoke({"name": "listIdentity","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
    listIdentityByKey: function(success, failure, key, async){
        this.action.invoke({"name": "listIdentityByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listIdentityByPinyin: function(success, failure, key, async){
        this.action.invoke({"name": "listIdentityByPinyin","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },

	listIdentityByPerson: function(success, failure, id, async){
		this.action.invoke({"name": "listIdentityByPerson","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	listGroupNext: function(last, count, success, failure, async){
		this.action.invoke({"name": "listGroupNext","async": async, "parameter": {"id": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
	},
    listSupGroupDirect: function(success, failure, id, async){
        this.action.invoke({"name": "listSupGroupDirect","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },


	listRoleNext: function(last, count, success, failure, async){
		this.action.invoke({"name": "listRoleNext","async": async, "parameter": {"id": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
	},
	listPersonNext: function(last, count, success, failure, async){
		this.action.invoke({"name": "listPersonNext","async": async, "parameter": {"id": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
	},
	listPersonAttribute: function(success, failure, id, async){
		this.action.invoke({"name": "listPersonAttribute","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
    listPersonByKey: function(success, failure, key, async){
        this.action.invoke({"name": "listPersonByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listPersonByPinyin: function(success, failure, key, async){
        this.action.invoke({"name": "listPersonByPinyin","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listRoleByKey: function(success, failure, key, async){
    this.action.invoke({"name": "listRoleByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
},
    listRoleByGroup: function(success, failure, id, async){
        this.action.invoke({"name": "listRoleByGroup","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },


    listGroupByKey: function(success, failure, key, async){
        this.action.invoke({"name": "listGroupByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listGroupByPinyin: function(success, failure, key, async){
        this.action.invoke({"name": "listGroupByPinyin","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },


	getIdentity: function(success, failure, id, async){
		this.action.invoke({"name": "getIdentity","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	getPerson: function(success, failure, id, async){
		this.action.invoke({"name": "getPerson","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	getDepartment: function(success, failure, id, async){
		this.action.invoke({"name": "getDepartment","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	getCompany: function(success, failure, id, async){
		this.action.invoke({"name": "getCompany","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	getGroup: function(success, failure, id, async){
		this.action.invoke({"name": "getGroup","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
    getCompanyAccess: function(success, failure, id, async){
        this.action.invoke({"name": "getCompanyAccess","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

	
	saveCompany: function(data, success, failure){
		if (data.id){
			this.updateCompany(data, success, failure);
		}else{
			this.addCompany(data, success, failure);
		}
	},
	updateCompany: function(data, success, failure){
		this.action.invoke({"name": "updateCompany","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addCompany: function(data, success, failure){
		this.action.invoke({"name": "addCompany","data": data,"success": success,"failure": failure});
	},
	
	saveDepartment: function(data, success, failure){
		if (data.id){
			this.updateDepartment(data, success, failure);
		}else{
			this.addDepartment(data, success, failure);
		}
	},
	updateDepartment: function(data, success, failure){
		this.action.invoke({"name": "updateDepartment","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addDepartment: function(data, success, failure){
		this.action.invoke({"name": "addDepartment", "data": data, "success": success, "failure": failure});
	},
	
	saveCompanyDuty: function(data, success, failure){
		if (data.id){
			this.updateCompanyDuty(data, success, failure);
		}else{
			this.addCompanyDuty(data, success, failure);
		}
	},
	updateCompanyDuty: function(data, success, failure){
		this.action.invoke({"name": "updateCompanyDuty","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addCompanyDuty: function(data, success, failure){
		this.action.invoke({"name": "addCompanyDuty","data": data,"success": success,"failure": failure});
	},
	
	saveDepartmentDuty: function(data, success, failure){
		if (data.id){
			this.updateDepartmentDuty(data, success, failure);
		}else{
			this.addDepartmentDuty(data, success, failure);
		}
	},
	updateDepartmentDuty: function(data, success, failure){
		this.action.invoke({"name": "updateDepartmentDuty","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addDepartmentDuty: function(data, success, failure){
		this.action.invoke({"name": "addDepartmentDuty","data": data,"success": success,"failure": failure});
	},

	saveCompanyAttribute: function(data, success, failure){
		if (data.id){
			this.updateCompanyAttribute(data, success, failure);
		}else{
			this.addCompanyAttribute(data, success, failure);
		}
	},
	updateCompanyAttribute: function(data, success, failure){
		this.action.invoke({"name": "updateCompanyAttribute","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addCompanyAttribute: function(data, success, failure){
		this.action.invoke({"name": "addCompanyAttribute","data": data,"success": success,"failure": failure});
	},
	
	saveDepartmentAttribute: function(data, success, failure){
		if (data.id){
			this.updateDepartmentAttribute(data, success, failure);
		}else{
			this.addDepartmentAttribute(data, success, failure);
		}
	},
	updateDepartmentAttribute: function(data, success, failure){
		this.action.invoke({"name": "updateDepartmentAttribute","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addDepartmentAttribute: function(data, success, failure){
		this.action.invoke({"name": "addDepartmentAttribute","data": data,"success": success,"failure": failure});
	},
	
	saveGroup: function(data, success, failure){
		if (data.id){
			this.updateGroup(data, success, failure);
		}else{
			this.addGroup(data, success, failure);
		}
	},
	updateGroup: function(data, success, failure){
		this.action.invoke({"name": "updateGroup","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addGroup: function(data, success, failure){
		this.action.invoke({"name": "addGroup","data": data,"success": success,"failure": failure});
	},
	
	saveRole: function(data, success, failure){
		if (data.id){
			this.updateRole(data, success, failure);
		}else{
			this.addRole(data, success, failure);
		}
	},
	updateRole: function(data, success, failure){
		this.action.invoke({"name": "updateRole","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addRole: function(data, success, failure){
		this.action.invoke({"name": "addRole","data": data,"success": success,"failure": failure});
	},
	
	savePerson: function(data, success, failure){
		if (data.id){
			this.updatePerson(data, success, failure);
		}else{
			this.addPerson(data, success, failure);
		}
	},
	updatePerson: function(data, success, failure){
		this.action.invoke({"name": "updatePerson","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addPerson: function(data, success, failure){
		this.action.invoke({"name": "addPerson","data": data,"success": success,"failure": failure});
	},

	savePersonAttribute: function(data, success, failure){
		if (data.id){
			this.updatePersonAttribute(data, success, failure);
		}else{
			this.addPersonAttribute(data, success, failure);
		}
	},
	updatePersonAttribute: function(data, success, failure){
		this.action.invoke({"name": "updatePersonAttribute","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addPersonAttribute: function(data, success, failure){
		this.action.invoke({"name": "addPersonAttribute","data": data,"success": success,"failure": failure});
	},
	
	saveIdentity: function(data, success, failure){
		if (data.id){
			this.updateIdentity(data, success, failure);
		}else{
			this.addIdentity(data, success, failure);
		}
	},
	updateIdentity: function(data, success, failure){
		this.action.invoke({"name": "updateIdentity","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
	},
	addIdentity: function(data, success, failure){
		this.action.invoke({"name": "addIdentity","data": data,"success": success,"failure": failure});
	},
	
	deleteCompanyDuty: function(id, success, failure, async){
		this.action.invoke({"name": "removeCompanyDuty", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
	},
	deleteDepartmentDuty: function(id, success, failure, async){
		this.action.invoke({"name": "removeDepartmentDuty", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
	},
	deleteCompanyAttribute: function(id, success, failure, async){
		this.action.invoke({"name": "removeCompanyAttribute", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
	},
	deleteDepartmentAttribute: function(id, success, failure, async){
		this.action.invoke({"name": "removeDepartmentAttribute", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
	},
	deletePersonAttribute: function(id, success, failure, async){
		this.action.invoke({"name": "removePersonAttribute", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
	},
	deleteIdentity: function(id, success, failure, async){
		this.action.invoke({"name": "removeIdentity", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
	},
	deleteGroup: function(id, success, failure, async){
		this.action.invoke({"name": "removeGroup", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
	},
	deleteRole: function(id, success, failure, async){
		this.action.invoke({"name": "removeRole", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
	},
    deleteCompany: function(id, success, failure, async){
        this.action.invoke({"name": "removeCompany", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    deleteDepartment: function(id, success, failure, async){
        this.action.invoke({"name": "removeDepartment", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    deletePerson: function(id, success, failure, async){
        this.action.invoke({"name": "removePerson", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    changePersonIcon: function(id, success, failure, formData, file){
        this.action.invoke({"name": "changePersonIcon", "parameter": {"id": id},"data": formData,"file": file,"success": success,"failure": failure});
    },

    listApplications: function(success, failure, async){
        this.appAction.invoke({"name": "listApplications","async": async,	"success": success,	"failure": failure});
    },
    listApplicationsProcess: function(success, failure, async){
        this.appAction.invoke({"name": "listApplicationsProcess","async": async,	"success": success,	"failure": failure});
    },
    getApplications: function(success, failure, id, async){
        this.appAction.invoke({"name": "getApplications","async": async, "parameter": {"flag": id},	"success": success,	"failure": failure});
    },
    listProcess: function(success, failure, id, async){
        this.appAction.invoke({"name": "listProcess","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    listFormField: function(application, success, failure, async){
        this.designerAction.invoke({"name": "listFormField","async": async, "parameter": {"id": application}, "success": success,	"failure": failure});
    },


	listCMSApplication: function(success, failure, async){
		this.cmsAction.invoke({"name": "listCMSApplication","async": async,	"success": success,	"failure": failure});
	},
	getCMSApplication: function(success, failure, id, async){
		this.cmsAction.invoke({"name": "getCMSApplication","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
	listCMSCategory: function(success, failure, appId, async){
		this.cmsAction.invoke({"name": "listCMSCategory","async": async, "parameter": {"appId": appId},	"success": success,	"failure": failure});
	},
	getCMSForm: function(id, success, failure, async){
		this.cmsAction.invoke({"name": "getCMSForm","async": async, "parameter": {"id": id },	"success": success,	"failure": failure});
	}
});