MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("cms.Module", "ExcelForm", null, false);

this.define("dipatchNumberToCity", function(){
    var ids = this.getSelectedId();
    if( ids.length == 0 ){
        this.form.app.notice("先选择号码","error");
        return;
    }
    var units = this.getLevel1Unit();
    var unitList = [];
    units.each( function( u ){
        unitList.push({
            name : u.name,
            id : u.distinguishedName
        })
    });
    MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false);
    var opt  = {
        "count": 1,
        "title": "选择分配的组织",
        "selectableItems" : unitList,
        "values": [],
        "onComplete": function( array ){
            if( !array || array.length == 0 )return;
            var unit = array[0].data.id;
            if( !unit )return;
            this.saveDocList( ids, unit, "", "" );
        }.bind(this)
    };
    var selector = new MWF.xApplication.Template.Selector.Custom(this.form.app.content, opt );
    selector.load();
}.bind(this));

this.define("dipatchNumberToCounty", function( city, range ){
    var ids = this.getSelectedId();
    if( ids.length == 0 ){
        this.form.app.notice("先选择号码","error");
        return;
    }

    var units = [];
    if( city ){
        var unit = this.org.listSubUnit( city , false );
        unit.each( function( u ){
            units.push({
                name : u.name,
                id : u.distinguishedName
            })
        });
    }
    if( city ){
        MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "selectableItems" : units,
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.id;
                this.saveDocList(ids, city, unit, ""  )
            }.bind(this)
        };
        var selector = new MWF.xApplication.Template.Selector.Custom(this.form.app.content, opt );
        selector.load();
    }else{
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "type" : "unit",
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.distinguishedName;
                
                var levelName = array[0].data.levelName;
                if( levelName.split("/").length != 2 ){
                    this.form.app.notice("请选择县级分公司", "error");
                    return false;
                }
                this.getAllUnit();
                var c = this.name_dnName[levelName.split("/")[0]];
                this.saveDocList(ids, c, unit, "")
            }.bind(this)
        };
        if( range )opt.units = [range];
        var selector = new MWF.O2Selector(this.form.app.content, opt );
    }
}.bind(this));

this.define("dipatchNumberToBranch", function( county, range ){
    var ids = this.getSelectedId();
    if( ids.length == 0 ){
        this.form.app.notice("先选择号码","error");
        return;
    }

    var units = [];
    if( county ){
        var unit = this.org.listSubUnit( county , false );
        unit.each( function( u ){
            units.push({
                name : u.name,
                id : u.distinguishedName
            })
        });
    }
    if( county ){
        MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "selectableItems" : units,
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.id;
                this.getAllUnit();
                var levelName = this.dnName_levelName[unit];
                if( levelName.split("/").length != 3 ){
                    this.form.app.notice("请选择网格", "error");
                    return false;
                }
                var c = this.name_dnName[levelName.split("/")[0]];
                this.saveDocList(ids, c, county, unit )
            }.bind(this)
        };
        var selector = new MWF.xApplication.Template.Selector.Custom(this.form.app.content, opt );
        selector.load();
    }else{
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "type" : "unit",
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.distinguishedName;
                
                this.getAllUnit();
                var levelName = this.dnName_levelName[unit];
                if( levelName.split("/").length != 3 ){
                    this.form.app.notice("请选择网格", "error");
                    return false;
                }
                var city2 = this.name_dnName[levelName.split("/")[0]];
                var county2 = this.name_dnName[levelName.split("/")[1]];
                this.saveDocList(ids, city2, county2, unit );
            }.bind(this)
        };
        if( range )opt.units = [range];
        var selector = new MWF.O2Selector(this.form.app.content, opt );
    }
}.bind(this));

this.define("saveDocList", function( ids, city, county, branch ){

    ids.each( function(id){
        
        var oldData = this.form.selectedItemJson[id];
        var newData = { docStatus : "published", city : city, county : county , branch : branch };
        if( !this.form.statJson ){
            this.form.statJson = new StatJson(this);
        }
        this.form.statJson.changeData( newData, oldData, oldData.batch );
        this.form.statJson.submit();
    }.bind(this));
    if( this.form.currentView.docStatus == "error" ){
        var changeCount = 0;
        ids.each( function( id ){
            this.saveDoc( id, city, county, branch, function(){
                changeCount++;
                if( changeCount == ids.length ){
                    this.setUploadedUnit( function(){
                        this.form.app.notice("分配成功","");
                        this.createImportBatchDiv();
                        this.loadStatTable( this.statTableOptions ? this.statTableOptions.container : this.form.get("statContaienr").node );
                        this.form.view.reload();
                        this.form.view.selectedItems = [];
                        if( this.form.view_error ){
                            this.form.view_error.reload();
                            this.form.view_error.selectedItems = [];
                        }
                    }.bind(this));
                }
            }.bind(this))
        }.bind(this))
    }else{
        this.saveDcc(ids, ["city","county","branch"], [city,county,branch], function(){
            this.setUploadedUnit( function(){
                this.form.app.notice("分配成功","");
                this.createImportBatchDiv();
                this.loadStatTable( this.statTableOptions ? this.statTableOptions.container : this.form.get("statContaienr").node  );
                this.form.currentView.reload();
                this.form.currentView.selectedItems = [];
            }.bind(this));
        }.bind(this))
    }
}.bind(this));

this.define("saveDoc", function( id, city, county, branch, callback ){
    MWF.Actions.get("x_cms_assemble_control").getDocument(id, function( json ){
        var docData = json.data;
        docData.data.city = city;
        docData.data.county = county;
        docData.data.branch = branch;

        docData.data.errorText = "";
        docData.data.docStatus = "published";
        docData.data.status = "成功";
        docData.data.title = docData.data.subject;

        delete docData.data.$document;
        delete docData.document.viewCount;
        delete docData.document.publishTime;
        delete docData.document.hasIndexPic;
        delete docData.document.readPersonList;
        delete docData.document.readUnitList;
        delete docData.document.readGroupList;
        delete docData.document.authorPersonList;
        delete docData.document.authorUnitList;
        delete docData.document.authorGroupList;
        delete docData.document.managerList;
        delete docData.document.pictureList;

        delete docData.documentLogList;
        delete docData.isAppAdmin;
        delete docData.isCategoryAdmin;
        delete docData.isManager;
        delete docData.isCreator;
        delete docData.isEditor;

        docData.document.docData = docData.data;
        delete docData.data;

        docData.document.docStatus = "published";
        docData.document.subject = docData.document.title;

        MWF.Actions.get("x_cms_assemble_control").updateDocument( docData.document , function(){
            if( callback )callback();
        }.bind(this));
    }.bind(this))
}.bind(this));

this.define("dipatchNumber", function(){
    // var flag = (this.workContext.getControl().allowSave  && this.workContext.getActivity().alias == "draft") ;
    // if( !flag ){
    //     this.form.app.notice( "发起节点才能分配号码","error" );
    //     return;
    // }
    var ids = this.getSelectedId();
    if( ids.length == 0 ){
        this.form.app.notice("先选择号码","error");
        return;
    }
    var units = this.getSubUnit();
    if( units ){
        MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "selectableItems" : units,
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.id;
                this.setUnit(ids, unit )
            }.bind(this)
        };
        var selector = new MWF.xApplication.Template.Selector.Custom(this.form.app.content, opt );
        selector.load();
    }else{
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        var opt  = {
            "count": 1,
            "title": "选择分配的组织",
            "type" : "unit",
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                var unit = array[0].data.distinguishedName;
                
                this.setUnit(ids, unit)
            }.bind(this)
        };
        var selector = new MWF.O2Selector(this.form.app.content, opt );
    }

});

this.define("getSelectedId", function(){
    var ids = [];
    if( !this.form.currentView ){
        this.form.currentView = this.form.view;
    }
    this.form.selectedItemJson = {};
    this.form.currentView.selectedItems.each( function( item ){
        ids.push( item.data.bundle );
        this.form.selectedItemJson[ item.data.bundle ] = {
            batch : item.data.data.batch,
            city : item.data.data.city,
            county : item.data.data.county,
            branch : item.data.data.branch,
            docStatus : this.form.currentView.docStatus || "published"
        }
    }.bind(this));
    return ids;
});

this.define("getSubUnit", function(){
    var units = this.data.currentUnit;
    if( units ){
        var unit = this.org.listSubUnit( units , false );
    }else if( !this.data.newFlag ){
        var unit = this.getLevel1Unit(); //this.workContext.getWork().creatorUnitLevelName.split("/")[0];
    }else{
        return null;
    }
    //unit = unit.split("@")[0];
    var array = [];
    unit.each( function( u ){
        array.push({
            name : u.name,
            id : u.distinguishedName
        })
    });
    return array;
});

this.define("getLevel1Unit", function( callback){
    var array = [];
    var action = new this.Action("x_organization_assemble_express", {
        "lookup":{"uri": "/jaxrs/unit/list/level/object", "method": "POST"}
    });
    action.invoke({"name": "lookup","parameter": {}, "data": {"levelList":[ "1" ]}, "success": function(json){
        array = json.data;
        if(callback)callback(json);
    }.bind(this), async : false
    });
    return array;
}.bind(this));


this.define("setUnit", function(ids, unit){
    
    if( !unit )return;
    var flag = this.data.flag || this.data.newFlag;
    var f;
    if( !flag ){
        f = "city"
    }else if( flag == "city" ){
        f = "county"
    }else if( flag == "county" ){
        f = "branch"
    }
    this.saveDcc(ids, f, unit, function(){
        
        var value = this.data[f+"TaskPerson"];
        var array = [];
        ( value.length ? value : [] ).each( function( v ){
            array.push( typeOf( v ) == "string" ? v : v.distinguishedName )
        }.bind(this));
        array.push( unit );
        array = array.unique();
        this.data[f+"TaskPerson"] = array;

        this.form.app.notice("分配成功","");
        //this.context.data.save();
        this.form.save();
        this.form.view.reload();
        this.form.view.selectedItems = [];
    }.bind(this))
});

this.define("saveDcc", function( ids, field, value, callback){
    var action = new this.Action("x_cms_assemble_control", {
        "save":{"uri": "/jaxrs/document/batch/data/modify", "method": "PUT"}
    });
    var array = [];
    if( typeOf( field ) == "array" ){
        for( var i=0; i<field.length; i++ ){
            array.push({
                "dataPath": field[i],
                "dataType": "String",
                "dataString": value[i],
                "dataInteger": null,
                "dataBoolean": null,
                "dataDate": null
            })
        }
    }else{
        array.push({
            "dataPath": field,
            "dataType": "String",
            "dataString": value,
            "dataInteger": null,
            "dataBoolean": null,
            "dataDate": null
        })
    }
    action.invoke({"name": "save", "data": {
        "docIds" : ids,
        "dataChanges" : array
    }, "success": function(json){
        if(callback)callback(json);
    }.bind(this)
    });
}.bind(this));


MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
var ChannelTaskPhoneService = new Class({
    Extends: MWF.xDesktop.Actions.RestActions,
    initialize: function ( context, workId ) {
        this.context = context;
        this.serviceUrl = "http://localhost:8080/channeltask/";
        this.importUrl = this.serviceUrl + "import";
        this.phoneUrl = this.serviceUrl + "phone";
        this.importcheckUrl = this.serviceUrl + "importcheck";
    },
    import : function( formData, file, callback_success, callback_progress, async ){
        this.invoke( {
            method : "POST",
            uri: this.importUrl,
            async: async,
            enctype: "formdata", //formdata
            data: formData, // {}
            file: file,
            withCredentials : true,
            success: function( data ){ if(callback_success)callback_success(data) },
            failure: function(){}
        });
        if(callback_progress){
            window.setTimeout( function(){
                this.importcheck( callback_progress )
            }.bind(this), 1000 )
        }
    },
    importcheck : function( callback ){
        this.invoke( {
            method : "GET",
            uri: this.importUrl + "?workId" + this.workId ,
            async: true,
            withCredentials : true,
            success: function( data ){ if(callback)callback(data) },
            failure: function(){}
        })
    },
    listByImportbatchName : function(importbatchName ,start, end, docStatus, callback){
        this.phoneAction( "list", {
            importbatchName : importbatchName, start : start, end : end, docStatus: docStatus
        }, callback)
    },
    listByWork : function(start, end, docStatus, callback){
        this.phoneAction( "list", {
            workId : this.workId, start : start, end : end, docStatus : docStatus
        }, callback)
    },
    listByWorkAndCity : function(city, start, end, docStatus, callback){
        this.phoneAction( "list", {
            workId : this.workId, city : city, start : start, end : end, docStatus : docStatus
        }, callback)
    },
    listByWorkAndCounty : function(county, start, end, docStatus, callback){
        this.phoneAction( "list", {
            workId : this.workId, county : county, start : start, end : end, docStatus : docStatus
        }, callback)
    },
    listByWorkAndBranch : function(branch, start, end, docStatus, callback){
        this.phoneAction( "list", {
            workId : this.workId, branch : branch, start : start, end : end, docStatus : docStatus
        }, callback)
    },
    deleteByImportbatchName : function(importbatchName, callback){
        this.phoneAction( "delete", {
            importbatchName : importbatchName
        }, callback)
    },
    deleteByWorkId : function(callback){
        this.phoneAction( "delete", {
            workId : this.workId
        }, callback)
    },
    dispatch : function(branch, start, end, docStatus, callback){
        this.phoneAction( "dispatch", {
            workId : this.workId, branch : branch, start : start, end : end, docStatus : docStatus
        }, callback)
    },
    phoneAction : function( action, json, callback ){
        var param = "";
        for( var key in json ){
            if( json[key] )param = param + "&" + key + "=" + json[key];
        }
        this.invoke( {
            method : "GET",
            uri: this.phoneUrl + "?action=" + action + param,
            async: true,
            withCredentials : true,
            success: function( data ){ if(callback)callback(data) },
            failure: function(){}
        })
    },
    invoke: function(option){
        /*
         option = {
         method : "POST",
         uri: "",
         async: true,
         enctype: "", //formdata
         data: null, // {}
         file: null,
         withCredentials : true,
         success: function(){},
         failure: function(){}
         }
         */
        var method = option.method || "GET";
        var uri = option.uri;

        var async = (option.async===false) ? false : true;

        var callback = new MWF.xDesktop.Actions.RestActions.Callback(option.success, option.failure);
        if (option.enctype && (option.enctype.toLowerCase()=="formdata")){
            this.invokeFormData(method, uri, option.data, option.file, callback, async);
        }else{
            var data = (option.data) ? JSON.encode(option.data) : "";
            var credentials = true;
            if (option.withCredentials===false){
                credentials = false;
            }
            return MWF.restful(method, uri, data, callback, async, credentials);
        }
    }
});

var UploadExcelDialog = new Class({
    Extends: MWF.xApplication.cms.Module.ImportForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": "650",
        "height": "430",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "maxAction" : true,
        "title" : "导入号码"
    },
    _createTableContent: function () {

        this.formTableContainer.setStyles({"margin":"0px auto 20px atuo"});

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' width='20%'>说明：</td>" +
            "    <td styles='formTableValue' colspan='3' width='80%' style='font-size:12px;color:#666;line-height:20px;'>"+
            "       您可以直接在Excel表格里填写地市分公司、区县分公司和网格的名称，系统会以您导入的分公司名称进行流转分发。<br/>"+
            "请注意填写的名称需要与系统内的分公司/组织名称一致。<div item='openUnit''></div>"+ "<div item='url2'></div>"+//如果名称有重名，请使用层次名。
            "</td></tr>" +
            "<tr><td styles='formTableTitle' lable='url' width='20%'></td>" +
            "    <td styles='formTableValue' item='url' colspan='3' width='80%'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='file' ></td>" +
            "    <td styles='formTableValue' colspan='3'><div item='filename'></div><div item='file'></div></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", null, false);
        this.form = new MForm(this.formTableArea, {}, {
            isEdited: true,
            style : "cms",
            hasColon : true,
            itemTemplate: {
                openUnit : { type : "Innerhtml", value : "<a href='javascript:void(0)'>点击查看组织名称</a>",
                    event : { click : function(item, ev){
                        
                        layout.desktop.openApplication(ev, "Org", {
                            onQueryLoad : function(){
                                this.status = { navi : 0 }
                            }
                        });
                    }.bind(this)}
                },
                url2: { type : "Innerhtml", text : "下载模板", value : "<a target='_blank' href='../x_component_cms_Module/$ExcelForm/"+encodeURIComponent("Excel导入合法性说明.xls")+"'>点击查看校验说明</a>" },

                url: { type : "Innerhtml", text : "下载模板", value : "<a target='_blank' href='../x_component_cms_Module/$ExcelForm/"+encodeURIComponent("Excel模板下载.xls")+"'>Excel模板下载</a>" },
                file: { type : "button", value : "选择Excel文件",text : "选择文件", event :{
                    click : function(){
                        this.selectFile();
                    }.bind(this)
                } }

            }
        }, this.app);
        this.form.load();

    },
    _setCustom : function(){
        this.formBottomNode.setStyles({
            "margin":"0px auto 0px auto",
            "width" : "300px"
        });
    },
    ok: function( callback ){
        if( !this.formData ){
            this.app.notice( "请先选择Excel文件", "error" );
        }else{
            var user = layout.desktop.session.user;
            var identity = ( user.identityList && user.identityList.length > 0 ) ? user.identityList[0] : {};
            var json = {
                workName : this.data.workName,
                workId : this.data.workId,
                jobId : this.data.jobId,
                creatorPerson : user.distinguishedName,
                creatorIdentity : identity.distinguishedName,
                creatorUnitName : identity.unitLevelName.replace(/[\/]/,"^^")
            };
            if( !this.isSetData ){
                for( var key in json ){
                    this.formData.append( key,  json[key] );
                }
                this.isSetData = true;
            }

            this.loadProgressBar();
            var import_success = function( data ){

                var _form = this.context.form;

                this.progressBar.setProgress(js.data.processTotal, js.data.dataTotal, "正在导入数据");
                var array = this.context.data.importBatchNames ? this.context.data.importBatchNames.split(",") : [];
                array.push( json.data.importBatchName );
                this.context.data.importBatchNames = array.toString();
                this.context.form.save();

                this.progressBar.gotoStep(3);
                this.setResult();

                if( !_form.statJson ){
                    _form.statJson = new StatJson(this.context);
                }
                _form.statJson.addBatch(importBatchName, true);
                _form.statJson.submit();
                this.context.setUploadedUnit( function(){
                    _form.view.reload();
                    _form.view.selectedItems = [];
                    if( _form.view_error ){
                        _form.view_error.reload();
                        _form.view_error.selectedItems = [];
                    }
                    this.context.createImportBatchDiv();
                    this.context.loadStatTable( this.context.statTableOptions ? this.context.statTableOptions.container : this.context.form.get("statContaienr").node );
                }.bind(this));

                this.formData = null;
                this.file = null;
            }.bind(this);

            var importcheck_fun = function( data ){
                this.progressBar.setProgress(js.data.processTotal, js.data.dataTotal, "正在导入数据");
            }.bind(this);

            var service = new ChannelTaskPhoneService( this.context, this.data.workId );
            service.import(this.formData, this.file, import_success, importcheck_fun, true);
            //this.action.importDocumentFormExcel(this.data.categoryId, function (json) { //导入excel
            //    checkImportStatus(json);
            //}.bind(this), null, this.formData, this.file);
        }
    },
    setResult : function(){
        this.formTableArea.empty();

        this.formTopCloseActionNode.setStyle("display","");
        this.formTopTextNode.set("text","导入结束");
        var data = this.importedResultJson.data;

        new Element("div", {
            styles : {
                "margin-top" : "10px",
                "font-size" : "14px",
                "margin-left" : "10px"
            },
            text : "本批次共导入"+data.dataTotal+"条数据,成功导入"+data.successTotal+"条数据,发生错误"+data.errorTotal+"条"
        }).inject(this.formTableArea);
        if( !this.context.form.statJson ){
            this.context.form.statJson = new StatJson(this.context);
        }
        this.context.form.statJson.loadTable(this.formTableArea,  this.importBatchName );
        this.setFormNodeSize();
    },
    loadProgressBar : function(){
        this.formTableArea.empty();
        this.formBottomNode.setStyle("display","none");
        this.formTopCloseActionNode.setStyle("display","none");
        this.formTopTextNode.set("text","正在导入数据，请不要关闭窗口...");
        this.progressBar = new ProgressBar( this.formTableArea );
        this.progressBar.load();
    }
});

this.define("setNumberCount",function(){
    if( this.data.currentUnit ){
        if( !this.form.statJson ){
            this.form.statJson = new StatJson(this);
        }
        var count = this.form.statJson.getUnitCount( this.data.currentUnit );
        if( this.data.numberCount != count ){
            this.data.numberCount = count;
            this.form.save();
        }
    }
}.bind(this));

this.define("getErrorCount", function(){
    if( !this.form.statJson ){
        this.form.statJson = new StatJson(this);
    }
    return this.form.statJson.getErrorCount();
}.bind(this));

this.define("setUploadedUnit", function( callback ){
    if( !this.form.statJson ){
        this.form.statJson = new StatJson(this);
    }
    var unit = this.data.currentUnit;
    if( unit == "" && !this.data.newFlag ){
        //unit = this.workContext.getWork().creatorUnitLevelName.split("/")[0];
        var creatorUnitLevelName = this.workContext.getWork().creatorUnitLevelName;
        if( creatorUnitLevelName ){
            var u = creatorUnitLevelName.split("/")[0];
            var unit = this.org.getUnit( u );
        }
    }
    var flag = this.data.flag || this.data.newFlag;

    
    var array = [];
    if( !flag ){
        array = this.form.statJson.getCity();
        this.data.numberCount = this.form.statJson.getUnitCount();
    }else if( flag=="city" ){
        if( unit ){
            array = this.form.statJson.getCounty( unit );
            this.data.numberCount = this.form.statJson.getUnitCount(unit);
        }else{
            this.data.numberCount = this.form.statJson.getUnitCount();
            array = this.form.statJson.getAllCounty();
        }
    }else if( flag == "county" ){
        if(  unit ){
            var city = this.data.city;
            if( !city ){
                var creatorUnitLevelName = this.workContext.getWork().creatorUnitLevelName;
                if( creatorUnitLevelName ){
                    var u = creatorUnitLevelName.split("/")[0];
                    city = this.org.getUnit( u );
                }else{
                    var u = this.org.listSupUnit( unit );
                    city = u[0].distinguishedName;
                }
            }
            this.data.numberCount = this.form.statJson.getUnitCount( unit );
            array = this.form.statJson.getBranch( city, unit );
        }else{
            this.data.numberCount = this.form.statJson.getUnitCount();
            array = this.form.statJson.getAllBranch();
        }
    }

    
    var f;
    if( !flag ){
        f = "city";
    }else if( flag=="city" ){
        f = "county";
    }else if( flag == "county" ){
        f = "branch"
    }
    this.data[f+"TaskPerson"] = array;
    this.form.save(function(){
        if(callback)callback()
    });

});

this.define( "loadView", function( status, isSetCurrent ){
    var workId = this.data.provinceWorkId || this.data.cityWorkId || this.data.countyWorkId;
    var unit = this.data.currentUnit;
    if( unit == "" && !this.data.newFlag ){
        unit = this.workContext.getWork().creatorUnitLevelName.split("/")[0];
    }
    //unit = unit.split("@")[0];
    var flag = this.data.flag || this.data.newFlag;

    var control = this.workContext.getControl();
    var viewName;
    if( status == "published" ){
        viewName = "手机号码-导入成功"
    }else if( status == "error" ){
        viewName = "手机号码-导入失败"
    }else{
        viewName = "手机号码"
    }

    var viewJson = {
        "application": "渠道-手机号码设置",
        "viewName": viewName,
        "isTitle":  "yes",
        "select":  control.allowSave ? "multi" : "none", //none , single, multi
        //"titleStyles": this.json.titleStyles,
        //  "itemStyles": this.json.itemStyles,
        "isExpand": "no",
        "filter": [{
            "logic":"and",
            "path": "workId",
            "title": "workId",
            "comparison":"equals",
            "comparisonTitle":"等于",
            "value": workId,
            "formatType":"textValue"
        }]
    };
    if( flag && unit){
        viewJson.filter.push({
            "logic":"and",
            "path": flag,
            "title": flag,
            "comparison":"equals",
            "comparisonTitle":"等于",
            "value": unit,
            "formatType":"textValue"
        })
    }
    var container;
    if( status == "published" ){
        container = this.form.get("view_container_published").node
    }else if( status == "error" ){
        container = this.form.get("view_container_error").node
    }else{
        container = this.form.get("view_container").node
    }

    MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
        var view = new MWF.xApplication.query.Query.Viewer(container, viewJson, {
            "resizeNode": true, //(this.node.getStyle("height").toString().toLowerCase()!=="auto" && this.node.getStyle("height").toInt()>0),
            "onSelect": function(){
                //this.fireEvent("select");
            }.bind(this)
        });
        if( status == "published" ){
            view.docStatus = "published";
            this.form.view = view;
        }else if( status == "error" ){
            view.docStatus = "error";
            this.form.view_error = view;
        }else{
            this.form.view = view;
        }
        if( isSetCurrent )this.form.currentView = view;
    }.bind(this));
});

this.define("createImportBatchDiv", function(){
    if(!this.data.importBatchNames)return;
    if( !this.form.statJson ){
        this.form.statJson = new StatJson(this);
    }
    var _self = this;
    var div = this.form.get("importBatchDiv").node;
    div.empty();
    var tdCss = { "border-right" : "1px solid #ccc", "border-bottom" : "1px solid #ccc", "text-align" : "center" };
    var table = new Element( "table", {
        "width":"90%",
        "border":"0",
        "cellpadding":"5",
        "cellspacing":"0",
        "styles" : {"border-top" : "1px solid #ccc", "border-left" : "1px solid #ccc", "margin" : "20px auto 10px auto"}
    }).inject( div   );

    var tr = new Element("tr").inject( table );

    new Element("th", {  "styles": tdCss, text : "导入时间" }).inject( tr );
    new Element("th", {  "styles": tdCss, text : "校验通过条数" }).inject( tr );
    new Element("th", {  "styles": tdCss, text : "校验未通过条数" }).inject( tr );
    new Element("th", {   "styles": tdCss, text : "操作" }).inject( tr );

    this.data.importBatchNames.split(",").each( function(d){
        var timeStr = d.split("_")[1];

        var year = timeStr.substring(0,4);
        var month = timeStr.substring(4,6);
        var date = timeStr.substring(6,8);
        var hour = timeStr.substring(8,10);
        var minture = timeStr.substring(10,12);
        var second = timeStr.substring(12,14);
        var time = year+"-"+month+"-"+date + " " + hour + ":" + minture + ":" + second;

        var tr = new Element("tr").inject( table );


        new Element("td", {  "styles": tdCss, text : time }).inject( tr );
        new Element("td", {  "styles": tdCss, text : this.form.statJson.getPublishedCount( d ) }).inject( tr );
        new Element("td", {  "styles": tdCss, text : this.form.statJson.getErrorCount( d ) }).inject( tr );
        var td = new Element("td", {   "styles": tdCss }).inject( tr );

        var button = new Element("button", { styles : {
            "border-radius": "5px", "border": "1px solid rgb(204, 204, 204)", "height": "26px", "color": "rgb(119, 119, 119)", "cursor" : "pointer", "margin-right" : "20px"
        } , text : "只查看该批次导入的数据" }).inject(td);
        button.store("data",d);

        button.addEvent("click", function(e){
            var btn = e.target;
            var data = {
                "logic":"and",
                "path": "$document.importBatchName",
                "title": "workId",
                "comparison":"equals",
                "comparisonTitle":"等于",
                "value": btn.retrieve("data"),
                "formatType":"textValue"
            };
            if( this.form.view ){
                var view = this.form.view;
                var filter = view.json.filter  ? view.json.filter.clone() : [];
                filter.push( data );
                var filterList = {"filterList": filter };
                view.createViewNode( filterList );
            }
            if( this.form.view_error ){
                var view_error = this.form.view_error;
                var filter = view_error.json.filter  ? view_error.json.filter.clone() : [];
                filter.push( data );
                var filterList = {"filterList": filter };
                view_error.createViewNode( filterList );
            }
            this.loadStatTable( this.statTableOptions ? this.statTableOptions.contaier : this.form.get("statContaienr").node , btn.retrieve("data") );

        }.bind(this));

        var button = new Element("button", { styles : {
            "border-radius": "5px", "border": "1px solid rgb(204, 204, 204)", "height": "26px", "color": "rgb(119, 119, 119)", "cursor" : "pointer"
        } , text : "删除该批次导入的数据" }).inject(td);
        button.store("data",d);
        button.store("time",time);
        button.addEvent("click", function(e){
            this.form.app.confirm("infor", e, "删除确认", "删除后无法恢复，确定要删除"+e.target.retrieve("time")+"导入的数据？", 380, 150, function(){
                MWF.Actions.get("x_cms_assemble_control").deleteDocumentWithBatchName( e.target.retrieve("data"), function(){
                    var array = _self.data.importBatchNames.split(",");
                    var batch = e.target.retrieve("data");
                    _self.form.statJson.deleteBatch( batch );
                    _self.form.statJson.submit();
                    array.erase( batch );
                    _self.data.importBatchNames = array.toString();
                    _self.form.save(function(){
                        _self.setUploadedUnit( function(){
                            _self.form.app.notice( "删除成功" );
                            _self.form.loadErrorView = false;
                            _self.form.app.refresh();
                        });
                    });
                });
                this.close();
            }, function(){
                this.close();
            });
        }.bind(this))
    }.bind(this));
    var tr = new Element("tr").inject( table );
    new Element("td", {  "styles": tdCss, text : "总数" }).inject( tr );
    new Element("td", {  "styles": tdCss, text : this.form.statJson.getPublishedCount() }).inject( tr );
    new Element("td", {  "styles": tdCss, text : this.form.statJson.getErrorCount() }).inject( tr );
    var td = new Element("td", {   "styles": tdCss }).inject( tr );

    var button = new Element("button", { styles : {
        "border-radius": "5px", "border": "1px solid rgb(204, 204, 204)", "height": "26px", "color": "rgb(119, 119, 119)", "cursor" : "pointer", "margin-right" : "20px"
    } , text : "查看全部" }).inject(td);

    button.addEvent("click", function(e){
        var btn = e.target;
        if( this.form.view ){
            var view = this.form.view;
            var filter = view.json.filter  ? view.json.filter.clone() : [];
            var filterList = {"filterList": filter };
            view.createViewNode( filterList );
        }
        if( this.form.view_error ){
            var view_error = this.form.view_error;
            var filter = view_error.json.filter  ? view_error.json.filter.clone() : [];
            var filterList = {"filterList": filter };
            view_error.createViewNode( filterList );
        }
        this.loadStatTable( this.statTableOptions ? this.statTableOptions.contaier : this.form.get("statContaienr").node  );

    }.bind(this));
});


this.define("getAllUnit", function( callback){
    if( this.name_all ){
        if(callback)callback();
    }
    var array = this.name_all = [];
    this.name_levelName = {};
    this.dnName_levelName = {};
    this.name_dnName = {};
    var action = new this.Action("x_organization_assemble_express", {
        "lookup":{"uri": "/jaxrs/unit/list/all/object", "method": "GET"}
    });
    action.invoke({"name": "lookup","parameter": {}, data:null, "success": function(json){
        json.data.each( function(d){
            this.name_levelName[ d.name ] = d.levelName;
            this.dnName_levelName[ d.distinguishedName ] = d.levelName;
            this.name_dnName[ d.name ] = d.distinguishedName;
            array.push( d.name );
            array.push( d.distinguishedName );
            array.push( d.shortName );
            array.push( d.levelName );
        }.bind(this));
        if(callback)callback(json);
    }.bind(this), async : false
    });
    return array;
}.bind(this));


this.define("setWorkId", function(){
    if (this.workContext.getWork().activityName=="发起"){
        this.form.get("provinceWorkId").setData(this.workContext.getWork().id);
        this.form.get("currentWorkId").setData(this.workContext.getWork().id)
    }
    if (this.workContext.getWork().activityName=="市级接收单元负责人处理"){
        this.form.get("cityWorkId").setData(this.workContext.getWork().id);
        this.form.get("currentWorkId").setData(this.workContext.getWork().id)

    }
    if (this.workContext.getWork().activityName=="县级接收单元负责人处理"){
        this.form.get("countyWorkId").setData(this.workContext.getWork().id);
        this.form.get("currentWorkId").setData(this.workContext.getWork().id)

    }
    if (this.workContext.getWork().activityName=="网格接收单元负责人处理"){
        this.form.get("branchWorkId").setData(this.workContext.getWork().id);
        this.form.get("currentWorkId").setData(this.workContext.getWork().id)

    }
});

this.define("getUnitLevel", function(level, isObject){
    var identity = this.workContext.getWork().creatorIdentityDn;
    var topUnit;
    MWF.Actions.get("x_organization_assemble_express")[isObject ? "getUnitWithIdentityAndLevel" : "getUnitWithIdentityAndLevelValue" ]( {"identity":identity,"level":level}, function( json ){
        topUnit = json.data.unit;
    }.bind(this), null, false);
    return topUnit;
}.bind(this));

this.define("openMinder", function( workId ){
    
    var options = {
        pageId : "71acdde6-97cc-4c6d-abe2-817ea5afad4f",
        portalId : "b66420c3-dee9-4b4c-9d52-050fd0921864",
        workId : workId,
        "appId": "portal_"+workId
    };
    if( layout.desktop.openApplication ){
        layout.desktop.openApplication(null, "portal.Portal", options)
    }else{
        window.open( o2.filterUrl("../x_desktop/app.html?app=portal.Portal&option="+ JSON.stringify(options)) );
    }
});

this.define("openUploadForm", function(){
    // var flag = (this.workContext.getControl().allowSave  && this.workContext.getActivity().alias == "draft") ;
    // if( !flag ){
    //     this.form.app.notice( "发起节点才能上传Excel","error" );
    //     return;
    // }
    if( !this.data.subject ){
        this.form.app.notice( "请填写任务名称并保存","error" );
        return;
    }
    var dialog = this.form.uploadExcelDialog = new UploadExcelDialog( { app : this.form.app }, {
        workName : this.data.subject,
        workId : this.data.provinceWorkId || this.data.currentWorkId,
        jobId : this.workContext.getWork().job,
        categoryId:"288a0f05-78dd-4650-af79-236e33832a7e"
    }, {

    });
    dialog.contextForm = this.form;
    dialog.context = this;
    dialog.edit();
});


var ProgressBar = new Class({
    initialize: function ( container ) {
        this.container = container;
    },
    load : function(){
        this.getCss();

        this.loadSteps();
        this.loadProgressBar();
    },
    setProgress : function( processed, total, text ){
        var width = Math.floor(( processed / total ) * 100 );
        this.progressFront.setStyles({ width: width+"%" });
        this.textNode.set("text", text + "，共"+total+"条，已处理"+ processed + "条，进度" + width + "%"  );
    },
    loadProgressBar : function(){
        this.progressNode = new Element("div", { styles : this.css.progressNode }).inject( this.container );

        this.progressBack = new Element("div.progressBack", { styles: this.css.progressBack }).inject(this.progressNode);
        this.progressBack.setStyle("width", "100%");

        this.progressFront = new Element("div.progressFront", { styles: this.css.progressFront, text : " " }).inject(this.progressBack);
        this.progressFront.setStyle("width", "0px");

        this.textNode = new Element("div", { styles : this.css.textNode }).inject( this.container );
    },
    getCss : function(){
        this.css = {
            "loadingNode" : {

            },
            "textNode" : {
                "margin-top" : "10px",
                "font-size" : "12px",
                "margin-left" : "10px"
            },
            "progressNode" : {
                "margin" : "10px 0px",
                "overflow" : "hidden"
            },
            "progressBack" : {
                "float":"left",
                "border-radius" : "10px",
                "background-color" : "#f4f4f4",
                "height" : "16px"
            },
            "progressFront" : {
                "height" : "16px",
                "background-color" : "#4a9adb"
            }
        }
    }
});


this.define("loadStatTable", function( container , batchName, unitLevel, unitName ){
    container.empty();
    if( !this.form.statJson ){
        this.form.statJson = new StatJson(this);
    }
    if( !unitLevel && this.statTableOptions ){
        unitLevel = this.statTableOptions.unitLevel
    }
    if( !unitName && this.statTableOptions ){
        unitName = this.statTableOptions.unitName
    }
    this.form.statJson.loadTable( container, batchName, unitLevel, unitName );
});


var StatJson = new Class({
    initialize: function ( context ) {
        this.context = context;

        if( this.context.data.statJson ){
            this.json = JSON.parse(this.context.data.statJson);
        }else{
            this.json = {
                total : {
                    publishedCount : 0,
                    errorCount : 0
                },
                batch: {}
            }
        }
    },
    submit : function(){
        this.deleteEmptyUnit();
        for( var key in this.json.batch ){
            this.deleteEmptyUnit(key);
        }
        this.context.data.statJson = JSON.stringify(this.json);
    },
    addBatch : function( batchName, isSetCurrent ){
        if( !this.json.batch[batchName] ){
            this.json.batch[batchName] = {
                publishedCount : 0,
                errorCount : 0
            };
        }
        if( isSetCurrent )this.currentBatch = this.json.batch[batchName];
    },
    deleteBatch : function( batchName ){
        var json = this.json;
        var batchData = json.batch[batchName];
        if( batchData ){
            if( batchData.publishedCount ){
                json.total.publishedCount = json.total.publishedCount - batchData.publishedCount;
            }
            if( batchData.errorCount ){
                json.total.errorCount = json.total.errorCount - batchData.errorCount;
            }
            this.reduceByBatchData( batchData );
            delete this.json.batch[batchName];
        }
    },
    reduceByBatchData : function( batchData ){
        var totalData = this.json.total;
        for( var key in batchData ){
            if( key != "publishedCount" && key != "errorCount" ){
                var totalD = totalData[key];
                var batchD = batchData[key];
                if( batchData.publishedCount )totalD.publishedCount = totalD.publishedCount - batchD.publishedCount;
                if( batchData.errorCount )totalD.errorCount = totalD.errorCount - batchD.errorCount;
                for( var key_2 in batchD ){
                    if( key_2 != "publishedCount" && key_2 != "errorCount" ){
                        var totalD_2 = totalD[key_2];
                        var batchD_2 = batchD[key_2];
                        if( batchD_2.publishedCount )totalD_2.publishedCount = totalD_2.publishedCount - batchD_2.publishedCount;
                        if( batchD_2.errorCount )totalD_2.errorCount = totalD_2.errorCount - batchD_2.errorCount;
                        for(var key_3 in batchD_2 ){
                            if( key_3 != "publishedCount" && key_3 != "errorCount" ) {
                                var totalD_3 = totalD_2[key_3];
                                var batchD_3 = batchD_2[key_3];
                                if( batchD_3.publishedCount )totalD_3.publishedCount = totalD_3.publishedCount - batchD_3.publishedCount;
                                if( batchD_3.errorCount )totalD_3.errorCount = totalD_3.errorCount - batchD_3.errorCount;
                            }
                        }
                    }
                }
            }
        }
    },
    addData: function( cmsDocData ){
        var d = cmsDocData;
        var totalJson = this.json.total;
        var batchJson = this.currentBatch;
        if( d.docStatus == "published" ){
            totalJson.publishedCount++;
            batchJson.publishedCount++;
            this.addCount( totalJson, d );
            this.addCount( batchJson, d );
        }else if( d.docStatus == "error" ){
            totalJson.errorCount++;
            batchJson.errorCount++;
        }
    },
    addCount : function( json, d ){
        if( d.city ){
            var cityJson = json[ d.city ];
            if( !cityJson ){
                cityJson = json[ d.city ] = { publishedCount : 0 };
            }
            cityJson.publishedCount ++;
            if( d.county ){
                var countyJson = cityJson[ d.county ];
                if( !countyJson ){
                    countyJson = cityJson[ d.county ] = { publishedCount : 0 };
                }
                countyJson.publishedCount ++;
                if( d.branch ) {
                    var branchJson = countyJson[d.branch];
                    if (!branchJson) {
                        branchJson = countyJson[d.branch] = {publishedCount: 0};
                    }
                    branchJson.publishedCount++;
                }
            }
        }else{
            var city = "未设置组织";
            var cityJson = json[ city ];
            if( !cityJson ){
                cityJson = json[ city ] = { publishedCount : 0 };
            }
            cityJson.publishedCount ++;
        }
    },
    reduceCount : function( json, d ){
        if( d.city ){
            var cityJson = json[ d.city ];
            if( !cityJson ){
                cityJson = json[ d.city ] = { publishedCount : 0 };
            }
            cityJson.publishedCount --;
            if( d.county ){
                var countyJson = cityJson[ d.county ];
                if( !countyJson ){
                    countyJson = cityJson[ d.county ] = { publishedCount : 0 };
                }
                countyJson.publishedCount --;
                if( d.branch ) {
                    var branchJson = countyJson[d.branch];
                    if (!branchJson) {
                        branchJson = countyJson[d.branch] = {publishedCount: 0};
                    }
                    branchJson.publishedCount--;
                }
            }
        }else{
            var city = "未设置组织";
            var cityJson = json[ city ];
            if( !cityJson ){
                cityJson = json[ city ] = { publishedCount : 0 };
            }
            cityJson.publishedCount--;
        }
    },
    getCity : function(){
        var totalJson = this.json.total;
        var city = [];
        for( var key in totalJson ){
            if( key != "publishedCount" && key != "errorCount" && key != "未设置组织"){
                if( totalJson[key].publishedCount > 0 ){
                    city.push(key);
                }
            }
        }
        return city;
    },
    getCounty : function( city ){
        var totalJson = this.json.total;
        var county = [];
        if( totalJson[city] ){
            var cityJson = totalJson[city];
            for( var key in cityJson  ){
                if( key != "publishedCount" && key != "errorCount" && key != "未设置组织"){
                    if( cityJson[key].publishedCount > 0 ){
                        county.push(key);
                    }
                }
            }
        }
        return county;
    },
    getBranch : function( city, county ){
        var totalJson = this.json.total;
        var branch = [];
        if( totalJson[city] ){
            var cityJson = totalJson[city];
            if( cityJson[county] ){
                var countyJson = cityJson[county];
                for( var key in countyJson  ){
                    if( key != "publishedCount" && key != "errorCount" && key != "未设置组织"){
                        if( countyJson[key].publishedCount > 0 ){
                            branch.push(key);
                        }
                    }
                }
            }
        }
        return branch;
    },
    getAllCounty : function(){
        
        var totalJson = this.json.total;
        var county = [];
        for(var key in totalJson ){
            if( key != "publishedCount" && key != "errorCount" && key != "未设置组织"){
                for( var key_2 in totalJson[key] ){
                    if( key_2 != "publishedCount" && key_2 != "errorCount" && key_2 != "未设置组织"){
                        if( totalJson[key][key_2].publishedCount > 0 ){
                            county.push(key_2);
                        }
                    }
                }
            }
        }
        return county;
    },
    getAllBranch : function(){
        var totalJson = this.json.total;
        var branch = [];
        for(var key in totalJson ){
            if( key != "publishedCount" && key != "errorCount" && key != "未设置组织"){
                for( var key_2 in totalJson[key] ){
                    if( key_2 != "publishedCount" && key_2 != "errorCount" && key_2 != "未设置组织"){
                        for( var key_3 in totalJson[key][key_2] ){
                            if( key_3 != "publishedCount" && key_3 != "errorCount" && key_3 != "未设置组织"){
                                if( totalJson[key][key_2][key_3].publishedCount > 0 ){
                                    branch.push(key_3);
                                }
                            }
                        }
                    }
                }
            }
        }
        return branch;
    },
    getUnitCount : function( unit, importBatchName ){
        var data;
        if( importBatchName && this.json.batch[importBatchName]) {
            data = this.json.batch[importBatchName];
        }else{
            data = this.json.total;
        }
        if( !unit )return data.publishedCount;
        for( var city in data ){
            var cityData = data[city];
            if( city == unit )return cityData.publishedCount;
            for( var county in cityData ){
                var countyData = cityData[county];
                if( county == unit )return countyData.publishedCount;
                for( var branch in countyData ){
                    var branchData = countyData[branch];
                    if( branch == unit )return branchData.publishedCount;
                }
            }
        }
        return 0;
    },
    changeData : function( newData, oldData, importBatchName ){
        
        //var oldData = {
        //    status : "error",
        //    city : "",
        //    county : "",
        //    branch : ""
        //};

        var batchJson;
        if( importBatchName && this.json.batch[importBatchName]) {
            batchJson = this.json.batch[importBatchName];
        }
        var totalJson = this.json.total;

        if( oldData.docStatus == "error" ){
            totalJson.errorCount--;
            if( batchJson )batchJson.errorCount--;
        }
        if( oldData.docStatus == "published" ){
            totalJson.publishedCount--;
            this.reduceCount( totalJson, oldData );
            if( batchJson ){
                batchJson.publishedCount--;
                this.reduceCount( batchJson, oldData );
            }
        }

        if( newData.docStatus == "error"){
            totalJson.errorCount++;
            if( batchJson )batchJson.errorCount++;
        }
        if( newData.docStatus == "published"){
            totalJson.publishedCount++;
            this.addCount( totalJson, newData );
            if( batchJson ){
                batchJson.publishedCount++;
                this.addCount( batchJson, newData );
            }
        }
    },
    getPublishedCount : function( importBatchName ){
        if( !importBatchName ){
            return this.json.total.publishedCount;
        }else{
            if( this.json.batch[importBatchName]) {
                var batchJson = this.json.batch[importBatchName];
                return batchJson.publishedCount;
            }
        }
    },
    getErrorCount : function( importBatchName ){
        if( !importBatchName ){
            return this.json.total.errorCount;
        }else{
            if( this.json.batch[importBatchName]) {
                var batchJson = this.json.batch[importBatchName];
                return batchJson.errorCount;
            }
        }
    },
    deleteEmptyUnit : function( batchName ){
        if( batchName ){
            var data = this.json.batch[batchName];
        }else{
            var data = this.json.total;
        }
        for( var key in data ){
            if( key != "publishedCount" && key != "errorCount" ){
                var cityData = data[key];
                if( !cityData.publishedCount && !cityData.errorCount ){
                    delete data[key]
                }else{
                    for( var key_2 in cityData ){
                        if( key_2 != "publishedCount" && key_2 != "errorCount" ){
                            var countyData = cityData[key_2];
                            if( !countyData.publishedCount && !countyData.errorCount ){
                                delete data[key][key_2]
                            }else{
                                for(var key_3 in countyData ){
                                    if( key_3 != "publishedCount" && key_3 != "errorCount" ) {
                                        var branchData = countyData[key_3];
                                        if( !branchData.publishedCount && !branchData.errorCount ) {
                                            delete data[key][key_2][key_3]
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    },
    getNoUnitJson : function( d ){
        var data = Object.clone(d);
        for( var key in data ){
            if( key != "publishedCount" && key != "errorCount" ){
                var cityData = data[key];
                var cityCount = cityData.publishedCount;
                var countyCount = 0;
                for( var county in cityData ){
                    if( county != "publishedCount" && county != "errorCount"  ){
                        var countyData = cityData[county];
                        countyCount = countyCount + countyData.publishedCount;
                        var branchCount = 0;
                        for( var branch in countyData ) {
                            if( branch != "publishedCount" && branch != "errorCount"  ) {
                                var branchData = countyData[branch];
                                branchCount = branchCount + branchData.publishedCount
                            }
                        }
                        if( countyData.publishedCount >  branchCount ){
                            countyData["未设置"] = { publishedCount : countyData.publishedCount - branchCount };
                        }
                    }
                }
                if( cityData.publishedCount >  countyCount ){
                    cityData["未设置"] = { publishedCount : cityData.publishedCount - countyCount };
                }
            }
        }
        return data;
    },
    loadTable : function( container, batchName, unitLevel, unitName ){
        if( !unitLevel ){
            this._loadTable(container, batchName);
        }else{
            this._loadTableByUnit( container, batchName, unitLevel, unitName  )
        }
    },
    _loadTableByUnit : function(container, batchName, unitLevel, unitName ){
        if (batchName) {
            var d = this.json.batch[batchName];
        } else {
            var d = this.json.total;
        }
        var data = this.getNoUnitJson( d );
        

        var table = this.table = new Element( "table", {
            "width":"90%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : {"border-top" : "1px solid #ccc", "border-left" : "1px solid #ccc", "margin" : "20px auto 10px auto", "font-size":"14px"}
        }).inject( container   );

        if( batchName ){
            var timeStr = batchName.split("_")[1];

            var year = timeStr.substring(0,4);
            var month = timeStr.substring(4,6);
            var date = timeStr.substring(6,8);
            var hour = timeStr.substring(8,10);
            var minture = timeStr.substring(10,12);
            var second = timeStr.substring(12,14);
            var title  = year+"-"+month+"-"+date + " " + hour + ":" + minture + ":" + second + "批次数据统计"
        }else{
            var title = "数据统计";
        }
        if( unitLevel == "city" )this._loadTableByCity(title, data, table, unitName );
        if( unitLevel == "county" )this._loadTableByCounty(title, data, table, unitName );
        if( unitLevel == "branch" )this._loadTableByBranch(title, data, table, unitName );
    },
    _loadTableByCity : function(title, data, table, unitName){
        var tdCss = { "border-right" : "1px solid #ccc", "border-bottom" : "1px solid #ccc", "text-align" : "center" };
        var tdTitleCss = { "border-right" : "1px solid #ccc", "border-bottom" : "1px solid #ccc", "text-align" : "center" , "font-size":"16px", "font-weight" : "bold"};

        var tr = new Element("tr").inject( table );
        new Element("td", {  "styles": tdTitleCss, text : title, colspan:3 }).inject( tr );

        var tr = new Element("tr").inject( table );
        new Element("th", {  "styles": tdCss, text : "市分" }).inject( tr );
        new Element("th", {  "styles": tdCss, text : "县分" }).inject( tr );
        new Element("th", {  "styles": tdCss, text : "网格" }).inject( tr );

        
        for( var city in data ){
            if( city != unitName )continue;
            if( city != "publishedCount" && city!="errorCount" ){
                var cityTr = new Element("tr").inject( table );
                var cityData = data[city];
                var cityShow =  city == "未设置组织" ? "未设置" : city.split("@")[0];
                var cityTd = new Element("td", {  "styles": tdCss, text : cityShow + "(" + cityData.publishedCount + ")" }).inject( cityTr );
                var citySpan = 1;
                var countyIndex = 0;
                var countyTr = null;
                var countyTd = null;
                var branchTd = null;
                for( var county in cityData ){
                    if( county != "publishedCount" && county!="errorCount" ){
                        if( countyIndex != 0 ){
                            citySpan++;
                            countyTr = new Element("tr").inject( table );
                        }
                        countyIndex ++;
                        var countySpan = 1;
                        var countyData = cityData[county];
                        countyTd = new Element("td", {  "styles": tdCss, text : county.split("@")[0]  + "(" + countyData.publishedCount + ")" }).inject( countyTr || cityTr );
                        var branchIndex = 0;
                        var branchTr = null;
                        for( var branch in countyData ){
                            if( branch != "publishedCount" && branch!="errorCount" ) {
                                if( branchIndex != 0 ){
                                    branchTr = new Element("tr").inject( table );
                                    citySpan++;
                                    countySpan++;
                                }
                                branchIndex++;
                                var branchData = countyData[branch];
                                branchTd = new Element("td", {"styles": tdCss, text: branch.split("@")[0]+ "(" + branchData.publishedCount + ")"  }).inject( branchTr || countyTr || cityTr );
                            }
                        }
                        if( branchIndex == 0 ){
                            branchTd = new Element("td", {  "styles": tdCss, text :"" }).inject( branchTr || countyTr || cityTr  );
                        }
                        countyTd.set("rowspan",countySpan);
                    }
                }
                if( countyIndex == 0 ){
                    countyTd = new Element("td", {  "styles": tdCss, text :"" }).inject( countyTr || cityTr );
                }
                if( !branchTd ){
                    new Element("td", {  "styles": tdCss, text :"" }).inject( countyTr || cityTr );
                }
                cityTd.set("rowspan",citySpan);
            }
        }
    },
    _loadTableByCounty : function(title, data, table, unitName){
        var tdCss = { "border-right" : "1px solid #ccc", "border-bottom" : "1px solid #ccc", "text-align" : "center" };
        var tdTitleCss = { "border-right" : "1px solid #ccc", "border-bottom" : "1px solid #ccc", "text-align" : "center" , "font-size":"16px", "font-weight" : "bold"};

        var tr = new Element("tr").inject( table );
        new Element("td", {  "styles": tdTitleCss, text : title, colspan:2 }).inject( tr );

        var tr = new Element("tr").inject( table );
        new Element("th", {  "styles": tdCss, text : "县分" }).inject( tr );
        new Element("th", {  "styles": tdCss, text : "网格" }).inject( tr );

        
        for( var city in data ){
            if( city != "publishedCount" && city!="errorCount" ){
                var cityData = data[city];

                var countyIndex = 0;
                var countyTr = null;
                var countyTd = null;
                var branchTd = null;
                for( var county in cityData ){
                    if( unitName != county )continue;
                    if( county != "publishedCount" && county!="errorCount" ){
                        countyTr = new Element("tr").inject( table );
                        countyIndex ++;
                        var countySpan = 1;
                        var countyData = cityData[county];
                        countyTd = new Element("td", {  "styles": tdCss, text : county.split("@")[0]  + "(" + countyData.publishedCount + ")" }).inject( countyTr  );
                        var branchIndex = 0;
                        var branchTr = null;
                        for( var branch in countyData ){
                            if( branch != "publishedCount" && branch!="errorCount" ) {
                                if( branchIndex != 0 ){
                                    branchTr = new Element("tr").inject( table );
                                    countySpan++;
                                }
                                branchIndex++;
                                var branchData = countyData[branch];
                                branchTd = new Element("td", {"styles": tdCss, text: branch.split("@")[0]+ "(" + branchData.publishedCount + ")"  }).inject( branchTr || countyTr );
                            }
                        }
                        if( branchIndex == 0 ){
                            branchTd = new Element("td", {  "styles": tdCss, text :"" }).inject( branchTr || countyTr  );
                        }
                        countyTd.set("rowspan",countySpan);
                    }
                }
                if( !branchTd && countyTr ){
                    new Element("td", {  "styles": tdCss, text :"" }).inject( countyTr );
                }
            }
        }
    },
    _loadTableByBranch : function(title, data, table, unitName){
        var tdCss = { "border-right" : "1px solid #ccc", "border-bottom" : "1px solid #ccc", "text-align" : "center" };
        var tdTitleCss = { "border-right" : "1px solid #ccc", "border-bottom" : "1px solid #ccc", "text-align" : "center" , "font-size":"16px", "font-weight" : "bold"};

        var tr = new Element("tr").inject( table );
        new Element("td", {  "styles": tdTitleCss, text : title }).inject( tr );

        for( var city in data ){
            if( city != "publishedCount" && city!="errorCount" ){
                var cityData = data[city];

                var branchTd = null;
                for( var county in cityData ){
                    if( county != "publishedCount" && county!="errorCount" ){
                        var countyData = cityData[county];
                        for( var branch in countyData ){
                            if( branch != unitName )continue;
                            if( branch != "publishedCount" && branch!="errorCount" ) {
                                var branchTr = new Element("tr").inject( table );
                                var branchData = countyData[branch];
                                branchTd = new Element("td", {"styles": tdCss, text: branch.split("@")[0]+ "(" + branchData.publishedCount + ")"  }).inject( branchTr );
                            }
                        }
                    }
                }
            }
        }
    },
    _loadTable : function( container, batchName ) {
        if (batchName) {
            var d = this.json.batch[batchName];
        } else {
            var d = this.json.total;
        }
        var data = this.getNoUnitJson( d );
        

        var tdCss = { "border-right" : "1px solid #ccc", "border-bottom" : "1px solid #ccc", "text-align" : "center" };
        var tdTitleCss = { "border-right" : "1px solid #ccc", "border-bottom" : "1px solid #ccc", "text-align" : "center" , "font-size":"16px", "font-weight" : "bold"};
        var table = this.table = new Element( "table", {
            "width":"90%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : {"border-top" : "1px solid #ccc", "border-left" : "1px solid #ccc", "margin" : "20px auto 10px auto", "font-size":"14px"}
        }).inject( container   );

        var tr = new Element("tr").inject( table );
        if( batchName ){
            var timeStr = batchName.split("_")[1];

            var year = timeStr.substring(0,4);
            var month = timeStr.substring(4,6);
            var date = timeStr.substring(6,8);
            var hour = timeStr.substring(8,10);
            var minture = timeStr.substring(10,12);
            var second = timeStr.substring(12,14);
            var title  = year+"-"+month+"-"+date + " " + hour + ":" + minture + ":" + second + "批次数据统计"
        }else{
            var title = "数据统计";
        }
        new Element("td", {  "styles": tdTitleCss, text : title, colspan:3 }).inject( tr );

        var tr = new Element("tr").inject( table );
        new Element("td", {  "styles": tdCss, text : "校验未通过（条）" }).inject( tr );
        new Element("td", {  "styles": tdCss, colspan:2, text : data.errorCount || "" }).inject( tr );

        var tr = new Element("tr").inject( table );
        new Element("td", {  "styles": tdCss, text : "校验通过（条）" }).inject( tr );
        new Element("td", {  "styles": tdCss, colspan:2, text : data.publishedCount || "" }).inject( tr );


        var tr = new Element("tr").inject( table );
        new Element("th", {  "styles": tdCss, text : "市分" }).inject( tr );
        new Element("th", {  "styles": tdCss, text : "县分" }).inject( tr );
        new Element("th", {  "styles": tdCss, text : "网格" }).inject( tr );

        
        for( var city in data ){
            if( city != "publishedCount" && city!="errorCount" ){
                var cityTr = new Element("tr").inject( table );
                var cityData = data[city];
                var cityShow =  city == "未设置组织" ? "未设置" : city.split("@")[0];
                var cityTd = new Element("td", {  "styles": tdCss, text : cityShow + "(" + cityData.publishedCount + ")" }).inject( cityTr );
                var citySpan = 1;
                var countyIndex = 0;
                var countyTr = null;
                var countyTd = null;
                var branchTd = null;
                for( var county in cityData ){
                    if( county != "publishedCount" && county!="errorCount" ){
                        if( countyIndex != 0 ){
                            citySpan++;
                            countyTr = new Element("tr").inject( table );
                        }
                        countyIndex ++;
                        var countySpan = 1;
                        var countyData = cityData[county];
                        countyTd = new Element("td", {  "styles": tdCss, text : county.split("@")[0]  + "(" + countyData.publishedCount + ")" }).inject( countyTr || cityTr );
                        var branchIndex = 0;
                        var branchTr = null;
                        for( var branch in countyData ){
                            if( branch != "publishedCount" && branch!="errorCount" ) {
                                if( branchIndex != 0 ){
                                    branchTr = new Element("tr").inject( table );
                                    citySpan++;
                                    countySpan++;
                                }
                                branchIndex++;
                                var branchData = countyData[branch];
                                branchTd = new Element("td", {"styles": tdCss, text: branch.split("@")[0]+ "(" + branchData.publishedCount + ")"  }).inject( branchTr || countyTr || cityTr );
                            }
                        }
                        if( branchIndex == 0 ){
                            branchTd = new Element("td", {  "styles": tdCss, text :"" }).inject( branchTr || countyTr || cityTr  );
                        }
                        countyTd.set("rowspan",countySpan);
                    }
                }
                if( countyIndex == 0 ){
                    countyTd = new Element("td", {  "styles": tdCss, text :"" }).inject( countyTr || cityTr );
                }
                if( !branchTd ){
                    new Element("td", {  "styles": tdCss, text :"" }).inject( countyTr || cityTr );
                }
                cityTd.set("rowspan",citySpan);
            }
        }
    }
});
