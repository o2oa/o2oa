MWF.xDesktop.requireApp("process.ProcessManager", "DictionaryExplorer", null, false);
MWF.xApplication.process.Application.DictionaryExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer,
    _getItemObject: function(item){
        return new MWF.xApplication.process.Application.DictionaryExplorer.Dictionary(this, item);
    }
});

MWF.xApplication.process.Application.DictionaryExplorer.Dictionary = new Class({
    Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer.Dictionary,
    _open: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
                this.actions.application = this.application;
                this.options.noModifyName = _self.explorer.options.noModifyName;
                this.options.readMode = _self.explorer.options.readMode
            }
        };
        this.explorer.app.desktop.openApplication(e, "process.DictionaryDesigner", options);
    },
    _getLnkPar: function(){
        return {
            "icon": this.explorer.path+this.explorer.options.style+"/dictionaryIcon/lnk.png",
            "title": this.data.name,
            "par": "process.DictionaryDesigner#{\"id\": \""+this.data.id+"\", \"applicationId\": \""+this.explorer.app.options.application.id+"\", \"options\": {\"action\": \"Application\",\"noCreate\": "+this.explorer.options.noCreate+", \"noDelete\": "+this.explorer.options.noDelete+", \"noModifyName\": "+this.explorer.options.noModifyName+", \"readMode\": "+this.explorer.options.readMode+"}}"
        };
    }
});