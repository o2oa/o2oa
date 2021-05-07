MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xApplication.Attendance.AppealExplorer = new Class({
    Extends: MWF.xApplication.Attendance.Explorer,
    Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "../x_component_Attendance/$AppealExplorer/";
        this.cssPath = "../x_component_Attendance/$AppealExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.preMonthDate = new Date();

        this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    load: function(){
        this.loadConfig();
        this.loadToolbar();
        this.loadFilter();
        this.loadContentNode();

        var month = (this.preMonthDate.getMonth()+1).toString();
        if( month.length == 1 )month = "0"+month;
        var filterData = {
            "status" : "0",
            "yearString" : this.preMonthDate.getFullYear().toString(),
            "monthString" : month
        };
        this.loadView( filterData );
        this.setNodeScroll();

    },
    loadConfig : function(){
        /*this.config = {};
        var v;
        this.actions.getSettingCode( "APPEALABLE", function(json){
            v =  json.data ? json.data.configValue : null
        },null, false)
        if( !v ){
            this.config.APPEALABLE = true;
        }else{
            this.config.APPEALABLE = (v != "false" )
        }*/
        this.config = {};
        var v;
        //需要判断申述类型listSetting2020年6月16日 by gee
        this.configSetting = new Object(null);
        this.actions.listSetting(function(json){
            var data = json.data;
            if(!!data){
                json.data.map(function(e){
                    this.configSetting[e.configCode]=e;
                }.bind(this));
                v = this.configSetting.APPEALABLE.configValue;
            }else{
                v = null;
            }
        }.bind(this),null,false);
        if( !v ){
            this.config.APPEALABLE = true;
        }else{
            this.config.APPEALABLE = (v != "false" )
        }
    },
    loadToolbar: function(){
        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode});
        this.toolbarNode.inject(this.node);

            var toolbarUrl = this.path + "toolbar.json";
            MWF.getJSON(toolbarUrl, function (json) {
                json.each(function (tool) {
                   if( !this.config.APPEALABLE && tool.condition=="onlock" ){
                       this.createToolbarItemNode(tool)
                    }else if( this.config.APPEALABLE && tool.condition!="onlock" ){
                       this.createToolbarItemNode(tool)
                   }
                }.bind(this));
            }.bind(this));
    },
    loadFilter : function(){
        this.fileterNode = new Element("div.fileterNode", {
            "styles" : this.css.fileterNode
        }).inject(this.node);

        var table = new Element("table", {
            "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.filterTable, "class" : "filterTable"
        }).inject( this.fileterNode );
        var tr = new Element("tr").inject(table);

        //var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : this.preMonthDate.format(this.app.lp.dateFormatMonth)  }).inject(tr);

        this.createYearSelectTd( tr );
        this.createMonthSelectTd( tr );
        this.createStatusSelectTd(tr);
        this.createAppealReasonTd(tr);
        this.createUnitTd(tr);
        this.createPersonTd( tr );
        this.createActionTd( tr );
    },
    createStatusSelectTd : function( tr ){
        var _self = this;
        var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : this.app.lp.auditStatus  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        this.status = new MDomItem( td, {
            "name" : "status",
            "type" : "select",
            "value" : "0",
            "selectText" : this.app.lp.auditStatusSelectText,
            "selectValue" :["999","0","1","-1"]
        }, true, this.app );
        this.status.load();
    },
    createAppealReasonTd : function( tr ){
        var _self = this;
        var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : this.app.lp.appealReason  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        this.appealReason = new MDomItem( td, {
            "name" : "appealReason",
            "type" : "select",
            "selectText" : this.app.lp.appealReasonSelectText
        }, true, this.app );
        this.appealReason.load();
    },
    createUnitTd : function(tr){
        var _self = this;
        var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : this.app.lp.department  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        this.unitName = new MDomItem( td, {
            "name" : "unitName",
            "style" : {"width":"60px"},
            "defaultValue" : this.app.manageUnits.length > 0 ? this.app.manageUnits[0] : "",
            "event" : {
                "click" : function(mdi){ _self.selecePerson( mdi, "unit" ); }
            }
        }, true, this.app );
        this.unitName.load();
    },
    createPersonTd : function(tr){
        var _self = this;
        var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : this.app.lp.person }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        this.empName = new MDomItem( td, {
            "name" : "empName",
            "style" : {"width":"60px"},
            "event" : {
                "click" : function(mdi){ _self.selecePerson( mdi, "person" ); }
            }
        }, true, this.app );
        this.empName.load();
    },
    createYearSelectTd : function( tr ){
        var _self = this;
        var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : this.app.lp.annuaal  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        this.yearString = new MDomItem( td, {
            "name" : "yearString",
            "type" : "select",
            "selectValue" : function(){
                var years = [];
                var year = new Date().getFullYear();
                for(var i=0; i<6; i++ ){
                    years.push( year-- );
                }
                return years;
            }
        }, true, this.app );
        this.yearString.load();
    },
    createMonthSelectTd : function( tr ){
        var _self = this;
        var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : this.app.lp.months  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        this.monthString = new MDomItem( td, {
            "name" : "monthString",
            "type" : "select",
            "selectValue" :["","01","02","03","04","05","06","07","08","09","10","11","12"]
        }, true, this.app );
        this.monthString.load();
    },
    createActionTd : function( tr ){
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        var input = new Element("button",{
            "text" : this.app.lp.search,
            "styles" : this.css.filterButton
        }).inject(td);
        input.addEvent("click", function(){
            /*var year = this.preMonthDate.getFullYear().toString();
            var month = (this.preMonthDate.getMonth()+1).toString();
            if( month.length == 1 )month = "0"+month;*/
            var filterData = {
                status : this.status.getValue(),
                appealReason : this.appealReason.getValue(),
                unitName : this.unitName.getValue(),
                empName : this.empName.getValue(),
                yearString : this.yearString.getValue(),
                monthString : this.monthString.getValue()
            }
            this.loadView( filterData );
        }.bind(this))
    },
    selecePerson: function( el, type ){
        var text = this.app.lp.selecePerson;
        if( type=="topUnit") {
            text = this.app.lp.selectCompany;
        }else if( type=="unit"){
            text = this.app.lp.selectDepartment;
        }
        var options = {
            "type": type, //topUnit unit person,
            "title": text,
            "count" : "1",
            "values": [ el.get("value") ] || [],
            "onComplete": function(items){
                var  arr = [];
                items.each(function(item){
                    arr.push(item.data.name);
                }.bind(this));
                el.set("value",arr.join(","));
            }.bind(this)
        };
        var selector = new MWF.O2Selector(this.app.content, options);
    },
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
        this.view = new MWF.xApplication.Attendance.AppealExplorer.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        this.view.filterData = filterData;
        this.view.load();
        this.setContentSize();
    },
    createDocument: function(){
        if(this.view)this.view._createDocument();
    },
    agreeAppeals: function( e ){
        var _self = this;
        var count = 0;
        this.view.items.each( function( it ){
            if( it.checkboxElement && it.checkboxElement.get("checked" ) )count++;
        }.bind(this))
        if( count == 0 ){
            this.app.notice(this.app.lp.selectAppealNotice,"error");
            return;
        }
        this.app.confirm("warn", e, this.app.lp.agreeAppealConfirmTitle, this.app.lp.agreeAppealConfirmContent.replace("{count}", count), 350, 120, function(){
            _self.batchAppeals = true;
            _self.view.items.each( function( it ){
                if( it.checkboxElement && it.checkboxElement.get("checked" ) )it.agree( true );
            }.bind(this));
            if(_self.view)_self.view.reload();
            _self.batchAppeals = false;
            _self.app.notice( this.app.lp.actionSuccess, "success");
            this.close();
        }, function(){
            this.close();
        });
    },
    denyAppeals: function( e ){
        var _self = this;
        var count = 0;
        this.view.items.each( function( it ){
            if( it.checkboxElement && it.checkboxElement.get("checked" ) )count++;
        }.bind(this));
        if( count == 0 ){
            this.app.notice(this.app.lp.selectAppealNotice,"error");
            return;
        }
        this.app.confirm("warn", e, this.app.lp.disagreeAppealConfirmTitle, this.app.lp.disagreeAppealConfirmContent.replace("{count}", count), 350, 120, function(){
            _self.batchAppeals = true;
            _self.view.items.each( function( it ){
                if( it.checkboxElement && it.checkboxElement.get("checked" ) )it.deny( true );
            }.bind(this));
            if(_self.view)_self.view.reload();
            _self.batchAppeals = false;
            _self.app.notice( this.app.lp.actionSuccess , "success");
            this.close();
        }, function(){
            this.close();
        });
    }
});

MWF.xApplication.Attendance.AppealExplorer.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.AppealExplorer.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if(!count )count=20;
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        var filter = this.filterData || {};
        filter.processPerson1 = layout.desktop.session.user.distinguishedName;
        this.actions.listAppealFilterNext(id, count, filter, function(json){
            var data = json.data;
            data.each(function(d){
                d.APPEALABLE = this.explorer.config.APPEALABLE;
                d.APPEAL_AUDIFLOWTYPE = this.explorer.configSetting.APPEAL_AUDIFLOWTYPE.configValue;
            }.bind(this));
            data.sort( function( a, b ){
                return parseInt( b.appealDateString.replace(/-/g,"") ) -  parseInt( a.appealDateString.replace(/-/g,"") );
            })
            json.data = data;
            if (callback) callback(json);
        }.bind(this));
    },
    _removeDocument: function(documentData, all){

    },
    _createDocument: function(){

    },
    _openDocument: function( documentData ){

        /*if(!!documentData.appealAuditInfo){
            var workid = documentData.appealAuditInfo.workId;
            var options = {"workId":workid, "appId": "process.Work"+workid};
            this.app.desktop.openApplication(null, "process.Work", options);
        }else{
            var appeal = new MWF.xApplication.Attendance.AppealExplorer.Appeal(this.explorer, documentData );
            if( !documentData.status ){
                appeal.edit();
            }else{
                appeal.open();
            }
        }*/
        if(documentData.appealAuditInfo){
            if(documentData.appealAuditInfo.workId){
                var workid = documentData.appealAuditInfo.workId;
                var options = {"workId":workid, "appId": "process.Work"+workid};
                this.app.desktop.openApplication(null, "process.Work", options);
                return;
            }
        }
        var appeal = new MWF.xApplication.Attendance.AppealExplorer.Appeal(this.explorer, documentData );
        appeal.open();

    }

})

MWF.xApplication.Attendance.AppealExplorer.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document,
    agree : function(  ){
        var data = { 'ids' : [this.data.id], 'status':'1' };
        this.process( data );
    },
    deny : function( batch ){
        var data = { 'ids' : [this.data.id], 'status':'-1' };
        this.process( data );
    },
    process: function( data ){
        this.app.restActions.processAppeal( data, function(json){
            if( json.type == "ERROR" ){
                this.app.notice( json.message  , "error");
            }else{
                if( !this.explorer.batchAppeals ){
                    if(this.explorer.view)this.explorer.view.reload();
                    this.app.notice( this.app.lp.actionSuccess.replace("{count}", count) , "success");
                }
            }
        }.bind(this), null, false );
    }

})


MWF.xApplication.Attendance.AppealExplorer.Appeal = new Class({
    Extends: MWF.widget.Common,
    initialize: function( explorer, data ){
        this.explorer = explorer;
        this.app = explorer.app;
        this.data = data || {};
        this.css = this.explorer.css;
        //this.app.restActions.getAppeal(this.data.detailId, function(json){
        //    this.data = json.data
        //}.bind(this),null,false)
        //alert(JSON.stringify(this.data))
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
        if( this.explorer.config.APPEALABLE )this.isEdited = true;
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


        var table = new Element("table", {
            "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.editTable, "class" : "editTable"
        }).inject( this.createTableArea );


        var d = this.data;

        var appealStatus = this.app.lp.draft;
            if (d.status == 0 ) {
                appealStatus = this.app.lp.todo;
            } else if (d.status == 1) {
                appealStatus = this.app.lp.approve;
            } else if (d.status == -1) {
                appealStatus = this.app.lp.deny;
            }
        this.data.appealStatusShow = appealStatus;

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td colspan='4' styles='formTableHead'>"+this.app.lp.apealForm+"</td></tr>" +
            "<tr><td styles='formTableTitle' lable='empNameShow'></td>"+
            "    <td styles='formTableValue' item='empNameShow'></td>" +
            "    <td styles='formTableTitle' lable='recordDateString'></td>"+
            "    <td styles='formTableValue' item='recordDateString'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='onDutyTime'></td>"+
            "    <td styles='formTableValue' item='onDutyTime'></td>" +
            "    <td styles='formTableTitle' lable='offDutyTime'></td>"+
            "    <td styles='formTableValue' item='offDutyTime'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='appealStatusShow'></td>"+
            "    <td styles='formTableValue' item='appealStatusShow'  colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='appealReason'></td>"+
            "    <td styles='formTableValue' item='appealReason' colspan='3'></td></tr>" +
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
            "</table>"
        this.createTableArea.set("html",html);

        var lp = this.app.lp;
        this.document = new MForm( this.createTableArea, this.data, {
            style : "popup",
            isEdited : this.isEdited || this.isNew,
            itemTemplate : {
                empNameShow : { text: lp.employeeName, type : "innertext", value : this.data.empName.split("@")[0] },
                recordDateString : { text:lp.recordDate,  type : "innertext"},
                onDutyTime : { text: lp.onDutyTime ,  type : "innertext"},
                offDutyTime : { text: lp.offDutyTime,  type : "innertext"},
                statusShow : {  text:lp.attendanceStatus , type : "innertext" },
                appealStatusShow : { text:lp.appealStatus, type : "innertext"},
                appealReason : {
                    text:lp.appealReason,  type : "innertext"
                },
                address : { text: lp.address, type : "innertext" },
                selfHolidayType : {
                    text: lp.leaveType,
                    type : "innertext"
                },
                startTime : {  text:lp.startTime, type : "innertext" },
                endTime : {  text:lp.endTime, type : "innertext" },
                appealDescription : { text: lp.appealDescriptoin , type : "innertext" }
                //opinion1 : { text :"审批意见",type : "textarea" }
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

        if( this.isNew || this.isEdited ){
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
        var lp = this.app.lp;
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

        var height = "570";
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
        var data = { 'ids' : [this.data.id], 'status':'-1', 'opinion1': this.document.items.opinion1.getValue() };
        //if (data.opinion1 ){
            this.process( data );
        //}else{
        //    this.app.notice( "请填写意见", "error");
        //}
    },
    okCreate: function(e){
        var data = { 'ids' : [this.data.id], 'status':'1', 'opinion1': this.document.items.opinion1.getValue() };
        this.process( data );
    },
    process: function( data ){
        this.app.restActions.processAppeal( data, function(json){
            if( json.type == "ERROR" ){
                this.app.notice( json.message , "error");
            }else{
                this.createMarkNode.destroy();
                this.createAreaNode.destroy();
                if(this.explorer.view)this.explorer.view.reload();
                this.app.notice( this.app.lp.actionSuccess, "success");
            }
            //    this.app.processConfig();
        }.bind(this));
    }
})
