MWF.require("MWF.widget.Mask", null, false);
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Index = MWF.xApplication.cms.Index || {};
MWF.xApplication.cms.Index.Starter = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
	options: {
		"style": "default"
	},
    initialize: function(columnData, categoryData, app, options){
        this.setOptions(options);
        this.path = "/x_component_cms_Index/$Starter/";
        this.cssPath = "/x_component_cms_Index/$Starter/"+this.options.style+"/css.wcss";
        this._loadCss();

        MWF.xDesktop.requireApp("cms.Index", "$Starter."+MWF.language, null, false);
        this.lp = MWF.xApplication.cms.Index.Starter.lp;

        this.columnData = columnData;
        this.categoryData = categoryData;
        this.app = app;
    },
    load: function(){
        this.createMarkNode();
        this.createAreaNode();
        this.createStartNode();

        this.areaNode.inject(this.markNode, "after");
        this.areaNode.fade("in");
        $("form_startSubject").focus();

        this.setStartNodeSize();
        this.setStartNodeSizeFun = this.setStartNodeSize.bind(this);
        this.app.addEvent("resize", this.setStartNodeSizeFun);
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

        this.formNode = new Element("div", {
            "styles": this.css.formNode
        }).inject(this.createNode);

        var html = "<table width=\"100%\" height=\"90%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">" +
            "<tr><td colSpan=\"2\" style=\"height: 60px; line-height: 60px; text-align: center; font-size: 24px; font-weight: bold\">" +
            this.lp.start+" - "+this.categoryData.name+"</td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px; text-align: left\">"+this.lp.department+":</td>" +
            "<td style=\"; text-align: left;\" id=\"form_startDepartment\"></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.lp.identity+":</td>" +
            "<td style=\"; text-align: left;\"><div id=\"form_startIdentity\"></div></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.lp.date+":</td>" +
            "<td style=\"; text-align: left;\"><div id=\"form_startDate\"></div></td></tr>" +
            "<tr><td style=\"height: 30px; line-height: 30px;  text-align: left\">"+this.lp.subject+":</td>" +
            "<td style=\"; text-align: left;\"><input type=\"text\" id=\"form_startSubject\" " +
            "style=\"width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC; " +
            "height: 26px;\"/></td></tr>" +
            "</table>";
        this.formNode.set("html", html);

        this.setStartFormContent();

        this.cancelActionNode = new Element("div", {
            "styles": this.css.cancelActionNode,
            "text": this.lp.cancel
        }).inject(this.formNode);
        this.startOkActionNode = new Element("div", {
            "styles": this.css.startOkActionNode,
            "text": this.lp.ok
        }).inject(this.formNode);

        this.cancelActionNode.addEvent("click", function(e){
            this.cancelStart(e);
        }.bind(this));
        this.startOkActionNode.addEvent("click", function(e){
            this.okStart(e);
        }.bind(this));
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
        if (this.app.desktop.session.user.name){
            this.orgAction.listIdentityByPerson(function(json){
                var selected = (json.data.length==1) ? true : false;
                json.data.each(function(id){
                    var departSel = new MWF.xApplication.cms.Index.Starter.DepartmentSel(id, this, this.departmentSelArea, this.identityArea);
                    if (selected) departSel.selected();
                }.bind(this));
            }.bind(this), null, this.app.desktop.session.user.name)
        }
    },

    getOrgAction: function(callback){
        if (!this.orgAction){
            MWF.require("MWF.xAction.org.express.RestActions", function(){
                this.orgAction = new MWF.xAction.org.express.RestActions();
                if (callback) callback();
            }.bind(this));
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
        var hY = size.y*0.8;
        var mY = size.y*0.2/2;
        this.createNode.setStyles({
            "height": ""+hY+"px",
            "margin-top": ""+mY+"px"
        });

        var iconSize = this.createNewNode.getSize();
        var formHeight = hY*0.7;
        if (formHeight>250) formHeight = 250;
        var formMargin = hY*0.3/2-iconSize.y;
        this.formNode.setStyles({
            "height": ""+formHeight+"px",
            "margin-top": ""+formMargin+"px"
        });
    },
    cancelStart: function(e){
        var _self = this;
        if ($("form_startSubject").get("value")){
            this.app.confirm("warn", e, this.lp.start_cancel_title, this.lp.start_cancel, "320px", "100px", function(){
                _self.markNode.destroy();
                _self.areaNode.destroy();
                this.close();
            },function(){
                this.close();
            }, null, this.app.content);
        }else{
            this.markNode.destroy();
            this.areaNode.destroy();
        }
    },
    okStart: function(e){
        var title = $("form_startSubject").get("value");
        this.getDocumentAction();
        var data = {
            "id" : this.documentAction.getUUID(),
            "isNewDocument" : true,
            "title": title,
            "creatorIdentity": this.identityArea.get("value"),
            "appId" :this.categoryData.appId,
            "catagoryId" : this.categoryData.id,
            "form" : this.categoryData.formId,
            "formName" :this.categoryData.formName,
            "docStatus" : "draft",
            "catagoryName" : this.categoryData.name,
            "categoryAlias" : this.categoryData.alias,
            "attachmentList" : []
        };

        if (!data.title){
            $("form_startSubject").setStyle("border-color", "red");
            $("form_startSubject").focus();
            this.app.notice(this.lp.inputSubject, "error");
        }else if (!data.creatorIdentity){
            this.departmentSelArea.setStyle("border-color", "red");
            this.app.notice(this.lp.selectStartId, "error");
        }else{
            this.mask = new MWF.widget.Mask({"style": "desktop"});
            this.mask.loadNode(this.areaNode);
            this.getDocumentAction(function(){
                this.documentAction.addDocument( data, function(json){
                    this.mask.hide();

                    this.markNode.destroy();
                    this.areaNode.destroy();

                    this.fireEvent("started", [json.data, title, this.categoryData.name]);

                    //this.app.refreshAll();
                    this.app.notice(this.lp.Started, "success");
                    //    this.app.processConfig();
                }.bind(this), null);
            }.bind(this));
        }
    },
    getDocumentAction: function(callback){
        if (!this.documentAction){
            MWF.xDesktop.requireApp("cms.Index", "Actions.RestActions", function(){
                this.documentAction = new MWF.xApplication.cms.Index.Actions.RestActions();
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    }

});
MWF.xApplication.cms.Index.Starter.DepartmentSel = new Class({
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