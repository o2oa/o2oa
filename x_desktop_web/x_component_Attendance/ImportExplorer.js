MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.xApplication.Attendance.ImportExplorer = new Class({
	Extends: MWF.xApplication.Attendance.Explorer,
	Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Attendance/$ImportExplorer/";
        this.cssPath = "/x_component_Attendance/$ImportExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    loadView : function(){
        this.view = new MWF.xApplication.Attendance.ImportExplorer.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        this.view.load();
        this.setContentSize();
    },
    createDocument: function(){
        if(this.view)this.view._createDocument();
    },
    importExcel : function(){
        this.importer = new MWF.xApplication.Attendance.ImportExplorer.Importer( this );
        this.importer.upload();
    },
    checkData : function(){
        var selector = new MWF.xApplication.Attendance.ImportExplorer.YearMonthSelctor(this);
        selector.edit();
    },
    analyseData : function(){
        this.actions.analyseDetail("0","0",function(){
            this.app.notice("分析考勤数据成功","success")
        }.bind(this))
    },
    staticData : function(){
        this.actions.staticAllDetail(function(){
            this.app.notice("统计考勤数据成功","success")
        }.bind(this))
    },
    downloadTemplate : function(){
        window.open( this.path + encodeURIComponent( "dataTemplate.xls" ), "_blank" )
    },
    showDescription: function( el ){
        if( this.descriptionNode ){
            this.descriptionNode.setStyle("display","block");
            this.descriptionNode.position({
                relativeTo: el,
                position: 'bottomLeft',
                edge: 'upperCenter',
                offset:{
                    x : -60,
                    y : 0
                }
            });
        }else{
            this.descriptionNode = new Element("div", {"styles": this.css.descriptionNode}).inject(this.node);
            this.descriptionNode.position({
                relativeTo: el,
                position: 'bottomLeft',
                edge: 'upperCenter',
                offset:{
                    x : -60,
                    y : 0
                }
            });
            this.descriptionNode.addEvent("mousedown", function(e){e.stopPropagation();});
            document.body.addEvent("mousedown", function(){ this.descriptionNode.setStyle("display","none")}.bind(this));
            var table = new Element("table", {
                "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.filterTable, "class" : "filterTable"
            }).inject( this.descriptionNode );
            var tr = new Element("tr").inject(table);
            new Element("td",{ "text" : "数据导入步骤" , "styles" : this.css.descriptionTdHead }).inject(tr);
            var tr = new Element("tr").inject(table);
            new Element("td",{ "text" :"1、下载Excel模板，根据模板格式填写考勤数据；" , "styles" : this.css.descriptionTdValue  }).inject(tr);
            var tr = new Element("tr").inject(table);
            new Element("td",{ "text" : "2、点击导入考勤数据按钮，选择考勤数据并确定，系统将校验考勤数据是否正确并导入数据；"  , "styles" : this.css.descriptionTdValue }).inject(tr);
            var tr = new Element("tr").inject(table);
            new Element("td",{ "text" : "3、点击核对考勤数据按钮，选择需要核对的年度和月份，系统将核对需要考勤的人员的数据；"  , "styles" : this.css.descriptionTdValue }).inject(tr);
            var tr = new Element("tr").inject(table);
            new Element("td",{ "text" : "4、点击分析考勤数据按钮，系统将生成出勤明细数据；"  , "styles" : this.css.descriptionTdValue }).inject(tr);
            var tr = new Element("tr").inject(table);
            new Element("td",{ "text" : "5、点击统计考勤数据按钮，系统将生成个人、部门、公司的出勤率统计。"  , "styles" : this.css.descriptionTdValue }).inject(tr);
        }
    }
});

MWF.xApplication.Attendance.ImportExplorer.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.ImportExplorer.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
            this.actions.listAttachmentInfo(function(json){
                if (callback) callback(json);
            });
    },
    _removeDocument: function(document, all){
        this.actions.deleteAttachment(document.id, function(json){
            this.explorer.view.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _createDocument: function(){
        //var permission = new MWF.xApplication.Attendance.ImportExplorer.Importer(this.explorer);
        //permission.create();
    },
    _openDocument: function( documentData ){
        this.actions.getAttachmentStream( documentData.id )
    }

})

MWF.xApplication.Attendance.ImportExplorer.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document
    ,
    openVaild : function( e ){
        //this.importer = new MWF.xApplication.Attendance.ImportExplorer.Importer( this );
    }
})

MWF.xApplication.Attendance.ImportExplorer.YearMonthSelctor = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.PopupForm,
    _createTableContent: function(){

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td colspan='2' styles='formTableHead'>选择核对月份</td></tr>" +
            "<tr><td styles='formTabelTitle' lable='cycleYear'></td>"+
            "    <td styles='formTableValue' item='cycleYear'></td></tr>" +
            "<tr><td styles='formTabelTitle' lable='cycleMonth'></td>"+
            "    <td styles='formTableValue' item='cycleMonth'></td></tr>" +
            "</table>"
        this.formTableArea.set("html",html);
        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.formTableArea, {}, {
                isEdited : this.isEdited || this.isNew,
                itemTemplate : {
                    cycleYear : {
                        text:"年度",
                        type : "select",
                        selectValue : function(){
                            var years = []; d = new Date();
                            for(var i=0 ; i<5; i++){
                                years.push(d.getFullYear());
                                d.setFullYear(d.getFullYear()-1)
                            }
                            return years;
                        }
                    },
                    cycleMonth : {
                        text:"月份",
                        type : "select",
                        defaultValue : function(){ return new Date().getMonth(); },
                        selectValue : ["1","2","3","4","5","6","7","8","9","10","11","12"]
                    }
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    _ok: function( data, callback ){
        this.app.restActions.checkDetail( data.cycleYear, data.cycleMonth, function(json){
            this.app.notice("考勤数据核对成功");
            this.close();
        }.bind(this));
    }
})

MWF.xApplication.Attendance.ImportExplorer.Importer = new Class({
    Extends: MWF.widget.Common,
    initialize: function( explorer, data ){
        this.explorer = explorer;
        this.app = explorer.app;
        this.data = data || {};
        this.css = this.explorer.css;
        this.actions = this.explorer.actions;

        this.load();
    },
    load: function(){

    },
    _openCheckPage : function(  ){
        this.checkMarkNode = new Element("div", {
            "styles": this.css.checkMarkNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.app.content, "after");

        this.checkAreaNode = new Element("div", {
            "styles": this.css.checkAreaNode
        });

        this.createNode();

        this.checkAreaNode.inject(this.checkMarkNode, "after");
        this.checkAreaNode.fade("in");

        this.setCheckNodeSize();
        this.setCheckNodeSizeFun = this.setCheckNodeSize.bind(this);
        this.addEvent("resize", this.setCheckNodeSizeFun);
    },
    createNode: function(){
        var _self = this;
        this.checkNode = new Element("div", {
            "styles": this.css.checkNode
        }).inject(this.checkAreaNode);

        this.closeCheckNode = new Element("div", {
            "styles": this.css.closeCheckNode
        }).inject(this.checkNode);
        this.closeCheckNode.addEvent("click", function(){
            this.closeLayout();
        }.bind(this))


        this.checkFormNode = new Element("div", {
            "styles": this.css.checkFormNode
        }).inject(this.checkNode);

        var lp = this.app.lp.importer;

        this.checkFormTitleNode = new Element("div", {
            "styles": this.css.checkFormTitleNode,
            "text" : "考勤数据导入校验结果"
        }).inject(this.checkFormNode);

        this.checkFormDescriptionNode = new Element("div", {
            "styles": this.css.checkFormDescriptionNode,
            "text" : "您上传的文件：“" +  this.uploadFileName + "”未通过校验，请修改后重新导入。"
        }).inject(this.checkFormNode);

        this.checkTableContainer = new Element("div", {
            "styles": this.css.checkTableContainer
        }).inject(this.checkFormNode);

        this.checkTableArea = new Element("div", {
            "styles": this.css.checkTableArea
        }).inject(this.checkTableContainer);

        if( this.checkData.checkStatus != "error" ){

        }else{
            //"rowCount": 46,
            //    "errorCount": 46,

            var table = new Element("table", {
                "width" : "100%", "border" : "", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.editTable, "class" : "editTable"
            }).inject( this.checkTableArea );

            var tr = new Element("tr").inject(table);
            var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "员工号"  }).inject(tr);
            var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "员工名字"  }).inject(tr);
            var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "日期"  }).inject(tr);
            var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "上班时间"  }).inject(tr);
            var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "下班时间"  }).inject(tr);
            var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "检查结果"  }).inject(tr);
            var td = new Element("td", {  "styles" : this.css.editTableTitle, "text" : "描述"  }).inject(tr);
            td.setStyle( "width" , "300px" );

            this.checkData.detailList.each(function( d ){
                var tr = new Element("tr").inject(table);
                var td = new Element("td", { "styles" : this.css.editTableValue , "text": d.employeeNo }).inject(tr);
                var td = new Element("td", { "styles" : this.css.editTableValue , "text": d.employeeName }).inject(tr);
                var td = new Element("td", { "styles" : this.css.editTableValue , "text": d.recordDateString }).inject(tr);
                var td = new Element("td", { "styles" : this.css.editTableValue , "text": d.onDutyTime }).inject(tr);
                var td = new Element("td", { "styles" : this.css.editTableValue , "text": d.offDutyTime }).inject(tr);
                var td = new Element("td", { "styles" : this.css.editTableValue , "text": d.checkStatus == "error" ? "错误" : "正确" }).inject(tr);
                var td = new Element("td", { "styles" : this.css.editTableValue , "text": d.description }).inject(tr);
            }.bind(this))

        }
        //this.checkFormNode.set("html", html);

        this.setScrollBar(this.checkTableContainer)

        //
        //this.cancelActionNode = new Element("div", {
        //    "styles": this.css.checkCancelActionNode,
        //    "text": this.app.lp.cancel
        //}).inject(this.checkFormNode);
        //this.checkOkActionNode = new Element("div", {
        //    "styles": this.css.checkOkActionNode,
        //    "text": this.app.lp.ok
        //}).inject(this.checkFormNode);
        //
        //this.cancelActionNode.addEvent("click", function(e){
        //    this.cancelCreate(e);
        //}.bind(this));
        //this.checkOkActionNode.addEvent("click", function(e){
        //    this.okCreate(e);
        //}.bind(this));
    },

    setCheckNodeSize: function(){
        var size = this.app.node.getSize();
        var allSize = this.app.content.getSize();

        this.checkAreaNode.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px"
        });
        var hY = size.y*0.9;
        var mY = size.y*0.2/2;
        this.checkNode.setStyles({
            "height": ""+hY+"px",
            "margin-top": ""+mY+"px"
        });

        var formHeight = hY*0.95;
        if (formHeight< 250) formHeight = 250;
        this.checkFormNode.setStyles({
            "height": ""+formHeight+"px"
        });

        var titlesize = this.checkFormTitleNode.getSize();
        var descriptionsize = this.checkFormDescriptionNode.getSize();
        var tableHeight = formHeight - titlesize.y - descriptionsize.y - 50;
        this.checkTableContainer.setStyles({
            "height": ""+tableHeight+"px"
        });
    },
    closeLayout: function(e){
        this.checkMarkNode.destroy();
        this.checkAreaNode.destroy();
        delete this;
    },

    upload : function(){
        if (!this.uploadFileAreaNode){
            this.uploadFileAreaNode = new Element("div");
            var html = "<input name=\"file\" type=\"file\"/>";
            this.uploadFileAreaNode.set("html", html);

            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", function(){

                var files = fileNode.files;
                if (files.length){
                    for (var i = 0; i < files.length; i++) {
                        var file = files.item(i);
                        var tmp = file.name.split(".");
                        this.uploadFileName = file.name;
                        if( tmp[tmp.length-1].toLowerCase() != "xls" && tmp[tmp.length-1].toLowerCase() != "xlsx" ){
                            this.app.notice("请导入excel文件！","error");
                            return;
                        }
                        var formData = new FormData();
                        formData.append('file', file);
                        this.actions.uploadAttachment( function( json ){
                            var id = json.id;
                            this.actions.checkAttachment(id, function(data){
                                this.checkData = data.data;
                                if( this.checkData.checkStatus == "error" ){
                                    this._openCheckPage()
                                }else{
                                    this.app.notice("文件已经成功上传并通过校验，系统正在导入，请不要关闭页面！","success");
                                    this.import( id );
                                }
                            }.bind(this))
                        }.bind(this), function(xhr, text, error){
                            var errorText = error;
                            if (xhr) errorText = xhr.responseText;
                            this.app.notice( errorText,"error");
                        }.bind(this), formData, file);
                    }
                }
            }.bind(this));
        }
        var fileNode = this.uploadFileAreaNode.getFirst();
        fileNode.click();
    },
    import: function( id ){
        this.actions.importAttachment( id, function(json){
            if( json.type == "ERROR" ){
                this.app.notice( json.message  , "error");
            }else{
                this.app.notice("数据导入成功！","success");

                //this.app.notice("数据导入成功，系统正在分析打卡记录！","success");
                //var recordList = json.data.dateRecordList;
                //var listCount = recordList.length;
                //var prcessedCount = 0;
                //
                //this.actions.analyseDetail( "(0)" , "(0)" ,function(){
                //
                //    this.app.notice("分析打卡数据成功，系统正在统计考勤记录！","success");
                //
                //    recordList.each( function( r ){
                //        this.actions.staticDetail( r.year , r.month ,function(){
                //            prcessedCount ++;
                //            if( prcessedCount == listCount ){
                //                this.app.notice("统计考勤记录成功，已完成所有导入步骤！","success");
                //            }
                //        }.bind(this))
                //    }.bind(this))
                //
                //}.bind(this))

            }
        }.bind(this))
    }

})

