// MWF.xDesktop.requireApp("Organization", "GroupExplorer", null, false);
// MWF.xDesktop.requireApp("Organization", "OrgExplorer", null, false);
MWF.xDesktop.requireApp("Org", "$Explorer", null, false);
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xApplication.Org.PersonExplorer = new Class({
	Extends: MWF.xApplication.Org.$Explorer,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
    _isActionManager: function(){
        return (MWF.AC.isOrganizationManager() || MWF.AC.isPersonManager() || MWF.AC.isUnitManager());
    },
    _listElementNext: function(lastid, count, callback){
        this.actions.listPersonNext(lastid||"(0)", count, function(json){
            if (callback) {
                callback.apply(this, [json]);
            }
        }.bind(this));
    },
    _newElement: function(data, explorer, i){
        return new MWF.xApplication.Org.PersonExplorer.Person(data, explorer, this.isEditor, i);
    },
    _listElementByKey: function(callback, failure, key){
        //this.actions.listPersonByPinyin(function(json){
        this.actions.listPersonByKey(function(json){
            if (callback) {
                callback.apply(this, [json]);
            }
        }.bind(this), failure, key);
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
            }
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
            this.propertyTab = new MWF.widget.Tab(this.propertyContentNode, {"style": "unit"});
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
                "get": function(){ return this.distinguishedName; },
                "set": function(value){ this.distinguishedName = value; }
            }, {
                "get": function(){ return ""; },
                "events": {
                    "init": function(){
                        var contentNode = this.td;
                        if (this.data.woUnitDutyList){
                            this.data.woUnitDutyList.each(function(duty){
                                new MWF.widget.O2Duty(duty, contentNode, {"style": "xform"});
                            }.bind(this));
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
                        debugger;
                        if (_self.data.control.allowEdit){_self.editIdentity(this.data, this.td, this.item);}
                    }
                }
            }]
        });
        this.identityList.load([
            {"style": "width: 12%", "text": this.explorer.app.lp.IdentityName},
            {"style": "width: 12%", "text": this.explorer.app.lp.IdentityInUnit},
            {"style": "width: 44%", "text": this.explorer.app.lp.personUnique},
            {"style": "width: 20%", "text": this.explorer.app.lp.IdentityDuty},
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
                            debugger;
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
            o2.Actions.load("x_organization_assemble_control").RoleAction.listWithPerson(this.data.id, function (json) {
                this.roleDataList = json.data;
                json.data.each( function ( item ) {
                    this.roleList.push(item);
                }.bind(this))
            }.bind(this), null, false);
        }

        // this.data.woPersonAttributeList.each(function(item){
        //     this.roleList.push(item);
        // }.bind(this));
    }
});
MWF.xApplication.Org.PersonExplorer.PersonContent.TitleInfor = new Class({
    Extends: MWF.xApplication.Org.$Explorer.ItemContent.TitleInfor,
    loadAction: function(){
        //this.explorer.app.lp.edit
        this.nameNode.setStyle("margin-right", "80px");
        if (MWF.AC.isOrganizationManager() || MWF.AC.isPersonManager()){
            this.resetPasswordAction = new Element("div", {"styles": this.style.titleInforResetPasswordNode, "text": this.item.explorer.app.lp.resetPassword}).inject(this.nameNode, "before");
            this.resetPasswordAction.addEvent("click", function(e){this.resetPassword(e);}.bind(this));

            this.unlockPersonAction = new Element("div", {"styles": this.style.titleInforUnlockPersonNode, "text": this.item.explorer.app.lp.unlockPerson}).inject(this.nameNode, "before");
            this.unlockPersonAction.addEvent("click", function(e){this.unlockPerson(e);}.bind(this));
        }
        if (this.data.control.allowEdit){
            this.iconNode.setStyle("cursor", "pointer");
            this.iconNode.addEvent("click", function(){this.changePersonIcon();}.bind(this));
        }
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
        }.bind(this));
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
        this.addInfor(text);
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

        var n = this.editContentNode.getElement(".infor_employee");
        if (n) n.set("text", this.data.employee || "");

        var n = this.editContentNode.getElement(".infor_mobile");
        if (n) n.set("text", this.data.mobile || "");

        var n = this.editContentNode.getElement(".infor_unique");
        if (n) n.set("text", this.data.unique || "");

        var n = this.editContentNode.getElement(".infor_gender");
        if (n) n.set("text", this.getGenderType());

        var n = this.editContentNode.getElement(".infor_mail");
        if (n) n.set("text", this.data.mail || "");

        var n = this.editContentNode.getElement(".infor_weixin");
        if (n) n.set("text", this.data.weixin || "");

        var n = this.editContentNode.getElement(".infor_qq");
        if (n) n.set("text", this.data.qq || "");

        var n = this.editContentNode.getElement(".infor_officePhone");
        if (n) n.set("text", this.data.officePhone || "");

        var n = this.editContentNode.getElement(".infor_boardDate");
        if (n) n.set("text", this.data.boardDate || "");

        var n = this.editContentNode.getElement(".infor_birthday");
        if (n) n.set("text", this.data.birthday || "");

        var n = this.editContentNode.getElement(".infor_ipAddress");
        if (n) n.set("text", this.data.ipAddress || "");

        var n = this.editContentNode.getElement(".infor_description");
        if (n) n.set("text", this.data.description || "");

        this.editContentNode.getElements("td.inforTitle").setStyles(this.style.baseInforTitleNode);
        this.editContentNode.getElements("td.inforContent").setStyles(this.style.baseInforContentNode);
        this.editContentNode.getElements("td.inforAction").setStyles(this.style.baseInforActionNode);

        var tdContents = this.editContentNode.getElements("td.inforContent");
        if (this.data.superior) new MWF.widget.O2Person({"name": this.data.superior}, tdContents[5], {"style": "xform"});

        this.loadAction();
    },
    getContentHtml: function(){
        var html = "<table width='100%' cellpadding='3px' cellspacing='5px'>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personName+":</td><td class='inforContent infor_name'>"+(this.data.name || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personEmployee+":</td><td class='inforContent infor_employee'>"+(this.data.employee || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personMobile+":</td><td class='inforContent infor_mobile'>"+(this.data.mobile || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personUnique+":</td><td class='inforContent infor_unique'>"+(this.data.unique || "")+"</td></tr>";
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
        this.employeeInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[1]);
        this.employeeInputNode.set("value", (this.data.employee));

        tdContents[2].setStyles(this.style.baseInforContentNode_edit).empty();
        this.mobileInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[2]);
        this.mobileInputNode.set("value", (this.data.mobile));

        tdContents[3].setStyles(this.style.baseInforContentNode_edit).empty();
        this.uniqueInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[3]);
        this.uniqueInputNode.set("value", (this.data.unique));

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
                        this.data.superior = items[0].data.distinguishedName;
                        this.superiorInputNode.empty();
                        new MWF.widget.O2Person(items[0].data, this.superiorInputNode, {"style": "xform"});
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
        this.ipAddressInputNode = new Element("input", {"styles": this.style.inputNode, "placeHolder": this.explorer.app.lp.ipAddressPlaceHolder, }).inject(tdContents[12]);
        this.ipAddressInputNode.set("value", (this.data.ipAddress));


        tdContents[13].setStyles(this.style.baseInforContentNode_edit).empty();
        this.descriptionInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[13]);
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
        //if (!this.nameInputNode.get("value") || !this.employeeInputNode.get("value") || !this.mobileInputNode.get("value") || !gender){
        if (!this.nameInputNode.get("value") || !this.mobileInputNode.get("value") || !gender){
            this.explorer.app.notice(this.explorer.app.lp.inputPersonInfor, "error", this.explorer.propertyContentNode);
            return false;
        }

        // var array = [];
        // var ipAddress = this.ipAddressInputNode.get("value") || "";
        // var ipV4Format = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
        // var ipV6Format = /^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/;
        // if( ipAddress.trim() ){
        //     ipAddress.split(",").each( function(ip){
        //         if(!ip.match(ipV4Format) && !ip.match(ipV6Format))array.push( ip );
        //     })
        // }
        // if( array.length > 0 ){
        //     this.explorer.app.notice( this.explorer.app.lp.ipAddressIncorrectNotice + array.join(","), "error", this.explorer.propertyContentNode);
        //     return false;
        // }

        //this.data.genderType = gender;
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

        var tdContents = this.editContentNode.getElements("td.inforContent");
        var radios = tdContents[4].getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                data.genderType = radios[i].value;
                break;
            }
        }
        this.explorer.actions.savePerson(data, function(json){
            debugger;
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
        if (this.data.id){
            var tdContents = this.editContentNode.getElements("td.inforContent");
            tdContents[0].setStyles(this.style.baseInforContentNode).set("text", this.data.name || "");
            tdContents[1].setStyles(this.style.baseInforContentNode).set("text", this.data.employee || "");
            tdContents[2].setStyles(this.style.baseInforContentNode).set("text", this.data.mobile || "");
            tdContents[3].setStyles(this.style.baseInforContentNode).set("text", this.data.unique || "");
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

            this.mode = "read";

            this.editNode.setStyle("display", "block");
            this.saveNode.setStyle("display", "none");
            this.cancelNode.setStyle("display", "none");
        }else{
            this.item.destroy();
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