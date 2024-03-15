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
        "moduleEvents": ["load", "beforeLoadView", "loadViewLayout", "loadView", "queryLoad", "postLoad", "select", "unselect", "openDocument"]
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
    selectView: function(callback){
        var viewData = this.json.queryStatement;

        if (viewData){
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
                "application": viewData.appName,
                "statementName": viewData.name,
                "statementId": viewData.id,
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

            this.fireEvent("beforeLoadView", [viewJson]);

            var options = {};
            // var width = options.width || "850";
            // var height = options.height || "700";
            var width = this.json.DialogWidth || "850";
            var height = this.json.DialogHeight || "700";

            if (layout.mobile){
                var size = document.body.getSize();
                width = size.x;
                height = size.y;
                options.style = "viewmobile";
            }
            width = width.toInt();
            height = height.toInt();

            var size = this.form.app.content.getSize();
            var x = (size.x-width)/2;
            var y = (size.y-height)/2;
            if (x<0) x = 0;
            if (y<0) y = 0;
            if (layout.mobile){
                x = 20;
                y = 0;
            }

            var _self = this;
            MWF.require("MWF.xDesktop.Dialog", function(){
                var dlg = new MWF.xDesktop.Dialog({
                    "title": this.json.title || "select view",
                    "style": options.style || "view",
                    "top": y,
                    "left": x-20,
                    "fromTop":y,
                    "fromLeft": x-20,
                    "width": width,
                    "height": height,
                    "html": "",
                    "maskNode": layout.mobile?$(document.body) : this.form.app.content,
                    "container": layout.mobile?$(document.body) : this.form.app.content,
                    "buttonList": [
                        {
                            "text": MWF.LP.process.button.ok,
                            "action": function(){
                                //if (callback) callback(_self.view.selectedItems);

                                if (callback) callback(_self.view.getData());
                                this.close();
                            }
                        },
                        {
                            "text": MWF.LP.process.button.cancel,
                            "action": function(){this.close();}
                        }
                    ],
                    "onPostShow": function(){
                        if(layout.mobile){
                            dlg.node.setStyle("z-index",200);
                        }
                        MWF.xDesktop.requireApp("query.Query", "Statement", function(){
                            this.view = new MWF.xApplication.query.Query.Statement(dlg.content, viewJson, {
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
                            }, this.form.app, this.form.Macro );
                        }.bind(this));
                    }.bind(this)
                });
                dlg.show();

                if (layout.mobile){
                    var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                    var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                    if (backAction) backAction.addEvent("click", function(e){
                        dlg.close();
                    }.bind(this));
                    if (okAction) okAction.addEvent("click", function(e){
                        //if (callback) callback(this.view.selectedItems);
                        if (callback) callback(this.view.getData());
                        dlg.close();
                    }.bind(this));
                }

                // MWF.xDesktop.requireApp("process.Xform", "widget.View", function(){
                //     this.view = new MWF.xApplication.process.Xform.widget.View(dlg.content.getFirst(), viewJson, {"style": "select"});
                // }.bind(this));
                // MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
                //     this.view = new MWF.xApplication.query.Query.Viewer(dlg.content, viewJson, {"style": "select"});
                // }.bind(this));
            }.bind(this));
        }
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
