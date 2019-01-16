MWF.xApplication.Report.StrategyExplorer.Deployment = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isEdited" : true,
        "isKeyworkEdited" : true
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
        this.month = parseInt(this.data.month);

        this.node = new Element("div", {
            styles : this.css.deplymentNode
        }).inject( this.container );

        this.loadPerson();


        this.keyWorkContainer = new Element("div").inject( this.node );
        this.keyworkList = [];

        this.data.thisMonth_workList.each( function( data, i ){
            this.loadKeyWork( data, i+1 );
        }.bind(this))
    },
    loadPerson: function(){
        this.personNode = new Element("div.personDeployNode",{
            styles : this.css.personDeployNode
        }).inject( this.node );
        var html = "<table width='96%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' >" +
            "<tr>" +
            "   <td style='width: 15%;font-size: 14px;' styles='formTableTitleP10'>汇报填写人员</td>" +
            "    <td style='width: 85%' item='workreportPersonList' styles='formTableValueP10'></td>"+
            "</tr>" +
            "</table>";
        this.personNode.set("html", html);
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.peronform = new MForm(this.personNode, this.data, {
                verifyType : "single",
                isEdited: this.options.isEdited,
                style : "report",
                itemTemplate: {
                    workreportPersonList: { text : this.lp.targetPerson, type : "org", orgType : "identity", isEdited : this.options.isEdited,
                        count : 0, notEmpty : true, units : [this.data.targetUnit.split("@")[0]], event :{
                            change : function( item ){
                                this.save( item.getElements()[0] );
                            }.bind(this)
                        } }
                }
            }, this.app );
            this.peronform.load();
        }.bind(this), true);
    },
    loadKeyWork : function( data, index ){
        var keywork = new MWF.xApplication.Report.StrategyExplorer.Deployment.KeyWorkItem( this.keyWorkContainer, this, data, {
            reportId : this.data.id,
            isEdited : this.options.isKeyworkEdited && this.options.isEdited,
            orderNumber : this.data.orderNumber || index
        } );
        keywork.load();
        this.keyworkList.push( keywork );
    },
    getPersonString : function(){

    },
    arrayIsContains : function( array, identity ){
        for( var i=0; i<array.length; i++ ){
            if( array[i].woPerson.distinguishedName == identity.woPerson.distinguishedName  ){
                return true;
            }
        }
        return false;
    },
    getPerson : function( ){
        var indentityList = this.peronform.getItem("workreportPersonList").dom.getData(true);
        var indentity = Object.clone( ( layout.desktop.session.user || layout.user ).identityList[0] );
        var user = ( layout.desktop.session.user || layout.user );
        indentity.woPerson = user;
        var identity1 = MWF.org.parseOrgData( indentity );
        if( !this.arrayIsContains( indentityList, identity1 ) ){
            indentityList.push( identity1 );
        }
        return indentityList;
    },
    submit: function(){
        if( this.errorNodeList ){
            this.errorNodeList.each( function(node){
                node.destroy();
            })
        }
        var flag = false;
        var result = this.getResult( true );
        if( result ){
            var data = this.getPerson();
            var person = [];
            var identity = [];
            data.each( function( d ){
                var dn = d.woPerson ? d.woPerson.distinguishedName : d.distinguishedName;
                if( !person.contains( dn  ) ){
                    person.push( dn );
                }
                if( !identity.contains(d.distinguishedName) ){
                    identity.push( d.distinguishedName )
                }
            });
            result.workreportPersonList = identity;

            var readerList = [];
            person.each( function( p ){
                readerList.push({
                    permission : "阅读",
                    permissionObjectType : "人员",
                    permissionObjectName : p
                })
            });
            result.readerList = readerList;

            var authorList = [];
            person.each( function( p ){
                authorList.push({
                    permission : "作者",
                    permissionObjectType : "人员",
                    permissionObjectName : p
                })
            });
            result.authorList = authorList;
            this.actions.submitWorkPerson( result, function(){
                //this.app.notice("保存成功");
                flag = true;
            }.bind(this), null, false )
        }
        return flag;
    },
    save: function( node ){
        var flag = false;
        var result = this.getResult( false );
        if( result ){
            this.actions.saveWorkPerson( result, function(){
                this.app.notice("保存并上传成功", "success" );
                flag = true;
            }.bind(this), null, false )
        }
        return flag;
    },
    getResult : function( verify ){
        var flag = true;
        var p = this.peronform.getResult( verify, null, true, false, true );
        if( verify && !p )flag = false;

        if( this.options.isKeyworkEdited ){
            var workList = [];

            this.keyworkList.each( function( keywork ){
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
                    orderNumber : keywork.options.orderNumber,
                    workTitle : title,
                    measuresList : measuresList
                }) ;

                if( verify ){
                    var planDataList = keywork.getPlanData();
                    if( !planDataList || planDataList.length == 0 ){
                        this.createErrorNode( keywork.planListNode, "请填写计划" );
                        flag = false;
                    }
                }
            }.bind(this));

            if( !flag )return false;
            return {
                id : this.data.id,
                workreportPersonList : p.workreportPersonList,
                workList : workList
            }
        }else{
            if( !flag )return false;
            return {
                id : this.data.id,
                workreportPersonList : p.workreportPersonList
            }
        }

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

MWF.xApplication.Report.StrategyExplorer.Deployment.KeyWorkItem = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "reportId" : "",
        "isEdited" : true,
        "orderNumber" : 1
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
        //this.node = new Element("div.keyWorkNode", { styles : this.css.keyWorkNode }).inject( this.container );

        var table = new Element( "table", {
            "width":"96%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : this.css.formTable
        }).inject( this.container  );

        var tr = new Element("tr").inject( table );

        new Element("td", {
            "rowspan" : 2,
            "text" : this.data.orderNumber || this.options.orderNumber,
            "styles": this.css.formTableTitle
        }).inject( tr );
        new Element("td", {
            "text" : "部门重点工作",
            "width" : "140",
            "styles": this.css.formTableTitle
        }).inject( tr );
        var contentTd = new Element("td", {
            "styles": this.css.formTableValue
        }).inject( tr );
        if( this.options.isEdited ){
            this.workTitleInput = new Element("input", {
                "value" : this.data.workTitle,
                "styles" : this.css.keyWorkTitleInput
            }).inject( contentTd )
        }else{
            new Element("div", {
                "html" : this.app.common.replaceWithBr( this.data.workTitle ),
                styles : { "width" : "870px", "float" : "left" }
            }).inject( contentTd )
        }

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
        new Element("td", {
            "text" : "工作计划",
            "styles": this.css.formTableTitle
        }).inject( tr );
        contentTd = new Element("td", {
            "styles": this.css.formTableValue,
            "html" : this.app.common.replaceWithBr( this.data.workPlanSummary )
        }).inject( tr );

        //this.loadWorkTitle();

        //this.loadMeasure();
    },
    //loadWorkTitle : function(){
    //    var topNode = new Element("div.keyWorkTopNode", {  styles : this.css.keyWorkTopNode  }).inject( this.node );
    //
    //    var orderNumber = this.data.orderNumber || this.options.orderNumber;
    //    var tetNode = new Element("div.keyWorkTopTextNode", {
    //        styles : this.css.keyWorkTopTextNode,
    //        text : "【"+ this.explorer.month + "月" +"】"+"部门重点工作"+ this.lp[ orderNumber ] +"："
    //    }).inject( topNode );
    //    if( this.options.isEdited ){
    //        this.workTitleInput = new Element("input", {
    //            "value" : this.data.workTitle,
    //            "styles" : this.css.keyWorkTitleInput
    //        }).inject( topNode )
    //    }else{
    //        new Element("div", {
    //            "text" : this.data.workTitle
    //        }).inject( topNode )
    //    }
    //
    //    var showMeasureNode = new Element("div",{
    //        "styles" : {
    //            "float" : "right"
    //        },
    //        "text" : "查看举措"
    //    }).inject( topNode );
    //    var tooltip = new MWF.xApplication.Report.ShowMeasureTooltip( this.app.content, showMeasureNode, this.app, this.explorer.data, {
    //        style : "report",
    //        position : { x : "auto", y : "auto" },
    //        event : "click"
    //    });
    //    tooltip.measuresList = this.data.measuresList;
    //},
    getWorkTitle : function(){
        return this.workTitleInput.get("value");
    },
    getMeasuresList : function(){
      return this.measuresList || this.data.measuresList
    },
    //loadMeasure: function(){
    //    var _self = this;
    //    this.measureListNode = new Element("div.measureListNode", {
    //        styles : this.css.listContainer
    //    }).inject( this.node );
    //
    //    var listTopNode = new Element("div.measureTopNode", {
    //        styles : this.css.listTop,
    //        text : "【"+ this.explorer.month + "月" +"】"+"举措："
    //    }).inject( this.measureListNode );
    //    if( this.options.isEdited ){
    //        new Element("button", {
    //            text : "选择举措",
    //            styles : this.css.listTopAction,
    //            events : {
    //                "mouseover" : function( ev ){
    //                    ev.target.setStyles( _self.css.listTopAction_over )
    //                },
    //                "mouseout" : function(ev){
    //                    ev.target.setStyles( _self.css.listTopAction )
    //                },
    //                "click" : function(){
    //                    var form = new MWF.xApplication.Report.SelectMeasureForm(this, this.explorer.data, {
    //                        onPostOk: function( list, value ){
    //                            this.measuresList = value;
    //                            this.measureContentNode.empty();
    //                            list.each( function(d,i){
    //                                this.createMeasureNode(d,i);
    //                            }.bind(this))
    //                        }.bind(this)
    //                    }, { app : this.app });
    //                    form.edit();
    //                }.bind(this)
    //            }
    //        }).inject( listTopNode )
    //    }
    //
    //    this.measureContentNode = new Element("div", {
    //        styles: this.css.listNode
    //    }).inject(this.measureListNode);
    //
    //    this.selectableMeasureObject = {};
    //    this.explorer.data.selectableMeasures.each(function (m) {
    //        this.selectableMeasureObject[m.id] = m;
    //    }.bind(this));
    //    this.data.measuresList.each(function (id, i) {
    //        if (id) {
    //            var data = this.selectableMeasureObject[id];
    //            this.createMeasureNode( data, i );
    //        }
    //    }.bind(this));
    //
    //
    //},
    //createMeasureNode : function( data ,i  ){
    //    var measureNoe = new Element("div", {
    //        styles: this.css.itemMeasureNode
    //    }).inject(this.measureContentNode);
    //
    //    var iconNode = new Element("div.itemMeasureIconNode", {styles: this.css.itemMeasureIconNode}).inject(measureNoe);
    //    var tetNode = new Element("div.itemMeasureTextNode", {
    //        styles: this.css.itemMeasureTextNode,
    //        text: "举措" + (i + 1) + "：" + data.measuresinfotitle
    //    }).inject(measureNoe);
    //    this.loadMeasureTooltip(iconNode, data.id);
    //},
    getPlanData : function(){
        return this.planDataList
    },
    loadMeasureTooltip: function( node, measureId ){
        new MWF.xApplication.Report.MeasureTooltip( this.app.content, node, this.app, null, {
            position : { x : "right", y : "auto" },
            measureId : measureId,
            displayDelay : 300
        })
    }
});
