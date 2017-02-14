MWF.xDesktop.requireApp("cms.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("cms.Xform", "widget.QueryView", null, false);
MWF.xApplication.cms.Xform.QueryView = MWF.CMSQueryView =  new Class({
	Extends: MWF.CMS$Module,

    _loadUserInterface: function(){
        this.node.empty();

        //MWF.xDesktop.requireApp("process.Xform", "widget.QueryView", function(){
            this.json.application = this.form.json.applicationName;
            this.queryView = new MWF.xApplication.cms.Xform.widget.QueryView(this.node, this.json);
        //}.bind(this), false);
    },
    getData: function(){
        if (this.queryView.selectedItems.length){
            var arr = [];
            this.queryView.selectedItems.each(function(item){
                arr.push(item.data);
            });
            return arr;
        }else{
            return [];
        }
    }
});