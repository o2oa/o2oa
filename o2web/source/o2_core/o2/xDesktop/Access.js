MWF.xDesktop = MWF.xDesktop || {};
MWF.xApplication = MWF.xApplication || {};
//MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);

MWF.xDesktop.Access = MWF.AC = {
    "companyList": null,
    "action": null,

    getRoleList: function(){
        if (!this.roleList){
            this.roleList = [];
            layout.desktop.session.user.roleList.each(function(role){
                this.roleList.push(role.substring(0, role.indexOf("@")).toLowerCase());
            }.bind(this));
        }
    },
    isAdministrator: function(){
        this.getRoleList();
        return (layout.desktop.session.user.name.toLowerCase() === "xadmin") || (this.roleList.indexOf("manager")!==-1);
    },
    isProcessManager: function(){
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("processplatformmanager")!==-1);
    },
    isPortalManager: function(){
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("portalmanager")!==-1);
    },
    isQueryManager: function(){
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("querymanager")!==-1);
    },
    isOrganizationManager: function(){
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("organizationmanager")!==-1);
    },
    isMessageManager: function(){
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("messagemanager")!==-1);
    },
    isServiceManager: function(){
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("servicemanager")!==-1);
    },

    isUnitManager: function(){
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("unitmanager")!==-1);
    },
    isGroupManager: function(){
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("groupmanager")!==-1);
    },
    isRoleManager: function(){
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("rolemanager")!==-1);
    },
    isPersonManager: function(){
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("personmanager")!==-1);
    },

    isGroupCreator: function(){
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return (this.roleList.indexOf("groupcreator")!==-1);
    },
    isProcessPlatformCreator: function(){
        if (this.isAdministrator()) return true;
        if (this.isProcessManager()) return true;
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return (this.roleList.indexOf("processplatformcreator")!==-1);
    },
    isPortalPlatformCreator: function(){
        if (this.isAdministrator()) return true;
        if (this.isPortalManager()) return true;
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return (this.roleList.indexOf("portalcreator")!==-1);
    },

    isQueryPlatformCreator: function(){
        if (this.isAdministrator()) return true;
        if (this.isQueryManager()) return true;
        if (!layout.desktop.session.user.roleList) return false;
        this.getRoleList();
        return (this.roleList.indexOf("querycreator")!==-1);
    },
    isApplicationManager: function(option){
        if (this.isAdministrator()) {
            if (option.yes) option.yes();
        }else{

        }
    },
    isCMSManager: function(){
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("cmsmanager")!==-1);
    },
    isBBSManager: function(){
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("bbsmanager")!==-1) || (this.roleList.indexOf("bssmanager")!==-1);
    },
    isOKRManager: function(){
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("okrmanager")!==-1);
    },
    isCRMManager: function(){
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("crmmanager")!==-1);
    },
    isAttendanceManager: function(){
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("attendancemanager")!==-1);
    },
    isMeetingAdministrator: function(){
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("meetingmanager")!==-1);
    },
    isHotPictureManager: function(){
        this.getRoleList();
        return this.isAdministrator() || (this.roleList.indexOf("hotpicturemanager")!==-1);
    },
    isPersonEditor: function(option){
        //{list: "idlist", "yes": trueFunction, "no": falseFunction}
        if (this.isAdministrator()) return true;
        if (this.isPersonManager()) return true;
        if (option.list && option.list.length){
            if (option.list.indexOf(layout.desktop.session.user.id)!==-1) return true;
        }
        return false;
    },
    isCompanyEditor: function(option) {
        //{id: "companyId", "yes": trueFunction, "no": falseFunction}
        if (this.isAdministrator()){
            if (option.yes) option.yes();
        }else if (this.isCompanyCreator()){
            if (option.yes) option.yes();
        }else{
            this.getCompanyList(function(){
                if (option.id){
                    if (this.companyList.indexOf(option.id)!==-1){
                        if (option.yes) option.yes();
                    }else{
                        if (option.no) option.no();
                    }
                }else{
                    if (this.companyList.length>0){
                        if (option.yes) option.yes();
                    }else{
                        if (option.no) option.no();
                    }
                }
            }.bind(this));
        }
    },
    isDepartmentEditor: function(option) {
        //{id: "superCompanyId", "yes": trueFunction, "no": falseFunction}
        if (this.isAdministrator()){
            if (option.yes) option.yes();
        }else if (this.isCompanyCreator()){
            if (option.yes) option.yes();
        }else{
            this.getCompanyList(function(){
                if (option.id){
                    if (this.companyList.indexOf(option.id)!==-1){
                        if (option.yes) option.yes();
                    }else{
                        if (option.no) option.no();
                    }
                }else{
                    if (option.no) option.no();
                }
            }.bind(this));
        }
    },

    getCompanyList: function(callback){
        if (this.companyList===null){
            this.getAction();
            this.action.getCompanyAccess(function(json){
                if (json.data){
                    this.companyList = json.data;
                }else{
                    this.companyList = [];
                }
                if (callback) callback();
            }.bind(this), null, layout.desktop.session.user.id);
        }else{
            if (callback) callback();
        }
    },
    getAction: function(){
        if (!this.action) this.action = MWF.Actions.get("x_organization_assemble_control");
        //if (!this.action) this.action = new MWF.xApplication.Organization.Actions.RestActions();
    }

}
