MWF.xDesktop.requireApp("Org", "PersonExplorer", null, false);
MWF.xApplication.Org.GroupExplorer = new Class({
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
            "elementLoaded": this.app.lp.groupLoaded,
            "search": this.app.lp.search,
            "searchText": this.app.lp.searchText,
            "elementSave": this.app.lp.groupSave,
            "deleteElements": this.app.lp.deleteGroups,
            "deleteElementsCancel": this.app.lp.deleteElementsCancel,

            "deleteElementsTitle": this.app.lp.deleteGroupsTitle,
            "deleteElementsConfirm": this.app.lp.deleteGroupsConfirm,

            "elementBaseText": this.app.lp.groupBaseText,
            "elementName": this.app.lp.groupName,

            "edit": this.app.lp.edit,
            "cancel": this.app.lp.cancel,
            "save": this.app.lp.save,
            "add": this.app.lp.add,

            "inputElementName": this.app.lp.inputGroupName,

            "elementMemberPersonText": this.app.lp.groupMemberPersonText,

            "personEmployee": this.app.lp.personEmployee,
            "personDisplay": this.app.lp.personDisplay,
            "personMail": this.app.lp.personMail,
            "personPhone": this.app.lp.personPhone,

            "deletePersonMemberTitle": this.app.lp.deletePersonMemberTitle,
            "deletePersonMember": this.app.lp.deletePersonMember,

            "elementMemberGroupText": this.app.lp.groupMemberGroupText,

            "groupDescription": this.app.lp.groupDescription,
            "groupName": this.app.lp.groupName,

            "deleteGroupMemberTitle": this.app.lp.deleteGroupMemberTitle,
            "deleteGroupMember": this.app.lp.deleteGroupMember
        }
    },

    _listElementNext: function(lastid, count, callback){
        this.actions.listGroupNext(lastid, count, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this));
    },
    _newElement: function(data, explorer){
        return new MWF.xApplication.Org.GroupExplorer.Group(data, explorer, this.isEditor);
    },
    _listElementByKey: function(callback, failure, key){
        this.actions.listGroupByKey(function(json){
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
    }
});

MWF.xApplication.Org.GroupExplorer.Group = new Class({
    Extends: MWF.xApplication.Org.PersonExplorer.Item,
    showItemProperty: function(){
        this.content = new MWF.xApplication.Org.GroupExplorer.GroupContent(this);
    },
    "delete": function(success, failure){
        this.explorer.actions.deleteGroup(this.data.id, function(){
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
        return "/x_component_Org/$Explorer/default/icon/group.png";
    }
});
MWF.xApplication.Org.GroupExplorer.GroupContent = new Class({
    Extends: MWF.xApplication.Org.PersonExplorer.ItemContent
});