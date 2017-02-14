MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Execution", "WorkForm", null, false);

MWF.xApplication.Execution.WorkDeploy = new Class({
    Extends: MWF.xApplication.Template.Explorer.PopupForm,
    Implements: [Options, Events],
    options: {
        //"centerWorkId" : "fc44be47-7271-469f-8f04-deebdb71d3e6",
        "style": "default",
        "width": "90%",
        "height": "90%",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": false,
        "closeAction": true,
        "isNew": false,
        "isEdited": true
    },
    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp.WorkDeploy;
        this.actions = this.app.restActions;
        this.path = "/x_component_Execution/$WorkDeploy/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};

        this.actions = actions;
    },
    load: function () {
        if(!this.appointSwitch){
            var switchData = {};
            switchData.configCode = "WORK_AUTHORIZE";
            this.actions.getProfileByCode(switchData,function(json){
                if(json.type == "success"){
                    if(json.data && json.data.configValue){
                        this.appointSwitch = json.data.configValue;
                    }
                }
            }.bind(this),null,false);
        }
        if(!this.splitSwitch){
            var switchData = {};
            switchData.configCode = "WORK_DISMANTLING";
            this.actions.getProfileByCode(switchData,function(json){
                if(json.type == "success"){
                    if(json.data && json.data.configValue){
                        this.splitSwitch = json.data.configValue;
                    }
                }
            }.bind(this),null,false);
        }

        if(!this.archiveSwitch){
            var switchData = {};
            switchData.configCode = "ARCHIVEMANAGER";
            this.actions.getProfileByCode(switchData,function(json){
                if(json.type == "success"){
                    if(json.data && json.data.configValue){
                        if(json.data.configValue.indexOf(this.app.identity)>-1)
                        this.archiveSwitch = true;
                    }
                }
            }.bind(this),null,false);
        }

        //alert(JSON.stringify(this.data))
        if (this.options.isNew) {
            this.create();
        } else if (this.options.isEdited) {
            this.edit();
        } else {
            this.open();
        }
    },
    createShade: function(txt){
        if(this.shadeDiv){ this.shadeDiv.destroy()}
        if(this.shadeTxtDiv)  this.shadeTxtDiv.destroy()
        this.shadeDiv = new Element("div.shadeDiv").inject(this.formNode)
        this.shadeTxtDiv = new Element("div.shadeTxtDiv").inject(this.shadeDiv);
        this.shadeTxtDiv.set("text",txt)

        this.shadeDiv.setStyles({
            "left":"0px","top":"40px","width":"100%","height":"100%","position":"absolute","opacity":"0.6","background-color":"#999999","z-index":"999",
            "text-align":"center"
        })
        this.shadeTxtDiv.setStyles({"color":"#ffffff","font-size":"30px","margin-top":"300px"})
    },
    destroyShade : function(){
        if(this.shadeDiv) this.shadeDiv.destroy()
        if(this.shadeDiv) this.shadeDiv.destroy()
    },
    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopIconNode = new Element("div", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode)

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.options.title
            }).inject(this.formTopNode)

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            this.formTopContentNode = new Element("div", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode)

            this._createTopContent();

        }
    },
    _createTopContent: function () {

        var html = "<span styles='formTopContentTitle' lable='drafter'></span>" +
            "    <span styles='formTopContentValue' item='drafter'></span>" +
            "<span styles='formTopContentTitle' lable='draftDepartment'></span>" +
            "    <span styles='formTopContentValue' item='draftDepartment'></span>" +
            "<span styles='formTopContentTitle' lable='draftDate'></span>" +
            "    <span styles='formTopContentValue' item='draftDate'></span>"

        this.formTopContentNode.set("html", html);

        this.getCenterWorkInfor( function( json ){
            if(!json.data){
                json.data = {};
                json.data.creatorName = this.app.user;
                json.data.creatorOrganizationName = this.app.department;
                json.data.createTime = json.date;
            }
            var form = new MForm(this.formTopContentNode, json.data, {
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    drafter: {text: this.lp.drafter + ":", name:"creatorName", type: "innertext"},
                    draftDepartment: {text: this.lp.draftDepartment + ":", name:"creatorOrganizationName", type: "innertext"},
                    draftDate: {text: this.lp.draftDate + ":",name:"createTime", type: "innertext"}
                }
            }, this.app, this.css);
            form.load();
        }.bind(this) )

    },
    getCenterWorkInfor: function( callback ){

        var auditData = {};
        this.reportAuditLeader = "";
        auditData.configCode = "REPORT_AUDIT_LEADER";
        this.actions.getProfileByCode(auditData,function(json){
            if(json.type == "success"){
                if(json.data && json.data.configValue){
                    this.reportAuditLeader = json.data.configValue
                }
            }

            //this.reportAuditLeader
        }.bind(this),null,false);
        if( this.centerWorkInforData ){
            if(callback)callback(this.centerWorkInforData);
        }else{

            this.actions.getMainTask( this.options.centerWorkId,function( json ){
                this.centerWorkInforData = json.data;
                if(callback)callback(json);
            }.bind(this),null,false)
        }
    },
    reloadContent: function(data){
        this.formTableArea.empty();
        this._createTableContent(data);
    },
    _createTableContent: function (data) {

        data = data || {
                processIdentity : this.app.identity,
                deployerName : this.app.user,
                creatorName : this.app.user
            }
        if(data.id){
            this.centerWorkId = data.id;
        }
        if(this.data.id){
            this.centerWorkId = this.data.id;
        }
        if(this.centerWorkId){
            this.getMainTask(this.centerWorkId, function( json ){
                data = json.data;
                this.centerWorkData = json.data;
            }.bind(this));
        }

        this.createCenterWorkInfor(data);
        this.importBaseWork();
        this.createMyWorkList();
        //this.createSplitWorkList();
        this.creataDeployWorkList();



    },
    importBaseWork: function(){

        this.importDiv = new Element("div.importDiv",{
            "styles" : this.css.importDiv
        }).inject(this.formTableArea);
        this.importTemplateDiv = new Element("div.importTemplateDiv",{
            "styles": this.css.importTemplateDiv,
            "text":this.lp.importTemplate
        }).inject(this.importDiv)
        this.importTemplateDiv.addEvents({
            "click":function(){
                window.open("/x_component_Execution/baseWork.xls")
            }.bind(this)
        })
        this.importTitleDiv = new Element("div.importTitleDiv",{
            "styles": this.css.importTitleDiv,
            "text":this.lp.importTemplateTitle
        }).inject(this.importDiv);


        //拟稿环节有导入操作，其他环节只有拟稿人才有导入操作
        if(this.centerWorkData==undefined || (this.centerWorkData && this.centerWorkData.processStatus==this.lp.statusDraft) || this.centerWorkData.deployerIdentity == this.app.identity ){

        }else{
            if(this.importDiv) {
                this.importDiv.destroy();
            }
        }

    },
    upload : function(){
        var r = this.centerForm.getResult(true,",",true,false,true);
        if( !r ){
            return false;
        }
        if(this.centerWorkData==undefined || (this.centerWorkData && this.centerWorkData.processStatus==this.lp.statusDraft)){
            this.saveMainTask( r, function( json ){

                    this.getMainTask(json.userMessage, function( data ){

                        this.centerWorkInforData = data.data;

                        this.loadCenterWorkInfor(data.data)

                    }.bind(this))

            }.bind(this) )
        }


        if (!this.uploadFileAreaNode){
            this.uploadFileAreaNode = new Element("div");
            var html = "<input name=\"file\" type=\"file\" />";
            this.uploadFileAreaNode.set("html", html);

            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", function(){

                var files = fileNode.files;
                if (files.length){
                    for (var i = 0; i < files.length; i++) {
                        var file = files.item(i);
                        var tmp = file.name.split(".");
                        this.uploadFileName = file.name;
                        if( tmp[tmp.length-1].toLowerCase() != "xls" && tmp[tmp.length-1].toLowerCase() != "xlsx" ){
                            this.app.notice("请导入excel文件！","error");
                            return;
                        }
                        var formData = new FormData();
                        formData.append('file', file);

                        this.createShade("正在导入，请稍后.....")
                        this.actions.importBaseWork(this.centerWorkId,function(json){

                            this.reloadContent(this.centerWorkInforData)
                            this.destroyShade()
                        }.bind(this),function(xhr,text,error){
                            var errorText = error;
                            if (xhr) errorMessage = xhr.responseText;
                            var e = JSON.parse(errorMessage);
                            if(e.userMessage){
                                this.app.notice( e.userMessage,"error");
                            }else{
                                this.app.notice( errorText,"error");
                            }

                            this.destroyShade()
                        }.bind(this),formData,file)

                    }
                }
            }.bind(this));
        }
        var fileNode = this.uploadFileAreaNode.getFirst();
        fileNode.click();
    },
    createCenterWorkInfor: function(data) {
        this.centerWorkContentArea = new Element("div.centerWorkContentArea", {
            "styles": this.css.workContentArea
        }).inject(this.formTableArea);


        var workContentTitleNode = new Element("div", {
            "styles": this.css.workContentTitleNode,
            "text": this.lp.centerWorkInfor
        }).inject(this.centerWorkContentArea);

        this.centerWorkContentNode = new Element("div.centerWorkContentNode", {
            "styles": this.css.workContentNode
        }).inject(this.centerWorkContentArea);

        this.loadCenterWorkInfor(data)

    },
    loadCenterWorkInfor: function(data){
        this.centerWorkId = data.id
        this.centerWorkContentNode.empty();
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='centerWorkInforTable'>" +
            "<tr><td styles='centerWorkInforTitle' lable='centerWorkTitle'></td>" +
            "    <td styles='centerWorkInforValue' item='centerWorkTitle'></td></tr>" +
            "<tr><td colspan='2'>" +
            //"<div styles='centerWorkInforTitleDiv' lable='defaultWorkType'></div>" +
            //"    <div styles='centerWorkInforValueDiv' item='defaultWorkType'></div>" +
            //"   <div styles='centerWorkInforTitleDiv' lable='defaultWorkLevel'></div>" +
            //"    <div styles='centerWorkInforValueDiv' item='defaultWorkLevel'></div>" +
            "   <div styles='centerWorkInforTitleDiv' lable='reportAuditLeader'></div>" +
            "    <div styles='centerWorkInforValueDiv' item='reportAuditLeader'></div>" +
            "   <div styles='centerWorkInforTitleDiv' lable='defaultWorkType'></div>" +
            "    <div styles='centerWorkInforValueDiv' item='defaultWorkType'></div>" +
            "   <div styles='centerWorkInforTitleDiv' lable='workCompletedLimit'></div>" +
            "    <div styles='centerWorkInforValueDiv' item='workCompletedLimit'></div>" +
            "</td></tr>" +
            "<tr><td styles='centerWorkInforTitle' lable='centerWorkMemo'></td>" +
            "    <td styles='centerWorkInforValue' item='centerWorkMemo'></td></tr>" +
            "</table>"
        this.centerWorkContentNode.set("html", html);


        //this.getCenterWorkInfor( function( json ){
        var resultWorkType = []
        var resultWorkTypeTxt = ""
        this.actions.listCategoryAll(function(json){
            if(json.data){
                for(i=0;i<json.data.length;i++){
                    if(json.data[i].workTypeName){
                        resultWorkType.push(json.data[i].workTypeName)
                    }
                }
            }
            }.bind(this),null,false
        )
        resultWorkTypeTxt = resultWorkType.join(",")
        if(resultWorkType.length>0){
            resultWorkTypeTxt = ","+resultWorkTypeTxt
        }


        var form = this.centerForm = new MForm(this.centerWorkContentNode, data, {
            isEdited: this.isEdited || this.isNew,

            itemTemplate: {
                centerWorkTitle: {
                    text: this.lp.centerWorkTitle + ":", name : "title", type: "text",
                    notEmpty:true,
                },
                defaultWorkType: {
                    text: this.lp.defaultWorkType + ":", name : "defaultWorkType", type: "select",
                    selectValue : resultWorkTypeTxt,
                    selectText : resultWorkTypeTxt,
                    notEmpty:true,
                    style : {"width":"100px","height":"30px","color":"#999999","border-radius":"1px","box-shadow": "0px 0px 1px #CCC"}
                },
                defaultWorkLevel: {
                    text: this.lp.defaultWorkLevel + ":", name:"defaultWorkLevel", type: "select",selectValue : this.lp.defaultWorkTypeValue,
                    notEmpty:true,
                    style : {"width":"200px","height":"30px","color":"#999999","border-radius":"1px","box-shadow": "0px 0px 1px #CCC"}
                },
                reportAuditLeader: {
                    text: this.lp.reportAuditLeader + ":", name:"reportAuditLeaderIdentity",
                    tType:"identity",
                    attr:{"readonly":true},
                    notEmpty:false,
                    count: 0,
                    value: this.reportAuditLeader?this.reportAuditLeader:"",
                    style : {"width":"300px"}
                },

                workCompletedLimit: {
                    text: this.lp.workCompletedLimit + ":", name:"defaultCompleteDateLimitStr", type: "text",
                    tType:"date",
                    attr:{"readonly":true},
                    notEmpty:true
                },
                centerWorkMemo: {
                    text: this.lp.centerWorkMemo + ":",
                    name: "description",
                    type: "textarea"
                }
            }
        }, this.app);
        form.load();
        //}.bind(this))

    },
    reloadList : function(){
        this.splitWorkContentArea.destroy();
        this.myWorkContentArea.destroy();

        this.createMyWorkList();
        //this.createSplitWorkList();
        this.creataDeployWorkList();
    },
    createMyWorkList : function(){
        if(this.myWorkContentArea) this.myWorkContentArea.destroy();
        var workContentArea = this.myWorkContentArea = new Element("div.workContentArea", {
            "styles": this.css.workContentArea
        }).inject(this.formTableArea);

        var workContentTitleNode = new Element("div", {
            "styles": this.css.workContentTitleNode,
            "text": this.lp.myWorkInfor
        }).inject(workContentArea);

        var workContentNode = new Element("div", {
            "styles": this.css.workContentNode
        }).inject(workContentArea);
        //this.createSplitWorkList();
        var list = this.myWorkView = new MWF.xApplication.Execution.WorkDeploy.MyWorkView(workContentNode, this.app, this, { templateUrl : this.path+"listItem.json" })
        list.load();

        ////subList权限控制
        //if(this.centerWorkData==undefined || (this.centerWorkData && this.centerWorkData.processStatus==this.lp.statusDraft)){
        //    //拟稿环节不需要显示拆分list
        //    this.splitWorkContentArea.setStyle("display","none");
        //}else if(this.centerWorkData.deployerIdentity == this.app.identity){
        //    //不是拟稿状态，如果当前身份等于中心工作身份 1、如果具体工作没有当前身份负责的工作，不需要显示拆分list 2、如果有显示拆分list
        //    var subListFlag = false;
        //    this.myWorkView.myWorkJson.data.each(function(d){
        //        if(d.subWrapOutOkrWorkBaseInfos){
        //            d.subWrapOutOkrWorkBaseInfos.each(function(d1){
        //                if(d1.deployerIdentity == this.app.identity){
        //                    subListFlag = true
        //                }
        //            }.bind(this))
        //        }
        //    }.bind(this))
        //    if(!subListFlag){
        //        //this.splitWorkContentArea.setStyle("display","none");
        //    }
        //}

    },
    createSplitWorkList : function(){

        var workContentArea = this.splitWorkContentArea = new Element("div.splitWorkContentArea", {
            "styles": this.css.workContentArea
        }).inject(this.formTableArea);


        var workContentTitleNode = new Element("div", {
            "styles": this.css.workContentTitleNode,
            "text": this.lp.deployWorkInfor
        }).inject(workContentArea);

        this.subWorkContentNode = new Element("div.subWorkContentNode", {
            "styles": this.css.workContentNode
        }).inject(workContentArea);
        //var list = new MWF.xApplication.Execution.CenterWorkDeployer.MyWorkView(workContentNode, this.app, this, { templateUrl : this.path+"listItem.json" })
        //list.load();

    },

    creataDeployWorkList : function(){
        var workContentArea = this.deployWorkContentArea = new Element("div.deployWorkContentArea", {
            "styles": this.css.workContentArea
        }).inject(this.formTableArea);


        var workContentTitleNode = new Element("div", {
            "styles": this.css.workContentTitleNode,
            "text": "hidden"
        }).inject(workContentArea);

        this.deployWorkContentNode = new Element("div.deployWorkContentNode", {
            "styles": this.css.workContentNode
        }).inject(workContentArea);
        this.createSplitWorkList();
        var list = this.myDeployView = new MWF.xApplication.Execution.WorkDeploy.MyDeployView(this.deployWorkContentNode, this.app, this, { templateUrl : this.path+"listItem_deploy.json" })
        list.load();

        //this.deployWorkContentArea.setStyle("display","none")
        this.deployWorkContentArea.destroy();
    },

    _createBottomContent: function () {
        if(this.formBottomNode) this.formBottomNode.empty();
        this.newWorkActionNode = new Element("div.newWorkActionNode", {
            "styles": this.css.formActionNode,
            "text": this.lp.newWork
        }).inject(this.formBottomNode);

        this.newWorkActionNode.addEvents({
            "click":function(){
                var r = this.centerForm.getResult(true,",",true,false,true);
                if( !r ){
                    return
                }
                if(this.centerWorkData==undefined || (this.centerWorkData && this.centerWorkData.processStatus==this.lp.statusDraft)){
                    this.saveMainTask( r, function( json ){
                        if( json.type && json.type === "error"){
                            this.app.notice(json.userMessage, "error")
                        }else{
                            this.getMainTask(json.userMessage, function( data ){
                                //this.reloadMainTask( data.data )
                                this.centerWorkInforData = data.data;
                                this.centerWorkData = data.data;

                                this.loadCenterWorkInfor(data.data)
                                //this.reloadContent(this.centerWorkInforData)
                                this.openWorkForm( data.data );
                                this._createBottomContent();
                            }.bind(this))
                        }
                    }.bind(this))
                }else{
                    this.openWorkForm( this.centerWorkData );
                }

            }.bind(this)
        })

        //只有拟稿状态才有删除按钮
        if(this.centerWorkData && this.centerWorkData.processStatus && this.centerWorkData.processStatus == this.lp.statusDraft){
            this.deleteActionNode = new Element("div.deleteActionNode",{
                "styles" : this.css.formActionNode,
                "text" : this.lp.remove
            }).inject(this.formBottomNode);
            var _self = this;
            this.deleteActionNode.addEvents({
                "click": function(e){
                    this.app.confirm("warn",e,this.lp.submitWarn.warnTitle,this.lp.submitWarn.warnContent,300,120,function(){
                        _self.actions.deleteCenterWork(_self.centerWorkData.id,function(json){
                            _self.app.notice(json.userMessage,"success");
                            _self.close();
                            _self.fireEvent("reloadView",{"tab":"drafter"});
                        }.bind(_self),function(xhr,text,error){
                            var errorText = error;
                            if (xhr) errorMessage = xhr.responseText;
                            var e = JSON.parse(errorMessage);
                            if(e.userMessage){
                                _self.app.notice( e.userMessage,"error");
                            }else{
                                _self.app.notice( errorText,"error");
                            }
                        }.bind(_self),false)
                        this.close();
                    },function(){
                        this.close();
                    })


                }.bind(this)
            })
        }



        this.importActionNode = new Element("div.newWorkActionNode",{
            "styles": this.css.formActionNode,
            "text" :this.lp.import
        }).inject(this.formBottomNode);
        this.importActionNode.addEvents({
            "click":function(){
                this.upload();
            }.bind(this)
        })




        this.deployActionNode = new Element("div.formActionNode", {
            "styles": this.css.formActionNode,
            "text": this.lp.goonDeploy
        }).inject(this.formBottomNode);

        this.deployActionNode.addEvent("click", function (e) {
            this.deploy(e);
        }.bind(this));

        if (this.centerWorkData && this.centerWorkData.status != this.lp.statuArchive) {
            if(this.archiveSwitch){
                this.archiveActionNode = new Element("div.formActionNode", {
                    "styles": this.css.formActionNode,
                    "text": this.lp.actionArchive
                }).inject(this.formBottomNode);
                this.archiveActionNode.addEvent("click", function (e) {
                    this.archive(e)
                }.bind(this));
            }
        }



        this.cancelActionNode = new Element("div.formActionNode", {
            "styles": this.css.formActionNode,
            "text": this.lp.close
        }).inject(this.formBottomNode);
        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

        //底部按钮控制
        //拟稿环节有创建按钮，其他环节只有拟稿人才有创建按钮
        //拟稿环节有导入操作，其他环节只有拟稿人才有导入
        if(this.centerWorkData==undefined || (this.centerWorkData && this.centerWorkData.processStatus==this.lp.statusDraft) || this.centerWorkData.deployerIdentity == this.app.identity ){

        }else{
            if(this.newWorkActionNode) {
                this.newWorkActionNode.destroy();
            }
            if(this.importActionNode){
                this.importActionNode.destroy();
            }
        }
        //如果已归档 所有按钮删除
        if (this.centerWorkData && this.centerWorkData.status == this.lp.statuArchive) {
            if(this.newWorkActionNode)this.newWorkActionNode.destroy();
            if(this.deleteActionNode)this.deleteActionNode.destroy();
            if(this.importActionNode)this.importActionNode.destroy();
            if(this.deployActionNode)this.deployActionNode.destroy();
            if(this.archiveActionNode)this.archiveActionNode.destroy();
        }

    },
    saveMainTask: function(data, callback){
        this.app.restActions.saveMainTask( data,
            function(json){
                if( callback )callback(json);
            }.bind(this),
            function(xhr,text,error){
                var errorText = error;
                if (xhr) errorMessage = xhr.responseText;
                var e = JSON.parse(errorMessage);
                if(e.userMessage){
                    this.app.notice( e.userMessage,"error");
                }else{
                    this.app.notice( errorText,"error");
                }
            }.bind(this),
            false
        );
    },
    getMainTask: function(id, callback){
        this.app.restActions.getMainTask( id, function(json){
            if( callback )callback(json);
        }.bind(this),null,false);
    },
    openWorkForm : function( data ){

        this.myWorkView._create( data );

    },
    //loadBaseWork:function(){
    //    //alert("id="+this.centerWorkId);
    //
    //    this.subTaskContentDiv.empty();
    //
    //    //if(this.centerWorkId && this.centerWorkId!=""){
    //
    //    var list = new MWF.xApplication.Execution.WorkDeploy.MyWorkView(workContentNode, this.app, this, { templateUrl : this.path+"listItem.json" })
    //    list.load();
    //
    //
    //},
    archive:function(){
        this.actions.archiveMainTask(this.centerWorkData.id,function(){
            this.app.notice(this.lp.statuArchive,"success");
            this.close();
        }.bind(this),function(xhr,text,error){
            var errorText = error;
            if (xhr) errorMessage = xhr.responseText;
            var e = JSON.parse(errorMessage);
            if(e.userMessage){
                this.app.notice( e.userMessage,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }.bind(this),false)
    },
    deploy: function(){
        if(this.centerWorkData==undefined){
            this.app.notice(this.lp.warnIng.baseWorkNotEmpty, "ok");
            return false;
        }

        if(this.centerWorkData.processStatus==this.lp.statusDraft){
            var dataLen = 0
            this.actions.getUserDeployBaseWork( this.centerWorkData.id, function(json){
                if(json.data){
                   dataLen = json.data.length
                }

            }.bind(this),null,false);
            if(dataLen==0){
                this.app.notice(this.lp.warnIng.baseWorkNotEmpty, "ok");
                return false;
            }
        }


        var ids = [];

        this.actions.getUserDeployBaseWork( this.centerWorkId, function(json){
            if(this.centerWorkInforData){
                if(this.centerWorkInforData.processStatus == this.lp.statusDraft){  //中心工作草稿环节，
                    json.data.each(function(d){
                        if(d.workProcessStatus == this.lp.statusDraft){
                            ids.push(d.id)
                        }
                    }.bind(this))
                }else{  //其他环节，其他环节也有可能拟稿人追加
                    json.data.each(function( d ){
                        if( d.subWrapOutOkrWorkBaseInfos ){
                            d.subWrapOutOkrWorkBaseInfos.each(function( infor ){
                                if( infor.workProcessStatus == this.lp.statusDraft ){
                                    ids.push( infor.id )
                                }
                            }.bind(this))
                        }
                        if(d.workProcessStatus == this.lp.statusDraft){
                            ids.push(d.id)
                        }
                    }.bind(this))
                }
            }

            //if( ids.length > 0 ){
                var data = {};
                data.centerId = this.centerWorkId;
                data.workIds = ids;
                this.createShade("正在部署，请稍等...");

                this.actions.deployBaseWork( data, function( j ){
                    if(j.type && j.type=="success"){
                        this.destroyShade();
                        this.app.notice(this.lp.deployeSuccess, "ok");
                        //this.reloadContent();
                        this.close();
                        this.fireEvent("reloadView");
                        //if(this.app.workTask && this.app.workTask.contentDiv){ alert("load worktask")
                        //    this.app.workTask.contentDiv.destroy()
                        //    this.app.workTask.createContentDiv();
                        //}
                        //if(this.app){ alert("load main")
                        //    this.app.createTodoList();
                        //}

                    }else{
                        this.app.notice(j.data.message, "error")
                        this.destroyShade();
                    }

                }.bind(this),function(xhr,text,error){
                    var errorText = error;
                    if (xhr) errorMessage = xhr.responseText;
                    var e = JSON.parse(errorMessage);
                    if(e.userMessage){
                        this.app.notice( e.userMessage,"error");
                    }else{
                        this.app.notice( errorText,"error");
                    }
                    this.destroyShade();
                }.bind(this),true);
            //}else{
            //    this.app.notice(this.lp.noWordNeedDeployed, "ok");
            //}
            //this.destroyShade();
        }.bind(this),function(xhr,text,error){}.bind(this),false);
    }
})

MWF.xApplication.Execution.WorkDeploy.MyWorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkDeploy.MyWorkDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        //var tmpArr = [];
        if(this.explorer.centerWorkId){
            this.actions.getUserProcessBaseWork( this.explorer.centerWorkId, function(json){ //alert("process=length="+json.data.length+"="+JSON.stringify(json))
                if(json.data.length==0){
                    this.explorer.myWorkContentArea.destroy();
                }
                //this.myWorkJson = json
                if (callback) callback(json)
            }.bind(this),null,false);
        }

    },
    _removeDocument: function(documentData, all){
        this.actions.deleteBaseWork(documentData.id, function(json){
            if(json.type && json.type=="success"){
                this.app.notice(this.app.lp.deleteDocumentOK, "success");
                this.reload();
                this.explorer.contentChanged = true;
            }
        }.bind(this),function(xhr,text,error){
            var errorText = error;
            if (xhr) errorMessage = xhr.responseText;
            var e = JSON.parse(errorMessage);
            if(e.userMessage){
                this.app.notice( e.userMessage,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }.bind(this));



    },
    _create: function(data){
        MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
            this.workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,{"centerWorkId": data.id || this.options.centerWorkId },{
                "isNew": true,
                "isEdited": false,
                "actionStatus":"save",
                "onPostSave" : function(){
                    this.explorer.contentChanged = true;
                }.bind(this)
            });
            //alert("cccc="+this.centerWorkId)

            this.workform.load();
        }.bind(this));
    },
    _openDocument: function( documentData ){
        this.workForm = new MWF.xApplication.Execution.WorkForm(this, this.actions, documentData, {
            "isNew": false,
            "isEdited": false
        })
        this.workForm.load();
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

})

MWF.xApplication.Execution.WorkDeploy.MyWorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    action_comfirm : function(){

    },
    action_edit : function(){
        this.workForm = new MWF.xApplication.Execution.WorkDeploy.WorkForm(this.explorer, this.actions, this.data, {
            "isNew": false,
            "isEdited": true,
            "actionStatus":"save",
            "onPostSave" : function(){
                this.view.explorer.contentChanged = true;
            }.bind(this)
        })
        this.workForm.load();
    },
    action_remove : function(){
        this.view.app.restActions.deleteBaseWork( this.data.id, function(json){
            if(json.type && json.type=="success"){
                this.view.app.notice(this.app.lp.deleteDocumentOK, "success");
                this.view.reload();
                this.view.explorer.contentChanged = true;
            }

        }.bind(this),function(xhr,text,error){
            var errorText = error;
            if (xhr) errorMessage = xhr.responseText;
            var e = JSON.parse(errorMessage);
            if(e.userMessage){
                this.app.notice( e.userMessage,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }.bind(this));
    },
    action_split:function(){
        var data = {
            title : this.data.title,
            centerId : this.data.centerId,
            //centerTitle: this.data.centerTitle,
            parentWorkId : this.data.id,
            //parentWorkTitle : this.data.title,
            workType : this.data.workType,
            workLevel : this.data.workLevel,
            completeDateLimitStr : this.data.completeDateLimitStr,
            completeDateLimit : this.data.completeDateLimit,
            reportCycle: this.data.reportCycle,
            reportDayInCycle: this.data.reportDayInCycle
        }
        if(this.data.id){
            this.actions.getBaseWorkDetails(this.data.id, function (json) {
                data.workSplitAndDescription = json.data.workDetail
                data.specificActionInitiatives = json.data.progressAction
                data.cityCompanyDuty = json.data.dutyDescription
                data.milestoneMark = json.data.landmarkDescription
                data.importantMatters = json.data.majorIssuesDescription
            }.bind(this),null,false)
        }




        var workForm = new MWF.xApplication.Execution.WorkDeploy.WorkForm(this.view.explorer, this.actions, data, {
            "isNew": true,
            "isEdited": false,
            "actionStatus":"save",
            "onPostSave" : function(){
                this.view.explorer.contentChanged = true;
            }.bind(this)
        })
        workForm.load();
    },
    action_appoint:function(){

        var flag = true;
        this.view.actions.getBaseWorksByParentId(this.data.id,function(json){
            if(json.data){
                json.data.each(function(n,i){
                    if(n.workProcessStatus && n.workProcessStatus == this.view.lp.statusDraft){
                        flag = false
                    }
                }.bind(this))
            }
        }.bind(this),function(){
            flag = false
        }.bind(this),false)

        if(!flag){
            this.view.app.notice(this.view.explorer.lp.cannotAppoint,"error")
            return false
        }

        var data = {
            workId : this.data.id
        };
        var appointForm =  new MWF.xApplication.Execution.WorkDeploy.Appoint(this.view.app,this.view.app.restActions,data,this.view.css,{
            "ieEdited": true,
            "onReloadView" : function( data ){
                //判断如果只有一个可以拆解的 则关掉本窗口，刷新父窗口，如果还有则刷新本窗口
                //alret("刷新或关闭");

                this.checkFlag = false;
                this.view.explorer.actions.getUserProcessBaseWork( this.data.centerId, function(json){
                    this.checkFlag = false;
                    json.data.each(function(o,i){
                        if(o.responsibilityIdentity == this.view.app.identity){
                            this.checkFlag = true;
                        }
                    }.bind(this))
                }.bind(this),null,false);
                if(this.checkFlag){
                    this.view.explorer.reloadList()
                }else{
                    this.view.explorer.close();
                    this.view.explorer.fireEvent("reloadView")
                }
            }.bind(this)
        });
        appointForm.load();
    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if(itemNode.getElements("div[item='title']").length>0){
            this.view.actions.getBaseWorksByParentId(itemData.id,function(json){
                if(json.data && json.data.length>0){
                    itemNode.getElements("div[item='title']").setStyle("color","#ec6a1a");
                    //itemNode.getElements("div[item='title']").set("title",this.view.lp.splitReady);
                }
            }.bind(this))
        }

        if(itemNode.getElements("div[name='appointDiv']")){
            if(itemData.okrWorkAuthorizeRecord){
                itemNode.getElements("div[name='appointDiv']").setStyle("display","")
            }
        }
        if(itemNode.getElements("div[styles='documentSubject']")){
            itemNode.getElements("div[styles='documentSubject']").set("title",itemData.shortWorkDetail)
        }
    },
    editActionReturn : function(d){
        if(d.status == this.lp.statuArchive){
            return false;
        }
        if(d.workProcessStatus == this.lp.statusDraft){
            return true;
        }
        return false;
    },
    appointActionReturn : function(d){
        if(d.status == this.lp.statuArchive){
            return false;
        }
        var flag = false;
        if(this.view.explorer.appointSwitch && this.view.explorer.appointSwitch.toUpperCase() == "OPEN"){
            flag = true;
            this.view.actions.getBaseWorksByParentId(d.id,function(json){
                if(json.data){
                    json.data.each(function(n,i){
                        if(n.workProcessStatus && n.workProcessStatus == this.view.lp.statusDraft){
                            flag = false
                        }
                    }.bind(this))
                }
            }.bind(this),function(){
                flag = false
            }.bind(this),false)
            if(true){
                if(d.responsibilityIdentity != this.view.app.identity){
                    flag = false;
                }
            }
        }else{
            flag = false;
        }

        return flag
    },
    removeActionReturn : function(d){
        if(d.status == this.lp.statuArchive){
            return false;
        }
        if(d.workProcessStatus == this.lp.statusDraft){
            return true;
        }
        return false;
    },
    splitActionReturn : function(d){
        if(d.status == this.lp.statuArchive){
            return false;
        }
        if(this.view.explorer.splitSwitch && this.view.explorer.splitSwitch.toUpperCase() == "OPEN"){
            //如果不等于草稿并且身份相同 显示拆分
            if(d.workProcessStatus != this.lp.statusDraft && d.responsibilityIdentity == this.app.identity){
                return true;
            }
        }

        return false;
    }

})




MWF.xApplication.Execution.WorkDeploy.SubWorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkDeploy.SubWorkDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        var json = {
            data : this.parentWorkData.subWrapOutOkrWorkBaseInfos,
            count : this.parentWorkData.subWrapOutOkrWorkBaseInfos.length,
            size : this.parentWorkData.subWrapOutOkrWorkBaseInfos.length
        }
        if (callback) callback(json);
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteBaseWork(documentData.id, function(json){

            if(json.type && json.type=="success"){
                this.app.notice(json.userMessage, "success");
            }
            //alert("subview")
            this.explorer.reloadContent();
            this.explorer.contentChanged = true;

        }.bind(this),function(xhr,text,error){
            var errorText = error;
            if (xhr) errorMessage = xhr.responseText;
            var e = JSON.parse(errorMessage);
            if(e.userMessage){
                this.app.notice( e.userMessage,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        this.workForm = new MWF.xApplication.Execution.WorkDeploy.WorkForm(this, this.actions, documentData, {
            "isNew": false,
            "isEdited": false
        })
        this.workForm.load();
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

})

MWF.xApplication.Execution.WorkDeploy.SubWorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    action_edit : function(){
        this.workForm = new MWF.xApplication.Execution.WorkDeploy.WorkForm(this.view.explorer, this.actions, this.data, {
            "isNew": false,
            "isEdited": true,
            "actionStatus":"save",
            "onPostSave" : function(){
                this.view.explorer.contentChanged = true;
            }.bind(this)
        })
        this.workForm.load();
    },
    _queryCreateDocumentNode:function( itemData ){
        if( !this.view.titleLoaded ){
            var titleNode = new Element("tr").inject(this.view.viewNode)
            tdNode = new Element("td",{
                text : this.view.parentWorkData.title,
                colspan : this.view.template.items.length,
                styles : this.css.titleTdNode
            }).inject(titleNode)
            this.view.titleLoaded = true;
        }
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if(itemNode.getElements("div[styles='documentSubject']")){
            itemNode.getElements("div[styles='documentSubject']").set("title",itemData.shortWorkDetail)
        }
    },
    editActionReturn: function(d){
        if(d.status == this.lp.statuArchive){
            return false;
        }
        //alert(JSON.stringify(d))
        if(d.workProcessStatus == this.lp.statusDraft){
            return true;
        }
        return false;
    },
    removeActionReturn: function(d){
        if(d.status == this.lp.statuArchive){
            return false;
        }

        return true;
    }
})




MWF.xApplication.Execution.WorkDeploy.MyDeployView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkDeploy.MyDeployDocument(this.viewNode, data, this.explorer, this);
    },



    _getCurrentPageData: function(callback, count){
        //var json = {
        //    data : this.parentWorkData.subWrapOutOkrWorkBaseInfos,
        //    count : this.parentWorkData.subWrapOutOkrWorkBaseInfos.length,
        //    size : this.parentWorkData.subWrapOutOkrWorkBaseInfos.length
        //}
        //if (callback) callback(json);
        this.actions.getUserDeployBaseWork( this.explorer.centerWorkId, function(json){ //alert("deploy="+JSON.stringify(json))
            var formatJson = {};
            var formatData = [];
            var centerData = [];
            var centerJson = {};


            if(json.type=="success"){
                if(json.data){
                    json.data.each(function(d){
                        if(d.subWrapOutOkrWorkBaseInfos){
                            //如果有subWrapOutOkrWorkBaseInfos对象 说明是层次关系
                            formatData.push(d)
                        }else{
                            //把中心工作的头放到对象中，本身对象作为subWrapOutOkrWorkBaseInfos的值
                            centerData.push(d)
                        }
                    }.bind(this))
                }
            }
            if(centerData.length>0){
                if(this.explorer.centerWorkData){
                    this.explorer.centerWorkData.subWrapOutOkrWorkBaseInfos = centerData;
                }else{

                    this.explorer.getMainTask(this.explorer.centerWorkId, function( json ){
                        data = json.data;
                        this.explorer.centerWorkData = json.data;
                    }.bind(this),null,false);

                    this.explorer.centerWorkData.subWrapOutOkrWorkBaseInfos = centerData;
                }

                formatData.push(this.explorer.centerWorkData)
            }
            formatJson.data = formatData;
           if(formatJson.data.length==0){
               this.explorer.splitWorkContentArea.destroy();
           }
            //this.myWorkJson = json
            if (callback) callback(formatJson)
        }.bind(this),null,false);

    },
    _removeDocument: function(documentData, all){
        this.actions.deleteBaseWork(documentData.id, function(json){

            this.explorer.reloadContent();
            this.explorer.contentChanged = true;
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(data){

    },
    _openDocument: function( documentData ){
        this.workForm = new MWF.xApplication.Execution.WorkDeploy.WorkForm(this, this.actions, documentData, {
            "isNew": false,
            "isEdited": false
        })
        this.workForm.load();
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

})

MWF.xApplication.Execution.WorkDeploy.MyDeployDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    action_edit : function(){
        this.workForm = new MWF.xApplication.Execution.WorkDeploy.WorkForm(this.view.explorer, this.actions, this.data, {
            "isNew": false,
            "isEdited": true,
            "actionStatus":"save",
            "onPostSave" : function(){
                this.view.explorer.contentChanged = true;
            }.bind(this)
        })
        this.workForm.load();
    },
    _queryCreateDocumentNode:function( itemData ){
        //if( !this.view.titleLoaded ){
        //    var titleNode = new Element("tr").inject(this.view.viewNode)
        //    tdNode = new Element("td",{
        //        //text : this.view.parentWorkData.title,
        //        //colspan : this.view.template.items.length,
        //        //styles : this.css.titleTdNode
        //        text :"biaottttttttttt"
        //    }).inject(titleNode)
        //    this.view.titleLoaded = true;
        //}
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if( itemData.subWrapOutOkrWorkBaseInfos ){

            var list = new MWF.xApplication.Execution.WorkDeploy.SubWorkView( this.view.explorer.subWorkContentNode,
                this.view.explorer.app,
                this.view.explorer, {
                    templateUrl : this.view.explorer.path+"listItem_sub.json",
                    hasHead : !this.view.subViewHeadLoaded //头部只建一次
                })
            list.parentWorkData = itemData;
            list.load();
            this.view.subViewHeadLoaded = true;
        }
        if(itemNode.getElements("div[styles='documentSubject']")){
            itemNode.getElements("div[styles='documentSubject']").set("title",itemData.shortWorkDetail)
        }
    },
    editActionReturn: function(d){
        //alert(JSON.stringify(d))
        if(d.workProcessStatus == this.lp.statusDraft){
            return true;
        }
        return false;
    },

    removeActionReturn : function(d){
        if(d.status == this.lp.statuArchive){
            return false;
        }
        if(d.workProcessStatus == this.lp.statusDraft){
            return true;
        }
        return false;
    },
    splitActionReturn : function(d){
        if(d.status == this.lp.statuArchive){
            return false;
        }
        //如果不等于草稿并且身份相同 显示拆分
        if(d.workProcessStatus != this.lp.statusDraft && d.responsibilityIdentity == this.app.identity){
            return true;
        }

        return false;
    },
    appointActionReturn : function(d){
        if(d.status == this.lp.statuArchive){
            return false;
        }
        return true;
    }
})


MWF.xApplication.Execution.WorkDeploy.WorkForm = new Class({
    Extends: MWF.xApplication.Execution.WorkForm,
    _ok: function (data, callback) {
        data.title = data.workDetail;
        if(this.options.isNew){
            data.deployerName = this.app.user;
            data.creatorName = this.app.user;
        }
        this.app.restActions.saveTask(data,function(json){

            if(json.type && json.type=="success"){
                this.app.notice(this.lp.submitSuccess, "ok");
                this.explorer.reloadContent();
                this.close();
            }
            this.fireEvent("postSave", json);
        }.bind(this),function(xhr,text,error){
            var errorText = error;
            if (xhr) errorMessage = xhr.responseText;
            var e = JSON.parse(errorMessage);
            if(e.userMessage){
                this.app.notice( e.userMessage,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }.bind(this));
    }
})

MWF.xApplication.Execution.WorkDeploy.Appoint = new Class({
    Extends: MWF.xApplication.Template.Explorer.PopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "500",
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": false,
        "closeAction": true,
        "closeText" : "",
        "needLogout" : false,
        "isNew": true
    },
    initialize: function (app, actions, data, css, options) {
        this.setOptions(options);
        this.app = app;
        this.actions = this.app.restActions;
        this.css = css;

        //this.options.title = this.app.lp.idenitySelectTitle;
        //
        //this.identities = identities;
        this.data = data || {};
        this.actions = actions;
    },
    load: function () {
        this.create();
    },

    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopIconNode = new Element("div.formTopIconNode", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode)

            this.formTopTextNode = new Element("div.formTopTextNode", {
                "styles": this.css.formTopTextNode,
                "text": this.app.lp.workTask.appoint.appointTitle
            }).inject(this.formTopNode)

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            this.formTopContentNode = new Element("div.formTopContentNode", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode)

            //this._createTopContent();

        }

    },
    _createTableContent: function () {
        var table = new Element("table",{"width":"100%",border:"0",cellpadding:"5",cellspacing:"0"}).inject(this.formTableArea);
        table.setStyles({"margin-top":"40px"})
        var tr = new Element("tr").inject(table);
        var td = new Element("td",{
            text : this.app.lp.workTask.appoint.appointFor,
            valign:"middle",
            width:"20%"
        }).inject(tr);
        td = new Element("td",{width:"80%"}).inject(tr);
        this.appointPerson = new Element("input",{
            "readonly": true
        }).inject(td);
        this.appointPerson.setStyles({"width":"90%","height":"20px"})
        this.appointPerson.addEvents({
            "click":function(){
                this.selectPerson(this.appointPerson,"identity",1)
            }.bind(this)
        })
        tr = new Element("tr").inject(table);
        td = new Element("td",{
            "text" : this.app.lp.workTask.appoint.appointOpinion,
            valign:"middle"
        }).inject(tr);


        td = new Element("td").inject(tr);
        this.appointOpinion = new Element("textarea").inject(td)
        this.appointOpinion.setStyles({"width":"90%","height":"50px"})



    },

    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.app.lp.workTask.appoint.appointCancel
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close();
        }.bind(this));

        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css.formOkActionNode,
            "text": this.app.lp.workTask.appoint.appointOK
        }).inject(this.formBottomNode);

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));
    },
    ok:function(){

        if(this.appointPerson.get("value")==""){
            this.app.notice(this.app.lp.workTask.appoint.personEmpty,"error")
            return false;
        }
        if(this.appointOpinion.get("value")==""){
            this.app.notice(this.app.lp.workTask.appoint.opinionEmpty,"error")
            return false;
        }
        var submitData = {
            workId : this.data.workId,
            undertakerIdentity : this.appointPerson.get("value"),
            authorizeOpinion : this.appointOpinion.get("value")
        }
        this.actions.appointBaseWork(submitData,function(json){
            this.close();
            this.fireEvent("reloadView");
        }.bind(this),function(xhr,text,error){
            var errorText = error;
            if (xhr) errorMessage = xhr.responseText;
            var e = JSON.parse(errorMessage);
            if(e.userMessage){
                this.app.notice( e.userMessage,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }.bind(this),false)
    },
    selectPerson: function( item, type,count ) {
        MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
        this.fireEvent("querySelect", this);
        var value = item.get("value").split(this.valSeparator);
        var options = {
            "type": type,
            "title": this.app.lp.workTask.appoint.appointTitle,
            "count": count,
            "names": value || [],
            "onComplete": function (items) {
                var arr = [];
                items.each(function (item) {
                    arr.push(item.data.name);
                }.bind(this));
                item.set("value", arr.join(","));
            }.bind(this)
        };

        var selector = new MWF.OrgSelector(this.app.content, options);
    }

});
