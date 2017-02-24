MWF.xApplication.Forum = MWF.xApplication.Forum || {};
MWF.xApplication.Forum.Actions = MWF.xApplication.Forum.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.Forum.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_bbs_assemble_control", "x_component_Forum");

        this.actionOrg = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_express", "x_component_Forum");

        this.actionHotPic = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_hotpic_assemble_control", "x_component_Forum");

        //this.actionInstrument = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_instrument_assemble_tunnel", "x_component_Forum");

        this.actionPerson =  new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_personal", "x_component_Forum");

        this.actionAuthentication = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_authentication", "x_component_Forum");
	},
	getId: function(count, success, failure, async){
		this.action.invoke({"name": "getId","async": async, "parameter": {"count": count},	"success": success,	"failure": failure});
	},
    getUUID: function(success){

        var id = "";
        this.action.invoke({"name": "getId","async": false, "parameter": {"count": "1"}, "success": function(ids){
            id = ids.data[0];
            if (success) success(id);
        },	"failure": null});
        return id;
    },

    getPerson: function(success, failure, name, async){
        this.actionOrg.invoke({"name": "getPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getPersonIcon: function( name, callback ){
        this.actionOrg.getActions(function(){
            var url = this.actionOrg.actions.getPersonIcon.uri;
            url = url.replace("{name}", encodeURIComponent(name));
            if (callback) callback(this.actionOrg.address+url);
        }.bind(this));
    },
    listCompanyByPerson: function(success, failure, name, async){
        this.actionOrg.invoke({"name": "listCompanyByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listDepartmentByPerson: function(success, failure, name, async){
        this.actionOrg.invoke({"name": "listDepartmentByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listDepartmentByCompany: function(success, failure, companyName, async){
        this.actionOrg.invoke({"name": "listDepartmentByCompany","async": async, "parameter": {"companyName": companyName},	"success": success,	"failure": failure});
    },

    getUserInfor : function(data, success, failure, async){
        this.action.invoke({"name": "getUserInfor","data": data, "async": async, "success": success,"failure": failure});
    },
    login : function(data, success, failure, async){
        this.action.invoke({"name": "login","data": data, "async": async, "success": success,"failure": failure});
    },

    getBBSName: function(success, failure, async){
        this.action.invoke({"name": "getBBSName", "async": async, "success": success,"failure": failure});
    },
    getSystemSetting: function(id, success, failure, async){
        this.action.invoke({"name": "getSystemSetting", "async": async, "parameter": {"id": id },"success": success,"failure": failure});
    },
    getSystemSettingByCode: function(data, success, failure, async){
        this.action.invoke({"name": "getSystemSettingByCode","data": data, "async": async,"success": success,"failure": failure});
    },
    listSystemSettingAll: function(success, failure, async){
        this.action.invoke({"name": "listSystemSettingAll","async": async, "success": success,	"failure": failure});
    },
    saveSystemSetting: function(data, success, failure, async){
        this.action.invoke({"name": "saveSystemSetting","data": data, "async": async,"success": success,"failure": failure});
    },

    getCategory: function(id, success, failure, async){
        this.action.invoke({"name": "getCategory", "async": async, "parameter": {"id": id },"success": success,"failure": failure});
    },
    listCategoryAll: function(success, failure, async){
        this.action.invoke({"name": "listCategoryAll","async": async, "success": success,	"failure": failure});
    },
    listCategoryAllByAdmin : function(success,failure, async){
        this.action.invoke({"name": "listCategoryAllByAdmin", "async": async, "success": success,	"failure": failure});
    },
    saveCategory: function(data, success, failure, async){
        this.action.invoke({"name": "saveCategory","data": data, "async": async,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deleteCategory: function(id, success, failure, async){
        this.action.invoke({"name": "deleteCategory", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    deleteCategoryForce: function(id, success, failure, async){
        this.action.invoke({"name": "deleteCategoryForce", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    getSection: function(id, success, failure, async ){
        this.action.invoke({"name": "getSection", "async": async, "parameter": {"id": id },"success": success,"failure": failure});
    },
    listSection: function(forumId, success, failure, async){
        this.action.invoke({"name": "listSection", "parameter": {"forumId": forumId },"async": async, "success": success,	"failure": failure});
    },
    listSectionByAdmin: function(forumId, success, failure, async){
        this.action.invoke({"name": "listSectionByAdmin", "parameter": {"forumId": forumId },"async": async, "success": success,	"failure": failure});
    },
    listSubSection: function(sectionId, success, failure, async){
        this.action.invoke({"name": "listSubSection", "parameter": {"sectionId": sectionId },"async": async, "success": success,	"failure": failure});
    },
    listSubSectionByAdmin: function(sectionId, success, failure, async){
        this.action.invoke({"name": "listSubSectionByAdmin", "parameter": {"sectionId": sectionId },"async": async, "success": success,	"failure": failure});
    },
    listSectionAll: function(success, failure, async){
        this.action.invoke({"name": "listSectionAll", "async": async, "success": success,	"failure": failure});
    },
    saveSection: function(data, success, failure, async){
        this.action.invoke({"name": "saveSection","data": data, "async": async,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deleteSection: function(id, success, failure, async){
        this.action.invoke({"name": "deleteSection", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    deleteSectionForce: function(id, success, failure, async){
        this.action.invoke({"name": "deleteSectionForce", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    uploadSectionIcon: function(id, success, failure, formData, file){
        this.action.invoke({"name": "uploadSectionIcon", "data": formData,"file": file, "parameter": {"id": id}, "success": success,"failure": failure});
    },


    listPermissionAll: function(success, failure, async){
        this.action.invoke({"name": "listPermissionAll", "async": async, "success": success,	"failure": failure});
    },
    listPermissionByRole: function(roleCode, data, success, failure, async){
        this.action.invoke({"name": "listPermissionByRole","data": data, "async": async, "parameter": {"roleCode": roleCode},"success": success,"failure": failure});
    },
    listPermissionByForum: function(forumId, data, success, failure, async){
        this.action.invoke({"name": "listPermissionByForum","data": data, "async": async, "parameter": {"forumId": forumId},"success": success,"failure": failure});
    },
    listPermissionBySection: function(sectionId, data, success, failure, async){
        this.action.invoke({"name": "listPermissionBySection","data": data, "async": async, "parameter": {"sectionId": sectionId},"success": success,"failure": failure});
    },

    listSectionPermission: function(sectionId, success, failure, async){
        this.action.invoke({"name": "listSectionPermission", "async": async, "parameter": {"sectionId": sectionId},"success": success,"failure": failure});
    },
    listSubjectPermission: function(subjectId, success, failure, async){
        this.action.invoke({"name": "listSubjectPermission", "async": async, "parameter": {"subjectId": subjectId},"success": success,"failure": failure});
    },
    listSubjectPublishPermission: function(sectionId, success, failure, async){
        this.action.invoke({"name": "listSubjectPublishPermission", "async": async, "parameter": {"sectionId": sectionId},"success": success,"failure": failure});
    },
    listReplyPublishPermission: function(subjectId, success, failure, async){
        this.action.invoke({"name": "listReplyPublishPermission", "async": async, "parameter": {"subjectId": subjectId},"success": success,"failure": failure});
    },

    listRoleMemberByCode: function( data, success, failure, async){
        this.action.invoke({"name": "listRoleMemberByCode","data": data, "async": async, "success": success,"failure": failure});
    },
    getRole: function(id, success, failure){
        this.action.invoke({"name": "getRole", "parameter": {"id": id },"success": success,"failure": failure});
    },
    saveRole: function(data, success, failure, async){
        this.action.invoke({"name": "saveRole","data": data, "async": async,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deleteRole: function(id, success, failure, async){
        this.action.invoke({"name": "deleteRole", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    listRoleAll: function(success, failure, async){
        this.action.invoke({"name": "listRoleAll", "async": async, "success": success,	"failure": failure});
    },
    listRoleByForum: function(forumId, data, success, failure, async){
        this.action.invoke({"name": "listRoleByForum","data": data, "async": async, "parameter": {"forumId": forumId},"success": success,"failure": failure});
    },
    listRoleBySection: function(sectionId, data, success, failure, async){
        this.action.invoke({"name": "listRoleBySection","data": data, "async": async, "parameter": {"sectionId": sectionId},"success": success,"failure": failure});
    },
    listRoleByOrganization: function(data, success, failure, async){
        this.action.invoke({"name": "listRoleByOrganization","data": data, "async": async,"success": success,"failure": failure});
    },
    listRoleByUser: function(data, success, failure, async){
        this.action.invoke({"name": "listRoleByUser","data": data, "async": async,"success": success,"failure": failure});
    },
    bindObject: function(data, success, failure, async){
        this.action.invoke({"name": "bindObject","data": data, "async": async,"success": success,"failure": failure});
    },
    bindRole: function(data, success, failure, async){
        this.action.invoke({"name": "bindRole","data": data, "async": async,"success": success,"failure": failure});
    },


    listRecommendedSubject : function( count,  success,failure, async){
        this.action.invoke({"name": "listRecommendedSubject","parameter": {"count" : count }, "async": async, "success": success,	"failure": failure});
    },
    listCreamSubjectFilterPage : function(page, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listCreamSubjectFilterPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listSubjectSearchPage : function(page, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listSubjectSearchPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getSubject: function(id, success, failure){
        this.action.invoke({"name": "getSubject", "parameter": {"id": id },"success": success,"failure": failure});
    },
    getSubjectView: function(id, success, failure, async){
        this.action.invoke({"name": "getSubjectView", "parameter": {"id": id },"async": async, "success": success,	"failure": failure});
    },
    saveSubject: function(data, success, failure, async){
        this.action.invoke({"name": "saveSubject","data": data, "async": async,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deleteSubject: function(id, success, failure, async){
        this.action.invoke({"name": "deleteSubject", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    listSubjectFilterPage : function(page, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listSubjectFilterPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listTopSubject: function(sectionId, success, failure, async){
        this.action.invoke({"name": "listTopSubject", "async": async, "parameter": {"sectionId": sectionId}, "success": success, "failure": failure});
    },
    setCream: function(id, success, failure, async){
        this.action.invoke({"name": "setCream", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    cancelCream: function(id, success, failure, async){
        this.action.invoke({"name": "cancelCream", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    topToBBS: function(id, success, failure, async){
        this.action.invoke({"name": "topToBBS", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    cancelTopToBBS: function(id, success, failure, async){
        this.action.invoke({"name": "cancelTopToBBS", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    topToForum: function(id, success, failure, async){
        this.action.invoke({"name": "topToForum", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    cancelTopToForum: function(id, success, failure, async){
        this.action.invoke({"name": "cancelTopToForum", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    topToMainSection: function(id, success, failure, async){
        this.action.invoke({"name": "topToMainSection", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    cancelTopToMainSection: function(id, success, failure, async){
        this.action.invoke({"name": "cancelTopToMainSection", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    topToSection: function(id, success, failure, async){
        this.action.invoke({"name": "topToSection", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    cancelTopToSection: function(id, success, failure, async){
        this.action.invoke({"name": "cancelTopToSection", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    setRecommend: function(id, success, failure, async){
        this.action.invoke({"name": "setRecommend", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    cancelRecommend: function(id, success, failure, async){
        this.action.invoke({"name": "cancelRecommend", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    lock: function(id, success, failure, async){
        this.action.invoke({"name": "lock", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    unlock: function(id, success, failure, async){
        this.action.invoke({"name": "unlock", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    getReply: function(id, success, failure, async){
        this.action.invoke({"name": "getReply", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    saveReply: function(data, success, failure, async){
        this.action.invoke({"name": "saveReply","data": data, "async": async,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deleteReply: function(id, success, failure, async){
        this.action.invoke({"name": "deleteReply", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    listReplyFilterPage : function(page, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listReplyFilterPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    listMySubjectPage : function(page, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listMySubjectPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listMyReplyPage : function(page, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listMyReplyPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listUserSubjectPage : function(page, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listUserSubjectPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    listDetailFilterNext : function( id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listDetailFilterNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listDetailFilterPrev : function(  id, count, filterData, success,failure, async){
        this.action.invoke({"name": "listDetailFilterPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    deployBaseWork : function(data, success, failure, async){
        this.action.invoke({"name": "deployBaseWork","data": data, "async": async,"success": success,"failure": failure});
    },

    listAttachmentInfo: function(success, failure, async){
        this.action.invoke({"name": "listAttachmentInfo","async": async, "success": success,	"failure": failure});
    },
    listAttachment: function(documentid, success, failure, async){
        this.action.invoke({"name": "listAttachment","async": async, "parameter": {"documentid": documentid},	"success": success,	"failure": failure});
    },
    uploadAttachment: function(documentid, success, failure, formData, file){
        this.action.invoke({"name": "uploadAttachment", "parameter": {"documentid": documentid},"data": formData,"file": file,"success": success,"failure": failure});
    },
    //replaceAttachment: function(id, documentid, success, failure, formData, file){
    //    this.action.invoke({"name": "replaceAttachment", "parameter": {"documentid": documentid, "id": id},"data": formData,"file": file,"success": success,"failure": failure});
    //},
    getAttachment: function(id, documentid, success, failure, async){
        this.action.invoke({"name": "getAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },
    deleteAttachment: function(id, documentid, success, failure, async){
        this.action.invoke({"name": "deleteAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },
    getAttachmentData: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },
    getAttachmentStream: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.action.address+url);
        }.bind(this));
    },

    getAttachmentUrl: function(id, documentid, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    },

    getHotPic: function(application, infoId , success, failure, async){
        this.actionHotPic.invoke({"name": "getHotPic", "parameter": {"application": application, "infoId" : infoId },"success": success,"failure": failure, "async": async});
    },
    saveHotPic: function(data, success, failure, async){
        this.actionHotPic.invoke({"name": "saveHotPic", data : data, "success": success,"failure": failure, "async": async});
    },
    removeHotPic: function(id, success, failure, async){
        this.actionHotPic.invoke({"name": "removeHotPic", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    removeHotPicByInfor: function(application, infoId , success, failure){
        this.actionHotPic.invoke({"name": "removeHotPicByInfor", "parameter": {"application": application, "infoId" : infoId },"success": success,"failure": failure});
    },
    listHotPicFilterPage : function(page, count,  filterData, success,failure, async){
        this.actionHotPic.invoke({"name": "listHotPicFilterPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },


    getInternetImageBaseBase64: function(data, success, failure, async){
        this.action.invoke({"name": "getInternetImageBaseBase64", data : data, "success": success,"failure": failure, "async": async});
    },
    convertLocalImageToBase64: function(size, success, failure, formData, file){
        this.action.invoke({"name": "convertLocalImageToBase64", "parameter": {"size": size},"data": formData,"file": file,"success": success,"failure": failure});
    },
    getSubjectAttachmentBase64: function(id, size , success, failure, async){
        this.action.invoke({"name": "getSubjectAttachmentBase64", "parameter": {"id": id, "size" : size },"success": success,"failure": failure, "async": async});
    },

    getRegisterMode: function(success, failure, async){
        this.actionPerson.invoke({"name": "getRegisterMode", "success": success,"failure": failure, "async": async});
    },

    getLoginMode: function(success, failure, async){
        this.actionAuthentication.invoke({"name": "getLoginMode", "success": success,"failure": failure, "async": async});
    },
    logout: function(success, failure, async){
        this.actionAuthentication.invoke({"name": "logout", "success": success,"failure": failure, "async": async});
    },
    authentication: function( success, failure, async){
        this.actionAuthentication.invoke({"name": "authentication", "success": success,"failure": failure, "async": async});
    }
});