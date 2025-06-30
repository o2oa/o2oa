MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("process.Xform", "ViewSelector", null, false);
/** @class StatementSelector 查询视图选择组件。
 * @o2cn 查询视图选择
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var statementSelector = this.form.get("fieldId"); //获取组件
 * //方法2
 * var statementSelector = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.ViewSelector
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.StatementSelector = MWF.APPStatementSelector =  new Class({
	Implements: [Events],
	Extends: MWF.xApplication.process.Xform.ViewSelector,
    options: {
        /**
         * 视图参数（options）已经准备好，还未加载视图时执行。可以通过this.event得到视图参数，并可修改this.event修改视图的加载。
         * @since V8.2
         * @event MWF.xApplication.process.Xform.StatementSelector#beforeLoadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 视图设计已经获取，容器也已经准备好。
         * @event MWF.xApplication.process.Xform.StatementSelector#loadViewLayout
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 异步加载查询视图后执行。
         * @since V8.2
         * @event MWF.xApplication.process.Xform.StatementSelector#loadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 选中查询视图中的一条记录后执行。
         * @since V8.2
         * @event MWF.xApplication.process.Xform.StatementSelector#select
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 取消选中查询视图中的一条记录后执行。
         * @since V8.2
         * @event MWF.xApplication.process.Xform.StatementSelector#unselect
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 打开查询视图中的一条记录后执行。
         * @event MWF.xApplication.process.Xform.StatementSelector#openDocument，可以通过this.event得到打开的文档参数
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 加载对话框的时候执行，this.event可以获取到对话框对象。
         * @event MWF.xApplication.process.Xform.ViewSelector#loadDialog
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 显示对话框的时候执行，this.event可以获取到对话框对象。
         * @event MWF.xApplication.process.Xform.ViewSelector#showDialog
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "loadDialog", 'showDialog', "beforeLoadView", "loadViewLayout", "loadView", "queryLoad", "postLoad", "select", "unselect", "openDocument"]
    },
    doResult: function(data){
        if (this.json.result === "script"){
            this.selectedData = data;
            return (this.json.selectedScript.code) ? this.form.Macro.exec(this.json.selectedScript.code, this) : "";
        }else{
            Object.each(this.json.selectedSetValues, function(v, k){
                var value = "";
                data.each(function(d, idx){
                    // Object.each(d, function(dv, dk){
                    //     if (dk===v) value = (value) ? (value+", "+dv) : dv;
                    // }.bind(this));

                    var pathList = v.split(".");
                    for( var i=0; i<pathList.length; i++ ){
                        var p = pathList[i];
                        if( (/(^[1-9]\d*$)/.test(p)) )p = p.toInt();
                        if( d[ p ] ){
                            d = d[ p ];
                        }else{
                            d = "";
                            break;
                        }
                    }

                    if( typeOf(d) === "array" || typeOf(d) === "object" ) {
                        d = JSON.stringify(d);
                    }

                    value = (value) ? (value+", "+d) : d;

                }.bind(this));

                var field = this.form.all[k];
                if (field){
                    field.setData(value);
                    if (value){
                        if (field.descriptionNode) field.descriptionNode.setStyle("display", "none");
                    }else{
                        if (field.descriptionNode) field.descriptionNode.setStyle("display", "block");
                    }
                }
            }.bind(this));
        }
    },
    getViewName: function (){
        var appName, statementName, statementId;
        if (this.json.statementType === "script") {
            if (this.json.statementScript && this.json.statementScript.code) {
                var data = this.form.Macro.exec(this.json.statementScript.code, this);
                if (typeOf(data) === 'object') {
                    appName = data.application;
                    statementName = data.statement;
                }else if(typeOf(data) === 'string'){
                    statementName = data;
                }
            }
        }else{
            appName = (this.json.queryStatement) ? this.json.queryStatement.appName : "";
            statementName =  (this.json.queryStatement) ? this.json.queryStatement.name : "";
            statementId = (this.json.queryStatement) ? this.json.queryStatement.id : "";
        }
        return {appName: appName, statementName: statementName, statementId: statementId};
    },
    selectView: function(callback){

        var viewObj = this.getViewName();
        var appName = viewObj.appName, statementName = viewObj.statementName, statementId = viewObj.statementId;
        if( !statementName && !statementId ){
            if(callback) callback();
            return ;
        }

        var filter = null;
        if (this.json.filterList && this.json.filterList.length){
            filter = [];
            this.json.filterList.each(function(entry){
                entry.value = this.form.Macro.exec(entry.code.code, this);
                //delete entry.code;
                filter.push(entry);
            }.bind(this));
        }

        var parameter = null;
        if( this.json.parameterList && this.json.parameterList.length ){
            parameter = {};
            this.json.parameterList.each(function(entry){
                parameter[entry.parameter] = this.parseParameter(entry);
            }.bind(this));
        }

        var viewJson = {
            "application": appName,
            "statementName": statementName,
            "statementId": statementId,
            "isTitle": this.json.isTitle || "yes",
            "select": this.json.select || "single",
            "titleStyles": this.json.titleStyles,
            "itemStyles": this.json.itemStyles,
            "isExpand": this.json.isExpand || "no",
            "showActionbar" : this.json.actionbar === "show",
            "filter": filter,
            "parameter": parameter,
            "defaultSelectedScript" : this.json.defaultSelectedScript ? this.json.defaultSelectedScript.code : null,
            "selectedAbleScript" : this.json.selectedAbleScript ? this.json.selectedAbleScript.code : null
        };

        this.viewJson = viewJson;

        var viewOptions = {
            "style": "select",
            "onLoadLayout": function () {
                this.fireEvent("loadViewLayout");
            }.bind(this),
            "onLoadView": function(){
                this.fireEvent("loadView");
            }.bind(this),
            "onSelect": function(item){
                this.fireEvent("select", [item]);
            }.bind(this),
            "onUnselect": function(item){
                this.fireEvent("unselect", [item]);
            }.bind(this),
            "onOpenDocument": function(options, item){
                this.openOptions = {
                    "options": options,
                    "item": item
                };
                this.fireEvent("openDocument", [this.openOptions]);
                this.openOptions = null;
            }.bind(this)
        };
        this.viewOptions = viewOptions;

        var _self = this;

        var dlgOptions = {
            title: this.json.title,
            width: this.json.DialogWidth || "850",
            height: this.json.DialogHeight || "700",
            style: this.json.viewStyle || "v10_view",
            "onPostLoad": function (){
                _self.fireEvent("loadDialog", [this]);
            },
            "onPostShow": function(){
                if(layout.mobile){
                    this.node.setStyle("z-index",200);
                }
                _self.fireEvent("showDialog", [this]);
            }
        };
        this.dialogOptions = dlgOptions;

        this.fireEvent("beforeLoadView", [viewJson]);

        this.form.Macro.environment.statement.select(viewJson, callback, dlgOptions, viewOptions, (viewer)=>{
            this.view = viewer;
        });
    },
    parseParameter: function (f) {
        var value = f.value;
        if( f.valueType === "script" ){
            value = this.form.Macro.exec(f.valueScript ? f.valueScript.code : "", this);
        }
        if (typeOf(value) === "date") {
            value = value.format("db");
        }
        var user = layout.user;
        switch (value) {
            case "@person":
                value = user.distinguishedName;
                break;
            case "@identityList":
                value = user.identityList.map(function (d) {
                    return d.distinguishedName;
                });
                break;
            case "@unitList":
                o2.Actions.load("x_organization_assemble_express").UnitAction.listWithPerson({"personList": [user.distinguishedName]}, function (json) {
                    value = json.unitList;
                }, null, false);
                break;
            case "@unitAllList":
                o2.Actions.load("x_organization_assemble_express").UnitAction.listWithIdentitySupNested({"personList": [user.distinguishedName]}, function (json) {
                    value = json.unitList;
                }, null, false);
                break;
            case "@year":
                value = (new Date().getFullYear()).toString();
                break;
            case "@season":
                var m = new Date().format("%m");
                if (["01", "02", "03"].contains(m)) {
                    value = "1"
                } else if (["04", "05", "06"].contains(m)) {
                    value = "2"
                } else if (["07", "08", "09"].contains(m)) {
                    value = "3"
                } else {
                    value = "4"
                }
                break;
            case "@month":
                value = new Date().format("%Y-%m");
                break;
            case "@time":
                value = new Date().format("db");
                break;
            case "@date":
                value = new Date().format("%Y-%m-%d");
                break;
            default:
        }

        if (f.formatType === "dateTimeValue" || f.formatType === "datetimeValue") {
            value = "{ts '" + value + "'}"
        } else if (f.formatType === "dateValue") {
            value = "{d '" + value + "'}"
        } else if (f.formatType === "timeValue") {
            value = "{t '" + value + "'}"
        }
        return value;
    }

});
