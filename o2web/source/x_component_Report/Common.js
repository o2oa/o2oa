MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("Report", "Attachment", null, false);


MWF.xApplication.Report = MWF.xApplication.Report || {};

MWF.xApplication.Report.Common = new Class({
    initialize: function ( app ) {
        this.app = app;
    },
    //getIdentityString : function( username, callback ){
    //    var ids = [];
    //    this.getIdentity( username, function( identityList ){
    //        for( var i=0; i<identityList.length; i++ ){
    //            ids.push( identityList[i].distinguishedName )
    //        }
    //        if(callback)callback( ids );
    //    }.bind(this))
    //},
    getIdentity : function( username, callback ){
        if(!username) username = ( layout.desktop.session.user || layout.user ).distinguishedName;
        if(!this.identityList)this.identityList={};
        if( this.identityList[username] ){
            if(callback)callback(this.identityList[username]);
        }else{
            MWF.Actions.get("x_organization_assemble_express").listIdentityWithPersonValue( {"personList":[ username ]} , function( json ){
                this.identityList[username] = json.data.identityList;
                if(callback)callback(this.identityList[username]);
            }.bind(this))
        }
    },
    openReport: function( reportData, view ){
        if( reportData.reportStatus == "已完成" || reportData.reportStatus == "结束" || reportData.reportStatus == "董事长审阅" ){
            MWF.Actions.get("x_processplatform_assemble_surface").listWorkByJob( reportData.wf_JobId, function( json ){
                var workCompletedList = json.data.workCompletedList;
                if( workCompletedList.length > 0 ){
                    var options = {
                        "workCompletedId": workCompletedList[0].id,
                        "appId": workCompletedList[0].id,
                        "onQueryClose" : function(){
                        }.bind(this)
                    };
                    this.app.desktop.openApplication(null, "process.Work", options);
                }
            }.bind(this))
        }else{
            var options = {
                "workId": reportData.wf_WorkId,
                "appId": reportData.wf_WorkId,
                "onQueryClose" : function(){
                    this.obj.app.restActions.getReport( reportData.id, function( json ){
                        if( this.reportData.activityName != json.data.activityName ){
                            try{ view.reload(); }catch(e){}
                        }
                    }.bind(this))
                }.bind({ obj : this, reportData : reportData })
            };
            this.app.desktop.openApplication(null, "process.Work", options);
        }
    },
    addWork : function( reportData, keyworkData, view, orderNumber ){
        var form = new MWF.xApplication.Report.WorkForm( this.app, { "reportId" : reportData.id, "targetPerson" : reportData.targetPerson }, {
            orderNumber : orderNumber
        }, {
            app : this.app
        } );
        form.view = view;
        form.reportData = reportData;
        form.keyworkData = keyworkData;
        form.create();
    },
    editWork : function( data, reportData,view ){
        var form = new MWF.xApplication.Report.WorkForm( this.app, data, {}, {
            app : this.app
        } );
        form.reportData = reportData;
        form.view = view;
        form.edit();
    },
    openWork : function( data, reportData,keyworkData, view, editedAble ){
        var form = new MWF.xApplication.Report.WorkForm( this.app, data, {
            editedAble : editedAble
        }, {
            app : this.app
        } );
        form.reportData = reportData;
        form.keyworkData = keyworkData;
        form.view = view;
        form.open();
    },
    deleteWork: function( data, e, callback ){
        var _self = this;
        var text = this.app.lp.delete_work.replace(/{name}/g, data.progressContent );
        this.app.confirm("infor", e, this.app.lp.delete_work_title, text, 380, 150, function(){
            _self._deleteWork( data, callback, e );
            this.close();
        }, function(){
            this.close();
        });
    },
    _deleteWork: function(data, callback, ev){
        this.app.restActions.deleteWork( data.id, function(){
            if( callback )callback();
            this.app.notice( this.app.lp.deleteDocumentOK, "success", ev.target )
        }.bind(this))
    },


    addCustomWork : function( reportData, keyworkData, view, orderNumber ){
        var form = new MWF.xApplication.Report.CustomWorkForm( this.app, { "reportId" : reportData.id, "targetPerson" : reportData.targetPerson }, {
            orderNumber : orderNumber
        }, {
            app : this.app
        } );
        form.view = view;
        form.reportData = reportData;
        form.keyworkData = keyworkData;
        form.create();
    },
    editCustomWork : function( data, reportData,view ){
        var form = new MWF.xApplication.Report.CustomWorkForm( this.app, data, {}, {
            app : this.app
        } );
        form.reportData = reportData;
        form.view = view;
        form.edit();
    },
    openCustomWork : function( data, reportData,keyworkData, view, editedAble ){
        var form = new MWF.xApplication.Report.CustomWorkForm( this.app, data, {
            editedAble : editedAble
        }, {
            app : this.app
        } );
        form.reportData = reportData;
        form.keyworkData = keyworkData;
        form.view = view;
        form.open();
    },
    deleteCustomWork: function( data, e, callback ){
        var _self = this;
        var text = this.app.lp.delete_work.replace(/{name}/g, data.workTitle );
        this.app.confirm("infor", e, this.app.lp.delete_work_title, text, 380, 150, function(){
            _self._deleteCustomWork( data, callback , e);
            this.close();
        }, function(){
            this.close();
        });
    },
    _deleteCustomWork: function(data, callback, ev){
        this.app.restActions.deleteWork( data.id, function(){
            if( callback )callback();
            this.app.notice( this.app.lp.deleteDocumentOK, "success", ev.target )
        }.bind(this))
    },



    addExtWork : function( reportData, keyworkData, view, orderNumber, category ){
        var form = new MWF.xApplication.Report.ExtWorkForm( this.app, { "reportId" : reportData.id, "targetPerson" : reportData.targetPerson }, {
            category : category,
            orderNumber : orderNumber
        }, {
            app : this.app
        } );
        form.view = view;
        form.reportData = reportData;
        form.keyworkData = keyworkData;
        form.create();
    },
    editExtWork : function( data, reportData, parentData, view, category ){
        var form = new MWF.xApplication.Report.ExtWorkForm( this.app, data, {category : category}, {
            app : this.app
        } );
        form.reportData = reportData;
        form.keyworkData = parentData;
        form.view = view;
        form.edit();
    },
    openExtWork : function( data, reportData,keyworkData, view, editedAble, category ){
        var form = new MWF.xApplication.Report.ExtWorkForm( this.app, data, {
            editedAble : editedAble,
            category : category
        }, {
            app : this.app
        } );
        form.reportData = reportData;
        form.keyworkData = keyworkData;
        form.view = view;
        form.open();
    },
    deleteExtWork: function( data, e, callback, category ){
        var _self = this;
        var text = "确定要删除该工作？";
        this.app.confirm("infor", e, this.app.lp.delete_work_title, text, 380, 150, function(){
            _self._deleteExtWork( data, callback , e, category);
            this.close();
        }, function(){
            this.close();
        });
    },
    _deleteExtWork: function(data, callback, ev, category){
        this.app.restActions[ "delete"+category ]( data.id, function(){
            if( callback )callback();
            this.app.notice( this.app.lp.deleteDocumentOK, "success", ev.target )
        }.bind(this))
    },



    addPlan : function(reportData, keyworkData, view, isPlanNext, orderNumber){
        var form = new MWF.xApplication.Report.PlanForm( this.app,
            { "reportId" : reportData.id, "targetPerson" : reportData.targetPerson },
            { "isPlanNext" : isPlanNext , orderNumber : orderNumber },
            { app : this.app }
        );
        form.reportData = reportData;
        form.keyworkData = keyworkData;
        form.view = view;
        form.create();
    },
    editPlan : function( data , reportData, keyworkData, view, isPlanNext){
        var form = new MWF.xApplication.Report.PlanForm( this.app, data, {"isPlanNext" : isPlanNext}, {
            app : this.app
        } );
        form.reportData = reportData;
        form.keyworkData = keyworkData;
        form.view = view;
        form.edit();
    },
    openPlan : function( data , reportData, keyworkData, view, isPlanNext, editedAble){
        var form = new MWF.xApplication.Report.PlanForm( this.app, data, {
            "isPlanNext" : isPlanNext,
            "editedAble" : editedAble
        }, {
            app : this.app
        } );
        form.reportData = reportData;
        form.keyworkData = keyworkData;
        form.view = view;
        form.open();
    },
    deletePlan: function( data, e , callback){
        var _self = this;
        var text = this.app.lp.delete_plan.replace(/{name}/g, data.planContent );
        this.app.confirm("infor", e, this.app.lp.delete_plan_title, text, 380, 150, function(){
            _self._deletePlan( data, callback, e );
            this.close();
        }, function(){
            this.close();
        });
    },
    _deletePlan: function( data, callback, ev ){
        this.app.restActions.deletePlan( data.id, function(){
            if( callback )callback();
            this.app.notice( this.app.lp.deleteDocumentOK, "success", ev.target )
        }.bind(this))
    },
    deletePlanNext: function( data, e , callback){
        var _self = this;
        var text = this.app.lp.delete_plan.replace(/{name}/g, data.planContent );
        this.app.confirm("infor", e, this.app.lp.delete_plan_title, text, 380, 150, function(){
            _self._deletePlanNext( data, callback, e );
            this.close();
        }, function(){
            this.close();
        });
    },
    _deletePlanNext: function( data, callback , ev ){
        this.app.restActions.deletePlanNext( data.id, function(){
            if( callback )callback();
            this.app.notice( this.app.lp.deleteDocumentOK, "success", ev.target )
        }.bind(this))
    },
    getUnitWithExportPermission : function( callback ){
        this.getIdentity( null, function( identityList ){
            this.unitList = [];
            identityList.each( function( identity ){
                MWF.Actions.get("x_organization_assemble_express").listUnitWithDuty( {"name":"部门战略管理员","identity":identity}, function( json ){
                    for( var i=0; i<json.data.length; i++){
                        this.unitList.push( json.data[i].distinguishedName );
                    }
                }.bind(this), null, false );
                MWF.Actions.get("x_organization_assemble_express").listUnitWithDuty( {"name":"部主管","identity":identity}, function( json ){
                    for( var i=0; i<json.data.length; i++){
                        this.unitList.push( json.data[i].distinguishedName );
                    }
                }.bind(this), null, false );
            }.bind(this));
            if( callback )callback(this.unitList);
        }.bind(this))
    },
    hasExportAllUnitPermission: function(){
        if( typeOf( this.exportFlag ) == "boolean" ){
            return this.exportFlag;
        }
        this.exportFlag = false;
        if( this.isAdmin() ){
            this.exportFlag = true;
            return this.exportFlag;
        }
        var username = ( layout.desktop.session.user || layout.user ).distinguishedName;
        MWF.Actions.get("x_organization_assemble_express").listPersonWithGroup( {"groupList":["ReportExporter"]}, function( json ){
            for( var i=0; i<json.data.length; i++){
                if( json.data[i].distinguishedName == username ){
                    this.exportFlag = true;
                    break;
                }
            }
        }.bind(this), null, false );
        return this.exportFlag;
    },
    isAdmin : function(){
        //return MWF.AC.isAdministrator();
        if( typeOf( this.adminFlag ) == "boolean" ){
            return this.adminFlag;
        }else{
            this.app.restActions.isAdmin( function( json ){
                this.adminFlag = json.data.value
            }.bind(this), null, false);
            return this.adminFlag;
        }
    },
    addZero : function( str, length ){
        var zero = "";
        str = str.toString();
        for( var i=0; i<length; i++ ){
            zero = zero + "0";
        }
        var s = zero + str;
        return s.substr(s.length - length, length );
    },
    listSetting : function( callback ){
        if( this.setting ){
            if( callback )callback( this.setting );
        }else{
            this.setting = {};
            this.app.restActions.listSetting( function( json ){
                json.data.each( function(d){
                    var value = d.configValue;
                    if( typeOf(value) == "string"){
                        if( value == "NONE" )value = ""
                    }else if( typeOf(value) == "array" ){
                        for( var i=0; i<value.length-1; i++ ){
                            if( value[i] == "NONE" )value[i] = ""
                        }
                    }
                    this.setting[d.configCode] = value;
                }.bind(this));
                if( callback )callback( this.setting );
            }.bind(this))

        }
    },
    replaceWithBr : function( str ){
        if( typeOf(str) != "string" )return "";
        var reg=new RegExp("\n","g");
        return str.replace(reg,"<br/>");
    },
    splitWithLength : function( str, length ){
        var result = [];
        var arr = str.split("\n");
        arr.each( function( s ){
            do {
                result.push( s.substr( 0, Math.min( s.length, length ) ) );
                s = ( s.length > length ) ? s.substr( length , s.length ) : "";
            }while ( s );
        });
        return result.join("\n");
    }
});

MWF.xApplication.Report.ReportFileter = new Class({
    Implements: [Options, Events],
    options : {
        items : ["reportType","title","year","month","reportDate","targetList","activityList","currentPersonList","reportStatus"],
        defaultResult : {}
    },
    initialize: function ( container, app, options ) {
        this.setOptions( options );
        this.container = container;
        this.app = app;
        this.lp = app.lp;
        this.css = app.css;
        this.load();
    },
    load: function(){
        this.searchBarAreaNode = new Element("div", {
            "styles": this.css.searchBarAreaNode
        }).inject(this.container);

        this.searchBarNode = new Element("div", {
            "styles": this.css.searchBarNode
        }).inject(this.searchBarAreaNode);

        this.searchBarInputBoxNode = new Element("div", {
            "styles": this.css.searchBarInputBoxNode
        }).inject(this.searchBarNode);
        this.searchBarInputNode = new Element("input", {
            "type": "text",
            "value": this.lp.searchKey,
            "styles": this.css.searchBarInputNode
        }).inject(this.searchBarInputBoxNode);

        this.searchBarResetActionNode = new Element("div", {
            "styles": this.css.searchBarResetActionNode
        }).inject(this.searchBarInputBoxNode);
        this.searchBarResetActionNode.setStyle("display","none");

        this.searchBarActionNode = new Element("div", {
            "styles": this.css.searchBarActionNode
        }).inject(this.searchBarNode);

        this.searchBarMoreActionNode = new Element("div", {
            "styles": this.css.searchMoreActionNode,
            "title" : "高级搜索"
        }).inject(this.searchBarNode);


        var _self = this;
        this.searchBarActionNode.addEvent("click", function(){
            this.search();
        }.bind(this));
        this.searchBarResetActionNode.addEvent("click", function(){
            this.reset();
        }.bind(this));

        this.searchBarInputNode.addEvents({
            "focus": function(){
                if (this.value==_self.lp.searchKey) this.set("value", "");
            },
            "blur": function(){if (!this.value) this.set("value", _self.lp.searchKey);},
            "keydown": function(e){
                if (e.code==13){
                    this.search();
                    e.preventDefault();
                }
            }.bind(this)
        });
        this.loadMore();
    },
    destroy : function(){
        this.tootip.destroy();
        this.container.empty();
    },
    reload: function( opt ){
        this.removeEvent( "search" );
        this.setOptions( opt );
        this.tootip.reload( opt );
    },
    getResult : function(){
        var value = this.searchBarInputNode.get("value");
        var result = { title: value == this.lp.searchKey ? "" : value };
        if( !result.title )result = {};
        if( this.options.defaultResult ){
            result = Object.merge(result, this.options.defaultResult);
        }
        return result;
    },
    search : function( result ){
        this.searchBarResetActionNode.setStyle("display","");
        this.fireEvent("search" , result || this.getResult())
    },
    reset : function( result ){
        this.searchBarResetActionNode.setStyle("display","none");
        this.searchBarInputNode.set("value",this.lp.searchKey );
        this.fireEvent("search", result || this.options.defaultResult );
    },
    loadMore : function(){
        this.tootip = new MWF.xApplication.Report.FileterTooltip(this.app.content, this.searchBarMoreActionNode, this.app, {}, this.options);
        this.tootip.parent = this;
    }
});

MWF.xApplication.Report.FileterTooltip = new Class({
    Extends: MTooltips,
    options : {
        event : "click", //事件类型， mouseenter对应mouseleave，click 对应 container 的  click
        position : "right",
        nodeStyles : {
            "font-size" : "12px",
            "position" : "absolute",
            "max-width" : "500px",
            "min-width" : "360px",
            "z-index" : "11",
            "background-color" : "#fff",
            "padding" : "20px",
            "border-radius" : "8px",
            "box-shadow": "0 0 18px 0 #999",
            "-webkit-user-select": "text",
            "-moz-user-select": "text"
        },

        items : ["reportType","title","year","month","reportDate","targetList","activityList","currentPersonList","reportStatus","reportObjType"],
        defaultResult : {}
    },
    destroy: function(){
        if(this.node)this.node.destroy();
        if(this.markNode)this.markNode.destroy();
        this.node = null;
        this.markNode = null;
    },
    reload: function( options ){
        if(this.node)this.node.destroy();
        if(this.markNode)this.markNode.destroy();
        this.node = null;
        this.markNode = null;
        this.setOptions(options);
    },
    _customNode : function( node ){
    },
    _loadCustom: function (callback) {
        this.getReportType( function(){
            this.loadHTML();
            this.loadForm();
            if (callback)callback();
        }.bind(this))
    },
    _getHtml: function () {

    },
    getReportType : function( callback ){
        this.app.common.listSetting( function( s ){
            this.setting = s;
            this.reportType = { value : [""], text : ["全部"] };
            if( s.MONTHREPORT_ENABLE == "true" ){
                this.reportType.value.push("MONTH");
                this.reportType.text.push("月报");
            }
            if( s.WEEKREPORT_ENABLE == "true" ){
                this.reportType.value.push("WEEK");
                this.reportType.text.push("周报");
            }
            if( s.DAYREPORT_ENABLE == "true" ){
                this.reportType.value.push("DAILY");
                this.reportType.text.push("日报");
            }
            if(callback)callback();
        }.bind(this))
    },
    getItemTemplate: function(){
        var template = {
            title : { text: "标题", type : "text" },
            year : { text: "年度", type : "select", defaultValue : new Date().getFullYear(),
                selectValue : ["",2016,2017,2018,2019,2020,2021,2022,2023,2024,2025,2026,2027,2028,2029,2030,2031,2032,2033,2034,2035,2036,2037]
            },
            month : { text: "月份", type : "select", selectValue : ["","01","02","03","04","05","06","07","08","09","10","11","12"] },
            //reportDate : {text: "日期", tType: "date"},
            targetList : { text:"汇报者", type: "org", orgType : "person" },
            activityList : {"text":"审核环节"},
            currentPersonList : { text:"当前处理人", type: "org", orgType : "person" },
            reportObjType : {"text":"汇报类型", type : "select", selectValue : ["","PERSON","UNIT"], selectText : ["","个人汇报","组织汇报"]},
            reportStatus: {"text":"汇报状态", type : "select", selectValue : ["","汇报者填写","审核中","已完成"]}
        };
        if( this.reportType.value.length > 2 ){
            template.reportType = { text : "类别", type : "select", selectValue : this.reportType.value, selectText : this.reportType.text }
        }
        for( var key in template ) {
            if (!this.options.items.contains(key)) {
                delete template[key];
            }
        }
        template.filterAction = {"text":"搜索", "value":"搜索", type : "button", className : "inputSeachButton", event : {
            click : function(){
                this.parent.search( this.getResult() );
            }.bind(this)
        }};
        template.resetAction = {"text":"重置", "value":"重置", type : "button", className : "inputResetButton", event : {
            click : function(){
                this.parent.reset( this.reset() );
            }.bind(this)
        }};
        return template;
    },
    loadHTML : function(){
        this.formNode = new Element("div").inject(this.node);
        var htmlList = {
            title : "<tr><td styles='formTableTitle14' lable='title' width='30%'></td><td item='title' styles='formTableValue14' width='70%'></td></tr>",
            year : "<tr><td styles='formTableTitle14' lable='year'></td><td item='year' styles='formTableValue14'></td></tr>",
            month : "<tr><td styles='formTableTitle14' lable='month'></td><td item='month' styles='formTableValue14'></td></tr>",
            targetList : "<tr><td styles='formTableTitle14' lable='targetList'></td><td item='targetList' styles='formTableValue14'></td></tr>",
            currentPersonList : "<tr><td styles='formTableTitle14' lable='currentPersonList'></td><td item='currentPersonList' styles='formTableValue14'></td></tr>",
            activityList : "<tr><td styles='formTableTitle14' lable='activityList'></td><td item='activityList' styles='formTableValue14'></td></tr>" //,
            //reportStatus : "<tr><td styles='formTableTitle14' lable='reportStatus'></td><td item='reportStatus' styles='formTableValue14'></td></tr>",
            //reportObjType : "<tr><td styles='formTableTitle14' lable='reportObjType'></td><td item='reportObjType' styles='formTableValue14'></td></tr>"
        };
        if( this.reportType.value.length > 2 ){
            htmlList.reportType =  "<tr><td styles='formTableTitle14' lable='reportType'></td><td item='reportType' styles='formTableValue14'></td></tr>"
        }
        for( var key in htmlList ) {
            if (!this.options.items.contains(key)){
                delete htmlList[key];
            }
        }
        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>";
        for( var key in htmlList ){
            html += htmlList[key];
        }
        html += "<tr><td></td><td styles='formTableValue14'><span item='filterAction'></span><span item='resetAction'></span></td></tr>";
        html += "</table>";
        this.formNode.set("html",html);
    },
    loadForm : function(){
        this.form = new MForm(this.formNode, {}, {
            usesNewVersion : true,
            isEdited: true,
            style : "report",
            hasColon : true,
            itemTemplate: this.getItemTemplate()
        }, this.app);
        this.form.load();
    },
    reset: function(){
        this.form.reset();
        return this.options.defaultResult;
    },
    getResult : function(){
        var result = this.form.getResult(false, null, false, false, false);
        for( var key in result ){
            var v = result[key];
            if( typeOf( v ) == "array" && v.length == 0 ){
                delete result[key];
            }else if( !v ){
                delete result[key];
            }
        }
        delete result.filterAction;
        if( this.options.defaultResult ){
            result = Object.merge(result, this.options.defaultResult);
        }
        if( result.activityList )result.activityList = [result.activityList];
        //if( result.targetList ){
        //    var targetCNList = [];
        //    result.targetList.each( function( id ){
        //        targetCNList.push(id.split("@")[0])
        //    }.bind(this));
        //    result.targetList = targetCNList.unique();
        //}
        return result;
    }
});

MWF.xApplication.Report.PlanForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "report",
        "width": 800,
        "height": 400,
        "minWidth" : 700,
        "minHeight" : 320,
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "maxAction" : true,
        "hasBottom": true,
        "draggable": true,
        "resizeable": true,
        "editedAble" : true,
        "closeAction": true,
        "isPlanNext" : true
    },
    _createTableContent: function () {
        if( this.data.planContent && this.data.planContent=="暂无内容" )this.data.planContent="";
        this.formTopTextNode.set( "text", this.options.isPlanNext ? "下月计划" : "本月计划"  );

        var boxStyle = (this.isEdited || this.isNew) ? "border:1px solid #ccc; border-radius: 4px;overflow: hidden;padding:8px;" : "";

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='workTitle' width='20%'></td>" +
            "    <td styles='formTableValue14' colspan='3'>"+
            "       <div item='workTitle' style='"+ "" +"'></div>" +
            "</td></tr>" +
            //"<tr><td styles='formTableTitle' lable='workContent'></td>" +
            //"    <td styles='formTableValue14' item='workContent' colspan='3'></td></tr>" +
            //"<tr style='display: none;' item='measureTr'><td styles='formTableTitle' lable='measure'></td>" +
            //"    <td styles='formTableValue14' colspan='3' item='measure'></td></tr>" +
            //"<tr><td styles='formTableTitle' lable='title'></td>" +
            //"    <td styles='formTableValue' item='title' colspan='3'></td></tr>" +
            //"<tr><td styles='formTableTitle' lable='startTime' width='20%'></td>" +
            //"    <td styles='formTableValue14' item='startTime'></td>" +
            //"    <td styles='formTableTitleRight' lable='endTime'  width='20%'></td>" +
            //"    <td styles='formTableValue14' item='endTime'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='planContent'></td>" +
            "    <td styles='formTableValue14' item='planContent' colspan='3'></td></tr>" +
            //"<tr><td styles='formTableValue14' item='attachmentArea' colspan='4'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        this.loadForm()

    },
    loadForm : function(){
        if(this.data)this.data.workTitle = this.keyworkData.workTitle;
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                usesNewVersion : true,
                isEdited: this.isEdited || this.isNew,
                style : "report",
                hasColon : true,
                itemTemplate: {
                    //title: { text : this.lp.planName, notEmpty : true },
                    workTitle: { type : "innertext", text : this.lp.keyWork,   isEdited : false },
                    workContent: { text : this.lp.workContent, type : "innertext", defaultValue : this.keyworkData.workDescribe || "　" },　
                    //measure : { disable : !measureIdList.length , isEdited : false, type:"checkbox" ,text : this.lp.measureText, notEmpty : true, selectValue : measureIdList, selectText: measureTitleList, value : measureIdList },
                    //startTime: { text : this.lp.startTime, tType : "date", notEmpty : true },
                    //endTime: { text : this.lp.endTime, tType : "date", notEmpty : true },
                    planContent: { text : this.lp.planContent, type : "textarea", notEmpty : true, style : { height : "150px" } }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": this.lp.save
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        if( this.isEdited ){
            this.removeAction = new Element("button.inputCancelButton", {
                "styles": this.css.inputCancelButton,
                "text": this.lp.remove
            }).inject(this.formBottomNode);

            this.removeAction.addEvent("click", function (e) {
                this.remove(e);
            }.bind(this));
        }

        //var identityList = layout.desktop.session.user.identity
        if( !this.isEdited && !this.isNew && this.getEditPermission()){
            this.editAction = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": this.lp.edit
            }).inject(this.formBottomNode);
            this.editAction.addEvent("click", function (e) {
                this.editWork(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": this.lp.close
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    editWork : function(){
        this.formTopNode = null;
        if(this.setFormNodeSizeFun && this.app ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();
        this.edit();
    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            //data.workTitle = this.form.getItem("workId").getText();
            if( !data.flag ){
                data.reportId = this.reportData.id;
                data.workInfoId = this.keyworkData.id;
                data.keyWorkId = this.keyworkData.keyWorkId;
                data.flag = this.reportData.flag;
                data.year = this.reportData.year;
                data.month = this.reportData.month;
                data.week = this.reportData.week;
                data.date = this.reportData.date;
                data.targetPerson = ( layout.desktop.session.user || layout.user ).distinguishedName;
                data.orderNumber = this.options.orderNumber || 1;
            }
            data.workDescribe = data.workContent;
            data.title = data.workTitle;
            var act = this.options.isPlanNext ? "savePlanNext" : "savePlan";
            this.actions[act]( data, function(json){
                this.app.notice(this.lp.save_success, "success", this.formNode );
                var view = this.view;
                this.close();
                this.view.reload();
            }.bind(this));
        }
    },
    remove: function( ev ){
        var view = this.view;
        this.app.common[ this.options.isPlanNext ? "deletePlanNext" : "deletePlan" ]( this.data, ev, function(){
            view.reload();
            this.close();
        }.bind(this));
    },
    loadAttachment: function( area ){
        this.attachment = new MWF.xApplication.Report.Attachment( area, this.app, this.app.restActions, this.lp, {
            documentId : this.advanceId || this.data.id,
            isNew : this.isNew,
            isEdited : this.isEdited,
            "size" : "min",
            onQueryUploadAttachment : function(){
                this.attachment.isQueryUploadSuccess = true;
            }.bind(this),
            onDelete : function( data ){
            }.bind(this)
        });
        this.attachment.load();
    },
    getEditPermission : function(){
        if( !this.options.editedAble )return false;
        //if( !this.options.isPlanNext )return false;
        var username = ( layout.desktop.session.user || layout.user ).distinguishedName;
        var reportTarget = this.view && this.view.report && this.view.report.data && this.view.report.data.targetPerson;
        if(  username != this.data.targetPerson && !this.app.common.isAdmin() && username != reportTarget ){
            return false;
        }
        return true;
        //this.reportData.reportStatus
    }
});

MWF.xApplication.Report.WorkForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "report",
        "width": 800,
        "height": 450,
        "minWidth" : 700,
        "minHeight" : 320,
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "maxAction" : true,
        "hasBottom": true,
        "draggable": true,
        "resizeable": true,
        "editedAble" : true,
        "closeAction": true
    },
    _createTableContent: function () {
        if( this.data.progressContent && this.data.progressContent=="暂无内容" )this.data.progressContent="";
        this.formTopTextNode.set( "text", this.lp.work );

        var boxStyle = (this.isEdited || this.isNew) ? "border:1px solid #ccc; border-radius: 4px;overflow: hidden;padding:8px;" : "";

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='workTitle' width='20%'></td>" +
            "    <td styles='formTableValue14' colspan='3'>"+
            "       <div item='workTitle' style='"+ "" +"'></div>" +
            "</td></tr>" +
            //"<tr><td styles='formTableTitle' lable='workContent'></td>" +
            //"    <td styles='formTableValue14' item='workContent' colspan='3'></td></tr>" +
            //"<tr style='display: none;' item='measureTr'><td styles='formTableTitle' lable='measure'></td>" +
            //"    <td styles='formTableValue14' colspan='3' item='measure'></td></tr>" +
            //"<tr><td styles='formTableTitle' lable='title'></td>" +
            //"    <td styles='formTableValue' item='title' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='progressContent'></td>" +
            "    <td styles='formTableValue14' item='progressContent' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        //this.listMeasure( function( measureList ){
            this.loadForm();
        //}.bind(this))

    },
    //listMeasure: function( callback ){
    //    var measureList = [];
    //    this.selectableMeasureObject = {};
    //    this.keyworkData.selectableMeasures.each( function( m ){
    //        if( m ){
    //            this.selectableMeasureObject[ m.id ]=m;
    //        }
    //    }.bind(this));
    //    this.keyworkData.measuresList.each( function( id, i ){
    //        if( id && this.selectableMeasureObject[ id ] ){
    //            var data = this.selectableMeasureObject[ id ];
    //            measureList.push( data );
    //        }
    //    }.bind(this));
    //    if(callback)callback( measureList );
    //},
    loadForm : function( measureList ){
        //var measureIdList = [];
        //var measureTitleList = [];
        //if( measureList ){
        //    measureList.each( function( d ){
        //        measureIdList.push( d.id );
        //        measureTitleList.push( d.measuresinfotitle )
        //    });
        //}
        //if( measureIdList.length ){
        //    this.formTableArea.getElement("[item='measureTr']").setStyle("display","");
        //}
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                usesNewVersion : true,
                isEdited: this.isEdited || this.isNew,
                style : "report",
                hasColon : true,
                itemTemplate: {
                    //title: { text : this.lp.workName, notEmpty : true },
                    workTitle: { type : "innertext", text : this.lp.keyWork,  defaultValue : this.keyworkData.workTitle , isEdited : false },
                    workContent: { text : this.lp.workContent,  type : "innertext", defaultValue : this.keyworkData.workDescribe || "　"},
                    //measure : { disable : !measureIdList.length ,  isEdited : false, type:"checkbox" ,text : this.lp.measureText, notEmpty : true, selectValue : measureIdList, selectText: measureTitleList, value : measureIdList  },
                    progressContent: { text : this.lp.completion, type : "textarea", notEmpty : true, style : { height : "150px" } }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": this.lp.save
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        if( this.isEdited ){
            this.removeAction = new Element("button.inputCancelButton", {
                "styles": this.css.inputCancelButton,
                "text": this.lp.remove
            }).inject(this.formBottomNode);

            this.removeAction.addEvent("click", function (e) {
                this.remove(e);
            }.bind(this));
        }

        //var identityList = layout.desktop.session.user.identity
        if( !this.isEdited && !this.isNew && this.getEditPermission()){
            this.editAction = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": this.lp.edit
            }).inject(this.formBottomNode);
            this.editAction.addEvent("click", function (e) {
                this.editWork(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": this.lp.close
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    editWork : function(){
        this.formTopNode = null;
        if(this.setFormNodeSizeFun && this.app ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();
        this.edit();
    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            if( !data.flag ){
                data.reportId = this.reportData.id;
                data.workInfoId = this.keyworkData.id;
                data.keyWorkId = this.keyworkData.keyWorkId;
                data.flag = this.reportData.flag;
                data.year = this.reportData.year;
                data.month = this.reportData.month;
                data.week = this.reportData.week;
                data.date = this.reportData.date;
                data.targetPerson = ( layout.desktop.session.user || layout.user ).distinguishedName;
                data.orderNumber = this.options.orderNumber || 1;
            }
            data.workDescribe = data.workContent;
            data.title = data.workTitle;
            this.actions.saveWork( data, function(json){
                this.app.notice(this.lp.save_success, "success", this.formNode );
                var view = this.view;
                this.close();
                this.view.reload();
            }.bind(this));
        }
    },
    remove: function( ev ){
        var view = this.view;
        this.app.common.deleteWork( this.data, ev, function(){
            view.reload();
            this.close();
        }.bind(this));
    },
    loadAttachment: function( area ){
        this.attachment = new MWF.xApplication.Report.Attachment( area, this.app, this.app.restActions, this.lp, {
            documentId : this.advanceId || this.data.id,
            isNew : this.isNew,
            isEdited : this.isEdited,
            "size" : "min",
            onQueryUploadAttachment : function(){
                this.attachment.isQueryUploadSuccess = true;
            }.bind(this),
            onDelete : function( data ){
            }.bind(this)
        });
        this.attachment.load();
    },
    getEditPermission : function(){
        if( !this.options.editedAble )return false;
        var username = ( layout.desktop.session.user || layout.user ).distinguishedName;
        var reportTarget = this.view && this.view.report && this.view.report.data && this.view.report.data.targetPerson;
        if(  username != this.data.targetPerson && !this.app.common.isAdmin() && username != reportTarget ){
            return false;
        }
        return true;
    }
});

MWF.xApplication.Report.CustomWorkForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "report",
        "width": 800,
        "height": 550,
        "hasTop": true,
        "hasIcon": false,
        "maxAction" : true,
        "draggable": true,
        "resizeable": true,
        "editedAble" : true
    },
    _createTableContent: function () {
        if( this.data.progressContent && this.data.progressContent=="暂无内容" )this.data.progressContent="";
        this.formTopTextNode.set( "text", this.lp.work );

        var boxStyle = (this.isEdited || this.isNew) ? "border:1px solid #ccc; border-radius: 4px;overflow: hidden;padding:8px;" : "";

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='workTitle' width='20%'></td>" +
            "    <td styles='formTableValue14' colspan='3' item='workTitle'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='workDescribe'></td>" +
            "    <td styles='formTableValue14' item='workDescribe' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='workTag'></td>" +
            "    <td styles='formTableValue14' item='workTag' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='progressContent'></td>" +
            "    <td styles='formTableValue14' item='progressContent' colspan='3'></td></tr>" +
                //"<tr><td styles='formTableValue14' item='attachmentArea' colspan='4'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        this.loadForm()

    },
    loadForm : function( ){
        var _self = this;
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                usesNewVersion : true,
                isEdited: this.isEdited || this.isNew,
                style : "report",
                hasColon : true,
                itemTemplate: {
                    workTitle: { type : "text", text : "工作标题", notEmpty : true  },
                    workDescribe: { type : "textarea", text : this.lp.workContent, notEmpty : true },
                    workTag : { type:"mselector" ,text : "工作标签", notEmpty : true,  mSelectorOptions : {
                        "defaultOptionLp" : "选择或填写标签",
                        "inputEnable" : true,
                        "valueField" : "tagName",
                        "width" : "500px",
                        "onLoadData" : function( callback ){
                            _self.actions.listWorkTagWithUnit( _self.reportData.targetUnit, function( json ){
                                var arr = [];
                                (json.data || []).each( function(d){
                                    if(d.tagName != "部门重点工作" )arr.push(d)
                                });
                                callback( arr );
                            }.bind(this))
                        }
                    }},
                    progressContent: { text : this.lp.completion, type : "textarea", notEmpty : true, style : { height : "150px" } }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": this.lp.save
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        if( this.isEdited ){
            this.removeAction = new Element("button.inputCancelButton", {
                "styles": this.css.inputCancelButton,
                "text": this.lp.remove
            }).inject(this.formBottomNode);

            this.removeAction.addEvent("click", function (e) {
                this.remove(e);
            }.bind(this));
        }

        //var identityList = layout.desktop.session.user.identity
        if( !this.isEdited && !this.isNew && this.getEditPermission()){
            this.editAction = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": this.lp.edit
            }).inject(this.formBottomNode);
            this.editAction.addEvent("click", function (e) {
                this.editWork(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": this.lp.close
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    editWork : function(){
        this.formTopNode = null;
        if(this.setFormNodeSizeFun && this.app ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();
        this.edit();
    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            if( data.workTag == "选择或填写标签" || data.workTag == ""){
                this.app.notice("请选择或填写工作标签","error", this.formTableArea);
                return false;
            }
            //data.workTitle = this.form.getItem("workId").getText();
            if( !data.flag ){
                data.reportId = this.reportData.id;
                //data.workInfoId = this.keyworkData.id;
                //data.keyWorkId = this.keyworkData.workId;
                data.flag = this.reportData.flag;
                data.year = this.reportData.year;
                data.month = this.reportData.month;
                data.week = this.reportData.week;
                data.date = this.reportData.date;
                data.targetPerson = ( layout.desktop.session.user || layout.user ).distinguishedName;
                data.orderNumber = this.options.orderNumber || 1;
            }
            if( data.keyWorkObject ){
                delete data.keyWorkObject;
            }
            this.actions.saveWork( data, function(json){
                this.app.notice(this.lp.save_success, "success", this.formNode );
                var view = this.view;
                this.close();
                this.view.reload();
            }.bind(this));
        }
    },
    remove: function( ev ){
        var view = this.view;
        this.app.common.deleteCustomWork( this.data, ev, function(){
            view.reload();
            this.close();
        }.bind(this));
    },
    loadAttachment: function( area ){
        this.attachment = new MWF.xApplication.Report.Attachment( area, this.app, this.app.restActions, this.lp, {
            documentId : this.advanceId || this.data.id,
            isNew : this.isNew,
            isEdited : this.isEdited,
            "size" : "min",
            onQueryUploadAttachment : function(){
                this.attachment.isQueryUploadSuccess = true;
            }.bind(this),
            onDelete : function( data ){
            }.bind(this)
        });
        this.attachment.load();
    },
    getEditPermission : function(){
        if( !this.options.editedAble )return false;
        var username = ( layout.desktop.session.user || layout.user ).distinguishedName;
        var reportTarget = this.view && this.view.report && this.view.report.data && this.view.report.data.targetPerson;
        if(  username != this.data.targetPerson && !this.app.common.isAdmin() && username != reportTarget ){
            return false;
        }
        return true;
    }
});

MWF.xApplication.Report.ExtWorkForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "report",
        "width": 800,
        "height": 450,
        "hasTop": true,
        "hasIcon": false,
        "maxAction" : true,
        "draggable": true,
        "resizeable": true,
        "editedAble" : true,
        "category" : ""
    },
    _createTableContent: function () {
        this.formTopTextNode.set( "text", this.lp[ this.options.category ] );

        var boxStyle = (this.isEdited || this.isNew) ? "border:1px solid #ccc; border-radius: 4px;overflow: hidden;padding:8px;" : "";

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='title' width='20%'></td>" +
            "    <td styles='formTableValue14' colspan='3' item='title'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='content'></td>" +
            "    <td styles='formTableValue14' item='content' colspan='3'></td></tr>" +
                //"<tr><td styles='formTableValue14' item='attachmentArea' colspan='4'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        this.loadForm()

    },
    loadForm : function( ){
        var _self = this;
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                usesNewVersion : true,
                isEdited: this.isEdited || this.isNew,
                style : "report",
                hasColon : true,
                itemTemplate: {
                    title: { type : "innerText", text : "类别", notEmpty : true, defaultValue : this.lp[ this.options.category ]  },
                    content: { text : "内容", type : "textarea", notEmpty : true, style : { height : "150px" } }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": this.lp.save
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        if( this.isEdited ){
            this.removeAction = new Element("button.inputCancelButton", {
                "styles": this.css.inputCancelButton,
                "text": this.lp.remove
            }).inject(this.formBottomNode);

            this.removeAction.addEvent("click", function (e) {
                this.remove(e);
            }.bind(this));
        }

        //var identityList = layout.desktop.session.user.identity
        if( !this.isEdited && !this.isNew && this.getEditPermission()){
            this.editAction = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": this.lp.edit
            }).inject(this.formBottomNode);
            this.editAction.addEvent("click", function (e) {
                this.editWork(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": this.lp.close
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    editWork : function(){
        this.formTopNode = null;
        if(this.setFormNodeSizeFun && this.app ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();
        this.edit();
    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            if( !data.id ){
                data.reportId = this.reportData.id;
                //data.workInfoId = this.keyworkData.id;
                //data.keyWorkId = this.keyworkData.workId;
                //data.flag = this.reportData.flag;
                //data.year = this.reportData.year;
                //data.month = this.reportData.month;
                //data.week = this.reportData.week;
                //data.date = this.reportData.date;
                data.category = this.keyworkData.id;
                data.targetPerson = ( layout.desktop.session.user || layout.user ).distinguishedName;
                data.orderNumber = this.options.orderNumber || 1;
            }
            this.actions["save"+this.options.category]( data, function(json){
                this.app.notice(this.lp.save_success, "success", this.formNode );
                var view = this.view;
                this.close();
                this.view.reload();
            }.bind(this));
        }
    },
    remove: function( ev ){
        var view = this.view;
        this.app.common.deleteExtWork( this.data, ev, function(){
            view.reload();
            this.close();
        }.bind(this), this.options.category);
    },
    loadAttachment: function( area ){
        this.attachment = new MWF.xApplication.Report.Attachment( area, this.app, this.app.restActions, this.lp, {
            documentId : this.advanceId || this.data.id,
            isNew : this.isNew,
            isEdited : this.isEdited,
            "size" : "min",
            onQueryUploadAttachment : function(){
                this.attachment.isQueryUploadSuccess = true;
            }.bind(this),
            onDelete : function( data ){
            }.bind(this)
        });
        this.attachment.load();
    },
    getEditPermission : function(){
        if( !this.options.editedAble )return false;
        var username = ( layout.desktop.session.user || layout.user ).distinguishedName;
        //var usernameCN = username.split("@")[0];
        var reportTarget = this.view && this.view.report && this.view.report.data && this.view.report.data.targetPerson;
        if(  username != this.data.targetPerson  && !this.app.common.isAdmin() && username != reportTarget ){ //&& usernameCN != this.data.targetPerson
            return false;
        }
        return true;
    }
});

MWF.xApplication.Report.ReportTooltip = new Class({
    Extends: MTooltips,
    _loadCustom : function( callback ){
        if(callback)callback();
    },
    _getHtml : function(){

        var d = this.data;
        var lp = this.lp;

        var titleStyle = "font-size:14px;color:#333";
        var valueStyle = "font-size:14px;color:#666;padding-right:20px";

        var html =
            "<div style='overflow: hidden;padding:15px 20px 20px 10px;height:16px;line-height:16px;'>" +
            "   <div style='font-size: 16px;color:#333;float: left'>"+ lp[d.reportObjType] + lp[d.reportType] +"</div>"+
            "</div>"+
            "<div style='font-size: 16px;color:#333;padding:0px 10px 15px 20px;'>"+ d.title +"</div>"+
            "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
            "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
            "<tr><td style='"+titleStyle+"' width='100'>管理员:</td>" +
            "    <td style='"+valueStyle+"'>"+ ( d.targetPerson ? d.targetPerson.split("@")[0] : "" ) +"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>" + lp.targetUnit +":</td>" +
            "    <td style='"+valueStyle+"'>" + ( d.targetUnit ? d.targetUnit.split("@")[0] : "" )+ "</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.createDate +":</td>" +
            "    <td style='"+valueStyle+"'>"+ d.createDateString+"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.activityName +":</td>" +
            "    <td style='"+valueStyle+"'>"+ d.activityName+"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.currentPersonName+":</td>" +
            "    <td style='"+valueStyle+"'>"+ ( d.currentPersonName ? d.currentPersonName.split("@")[0] : "" ) +"</td></tr>" +
            "</table>";
        return html;
    }
});

MWF.xApplication.Report.SideBar = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize : function( container, app,  options){
        this.setOptions( options );
        this.container = container;
        this.app = app;
        //this.css = this.app.css;
        this.lp = this.app.lp;
        this.isHidden = false;
        this.cssPath = "/x_component_Report/$Common/"+this.options.style+"/sidebar/css.wcss";
        this._loadCss();

        this.load();
    },
    load : function(){
        this.node = new Element("div.sideBar", {
            "styles": this.css.node,
            events : {
                mousedown : function( ev ){
                    ev.stopPropagation();
                }
            }
        }).inject(this.container);

        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.loadStatusArea();

        new Element("div.contentLine", {
            "styles" : this.css.contentLine
        }).inject( this.contentNode );

        this.loadWaitDoNode();

        this.trapezoid = new Element("div.trapezoid",{
            "styles":this.css.trapezoid_toRight,
            events : {
                click : function(){
                    this.trigger();
                }.bind(this)
            }
        }).inject(this.node);

        this.listData( function(){
            this.loadWaitDo( function(){
                var x = this.node.getSize().x - 8;
                this.node.setStyle( "right", "-"+x+"px" );

                this.resetNodeSize();
                this.resetNodeSizeFun = this.resetNodeSize.bind(this);
                this.app.addEvent("resize", this.resetNodeSizeFun );

                this.hideFun = this.hide.bind(this);
                this.app.node.addEvent("mousedown", this.hideFun);
            }.bind(this));
        }.bind(this) )
    },
    loadStatusArea : function(){
        var area = new Element("div", {
            "styles" : this.css.statusArea
        }).inject( this.contentNode );

        var lp = this.lp.config;

        var html = "<div class='titleDiv'>"+lp.reportStatus+"</div>" +
            "<div class = 'statusStyle'>"+
            "   <div class='statusIconStyle' style='background-color:"+ lp.waitColor +"'></div>" +
            "   <div class = 'statusTextStyle'>"+lp.wait+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div class='statusIconStyle' style='background-color:"+ lp.auditColor +"'></div>" +
            "   <div class = 'statusTextStyle'>"+lp.audit+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div class='statusIconStyle' style='background-color:"+ lp.progressColor +"'></div>" +
            "   <div class = 'statusTextStyle'>"+lp.progress+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div  class='statusIconStyle' style='background-color:"+ lp.completedColor +"'></div>" +
            "   <div class = 'statusTextStyle'>"+lp.completed+"</div></div>" +
            "</div>";

        area.set("html", html);
        area.getElements("div.titleDiv").setStyles( this.css.titleDiv );
        area.getElements("div.statusStyle").setStyles( this.css.statusStyle );
        area.getElements("div.statusIconStyle").setStyles( this.css.statusIconStyle );
        area.getElements("div.statusIconStyle2").setStyles( this.css.statusIconStyle2 );
        area.getElements("div.statusTextStyle").setStyles( this.css.statusTextStyle );
    },
    loadWaitDoNode: function(){

        var area = new Element("div.reportArea", {
            "styles" : this.css.reportArea
        }).inject( this.contentNode );

        new Element("div.titleDiv", {
            "styles" : this.css.titleDiv,
            "text" : this.lp.reportNotice
        }).inject( area );

        this.reportNode = Element("div", {
            "styles" : this.css.reportNode
        }).inject( area );

    },
    loadWaitDo : function( callback ){
        var today = new Date();
        var user = ( layout.desktop.session.user || layout.user );
        var dn = user.distinguishedName;

        var data = this.data;

        if( user.distinguishedName ){
            var userName = user.distinguishedName.split("@")[0]
        }else{
            var userName = user.name
        }
        var lp = data.length ?  this.lp.reportTopInfor : this.lp.noReportTopInfor ;
        this.reportTopNode = new Element("div", {
            "styles" : this.css.reportTopNode,
            "html" : lp.replace("{userName}",userName).replace("{count}",data.length )
        }).inject( this.reportNode );

        this.scrollNode = new Element("div.scrollNode", {
            "styles" : this.css.scrollNode
        }).inject( this.reportNode );

        this.reportItemContainer = new Element("div.reportItemContainer", {
            "styles" : this.css.reportItemContainer
        }).inject( this.scrollNode );

        data.each( function( d, i ){
            var itemNode = new Element("div.reportItemNode", {
                "styles" : this.css.reportItemNode,
                "events" : {
                    click : function(){
                        this.obj.app.common.openReport( this.data );
                    }.bind({ obj : this, data : d })
                }
            }).inject( this.reportItemContainer );

            this.tooltipList = this.tooltipList || [];
            this.tooltipList.push( new MWF.xApplication.Report.ReportTooltip(this.app.content, itemNode, this.app, d, {
                    axis : "x",
                    hiddenDelay : 300,
                    displayDelay : 300
                })
            );

            var colorNode = new Element("div.reportItemColorNode", {
                "styles" : this.css.reportItemColorNode,
                "text" : i+1
            }).inject( itemNode );

            var textNode = new Element("div.reportItemTextNode", {
                "styles" : this.css.reportItemTextNode,
                "text" : d.title
            }).inject( itemNode );

            var lp = this.lp.config;
            var status;
            if( d.reportStatus == "审核中" && user.distinguishedName == d.currentPersonName ){
                status = "需要我审核"
            }else{
                status = d.reportStatus;
            }
            switch (status){
                case "汇报者填写":
                    colorNode.setStyles({ "background-color": lp.waitColor });
                    break;
                case "审核中":
                    colorNode.setStyles({ "background-color": lp.progressColor });
                    break;
                case "需要我审核":
                    colorNode.setStyles({ "background-color": lp.auditColor });
                    break;
                case "已完成":
                    colorNode.setStyles({ "background-color": lp.completedColor });
                    break;
            }

            var y = itemNode.getSize().y ;
            colorNode.setStyle("margin-top", ( y - 20)/2 );
        }.bind(this));

        this.setScrollBar( this.scrollNode );
        if( callback )callback();
    },
    listData : function( callback ){
        this.app.restActions.listReportNextWithFilter((0), 100, {
            targetList : [this.app.userName],
            reportStatus : "汇报者填写"
        }, function(json){
            if( !json.data )json.data = [];
            this.data = json.data;

            this.app.restActions.listReportNextWithFilter((0), 100, {
                currentPersonList : [this.app.userName],
                reportStatus : "审核中"
            }, function(j){
                if( !j.data )j.data = [];
                j.data.each( function( j ){
                    this.data.push( j );
                }.bind(this));
                if (callback)callback();
            }.bind(this));
        }.bind(this));
    },
    trigger : function(){
        this.isHidden ? this.show( true ) : this.hide( true )
    },
    hide: function( isFireEvent ){
        var x = this.node.getSize().x - 9;
        var fx = new Fx.Morph(this.node, {
            "duration": "300",
            "transition": Fx.Transitions.Expo.easeOut
        });
        fx.start({
            //"opacity": 0
        }).chain(function(){
            this.isHidden = true;
            //this.node.setStyle("display", "none");
            this.node.setStyles({
                "right": "-"+x+"px"
            });
            this.trapezoid.setStyles( this.css.trapezoid_toLeft );
            //if(isFireEvent)this.app.fireEvent("resize");
        }.bind(this));
    },
    show: function( isFireEvent ){
        this.node.setStyles(this.css.node);
        this.trapezoid.setStyles( this.css.trapezoid_toRight );
        //var x = this.node.getSize().x - 8;
        //this.node.setStyles( "right", "-"+x+"px" );
        var fx = new Fx.Morph(this.node, {
            "duration": "500",
            "transition": Fx.Transitions.Expo.easeOut
        });
        this.app.fireAppEvent("resize");
        fx.start({
            "opacity": 1
        }).chain(function(){
            this.node.setStyles({
                //"position": "static",
                //"width": "auto"
                "right": "0px"
            });
            this.isHidden = false;
            //if(isFireEvent)this.app.fireEvent("resize");
        }.bind(this))
    },
    //show: function(){
    //    this.node.setStyles(this.css.configNode);
    //    var fx = new Fx.Morph(this.node, {
    //        "duration": "500",
    //        "transition": Fx.Transitions.Expo.easeOut
    //    });
    //    fx.start({
    //        "opacity": 1
    //    }).chain(function(){
    //        this.hideFun = this.hide.bind(this);
    //        this.app.content.addEvent("mousedown", this.hideFun);
    //    }.bind(this));
    //},
    //hide: function(){
    //    this.node.destroy();
    //    this.app.content.removeEvent("mousedown", this.hideFun);
    //    MWF.release(this);
    //},
    resetNodeSize: function(){
        var size = this.container.getSize();

        this.node.setStyle("height", size.y - 50 );
        this.trapezoid.setStyle("top", ( (size.y - 50)/2 - this.trapezoid.getSize().y/2 ));

        var y = size.y - 395;
        var meetContainerY = this.reportItemContainer.getSize().y + 12;
        this.scrollNode.setStyle("height", Math.min( y, meetContainerY ) );
    },
    getSize : function(){
        //var size = this.node.getSize();
        //return {
        //    x : this.isHidden ? 9 : size.x,
        //    y : size.y
        //}
        return { x : 9, y : 0 }
    },
    showByType : function( type ){

    },
    reload : function(){
        this.destory();
        this.app.reload();
    },
    openReport : function( data ){
        var form = new MWF.xApplication.Report.ReportForm(this, data, {}, {app:this.app});
        form.view = this.app;
        form.open();
    },
    destory : function(){
        this.tooltipList.each( function( t ){
            t.destory();
        });
        this.app.removeEvent("resize", this.resetNodeSizeFun );
        this.app.node.removeEvent("mousedown", this.hideFun);
        this.node.destory();
    }
});

MWF.xApplication.Report.ReportArea = new Class({
    initialize: function(container, view, data){
        this.container = container;
        this.view = view;
        this.css = this.view.css;
        this.app = this.view.app;
        this.data = data;
        this.beginDate = Date.parse(this.data.startTime);
        this.endDate = Date.parse(this.data.completedTime);

        this.userName = ( layout.desktop.session.user || layout.user ).distinguishedName;
        this.userId = ( layout.desktop.session.user || layout.user ).id;

        this.path = "/x_component_Report/$Common/default/reportarea/";
        this.cssPath = "/x_component_Report/$Common/default/reportarea/css.wcss";
        this._loadCss();
        this.load();
    },
    load: function(){
        var d = this.data;

        this.node = new Element("div", {"styles": this.css.reportNode}).inject( this.container );
        this.node.addEvents({
            mouseenter : function(){
                this.node.setStyles( this.css.reportNode_over );
                this.subjectNode.setStyles( this.css.reportSubjectNode_over );
            }.bind(this),
            mouseleave : function(){
                this.node.setStyles( this.css.reportNode );
                this.subjectNode.setStyles( this.css.reportSubjectNode );
            }.bind(this),
            click : function(){
                this.openReport()
            }.bind(this)
        });

        this.colorNode = new Element("div", {"styles": this.css.reportColorNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.reportContentNode}).inject(this.node);

        //var beginTime = (this.beginDate.getHours() < 12 ? this.app.lp.am : this.app.lp.pm) + " " + this.getString( this.beginDate.getHours() ) + ":" + this.getString( this.beginDate.getMinutes() );
        //var endTime = (this.endDate.getHours() < 12 ? this.app.lp.am : this.app.lp.pm) + " " + this.getString( this.endDate.getHours() ) + ":" + this.getString( this.endDate.getMinutes() );
        //this.timeNode = new Element("div", {
        //    "styles": this.css.reportTimeNode,
        //    "text" : beginTime + "-" + endTime
        //}).inject(this.contentNode);

        this.subjectNode = new Element("div", {
            "styles": this.css.reportSubjectNode,
            "text": this.data.title
        }).inject(this.contentNode);

        this.descriptionNode = new Element("div", {
            "styles": this.css.reportDescriptionNode,
            "text" : this.app.lp[d.reportObjType] + this.app.lp[d.reportType] + "　" +
            ( d.currentPersonName ? d.currentPersonName.split("@")[0]　+ "　" : "" )  + d.activityName　
        }).inject(this.contentNode);

        var status;
        var lp = this.app.lp.config;
        if( d.reportStatus == "审核中" && this.app.userName == d.currentPersonName ){
            status = "需要我审核"
        }else{
            status = d.reportStatus;
        }
        switch (status){
            case "汇报者填写":
                this.colorNode.setStyles({ "background-color": lp.waitColor });
                break;
            case "审核中":
                this.colorNode.setStyles({ "background-color": lp.progressColor });
                break;
            case "需要我审核":
                this.colorNode.setStyles({ "background-color": lp.auditColor });
                break;
            case "已完成":
                this.colorNode.setStyles({ "background-color": lp.completedColor });
                break;
        }

        this.resetNodeSize();

        this.loadTooltip();

    },
    getString : function( str ){
        var s = "00" + str;
        return s.substr(s.length - 2, 2 );
    },
    _loadCss: function(){
        var key = encodeURIComponent(this.cssPath);
        if (MWF.widget.css[key]){
            this.css = MWF.widget.css[key];
        }else{
            var r = new Request.JSON({
                url: this.cssPath,
                secure: false,
                async: false,
                method: "get",
                noCache: false,
                onSuccess: function(responseJSON, responseText){
                    this.css = responseJSON;
                    MWF.widget.css[key] = responseJSON;
                }.bind(this),
                onError: function(text, error){
                    alert(error + text);
                }
            });
            r.send();
        }
    },
    loadTooltip : function( isHideAttachment ){
        this.tooltip = new MWF.xApplication.Report.ReportTooltip(this.app.content, this.node, this.app, this.data, {
            axis : "x",
            hiddenDelay : 300,
            displayDelay : 300,
            isHideAttachment : isHideAttachment
        });
    },
    openReport : function(){
        this.app.common.openReport( this.data, this.view )
    },
    resetNodeSize: function(){
        var contentSize = this.contentNode.getSize();
        this.colorNode.setStyle("height", contentSize.y );
    },
    destroy: function(){
        if(this.tooltip)this.tooltip.destroy();
        this.node.destroy();
        MWF.release(this);
    }
});

MWF.xApplication.Report.KeyWorkTooltip = new Class({
    Extends: MTooltips,
    _loadCustom : function( callback ){
        if(callback)callback();
    },
    _getHtml : function(){
        var d = this.data;
        var lp = this.lp.keyWorkList.popupForm;

        var titleStyle = "font-size:14px;color:#333;";
        var valueStyle = "font-size:14px;color:#666;padding-right:20px";

        var department = [];
        if( d.deptlist ){
            d.deptlist.each( function( dept ){
                department.push( dept.split("@")[0] )
            })
        }

        var html =
            "<div style='overflow: hidden;padding:15px 20px 20px 10px;height:16px;line-height:16px;'>" +
            "   <div style='font-size: 16px;color:#333;float: left'>"+ this.lp.keyWorkList.name +"</div>"+
            "</div>"+
            "<div style='font-size: 16px;color:#333;padding:0px 10px 15px 20px;'>"+ d.strategydeploytitle +"</div>"+
            "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
            "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
            "<tr><td style='"+titleStyle+"' width='80'>"+ lp.sequencenumber+":</td>" +
            "    <td style='"+valueStyle+"'>"+ d.sequencenumber +"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.year +":</td>" +
            "    <td style='"+valueStyle+"'>"+ d.strategydeployyear+"</td></tr>" +
            "<tr style='display: "+ (department.length ? "" : "none") +"'><td style='"+titleStyle+"'>"+ lp.department +":</td>" +
            "    <td style='"+valueStyle+"'>"+  department.join("  ") +"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.description+":</td>" +
            "    <td style='"+valueStyle+"'>"+ d.strategydeploydescribe +"</td></tr>" +
            "</table>";
        return html;
    }
});

MWF.xApplication.Report.MeasureTooltip = new Class({
    Extends: MTooltips,
    _loadCustom : function( callback ){
        //if( this.options.measureId ){
        //    this.app.restActions.getMeasureById( this.options.measureId, function( json ){
        //        this.measureData = json.data;
        //        this.setContent();
        //        if(callback)callback();
        //    }.bind(this))
        //}else{
        //    if(callback)callback();
        //}
        //this.measureData = this.data;
        //if(callback)callback();

        var d = this.data;
        var lp = this.lp.measure.popupForm;

        var department = [];
        d.deptlist.each( function( dept ){
            department.push( dept.split("@")[0] )
        });

        var table = new Element( "table", {
            "width":"100%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : this.css.formTable
        }).inject( this.contentNode );

        var tr = new Element("tr").inject( table );
        new Element("td", {
            "text" : "举措",
            "width" : "70",
            "styles": this.css.formTableTitle
        }).inject( tr );
        var td =new Element("td", {
            "styles": this.css.formTableValue,
            "text" : d.measuresinfotitle
        }).inject( tr );

        tr = new Element("tr").inject( table );
        new Element("td", {
            "text" : lp.sequencenumber,
            "width" : "70",
            "styles": this.css.formTableTitle
        }).inject( tr );
        var td =new Element("td", {
            "styles": this.css.formTableValue,
            "text" : d.sequencenumber
        }).inject( tr );

        tr = new Element("tr").inject( table );
        new Element("td", {
            "text" : lp.year,
            "width" : "70",
            "styles": this.css.formTableTitle
        }).inject( tr );
        var td =new Element("td", {
            "styles": this.css.formTableValue,
            "text" : d.measuresinfoyear
        }).inject( tr );

        tr = new Element("tr").inject( table );
        new Element("td", {
            "text" : lp.department,
            "width" : "70",
            "styles": this.css.formTableTitle
        }).inject( tr );
        var td =new Element("td", {
            "styles": this.css.formTableValue,
            "text" : department.join("  ")
        }).inject( tr );

        tr = new Element("tr").inject( table );
        new Element("td", {
            "text" : lp.description,
            "width" : "70",
            "styles": this.css.formTableTitle
        }).inject( tr );
        var td =new Element("td", {
            "styles": this.css.formTableValue,
            "text" : d.measuresinfodescribe
        }).inject( tr );
        if(callback)callback();
    },
    setContent : function(){
        //this.contentNode.set( "html", this._getHtml() );
    },
    _getHtml : function(){
        //if( !this.data  )return;
        //var d = this.measureData || this.data;
        //var lp = this.lp.measure.popupForm;
        //
        //var titleStyle = "font-size:14px;color:#333";
        //var valueStyle = "font-size:14px;color:#666;padding-right:20px";
        //
        //var department = [];
        //d.deptlist.each( function( dept ){
        //    department.push( dept.split("@")[0] )
        //});
        //
        //var html =
        //    "<div style='overflow: hidden;padding:15px 20px 20px 10px;height:16px;line-height:16px;'>" +
        //    "   <div style='font-size: 16px;color:#333;float: left'>"+ this.lp.measure.name +"</div>"+
        //    "</div>"+
        //    "<div style='font-size: 16px;color:#333;padding:0px 10px 15px 20px;'>"+ d.measuresinfotitle +"</div>"+
        //    "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
        //    "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
        //    "<tr><td style='"+titleStyle+"' width='80'>"+ lp.sequencenumber+":</td>" +
        //    "    <td style='"+valueStyle+"'>"+ d.sequencenumber +"</td></tr>" +
        //    "<tr><td style='"+titleStyle+"'>"+ lp.year +":</td>" +
        //    "    <td style='"+valueStyle+"'>"+ d.measuresinfoyear+"</td></tr>" +
        //    "<tr><td style='"+titleStyle+"'>"+ lp.department +":</td>" +
        //    "    <td style='"+valueStyle+"'>"+  department.join("  ") +"</td></tr>" +
        //    "<tr><td style='"+titleStyle+"'>"+ lp.description+":</td>" +
        //    "    <td style='"+valueStyle+"'>"+ d.measuresinfodescribe +"</td></tr>" +
        //    "</table>";
        //return html;
    }
});

MWF.xApplication.Report.PriorityTooltip = new Class({
    Extends: MTooltips,
    _loadCustom : function( callback ){
        if( this.data.keyWorkId ){
            this.app.strategyActions.getPriorityById( this.data.keyWorkId, function( json ){
                this.priorityData = json.data;
                this.setContent();
                if(callback)callback();
            }.bind(this))
        }else{
            if(callback)callback();
        }
    },
    setContent : function(){
        this.contentNode.set( "html", this._getHtml() );
    },
    _getHtml : function(){
        if( !this.priorityData && this.data.keyWorkId )return;
        var d = this.priorityData || this.data;
        var lp = this.lp.priority.popupForm;

        var titleStyle = "font-size:14px;color:#333";
        var valueStyle = "font-size:14px;color:#666;padding-right:20px";


        var html =
            "<div style='overflow: hidden;padding:15px 20px 20px 10px;height:16px;line-height:16px;'>" +
            "   <div style='font-size: 16px;color:#333;float: left'>"+ this.lp.priority.name +"</div>"+
            "</div>"+
            "<div style='font-size: 16px;color:#333;padding:0px 10px 15px 20px;'>"+ d.keyworktitle +"</div>"+
            "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
            "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
            "<tr><td style='"+titleStyle+"' width='80'>"+ lp.sequencenumber+":</td>" +
            "    <td style='"+valueStyle+"'>"+ d.sequencenumber +"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.year +":</td>" +
            "    <td style='"+valueStyle+"'>"+ d.keyworkyear+"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.validDate +":</td>" +
            "    <td style='"+valueStyle+"'>"+ d.keyworkbegindate + " "+ lp.validDateConnect + " "+  d.keyworkenddate +"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.department +":</td>" +
            "    <td style='"+valueStyle+"'>"+ (d.keyworkunit ? d.keyworkunit.split("@")[0] : "") +"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.description+":</td>" +
            "    <td style='"+valueStyle+"'>"+ d.keyworkdescribe +"</td></tr>" +
            "</table>";

        return html;
    }
});

MWF.xApplication.Report.ShowMeasureTooltip  = new Class({
    Extends: MTooltips,
    options : {
        overflow : "scroll",
        nextMonth : false
    },
    _loadCustom : function( callback ){
        var data = this.data;
        var measureNode = new Element("div", {
            //styles : this.css.measureDeployNode
        }).inject( this.contentNode );

        var measureObject = {};
        var selectableMeasures = this.options.nextMonth ? data.nextMonth_selectableMeasures : data.selectableMeasures;
        ( selectableMeasures || [] ).each( function( measure ){
            measureObject[ measure.id ] = measure;
        }.bind(this));
        this.measuresList.each( function( id ){
            var measure = measureObject[id];
            if( measure ){
                var table = new Element( "table", {
                    "width":"100%",
                    "border":"0",
                    "cellpadding":"5",
                    "cellspacing":"0",
                    "styles" : this.css.formTable
                }).inject( measureNode );
                var tr = new Element("tr").inject( table );
                new Element("td", {
                    "text" : "举措",
                    "width" : "70",
                    "styles": this.css.formTableTitle
                }).inject( tr );
                new Element("td", {
                        "text": measure.measuresinfotitle,
                        "styles": this.css.formTableValue
                    }).inject( tr );
                tr = new Element("tr").inject( table );
                new Element("td", {
                    "text" : "内容",
                    "styles": this.css.formTableTitle
                }).inject( tr );
                new Element("td", {
                    "text": measure.measuresinfodescribe,
                    "styles": this.css.formTableValue
                }).inject( tr );
            }
        }.bind(this));
        if(callback)callback();
    }
});

MWF.xApplication.Report.SelectMeasureTooltips = new Class({
    Extends: MTooltips,
    options : {
        overflow : "scroll"
    },
    _loadCustom : function( callback ){
        var data = this.data;
        var measureNode = new Element("div", {
            //styles : this.css.measureDeployNode
        }).inject( this.contentNode ); 
        measureNode.setStyle("padding-bottom", "10px"); 

        //var measureObject = {};
        ( data.nextMonth_selectableMeasures || [] ).each( function( measure ){
            //measureObject[ measure.id ] = measure;
            var table = new Element( "table", {
                "width":"100%",
                "border":"0",
                "cellpadding":"5",
                "cellspacing":"0",
                "styles" : this.css.formTable
            }).inject( measureNode );

            var tr = new Element("tr").inject( table );

            tr = new Element("tr").inject( table );
            new Element("td", {
                "text" : "举措",
                "width" : "70",
                "styles": this.css.formTableTitle
            }).inject( tr );
            var td =new Element("td", {
                "styles": this.css.formTableValue
            }).inject( tr );

            var table_select = new Element( "table", {
                "width":"100%",
                "border":"0",
                "cellpadding":"0",
                "cellspacing":"0"
            }).inject( td );
            var tr_select = new Element("tr").inject( table_select );

            var td_select =new Element("td", { width : "30" }).inject( tr_select );
            var checkbox = new Element( "input", {
                type : "checkbox",
                "data-id" : measure.id,
                checked : this.measuresList.contains( measure.id  )
            }).inject( td_select );
            checkbox.addEvent("click", function(){
                var value = [];
                this.contentNode.getElements("input[type='checkbox']").each( function( checkbox ){
                    if( checkbox.get("checked") ){
                        value.push( checkbox.get("data-id") );
                    }
                }.bind(this));
                var list = [];
                ( this.data.nextMonth_selectableMeasures || [] ).each( function( measure ){
                    if( value.contains( measure.id ) ){
                        list.push( measure )
                    }
                }.bind(this));
                this.fireEvent("select", [list, value]);
            }.bind(this));

            var td_select =new Element("td").inject( tr_select );
            new Element("div", { "text": measure.measuresinfotitle }).inject( td_select );


            tr = new Element("tr").inject( table );
            new Element("td", {
                "text" : "内容",
                "styles": this.css.formTableTitle
            }).inject( tr );
            new Element("td", {
                "text": measure.measuresinfodescribe,
                "styles": this.css.formTableValue
            }).inject( tr );
        }.bind(this));
        if(callback)callback();
    }
});

MWF.xApplication.Report.SelectMeasureForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "report",
        "width": 800,
        "height": 450,
        "minWidth" : 700,
        "minHeight" : 300,
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "maxAction" : true,
        "hasBottom": true,
        "draggable": true,
        "resizeable": true,
        "editedAble" : true,
        "closeAction": true
    },
    _createTableContent: function () {
        var data = this.data;

        this.formTopTextNode.set( "text", "选择举措" );

        var node = new Element("div", {
            styles : { "overflow": "hidden", "padding":"10px", "margin" : "20px 20px" }
        }).inject( this.formTableArea );

        //this.loadTooltip( keyworkNode, data, "keywork"  );

        var measureNode = new Element("div", {
            styles : this.css.measureDeployNode
        }).inject( node );

        var measureIdList = [];
        var measureTitleList = [];
        var measureObject = {};
        data.selectableMeasures.each( function( measure ){
            measureIdList.push( measure.id );
            measureTitleList.push( measure.measuresinfotitle );
            measureObject[ measure.id ] = measure;
        }.bind(this));
        this.item = new MDomItem( measureNode, {
            name : "measures", type : "checkbox", selectValue: measureIdList, selectText : measureTitleList, value : data.measuresList, style : { "overflow" : "hidden" },
            onPostLoad : function( item ){
                item.items.each( function( it ){
                    var iconNode = new Element("span", {
                        styles : this.css.measureIconNode
                    }).inject( it, "top" );
                    var id = it.getElement("input[type='checkbox']").get("value");
                    this.loadMeasureTooltip(iconNode, id);

                    var d = measureObject[id];
                    var descriptionNode = new Element("div",{
                        styles : this.css.measuresDescribeNode,
                        text : "工作内容：" + d.measuresinfodescribe
                    }).inject( it, "after" )
                }.bind(this))
            }.bind(this)
        } , null, this.app, this.css);
        this.item.load();
    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": "确定"
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": this.lp.close
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    loadMeasureTooltip: function( node, measureId ){
        new MWF.xApplication.Report.MeasureTooltip( this.app.content, node, this.app, null, {
            position : { x : "right", y : "auto" },
            measureId : measureId,
            displayDelay : 300
        })
    },
    save: function(){
        var list = [];
        var value = this.item.getValue();
        this.data.selectableMeasures.each( function( measure ){
            if( value.contains( measure.id ) ){
                list.push( measure )
            }
        }.bind(this));
        this.fireEvent("postOk", [list, value]);
        this.close()
    }
});

MWF.xApplication.Report.StatisticsForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "report",
        "width": 800,
        "height": 450,
        "hasTop": true,
        "hasIcon": false,
        "maxAction" : true,
        "draggable": true,
        "resizeable": true,
        "editedAble" : true,
        "category" : ""
    },
    _createTableContent: function () {
        this.lp = {
            ok : "导出",
            close : "取消"
        };
        this.formTopTextNode.set( "text", "导出" );

        var boxStyle = (this.isEdited || this.isNew) ? "border:1px solid #ccc; border-radius: 4px;overflow: hidden;padding:8px;" : "";

        this.formTableArea.setStyle("margin-top","20px");

        var selectAll = "";
        if( this.app.common.isAdmin() || this.app.exportAllFlag ){
            selectAll = "<tr><td styles='formTableTitleP14' lable='allUnit'></td>" +
                "    <td styles='formTableValueP14' item='allUnit' ></td></tr>"
        }

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitleP14' lable='year' width='20%'></td>" +
            "    <td styles='formTableValueP14' item='year'></td></tr>" +
            "<tr><td styles='formTableTitleP14' lable='month'></td>" +
            "    <td styles='formTableValueP14' item='month' ></td></tr>" +
            "<tr><td styles='formTableTitleP14' lable='wfProcessStatus'></td>" +
            "    <td styles='formTableValueP14' item='wfProcessStatus' ></td></tr>" +
            "<tr><td styles='formTableTitleP14' lable='unitList'></td>" +
            "    <td styles='formTableValueP14' item='unitList' ></td></tr>" +
            selectAll +
            "</table>";
        this.formTableArea.set("html", html);

        this.loadForm()

    },
    loadForm : function( ){
        var _self = this;
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                usesNewVersion : true,
                isEdited: this.isEdited || this.isNew,
                style : "report",
                hasColon : true,
                itemTemplate: {
                    year: { type : "select", notEmpty : true, text : "年度",
                        selectText : function(){
                            var arr = [];
                            var data = new Date();
                            data.decrement("year",5);
                            for( var i=0; i<11; i++ ){
                                data.increment("year",1);
                                arr.push(data.getFullYear()+ "年")
                            }
                            return arr
                        },
                        selectValue : function(){
                            var arr = [];
                            var data = new Date();
                            data.decrement("year",5);
                            for( var i=0; i<11; i++ ){
                                data.increment("year",1);
                                arr.push(data.getFullYear().toString())
                            }
                            return arr
                        },
                        defaultValue : (new Date()).getFullYear().toString(),
                        event : {
                            change : function(){
                                this.listUnitNamesForReport();
                            }.bind(this)
                        }
                    },
                    month: { type : "select", notEmpty : true, text : "月份",
                        selectText : ["1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"] ,
                        selectValue : ["01","02","03","04","05","06","07","08","09","10","11","12"],
                        defaultValue : (new Date().format("%m")).toString(),
                        event : {
                            change : function(){
                                this.listUnitNamesForReport();
                            }.bind(this)
                        }
                    },
                    unitList : { type : "checkbox", notEmpty : true, text : "部门" },
                    wfProcessStatus: { type : "select", text : "审批状态",
                        selectText : ["全部","已完成","流转中"] ,
                        selectValue : ["","已完成","流转中"],
                        event : {
                            change : function(){
                                this.listUnitNamesForReport();
                            }.bind(this)
                        }
                    },
                    allUnit : { type : "checkbox", text : "所有部门", selectValue : ["yes"], selectText : ["是"], event : {
                        change : function(item, ev){
                            if(item.getValue().join() == "yes"){
                                this.form.getItem("unitList").setValue( this.allUnitList );
                            }else{
                                this.form.getItem("unitList").setValue("");
                            }
                        }.bind(this)
                    }}
                }
            }, this.app);
            this.form.load();

            this.listUnitNamesForReport( (new Date()).getFullYear().toString(), (new Date().format("%m")).toString() )
        }.bind(this), true);
    },
    listUnitNamesForReport : function(year, month, wfProcessStatus){
        var unitListItem = this.formTableArea.getElement("[item='unitList']");
        if(this.nounitListNode)this.nounitListNode.destroy();
        if( !year )year = this.form.getItem("year").getValue();
        if( !month )month = this.form.getItem("month").getValue();
        if( !wfProcessStatus )wfProcessStatus =  this.form.getItem("wfProcessStatus").getValue();
        this.app.restActions.listUnitNamesForReport({
            year : year,
            month : month,
            wfProcessStatus : wfProcessStatus ? [wfProcessStatus] : null
        }, function( json ){
            var value = this.allUnitList = [];
            var text = [];
            var data = json.data || [];
            if( this.app.exportAllFlag ){
                data.each( function( d ){
                    value.push(d.value );
                    text.push(d.value.split("@")[0] );
                });
            }else{
                data.each(function(d){
                    if( this.app.unitWithExport.contains(d.value ) ){
                        value.push(d.value );
                        text.push(d.value.split("@")[0] );
                    }
                }.bind(this));
            }
            if( value.length == 0 ){
                this.form.getItem("unitList").disable();
                this.nounitListNode = new Element("div", {
                    text : "系统未找"+year+"年"+ month  +"月"+this.form.getItem("wfProcessStatus").getValue()+"的工作汇报"
                }).inject( unitListItem );
                var allUnit = this.form.getItem("allUnit");
                if( allUnit ){
                    allUnit.setValue( "" );
                }
            }else{
                if( this.nounitListNode )this.nounitListNode.destroy();
                var unitList = this.form.getItem("unitList");
                unitList.resetItemOptions( value, text, true );
                unitList.setValue( this.allUnitList );
                var allUnit = this.form.getItem("allUnit");
                if( allUnit ){
                    allUnit.setValue( "yes" );
                }
            }
        }.bind(this), null, false)
    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": this.lp.ok
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.export(e);
            }.bind(this));
        }


        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": this.lp.close
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    export: function(){
        var data = this.form.getResult(true,null,true,false,true);
        if( data ){
            data.wfProcessStatus = data.wfProcessStatus ? [data.wfProcessStatus] : null;
            data.unitList = data.unitList || null;
            this.app.restActions.statByUnit( data , function( json ){
                if( json.data && json.data.id ){
                    this.app.restActions.getExportFileStream( json.data.id );
                }else{
                    this.app.notice("系统中未找到指定条件的数据","error");
                }
            }.bind(this));
        }

    }
});



MWF.xApplication.Report.SummarizationForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "report",
        "width": "80%",
        "height": "90%",
        "hasTop": true,
        "hasBottom": false,
        "hasIcon": false,
        "maxAction" : true,
        "draggable": true,
        "resizeable": true,
        "editedAble" : true,
        "category" : ""
    },
    createContent: function () {
        this.tableContainer = new Element("div.formTabContainer",{
            styles : {
                "padding-top" : "10px",
                "margin":  "0px auto 20px",
                "width": "90%"
            }
        }).inject(this.formNode);

        this.formContentNode = new Element("div.formContentNode", {
            "styles": this.css.formContentNode
        }).inject(this.formNode);

        this.formTableContainer = new Element("div.formTableContainer", {
            "styles": this.css.formTableContainer
        }).inject(this.formContentNode);

        this.formTableArea = new Element("div.formTableArea", {
            "styles": this.css.formTableArea
        }).inject(this.formTableContainer);

        this._createTableContent();
    },
    _createTableContent: function () {
        this.lp = {
            ok : "查询",
            close : "取消"
        };
        this.formTopTextNode.set( "text", "部门五项重点工作统览" );

        var boxStyle = (this.isEdited || this.isNew) ? "border:1px solid #ccc; border-radius: 4px;overflow: hidden;padding:8px;" : "";

        this.formTableArea.setStyle("margin-top","20px");
        this.formTableContainer.setStyle("width","90%");

        var selectAll = "";

        var html = "<table width='96%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitleP14' lable='year'></td>" +
            "    <td styles='formTableValueP14' item='year'></td>" +
            "    <td styles='formTableTitleP14' lable='month'></td>" +
            "    <td styles='formTableValueP14' item='month' ></td>" +
            "   <td styles='formTableTitleP14' lable='wfProcessStatus'></td>" +
            "    <td styles='formTableValueP14' item='wfProcessStatus' ></td>" +
            "    <td styles='formTableValueP14' item='ok' style='width: 80px;'></td></tr>" +
            "</table>";
        this.tableContainer.set("html", html);

        this.loadForm()

    },
    loadForm : function( ){
        var _self = this;
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.tableContainer, this.data, {
                usesNewVersion : true,
                isEdited: this.isEdited || this.isNew,
                style : "report",
                hasColon : true,
                itemTemplate: {
                    year: { type : "select", notEmpty : true, text : "年度",
                        selectText : function(){
                            var arr = [];
                            var data = new Date();
                            data.decrement("year",5);
                            for( var i=0; i<11; i++ ){
                                data.increment("year",1);
                                arr.push(data.getFullYear()+ "年")
                            }
                            return arr
                        },
                        selectValue : function(){
                            var arr = [];
                            var data = new Date();
                            data.decrement("year",5);
                            for( var i=0; i<11; i++ ){
                                data.increment("year",1);
                                arr.push(data.getFullYear().toString())
                            }
                            return arr
                        },
                        defaultValue : (new Date()).getFullYear().toString()
                    },
                    month: { type : "select",  text : "月份",
                        selectText : ["全年","1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"] ,
                        selectValue : ["","01","02","03","04","05","06","07","08","09","10","11","12"]
                    },
                    unitList : { type : "checkbox", text : "部门" },
                    ok : { type : "button", text : "查询", value : "查询", event : {
                        click : function(){ this.ok()}.bind(this)
                    } },
                    wfProcessStatus: { type : "select", text : "审批状态",
                        selectText : ["全部","已完成","流转中"] ,
                        selectValue : ["","已完成","流转中"]
                    }
                }
            }, this.app);
            this.form.load();

            this.ok();
        }.bind(this), true);
    },
    ok: function(){
        var data = this.form.getResult(true,null,true,false,true);
        data.wfProcessStatus = data.wfProcessStatus ? [data.wfProcessStatus] : null;
        if( !this.app.exportAllFlag )data.unitList = this.app.unitWithExport;
        this.formTableArea.empty();
        this.app.restActions.listWorkInfoByYear(  data.year, data, function( json ){
            var table = new Element( "table", {
                "width":"96%",
                "border":"0",
                "cellpadding":"5",
                "cellspacing":"0",
                "styles" : this.form.css.formTable
            }).inject( this.formTableArea  );

            var tr = new Element("tr").inject( table );
            var td = new Element("th", {
                "styles": this.form.css.formTableTitleP14,
                "text" : "部门"
            }).inject( tr );
            var td = new Element("th", {
                "styles": this.form.css.formTableTitleP14,
                "text" : "月份"
            }).inject( tr );
            var td = new Element("th", {
                "styles": this.form.css.formTableTitleP14,
                "text" : "重点工作"
            }).inject( tr );

            json.data.each( function( data, i ){
                tr  = new Element("tr").inject( table );
                var count = 0;
                var firstTd = td = new Element("td", {
                    "styles": this.form.css.formTableValueP14,
                    "text" : data.unitName.split("@")[0] + "(" + data.workTotal + ")"
                }).inject( tr );
                td.setStyle("text-align","center");

                if( data.workMonths ){
                    var flag = false;
                    data.workMonths.each( function( monthData, j ){
                        if( monthData.workInfoList ){
                            //if( j != 0 )tr = new Element("tr").inject( table );
                            if( flag ){
                                tr = new Element("tr").inject( table );
                            }else{
                                flag = true;
                            }
                            var count2 = 0;
                            var secondTd = td = new Element("td", {
                                "styles": this.form.css.formTableValueP14,
                                "text" : parseInt( monthData.month ) + "月"
                            }).inject( tr );
                            td.setStyle("text-align","center");
                            monthData.workInfoList.each( function( workInfo, k ){
                                count = count + 1;
                                count2 = count2 + 1;
                                if( k != 0 )tr = new Element("tr").inject( table );
                                var td = new Element("td", {
                                    "styles": this.form.css.formTableValueP14,
                                    "text" : workInfo.workName || "未设置"
                                }).inject( tr );
                            }.bind(this));
                            secondTd.set("rowspan",count2);
                        }
                    }.bind(this))
                }
                firstTd.set("rowspan",count);
            }.bind(this))
        }.bind(this))

    },
    setFormNodeSize: function (width, height, top, left) {
        if (!width)width = this.options.width ? this.options.width : "50%";
        if (!height)height = this.options.height ? this.options.height : "50%";
        if (!top) top = this.options.top ? this.options.top : 0;
        if (!left) left = this.options.left ? this.options.left : 0;

        var containerSize = this.container.getSize();
        if( containerSize.x < width )width = containerSize.x;
        if( containerSize.y < height )height = containerSize.y;

        var allSize = this.app.content.getSize();
        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
        300 > width && (width = 300);
        220 > height && (height = 220);

        top = top || parseInt((limitHeight - height) / 2, 10); //+appTitleSize.y);
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
        var tabSize = this.tableContainer ? this.tableContainer.getSize() : {x: 0, y: 0};

        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y - tabSize.y - 30;
        //var formMargin = formHeight -iconSize.y;
        this.formContentNode.setStyles({
            "height": "" + contentHeight + "px"
        });
        this.formTableContainer.setStyles({
            "height": "" + contentHeight + "px"
        });
    }
});