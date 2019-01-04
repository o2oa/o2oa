MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xApplication.Attendance.StatisticsCycle = new Class({
	Extends: MWF.xApplication.Attendance.Explorer,
	Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Attendance/$StatisticsCycle/";
        this.cssPath = "/x_component_Attendance/$StatisticsCycle/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    loadView : function(){
        this.view = new MWF.xApplication.Attendance.StatisticsCycle.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        this.view.load();
        this.setContentSize();
    },
    createDocument: function(){
        if(this.view)this.view._createDocument();
    }
});

MWF.xApplication.Attendance.StatisticsCycle.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.StatisticsCycle.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
            this.actions.listCycle(function(json){
                if (callback) callback(json);
            });
    },
    _removeDocument: function(document, all){
        this.actions.deleteCycle(document.id, function(json){
            this.explorer.view.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _createDocument: function(){
        var sc = new MWF.xApplication.Attendance.StatisticsCycle.Form(this.explorer);
        sc.create();
    },
    _openDocument: function( documentData ){
        var sc = new MWF.xApplication.Attendance.StatisticsCycle.Form(this.explorer, documentData );
        sc.edit();
    }

});

MWF.xApplication.Attendance.StatisticsCycle.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});


MWF.xApplication.Attendance.StatisticsCycle.Form = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.PopupForm,
    options : {
        "width": 600,
        "height": 600,
        "hasTop" : true,
        "hasBottom" : true,
        "title" : "",
        "draggable" : true,
        "closeAction" : true
    },
    _createTableContent: function(){
        var lp = this.app.lp.schedule;

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td colspan='2' styles='formTableHead'>统计周期设置</td></tr>" +
            "<tr><td styles='formTabelTitle' lable='topUnitName'></td>"+
            "    <td styles='formTableValue'>" +
            "       <div item='topUnitName'></div>"+
            "       <div style='font-size: 12px;color: #999;'>双击选择，填写'*'匹配所有公司</div>"+
            "   </td></tr>" +
            "<tr><td styles='formTabelTitle' lable='unitName'></td>"+
            "    <td styles='formTableValue'>" +
            "       <div item='unitName'></div>"+
            "       <div style='font-size: 12px;color: #999;'>双击选择，填写'*'匹配所有部门</div>"+
            "   </td></tr>" +
            "<tr><td styles='formTabelTitle' lable='cycleYear'></td>"+
            "    <td styles='formTableValue' item='cycleYear'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='cycleMonth'></td>"+
            "    <td styles='formTableValue' item='cycleMonth'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='cycleStartDateString'></td>"+
            "    <td styles='formTableValue' item='cycleStartDateString'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='cycleEndDateString'></td>"+
            "    <td styles='formTableValue' item='cycleEndDateString'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='description'></td>"+
            "    <td styles='formTableValue' item='description'></td></tr>" +
            "</table>";
        this.formTableArea.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.formTableArea, this.data, {
                isEdited : this.isEdited || this.isNew,
                itemTemplate : {
                    topUnitName : { text: "统计公司",  tType : "unit",unsetDefaultEvent : true, event : {
                        dblclick : function(item, ev){
                            item.selectPerson(item.getElements()[0],"","unit")
                        }
                    }},
                    unitName : { text: "统计部门", tType : "unit",unsetDefaultEvent : true, event : {
                        dblclick : function(item, ev){
                            item.selectPerson(item.getElements()[0],"","unit")
                        }
                    } },
                    cycleYear : { text: "统计周期年份", type : "select", notEmpty:true, defaultValue : new Date().getFullYear(), selectValue : function(){
                        var years = [];
                        var year = new Date().getFullYear() + 5;
                        for (var i = 0; i < 10; i++) {
                            years.push(year--);
                        }
                        return years;
                    }},
                    cycleMonth : { text: "统计周期月份",  type : "select",notEmpty:true, selectValue : ["01","02","03","04","05","06","07","08","09","10","11","12"] },
                    cycleStartDateString : { text: "开始日期", tType : "date"},
                    cycleEndDateString : {  text:"结束日期", tType : "date" },
                    description : { text:"说明备注", type : "textarea" }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _ok: function( data, callback ){
        this.app.restActions.saveCycle(data, function(json){
            if( callback )callback(json);
        }.bind(this));
    }
});


MWF.xApplication.Attendance.StatisticsCycle.StatisticsCycle2 = new Class({
    Extends: MWF.widget.Common,
    options: {
        "width": "600",
        "height": "600"
    },
    initialize: function( explorer, data ){
        this.explorer = explorer;
        this.app = explorer.app;
        this.data = data || {};
        this.css = this.explorer.css;

        this.load();
    },
    load: function(){

    },

    open: function(e){
        this.isNew = false;
        this.isEdited = false;
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

        this.createNode = new Element("div", {
            "styles": this.css.createNode
        }).inject(this.createAreaNode);

        this.createIconNode = new Element("div", {
            "styles": this.isNew ? this.css.createNewNode : this.css.createIconNode
        }).inject(this.createNode);

        this.createFormNode = new Element("div", {
            "styles": this.css.createFormNode
        }).inject(this.createNode);

        this.createTableContainer = new Element("div", {
            "styles": this.css.createTableContainer
        }).inject(this.createFormNode);

        this.createTableArea = new Element("div", {
            "styles": this.css.createTableArea
        }).inject(this.createTableContainer);


        var table = new Element("table", {
            "width" : "100%", "height" : "250", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.editTable, "class" : "editTable"
        }).inject( this.createTableArea );

        var d = this.data;

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "styles" : this.css.editTableHead, "colspan": "4", "text" : "统计周期设置"  }).inject(tr);

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "公司名称："  }).inject(tr);
        var td = new Element("td", { "styles" : this.css.editTableValue }).inject(tr);
        if( !this.isNew && !this.isEdited  ){
            td.set("text",  d.topUnitName  )
        }else{
            this.topUnitName = new MDomItem( td, {
                "name" : "topUnitName",
                "value" : d.topUnitName,
                "style" : this.css.inputPersonStyle,
                "event" : {
                    "dblclick" : function( mdi){ _self.selectPeople(this, "topUnit", mdi.get("value").split(",") ) }
                }
            }, true, this.app );
            this.topUnitName.load();
            new Element("div", { "text" : "双击选择，填写'*'匹配所有公司" , "styles" : {"color":"#ccc"}}).inject(td)
        }

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "部门名称："  }).inject(tr);
        var td = new Element("td", { "styles" : this.css.editTableValue }).inject(tr);
        if( !this.isNew && !this.isEdited  ){
            td.set("text",  d.unitName  )
        }else{
            this.unitName = new MDomItem( td, {
                "name" : "unitName",
                "value" : d.unitName,
                "style" : this.css.inputPersonStyle,
                "event" : {
                    "dblclick" : function( mdi){ _self.selectPeople(this, "unit", mdi.get("value").split(",") ) }
                }
            }, true, this.app );
            this.unitName.load();
            new Element("div", { "text" : "双击选择，填写'*'匹配所有部门" , "styles" : {"color":"#ccc"} }).inject(td)
        }

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "统计周期年份："  }).inject(tr);
        var td = new Element("td", { "styles" : this.css.editTableValue }).inject(tr);
        if( !this.isNew && !this.isEdited ){
            td.set( "text", d.cycleYear || "")
        }else {
            this.cycleYear = new MDomItem(td, {
                "name": "cycleYear",
                "type": "select",
                "value": d.cycleYear || new Date().getFullYear(),
                "selectValue": function () {
                    var years = [];
                    var year = new Date().getFullYear() + 5;
                    for (var i = 0; i < 10; i++) {
                        years.push(year--);
                    }
                    return years;
                }
            }, true, this.app);
            this.cycleYear.load();
        }

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "统计周期月份："  }).inject(tr);
        var td = new Element("td", { "styles" : this.css.editTableValue }).inject(tr);
        if( !this.isNew && !this.isEdited ){
            td.set( "text", d.cycleMonth || "")
        }else{
            this.cycleMonth = new MDomItem( td, {
                "name" : "cycleMonth",
                "type" : "select",
                "value" : d.cycleMonth,
                "selectValue" :["01","02","03","04","05","06","07","08","09","10","11","12"]
            }, true, this.app );
            this.cycleMonth.load();
        }

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "开始日期："  }).inject(tr);
        var td = new Element("td", { "styles" : this.css.editTableValue }).inject(tr);
        this.cycleStartDateString = new MDomItem( td, {
            "name" : "cycleStartDateString",
            "value" : d.cycleStartDateString,
            "style" : this.css.inputTimeStyle,
            "event" : {
                "click" : function( mdi){
                    var da = new Date(_self.cycleYear.getValue() + "-" + _self.cycleMonth.getValue() + "-" + "01");
                    _self.selectDateTime(this, false, false, da.decrement("month", 1) );
                }
            }
        }, true, this.app );
        this.cycleStartDateString.load();

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "结束日期："  }).inject(tr);
        var td = new Element("td", { "styles" : this.css.editTableValue }).inject(tr);
        this.cycleEndDateString = new MDomItem( td, {
            "name" : "cycleEndDateString",
            "value" : d.cycleEndDateString,
            "style" : this.css.inputTimeStyle,
            "event" : {
                "click" : function( mdi){
                    var da = new Date(_self.cycleYear.getValue() + "-" + _self.cycleMonth.getValue() + "-" + "01");
                    _self.selectDateTime(this, false, false, da );
                }
            }
        }, true, this.app );
        this.cycleEndDateString.load();

        var tr = new Element("tr").inject(table);
        var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "说明备注："  }).inject(tr);
        var td = new Element("td", { "styles" : this.css.editTableValue }).inject(tr);
        this.description = new MDomItem( td, {
            "type" : "textarea",
            "name" : "description",
            "value" : d.description,
            "style" : this.css.inputTextAreaStyle
        }, true, this.app );
        this.description.load();


        this.cancelActionNode = new Element("div", {
            "styles": this.css.createCancelActionNode,
            "text": "取消"
        }).inject(this.createFormNode);


        this.cancelActionNode.addEvent("click", function(e){
            this.cancelCreate(e);
        }.bind(this));

        if( this.isNew || this.isEdited){
            this.createOkActionNode = new Element("div", {
                "styles": this.css.createOkActionNode,
                "text": "确定"
            }).inject(this.createFormNode);

            this.createOkActionNode.addEvent("click", function(e){
                this.okCreate(e);
            }.bind(this));
        }
    },

    setCreateNodeSize: function (width, height, top, left) {
        if (!width)width = this.options && this.options.width ? this.options.width : "50%";
        if (!height)height = this.options && this.options.height ? this.options.height : "50%";
        if (!top) top = this.options && this.options.top ? this.options.top : 0;
        if (!left) left = this.options && this.options.left ? this.options.left : 0;

        var allSize = this.app.content.getSize();
        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
        300 > width && (width = 300);
        220 > height && (height = 220);
        top = top || parseInt((limitHeight - height) / 2, 10);
        left = left || parseInt((limitWidth - width) / 2, 10);

        this.createAreaNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px",
            "top": "" + top + "px",
            "left": "" + left + "px"
        });

        this.createNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px"
        });

        var iconSize = this.createIconNode ? this.createIconNode.getSize() : {x: 0, y: 0};
        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};

        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y;
        //var formMargin = formHeight -iconSize.y;
        this.createFormNode.setStyles({
            "height": "" + contentHeight + "px"
        });
    },
    //setCreateNodeSize: function(){
    //    var size = this.app.node.getSize();
    //    var allSize = this.app.content.getSize();
    //
    //    this.createAreaNode.setStyles({
    //        "width": ""+size.x+"px",
    //        "height": ""+size.y+"px"
    //    });
    //    var hY = size.y*0.9;
    //    var mY = size.y*0.2/2;
    //    this.createNode.setStyles({
    //        "height": ""+hY+"px",
    //        "margin-top": ""+mY+"px"
    //    });
    //
    //    var iconSize = this.createIconNode.getSize();
    //    var formHeight = hY*0.7;
    //    if (formHeight>250) formHeight = 250;
    //    var formMargin = hY*0.3/2-iconSize.y;
    //    this.createFormNode.setStyles({
    //        "height": ""+formHeight+"px",
    //        "margin-top": ""+formMargin+"px"
    //    });
    //},
    cancelCreate: function(e){
        var _self = this;
        this.createMarkNode.destroy();
        this.createAreaNode.destroy();
        delete _self;
    },
    okCreate: function(e){
        var data = {
            "topUnitName": this.topUnitName.get("value")=="" ? "*" : this.topUnitName.get("value"),
            "unitName" : this.unitName.get("value")=="" ? "*" : this.unitName.get("value"),
            "cycleYear": this.cycleYear.get("value"),
            "cycleMonth": this.cycleMonth.get("value"),
            "cycleStartDateString": this.cycleStartDateString.get("value"),
            "cycleEndDateString": this.cycleEndDateString.get("value"),
            "description": this.description.get("value")
        };
        if( this.data.id ) data.id = this.data.id;
        if (data.cycleStartDateString && data.cycleEndDateString ){
            this.app.restActions.saveCycle(data, function(json){
                if( json.type == "ERROR" ){
                    this.app.notice( json.message  , "error");
                }else{
                    this.createMarkNode.destroy();
                    this.createAreaNode.destroy();
                    if(this.explorer.view)this.explorer.view.reload();
                    this.app.notice( this.isNew ? this.app.lp.createSuccess : this.app.lp.updateSuccess  , "success");
                }

                //    this.app.processConfig();
            }.bind(this));
        }else{
           // this.adminName.setStyle("border-color", "red");
            //this.adminName.focus();
            this.app.notice( "请选择开始日期和结束日期", "error");
        }
    },
    selectDateTime : function( el, timeOnly, isTme, baseDate ){
        var opt = {
            "style": "xform",
            "timeOnly": timeOnly,
            "isTime": isTme,
            "target": this.app.content
        };
        if( baseDate )opt.baseDate = baseDate;
        MWF.require("MWF.widget.Calendar", function(){
            var calendar = new MWF.widget.Calendar( el, opt );
            calendar.show();
        }.bind(this));
    },
    selectPeople: function(el, type, value ){
        var title;
        if( type == "unit" ){
            title = "选择部门"
        }else if( type == "topUnit" ){
            title = "选择公司"
        }else{
            title = "选择个人"
        }
        var options = {
            "type": type,
            "title": title,
            "count" : "1",
            "values": value || [],
            "onComplete": function(items){
                var vs = [];
                items.each(function(item){
                    vs.push(item.data.name);
                }.bind(this));
                el.set("value",vs.join( "," ));
            }.bind(this)
        };
        var selector = new MWF.O2Selector(this.app.content, options);
    }
});

