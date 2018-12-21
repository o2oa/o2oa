MWF.xDesktop.requireApp("process.ProcessManager", "DictionaryExplorer", null, false);
MWF.xApplication.process.ProcessManager.FileExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.APPPM.LP.file.create,
        "search": MWF.APPPM.LP.file.search,
        "searchText": MWF.APPPM.LP.file.searchText,
        "noElement": MWF.APPPM.LP.file.noDictionaryNoticeText
    },

    _createElement: function(e){
        // var _self = this;
        // var options = {
        //     "onQueryLoad": function(){
        //         this.actions = _self.app.restActions;
        //         this.application = _self.app.options.application || _self.app.application;
        //         this.explorer = _self;
        //     }
        // };
        // this.app.desktop.openApplication(e, "process.FileDesigner", options);
        new MWF.xApplication.process.ProcessManager.FileDesigner(this);
    },
    _loadItemDataList: function(callback){
        var id = "";
        if (this.app.application) id = this.app.application.id;
        if (this.app.options.application) id = this.app.options.application.id;
        this.actions.listFile(id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.process.ProcessManager.FileExplorer.File(this, item)
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.APPPM.LP.file.create,
            "search": MWF.APPPM.LP.file.search,
            "searchText": MWF.APPPM.LP.file.searchText,
            "noElement": MWF.APPPM.LP.file.noScriptNoticeText
        };
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteFile();
            }else{
                item.deleteFile(function(){
                //    this.reloadItems();
                //    this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.process.ProcessManager.FileExplorer.File = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer.Dictionary,

    _customNodes: function(){
        // if (!this.data.validated){
        //     new Element("div", {"styles": this.explorer.css.itemErrorNode}).inject(this.node);
        //     this.node.setStyle("background-color", "#f9e8e8");
        // }
    },
	_open: function(e){
		var _self = this;
        MWF.Actions.get("x_processplatform_assemble_designer").getFile(this.data.id, function(json){
            th