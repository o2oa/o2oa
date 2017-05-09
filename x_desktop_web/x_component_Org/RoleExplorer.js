MWF.xDesktop.requireApp("Org", "PersonExplorer", null, false);
MWF.xApplication.Org.RoleExplorer = new Class({
	Extends: MWF.xApplication.Org.PersonExplorer,
	Implements: [Options, Events],
	options: {
		"style": "default",
        "lp": {

        },
        "creator": false
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
        return new MWF.xApplication.Org.RoleExplorer.Role(data, explorer, this.isEditor);
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
        if (MWF.AC.isAdministrator()){
            this.isEditor = true;
            this.addTopElementNode = new Element("div", {"styles": this.css.addTopGroupNode}).inject(this.toolbarNode);
            this.addTopElementNode.addEvent("click", function(){
                this.addTopElement();
            }.bind(this));
        }
        this.createSearchNode();
        this.loadPingyinArea();
    }
});

MWF.xApplication.Org.RoleExplorer.Role = new Class({
    Extends: MWF.xApplication.Org.PersonExplorer.Item,
    showItemProperty: function(){
        this.content = new MWF.xApplication.Org.RoleExplorer.RoleContent(this);
    },
    "delete": function(success, failure){
        this.explorer.actions.deleteRole(this.data.id, function(){
            this.destroy();
            if (success) success();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);

            if (failure) failure();
        });
    },
    _getIcon: function(){
        return "/x_component_Org/$Explorer/default/icon/role.png";
    }
});
MWF.xApplication.Org.RoleExplorer.RoleContent = new Class({
    Extends: MWF.xApplication.Org.PersonExplorer.ItemContent
});