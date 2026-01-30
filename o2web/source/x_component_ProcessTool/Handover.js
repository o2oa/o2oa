MWF.xDesktop.requireApp("ProcessTool", "Task", null ,false);

MWF.xApplication.ProcessTool.Handover = new Class({
    Extends: MWF.xApplication.ProcessTool.Task,
    loadOperate: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.operateNode", {
            "styles": this.css.fileterNode
        }).inject(this.topOperateNode);

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='add'></td>" +
            "    <td styles='filterTableValue' item='remove'></td>" +
            "    <td styles='filterTableValue' item='start'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);

        this.fileterForm = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {

                add: {
                    "value": "新增交接", type: "button", className: "filterButton", event: {
                        click: function (e) {
                            var form = new MWF.xApplication.ProcessTool.Handover.EditForm({app: this.app,view:this}, {} );
                            form.edit();
                        }.bind(this)
                    }
                },
                remove: {
                    "value": "删除", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.fileterForm.app.confirm("warn", e.node, "提示", "您确定要删除吗？", 350, 120, function () {

                                checkedItems.each(function (item){
                                    //item.node.setStyles(item.css.documentNode_remove);
                                    _self.view._remove(item.data,_self.fileterForm);
                                }.bind(this));

                                this.close();
                                //_self.fileterForm.app.notice("删除成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
                start: {
                    "value": "运行", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.fileterForm.app.confirm("warn", e.node, "提示", "您确定要运行吗？", 350, 120, function () {

                                checkedItems.each(function (item){
                                    //item.node.setStyles(item.css.documentNode_remove);
                                    _self.view._start(item.data,_self.fileterForm);
                                }.bind(this));

                                this.close();
                                //_self.fileterForm.app.notice("运行成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
            }
        }, this.app, this.css);
        this.fileterForm.load();
    },
    loadFilter: function () {
    },
});
MWF.xApplication.ProcessTool.Handover.View = new Class({
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

        this.app.action.HandoverAction.listPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))

    },
    _create: function () {

    },
    _remove : function (data,form){
        this.app.action.HandoverAction.cancel(data.id,{},function (){

            form.app.notice("删除成功","success");

        },function (json){

        },false);

    },
    _start : function (data,form){
        this.app.action.HandoverAction.process(data.id,{},function (){
            form.app.notice("运行成功","success");
        },function (json){

        },false);
    },
    _open: function (data) {

        this.app.action.HandoverAction.get(
            data.id,
            function( json ){
                data = json.data;

                this.showJobList(data.handoverJobList);

                console.log(data)
            }.bind(this)
        );
    },
    showJobList : function (handoverJobList){

        var node = new Element("div");

        if(handoverJobList.length>0){
            handoverJobList.forEach(function (job) {
                var div = new Element("div").inject(node);
                div.set("text",job);
                div.setStyle("margin-bottom","5px");
                div.addEvent("click",function (){
                    var options = {"job": job};
                    layout.openApplication(null, "process.Work", options);
                })
            })

        }else {
            node.set("html","<span style='color: #999999;margin: 10px'>没有文档</span>")
        }

        var dlg = o2.DL.open({
            "title": "查看详情",
            "width": "600px",
            "height": "360px",
            "mask": true,
            "content": node,
            "container": null,
            "positionNode": this.explorer.app.content,
            "onQueryClose": function () {
                node.destroy();
            }.bind(this),
            "onPostShow": function () {
                dlg.reCenter();

            }.bind(this)
        });

    }

});
MWF.xApplication.ProcessTool.Handover.Document = new Class({
    Extends: MWF.xApplication.ProcessTool.Task.Document,

    open: function () {
        this.view._open(this.data);
    }
});
MWF.xApplication.ProcessTool.Handover.EditForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "attendanceV2",
        "width": "700",
        "height": "500",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : false,
        "resizeable" : false,
        "closeAction": true,
        "title": "新增权限交接",
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

        if(!this.data.type) this.data.type = "授权"
        this.formTableArea.set("html", this.getHtml());

        this.form = new MForm(this.formTableArea, this.data, {
            isEdited: true,
            style : "attendance",
            itemTemplate: {
                tip : {
                    "value": "?交接类型说明", type: "innertext",  style: {
                        "font-size":"14px",
                        "color":"red",
                        "cursor":"pointer",
                        "margin-left":"20px"
                    }
                },
                selectPerosn: {
                    "value": "选择", type: "button", className: "filterButton", event: {
                        click: function (e) {
                            var item = this.form.getItem("person");
                            var v = item.getValue();
                            o2.xDesktop.requireApp("Selector", "package", function(){
                                var options = {
                                    "type": "Person",
                                    "values": [],
                                    "count": 1,
                                    "onComplete": function (items) {

                                        if(items.length>0) item.setValue(items[0].data.distinguishedName);

                                    }.bind(this)
                                };
                                new o2.O2Selector(this.app.desktop.node, options);
                            }.bind(this),false);
                        }.bind(this)
                    }
                },
                type: {
                    text: "权限交接类型", type: "radio",
                    selectText: ["替换", "授权"],
                    selectValue: ["替换", "授权"],
                    defaultValue: this.data.type||"授权",
                    event :{
                        "click":function(){

                            var type = this.form.getItem("type").getValue();


                            this.formTableArea.getElement("[item=oldPerson]").parentNode.setStyle("display","none");
                            this.formTableArea.getElement("[item=person]").parentNode.setStyle("display","none");

                            if(type=="替换"){
                                this.formTableArea.getElement("[item=oldPerson]").parentNode.setStyle("display","");
                            }else{
                                this.formTableArea.getElement("[item=person]").parentNode.setStyle("display","");

                            }
                        }.bind(this)
                    },
                },
                title: { text: "标题",type: "text",defaultValue:this.data.title,notEmpty:true},

                person: { text: "用户",type: "text",defaultValue:this.data.person,notEmpty:true,


                    event: {

                        "dblclick": function (item, ev){

                            var v = item.getValue();
                            o2.xDesktop.requireApp("Selector", "package", function(){
                                var options = {
                                    "type": "Person",
                                    "values": [],
                                    "count": 1,
                                    "onComplete": function (items) {

                                        if(items.length>0) item.setValue(items[0].data.distinguishedName);

                                    }.bind(this)
                                };
                                new o2.O2Selector(this.app.desktop.node, options);
                            }.bind(this),false);
                        }.bind(this)}
                },

                targetIdentity: { text: "目标用户",type: "org",  orgType: ["identity"], count : 1, orgWidgetOptions : {

                    },defaultValue:this.data.targetIdentity},
                scheme: {
                    text: "授权类型", type: "radio",
                    selectText: ["全部", "应用", "流程","指定工作"],
                    selectValue: ["all", "application", "process","job"],
                    defaultValue: this.data.scheme||"all",
                    event :{
                        "click":function(){

                            var type = this.form.getItem("scheme").getValue();
                            this.formTableArea.getElement("td[item=application]").parentNode.setStyle("display","none");
                            this.formTableArea.getElement("td[item=process]").parentNode.setStyle("display","none");
                            this.formTableArea.getElement("td[item=job]").parentNode.setStyle("display","none");

                            if(type=="all"){
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
                                    "count": 0,
                                    "onComplete": function (items) {

                                        var processList = [];
                                        var porcessListValue = [];
                                        if(items.length>0){

                                            items.forEach(function (item) {
                                                var d = item.data;
                                                processList.push(d.name);
                                                porcessListValue.push(d.id);
                                            })
                                        }

                                        this.data.processList = porcessListValue;
                                        item.setValue(processList.join());


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
                                    "count": 0,
                                    "onComplete": function (items) {

                                        var processList = [];
                                        var porcessListValue = [];
                                        if(items.length>0){

                                            items.forEach(function (item) {
                                                var d = item.data;
                                                processList.push(d.name);
                                                porcessListValue.push(d.id);
                                            })
                                        }

                                        this.data.applicationList = porcessListValue;
                                        item.setValue(processList.join());

                                    }.bind(this)
                                };
                                new o2.O2Selector(this.app.desktop.node, options);
                            }.bind(this),false);
                        }.bind(this)}
                },
                job: {
                    "text": "文档列表",
                    "type": "textarea",
                    "defaultValue":this.data.jobList,
                    "event": {

                        "click": function (item, ev){
                            if(this.form.getItem("person").getValue()===""){

                                this.app.notice("人员不能为空")
                                return
                            }

                            this.curitem = item;

                            var node = new Element("div");

                            this.dlg = o2.DL.open({
                                "title": "选择工作",
                                "width": "1000px",
                                "height": "700px",
                                "mask": true,
                                "content": node,
                                "container": null,
                                "positionNode": this.explorer.app.content,
                                "onQueryClose": function () {
                                    node.destroy();
                                }.bind(this),
                                "buttonList": [
                                    {
                                        "text": "确认",
                                        "action": function () {

                                            var arr = [];

                                            var checkedItems = this.review.view.getCheckedItems();
                                            checkedItems.each(function (item){
                                                console.log(item)

                                                arr.push(item.data.job);
                                            }.bind(this));

                                            this.curitem.setValue(arr.join("\n"));
                                            this.data.jobList = arr;

                                            this.dlg.close();
                                        }.bind(this)
                                    },
                                    {
                                        "text": "关闭",
                                        "action": function () {
                                            this.dlg.close();
                                        }.bind(this)
                                    }
                                ],
                                "onPostShow": function () {

                                    MWF.xDesktop.requireApp("ProcessTool", "Review", function(){
                                        this.review = new MWF.xApplication.ProcessTool.Review(node,this.app,this,{
                                            "className" : "Review"
                                        });
                                        this.review.load();

                                    }.bind(this));


                                    this.dlg.reCenter();

                                }.bind(this)
                            });


                        }.bind(this)}
                }
            },
            onPostLoad:function(){

                new mBox.Tooltip({
                    content: "1、替换一般用于人员名称的变更，比如，张三改名成张三1，替换后张三的权限不再保留；<br/>2、授权一般用于权限交接，比如人员离职或者人员调动，授权后授权人的权限不变。",
                    setStyles: {content: {padding: 20, lineHeight: 20}},
                    attach:this.form.getItem("tip").node,
                    offset: {
                        x: 50,
                        y:-20
                    },
                    transition: 'flyin'
                });
            }.bind(this)
        },this.app,this.css);
        this.form.load();

    },

    getHtml : function(){
        return  "<table width='98%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable' id='empowerEditTable'>" +
            "<tr ><td styles='formTableTitleRight' lable='title'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='title'></div>" +
            "   </td>" +
            "</tr>" +

            "<tr ><td styles='formTableTitleRight' lable='type'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='type' style='float: left'></div>" +
            "       <div item='tip'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr ><td styles='formTableTitleRight' lable='person'></td>" +
            "    <td styles='formTableValue' colspan='2' >" +
            "    <div item='person' style='float: left;width: 340px'></div>" +
            "   <div item='selectPerosn'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr><td styles='formTableTitleRight' lable='targetIdentity'></td>" +
            "    <td styles='formTableValue' colspan='2' item='targetIdentity'>" +
            "    </td>" +
            "</tr>" +
            "<tr><td styles='formTableTitleRight' lable='scheme'></td>" +
            "    <td styles='formTableValue' item='scheme' colspan='2'></td>" +
            "</tr>" +
            "<tr style='display:"+(this.data.type=="application"?"":"none")+"'><td styles='formTableTitleRight' lable='application'>应用</td>" +
            "    <td styles='formTableValue1' item='application' colspan='2'></td>" +
            "</tr>" +
            "<tr style='display:"+(this.data.type=="process"?"":"none")+"'><td styles='formTableTitleRight' lable='process'>流程</td>" +
            "    <td styles='formTableValue1' item='process' colspan='2'></td>" +
            "</tr>" +
            "<tr style='display:"+(this.data.type=="job"?"":"none")+"'><td styles='formTableTitleRight' lable='job'>job</td>" +
            "    <td styles='formTableValue1' item='job' colspan='2'></td>" +
            "</tr>" +
            "</table>";
    },
    _ok: function (data, callback) {

        this.app.action.HandoverAction.post(data,function (){
            this.app.notice("创建成功","success");
            this.explorer.view.loadView();
            this.close();
        }.bind(this),null,false);


    },
});
