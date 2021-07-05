o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.require("o2.xDesktop.Common", null, false);
o2.require("o2.xDesktop.Actions.RestActions", null, false);
o2.widget.O2Identity = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"style": "default",
        "canRemove": false,
        "lazy": false,
        "disableInfor" : false
	},
	initialize: function(data, container, options){

		this.setOptions(options);
		this.loadedInfor = false;

		this.path = o2.session.path+"/widget/$O2Identity/";
		this.cssPath = o2.session.path+"/widget/$O2Identity/"+this.options.style+"/css.wcss";
		this._loadCss();

        this.container = $(container);
        this.data = data;
        this.style = this.css;

        this.action = new o2.xDesktop.Actions.RestActions("", "x_organization_assemble_control", "x_component_Org");
        // this.explorer = explorer;
        // this.removeAction = removeAction;
        this.load();

        //o2.widget.O2Identity.iditems.push(this);
	},
    setText: function(){
	    var disply;
	    if( this.data.displayName ){
            disply = this.data.displayName;
        }else{
	        var name = this.data.name || o2.name.cn(this.data.distinguishedName);
	        var unit;
            if(this.data.unitName){
                unit = this.data.unitName;
            }else if( this.data.unitLevelName ){
                var list = this.data.unitLevelName.split("/");
                unit = list[ list.length - 1 ];
            }
            disply = name + (unit ? "("+unit+")" : "")
        }
        this.node.set("text", this.data.displayName || disply );
    },
    load: function(){
        var style = ( layout.mobile && this.style.identityNode_mobile ) ?
            this.style.identityNode_mobile : this.style.identityNode;

        if (!this.options.lazy && !this.options.disableInfor) this.getPersonData();
        this.node = new Element("div", {"styles": style }).inject(this.container);
        this.setText();

        if (this.options.canRemove){
            this.removeNode = new Element("div", {"styles": this.style.identityRemoveNode}).inject(this.node);
            this.removeNode.addEvent("click", function(e){
                this.fireEvent("remove", [this, e]);
                e.stopPropagation();
            }.bind(this));
        }

        if( !this.options.disableInfor && !layout.mobile){
            if (!this.options.lazy ){
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
        }
        this.setEvent();
        if( !layout.mobile ){
            this.node.addEvents({
                "mouseover": function(){

                    // var style_over = ( layout.mobile && this.style.identityNode_over_mobile ) ?
                    //     this.style.identityNode_over_mobile : this.style.identityNode_over;

                    this.node.setStyles( this.style.identityNode_over ); //style_over
                }.bind(this),
                "mouseout": function(){

                    // var style = ( layout.mobile && this.style.identityNode_mobile ) ?
                    //     this.style.identityNode_mobile : this.style.identityNode;

                    this.node.setStyles( this.style.identityNode ); //style
                }.bind(this)
            });
        }
    },
    setEvent: function(){
	    if( this.open ){
            this.node.addEvents({
                "click": function(ev){
                    this.open(ev);
                    ev.stopPropagation();
                }.bind(this)
            });
        }
    },
    getPersonData: function(){
        if (!this.data.dutys){
            var action = o2.Actions.get("x_organization_assemble_control");
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


        //listDutyNameWithIdentity
    },
    createInforNode: function(callback){
        var person = this.getPersonData();
        if (person){
            this.inforNode = new Element("div", {
                "styles": this.style.identityInforNode
            });
            var nameNode = new Element("div", {
                "styles": this.style.identityInforNameNode
            }).inject(this.inforNode);

            var uri = "/jaxrs/person/{flag}/icon";
            uri = uri.replace("{flag}", person.id || person.unique || person.distinguishedName );
            this.action.getAddress();
            uri = this.action.address+uri;

            uri = o2.filterUrl(uri);

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
                "text": person.employee || ""
            }).inject(rightNode);

            // var phoneNode = new Element("div", {
            //     "styles": this.style.identityInforPhoneNode,
            //     "html": "<div style='width:30px; float:left'>"+o2.LP.desktop.person.personMobile+": </div><div style='width:90px; float:left; margin-left:10px'>"+(person.mobile || "")+"</div>"
            // }).inject(this.inforNode);
            // var mailNode = new Element("div", {
            //     "styles": this.style.identityInforPhoneNode,
            //     "html": "<div style='width:30px; float:left'>"+o2.LP.desktop.person.personMail+": </div><div style='width:90px; float:left; margin-left:10px'>"+(person.mail || "")+"</div>"
            // }).inject(this.inforNode);

            var dutys = [];
            if (this.data.dutys && this.data.dutys.length){
                this.data.dutys.each(function(d){
                    var n = d.name+"("+d.woUnit.levelName+")";
                    dutys.push(n);
                });
            }
            var dutyNode = new Element("div", {
                "styles": this.style.identityInforPhoneNode,
                "html": "<div style='width:30px; float:left'>"+o2.LP.desktop.person.duty+": </div><div style='width:160px; float:left; margin-left:10px'>"+(dutys.join(","))+"</div>"
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
        o2.release(this);
    }
});
// o2.widget.Person = new Class({
//     Implements: [Options, Events],
//     Extends: o2.widget.Identity,
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

o2.widget.O2Person = new Class({
    Extends: o2.widget.O2Identity,
    getPersonData: function(){
        if (!this.data.distinguishedName){
            this.action.actions = {"getPerson": {"uri": "/jaxrs/person/{id}"}};
            this.action.invoke({"name": "getPerson", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
                var dutyList = [];
                if( this.data.woIdentityList && this.data.woIdentityList.length ){
                    this.data.woIdentityList.each(function (id) {
                        if(id.woUnitDutyList && id.woUnitDutyList.length)dutyList = dutyList.concat(id.woUnitDutyList);
                    })
                }
                this.data.dutys = dutyList;
            }.bind(this)});
        }
        return this.data;
    },
    setText: function(){
        this.node.set("text", this.data.displayName || this.data.name);
    }
});
o2.widget.O2Unit = new Class({
    Extends: o2.widget.O2Identity,
    getPersonData: function(){
        if (!this.data.distinguishedName || !this.data.levelName){
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
        this.node.set("text", this.data.displayName || this.data.name);
    }
});
o2.widget.O2Duty = new Class({
    Extends: o2.widget.O2Identity,
    getPersonData: function(){
        return this.data;
        // if (!this.data.woUnit){
        //     this.action.actions = {"getUnitduty": {"uri": "/jaxrs/unitduty/{id}"}};
        //     this.action.invoke({"name": "getUnitduty", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
        //         this.data = json.data;
        //     }.bind(this)});
        // }
    },
    createInforNode: function(){
        return false;
        // this.inforNode = new Element("div", {
        //     "styles": this.style.identityInforNode
        // });
        // var nameNode = new Element("div", {
        //     "styles": this.style.identityInforNameNode,
        //     "text": this.data.woUnit.levelName
        // }).inject(this.inforNode);
        // this.tooltip = new mBox.Tooltip({
        //     content: this.inforNode,
        //     setStyles: {content: {padding: 15, lineHeight: 20}},
        //     attach: this.node,
        //     transition: 'flyin'
        // });
    },
    setText: function(){
        this.node.set("text", this.data.displayName || this.data.name);
    }
});
o2.widget.O2Group = new Class({
    Extends: o2.widget.O2Unit,
    getPersonData: function(){
        if (!this.data.distinguishedName){
            this.action.actions = {"getGroup": {"uri": "/jaxrs/group/{id}"}};
            this.action.invoke({"name": "getGroup", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
            }.bind(this)});
        }
    },
    setText: function(){
        this.node.set("text", this.data.displayName || this.data.name);
    },
    createInforNode: function(){
        return false;
    }
});
o2.widget.O2Application = new Class({
    Extends: o2.widget.O2Group,
    getPersonData: function(){
        if (!this.data.name){
            this.action = new o2.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "");
            this.action.actions = {"getApplication": {"uri": "/jaxrs/application/{id}"}};
            this.action.invoke({"name": "getApplication", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
            }.bind(this)});
        }
    }
});
o2.widget.O2CMSApplication = new Class({
    Extends: o2.widget.O2Group,
    getPersonData: function(){
        if (!this.data.name){
            o2.Actions.get("x_cms_assemble_control").getApplication((this.data.id || this.data.name), function(json){
                this.data = json.data;
            }.bind(this), null, false);
            // this.action = new o2.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "");
            // this.action.actions = {"getApplication": {"uri": "/jaxrs/application/{id}"}};
            // this.action.invoke({"name": "getApplication", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
            //     this.data = json.data;
            // }.bind(this)});
        }
    }
});



o2.widget.O2Process = new Class({
    Extends: o2.widget.O2Group,
    getPersonData: function(){
        if (!this.data.name){
            this.action = new o2.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "");
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
            "text": this.data.name || this.data.applicationName || this.data.appName
        }).inject(this.inforNode);
        this.tooltip = new mBox.Tooltip({
            content: this.inforNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.node,
            transition: 'flyin'
        });
    },
    open : function (e) {
        debugger;
        if( this.data.id && this.data.application ){
            var appId = "process.ProcessManager" + this.data.application;
            if (layout.desktop.apps[appId]){
                layout.desktop.apps[appId].setCurrent();
            }else {
                var options = { "application": {
                    "id": this.data.application,
                     "name": this.data.applicationName || ""
                }};
                layout.desktop.openApplication(e, "process.ProcessManager", options);
            }
        }
    }
});
o2.widget.O2CMSCategory = new Class({
    Extends: o2.widget.O2Group,
    getPersonData: function(){
        if (!this.data.name){
            o2.Actions.get("x_cms_assemble_control").getCategory((this.data.id || this.data.name), function(json){
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
    },
    open : function (e) {
        debugger;
        if( this.data.id && this.data.appId ){
            // var appId = "cms.ColumnManager" + this.data.id;
            // if (layout.desktop.apps[appId]){
            //     layout.desktop.apps[appId].setCurrent();
            // }else {
                var options = {
                    "navi":"categoryConfig",
                    "column":{
                        "id" : this.data.appId,
                        "appName": this.data.appName || ""
                    },
                    "currentCategoryId":this.data.id
                };
                layout.desktop.openApplication(e, "cms.ColumnManager", options);
            // }
        }
    }
});


o2.widget.O2View = new Class({
    Extends: o2.widget.O2Group,
    getPersonData: function(){
        if (!this.data.query && this.data.id){
            var data = null;
            o2.Actions.get("x_query_assemble_surface").getStatById(this.data.id, function(json){
                data = json.data
            }, null, false);
            this.data = data;
            return data;
        }else{
            return this.data;
        }
    },
    createInforNode: function(){
        this.inforNode = new Element("div", {
            "styles": this.style.identityInforNode
        });
        var nameNode = new Element("div", {
            "styles": this.style.identityInforNameNode,
            "text": this.data.applicationName || this.data.appName || this.data.name
        }).inject(this.inforNode);
        this.tooltip = new mBox.Tooltip({
            content: this.inforNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.node,
            transition: 'flyin'
        });
    }
});
o2.widget.O2CMSView = new Class({
    Extends: o2.widget.O2View
});
o2.widget.O2QueryView = new Class({
    Extends: o2.widget.O2View,
    getPersonData: function(){
        if (!this.data.query && this.data.id){
            var data = null;
            o2.Actions.get("x_query_assemble_surface").getViewById(this.data.id, function(json){
                data = json.data
            }, null, false);
            this.data = data;
            return data;
        }else{
            return this.data;
        }
    },
    open : function (e) {
        if( this.data.id && this.data.query ){
            var appId = "query.ViewDesigner" + this.data.id;
            if (layout.desktop.apps[appId]){
                layout.desktop.apps[appId].setCurrent();
            }else {
                var options = {"id": this.data.id, "application": this.data.query, "appId": appId};
                layout.desktop.openApplication(e, "query.ViewDesigner", options);
            }
        }
    }
});

o2.widget.O2QueryStatement = new Class({
    Extends: o2.widget.O2View,
    getPersonData: function(){
        if (!this.data.query && this.data.id){
            var data = null;
            o2.Actions.load("x_query_assemble_designer").StatementAction.get(this.data.id, function(json){
                data = json.data
            }, null, false);
            this.data = data;
            return data;
        }else{
            return this.data;
        }
    },
    open : function (e) {
        if( this.data.id && this.data.query ){
            var appId = "query.StatementDesigner" + this.data.id;
            if (layout.desktop.apps[appId]){
                layout.desktop.apps[appId].setCurrent();
            }else {
                var options = {"id": this.data.id, "application": this.data.query, "appId": appId};
                layout.desktop.openApplication(e, "query.StatementDesigner", options);
            }
        }
    }
});

o2.widget.O2QueryStat = new Class({
    Extends: o2.widget.O2View,
    getPersonData: function(){
        if (!this.data.query && this.data.id){
            var data = null;
            o2.Actions.get("x_query_assemble_surface").getStatById(this.data.id, function(json){
                data = json.data
            }, null, false);
            this.data = data;
            return data;
        }else{
            return this.data;
        }
    },
    open : function (e) {
        if( this.data.id && this.data.query){
            var appId = "query.StatDesigner" + this.data.id;
            if (layout.desktop.apps[appId]){
                layout.desktop.apps[appId].setCurrent();
            }else {
                var options = {"id": this.data.id,"application": this.data.query, "appId": appId};
                layout.desktop.openApplication(e, "query.StatDesigner", options);
            }
        }
    }
});

o2.widget.O2QueryTable = new Class({
    Extends: o2.widget.O2View,
    getPersonData: function(){
        if (!this.data.query && this.data.id){
            var data = null;
            o2.Actions.get("x_query_assemble_surface").getTableById(this.data.id, function(json){
                data = json.data
            }, null, false);
            this.data = data;
            return data;
        }else{
            return this.data;
        }
    },
    open : function (e) {
        if( this.data.id && this.data.query){
            var appId = "query.TableDesigner" + this.data.id;
            if (layout.desktop.apps[appId]){
                layout.desktop.apps[appId].setCurrent();
            }else {
                var options = {"id": this.data.id,"application": this.data.query, "appId": appId};
                layout.desktop.openApplication(e, "query.TableDesigner", options);
            }
        }
    }
});

o2.widget.O2QueryImportModel = new Class({
    Extends: o2.widget.O2View,
    getPersonData: function(){
        if (!this.data.query && this.data.id){
            var data = null;
            o2.Actions.get("x_query_assemble_surface").getImportModelById(this.data.id, function(json){
                data = json.data
            }, null, false);
            this.data = data;
            return data;
        }else{
            return this.data;
        }
    },
    open : function (e) {
        if( this.data.id && this.data.query){
            var appId = "query.ImporterDesigner" + this.data.id;
            if (layout.desktop.apps[appId]){
                layout.desktop.apps[appId].setCurrent();
            }else {
                var options = {"id": this.data.id,"application": this.data.query, "appId": appId};
                layout.desktop.openApplication(e, "query.ImporterDesigner", options);
            }
        }
    }
});

o2.widget.O2FormField = new Class({
    Extends: o2.widget.O2Group,
    getPersonData: function(){
        return this.data;
    }
});
o2.widget.O2Role = new Class({
    Extends: o2.widget.O2Group,
    getPersonData: function(){
        if (!this.data.distinguishedName){
            this.action.actions = {"getRole": {"uri": "/jaxrs/role/{id}"}};
            this.action.invoke({"name": "getRole", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                this.data = json.data;
            }.bind(this)});
        }
    }
});
o2.widget.O2File = new Class({
    Extends: o2.widget.O2Group,
    createInforNode: function(){
        this.inforNode = new Element("div", {
            "styles": this.style.identityInforNode
        });
        var extName = this.data.fileName.substring(this.data.fileName.lastIndexOf(".")+1, this.data.fileName.length).toLowerCase();
        if (["png","jpg","bmp","gif","jpeg","jpe"].indexOf(extName)!==-1){
            var url = (this.data.portal) ? MWF.xDesktop.getPortalFileUr(this.data.id, this.data.portal) : MWF.xDesktop.getProcessFileUr(this.data.id, this.data.application);
            var img = new Element("img", {"src": url, "styles": {"max-width": "280px", "max-height": "140px"}}).inject(this.inforNode);
        }else{
            var nameNode = new Element("div", {
                "styles": this.style.identityInforNameNode,
                "text": this.data.applicationName || this.data.appName || this.data.name
            }).inject(this.inforNode);
        }

        this.tooltip = new mBox.Tooltip({
            content: this.inforNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.node,
            transition: 'flyin'
        });
    },

    getPersonData: function(){
        return this.data;
    }
});

o2.widget.O2Script = new Class({
    Extends: o2.widget.O2Group,
    getPersonData: function(){
        return this.data;
    },
    createInforNode: function(){
        if( !this.data.appType )return false;

        this.inforNode = new Element("div", {
            "styles": this.style.identityInforNode
        });
        var nameNode = new Element("div", {
            "text": o2.LP[this.data.appType+"Name"]
        }).inject(this.inforNode);
        var nameTextNode = new Element("div", {
            "text": this.data.applicationName || this.data.appName
        }).inject(this.inforNode);
        this.tooltip = new mBox.Tooltip({
            content: this.inforNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.node,
            transition: 'flyin'
        });
    },
    open : function (e) {
        if( this.data.id && this.data.appId &&  this.data.appType){
            var appName;
            if( this.data.appType === "cms" ){
                appName = "cms.ScriptDesigner";
            }else if( this.data.appType === "portal" ){
                appName = "portal.ScriptDesigner";
            }else if( this.data.appType === "process" ) {
                appName = "process.ScriptDesigner";
            }
            var appId = appName + this.data.id;
            if (layout.desktop.apps[appId]){
                layout.desktop.apps[appId].setCurrent();
            }else {
                var options = {"id": this.data.id, "appId": appId, "application":this.data.appId};
                layout.desktop.openApplication(e, appName, options);
            }
        }
    }
});

o2.widget.O2FormStyle = new Class({
    Extends: o2.widget.O2Group,
    getPersonData: function(){
        return this.data;
    },
    open : function (e) {
        if( typeOf(this.data)==="object" && this.data.id && this.data.appId && this.data.type === "script"){
            var appName;
            if( this.data.appType === "cms" ){
                appName = "cms.ScriptDesigner";
            }else{
                appName = "process.ScriptDesigner";
            }
            var appId = appName + this.data.id;
            if (layout.desktop.apps[appId]){
                layout.desktop.apps[appId].setCurrent();
            }else {
                var options = {"id": this.data.id, "appId": appId, "application":this.data.appId};
                layout.desktop.openApplication(e, appName, options);
            }
        }
    }
});

o2.widget.O2Dictionary = new Class({
    Extends: o2.widget.O2Group,
    getPersonData: function(){
        return this.data;
    },
    createInforNode: function(){
        if( !this.data.appType )return false;

        this.inforNode = new Element("div", {
            "styles": this.style.identityInforNode
        });
        var nameNode = new Element("div", {
            "text": o2.LP[this.data.appType+"Name"]
        }).inject(this.inforNode);
        var nameTextNode = new Element("div", {
            "text": this.data.applicationName || this.data.appName
        }).inject(this.inforNode);
        this.tooltip = new mBox.Tooltip({
            content: this.inforNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.node,
            transition: 'flyin'
        });
    },
    open : function (e) {
        if( this.data.id && this.data.appId && this.data.appType){
            var appName;
            if( this.data.appType === "cms" ){
                appName = "cms.DictionaryDesigner";
            }else if( this.data.appType === "process" ) {
                appName = "process.DictionaryDesigner";
            }
            var appId = appName + this.data.id;
            if (layout.desktop.apps[appId]){
                layout.desktop.apps[appId].setCurrent();
            }else {
                var options = {"id": this.data.id, "appId": appId, "application":this.data.appId};
                layout.desktop.openApplication(e, appName, options);
            }
        }
    }
});


o2.widget.O2Other = new Class({
    Extends: o2.widget.O2Group,
    getPersonData: function(){
        return this.data;
    }
});

/**
 * @return {null}
 */
o2.widget.O2Org = function(value, container, options){
    var v = (o2.typeOf(value)==="string") ? {"name": value} : value.distinguishedName;
    var t = v.distinguishedName || v.name || "";
    if (t) {
        var flag = t.substr(t.length - 1, 1);
        switch (flag.toLowerCase()) {
            case "i":
                return new o2.widget.O2Identity(v, container, options);
            case "p":
                return new o2.widget.O2Person(v, container, options);
            case "u":
                return new o2.widget.O2Unit(v, container, options);
            case "g":
                return new o2.widget.O2Group(v, container, options);
            case "r":
                return new o2.widget.O2Role(v, container, options);
            case "d":
                return new o2.widget.O2Duty(v, container, options);
            default:
                return new o2.widget.O2Other(v, container, options);
        }
    }
    return null;
};

// o2.widget.O2Identity.iditems = o2.widget.O2Identity.iditems || [];
// o2.widget.O2Identity.intervalId = window.setInterval(function(){
//     if (o2.widget.O2Identity.iditems && o2.widget.O2Identity.iditems.length){
//         o2.widget.O2Identity.iditems.each(function(item){
//             if (item.tooltip){
//                 debugger;
//                 if (item.tooltip.options.attach){
//
//                 }
//             }
//         });
//     }
// }, 10000);
