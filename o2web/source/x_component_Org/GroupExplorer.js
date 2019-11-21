MWF.xDesktop.requireApp("Org", "RoleExplorer", null, false);
MWF.xApplication.Org.GroupExplorer = new Class({
	Extends: MWF.xApplication.Org.$Explorer,
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
            "noSignature": this.app.lp.noSignature,
            "edit": this.app.lp.edit,
            "cancel": this.app.lp.cancel,
            "save": this.app.lp.save,
            "add": this.app.lp.add
        }
    },

    _listElementNext: function(lastid, count, callback){
        this.actions.listGroupNext(lastid || "(0)", count, function(json){
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
            "unitList": [],
            "description": "",
            "unique": "",
            "orderNumber": "",
            "id": "",
            "name": "",
            "control": {
                "allowEdit": true,
                "allowDelete": true
            }
        };
    },
    _isActionManager: function() {
        return (MWF.AC.isOrganizationManager() || MWF.AC.isGroupManager());
    }
});

MWF.xApplication.Org.GroupExplorer.Group = new Class({
    Extends: MWF.xApplication.Org.$Explorer.Item,
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
    Extends: MWF.xApplication.Org.$Explorer.ItemContent,
    _getData: function(callback){
        if (this.item.data.id){
            this.explorer.actions.getGroup(function(json){
                this.data = json.data;
                this.item.data = json.data;
                if (callback) callback();
            }.bind(this), null, this.item.data.id);
        }else{
            this.data = this.item.data;
            if (callback) callback();
        }
    },
    _showItemPropertyTitle: function(){
        this.titleInfor = new MWF.xApplication.Org.GroupExplorer.GroupContent.TitleInfor(this);
    },
    _showItemPropertyBottom: function(){
        this.bottomInfor = new MWF.xApplication.Org.GroupExplorer.GroupContent.BottomInfor(this);
    },
    loadItemPropertyTab: function(callback){
        this.propertyTabContainerNode = new Element("div", {"styles": this.item.style.tabTitleNode}).inject(this.propertyContentNode, "top");
        MWF.require("MWF.widget.Tab", function(){
            this.propertyTab = new MWF.widget.Tab(this.propertyContentNode, {"style": "unit"});
            this.propertyTab.load();

            this.propertyTab.tabNodeContainer.inject(this.propertyTabContainerNode);

            if (callback) callback();
        }.bind(this));
    },

    _loadTabs: function(){
        this.baseContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.basePage = this.propertyTab.addTab(this.baseContentNode, this.explorer.app.lp.groupBaseText);

        this.personMemberContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.personMemberPage = this.propertyTab.addTab(this.personMemberContentNode, this.explorer.app.lp.groupMemberPersonText);

        this.groupMemberContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.groupMemberPage = this.propertyTab.addTab(this.groupMemberContentNode, this.explorer.app.lp.groupMemberGroupText);

        this.unitMemberContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.unitMemberPage = this.propertyTab.addTab(this.unitMemberContentNode, this.explorer.app.lp.unitMemberGroupText);
    },
    _loadContent: function(){
        debugger;
        this._listBaseInfor();
        this.loadListCount();
        var _self = this;
        this.personMemberList = this._listMembers("personList", "woPersonList", this.personMemberContentNode, [{
            "getHtml": function(){
                var src = _self.explorer.actions.getPersonIcon(this.id);
                return "<div style='width:24px; height:24px;''><img style='width:24px; height:24px; border-radius:12px; border: 0' src='"+src+"'/></div>";
            },
            "set": function(){}
        }, "name", "employee", "mobile", "mail", {
            "getHtml": function(){
                return "<div style='width:24px; height:24px; cursor: pointer; background:url(/x_component_Org/$Explorer/"+
                    _self.explorer.app.options.style+"/icon/open.png) center center no-repeat'></div>";
            },
            "events": {
                "click": function(){
                    _self.explorer.openPerson(this.data, this.td);
                }
            }
        }], [
            {"style": "width: 30px", "text": ""},
            {"style": "width: 20%", "text": this.explorer.app.lp.personName},
            {"style": "", "text": this.explorer.app.lp.personEmployee},
            {"style": "", "text": this.explorer.app.lp.personMobile},
            {"style": "", "text": this.explorer.app.lp.personMail},
            {"style": "width: 30px", "text": ""}
        ], this.addPersonMember.bind(this), "personCountNode", this.explorer.app.lp.deletePersonMemeberTitle, this.explorer.app.lp.deletePersonMemeber);

        this.groupMemberList = this._listMembers("groupList", "woGroupList", this.groupMemberContentNode, ["name", "distinguishedName",  //"description",
            {
                "getHtml": function(){
                    return "<div style='width:24px; height:24px; cursor: pointer; background:url(/x_component_Org/$Explorer/"+
                        _self.explorer.app.options.style+"/icon/open.png) center center no-repeat'></div>";
                },
                "events": {
                    "click": function(){
                        _self.explorer.openGroup(this.data, this.td);
                    }
                }
            }
        ], [
            {"style": "width: 20%", "text": this.explorer.app.lp.groupName},
            {"style": "width: 40%", "text": this.explorer.app.lp.groupDn},
            //{"style": "", "text": this.explorer.app.lp.groupDescription},
            {"style": "width: 30px", "text": ""}
        ], this.addGroupMember.bind(this), "groupCountNode", this.explorer.app.lp.deleteGroupMemeberTitle, this.explorer.app.lp.deleteGroupMemeber);

        this.unitMemberList = this._listMembers("unitList", "woUnitList", this.unitMemberContentNode, ["name", "levelName", //"typeList",
            {
            "getHtml": function(){
                return "<div style='width:24px; height:24px; cursor: pointer; background:url(/x_component_Org/$Explorer/"+
                    _self.explorer.app.options.style+"/icon/open.png) center center no-repeat'></div>";
            },
            "events": {
                "click": function(){
                    _self.explorer.openUnit(this.data, this.td);
                }
            }
        }], [
            {"style": "width: 20%", "text": this.explorer.app.lp.unitName},
            {"style": "width: 40%", "text": this.explorer.app.lp.unitLevelName},
            //{"style": "", "text": this.explorer.app.lp.unitTypeList},
            {"style": "width: 30px", "text": ""}
        ], this.addUnitMember.bind(this), "unitCountNode", this.explorer.app.lp.deleteUnitMemeberTitle, this.explorer.app.lp.deleteUnitMemeber);
    },
    loadListCount: function(){
        var personCount = this.data.personList.length;
        if (personCount){
            if (this.personCountNode){
                this.personCountNode.set("text", personCount);
            }else{
                this.personCountNode = new Element("div", {"styles": this.item.style.tabCountNode, "text": personCount}).inject(this.personMemberPage.tabNode);
            }
        }else{
            if (this.personCountNode) this.personCountNode.destroy();
        }
        var groupCount = this.data.groupList.length;
        if (groupCount){
            if (this.groupCountNode){
                this.groupCountNode.set("text", groupCount);
            }else{
                this.groupCountNode = new Element("div", {"styles": this.item.style.tabCountNode, "text": groupCount}).inject(this.groupMemberPage.tabNode);
            }
        }else{
            if (this.groupCountNode) this.groupCountNode.destroy();
        }
        var unitCount = this.data.unitList.length;
        if (unitCount){
            if (this.unitCountNode){
                this.unitCountNode.set("text", unitCount);
            }else{
                this.unitCountNode = new Element("div", {"styles": this.item.style.tabCountNode, "text": unitCount}).inject(this.unitMemberPage.tabNode);
            }
        }else{
            if (this.unitCountNode) this.unitCountNode.destroy();
        }


    },
    _listBaseInfor: function(){
        this.baseInfor = new MWF.xApplication.Org.GroupExplorer.GroupContent.BaseInfor(this);
    },
    _listMembers: function(list, woList, node, attr, titles, addItemFun, countNode, deleteTitle, deleteText){
        var memberList = new MWF.xApplication.Org.List(node, this, {
            "action": this.data.control.allowEdit,
            "canEdit": false,
            "deleteItemTitle": deleteTitle,
            "deleteItemText": deleteText,
            "data": {
                "person": this.data.id,
                "name": "",
                "unique": "",
                "orderNumber": "",
                "attributeList": []
            },
            "attr": attr,
            "onQueryDelete": function(){
                this.saveCloneData = Object.clone(this.data);
            }.bind(this),
            "onDelete": function(continueDelete){
                this.explorer.actions.saveGroup(this.saveCloneData, function(json){
                    this.data[list] = this.saveCloneData[list];
                    this.data[woList] = this.saveCloneData[woList];
                    this.data.id = json.data.id;
                    this.saveCloneData = null;
                    delete this.saveCloneData;
                }.bind(this), function(xhr, text, error){
                    continueDelete = false;
                    this.explorer.app.notice((JSON.decode(xhr.responseText).message.trim() || "request json error"), "error");
                }.bind(this), false);
            }.bind(this),
            "onPostDelete": function(delCount){
                if (this[countNode]){
                    var count = this[countNode].get("text").toInt()-delCount;
                    this[countNode].set("text", count);
                }
            }.bind(this)
        });
        memberList.addItem = addItemFun;
        memberList.load(titles);
        var _self = this;
        if (this.data[woList] && this.data[woList].length){
            this.data[woList].each(function(d){
                var item = memberList.push(d);
                item["delete"] = function(callback){
                    _self.saveCloneData[list].erase(this.data.id);
                    _self.saveCloneData[woList] = _self.saveCloneData[woList].filter(function(a){
                        return (this.data.id !== a.id);
                    }.bind(this));
                    if (callback) callback();
                };
            }.bind(this));
        }
        return memberList;
    },
    addListItem: function(memberList, data, list, woList){
        var _self = this;
        var item = memberList.push(data);
        item["delete"] = function(callback){
            _self.saveCloneData[list].erase(this.data.id);
            _self.saveCloneData[woList] = _self.saveCloneData[woList].filter(function(a){
                return (this.data.id !== a.id);
            }.bind(this));
            if (callback) callback();
        };
    },
    checkSaveBaseInfor: function(callback){
        if (!this.data.id){
            if (this.baseInfor){
                if (this.baseInfor.mode==="edit") this.baseInfor.save(function(){
                    if (callback) callback();
                }.bind(this));
            }
        }else{
            if (callback) callback();
        }
    },
    addPersonMember: function(){
        this.checkSaveBaseInfor(function(){
            MWF.xDesktop.requireApp("Selector", "Person", function(){
                var selector = new MWF.xApplication.Selector.Person(this.explorer.app.content,{
                    "values": this.data.personList,
                    "onComplete": function(items){
                        var ids = [];
                        var persons = [];
                        items.each(function(item){
                            ids.push(item.data.id);
                            persons.push(item.data);
                        });
                        this.data.personList = ids;
                        this.data.woPersonList = persons;

                        this._saveElement(this.data, function(){
                            this.personMemberList.clear();
                            this.data.woPersonList.each(function(d){
                                this.addListItem(this.personMemberList, d, "personList", "woPersonList");
                            }.bind(this));
                        }.bind(this));
                    }.bind(this)
                });
                selector.load();
            }.bind(this));
        }.bind(this));
    },
    addGroupMember: function(){
        this.checkSaveBaseInfor(function(){
            MWF.xDesktop.requireApp("Selector", "Group", function(){
                var selector = new MWF.xApplication.Selector.Group(this.explorer.app.content,{
                    "values": this.data.groupList,
                    "onComplete": function(items){
                        var ids = [];
                        var groups = [];
                        items.each(function(item){
                            ids.push(item.data.id);
                            groups.push(item.data);
                        });
                        this.data.groupList = ids;
                        this.data.woGroupList = groups;

                        this._saveElement(this.data, function(){
                            this.groupMemberList.clear();
                            this.data.woGroupList.each(function(d){
                                this.addListItem(this.groupMemberList, d, "groupList", "woGroupList");
                            }.bind(this));
                        }.bind(this));
                    }.bind(this)
                });
                selector.load();
            }.bind(this));
        }.bind(this));
    },
    addUnitMember: function(){
        this.checkSaveBaseInfor(function(){
            MWF.xDesktop.requireApp("Selector", "Unit", function(){
                var selector = new MWF.xApplication.Selector.Unit(this.explorer.app.content,{
                    "values": this.data.unitList,
                    "onComplete": function(items){
                        var ids = [];
                        var groups = [];
                        items.each(function(item){
                            ids.push(item.data.id);
                            groups.push(item.data);
                        });
                        this.data.unitList = ids;
                        this.data.woUnitList = groups;

                        this._saveElement(this.data, function(){
                            this.unitMemberList.clear();
                            this.data.woUnitList.each(function(d){
                                this.addListItem(this.unitMemberList, d, "unitList", "woUnitList");
                            }.bind(this));
                        }.bind(this));
                    }.bind(this)
                });
                selector.load();
            }.bind(this));
        }.bind(this));
    },

    _saveElement: function(data, success, failure){
        this.explorer.actions.saveGroup(data, function(json){
            Object.merge(this.data, data);
            if (this.data.id){
                this.data.id = json.data.id;
                this.item.refresh();
                if (success) success();
            }else{
                this.explorer.actions.getGroup(function(json){
                    this.data = json.data;
                    this.item.refresh();
                    if (success) success();
                }.bind(this), null, json.data.id);
            }
        }.bind(this), function(xhr, text, error){
            if (failure) failure(xhr, text, error);
        }.bind(this));
    }
});

MWF.xApplication.Org.GroupExplorer.GroupContent.TitleInfor = new Class({
    Extends: MWF.xApplication.Org.RoleExplorer.RoleContent.TitleInfor,
    _getIcon: function(){
        return "/x_component_Org/$Explorer/default/icon/group70.png";
    }
});

MWF.xApplication.Org.GroupExplorer.GroupContent.BottomInfor = new Class({
    Extends: MWF.xApplication.Org.$Explorer.ItemContent.BottomInfor,
    addInforList: function(){
        var text = this.explorer.app.lp.groupReadDn.replace(/{dn}/g, (this.data.distinguishedName || " "));
        this.addInfor(text);

        text = this.explorer.app.lp.groupReadCreate.replace(/{date}/g, (this.data.createTime || " "));
        text = text.replace(/{date2}/g, (this.data.updateTime || " "));
        this.addInfor(text);
    }
});
MWF.xApplication.Org.GroupExplorer.GroupContent.BaseInfor = new Class({
    Extends: MWF.xApplication.Org.RoleExplorer.RoleContent.BaseInfor,

    getContentHtml: function(){
        var html = "<table width='100%' cellpadding='3px' cellspacing='5px'>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.groupName+":</td><td class='inforContent infor_name'></td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.groupUnique+":</td><td class='inforContent infor_unique'></td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.groupDescription+":</td><td colspan='3' class='inforContent infor_description'></td>";
        html += "<tr><td colspan='4' class='inforAction'></td></tr>";
        //this.baseInforRightNode.set("html", html);
        return html;
    },
    loadAction: function(){
        //this.explorer.app.lp.edit
        var actionAreas = this.editContentNode.getElements("td");
        var actionArea = actionAreas[actionAreas.length-1];

        if (this.data.control.allowEdit){
            this.baseInforEditActionAreaNode = new Element("div", {"styles": this.style.baseInforEditActionAreaNode}).inject(actionArea);

            this.editNode = new Element("div", {"styles": this.style.actionEditNode, "text": this.explorer.app.lp.editGroup}).inject(this.baseInforEditActionAreaNode);
            this.saveNode = new Element("div", {"styles": this.style.actionSaveNode, "text": this.explorer.app.lp.saveGroup}).inject(this.baseInforEditActionAreaNode);
            this.cancelNode = new Element("div", {"styles": this.style.actionCancelNode, "text": this.explorer.app.lp.cancel}).inject(this.baseInforEditActionAreaNode);

            this.editNode.setStyle("display", "block");
            this.editNode.addEvent("click", this.edit.bind(this));
            this.saveNode.addEvent("click", this.save.bind(this));
            this.cancelNode.addEvent("click", this.cancel.bind(this));
        }else{

        }
    },
    save: function(){
        if (!this.nameInputNode.get("value")){
            this.explorer.app.notice(this.explorer.app.lp.inputGroupInfor, "error", this.explorer.propertyContentNode);
            return false;
        }
        //this.data.genderType = gender;
        if (!this.uniqueInputNode.get("value")) this.data.unique = this.nameInputNode.get("value");
        this.content.propertyContentScrollNode.mask({
            "style": {
                "opacity": 0.7,
                "background-color": "#999"
            }
        });

        this.saveGroup(function(){
            this.cancel();
            this.content.propertyContentScrollNode.unmask();
        }.bind(this), function(xhr, text, error){
            this.explorer.app.notice((JSON.decode(xhr.responseText).message.trim() || "request json error"), "error");
            this.content.propertyContentScrollNode.unmask();
        }.bind(this));
    },
    saveGroup: function(callback, cancel){
        var data = Object.clone(this.data);
        data.name = this.nameInputNode.get("value");
        data.unique = this.uniqueInputNode.get("value");
        data.description = this.descriptionInputNode.get("value");

        this.explorer.actions.saveGroup(data, function(json){
            Object.merge(this.data, data);
            if (this.data.id){
                this.data.id = json.data.id;
                this.item.refresh();
                if (callback) callback();
            }else{
                this.explorer.actions.getGroup(function(json){
                    this.data = Object.merge(this.data, json.data);
                    this.item.data = this.data;
                    this.item.refresh();
                    if (callback) callback();
                }.bind(this), null, json.data.id);
            }
        }.bind(this), function(xhr, text, error){
            if (cancel) cancel(xhr, text, error);
        }.bind(this));
    },
    destroy: function(){
        this.node.empty();
        this.node.destroy();
        MWF.release(this);
    }
});