MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Execution", "WorkForm", null, false);

MWF.xApplication.Execution.WorkDeploy = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
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

        this.getCenterWorkInfo();

        if (this.options.isNew) {
            this.create();
        } else if (this.options.isEdited) {
            this.edit();
        } else {
            this.open();
        }
    },
    getCenterWorkInfo:function(centerId){
        var id = "(0)";
        if(arguments.length==1){
            id = centerId;
        }else{
            if(this.data.id){
                id = this.data.id;
            }
        }

        this.actions.getCenterWorkInfo(id,function(json){
            if(json.type = "success"){
                this.centerWorkData = json.data;
                this.centerWorkId = this.centerWorkData.id
            }
        }.bind(this),function(xhr,error,text){
            this.showErrorMessage(xhr,error,text)
        }.bind(this),false)
    },
    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopIconNode = new Element("div", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.options.title
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close();
                }.bind(this))
            }

            this.formTopContentNode = new Element("div", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode);

            this._createTopContent();
        }
    },
    _createTopContent: function () {
        if(this.formTopContentNode) this.formTopContentNode.empty();
        var html = "<span styles='formTopContentTitle' lable='drafter'></span>" +
            "    <span styles='formTopContentValue' item='drafter'></span>" +
            "<span styles='formTopContentTitle' lable='draftDepartment'></span>" +
            "    <span styles='formTopContentValue' item='draftDepartment'></span>" +
            "<span styles='formTopContentTitle' lable='draftDate'></span>" +
            "    <span styles='formTopContentValue' item='draftDate'></span>";

        this.formTopContentNode.set("html", html);


        var form = new MForm(this.formTopContentNode, this.centerWorkData, {
            isEdited: false,
            itemTemplate: {
                drafter: {
                    text: this.lp.drafter + ":",
                    value:this.centerWorkData.creatorName.split("@")[0],
                    type: "innertext"
                },
                draftDepartment: {
                    text: this.lp.draftDepartment + ":",
                    value:this.centerWorkData.creatorUnitName.split("@")[0],
                    type: "innertext"
                },
                draftDate: {text: this.lp.draftDate + ":",name:"createTime", type: "innertext"}
            }
        }, this.app, this.css);
        form.load();
    },
    reloadTableContent:function(id){
        if(arguments.length==0){
            if(this.centerWorkData){
                id = this.centerWorkData.id
            }else if(this.data.id){
                id = this.data.id
            }else if(this.options.centerWorkId){
                id = this.options.centerWorkId
            }
        }
        this.getCenterWorkInfo(id);
        this._createTopContent();
        this._createTableContent(this.centerWorkData);
        this._createBottomContent();
    },
    _createTableContent: function () {
        if(this.formTableArea) this.formTableArea.empty();
        //alert(this.centerWorkData)
        this.createCenterWorkInfor(this.centerWorkData);
        this.createImportContent();
        this.createMyWorkList();
        this.creataMyDeployWorkList();

    },
    createCenterWorkInfor: function(data) {
        this.centerWorkContentArea = new Element("div.centerWorkContentArea", {
            "styles": this.css.workContentArea
        }).inject(this.formTableArea);

        var workContentTitleNode = new Element("div.workContentTitleNode", {
            "styles": this.css.workContentTitleNode,
            "text": this.lp.centerWorkInfor
        }).inject(this.centerWorkContentArea);

        this.centerWorkContentNode = new Element("div.centerWorkContentNode", {
            "styles": this.css.workContentNode
        }).inject(this.centerWorkContentArea);

        this.loadCenterWorkInfor(data)

    },
    loadCenterWorkInfor: function(data){
        this.centerWorkContentNode.empty();
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='centerWorkInforTable'>" +
            "<tr><td styles='centerWorkInforTitle' lable='centerWorkTitle'></td>" +
            "    <td styles='centerWorkInforValue' item='centerWorkTitle'></td></tr>" +
            "<tr><td colspan='2'>" +
            "   <div styles='centerWorkInforTitleDiv' lable='reportAuditLeader'></div>" +
            "    <div styles='centerWorkInforValueDiv' item='reportAuditLeader'></div>" +
            "   <div styles='centerWorkInforTitleDiv' lable='defaultWorkType'></div>" +
            "    <div styles='centerWorkInforValueDiv' item='defaultWorkType'></div>" +
            "   <div styles='centerWorkInforTitleDiv' lable='workCompletedLimit'></div>" +
            "    <div styles='centerWorkInforValueDiv' item='workCompletedLimit'></div>" +
            "</td></tr>" +
            "<tr><td styles='centerWorkInforTitle' lable='centerWorkMemo'></td>" +
            "    <td styles='centerWorkInforValue' item='centerWorkMemo'></td></tr>" +
            "</table>";
        this.centerWorkContentNode.set("html", html);

        var resultWorkType = [];
        var resultWorkTypeTxt = "";
        if(data.workTypes){
            data.workTypes.each(function(d,i){
                if(d.workTypeName) resultWorkType.push(d.workTypeName)
            })
        }

        resultWorkTypeTxt = resultWorkType.join(",");
        if(resultWorkType.length>0){
            resultWorkTypeTxt = ","+resultWorkTypeTxt
        }

        var form = this.centerForm = new MForm(this.centerWorkContentNode, data, {
            isEdited: this.isEdited || this.isNew,

            itemTemplate: {
                centerWorkTitle: {
                    text: this.lp.centerWorkTitle + ":", name : "title", type: "text",
                    notEmpty:true
                },
                defaultWorkType: {
                    text: this.lp.defaultWorkType + ":", name : "defaultWorkType", type: "select",
                    selectValue : resultWorkTypeTxt,
                    selectText : resultWorkTypeTxt,
                    notEmpty:true,
                    style : {"width":"100px","height":"30px","border-radius":"1px"}
                },
                defaultWorkLevel: {
                    text: this.lp.defaultWorkLevel + ":", name:"defaultWorkLevel", type: "select",selectValue : this.lp.defaultWorkTypeValue,
                    notEmpty:true,
                    style : {"width":"200px","height":"30px","color":"#999999","border-radius":"1px","box-shadow": "0px 0px 1px #CCC"}
                },
                reportAuditLeader: {
                    text: this.lp.reportAuditLeader + ":",
                    name:"reportAuditLeaderIdentityList",
                    type:"org",
                    orgType:"identity",
                    attr:{"readonly":true},
                    notEmpty:false,
                    count: 0,
                    value: this.reportAuditLeaderList?this.reportAuditLeaderList.join(","):"",
                    style : {"width":"400px"}
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
    },

    createImportContent: function(){
        if(this.centerWorkData && this.centerWorkData.operation){
            if(this.centerWorkData.operation.indexOf("IMPORTWORK")>-1){
                this.importDiv = new Element("div.importDiv",{
                    "styles" : this.css.importDiv
                }).inject(this.formTableArea);
                this.importTemplateDiv = new Element("div.importTemplateDiv",{
                    "styles": this.css.importTemplateDiv,
                    "text":this.lp.importTemplate
                }).inject(this.importDiv);
                this.importTemplateDiv.addEvents({
                    "click":function(){
                        window.open("/x_component_Execution/baseWork.xls")
                    }.bind(this)
                });
                this.importTitleDiv = new Element("div.importTitleDiv",{
                    "styles": this.css.importTitleDiv,
                    "text":this.lp.importTemplateTitle
                }).inject(this.importDiv);
            }
        }
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

        workContentNode = new Element("div", {
            "styles": this.css.workContentNode
        }).inject(workContentArea);
        //this.createSplitWorkList();
        var list = this.myWorkView = new MWF.xApplication.Execution.WorkDeploy.MyWorkView(workContentNode, this.app, this, { templateUrl : this.path+"myWork.json" });
        list.load();
    },
    creataMyDeployWorkList : function(){
        var workContentArea = this.myDeployWorkArea = new Element("div.myDeployWorkArea", {
            "styles": this.css.workContentArea
        }).inject(this.formTableArea);

        var myDeployWorkTitleNode = new Element("div.myDeployWorkTitleNode", {
            "styles": this.css.workContentTitleNode,
            "text": this.lp.deployWorkInfor
        }).inject(workContentArea);


        var list = this.myDeployWorkView = new MWF.xApplication.Execution.WorkDeploy.MyDeployWorkView(workContentArea, this.app, this, { templateUrl : this.path+"myDeployWork.json" });
        list.load();

    },





    //*************************底部按钮及方法**************************************
    _createBottomContent: function () {
        if(this.formBottomNode) this.formBottomNode.empty();

        if(this.centerWorkData && this.centerWorkData.operation){
            this.centerWorkData.operation.each(function(d,i){
                if(d == "CLOSE"){
                    this.closeBotton = new Element("div.closeBotton", {
                        "styles": this.css.formActionNode,
                        "text": this.lp.botton.close
                    }).inject(this.formBottomNode);
                    this.closeBotton.addEvent("click", function (e) {
                        this.closeWork(e);
                    }.bind(this));
                }else if(d == "CREATEWORK"){
                    this.newBotton = new Element("div.newBotton", {
                        "styles": this.css.formActionNode,
                        "text": this.lp.botton.new
                    }).inject(this.formBottomNode);
                    this.newBotton.addEvent("click", function (e) {
                        this.createWork(e);
                    }.bind(this));
                }else if(d == "IMPORTWORK"){
                    this.importBotton = new Element("div.importBotton", {
                        "styles": this.css.formActionNode,
                        "text": this.lp.botton.import
                    }).inject(this.formBottomNode);
                    this.importBotton.addEvent("click", function (e) {
                        this.importWork(e);
                    }.bind(this));
                }else if(d == "DELETE"){
                    this.deleteBotton = new Element("div.deleteBotton", {
                        "styles": this.css.formActionNode,
                        "text": this.lp.botton.delete
                    }).inject(this.formBottomNode);
                    this.deleteBotton.addEvent("click", function (e) {
                        this.deleteWork(e);
                    }.bind(this));
                }else if(d == "DEPLOY"){
                    this.deployBotton = new Element("div.deployBotton", {
                        "styles": this.css.formActionNode,
                        "text": this.lp.botton.deploy
                    }).inject(this.formBottomNode);
                    this.deployBotton.addEvent("click", function (e) {
                        this.deployWork(e);
                    }.bind(this));
                }else if(d == "ARCHIVE"){
                    this.archiveBotton = new Element("div.archiveBotton", {
                        "styles": this.css.formActionNode,
                        "text": this.lp.botton.archive
                    }).inject(this.formBottomNode);
                    this.archiveBotton.addEvent("click", function (e) {
                        this.archiveWork(e);
                    }.bind(this));
                } if(d == "CONFIRM"){
                    this.confirmBotton = new Element("div.confirmBotton", {
                        "styles": this.css.formActionNode,
                        "text": this.lp.botton.confirm
                    }).inject(this.formBottomNode);
                    this.confirmBotton.addEvent("click", function (e) {
                        this.confirmWork(e);
                    }.bind(this));
                }
            }.bind(this))
        }
    },

    closeWork:function(data){
        this.close();
        //this.fireEvent("reloadView", data);
        this.fireEvent("reloadView", {"action":"reload"});
    },
    createWork:function(){
        var r = this.centerForm.getResult(true,",",true,false,true);
        if( !r ){
            return
        }

        if(this.options.isNew || this.options.isEdited){
            this.saveCenterWork(r,function(json){
                if(json.type && json.type == "error"){
                    this.app.notice(json.message, "error")
                }else{
                    if(json.data && json.data.id)this.reloadTableContent(json.data.id);
                    if(this.centerWorkData)this.openWorkForm(this.centerWorkData)
                }
            }.bind(this))
        }else{
            if(this.centerWorkData)this.openWorkForm(this.centerWorkData)
        }
    },
    importWork:function(){

        var centerId;
        var r = this.centerForm.getResult(true,",",true,false,true);
        if( !r ){
            return false;
        }

        //if(r.reportAuditLeaderIdentityList == ""){
        //    r.reportAuditLeaderIdentityList = [];
        //}else{
        //    r.reportAuditLeaderIdentityList = r.reportAuditLeaderIdentityList.split(",");
        //}
        if(this.options.isNew || this.options.isEdited){
            this.saveCenterWork(r,function(json){
                if(json.type && json.type == "error"){
                    this.app.notice(json.message, "error")
                }else{

                    if(this.centerWorkData){
                        centerId = this.centerWorkData.id;
                        this.createUpload(centerId);
                        this.reloadTableContent(centerId)
                    }
                    //if(json.data && json.data.id){
                    //    centerId = json.data.id;alert(centerId)
                    //    this.reloadTableContent(json.data.id)
                    //}

                }
            }.bind(this))
        }else{
            if(this.centerWorkData) centerId = this.centerWorkData.id;
            this.createUpload(centerId);
        }

        //this.createUpload(centerId);


    },
    createUpload:function(centerId){
        if (centerId){
            if(this.uploadFileAreaNode) this.uploadFileAreaNode.destroy();
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

                        this.app.createShade(null,"正在导入，请稍后.....");

                        this.actions.importBaseWork(centerId,formData,file,function(json){
                            this.reloadTableContent(centerId);
                            this.app.destroyShade()
                        }.bind(this),function(xhr,text,error){
                            this.showErrorMessage(xhr,text,error);
                            this.app.destroyShade()
                        }.bind(this));


                        //this.actions.importBaseWork(centerId,function(json){
                        //    this.reloadTableContent(centerId);
                        //    this.app.destroyShade()
                        //}.bind(this),function(xhr,text,error){
                        //    this.showErrorMessage(xhr,text,error);
                        //    this.app.destroyShade()
                        //}.bind(this),formData,file)

                    }
                }
            }.bind(this));
            var fileNode = this.uploadFileAreaNode.getFirst();
            //alert(13)
            debugger;
            //alert(this.uploadFileAreaNode.get("html"))
            fileNode.click();
            //this.uploadFileAreaNode.destroy();
        }
    },
    deleteWork:function(e){
        var _self = this;
        _self.app.confirm("warn",e,_self.lp.submitWarn.warnTitle,_self.lp.submitWarn.warnContent,300,120,function(){
            _self.app.createShade();
            _self.actions.deleteCenterWork(_self.centerWorkData.id, function(json){
                if(json.type && json.type=="success"){
                    _self.app.notice(this.lp.prompt.deleteCenterWork, "success");
                    _self.closeWork({"action":"reload"});
                }
                _self.app.destroyShade();
            }.bind(_self),function(xhr,text,error){
                _self.showErrorMessage(xhr,text,error);
                _self.app.destroyShade();
            }.bind(_self));

            this.close()

        },function(){
            this.close();
        })

    },
    deployWork:function(e){
        var _self = this;
        _self.app.confirm("warn",e,_self.lp.submitWarn.warnTitle,_self.lp.submitWarn.warnDeployContent,300,120,function(){
            _self.app.createShade();
            _self.actions.deployCenterWork(_self.centerWorkData.id, function(json){
                if(json.type && json.type=="success"){
                    _self.app.notice(this.lp.prompt.deployCenterWork, "success");
                    _self.close();
                    _self.fireEvent("reloadView", {"action":"reload"});
                    _self.app.destroyShade();
                }
            }.bind(_self),function(xhr,text,error){
                _self.showErrorMessage(xhr,text,error);
                _self.app.destroyShade();
            }.bind(_self));

            this.close()

        },function(){
            this.close();
        })
    },
    confirmWork:function(e){
        var _self = this;
        _self.app.confirm("warn",e,_self.lp.submitWarn.warnTitle,_self.lp.submitWarn.warnConfirmContent,300,120,function(){
            _self.actions.deployCenterWork(_self.centerWorkData.id, function(json){
                _self.app.createShade();
                if(json.type && json.type=="success"){
                    _self.app.notice(this.lp.prompt.comfirmCenterWork, "success");
                    _self.close();
                    _self.fireEvent("reloadView", {"action":"reload"});
                    _self.app.destroyShade();
                }
            }.bind(_self),function(xhr,text,error){
                _self.showErrorMessage(xhr,text,error);
                _self.app.destroyShade();
            }.bind(_self));

            this.close()

        },function(){
            this.close();
        })
    },
    archiveWork:function(e){
        var _self = this;
        _self.app.confirm("warn",e,_self.lp.submitWarn.warnTitle,_self.lp.submitWarn.warnArchiveContent,300,120,function(){
            _self.app.createShade();
            _self.actions.archiveMainTask(_self.centerWorkData.id, function(json){
                if(json.type && json.type=="success"){
                    _self.app.notice(this.lp.prompt.archiveCenterWork, "success");
                    _self.close();
                    _self.fireEvent("reloadView", {"action":"reload"});
                    _self.app.destroyShade();
                }
            }.bind(_self),function(xhr,text,error){
                _self.showErrorMessage(xhr,text,error);
                _self.app.destroyShade();
            }.bind(_self));

            this.close()
        },function(){
            this.close();
        })

    },



    //*************************底部按钮及方法**************************************


    saveCenterWork: function(data,callback){

        if(data.reportAuditLeaderIdentityList == ""){
            data.reportAuditLeaderIdentityList = [];
        }else{
            data.reportAuditLeaderIdentityList = data.reportAuditLeaderIdentityList.split(",");
        }

        this.app.restActions.saveCenterWork( data,
            function(json){
                if( callback )callback(json);
            }.bind(this),
            function(xhr,text,error){
                this.showErrorMessage(xhr,text,error)
            }.bind(this),
            false
        );
    },
    openWorkForm : function( data ){
        MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
            this.workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,{
                "centerWorkId": data.id || this.options.centerWorkId,
                "centerWorkTitle":data.title
            },{
                "isNew": true,
                "isEdited": false,
                "actionStatus":"save",
                "onPostSave" : function(){
                    this.reloadTableContent()
                }.bind(this)
            });
            //alert("cccc="+this.centerWorkId)

            this.workform.load();
        }.bind(this));

    },
    showErrorMessage:function(xhr,text,error){
        var errorText = error;
        if (xhr) errorMessage = xhr.responseText;
        if(errorMessage!=""){
            var e = JSON.parse(errorMessage);
            if(e.message){
                this.app.notice( e.message,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }else{
            this.app.notice(errorText,"error");
        }

    }
});


MWF.xApplication.Execution.WorkDeploy.MyDeployWorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkDeploy.MyDeployWorkDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if(!this.explorer.centerWorkId) return;
        this.actions.getMyDeployWork(this.explorer.centerWorkId,function(json){
                if(json.data.length==0){
                    this.explorer.myDeployWorkArea.destroy();
                }
                if (callback) callback(json)
        }.bind(this),
        function(xhr,text,error){
            this.explorer.showErrorMessage(xhr,text,error)
        }.bind(this),false)

    },

    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

});

MWF.xApplication.Execution.WorkDeploy.MyDeployWorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    //action_edit : function(){
    //    this.workForm = new MWF.xApplication.Execution.WorkDeploy.WorkForm(this.view.explorer, this.actions, this.data, {
    //        "isNew": false,
    //        "isEdited": true,
    //        "actionStatus":"save",
    //        "onPostSave" : function(){
    //            this.view.explorer.contentChanged = true;
    //        }.bind(this)
    //    });
    //    this.workForm.load();
    //},
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        var ftd = itemNode.getElements("td")[0];
        itemNode.empty();
        var newTd = new Element("td",{
            "colspan":this.view.template.items.length,
            "style":ftd.get("style"),
            "html":ftd.get("html")
        }).inject(itemNode);

        if(itemData.subWorks){
            itemData.subWorks.each(function(d,i){
                var subTrNode = new Element("tr.subTrNode",{
                    "styles":this.css.subTrNode
                }).inject(this.view.viewNode);
                subTrNode.addEvents({
                    "click":function(){
                        this.action_view(d.id)
                    }.bind(this),
                    "mouseover":function(){
                        subTrNode.setStyles(this.css["documentNode_over"])
                    }.bind(this),
                    "mouseout":function(){
                        subTrNode.setStyles(this.css.subTrNode)
                    }.bind(this)
                });
                this.view.template.items.each(function(dd,i){
                    var htmlValue = dd.content.html;
                    subTrNode.set("html",subTrNode.get("html")+htmlValue)
                }.bind(this));
                this.setLables(subTrNode,d);
                this.setValues(subTrNode,d);
                this.setStyles(subTrNode,d);
                this.setActions(subTrNode,d)
            }.bind(this))

        }
    },
    setLables:function(container,data){
        container.getElements("[lable]").each(function(el){
            var val = el.get("lable");
            if( val && this.lp[val] ){
                el.set("text", this.lp[val] )
            }
        }.bind(this))
    },
    setValues:function(container,data){
        container.getElements("[item]").each(function(el){
            var val = el.get("item");

            if(data[val]){
                //特殊处理人员组织的字符串
                //var exp = "val == 'responsibilityUnitName' || val == 'deployerUnitName'";
                //exp = exp + " || val == 'responsibilityEmployeeName' || val == 'deployerName'";


                if(val == 'responsibilityUnitName' || val == 'deployerUnitName' || val == 'responsibilityEmployeeName' || val == 'deployerName'){
                    if(data[val]!=""){
                        if(data[val].indexOf(",")>0){
                            var v = data[val];
                            var vs = v.split(",");
                            var r = "";
                            for(i=0;i<vs.length;i++){
                                if(r=="") r = vs[i].split("@")[0];
                                else r = r + ","+vs[i].split("@")[0]
                            }
                            data[val]=r;
                        }else{
                            data[val] = data[val].split("@")[0];
                        }
                    }

                }

                if(val == 'cooperateUnitNameList' || val == 'cooperateEmployeeNameList' ){
                    var r = "";
                    for(i=0;i<data[val].length;i++){
                        if(r=="") r = data[val][i].split("@")[0];
                        else r = r + ","+data[val][i].split("@")[0]
                    }
                    data[val] = r;
                }

                el.set("text", data[val].length>70 ? data[val].substr(0,70)+'...' : data[val])
            }
        }.bind(this));
        container.getElements("[title]").each(function(el){
            var val = el.get("title");
            if(data[val]){
                el.set("title", data[val] )
            }
        }.bind(this))
    },
    setStyles:function(container,data){
        var tdActionNode = container.getElements("td[actionTd='yes']");
        if(!tdActionNode) return;
        container.getElements("[styles]").each(function(el){
            var val = el.get("styles");
            if( val && this.css[val] ){
                el.setStyles(this.css[val])
            }
        }.bind(this));
        container.getElements("[subStyles]").each(function(el){
            var val = el.get("subStyles");
            if( val && this.css[val] ){
                el.setStyles(this.css[val])
            }
        }.bind(this))
    },
    setActions:function(container,data){
        var actionTdNode = container.getElement("td[actionTd='yes']");
        if(!actionTdNode) return;
        if(data.operation){
            data.operation.each(function(d,i){
                var actionSpan = new Element("span.actionSpan",{
                    "styles":this.css.documentActionNode
                }).inject(actionTdNode);
                if(data.operation.length==1){
                    if(d == "VIEW"){
                        actionSpan.set("text",this.lp.action_view);
                        actionSpan.addEvent("click", function (e) {
                            this.action_view(data.id);
                            return false;
                        }.bind(this));
                    }
                }
                if(d == "EDIT"){
                    actionSpan.set("text",this.lp.action_edit);
                    actionSpan.addEvent("click", function (e) {
                        this.action_edit(data.id);
                        return false;
                    }.bind(this));
                }else if(d == "DELETE"){
                    actionSpan.set("text",this.lp.action_delete);
                    actionSpan.addEvent("click", function (e) {
                        this.action_delete(data.id,e);
                        return false;
                    }.bind(this));
                }else if(d == "ARCHIVE"){
                    actionSpan.set("text",this.lp.action_archive);
                    actionSpan.addEvent("click", function (e) {
                        this.action_archive(data.id,e);
                        return false;
                    }.bind(this));
                }
            }.bind(this))
        }
    },

    action_view:function(id){
        this.workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,{"id": id },{
            "isNew": false,
            "isEdited": false,
            "actionStatus":"save",
            "onPostSave" : function(){
                this.explorer.contentChanged = true;
            }.bind(this)
        });
        this.workform.load();

    },
    action_edit:function(id){
        this.workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,{"id": id },{
            "isNew": false,
            "isEdited": true,
            "actionStatus":"save",
            "onPostSave" : function(){
                this.explorer.reloadTableContent()
            }.bind(this)
        });
        this.workform.load();
    },
    action_delete:function(id,e){
        var _self = this;
        _self.view.app.confirm("warn",e,_self.view.app.lp.WorkDeploy.submitWarn.warnTitle,_self.view.app.lp.WorkDeploy.submitWarn.warnContent,300,120,function(){
            _self.app.createShade();
            _self.actions.deleteBaseWork(id, function(json){
                if(json.type && json.type=="success"){
                    this.app.notice(_self.view.explorer.lp.prompt.deleteBaseWork, "success");
                    _self.view.explorer.reloadTableContent();
                    _self.app.destroyShade();
                }
            }.bind(_self),function(xhr,text,error){
                _self.view.explorer.showErrorMessage(xhr,text,error);
                _self.app.destroyShade();
            }.bind(_self));

            this.close()


        },function(){
            this.close();
        })
    },
    action_archive:function(id,e){
        var _self = this;
        _self.view.app.confirm("warn",e,_self.view.app.lp.WorkDeploy.submitWarn.warnTitle,_self.view.app.lp.WorkDeploy.submitWarn.warnArchiveContent,300,120,function(){
            _self.app.createShade();
            _self.actions.archiveBaseWork(id, function(json){
                if(json.type && json.type=="success"){
                    this.app.notice(_self.view.explorer.lp.prompt.archiveBaseWork, "success");
                    _self.view.explorer.reloadTableContent();
                    _self.app.destroyShade();
                }
            }.bind(_self),function(xhr,text,error){
                _self.view.explorer.showErrorMessage(xhr,text,error);
                _self.app.destroyShade();
            }.bind(_self));

            this.close();



        },function(){
            this.close();
        })
    }
});


MWF.xApplication.Execution.WorkDeploy.MyWorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkDeploy.MyWorkDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if(this.explorer.centerWorkId){
            this.actions.getMyRelativeWork( this.explorer.centerWorkId, function(json){
                if(json.data.length==0){
                    this.explorer.myWorkContentArea.destroy();
                }
                if (callback) callback(json)
            }.bind(this),null,false);
        }

    },
    _openDocument: function( documentData ){
        this.workForm = new MWF.xApplication.Execution.WorkForm(this, this.actions, documentData, {
            "isNew": false,
            "isEdited": false
        });
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

});

MWF.xApplication.Execution.WorkDeploy.MyWorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    viewActionReturn:function(d) {
        var ret = false;
        if(d.operation && d.operation.length==1){
            ret = true;
        }
        return ret;
    },
    splitActionReturn:function(d) {
        var ret = false;
        if (d.operation && d.operation.indexOf("SPLIT")>-1)ret = true;
        return ret;
    },
    authorizeActionReturn:function(d) {
        var ret = false;
        if (d.operation && d.operation.indexOf("AUTHORIZE")>-1)ret = true;
        return ret;
    },
    tackBackActionReturn:function(d){
        var ret = false;
        if (d.operation && d.operation.indexOf("TACKBACK")>-1)ret = true;
        return ret;
    },
    archiveActionReturn:function(d){
        var ret = false;
        if (d.operation && d.operation.indexOf("ARCHIVE")>-1)ret = true;
        return ret
    },
    action_view:function(){
        MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
            var workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,this.data,{
                "isNew": false,
                "isEdited": false
            });
            workform.load();
        }.bind(this));

    },
    action_split:function(){
        MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
            var data = {
                title : this.data.title,
                centerId : this.data.centerId,
                centerWorkTitle: this.data.centerTitle,
                parentWorkId : this.data.id,
                //parentWorkTitle : this.data.title,
                workType : this.data.workType,
                workLevel : this.data.workLevel,
                completeDateLimitStr : this.data.completeDateLimitStr,
                completeDateLimit : this.data.completeDateLimit,
                reportCycle: this.data.reportCycle,
                reportDayInCycle: this.data.reportDayInCycle
            };
            if(this.data.id){
                this.actions.getBaseWorkDetails(this.data.id, function (json) {
                    data.workSplitAndDescription = json.data.workDetail;
                    //data.specificActionInitiatives = json.data.progressAction
                    //data.cityCompanyDuty = json.data.dutyDescription
                    //data.milestoneMark = json.data.landmarkDescription
                    //data.importantMatters = json.data.majorIssuesDescription
                }.bind(this),null,false)
            }

            var workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,{"centerWorkTitle":this.data.centerTitle},{
                "isNew": true,
                "isEdited": false,
                "parentWorkId":this.data.id,
                "actionStatus":"save",
                "onPostSave" : function(){
                    this.explorer.reloadTableContent()
                }.bind(this)
            });

            workform.load();
        }.bind(this));
    },
    action_authorize:function(){
        var data = {
            workId : this.data.id
        };
        var appointForm =  new MWF.xApplication.Execution.WorkDeploy.Appoint(this.view.app,this.view.app.restActions,data,this.view.css,{
            "ieEdited": true,
            "onReloadView" : function( data ){
                this.explorer.reloadTableContent()
            }.bind(this)
        });
        appointForm.load();
    },
    action_tackBack: function(e){
        var _self = this;
        _self.app.confirm("warn",e,_self.lp.submitWarn.warnTitle,_self.lp.submitWarn.warnTackBackContent,300,120,function(){
            _self.actions.unAppointBaseWork({workId:_self.data.id}, function(json){
                if(json.type && json.type=="success"){
                    _self.app.notice(_self.explorer.lp.prompt.tackbackBaseWork, "success");
                    _self.explorer.reloadTableContent();
                }
            }.bind(_self),function(xhr,text,error){
                _self.explorer.showErrorMessage(xhr,text,error)
            }.bind(_self));

            this.close()

        },function(){
            this.close();
        })
    },
    action_archive:function(e){
        var _self = this;
        _self.view.app.confirm("warn",e,_self.view.app.lp.WorkDeploy.submitWarn.warnTitle,_self.view.app.lp.WorkDeploy.submitWarn.warnArchiveContent,300,120,function(){
            _self.app.createShade();
            _self.actions.archiveBaseWork(_self.data.id, function(json){
                if(json.type && json.type=="success"){
                    this.app.notice(_self.view.explorer.lp.prompt.archiveBaseWork, "success");
                    _self.view.explorer.reloadTableContent();
                    _self.app.destroyShade();
                }
            }.bind(_self),function(xhr,text,error){
                _self.view.explorer.showErrorMessage(xhr,text,error);
                _self.app.destroyShade();
            }.bind(_self));

            this.close();



        },function(){
            this.close();
        })
    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        if(itemNode.getElements("div[item='workDetail']").length>0){
            if(itemData.hasSubWorks){
                itemNode.getElements("div[item='workDetail']").setStyle("color","#ec6a1a");
            }
            //this.view.actions.getBaseWorksByParentId(itemData.id,function(json){
            //    if(json.data && json.data.length>0){
            //        itemNode.getElements("div[item='title']").setStyle("color","#ec6a1a");
            //        //itemNode.getElements("div[item='title']").set("title",this.view.lp.splitReady);
            //    }
            //}.bind(this))
        }

        if(itemNode.getElements("div[name='appointDiv']")){
            if(itemData.workProcessIdentity && itemData.workProcessIdentity.indexOf("AUTHORIZE")>-1){
                itemNode.getElements("div[name='appointDiv']").setStyle("display","")
            }
        }
        if(itemNode.getElements("div[styles='documentSubject']")){
            itemNode.getElements("div[styles='documentSubject']").set("title",itemData.workDetail)
        }
    }

});


MWF.xApplication.Execution.WorkDeploy.Appoint = new Class({
    Extends: MPopupForm,
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
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div.formTopTextNode", {
                "styles": this.css.formTopTextNode,
                "text": this.app.lp.workTask.appoint.appointTitle
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            this.formTopContentNode = new Element("div.formTopContentNode", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode);

            //this._createTopContent();

        }

    },
    _createTableContent: function () {
        var table = new Element("table",{"width":"100%",border:"0",cellpadding:"5",cellspacing:"0"}).inject(this.formTableArea);
        table.setStyles({"margin-top":"40px"});
        var tr = new Element("tr").inject(table);
        var td = new Element("td",{
            text : this.app.lp.workTask.appoint.appointFor,
            valign:"middle",
            width:"20%"
        }).inject(tr);
        td = new Element("td",{width:"80%"}).inject(tr);

        this.appointPerson = new MDomItem( td, {
            "name" : "appointPerson", "type":"org","orgType":"identity","notEmpty":true,
            "style":{"width":"90%","height":"25px","border":"1px solid #666"}
        }, true, this.app );
        this.appointPerson.load();

        //})
        tr = new Element("tr").inject(table);
        td = new Element("td",{
            "text" : this.app.lp.workTask.appoint.appointOpinion,
            valign:"middle"
        }).inject(tr);


        td = new Element("td").inject(tr);
        this.appointOpinion = new Element("textarea").inject(td);
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

        if(this.appointPerson.getValue()==""){
            this.app.notice(this.app.lp.workTask.appoint.personEmpty,"error");
            return false;
        }
        if(this.appointOpinion.get("value")==""){
            this.app.notice(this.app.lp.workTask.appoint.opinionEmpty,"error");
            return false;
        }
        var submitData = {
            workId : this.data.workId,
            //undertakerIdentity : this.appointPerson.get("value"),
            undertakerIdentity : this.appointPerson.getValue(","),
            authorizeOpinion : this.appointOpinion.get("value")
        };
        this.actions.appointBaseWork(submitData,function(json){
            this.app.notice(this.app.lp.WorkDeploy.prompt.authorizeBaseWork,"success");
            this.close();
            this.fireEvent("reloadView");
        }.bind(this),function(xhr,text,error){
            var errorText = error;
            if (xhr) errorMessage = xhr.responseText;
            var e = JSON.parse(errorMessage);
            if(e.message){
                this.app.notice( e.message,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }.bind(this),false)
    },
    selectPerson: function( item, type,count ) {
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        this.fireEvent("querySelect", this);
        var value = item.get("value").split(this.valSeparator);
        var options = {
            "type": type,
            "title": this.app.lp.workTask.appoint.appointTitle,
            "count": count,
            "values": value || [],
            "onComplete": function (items) {
                var arr = [];
                items.each(function (item) {
                    arr.push(item.data.distinguishedName);
                }.bind(this));
                item.set("value", arr.join(","));
            }.bind(this)
        };

        var selector = new MWF.O2Selector(this.app.content, options);
    }

});
