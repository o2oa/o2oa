MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);
/** @class Statement 查询视图组件。
 * @o2cn 查询视图
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var statement = this.form.get("fieldId"); //获取组件
 * //方法2
 * var statement = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Statement = MWF.APPStatement =  new Class(
    /** @lends MWF.xApplication.process.Xform.Statement# */
{
	Extends: MWF.APP$Module,
    options: {
        /**
         * 视图参数（options）已经准备好，还未加载视图时执行。可以通过this.event得到视图参数，并可修改this.event修改视图的加载。
         * @event MWF.xApplication.process.Xform.Statement#beforeLoadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 视图设计已经获取，容器也已经准备好。可以通过this.event得到视图参数，并可修改this.event修改视图的加载。
         * @event MWF.xApplication.process.Xform.Statement#loadViewLayout
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 异步加载查询视图后执行。
         * @event MWF.xApplication.process.Xform.Statement#loadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 选中查询视图中的一条记录后执行。
         * @event MWF.xApplication.process.Xform.Statement#select
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 取消选中查询视图中的一条记录后执行。
         * @since V8.0
         * @event MWF.xApplication.process.Xform.Statement#unselect
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 打开查询视图中的一条记录后执行。
         * @event MWF.xApplication.process.Xform.Statement#openDocument，可以通过this.event得到打开的文档参数
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "beforeLoadView", "loadViewLayout", "loadView", "queryLoad", "postLoad", "select", "unselect", "openDocument"]
    },

    _loadUserInterface: function(){
        MWF.xDesktop.requireApp("query.Query", "Statement", null, false);
        this.node.empty();
    },
    _afterLoaded: function(){
        if (this.json.queryStatement){
            this.loadView();
        }
    },
    /**
     * @summary 重新加载查询视图
     * @example
     * this.form.get("fieldId").reload()
     */
    reload: function( callback ){
        if (this.view){
            if (this.view.loadViewRes && this.view.loadViewRes.res) if (this.view.loadViewRes.res.isRunning()) this.view.loadViewRes.res.cancel();
            if (this.view.getViewRes && this.view.getViewRes.res) if (this.view.getViewRes.res.isRunning()) this.view.getViewRes.res.cancel();
        }
        this.node.empty();
        this.loadView( callback );
    },
    /**
     * @summary 当查询视图被设置为延迟加载（未立即载入），通过active方法激活
     * @example
     * this.form.get("fieldId").active()
     */
    active: function( callback ){
        if (this.view){
            if (!this.view.loadingAreaNode) this.view.loadView( callback );
        }else{
            this.loadView( callback );
        }
    },
    loadView: function( callback ){
        if (!this.json.queryStatement) return "";

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



        //var data = JSON.parse(this.json.data);
        var viewJson = {
            "application": (this.json.queryStatement) ? this.json.queryStatement.appName : this.json.application,
            "statementName": (this.json.queryStatement) ? this.json.queryStatement.name : this.json.statementName,
            "statementId": (this.json.queryStatement) ? this.json.queryStatement.id : this.json.statementId,
            "isTitle": this.json.isTitle || "yes",
            "select": this.json.select || "none",
            "titleStyles": this.json.titleStyles,
            "itemStyles": this.json.itemStyles,
            "isExpand": this.json.isExpand || "no",
            "showActionbar" : this.json.actionbar === "show",
            "filter": filter,
            "parameter": parameter,
            "parameterList": this.json.parameterList,
            "defaultSelectedScript" : this.json.defaultSelectedScript ? this.json.defaultSelectedScript.code : null,
            "selectedAbleScript" : this.json.selectedAbleScript ? this.json.selectedAbleScript.code : null
        };

        this.fireEvent("beforeLoadView", [viewJson]);

        //MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
        /**
         * @summary Statement组件，平台使用该组件实现查询视图的功能
         * @member {MWF.xApplication.query.Query.Statement}
         * @example
         *  //可以在脚本中获取该组件
         * var view = this.form.get("fieldId").view; //获取组件对象
         */
            this.view = new MWF.xApplication.query.Query.Statement(this.node, viewJson, {
                "isload": (this.json.loadView!=="no"),
                "resizeNode": (this.node.getStyle("height").toString().toLowerCase()!=="auto" && this.node.getStyle("height").toInt()>0),
                "onLoadLayout": function () {
                    this.fireEvent("loadViewLayout");
                }.bind(this),
                "onLoadView": function(){
                    this.fireEvent("loadView");
                    if(callback)callback();
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
            }, this.form.app, this.form.Macro);
        //}.bind(this));
    },
    /**
     * @summary 获取查询视图被选中行的数据
     * @return {Object[]} 被选中行的数据
     * @example
     * var data = this.form.get("fieldId").getData();
     */
    getData: function(){
        if (this.view.selectedItems.length){
            var arr = [];
            this.view.selectedItems.each(function(item){
                arr.push(item.data);
            });
            return arr;
        }else{
            return [];
        }
    },
    parseParameter: function (f) {
        var value = f.value;
        if( f.valueType === "script" ){
            value = this.form.Macro.exec(f.valueScript ? f.valueScript.code : "", this);
        }
        return value;
        // 后面的放在 queryStatement中解析了
        // if (typeOf(value) === "date") {
        //     value = value.format("db");
        // }
        // var user = layout.user;
        // switch (value) {
            // case "@person":
            //     value = user.distinguishedName;
            //     break;
            // case "@identityList":
            //     value = user.identityList.map(function (d) {
            //         return d.distinguishedName;
            //     });
            //     break;
            // case "@unitList":
            //     o2.Actions.load("x_organization_assemble_express").UnitAction.listWithPerson({"personList": [user.distinguishedName]}, function (json) {
            //         value = json.unitList;
            //     }, null, false);
            //     break;
            // case "@unitAllList":
            //     o2.Actions.load("x_organization_assemble_express").UnitAction.listWithIdentitySupNested({"personList": [user.distinguishedName]}, function (json) {
            //         value = json.unitList;
            //     }, null, false);
            //     break;
        //     case "@year":
        //         value = (new Date().getFullYear()).toString();
        //         break;
        //     case "@season":
        //         var m = new Date().format("%m");
        //         if (["01", "02", "03"].contains(m)) {
        //             value = "1"
        //         } else if (["04", "05", "06"].contains(m)) {
        //             value = "2"
        //         } else if (["07", "08", "09"].contains(m)) {
        //             value = "3"
        //         } else {
        //             value = "4"
        //         }
        //         break;
        //     case "@month":
        //         value = new Date().format("%Y-%m");
        //         break;
        //     case "@time":
        //         value = new Date().format("db");
        //         break;
        //     case "@date":
        //         value = new Date().format("%Y-%m-%d");
        //         break;
        //     default:
        // }
        //
        // if (f.formatType === "dateTimeValue" || f.formatType === "datetimeValue") {
        //     value = "{ts '" + value + "'}"
        // } else if (f.formatType === "dateValue") {
        //     value = "{d '" + value + "'}"
        // } else if (f.formatType === "timeValue") {
        //     value = "{t '" + value + "'}"
        // }
        // return value;
    }
});
