MWF.xDesktop.requireApp("Organization", "GroupExplorer", null, false);
MWF.xApplication.Organization.RoleExplorer = new Class({
	Extends: MWF.xApplication.Organization.GroupExplorer,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
    _loadPath: function(){
        this.path = "/x_component_Organization/$RoleExplorer/";
        this.cssPath = "/x_component_Organization/$RoleExplorer/"+this.options.style+"/css.wcss";
    },
    _loadLp: function(){
        this.options.lp = {
            "elementLoaded": this.app.lp.roleLoaded,
            "search": this.app.lp.search,
            "searchText": this.app.lp.searchText,
            "elementSave": this.app.lp.roleSave,
            "deleteElements": this.app.lp.deleteRoles,

            "deleteElementsTitle": this.app.lp.deleteRolesTitle,
            "deleteElementsConfirm": this.app.lp.deleteRolesConfirm,

            "elementBaseText": this.app.lp.roleBaseText,
            "elementName": this.app.lp.roleName,

            "edit": this.app.lp.edit,
            "cancel": this.app.lp.cancel,
            "save": this.app.lp.save,
            "add": this.app.lp.add,

            "inputElementName": this.app.lp.inputRoleName,

            "elementMemberPersonText": this.app.lp.roleMemberPersonText,

            "personEmployee": this.app.lp.personEmployee,
            "personDisplay": this.app.lp.personDisplay,
            "personMail": this.app.lp.personMail,
            "personPhone": this.app.lp.personPhone,

            "deletePersonMemberTitle": this.app.lp.deletePersonMemberTitle,
            "deletePersonMember": this.app.lp.deletePersonMember,

            "elementMemberGroupText": this.app.lp.roleMemberGroupText,

            "groupDescription": this.app.lp.groupDescription,
            "groupName": this.app.lp.groupName,

            "deleteGroupMemberTitle": this.app.lp.deleteGroupMemberTitle,
            "deleteGroupMember": this.app.lp.deleteGroupMember
        }
    },
    _listElementNext: function(lastid, count, callback){
        this.actions.listRoleNext(lastid, count, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this));
    },
    _newElement: function(data, explorer){
        return new MWF.xApplication.Organization.RoleExplorer.Role(data, explorer, this.isEditor);
    },
    _listElementByKey: function(callback, failure, key){
        this.actions.listRoleByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getAddElementData: function(){
        return {
            "personList": [],
            "groupList": [],
            "id": "",
            "name": ""
        };
    },
    loadToolbar: function(){
        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode}).inject(this.chartAreaNode);
        if (MWF.AC.isAdministrator()){
            this.isEditor = true;
            this.addTopElementNode = new Element("div", {"styles": this.css.addTopGroupNode}).inject(this.toolbarNode);
            this.addTopElementNode.addEvent("click", function(){
                this.addTopElement();
            }.bind(this));
        }
        this.createSearchNode();
    }
});

MWF.xApplication.Organization.RoleExplorer.Role = new Class({
	Extends: MWF.xApplication.Organization.GroupExplorer.Group,
    _saveElement: function(data, success, failure){
        this.explorer.actions.saveRole(data, success, failure);
    },
    _deleteElement: function(id, success, failure){
       this.explorer.actions.deleteRole(id, success, failure);
    }
});
