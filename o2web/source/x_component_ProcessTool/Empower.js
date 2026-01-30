MWF.xDesktop.requireApp("ProcessTool", "Task", null ,false);
MWF.xApplication.ProcessTool.Empower = new Class({
    Extends: MWF.xApplication.ProcessTool.Task,
    loadOperate: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.operateNode", {
            "styles": this.css.fileterNode
        }).inject(this.topOperateNode);

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='remove'></td>" +
            "    <td styles='filterTableValue' item='add'></td>" +

            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);

        this.fileterForm = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                remove: {
                    "value": "删除", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要删除吗？", 350, 120, function () {

                                checkedItems.each(function (item){
                                    //item.node.setStyles(item.css.documentNode_remove);
                                    _self.view._remove(item.data);
                                }.bind(this));

                                this.close();
                                _self.form.app.notice("删除成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
                add: {
                    "value": "新增授权", type: "button", className: "filterButton", event: {
                        click: function (e) {
                            var form = new MWF.xApplication.ProcessTool.Empower.EditForm({app: this.app,view:this}, {} );
                            form.edit();
                        }.bind(this)
                    }
                },
            }
        }, this.app, this.css);
        this.fileterForm.load();
    },
    loadFilter: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles": this.css.fileterNode
        }).inject(this.topContentNode);

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" + //style='width: 900px;'
            "<tr>" +

            "    <td styles='filterTableTitle' lable='fromPerson'></td>" +
            "    <td styles='filterTableValue' item='fromPerson'></td>" +
            "    <td styles='filterTableTitle' lable='startTime'></td>" +
            "    <td styles='filterTableValue' item='startTime'></td>" +
            "    <td styles='filterTableTitle' lable='endTime'></td>" +
            "    <td styles='filterTableValue' item='endTime'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "    <td styles='filterTableValue' item='reset'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);


        this.form = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                fromPerson: {
                    "text": "授权人",
                    "type": "org",
                    "orgType": "identity",
                    "orgOptions": {"resultType": "person"},
                    "style": {"min-width": "100px"},
                    "orgWidgetOptions": {"disableInfor": true}
                },
                startTime: {
                    text: lp.startTime,
                    "tType": "datetime",
                    "style": {"width":"150px"}
                },
                endTime: {
                    text: lp.endTime,
                    "tType": "datetime",
                    "style": {"width":"150px"}
                },
                action: {
                    "value": lp.query, type: "button", className: "filterButton", event: {
                        click: function () {
                            var result = this.form.getResult(false, null, false, true, false);
                            for (var key in result) {
                                if (!result[key]) {
                                    delete result[key];
                                } else if (key === "fromPerson" && result[key].length > 0) {

                                    result["fromPerson"] = result[key][0];

                                }
                            }
                            this.loadView(result);
                        }.bind(this)
                    }
                },
                reset: {
                    "value": lp.reset, type: "button", className: "filterButtonGrey", event: {
                        click: function () {
                            this.form.reset();
                            this.loadView();
                        }.bind(this)
                    }
                },
            }
        }, this.app, this.css);
        this.form.load();
    },
});
MWF.xApplication.ProcessTool.Empower.View = new Class({
    Extends: MWF.xApplication.ProcessTool.Task.View,

    _getCurrentPageData: function (callback, count, pageNum) {
        this.clearBody();
        if (!count) count = 15;
        if (!pageNum) {
            if (this.pageNum) {
                pageNum = this.pageNum = this.pageNum + 1;
            } else {
                pageNum = this.pageNum = 1;
            }
        } else {
            this.pageNum = pageNum;
        }

        var filter = this.filterData || {};

        this.app.app.orgAction.EmpowerAction.managerlistPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))

    },
    _remove : function (data){
        this.app.app.orgAction.EmpowerAction.managerDelete(data.id,function (){},null,false);
    },
    _create: function () {

    },
    _open: function (data) {
        var options = {"workId": data.work};
        this.app.desktop.openApplication(null, "process.Work", options);
    },

});
MWF.xApplication.ProcessTool.Empower.Document = new Class({
    Extends: MWF.xApplication.ProcessTool.Task.Document,

    open: function () {
        this.view._open(this.data);
    },
    edit : function (){

        var form = new MWF.xApplication.ProcessTool.Empower.EditForm({app: this.app.app,view:this.app}, this.data );
        form.edit();

    },
    remove : function (e){

        var _self = this;
        this.node.setStyles(this.css.documentNode_remove);
        this.readyRemove = true;
        this.view.lockNodeStyle = true;

        this.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {

            _self.view._remove(_self.data);
            _self.view.lockNodeStyle = false;

            this.close();
            _self.view.app.loadView();

        }, function () {
            _self.node.setStyles(_self.css.documentNode);
            _self.readyRemove = false;
            _self.view.lockNodeStyle = false;
            this.close();
        });
    }
});
MWF.xApplication.ProcessTool.Empower.EditForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "attendanceV2",
        "width": "600",
        "height": "400",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "resizeable" : true,
        "closeAction": true,
        "title": "编辑外出授权",
        "closeByClickMaskWhenReading": true,
        "hasBottom" : true,
        "buttonList" : [{ "type":"ok", "text": "保存" },{ "type":"cancel", "text": "取消" }]
    },
    onQueryOpen : function (){

    },
    _postLoad: function(){
        this._createTableContent_();
    },
    _createTableContent: function(){},
    _createTableContent_: function () {
        var identityTextList = [];
        var identityList =[];
        var startTime, endTime, defaultStartDate, defaultStartTime, defaultEndDate, defaultEndTime;
        if( this.data.startTime && this.data.endTime ){
            startTime= this.date = typeOf( this.data.startTime )=="string" ? Date.parse( this.data.startTime ) : this.data.startTime;
            endTime= typeOf( this.data.endTime )=="string" ? Date.parse( this.data.endTime ) : this.data.endTime;
            defaultStartDate = startTime.format("%Y-%m-%d");
            defaultStartTime = startTime.format("%H:%M");
            defaultEndDate = endTime.format("%Y-%m-%d");
            defaultEndTime = endTime.format("%H:%M");
        }else{
            startTime = this.date = new Date().increment("hour",1);
            endTime = startTime.clone().increment("hour",1);
            defaultStartDate = startTime.format("%Y-%m-%d");
            defaultStartTime = startTime.format("%H") + ":00";
            defaultEndDate = endTime.format("%Y-%m-%d");
            defaultEndTime = endTime.format("%H") + ":00";
        }

        this.formTableArea.set("html", this.getHtml());

        this.form = new MForm(this.formTableArea, this.data, {
            isEdited: true,
            style : "attendance",
            itemTemplate: {
                fromIdentity: { text: "授权人",type: "org",  orgType: ["identity"], count : 1, orgWidgetOptions : {
                    },defaultValue:this.data.fromIdentity},
                toIdentity: { text: "被授权人",type: "org",  orgType: ["identity"], count : 1, orgWidgetOptions : {

                    },defaultValue:this.data.toIdentity},
                startDateInput: {
                    text: "授权开始时间",
                    tType: "date",
                    defaultValue: defaultStartDate,
                    notEmpty: true
                },
                startTimeInput: {
                    tType: "time",
                    defaultValue: defaultStartTime,
                    className: ((this.isNew || this.isEdited || 1) ? "inputTimeUnformatWidth" : ""),
                    disable: this.data.isAllDayEvent
                },
                endDateInput: {
                    text: "授权结束时间",
                    tType: "date",
                    defaultValue: defaultEndDate,
                    notEmpty: true
                },
                endTimeInput: {
                    tType: "time",
                    defaultValue: defaultEndTime,
                    className: ((this.isNew || this.isEdited || 1) ? "inputTimeUnformatWidth" : ""),
                    disable: this.data.isAllDayEvent
                },
                type: {
                    text: "授权类型", type: "radio",
                    selectText: ["全部", "应用", "流程"],
                    selectValue: ["all", "application", "process"],
                    defaultValue: this.data.type||"all",
                    event :{
                        "click":function(){

                            var type = this.form.getItem("type").getValue();
                            this.formTableArea.getElement("td[item=application]").parentNode.setStyle("display","none");
                            this.formTableArea.getElement("td[item=process]").parentNode.setStyle("display","none");
                            if(type=="all"){
                                //this.formTableArea.getElement("td[item=application]").parentNode.setStyle("display","");
                                //this.formTableArea.getElement("td[item=process]").parentNode.setStyle("display","");
                            }else{
                                this.formTableArea.getElement("td[item="+type+"]").parentNode.setStyle("display","");
                            }
                        }.bind(this)
                    }
                },
                process: {
                    "text": "流程",
                    "type": "text",
                    "defaultValue":this.data.processName,
                    "event": {

                        "click": function (item, ev){

                            var v = item.getValue();
                            o2.xDesktop.requireApp("Selector", "package", function(){
                                var options = {
                                    "type": "Process",
                                    "values": this.data.type=="process"?[{name:this.data.processName,id:this.data.process}]:[],
                                    "count": 1,
                                    "onComplete": function (items) {
                                        if(items.length>0){

                                            var d = items[0].data;
                                            console.log(d)
                                            this.data.processName = d.name;
                                            this.data.processId = d.id;
                                            this.data.edition = d.edition;
                                            item.setValue(d.name);
                                        }
                                    }.bind(this)
                                };
                                new o2.O2Selector(this.app.desktop.node, options);
                            }.bind(this),false);
                        }.bind(this)}
                },
                application: {
                    "text": "应用",
                    "type": "text",
                    "defaultValue":this.data.applicationName,
                    "event": {

                        "click": function (item, ev){
                            var v = item.getValue();
                            o2.xDesktop.requireApp("Selector", "package", function(){
                                var options = {
                                    "type": "Application",
                                    "values": this.data.type=="application"?[{name:this.data.applicationName,id:this.data.application}]:[],
                                    "count": 1,
                                    "onComplete": function (items) {

                                        if(items.length>0){

                                            var d = items[0].data;
                                            this.data.applicationName = d.name;
                                            this.data.applicationId = d.id;
                                            item.setValue(d.name);
                                        }

                                    }.bind(this)
                                };
                                new o2.O2Selector(this.app.desktop.node, options);
                            }.bind(this),false);
                        }.bind(this)}
                }
            },
            onPostLoad:function(){

            }.bind(this)
        },this.app,this.css);
        this.form.load();

    },

    getHtml : function(){
        return  "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable' id='empowerEditTable'>" +

            "<tr ><td styles='formTableTitleRight' lable='fromIdentity'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='fromIdentity'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr><td styles='formTableTitleRight' lable='toIdentity'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='toIdentity'></div>" +
            "    </td>" +
            "</tr>" +
            "<tr><td styles='formTableTitleRight' width='100' lable='startDateInput'></td>" +
            "    <td styles='formTableValue' item='startDateInput' width='205'></td>" +
            "    <td styles='formTableValue' item='startTimeInput'></td>" +
            "</tr>" +
            "<tr><td styles='formTableTitleRight' lable='endDateInput'></td>" +
            "    <td styles='formTableValue' item='endDateInput'></td>" +
            "    <td styles='formTableValue' item='endTimeInput'></td>" +
            "</tr>" +
            "<tr><td styles='formTableTitleRight' lable='type'></td>" +
            "    <td styles='formTableValue' item='type' colspan='2'></td>" +
            "</tr>" +
            "<tr style='display:"+(this.data.type=="application"?"":"none")+"'><td styles='formTableTitleRight' lable='application'>应用</td>" +
            "    <td styles='formTableValue1' item='application' colspan='2'></td>" +
            "</tr>" +
            "<tr style='display:"+(this.data.type=="process"?"":"none")+"'><td styles='formTableTitleRight' lable='process'>流程</td>" +
            "    <td styles='formTableValue1' item='process' colspan='2'></td>" +
            "</tr>" +
            "</table>";
    },
    _ok: function (data, callback) {
        console.log(data)

        data.fromPerson = data.fromIdentity.split("@")[0];
        data.toPerson = data.toIdentity.split("@")[0];
        data.startTime = data.startDateInput+" "+data.startTimeInput+":00";
        data.completedTime = data.endDateInput+" "+data.endTimeInput+":00";

        data.process = data.processId;
        data.application = data.applicationId;

        if( Date.parse(data.completedTime) - Date.parse(data.startTime) < 0 ){
            this.app.notice("授权时间出错","error");
            return;
        }

        data.enable = true;

        if(data.type=="all"){
            delete data.processName;
            delete data.process;
            delete data.processAlias;
            delete data.edition;
            delete data.application;
            delete data.applicationName;
            delete data.applicationAlias;

        }else if(data.type=="application"){
            delete data.processName;
            delete data.process;
            delete data.processAlias;
            delete data.edition;

        }else if(data.type=="process"){
            delete data.application;
            delete data.applicationName;
            delete data.applicationAlias;
        }

        if(data.id){
            this.app.orgAction.EmpowerAction.managerEdit(data.id,data,function (){
                this.app.notice("保存成功","success");
                this.explorer.view.loadView();
                this.close();
            }.bind(this),null,false);
        }else {
            this.app.orgAction.EmpowerAction.managerCreate(data,function (){
                this.app.notice("创建成功","success");
                this.explorer.view.loadView();
                this.close();
            }.bind(this),null,false);
        }



    },
});
