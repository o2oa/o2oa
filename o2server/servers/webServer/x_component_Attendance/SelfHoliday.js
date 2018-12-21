MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xApplication.Attendance.SelfHoliday = new Class({
	Extends: MWF.xApplication.Attendance.Explorer,
	Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Attendance/$SelfHoliday/";
        this.cssPath = "/x_component_Attendance/$SelfHoliday/"+this.options.style+"/css.wcss";
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

        this.loadView();
        this.setNodeScroll();

    },
    loadFilter : function(){
        this.fileterNode = new Element("div.fileterNode", {
            "styles" : this.css.fileterNode
        }).inject(this.node);

        this._loadFilterContent();
    },
    exportExcel : function(){
        var exportForm = new MWF.xApplication.Attendance.SelfHoliday.ExportExcelForm( this );
        exportForm.edit();
    },
    _loadFilterContent : function(){
        var _self = this;
        var html = "<table bordr='0' cellpadding='5' cellspacing='0' styles='formTable' width='950'>"+
            "<tr>" +
            "    <td styles='formTableTitle' lable='q_topUnitName'></td>"+
            "    <td styles='formTableValue' item='q_topUnitName'></td>"+
            "    <td styles='formTableTitle' lable='q_unitName'></td>"+
            "    <td styles='formTableValue' item='q_unitName'></td>"+
            "    <td styles='formTableTitle' lable='q_empName'></td>"+
            "    <td styles='formTableValue' item='q_empName'></td>"+
            "    <td styles='formTableValue' item='action'></td>"+
            "</tr>" +
            "</table>";
        this.fileterNode.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.filter = new MForm( this.fileterNode,  {}, {
                style : "filter",
                isEdited : true,
                itemTemplate : {
                    q_topUnitName : { "text" : "选择公司", "type" : "org", "orgType" : "unit", style : {"min-width" : "200px"} },
                    q_unitName : { "text" : "选择部门", "type" : "org", "orgType" : "unit", style : {"min-width" : "250px"} },
                    q_empName : {  "text" : "选择人员", "type" : "org", "orgType" : "person", style : {"min-width" : "100px"} },
                    action : {
                        "type" : "button",
                        "value" : "查询",
                        "event" : { "click" : function(){
                            var filterData =  _self.filter.getResult( true,",",true,true,true);
                            this.loadView( filterData );
                        }.bind(this)}
                    }
                }
            }, this.app, this.css);
            this.filter.load();
        }.bind(this), true);
    },
    loadView : function( filterData ){
        this.elementContentNode.empty();
        this.view = new MWF.xApplication.Attendance.SelfHoliday.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        this.view.filterData = filterData;
        this.view.load();
        this.setContentSize();
    },
    createDocument: function(){
        if(this.view)this.view._createDocument();
    }
});

MWF.xApplication.Attendance.SelfHoliday.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.SelfHoliday.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        //this.actions.listSelfHoliday(function(json){
        //    if (callback) callback(json);
        //});
        if(!count )count=20;
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        var filter = this.filterData || {};
        this.actions.listSelfHolidayFilterNext(id, count, filter, function(json){
            if (callback) callback(json);
        });
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteSelfHoliday(documentData.id, function(json){
            this.explorer.view.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _createDocument: function(){
        var selfHoliday = new MWF.xApplication.Attendance.SelfHoliday.Form(this.explorer);
        selfHoliday.create();
    },
    _openDocument: function( documentData ){
        var selfHoliday = new MWF.xApplication.Attendance.SelfHoliday.Form(this.explorer, documentData );
        selfHoliday.open();
    }

});

MWF.xApplication.Attendance.SelfHoliday.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});

MWF.xApplication.Attendance.SelfHoliday.ExportExcelForm = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.PopupForm,
    _createTableContent: function(){

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td colspan='2' styles='formTableHead'>导出员工休假记录</td></tr>" +
            "<tr>" +
            "    <td styles='formTableTitle' lable='startDate'></td>"+
            "    <td styles='formTableValue' item='startDate'></td>"+
            "</tr>" +
            "<tr>" +
            "    <td styles='formTableTitle' lable='endDate'></td>"+
            "    <td styles='formTableValue' item='endDate'></td>"+
            //"    <td styles='formTableValue' item='action'></td>"+
            "</tr>" +
            "</table>";
        this.formTableArea.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.formTableArea, {q_empName : layout.desktop.session.user.distinguishedName }, {
                isEdited : true,
                itemTemplate : {
                    startDate : {  "text" : "开始日期", "tType" : "date" },
                    endDate : { "text" : "结束日期", "tType" : "date" }
                }
            }, this.app );
            this.form.load();
        }.bind(this), true);

    },
    _ok: function( data, callback ){
        this.app.restActions.exportSelfHoliday( data.startDate, data.endDate, function(json){
            if( callback )callback(json);
        }.bind(this) );
        this.close();
    }
});

MWF.xApplication.Attendance.SelfHoliday.Form = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.PopupForm,
    options : {
        "width": 600,
        "height": 450,
        "hasTop" : true,
        "hasBottom" : true,
        "title" : "",
        "draggable" : true,
        "closeAction" : true
    },
    _createTableContent: function(){

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td colspan='2' styles='formTableHead'>员工休假记录</td></tr>" +
            "<tr><td styles='formTabelTitle' lable='unitName'></td>"+
            "    <td styles='formTableValue' item='unitName'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='employeeName'></td>"+
            "    <td styles='formTableValue' item='employeeName'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='leaveType'></td>"+
            "    <td styles='formTableValue' item='leaveType'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='startTime'></td>"+
            "    <td styles='formTableValue' item='startTime'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='endTime'></td>"+
            "    <td styles='formTableValue' item='endTime'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='leaveDayNumber'></td>"+
            "    <td styles='formTableValue' item='leaveDayNumber'></td></tr>" +
            "</table>";
        this.formTableArea.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.formTableArea, this.data, {
                isEdited : this.isEdited || this.isNew,
                itemTemplate : {
                    unitName : { text : "部门", "type" : "org", orgType : "unit", notEmpty : true},
                    employeeName : { text: "员工姓名",  "type" : "org", orgType : "person" , notEmpty : true },
                    leaveType : {
                        text: "休假类型",
                        "type" : "select", notEmpty : true,
                        "selectValue" : "带薪年休假,带薪病假,带薪福利假,扣薪事假,其他".split(",")
                    },
                    startTime : {
                        text:"开始时间", tType : "datetime", notEmpty : true
                    },
                    endTime : {
                        text:"结束时间", tType : "datetime", notEmpty : true
                    },
                    leaveDayNumber : {
                        text:"休假天数", tType : "number", notEmpty : true
                    }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _ok: function( data, callback ){
        this.app.restActions.saveSelfHoliday(data, function(json){
             if( callback )callback(json);
        }.bind(this));
    }
});
//
//MWF.xApplication.Attendance.SelfHoliday.Form = new Class({
//    Extends: MWF.widget.Common,
//    Implements: [Options, Events],
//    options: {
//        "width": "500",
//        "height": "400"
//    },
//    initialize: function( explorer, data ){
//        this.explorer = explorer;
//        this.app = explorer.app;
//        this.data = data || {};
//        this.css = this.explorer.css;
//
//        this.load();
//    },
//    load: function(){
//
//    },
//
//    open: function(){
//        this.isNew = false;
//        this.isEdited = false;
//        this._open();
//    },
//    create: function(){
//        this.isNew = true;
//        this._open();
//    },
//    edit: function(){
//        this.isEdited = true;
//        this._open();
//    },
//    _open : function(){
//        this.createMarkNode = new Element("div", {
//            "styles": this.css.createMarkNode,
//            "events": {
//                "mouseover": function(e){e.stopPropagation();},
//                "mouseout": function(e){e.stopPropagation();}
//            }
//        }).inject(this.app.content, "after");
//
//        this.createAreaNode = new Element("div", {
//            "styles": this.css.createAreaNode
//        });
//
//        this.createNode();
//
//        this.createAreaNode.inject(this.createMarkNode, "after");
//        this.createAreaNode.fade("in");
//
//
//        this.setCreateNodeSize();
//        this.setCreateNodeSizeFun = this.setCreateNodeSize.bind(this);
//        this.addEvent("resize", this.setCreateNodeSizeFun);
//    },
//    createNode: function(){
//        var _self = this;
//        var d = this.data;
//        this.createNode = new Element("div", {
//            "styles": this.css.createNode
//        }).inject(this.createAreaNode);
//
//
//        this.createIconNode = new Element("div", {
//            "styles": this.isNew ? this.css.createNewNode : this.css.createIconNode
//        }).inject(this.createNode);
//
//
//        this.createFormNode = new Element("div", {
//            "styles": this.css.createFormNode
//        }).inject(this.createNode);
//
//
//        var table = new Element("table", {
//            "width" : "100%", "border" : "0", "cellpadding" : "0", "cellspacing" : "0"
//        }).inject( this.createFormNode );
//
//        var tr = new Element("tr").inject(table);
//        var td = new Element("td", { "colspan":'2', "styles" : this.css.editTableHead, "text" : "员工休假记录"  }).inject(tr);
//
//        var tr = new Element("tr").inject(table);
//        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "部门："  }).inject(tr);
//        var td = new Element("td", { "styles" : this.css.editTableValue }).inject(tr);
//        if( !this.isNew && !this.isEdited  ){
//            td.set("text",  d.unitName  )
//        }else{
//           this.unitName = new MDomItem( td, {
//                "name" : "unitName",
//                "value" : d.unitName,
//                "style" : this.css.inputPersonStyle,
//               "event" : {
//                   "click" : function( mdi){ _self.selectPeople(this, "unit", mdi.get("value").split(",") ) }
//               }
//            }, true, this.app );
//            this.unitName.load();
//        }
//
//        var tr = new Element("tr").inject(table);
//        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "员工姓名："  }).inject(tr);
//        var td = new Element("td", { "styles" : this.css.editTableValue  }).inject(tr);
//        if( !this.isNew && !this.isEdited  ){
//            td.set("text",  d.employeeName  )
//        }else{
//            this.employeeName = new MDomItem( td, {
//                "name" : "employeeName",
//                "value" : d.employeeName,
//                "style" : this.css.inputPersonStyle,
//                "event" : {
//                    "click" : function( mdi){ _self.selectPeople(this, "person", mdi.get("value").split(",") ) }
//                }
//            }, true, this.app );
//            this.employeeName.load();
//        }
//
//        var tr = new Element("tr").inject(table);
//        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "休假类型："  }).inject(tr);
//        var td = new Element("td", { "styles" : this.css.editTableValue  }).inject(tr);
//        if( !this.isNew && !this.isEdited  ){
//            td.set("text",  d.leaveType  )
//        }else{
//            this.leaveType = new MDomItem( td, {
//                "name" : "leaveType",
//                "type" : "select",
//                "value" : d.leaveType,
//                "selectValue" : "带薪年休假,带薪病假,带薪福利假,扣薪事假,其他".split(",")
//            }, true, this.app );
//            this.leaveType.load();
//        }
//
//        var tr = new Element("tr").inject(table);
//        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "开始时间："  }).inject(tr);
//        var td = new Element("td", { "styles" : this.css.editTableValue }).inject(tr);
//        if( !this.isNew && !this.isEdited  ){
//            td.set("text",  d.startTime  )
//        }else{
//            this.startTime = new MDomItem( td, {
//                "name" : "startTime",
//                "value" : d.startTime,
//                "style" : this.css.inputTimeStyle,
//                "event" : {
//                    "click" : function( mdi){ _self.selectDateTime(this, false, true ) }
//                }
//            }, true, this.app );
//            this.startTime.load();
//        }
//
//        var tr = new Element("tr").inject(table);
//        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "结束时间："  }).inject(tr);
//        var td = new Element("td", { "styles" : this.css.editTableValue} ).inject(tr);
//        if( !this.isNew && !this.isEdited  ){
//            td.set("text",  d.endTime  )
//        }else{
//            this.endTime = new MDomItem( td, {
//                "name" : "endTime",
//                "value" : d.endTime,
//                "style" : this.css.inputTimeStyle,
//                "event" : {
//                    "click" : function( mdi){ _self.selectDateTime(this, false, true ) }
//                }
//            }, true, this.app );
//            this.endTime.load();
//        }
//
//        var tr = new Element("tr").inject(table);
//        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "休假天数："  }).inject(tr);
//        var td = new Element("td", { "styles" : this.css.editTableValue}).inject(tr);
//        if( !this.isNew && !this.isEdited  ){
//            td.set("text",  d.leaveDayNumber  )
//        }else{
//            this.leaveDayNumber = new MDomItem( td, {
//                "name" : "leaveDayNumber",
//                "value" : d.leaveDayNumber,
//                "style" : this.css.inputStyle,
//                "event" : {
//                    "keyup" : function(){ this.value=this.value.replace(/[^\d.]/g,'') }
//                }
//            }, true, this.app );
//            this.leaveDayNumber.load();
//        }
//
//        if( this.isNew || this.isEdited ){
//            this.createOkActionNode = new Element("div", {
//                "styles": this.css.createOkActionNode,
//                "text": this.app.lp.ok
//            }).inject(this.createFormNode);
//
//            this.createOkActionNode.addEvent("click", function(e){
//                this.okCreate(e);
//            }.bind(this));
//        }
//        this.cancelActionNode = new Element("div", {
//            "styles": this.css.createCancelActionNode,
//            "text": this.app.lp.cancel
//        }).inject(this.createFormNode);
//
//        this.cancelActionNode.addEvent("click", function(e){
//            this.cancelCreate(e);
//        }.bind(this));
//
//    },
//    setCreateNodeSize: function (width, height, top, left) {
//        if (!width)width = this.options && this.options.width ? this.options.width : "50%"
//        if (!height)height = this.options && this.options.height ? this.options.height : "50%"
//        if (!top) top = this.options && this.options.top ? this.options.top : 0;
//        if (!left) left = this.options && this.options.left ? this.options.left : 0;
//
//        var allSize = this.app.content.getSize();
//        var limitWidth = allSize.x; //window.screen.width
//        var limitHeight = allSize.y; //window.screen.height
//
//        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
//        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
//        300 > width && (width = 300);
//        220 > height && (height = 220);
//        top = top || parseInt((limitHeight - height) / 2, 10);
//        left = left || parseInt((limitWidth - width) / 2, 10);
//
//        this.createAreaNode.setStyles({
//            "width": "" + width + "px",
//            "height": "" + height + "px",
//            "top": "" + top + "px",
//            "left": "" + left + "px"
//        });
//
//        this.createNode.setStyles({
//            "width": "" + width + "px",
//            "height": "" + height + "px"
//        });
//
//        var iconSize = this.createIconNode ? this.createIconNode.getSize() : {x: 0, y: 0};
//        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
//        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};
//
//        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y;
//        //var formMargin = formHeight -iconSize.y;
//        this.createFormNode.setStyles({
//            "height": "" + contentHeight + "px"
//        });
//    },
//    cancelCreate: function(e){
//        var _self = this;
//        if( this.unitName )var unitName = this.unitName.get("value");
//        if ( this.isNew &&  unitName!="" && unitName!="default" ){
//            this.app.confirm("warn", e,
//                this.app.lp.create_cancel_title,
//                this.app.lp.create_cancel, "320px", "100px",
//                function(){
//                    _self.createMarkNode.destroy();
//                    _self.createAreaNode.destroy();
//                    this.close();
//                },function(){
//                    this.close();
//                }
//            );
//        }else{
//            this.createMarkNode.destroy();
//            this.createAreaNode.destroy();
//            delete _self;
//        }
//    },
//    okCreate: function(e){
//        var data = {
//            "id" : (this.data && this.data.id) ? this.data.id : null,
//            "unitName": this.unitName.get("value"),
//            "employeeName": this.employeeName.get("value"),
//            "leaveType": this.leaveType.get("value"),
//            "startTime": this.startTime.get("value"),
//            "endTime": this.endTime.get("value"),
//            "leaveDayNumber": this.leaveDayNumber.get("value")
//        };
//
//        //alert(JSON.stringify(data))
//
//        if (data.unitName && data.employeeName && data.leaveType && data.startTime && data.endTime && data.leaveDayNumber){
//            this.app.restActions.saveSelfHoliday(data, function(json){
//                if( json.type == "ERROR" ){
//                    this.app.notice( json.message  , "error");
//                }else{
//                    this.createMarkNode.destroy();
//                    this.createAreaNode.destroy();
//                    if(this.explorer.view)this.explorer.view.reload();
//                    this.app.notice( this.isNew ? this.app.lp.createSuccess : this.app.lp.updateSuccess  , "success");
//                }
//                //    this.app.processConfig();
//            }.bind(this));
//        }else{
//            this.app.notice( this.app.lp.selfHoliday.inputVaild, "error");
//        }
//    },
//    selectDateTime : function( el, timeOnly, isTme ){
//        MWF.require("MWF.widget.Calendar", function(){
//            var calendar = new MWF.widget.Calendar( el, {
//                "style": "xform",
//                "timeOnly": timeOnly,
//                "isTime": isTme,
//                "target": this.app.content
//            });
//            calendar.show();
//        }.bind(this));
//    },
//    selectPeople: function(el, type, value ){
//        var title
//        if( type == "unit" ){
//            title = "选择部门"
//        }else if( type == "topUnit" ){
//            title = "选择公司"
//        }else{
//            title = "选择个人"
//        }
//        var options = {
//            "type": type,
//            "title": title,
//            "count" : "1",
//            "values": value || [],
//            "onComplete": function(items){
//                var vs = [];
//                items.each(function(item){
//                    vs.push(item.data.name);
//                }.bind(this));
//                el.set("value",vs.join( "," ));
//            }.bind(this)
//        };
//        var selector = new MWF.O2Selector(this.app.content, options);
//    }
//});

