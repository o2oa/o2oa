MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);
/** @class Statement 查询视图组件。
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
         * @event MWF.xApplication.process.Xform.View#beforeLoadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 异步加载查询视图后完成。
         * @event MWF.xApplication.process.Xform.Statement#loadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 选中查询视图中的一条记录后执行。
         * @event MWF.xApplication.process.Xform.Statement#select
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 打开查询视图中的一条记录后执行。
         * @event MWF.xApplication.process.Xform.Statement#openDocument
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "beforeLoadView", "loadView", "queryLoad", "postLoad", "select", "openDocument"]
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
    reload: function(){
        if (this.view){
            if (this.view.loadViewRes && this.view.loadViewRes.res) if (this.view.loadViewRes.res.isRunning()) this.view.loadViewRes.res.cancel();
            if (this.view.getViewRes && this.view.getViewRes.res) if (this.view.getViewRes.res.isRunning()) this.view.getViewRes.res.cancel();
        }
        this.node.empty();
        this.loadView();
    },
    /**
     * @summary 当查询视图被设置为延迟加载（未立即载入），通过active方法激活
     * @example
     * this.form.get("fieldId").active()
     */
    active: function(){
        if (this.view){
            if (!this.view.loadingAreaNode) this.view.loadView();
        }else{
            this.loadView();
        }
    },
    loadView: function(){
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
                "onLoadView": function(){
                    this.fireEvent("loadView");
                }.bind(this),
                "onSelect": function(){
                    this.fireEvent("select");
                }.bind(this),
                "onOpenDocument": function(options, item){
                    this.openOptions = {
                        "options": options,
                        "item": item
                    };
                    this.fireEvent("openDocument");
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
    }
});
