MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Execution", "WorkForm", null, false);

MWF.xApplication.Execution.WorkGather = new Class({
    Extends: MWF.xApplication.Template.Explorer.PopupForm,
    Implements: [Options, Events],
    options: {
        //"centerWorkId" : "fc44be47-7271-469f-8f04-deebdb71d3e6",
        "style": "default",
        "width": "100%",
        "height": "100%",
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
        this.lp = this.app.lp.workGather;
        this.actions = this.app.restActions;
        this.path = "/x_component_Execution/$WorkGather/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.data = data || {};

        this.actions = actions;


    },
    load: function () {
        //alert(JSON.stringify(this.data))
        if (this.options.isNew) {
            this.create();
        } else if (this.options.isEdited) {
            this.edit();
        } else {
            this.open();
        }
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
                "text": this.data.title
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

    },

    _createTableContent: function (data) {
        this.titleDiv = new Element("div.titleDiv",{
            "styles":this.css.titleDiv,
            "text":this.data.title
        }).inject(this.formTableArea)

        this.inforDiv = new Element("div.inforDiv",{"styles":this.css.inforDiv}).inject(this.formTableArea);
        this.gatherDiv = new Element("div.gatherDiv",{"styles":this.css.gatherDiv}).inject(this.formTableArea);

        this.getDepartmentGather();
    },
    getDepartmentGather: function(){
        this.reportDataArr = [];
        this.gatherDiv.empty();
        this.WorkReportView = new MWF.xApplication.Execution.WorkGather.WorkReportView(this.gatherDiv, this.app, this, { templateUrl : this.path+"listItem.json" })
        this.WorkReportView.load();
    },

    openWorkReport: function(workReportId,workId){
        MWF.xDesktop.requireApp("Execution", "WorkReport", function(){

            var data = {
                workReportId : workReportId,
                workId : workId
            }

            this.workReport = new MWF.xApplication.Execution.WorkReport(this, this.actions,data,{
                "isEdited":false,
                "width":"90%",
                "height":"90%",
                onReloadView : function( data ){
                    this.getDepartmentGather();
                }.bind(this)
            } );
            this.workReport.load();
        }.bind(this));
    },
    _createBottomContent: function () {

        this.submitActionNode = new Element("div.submitActionNode", {
            "styles": this.css.formActionNode,
            "text": this.lp.bottomAction.submit
        }).inject(this.formBottomNode);
        var _self = this;
        this.submitActionNode.addEvent("click", function (e) {
                this.app.confirm("warn",e,this.lp.submitWarn.warnTitle,this.lp.submitWarn.warnContent,300,120,function(){
                    _self.submitGather()
                    this.close();
                },function(){
                    this.close();
                })

        }.bind(this));

        this.closeActionNode = new Element("div.formActionNode", {
            "styles": this.css.formActionNode,
            "text": this.lp.bottomAction.close
        }).inject(this.formBottomNode);
        this.closeActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    submit:function(workReportData){
        var adminText = this.gatherDiv.getElementById("admin"+workReportData.id);
        var adminValue = "";
        if(adminText) adminValue = adminText.get("value");
        var opinionValue = ""
        var opinionText = this.gatherDiv.getElementById("opinion"+workReportData.id);
        if(opinionText) opinionValue = opinionText.get("value");
        var submitData = {
            workId : workReportData.workId,
            id : workReportData.id,
            adminSuperviseInfo : adminValue,
            opinion :  opinionValue
        }
        this.actions.submitWorkReport( submitData, function(json){
            if(json.type == "success"){
                this.app.notice(json.userMessage,"success");
                this.actions.getDepartmentGather(this.data.gatherId,function(json){
                    if(json.data.reportInfos && json.data.reportInfos.length==0){
                        this.fireEvent("reloadView");
                        this.close()
                    }else{
                        this.getDepartmentGather();
                    }
                }.bind(this),null,false)

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
    submitGather:function(){
        this.submitStatus = true;
        this.submitError = "";
        for(var i=0;i<this.reportDataArr.length;i++){
            this.currentReportData = this.reportDataArr[i]
            if(this.submitStatus){
                var adminText = this.gatherDiv.getElementById("admin"+this.currentReportData.id);
                var adminValue = "";
                if(adminText) adminValue = adminText.get("value");
                var opinionValue = ""
                var opinionText = this.gatherDiv.getElementById("opinion"+this.currentReportData.id);
                if(opinionText) opinionValue = opinionText.get("value");
                var submitData = {
                    workId : this.currentReportData.workId,
                    id : this.currentReportData.id,
                    adminSuperviseInfo : adminValue,
                    opinion :  opinionValue
                }
                //if(i==0){
                //    submitData.workId = "fefwfwfwfewfwfewfe";
                //    submitData.id = "eeeeeeeeee"
                //
                //}
                this.actions.submitWorkReport( submitData, function(json){

                }.bind(this), function(xhr){
                    var json = JSON.parse(xhr.responseText);
                    this.submitError = "《"+this.currentReportData.title+"》"+json.userMessage
                    this.submitStatus = false;
                }.bind(this),false);
            }
        }

        if(!this.submitStatus){
            this.app.notice(this.submitError, "error");
            this.getDepartmentGather();
        }else{
            this.app.notice(this.lp.submitWarn.submitSuccess, "success");
            this.fireEvent("reloadView");
            this.close();
        }
    }

})




MWF.xApplication.Execution.WorkGather.WorkReportView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.WorkGather.WorkReportDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        this.actions.getDepartmentGather(this.explorer.data.gatherId,function(json){ //alert(JSON.stringify(json))
            if(callback) callback(json)
        }.bind(this),null,false)


    },
    loadElementList: function (count) {
        if (!this.isItemsLoaded) {
            if (!this.isItemLoadding) {
                this.isItemLoadding = true;
                this._getCurrentPageData(function (json) {
                    //if( !json.data )return;
                    var length = json.count;  //|| json.data.length;

                    //if (!this.isCountShow){
                    //    this.filterAllProcessNode.getFirst("span").set("text", "("+this.count+")");
                    //    this.isCountShow = true;
                    //}
                    if (length <= this.items.length) {
                        this.isItemsLoaded = true;
                    }
                    json.data.reportInfos.each(function (data) {
                        if (!this.documents[data.id]) {
                            var item = this._createDocument(data);
                            this.items.push(item);
                            this.documents[data.id] = item;
                        }
                    }.bind(this));

                    this.isItemLoadding = false;

                    if (this.loadItemQueue > 0) {
                        this.loadItemQueue--;
                        this.loadElementList();
                    }
                }.bind(this), count);
            } else {
                this.loadItemQueue++;
            }
        }
    },
    _removeDocument: function(documentData, all){

    },
    _create: function(data){

    },
    _openDocument: function( documentData ){
        //this.workForm = new MWF.xApplication.Execution.WorkForm(this, this.actions, documentData, {
        //    "isNew": false,
        //    "isEdited": false
        //})
        //this.workForm.load();
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

MWF.xApplication.Execution.WorkGather.WorkReportDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        //alert(JSON.stringify(itemData))
        //alert(itemNode.get("html"))
        if( itemData.reports ){
            itemData.reports.each(function(d){
                this.view.explorer.reportDataArr.push(d);
                var trNode = new Element("tr.trNodeTitle",{"styles":this.view.css.trNodeTitle}).inject(this.view.viewNode);
                var tdNode = new Element("td.tdNodeTitle",{
                    "styles":this.view.css.tdNodeTitle,
                    "text": d.title,
                    "colspan":"6"
                }).inject(trNode).
                    addEvents({
                        "click":function(){
                            this.view.explorer.openWorkReport(d.id, d.workId);
                        }.bind(this)
                    })

                trNode = new Element("tr.trNode",{"styles":this.view.css.trNode}).inject(this.view.viewNode)
                tdNode = new Element("td.tdNodeContent",{
                    "styles":this.view.css.tdNodeContent
                }).inject(trNode);
                divNode = new Element("div.divNode",{
                    "styles":this.view.css.divNode,
                    "text": d.workInfo.shortProgressAction?d.workInfo.shortProgressAction:"",
                    "title": d.workInfo.shortProgressAction?d.workInfo.shortProgressAction:""
                }).inject(tdNode);
                tdNode = new Element("td.tdNodeContent",{
                    "styles":this.view.css.tdNodeContent
                }).inject(trNode);
                divNode = new Element("div.divNode",{
                    "styles":this.view.css.divNode,
                    "text": d.progressDescription,
                    "title": d.progressDescription
                }).inject(tdNode);
                tdNode = new Element("td.tdNodeContent",{
                    "styles":this.view.css.tdNodeContent
                }).inject(trNode);
                divNode = new Element("div.divNode",{
                    "styles":this.view.css.divNode,
                    "text": d.workPlan,
                    "title": d.workPlan
                }).inject(tdNode);
                //管理员
                if(d.isWorkAdmin && d.processStatus == this.view.lp.activityName.manager){
                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    var teextareaNode = new Element("textarea.tetareaNode",{
                        "styles":this.view.css.textareaNode,
                        "id":"admin"+ d.id
                    }).inject(tdNode);
                }else{
                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    divNode = new Element("div.divNode", {
                        "styles": this.view.css.divNode,
                        "text": d.adminSuperviseInfo,
                        "title": d.adminSuperviseInfo
                    }).inject(tdNode)
                }
                //领导
                //this.workReportData.processStatus == this.lp.activityName.leader && this.workReportData.isReadLeader && this.processIdentity.indexOf(this.app.identity)>-1
                if(d.processStatus == this.view.lp.activityName.leader && d.isReadLeader && d.currentProcessorIdentity.indexOf(this.app.identity)>-1){
                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    var teextareaNode = new Element("textarea.tetareaNode",{
                        "styles":this.view.css.textareaNode,
                        "id":"opinion"+ d.id
                    }).inject(tdNode);
                }else{
                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent,
                        "text": ""
                    }).inject(trNode);
                }

                var tdNodeAction = new Element("td.tdNodeAction",{
                    "styles":this.view.css.tdNodeAction,
                }).inject(trNode);
                var actionTxt = new Element("a.actionTxt",{
                    "styles":this.view.css.actionTxt,
                    "text":this.view.lp.viewSubmit
                }).inject(tdNodeAction)
                    .addEvents({
                        "click":function(){
                            this.view.explorer.submit(d)
                        }.bind(this)
                    })


            }.bind(this))

            //this.view.viewNode.getElements("textarea").setStyle("height","100%")
        }

    }

})