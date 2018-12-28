MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Execution", "WorkForm", null, false);

MWF.xApplication.Execution.WorkGather = new Class({
    Extends: MPopupForm,
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
        this.app = explorer.app||explorer;
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
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.data.title
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            this.formTopContentNode = new Element("div", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode);

            this._createTopContent();

        }
    },
    _createTopContent: function () {

    },

    _createTableContent: function (data) {
        this.titleDiv = new Element("div.titleDiv",{
            "styles":this.css.titleDiv,
            "text":this.data.title
        }).inject(this.formTableArea);

        this.inforDiv = new Element("div.inforDiv",{"styles":this.css.inforDiv}).inject(this.formTableArea);
        //this.gatherDiv = new Element("div.gatherDiv",{"styles":this.css.gatherDiv}).inject(this.formTableArea);

        this.gatherJson = null;
        this.gatherJsonLen = 0;
        this.loadGather();
    },

    loadGather: function(){

        if(this.gatherDiv) this.gatherDiv.destroy();
        this.gatherDiv = new Element("div.gatherDiv",{"styles":this.css.gatherDiv}).inject(this.formTableArea);
        this.tabContentDiv = new Element("div.tabContentDiv",{
            "styles":this.css.tabContentDiv
        }).inject(this.gatherDiv);

        this.actions.getDepartmentGather(this.data.gatherId,function(json){
            if(json.data) {
                this.gatherJson = json.data;
                this.gatherJsonLen = json.data.length;
            }
        }.bind(this),null,false);
        if(this.gatherJsonLen>0){
            this.loadTabContent();
        }

    },

    loadTabContent : function(){
        for(i=0;i<this.gatherJson.length;i++){

            this.tabContentLi = new Element("li.tabContentLi",{
                "styles": this.css.tabContentLi,
                "tabIndex" : i,
                "text" : this.gatherJson[i].activityName+"("+this.gatherJson[i].count+")",
                "tabName" : this.gatherJson[i].activityName
            }).inject(this.tabContentDiv);

        }
        var _this = this;
        this.tabContentDiv.getElements("li").addEvents({
            "click":function(){
                _this.loadGatherContent(this)
            }
        });

        if(this.curTabIndex){

        }else{
            this.curTabName = "";
            this.curTabIndex = 0;
        }

        this.tabContentDiv.getElements("li")[this.curTabIndex].click();
    },

    loadGatherContent: function(obj){
        this.curTabName = obj.get("tabName");
        this.curTabIndex = obj.get("tabIndex");
        this.tabContentDiv.getElements("li").setStyles({"background-color":"","color":""});
        obj.setStyles({"background-color":"#3d77c1","color":"#ffffff"});


        if(this.gatherContentDiv) this.gatherContentDiv.destroy();
        this.gatherContentDiv = new Element("div.gatherContentDiv",{
            "styles": this.css.gatherContentDiv
        }).inject(this.gatherDiv);

        var tab = obj.get("tabIndex");
        var tabName = obj.get("tabName");
        if(obj.get("tabName") == this.lp.gatherName.drafter){
            templateUrl = this.path+"listItem_drafter.json"
        }else if(obj.get("tabName") == this.lp.gatherName.manager){
            templateUrl = this.path+"listItem_manager.json"
        }else if(obj.get("tabName") == this.lp.gatherName.leader){
            templateUrl = this.path+"listItem_leader.json"
        }

        this.reportDataArr = [];
        this.WorkReportView = new MWF.xApplication.Execution.WorkGather.WorkReportView(this.gatherContentDiv, this.app, this, { templateUrl : templateUrl,tab: tab,tabName:tabName});
        this.WorkReportView.load();
    },


    openWorkReport: function(workReportId,workId){
        MWF.xDesktop.requireApp("Execution", "WorkReport", function(){

            var data = {
                workReportId : workReportId,
                workId : workId
            };

            this.workReport = new MWF.xApplication.Execution.WorkReport(this, this.actions,data,{
                "isEdited":false,
                "width":"90%",
                "height":"90%",
                onReloadView : function( data ){

                    this.actions.getDepartmentGather(this.data.gatherId,function(json){
                        if(json.data && json.data.length==0){
                            this.fireEvent("reloadView");
                            this.close()
                        }
                    }.bind(this),null,false);

                    this.loadGather();
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

        var submitInfor = (this.curTabName == this.lp.activityName.drafter || this.curTabName == this.lp.activityName.manager) ? this.lp.submitWarn.warnGatherSubmit:this.lp.submitWarn.warnGatherSubmitAll;
        var _self = this;
        this.submitActionNode.addEvent("click", function (e) {
                this.app.confirm("warn",e,this.lp.submitWarn.warnTitle,submitInfor,300,150,function(){
                    _self.submitGather();
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
    submit:function(workReportData,e){
        var progressText = this.gatherDiv.getElementById("progress"+workReportData.id);
        var progressValue = "";
        if(progressText) progressValue = progressText.get("value");
        var planText = this.gatherDiv.getElementById("plan"+workReportData.id);
        var planValue = "";
        if(planText) planValue = planText.get("value");
        var adminText = this.gatherDiv.getElementById("admin"+workReportData.id);
        var adminValue = "";
        if(adminText) adminValue = adminText.get("value");
        var opinionValue = "";
        var opinionText = this.gatherDiv.getElementById("opinion"+workReportData.id);
        if(opinionText) opinionValue = opinionText.get("value");
        var isWorkCompletedText = this.gatherDiv.getElementById("completeSelect"+workReportData.id);
        var isWorkCompletedValue = "";
        if(isWorkCompletedText) isWorkCompletedValue = isWorkCompletedText.get("value");
        var progressPercentText = this.gatherDiv.getElementById("completePercentSelect"+workReportData.id);
        var progressPercentValue = "";
        if(progressPercentText) progressPercentValue = parseInt(progressPercentText.get("value"));

        if(progressText && progressValue==""){
            this.app.notice(this.lp.viewProgressDescription+this.lp.notEmpty,"error");
            return false;
        }
        if(planText && planValue==""){
            this.app.notice(this.lp.viewWorkPlan+this.lp.notEmpty,"error");
            return false;
        }
        var submitData = {
            workId : workReportData.workId,
            id : workReportData.id,
            progressDescription : progressValue,
            workPlan : planValue,
            adminSuperviseInfo : adminValue,
            opinion :  opinionValue

        };
        if(isWorkCompletedText){
            submitData.isWorkCompleted = isWorkCompletedValue == "yes"
        }
        if(progressPercentText){
            submitData.progressPercent = progressPercentValue
        }

        if(isWorkCompletedText && isWorkCompletedText.get("value")=="yes"){
            var _self = this;
            this.app.confirm("warn",e,_self.lp.submitWarn.warnTitle,_self.lp.submitWarn.warnWorkCompleted,300,150,function(){
                _self.app.createShade();
                _self.actions.submitWorkReport( submitData, function(json){
                    _self.app.destroyShade();
                    if(json.type == "success"){
                        _self.app.notice(_self.lp.prompt.submitWorkReport,"success");
                        _self.actions.getDepartmentGather(_self.data.gatherId,function(json){
                            if(json.data && json.data.length==0){
                                _self.fireEvent("reloadView");
                                _self.close()
                            }else{
                                if(_self.gatherJson[this.curTabIndex].count == 1){
                                    _self.curTabIndex = 0;
                                    _self.curTabName = "";
                                }
                                _self.loadGather();
                            }
                        }.bind(_self),null,false)
                    }

                }.bind(_self),function(xhr,text,error){
                    _self.app.destroyShade();
                    var errorText = error;
                    if (xhr) errorMessage = xhr.responseText;
                    var e = JSON.parse(errorMessage);
                    if(e.message){
                        _self.app.notice( e.message,"error");
                    }else{
                        _self.app.notice( errorText,"error");
                    }
                }.bind(_self));

                this.close();
            },function(){
                this.close();
            })
        }else{
            this.app.createShade();
            this.actions.submitWorkReport( submitData, function(json){
                this.app.destroyShade();
                if(json.type == "success"){
                    this.app.notice(this.lp.prompt.submitWorkReport,"success");
                    this.actions.getDepartmentGather(this.data.gatherId,function(json){
                        if(json.data && json.data.length==0){
                            this.fireEvent("reloadView");
                            this.close()
                        }else{
                            if(this.gatherJson[this.curTabIndex].count == 1){
                                this.curTabIndex = 0;
                                this.curTabName = "";
                            }
                            this.loadGather();
                        }
                    }.bind(this),null,false)
                }
            }.bind(this),function(xhr,text,error){
                var errorText = error;
                if (xhr) errorMessage = xhr.responseText;
                var e = JSON.parse(errorMessage);
                if(e.message){
                    this.app.notice( e.message,"error");
                }else{
                    this.app.notice( errorText,"error");
                }
                this.app.destroyShade();
            }.bind(this));
        }





        //this.actions.submitWorkReport( submitData, function(json){
        //    if(json.type == "success"){
        //        this.app.notice(this.lp.prompt.submitWorkReport,"success");
        //        this.actions.getDepartmentGather(this.data.gatherId,function(json){
        //            if(json.data && json.data.length==0){
        //                this.fireEvent("reloadView");
        //                this.close()
        //            }else{
        //                if(this.gatherJson[this.curTabIndex].count == 1){
        //                    this.curTabIndex = 0;
        //                    this.curTabName = "";
        //                }
        //                this.loadGather();
        //            }
        //        }.bind(this),null,false)
        //
        //    }
        //}.bind(this),function(xhr,text,error){
        //    var errorText = error;
        //    if (xhr) errorMessage = xhr.responseText;
        //    var e = JSON.parse(errorMessage);
        //    if(e.message){
        //        this.app.notice( e.message,"error");
        //    }else{
        //        this.app.notice( errorText,"error");
        //    }
        //}.bind(this));

    },
    submitGather:function(){
        this.submitStatus = true;
        this.submitError = "";
        if(this.reportDataArr){
            this.app.createShade();
            for(var i=0;i<this.reportDataArr.length;i++){
                this.currentReportData = this.reportDataArr[i];
                if(this.submitStatus){
                    var progressText = this.gatherDiv.getElementById("progress"+this.currentReportData.id);
                    var progressValue = "";
                    if(progressText) progressValue = progressText.get("value");
                    var planText = this.gatherDiv.getElementById("plan"+this.currentReportData.id);
                    var planValue = "";
                    if(planText) planValue = planText.get("value");
                    var adminText = this.gatherDiv.getElementById("admin"+this.currentReportData.id);
                    var adminValue = "";
                    if(adminText) adminValue = adminText.get("value");
                    var opinionValue = "";
                    var opinionText = this.gatherDiv.getElementById("opinion"+this.currentReportData.id);
                    if(opinionText) opinionValue = opinionText.get("value");
                    var isWorkCompletedText = this.gatherDiv.getElementById("completeSelect"+this.currentReportData.id);
                    var isWorkCompletedValue = "";
                    if(isWorkCompletedText) isWorkCompletedValue = isWorkCompletedText.get("value");
                    var progressPercentText = this.gatherDiv.getElementById("completePercentSelect"+this.currentReportData.id);
                    var progressPercentValue = "";
                    if(progressPercentText) progressPercentValue = parseInt(progressPercentText.get("value"));

                    var submitData = {
                        workId : this.currentReportData.workId,
                        id : this.currentReportData.id,
                        progressDescription : progressValue,
                        workPlan : planValue,
                        adminSuperviseInfo : adminValue,
                        opinion :  opinionValue
                    };
                    if(isWorkCompletedText){
                        //submitData.isWorkCompleted = isWorkCompletedValue=="yes"?true:false
                        submitData.isWorkCompleted = isWorkCompletedValue == "yes"
                    }
                    if(progressPercentText){
                        submitData.progressPercent = progressPercentValue
                    }
                    //if(i==0){
                    //    submitData.workId = "fefwfwfwfewfwfewfe";
                    //    submitData.id = "eeeeeeeeee"
                    //
                    //}
                    this.actions.submitWorkReport( submitData, function(json){

                    }.bind(this), function(xhr){
                        var json = JSON.parse(xhr.responseText);
                        this.submitError = "《"+this.currentReportData.title+"》"+json.message;
                        this.submitStatus = false;
                    }.bind(this),false);
                }
            }
        }


        if(!this.submitStatus){
            this.app.notice(this.submitError, "error");

            this.loadGather();
        }else{
            //alert(this.gatherJsonLen)
            this.app.notice(this.lp.submitWarn.submitSuccess, "success");
            if(this.gatherJsonLen==1 ||this.gatherJsonLen==0 ){
                this.fireEvent("reloadView");
                this.close();
            }else{
                this.curTabIndex = 0;
                this.curTabName = "";
                this.loadGather();
            }

        }
        this.app.destroyShade();
    }

});




MWF.xApplication.Execution.WorkGather.WorkReportView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        data.tabName = this.options.tabName;
        return new MWF.xApplication.Execution.WorkGather.WorkReportDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        this.actions.getDepartmentGather(this.explorer.data.gatherId,function(json){ //alert(JSON.stringify(json))
            if(json.data){
                if(this.options.tab){
                    var tabIndex = parseInt(this.options.tab);
                    json = json.data[tabIndex].reportCollect
                } else{
                    json = json.data[0].reportCollect
                }
                if(callback) callback(json)
            }

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
                    json.reportInfos.each(function (data) {
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
        if(itemData.tabName ==this.view.lp.gatherName.drafter){
            cols = 5
        }else if(itemData.tabName ==this.view.lp.gatherName.manager){
            cols = 6
        }else if(itemData.tabName ==this.view.lp.gatherName.leader){
            cols = 7
        }
        if( itemData.reports ){
            itemData.reports.each(function(d,ii){
                this.view.explorer.reportDataArr.push(d);
                var trNode = new Element("tr.trNodeTitle",{"styles":this.view.css.trNodeTitle}).inject(this.view.viewNode);
                var tdNode = new Element("td.tdNodeTitle",{
                    "styles":this.view.css.tdNodeTitle,
                    //"text": d.title + "(" +d.createTime+ ")",
                    "html" : d.title + "&nbsp;&nbsp;&nbsp;&nbsp;(" +d.createTime+ ")",
                    "colspan":cols
                }).inject(trNode).
                    addEvents({
                        "click":function(){
                            this.view.explorer.openWorkReport(d.id, d.workId);
                        }.bind(this)
                    });

                trNode = new Element("tr.trNode",{"styles":this.view.css.trNode}).inject(this.view.viewNode);
                tdNode = new Element("td.tdNodeContent",{
                    "styles":this.view.css.tdNodeContent
                }).inject(trNode); //事项分解
                divNode = new Element("div.divNode",{
                    "styles":this.view.css.divNode,
                    "text": d.workInfo.shortProgressAction?d.workInfo.shortProgressAction:"",
                    "title": d.workInfo.shortProgressAction?d.workInfo.shortProgressAction:""
                }).inject(tdNode); //具体行动

                if(itemData.tabName ==this.view.lp.gatherName.drafter){//草稿
                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    var teextareaNode = new Element("textarea.tetareaNode",{
                        "styles":this.view.css.textareaNode,
                        "id":"progress"+ d.id,
                        "value": d.progressDescription
                    }).inject(tdNode); //截止当前
                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    var teextareaNode = new Element("textarea.tetareaNode",{
                        "styles":this.view.css.textareaNode,
                        "id":"plan"+ d.id,
                        "value": d.workPlan
                    }).inject(tdNode); //下一步

                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);

                    var tmpDiv = new Element("div.completeSelectDiv",{
                        styles:this.css.completeSelectDiv
                    }).inject(tdNode);

                    var completeSelect = new Element("select.completeSelect",{
                        styles:this.css.completeSelect,
                        id:"completeSelect"+ d.id,
                        position:ii
                    }).inject(tmpDiv);
                    var completeSelectOption = new Element("option.completeSelectOption",{"text":this.lp.isCompletedNoOption,"value":"no"}).inject(completeSelect);
                    completeSelectOption = new Element("option.completeSelectOption",{"text":this.lp.isCompletedYesOption,"value":"yes"}).inject(completeSelect);
                    var _self = this;
                    completeSelect.addEvents({
                        "change":function(){
                            if(this.get("value")=="yes"){
                                var po = this.get("position");
                                _self.view.explorer.gatherContentDiv.getElements(".completePercentSelect[position="+po+"]").set("value","100")
                            }
                        }
                    });
                    completeSelect.set("value", d.isWorkCompleted?"yes":"no");
                    tmpDiv = new Element("div.completeSelectDiv",{
                        styles:this.css.completeSelectDiv
                    }).inject(tdNode);
                    var completePercentSelect = new Element("select.completePercentSelect",{
                        styles:this.css.completeSelect,
                        id:"completePercentSelect"+ d.id,
                        position:ii
                    }).inject(tmpDiv);
                    var completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"0%","value":"0"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"10%","value":"10"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"20%","value":"20"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"30%","value":"30"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"40%","value":"40"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"50%","value":"50"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"60%","value":"60"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"70%","value":"70"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"80%","value":"80"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"90%","value":"90"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"100%","value":"100"}).inject(completePercentSelect);
                    //是否办结

                    completePercentSelect.set("value",parseInt(d.progressPercent))

                }else if(itemData.tabName ==this.view.lp.gatherName.manager){//管理员
                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    divNode = new Element("div.divNode",{
                        "styles":this.view.css.divNode,
                        "text": d.progressDescription,
                        "title": d.progressDescription
                    }).inject(tdNode); //截止当前
                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    divNode = new Element("div.divNode",{
                        "styles":this.view.css.divNode,
                        "text": d.workPlan,
                        "title": d.workPlan
                    }).inject(tdNode); //下一步要点


                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);

                    var tmpDiv = new Element("div.completeSelectDiv",{
                        styles:this.css.completeSelectDiv
                    }).inject(tdNode);

                    var completeSelect = new Element("select.completeSelect",{
                        styles:this.css.completeSelect,
                        id:"completeSelect"+ d.id,
                        position:ii
                    }).inject(tmpDiv);
                    var completeSelectOption = new Element("option.completeSelectOption",{"text":this.lp.isCompletedNoOption,"value":"no"}).inject(completeSelect);
                    completeSelectOption = new Element("option.completeSelectOption",{"text":this.lp.isCompletedYesOption,"value":"yes"}).inject(completeSelect);
                    var _self = this;
                    completeSelect.addEvents({
                        "change":function(){
                            if(this.get("value")=="yes"){
                                var po = this.get("position");
                                _self.view.explorer.gatherContentDiv.getElements(".completePercentSelect[position="+po+"]").set("value","100")
                            }
                        }
                    });
                    completeSelect.set("value", d.isWorkCompleted?"yes":"no");
                    tmpDiv = new Element("div.completeSelectDiv",{
                        styles:this.css.completeSelectDiv
                    }).inject(tdNode);
                    var completePercentSelect = new Element("select.completePercentSelect",{
                        styles:this.css.completeSelect,
                        id:"completePercentSelect"+ d.id,
                        position:ii
                    }).inject(tmpDiv);
                    var completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"0%","value":"0"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"10%","value":"10"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"20%","value":"20"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"30%","value":"30"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"40%","value":"40"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"50%","value":"50"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"60%","value":"60"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"70%","value":"70"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"80%","value":"80"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"90%","value":"90"}).inject(completePercentSelect);
                    completePercentSelectOption = new Element("option.completePercentSelectOption",{"text":"100%","value":"100"}).inject(completePercentSelect);
                    //是否办结

                    completePercentSelect.set("value",parseInt(d.progressPercent));

                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    var teextareaNode = new Element("textarea.tetareaNode",{
                        "styles":this.view.css.textareaNode,
                        "id":"admin"+ d.id,
                        value: d.adminSuperviseInfo
                    }).inject(tdNode);


                     //管理员

                }else if(itemData.tabName ==this.view.lp.gatherName.leader){ //领导
                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    divNode = new Element("div.divNode",{
                        "styles":this.view.css.divNode,
                        "text": d.progressDescription,
                        "title": d.progressDescription
                    }).inject(tdNode); //截止当前
                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    divNode = new Element("div.divNode",{
                        "styles":this.view.css.divNode,
                        "text": d.workPlan,
                        "title": d.workPlan
                    }).inject(tdNode); //下一步要点

                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    divNode = new Element("div.divNode", {
                        "styles": this.view.css.divNode,
                        "text": d.isWorkCompleted?"是":"否"
                    }).inject(tdNode);
                    var percentComplete = new Element("div.percentComplete",{
                        "text":"完成率:"+ d.progressPercent+"%"
                    }).inject(divNode);


                    //是否办结


                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    divNode = new Element("div.divNode", {
                        "styles": this.view.css.divNode,
                        "text": d.adminSuperviseInfo,
                        "title": d.adminSuperviseInfo
                    }).inject(tdNode); //管理员
                    tdNode = new Element("td.tdNodeContent",{
                        "styles":this.view.css.tdNodeContent
                    }).inject(trNode);
                    var teextareaNode = new Element("textarea.tetareaNode",{
                        "styles":this.view.css.textareaNode,
                        "id":"opinion"+ d.id,
                        "value":this.lp.leaderDefaultOpinion
                    }).inject(tdNode); //领导

                }


                var tdNodeAction = new Element("td.tdNodeAction",{
                    "styles":this.view.css.tdNodeAction
                }).inject(trNode);
                var actionTxt = new Element("a.actionTxt",{
                    "styles":this.view.css.actionTxt,
                    "text":this.view.lp.viewSubmit
                }).inject(tdNodeAction)
                    .addEvents({
                        "click":function(e){
                            this.view.explorer.submit(d,e)
                        }.bind(this)
                    }); // 操作







                ////管理员
                //if(d.isWorkAdmin && d.processStatus == this.view.lp.activityName.manager){
                //
                //}else{
                //
                //}
                ////领导
                ////this.workReportData.processStatus == this.lp.activityName.leader && this.workReportData.isReadLeader && this.processIdentity.indexOf(this.app.identity)>-1
                //if(d.processStatus == this.view.lp.activityName.leader && d.isReadLeader && d.currentProcessorIdentity.indexOf(this.app.identity)>-1){
                //    tdNode = new Element("td.tdNodeContent",{
                //        "styles":this.view.css.tdNodeContent
                //    }).inject(trNode);
                //    var teextareaNode = new Element("textarea.tetareaNode",{
                //        "styles":this.view.css.textareaNode,
                //        "id":"opinion"+ d.id
                //    }).inject(tdNode);
                //}else{
                //    tdNode = new Element("td.tdNodeContent",{
                //        "styles":this.view.css.tdNodeContent,
                //        "text": ""
                //    }).inject(trNode);
                //}
                //
                //var tdNodeAction = new Element("td.tdNodeAction",{
                //    "styles":this.view.css.tdNodeAction,
                //}).inject(trNode);
                //var actionTxt = new Element("a.actionTxt",{
                //    "styles":this.view.css.actionTxt,
                //    "text":this.view.lp.viewSubmit
                //}).inject(tdNodeAction)
                //    .addEvents({
                //        "click":function(){
                //            this.view.explorer.submit(d)
                //        }.bind(this)
                //    })


            }.bind(this));

            //this.view.viewNode.getElements("textarea").setStyle("height","100%")
        }

    }

});