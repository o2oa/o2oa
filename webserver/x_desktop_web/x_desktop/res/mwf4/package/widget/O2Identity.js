MWF.widget = MWF.widget || {};
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.widget.O2Identity = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default",
        "canRemove": false,
        "lazy": false
	},
	initialize: function(data, container, options){
		this.setOptions(options);
		this.loadedInfor = false;

		this.path = MWF.defaultPath+"/widget/$O2Identity/";
		this.cssPath = MWF.defaultPath+"/widget/$O2Identity/"+this.options.style+"/css.wcss";
		this._loadCss();

        this.container = $(container);
        this.data = data;
        this.style = this.css;

        this.action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_control", "x_component_Org");
        // this.explorer = explorer;
        // this.removeAction = removeAction;
        this.load();
	},
    setText: function(){
        this.node.set("text", this.data.name+"("+this.data.unitName+")");
    },
    load: function(){
        if (!this.options.lazy) this.getPersonData();
        this.node = new Element("div", {"styles": this.style.identityNode}).inject(this.container);
        this.setText();

        if (this.options.canRemove){
            this.removeNode = new Element("div", {"styles": this.style.identityRemoveNode}).inject(this.node);
            this.removeNode.addEvent("click", function(e){
                this.fireEvent("remove", [this, e]);
                e.stopPropagation();
            }.bind(this));
        }

        if (!this.options.lazy){
            this.createInforNode(function(){
                this.fireEvent("loadedInfor", [this]);
            }.bind(this));
        }else{
            this.node.addEvents({
                "mouseover": function(){
                    if (!this.loadedInfor){
                        this.getPersonData();
                        this.createInforNode(function(){
                            this.fireEvent("loadedInfor", [this]);
                        }.bind(this));
                    }
                }.bind(this)
            });
        }
        this.setEvent();
        this.node.addEvents({
            "mouseover": function(){
                this.node.setStyles(this.style.identityNode_over);
            }.bind(this),
            "mouseout": function(){
                this.node.setStyles(this.style.identityNode);
            }.bind(this)
        });
    },
    setEvent: function(){},
    getPersonData: function(){
        if (!this.data.dutys){
            var action = MWF.Actions.get("x_organization_assemble_control");
            var id = this.data.distinguishedName || this.data.id || this.data.unique;
            if (id) action.listUnitdutyByIdentity(id, function(json){
                this.data.dutys = json.data;
            }.bind(this), null, false);
        }

        if (!this.data.woPerson){
            // var uri = "/jaxrs/person/{flag}";
            // //uri = uri.replace("{flag}", this.data.person);
            // var uriIdentity = "/jaxrs/identity/{id}";
            this.action.actions = {
                "getPerson": {"uri": "/jaxrs/person/{flag}"},
                "getIdentity": {"uri": "/jaxrs/identity/{id}"}
            };
            var woPerson;
            if (this.data.person){
                this.action.invoke({"name": "getPerson", "async": false, "parameter": {"flag": this.data.person}, "success": function(json){
                    this.data.woPerson = woPerson;
                    woPerson = json.data;
                }.bind(this)});
            }else{
                this.action.invoke({"name": "getIdentity", "async": false, "parameter": {"id": this.data.id || this.data.name}, "success": function(json){
                    this.data = json.data;
                    woPerson = json.data.woPerson;
                }.bind(this)});
            }

            return woPerson;
        }else{
            return this.data.woPerson;
        }


        listDutyNameWithIdentity
    },
    createInforNode: function(callback){
        debugger;
        var person = this.getPersonData();
        if (person){
            this.inforNode = new Element("div", {
                "styles": this.style.identityInforNode
            });
            var nameNode = new Element("div", {
                "styles": this.style.identityInforNameNode
            }).inject(this.inforNode);

            var uri = "/jaxrs/person/{flag}/icon";
            uri = uri.replace("{flag}", person.id);
            this.action.getAddress();
            uri = this.action.address+uri;

            img = "<img width='50' height='50' border='0' src='"+uri+"' style='border-radius:25px'/>";

            var picNode = new Element("div", {
                "styles": this.style.identityInforPicNode,
                "html": img
            }).inject(nameNode);
            var rightNode = new Element("div", {
                "styles": this.style.identityInforRightTextNode
            }).inject(nameNode);
            var nameTextNode = new Element("div", {
                "styles": this.style.identityInforNameTextNode,
                "text": person.name
            }).inject(rightNode);
            var employeeTextNode = new Element("div", {
                "styles": this.style.identityInforEmployeeTextNode,
                "text": person.employee
            }).inject(rightNode);

            // var phoneNode = new Element("div", {
            //     "styles": this.style.identityInforPhoneNode,
            //     "html": "<div style='width:30px; float:left'>"+MWF.LP.desktop.person.personMobile+": </div><div style='width:90px; float:left; margin-left:10px'>"+(person.mobile || "")+"</div>"
            // }).inject(this.inforNode);
            // var mailNode = new Element("div", {
            //     "styles": this.style.identityInforPhoneNode,
            //     "html": "<div style='width:30px; float:left'>"+MWF.LP.desktop.person.personMail+": </div><div style='width:90px; float:left; margin-left:10px'>"+(person.mail || "")+"</div>"
            // }).inject(this.inforNode);
debugger;
            var dutys = [];
            if (this.data.dutys && this.data.dutys.length){
                this.data.dutys.each(function(d){
                    var n = d.name+"("+d.woUnit.levelName+")";
                    dutys.push(n);
                });
            }
            var dutyNode = new Element("div", {
                "styles": this.style.identityInforPhoneNode,
                "html": "<div style='width:30px; float:left'>"+MWF.LP.desktop.person.duty+": </div><div style='width:160px; float:left; margin-left:10px'>"+(dutys.join(","))+"</div>"
            }).inject(this.inforNode);

            this.loadedInfor = true;
            this.tooltip = new mBox.Tooltip({
                content: this.inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: this.node,
                transition: 'flyin'
            });
        }
        if (callback) callback();
    },
    destroy: function(){
        if (this.tooltip) this.tooltip.destroy();
        this.node.destroy();
        MWF.release(this);
    }
});
// MWF.widget.Person = new Class({
//     Implements: [Options, Events],
//     Extends: MWF.widget.Identity,
//     getPerson: function(callback){
//         if (this.data.name && this.data.id){
//             if (callback) callback({"data": this.data});
//         }else{
//             var key = this.data.name;
//             this.explorer.actions["getPerson"](function(json){
//                 if (callback) callback(json);
//             }, null, key);
//         }
//     }
// });

MWF.widget.O2Person = new Class({
    Extends: MWF.widget.O2Identity,
    getPersonData: function(){
        if (!this.data.distinguishedName){
            this.action.actions = {"getPerson": {"uri": "/jaxrs/person/{id}"}};
            this.action.invoke({"name": "getPerson", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
            }.bind(this)});
        }
        return this.data;
    },
    setText: function(){
        this.node.set("text", this.data.name);
    }
});
MWF.widget.O2Unit = new Class({
    Extends: MWF.widget.O2Identity,
    getPersonData: function(){
        if (!this.data.distinguishedName){
            this.action.actions = {"getUnit": {"uri": "/jaxrs/unit/{id}"}};
            this.action.invoke({"name": "getUnit", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
            }.bind(this)});
        }
    },
    createInforNode: function(){
        this.inforNode = new Element("div", {
            "styles": this.style.identityInforNode
        });
        var nameNode = new Element("div", {
            "styles": this.style.identityInforNameNode,
            "text": this.data.levelName
        }).inject(this.inforNode);
        this.tooltip = new mBox.Tooltip({
            content: this.inforNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.node,
            transition: 'flyin'
        });
    },
    setText: function(){
        this.node.set("text", this.data.name);
    }
});
MWF.widget.O2Duty = new Class({
    Extends: MWF.widget.O2Identity,
    getPersonData: function(){
        if (!this.data.woUnit){
            this.action.actions = {"getUnitduty": {"uri": "/jaxrs/unitduty/{id}"}};
            this.action.invoke({"name": "getUnitduty", "async": false, "parameter": {"id": (this.data.dutyId || this.data.name)}, "success": function(json){
                this.data = json.data;
            }.bind(this)});
        }
    },
    createInforNode: function(){
        this.inforNode = new Element("div", {
            "styles": this.style.identityInforNode
        });
        var nameNode = new Element("div", {
            "styles": this.style.identityInforNameNode,
            "text": this.data.woUnit.levelName
        }).inject(this.inforNode);
        this.tooltip = new mBox.Tooltip({
            content: this.inforNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.node,
            transition: 'flyin'
        });
    },
    setText: function(){
        this.node.set("text", this.data.name);
    }
});
MWF.widget.O2Group = new Class({
    Extends: MWF.widget.O2Unit,
    getPersonData: function(){
        if (!this.data.distinguishedName){
            this.action.actions = {"getGroup": {"uri": "/jaxrs/group/{id}"}};
            this.action.invoke({"name": "getGroup", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
            }.bind(this)});
        }
    },
    setText: function(){
        this.node.set("text", this.data.name);
    },
    createInforNode: function(){
        return false;
    }
});
MWF.widget.O2Application = new Class({
    Extends: MWF.widget.O2Group,
    getPersonData: function(){
        if (!this.data.name){
            this.action = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "");
            this.action.actions = {"getApplication": {"uri": "/jaxrs/application/{id}"}};
            this.action.invoke({"name": "getApplication", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
            }.bind(this)});
        }
    }
});
MWF.widget.O2CMSApplication = new Class({
    Extends: MWF.widget.O2Group,
    getPersonData: function(){
        if (!this.data.name){
            MWF.Actions.get("x_cms_assemble_control").getApplication((this.data.id || this.data.name), function(json){
                this.data = json.data;
            }.bind(this), null, false);
            // this.action = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "");
            // this.action.actions = {"getApplication": {"uri": "/jaxrs/application/{id}"}};
            // this.action.invoke({"name": "getApplication", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
            //     this.data = json.data;
            // }.bind(this)});
        }
    }
});



MWF.widget.O2Process = new Class({
    Extends: MWF.widget.O2Group,
    getPersonData: function(){
        if (!this.data.name){
            this.action = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "");
            this.action.actions = {"getProces": {"uri": "/jaxrs/process/{id}/complex"}};
            this.action.invoke({"name": "getProces", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
            }.bind(this)});
        }
    },
    createInforNode: function(){
        this.inforNode = new Element("div", {
            "styles": this.style.identityInforNode
        });
        var nameNode = new Element("div", {
            "styles": this.style.identityInforNameNode,
            "text": this.data.applicationName || this.data.appName
        }).inject(this.inforNode);
        this.tooltip = new mBox.Tooltip({
            content: this.inforNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.node,
            transition: 'flyin'
        });
    }
});
MWF.widget.O2CMSCategory = new Class({
    Extends: MWF.widget.O2Group,
    getPersonData: function(){
        if (!this.data.name){
            MWF.Actions.get("x_cms_assemble_control").getCategory((this.data.id || this.data.name), function(json){
                this.data = json.data;
            }.bind(this), null, false);
        }
    },
    createInforNode: function(){
        this.inforNode = new Element("div", {
            "styles": this.style.identityInforNode
        });
        var nameNode = new Element("div", {
            "styles": this.style.identityInforNameNode,
            "text": this.data.applicationName || this.data.appName
        }).inject(this.inforNode);
        this.tooltip = new mBox.Tooltip({
            content: this.inforNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.node,
            transition: 'flyin'
        });
    }
});


MWF.widget.O2View = new Class({
    Extends: MWF.widget.O2Group,
    getPersonData: function(){
        return this.data;
    },
    createInforNode: function(){
        this.inforNode = new Element("div", {
            "styles": this.style.identityInforNode
        });
        var nameNode = new Element("div", {
            "styles": this.style.identityInforNameNode,
            "text": this.data.applicationName || this.data.appName
        }).inject(this.inforNode);
        this.tooltip = new mBox.Tooltip({
            content: this.inforNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.node,
            transition: 'flyin'
        });
    }
});
MWF.widget.O2CMSView = new Class({
    Extends: MWF.widget.O2View
});
MWF.widget.O2QueryView = new Class({
    Extends: MWF.widget.O2View
});
MWF.widget.O2QueryStat = new Class({
    Extends: MWF.widget.O2View
});
MWF.widget.O2FormField = new Class({
    Extends: MWF.widget.O2Group,
    getPersonData: function(){
        return this.data;
    }
});
MWF.widget.O2Role = new Class({
    Extends: MWF.widget.O2Group,
    getPersonData: function(){
        if (!this.data.distinguishedName){
            this.action.actions = {"getRole": {"uri": "/jaxrs/role/{id}"}};
            this.action.invoke({"name": "getRole", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
            }.bind(this)});
        }
    }
});
MWF.widget.O2Other = new Class({
    Extends: MWF.widget.O2Group,
    getPersonData: function(){
        return this.data;
    }
});