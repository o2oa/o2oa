MWF.require("MWF.widget.O2Identity", null, false);
MWF.xDesktop.requireApp("Org", "$Explorer", null, false);
MWF.xApplication.Org.UnitExplorer = new Class({
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
            "search": this.app.lp.search,
            "searchText": this.app.lp.searchText,
            "elementSave": this.app.lp.organizationSave,
            "deleteElements": this.app.lp.deleteOrganization,
            "deleteElementsCancel": this.app.lp.deleteElementsCancel,
            "deleteElementsTitle": this.app.lp.deleteOrganizationTitle,
            "deleteElementsConfirm": this.app.lp.deleteOrganizationSubConfirm,
            "noSignature": this.app.lp.noSignature,
            "edit": this.app.lp.edit,
            "cancel": this.app.lp.cancel,
            "save": this.app.lp.save,
            "add": this.app.lp.add
        }
    },
    loadElements: function(addToNext){
        if (!this.isElementLoaded){
            if (!this.loaddingElement){
                this.loaddingElement = true;
                this._listElementNext(function(json){
                    if (json.data.length){
                        this.loadListContent(json.data);
                    }else{
                        if (!this.elements.length){
                            this.setNoGroupNoticeArea();
                        }
                    }
                    this.loadElementQueue = 0;
                    this.isElementLoaded = true;
                    this.loaddingElement = false;
                }.bind(this));
            }else{
                if (addToNext) this.loadElementQueue++;
            }
        }
    },
    loadListContent: function(data){
        data.each(function(itemData, i){
            var item = this._newElement(itemData, this, i);
            this.elements.push(item);
            item.load();
            if (this.elements.length===1){
                item.selected();
                if (!item.isExpand) item.expand();
            }

        }.bind(this));
    },
    _listElementNext: function(callback){
        if (MWF.AC.isOrganizationManager()){
            this.actions.listTopUnit(function(json){
                if (callback) callback.apply(this, [json]);
            }.bind(this));
        }else{
            if (layout.session.user.identityList.length){
                var json = {"data": []};
                var unitNames = [];
                layout.session.user.identityList.each(function(id){
                    var idFlag = (id.distinguishedName || id.id || id.unique || id.name);
                    o2.Actions.get("x_organization_assemble_express").getUnitWithIdentityAndLevel({"identity": idFlag, "level": 1}, function(o){
                        if (o.data){
                            this.actions.getUnit(o.data.distinguishedName, function(u){
                                if (unitNames.indexOf(u.data.distinguishedName)==-1){
                                    unitNames.push(u.data.distinguishedName);
                                    json.data.push(u.data);
                                }
                            }.bind(this),null, false);
                        }
                    }.bind(this), null, false);
                }.bind(this));
                if (callback) callback.apply(this, [json]);
            }
        }
    },
    _newElement: function(data, explorer){
        return new MWF.xApplication.Org.UnitExplorer.Unit(data, explorer, this.isEditor, 0);
    },
    _listElementByKey: function(callback, failure, key){
        this.actions.listUnitByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getAddElementData: function(){
        return {
            "name": "",
            "unique": "",
            "typeList": ["company"],
            "description": "",
            "shortName": "",
            "superior": "",
            "orderNumber": "",
            "controllerList": [],
            "control": {
                "allowEdit": true,
                "allowDelete": true
            },
            "woSubDirectIdentityList": [],
            "woUnitAttributeList": [],
            "woUnitDutyList": []
        };
    },
    deleteSelectedElements: function(e){
        var _self = this;
        this.app.confirm("infor", e, this.options.lp.deleteElementsTitle, {"html": this.options.lp.deleteElementsConfirm}, 500, 260, function(){
            var deleted = [];
            var doCount = 0;
            var readyCount = _self.deleteElements.length;
            var errorText = "";

            var complete;
            complete = function () {
                if (doCount === readyCount) {
                    if (errorText) {
                        _self.app.notice(errorText, "error", _self.propertyContentNode, {x: "left", y: "top"});
                    }
                }
            };
            _self.deleteElements.each(function(element){
                element["delete"](function(){
                    deleted.push(element);
                    doCount++;
                    if (_self.deleteElements.length===doCount){
                        _self.deleteElements = _self.deleteElements.filter(function (item) {
                            return !deleted.contains(item);
                        });
                        _self.checkDeleteElements();
                    }
                    complete();
                }, function(error){
                    errorText = (errorText) ? errorText+"<br/><br/>"+error : error;
                    doCount++;
                    if (_self.deleteElements.length !== doCount) {
                    } else {
                        _self.deleteElements = _self.deleteElements.filter(function (item) {
                            return !deleted.contains(item);
                        });
                        _self.checkDeleteElements();
                    }
                    complete();
                });
            });
            this.close();
        }, function(){
            this.close();
        });
    }
});

MWF.xApplication.Org.UnitExplorer.Unit = new Class({
    Extends: MWF.xApplication.Org.$Explorer.Item,
    initialize: function(data, explorer, isEditor, i, listNode, parent){
        this.i = i;
        this.level = i;
        this.parent = parent;
        this.data = data;
        this.explorer = explorer;
        this.listNode = listNode || this.explorer.listNode;
        this.propertyContentNode = this.explorer.propertyContentNode;
        this.initStyle();

        this.selectedAttributes = [];

        this.isEdit = false;
        this.isEditor = isEditor;
        this.deleteSelected = false;

        this.subUnits = [];
    },
    refresh: function(){
        this._loadTextNode();
        if (this.content){
            if (this.content.titleInfor) this.content.titleInfor.refresh();
            if (this.content.bottomInfor) this.content.bottomInfor.refresh();
        }
        this.addActions();
    },
    initStyle: function(){
        var css = Object.clone(this.explorer.css.item);
        this.style = Object.merge(css, this.explorer.css.unitItem);
    },
    _loadTextNode: function(){
        this.textNode.set({"text": this.data.name+((this.data.subDirectUnitCount) ? " ("+(this.data.subDirectUnitCount)+")" : "")});
    },
    load: function(){
        this.node = new Element("div", {"styles": this.style.node}).inject(this.listNode);
        this.contentNode = new Element("div", {"styles": this.style.contentNode}).inject(this.node);
        var left = (10*this.level);
        this.contentNode.setStyle("padding-left", ""+left+"px");

        if ((this.level%2)===1){
            this.node.setStyle("background-color", "#ffffff");
            this.contentNode.setStyle("background-color", "#ffffff");
        }

        this.childNode = new Element("div", {"styles": this.style.childNode}).inject(this.node);

        this.toggleIconNode = new Element("div", {"styles": this.style.unitToggleIconNode}).inject(this.contentNode);
        this.setToggleIconNode();
        this.setToggleAction();

        this.iconNode = new Element("div", {"styles": this.style.unitIconNode}).inject(this.contentNode);
        var src = this._getIcon();
        this.iconNode.setStyle("background-image", "url("+src+")");

        this.actionNode = new Element("div", {"styles": this.style.actionNode}).inject(this.contentNode);

        this.textNode = new Element("div", {"styles": this.style.unitTextNode}).inject(this.contentNode);
        this._loadTextNode();

        this.setNewItem();

        this.node.inject(this.listNode);

        this.addActions();
        this.setEvent();
    },
    addActions: function(){

        //if (this.isEditor){
            if (this.data.id){
                if (this.data.control.allowDelete){
                    if (!this.deleteNode){
                        this.deleteNode = new Element("div", {"styles": this.style.actionDeleteNode}).inject(this.actionNode);
                        this.deleteNode.addEvent("click", function(e){
                            if (!this.notDelete){
                                if (!this.deleteSelected){
                                    this.setDelete();
                                }else{
                                    this.setUndelete();
                                }
                            }
                            e.stopPropagation();
                        }.bind(this));
                    }
                }
                if (this.data.control.allowEdit){
                    if (!this.addNode){
                        this.addNode = new Element("div", {"styles": this.style.actionAddNode}).inject(this.actionNode);
                        this.addNode.addEvent("click", function(e){
                            if (!this.notDelete){
                                this.addSubUnit();
                            }
                            e.stopPropagation();
                        }.bind(this));
                    }
                }
                if (this.explorer.currentItem===this){
                    if (this.deleteNode) this.deleteNode.setStyles(this.style.actionDeleteNode_selected);
                    if (this.addNode) this.addNode.setStyles(this.style.actionAddNode_selected);
                }
            }
       // }
    },
    addSubUnit: function(){
        this.expand(function(){

            var isNewElement = true;
            if (this.explorer.currentItem) isNewElement = this.explorer.currentItem.unSelected();
            if (isNewElement){
                var newElementData = this.explorer._getAddElementData();
                newElementData.superior = this.data.id;
                var item = new MWF.xApplication.Org.UnitExplorer.Unit(newElementData, this.explorer, this.isEditor, this.level+1, this.childNode, this);
                item.load();
                item.selected();
                item.editBaseInfor();

                (new Fx.Scroll(this.explorer.listScrollNode)).toElementCenter(item.node);
            }else{
                this.app.notice(this.explorer.options.lp.elementSave, "error", this.explorer.propertyContentNode);
            }
        }.bind(this));
    },
    setDeleteFromP: function(){
        this.notDelete = true;
        this.subUnits.each(function(unit){
            unit.setDeleteFromP();
        });
        this.deleteNode.setStyles(this.style.actionDeleteNode_delete);
        this.contentNode.setStyles(this.style.contentNode_delete);
        this.textNode.setStyles(this.style.unitTextNode);
        //this.explorer.deleteElements.push(this);
        this.deleteSelected = true;

        this.explorer.checkDeleteElements(this);
    },
    setDelete: function(){
        //this.actionNode.fade("in");
        this.subUnits.each(function(unit){
            unit.setDeleteFromP();
        });
        this.deleteNode.setStyles(this.style.actionDeleteNode_delete);
        if (this.addNode) this.addNode.setStyles(this.style.actionAddNode_delete);
        this.contentNode.setStyles(this.style.contentNode_delete);
        this.textNode.setStyles(this.style.unitTextNode);
        this.explorer.deleteElements.push(this);
        this.deleteSelected = true;

        this.explorer.checkDeleteElements(this);
    },
    setUndelete: function(){
        //this.actionNode.fade("out");
        this.notDelete = false;
        this.subUnits.each(function(unit){
            unit.setUndelete();
        });
        if (this.explorer.currentItem!==this){
            if (this.deleteNode) this.deleteNode.setStyles(this.style.actionDeleteNode);
            if (this.addNode)  this.addNode.setStyles(this.style.actionAddNode);
            this.contentNode.setStyles(this.style.contentNode);
            this.textNode.setStyles(this.style.unitTextNode);
        }else{
            this.contentNode.setStyles(this.style.contentNode_selected);
            this.textNode.setStyles(this.style.textNode_selected);
            this.actionNode.setStyles(this.style.actionNode_selected);
            if (this.deleteNode) this.deleteNode.setStyles(this.style.actionDeleteNode_selected);
            if (this.addNode)  this.addNode.setStyles(this.style.actionAddNode_selected);
            if (this.addNode) this.addNode.setStyles(this.style.actionAddNode_selected);
        }

        this.explorer.deleteElements.erase(this);
        this.deleteSelected = false;
        this.explorer.checkDeleteElements(this);
    },
    setToggleIconNode: function(){
        if (this.data.subDirectUnitCount>0){
            var toggle_on = (this.explorer.currentItem===this) ? "toggle_current_on" : "toggle_on";
            var toggle_off = (this.explorer.currentItem===this) ? "toggle_current_off" : "toggle_off";
            if (this.isExpand){
                this.toggleIconNode.setStyle("background-image", "url(../x_component_Org/$Explorer/"+this.explorer.app.options.style+"/icon/"+toggle_on+".png)");
            }else{
                this.toggleIconNode.setStyle("background-image", "url(../x_component_Org/$Explorer/"+this.explorer.app.options.style+"/icon/"+toggle_off+".png)");
            }
        }else{
            this.toggleIconNode.setStyle("background-image", "");
        }
    },
    setToggleAction: function(){

        this.toggleIconNode.addEvent("click", function(e){
            this.expandOrCollapse();
            e.stopPropagation();
        }.bind(this));
    },
    expandOrCollapse: function(){

        if (this.isExpand){
            this.collapse();
        }else{
            this.expand();
        }
    },
    listSubUnit: function(callback){
        this.node.mask();
        this.explorer.actions.listSubUnitDirect(function(json){
            if (json.data.length){
                json.data.each(function(itemData){
                    var item = new MWF.xApplication.Org.UnitExplorer.Unit(itemData, this.explorer, this.isEditor, this.level+1, this.childNode, this);
                    this.explorer.elements.push(item);
                    item.load();
                    this.subUnits.push(item);
                }.bind(this));
            }
            this.isLoadSub = true;
            this.node.unmask();
            if (callback) callback();
        }.bind(this), null, this.data.id);
    },
    expand: function(callback){
        this.childNode.setStyle("display", "block");
        this.isExpand = true;
        this.setToggleIconNode();
        if (!this.isLoadSub){
            this.listSubUnit(callback);
        }else{
            if (callback) callback();
        }
    },
    collapse: function(){
        this.childNode.setStyle("display", "none");
        this.isExpand = false;
        this.setToggleIconNode();
    },
    unSelected: function(){
        if (this.content.baseInfor.mode==="edit") return false;
        this.explorer.currentItem = null;
        this.contentNode.setStyles(this.style.contentNode);
        this.textNode.setStyles(this.style.unitTextNode);
        this.actionNode.setStyles(this.style.actionNode);
        if (this.deleteNode) this.deleteNode.setStyles(this.style.actionDeleteNode);
        if (this.addNode) this.addNode.setStyles(this.style.actionAddNode);
        this.iconNode.setStyle("background-image", "url("+this._getIcon()+")");
        this.setToggleIconNode();
        this.clearItemProperty();
        return true;
    },
    selected: function(){
        this.explorer.currentItem = this;
        this.contentNode.setStyles(this.style.contentNode_selected);
        this.textNode.setStyles(this.style.textNode_selected);
        this.actionNode.setStyles(this.style.actionNode_selected);
        if (this.deleteNode) this.deleteNode.setStyles(this.style.actionDeleteNode_selected);
        if (this.addNode) this.addNode.setStyles(this.style.actionAddNode_selected);

        this.iconNode.setStyle("background-image", "url("+this._getIcon()+")");
        this.setToggleIconNode();
        this.showItemProperty();
    },

    showItemProperty: function(){
        this.content = new MWF.xApplication.Org.UnitExplorer.UnitContent(this);
    },
    "delete": function(success, failure){
        this.explorer.actions.deleteUnit(this.data.id, function(){
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
        return (this.explorer.currentItem===this) ? "../x_component_Org/$Explorer/default/icon/unit_current.png" : "../x_component_Org/$Explorer/default/icon/unit.png";
    },
    _isActionManager: function(){
        return (MWF.AC.isOrganizationManager() || MWF.AC.isUnitManager());
    }
});
MWF.xApplication.Org.UnitExplorer.UnitContent = new Class({
    Extends: MWF.xApplication.Org.$Explorer.ItemContent,
    _getData: function(callback){
        if (this.item.data.id){
            this.explorer.actions.getUnit(function(json){
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
        this.titleInfor = new MWF.xApplication.Org.UnitExplorer.UnitContent.TitleInfor(this);
    },
    _showItemPropertyBottom: function(){
        this.bottomInfor = new MWF.xApplication.Org.UnitExplorer.UnitContent.BottomInfor(this);
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
        this.basePage = this.propertyTab.addTab(this.baseContentNode, this.explorer.app.lp.unitBaseText);

        this.personMemberContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.personMemberPage = this.propertyTab.addTab(this.personMemberContentNode, this.explorer.app.lp.unitPersonMembers);

        this.dutyContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.dutyPage = this.propertyTab.addTab(this.dutyContentNode, this.explorer.app.lp.unitDutys);

        if (this.data.control.allowEdit){
            this.attributeContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
            this.attributePage = this.propertyTab.addTab(this.attributeContentNode, this.explorer.app.lp.unitAttribute);
        }
    },
    _loadContent: function(){
        this._listBaseInfor();
        this.loadListCount();
        this._listIdentityMembers();
        this._listDutys();
        if (this.data.control.allowEdit) this._listAttributes();
        //var _self = this;
        // this.personMemberList = this._listMembers("personList", "woSubDirectIdentityList", this.personMemberContentNode, [{
        //     "get": function(){
        //         var src = _self.explorer.actions.getPersonIcon(this.person);
        //         return "<div style='width:24px; height:24px;''><img style='width:24px; height:24px; border-radius:12px; border: 0' src='"+src+"'/></div>";
        //     },
        //     "set": function(){}
        // }, "name", "employee", "mobile", "mail"], [
        //     {"style": "width: 30px", "text": ""},
        //     {"style": "width: 20%", "text": this.explorer.app.lp.personName},
        //     {"style": "", "text": this.explorer.app.lp.personEmployee},
        //     {"style": "", "text": this.explorer.app.lp.personMobile},
        //     {"style": "", "text": this.explorer.app.lp.personMail}
        // ], this.addPersonMember.bind(this), "personCountNode");
        //
        // this.groupMemberList = this._listMembers("groupList", "woGroupList", this.groupMemberContentNode, ["name", "distinguishedName", "description"], [
        //     {"style": "width: 20%", "text": this.explorer.app.lp.groupName},
        //     {"style": "width: 40%", "text": this.explorer.app.lp.groupDn},
        //     {"style": "", "text": this.explorer.app.lp.groupDescription}
        // ], this.addGroupMember.bind(this), "groupCountNode");
    },
    loadListCount: function(){
        var identityCount = this.data.woSubDirectIdentityList.length;
        if (identityCount){
            if (this.identityCountNode){
                this.identityCountNode.set("text", identityCount);
            }else{
                this.identityCountNode = new Element("div", {"styles": this.item.style.tabCountNode, "text": identityCount}).inject(this.personMemberPage.tabNode);
            }
        }else{
            if (this.identityCountNode) this.identityCountNode.destroy();
        }
        var dutyCount = this.data.woUnitDutyList.length;
        if (dutyCount){
            if (this.dutyCountNode){
                this.dutyCountNode.set("text", dutyCount);
            }else{
                this.dutyCountNode = new Element("div", {"styles": this.item.style.tabCountNode, "text": dutyCount}).inject(this.dutyPage.tabNode);
            }
        }else{
            if (this.dutyCountNode) this.dutyCountNode.destroy();
        }

        if (this.data.control.allowEdit){
            var attributeCount = this.data.woUnitAttributeList.length;
            if (attributeCount){
                if (this.attributeCountNode){
                    this.attributeCountNode.set("text", attributeCount);
                }else{
                    this.attributeCountNode = new Element("div", {"styles": this.item.style.tabCountNode, "text": attributeCount}).inject(this.attributePage.tabNode);
                }
            }else{
                if (this.attributeCountNode) this.attributeCountNode.destroy();
            }
        }

    },
    _listBaseInfor: function(){
        this.baseInfor = new MWF.xApplication.Org.UnitExplorer.UnitContent.BaseInfor(this);
    },
    _listDutys: function(){
        var _self = this;
        this.dutyList = new MWF.xApplication.Org.List(this.dutyContentNode, this, {
            "action": this.data.control.allowEdit,
            "saveAction": "saveUnitduty",
            "deleteAction": "deleteUnitduty",
            "data": {
                "description":"",
                "name": "",
                "unique": "",
                "unit": this.data.id,
                "orderNumber": "",
                "identityList": [],
                "woIdentityList": []
            },
            "attr": ["name", "description", {
                "get": function(){
                    //var html = "";
                    // this.woIdentityList.each(function(identity){
                    //     html+="<div>"+identity.name+"</div>"
                    // }.bind(this));
                    return "";
                },
                "events": {
                    "init": function(){
                        //var divs = this.td.getElements("div");
                        var contentNode = this.td;
                        if (this.item.list.options.action){
                            var actionDiv = new Element("div", {"styles": _self.item.style.dutyIdentityAction}).inject(this.td);
                            contentNode = new Element("div", {"styles": _self.item.style.dutyIdentityContent}).inject(this.td);
                            actionDiv.addEvent("click", function(){
                                _self.editDutyIdentity(this.data, contentNode);
                            }.bind(this));
                        }
                        var _dutyData = this.data;
                        this.data.woIdentityList.each(function(identity, i){
                            new MWF.widget.O2Identity(identity, contentNode, {
                                "lazy" : true,
                                "canRemove": _self.data.control.allowEdit,
                                "onRemove": function(O2Identity, e){
                                    _self.deleteDutyIdentity(_dutyData, e, O2Identity);
                                }
                            })
                        }.bind(this));

                    },
                    "click": function(){
                        //_self.explorer.openGroup(this.data, this.td);
                    }
                }
            }],
            "onPostSave": function(item, id){
                if (!item.data.id){
                    item.data.id = id;
                    this.data.woUnitDutyList.push(item.data);
                }
                this.loadListCount();
            }.bind(this),
            "onPostDelete": function(delCount){
                if (this.dutyCountNode){
                    var count = this.dutyCountNode.get("text").toInt()-delCount;
                    this.dutyCountNode.set("text", count);
                }
            }.bind(this),
        });
        //this.dutyList.addItem = function(){this.addDuty();};
        this.dutyList.load([
            {"style": "width: 20%", "text": this.explorer.app.lp.dutyName},
            {"style": "", "text": this.explorer.app.lp.description},
            {"style": "width: 50%", "text": this.explorer.app.lp.dutyMembers}
        ]);

        this.data.woUnitDutyList.each(function(item){
            //this.attributes.push(new MWF.xApplication.Org.PersonExplorer.PersonAttribute(this.attributeTabContentNode.getElement("table").getFirst(), item, this, this.explorer.css.list));
            this.dutyList.push(item);
        }.bind(this));
    },
    editDutyIdentity: function(dutyData, contentNode){
        var _self = this;
        MWF.xDesktop.requireApp("Selector", "Identity", function(){
            var selector = new MWF.xApplication.Selector.Identity(this.explorer.app.content,{
                "values": dutyData.identityList,
                "onComplete": function(items){
                    var woIdentityList = [];
                    var identityList = [];

                    contentNode.empty();
                    items.each(function(item, i){
                        woIdentityList.push(item.data);
                        identityList.push(item.data.id);

                        new MWF.widget.O2Identity(item.data, contentNode, {
                            "canRemove": true,
                            "onRemove": function(O2Identity, e){
                                _self.deleteDutyIdentity(dutyData, e, O2Identity);
                            }
                        })
                    }.bind(this));
                    dutyData.identityList = identityList;
                    dutyData.woIdentityList = woIdentityList;

                    _self.saveDuty(dutyData);
                }.bind(this)
            });
            selector.load();
        }.bind(this));
    },
    deleteDutyIdentity: function(dutyData, e, O2Identity){
        var _self = this;
        var text = this.explorer.app.lp.deleteDutyIdentity.replace(/{duty}/g, dutyData.name);
        text = text.replace(/{identity}/g, O2Identity.data.name);
        this.explorer.app.confirm("warn", e, this.explorer.app.lp.deleteDutyIdentityTitle, text, "360", "170", function(){
            dutyData.identityList.erase(O2Identity.data.id);
            dutyData.woIdentityList = dutyData.woIdentityList.filter(function(a){
                return (O2Identity.data.id !== a.id);
            });
            _self.saveDuty(dutyData, function(){
                O2Identity.destroy();
            });
            this.close();
        }, function(){
            this.close();
        });
    },
    saveDuty: function(data, callback){
        this.propertyContentScrollNode.mask({
            "style": {
                "opacity": 0.7,
                "background-color": "#999"
            }
        });
        this.explorer.actions.saveUnitduty(data, function(){
            this.propertyContentScrollNode.unmask();
            if (callback) callback();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            this.explorer.app.notice("request json error: "+errorText, "error");
            this.content.propertyContentScrollNode.unmask();
        }.bind(this));
    },
    _listAttributes: function(){
        this.attributeList = new MWF.xApplication.Org.List(this.attributeContentNode, this, {
            "action": this.data.control.allowEdit,
            "saveAction": "saveUnitattribute",
            "deleteAction": "deleteUnitattribute",
            "data": {
                "description":"",
                "name": "",
                "unique": "",
                "unit": this.data.id,
                "orderNumber": "",
                "attributeList": []
            },
            "attr": ["name", {
                "get": function(){return this.attributeList.join(",")},
                "set": function(value){this.attributeList = value.split(/,\s*/g)}
            }, "description"],
            "onPostSave": function(item, id){
                if (!item.data.id){
                    item.data.id = id;
                    this.data.woUnitAttributeList.push(item.data);
                }
                this.loadListCount();
            }.bind(this),
            "onPostDelete": function(delCount){
                if (this.attributeCountNode){
                    var count = this.attributeCountNode.get("text").toInt()-delCount;
                    this.attributeCountNode.set("text", count);
                }
            }.bind(this),
        });
        this.attributeList.load([
            {"style": "width: 20%", "text": this.explorer.app.lp.attributeName},
            {"style": "width: 45%", "text": this.explorer.app.lp.attributeValue},
            {"style": "", "text": this.explorer.app.lp.description}
        ]);

        this.data.woUnitAttributeList.each(function(item){
            //this.attributes.push(new MWF.xApplication.Org.PersonExplorer.PersonAttribute(this.attributeTabContentNode.getElement("table").getFirst(), item, this, this.explorer.css.list));
            this.attributeList.push(item);
        }.bind(this));
    },
    _listIdentityMembers: function(){
        var _self = this;
        this.identityMemberList = new MWF.xApplication.Org.List(this.personMemberContentNode, this, {
            "action": this.data.control.allowEdit,
            "canEdit": false,
            "deleteAction": "deleteIdentity",
            "deleteItemTitle": this.explorer.app.lp.deleteIdentityMemeberTitle,
            "deleteItemText": this.explorer.app.lp.deleteIdentityMemeber,
            "data": {},
            "attr": [{
                "getHtml": function(){
                    var src = _self.explorer.actions.getPersonIcon(this.woPerson.id);
                    return "<div style='width:24px; height:24px;''><img style='width:24px; height:24px; border-radius:12px; border: 0' src='"+src+"'/></div>";
                },
                "set": function(){}
            }, {
                "get": function(){return this.woPerson.name}
            }, {
                "get": function(){return this.woPerson.employee}
            }, {
                "get": function(){return this.woPerson.mobile}
            }, {
                "get": function(){return this.woPerson.mail}
            }, {
                "getHtml": function(){
                    return "<div style='width:24px; height:24px; background:url(../x_component_Org/$Explorer/"+
                        _self.explorer.app.options.style+"/icon/open.png) center center no-repeat'></div>";
                },
                "events": {
                    "click": function(){
                        _self.explorer.openPerson(this.data.woPerson, this.td);
                    }
                }
            }, {
                "getHtml": function(){
                    return "<div style='-webkit-user-select: none; -moz-user-select: none; width:24px; height:24px; cursor: move; background:url(../x_component_Org/$Explorer/"+
                        _self.explorer.app.options.style+"/icon/move.png) center center no-repeat'></div>";
                },
                "events": {
                    "selectstart": function(e){e.stopPropagation(); e.preventDefault();return false;},
                    "touchstart": function(e){_self.startOrder(this.item, this.td, e)},
                    "mousedown": function(e){_self.startOrder(this.item, this.td, e)}
                }
            }],
            "onPostDelete": function(delCount){
                if (this.identityCountNode){
                    var count = this.identityCountNode.get("text").toInt()-delCount;
                    this.identityCountNode.set("text", count);
                }
            }.bind(this),
            "onPostLoadAction": function () {
                debugger;
                this.sortAction = new Element("div", {"styles": this.css.sortActionNode, "text": _self.explorer.app.lp.sortByPinYin}).inject(this.actionNode);
                this.sortAction.addEvent("click", function (e) {
                    _self.sortByPinYin(e)
                })
            }
        });
        this.identityMemberList.addItem = this.addPersonMember.bind(this);
        this.identityMemberList.load([
            {"style": "width: 30px", "text": ""},
            {"style": "width: 20%", "text": this.explorer.app.lp.personName},
            {"style": "", "text": this.explorer.app.lp.personEmployee},
            {"style": "", "text": this.explorer.app.lp.personMobile},
            {"style": "", "text": this.explorer.app.lp.personMail},
            {"style": "width: 10px", "text": ""},
            {"style": "width: 10px", "text": ""}
        ]);
        this.data.woSubDirectIdentityList.each(function(id){
            var item = this.identityMemberList.push(id);
        }.bind(this));
    },
    sortByPinYin : function(e){
        var _self = this;
        this.explorer.app.confirm("infor", e, this.explorer.app.lp.sortByPinYin, {"html": this.explorer.app.lp.sortByPinYinConfirmContent}, 300, 180, function(){
            debugger;
            var list = _self.data.woSubDirectIdentityList;
            list.sort( function(a, b){
               return a.name.localeCompare(b.name);
            });
            for( var i=0; i<list.length; i++ ){
                _self.explorer.actions.orderIdentity(list[i].id, "(0)", function(){}, null, false);
            }
            _self.identityMemberList.clear();
            list.each(function(id){
                var item = _self.identityMemberList.push(id);
            }.bind(this));
            this.close();
        }, function(){
            this.close();
        });
    },
    startOrder: function(item, td, e){
        var tr = td.getParent("tr");
        var table = tr.getParent("table");
        var div = td.getFirst("div");
        var size = tr.getSize();
        var titleTr = table.getElement("tr");

        var moveNode = new Element("table", {"styles": {
            "opacity": 0.7,
            "border": "1px dashed #999",
            "z-index": 10000,
            "width": size.x,
            "height": size.y,
            "background-color": "#CCC",
            "position": "absolute"
        }}).inject(this.explorer.app.content);
        var moveNodeTr = tr.clone().inject(moveNode);
        var moveTds = moveNodeTr.getElements("td");
        titleTr.getElements("th").each(function(cell, i){
            moveTds[i].setStyle("width", ""+cell.getSize().x+"px");
        });
        moveNode.position({
            relativeTo: tr,
            position: 'upperLeft',
            edge: 'upperLeft'
        });
        div.setStyle("display", "none");

        var nextData = {};
        var ntr = tr.getNext("tr");
        if (ntr) nextData = ntr.retrieve("data");
        if (!nextData) nextData={};

        var drag = new Drag.Move(moveNode, {
            "container": table,
            "droppables": table.getElements("tr").erase(tr),
            "preventDefault": true,
            "stopPropagation": true,
            "onStart": function(){
                tr.setStyles({
                    "display": "none",
                    "background-color": "#dff3fc"
                });
            }.bind(this),
            "onEnter": function(element, droppable){
                tr.inject(droppable, "after");
                droppable.setStyles({"background": "#fcf8f1"});
                tr.setStyles({"display": "table-row"});
                element.setStyles({"display": "none"});

                var nextTr = tr.getNext("tr");
                if (nextTr){
                    nextData = nextTr.retrieve("data");
                }else{
                    nextData={};
                }
                if (!nextData) nextData={};

            },
            "onLeave": function(element, droppable){
                droppable.setStyles({"background": "transparent "});
                tr.setStyles({"display": "none"});
                element.setStyles({"display": "block"});
            },
            "onDrop": function(dragging, droppable, e){
                var nextTr = tr.getNext("tr");
                if (nextTr){
                    nextData = nextTr.retrieve("data");
                }else{
                    nextData={};
                }
                if (!nextData) nextData={};
                moveNode.destroy();

                droppable.setStyles({"background": "transparent "});
                tr.setStyles({"background": "transparent "});
                this.explorer.actions.orderIdentity(item.data.id, nextData.id || "(0)", function(){});
                div.setStyle("display", "block");
            }.bind(this),
            "onCancel": function(dragging){
                dragging.destroy();
                drag = null;
                div.setStyle("display", "block");
            }
        });
        drag.start(e);



        // var moveNode = new Element("div", {"styles": {
        //     "opacity": 0.7,
        //     "border": "1px dashed #999",
        //     "z-index": 10000,
        //     "width": size.x,
        //     "height": size.y,
        //     "background-color": "#CCC",
        //     "position": "absolute"
        // }}).inject(this.explorer.app.content);
        // moveNode.setStyles({
        //     "opacity": 0.7,
        //     "border": "1px dashed #999",
        //     "z-index": 10000,
        //     "width": size.x,
        //     "height": size.y,
        //     "background-color": "#CCC",
        //     "position": "absolute"
        // });


    },

    addPersonMember: function(){
        this.checkSaveBaseInfor(function(){
            MWF.xDesktop.requireApp("Selector", "Person", function(){
                var selector = new MWF.xApplication.Selector.Person(this.explorer.app.content,{
                    "values": [],
                    "onComplete": function(items){

                        var data = {
                            "description": "",
                            "name": "",
                            "unique": "",
                            "person": "",
                            "department": "",
                            "unit": this.data.id,
                            "orderNumber": ""
                        };
                        items.each(function(item){
                            var idData = Object.clone(data);
                            idData.name = item.data.name;
                            idData.person = item.data.id;
                            this.explorer.actions.saveIdentity(idData, function(d){
                                this.explorer.actions.getIdentity(function(id){
                                    this.data.woSubDirectIdentityList.push(id.data);
                                    this.identityMemberList.push(id.data);
                                    this.loadListCount();
                                }.bind(this), null, d.data.id);
                            }.bind(this));
                        }.bind(this));
                    }.bind(this)
                });
                selector.load();
            }.bind(this));
        }.bind(this));
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
    }
});
MWF.xApplication.Org.UnitExplorer.UnitContent.TitleInfor = new Class({
    Extends: MWF.xApplication.Org.$Explorer.ItemContent.TitleInfor,
    _getStyle: function(){
        var css = Object.clone(this.item.style.person);
        return Object.merge(css, this.item.style.role);
    },
    _getIcon: function(){
        return "../x_component_Org/$Explorer/default/icon/unit70.png";
    },
    setBackground: function(){
        this.titleBgNode.setStyle("background-image", "url(../x_component_Org/$Explorer/"+this.explorer.app.options.style+"/icon/unit_bg_bg.png)");
        this.titleNode.setStyle("background-image", "url(../x_component_Org/$Explorer/"+this.explorer.app.options.style+"/icon/unit_bg.png)");
    },
    loadRightInfor: function(){
        //var text = this.data.name+((this.data.unique) ? "（"+this.data.unique+"）" : "");
        var text = this.data.name;
        if (!this.nameNode) this.nameNode = new Element("div", {"styles": this.style.titleInforNameNode}).inject(this.titleInforRightNode);
        if (!this.signatureNode) this.signatureNode = new Element("div", {"styles": this.style.titleInforSignatureNode}).inject(this.titleInforRightNode);
        this.nameNode.set("text", text);
        this.signatureNode.set("text", (this.data.levelName || "" ));
    }
});
MWF.xApplication.Org.UnitExplorer.UnitContent.BottomInfor = new Class({
    Extends: MWF.xApplication.Org.$Explorer.ItemContent.BottomInfor,
    addInforList: function(){
        var text = this.explorer.app.lp.unitReadDn.replace(/{dn}/g, (this.data.distinguishedName || " "));
        this.addInfor(text);

        text = this.explorer.app.lp.unitReadLevel.replace(/{level}/g, (this.data.level || " "));
        text = text.replace(/{levelName}/g, (this.data.levelName || " "));
        this.addInfor(text);

        text = this.explorer.app.lp.unitReadCreate.replace(/{date}/g, (this.data.createTime || " "));
        text = text.replace(/{date2}/g, (this.data.updateTime || " "));
        this.addInfor(text);
    }
});

MWF.xApplication.Org.UnitExplorer.UnitContent.BaseInfor = new Class({
    initialize: function(content){
        this.content = content;
        this.item = content.item;
        this.data = this.item.data;
        this.explorer = this.item.explorer;
        this.contentNode = this.content.baseContentNode;
        this.style = this.item.style.person;
        this.attributes = [];
        this.mode = "read";
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.style.baseContentNode}).inject(this.contentNode);
        this.editContentNode = new Element("div", {"styles": this.style.baseEditNode}).inject(this.node);

        this.editContentNode.set("html", this.getContentHtml());

        var n = this.editContentNode.getElement(".infor_name");
        if (n) n.set("text", this.data.name || "");

        var n = this.editContentNode.getElement(".infor_unique");
        if (n) n.set("text", this.data.unique || "");

        var n = this.editContentNode.getElement(".infor_type");
        if (n) n.set("text", this.data.typeList.join(", ") || "");

        var n = this.editContentNode.getElement(".infor_shortName");
        if (n) n.set("text", this.data.shortName || "");

        var n = this.editContentNode.getElement(".infor_description");
        if (n) n.set("text", this.data.description || "");

        var n = this.editContentNode.getElement(".infor_orderNumber");
        if (n) n.set("text", this.data.orderNumber || "");


        this.editContentNode.getElements("td.inforTitle").setStyles(this.style.baseInforTitleNode);
        this.editContentNode.getElements("td.inforContent").setStyles(this.style.baseInforContentNode);
        this.editContentNode.getElements("td.inforAction").setStyles(this.style.baseInforActionNode);

        var tdContents = this.editContentNode.getElements("td.inforContent");
        if (this.data.control.allowEdit){
            if (this.data.controllerList){
                this.data.controllerList.each(function(id){
                    new MWF.widget.O2Person({"name": id}, tdContents[5], {"style": "xform"});
                }.bind(this));
            }
            if (this.data.superior) new MWF.widget.O2Unit({"name": this.data.superior}, tdContents[6], {"style": "xform"});
        }


        this.loadAction();
    },
    getContentHtml: function(){
        var html = "<table width='100%' cellpadding='3px' cellspacing='5px'>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.unitName+":</td><td class='inforContent infor_name'></td>";
        if (this.data.control.allowEdit) html += "<td class='inforTitle'>"+this.explorer.app.lp.unitUnique+":</td><td class='inforContent infor_unique'></td>";
        html += "</tr><tr><td class='inforTitle'>"+this.explorer.app.lp.unitTypeList+":</td><td class='inforContent infor_type'></td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.unitShortName+":</td><td class='inforContent infor_shortName'></td></tr>";
        // html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.unitLevel+":</td><td class='inforContent'>"+this.data.level+"</td>" +
        //     "<td class='inforTitle'>"+this.explorer.app.lp.unitLevelName+":</td><td class='inforContent'>"+(this.data.levelName || "")+"</td></tr>";

        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.unitDescription+":</td><td colspan='3' class='inforContent infor_description'></td>";
        if (this.data.control.allowEdit){
            html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.unitControllerList+":</td><td class='inforContent'></td>" +
                "<td class='inforTitle'>"+this.explorer.app.lp.unitSuperUnit+":</td><td class='inforContent'></td></tr>";
            html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.orderNumber+":</td><td colspan='3' class='inforContent infor_orderNumber'></td></tr>";
        }

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

            this.editNode = new Element("div", {"styles": this.style.actionEditNode, "text": this.explorer.app.lp.editUnit}).inject(this.baseInforEditActionAreaNode);

            this.saveNode = new Element("div", {"styles": this.style.actionSaveNode, "text": this.explorer.app.lp.saveUnit}).inject(this.baseInforEditActionAreaNode);
            this.cancelNode = new Element("div", {"styles": this.style.actionCancelNode, "text": this.explorer.app.lp.cancel}).inject(this.baseInforEditActionAreaNode);

            this.editNode.setStyle("display", "block");
            this.editNode.addEvent("click", this.edit.bind(this));
            this.saveNode.addEvent("click", this.save.bind(this));
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
        this.typeListInputNode = new Element("input", {"styles": this.style.inputNode_type}).inject(tdContents[2]);
        this.typeListInputNode.set("value", ((this.data.typeList.length) ? this.data.typeList.join(", "): ""));
        this.loadUnitTypeSelect();

        tdContents[3].setStyles(this.style.baseInforContentNode_edit).empty();
        this.shortNameInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[3]);
        this.shortNameInputNode.set("value", (this.data.shortName || ""));


        tdContents[4].setStyles(this.style.baseInforContentNode_edit).empty();
        this.descriptionInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[4]);
        this.descriptionInputNode.set("value", (this.data.description || ""));

        tdContents[5].setStyles(this.style.baseInforContentNode_edit).empty();
        this.controllerListInputNode = new Element("div", {"styles": this.style.inputNode_person}).inject(tdContents[5]);

        tdContents[6].setStyles(this.style.baseInforContentNode_edit).empty();
        this.superUnitInputNode = new Element("div", {"styles": this.style.inputNode_person}).inject(tdContents[6]);

        tdContents[7].setStyles(this.style.baseInforContentNode_edit).empty();
        this.orderNumberInputNode = new Element("input", {"styles": this.style.inputNode, "type":"number"}).inject(tdContents[7]);
        this.orderNumberInputNode.set("value", (this.data.orderNumber || ""));
        //this.controllerListInputNode.set("value", ((this.data.controllerList) ? this.data.controllerList.join(", ") : ""));

        if (this.data.superior) new MWF.widget.O2Unit({"name": this.data.superior}, this.superUnitInputNode, {"style": "xform"});
        this.superUnitInputNode.addEvent("click", function(){
            MWF.xDesktop.requireApp("Selector", "package", function(){
                var options = {
                    "type": "unit",
                    "values": [this.data.superior] || [],
                    "count": 1,
                    "onComplete": function(items){
                        this.superUnitInputNode.empty();
                        this.data.oldSuperior = this.data.superior;
                        if (items.length){
                            this.data.superior = items[0].data.id;
                            new MWF.widget.O2Unit({"name": this.data.superior}, this.superUnitInputNode, {"style": "xform"})
                        }else{
                            this.data.superior = "";
                        }
                    }.bind(this)
                };
                var selector = new MWF.O2Selector(this.explorer.app.content, options);
            }.bind(this));
        }.bind(this));

        if (this.data.controllerList){
            this.data.controllerList.each(function(id){
                new MWF.widget.O2Person({"name": id}, this.controllerListInputNode, {"style": "xform"});
            }.bind(this));
        }
        this.controllerListInputNode.addEvent("click", function(){
            MWF.xDesktop.requireApp("Selector", "package", function(){
                var options = {
                    "type": "person",
                    "values": this.data.controllerList || [],
                    "count": 0,
                    "onComplete": function(items){
                        this.data.oldControllerList = this.data.controllerList;
                        var controllerList = [];
                        this.controllerListInputNode.empty();
                        items.each(function(item){
                            controllerList.push(item.data.id);
                            new MWF.widget.O2Person(item.data, this.controllerListInputNode, {"style": "xform"});
                        }.bind(this));
                        this.data.controllerList = controllerList;
                    }.bind(this)
                };
                var selector = new MWF.O2Selector(this.explorer.app.content, options);
            }.bind(this));
        }.bind(this));


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
    loadUnitTypeSelect: function(){
        if (this.typeListInputNode){
            this.typeListInputNode.addEvents({
                "blur": function(){this.hideTypeSelectNode();}.bind(this),
                "click": function(){this.showTypeSelectNode();}.bind(this),
                "focus": function(){this.showTypeSelectNode();}.bind(this)
            });
        }
    },
    hideTypeSelectNode: function(){
        if (this.typeSelectNode) this.typeSelectNode.destroy();
        this.typeSelectNode = null;
    },
    showTypeSelectNode: function(){
        if (!this.typeSelectNode){
            this.typeSelectNode = new Element("div", {"styles": this.style.typeSelectNode}).inject(this.typeListInputNode, "after");
            var size = this.typeListInputNode.getSize();
            var width = size.x-3;
            this.typeSelectNode.setStyle("width", ""+width+"px");
            this.typeSelectNode.position({
                "relativeTo": this.typeListInputNode,
                "position": 'bottomLeft',
                "edge": 'upperLeft',
                "offset": {"x": 1, "y": -3}
            });

            this.explorer.actions.listUnitType(function(json){
                var count = json.data.valueList.length;
                var height = (count*30);
                this.typeSelectNode.setStyle("height", ""+height+"px");

                json.data.valueList.each(function(t, i){
                    this.createTypeSelectItem(t, i);
                }.bind(this));
            }.bind(this));
        }
    },
    createTypeSelectItem: function(text, i){
        var typeSelectItemNode = new Element("div", {"styles": this.style.typeSelectItemNode}).inject(this.typeSelectNode);
        if ((i % 2)===0) typeSelectItemNode.setStyle("background", "#f4f9ff");
        var iconNode = new Element("div", {"styles": this.style.typeSelectItemIconNode}).inject(typeSelectItemNode);
        var textNode = new Element("div", {"styles": this.style.typeSelectItemTextNode}).inject(typeSelectItemNode);
        textNode.set("text", text);

        var _self = this;
        typeSelectItemNode.addEvents({
            "mouseover": function(){this.setStyle("background-color", "#fef5e7");},
            "mouseout": function(){this.setStyle("background", "#ffffff"); if ((i % 2)===0) this.setStyle("background", "#f4f9ff");},
            "mousedown": function(){
                _self.typeListInputNode.set("value", this.get("text"));
            }
        });

    },

    save: function(){
        var tdContents = this.editContentNode.getElements("td.inforContent");
        if (!this.nameInputNode.get("value")){
            this.explorer.app.notice(this.explorer.app.lp.inputUnitInfor, "error", this.explorer.propertyContentNode);
            return false;
        }
        //this.data.genderType = gender;
        //if (!this.uniqueInputNode.get("value")) this.data.unique = this.employeeInputNode.get("value");
        this.content.propertyContentScrollNode.mask({
            "style": {
                "opacity": 0.7,
                "background-color": "#999"
            }
        });

        this.saveUnit(function(){
            this.cancel(  null,true );
            this.content.propertyContentScrollNode.unmask();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            this.explorer.app.notice("request json error: "+errorText, "error");
            this.content.propertyContentScrollNode.unmask();
        }.bind(this));
    },
    saveUnit: function(callback, cancel){
        var data = Object.clone(this.data);
        data.name = this.nameInputNode.get("value");
        data.unique = this.uniqueInputNode.get("value");
        data.typeList = (this.typeListInputNode.get("value")) ? this.typeListInputNode.get("value").split(/,\s*/g) : [];
        data.shortName = this.shortNameInputNode.get("value");
        data.description = this.descriptionInputNode.get("value");
        data.orderNumber = this.orderNumberInputNode.get("value");
        delete data.oldSuperior;
        delete data.oldControllerList;
        //data.controllerList = (this.controllerListInputNode.get("value")) ? this.controllerListInputNode.get("value").split(/,\s*/g) : [];

        this.explorer.actions.saveUnit(data, function(json){
            Object.merge(this.data, data);
            if (this.data.id){
                this.data.id = json.data.id;
                this.item.refresh();
                if (callback) callback();
            }else{
                this.explorer.actions.getUnit(function(json){
                    this.data = Object.merge(this.data, json.data);
                    this.item.data = this.data;
                    this.item.refresh();
                    if (this.item.parent) this.item.parent.subUnits.push(this.item);
                    if (callback) callback();
                }.bind(this), null, json.data.id);
            }
        }.bind(this), function(xhr, text, error){
            if (cancel) cancel(xhr, text, error);
        }.bind(this));
    },
    cancel: function( ev, flag ){
        if (this.data.id){
            var tdContents = this.editContentNode.getElements("td.inforContent");
            tdContents[0].setStyles(this.style.baseInforContentNode).set("text", this.data.name || "");
            tdContents[1].setStyles(this.style.baseInforContentNode).set("text", this.data.unique || "");
            tdContents[2].setStyles(this.style.baseInforContentNode).set("text", ((this.data.typeList.length) ? this.data.typeList.join(", "): ""));
            tdContents[3].setStyles(this.style.baseInforContentNode).set("text", this.data.shortName || "");
            tdContents[4].setStyles(this.style.baseInforContentNode).set("text", this.data.description || "");
            //tdContents[5].setStyles(this.style.baseInforContentNode).set("text", ((this.data.controllerList.length) ? this.data.controllerList.join(", "): ""));
            tdContents[5].setStyles(this.style.baseInforContentNode).empty();
            tdContents[6].setStyles(this.style.baseInforContentNode).empty();
            tdContents[7].setStyles(this.style.baseInforContentNode).set("text", this.data.orderNumber || "");

            if( !flag ){
                if (this.data.oldSuperior) this.data.superior = this.data.oldSuperior;
                if (this.data.oldControllerList) this.data.controllerList = this.data.oldControllerList;
            }
            delete this.data.oldSuperior;
            delete this.data.oldControllerList;

            if (this.data.superior) new MWF.widget.O2Unit({"name": this.data.superior}, tdContents[6], {"style": "xform"});
            if (this.data.controllerList){
                this.data.controllerList.each(function(id){
                    new MWF.widget.O2Person({"name": id}, tdContents[5], {"style": "xform"});
                }.bind(this));
            }

            this.mode = "read";

            this.editNode.setStyle("display", "block");
            this.saveNode.setStyle("display", "none");
            this.cancelNode.setStyle("display", "none");
        }else{
            this.item.destroy();
        }
    },
    destroy: function(){
        this.node.empty();
        this.node.destroy();
        MWF.release(this);
    }
});