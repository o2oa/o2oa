MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);
MWF.xApplication.process.Xform.Stat = MWF.APPStat =  new Class({
	Extends: MWF.APP$Module,
    options: {
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
        var viewJson = {
            "application": this.json.queryStat.appName,
            "statName": this.json.queryStat.name,
            "isChart": (this.json.isChart!="no"),
            "isLegend": (this.json.isLegend!="no"),
            "isTable": (this.json.isTable!="no")
        };

        MWF.xDesktop.requireApp("query.Query", "Statistician", function(){
            this.stat = new MWF.xApplication.query.Query.Statistician(this.form.app, this.node, viewJson, {
                "resizeNode": (this.node.getStyle("height").toString().toLowerCase()!=="auto" && this.node.getStyle("height").toInt()>0),
                "onLoaded": function(){
                    this.fireEvent("loadStat");
                }.bind(this)
            });
        }.bind(this));
    },
    getData: function(){
        if (!this.stat) return null;
        if (!this.stat.stat) return null;
        return this.stat.stat.data;
    }
});