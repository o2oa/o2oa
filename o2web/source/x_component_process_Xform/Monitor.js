MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.Monitor", null, false);
/** @class Monitor 流程图组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var attachment = this.form.get("name"); //获取组件
 * //方法2
 * var attachment = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Monitor = MWF.APPMonitor =  new Class(
    /** @lends MWF.xApplication.process.Xform.Monitor# */
    {
    Extends: MWF.APP$Module,

    _loadUserInterface: function(){
        this.node.empty();

        MWF.xDesktop.requireApp("process.Xform", "widget.Monitor", function(){
        //    debugger;
            var process = (this.form.businessData.work) ? this.form.businessData.work.process : this.form.businessData.workCompleted.process;
            /**
             * @summary 流程图对象，是一个 MWF.xApplication.process.Xform.widget.Monitor 类实例
             */
            this.monitor = new MWF.xApplication.process.Xform.widget.Monitor(this.node, this.form.businessData.workLogList, process,{
                "onPostLoad" : function(){
                    this.fireEvent("postLoad");
                }.bind(this)
            });
        }.bind(this), false);
    }
});
