MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Execution","Attachment",null,false);
MWF.xDesktop.requireApp("Execution","ReportAttachment",null,false);

MWF.xApplication.Execution.WorkReport = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
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
        this.lp = this.app.lp.WorkReport;
        this.actions = this.app.restActions;
        this.path = "/x_component_Execution/$WorkReport/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();
        //this.container = this.options.container||this.app.content;

        this.options.title = this.lp.title;

        this.data = data || {};

        this.actions = actions;
    },
    load: function () {
        if (this.options.isNew) {
            this.create();
        } else if (this.options.isEdited) {
            this.edit();
        } else {
            this.open();
        }

        this.setContentSize();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);

        if(this.workReportData){
            //百分比
            if(this.completeProgressContentDiv){
                var obj = this.completeProgressContentDiv.getElements(".completeProgressLineDiv");
                obj.setStyles({"background":"#ccc"});
                var curLen = parseInt(this.workReportData.progressPercent/10);
                obj.each(function(d,j){
                    if(j<curLen){
                        d.setStyles({"background":"#369"})
                    }
                })
            }

            //是否完成
            if(this.completeSelect){
                this.completeSelect.set("value",this.workReportData.isWorkCompleted?"yes":"no")
            }
        }

    },
    setContentSize: function () {
        var allSize = this.app.content.getSize();

        this.reportContentInforHeight = ( allSize.y - (this.formTopNode.getSize().y ) );
        //alert(reportContentInforHeight)
        if(this.prevReportInforListDiv){
            this.prevReportInforListDiv.setStyles({
                "height": "" + this.reportContentInforHeight + "px"
            });
            }

    },
    reload:function(data){

    },
    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopIconNode = new Element("div", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div.formTopTextNode", {
                "styles": this.css.formTopTextNode,
                "text": this.lp.topTitle + ( this.data.title ? ("-" + this.data.title ) : "" )
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
    createContent: function () {
        this.formContentNode = new Element("div.formContentNode", {
            "styles": this.css.formContentNode
        }).inject(this.formNode);



        this.formTableContainer = new Element("div.formTableContainer", {
            "styles": this.css.formTableContainer
        }).inject(this.formContentNode);


        this.formTableArea = new Element("div.formTableArea", {
            "styles": this.css.formTableArea
        }).inject(this.formTableContainer);

        this.reportLinksDiv = new Element("div.reportLinksDiv",{
            "styles":this.css.reportLinksDiv,
            "text":this.lp.reportLinks
        }).inject(this.formTableArea)
            .addEvents({
                "click":function(){
                    this.createPrevReport();
                }.bind(this)
            });

        this.titleDiv = new Element("div.titleDiv",{
            "styles":this.css.titleDiv,
            "text":this.lp.topTitle + ( this.data.title ? ("-" + this.data.title ) : "" ),
            "title":this.lp.topTitle + ( this.data.title ? ("-" + this.data.title ) : "" )
        }).inject(this.formTableArea);


        this.centerWorkDiv = new Element("div.centerWorkDiv",{"styles":this.css.centerWorkDiv}).inject(this.formTableArea);
        this.centerWorkTitleDiv = new Element("div.centerWorkTitleDiv",{
            "styles":this.css.tabTitleDiv,
            "text":this.lp.title
        }).inject(this.centerWorkDiv);

        this.tableContentDiv = new Element("div.tableContentDiv").inject(this.formTableArea);



        if(this.options.workReportId){
            this.workReportId = this.options.workReportId;
        }

        this.workId = this.data.workId;
        this.processStatus = "";
        this.processIdentity = "";
        if(this.options.from && this.options.from == "drafter"){
            this.actions.workReportDrafter(this.data.workId, function( json ){
                if(json.type && json.type=="success"){
                    this.workReportData = json.data;
                    if(json.data.id){
                        this.workReportId = json.data.id;
                    }

                    if(json.data.currentProcessorIdentityList){
                        this.processIdentity = json.data.currentProcessorIdentityList
                    }
                    if(json.data.processStatus){
                        this.processStatus = json.data.processStatus
                    }
                }
            }.bind(this),null,false);
        }else{  //不是草稿的 直接获取this.data信息
            this.actions.getWorkReport(this.data.workReportId,function(json){ //alert(JSON.stringify(json))
                if(json.type=="success"){
                    this.workReportData = json.data;
                    if(json.data.id){
                        this.workReportId = json.data.id
                    }
                }
            }.bind(this),null,false);

            if(this.workReportData.currentProcessorIdentityList){
                this.processIdentity = this.workReportData.currentProcessorIdentityList
            }
            if(this.workReportData.processStatus){
                this.processStatus = this.workReportData.processStatus
            }
        }

        //alert("workreportData="+JSON.stringify(this.workReportData))
        //获取工作信息
        if(this.data.workId){
            this.actions.getTask(this.workId, function(json){
                if(json.data){
                    this.workData = json.data;
                }
            }.bind(this),null,false);

        }
        //alert("this.workReportId="+this.workReportId)
        //alert("this.workId="+this.workId)
        //获取具体工作详细信息
        if(this.data.workId){
            this.actions.getBaseWorkDetails(this.workId, function (json) {
                this.workData.workSplitAndDescription = json.data.workDetail;
                this.workData.specificActionInitiatives = json.data.progressAction;
                this.workData.cityCompanyDuty = json.data.dutyDescription;
                this.workData.milestoneMark = json.data.landmarkDescription;
                this.workData.importantMatters = json.data.majorIssuesDescription;
            }.bind(this),null,false);
        }

        this._createTableContent();

        if(this.workReportData.title){
            this.titleDiv.set("text",this.workReportData.title.length>50?this.workReportData.title.substr(0,50)+"...":this.workReportData.title);
            this.titleDiv.set("title",this.workReportData.title);
        }

        //委派记录
        if(this.workData.okrWorkAuthorizeRecords){
        //if(this.workData.workDeployAuthorizeRecords){
            this.appointContentDiv = new Element("div.appointContentDiv",{
                "styles":this.css.appointContentDiv
            }).inject(this.formTableArea);
            this.appointContentTitleDiv = new Element("div.appointContentTitleDiv",{
                "styles":this.css.tabTitleDiv,
                "text":this.lp.appointTitle
            }).inject(this.appointContentDiv);
            this.appointContentInfor = new Element("div.appointContentInfor",{
                "styles": this.css.appointContentInfor
            }).inject(this.appointContentDiv);


            this.workData.okrWorkAuthorizeRecords.each(function(d){
                if(d.operationType == "AUTHORIZE"){
                    var ttext = d.source.split("@")[0]+this.lp.appointFor+ d.target.split("@")[0];
                    ttext += "("+ d.operationTime+") ";
                    ttext += "意见：" + d.opinion;
                }
                if(d.operationType == "TACKBACK"){
                    var ttext = d.source.split("@")[0]+this.lp.appointBack;
                    ttext += "("+ d.operationTime+") ";
                    ttext += "意见：" + d.opinion;
                }

                this.appointRecordDiv = new Element("div.appointRecordDiv",{
                    "styles":this.css.appointRecordDiv,
                    "text": ttext
                }).inject(this.appointContentInfor)
            }.bind(this))

        }

        //判断状态 如果草稿并且当前人是拟稿人，显示contentTextarea1，contentTextarea2 编辑状态 其他只读
        //当前秘书状态、并且当前人是秘书 contentTextarea3 编辑，其他只读
        //当前领导、并且当前人领导（当前处理人）:contentTextarea4编辑 其他只读

        //拟稿人填写
        this.reportContentDiv = new Element("div.centerWorkDiv",{"styles":this.css.reportContentDiv}).inject(this.formTableArea);
        this.reportContentTitleDiv = new Element("div.reportContentTitleDiv",{
            "styles":this.css.tabTitleDiv,
            "text":this.lp.reportContentTitle
        }).inject(this.reportContentDiv);
        this.reportContentInfor = new Element("div.reportContentInfor",{
            "styles": this.css.reportContentInfor
        }).inject(this.reportContentDiv);
        this.contentTitle1 = new Element("div.contentTitle1",{
            "styles":this.css.contentTitle,
            "text":this.lp.contentTitle1 + "："
        }).inject(this.reportContentInfor);


        if(this.workReportData.processStatus == this.lp.activityName.drafter && this.workReportData.isReporter){
            this.contentTextarea1 = new Element("textarea.contentTextarea1",{
                "styles":this.css.contentTextarea,
                "text" : this.workReportData.progressDescription?this.workReportData.progressDescription:""
            }).inject(this.reportContentInfor);
        }else{
            this.contentTextStr1 = new Element("div.contentTextStr1",{
                "styles": this.css.contentTextStr,
                "text" : this.workReportData.progressDescription?this.workReportData.progressDescription:""
            }).inject(this.reportContentInfor)
        }
        this.contentTitle2 = new Element("div.contentTitle2",{
            "styles":this.css.contentTitle,
            "text":this.lp.contentTitle2 + "："
        }).inject(this.reportContentInfor);

        if(this.workReportData.processStatus == this.lp.activityName.drafter && this.workReportData.isReporter){
            this.contentTextarea2 = new Element("textarea.contentTextarea2",{
                "styles":this.css.contentTextarea,
                "text" : this.workReportData.workPlan?this.workReportData.workPlan:""
            }).inject(this.reportContentInfor);
        }else{
            this.contentTextStr2 = new Element("div.contentTextStr2",{
                "styles" : this.css.contentTextStr,
                "text" : this.workReportData.workPlan?this.workReportData.workPlan:""
            }).inject(this.reportContentInfor)
        }

        //是否办结
        this.completeDiv = new Element("div.completeDiv",{
            "styles":this.css.completeDiv
        }).inject(this.reportContentInfor);
        this.completeTextSpan = new Element("span.completeTextSpan",{
            "styles":this.css.completeTextSpan,
            "text":this.lp.isCompleted
        }).inject(this.completeDiv);
        //if(this.processIdentity == this.app.identity || this.processIdentity.indexOf(this.app.identity)>-1) {
        if((this.workReportData.processStatus == this.lp.activityName.drafter && this.workReportData.isReporter)|| (this.workReportData.processStatus == this.lp.activityName.manager && (this.processIdentity == this.app.identity || this.processIdentity.indexOf(this.app.identity)>-1))){
            this.completeSelect = new Element("select.completeSelect",{
                styles:this.css.completeSelect
            }).inject(this.completeDiv);
            //this.completeSelectOption = new Element("option.completeSelectOption",{"text":"","value":"no"}).inject(this.completeSelect)
            this.completeSelectOption = new Element("option.completeSelectOption",{"text":this.lp.isCompletedNoOption,"value":"no"}).inject(this.completeSelect);
            this.completeSelectOption = new Element("option.completeSelectOption",{"text":this.lp.isCompletedYesOption,"value":"yes"}).inject(this.completeSelect);
            this.completeSelect.addEvents({
                "change":function(){
                    if(this.completeSelect.get("value")=="yes"){
                        var obj = this.completeProgressContentDiv.getElements(".completeProgressLineDiv");
                        obj.setStyles({"background":"#369"});
                        this.completePercentRateSpan.set("text","100%");

                    }else{
                        this.completePercentRateSpan.set("text",parseInt(this.workReportData.progressPercent)+"%");
                        var obj = this.completeProgressContentDiv.getElements(".completeProgressLineDiv");
                        obj.setStyles({"background":"#ccc"});
                        var curLen = parseInt(this.workReportData.progressPercent/10);
                        obj.each(function(d,j){
                            if(j<curLen){
                                d.setStyles({"background":"#369"});
                            }
                        })
                    }
                }.bind(this)
            })

        }else{
            var tmpstr = this.workReportData.isWorkCompleted?this.lp.isCompletedYesOption:this.lp.isCompletedNoOption;
            this.completeTextSpan.set("text",this.completeTextSpan.get("text")+" "+tmpstr)
        }

        this.completePercentSpan = new Element("span.completePercentSpan",{
            "styles":this.css.completePercentSpan,
            "text":this.lp.completePercent
        }).inject(this.completeDiv);
        this.completePercentRateSpan = new Element("span.completePercentRateSpan",{
            "id":"completePercentRateSpan",
            "styles":this.css.completePercentRateSpan,
            "text":parseInt(this.workReportData.progressPercent)+"%"
        }).inject(this.completeDiv);



        if((this.workReportData.processStatus == this.lp.activityName.drafter && this.workReportData.isReporter)|| (this.workReportData.processStatus == this.lp.activityName.manager && (this.processIdentity == this.app.identity || this.processIdentity.indexOf(this.app.identity)>-1))){
            this.completeProgressDiv = new Element("div.completeProgressDiv",{
                "styles":this.css.completeProgressDiv
            }).inject(this.completeDiv);

            this.completeProgressContentDiv = new Element("div.completeProgressContentDiv",{
                "styles":this.css.completeProgressContentDiv
            }).inject(this.completeProgressDiv);

            var _self = this;
            for(i=0;i<11;i++){
                var tmpPointDiv = new Element("div.completeProgressPointDiv",{
                    "styles":this.css.completeProgressPointDiv,
                    "position":i
                }).inject(_self.completeProgressContentDiv);
                tmpPointDiv.setStyles({"left":(i*50)+"px"});
                tmpPointDiv.addEvents({
                    "click":function(){
                        _self.selectProgress(parseInt(this.get("position")),"point")
                    }
                });
                if(i<10){
                    var tmpLineDiv = new Element("div.completeProgressLineDiv",{
                        "styles":this.css.completeProgressLineDiv,
                        "position":i
                    }).inject(_self.completeProgressContentDiv);
                    tmpLineDiv.setStyles({"left":(i*50)+"px"});
                    tmpLineDiv.addEvents({
                        "click":function(){
                            _self.selectProgress(parseInt(this.get("position"))+1,"line");
                        }
                    })
                }

            }

            this.completeProgressTextDiv = new Element("div.completeProgressTextDiv",{
                "styles":this.css.completeProgressTextDiv
            }).inject(this.completeProgressDiv);
            for(i=0;i<11;i++){
                var tmpCompletePercentTextSpan = new Element("lable.tmpCompletePercentTextSpan",{
                    "styles":this.css.tmpCompletePercentTextSpan,
                    "text":(i*10)+"%"
                }).inject(this.completeProgressTextDiv)
            }
        }

        //是否办结

        this.reportAttachment = new Element("div.reportAttachment",{
            "item":"reportAttachments"
        }).inject(this.reportContentInfor);
        this.reportAttachment.setStyles({"width":"95%"});

        var isUpload = false;
        if(this.workReportData.processStatus == this.lp.activityName.drafter && this.workReportData.isReporter){
            isUpload = true
        }
        this.reportAttachmentArea = this.formTableArea.getElement("[item='reportAttachments']");
        this.loadReportAttachment( this.reportAttachmentArea,isUpload );

        //获取秘书及领导评价信息
        var opinionData = {};
        opinionData.workReportId = "";
        //this.actions.getWorkReportOpinion();

        //管理员填写
        if(this.workReportData.needAdminAudit){
            this.createAdminContent();
        }


        //领导填写,在草稿和督办员环节不显示
        if(this.workReportData && (this.workReportData.activityName != this.lp.activityName.drafter && this.workReportData.activityName != this.lp.activityName.manager)){
            this.reportContentDiv = new Element("div.centerWorkDiv",{"styles":this.css.reportContentDiv}).inject(this.formTableArea);
            this.reportContentTitleDiv = new Element("div.reportContentTitleDiv",{
                "styles":this.css.tabTitleDiv,
                "text":this.lp.leaderContentTitle
            }).inject(this.reportContentDiv);
            this.reportContentInfor = new Element("div.reportContentInfor",{
                "styles": this.css.reportContentInfor
            }).inject(this.reportContentDiv);

            this.getLeaderOpinions();

            if(this.workReportData.processStatus == this.lp.activityName.leader && this.workReportData.isReadLeader && this.processIdentity.indexOf(this.app.identity)>-1){
                this.contentTextarea4 = new Element("textarea.contentTextarea4",{
                    "styles":this.css.contentTextarea,
                    "text" : this.leaderOpinionDrafter?this.leaderOpinionDrafter:""
                }).inject(this.reportContentInfor);
            }else{
                //if(this.workReportData.reportWorkflowType && this.workReportData.reportWorkflowType == "DEPLOYER"){
                //    //一对一，上下级
                //    this.contentTextStr4 = new Element("div.contentTextStr4",{
                //        "styles": this.css.contentTextStr,
                //        "text":"意见"
                //    }).inject(this.reportContentInfor)
                //}else{
                //    this.getLeaderOpinions();
                //}

            }

        }


        //权限控制，如果已归档，则输入框去掉
        if(this.workReportData && this.workReportData.status == this.lp.statuArchive){
            if(this.contentTextarea1)this.contentTextarea1.destroy();
            if(this.contentTextarea2)this.contentTextarea2.destroy();
            if(this.contentTextarea3)this.contentTextarea3.destroy();
            if(this.contentTextarea4)this.contentTextarea4.destroy();

        }

    },
    selectProgress:function(i,s){
        var obj = this.completeProgressContentDiv.getElements(".completeProgressLineDiv");
        obj.setStyles({"background":"#ccc"});
        obj.each(function(d,j){
            if(j<i){
                d.setStyles({"background":"#369"})
            }
        });
        this.completePercentRateSpan.set("text",(i*10)+"%");
        if(i==10 && this.completeSelect){
            this.completeSelect.set("value","yes")
        }
        if(i<10 && this.completeSelect){
            this.completeSelect.set("value","no")
        }
    },
    getLeaderOpinions: function(){
        //获取领导意见
        var logs = this.workReportData.processLogs;
        this.leaderTitle = [];
        this.leaderValue = [];
        if(logs){
            logs.each(function(data){
                if(data.activityName == this.lp.activityName.leader && data.processStatus == this.lp.status.drafter && data.processorIdentity == this.app.identity){
                    this.leaderOpinionDrafter = data.opinion
                }else{
                    if(data.activityName == this.lp.activityName.leader && data.processStatus == this.lp.status.effect){
                        this.leaderTitle.push(data.processorIdentity.split("@")[0]+"("+data.processTimeStr+")");
                        this.leaderValue.push(data.opinion )
                    }
                }
            }.bind(this))
        }
        //领导意见显示区域
        this.reportLeaderOpinionsDiv = new Element("div.reportLeaderOpinionsDiv",{
            "styles":this.css.reportLeaderOpinionsDiv
        }).inject(this.reportContentInfor);
        for(var i=0;i<this.leaderTitle.length;i++){
            var reportLeaderContentDiv = new Element("div.reportLeaderContentDiv",{"styles":this.css.reportLeaderContentDiv}).inject(this.reportLeaderOpinionsDiv);
            var reportLeaderTitleDiv = new Element("div.reportLeaderTitleDiv",{
                "styles":this.css.reportLeaderTitleDiv,
                "text":this.leaderTitle[i]+":"
            }).inject(reportLeaderContentDiv);
            var reportLeaderValueDiv = new Element("div.reportLeaderValueDiv",{
                "styles":this.css.reportLeaderValueDiv,
                "text":this.leaderValue[i]
            }).inject(reportLeaderContentDiv);
        }

    },
    createPrevReport: function(){
        if(this.prevReportDiv) this.prevReportDiv.destroy();
        this.prevReportDiv = new Element("div.prevReportDiv",{
            styles:this.css.prevReportDiv
        }).inject(this.formTableContainer);

        this.prevReportTopDiv = new Element("div.prevReportTopDiv",{
            "styles":this.css.prevReportTopDiv
        }).inject(this.prevReportDiv);
        this.prevReportTopTitleDiv = new Element("div.prevReportTopTitleDiv",{
            "styles": this.css.prevReportTopTitleDiv,
            "text" : this.lp.reportLinks+"："
        }).inject(this.prevReportTopDiv);
        this.prevReportTopCloseDiv = new Element("div.prevReportTopCloseDiv",{
            "styles": this.css.prevReportTopCloseDiv
        }).inject(this.prevReportTopDiv)
            .addEvents({
                "click": function(){
                    this.prevReportDiv.destroy();
                }.bind(this)
            });
        this.prevReportListDiv = new Element("div.prevReportListDiv",{
            "styles":this.css.prevReportListDiv
        }).inject(this.prevReportDiv);

        this.actions.getWorkReportList(this.workReportData.workId, function( json ){
            if(json.type && json.type=="success" && json.data){
                json.data.each(function(data){
                    var createTimes = data.createTime.split(" ")[0];

                     var prevReportWorkId = data.id;
                     var prevReportListLi = new Element("li.prevReportListLi",{
                        "styles": this.css.prevReportListLi,
                         "id" : prevReportWorkId,
                        "text": createTimes + "-" + data.shortTitle
                    }).inject(this.prevReportListDiv)
                        .addEvents({
                            "mouseover":function(){
                                prevReportListLi.setStyle("background-color","#3c76c1");
                            }.bind(this),
                            "mouseout":function(){
                                if(prevReportWorkId != this.currentPrevReportLinkId){
                                    prevReportListLi.setStyle("background-color","");
                                }
                            }.bind(this),
                             "click" :function(){
                                 this.prevReportTopCloseDiv.setStyle("display","none");

                                this.expandWorkReportInfor(prevReportListLi);
                             }.bind(this)
                        })
                }.bind(this))
            }
        }.bind(this),null,false);

        //this.createPrevReportInfor();

    },
    createPrevReportInfor : function(workReportId){
        if(this.prevReportInforDiv) this.prevReportInforDiv.destroy();

        this.prevReportInforDiv = new Element("div.prevReportInforDiv",{
            "styles": this.css.prevReportInforDiv
        }).inject(this.formTableContainer);

        this.prevReportInforTopDiv = new Element("div.prevReportInforTopDiv",{
            "styles":this.css.prevReportInforTopDiv
        }).inject(this.prevReportInforDiv);

        this.prevReportInforTopCloseDiv = new Element("div.prevReportInforTopCloseDiv",{
            "styles": this.css.prevReportInforTopCloseDiv
        }).inject(this.prevReportInforTopDiv)
            .addEvents({
                "click": function(){
                    this.prevReportDiv.destroy();
                    this.prevReportInforDiv.destroy();
                }.bind(this)
            });
        this.prevReportInforListDiv = new Element("div.prevReportInforListDiv",{
            "styles":this.css.prevReportInforListDiv
        }).inject(this.prevReportInforDiv);
        this.prevReportInforListDiv.setStyles({"height":this.reportContentInforHeight+"px"})

        //这里显示具体内容
        this.actions.getWorkReport(workReportId,function(json){
            //alert(JSON.stringify(json))
            if(json.type == "success"){
                var prevContentDiv = new Element("div.prevContentDiv",{
                    "styles": this.css.prevContentDiv
                }).inject(this.prevReportInforListDiv);
                var prevContentTitleDiv = new Element("div.prevContentTitleDiv",{
                    "styles" : this.css.prevContentTitleDiv,
                    "text" : this.lp.contentTitle1 + ":"
                }).inject(prevContentDiv);
                var prevContentValueDiv = new Element("div.prevContentValueDiv",{
                    "styles": this.css.prevContentValueDiv,
                    "text" : json.data.progressDescription
                }).inject(prevContentDiv);

                prevContentDiv = new Element("div.prevContentDiv",{
                    "styles": this.css.prevContentDiv
                }).inject(this.prevReportInforListDiv);
                prevContentTitleDiv = new Element("div.prevContentTitleDiv",{
                    "styles" : this.css.prevContentTitleDiv,
                    "text" : this.lp.contentTitle2 + ":"
                }).inject(prevContentDiv);
                prevContentValueDiv = new Element("div.prevContentValueDiv",{
                    "styles": this.css.prevContentValueDiv,
                    "text" : json.data.workPlan
                }).inject(prevContentDiv);
                //是否办结
                prevContentTitleDiv = new Element("div.prevContentTitleDiv",{
                    "styles" : this.css.prevContentTitleDiv,
                    "text" : this.lp.contentTitle2 + ":"
                }).inject(prevContentDiv);

                var tmpstr = json.data.isWorkCompleted?" "+this.lp.isCompletedYesOption+" ":" "+this.lp.isCompletedNoOption+" ";
                tmpstr = this.lp.isCompleted+":" + tmpstr;
                tmpstr = tmpstr + " " +this.lp.completePercent + " " + parseInt(json.data.progressPercent)+"%";
                prevContentTitleDiv.set("text",tmpstr);


                //管理员督办
                if(json.data.needAdminAudit){
                    prevContentDiv = new Element("div.prevContentDiv",{
                        "styles": this.css.prevContentDiv
                    }).inject(this.prevReportInforListDiv);
                    prevContentTitleDiv = new Element("div.prevContentTitleDiv",{
                        "styles" : this.css.prevContentTitleDiv,
                        "text" : this.lp.adminContentTitle + ":"
                    }).inject(prevContentDiv);
                    prevContentValueDiv = new Element("div.prevContentValueDiv",{
                        "styles": this.css.prevContentValueDiv,
                        "text" : json.data.adminSuperviseInfo?json.data.adminSuperviseInfo:""
                    }).inject(prevContentDiv);
                }

                //领导评价
                prevContentDiv = new Element("div.prevContentDiv",{
                    "styles": this.css.prevContentDiv
                }).inject(this.prevReportInforListDiv);
                prevContentTitleDiv = new Element("div.prevContentTitleDiv",{
                    "styles" : this.css.prevContentTitleDiv,
                    "text" : this.lp.leaderContentTitle + ":"
                }).inject(prevContentDiv);
                prevContentValueDiv = new Element("div.prevContentValueDiv",{
                    "styles": this.css.prevContentValueDiv
                }).inject(prevContentDiv);

                var reportLeaderOpinionsDiv = new Element("div.reportLeaderOpinionsDiv",{
                    "styles":this.css.reportLeaderOpinionsDiv
                }).inject(prevContentValueDiv);


                var preLogs = json.data.processLogs;
                this.preLeaderTitle = [];
                this.preLeaderValue = [];
                if(preLogs){
                    preLogs.each(function(data){
                        if(data.activityName == this.lp.activityName.leader && data.processStatus == this.lp.status.drafter && data.processorIdentity == this.app.identity){
                            this.leaderOpinionDrafter = data.opinion
                        }else{
                            if(data.activityName == this.lp.activityName.leader && data.processStatus == this.lp.status.effect){
                                this.preLeaderTitle.push(data.processorIdentity.split("@")[0]+"("+data.processTimeStr+")");
                                this.preLeaderValue.push(data.opinion )
                            }
                        }
                    }.bind(this))
                }


                for(var i=0;i<this.preLeaderTitle.length;i++){
                    var reportLeaderContentDiv = new Element("div.reportLeaderContentDiv",{"styles":this.css.reportLeaderContentDiv}).inject(reportLeaderOpinionsDiv);
                    reportLeaderContentDiv.setStyle("border-bottom","1px dashed #3c76c1");
                    var reportLeaderTitleDiv = new Element("div.reportLeaderTitleDiv",{
                        "styles":this.css.reportLeaderTitleDiv,
                        "text":this.preLeaderTitle[i]+":"
                    }).inject(reportLeaderContentDiv);
                    var reportLeaderValueDiv = new Element("div.reportLeaderValueDiv",{
                        "styles":this.css.reportLeaderValueDiv,
                        "text":this.preLeaderValue[i]
                    }).inject(reportLeaderContentDiv);
                }

                //附件
                prevContentDiv = new Element("div.prevContentDiv",{
                    "styles": this.css.prevContentDiv
                }).inject(this.prevReportInforListDiv);
                prevContentTitleDiv = new Element("div.prevContentTitleDiv",{
                    "styles" : this.css.prevContentTitleDiv,
                    "text" :   this.lp.attachment+":"
                }).inject(prevContentDiv);
                prevContentValueDiv = new Element("div.prevContentValueDiv",{
                    "styles": this.css.prevContentValueDiv
                }).inject(prevContentDiv);

                this.loadPreReportAttachment(prevContentValueDiv,workReportId);
            }
        }.bind(this),null,false);
        this.setScrollBar(this.prevReportInforListDiv);
    },
    loadPreReportAttachment: function( area,id ){
        this.attachment = new MWF.xApplication.Execution.ReportAttachment( area, this.app, this.actions, this.app.lp, {
            //documentId : this.data.workId,
            documentId : id,
            isNew : this.options.isNew,
            isEdited : false,
            "size":"min"
        });
        this.attachment.load();
    },
    expandWorkReportInfor:function(prevReportListLi){
        this.currentPrevReportLinkId = prevReportListLi.get("id");
        var liObj = this.prevReportListDiv.getElements("li");
        liObj.setStyle("background-color","");
        prevReportListLi.setStyle("background-color","#3c76c1");
        this.createPrevReportInfor(this.currentPrevReportLinkId);
        this.prevReportInforDiv.setStyle("display","");
    },
    createAdminContent: function(){
        this.reportContentDiv = new Element("div.centerWorkDiv",{"styles":this.css.reportContentDiv}).inject(this.formTableArea);
        this.reportContentTitleDiv = new Element("div.reportContentTitleDiv",{
            "styles":this.css.tabTitleDiv,
            "text":this.lp.adminContentTitle
        }).inject(this.reportContentDiv);
        this.reportContentInfor = new Element("div.reportContentInfor",{
            "styles": this.css.reportContentInfor
        }).inject(this.reportContentDiv);


        if(this.workReportData.processStatus == this.lp.activityName.manager && this.workReportData.isWorkAdmin){
            this.contentTextarea3 = new Element("textarea.contentTextarea3",{
                "styles":this.css.contentTextarea,
                "value":this.workReportData.adminSuperviseInfo?this.workReportData.adminSuperviseInfo:""
            }).inject(this.reportContentInfor);
        }else{
            this.contentTextStr3 = new Element("div.contentTextStr3",{
                "styles": this.css.contentTextStr,
                "text" : this.workReportData.adminSuperviseInfo?this.workReportData.adminSuperviseInfo:""
            }).inject(this.reportContentInfor)
        }
    },
    _createTableContent: function () {
        var html = "<table style='width:95%; margin:10px 40px; margin-bottom: 0px;' border='0'>" +
                    "<tr>"+
                    "   <td styles='formTableTitle' lable='deployPerson' width='10%'></td>" +
                    "   <td styles='formTableValue' item='deployPerson' width='20%'></td>" +
                    "   <td styles='formTableTitle' lable='timeLimit' width='10%'></td>" +
                    "   <td styles='formTableValue' item='timeLimit' width='20%'></td>" +
                    "   <td styles='formTableTitle' lable='' width='10%'></td>" +
                    "   <td styles='formTableValue' item='' width='20%'></td>" +
                    "</tr>"+
                    "<tr>"+
                    "   <td styles='formTableTitle' lable='dutyDepartment'></td>" +
                    "   <td styles='formTableValue' item='dutyDepartment'></td>" +
                    "   <td styles='formTableTitle' lable='dutyPerson'></td>" +
                    "   <td styles='formTableValue' item='dutyPerson'></td>" +
                    "   <td styles='formTableTitle' lable='reportCycle'></td>" +
                    "   <td styles='formTableValue'><span item='reportCycle'></span><span item='reportDay'></span></td>" +
                    "</tr>"+
                    "<tr>"+
                    "   <td styles='formTableTitle' lable='secondDepartment'></td>" +
                    "   <td styles='formTableValue' item='secondDepartment'></td>" +
                    "   <td styles='formTableTitle' lable='secondPerson'></td>" +
                    "   <td styles='formTableValue' item='secondPerson'></td>" +
                    "   <td styles='formTableTitle' lable='readReader'></td>" +
                    "   <td styles='formTableValue' item='readReader'></td>" +
                    "</tr>"+
                    "</table>"+
                    "<div id='expandIcon' style='text-align: center; cursor:pointer;'><img style='width:20px;height:10px;' src='/x_component_Execution/$WorkReport/default/icon/expand.gif'></div>"+
                    "<table id='workDetails' style='width:95%; margin:0px 40px; display:none' border='0'>"+
                    "<tr>"+
                    "   <td styles='formTableTitle' lable='workSplitAndDescription' width='10%' valign='top'></td>" +
                    "   <td styles='formTableValue' item='workSplitAndDescription' colspan='5'></td>" +
                    "</tr>"+
                    "<tr>"+
                    "   <td styles='formTableTitle' lable='specificActionInitiatives' valign='top'></td>" +
                    "   <td styles='formTableValue' item='specificActionInitiatives' colspan='5'></td>" +
                    "</tr>"+
                    "<tr>"+
                    "   <td styles='formTableTitle' lable='milestoneMark' valign='top'></td>" +
                    "   <td styles='formTableValue' item='milestoneMark' colspan='5'></td>" +
                    "</tr>"+

                    "<tr>"+
                    "   <td styles='formTableValue' colspan='6'>" +
                    "       <div styles='formTableValueDiv' item='attachments'></div>"+
                    "   </td>"+
                    "<tr>"+
                    "</table>"+
                    "<div id='foldIcon' style='text-align: center; cursor:pointer;display:none;'><img style='width:20px;height:10px;' src='/x_component_Execution/$WorkReport/default/icon/fold.gif'></div>"
        this.tableContentDiv.set("html", html);

        this.expandDiv = this.tableContentDiv.getElementById("expandIcon");
        this.foldDiv = this.tableContentDiv.getElementById("foldIcon");
        this.workDetailsTab = this.tableContentDiv.getElementById("workDetails");

        if(this.expandDiv){
            this.expandDiv.addEvents({
                "click":function(){
                    if(this.workDetailsTab) this.workDetailsTab.setStyle("display","");
                    this.expandDiv.setStyle("display","none");
                    this.foldDiv.setStyle("display","");
                }.bind(this)
            })
        }

        if(this.foldDiv){
            this.foldDiv.addEvents({
                "click":function(){
                    if(this.workDetailsTab) this.workDetailsTab.setStyle("display","none");
                    this.expandDiv.setStyle("display","");
                    this.foldDiv.setStyle("display","none");
                }.bind(this)
            })
        }
       this.loadForm();
    },
    loadForm: function(){
        this.form = new MForm(this.formTableArea, this.workData, {
            style: "execution",
            isEdited: this.isEdited || this.isNew,
            itemTemplate: this.getItemTemplate(this.lp )
        },this.app);
        this.form.load();

        this.attachmentArea = this.formTableArea.getElement("[item='attachments']");
        this.loadAttachment( this.attachmentArea );
    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            workType: {
                text: lp.workType + ":",
                    selectValue: lp.workTypeValue.split(",")
            },
            workLevel: {
                text: lp.workLevel + ":",
                    type: "select",
                    notEmpty:true,
                    selectValue: lp.workLevelValue.split(",")
            },
            timeLimit: {text: lp.timeLimit + ":", tType: "date",name:"completeDateLimitStr",notEmpty:true},
            reportCycle: {
                text: lp.reportCycle + ":",
                type: "select",
                notEmpty:true,
                //selectValue: lp.reportCycleValue.split(","),
                selectText: lp.reportCycleText.split(",")
            },
            reportDay: {
                type: "select",
                name:"reportDayInCycle",
                //aa:function(){alert(JSON.stringify(this.workData.reportCycle))}.bind(this),
                selectValue: (!this.workData.reportCycle || this.workData.reportCycle==lp.reportCycleText.split(",")[0])?lp.weekDayValue.split(","):lp.monthDayValue.split(","),
                selectText:  (!this.workData.reportCycle || this.workData.reportCycle==lp.reportCycleText.split(",")[0])?lp.weekDayText.split(","):lp.monthDayText.split(",")
            },
            dutyDepartment: {text: lp.dutyDepartment + ":", type:"org",orgType: "unit",name:"responsibilityUnitName"},
            dutyPerson: {text: lp.dutyPerson + ":", type:"org",orgType: "identity",count:1,name:"responsibilityIdentity",notEmpty:true},
            secondDepartment: {
                text: lp.secondDepartment + ":", type:"org",orgType: "unit",
                name:"cooperateUnitNameList",
                value:this.workData.cooperateUnitNameList?this.workData.cooperateUnitNameList.join(","):""
            },
            secondPerson: {
                text: lp.secondPerson + ":", type:"org",orgType: "identity",
                name:"cooperateIdentityList",
                value:this.workData.cooperateIdentityList?this.workData.cooperateIdentityList.join(","):"",
                count: 0
            },
            readReader: {
                text: lp.readReader + ":", type:"org",orgType: "identity",
                name:"readLeaderIdentityList",
                value:this.workData.readLeaderIdentityList?this.workData.readLeaderIdentityList.join(","):"",
                count: 0
            },
            deployPerson :{text: lp.deployPerson,name:"deployerIdentity",type:"org",orgType: "identity"},
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
            documentId : this.data.workId,
            isNew : this.options.isNew,
            isEdited : this.options.isEdited
        });
        this.attachment.load();
    },

    loadReportAttachment: function( area,edit ){
        this.attachment = new MWF.xApplication.Execution.ReportAttachment( area, this.app, this.actions, this.app.lp, {
            //documentId : this.data.workId,
            documentId : this.workReportId,
            isNew : this.options.isNew,
            isEdited : edit,
            "size":this.workReportData.processStatus == this.lp.activityName.drafter ? "max":"min",
            onQueryUploadAttachment : function(){
                var saveData = {};
                saveData.workId = this.workReportData.workId;
                saveData.id = this.workReportData.id;
                if(this.workReportData.processStatus == this.lp.activityName.drafter){
                    saveData.progressDescription = this.contentTextarea1.value;
                    saveData.workPlan = this.contentTextarea2.value
                }

                this.actions.saveWorkReport( saveData, function(json){
                    if(json.type == "success"){
                        this.attachment.isQueryUploadSuccess = true;
                    }
                }.bind(this),function(xhr,text,error){
                    this.attachment.isQueryUploadSuccess = false;
                }.bind(this),false);

            }.bind(this)
        });
        this.attachment.load();
    },
    readDone: function(){
        //alert(this.data.todoId)
        this.actions.readDone(this.data.todoId,function(json){
            this.app.notice(this.lp.prompt.readDone,"success");
            this.fireEvent("reloadView", json);
            this.close();
        }.bind(this),function(xhr){}.bind(this))
    },
    save: function(){
        this.createShade();
        //this.saveActionNode.removeEvents("click")
        var saveData = {};
        saveData.workId = this.workReportData.workId;
        saveData.id = this.workReportData.id;
        var rateTmp = 0;
        rateTmp = parseInt(this.completePercentRateSpan.get("text").replace("%",""));

        if(this.workReportData.processStatus == this.lp.activityName.drafter){
            saveData.progressDescription = this.contentTextarea1.value;
            saveData.workPlan = this.contentTextarea2.value;
            saveData.isWorkCompleted = this.completeSelect.get("value")=="yes"?true:false
            //saveData.progressPercent = this.completePercentRateSpan.get("text")*100
            saveData.progressPercent = rateTmp
        }else if(this.workReportData.processStatus == this.lp.activityName.manager){
            saveData.adminSuperviseInfo = this.contentTextarea3.value;
            saveData.isWorkCompleted = this.completeSelect.get("value")=="yes"?true:false
            saveData.progressPercent = rateTmp
        }else if(this.workReportData.processStatus == this.lp.activityName.leader){
            saveData.opinion = this.contentTextarea4.value
        }

        if(saveData.progressDescription){
            if(saveData.progressDescription.length>600){
                this.app.notice( "字数不能大于600","error");
                this.destroyShade();
                return false;
            }
        }
        if(saveData.workPlan){
            if(saveData.workPlan.length>600){
                this.app.notice( "字数不能大于600","error");
                this.destroyShade();
                return false;
            }
        }

        this.actions.saveWorkReport( saveData, function(json){
            if(json.type == "success"){
                this.app.notice(this.lp.information.saveSuccess, "success");
                this.destroyShade();
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
            this.destroyShade();
        }.bind(this),true);
        //this.saveActionNode.addEvents({
        //    "click": function () {
        //        this.save();
        //    }.bind(this)
        //})
    },
    submit: function(e){
        if(this.contentTextarea1){
            if(this.contentTextarea1.value == ""){
                this.app.notice(this.lp.contentTitle1+this.lp.checkEmpty, "error");
                return false;
            }
        }
        if(this.contentTextarea2){
            if(this.contentTextarea2.value == ""){
                this.app.notice(this.lp.contentTitle2+this.lp.checkEmpty, "error");
                return false;
            }
        }


        var saveData = {};
        saveData.workId = this.workReportData.workId;
        saveData.id = this.workReportData.id;
        var rateTmp = 0;
        rateTmp = parseInt(this.completePercentRateSpan.get("text").replace("%",""));
        if(this.workReportData.processStatus == this.lp.activityName.drafter){
            saveData.progressDescription = this.contentTextarea1.value;
            saveData.workPlan = this.contentTextarea2.value;
            saveData.isWorkCompleted = this.completeSelect.get("value")=="yes"?true:false
            //saveData.progressPercent = this.completePercentRateSpan.get("text")*100
            saveData.progressPercent = rateTmp
        }else if(this.workReportData.processStatus == this.lp.activityName.manager){
            saveData.adminSuperviseInfo = this.contentTextarea3.value;
            saveData.isWorkCompleted = this.completeSelect.get("value")=="yes"?true:false
            //saveData.progressPercent = this.completePercentRateSpan.get("text")*100
            saveData.progressPercent = rateTmp
        }else if(this.workReportData.processStatus == this.lp.activityName.leader){
            saveData.opinion = this.contentTextarea4.value
        }
        if(saveData.progressDescription){
            if(saveData.progressDescription.length>600){
                this.app.notice( "字数不能大于600","error");
                return false;
            }
        }
        if(saveData.workPlan){
            if(saveData.workPlan.length>600){
                this.app.notice( "字数不能大于600","error");
                return false;
            }
        }

        if(this.completeSelect && this.completeSelect.get("value")=="yes"){
            var _self = this;
            this.app.confirm("warn",e,_self.lp.submitWarn.warnTitle,_self.lp.submitWarn.warnContent,300,150,function(){
                _self.createShade();
                _self.actions.submitWorkReport( saveData, function(json){
                    if(json.type == "success"){
                        _self.app.notice(_self.lp.prompt.submitWorkReport,"success");
                        _self.fireEvent("reloadView", json);
                        _self.close();
                    }
                    _self.destroyShade()
                }.bind(_self),function(xhr,text,error){
                    var errorText = error;
                    if (xhr) errorMessage = xhr.responseText;
                    var e = JSON.parse(errorMessage);
                    if(e.message){
                        _self.app.notice( e.message,"error");
                    }else{
                        _self.app.notice( errorText,"error");
                    }
                    _self.destroyShade()
                }.bind(_self),true);

                this.close();
            },function(){
                this.close();
            })
        }else{
            this.createShade();
            this.actions.submitWorkReport( saveData, function(json){
                if(json.type == "success"){
                    this.app.notice(this.lp.prompt.submitWorkReport,"success");
                    this.fireEvent("reloadView", json);
                    this.close();
                }
                this.destroyShade()
            }.bind(this),function(xhr,text,error){
                var errorText = error;
                if (xhr) errorMessage = xhr.responseText;
                var e = JSON.parse(errorMessage);
                if(e.message){
                    this.app.notice( e.message,"error");
                }else{
                    this.app.notice( errorText,"error");
                }
                this.destroyShade()
            }.bind(this),true);
        }



    },
    _createBottomContent: function () {

        if(this.processIdentity == this.app.identity || this.processIdentity.indexOf(this.app.identity)>-1) {
            this.submitActionNode = new Element("div.submitActionNode", {
                "styles": this.css.formCancelActionNode,
                "text": this.lp.bottomAction.submit
            }).inject(this.formBottomNode)
                .addEvents({
                    "click": function (e) {
                        this.submit(e);
                    }.bind(this)
                })
        }

        if(this.processIdentity == this.app.identity || this.processIdentity.indexOf(this.app.identity)>-1) {
            this.saveActionNode = new Element("div.saveActionNode", {
                "styles": this.css.formCancelActionNode,
                "text": this.lp.bottomAction.save
            }).inject(this.formBottomNode)
                .addEvents({
                    "click": function () {
                        this.save();
                    }.bind(this)
                })
        }

        if(this.options.isRead){
            this.readActionNode = new Element("div.readActionNode", {
                "styles": this.css.formCancelActionNode,
                "text": this.lp.bottomAction.readDone
            }).inject(this.formBottomNode);
            this.readActionNode.addEvent("click", function (e) {
                this.readDone(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.bottomAction.close
        }).inject(this.formBottomNode);
        this.cancelActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));



        //控制按钮权限
        //如果已归档，则只留下关闭按钮
        if (this.workReportData && this.workReportData.status == this.lp.statuArchive) {
            if(this.submitActionNode)this.submitActionNode.destroy();
            if(this.saveActionNode)this.saveActionNode.destroy();
            if(this.readActionNode)this.readActionNode.destroy();

        }
    },
    createShade: function(o,txtInfo){
        var defaultObj = this.formAreaNode||this.container||this.app;
        var obj = o || defaultObj;
        var txt = txtInfo || "loading...";
        if(this.shadeDiv){ this.shadeDiv.destroy()}
        if(this["shadeTxtDiv"])  this["shadeTxtDiv"].destroy();
        this.shadeDiv = new Element("div.shadeDiv").inject(obj);
        this.inforDiv = new Element("div.inforDiv",{
            styles:{"height":"16px","display":"inline-block","position":"absolute","background-color":"#000000","border-radius":"3px","padding":"5px 10px"}
        }).inject(this.shadeDiv);
        this.loadImg = new Element("img.loadImg",{
            styles:{"width":"16px","height":"16px","float":"left"},
            src:this.path+"default/icon/loading.gif"
        }).inject(this.inforDiv);

        this.shadeTxtSpan = new Element("span.shadeTxtSpan").inject(this.inforDiv);
        this.shadeTxtSpan.set("text",txt);
        this.shadeDiv.setStyles({
            "width":"100%","height":"100%","position":"absolute","opacity":"0.6","background-color":"#cccccc","z-index":"999"
        });
        this.shadeTxtSpan.setStyles({"color":"#ffffff","font-size":"12px","display":"inline-block","line-height":"16px","padding-left":"5px"});

        var x = obj.getSize().x;
        var y = obj.getSize().y;
        this.shadeDiv.setStyles({
            "left":(obj.getLeft()-defaultObj.getLeft())+"px",
            "top":(obj.getTop()-defaultObj.getTop())+"px",
            "width":x+"px",
            "height":y+"px"
        });
        if(obj.getStyle("position")=="absolute"){
            this.shadeDiv.setStyles({
                "left":"0px",
                "top":"0px"
            })
        }
        this.inforDiv.setStyles({
            "left":(x/2)+"px",
            "top":(y/2)+"px"
        })
    },
    destroyShade : function(){
        if(this.shadeDiv) this.shadeDiv.destroy();
        //if(this.shadeDiv) this.shadeDiv.destroy()
    },
    showErrorMessage:function(xhr,text,error){
        var errorText = error;
        var errorMessage;
        if (xhr) errorMessage = xhr.responseText;
        if(errorMessage!=""){
            var e = JSON.parse(errorMessage);
            if(e.message){
                this.notice( e.message,"error");
            }else{
                this.notice( errorText,"error");
            }
        }else{
            this.notice(errorText,"error");
        }

    }
});


