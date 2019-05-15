MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Execution", "WorkForm", null, false);

MWF.xApplication.Execution.CoreWork = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "100%",
        "height": "100%",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": false,
        "closeAction": true,
        "isNew": false,
        "isEdited": true
    },
    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp.coreWork;
        this.actions = this.app.restActions;
        this.path = "/x_component_Execution/$CoreWork/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};

        this.actions = actions;
    },
    load: function () {
        if (this.options.isNew) {
            this.create();
        } else if (this.options.isEdited) {
            this.edit();
        } else {
            this.open();
        }
    },
    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopIconNode = new Element("div", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.options.title
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            this.formTopContentNode = new Element("div", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode);

            this._createTopContent();

        }
    },
    _createTopContent: function () {

        var html = "<span styles='formTopContentTitle' lable='drafter'></span>" +
            "    <span styles='formTopContentValue' item='drafter'></span>" +
            "<span styles='formTopContentTitle' lable='draftDepartment'></span>" +
            "    <span styles='formTopContentValue' item='draftDepartment'></span>" +
            "<span styles='formTopContentTitle' lable='draftDate'></span>" +
            "    <span styles='formTopContentValue' item='draftDate'></span>";
        this.formTopContentNode.set("html", html);

        var form = new MForm(this.formTopContentNode, {
            drafter: "xadmin",
            draftDepartment: "开发部",
            draftDate: "2016-02-02"
        }, {
            isEdited: this.isEdited || this.isNew,
            itemTemplate: {
                drafter: {text: this.lp.drafter + ":", type: "innertext"},
                draftDepartment: {text: this.lp.draftDepartment + ":", type: "innertext"},
                draftDate: {text: this.lp.draftDate + ":", type: "innertext"}
            }
        }, this.app, this.css);
        form.load();
    },
    _createTableContent: function () {
        this.createCoreWorkInfor();
        this.createMyWorkList();
        this.createSplitWorkList();
    },
    createCoreWorkInfor: function() {
        var workContentArea = new Element("div.workContentArea", {
            "styles": this.css.workContentArea
        }).inject(this.formTableArea);

        var workContentTitleNode = new Element("div", {
            "styles": this.css.workContentTitleNode,
            "text": this.lp.coreWorkInfor
        }).inject(workContentArea);

        var workContentNode = new Element("div", {
            "styles": this.css.workContentNode
        }).inject(workContentArea);

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='coreWorkInforTable'>" +
            "<tr><td styles='coreWorkInforTitle' lable='coreWorkTitle'></td>" +
            "    <td styles='coreWorkInforValue' item='coreWorkTitle'></td></tr>" +
            "<tr><td>" +
            "<span styles='coreWorkInforTitleDiv' lable='defaultWorkType'></span>" +
            "    <span styles='coreWorkInforValueDiv' item='defaultWorkType'></span>" +
            "   <span styles='coreWorkInforTitleDiv' lable='defaultWorkLevel'></span>" +
            "    <span styles='coreWorkInforValueDiv' item='defaultWorkLevel'></span>" +
            "   <span styles='coreWorkInforTitleDiv' lable='workCompletedLimit'></span>" +
            "    <span styles='coreWorkInforValueDiv' item='workCompletedLimit'></span>" +
            "</td></tr>" +
            "<tr><td styles='coreWorkInforTitle' lable='coreWorkMemo'></td>" +
            "    <td styles='coreWorkInforValue' item='coreWorkMemo'></td></tr>" +
            "</table>"
        workContentNode.set("html", html);

        var form = new MForm(workContentNode, {data: "data"}, {
            isEdited: this.isEdited || this.isNew,
            itemTemplate: {
                coreWorkTitle: {text: this.lp.coreWorkTitle + ":", type: "innertext"},
                defaultWorkType: {text: this.lp.defaultWorkType + ":", type: "innertext"},
                defaultWorkLevel: {text: this.lp.defaultWorkLevel + ":", type: "innertext"},
                workCompletedLimit: {text: this.lp.workCompletedLimit + ":", type: "innertext"},
                coreWorkMemo: {
                    text: this.lp.coreWorkMemo + ":",
                    type: "innertext"
                }
            }
        }, this.app);
        form.load();
    },
    createMyWorkList : function(){
        var workContentArea = new Element("div.workContentArea", {
            "styles": this.css.workContentArea
        }).inject(this.formTableArea);

        var workContentTitleNode = new Element("div", {
            "styles": this.css.workContentTitleNode,
            "text": this.lp.myWorkInfor
        }).inject(workContentArea);

        var workContentNode = new Element("div", {
            "styles": this.css.workContentNode
        }).inject(workContentArea);
        var list = new MWF.xApplication.Execution.CoreWork.MyWorkView(workContentNode, this.app, this, { templateUrl : this.path+"listItem.json" });
        list.load();
    },
    createSplitWorkList : function(){
        var workContentArea = new Element("div.workContentArea", {
            "styles": this.css.workContentArea
        }).inject(this.formTableArea);

        var workContentTitleNode = new Element("div", {
            "styles": this.css.workContentTitleNode,
            "text": this.lp.splitWorkInfor
        }).inject(workContentArea);

        var workContentNode = new Element("div", {
            "styles": this.css.workContentNode
        }).inject(workContentArea);
        var list = new MWF.xApplication.Execution.CoreWork.MyWorkView(workContentNode, this.app, this, { templateUrl : this.path+"listItem.json" });
        list.load();
    }
})

MWF.xApplication.Execution.CoreWork.MyWorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.CoreWork.MyWorkDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        //this.actions.listHolidayAll(function(json){
        //    if (callback) callback(json);
        //});
        var json =  {
            "type": "success",
            "data": [
                {
                    "id": "53a508ec-7862-4036-a273-c15830cd3f86",
                    "createTime": "2016-04-19 15:38:50",
                    "updateTime": "2016-04-19 15:38:50",
                    "sequence": "2016041915385053a508ec-7862-4036-a273-c15830cd3f46",
                    "subject": "2016年人力资源部弹性福利系统优化和福利套餐方案制定工作",
                    "defaultWorkLevel": "公司重点工作",
                    "defaultWorkType": "深化改革",
                    "dutyDepartent": "人力资源部",
                    "doDepartment": "公司领导",
                    "secondDepartment": "综合部，信息化事业部",
                    "dutyPerson": "蒋艺娟",
                    "doPerson": "蔡全根",
                    "secondPerson": "宋兰美，周琼",
                    "timeLimit": "2016-08-18"
                },
                {
                    "id": "53a508ec-7862-4036-a273-c15830cd3f88",
                    "createTime": "2016-04-19 15:38:50",
                    "updateTime": "2016-04-19 15:38:50",
                    "sequence": "2016041915385053a508ec-7862-4036-a273-c15830cd3f46",
                    "subject": "2016年人力资源部弹性福利系统优化和福利套餐方案制定工作",
                    "defaultWorkLevel": "公司重点工作",
                    "defaultWorkType": "深化改革",
                    "dutyDepartent": "人力资源部",
                    "doDepartment": "公司领导",
                    "secondDepartment": "综合部，信息化事业部",
                    "dutyPerson": "蒋艺娟",
                    "doPerson": "蔡全根",
                    "secondPerson": "宋兰美，周琼",
                    "timeLimit": "2016-08-18"
                }
            ],
            "date": "2016-05-27 14:20:07",
            "spent": 2,
            "size": 2,
            "count": 0,
            "position": 0,
            "message": ""
        };
        if (callback) callback(json);
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteSchedule(documentData.id, function(json){
            this.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        this.workForm = new MWF.xApplication.Execution.WorkForm(this, this.actions, documentData, {
            "isNew": false,
            "isEdited": false
        });
        this.workForm.load();
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

});

MWF.xApplication.Execution.CoreWork.MyWorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    action_comfirm : function(){

    },
    action_split:function(){
        this.workForm = new MWF.xApplication.Execution.WorkForm(this, this.actions, this.data, {
            "isNew": true,
            "isEdited": false
        });
        this.workForm.load();
    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    }
    //open: function(){
    //    alert("open")
    //}
});


