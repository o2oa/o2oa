MWF.require("MWF.widget.Mask", null, false);
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.TaskCenter = MWF.xApplication.process.TaskCenter || {};
MWF.xApplication.process.TaskCenter.ProcessStarter = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
	options: {
		"style": "default"
	},
    initialize: function(data, app, options){
        this.setOptions(options);
        this.path = "/x_component_process_TaskCenter/$ProcessStarter/";
        this.cssPath = "/x_component_process_TaskCenter/$ProcessStarter/"+this.options.style+"/css.wcss";
        this._loadCss();

        MWF.xDesktop.requireApp("process.TaskCenter", "$ProcessStarter."+MWF.language, null, false);
        this.lp = MWF.xApplication.process.TaskCenter.ProcessStarter.lp;

        this.data = data;
        this.app = app;
    },
    load: function(){
        this.getOrgAction(function(){
            if (this.app.desktop.session.user.name){
                this.orgAction.listIdentityByPerson(function(json){
                    this.identitys = json.data;
                    if (json.data.length){
                        if (json.data.length==1){
                            var data = {
                                "title": this.data.name+"-"+this.lp.unnamed,
                                "identity": this.identitys[0].name
                            };

                            this.mask = new MWF.widget.Mask({"style": "desktop"});
                            this.mask.loadNode(this.app.content);
                            this.getWorkAction(function(){
                                this.workAction.startWork(function(json){
                                    this.mask.hide();
                                    //this.markNode.destroy();
                                    //this.areaNode.destroy();

                                    this.fireEvent("started", [json.data, data.title, this.data.name]);

                                    if (this.app.refreshAll) this.app.refreshAll();
                                    this.app.notice(this.lp.processStarted, "success");
                                    //    this.app.processConfig();
                                }.bind(this), null, this.data.id, data);
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
                    }
                }.bind(this), null, this.app.desktop.session.user.name)
            }
        }.bind(this));
    },

    createMarkNode: function(){
        this.markNode = new Element("div#mark", {
            "styles": this.css.markNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.app.content);
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
            "<tr><td colSpan=\"2\" style=\"height: 50px; line-height: 60px; text-align: center; font-size: 24px; font-weight: bold\">" +
            this.lp.start+" - "+this.data.name+"</td></tr>" +
            "<tr><td colSpan=\"2\" style=\"height: 40px; color: #0044cc; line-height: 60px; text-align: center; font-size: 18px; font-weight: bold\">" +
            this.lp.selectStartIdentity+"</td></tr>" +
            "<tr><td colSpan=\"2\" id=\"form_startIdentity\"></td></tr>" +
            //"<tr><td style=\"height: 30px; line-height: 30px; text-align: left\">"+this.lp.department+":</td>" +
            //"<td style=\"; text-align: left;\" id=\"form_startDepartment\"></td></tr>" +
            //"<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.lp.identity+":</td>" +
            //"<td style=\"; text-align: left;\"><div id=\"form_startIdentity\"></div></td></tr>" +
            //"<tr style=\"display: none\"><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.lp.date+":</td>" +
            //"<td style=\"; text-align: left;\"><div id=\"form_startDate\"></div></td></tr>" +
            //"<tr style=\"display: none\"><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.lp.subject+":</td>" +
            //"<td style=\"text-align: left;\"><input type=\"text\" id=\"form_startSubject\" " +
            //"style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            //"height: 26px;\"/></td></tr>" +
            "</table>";
        this.formNode.set("html", html);

        this.identityArea = this.formNode.getElementById("form_startIdentity");

        MWF.xDesktop.requireApp("Organization", "PersonExplorer", function(){
            var o = {
                "data": this.app.desktop.session.user,
                "explorer":{
                    "app": {
                        "lp":this.lp
                    },
                    "actions": this.orgAction
                }
            };
            var _self = this;
            this.identitys.each(function(item){
                var id = new MWF.xApplication.Organization.PersonExplorer.Identity(this.identityArea, item, o, this.css);
                id.node.store("identity", id);
                id.node.addEvents({
                    "mouseover": function(){
                        this.setStyles(_self.css.identityNode_over);
                        this.getFirst().getLast().setStyles(_self.css.identityInforNameTextNode_over);
                        this.getFirst().getNext().getFirst().setStyles(_self.css.identityTitleNode_over);
                        this.getFirst().getNext().getNext().getFirst().setStyles(_self.css.identityTitleNode_over);
                        this.getFirst().getNext().getNext().getNext().getFirst().setStyles(_self.css.identityTitleNode_over);
                    },
                    "mouseout": function(){
                        this.setStyles(_self.css.identityNode);
                        this.getFirst().getLast().setStyles(_self.css.identityInforNameTextNode);
                        this.getFirst().getNext().getFirst().setStyles(_self.css.identityTitleNode);
                        this.getFirst().getNext().getNext().getFirst().setStyles(_self.css.identityTitleNode);
                        this.getFirst().getNext().getNext().getNext().getFirst().setStyles(_self.css.identityTitleNode);
                    },
                    "click": function(){
                        var identity = this.retrieve("identity");
                        if (identity){
                            _self.okStartProcess(identity.data.name);
                        }
                    }
                });
            }.bind(this));

        }.bind(this));
        //
        //this.setStartFormContent();
        //this.actionNode = new Element("div", {
        //    "styles": this.css.actionNode
        //}).inject(this.formNode);
        //

        //
        //this.cancelActionNode.addEvent("click", function(e){
        //    this.cancelStartProcess(e);
        //}.bind(this));
        //this.startOkActionNode.addEvent("click", function(e){
        //    this.okStartProcess(e);
        //}.bind(this));
    },
    setStartFormContent: function(){
        this.dateArea = this.formNode.getElementById("form_startDate");
        var d = new Date();
        this.dateArea.set("text", d.format("%Y-%m-%d %H:%M"));

        this.departmentSelArea = this.formNode.getElementById("form_startDepartment");
        this.identityArea = this.formNode.getElementById("form_startIdentity");

        this.getOrgAction(function(){
            //if (!this.app.desktop.user.id){
            //    this.orgAction.listPersonByKey(function(json){
            //        if (json.data.length) this.app.desktop.user.id = json.data[0].id
            //        this.loadDepartments();
            //    }.bind(this), null, this.app.desktop.user.name);
            //}else{
            //    this.loadDepartments();
            //}
            this.loadDepartments();
        }.bind(this));
    },
    loadDepartments: function(){
        //if (this.app.desktop.user.id){
        //    this.orgAction.listIdentityByPerson(function(json){
        //        var selected = (json.data.length==1) ? true : false;
        //        json.data.each(function(id){
        //            var departSel = new MWF.xApplication.process.TaskCenter.ProcessStarter.DepartmentSel(id, this, this.departmentSelArea, this.identityArea);
        //            if (selected) departSel.selected();
        //        }.bind(this));
        //    }.bind(this), null, this.app.desktop.user.id)
        //}
        //if (this.app.desktop.session.user.name){
        //    this.orgAction.listIdentityByPerson(function(json){
        //        var selected = (json.data.length==1) ? true : false;
        //        json.data.each(function(id){
        //            var departSel = new MWF.xApplication.process.TaskCenter.ProcessStarter.DepartmentSel(id, this, this.departmentSelArea, this.identityArea);
        //            if (selected) departSel.selected();
        //        }.bind(this));
        //    }.bind(this), null, this.app.desktop.session.user.name)
        //}

        var selected = (this.identitys.length==1);
        this.identitys.each(function(id){
            var departSel = new MWF.xApplication.process.TaskCenter.ProcessStarter.DepartmentSel(id, this, this.departmentSelArea, this.identityArea);
            if (selected) departSel.selected();
        }.bind(this));
    },

    getOrgAction: function(callback){
        if (!this.orgAction){
            //MWF.xDesktop.requireApp("Organization", "Actions.RestActions", function(){
            //    this.orgAction = new MWF.xApplication.Organization.Actions.RestActions();
            //    if (callback) callback();
            //}.bind(this));
            MWF.xDesktop.requireApp("Selector", "Actions.RestActions", function(){
                this.orgAction = new MWF.xApplication.Selector.Actions.RestActions();
                if (callback) callback();
            }.bind(this));



            //MWF.require("MWF.xAction.org.express.RestActions", function(){
            //    this.orgAction = new MWF.xAction.org.express.RestActions();
            //    if (callback) callback();
            //}.bind(this));
        }else{
            if (callback) callback();
        }
    },
    setStartNodeSize: function(){
        var size = this.app.content.getSize();
        var allSize = this.app.content.getSize();
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
        var w = this.identitys.length*294;
        this.formNode.setStyles({
            "width": ""+w+"px"
        });
        w = w + 60;
        this.createNode.setStyles({
            "width": ""+w+"px"
        });
        //var iconSize = this.createNewNode.getSize();
        //var formHeight = hY*0.7;
        //if (formHeight>250) formHeight = 250;
        //var formMargin = hY*0.3/2-iconSize.y;
        //this.formNode.setStyles({
        //    "height": ""+formHeight+"px",
        //    "margin-top": ""+formMargin+"px"
        //});
    },
    cancelStartProcess: function(e){
        //var _self = this;
        //if ($("form_startSubject").get("value")){
        //    this.app.confirm("warn", e, this.lp.startProcess_cancel_title, this.lp.startProcess_cancel, "320px", "100px", function(){
        //        _self.markNode.destroy();
        //        _self.areaNode.destroy();
        //        this.close();
        //    },function(){
        //        this.close();
        //    }, null, this.app.content);
        //}else{
        //    this.markNode.destroy();
        //    this.areaNode.destroy();
        //}

        this.markNode.destroy();
        this.areaNode.destroy();
    },
    okStartProcess: function(identity){
        //var title = $("form_startSubject").get("value");
        //var data = {
        //    "title": title,
        //    "identity": this.identityArea.get("value")
        //};
        var data = {
            "title": this.data.name+"-"+this.lp.unnamed,
            //"identity": this.identityArea.get("value")
            "identity": identity
        };

        //if (!data.title){
        //    $("form_startSubject").setStyle("border-color", "red");
        //    $("form_startSubject").focus();
        //    this.app.notice(this.lp.inputProcessSubject, "error");
        //}else
        if (!data.identity){
            this.departmentSelArea.setStyle("border-color", "red");
            this.app.notice(this.lp.selectStartId, "error");
        }else{
            this.mask = new MWF.widget.Mask({"style": "desktop"});
            this.mask.loadNode(this.areaNode);
            this.getWorkAction(function(){
                this.workAction.startWork(function(json){
                    this.mask.hide();

                    this.markNode.destroy();
                    this.areaNode.destroy();

                    this.fireEvent("started", [json.data, data.title, this.data.name]);

                    this.app.refreshAll();
                    this.app.notice(this.lp.processStarted, "success");
                    //    this.app.processConfig();
                }.bind(this), null, this.data.id, data);
            }.bind(this));
        }
    },
    getWorkAction: function(callback){
        if (!this.workAction){
            MWF.xDesktop.requireApp("process.TaskCenter", "Actions.RestActions", function(){
                this.workAction = new MWF.xApplication.process.TaskCenter.Actions.RestActions();
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    }

});
MWF.xApplication.process.TaskCenter.ProcessStarter.DepartmentSel = new Class({
    initialize: function(data, starter, container, idArea){
        this.data = data;
        this.starter = starter;
        this.container = container;
        this.idArea = idArea;
        this.css = this.starter.css;
        this.isSelected = false;
        this.load();
    },
    load: function(){

        this.node = new Element("div", {"styles": this.css.departSelNode}).inject(this.container);
        this.starter.orgAction.getDepartmentByIdentity(function(department){
            this.node.set("text", department.data.name);
        }.bind(this), null, this.data.name);

        this.node.addEvents({
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css.departSelNode_over);}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css.departSelNode_out);}.bind(this),
            "click": function(){
                this.selected();
            }.bind(this),
        });
    },
    selected: function(){
        if (!this.isSelected){
            if (this.starter.currentDepartment) this.starter.currentDepartment.unSelected();
            this.node.setStyles(this.css.departSelNode_selected);
            this.isSelected = true;
            this.starter.currentDepartment = this;

            this.idArea.set({
                "text": this.data.display,
                "value": this.data.name
            });
        }
    },
    unSelected: function(){
        if (this.isSelected){
            if (this.starter.currentDepartment) this.starter.currentDepartment = null;;
            this.node.setStyles(this.css.departSelNode);
            this.isSelected = false;
        }
    }

});