MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);

/** @class Stat 统计组件。
 * @o2cn 统计组件
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
        /**
         * 图表加载完成触发.
         * @event MWF.xApplication.process.Xform.Stat#afterLoadStat
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "loadStat", "loadChart"]
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
    loadStat: function( json ){
        var viewJson = Object.merge(this.getDefaultJson(), json || {});
	    if ( viewJson.application && viewJson.statName ){
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
                    }.bind(this),
                    "onLoadChart": function(){
                        this.fireEvent("loadChart");
                    }.bind(this)
                });
            }.bind(this));
        }
    },
    getDefaultJson: function(){
	    if( this.json.queryStat ){
	        return {
                "application": this.json.queryStat.appName,
                "statName": this.json.queryStat.name,
                "isChart": (this.json.isChart!=="no"),
                "isLegend": (this.json.isLegend!=="no"),
                "isTable": (this.json.isTable!=="no"),
                "isRowToColumn": (this.json.isRowToColumn!=="no"),
                "tableNodeStyles": this.json.tableNodeStyles,
                "chartNodeStyles": this.json.chartNodeStyles
            };
        }else{
	        return {};
        }
    },

    /**
     * @summary 重新加载统计。
     *  @param json {Object} 加载选项
     *  <pre><code class='language-js'>[{
     *     "application": "",   //数据中心应用名称
     *     "statName": "",         //统计名称
     *     "isChart": true, //是否显示图表
     *     "isLegend": true, //是否显示图例
     *     "isTable": true, //是否显示表格
     *     "isRowToColumn": true, //是否显示行列转换
     * }]</code></pre>
     *  @example
     *  this.form.get("fieldId").reloadStat({
     *      "application": "数据中心应用名称",
     *      "statName": "统计名称"
     *  });
     *  @return {Boolean} 是否通过校验
     */
    reloadStat: function(json){
        var viewJson = Object.merge(this.getDefaultJson(), json || {});
	    if( viewJson.application && viewJson.statName ){
	        if( this.stat ){
                this.stat.reload(viewJson);
            }else{
                this.loadStat(viewJson);
            }
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