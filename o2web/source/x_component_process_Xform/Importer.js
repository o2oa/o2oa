MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("process.Xform", "Button", null, false);
/** @class Importer 导入数据组件，本组件通过导入模型来执行数据的导入，支持内容管理文档，流程管理work，自建表数据的导入。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var importer = this.form.get("fieldId"); //获取组件
 * //方法2
 * var importer = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.Button
 * @o2category FormComponents
 * @since v6.2
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Importer = MWF.APPImporter =  new Class(
    /** @lends MWF.xApplication.process.Xform.Importer# */
    {
	Implements: [Events],
    Extends: MWF.xApplication.process.Xform.Button,
    options: {
        /**
         * 加载importer（导入模型对象）的时候执行，可以通过this.target.importer获取导入模型对象。
         * @event MWF.xApplication.process.Xform.Importer#loadImporter
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 导入前触发，this.event指向导入的数据，您可以通过修改this.event来修改数据。
         * @event MWF.xApplication.process.Xform.Importer#beforeImport
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         * @example
         * <caption>this.event数据格式如下：</caption>
         *[
         *  [ "标题一","张三","男","大学本科","计算机","2001-1-2","2019-9-2" ], //第一行数据
         *  [ "标题二","李四","男","大学专科","数学","1998-1-2","2018-9-2" ]  //第二行数据
         *]
         */
        /**
         * 数据已经生成，前台进行数据校验时触发，this.event指向导入的数据。
         * @event MWF.xApplication.process.Xform.Importer#validImport
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
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
        /**
         * 前台校验成功，并且后台执行完导入后触发，this.event指向后台返回的导入结果。
         * @event MWF.xApplication.process.Xform.Importer#afterImport
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         * @example
         * <caption>this.event格式如下：</caption>
         * {
         *     "status": "导入成功", //导入结果：状态有 "导入成功","部分成功","导入失败"
         *     "count" : 10, //导入总数量
         *     "failCount": 0, //失败数量
         *     "distribution": "" //导入时候时的错误信息
         * }
         */
        /**
         * 创建每行需要导入的数据前触发，this.event指向当前行对象，您可以通过修改this.event.importData来修改数据。
         * @event MWF.xApplication.process.Xform.Importer#beforeCreateRowData
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 创建每行需要导入的数据后触发，this.event指向当前行对象。
         * @event MWF.xApplication.process.Xform.Importer#afterCreateRowData
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
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
        "moduleEvents": ["queryLoad","postLoad", "afterLoad", "loadImporter",
            "beforeImport", "validImport","afterImport", "beforeCreateRowData", "afterCreateRowData"]
    },
    _loadUserInterface: function(){
        var button = this.node.getElement("button");
        if (!button) button = new Element("button");
        button.inject(this.node, "after");
        this.node.destroy();
        this.node = button;
        this.node.set({
            "id": this.json.id,
            "text": this.json.name || this.json.id,
            "styles": this.form.css.buttonStyles,
            "MWFType": this.json.type
        });
        this.node.addEvent("click", function(){
            this.upload();
        }.bind(this));
        if( this.json.allowDownloadTempalte && this.json.downloadTempalteFieldId ){
            this.setDownloadEvent();
        }
    },
    getImporter: function(callback){
        var options;
        if( this.json.queryImportModel.id ){
            options = { "id" : this.json.queryImportModel.id };
        }else{
            options = {
                "application": this.json.queryImportModel.application || this.json.queryImportModel.appName,
                "name": this.json.queryImportModel.alias || this.json.queryImportModel.name
            }
        }
        MWF.xDesktop.requireApp("query.Query", "Importer", function () {
            /**
             * @summary 导入模型对象.
             * @member {MWF.xApplication.query.Query.Importer}
             * @example
             * var importer = this.form.get("fieldId").importer; //获取组件
             * if(importer)importer.importFromExcel(); //执行导入
             */
            this.importer = new MWF.xApplication.query.Query.Importer(this.form.app.content, options, {
                "onQueryLoad": function () {
                    this.fireEvent("loadImporter")
                }.bind(this),
                "onBeforeImport": function (importedData) {
                    this.fireEvent("beforeImport", importedData)
                }.bind(this),
                "onValidImport": function (arg) {
                    this.fireEvent("validImport", [arg])
                }.bind(this),
                "onAfterImport": function ( infor ) {
                    this.fireEvent("afterImport", [infor])
                }.bind(this),
                "onBeforeCreateRowData": function (row) {
                    this.fireEvent("beforeCreateRowData", [row])
                }.bind(this),
                "onAfterCreateRowData": function (row) {
                    this.fireEvent("afterCreateRowData", [row])
                }.bind(this),
            }, this.form.app, this.form.Macro);
            if(callback)callback();
        }.bind(this));
    },
    upload: function () {
        if( this.importer ){
            this.importer.importFromExcel();
        }else{
            this.getImporter(function(){
                this.importer.load();
            }.bind(this))
        }

    },
    setDownloadEvent: function () {
        this.bindEvent = function () {
            var module = this._getModuleByPath(this.json.downloadTempalteFieldId);

            if(module)module.node.addEvent("click", function () {
                this.downloadTemplate();
            }.bind(this))

            this.fireEvent("afterLoad");

            //加载完成以后，删除事件
            this.form.removeEvent("afterModulesLoad", this.bindEvent );
        }.bind(this);

        //去要表单的所有组件加载完成以后再去获取外部组件
        this.form.addEvent("afterModulesLoad", this.bindEvent );
    },
    downloadTemplate: function(){
        if( this.importer ){
            this.importer.downloadTemplate( this.getExcelName() );
        }else{
            this.getImporter(function(){
                this.importer.downloadTemplate( this.getExcelName() );
            }.bind(this))
        }
    },

    getExcelName: function(){
	    debugger;
        var title;
        if( this.json.excelName && this.json.excelName.code ){
            title = this.form.Macro.exec(this.json.excelName.code, this);
        }
        return title || ""
    }
	
}); 
