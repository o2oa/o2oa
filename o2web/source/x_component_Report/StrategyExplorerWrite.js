MWF.xApplication.Report.StrategyExplorer.Write = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isEdited" : false,
        "status" : "write"
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
        this.month = parseInt(this.data.month);

        this.thisMonth_inputContent_all = [];
        this.nextMonth_inputTitle_all = [];
        this.nextMonth_inputContent_all = [];

        this.nextMonth_inputTitle = [];

        this.thisMonthKeyworkList = [];
        this.thisMonthKeyworkNode = new Element("div", {
            //styles : this.css.monthNode
        }).inject( this.explorer.summaryContainer );
        //new Element( "div", {
        //    text : "本月【"+ this.month + "月】",
        //    styles : this.css.monthTitleNode
        //}).inject( this.thisMonthKeyworkNode );
        this.data.thisMonth_workList.each( function( data, index ){
            var keywork = new MWF.xApplication.Report.StrategyExplorer.Write.ThisKeyWorkItem( this.thisMonthKeyworkNode, this, data, {
                reportId : this.data.id,
                "isEdited" : this.options.isEdited,
                "status" : this.options.status,
                "orderNumber" : index + 1
            });
            keywork.load();
            this.thisMonthKeyworkList.push( keywork );
        }.bind(this));

        this.nexMonthKeyworkList = [];
        this.nextMonthKeyWorkNode = new Element("div", {
            //styles : this.css.monthNode
        }).inject( this.explorer.planContainer );

        //new Element( "div", {
        //    text : "下月【"+ ( parseInt( this.month ) + 1 )+ "月】",
        //    styles : this.css.monthTitleNode
        //}).inject( this.nextMonthKeyWorkNode );
        this.data.nextMonth_workList.each( function( data, index ){
            if( data.workTitle == "" ){
                var tmpTitle = ( this.data.thisMonth_workList && this.data.thisMonth_workList.length > index) ? this.data.thisMonth_workList[index].workTitle : "";
                if( tmpTitle ){
                    data.workTitle = tmpTitle;
                    this.app.restActions.saveWorkInfor( data , function(){
                        //this.app.notice( "部门重点工作保存并上传成功", "success", this.titleInput_all.getParent() )
                    }.bind(this));
                }
            }
            var keywork = new MWF.xApplication.Report.StrategyExplorer.Write.NextKeyWorkItem( this.nextMonthKeyWorkNode, this, data, {
                reportId : this.data.id,
                "isEdited" : this.options.isEdited,
                "status" : this.options.status,
                "orderNumber" : index + 1,
                "defaultTitle" : (this.data.thisMonth_workList && this.data.thisMonth_workList.length > index) ? this.data.thisMonth_workList[index].workTitle : ""
            });
            keywork.load();
            this.nexMonthKeyworkList.push( keywork );
        }.bind(this));

        if( this.options.isEdited && this.options.status=="write"){
            //new Element("button", {
            //    value : "标题沿用上月重点工作",
            //    text : "标题沿用上月重点工作",
            //    styles : this.css.normalButton,
            //    events : {
            //        click : function(){
            //            this.nextMonth_inputTitle.each( function( input, index ){
            //                if( this.data.thisMonth_workList && this.data.thisMonth_workList.length > index ){
            //                    if( input.get("value") ){
            //                        input.set("value", input.get("value") + " " +this.data.thisMonth_workList[index].workTitle );
            //                    }else{
            //                        input.set("value", this.data.thisMonth_workList[index].workTitle );
            //                    }
            //                }
            //            }.bind(this))
            //        }.bind(this)
            //    }
            //}).inject(this.nextMonthKeyWorkNode);

            var _self = this;
            this.nextMonth_inputTitle.each( function( input, index ){
                var button = new Element("button", {
                    value : "沿用标题",
                    text : "沿用标题",
                    title : "标题沿用上月重点工作",
                    styles : this.css.normalButton,
                    events : {
                        click : function(){
                            var index = this.index;
                            var input = this.input;
                            if( _self.data.thisMonth_workList && _self.data.thisMonth_workList.length > index ){
                                if( input.get("value") ){
                                    input.set("value", input.get("value") + " " +_self.data.thisMonth_workList[index].workTitle );
                                }else{
                                    input.set("value", _self.data.thisMonth_workList[index].workTitle );
                                }
                            }
                        }.bind({ input : input, index : index })
                    }
                }).inject(input, "before");

                if( _self.data.thisMonth_workList && _self.data.thisMonth_workList.length > index  ){
                    new Element("div", {
                        "styles" : { "margin-bottom" : "5px" },
                        html : this.app.common.replaceWithBr( _self.data.thisMonth_workList[index].workTitle )
                    }).inject(button, "before");
                }

            }.bind(this))
        }


        this.extWorkNode = new Element("div", {
            styles : {
                width : "96%",
                margin : "0px auto"
            }
        }).inject( this.explorer.threeworkContainer );
        var extwork = this.extWork = new MWF.xApplication.Report.StrategyExplorer.Write.ExtWork(this.extWorkNode, this, this.data, {
            reportId: this.data.id,
            "isEdited" : this.options.isEdited,
            "status" : this.options.status
        });
        extwork.load();


        //this.extFuwuKeyworkList = [];
        //this.extFuWuNode = new Element("div", {
        //    styles : this.css.monthNode
        //}).inject( this.explorer.threeworkContainer ); //客户服务
        //new Element( "div", {
        //    text : "【"+ this.month+ "月】服务客户",
        //    styles : this.css.monthTitleNode
        //}).inject( this.extFuWuNode );
        //this.data.extFuwuCategories.each( function( data, index ) {
        //    var keywork = new MWF.xApplication.Report.StrategyExplorer.Write.ExtKeyWorkItem(this.extFuWuNode, this, data, {
        //        reportId: this.data.id,
        //        category : "Fuwu",
        //        index : index+1,
        //        "isEdited" : this.options.isEdited,
        //        "status" : this.options.status
        //    });
        //    keywork.load();
        //    this.extFuwuKeyworkList.push( keywork );
        //}.bind(this));
        //
        //this.extGuanaiKeyworkList = [];
        //this.extGuanAiNode = new Element("div", {
        //    styles : this.css.monthNode
        //}).inject( this.node ); //客户服务
        //new Element( "div", {
        //    text : "【"+ this.month+ "月】关爱员工",
        //    styles : this.css.monthTitleNode
        //}).inject( this.extGuanAiNode );
        //this.data.extGuanaiCategories.each( function( data , index ) {
        //    var keywork = new MWF.xApplication.Report.StrategyExplorer.Write.ExtKeyWorkItem(this.extGuanAiNode, this, data, {
        //        reportId: this.data.id,
        //        category : "Guanai",
        //        index : index+1,
        //        "isEdited" : this.options.isEdited,
        //        "status" : this.options.status
        //    });
        //    keywork.load();
        //    this.extGuanaiKeyworkList.push( keywork );
        //}.bind(this));
        //
        //this.extYijianKeyworkList = [];
        //this.extYiJianNode = new Element("div", {
        //    styles : this.css.monthNode
        //}).inject( this.node ); //客户服务
        //new Element( "div", {
        //    text : "【"+ this.month+ "月】意见建议",
        //    styles : this.css.monthTitleNode
        //}).inject( this.extYiJianNode );
        //this.data.extYijianCategories.each( function( data , index ) {
        //    var keywork = new MWF.xApplication.Report.StrategyExplorer.Write.ExtKeyWorkItem(this.extYiJianNode, this, data, {
        //        reportId: this.data.id,
        //        category : "Yijian",
        //        index : index+1,
        //        "isEdited" : this.options.isEdited,
        //        "status" : this.options.status
        //    });
        //    keywork.load();
        //    this.extYijianKeyworkList.push( keywork );
        //}.bind(this))

    },
    getExtKeywork : function( category, id ){
        var keyworkList = this[ "ext"+category+ "KeyworkList"];
        for( var i = 0; i<keyworkList.length; i++ ){
            if( keyworkList[i].data.id == id ){
                return keyworkList[ i ]
            }
        }
        return null;
    },
    getThisMonthKeywork : function(id){
        for( var i = 0; i<this.thisMonthKeyworkList.length; i++ ){
            if( this.thisMonthKeyworkList[i].data.id == id ){
                return this.thisMonthKeyworkList[ i ]
            }
        }
        return null;
    },
    getNextMonthKeywork : function(id){
        for( var i = 0; i<this.nexMonthKeyworkList.length; i++ ){
            if( this.nexMonthKeyworkList[i].data.id == id ){
                return this.nexMonthKeyworkList[ i ]
            }
        }
        return null;
    },
    listPlan : function( workInfoId, refresh, callback ){
        if( !refresh && this.planDataObject ) {
            if(callback)callback( this.planDataObject[workInfoId] || [] ) ;
        }else{
            this.actions.listPlan( this.data.id || this.options.id, function( json ){
                this.planDataObject = {};
                json.data.each( function( d ){
                    if( !this.planDataObject[d.workInfoId] ) {
                        this.planDataObject[d.workInfoId] = [];
                    }
                    this.planDataObject[d.workInfoId].push( d );
                }.bind(this));
                if(callback)callback( this.planDataObject[workInfoId] || [] ) ;
            }.bind(this))
        }
    },
    listWork : function( workInfoId, refresh, callback ){
        if( !refresh && this.workDataObject ) {
            if(callback)callback( this.workDataObject[workInfoId] || [] ) ;
        }else{
            this.actions.listWork( this.data.id || this.options.id, function( json ){
                this.workDataObject = {};
                json.data.each( function( d ){
                    if( !this.workDataObject[d.workInfoId] ) {
                        this.workDataObject[d.workInfoId] = [];
                    }
                    this.workDataObject[d.workInfoId].push( d );
                }.bind(this));
                if(callback)callback( this.workDataObject[workInfoId] || [] ) ;
            }.bind(this))
        }
    },
    listPlanNext : function( workInfoId, refresh, callback ){
        if( !refresh && this.planNextDataObject ) {
            if(callback)callback( this.planNextDataObject[workInfoId] || [] ) ;
        }else{
            this.actions.listPlanNext( this.data.id || this.options.id, function( json ){
                this.planNextDataObject = {};
                json.data.each( function( d ){
                    if( !this.planNextDataObject[d.workInfoId] ) {
                        this.planNextDataObject[d.workInfoId] = [];
                    }
                    this.planNextDataObject[d.workInfoId].push( d );
                }.bind(this));
                if(callback)callback( this.planNextDataObject[workInfoId] || [] ) ;
            }.bind(this))
        }
    },
    //listExt : function( workInfoId, refresh, callback, category ){
    //    this.extData = this.extData || {};
    //    if( !refresh && this.extData[category] ) {
    //        if(callback)callback( this.extData[category][workInfoId] || [] ) ;
    //    }else{
    //        this.actions["list"+ category +"WithReportId"]( this.data.id || this.options.id, function( json ){
    //            this.extData[category] = {};
    //            json.data.each( function( d ){
    //                if( !this.extData[category][d.category] ) {
    //                    this.extData[category][d.category] = [];
    //                }
    //                this.extData[category][d.category].push( d );
    //            }.bind(this));
    //            if(callback)callback( this.extData[category][workInfoId] || [] ) ;
    //        }.bind(this))
    //    }
    //},
    submit: function(){
        var flag = false;
        var result = this.getResult( true );
        if( result ){
            this.actions.saveWorkPerson( result, function(){
                //this.app.notice("保存并上传成功");
                flag = true;
            }.bind(this), null, false )
        }
        if( !flag && this.errorNodeList && this.errorNodeList.length > 0 ){
            var errorNode = this.errorNodeList[0];
            if( this.app.scrollNode ){
                this.app.scrollNode.scrollTo( 0, errorNode.getCoordinates().top - this.app.scrollNode.getCoordinates().top + this.app.scrollNode.scrollTop.toFloat() - 120 );
            }
        }
        return flag;
    },
    save: function(){
        var flag = false;
        var result = this.getResult( false );
        if( result ){
            this.actions.saveWorkPerson( result, function(){
                //this.app.notice("保存并上传成功");
                flag = true;
            }.bind(this), null, false )
        }
        return flag;
    },
    verifyProcess : function( status ){
        if( this.errorNodeList && this.errorNodeList.length ){
            while( this.errorNodeList.length > 0 ){
                this.errorNodeList.pop().destroy();
            }
        }
        if( status == "confirm" || status=="audit" ){
            var flag1 = true;
            this.thisMonth_inputContent_all.each( function( input ){
                if( input.get("value") == "" ){
                    flag1 = false;
                    this.createErrorNode( input, "请填写工作总结" )
                }
            }.bind(this));

            var flag2 = true;
            this.nextMonth_inputTitle_all.each( function( input ){
                if( input.get("value") == "" ){
                    flag2 = false;
                    this.createErrorNode( input, "请填写标题" )
                }
            }.bind(this));

            var flag3 = true;
            this.nextMonth_inputContent_all.each( function( input ){
                if( input.get("value") == "" ){
                    flag3 = false;
                    this.createErrorNode( input, "请填写工作计划" )
                }
            }.bind(this));

            var flag7 = true;
            //this.nexMonthKeyworkList.each( function( keywork ){
            //    var measuresList = keywork.getMeasuresList();
            //    if( !measuresList || measuresList.length == 0 ){
            //        flag7 = false;
            //        this.createErrorNode( keywork.measureContentNode, "请选择举措" );
            //    }
            //}.bind(this));

            var flag4,flag5, flag6;
            this.extWork.gridContainer.getElements( "textarea").each( function( textarea ){
                var name = textarea.get("name").split("_")[0];
                if( textarea.get("value").trim() != "" ){
                    if( name == "fuwu" )flag4 = true;
                    if( name == "guanai" )flag5 = true;
                    if( name == "yijian" )flag6 = true;
                }
            }.bind(this));

            var errorTextArr = [];
            if( !flag4 )errorTextArr.push("至少需要填写一项服务客户内容");
            if( !flag5 )errorTextArr.push("至少需要填写一项关爱员工内容");
            if( !flag6 )errorTextArr.push("至少需要填写一项意见建议内容");
            if( errorTextArr.length > 0 ){
                this.createErrorNode( this.extWork.gridContainer, errorTextArr.join(",")+ "。")
            }

            errorTextArr = [];
            if( !flag1 )errorTextArr.push("本月工作总结不能为空");
            if( !flag2 )errorTextArr.push("下月部门重点工作不能为空");
            if( !flag3 )errorTextArr.push("下月工作计划工作不能为空");
            if( !flag7 )errorTextArr.push("下月举措不能为空");
            if( !flag4 )errorTextArr.push("至少需要填写一项服务客户内容");
            if( !flag5 )errorTextArr.push("至少需要填写一项关爱员工内容");
            if( !flag6 )errorTextArr.push("至少需要填写一项意见建议内容");
            if( errorTextArr.length > 0 ){
                this.app.notice( errorTextArr.join(",<br/>") + "。", "error" );
            }

            return flag1 && flag2 && flag3 && flag4 && flag5 && flag6;
        }
        return true;

    },
    getResult : function( verify ){
        if( this.errorNodeList ){
            this.errorNodeList.each( function(node){
                node.destroy();
            }.bind(this));
            this.errorNodeList = []
        }
        var flag = true;

        if( this.options.isEdited && this.options.status == "confirm" ){
            var workList = [];

            this.nexMonthKeyworkList.each( function( keywork ){
                var title = keywork.getWorkTitle();
                if( !title || title.length == 0 ){
                    if( verify ){
                        this.createErrorNode( keywork.workTitleInput, "请填写工作标题", { "float" : "left" } );
                        flag = false;
                    }
                }

                var measuresList = keywork.getMeasuresList();
                if( !measuresList || measuresList.length == 0 ){
                    if( verify ){
                        this.createErrorNode( keywork.measureContentNode, "请选择举措" );
                        flag = false;
                    }
                }
                workList.push({
                    id : keywork.data.id,
                    orderNumber : keywork.getOrderNumber(),
                    workTitle : title,
                    measuresList : measuresList
                }) ;
            }.bind(this));

            if( !flag )return false;
            return {
                id : this.data.id,
                workList : workList
            }
        }

    },
    createErrorNode : function(node, text, styles){
        if( !this.errorNodeList )this.errorNodeList = [];
        var div = new Element("div", {
            text : text,
            styles : this.css.warningMessageNode
        }).inject( node, "after" );
        if(styles)div.setStyles(styles);
        this.errorNodeList.push( div );
    }
});

MWF.xApplication.Report.StrategyExplorer.Write.ThisKeyWorkItem = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "reportId" : "",
        "isEdited" : true,
        "orderNumber" : 1,
        "status" : ""
    },
    initialize: function(container, explorer, data, options ) {
        this.setOptions(options);
        this.container = container;
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.css = this.explorer.css;
        this.actions = this.app.restActions;
        this.data = data;
    },
    load: function(){
        var status = this.options.status;
        var table = new Element( "table", {
            "width":"96%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : this.css.formTable
        }).inject( this.container  );

        var tr = new Element("tr").inject( table );

        new Element("td", {  "rowspan" : 3, "text" : this.data.orderNumber || this.options.orderNumber, "width" : "30", "styles": this.css.formTableTitle }).inject( tr );
        new Element("td", { "text" : "部门重点工作",  "width" : "140", "styles": this.css.formTableTitle }).inject( tr );

        var contentTd = new Element("td", { "styles": this.css.formTableValue  }).inject( tr );
        new Element("div", {
            "html" : this.app.common.replaceWithBr( this.data.workTitle ),
            styles : { "width" : "870px", "float" : "left" }
        }).inject( contentTd );
        var showMeasureNode = new Element("input",{
            "type" : "button",
            "styles" : this.css.showMeasureNode,
            "value" : "查看举措"
        }).inject( contentTd );
        var tooltip = new MWF.xApplication.Report.ShowMeasureTooltip( this.app.content, showMeasureNode, this.app, this.explorer.data, {
            style : "report",
            position : { x : "auto", y : "auto" },
            event : "click"
        });
        tooltip.measuresList = this.data.measuresList;

        tr = new Element("tr").inject( table );
        new Element("td", {  "text" : "工作计划", "styles": this.css.formTableTitle }).inject( tr );
        contentTd = new Element("td", {
            "styles": this.css.formTableValue,
            html : this.app.common.replaceWithBr( this.data.workPlanSummary )
        }).inject( tr );
        //this.loadPlanView( contentTd );

        tr = new Element("tr").inject( table );
        new Element("td", {  "text" : "工作总结", "styles": this.css.formTableTitle }).inject( tr );
        contentTd = new Element("td", { "styles": this.css.formTableValue  }).inject( tr );
        //this.loadWorkAction( contentTd );
        if( status == "confirm" || status == "write" ){
            this.loadWorkView( contentTd );
        }
        if( status == "confirm" || status == "audit" ){
            if( this.options.isEdited ){
                this.contentInput_all = new Element("textarea", {
                    placeholder :  status == "confirm" ? "请汇总工作总结" : "",
                    styles : this.css.textarea,
                    value : this.data.workProgSummary
                }).inject( contentTd );
                this.explorer.thisMonth_inputContent_all.push( this.contentInput_all );
                this.contentInput_all.addEvent("blur", function(){
                    if( this.data.workProgSummary != this.contentInput_all.get("value") ){
                        this.data.workProgSummary = this.contentInput_all.get("value");
                        if(this.app.onChangeValue)this.app.onChangeValue();
                    }
                    this.app.restActions.saveWorkProgSummary( {
                        id : this.data.id,
                        workProgSummary : this.contentInput_all.get("value")
                    }, function(){
                        this.app.notice( "工作总结保存并上传成功", "success", this.contentInput_all.getParent() )
                    }.bind(this))
                }.bind(this))
            }else{
                if( status == "confirm" ){
                    var div = new Element("div").inject( contentTd  );
                    var html = "<table width='100%' bordr='0' cellpadding='3' cellspacing='0' style='margin-top: 5px; '>" +
                        "<tr><td width='70' align='center'>汇总:</td>" +
                        "    <td>"+this.app.common.replaceWithBr( this.data.workProgSummary )+"</td>"+
                        "</table>";
                    div.set("html", html);
                }else{
                    var div = new Element("div").inject( contentTd  );
                    div.set("html", this.app.common.replaceWithBr( this.data.workProgSummary ));
                }
            }
        }
    },
    addContentToAll : function( data ){
        var oldValue = this.contentInput_all.get("value");
        if( !oldValue ){
            this.contentInput_all.set("value", data.progressContent); //JSON.stringify( data ))
        }else{
            this.contentInput_all.set("value", oldValue + " " + data.progressContent); //JSON.stringify( data ))
        }

        if(this.app.onChangeValue)this.app.onChangeValue();
        this.data.workProgSummary = this.contentInput_all.get("value");

        this.app.restActions.saveWorkProgSummary( {
            id : this.data.id,
            workProgSummary : this.contentInput_all.get("value")
        }, function(){
            this.app.notice( "工作总结保存并上传成功", "success", this.contentInput_all.getParent() )
        }.bind(this))
    },
    loadPlanView : function( container ){
        this.planViewNode = new Element("div").inject( container );
        this.planView = new MWF.xApplication.Report.StrategyExplorer.PlanView(this.planViewNode, this.app, this, {
            "style": "default",
            "documentSortable" : false, //this.options.status == "confirm" && this.options.isEdited,
            "reportId" : this.options.reportId,
            "isEdited" : this.options.isEdited,
            "templateUrl": this.explorer.path+"listItemPlan.json",
            "onDocumentSortComplete" : function( element, serial ){
                var arr = [];
                this.planView.node.getElements("[item='id']").each( function( el, index ){
                    var id = el.get("text");
                    this.planView.documents[id].data.orderNumber = index+1;
                    arr.push( {
                        id : id,
                        orderNumber : index+1
                    })
                }.bind(this));
                this.actions.updatePlanOrder( { orderList : arr } )
            }.bind(this)
        });
        this.planView.report = this.explorer.explorer;
        this.planView.data = this.data.planList;
        this.planView.load();
    },
    loadWorkView : function( container ){
        //this.workViewNode = new Element("div").inject( this.workNode);
        this.workView = new MWF.xApplication.Report.StrategyExplorer.WorkView( container || this.workViewNode, this.app, this, {
            "style": "default",
            "documentSortable" : false, //this.options.status == "confirm" && this.options.isEdited,
            "reportId" : this.options.reportId,
            "isEdited" : this.options.isEdited,
            "templateUrl": this.explorer.path+"listItemWork.json",
            "onDocumentSortComplete" : function( element, serial ){
                var arr = [];
                this.workView.node.getElements("[item='id']").each( function( el, index ){
                    var id = el.get("text");
                    this.workView.documents[id].data.orderNumber = index+1;
                    arr.push( {
                        id : id,
                        orderNumber : index+1
                    })
                }.bind(this));
                this.actions.updateWorkOrder( { orderList : arr } )
            }.bind(this)
        });
        this.workView.report = this.explorer.explorer;
        this.workView.data = this.data.progList;
        this.workView.load();
    }
});

MWF.xApplication.Report.StrategyExplorer.Write.NextKeyWorkItem = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "reportId" : "",
        "isEdited" : false,
        "orderNumber" : 1,
        "status" : "",
        "defaultTitle" :  ""
    },
    initialize: function(container, explorer, data, options ) {
        this.setOptions(options);
        this.container = container;
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.css = this.explorer.css;
        this.actions = this.app.restActions;
        this.data = data;
    },
    load: function(){
        var status = this.options.status;
        var table = new Element( "table", {
            "width":"96%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : this.css.formTable
        }).inject( this.container  );

        var tr = new Element("tr").inject( table );
        new Element("td", {  "rowspan" : status == "write" ? 3 : 4, "text" : this.data.orderNumber || this.options.orderNumber, "width" : "30", "styles": this.css.formTableTitle }).inject( tr );
        new Element("td", {
            "text" : status == "write" ? "标题" : "部门重点工作",
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );
        var contentTd = new Element("td", { "styles": this.css.formTableValue  }).inject( tr );
        if( status == "confirm" || status == "write" ) {
            this.loadPlanTitleViewNext(contentTd);
        }
        if( status == "confirm" || status == "audit" ){
            if( this.options.isEdited ){
                this.titleInput_all = new Element("textarea", {
                    placeholder : status == "confirm" ? "请汇总部门重点工作" : "",
                    styles : this.css.textarea,
                    value : this.data.workTitle || this.options.defaultTitle
                }).inject( contentTd );

                this.explorer.nextMonth_inputTitle_all.push( this.titleInput_all );

                this.titleInput_all.addEvent("blur", function(){
                    var data = Object.clone(this.data);
                    delete data.planNextList;
                    delete data.planList;
                    //data.measuresList = this.selectedMeasuresList ;

                    if( this.data.workTitle != this.titleInput_all.get("value") ){
                        this.data.workTitle = this.titleInput_all.get("value");
                        if(this.app.onChangeValue)this.app.onChangeValue();
                    }

                    data.workPlanSummary = this.contentInput_all.get("value");
                    data.workTitle = this.titleInput_all.get("value");
                    //data.orderNumber =  this.data.orderNumber || this.options.orderNumber;
                    this.app.restActions.saveWorkInfor( data , function(){
                        this.app.notice( "部门重点工作保存并上传成功", "success", this.titleInput_all.getParent() )
                    }.bind(this));
                }.bind(this))
            }else{
                if( status == "confirm" ){
                    var div = new Element("div").inject( contentTd  );
                    var html = "<table width='100%' bordr='0' cellpadding='3' cellspacing='0' style='margin-top: 5px; '>" +
                        "<tr><td width='70' align='center'>汇总:</td>" +
                        "    <td>"+this.app.common.replaceWithBr( this.data.workTitle )+"</td>"+
                        "</table>";
                    div.set("html", html);
                }else{
                    var div = new Element("div").inject( contentTd  );
                    div.set("html", this.app.common.replaceWithBr( this.data.workTitle ));
                }
            }
        }

        //if( this.options.isEdited ){
        //    this.workTitleInput = new Element("input", {
        //        "value" : this.data.workTitle,
        //        "styles" : this.css.keyWorkTitleInput
        //    }).inject( contentTd )
        //}else{
        //    new Element("div", {
        //        "text" : this.data.workTitle
        //    }).inject( contentTd )
        //}

        if( status == "confirm" || status == "audit" ){
            var tr = new Element("tr").inject( table );
            new Element("td", {
                "text" : "举措",
                "width" : "140",
                "styles": this.css.formTableTitle
            }).inject( tr );
            var contentTd = new Element("td", { "styles": this.css.formTableValue  }).inject( tr );

            var table_m = new Element("table", { width: "100%", border : 0, cellspacing : 0, cellpadding : 0, styles : this.css.listViewTable }).inject( contentTd );
            var tr_m = new Element("tr", { styles : this.css.listViewTableTr }).inject( table_m );
            var td_m = new Element("td", { styles : this.css.listViewTableTd }).inject( tr_m );
            this.measureContentNode = new Element("div").inject( td_m );
            this.loadMeasureList( this.data.measuresList || []);

            td_m = new Element("td", { styles : this.css.listViewTableTd, width : "80" }).inject( tr_m );
            if( this.options.isEdited ){
                var button = new Element("button", {
                    text : "选择举措",
                    styles : this.css.selectMeasureAction
                    //events : {
                    //    "click" : function(){
                    //
                    //        //var form = new MWF.xApplication.Report.SelectMeasureForm(this, this.explorer.data, {
                    //        //    onPostOk: function( list, value ){
                    //        //        this.measureContentNode.empty();
                    //        //        var idList = [];
                    //        //        list.each( function( it ){
                    //        //            idList.push( it.id );
                    //        //        });
                    //        //        this.loadMeasureList( idList );
                    //        //        this.measuresList = idList;
                    //        //    }.bind(this)
                    //        //}, { app : this.app });
                    //        //form.edit();
                    //    }.bind(this)
                    //}
                }).inject( td_m );
                var tooltip = new MWF.xApplication.Report.SelectMeasureTooltips( this.app.content, button, this.app, this.explorer.data, {
                    style : "report",
                    hasArrow : false,
                    position : { x : "auto", y : "auto" },
                    event : "click",
                    onSelect : function( list, value ){
                        this.measureContentNode.empty();
                        var idList = [];
                        list.each( function( it ){
                            idList.push( it.id );
                        });
                        this.loadMeasureList( idList );

                        if(this.app.onChangeValue)this.app.onChangeValue();

                        this.measuresList = this.data.measuresList = idList;

                        var data = Object.clone(this.data);
                        delete data.planNextList;
                        delete data.planList;
                        //data.measuresList = this.selectedMeasuresList ;
                        data.workPlanSummary = this.contentInput_all.get("value");
                        data.workTitle = this.titleInput_all.get("value");
                        this.app.restActions.saveWorkInfor( data , function(){
                            this.app.notice( "举措保存并上传成功", "success" )
                        }.bind(this));
                    }.bind(this)
                });
                tooltip.measuresList = this.data.measuresList;
            }
        }


        tr = new Element("tr").inject( table );
        new Element("td", {  "text" : "工作计划", "styles": this.css.formTableTitle }).inject( tr );
        contentTd = new Element("td", { "styles": this.css.formTableValue  }).inject( tr );
        //this.loadPlanNextAction( contentTd, true ,true );
        if( status == "confirm" || status == "write" ){
            this.loadPlanViewNext( contentTd );
        }
        if( status == "confirm" || status == "audit" ){
            if( this.options.isEdited ) {
                this.contentInput_all = new Element("textarea", {
                    placeholder: status == "confirm" ? "请汇总计划" : "",
                    styles: this.css.textarea,
                    value: this.data.workPlanSummary
                }).inject(contentTd);
                this.explorer.nextMonth_inputContent_all.push( this.contentInput_all );
                this.contentInput_all.addEvent("blur", function () {

                    if( this.data.workPlanSummary != this.contentInput_all.get("value") ){
                        this.data.workPlanSummary = this.contentInput_all.get("value");
                        if(this.app.onChangeValue)this.app.onChangeValue();
                    }

                    this.app.restActions.saveWorkPlanSummary({
                        id: this.data.id,
                        workPlanSummary: this.contentInput_all.get("value")
                    }, function(){
                        this.app.notice( "工作计划保存并上传成功", "success", this.contentInput_all.getParent() )
                    }.bind(this))
                }.bind(this))
            }else{
                if( status == "confirm" ){
                    var div = new Element("div").inject( contentTd  );
                    var html = "<table width='100%' bordr='0' cellpadding='3' cellspacing='0' style='margin-top: 5px; '>" +
                        "<tr><td width='70' align='center'>汇总:</td>" +
                        "    <td>"+this.app.common.replaceWithBr( this.data.workPlanSummary )+"</td>"+
                        "</table>";
                    div.set("html", html);
                }else{
                    var div = new Element("div").inject( contentTd  );
                    div.set("html", this.app.common.replaceWithBr( this.data.workPlanSummary ));
                }
            }
        }
    },
    addTitleToAll : function( data ){
        var oldValue = this.titleInput_all.get("value");
        if( !oldValue ){
            this.titleInput_all.set("value", data.title); //JSON.stringify( data ))
        }else{
            this.titleInput_all.set("value", oldValue + " " + data.title); //JSON.stringify( data ))
        }
        var data = Object.clone(this.data);
        delete data.planNextList;
        delete data.planList;

        this.data.workTitle = this.titleInput_all.get("value");
        if(this.app.onChangeValue)this.app.onChangeValue();

        //data.measuresList = this.selectedMeasuresList ;
        data.workPlanSummary = this.contentInput_all.get("value");
        data.workTitle = this.titleInput_all.get("value");
        this.app.restActions.saveWorkInfor( data, function(){
            this.app.notice( "部门重点工作保存并上传成功", "success", this.titleInput_all.getParent() )
        }.bind(this));
    },
    addContentToAll : function( data ){
        var oldValue = this.contentInput_all.get("value");
        if( !oldValue ){
            this.contentInput_all.set("value", data.planContent); //JSON.stringify( data ))
        }else{
            this.contentInput_all.set("value", oldValue + " " + data.planContent); //JSON.stringify( data ))
        }

        this.data.workPlanSummary = this.contentInput_all.get("value");
        if(this.app.onChangeValue)this.app.onChangeValue();

        this.app.restActions.saveWorkPlanSummary({
            id: this.data.id,
            workPlanSummary: this.contentInput_all.get("value")
        }, function(){
            this.app.notice( "工作计划保存并上传成功", "success", this.contentInput_all.getParent() )
        }.bind(this))
    },
    getOrderNumber : function(){
        return this.data.orderNumber || this.options.orderNumber;
    },
    getWorkTitle : function(){
        return this.workTitleInput.get("value");
    },
    getMeasuresList : function(){
        return this.measuresList || this.data.measuresList;
    },
    loadMeasureList: function( list ){
        this.selectableMeasureObject = {};
        ( this.explorer.data.nextMonth_selectableMeasures || [] ).each( function( m ){
            this.selectableMeasureObject[ m.id ]=m;
        }.bind(this));

        var table_m = new Element("table", { width: "100%", border : 0, cellspacing : 0, cellpadding : 3, styles : this.css.listViewTable }).inject( this.measureContentNode );
        list.each( function( id,i ){
            if( id ){
                var data = this.selectableMeasureObject[ id ];
                var tr_m = new Element("tr", { styles : this.css.listViewTableTr }).inject( table_m );

                var td_m = new Element("td", { styles : this.css.listViewTableTd , width : "40"}).inject( tr_m );
                var iconNode = new Element("div", {
                    styles : this.css.descriptionNode,
                    text : "详情"
                }).inject( td_m );
                this.loadMeasureTooltip( iconNode, id );

                //var td_m = new Element("td", { styles : this.css.listViewTableTd, width : "10", text : i + ":" }).inject( tr_m );
                var td_m = new Element("td", { styles : this.css.listViewTableTd, text : data.measuresinfotitle }).inject( tr_m );
                //var tetNode = new Element("div.itemMeasureTextNode", {
                //    styles : this.css.itemMeasureTextNode,
                //    text :  (i+1) + "：" +data.measuresinfotitle
                //}).inject( measureNoe );
            }
        }.bind(this));
    },
    loadMeasureTooltip: function( node, measureId ){
        var tooltip = new MWF.xApplication.Report.MeasureTooltip( this.app.content, node, this.app, this.selectableMeasureObject[ measureId ], {
            style : "report",
            position : { x : "auto", y : "auto" },
            measureId : measureId,
            displayDelay : 300
        })
    },
    loadPlanTitleViewNext : function( container ){
        var _self = this;
        this.planViewTitleNextNode = new Element("div",{
            "data-id" : this.data.id
        }).inject( container );
        this.planTitleViewNext = new MWF.xApplication.Report.StrategyExplorer.PlanTitleViewNext(this.planViewTitleNextNode, this.app, this, {
            "style": "default",
            "reportId" : this.options.reportId,
            "isEdited" : this.options.isEdited,
            "templateUrl": this.explorer.path+"listItemPlanTitleNext.json"
        });
        this.planTitleViewNext.report = this.explorer.explorer;

        //this.planViewNext.data = this.data.planNextList;
        this.planTitleViewNext.load();
    },
    loadPlanViewNext : function( container ){
        var _self = this;
        this.planViewNextNode = new Element("div",{
            "data-id" : this.data.id
        }).inject( container );
        this.planViewNext = new MWF.xApplication.Report.StrategyExplorer.PlanViewNext(this.planViewNextNode, this.app, this, {
            "style": "default",
            "reportId" : this.options.reportId,
            "isEdited" : this.options.isEdited,
            "templateUrl": this.explorer.path+"listItemPlanNext.json"
        });
        this.planViewNext.report = this.explorer.explorer;

        //this.planViewNext.data = this.data.planNextList;
        this.planViewNext.load();
    },
    savePersonPlan: function( ev, noticeText ){
        var data = this.currentPersonData;
        var title = this.titleInput.get("value").trim();
        var content = this.contentInput.get("value").trim();
        if( title || content ){
            if( !data ){ //如果没有保存过
                data = {};
                var reportData = this.explorer.explorer.data;
                var keyworkData = this.data;
                data.reportId = reportData.id;
                data.workInfoId = keyworkData.id;
                data.keyWorkId = keyworkData.keyWorkId;
                data.workTitle = keyworkData.workTitle;
                data.flag = reportData.flag;
                data.year = reportData.year;
                data.month = reportData.month;
                data.week = reportData.week;
                data.date = reportData.date;
                data.targetPerson = ( layout.desktop.session.user || layout.user ).distinguishedName;
                data.orderNumber = this.options.orderNumber || 1;
            }
            data.title = title;

            data.workDescribe = content;
            data.planContent = content;

            this.app.restActions.savePlanNext( data, function(json){
                var id = json.data.id;
                this.app.restActions.getPlanNext( id, function(js){
                    this.currentPersonData = js.data;
                }.bind(this));
                this.app.notice( noticeText, "success", ev.target.getParent() )
            }.bind(this));
        }else if( !title && !content && data ){ //清空内容了，需要删除
            this.app.restActions.deletePlanNext( data.id, function(){
                this.currentPersonData = null;
                this.app.notice( noticeText, "success", ev.target.getParent() )
            }.bind(this))
        }
    }
});

MWF.xApplication.Report.StrategyExplorer.Write.ExtWork = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "reportId": "",
        "isEdited": true,
        "status": ""
    },
    initialize: function (container, explorer, data, options) {
        this.setOptions(options);
        this.container = container;
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.css = this.explorer.css;
        this.actions = this.app.restActions;
        this.data = data;
    },
    load: function () {
        this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;
        this.summaryContainer_last = new Element("div").inject(this.container);
        this.personContainer = new Element("div").inject(this.container);
        this.summaryContainer = new Element("div").inject(this.container);
        this.listSummary_last();
        if( this.options.status == "confirm" || this.options.status == "write" ){
            this.listPersonData();
        }
        if( this.options.status == "confirm" || this.options.status == "audit" ){
            this.listSummaryData();
        }
    },
    listSummary_last : function(){
        if( this.data.WoReport_I_Ext_Contents_sumamry_lastMonth ){
            var table = this.table = new Element( "table", {
                "width":"100%",
                "border":"0",
                "cellpadding":"5",
                "cellspacing":"0",
                "styles" : this.css.formTable
            }).inject( this.summaryContainer_last   );

            var tr = new Element("tr").inject( table );
            new Element("td", {  colspan : 4, "styles": this.css.formTableTitle, text : "上月汇总" }).inject( tr );

            tr = new Element("tr").inject( table );
            new Element("td", {  "width" : "30", "styles": this.css.formTableTitle, text : "序号" }).inject( tr );
            new Element("td", {  "width" : "140", "styles": this.css.formTableTitle, text : "服务客户" }).inject( tr );
            new Element("td", {  "width" : "140", "styles": this.css.formTableTitle, text : "关爱员工" }).inject( tr );
            new Element("td", {  "width" : "140", "styles": this.css.formTableTitle, text : "意见建议" }).inject( tr );

            this.data.WoReport_I_Ext_Contents_sumamry_lastMonth.each( function( d, i ){
                tr = new Element("tr").inject( table );

                var td = new Element("td", {  "width" : "30", "align" : "center", "styles": this.css.formTableValue, text : i+1 }).inject( tr );
                td.setStyle("text-align","center");

                new Element("td", {
                    "text" : d.fuwu,
                    "width" : "140",
                    "styles": this.css.formTableValue
                }).inject( tr );

                new Element("td", {
                    "text" : d.guanai,
                    "width" : "140",
                    "styles": this.css.formTableValue
                }).inject( tr );

                new Element("td", {
                    "text" : d.yijian,
                    "width" : "140",
                    "styles": this.css.formTableValue
                }).inject( tr );
            }.bind(this) )

        }
    },
    listSummaryData : function(){
        var summaryData = this.data.WoReport_I_Ext_Contents_sumamry;
        if( !summaryData || !summaryData.length ){
            for( var i = 1; i<=5; i++ ){
                var  d = {
                    "infoLevel": "汇总",
                    "orderNumber" : i
                };
                this.app.restActions.saveExtWork( this.data.id, d, function( json ){
                }.bind(this), null, false)
            }
            this.actions.listExtWorkWithReportId(this.data.id, { "infoLevel": "汇总" }, function( json ){
                this.loadSummaryGrid( json.data );
            }.bind(this));
        }else{
            this.loadSummaryGrid( this.data.WoReport_I_Ext_Contents_sumamry );
        }
    },
    listPersonData : function(){
        var currentPersonData = [];
        var otherPersonData = [];
        //this.actions.listExtWorkWithReportId(this.options.reportId, { "infoLevel": "员工" }, function( json ){
        var array = this.data.WoReport_I_Ext_Contents || [];
        array.sort( function( a, b ){
            var flag = a.targetPerson.localeCompare(b.targetPerson);
            if( flag == 0 ){ //相等
                return a.orderNumber - b.orderNumber;
            }else{
                return flag;
            }
        });
        array.each( function(d){
            if(d.targetPerson == this.userName && this.options.status == "write" ){
                currentPersonData.push( d );
            }else{
                if( d.fuwu || d.guanai || d.yijian ){
                    otherPersonData.push( d );
                }
            }
        }.bind(this));
        this.loadPersonTable( otherPersonData );
        if( this.options.status == "write" ){
            this.loadPersonGrid( currentPersonData );
        }
        //}.bind(this));
    },
    loadSummaryGrid : function( data ){
        this.gridContainer = new Element("div").inject( this.summaryContainer );
        MWF.xDesktop.requireApp("Template", "MGrid", function () {
            var extwork_grid = new MGrid( this.gridContainer, data, {
                style: "report",
                isEdited:  this.options.isEdited ,
                hasOperation: false,
                hasSequence: true,
                tableAttributes: {width: "100%", border: "0", cellpadding: "5", cellspacing: "0"},
                itemTemplate: {
                    fuwu : {
                        attr : { placeholder: "请汇总服务客户"},
                        text: "服务客户",
                        type : "textarea",
                        event : { "blur" : function( item, ev ){ this.saveExtWork_summary( item, ev, "服务客户" ) }.bind(this) }
                    },
                    guanai: {
                        attr : { placeholder: "请汇总关爱员工"},
                        text: "关爱员工",
                        type : "textarea",
                        event : { "blur" : function(item, ev){ this.saveExtWork_summary( item , ev, "关爱员工") }.bind(this) }
                    },
                    yijian: {
                        attr : { placeholder: "请汇总意见建议"},
                        text: "意见建议",
                        type : "textarea",
                        event : { "blur" : function(item, ev){ this.saveExtWork_summary(item, ev, "意见建议") }.bind(this) }
                    }
                },
                onPostCreateTable: function ( grid ){
                    var table = grid.table;
                    var tr = new Element("tr").inject(table);
                    new Element("td", {  "colspan" :  4, "styles": this.css.formTableTitle, text : "本月汇总" }).inject( tr );
                },
                onQueryCreateTr: function () {
                },
                onQueryRemoveTr: function(e, el, trObj){
                }.bind(this)
            }, this.app);
            //vote_grid.setThTemplate("<tr><th label='fuwu' style=''></th><th button_add></th></tr>");
            //vote_grid.setTrTemplate("<tr><td><div item='optionTextContent' style='padding-top:10px'></div><div item='optionPictureId' style='padding-top: 5px;'></div></td><td button_remove style='vertical-align: top;padding-top:15px;'></td></tr>");
            extwork_grid.load();
            var addTrCount = data.length ? 5 - data.length : 5;
            extwork_grid.addTrs( addTrCount - 1 );
        }.bind(this), true);
    },
    loadPersonTable: function( data ){
        var table = this.table = new Element( "table", {
            "width":"100%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : this.css.formTable
        }).inject( this.personContainer );

        var text = this.options.status == "write" ? "本月同事填写" : "本月员工填写";
        var tr = new Element("tr").inject(table);
        new Element("td", {  "colspan" :  (this.options.isEdited ? 5 : 4), "styles": this.css.formTableTitle, text : text }).inject( tr );

        var tr = new Element("tr").inject( table );
        new Element("td", {  "width" : "20", "styles": this.css.formTableTitle, text : "姓名" }).inject( tr );
        new Element("td", {  "width" : "100", "styles": this.css.formTableTitle, text : "服务客户" }).inject( tr );
        new Element("td", {  "width" : "100", "styles": this.css.formTableTitle, text : "关爱员工" }).inject( tr );
        new Element("td", {  "width" : "100", "styles": this.css.formTableTitle, text : "意见建议" }).inject( tr );

        data.each( function(d){
            var tr = new Element("tr").inject( table );
            new Element("td", {  "styles": this.css.formTableValue, text : d.targetPerson.split("@")[0] }).inject( tr );
            new Element("td", {  "styles": this.css.formTableValue, text : d.fuwu }).inject( tr );
            new Element("td", {  "styles": this.css.formTableValue, text : d.guanai }).inject( tr );
            new Element("td", {  "styles": this.css.formTableValue, text : d.yijian }).inject( tr );
        }.bind(this))

    },
    loadPersonGrid : function( data ){
        this.gridContainer = new Element("div").inject( this.personContainer );
        MWF.xDesktop.requireApp("Template", "MGrid", function () {
            var extwork_grid = new MGrid( this.gridContainer, data, {
                style: "report",
                isEdited:  this.options.isEdited ,
                hasOperation: true,
                hasSequence: true,
                minTrCount: 1,
                maxTrCount : 5,
                tableAttributes: {width: "100%", border: "0", cellpadding: "5", cellspacing: "0"},
                lp : {
                    "add" : "添加",  "remove" : "删除"
                },
                itemTemplate: {
                    fuwu : {
                        text: "服务客户",
                        type : "textarea",
                        event : { "blur" : function( item, ev ){ this.saveExtWork_person( item, ev, "服务客户" ) }.bind(this) }
                    },
                    guanai: {
                        text: "关爱员工",
                        type : "textarea",
                        event : { "blur" : function(item, ev){ this.saveExtWork_person( item , ev, "关爱员工") }.bind(this) }
                    },
                    yijian: {
                        text: "意见建议",
                        type : "textarea",
                        event : { "blur" : function(item, ev){ this.saveExtWork_person(item, ev, "意见建议") }.bind(this) }
                    }
                },
                onNewData: function ( grid, callback ) {
                    var list = grid.trList;
                    if( list.length > 0 ){
                        var last = list[ list.length - 1 ];
                        var orderNumber = last.sourceData.orderNumber + 1;
                    }else{
                        var orderNumber = 1;
                    }
                    if( callback )callback( this.addEmptyExtWork_person( orderNumber ) ) ;
                }.bind(this),
                onPostCreateTable: function ( grid ){
                    var table = grid.table;
                    var tr = new Element("tr").inject(table);
                    new Element("td", {  "colspan" :  (this.options.isEdited ? 5 : 4), "styles": this.css.formTableTitle, text : "本月本人填写" }).inject( tr );
                },
                onQueryRemoveTr: function(e, el, trObj){
                    if( trObj.sourceData && trObj.sourceData.id ){
                        this.app.restActions.deleteExtWork( this.options.reportId, trObj.sourceData.id, function(){
                        }.bind(this), null, false)
                    }
                }.bind(this)
            }, this.app);
            //vote_grid.setThTemplate("<tr><th label='fuwu' style=''></th><th button_add></th></tr>");
            //vote_grid.setTrTemplate("<tr><td><div item='optionTextContent' style='padding-top:10px'></div><div item='optionPictureId' style='padding-top: 5px;'></div></td><td button_remove style='vertical-align: top;padding-top:15px;'></td></tr>");
            extwork_grid.load();
            //if (!d) {
            //extwork_grid.addTrs(3);
            //}
        }.bind(this), true);
    },
    saveExtWork_summary:  function( item, ev, noticeText ){
        var result = item.parent.getResult(true, "<br>", false, false, true );
        result.infoLevel = "汇总";
        if(!result.orderNumber){
            result.orderNumber = item.options.index;
        }

        if(this.app.onChangeValue)this.app.onChangeValue();

        this.app.restActions.saveExtWork( this.data.id, result, function( json ){
            if( !result.id ){
                this.app.restActions.getExtWork( json.data.id, function( js ) {
                    item.parent.sourceData = js.data;
                }.bind(this), null, false);
            }
            this.app.notice( noticeText+"保存并上传成功", "success", ev.target.getParent("tr") );
        }.bind(this), null, false)
    },
    saveExtWork_person : function( item, ev, noticeText ){
        var result = item.parent.getResult(true, "<br>", false, false, true );
        result.infoLevel = "员工";
        if( !result.orderNumber )result.orderNumber = 1;
        this.app.restActions.saveExtWork( this.data.id, result, function( json ){
            if( !result.id ){
                this.app.restActions.getExtWork( json.data.id, function( js ) {
                    item.parent.sourceData = js.data;
                }.bind(this), null, false);
            }
            this.app.notice( noticeText+"保存并上传成功", "success", ev.target.getParent("tr") );
        }.bind(this), null, false)
    },
    addEmptyExtWork_person:  function( orderNumber ){
        var result = {};
        result.infoLevel = "员工";
        result.orderNumber = orderNumber;
        var d;
        this.app.restActions.saveExtWork( this.data.id, result, function( json ){
            this.app.restActions.getExtWork( json.data.id, function( js ) {
                d = js.data;
            }.bind(this), null, false);
        }.bind(this), null, false);
        return d;
    }
    //saveExtWork:  function( item, infoLevel ){
    //    var result = item.parent.getResult(true, "<br>", false, false, true );
    //    if( result.fuwu || result.guanai || result.yijian ){
    //        result.infoLevel = infoLevel;
    //        this.app.restActions.saveExtWork( this.data.id, result, function( json ){
    //            if( !result.id ){
    //
    //                this.app.restActions.getExtWork( json.data.id, function( js ) {
    //                    //if( !item.parent.sourceData )item.parent.sourceData = {};
    //                    //item.parent.sourceData.id = json.data.id;
    //                    item.parent.sourceData = js.data;
    //                }.bind(this));
    //            }
    //        }.bind(this), null, false)
    //    }else if( result.id ){
    //        if( infoLevel == "员工" ){
    //            this.app.restActions.deleteExtWork( result.id, this.options.reportId, function(){
    //                item.parent.sourceData = {};
    //            }.bind(this), null, false)
    //        }
    //    }
    //}
});

MWF.xApplication.Report.StrategyExplorer.WorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.Report.StrategyExplorer.WorkDocument( this.viewBodyNode || this.viewNode, data, this.departmentexplorer, this, null, index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        //this.actions.listWork( this.options.reportId, function( json ){
        //    if( callback )callback( json );
        //})
        this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;

        if( !this.loaded ){
            this.loaded = true;
            var arr = this.explorer.data.progList || [];
            //this.setAction( arr );
            if( callback )callback( { data : arr } )
        }else{
            this.explorer.explorer.listWork( this.explorer.data.id, this.loaded, function( arr ){
                this.loaded = true;
                arr.sort( function( a , b){
                    return a.orderNumber - b.orderNumber
                });
                //this.setAction( arr );
                if( callback )callback( { data : arr } )
            }.bind(this));
        }
    },
    //setAction : function( arr ){
    //    var addFlag = true;
    //    for( var i=0; i<arr.length; i++ ){
    //        if( arr[i].targetPerson == this.userName ){
    //            addFlag = false;
    //        }
    //    }
    //    if(this.explorer.workAddAction)this.explorer.workAddAction.setStyle( "display", addFlag ? "" : "none" );
    //},
    _removeDocument: function (documentData, all) {
        this.app.common.deleteWork( documentData, e, function(){
            this.reload();
        }.bind(this))
    },
    _openDocument: function (documentData) {
        this.app.common.openWork( documentData, this.report.data, this.explorer.data, this, this.report.isEdited );
    },
    _postCreateViewBody : function( bodyNode ){
        if( this.getStatus() == "write" && this.options.isEdited ){
            this.contentInput = new Element("textarea", {
                placeholder : "请填写您的工作总结",
                styles : this.css.textarea
            }).inject( this.container );
            if( this.currentPersonData ){
                this.contentInput.set("text", this.currentPersonData.progressContent );
                this.contentInput.set("value", this.currentPersonData.progressContent );
            }
            this.contentInput.addEvent("blur", function(ev){
                this.saveWork( ev, "工作总结保存并上传成功" );
            }.bind(this))
        }
    },
    saveWork: function(ev, noticeText){
        var data = this.currentPersonData;
        var content = this.contentInput.get("value").trim();
        if( content ){
            if( !data ){ //如果没有保存过
                data = {};
                var reportData = this.report.data;
                var keyworkData = this.explorer.data;
                data.reportId = reportData.id;
                data.workInfoId = keyworkData.id;
                data.keyWorkId = keyworkData.keyWorkId;
                data.title = keyworkData.workTitle;
                data.workTitle = keyworkData.workTitle;
                data.flag = reportData.flag;
                data.year = reportData.year;
                data.month = reportData.month;
                data.week = reportData.week;
                data.date = reportData.date;
                data.targetPerson = ( layout.desktop.session.user || layout.user ).distinguishedName;
                data.orderNumber = this.options.orderNumber || 1;
            }
            data.workDescribe = content;
            data.progressContent = content;
            this.app.restActions.saveWork( data, function(json){
                var id = json.data.id;
                this.app.restActions.getWork( id, function(js){
                    this.currentPersonData = js.data;
                }.bind(this));
                this.app.notice( noticeText, "success", ev.target.getParent() )
            }.bind(this));
        }else if( !content && data ){ //清空内容了，需要删除
            this.app.restActions.deleteWork( data.id, function(){
                this.currentPersonData = null;
                this.app.notice( noticeText, "success", ev.target.getParent() )
            }.bind(this))
        }
    },
    getStatus : function(){
        if( !this.status ){
            this.status = this.explorer.options.status;
        }
        return this.status
    }

});

MWF.xApplication.Report.StrategyExplorer.WorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    open: function(  ){
        //this.view._openDocument( this.data )
    },
    edit: function(node, ev){
        //this.app.common.editWork( this.data, this.view.report.data, this.explorer.data, this.view );
        //ev.stopPropagation();
    },
    delete : function(node, ev){
        this.app.common.deleteWork( this.data, ev, function(){
            this.view.reload();
        }.bind(this));
        ev.stopPropagation();
    },
    _postCreateDocumentNode : function(node, data){
        if( this.view.getStatus() == "write" && this.view.options.isEdited ){
            if( data.targetPerson == this.view.userName ){
                node.setStyle("display","none");
                this.view.currentPersonData = data;
            }
        }
    }
});

MWF.xApplication.Report.StrategyExplorer.PlanView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.Report.StrategyExplorer.PlanDocument( this.viewBodyNode || this.viewNode, data, this.explorer, this, null, index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        //this.actions.listPlan( this.options.reportId, function( json ){
        //    if( callback )callback( json );
        //})
        this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;
        if( !this.loaded ){
            this.loaded = true;
            if( callback )callback( { data : this.explorer.data.planList } )
        }else{
            this.explorer.explorer.listPlan( this.explorer.data.id, this.loaded, function( arr ){
                this.loaded = true;
                arr.sort( function( a , b){
                    return a.orderNumber - b.orderNumber
                });
                if( callback )callback( { data : arr } )
            }.bind(this));
        }
        //if( callback )callback( { data : this.data || [] } )
    },
    _openDocument: function (documentData) {
        this.app.common.openPlan( documentData, this.report.data, this.explorer.data, this, false, this.report.isEdited );
    }

});

MWF.xApplication.Report.StrategyExplorer.PlanDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    open: function(  ){
        this.view._openDocument( this.data )
    },
    edit: function( node , ev ){
        this.app.common.editPlan( this.data, this.view.report.data, this.explorer.data, this.view, false );
        ev.stopPropagation();
    },
    delete : function(node, ev){
        this.app.common.deletePlan( this.data, ev, function (){
            this.view.reload();
        }.bind(this));
        ev.stopPropagation();
    }
});

MWF.xApplication.Report.StrategyExplorer.PlanViewNext = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.Report.StrategyExplorer.PlanDocumentNext(this.viewBodyNode || this.viewNode, data, this.explorer, this, null, index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;
        var addFlag = true;
        if( !this.loaded ){
            this.loaded = true;
            var arr = this.explorer.data.planNextList || [];
            //this.setAction( arr );
            if( callback )callback( { data : arr } )
        }else{
            this.explorer.explorer.listPlanNext( this.explorer.data.id, this.loaded, function( arr ){
                this.loaded = true;
                arr.sort( function( a , b){
                    return a.orderNumber - b.orderNumber
                });
                //this.setAction( arr );
                if( callback )callback( { data : arr } )
            }.bind(this));
        }
    },
    _openDocument: function (documentData) {
        this.app.common.openPlan( documentData, this.report.data, this.explorer.data,this, true, this.report.isEdited );
    },
    _postCreateViewBody : function( bodyNode ){
        if( this.getStatus() == "write" && this.options.isEdited ){
            this.explorer.contentInput = new Element("textarea", {
                placeholder : "请填写计划",
                styles : this.css.textarea
            }).inject( this.container );
            if( this.explorer.currentPersonData ){
                this.explorer.contentInput.set("text", this.explorer.currentPersonData.planContent);
                this.explorer.contentInput.set("value", this.explorer.currentPersonData.planContent)
            }
            this.explorer.contentInput.addEvent("blur", function(ev){
                this.explorer.savePersonPlan( ev, "计划保存并上传成功" );
            }.bind(this))
        }
    },
    getStatus : function(){
        if( !this.status ){
            this.status = this.explorer.options.status;
        }
        return this.status
    }
});

MWF.xApplication.Report.StrategyExplorer.PlanDocumentNext = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    open: function(  ){
        this.view._openDocument( this.data )
    },
    edit: function( node , ev ){
        this.app.common.editPlan( this.data, this.view.report.data, this.explorer.data, this.view, true );
        ev.stopPropagation();
    },
    delete : function(node, ev){
        this.app.common.deletePlanNext( this.data, ev, function () {
            this.view.reload();
        }.bind(this));
        ev.stopPropagation();
    },
    _postCreateDocumentNode : function(node, data){
        if( this.view.getStatus() == "write" && this.view.options.isEdited ){
            if( data.targetPerson == this.view.userName ){
                node.setStyle("display","none");
                this.explorer.currentPersonData = data;
            }
        }
    }
});

MWF.xApplication.Report.StrategyExplorer.PlanTitleViewNext = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.Report.StrategyExplorer.PlanTitleDocumentNext(this.viewBodyNode || this.viewNode, data, this.explorer, this, null, index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;
        var addFlag = true;
        if( !this.loaded ){
            this.loaded = true;
            var arr = this.explorer.data.planNextList || [];
            //this.setAction( arr );
            if( callback )callback( { data : arr } )
        }else{
            this.explorer.explorer.listPlanNext( this.explorer.data.id, this.loaded, function( arr ){
                this.loaded = true;
                arr.sort( function( a , b){
                    return a.orderNumber - b.orderNumber
                });
                //this.setAction( arr );
                if( callback )callback( { data : arr } )
            }.bind(this));
        }
    },
    _postCreateViewBody : function( bodyNode ){
        if( this.getStatus() == "write" && this.options.isEdited ) {
            this.explorer.titleInput = new Element("textarea", {
                placeholder: "请填写标题",
                styles: this.css.textarea
            }).inject(this.container);
            this.explorer.explorer.nextMonth_inputTitle.push( this.explorer.titleInput );
            if (this.explorer.currentPersonData) {
                this.explorer.titleInput.set("text",this.explorer.currentPersonData.title);
                this.explorer.titleInput.set("value",this.explorer.currentPersonData.title);
            }
            this.explorer.titleInput.addEvent("blur", function ( ev ) {
                this.explorer.savePersonPlan( ev, "标题保存并上传成功" );
            }.bind(this))
        }
    },
    getStatus : function(){
        if( !this.status ){
            this.status = this.explorer.options.status;
        }
        return this.status
    }
});

MWF.xApplication.Report.StrategyExplorer.PlanTitleDocumentNext = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    open: function(  ){
        this.view._openDocument( this.data )
    },
    edit: function( node , ev ){
        this.app.common.editPlan( this.data, this.view.report.data, this.explorer.data, this.view, true );
        ev.stopPropagation();
    },
    delete : function(node, ev){
        this.app.common.deletePlanNext( this.data, ev, function () {
            this.view.reload();
        }.bind(this));
        ev.stopPropagation();
    },
    _postCreateDocumentNode : function(node, data){
        if( this.view.getStatus() == "write" && this.view.options.isEdited ){
            if(data.targetPerson == this.view.userName ){
                node.setStyle("display","none");
                this.explorer.currentPersonData = data;
            }
        }
    }
});

