MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Execution","Attachment",null,false);

MWF.xApplication.Execution.WorkForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "800",
        "height": "100%",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": false,
        "closeAction": true
    },
    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp.workForm;
        this.actions = this.app.restActions;
        this.path = "/x_component_Execution/$WorkForm/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};
        this.actions = actions;
    },
    load: function () {
        //isNew = false,重新获取一遍，isNew = new 是新建工作 新建判断都方在options里 有parentWorkId一定是有上级工作，如果是centerWorkId是第一层创建
        if(this.options.isNew){
            if(this.options.parentWorkId){
                this.actions.getBaseWorkInfo(this.options.parentWorkId,function(json){
                        if(json.data){
                            this.parentWorkData = json.data
                        }
                    }.bind(this),
                    function(xhr,text,error){
                        this.showErrorMessage(xhr,text,error)
                    }.bind(this),false
                )
            }else if(this.options.centerWorkId){
                this.data.centerId = this.options.centerWorkId
            }else if(this.data.centerWorkId) {
                this.data.centerId = this.data.centerWorkId
            }
        }else{
            if(this.data.id){
                this.id = this.data.id
            }else if(this.options.id){
                this.id = this.options.id
            }

            this.actions.getBaseWorkInfo(this.id,function(json){
                    if(json.data){
                        this.data = json.data
                    }
                }.bind(this),
                function(xhr,text,error){
                    this.showErrorMessage(xhr,text,error)
                }.bind(this),false
            )
        }

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
                "text": this.options.title + ( this.data.title ? ("-" + this.data.title ) : "" )
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
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
    _createTableContent: function () {
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle' lable='centerWorkTitle'></td>" +
            "   <td styles='formTableValue' item='centerWorkTitle' colspan='3'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='timeLimit'></td>" +
            "   <td styles='formTableValue' item='timeLimit'></td>" +
            "   <td styles='formTableTitle' lable='reportCycle'></td>" +
            "   <td styles='formTableValue'><span item='reportCycle'></span><span item='reportDay'></span></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='dutyDepartment'></td>" +
            "   <td styles='formTableValue' item='dutyDepartment'></td>" +
            "   <td styles='formTableTitle' lable='dutyPerson'></td>" +
            "   <td styles='formTableValue' item='dutyPerson'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='secondDepartment'></td>" +
            "   <td styles='formTableValue' item='secondDepartment'></td>" +
            "   <td styles='formTableTitle' lable='secondPerson'></td>" +
            "   <td styles='formTableValue' item='secondPerson'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='readReader'></td>" +
            "   <td styles='formTableValue' item='readReader' colspan='3'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableValue' colspan='4'>" +
            "       <div styles='formTableTitleDiv' lable='workSplitAndDescription'></div>" +
            "       <div styles='formTableValueDiv' item='workSplitAndDescription'></div>" +
            "   </td>" +
            "</tr><tr>" +
            "   <td styles='formTableValue' colspan='4'>" +
            "       <div styles='formTableTitleDiv' lable='specificActionInitiatives'></div>" +
            "       <div styles='formTableValueDiv' item='specificActionInitiatives'></div>" +
            "   </td>" +
            "</tr><tr>" +
            "</tr><tr>" +
            "   <td styles='formTableValue' colspan='4'>" +
            "       <div styles='formTableTitleDiv' lable='milestoneMark'></div>" +
            "       <div styles='formTableValueDiv' item='milestoneMark'></div>" +
            "   </td>" +
            "</tr><tr>" +
            "   <td styles='formTableValue' colspan='4'>" +
            "       <div styles='formTableValueDiv' item='attachments'></div>"+
            "   </td>" +
            "</tr>"+
            "</table>";
        this.formTableArea.set("html", html);

       this.loadForm();
    },
    loadForm: function(){
        if(this.parentWorkData){
            this.data.parentWorkId = this.parentWorkData.id;
            this.data.workDetail = this.parentWorkData.workDetail;
            this.data.centerId = this.parentWorkData.centerId;
        }
        this.form = new MForm(this.formTableArea, this.data, {
            style: "execution",
            isEdited: this.isEdited || this.isNew,
            itemTemplate: this.getItemTemplate(this.lp )
        },this.app);
        this.form.load();
        var taObj = this.formTableArea.getElements("textarea");
        taObj.setStyles({height:"70px"});

        this.attachmentArea = this.formTableArea.getElement("[item='attachments']");

        this.loadAttachment( this.attachmentArea );

        //部署过程
        this.tmpLp = this.lp.processInfo;
        this.processInfo = new Element("div.processInfo",{
            "styles":this.css.processInfoDiv
            //}).inject(this.detailContentNode,"top")
        }).inject(this.formTableArea);
        this.processInfoTitle = new Element("div.processInfoTitle",{
            "styles":this.css.processInfoTitleDiv,
            "text":this.tmpLp.title
        }).inject(this.processInfo);
        //alert(JSON.stringify(this.data.okrWorkAuthorizeRecords))
        this.processInfoContent = new Element("div.processInfoContent",{
            "styles":this.css.processInfoContent
        }).inject(this.processInfo);


        var tHead = "<table styles='processTable'>";
        var tBody = "<tr>";
        tBody+="<td styles='processTH'>"+this.tmpLp.operate+"</td>";
        tBody+="<td styles='processTH'>"+this.tmpLp.time+"</td>";
        tBody+="<td styles='processTH'>"+this.tmpLp.source.split('@')[0]+"</td>";
        tBody+="<td styles='processTH'>"+this.tmpLp.target.split('@')[0]+"</td>";
        tBody+="<td styles='processTH'>"+this.tmpLp.opinion+"</td>";
        tBody+="</tr>";
        if(this.data.workDeployAuthorizeRecords){
            this.data.workDeployAuthorizeRecords.each(function(d){

                tBody += "<tr>";
                tBody+="<td styles='processTD'>"+d.operationTypeCN+"</td>";
                tBody+="<td styles='processTD'>"+ d.operationTime+"</td>";
                tBody+="<td styles='processTD'>"+ d.source.split('@')[0]+"</td>";
                tBody+="<td styles='processTD'>"+ d.target.split('@')[0]+"</td>";
                tBody+="<td styles='processTD'>"+d.opinion+"</td>";
                tBody+="</tr>"


            }.bind(this))
        }
        var tBottom = "</table>";
        this.processInfoContent.set("html",tHead+tBody+tBottom);
        this.formatStyles(this.processInfoContent);


    },
    formatStyles:function(obj){
        obj.getElements("[styles]").each(function(el){
            var styles = el.get("styles");
            if( styles && this.css[styles] ){
                el.setStyles( this.css[styles] )
            }
        }.bind(this))
    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            centerWorkTitle:{
                text: lp.centerWorkTitle+":",
                type :"innerText",
                value : this.data.centerWorkInfo?this.data.centerWorkInfo.title:(this.data.centerWorkTitle?this.data.centerWorkTitle.title:"")
            },
            workType: {
                text: lp.workType + ":",
                    type: "select",
                    notEmpty:true,
                    selectValue: lp.workTypeValue.split(",")
            },
            workLevel: {
                text: lp.workLevel + ":",
                    type: "select",
                    notEmpty:true,
                    selectValue: lp.workLevelValue.split(",")
            },
            timeLimit: {text: lp.timeLimit + ":", tType: "date",name:"completeDateLimitStr",notEmpty:true,attr : {readonly:true}},
            reportCycle: {
                text: lp.reportCycle + ":",
                    type: "select",
                    notEmpty:true,
                    //selectValue: lp.reportCycleValue.split(","),
                    selectText: lp.reportCycleText.split(","),
                    className: (_self.isEdited || _self.isNew) ? "inputSelectUnformatWidth":"",
                    event: {
                    change: function (item, ev) {
                        if (item.get("value") == lp.reportCycleText.split(",")[0]) {
                            this.form.getItem("reportDay").resetItemOptions(lp.weekDayValue.split(","),lp.weekDayText.split(","))
                        } else if (item.get("value") == lp.reportCycleText.split(",")[1]) {
                            this.form.getItem("reportDay").resetItemOptions(lp.monthDayValue.split(","),lp.monthDayText.split(","))
                        }
                    }.bind(this)
                   }
            },
            reportDay: {
                type: "select",
                name:"reportDayInCycle",
                notEmpty:true,
                selectValue: (!this.data.reportCycle || this.data.reportCycle==lp.reportCycleText.split(",")[0])?lp.weekDayValue.split(","):lp.monthDayValue.split(","), //lp.weekDayValue.split(","),
                selectText:  (!this.data.reportCycle || this.data.reportCycle==lp.reportCycleText.split(",")[0])?lp.weekDayText.split(","):lp.monthDayText.split(","),
                className: (_self.isEdited || _self.isNew) ? "inputSelectUnformatWidth":""
            },
            dutyDepartment: {
                type:"org",
                orgType:"unit",
                text: lp.dutyDepartment + ":",
                name:"responsibilityUnitName",
                notEmpty:true,
                attr : {readonly:true},
                event:{
                    "change":function(item){
                        var department = item.getValue(",");
                        if( department ){
                            _self.getDepartmentLeader( department, function( leader ){
                                _self.form.getItem("dutyPerson").setValue(leader);
                            })
                        }

                    }
                }
            },
            dutyPerson: {
                type:"org",
                orgType:"identity",
                text: lp.dutyPerson + ":",
                count:1,
                name:"responsibilityIdentity",
                notEmpty:true,
                attr : {readonly:true},
                onQuerySelect : function( item ){
                    var department = this.form.getItem("dutyDepartment").getValue();
                    //item.options.departments = department ? [department] : [] ;
                    department = typeOf(department)=="string" ? [department] : department;
                    var list = [];
                    (department || []).each( function(d){
                        return list.push( d.split("@")[0] );
                    });
                    item.options.units = list || [] ;
                }.bind(this)
            },
            secondDepartment: {
                text: lp.secondDepartment + ":",
                type: "org",
                orgType:"unit",
                name:"cooperateUnitNameList",
                value:this.cooperateUnitNameList?this.cooperateUnitNameList.join(","):"",
                count: 0,
                attr : {readonly:true},
                event:{
                "change":function(item){
                    var deptstr = item.getValue(",");
                    if(deptstr){
                        var depts = deptstr.split(",");
                        var users = "";
                        for(var i=0;i<depts.length;i++){
                            if(depts[i]!=""){
                                _self.getDepartmentLeader( depts[i], function( leader ){
                                    if(users=="") users = leader;
                                    else users = users + ","+leader
                                })
                            }
                        }
                        _self.form.getItem("secondPerson").setValue(users);
                    }
                }
            }},
            secondPerson: {
                text: lp.secondPerson + ":", type:"org",orgType: "identity",
                name:"cooperateIdentityList",
                value:this.cooperateIdentity?this.cooperateIdentity.join(","):"",
                count: 0,attr : {readonly:true}
            },
            readReader: {
                text: lp.readReader + ":",type:"org", orgType: "identity",
                name:"readLeaderIdentityList",
                value:this.readLeaderIdentity?this.readLeaderIdentity.join(","):"",
                attr : {readonly:true},count: 0
            },
            subject: {text: lp.subject + ":",name:"title",notEmpty:true},
            workSplitAndDescription: {text: lp.workSplitAndDescription + ":", type: "textarea",name:"workDetail",notEmpty:true},
            specificActionInitiatives: {text: lp.specificActionInitiatives + ":", type: "textarea",name:"progressAction"},
            cityCompanyDuty: {text: lp.cityCompanyDuty + ":", type: "textarea",name:"dutyDescription"},
            milestoneMark: {text: lp.milestoneMark + ":", type: "textarea",name:"landmarkDescription"},
            importantMatters: {text: lp.importantMatters + ":", type: "textarea",name:"majorIssuesDescription"}

        }
    },
    loadAttachment: function( area ){
        this.attachment = new MWF.xApplication.Execution.Attachment( area, this.app, this.actions, this.app.lp, {
            size:this.options.isNew?"max":"min",
            documentId : this.data.id,
            isNew : this.options.isNew,
            isEdited : this.options.isEdited,
            onQueryUploadAttachment : function(){
                this.attachment.isQueryUploadSuccess = true;
                if( !this.data.id || this.data.id=="" ){
                    var data = this.form.getResult(true, ",", true, false, true);
                    if( !data ){
                        this.attachment.isQueryUploadSuccess = false;
                        return;
                    }
                    if(this.options.isNew){
                        data.centerId = this.options.centerWorkId || this.data.centerWorkId || this.data.centerId ;
                    }
                    if(data.cooperateUnitNameList == ""){
                        data.cooperateUnitNameList = [];
                    }else{
                        data.cooperateUnitNameList = data.cooperateUnitNameList.split(",");
                    }
                    if(data.cooperateIdentityList == ""){
                        data.cooperateIdentityList = [];
                    }else{
                        data.cooperateIdentityList = data.cooperateIdentityList.split(",");
                    }
                    if(data.readLeaderIdentityList == ""){
                        data.readLeaderIdentityList = [];
                    }else{
                        data.readLeaderIdentityList = data.readLeaderIdentityList.split(",");
                    }
                    this.app.restActions.saveTask(data, function(json){
                        if(json.type && json.type == "success"){
                            if(json.data.id) {
                                this.attachment.options.documentId = json.data.id;
                                this.data.id = json.data.id;
                                //this.options.isNew = false;
                            }
                        }
                    }.bind(this), function(xhr,text,error){
                        this.showErrorMessage(xhr,text,error)
                    }.bind(this),false)
                }
            }.bind(this)
        });

        this.attachment.load();
    },
    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.app.lp.cancel
        }).inject(this.formBottomNode);


        this.cancelActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

        if ((this.isNew || this.isEdited) && this.options.actionStatus == "save" ) {
            this.okActionNode = new Element("div.formOkActionNode", {
                "styles": this.css.formOkActionNode,
                "text": this.app.lp.ok
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.ok(e);
            }.bind(this));
        }

        if(this.options.actionStatus=="deploy"){
            this.deployActionNode = new Element("div.formDeployActionNode",{
                "styles": this.css.formOkActionNode,
                "text" : this.app.lp.deploy
            }).inject(this.formBottomNode)
                .addEvent("click",function(){
                    this.deploy();
                }.bind(this))
        }
    },
    deploy: function(){
        //先调用保存，再部署
        var data = this.form.getResult(true, ",", true, false, true);
        if (data) {
            if(data.cooperateUnitNameList == ""){
                data.cooperateUnitNameList = [];
            }else{
                data.cooperateUnitNameList = data.cooperateUnitNameList.split(",");
            }
            if(data.cooperateIdentityList == ""){
                data.cooperateIdentityList = [];
            }else{
                data.cooperateIdentityList = data.cooperateIdentityList.split(",");
            }
            if(data.readLeaderIdentityList == ""){
                data.readLeaderIdentityList = [];
            }else{
                data.readLeaderIdentityList = data.readLeaderIdentityList.split(",");
            }

            data.title = data.workDetail;
            data.deployerName = this.app.user;
            data.creatorName = this.app.user;
            data.centerId = this.data.centerId;

            this.app.createShade();
            this.app.restActions.saveTask(data,function(json){
                if(json.type && json.type == "success"){
                    if(json.data.id){
                        var ids = [];
                        ids.push(json.data.id);
                        var workData = {  "workIds":ids };

                        this.actions.deployBaseWork( workData, function( json ){
                            if(json.type && json.type=="success"){
                                this.app.notice(this.app.lp.WorkDeploy.deployeSuccess, "ok");
                                //this.reloadContent();
                                this.close();

                            }
                            this.fireEvent("postDeploy", json);
                            this.app.destroyShade();
                        }.bind(this),function(xhr,error,text){
                            this.app.destroyShade();
                            this.app.showErrorMessage(xhr,error,text)
                        }.bind(this));
                    }
                }
            }.bind(this),function(xhr,error,text){
                this.app.showErrorMessage(xhr,error,text)
            }.bind(this))
        }
    },
    _ok: function (data, callback) {

        if(data.cooperateUnitNameList == ""){
            data.cooperateUnitNameList = [];
        }else{
            data.cooperateUnitNameList = data.cooperateUnitNameList.split(",");
        }
        if(data.cooperateIdentityList == ""){
            data.cooperateIdentityList = [];
        }else{
            data.cooperateIdentityList = data.cooperateIdentityList.split(",");
        }
        if(data.readLeaderIdentityList == ""){
            data.readLeaderIdentityList = [];
        }else{
            data.readLeaderIdentityList = data.readLeaderIdentityList.split(",");
        }

        this.app.createShade();
        this.app.restActions.saveTask(data,function(json){
            if(json.type && json.type=="success"){
                this.app.notice(this.lp.submitSuccess, "ok");
                if(this.options.tabLocation){
                    //if(this.options.from == "view"){
                        //if(this.app.workTask.contentDiv)  this.app.workTask.contentDiv.destroy()
                        if(this.app.workTask)this.app.workTask.loadBaseWorkList(this.options.tabLocation);
                        if(this.app.workList)this.app.workList.loadBaseWorkList(this.options.tabLocation);
                    //}

                }else{
                    if(this.explorer && this.explorer.explorer && this.explorer.explorer.reloadList){
                        this.explorer.explorer.reloadList();
                    }
                }


                this.close();
            }
            this.app.destroyShade();
            this.fireEvent("postSave", json);
        }.bind(this),function(xhr,text,error){
            this.app.destroyShade();
            var errorText = error;
            if (xhr) errorMessage = xhr.responseText;
            var e = JSON.parse(errorMessage);
            if(e.message){
                this.app.notice( e.message,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }.bind(this));
        //this.app.restActions.saveDocument( this.data.id, data, function(json){
        //    if( callback )callback(json);
        //}.bind(this));
    },
    getDepartmentLeader:function(department, callback){
        var data = {};
        data.name =this.app.lp.departmentLeader;
        data.unit = department;

        this.app.orgActions.getDutyValue(data,function(json){
                if(json.data.identityList && json.data.identityList.length>0){
                    if(callback)callback(json.data.identityList[0])
                }
        }.bind(this),null,false);
        //this.app.restActions.getDepartmentDuty(function( json ){
        //    if( json.data.identityList && json.data.identityList.length > 0 ){
        //        if(callback) callback(json.data.identityList[0])
        //        //this.app.restActions.getPersonByIdentity( function(data){
        //        //    if(callback)callback(data.data.name);
        //        //}.bind(this),null, json.data.identityList[0] ,false)
        //    }
        //}.bind(this),null,this.app.lp.departmentLeader, department, false )
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
            this.app.notice(errorText,"error")
        }

    }
});


