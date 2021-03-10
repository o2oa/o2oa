MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xScript.Macro", null, false);
MWF.xDesktop.requireApp("query.Query", "lp.zh-cn", null, false);
MWF.xApplication.query.Query.Importer = MWF.QImporter = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "moduleEvents": ["queryLoad", "postLoadJson","postLoad", "queryImportData", "postImportData", "validImport", "queryImportRowData", "postImportRowData"]
    },
    initialize: function(container, options, app, parentMacro){

        this.setOptions(options);

        this.path = "../x_component_query_Query/$Importer/";
        this.cssPath = "../x_component_query_Query/$Importer/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.lp = MWF.xApplication.query.Query.LP;

        this.app = app;

        this.contains = container;

        this.parentMacro = parentMacro;

        this.importerJson = null;

        this.load();

    },
    load: function(){
        this.excelUtils = new MWF.xApplication.query.Query.Importer.ExcelUtils( this );
        this.getImporterJSON( function () {
            this.loadMacro( function () {
                this._loadModuleEvents();
                if (this.fireEvent("queryLoad")){
                    this.importFromExcel()
                }
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
        var path = "/x_component_query_Query/$Main/importer_test.json";
        var r = new Request.JSON({
            url: o2.filterUrl(path),
            secure: false,
            async: false,
            method: "get",
            noCache: false,
            onSuccess: function(responseJSON, responseText){
                this.json = responseJSON;
                this.importerJson = responseJSON.data;
                debugger;
                this.fireEvent("postLoadJson");
            }.bind(this),
            onError: function(text, error){
                alert(error + text);
            }
        });
        r.send();
        return;



        // if (this.json.application){
        //     this.getViewRes = this.lookupAction.getView(this.json.viewName, this.json.application, function(json){
        //         this.importerJson = JSON.decode(json.data.data);
        //         this.json = Object.merge(this.json, json.data);
        //         if (callback) callback();
        //     }.bind(this));
        // }else{
        //     this.getViewRes = this.lookupAction.getViewById(this.json.viewId, function(json){
        //         this.importerJson = JSON.decode(json.data.data);
        //         this.json.application = json.data.query;
        //         this.json = Object.merge(this.json, json.data);
        //         if (callback) callback();
        //     }.bind(this));
        // }
    },
    _loadModuleEvents : function(){
        Object.each(this.importerJson.events, function(e, key){
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
        this.importerJson.selectList.each(function(columnJson, index){
            var dataType = this.importerJson.type === "querytable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;
            if( dataType === "date" )dateColIndexArray.push( index+1 );
        }.bind(this));
        return dateColIndexArray;
    },
    getOrgColIndexArray : function(){
        var orgColIndexArray = [];
        this.importerJson.selectList.each(function(columnJson, index){
            if( columnJson.isName )orgColIndexArray.push( index+1 );
        }.bind(this));
        return orgColIndexArray;
    },
    importFromExcel : function(){

        this.excelUtils.upload( this.getDateColIndexArray(), function (importedData) {

            this.fireEvent("queryImportData");

            var checkAndImport = function () {
                if( !this.checkImportedData( importedData ) ){
                    this.openImportedErrorDlg( importedData );
                }else{
                    this.setImportData( importedData )
                }
            }.bind(this);

            var orgColIndexArray = this.getOrgColIndexArray();
            if( orgColIndexArray.length > 0 ){
                this.listAllOrgDataByImport( orgColIndexArray, importedData, function () {
                    checkAndImport();
                }.bind(this));
            }else{
                checkAndImport();
            }


        }.bind(this));
    },

    setImportData: function(importedData){

        var data = {
            "data" : []
        };

        importedData.each( function( importedLineData, lineIndex ){





            data.data.push( lineData );
        }.bind(this));

        this.fireEvent("postImportData", [data] );

        this.setData( data );
        this.form.notice( this.lp.importSuccess );

    },
    openImportedErrorDlg : function( importedData ){
        var _self = this;

        var objectToString = function (obj, type) {
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
        }

        var htmlArray = ["<table "+ objectToString( this.css.properties ) +" style='"+objectToString( this.css.tableStyles, "style" )+"'>"];

        var titleStyle = objectToString( this.css.titleStyles, "style" );
        htmlArray.push( "<tr>" );
        this.importerJson.selectList.each( function (columnJson, i) {
            htmlArray.push( "<th style='"+titleStyle+"'>"+columnJson.displayName+"</th>" );
        });
        htmlArray.push( "<th style='"+titleStyle+"'> "+this.lp.validationInfor +"</th>" );
        htmlArray.push( "</tr>" );

        var contentStyles = Object.clone( this.css.contentStyles );
        if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
        var contentStyle = objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

        importedData.each( function( lineData, lineIndex ){

            htmlArray.push( "<tr>" );
            this.importerJson.selectList.each( function (columnJson, i) {
                htmlArray.push( "<td style='"+contentStyle+"'>"+ ( lineData[ i ] || '' ).replace(/&#10;/g,"<br/>") +"</td>" ); //换行符&#10;
            });
            htmlArray.push( "<td style='"+contentStyle+"'>"+( lineData.errorTextList ? lineData.errorTextList.join("<br/>") : "" )+"</td>" );
            htmlArray.push( "</tr>" );

        }.bind(this));
        htmlArray.push( "</table>" );

        var div = new Element("div", { style : "padding:10px;", html : htmlArray.join("") });
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
                    "text": this.lp.datagridExport,
                    "action": function () { _self.exportWithImportDataToExcel(columnList, importedData); }
                },
                {
                    "type": "cancel",
                    "text": this.lp.cancel,
                    "action": function () { dlg.close(); }
                }
            ],
            "onPostClose": function(){
                dlg = null;
            }.bind(this)
        });

    },
    checkImportedData : function( importedData ){
        var flag = true;

        var lp = this.lp;
        var columnText =  lp.importValidationColumnText;
        var columnTextExcel = lp.importValidationColumnTextExcel;

        importedData.each( function(lineData, lineIndex){

            var errorTextList = [];
            var errorTextListExcel = [];

            this.importerJson.selectList.each( function (columnJson, i) {

                var colInfor = columnText.replace( "{n}", i );
                var colInforExcel = columnTextExcel.replace( "{n}", this.excelUtils.index2ColName( i-1 ) );

                var d = lineData[i] || "";

                var dataType = this.importerJson.type === "querytable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;


                if( !columnJson.allowEmpty && !d ){
                    errorTextList.push( colInfor + lp.canNotBeEmpty + + lp.fullstop );
                    errorTextListExcel.push( colInforExcel + lp.canNotBeEmpty + + lp.fullstop );
                }

                switch ( dataType ) {
                    case "string":
                        if( columnJson.isName ){
                            var  arr = d.split(/\s*,\s*/g ); //空格,空格
                            arr.each( function(d, idx){
                                var obj = this.getOrgData( d );
                                if( obj.errorText ){
                                    errorTextList.push( colInfor + obj.errorText + + lp.fullstop );
                                    errorTextListExcel.push( colInforExcel + obj.errorText + + lp.fullstop );
                                }
                            }.bind(this));
                        }
                        break;
                    case "Number":
                        if (parseFloat(d).toString() === "NaN"){
                            errorTextList.push( colInfor + d + lp.notValidNumber + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + d + lp.notValidNumber + lp.fullstop );
                        }
                        break;
                    case "Calendar":
                        if( !( isNaN(d) && !isNaN(Date.parse(d) ))){
                            errorTextList.push(colInfor + d + lp.notValidDate + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + d + lp.notValidDate + lp.fullstop );
                        }
                        break;
                    default:
                        break;
                }
            }.bind(this));

            if(errorTextList.length>0){
                lineData.errorTextList = errorTextList;
                lineData.errorTextListExcel = errorTextListExcel;
                flag = false;
            }

            debugger;

        }.bind(this));

        var arg = {
            validted : flag,
            data : importedData
        };
        this.fireEvent( "validImport", [arg] );

        return arg.validted;
    },
    getOrgData : function( str ){
        str = str.trim();
        var flag = str.substr(str.length-2, 2);
        switch (flag.toLowerCase()){
            case "@i":
                return this.identityMapImported[str] || {"errorText": str + this.lp.notExistInSystem };
            case "@p":
                return this.personMapImported[str] || {"errorText":  str + this.lp.notExistInSystem };
            case "@u":
                return this.unitMapImported[str] ||  {"errorText":  str + this.lp.notExistInSystem };
            case "@g":
                return this.groupMapImported[str] ||  {"errorText":  str + this.lp.notExistInSystem };
            default:
                return this.identityMapImported[str] ||
                    this.personMapImported[str] ||
                    this.unitMapImported[str] ||
                    this.groupMapImported[str] ||
                    {"errorText":  str + this.lp.notExistInSystem };

        }
    },
    listAllOrgDataByImport : function (orgColIndexArray, importedData, callback) {
        var identityList = [], personList = [], unitList = [], groupList = [];
        if( orgColIndexArray.length > 0 ){
            importedData.each( function( lineData, lineIndex ){
                // if( lineIndex === 0 )return;

                orgColIndexArray.each( function (colIndex, i) {

                    if( !lineData[colIndex] )return;

                    var arr = lineData[colIndex].split(/\s*,\s*/g );
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
                })
            });
            var identityLoaded, personLoaded, unitLoaded, groupLoaded;
            var check = function () {
                if( identityLoaded && personLoaded && unitLoaded && groupLoaded ){
                    if(callback)callback();
                }
            };

            this.identityMapImported = {};
            if( identityList.length ){
                o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({ identityList : identityList }, function (json) {
                    json.data.each( function (d) { this.identityMapImported[ d.matchKey ] = d; }.bind(this));
                    identityLoaded = true;
                    check();
                }.bind(this))
            }else{
                identityLoaded = true;
                check();
            }

            this.personMapImported = {};
            if( personList.length ){
                o2.Actions.load("x_organization_assemble_express").PersonAction.listObject({ personList : personList }, function (json) {
                    json.data.each( function (d) { this.personMapImported[ d.matchKey ] = d; }.bind(this));
                    personLoaded = true;
                    check();
                }.bind(this))
            }else{
                personLoaded = true;
                check();
            }

            this.unitMapImported = {};
            if( unitList.length ){
                o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({ unitList : unitList }, function (json) {
                    json.data.each( function (d) { this.unitMapImported[ d.matchKey ] = d; }.bind(this));
                    unitLoaded = true;
                    check();
                }.bind(this))
            }else{
                unitLoaded = true;
                check();
            }

            this.groupMapImported = {};
            if( groupList.length ){
                o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({ groupList : groupList }, function (json) {
                    json.data.each( function (d) { this.groupMapImported[ d.matchKey ] = d; }.bind(this));
                    groupLoaded = true;
                    check();
                }.bind(this))
            }else{
                groupLoaded = true;
                check();
            }
        }
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

MWF.xApplication.query.Query.Importer.Item = new Class({
    initialize: function(importer, data,){
        this.importer = importer;
        this.data = data;
        this.clazzType = "item";

        this.load();
    },
    load: function(){
        this.view.fireEvent("queryLoadItemRow", [null, this]);


        this.view.fireEvent("postLoadItemRow", [null, this]);
    }
});

MWF.xApplication.query.Query.Importer.ExcelUtils = new Class({
    initialize: function( importer ){
        this.importer = importer;
        if (!FileReader.prototype.readAsBinaryString) {
            FileReader.prototype.readAsBinaryString = function (fileData) {
                var binary = "";
                var pt = this;
                var reader = new FileReader();
                reader.onload = function (e) {
                    var bytes = new Uint8Array(reader.result);
                    var length = bytes.byteLength;
                    for (var i = 0; i < length; i++) {
                        binary += String.fromCharCode(bytes[i]);
                    }
                    //pt.result  - readonly so assign binary
                    pt.content = binary;
                    pt.onload();
                };
                reader.readAsArrayBuffer(fileData);
            }
        }
    },
    _loadResource : function( callback ){
        if( !window.XLSX || !window.xlsxUtils ){
            var uri = "../x_component_Template/framework/xlsx/xlsx.full.js";
            var uri2 = "../x_component_Template/framework/xlsx/xlsxUtils.js";
            COMMON.AjaxModule.load(uri, function(){
                COMMON.AjaxModule.load(uri2, function(){
                    callback();
                }.bind(this))
            }.bind(this))
        }else{
            callback();
        }
    },
    index2ColName : function( index ){
        if (index < 0) {
            return null;
        }
        var num = 65;// A的Unicode码
        var colName = "";
        do {
            if (colName.length > 0)index--;
            var remainder = index % 26;
            colName =  String.fromCharCode(remainder + num) + colName;
            index = (index - remainder) / 26;
        } while (index > 0);
        return colName;
    },

    upload : function ( dateColIndexArray, callback ) {
        var dateColArray = [];
        dateColIndexArray.each( function (idx) {
            dateColArray.push( this.index2ColName( idx ));
        }.bind(this))


        var uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" accept=\"csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel\" />";
        uploadFileAreaNode.set("html", html);

        var fileUploadNode = uploadFileAreaNode.getFirst();
        fileUploadNode.addEvent("change", function () {
            var files = fileNode.files;
            if (files.length) {
                var file = files.item(0);
                if( file.name.indexOf(" ") > -1 ){
                    this.form.notice( this.importer.lp.uploadedFilesCannotHaveSpaces, "error");
                    return false;
                }

                //第三个参数是日期的列
                this.import( file, function(json){
                    //json为导入的结果
                    if(callback)callback(json);
                    uploadFileAreaNode.destroy();
                }.bind(this), dateColArray ); //["E","F"]

            }
        }.bind(this));
        var fileNode = uploadFileAreaNode.getFirst();
        fileNode.click();
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
                workbook = window.XLSX.read(data, { type: 'binary' });
                //wb.SheetNames[0]是获取Sheets中第一个Sheet的名字
                //wb.Sheets[Sheet名]获取第一个Sheet的数据
                var sheet = workbook.SheetNames[0];
                if (workbook.Sheets.hasOwnProperty(sheet)) {
                    // fromTo = workbook.Sheets[sheet]['!ref'];
                    // console.log(fromTo);
                    debugger;
                    var worksheet = workbook.Sheets[sheet];

                    if( dateColArray && typeOf(dateColArray) == "array" && dateColArray.length ){
                        var rowCount;
                        if( worksheet['!range'] ){
                            rowCount = worksheet['!range'].e.r;
                        }else{
                            var ref = worksheet['!ref'];
                            var arr = ref.split(":");
                            if(arr.length === 2){
                                rowCount = parseInt( arr[1].replace(/[^0-9]/ig,"") );
                            }
                        }
                        if( rowCount ){
                            for( var i=0; i<dateColArray.length; i++ ){
                                for( var j=1; j<=rowCount; j++ ){
                                    var cell = worksheet[ dateColArray[i]+j ];
                                    if( cell ){
                                        delete cell.w; // remove old formatted text
                                        cell.z = 'yyyy-mm-dd'; // set cell format
                                        window.XLSX.utils.format_cell(cell); // this refreshes the formatted text.
                                    }
                                }
                            }
                        }
                    }

                    debugger;

                    var json = window.XLSX.utils.sheet_to_json( worksheet, {header:1} );
                    //var data = window.XLSX.utils.sheet_to_row_object_array(workbook.Sheets[sheet], {dateNF:'YYYY-MM-DD'});
                    if(callback)callback(json);
                    // console.log(JSON.stringify(json));
                    // break; // 如果只取第一张表，就取消注释这行
                }
                // for (var sheet in workbook.Sheets) {
                //     if (workbook.Sheets.hasOwnProperty(sheet)) {
                //         fromTo = workbook.Sheets[sheet]['!ref'];
                //         console.log(fromTo);
                //         var json = window.XLSX.utils.sheet_to_json(workbook.Sheets[sheet]);
                //         console.log(JSON.stringify(json));
                //         // break; // 如果只取第一张表，就取消注释这行
                //     }
                // }
            };
            reader.readAsBinaryString(file);
        })
    }
});