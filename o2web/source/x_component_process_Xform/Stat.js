MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);

/** @class Stat 统计组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var stat = this.form.get("fieldId"); //获取组件
 * //方法2
 * var stat = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Stat = MWF.APPStat =  new Class(
    /** @lends MWF.xApplication.process.Xform.Stat# */
{
	Extends: MWF.APP$Module,
    options: {
        /**
         * 组件异步加载完成触发.
         * @event MWF.xApplication.process.Xform.Stat#loadStat
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "loadStat"]
    },

    _loadUserInterface: function(){
        this.node.empty();
    },
    _afterLoaded: function(){
        this.node.setStyle("min-height", "100px");
        this.loadStat();
    },
    active: function(){
        if (this.stat) this.stat.loadStatData();
    },
    reload: function(){
	    this.active();
    },
    loadStat: function(){
	    if (this.json.queryStat){
            var viewJson = {
                "application": this.json.queryStat.appName,
                "statName": this.json.queryStat.name,
                "isChart": (this.json.isChart!="no"),
                "isLegend": (this.json.isLegend!="no"),
                "isTable": (this.json.isTable!="no")
            };

            MWF.xDesktop.requireApp("query.Query", "Statistician", function(){
                /**
                 * @summary Statistician组件，平台使用该组件执行统计的逻辑
                 * @member {MWF.xApplication.query.Query.Statistician}
                 * @example
                 *  //可以在脚本中获取该组件
                 * var field = this.form.get("fieldId").stat; //获取组件对象
                 */
                this.stat = new MWF.xApplication.query.Query.Statistician(this.form.app, this.node, viewJson, {
                    "resizeNode": (this.node.getStyle("height").toString().toLowerCase()!=="auto" && this.node.getStyle("height").toInt()>0),
                    "onLoaded": function(){
                        this.fireEvent("loadStat");
                    }.bind(this)
                });
            }.bind(this));
        }
    },
    /**
     * @summary 获取统计数据。
     *  @return {Ojbect} 统计数据.
     *  @example
     *  var data = this.form.get("fieldId").getData();
     *  @return {Boolean} 是否通过校验
     */
    getData: function(){
        if (!this.stat) return null;
        if (!this.stat.stat) return null;
        return this.stat.stat.data;
    }
});