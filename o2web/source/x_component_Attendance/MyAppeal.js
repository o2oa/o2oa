MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xApplication.Attendance.MyAppeal = new Class({
    Extends: MWF.xApplication.Attendance.Explorer,
    Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "../x_component_Attendance/$MyAppeal/";
        this.cssPath = "../x_component_Attendance/$MyAppeal/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    load: function(){
        this.loadToolbar();
        this.loadFilter();
        this.loadContentNode();

        var month = (new Date().getMonth()+1).toString();
        if( month.length == 1 )month = "0"+month;
        var filterData = {
            "status" : "999",
            "yearString" : new Date().getFullYear().toString(),
            "monthString" : month
        };
        this.loadView( filterData );
        this.setNodeScroll();

    },
    loadFilter: function(){
        var lp = MWF.xApplication.Attendance.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles" : this.css.fileterNode
        }).inject(this.node);

        var html = "<table bordr='0' cellpadding='5' cellspacing='0' style='font-size: 14px;color:#666'>"+
            "<tr>" +
            "    <td styles='filterTableTitle' lable='yearString'></td>"+
            "    <td styles='filterTableValue' item='yearString'></td>" +
            "    <td styles='filterTableTitle' lable='monthString'></td>"+
            "    <td styles='filterTableValue' item='monthString'></td>" +
            "    <td styles='filterTableTitle' lable='status'></td>"+
            "    <td styles='filterTableValue' item='status'></td>" +
            "    <td styles='filterTableTitle' lable='appealReason'></td>"+
            "    <td styles='filterTableValue' item='appealReason'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.fileterNode, {}, {
                isEdited : true,
                itemTemplate : {
                    yearString : {
                        text : lp.annuaal,
                        "type" : "select",
                        "selectValue" : function(){
                            var years = [];
                            var year = new Date().getFullYear();
                            for(var i=0; i<6; i++ ){
                                years.push( year-- );
                            }
                            return years;
                        },
                        "event" : {
                            "change" : function( item, ev ){
                                var values = this.getDateSelectValue();
                                item.form.getItem( "date").resetItemOptions( values , values )
                            }.bind(this)
                        }
                    },
                    monthString : {
                        text : lp.months,
                        "type" : "select",
                        "defaultValue" : function(){
                            var month = (new Date().getMonth() + 1 ).toString();
                            return  month.length == 1 ? "0"+month : month;
                        },
                        "selectValue" :["","01","02","03","04","05","06","07","08","09","10","11","12"],
                        "event" : {
                            "change" : function( item, ev ){
                                var values = this.getDateSelectValue();
                                item.form.getItem( "date").resetItemOptions( values , values )
                            }.bind(this)
                        }
                    },
                    status : {
                        "text" : lp.auditStatus,
                        "type" : "select",
                        "value" : "999",
                        "selectText" : lp.auditStatusSelectText,
                        "selectValue" :["999","0","1","-1"]
                    },
                    appealReason : {
                        "text" : lp.appealReason,
                        "type" : "select",
                        "selectText" : lp.appealReasonSelectText
                    },
                    action : { "value" : lp.search, type : "button", className : "filterButton", event : {
                            click : function(){
                                var result = this.form.getResult(true,",",true,true,false);
                                if( !result )return;
                                this.loadView( result );
                            }.bind(this)
                        }}
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);
    },
    //loadFilter : function(){
    //    this.fileterNode = new Element("div.fileterNode", {
    //        "styles" : this.css.fileterNode
    //    }).inject(this.node)
    //
    //    var table = new Element("table", {
    //        "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.filterTable, "class" : "filterTable"
    //    }).inject( this.fileterNode );
    //
    //    var tr = new Element("tr").inject(table);
    //
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : (new Date).format(this.app.lp.dateFormatMonth)  }).inject(tr);
    //
    //    this.createStatusSelectTd(tr);
    //    this.createAppealReasonTd(tr);
    //    //this.createUnitTd(tr);
    //    //this.createPersonTd( tr );
    //    //this.createYearSelectTd( tr );
    //    //this.createMonthSelectTd( tr );
    //    this.createActionTd( tr );
    //},
    //createStatusSelectTd : function( tr ){
    //    var _self = this;
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "审批状态"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.status = new MDomItem( td, {
    //        "name" : "status",
    //        "type" : "select",
    //        "value" : "999",
    //        "selectText" :["所有状态","待处理","审批通过","审批未通过"],
    //        "selectValue" :["999","0","1","-1"]
    //    }, true, this.app );
    //    this.status.load();
    //},
    //createAppealReasonTd : function( tr ){
    //    var _self = this;
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "申诉原因"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.appealReason = new MDomItem( td, {
    //        "name" : "appealReason",
    //        "type" : "select",
    //        "selectText" :["","临时请假","出差","因公外出","其他"]
    //    }, true, this.app );
    //    this.appealReason.load();
    //},
    //createUnitTd : function(tr){
    //    var _self = this;
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "部门"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.unitName = new MDomItem( td, {
    //        "name" : "unitName",
    //        "style" : {"width":"60px"},
    //        "defaultValue" : this.app.manageUnits.length > 0 ? this.app.manageUnits[0] : "",
    //        "event" : {
    //            "click" : function(mdi){ _self.selecePerson( mdi, "unit" ); }
    //        }
    //    }, true, this.app );
    //    this.unitName.load();
    //},
    //createPersonTd : function(tr){
    //    var _self = this;
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "人员"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.empName = new MDomItem( td, {
    //        "name" : "empName",
    //        "style" : {"width":"60px"},
    //        "event" : {
    //            "click" : function(mdi){ _self.selecePerson( mdi, "person" ); }
    //        }
    //    }, true, this.app );
    //    this.empName.load();
    //},
    //createYearSelectTd : function( tr ){
    //    var _self = this;
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "年度"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.yearString = new MDomItem( td, {
    //        "name" : "yearString",
    //        "type" : "select",
    //        "selectValue" : function(){
    //            var years = [];
    //            var year = new Date().getFullYear();
    //            for(var i=0; i<6; i++ ){
    //                years.push( year-- );
    //            }
    //            return years;
    //        }
    //    }, true, this.app );
    //    this.yearString.load();
    //},
    //createMonthSelectTd : function( tr ){
    //    var _self = this;
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "月份"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.monthString = new MDomItem( td, {
    //        "name" : "monthString",
    //        "type" : "select",
    //        "selectValue" :["","01","02","03","04","05","06","07","08","09","10","11","12"]
    //    }, true, this.app );
    //    this.monthString.load();
    //},
    //createActionTd : function( tr ){
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    var input = new Element("button",{
    //        "text" : "查询",
    //        "styles" : this.css.filterButton
    //    }).inject(td);
    //    input.addEvent("click", function(){
    //        var filterData = {
    //            status : this.status.getValue(),
    //            appealReason : this.appealReason.getValue(),
    //            //unitName : this.unitName.getValue(),
    //            //empName : this.empName.getValue(),
    //            //yearString : this.yearString.getValue(),
    //           //monthString : this.monthString.getValue()
    //        }
    //        this.loadView( filterData );
    //    }.bind(this))
    //},
    //selecePerson: function( el, type ){
    //    var text = "选择人员"
    //    if( type=="topUnit") {
    //        text = "选择公司"
    //    }else if( type=="unit"){
    //        text = "选择部门"
    //    }
    //    var options = {
    //        "type": type, //topUnit unit person,
    //        "title": text,
    //        "count" : "1",
    //        "values": [ el.get("value") ] || [],
    //        "onComplete": function(items){
    //            var  arr = [];
    //            items.each(function(item){
    //                arr.push(item.data.name);
    //            }.bind(this));
    //            el.set("value",arr.join(","));
    //        }.bind(this)
    //    };
    //    var selector = new MWF.O2Selector(this.app.content, options);
    //},
    setContentSize: function(){
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : {"x":0,"y":0};
        var titlebarSize = this.app.titleBar ? this.app.titleBar.getSize() : {"x":0,"y":0};
        var filterSize = this.fileterNode ? this.fileterNode.getSize() : {"x":0,"y":0};
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();
        //var filterSize = this.filterNode.getSize();
        var filterConditionSize = this.filterConditionNode ? this.filterConditionNode.getSize() : {"x":0,"y":0};

        var height = nodeSize.y-toolbarSize.y-pt-pb-filterConditionSize.y-titlebarSize.y-filterSize.y;
        this.elementContentNode.setStyle("height", ""+height+"px");

        this.pageCount = (height/30).toInt()+5;

        if (this.view && this.view.items.length<this.pageCount){
            this.view.loadElementList(this.pageCount-this.view.items.length);
        }
    },
    loadView : function( filterData ){
        this.elementContentNode.empty();
        this.view = new MWF.xApplication.Attendance.MyAppeal.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        this.view.filterData = filterData;
        this.view.load();
        this.setContentSize();
    },
    createDocument: function(){
        if(this.view)this.view._createDocument();
    }
});

MWF.xApplication.Attendance.MyAppeal.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.MyAppeal.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if(!count )count=20;
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        var filter = this.filterData || {};

        /*var month = (new Date().getMonth()+1).toString();
        if( month.length == 1 )month = "0"+month;
        filter.yearString = new Date().getFullYear().toString();
        filter.monthString = month;*/
        filter.empName = layout.desktop.session.user.distinguishedName;

        this.actions.listAppealFilterNext(id, count, filter, function(json){
            var data = json.data;
            data.sort( function( a, b ){
                return parseInt( b.appealDateString.replace(/-/g,"") ) -  parseInt( a.appealDateString.replace(/-/g,"") );
            });
            json.data = data;
            if (callback) callback(json);
        });
    },
    _removeDocument: function(documentData, all){

    },
    _createDocument: function(){

    },
    _openDocument: function( documentData ){

        if(documentData.appealAuditInfo){
            if(documentData.appealAuditInfo.workId){
                var workid = documentData.appealAuditInfo.workId;
                var options = {"workId":workid, "appId": "process.Work"+workid};
                this.app.desktop.openApplication(null, "process.Work", options);
                return;
            }
        }
        var appeal = new MWF.xApplication.Attendance.MyAppeal.Appeal(this.explorer, documentData );
        appeal.open();

    }

});

MWF.xApplication.Attendance.MyAppeal.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document,
    agree : function(){

    },
    deny : function(){

    }

});


MWF.xApplication.Attendance.MyAppeal.Appeal = new Class({
    Extends: MWF.widget.Common,
    initialize: function( explorer, data ){
        this.explorer = explorer;
        this.app = explorer.app;
        this.data = data || {};
        //this.app.restActions.getAppeal(this.data.detailId, function(json){
        //    this.data = json.data
        //}.bind(this),null,false)
        //alert(JSON.stringify(this.data))
        this.css = this.explorer.css;

        this.load();
    },
    load: function(){
        this.app.restActions.getDetail(this.data.detailId, function(json){
            this.data.onDutyTime = json.data.onDutyTime;
            this.data.offDutyTime = json.data.offDutyTime;
        }.bind(this),null,false)
    },

    open: function(e){
        this.isNew = false;
        this.isEdited = false;
        this._open();
    },
    create: function(){
        this.isNew = true;
        this._open();
    },
    edit: function(){
        this.isEdited = true;
        this._open();
    },
    _open : function(){
        this.createMarkNode = new Element("div", {
            "styles": this.css.createMarkNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.app.content, "after");

        this.createAreaNode = new Element("div", {
            "styles": this.css.createAreaNode
        });

        this.createNode();

        this.createAreaNode.inject(this.createMarkNode, "after");
        this.createAreaNode.fade("in");

        this.setCreateNodeSize();
        this.setCreateNodeSizeFun = this.setCreateNodeSize.bind(this);
        this.addEvent("resize", this.setCreateNodeSizeFun);
    },
    createNode: function(){
        var _self = this;
        var lp = MWF.xApplication.Attendance.LP;

        this.createNode = new Element("div", {
            "styles": this.css.createNode
        }).inject(this.createAreaNode);

        //
        //this.createIconNode = new Element("div", {
        //    "styles": this.isNew ? this.css.createNewNode : this.css.createIconNode
        //}).inject(this.createNode);

        this.createContainerNode = new Element("div", {
            "styles": this.css.createContainerNode
        }).inject(this.createNode);


        this.setScrollBar( this.createContainerNode );


        this.createFormNode = new Element("div", {
            "styles": this.css.createFormNode
        }).inject(this.createContainerNode);

        this.createTableContainer = new Element("div", {
            "styles": this.css.createTableContainer
        }).inject(this.createFormNode);

        this.createTableArea = new Element("div", {
            "styles": this.css.createTableArea
        }).inject(this.createTableContainer);


        var d = this.data;
        var appealStatus = lp.draft;
        if (d.status == 0 ) {
            appealStatus = lp.todo;
        } else if (d.status == 1) {
            appealStatus = lp.approve
        } else if (d.status == -1) {
            appealStatus = lp.deny
        }
        this.data.appealStatusShow = appealStatus;
debugger
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td colspan='4' styles='formTableHead'>"+lp.apealApplyForm+"</td></tr>" +
            "<tr><td styles='formTableTitle'>"+lp.employeeName+"</td>"+
            "    <td styles='formTableValue'>"+this.data.empName.split("@")[0]+"</td>" +
            "    <td styles='formTableTitle' lable='recordDateString'></td>"+
            "    <td styles='formTableValue' item='recordDateString'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='onDutyTime'></td>"+
            "    <td styles='formTableValue' item='onDutyTime'></td>" +
            "    <td styles='formTableTitle' lable='offDutyTime'></td>"+
            "    <td styles='formTableValue' item='offDutyTime'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='appealStatusShow'></td>"+
            "    <td styles='formTableValue' item='appealStatusShow'  colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='appealReason'></td>"+
            "    <td styles='formTableValue' item='appealReason'></td>" +
            "   <td styles='formTableTitle' lable='processPerson1Show'></td>"+
            "    <td styles='formTableValue' item='processPerson1Show'></td></tr>" +
            "<tr contain='selfHolidayType'><td styles='formTableTitle' lable='selfHolidayType'></td>"+
            "    <td styles='formTableValue' item='selfHolidayType' colspan='3'></td></tr>" +
            "<tr contain='address'><td styles='formTableTitle' lable='address'></td>"+
            "    <td styles='formTableValue' item='address' colspan='3'></td></tr>" +
            "<tr contain='startTime'><td styles='formTableTitle' lable='startTime'></td>"+
            "    <td styles='formTableValue' item='startTime' colspan='3'></td></tr>" +
            "<tr contain='endTime'><td styles='formTableTitle' lable='endTime'></td>"+
            "    <td styles='formTableValue' item='endTime' colspan='3'></td></tr>" +
            "<tr contain='appealDescription'><td styles='formTableTitle' lable='appealDescription'></td>"+
            "    <td styles='formTableValue' item='appealDescription' colspan='3'></td></tr>" +
            /*"<tr contain='opinion1'><td styles='formTableTitle' lable='opinion1'></td>"+
            "    <td styles='formTableValue' item='opinion1' colspan='3'></td></tr>" +*/
            "</table>";
        this.createTableArea.set("html",html);

        this.document = new MForm( this.createTableArea, this.data, {
            style : "popup",
            isEdited : this.isEdited || this.isNew,
            itemTemplate : {
                recordDateString : { text:lp.recordDate,  type : "innertext"},
                onDutyTime : { text:lp.onDutyTime,  type : "innertext"},
                offDutyTime : { text:lp.offDutyTime,  type : "innertext"},
                statusShow : {  text:lp.attendanceStatus, type : "innertext" },
                appealStatusShow : { text:lp.appealStatus, type : "innertext"},
                processPerson1Show : {text:lp.auditor, type:"innertext", value : this.data.appealAuditInfo?this.data.appealAuditInfo.currentProcessor.split("@")[0] :""},
                appealReason : {
                    notEmpty : true,
                    text: lp.appealReason,
                    type : "select",
                    selectValue : lp.appealReasonSelectText,
                    event : { change : function(mdi){
                            _self.switchFieldByAppealReason(mdi.getValue());
                        }}
                },
                address : { text: lp.address },
                selfHolidayType : {
                    text: lp.leaveType,
                    type : "select",
                    selectValue : lp.leaveTypeSelectText
                },
                startTime : {  text: lp.startTime, tType : "datetime" },
                endTime : {  text:lp.endTime, tType : "datetime" },
                appealDescription : { text: lp.appealDescriptoin }
                //opinion1 : { text :"审批意见" }
            }
        }, this.app,this.css);
        this.document.load();
        _self.switchFieldByAppealReason(this.data.appealReason);


        //createFormNode.set("html", html);

        //this.setScrollBar(this.createTableContainer)


        this.cancelActionNode = new Element("div", {
            "styles": this.css.createCancelActionNode,
            "text": lp.close
        }).inject(this.createFormNode);


        this.cancelActionNode.addEvent("click", function(e){
            this.cancelCreate(e);
        }.bind(this));

        if( this.isNew || this.isEdited){
            this.denyActionNode = new Element("div", {
                "styles": this.css.createDenyActionNode,
                "text": lp.disagree
            }).inject(this.createFormNode);
            this.createOkActionNode = new Element("div", {
                "styles": this.css.createOkActionNode,
                "text": lp.agree
            }).inject(this.createFormNode);

            this.denyActionNode.addEvent("click", function(e){
                this.deny(e);
            }.bind(this));
            this.createOkActionNode.addEvent("click", function(e){
                this.okCreate(e);
            }.bind(this));
        }

    },
    switchFieldByAppealReason : function( ar ){
        var lp = MWF.xApplication.Attendance.LP;
        var tempField = ["selfHolidayType","startTime","endTime","address","appealDescription"];
        var showField = [];
        if( ar == lp.temporaryLeave ){
            showField = ["selfHolidayType","startTime","endTime"];
        }else if( ar == lp.out ){
            showField = ["address","startTime","endTime"];
        }else if( ar == lp.businessTrip ){
            showField = ["address","startTime","endTime","appealDescription"];
        }else if( ar == lp.other ){
            showField = ["appealDescription"];
        }
        tempField.each( function( f ){
            this.createTableArea.getElement("[contain='"+f+"']").setStyle("display", showField.contains(f) ? "" : "none" );
            if( this.isNew || this.isEdited )this.document.items[f].options.notEmpty = (showField.contains(f) ? true : false )
        }.bind(this))
    },
    setCreateNodeSize: function(){
        var size = this.app.node.getSize();
        var allSize = this.app.content.getSize();

        var height = "560";
        var width = "800";

        this.createAreaNode.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px"
        });
        var hY = height;
        var mY = (size.y-height)/2;
        this.createNode.setStyles({
            "height": ""+hY+"px",
            "margin-top": ""+mY+"px",
            "width" : ""+width+"px"
        });

        this.createContainerNode.setStyles({
            "height": ""+hY+"px"
        });

        var iconSize = this.createIconNode ? this.createIconNode.getSize() : {x:0,y:0};
        var formMargin = hY-iconSize.y-60;
        this.createFormNode.setStyles({
            "height": ""+formMargin+"px",
            "margin-top": ""+60+"px"
        });
    },
    cancelCreate: function(e){
        this.createMarkNode.destroy();
        this.createAreaNode.destroy();
        delete this;
    },
    deny : function(e){
        var data = { 'ids' : [this.data.id], 'status':'-1', 'opinion1': this.opinion1.getValue() };
        if (data.opinion1 ){
            this.process( data );
        }else{
            this.app.notice( MWF.xApplication.Attendance.LP.inputIdeaNotice, "error");
        }
    },
    okCreate: function(e){
        var data = { 'ids' : [this.data.id], 'status':'1', 'opinion1': this.opinion1.getValue() };
        this.process( data );
    },
    process: function( data ){
        this.app.restActions.processAppeal( data, function(json){
            if( json.type == "ERROR" ){
                this.app.notice( json.message  , "error");
            }else{
                this.createMarkNode.destroy();
                this.createAreaNode.destroy();
                if(this.explorer.view)this.explorer.view.reload();
                this.app.notice( MWF.xApplication.Attendance.LP.processSuccess, "success");
            }
            //    this.app.processConfig();
        }.bind(this));
    }
});

