MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.TableDesigner = MWF.xApplication.query.TableDesigner || {};
MWF.APPDTBD = MWF.xApplication.query.TableDesigner;

MWF.xDesktop.requireApp("query.TableDesigner", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("query.ViewDesigner", "ViewBase", null, false);
MWF.xDesktop.requireApp("query.TableDesigner", "Property", null, false);

MWF.xApplication.query.TableDesigner.Table = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.ViewBase,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "../x_component_query_TableDesigner/$Table/table.html"
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "../x_component_query_TableDesigner/$Table/";
        this.cssPath = "../x_component_query_TableDesigner/$Table/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.designer = designer;
        this.data = data;
        if (!this.data.draftData) this.data.draftData = {};
        this.parseData();

        this.node = this.designer.designNode;
        this.areaNode = new Element("div", {"styles": {"height": "100%", "overflow": "auto"}});

        this.propertyListNode = this.designer.propertyDomArea;

        if(this.designer.application) this.data.applicationName = this.designer.application.name;
        if(this.designer.application) this.data.application = this.designer.application.id;

        this.isNewTable = (this.data.id) ? false : true;

        this.items = [];
        this.view = this;
        this.queryView = null;

        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },
    parseData: function(){
        this.json = this.data;
    },
    load : function(){
        this.setAreaNodeSize();
        this.designer.addEvent("resize", this.setAreaNodeSize.bind(this));
        this.areaNode.inject(this.node);

        this.designer.viewListAreaNode.getChildren().each(function(node){
            var table = node.retrieve("table");
            if (table.id==this.data.id){
                if (this.designer.currentListViewItem){
                    this.designer.currentListViewItem.setStyles(this.designer.css.listViewItem);
                }
                node.setStyles(this.designer.css.listViewItem_current);
                this.designer.currentListViewItem = node;
                this.lisNode = node;
            }
        }.bind(this));

        this.domListNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.designer.propertyDomArea);
        this.designer.propertyTitleNode.set("text", this.designer.lp.clumn);
        this.designer.propertyDomPercent = 0.5;
        this.designer.loadPropertyContentResize();
        this.createColumnEditTable();

        this.loadView();
        this.refreshNode.setStyles(this.css.tableRunNode);
        if (!this.data.buildSuccess){
            this.refreshNode.hide();
            this.addColumnNode.hide();
        }

        this.selected();
        this.setEvent();

        this.setViewWidth();
        this.designer.addEvent("resize", this.setViewWidth.bind(this));

        this.checkToolbars();
    },
    setEvent: function(){
        this.areaNode.addEvent("click", this.selected.bind(this));
        this.refreshNode.addEvent("click", function(e){
            this.loadViewData();
            e.stopPropagation();
        }.bind(this));
        this.addColumnNode.addEvent("click", function(e){
            debugger;
            this.addLine();
            e.stopPropagation();
        }.bind(this));
    },
    addLine: function(){
        var data = this.getNewData();
        this.createNewLineDlg(data);
    },
    createNewLineDlg: function(data){
        var div = new Element("div", {"styles": {"margin": "10px 10px 0px 10px", "padding": "5px", "height": "400px", "width": "440px", "overflow": "hidden"}});
        var options ={
            "content": div,
            "title": this.designer.lp.addLine,
            "container": this.designer.content,
            "width": 500,
            "mask": false,
            "height": 530,
            "buttonList": [
                {
                    "text": this.designer.lp.ok,
                    "action": function(){
                        this.saveNewLine(dlg);
                    }.bind(this)
                },
                {
                    "text": this.designer.lp.cancel,
                    "action": function(){dlg.close();}.bind(this),
                    "styles": {
                        "border": "1px solid #999",
                        "background-color": "#f3f3f3",
                        "color": "#666666",
                        "height": "30px",
                        "border-radius": "5px",
                        "min-width": "80px",
                        "margin": "10px 5px"
                    }
                }
            ],
            "onResize": function(){
                var size = dlg.content.getSize();
                var width = size.x-60;
                var height = size.y - 30;
                div.setStyles({
                    "width": ""+width+"px",
                    "height": ""+height+"px",
                });
            }
        }
        var dlg = o2.DL.open(options);

        o2.require("o2.widget.JavascriptEditor", function(){
            dlg.editor = new o2.widget.JavascriptEditor(div, {"option": {"mode": "json"}});
            dlg.editor.load(function(){
                debugger;
                dlg.editor.editor.setValue(JSON.stringify(data, null, "\t"));
            }.bind(this));
        }.bind(this), false);

        return dlg;
    },
    saveNewLine: function(dlg){
        var str = dlg.editor.editor.getValue();
        try{
            var data = JSON.parse(str);
            this.designer.actions.insertRow(this.data.id, data, function(){
                if (this.lastSelectJPQL) this.runJpql(this.lastSelectJPQL);
                this.designer.notice(this.designer.lp.newLineSuccess, "success");
                dlg.close();
            }.bind(this));
        }catch(e){
            this.designer.notice(this.designer.lp.newLineSuccess, "error");
        }

    },
    getNewData: function(){
        var data = JSON.parse(this.data.data);
        var newLineData = {};
        data.fieldList.each(function(field){
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
        }.bind(this));
        return newLineData;
    },
    checkToolbars: function(){
        if (this.designer.toolbar){
            var buildBtn = this.designer.toolbar.childrenButton[1];
            var draftBtn = this.designer.toolbar.childrenButton[2];

            buildBtn.setDisable(true);
            draftBtn.setDisable(true);

            if (!this.data.isNewTable) buildBtn.setDisable(false);
            if (this.data.status=="build") draftBtn.setDisable(false);
        }
    },
    createColumnEditTable: function(){
        this.columnListTable = new Element("table", {"styles": this.css.columnListTable}).inject(this.domListNode);
        this.columnListHeaderTr = this.columnListTable.insertRow(-1).setStyles(this.css.columnListTr);
        this.columnListHeaderTr.insertCell().setStyles(this.css.columnListHeaderTd).set("text", this.designer.lp.name);
        this.columnListHeaderTr.insertCell().setStyles(this.css.columnListHeaderTd).set("text", this.designer.lp.description);
        this.columnListHeaderTr.insertCell().setStyles(this.css.columnListHeaderTd).set("text", this.designer.lp.type);

        this.columnListEditTr = this.columnListTable.insertRow(-1).setStyles(this.css.columnListEditTr);
        var td = this.columnListEditTr.insertCell().setStyles(this.css.columnListTd);
        this.columnListEditNameInput = new Element("input", {"styles": this.css.columnListEditInput}).inject(td);

        td = this.columnListEditTr.insertCell().setStyles(this.css.columnListTd);
        this.columnListEditDescriptionInput = new Element("input", {"styles": this.css.columnListEditInput}).inject(td);

        td = this.columnListEditTr.insertCell().setStyles(this.css.columnListTd);
        this.columnListEditTypeSelect = new Element("select", {"styles": this.css.columnListEditSelect}).inject(td);
        //var options = '<option value=""></option>';
        var options = '<option value="string">string</option>';
        options += '<option value="integer">integer</option>';
        options += '<option value="long">long</option>';
        options += '<option value="double">double</option>';
        options += '<option value="boolean">boolean</option>';
        options += '<option value="date">date</option>';
        options += '<option value="time">time</option>';
        options += '<option value="dateTime">dateTime</option>';
        options += '<option value="stringList">stringList</option>';
        options += '<option value="integerList">integerList</option>';
        options += '<option value="longList">longList</option>';
        options += '<option value="doubleList">doubleList</option>';
        options += '<option value="booleanList">booleanList</option>';
        options += '<option value="stringLob">stringLob</option>';
        options += '<option value="stringMap">stringMap</option>';
        this.columnListEditTypeSelect.set("html", options);

        this.columnListEditTypeSelect.addEvents({
            "change": function(e){
                this.checkAddColumn();
            }.bind(this)
        });
        this.columnListEditNameInput.addEvents({
            "keydown": function(e){
                if (e.code==13) this.checkAddColumn();
            }.bind(this)
        });
        this.columnListEditDescriptionInput.addEvents({
            "keydown": function(e){
                if (e.code==13) this.checkAddColumn();
            }.bind(this)
        });
    },
    checkColumnName: function(name){
        var rex = /^\d+|\.|\#|\s|\@|\&|\*|\(|\)|\=|\+|\!|\^|\$|\%|\;|\"|\{|\}|\[|\]|\||\\|\,|\.|\?|\/|\:|\;|\'|\"|\<|\>/g;
        if (rex.test(name)){
            this.designer.notice(this.designer.lp.errorName, "error");
            return false;
        }
        return true;
    },
    checkAddColumn: function(){
        var name = this.columnListEditNameInput.get("value");
        var description = this.columnListEditDescriptionInput.get("value");
        var type = this.columnListEditTypeSelect.options[this.columnListEditTypeSelect.selectedIndex].value;
        if (name && this.checkColumnName(name)){
            if (!this.json.draftData.fieldList) this.json.draftData.fieldList = [];
            var columnNames = this.json.draftData.fieldList.map(function(item){ return item.name; });
            if ((columnNames.indexOf(name)!=-1)){
                this.designer.notice(this.designer.lp.duplicateName, "error");
                this.columnListEditNameInput.focus();
            }else{
                var o = {
                    "name": name,
                    "description": description,
                    "type": type
                }
                this.addColumn(o);
                this.columnListEditNameInput.set("value", "");
                this.columnListEditDescriptionInput.set("value", "");
                this.columnListEditNameInput.focus();
            }
        }
    },
    showProperty: function(){
        if (!this.property){
            this.property = new MWF.xApplication.query.TableDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
                "path": this.options.propertyPath,
                "onPostLoad": function(){
                    this.property.show();
                }.bind(this)
            });
            this.property.load();
        }else{
            this.property.show();
        }
    },
    addColumn: function(data){
        var json;
        if (!data){
            if (!this.json.draftData.fieldList) this.json.draftData.fieldList = [];
            var columnNames = this.json.draftData.fieldList.map(function(item){ return item.name; });
            var name = "column";
            var i=1;
            while(columnNames.indexOf(name)!=-1){
                name = "column"+i;
                i++;
            }
            json = {
                "name": name,
                "type":"string",
                "description": this.designer.lp.newColumn
            };
        }else{
            json = data;
        }

        this.json.draftData.fieldList.push(json);
        var column = new MWF.xApplication.query.TableDesigner.Table.Column(json, this);
        this.items.push(column);
        column.selected();

        if (this.viewContentTableNode){
            var trs = this.viewContentTableNode.getElements("tr");
            trs.each(function(tr){
                new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr)
            }.bind(this));
            //this.setContentColumnWidth();
        }
        this.setViewWidth();
        this.addColumnNode.scrollIntoView(true);


        //new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 0}).toRight();
    },

    loadViewColumns: function(){
        if (this.json.draftData.fieldList) {
            this.json.draftData.fieldList.each(function (json) {
                this.items.push(new MWF.xApplication.query.TableDesigner.Table.Column(json, this));
            }.bind(this));
        }
    },
    saveSilence: function(callback){
        debugger;
        if (!this.data.name){
            this.designer.notice(this.designer.lp.inputTableName, "error");
            return false;
        }
        if( this.data.status !== "build" ){
            var reg = /^[A-Za-z]/;
            if( !reg.test(this.data.name) ){
                this.designer.notice(this.designer.lp.tableNameNotStartWithLetter, "error");
                return false;
            }

            var reg2 = /^[A-Za-z0-9]+$/;
            if( !reg2.test(this.data.name) ){
                this.designer.notice(this.designer.lp.tableNameNotBeLetterAndNumber, "error");
                return false;
            }
        }
        if (!this.json.draftData.fieldList.length){
            this.designer.notice(this.designer.lp.errorFieldList, "error");
            return false;
        }
        this.designer.actions.saveTable(this.data, function(json){
            this.data.id = json.data.id;
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
            }
            this.checkToolbars();
            if (callback) callback();
        }.bind(this));
    },
    save: function(callback){
        debugger;
        if (!this.data.name){
            this.designer.notice(this.designer.lp.inputTableName, "error");
            return false;
        }
        if( this.data.status !== "build" ){
            var reg = /^[A-Za-z]/;
            if( !reg.test(this.data.name) ){
                this.designer.notice(this.designer.lp.tableNameNotStartWithLetter, "error");
                return false;
            }
            if( this.data.alias && !reg.test(this.data.alias) ){
                this.designer.notice(this.designer.lp.tableAliasNotStartWithLetter, "error");
                return false;
            }


            var reg2 = /^[A-Za-z0-9]+$/;
            if( !reg2.test(this.data.name) ){
                this.designer.notice(this.designer.lp.tableNameNotBeLetterAndNumber, "error");
                return false;
            }
            if( this.data.alias && !reg2.test(this.data.alias) ){
                this.designer.notice(this.designer.lp.tableAliasNotBeLetterAndNumber, "error");
                return false;
            }
        }
        if (!this.json.draftData.fieldList.length){
            this.designer.notice(this.designer.lp.errorFieldList, "error");
            return false;
        }
        this.designer.actions.saveTable(this.data, function(json){
            this.designer.notice(this.designer.lp.save_success, "success", this.node, {"x": "left", "y": "bottom"});

            this.data.id = json.data.id;
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
            }
            this.checkToolbars();
            if (callback) callback();
        }.bind(this));
    },
    unSelected: function(){
        this.currentSelectedModule = null;
        return true;
    },
    statusBuild: function(e){
        var _self = this;
        if (!e) e = this.node;
        this.designer.confirm("warn", e, MWF.APPDTBD.LP.statusBuildTitle, MWF.APPDTBD.LP.statusBuildInfor, 420, 120, function(){
            _self.designer.actions.statusBuild(_self.data.id, function(json){
                debugger;
                this.designer.notice(this.designer.lp.statusBuild_success, "success", this.node, {"x": "left", "y": "bottom"});
                this.designer.actions.getTable(json.data.id, function(tjson){
                    this.data.status = tjson.data.status;
                    this.checkToolbars();
                }.bind(this));
            }.bind(_self));
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    statusDraft: function(e){
        var _self = this;
        if (!e) e = this.node;
        this.designer.confirm("warn", e, MWF.APPDTBD.LP.statusDraftTitle, {"html": MWF.APPDTBD.LP.statusDraftInfor}, 450, 120, function(){
            this.close();
            _self.designer.confirm("warn", e, MWF.APPDTBD.LP.statusDraftTitle, {"html": MWF.APPDTBD.LP.statusDraftInforAgain}, 480, 120, function(){
                _self.designer.actions.statusDraft(_self.data.id, function(json){
                    this.designer.notice(this.designer.lp.statusDraft_success, "success", this.node, {"x": "left", "y": "bottom"});
                    this.designer.actions.getTable(json.data.id, function(tjson){
                        this.data.status = tjson.data.status;
                        this.checkToolbars();
                    }.bind(this));
                }.bind(_self));
                this.close();
            }, function(){
                this.close();
            }, null);

        }, function(){
            this.close();
        }, null);
    },
    buildAllView: function(e){
        var _self = this;
        if (!e) e = this.node;
        this.designer.confirm("warn", e, MWF.APPDTBD.LP.buildAllViewTitle, MWF.APPDTBD.LP.buildAllViewInfor, 480, 120, function(){
            _self.designer.actions.buildAllTable(function(json){
                this.designer.notice(this.designer.lp.buildAllView_success, "success", this.node, {"x": "left", "y": "bottom"});
            }.bind(_self));
            this.close();
        }, function(){
            this.close();
        }, null);
    },

    buildAllView: function(e){
        var _self = this;
        if (!e) e = this.node;
        this.designer.confirm("warn", e, MWF.APPDTBD.LP.buildAllViewTitle, MWF.APPDTBD.LP.buildAllViewInfor, 480, 120, function(){
            _self.designer.actions.buildAllTable(function(json){
                this.designer.notice(this.designer.lp.buildAllView_success, "success", this.node, {"x": "left", "y": "bottom"});
            }.bind(_self));
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    tableClear: function(e){
        var _self = this;
        if (!e) e = this.node;
        this.designer.confirm("warn", e, MWF.APPDTBD.LP.tableClearTitle, MWF.APPDTBD.LP.tableClearInfo, 480, 120, function(){
            _self.designer.actions.deleteAllRow(_self.data.id,function(json){
                this.designer.notice(this.designer.lp.tableClear_success, "success", this.node, {"x": "left", "y": "bottom"});
            }.bind(_self));
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    tableImplode: function(e){
        var _self = this;
        if (!e) e = this.node;
        this.designer.confirm("warn", e, MWF.APPDTBD.LP.tableImplodeTitle, MWF.APPDTBD.LP.tableImplodeInfo, 480, 120, function(){
            _self.implodeLocal();
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    tableExcelImplode: function(e){
        var _self = this;
        if (!e) e = this.node;
        this.designer.confirm("warn", e, MWF.APPDTBD.LP.tableExcelImplodeTitle, MWF.APPDTBD.LP.tableExcelImplodeInfo, 480, 120, function(){
            _self.implodeExcelLocal();
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    implodeExcelLocal : function (){
        var ExcelUtils = new MWF.xApplication.query.TableDesigner.Table.ExcelUtils();
        var uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" accept=\"*\" />";
        uploadFileAreaNode.set("html", html);

        var fileUploadNode = uploadFileAreaNode.getFirst();
        fileUploadNode.addEvent("change", function () {
            var files = fileNode.files;
            if (files.length) {
                var file = files.item(0);
                //第三个参数是日期的列
                ExcelUtils.import( file, function(json){
                    console.log(JSON.stringify(json));

                    this.designer.actions.rowSave(this.data.id,json[0],function(json){
                        this.designer.notice(this.designer.lp.tableImplode_success, "success", this.node, {"x": "left", "y": "bottom"});
                    }.bind(this));
                }.bind(this) );

            }
        }.bind(this));
        var fileNode = uploadFileAreaNode.getFirst();
        fileNode.click();
    },
    implodeLocal: function(){
        if (!this.uploadFileAreaNode){
            this.uploadFileAreaNode = new Element("div");
            var html = "<input name=\"file\" type=\"file\" accept=\".json\"/>";
            this.uploadFileAreaNode.set("html", html);
            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", this.implodeLocalFile.bind(this));
        }else{
            if (this.fileUploadNode) this.fileUploadNode.destroy();
            this.uploadFileAreaNode.empty();
            var html = "<input name=\"file\" type=\"file\" accept=\".json\"/>";
            this.uploadFileAreaNode.set("html", html);
            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", this.implodeLocalFile.bind(this));
        }
        this.fileUploadNode.click();
    },
    implodeLocalFile: function(){
        var files = this.fileUploadNode.files;
        if (files.length){
            var file = files[0];
            var reader = new FileReader();
            reader.readAsText(file);
            var _self = this;
            reader.onload = function(){
                var data = JSON.parse(this.result);

                _self.designer.actions.rowSave(_self.data.id,data,function(json){
                    this.designer.notice(this.designer.lp.tableImplode_success, "success", this.node, {"x": "left", "y": "bottom"});
                }.bind(_self));

            };
        }
    },
    tableExplode: function(e){
        var _self = this;
        if (!e) e = this.node;
        this.designer.confirm("warn", e, MWF.APPDTBD.LP.tableExplodeTitle, MWF.APPDTBD.LP.tableExplodeInfo, 480, 120, function(){

            var url =  _self.designer.actions.action.address + _self.designer.actions.action.actions.exportRow.uri

            url = url.replace("{tableFlag}",_self.data.id);
            url = url.replace("{count}",1000);
            window.open(o2.filterUrl(url))
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    tableExcelExplode: function(e){
        var ExcelUtils = new MWF.xApplication.query.TableDesigner.Table.ExcelUtils();
        var _self = this;
        if (!e) e = this.node;
        this.designer.confirm("warn", e, MWF.APPDTBD.LP.tableExcelExplodeTitle, MWF.APPDTBD.LP.tableExplodeInfo, 480, 120, function(){
            var fieldList = JSON.parse(_self.view.data.data).fieldList;
            var fieldArr = [];
            var resultArr = [];
            fieldList.each(function (field){
                fieldArr.push(field.name);
                resultArr.push("o." + field.name);
            })
            var array = [fieldArr];
            var jpql = {
                "data": "select " + resultArr.join() + " from " + _self.data.name + " o",
                "type": "select",
                "firstResult": 0,
                "maxResults": 1000
            }
            _self.designer.actions.executeJpql(_self.data.id, jpql, function(json){
                json.data.each(function (d){
                    var f = [];
                    if(o2.typeOf(json.data[0])==="string"){
                        f = [d];
                    }else {
                        d.each(function (dd){
                            if(!dd){
                                f.push("");
                            }else {
                                f.push(dd);
                            }
                        })
                    }
                    array.push(f);
                })
                ExcelUtils.export(array,  MWF.APPDTBD.LP.exportExcelFileName +(new Date).format("db"));
            }.bind(this));
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    setContentHeight: function(){
        var size = this.areaNode.getSize();
        var titleSize = this.viewTitleNode.getSize()
        debugger;
        var height = size.y-titleSize.y-2;
        // if (this.jpqlAreaNode){
        //     var jpqlSize = this.jpqlAreaNode.getComputedSize();
        //     height = height - jpqlSize.totalHeight;
        // }

        this.viewContentScrollNode.setStyle("height", height);

        var contentSize = this.viewContentBodyNode.getSize();
        if (height<contentSize.y) height = contentSize.y+10;

        this.viewContentNode.setStyle("height", height);
        this.contentLeftNode.setStyle("height", height);
        this.contentRightNode.setStyle("height", height);
        //this.viewContentBodyNode.setStyle("min-height", height);
    },
    createJpqlAreaNode: function(callback){
        if (!this.jpqlAreaNode){
            this.viewTitleNode.setStyles(this.css.viewTitleNode_run);
            this.refreshNode.setStyles(this.css.tableRunNode_run);
            this.addColumnNode.setStyles(this.css.addColumnNode_run);

            this.jpqlAreaNode = new Element("div", {
                "styles": this.css.jpqlAreaNode
            }).inject(this.viewTitleContentNode, "top");

            this.jpqlEditor = new MWF.xApplication.query.TableDesigner.Table.JPQLRunner(this.jpqlAreaNode, this.refreshNode, this);
            this.jpqlEditor.load(function(){
                this.jpqlEditor.setJpql("slect", "select o from "+this.data.name+" o", 0, 50);
                if (callback) callback();
            }.bind(this));
            this.setContentHeight();
        }else{
            if (callback) callback();
        }
    },
    runJpql: function(jpql){
        this.designer.actions.executeJpql(this.data.id, jpql, function(json){
            this.loadViewMask.hide();

            if (jpql.type!="select"){
                this.designer.notice(this.designer.lp.jpqlRunSuccess, "success");
                if (this.lastSelectJPQL) this.runJpql(this.lastSelectJPQL);
            }else{
                this.lastSelectJPQL = jpql;
                this.viewContentBodyNode.empty();
                this.viewContentTableNode = new Element("table", {
                    "styles": this.css.viewContentTableNode,
                    "border": "0px",
                    "cellPadding": "0",
                    "cellSpacing": "0"
                }).inject(this.viewContentBodyNode);

                if (json.data.length){
                    json.data.each(function(line, idx){
                        new MWF.xApplication.query.TableDesigner.Table.DataLine(line, this);
                    }.bind(this));
                    this.setContentColumnWidth();
                    this.setContentHeight();
                }
            }

        }.bind(this), function(xhr, text, error){
            this.loadViewMask.hide();
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
        }.bind(this));
    },
    loadViewData: function(){
        if (this.data.buildSuccess){
            if (this.data.id){
                o2.require("o2.widget.Mask", null, false);
                this.loadViewMask = new o2.widget.Mask();
                this.loadViewMask.loadNode(this.viewAreaNode);

                this.createJpqlAreaNode(function(){
                    var jpql = this.jpqlEditor.getJpql();
                    this.runJpql(jpql);
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.query.TableDesigner.Table.Column = new Class({
    Extends:MWF.xApplication.query.ViewDesigner.ViewBase.Column,
    initialize: function(json, view, next){
        this.propertyPath = "../x_component_query_TableDesigner/$Table/column.html";
        this.view = view;
        this.json = json;
        this.next = next;
        this.css = this.view.css;
        this.content = this.view.viewTitleTrNode;
        this.domListNode = this.view.domListNode;
        this.load();
    },
    createDomListItem: function(){
        //this.view.columnListEditTr;
        var idx = this.view.columnListTable.rows.length;
        this.listNode = this.view.columnListTable.insertRow(idx-1).setStyles(this.css.cloumnListNode);
        this.listNode.insertCell().setStyles(this.css.columnListTd).set("text", this.json.name);
        this.listNode.insertCell().setStyles(this.css.columnListTd).set("text", this.json.description);
        this.listNode.insertCell().setStyles(this.css.columnListTd).set("text", this.json.type);
        this.resetTextNode();
    },
    selected: function(){
        debugger;
        if (this.view.currentSelectedModule){
            if (this.view.currentSelectedModule==this){
                return true;
            }else{
                if (!this.view.currentSelectedModule.unSelected()) return true;
            }
        }
        this.node.setStyles(this.css.viewTitleColumnNode_selected);
        this.listNode.setStyles(this.css.cloumnListNode_selected);

        new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 100}).toElementEdge(this.node);
        new Fx.Scroll(this.view.designer.propertyDomArea, {"wheelStops": false, "duration": 100}).toElement(this.listNode);

        this.view.currentSelectedModule = this;
        this.isSelected = true;
        this._showActions();
        this.showProperty();
    },
    unSelected: function(){
        if (this.checkColumn()){
            this.resetTextNode();
            this.view.currentSelectedModule = null;
            //this.node.setStyles(this.css.viewTitleColumnNode);
            if (this.isError){
                this.node.setStyles(this.css.viewTitleColumnNode_error)
            }else{
                this.node.setStyles(this.css.viewTitleColumnNode)
            }

            this.listNode.setStyles(this.css.cloumnListNode);
            this.isSelected = false;
            this._hideActions();
            this.hideProperty();
            return true;
        }
        return false;
    },
    checkColumn: function(){
        debugger;
        var tds = this.listNode.getElements("td");
        var nameInput = tds[0].getFirst();
        var descriptionInput = tds[1].getFirst();
        var select = tds[2].getFirst();

        if (nameInput && nameInput.tagName.toString().toLowerCase()=="input"){
            var name = tds[0].getFirst().get("value");
            var description = tds[1].getFirst().get("value");
            var type = tds[2].getFirst().options[tds[2].getFirst().selectedIndex].value;

            if (name && name!==this.json.name && this.view.checkColumnName(name)){
                if (!this.view.json.draftData.fieldList) this.view.json.draftData.fieldList = [];
                var columnNames = this.view.json.draftData.fieldList.map(function(item){ return item.name; });
                if ((columnNames.indexOf(name)!=-1)){
                    this.view.designer.notice(this.view.designer.lp.duplicateName, "error");
                    tds[0].getFirst().focus();
                    return false;
                }else{
                    this.json.name = name;
                    this.json.description = description;
                    this.json.type = type;
                    return true;
                }
            }else if (name==this.json.name){
                this.json.name = name;
                this.json.description = description;
                this.json.type = type;
                return true;
            }else{
                this.view.designer.notice(this.view.designer.lp.inputName, "error");
                tds[0].getFirst().focus();
                return false;
            }
        }
        return true;
    },

    showProperty: function(){
        var tds = this.listNode.getElements("td");
        tds[0].empty();
        tds[1].empty();
        tds[2].empty();
        var nameInput = new Element("input", {"styles": this.css.columnListEditModifyInput, "value": this.json.name}).inject(tds[0]);
        var descriptionInput = new Element("input", {"styles": this.css.columnListEditModifyInput, "value": this.json.description}).inject(tds[1]);

        var select = new Element("select", {"styles": this.css.columnListEditModifySelect}).inject(tds[2]);
        //var options = '<option value=""></option>';
        var options = '<option '+((this.json.type=='string') ? 'selected' : '')+' value="string">string</option>';
        options += '<option '+((this.json.type=='integer') ? 'selected' : '')+' value="integer">integer</option>';
        options += '<option '+((this.json.type=='long') ? 'selected' : '')+' value="long">long</option>';
        options += '<option '+((this.json.type=='double') ? 'selected' : '')+' value="double">double</option>';
        options += '<option '+((this.json.type=='boolean') ? 'selected' : '')+' value="boolean">boolean</option>';
        options += '<option '+((this.json.type=='date') ? 'selected' : '')+' value="date">date</option>';
        options += '<option '+((this.json.type=='time') ? 'selected' : '')+' value="time">time</option>';
        options += '<option '+((this.json.type=='dateTime') ? 'selected' : '')+' value="dateTime">dateTime</option>';
        options += '<option '+((this.json.type=='stringList') ? 'selected' : '')+' value="stringList">stringList</option>';
        options += '<option '+((this.json.type=='integerList') ? 'selected' : '')+' value="integerList">integerList</option>';
        options += '<option '+((this.json.type=='longList') ? 'selected' : '')+' value="longList">longList</option>';
        options += '<option '+((this.json.type=='doubleList') ? 'selected' : '')+' value="doubleList">doubleList</option>';
        options += '<option '+((this.json.type=='booleanList') ? 'selected' : '')+' value="booleanList">booleanList</option>';
        options += '<option '+((this.json.type=='stringLob') ? 'selected' : '')+' value="stringLob">stringLob</option>';
        options += '<option '+((this.json.type=='stringMap') ? 'selected' : '')+' value="stringMap">stringMap</option>';
        select.set("html", options);

        nameInput.focus();

        select.addEvents({
            "change": function(e){
                if (this.checkColumn()) this.resetColumnTextNode();
            }.bind(this),
            "click": function(e){e.stopPropagation()}
        });
        nameInput.addEvents({
            "keydown": function(e){ if (e.code==13) if (this.checkColumn()) this.resetColumnTextNode(); }.bind(this),
            "change": function(e){ if (this.checkColumn()) this.resetColumnTextNode(); }.bind(this),
            "click": function(e){e.stopPropagation()}
        });
        descriptionInput.addEvents({
            "keydown": function(e){ if (e.code==13) if (this.checkColumn()) this.resetColumnTextNode(); }.bind(this),
            "change": function(e){ if (this.checkColumn()) this.resetColumnTextNode(); }.bind(this),
            "click": function(e){e.stopPropagation()}
        });
    },
    hideProperty: function(){
        var tds = this.listNode.getElements("td");
        tds[0].empty().set("text", this.json.name);
        tds[1].empty().set("text", this.json.description);
        tds[2].empty().set("text", this.json.type);
    },
    resetColumnTextNode: function(){
        var text = (this.json.description) ? this.json.name+"("+this.json.description+")" : this.json.name;
        this.textNode.set("text", text);
    },
    resetTextNode: function(){
        var text = (this.json.description) ? this.json.name+"("+this.json.description+")" : this.json.name;

        this.textNode.set("text", text);
        this.listNode.getFirst().set("text", this.json.name);
        this.listNode.getFirst().getNext().set("text", this.json.description);
        this.listNode.getLast().set("text", this.json.type);
    },
    "delete": function(e){
        var _self = this;
        if (!e) e = this.node;
        this.view.designer.confirm("warn", e, MWF.APPDTBD.LP.deleteColumnTitle, MWF.APPDTBD.LP.deleteColumn, 300, 120, function(){
            _self.destroy();

            this.close();
        }, function(){
            this.close();
        }, null);
    },
    destroy: function(){
        if (this.view.currentSelectedModule==this) this.view.currentSelectedModule = null;
        if (this.actionArea) this.actionArea.destroy();
        if (this.listNode) this.listNode.destroy();
        if (this.property) this.property.propertyContent.destroy();

        var idx = this.view.items.indexOf(this);

        if (this.view.viewContentTableNode){
            var trs = this.view.viewContentTableNode.getElements("tr");
            trs.each(function(tr){
                tr.deleteCell(idx);
            }.bind(this));
        }

        if (this.view.json.draftData.fieldList) this.view.json.draftData.fieldList.erase(this.json);
        this.view.items.erase(this);
        this.areaNode.destroy();
        this.view.selected();
        this.view.setViewWidth();
        MWF.release(this);
        delete this;
    },
    addColumn: function(e, data){
        var json;
        if (!data){
            if (!this.view.json.draftData.fieldList) this.view.json.draftData.fieldList = [];
            var columnNames = this.view.json.draftData.fieldList.map(function(item){ return item.name; });
            var name = "column";
            var i=1;
            while(columnNames.indexOf(name)!=-1){
                name = "column"+i;
                i++;
            }
            json = {
                "name": name,
                "type":"string",
                "description": this.view.designer.lp.newColumn
            };
        }else{
            json = data;
        }

        this.view.json.draftData.fieldList.push(json);
        var column = new MWF.xApplication.query.TableDesigner.Table.Column(json, this.view, this);
        this.view.items.push(column);
        column.selected();

        if (this.view.viewContentTableNode){
            var trs = this.view.viewContentTableNode.getElements("tr");
            trs.each(function(tr){
                new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr)
            }.bind(this));
            //this.setContentColumnWidth();
        }
        this.view.setViewWidth();
        this.view.addColumnNode.scrollIntoView(true);
    },

    _createIconAction: function(){
        if (!this.actionArea){
            this.actionArea = new Element("div", {"styles": this.css.actionAreaNode}).inject(this.view.areaNode, "after");
            this._createAction({
                "name": "add",
                "icon": "add.png",
                "event": "click",
                "action": "addColumn",
                "title": MWF.APPDVD.LP.action.add
            });
            this._createAction({
                "name": "delete",
                "icon": "delete1.png",
                "event": "click",
                "action": "delete",
                "title": MWF.APPDVD.LP.action["delete"]
            });
        }
    }
});

MWF.xApplication.query.TableDesigner.Table.DataLine = new Class({
    initialize: function(data, table){
        this.table = table;
        this.lineData = data;
        this.tableData = this.table.data;
        this.tableContentTableNode = this.table.viewContentTableNode;
        this.css = this.table.css;
        this.load();
    },

    load: function(){
        this.tr = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.tableContentTableNode);

        this.tableData.draftData.fieldList.each(function(c, i){
            var d = this.lineData[c.name];
            var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.tr);
            td.store("field", c);

            if (d!=undefined){

                var t = o2.typeOf(d);
                switch (t){
                    case "array":
                        td.store("data", d);
                        td.set("html", "<div style='background-color:#dddddd; cursor: pointer;float: left; padding: 3px; font-size: 10px;'>[...]</div>");
                        break;
                    case "object":
                        td.store("data", d);
                        td.set("html", "<div style='background-color:#dddddd; cursor: pointer;float: left; padding: 3px; font-size: 10px;'>{...}</div>");
                        break;
                    default:
                        td.set("text", d);
                }
            }else{
                switch (c.type){
                    case "stringList":
                    case "integerList":
                    case "longList":
                    case "doubleList":
                    case "booleanList":
                        td.set("html", "<div style='background-color:#dddddd; cursor: pointer;float: left; padding: 3px; font-size: 10px;'>[...]</div>");
                        break;
                    case "stringLob":
                        td.set("html", "<div style='background-color:#dddddd; cursor: pointer;float: left; padding: 3px; font-size: 10px;'>...</div>");
                        break;
                    case "stringMap":
                        td.set("html", "<div style='background-color:#dddddd; cursor: pointer;float: left; padding: 3px; font-size: 10px;'>{...}</div>");
                        break;
                    default:
                        td.set("text", "");
                }
            }

            if (td.getFirst()){
                td.getFirst().addEvent("click", function(e){
                    this.getFieldValue(e.target.getParent());
                }.bind(this));
            }
        }.bind(this));
    },
    createObjectValueDlg: function(target){
        var div = new Element("div", {"styles": {"margin": "10px 10px 0px 10px", "padding": "5px", "overflow": "hidden"}});
        //var node = new Element("div", {"styles": {"margin": "10px 10px 0px 10px", "padding": "5px", "overflow": "hidden"}}).inject(div);
        var p = target.getPosition(this.table.designer.content);
        var s = target.getSize();
        var size = this.table.designer.content.getSize();
        var top = p.y;
        var left = (p.x+s.x/2)-180;
        if (top+400+10>size.y) top = size.y-400-10;
        if (left+360+10>size.x) left = size.x-360-10;
        if (top<10) top = 10;
        if (left<10) left = 10;
        var fromTop = p.y+s.y/2;
        var fromLeft = p.x+s.x/2;

        var options ={
            "content": div,
            "isTitle": false,
            "container": this.table.designer.content,
            "width": 360,
            "height": 400,
            "top": top,
            "left": left,
            "fromTop": fromTop,
            "fromLeft": fromLeft,
            "buttonList": [
                {
                    "text": this.table.designer.lp.close,
                    "action": function(){dlg.close();}.bind(this)
                }
            ]
        }
        var dlg = o2.DL.open(options);
        return dlg;
    },
    getFieldValue: function(node){
        var field = node.retrieve("field")
        this.loadFieldValue(field.name, function(){
            var value = this.lineData[field.name];
            var dlg = this.createObjectValueDlg(node);
            var listNode = dlg.content.getFirst();

            switch (field.type){
                case "stringList":
                case "integerList":
                case "longList":
                case "doubleList":
                case "booleanList":
                    o2.require("o2.widget.Arraylist", function(){
                        var list = new o2.widget.Arraylist(listNode, {
                            "style": "table",
                            "title": field.name,
                            "isAdd": false,
                            "isDelete": false,
                            "isModify": false
                        });
                        list.load(value);
                    }.bind(this));
                    break;
                case "stringLob":
                    td.set("html", "<div style='background-color:#dddddd; cursor: pointer;float: left; padding: 3px; font-size: 10px;'>...</div>");
                    break;
                case "stringMap":
                    o2.require("o2.widget.Maplist", function(){
                        var list = new o2.widget.Maplist(listNode, {
                            "style": "table",
                            "title": field.name,
                            "isAdd": false,
                            "isDelete": false,
                            "isModify": false
                        });
                        list.load(value);
                    }.bind(this));
                    break;
            }
        }.bind(this));
    },
    loadFieldValue: function(name, callback){
        if (name){
            if (this.lineData[name]){
                if (callback) callback();
            }else{
                this.table.designer.actions.getRow(this.tableData.id, this.lineData.id, function(json){
                    this.lineData = json.data;
                    if (callback) callback();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.query.TableDesigner.Table.JPQLRunner = new Class({
    initialize: function(node, runNode, table){
        this.table = table;
        this.node = node;
        this.runNode = runNode;
        this.css = this.table.css;
        this.lp = this.table.designer.lp;
        //this.select = select;
    },
    load: function(callback){
        this.optionArea = new Element("div", {"styles": this.css.jpqlOptionArea}).inject(this.node);
        //this.contentArea = new Element("div", {"styles": this.css.jpqlContentArea}).inject(this.node);
        this.contentWhereArea = new Element("div", {"styles": this.css.jpqlContentWhereArea}).inject(this.node);
        this.loadOptions();
        //this.loadSelectEditor(callback);
        this.loadEditor(callback);
    },
    loadOptions: function(){
        var html = "<table cellpadding='0' cellspacing='0' style='height:30px'><tr>";
        html += "<td style='padding: 0 5px'>"+this.lp.jpqlType+"</td>";
        html += "<td style='padding: 0 5px'><select><option value='select'>select</option><option value='update'>update</option><option value='delete'>delete</option></select></td>";
        html += "<td style='padding: 0 5px'>"+this.lp.jpqlFromResult+"</td>";
        html += "<td style='padding: 0 5px'><input type='number' value='1'/></td>";
        html += "<td style='padding: 0 5px'>"+this.lp.jpqlMaxResult+"</td>";
        html += "<td style='padding: 0 5px'><input type='number' value='50'/></td>";
        // html += "<td style='padding: 0 5px'>"+this.lp.jpqlSelectTitle+"</td>";
        // html += "<td style='padding: 0 5px'><input readonly type='text' value='"+this.select+"'/></td>";
        // html += "<td style='padding: 0 5px'>"+this.lp.inputWhere+"</td>";
        html += "</tr></table>";
        this.optionArea.set("html", html);
        this.jpqlTypeSelect = this.optionArea.getElement("select");
        var inputs = this.optionArea.getElements("input");
        this.fromResultInput = inputs[0];
        this.maxResultsInput = inputs[1];
        this.jpqlTypeSelect.setStyles(this.css.jpqlTypeSelect);
        this.fromResultInput.setStyles(this.css.jpqlOptionInput);
        this.maxResultsInput.setStyles(this.css.jpqlOptionInput);

        this.jpqlTypeSelect.addEvent("change", function(){
            var type = this.jpqlTypeSelect.options[this.jpqlTypeSelect.selectedIndex].value;
            this.changeType(type, true);
        }.bind(this));
        // inputs[2].setStyles(this.css.jpqlOptionInput);
        // inputs[2].setStyle("width", "200px")
    },
    // loadSelectEditor: function(){
    //     this.contentArea.set("text", this.select);
    //     o2.require("o2.widget.ace", function(){
    //         o2.widget.ace.load(function(){
    //             o2.load("../o2_lib/ace/src-min-noconflict/ext-static_highlight.js", function(){
    //                 var highlight = ace.require("ace/ext/static_highlight");
    //                 highlight(this.contentArea, {mode: "ace/mode/jql", theme: "ace/theme/tomorrow", "fontSize": 16});
    //             }.bind(this));
    //         }.bind(this));
    //     }.bind(this));
    // },
    loadEditor: function(callback){
        o2.require("o2.widget.JavascriptEditor", function(){
            this.editor = new o2.widget.JavascriptEditor(this.contentWhereArea, {"title": "JPQL", "option": {"mode": "sql"}});
            this.editor.load(function(){
                this.editor.addEditorEvent("change", function(){
                    this.checkJpqlType();
                }.bind(this));

                // this.editor.editor.on("change", function(){
                //     this.checkJpqlType();
                // }.bind(this));
                if (callback) callback();
            }.bind(this));
        }.bind(this), false);
    },
    checkJpqlType: function(){
        var str = this.editor.editor.getValue();
        var jpql_select = /^select/i;
        var jpql_update = /^update/i;
        var jpql_delete = /^delete/i;
        if (jpql_select.test(str)) return this.changeType("select");
        if (jpql_update.test(str)) return this.changeType("update");
        if (jpql_delete.test(str)) return this.changeType("delete");
    },
    changeType: function(type, force){
        if (type != this.jpqlTypeSelect.options[this.jpqlTypeSelect.selectedIndex].value || force){
            for (var i=0; i<this.jpqlTypeSelect.options.length; i++){
                if (this.jpqlTypeSelect.options[i].value==type){
                    this.jpqlTypeSelect.options[i].set("selected", true);
                    break;
                }
            }
            if (type!="select"){
                var tds = this.optionArea.getElements("td");
                tds[2].hide();
                tds[3].hide();
                tds[4].hide();
                tds[5].hide();
            }else{
                var tds = this.optionArea.getElements("td");
                tds[2].show();
                tds[3].show();
                tds[4].show();
                tds[5].show();
            }
        }
    },
    setJpql: function(type, jpql, firstResult, maxResults){
        if (this.editor){
            if (this.editor.editor) this.editor.editor.setValue(jpql);
        }
        if (this.jpqlTypeSelect){
            for (var i=0; i<this.jpqlTypeSelect.options.length; i++){
                if (this.jpqlTypeSelect.options[i].value==type.toString().toLowerCase()){
                    this.jpqlTypeSelect.options[i].set("selected", true);
                    break;
                }
            }
        }
        if (this.fromResultInput) this.fromResultInput.set("value", firstResult);
        if (this.maxResultsInput) this.maxResultsInput.set("value", maxResults);
    },
    getJpql: function(){
        var jpql = this.editor.editor.getValue();
        var type = this.jpqlTypeSelect.options[this.jpqlTypeSelect.selectedIndex].get("value");
        var fromResult = this.fromResultInput.get("value");
        var maxResults = this.maxResultsInput.get("value");
        return {
            "data": jpql,
            "type": type,
            "firstResult": fromResult.toInt(),
            "maxResults": maxResults.toInt()
        }
    }
});

MWF.xApplication.query.TableDesigner.Table.ExcelUtils = new Class({
    _loadResource : function( callback ){
        var uri = "/x_component_Template/framework/xlsx/xlsx.full.js";
        var uri2 = "/x_component_Template/framework/xlsx/xlsxUtils.js";
        COMMON.AjaxModule.load(uri, function(){
            COMMON.AjaxModule.load(uri2, function(){
                callback();
            }.bind(this))
        }.bind(this))
    },
    _openDownloadDialog: function(url, saveName){
        /**
         * 通用的打开下载对话框方法，没有测试过具体兼容性
         * @param url 下载地址，也可以是一个blob对象，必选
         * @param saveName 保存文件名，可选
         */
        if( Browser.name !== 'ie' ){
            if(typeof url == 'object' && url instanceof Blob){
                url = URL.createObjectURL(url); // 创建blob地址
            }
            var aLink = document.createElement('a');
            aLink.href = url;
            aLink.download = saveName || ''; // HTML5新增的属性，指定保存文件名，可以不要后缀，注意，file:///模式下不会生效
            var event;
            if(window.MouseEvent && typeOf( window.MouseEvent ) == "function" ) event = new MouseEvent('click');
            else
            {
                event = document.createEvent('MouseEvents');
                event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
            }
            aLink.dispatchEvent(event);
        }else{
            window.navigator.msSaveBlob( url, saveName);
        }
    },
    export : function(array, fileName){
        this._loadResource( function(){
            data = xlsxUtils.format2Sheet(array, 0, 0, null);//偏移3行按keyMap顺序转换
            var wb = xlsxUtils.format2WB(data, "sheet1", undefined);
            var wopts = { bookType: 'xlsx', bookSST: false, type: 'binary' };
            var dataInfo = wb.Sheets[wb.SheetNames[0]];

            var widthArray = [];
            array[0].each( function( v, i ){

                widthArray.push( {wpx: 100} );

                var at = String.fromCharCode(97 + i).toUpperCase();
                var di = dataInfo[at+"1"];
                // di.v = v;
                // di.t = "s";
                di.s = {  //设置副标题样式
                    font: {
                        //name: '宋体',
                        sz: 12,
                        color: {rgb: "#FFFF0000"},
                        bold: true,
                        italic: false,
                        underline: false
                    },
                    alignment: {
                        horizontal: "center" ,
                        vertical: "center"
                    }
                };
            }.bind(this));
            dataInfo['!cols'] = widthArray,

                this._openDownloadDialog(xlsxUtils.format2Blob(wb), fileName +".xlsx");
        }.bind(this))
    },
    import : function( file, callback, dateColArray ){
        this._loadResource( function(){
            var reader = new FileReader();
            var workbook, data;
            reader.onload = function (e) {
                //var data = data.content;
                if (!e) {
                    data = reader.content;
                }else {
                    data = e.target.result;
                }
                workbook = XLSX.read(data, { type: 'binary' });
                //wb.SheetNames[0]是获取Sheets中第一个Sheet的名字
                //wb.Sheets[Sheet名]获取第一个Sheet的数据
                var sheet = workbook.SheetNames[0];
                var jsonList = [];
                for (var sheet in workbook.Sheets) {
                    if (workbook.Sheets.hasOwnProperty(sheet)) {
                        fromTo = workbook.Sheets[sheet]['!ref'];
                        console.log(fromTo);
                        var json = XLSX.utils.sheet_to_json(workbook.Sheets[sheet]);
                        console.log(JSON.stringify(json));
                        jsonList.push(json);
                        // break; // 如果只取第一张表，就取消注释这行
                    }
                }
                if(callback)callback(jsonList);
            };
            reader.readAsBinaryString(file);
        })
    }
});
