MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Execution", "WorkForm", null, false);
MWF.xDesktop.requireApp("Execution", "Chat", null, false);

MWF.xApplication.Execution.WorkDetail = new Class({
    Extends: MWF.xApplication.Execution.WorkForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "100%",
        "height": "100%",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": false,
        "title": "",
        "draggable": false,
        "closeAction": true,
        "isNew": false,
        "isEdited": true,
        "hasScroll" : false
    },
    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp;
        this.actions = this.app.restActions;
        this.path = "/x_component_Execution/$WorkDetail/"; 
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};
        this.workDetailLp = this.app.lp.WorkDetail;

        this.actions.getBaseWorkInfo(this.data.id,function(json){
                if(json.data){
                    this.baseWorkData = json.data
                }
            }.bind(this),
            function(xhr,text,error){
                this.showErrorMessage(xhr,text,error)
            }.bind(this),false
        )

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
                "text": this.data.title || ""
            }).inject(this.formTopNode)

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }
            this._createTopContent();

        }
    },
    _createTopContent: function () {


    },
    _createTableContent: function () {
        this.table = new Element("table", {
            "height": "100%",
            "border": "0",
            "cellpadding": "0",
            "cellspacing": "0",
            "class": "formTable"
        }).inject(this.formTableArea);
        this.tr = new Element("tr", {
            "valign": "top"
        }).inject(this.table);

        this.leftArea = new Element("td.leftArea", {
            "styles": this.css.leftArea
        }).inject(this.tr);

        this.detailArea = new Element("td.detailArea", {
            "styles": this.css.detailArea
        }).inject(this.tr);

        this.chatArea = new Element("td.chatArea", {
            "styles": this.css.chatArea
        }).inject(this.tr);


        this.loadLeftContent();
        this.loadForm();
        this.loadChatNode();

        this.setContentSize();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);
    },
    loadLeftContent: function () {
        this.reportArea = new Element("div.reportArea", {
            "styles": this.css.reportArea
        }).inject(this.leftArea);
        this.loadReportTop();
        this.loadReportContent();

        this.questionArea = new Element("div.questionArea", {
            "styles": this.css.questionArea
        }).inject(this.leftArea);
        this.loadQuestionTop();
        this.loadQuestionContent();
    },
    loadReportTop: function () {
        this.reportTopNode = new Element("div.reportTopNode", {
            "styles": this.css.reportTopNode
        }).inject(this.reportArea);

        this.reportTopIconNode = new Element("div", {
            "styles": this.css.reportTopIconNode
        }).inject(this.reportTopNode);

        this.reportTopTextNode = new Element("div", {
            "styles": this.css.reportTopTextNode,
            "text": this.lp.workReportTitle
        }).inject(this.reportTopNode)
    },
    loadReportContent: function () {
        _self = this;
        this.reportContentNode = new Element("div", {
            "styles": this.css.reportContentNode
        }).inject(this.reportArea);

        this.setScrollBar(this.reportContentNode);

        this.getReportData(function(json){
            if( json.data ){
                json.data.each(function (d) {
                    var color = "";
                    if(d.activityName == this.lp.WorkReport.activityName.drafter) color = "#ff0000";
                    else{
                        if(d.processLogs){
                            d.processLogs.each(function(dd){
                                if(dd.activityName == this.lp.WorkReport.activityName.leader){
                                    color = "#00FF00"
                                }
                            }.bind(this))
                        }
                    }

                    var createTimes = d.createTime.split(" ")[0].split("-");
                    var createTime = createTimes[0] + this.lp.year +  createTimes[1] + this.lp.month + createTimes[2] + this.lp.day;
                    var reportItemNode = new Element("div", {
                        "styles": this.css.reportItemNode
                    }).inject(this.reportContentNode);
                    new Element("div", {
                        "styles": this.css.reportItemIconNode
                    }).inject(reportItemNode);
                    var reportItemTextNode = new Element("div", {
                        "styles": this.css.reportItemTextNode,
                        //"text": createTime + "-" + d.shortTitle
                        "html" : "<font color='"+color+"'>"+createTime + "-" + d.shortTitle +"</font>"
                    }).inject(reportItemNode);
                    reportItemNode.addEvents({
                        "mouseover" : function(){
                            if( _self.curReportItemNode != this.node )this.node.setStyles( _self.css.reportItemNode_over  );
                        }.bind({ node : reportItemNode }),
                        "mouseout" : function(){
                            if( _self.curReportItemNode != this.node )this.node.setStyles( _self.css.reportItemNode  );
                        }.bind({ node : reportItemNode }),
                        "click": function( ev ){
                            if( this.node != _self.curReportItemNode ){
                                this.node.setStyles( _self.css.reportItemNode_over );
                                if(_self.curReportItemNode)_self.curReportItemNode.setStyles( _self.css.reportItemNode  );
                                _self.curReportItemNode = this.node;
                                _self.createPrevReportInfor( this.data.id );
                            }
                        }.bind({ data : d, node : reportItemNode })
                    })
                }.bind(this))
            }
        }.bind(this))
    },
    getReportData: function (callback) {
        this.actions.getWorkReportList( this.data.id , function(json){
            if (callback)callback(json)
        })
    },
    createPrevReportInfor : function(workReportId){
        var lp = this.app.lp.WorkReport;
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
                    this.prevReportInforDiv.destroy();
                    if(this.curReportItemNode)this.curReportItemNode.setStyles( this.css.reportItemNode  );
                    this.curReportItemNode = null;
                }.bind(this)
            })
        this.prevReportInforListDiv = new Element("div.prevReportInforListDiv",{
            "styles":this.css.prevReportInforListDiv
        }).inject(this.prevReportInforDiv);

        //这里显示具体内容
        this.actions.getWorkReport(workReportId,function(json){
            //alert(JSON.stringify(json))
            if(json.type == "success"){
                var prevContentDiv = new Element("div.prevContentDiv",{
                    "styles": this.css.prevContentDiv
                }).inject(this.prevReportInforListDiv);
                var prevContentTitleDiv = new Element("div.prevContentTitleDiv",{
                    "styles" : this.css.prevContentTitleDiv,
                    "text" : lp.contentTitle1 + ":"
                }).inject(prevContentDiv)
                var prevContentValueDiv = new Element("div.prevContentValueDiv",{
                    "styles": this.css.prevContentValueDiv,
                    "text" : json.data.progressDescription
                }).inject(prevContentDiv);

                prevContentDiv = new Element("div.prevContentDiv",{
                    "styles": this.css.prevContentDiv
                }).inject(this.prevReportInforListDiv);
                prevContentTitleDiv = new Element("div.prevContentTitleDiv",{
                    "styles" : this.css.prevContentTitleDiv,
                    "text" : lp.contentTitle2 + ":"
                }).inject(prevContentDiv)
                prevContentValueDiv = new Element("div.prevContentValueDiv",{
                    "styles": this.css.prevContentValueDiv,
                    "text" : json.data.workPlan
                }).inject(prevContentDiv);

                //管理员督办
                if(json.data.needAdminAudit){
                    prevContentDiv = new Element("div.prevContentDiv",{
                        "styles": this.css.prevContentDiv
                    }).inject(this.prevReportInforListDiv);
                    prevContentTitleDiv = new Element("div.prevContentTitleDiv",{
                        "styles" : this.css.prevContentTitleDiv,
                        "text" : lp.adminContentTitle + ":"
                    }).inject(prevContentDiv)
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
                    "text" : lp.leaderContentTitle + ":"
                }).inject(prevContentDiv)
                prevContentValueDiv = new Element("div.prevContentValueDiv",{
                    "styles": this.css.prevContentValueDiv
                }).inject(prevContentDiv);

                var reportLeaderOpinionsDiv = new Element("div.reportLeaderOpinionsDiv",{
                    "styles":this.css.reportLeaderOpinionsDiv
                }).inject(prevContentValueDiv);

                //alert(JSON.stringify(json.data.processLogs))
                var preLogs = json.data.processLogs;
                this.preLeaderTitle = [];
                this.preLeaderValue = [];
                if(preLogs){
                    preLogs.each(function(data){
                        if(data.activityName == this.lp.WorkReport.activityName.leader && data.processStatus == this.lp.WorkReport.status.drafter && data.processorIdentity == this.app.identity){
                            this.leaderOpinionDrafter = data.opinion
                        }else{
                            if(data.activityName == this.lp.WorkReport.activityName.leader && data.processStatus == this.lp.WorkReport.status.effect){
                                this.preLeaderTitle.push(data.processorIdentity+"("+data.processTimeStr+")");
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
            }
        }.bind(this),null,false)
    },
    loadQuestionTop: function () {
        this.questionTopNode = new Element("div.questionTopNode", {
            "styles": this.css.questionTopNode
        }).inject(this.questionArea)

        this.questionTopIconNode = new Element("div", {
            "styles": this.css.questionTopIconNode
        }).inject(this.questionTopNode)

        this.questionTopTextNode = new Element("div", {
            "styles": this.css.questionTopTextNode,
            "text": this.lp.workQuestionTitle
        }).inject(this.questionTopNode)
    },
    loadQuestionContent: function () {
        this.questionContentNode = new Element("div", {
            "styles": this.css.questionContentNode
        }).inject(this.questionArea)

        this.setScrollBar(this.questionContentNode);

        this.getQuestionData(function (json) {
            json.data.each(function (d) {
                var questionItemNode = new Element("div", {
                    "styles": this.css.questionItemNode
                }).inject(this.questionContentNode)
                new Element("div", {
                    "styles": this.css.questionItemIconNode
                }).inject(questionItemNode)
                var questionItemTextNode = new Element("div", {
                    "styles": this.css.questionItemTextNode,
                    "text": d.subject
                }).inject(questionItemNode)
            }.bind(this))
        }.bind(this))
    },
    getQuestionData: function (callback) {
        var json = { data : [] };
        if (callback)callback(json)
    },

    loadChatNode: function () {
        this.chatTopNode = new Element("div.chatTopNode", {
            "styles": this.css.chatTopNode
        }).inject(this.chatArea)

        this.chatTopIconNode = new Element("div", {
            "styles": this.css.chatTopIconNode
        }).inject(this.chatTopNode)

        this.chatTopTextNode = new Element("div", {
            "styles": this.css.chatTopTextNode,
            "text": this.lp.workChatTitle
        }).inject(this.chatTopNode)

        this.loadChatContent();
    },
    loadChatContent: function () {
        this.chatContentNode = new Element("div", {
            "styles": this.css.chatContentNode
        }).inject(this.chatArea)

        this.chatContentListNode = new Element("div.chatContentListNode", {
            "styles": this.css.chatContentListNode
        }).inject(this.chatContentNode)

        this.chatEditorNode = new Element("div",{
            "styles": this.css.chatEditorNode
        }).inject(this.chatContentNode)
        this.chat = new MWF.xApplication.Execution.Chat(this.chatContentListNode, this.chatEditorNode, this.app, this.actions, this.lp, {
            "workId": this.data.id
        })
        this.chat.load();
        //this.getChatData(function (json) {
        //
        //}.bind(this))
        //
        //this.loadEditor(this.chatEditorNode)
    },
    getChatData: function (callback) {
        var json = {};
        if (callback)callback(json)
    },
    loadForm: function () {
        this.detailTopNode = new Element("div.detailTopNode", {
            "styles": this.css.detailTopNode
        }).inject(this.detailArea)

        this.detailTopIconNode = new Element("div", {
            "styles": this.css.detailTopIconNode
        }).inject(this.detailTopNode)

        this.detailTopTextNode = new Element("div", {
            "styles": this.css.detailTopTextNode,
            "text": this.lp.workDetailTitle
        }).inject(this.detailTopNode)

        this.detailContentNode = new Element("div.detailContentNode", {
            "styles": this.css.detailContentNode
        }).inject(this.detailArea)


        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
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
            //"   <td styles='formTableTitle' lable='subject'></td>" +
            //"   <td styles='formTableValue' item='subject' colspan='3'></td>" +
            //"</tr><tr>" +
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
            "   <td styles='formTableValue' colspan='4'>" +
            "       <div styles='formTableTitleDiv' lable='milestoneMark'></div>" +
            "       <div styles='formTableValueDiv' item='milestoneMark'></div>" +
            "   </td>" +
            "</tr><tr>" +

            "   <td styles='formTableValue' colspan='4'>" +
            "       <div styles='formTableValueDiv' item='attachments'></div>"+
            "   </td>" +
            "</tr>"+
            "</table>"
        this.detailContentNode.set("html", html);
        this.form = new MForm(this.detailContentNode, this.data, {
            style: "execution",
            isEdited: this.isEdited || this.isNew,
            itemTemplate: this.getWorkDetailsItemTemplate(this.lp.workForm),
            onPostLoad:function(){

            }.bind(this)
        }, this.app,this.css);
        this.form.load();

        this.attachmentArea = this.detailArea.getElement("[item='attachments']");
        this.loadAttachment(this.attachmentArea);

        this.tmpLp = this.workDetailLp.processInfo;
        this.processInfo = new Element("div.processInfo",{
            "styles":this.css.processInfoDiv
        //}).inject(this.detailContentNode,"top")
        }).inject(this.detailContentNode)
        this.processInfoTitle = new Element("div.processInfoTitle",{
            "styles":this.css.processInfoTitleDiv,
            "text":this.tmpLp.title
        }).inject(this.processInfo)
        //alert(JSON.stringify(this.data.okrWorkAuthorizeRecords))
        this.processInfoContent = new Element("div.processInfoContent",{
            "styles":this.css.processInfoContent
        }).inject(this.processInfo)


        var tHead = "<table styles='processTable'>"
        var tBody = "<tr>"
        tBody+="<td styles='processTH'>"+this.tmpLp.operate+"</td>"
        tBody+="<td styles='processTH'>"+this.tmpLp.time+"</td>"
        tBody+="<td styles='processTH'>"+this.tmpLp.source+"</td>"
        tBody+="<td styles='processTH'>"+this.tmpLp.target+"</td>"
        tBody+="<td styles='processTH'>"+this.tmpLp.opinion+"</td>"
        tBody+="</tr>"
        if(this.baseWorkData.workDeployAuthorizeRecords){
            this.baseWorkData.workDeployAuthorizeRecords.each(function(d){

                tBody += "<tr>"
                tBody+="<td styles='processTD'>"+d.operationTypeCN+"</td>"
                tBody+="<td styles='processTD'>"+ d.operationTime+"</td>"
                tBody+="<td styles='processTD'>"+ d.source+"</td>"
                tBody+="<td styles='processTD'>"+ d.target+"</td>"
                tBody+="<td styles='processTD'>"+d.opinion+"</td>"
                tBody+="</tr>"


            }.bind(this))
        }
        tBottom = "</table>"
        this.processInfoContent.set("html",tHead+tBody+tBottom)
        this.formatStyles(this.processInfoContent)

        this.setScrollBar(this.detailContentNode)

    },
    formatStyles:function(obj){
        obj.getElements("[styles]").each(function(el){
            var styles = el.get("styles");
            if( styles && this.css[styles] ){
                el.setStyles( this.css[styles] )
            }
        }.bind(this))
    },
    getWorkDetailsItemTemplate:function(lp){
        _self = this;
        return {
            workType: {
                text: lp.workType + ":",
                type: "select",
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
                //selectValue: lp.reportCycleValue.split(","),
                selectText: lp.reportCycleText.split(","),
                className: "inputSelectUnformatWidth",

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
                selectValue: (!this.data.reportCycle || this.data.reportCycle==lp.reportCycleText.split(",")[0])?lp.weekDayValue.split(","):lp.monthDayValue.split(","), //lp.weekDayValue.split(","),
                selectText:  (!this.data.reportCycle || this.data.reportCycle==lp.reportCycleText.split(",")[0])?lp.weekDayText.split(","):lp.monthDayText.split(",")
            },
            dutyDepartment:{text:lp.dutyDepartment+":",name:"responsibilityOrganizationName"},
            dutyPerson:{text:lp.dutyPerson+":",name:"responsibilityIdentity"},
            secondDepartment:{text:lp.secondDepartment+":",name:"cooperateOrganizationName"},

            secondPerson: {text: lp.secondPerson + ":", tType: "identity",name:"cooperateIdentity", count: 0},
            readReader: {text: lp.readReader + ":", tType: "identity", name:"readLeaderIdentity",count: 0},
            subject: {text: lp.subject + ":",name:"title",notEmpty:true},
            workSplitAndDescription: {text: lp.workSplitAndDescription + ":", type: "textarea",name:"workDetail",notEmpty:true},
            specificActionInitiatives: {text: lp.specificActionInitiatives + ":", type: "textarea",name:"progressAction"},
            cityCompanyDuty: {text: lp.cityCompanyDuty + ":", type: "textarea",name:"dutyDescription"},
            milestoneMark: {text: lp.milestoneMark + ":", type: "textarea",name:"landmarkDescription"},
            importantMatters: {text: lp.importantMatters + ":", type: "textarea",name:"majorIssuesDescription"}
        }
    },
    loadAttachment: function (area) {
        this.attachment = new MWF.xApplication.Execution.Attachment(area, this.app, this.actions, this.app.lp, {
            documentId: this.data.id,
            isNew: this.options.isNew,
            isEdited: this.options.isEdited,
            size: "min",
            isSizeChange: true
        })
        this.attachment.load();
    },
    _ok: function (data, callback) {
        //alert(JSON.stringify(data))
        //this.app.restActions.saveDocument( this.data.id, data, function(json){
        //    if( callback )callback(json);
        //}.bind(this));
    },
    setFormNodeSize: function (width, height, top, left) {
        if (!width)width = this.options.width ? this.options.width : "50%"
        if (!height)height = this.options.height ? this.options.height : "50%"
        if (!top) top = this.options.top ? this.options.top : 0;
        if (!left) left = this.options.left ? this.options.left : 0;

        //var appTitleSize = this.app.window.title.getSize()；

        var allSize = this.app.content.getSize();

        var topTextWidth = allSize.x - this.formTopCloseActionNode.getSize().x - this.formTopIconNode.getSize().x - 40;
        this.formTopTextNode.setStyles({
            "width": "" + topTextWidth + "px"
        })

        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
        300 > width && (width = 300);
        220 > height && (height = 220);

        top = top || parseInt((limitHeight - height) / 2, 10) //+appTitleSize.y);
        left = left || parseInt((limitWidth - width) / 2, 10);

        this.formAreaNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px",
            "top": "" + top + "px",
            "left": "" + left + "px"
        });

        this.formNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px"
        });

        var iconSize = this.formIconNode ? this.formIconNode.getSize() : {x: 0, y: 0};
        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};

        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y;
        //var formMargin = formHeight -iconSize.y;
        this.formContentNode.setStyles({
            "height": "" + contentHeight + "px"
        });
        this.formTableContainer.setStyles({
            "height": "" + contentHeight + "px"
        });

        this.detailContentNode.setStyles({
            "height": "" + (contentHeight - this.detailTopNode.getSize().y) + "px"
        });

        if(this.chatContentListNode){
            this.chatContentListNode.setStyles({
                "height": "" + (contentHeight - this.chatTopNode.getSize().y - this.chatEditorNode.getSize().y) + "px"
            });
        }

        var reportContentHeight = ( contentHeight - (this.reportTopNode.getSize().y * 2) ) / 2 ;
        this.reportContentNode.setStyles({
            "height": "" + reportContentHeight + "px"
        });
        this.questionContentNode.setStyles({
            "height": "" + reportContentHeight + "px"
        });
    },
    setContentSize: function () {
        var allSize = this.app.content.getSize();
        var leftAreaWidth = this.leftArea.getStyle("width");
        var chatAreaWidth = this.chatArea.getStyle("width");

        var width = allSize.x - parseInt(leftAreaWidth) - parseInt(chatAreaWidth); // - 10;
        this.detailArea.setStyles({
            "width": "" + width + "px"
        });
    },
    setScrollBar: function(node, style, offset, callback){
        if (!style) style = "attachment";
        if (!offset){
            offset = {
                "V": {"x": 0, "y": 0},
                "H": {"x": 0, "y": 0}
            };
        };
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(node, {
                "style": style,
                "offset": offset,
                "indent": false
            });
            if (callback) callback();
        });
        return false;
    },

    showErrorMessage:function(xhr,text,error){
        var errorText = error;
        if (xhr) errorMessage = xhr.responseText;
        if(errorMessage!=""){
            var e = JSON.parse(errorMessage);
            if(e.userMessage){
                this.app.notice( e.userMessage,"error");
            }else{
                this.app.notice( errorText,"error");
            }
        }else{
            this.app.notice(errorText,"error")
        }

    }
});