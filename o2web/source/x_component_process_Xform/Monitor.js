MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("process.Xform", "widget.Monitor", null, false);
MWF.xApplication.process.Xform.Monitor = MWF.APPMonitor =  new Class({
    Extends: MWF.APP$Module,

    _loadUserInterface: function(){
        this.node.empty();

        //MWF.xDesktop.requireApp("process.Xform", "widget.Monitor", function(){
        //    debugger;
            var process = (this.form.businessData.work) ? this.form.businessData.work.process : this.form.businessData.workCompleted.process;
            this.monitor = new MWF.xApplication.process.Xform.widget.Monitor(this.node, this.form.businessData.workLogList, process,{
                "onPostLoad" : function(){
                    this.fireEvent("postLoad");
                }.bind(this)
            });
        //}.bind(this), false);
    }
});