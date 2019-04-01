MWF.require("MWF.widget.Mask", null, false);
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Index = MWF.xApplication.cms.Index || {};
MWF.xApplication.cms.Index.Starter = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
	options: {
		"style": "default",
        "ignoreDrafted" : false,
        "categoryFlag" : "", //category id or name
        "columnFlag" : "",//column id or name
        "appFlag" : "" //column id or name
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
    initData : function(){
        var appId = this.options.appFlag || this.options.columnFlag;
        if( !this.columnData && appId){
            MWF.Actions.get("x_cms_assemble_control").getColumn( appId, function( json ){
                this.columnData = json.data
            }.bind(this), null, false)
        }

        var categoryId = this.options.categoryFlag;
        if( !this.categoryData && categoryId ){
            MWF.Actions.get("x_cms_assemble_control").getCategory( categoryId, function(js){
                this.categoryData = js.data;
                if( !this.columnData ){
                    MWF.Actions.get("x_cms_assemble_control").getColumn( this.categoryData.appId, function( json ){
                        this.columnData = json.data;
                    }.bind(this), null, false)
                }
            }.bind(this), null, false)
        }
    },
    load: function( ){
        this.initData();
        this.identityList = this.getIdentities();
        if( this.options.ignoreDrafted ){
            this._load();
            this.fireEvent( "postLoad" );
        }else if(this.categoryData.workflowAppId && this.categoryData.workflowFlag ){
            this._load();
            this.fireEvent( "postLoad" );
        }else{
            var fielter = {
                "categoryIdList": [this.categoryData.id ],
                "creatorList": [layout.desktop.session.user.distinguishedName]
            };
            this.getDocumentAction( function(){
                this.documentAction.listDraftNext("(0)", 1, fielter, function(json){
                    if( json.data.length > 0 ){
                        this._openDocument(json.data[0].id);
                        this.fireEvent( "postLoad" );
                    }else{
                        this._load();
                        this.fireEvent( "postLoad" );
                    }
                }.bind(this));
            }.bind(this));
        }
    },
    _load: function(){
        if( this.categoryData && this.isIgnoreTitle() && this.identityList.length == 1 ) { //信息需要输入标题，数据不需要输入标题
            this.okStart();
        }else{
            this.createMarkNode();
            this.createAreaNode();
            this.createStartNode();

            this.checkSubject();

            this.areaNode.inject(this.markNode, "after");
            this.areaNode.fade("in");
            if($("form_startSubject"))$("form_startSubject").focus();

            this.setStartNodeSize();
            this.setStartNodeSizeFun = this.setStartNodeSize.bind(this);
            this.app.addEvent("resize", this.setStartNodeSizeFun);
        }
    },
    _openDocument: function(id,el){
        var _self = this;

        var appId = "cms.Document"+id;
        if (_self.app.desktop.apps[appId]){
            _self.app.desktop.apps[appId].setCurrent();
        }else {
            var options = {
                "readonly" :false,
                "documentId": id,
                "appId": appId,
                "postPublish" : function(){
                    //if(_self.creater.view )_self.creater.view.reload();
                    this.fireEvent( "postPublish" );
                }.bind(this)
            };
            this.app.desktop.openApplication(el, "cms.Document", options);
        }
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

        var categoryName = this.categoryData.name || this.categoryData.categoryName;
        var html = "<table width=\"100%\" height=\"90%\" border=\"0\" cellPadding=\"0\" cellSpacing=\"0\">" +
            "<tr><td colSpan=\"2\" style=\"height: 60px; line-height: 60px; text-align: center; font-size: 24px; font-weight: bold\">" +
            this.lp.start+" - "+categoryName+"</td></tr>" +
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
    getIdentities: function(){
        var identities = [];
        MWF.Actions.get("x_organization_assemble_personal").getPerson(function(json){
            var identities1 = (json.data && json.data.woIdentityList) ? json.data.woIdentityList : [];
            identities1.each( function(i){
                if( this.options.identity ){
                    if( this.options.identity == i.distinguishedName ){
                        if( i.distinguishedName )identities.push(i);
                    }
                }else{
                    if( i.distinguishedName )identities.push(i);
                }
            }.bind(this));
        }.bind(this), null, false );
        return identities;
    },
    setStartFormContent: function(){
        this.dateArea = this.formNode.getElementById("form_startDate");
        var d = new Date();
        this.dateArea.set("text", d.format("%Y-%m-%d %H:%M"));

        this.departmentSelArea = this.formNode.getElementById("form_startDepartment");
        this.identityArea = this.formNode.getElementById("form_startIdentity");

        //this.getOrgAction(function(){
            //if (!this.app.desktop.user.id){
            //    this.orgAction.listPersonByKey(function(json){
            //        if (json.data.length) this.app.desktop.user.id = json.data[0].id
            //        this.loadDepartments();
            //    }.bind(this), null, this.app.desktop.user.name);
            //}else{
            //    this.loadDepartments();
            //}
            this.loadDepartments();
        //}.bind(this));
    },
    isIgnoreTitle : function(){
        return this.categoryData && this.categoryData.documentType != "信息"
    },
    checkSubject: function(){
        if( this.categoryData &&  this.subjectInput ){
            if( this.isIgnoreTitle() ){
                this.subjectInput.getParent("tr").setStyle("display","none");
            }else{
                this.subjectInput.getParent("tr").setStyle("display","");
            }
        }
    },
    loadDepartments: function(){
        //if (this.app.desktop.session.user.name){
        //    this.orgAction.listIdentityByPerson(function(json){
        //        var selected = (json.data.length==1) ? true : false;
        //        json.data.each(function(id){
        //            var departSel = new MWF.xApplication.cms.Index.Starter.DepartmentSel(id, this, this.departmentSelArea, this.identityArea);
        //            if (selected) departSel.selected();
        //        }.bind(this));
        //    }.bind(this), null, this.app.desktop.session.user.name)
        //}
        MWF.Actions.get("x_organization_assemble_personal").getPerson(function(json){
            var identities1 = (json.data && json.data.woIdentityList) ? json.data.woIdentityList : [];
            var identities = [];
            identities1.each( function(i){
                if( i.distinguishedName )identities.push(i);
            }.bind(this));
            var selected = (identities.length==1) ? true : false;
            identities.each(function(id){
                var departSel = new MWF.xApplication.cms.Index.Starter.DepartmentSel(id, this, this.departmentSelArea, this.identityArea);
                if (selected) departSel.selected();
            }.bind(this));
        }.bind(this), null )
    },

    //getOrgAction: function(callback){
    //    if (!this.orgAction){
    //        MWF.require("MWF.xAction.org.express.RestActions", function(){
    //            this.orgAction = new MWF.xAction.org.express.RestActions();
    //            if (callback) callback();
    //        }.bind(this));
    //    }else{
    //        if (callback) callback();
    //    }
    //},
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
        if( hY > 500 )hY = 500;
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
        if ($("form_startSubject") && $("form_startSubject").get("value")){
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
    okStart: function(){
        if( this.categoryData.workflowAppId && this.categoryData.workflowFlag ){
            this._createProcessDocument();
        }else{
            this._createDocument();
        }
    },
    _createDocument: function(e){
        var subjectObj = $("form_startSubject");
        var title = subjectObj ? subjectObj.get("value") : "";
        this.getDocumentAction();
        var data = {
            "id" : this.documentAction.getUUID(),
            "isNewDocument" : true,
            "title": title,
            "creatorIdentity": this.identityArea ? this.identityArea.get("value") : this.identityList[0].distinguishedName,
            "appId" :this.categoryData.appId,
            "categoryId" : this.categoryData.id,
            "form" : this.categoryData.formId,
            "formName" :this.categoryData.formName,
            "docStatus" : "draft",
            "categoryName" : this.categoryData.name || this.categoryData.categoryName,
            "categoryAlias" : this.categoryData.alias || this.categoryData.categoryAlias,
            "attachmentList" : []
        };

        if (!data.title && !this.isIgnoreTitle()){
            if( subjectObj ){
                subjectObj.setStyle("border-color", "red");
                subjectObj.focus();
            }
            this.app.notice(this.lp.inputSubject, "error");
        }else if (!data.creatorIdentity){
            this.departmentSelArea.setStyle("border-color", "red");
            this.app.notice(this.lp.selectStartId, "error");
        }else{
            if( this.isIgnoreTitle() )data.title = "无标题";
            if( this.areaNode ){
                this.mask = new MWF.widget.Mask({"style": "desktop"});
                this.mask.loadNode(this.areaNode);
            }
            this.getDocumentAction(function(){
                this.documentAction.addDocument( data, function(json){
                    if(this.mask)this.mask.hide();

                    if(this.markNode)this.markNode.destroy();
                    if(this.areaNode)this.areaNode.destroy();

                    this._openDocument( json.data.id );
                    //this.fireEvent("started", [json.data, title, this.categoryData.name]);

                    //this.app.refreshAll();
                    this.app.notice(this.lp.Started, "success");
                    //    this.app.processConfig();
                }.bind(this), null);
            }.bind(this));
        }
    },
    getDocumentAction: function(callback){
        if (!this.documentAction){
            //MWF.xDesktop.requireApp("cms.Index", "Actions.RestActions", function(){
                this.documentAction = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.Index.Actions.RestActions();
                if (callback) callback();
            //}.bind(this));
        }else{
            if (callback) callback();
        }
    },


    _createProcessDocument:function(e){
        var subjectObj = $("form_startSubject");
        var title = subjectObj ? subjectObj.get("value") : "";
        var processId = this.categoryData.workflowFlag;
        var data = {
            "title":title,
            "identity": this.identityArea.get("value")
        };
        if (!data.title || !this.isIgnoreTitle()){
            if( subjectObj ){
                subjectObj.setStyle("border-color", "red");
                subjectObj.focus();
            }
            this.app.notice(this.lp.inputSubject, "error");
        }else if (!data.identity){
            this.departmentSelArea.setStyle("border-color", "red");
            this.app.notice(this.lp.selectStartId, "error");
        }else{
            if( this.isIgnoreTitle() )title = "无标题";
            var workData = {
                cmsDocument : {
                    "isNewDocument" : true,
                    "title": title,
                    "creatorIdentity": data.identity,
                    "appId" :this.categoryData.appId,
                    "categoryId" : this.categoryData.id,
                    //"form" : this.categoryData.formId,
                    //"formName" :this.categoryData.formName,
                    "docStatus" : "draft",
                    "categoryName" : this.categoryData.name || this.categoryData.categoryName,
                    "categoryAlias" : this.categoryData.alias || this.categoryData.categoryAlias,
                    "createTime": new Date().format("db"),
                    "attachmentList" : []
                }
            };

            this.mask = new MWF.widget.Mask({"style": "desktop"});
            this.mask.loadNode(this.areaNode);
            this.getDocumentAction(function(){
                MWF.Actions.get("x_processplatform_assemble_surface").startWork( function( json ){
                    this.mask.hide();

                    this.markNode.destroy();
                    this.areaNode.destroy();

                    this.afterStartProcess( json.data, data.title, this.categoryData.workflowName, workData );
                    //this.fireEvent("started", [json.data, title, this.categoryData.name]);

                    //this.app.refreshAll();
                    this.app.notice(this.lp.Started, "success");
                }.bind(this), null, processId, data)
            }.bind(this));
        }
    },
    afterStartProcess: function(data, title, processName, workData){
        var workInfors = [];
        var currentTask = [];

        data.each(function(work){
            if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
            workInfors.push(this.getStartWorkInforObj(work));
        }.bind(this));
        var workId = currentTask[0];

        MWF.Actions.get("x_processplatform_assemble_surface").saveData(function(){

            if (currentTask.length==1){
                var options = {"workId": workId};
                this.app.desktop.openApplication(null, "process.Work", options);

                this.createStartWorkResault(workInfors, title, processName, false);
            }else{
                this.createStartWorkResault(workInfors, title, processName, true);
            }

        }.bind(this), null, workId, workData)


    },
    getStartWorkInforObj: function(work){
        var users = [];
        var currentTask = "";
        work.taskList.each(function(task, idx){
            users.push(task.person+"("+task.department + ")");
            if (work.currentTaskIndex==idx) currentTask = task.id;
        }.bind(this));
        return {"activity": work.fromActivityName, "users": users, "currentTask": currentTask};
    },
    createStartWorkResault: function(workInfors, title, processName, isopen){
        var content = "";
        workInfors.each(function(infor){
            content += "<div><b>"+this.lp.nextActivity+"<font style=\"color: #ea621f\">"+infor.activity+"</font>, "+this.lp.nextUser+"<font style=\"color: #ea621f\">"+infor.users.join(", ")+"</font></b>";
            if (infor.currentTask && isopen){
                content += "&nbsp;&nbsp;&nbsp;&nbsp;<span value=\""+infor.currentTask+"\">"+this.lp.deal+"</span></div>";
            }else{
                content += "</div>";
            }
        }.bind(this));

        var msg = {
            "subject": this.lp.processStarted,
            "content": "<div>"+this.lp.processStartedMessage+"“["+processName+"]"+title+"”</div>"+content
        };
        var tooltip = layout.desktop.message.addTooltip(msg);
        var item = layout.desktop.message.addMessage(msg);

        this.setStartWorkResaultAction(tooltip);
        this.setStartWorkResaultAction(item);
    },
    setStartWorkResaultAction: function(item){
        var node = item.node.getElements("span");
        node.setStyles(this.css.dealStartedWorkAction);
        var _self = this;
        node.addEvent("click", function(e){
            var options = {"taskId": this.get("value")};
            _self.app.desktop.openApplication(e, "process.Work", options);
        });
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
        //this.starter.orgAction.getDepartmentByIdentity(function(department){
        //    this.node.set("text", department.data.name);
        //}.bind(this), null, this.data.name);
        var unit = this.data.woUnit ? this.data.woUnit.name : this.lp.unnamedUnit;
        this.node.set("text",this.data.woUnit.name);

        this.node.addEvents({
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css.departSelNode_over);}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css.departSelNode_out);}.bind(this),
            "click": function(){
                this.selected();
            }.bind(this)
        });
    },
    selected: function(){
        if (!this.isSelected){
            if (this.starter.currentDepartment) this.starter.currentDepartment.unSelected();
            this.node.setStyles(this.css.departSelNode_selected);
            this.isSelected = true;
            this.starter.currentDepartment = this;

            this.idArea.set({
                "text": this.data.name,
                "value": this.data.distinguishedName
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