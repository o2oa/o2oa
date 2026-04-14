
MWF.xApplication.ProcessTool.Info = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "className" : ""
    },

    initialize: function(node, app, options){
        this.setOptions(options);
        this.app = app;
        this.path = "../x_component_ProcessTool/$Main/" + this.options.style + "/";

        this.css = this.app.css;
        this.lp = this.app.lp;

        this.className = this.options.className;

        this.action = o2.Actions.load("x_cms_assemble_control");

        this.node = $(node);
    },
    reload: function(){
        this.node.empty();
        this.load();
    },
    load: function(){
        this.node.empty();
        this.createTopNode();
        this.createContainerNode();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.addEvent("resize", this.setContentSizeFun);
        this.loadView();

        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));
    },
    loadView: function (filterData) {
        console.log(filterData)
        if (this.view) this.view.destroy();
        this.contentNode.empty();
        var viewContainerNode = this.viewContainerNode = new Element("div.viewContainerNode", {
            "styles": this.css.viewContainerNode
        }).inject(this.contentNode);

        this.view = new MWF.xApplication.ProcessTool[this.className].View(viewContainerNode, this, this, {
            templateUrl: this.path + this.className + "_listItem.json",
            "pagingEnable": true,
            "wrapView": true,
            "noItemText": this.lp.noItem,
            // "scrollType": "window",
            "pagingPar": {
                pagingBarUseWidget: true,
                position: ["bottom"],
                style: "blue_round",
                hasReturn: false,
                currentPage: this.options.viewPageNum,
                countPerPage: 15,
                visiblePages: 9,
                hasNextPage: true,
                hasPrevPage: true,
                hasTruningBar: true,
                hasJumper: true,
                returnText: "",
                hiddenWithDisable: false,
                text: {
                    prePage: "",
                    nextPage: "",
                    firstPage: this.lp.firstPage,
                    lastPage: this.lp.lastPage
                },
                onPostLoad: function () {
                    debugger;
                    this.setContentSize();
                }.bind(this)
            }
        }, {
            lp: this.lp
        });
        if (filterData) this.view.filterData = filterData;
        this.view.load();
    },
    getOffsetY: function (node) {
        return (node.getStyle("margin-top").toInt() || 0) +
            (node.getStyle("margin-bottom").toInt() || 0) +
            (node.getStyle("padding-top").toInt() || 0) +
            (node.getStyle("padding-bottom").toInt() || 0) +
            (node.getStyle("border-top-width").toInt() || 0) +
            (node.getStyle("border-bottom-width").toInt() || 0);
    },
    setContentSize: function () {

        var nodeSize = this.app.node.getSize();
        var h = nodeSize.y - this.getOffsetY(this.node);
        var topY = this.topContainerNode ? (this.getOffsetY(this.topContainerNode) + this.topContainerNode.getSize().y) : 0;
        h = h - topY;
        h = h - this.getOffsetY(this.viewContainerNode);
        h = h - this.getOffsetY(this.app.node);

        var pageSize = (this.view && this.view.pagingContainerBottom) ? this.view.pagingContainerBottom.getComputedSize() : {totalHeight: 0};
        h = h - pageSize.totalHeight;

        this.view.viewWrapNode.setStyles({
            "height": "" + h + "px",
            "overflow": "auto"
        });
    },
    createContainerNode: function () {
        this.createContent();
    },
    createContent: function () {

        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.node);

        this.contentNode = new Element("div.contentNode", {
            "styles": this.css.contentNode
        }).inject(this.middleNode);

    },
    createTopNode: function () {
        this.topContainerNode = new Element("div.topContainerNode", {
            "styles": this.css.topContainerNode
        }).inject(this.node);

        this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.topContainerNode);

        this.topContentNode = new Element("div", {
            "styles": this.css.topContentNode
        }).inject(this.topNode);

        this.topOperateNode = new Element("div", {
            "styles": this.css.topOperateNode
        }).inject(this.topNode);

        this.loadOperate();
        this.loadFilter();

    },
    loadOperate: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.operateNode", {
            "styles": this.css.fileterNode
        }).inject(this.topOperateNode);

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='remove'></td>" +
            "    <td styles='filterTableValue' item='publish'></td>" +
            "    <td styles='filterTableValue' item='cancelPublish'></td>" +
            "    <td styles='filterTableValue' item='top'></td>" +
            "    <td styles='filterTableValue' item='cancelTop'></td>" +
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
                publish: {
                    "value": "发布", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要发布吗？", 350, 120, function () {

                                checkedItems.each(function (item){

                                    _self.view._publish(item.data);
                                }.bind(this));

                                this.close();
                                _self.form.app.notice("发布成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
                cancelPublish: {
                    "value": "取消发布", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要取消发布吗？", 350, 120, function () {

                                checkedItems.each(function (item){
                                    //item.node.setStyles(item.css.documentNode_remove);
                                    _self.view._cancelPublish(item.data);
                                }.bind(this));

                                this.close();
                                _self.form.app.notice("取消发布成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
                top: {
                    "value": "置顶", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要置顶吗？", 350, 120, function () {

                                checkedItems.each(function (item){
                                    //item.node.setStyles(item.css.documentNode_remove);
                                    _self.view._top(item.data);
                                }.bind(this));

                                this.close();
                                _self.form.app.notice("置顶成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
                cancelTop: {
                    "value": "取消置顶", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要取消置顶吗？", 350, 120, function () {

                                checkedItems.each(function (item){
                                    //item.node.setStyles(item.css.documentNode_remove);
                                    _self.view._cancelTop(item.data);
                                }.bind(this));

                                this.close();
                                _self.form.app.notice("取消置顶成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                }
            }
        }, this.app, this.css);
        this.fileterForm.load();
    },
    loadFilter: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles": this.css.fileterNode
        }).inject(this.topContentNode);

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableTitle' lable='title'></td>" +
            "    <td styles='filterTableTitle' item='title'></td>" +
            "    <td styles='filterTableTitle' lable='appName'></td>" +
            "    <td styles='filterTableTitle' item='appName'></td>" +
            "    <td styles='filterTableTitle' lable='categoryName'></td>" +
            "    <td styles='filterTableTitle' item='categoryName'></td>" +
            "    <td styles='filterTableTitle' lable='creatorList'></td>" +
            "    <td styles='filterTableValue' item='creatorList'></td>" +
            "    <td styles='filterTableTitle' lable='creatorUnitNameList'></td>" +
            "    <td styles='filterTableValue' item='creatorUnitNameList'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "    <td styles='filterTableValue' item='reset'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);


        this.form = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                title: {text: "标题", "type": "text", "style": {"min-width": "100px"}},
                appName: {
                    text: "栏目",
                    "type": "text",

                    "style": {"min-width": "100px"},
                    "event": {

                        "click": function (item, ev){
                            debugger
                            var v = item.getValue();
                            o2.xDesktop.requireApp("Selector", "package", function(){
                                var options = {
                                    "type": "CMSApplication",
                                    "values": item.node.retrieve("items") || [],
                                    "count": 1,
                                    "onComplete": function (items) {
                                        var arr = [];
                                        var arr2 = [];
                                        items.each(function (data) {
                                            arr.push(data.data);
                                            arr2.push(items[0].data.name);
                                        });
                                        item.setValue(arr2.join(","));
                                        item.node.store("items", arr);
                                    }.bind(this)
                                };
                                new o2.O2Selector(this.app.desktop.node, options);
                            }.bind(this),false);
                        }.bind(this)}
                },
                categoryName: {
                    text: "应用",
                    "type": "text",

                    "style": {"min-width": "100px"},
                    "event": {

                        "click": function (item, ev){
                            debugger
                            var v = item.getValue();
                            o2.xDesktop.requireApp("Selector", "package", function(){
                                var options = {
                                    "type": "CMSCategory",
                                    "values": item.node.retrieve("items") || [],
                                    "count": 1,
                                    "onComplete": function (items) {
                                        var arr = [];
                                        var arr2 = [];
                                        items.each(function (data) {
                                            arr.push(data.data);
                                            arr2.push(items[0].data.name);
                                        });
                                        item.setValue(arr2.join(","));
                                        item.node.store("items", arr);
                                    }.bind(this)
                                };
                                new o2.O2Selector(this.app.desktop.node, options);
                            }.bind(this),false);
                        }.bind(this)}
                },
                creatorList: {
                    "text": "创建人",
                    "type": "org",
                    "orgType": "identity",
                    "orgOptions": {"resultType": "person"},
                    "style": {"min-width": "100px"},
                    "orgWidgetOptions": {"disableInfor": true}
                },
                creatorUnitNameList: {
                    "text": "部门",
                    "type": "org",
                    "orgType": "unit",
                    "orgOptions": {"resultType": "person"},
                    "style": {"min-width": "100px"},
                    "orgWidgetOptions": {"disableInfor": true}
                },
                action: {
                    "value": lp.query, type: "button", className: "filterButton", event: {
                        click: function () {
                            var result = this.form.getResult(false, null, false, true, false);
                            for (var key in result) {
                                if (!result[key]) {
                                    delete result[key];
                                } else if (key === "appName" && result[key].length > 0) {
                                    //result[key] = result[key][0].split("@")[1];
                                    result["appNameList"] = [result[key]];
                                    delete result[key];
                                }else if (key === "categoryName" && result[key] !== "") {
                                    //result[key] = result[key][0].split("@")[1];
                                    result["categoryNameList"] = [result[key]];
                                    delete result[key];
                                }else if (key === "categoryName" && result[key] !== "") {
                                    //result[key] = result[key][0].split("@")[1];
                                    result["categoryNameList"] = [result[key]];
                                    delete result[key];
                                }else if (key === "endTime" && result[key] !== "") {
                                    result[key] = result[key][0] + " 23:59:59"

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
MWF.xApplication.ProcessTool.Info.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.ProcessTool[this.app.className].Document(this.viewNode, data, this.explorer, this, null, index);
    },
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

        filter.documentType = "全部";
        filter.statusList = ["published" , "draft" ,"archived"];


        o2.Actions.load("x_cms_assemble_control").DocumentAction.managerQuery_listWithFilterPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;

            // json.data.each(function(d){
            //     d.appName = this.app.app.getAppName(d.appId);
            // }.bind(this));

            if (callback) callback(json);
        }.bind(this))

    },
    _remove : function (data){
        o2.Actions.load("x_cms_assemble_control").DocumentAction.persist_delete(data.id,function (){},null,false);
    },
    _publish : function (data){
        o2.Actions.load("x_cms_assemble_control").DocumentAction.persist_publishAndNotify(data.id,function (){},null,false);
    },
    _cancelPublish : function (data){
        o2.Actions.load("x_cms_assemble_control").DocumentAction.persist_publishCancel(data.id,function (){},null,false);
    },
    _create: function () {

    },
    _open: function (data) {
        var options = {"documentId": data.id};
        this.app.app.desktop.openApplication(null, "cms.Document", options);
    },
    _top : function (data){

        o2.Actions.load("x_cms_assemble_control").DocumentAction.persist_top(data.id,function (){},null,false);
    },
    _cancelTop : function (data){

        o2.Actions.load("x_cms_assemble_control").DocumentAction.persist_unTop(data.id,function (){},null,false);
    },
    _queryCreateViewNode: function () {

    },
    _postCreateViewNode: function (viewNode) {

    },
    _queryCreateViewHead: function () {

    },
    _postCreateViewHead: function (headNode) {

    }


});
MWF.xApplication.ProcessTool.Info.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument: function (itemNode, ev) {
        var removeNode = itemNode.getElements("[styles='removeNode']")[0];
        if (removeNode) removeNode.setStyle("opacity", 1)
    },
    mouseoutDocument: function (itemNode, ev) {
        var removeNode = itemNode.getElements("[styles='removeNode']")[0];
        if (removeNode) removeNode.setStyle("opacity", 0)
    },
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {

    },
    open: function () {
        this.view._open(this.data);
    },
    edit : function (){
        var form = new MWF.xApplication.ProcessTool.Info.EditForm({app: this.app.app}, this.data );
        form.open();
    },
    remove : function (e){

        var _self = this;
        this.node.setStyles(this.css.documentNode_remove);
        this.readyRemove = true;
        this.view.lockNodeStyle = true;

        this.explorer.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {

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
MWF.xApplication.ProcessTool.Info.EditForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "attendanceV2",
        "width": "800",
        "height": "700",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "resizeable" : true,
        "closeAction": true,
        "title": "维护管理",
        "hideBottomWhenReading": true,
        "closeByClickMaskWhenReading": true,
    },
    _postLoad: function(){
        if(this.data.completedTime){
            this.isCompletedWork = true;
        }
        this._createTableContent_();
    },
    _createTableContent: function(){},
    _createTableContent_: function () {

        //this.formTableArea.set("html", this.getHtml());
        this.formTableContainer.setStyle("width","90%");
        this.formTableContainer.setStyle("margin","0px auto 10px");
        this.loadTab();

    },
    loadTab : function (){

        this.tabNode = new Element("div",{"styles" : this.css.tabNode }).inject(this.formTableArea);

        this.attachementArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
        this.businessDataArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);

        MWF.require("MWF.widget.Tab", function(){

            this.tabs = new MWF.widget.Tab(this.tabNode, {"style": "attendance"});
            this.tabs.load();

            this.businessDataPage = this.tabs.addTab(this.businessDataArea, "业务数据", false);
            this.businessDataPage.addEvent("show",function(){
                if(!this.initBusinessData) this.loadBusinessData();
            }.bind(this));

            this.attachementPage = this.tabs.addTab(this.attachementArea, "附件", false);
            this.attachementPage.addEvent("show",function(){
                if(!this.initAttachement) this.loadAttachement();
            }.bind(this));

            this.tabs.pages[0].showTab();
        }.bind(this));
    },

    loadAttachement : function (){
        o2.Actions.load("x_cms_assemble_control").FileInfoAction.listFileInfoByDocumentId(this.data.id, function (json) {
            this.attachmentList = json.data;
            this._loadAttachement();
            this.initAttachement = true;
        }.bind(this), null, false);
    },
    _loadAttachement : function (){
        console.log(this.css)
        this.attachementArea.empty();
        this.attachmentContentNode = new Element("div").inject(this.attachementArea)
        var attachmentTableNode = new Element("table.table",{
            "border" : 0,
            "cellpadding" : 5,
            "cellspacing" : 0
        }).inject(this.attachmentContentNode);

        var attachmentTableTheadNode = new Element("thead").inject(attachmentTableNode);
        var attachmentTableTbodyNode = new Element("tbody").inject(attachmentTableNode);
        var attachmentTableTheadTrNode = new Element("tr").inject(attachmentTableTheadNode);
        Array.each(["附件名称", "上传人", "上传时间", "标识","大小","操作"], function (text) {
            new Element("th", {"text": text}).inject(attachmentTableTheadTrNode);
        }.bind(this));

        var siteArr = [];
        this.attachmentList.each(function (attachment) {

            if(!siteArr.contains(attachment.site)) siteArr.push(attachment.site);
            var trNode = new Element("tr").inject(attachmentTableTbodyNode);
            trNode.store("data", attachment);

            Array.each([attachment.name,  attachment.creatorUid.split("@")[0], attachment.createTime ,attachment.site,attachment["length"]], function (text, index) {
                new Element("td", {
                    text: index ===5 ? this.getFileSize(text) : text
                }).inject(trNode);
            }.bind(this));

            var tdOpNode = new Element("td").inject(trNode);
            var deleteButton = new Element("button", {"text": "删除", "class": "button"}).inject(tdOpNode);
            deleteButton.addEvent("click", function (e) {
                _self = this;
                this.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {
                    _self.app.action.FileInfoAction.delete(attachment.id,function(json){
                        _self.loadAttachement();
                    },null,false);
                    this.close();
                }, function(){
                    this.close();
                });
            }.bind(this));
            var downButton = new Element("button", {"text": "下载", "class": "button"}).inject(tdOpNode);
            downButton.addEvent("click", function () {

                var locate = window.location;
                var protocol = locate.protocol;
                var addressObj = layout.serviceAddressList["x_cms_assemble_control"];
                var address = protocol+"//"+addressObj.host+(addressObj.port==80|| addressObj.port === ""? "" : ":"+addressObj.port)+addressObj.context;
                window.open(o2.filterUrl(address) + "/jaxrs/fileinfo/download/document/"+ attachment.id +"/stream")

            }.bind(this));


        }.bind(this));


        var attachmentUploadDiv = new Element("div").inject(this.attachmentContentNode);

        var siteSelect = new Element("select",{"class":"select","style":"float:left"}).inject(attachmentUploadDiv);
        new Element("option",{value:"",text:""}).inject(siteSelect);
        siteArr.each(function(site){
            new Element("option",{value:site,text:site}).inject(siteSelect);
        });
        siteSelect.addEvent("change",function(){
            uploadSite.set("value",siteSelect.get("value"));
        });
        var uploadSite = new Element("input",{
            "class":"input",
            "placeholder":"对应上传的附件标识",
            "style" :"width:200px;float:left"
        }).inject(attachmentUploadDiv);
        var uploadButton = new Element("button", {"text": "上传", "class": "button"}).inject(attachmentUploadDiv);

        uploadButton.addEvent("click", function () {

            if(uploadSite.get("value")==""){
                this.app.notice("对应上传的附件标识不能为空","error");
                return false;
            }
            var options = {
                "title": "附件区域"
            };

            var site = uploadSite.get("value");

            var uploadAction = "uploadAttachment";
            o2.require("o2.widget.Upload", null, false);
            var upload = new o2.widget.Upload(this.app.content, {
                "action": o2.Actions.get("x_cms_assemble_control").action,
                "method": uploadAction,
                "parameter": {
                    "id": this.data.id
                },
                "data":{
                    "site": site
                },
                "onCompleted": function(){
                    this.loadAttachement();
                }.bind(this)
            });
            upload.load();


        }.bind(this));
    },
    loadBusinessData : function (){
        o2.Actions.load("x_cms_assemble_control").DataAction.getWithDocument(this.data.id, function (json) {
            this.workData = json.data;
            this._loadBusinessData();
            this.initBusinessData = true;
        }.bind(this), null, false);
    },
    _loadBusinessData : function (){
        var workData = this.workData;
        var workDataContentNode = new Element("div",{"style":"margin:5px"}).inject(this.businessDataArea);

        this.workDataContentNode = workDataContentNode;

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableTitle' lable='fieldList'></td>" +
            "    <td styles='filterTableTitle' item='fieldList'></td>" +
            "    <td styles='filterTableTitle' lable='fieldType'></td>" +
            "    <td styles='filterTableTitle' item='fieldType'></td>" +
            "    <td styles='filterTableTitle' lable='fieldName'></td>" +
            "    <td styles='filterTableTitle' item='fieldName'></td>" +
            "</tr>" +
            "<tr>" +
            "    <td styles='filterTableTitle' lable='fieldValue'></td>" +
            "    <td styles='filterTableValue' item='fieldValue'  colspan=3></td>" +
            "   <td styles='filterTableValue' colspan=2><div item='action' ></div></td>" +
            "</tr>" +
            "</table><div item='workData'></div>"

        workDataContentNode.set("html", html);

        this.form = new MForm(workDataContentNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                fieldList: {
                    "text": "字段列表",
                    "type": "select",
                    "style": {"max-width": "150px"},
                    "selectValue": function () {
                        var arr = [""];
                        arr.append(Object.keys(workData));
                        return arr;
                    },
                    "event": {
                        "change": function (item, ev) {

                            var type = typeof(workData[item.getValue()]);
                            item.form.getItem("fieldType").setValue(type);
                            item.form.getItem("fieldName").setValue(item.getValue());

                            if(type === "object" || type === "array"){
                                item.form.getItem("fieldValue").setValue(JSON.stringify(workData[item.getValue()]));
                            }else {
                                item.form.getItem("fieldValue").setValue(workData[item.getValue()]);
                            }


                        }.bind(this)
                    }
                },
                fieldType: {
                    "text": "字段类型",
                    "type": "select",
                    "style": {"max-width": "150px"},
                    "selectValue": function () {
                        var array = ["","array","boolean","string","number","object"];
                        return array;
                    },
                    "event": {
                        "change": function (item, ev) {

                        }.bind(this)
                    }
                },
                fieldName: {text: "字段名", "type": "text", "style": {"min-width": "100px"}},
                fieldValue: {text: "字段值", "type": "textarea", "style": {"width": "100%","margin-left": "10px"}},

                action: {
                    "value": "修改", type: "button", className: "filterButton", event: {
                        click: function (e) {
                            var result = this.form.getResult(false, null, false, true, false);

                            var fieldName = result["fieldName"];
                            var fieldType = result["fieldType"];
                            var fieldValue = result["fieldValue"];

                            if (!fieldName) return false;
                            workData[fieldName] = (fieldType === "object" ? JSON.parse(fieldValue) : fieldValue);

                            _self = this;
                            this.app.confirm("warn", e.node, "提示", "确认是否修改", 350, 120, function () {
                                _self.app.action.DataAction.updateWithDocument(_self.data.id,workData,function (json){},null,false);
                                _self.app.notice("success");

                                _self.loadScriptEditor();

                                this.close();
                            }, function(){
                                this.close();
                            });

                        }.bind(this)
                    }
                }
            }
        }, this.app, this.css);
        this.form.load();
        this.loadScriptEditor();
    },
    loadScriptEditor:function(){
        if( !this.workData )return;
        MWF.require("MWF.widget.JavascriptEditor", null, false);

        var workDataNode = this.formTableContainer.getElement('[item="workData"]');

        this.scriptEditor = new MWF.widget.JavascriptEditor(workDataNode, {
            "forceType": "ace",
            "option": { "mode" : "json" }
        });
        this.scriptEditor.load(function(){
            this.scriptEditor.setValue(JSON.stringify(this.workData, null, "\t"));
            this.scriptEditor.editor.setReadOnly(true);
            this.addEvent("afterResize", function () {
                this.resizeScript();
            }.bind(this))
            this.addEvent("queryClose", function () {

            }.bind(this))
            this.resizeScript();
        }.bind(this));
    },
    resizeScript: function () {
        var size = this.formTableContainer.getSize();
        var tableSize = this.formTableContainer.getElement('table').getSize();
        this.formTableContainer.getElement('[item="workData"]').setStyle("height", size.y - tableSize.y - 70);
        if (this.scriptEditor && this.scriptEditor.editor) this.scriptEditor.editor.resize();
    },
    getFileSize: function (size) {
        if (!size)
            return "";
        var num = 1024.00; //byte
        if (size < num)
            return size + "B";
        if (size < Math.pow(num, 2))
            return (size / num).toFixed(2) + "K"; //kb
        if (size < Math.pow(num, 3))
            return (size / Math.pow(num, 2)).toFixed(2) + "M"; //M
        if (size < Math.pow(num, 4))
            return (size / Math.pow(num, 3)).toFixed(2) + "G"; //G
    },

});
