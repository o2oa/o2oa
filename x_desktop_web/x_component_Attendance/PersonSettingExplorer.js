MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xApplication.Attendance.PersonSettingExplorer = new Class({
	Extends: MWF.xApplication.Attendance.Explorer,
	Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Attendance/$PersonSettingExplorer/";
        this.cssPath = "/x_component_Attendance/$PersonSettingExplorer/"+this.options.style+"/css.wcss";
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
        this.view = new MWF.xApplication.Attendance.PersonSettingExplorer.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey  );
        this.view.load();
        this.setContentSize();
    },
    createDocument: function(){
        if(this.view)this.view._createDocument();
    }
});

MWF.xApplication.Attendance.PersonSettingExplorer.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.PersonSettingExplorer.Document(this.table, data, this.explorer, this);
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
        var form = new MWF.xApplication.Attendance.PersonSettingExplorer.Form(this.explorer);
        form.create();
    },
    _openDocument: function( documentData ){
        var form = new MWF.xApplication.Attendance.PersonSettingExplorer.Form(this.explorer, documentData );
        form.edit();
    }

})

MWF.xApplication.Attendance.PersonSettingExplorer.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

})


MWF.xApplication.Attendance.PersonSettingExplorer.Form = new Class({
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
            "<tr><td styles='formTabelTitle' lable='companyName'></td>"+
            "    <td styles='formTableValue' item='companyName'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='organizationName'></td>"+
            "    <td styles='formTableValue' item='organizationName'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='employeeName'></td>"+
            "    <td styles='formTableValue' item='employeeName'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='employeeNumber'></td>"+
            "    <td styles='formTableValue' item='employeeNumber'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='empInCompanyTime'></td>"+
            "    <td styles='formTableValue' item='empInCompanyTime'></td></tr>" +
            "</table>"
        this.formTableArea.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.formTableArea, this.data, {
                isEdited : this.isEdited || this.isNew,
                itemTemplate : {
                    configType : { text:"配置类型", type : "select", selectText : ["需要考勤","不需要考勤"], selectValue : ["REQUIRED","NOTREQUIRED"] },
                    companyName : { text:"公司名称",  tType : "company", notEmpty:true },
                    organizationName : { text:"部门名称",  tType : "department", notEmpty:true },
                    employeeName : { text:"员工姓名",  tType : "person", notEmpty:true},
                    employeeNumber : {  text:"员工编号", notEmpty:true },
                    empInCompanyTime : { text:"入职日期", tType : "date" }
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

