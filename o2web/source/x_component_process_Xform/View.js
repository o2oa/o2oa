MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);
/** @class View 视图组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var view = this.form.get("fieldId"); //获取组件
 * //方法2
 * var view = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.View = MWF.APPView =  new Class(
    /** @lends MWF.xApplication.process.Xform.View# */
{
	Extends: MWF.APP$Module,
    options: {
        /**
         * 视图参数（options）已经准备好，还未加载视图时执行。可以通过this.event得到视图参数，并可修改this.event修改视图的加载。
         * @event MWF.xApplication.process.Xform.View#beforeLoadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 异步加载视图后完成。
         * @event MWF.xApplication.process.Xform.View#loadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 选中视图中的一条记录后执行。
         * @event MWF.xApplication.process.Xform.View#select
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 打开视图中的一条记录后执行。
         * @event MWF.xApplication.process.Xform.View#openDocument
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "beforeLoadView", "loadView", "queryLoad", "postLoad", "select", "openDocument"]
    },

    _loadUserInterface: function(){
        MWF.xDesktop.requireApp("query.Query", "Viewer", null, false);
        this.node.empty();
    },
    _afterLoaded: function(){
        if (this.json.queryView){
            this.loadView();
        }else{
            if (this.json.selectViewType==="cms"){
                this.loadCMSView();
            }else if (this.json.selectViewType==="process"){
                this.loadPrcessView();
            }else{
                this.loadView();
            }
        }
    },
    /**
     * @summary 重新加载视图
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
     * @summary 当视图被设置为延迟加载（未立即载入），通过active方法激活
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
        if (!this.json.queryView || !this.json.queryView.name || !this.json.queryView.appName) return "";
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
            "application": (this.json.queryView) ? this.json.queryView.appName : this.json.application,
            "viewName": (this.json.queryView) ? this.json.queryView.name : this.json.viewName,
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
            this.view = new MWF.xApplication.query.Query.Viewer(this.node, viewJson, {
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

    loadPrcessView: function(){
        var filter = null;
        if (this.json.filterList && this.json.filterList.length){
            filter = [];
            this.json.filterList.each(function(entry){
                entry.value = this.form.Macro.exec(entry.code.code, this);
                //delete entry.code;
                filter.push(entry);
            }.bind(this));
        }
        var viewJson = {
            "application": this.json.processView.application,
            "viewName": this.json.processView.name,
            "isTitle": this.json.isTitle || "yes",
            "select": this.json.select || "none",
            "titleStyles": this.json.titleStyles,
            "itemStyles": this.json.itemStyles,
            "isExpand": this.json.isExpand || "no",
            "showActionbar" : this.json.actionbar === "show",
            "filter": filter
        };
        MWF.xDesktop.requireApp("process.Application", "Viewer", function(){
            this.view = new MWF.xApplication.process.Application.Viewer(this.node, viewJson, {
                "resizeNode": (this.node.getStyle("height").toString().toLowerCase()!=="auto" && this.node.getStyle("height").toInt()>0),
                "onSelect": function(){
                    this.fireEvent("select");
                }.bind(this)
            });
        }.bind(this));
    },
    loadCMSView: function(){
        var filter = null;
        if (this.json.filterList && this.json.filterList.length){
            filter = [];
            this.json.filterList.each(function(entry){
                entry.value = this.form.Macro.exec(entry.code.code, this);
                //delete entry.code;
                filter.push(entry);
            }.bind(this));
        }
        var viewJson = {
            "application": this.json.cmsView.appId,
            "viewName": this.json.cmsView.name,
            "isTitle": this.json.isTitle || "yes",
            "select": this.json.select || "none",
            "titleStyles": this.json.titleStyles,
            "itemStyles": this.json.itemStyles,
            "isExpand": this.json.isExpand || "no",
            "showActionbar" : this.json.actionbar === "show",
            "filter": filter
        };

        MWF.xDesktop.requireApp("process.Application", "Viewer", function(){
            /**
             * @summary view组件，平台使用该组件实现视图的功能
             * @member {MWF.xApplication.process.Application.Viewer}
             * @example
             *  //可以在脚本中获取该组件
             * var view = this.form.get("fieldId").view; //获取组件对象
             */
            this.view = new MWF.xApplication.process.Application.Viewer(this.node, viewJson, {
                "actions": {
                    "lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
                    "getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"}
                },
                "actionRoot": "x_cms_assemble_control",
                "resizeNode": (this.node.getStyle("height").toString().toLowerCase()!=="auto" && this.node.getStyle("height").toInt()>0),
                "onSelect": function(){
                    this.fireEvent("select");
                }.bind(this)
            });
        }.bind(this));
    },
    /**
     * @summary 获取视图被选中行的数据
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
