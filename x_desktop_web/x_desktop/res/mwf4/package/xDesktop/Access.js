MWF.xDesktop = MWF.xDesktop || {};
MWF.xApplication = MWF.xApplication || {};
MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);

MWF.xDesktop.Access = MWF.AC = {
    "companyList": null,
    "action": null,

    isAdministrator: function(){
        return (layout.desktop.session.user.name.toLowerCase() == "xadmin") || (layout.desktop.session.user.roleList.indexOf("Manager")!=-1);
    },
    isGroupCreator: function(){
        if (!layout.desktop.session.user.roleList) return false;
        return (layout.desktop.session.user.roleList.indexOf("GroupCreator")!=-1);
    },
    isCompanyCreator: function(){
        if (!layout.desktop.session.user.roleList) return false;
        return (layout.desktop.session.user.roleList.indexOf("CompanyCreator")!=-1);
    },
    isProcessPlatformCreator: function(){
        if (this.isAdministrator()) return true;
        if (!layout.desktop.session.user.roleList) return false;
        return (layout.desktop.session.user.roleList.indexOf("ProcessPlatformCreator")!=-1);
    },
    isApplicationManager: function(option){
        if (this.isAdministrator()) {
            if (option.yes) option.yes();
        }else{

        }
    },
    isMeetingAdministrator: function(){
        return this.isAdministrator() || (layout.desktop.session.user.roleList.indexOf("MeetingManager")!=-1);
    },

    isPersonManager: function(){
        if (!layout.desktop.session.user.roleList) return false;
        return (layout.desktop.session.user.roleList.indexOf("PersonManager")!=-1);
    },
    isPersonEditor: function(option){
        //{list: "idlist", "yes": trueFunction, "no": falseFunction}
        if (this.isAdministrator()) return true;
        if (this.isPersonManager()) return true;
        if (option.list && option.list.length){
            if (option.list.indexOf(layout.desktop.session.user.id)!=-1) return true;
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
                    if (this.companyList.indexOf(option.id)!=-1){
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
                    if (this.companyList.indexOf(option.id)!=-1){
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
        if (!this.action) this.action = new MWF.xApplication.Organization.Actions.RestActions();
    }

}