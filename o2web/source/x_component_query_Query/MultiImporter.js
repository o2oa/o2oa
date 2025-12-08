this.include({'type':'service', name:'o2ExcelUtilsV2'});
MWF.xDesktop.requireApp("query.Query", "Importer", null, false);
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
MWF.QMultiImporter = new Class(
    {
        Implements: [Options, Events],
        Extends: MWF.widget.Common,
        options: {
            "style": "default"
        },
        initialize: function(container, json, options, app, parentMacro){
            //json格式
            // {
            //      name: '', //下载的文件
            //     "importers": [
            //         {
            //             application: '', //导入模型应用名称
            //             name: '', //导入模型
            //             dataType: 'master', //master或slave, master表示主文档数据，slave下层数据
            //             matchKeys: ['employee'] //和数据库中匹配的字段，判断导入的文档是否已经存在，进一步执行修改还是新增操作。
            //         },
            //         {
            //             application: '', //导入模型应用名称
            //             name: '', //导入模型
            //             dataType: 'slave', //master或slave, master表示主文档数据，slave下层数据
            //             path: 'datatable.data', //数据路径，数据表格id+data
            //             matchMasterKeys: [{ //关联字段
            //                 master: 'employee', //主文档的字段
            //                 slave: 'employee', //下层数据的字段
            //             }]
            //         }
            //     ]
            // }

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
                    importer.multiImporter = this;
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
        getMainImporter: function(){
            if( this.mainImporter ){
                return this.mainImporter;
            }
            var masters = this.importerList.filter((importer)=>{
                return importer.json.dataType === 'master';
            });
            this.mainImporter = masters.length ? masters[0] : this.importerList[0];
            return this.mainImporter;
        },
        getTargetType: function(){
            //导入的类型，cms还是process
            return this.getMainImporter().json.type;
        },
        getTarget: function(){
            var mainImpoter = this.getMainImporter();
            return mainImpoter.json.type === 'cms' ?
                mainImpoter.json.data.category :
                mainImpoter.json.data.process;
        },
        createLoadding: function(){
            this.loadingAreaNode = new Element("div", {"styles": this.css.viewLoadingAreaNode}).inject(this.contentAreaNode);
            new Element("div", {"styles": {"height": "5px"}}).inject(this.loadingAreaNode);
            var loadingNode = new Element("div", {"styles": this.css.viewLoadingNode}).inject(this.loadingAreaNode);
            new Element("div", {"styles": this.css.viewLoadingIconNode}).inject(loadingNode);
            var loadingTextNode = new Element("div", {"styles": this.css.viewLoadingTextNode}).inject(loadingNode);
            loadingTextNode.set("text", "loading...");
        },
        copyView: function(){
            // {
            //     "type": "cms",
            //     "viewName": "合同档案",
            //     "categoryList": [{
            //         "name": "合同档案",
            //         "id": "f4bc07cb-fefd-4eb0-9a1e-d7795f8d9816"
            //     }],
            //     "processList": [{
            //         "name": "指标流程",
            //         "alias": "",
            //         "id": "205d2d6d-8ed7-49e2-b788-1f35446a59da"
            //     }],
            //     "fieldList": ["subject"]
            // }
            var importer = this.getMainImporter();
            var type = this.getTargetType();
            var body = {
                type: type,
                viewName: this.json.name,
                fieldList: this.getMatchKeys()
            };
            if( body.fieldList.length === 0 ){
                return;
            }
            switch (type){
                case 'cms':
                    body.categoryList = [importer.json.data.category]; break;
                case 'process':
                    body.processList = [importer.json.data.process]; break;
                default:
                    this.notice('导入失败，导入模型的导入目标设置成了数据表，请联系管理员！', 'error');
                    return;
            }
            this.matchViewJson = o2.Actions.load('x_program_center').InvokeAction.execute('o2ExcelViewCopyer', body, (json)=>{
                return {
                    view: json.newViewId,
                    application: json.application
                };
            });
        },
        getMatchKeys: function(){
            return this.importerList.map((importer)=>{
                return this.getMatchKeysWithImporter(importer);
            }).flat(Infinity).unique();
        },
        getMatchKeysWithImporter: function(importer){
            if( importer.json.dataType === 'master' ){
                return importer.json.matchKeys || [];
            }else{
                return (importer.json.matchMasterKeys || []).map((matchKey)=>{
                    return matchKey.master;
                });
            }
        },
        getDateColIndexArray: function (){
            return this.importerList.map((importer)=>{
                return importer.getDateColIndexArray();
            });
        },
        importFromExcel: function(){
            var masters = this.json.importers.filter((importer)=>{return importer.dataType === 'master';});
            if(masters.length > 1){
                this.notice('导入失败，有两个以上导入模型同时为主数据模型，请联系管理员！', 'error');
                return;
            }

            var p = !this.importerList ? this.load() : null;
            Promise.resolve(p).then(()=>{
                this.copyView();

                this.excelImporter = new MWF.QMultiImporter.ExcelImporter({
                    dateColIndexes: this.getDateColIndexArray()
                });

                this.progressDialog = new MWF.QMultiImporter.ProgressDialog(this);
                this.progressDialog.multipleImporter = this;

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
                        var hasError = arr.some((result)=>{
                            return result.status === 'error';
                        });
                        hasError ? this.openErrorDlg(arr) : this.doImportData(arr);
                    });
                }.bind(this));
            })
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
        openErrorDlg : function(resultList){
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
                    // {
                    //     "type": "exportWithError",
                    //     "text": this.lp.exportExcel,
                    //     "action": function () { _self.exportWithImportDataToExcel(); }
                    // },
                    {
                        "type": "cancel",
                        "text": this.lp.cancel,
                        "action": function () { dlg.close(); }
                    }
                ],
                "onPostShow": function () {
                    var contentStyles = Object.clone( this.css.contentStyles );
                    if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
                    var contentStyleStr = this.objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

                    var htmlArray = [];
                    resultList.forEach((result)=>{
                        htmlArray.push(`<div style='font-size:16px;margin-bottom:5px;'>${result.importer.json.name}：</div>`)
                        htmlArray.push("<table "+ this.objectToString( this.css.properties ) +" style='"+this.objectToString( this.css.tableStyles, "style" )+"'>");
                        if(result.status==='error'){
                            htmlArray.push( result.importer._getErrorHeadHtml() );
                            htmlArray.push( result.importer._getErrorContentHtml() );
                        }else{
                            htmlArray.push(
                                `<tr><td style=${contentStyleStr}><div style='color:green;'>校验通过</div></td></tr>`
                            );
                        }
                        htmlArray.push( "</table>" );
                        htmlArray.push("<div style='height:15px;'></div>")
                    })
                    div.set("html" , htmlArray.join(''));
                }.bind(this),
                "onPostClose": function(){
                    dlg = null;
                }.bind(this)
            });

        },
        openResultDlg : function(resultList){
            if(this.progressDialog)this.progressDialog.close();


            var _self = this;

            var div = new Element("div", { style : "flex:1;height: 520px;" });
            var dlg = o2.DL.open({
                "style" : "user",
                "title": '导入详情',
                "content": div,
                "offset": {"y": 0},
                "isMax": true,
                "width": 1200,
                "height": 700,
                "buttonList": [
                    {
                        "type": "cancel",
                        "text": '关闭',
                        "action": function () { dlg.close(); }
                    }
                ],
                "onPostShow": function () {
                    MWF.xDesktop.requireApp("query.Query", "Statement", function () {
                        debugger;
                        this.statement = new MWF.xApplication.query.Query.Statement(dlg.content.getFirst(), {
                            "statementName" : "o2ExcelMultipleImportDataLog",
                            "filter": [
                                {
                                    "path":"o.mainLogId", //查询语句格式为jpql使用o.title，为原生sql中使用xtitle
                                    "comparison":"equals",
                                    "value": this.log.id,
                                    "formatType":"textValue"
                                }
                            ]
                        }, {}, this.app, this.parentMacro);
                    }.bind(this));
                }.bind(this),
                "onPostClose": function(){
                    dlg = null;
                }.bind(this)
            });

        },
        parseData: function(resultList){
            resultList.forEach((result)=>{
                if( result.importer.json.dataType !== 'master' ){
                    result.masterDataMap = this.toMasterDataMap(result);
                }
            });

            var masters = resultList.filter((r)=>{return r.importer.json.dataType === 'master';});
            var master = masters[0] || null;
            if(master){
                var r = master.importer;
                var masterData = r.getData();
                resultList.forEach((result)=>{
                    if( result.importer.json.dataType !== 'master' ){
                        var matchMasterKeys = result.importer.json.matchMasterKeys;
                        masterData.forEach((d)=>{
                            var data;
                            switch(r.json.type){
                                case 'cms': data = d.docData; break;
                                case 'process': data = d.data; break;
                                default: data = d.srcData; break;
                            }
                            var key = matchMasterKeys.map((obj)=>{ return data[obj.master]; }).join('_');
                            if( result.masterDataMap[key] ){
                                this.setDataWithPath(data, result.importer.json.path, result.masterDataMap[key].list);
                                delete result.masterDataMap[key];
                            }
                        });
                    }
                });
                master.masterData = masterData;
            }
        },
        toMasterDataMap: function(slaveResult){
            var r = slaveResult.importer;
            var slaveDataList = r.getData();
            var map = {};
            var matchMasterKeys = r.json.matchMasterKeys;
            slaveDataList.forEach((d)=>{
                var data;
                switch(r.json.type){
                    case 'cms': data = d.docData; break;
                    case 'process': data = d.data; break;
                    default: data = d.srcData; break;
                }
                var key = matchMasterKeys.map((obj)=>{ return data[obj.slave]; }).join('_');
                !map[key] && (map[key] = {key: key, path:r.json.path, list: []});
                map[key].list.push(data);
            });
            return map;
        },
        matchDoc: function (result){
            if( !this.matchViewJson ){
                return;
            }
            var matchKeys = this.getMatchKeysWithImporter(result.importer);
            if( !matchKeys.length ){
                return;
            }
            debugger;
            var matchObjList = matchKeys.map((key)=>{
                var list = [];
                if( result.importer.json.dataType === 'master' ){
                    list = result.masterData.map((data)=>{
                        return this.getDataWithPath(data.docData || data.data || data.srcData, key);
                    }).unique();
                }else{
                    Object.each(result.masterDataMap, (data, key1)=>{
                        data.list.forEach((d)=>{
                            list.push( this.getDataWithPath(d, key));
                        }).unique();
                    });
                }
                return {key: key, list: list};
            });
            return Promise.resolve(this.matchViewJson).then((viewJson)=>{
                viewJson.filter = matchObjList.map((matchObj)=>{
                    return {
                        "logic":"and",
                        "path": matchObj.key,
                        "comparison":"in",
                        "value": matchObj.list.join(','),
                        "formatType":"textValue"
                    };
                });
                return o2.api.view.lookup(viewJson).then((data)=>{
                    //data 为返回的数据。
                    var map = {};
                    debugger;
                    data.grid.forEach((row)=>{
                        var mapKey = matchKeys.map(key=>{
                            return row.data[key];
                        }).join('_');
                        map[mapKey] = row.bundle;
                    });

                    if( result.importer.json.dataType === 'master' ){
                        result.masterData.forEach((data)=>{
                            var mapKey = matchKeys.map(key=>{
                                return this.getDataWithPath(data.docData || data.data || data.srcData, key);
                            }).join('_');
                            map[mapKey] && (data.id = map[mapKey]);
                        });
                    }else{
                        if( result.masterDataMap ){
                            Object.each(result.masterDataMap, (data, key1)=>{
                                map[key1] && (data.id = map[key1]);
                            });
                        }
                    }
                });
            });
        },
        doImportData: function(resultList){

            resultList.forEach((r)=>{
                if( r.importer !== this.getMainImporter() ){
                    r.importer.progressBar.hide();
                }else{
                    r.importer.progressBar.setMessageTitle(`正在往“${this.getTarget().name}”导入数据`);
                }
            })

            // if(this.progressDialog)this.progressDialog.close();
            //创建数据
            //再次校验数据（计算的内容）
            var date = new Date();

            this.parseData(resultList);

            //总导入主文档数
            this.totalCount = 0;
            resultList.forEach( (result)=>{
                if( result.importer.json.dataType === 'master' ) {
                    result.totalCount = result.masterData.length;
                    this.totalCount += result.totalCount;
                }else{
                    result.totalCount = Object.keys(result.masterDataMap).length;
                    this.totalCount += result.totalCount;
                }
            })

            var actionPromises = [];
            var completedCount = 0;
            var checkAllCompleted = ()=>{
                completedCount++;
                if( resultList.length === completedCount ){
                    Promise.allSettled(actionPromises).then(() => {
                        this._saveLog();
                    })
                }
            }
            //保存主数据
            resultList.forEach( (result)=>{
                var p = this.matchDoc(result);
                Promise.resolve(p).then(()=>{

                    console.log(result.masterData || result.masterDataMap);

                    if( result.importer.json.dataType === 'master' ){
                        result.masterData.forEach((d)=>{
                            var promise;
                            switch (this.getTargetType()) {
                                case 'cms':
                                    promise = !!d.id ? this.updateDocument(result, d) : this.createDocument(result, d);
                                    break;
                                case 'process':
                                    promise = !!d.id ? this.updateWork(result, d) : this.createWork(result, d);
                                    break;
                            }
                            actionPromises.push(promise);
                        })
                    }else{
                        Object.each(result.masterDataMap, (d)=>{
                            if(!d.id){
                                this.logFailure(result.importer, d, '根据关键字未在系统和主表中匹配到主数据。');
                            }else{

                            }
                        })
                    }

                    checkAllCompleted();
                })
            });

            return;



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
        getErrorText: function (xhr) {
            var responseJSON = JSON.parse( (err.xhr || err).responseText || '' );
            return responseJSON.message; //message为错误提示文本
        },
        updateDocument: function(result, data){
            const method = o2.Actions.load('x_cms_assemble_control').DataAction.updateWithDocument;
            return method(data.id, data.docData, (json)=>{
                return this.logSuccess(result, data, '修改数据');
            }, (xhr)=>{
                this.logFailure(result, data, this.getErrorText(xhr));
                return true;
            });
        },
        createDocument: function(result, data){
            const method = o2.Actions.load('x_cms_assemble_control').DocumentAction.persist_publishContent;
            return method(data, (json)=>{
                data.id = json.data.id;
                return this.logSuccess(result, data, '创建文档');
            }, (xhr)=>{
                this.logFailure(result, data, this.getErrorText(xhr));
                return true;
            });
        },
        updateWork: function(result, data){
            const method = o2.Actions.load('x_processplatform_assemble_surface').DataAction.updateWithJob;
            return method(data.id, data.data, (json)=>{
                return this.logSuccess(result, data, '修改数据');
            }, (xhr)=>{
                this.logFailure(result, data, this.getErrorText(xhr))
                return true;
            })
        },
        createWork: function(result, data){
            const method = o2.Actions.load('x_processplatform_assemble_surface').WorkAction.create;
            return method(data, (json)=>{
                data.id = json.data.job;
                return this.logSuccess(result, data, '发起流程');
            }, (xhr)=>{
                this.logFailure(result, data, this.getErrorText(xhr));
                return true;
            });
        },
        logFailure: function(result, data, errorText){
            return this._addlog(result, data, null, errorText);
        },
        logSuccess: function(result, data, operationType){
            return this._addlog(result, data, operationType);
        },
        _addlog: function(result, data, operationType, errorText, saveFlag){
            debugger;
            var importer = result.importer;
            var mainImpoter = this.getMainImporter();
            if(!this.log){
                this.log = {
                    "person": layout.session.user.distinguishedName,
                    "fileName": this.excelImporter.file.name,
                    "totalCount": this.totalCount,
                    "successCount": 0,
                    "failureCount": 0,
                    "importerName": this.importerList.map((importer)=>{return importer.json.name}),
                    "importer": this.importerList.map((importer)=>{return importer.json.id}),
                    "targetType": mainImpoter.json.type,
                    "targetName": this.getTarget().name,
                    "targetId": this.getTarget().id,
                }
                !errorText ? this.log.successCount++ : this.log.errorCount++;

                mainImpoter.progressBar.showImporting(this.log);

                this.logTable = new o2.api.Table('o2ExcelMultipleImportLog');
                this.logPromise = this.logTable.addRow(this.log, (json)=>{
                    this.log.id = json.data.id;
                    return importer.saveLog(this.log, data, operationType, errorText);
                });
                return this.logPromise;
            }else{
                !errorText ? this.log.successCount++ : this.log.errorCount++;

                mainImpoter.progressBar.showImporting(this.log);

                if(!!saveFlag)this.logTable.updateRow(this.log.id, this.log);
                return importer.saveLog(this.log, data, operationType, errorText);
            }
        },
        _saveLog: function(){
            debugger;
            if(this.log && this.logTable){
                return this.logTable.updateRow(this.log.id, this.log);
            }
        },
        getDataWithPath: function(obj, path){
            var idList = path.split(".");
            idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });

            var lastIndex = idList.length - 1;

            for(var i=0; i<=lastIndex; i++){
                var id = idList[i];
                if( !id && id !== 0 )return null;
                if( ["object","array"].contains(o2.typeOf(obj)) ){
                    if( i === lastIndex ){
                        return obj[id];
                    }else{
                        obj = obj[id];
                    }
                }else{
                    return null;
                }
            }
        },
        setDataWithPath: function(obj, path, data){
            var isNumberString = function(string){
                return string.toInt().toString() === string;
            }
            var names = path.split(".");
            var d = obj;
            Array.each(names, function (n, idx) {
                if( idx === names.length -1 )return;
                if ( !d[n] ){
                    var value = isNumberString( names[idx+1] ) ? [] : {};
                    var n1 = isNumberString( n ) ? n.toInt() : n;
                    d[n1] = value;
                    d = d[n1];
                }else{
                    d = d[n];
                }
            }.bind(this));
            d[names[names.length -1]] = data;
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
    getSampleData: function (d){
        debugger;
        var data;
        switch(this.multiImporter.getTargetType()){
            case 'cms': data = d.docData; break;
            case 'process': data = d.data; break;
            default: data = d.srcData; break;
        }
        var sampleData = [];
        var mainImporter = this.multiImporter.getMainImporter();
        var columnList = mainImporter.json.data.columnList;
        for( var i=0; i<columnList.length && i<10; i++ ){
            var column = columnList[i];
            sampleData.push({
                t: column.displayName,
                v: this.multiImporter.getDataWithPath(data, column.path)
            });
        }
        return sampleData;
    },
    saveLog: function(mainLog, data, operationType, errorText){
        if( !this.logTable )this.logTable = new o2.api.Table("o2ExcelMultipleImportDataLog");
        // {
        //     "person": "",
        //     "fileName": "",
        //     "sheetName": "",
        //     "status": "",
        //     "errorText": "",
        //     "importerName": "",
        //     "importer": "",
        //     "targetType": "",
        //     "targetName": "",
        //     "targetId": "",
        //     "data": "",
        //     "docId": "",
        //     "mainLogId": ""
        // }
        var log = {
            ...mainLog,
            sheetName: this.json.name,
            status: !errorText ? 'success' : 'failure',
            errorText: errorText || '',
            importerName: this.json.name,
            importer: this.json.id,
            data: JSON.stringify(data),
            sampleData: JSON.stringify(this.getSampleData(data)),
            docId: data.id,
            mainLogId: mainLog.id,
            operationType: operationType
        }
        delete data.id;
        return this.logTable.addRow(log);
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

            this.progressBar = new MWF.QMultiImporter.ProgressBar(this, progressDialog);

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
                                //this.doImportData(resolve);
                                resolve({status: 'success', importer: this});
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
    }
});

MWF.QMultiImporter.Row = new Class({
    Extends: MWF.QImporter.Row,
    checkCMS : function( notCheckName ){
        if( this.importer.json.dataType !== 'master' ){
            return;
        }

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

        data = this.document.publishTime;
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

        if( this.importer.json.dataType !== 'master' ){
            return;
        }

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

            data = this.work.startTime;
            if(!data){
                errorTextList.push(this.getCol("startTime", false) + lp.noStartTime + lp.fullstop );
                errorTextListExcel.push(this.getCol("startTime", false) + lp.noStartTime + lp.fullstop );
            }else if( ! new Date(data).isValid() ){
                errorTextList.push(this.getCol("startTime", false) + '"'+ data +'"'+ lp.startTimeFormatError + lp.fullstop );
                errorTextListExcel.push(this.getCol("startTime", false) + '"'+ data +'"'+ lp.startTimeFormatError + lp.fullstop );
            }

            data = this.work.completeTime;
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
});

//MWF.xDesktop.requireApp("Template", "utils.ExcelUtilsV2", null, false);

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
                    "action": function () { _self.multipleImporter.openResultDlg(); }
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
    }
});

MWF.QMultiImporter.ProgressBar = new Class({
    Extends: MWF.QImporter.ProgressBar,
    initialize : function( importer, dlg, options ){
        this.setOptions(options);
        this.importer = importer;
        this.dlg = dlg;
        this.actions = this.importer.lookupAction;
        this.lp = MWF.xApplication.query.Query.LP;
        this.css = importer.css;
        this.container = dlg.contentNode;
        this.titleNode = new Element('div', {
            text: this.importer.json.name,
            style: 'color:#666'
        }).inject(this.container);
        this.contentNode = new Element('div', {style: 'margin-bottom:10px;'}).inject(this.container);
        this.status = "ready";

        this.lp = Object.assign(this.lp, {
            "openImportRecordDetail": "查看导入日志",
            "importSuccess" : "导入文档成功！",
            "importPartSuccessTitle": "部分导入成功",
            "importFailTitle": "导入失败",
            "checkDataTitle": "数据导入",
            "checkDataContent": "正在检查数据...",
            "importDataTitle": "正在导入数据",
            "importDataContent": "开始导入文档，共{count}条",
            "readyToImportData": "正在准备导入文档",
            "readyToImportData1": "正在准备导入文档...",
            "importSpeed": "条/秒",
            "importingDataContent": "正导入文档: {speed}条/秒,共{total}条,剩余{remaining}条",
            "importingDataErrorContent": ",出错{errorCount}条",
        })

    },
    hide: function (){
        this.titleNode.hide();
        this.contentNode.hide();
    },
    showImporting: function( log, callback ){
        debugger;
        // this.node.show();
        this.setContentHtml();
        if( !this.currentDate )this.currentDate = new Date();

        var executeCount = log.successCount + log.failureCount;
        if( log.totalCount > executeCount ){ //导入中
            //this.setMessageTitle( this.lp.importDataTitle );
            this.setMessageText( this.lp.importDataContent.replace( "{count}", log.totalCount ) );
            this.updateProgress( log );
        }else{ //已经结束, 状态有 "导入成功","部分成功","导入失败"
            this.transferComplete( log );
            if( callback )callback( log );
        }

        // this.setSize();
    },
    updateProgress: function(log){
        //status, data.executeCount, data.count, data.failCount
        var lp = this.lp;

        var total = log.totalCount;
        var processed = log.successCount + log.failureCount;
        var failCount = log.failureCount;

        var percent = 100*(processed/total);

        var sendDate = new Date();
        var lastDate = this.lastTime || this.currentDate;
        var ms = sendDate.getTime() - lastDate.getTime();
        var speed = ( (processed - ( this.lastProcessed || 0 )) * 1000)/ms ;
        var u = lp.importSpeed;
        speed = speed.round(2);

        this.progressPercentNode.setStyle("width", ""+percent+"%");

        var text = lp.importingDataContent.replace("{speed}",(speed === Infinity || speed === -Infinity) ? '' : speed).replace("{total}",total).replace("{remaining}",( total - processed ));
        text += failCount ? lp.importingDataErrorContent.replace("{errorCount}",failCount) : "";
        this.progressInforNode.set("text", text);

        this.lastProcessed = processed;
        this.lastTime = new Date();
    },
    transferComplete: function( log ){
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

        if( log.totalCount === log.successCount ){
            var size = log.totalCount;
            var speed = (size * 1000)/ms ;
            var u = lp.importSpeed;
            speed = speed.round(2);

            this.setMessageTitle( lp.importSuccessTitle );
            var text = lp.importSuccessContent.replace("{total}",size).replace("{speed}",speed).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }else if(log.successCount > 0){
            var size = log.totalCount;
            var speed = (size * 1000)/ms ;
            var u = lp.importSpeed;
            speed = speed.round(2);

            this.setMessageTitle( lp.importPartSuccessTitle );
            var text = lp.importPartSuccessContent.replace("{total}",size).replace("{speed}",speed).replace("{errorCount}",data.failureCount).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }else{ //导入失败
            var size = data.totalCount;
            this.setMessageTitle( lp.importFailTitle );
            var text = lp.importFailContent.replace("{errorInfo}",data.distribution || "").replace("{total}",size).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }
        this.clearMessageProgress();
        this.showCloseAction();
    },
    showCloseAction: function(){
        this.dlg.dlg.titleAction.show();
        this.dlg.dlg.button.show();
    },
    close: function(){
        this.dlg.dlg.close();
    },
    setMessageTitle: function( text){
        //this.dlg.titleText.set("text", text);
        this.titleNode.textContent = text;
    }
});