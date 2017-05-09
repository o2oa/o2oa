MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);
MWF.xApplication.process.Xform.View = MWF.APPView =  new Class({
	Extends: MWF.APP$Module,

    _loadUserInterface: function(){
        this.node.empty();

        //MWF.xDesktop.requireApp("process.Xform", "widget.View", function(){
            this.json.application = this.form.json.applicationName;
            this.view = new MWF.xApplication.process.Xform.widget.View(this.node, this.json, {
                "onSelect": function(){
                    this.fireEvent("select");
                }.bind(this)
            });
        //}.bind(this), false);
    },
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