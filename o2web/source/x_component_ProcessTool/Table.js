MWF.xDesktop.requireApp("ProcessTool", "Task", null ,false);
MWF.require("MWF.widget.JavascriptEditor", null, false);

MWF.xApplication.ProcessTool.Table = new Class({
    Extends: MWF.xApplication.ProcessTool.Task,
    load: function(){
        this.node.empty();
        this.createTopNode();
        this.createContainerNode();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.addEvent("resize", this.setContentSizeFun);
        this.loadView({"table":this.table});

        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));
    },
    loadOperate: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.operateNode", {
            "styles": this.css.fileterNode
        }).inject(this.topOperateNode);

        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
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
                    "value": "添加", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            this.view._openDocument(this.getNewData(this.view.fieldList));


                        }.bind(this)
                    }
                },
            }
        }, this.app, this.css);
        this.fileterForm.load();
    },
    getNewData : function (fieldList){
        var newLineData = {};
        fieldList.each(function (field) {
            switch (field.type) {
                case "string":
                    newLineData[field.name] = "";
                    break;
                case "integer":
                case "long":
                case "double":
                    newLineData[field.name] = 0;
                    break;
                case "date":
                    var str = new Date().format("%Y-%m-%d");
                    newLineData[field.name] = str;
                    break;
                case "time":
                    var str = new Date().format("%H:%M:%S");
                    newLineData[field.name] = str;
                    break;
                case "dateTime":
                    var str = new Date().format("db");
                    newLineData[field.name] = str;
                    break;
                case "boolean":
                    newLineData[field.name] = true;
                    break;
                case "stringList":
                case "integerList":
                case "longList":
                case "doubleList":
                    newLineData[field.name] = [];
                    break;
                case "stringLob":
                    newLineData[field.name] = "";
                    break;
                case "stringMap":
                    newLineData[field.name] = {};
                    break;
            }
        });
        return newLineData;
    },
    loadFilter: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.tableObj = {};
        this.fileterNode = new Element("div.fileterNode", {
            "styles": this.css.fileterNode
        }).inject(this.topContentNode);

        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" + //style='width: 900px;'
            "<tr>" +

            "    <td styles='filterTableTitle' lable='application'></td>" +
            "    <td styles='filterTableValue' item='application'></td>" +

            "    <td styles='filterTableTitle' lable='table'></td>" +
            "    <td styles='filterTableValue' item='table'></td>" +


            "    <td styles='filterTableTitle' lable='searchKey'></td>" +
            "    <td styles='filterTableValue' item='searchKey'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "    <td styles='filterTableValue' item='reset'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);
        var appNameList = [];
        var appValueList = [];

        var tableNameList = [];
        var tableValueList = [];


        o2.Actions.load("x_query_assemble_designer").QueryAction.listAll(function (json) {
            var count = json.data.length;
            var index = 0 ;
            json.data.each(function (data) {

                index ++;
                o2.Actions.load("x_query_assemble_designer").TableAction.listWithQuery(data.id, function (json2) {

                    this.tableObj[data.id] = json2.data;
                    if (json2.data.length === 0) {

                    } else {
                        appNameList.push(data.name);
                        appValueList.push(data.id);

                        if(appValueList.length ==1){

                            json2.data.each(function(d2){
                                tableNameList.push(d2.name);
                                tableValueList.push(d2.name);
                            })
                        }
                    }
                    if(index === count){
                        this.form = new MForm(this.fileterNode, {}, {
                            style: "attendance",
                            isEdited: true,
                            itemTemplate: {

                                application: {text: "应用", "type": "select","selectValue" :appValueList, "selectText" :appNameList,
                                    event: {
                                        change: function (it) {
                                            var v = it.getValue();
                                            console.log(this.tableObj[v]);
                                            var values = [];
                                            this.tableObj[v].each(function(vv){
                                                values.push(vv.name);
                                            })
                                            it.parent.getItem("table").resetItemOptions(values,values);
                                            if(values.length>0){
                                                this.loadView({"table":values[0]})
                                            }
                                        }.bind(this)
                                    },
                                },

                                table: {text: "数据表", "type": "select","selectValue" :tableValueList,
                                    "selectText" :tableNameList,event: {
                                        change: function (it) {
                                            var v = it.getValue();
                                            this.loadView({"table":v})
                                        }.bind(this)
                                    },},


                                searchKey: {text: "where条件查询", "type": "text", "style": {"min-width": "450px"}},
                                action: {
                                    "value": lp.query, type: "button", className: "filterButton", event: {
                                        click: function () {
                                            var result = this.form.getResult(false, null, false, true, false);
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
                        }, this, this.css);
                        this.form.load();
                        // if(tableValueList.length>0){
                        //     this.loadView({"table":tableValueList[0]});
                        // }

                    }

                }.bind(this))

            }.bind(this));

        }.bind(this),null,false);


    },
});

MWF.xApplication.ProcessTool.Table.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.ProcessTool.Table.Document(this.viewNode, data, this.explorer, this, null, index);
    },
    ayalyseTemplate: function () {
        if(!this.filterData.table) return;

        // console.log(this.options.data)
        MWF.getJSON(this.options.templateUrl, function (json) {
            this.template = json;

            o2.Actions.load("x_query_assemble_designer").TableAction.get(this.filterData.table, function( json ){
                this.fieldList = JSON.parse(json.data.data).fieldList;
                this.fieldList.each(function (field){
                    var d = {
                        "name": field.name,
                        "head": {
                            "html": "<th styles='normalThNode' >" + (field.description!==""?field.description:field.name) + "</th>",
                            "width" : "100px"
                        },
                        "content": {
                            "html": "<td styles='normalTdNode' item='"+field.name+"'></td>",
                            "items": { }
                        }
                    }
                    d.content.items[field.name] = {};
                    this.template.items.push( d );
                }.bind(this));

            }.bind(this),null,false);

        }.bind(this), false)
    },
    _getCurrentPageData: function (callback, count, pageNum) {
        if(!this.filterData.table) return;
        this.table = this.filterData.table;
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

        if(pageNum === 1) {
            firstResult = 0;
        }else{
            firstResult = (pageNum - 1) * count ;
        }

        var whereArr = [];
        whereArr.push("1>0");

        if(this.filterData){

            if(this.filterData.searchKey && this.filterData.searchKey !== ""){
                whereArr.push(this.filterData.searchKey);
            }

        }

        var data = {};
        data["type"] = "select";
        data["data"] = "select o from " + this.table +" o where "+ whereArr.join(" and ") +" order by o.updateTime desc";
        data["countData"] = "select count(o) from "+ this.table +" o where " + whereArr.join(" and ");
        data["maxResults"] = count;
        data["firstResult"] = firstResult;


        o2.Actions.load("x_query_assemble_designer").TableAction.execute(this.table,data, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))
    },
    _remove: function (data) {
        o2.Actions.load("x_query_assemble_designer").TableAction.rowDelete(this.table, data.id ,function (json) {

        }.bind(this),null,false);
    },
    _create: function () {

    },
    _openDocument: function (documentData) {
        console.log(documentData)

        var div = new Element("div",{"style":"height:450px;border:1px solid rgb(230, 230, 230)"});
        var editor;
        var dlg = o2.DL.open({
            "title": "修改",
            "width": "800px",
            "height": "560px",
            "mask": true,
            "content": div,
            "container": null,
            "positionNode": this.app.content,
            "onQueryClose": function () {
                div.destroy();
            }.bind(this),
            "buttonList": [
                {
                    "text": "保存",
                    "action": function () {
                        var str = editor.editor.getValue();
                        o2.Actions.load("x_query_assemble_designer").TableAction.rowSave(this.table,JSON.parse(str),function (json){
                            dlg.close();
                            this.app.loadView();
                        }.bind(this),null,false);

                    }.bind(this)
                },
                {
                    "text": "关闭",
                    "action": function () {
                        dlg.close();
                    }.bind(this)
                }
            ],
            "onPostShow": function () {

                editor = new o2.widget.JavascriptEditor(div, { "forceType": "ace","option": { "mode": "json" } });
                editor.load(function () {
                    editor.editor.setValue(JSON.stringify(documentData, null, "\t"));
                });

                dlg.reCenter();
            }.bind(this)
        });

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

MWF.xApplication.ProcessTool.Table.Document = new Class({
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
        //var iconNode = itemNode.getElements("[item='icon']")[0];
        //MWF.getJSON( this.view.pictureUrlHost + iconNode.get("picUrl"), function( json ){
        //    iconNode.set("src", json.data.value);
        //} )
    },
    open: function () {
        this.view._openDocument(this.data);
    }
});
