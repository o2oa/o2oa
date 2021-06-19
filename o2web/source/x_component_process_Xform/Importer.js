MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("process.Xform", "Button", null, false);
/** @class Importer 导入数据组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var importer = this.form.get("fieldId"); //获取组件
 * //方法2
 * var importer = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.Importer
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Importer = MWF.APPImporter =  new Class({
	Implements: [Events],
    Extends: MWF.xApplication.process.Xform.Button,
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
    upload: function () {
	    debugger;
	    var options;
	    if( this.json.queryImportModel.id ){
            options = {
                "id" : this.json.queryImportModel.id
            };
        }else{
            options = {
                "application": this.json.queryImportModel.application || this.json.queryImportModel.appName,
                "name": this.json.queryImportModel.alias || this.json.queryImportModel.name
            }
        }
        MWF.xDesktop.requireApp("query.Query", "Importer", function () {
            var importer = new MWF.xApplication.query.Query.Importer(this.form.app.content, options, {}, this.form.app, this.form.Macro);
            importer.load();
        }.bind(this));
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

        var options;
        if( this.json.queryImportModel.id ){
            options = {
                "id" : this.json.queryImportModel.id
            };
        }else{
            options = {
                "application": this.json.queryImportModel.application || this.json.queryImportModel.appName,
                "name": this.json.queryImportModel.alias || this.json.queryImportModel.name
            }
        }
        MWF.xDesktop.requireApp("query.Query", "Importer", function () {
            var importer = new MWF.xApplication.query.Query.Importer(this.form.app.content, options, {}, this.form.app, this.form.Macro);
            importer.downloadTemplate( this.getExcelName() );
        }.bind(this));
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
