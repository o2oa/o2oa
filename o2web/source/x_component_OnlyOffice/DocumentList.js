MWF.xAction.RestActions.Action["x_onlyofficefile_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action
});
MWF.xApplication.OnlyOffice.DocumentList = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "className" : ""
    },

    initialize: function(node, app, options){
        this.setOptions(options);
        this.app = app;
        this.path = "../x_component_OnlyOffice/$Main/" + this.options.style + "/";

        this.css = this.app.css;
        this.lp = this.app.lp;

        this.className = this.options.className;

        this.action = this.app.action;
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

        this.view = new MWF.xApplication.OnlyOffice[this.className].View(viewContainerNode, this, this, {
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
        var lp = MWF.xApplication.OnlyOffice.LP;
        this.fileterNode = new Element("div.operateNode", {
            "styles": this.css.fileterNode
        }).inject(this.topOperateNode);

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='remove'></td>" +
            "    <td styles='filterTableValue' item='upload'></td>" +
            "    <td styles='filterTableValue' item='create'></td>" +
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
                create: {
                    "value": "创建", type: "button", className: "filterButton", event: {
                        click: function (ev,node) {
                            this.switchTagFor( node.target );
                            node.stopPropagation();
                        }.bind(this)
                    }
                },
                upload: {
                    "value": "上传", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            o2.Actions.get("x_onlyofficefile_assemble_control").action.actions = {};
                            o2.Actions.get("x_onlyofficefile_assemble_control").action.actions.upload = {
                                "enctype": "formData",
                                "method": "POST",
                                "uri": "/jaxrs/onlyoffice/upload"
                            }


                            o2.require("o2.widget.Upload", null, false);
                            var upload = new o2.widget.Upload(this.app.content, {
                                "action": o2.Actions.get("x_onlyofficefile_assemble_control").action,
                                "method": "upload",
                                "accept" : ".doc,.docx,.ppt,.pptx,.xls,.xlsx",
                                "parameter": {
                                },
                                "data":{
                                },
                                "onCompleted": function(){
                                    this.form.app.notice("导入成功","success");
                                    this.loadView();
                                }.bind(this)
                            });
                            upload.load();

                        }.bind(this)
                    }
                }
            }
        }, this.app, this.css);
        this.fileterForm.load();
    },
    switchTagFor : function( el ){
        var _self = this;
        var node = this.tagForListNode;
        var parentNode = el;
        if(node){
            if(  node.getStyle("display") == "block" ){
                node.setStyle("display","none");
            }else{
                node.setStyle("display","block");
                node.position({
                    relativeTo: parentNode,
                    position: 'bottomCenter',
                    edge: 'upperCenter'
                });
            }
        }else{
            node = this.tagForListNode = new Element("div",{
                "styles" :  this.css.drownListNode
            }).inject(this.node);
            this.app.content.addEvent("click",function(){
                _self.tagForListNode.setStyle("display","none");
            });

            var actionList = [
                {
                    title : "文档",
                    event : function (e){
                        this.view._create("docx");
                        this.loadView();
                    }.bind(this)

                },
                {
                    title : "演示文稿",
                    event : function (e){
                        this.view._create("pptx");
                        this.loadView();
                    }.bind(this)

                },
                {
                    title : "表格",
                    event : function (e){
                        this.view._create("xlsx");
                        this.loadView();
                    }.bind(this)

                }
            ]
            node.setStyle("margin-left","30px");
            node.setStyle("margin-top","10px");
            actionList.each(function (action){
                var dNode = new Element("div",{
                    "text" : action.title,
                    "styles" : this.css.drownSelectNode,
                }).inject(node);

                dNode.addEvents({
                    "mouseover" : function(){ this.setStyles(_self.css.drownSelectNode_over); },
                    "mouseout" : function(){  this.setStyles(_self.css.drownSelectNode); },
                    "click" : function(e){
                        _self.tagForListNode.setStyle("display","none");
                        this.setStyles(_self.css.drownSelectNode);
                        if(action.event){
                            action.event(e);
                        }
                        e.stopPropagation();
                    }
                })
                node.position({
                    relativeTo: parentNode,
                    position: 'bottomCenter',
                    edge: 'upperCenter'
                });
            }.bind(this));

        }
    },
    loadFilter: function () {
        var lp = MWF.xApplication.OnlyOffice.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles": this.css.fileterNode
        }).inject(this.topContentNode);

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableTitle' lable='fileName'></td>" +
            "    <td styles='filterTableTitle' item='fileName'></td>" +
            "    <td styles='filterTableTitle' lable='creator'></td>" +
            "    <td styles='filterTableTitle' item='creator'></td>" +
            "    <td styles='filterTableTitle' lable='fileId'></td>" +
            "    <td styles='filterTableTitle' item='fileId'></td>" +
            "    <td styles='filterTableTitle' lable='docId'></td>" +
            "    <td styles='filterTableTitle' item='docId'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "    <td styles='filterTableValue' item='reset'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);


        this.form = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                fileName: {text: "文件名", "type": "text", "style": {"min-width": "100px"}},
                fileId: {text: "文件Id", "type": "text", "style": {"min-width": "100px"}},
                docId: {text: "关联文档id", "type": "text", "style": {"min-width": "100px"}},

                creator: {
                    "text": "创建人",
                    "type": "org",
                    "orgType": "identity",
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
                                } else if (key === "creator" && result[key].length > 0) {
                                    //result[key] = result[key][0].split("@")[1];
                                    result["creator"] = result[key][0];
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
MWF.xApplication.OnlyOffice.DocumentList.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.OnlyOffice[this.app.className].Document(this.viewNode, data, this.explorer, this, null, index);
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

        this.app.action.OnlyofficeAction.listPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            this._fixData(json.data);
            if (callback) callback(json);
        }.bind(this))

    },
    _fixData : function (dataList){
        dataList.each(function (data){

            data.fileSize = this.getFileSize(data.length);

            switch(data.category)
            {
                case "template":
                    data.categoryName = "模板";
                    break;
                case "x_processplatform_assemble_surface":
                    data.categoryName = "流程";
                    break;
                case "x_cms_assemble_control":
                    data.categoryName = "内容管理";
                    break;
                case "OfficeOnline":
                    data.categoryName = "在线协作";
                    break;
                default:
                    data.categoryName = "默认";
            }
        }.bind(this));
        return dataList;
    },
    getFileSize : function( size ){
        if (!size)
            return "-";
        var num = 1024.00; //byte
        if (size < num)
            return size + "B";
        if (size < Math.pow(num, 2))
            return (size / num).toFixed(2) + "K"; //kb
        if (size < Math.pow(num, 3))
            return (size / Math.pow(num, 2)).toFixed(2) + "M"; //M
        if (size < Math.pow(num, 4))
            return (size / Math.pow(num, 3)).toFixed(2) + "G"; //G
        return (size / Math.pow(num, 4)).toFixed(2) + "T";
    },
    _remove : function (data){
        this.app.action.OnlyofficeAction.delete(data.id,function (){},null,false);
    },
    _create: function (type) {
        var d = {
            "fileName" : "demo." + type,
            "fileType" : type
        }
        this.app.action.OnlyofficeAction.create(d,function (json){
            this._edit(json.data);
        }.bind(this),function (json){

        },false);
    },
    _open: function (data) {
        var jars = data.category;
        if(jars === "template") jars = "";
        var options = {
            "documentId": data.id,
            "mode":"view",
            "jars" : jars,
            "appId":  "OnlyOfficeEditor" + data.id
        };
        this.app.app.desktop.openApplication(null, "OnlyOfficeEditor", options);
    },
    _edit: function (data) {
        var jars = data.category;
        if(jars === "template") jars = "";
        var options = {
            "documentId": data.id,
            "mode":"edit",
            "jars" : jars,
            "appId":  "OnlyOfficeEditor" + data.id
        };
        this.app.app.desktop.openApplication(null, "OnlyOfficeEditor", options);
    },
    _download : function (data){
        var host = o2.Actions.getHost("x_onlyofficefile_assemble_control");

        window.open( host + "/x_onlyofficefile_assemble_control/jaxrs/onlyoffice/file/" + data.id + "/0");

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
MWF.xApplication.OnlyOffice.DocumentList.Document = new Class({
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
        this.view._edit(this.data);
    },
    download : function (){
        this.view._download(this.data);
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
