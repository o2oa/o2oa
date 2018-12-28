MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xApplication.Attendance.PersonSetting = new Class({
	Extends: MWF.xApplication.Attendance.Explorer,
	Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Attendance/$PersonSetting/";
        this.cssPath = "/x_component_Attendance/$PersonSetting/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    load: function(){
        this.loadToolbar();
        //this.loadFilter();
        this.loadContentNode();

        this.loadView();
        this.setNodeScroll();

    },
    loadView : function(){
        this.view = new MWF.xApplication.Attendance.PersonSetting.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey  );
        this.view.load();
        this.setContentSize();
    },
    createDocument: function(){
        if(this.view)this.view._createDocument();
    }
});

MWF.xApplication.Attendance.PersonSetting.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.PersonSetting.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
            this.actions.listPersonSetting(function(json){
                if (callback) callback(json);
            });
    },
    _removeDocument: function(document, all){
        this.actions.deletePersonSetting(document.id, function(json){
            this.explorer.view.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _createDocument: function(){
        var form = new MWF.xApplication.Attendance.PersonSetting.Form(this.explorer);
        form.create();
    },
    _openDocument: function( documentData ){
        var form = new MWF.xApplication.Attendance.PersonSetting.Form(this.explorer, documentData );
        form.edit();
    }

});

MWF.xApplication.Attendance.PersonSetting.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});


MWF.xApplication.Attendance.PersonSetting.Form = new Class({
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
            "<tr><td colspan='2' styles='formTableHead'>考勤人员设置</td></tr>" +
            "<tr><td styles='formTabelTitle' lable='configType'></td>"+
            "    <td styles='formTableValue' item='configType'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='topUnitName'></td>"+
            "    <td styles='formTableValue' item='topUnitName'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='unitName'></td>"+
            "    <td styles='formTableValue' item='unitName'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='employeeName'></td>"+
            "    <td styles='formTableValue' item='employeeName'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='employeeNumber'></td>"+
            "    <td styles='formTableValue' item='employeeNumber'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='empInTopUnitTime'></td>"+
            "    <td styles='formTableValue' item='empInTopUnitTime'></td></tr>" +
            "</table>";
        this.formTableArea.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.formTableArea, this.data, {
                isEdited : this.isEdited || this.isNew,
                itemTemplate : {
                    configType : { text:"配置类型", type : "select", selectText : ["需要考勤","不需要考勤"], selectValue : ["REQUIRED","NOTREQUIRED"] },
                    topUnitName : { text:"公司名称",  type : "org", orgType : "unit", notEmpty:true },
                    unitName : { text:"部门名称",  type : "org", orgType : "unit", notEmpty:true },
                    employeeName : { text:"员工姓名", type : "org", orgType : "person", notEmpty:true , "event" : {
                        change : function(  item , ev ){
                            if( typeOf( item.orgObject ) == "array" && item.orgObject.length > 0  ){
                                var data = item.orgObject[0].data;
                                if( data ){
                                    item.form.getItem( "employeeNumber").setValue( data.employee );
                                }
                            }
                        }
                    } },
                    employeeNumber : {  text:"员工编号", notEmpty:true },
                    empInTopUnitTime : { text:"入职日期", tType : "date" }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _ok: function( data, callback ){
        this.app.restActions.savePersonSetting( data, function(json){
            if( callback )callback(json);
        }.bind(this));
    }
});

