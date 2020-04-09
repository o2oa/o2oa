MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("query.Query", "Viewer", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);
MWF.xApplication.process.Xform.View = MWF.APPView =  new Class({
	Extends: MWF.APP$Module,
    options: {
        "moduleEvents": ["load", "loadView", "queryLoad", "postLoad", "select", "openDocument"]
    },

    _loadUserInterface: function(){
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
    reload: function(){
        if (this.view){
            if (this.view.loadViewRes) if (this.view.loadViewRes.isRunning()) this.view.loadViewRes.cancel();
            if (this.view.getViewRes) if (this.view.getViewRes.isRunning()) this.view.getViewRes.cancel();
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
            "filter": filter
        };

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
            });
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
            "filter": filter
        };

        MWF.xDesktop.requireApp("process.Application", "Viewer", function(){
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