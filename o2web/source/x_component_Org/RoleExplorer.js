MWF.xDesktop.requireApp("Org", "$Explorer", null, false);
MWF.xApplication.Org.RoleExplorer = new Class({
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
            "elementLoaded": this.app.lp.roleLoaded,
            "search": this.app.lp.search,
            "searchText": this.app.lp.searchText,
            "elementSave": this.app.lp.roleSave,
            "deleteElements": this.app.lp.deleteRoles,
            "deleteElementsCancel": this.app.lp.deleteElementsCancel,
            "deleteElementsTitle": this.app.lp.deleteRolesTitle,
            "deleteElementsConfirm": this.app.lp.deletePersonsConfirm,
            "elementBaseText": this.app.lp.roleBaseText,
            "elementName": this.app.lp.roleName,
            "noSignature": this.app.lp.noSignature,
            "edit": this.app.lp.edit,
            "cancel": this.app.lp.cancel,
            "save": this.app.lp.save,
            "add": this.app.lp.add
        }
    },

    _listElementNext: function(lastid, count, callback){
        this.actions.listRoleNext(lastid || "(0)", count, function(json){
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
    loadToolbar: function(){
        if (this._isActionManager()){
            this.isEditor = true;
            this.addTopElementNode = new Element("div", {"styles": this.css.addTopGroupNode}).inject(this.toolbarNode);
            this.addTopElementNode.addEvent("click", function(){
                this.addTopElement();
            }.bind(this));
        }
        this.createSearchNode();
        this.loadPingyinArea();
    },
    _isActionManager: function(){
        return (MWF.AC.isOrganizationManager() || MWF.AC.isRoleManager());
    }
});

MWF.xApplication.Org.RoleExplorer.Role = new Class({
    Extends: MWF.xApplication.Org.$Explorer.Item,
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
    Extends: MWF.xApplication.Org.$Explorer.ItemContent,
    _getData: function(callback){
        if (this.item.data.id){
            this.explorer.actions.getRole(function(json){
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
        this.titleInfor = new MWF.xApplication.Org.RoleExplorer.RoleContent.TitleInfor(this);
        //this.baseInfor = new MWF.xApplication.Org.BaseInfor(this);
    },
    _showItemPropertyBottom: function(){
        this.bottomInfor = new MWF.xApplication.Org.RoleExplorer.RoleContent.BottomInfor(this);
    },
    _loadTabs: function(){
        this.baseContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.basePage = this.propertyTab.addTab(this.baseContentNode, this.explorer.app.lp.roleBaseText);

        this.personMemberContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.personMemberPage = this.propertyTab.addTab(this.personMemberContentNode, this.explorer.app.lp.rolePersonMembers);

        this.groupMemberContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.groupMemberPage = this.propertyTab.addTab(this.groupMemberContentNode, this.explorer.app.lp.roleGroupMembers);

    },
    _loadContent: function(){
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

        this.groupMemberList = this._listMembers("groupList", "woGroupList", this.groupMemberContentNode, ["name", "distinguishedName", "description", {
            "getHtml": function(){
                return "<div style='width:24px; height:24px; cursor: pointer; background:url(/x_component_Org/$Explorer/"+
                    _self.explorer.app.options.style+"/icon/open.png) center center no-repeat'></div>";
            },
            "events": {
                "click": function(){
                    _self.explorer.openGroup(this.data, this.td);
                }
            }
        }], [
            {"style": "width: 20%", "text": this.explorer.app.lp.groupName},
            {"style": "width: 40%", "text": this.explorer.app.lp.groupDn},
            {"style": "", "text": this.explorer.app.lp.groupDescription},
            {"style": "width: 30px", "text": ""}
        ], this.addGroupMember.bind(this), "groupCountNode", this.explorer.app.lp.deleteGroupMemeberTitle, this.explorer.app.lp.deleteGroupMemeber);
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
    },
    _listBaseInfor: function(){
        this.baseInfor = new MWF.xApplication.Org.RoleExplorer.RoleContent.BaseInfor(this);
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
                this.explorer.actions.saveRole(this.saveCloneData, function(json){
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

        if (this.data[woList] && this.data[woList].length){
            this.data[woList].each(function(d){
                this.addListItem(memberList, d, list, woList);
                // var item = memberList.push(d);
                // item["delete"] = function(callback){
                //     debugger;
                //     _self.saveCloneData[list].erase(this.data.id);
                //     _self.saveCloneData[woList] = _self.saveCloneData[woList].filter(function(a){
                //         return (this.data.id !== a.id);
                //     }.bind(this));
                //     if (callback) callback();
                // };
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
                            this.loadListCount();
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
                            this.loadListCount();
                        }.bind(this));
                    }.bind(this)
                });
                selector.load();
            }.bind(this));
        }.bind(this));
    },

    _saveElement: function(data, success, failure){
        this.explorer.actions.saveRole(data, function(json){
            Object.merge(this.data, data);
            if (this.data.id){
                this.data.id = json.data.id;
                this.item.refresh();
                if (success) success();
            }else{
                this.explorer.actions.getRole(function(json){
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
MWF.xApplication.Org.RoleExplorer.RoleContent.TitleInfor = new Class({
    Extends: MWF.xApplication.Org.$Explorer.ItemContent.TitleInfor,
    _getStyle: function(){
        var css = Object.clone(this.item.style.person);
        return Object.merge(css, this.item.style.role);
    },
    _getIcon: function(){
        return "/x_component_Org/$Explorer/default/icon/role70.png";
    },
    setBackground: function(){
        this.titleBgNode.setStyle("background-image", "url(/x_component_Org/$Explorer/"+this.explorer.app.options.style+"/icon/group_bg_bg.png)");
        this.titleNode.setStyle("background-image", "url(/x_component_Org/$Explorer/"+this.explorer.app.options.style+"/icon/group_bg.png)");
    },
    loadRightInfor: function(){
        //var text = this.data.name+((this.data.unique) ? "（"+this.data.unique+"）" : "");
        var text = this.data.name;
        if (!this.nameNode) this.nameNode = new Element("div", {"styles": this.style.titleInforNameNode}).inject(this.titleInforRightNode);
        if (!this.signatureNode) this.signatureNode = new Element("div", {"styles": this.style.titleInforSignatureNode}).inject(this.titleInforRightNode);
        this.nameNode.set("text", text);
        this.signatureNode.set("text", (this.data.distinguishedName || "" ));
    }
});

MWF.xApplication.Org.RoleExplorer.RoleContent.BottomInfor = new Class({
    Extends: MWF.xApplication.Org.$Explorer.ItemContent.BottomInfor,
    addInforList: function(){
        var text = this.explorer.app.lp.roleReadDn.replace(/{dn}/g, (this.data.distinguishedName || " "));
        this.addInfor(text);

        text = this.explorer.app.lp.roleReadCreate.replace(/{date}/g, (this.data.createTime || " "));
        text = text.replace(/{date2}/g, (this.data.updateTime || " "));
        this.addInfor(text);
    }
});
MWF.xApplication.Org.RoleExplorer.RoleContent.BaseInfor = new Class({
    initialize: function(content){
        this.content = content;
        this.item = content.item;
        this.data = this.content.data;
        this.explorer = this.item.explorer;
        this.contentNode = this.content.baseContentNode;
        this.style = this.item.style.person;
        this.mode = "read";
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.style.baseContentNode}).inject(this.contentNode);
        this.editContentNode = new Element("div", {"styles": this.style.baseEditNode}).inject(this.node);

        this.editContentNode.set("html", this.getContentHtml());
        this.editContentNode.getElement(".infor_name").set("text", this.data.name || "");
        this.editContentNode.getElement(".infor_unique").set("text", this.data.unique || "");
        this.editContentNode.getElement(".infor_description").set("text", this.data.description || "");

        this.editContentNode.getElements("td.inforTitle").setStyles(this.style.baseInforTitleNode);
        this.editContentNode.getElements("td.inforContent").setStyles(this.style.baseInforContentNode);
        this.editContentNode.getElements("td.inforAction").setStyles(this.style.baseInforActionNode);
        this.loadAction();
    },
    getContentHtml: function(){
        var html = "<table width='100%' cellpadding='3px' cellspacing='5px'>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.roleName+":</td><td class='inforContent infor_name'></td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.roleUnique+":</td><td class='inforContent infor_unique'></td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.roleDescription+":</td><td colspan='3' class='inforContent infor_description'></td>";
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

            this.editNode = new Element("div", {"styles": this.style.actionEditNode, "text": this.explorer.app.lp.editRole}).inject(this.baseInforEditActionAreaNode);
            this.saveNode = new Element("div", {"styles": this.style.actionSaveNode, "text": this.explorer.app.lp.saveRole}).inject(this.baseInforEditActionAreaNode);
            this.cancelNode = new Element("div", {"styles": this.style.actionCancelNode, "text": this.explorer.app.lp.cancel}).inject(this.baseInforEditActionAreaNode);

            this.editNode.setStyle("display", "block");
            this.editNode.addEvent("click", this.edit.bind(this));
            this.saveNode.addEvent("click", function(){this.save();}.bind(this));
            this.cancelNode.addEvent("click", this.cancel.bind(this));
        }else{

        }
    },
    edit: function(){
        var tdContents = this.editContentNode.getElements("td.inforContent");
        tdContents[0].setStyles(this.style.baseInforContentNode_edit).empty();
        this.nameInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[0]);
        this.nameInputNode.set("value", (this.data.name));

        tdContents[1].setStyles(this.style.baseInforContentNode_edit).empty();
        this.uniqueInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[1]);
        this.uniqueInputNode.set("value", (this.data.unique));

        tdContents[2].setStyles(this.style.baseInforContentNode_edit).empty();
        this.descriptionInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[2]);
        this.descriptionInputNode.set("value", (this.data.description));

        var _self = this;
        this.editContentNode.getElements("input").addEvents({
            "focus": function(){if (this.get("type").toLowerCase()==="text"){this.setStyles(_self.style.inputNode_focus);}},
            "blur": function(){if (this.get("type").toLowerCase()==="text"){this.setStyles(_self.style.inputNode_blur);}}
        });

        this.mode = "edit";

        this.editNode.setStyle("display", "none");
        this.saveNode.setStyle("display", "block");
        this.cancelNode.setStyle("display", "block");
    },
    save: function(callback){
        if (!this.nameInputNode.get("value")){
            this.explorer.app.notice(this.explorer.app.lp.inputRoleInfor, "error", this.explorer.propertyContentNode);
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

        this.saveRole(function(){
            this.cancel();
            this.content.propertyContentScrollNode.unmask();
            if (callback) callback();
        }.bind(this), function(xhr, text, error){
            this.explorer.app.notice((JSON.decode(xhr.responseText).message.trim() || "request json error"), "error");
            this.content.propertyContentScrollNode.unmask();
        }.bind(this));
    },
    saveRole: function(callback, cancel){
        var data = Object.clone(this.data);
        data.name = this.nameInputNode.get("value");
        data.unique = this.uniqueInputNode.get("value");
        data.description = this.descriptionInputNode.get("value");

        this.explorer.actions.saveRole(data, function(json){
            Object.merge(this.data, data);
            if (this.data.id){
                this.data.id = json.data.id;
                this.item.refresh();
                if (callback) callback();
            }else{
                this.explorer.actions.getRole(function(json){
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
    cancel: function(){
        if (this.data.id){
            var tdContents = this.editContentNode.getElements("td.inforContent");
            tdContents[0].setStyles(this.style.baseInforContentNode).set("text", this.data.name || "");
            tdContents[1].setStyles(this.style.baseInforContentNode).set("text", this.data.unique || "");
            tdContents[2].setStyles(this.style.baseInforContentNode).set("text", this.data.description || "");

            this.mode = "read";

            this.editNode.setStyle("display", "block");
            this.saveNode.setStyle("display", "none");
            this.cancelNode.setStyle("display", "none");
        }else{
            this.item.destroy();
        }
    },

    // getGenderType: function(){
    //     var text = "";
    //     if (this.data.genderType){
    //         switch (this.data.genderType) {
    //             case "m":
    //                 text = this.explorer.app.lp.man;
    //                 break;
    //             case "f":
    //                 text = this.explorer.app.lp.female;
    //                 break;
    //             default:
    //                 text = this.explorer.app.lp.other;
    //         }
    //     }
    //     return text;
    // },
    destroy: function(){
        this.node.empty();
        this.node.destroy();
        MWF.release(this);
    }
    // _getIcon: function(){
    //     var src = "data:image/png;base64,"+this.data.icon;
    //     if (!this.data.icon){
    //         if (this.data.genderType==="f"){
    //             src = "/x_component_Org/$Explorer/default/icon/female.png"
    //         }else{
    //             src = "/x_component_Org/$Explorer/default/icon/man.png"
    //         }
    //     }
    //     return src;
    // }
});