MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xApplication.Attendance.PermissionExplorer = new Class({
	Extends: MWF.xApplication.Attendance.Explorer,
	Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "../x_component_Attendance/$PermissionExplorer/";
        this.cssPath = "../x_component_Attendance/$PermissionExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    loadView : function(){
        this.view = new MWF.xApplication.Attendance.PermissionExplorer.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        this.view.load();
        this.setContentSize();
    },
    createDocument: function(){
        if(this.view)this.view._createDocument();
    }
});

MWF.xApplication.Attendance.PermissionExplorer.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.PermissionExplorer.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
            this.actions.listPermission(function(json){
                if (callback) callback(json);
            });
    },
    _removeDocument: function(document, all){
        this.actions.deletePermission(document.id, function(json){
            this.explorer.view.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _createDocument: function(){
        var permission = new MWF.xApplication.Attendance.PermissionExplorer.Permission(this.explorer);
        permission.create();
    },
    _openDocument: function( documentData ){
        var permission = new MWF.xApplication.Attendance.PermissionExplorer.Permission(this.explorer, documentData );
        permission.edit();
    }

});

MWF.xApplication.Attendance.PermissionExplorer.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});


MWF.xApplication.Attendance.PermissionExplorer.Permission = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.PopupForm,
    options : {
        "width": 500,
        "height": 400,
        "hasTop" : true,
        "hasBottom" : true,
        "title" : "",
        "draggable" : true,
        "closeAction" : true
    },
    _createTableContent: function(){
        var lp = this.app.lp.permission;

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td colspan='2' styles='formTableHead'>"+lp.setAttendancer+"</td></tr>" +
            "<tr><td styles='formTabelTitle' lable='adminLevel'></td>"+
            "    <td styles='formTableValue' item='adminLevel'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='unitName'></td>"+
            "    <td styles='formTableValue' item='unitName'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='adminName'></td>"+
            "    <td styles='formTableValue' item='adminName'></td></tr>" +
            "</table>";
        this.formTableArea.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.formTableArea, this.data, {
                isEdited : this.isEdited || this.isNew,
                itemTemplate : {
                    adminLevel : { text: lp.role,  type : "select",
                        "selectText" : lp.roleSelectText,
                        "selectValue" : ["COMPANY","DEPT"]
                    },
                    unitName : { text: lp.unit, type : "org", orgType:"unit",notEmpty:true },
                    adminName : { text: lp.personName,  type : "org", orgType:"person" ,notEmpty:true }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _ok: function( data, callback ){
        this.app.restActions.savePermission(data, function(json){
            if( callback )callback(json);
        }.bind(this));
    }
});

//
//MWF.xApplication.Attendance.PermissionExplorer.Permission2 = new Class({
//    Extends: MWF.widget.Common,
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
//        if( this.data.unitName )this.data.unit = this.data.unitName;
//        if( this.data.adminName )this.data.personName = this.data.adminName;
//        if( this.data.adminLevel )this.data.role = this.data.adminLevel;
//    },
//
//    open: function(e){
//        this.isNew = false;
//        this.isEdited = false;
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
//        this.personName.focus();
//
//        this.setCreateNodeSize();
//        this.setCreateNodeSizeFun = this.setCreateNodeSize.bind(this);
//        this.addEvent("resize", this.setCreateNodeSizeFun);
//    },
//    createNode: function(){
//        var _self = this;
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
//        var lp = this.app.lp.permission;
//
//        var inputStyle = "width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC;height: 26px;";
//        var inputPersonStyle = "width: 99%; border:1px solid #999; background-color:#FFF; border-radius: 3px; box-shadow: 0px 0px 6px #CCC;height: 26px;"+
//            "background : url(../x_component_Attendance/$PermissionExplorer/default/icon/selectperson.png) 98% center no-repeat";
//
//        //+(!this.isNew && !this.isEdited  ? "" :
//        //        ("<input type='text' id='role' " + "style='" + inputStyle +"'" + " value='" + ( this.data && this.data.role ? this.data.role : "") + "'/>")) +
//
//        var html = "<table width='100%' height='200' border='0' cellPadding='0' cellSpacing='0'>" +
//            "<tr>"+
//            "<td colspan='2' style='height: 50px; line-height: 50px; text-align: center; min-width: 80px; font-size:18px;font-weight: bold;'>" + lp.setAttendancer + "</td>" +
//            "</tr>" +
//            "<tr>"+
//            "<td style='height: 30px; line-height: 30px; text-align: left; min-width: 80px; width:25%'>" + lp.role + ":</td>" +
//            "<td style='; text-align: left;' id='roleArea'></td>"+
//            "</tr>" +
//            "<tr id='unitArea'>" +
//            "<td style='height: 30px; line-height: 30px;  text-align: left' >"+lp.unit+":</td>" +
//            "<td style='; text-align: right;'>" +
//            (!this.isNew && !this.isEdited  ? "" :
//                ("<input type='text' id='unit' " + "style='" + inputPersonStyle +"'" + " value='" + ( this.data && this.data.unit ? this.data.unit : "") + "'/>")) +
//            "</td>" +
//            "</tr>" +
//            "<tr>"+
//            "<td style='height: 30px; line-height: 30px; text-align: left'>"+lp.personName+":</td>" +
//            "<td style='text-align: right;'>"+
//            (!this.isNew && !this.isEdited  ? "" :
//                ("<input type='text' id='personName' " + "style='" + inputPersonStyle +"'" + " value='" + ( this.data && this.data.personName ? this.data.personName : "") + "'/>")) +
//            "</td>" +
//            "</tr>" +
//            "</table>";
//        this.createFormNode.set("html", html);
//
//        this.personName = this.createFormNode.getElement("#personName");
//        this.unit = this.createFormNode.getElement("#unit");
//        this.roleArea = this.createFormNode.getElement("#roleArea");
//        this.role = new MDomItem( this.roleArea, {
//            "name" : "role",
//            "type" : "select",
//            "value" : this.data.role || "DEPT",
//            "selectText" : ["公司管理员","部门管理员"],
//            "selectValue" : ["COMPANY","DEPT"],
//            "style" : {"text-align":"left"}
//        }, true, this.app );
//        this.role.load();
//
//        this.personName.addEvent("click",function(){
//            this.selecePerson()
//        }.bind(this))
//        this.unit.addEvent("click",function(){
//            this.selectUnit( this.role.get("value") )
//        }.bind(this))
//
//        this.cancelActionNode = new Element("div", {
//            "styles": this.css.createCancelActionNode,
//            "text": this.app.lp.cancel
//        }).inject(this.createFormNode);
//        this.createOkActionNode = new Element("div", {
//            "styles": this.css.createOkActionNode,
//            "text": this.app.lp.ok
//        }).inject(this.createFormNode);
//
//        this.cancelActionNode.addEvent("click", function(e){
//            this.cancelCreate(e);
//        }.bind(this));
//        this.createOkActionNode.addEvent("click", function(e){
//            this.okCreate(e);
//        }.bind(this));
//    },
//
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
//        if ( this.isNew &&  this.personName.get("value") ){
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
//            "id" : this.data.id || null,
//            "unitName": this.unit.get("value"),
//            //"organizationOu" : this.unit.get("value")=="" ? "" : this.data.organizationOu,
//            "adminName": this.personName.get("value"),
//            "adminLevel": this.role.getValue()
//        };
//        if (data.adminName ){
//            this.app.restActions.savePermission(data, function(json){
//                if( json.type == "ERROR" ){
//                    this.app.notice( json.message  , "error");
//                }else{
//                    this.createMarkNode.destroy();
//                    this.createAreaNode.destroy();
//                    if(this.explorer.view)this.explorer.view.reload();
//                    this.app.notice( this.isNew ? this.app.lp.createSuccess : this.app.lp.updateSuccess  , "success");
//                }
//
//                //    this.app.processConfig();
//            }.bind(this));
//        }else{
//            this.adminName.setStyle("border-color", "red");
//            this.adminName.focus();
//            this.app.notice( this.app.lp.inputName, "error");
//        }
//    },
//    selecePerson: function(){
//        var options = {
//            "type": "person",
//            "title": "设置管理员",
//            "count" : "1",
//            "values": [this.data.personName] || [],
//            "onComplete": function(items){
//                this.data.personName = [];
//                items.each(function(item){
//                    this.data.personName.push(item.data.name);
//                }.bind(this));
//                this.personName.set("value",this.data.personName);
//            }.bind(this)
//        };
//        var selector = new MWF.O2Selector(this.app.content, options);
//    },
//    selectUnit: function( u ){
//        var options = {
//            "type": u=="COMPANY" ? "topUnit" : "unit",
//            "title": u=="COMPANY" ? "选择公司":"选择部门",
//            "count" : "1",
//            "values": [this.data.unit] || [],
//            "onComplete": function(items){
//                this.data.unit = [];
//                items.each(function(item){
//                    this.data.unit.push(item.data.name);
//                }.bind(this));
//                this.unit.set("value",this.data.unit);
//            }.bind(this)
//        };
//        var selector = new MWF.O2Selector(this.app.content, options);
//    }
//});

