// MWF.xDesktop.requireApp("Organization", "GroupExplorer", null, false);
// MWF.xDesktop.requireApp("Organization", "OrgExplorer", null, false);
MWF.xDesktop.requireApp("Org", "$Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xApplication.Org.PersonExplorer = new Class({
	Extends: MWF.xApplication.Org.$Explorer,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
    loadToolbar: function(){
        if (this._isActionManager()) {
            this.isEditor = true;
            this.addTopElementNode = new Element("div", {"styles": this.css.addTopGroupNode}).inject(this.toolbarNode);
            this.addTopElementNode.addEvent("click", function () {
                this.addTopElement();
            }.bind(this));
        }
        this.filterNode = new Element("div", {"styles": this.css.filterNode}).inject(this.toolbarNode);
        this.loadFilterTooltip();

        this.createSearchNode();
        this.loadPingyinArea();
    },
    loadFilterTooltip: function(){
        this.filterTooltip = new MWF.xApplication.Org.PersonExplorer.FilterTooltip(this.app.content, this.filterNode, null, {}, {
            event : "click"
        });
        this.filterTooltip.explorer = this;
    },
    _isActionManager: function(){
        return (MWF.AC.isOrganizationManager() || MWF.AC.isPersonManager() || MWF.AC.isUnitManager());
    },
    _listElementNext: function(lastid, count, callback, page){
        // this.actions.listPersonNext(lastid||"(0)", count, function(json){
        //     if (callback) {
        //         callback.apply(this, [json]);
        //     }
        // }.bind(this));
        o2.Actions.load("x_organization_assemble_control").PersonAction.listFilterPaging(page, count, this.filterData || {}, function(json){
            if (callback) {
                callback.apply(this, [json]);
            }
        }.bind(this));
    },
    _newElement: function(data, explorer, i){
        return new MWF.xApplication.Org.PersonExplorer.Person(data, explorer, this.isEditor, i);
    },
    _listElementByKey: function(callback, failure, key){

        // this.actions.listPersonByKey(function(json){
        //     if (callback) {
        //         callback.apply(this, [json]);
        //     }
        // }.bind(this), failure, key);
        o2.Actions.load("x_organization_assemble_control").PersonAction.listFilterPaging(1, 10000, {
            key: key
        }, function(json){
            if (callback) {
                callback.apply(this, [json]);
            }
        }.bind(this), failure)
    },
    _getAddElementData: function(){
        return {
            "genderType": "m",
            "signature": "",
            "description": "",
            "unique": "",
            "orderNumber": "",
            "superior": "",
            "officePhone": "",
            "boardDate": "",
            "birthday": "",
            "employee": "",
            "password": "",
            "display": "",
            "qq": "",
            "mail": "",
            "weixin": "",
            "weibo": "",
            "mobile": "",
            "name": "",
            "ipAddress" : "",
            "controllerList": [],
            "woPersonAttributeList":[],
            "woIdentityList": [],
            "control": {
                "allowEdit": true,
                "allowDelete": true
            },
            "subjectSecurityClearance": null
        };
    }


});

MWF.xApplication.Org.PersonExplorer.Person = new Class({
    Extends: MWF.xApplication.Org.$Explorer.Item,

    showItemProperty: function(){
        this.content = new MWF.xApplication.Org.PersonExplorer.PersonContent(this);
    },
    _loadTextNode: function(){
        var html = "<div style='float:left; height:50px; overflow:hidden'>"+this.data.name+"</div>";
        html += "<div style='float: right; overflow:hidden; font-size: 12px; color: #aaaaaa;'>"+(this.data.mobile || "")+"</div>";
        this.textNode.set({"html": html});
    },
    "delete": function(success, failure){
        this.explorer.actions.deletePerson(this.data.id, function(){
            this.destroy();
            if (success) success();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);

            if (failure) failure();
        });
    },
    _getIcon: function(nocache){
        var url = (this.data.id) ? this.explorer.actions.getPersonIcon(this.data.id) : "../x_component_Org/$Explorer/default/icon/man.png";
        return (nocache) ? url+"?"+(new Date().getTime()) : url;
        //return (this.data.id) ? this.explorer.actions.getPersonIcon(this.data.id) : "../x_component_Org/$Explorer/default/icon/man.png";
        // var src = "data:image/png;base64,"+this.data.icon;
        // if (!this.data.icon){
        //     if (this.data.genderType==="f"){
        //         src = "../x_component_Org/$Explorer/default/icon/female24.png"
        //     }else{
        //         src = "../x_component_Org/$Explorer/default/icon/man24.png"
        //     }
        // }
        // return src;
    }
});

MWF.xApplication.Org.PersonExplorer.PersonContent = new Class({
    Extends: MWF.xApplication.Org.$Explorer.ItemContent,

    _getData: function(callback){
        if (this.item.data.id){
            this.explorer.actions.getPerson(function(json){
                this.data = json.data;
                this.item.data = json.data;
                if (callback) callback();
            }.bind(this), null, this.item.data.id);
        }else{
            this.data = this.item.data;
            if (callback) callback();
        }
    },
    edit: function(){
        if (this.baseInfor) this.baseInfor.edit();
    },

    _showItemPropertyTitle: function(){
        this.titleInfor = new MWF.xApplication.Org.PersonExplorer.PersonContent.TitleInfor(this);
        //this.baseInfor = new MWF.xApplication.Org.BaseInfor(this);
    },
    _showItemPropertyBottom: function(){
        this.bottomInfor = new MWF.xApplication.Org.PersonExplorer.PersonContent.BottomInfor(this);
    },
    loadItemPropertyTab: function(callback){
        this.propertyTabContainerNode = new Element("div", {"styles": this.item.style.tabTitleNode}).inject(this.propertyContentNode, "top");

        MWF.require("MWF.widget.Tab", function(){
            this.propertyTab = new MWF.widget.Tab(this.propertyContentNode, {"style": "unit", "useMainColor":true});
            this.propertyTab.load();

            this.propertyTab.tabNodeContainer.inject(this.propertyTabContainerNode);
            this.propertyTab.tabNodeContainer.setStyle("width","480px");
            if (callback) callback();
        }.bind(this));
    },
    _loadTabs: function(){
        this.baseContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.basePage = this.propertyTab.addTab(this.baseContentNode, this.explorer.app.lp.personBaseText);

        this.attributeContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.attributePage = this.propertyTab.addTab(this.attributeContentNode, this.explorer.app.lp.personAttributeText);

        this.identityContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.identityPage = this.propertyTab.addTab(this.identityContentNode, this.explorer.app.lp.personIdentityText);

        this.roleContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        this.rolePage = this.propertyTab.addTab(this.roleContentNode, this.explorer.app.lp.personRoleText);

        // this.managerContentNode = new Element("div", {"styles": this.item.style.tabContentNode});
        // this.managerPage = this.propertyTab.addTab(this.managerContentNode, this.explorer.app.lp.controllerListText);
    },
    _loadContent: function(){
        this._listBaseInfor();
        if( this.data && this.data.id ){
            this._listAttribute();
            this._listIdentity();
            this._listRole();
            this.loadListCount();
        }

        //
        // this.showAttribute();
    },
    loadList : function(){
        this._listAttribute();
        this._listIdentity();
        this._listRole();
        this.loadListCount();
    },
    loadListCount: function(){
        if (this.data.woIdentityList){
            var identityCount = this.data.woIdentityList.length;
            if (identityCount){
                if (!this.identityCountNode){
                    this.identityCountNode = new Element("div", {"styles": this.item.style.tabCountNode, "text": identityCount}).inject(this.identityPage.tabNode);
                }else{
                    this.identityCountNode.set("text", identityCount);
                }
            }else{
                if (this.identityCountNode) this.identityCountNode.destroy();
            }
        }

        if (this.data.woPersonAttributeList){
            var attributeCount = this.data.woPersonAttributeList.length;
            if (attributeCount){
                if (!this.attributeCountNode){
                    this.attributeCountNode = new Element("div", {"styles": this.item.style.tabCountNode, "text": attributeCount}).inject(this.attributePage.tabNode);
                }else{
                    this.attributeCountNode.set("text", attributeCount);
                }
            }else{
                if (this.attributeCountNode) this.attributeCountNode.destroy();
            }
        }

        if( this.roleDataList ){
            var roleCount = this.roleDataList.length;
            if (roleCount){
                if (!this.roleCountNode){
                    this.roleCountNode = new Element("div", {"styles": this.item.style.tabCountNode, "text": roleCount}).inject(this.rolePage.tabNode);
                }else{
                    this.roleCountNode.set("text", roleCount);
                }
            }else{
                if (this.roleCountNode) this.roleCountNode.destroy();
            }
        }

        // var groupCount = this.data.groupList.length;
        // if (groupCount){
        //     this.groupCountNode = new Element("div", {"styles": this.item.style.tabCountNode, "text": groupCount}).inject(this.groupMemberPage.tabNode);
        // }
    },

    _listBaseInfor: function(){
        this.baseInfor = new MWF.xApplication.Org.PersonExplorer.PersonContent.BaseInfor(this);
    },
    _listAttribute: function(){
        this.attributeList = new MWF.xApplication.Org.List(this.attributeContentNode, this, {
            "action": this.data.control.allowEdit,
            "data": {
                "person": this.data.id,
                "name": "",
                "unique": "",
                "orderNumber": "",
                "attributeList": [],
                "description":""
            },
            "attr": ["name", {
                "get": function(){return this.attributeList.join(",")},
                "set": function(value){ this.attributeList = value.split(/,\s*/g)}
            }, "description"],
            "onPostSave": function(item, id){
                if (!item.data.id){
                    item.data.id = id;
                    this.data.woPersonAttributeList.push(item.data);
                }
                this.loadListCount();

                // if (!item.data.id){
                //     if (this.attributeCountNode){
                //         var count = this.attributeCountNode.get("text").toInt()+1;
                //         this.attributeCountNode.set("text", count);
                //     }
                // }
            }.bind(this),
            "onPostDelete": function(delCount){
                if (this.attributeCountNode){
                    var count = this.attributeCountNode.get("text").toInt()-delCount;
                    this.attributeCountNode.set("text", count);
                }
            }.bind(this)
        });
        this.attributeList.load([
            {"style": "width: 20%", "text": this.explorer.app.lp.attributeName},
            {"style": "", "text": this.explorer.app.lp.attributeValue},
            {"style": "", "text": this.explorer.app.lp.description}
        ]);

        this.data.woPersonAttributeList.each(function(item){
            this.attributeList.push(item);
        }.bind(this));
        // if (this.data.id){
        //     this.explorer.actions.listPersonAttribute(function(json){
        //
        //         var attributeCount = json.data.length;
        //         if (attributeCount){
        //             this.attributeCountNode = new Element("div", {"styles": this.item.style.tabCountNode, "text": attributeCount}).inject(this.attributePage.tabNode);
        //         }
        //
        //         json.data.each(function(item){
        //             //this.attributes.push(new MWF.xApplication.Org.PersonExplorer.PersonAttribute(this.attributeTabContentNode.getElement("table").getFirst(), item, this, this.explorer.css.list));
        //             this.attributeList.push(item);
        //         }.bind(this));
        //     }.bind(this), null, this.data.id);
        // }
    },
    getDutyActionPermission: function(){
        if( MWF.AC.isSystemManager() )return false;
        if( MWF.AC.isOrganizationManager() )return true;
        if( MWF.AC.isSecurityManager() )return true;
        return false;
    },
    _listIdentity: function(){
        var _self = this;
        this.identityList = new MWF.xApplication.Org.List(this.identityContentNode, this, {
            "action": false,
            "canEdit": false,
            "saveAction": "saveIdentity",
            "data": {
                "person": this.data.id,
                "name": "",
                "attributeList": []
            },
            "attr": ["name", {
                "get": function(){ return ""; },
                "events": {
                    "init": function(){
                        var contentNode = this.td;
                        new MWF.widget.O2Unit(this.data.woUnit, contentNode, {"style": "xform"});
                    }
                }
            }, {
                "getHtml": function(){
                    return "<div style='word-break: break-word;'>"+o2.txt(this.distinguishedName)+"</div>";
                },
                //"get": function(){ return this.distinguishedName; },
                "set": function(value){ this.distinguishedName = value; }
            }, {
                "get": function(){ return ""; },
                "events": {
                    "init": function(){
                        var contentNode = new Element("div.duty-wrap").inject(this.td);
                        var dutyNode = new Element("div.duty-area").inject(contentNode);
                        if (this.data.woUnitDutyList){
                            this.data.woUnitDutyList.each(function(duty){
                                new MWF.widget.O2Duty(duty, dutyNode, {"style": "xform", "showUnit": true});
                            }.bind(this));
                        }
                        if( _self.getDutyActionPermission() ){
                            var editDutyIcon = new Element("i.o2icon-edit2", {
                                "title": MWF.xApplication.Org.LP.editDuty
                            }).inject(contentNode);
                            editDutyIcon.addClass("edit-duty-icon");
                            editDutyIcon.addEvent("click", function () {
                                new MWF.O2Selector(_self.explorer.app.content, {
                                    "type": "UnitDuty",
                                    "values": this.data.woUnitDutyList,
                                    "onComplete" : function( items ){
                                        var selectedList = items.map( function(item){ debugger; return item.data; });
                                        var oldIdList = this.data.woUnitDutyList.map(function (d) { return d.id; })
                                        var newIdList = selectedList.map(function (d) { return d.id; });

                                        var addList = selectedList.filter(function (d) {
                                            return !oldIdList.contains( d.id );
                                        }.bind(this));
                                        var removeList = this.data.woUnitDutyList.filter(function (d) {
                                            return !newIdList.contains( d.id );
                                        });

                                        var currentIdentityId = this.data.id;

                                        var psRemove = removeList.map(function (d) {
                                            var id = d.id;
                                            return o2.Actions.load("x_organization_assemble_control").UnitDutyAction.get(id).then(function (json) {
                                                json.data.identityList = json.data.identityList.filter(function (identity) {
                                                    return identity !== currentIdentityId;
                                                })
                                                return o2.Actions.load("x_organization_assemble_control").UnitDutyAction.edit(id, json.data);
                                            }.bind(this));
                                        });

                                        var psAdd = addList.map(function (d) {
                                            var id = d.id;
                                            return o2.Actions.load("x_organization_assemble_control").UnitDutyAction.get(id).then(function (json) {
                                                json.data.identityList.push(currentIdentityId);
                                                return o2.Actions.load("x_organization_assemble_control").UnitDutyAction.edit(id, json.data);
                                            }.bind(this));
                                        });

                                        Promise.all( psRemove.concat(psAdd) ).then(function( list ){

                                            _self.explorer.app.notice(MWF.xApplication.Org.LP.modifySuccess);

                                            this.data.woUnitDutyList = selectedList;
                                            dutyNode.empty();
                                            this.data.woUnitDutyList.each(function(duty){
                                                new MWF.widget.O2Duty(duty, dutyNode, {"style": "xform", "showUnit": true});
                                            }.bind(this));
                                        }.bind(this), function () {
                                            _self.explorer.app.notice(MWF.xApplication.Org.LP.modifyFail, "error");
                                        });
                                    }.bind(this)
                                })
                            }.bind(this))
                        }
                    }
                }
            }, {
                "getHtml": function(){
                    if (this.major){
                        return "<div style='width:24px; height:24px; background:url(../x_component_Org/$Explorer/"+
                            _self.explorer.app.options.style+"/icon/mainid.png) center center no-repeat'></div>";
                    }else{
                        return "<div title='"+_self.explorer.app.lp.setIdentityMain+"' style='width:24px; height:24px; cursor: pointer; background:url(../x_component_Org/$Explorer/"+
                            _self.explorer.app.options.style+"/icon/select.png) center center no-repeat'></div>";
                    }
                },
                "events": {
                    "click": function(){
                        if (!this.data.major){
                            if (_self.data.control.allowEdit){_self.setMainIdentity(this.data, this.td, this.item);}
                        }
                    }
                }
            },{
                "getHtml": function(){
                    if (_self.data.control.allowEdit){
                        return "<div style='width:24px; height:24px; cursor: pointer; background:url(../x_component_Org/$Explorer/"+
                            _self.explorer.app.options.style+"/icon/edit.png) center center no-repeat'></div>";
                    }
                    return "";
                },
                "events": {
                    "click": function(){
                        if (_self.data.control.allowEdit){_self.editIdentity(this.data, this.td, this.item);}
                    }
                }
            }]
        });
        this.identityList.load([
            {"style": "width: 12%", "text": this.explorer.app.lp.IdentityName},
            {"style": "width: 12%", "text": this.explorer.app.lp.IdentityInUnit},
            {"style": "width: 30%", "text": this.explorer.app.lp.personUnique},
            {"style": "width: 34%", "text": this.explorer.app.lp.IdentityDuty},
            {"style": "width: 10%", "text": this.explorer.app.lp.IdentityMain},
            {"style": "width: 30px", "text": ""}
        ]);

        this.data.woIdentityList.each(function(item){
            this.identityList.push(item);
        }.bind(this));
    },
    setMainIdentity: function(data, node, item){

        data.major = true;
        this.explorer.actions.saveIdentity(data, function(json){
            this.explorer.actions.getPerson(function(iJson){
                // data = iJson.data;
                // item.reload(iJson.data);
                this.data.woIdentityList = iJson.data.woIdentityList;
                this.identityList.clear();
                this.data.woIdentityList.each(function(item){
                    this.identityList.push(item);
                }.bind(this));

            }.bind(this), null, this.data.id);
        }.bind(this));
    },
    editIdentity: function(data, node, item){
        var _self = this;
        var position = node.getPosition(this.explorer.app.content);
        var width = 700;
        var height = 170;
        var size = this.explorer.app.content.getSize();
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (x<0) x = 0;
        if (y<20) y = 20;

        MWF.require("MWF.xDesktop.Dialog", function() {
            var dlg = new MWF.xDesktop.Dialog({
                "title": this.explorer.app.lp.modifyIdentity,
                "style": "org",
                "top": y - 20,
                "left": x,
                "fromTop": position.y - 20,
                "fromLeft": position.x,
                "width": width,
                "height": height,
                "html": "<div></div>",
                "maskNode": this.explorer.app.content,
                "container": this.explorer.app.content,
                "buttonList": [
                    {
                        "text": MWF.LP.process.button.ok,
                        "action": function () {
                            _self.saveIdentity(dlg, data, item);
                            this.close();
                        }
                    },
                    {
                        "text": MWF.LP.process.button.cancel,
                        "action": function () {
                            this.close();
                        }
                    }
                ]
            });
            dlg.show();
            var node = dlg.content.getFirst();
            var html = "<table width='90%' cellpadding='0px' cellspacing='5px' align='center' style='margin-top:10px'>" +
                "<tr><th width='30%'>"+this.explorer.app.lp.IdentityName+"</th><th>"+this.explorer.app.lp.personUnique+"</th><th>"+this.explorer.app.lp.IdentityMain+"</th></tr>" +
                "<tr><td style='text-align: center'><input value='' type='type' style='padding: 0px 3px; width: 95%; border: 1px solid #cccccc; height: 24px; border-radius: 3px; line-height: 24px;'/></td>" +
                "<td style='text-align: center'><input value='' type='type' style='padding: 0px 3px; width: 95%; border: 1px solid #cccccc; height: 24px; border-radius: 3px; line-height: 24px;'/></td>" +
                "<td style='text-align: center'><input value='yes' type='checkbox' "+((data.major) ? "checked" : "")+"/></td></tr></table>";

            node.set("html", html);
            var inputs = node.getElements("input");
            if (inputs[0]) inputs[0].set("value", data.name);
            if (inputs[1]) inputs[1].set("value", data.unique);
            //if (inputs[2]) inputs[2].set("value", data.major)

        }.bind(this));
    },
    saveIdentity: function(dlg, data, item){
        var node = dlg.content.getFirst();
        var inputs = node.getElements("input");
        var name = inputs[0].get("value");
        var unique = inputs[1].get("value");
        var major = (inputs[2].checked);

        if (data.name!==name || data.unique!==unique){
            if (name) data.name = name;
            data.unique=unique;
            data.major = major;
            this.explorer.actions.saveIdentity(data, function(json){
                this.explorer.actions.getPerson(function(iJson){
                    // data = iJson.data;
                    // item.reload(iJson.data);
                    this.data.woIdentityList = iJson.data.woIdentityList;
                    this.identityList.clear();
                    this.data.woIdentityList.each(function(item){
                        this.identityList.push(item);
                    }.bind(this));

                }.bind(this), null, this.data.id);
            }.bind(this));
        }
    },
    _listRole: function(){
        var _self = this;
        this.roleList = new MWF.xApplication.Org.List(this.roleContentNode, this, {
            "action": false,
            "canEdit": false,
            "data": {
                // "person": this.data.id,
                // "name": "",
                // "unique": "",
                // "orderNumber": "",
                // "attributeList": [],
                // "description":""
            },
            "attr": ["name",
                "distinguishedName",
                "description",{
                    "getHtml": function(){
                        if (_self.data.control.allowEdit){
                            return "<div style='width:24px; height:24px; cursor: pointer; background:url(../x_component_Org/$Explorer/"+
                                _self.explorer.app.options.style+"/icon/open.png) center center no-repeat'></div>";
                        }
                        return "";
                    },
                    "events": {
                        "click": function(){
                            _self.explorer.openRole(this.data, this.td);
                        }
                    }
                }]
        });
        this.roleList.load([
            {"style": "width: 15%", "text": this.explorer.app.lp.roleName},
            {"style": "width: 30%", "text": this.explorer.app.lp.roleFullName},
            {"style": "", "text": this.explorer.app.lp.description},
            {"style": "width: 30px", "text": ""}
        ]);

        if( this.data.id ){
            // o2.Actions.load("x_organization_assemble_control").RoleAction.listWithPerson(this.data.id, function (json) {
            //     this.roleDataList = json.data;
            //     json.data.each( function ( item ) {
            //         this.roleList.push(item);
            //     }.bind(this))
            // }.bind(this), null, false);
            if( this.data.woRoleList && this.data.woRoleList.length ){
                this.roleDataList = this.data.woRoleList;
                this.data.woRoleList.each( function ( item ) {
                    this.roleList.push(item);
                }.bind(this))
            }

        }

        // this.data.woPersonAttributeList.each(function(item){
        //     this.roleList.push(item);
        // }.bind(this));
    }
});
MWF.xApplication.Org.PersonExplorer.PersonContent.TitleInfor = new Class({
    Extends: MWF.xApplication.Org.$Explorer.ItemContent.TitleInfor,
    getActionPermission: function(){
        if(MWF.AC.isManager())return true;
        if(MWF.AC.isSecurityManager())return true;
        if(MWF.AC.isSystemManager())return false;
        if(MWF.AC.isOrganizationManager())return true;
        if(MWF.AC.isPersonManager())return true;
        return false;
    },
    loadAction: function(){
        //this.explorer.app.lp.edit
        this.nameNode.setStyle("margin-right", "80px");
        if (this.getActionPermission()){

            this.resetPasswordAction = new Element("div", {"styles": this.style.titleInforResetPasswordNode, "text": this.item.explorer.app.lp.resetPassword}).inject(this.nameNode, "before");
            this.resetPasswordAction.addEvent("click", function(e){this.resetPassword(e);}.bind(this));

            this.expiredTimeAction = new Element("div", {"styles": this.style.titleInforUnlockPersonNode, "text": this.item.explorer.app.lp.expiredTime}).inject(this.nameNode, "before");
            this.expiredTimeAction.addEvent("click", function(e){this.setPasswordExpiredTime(e);}.bind(this));

            this.lockPersonAction = new Element("div", {"styles": this.style.titleInforUnlockPersonNode, "text": this.item.explorer.app.lp.lockPerson}).inject(this.nameNode, "before");
            this.lockPersonAction.addEvent("click", function(e){this.lockPerson(e);}.bind(this));
            if( this.data.status === '1' )this.lockPersonAction.hide(); //data.status = 1 表示已锁定

            this.unlockPersonAction = new Element("div", {"styles": this.style.titleInforUnlockPersonNode, "text": this.item.explorer.app.lp.unlockPerson}).inject(this.nameNode, "before");
            this.unlockPersonAction.addEvent("click", function(e){this.unlockPerson(e);}.bind(this));
            if( this.data.status !== '1' )this.unlockPersonAction.hide();

            this.banPersonAction = new Element("div", {"styles": this.style.titleInforUnlockPersonNode, "text": this.item.explorer.app.lp.banPerson}).inject(this.nameNode, "before");
            this.banPersonAction.addEvent("click", function(e){this.banPerson(e);}.bind(this));
            if( this.data.status === '2' )this.banPersonAction.hide(); //data.status = 2 表示已禁用

            this.unbanPersonAction = new Element("div", {"styles": this.style.titleInforUnlockPersonNode, "text": this.item.explorer.app.lp.unbanPerson}).inject(this.nameNode, "before");
            this.unbanPersonAction.addEvent("click", function(e){this.unbanPerson(e);}.bind(this));
            if( this.data.status !== '2' )this.unbanPersonAction.hide();

        }
        if (this.data.control.allowEdit){
            this.iconNode.setStyle("cursor", "pointer");
            this.iconNode.addEvent("click", function(){this.changePersonIcon();}.bind(this));
        }
    },
    banPerson: function (){
        var form = new MWF.xApplication.Org.PersonExplorer.BanPersonForm(this.explorer, {}, {
            name :  this.data.name || "",
            onPostOk : function( reason ){
                o2.Actions.load("x_organization_assemble_control").PersonAction.banPerson(this.data.id, {
                    desc: reason
                }, function(){
                    this.explorer.app.notice( this.explorer.app.lp.banPersonSuccess.replace('{name}', this.data.name) );
                    this.unbanPersonAction.show();
                    this.banPersonAction.hide();
                    window.setTimeout( function (){
                        this.explorer.currentItem.changeSelectedItem();
                    }.bind(this), 300)
                }.bind(this));
            }.bind(this)
        });
        form.create();
    },
    unbanPerson: function(e){
        var _self = this;
        var text = this.item.explorer.app.lp.unbanPersonText.replace("{name}", this.data.name);
        this.item.explorer.app.confirm("info", e, this.item.explorer.app.lp.unbanPersonTitle, text, "360", "120", function(){
            _self.doUnbanPerson();
            this.close();
        }, function(){
            this.close();
        });
    },
    doUnbanPerson: function (e){
        MWF.Actions.load("x_organization_assemble_control").PersonAction.unbanPerson(this.data.id, {}, function(){
            var text = this.item.explorer.app.lp.unbanPersonSuccess;
            text = text.replace("{name}", this.data.name);
            this.item.explorer.app.notice(text, "success");
            this.unbanPersonAction.hide();
            this.banPersonAction.show();
            window.setTimeout( function (){
                this.explorer.currentItem.changeSelectedItem();
            }.bind(this), 300)
        }.bind(this));
    },
    resetPassword: function(e){
        var _self = this;
        var text = this.item.explorer.app.lp.resetPasswordText;
        text = text.replace("{name}", this.data.name);
        this.item.explorer.app.confirm("info", e, this.item.explorer.app.lp.resetPasswordTitle, text, "360", "120", function(){
            _self.doResetPassword();
            this.close();
        }, function(){
            this.close();
        });
    },
    doResetPassword: function(){
        var action = MWF.Actions.get("x_organization_assemble_control");
        action.resetPassword(this.data.id, function(){
            var text = this.item.explorer.app.lp.resetPasswordSuccess;
            text = text.replace("{name}", this.data.name);
            this.item.explorer.app.notice(text, "success");
        }.bind(this));
    },
    lockPerson: function (e){
        var form = new MWF.xApplication.Org.PersonExplorer.LockPersonForm(this.explorer, {}, {
            name :  this.data.name || "",
            onPostOk : function( lockExpiredTime, reason ){
                o2.Actions.load("x_organization_assemble_control").PersonAction.lockPerson(this.data.id, {
                    lockExpiredTime: lockExpiredTime,
                    desc: reason
                }, function(){
                    this.explorer.app.notice( this.explorer.app.lp.lockPersonSuccess.replace('{name}', this.data.name) );
                    this.unlockPersonAction.show();
                    this.lockPersonAction.hide();
                    window.setTimeout( function (){
                        this.explorer.currentItem.changeSelectedItem();
                    }.bind(this), 300);
                }.bind(this));
            }.bind(this)
        });
        form.create();
    },
    unlockPerson : function(e){
        var _self = this;
        var text = this.item.explorer.app.lp.unlockPersonText;
        text = text.replace("{name}", this.data.name);
        this.item.explorer.app.confirm("info", e, this.item.explorer.app.lp.unlockPersonTitle, text, "360", "120", function(){
            _self.doUnlockPerson();
            this.close();
        }, function(){
            this.close();
        });
    },
    doUnlockPerson: function(){
        MWF.Actions.load("x_organization_assemble_control").PersonAction.unlockPerson(this.data.id, function(){
            var text = this.item.explorer.app.lp.unlockPersonSuccess;
            text = text.replace("{name}", this.data.name);
            this.item.explorer.app.notice(text, "success");
            this.unlockPersonAction.hide();
            this.lockPersonAction.show();
            window.setTimeout( function (){
                this.explorer.currentItem.changeSelectedItem();
            }.bind(this), 300);
        }.bind(this));
    },
    setPasswordExpiredTime: function(){
        var form = new MWF.xApplication.Org.PersonExplorer.PasswordExpiredTimeForm(this.explorer, {}, {
            expiredTime :  this.data.expiredTime || "",
            onPostOk : function( expiredTime ){
                o2.Actions.load("x_organization_assemble_control").PersonAction.setPasswordExpiredTime(this.data.id, expiredTime, function(){
                    this.content.bottomInfor.setPasswordExpiredTime(expiredTime);
                    this.explorer.app.notice( this.explorer.app.lp.expiredTimeSuccess );
                }.bind(this))
            }.bind(this)
        });
        form.create();
    },
    changePersonIcon: function(){
        var options = {};
        var width = "668";
        var height = "510";
        width = width.toInt();
        height = height.toInt();

        var size = this.explorer.app.content.getSize();
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (x<0) x = 0;
        if (y<0) y = 0;
        if (layout.mobile){
            x = 20;
            y = 0;
        }

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function() {
            MWF.require("MWF.widget.ImageClipper", function(){
                var dlg = new MWF.xDesktop.Dialog({
                    "title": this.explorer.app.lp.changePersonIcon,
                    "style": "image",
                    "top": y,
                    "left": x - 20,
                    "fromTop": y,
                    "fromLeft": x - 20,
                    "width": width,
                    "height": height,
                    "html": "<div></div>",
                    "maskNode": this.explorer.app.content,
                    "container": this.explorer.app.content,
                    "buttonList": [
                        {
                            "text": MWF.LP.process.button.ok,
                            "action": function () {
                                _self.uploadPersonIcon();
                                this.close();
                            }
                        },
                        {
                            "text": MWF.LP.process.button.cancel,
                            "action": function () {
                                _self.image = null;
                                this.close();
                            }
                        }
                    ]
                });
                dlg.show();

                this.image = new MWF.widget.ImageClipper(dlg.content.getFirst(), {
                    "aspectRatio": 1,
                    "description" : "",
                    "imageUrl" : this._getIcon(true),
                    "resetEnable" : false
                });
                this.image.load();
            }.bind(this));
        }.bind(this))
    },
    uploadPersonIcon: function(){
        if (this.image){
            if( this.image.getResizedImage() ){
                this.explorer.actions.changePersonIcon(this.data.id, this.image.getFormData(), this.image.getResizedImage(), function(){
                    this.iconNode.set("src", "");
                    if (this.item.iconNode) this.item.iconNode.getElement("img").set("src", "");
                    window.setTimeout(function(){
                        this.iconNode.set("src", this._getIcon(true));
                        if (this.item.iconNode) this.item.iconNode.getElement("img").set("src", this.item._getIcon(true));
                    }.bind(this), 100);
                }.bind(this), null);
            }
        }
    }
});
MWF.xApplication.Org.PersonExplorer.PersonContent.BottomInfor = new Class({
    Extends: MWF.xApplication.Org.$Explorer.ItemContent.BottomInfor,
    addInforList: function(){
        var text = this.explorer.app.lp.personReadDn.replace(/{dn}/g, (this.data.distinguishedName || " "));
        this.addInfor(text);

        text = this.explorer.app.lp.personReadCreate.replace(/{date}/g, (this.data.createTime || " "));
        text = text.replace(/{date2}/g, (this.data.updateTime || " "));
        this.addInfor(text);

        text = this.explorer.app.lp.personReadLogin.replace(/{date}/g, (this.data.lastLoginTime || " "));
        text = text.replace(/{ip}/g, (this.data.lastLoginAddress || " "));
        text = text.replace(/{client}/g, (this.data.lastLoginClient || " "));
        this.addInfor(text);

        text = this.explorer.app.lp.personReadPassword.replace(/{date}/g, (this.data.passwordExpiredTime || " "));
        text = text.replace(/{date2}/g, (this.data.changePasswordTime || " "));
        this.passwordExpiredTimeNode = this.addInfor(text);
    },
    setPasswordExpiredTime: function ( passwordExpiredTime ) {
        var text = this.explorer.app.lp.personReadPassword.replace(/{date}/g, (passwordExpiredTime || " "));
        text = text.replace(/{date2}/g, (this.data.changePasswordTime || " "));
        this.passwordExpiredTimeNode.set("text", text);
    }
});

MWF.xApplication.Org.PersonExplorer.PersonContent.BaseInfor = new Class({
    initialize: function(content){
        this.content = content;
        this.item = content.item;
        this.data = this.content.data;
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

        n = this.editContentNode.getElement(".infor_employee");
        if (n) n.set("text", this.data.employee || "");

        n = this.editContentNode.getElement(".infor_mobile");
        if (n) n.set("text", this.data.mobile || "");

        n = this.editContentNode.getElement(".infor_unique");
        if (n) n.set("text", this.data.unique || "");

        n = this.editContentNode.getElement(".infor_gender");
        if (n) n.set("text", this.getGenderType());

        n = this.editContentNode.getElement(".infor_mail");
        if (n) n.set("text", this.data.mail || "");

        n = this.editContentNode.getElement(".infor_weixin");
        if (n) n.set("text", this.data.weixin || "");

        n = this.editContentNode.getElement(".infor_qq");
        if (n) n.set("text", this.data.qq || "");

        n = this.editContentNode.getElement(".infor_officePhone");
        if (n) n.set("text", this.data.officePhone || "");

        n = this.editContentNode.getElement(".infor_boardDate");
        if (n) n.set("text", this.data.boardDate || "");

        n = this.editContentNode.getElement(".infor_birthday");
        if (n) n.set("text", this.data.birthday || "");

        n = this.editContentNode.getElement(".infor_ipAddress");
        if (n) n.set("text", this.data.ipAddress || "");

        n = this.editContentNode.getElement(".infor_description");
        if (n) n.set("text", this.data.description || "");

        n = this.editContentNode.getElement(".infor_securityLabel");
        this.getSecurityLabelText().then(function(securityLabel){
            if (this.mode !== "edit") if (n) n.set("text", securityLabel || "");
        }.bind(this));


        this.editContentNode.getElements("td.inforTitle").setStyles(this.style.baseInforTitleNode);
        this.editContentNode.getElements("td.inforContent").setStyles(this.style.baseInforContentNode);
        this.editContentNode.getElements("td.inforAction").setStyles(this.style.baseInforActionNode);

        var tdContents = this.editContentNode.getElements("td.inforContent");
        if (this.data.superior) new MWF.widget.O2Person({"name": this.data.superior}, tdContents[5], {"style": "xform"});

        this.loadAction();
    },

    getSecurityLabelText(){
        return this.getSecurityLabelList().then(function(labelList){

            var securityLabel = "";
            var keys = Object.keys(labelList);
            for (var i=0; i<keys.length; i++){
                var value = this.content.securityLabelList[keys[i]];
                if (value === this.data.subjectSecurityClearance){
                    securityLabel = keys[i];
                    break;
                }
            }
            return securityLabel;

        }.bind(this));
    },
    getSecurityLabelList: function(){
        if (this.content.securityLabelList) return Promise.resolve(this.content.securityLabelList);

        return o2.Actions.load("x_general_assemble_control").SecurityClearanceAction.subject().then(function(json){
            this.content.securityLabelList = json.data;
            return this.content.securityLabelList;
        }.bind(this));
    },

    getContentHtml: function(){
        var statusText = "";
        this.explorer.app.lp.statusOption.each(function (opt){
            if( opt.value === (this.data.status || "0") )statusText = opt.text;
        }.bind(this));

        var html = "<table width='100%' cellpadding='3px' cellspacing='5px'>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personName+":</td><td class='inforContent infor_name'>"+(this.data.name || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personUnique+":</td><td class='inforContent infor_unique'>"+(this.data.unique || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personMobile+":</td><td class='inforContent infor_mobile'>"+(this.data.mobile || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personEmployee+":</td><td class='inforContent infor_employee'>"+(this.data.employee || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personGender+":</td><td class='inforContent infor_gender'>"+this.getGenderType()+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personSuperior+":</td><td class='inforContent'>"+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personMail+":</td><td class='inforContent infor_mail'>"+(this.data.mail || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personWeixin+":</td><td class='inforContent infor_weixin'>"+(this.data.weixin || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personQQ+":</td><td class='inforContent infor_qq'>"+(this.data.qq || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personOfficePhone+":</td><td class='inforContent infor_officePhone'>"+(this.data.officePhone || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personBoardDate+":</td><td class='inforContent infor_boardDate'>"+(this.data.boardDate || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personBirthday+":</td><td class='inforContent infor_birthday'>"+(this.data.birthday || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.ipAddress+":</td><td class='inforContent infor_ipAddress'>"+(this.data.ipAddress || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.description+":</td><td class='inforContent infor_description'>"+(this.data.description || "")+"</td></tr>";

        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.securityLabel+":</td><td class='inforContent infor_securityLabel'>"+(this.data.subjectSecurityClearance || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.status+":</td><td class='inforContent infor_status'>"+(statusText || "")+"</td></tr>";

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

            this.editNode = new Element("div", {"styles": this.style.actionEditNode, "text": this.explorer.app.lp.editPerson}).inject(this.baseInforEditActionAreaNode);

            this.saveNode = new Element("div", {"styles": this.style.actionSaveNode, "text": this.explorer.app.lp.savePerson}).inject(this.baseInforEditActionAreaNode);
            this.saveNode.addClass("mainColor_bg");
            this.cancelNode = new Element("div", {"styles": this.style.actionCancelNode, "text": this.explorer.app.lp.cancel}).inject(this.baseInforEditActionAreaNode);

            this.editNode.setStyle("display", "block");
            this.editNode.addEvent("click", this.edit.bind(this));
            this.saveNode.addEvent("click", this.save.bind(this));
            this.cancelNode.addEvent("click", this.cancel.bind(this));
        }else{

        }
    },
    edit: function(){
        debugger;
        var tdContents = this.editContentNode.getElements("td.inforContent");
        tdContents[0].setStyles(this.style.baseInforContentNode_edit).empty();
        this.nameInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[0]);
        this.nameInputNode.set("value", (this.data.name));

        tdContents[1].setStyles(this.style.baseInforContentNode_edit).empty();
        this.uniqueInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[1]);
        this.uniqueInputNode.set("value", (this.data.unique));
        if( this.data.id ){
            this.tooltip = new MWF.xApplication.Org.PersonExplorer.PersonContent.UniqueTooltip(this.explorer.app.content, tdContents[1], this.explorer.app, {}, {
                axis : "y",
                position : {
                    x : "right"
                },
                hiddenDelay : 300,
                displayDelay : 300
            });
        }

        tdContents[2].setStyles(this.style.baseInforContentNode_edit).empty();
        this.mobileInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[2]);
        this.mobileInputNode.set("value", (this.data.mobile));

        tdContents[3].setStyles(this.style.baseInforContentNode_edit).empty();
        this.employeeInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[3]);
        this.employeeInputNode.set("value", (this.data.employee));

        tdContents[4].setStyles(this.style.baseInforContentNode_edit).empty();
        var html = "<input name=\"personGenderRadioNode\" value=\"m\" type=\"radio\" "+((this.data.genderType==="m") ? "checked" : "")+"/>"+this.explorer.app.lp.man;
        html += "<input name=\"personGenderRadioNode\" value=\"f\" type=\"radio\" "+((this.data.genderType==="f") ? "checked" : "")+"/>"+this.explorer.app.lp.female;
        html += "<input name=\"personGenderRadioNode\" value=\"d\" type=\"radio\" "+((this.data.genderType==="d") ? "checked" : "")+"/>"+this.explorer.app.lp.other;
        tdContents[4].set("html", html);

        tdContents[5].setStyles(this.style.baseInforContentNode_edit).empty();
        this.superiorInputNode = new Element("div", {"styles": this.style.inputNode_person}).inject(tdContents[5]);
        //this.superiorInputNode.set("value", (this.data.superior));
        if (this.data.superior) new MWF.widget.O2Person({"name": this.data.superior}, this.superiorInputNode, {"style": "xform"});
        this.superiorInputNode.addEvent("click", function(){
            MWF.xDesktop.requireApp("Selector", "package", function(){
                var options = {
                    "type": "person",
                    "values": (this.data.superior) ? [this.data.superior] : [],
                    "count": 1,
                    "onComplete": function(items){
                        this.superiorInputNode.empty();
                        if (items.length){
                            this.data.superior = items[0].data.distinguishedName;
                            new MWF.widget.O2Person(items[0].data, this.superiorInputNode, {"style": "xform"});
                        }else{
                            this.data.superior = "";
                        }
                    }.bind(this)
                };
                var selector = new MWF.O2Selector(this.explorer.app.content, options);
            }.bind(this));
        }.bind(this));

        tdContents[6].setStyles(this.style.baseInforContentNode_edit).empty();
        this.mailInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[6]);
        this.mailInputNode.set("value", (this.data.mail));

        tdContents[7].setStyles(this.style.baseInforContentNode_edit).empty();
        this.weixinInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[7]);
        this.weixinInputNode.set("value", (this.data.weixin));

        tdContents[8].setStyles(this.style.baseInforContentNode_edit).empty();
        this.qqInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[8]);
        this.qqInputNode.set("value", (this.data.qq));

        tdContents[9].setStyles(this.style.baseInforContentNode_edit).empty();
        this.officePhoneInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[9]);
        this.officePhoneInputNode.set("value", (this.data.officePhone));

        tdContents[10].setStyles(this.style.baseInforContentNode_edit).empty();
        this.boardDateInputNode = new Element("input", {"styles": this.style.inputNode_calendar, "readonly": true}).inject(tdContents[10]);
        this.boardDateInputNode.set("value", (this.data.boardDate));

        MWF.require("MWF.widget.Calendar", function(){
            var boardDateCalendar = new MWF.widget.Calendar(this.boardDateInputNode, {
                "style": "xform",
                "isTime": false,
                "target": this.explorer.app.content,
                "format": "%Y-%m-%d"
            });
        }.bind(this));

        tdContents[11].setStyles(this.style.baseInforContentNode_edit).empty();
        this.birthdayInputNode = new Element("input", {"styles": this.style.inputNode_calendar, "readonly": true}).inject(tdContents[11]);
        this.birthdayInputNode.set("value", (this.data.birthday));

        MWF.require("MWF.widget.Calendar", function(){
            var birthdayCalendar = new MWF.widget.Calendar(this.birthdayInputNode, {
                "style": "xform",
                "isTime": false,
                "target": this.explorer.app.content,
                "format": "%Y-%m-%d"
            });
        }.bind(this));

        tdContents[12].setStyles(this.style.baseInforContentNode_edit).empty();
        this.ipAddressInputNode = new Element("input", {
            "styles": this.style.inputNode,
            "placeHolder": this.explorer.app.lp.ipAddressPlaceHolder,
            "autocomplete": "off",
            "title": this.explorer.app.lp.ipAddressPlaceHolder
        }).inject(tdContents[12]);
        this.ipAddressInputNode.set("value", (this.data.ipAddress));


        tdContents[13].setStyles(this.style.baseInforContentNode_edit).empty();
        this.descriptionInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[13]);
        this.descriptionInputNode.set("value", (this.data.description));

        tdContents[14].setStyles(this.style.baseInforContentNode_edit).empty();
        var securityLabel = this.data.subjectSecurityClearance;
        this.securityLabelSelectNode = new Element("select", {"styles": this.style.selectNode}).inject(tdContents[14]);
        new Element("option", {value: "", text: ""}).inject(this.securityLabelSelectNode);

        this.getSecurityLabelList().then(function(securityLabelList){
            if (securityLabelList) Object.keys(securityLabelList).forEach(function(key){
                var value = securityLabelList[key];
                var option = new Element("option", {value: value, text: key}).inject(this.securityLabelSelectNode);
                if (securityLabel === value){
                    option.selected = true;
                }
            }.bind(this));
        }.bind(this));

        // this.securityLabelSelectNode.set("value", (this.data.description));

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
    save: function(){
        var tdContents = this.editContentNode.getElements("td.inforContent");
        var gender = "";
        var radios = tdContents[4].getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                gender = radios[i].value;
                break;
            }
        }

        return o2.Actions.load("x_general_assemble_control").SecurityClearanceAction.enable().then(function(json){
            if (json.data.enable===true){
                var label = this.securityLabelSelectNode.options[this.securityLabelSelectNode.selectedIndex].value;
                if (!label || !this.nameInputNode.get("value") || !this.mobileInputNode.get("value") || !this.uniqueInputNode.get("value") || !gender){
                    this.explorer.app.notice(this.explorer.app.lp.inputPersonInfor2, "error", this.explorer.propertyContentNode);
                    return false;
                }
            }else{
                if (!this.nameInputNode.get("value") || !this.mobileInputNode.get("value") || !this.uniqueInputNode.get("value") || !gender){
                    this.explorer.app.notice(this.explorer.app.lp.inputPersonInfor, "error", this.explorer.propertyContentNode);
                    return false;
                }
            }

            if (!this.uniqueInputNode.get("value")) this.data.unique = this.employeeInputNode.get("value");
            this.content.propertyContentScrollNode.mask({
                "style": {
                    "opacity": 0.7,
                    "background-color": "#999"
                }
            });

            this.savePerson(function(){
                this.cancel();
                this.content.propertyContentScrollNode.unmask();
            }.bind(this), function(xhr, text, error){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
                this.content.propertyContentScrollNode.unmask();
            }.bind(this));
        }.bind(this));
    },
    savePerson: function(callback, cancel){
        var data = Object.clone(this.data);
        data.name = this.nameInputNode.get("value");
        data.employee = this.employeeInputNode.get("value");
        data.mobile = this.mobileInputNode.get("value");
        data.unique = this.uniqueInputNode.get("value");
        //data.superior = this.superiorInputNode.get("value");
        data.mail = this.mailInputNode.get("value");
        data.weixin = this.weixinInputNode.get("value");
        data.qq = this.qqInputNode.get("value");
        data.officePhone = this.officePhoneInputNode.get("value");
        data.boardDate = this.boardDateInputNode.get("value");
        data.birthday = this.birthdayInputNode.get("value");
        data.ipAddress = this.ipAddressInputNode.get("value");
        data.description = this.descriptionInputNode.get("value");
        data.description = this.descriptionInputNode.get("value");
        var securityLabel = this.securityLabelSelectNode.options[this.securityLabelSelectNode.selectedIndex].value;
        data.subjectSecurityClearance = (securityLabel) ? parseInt(securityLabel) : null;

        var tdContents = this.editContentNode.getElements("td.inforContent");
        var radios = tdContents[4].getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                data.genderType = radios[i].value;
                break;
            }
        }
        this.explorer.actions.savePerson(data, function(json){
            Object.merge(this.data, data);
            if (this.data.id){
                this.data.id = json.data.id;
                this.item.refresh();
                if (callback) callback();
            }else{
                this.explorer.actions.getPerson(function(json){
                    this.data = Object.merge(this.data, json.data);
                    this.item.data = this.data;
                    this.item.refresh();

                    this.content.loadList();

                    if (callback) callback();
                }.bind(this), null, json.data.id);
            }
        }.bind(this), function(xhr, text, error){
            if (cancel) cancel(xhr, text, error);
        }.bind(this));
        // }.bind(this), function(xhr, text, error){
        //     if (cancel) cancel(xhr, text, error);
        // }.bind(this));
    },
    cancel: function(){
        debugger;
        if (this.data.id){
            var tdContents = this.editContentNode.getElements("td.inforContent");
            tdContents[0].setStyles(this.style.baseInforContentNode).set("text", this.data.name || "");
            tdContents[1].setStyles(this.style.baseInforContentNode).set("text", this.data.unique || "");
            tdContents[2].setStyles(this.style.baseInforContentNode).set("text", this.data.mobile || "");
            tdContents[3].setStyles(this.style.baseInforContentNode).set("text", this.data.employee || "");
            tdContents[4].setStyles(this.style.baseInforContentNode).set("text", this.getGenderType());
            tdContents[5].setStyles(this.style.baseInforContentNode).set("text", "");
            if (this.data.superior) new MWF.widget.O2Person({"name": this.data.superior}, tdContents[5], {"style": "xform"});

            tdContents[6].setStyles(this.style.baseInforContentNode).set("text", this.data.mail || "");
            tdContents[7].setStyles(this.style.baseInforContentNode).set("text", this.data.weixin || "");
            tdContents[8].setStyles(this.style.baseInforContentNode).set("text", this.data.qq || "");
            tdContents[9].setStyles(this.style.baseInforContentNode).set("text", this.data.officePhone || "");
            tdContents[10].setStyles(this.style.baseInforContentNode).set("text", this.data.boardDate || "");
            tdContents[11].setStyles(this.style.baseInforContentNode).set("text", this.data.birthday || "");
            tdContents[12].setStyles(this.style.baseInforContentNode).set("text", this.data.ipAddress || "");
            tdContents[13].setStyles(this.style.baseInforContentNode).set("text", this.data.description || "");

            this.getSecurityLabelText().then(function(securityLabel){
                tdContents[14].setStyles(this.style.baseInforContentNode).set("text", securityLabel || "");
            }.bind(this));



            this.mode = "read";

            this.editNode.setStyle("display", "block");
            this.saveNode.setStyle("display", "none");
            this.cancelNode.setStyle("display", "none");
        }else{
            this.item.destroy();
        }
        if( this.tooltip ){
            this.tooltip.destroy();
            this.tooltip = null;
        }
    },

    getGenderType: function(){
        var text = "";
        if (this.data.genderType){
            switch (this.data.genderType) {
                case "m":
                    text = this.explorer.app.lp.man;
                    break;
                case "f":
                    text = this.explorer.app.lp.female;
                    break;
                default:
                    text = this.explorer.app.lp.other;
            }
        }
        return text;
    },
    destroy: function(){
        if( this.tooltip ){
            this.tooltip.destroy();
            this.tooltip = null;
        }
        this.node.empty();
        this.node.destroy();
        MWF.release(this);
    },
    _getIcon: function(nocache){
        var url = (this.data.id) ? this.explorer.actions.getPersonIcon(this.data.id) : "../x_component_Org/$Explorer/default/icon/man.png";
        return (nocache) ? url+"?"+(new Date().getTime()) : url;
        //return (this.data.id) ? this.explorer.actions.getPersonIcon(this.data.id) : "../x_component_Org/$Explorer/default/icon/man.png";
        // var src = "data:image/png;base64,"+this.data.icon;
        // if (!this.data.icon){
        //     if (this.data.genderType==="f"){
        //         src = "../x_component_Org/$Explorer/default/icon/female.png"
        //     }else{
        //         src = "../x_component_Org/$Explorer/default/icon/man.png"
        //     }
        // }
        // return src;
    }
});

MWF.xApplication.Org.PersonExplorer.PersonContent.UniqueTooltip = new Class({
    Extends: MTooltips,
    _getHtml : function(){
        var html =
            "<div item='containr' style='line-height:24px;'><div style='font-size: 14px;color:red;float:left; '>"+ this.lp.personUniqueModifyNote +"</div></div>";
        return html;
    }
});

MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.Org.PersonExplorer.PasswordExpiredTimeForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "cms_xform",
        "width": "580",
        "height": "220",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.Org.LP.expiredTimeTitle,
        "draggable": true,
        "closeAction": true,
        "publishTime": ""
    },
    _createTableContent: function () {
        this.formTopTextNode.addClass("mainColor_color");

        this.formAreaNode.setStyle("z-index", 1002);
        this.formMaskNode.setStyle("z-index", 1002);
        this.formTableContainer.setStyles({
            "margin":"20px 40px 0px 40px"
        });
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableValue' item='expiredTime'></td>" +
            "</tr>"+
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: "meeting",
                isEdited: true,
                itemTemplate: {
                    expiredTime: {
                        tType: "date",
                        notEmpty: true,
                        value: this.options.expiredTime || "",
                        attr: {
                            "readonly":true
                        },
                        calendarOptions : {
                            "secondEnable": false,
                            "format": "%Y-%m-%d",
                            "onShow": function () {
                                this.container.setStyle("z-index", 1003 );
                            }
                        }
                    }
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);
    },
    _createBottomContent: function () {

        this.closeActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.cancel
        }).inject(this.formBottomNode);

        this.closeActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css.formOkActionNode,
            "text": this.lp.ok
        }).inject(this.formBottomNode);
        this.okActionNode.addClass("mainColor_bg");

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));


    },
    ok: function (e) {
        this.fireEvent("queryOk");

        var result = this.form.getResult(true, null);
        if( !result ){
            this.app.notice(this.lp.inputExpiredTime, "error");
            return;
        }
        (this.formMaskNode || this.formMarkNode).destroy();
        this.formAreaNode.destroy();
        this.fireEvent("postOk", result.expiredTime);

    }
});

MWF.xApplication.Org.PersonExplorer.LockPersonForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "cms_xform",
        "width": "580",
        "height": "330",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.Org.LP.lockPerson,
        "draggable": true,
        "closeAction": true,
        "lockExpiredTime": ""
    },
    _createTableContent: function () {
        this.formTopTextNode.addClass("mainColor_color");

        this.formAreaNode.setStyle("z-index", 1002);
        this.formMaskNode.setStyle("z-index", 1002);
        this.formTableContainer.setStyles({
            "margin":"20px 40px 0px 40px"
        });
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableValue' style='width:70px;'></td>" +
            "   <td styles='formTableValue'>"+MWF.xApplication.Org.LP.lockPersonInfo+"</td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableValue' style='width:70px;' lable='lockExpiredTime'></td>" +
            "   <td styles='formTableValue' item='lockExpiredTime'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableValue' style='width:70px;' lable='reason'></td>" +
            "   <td styles='formTableValue' item='reason'></td>" +
            "</tr>"+
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: "meeting",
                isEdited: true,
                itemTemplate: {
                    lockExpiredTime: {
                        text: MWF.xApplication.Org.LP.lockLimitTime,
                        tType: "date",
                        notEmpty: true,
                        value: this.options.lockExpiredTime || "",
                        attr: {
                            "readonly":true
                        },
                        calendarOptions : {
                            "secondEnable": false,
                            "format": "%Y-%m-%d",
                            "onShow": function () {
                                this.container.setStyle("z-index", 1003 );
                            }
                        }
                    },
                    reason: {
                        text: MWF.xApplication.Org.LP.lockPersonReason,
                        type: "textarea",
                        notEmpty: true,
                        value: this.options.reason || ""
                    }
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);
    },
    _createBottomContent: function () {

        this.closeActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.cancel
        }).inject(this.formBottomNode);

        this.closeActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css.formOkActionNode,
            "text": this.lp.ok
        }).inject(this.formBottomNode);
        this.okActionNode.addClass("mainColor_bg");

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));


    },
    ok: function (e) {
        this.fireEvent("queryOk");

        var result = this.form.getResult(true, null);
        if( !result ){
            this.app.notice(this.lp.inputLockExpiredTimeAndReason, "error");
            return;
        }
        (this.formMaskNode || this.formMarkNode).destroy();
        this.formAreaNode.destroy();
        this.fireEvent("postOk", [result.lockExpiredTime, result.reason]);

    }
});

MWF.xApplication.Org.PersonExplorer.BanPersonForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "cms_xform",
        "width": "580",
        "height": "290",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : true,
        "hasBottom": true,
        "title": MWF.xApplication.Org.LP.banPerson,
        "draggable": true,
        "closeAction": true,
        "publishTime": ""
    },
    _createTableContent: function () {
        this.formTopTextNode.addClass("mainColor_color");

        this.formAreaNode.setStyle("z-index", 1002);
        this.formMaskNode.setStyle("z-index", 1002);
        this.formTableContainer.setStyles({
            "margin":"20px 40px 0px 40px"
        });
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableValue' style='width:70px;'></td>" +
            "   <td styles='formTableValue'>"+MWF.xApplication.Org.LP.banPersonText.replace("{name}", this.options.name)+"</td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableValue' style='width:70px;' lable='reason'></td>" +
            "   <td styles='formTableValue' item='reason'></td>" +
            "</tr>"+
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: "meeting",
                isEdited: true,
                itemTemplate: {
                    reason: {
                        text: MWF.xApplication.Org.LP.reason,
                        type: "textarea",
                        notEmpty: true,
                        value: this.options.reason || ""
                    }
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);
    },
    _createBottomContent: function () {

        this.closeActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.cancel
        }).inject(this.formBottomNode);

        this.closeActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css.formOkActionNode,
            "text": this.lp.ok
        }).inject(this.formBottomNode);
        this.okActionNode.addClass("mainColor_bg");

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));


    },
    ok: function (e) {
        this.fireEvent("queryOk");

        var result = this.form.getResult(true, null);
        if( !result ){
            this.app.notice(this.lp.inputReason, "error");
            return;
        }
        (this.formMaskNode || this.formMarkNode).destroy();
        this.formAreaNode.destroy();
        this.fireEvent("postOk", result.expiredTime);

    }
});

MWF.xApplication.Org.PersonExplorer.FilterTooltip = new Class({
    Extends: MTooltips,
    _customNode : function(node, contentNode){
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0'>" +
            "<tr>" +
            "   <td styles='formTableValue' style='width:30px;' lable='key'></td>" +
            "   <td styles='formTableValue' item='key'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableValue' lable='status'></td>" +
            "   <td styles='formTableValue' item='status'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableValue'></td>" +
            "   <td styles='formTableValue'><div class='mainColor_bg' item='searchAction'>"+MWF.xApplication.Org.LP.search+"</div></td>" +
            "</tr>"+
            "</table>";
        this.contentNode.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.contentNode, {}, {
                style: "execution",
                isEdited: true,
                itemTemplate: {
                    key: {
                        text: MWF.xApplication.Org.LP.search,
                        attr:{
                            placeholder: MWF.xApplication.Org.LP.searchText
                        }
                    },
                    status: {
                        text: MWF.xApplication.Org.LP.status,
                        type: "select",
                        selectValue: MWF.xApplication.Org.LP.statusOption.map(function (opt){
                            return opt.value;
                        }),
                        selectText: MWF.xApplication.Org.LP.statusOption.map(function (opt){
                            return opt.text;
                        })
                    }
                }
            });
            this.form.load();

            var searchAction = this.contentNode.getElement("[item='searchAction'");
            searchAction.setStyles(this.explorer.css.filterSearchAction);
            searchAction.addEvent("click", function (){
                this.explorer.filterData = this.form.getItemsKeyValue();
                this.explorer.reloadElements();
            }.bind(this));

        }.bind(this), true);
    }
});

