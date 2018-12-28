MWF.xApplication.Report.StrategyExplorer.Print = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isEdited" : false,
        "status" : ""
    },
    initialize: function (container, explorer, data, options){
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
    load: function(){
        if( this.data.detail.opinions ){
            var opinions = JSON.parse( this.data.detail.opinions );
            if( typeOf(opinions) == "array" ){
                this.loadOpinion();
            }
        }
        this._load();
    },
    loadOpinion : function(){
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
    },
    _load: function () {
        //this.node = new Element("div", {
        //    styles : this.css.deplymentNode
        //}).inject( this.container );
        this.month = parseInt(this.data.month);

        var table = this.table = new Element( "table", {
            "width":"100%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : this.css.formTable
        }).inject( this.explorer.totalContainer );

        var tr = new Element("tr").inject( table );

        new Element("td", {  rowspan : 2, "width" : "30", "styles": this.css.formTableTitle, text : "序号" }).inject( tr );

        new Element("td", {
            "colspan" : 3,
            "text" : this.data.year + "年" + parseInt( this.data.month ) + "月工作总结",
            "styles": this.css.formTableTitle
        }).inject( tr );

        //var nextMonth = new Date( this.data.year, this.data.month, 1 ).increment("month", 1);
        //var text = nextMonth.getFullYear() + "年" + (nextMonth.getMonth()) + "月工作计划";

        var nextMonth = new Date( this.data.year, parseInt( this.data.month ) - 1, 1 ).increment("month", 1);
        var text = nextMonth.getFullYear() + "年" + (nextMonth.getMonth() + 1) + "月工作计划";
        new Element("td", {
            "colspan" : 2,
            "text" : text,
            "styles": this.css.formTableTitle
        }).inject( tr );

        new Element("td", {
            rowspan : 2,
            "width" : "200",
            "text" : "服务客户",
            "styles": this.css.formTableTitle
        }).inject( tr );

        new Element("td", {
            rowspan : 2,
            "width" : "200",
            "text" : "关爱员工",
            "styles": this.css.formTableTitle
        }).inject( tr );

        new Element("td", {
            rowspan : 2,
            "width" : "200",
            "text" : "意见建议",
            "styles": this.css.formTableTitle
        }).inject( tr );


        var tr = new Element("tr").inject( table );

        new Element("td", {
            "text" : "部门重点工作",
            "width" : "200",
            "styles": this.css.formTableTitle
        }).inject( tr );


        new Element("td", {
            "text" : "计划",
            "width" : "200",
            "styles": this.css.formTableTitle
        }).inject( tr );

        new Element("td", {
            "text" : "总结",
            "width" : "200",
            "styles": this.css.formTableTitle
        }).inject( tr );

        new Element("td", {
            "text" : "部门重点工作",
            "width" : "200",
            "styles": this.css.formTableTitle
        }).inject( tr );


        new Element("td", {
            "text" : "计划",
            "width" : "200",
            "styles": this.css.formTableTitle
        }).inject( tr );

        this.loadContent()
    },
    loadContent : function(){
        var arr = this.getTableData();
        arr.each( function( d, i ){
            var tr = new Element("tr").inject( this.table );

            new Element("td", {
                "text" : d.sequence,
                "align" : "center",
                "styles": this.css.formTableValue
            }).inject( tr );

            var td = new Element("td", {
                "valign" : "top",
                "html" : this.app.common.replaceWithBr(d.thisMonth.title),
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
                "valign" : "top",
                "html" : this.app.common.replaceWithBr(d.thisMonth.plan),
                "styles": this.css.formTableValue
            }).inject( tr );

            td = new Element("td", {
                "valign" : "top",
                "styles": this.css.formTableValue
            }).inject( tr );
            if( this.options.status == "deployment" ){
            }else if( this.options.status == "write" ){
                var div = new Element("div").inject(td);
                this.loadThisMontWorkProg( div, i, false );
            }else if( this.options.status == "confirm" ){
                //var div = new Element("div").inject(td);
                //this.loadThisMontWorkProg( div, i, true );
                td.set("html" , this.app.common.replaceWithBr(d.thisMonth.prog))
            }else if( this.options.status == "audit" ){
                td.set("html" , this.app.common.replaceWithBr(d.thisMonth.prog))
            }else if( this.options.status == "summary" ){
                td.set("html" , this.app.common.replaceWithBr(d.thisMonth.prog))
            }

            td = new Element("td", {
                "valign" : "top",
                "styles": this.css.formTableValue
            }).inject( tr );
            if( this.options.status == "deployment" ){
            }else if( this.options.status == "write" ){
                var div = new Element("div").inject(td);
                this.loadNextMontWorkTitle( div, i, false );
            }else if( this.options.status == "confirm" ){
                //var div = new Element("div").inject(td);
                //this.loadNextMontWorkTitle( div, i, true );
                td.set("html" , this.app.common.replaceWithBr(d.nextMonth.title))
            }else if( this.options.status == "audit" ){
                td.set("html" , this.app.common.replaceWithBr(d.nextMonth.title))
            }else if( this.options.status == "summary" ){
                td.set("html" , this.app.common.replaceWithBr(d.nextMonth.title))
            }
            if( this.options.status == "confirm" || this.options.status == "audit" ){
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
            }


            td = new Element("td", {
                "valign" : "top",
                "styles": this.css.formTableValue
            }).inject( tr );
            if( this.options.status == "deployment" ){
            }else if( this.options.status == "write" ){
                var div = new Element("div").inject(td);
                this.loadNextMontWorkPlan( div, i, false );
            }else if( this.options.status == "confirm" ){
                //var div = new Element("div").inject(td);
                //this.loadNextMontWorkPlan( div, i, true );
                td.set("html" , this.app.common.replaceWithBr(d.nextMonth.plan))
            }else if( this.options.status == "audit" ){
                td.set("html" , this.app.common.replaceWithBr(d.nextMonth.plan))
            }else if( this.options.status == "summary" ){
                td.set("html" , this.app.common.replaceWithBr(d.nextMonth.plan))
            }

            this.loadExtTd( d, tr, i, "fuwu" );

            this.loadExtTd( d, tr, i, "guanai" );

            this.loadExtTd( d, tr, i, "yijian" );
        }.bind(this))
    },
    loadExtTd : function( d, tr, i, type ){
        var td;
        if( this.options.status == "deployment" ){
            td = new Element("td", {
                "valign" : "top",
                "styles": this.css.formTableValue
            }).inject( tr );
        }else if( this.options.status == "write" ){
            //if( i == 0 ){
            //    td = new Element("td", {
            //        "valign" : "top",
            //        "styles": this.css.formTableValue,
            //        "rowspan" : "5"
            //    }).inject( tr );
            //    var div = new Element("div").inject(td);
            //    this.loadExtWork( div, false, type );
            //}
            td = new Element("td", {
                "valign" : "top",
                "styles": this.css.formTableValue
            }).inject( tr );
            this.loadExtWork_write( td, false, type, i );
        }else if( this.options.status == "confirm" ){
            //if( i == 0 ){
            //    td = new Element("td", {
            //        "valign" : "top",
            //        "styles": this.css.formTableValue,
            //        "rowspan" : "5"
            //    }).inject( tr );
            //    var div = new Element("div").inject(td);
            //    this.loadExtWork( div, true, type );
            //}
            td = new Element("td", {
                "valign" : "top",
                "styles": this.css.formTableValue
            }).inject( tr );
            td.set("html" , this.app.common.replaceWithBr(d.extWork[type]))
        }else if( this.options.status == "audit" ){
            td = new Element("td", {
                "valign" : "top",
                "styles": this.css.formTableValue
            }).inject( tr );
            td.set("html" , this.app.common.replaceWithBr(d.extWork[type]))
        }else if( this.options.status == "summary" ){
            td = new Element("td", {
                "valign" : "top",
                "styles": this.css.formTableValue
            }).inject( tr );
            td.set("html" , this.app.common.replaceWithBr(d.extWork[type]))
        }
    },
    loadExtWork_write : function( container, hasSummary, type, i ){
        var table;
        if( !this.ExtWorkData ){
            var work = this.ExtWorkData = this.data.WoReport_I_Ext_Contents || [];
            this.ExtWorkObject = {};
            work.sort( function( a, b ){
                var flag = a.targetPerson.localeCompare(b.targetPerson);
                if( flag == 0 ){ //相等
                    return a.orderNumber - b.orderNumber;
                }else{
                    return flag;
                }
            });
            work.each( function( w ){
                if( ! this.ExtWorkObject[ w.orderNumber ] ) {
                    this.ExtWorkObject[ w.orderNumber ] = [];
                }
                this.ExtWorkObject[ w.orderNumber].push( w );
            }.bind(this))
        }
        table = new Element( "table", {
            "width":"100%", "border":"0", "cellpadding":"3", "cellspacing":"0"
        }).inject( container );
        if( this.ExtWorkObject[ i + 1] ){
            this.ExtWorkObject[ i + 1].each( function(d){
                if( d[type] ){
                    var tr = new Element("tr").inject( table );
                    new Element("td", {  "width" : "40",  text : d.targetPerson.split("@")[0] + ":" }).inject( tr );
                    var td = new Element("td", {  }).inject( tr );
                    td.set("html" , this.app.common.replaceWithBr( d[type] ))
                }
            }.bind(this))
        }
        //this.ExtWorkData.each( function( d ){
        //    if( d[type] ){
        //        var tr = new Element("tr").inject( table );
        //        new Element("td", {  "width" : "40",  text : d.targetPerson.split("@")[0] + ":" }).inject( tr );
        //        var td = new Element("td", {  }).inject( tr );
        //        td.set("html" , this.app.common.replaceWithBr( d[type] ))
        //    }
        //}.bind(this));
        if( hasSummary && table ){
            var table = new Element( "table", {
                "width":"100%", "border":"0", "cellpadding":"3", "cellspacing":"0", "styles" : this.css.formTable
            }).inject( container );
            this.data.WoReport_I_Ext_Contents_sumamry.each( function( d ){
                if( d[type] ){
                    var tr = new Element("tr").inject( table );
                    new Element("td", {  "width" : "40",  text : "汇总:" }).inject( tr );
                    var td = new Element("td", {  }).inject( tr );
                    td.set("html" , this.app.common.replaceWithBr( d[type] ))
                }
            }.bind(this));
        }
    },
    //loadExtWork : function( container, hasSummary, type ){
    //    var table;
    //    if( !this.ExtWorkData ){
    //        var work = this.ExtWorkData = this.data.WoReport_I_Ext_Contents || [];
    //        work.sort( function( a, b ){
    //            var flag = a.targetPerson.localeCompare(b.targetPerson);
    //            if( flag == 0 ){ //相等
    //                return a.orderNumber - b.orderNumber;
    //            }else{
    //                return flag;
    //            }
    //        });
    //    }
    //    table = new Element( "table", {
    //        "width":"100%", "border":"0", "cellpadding":"3", "cellspacing":"0"
    //    }).inject( container );
    //    this.ExtWorkData.each( function( d ){
    //        if( d[type] ){
    //            var tr = new Element("tr").inject( table );
    //            new Element("td", {  "width" : "40",  text : d.targetPerson.split("@")[0] + ":" }).inject( tr );
    //            var td = new Element("td", {  }).inject( tr );
    //            td.set("html" , this.app.common.replaceWithBr( d[type] ))
    //        }
    //    }.bind(this));
    //    if( hasSummary && table ){
    //        var table = new Element( "table", {
    //            "width":"100%", "border":"0", "cellpadding":"3", "cellspacing":"0", "styles" : this.css.formTable
    //        }).inject( container );
    //        this.data.WoReport_I_Ext_Contents_sumamry.each( function( d ){
    //            if( d[type] ){
    //                var tr = new Element("tr").inject( table );
    //                new Element("td", {  "width" : "40",  text : "汇总:" }).inject( tr );
    //                var td = new Element("td", {  }).inject( tr );
    //                td.set("html" , this.app.common.replaceWithBr( d[type] ))
    //            }
    //        }.bind(this));
    //    }
    //},
    loadThisMontWorkProg : function( container, idx, hasSummary ){
        var table;
        var work = this.data.thisMonth_workList[idx];
        if( work && work.progList && work.progList.length > 0 ){
            table = new Element( "table", {
                "width":"100%", "border":"0", "cellpadding":"3", "cellspacing":"0"
            }).inject( container );

            work.progList.each( function( d ){
                if( d.progressContent ){
                    var tr = new Element("tr").inject( table );
                    new Element("td", {  "width" : "40", text : d.targetPerson.split("@")[0]+ ":" }).inject( tr );
                    var td = new Element("td", {   }).inject( tr );
                    td.set("html" , this.app.common.replaceWithBr(d.progressContent))
                }
            }.bind(this));
        }
        if( hasSummary && table){
            var tr = new Element("tr").inject( table );
            new Element("td", {  "width" : "40",  text : "汇总:" }).inject( tr );
            var td = new Element("td", {  }).inject( tr );
            td.set("html" , this.app.common.replaceWithBr( work.workProgSummary))
        }
    },
    loadNextMontWorkTitle : function( container, idx, hasSummary ){
        var work = this.data.nextMonth_workList[idx];
        var table;
        if( work && work.planNextList && work.planNextList.length > 0 ){
            table  = new Element( "table", {
                "width":"100%", "border":"0", "cellpadding":"3", "cellspacing":"0", "styles" : this.css.formTable
            }).inject( container );

            work.planNextList.each( function( d ){
                if( d.title ){
                    var tr = new Element("tr").inject( table );
                    new Element("td", {  "width" : "40",  text : d.targetPerson.split("@")[0]+ ":" }).inject( tr );
                    var td = new Element("td", {  }).inject( tr );
                    td.set("html" , this.app.common.replaceWithBr(d.title))
                }
            }.bind(this));
        }
        if( hasSummary && table){
            var tr = new Element("tr").inject( table );
            new Element("td", {  "width" : "40",  text : "汇总:" }).inject( tr );
            var td = new Element("td", {   }).inject( tr );
            td.set("html" , this.app.common.replaceWithBr( work.workTitle ))
        }
    },
    loadNextMontWorkPlan : function( container, idx, hasSummary ){
        var work = this.data.nextMonth_workList[idx];
        var table;
        if( work && work.planNextList && work.planNextList.length > 0 ){
            table  = new Element( "table", {
                "width":"100%", "border":"0", "cellpadding":"3", "cellspacing":"0", "styles" : this.css.formTable
            }).inject( container );

            work.planNextList.each( function( d ){
                if( d.planContent ){
                    var tr = new Element("tr").inject( table );
                    new Element("td", {  "width" : "40",  text : d.targetPerson.split("@")[0]+ ":" }).inject( tr );
                    var td = new Element("td", { }).inject( tr );
                    td.set("html" , this.app.common.replaceWithBr(d.planContent))
                }
            }.bind(this));
        }
        if( hasSummary && table){
            var tr = new Element("tr").inject( table );
            new Element("td", {  "width" : "40",  text : "汇总:" }).inject( tr );
            var td = new Element("td", {  }).inject( tr );
            td.set("html" , this.app.common.replaceWithBr( work.workPlanSummary))
        }
    },
    getTableData : function(){
        this.tableData = [];
        for( var i=0; i<5; i++ ){
            var extWorkData = this.data.WoReport_I_Ext_Contents_sumamry ? this.data.WoReport_I_Ext_Contents_sumamry[i] : null;
            var thisMonthData = this.data.thisMonth_workList ? this.data.thisMonth_workList[i] : null;
            var nextMonthData = this.data.nextMonth_workList ? this.data.nextMonth_workList[i] : null;
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