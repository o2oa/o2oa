MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);
MWF.xApplication.process.Xform.Statement = MWF.APPStatement =  new Class({
	Extends: MWF.APP$Module,
    options: {
        "moduleEvents": ["load", "loadView", "queryLoad", "postLoad", "select", "openDocument"]
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
    reload: function(){
        if (this.view){
            if (this.view.loadViewRes && this.view.loadViewRes.res) if (this.view.loadViewRes.res.isRunning()) this.view.loadViewRes.res.cancel();
            if (this.view.getViewRes && this.view.getViewRes.res) if (this.view.getViewRes.res.isRunning()) this.view.getViewRes.res.cancel();
        }
        this.node.empty();
        this.loadView();
    },
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

        //MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
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
