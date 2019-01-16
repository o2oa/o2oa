MWF.xApplication.Report.StrategyExplorer.Summary = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isEdited" : false,
        "status" : "summary",
        "isToRead" : false,
        "isToReadLeader" : false
    },
    initialize: function (container, explorer, data, options) {
        this.setOptions( options );
        this.container = container;
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.css = this.explorer.css;
        this.actions = this.app.restActions;
        this.data = data;
        this.path = "/x_component_Report/$StrategyExplorer/";
    },
    load: function () {
        //this.node = new Element("div", {
        //    styles : this.css.deplymentNode
        //}).inject( this.container );
        if( this.options.isToReadLeader ){
            this.loadLeaderRead();
        }else if( this.options.isToRead ){
            this.loadOpinion( true );
        }else if( this.data.detail.opinions ){
            var opinions = JSON.parse( this.data.detail.opinions );
            if( typeOf(opinions) == "array" ){
                this.loadOpinion( false );
            }
        }

        this._load();
    },
    loadOpinion : function( showBotton ){
        var table = new Element( "table", {
            "width":"96%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : this.css.formTable
        }).inject( this.explorer.ideaContainer  );

        var tr = new Element("tr").inject( table );
        var td = new Element("td", {
            "styles": this.css.formTableTitle,
            "text" : "领导意见"
        }).inject( tr );
        td.setStyle("width","14%");

        var td = new Element("td", {
            "styles": this.css.formTableValue
        }).inject( tr );

        var opin = this.data.detail.opinions;
        if( opin ){
            var opinions = JSON.parse( opin );
            if( typeOf(opinions) == "array" ){
                var div = new Element("div").inject(td);
                opinions.each( function(o){

                    var table1 = new Element( "table", {
                        "width":"100%",
                        "border":"0",
                        "cellpadding":"0",
                        "cellspacing":"0"
                    }).inject( div  );

                    var tr1 = new Element("tr").inject( table1 );
                    var td1 = new Element("td", {
                        "text" : o.identity.split("@")[0] + ":"
                    }).inject( tr1 );
                    td1.setStyle("width","50px");

                    td1 = new Element("td", {
                        "text" : ( o.content ? this.app.common.replaceWithBr(o.content) : "已阅" ) + "      (" + o.datetime + ")"
                    }).inject( tr1 );
                }.bind(this));
            }
        }

        if(showBotton){
            div = new Element("div", {
                styles : { "margin-top" : "10px" }
            }).inject(td);
            var button = new Element("button",{
                value : "已阅",
                text : "已阅",
                styles : this.css.setRead
            }).inject(div);
            button.addEvent("click", function(e){
                this.setReaded(e)
            }.bind(this));
        }
    },
    loadLeaderRead : function(){
        var idea = "";
        if( this.data.detail.opinions ){
            var opinions = JSON.parse( this.data.detail.opinions );
            if( typeOf(opinions) == "array" ){
                opinions.each( function(o){
                    if( o.name == this.explorer.userName ){
                        idea = o.content;
                    }
                }.bind(this));
            }
        }

        var table = new Element( "table", {
            "width":"96%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : this.css.formTable
        }).inject( this.explorer.ideaContainer  );

        var tr = new Element("tr").inject( table );
        var td = new Element("td", {
            "styles": this.css.formTableTitle,
            "text" : "填写意见"
        }).inject( tr );
        td.setStyle("width","14%");

        var td = new Element("td", {
            "styles": this.css.formTableValue
        }).inject( tr );

        var div = new Element("div").inject(td);
        this.textarea_idea = new Element("textarea",{
            styles : this.css.textarea,
            value : idea
        }).inject( div );
        this.textarea_idea.addEvent("blur", function(){
            var data = [
                {
                    "identity": null,
                    "name": ( layout.desktop.session.user || layout.user ).distinguishedName,
                    "activity": "领导阅知",
                    "content": this.textarea_idea.get("value")
                }
            ];
            this.app.restActions.saveOpinion( this.data.id, {
                opinions : data
            })
        }.bind(this));

        div = new Element("div").inject(td);
        var button = new Element("button",{
            value : "已阅",
            text : "已阅",
            styles : this.css.setRead
        }).inject(div);
        button.addEvent("click", function(e){
            this.setReaded(e)
        }.bind(this));


    },
    getWorkId : function(){
        var workApp = this.explorer.workApp;
        if( workApp.work && workApp.work.id){
            return workApp.work.id
        }else if( workApp.workCompleted && workApp.workCompleted.id ){
            return workApp.workCompleted.id
        }else if( workApp.data && workApp.data.$work ){
            var work = workApp.data.$work;
            if( work.completed ){
                return work.workCompletedId;
            }else{
                return work.workId;
            }
        }
    },
    sendRead : function( callback ){
        MWF.Actions.get("x_organization_assemble_express").getDutyValue( {"name":"部主管","unit":this.data.targetUnit }, function( js ){
            var idList = js.data.identityList;
            if( idList && typeOf(idList)=="array" && idList.length > 0  ){
                var workId = this.getWorkId();
                MWF.Actions.get("x_processplatform_assemble_surface").sendReaderByWorkCompleted( function(){
                    if( callback )callback();
                }.bind(this), null, workId, {"identityList":idList} ,false );
            }else{
                if( callback )callback();
            }
        }.bind(this))

    },
    setReaded: function(e){
        var _self = this;

        var _setReaded = function(){
            var readData = _self.explorer.readData;
            if( _self.textarea_idea ){
                readData.opinion = _self.textarea_idea.get("value").trim();
            }
            MWF.Actions.get("x_processplatform_assemble_surface").setReaded(function(){
                _self.app.notice("标记已阅成功!");
                _self.app.close();
            }.bind(_self), null, readData.id, readData );
        }.bind(this);

        var text = "您确定要标记为已阅吗？";

        this.app.confirm("infor", e, "标记已阅确认", text, 350, 130, function(){
            if( _self.options.isToReadLeader ){
                var value = _self.textarea_idea.get("value").trim();
                if( !value || value == "" ){
                    _self.textarea_idea.set("value","已阅");
                    //value="已阅";
                }
                var data = [
                    {
                        "identity": null,
                        "name": ( layout.desktop.session.user || layout.user ).distinguishedName,
                        "activity": "领导阅知",
                        "content": _self.textarea_idea.get("value")
                    }
                ];
                _self.app.restActions.saveOpinion( _self.data.id, {
                    opinions : data
                }, function(){
                    _self.app.restActions.modifyReportStatus( _self.data.id, { reportStatus : "结束" }, function(){
                        _self.sendRead( function(){
                            _setReaded();
                            this.close();
                        }.bind(this));
                    }.bind(this))
                }.bind(this));
                //if( value ){
                //    _self.sendRead( function(){
                //        _setReaded();
                //        this.close();
                //    }.bind(this));
                //}else{
                //    _setReaded();
                //    this.close();
                //}
            }else{
                _setReaded();
                this.close();
            }
        }, function(){
            this.close();
        }, null, this.app.content);
    },
    _load : function(){
        this.month = parseInt(this.data.month);

        var table = this.table = new Element( "table", {
            "width":"96%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : this.css.formTable
        }).inject( this.explorer.totalContainer   );

        var tr = new Element("tr").inject( table );

        new Element("td", {  rowspan : 2, "width" : "30", "styles": this.css.formTableTitle, text : "序号" }).inject( tr );

        new Element("td", {
            "colspan" : 3,
            "text" : this.data.year + "年" + parseInt( this.data.month ) + "月工作总结",
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );

        var nextMonth = new Date( this.data.year, parseInt( this.data.month ) - 1, 1 ).increment("month", 1);
        var text = nextMonth.getFullYear() + "年" + (nextMonth.getMonth() + 1) + "月工作计划";
        new Element("td", {
            "colspan" : 2,
            "text" : text,
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );

        new Element("td", {
            rowspan : 2,
            "text" : "服务客户",
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );

        new Element("td", {
            rowspan : 2,
            "text" : "关爱员工",
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );

        new Element("td", {
            rowspan : 2,
            "text" : "意见建议",
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );


        var tr = new Element("tr").inject( table );

        new Element("td", {
            "text" : "部门重点工作",
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );


        new Element("td", {
            "text" : "计划",
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );

        new Element("td", {
            "text" : "总结",
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );

        new Element("td", {
            "text" : "部门重点工作",
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );


        new Element("td", {
            "text" : "计划",
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );

        this.listExtSummaryData( function(){
            this.loadContent()
        }.bind(this))
    },
    listExtSummaryData : function( callback ){
        //this.actions.listExtWorkWithReportId(this.options.reportId, { "infoLevel": "员工" }, function( json ){
        this.extSummaryData = this.data.WoReport_I_Ext_Contents_sumamry || [];
        if( callback )callback();
    },
    loadContent : function(){
        var arr = this.getTableData();
        arr.each( function( d ){
            var tr = new Element("tr").inject( this.table );

            new Element("td", {
                "text" : d.sequence,
                "align" : "center",
                "styles": this.css.formTableValue
            }).inject( tr );

            var td = new Element("td", {
                "html" : this.app.common.replaceWithBr(d.thisMonth.title),
                "width" : "140",
                "styles": this.css.formTableValue
            }).inject( tr );
            if( d.thisMonth.measuresList.length ){
                var showMeasureNode = new Element("input",{
                    "type" : "button",
                    "styles" : this.css.showMeasureNode2,
                    "value" : "查看举措"
                }).inject( td );
                var tooltip = new MWF.xApplication.Report.ShowMeasureTooltip( this.app.content, showMeasureNode, this.app, this.data, {
                    style : "report",
                    position : { x : "auto", y : "auto" },
                    event : "click"
                });
                tooltip.measuresList = d.thisMonth.measuresList;
            }


            new Element("td", {
                "html" : this.app.common.replaceWithBr(d.thisMonth.plan),
                "width" : "140",
                "styles": this.css.formTableValue
            }).inject( tr );

            new Element("td", {
                "html" : this.app.common.replaceWithBr(d.thisMonth.prog),
                "width" : "140",
                "styles": this.css.formTableValue
            }).inject( tr );

            td = new Element("td", {
                "html" : this.app.common.replaceWithBr(d.nextMonth.title),
                "width" : "140",
                "styles": this.css.formTableValue
            }).inject( tr );
            if( d.nextMonth.measuresList.length ){
                var showMeasureNode = new Element("input",{
                    "type" : "button",
                    "styles" : this.css.showMeasureNode2,
                    "value" : "查看举措"
                }).inject( td );
                var tooltip = new MWF.xApplication.Report.ShowMeasureTooltip( this.app.content, showMeasureNode, this.app, this.data, {
                    style : "report",
                    nextMonth : true,
                    position : { x : "auto", y : "auto" },
                    event : "click"
                });
                tooltip.measuresList = d.nextMonth.measuresList;
            }


            new Element("td", {
                "html" : this.app.common.replaceWithBr(d.nextMonth.plan),
                "width" : "140",
                "styles": this.css.formTableValue
            }).inject( tr );


            new Element("td", {
                "html" : this.app.common.replaceWithBr(d.extWork.fuwu),
                "width" : "140",
                "styles": this.css.formTableValue
            }).inject( tr );

            new Element("td", {
                "html" : this.app.common.replaceWithBr(d.extWork.guanai),
                "width" : "140",
                "styles": this.css.formTableValue
            }).inject( tr );

            new Element("td", {
                "html" : this.app.common.replaceWithBr(d.extWork.yijian),
                "width" : "140",
                "styles": this.css.formTableValue
            }).inject( tr );
        }.bind(this))
    },
    getTableData : function(){
        this.tableData = [];
        for( var i=0; i<5; i++ ){
            var extWorkData = this.extSummaryData[i];
            var thisMonthData = this.data.thisMonth_workList[i];
            var nextMonthData = this.data.nextMonth_workList[i];
            var object = {
                sequence : i+1,
                thisMonth : {
                    title : thisMonthData ? thisMonthData.workTitle : "",
                    plan : thisMonthData ? thisMonthData.workPlanSummary : "",
                    prog : thisMonthData ? thisMonthData.workProgSummary : "",
                    measuresList : thisMonthData ? thisMonthData.measuresList : []
                },
                nextMonth : {
                    title : nextMonthData ? nextMonthData.workTitle : "",
                    plan : nextMonthData ? nextMonthData.workPlanSummary : "",
                    measuresList : nextMonthData ? nextMonthData.measuresList : []
                },
                extWork : {
                    fuwu : extWorkData ? extWorkData.fuwu : "",
                    guanai : extWorkData ? extWorkData.guanai : "",
                    yijian : extWorkData ? extWorkData.yijian : ""
                }
            };
            this.tableData.push( object );
        }
        return this.tableData;
    }
});