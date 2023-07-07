MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xScript.Macro", null, false);
MWF.require("o2.widget.Dialog", null, false);
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
MWF.xApplication.query.Query.Importer = MWF.QImporter = new Class(
    /** @lends MWF.xApplication.query.Query.Importer# */
    {
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "moduleEvents": [
            /**
             * 加载importer（导入模型对象）的时候执行。可通过this.target获取当前对象。
             * @event MWF.xApplication.query.Query.Importer#queryLoad
             */
            "queryLoad",
            /**
             * 导入前触发，this.event指向导入的数据，您可以通过修改this.event来修改数据。
             * @event MWF.xApplication.query.Query.Importer#beforeImport
             * @example
             * <caption>this.event数据格式如下：</caption>
             *[
             *  [ "标题一","张三","男","大学本科","计算机","2001-1-2","2019-9-2" ], //第一行数据
             *  [ "标题二","李四","男","大学专科","数学","1998-1-2","2018-9-2" ]  //第二行数据
             *]
             */
            "beforeImport",
            /**
             * 前台校验成功，并且后台执行完导入后触发，this.event指向后台返回的导入结果。
             * @event MWF.xApplication.query.Query.Importer#afterImport
             * @example
             * <caption>this.event格式如下：</caption>
             * {
             *     "status": "导入成功", //导入结果：状态有 "导入成功","部分成功","导入失败"
             *     "data": {}, //前台组织好的需要导入的数据
             *     "rowList": [], //前台组织的行对象数组
             *     "count" : 10, //导入总数量
             *     "failCount": 0, //失败数量
             *     "distribution": "" //导入时候时的错误信息
             * }
             */
            "afterImport",
            /**
             * 数据已经生成，前台进行数据校验时触发，this.event指向导入的数据。
             * @event MWF.xApplication.query.Query.Importer#validImport
             * @example
             * <caption>this.event数据格式如下：</caption>
             * {
             *     "data" : [
             *          [ "标题一","张三","男","大学本科","计算机","2001-1-2","2019-9-2" ], //第一行数据
             *          [ "标题二","李四","男","大学专科","数学","1998-1-2","2018-9-2" ]  //第二行数据
             * 	    ],
             *     "rowList": [], //导入的行对象，数据格式常见本章API的afterCreateRowData说明。
             *     "validted" : true  //是否校验通过，可以在本事件中修改该参数，确定是否强制导入
             * }
             */
            "validImport",
            /**
             * 创建每行需要导入的数据前触发，this.event指向当前行对象，您可以通过修改this.event.importData来修改数据。
             * @event MWF.xApplication.query.Query.Importer#beforeCreateRowData
             */
            "beforeCreateRowData",
            /**
             * 创建每行需要导入的数据后触发，this.event指向当前行对象。
             * @event MWF.xApplication.query.Query.Importer#afterCreateRowData
             * @example
             * <caption>this.event格式如下：</caption>
             * {
             *     "importData": [ "标题一","张三","男","大学本科","计算机","2001-1-2","2019-9-2" ], //导入的数据
             *     "data" : {//根据导入模型生成的业务数据
             *  	   {
             *  	    "subject", "标题一", //subject为导入模型列配置的路径
             *  	 	"name" : "张三",
             *  	    ...
             *     },
             *     "document": { //如果导入目标是内容管理，则包含document对象
             *          "title": "标题一"
             *          "identity": "xxx@xxx@I"
             *          ...
             *     },
             *     "work": { //如果导入目标是流程管理，则包含work对象
             *          "title": "标题一"
             *          "identity": "xxx@xxx@I"
             *          ...
             *     },
             *     "errorTextList" : [],  //错误信息
             *     "errorTextListExcel": [] //在出错界面导出Excel时的错误信息
             * }
             */
            "afterCreateRowData"
         ]
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
        this.excelUtils = new MWF.xApplication.query.Query.Importer.ExcelUtils( this );

        this.getImporterJSON( function () {
            this.loadMacro( function () {
                this._loadModuleEvents();
                this.fireEvent("queryLoad");
                this.importFromExcel()
            }.bind(this))
        }.bind(this))

    },
    loadMacro: function (callback) {
        MWF.require("MWF.xScript.Macro", function () {
            this.Macro = new MWF.Macro.ViewContext(this);
            if (callback) callback();
        }.bind(this));
    },
    createLoadding: function(){
        this.loadingAreaNode = new Element("div", {"styles": this.css.viewLoadingAreaNode}).inject(this.contentAreaNode);
        new Element("div", {"styles": {"height": "5px"}}).inject(this.loadingAreaNode);
        var loadingNode = new Element("div", {"styles": this.css.viewLoadingNode}).inject(this.loadingAreaNode);
        new Element("div", {"styles": this.css.viewLoadingIconNode}).inject(loadingNode);
        var loadingTextNode = new Element("div", {"styles": this.css.viewLoadingTextNode}).inject(loadingNode);
        loadingTextNode.set("text", "loading...");
    },
    getImporterJSON: function(callback){
        if( this.importerJson && this.json ){
            if (callback) callback();
        }else{
            if (this.json.name){
                this.lookupAction.getImportModel(this.json.name, this.json.application, function(json){
                    this.importerId = json.data.id;
                    this.importerJson = JSON.decode(json.data.data);
                    json.data.data = this.importerJson;
                    this.json = Object.merge(this.json, json.data);
                    if (callback) callback();
                }.bind(this));
            }else{
                this.lookupAction.getImportModelById(this.json.id, function(json){
                    this.importerId = json.data.id;
                    this.importerJson = JSON.decode(json.data.data);
                    json.data.data = this.importerJson;
                    this.json.application = json.data.query;
                    this.json = Object.merge(this.json, json.data);
                    if (callback) callback();
                }.bind(this));
            }
        }
    },
    _loadModuleEvents : function(){
        Object.each(this.json.data.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event, target){
                        return this.Macro.fire(e.code, target || this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    getDateColIndexArray: function(){
        var dateColIndexArray = [];
        this.json.data.columnList.each(function(columnJson, index){
            var dataType = this.json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;
            if( ["date","dateTime"].contains(dataType) )dateColIndexArray.push( index );
        }.bind(this));
        return dateColIndexArray;
    },
    getOrgColIndexArray : function(){
        var orgColIndexArray = [];
        this.json.data.columnList.each(function(columnJson, index){
            if( columnJson.isName ) {
                orgColIndexArray.push(index);
            }
            // }else if( this.json.type === "cms" ){
            //     if( columnJson.isPublisher || columnJson.isAuthor || columnJson.isReader ){
            //         orgColIndexArray.push( index );
            //     }
            // }
        }.bind(this));
        return orgColIndexArray;
    },
    importFromExcel : function(){

        this.rowList = [];

        this.excelUtils.upload( this.getDateColIndexArray(), function (importedData) {

            importedData = importedData.filter(function (array) {
                for( var i=0; i<array.length; i++ ){
                    if(array[i])return true;
                }
                return false;
            });
            if( !importedData.length )return;

            var titleLength = importedData[0].length;
            importedData.each(function (array) {
                for( var i=0; i<titleLength; i++){
                    if( typeOf(array[i]) === "null" )array[i] = "";
                }
            })

            this.progressBar = new MWF.xApplication.query.Query.Importer.ProgressBar( this, {
                "onPostShow": function(){
                    this.progressBar.showCheckData();

                    this.importedData = importedData;

                    if( this.importedData.length > 0 )this.importedData.shift();

                    this.fireEvent("beforeImport", [this.importedData]);
                    Promise.resolve( this.importedData.promise ).then(function () {
                        this.listAllOrgDataByImport( function () {
                            this.importedData.each( function( lineData, lineIndex ){
                                this.rowList.push( new MWF.xApplication.query.Query.Importer.Row( this, lineData, lineIndex ) )
                            }.bind(this));

                            var isValid = this.json.enableValid ? this.checkImportedData() : this.checkNecessaryImportedData();
                            Promise.resolve(isValid).then(function ( isValid ) {
                                if( isValid ){
                                    this.doImportData();
                                }else{
                                    this.openImportedErrorDlg();
                                }
                            }.bind(this));
                        }.bind(this));
                    }.bind(this));
                }.bind(this)
            });
            this.progressBar.importerId = this.importerId;
        }.bind(this));
    },
    getData : function(){
        var data = ( this.rowList || [] ).map( function(row){
            return row.getResult();
        });
        return data;
    },
    doImportData: function(){
        //创建数据
        // this.rowList.each( function( row, i ){
        //     row.createData();
        // }.bind(this));

        //再次校验数据（计算的内容）
        var date = new Date();

        var flag = true;
        this.rowList.each( function(row, index){
            if( row.errorTextList.length )flag = false;
        }.bind(this));

        var arg = {
            validted : flag,
            data : this.importedData,
            rowList : this.rowList
        };
        this.fireEvent( "validImport", [arg] );

        Promise.resolve( arg.promise ).then(function(){
            flag = arg.validted;

            if( !flag ){
                this.openImportedErrorDlg();
                return;
            }

            var data = this.getData();

            this.lookupAction.getUUID(function(json){
                this.recordId = json.data;
                this.lookupAction.executImportModel(this.json.id, {
                    "recordId": this.recordId,
                    "data" : data
                }, function () {
                    //this.showImportingStatus( data, date )
                    this.progressBar.showImporting( this.recordId, function( data ){
                        data.data = data;
                        data.rowList = this.rowList;
                        this.fireEvent("afterImport", data);
                        return data;
                    }.bind(this), date);

                }.bind(this), function (xhr) {
                    var requestJson = JSON.parse(xhr.responseText);
                    this.app.notice(requestJson.message, "error");
                    this.progressBar.close();
                }.bind(this))
            }.bind(this))
        }.bind(this))
    },
    objectToString: function (obj, type) {
        if(!obj)return "";
        var arr = [];
        Object.each(obj,  function (value, key) {
            if( type === "style" ){
                arr.push( key + ":"+ value +";" )
            }else{
                arr.push( key + "='"+ value +"'" )
            }
        })
        return arr.join( " " )
    },
    openImportedErrorDlg : function(){
        if(this.progressBar)this.progressBar.close();

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

                var titleStyle = this.objectToString( this.css.titleStyles, "style" );
                htmlArray.push( "<tr>" );
                this.json.data.columnList.each( function (columnJson, i) {
                    htmlArray.push( "<th style='"+titleStyle+"'>"+columnJson.displayName+"</th>" );
                });
                htmlArray.push( "<th style='"+titleStyle+"'> "+this.lp.validationInfor +"</th>" );
                htmlArray.push( "</tr>" );

                var contentStyles = Object.clone( this.css.contentStyles );
                if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
                var contentStyle = this.objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

                this.rowList.each( function( row, lineIndex ){

                    var lineData = row.importedData;

                    htmlArray.push( "<tr>" );
                    this.json.data.columnList.each( function (columnJson, i) {
                        htmlArray.push( "<td style='"+contentStyle+"'>"+ ( lineData[ i ] || '' ).replace(/&#10;/g,"<br/>") +"</td>" ); //换行符&#10;
                    });
                    htmlArray.push( "<td style='"+contentStyle+"'>"+( row.errorTextList ? row.errorTextList.join("<br/>") : "" )+"</td>" );
                    htmlArray.push( "</tr>" );

                }.bind(this));
                htmlArray.push( "</table>" );
                div.set("html" , htmlArray.join(""));
            }.bind(this),
            "onPostClose": function(){
                dlg = null;
            }.bind(this)
        });

    },
    //必须校验的数据
    checkNecessaryImportedData: function(){
        var flag = true;

        this.rowList.each( function(row, index){
            if( !row.checkNecessary() )flag = false;
        }.bind(this));

        var arg = {
            validted : flag,
            data : this.importedData,
            rowList : this.rowList
        };
        this.fireEvent( "validImport", [arg] );

        return Promise.resolve( arg.promise ).then(function () {
            return arg.validted;
        });
    },
    //校验Excel中的数据
    checkImportedData : function(){
        var flag = true;

        this.rowList.each( function(row, index){
            if( !row.checkValid() )flag = false;
        }.bind(this));

        var arg = {
            validted : flag,
            data : this.importedData,
            rowList : this.rowList
        };
        this.fireEvent( "validImport", [arg] );

        return Promise.resolve( arg.promise ).then(function () {
            return arg.validted;
        });
    },
    getOrgData : function( str, ignoreNone, isParse ){
        str = str.trim();
        var flag = str.substr(str.length-2, 2);
        var d;
        switch (flag.toLowerCase()){
            case "@i":
                d =  this.identityMapImported[str];
                break;
            case "@p":
                d = this.personMapImported[str];
                break;
            case "@u":
                d = this.unitMapImported[str];
                break;
            case "@g":
                d = this.groupMapImported[str];
                break;
            default:
                d = this.identityMapImported[str] ||
                    this.personMapImported[str] ||
                    this.unitMapImported[str] ||
                    this.groupMapImported[str];
                break;
        }
        if( d )return isParse ? MWF.org.parseOrgData(d, true, true) : d;
        if( ignoreNone ) {
            return null;
        }else{
            return {"errorText":  str + this.lp.notExistInSystem };
        }
    },
    stringToArray: function(string){
        return string.replace(/[\n\r]/g,",").replace(/&#10;/g,",").split(/\s*,\s*/g ).filter(function(s){
            return !!s;
        });
    },
    listAllOrgDataByImport : function ( callback ) {

        var orgColIndexArray = this.getOrgColIndexArray();

        if( orgColIndexArray.length === 0 ){
            if(callback)callback();
            return;
        }

        var identityList = [], personList = [], unitList = [], groupList = [];
        if( orgColIndexArray.length > 0 ){
            this.importedData.each( function( lineData, lineIndex ){
                // if( lineIndex === 0 )return;

                orgColIndexArray.each( function (colIndex, i) {

                    if( !lineData[colIndex] )return;

                    var arr = this.stringToArray(lineData[colIndex]);
                    arr.each( function( a ){
                        a = a.trim();
                        var flag = a.substr(a.length-2, 2);
                        switch (flag.toLowerCase()){
                            case "@i":
                                identityList.push( a ); break;
                            case "@p":
                                personList.push( a ); break;
                            case "@u":
                                unitList.push( a ); break;
                            case "@g":
                                groupList.push( a ); break;
                            default:
                                identityList.push( a );
                                personList.push( a );
                                unitList.push( a );
                                groupList.push( a );
                                break;
                        }
                    })
                }.bind(this))
            }.bind(this));
            var identityLoaded, personLoaded, unitLoaded, groupLoaded;
            var check = function () {
                if( identityLoaded && personLoaded && unitLoaded && groupLoaded ){
                    if(callback)callback();
                }
            };

            this.identityMapImported = {};
            if( identityList.length ){
                identityList = identityList.unique();
                o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({ identityList : identityList }, function (json) {
                    json.data.each( function (d) { if(d)this.identityMapImported[ d.matchKey ] = d; }.bind(this));
                    identityLoaded = true;
                    check();
                }.bind(this))
            }else{
                identityLoaded = true;
                check();
            }

            this.personMapImported = {};
            if( personList.length ){
                personList = personList.unique();
                o2.Actions.load("x_organization_assemble_express").PersonAction.listObject({ personList : personList }, function (json) {
                    json.data.each( function (d) { if(d)this.personMapImported[ d.matchKey ] = d; }.bind(this));
                    personLoaded = true;
                    check();
                }.bind(this))
            }else{
                personLoaded = true;
                check();
            }

            this.unitMapImported = {};
            if( unitList.length ){
                unitList = unitList.unique();
                o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({ unitList : unitList }, function (json) {
                    json.data.each( function (d) { if(d)this.unitMapImported[ d.matchKey ] = d; }.bind(this));
                    unitLoaded = true;
                    check();
                }.bind(this))
            }else{
                unitLoaded = true;
                check();
            }

            this.groupMapImported = {};
            if( groupList.length ){
                groupList = groupList.unique();
                o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({ groupList : groupList }, function (json) {
                    json.data.each( function (d) { if(d)this.groupMapImported[ d.matchKey ] = d; }.bind(this));
                    groupLoaded = true;
                    check();
                }.bind(this))
            }else{
                groupLoaded = true;
                check();
            }
        }
    },

    // showImportingStatus: function( improtedData, date ){
    //     this.progressBar.showImporting( this.recordId, function( data ){
    //         data.data = improtedData;
    //         data.rowList = this.rowList;
    //         this.fireEvent("afterImport", data)
    //     }.bind(this), date);
    // },

    exportWithImportDataToExcel : function ( importData ) {

        if( !this.excelUtils ){
            this.excelUtils = new MWF.xApplication.query.Query.Importer.ExcelUtils( this );
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

            this.excelUtils.exportToExcel(
                arg.data,
                arg.title,
                arg.colWidthArray,
                this.getDateIndexArray()
            )
        }.bind(this);

        if( !this.importerJson ){
            this.getImporterJSON( function () {
                exportTo();
            }.bind(this))
        }else{
            exportTo();
        }
    },

    downloadTemplate: function(fileName, callback){
        if( !this.excelUtils ){
            this.excelUtils = new MWF.xApplication.query.Query.Importer.ExcelUtils( this );
        }
        var doExport = function () {
            var arg = {
                data : [this.getTitleArray()],
                colWidthArray : this.getColWidthArray(),
                title : this.getFileName(fileName)
            };
            if(callback)callback(arg);

            this.excelUtils.exportToExcel(
                arg.data,
                arg.title,
                arg.colWidthArray,
                this.getDateIndexArray()
            )
        }.bind(this)
        if( !this.importerJson ){
            this.getImporterJSON( function () {
                doExport()
            }.bind(this))
        }else{
            doExport();
        }
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
    getTitleArray: function(){
        var titleArray = [];
        this.json.data.columnList.each( function (columnJson, i) {
            titleArray.push( columnJson.displayName );
        }.bind(this));
        return titleArray;
    },
    getColWidthArray : function(){
        var colWidthArr = [];
        this.json.data.columnList.each( function (columnJson, i) {
            var dataType = this.json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;
            switch ( dataType ) {
                case "string":
                case "stringList":
                    if( columnJson.isName ){
                        colWidthArr.push( 340 );
                    }else if( columnJson.isSummary ){
                        colWidthArr.push( 260 );
                    }else{
                        colWidthArr.push( 150 );
                    }
                    break;
                case "number":
                case "integer":
                case "long":
                case "double":
                case "numberList":
                case "integerList":
                case "longList":
                case "doubleList":
                    colWidthArr.push(150);
                    break;
                case "date":
                case "dateTime":
                case "dateList":
                case "dateTimeList":
                    colWidthArr.push(150);
                    break;
                default:
                    colWidthArr.push(150);
                    break;
            }
        }.bind(this));

        return colWidthArr;
    },
    getDateIndexArray : function(){
        var dateIndexArr = []; //日期格式列下标
        this.json.data.columnList.each( function (columnJson, i) {
            var dataType = this.json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;
            switch ( dataType ) {
                case "date":
                case "dateTime":
                case "dateList":
                case "dateTimeList":
                    dateIndexArr.push(i);
                    break;
                default:
                    break;
            }
        }.bind(this));
        return dateIndexArr;
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

MWF.xApplication.query.Query.Importer.Row = new Class({
    initialize: function(importer, importedData, rowIndex){
        this.importer = importer;
        this.importedData = importedData;
        this.clazzType = "row";

        this.lp = this.importer.lp;

        this.data = {};
        this.errorTextList = [];
        this.errorTextListExcel = [];
    },
    checkValid : function(){

        var lp = this.lp;

        var columnText =  lp.importValidationColumnText;
        var columnTextExcel = lp.importValidationColumnTextExcel;

        var errorTextList = [];
        var errorTextListExcel = [];


        this.importer.json.data.columnList.each( function (columnJson, i) {

            var colInfor = columnText.replace( "{n}", i+1 );
            var colInforExcel = columnTextExcel.replace( "{n}", this.importer.excelUtils.index2ColName( i ) );

            var value = this.importedData[i] || "";

            var dataType = this.importer.json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;

            if( !columnJson.allowEmpty && !value ){
                errorTextList.push( colInfor + lp.canNotBeEmpty + lp.fullstop );
                errorTextListExcel.push( colInforExcel + lp.canNotBeEmpty + lp.fullstop );
            }

            if( columnJson.validFieldType !== false && value ){

                switch ( dataType ) {
                    case "string":
                    case "stringList":
                        if( columnJson.isName ){
                            var arr = this.stringToArray(value);
                            arr.each( function(d, idx){
                                var obj = this.importer.getOrgData( d );
                                if( obj.errorText ){
                                    errorTextList.push( colInfor + obj.errorText + lp.fullstop );
                                    errorTextListExcel.push( colInforExcel + obj.errorText + lp.fullstop );
                                }
                            }.bind(this));
                        }
                        break;
                    case "number":
                    case "integer":
                    case "long":
                    case "double":
                        value = value.replace(/&#10;/g,"");
                        if (isNaN(value)){
                            errorTextList.push( colInfor + value + lp.notValidNumber + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidNumber + lp.fullstop );
                        }
                        break;
                    case "numberList":
                    case "integerList":
                    case "longList":
                    case "doubleList":
                        var arr = this.stringToArray(value);
                        arr.each( function(d, idx){
                            if (isNaN(d)){
                                errorTextList.push( colInfor + d + lp.notValidNumber + lp.fullstop );
                                errorTextListExcel.push( colInforExcel + d + lp.notValidNumber + lp.fullstop );
                            }
                        }.bind(this));
                        break;
                    case "date":
                    case "dateTime":
                        value = value.replace(/&#10;/g,"");
                        if( !( new Date(value).isValid() )){
                            errorTextList.push(colInfor + value + lp.notValidDate + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidDate + lp.fullstop );
                        }
                        break;
                    case "dateList":
                    case "dateTimeList":
                        var  arr = this.stringToArray(value);
                        arr.each( function(d, idx){
                            if( !( new Date(d).isValid() )){
                                errorTextList.push(colInfor + d + lp.notValidDate + lp.fullstop );
                                errorTextListExcel.push( colInforExcel + d + lp.notValidDate + lp.fullstop );
                            }
                        }.bind(this));
                        break;
                    case "boolean":
                        value = value.replace(/&#10;/g,"");
                        if( !["true","false"].contains(value.trim().toLowerCase()) ){
                            errorTextList.push(colInfor + value + lp.notValidBoolean + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidBoolean + lp.fullstop );
                        }
                        break;
                    case "booleanList":
                        var  arr = this.stringToArray(value);
                        arr.each( function(d, idx){
                            if( !["true","false"].contains(d.trim().toLowerCase())){
                                errorTextList.push(colInfor + d + lp.notValidBoolean + lp.fullstop );
                                errorTextListExcel.push( colInforExcel + d + lp.notValidBoolean + lp.fullstop );
                            }
                        }.bind(this));
                        break;
                    case "json":
                    case "stringMap":
                        value = value.replace(/&#10;/g,"");
                        try{
                            var d = JSON.parse(value);
                        }catch (e) {
                            errorTextList.push(colInfor + value + lp.notValidJson + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidJson + lp.fullstop );
                        }
                        break;
                    default:
                        break;
                }
            }
        }.bind(this));

        this.errorTextList = this.errorTextList.concat( errorTextList );
        this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

        if(this.errorTextList.length>0){
            return false;
        }

        this.createData();

        if( this.importer.json.type === "cms" ){
            this.checkCMS( true );
        }else if( this.importer.json.type === "process" ){
            this.checkProcess( true );
        }

        if(this.errorTextList.length>0){
            return false;
        }

        return true;
    },
    checkNecessary: function(){

        var lp = this.lp;

        var columnText =  lp.importValidationColumnText;
        var columnTextExcel = lp.importValidationColumnTextExcel;

        var errorTextList = [];
        var errorTextListExcel = [];


        this.importer.json.data.columnList.each( function (columnJson, i) {

            var colInfor = columnText.replace( "{n}", i+1 );
            var colInforExcel = columnTextExcel.replace( "{n}", this.importer.excelUtils.index2ColName( i ) );

            var value = this.importedData[i] || "";

            var dataType = this.importer.json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;

            if( columnJson.validFieldType !== false && value ){

                switch ( dataType ) {
                    case "json":
                    case "stringMap":
                        value = value.replace(/&#10;/g,"");
                        try{
                            var d = JSON.parse(value);
                        }catch (e) {
                            errorTextList.push(colInfor + value + lp.notValidJson + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidJson + lp.fullstop );
                        }
                        break;
                    default:
                        break;
                }
            }
        }.bind(this));

        this.errorTextList = this.errorTextList.concat( errorTextList );
        this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

        if(this.errorTextList.length>0){
            return false;
        }

        this.createData();

        if( this.importer.json.type === "cms" ){
            this.checkCMS();
        }else if( this.importer.json.type === "process" ){
            this.checkProcess();
        }

        if(this.errorTextList.length>0){
            return false;
        }

        return true;

        // var flag = true;
        // if( this.importer.json.type === "cms" ){
        //     if( !this.checkCMS() )flag = false;
        // }else if( this.importer.json.type === "process" ){
        //     if( !this.checkProcess() )flag = false;
        // }
        // return flag;
    },
    getCol: function(key, isExcel){
        var lp = this.lp;
        if( this.pathIndexMap && typeOf(this.pathIndexMap[key]) === "number"){
            var i = this.pathIndexMap[key];
            if( isExcel ){
                var text = lp.importValidationColumnTextExcel;
                return text.replace( "{n}", this.importer.excelUtils.index2ColName( i ) );
            }else{
                var text =  lp.importValidationColumnText;
                return text.replace( "{n}", i+1 );
            }
        }
        return ""
    },
    checkCMS : function( notCheckName ){

        var lp = this.lp;

        var errorTextList = [];
        var errorTextListExcel = [];

        var data = this.document.identity;
        if(!data){

            errorTextList.push( this.getCol("identity", false) + lp.noDrafter + lp.fullstop );
            errorTextListExcel.push( this.getCol("identity", true) + lp.noDrafter + lp.fullstop );

        }else if(data.split("@").getLast().toLowerCase() !== "i"){

            errorTextList.push( this.getCol("identity", false) + '"'+ data +'"'+ lp.drafterIsNotIdentity + lp.fullstop );
            errorTextListExcel.push(  this.getCol("identity", true) + '"'+ data +'"'+ lp.drafterIsNotIdentity + lp.fullstop );

        }

        var data = this.document.publishTime;
        if(!data){
            errorTextList.push(this.getCol("publishTime", false) + lp.noPublishTime + lp.fullstop );
            errorTextListExcel.push(this.getCol("publishTime", false) + lp.noPublishTime + lp.fullstop );
        }else if( ! new Date(data).isValid() ){
            errorTextList.push(this.getCol("publishTime", false) + '"'+ data +'"'+ lp.publishTimeFormatError + lp.fullstop );
            errorTextListExcel.push(this.getCol("publishTime", false) + '"'+ data +'"'+ lp.publishTimeFormatError + lp.fullstop );
        }

        data = this.document.title;
        if( data && data.length > 70){
            errorTextList.push(this.getCol("title", false) + '"'+ data +'"'+ lp.cmsTitleLengthInfor + lp.fullstop );
            errorTextListExcel.push(this.getCol("title", false) + '"'+ data +'"'+ + lp.cmsTitleLengthInfor + lp.fullstop );
        }

        data = this.document.summary;
        if( data && data.length > 70 ){
            errorTextList.push(this.getCol("summary", false) + '"'+ data +'"'+ lp.cmsSummaryLengthInfor + lp.fullstop );
            errorTextListExcel.push( this.getCol("summary", false) + '"'+ data +'"'+ lp.cmsSummaryLengthInfor + lp.fullstop );
        }

        this.errorTextList = this.errorTextList.concat( errorTextList );
        this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

        if(errorTextList.length > 0){
            return false;
        }

        return true;
    },
    checkProcess : function( notCheckName ){

        var lp = this.lp;
        var json = this.importer.json;

        var columnText =  lp.importValidationColumnText;
        var columnTextExcel = lp.importValidationColumnTextExcel;

        var errorTextList = [];
        var errorTextListExcel = [];

        var data = this.work.identity;
        if(!data){
            errorTextList.push( this.getCol("identity", false) + lp.noDrafter + lp.fullstop );
            errorTextListExcel.push( this.getCol("identity", true) + lp.noDrafter + lp.fullstop );
        }else if(data.split("@").getLast().toLowerCase() !== "i"){
            errorTextList.push( this.getCol("identity", false) + '"'+ data +'"'+ lp.drafterIsNotIdentity + lp.fullstop );
            errorTextListExcel.push(  this.getCol("identity", true) + '"'+ data +'"'+ lp.drafterIsNotIdentity + lp.fullstop );
        }

        if( json.data.processStatus === "completed" ){
            if(!this.work.form){
                errorTextList.push( lp.noForm + lp.fullstop );
                errorTextListExcel.push( lp.noForm + lp.fullstop );
            }

            var data = this.work.startTime;
            if(!data){
                errorTextList.push(this.getCol("startTime", false) + lp.noStartTime + lp.fullstop );
                errorTextListExcel.push(this.getCol("startTime", false) + lp.noStartTime + lp.fullstop );
            }else if( ! new Date(data).isValid() ){
                errorTextList.push(this.getCol("startTime", false) + '"'+ data +'"'+ lp.startTimeFormatError + lp.fullstop );
                errorTextListExcel.push(this.getCol("startTime", false) + '"'+ data +'"'+ lp.startTimeFormatError + lp.fullstop );
            }

            var data = this.work.completeTime;
            if(!data){
                errorTextList.push(this.getCol("completeTime", false) + lp.noEndTime + lp.fullstop );
                errorTextListExcel.push(this.getCol("completeTime", false) + lp.noEndTime + lp.fullstop );
            }else if( ! new Date(data).isValid() ){
                errorTextList.push(this.getCol("completeTime", false) + '"'+ data +'"'+ lp.endTimeFormatError + lp.fullstop );
                errorTextListExcel.push(this.getCol("completeTime", false) + '"'+ data +'"'+ lp.endTimeFormatError + lp.fullstop );
            }
        }

        this.errorTextList = this.errorTextList.concat( errorTextList );
        this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

        if(errorTextList.length > 0){
            return false;
        }

        return true;
    },


    createData : function(){

        var json = this.importer.json;

        if( json.type === "cms" ){
            this.document = {
                categoryId : json.data.category.id,
                readerList : [],
                authorList : [],
                docData : this.data
            };
        }else if( json.type === "process" ){
            this.work = {
                processFlag : json.data.process.id,
                data: this.data
            };
        }

        this.importer.fireEvent("beforeCreateRowData", [this]);

        json.data.columnList.each( function (columnJson, i) {
            if(!columnJson.path)return;

            var value = this.importedData[i] || "";
            if( !value )return;

            var data = this.parseData(value, (json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess), columnJson);
            if( !data && data !== 0 )return;

            if( json.type === "dynamicTable" ){
                this.data[ columnJson.path ] = data;
            }else{
                this.setDataWithPath(this.data, columnJson.path, data);
            }

            if( json.type === "cms" ){
                if( columnJson.isName ) {
                    if (columnJson.isAuthor) {
                        var array = this.parseCMSReadAndAuthor(data, "作者");
                        this.document.authorList = this.document.authorList.concat(array)
                    }
                    if (columnJson.isReader) {
                        var array = this.parseCMSReadAndAuthor(data, "阅读");
                        this.document.readerList = this.document.readerList.concat(array)
                    }
                }

            }

        }.bind(this));

        json.data.calculateFieldList.each( function (fieldJson, i) {
            if( fieldJson.valueScript ){
                var data = this.importer.Macro.exec( fieldJson.valueScript, this );

                if( o2.typeOf(data) === "null" )return;
                if( !fieldJson.path )return;

                if( json.type === "dynamicTable" ){
                    this.data[ fieldJson.path ] = data;
                }else{
                    this.setDataWithPath(this.data, fieldJson.path, data);
                }

                if( json.type === "cms" ){
                    if( fieldJson.isAuthor ){
                        var array = this.parseCMSReadAndAuthor( data, "作者" );
                        this.document.authorList = this.document.authorList.concat( array )
                    }
                    if( fieldJson.isReader ){
                        var array = this.parseCMSReadAndAuthor( data, "阅读" );
                        this.document.readerList = this.document.readerList.concat( array )
                    }

                }
            }
        }.bind(this));

        var array;
        if( json.type === "cms" ){
            this.document.docData = this.data;

            if( json.data.documentPublisher === "importer" ){
                array = layout.session.user.identityList;
                if( array && array.length ){
                    this.document.identity = array[0].distinguishedName
                }
            }else{
                this.setDataWithField(this.document, "documentPublisherField", "identity", true);
            }

            if( json.data.documentPublishTime === "importer" ){
                this.document.publishTime = new Date().format("db");
            }else{
                this.setDataWithField(this.document, "documentPublisherTimeField", "publishTime", false);
            }

            this.setDataWithField(this.document, "documentTitleField", "title", false);
            if( !this.document.title )this.document.title = "无标题";

            this.setDataWithField(this.document, "documentSummaryField", "summary", false);

        }else if( json.type === "process" ){
            this.work.data = this.data;

            if( json.data.processDrafter === "importer" ){
                array = layout.session.user.identityList;
                if( array && array.length ){
                    this.work.identity = array[0].distinguishedName
                }
            }else{
                this.setDataWithField(this.work, "processDrafterField", "identity", true);
            }

            this.setDataWithField(this.work, "processTitleField", "title", false);
            if( !this.work.title )this.work.title = "无标题";

            if( json.data.processStatus === "completed" ){
                this.work.form = json.data.processForm || "";
                this.setDataWithField(this.work, "processSerialField", "serial", false);
                this.setDataWithField(this.work, "processStartTimeField", "startTime", false);
                this.setDataWithField(this.work, "processCompleteTimeField", "completeTime", false);
            }
        }


        this.importer.fireEvent("afterCreateRowData", [ this]);
    },
    parseData: function(value, dataType, json){
        var data;
        var type = this.importer.json.type;
        switch ( dataType ) {
            case "string":
            case "stringList":
                if( json.isName ){
                    var  arr = this.stringToArray(value);
                    if( type === "dynamicTable" ){
                        data = arr
                    }else{
                        data = arr.map( function(d, idx){
                            return this.importer.getOrgData( d, true, true ) || d;
                        }.bind(this)).clean();
                    }
                }else{
                    if( dataType === "string" ){
                        var linebreak = type === "dynamicTable" ? json.lineBreak_Querytable : json.lineBreak_CMSProcess;
                        data = value.replace(/&#10;/g, linebreak || "" )
                    }else{
                        data = this.stringToArray(value);
                    }
                }
                break;
            case "number":
            case "double":
                value = value.replace(/&#10;/g,"");
                data = parseFloat(value);
                break;
            case "integer":
            case "long":
                value = value.replace(/&#10;/g,"");
                data = parseInt( value );
                break;
            case "numberList":
            case "doubleList":
                data = this.stringToArray(value).map( function(d, idx){ return parseFloat( d ); }.bind(this)).clean();
                break;
            case "integerList":
            case "longList":
                data = this.stringToArray(value).map( function(d, idx){ return parseInt( d ); }.bind(this)).clean();
                break;
            case "date":
                value = value.replace(/&#10;/g,"");
                data = Date.parse(value).format( "%Y-%m-%d" );
                break;
            case "dateTime":
                value = value.replace(/&#10;/g,"");
                data = Date.parse(value).format( "db" );
                break;
            case "dateList":
                data = this.stringToArray(value).map( function(d, idx){ return Date.parse(d).format( "%Y-%m-%d" ); }.bind(this)).clean();
                break;
            case "dateTimeList":
                data = this.stringToArray(value).map( function(d, idx){ return Date.parse(d).format( "db" ); }.bind(this)).clean();
                break;
            case "boolean":
                value = value.replace(/&#10;/g,"");
                data = value.trim().toLowerCase() !== "false";
                break;
            case "booleanList":
                data = this.stringToArray(value).map( function(d, idx){ return value.trim().toLowerCase() !== "false"; }.bind(this)).clean();
                break;
            case "json":
            case "stringMap":
                value = value.replace(/&#10;/g,"");
                data = JSON.parse(value);
                break;
            default:
                data = value.replace(/&#10;/g,"");
                break;
        }
        return data;
    },
    stringToArray: function(string){
        return string.replace(/[\n\r]/g,",").replace(/&#10;/g,",").split(/\s*,\s*/g ).filter(function(s){
            return !!s;
        });
    },
    setDataWithPath: function(obj, path, data){
        var names = path.split(".");
        var d = obj;
        Array.each(names, function (n, idx) {
            if( idx === names.length -1 )return;
            if ( !d[n] ){
                var value = this.isNumberString( names[idx+1] ) ? [] : {};
                var n1 = this.isNumberString( n ) ? n.toInt() : n;
                d[n1] = value;
                d = d[n1];
            }else{
                d = d[n];
            }
        }.bind(this));
        d[names[names.length -1]] = data;
    },
    setDataWithField: function(obj, fieldName, path, isName){
        if(!this.pathIndexMap)this.pathIndexMap = {};

        if( !path )return;

        var json = this.importer.json;
        if( json.data[fieldName] ){
            var f = json.data[fieldName];

            json.data.columnList.each(function(json, i){
                if(json.path === f)this.pathIndexMap[path] = i;
            }.bind(this));

            var d = this.data;
            Array.each( f.split("."), function (n) {
                if(this.isNumberString(n))n = n.toInt();
                if (d) d = d[n];
            }.bind(this));
            if(!d)return;

            var _d = (typeOf(d) === "array" && d.length) ? d[0] : d;
            if( _d ){
                if(isName){
                    obj[path] = typeOf(_d) === "object" ? _d.distinguishedName : _d;
                }else{
                    obj[path] = d;
                }
            }
        }
    },
    isNumberString: function(string){
       return string.toInt().toString() === string;
    },
    parseCMSReadAndAuthor : function( data, t ){
        var cnArray = ["组织","群组","人员","人员","角色"];
        var keyArray = ["U","G","I","P","R"];
        if( typeOf(data) !== "array" )data = [data];
        return data.map( function( d ){
            var dn = typeOf( d ) === "string" ? d : d.distinguishedName;

            var name;
            if( typeOf(d) === "object" && d.name ){
                name = d.name;
            }else if( MWF.name && MWF.name.cn ){
                name = MWF.name.cn( dn );
            }else{
                name = dn.split("@")[0];
            }

            var index = keyArray.indexOf(dn.substr(dn.length-1, 1));
            if( index > -1 ){
                return {
                    "permission" : t,
                    "permissionObjectType": cnArray[ index ],
                    "permissionObjectName": name,
                    "permissionObjectCode": dn
                }
            }
        }).clean()
    },

    getSrcData: function(){
        var srcData = {};
        this.importer.json.data.columnList.each( function (columnJson, i) {
            if(columnJson.path)srcData[ columnJson.path ] = this.importedData[i] || "";
        }.bind(this));
        return srcData;
    },
    getResult: function(){
        if( this.importer.json.type === "cms" ){
            this.document.srcData = this.getSrcData(); //this.importedData;
            return this.document;
        }else if( this.importer.json.type === "process" ){
            this.work.srcData = this.getSrcData(); //this.importedData;
            return this.work;
        }else if( this.importer.json.type === "dynamicTable" ){
            this.data.srcData = this.getSrcData(); //this.importedData;
            return this.data;
        }
    }
});

MWF.xDesktop.requireApp("Template", "utils.ExcelUtils", null, false);
MWF.xApplication.query.Query.Importer.ExcelUtils = new Class({
    Extends: MWF.xApplication.Template.utils.ExcelUtils,
    initialize: function(){
        this.sheet2JsonOptions = {header:1};
    }
});

MWF.xApplication.query.Query.Importer.ProgressBar = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        zindex: null,
        disableDetailButton: false
    },
    initialize : function( importer, options ){
        this.setOptions(options);
        this.importer = importer;
        this.actions = this.importer.lookupAction;
        this.lp = MWF.xApplication.query.Query.LP;
        this.css = importer.css;
        this.openDlg();
        this.status = "ready";
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
            "height": 200,
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
    createNode: function( noProgress ){
        // var lp = this.lp;
        // this.maskNode = new Element("div",{"styles": this.css.maskNode}).inject(this.importer.container);
        //
        // this.node = new Element("div", {"styles": this.css.progressBarNode}).inject(this.importer.container);
        //
        // this.topNode =  new Element("div",{"styles": this.css.progressTopNode}).inject(this.node);
        //
        // this.subjectNode =  new Element("div",{"styles": this.css.progressSubjectNode}).inject(this.topNode);
        //
        // this.topCloseAction = new Element("div.topCloseAction", {
        //     "styles": this.css.progressTopCloseAction,
        //     "text": "x"
        // }).inject(this.topNode);
        // this.topCloseAction.hide();
        // this.topCloseAction.addEvent("click", function(){
        //     this.close();
        // }.bind(this))
        //
        // this.contentNode = new Element("div",{"styles": this.css.processContentNode}).inject(this.node);
        //
        // this.bottomNode = new Element("div", {"styles": this.css.progressBottomNode}).inject(this.node);
        // this.bottomNode.hide();
        //
        // this.closeAction = new Element("div.closeAction", {
        //     "styles": this.css.progressCloseAction,
        //     "text": lp.close
        // }).inject(this.bottomNode);
        // this.closeAction.addEvent("click", function(){
        //     this.close();
        // }.bind(this))
    },
    setContentHtml: function(noProgress){
        var lp = this.lp;
        var contentHTML = "";
        if (noProgress){
            // contentHTML = "<div style=\"height: 20px; line-height: 20px\">"+lp.readyToImportData1+"</div></div>" ;
            contentHTML =
                "<div style=\"overflow: hidden\">" +
                "   <div class='mwf_progressInforNode'>"+lp.readyToImportData1+"</div>"+
                "</div>" ;
            this.contentNode.set("html", contentHTML );
            this.progressNode = null;
            this.progressPercentNode = null;
            this.progressInforNode = this.contentNode.getElement(".mwf_progressInforNode");
            this.progressInforNode.setStyles(this.css.progressInforNode)
        }else{
            contentHTML =
                "<div style=\"overflow: hidden\">"+
                "   <div class='mwf_progressNode'>" +
                "       <div class='mwf_progressPercentNode'></div>"+
                "   </div>" +
                "   <div class='mwf_progressInforNode'>"+lp.readyToImportData1+"</div>"+
                "</div>" ;
            this.contentNode.set("html", contentHTML );
            this.progressNode = this.contentNode.getElement(".mwf_progressNode");
            this.progressNode.setStyles(this.css.progressNode);

            this.progressPercentNode = this.contentNode.getElement(".mwf_progressPercentNode");
            this.progressPercentNode.setStyles(this.css.progressPercentNode);

            this.progressInforNode = this.contentNode.getElement(".mwf_progressInforNode");
            this.progressInforNode.setStyles(this.css.progressInforNode)
        }
    },
    showCheckData : function(){
        // this.node.show();
        this.setContentHtml(true);
        this.setMessageTitle( this.lp.checkDataTitle );
        this.setMessageText( this.lp.checkDataContent );
        this.status = "check";
        // this.setSize();
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
        this.dlg.titleAction.show();
        this.dlg.button.show();

        // if( this.bottomNode )this.bottomNode.show();
        // if( this.topCloseAction )this.topCloseAction.show();
        // this.setSize();
    },
    close: function(){
        // this.maskNode.destroy();
        // this.node.destroy();
        this.dlg.close();
    },
    // setSize: function(){
    //     var containerSize = this.importer.container.getSize();
    //     var nodeSize = this.node.getSize();
    //     var top = (containerSize.y - nodeSize.y) / 2;
    //     var left = (containerSize.x - nodeSize.x) / 2;
    //     this.node.setStyles({
    //         "top": (top-40)+"px",
    //         "left": left+"px"
    //     })
    // },
    updateProgress: function(data){
        //status, data.executeCount, data.count, data.failCount
        var lp = this.lp;

        var total = data.count.toInt();
        var processed = data.executeCount.toInt();
        var failCount = data.failCount.toInt();

        var percent = 100*(processed/total);

        var sendDate = new Date();
        var lastDate = this.lastTime || this.currentDate;
        var ms = sendDate.getTime() - lastDate.getTime();
        var speed = ( (processed - ( this.lastProcessed || 0 )) * 1000)/ms ;
        var u = lp.importSpeed;
        speed = speed.round(2);

        this.progressPercentNode.setStyle("width", ""+percent+"%");

        var text = lp.importingDataContent.replace("{speed}",speed).replace("{total}",total).replace("{remaining}",( total - processed ));
        text += failCount ? lp.importingDataErrorContent.replace("{errorCount}",failCount) : "";
        this.progressInforNode.set("text", text);

        this.lastProcessed = processed;
        this.lastTime = new Date();
    },
    transferComplete: function( data ){
        var lp = this.lp;
        var sendDate = new Date();
        var ms = sendDate.getTime()-this.currentDate.getTime();
        if(ms<1000)ms = 1000;

        var timeStr = "";
        if (ms>3600000){
            var h = ms/3600000;
            var m_s = ms % 3600000;
            var m = m_s / 60000;
            var s_s = m_s % 60000;
            var s = s_s/1000;
            timeStr = ""+h.toInt()+lp.hour+m.toInt()+lp.mintue+s.toInt()+lp.second;
        }else if (ms>60000){
            var m = ms / 60000;
            var s_s = ms % 60000;
            var s = s_s/1000;
            timeStr = ""+m.toInt()+lp.mintue+s.toInt()+lp.second;
        }else{
            var s = ms/1000;
            timeStr = ""+s.toInt()+lp.second;
        }

        if( data.status === "导入成功" ){
            var size = data.count;
            var speed = (size * 1000)/ms ;
            var u = lp.importSpeed;
            speed = speed.round(2);

            this.setMessageTitle( lp.importSuccessTitle );
            var text = lp.importSuccessContent.replace("{total}",size).replace("{speed}",speed).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }else if(data.status === "部分成功"){
            var size = data.count;
            var speed = (size * 1000)/ms ;
            var u = lp.importSpeed;
            speed = speed.round(2);

            this.setMessageTitle( lp.importPartSuccessTitle );
            var text = lp.importPartSuccessContent.replace("{total}",size).replace("{speed}",speed).replace("{errorCount}",data.failCount).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }else{ //导入失败
            var size = data.count;
            this.setMessageTitle( lp.importFailTitle );
            var text = lp.importFailContent.replace("{errorInfo}",data.distribution || "").replace("{total}",size).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }
        this.clearMessageProgress();
        this.showCloseAction();
    },
    setMessageText: function( text){
        this.progressInforNode.set("text", text);
    },
    setMessageTitle: function( text){
        this.dlg.titleText.set("text", text);
    },
    clearMessageProgress: function(){
        this.progressNode.destroy();
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