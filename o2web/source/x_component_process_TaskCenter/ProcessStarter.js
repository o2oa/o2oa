MWF.require("MWF.widget.Mask", null, false);
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.TaskCenter = MWF.xApplication.process.TaskCenter || {};
MWF.xDesktop.requireApp("process.TaskCenter", "lp."+o2.language, null, false);
MWF.xApplication.process.TaskCenter.ProcessStarter = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
	options: {
		"style": "default",
        "workData" : null,
        "identity": null,
        "latest": false,
        "skipDraftCheck": null
	},
    initialize: function(data, app, options){
        this.setOptions(options);
        this.path = "../x_component_process_TaskCenter/$ProcessStarter/";
        this.cssPath = "../x_component_process_TaskCenter/$ProcessStarter/"+this.options.style+"/css.wcss";
        this._loadCss();

        MWF.xDesktop.requireApp("process.TaskCenter", "$ProcessStarter."+MWF.language, null, false);
        this.lp = MWF.xApplication.process.TaskCenter.ProcessStarter.lp;

        this.data = data;
        this.app = app;
    },
    load: function(){
        this.getOrgAction(function(){
            if (this.app.desktop.session.user.distinguishedName){
                o2.Actions.load("x_processplatform_assemble_surface").ProcessAction.listAvailableIdentityWithProcess(this.data.id, function(json){
                    this.identitys = json.data || [];
                    if (this.identitys.length){

                        if (this.options.identity){
                            var identityList = typeOf( this.options.identity ) === "array" ? this.options.identity : [this.options.identity];
                            this.identitys = this.identitys.filter(function(id){
                                for( var i=0; i<identityList.length; i++ ){
                                    var identity = identityList[i] || "";
                                    var dn = (typeOf(identity)==="string") ? identity : identity.distinguishedName;
                                    id.index = i;
                                    if( id.distinguishedName===dn )return true;
                                }
                                return false;
                            }.bind(this));

                            this.identitys.sort(function(a, b){
                                return a.index - b.index
                            });
                        }

                        if (this.identitys.length){
                            if (this.identitys.length==1){
                                var data = {
                                    "title": this.data.name+"-"+this.lp.unnamed,
                                    "identity": this.identitys[0].distinguishedName,
                                    "latest": this.options.latest,
                                    "skipDraftCheck": this.options.skipDraftCheck
                                };
                                if( this.options.workData ){
                                    data.data = this.options.workData;
                                    if (data.data.title || data.data.subject) data.title = data.data.title || data.data.subject;
                                }

                                this.mask = new MWF.widget.Mask({"style": "desktop"});
                                this.mask.loadNode(this.app.content);

                                this.fireEvent("beforeStarted", [data]);
                                this.getWorkAction(function(){
                                    this.workAction.startWork(function(json){
                                        debugger;
                                        this.mask.hide();
                                        //this.markNode.destroy();
                                        //this.areaNode.destroy();

                                        this.fireEvent("started", [json.data, data.title, this.data.name]);

                                        if (this.app.refreshAll) this.app.refreshAll();
                                        //this.app.notice(this.lp.processStarted, "success");
                                        //    this.app.processConfig();
                                    }.bind(this), function(xhr, text, error){
                                        if (xhr.status!=0){
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
                                        }
                                        if(this.mask)this.mask.hide();
                                    }.bind(this), this.data.id, data);
                                }.bind(this));

                            }else{
                                this.createMarkNode();
                                this.createAreaNode();
                                this.createStartNode();

                                this.areaNode.inject(this.markNode, "after");
                                this.areaNode.fade("in");
                                //$("form_startSubject").focus();

                                this.setStartNodeSize();
                                this.setStartNodeSizeFun = this.setStartNodeSize.bind(this);
                                this.app.addEvent("resize", this.setStartNodeSizeFun);

                                this.fireEvent("selectId");
                            }
                        }else{
                            var dns = [];
                            var iList = typeOf( this.options.identity ) === "array" ? this.options.identity : [this.options.identity];
                            for( var i=0; i<iList.length; i++ ){
                                var identity = iList[i] || "";
                                var dn = (typeOf(identity)==="string") ? identity : identity.distinguishedName;
                                if(dn)dns.push(dn);
                            }

                            var t = this.lp.identityNotInRange.replace("{name}", dns.join("、"));
                            this.app.notice(t, "error");
                        }
                    }else{
                        var t = this.lp.noIdentitys.replace("{name}", this.app.desktop.session.user.name);
                        this.app.notice(t, "error");
                    }
                }.bind(this))
            }
        }.bind(this));
    },

    createMarkNode: function(){
        var app = (this.app.embeded && this.app.eventTarget) ? this.app.eventTarget : this.app;
        this.markNode = new Element("div#mark", {
            "styles": this.css.markNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(app.content);
    },
    createAreaNode: function(){
        this.areaNode = new Element("div#area", {
            "styles": this.css.areaNode
        });
    },
    createStartNode: function(){
        this.createNode = new Element("div", {
            "styles": this.css.createNode
        }).inject(this.areaNode);
        this.createNewNode = new Element("div", {
            "styles": this.css.createNewNode
        }).inject(this.createNode);

        this.createCloseNode = new Element("div", {
            "styles": this.css.createCloseNode
        }).inject(this.createNode);
        this.createCloseNode.addEvent("click", function(e){
            this.cancelStartProcess(e);
        }.bind(this));

        this.formNode = new Element("div", {
            "styles": this.css.formNode
        }).inject(this.createNode);

        var html = "<table width=\"100%\" height=\"90%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">" +
            "<tr><td style=\"height: 50px; line-height: 50px; text-align: left; font-size: 16px; color:#333333; \">" +
            this.lp.start+" - "+this.data.name+"</td></tr>" +
            "<tr><td style=\"height: 60px; color: #0044cc; line-height: 80px; text-align: left; font-size: 16px; color:#333333; display: block; overflow: hidden\"><div style='padding-left:30px; line-height:80px'>" +
            this.lp.selectStartIdentity+"</div></td></tr>" +
            "<tr><td id=\"form_startIdentity\"></td></tr>" +
            "</table>";
        this.formNode.set("html", html);

        this.identityArea = this.formNode.getElementById("form_startIdentity");

        // MWF.xDesktop.requireApp("Organization", "PersonExplorer", function(){
        //     var o = {
        //         "data": this.app.desktop.session.user,
        //         "explorer":{
        //             "app": {
        //                 "lp":this.lp
        //             },
        //             "actions": this.orgAction
        //         }
        //     };
        var _self = this;
        this.identitys.each(function(item){
            //if (item.woUnit){
            var id = new MWF.xApplication.process.TaskCenter.ProcessStarter.Identity(this.identityArea, item, this, this.css);
            id.node.store("identity", id);
            id.node.addEvents({
                "mouseover": function(){
                    this.addClass("mainColor_border");
                    this.setStyles(_self.css.identityNode_over);
                    this.getFirst().getLast().setStyles(_self.css.identityInforNameTextNode_over);
                    this.getFirst().getNext().getFirst().setStyles(_self.css.identityTitleNode_over);
                    this.getFirst().getNext().getNext().getFirst().setStyles(_self.css.identityTitleNode_over);
                    //this.getFirst().getNext().getNext().getNext().getFirst().setStyles(_self.css.identityTitleNode_over);
                },
                "mouseout": function(){
                    this.removeClass("mainColor_border");
                    this.setStyles((layout.mobile) ? _self.css.identityNode_mobile : _self.css.identityNode);
                    this.getFirst().getLast().setStyles(_self.css.identityInforNameTextNode);
                    this.getFirst().getNext().getFirst().setStyles(_self.css.identityTitleNode);
                    this.getFirst().getNext().getNext().getFirst().setStyles(_self.css.identityTitleNode);
                    //this.getFirst().getNext().getNext().getNext().getFirst().setStyles(_self.css.identityTitleNode);
                },
                "click": function(){
                    var identity = this.retrieve("identity");
                    if (identity){
                        _self.okStartProcess(identity.data.distinguishedName);
                    }
                }
            });
            // if (id.data.major){
            //     id.node.setStyles(this.css.identityNode_over);
            //     id.node.getFirst().getLast().setStyles(this.css.identityInforNameTextNode_over);
            //     id.node.getFirst().getNext().getFirst().setStyles(this.css.identityTitleNode_over);
            //     id.node.getFirst().getNext().getNext().getFirst().setStyles(this.css.identityTitleNode_over);
            // }
        }.bind(this));

        if (layout.mobile){
            this.areaNode.setStyles(this.css.areaNode_mobile);
            this.createNode.setStyles(this.css.createNode_mobile);
        }
    },
    getOrgAction: function(callback){
        if (!this.orgAction){
            this.orgAction = MWF.Actions.get("x_organization_assemble_control");
            if (callback) callback();
            // MWF.xDesktop.requireApp("Selector", "Actions.RestActions", function(){
            //     this.orgAction = new MWF.xApplication.Selector.Actions.RestActions();
            //     if (callback) callback();
            // }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    setStartNodeSize: function(){
        if (!layout.mobile){
            var app = (this.app.embeded && this.app.eventTarget) ? this.app.eventTarget : this.app;
            var size = app.content.getSize();
            var allSize = app.content.getSize();
            this.markNode.setStyles({
                "width": ""+allSize.x+"px",
                "height": ""+allSize.y+"px"
            });
            this.areaNode.setStyles({
                "width": ""+size.x+"px",
                "height": ""+size.y+"px"
            });
            var hY = size.y*0.7;
            var mY = size.y*0.3/2;
            this.createNode.setStyles({
                "height": ""+hY+"px",
                "margin-top": ""+mY+"px"
            });
            var count = this.identitys.length;
            if (count>2) count=2;
            var w = count*320;
            this.formNode.setStyles({
                "width": ""+w+"px"
            });
            w = w + 60;
            this.createNode.setStyles({
                "width": ""+w+"px"
            });
        }
    },
    cancelStartProcess: function(e){
        this.markNode.destroy();
        this.areaNode.destroy();
    },
    okStartProcess: function(identity){
        var data = {
            "title": this.data.name+"-"+this.lp.unnamed,
            "latest": this.options.latest,
            "skipDraftCheck": this.options.skipDraftCheck,
            "identity": identity
        };
        if( this.options.workData ){
            data.data = this.options.workData;
            if (data.data.title || data.data.subject) data.title = data.data.title || data.data.subject;
        }

        if (!data.identity){
            this.departmentSelArea.setStyle("border-color", "red");
            if (this.app && this.app.notice) this.app.notice(this.lp.selectStartId, "error");
        }else{
            this.mask = new MWF.widget.Mask({"style": "desktop"});
            this.mask.loadNode(this.areaNode);
            this.fireEvent("beforeStarted", [data]);
            this.getWorkAction(function(){
                this.workAction.startWork(function(json){
                    this.mask.hide();

                    this.markNode.destroy();
                    this.areaNode.destroy();

                    this.fireEvent("started", [json.data, data.title, this.data.name]);

                    if (this.app && this.app.refreshAll) this.app.refreshAll();
                    //if (this.app && this.app.notice) this.app.notice(this.lp.processStarted, "success");
                    //    this.app.processConfig();
                }.bind(this), function(xhr, text, error){
                    if (xhr.status!=0){
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
                    }
                    if(this.mask)this.mask.hide();

                    // if(this.markNode)this.markNode.destroy();
                    // if(this.areaNode)this.areaNode.destroy();
                }.bind(this), this.data.id, data);
            }.bind(this));
        }
    },
    getWorkAction: function(callback){
        if (!this.workAction){
            this.workAction = MWF.Actions.get("x_processplatform_assemble_surface");
            if (callback) callback();
            // MWF.xDesktop.requireApp("process.TaskCenter", "Actions.RestActions", function(){
            //     this.workAction = new MWF.xApplication.process.TaskCenter.Actions.RestActions();
            //     if (callback) callback();
            // }.bind(this));
        }else{
            if (callback) callback();
        }
    }

});

MWF.xApplication.process.TaskCenter.ProcessStarter.Identity = new Class({
    initialize: function(container, data, starter, style){
        this.container = $(container);
        this.data = data;
        this.starter = starter;
        this.action = this.starter.orgAction;
        this.style = style;
        //this.item = item;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {
            "styles": (layout.mobile) ? this.style.identityNode_mobile : this.style.identityNode
        }).inject(this.container);

        var nameNode = new Element("div", {
            "styles": this.style.identityInforNameNode
        }).inject(this.node);

        var url = this.action.getPersonIcon(this.starter.app.desktop.session.user.id);
        var img = "<img width='50' height='50' border='0' src='"+url+"'></img>";
        // if (this.item.data.icon){
        //     img = "<img width='50' height='50' border='0' src='data:image/png;base64,"+this.item.data.icon+"'></img>"
        // }else{
        //     if (this.item.data.genderType=="f"){
        //         img = "<img width='50' height='50' border='0' src='"+"../x_component_Organization/$PersonExplorer/default/icon/female.png'></img>";
        //     }else{
        //         img = "<img width='50' height='50' border='0' src='"+"../x_component_Organization/$PersonExplorer/default/icon/man.png'></img>";
        //     }
        // }

        var picNode = new Element("div", {
            "styles": this.style.identityInforPicNode,
            "html": img
        }).inject(nameNode);
        var nameTextNode = new Element("div", {
            "styles": this.style.identityInforNameTextNode,
            "text": this.data.name
        }).inject(nameNode);

        var unitNode = new Element("div", {"styles": this.style.identityDepartmentNode}).inject(this.node);
        var unitTitleNode = new Element("div", {
            "styles": this.style.identityTitleNode,
            "text": MWF.xApplication.process.TaskCenter.LP.unit
        }).inject(unitNode);
        this.unitTextNode = new Element("div", {"styles": this.style.identityTextNode}).inject(unitNode);
        if (this.data.woUnit) this.unitTextNode.set({"text": this.data.woUnit.levelName, "title": this.data.woUnit.levelName});

        // var companyNode = new Element("div", {"styles": this.style.identityCompanyNode}).inject(this.node);
        // var companyTitleNode = new Element("div", {
        //     "styles": this.style.identityTitleNode,
        //     "text": this.item.explorer.app.lp.company
        // }).inject(companyNode);
        // this.companyTextNode = new Element("div", {"styles": this.style.identityTextNode}).inject(companyNode);

        var dutyNode = new Element("div", {"styles": this.style.identityDutyNode}).inject(this.node);
        var dutyTitleNode = new Element("div", {
            "styles": this.style.identityTitleNode,
            "text": MWF.xApplication.process.TaskCenter.LP.duty
        }).inject(dutyNode);
        this.dutyTextNode = new Element("div", {"styles": this.style.identityTextNode}).inject(dutyNode);
        var dutyTextList = [];
        var dutyTitleList = [];
        this.data.woUnitDutyList.each(function(duty){
            dutyTextList.push(duty.name);
            if (duty.woUnit) dutyTitleList.push(duty.name+"("+duty.woUnit.levelName+")");
        }.bind(this));
        this.dutyTextNode.set({"text": dutyTextList.join(", "), "title": dutyTitleList.join(", ")});

        var unitTextNode = new Element("div.mainColor_color", {
            "styles": this.style.identityInforUnitTextNode,
            "text": "【"+this.data.woUnit.name+"】"
        }).inject(this.node);

        // this.item.explorer.actions.getDepartment(function(json){
        //     this.department = json.data;
        //     this.departmentTextNode.set({"text": this.department.name, "title": this.department.name});
        //
        //     this.item.explorer.actions.getCompany(function(json){
        //         this.company = json.data;
        //         this.companyTextNode.set({"text": this.company.name, "title": this.company.name});
        //     }.bind(this), null, this.department.company);
        //
        // }.bind(this), null, this.data.department);
        //
        //
        // this.item.explorer.actions.listCompanyDutyByIdentity(function(json){
        //     json.data.each(function(duty){
        //         var text = this.dutyTextNode.get("text");
        //         if (text){
        //             text = text+", "+duty.name;
        //         }else{
        //             text = duty.name;
        //         }
        //         this.dutyTextNode.set({"text": text, "title": text});
        //     }.bind(this));
        // }.bind(this), null, this.data.id);

        // this.item.explorer.actions.listDepartmentDutyByIdentity(function(json){
        //     json.data.each(function(duty){
        //         var text = this.dutyTextNode.get("text");
        //         if (text){
        //             text = text+", "+duty.name;
        //         }else{
        //             text = duty.name;
        //         }
        //         this.dutyTextNode.set({"text": text, "title": text});
        //     }.bind(this));
        // }.bind(this), null, this.data.id);
    }
});
// MWF.xApplication.process.TaskCenter.ProcessStarter.DepartmentSel = new Class({
//     initialize: function(data, starter, container, idArea){
//         this.data = data;
//         this.starter = starter;
//         this.container = container;
//         this.idArea = idArea;
//         this.css = this.starter.css;
//         this.isSelected = false;
//         this.load();
//     },
//     load: function(){
//
//         this.node = new Element("div", {"styles": this.css.departSelNode}).inject(this.container);
//         this.starter.orgAction.getDepartmentByIdentity(function(department){
//             this.node.set("text", department.data.name);
//         }.bind(this), null, this.data.name);
//
//         this.node.addEvents({
//             "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css.departSelNode_over);}.bind(this),
//             "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css.departSelNode_out);}.bind(this),
//             "click": function(){
//                 this.selected();
//             }.bind(this),
//         });
//     },
//     selected: function(){
//         if (!this.isSelected){
//             if (this.starter.currentDepartment) this.starter.currentDepartment.unSelected();
//             this.node.setStyles(this.css.departSelNode_selected);
//             this.isSelected = true;
//             this.starter.currentDepartment = this;
//
//             this.idArea.set({
//                 "text": this.data.display,
//                 "value": this.data.name
//             });
//         }
//     },
//     unSelected: function(){
//         if (this.isSelected){
//             if (this.starter.currentDepartment) this.starter.currentDepartment = null;;
//             this.node.setStyles(this.css.departSelNode);
//             this.isSelected = false;
//         }
//     }
//
// });
