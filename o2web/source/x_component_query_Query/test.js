MWF.xDesktop.requireApp("query.Query", "ImporterRecord", null, false);
MWF.xDesktop.requireApp("query.Query", "Importer", null, false);
var _self = this;
o2.ContractImporter = new Class({
    Extends: MWF.QImporter,
    doImportData: function(){
        //创建数据

        var date = new Date();

        var data = this.getData();

        if( this.options.type === "update" ){
            debugger;
            var multiData = {};
            if( this.options.manyForOne ){
                //this.options.path
                data.each(function(d){
                    var list = multiData[d.docData.contractSerial];
                    if( !list )list = multiData[d.docData.contractSerial] = [];
                    list.push(d.docData);
                })
            }else{
                //this.options.path
            }
            this.updataToDatabase( multiData, data.length );
            //showUpdateImportingStatus
        }else{
            this.lookupAction.getUUID(function(json){
                this.recordId = json.data;
                this.lookupAction.executImportModel(this.json.id, {
                    "recordId": this.recordId,
                    "data" : data
                }, function () {
                    //this.showImportingStatus()
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
        }
    },
    getUpdateAction: function(){
        if( this.updateAction )return;
        if( this.options.path ){
            this.updateAction = o2.Actions.load("x_cms_assemble_control").DataAction.updateWithDocumentWithPath0;
        }else{
            this.updateAction = o2.Actions.load("x_cms_assemble_control").DataAction.updateWithDocument;
        }
    },
    updataToDatabase: function(needUpdateData, totalCount){
        this.getUpdateAction();

        var executeCount = 0;
        var failCount = 0;
        var promiseList = [];
        Object.each(needUpdateData, function(d, key){
            debugger;
            var bundle = this.dataBundleMap[key].bundle;
            var pathList = this.options.path.split(".");
            var path0 = pathList.shift();
            var data = {};
            this.setDataWithPath(data, pathList.join("."), d);
            var argus = [bundle].concat( path0, [data], function () {
                executeCount += d.length;
                this.progressBar.showUpdateImporting( {
                    count: totalCount,
                    executeCount: executeCount,
                    failCount: failCount,
                    status: "导入中"
                });
            }.bind(this), function () {
                failCount += d.length;
                this.progressBar.showUpdateImporting( {
                    count: totalCount,
                    executeCount: executeCount,
                    failCount: failCount,
                    status: "导入中"
                });
            });
            var p = this.updateAction.apply(null, argus);
            promiseList.push(p);
        }.bind(this));

        Promise.all(promiseList).then(function () {
            var status = "导入成功";
            if( failCount > 0 ){
                status = executeCount > 0 ? "部分成功" : "导入失败"
            }
            var d = {
                count: totalCount,
                executeCount: executeCount,
                failCount: failCount,
                status: status,
                data : needUpdateData
            };
            this.progressBar.showUpdateImporting( d );
            this.fireEvent("afterImport", d);
        }.bind(this), function (e) {
            this.app.notice(e.message, "error");
            this.progressBar.close();
        }.bind(this))
    },
    importFromExcel : function(){

        this.rowList = [];

        this.identityMapImported = {};
        this.personMapImported = {};
        this.unitMapImported = {};
        this.groupMapImported = {};

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

            this.progressBar = new o2.ContractImporter.ProgressBar( this, {
                "disableDetailButton": true,
                "onPostShow": function(){
                    this.progressBar.showCheckData();

                    this.importedData = importedData;

                    if( this.importedData.length > 0 )this.importedData.shift();

                    this.fireEvent("beforeImport", [this.importedData]);


                    debugger;

                    Promise.resolve( this.importedData.promise ).then(function () {
                        Promise.resolve( this.queryBundle() ).then(function(map){
                            this.dataBundleMap = map;
                            this.listOrgDataFromDb(
                                this.getImportedOrgData(),
                                function () {
                                    this.importedData.each( function( lineData, lineIndex ){
                                        this.rowList.push( new o2.ContractImporter.Row( this, lineData, lineIndex ) )
                                    }.bind(this));

                                    var isValid = this.json.enableValid ? this.checkImportedData() : this.checkNecessaryImportedData();
                                    Promise.resolve(isValid).then(function ( isValid1 ) {
                                        if( isValid1 ){
                                            this.doImportData();
                                        }else{
                                            this.openImportedErrorDlg();
                                        }
                                    }.bind(this));
                                }.bind(this));
                        }.bind(this));
                    }.bind(this));
                }.bind(this)
            });
            this.progressBar.importerId = this.importerId;
        }.bind(this));
    },
    queryBundle: function(){
        var contractSerials = this.importedData.map(function(d){
            return d[0]; //合同编号
        }).unique();
        return o2.Actions.load("x_query_assemble_surface").ViewAction.executeWithQuery("aeeced46-7251-4c18-8bad-666539525d91", "b94e50e2-488f-461f-80f7-e51902718cb0",{
            filterList: [{
                "logic":"and",
                "path":"contractSerial",
                "comparison": contractSerials.length === 1 ? "equals" : "in",
                "value": contractSerials.join(","),
                "formatType":"textValue"
            }]
        }).then(function(json){
            var map = {};
            json.data.grid.each(function(g){
                map[g.data.contractSerial] = g;
            })
            return map;
        })
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
    isNumberString: function(string){
        return string.toInt().toString() === string;
    }
})

o2.ContractImporter.Row = new Class({
    Extends: MWF.QImporter.Row,
    checkCMS : function( notCheckName ){

        var lp = this.lp;

        var errorTextList = [];
        var errorTextListExcel = [];


        var contractSerial = this.data.contractSerial;
        if( this.importer.options.type === "update" ){
            if(!this.importer.dataBundleMap[this.data.contractSerial] ){
                errorTextList.push(this.getCol("contractSerial", false) + '未在系统中找到合同编号为"'+ contractSerial +'"'+ "的合同档案" + lp.fullstop );
                errorTextListExcel.push( this.getCol("contractSerial", false) + '未在系统中找到合同编号为"'+ contractSerial +'"'+ "的合同档案" + lp.fullstop );
            }
        }else{
            if(this.importer.dataBundleMap[this.data.contractSerial] ){
                errorTextList.push(this.getCol("contractSerial", false) + '系统中已经存在合同编号为"'+ contractSerial +'"'+ "的合同档案，不能重复导入" + lp.fullstop );
                errorTextListExcel.push( this.getCol("contractSerial", false) + '系统中已经存在合同编号为"'+ contractSerial +'"'+ "的合同档案，不能重复导入" + lp.fullstop );
            }
        }

        this.errorTextList = this.errorTextList.concat( errorTextList );
        this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

        if(errorTextList.length > 0){
            return false;
        }

        return true;
    }

});

o2.ContractImporter.ProgressBar = new Class({
    Extends: MWF.QImporter.ProgressBar,
    showUpdateImporting: function( statusData ){
        if( !this.setedContentHtml ){
            this.setContentHtml();
            this.setedContentHtml = true;
        }
        this.currentDate = new Date();
        var data = statusData;
        this.status = data.status;
        if( data.status === "待导入" ) { //有其他人正在导入或上次导入还未完成
            this.setMessageTitle(this.lp.importWaitingTitle);
            this.setMessageText(this.lp.importWaitingContent);
        }else if( data.status === "导入中" ){ //导入中
            this.setMessageTitle( this.lp.importDataTitle );
            this.setMessageText( this.lp.importDataContent.replace( "{count}", data.executeCount));
            this.updateProgress( data );
        }else{ //已经结束, 状态有 "导入成功","部分成功","导入失败"
            this.transferComplete( data );
        }
        // this.setSize();
    }
})



this.define("importFormExcel", function(app, view, options){
    var importer = new o2.ContractImporter(this.form.getApp().content, {
        "name" : view, //（String）必选，导入模型的名称、别名或ID
        "application" : app //（String）必选，导入模型所在应用的名称、别名或ID
    }, options, this.form.getApp(), this);
    importer.load();
})