MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xScript.Macro", null, false);
MWF.require("o2.widget.Dialog", null, false);
MWF.xDesktop.requireApp("query.Query", "Importer", null, false);
MWF.xDesktop.requireApp("query.Query", "lp."+o2.language, null, false);
/** @classdesc Importer 数据中心的导入模型。
 * @class
 * @o2cn 导入模型
 * @o2category QueryImporter
 * @o2range {QueryImporter}
 * @hideconstructor
 * @example
 * //在导入模型的事件中获取该类
 * var view = this.target;
 * */
MWF.xApplication.query.Query.MultiImporter = MWF.QMultiImporter = new Class(
    {
        Implements: [Options, Events],
        Extends: MWF.widget.Common,
        options: {
            "style": "default"
        },
        initialize: function(container, json, options, app, parentMacro){

            this.setOptions(options);

            this.path = "../x_component_query_Query/$Importer/";
            this.cssPath = "../x_component_query_Query/$Importer/"+this.options.style+"/css.wcss";
            this._loadCss();
            this.lp = MWF.xApplication.query.Query.LP;

            this.app = app;

            this.json = json;

            this.container = container;

            this.parentMacro = parentMacro;

            this.lookupAction = MWF.Actions.get("x_query_assemble_surface");

        },
        load: function(){
            return new Promise((resolve)=>{
                var ps = this.json.importers.map( (importerJSON)=>{
                    var importer = new MWF.QMultiImporter.Importer(
                        this.container,
                        importerJSON,
                        this.options,
                        this.app,
                        this.parentMacro
                    );
                    return importer.load();
                });
                return Promise.all(ps).then((arr)=>{
                    this.importerList = [...arr];
                    this.fireEvent("queryLoad");
                    resolve(this);
                    return this;
                });
            });
        },
        createLoadding: function(){
            this.loadingAreaNode = new Element("div", {"styles": this.css.viewLoadingAreaNode}).inject(this.contentAreaNode);
            new Element("div", {"styles": {"height": "5px"}}).inject(this.loadingAreaNode);
            var loadingNode = new Element("div", {"styles": this.css.viewLoadingNode}).inject(this.loadingAreaNode);
            new Element("div", {"styles": this.css.viewLoadingIconNode}).inject(loadingNode);
            var loadingTextNode = new Element("div", {"styles": this.css.viewLoadingTextNode}).inject(loadingNode);
            loadingTextNode.set("text", "loading...");
        },
        getDateColIndexArray: function (){
            return this.importerList.map((importer)=>{
                return importer.getDateColIndexArray();
            });
        },
        importFromExcel: function(){
            var p = !this.importerList ? this.load() : null;
            Promise.resolve(p).then(()=>{
                this.excelImporter = new MWF.QMultiImporter.ExcelImporter({
                    dateColIndexes: this.getDateColIndexArray()
                });

                this.progressDialog = new MWF.QMultiImporter.ProgressDialog(this);

                var ps = [], count=0;
                this.excelImporter.execute( function (importedDataList) {
                    importedDataList.forEach((importedData, i)=>{
                        var p = this.importerList[i].importFromExcel(importedData, this.progressDialog, ()=>{
                            count++;
                            if( this.importerList.length === count ){
                                this.progressDialog.openDlg();
                            }
                        });
                        ps.push(p);
                    });
                    Promise.all(ps).then((arr)=>{
                        // arr.some((result)=>{
                        //     return result.status === 'error';
                        // });
                        this.openResultDlg(arr);
                    });
                }.bind(this));
            })
        },
        openResultDlg : function(resultList){
            if(this.progressDialog)this.progressDialog.close();

            var _self = this;

            var div = new Element("div", { style : "padding:10px;" });
            var dlg = o2.DL.open({
                "style" : "user",
                "title": this.lp.importFail,
                "content": div,
                "offset": {"y": 0},
                "isMax": true,
                "width": 1000,
                "height": 700,
                "buttonList": [
                    {
                        "type": "exportWithError",
                        "text": this.lp.exportExcel,
                        "action": function () { _self.exportWithImportDataToExcel(); }
                    },
                    {
                        "type": "cancel",
                        "text": this.lp.cancel,
                        "action": function () { dlg.close(); }
                    }
                ],
                "onPostShow": function () {
                    var htmlArray = ["<table "+ this.objectToString( this.css.properties ) +" style='"+this.objectToString( this.css.tableStyles, "style" )+"'>"];

                    htmlArray.push( this._getErrorHeadHtml() );

                    htmlArray.push( this._getErrorContentHtml() )

                    htmlArray.push( "</table>" );
                    div.set("html" , htmlArray.join(""));
                }.bind(this),
                "onPostClose": function(){
                    dlg = null;
                }.bind(this)
            });

        },

        exportWithImportDataToExcel : function ( importData ) {

            if( !this.excelExporter ){
                this.excelExporter = new MWF.QMultiImporter.ExcelExporter();
            }

            var exportTo = function () {
                var resultArr = [];
                var titleArr = this.getTitleArray();
                titleArr.push( this.lp.validationInfor );
                resultArr.push( titleArr );

                if( importData ){
                    importData.each( function (lineData, lineIndex) {
                        var array = [];
                        if( o2.typeOf(lineData)==="array" ) {
                            lineData.each(function (d, i) {
                                array.push((d || '').replace(/&#10;/g, "\n"));
                            });
                        }else if(o2.typeOf(lineData)==="object"){
                            this.json.data.columnList.each( function (columnJson, i) {
                                array.push( lineData[columnJson.path] || "" )
                            }.bind(this));
                            if( lineData["o2ErrorText"] ){
                                array.push( lineData["o2ErrorText"] );
                            }
                        }
                        resultArr.push( array );
                    }.bind(this));
                }else{
                    this.rowList.each( function( row, lineIndex ){
                        var lineData = row.importedData;
                        var array = [];
                        for( var i=0; i<lineData.length; i++ ){
                            var d = ( lineData[i] || '' ).replace(/&#10;/g, "\n");
                            array.push( d );
                        }
                        // lineData.each( function (d, i) {
                        //     array.push( ( d || '' ).replace(/&#10;/g, "\n") );
                        // });

                        array.push( row.errorTextListExcel ? row.errorTextListExcel.join("\n") : ""  );
                        resultArr.push( array );
                    }.bind(this));
                }


                var colWidthArray = this.getColWidthArray();
                colWidthArray.push(260);

                var arg = {
                    data : resultArr,
                    colWidthArray : colWidthArray,
                    title : this.getFileName()
                };

                this.fireEvent("export", [arg]);

                this.excelExporter.execute(
                    arg.data,
                    arg.title,
                    arg.colWidthArray,
                    this.getDateIndexArray()
                )
            }.bind(this);

            if( !this.importerJsonList ){
                this.getImporterJsonList( function () {
                    exportTo();
                }.bind(this))
            }else{
                exportTo();
            }
        },


        downloadTemplate: function(fileName, callback){

            var p = !this.importerList ? this.load() : null;

            return Promise.resolve(p).then(()=>{
                var args = this.importerList.map((importer)=>{
                    return {
                        data : [importer.getTitleArray()],
                        sheetName: importer.getFileName(),
                        colWidthArray : importer.getColWidthArray(),
                        dateIndexArray: importer.getDateIndexArray()
                    };
                });

                //if(callback)callback(args);

                var excelExporter = new MWF.QMultiImporter.ExcelExporter({
                    fileName: this.getFileName(fileName),
                    worksheet: args.map((arg)=>{
                        return {
                            isTemplate: true,
                            sheetName: arg.sheetName,
                            colWidthArray: arg.colWidthArray,
                            dateIndexArray: arg.dateIndexArray
                        };
                    })
                });

                return excelExporter.execute(
                    args.map( (arg)=> {return arg.data;} ),
                    callback
                );
            });
        },
        getFileName: function(fileName){
            var title = fileName || this.json.name;
            var titleA = title.split(".");
            if( ["xls","xlst"].contains( titleA[titleA.length-1].toLowerCase() ) ){
                titleA.splice( titleA.length-1 );
            }
            title = titleA.join(".");
            return title;
        },
        //api 使用 开始

        confirm: function (type, e, title, text, width, height, ok, cancel, callback, mask, style) {
            this.app.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style)
        },
        alert: function (type, title, text, width, height) {
            this.app.alert(type, "center", title, text, width, height);
        },
        notice: function (content, type, target, where, offset, option) {
            this.app.notice(content, type, target, where, offset, option)
        }
        //api 使用 结束
    });

MWF.QMultiImporter.Importer = new Class({
    Extends: MWF.QImporter,
    load: function( callback ){
        //this.excelUtils = new MWF.xApplication.query.Query.Importer.ExcelUtils();
        var ps = [this.getImporterJSON(), this.loadMacro()];
        return Promise.all(ps).then(()=>{
            this._loadModuleEvents();
            this.fireEvent("queryLoad");
            //this.importFromExcel();
            if(callback)callback();
            return this;
        });
    },
    importFromExcel: function (importedData, progressDialog, callback){
        return new Promise((resolve)=>{
            if( this.importerJson && this.json ){
                this._importFromExcel(importedData, progressDialog, resolve, callback);
            }else{
                this.load(()=>{
                    this._importFromExcel(importedData, progressDialog, resolve, callback);
                });
            }
        });
    },
    _importFromExcel: function( importedData, progressDialog, resolve, callback){

        this.rowList = [];

        this.identityMapImported = {};
        this.personMapImported = {};
        this.unitMapImported = {};
        this.groupMapImported = {};

        this.lookupAction.getUUID( function (json){
            this.recordId = json.data;
        }.bind(this), null, false);

        importedData = importedData.filter(function (array) {
            for( var i=0; i<array.length; i++ ){
                if(array[i])return true;
            }
            return false;
        });
        if( !importedData.length ){
            if(callback)callback();
            resolve({status: 'success', importer: this});
        }

        var titleLength = importedData[0].length;
        importedData.each(function (array) {
            for( var i=0; i<titleLength; i++){
                if( typeOf(array[i]) === "null" )array[i] = "";
            }
        });

        progressDialog.addEvent('postShow', function(){

            this.progressBar = new MWF.QMultiImporter.ProgressBar(this, progressDialog.contentNode);

            this.progressBar.showCheckData();
            var level = this.getTitleLevel();
            if( importedData.length >= level ){
                importedData.splice(0, level);
                this.importedData = importedData;
            }else{
                this.importedData = [];
            }

            this.fireEvent("beforeImport", [this.importedData]);
            Promise.resolve( this.importedData.promise ).then(function () {
                this.listOrgDataFromDb(
                    this.getImportedOrgData(),
                    function () {
                        this.importedData.each( function( lineData, lineIndex ){
                            this.rowList.push( new MWF.QMultiImporter.Row( this, lineData, lineIndex ) );
                        }.bind(this));

                        var isValid = this.json.enableValid ? this.checkImportedData() : this.checkNecessaryImportedData();
                        Promise.resolve(isValid).then(function ( isValid1 ) {
                            if( isValid1 ){
                                this.doImportData(resolve);
                            }else{
                                //this.openImportedErrorDlg();
                                this.progressBar.setMessageText('导入失败：校验未通过');
                                resolve({status: 'error', importer: this});
                            }
                        }.bind(this));
                    }.bind(this)
                );
            }.bind(this));

            this.progressBar.importerId = this.importerId;
        }.bind(this));

        if(callback)callback();
    },
    doImportData: function(resolve){
        //创建数据
        //再次校验数据（计算的内容）
        var date = new Date();

        var data = this.getData();

        this.lookupAction.executImportModel(this.json.id, {
            "recordId": this.recordId,
            "data" : data
        }, function () {
            this.progressBar.showImporting( this.recordId, function( data ){
                data.data = data;
                data.rowList = this.rowList;
                this.fireEvent("afterImport", data);
                resolve({status: 'success', importer: this, data:data});
                return data;
            }.bind(this), date);

        }.bind(this), function (xhr) {
            var requestJson = JSON.parse(xhr.responseText);
            this.app.notice(requestJson.message, "error");
            resolve({status: 'failure', importer: this, data:data});
            this.progressBar.close();
        }.bind(this));
    },
});

MWF.QMultiImporter.Row = new Class({
    Extends: MWF.QImporter.Row
});

MWF.xDesktop.requireApp("Template", "utils.ExcelUtilsV2", null, false);
MWF.QMultiImporter.ExcelImporter = new Class({
    Extends: MWF.ExcelImporter
});

MWF.QMultiImporter.ExcelExporter = new Class({
    Extends: MWF.ExcelExporter
});

MWF.QMultiImporter.ProgressDialog = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        zindex: null,
        disableDetailButton: false
    },
    initialize : function( importer, options ){
        this.setOptions(options);
        this.lp = MWF.xApplication.query.Query.LP;
        this.css = importer.css;
    },
    openDlg: function () {
        var _self = this;
        this.contentNode = new Element("div",{"styles": this.css.processContentNode});
        var opt = {
            "style" : "user",
            "title": this.lp.importRecordDetail,
            "content": this.contentNode,
            "offset": {"y": 0},
            "isMax": false,
            "width": 500,
            "height": 400,
            "zindex": this.options.zindex,
            "buttonList": [
                {
                    "type": "openImportRecordDetail",
                    "text": this.lp.openImportRecordDetail,
                    "action": function () { _self.openImportRecordDetail(); }
                },
                {
                    "type": "cancel",
                    "text": this.lp.close,
                    "action": function () { this.dlg.close(); }.bind(this)
                }
            ],
            "onPostShow": function(){
                this.fireEvent("postShow");
            }.bind(this),
            "onPostLoad": function () {
                this.titleAction.hide();
                this.button.hide();
            },
            "onPostClose": function(){
                this.dlg = null;
            }.bind(this)
        };
        if( this.options.disableDetailButton ){
            opt.buttonList.splice(0, 1);
        }
        this.dlg = o2.DL.open(opt);
    },
    showCloseAction: function(){
        this.dlg.titleAction.show();
        this.dlg.button.show();

        // if( this.bottomNode )this.bottomNode.show();
        // if( this.topCloseAction )this.topCloseAction.show();
        // this.setSize();
    },
    close: function(){
        this.dlg.close();
    },
});

MWF.QMultiImporter.ProgressBar = new Class({
    Extends: MWF.QImporter.ProgressBar,
    initialize : function( importer, container, options ){
        this.setOptions(options);
        this.importer = importer;
        this.actions = this.importer.lookupAction;
        this.lp = MWF.xApplication.query.Query.LP;
        this.css = importer.css;
        this.container = container;
        this.titleNode = new Element('div', {
            text: this.importer.json.name,
            style: 'color:#666'
        }).inject(this.container);
        this.contentNode = new Element('div', {style: 'margin-bottom:10px;'}).inject(this.container);
        this.status = "ready";
    },
    showImporting: function( recordId, callback, date ){
        // this.node.show();
        this.setContentHtml();
        this.recordId = recordId;
        this.currentDate = date || new Date();
        this.intervalId = window.setInterval( function(){
            this.actions.getImportModelRecordStatus( this.recordId, function( json ){
                var data = json.data;
                this.status = data.status;
                if( data.status === "待导入" ) { //有其他人正在导入或上次导入还未完成
                    this.setMessageTitle(this.lp.importWaitingTitle);
                    this.setMessageText(this.lp.importWaitingContent);
                }else if( data.status === "导入中" ){ //导入中
                    this.setMessageTitle( this.lp.importDataTitle );
                    this.setMessageText( this.lp.importDataContent.replace( "{count}", data.executeCount));
                    this.updateProgress( data );
                }else{ //已经结束, 状态有 "导入成功","部分成功","导入失败"
                    if( callback )callback( data );
                    if( data.promise && typeOf(data.promise.then) === "function" ){
                        Promise.resolve( data.promise ).then(function () {
                            if(this.intervalId)window.clearInterval( this.intervalId );
                            this.transferComplete( data );
                        }.bind(this))
                    }else{
                        if(this.intervalId)window.clearInterval( this.intervalId );
                        this.transferComplete( data );
                    }
                }
            }.bind(this), null)
        }.bind(this), 500 );
        // this.setSize();
    },
    showCloseAction: function(){
        // this.dlg.titleAction.show();
        // this.dlg.button.show();
    },
    close: function(){
        //this.dlg.close();
    },
    setMessageTitle: function( text){
        //this.dlg.titleText.set("text", text);
    },
    openImportRecordDetail: function () {
        MWF.xDesktop.requireApp("query.Query", "ImporterRecord", function () {
            var detail = new MWF.xApplication.query.Query.ImporterRecord.Detail(
                this.importer.container,
                this.importer.app,
                { importerId: this.importerId, recordId: this.recordId }
            );
            detail.load();
            this.dlg.close();
        }.bind(this), false);

    }
});
