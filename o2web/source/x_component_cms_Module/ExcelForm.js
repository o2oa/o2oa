MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Module = MWF.xApplication.cms.Module || {};
MWF.xDesktop.requireApp("cms.Module", "$ExcelForm.lp."+MWF.language, null, false);
//MWF.xDesktop.requireApp("cms.Module", "Actions.RestActions", null, false);

MWF.xApplication.cms.Module.ImportForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "cms",
        "width": "650",
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title" : MWF.xApplication.cms.Module.ExcelForm.lp.importData
    },
    initialize: function (explorer, data, options, para) {
        this.lp = MWF.xApplication.cms.Module.ExcelForm.lp;

        this.setOptions(options);

        this.data = data;

        this.explorer = explorer;
        this.app = this.explorer.app;
        this.container = this.app.content;

        this.action = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.Module.Actions.RestActions();

        this.path = "../x_component_cms_Module/$ExcelForm/";
        this.cssPath = "../x_component_Template/$MPopupForm/"+this.options.style+"/css.wcss";

        this.load();
        //this.orgAction = new MWF.xAction.org.express.RestActions();
    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
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
                url: { text : this.lp.downloadTemplate }, //"下载模板"
                file: { type : "button", value : this.lp.selectExcelFile ,text : this.lp.selectFile, event :{
                    click : function(){
                        this.selectFile();
                    }.bind(this)
                } }
            }
        }, this.app);
        this.form.load();

    },
    _setCustom: function(){
        this.formTableContainer.setStyles({
            "margin-left" : "60px",
            "width" : "520px"
        });

        this.formBottomNode.setStyles({
            "padding-right" : "195px",
            "padding-bottom" : "20px"
        });

    },
    selectFile: function () {
        if (!this.uploadFileAreaNode) {
            this.uploadFileAreaNode = new Element("div");
            var html = "<input name=\"file\" type=\"file\" accept=\"csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel\" />";
            this.uploadFileAreaNode.set("html", html);

            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", function () {
                var files = fileNode.files;
                if (files.length) {
                    var file = files.item(0);
                    if( file.name.indexOf(" ") > -1 ){
                        this.app.notice("上传的文件不能带空格", "error");
                        return false;
                    }
                    this.file = file;
                    this.formData = new FormData();
                    this.formData.append('file', this.file);
                    var fileNameDiv = this.formTableArea.getElement("[item='filename']");
                    fileNameDiv.set("text",file.name);
                }
            }.bind(this));
        }
        var fileNode = this.uploadFileAreaNode.getFirst();
        fileNode.click();
    },
    ok: function( callback ){
        if( !this.formData ){
            this.app.notice( this.lp.selectExcelFileNotice, "error" ); //"请先选择Excel文件"
        }else{
            this.action.importDocumentFormExcel(this.data.id, function () {
                this.formData = null;
                this.file = null;
            }.bind(this), null, this.formData, this.file);
        }
    }
});


MWF.xApplication.cms.Module.ExportForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "cms",
        "width": "850",
        "height": "700",
        "maxAction" : true,
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "draggable": true,
        "title" : MWF.xApplication.cms.Module.ExcelForm.lp.exportData
    },
    initialize: function (explorer, data, options, para) {
        MWF.xDesktop.requireApp("cms.Module", "$ExcelForm.lp."+MWF.language, null, false);
        this.lp = MWF.xApplication.cms.Module.ExcelForm.lp;

        this.setOptions(options);

        this.data = data;

        this.explorer = explorer;
        this.app = this.explorer.app;
        this.container = this.app.content;

        this.action = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.Module.Actions.RestActions();

        this.path = "../x_component_cms_Module/$ExcelForm/";
        this.cssPath = "../x_component_Template/$MPopupForm/"+this.options.style+"/css.wcss";

        this.load();
        //this.orgAction = new MWF.xAction.org.express.RestActions();
    },
    _setCustom: function(){
        //this.formTableContainer.setStyles({
        //    "margin-left" : "60px",
        //    "width" : "520px"
        //});
        //
        //this.formBottomNode.setStyles({
        //    "padding-right" : "195px",
        //    "padding-bottom" : "20px"
        //});

    },
    _createTableContent: function(){
        //var filter = null;
        //if (this.json.filterList && this.json.filterList.length){
        //    filter = [];
        //    this.json.filterList.each(function(entry){
        //        entry.value = this.form.Macro.exec(entry.code.code, this);
        //        //delete entry.code;
        //        filter.push(entry);
        //    }.bind(this));
        //}
        this.formTableArea.setStyles({
            "margin-left":"20px",
            "margin-right":"20px"
        });
        var viewJson = {
            //"application": this.json.queryView.appName,
            "application" : this.data.importViewAppId,
            "viewName": this.data.importViewId, //this.data.importViewId, //this.json.queryView.name
            "isTitle": "yes",
             "select": "multi"
            //"isTitle": this.json.isTitle || "yes",
            //"select": this.json.select || "none",
            //"titleStyles": this.json.titleStyles,
            //"itemStyles": this.json.itemStyles,
            //"isExpand": this.json.isExpand || "no",
            //"filter": filter
        };

        MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
            this.view = new MWF.xApplication.query.Query.Viewer(this.formTableArea, viewJson, {
                //"resizeNode": (this.node.getStyle("height").toString().toLowerCase()!=="auto" && this.node.getStyle("height").toInt()>0),
                "onSelect": function(){
                    this.fireEvent("select");
                }.bind(this)
            }, this.app);
        }.bind(this));
    },
    _setNodesSize : function(width, height, formContentHeight, formTableHeight){
        this.formTableArea.setStyles({
            "height" : formTableHeight + "px"
        });
        if(this.view && this.view.node && this.view.viewAreaNode )this.view.setContentHeight();
    }
});