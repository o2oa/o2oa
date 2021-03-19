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
        "moduleEvents": ["queryLoad", "postLoadJson","postLoad", "beforeImport", "afterImport", "validImport", "beforeCreateRowData", "afterCreateRowData", "saveRow"]
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
        var path = "/x_component_query_Query/$Main/importer_test.json"; //_querytable
        var r = new Request.JSON({
            url: o2.filterUrl(path),
            secure: false,
            async: false,
            method: "get",
            noCache: false,
            onSuccess: function(responseJSON, responseText){
                this.json = responseJSON;
                debugger;
                if(callback)callback();
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
        this.json.data.selectList.each(function(columnJson, index){
            var dataType = this.json.type === "querytable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;
            if( dataType === "date" )dateColIndexArray.push( index );
        }.bind(this));
        return dateColIndexArray;
    },
    getOrgColIndexArray : function(){
        var orgColIndexArray = [];
        this.json.data.selectList.each(function(columnJson, index){
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

            this.importedData = importedData;

            if( this.importedData.length > 0 )this.importedData.shift();

            this.fireEvent("beforeImport");

            this.listAllOrgDataByImport( function () {
                this.importedData.each( function( lineData, lineIndex ){
                    this.rowList.push( new MWF.xApplication.query.Query.Importer.Row( this, lineData, lineIndex ) )
                }.bind(this));

                var isValid = this.json.enableValid ? this.checkImportedData() : this.checkNecessaryImportedData();
                if( isValid ){
                    this.doImportData();
                }else{
                    this.openImportedErrorDlg();
                }
            }.bind(this));


        }.bind(this));
    },
    getData : function(){
        return ( this.rowList || [] ).map( function(row){
            return row.data;
        })
    },
    doImportData: function(){
        //创建数据
        this.rowList.each( function( row, i ){
            row.createData();
        }.bind(this));

        //再次校验数据（计算的内容）
        var flag = true;
        this.rowList.each( function(row, index){
            if( row.errorTextList.length )flag = false;
        }.bind(this));
        if( !flag ){
            var arg = {
                validted : false,
                data : this.importedData,
                rowList : this.rowList
            };
            this.fireEvent( "validImport", [arg] );

            flag = arg.validted;
        }
        if( !flag ){
            this.openImportedErrorDlg();
            return;
        }

        if( this.json.type === "querytable" ){
            o2.Actions.load("x_query_assemble_designer").TableAction.rowSave(
                this.json.data.querytable[0].id || this.json.data.querytable[0].name,
                this.getData(),
                function(json){
                    this.fireEvent("afterImport", json );
                    this.app.notice( this.lp.importSuccess );
                }.bind(this)
            );
        }else if( this.json.type === "cms" ){

        }

    },
    openImportedErrorDlg : function(){
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
        this.json.data.selectList.each( function (columnJson, i) {
            htmlArray.push( "<th style='"+titleStyle+"'>"+columnJson.displayName+"</th>" );
        });
        htmlArray.push( "<th style='"+titleStyle+"'> "+this.lp.validationInfor +"</th>" );
        htmlArray.push( "</tr>" );

        var contentStyles = Object.clone( this.css.contentStyles );
        if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
        var contentStyle = objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

        this.rowList.each( function( row, lineIndex ){

            var lineData = row.importedData;

            htmlArray.push( "<tr>" );
            this.json.data.selectList.each( function (columnJson, i) {
                htmlArray.push( "<td style='"+contentStyle+"'>"+ ( lineData[ i ] || '' ).replace(/&#10;/g,"<br/>") +"</td>" ); //换行符&#10;
            });
            htmlArray.push( "<td style='"+contentStyle+"'>"+( row.errorTextList ? row.errorTextList.join("<br/>") : "" )+"</td>" );
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
                // {
                //     "type": "exportWithError",
                //     "text": this.lp.showWithExcel,
                //     "action": function () { _self.exportWithImportDataToExcel(columnList, this.importedData); }
                // },
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
    //必须校验的数据
    checkNecessaryImportedData: function(){
        var flag = true;


        if( this.json.type === "cms" ){

            this.rowList.each( function(row, index){
                if( !row.checkCMS() )flag = false;
            }.bind(this));

        }

        var arg = {
            validted : flag,
            data : this.importedData,
            rowList : this.rowList
        };
        this.fireEvent( "validImport", [arg] );

        return arg.validted;
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

        return arg.validted;
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


        this.importer.json.data.selectList.each( function (columnJson, i) {

            var colInfor = columnText.replace( "{n}", i+1 );
            var colInforExcel = columnTextExcel.replace( "{n}", this.importer.excelUtils.index2ColName( i ) );

            var value = this.importedData[i] || "";

            var dataType = this.importer.json.type === "querytable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;

            debugger;

            if( !columnJson.allowEmpty && !value ){
                errorTextList.push( colInfor + lp.canNotBeEmpty + lp.fullstop );
                errorTextListExcel.push( colInforExcel + lp.canNotBeEmpty + lp.fullstop );
            }

            if( columnJson.validFieldType !== false && value ){

                switch ( dataType ) {
                    case "string":
                    case "stringList":
                        if( columnJson.isName ){
                            var  arr = value.split(/\s*,\s*/g ); //空格,空格
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
                        if (parseFloat(value).toString() === "NaN"){
                            errorTextList.push( colInfor + value + lp.notValidNumber + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidNumber + lp.fullstop );
                        }
                        break;
                    case "numberList":
                    case "integerList":
                    case "longList":
                    case "doubleList":
                        var  arr = value.split(/\s*,\s*/g ); //空格,空格
                        arr.each( function(d, idx){
                            if (parseFloat(d).toString() === "NaN"){
                                errorTextList.push( colInfor + d + lp.notValidNumber + lp.fullstop );
                                errorTextListExcel.push( colInforExcel + d + lp.notValidNumber + lp.fullstop );
                            }
                        }.bind(this));
                        break;
                    case "date":
                    case "dateTime":
                        if( !( isNaN(value) && !isNaN(Date.parse(value) ))){
                            errorTextList.push(colInfor + value + lp.notValidDate + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidDate + lp.fullstop );
                        }
                        break;
                    case "dateList":
                    case "dateTimeList":
                        var  arr = value.split(/\s*,\s*/g ); //空格,空格
                        arr.each( function(d, idx){
                            if( !( isNaN(d) && !isNaN(Date.parse(d) ))){
                                errorTextList.push(colInfor + d + lp.notValidDate + lp.fullstop );
                                errorTextListExcel.push( colInforExcel + d + lp.notValidDate + lp.fullstop );
                            }
                        }.bind(this));
                        break;
                    default:
                        break;
                }
            }
        }.bind(this));

        this.errorTextList = this.errorTextList.concat( errorTextList );
        this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

        if( this.importer.json.type === "cms" ){
            this.checkCMS();
        }

        if(this.errorTextList.length>0){
            return false;
        }

        return true;


    },
    checkCMS : function(){
        var lp = this.lp;

        var columnText =  lp.importValidationColumnText;
        var columnTextExcel = lp.importValidationColumnTextExcel;

        var errorTextList = [];
        var errorTextListExcel = [];


        this.importer.json.data.selectList.each( function (columnJson, i) {

            var colInfor = columnText.replace( "{n}", i+1 );
            var colInforExcel = columnTextExcel.replace( "{n}", this.importer.excelUtils.index2ColName( i ) );

            var value = this.importedData[i] || "";

            var dataType = columnJson.dataType_CMSProcess;
            if( value && ["string","stringList"].contains(columnJson.dataType_CMSProcess) ){
                if( columnJson.isTitle ){
                    if( value.length > 70 ){
                        errorTextList.push(colInfor + value + lp.cmsTitleLengthInfor + lp.fullstop );
                        errorTextListExcel.push( colInforExcel + value + lp.cmsTitleLengthInfor + lp.fullstop );
                    }
                }
                if( columnJson.isSummary ){
                    if( value.length > 70 ){
                        errorTextList.push(colInfor + value + lp.cmsSummaryLengthInfor + lp.fullstop );
                        errorTextListExcel.push( colInforExcel + value + lp.cmsSummaryLengthInfor + lp.fullstop );
                    }
                }
                if( columnJson.isName ){
                    if( columnJson.isPublisher ){ //发布者必须要校验
                        var  arr = value.split(/\s*,\s*/g ); //空格,空格
                        arr.each( function(d, idx){
                            var obj = this.importer.getOrgData( d );
                            if( obj.errorText ){
                                errorTextList.push( colInfor + obj.errorText + lp.fullstop );
                                errorTextListExcel.push( colInforExcel + obj.errorText + lp.fullstop );
                            }
                        }.bind(this));
                    }
                }
             }

        }.bind(this));

        this.errorTextList = this.errorTextList.concat( errorTextList );
        this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

        if(errorTextList.length > 0){
            return false;
        }

        return true;
    },
    createData : function(){

        if( this.importer.json.type === "cms" ){
            this.document = {
                categoryId : this.importer.json.data.category[0].id,
                readerList : [],
                authorList : []
            };
        }

        this.importer.fireEvent("beforeCreateRowData", [null, this]);

        debugger;
        this.importer.json.data.selectList.each( function (columnJson, i) {

            var dataType = this.importer.json.type === "querytable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;

            var value = this.importedData[i] || "";

            var data;

            if( value ){

                switch ( dataType ) {
                    case "string":
                    case "stringList":
                        if( columnJson.isName ){
                            var  arr = value.split(/\s*,\s*/g ); //空格,空格
                            if( this.importer.json.type === "querytable" ){
                                data = arr
                            }else{
                                data = arr.map( function(d, idx){
                                    return this.importer.getOrgData( d, true, true ) || d;
                                }.bind(this)).clean();
                            }
                        }else{
                            data = dataType === "string" ? value : value.split(/\s*,\s*/g );
                        }
                        break;
                    case "number":
                    case "integer":
                    case "long":
                        data = parseInt( value );
                        break;
                    case "double":
                        data = parseFloat(value);
                        break;
                    case "numberList":
                    case "integerList":
                    case "longList":
                        data = value.split(/\s*,\s*/g ).map( function(d, idx){ return parseInt( d ); }.bind(this)).clean();
                        break;
                    case "doubleList":
                        data = value.split(/\s*,\s*/g ).map( function(d, idx){ return parseFloat( d ); }.bind(this)).clean();
                        break;
                    case "date":
                        data = Date.parse(value).format( "%Y-%m-%d" );
                        break;
                    case "dateTime":
                        data = Date.parse(value).format( "db" );
                        break;
                    case "dateList":
                        data = value.split(/\s*,\s*/g ).map( function(d, idx){ return Date.parse(d).format( "%Y-%m-%d" ); }.bind(this)).clean();
                        break;
                    case "dateTimeList":
                        data = value.split(/\s*,\s*/g ).map( function(d, idx){ return Date.parse(d).format( "db" ); }.bind(this)).clean();
                        break;
                    default:
                        data = value;
                        break;
                }

                if( this.importer.json.type === "querytable" ){
                    this.data[ columnJson.path ] = data;
                }else{
                    var names = columnJson.path.split(".");
                    var d = this.data;
                    Array.each(names, function (n, idx) {
                        if( idx === names.length -1 )return;
                        if ( !d[n] ) d[n] = {};
                        d = d[n];
                    });
                    d[names[names.length -1]] = data;
                }

                if( data && this.importer.json.type === "cms" ){
                    this.parseCMSDocument_FromExcel( columnJson, i, data )
                }

            }

        }.bind(this));

        this.importer.json.data.calculateFieldList.each( function (fieldJson, i) {
            if( fieldJson.valueScript ){
                var data = this.importer.Macro.exec( fieldJson.valueScript, this );

                if( this.importer.json.type === "querytable" ){
                    this.data[ fieldJson.path ] = data;
                }else{
                    var names = fieldJson.path.split(".");
                    var d = this.data;
                    Array.each(names, function (n, idx) {
                        if( idx === names.length -1 )return;
                        if ( !d[n] ) d[n] = {};
                        d = d[n];
                    });
                    d[names[names.length -1]] = data;
                }

                if( this.importer.json.type === "cms" ){
                    this.parseCMSDocument_CalculateField( fieldJson, data )
                }
            }
        }.bind(this));

        if( this.importer.json.type === "cms" ){
            this.document.docData = this.data;
            if( !this.document.identity ){ //没有发布身份，则设置
                var identityList = layout.session.user.identityList;
                if( identityList && identityList.length ){
                    this.document.identity = identityList[0].distinguishedName
                }
            }
        }

        console.log( this.data );


        this.importer.fireEvent("afterCreateRowData", [null, this]);
    },
    parseCMSDocument_FromExcel : function( columnJson, i, data, excelValue ){
        var lp = this.importer.lp;
        var columnText =  lp.importValidationColumnText;
        var columnTextExcel = lp.importValidationColumnTextExcel;

        var colInfor = columnText.replace( "{n}", i+1 );
        var colInforExcel = columnTextExcel.replace( "{n}", this.importer.excelUtils.index2ColName( i ) );

        if( columnJson.isTitle ){
            this.document.title = data;
        }
        if( columnJson.isSummary ){
            this.document.summary = data;
        }
        if( columnJson.isName ){
            if( columnJson.isPublisher && data.length){
               this.document.identity = data[0].distinguishedName
            }
            if( columnJson.isAuthor ){
                var array = this.parseCMSReadAndAuthor( data );
                this.document.authorList = this.document.authorList.concat( array )
            }
            if( columnJson.isReader ){
                var array = this.parseCMSReadAndAuthor( data );
                this.document.readerList = this.document.readerList.concat( array )
            }
        }
        if( columnJson.isTop ){
            if( [lp.yes,"yes","true"].contains(data) ){
                this.document.isTop = true;
            }
        }
    },
    parseCMSDocument_CalculateField : function( fieldJson, data ){
        var lp = this.importer.lp;

        var fieldInfor = lp.caculateFieldValidationText + ( fieldJson.displayName || fieldJson.path );

        if( columnJson.isTitle ){
            if( data.length > 70 ){
                this.errorTextList.push(  fieldInfor +'"'+ data +'"'+ lp.cmsTitleLengthInfor + lp.fullstop );
                this.errorTextListExcel.push(  fieldInfor +'"'+ data +'"'+ + lp.cmsTitleLengthInfor + lp.fullstop );
            }else{
                this.document.title = data;
            }
        }
        if( columnJson.isSummary ){
            if( data.length > 70 ){
                this.errorTextList.push( fieldInfor +'"'+ data +'"'+ lp.cmsSummaryLengthInfor + lp.fullstop );
                this.errorTextListExcel.push( fieldInfor +'"'+ data +'"'+ lp.cmsSummaryLengthInfor + lp.fullstop );
            }else{
                this.document.summary = data;
            }
        }
        if( columnJson.isPublisher ){
            var d = (typeOf(data) === "array" && data.length) ? data[0] : data;
            if( d ){
                d = typeOf(d) === "object" ? d.distinguishedName : d;
                this.document.identity = data[0].distinguishedName
            }
        }
        if( columnJson.isAuthor ){
            var array = this.parseCMSReadAndAuthor( data );
            this.document.authorList = this.document.authorList.concat( array )
        }
        if( columnJson.isReader ){
            var array = this.parseCMSReadAndAuthor( data );
            this.document.readerList = this.document.readerList.concat( array )
        }
        if( columnJson.isTop ){
            if( [lp.yes,"yes","true"].contains(data) ){
                this.document.isTop = true;
            }
        }
    },
    parseCMSReadAndAuthor : function( data ){
        var cnArray = ["组织","群组","人员","人员","角色"];
        var keyArray = ["U","G","I","P","R"];
        if( typeOf(data) !== "array" )data = [data];
        return data.map( function( d ){
            var dn = typeOf( d ) === "string" ? d : d.distinguishedName;
            var index = keyArray.indexOf(dn.substr(dn.length-1, 1));
            if( index > -1 ){
                return {
                    "permission" : "阅读",
                    "permissionObjectType" : cnArray[ index ],
                    "permissionObjectName" : dn
                }
            }
        }).clean()
    },
    save : function () {
        if( this.json.type === "querytable" ){

        }else if( this.json.type === "cms" ){

        }else if( this.json.type === "process" ){

        }
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